package zidoo.nfs;

import android.content.Context;
import java.io.File;
import zidoo.device.DeviceUtils;

public class RockNfsManager extends NfsManager {
    private final String SHELL_ROOT = "/data/etc";

    RockNfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/data/nfs";
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean mountNfs(java.lang.String r8, java.lang.String r9, java.lang.String r10) {
        /*
        r7 = this;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = r7.getNfsRoot();
        r5 = r5.append(r6);
        r6 = "/";
        r5 = r5.append(r6);
        r5 = r5.append(r10);
        r1 = r5.toString();
        r5 = "/data/etc";
        r5 = r7.makeDirs(r5);
        if (r5 == 0) goto L_0x0035;
    L_0x0025:
        r5 = r7.getNfsRoot();
        r5 = r7.makeDirs(r5);
        if (r5 == 0) goto L_0x0035;
    L_0x002f:
        r5 = r7.makeDirs(r1);
        if (r5 != 0) goto L_0x0037;
    L_0x0035:
        r4 = 0;
    L_0x0036:
        return r4;
    L_0x0037:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "busybox mount -t nfs -o nolock \"";
        r5 = r5.append(r6);
        r5 = r5.append(r8);
        r6 = ":";
        r5 = r5.append(r6);
        r5 = r5.append(r9);
        r6 = "\" ";
        r5 = r5.append(r6);
        r5 = r5.append(r1);
        r0 = r5.toString();
        zidoo.device.DeviceUtils.execute(r0);
        r2 = 0;
    L_0x0065:
        r5 = 50;
        r7.sleep(r5);
        r4 = r7.isNfsMounted(r8, r9, r1);
        if (r4 != 0) goto L_0x0077;
    L_0x0070:
        r3 = r2 + 1;
        r5 = 10;
        if (r2 < r5) goto L_0x0082;
    L_0x0076:
        r2 = r3;
    L_0x0077:
        if (r4 != 0) goto L_0x0036;
    L_0x0079:
        r5 = new java.io.File;
        r5.<init>(r1);
        r5.delete();
        goto L_0x0036;
    L_0x0082:
        r2 = r3;
        goto L_0x0065;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.nfs.RockNfsManager.mountNfs(java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    public boolean umountNfs(File file) {
        String mountPoint = file.getPath();
        if (!new File(mountPoint.replace("\"", "")).exists()) {
            return true;
        }
        int n;
        DeviceUtils.execute("busybox umount -fl " + mountPoint);
        int n2 = 0;
        while (true) {
            sleep(50);
            boolean exist = NfsManager.isNfsMounted(file.getPath(), true);
            if (!exist) {
                break;
            }
            n = n2 + 1;
            if (n2 >= 10) {
                break;
            }
            n2 = n;
        }
        n2 = n;
        if (!exist) {
            new File(mountPoint).delete();
        }
        if (exist) {
            return false;
        }
        return true;
    }

    private boolean makeDirs(String path) {
        File dir = new File(path);
        return dir.exists() || dir.mkdirs();
    }

    private void sleep(int time) {
        try {
            Thread.sleep((long) time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
