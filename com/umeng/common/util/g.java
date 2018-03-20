package com.umeng.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import com.umeng.common.Log;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/* compiled from: Helper */
public class g {
    public static final String a = System.getProperty("line.separator");
    private static final String b = "helper";

    public static String a(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes();
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.reset();
            instance.update(bytes);
            bytes = instance.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                stringBuffer.append(String.format("%02X", new Object[]{Byte.valueOf(bytes[i])}));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            return str.replaceAll("[^[a-z][A-Z][0-9][.][_]]", "");
        }
    }

    public static String b(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toHexString(b & 255));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            Log.a(b, "getMD5 error", e);
            return "";
        }
    }

    public static String a(File file) {
        byte[] bArr = new byte[1024];
        try {
            if (!file.isFile()) {
                return "";
            }
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read != -1) {
                    instance.update(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    BigInteger bigInteger = new BigInteger(1, instance.digest());
                    return String.format("%1$032x", new Object[]{bigInteger});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String a(Context context, long j) {
        String str = "";
        if (j < 1000) {
            return ((int) j) + "B";
        }
        if (j < 1000000) {
            return Math.round(((double) ((float) j)) / 1000.0d) + "K";
        }
        if (j < 1000000000) {
            return new DecimalFormat("#0.0").format(((double) ((float) j)) / 1000000.0d) + "M";
        }
        return new DecimalFormat("#0.00").format(((double) ((float) j)) / 1.0E9d) + "G";
    }

    public static String c(String str) {
        String str2 = "";
        try {
            long longValue = Long.valueOf(str).longValue();
            if (longValue < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                return ((int) longValue) + "B";
            }
            if (longValue < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
                return new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1024.0d) + "K";
            } else if (longValue < 1073741824) {
                return new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1048576.0d) + "M";
            } else {
                return new DecimalFormat("#0.00").format(((double) ((float) longValue)) / 1.073741824E9d) + "G";
            }
        } catch (NumberFormatException e) {
            return str;
        }
    }

    public static void a(Context context, String str) {
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(str));
    }

    public static boolean b(Context context, String str) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean d(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean e(String str) {
        if (d(str)) {
            return false;
        }
        String toLowerCase = str.trim().toLowerCase();
        if (toLowerCase.startsWith("http://") || toLowerCase.startsWith("https://")) {
            return true;
        }
        return false;
    }

    public static String a() {
        return a(new Date());
    }

    public static String a(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
