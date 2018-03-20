package com.umeng.analytics.a;

import com.umeng.analytics.f;
import com.umeng.common.Log;
import org.json.JSONObject;

/* compiled from: Session */
public class l extends n {
    public static final String f = "session_id";
    public String e;

    public void a(String str) {
        this.e = str;
    }

    public boolean a() {
        if (this.e != null) {
            return super.a();
        }
        Log.b(f.q, "Session id is not initialized");
        return false;
    }

    public void b(JSONObject jSONObject) throws Exception {
        super.b(jSONObject);
        jSONObject.put(f, this.e);
    }

    public void a(JSONObject jSONObject) throws Exception {
        super.a(jSONObject);
        this.e = jSONObject.getString(f);
    }
}
