package com.zidoo.custom.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ZidooBlueControlTool {
    private BlueControlConnectListener mBlueControlConnectListener = null;
    private BroadcastReceiver mBlueToothBroadcastReceiver = null;
    private Context mContext = null;

    public interface BlueControlConnectListener {
        void isConnect(boolean z);
    }

    public ZidooBlueControlTool(Context mContext, BlueControlConnectListener mBlueControlConnectListener) {
        this.mContext = mContext;
        this.mBlueControlConnectListener = mBlueControlConnectListener;
        init();
    }

    private void init() {
        this.mBlueToothBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (ZidooBlueControlTool.this.mBlueControlConnectListener != null) {
                    ZidooBlueControlTool.this.mBlueControlConnectListener.isConnect(intent.getBooleanExtra("isConnect", false));
                }
            }
        };
        IntentFilter infilter = new IntentFilter();
        infilter.addAction("com.zidoo.ble.connect.status");
        this.mContext.registerReceiver(this.mBlueToothBroadcastReceiver, infilter);
        this.mContext.sendBroadcast(new Intent("com.zidoo.bluetooth.querry.status"));
    }

    public void release() {
        try {
            if (this.mBlueToothBroadcastReceiver != null) {
                this.mContext.unregisterReceiver(this.mBlueToothBroadcastReceiver);
                this.mBlueToothBroadcastReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
