package com.zidoo.custom.usb;

public class ZidooStorageInfo {
    public static final int STORAGE_FLASH = 2;
    public static final int STORAGE_HDD = 4;
    public static final int STORAGE_SATA = 3;
    public static final int STORAGE_SDCARD = 1;
    public static final int STORAGE_USB = 0;
    public String mPath = "";
    public int mStorageType = -1;

    public ZidooStorageInfo(String path, int storageType) {
        this.mPath = path;
        this.mStorageType = storageType;
    }
}
