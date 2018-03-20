package zidoo.nfs;

public interface OnNfsSearchListener {
    void OnNFSDeviceAddListener(NfsDevice nfsDevice);

    void onCompleteListener(int i, boolean z);

    void onNfsDeveceChangeListener(int i);

    void onNfsScanStart(int i);
}
