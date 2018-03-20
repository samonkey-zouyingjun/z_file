package com.zidoo.custom.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.umeng.common.a;
import com.zidoo.custom.share.ZidooSharedPrefsUtil;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ZidooAppTool {
    public static final String FACTORY_KAIBOER = "com.kaiboer.text";
    public static final String FACTORY_ZIDOO = "com.zidoo.test";
    public static final String Install_hit_pg = "com.zidoo.busybox";
    public static final String Install_hit_pg_old = "com.android.install.apk";
    private static final String STARTAPPSHAER = "start_app_share";

    public static boolean openFactoryTest(Context context) {
        if (isAppLaunchInstall(context, FACTORY_ZIDOO)) {
            openApp(context, new ZidooStartAppInfo(FACTORY_ZIDOO));
            return true;
        } else if (isAppLaunchInstall(context, FACTORY_KAIBOER)) {
            openApp(context, new ZidooStartAppInfo(FACTORY_KAIBOER));
            return true;
        } else {
            Intent intent;
            if (isInstall(context, FACTORY_ZIDOO)) {
                try {
                    intent = new Intent(FACTORY_ZIDOO);
                    intent.setComponent(new ComponentName(FACTORY_ZIDOO, "com.zidoo.test.HomeActivity"));
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isInstall(context, FACTORY_KAIBOER)) {
                try {
                    intent = new Intent(FACTORY_KAIBOER);
                    intent.setComponent(new ComponentName(FACTORY_KAIBOER, "com.kaiboer.text.HomeActivity"));
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return false;
        }
    }

    public static boolean isInstall(Context context, String packageName) {
        if (isAppLaunchInstall(context, packageName) || isAppSystemInstall(context, packageName)) {
            return true;
        }
        return false;
    }

    public static boolean isAppLaunchInstall(Context context, String packageName) {
        if (packageName == null || context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return false;
        }
        return true;
    }

    public static boolean isAppSystemInstall(Context context, String pName) {
        if (pName == null) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pName, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }
        return false;
    }

    public static boolean isSystemApp(Context context, String pName) {
        try {
            if ((context.getPackageManager().getPackageInfo(pName, 0).applicationInfo.flags & 1) != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSystemUpdateApp(Context context, String pName) {
        try {
            if ((context.getPackageManager().getPackageInfo(pName, 0).applicationInfo.flags & 128) != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isCanUninstall(Context context, String pName) {
        if (!isSystemApp(context, pName) || isSystemUpdateApp(context, pName)) {
            return true;
        }
        return false;
    }

    public static boolean isUserApp(Context context, String pName) {
        return (isSystemApp(context, pName) || isSystemUpdateApp(context, pName)) ? false : true;
    }

    public static Drawable getAppIcon(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(packageName, 1);
            if (info != null) {
                return packageManager.getApplicationIcon(info.applicationInfo);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppLabel(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getPackageInfo(packageName, 1).applicationInfo).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isAppUpdata(Context context, String packageName, String newVersionName, int newVersionCode) {
        if (isAppUpdataByVersionCode(context, packageName, newVersionCode) || isAppUpdataByVersionName(context, packageName, newVersionName)) {
            return true;
        }
        return false;
    }

    public static boolean isAppUpdataByVersionName(Context context, String packageName, String newVersionName) {
        String oldVersion = getAppVersionName(context, packageName);
        if (oldVersion == null) {
            return false;
        }
        return isUpdataVersions(newVersionName, oldVersion);
    }

    public static boolean isAppUpdataByVersionCode(Context context, String packageName, int newVersionCode) {
        int oldVersionCode = getAppVersionCode(context, packageName);
        if (oldVersionCode >= 0 && oldVersionCode < newVersionCode) {
            return true;
        }
        return false;
    }

    public static String getAppVersionName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 1).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 1).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAppVersionCode(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 1).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isUpdataVersions(String newVersion, String oldVersion) {
        try {
            if (newVersion.equals(oldVersion)) {
                return false;
            }
            int i;
            newVersion = newVersion.replaceAll("-", "\\.");
            oldVersion = oldVersion.replaceAll("-", "\\.");
            String[] sNewVersion = newVersion.split("\\.");
            String[] sOldVersion = oldVersion.split("\\.");
            ArrayList<String> newVersionArray = new ArrayList();
            ArrayList<String> oldVersionArray = new ArrayList();
            newVersionArray.addAll(Arrays.asList(sNewVersion));
            oldVersionArray.addAll(Arrays.asList(sOldVersion));
            int difference;
            if (newVersionArray.size() > oldVersionArray.size()) {
                difference = newVersionArray.size() - oldVersionArray.size();
                for (i = 0; i < difference; i++) {
                    oldVersionArray.add("0");
                }
            } else {
                difference = oldVersionArray.size() - newVersionArray.size();
                for (i = 0; i < difference; i++) {
                    newVersionArray.add("0");
                }
            }
            i = 0;
            Iterator it = newVersionArray.iterator();
            while (it.hasNext()) {
                String s = (String) it.next();
                String old = (String) oldVersionArray.get(i);
                try {
                    int newVer = Integer.parseInt(s);
                    int oldVer = Integer.parseInt(old);
                    if (newVer > oldVer) {
                        return true;
                    }
                    if (newVer < oldVer) {
                        return false;
                    }
                    i++;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    int temp = s.compareToIgnoreCase(old);
                    if (temp < 0) {
                        return false;
                    }
                    if (temp > 0) {
                        return true;
                    }
                    i++;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openAPPDetail(Context context, String packageName) {
        if (packageName == null) {
            return false;
        }
        try {
            if (packageName.equals("")) {
                return false;
            }
            Intent intent = new Intent();
            if (isInstall(context, "com.android.tv.settings")) {
                intent.setPackage("com.android.tv.settings");
            }
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts(a.c, packageName, null));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setBootStartApp(Context context, String packageName) {
        if (packageName == null || packageName.trim().equals("")) {
            return false;
        }
        ZidooSharedPrefsUtil.putValue(context, STARTAPPSHAER, packageName);
        return true;
    }

    public static String getBootStartApp(Context context) {
        return ZidooSharedPrefsUtil.getValue(context, STARTAPPSHAER, null);
    }

    public static boolean cancelBootStartApp(Context context) {
        ZidooSharedPrefsUtil.delValue(context, STARTAPPSHAER);
        return true;
    }

    public static boolean isBootStartApp(Context context, String packageName) {
        if (packageName == null || packageName.equals("")) {
            return false;
        }
        String hitname = ZidooSharedPrefsUtil.getValue(context, STARTAPPSHAER, null);
        if (hitname == null || hitname.equals("") || !hitname.equals(packageName)) {
            return false;
        }
        return true;
    }

    public static void startBootAPP(Context context) {
        String pname = ZidooSharedPrefsUtil.getValue(context, STARTAPPSHAER, null);
        if (pname != null && !pname.equals("")) {
            openApp(context, new ZidooStartAppInfo(pname));
        }
    }

    public static boolean clearAppData(Context context, String pName, boolean isPrompt, boolean isClearmsg) {
        if (pName == null || pName.trim().equals("")) {
            return false;
        }
        Intent intent;
        if (isInstall(context, "com.zidoo.busybox")) {
            Bundle bundle = new Bundle();
            bundle.putString("ClearPackageName", pName);
            bundle.putBoolean("isPrompt", isPrompt);
            bundle.putBoolean("isClearmsg", isClearmsg);
            intent = new Intent();
            intent.setAction("zidoo.busybox.action");
            intent.putExtra("cmd", "ClearApkData");
            intent.putExtra("parameter", bundle);
            context.sendBroadcast(intent);
            return true;
        } else if (!isInstall(context, "com.android.install.apk")) {
            return false;
        } else {
            intent = new Intent();
            intent.setAction("install_apk_receiver.action");
            intent.putExtra("clearpname", pName);
            context.sendBroadcast(intent);
            return true;
        }
    }

    public static boolean clearAppData(Context context, String pName) {
        return clearAppData(context, pName, true, true);
    }

    public static boolean hitInstallApk(File file, Context context) {
        Intent intent;
        if (isInstall(context, "com.zidoo.busybox")) {
            Bundle bundle = new Bundle();
            bundle.putString("InstallPath", file.getAbsolutePath());
            intent = new Intent();
            intent.setAction("zidoo.busybox.action");
            intent.putExtra("cmd", "InstallApk");
            intent.putExtra("parameter", bundle);
            context.sendBroadcast(intent);
            return true;
        } else if (!isInstall(context, "com.android.install.apk")) {
            return false;
        } else {
            intent = new Intent();
            intent.setAction("install_apk_receiver.action");
            intent.putExtra("installPath", file.getAbsolutePath());
            context.sendBroadcast(intent);
            return true;
        }
    }

    public static void installApk(File file, Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(268435456);
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hitUnInstallApk(Context context, String pName) {
        if (pName == null || !isInstall(context, "com.zidoo.busybox")) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("UninstallPackageName", pName);
        Intent intent = new Intent();
        intent.setAction("zidoo.busybox.action");
        intent.putExtra("cmd", "UninstallApk");
        intent.putExtra("parameter", bundle);
        context.sendBroadcast(intent);
        return true;
    }

    public static boolean isCanHitUnInstallApk(Context context) {
        return isInstall(context, "com.zidoo.busybox");
    }

    public static boolean unInstall(Context context, String packageName) {
        if (packageName == null) {
            return false;
        }
        try {
            if (packageName.equals("")) {
                return false;
            }
            context.startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + packageName)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean openApp(android.content.Context r12, com.zidoo.custom.app.ZidooStartAppInfo r13) {
        /*
        r7 = 0;
        if (r13 != 0) goto L_0x0004;
    L_0x0003:
        return r7;
    L_0x0004:
        r3 = 0;
        r5 = 1;
        r8 = r13.getOpenType();	 Catch:{ Exception -> 0x00fc }
        switch(r8) {
            case 0: goto L_0x0093;
            case 1: goto L_0x00a1;
            case 2: goto L_0x00ce;
            case 3: goto L_0x0102;
            case 4: goto L_0x013f;
            default: goto L_0x000d;
        };	 Catch:{ Exception -> 0x00fc }
    L_0x000d:
        if (r3 == 0) goto L_0x0003;
    L_0x000f:
        r8 = r13.getBundle();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x001c;
    L_0x0015:
        r8 = r13.getBundle();	 Catch:{ Exception -> 0x00fc }
        r3.putExtras(r8);	 Catch:{ Exception -> 0x00fc }
    L_0x001c:
        r8 = r13.getKey_str();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0030;
    L_0x0022:
        r8 = r13.getValue_str();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0030;
    L_0x0028:
        r2 = 0;
    L_0x0029:
        r8 = r13.getKey_str();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.length;	 Catch:{ Exception -> 0x00fc }
        if (r2 < r8) goto L_0x0163;
    L_0x0030:
        r8 = r13.getKey_int();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0044;
    L_0x0036:
        r8 = r13.getValue_int();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0044;
    L_0x003c:
        r2 = 0;
    L_0x003d:
        r8 = r13.getKey_int();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.length;	 Catch:{ Exception -> 0x00fc }
        if (r2 < r8) goto L_0x0176;
    L_0x0044:
        r8 = r13.getKey_float();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0058;
    L_0x004a:
        r8 = r13.getValue_float();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0058;
    L_0x0050:
        r2 = 0;
    L_0x0051:
        r8 = r13.getKey_float();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.length;	 Catch:{ Exception -> 0x00fc }
        if (r2 < r8) goto L_0x0189;
    L_0x0058:
        r8 = r13.getKey_double();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x006c;
    L_0x005e:
        r8 = r13.getValue_double();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x006c;
    L_0x0064:
        r2 = 0;
    L_0x0065:
        r8 = r13.getKey_double();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.length;	 Catch:{ Exception -> 0x00fc }
        if (r2 < r8) goto L_0x019c;
    L_0x006c:
        r8 = r13.getKey_boolean();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0080;
    L_0x0072:
        r8 = r13.getValue_boolean();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x0080;
    L_0x0078:
        r2 = 0;
    L_0x0079:
        r8 = r13.getKey_boolean();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.length;	 Catch:{ Exception -> 0x00fc }
        if (r2 < r8) goto L_0x01af;
    L_0x0080:
        if (r5 == 0) goto L_0x01c2;
    L_0x0082:
        r6 = com.zidoo.custom.db.ZidooSQliteManger.getClassBaseManger(r12);	 Catch:{ Exception -> 0x00fc }
        r8 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r6.onclickInsertData(r8);	 Catch:{ Exception -> 0x00fc }
        startActivity(r3, r12);	 Catch:{ Exception -> 0x00fc }
    L_0x0090:
        r7 = 1;
        goto L_0x0003;
    L_0x0093:
        r8 = r12.getPackageManager();	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r3 = r8.getLaunchIntentForPackage(r9);	 Catch:{ Exception -> 0x00fc }
        goto L_0x000d;
    L_0x00a1:
        r8 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r8 = isInstall(r12, r8);	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x00ab:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x00b1:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.trim();	 Catch:{ Exception -> 0x00fc }
        r9 = "";
        r8 = r8.equals(r9);	 Catch:{ Exception -> 0x00fc }
        if (r8 != 0) goto L_0x000d;
    L_0x00c2:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x00fc }
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r4.<init>(r8);	 Catch:{ Exception -> 0x00fc }
        r3 = r4;
        goto L_0x000d;
    L_0x00ce:
        r8 = r12.getPackageManager();	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r3 = r8.getLaunchIntentForPackage(r9);	 Catch:{ Exception -> 0x00fc }
        if (r3 == 0) goto L_0x000d;
    L_0x00dc:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x00e2:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.trim();	 Catch:{ Exception -> 0x00fc }
        r9 = "";
        r8 = r8.equals(r9);	 Catch:{ Exception -> 0x00fc }
        if (r8 != 0) goto L_0x000d;
    L_0x00f3:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r3.setAction(r8);	 Catch:{ Exception -> 0x00fc }
        goto L_0x000d;
    L_0x00fc:
        r1 = move-exception;
    L_0x00fd:
        r1.printStackTrace();
        goto L_0x0003;
    L_0x0102:
        r8 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r8 = isAppSystemInstall(r12, r8);	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x010c:
        r8 = r13.getActivity();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x0112:
        r8 = r13.getActivity();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.trim();	 Catch:{ Exception -> 0x00fc }
        r9 = "";
        r8 = r8.equals(r9);	 Catch:{ Exception -> 0x00fc }
        if (r8 != 0) goto L_0x000d;
    L_0x0123:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x00fc }
        r8 = r13.getPckName();	 Catch:{ Exception -> 0x00fc }
        r4.<init>(r8);	 Catch:{ Exception -> 0x00fc }
        r0 = new android.content.ComponentName;	 Catch:{ Exception -> 0x01c7 }
        r8 = r13.getPckName();	 Catch:{ Exception -> 0x01c7 }
        r9 = r13.getActivity();	 Catch:{ Exception -> 0x01c7 }
        r0.<init>(r8, r9);	 Catch:{ Exception -> 0x01c7 }
        r4.setComponent(r0);	 Catch:{ Exception -> 0x01c7 }
        r3 = r4;
        goto L_0x000d;
    L_0x013f:
        r5 = 0;
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        if (r8 == 0) goto L_0x000d;
    L_0x0146:
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r8 = r8.trim();	 Catch:{ Exception -> 0x00fc }
        r9 = "";
        r8 = r8.equals(r9);	 Catch:{ Exception -> 0x00fc }
        if (r8 != 0) goto L_0x000d;
    L_0x0157:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x00fc }
        r8 = r13.getAction();	 Catch:{ Exception -> 0x00fc }
        r4.<init>(r8);	 Catch:{ Exception -> 0x00fc }
        r3 = r4;
        goto L_0x000d;
    L_0x0163:
        r8 = r13.getKey_str();	 Catch:{ Exception -> 0x00fc }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getValue_str();	 Catch:{ Exception -> 0x00fc }
        r9 = r9[r2];	 Catch:{ Exception -> 0x00fc }
        r3.putExtra(r8, r9);	 Catch:{ Exception -> 0x00fc }
        r2 = r2 + 1;
        goto L_0x0029;
    L_0x0176:
        r8 = r13.getKey_int();	 Catch:{ Exception -> 0x00fc }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getValue_int();	 Catch:{ Exception -> 0x00fc }
        r9 = r9[r2];	 Catch:{ Exception -> 0x00fc }
        r3.putExtra(r8, r9);	 Catch:{ Exception -> 0x00fc }
        r2 = r2 + 1;
        goto L_0x003d;
    L_0x0189:
        r8 = r13.getKey_float();	 Catch:{ Exception -> 0x00fc }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getValue_float();	 Catch:{ Exception -> 0x00fc }
        r9 = r9[r2];	 Catch:{ Exception -> 0x00fc }
        r3.putExtra(r8, r9);	 Catch:{ Exception -> 0x00fc }
        r2 = r2 + 1;
        goto L_0x0051;
    L_0x019c:
        r8 = r13.getKey_double();	 Catch:{ Exception -> 0x00fc }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getValue_double();	 Catch:{ Exception -> 0x00fc }
        r10 = r9[r2];	 Catch:{ Exception -> 0x00fc }
        r3.putExtra(r8, r10);	 Catch:{ Exception -> 0x00fc }
        r2 = r2 + 1;
        goto L_0x0065;
    L_0x01af:
        r8 = r13.getKey_boolean();	 Catch:{ Exception -> 0x00fc }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00fc }
        r9 = r13.getValue_boolean();	 Catch:{ Exception -> 0x00fc }
        r9 = r9[r2];	 Catch:{ Exception -> 0x00fc }
        r3.putExtra(r8, r9);	 Catch:{ Exception -> 0x00fc }
        r2 = r2 + 1;
        goto L_0x0079;
    L_0x01c2:
        r12.sendBroadcast(r3);	 Catch:{ Exception -> 0x00fc }
        goto L_0x0090;
    L_0x01c7:
        r1 = move-exception;
        r3 = r4;
        goto L_0x00fd;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.app.ZidooAppTool.openApp(android.content.Context, com.zidoo.custom.app.ZidooStartAppInfo):boolean");
    }

    private static void startActivity(Intent intent, Context context) {
        try {
            intent.addFlags(335544320);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openGooglePlaySoftDetaied(Context context, String pName) {
        try {
            Intent localIntent = new Intent();
            Uri localUri = Uri.parse("market://details?id=" + pName);
            localIntent.setAction("android.intent.action.VIEW");
            localIntent.setData(localUri);
            context.startActivity(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openWeb(Context context, String webUrl) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(webUrl)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openQuickSettings(Context context) {
        try {
            if (isAppLaunchInstall(context, "com.android.quick.settings")) {
                openApp(context, new ZidooStartAppInfo("com.android.quick.settings"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openSettings(Context context) {
        try {
            if (isAppLaunchInstall(context, "com.android.tv.settings")) {
                openApp(context, new ZidooStartAppInfo("com.android.tv.settings"));
                return;
            }
            try {
                Intent inre = new Intent("com.android.settings");
                inre.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                inre.addFlags(335544320);
                context.startActivity(inre);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void openZidooUpdate(Context context) {
        try {
            if (isAppLaunchInstall(context, "com.zidoo.ota.update")) {
                openApp(context, new ZidooStartAppInfo("com.zidoo.ota.update"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openZidooFile(Context context) {
        try {
            if (isAppLaunchInstall(context, "com.zidoo.fileexplorer")) {
                openApp(context, new ZidooStartAppInfo("com.zidoo.fileexplorer"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openBluetooth(Context context) {
        try {
            Intent inre = new Intent("android.settings.BLUETOOTH_SETTINGS");
            inre.setPackage("com.android.settings");
            context.startActivity(inre);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openPower(Context context) {
        try {
            Intent inre = new Intent("com.zidoo.custom.power");
            inre.setComponent(new ComponentName("com.zidoo.custom.power", "com.power.zidoo.HomeActivity"));
            inre.addFlags(335544320);
            inre.putExtra("isLongPowerKey", false);
            context.startActivity(inre);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openUpwizard(Context context) {
        if (ZidooBoxPermissions.isUpwizard(context)) {
            try {
                if (isInstall(context, "com.zidoo.setupwizard")) {
                    ZidooBoxPermissions.setUpwizard(context, 0);
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.zidoo.setupwizard", "com.zidoo.setupwizard.NewWelcomeActivity"));
                    intent.addFlags(335544320);
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
