package com.zidoo.fileexplorer.bean;

public enum ListType {
    FILE,
    SMB_DEVICE,
    SMB_FILE,
    SMB_SHARE,
    NFS_DEVICE,
    NFS_FILE,
    FAVORITE;

    public static boolean isNet(ListType type) {
        return (type == FILE || type == FAVORITE) ? false : true;
    }

    public static boolean isFile(ListType type) {
        return type == FILE || type == SMB_FILE || type == NFS_FILE || type == SMB_SHARE;
    }

    public static boolean isNetFile(ListType type) {
        return type == SMB_FILE || type == SMB_SHARE || type == NFS_FILE;
    }
}
