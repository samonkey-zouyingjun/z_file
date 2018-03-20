package com.zidoo.custom.wifi;

public class ZidooWifStatus {
    public static final int STATUS_CONNECTED = 2;
    public static final int STATUS_CONNECTEDING = 1;
    public static final int STATUS_DISCONNECTED = 0;
    private String gateWay;
    private String ip = "";
    private String mac = "";
    private String mask = "";
    private String ssid = "";
    private int status = 0;

    public String getSsid() {
        return this.ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getGateWay() {
        return this.gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public boolean isConnect() {
        return this.status == 2;
    }

    public void setConnect(boolean isConnect) {
        this.status = isConnect ? 2 : 0;
    }
}
