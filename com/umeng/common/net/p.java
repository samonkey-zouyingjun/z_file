package com.umeng.common.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.umeng.common.Log;
import com.umeng.common.util.g;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

/* compiled from: ResUtil */
public class p {
    public static boolean a = false;
    private static final String b = p.class.getName();
    private static final long c = 104857600;
    private static final long d = 10485760;
    private static final Map<ImageView, String> e = Collections.synchronizedMap(new WeakHashMap());
    private static Thread f;

    /* compiled from: ResUtil */
    public interface a {
        void a(com.umeng.common.net.o.a aVar);

        void a(b bVar);
    }

    /* compiled from: ResUtil */
    public enum b {
        BIND_FORM_CACHE,
        BIND_FROM_NET
    }

    /* compiled from: ResUtil */
    static class c extends AsyncTask<Object, Integer, Drawable> {
        private Context a;
        private String b;
        private ImageView c;
        private b d;
        private boolean e;
        private a f;
        private Animation g;
        private boolean h;
        private File i;

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return a(objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            a((Drawable) obj);
        }

        public c(Context context, ImageView imageView, String str, b bVar, File file, boolean z, a aVar, Animation animation, boolean z2) {
            this.i = file;
            this.a = context;
            this.b = str;
            this.f = aVar;
            this.d = bVar;
            this.e = z;
            this.g = animation;
            this.c = imageView;
            this.h = z2;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            if (this.f != null) {
                this.f.a(this.d);
            }
        }

        protected void a(Drawable drawable) {
            p.b(this.a, this.c, drawable, this.e, this.f, this.g, this.h, this.b);
        }

        protected Drawable a(Object... objArr) {
            if (p.a) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Drawable drawable;
            if (this.i == null || !this.i.exists()) {
                try {
                    p.a(this.a, this.b);
                    File b = p.b(this.a, this.b);
                    if (b == null || !b.exists()) {
                        drawable = null;
                    } else {
                        drawable = p.c(b.getAbsolutePath());
                    }
                    Log.c(p.b, "get drawable from net else file.");
                    return drawable;
                } catch (Exception e2) {
                    Log.e(p.b, e2.toString(), e2);
                    return null;
                }
            }
            drawable = p.c(this.i.getAbsolutePath());
            if (drawable == null) {
                this.i.delete();
            }
            Log.c(p.b, "get drawable from cacheFile.");
            return drawable;
        }
    }

    private static String b(String str) {
        int lastIndexOf = str.lastIndexOf(".");
        String str2 = "";
        if (lastIndexOf >= 0) {
            str2 = str.substring(lastIndexOf);
        }
        return g.a(str) + str2;
    }

    public static String a(Context context, String str) {
        File file;
        Exception e;
        if (g.d(str)) {
            return null;
        }
        try {
            String canonicalPath;
            long j;
            String str2 = b(str) + ".tmp";
            if (com.umeng.common.b.b()) {
                canonicalPath = Environment.getExternalStorageDirectory().getCanonicalPath();
                j = c;
            } else {
                canonicalPath = context.getCacheDir().getCanonicalPath();
                j = 10485760;
            }
            File file2 = new File(canonicalPath + com.umeng.common.a.a);
            if (file2.exists()) {
                if (b(file2.getCanonicalFile()) > j) {
                    synchronized (f) {
                        if (f == null) {
                            f = new Thread(new q(file2));
                            f.start();
                        }
                    }
                }
            } else if (!file2.mkdirs()) {
                Log.b(b, "Failed to create directory" + file2.getAbsolutePath() + ". Check permission. Make sure WRITE_EXTERNAL_STORAGE is added in your Manifest.xml");
            }
            file = new File(file2, str2);
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream inputStream = (InputStream) new URL(str).openConnection().getContent();
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileOutputStream.flush();
                        inputStream.close();
                        fileOutputStream.close();
                        File file3 = new File(file.getParent(), file.getName().replace(".tmp", ""));
                        file.renameTo(file3);
                        Log.a(b, "download img[" + str + "]  to " + file3.getCanonicalPath());
                        return file3.getCanonicalPath();
                    }
                }
            } catch (Exception e2) {
                e = e2;
                Log.a(b, e.getStackTrace().toString() + "\t url:\t" + g.a + str);
                if (file != null && file.exists()) {
                    file.deleteOnExit();
                }
                return null;
            }
        } catch (Exception e3) {
            e = e3;
            file = null;
            Log.a(b, e.getStackTrace().toString() + "\t url:\t" + g.a + str);
            file.deleteOnExit();
            return null;
        }
    }

    private static long b(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return 0;
        }
        Stack stack = new Stack();
        stack.clear();
        stack.push(file);
        long j = 0;
        while (!stack.isEmpty()) {
            File[] listFiles = ((File) stack.pop()).listFiles();
            long j2 = j;
            int i = 0;
            while (i < listFiles.length) {
                long j3;
                if (listFiles[i].isDirectory()) {
                    stack.push(listFiles[i]);
                    j3 = j2;
                } else {
                    j3 = listFiles[i].length() + j2;
                }
                i++;
                j2 = j3;
            }
            j = j2;
        }
        return j;
    }

    private static void c(File file) {
        if (file != null && file.exists() && file.canWrite() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    c(listFiles[i]);
                } else if (new Date().getTime() - listFiles[i].lastModified() > 1800) {
                    listFiles[i].delete();
                }
            }
        }
    }

    protected static File b(Context context, String str) throws IOException {
        String canonicalPath;
        String b = b(str);
        if (com.umeng.common.b.b()) {
            canonicalPath = Environment.getExternalStorageDirectory().getCanonicalPath();
        } else {
            canonicalPath = context.getCacheDir().getCanonicalPath();
        }
        File file = new File(new File(canonicalPath + com.umeng.common.a.a), b);
        return file.exists() ? file : null;
    }

    public static void a(Context context, ImageView imageView, String str, boolean z) {
        a(context, imageView, str, z, null, null, false);
    }

    public static void a(Context context, ImageView imageView, String str, boolean z, a aVar) {
        a(context, imageView, str, z, aVar, null, false);
    }

    public static void a(Context context, ImageView imageView, String str, boolean z, a aVar, Animation animation) {
        a(context, imageView, str, z, aVar, null, false);
    }

    public static void a(Context context, ImageView imageView, String str, boolean z, a aVar, Animation animation, boolean z2) {
        if (imageView != null) {
            e.put(imageView, str);
            try {
                File b = b(context, str);
                if (b == null || !b.exists() || a) {
                    new c(context, imageView, str, b.BIND_FROM_NET, null, z, aVar, animation, z2).execute(new Object[0]);
                    return;
                }
                if (aVar != null) {
                    aVar.a(b.BIND_FORM_CACHE);
                }
                Drawable c = c(b.getAbsolutePath());
                if (c == null) {
                    b.delete();
                }
                b(context, imageView, c, z, aVar, animation, z2, str);
            } catch (Exception e) {
                Log.b(b, "", e);
                if (aVar != null) {
                    aVar.a(com.umeng.common.net.o.a.FAIL);
                }
            }
        }
    }

    private static boolean a(ImageView imageView, String str) {
        String str2 = (String) e.get(imageView);
        if (str2 == null || str2.equals(str)) {
            return false;
        }
        return true;
    }

    private static synchronized void b(Context context, ImageView imageView, Drawable drawable, boolean z, a aVar, Animation animation, boolean z2, String str) {
        synchronized (p.class) {
            if (z2 && drawable != null) {
                try {
                    drawable = new BitmapDrawable(a(((BitmapDrawable) drawable).getBitmap()));
                } catch (Exception e) {
                    Log.b(b, "bind failed", e);
                    if (aVar != null) {
                        aVar.a(com.umeng.common.net.o.a.FAIL);
                    }
                }
            }
            if (drawable == null || imageView == null) {
                if (aVar != null) {
                    aVar.a(com.umeng.common.net.o.a.FAIL);
                }
                Log.e(b, "bind drawable failed. drawable [" + drawable + "]  imageView[+" + imageView + "+]");
            } else {
                if (!a(imageView, str)) {
                    if (z) {
                        imageView.setBackgroundDrawable(drawable);
                    } else {
                        imageView.setImageDrawable(drawable);
                    }
                    if (animation != null) {
                        imageView.startAnimation(animation);
                    }
                    if (aVar != null) {
                        aVar.a(com.umeng.common.net.o.a.SUCCESS);
                    }
                } else if (aVar != null) {
                    aVar.a(com.umeng.common.net.o.a.FAIL);
                }
            }
        }
    }

    private static Drawable c(String str) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromPath(str);
        } catch (OutOfMemoryError e) {
            Log.e(b, "Resutil fetchImage OutOfMemoryError:" + e.toString());
        }
        return drawable;
    }

    private static Bitmap a(Bitmap bitmap) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(-12434878);
            canvas.drawRoundRect(rectF, (float) (bitmap.getWidth() / 6), (float) (bitmap.getHeight() / 6), paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError e) {
            Log.e(b, "Cant`t create round corner bitmap. [OutOfMemoryError] ");
            return null;
        }
    }
}
