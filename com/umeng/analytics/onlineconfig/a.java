package com.umeng.analytics.onlineconfig;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.umeng.analytics.f;
import com.umeng.analytics.i;
import com.umeng.common.Log;
import com.umeng.common.net.r;
import com.umeng.common.net.s;
import com.umeng.common.util.g;
import java.util.Iterator;
import org.json.JSONObject;

/* compiled from: OnlineConfigAgent */
public class a {
    private final String a = "last_config_time";
    private final String b = "report_policy";
    private final String c = "online_config";
    private String d = null;
    private String e = null;
    private UmengOnlineConfigureListener f = null;
    private c g = null;

    /* compiled from: OnlineConfigAgent */
    public class a extends s {
        final /* synthetic */ a a;
        private JSONObject e;

        public a(a aVar, JSONObject jSONObject) {
            this.a = aVar;
            super(null);
            this.e = jSONObject;
        }

        public JSONObject a() {
            return this.e;
        }

        public String b() {
            return this.d;
        }
    }

    /* compiled from: OnlineConfigAgent */
    public class b extends r implements Runnable {
        Context a;
        final /* synthetic */ a b;

        public b(a aVar, Context context) {
            this.b = aVar;
            this.a = context.getApplicationContext();
        }

        public void run() {
            try {
                b();
            } catch (Exception e) {
                this.b.a(null);
                Log.c(f.q, "reques update error", e);
            }
        }

        public boolean a() {
            return false;
        }

        private void b() {
            s aVar = new a(this.b, this.b.d(this.a));
            String[] strArr = f.s;
            b bVar = null;
            for (String a : strArr) {
                aVar.a(a);
                bVar = (b) a(aVar, b.class);
                if (bVar != null) {
                    break;
                }
            }
            if (bVar == null) {
                this.b.a(null);
                return;
            }
            Log.a(f.q, "response : " + bVar.b);
            if (bVar.b) {
                if (this.b.g != null) {
                    this.b.g.a(bVar.c, (long) bVar.d);
                }
                this.b.a(this.a, bVar);
                this.b.b(this.a, bVar);
                this.b.a(bVar.a);
                return;
            }
            this.b.a(null);
        }
    }

    public void a(Context context) {
        if (context == null) {
            try {
                Log.b(f.q, "unexpected null context in updateOnlineConfig");
                return;
            } catch (Exception e) {
                Log.b(f.q, "exception in updateOnlineConfig");
                return;
            }
        }
        new Thread(new b(this, context)).start();
    }

    public void a(Context context, String str, String str2) {
        this.d = str;
        this.e = str2;
        a(context);
    }

    public void a(UmengOnlineConfigureListener umengOnlineConfigureListener) {
        this.f = umengOnlineConfigureListener;
    }

    public void a() {
        this.f = null;
    }

    public void a(c cVar) {
        this.g = cVar;
    }

    public void b() {
        this.g = null;
    }

    private void a(JSONObject jSONObject) {
        if (this.f != null) {
            this.f.onDataReceived(jSONObject);
        }
    }

    private String b(Context context) throws Exception {
        String str = this.d;
        if (str == null) {
            str = com.umeng.common.b.q(context);
        }
        if (str != null) {
            return str;
        }
        throw new Exception("none appkey exception");
    }

    private String c(Context context) {
        return this.e == null ? com.umeng.common.b.u(context) : this.e;
    }

    private JSONObject d(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            String str = com.umeng.common.a.b;
            getClass();
            jSONObject.put(str, "online_config");
            jSONObject.put(com.umeng.common.a.g, b(context));
            jSONObject.put(com.umeng.common.a.f, com.umeng.common.b.d(context));
            jSONObject.put(com.umeng.common.a.c, com.umeng.common.b.v(context));
            jSONObject.put(com.umeng.common.a.h, "4.6.1");
            jSONObject.put(com.umeng.common.a.e, g.b(com.umeng.common.b.g(context)));
            jSONObject.put(com.umeng.common.a.d, c(context));
            jSONObject.put("report_policy", i.i(context)[0]);
            jSONObject.put("last_config_time", e(context));
            return jSONObject;
        } catch (Exception e) {
            Log.b(f.q, "exception in onlineConfigInternal");
            return null;
        }
    }

    private String e(Context context) {
        return i.b(context).getString(f.C, "");
    }

    private void a(Context context, b bVar) {
        Editor edit = i.b(context).edit();
        if (!TextUtils.isEmpty(bVar.e)) {
            edit.putString(f.C, bVar.e);
        }
        if (bVar.c != -1) {
            edit.putInt(f.A, bVar.c);
            edit.putLong(f.B, (long) bVar.d);
        }
        edit.commit();
    }

    private void b(Context context, b bVar) {
        if (bVar.a != null && bVar.a.length() != 0) {
            Editor edit = i.b(context).edit();
            try {
                JSONObject jSONObject = bVar.a;
                Iterator keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String str = (String) keys.next();
                    edit.putString(str, jSONObject.getString(str));
                }
                edit.commit();
                Log.a(f.q, "get online setting params: " + jSONObject);
            } catch (Exception e) {
                Log.c(f.q, "save online config params", e);
            }
        }
    }
}
