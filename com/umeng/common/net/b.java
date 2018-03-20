package com.umeng.common.net;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.umeng.common.Log;

/* compiled from: DownloadAgent */
class b implements ServiceConnection {
    final /* synthetic */ a a;

    b(a aVar) {
        this.a = aVar;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.c(a.b, "ServiceConnection.onServiceConnected");
        this.a.e = new Messenger(iBinder);
        if (this.a.d != null) {
            this.a.d.a();
        }
        try {
            Message obtain = Message.obtain(null, 4);
            a aVar = new a(this.a.f, this.a.g, this.a.h);
            aVar.d = this.a.i;
            aVar.e = this.a.j;
            aVar.f = this.a.k;
            aVar.g = this.a.l;
            obtain.setData(aVar.a());
            obtain.replyTo = this.a.a;
            this.a.e.send(obtain);
        } catch (RemoteException e) {
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Log.c(a.b, "ServiceConnection.onServiceDisconnected");
        this.a.e = null;
    }
}
