package com.umeng.analytics.a;

import android.content.Context;
import com.umeng.analytics.f;
import com.umeng.common.Log;
import org.json.JSONObject;

/* compiled from: Launch */
public class h extends l implements g {
    o a;

    public h(Context context, String str) {
        this.e = str;
        this.a = o.a(context);
    }

    public void a(o oVar) {
        this.a = oVar;
    }

    public void c(JSONObject jSONObject) throws Exception {
        if (this.a != null) {
            this.a.b(jSONObject);
        }
    }

    public void d(JSONObject jSONObject) throws Exception {
        o oVar = new o();
        oVar.a(jSONObject);
        if (oVar.a()) {
            this.a = oVar;
        }
    }

    public boolean a() {
        if (this.a == null && f.i) {
            Log.c(f.q, "missing location info in Launch");
        }
        return super.a();
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                super.a(jSONObject);
                d(jSONObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        c(jSONObject);
        super.b(jSONObject);
    }

    public JSONObject b() {
        JSONObject jSONObject;
        Exception e;
        try {
            jSONObject = new JSONObject();
            try {
                b(jSONObject);
            } catch (Exception e2) {
                e = e2;
                e.printStackTrace();
                return jSONObject;
            }
        } catch (Exception e3) {
            Exception exception = e3;
            jSONObject = null;
            e = exception;
            e.printStackTrace();
            return jSONObject;
        }
        return jSONObject;
    }
}
