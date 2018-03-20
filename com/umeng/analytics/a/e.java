package com.umeng.analytics.a;

import com.umeng.analytics.f;
import com.umeng.common.Log;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import org.json.JSONObject;

/* compiled from: Event */
public class e extends l implements g {
    public String a;
    public String b;
    public int c;
    public long d;
    private final String k = FavoriteDatabase.TAG;
    private final String l = "label";
    private final String m = "acc";
    private final String n = "du";

    public e(String str, String str2, String str3, int i, long j) {
        this.e = str;
        this.a = str2;
        this.b = str3;
        this.c = i;
        this.d = j;
    }

    public boolean a() {
        if (this.a == null) {
            Log.b(f.q, "mTag is not initilized");
            return false;
        } else if (this.c > 0 && this.c <= 10000) {
            return super.a();
        } else {
            Log.b(f.q, "mAcc is invalid : " + this.c);
            return false;
        }
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                super.a(jSONObject);
                this.a = jSONObject.getString(FavoriteDatabase.TAG);
                if (jSONObject.has("label")) {
                    this.b = jSONObject.getString("label");
                }
                this.c = jSONObject.getInt("acc");
                if (jSONObject.has("du")) {
                    this.d = jSONObject.getLong("du");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        jSONObject.put(FavoriteDatabase.TAG, this.a);
        jSONObject.put("acc", this.c);
        if (this.b != null) {
            jSONObject.put("label", this.b);
        }
        if (this.d > 0) {
            jSONObject.put("du", this.d);
        }
        super.b(jSONObject);
    }
}
