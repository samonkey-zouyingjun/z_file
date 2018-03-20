package com.umeng.analytics.a;

import android.support.v4.app.NotificationCompat;
import com.umeng.analytics.f;
import com.umeng.common.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: LogBody */
public class i implements g {
    public ArrayList<g> a = new ArrayList();
    public ArrayList<g> b = new ArrayList();
    public ArrayList<g> c = new ArrayList();
    public ArrayList<g> d = new ArrayList();
    public ArrayList<g> e = new ArrayList();
    private final String f = "launch";
    private final String g = "terminate";
    private final String h = "error";
    private final String i = NotificationCompat.CATEGORY_EVENT;
    private final String j = "ekv";

    public void a(h hVar) {
        if (hVar != null && hVar.a()) {
            this.a.add(hVar);
        }
    }

    public void a(m mVar) {
        if (mVar != null && mVar.a()) {
            this.b.add(mVar);
        }
    }

    public void a(d dVar) {
        if (dVar != null && dVar.a()) {
            this.c.add(dVar);
        }
    }

    public void a(e eVar) {
        if (eVar != null && eVar.a()) {
            this.d.add(eVar);
        }
    }

    public void a(String str, a aVar) {
        if (aVar != null && aVar.a()) {
            Iterator it = this.e.iterator();
            while (it.hasNext()) {
                b bVar = (b) ((g) it.next());
                if (bVar.a.equals(str)) {
                    bVar.b.add(aVar);
                    return;
                }
            }
            this.e.add(new b(str, aVar));
        }
    }

    public void a(b bVar) {
        if (bVar != null && bVar.a()) {
            this.e.add(bVar);
        }
    }

    private void c(JSONObject jSONObject) throws Exception {
        if (jSONObject.has("launch")) {
            JSONArray jSONArray = jSONObject.getJSONArray("launch");
            for (int i = 0; i < jSONArray.length(); i++) {
                h hVar = new h();
                hVar.a(jSONArray.getJSONObject(i));
                a(hVar);
            }
        }
    }

    private void d(JSONObject jSONObject) throws Exception {
        if (jSONObject.has("terminate")) {
            JSONArray jSONArray = jSONObject.getJSONArray("terminate");
            for (int i = 0; i < jSONArray.length(); i++) {
                m mVar = new m();
                mVar.a(jSONArray.getJSONObject(i));
                a(mVar);
            }
        }
    }

    private void e(JSONObject jSONObject) throws Exception {
        if (jSONObject.has(NotificationCompat.CATEGORY_EVENT)) {
            JSONArray jSONArray = jSONObject.getJSONArray(NotificationCompat.CATEGORY_EVENT);
            for (int i = 0; i < jSONArray.length(); i++) {
                e eVar = new e();
                eVar.a(jSONArray.getJSONObject(i));
                a(eVar);
            }
        }
    }

    private void f(JSONObject jSONObject) throws Exception {
        if (jSONObject.has("ekv")) {
            JSONArray jSONArray = jSONObject.getJSONArray("ekv");
            for (int i = 0; i < jSONArray.length(); i++) {
                b bVar = new b();
                bVar.a(jSONArray.getJSONObject(i));
                a(bVar);
            }
        }
    }

    private void g(JSONObject jSONObject) throws Exception {
        if (jSONObject.has("error")) {
            JSONArray jSONArray = jSONObject.getJSONArray("error");
            for (int i = 0; i < jSONArray.length(); i++) {
                d dVar = new d();
                dVar.a(jSONArray.getJSONObject(i));
                a(dVar);
            }
        }
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                c(jSONObject);
                d(jSONObject);
                e(jSONObject);
                f(jSONObject);
                g(jSONObject);
            } catch (Exception e) {
                Log.b(f.q, "merge log body eror", e);
            }
        }
    }

    private JSONArray a(ArrayList<g> arrayList) {
        JSONArray jVar;
        if (arrayList.size() > 0) {
            JSONArray jSONArray = new JSONArray();
            Iterator it = arrayList.iterator();
            JSONArray jSONArray2 = null;
            while (it.hasNext()) {
                try {
                    jVar = new j(this, (g) it.next());
                } catch (Exception e) {
                    Log.a(f.q, "Fail to write json ...", e);
                    jVar = jSONArray2;
                }
                if (jVar == null) {
                    jSONArray2 = jVar;
                } else {
                    jSONArray.put(jVar);
                    jSONArray2 = jVar;
                }
            }
            jVar = jSONArray;
        } else {
            jVar = null;
        }
        if (jVar == null || jVar.length() == 0) {
            return null;
        }
        return jVar;
    }

    public void b(JSONObject jSONObject) throws Exception {
        JSONArray a = a(this.a);
        JSONArray a2 = a(this.b);
        JSONArray a3 = a(this.d);
        JSONArray a4 = a(this.c);
        JSONArray a5 = a(this.e);
        if (a != null) {
            jSONObject.put("launch", a);
        }
        if (a2 != null) {
            jSONObject.put("terminate", a2);
        }
        if (a3 != null) {
            jSONObject.put(NotificationCompat.CATEGORY_EVENT, a3);
        }
        if (a4 != null) {
            jSONObject.put("error", a4);
        }
        if (a5 != null) {
            jSONObject.put("ekv", a5);
        }
    }

    public boolean a() {
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            if (!((g) it.next()).a()) {
                return false;
            }
        }
        it = this.b.iterator();
        while (it.hasNext()) {
            if (!((g) it.next()).a()) {
                return false;
            }
        }
        it = this.d.iterator();
        while (it.hasNext()) {
            if (!((g) it.next()).a()) {
                return false;
            }
        }
        it = this.e.iterator();
        while (it.hasNext()) {
            if (!((g) it.next()).a()) {
                return false;
            }
        }
        it = this.c.iterator();
        while (it.hasNext()) {
            if (!((g) it.next()).a()) {
                return false;
            }
        }
        if (this.a.size() == 0 && this.b.size() == 0 && this.d.size() == 0 && this.e.size() == 0 && this.c.size() == 0) {
            return false;
        }
        return true;
    }

    public void a(i iVar) {
        this.a.addAll(iVar.a);
        this.b.addAll(iVar.b);
        this.d.addAll(iVar.d);
        this.c.addAll(iVar.c);
        b(iVar.e);
    }

    private void b(ArrayList<g> arrayList) {
        if (!arrayList.isEmpty()) {
            b bVar;
            HashMap hashMap = new HashMap();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                bVar = (b) ((g) it.next());
                if (hashMap.containsKey(bVar.a)) {
                    ((b) hashMap.get(bVar.a)).a(bVar);
                } else {
                    hashMap.put(bVar.a, bVar);
                }
            }
            it = this.e.iterator();
            while (it.hasNext()) {
                bVar = (b) ((g) it.next());
                if (hashMap.containsKey(bVar.a)) {
                    ((b) hashMap.get(bVar.a)).a(bVar);
                } else {
                    hashMap.put(bVar.a, bVar);
                }
            }
            this.e.clear();
            for (b bVar2 : hashMap.values()) {
                this.e.add(bVar2);
            }
        }
    }

    public int b() {
        Iterator it = this.e.iterator();
        int i = 0;
        while (it.hasNext()) {
            i = ((b) ((g) it.next())).b.size() + i;
        }
        return (((this.a.size() + i) + this.b.size()) + this.d.size()) + this.c.size();
    }

    public void c() {
        this.a.clear();
        this.b.clear();
        this.d.clear();
        this.e.clear();
        this.c.clear();
    }
}
