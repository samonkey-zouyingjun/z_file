package com.zidoo.fileexplorer.bean;

import java.io.File;

public class ProtocolPath extends PathInfo {
    DeviceInfo deviceInfo;

    public ProtocolPath(DeviceInfo deviceInfo, int type) {
        this.deviceInfo = deviceInfo;
        this.type = type;
    }

    public String getName() {
        return this.deviceInfo.getName();
    }

    public File getFile() {
        return this.deviceInfo;
    }
}
