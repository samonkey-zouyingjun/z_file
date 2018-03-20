package com.umeng.common.net;

import android.widget.Toast;

/* compiled from: DownloadingService */
class g implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ DownloadingService b;

    g(DownloadingService downloadingService, String str) {
        this.b = downloadingService;
        this.a = str;
    }

    public void run() {
        Toast.makeText(this.b.r, this.a, 0).show();
    }
}
