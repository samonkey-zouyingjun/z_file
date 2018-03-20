package com.zidoo.fileexplorer.adapter;

import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.GridViewHolder;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.tool.FileOperater;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.WrapSingleLineTextView;

public class FavoriteGridAdapter extends BaseFileAdapter {
    public FavoriteGridAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
    }

    protected boolean isList() {
        return false;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public Favorite getItem(int position) {
        return this.listInfo.getFavorite(position);
    }

    public int contentCount() {
        return this.listInfo.favoriteCount();
    }

    public void remove(int position) {
        Favorite[] favorites = this.listInfo.getFavorites();
        Favorite[] newLists = new Favorite[(favorites.length - 1)];
        System.arraycopy(favorites, 0, newLists, 0, position);
        System.arraycopy(favorites, position + 1, newLists, position, newLists.length - position);
        this.listInfo.setFavorite(newLists);
    }

    public void remove(boolean[] remove, int count) {
        Favorite[] favorites = this.listInfo.getFavorites();
        Favorite[] newList = new Favorite[(favorites.length - count)];
        int j = 0;
        for (int i = 0; i < favorites.length; i++) {
            if (!remove[i]) {
                int j2 = j + 1;
                newList[j] = favorites[i];
                j = j2;
            }
        }
        this.listInfo.setFavorite(newList);
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        GridViewHolder holder;
        if (convertView == null) {
            holder = new GridViewHolder();
            GameObject layout = new Layout(this.glContext);
            layout.setWidth(232.0f);
            layout.setHeight(230.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(117.0f);
            icon.setHeight(123.0f);
            icon.setY(22.0f);
            holder.icon = icon;
            TImageView cb = new TImageView(this.glContext);
            cb.setWidth(50.0f);
            cb.setHeight(50.0f);
            cb.setPositionPixel(55.0f, 60.0f);
            holder.cb = cb;
            WrapSingleLineTextView name = new WrapSingleLineTextView(this.glContext);
            name.setMaxWidth(220.0f);
            name.setHeight(35.0f);
            name.setTextColor(-1);
            name.setTextSize(27.0f);
            name.setY(-60.0f);
            holder.name = name;
            layout.addGameObject(icon);
            layout.addGameObject(cb);
            layout.addGameObject(name);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (GridViewHolder) convertView.getTag();
        }
        try {
            if (isEmpty()) {
                holder.cb.setVisibility(false);
                holder.icon.setImageResource(R.drawable.icon_back);
                holder.name.setText(this.glContext.getString(R.string.back));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else if (position == contentCount()) {
                holder.cb.setVisibility(false);
                holder.icon.setImageResource(R.drawable.img_top);
                holder.name.setText(this.glContext.getString(R.string.back_top));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else {
                holder.name.setTextColor(-1);
                Favorite favorite;
                if (isMultiChoose()) {
                    favorite = this.listInfo.getFavorite(position);
                    holder.cb.setVisibility(true);
                    holder.cb.setImageResource(this.listInfo.isCheck(position) ? R.drawable.img_cb_ck : R.drawable.img_cb_n);
                    holder.name.setText(favorite.getName());
                    if (favorite.getFileType() == -1) {
                        holder.icon.setImageResource(R.drawable.icon_net);
                    } else {
                        holder.icon.setImageResource(FileOperater.getFileIconResource(favorite.getFileType()));
                    }
                } else {
                    holder.cb.setVisibility(false);
                    favorite = this.listInfo.getFavorite(position);
                    holder.name.setText(favorite.getName());
                    if (favorite.getFileType() == -1) {
                        holder.icon.setImageResource(R.drawable.icon_net);
                    } else {
                        holder.icon.setImageResource(FileOperater.getFileIconResource(favorite.getFileType()));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Grid>mAdapterFile getView Index Out Of Bounds", e.toString());
        }
        return convertView;
    }
}
