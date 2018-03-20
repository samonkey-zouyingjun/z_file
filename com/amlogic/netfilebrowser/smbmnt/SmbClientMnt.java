package com.amlogic.netfilebrowser.smbmnt;

import android.util.Log;
import java.util.ArrayList;

public class SmbClientMnt {
    public static final String TAG = "SmbClientMnt";
    private ArrayList<String> sharepaths = new ArrayList();

    public native int SmbGetNum();

    public native String SmbGetShareList(int i);

    public native int SmbGetStatus(int i);

    public native int SmbMount(String str);

    public native int SmbRefresh();

    public native int SmbUnMount(String str);

    public void FreshList() {
        this.sharepaths.clear();
        SmbRefresh();
    }

    public int GetStatus(int type) {
        return 0;
    }

    public ArrayList<String> GetListNum() {
        String item = "";
        int total = SmbGetNum();
        Log.d(TAG, "Get ListNum:" + Integer.toString(total));
        if (total > 0) {
            this.sharepaths.clear();
            for (int i = 0; i < total; i++) {
                item = SmbGetShareList(i);
                Log.d(TAG, "Got Item" + item);
                this.sharepaths.add(item);
            }
        }
        return this.sharepaths;
    }

    public String GetShareDir(int num) {
        if (num > this.sharepaths.size()) {
            return null;
        }
        return (String) this.sharepaths.get(num);
    }

    static {
        System.loadLibrary("smbmnt");
    }
}
