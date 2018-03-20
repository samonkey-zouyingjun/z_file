package com.zidoo.custom.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.init.ZidooJarPermissions;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ZidooStorageTool {
    private static final int CONNECTSTSTORAGE = 2;
    private static final int SECANADDSTORAGE = 0;
    private static final int SECANEDNSTSTORAGE = 1;
    private Context mContext = null;
    private String mExternalStorageDirectory = null;
    private Handler mHandler = null;
    private OnStorageMountListener mOnStorageMountListener = null;
    private BroadcastReceiver mReceiver = null;
    private ScanStorageOnListener mScanStorageOnListener = null;
    private ArrayList<ZidooStorageInfo> mStorageInfos = new ArrayList();

    public interface OnStorageMountListener {
        void mountStorageStatus(boolean z, ArrayList<ZidooStorageInfo> arrayList);
    }

    public interface ScanStorageOnListener {
        void onAddStorage(String str, int i);

        void onExitStorage(String str, int i);

        void onInitScan(String str, int i);

        void onInitScanEnd();

        void onInitScanStart();
    }

    public ZidooStorageTool(Context mContext) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = mContext;
        initData();
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        filter.addDataScheme(FileTypeManager.open_type_file);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    public void setScanStorageOnListener(ScanStorageOnListener scanStorageOnListener) {
        this.mScanStorageOnListener = scanStorageOnListener;
    }

    public void setOnStorageMountListener(OnStorageMountListener onStorageMountListener) {
        this.mOnStorageMountListener = onStorageMountListener;
    }

    public void startScanStorage() {
        this.mStorageInfos.clear();
        if (this.mScanStorageOnListener != null) {
            this.mScanStorageOnListener.onInitScanStart();
        }
        exeMount();
    }

    private void initData() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ZidooStorageInfo storageInfo = msg.obj;
                        if (ZidooStorageTool.this.mScanStorageOnListener != null) {
                            ZidooStorageTool.this.mScanStorageOnListener.onInitScan(storageInfo.mPath, storageInfo.mStorageType);
                            return;
                        }
                        return;
                    case 1:
                        if (ZidooStorageTool.this.mScanStorageOnListener != null) {
                            ZidooStorageTool.this.mScanStorageOnListener.onInitScanEnd();
                            return;
                        }
                        return;
                    case 2:
                        boolean isMount = ((Boolean) msg.obj).booleanValue();
                        if (ZidooStorageTool.this.mOnStorageMountListener != null) {
                            ZidooStorageTool.this.mOnStorageMountListener.mountStorageStatus(isMount, ZidooStorageTool.this.mStorageInfos);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String tmpstring = intent.getData().getPath();
                    if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED") || intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
                        int size = ZidooStorageTool.this.mStorageInfos.size();
                        for (int i = 0; i < size; i++) {
                            String path = ((ZidooStorageInfo) ZidooStorageTool.this.mStorageInfos.get(i)).mPath;
                            if (path.contains(tmpstring)) {
                                if (ZidooStorageTool.this.mScanStorageOnListener != null) {
                                    ZidooStorageTool.this.mScanStorageOnListener.onExitStorage(path, ((ZidooStorageInfo) ZidooStorageTool.this.mStorageInfos.get(i)).mStorageType);
                                }
                                ZidooStorageTool.this.mStorageInfos.remove(i);
                                if (ZidooStorageTool.this.mStorageInfos.size() <= 0) {
                                    ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(true)));
                                } else {
                                    ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(false)));
                                }
                            }
                        }
                        if (ZidooStorageTool.this.mStorageInfos.size() <= 0) {
                            ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(false)));
                        } else {
                            ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(true)));
                        }
                    } else if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
                        ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(true)));
                        ZidooStorageInfo storageInfo = ZidooStorageTool.this.addMountDevices(tmpstring);
                        if (storageInfo != null) {
                            ZidooStorageTool.this.mStorageInfos.add(storageInfo);
                            if (ZidooStorageTool.this.mScanStorageOnListener != null) {
                                ZidooStorageTool.this.mScanStorageOnListener.onAddStorage(storageInfo.mPath, storageInfo.mStorageType);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        initBroadCast();
    }

    public void release() {
        try {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mStorageInfos.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exeMount() {
        new Thread(new Runnable() {
            public void run() {
                ArrayList<String> mount_list = new ArrayList();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(AppConstant.PREFEREANCES_MOUNT).getInputStream()));
                    while (true) {
                        String line = in.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] split = line.split(" ");
                        if (split != null && split.length >= 3) {
                            String type;
                            String path;
                            if (VERSION.SDK_INT >= 25) {
                                type = split[4];
                                path = split[2];
                            } else {
                                type = split[2];
                                path = split[1];
                            }
                            if (type.equals("vfat") || type.equals("ntfs") || type.equals("ext4") || type.equals("ext2") || type.equals("ext3") || type.equals("extFat") || type.equals("exfat") || type.equals("fuse") || type.equals("ntfs3g") || type.equals("fuseblk")) {
                                mount_list.add(path);
                            }
                        }
                    }
                    ZidooStorageTool.this.mExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
                    ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(0, new ZidooStorageInfo(ZidooStorageTool.this.mExternalStorageDirectory, 2)));
                    int mount_size = mount_list.size();
                    boolean isconnect = false;
                    for (int i = 0; i < mount_size; i++) {
                        ZidooStorageInfo storageInfo = ZidooStorageTool.this.addMountDevices((String) mount_list.get(i));
                        if (storageInfo != null) {
                            ZidooStorageTool.this.mStorageInfos.add(storageInfo);
                            ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(0, storageInfo));
                            isconnect = true;
                        }
                    }
                    ZidooStorageTool.this.mHandler.sendMessage(ZidooStorageTool.this.mHandler.obtainMessage(2, Boolean.valueOf(isconnect)));
                    ZidooStorageTool.this.mHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ZidooStorageInfo addMountDevices(String path) {
        try {
            if (path.equals(this.mExternalStorageDirectory)) {
                return null;
            }
            if (path.contains("usb") || path.contains("uhost") || (path.contains("storage") && (ZidooBoxPermissions.isRealtekModel(this.mContext) || ZidooBoxPermissions.isMlogic905XModel(this.mContext) || (ZidooBoxPermissions.isRockModel(this.mContext) && VERSION.SDK_INT >= 23)))) {
                File usbFile = new File(path);
                if (usbFile == null || !usbFile.canRead()) {
                    return null;
                }
                return new ZidooStorageInfo(path, usbFile.getTotalSpace() > 322122547200L ? 4 : 0);
            } else if (path.contains("sd")) {
                sdFile = new File(path);
                if (sdFile == null || !sdFile.canRead()) {
                    return null;
                }
                return new ZidooStorageInfo(path, 1);
            } else if (!path.contains("sata")) {
                return null;
            } else {
                sdFile = new File(path);
                if (sdFile == null || !sdFile.canRead()) {
                    return null;
                }
                return new ZidooStorageInfo(path, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean unInstallUSB(Context context, String usbPath) {
        return unInstallUSB(context, usbPath, true, true);
    }

    public static boolean unInstallUSB(Context context, String usbPath, boolean isPrompt, boolean isClearmsg) {
        if (usbPath == null || usbPath.trim().equals("") || !ZidooAppTool.isInstall(context, "com.zidoo.busybox")) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("UninstallUsbPath", usbPath);
        bundle.putBoolean("isPrompt", isPrompt);
        bundle.putBoolean("isClearmsg", isClearmsg);
        Intent intent = new Intent();
        intent.setAction("zidoo.busybox.action");
        intent.putExtra("cmd", "UninstallUsb");
        intent.putExtra("parameter", bundle);
        context.sendBroadcast(intent);
        return true;
    }
}
