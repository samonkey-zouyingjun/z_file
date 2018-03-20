package zidoo.samba.exs;

import jcifs.smb.SmbFile;

public interface OnOpenSmbFileListener {
    public static final int OPEN_SMBFILE_FAIL = 2;
    public static final int OPEN_SMBFILE_SUCCESS = 1;

    void OnOpenSmbFileFail();

    void OnOpenSmbFileSuccess(SmbFile[] smbFileArr);
}
