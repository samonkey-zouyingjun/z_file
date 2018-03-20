package com.umeng.common.net;

import org.json.JSONObject;
import zidoo.http.HTTP;

/* compiled from: URequest */
public abstract class s {
    protected static String b = HTTP.POST;
    protected static String c = HTTP.GET;
    protected String d;

    public abstract JSONObject a();

    public abstract String b();

    protected String c() {
        return b;
    }

    public s(String str) {
        this.d = str;
    }

    public void a(String str) {
        this.d = str;
    }

    public String d() {
        return this.d;
    }
}
