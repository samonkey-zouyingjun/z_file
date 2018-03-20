package zidoo.samba.exs;

import java.util.ArrayList;

public interface OnRecvMsgListener {
    void onAdd(SambaDevice sambaDevice);

    void onComplete(boolean z);

    void onProgress(int i);

    ArrayList<SambaDevice> onQuery();

    void onSavedSmbDevices(ArrayList<SambaDevice> arrayList);

    void onStartScan();
}
