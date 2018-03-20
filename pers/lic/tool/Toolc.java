package pers.lic.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.umeng.analytics.a.o;
import com.umeng.common.util.e;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import pers.lic.tool.bean.GPSInfo;
import pers.lic.tool.itf.ArrayIterator;
import pers.lic.tool.itf.CollectionIterator;
import pers.lic.tool.itf.NameGetter;
import pers.lic.tool.itf.ValueGetter;
import zidoo.browse.BrowseConstant;
import zidoo.http.HTTP;

public class Toolc {
    public static String formatFileSize(long size) {
        if (size < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return size + " B";
        }
        if (size < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            return String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(((float) size) / 1024.0f)}) + " KB";
        } else if (size < 1073741824) {
            return String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(((float) size) / 1048576.0f)}) + " M";
        } else {
            return String.format(Locale.getDefault(), "%.2f", new Object[]{Float.valueOf(((float) size) / 1.07374182E9f)}) + " G";
        }
    }

    public static void fullScreen(@Nullable View v) {
        if (v != null) {
            try {
                v.setSystemUiVisibility(5894);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyScanFile(Context context, File dir) {
        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(dir)));
    }

    public static int minMax(int value, int min, int max) {
        if (value > max) {
            value = max;
        }
        return value < min ? min : value;
    }

    public static float minMax(float value, float min, float max) {
        if (value > max) {
            value = max;
        }
        return value < min ? min : value;
    }

    public static String encodeHttpParam(String param, String charset) {
        try {
            param = URLEncoder.encode(param, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

    public static String decodeHttpParam(String param, String charset) {
        try {
            param = URLDecoder.decode(param, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

    public static String httpGet(String spec, String charsetName, int connectTimeOut, int readTimeOut) {
        String result = null;
        InputStream inputstream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(spec.trim()).openConnection();
            connection.setConnectTimeout(connectTimeOut);
            connection.setReadTimeout(readTimeOut);
            if (connection.getResponseCode() == 200) {
                inputstream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, charsetName));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                result = sb.toString();
            }
            connection.disconnect();
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e32) {
                    e32.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String httpPost(String spec, String data, String charsetName, int connectTimeOut, int readTimeOut) {
        return httpPost(spec, null, data, charsetName, connectTimeOut, readTimeOut);
    }

    public static String httpPost(String spec, String session, String data, String charsetName, int connectTimeOut, int readTimeOut) {
        String result = null;
        InputStream inputstream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(spec).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(HTTP.POST);
            connection.setConnectTimeout(connectTimeOut);
            connection.setReadTimeout(readTimeOut);
            if (session != null) {
                connection.setRequestProperty("Cookie", session);
            }
            if (!TextUtils.isEmpty(data)) {
                PrintWriter pw = new PrintWriter(connection.getOutputStream());
                pw.print(data);
                pw.flush();
                pw.close();
            }
            if (connection.getResponseCode() == 200) {
                inputstream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, charsetName));
                StringBuffer sb = new StringBuffer();
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
                result = sb.toString();
            }
            connection.disconnect();
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e32) {
                    e32.printStackTrace();
                }
            }
        }
        return result;
    }

    public static GPSInfo getAddressByBaidu(double lat, double lon, @Nullable String apiKey) {
        if (apiKey == null) {
            apiKey = "UG74MHdmnH9OIcSrkxSNOrRE3r5d91KG";
        }
        String result = httpGet("http://api.map.baidu.com/geocoder/v2/?ak=" + apiKey + "&callback=renderReverse&location=" + lat + "," + lon + "&output=json&pois=0&mcode=" + "F1:41:D5:7C:F7:97:74:97:18:B8:D1:3A:71:88:E2:BE:73:A8:24:06;com.zidoo.album", e.f, 8000, 8000);
        if (result != null && result.matches("renderReverse&&renderReverse\\(.*\\)")) {
            String json = result.substring("renderReverse&&renderReverse(".length(), result.length() - 1);
            try {
                GPSInfo gps = new GPSInfo();
                JSONObject gpsJson = new JSONObject(json).getJSONObject(BrowseConstant.EXTRA_RESULT);
                try {
                    JSONObject location = gpsJson.getJSONObject("location");
                    gps.setLat(location.optDouble(o.e));
                    gps.setLng(location.optDouble(o.d));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                gps.setAddress(gpsJson.optString("formatted_address"));
                gps.setBusiness(gpsJson.optString("business"));
                JSONObject addressJson = gpsJson.getJSONObject("addressComponent");
                gps.setCountry(addressJson.optString("country"));
                gps.setCountryCode(addressJson.optInt("country_code"));
                gps.setProvince(addressJson.optString("province"));
                gps.setCity(addressJson.optString("city"));
                gps.setDistrict(addressJson.optString("district"));
                gps.setAdcode(addressJson.optString("adcode"));
                gps.setStreet(addressJson.optString("street"));
                gps.setStreetNumber(addressJson.optString("street_number"));
                gps.setDirection(addressJson.optString("direction"));
                gps.setDistance(addressJson.optString("distance"));
                gps.setSematicDescription(gpsJson.optString("sematic_description"));
                gps.setCityCode(gpsJson.optInt("cityCode"));
                return gps;
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isNetConnected(Context context) {
        return isEthernetConnected(context) || isWifiConnected(context);
    }

    @RequiresApi(api = 21)
    public static NetworkInfo getConnectedWifiNetwork(Context context) {
        return getConnectedNetwork(context, 1);
    }

    @RequiresApi(api = 21)
    public static NetworkInfo getConnectedNetwork(Context context, int type) {
        ConnectivityManager e = (ConnectivityManager) context.getSystemService("connectivity");
        for (Network network : e.getAllNetworks()) {
            NetworkInfo networkInfo = e.getNetworkInfo(network);
            if (networkInfo.getType() == type && networkInfo.getState() == State.CONNECTED) {
                return networkInfo;
            }
        }
        return null;
    }

    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (VERSION.SDK_INT < 23) {
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(1);
                if (wifiInfo == null || !wifiInfo.isConnected()) {
                    return false;
                }
                return true;
            } else if (getConnectedWifiNetwork(context) != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isEthernetConnected(Context context) {
        try {
            NetworkInfo etherInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9);
            if (etherInfo == null || !etherInfo.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getWifiIP(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService("wifi");
        if (wifi.getWifiState() == 3) {
            return intToIp(wifi.getConnectionInfo().getIpAddress());
        }
        return null;
    }

    private static String intToIp(int ip) {
        return (ip & 255) + "." + ((ip >> 8) & 255) + "." + ((ip >> 16) & 255) + "." + ((ip >> 24) & 255);
    }

    public static String getEthernetMac() {
        String mac = null;
        try {
            Process p = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address");
            if (p.waitFor() == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                if (line != null) {
                    mac = line;
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static String getEthernetIPAddress() {
        String ip = null;
        String temp = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String name = intf.getName().toLowerCase();
                        if (name.equals("eth0")) {
                            return inetAddress.getHostAddress().toString();
                        }
                        if (name.equals("wlan0")) {
                            ip = inetAddress.getHostAddress().toString();
                        } else {
                            temp = inetAddress.getHostAddress().toString();
                        }
                    }
                }
                if (!TextUtils.isEmpty(ip)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip == null ? temp : ip;
    }

    public static boolean isInstall(Context context, String packageName) {
        return isAppLaunchInstall(context, packageName) || isAppSystemInstall(context, packageName);
    }

    public static boolean isAppLaunchInstall(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    public static boolean isAppSystemInstall(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double convertRationalLatLonToDouble(String rationalString, String ref) {
        String[] parts = rationalString.split(",");
        String[] pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim());
        pair = parts[2].split("/");
        double result = ((minutes / 60.0d) + degrees) + ((Double.parseDouble(pair[0].trim()) / Double.parseDouble(pair[1].trim())) / 3600.0d);
        if (ref.equals("S") || ref.equals("W")) {
            return (double) ((float) (-result));
        }
        return result;
    }

    public static boolean isSystemApp(Context context) {
        int flags = context.getApplicationInfo().flags;
        return ((flags & 1) == 0 && (flags & 128) == 0) ? false : true;
    }

    public static String findLongestSubString(String s1, String s2) {
        String max = "";
        int i = 0;
        while (i < s1.length() && max.length() < s1.length() - i) {
            for (int j = (max.length() + i) + 1; j < s1.length(); j++) {
                String sub = s1.substring(i, j);
                if (!s2.contains(sub)) {
                    break;
                }
                if (sub.length() > max.length()) {
                    max = sub;
                }
            }
            i++;
        }
        return max;
    }

    public static boolean connectHost(String host, int timeout) {
        try {
            return InetAddress.getByName(host).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = 17)
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();
        return outBitmap;
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
            Log.w("Toolc", "getImageThumbnail", e);
        }
        return bitmap;
    }

    public static boolean pingIp(String ip, int timeOut) {
        Exception e;
        int result = -1;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ping -c 1 -w " + timeOut + " " + ip);
            result = process.waitFor();
            process.exitValue();
            if (process != null) {
                process.destroy();
            }
        } catch (Exception e2) {
            e = e2;
            try {
                e.printStackTrace();
                if (process != null) {
                    process.destroy();
                }
                if (result == 0) {
                    return false;
                }
                return true;
            } catch (Throwable th) {
                if (process != null) {
                    process.destroy();
                }
            }
        } catch (Exception e22) {
            e = e22;
            e.printStackTrace();
            if (process != null) {
                process.destroy();
            }
            if (result == 0) {
                return true;
            }
            return false;
        }
        if (result == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> Iterable<V> toIterable(Collection<K> collection, NameGetter<K, V> nameGetter) {
        return new CollectionIterator(collection.iterator(), nameGetter);
    }

    public static <K, V> Iterable<V> toIterable(K[] array, NameGetter<K, V> nameGetter) {
        return new ArrayIterator(array, nameGetter);
    }

    public static String toString(String between, int[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String toString(String between, long[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String toString(String between, double[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String toString(String between, float[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String toString(String between, short[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String toString(CharSequence between, Object[] array) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(array[i]);
            }
        }
        return sb.toString();
    }

    public static <K> String toString(CharSequence between, K[] array, ValueGetter<K> getter) {
        StringBuilder sb = new StringBuilder();
        if (array != null && array.length > 0) {
            sb.append(getter.getValue(array[0]));
            for (int i = 1; i < array.length; i++) {
                sb.append(between).append(getter.getValue(array[i]));
            }
        }
        return sb.toString();
    }

    public static <K> String toString(CharSequence between, Collection<K> collection, ValueGetter<K> getter) {
        StringBuilder sb = new StringBuilder();
        if (collection != null) {
            Iterator<K> iterator = collection.iterator();
            if (iterator.hasNext()) {
                sb.append(getter.getValue(iterator.next()));
                while (iterator.hasNext()) {
                    sb.append(between).append(getter.getValue(iterator.next()));
                }
            }
        }
        return sb.toString();
    }
}
