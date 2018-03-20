package com.umeng.analytics.a;

import com.umeng.analytics.f;
import com.umeng.common.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: EKV */
public class a implements g {
    public final HashMap<String, String> a;
    public String b;
    public long c;
    public long d;
    private final String e;
    private final String f;
    private final String g;

    public a() {
        this.c = 0;
        this.d = 0;
        this.e = "id";
        this.f = "ts";
        this.g = "du";
        this.a = new HashMap();
    }

    public a(String str, HashMap<String, String> hashMap, long j) {
        this.c = 0;
        this.d = 0;
        this.e = "id";
        this.f = "ts";
        this.g = "du";
        this.b = str;
        this.a = a((HashMap) hashMap);
        this.d = j;
        this.c = b();
    }

    private HashMap<String, String> a(HashMap<String, String> hashMap) {
        int i = 0;
        if (hashMap.size() > 10) {
            int size = hashMap.size() - 10;
            String[] strArr = new String[size];
            int i2 = 0;
            for (String str : hashMap.keySet()) {
                if (i2 >= size) {
                    break;
                }
                strArr[i2] = str;
                i2++;
            }
            int length = strArr.length;
            while (i < length) {
                hashMap.remove(strArr[i]);
                i++;
            }
        }
        return hashMap;
    }

    private long b() {
        return System.currentTimeMillis() / 1000;
    }

    private void c(JSONObject jSONObject) throws JSONException {
        for (Entry entry : this.a.entrySet()) {
            jSONObject.put((String) entry.getKey(), entry.getValue());
        }
    }

    private void d(JSONObject jSONObject) throws Exception {
        jSONObject.remove("id");
        jSONObject.remove("ts");
        jSONObject.remove("du");
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            String str = (String) keys.next();
            this.a.put(str, jSONObject.getString(str));
        }
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                this.b = jSONObject.getString("id");
                this.c = jSONObject.getLong("ts");
                if (jSONObject.has("du")) {
                    this.d = jSONObject.getLong("du");
                }
                d(jSONObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void b(JSONObject jSONObject) throws JSONException {
        try {
            jSONObject.put("id", this.b);
            jSONObject.put("ts", this.c);
            if (this.d > 0) {
                jSONObject.put("du", this.d);
            }
            c(jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean a() {
        if (this.b == null || this.c <= 0) {
            Log.b(f.q, "mId or mTs is not initialized");
            return false;
        } else if (!this.a.isEmpty()) {
            return true;
        } else {
            Log.b(f.q, "mCustomKV is not initialized");
            return false;
        }
    }
}
