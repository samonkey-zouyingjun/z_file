package com.umeng.common.net;

import android.support.v4.app.NotificationCompat;
import org.json.JSONObject;

/* compiled from: ReportResponse */
public class o extends t {
    public a a;

    /* compiled from: ReportResponse */
    public enum a {
        SUCCESS,
        FAIL
    }

    public o(JSONObject jSONObject) {
        super(jSONObject);
        if ("ok".equalsIgnoreCase(jSONObject.optString(NotificationCompat.CATEGORY_STATUS)) || "ok".equalsIgnoreCase(jSONObject.optString("success"))) {
            this.a = a.SUCCESS;
        } else {
            this.a = a.FAIL;
        }
    }
}
