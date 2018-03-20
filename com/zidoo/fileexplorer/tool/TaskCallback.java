package com.zidoo.fileexplorer.tool;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import jcifs.smb.SmbFile;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFolder;
import zidoo.samba.exs.SambaDevice;

public interface TaskCallback {
    public static final int ERROR_EXISTS = -5;
    public static final int ERROR_MALFORMED_URL = -4;
    public static final int ERROR_UNABLE_CONNECT_HOST = -2;
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_UNKNOWN_USER_OR_PASSWORD = -3;
    public static final int GET_EXISTS_SMBDEVICE = -1;
    public static final int GET_MOUNT_SMB = -2;
    public static final int GET_SAVED_DEVICE = -3;
    public static final int RESULT_ADD_SMB = 1;
    public static final int RESULT_DELAY_SHOW_ADD_SMB_DIALOG = 2;
    public static final int RESULT_LOAD_SMB_DEVICE = 3;
    public static final int RESULT_LOAD_SMB_SHARE = 4;
    public static final int RESULT_LOAD_SMB_SHARE_DEVICE = 5;
    public static final int SUCCESS_LOAD_NFS_DEVICE = 10;
    public static final int SUCCESS_LOAD_NFS_SHARE = 11;

    int addToList(String str);

    int addToList(SambaDevice sambaDevice);

    FileFilter getFileFilter();

    SambaDevice getSavedDevice(String str, String str2);

    Object[] getSavedDeviceAndPosition(SambaDevice sambaDevice);

    boolean isNfsMounted(String str);

    boolean isSmbMounted(String str, String str2, String str3);

    boolean mountNfs(NfsFolder nfsFolder);

    boolean mountSmb(SmbFile smbFile, SambaDevice sambaDevice);

    void onCallback(Object... objArr);

    ArrayList<NfsFolder> openNfs(NfsDevice nfsDevice);

    boolean saveSmb(SambaDevice sambaDevice);

    void sortFiles(File[] fileArr, int i);
}
