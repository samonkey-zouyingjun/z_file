package com.umeng.common.net;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.umeng.common.Log;
import com.umeng.common.util.DeltaUpdate;
import com.umeng.common.util.g;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DownloadingService extends Service {
    static final int a = 3;
    static final int b = 4;
    static final int c = 5;
    static final int d = 6;
    public static final int e = 0;
    public static final int f = 1;
    public static final int g = 2;
    public static final int h = 3;
    static final int i = 0;
    static final int j = 1;
    static final int k = 100;
    static final String l = "filename";
    public static boolean m = false;
    private static final String o = DownloadingService.class.getName();
    private static final int q = 3;
    private static final long u = 8000;
    private static Map<a, Messenger> v = new HashMap();
    private static Map<Integer, d> w = new HashMap();
    private static Boolean y = Boolean.valueOf(false);
    final Messenger n = new Messenger(new c(this));
    private NotificationManager p;
    private Context r;
    private Handler s;
    private a t;
    private BroadcastReceiver x;

    private interface a {
        void a(int i);

        void a(int i, int i2);

        void a(int i, Exception exception);

        void a(int i, String str);
    }

    class b extends Thread {
        final /* synthetic */ DownloadingService a;
        private Context b;
        private String c;
        private int d = 0;
        private long e = -1;
        private long f = -1;
        private int g = -1;
        private int h;
        private a i;
        private a j;

        public b(DownloadingService downloadingService, Context context, a aVar, int i, int i2, a aVar2) {
            this.a = downloadingService;
            try {
                this.b = context;
                this.j = aVar;
                this.d = i2;
                if (DownloadingService.w.containsKey(Integer.valueOf(i))) {
                    long[] jArr = ((d) DownloadingService.w.get(Integer.valueOf(i))).f;
                    if (jArr != null && jArr.length > 1) {
                        this.e = jArr[0];
                        this.f = jArr[1];
                    }
                }
                this.i = aVar2;
                this.h = i;
                if (com.umeng.common.b.b()) {
                    this.c = Environment.getExternalStorageDirectory().getCanonicalPath();
                    new File(this.c).mkdirs();
                } else {
                    this.c = this.b.getFilesDir().getAbsolutePath();
                }
                this.c += "/download/.um/apk";
                new File(this.c).mkdirs();
            } catch (Exception e) {
                Log.c(DownloadingService.o, e.getMessage(), e);
                this.i.a(this.h, e);
            }
        }

        public void run() {
            boolean z = false;
            this.d = 0;
            try {
                if (this.i != null) {
                    this.i.a(this.h);
                }
                if (this.e > 0) {
                    z = true;
                }
                a(z);
                if (DownloadingService.v.size() <= 0) {
                    this.a.stopSelf();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void a(int i) {
            this.g = i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void a(boolean r14) {
            /*
            r13 = this;
            r2 = 0;
            r1 = 0;
            r0 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0.<init>();	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = r13.j;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = r3.c;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = com.umeng.common.util.g.a(r3);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r0.append(r3);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = ".apk.tmp";
            r0 = r0.append(r3);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r0.toString();	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = r13.j;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = r3.a;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r4 = "delta_update";
            r3 = r3.equalsIgnoreCase(r4);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            if (r3 == 0) goto L_0x06ff;
        L_0x002b:
            r3 = "apk";
            r4 = "patch";
            r0 = r0.replace(r3, r4);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = r0;
        L_0x0036:
            r0 = com.umeng.common.b.b();	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            if (r0 == 0) goto L_0x019d;
        L_0x003c:
            r0 = new java.io.File;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r4 = r13.c;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0.<init>(r4, r3);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r8 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r3 = 1;
            r8.<init>(r0, r3);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r4 = r0;
        L_0x004a:
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = "saveAPK: url = %1$15s\t|\tfilename = %2$15s";
            r3 = 2;
            r3 = new java.lang.Object[r3];	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r5 = 0;
            r6 = r13.j;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r6 = r6.c;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r3[r5] = r6;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r5 = 1;
            r6 = r4.getAbsolutePath();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r3[r5] = r6;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = java.lang.String.format(r1, r3);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            com.umeng.common.Log.c(r0, r1);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r0 = new java.net.URL;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = r13.j;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = r1.c;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r0.<init>(r1);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r0 = r0.openConnection();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r0 = (java.net.HttpURLConnection) r0;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = "GET";
            r0.setRequestMethod(r1);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = "Accept-Encoding";
            r3 = "identity";
            r0.setRequestProperty(r1, r3);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = "Connection";
            r3 = "keep-alive";
            r0.addRequestProperty(r1, r3);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r0.setConnectTimeout(r1);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
            r0.setReadTimeout(r1);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r1 = r4.exists();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            if (r1 == 0) goto L_0x00cf;
        L_0x00a0:
            r6 = r4.length();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r10 = 0;
            r1 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
            if (r1 <= 0) goto L_0x00cf;
        L_0x00aa:
            r1 = "Range";
            r3 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r3.<init>();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r5 = "bytes=";
            r3 = r3.append(r5);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r6 = r4.length();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r3 = r3.append(r6);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r5 = "-";
            r3 = r3.append(r5);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r3 = r3.toString();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r0.setRequestProperty(r1, r3);	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
        L_0x00cf:
            r0.connect();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            r9 = r0.getInputStream();	 Catch:{ IOException -> 0x06f0, RemoteException -> 0x06e0, all -> 0x06cf }
            if (r14 != 0) goto L_0x00fd;
        L_0x00d8:
            r2 = 0;
            r13.e = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.getContentLength();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = (long) r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r13.f = r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = "getContentLength: %1$15s";
            r2 = 1;
            r2 = new java.lang.Object[r2];	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = 0;
            r6 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r5 = java.lang.Long.valueOf(r6);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2[r3] = r5;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = java.lang.String.format(r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            com.umeng.common.Log.c(r0, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x00fd:
            r0 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r5 = new byte[r0];	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = 0;
            r6 = 50;
            r1 = 1;
            r2 = com.umeng.common.net.DownloadingService.o;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r7.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r3.append(r7);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = "saveAPK getContentLength ";
            r3 = r3.append(r7);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = java.lang.String.valueOf(r10);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r3.append(r7);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r3.toString();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            com.umeng.common.Log.c(r2, r3);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = com.umeng.common.net.c.a(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r3.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r7.c;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2.a(r3, r7);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x013f:
            r2 = r13.g;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r2 >= 0) goto L_0x06fc;
        L_0x0143:
            r2 = r9.read(r5);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r2 <= 0) goto L_0x06fc;
        L_0x0149:
            r3 = 0;
            r8.write(r5, r3, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10 = r13.e;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = (long) r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r2 + r10;
            r13.e = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r0 + 1;
            r0 = r0 % r6;
            if (r0 != 0) goto L_0x06f9;
        L_0x0158:
            r0 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.b.n(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 != 0) goto L_0x01bc;
        L_0x0160:
            r0 = 0;
        L_0x0161:
            r9.close();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r8.close();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.g;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 1;
            if (r1 != r2) goto L_0x02bf;
        L_0x016c:
            r0 = com.umeng.common.net.DownloadingService.w;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = java.lang.Integer.valueOf(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.get(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = (com.umeng.common.net.DownloadingService.d) r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r0.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 0;
            r4 = r13.e;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1[r2] = r4;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r0.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 1;
            r4 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1[r2] = r4;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = 2;
            r2 = r13.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = (long) r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0[r1] = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r9 == 0) goto L_0x0197;
        L_0x0194:
            r9.close();	 Catch:{ IOException -> 0x02a2 }
        L_0x0197:
            if (r8 == 0) goto L_0x019c;
        L_0x0199:
            r8.close();	 Catch:{ IOException -> 0x029c }
        L_0x019c:
            return;
        L_0x019d:
            r0 = r13.b;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r0.getFilesDir();	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r0.getAbsolutePath();	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r13.c = r0;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r13.b;	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r4 = 32771; // 0x8003 float:4.5922E-41 double:1.6191E-319;
            r1 = r0.openFileOutput(r3, r4);	 Catch:{ IOException -> 0x06e4, RemoteException -> 0x06d6, all -> 0x06c5 }
            r0 = r13.b;	 Catch:{ IOException -> 0x06ea, RemoteException -> 0x06db, all -> 0x06ca }
            r0 = r0.getFileStreamPath(r3);	 Catch:{ IOException -> 0x06ea, RemoteException -> 0x06db, all -> 0x06ca }
            r4 = r0;
            r8 = r1;
            goto L_0x004a;
        L_0x01bc:
            r10 = r13.e;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = (float) r10;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
            r0 = r0 * r2;
            r10 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = (float) r10;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0 / r2;
            r0 = (int) r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 100;
            if (r0 <= r2) goto L_0x06f6;
        L_0x01cb:
            r0 = 99;
            r2 = r0;
        L_0x01ce:
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 == 0) goto L_0x01d9;
        L_0x01d2:
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r7, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x01d9:
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ DeadObjectException -> 0x020f }
            r7 = r13.j;	 Catch:{ DeadObjectException -> 0x020f }
            r0 = r0.get(r7);	 Catch:{ DeadObjectException -> 0x020f }
            if (r0 == 0) goto L_0x01fb;
        L_0x01e5:
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ DeadObjectException -> 0x020f }
            r7 = r13.j;	 Catch:{ DeadObjectException -> 0x020f }
            r0 = r0.get(r7);	 Catch:{ DeadObjectException -> 0x020f }
            r0 = (android.os.Messenger) r0;	 Catch:{ DeadObjectException -> 0x020f }
            r7 = 0;
            r10 = 3;
            r11 = 0;
            r7 = android.os.Message.obtain(r7, r10, r2, r11);	 Catch:{ DeadObjectException -> 0x020f }
            r0.send(r7);	 Catch:{ DeadObjectException -> 0x020f }
        L_0x01fb:
            r0 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.net.c.a(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r7.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10 = r10.c;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r7, r10, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r3;
            goto L_0x013f;
        L_0x020f:
            r0 = move-exception;
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = "Service Client for downloading %1$15s is dead. Removing messenger from the service";
            r10 = 1;
            r10 = new java.lang.Object[r10];	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r11 = 0;
            r12 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r12 = r12.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10[r11] = r12;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = java.lang.String.format(r7, r10);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            com.umeng.common.Log.b(r0, r7);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r7 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r10 = 0;
            r0.put(r7, r10);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            goto L_0x01fb;
        L_0x0233:
            r0 = move-exception;
            r1 = r0;
            r2 = r8;
            r3 = r9;
        L_0x0237:
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ all -> 0x0595 }
            r4 = r1.getMessage();	 Catch:{ all -> 0x0595 }
            com.umeng.common.Log.c(r0, r4, r1);	 Catch:{ all -> 0x0595 }
            r0 = r13.d;	 Catch:{ all -> 0x0595 }
            r0 = r0 + 1;
            r13.d = r0;	 Catch:{ all -> 0x0595 }
            r4 = 3;
            if (r0 <= r4) goto L_0x05bd;
        L_0x024b:
            r0 = r13.j;	 Catch:{ all -> 0x0595 }
            r0 = r0.g;	 Catch:{ all -> 0x0595 }
            if (r0 != 0) goto L_0x05bd;
        L_0x0251:
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ RemoteException -> 0x0577 }
            r4 = "Download Fail out of max repeat count";
            com.umeng.common.Log.b(r0, r4);	 Catch:{ RemoteException -> 0x0577 }
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ RemoteException -> 0x0577 }
            r4 = r13.j;	 Catch:{ RemoteException -> 0x0577 }
            r0 = r0.get(r4);	 Catch:{ RemoteException -> 0x0577 }
            r0 = (android.os.Messenger) r0;	 Catch:{ RemoteException -> 0x0577 }
            r4 = 0;
            r5 = 5;
            r6 = 0;
            r7 = 0;
            r4 = android.os.Message.obtain(r4, r5, r6, r7);	 Catch:{ RemoteException -> 0x0577 }
            r0.send(r4);	 Catch:{ RemoteException -> 0x0577 }
            r0 = r13.a;	 Catch:{ all -> 0x0595 }
            r4 = r13.h;	 Catch:{ all -> 0x0595 }
            r0.a(r4);	 Catch:{ all -> 0x0595 }
            r13.a(r1);	 Catch:{ all -> 0x0595 }
            r0 = r13.a;	 Catch:{ all -> 0x0595 }
            r0 = r0.s;	 Catch:{ all -> 0x0595 }
            r1 = new com.umeng.common.net.j;	 Catch:{ all -> 0x0595 }
            r1.<init>(r13);	 Catch:{ all -> 0x0595 }
            r0.post(r1);	 Catch:{ all -> 0x0595 }
        L_0x028a:
            if (r3 == 0) goto L_0x028f;
        L_0x028c:
            r3.close();	 Catch:{ IOException -> 0x0668 }
        L_0x028f:
            if (r2 == 0) goto L_0x019c;
        L_0x0291:
            r2.close();	 Catch:{ IOException -> 0x0296 }
            goto L_0x019c;
        L_0x0296:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x029c:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x02a2:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x02b3 }
            if (r8 == 0) goto L_0x019c;
        L_0x02a8:
            r8.close();	 Catch:{ IOException -> 0x02ad }
            goto L_0x019c;
        L_0x02ad:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x02b3:
            r0 = move-exception;
            if (r8 == 0) goto L_0x02b9;
        L_0x02b6:
            r8.close();	 Catch:{ IOException -> 0x02ba }
        L_0x02b9:
            throw r0;
        L_0x02ba:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x02b9;
        L_0x02bf:
            r1 = r13.g;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 2;
            if (r1 != r2) goto L_0x030c;
        L_0x02c4:
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.e;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r6 = r13.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r6 = (long) r6;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r1, r2, r4, r6);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.p;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.cancel(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r9 == 0) goto L_0x02e2;
        L_0x02df:
            r9.close();	 Catch:{ IOException -> 0x02ef }
        L_0x02e2:
            if (r8 == 0) goto L_0x019c;
        L_0x02e4:
            r8.close();	 Catch:{ IOException -> 0x02e9 }
            goto L_0x019c;
        L_0x02e9:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x02ef:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0300 }
            if (r8 == 0) goto L_0x019c;
        L_0x02f5:
            r8.close();	 Catch:{ IOException -> 0x02fa }
            goto L_0x019c;
        L_0x02fa:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0300:
            r0 = move-exception;
            if (r8 == 0) goto L_0x0306;
        L_0x0303:
            r8.close();	 Catch:{ IOException -> 0x0307 }
        L_0x0306:
            throw r0;
        L_0x0307:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x0306;
        L_0x030c:
            if (r0 != 0) goto L_0x0384;
        L_0x030e:
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = "Download Fail repeat count=";
            r1 = r1.append(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r1.append(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r1.toString();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            com.umeng.common.Log.b(r0, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.get(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = (android.os.Messenger) r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = 0;
            r2 = 5;
            r3 = 0;
            r4 = 0;
            r1 = android.os.Message.obtain(r1, r2, r3, r4);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.send(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 == 0) goto L_0x0355;
        L_0x034d:
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 0;
            r0.a(r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x0355:
            if (r9 == 0) goto L_0x035a;
        L_0x0357:
            r9.close();	 Catch:{ IOException -> 0x0367 }
        L_0x035a:
            if (r8 == 0) goto L_0x019c;
        L_0x035c:
            r8.close();	 Catch:{ IOException -> 0x0361 }
            goto L_0x019c;
        L_0x0361:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0367:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0378 }
            if (r8 == 0) goto L_0x019c;
        L_0x036d:
            r8.close();	 Catch:{ IOException -> 0x0372 }
            goto L_0x019c;
        L_0x0372:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0378:
            r0 = move-exception;
            if (r8 == 0) goto L_0x037e;
        L_0x037b:
            r8.close();	 Catch:{ IOException -> 0x037f }
        L_0x037e:
            throw r0;
        L_0x037f:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x037e;
        L_0x0384:
            r0 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 == 0) goto L_0x03c3;
        L_0x038a:
            r0 = new java.util.HashMap;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = "dsize";
            r2 = r13.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.put(r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = com.umeng.common.util.g.a();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = " ";
            r1 = r1.split(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 1;
            r1 = r1[r2];	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = "dtime";
            r0.put(r2, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = "ptimes";
            r2 = r13.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.put(r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = 1;
            r2 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r2.f;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            com.umeng.common.net.DownloadingService.b(r0, r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x03c3:
            r0 = new java.io.File;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r4.getParent();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r4.getName();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = ".tmp";
            r5 = "";
            r2 = r2.replace(r3, r5);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.<init>(r1, r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4.renameTo(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r0.getAbsolutePath();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r2.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r2 == 0) goto L_0x053d;
        L_0x03e7:
            r2 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r2.d;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.util.g.a(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r2.equalsIgnoreCase(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 != 0) goto L_0x053d;
        L_0x03f5:
            r0 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = "delta_update";
            r0 = r0.equalsIgnoreCase(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 == 0) goto L_0x04a0;
        L_0x0402:
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.p;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.cancel(r2);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = new android.os.Bundle;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = "filename";
            r0.putString(r2, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = android.os.Message.obtain();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 5;
            r1.what = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 3;
            r1.arg1 = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1.arg2 = r2;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1.setData(r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r2 = r13.j;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r0 = r0.get(r2);	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            if (r0 == 0) goto L_0x0444;
        L_0x0435:
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r2 = r13.j;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r0 = r0.get(r2);	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r0 = (android.os.Messenger) r0;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r0.send(r1);	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
        L_0x0444:
            r0 = r13.a;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r1 = r13.h;	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
            r0.a(r1);	 Catch:{ RemoteException -> 0x045d, IOException -> 0x0233 }
        L_0x044b:
            if (r9 == 0) goto L_0x0450;
        L_0x044d:
            r9.close();	 Catch:{ IOException -> 0x0483 }
        L_0x0450:
            if (r8 == 0) goto L_0x019c;
        L_0x0452:
            r8.close();	 Catch:{ IOException -> 0x0457 }
            goto L_0x019c;
        L_0x0457:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x045d:
            r0 = move-exception;
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            goto L_0x044b;
        L_0x0466:
            r0 = move-exception;
        L_0x0467:
            r1 = r13.a;	 Catch:{ all -> 0x06d3 }
            r2 = r13.h;	 Catch:{ all -> 0x06d3 }
            r1.a(r2);	 Catch:{ all -> 0x06d3 }
            r0.printStackTrace();	 Catch:{ all -> 0x06d3 }
            if (r9 == 0) goto L_0x0476;
        L_0x0473:
            r9.close();	 Catch:{ IOException -> 0x0685 }
        L_0x0476:
            if (r8 == 0) goto L_0x019c;
        L_0x0478:
            r8.close();	 Catch:{ IOException -> 0x047d }
            goto L_0x019c;
        L_0x047d:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0483:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0494 }
            if (r8 == 0) goto L_0x019c;
        L_0x0489:
            r8.close();	 Catch:{ IOException -> 0x048e }
            goto L_0x019c;
        L_0x048e:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0494:
            r0 = move-exception;
            if (r8 == 0) goto L_0x049a;
        L_0x0497:
            r8.close();	 Catch:{ IOException -> 0x049b }
        L_0x049a:
            throw r0;
        L_0x049b:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x049a;
        L_0x04a0:
            r0 = com.umeng.common.net.DownloadingService.v;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r0.get(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = (android.os.Messenger) r0;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = 0;
            r2 = 5;
            r3 = 0;
            r4 = 0;
            r1 = android.os.Message.obtain(r1, r2, r3, r4);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.send(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0 = new android.app.Notification;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = 17301634; // 0x1080082 float:2.497962E-38 double:8.548143E-317;
            r2 = " 下载失败。";
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.<init>(r1, r2, r4);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = 0;
            r3 = new android.content.Intent;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4 = 0;
            r1 = android.app.PendingIntent.getActivity(r1, r2, r3, r4);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = r13.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r3 = com.umeng.common.b.w(r3);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4.<init>();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r5 = r13.j;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r5 = r5.b;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4 = r4.append(r5);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r5 = " 下载失败。";
            r4 = r4.append(r5);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r4 = r4.toString();	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.setLatestEventInfo(r2, r3, r4, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r0.flags;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r1 | 16;
            r0.flags = r1;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r13.a;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1 = r1.p;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r1.notify(r2, r0);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r9 == 0) goto L_0x0513;
        L_0x0510:
            r9.close();	 Catch:{ IOException -> 0x0520 }
        L_0x0513:
            if (r8 == 0) goto L_0x019c;
        L_0x0515:
            r8.close();	 Catch:{ IOException -> 0x051a }
            goto L_0x019c;
        L_0x051a:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0520:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0531 }
            if (r8 == 0) goto L_0x019c;
        L_0x0526:
            r8.close();	 Catch:{ IOException -> 0x052b }
            goto L_0x019c;
        L_0x052b:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0531:
            r0 = move-exception;
            if (r8 == 0) goto L_0x0537;
        L_0x0534:
            r8.close();	 Catch:{ IOException -> 0x0538 }
        L_0x0537:
            throw r0;
        L_0x0538:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x0537;
        L_0x053d:
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            if (r0 == 0) goto L_0x0548;
        L_0x0541:
            r0 = r13.i;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r2 = r13.h;	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
            r0.a(r2, r1);	 Catch:{ IOException -> 0x0233, RemoteException -> 0x0466 }
        L_0x0548:
            if (r9 == 0) goto L_0x054d;
        L_0x054a:
            r9.close();	 Catch:{ IOException -> 0x055a }
        L_0x054d:
            if (r8 == 0) goto L_0x019c;
        L_0x054f:
            r8.close();	 Catch:{ IOException -> 0x0554 }
            goto L_0x019c;
        L_0x0554:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x055a:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x056b }
            if (r8 == 0) goto L_0x019c;
        L_0x0560:
            r8.close();	 Catch:{ IOException -> 0x0565 }
            goto L_0x019c;
        L_0x0565:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x056b:
            r0 = move-exception;
            if (r8 == 0) goto L_0x0571;
        L_0x056e:
            r8.close();	 Catch:{ IOException -> 0x0572 }
        L_0x0571:
            throw r0;
        L_0x0572:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x0571;
        L_0x0577:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x05a3 }
            r0 = r13.a;	 Catch:{ all -> 0x0595 }
            r4 = r13.h;	 Catch:{ all -> 0x0595 }
            r0.a(r4);	 Catch:{ all -> 0x0595 }
            r13.a(r1);	 Catch:{ all -> 0x0595 }
            r0 = r13.a;	 Catch:{ all -> 0x0595 }
            r0 = r0.s;	 Catch:{ all -> 0x0595 }
            r1 = new com.umeng.common.net.j;	 Catch:{ all -> 0x0595 }
            r1.<init>(r13);	 Catch:{ all -> 0x0595 }
            r0.post(r1);	 Catch:{ all -> 0x0595 }
            goto L_0x028a;
        L_0x0595:
            r0 = move-exception;
            r8 = r2;
            r9 = r3;
        L_0x0598:
            if (r9 == 0) goto L_0x059d;
        L_0x059a:
            r9.close();	 Catch:{ IOException -> 0x06a8 }
        L_0x059d:
            if (r8 == 0) goto L_0x05a2;
        L_0x059f:
            r8.close();	 Catch:{ IOException -> 0x06a2 }
        L_0x05a2:
            throw r0;
        L_0x05a3:
            r0 = move-exception;
            r4 = r13.a;	 Catch:{ all -> 0x0595 }
            r5 = r13.h;	 Catch:{ all -> 0x0595 }
            r4.a(r5);	 Catch:{ all -> 0x0595 }
            r13.a(r1);	 Catch:{ all -> 0x0595 }
            r1 = r13.a;	 Catch:{ all -> 0x0595 }
            r1 = r1.s;	 Catch:{ all -> 0x0595 }
            r4 = new com.umeng.common.net.j;	 Catch:{ all -> 0x0595 }
            r4.<init>(r13);	 Catch:{ all -> 0x0595 }
            r1.post(r4);	 Catch:{ all -> 0x0595 }
            throw r0;	 Catch:{ all -> 0x0595 }
        L_0x05bd:
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ all -> 0x0595 }
            r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0595 }
            r1.<init>();	 Catch:{ all -> 0x0595 }
            r4 = "wait for repeating Test network repeat count=";
            r1 = r1.append(r4);	 Catch:{ all -> 0x0595 }
            r4 = r13.d;	 Catch:{ all -> 0x0595 }
            r1 = r1.append(r4);	 Catch:{ all -> 0x0595 }
            r1 = r1.toString();	 Catch:{ all -> 0x0595 }
            com.umeng.common.Log.c(r0, r1);	 Catch:{ all -> 0x0595 }
            r0 = r13.j;	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r0.g;	 Catch:{ InterruptedException -> 0x05f3 }
            if (r0 != 0) goto L_0x0606;
        L_0x05e0:
            r0 = 8000; // 0x1f40 float:1.121E-41 double:3.9525E-320;
            java.lang.Thread.sleep(r0);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r13.f;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = 1;
            r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
            if (r0 >= 0) goto L_0x0600;
        L_0x05ed:
            r0 = 0;
            r13.a(r0);	 Catch:{ InterruptedException -> 0x05f3 }
            goto L_0x028a;
        L_0x05f3:
            r0 = move-exception;
            r13.a(r0);	 Catch:{ all -> 0x0595 }
            r0 = r13.a;	 Catch:{ all -> 0x0595 }
            r1 = r13.h;	 Catch:{ all -> 0x0595 }
            r0.a(r1);	 Catch:{ all -> 0x0595 }
            goto L_0x028a;
        L_0x0600:
            r0 = 1;
            r13.a(r0);	 Catch:{ InterruptedException -> 0x05f3 }
            goto L_0x028a;
        L_0x0606:
            r0 = com.umeng.common.net.DownloadingService.w;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = r13.h;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = java.lang.Integer.valueOf(r1);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r0.get(r1);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = (com.umeng.common.net.DownloadingService.d) r0;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = r0.f;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = 0;
            r6 = r13.e;	 Catch:{ InterruptedException -> 0x05f3 }
            r1[r4] = r6;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = r0.f;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = 1;
            r6 = r13.f;	 Catch:{ InterruptedException -> 0x05f3 }
            r1[r4] = r6;	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r0.f;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = 2;
            r4 = r13.d;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = (long) r4;	 Catch:{ InterruptedException -> 0x05f3 }
            r0[r1] = r4;	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r13.h;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = "continue";
            r0 = com.umeng.common.net.l.a(r0, r1);	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = new android.content.Intent;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = r13.b;	 Catch:{ InterruptedException -> 0x05f3 }
            r5 = com.umeng.common.net.DownloadingService.class;
            r1.<init>(r4, r5);	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = "com.umeng.broadcast.download.msg";
            r1.putExtra(r4, r0);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r13.a;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = r13.b;	 Catch:{ InterruptedException -> 0x05f3 }
            r0.a(r4, r1);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = r13.a;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = r13.b;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = r13.b;	 Catch:{ InterruptedException -> 0x05f3 }
            r4 = com.umeng.common.a.c.c(r4);	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = r1.getString(r4);	 Catch:{ InterruptedException -> 0x05f3 }
            r0.a(r1);	 Catch:{ InterruptedException -> 0x05f3 }
            r0 = com.umeng.common.net.DownloadingService.o;	 Catch:{ InterruptedException -> 0x05f3 }
            r1 = "changed play state button on op-notification.";
            com.umeng.common.Log.c(r0, r1);	 Catch:{ InterruptedException -> 0x05f3 }
            goto L_0x028a;
        L_0x0668:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0679 }
            if (r2 == 0) goto L_0x019c;
        L_0x066e:
            r2.close();	 Catch:{ IOException -> 0x0673 }
            goto L_0x019c;
        L_0x0673:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0679:
            r0 = move-exception;
            if (r2 == 0) goto L_0x067f;
        L_0x067c:
            r2.close();	 Catch:{ IOException -> 0x0680 }
        L_0x067f:
            throw r0;
        L_0x0680:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x067f;
        L_0x0685:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x0696 }
            if (r8 == 0) goto L_0x019c;
        L_0x068b:
            r8.close();	 Catch:{ IOException -> 0x0690 }
            goto L_0x019c;
        L_0x0690:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x019c;
        L_0x0696:
            r0 = move-exception;
            if (r8 == 0) goto L_0x069c;
        L_0x0699:
            r8.close();	 Catch:{ IOException -> 0x069d }
        L_0x069c:
            throw r0;
        L_0x069d:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x069c;
        L_0x06a2:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x05a2;
        L_0x06a8:
            r1 = move-exception;
            r1.printStackTrace();	 Catch:{ all -> 0x06b9 }
            if (r8 == 0) goto L_0x05a2;
        L_0x06ae:
            r8.close();	 Catch:{ IOException -> 0x06b3 }
            goto L_0x05a2;
        L_0x06b3:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x05a2;
        L_0x06b9:
            r0 = move-exception;
            if (r8 == 0) goto L_0x06bf;
        L_0x06bc:
            r8.close();	 Catch:{ IOException -> 0x06c0 }
        L_0x06bf:
            throw r0;
        L_0x06c0:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x06bf;
        L_0x06c5:
            r0 = move-exception;
            r8 = r1;
            r9 = r2;
            goto L_0x0598;
        L_0x06ca:
            r0 = move-exception;
            r8 = r1;
            r9 = r2;
            goto L_0x0598;
        L_0x06cf:
            r0 = move-exception;
            r9 = r2;
            goto L_0x0598;
        L_0x06d3:
            r0 = move-exception;
            goto L_0x0598;
        L_0x06d6:
            r0 = move-exception;
            r8 = r1;
            r9 = r2;
            goto L_0x0467;
        L_0x06db:
            r0 = move-exception;
            r8 = r1;
            r9 = r2;
            goto L_0x0467;
        L_0x06e0:
            r0 = move-exception;
            r9 = r2;
            goto L_0x0467;
        L_0x06e4:
            r0 = move-exception;
            r3 = r2;
            r2 = r1;
            r1 = r0;
            goto L_0x0237;
        L_0x06ea:
            r0 = move-exception;
            r3 = r2;
            r2 = r1;
            r1 = r0;
            goto L_0x0237;
        L_0x06f0:
            r0 = move-exception;
            r1 = r0;
            r3 = r2;
            r2 = r8;
            goto L_0x0237;
        L_0x06f6:
            r2 = r0;
            goto L_0x01ce;
        L_0x06f9:
            r0 = r3;
            goto L_0x013f;
        L_0x06fc:
            r0 = r1;
            goto L_0x0161;
        L_0x06ff:
            r3 = r0;
            goto L_0x0036;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.umeng.common.net.DownloadingService.b.a(boolean):void");
        }

        private void a(Exception exception) {
            Log.b(DownloadingService.o, "can not install. " + exception.getMessage());
            if (this.i != null) {
                this.i.a(this.h, exception);
            }
            this.a.a(this.j, this.e, this.f, (long) this.d);
        }
    }

    class c extends Handler {
        final /* synthetic */ DownloadingService a;

        c(DownloadingService downloadingService) {
            this.a = downloadingService;
        }

        public void handleMessage(Message message) {
            Log.c(DownloadingService.o, "IncomingHandler(msg.what:" + message.what + " msg.arg1:" + message.arg1 + " msg.arg2:" + message.arg2 + " msg.replyTo:" + message.replyTo);
            switch (message.what) {
                case 4:
                    Bundle data = message.getData();
                    Log.c(DownloadingService.o, "IncomingHandler(msg.getData():" + data);
                    a a = a.a(data);
                    if (DownloadingService.d(a)) {
                        Log.a(DownloadingService.o, a.b + " is already in downloading list. ");
                        Toast.makeText(this.a.r, com.umeng.common.a.c.b(this.a.r), 0).show();
                        Message obtain = Message.obtain();
                        obtain.what = 5;
                        obtain.arg1 = 2;
                        obtain.arg2 = 0;
                        try {
                            message.replyTo.send(obtain);
                            return;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    DownloadingService.v.put(a, message.replyTo);
                    this.a.c(a);
                    return;
                default:
                    super.handleMessage(message);
                    return;
            }
        }
    }

    private static class d {
        b a;
        Notification b;
        int c;
        int d;
        a e;
        long[] f = new long[3];

        public d(a aVar, int i) {
            this.c = i;
            this.e = aVar;
        }

        public void a() {
            DownloadingService.w.put(Integer.valueOf(this.c), this);
        }

        public void b() {
            if (DownloadingService.w.containsKey(Integer.valueOf(this.c))) {
                DownloadingService.w.remove(Integer.valueOf(this.c));
            }
        }
    }

    private class e extends AsyncTask<String, Void, Integer> {
        public int a;
        public String b;
        final /* synthetic */ DownloadingService c;
        private a d;

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return a((String[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            a((Integer) obj);
        }

        public e(DownloadingService downloadingService, int i, a aVar, String str) {
            this.c = downloadingService;
            this.a = i;
            this.d = aVar;
            this.b = str;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Integer a(String... strArr) {
            int a = DeltaUpdate.a(strArr[0], strArr[1], strArr[2]) + 1;
            new File(strArr[2]).delete();
            if (a != 1) {
                Log.a(DownloadingService.o, "file patch error");
            } else if (g.a(new File(strArr[1])).equalsIgnoreCase(this.d.e)) {
                Log.a(DownloadingService.o, "file patch success");
            } else {
                Log.a(DownloadingService.o, "file patch error");
                return Integer.valueOf(0);
            }
            return Integer.valueOf(a);
        }

        protected void a(Integer num) {
            Message obtain;
            if (num.intValue() == 1) {
                Notification notification = new Notification(17301634, com.umeng.common.a.p, System.currentTimeMillis());
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(268435456);
                intent.setDataAndType(Uri.fromFile(new File(this.b)), "application/vnd.android.package-archive");
                notification.setLatestEventInfo(this.c.r, com.umeng.common.b.w(this.c.r), com.umeng.common.a.p, PendingIntent.getActivity(this.c.r, 0, intent, 134217728));
                notification.flags = 16;
                this.c.p.notify(this.a + 1, notification);
                if (DownloadingService.b(this.c.r)) {
                    this.c.p.cancel(this.a + 1);
                    this.c.r.startActivity(intent);
                }
                Bundle bundle = new Bundle();
                bundle.putString(DownloadingService.l, this.b);
                obtain = Message.obtain();
                obtain.what = 5;
                obtain.arg1 = 1;
                obtain.arg2 = this.a;
                obtain.setData(bundle);
                try {
                    if (DownloadingService.v.get(this.d) != null) {
                        ((Messenger) DownloadingService.v.get(this.d)).send(obtain);
                    }
                    this.c.a(this.a);
                    return;
                } catch (RemoteException e) {
                    this.c.a(this.a);
                    return;
                }
            }
            this.c.p.cancel(this.a + 1);
            bundle = new Bundle();
            bundle.putString(DownloadingService.l, this.b);
            obtain = Message.obtain();
            obtain.what = 5;
            obtain.arg1 = 3;
            obtain.arg2 = this.a;
            obtain.setData(bundle);
            try {
                if (DownloadingService.v.get(this.d) != null) {
                    ((Messenger) DownloadingService.v.get(this.d)).send(obtain);
                }
                this.c.a(this.a);
            } catch (RemoteException e2) {
                this.c.a(this.a);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        Log.c(o, "onBind ");
        return this.n.getBinder();
    }

    public void onStart(Intent intent, int i) {
        Log.c(o, "onStart ");
        a(getApplicationContext(), intent);
        super.onStart(intent, i);
    }

    public void onCreate() {
        super.onCreate();
        if (m) {
            Log.LOG = true;
            Debug.waitForDebugger();
        }
        Log.c(o, "onCreate ");
        this.p = (NotificationManager) getSystemService("notification");
        this.r = this;
        this.s = new d(this);
        this.t = new e(this);
    }

    private void a(int i) {
        d dVar = (d) w.get(Integer.valueOf(i));
        if (dVar != null) {
            Log.c(o, "download service clear cache " + dVar.e.b);
            if (dVar.a != null) {
                dVar.a.a(2);
            }
            this.p.cancel(dVar.c);
            if (v.containsKey(dVar.e)) {
                v.remove(dVar.e);
            }
            dVar.b();
            e();
        }
    }

    private void d() {
        IntentFilter intentFilter = new IntentFilter(l.d);
        this.x = new f(this);
        registerReceiver(this.x, intentFilter);
    }

    private void c(a aVar) {
        Log.c(o, "startDownload([mComponentName:" + aVar.a + " mTitle:" + aVar.b + " mUrl:" + aVar.c + "])");
        int a = a(aVar);
        b bVar = new b(this, getApplicationContext(), aVar, a, 0, this.t);
        d dVar = new d(aVar, a);
        dVar.a();
        dVar.a = bVar;
        bVar.start();
        e();
        if (m) {
            for (Integer num : w.keySet()) {
                Log.c(o, "Running task " + ((d) w.get(num)).e.b);
            }
        }
    }

    public static int a(a aVar) {
        return Math.abs((int) (((long) ((aVar.b.hashCode() >> 2) + (aVar.c.hashCode() >> 3))) + System.currentTimeMillis()));
    }

    private void a(a aVar, long j, long j2, long j3) {
        if (aVar.f != null) {
            Map hashMap = new HashMap();
            hashMap.put("dsize", String.valueOf(j));
            hashMap.put("dtime", g.a().split(" ")[1]);
            float f = 0.0f;
            if (j2 > 0) {
                f = ((float) j) / ((float) j2);
            }
            hashMap.put("dpcent", String.valueOf((int) (f * 100.0f)));
            hashMap.put("ptimes", String.valueOf(j3));
            b(hashMap, false, aVar.f);
        }
    }

    public void a(String str) {
        synchronized (y) {
            if (!y.booleanValue()) {
                Log.c(o, "show single toast.[" + str + "]");
                y = Boolean.valueOf(true);
                this.s.post(new g(this, str));
                this.s.postDelayed(new h(this), 1200);
            }
        }
    }

    private static boolean b(Context context) {
        List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        String packageName = context.getPackageName();
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.importance == 100 && runningAppProcessInfo.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean d(a aVar) {
        if (m) {
            int nextInt = new Random().nextInt(1000);
            if (v != null) {
                for (a aVar2 : v.keySet()) {
                    Log.c(o, "_" + nextInt + " downling  " + aVar2.b + "   " + aVar2.c);
                }
            } else {
                Log.c(o, "_" + nextInt + "downling  null");
            }
        }
        if (v == null) {
            return false;
        }
        for (a aVar22 : v.keySet()) {
            if (aVar.e != null && aVar.e.equals(aVar22.e)) {
                return true;
            }
            if (aVar22.c.equals(aVar.c)) {
                return true;
            }
        }
        return false;
    }

    public void onDestroy() {
        try {
            c.a(getApplicationContext()).a(259200);
            c.a(getApplicationContext()).finalize();
            if (this.x != null) {
                unregisterReceiver(this.x);
            }
        } catch (Exception e) {
            Log.b(o, e.getMessage());
        }
        super.onDestroy();
    }

    private Notification a(a aVar, int i, int i2) {
        Context applicationContext = getApplicationContext();
        Notification notification = new Notification(17301633, com.umeng.common.a.m, 1);
        RemoteViews remoteViews = new RemoteViews(applicationContext.getPackageName(), com.umeng.common.a.b.a(applicationContext));
        remoteViews.setProgressBar(com.umeng.common.a.a.c(applicationContext), 100, i2, false);
        remoteViews.setTextViewText(com.umeng.common.a.a.b(applicationContext), i2 + "%");
        remoteViews.setTextViewText(com.umeng.common.a.a.d(applicationContext), applicationContext.getResources().getString(com.umeng.common.a.c.g(applicationContext.getApplicationContext())) + aVar.b);
        remoteViews.setTextViewText(com.umeng.common.a.a.a(applicationContext), "");
        remoteViews.setImageViewResource(com.umeng.common.a.a.e(applicationContext), 17301633);
        notification.contentView = remoteViews;
        notification.contentIntent = PendingIntent.getActivity(applicationContext, 0, new Intent(), 134217728);
        if (aVar.g) {
            notification.flags = 2;
            remoteViews.setOnClickPendingIntent(com.umeng.common.a.a.f(applicationContext), l.b(getApplicationContext(), l.a(i, l.b)));
            remoteViews.setViewVisibility(com.umeng.common.a.a.f(applicationContext), 0);
            b(notification, i);
            PendingIntent b = l.b(getApplicationContext(), l.a(i, l.c));
            remoteViews.setViewVisibility(com.umeng.common.a.a.h(applicationContext), 0);
            remoteViews.setOnClickPendingIntent(com.umeng.common.a.a.h(applicationContext), b);
        } else {
            notification.flags = 16;
            remoteViews.setViewVisibility(com.umeng.common.a.a.f(applicationContext), 8);
            remoteViews.setViewVisibility(com.umeng.common.a.a.h(applicationContext), 8);
        }
        return notification;
    }

    private static final void b(Map<String, String> map, boolean z, String[] strArr) {
        new Thread(new i(strArr, z, map)).start();
    }

    private void a(Notification notification, int i) {
        int f = com.umeng.common.a.a.f(this.r);
        notification.contentView.setTextViewText(f, this.r.getResources().getString(com.umeng.common.a.c.e(this.r.getApplicationContext())));
        notification.contentView.setInt(f, "setBackgroundResource", com.umeng.common.c.a(this.r).d("umeng_common_gradient_green"));
        this.p.notify(i, notification);
    }

    private void b(Notification notification, int i) {
        int f = com.umeng.common.a.a.f(this.r);
        notification.contentView.setTextViewText(f, this.r.getResources().getString(com.umeng.common.a.c.d(this.r.getApplicationContext())));
        notification.contentView.setInt(f, "setBackgroundResource", com.umeng.common.c.a(this.r).d("umeng_common_gradient_orange"));
        this.p.notify(i, notification);
    }

    private void e() {
        if (m) {
            int size = v.size();
            int size2 = w.size();
            Log.a(o, "Client size =" + size + "   cacheSize = " + size2);
            if (size != size2) {
                throw new RuntimeException("Client size =" + size + "   cacheSize = " + size2);
            }
        }
    }

    private boolean a(Context context, Intent intent) {
        int parseInt;
        try {
            String[] split = intent.getExtras().getString(l.e).split(":");
            parseInt = Integer.parseInt(split[0]);
            CharSequence trim = split[1].trim();
            if (!(parseInt == 0 || TextUtils.isEmpty(trim) || !w.containsKey(Integer.valueOf(parseInt)))) {
                d dVar = (d) w.get(Integer.valueOf(parseInt));
                b bVar = dVar.a;
                if (l.b.equals(trim)) {
                    if (bVar == null) {
                        Log.c(o, "Receive action do play click.");
                        if (!com.umeng.common.b.a(context, "android.permission.ACCESS_NETWORK_STATE") || com.umeng.common.b.n(context)) {
                            bVar = new b(this, context, dVar.e, parseInt, dVar.d, this.t);
                            dVar.a = bVar;
                            bVar.start();
                            b(dVar.b, parseInt);
                            return true;
                        }
                        Toast.makeText(context, context.getResources().getString(com.umeng.common.a.c.a(context.getApplicationContext())), 1).show();
                        return false;
                    }
                    Log.c(o, "Receive action do play click.");
                    bVar.a(1);
                    dVar.a = null;
                    a(dVar.b, parseInt);
                    return true;
                } else if (l.c.equals(trim)) {
                    Log.c(o, "Receive action do stop click.");
                    if (bVar != null) {
                        bVar.a(2);
                    } else {
                        a(dVar.e, dVar.f[0], dVar.f[1], dVar.f[2]);
                    }
                    a(parseInt);
                    return true;
                }
            }
        } catch (Exception e) {
            a(parseInt);
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable th) {
            a(parseInt);
        }
        return false;
    }
}
