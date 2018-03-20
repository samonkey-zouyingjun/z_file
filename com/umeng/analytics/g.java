package com.umeng.analytics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.umeng.common.Log;

/* compiled from: UmengNetworkHelper */
class g {
    private static final String a = g.class.getName();

    g() {
    }

    public static String a(Context context) {
        if (context.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
            return null;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return null;
            }
            if (activeNetworkInfo.getType() == 1) {
                return null;
            }
            String extraInfo = activeNetworkInfo.getExtraInfo();
            Log.a(a, "net type:" + extraInfo);
            if (extraInfo == null) {
                return null;
            }
            if (extraInfo.equals("cmwap") || extraInfo.equals("3gwap") || extraInfo.equals("uniwap")) {
                return "10.0.0.172";
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
