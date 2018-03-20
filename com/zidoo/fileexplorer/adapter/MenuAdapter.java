package com.zidoo.fileexplorer.adapter;

import com.zidoo.fileexplorer.menu.FileMenu;
import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;
import zidoo.tarot.widget.ViewGroup;

public class MenuAdapter extends Adapter {
    GLContext glContext;
    FileMenu[] menuInfos = new FileMenu[0];

    private class ViewHolder {
        TImageView icon;
        TTextView name;

        private ViewHolder() {
        }
    }

    public MenuAdapter(GLContext glContext) {
        this.glContext = glContext;
    }

    public void setMenuInfos(FileMenu[] menuInfos) {
        this.menuInfos = menuInfos;
        notifyDataSetChanged();
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        ViewHolder holder;
        int i = -1;
        if (convertView == null) {
            holder = new ViewHolder();
            GameObject layout = new ViewGroup(this.glContext);
            layout.setWidth(240.0f);
            layout.setHeight(80.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(60.0f);
            icon.setHeight(60.0f);
            icon.setX(-110.0f);
            holder.icon = icon;
            TTextView name = new TTextView(this.glContext);
            name.setWidth(240.0f);
            name.setHeight(80.0f);
            name.setX(70.0f);
            name.setSingleLine(true);
            name.setMarquee(true);
            name.setTextSize(38.0f);
            name.setTextColor(-1);
            name.setTextGravity(16);
            holder.name = name;
            layout.addGameObject(icon);
            layout.addGameObject(name);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileMenu info = this.menuInfos[position];
        holder.icon.setImageResource(info.getIcon());
        TTextView tTextView = holder.name;
        if (!info.isAble()) {
            i = -3355444;
        }
        tTextView.setTextColor(i);
        holder.name.setText(this.glContext.getString(info.getName()));
        return convertView;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public FileMenu getItem(int position) {
        return this.menuInfos[position];
    }

    public int getCount() {
        return this.menuInfos.length;
    }
}
