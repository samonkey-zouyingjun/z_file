package com.umeng.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.umeng.common.util.g;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.microedition.khronos.opengles.GL10;

/* compiled from: DeviceConfig */
public class b {
    protected static final String a = b.class.getName();
    protected static final String b = "Unknown";
    public static final int c = 8;
    private static final String d = "2G/3G";
    private static final String e = "Wi-Fi";

    public static boolean a(String str, Context context) {
        try {
            context.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean a(Context context) {
        return context.getResources().getConfiguration().locale.toString().equals(Locale.CHINA.toString());
    }

    public static Set<String> b(Context context) {
        Set<String> hashSet = new HashSet();
        List installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            hashSet.add(((PackageInfo) installedPackages.get(i)).packageName);
        }
        return hashSet;
    }

    public static boolean c(Context context) {
        if (context.getResources().getConfiguration().orientation == 1) {
            return true;
        }
        return false;
    }

    public static String d(Context context) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (NameNotFoundException e) {
            return b;
        }
    }

    public static String e(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return b;
        }
    }

    public static boolean a(Context context, String str) {
        if (context.getPackageManager().checkPermission(str, context.getPackageName()) != 0) {
            return false;
        }
        return true;
    }

    public static String f(Context context) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
    }

    public static String[] a(GL10 gl10) {
        try {
            String[] strArr = new String[2];
            String glGetString = gl10.glGetString(7936);
            String glGetString2 = gl10.glGetString(7937);
            strArr[0] = glGetString;
            strArr[1] = glGetString2;
            return strArr;
        } catch (Exception e) {
            Log.b(a, "Could not read gpu infor:", e);
            return new String[0];
        }
    }

    public static String a() {
        Exception exception;
        String str;
        Exception exception2;
        String str2 = null;
        try {
            Reader fileReader = new FileReader("/proc/cpuinfo");
            if (fileReader != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(fileReader, 1024);
                    str2 = bufferedReader.readLine();
                    bufferedReader.close();
                    fileReader.close();
                } catch (Exception e) {
                    try {
                        Log.b(a, "Could not read from file /proc/cpuinfo", e);
                    } catch (Exception e2) {
                        exception = e2;
                        str = str2;
                        exception2 = exception;
                        Log.b(a, "Could not open file /proc/cpuinfo", exception2);
                        str2 = str;
                        if (str2 != null) {
                            str2 = str2.substring(str2.indexOf(58) + 1);
                        }
                        return str2.trim();
                    }
                }
            }
        } catch (Exception e22) {
            exception = e22;
            str = str2;
            exception2 = exception;
            Log.b(a, "Could not open file /proc/cpuinfo", exception2);
            str2 = str;
            if (str2 != null) {
                str2 = str2.substring(str2.indexOf(58) + 1);
            }
            return str2.trim();
        }
        if (str2 != null) {
            str2 = str2.substring(str2.indexOf(58) + 1);
        }
        return str2.trim();
    }

    public static String g(Context context) {
        String deviceId;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            Log.e(a, "No IMEI.");
        }
        String str = "";
        try {
            if (a(context, "android.permission.READ_PHONE_STATE")) {
                deviceId = telephonyManager.getDeviceId();
                if (TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
                Log.e(a, "No IMEI.");
                deviceId = r(context);
                if (TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
                Log.e(a, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
                deviceId = Secure.getString(context.getContentResolver(), "android_id");
                Log.a(a, "getDeviceId: Secure.ANDROID_ID: " + deviceId);
                return deviceId;
            }
        } catch (Exception e) {
            Log.e(a, "No IMEI.", e);
        }
        deviceId = str;
        if (TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        Log.e(a, "No IMEI.");
        deviceId = r(context);
        if (TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        Log.e(a, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
        deviceId = Secure.getString(context.getContentResolver(), "android_id");
        Log.a(a, "getDeviceId: Secure.ANDROID_ID: " + deviceId);
        return deviceId;
    }

    public static String h(Context context) {
        return g.b(g(context));
    }

    public static String i(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager == null) {
                return b;
            }
            return telephonyManager.getNetworkOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
    }

    public static String j(Context context) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            int i = displayMetrics.widthPixels;
            return String.valueOf(displayMetrics.heightPixels) + "*" + String.valueOf(i);
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
    }

    public static String[] k(Context context) {
        String[] strArr = new String[]{b, b};
        if (context.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
            strArr[0] = b;
            return strArr;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            strArr[0] = b;
            return strArr;
        } else if (connectivityManager.getNetworkInfo(1).getState() == State.CONNECTED) {
            strArr[0] = e;
            return strArr;
        } else {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (networkInfo.getState() != State.CONNECTED) {
                return strArr;
            }
            strArr[0] = d;
            strArr[1] = networkInfo.getSubtypeName();
            return strArr;
        }
    }

    public static boolean l(Context context) {
        return e.equals(k(context)[0]);
    }

    public static Location m(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService("location");
            if (a(context, "android.permission.ACCESS_FINE_LOCATION")) {
                Location lastKnownLocation = locationManager.getLastKnownLocation("gps");
                if (lastKnownLocation != null) {
                    Log.c(a, "get location from gps:" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                    return lastKnownLocation;
                }
            }
            if (a(context, "android.permission.ACCESS_COARSE_LOCATION")) {
                Location lastKnownLocation2 = locationManager.getLastKnownLocation("network");
                if (lastKnownLocation2 != null) {
                    Log.c(a, "get location from network:" + lastKnownLocation2.getLatitude() + "," + lastKnownLocation2.getLongitude());
                    return lastKnownLocation2;
                }
            }
            Log.c(a, "Could not get location from GPS or Cell-id, lack ACCESS_COARSE_LOCATION or ACCESS_COARSE_LOCATION permission?");
            return null;
        } catch (Exception e) {
            Log.b(a, e.getMessage());
            return null;
        }
    }

    public static boolean n(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean b() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static int o(Context context) {
        try {
            Calendar instance = Calendar.getInstance(y(context));
            if (instance != null) {
                return instance.getTimeZone().getRawOffset() / 3600000;
            }
        } catch (Exception e) {
            Log.a(a, "error in getTimeZone", e);
        }
        return 8;
    }

    public static String[] p(Context context) {
        String[] strArr = new String[2];
        try {
            Locale y = y(context);
            if (y != null) {
                strArr[0] = y.getCountry();
                strArr[1] = y.getLanguage();
            }
            if (TextUtils.isEmpty(strArr[0])) {
                strArr[0] = b;
            }
            if (TextUtils.isEmpty(strArr[1])) {
                strArr[1] = b;
            }
        } catch (Exception e) {
            Log.b(a, "error in getLocaleInfo", e);
        }
        return strArr;
    }

    private static Locale y(Context context) {
        Locale locale = null;
        try {
            Configuration configuration = new Configuration();
            System.getConfiguration(context.getContentResolver(), configuration);
            if (configuration != null) {
                locale = configuration.locale;
            }
        } catch (Exception e) {
            Log.b(a, "fail to read user config locale");
        }
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }

    public static String q(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (applicationInfo != null) {
                String string = applicationInfo.metaData.getString("UMENG_APPKEY");
                if (string != null) {
                    return string.trim();
                }
                Log.b(a, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.");
            }
        } catch (Exception e) {
            Log.b(a, "Could not read UMENG_APPKEY meta-data from AndroidManifest.xml.", e);
        }
        return null;
    }

    public static String r(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (a(context, "android.permission.ACCESS_WIFI_STATE")) {
                return wifiManager.getConnectionInfo().getMacAddress();
            }
            Log.e(a, "Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
            return "";
        } catch (Exception e) {
            Log.e(a, "Could not get mac address." + e.toString());
        }
    }

    public static String s(Context context) {
        try {
            int a;
            int a2;
            Object displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            if ((context.getApplicationInfo().flags & 8192) == 0) {
                a = a(displayMetrics, "noncompatWidthPixels");
                a2 = a(displayMetrics, "noncompatHeightPixels");
            } else {
                a2 = -1;
                a = -1;
            }
            if (a == -1 || r0 == -1) {
                a = displayMetrics.widthPixels;
                a2 = displayMetrics.heightPixels;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(a);
            stringBuffer.append("*");
            stringBuffer.append(a2);
            return stringBuffer.toString();
        } catch (Exception e) {
            Log.b(a, "read resolution fail", e);
            return b;
        }
    }

    private static int a(Object obj, String str) {
        try {
            Field declaredField = DisplayMetrics.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField.getInt(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String t(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService("phone")).getNetworkOperatorName();
        } catch (Exception e) {
            Log.a(a, "read carrier fail", e);
            return b;
        }
    }

    public static String a(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String c() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static Date a(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static int a(Date date, Date date2) {
        if (!date.after(date2)) {
            Date date3 = date2;
            date2 = date;
            date = date3;
        }
        return (int) ((date.getTime() - date2.getTime()) / 1000);
    }

    public static String u(Context context) {
        String str = b;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (!(applicationInfo == null || applicationInfo.metaData == null)) {
                Object obj = applicationInfo.metaData.get("UMENG_CHANNEL");
                if (obj != null) {
                    String obj2 = obj.toString();
                    if (obj2 != null) {
                        return obj2;
                    }
                    Log.a(a, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
                }
            }
        } catch (Exception e) {
            Log.a(a, "Could not read UMENG_CHANNEL meta-data from AndroidManifest.xml.");
            e.printStackTrace();
        }
        return str;
    }

    public static String v(Context context) {
        return context.getPackageName();
    }

    public static String w(Context context) {
        return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
    }

    public static boolean x(Context context) {
        try {
            return (context.getApplicationInfo().flags & 2) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
