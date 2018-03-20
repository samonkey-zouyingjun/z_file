package com.zidoo.custom.db;

import android.content.Context;
import com.zidoo.custom.app.AppClassBaseManger;
import com.zidoo.custom.app.AppTopBaseManger;
import com.zidoo.custom.db.DBConstants.Constant;
import com.zidoo.custom.down.ZidooDownApkBaseManger;
import com.zidoo.custom.lock.LockBaseManger;

public class ZidooSQliteManger {
    public static AppClassBaseManger getClassBaseManger(Context context) {
        if (Constant.class_dataBase != null) {
            return Constant.class_dataBase;
        }
        Constant.class_dataBase = new AppClassBaseManger(context);
        return Constant.class_dataBase;
    }

    public static ZidooDownApkBaseManger getDownApkBaseManger(Context context) {
        if (Constant.down_apk_dataBase != null) {
            return Constant.down_apk_dataBase;
        }
        Constant.down_apk_dataBase = new ZidooDownApkBaseManger(context);
        return Constant.down_apk_dataBase;
    }

    public static LockBaseManger getLockBaseManger(Context context) {
        if (Constant.mLockBaseManger != null) {
            return Constant.mLockBaseManger;
        }
        Constant.mLockBaseManger = new LockBaseManger(context);
        return Constant.mLockBaseManger;
    }

    public static AppTopBaseManger getAppTopBaseManger(Context context) {
        if (Constant.mAppTopBaseManger != null) {
            return Constant.mAppTopBaseManger;
        }
        Constant.mAppTopBaseManger = new AppTopBaseManger(context);
        return Constant.mAppTopBaseManger;
    }
}
