package com.umeng.analytics.a;

import org.json.JSONArray;

/* compiled from: Page */
public class k {
    public String a;
    public int b;

    public k(JSONArray jSONArray) throws Exception {
        this.a = jSONArray.getString(0);
        this.b = jSONArray.getInt(1);
    }
}
