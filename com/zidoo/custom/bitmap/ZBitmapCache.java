package com.zidoo.custom.bitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.LruCache;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

@SuppressLint({"NewApi"})
public class ZBitmapCache {
    public static final String TAG = "BitmapCache";
    private static final LruCache<String, Bitmap> sHardBitmapCache = new AnonymousClass1(16777216);
    private static final LinkedHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new AnonymousClass2(40, 0.75f, true);

    class AnonymousClass1 extends LruCache<String, Bitmap> {
        AnonymousClass1(int $anonymous0) {
            super($anonymous0);
        }

        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }

        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            ZBitmapCache.sSoftBitmapCache.put(key, new SoftReference(oldValue));
        }
    }

    class AnonymousClass2 extends LinkedHashMap<String, SoftReference<Bitmap>> {
        AnonymousClass2(int $anonymous0, float $anonymous1, boolean $anonymous2) {
            super($anonymous0, $anonymous1, $anonymous2);
        }

        public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
            return (SoftReference) super.put(key, value);
        }

        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> entry) {
            return size() > 20;
        }
    }

    public static boolean putBitmap(String path, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        synchronized (sHardBitmapCache) {
            sHardBitmapCache.put(path, bitmap);
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getBitmap(java.lang.String r5) {
        /*
        r4 = sHardBitmapCache;
        monitor-enter(r4);
        r3 = sHardBitmapCache;	 Catch:{ all -> 0x002e }
        r0 = r3.get(r5);	 Catch:{ all -> 0x002e }
        r0 = (android.graphics.Bitmap) r0;	 Catch:{ all -> 0x002e }
        if (r0 == 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r4);	 Catch:{ all -> 0x002e }
    L_0x000e:
        return r0;
    L_0x000f:
        monitor-exit(r4);	 Catch:{ all -> 0x002e }
        r4 = sSoftBitmapCache;
        monitor-enter(r4);
        r3 = sSoftBitmapCache;	 Catch:{ all -> 0x0039 }
        r2 = r3.get(r5);	 Catch:{ all -> 0x0039 }
        r2 = (java.lang.ref.SoftReference) r2;	 Catch:{ all -> 0x0039 }
        if (r2 == 0) goto L_0x0036;
    L_0x001d:
        r1 = r2.get();	 Catch:{ all -> 0x0039 }
        r1 = (android.graphics.Bitmap) r1;	 Catch:{ all -> 0x0039 }
        if (r1 == 0) goto L_0x0031;
    L_0x0025:
        r3 = r1.isRecycled();	 Catch:{ all -> 0x0039 }
        if (r3 != 0) goto L_0x0031;
    L_0x002b:
        monitor-exit(r4);	 Catch:{ all -> 0x0039 }
        r0 = r1;
        goto L_0x000e;
    L_0x002e:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x002e }
        throw r3;
    L_0x0031:
        r3 = sSoftBitmapCache;	 Catch:{ all -> 0x0039 }
        r3.remove(r5);	 Catch:{ all -> 0x0039 }
    L_0x0036:
        monitor-exit(r4);	 Catch:{ all -> 0x0039 }
        r0 = 0;
        goto L_0x000e;
    L_0x0039:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0039 }
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.bitmap.ZBitmapCache.getBitmap(java.lang.String):android.graphics.Bitmap");
    }

    public static Bitmap removeBitmap(String path) {
        Bitmap bitmap = (Bitmap) sHardBitmapCache.remove(path);
        if (bitmap == null) {
            return (Bitmap) ((SoftReference) sSoftBitmapCache.remove(path)).get();
        }
        return bitmap;
    }

    public static void deleteBitmap(String path) {
        Bitmap bitmap = (Bitmap) sHardBitmapCache.remove(path);
        if (bitmap == null) {
            bitmap = (Bitmap) ((SoftReference) sSoftBitmapCache.remove(path)).get();
            if (bitmap != null) {
                bitmap.recycle();
                return;
            }
            return;
        }
        bitmap.recycle();
    }

    public static void destroy() {
        sHardBitmapCache.evictAll();
        sSoftBitmapCache.clear();
    }
}
