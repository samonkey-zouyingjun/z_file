package zidoo.samba.manager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.io.File;

@SuppressLint({"NewApi"})
public class RTD1295SmbManager extends SambaManager {
    private final int MOUNT_SUCCESS = 933;
    private final String SMB_MOUNT_ACTION = "android.intent.action.SMB_MOUNT";
    private final String SMB_UMOUNT_ACTION = "android.intent.action.SMB_UMOUNT";
    private final BroadcastReceiver SmbStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            synchronized (RTD1295SmbManager.this) {
                RTD1295SmbManager.this.mResult = intent.getIntExtra("code", 0);
                Log.d("lisupan", "receive the mount result:" + RTD1295SmbManager.this.mResult);
                RTD1295SmbManager.this.notify();
            }
        }
    };
    private final int UMOUNT_SUCCESS = 935;
    int mResult = 0;

    RTD1295SmbManager(Context context) {
        super(context);
    }

    public String getSmbRoot() {
        return "/tmp/ramfs/mnt";
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("//" + param[0] + "/" + param[1].replaceAll(" ", "\\\\040"), param[2].replaceAll(" ", "\\\\040"));
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        boolean z = false;
        if (creatMountPoint(mountPoint)) {
            registerReceiver();
            synchronized (this) {
                this.mResult = 0;
                try {
                    this.mContext.sendBroadcast(new Intent("android.intent.action.SMB_MOUNT").putExtra("smb_mount_cmd", true).putExtra("smbPath", sharePath).putExtra("mountPath", mountPoint).putExtra("user", user).putExtra("passwd", pwd));
                    wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.mResult == 933) {
                    z = true;
                }
                unregisterReceiver();
            }
        }
        return z;
    }

    public boolean unMountSmbs(File parent) {
        this.mContext.sendBroadcast(new Intent("android.intent.action.SMB_UMOUNT").putExtra("smb_mount_cmd", true));
        return true;
    }

    public boolean unMountSmb(File file) {
        boolean success = true;
        registerReceiver();
        synchronized (this) {
            this.mResult = 0;
            this.mContext.sendBroadcast(new Intent("android.intent.action.SMB_UMOUNT").putExtra("smb_mount_cmd", true));
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.mResult != 935) {
                success = false;
            }
            unregisterReceiver();
        }
        return success;
    }

    private boolean creatMountPoint(String mountPoint) {
        File file = new File(mountPoint);
        if (file.exists()) {
            return true;
        }
        try {
            if (file.mkdirs()) {
                file.setReadable(true, false);
                file.setWritable(true, false);
                file.setExecutable(true, false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void registerReceiver() {
        this.mContext.registerReceiver(this.SmbStatusReceiver, new IntentFilter("android.rtksmb.resp_code"));
    }

    private void unregisterReceiver() {
        this.mContext.unregisterReceiver(this.SmbStatusReceiver);
    }
}
