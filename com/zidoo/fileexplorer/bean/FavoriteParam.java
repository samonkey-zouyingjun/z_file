package com.zidoo.fileexplorer.bean;

import java.io.File;
import zidoo.nfs.NfsDevice;
import zidoo.samba.exs.SambaDevice;

public class FavoriteParam {
    DeviceInfo deviceInfo;
    File file;
    int index;
    ListType listType;
    NfsDevice nfs;
    SambaDevice smb;
    int tag;

    public FavoriteParam(DeviceInfo deviceInfo, ListType listType) {
        this.deviceInfo = deviceInfo;
        this.listType = listType;
    }

    public DeviceInfo getDeviceInfo() {
        return this.deviceInfo;
    }

    public ListType getListType() {
        return this.listType;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public int getListIndex() {
        return this.index;
    }

    public void setListIndex(int index) {
        this.index = index;
    }

    public SambaDevice getSmb() {
        return this.smb;
    }

    public void setSmb(SambaDevice smb) {
        this.smb = smb;
    }

    public void setNfs(NfsDevice nfs) {
        this.nfs = nfs;
    }

    public NfsDevice getNfs() {
        return this.nfs;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
    }
}
