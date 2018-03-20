package com.zidoo.custom.file;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ZApkFileTool {
    private static ZApkInfo getApkFileInfos_v51(Context mContext, String apkPath) {
        Exception e;
        Throwable th;
        ZApkInfo zApkInfo = null;
        try {
            Class<?> cPackageParser = Class.forName("android.content.pm.PackageParser");
            Constructor<?> ctPackageParser = cPackageParser.getConstructor(new Class[]{String.class});
            Method parsePackage = cPackageParser.getDeclaredMethod("parsePackage", new Class[]{File.class, String.class, DisplayMetrics.class, Integer.TYPE});
            Constructor<AssetManager> ctAssetManager = AssetManager.class.getConstructor(null);
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", new Class[]{String.class});
            Constructor<Resources> ctResources = Resources.class.getConstructor(new Class[]{AssetManager.class, DisplayMetrics.class, Configuration.class});
            Resources res = mContext.getResources();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            Configuration configuration = res.getConfiguration();
            PackageManager pm = mContext.getPackageManager();
            AssetManager oAssetManager = null;
            try {
                File apk = new File(apkPath);
                if (apk.exists()) {
                    PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, 1);
                    if (packageInfo == null) {
                        if (oAssetManager != null) {
                            oAssetManager.close();
                        }
                        return null;
                    }
                    ZApkInfo zApkInfo2 = new ZApkInfo();
                    try {
                        zApkInfo2.mInfo = packageInfo;
                        zApkInfo2.mLength = apk.length();
                        zApkInfo2.mPath = apkPath;
                        zApkInfo2.mVersion = packageInfo.versionName;
                        zApkInfo2.mCode = packageInfo.versionCode;
                        zApkInfo2.mPackageName = packageInfo.packageName;
                        Object oPackageParser = ctPackageParser.newInstance(new Object[]{apkPath});
                        new DisplayMetrics().setToDefaults();
                        Object oPackage = parsePackage.invoke(oPackageParser, new Object[]{new File(apkPath), apkPath, metrics, Integer.valueOf(0)});
                        ApplicationInfo info = (ApplicationInfo) oPackage.getClass().getDeclaredField("applicationInfo").get(oPackage);
                        oAssetManager = (AssetManager) ctAssetManager.newInstance(null);
                        addAssetPath.invoke(oAssetManager, new Object[]{apkPath});
                        Resources resources = (Resources) ctResources.newInstance(new Object[]{oAssetManager, displayMetrics, configuration});
                        if (info.labelRes != 0) {
                            zApkInfo2.mAppName = resources.getText(info.labelRes).toString();
                        }
                        if (info.icon != 0) {
                            zApkInfo2.mIcon = resources.getDrawable(info.icon);
                        }
                        if (oAssetManager != null) {
                            try {
                                oAssetManager.close();
                                zApkInfo = zApkInfo2;
                            } catch (Exception e2) {
                                e = e2;
                                zApkInfo = zApkInfo2;
                                Log.e("bob", "getApkFileInfos_v51 -1 error = " + e.toString());
                                return zApkInfo;
                            }
                        }
                        zApkInfo = zApkInfo2;
                    } catch (Exception e3) {
                        e = e3;
                        zApkInfo = zApkInfo2;
                        try {
                            Log.e("bob", "getApkFileInfos_v51 -0 error = " + e.toString());
                            if (oAssetManager != null) {
                                oAssetManager.close();
                            }
                            return zApkInfo;
                        } catch (Throwable th2) {
                            th = th2;
                            if (oAssetManager != null) {
                                oAssetManager.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        zApkInfo = zApkInfo2;
                        if (oAssetManager != null) {
                            oAssetManager.close();
                        }
                        throw th;
                    }
                    return zApkInfo;
                }
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
                return null;
            } catch (Exception e4) {
                e = e4;
                Log.e("bob", "getApkFileInfos_v51 -0 error = " + e.toString());
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
                return zApkInfo;
            }
        } catch (Exception e5) {
            e = e5;
            Log.e("bob", "getApkFileInfos_v51 -1 error = " + e.toString());
            return zApkInfo;
        }
    }

    public static ZApkInfo getApkFileInfos(Context mContext, String apkPath) {
        Exception e;
        Throwable th;
        ZApkInfo zApkInfo = null;
        try {
            Class<?> cPackageParser = Class.forName("android.content.pm.PackageParser");
            Method parsePackage = cPackageParser.getDeclaredMethod("parsePackage", new Class[]{File.class, Integer.TYPE});
            Constructor<AssetManager> ctAssetManager = AssetManager.class.getConstructor(null);
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", new Class[]{String.class});
            Constructor<Resources> ctResources = Resources.class.getConstructor(new Class[]{AssetManager.class, DisplayMetrics.class, Configuration.class});
            Resources res = mContext.getResources();
            DisplayMetrics displayMetrics = res.getDisplayMetrics();
            Configuration configuration = res.getConfiguration();
            PackageManager pm = mContext.getPackageManager();
            AssetManager oAssetManager = null;
            try {
                File apk = new File(apkPath);
                if (apk.exists()) {
                    PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, 1);
                    if (packageInfo == null) {
                        if (oAssetManager != null) {
                            oAssetManager.close();
                        }
                        return null;
                    }
                    ZApkInfo zApkInfo2 = new ZApkInfo();
                    try {
                        zApkInfo2.mInfo = packageInfo;
                        zApkInfo2.mLength = apk.length();
                        zApkInfo2.mPath = apkPath;
                        zApkInfo2.mVersion = packageInfo.versionName;
                        zApkInfo2.mCode = packageInfo.versionCode;
                        zApkInfo2.mPackageName = packageInfo.packageName;
                        Object oPackageParser = cPackageParser.newInstance();
                        new DisplayMetrics().setToDefaults();
                        Object oPackage = parsePackage.invoke(oPackageParser, new Object[]{new File(apkPath), Integer.valueOf(0)});
                        ApplicationInfo info = (ApplicationInfo) oPackage.getClass().getDeclaredField("applicationInfo").get(oPackage);
                        oAssetManager = (AssetManager) ctAssetManager.newInstance(null);
                        addAssetPath.invoke(oAssetManager, new Object[]{apkPath});
                        Resources resources = (Resources) ctResources.newInstance(new Object[]{oAssetManager, displayMetrics, configuration});
                        if (info.labelRes != 0) {
                            zApkInfo2.mAppName = resources.getText(info.labelRes).toString();
                        }
                        if (info.icon != 0) {
                            zApkInfo2.mIcon = resources.getDrawable(info.icon);
                        }
                        if (oAssetManager != null) {
                            try {
                                oAssetManager.close();
                                zApkInfo = zApkInfo2;
                            } catch (Exception e2) {
                                e = e2;
                                zApkInfo = zApkInfo2;
                                Log.e("bob", "getApkFileInfos -1 error = " + e.toString());
                                zApkInfo = getApkFileInfos_v51(mContext, apkPath);
                                return zApkInfo;
                            }
                        }
                        zApkInfo = zApkInfo2;
                    } catch (Exception e3) {
                        e = e3;
                        zApkInfo = zApkInfo2;
                        try {
                            Log.e("bob", "getApkFileInfos -0 error = " + e.toString());
                            if (oAssetManager != null) {
                                oAssetManager.close();
                            }
                            return zApkInfo;
                        } catch (Throwable th2) {
                            th = th2;
                            if (oAssetManager != null) {
                                oAssetManager.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        zApkInfo = zApkInfo2;
                        if (oAssetManager != null) {
                            oAssetManager.close();
                        }
                        throw th;
                    }
                    return zApkInfo;
                }
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
                return null;
            } catch (Exception e4) {
                e = e4;
                Log.e("bob", "getApkFileInfos -0 error = " + e.toString());
                if (oAssetManager != null) {
                    oAssetManager.close();
                }
                return zApkInfo;
            }
        } catch (Exception e5) {
            e = e5;
            Log.e("bob", "getApkFileInfos -1 error = " + e.toString());
            zApkInfo = getApkFileInfos_v51(mContext, apkPath);
            return zApkInfo;
        }
    }
}
