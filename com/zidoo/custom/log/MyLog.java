package com.zidoo.custom.log;

import android.util.Log;

public class MyLog {
    private static boolean ISDEBUGLOG = true;
    private static boolean ISFORCIBLYTAG = true;
    private static String TAG = "bob";

    public static void setDebugLog(boolean isDebuglog) {
        ISDEBUGLOG = isDebuglog;
    }

    public static void setForciblytag(boolean isForciblytag) {
        ISFORCIBLYTAG = isForciblytag;
    }

    public static void setTAG(String tag) {
        TAG = tag;
    }

    public static void v(String tag, String msg) {
        if (!ISDEBUGLOG) {
            return;
        }
        if (ISFORCIBLYTAG) {
            Log.v(TAG, msg);
        } else {
            Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (!ISDEBUGLOG) {
            return;
        }
        if (ISFORCIBLYTAG) {
            Log.e(TAG, msg);
        } else {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void fv(String tag, String msg) {
        if (ISFORCIBLYTAG) {
            Log.v(TAG, msg);
        } else {
            Log.v(tag, msg);
        }
    }

    public static void fv(String msg) {
        fv(TAG, msg);
    }

    public static void fe(String tag, String msg) {
        if (ISFORCIBLYTAG) {
            Log.e(TAG, msg);
        } else {
            Log.e(tag, msg);
        }
    }

    public static void fe(String msg) {
        fe(TAG, msg);
    }
}
