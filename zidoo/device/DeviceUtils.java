package zidoo.device;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import com.umeng.common.a;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import zidoo.bean.VolumeInfo;
import zidoo.browse.BrowseConstant;

public class DeviceUtils {
    private static Class<?> CLASS_VOLUMN = null;
    private static Method GETBESTVOLUMEDESCRIPTION = null;
    private static final String SHELL_HEAD = "#!/system/bin/sh";
    private static final String SHELL_LOG_PATH = "/data/etc/nfs_log";
    private static final String SHELL_PATH = "/data/etc/nfsmanager.sh";
    private static final String SHELL_ROOT = "/data/etc";

    static {
        CLASS_VOLUMN = null;
        GETBESTVOLUMEDESCRIPTION = null;
        try {
            CLASS_VOLUMN = Class.forName("android.os.storage.VolumeInfo");
            GETBESTVOLUMEDESCRIPTION = StorageManager.class.getDeclaredMethod("getBestVolumeDescription", new Class[]{CLASS_VOLUMN});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public static BlockInfo getDeviceLabelBySu(ZDevice device, String block) {
        if (TextUtils.isEmpty(block)) {
            return null;
        }
        String label = null;
        String uuid = null;
        String type = null;
        try {
            String line = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("su -c blkid " + block).getInputStream())).readLine();
            if (line != null) {
                uuid = getParam(line, "UUID");
                label = getParam(line, "LABEL");
                type = getParam(line, "TYPE");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obtainUsbBlockInfo(device, block, label, uuid, type);
    }

    public static BlockInfo getDeviceLabelByCmd(ZDevice device, String block) {
        if (TextUtils.isEmpty(block)) {
            return null;
        }
        String line = executeWithRuselt("blkid " + block);
        String label = null;
        String uuid = null;
        String type = null;
        if (line != null) {
            try {
                uuid = getParam(line, "UUID");
                label = getParam(line, "LABEL");
                type = getParam(line, "TYPE");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obtainUsbBlockInfo(device, block, label, uuid, type);
    }

    public static String getParam(String line, String title) {
        Matcher matcher = Pattern.compile(title + "=\"[^\"]+\"").matcher(line);
        if (!matcher.find()) {
            return null;
        }
        String param = matcher.group();
        return param.substring(title.length() + 2, param.length() - 1);
    }

    public static void execute(String cmd) {
        File shellDir = new File(SHELL_ROOT);
        if (shellDir.exists() || shellDir.mkdirs()) {
            File shellLog = new File(SHELL_LOG_PATH);
            try {
                if (shellLog.exists()) {
                    shellLog.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                shellLog.createNewFile();
                shellLog.setReadable(true);
                shellLog.setExecutable(true);
                shellLog.setWritable(true);
                String command = cmd + " > " + SHELL_LOG_PATH + " 2>&1";
                try {
                    if (ShellFileWrite(new String[]{SHELL_HEAD, command})) {
                        try {
                            System.gc();
                            Thread.sleep(20);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        try {
                            Class.forName("android.os.SystemProperties").getDeclaredMethod("set", new Class[]{String.class, String.class}).invoke(null, new Object[]{"ctl.start", "nfsmanager"});
                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e3) {
                            e3.printStackTrace();
                        } catch (IllegalArgumentException e4) {
                            e4.printStackTrace();
                        } catch (InvocationTargetException e5) {
                            e5.printStackTrace();
                        } catch (ClassNotFoundException e6) {
                            e6.printStackTrace();
                        }
                    }
                } catch (Exception e7) {
                }
            } catch (IOException e8) {
                e8.printStackTrace();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String executeWithRuselt(java.lang.String r12) {
        /*
        execute(r12);
        r6 = 0;
        r4 = 0;
        r5 = r4;
    L_0x0006:
        r8 = 50;
        java.lang.Thread.sleep(r8);	 Catch:{ InterruptedException -> 0x002f }
    L_0x000b:
        r3 = new java.io.File;	 Catch:{ IOException -> 0x0034 }
        r7 = "/data/etc/nfs_log";
        r3.<init>(r7);	 Catch:{ IOException -> 0x0034 }
        r8 = r3.length();	 Catch:{ IOException -> 0x0034 }
        r10 = 0;
        r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r7 <= 0) goto L_0x0043;
    L_0x001d:
        r0 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0034 }
        r7 = new java.io.FileReader;	 Catch:{ IOException -> 0x0034 }
        r7.<init>(r3);	 Catch:{ IOException -> 0x0034 }
        r0.<init>(r7);	 Catch:{ IOException -> 0x0034 }
        r6 = r0.readLine();	 Catch:{ IOException -> 0x0034 }
        if (r6 == 0) goto L_0x003f;
    L_0x002d:
        r4 = r5;
    L_0x002e:
        return r6;
    L_0x002f:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ IOException -> 0x0034 }
        goto L_0x000b;
    L_0x0034:
        r1 = move-exception;
        r1.printStackTrace();
    L_0x0038:
        r4 = r5 + 1;
        r7 = 5;
        if (r5 >= r7) goto L_0x002e;
    L_0x003d:
        r5 = r4;
        goto L_0x0006;
    L_0x003f:
        r0.close();	 Catch:{ IOException -> 0x0034 }
        goto L_0x0038;
    L_0x0043:
        r3 = 0;
        java.lang.System.gc();	 Catch:{ IOException -> 0x0034 }
        goto L_0x0038;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.device.DeviceUtils.executeWithRuselt(java.lang.String):java.lang.String");
    }

    private static boolean ShellFileWrite(String[] cmd) {
        File shell = new File(SHELL_PATH);
        if (shell.exists()) {
            shell.delete();
        }
        try {
            shell.createNewFile();
            Runtime.getRuntime().exec("chmod 777 /data/etc/nfsmanager.sh");
            try {
                BufferedWriter buffwr = new BufferedWriter(new FileWriter(shell));
                for (String str : cmd) {
                    buffwr.write(str);
                    buffwr.newLine();
                    buffwr.flush();
                }
                buffwr.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    @Deprecated
    static String getVirtualCDRomPath(Context context, String isoFile) {
        try {
            String cdromPath = createVirtualCDRomPathIfNeed(context, isoFile);
            Class<?> ISOMountManager = Class.forName("com.softwinner.ISOMountManager");
            ISOMountManager.getMethod("umount", new Class[]{String.class}).invoke(null, new Object[]{cdromPath});
            if (((Integer) ISOMountManager.getMethod(AppConstant.PREFEREANCES_MOUNT, new Class[]{String.class, String.class}).invoke(null, new Object[]{cdromPath, isoFile})).intValue() == 0) {
                return cdromPath;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
        return null;
    }

    private static String createVirtualCDRomPathIfNeed(Context context, String isoFile) {
        File f = new File(context.getDir("CDROM", 0), new File(isoFile).getName());
        if (f.exists() || f.mkdirs()) {
            return f.getAbsolutePath();
        }
        return null;
    }

    @Deprecated
    static boolean playBlurayFolder(Context context, String path) {
        try {
            String videoPath = findBlurayVideo(context, path);
            if (videoPath == null) {
                return false;
            }
            File file = new File(videoPath);
            Intent bdIntent = new Intent();
            bdIntent.putExtra("android.intent.extra.bdfolderplaymode", true);
            bdIntent.setComponent(new ComponentName("com.softwinner.TvdVideo", "com.softwinner.TvdVideo.TvdVideoActivity"));
            bdIntent.setDataAndType(Uri.fromFile(file), "video/*");
            context.startActivity(bdIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e("ActivityNotFoundException", e.toString());
            return false;
        }
    }

    @Deprecated
    static boolean playBlurayFolder(Context context, String path, String realpath) {
        try {
            String videoPath = findBlurayVideo(context, path);
            if (videoPath == null) {
                return false;
            }
            File file = new File(videoPath);
            Intent bdIntent = new Intent();
            bdIntent.putExtra("VideoPath000", realpath);
            bdIntent.putExtra("android.intent.extra.bdfolderplaymode", true);
            bdIntent.setComponent(new ComponentName("com.softwinner.TvdVideo", "com.softwinner.TvdVideo.TvdVideoActivity"));
            bdIntent.setDataAndType(Uri.fromFile(file), "video/*");
            context.startActivity(bdIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e("ActivityNotFoundException", e.toString());
            return false;
        }
    }

    private static String findBlurayVideo(Context context, String path) {
        try {
            if (System.getInt(context.getContentResolver(), (String) System.class.getField("BD_FOLDER_PLAY_MODE").get(System.class), 0) != 0) {
                File f = new File(path + "/" + "BDMV/STREAM");
                if (f.exists() && f.isDirectory()) {
                    return path;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static File getDefaultDeviceConfigFile(Context context) {
        return new File(context.getDir(BrowseConstant.EXTRA_DEVICE, 0), "usb");
    }

    public static void saveUsbDeviceAsFile(Context context, ArrayList<ZDevice> devices, File file) {
        try {
            ZDevice device;
            String info;
            if (!file.exists()) {
                file.createNewFile();
            }
            ArrayList<ZDevice> usbDevices = new ArrayList();
            Iterator it = devices.iterator();
            while (it.hasNext()) {
                device = (ZDevice) it.next();
                if (device.getType() == DeviceType.TF || device.getType() == DeviceType.SD || device.getType() == DeviceType.HDD) {
                    usbDevices.add(device);
                }
            }
            if (usbDevices.size() > 0) {
                JSONArray array = new JSONArray();
                it = usbDevices.iterator();
                while (it.hasNext()) {
                    device = (ZDevice) it.next();
                    JSONObject devJso = new JSONObject();
                    devJso.put(BrowseConstant.EXTRA_PATH, device.getPath());
                    devJso.put(a.b, device.getType());
                    if (device.getBlock() != null) {
                        BlockInfo block = device.getBlock();
                        JSONObject blkJso = new JSONObject();
                        blkJso.put(BrowseConstant.EXTRA_PATH, block.getPath());
                        blkJso.put("label", block.getLabel());
                        blkJso.put(FavoriteDatabase.UUID, block.getUuid());
                        blkJso.put(a.b, block.getType());
                        devJso.put("block", blkJso);
                    }
                    array.put(devJso);
                }
                info = array.toString();
            } else {
                info = "";
            }
            FileWriter writer = new FileWriter(file);
            writer.write(info);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static ArrayList<ZDevice> getDevicesFromFile(Context context, File file) {
        ArrayList<ZDevice> devices = new ArrayList();
        try {
            FileReader fileReader = new FileReader(file);
            char[] buffer = new char[8192];
            StringBuffer sb = new StringBuffer();
            while (fileReader.read(buffer) != -1) {
                sb.append(buffer);
            }
            String result = sb.toString();
            if (!TextUtils.isEmpty(result)) {
                JSONArray array = new JSONArray(result);
                int len = array.length();
                for (int i = 0; i < len; i++) {
                    JSONObject devJso = array.getJSONObject(i);
                    ZDevice device = new ZDevice(devJso.getString(BrowseConstant.EXTRA_PATH), DeviceType.valueOf(devJso.getString(a.b)));
                    if (devJso.has("block")) {
                        JSONObject blkJso = devJso.getJSONObject("block");
                        device.setBlock(new BlockInfo(blkJso.getString(BrowseConstant.EXTRA_PATH), blkJso.has("label") ? blkJso.getString("label") : "??", blkJso.getString(FavoriteDatabase.UUID), blkJso.getString(a.b)));
                    }
                    devices.add(device);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        }
        return devices;
    }

    public static ArrayList<ZDevice> getVolumesForMarshmallow(Context context) {
        ArrayList<ZDevice> devices = new ArrayList();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService("storage");
            for (Object volume : (List) storageManager.getClass().getDeclaredMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])) {
                VolumeInfo vi = new VolumeInfo(volume);
                if (vi.isPublic() && vi.isMountedReadable()) {
                    ZDevice device = new ZDevice(vi.getPath());
                    DeviceType deviceType = ((String) GETBESTVOLUMEDESCRIPTION.invoke(storageManager, new Object[]{volume})).toLowerCase(Locale.getDefault()).contains("sd") ? DeviceType.TF : device.getTotalSpace() > 322122547200L ? DeviceType.HDD : DeviceType.SD;
                    device.setType(deviceType);
                    device.setBlock(obtainUsbBlockInfo(device, "/dev/block/vold/" + vi.getId(), vi.getLabel(), vi.getUuid(), vi.getType()));
                    devices.add(device);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
        return devices;
    }

    public static BlockInfo obtainUsbBlockInfo(ZDevice device, String path, String label, String uuid, String type) {
        if (path == null) {
            path = device.getPath();
        }
        if (type == null) {
            type = "zusb";
        }
        if (!TextUtils.isEmpty(label) && uuid != null) {
            return new BlockInfo(path, label, uuid, type);
        }
        File blockDir = new File(device.getPath() + "/.zxc");
        if (!blockDir.exists()) {
            blockDir.mkdirs();
        }
        File blockFile = new File(blockDir, "block");
        if (blockFile.exists()) {
            try {
                char[] buffer = new char[1024];
                int len = new FileReader(blockFile).read(buffer);
                if (len != -1) {
                    JSONObject object = new JSONObject(new String(buffer, 0, len));
                    return new BlockInfo(object.getString(BrowseConstant.EXTRA_PATH), object.getString("label"), object.getString(FavoriteDatabase.UUID), object.getString(a.b));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                blockFile.createNewFile();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        String code = null;
        if (TextUtils.isEmpty(label)) {
            code = obtainRandomCode();
            label = device.getType().name() + "(" + code + ")";
        }
        if (uuid == null) {
            if (code == null) {
                try {
                    code = obtainRandomCode();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            uuid = UUID.nameUUIDFromBytes(("ZIDOO-BLOCK-" + device.getType().name() + "-" + code).getBytes()).toString();
        }
        try {
            object = new JSONObject();
            object.put(BrowseConstant.EXTRA_PATH, path);
            object.put("label", label);
            object.put(FavoriteDatabase.UUID, uuid);
            object.put(a.b, type);
            FileWriter writer = new FileWriter(blockFile);
            writer.write(object.toString());
            writer.flush();
            writer.close();
        } catch (IOException e22) {
            e22.printStackTrace();
        } catch (JSONException e4) {
            e4.printStackTrace();
        }
        return new BlockInfo(path, label, uuid, type);
    }

    private static String obtainRandomCode() {
        Random random = new Random();
        int a = random.nextInt(10);
        int b = random.nextInt(10);
        String code = Long.toHexString(System.currentTimeMillis());
        if (code.length() > 4) {
            code = code.substring(code.length() - 4, code.length());
        }
        return (code + a) + b;
    }
}
