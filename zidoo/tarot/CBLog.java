package zidoo.tarot;

import android.util.Log;
import zidoo.tarot.Config.DebugConfig;

public class CBLog {
    public static final void w(String TAG, String msg) {
        if (DebugConfig.sDebug) {
            Log.w(TAG, msg);
        }
    }

    public static final void e(String TAG, String msg) {
        if (DebugConfig.sDebug) {
            Log.e(TAG, msg);
        }
    }

    public static final void i(String TAG, String msg) {
        if (DebugConfig.sDebug) {
            Log.i(TAG, msg);
        }
    }

    public static final void d(String TAG, String msg) {
        if (DebugConfig.sDebug) {
            Log.d(TAG, msg);
        }
    }

    public static final void v(String TAG, String msg) {
        if (DebugConfig.sDebug) {
            Log.v(TAG, msg);
        }
    }
}
