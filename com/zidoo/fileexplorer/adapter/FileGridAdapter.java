package com.zidoo.fileexplorer.adapter;

import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.GridViewHolder;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.tool.FileOperater;
import java.io.File;
import zidoo.file.FileType;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.WrapSingleLineTextView;

public class FileGridAdapter extends BaseFileAdapter {
    public FileGridAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
    }

    protected boolean isList() {
        return false;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public File getItem(int position) {
        return this.listInfo.getChild(position);
    }

    public int contentCount() {
        return this.listInfo.childCount();
    }

    public void remove(boolean[] remove, int count) {
        File[] children = this.listInfo.getChildren();
        File[] newList = new File[(children.length - count)];
        int j = 0;
        for (int i = 0; i < children.length; i++) {
            if (!remove[i]) {
                int j2 = j + 1;
                newList[j] = children[i];
                j = j2;
            }
        }
        this.listInfo.setChildren(newList);
    }

    public void remove(int position) {
        File[] children = this.listInfo.getChildren();
        File[] newLists = new File[(children.length - 1)];
        System.arraycopy(children, 0, newLists, 0, position);
        System.arraycopy(children, position + 1, newLists, position, newLists.length - position);
        this.listInfo.setChildren(newLists);
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
            holder.id = position;
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
                File file;
                if (isMultiChoose()) {
                    file = this.listInfo.getChild(position);
                    holder.cb.setVisibility(true);
                    holder.cb.setImageResource(this.listInfo.isCheck(position) ? R.drawable.img_cb_ck : R.drawable.img_cb_n);
                    holder.icon.setImageResource(FileOperater.getFileIconResource(FileType.getType(file)));
                    holder.name.setText(file.getName());
                } else {
                    holder.cb.setVisibility(false);
                    file = this.listInfo.getChild(position);
                    holder.icon.setImageResource(FileOperater.getFileIconResource(FileType.getType(file)));
                    holder.name.setText(file.getName());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Grid>mAdapterFile getView Index Out Of Bounds", e.toString());
        }
        return convertView;
    }
}
