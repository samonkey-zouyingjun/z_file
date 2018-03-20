package com.umeng.common.net;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import com.umeng.common.Log;
import com.umeng.common.a;
import com.umeng.common.b;
import com.umeng.common.util.DeltaUpdate;
import java.io.File;

/* compiled from: DownloadingService */
class d extends Handler {
    final /* synthetic */ DownloadingService a;

    d(DownloadingService downloadingService) {
        this.a = downloadingService;
    }

    public void handleMessage(Message message) {
        a aVar;
        String string;
        switch (message.what) {
            case 5:
                aVar = (a) message.obj;
                int i = message.arg2;
                try {
                    string = message.getData().getString("filename");
                    Log.c(DownloadingService.o, "Cancel old notification....");
                    Notification notification = new Notification(17301634, a.o, System.currentTimeMillis());
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addFlags(268435456);
                    intent.setDataAndType(Uri.fromFile(new File(string)), "application/vnd.android.package-archive");
                    notification.setLatestEventInfo(this.a.r, aVar.b, a.o, PendingIntent.getActivity(this.a.r, 0, intent, 134217728));
                    notification.flags = 16;
                    this.a.p = (NotificationManager) this.a.getSystemService("notification");
                    this.a.p.notify(i + 1, notification);
                    Log.c(DownloadingService.o, "Show new  notification....");
                    boolean a = DownloadingService.b(this.a.r);
                    Log.c(DownloadingService.o, String.format("isAppOnForeground = %1$B", new Object[]{Boolean.valueOf(a)}));
                    if (a) {
                        this.a.p.cancel(i + 1);
                        this.a.r.startActivity(intent);
                    }
                    Log.a(DownloadingService.o, String.format("%1$10s downloaded. Saved to: %2$s", new Object[]{aVar.b, string}));
                    return;
                } catch (Exception e) {
                    Log.b(DownloadingService.o, "can not install. " + e.getMessage());
                    this.a.p.cancel(i + 1);
                    return;
                }
            case 6:
                aVar = (a) message.obj;
                int i2 = message.arg2;
                String string2 = message.getData().getString("filename");
                this.a.p.cancel(i2);
                Notification notification2 = new Notification(17301633, a.q, System.currentTimeMillis());
                notification2.setLatestEventInfo(this.a.r, b.w(this.a.r), a.q, PendingIntent.getActivity(this.a.r, 0, new Intent(), 134217728));
                this.a.p.notify(i2 + 1, notification2);
                string = string2.replace(".patch", ".apk");
                String a2 = DeltaUpdate.a(this.a);
                new e(this.a, i2, aVar, string).execute(new String[]{a2, string, string2});
                return;
            default:
                return;
        }
    }
}
