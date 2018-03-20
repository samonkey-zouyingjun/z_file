package com.zidoo.fileexplorer.adapter;

import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.SmbViewHolder;
import com.zidoo.fileexplorer.config.AppConstant;
import zidoo.samba.exs.SambaDevice;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.WrapSingleLineTextView;

public class SmbGridAdapter extends BaseFileAdapter {
    public SmbGridAdapter(GLContext glContext, ListInfo listInfo) {
        super(glContext, listInfo);
    }

    protected boolean isList() {
        return false;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public SambaDevice getItem(int position) {
        return this.listInfo.getSmbDevice(position);
    }

    public int contentCount() {
        return this.listInfo.smbSize(0);
    }

    public void remove(boolean[] remove, int count) {
    }

    public void remove(int position) {
        this.listInfo.removeSmb(position);
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        SmbViewHolder holder;
        if (convertView == null) {
            holder = new SmbViewHolder();
            GameObject layout = new Layout(this.glContext);
            layout.setWidth(232.0f);
            layout.setHeight(230.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(117.0f);
            icon.setHeight(123.0f);
            icon.setY(22.0f);
            holder.icon = icon;
            WrapSingleLineTextView name = new WrapSingleLineTextView(this.glContext);
            name.setMaxWidth(220.0f);
            name.setHeight(35.0f);
            name.setTextColor(-1);
            name.setTextSize(27.0f);
            name.setY(-67.0f);
            holder.name = name;
            layout.addGameObject(icon);
            layout.addGameObject(name);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (SmbViewHolder) convertView.getTag();
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
                SambaDevice device = this.listInfo.getSmbDevice(position);
                holder.icon.setImageResource(this.listInfo.isSavedSmb(position) ? R.drawable.icon_net : R.drawable.icon_net_unavailable);
                holder.name.setText(AppConstant.sPrefereancesSmbDisplay == 0 ? device.getName() : device.getIp());
                holder.name.setTextColor(-1);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e("Grid>mAdapterSmb getView Index Out Of Bounds", e.toString());
        }
        return convertView;
    }
}
