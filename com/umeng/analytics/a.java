package com.umeng.analytics;

import android.content.Context;
import com.umeng.common.Log;
import java.lang.Thread.UncaughtExceptionHandler;

/* compiled from: CrashHandler */
class a implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler a;
    private h b;
    private Context c;

    public void a(Context context) {
        if (Thread.getDefaultUncaughtExceptionHandler() != this) {
            this.c = context.getApplicationContext();
            this.a = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void a(h hVar) {
        this.b = hVar;
    }

    public void uncaughtException(Thread thread, Throwable th) {
        a(th);
        if (this.a != null) {
            this.a.uncaughtException(thread, th);
        }
    }

    private void a(Throwable th) {
        if (th == null) {
            Log.e(f.q, "Exception is null in handleException");
        } else {
            this.b.b(this.c, th);
        }
    }
}
