package com.zidoo.custom.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import com.umeng.common.util.e;
import com.zidoo.custom.init.ZidooJarPermissions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import org.apache.http.conn.util.InetAddressUtils;

public class ZidooNetStatusTool {
    private static ArrayList<OnlyNetWorkListener> mOnlyNetWorkListenerList = new ArrayList();
    private boolean isConnect = false;
    private boolean isEthernetConnect = false;
    private boolean isWifiConnect = false;
    private BroadcastReceiver mBroadcastReceiver = null;
    private Context mContext = null;
    private NetWorkListener mNetWorkListener = null;

    public interface NetWorkListener {
        void ethernetConnected(boolean z);

        void netWorkConnected(boolean z);

        void wifiConnected(boolean z);
    }

    public interface OnlyNetWorkListener {
        void netWorkConnected(boolean z);
    }

    public ZidooNetStatusTool(Context context, NetWorkListener netWorkListener) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = context;
        this.mNetWorkListener = netWorkListener;
        mOnlyNetWorkListenerList.clear();
        this.isWifiConnect = isWifiConnected(context);
        this.isEthernetConnect = isEthernetConnected(context);
        if (this.isWifiConnect || this.isEthernetConnect) {
            this.isConnect = true;
        } else {
            this.isConnect = false;
        }
        if (this.mNetWorkListener != null) {
            this.mNetWorkListener.wifiConnected(this.isWifiConnect);
        }
        if (this.mNetWorkListener != null) {
            this.mNetWorkListener.ethernetConnected(this.isEthernetConnect);
        }
        initData();
    }

    public void addOnlyNetWorkListener(OnlyNetWorkListener onlyNetWorkListener) {
        mOnlyNetWorkListenerList.add(onlyNetWorkListener);
    }

    public void moveOnlyNetWorkListener(OnlyNetWorkListener onlyNetWorkListener) {
        mOnlyNetWorkListenerList.remove(onlyNetWorkListener);
    }

    private void initData() {
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean isnetwork;
                String action = intent.getAction();
                boolean iswifi = false;
                if (ZidooNetStatusTool.isWifiConnected(context)) {
                    iswifi = true;
                }
                if (!(ZidooNetStatusTool.this.mNetWorkListener == null || ZidooNetStatusTool.this.isWifiConnect == iswifi)) {
                    ZidooNetStatusTool.this.mNetWorkListener.wifiConnected(iswifi);
                }
                boolean isweb = false;
                if (ZidooNetStatusTool.isEthernetConnected(context)) {
                    isweb = true;
                }
                if (!(ZidooNetStatusTool.this.mNetWorkListener == null || ZidooNetStatusTool.this.isEthernetConnect == isweb)) {
                    ZidooNetStatusTool.this.mNetWorkListener.ethernetConnected(isweb);
                }
                if (iswifi || isweb) {
                    isnetwork = true;
                } else {
                    isnetwork = false;
                }
                if (ZidooNetStatusTool.this.isConnect != isnetwork) {
                    if (ZidooNetStatusTool.this.mNetWorkListener != null) {
                        ZidooNetStatusTool.this.mNetWorkListener.netWorkConnected(isnetwork);
                    }
                    int size = ZidooNetStatusTool.mOnlyNetWorkListenerList.size();
                    for (int i = 0; i < size; i++) {
                        ((OnlyNetWorkListener) ZidooNetStatusTool.mOnlyNetWorkListenerList.get(i)).netWorkConnected(isnetwork);
                    }
                }
                ZidooNetStatusTool.this.isWifiConnect = iswifi;
                ZidooNetStatusTool.this.isEthernetConnect = isweb;
                ZidooNetStatusTool.this.isConnect = isnetwork;
            }
        };
        IntentFilter infilter = new IntentFilter();
        infilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        infilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, infilter);
    }

    public void release() {
        try {
            if (this.mBroadcastReceiver != null) {
                this.mContext.unregisterReceiver(this.mBroadcastReceiver);
                this.mBroadcastReceiver = null;
            }
            mOnlyNetWorkListenerList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getEthernetMac() {
        String mac = null;
        try {
            InputStream is = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address").getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line = bf.readLine();
            if (line != null) {
                mac = line;
            }
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static String getModel() {
        String model = null;
        try {
            model = URLEncoder.encode(Build.MODEL, e.f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public static String getWifiMac(Context context) {
        try {
            WifiManager wifiMgr = (WifiManager) context.getSystemService("wifi");
            WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
            if (info != null) {
                return info.getMacAddress();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getIp() {
        String ethip1 = null;
        String wlanip2 = null;
        String hint2 = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ip = inetAddress.getHostAddress().toString();
                        if (intf.getName().toLowerCase().equals("eth0")) {
                            ethip1 = ip;
                        } else if (intf.getName().toLowerCase().equals("wlan0")) {
                            wlanip2 = ip;
                        } else {
                            hint2 = ip;
                        }
                    }
                }
            }
            if (ethip1 != null) {
                return ethip1;
            }
            if (wlanip2 != null) {
                return wlanip2;
            }
            return hint2;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getWifiIp() {
        String wlanip2 = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ip = inetAddress.getHostAddress().toString();
                        if (intf.getName().toLowerCase().equals("wlan0")) {
                            wlanip2 = ip;
                            break;
                        }
                    }
                }
            }
            return wlanip2;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getEthernetIp() {
        String ethip1 = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ip = inetAddress.getHostAddress().toString();
                        if (intf.getName().toLowerCase().equals("eth0")) {
                            ethip1 = ip;
                            break;
                        }
                    }
                }
            }
            return ethip1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isNetworkConnected(Context context) {
        if (isWifiConnected(context) || isEthernetConnected(context)) {
            return true;
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        boolean z = true;
        try {
            NetworkInfo wifiInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            if (wifiInfo != null) {
                z = wifiInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    public static boolean isEthernetConnected(Context context) {
        try {
            NetworkInfo etherInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9);
            if (etherInfo != null) {
                return etherInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isIP(String ip) {
        boolean z = false;
        if (ip != null) {
            try {
                z = Pattern.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}").matcher(ip).matches();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return z;
    }

    public static boolean isPort(int prot) {
        boolean isOk = true;
        try {
            InputStream intput = Runtime.getRuntime().exec("netstat").getInputStream();
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(intput), 1024);
            String str;
            do {
                str = localBufferedReader.readLine();
                if (str == null) {
                    break;
                }
            } while (!str.contains("0.0.0.0:" + prot));
            isOk = false;
            intput.close();
            localBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOk;
    }
}
