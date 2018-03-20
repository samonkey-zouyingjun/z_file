package com.zidoo.fileexplorer.tool;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.FastIdentifier;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.config.AppConstant;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import zidoo.file.FileType;
import zidoo.tool.ZidooFileUtils;

public class Utils {
    public static void toast(Context context, String msg, int duration) {
        ZidooToast.toast(context, msg);
    }

    public static void toast(Context context, String msg) {
        toast(context, msg, 0);
    }

    public static void toast(Context context, int stirngId) {
        toast(context, context.getString(stirngId), 0);
    }

    public static String formatFileSize(long size) {
        if (size < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return size + " B";
        }
        if (size < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            return String.format("%.2f", new Object[]{Float.valueOf(((float) size) / 1024.0f)}) + " KB";
        } else if (size < 1073741824) {
            return String.format("%.2f", new Object[]{Float.valueOf(((float) size) / 1048576.0f)}) + " M";
        } else {
            return String.format("%.2f", new Object[]{Float.valueOf(((float) size) / 1.07374182E9f)}) + " G";
        }
    }

    public static String formatUsbDevice(int num) {
        return AppConstant.sUsb[num];
    }

    public static String formatHddDevice(int num) {
        return AppConstant.sHdd[num];
    }

    public static int findSelectdPosition(String[] fileLists, String selected) {
        for (int i = 0; i < fileLists.length; i++) {
            if (fileLists[i].equalsIgnoreCase(selected)) {
                return i;
            }
        }
        return -1;
    }

    public static int findSelectdPosition(File[] files, File selected) {
        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(selected)) {
                return i;
            }
        }
        return -1;
    }

    public static final boolean pingIp(String ip, int timeOut) {
        int result = -1;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ping -c 1 -w " + timeOut + " " + ip);
            result = process.waitFor();
            process.exitValue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        } finally {
            process.destroy();
        }
        if (result == 0) {
            return true;
        }
        return false;
    }

    public static boolean matchIP(String ip) {
        return Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}").matcher(ip).matches();
    }

    public static boolean NetIsConnected(Context context) {
        return wifiIsConnected(context) || etherNetIsConnected(context);
    }

    public static boolean wifiIsConnected(Context context) {
        try {
            NetworkInfo wifiInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            if (wifiInfo != null) {
                return wifiInfo.isConnected();
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean etherNetIsConnected(Context context) {
        try {
            return ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9).isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean pingHost(String ip, int timeout) {
        try {
            return InetAddress.getByName(ip).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap createAlbumThumbnail(java.lang.String r7) {
        /*
        r1 = 0;
        r3 = new android.media.MediaMetadataRetriever;
        r3.<init>();
        r3.setDataSource(r7);	 Catch:{ IllegalArgumentException -> 0x0022, RuntimeException -> 0x003b }
        r0 = r3.getEmbeddedPicture();	 Catch:{ IllegalArgumentException -> 0x0022, RuntimeException -> 0x003b }
        r4 = 0;
        r5 = r0.length;	 Catch:{ IllegalArgumentException -> 0x0022, RuntimeException -> 0x003b }
        r1 = android.graphics.BitmapFactory.decodeByteArray(r0, r4, r5);	 Catch:{ IllegalArgumentException -> 0x0022, RuntimeException -> 0x003b }
        r3.release();	 Catch:{ RuntimeException -> 0x0017 }
    L_0x0016:
        return r1;
    L_0x0017:
        r2 = move-exception;
        r4 = "Utils";
        r5 = "获取音乐缩略图出错2";
        android.util.Log.e(r4, r5);
        goto L_0x0016;
    L_0x0022:
        r2 = move-exception;
        r4 = "Utils";
        r5 = "获取音乐缩略图出错0";
        android.util.Log.e(r4, r5);	 Catch:{ all -> 0x0054 }
        r3.release();	 Catch:{ RuntimeException -> 0x0030 }
        goto L_0x0016;
    L_0x0030:
        r2 = move-exception;
        r4 = "Utils";
        r5 = "获取音乐缩略图出错2";
        android.util.Log.e(r4, r5);
        goto L_0x0016;
    L_0x003b:
        r2 = move-exception;
        r4 = "Utils";
        r5 = "获取音乐缩略图出错1";
        android.util.Log.e(r4, r5);	 Catch:{ all -> 0x0054 }
        r3.release();	 Catch:{ RuntimeException -> 0x0049 }
        goto L_0x0016;
    L_0x0049:
        r2 = move-exception;
        r4 = "Utils";
        r5 = "获取音乐缩略图出错2";
        android.util.Log.e(r4, r5);
        goto L_0x0016;
    L_0x0054:
        r4 = move-exception;
        r3.release();	 Catch:{ RuntimeException -> 0x0059 }
    L_0x0058:
        throw r4;
    L_0x0059:
        r2 = move-exception;
        r5 = "Utils";
        r6 = "获取音乐缩略图出错2";
        android.util.Log.e(r5, r6);
        goto L_0x0058;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.tool.Utils.createAlbumThumbnail(java.lang.String):android.graphics.Bitmap");
    }

    public static Drawable getApkIcon_5_1(Context context, File apk) {
        Drawable icon = null;
        try {
            Class<?> cPackageParser = Class.forName("android.content.pm.PackageParser");
            Method parsePackage = cPackageParser.getDeclaredMethod("parsePackage", new Class[]{File.class, Integer.TYPE});
            Constructor<AssetManager> ctAssetManager = AssetManager.class.getConstructor((Class[]) null);
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", new Class[]{String.class});
            Constructor<Resources> ctResources = Resources.class.getConstructor(new Class[]{AssetManager.class, DisplayMetrics.class, Configuration.class});
            Resources res = context.getResources();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            Configuration configuration = res.getConfiguration();
            AssetManager oAssetManager = null;
            try {
                String apkPath = apk.getPath();
                Object oPackageParser = cPackageParser.newInstance();
                new DisplayMetrics().setToDefaults();
                Object oPackage = parsePackage.invoke(oPackageParser, new Object[]{new File(apkPath), Integer.valueOf(0)});
                ApplicationInfo info = (ApplicationInfo) oPackage.getClass().getDeclaredField("applicationInfo").get(oPackage);
                oAssetManager = (AssetManager) ctAssetManager.newInstance((Object[]) null);
                addAssetPath.invoke(oAssetManager, new Object[]{apkPath});
                Resources resources = (Resources) ctResources.newInstance(new Object[]{oAssetManager, displayMetrics, configuration});
                if (info.icon != 0) {
                    icon = resources.getDrawable(info.icon);
                }
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            } catch (Exception e) {
                Log.e("getApkFileInfos", e.toString());
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            } catch (Throwable th) {
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            }
        } catch (Exception e2) {
        }
        return icon;
    }

    public static Drawable getApkIcon(Context context, File apk) {
        Drawable icon = null;
        try {
            Class<?> cPackageParser = Class.forName("android.content.pm.PackageParser");
            Constructor<?> ctPackageParser = cPackageParser.getConstructor(new Class[]{String.class});
            Method parsePackage = cPackageParser.getDeclaredMethod("parsePackage", new Class[]{File.class, String.class, DisplayMetrics.class, Integer.TYPE});
            Constructor<AssetManager> ctAssetManager = AssetManager.class.getConstructor((Class[]) null);
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", new Class[]{String.class});
            Constructor<Resources> ctResources = Resources.class.getConstructor(new Class[]{AssetManager.class, DisplayMetrics.class, Configuration.class});
            Resources res = context.getResources();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            Configuration configuration = res.getConfiguration();
            AssetManager oAssetManager = null;
            try {
                String apkPath = apk.getPath();
                Object oPackageParser = ctPackageParser.newInstance(new Object[]{apkPath});
                new DisplayMetrics().setToDefaults();
                Object oPackage = parsePackage.invoke(oPackageParser, new Object[]{new File(apkPath), apkPath, metrics, Integer.valueOf(0)});
                ApplicationInfo info = (ApplicationInfo) oPackage.getClass().getDeclaredField("applicationInfo").get(oPackage);
                oAssetManager = (AssetManager) ctAssetManager.newInstance((Object[]) null);
                addAssetPath.invoke(oAssetManager, new Object[]{apkPath});
                Resources resources = (Resources) ctResources.newInstance(new Object[]{oAssetManager, displayMetrics, configuration});
                if (info.icon != 0) {
                    icon = resources.getDrawable(info.icon);
                }
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            } catch (Exception e) {
                Log.e("getApkFileInfos", e.toString());
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            } catch (Throwable th) {
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
            }
            return icon;
        } catch (Exception e2) {
            return getApkIcon_5_1(context, apk);
        }
    }

    public static void saveFiel(Context context, String s) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput("mylog.txt", 0);
            fos.write(s.getBytes());
            fos.flush();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (IOException e32) {
            e32.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e322) {
                    e322.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e3222) {
                    e3222.printStackTrace();
                }
            }
        }
    }

    public static SmbFileFilter getDefaultSmbFileFilter() {
        return new SmbFileFilter() {
            public boolean accept(SmbFile file) throws SmbException {
                String path = file.getPath();
                return (path.endsWith("$") || path.endsWith("$/") || file.getType() == 32 || (!AppConstant.sPrefereancesHidden && file.getName().startsWith("."))) ? false : true;
            }
        };
    }

    public static boolean isZh(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().endsWith("zh");
    }

    public static boolean isIllegal(String s) {
        if (s.length() > 11) {
            return true;
        }
        return Pattern.compile("(M-[^M]){2}").matcher(s).find();
    }

    public static boolean getApkVisible(Context context) {
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://zidoo.fileexplorer/apk/visible"), null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                if (cursor.getInt(0) == 1) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setApkVisible(Context context, boolean visible) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://zidoo.fileexplorer/apk/visible");
            ContentValues values = new ContentValues();
            values.put(AppConstant.PREFEREANCES_APK_VISIBLE, Boolean.valueOf(visible));
            if (contentResolver.update(uri, values, null, null) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Deprecated
    public static ArrayList<String> getRemovedDevicesPath() throws IOException {
        ArrayList<String> temp = new ArrayList();
        if (temp.size() == 0) {
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(AppConstant.PREFEREANCES_MOUNT).getInputStream()));
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return temp;
            }
            String[] split = line.split(" ");
            if (split != null && split.length >= 3) {
                String tag = split[2];
                if (tag.equals("vfat") || tag.equals("ntfs") || tag.equals("ext4") || tag.equals("ext2") || tag.equals("ext3") || split[2].equals("hfsplus") || tag.equals("extFat") || tag.equals("exfat") || tag.equals("fuse") || tag.equals("ntfs3g") || tag.equals("fuseblk")) {
                    String path = split[1];
                    if (path.contains("usb") || path.contains("uhost") || path.contains("udisk") || path.contains("sd")) {
                        Iterator<String> iterator = temp.iterator();
                        while (iterator.hasNext()) {
                            if (path.equals((String) iterator.next())) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getFastIdentifierType(Context context, int tag) {
        switch (tag) {
            case 0:
                return context.getString(R.string.identifier_type_flash);
            case 1:
                return context.getString(R.string.identifier_type_usb);
            case 2:
            case 3:
            case 6:
                return context.getString(R.string.identifier_type_smb);
            case 4:
            case 5:
                return context.getString(R.string.identifier_type_nfs);
            default:
                return context.getString(R.string.identifier_type_flash);
        }
    }

    public static String getSmbShare(String url) {
        String uri = url.substring(6);
        uri = uri.substring(uri.indexOf("/") + 1);
        int p = uri.indexOf("/");
        return p == -1 ? uri : uri.substring(0, p);
    }

    public static String getSmbUri(String url) {
        String uri = url.substring(6);
        int sp = uri.indexOf(47);
        return sp == -1 ? uri : uri.substring(sp);
    }

    public static boolean characterIllegal(String s) {
        String[] illegals = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
        for (CharSequence contains : illegals) {
            if (s.contains(contains)) {
                return true;
            }
        }
        return false;
    }

    public static void sortFiles(File[] result, int sortWay) {
        switch (sortWay) {
            case 0:
                CompareTool.sortByName(result, true);
                return;
            case 1:
                CompareTool.sortBySize(result, true);
                return;
            case 2:
                CompareTool.sortByDate(result, true);
                return;
            case 3:
                CompareTool.sortByType(result, true);
                return;
            case 4:
                CompareTool.sortByName(result, false);
                return;
            case 5:
                CompareTool.sortBySize(result, false);
                return;
            case 6:
                CompareTool.sortByDate(result, false);
                return;
            case 7:
                CompareTool.sortByType(result, false);
                return;
            default:
                return;
        }
    }

    public static void createShortcut(Context context, String name, Intent launcherIntent, int iconRes, Bitmap iconBitmap) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", true);
        shortcutIntent.putExtra("android.intent.extra.shortcut.NAME", name);
        shortcutIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(context, iconRes));
        shortcutIntent.putExtra("android.intent.extra.shortcut.ICON", iconBitmap);
        shortcutIntent.putExtra("android.intent.extra.shortcut.INTENT", launcherIntent);
        shortcutIntent.setExtrasClassLoader(FastIdentifier.class.getClassLoader());
        context.sendBroadcast(shortcutIntent);
    }

    public static final String pathToUri(String root, String path) {
        String mountName;
        String other;
        String uri = path.substring(root.length() + 1);
        int sp = uri.indexOf(47);
        if (sp == -1) {
            mountName = uri;
            other = "";
        } else {
            mountName = uri.substring(0, sp);
            other = uri.substring(sp);
        }
        int jp = mountName.indexOf(35);
        StringBuilder stringBuilder = new StringBuilder();
        if (jp != -1) {
            mountName = mountName.substring(jp + 1);
        }
        return stringBuilder.append(ZidooFileUtils.decodeCommand(mountName)).append(other).toString();
    }

    public static boolean isInstall(Context context, String packageName) {
        return isAppLaunchInstall(context, packageName) || isAppSystemInstall(context, packageName);
    }

    public static boolean isAppLaunchInstall(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    public static boolean isAppSystemInstall(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File[] getVideoList(String path, boolean showHidden, int way) {
        try {
            FileFilter filter;
            File video = new File(path);
            if (showHidden) {
                filter = new FileFilter() {
                    public boolean accept(File file) {
                        return FileType.isAudioFile(file);
                    }
                };
            } else {
                filter = new FileFilter() {
                    public boolean accept(File file) {
                        if (file.getName().charAt(0) == '.' || !FileType.isAudioFile(file)) {
                            return false;
                        }
                        return true;
                    }
                };
            }
            File[] files = video.getParentFile().listFiles(filter);
            sortFiles(files, way);
            String name = video.getName();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(name)) {
                    int index = i;
                    return files;
                }
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
            return new File[0];
        }
    }

    public static void startActivityForRTD(Context context, Intent intent) {
        if (intent != null) {
            try {
                intent.putExtra(AppConstant.PREFEREANCES_HIDDEN, AppConstant.sPrefereancesHidden);
                intent.putExtra(AppConstant.PREFEREANCES_SORT, AppConstant.sPrefereancesSortWay);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getFavoriteType(Favorite favorite) {
        String uri = favorite.getUri();
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        int e = uri.lastIndexOf(47);
        return FileType.getFileType(e == -1 ? uri : uri.substring(e + 1));
    }

    public static boolean execute(String... progArray) {
        int result = -1;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(progArray);
            result = p.waitFor();
            if (p != null) {
                try {
                    p.exitValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (p != null) {
                try {
                    p.exitValue();
                } catch (Exception e22) {
                    e22.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (p != null) {
                try {
                    p.exitValue();
                } catch (Exception e222) {
                    e222.printStackTrace();
                }
            }
        }
        return result == 0;
    }
}
