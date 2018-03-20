package com.zidoo.custom.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import com.zidoo.custom.net.ZidooNetStatusTool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZidooWifiTool {
    private Context mContext = null;
    ArrayList<ZidooWifiInfo> mScanResultInfos = new ArrayList();
    private BroadcastReceiver mWifiBroadcastReceiver = null;
    private IntentFilter mWifiFilter = null;
    public ZidooWifiListListener mWifiListListener = null;
    private WifiManager mWifiManager = null;

    private class WifiComparator implements Comparator<ScanResult> {
        private WifiComparator() {
        }

        public int compare(ScanResult sr1, ScanResult sr2) {
            int result = 0;
            if (sr1.level < sr2.level) {
                result = 1;
            }
            if (sr1.level > sr2.level) {
                return -1;
            }
            return result;
        }
    }

    public interface ZidooWifiListListener {
        void wifiInfos(ZidooWifStatus zidooWifStatus);

        void wifiList(List<ZidooWifiInfo> list);
    }

    public ZidooWifiTool(Context mContext, ZidooWifiListListener wifiListListener) {
        this.mContext = mContext;
        this.mWifiListListener = wifiListListener;
        init();
    }

    private void init() {
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        initWifiBroadcast();
    }

    private String getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return "WEP";
        }
        if (result.capabilities.contains("PSK")) {
            boolean wpa = result.capabilities.contains("WPA-PSK");
            boolean wpa2 = result.capabilities.contains("WPA2-PSK");
            if (wpa2 && wpa) {
                return "WPA/WPA2";
            }
            if (wpa2) {
                return "WPA2";
            }
            if (wpa) {
                return "WPA";
            }
            return "WPA-PSK";
        } else if (result.capabilities.contains("EAP")) {
            return "802.1x";
        } else {
            return "No";
        }
    }

    public void initWifiBroadcast() {
        this.mWifiBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                    ZidooWifiTool.this.mScanResultInfos.clear();
                    List<ScanResult> wifiList = ZidooWifiTool.this.mWifiManager.getScanResults();
                    if (wifiList != null && wifiList.size() > 0) {
                        Collections.sort(wifiList, new WifiComparator());
                        StringBuffer sBuffer = new StringBuffer();
                        if (ZidooWifiTool.this.mWifiListListener != null) {
                            for (ScanResult sr : wifiList) {
                                if (sBuffer.indexOf(new StringBuilder(String.valueOf(sr.SSID)).toString()) == -1) {
                                    sBuffer.append(sr.SSID);
                                    ZidooWifiInfo scanResultInfo = new ZidooWifiInfo(ZidooWifiTool.this.getSecurity(sr), sr);
                                    if (scanResultInfo.getStatu().equals("No")) {
                                        scanResultInfo.setLock(false);
                                    } else {
                                        scanResultInfo.setLock(true);
                                    }
                                    int f = Math.abs(sr.level);
                                    if (f >= 86) {
                                        scanResultInfo.setLevel(0);
                                    } else if (f >= 71) {
                                        scanResultInfo.setLevel(1);
                                    } else if (f >= 56) {
                                        scanResultInfo.setLevel(2);
                                    } else {
                                        scanResultInfo.setLevel(3);
                                    }
                                    ZidooWifiTool.this.mScanResultInfos.add(scanResultInfo);
                                }
                            }
                        }
                    }
                    ZidooWifiTool.this.mWifiListListener.wifiList(ZidooWifiTool.this.mScanResultInfos);
                } else if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                    switch (ZidooWifiTool.this.mWifiManager.getWifiState()) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            return;
                        default:
                            return;
                    }
                } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    ZidooWifiTool.this.getWifiData();
                } else if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                    ZidooWifiTool.this.mWifiManager.startScan();
                } else if (intent.getAction().equals("android.net.wifi.RSSI_CHANGED")) {
                    ZidooWifiTool.this.mWifiManager.startScan();
                } else if (intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
                    ZidooWifiTool.this.mWifiManager.startScan();
                } else if (intent.getAction().equals("android.net.wifi.NETWORK_IDS_CHANGED")) {
                    ZidooWifiTool.this.mWifiManager.startScan();
                }
            }
        };
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        mFilter.addAction("android.net.wifi.SCAN_RESULTS");
        mFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        mFilter.addAction("android.net.wifi.STATE_CHANGE");
        mFilter.addAction("android.net.wifi.RSSI_CHANGED");
        mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mWifiFilter = mFilter;
        this.mContext.registerReceiver(this.mWifiBroadcastReceiver, this.mWifiFilter);
    }

    public boolean isWifiEnabled() {
        if (this.mWifiManager != null) {
            return this.mWifiManager.isWifiEnabled();
        }
        return false;
    }

    public boolean openWifi() {
        if (this.mWifiManager == null || this.mWifiManager.isWifiEnabled() || !this.mWifiManager.setWifiEnabled(true)) {
            return false;
        }
        getWifiData();
        return true;
    }

    public boolean closenWifi() {
        return (this.mWifiManager == null || !this.mWifiManager.isWifiEnabled() || this.mWifiManager.setWifiEnabled(false)) ? true : true;
    }

    public void scanWifi() {
        this.mWifiManager.startScan();
    }

    public void getWifiData() {
        if (this.mWifiListListener != null) {
            ZidooWifStatus wifiInfo = new ZidooWifStatus();
            if (isWifiEnabled()) {
                WifiInfo info = this.mWifiManager.getConnectionInfo();
                if (info == null) {
                    wifiInfo.setSsid("");
                    wifiInfo.setIp("");
                    wifiInfo.setMac("");
                    wifiInfo.setMask("");
                    wifiInfo.setGateWay("");
                    wifiInfo.setStatus(0);
                } else if (ZidooNetStatusTool.isWifiConnected(this.mContext)) {
                    DhcpInfo dhcpInfo = this.mWifiManager.getDhcpInfo();
                    wifiInfo.setSsid(info.getSSID());
                    wifiInfo.setIp(Formatter.formatIpAddress(info.getIpAddress()));
                    wifiInfo.setMac(info.getMacAddress().toUpperCase());
                    wifiInfo.setMask(Formatter.formatIpAddress(dhcpInfo.netmask));
                    wifiInfo.setGateWay(Formatter.formatIpAddress(dhcpInfo.gateway));
                    wifiInfo.setStatus(2);
                } else {
                    boolean isWifiSSid = false;
                    String ssidString = info.getSSID();
                    if (ssidString != null) {
                        if (ssidString.contains("\"")) {
                            ssidString = ssidString.replace("\"", "");
                        }
                        int size = this.mScanResultInfos.size();
                        for (int i = 0; i < size; i++) {
                            if (ssidString.equals(((ZidooWifiInfo) this.mScanResultInfos.get(i)).getResult().SSID)) {
                                isWifiSSid = true;
                                break;
                            }
                        }
                    }
                    if (isWifiSSid) {
                        wifiInfo.setSsid(info.getSSID());
                        wifiInfo.setIp("");
                        wifiInfo.setMac("");
                        wifiInfo.setMask("");
                        wifiInfo.setGateWay("");
                        wifiInfo.setStatus(1);
                    } else {
                        wifiInfo.setSsid("");
                        wifiInfo.setIp("");
                        wifiInfo.setMac("");
                        wifiInfo.setMask("");
                        wifiInfo.setGateWay("");
                        wifiInfo.setStatus(0);
                    }
                }
            } else {
                wifiInfo.setSsid("");
                wifiInfo.setIp("");
                wifiInfo.setMac("");
                wifiInfo.setMask("");
                wifiInfo.setGateWay("");
                wifiInfo.setStatus(0);
            }
            this.mWifiListListener.wifiInfos(wifiInfo);
        }
    }
}
