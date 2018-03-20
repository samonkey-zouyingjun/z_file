package com.umeng.common.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* compiled from: DownloadingService */
class f extends BroadcastReceiver {
    final /* synthetic */ DownloadingService a;

    f(DownloadingService downloadingService) {
        this.a = downloadingService;
    }

    public void onReceive(Context context, Intent intent) {
        this.a.a(context, intent);
    }
}
