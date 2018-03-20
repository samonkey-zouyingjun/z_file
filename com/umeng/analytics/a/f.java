package com.umeng.analytics.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import com.umeng.analytics.i;
import com.umeng.common.a;
import com.umeng.common.b;
import org.json.JSONObject;

/* compiled from: Header */
public class f implements g {
    public String A;
    public String B;
    public String C;
    public String D;
    public String E;
    public String F;
    private final String G = a.g;
    private final String H = a.d;
    private final String I = "device_id";
    private final String J = a.e;
    private final String K = "mc";
    private final String L = com.umeng.analytics.f.H;
    private final String M = "device_model";
    private final String N = "os";
    private final String O = "os_version";
    private final String P = "resolution";
    private final String Q = "cpu";
    private final String R = "gpu_vender";
    private final String S = "gpu_renderer";
    private final String T = "device_board";
    private final String U = "device_brand";
    private final String V = "device_manutime";
    private final String W = "device_manufacturer";
    private final String X = "device_manuid";
    private final String Y = "device_name";
    private final String Z = "app_version";
    public String a;
    private final String aa = a.f;
    private final String ab = "package_name";
    private final String ac = "sdk_type";
    private final String ad = a.h;
    private final String ae = "timezone";
    private final String af = "country";
    private final String ag = "language";
    private final String ah = "access";
    private final String ai = "access_subtype";
    private final String aj = "carrier";
    private final String ak = "wrapper_type";
    private final String al = "wrapper_version";
    public String b;
    public String c;
    public String d;
    public String e;
    public long f;
    public String g;
    public String h;
    public String i;
    public String j;
    public String k;
    public String l;
    public String m;
    public String n;
    public String o;
    public long p;
    public String q;
    public String r;
    public String s;
    public String t;
    public String u;
    public String v;
    public String w;
    public String x;
    public int y;
    public String z;

    public f(String str, String str2) {
        this.a = str;
        this.b = str2;
    }

    private void c(JSONObject jSONObject) throws Exception {
        this.a = jSONObject.getString(a.g);
        this.c = jSONObject.getString("device_id");
        this.d = jSONObject.getString(a.e);
        if (jSONObject.has("mc")) {
            this.e = jSONObject.getString("mc");
        }
        if (jSONObject.has(a.d)) {
            this.b = jSONObject.getString(a.d);
        }
        if (jSONObject.has(com.umeng.analytics.f.H)) {
            this.f = jSONObject.getLong(com.umeng.analytics.f.H);
        }
    }

    private void d(JSONObject jSONObject) throws Exception {
        String string;
        String str = null;
        this.g = jSONObject.has("device_model") ? jSONObject.getString("device_model") : null;
        if (jSONObject.has("os")) {
            string = jSONObject.getString("os");
        } else {
            string = null;
        }
        this.h = string;
        if (jSONObject.has("os_version")) {
            string = jSONObject.getString("os_version");
        } else {
            string = null;
        }
        this.i = string;
        if (jSONObject.has("resolution")) {
            string = jSONObject.getString("resolution");
        } else {
            string = null;
        }
        this.j = string;
        if (jSONObject.has("cpu")) {
            string = jSONObject.getString("cpu");
        } else {
            string = null;
        }
        this.k = string;
        if (jSONObject.has("gpu_vender")) {
            string = jSONObject.getString("gpu_vender");
        } else {
            string = null;
        }
        this.l = string;
        if (jSONObject.has("gpu_renderer")) {
            string = jSONObject.getString("gpu_renderer");
        } else {
            string = null;
        }
        this.m = string;
        if (jSONObject.has("device_board")) {
            string = jSONObject.getString("device_board");
        } else {
            string = null;
        }
        this.n = string;
        if (jSONObject.has("device_brand")) {
            string = jSONObject.getString("device_brand");
        } else {
            string = null;
        }
        this.o = string;
        this.p = jSONObject.has("device_manutime") ? jSONObject.getLong("device_manutime") : 0;
        if (jSONObject.has("device_manufacturer")) {
            string = jSONObject.getString("device_manufacturer");
        } else {
            string = null;
        }
        this.q = string;
        if (jSONObject.has("device_manuid")) {
            string = jSONObject.getString("device_manuid");
        } else {
            string = null;
        }
        this.r = string;
        if (jSONObject.has("device_name")) {
            str = jSONObject.getString("device_name");
        }
        this.s = str;
    }

    private void e(JSONObject jSONObject) throws Exception {
        String string;
        String str = null;
        if (jSONObject.has("app_version")) {
            string = jSONObject.getString("app_version");
        } else {
            string = null;
        }
        this.t = string;
        if (jSONObject.has(a.f)) {
            string = jSONObject.getString(a.f);
        } else {
            string = null;
        }
        this.u = string;
        if (jSONObject.has("package_name")) {
            str = jSONObject.getString("package_name");
        }
        this.v = str;
    }

    private void f(JSONObject jSONObject) throws Exception {
        this.w = jSONObject.getString("sdk_type");
        this.x = jSONObject.getString(a.h);
    }

    private void g(JSONObject jSONObject) throws Exception {
        String string;
        String str = null;
        this.y = jSONObject.has("timezone") ? jSONObject.getInt("timezone") : 8;
        if (jSONObject.has("country")) {
            string = jSONObject.getString("country");
        } else {
            string = null;
        }
        this.z = string;
        if (jSONObject.has("language")) {
            str = jSONObject.getString("language");
        }
        this.A = str;
    }

    private void h(JSONObject jSONObject) throws Exception {
        String string;
        String str = null;
        if (jSONObject.has("access")) {
            string = jSONObject.getString("access");
        } else {
            string = null;
        }
        this.B = string;
        if (jSONObject.has("access_subtype")) {
            string = jSONObject.getString("access_subtype");
        } else {
            string = null;
        }
        this.C = string;
        if (jSONObject.has("carrier")) {
            str = jSONObject.getString("carrier");
        }
        this.D = str;
    }

    private void i(JSONObject jSONObject) throws Exception {
        String string;
        String str = null;
        if (jSONObject.has("wrapper_type")) {
            string = jSONObject.getString("wrapper_type");
        } else {
            string = null;
        }
        this.E = string;
        if (jSONObject.has("wrapper_version")) {
            str = jSONObject.getString("wrapper_version");
        }
        this.F = str;
    }

    public void a(JSONObject jSONObject) throws Exception {
        if (jSONObject != null) {
            c(jSONObject);
            d(jSONObject);
            e(jSONObject);
            f(jSONObject);
            g(jSONObject);
            h(jSONObject);
            i(jSONObject);
        }
    }

    private void j(JSONObject jSONObject) throws Exception {
        jSONObject.put(a.g, this.a);
        jSONObject.put("device_id", this.c);
        jSONObject.put(a.e, this.d);
        if (this.b != null) {
            jSONObject.put(a.d, this.b);
        }
        if (this.e != null) {
            jSONObject.put("mc", this.e);
        }
        if (this.f > 0) {
            jSONObject.put(com.umeng.analytics.f.H, this.f);
        }
    }

    private void k(JSONObject jSONObject) throws Exception {
        if (this.g != null) {
            jSONObject.put("device_model", this.g);
        }
        if (this.h != null) {
            jSONObject.put("os", this.h);
        }
        if (this.i != null) {
            jSONObject.put("os_version", this.i);
        }
        if (this.j != null) {
            jSONObject.put("resolution", this.j);
        }
        if (this.k != null) {
            jSONObject.put("cpu", this.k);
        }
        if (this.l != null) {
            jSONObject.put("gpu_vender", this.l);
        }
        if (this.m != null) {
            jSONObject.put("gpu_vender", this.m);
        }
        if (this.n != null) {
            jSONObject.put("device_board", this.n);
        }
        if (this.o != null) {
            jSONObject.put("device_brand", this.o);
        }
        if (this.p > 0) {
            jSONObject.put("device_manutime", this.p);
        }
        if (this.q != null) {
            jSONObject.put("device_manufacturer", this.q);
        }
        if (this.r != null) {
            jSONObject.put("device_manuid", this.r);
        }
        if (this.s != null) {
            jSONObject.put("device_name", this.s);
        }
    }

    private void l(JSONObject jSONObject) throws Exception {
        if (this.t != null) {
            jSONObject.put("app_version", this.t);
        }
        if (this.u != null) {
            jSONObject.put(a.f, this.u);
        }
        if (this.v != null) {
            jSONObject.put("package_name", this.v);
        }
    }

    private void m(JSONObject jSONObject) throws Exception {
        jSONObject.put("sdk_type", this.w);
        jSONObject.put(a.h, this.x);
    }

    private void n(JSONObject jSONObject) throws Exception {
        jSONObject.put("timezone", this.y);
        if (this.z != null) {
            jSONObject.put("country", this.z);
        }
        if (this.A != null) {
            jSONObject.put("language", this.A);
        }
    }

    private void o(JSONObject jSONObject) throws Exception {
        if (this.B != null) {
            jSONObject.put("access", this.B);
        }
        if (this.C != null) {
            jSONObject.put("access_subtype", this.C);
        }
        if (this.D != null) {
            jSONObject.put("carrier", this.D);
        }
    }

    private void p(JSONObject jSONObject) throws Exception {
        if (this.E != null) {
            jSONObject.put("wrapper_type", this.E);
        }
        if (this.F != null) {
            jSONObject.put("wrapper_version", this.F);
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        j(jSONObject);
        k(jSONObject);
        l(jSONObject);
        m(jSONObject);
        n(jSONObject);
        o(jSONObject);
        p(jSONObject);
    }

    public boolean a() {
        if (this.a == null) {
            Log.e(com.umeng.analytics.f.q, "missing appkey ");
            return false;
        } else if (this.c != null && this.d != null) {
            return true;
        } else {
            Log.e(com.umeng.analytics.f.q, "missing device id");
            return false;
        }
    }

    public void a(Context context, String... strArr) {
        if (strArr != null && strArr.length == 2) {
            this.a = strArr[0];
            this.b = strArr[1];
        }
        if (this.a == null) {
            this.a = b.q(context);
        }
        if (this.b == null) {
            this.b = b.u(context);
        }
        this.c = b.g(context);
        this.d = b.h(context);
        this.e = b.r(context);
        SharedPreferences c = i.c(context);
        if (c != null) {
            this.f = c.getLong(com.umeng.analytics.f.H, 0);
        }
    }

    public void a(Context context) {
        this.g = Build.MODEL;
        this.h = "Android";
        this.i = VERSION.RELEASE;
        this.j = b.s(context);
        this.k = b.a();
        this.n = Build.BOARD;
        this.o = Build.BRAND;
        this.p = Build.TIME;
        this.q = Build.MANUFACTURER;
        this.r = Build.ID;
        this.s = Build.DEVICE;
    }

    public void b(Context context) {
        this.t = b.e(context);
        this.u = b.d(context);
        this.v = b.v(context);
    }

    public void c(Context context) {
        this.w = "Android";
        this.x = com.umeng.analytics.f.c;
    }

    public void d(Context context) {
        this.y = b.o(context);
        String[] p = b.p(context);
        this.z = p[0];
        this.A = p[1];
    }

    public void e(Context context) {
        String[] k = b.k(context);
        this.B = k[0];
        this.C = k[1];
        this.D = b.t(context);
    }

    public void b(Context context, String... strArr) {
        a(context, strArr);
        a(context);
        b(context);
        c(context);
        d(context);
        e(context);
    }

    public boolean b() {
        if (this.a == null || this.c == null) {
            return false;
        }
        return true;
    }
}
