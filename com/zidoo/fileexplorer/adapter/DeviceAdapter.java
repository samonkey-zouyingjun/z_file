package com.zidoo.fileexplorer.adapter;

import com.zidoo.fileexplorer.bean.DefaultViewHolder;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.tool.FileOperater;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.device.DeviceType;
import zidoo.tarot.GLContext;
import zidoo.tarot.gameobject.dataview.Adapter;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;

public class DeviceAdapter extends Adapter {
    ArrayList<DeviceInfo> deviceInfos = new ArrayList();
    GLContext glContext;

    public DeviceAdapter(GLContext glContext) {
        this.glContext = glContext;
    }

    public void setDeviceInfos(ArrayList<DeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public ArrayList<DeviceInfo> getDevices() {
        return this.deviceInfos;
    }

    public GameObject getView(int position, GameObject convertView, Layout parent) {
        DefaultViewHolder holder;
        if (convertView == null) {
            holder = new DefaultViewHolder();
            GameObject layout = new Layout(this.glContext);
            layout.setWidth(260.0f);
            layout.setHeight(80.0f);
            TImageView icon = new TImageView(this.glContext);
            icon.setWidth(60.0f);
            icon.setHeight(60.0f);
            icon.setX(-123.0f);
            holder.icon = icon;
            TTextView name = new TTextView(this.glContext);
            name.setWidth(215.0f);
            name.setHeight(48.0f);
            name.setTextColor(-1);
            name.setTextSize(40.0f);
            name.setSingleLine(true);
            name.setMarquee(true);
            name.setTextGravity(16);
            name.setX(45.0f);
            holder.name = name;
            layout.addGameObject(icon);
            layout.addGameObject(name);
            convertView = layout;
            convertView.setTag(holder);
        } else {
            holder = (DefaultViewHolder) convertView.getTag();
        }
        DeviceInfo deviceInfo = (DeviceInfo) this.deviceInfos.get(position);
        holder.icon.setImageResource(FileOperater.getDeviceIconResource(deviceInfo.getType()));
        holder.name.setText(deviceInfo.getName());
        return convertView;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public DeviceInfo getItem(int position) {
        return (DeviceInfo) this.deviceInfos.get(position);
    }

    public int getCount() {
        return this.deviceInfos.size();
    }

    public DeviceInfo getSmbDevice() {
        Iterator it = this.deviceInfos.iterator();
        while (it.hasNext()) {
            DeviceInfo deviceInfo = (DeviceInfo) it.next();
            if (deviceInfo.getType() == DeviceType.SMB) {
                return deviceInfo;
            }
        }
        return null;
    }

    public DeviceInfo getNfsDevice() {
        Iterator it = this.deviceInfos.iterator();
        while (it.hasNext()) {
            DeviceInfo deviceInfo = (DeviceInfo) it.next();
            if (deviceInfo.getType() == DeviceType.NFS) {
                return deviceInfo;
            }
        }
        return null;
    }

    public void add(DeviceInfo deviceInfo) {
        this.deviceInfos.add(deviceInfo);
    }
}
