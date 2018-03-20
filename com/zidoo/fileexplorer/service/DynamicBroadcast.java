package com.zidoo.fileexplorer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.Utils;
import java.util.ArrayList;
import zidoo.device.DeviceType;
import zidoo.device.ZDevice;
import zidoo.model.BoxModel;

public class DynamicBroadcast extends BroadcastReceiver {
    FileMolder fileMolder;

    public DynamicBroadcast(FileMolder fileMolder) {
        this.fileMolder = fileMolder;
    }

    public void onReceive(Context context, Intent intent) {
        boolean isX6 = false;
        String action = intent.getAction();
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (!Utils.NetIsConnected(context) && ListType.isNet(this.fileMolder.getListType())) {
                this.fileMolder.netError();
            }
        } else if (action.equals("zidoo_usb_uninstall")) {
            if (BoxModel.getBoxModel(context) == 3) {
                isX6 = true;
            }
            final String path = intent.getStringExtra("uninstallusb");
            new Thread(new Runnable() {
                public void run() {
                    ZDevice device;
                    if (isX6) {
                        device = DynamicBroadcast.this.fileMolder.getDevice();
                    } else {
                        device = new DeviceInfo(path, DeviceType.SD);
                    }
                    ArrayList<ZDevice> removes = new ArrayList();
                    removes.add(device);
                    DynamicBroadcast.this.fileMolder.removeUsb(removes);
                }
            }).start();
        } else if (action.equals(AppConstant.ACTION_INNER_USB_BROADCAST)) {
            ArrayList<ZDevice> devices = intent.getParcelableArrayListExtra(AppConstant.EXTRA_USB_DEVICE);
            if (intent.getBooleanExtra(AppConstant.EXTRA_IS_REMOVE_OR_ADD_USB, false)) {
                this.fileMolder.removeUsb(devices);
            } else {
                this.fileMolder.AddUsb(devices);
            }
        }
    }
}
