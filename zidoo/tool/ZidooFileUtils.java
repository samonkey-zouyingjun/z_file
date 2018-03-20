package zidoo.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import zidoo.file.FileType;

public class ZidooFileUtils {
    public static void sendPauseBroadCast(Context context) {
        Intent intent = new Intent("com.kaiboer.gl.mediaplayer.stopmusic.action");
        intent.putExtra("key", 0);
        context.sendBroadcast(intent);
        context.sendBroadcast(new Intent("com.kaiboer.fm.music_stop"));
    }

    public static boolean isAppSystemInstall(Context context, String pkgName) {
        try {
            return context.getPackageManager().getPackageInfo(pkgName, 0) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getPalyList(String rootPath) {
        ArrayList<String> movie_list = new ArrayList();
        try {
            File file = new File(rootPath.substring(0, rootPath.lastIndexOf("/")));
            if (file.isDirectory()) {
                for (File p_file : file.listFiles()) {
                    String path = p_file.getAbsolutePath();
                    if (FileType.isType(2, p_file.getName())) {
                        movie_list.add(path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie_list;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getIpInfo() {
        String ip = null;
        String temp = null;
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String name = intf.getName().toLowerCase();
                        if (name.equals("eth0") || name.equals("eth1")) {
                            return inetAddress.getHostAddress();
                        }
                        if (name.equals("wlan0")) {
                            ip = inetAddress.getHostAddress();
                        } else {
                            temp = inetAddress.getHostAddress();
                        }
                    }
                }
                if (!TextUtils.isEmpty(ip)) {
                    break;
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }
        return ip == null ? temp : ip;
    }

    public static String getSelfAddress(Context context) {
        String wifi = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                String name = netInterface.getName();
                String temp;
                if (name.equals("eth0") || name.equals("eth1")) {
                    if (cm.getNetworkInfo(9).isConnected()) {
                        temp = parseAddress(addresses);
                        if (temp != null) {
                            return temp;
                        }
                    } else {
                        continue;
                    }
                } else if (name.equals("wlan0") && cm.getNetworkInfo(1).isConnected()) {
                    temp = parseAddress(addresses);
                    if (temp != null) {
                        wifi = temp;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return wifi;
    }

    private static String parseAddress(Enumeration<InetAddress> addresses) {
        while (addresses.hasMoreElements()) {
            InetAddress ad = (InetAddress) addresses.nextElement();
            if (ad != null && (ad instanceof Inet4Address)) {
                return ad.getHostAddress();
            }
        }
        return null;
    }

    @Deprecated
    public static String encodeCommand(String str) {
        return str;
    }

    @Deprecated
    public static String decodeCommand(String cmd) {
        return cmd;
    }

    public static String escapeSequence(String input) {
        if (input == null) {
            throw new NullPointerException();
        }
        String result = "";
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case ' ':
                case MotionEventCompat.AXIS_GENERIC_7 /*38*/:
                case MotionEventCompat.AXIS_GENERIC_8 /*39*/:
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                case MotionEventCompat.AXIS_GENERIC_10 /*41*/:
                case '`':
                    result = result + "\\" + c;
                    break;
                default:
                    result = result + c;
                    break;
            }
        }
        return result;
    }
}
