package com.umeng.common.net;

import android.app.Notification;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.umeng.common.Log;
import com.umeng.common.a.a;

/* compiled from: DownloadingService */
class e implements a {
    final /* synthetic */ DownloadingService a;

    e(DownloadingService downloadingService) {
        this.a = downloadingService;
    }

    public void a(int i) {
        int i2 = 0;
        if (DownloadingService.w.containsKey(Integer.valueOf(i))) {
            d dVar = (d) DownloadingService.w.get(Integer.valueOf(i));
            long[] jArr = dVar.f;
            if (jArr != null && jArr[1] > 0) {
                i2 = (int) ((((float) jArr[0]) / ((float) jArr[1])) * 100.0f);
                if (i2 > 100) {
                    i2 = 99;
                }
            }
            Notification a = this.a.a(dVar.e, i, i2);
            dVar.b = a;
            this.a.p.notify(i, a);
        }
    }

    public void a(int i, int i2) {
        if (DownloadingService.w.containsKey(Integer.valueOf(i))) {
            d dVar = (d) DownloadingService.w.get(Integer.valueOf(i));
            a aVar = dVar.e;
            Notification notification = dVar.b;
            notification.contentView.setProgressBar(a.c(this.a.r), 100, i2, false);
            notification.contentView.setTextViewText(a.b(this.a.r), String.valueOf(i2) + "%");
            this.a.p.notify(i, notification);
            Log.c(DownloadingService.o, String.format("%3$10s Notification: mNotificationId = %1$15s\t|\tprogress = %2$15s", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), aVar.b}));
        }
    }

    public void a(int i, String str) {
        if (DownloadingService.w.containsKey(Integer.valueOf(i))) {
            d dVar = (d) DownloadingService.w.get(Integer.valueOf(i));
            if (dVar != null) {
                a aVar = dVar.e;
                dVar.b.contentView.setTextViewText(a.b(this.a.r), String.valueOf(100) + "%");
                c.a(this.a.r).a(aVar.a, aVar.c, 100);
                Bundle bundle = new Bundle();
                bundle.putString("filename", str);
                Message obtain;
                if (aVar.a.equalsIgnoreCase("delta_update")) {
                    obtain = Message.obtain();
                    obtain.what = 6;
                    obtain.arg1 = 1;
                    obtain.obj = aVar;
                    obtain.arg2 = i;
                    obtain.setData(bundle);
                    this.a.s.sendMessage(obtain);
                    return;
                }
                obtain = Message.obtain();
                obtain.what = 5;
                obtain.arg1 = 1;
                obtain.obj = aVar;
                obtain.arg2 = i;
                obtain.setData(bundle);
                this.a.s.sendMessage(obtain);
                obtain = Message.obtain();
                obtain.what = 5;
                obtain.arg1 = 1;
                obtain.arg2 = i;
                obtain.setData(bundle);
                try {
                    if (DownloadingService.v.get(aVar) != null) {
                        ((Messenger) DownloadingService.v.get(aVar)).send(obtain);
                    }
                    this.a.a(i);
                } catch (RemoteException e) {
                    this.a.a(i);
                }
            }
        }
    }

    public void a(int i, Exception exception) {
        if (DownloadingService.w.containsKey(Integer.valueOf(i))) {
            d dVar = (d) DownloadingService.w.get(Integer.valueOf(i));
            a aVar = dVar.e;
            Notification notification = dVar.b;
            notification.contentView.setTextViewText(a.d(this.a.r), aVar.b + com.umeng.common.a.l);
            this.a.p.notify(i, notification);
            this.a.a(i);
        }
    }
}
