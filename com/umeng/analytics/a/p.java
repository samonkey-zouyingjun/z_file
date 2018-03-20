package com.umeng.analytics.a;

import android.content.Context;
import android.content.SharedPreferences;
import com.umeng.analytics.f;
import com.umeng.analytics.i;
import com.umeng.common.Log;
import java.lang.reflect.Method;
import org.json.JSONObject;

/* compiled from: UTraffic */
public class p implements g {
    private static final String c = "uptr";
    private static final String d = "dntr";
    public long a = 0;
    public long b = 0;

    public void a(JSONObject jSONObject) throws Exception {
        if (jSONObject != null) {
            if (jSONObject.has(c)) {
                this.a = jSONObject.getLong(c);
            }
            if (jSONObject.has(d)) {
                this.b = jSONObject.getLong(d);
            }
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        if (this.a > 0) {
            jSONObject.put(c, this.a);
        }
        if (this.b > 0) {
            jSONObject.put(d, this.b);
        }
    }

    public boolean a() {
        if (this.a <= 0 || this.b <= 0) {
            return false;
        }
        return true;
    }

    public static p a(Context context) {
        try {
            p pVar = new p();
            long[] b = b(context);
            if (b[0] <= 0 || b[1] <= 0) {
                return null;
            }
            SharedPreferences e = i.e(context);
            long j = e.getLong(c, -1);
            long j2 = e.getLong(d, -1);
            e.edit().putLong(c, b[1]).putLong(d, b[0]).commit();
            if (j <= 0 || j2 <= 0) {
                return null;
            }
            b[0] = b[0] - j2;
            b[1] = b[1] - j;
            if (b[0] <= 0 || b[1] <= 0) {
                return null;
            }
            pVar.b = b[0];
            pVar.a = b[1];
            return pVar;
        } catch (Exception e2) {
            Log.e(f.q, "sdk less than 2.2 has get no traffic");
            return null;
        }
    }

    private static long[] b(Context context) throws Exception {
        Class cls = Class.forName("android.net.TrafficStats");
        Method method = cls.getMethod("getUidRxBytes", new Class[]{Integer.TYPE});
        Method method2 = cls.getMethod("getUidTxBytes", new Class[]{Integer.TYPE});
        if (context.getApplicationInfo().uid == -1) {
            return null;
        }
        r2 = new long[2];
        r2[0] = ((Long) method.invoke(null, new Object[]{Integer.valueOf(context.getApplicationInfo().uid)})).longValue();
        r2[1] = ((Long) method2.invoke(null, new Object[]{Integer.valueOf(r5)})).longValue();
        return r2;
    }
}
