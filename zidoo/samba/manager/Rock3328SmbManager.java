package zidoo.samba.manager;

import android.content.Context;
import java.io.File;
import zidoo.tool.BlkidManager;

public class Rock3328SmbManager extends SambaManager {
    Rock3328SmbManager(Context context) {
        super(context);
    }

    public String getSmbRoot() {
        return "/mnt/smb";
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("//" + param[0] + "/" + param[1].replaceAll(" ", "\\\\040"), param[2].replaceAll(" ", "\\\\040"));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean mountSmb(java.lang.String r14, java.lang.String r15, java.lang.String r16, java.lang.String r17, java.lang.String r18) {
        /*
        r13 = this;
        r13.mkdirs(r14);
        r0 = zidoo.tool.BlkidManager.getInstance();
        r1 = r16;
        r2 = r17;
        r3 = r18;
        r4 = r14;
        r5 = r15;
        r0.mountSmb(r1, r2, r3, r4, r5);
        r0 = 47;
        r8 = r14.lastIndexOf(r0);	 Catch:{ IllegalArgumentException -> 0x0043 }
        r0 = -1;
        if (r8 != r0) goto L_0x0037;
    L_0x001b:
        r12 = r14;
    L_0x001c:
        r10 = 0;
        r11 = r10;
    L_0x001e:
        r0 = 50;
        java.lang.Thread.sleep(r0);	 Catch:{ InterruptedException -> 0x003e }
    L_0x0023:
        r0 = 3;
        r0 = new java.lang.String[r0];	 Catch:{ IllegalArgumentException -> 0x0043 }
        r1 = 0;
        r0[r1] = r16;	 Catch:{ IllegalArgumentException -> 0x0043 }
        r1 = 1;
        r0[r1] = r12;	 Catch:{ IllegalArgumentException -> 0x0043 }
        r1 = 2;
        r0[r1] = r15;	 Catch:{ IllegalArgumentException -> 0x0043 }
        r0 = r13.isMounted(r0);	 Catch:{ IllegalArgumentException -> 0x0043 }
        if (r0 == 0) goto L_0x0057;
    L_0x0035:
        r0 = 1;
    L_0x0036:
        return r0;
    L_0x0037:
        r0 = r8 + 1;
        r12 = r14.substring(r0);	 Catch:{ IllegalArgumentException -> 0x0043 }
        goto L_0x001c;
    L_0x003e:
        r7 = move-exception;
        r7.printStackTrace();	 Catch:{ IllegalArgumentException -> 0x0043 }
        goto L_0x0023;
    L_0x0043:
        r6 = move-exception;
        r6.printStackTrace();
    L_0x0047:
        r9 = new java.io.File;
        r9.<init>(r15);
        r0 = r9.exists();
        if (r0 == 0) goto L_0x0055;
    L_0x0052:
        r9.delete();
    L_0x0055:
        r0 = 0;
        goto L_0x0036;
    L_0x0057:
        r10 = r11 + 1;
        r0 = 20;
        if (r11 >= r0) goto L_0x0047;
    L_0x005d:
        r11 = r10;
        goto L_0x001e;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.samba.manager.Rock3328SmbManager.mountSmb(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    private void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        }
    }

    public boolean unMountSmb(File file) {
        BlkidManager.getInstance().umount(file.getPath());
        return true;
    }
}
