package jcifs.smb;

import android.support.v4.internal.view.SupportMenu;
import jcifs.Config;

public class BufferCache {
    private static final int MAX_BUFFERS = Config.getInt("jcifs.smb.maxBuffers", 16);
    static Object[] cache = new Object[MAX_BUFFERS];
    private static int freeBuffers = 0;

    public static byte[] getBuffer() {
        synchronized (cache) {
            byte[] buf;
            if (freeBuffers > 0) {
                for (int i = 0; i < MAX_BUFFERS; i++) {
                    if (cache[i] != null) {
                        buf = (byte[]) cache[i];
                        cache[i] = null;
                        freeBuffers--;
                        return buf;
                    }
                }
            }
            buf = new byte[SupportMenu.USER_MASK];
            return buf;
        }
    }

    static void getBuffers(SmbComTransaction req, SmbComTransactionResponse rsp) {
        synchronized (cache) {
            req.txn_buf = getBuffer();
            rsp.txn_buf = getBuffer();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void releaseBuffer(byte[] r4) {
        /*
        r2 = cache;
        monitor-enter(r2);
        r1 = freeBuffers;	 Catch:{ all -> 0x0025 }
        r3 = MAX_BUFFERS;	 Catch:{ all -> 0x0025 }
        if (r1 >= r3) goto L_0x0023;
    L_0x0009:
        r0 = 0;
    L_0x000a:
        r1 = MAX_BUFFERS;	 Catch:{ all -> 0x0025 }
        if (r0 >= r1) goto L_0x0023;
    L_0x000e:
        r1 = cache;	 Catch:{ all -> 0x0025 }
        r1 = r1[r0];	 Catch:{ all -> 0x0025 }
        if (r1 != 0) goto L_0x0020;
    L_0x0014:
        r1 = cache;	 Catch:{ all -> 0x0025 }
        r1[r0] = r4;	 Catch:{ all -> 0x0025 }
        r1 = freeBuffers;	 Catch:{ all -> 0x0025 }
        r1 = r1 + 1;
        freeBuffers = r1;	 Catch:{ all -> 0x0025 }
        monitor-exit(r2);	 Catch:{ all -> 0x0025 }
    L_0x001f:
        return;
    L_0x0020:
        r0 = r0 + 1;
        goto L_0x000a;
    L_0x0023:
        monitor-exit(r2);	 Catch:{ all -> 0x0025 }
        goto L_0x001f;
    L_0x0025:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0025 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.BufferCache.releaseBuffer(byte[]):void");
    }
}
