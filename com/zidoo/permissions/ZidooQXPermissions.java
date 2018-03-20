package com.zidoo.permissions;

import com.zidoo.fileexplorer.db.FavoriteDatabase;

class ZidooQXPermissions {
    public native String readUUID();

    public native boolean writeUUID(String str);

    ZidooQXPermissions() {
    }

    public boolean init() {
        try {
            System.loadLibrary(FavoriteDatabase.UUID);
            return true;
        } catch (Throwable e) {
            System.out.println("bob  uuid lib = " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
