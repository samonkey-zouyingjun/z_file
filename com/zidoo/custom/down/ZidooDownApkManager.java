package com.zidoo.custom.down;

import android.content.Context;
import android.content.Intent;
import com.zidoo.custom.init.ZidooJarPermissions;
import java.io.File;

public class ZidooDownApkManager {
    private static ZidooDownApkManager mZidooDownTool = null;
    private Context mContext = null;

    private ZidooDownApkManager(Context mContext) {
        this.mContext = mContext;
        ZidooJarPermissions.checkZidooPermissions();
    }

    private ZidooDownApkManager() {
    }

    public static ZidooDownApkManager getInstance(Context context) {
        if (mZidooDownTool == null) {
            mZidooDownTool = new ZidooDownApkManager(context);
        }
        return mZidooDownTool;
    }

    public static void setMaxDownCount(Context context, int threaMax) {
        Intent intent_server = new Intent(context, ZidooDownApkService.class);
        intent_server.putExtra("threaMax", threaMax);
        intent_server.putExtra("cmd", 1);
        context.startService(intent_server);
    }

    public static void setDefaultInatall(boolean isDefaultInstall) {
        ZidooDownApkService.isDefaultInstall = isDefaultInstall;
    }

    public static void setDeleteApkFile(boolean isDeleteApkFile) {
        ZidooDownApkService.isDeleteApkFile = isDeleteApkFile;
    }

    private static void setDownApkFoldersPath(String downApkFoldersPath) {
        ZidooDownApkService.DOWNAPKPAHT = downApkFoldersPath;
        try {
            File file = new File(ZidooDownApkService.DOWNAPKPAHT);
            if (file == null || !file.exists()) {
                file.mkdirs();
                file.setReadable(true, false);
                file.setWritable(true, false);
            }
            if (file == null || !file.exists()) {
                throw new RuntimeException("zidoo downApkFoldersPath error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("zidoo downApkFoldersPath error");
        }
    }

    public static void initDownApk(Context context, String downApkFoldersPath) {
        context.startService(new Intent(context, ZidooDownApkService.class));
        setDownApkFoldersPath(downApkFoldersPath);
    }

    public void release() {
        mZidooDownTool = null;
    }

    public void registerDownApk(ZidooDownApkInfo zidooDownApkInfo, ZidooDownApkUITool zidooDownApkUITool) {
        if (zidooDownApkUITool == null) {
            throw new RuntimeException("zidoo zidooDownApkUITool null");
        }
        zidooDownApkUITool.start(zidooDownApkInfo);
    }
}
