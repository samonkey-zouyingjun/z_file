package com.zidoo.custom.wifi;

import android.net.wifi.ScanResult;

public class ZidooWifiInfo {
    public static final int LEVEL_HIGH = 3;
    public static final int LEVEL_LOW = 0;
    public static final int LEVEL_MIDDLE_0 = 1;
    public static final int LEVEL_MIDDLE_1 = 2;
    private boolean isLock = true;
    private int level = 0;
    private ScanResult result = null;
    private String statu = "";

    public String getStatu() {
        return this.statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public ScanResult getResult() {
        return this.result;
    }

    public void setResult(ScanResult result) {
        this.result = result;
    }

    public ZidooWifiInfo(String statu, ScanResult result) {
        this.statu = statu;
        this.result = result;
    }

    public boolean isLock() {
        return this.isLock;
    }

    public void setLock(boolean isLock) {
        this.isLock = isLock;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
