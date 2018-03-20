package com.umeng.common.net;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import com.umeng.common.Log;

/* compiled from: DownloadAgent */
public class a {
    private static final String b = a.class.getName();
    final Messenger a = new Messenger(new b(this));
    private Context c;
    private k d;
    private Messenger e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String[] k;
    private boolean l = false;
    private ServiceConnection m = new b(this);

    /* compiled from: DownloadAgent */
    static class a {
        public String a;
        public String b;
        public String c;
        public String d;
        public String e;
        public String[] f = null;
        public boolean g = false;

        public a(String str, String str2, String str3) {
            this.a = str;
            this.b = str2;
            this.c = str3;
        }

        public Bundle a() {
            Bundle bundle = new Bundle();
            bundle.putString("mComponentName", this.a);
            bundle.putString("mTitle", this.b);
            bundle.putString("mUrl", this.c);
            bundle.putString("mMd5", this.d);
            bundle.putString("mTargetMd5", this.e);
            bundle.putStringArray("reporturls", this.f);
            bundle.putBoolean("rich_notification", this.g);
            return bundle;
        }

        public static a a(Bundle bundle) {
            a aVar = new a(bundle.getString("mComponentName"), bundle.getString("mTitle"), bundle.getString("mUrl"));
            aVar.d = bundle.getString("mMd5");
            aVar.e = bundle.getString("mTargetMd5");
            aVar.f = bundle.getStringArray("reporturls");
            aVar.g = bundle.getBoolean("rich_notification");
            return aVar;
        }
    }

    /* compiled from: DownloadAgent */
    class b extends Handler {
        final /* synthetic */ a a;

        b(a aVar) {
            this.a = aVar;
        }

        public void handleMessage(Message message) {
            try {
                Log.c(a.b, "DownloadAgent.handleMessage(" + message.what + "): ");
                switch (message.what) {
                    case 3:
                        if (this.a.d != null) {
                            this.a.d.a(message.arg1);
                            return;
                        }
                        return;
                    case 5:
                        this.a.c.unbindService(this.a.m);
                        if (this.a.d == null) {
                            return;
                        }
                        if (message.arg1 == 1 || message.arg1 == 3) {
                            this.a.d.a(message.arg1, message.arg2, message.getData().getString("filename"));
                            return;
                        } else if (message.arg1 == 2) {
                            this.a.d.a(message.arg1, message.arg2, "");
                            return;
                        } else {
                            this.a.d.a(0, 0, null);
                            Log.c(a.b, "DownloadAgent.handleMessage(DownloadingService.DOWNLOAD_COMPLETE_FAIL): ");
                            return;
                        }
                    default:
                        super.handleMessage(message);
                        return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.c(a.b, "DownloadAgent.handleMessage(" + message.what + "): " + e.getMessage());
            }
            e.printStackTrace();
            Log.c(a.b, "DownloadAgent.handleMessage(" + message.what + "): " + e.getMessage());
        }
    }

    public void a(String str) {
        this.i = str;
    }

    public void b(String str) {
        this.j = str;
    }

    public void a(String[] strArr) {
        this.k = strArr;
    }

    public void a(boolean z) {
        this.l = z;
    }

    public a(Context context, String str, String str2, String str3, k kVar) {
        this.c = context.getApplicationContext();
        this.f = str;
        this.g = str2;
        this.h = str3;
        this.d = kVar;
    }

    public void a() {
        this.c.bindService(new Intent(this.c, DownloadingService.class), this.m, 1);
    }
}
