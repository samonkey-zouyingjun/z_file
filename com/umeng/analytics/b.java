package com.umeng.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.umeng.analytics.a.d;
import com.umeng.analytics.a.h;
import com.umeng.analytics.a.l;
import com.umeng.analytics.a.m;
import com.umeng.analytics.a.o;
import com.umeng.common.Log;
import com.umeng.common.util.g;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/* compiled from: InternalAgent */
class b extends d {
    String a = "";
    String b = "";
    private String l;
    private String m;
    private final int n = 0;
    private final int o = 1;
    private final String p = "start_millis";
    private final String q = "end_millis";
    private final String r = "last_fetch_location_time";
    private final long s = 10000;
    private final int t = 128;
    private final int u = 256;

    /* compiled from: InternalAgent */
    private final class a extends Thread {
        final /* synthetic */ b a;
        private final Object b = new Object();
        private Context c;
        private int d;

        a(b bVar, Context context, int i) {
            this.a = bVar;
            this.c = context.getApplicationContext();
            this.d = i;
        }

        public void run() {
            try {
                synchronized (this.b) {
                    switch (this.d) {
                        case 0:
                            this.a.j(this.c);
                            break;
                        case 1:
                            this.a.i(this.c);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in invokehander.", e);
            }
        }
    }

    b() {
    }

    void a() {
    }

    void b() {
    }

    void a(String str, String str2) {
        this.d.E = str;
        this.d.F = str2;
    }

    void a(Context context) {
        if (context == null) {
            try {
                Log.b(f.q, "unexpected null context in onPause");
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in Mobclick.onRause(). ", e);
            }
        } else if (context.getClass().getName().equals(this.l)) {
            new a(this, context, 0).start();
        } else {
            Log.b(f.q, "onPause() called without context from corresponding onResume()");
        }
    }

    void a(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            if (context == null) {
                Log.b(f.q, "unexpected null context in reportError");
                return;
            }
            this.c.a(new d(str));
            e(context);
        }
    }

    void a(Context context, Throwable th) {
        if (context != null && th != null) {
            this.c.a(new d(th));
            e(context);
        }
    }

    private void h(Context context) {
        if (context == null) {
            Log.b(f.q, "unexpected null context in onResume");
        } else {
            this.l = context.getClass().getName();
        }
    }

    void b(Context context) {
        try {
            h(context);
            getClass();
            new a(this, context, 1).start();
        } catch (Exception e) {
            Log.b(f.q, "Exception occurred in Mobclick.onResume(). ", e);
        }
    }

    void c(Context context) {
        try {
            getClass();
            a(context, 2);
        } catch (Exception e) {
            Log.b(f.q, "Exception occurred in Mobclick.flush(). ", e);
        }
    }

    void a(Context context, String str, String str2, long j, int i) {
        if (context != null) {
            try {
                if (a(str, 128) && i > 0) {
                    if (this.m == null) {
                        Log.e(f.q, "can't call onEvent before session is initialized");
                        return;
                    }
                    if (str2 != null) {
                        if (!a(str2, 256)) {
                            Log.b(f.q, "invalid label in onEvent");
                            return;
                        }
                    }
                    this.c.a(this.m, str, str2, j, i);
                    e(context);
                    return;
                }
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in Mobclick.onEvent(). ", e);
                return;
            }
        }
        Log.b(f.q, "invalid params in onEvent");
    }

    void a(Context context, String str, HashMap<String, String> hashMap, long j) {
        if (context != null) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    if (!a((Map) hashMap)) {
                        return;
                    }
                    if (this.m == null) {
                        Log.e(f.q, "can't call onEvent before session is initialized");
                        return;
                    }
                    this.c.a(this.m, str, hashMap, j);
                    e(context);
                    return;
                }
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in Mobclick.onEvent(). ", e);
                return;
            }
        }
        Log.b(f.q, "invalid params in onKVEventEnd");
    }

    private synchronized void i(Context context) {
        SharedPreferences e = i.e(context);
        if (e != null) {
            if (a(e)) {
                this.m = b(context, e);
                Log.a(f.q, "Start new session: " + this.m);
            } else {
                this.m = c(context, e);
                Log.a(f.q, "Extend current session: " + this.m);
            }
        }
    }

    private synchronized void j(Context context) {
        SharedPreferences e = i.e(context);
        if (e != null) {
            long j = e.getLong("start_millis", -1);
            if (j == -1) {
                Log.b(f.q, "onEndSession called before onStartSession");
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                Editor a = m.a(e, this.l, j, currentTimeMillis);
                a.putLong("start_millis", -1);
                a.putLong("end_millis", currentTimeMillis);
                a.commit();
            }
            a(context, e);
            a(context, 5);
        }
    }

    private void a(Context context, SharedPreferences sharedPreferences) {
        long currentTimeMillis = System.currentTimeMillis();
        if (f.i && currentTimeMillis - sharedPreferences.getLong("last_fetch_location_time", 0) >= 10000) {
            Editor a = o.a(context, sharedPreferences);
            if (a != null) {
                a.putLong("last_fetch_location_time", currentTimeMillis);
                a.commit();
            }
        }
    }

    private boolean a(SharedPreferences sharedPreferences) {
        if (System.currentTimeMillis() - sharedPreferences.getLong("end_millis", -1) > f.d) {
            return true;
        }
        return false;
    }

    private String b(Context context, SharedPreferences sharedPreferences) {
        long currentTimeMillis = System.currentTimeMillis();
        String a = a(context, currentTimeMillis);
        h hVar = new h(context, a);
        m a2 = m.a(context);
        this.c.a(hVar);
        this.c.a(a2);
        Editor edit = sharedPreferences.edit();
        edit.putString(l.f, a);
        edit.putLong("start_millis", currentTimeMillis);
        edit.putLong("end_millis", -1);
        edit.commit();
        getClass();
        a(context, 4);
        return a;
    }

    private String a(Context context, long j) {
        String q = this.k == null ? com.umeng.common.b.q(context) : this.k;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j).append(q).append(g.b(com.umeng.common.b.g(context)));
        return g.a(stringBuilder.toString());
    }

    private String c(Context context, SharedPreferences sharedPreferences) {
        Long valueOf = Long.valueOf(System.currentTimeMillis());
        Editor edit = sharedPreferences.edit();
        edit.putLong("start_millis", valueOf.longValue());
        edit.putLong("end_millis", -1);
        edit.commit();
        return sharedPreferences.getString(l.f, null);
    }

    private void d(Context context, String str) {
        try {
            if (f.m) {
                this.c.a(str);
                return;
            }
            j a = j.a(context, str);
            a.a(Long.valueOf(System.currentTimeMillis()));
            a.a(context);
        } catch (Exception e) {
            Log.a(f.q, "exception in save event begin info");
        }
    }

    private int e(Context context, String str) {
        try {
            long b;
            if (f.m) {
                b = this.c.b(str);
            } else {
                b = j.a(context, str).a().longValue();
            }
            if (b > 0) {
                return (int) (System.currentTimeMillis() - b);
            }
            return -1;
        } catch (Exception e) {
            Log.a(f.q, "exception in get event duration", e);
            return -1;
        }
    }

    void b(Context context, String str) {
        if (context == null || !a(str, 128)) {
            Log.b(f.q, "invalid params in onEventBegin");
        } else {
            d(context, "_t" + str);
        }
    }

    void c(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            Log.a(f.q, "input Context is null or event_id is empty");
            return;
        }
        int e = e(context, "_t" + str);
        if (e < 0) {
            Log.b(f.q, "event duration less than 0 in onEventEnd");
            return;
        }
        a(context, str, null, (long) e, 1);
    }

    void a(Context context, String str, String str2) {
        if (context != null && a(str, 128) && a(str2, 256)) {
            d(context, "_tl" + str + str2);
        } else {
            Log.b(f.q, "invalid params in onEventBegin");
        }
    }

    void b(Context context, String str, String str2) {
        if (context == null || TextUtils.isEmpty(str2)) {
            Log.b(f.q, "invalid params in onEventEnd");
            return;
        }
        int e = e(context, "_tl" + str + str2);
        if (e < 0) {
            Log.b(f.q, "event duration less than 0 in onEvnetEnd");
            return;
        }
        a(context, str, str2, (long) e, 1);
    }

    void a(Context context, String str, HashMap<String, String> hashMap, String str2) {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            Log.b(f.q, "invalid params in onKVEventBegin");
        } else if (a((Map) hashMap)) {
            try {
                String str3 = str + str2;
                this.c.a(str3, hashMap);
                this.c.a(str3);
            } catch (Exception e) {
                Log.e(f.q, "exception in save k-v event begin inof", e);
            }
        }
    }

    void c(Context context, String str, String str2) {
        if (context == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            Log.b(f.q, "invalid params in onKVEventEnd");
            return;
        }
        String str3 = str + str2;
        int e = e(context, str3);
        if (e < 0) {
            Log.b(f.q, "event duration less than 0 in onEvnetEnd");
            return;
        }
        a(context, str, this.c.c(str3), (long) e);
    }

    boolean a(String str, int i) {
        if (str == null) {
            return false;
        }
        int length = str.getBytes().length;
        if (length == 0 || length > i) {
            return false;
        }
        return true;
    }

    boolean a(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            Log.b(f.q, "map is null or empty in onEvent");
            return false;
        }
        for (Entry entry : map.entrySet()) {
            if (a((String) entry.getKey(), 128)) {
                if (!a((String) entry.getValue(), 256)) {
                }
            }
            Log.b(f.q, String.format("invalid key-<%s> or value-<%s> ", new Object[]{entry.getKey(), entry.getValue()}));
            return false;
        }
        return true;
    }

    void d(Context context) {
        try {
            j(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void b(Context context, Throwable th) {
        try {
            this.c.a(new d(th));
            j(context);
        } catch (Exception e) {
            Log.a(f.q, "Exception in onAppCrash", e);
        }
    }
}
