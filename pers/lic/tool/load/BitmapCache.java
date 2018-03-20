package pers.lic.tool.load;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.LruCache;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

@SuppressLint({"NewApi"})
public class BitmapCache<K> {
    public static final String TAG = "BitmapCache";
    static int sBitmapId = -520093696;
    private LruCache<K, Bitmap> sHardBitmapCache = new LruCache<K, Bitmap>(16777216) {
        protected int sizeOf(K k, Bitmap value) {
            return value.getByteCount();
        }

        protected void entryRemoved(boolean evicted, K key, Bitmap oldValue, Bitmap newValue) {
            BitmapCache.this.sSoftBitmapCache.put(key, new SoftReference(oldValue));
        }
    };
    private LinkedHashMap<K, SoftReference<Bitmap>> sSoftBitmapCache = new LinkedHashMap<K, SoftReference<Bitmap>>(40, 0.75f, true) {
        public SoftReference<Bitmap> put(K key, SoftReference<Bitmap> value) {
            return (SoftReference) super.put(key, value);
        }

        protected boolean removeEldestEntry(Entry<K, SoftReference<Bitmap>> entry) {
            return size() > 20;
        }
    };

    public boolean putBitmap(K key, Bitmap value) {
        if (value == null) {
            return false;
        }
        synchronized (this.sHardBitmapCache) {
            this.sHardBitmapCache.put(key, value);
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap getBitmap(K r5) {
        /*
        r4 = this;
        r3 = r4.sHardBitmapCache;
        monitor-enter(r3);
        r2 = r4.sHardBitmapCache;	 Catch:{ all -> 0x002a }
        r1 = r2.get(r5);	 Catch:{ all -> 0x002a }
        r1 = (android.graphics.Bitmap) r1;	 Catch:{ all -> 0x002a }
        if (r1 == 0) goto L_0x000f;
    L_0x000d:
        monitor-exit(r3);	 Catch:{ all -> 0x002a }
    L_0x000e:
        return r1;
    L_0x000f:
        monitor-exit(r3);	 Catch:{ all -> 0x002a }
        r3 = r4.sSoftBitmapCache;
        monitor-enter(r3);
        r2 = r4.sSoftBitmapCache;	 Catch:{ all -> 0x0027 }
        r0 = r2.get(r5);	 Catch:{ all -> 0x0027 }
        r0 = (java.lang.ref.SoftReference) r0;	 Catch:{ all -> 0x0027 }
        if (r0 == 0) goto L_0x0032;
    L_0x001d:
        r1 = r0.get();	 Catch:{ all -> 0x0027 }
        r1 = (android.graphics.Bitmap) r1;	 Catch:{ all -> 0x0027 }
        if (r1 == 0) goto L_0x002d;
    L_0x0025:
        monitor-exit(r3);	 Catch:{ all -> 0x0027 }
        goto L_0x000e;
    L_0x0027:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0027 }
        throw r2;
    L_0x002a:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x002a }
        throw r2;
    L_0x002d:
        r2 = r4.sSoftBitmapCache;	 Catch:{ all -> 0x0027 }
        r2.remove(r5);	 Catch:{ all -> 0x0027 }
    L_0x0032:
        monitor-exit(r3);	 Catch:{ all -> 0x0027 }
        r1 = 0;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: pers.lic.tool.load.BitmapCache.getBitmap(java.lang.Object):android.graphics.Bitmap");
    }

    public Bitmap removeBitmap(K key) {
        Bitmap value = (Bitmap) this.sHardBitmapCache.remove(key);
        if (value == null) {
            return (Bitmap) ((SoftReference) this.sSoftBitmapCache.remove(key)).get();
        }
        return value;
    }

    public void deleteBitmap(K key) {
        Bitmap value = (Bitmap) this.sHardBitmapCache.remove(key);
        if (value == null) {
            value = (Bitmap) ((SoftReference) this.sSoftBitmapCache.remove(key)).get();
            if (value != null) {
                value.recycle();
                return;
            }
            return;
        }
        value.recycle();
    }

    public void destroy() {
        this.sHardBitmapCache.evictAll();
        this.sSoftBitmapCache.clear();
    }
}
