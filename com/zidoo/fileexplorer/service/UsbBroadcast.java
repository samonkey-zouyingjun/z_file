package com.zidoo.fileexplorer.service;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.main.HomeActivity;
import com.zidoo.fileexplorer.tool.MyLog;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.device.ZDevice;
import zidoo.device.ZidooUsbBroadcastReceiver;
import zidoo.model.BoxModel;

public class UsbBroadcast extends ZidooUsbBroadcastReceiver {
    private static final String SATA_PATH = "/dev/bus/usb/006";
    private static UsbDialog sDialog = null;
    private static boolean sIsSata = false;

    private class UsbDialog extends Dialog implements OnClickListener, OnCheckedChangeListener {
        ArrayList<ZDevice> mDevices = new ArrayList();
        boolean sended = false;

        public UsbDialog(Context context, int theme) {
            super(context, theme);
            setContentView(R.layout.usb_hint_dialog);
        }

        public boolean remove(ArrayList<ZDevice> unmountDevices) {
            boolean remove = false;
            Iterator it = unmountDevices.iterator();
            while (it.hasNext()) {
                ZDevice device = (ZDevice) it.next();
                Iterator<ZDevice> iterator = this.mDevices.iterator();
                while (iterator.hasNext()) {
                    if (device.getPath().equals(((ZDevice) iterator.next()).getPath())) {
                        remove = true;
                        iterator.remove();
                        break;
                    }
                }
            }
            if (this.mDevices.isEmpty()) {
                super.dismiss();
            }
            return remove;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Button open = (Button) findViewById(R.id.update_hint_open);
            Button cancel = (Button) findViewById(R.id.update_hint_canle);
            ((CheckBox) findViewById(R.id.lock_ac_isport)).setOnCheckedChangeListener(this);
            ((TextView) findViewById(R.id.update_dialog_hint_title)).setText(R.string.check_usb);
            open.setText(R.string.open_usb);
            cancel.setText(R.string.cancel);
            open.setOnClickListener(this);
            cancel.setOnClickListener(this);
            Window window = getWindow();
            window.setType(2003);
            LayoutParams lp = window.getAttributes();
            lp.width = -1;
            lp.height = -1;
            open.requestFocus();
        }

        public void dismiss() {
            if (!this.sended && AppConstant.sAppRunState == 2) {
                UsbBroadcast.this.sendDevicesToActivity(getContext(), this.mDevices, false);
            }
            this.sended = false;
            this.mDevices.clear();
            super.dismiss();
        }

        public void show(ArrayList<ZDevice> devices) {
            this.mDevices.addAll(devices);
            super.show();
        }

        public void onClick(View v) {
            if (v.getId() == R.id.update_hint_open && !this.mDevices.isEmpty()) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.setFlags(335544320);
                intent.putExtra(AppConstant.EXTRA_ENTRY_MODE, 1);
                intent.putParcelableArrayListExtra(AppConstant.EXTRA_USB_DEVICE, this.mDevices);
                getContext().startActivity(intent);
                this.sended = true;
            }
            dismiss();
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            boolean z;
            if (isChecked) {
                z = false;
            } else {
                z = true;
            }
            AppConstant.sPrefereancesUsbTips = z;
            Editor editor = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit();
            editor.putBoolean(AppConstant.PREFEREANCES_USB_TIPS, AppConstant.sPrefereancesUsbTips);
            editor.commit();
        }
    }

    private class UsbHandler extends Handler {
        Context context;

        public UsbHandler(Context context, Looper looper) {
            super(looper);
            this.context = context;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ArrayList<ZDevice> devices = msg.obj;
                    switch (AppConstant.sAppRunState) {
                        case 0:
                            UsbBroadcast.this.checkShow(this.context, devices);
                            break;
                        case 1:
                            UsbBroadcast.this.sendDevicesToActivity(this.context, devices, false);
                            break;
                        case 2:
                            UsbBroadcast.this.checkShow(this.context, devices);
                            break;
                        default:
                            break;
                    }
                case 1:
                    ArrayList<ZDevice> unmountDevices = msg.obj;
                    UsbBroadcast.this.sendDevicesToActivity(this.context, unmountDevices, true);
                    if (UsbBroadcast.sDialog != null && UsbBroadcast.sDialog.isShowing()) {
                        UsbBroadcast.sDialog.remove(unmountDevices);
                        break;
                    }
            }
            super.handleMessage(msg);
        }
    }

    protected int getModel() {
        return BoxModel.sModel;
    }

    protected void onMount(Context context, ArrayList<ZDevice> devices, String path) {
        MyLog.d("on mount:" + devices.size());
        new UsbHandler(context, Looper.getMainLooper()).obtainMessage(0, devices).sendToTarget();
    }

    protected void onUmount(Context context, ArrayList<ZDevice> devices, String path) {
        MyLog.d("on umount:" + devices.size());
        new UsbHandler(context, Looper.getMainLooper()).obtainMessage(1, devices).sendToTarget();
    }

    private void showDialog(Context context, ArrayList<ZDevice> devices) {
        if (sDialog == null) {
            sDialog = new UsbDialog(context, R.style.defaultDialog);
        }
        sDialog.show(devices);
    }

    private static boolean isSataMount() {
        if (!isSataPath() || sIsSata) {
            return false;
        }
        sIsSata = true;
        return true;
    }

    private static boolean isSataUnMount() {
        if (isSataPath() || !sIsSata) {
            return false;
        }
        sIsSata = false;
        return true;
    }

    private static boolean isSataPath() {
        File dir = new File(SATA_PATH);
        if (dir.exists() && dir.isDirectory() && dir.listFiles().length > 1) {
            return true;
        }
        return false;
    }

    private void checkShow(Context context, ArrayList<ZDevice> devices) {
        if (isSataMount()) {
        }
        if (!context.getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getBoolean(AppConstant.PREFEREANCES_USB_TIPS, true) || AppConstant.sSystemBootTime == -1 || System.currentTimeMillis() - AppConstant.sSystemBootTime <= 10000 || Build.MODEL.equals("FM_PRO2")) {
            sendDevicesToActivity(context, devices, false);
        } else {
            showDialog(context, devices);
        }
    }

    private void sendDevicesToActivity(Context context, ArrayList<ZDevice> devices, boolean remove) {
        Intent intent = new Intent(AppConstant.ACTION_INNER_USB_BROADCAST);
        intent.putParcelableArrayListExtra(AppConstant.EXTRA_USB_DEVICE, devices);
        intent.putExtra(AppConstant.EXTRA_IS_REMOVE_OR_ADD_USB, remove);
        context.sendBroadcast(intent);
    }
}
