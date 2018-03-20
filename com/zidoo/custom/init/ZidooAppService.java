package com.zidoo.custom.init;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.zidoo.permissions.ZidooPhonePermissions;

public class ZidooAppService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("bob  ZidooAppService onStartCommand ");
        if (intent != null) {
            int cmd = intent.getIntExtra("cmd", 0);
            System.out.println("bob  ZidooAppService onStartCommand cmd = " + cmd);
            if (cmd == 0) {
                new ZidooPhonePermissions(this).initcheckPermissionsByUI(this, null, null);
            } else if (cmd == 0) {
                new ZidooPhonePermissions(this).initcheckPermissionsByModel(this, null, null);
            } else if (cmd == 1) {
                new ZidooPhonePermissions(this).initcheckPermissionsOnlyPictrue(this, null, null);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        try {
            startService(new Intent(this, ZidooAppService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
