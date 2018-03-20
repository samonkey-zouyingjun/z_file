package com.zidoo.fileexplorer.main;

import android.app.Activity;
import android.os.Build;
import com.zidoo.control.center.tool.ZidooControlTool;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.log.MyLog;
import com.zidoo.custom.update.ZidooUpdate;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.control.AppControlService;
import com.zidoo.permissions.ZidooBoxPermissions;
import zidoo.file.FileType;
import zidoo.model.BoxModel;

public class BoxModelConfig {
    public static boolean DEFAULT_APK_VISIBLE_SET = true;
    public static final boolean IS_HONGHE = false;
    public static final boolean IS_PULUO = false;
    public static final boolean IS_TVBOX = false;
    public static final boolean IS_XTREAMER = false;

    public static int check(Activity activity) {
        int i = 0;
        FileType.reset();
        FileType.initModelType(BoxModel.sModel, false);
        ZidooControlTool.registerElement(activity, AppControlService.SERCIER_ELEMENTNAME, AppControlService.SERCIER_ACTION, 1);
        int[] backDraw = new int[]{R.drawable.bg, R.drawable.theme1, R.drawable.theme2, R.drawable.theme3};
        if (0 >= backDraw.length) {
        }
        new ZidooBoxPermissions(activity).checkPermissionsOnlyPictrue(activity, null, null);
        int bgdResid = backDraw[0];
        AppConstant.sShowLogo = Build.MODEL.contains("ZIDOO");
        if (!AppConstant.sShowLogo) {
            DEFAULT_APK_VISIBLE_SET = !ZidooAppTool.isInstall(activity, "com.kaiboer.pay");
        }
        if (Build.MODEL.equals("ZIDOO_H6 Pro CN")) {
            MyLog.v("   DEFAULT_APK_VISIBLE_SET = " + DEFAULT_APK_VISIBLE_SET + " sShowLogo = " + AppConstant.sShowLogo);
        } else {
            MyLog.v("   DEFAULT_APK_VISIBLE_SET = " + DEFAULT_APK_VISIBLE_SET + " sShowLogo = " + AppConstant.sShowLogo);
        }
        try {
            if (!ZidooBoxPermissions.isWorldModel(activity)) {
                i = 1;
            }
            new ZidooUpdate(activity, i).getImg();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bgdResid;
    }
}
