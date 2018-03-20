package com.zidoo.fileexplorer.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.ListViewHolder;
import com.zidoo.fileexplorer.tool.FileOperater;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.text.SimpleDateFormat;
import zidoo.file.FileType;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;
import zidoo.tarot.widget.WrapSingleLineTextView;

@SuppressLint({"SimpleDateFormat"})
public class FileListAdapter extends BaseFileAdapter {
    final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
    final String DIRECTORY;

    public FileListAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
        this.DIRECTORY = glContext.getString(R.string.directory);
    }

    protected boolean isList() {
        return true;
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
            name.setHeight(40.0f);
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
        try {
            if (isEmpty()) {
                holder.cb.setVisibility(false);
                holder.typeOrSize.setVisibility(false);
                holder.date.setVisibility(false);
                holder.icon.setImageResource(R.drawable.icon_back);
                holder.name.setText(this.glContext.getString(R.string.back));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else if (position == contentCount()) {
                holder.cb.setVisibility(false);
                holder.typeOrSize.setVisibility(false);
                holder.date.setVisibility(false);
                holder.icon.setImageResource(R.drawable.img_top);
                holder.name.setText(this.glContext.getString(R.string.back_top));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else {
                holder.name.setTextColor(-1);
                File file;
                int type;
                if (isMultiChoose()) {
                    file = this.listInfo.getChild(position);
                    holder.cb.setVisibility(true);
                    holder.typeOrSize.setVisibility(true);
                    holder.date.setVisibility(true);
                    holder.cb.setImageResource(this.listInfo.isCheck(position) ? R.drawable.img_cb_ck : R.drawable.img_cb_n);
                    type = FileType.getType(file);
                    holder.icon.setImageResource(FileOperater.getFileIconResource(type));
                    holder.name.setText(file.getName());
                    holder.typeOrSize.setText(type == 0 ? this.glContext.getString(R.string.directory) : Utils.formatFileSize(file.length()));
                    holder.date.setText(this.DATEFORMAT.format(Long.valueOf(file.lastModified())));
                } else {
                    holder.cb.setVisibility(false);
                    file = this.listInfo.getChild(position);
                    holder.typeOrSize.setVisibility(true);
                    holder.date.setVisibility(true);
                    type = FileType.getType(file);
                    holder.icon.setImageResource(FileOperater.getFileIconResource(type));
                    holder.name.setText(file.getName());
                    holder.typeOrSize.setText(type == 0 ? this.DIRECTORY : Utils.formatFileSize(file.length()));
                    holder.date.setText(this.DATEFORMAT.format(Long.valueOf(file.lastModified())));
                }
            }
            holder.name.setX((holder.name.getWidth() / 2.0f) - 670.0f);
        } catch (IndexOutOfBoundsException e) {
            Log.e("List>mAdapterFile getView Index Out Of Bounds", e.toString());
        }
        return convertView;
    }
}
