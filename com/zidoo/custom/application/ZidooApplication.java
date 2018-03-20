package com.zidoo.custom.application;

import android.annotation.SuppressLint;
import android.app.Application;
import com.zidoo.custom.init.ZidooJarPermissions;
import com.zidoo.custom.log.MyLog;

@SuppressLint({"SimpleDateFormat"})
public class ZidooApplication extends Application {
    public void onCreate() {
        super.onCreate();
        init(null);
    }

    public void onCreate(String textSytle) {
        super.onCreate();
        init(textSytle);
    }

    private void init(String textSytle) {
        MyLog.v(getPackageName() + "  ZidooApplication  init");
        ZidooJarPermissions.initZidooJar(this, textSytle);
    }
}
