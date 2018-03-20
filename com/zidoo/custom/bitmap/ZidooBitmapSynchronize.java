package com.zidoo.custom.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.zidoo.custom.net.ZidooNetDataTool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ZidooBitmapSynchronize {
    public static boolean deleteBitmapFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] file1 = file.listFiles();
                    for (File delete : file1) {
                        if (file.isFile()) {
                            delete.delete();
                        }
                    }
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap getBitmapFile(String path) {
        Bitmap bitmap = null;
        try {
            File bitfile = new File(path);
            if (bitfile.exists()) {
                bitmap = BitmapFactory.decodeFile(bitfile.toString());
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap getBitmapFile(String downUrl, String path) {
        Bitmap bitmap = null;
        try {
            String[] filename = downUrl.split("/");
            File bitfile = new File(new StringBuilder(String.valueOf(path)).append(filename[filename.length - 1]).toString());
            if (bitfile.exists()) {
                bitmap = BitmapFactory.decodeFile(bitfile.toString());
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static boolean saveBitmap(Bitmap bitmap, String savePath) {
        return saveBitmap(bitmap, savePath, false);
    }

    public static boolean saveBitmap(Bitmap bitmap, String savePath, boolean isRecycle) {
        if (bitmap == null) {
            return false;
        }
        try {
            FileOutputStream out = new FileOutputStream(new File(savePath));
            if (!bitmap.compress(CompressFormat.PNG, 100, out)) {
                return false;
            }
            out.flush();
            out.close();
            if (isRecycle) {
                bitmap.recycle();
                System.gc();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap downloadBitmap(String url) {
        return downloadBitmap(url, false, 0);
    }

    public static Bitmap downloadBitmap(String url, String savePath) {
        Bitmap bitmap = downloadBitmap(url, false, 0);
        if (bitmap != null) {
            try {
                String[] filename = url.split("/");
                FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(filename[filename.length - 1]).toString()));
                if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap downloadBitmap(String url, String savePath, String name) {
        Bitmap bitmap = downloadBitmap(url, false, 0);
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(name).toString()));
                if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap downloadBitmap(String url, boolean isAboutSize, int size, String savePath, String name) {
        Bitmap bitmap = downloadBitmap(url, isAboutSize, size);
        if (bitmap != null) {
            try {
                FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(name).toString()));
                if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static boolean downloadBitmapRecycle(String url, String savePath) {
        Bitmap bitmap = downloadBitmap(url, false, 0);
        if (bitmap == null) {
            return false;
        }
        try {
            String[] filename = url.split("/");
            FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(filename[filename.length - 1]).toString()));
            if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        System.gc();
        return true;
    }

    public static boolean downloadBitmapRecycle(String url, String savePath, String name) {
        Bitmap bitmap = downloadBitmap(url, false, 0);
        if (bitmap == null) {
            return false;
        }
        try {
            FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(name).toString()));
            if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        System.gc();
        return true;
    }

    public static boolean downloadBitmapRecycle(String url, boolean isAboutSize, int size, String savePath, String name) {
        Bitmap bitmap = downloadBitmap(url, isAboutSize, size);
        if (bitmap == null) {
            return false;
        }
        try {
            FileOutputStream out = new FileOutputStream(new File(new StringBuilder(String.valueOf(savePath)).append(name).toString()));
            if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        System.gc();
        return true;
    }

    public static Bitmap downloadBitmap(String url, boolean isAboutSize, int size) {
        if (url == null || url.trim().equals("")) {
            return null;
        }
        try {
            InputStream in = ZidooNetDataTool.getInputStream(url, 40000, 40000);
            if (in == null) {
                return null;
            }
            Bitmap bitmap;
            if (isAboutSize) {
                Options opts = new Options();
                opts.inPreferredConfig = Config.RGB_565;
                opts.inPurgeable = true;
                opts.inInputShareable = true;
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, opts);
                opts.inSampleSize = computeSampleSize(opts, -1, size);
                opts.inJustDecodeBounds = false;
                InputStream in1 = ZidooNetDataTool.getInputStream(url, 40000, 40000);
                if (in1 == null) {
                    return null;
                }
                bitmap = BitmapFactory.decodeStream(in1, null, opts);
                if (in != null) {
                    in.close();
                }
                if (in1 == null) {
                    return bitmap;
                }
                in1.close();
                return bitmap;
            }
            bitmap = BitmapFactory.decodeStream(in);
            return bitmap != null ? bitmap : bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        } catch (Exception e2) {
            return null;
        }
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) options.outWidth;
        double h = (double) options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt((w * h) / ((double) maxNumOfPixels)));
        int upperBound = minSideLength == -1 ? 128 : (int) Math.min(Math.floor(w / ((double) minSideLength)), Math.floor(h / ((double) minSideLength)));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        }
        if (minSideLength != -1) {
            return upperBound;
        }
        return lowerBound;
    }

    private static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        if (initialSize > 8) {
            return initialSize + 7;
        }
        int roundedSize = 1;
        while (roundedSize < initialSize) {
            roundedSize <<= 1;
        }
        return roundedSize;
    }
}
