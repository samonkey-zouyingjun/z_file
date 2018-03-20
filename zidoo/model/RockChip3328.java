package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import zidoo.device.DeviceType;
import zidoo.device.DeviceUtils;
import zidoo.device.ZDevice;
import zidoo.tool.ZidooFileUtils;

public class RockChip3328 extends BoxModel {
    public RockChip3328(Context context) {
        super(context);
    }

    public ArrayList<ZDevice> getDeviceList(int tag, boolean getBlock) {
        final ArrayList<ZDevice> devices = new ArrayList();
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
        if ((tag & 1) != 0) {
            devices.add(new ZDevice(externalStorageDirectory, DeviceType.FLASH));
        }
        if ((tag & 2) != 0) {
            devices.addAll(DeviceUtils.getVolumesForMarshmallow(this.mContext));
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (this.mAutoSaveDevice) {
                new Thread(new Runnable() {
                    public void run() {
                        DeviceUtils.saveUsbDeviceAsFile(RockChip3328.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(RockChip3328.this.mContext));
                    }
                }).start();
            }
            return devices;
        }
        try {
            if (this.mAutoSaveDevice) {
                new Thread(/* anonymous class already generated */).start();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return devices;
    }

    protected Intent openAudio(File file) {
        Intent zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
        if (zidooAudio == null) {
            return super.openAudio(file);
        }
        zidooAudio.setDataAndType(Uri.fromFile(file), "audio/*");
        return zidooAudio;
    }

    protected Intent openVideo(File file) {
        try {
            ComponentName component = new ComponentName("com.rockchips.mediacenter", "com.rockchips.mediacenter.videoplayer.VideoPlayerActivity");
            if (this.mContext.getPackageManager().getActivityInfo(component, 0) != null) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(268468224);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                intent.setComponent(component);
                ZidooFileUtils.sendPauseBroadCast(this.mContext);
                return intent;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return super.openVideo(file);
    }

    public Intent getBDMVOpenWith(File dir) {
        ComponentName component = new ComponentName("com.rockchips.mediacenter", "com.rockchips.mediacenter.videoplayer.VideoPlayerActivity");
        try {
            if (this.mContext.getPackageManager().getActivityInfo(component, 0) != null) {
                Uri uri = Uri.fromFile(dir);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setComponent(component);
                intent.addFlags(268468224);
                intent.setDataAndType(uri, "video/*");
                return intent;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSupportBDMV() {
        return true;
    }
}
