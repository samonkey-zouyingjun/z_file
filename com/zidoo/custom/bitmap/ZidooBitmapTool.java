package com.zidoo.custom.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.ViewCompat;
import android.view.View;
import java.io.ByteArrayOutputStream;

public class ZidooBitmapTool {
    public static Drawable bitmapToDrawable(Context mContext, Bitmap bitmap) {
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable drawIdToDrawable(Context mContext, int drawID) {
        return mContext.getResources().getDrawable(drawID);
    }

    public static Bitmap drawIdToBitmap(Context mContext, int drawID) {
        return BitmapFactory.decodeResource(mContext.getResources(), drawID);
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap byteToBitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static String splitLastName(String url) {
        try {
            String[] filename = url.split("/");
            return filename[filename.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String splitLastName(String tag, String downUrl) {
        String name = null;
        try {
            String[] filename = downUrl.split("/");
            name = new StringBuilder(String.valueOf(tag)).append("_").append(filename[filename.length - 1]).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx, int w, int h, boolean isRecycle) {
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, w, h);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        Bitmap b = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Matrix matrix = new Matrix();
        Paint paint2 = new Paint();
        matrix.setTranslate((float) ((-bitmap.getWidth()) / 2), (float) ((-bitmap.getHeight()) / 2));
        matrix.postScale((((float) w) * 1.0f) / ((float) bitmap.getWidth()), (((float) h) * 1.0f) / ((float) bitmap.getHeight()));
        matrix.postTranslate((float) (w / 2), (float) (h / 2));
        c.drawBitmap(bitmap, matrix, paint2);
        if (isRecycle) {
            bitmap.recycle();
            System.gc();
        }
        bitmap = b;
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        System.gc();
        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        return getRoundedCornerBitmap(bitmap, roundPx, true);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx, boolean isRecycle) {
        return getRoundedCornerBitmap(bitmap, roundPx, bitmap.getWidth(), bitmap.getHeight(), isRecycle);
    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        try {
            int be;
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            options.inJustDecodeBounds = false;
            int beWidth = options.outWidth / width;
            int beHeight = options.outHeight / height;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if (be <= 0) {
                be = 1;
            }
            options.inSampleSize = be;
            bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath, options), width, height, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getnerateRefectedImage(Bitmap bitmap, int alpha, int destWidth, int destHeight) {
        try {
            int srcWidth = bitmap.getWidth();
            int srcHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.preScale(1.0f, -1.0f);
            Bitmap bitmapWithReflection = Bitmap.createBitmap(bitmap, 0, srcHeight - destHeight < 0 ? 0 : srcHeight - destHeight, srcWidth, destHeight, matrix, false);
            Bitmap reflectionBitmap = Bitmap.createBitmap(bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), Config.ARGB_8888);
            Paint paint = new Paint();
            Canvas canvas = new Canvas(reflectionBitmap);
            canvas.drawBitmap(bitmapWithReflection, 0.0f, 0.0f, null);
            paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, ((float) reflectionBitmap.getHeight()) * 0.8f, ViewCompat.MEASURED_SIZE_MASK | (alpha << 24), ViewCompat.MEASURED_SIZE_MASK, TileMode.CLAMP));
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawRect(0.0f, 0.0f, (float) reflectionBitmap.getWidth(), (float) reflectionBitmap.getHeight(), paint);
            paint.setShader(new LinearGradient(((float) reflectionBitmap.getWidth()) * 0.5f, 0.0f, ((float) reflectionBitmap.getWidth()) * 1.2f, 0.0f, ViewCompat.MEASURED_SIZE_MASK | (alpha << 24), ViewCompat.MEASURED_SIZE_MASK, TileMode.CLAMP));
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawRect(0.0f, 0.0f, (float) reflectionBitmap.getWidth(), (float) reflectionBitmap.getHeight(), paint);
            return reflectionBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, Math.round(((float) image.getWidth()) * 0.4f), Math.round(((float) image.getHeight()) * 0.4f), false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        blurScript.setRadius(blurRadius);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static boolean isTransparentBitmap(Bitmap b) {
        if (b == null) {
            return false;
        }
        int value = 0;
        for (int i = 0; i < b.getHeight(); i++) {
            for (int j = 0; j < b.getWidth(); j++) {
                value |= b.getPixel(j, i);
            }
        }
        if (value == 0) {
            return true;
        }
        return false;
    }

    public static Bitmap getViewBitmap(View view) {
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }
}
