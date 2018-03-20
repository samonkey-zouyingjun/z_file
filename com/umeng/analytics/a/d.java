package com.umeng.analytics.a;

import com.umeng.analytics.f;
import com.umeng.common.Log;
import com.zidoo.custom.app.AppTopBaseManger;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.json.JSONObject;

/* compiled from: Error */
public class d extends n implements g {
    public String a;
    private final String b = "context";

    public d(String str) {
        this.a = str;
    }

    public d(Throwable th) {
        this.a = a(th);
    }

    private String a(Throwable th) {
        String str = null;
        if (th != null) {
            try {
                Writer stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                th.printStackTrace(printWriter);
                for (Throwable cause = th.getCause(); cause != null; cause = cause.getCause()) {
                    cause.printStackTrace(printWriter);
                }
                str = stringWriter.toString();
                printWriter.close();
                stringWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public boolean a() {
        if (this.a != null) {
            return super.a();
        }
        Log.b(f.q, "mContent is not initialized");
        return false;
    }

    public void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                this.g = jSONObject.getString("date");
                this.h = jSONObject.getString(AppTopBaseManger.KEY_TIME);
                this.a = jSONObject.getString("context");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void b(JSONObject jSONObject) throws Exception {
        jSONObject.put("date", this.g);
        jSONObject.put(AppTopBaseManger.KEY_TIME, this.h);
        jSONObject.put("context", this.a);
    }
}
