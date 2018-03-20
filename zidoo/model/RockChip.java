package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import com.zidoo.custom.usb.FileTypeManager;
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

public class RockChip extends BoxModel {
    public RockChip(Context context) {
        super(context);
    }

    public ArrayList<ZDevice> getDeviceList(int tag, boolean getBlock) {
        final ArrayList<ZDevice> devices = new ArrayList();
        try {
            String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
            if ((tag & 1) != 0) {
                devices.add(new ZDevice(externalStorageDirectory, DeviceType.FLASH));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/mounts"))));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                String[] split = line.split(" ");
                if (split != null && split.length >= 3) {
                    if ((tag & 2) != 0 && (split[2].equals("vfat") || split[2].equals("ntfs") || split[2].equals("ext4") || split[2].equals("ext2") || split[2].equals("ext3") || split[2].equals("extFat") || split[2].equals("hfsplus") || split[2].equals("exfat") || split[2].equals("fuse") || split[2].equals("ntfs3g") || split[2].equals("fuseblk"))) {
                        String path = split[1];
                        if (externalStorageDirectory.equals(path)) {
                            continue;
                        } else if (path.contains("usb") || path.contains("uhost") || path.contains("udisk") || (VERSION.SDK_INT >= 23 && path.contains("storage"))) {
                            ZDevice usbFile = new ZDevice(path);
                            if (usbFile.canRead()) {
                                format = split[2];
                                usbFile.setFormat(split[2]);
                                usbFile.setType(usbFile.getTotalSpace() > 322122547200L ? DeviceType.HDD : DeviceType.SD);
                                if (getBlock) {
                                    blockInfo = DeviceUtils.getDeviceLabelByCmd(usbFile, split[0]);
                                    if (format.equals("fuseblk")) {
                                        label = new String(path);
                                        if (label.endsWith("/")) {
                                            label = label.substring(0, path.length() - 1);
                                        }
                                        e = label.lastIndexOf("/");
                                        if (e != -1) {
                                            label = label.substring(e + 1);
                                        }
                                        blockInfo.setLabel(label);
                                    }
                                    usbFile.setBlock(blockInfo);
                                }
                                devices.add(usbFile);
                            } else {
                                continue;
                            }
                        } else if (path.contains("sd")) {
                            ZDevice sdFile = new ZDevice(path);
                            if (sdFile != null && sdFile.canRead()) {
                                format = split[2];
                                sdFile.setFormat(split[2]);
                                if (getBlock) {
                                    blockInfo = DeviceUtils.getDeviceLabelByCmd(sdFile, split[0]);
                                    if (format.equals("fuseblk")) {
                                        label = new String(path);
                                        if (label.endsWith("/")) {
                                            label = label.substring(0, path.length() - 1);
                                        }
                                        e = label.lastIndexOf("/");
                                        if (e != -1) {
                                            label = label.substring(e + 1);
                                        }
                                        blockInfo.setLabel(label);
                                    }
                                    sdFile.setBlock(blockInfo);
                                }
                                sdFile.setType(DeviceType.SD);
                                devices.add(sdFile);
                            }
                        } else if (path.contains("samba")) {
                        }
                    } else if ((tag & 4) != 0 && split[2].equals("cifs")) {
                        devices.add(new ZDevice(split[1], DeviceType.SMB));
                    } else if ((tag & 8) != 0 && split[2].equals("nfs")) {
                        devices.add(new ZDevice(split[1], DeviceType.NFS));
                    }
                }
            }
            if (this.mAutoSaveDevice) {
                new Thread(new Runnable() {
                    public void run() {
                        DeviceUtils.saveUsbDeviceAsFile(RockChip.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(RockChip.this.mContext));
                    }
                }).start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return devices;
    }

    protected Intent openAudio(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        Intent zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
        if (zidooAudio != null) {
            intent = zidooAudio;
        }
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    protected Intent openVideo(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (ZidooFileUtils.isAppSystemInstall(this.mContext, "android.rk.RockVideoPlayer")) {
            intent.setComponent(new ComponentName("android.rk.RockVideoPlayer", "android.rk.RockVideoPlayer.VideoPlayActivity"));
            intent.putExtra("PlayMode", FileTypeManager.open_type_file);
            intent.putStringArrayListExtra("PlayList", ZidooFileUtils.getPalyList(file.getPath()));
        }
        intent.addFlags(268468224);
        intent.setDataAndType(uri, "video/*");
        ZidooFileUtils.sendPauseBroadCast(this.mContext);
        return intent;
    }

    public Intent getBDMVOpenWith(File dir) {
        if (!ZidooFileUtils.isAppSystemInstall(this.mContext, "android.rk.RockVideoPlayer")) {
            return null;
        }
        Uri uri = Uri.fromFile(dir);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setComponent(new ComponentName("android.rk.RockVideoPlayer", "android.rk.RockVideoPlayer.VideoPlayActivity"));
        intent.addFlags(268468224);
        intent.putExtra("PlayMode", FileTypeManager.open_type_file);
        intent.putStringArrayListExtra("PlayList", ZidooFileUtils.getPalyList(dir.getPath()));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    public boolean isSupportBDMV() {
        return true;
    }
}
