package com.umeng.analytics;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import com.umeng.analytics.a.f;
import com.umeng.analytics.onlineconfig.c;
import com.umeng.common.Log;
import com.umeng.common.util.e;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

/* compiled from: PolicyManager */
public abstract class d implements h, c {
    private final a a = new a();
    private final Object b = new Object();
    protected final c c = new c();
    protected final f d = new f();
    protected final int e = 1;
    protected final int f = 2;
    protected final int g = 3;
    protected final int h = 4;
    protected final int i = 5;
    String j = null;
    String k = null;
    private final Handler l;
    private final String m = "body";
    private final String n = "header";
    private int o = -1;
    private long p = -1;
    private long q = -1;
    private long r = -1;
    private boolean s = false;

    /* compiled from: PolicyManager */
    private final class a implements Runnable {
        final /* synthetic */ d a;
        private Context b;

        a(d dVar, Context context) {
            this.a = dVar;
            this.b = context.getApplicationContext();
        }

        public void run() {
            try {
                synchronized (this.a.b) {
                    this.a.c.a(this.b);
                }
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in ReportMessageHandler", e);
            } catch (Error e2) {
                Log.b(f.q, "Error : " + e2.getMessage());
                try {
                    i.j(this.b);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    /* compiled from: PolicyManager */
    private final class b implements Runnable {
        final /* synthetic */ d a;
        private Context b;

        b(d dVar, Context context) {
            this.a = dVar;
            this.b = context.getApplicationContext();
        }

        public void run() {
            try {
                synchronized (this.a.b) {
                    this.a.d(this.b);
                }
            } catch (Exception e) {
                Log.b(f.q, "Exception occurred in ReportMessageHandler", e);
            } catch (Error e2) {
                Log.b(f.q, "Error : " + e2.getMessage());
                try {
                    i.j(this.b);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public d() {
        HandlerThread handlerThread = new HandlerThread(f.q);
        handlerThread.start();
        this.l = new Handler(handlerThread.getLooper());
    }

    public void e(Context context) {
        a(context, 1);
    }

    public synchronized void a(Context context, int i) {
        if (!this.s && i == 4) {
            a(context);
            f(context);
            this.s = true;
        }
        if (i == 5) {
            this.c.a(context);
        } else {
            if (b(context, i)) {
                this.l.post(new b(this, context));
            }
            if (this.c.b()) {
                this.l.post(new a(this, context));
            }
        }
    }

    private void a(Context context) {
        if (this.o == -1) {
            int[] i = i.i(context);
            this.o = i[0];
            this.p = (long) i[1];
            if (this.o == 4 || this.o == 6) {
                this.q = i.e(context).getLong(f.F, -1);
            }
        }
    }

    public void f(Context context) {
        this.a.a(context);
        this.a.a((h) this);
    }

    private void b(Context context) {
        if (this.o == 6 || this.o == 4) {
            i.e(context).edit().putLong(f.F, this.q).commit();
        }
        if (this.r != -1) {
            this.d.f = this.r;
            i.c(context).edit().putLong(f.H, this.r).commit();
        }
    }

    boolean b(Context context, int i) {
        if (!com.umeng.common.b.n(context)) {
            return false;
        }
        switch (i) {
            case 1:
                break;
            case 2:
                return true;
            case 3:
                return false;
            case 4:
                if (this.o == 1) {
                    return true;
                }
                break;
            default:
                return false;
        }
        if (this.o == 0) {
            return true;
        }
        if (this.o == 6 && System.currentTimeMillis() - this.q > this.p) {
            this.q = System.currentTimeMillis();
            return true;
        } else if (this.o == 4 && System.currentTimeMillis() - this.q > f.g) {
            this.q = System.currentTimeMillis();
            return true;
        } else if (this.o == 5 && com.umeng.common.b.l(context)) {
            return true;
        } else {
            return false;
        }
    }

    private String c(Context context) {
        if (!this.d.b()) {
            this.d.b(context, this.k, this.j);
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.d.w);
        stringBuffer.append("/");
        stringBuffer.append(this.d.x);
        stringBuffer.append(" ");
        try {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append(context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString());
            stringBuffer2.append("/");
            stringBuffer2.append(this.d.t);
            stringBuffer2.append(" ");
            stringBuffer2.append(this.d.g);
            stringBuffer2.append("/");
            stringBuffer2.append(this.d.i);
            stringBuffer2.append(" ");
            stringBuffer2.append(this.d.d);
            stringBuffer.append(URLEncoder.encode(stringBuffer2.toString(), e.f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    private void d(Context context) {
        JSONObject g = g(context);
        if (g != null && !g.isNull("body")) {
            String str = null;
            for (String str2 : f.r) {
                str2 = a(context, g, str2);
                if (str2 != null) {
                    break;
                }
            }
            if (str2 != null) {
                i.j(context);
                Log.a(f.q, "send applog succeed :" + str2);
            } else {
                this.q = -1;
                i.b(context, g, com.umeng.common.b.d(context));
                Log.a(f.q, "send applog failed");
            }
            b(context);
        }
    }

    JSONObject g(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (!this.d.b()) {
                this.d.b(context, this.k, this.j);
            }
            if (this.d.a()) {
                this.c.b(context);
                if (this.c.a() <= 0) {
                    Log.c(f.q, "no message to send");
                    return null;
                } else if (this.c.c()) {
                    jSONObject.put("header", new e(this));
                    jSONObject.put("body", this.c.d());
                    this.c.e();
                    return jSONObject;
                } else {
                    throw new Exception("protocol Body has invalid field: " + this.c.d().toString());
                }
            }
            Log.b(f.q, "protocol Header need Appkey or Device ID ,Please check AndroidManifest.xml ");
            return null;
        } catch (Exception e) {
            Log.b(f.q, "", e);
            i.j(context);
            return null;
        } catch (Error e2) {
            Log.b(f.q, "Error:" + e2.getMessage());
            i.j(context);
            return null;
        }
    }

    private String a(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 64);
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine + "\n");
            } catch (Exception e) {
                stringBuilder = f.q;
                Log.b(stringBuilder, "Caught IOException in convertStreamToString()", e);
                return null;
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                    Log.b(f.q, "Caught IOException in convertStreamToString()", e2);
                    return null;
                }
            }
        }
        return stringBuilder.toString();
    }

    private String a(Context context, JSONObject jSONObject, String str) {
        Object httpPost = new HttpPost(str);
        HttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, SmbConstants.DEFAULT_RESPONSE_TIMEOUT);
        HttpClient defaultHttpClient = new DefaultHttpClient(basicHttpParams);
        httpPost.addHeader("X-Umeng-Sdk", c(context));
        try {
            String a = g.a(context);
            if (a != null) {
                defaultHttpClient.getParams().setParameter("http.route.default-proxy", new HttpHost(a, 80));
            }
            a = jSONObject.toString();
            Log.a(f.q, a);
            if (f.t) {
                byte[] a2 = com.umeng.common.util.f.a("content=" + a, "utf-8");
                httpPost.addHeader("Content-Encoding", "deflate");
                httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(a2), (long) com.umeng.common.util.f.a));
            } else {
                List arrayList = new ArrayList(1);
                arrayList.add(new BasicNameValuePair("content", a));
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, e.f));
            }
            Date date = new Date();
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            this.r = new Date().getTime() - date.getTime();
            if (execute.getStatusLine().getStatusCode() != 200) {
                return null;
            }
            Log.a(f.q, "Sent message to " + str);
            HttpEntity entity = execute.getEntity();
            if (entity != null) {
                return a(entity.getContent());
            }
            return null;
        } catch (Exception e) {
            Log.b(f.q, "ClientProtocolException,Failed to send message.", e);
            return null;
        } catch (Exception e2) {
            Log.b(f.q, "IOException,Failed to send message.", e2);
            return null;
        }
    }

    public void a(int i, long j) {
        this.o = i;
        this.p = j;
    }
}
