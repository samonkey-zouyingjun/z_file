package pers.lic.tool;

import android.util.Log;

public class Lig {
    private static final String TAG = "lisupan";
    private static boolean sDebug = true;

    public static void d(String method, Object... messages) {
        if (sDebug) {
            Log.d(TAG, getMessage(method, messages));
        }
    }

    public static void w(String method, Object... messages) {
        if (sDebug) {
            Log.w(TAG, getMessage(method, messages));
        }
    }

    public static void e(String method, Throwable e, Object... messages) {
        if (sDebug) {
            Log.e(TAG, getMessage(method, messages), e);
        }
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    private static String getMessage(String method, Object... messages) {
        if (messages == null || messages.length == 0) {
            return method;
        }
        if (messages.length == 1) {
            return method + " >>  " + messages[0];
        }
        StringBuilder message = new StringBuilder(method + " >> [ " + messages[0]);
        for (int i = 1; i < messages.length; i++) {
            message.append(" , ").append(messages[i]);
        }
        message.append(" ]");
        return message.toString();
    }
}
