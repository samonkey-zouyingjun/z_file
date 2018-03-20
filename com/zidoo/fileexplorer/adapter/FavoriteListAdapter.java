package com.zidoo.fileexplorer.adapter;

import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.ListViewHolder;
import com.zidoo.fileexplorer.tool.FileOperater;
import com.zidoo.fileexplorer.tool.Utils;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;
import zidoo.tarot.widget.WrapSingleLineTextView;

public class FavoriteListAdapter extends BaseFileAdapter {
    public FavoriteListAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
    }

    protected boolean isList() {
        return true;
    }

    public int contentCount() {
        return this.listInfo.favoriteCount();
    }

    public Favorite getItem(int position) {
        return this.listInfo.getFavorite(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        ListViewHolder holder;
        if (convertView == null) {
            holder = new ListViewHolder();
            GameObject layout = new Layout(this.glContext);
            layout.setWidth(1600.0f);
            layout.setHeight(80.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(58.0f);
            icon.setHeight(61.0f);
            icon.setX(-725.0f);
            holder.icon = icon;
            TImageView cb = new TImageView(this.glContext);
            cb.setWidth(45.0f);
            cb.setHeight(45.0f);
            cb.setX(740.0f);
            holder.cb = cb;
            WrapSingleLineTextView name = new WrapSingleLineTextView(this.glContext);
            name.setHeight(36.0f);
            name.setTextColor(-1);
            name.setTextSize(36.0f);
            name.setMaxWidth(980.0f);
            name.setX(6.0f);
            name.setY(-2.0f);
            holder.name = name;
            TTextView typeOrSize = new TTextView(this.glContext);
            typeOrSize.setWidth(130.0f);
            typeOrSize.setHeight(25.0f);
            typeOrSize.setX(450.0f);
            typeOrSize.setTextColor(-1);
            typeOrSize.setTextSize(24.0f);
            typeOrSize.setSingleLine(true);
            holder.typeOrSize = typeOrSize;
            TTextView date = new TTextView(this.glContext);
            date.setWidth(170.0f);
            date.setHeight(25.0f);
            date.setX(630.0f);
            date.setTextColor(-1);
            date.setTextSize(24.0f);
            date.setSingleLine(true);
            holder.date = date;
            layout.addGameObject(icon);
            layout.addGameObject(cb);
            layout.addGameObject(name);
            layout.addGameObject(typeOrSize);
            layout.addGameObject(date);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (ListViewHolder) convertView.getTag();
        }
        if (isEmpty()) {
            holder.typeOrSize.setVisibility(false);
            holder.date.setVisibility(false);
            holder.icon.setImageResource(R.drawable.icon_back);
            holder.name.setText(this.glContext.getString(R.string.back));
            holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            holder.cb.setVisibility(false);
        } else if (position == contentCount()) {
            holder.typeOrSize.setVisibility(false);
            holder.date.setVisibility(false);
            holder.icon.setImageResource(R.drawable.img_top);
            holder.name.setText(this.glContext.getString(R.string.back_top));
            holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            holder.cb.setVisibility(false);
        } else {
            holder.name.setTextColor(-1);
            Favorite favorite;
            int type;
            if (isMultiChoose()) {
                holder.cb.setVisibility(true);
                holder.typeOrSize.setVisibility(true);
                holder.date.setVisibility(true);
                holder.cb.setImageResource(this.listInfo.isCheck(position) ? R.drawable.img_cb_ck : R.drawable.img_cb_n);
                favorite = this.listInfo.getFavorite(position);
                holder.name.setText(favorite.getName());
                holder.date.setText(Utils.getFastIdentifierType(this.glContext, favorite.getTag()));
                type = favorite.getFileType();
                if (type == -1) {
                    holder.icon.setImageResource(R.drawable.icon_net);
                    holder.typeOrSize.setText(this.glContext.getString(R.string.type_net));
                } else {
                    holder.icon.setImageResource(FileOperater.getFileIconResource(type));
                    holder.typeOrSize.setText(type == 0 ? this.glContext.getString(R.string.directory) : Utils.formatFileSize(favorite.getFileLength()));
                }
            } else {
                holder.cb.setVisibility(false);
                favorite = this.listInfo.getFavorite(position);
                holder.name.setText(favorite.getName());
                holder.typeOrSize.setVisibility(true);
                holder.date.setVisibility(true);
                type = favorite.getFileType();
                if (type == -1) {
                    holder.icon.setImageResource(R.drawable.icon_net);
                    holder.typeOrSize.setText(this.glContext.getString(R.string.type_net));
                } else {
                    holder.icon.setImageResource(FileOperater.getFileIconResource(type));
                    holder.typeOrSize.setText(type == 0 ? this.glContext.getString(R.string.directory) : Utils.formatFileSize(favorite.getFileLength()));
                }
                holder.date.setText(Utils.getFastIdentifierType(this.glContext, favorite.getTag()));
            }
        }
        holder.name.setX((holder.name.getWidth() / 2.0f) - 670.0f);
        return convertView;
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
}
