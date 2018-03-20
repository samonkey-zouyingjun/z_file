package com.umeng.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.umeng.analytics.onlineconfig.a;
import com.umeng.common.Log;
import com.umeng.common.b;
import java.util.HashMap;
import javax.microedition.khronos.opengles.GL10;

public class MobclickAgent {
    private static final b a = new b();
    private static final a b = new a();

    static {
        b.a(a);
    }

    public static void setAutoLocation(boolean z) {
        f.i = z;
    }

    public static void setWrapper(String str, String str2) {
        a.a(str, str2);
    }

    public static void setSessionContinueMillis(long j) {
        f.d = j;
    }

    public static void setEnableEventBuffer(boolean z) {
        f.m = z;
    }

    public static void setOnlineConfigureListener(UmengOnlineConfigureListener umengOnlineConfigureListener) {
        b.a(umengOnlineConfigureListener);
    }

    static b a() {
        return a;
    }

    public static void setOpenGLContext(GL10 gl10) {
        if (gl10 != null) {
            String[] a = b.a(gl10);
            if (a.length == 2) {
                a.a = a[0];
                a.b = a[1];
            }
        }
    }

    public static void openActivityDurationTrack(boolean z) {
        f.j = z;
    }

    public static void setDebugMode(boolean z) {
        Log.LOG = z;
    }

    public static void setDefaultReportPolicy(Context context, int i) {
        Log.e(f.q, "此方法不再使用，请使用在线参数配置，发送策略");
    }

    public static void onPause(Context context) {
        a.a(context);
    }

    public static void onResume(Context context) {
        a.b(context);
    }

    public static void onResume(Context context, String str, String str2) {
        if (str == null || str.length() == 0) {
            Log.b(f.q, "unexpected empty appkey in onResume");
            return;
        }
        a.k = str;
        a.j = str2;
        a.b(context);
    }

    public static void onError(Context context) {
    }

    public static void onError(Context context, String str) {
        if (str == null || str.length() == 0) {
            Log.b(f.q, "unexpected empty appkey in onError");
            return;
        }
        a.k = str;
        onError(context);
    }

    public static void reportError(Context context, String str) {
        a.a(context, str);
    }

    public static void reportError(Context context, Throwable th) {
        a.a(context, th);
    }

    public static void flush(Context context) {
        a.c(context);
    }

    public static void onEvent(Context context, String str) {
        a.a(context, str, null, -1, 1);
    }

    public static void onEvent(Context context, String str, int i) {
        a.a(context, str, null, -1, i);
    }

    public static void onEvent(Context context, String str, String str2, int i) {
        if (TextUtils.isEmpty(str2)) {
            Log.a(f.q, "label is null or empty");
        } else {
            a.a(context, str, str2, -1, i);
        }
    }

    public static void onEvent(Context context, String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            Log.a(f.q, "label is null or empty");
        } else {
            a.a(context, str, str2, -1, 1);
        }
    }

    public static void onEvent(Context context, String str, HashMap<String, String> hashMap) {
        a.a(context, str, (HashMap) hashMap, -1);
    }

    public static void onEventDuration(Context context, String str, long j) {
        if (j <= 0) {
            Log.a(f.q, "duration is not valid in onEventDuration");
        } else {
            a.a(context, str, null, j, 1);
        }
    }

    public static void onEventDuration(Context context, String str, String str2, long j) {
        if (TextUtils.isEmpty(str2)) {
            Log.a(f.q, "label is null or empty");
        } else if (j <= 0) {
            Log.a(f.q, "duration is not valid in onEventDuration");
        } else {
            a.a(context, str, str2, j, 1);
        }
    }

    public static void onEventDuration(Context context, String str, HashMap<String, String> hashMap, long j) {
        if (j <= 0) {
            Log.a(f.q, "duration is not valid in onEventDuration");
        } else {
            a.a(context, str, (HashMap) hashMap, j);
        }
    }

    public static void onEventBegin(Context context, String str) {
        a.b(context, str);
    }

    public static void onEventEnd(Context context, String str) {
        a.c(context, str);
    }

    public static void onEventBegin(Context context, String str, String str2) {
        a.a(context, str, str2);
    }

    public static void onEventEnd(Context context, String str, String str2) {
        a.b(context, str, str2);
    }

    public static void onKVEventBegin(Context context, String str, HashMap<String, String> hashMap, String str2) {
        a.a(context, str, (HashMap) hashMap, str2);
    }

    public static void onKVEventEnd(Context context, String str, String str2) {
        a.c(context, str, str2);
    }

    public static String getConfigParams(Context context, String str) {
        return i.b(context).getString(str, "");
    }

    public static void updateOnlineConfig(Context context, String str, String str2) {
        if (str == null || str.length() == 0) {
            Log.b(f.q, "unexpected empty appkey in onResume");
        } else {
            b.a(context, str, str2);
        }
    }

    public static void updateOnlineConfig(Context context) {
        b.a(context);
    }

    public void setGender(Context context, Gender gender) {
        int i = 0;
        SharedPreferences a = i.a(context);
        switch (gender) {
            case Male:
                i = 1;
                break;
            case Female:
                i = 2;
                break;
        }
        a.edit().putInt("gender", i).commit();
    }

    public void setAge(Context context, int i) {
        SharedPreferences a = i.a(context);
        if (i < 0 || i > 200) {
            Log.a(f.q, "not a valid age!");
        } else {
            a.edit().putInt("age", i).commit();
        }
    }

    public void setUserID(Context context, String str, String str2) {
        SharedPreferences a = i.a(context);
        if (TextUtils.isEmpty(str)) {
            Log.a(f.q, "userID is null or empty");
            return;
        }
        a.edit().putString("user_id", str).commit();
        if (TextUtils.isEmpty(str2)) {
            Log.a(f.q, "id source is null or empty");
        } else {
            a.edit().putString("id_source", str2).commit();
        }
    }

    public static void onKillProcess(Context context) {
        a.d(context);
    }
}
