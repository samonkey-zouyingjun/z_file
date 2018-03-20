package com.zidoo.custom.init;

import android.content.Context;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.log.MyLog;
import com.zidoo.custom.widget.ZidooTypeface;
import com.zidoo.permissions.ZidooBoxPermissions;

public class ZidooJarPermissions {
    public static String LANGUAGE = "";
    private static final String[] PERMISSIONSAPP = new String[]{"com.zidoo.h3.ui", "com.example.zidootool", "com.zidoo.hotel", "com.zidoo.ziui5", "com.zidoo.shortcut.key", "com.example.zidootooltest", "com.qixun.smartphone", "com.zidoo.onekeytv", "com.zidoo.oldpeople.video", "com.zidoo.oldpeople.ui", "com.zidoo.oldpeople.wechat", "com.zidoo.push", "com.zidoo.haibao", "com.zidoo.voice", "android.rk.RockVideoPlayer", "com.kaiboer.launcher", "com.kaiboer.key.installation", "com.kaiboer.buy", "com.zidoo.hercules.help", "com.zidoo.old.media", "com.kaiboer.pay", "com.android.gallery3d", "com.zidoo.photoframe", "com.hsimov.vod", "com.zidoo.daydream", "com.zidoo.rtk.hdmi", "com.example.widgetstudy01", "com.zidoo.widget", "com.zidoo.usb.install", "com.zidoo.lingcod.tv.launcher", "com.android.quick.settings", "com.zidoo.audioplayer", "dddcom.zidoo.audioplayer", "com.zidoo.ziui", "com.zidoo.app", "com.zidoo.hh.launcher", "com.zidoo.bluraynavigation", "com.zidoo.subtitle", "com.zidoo.photoframe", "com.vef.ui", "com.rockchips.mediacenter", "com.softwinner.TvdVideo", "com.zidoo.control.center", "com.zidoo.cibn.launcher"};
    private static boolean isZidooPermission = false;
    private static ZidooBoxPermissions mZidooPermissions = null;

    public static void checkZidooPermissions() {
        if (!isZidooPermission) {
            throw new RuntimeException("ZidooJarPermissions error Please contact Zidoo Mr.Bob");
        }
    }

    public static boolean initZidooJar(Context context) {
        return initZidooJar(context, null);
    }

    public static boolean initZidooJar(Context context, String currentTextStyle) {
        MyLog.v("jar 20160707");
        initData(context, currentTextStyle);
        String currentPnmae = ZidooAppTool.getPackageName(context);
        for (Object equals : PERMISSIONSAPP) {
            if (currentPnmae.equals(equals)) {
                isZidooPermission = true;
                return true;
            }
        }
        throw new RuntimeException("ZidooJarPermissions error Please contact Zidoo Mr.Bob");
    }

    public static void initZidooJar(Context context, int[] drawID, String[] md5) {
        initZidooJar(context);
        mZidooPermissions = new ZidooBoxPermissions(context);
        mZidooPermissions.checkPermissionsOnlyPictrue(context, drawID, md5);
    }

    public static void initZidooJarByModel(Context context, int[] drawID, String[] md5) {
        initZidooJar(context);
        mZidooPermissions = new ZidooBoxPermissions(context);
        mZidooPermissions.checkPermissionsByModel(context, drawID, md5);
    }

    public static void initZidooJarByUI(Context context, int[] drawID, String[] md5) {
        initZidooJar(context);
        mZidooPermissions = new ZidooBoxPermissions(context);
        mZidooPermissions.checkPermissionsByUI(context, drawID, md5);
    }

    public static void initZidooJarByOnlyPictrue(Context context, int[] drawID, String[] md5) {
        initZidooJar(context);
        mZidooPermissions = new ZidooBoxPermissions(context);
        mZidooPermissions.checkPermissionsOnlyPictrue(context, drawID, md5);
    }

    public static void initZidooJarByAll(Context context, int[] drawID, String[] md5) {
        initZidooJar(context);
        mZidooPermissions = new ZidooBoxPermissions(context);
        mZidooPermissions.checkPermissionsByAll(context, drawID, md5);
    }

    public static void initData(Context context, String currentTextStyle) {
        try {
            LANGUAGE = context.getResources().getConfiguration().locale.getLanguage();
            ZidooTypeface.reset(currentTextStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextStyle(String currentTextStyle) {
        try {
            ZidooTypeface.reset(currentTextStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
