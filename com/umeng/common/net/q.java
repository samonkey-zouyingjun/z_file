package com.umeng.common.net;

import java.io.File;

/* compiled from: ResUtil */
final class q implements Runnable {
    final /* synthetic */ File a;

    q(File file) {
        this.a = file;
    }

    public void run() {
        p.c(this.a);
        p.f = null;
    }
}
