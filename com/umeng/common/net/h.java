package com.umeng.common.net;

/* compiled from: DownloadingService */
class h implements Runnable {
    final /* synthetic */ DownloadingService a;

    h(DownloadingService downloadingService) {
        this.a = downloadingService;
    }

    public void run() {
        DownloadingService.y = Boolean.valueOf(false);
    }
}
