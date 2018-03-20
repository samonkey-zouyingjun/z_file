package com.umeng.common.net;

import android.widget.Toast;
import com.umeng.common.a.c;

/* compiled from: DownloadingService */
class j implements Runnable {
    final /* synthetic */ b a;

    j(b bVar) {
        this.a = bVar;
    }

    public void run() {
        Toast.makeText(this.a.a, c.h(this.a.b), 0).show();
    }
}
