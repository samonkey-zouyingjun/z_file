package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.device.BlockInfo;
import zidoo.device.DeviceType;
import zidoo.device.DeviceUtils;
import zidoo.device.ZDevice;
import zidoo.tool.ZidooFileUtils;

public class RTD1295 extends BoxModel {
    public RTD1295(Context context) {
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
                            devices.add(new ZDevice(split[1].replaceAll("\\\\040", " "), DeviceType.SMB));
                        } else if ((tag & 8) == 0) {
                            continue;
                        } else if (split[2].equals("nfs")) {
                            devices.add(new ZDevice(split[1].replaceAll("\\\\040", " "), DeviceType.NFS));
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
                        DeviceUtils.saveUsbDeviceAsFile(RTD1295.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(RTD1295.this.mContext));
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

    @Deprecated
    public ArrayList<ZDevice> getDeviceListByCmd(int tag, boolean getBlock) {
        final ArrayList<ZDevice> devices = new ArrayList();
        try {
            String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
            if ((tag & 1) != 0) {
                devices.add(new ZDevice(externalStorageDirectory, DeviceType.FLASH));
            }
            ArrayList<TempBlock> blocks = new ArrayList();
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
                        } else if (path.contains("usb") || path.contains("uhost") || path.contains("udisk") || path.contains("storage")) {
                            ZDevice usbFile = new ZDevice(split[1]);
                            if (usbFile.canRead()) {
                                usbFile.setType(usbFile.getTotalSpace() > 322122547200L ? DeviceType.HDD : DeviceType.SD);
                                usbFile.setFormat(split[2]);
                                if (getBlock) {
                                    usbFile.setBlock(parseDevice(usbFile, split[0], blocks));
                                }
                                devices.add(usbFile);
                            } else {
                                continue;
                            }
                        } else if (path.contains("sd")) {
                            ZDevice sdFile = new ZDevice(split[1]);
                            if (sdFile.canRead()) {
                                sdFile.setType(DeviceType.SD);
                                sdFile.setFormat(split[2]);
                                if (getBlock) {
                                    sdFile.setBlock(parseDevice(sdFile, split[0], blocks));
                                }
                                devices.add(sdFile);
                            }
                        } else if (split[0].startsWith("/dev/block/vold/")) {
                            blocks.add(new TempBlock(path, split[0]));
                        }
                    } else if ((tag & 4) != 0 && split[2].equals("cifs")) {
                        devices.add(new ZDevice(split[1], DeviceType.SMB));
                    } else if ((tag & 8) != 0 && split[2].equals("nfs")) {
                        devices.add(new ZDevice(split[1], DeviceType.NFS));
                    } else if (split[0].startsWith("/dev/block/vold/")) {
                        blocks.add(new TempBlock(split[1], split[0]));
                    }
                }
            }
            if (this.mAutoSaveDevice) {
                new Thread(new Runnable() {
                    public void run() {
                        DeviceUtils.saveUsbDeviceAsFile(RTD1295.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(RTD1295.this.mContext));
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

    private BlockInfo parseDevice(ZDevice device, String dev, ArrayList<TempBlock> tempBlocks) {
        String block = null;
        Iterator it = tempBlocks.iterator();
        while (it.hasNext()) {
            TempBlock tb = (TempBlock) it.next();
            if (tb.path.endsWith(device.getName())) {
                block = tb.block;
                break;
            }
        }
        String label = null;
        if (device.getFormat().equals("fuseblk")) {
            String path = device.getPath();
            label = new String(path);
            if (label.endsWith("/")) {
                label = label.substring(0, path.length() - 1);
            }
            int e = label.lastIndexOf("/");
            if (e != -1) {
                label = label.substring(e + 1);
            }
        }
        return getDeviceBlockInfos(device, block, label);
    }

    public BlockInfo getDeviceBlockInfos(ZDevice device, String block, String label) {
        BlockInfo blockInfo = null;
        if (block != null) {
            try {
                Class<?> c = Class.forName("com.realtek.server.BlkidManager");
                Object blkidManager = c.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                String result = (String) c.getDeclaredMethod("blkid", new Class[]{String.class}).invoke(blkidManager, new Object[]{block});
                if (result != null) {
                    String param;
                    if (label == null) {
                        param = DeviceUtils.getParam(result, "LABEL");
                    } else {
                        param = label;
                    }
                    blockInfo = DeviceUtils.obtainUsbBlockInfo(device, block, param, DeviceUtils.getParam(result, "UUID"), DeviceUtils.getParam(result, "TYPE"));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
            }
        }
        if (blockInfo == null) {
            return DeviceUtils.obtainUsbBlockInfo(device, block, label, null, null);
        }
        return blockInfo;
    }

    protected Intent openVideo(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (ZidooFileUtils.isAppSystemInstall(this.mContext, "com.android.gallery3d")) {
            intent.setComponent(new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivity"));
            intent.putExtra("SourceFrom", "Local");
            boolean def = false;
            try {
                def = System.getInt(this.mContext.getContentResolver(), (String) System.class.getField("REALTEK_FORCE_RT_MEDIAPLAYER").get(null)) != 0;
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
            }
            intent.putExtra("MEDIA_BROWSER_USE_RT_MEDIA_PLAYER", def);
        }
        intent.addFlags(268468224);
        intent.setDataAndType(uri, "video/*");
        ZidooFileUtils.sendPauseBroadCast(this.mContext);
        return intent;
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

    public Intent getBDMVOpenWith(File dir) {
        Intent intent = null;
        if (ZidooFileUtils.isAppSystemInstall(this.mContext, "com.android.gallery3d")) {
            intent = new Intent();
            intent.setComponent(new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivity"));
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(dir), "video/*");
            boolean def = false;
            try {
                def = System.getInt(this.mContext.getContentResolver(), (String) System.class.getField("REALTEK_FORCE_RT_MEDIAPLAYER").get(null)) != 0;
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            } catch (IllegalArgumentException e4) {
                e4.printStackTrace();
            }
            intent.putExtra("MEDIA_BROWSER_USE_RT_MEDIA_PLAYER", def);
            intent.addFlags(268468224);
        }
        return intent;
    }

    public boolean isSupportBDMV() {
        return true;
    }
}
