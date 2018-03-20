package com.zidoo.custom.lock;

import android.content.Context;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.app.ZidooStartAppInfo;
import com.zidoo.custom.db.ZidooSQliteManger;
import com.zidoo.custom.share.ZidooSharedPrefsUtil;

public class LockManager {
    public static String DEFAULTPASSWORD = "17777777";

    public interface LockListener {
        void isLock(boolean z);
    }

    public static boolean openApp(Context context, ZidooStartAppInfo startAppInfo, LockListener lockListener) {
        if (startAppInfo == null) {
            return false;
        }
        if (!isLockApp(context, startAppInfo.getPckName())) {
            if (lockListener != null) {
                lockListener.isLock(false);
            }
            return ZidooAppTool.openApp(context, startAppInfo);
        } else if (lockListener == null) {
            return false;
        } else {
            lockListener.isLock(true);
            return false;
        }
    }

    public static void setLockApp(Context context, String pName, boolean isLock) {
        ZidooSQliteManger.getLockBaseManger(context).setLock(pName, isLock);
    }

    public static void setLockApp(Context context, String pName) {
        ZidooSQliteManger.getLockBaseManger(context).setLock(pName, !isLockApp(context, pName));
    }

    public static boolean isLockApp(Context context, String pName) {
        if (pName == null) {
            return false;
        }
        return ZidooSQliteManger.getLockBaseManger(context).isLock(pName);
    }

    public static boolean setLockPassWord(Context context, String passWord) {
        if (passWord == null || passWord.trim().equals("")) {
            return false;
        }
        ZidooSharedPrefsUtil.putValue(context, "lockpassWord", passWord);
        return true;
    }

    public static boolean isFirstSetPassWrod(Context context) {
        return ZidooSharedPrefsUtil.getValue(context, "lockpassWord", null) == null;
    }

    public static boolean isLockPassWord(Context context, String passWord) {
        if (passWord == null || passWord.trim().equals("")) {
            return false;
        }
        String currentpass = ZidooSharedPrefsUtil.getValue(context, "lockpassWord", null);
        if (currentpass != null && passWord.equals(currentpass)) {
            return true;
        }
        if (DEFAULTPASSWORD == null || !passWord.equals(DEFAULTPASSWORD)) {
            return false;
        }
        return true;
    }

    public static void setDefaultPassWord(String passWord) {
        DEFAULTPASSWORD = passWord;
    }
}
