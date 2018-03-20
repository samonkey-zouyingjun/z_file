package com.umeng.analytics.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.umeng.analytics.f;
import com.umeng.analytics.i;
import com.umeng.common.Log;
import com.umeng.common.b;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: Terminate */
public class m extends l implements g {
    private static final String k = "duration";
    private static final String l = "activities";
    private static final String m = "terminate_time";
    public o a;
    public p b;
    public long c = 0;
    private ArrayList<k> d = new ArrayList();

    public m(String str) {
        this.e = str;
    }

    public static m a(Context context) {
        m mVar = null;
        SharedPreferences e = i.e(context);
        String string = e.getString(l.f, null);
        if (string != null) {
            mVar = new m(string);
            o b = o.b(context);
            if (b != null && b.a()) {
                mVar.a = b;
            }
            p a = p.a(context);
            if (a != null && a.a()) {
                mVar.b = a;
            }
            mVar.d = c(e);
            mVar.c = d(e);
            String[] b2 = b(e);
            if (b2 != null && b2.length == 2) {
                mVar.g = b2[0];
                mVar.h = b2[1];
            }
            a(e);
        }
        return mVar;
    }

    private static void a(SharedPreferences sharedPreferences) {
        Editor edit = sharedPreferences.edit();
        edit.putLong(k, 0);
        edit.putString(l, "");
        edit.commit();
    }

    private static String[] b(SharedPreferences sharedPreferences) {
        long j = sharedPreferences.getLong(m, 0);
        if (j <= 0) {
            return null;
        }
        return b.a(new Date(j)).split(" ");
    }

    private static ArrayList<k> c(SharedPreferences sharedPreferences) {
        String string = sharedPreferences.getString(l, "");
        if (!"".equals(string)) {
            ArrayList<k> arrayList = new ArrayList();
            try {
                String[] split = string.split(";");
                for (String jSONArray : split) {
                    arrayList.add(new k(new JSONArray(jSONArray)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (arrayList.size() > 0) {
                return arrayList;
            }
        }
        return null;
    }

    private static long d(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(k, 0) / 1000;
    }

    public static Editor a(SharedPreferences sharedPreferences, String str, long j, long j2) {
        long j3 = j2 - j;
        long j4 = sharedPreferences.getLong(k, 0);
        Editor edit = sharedPreferences.edit();
        if (f.j) {
            String string = sharedPreferences.getString(l, "");
            if (!"".equals(string)) {
                string = string + ";";
            }
            string = string + String.format("[%s,%d]", new Object[]{str, Long.valueOf(j3 / 1000)});
            edit.remove(l);
            edit.putString(l, string);
        }
        edit.putLong(k, j3 + j4);
        edit.putLong(m, j2);
        edit.commit();
        return edit;
    }

    public boolean a() {
        if (this.a == null && f.i) {
            Log.c(f.q, "missing location info in Terminate");
        }
        if (this.b == null) {
            Log.e(f.q, "missing receive and transport Traffic in Terminate ");
        }
        if (this.c <= 0) {
            Log.b(f.q, "missing Duration info in Terminate");
            return false;
        }
        if (this.d == null || this.d.size() == 0) {
            Log.e(f.q, "missing Activities info in Terminate");
        }
        return super.a();
    }

    private void c(JSONObject jSONObject) throws Exception {
        if (this.b != null) {
            this.b.b(jSONObject);
        }
    }

    private void d(JSONObject jSONObject) throws Exception {
        if (this.a != null) {
            this.a.b(jSONObject);
        }
    }

    private void e(JSONObject jSONObject) throws Exception {
        o oVar = new o();
        oVar.a(jSONObject);
        if (oVar.a()) {
            this.a = oVar;
        }
    }

    private void f(JSONObject jSONObject) throws Exception {
        p pVar = new p();
        pVar.a(jSONObject);
        if (pVar.a()) {
            this.b = pVar;
        }
    }

    private void g(JSONObject jSONObject) throws Exception {
        if (jSONObject.has(l)) {
            JSONArray jSONArray = jSONObject.getJSONArray(l);
            for (int i = 0; i < jSONArray.length(); i++) {
                this.d.add(new k(jSONArray.getJSONArray(i)));
            }
        }
    }

    private void h(JSONObject jSONObject) throws Exception {
        if (this.d.size() != 0) {
            JSONArray jSONArray = new JSONArray();
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                k kVar = (k) it.next();
                JSONArray jSONArray2 = new JSONArray();
                jSONArray2.put(kVar.a);
                jSONArray2.put(kVar.b);
                jSONArray.put(jSONArray2);
            }
            jSONObject.put(l, jSONArray);
        }
    }

    public void a(JSONObject jSONObject) {
        try {
            super.a(jSONObject);
            this.c = jSONObject.getLong(k);
            f(jSONObject);
            e(jSONObject);
            g(jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        super.b(jSONObject);
        if (this.c > 0) {
            jSONObject.put(k, this.c);
        }
        c(jSONObject);
        d(jSONObject);
        h(jSONObject);
    }
}
