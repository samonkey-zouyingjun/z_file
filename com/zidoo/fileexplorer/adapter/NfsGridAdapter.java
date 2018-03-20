package com.zidoo.fileexplorer.adapter;

import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.DefaultViewHolder;
import com.zidoo.fileexplorer.bean.ListInfo;
import zidoo.nfs.NfsDevice;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;

public class NfsGridAdapter extends BaseFileAdapter {
    public NfsGridAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
    }

    protected boolean isList() {
        return false;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public NfsDevice getItem(int position) {
        return this.listInfo.getNfs(position);
    }

    public int contentCount() {
        return this.listInfo.nfsSize();
    }

    public void remove(boolean[] remove, int count) {
    }

    public void remove(int position) {
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        DefaultViewHolder holder;
        if (convertView == null) {
            holder = new DefaultViewHolder();
            GameObject layout = new Layout(this.glContext);
            layout.setWidth(232.0f);
            layout.setHeight(230.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(117.0f);
            icon.setHeight(123.0f);
            icon.setY(22.0f);
            holder.icon = icon;
            TTextView name = new TTextView(this.glContext);
            name.setWidth(220.0f);
            name.setHeight(35.0f);
            name.setTextColor(-1);
            name.setTextSize(27.0f);
            name.setTextGravity(17);
            name.setY(-67.0f);
            holder.name = name;
            layout.addGameObject(icon);
            layout.addGameObject(name);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (DefaultViewHolder) convertView.getTag();
        }
        try {
            if (isEmpty()) {
                holder.icon.setImageResource(R.drawable.icon_back);
                holder.name.setText(this.glContext.getString(R.string.back));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else if (position == contentCount()) {
                holder.icon.setImageResource(R.drawable.img_top);
                holder.name.setText(this.glContext.getString(R.string.back_top));
                holder.name.setTextColor(this.glContext.getResources().getColor(R.color.special_item));
            } else {
                holder.icon.setImageResource(R.drawable.icon_net);
                holder.name.setText(this.listInfo.getNfs(position).ip);
                holder.name.setTextColor(-1);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Grid>mAdapterNfs getView Index Out Of Bounds", e.toString());
        }
        return convertView;
    }
}
