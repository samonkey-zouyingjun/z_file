package com.zidoo.custom.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.zidoo.custom.init.ZidooJarPermissions;

public class ZidooBlueToothTool {
    private BroadcastReceiver mBlueToothBroadcastReceiver = null;
    private BlueToothConnectListener mBlueToothConnectListener = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Context mContext = null;

    public interface BlueToothConnectListener {
        void isBlueTooth(boolean z);

        void isConnect(boolean z);
    }

    public ZidooBlueToothTool(Context mContext, BlueToothConnectListener mBlueToothConnectListener) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = mContext;
        this.mBlueToothConnectListener = mBlueToothConnectListener;
        initData();
    }

    private void initData() {
        try {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mBluetoothAdapter != null) {
                if (this.mBlueToothConnectListener != null) {
                    this.mBlueToothConnectListener.isBlueTooth(true);
                    this.mBlueToothConnectListener.isConnect(isConnectEnable());
                }
                this.mBlueToothBroadcastReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if (ZidooBlueToothTool.this.mBlueToothConnectListener != null) {
                            ZidooBlueToothTool.this.mBlueToothConnectListener.isConnect(ZidooBlueToothTool.this.isConnectEnable());
                        }
                    }
                };
                initBlueTooth();
            } else if (this.mBlueToothConnectListener != null) {
                this.mBlueToothConnectListener.isBlueTooth(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBlueTooth() {
        IntentFilter infilter = new IntentFilter();
        infilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mBlueToothBroadcastReceiver, infilter);
    }

    public boolean isConnectEnable() {
        try {
            return this.mBluetoothAdapter.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEnable() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                return mBluetoothAdapter.isEnabled();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHaveBluetooth() {
        try {
            if (BluetoothAdapter.getDefaultAdapter() != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
