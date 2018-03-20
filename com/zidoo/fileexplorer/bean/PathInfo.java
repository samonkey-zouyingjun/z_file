package com.zidoo.fileexplorer.bean;

import java.io.File;

public abstract class PathInfo {
    public static final int PATHTYPE_FAVORITE = 4;
    public static final int PATHTYPE_FILE = 0;
    public static final int PATHTYPE_NET_SHARE = 3;
    public static final int PATHTYPE_NFSDEVICE = 2;
    public static final int PATHTYPE_SMBDEVICE = 1;
    int type = 0;

    public abstract File getFile();

    public abstract String getName();

    public int getType() {
        return this.type;
    }
}
