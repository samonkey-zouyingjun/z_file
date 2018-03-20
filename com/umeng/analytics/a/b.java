package com.umeng.analytics.a;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: EKVHub */
public class b implements g {
    public String a;
    public ArrayList<a> b = new ArrayList();

    public b(String str, a aVar) {
        this.a = str;
        this.b.add(aVar);
    }

    private void c(JSONObject jSONObject) throws Exception {
        if (jSONObject.length() != 0) {
            this.a = (String) jSONObject.keys().next();
        }
    }

    private void d(JSONObject jSONObject) throws Exception {
        if (this.a != null) {
            JSONArray jSONArray = jSONObject.getJSONArray(this.a);
            for (int i = 0; i < jSONArray.length(); i++) {
                a aVar = new a();
                aVar.a(jSONArray.getJSONObject(i));
                this.b.add(aVar);
            }
        }
    }

    public void a(JSONObject jSONObject) throws Exception {
        if (jSONObject != null) {
            c(jSONObject);
            d(jSONObject);
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        JSONArray jSONArray = new JSONArray();
        Iterator it = this.b.iterator();
        while (it.hasNext()) {
            jSONArray.put(new c(this, (a) it.next()));
        }
        jSONObject.put(this.a, jSONArray);
    }

    public boolean a() {
        if (this.a == null || this.b.size() == 0) {
            return false;
        }
        Iterator it = this.b.iterator();
        while (it.hasNext()) {
            if (!((a) it.next()).a()) {
                return false;
            }
        }
        return true;
    }

    public void a(b bVar) {
        this.b.addAll(bVar.b);
    }
}
