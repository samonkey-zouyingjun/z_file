package com.umeng.analytics.a;

import com.umeng.analytics.f;
import com.umeng.common.Log;
import com.umeng.common.util.g;
import com.zidoo.custom.app.AppTopBaseManger;
import org.json.JSONObject;

/* compiled from: Time */
public class n {
    public String g;
    public String h;
    protected final String i = "date";
    protected final String j = AppTopBaseManger.KEY_TIME;

    public n() {
        String a = g.a();
        this.g = a.split(" ")[0];
        this.h = a.split(" ")[1];
    }

    public boolean a() {
        if (this.g != null && this.h != null) {
            return true;
        }
        Log.b(f.q, "Date or Time is not initialized");
        return false;
    }

    public void b(JSONObject jSONObject) throws Exception {
        jSONObject.put("date", this.g);
        jSONObject.put(AppTopBaseManger.KEY_TIME, this.h);
    }

    public void a(JSONObject jSONObject) throws Exception {
        this.g = jSONObject.getString("date");
        this.h = jSONObject.getString(AppTopBaseManger.KEY_TIME);
    }
}
