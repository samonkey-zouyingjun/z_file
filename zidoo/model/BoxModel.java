package zidoo.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import zidoo.bean.StorageVolume;
import zidoo.device.DeviceReorganise;
import zidoo.device.DeviceType;
import zidoo.device.DeviceUtils;
import zidoo.device.ZDevice;
import zidoo.file.FileType;
import zidoo.file.OpenWith;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsManager;
import zidoo.nfs.NfsMount;
import zidoo.samba.manager.SambaManager;
import zidoo.samba.manager.SmbMount;
import zidoo.tool.ZidooFileUtils;

public abstract class BoxModel implements SmbMount, OpenWith, NfsMount, DeviceReorganise {
    public static final int MODEL_AMLOGIC = 4;
    public static final int MODEL_AMLOGIC_905X = 6;
    public static final int MODEL_H3 = 2;
    public static final int MODEL_H6 = 9;
    public static final int MODEL_INVALID = -1;
    public static final int MODEL_MSTART = 1;
    public static final int MODEL_OTHER = 0;
    public static final int MODEL_REALTEK = 5;
    public static final int MODEL_ROCKCHIP = 3;
    public static final int MODEL_ROCKCHIP_3229 = 7;
    public static final int MODEL_ROCKCHIP_3328 = 8;
    public static final int MODEL_ROCKCHIP_SDK_23 = 3001;
    public static int sModel = -1;
    protected boolean mAutoSaveDevice = true;
    protected Context mContext;
    protected NfsManager mNfsManager;
    private SambaManager mSambaManager;

    class TempBlock {
        String block;
        String path;

        TempBlock(String path, String block) {
            this.path = path;
            this.block = block;
        }
    }

    public BoxModel(Context context) {
        this.mContext = context;
    }

    public static BoxModel getModel(Context context, int model) {
        switch (model) {
            case 1:
                return new Mstar(context);
            case 2:
                return new H3(context);
            case 3:
            case 7:
                return new RockChip(context);
            case 4:
                return new Amlogic(context);
            case 5:
                return new RTD1295(context);
            case 6:
                return new Amlogic905x(context);
            case 8:
                return new RockChip3328(context);
            case 9:
                return new H6(context);
            case MODEL_ROCKCHIP_SDK_23 /*3001*/:
                return new RockChipSDK23(context);
            default:
                return new DefualtModel(context);
        }
    }

    public static BoxModel getModel(Context context) {
        return getModel(context, getModelCode(context));
    }

    @Deprecated
    public static final int getBoxModel(Context context) {
        return getModelCode(context);
    }

    public static final int getModelCode(Context context) {
        if (sModel == -1 && context != null) {
            sModel = ZidooBoxPermissions.getBoxModel(context);
            if (sModel == 3 && VERSION.SDK_INT >= 23) {
                sModel = MODEL_ROCKCHIP_SDK_23;
            }
        }
        return sModel;
    }

    public static final void setBoxModel(int model) {
        sModel = model;
    }

    protected SambaManager getSambaManager() {
        if (this.mSambaManager == null) {
            this.mSambaManager = SambaManager.getManager(this.mContext, getBoxModel(this.mContext));
        }
        return this.mSambaManager;
    }

    protected NfsManager getNfsManager() {
        if (this.mNfsManager == null) {
            this.mNfsManager = NfsFactory.getNfsManager(this.mContext, getBoxModel(this.mContext));
        }
        return this.mNfsManager;
    }

    public String getSmbRoot() {
        return getSambaManager().getSmbRoot();
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        return getSambaManager().mountSmb(sharePath, mountPoint, ip, user, pwd);
    }

    public boolean unMountSmb(File file) {
        return getSambaManager().unMountSmb(file);
    }

    public String getNfsRoot() {
        return getNfsManager().getNfsRoot();
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        return getNfsManager().mountNfs(ip, sharePath, mountPoint);
    }

    public boolean umountNfs(File file) {
        return getNfsManager().umountNfs(file);
    }

    public boolean isNfsMounted(String ip, String sharePath, String mountPath) {
        return getNfsManager().isNfsMounted(ip, sharePath, mountPath);
    }

    public void setAutoSaveDevice(boolean auto) {
        this.mAutoSaveDevice = auto;
    }

    public ArrayList<ZDevice> getLocalDeviceList() {
        return getDeviceList(3, false);
    }

    public ArrayList<ZDevice> getDeviceList(int tag, boolean getBlock) {
        final ArrayList<ZDevice> devices = new ArrayList();
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
        if ((tag & 1) != 0) {
            devices.add(new ZDevice(externalStorageDirectory, DeviceType.FLASH));
        }
        if ((tag & 2) != 0) {
            try {
                StorageManager storageManager = (StorageManager) this.mContext.getSystemService("storage");
                for (Object volume : (Object[]) storageManager.getClass().getDeclaredMethod("getVolumeList", new Class[0]).invoke(storageManager, new Object[0])) {
                    StorageVolume storageVolume = new StorageVolume(volume);
                    if (!storageVolume.isPrimary() && storageVolume.getState().equals("mounted") && storageVolume.getPath().canRead()) {
                        ZDevice device = new ZDevice(storageVolume.getPath().getPath());
                        device.setType(device.getTotalSpace() > 322122547200L ? DeviceType.HDD : DeviceType.SD);
                        device.setBlock(DeviceUtils.obtainUsbBlockInfo(device, "/dev/block/vold/" + storageVolume.getStorageId(), storageVolume.getLabel(), storageVolume.getUuid(), "vfat"));
                        devices.add(device);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((tag & 4) != 0 || (tag & 8) != 0) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/mounts"))));
                while (true) {
                    String line = in.readLine();
                    if (line != null) {
                        String[] split = line.split(" ");
                        if ((tag & 4) != 0 && split[2].equals("cifs")) {
                            devices.add(new ZDevice(split[1], DeviceType.SMB));
                        } else if ((tag & 8) == 0) {
                            continue;
                        } else if (split[2].equals("nfs")) {
                            devices.add(new ZDevice(split[1], DeviceType.NFS));
                        }
                    }
                }
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            if (this.mAutoSaveDevice) {
                new Thread(new Runnable() {
                    public void run() {
                        DeviceUtils.saveUsbDeviceAsFile(BoxModel.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(BoxModel.this.mContext));
                    }
                }).start();
            }
            return devices;
        }
        try {
            if (this.mAutoSaveDevice) {
                new Thread(/* anonymous class already generated */).start();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return devices;
    }

    public void openFile(File file) {
        try {
            Intent intent = getOpenWith(file);
            if (intent != null) {
                this.mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("Manager", e.toString());
        }
    }

    public Intent getOpenWith(File file) {
        switch (FileType.getType(file)) {
            case 1:
                return openAudio(file);
            case 2:
                return openVideo(file);
            case 3:
                return openImage(file);
            case 4:
                return openTxt(file);
            case 5:
                return openApk(file);
            case 6:
                return openPdf(file);
            case 7:
                return openWord(file);
            case 8:
                return openExcel(file);
            case 9:
                return openPpt(file);
            case 10:
                return openHtml(file);
            case 11:
                return openZip(file);
            case 12:
                return openOther(file);
            default:
                return null;
        }
    }

    protected Intent openAudio(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        return intent;
    }

    protected Intent openVideo(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        ZidooFileUtils.sendPauseBroadCast(this.mContext);
        return intent;
    }

    protected Intent openImage(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.imageplayer") != null) {
            intent.setClassName("com.zidoo.imageplayer", "com.zidoo.imageplayer.main.ImagePagerActivity");
        }
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return intent;
    }

    protected Intent openTxt(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "text/plain");
        return intent;
    }

    protected Intent openApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    protected Intent openHtml(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "text/html");
        return intent;
    }

    protected Intent openWord(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/msword");
        return intent;
    }

    protected Intent openPdf(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        return intent;
    }

    protected Intent openExcel(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        return intent;
    }

    protected Intent openPpt(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
        return intent;
    }

    protected Intent openZip(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "text/html");
        return intent;
    }

    protected Intent openOther(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "*/*");
        return intent;
    }

    public void openBDMV(File dir) {
        try {
            Intent intent = getBDMVOpenWith(dir);
            if (intent != null) {
                this.mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("Manager", e.toString());
        }
    }

    public void destory() {
        if (this.mSambaManager != null) {
            this.mSambaManager.destory();
        }
    }
}
