package com.zidoo.permissions;

import android.content.Context;

class ZidooUuidTool {
    private boolean isWriterUUID = false;
    private Context mContext = null;
    private ZidooQXPermissions zidooQXPermissions = null;

    public ZidooUuidTool(Context mContext) {
        this.mContext = mContext;
        this.zidooQXPermissions = new ZidooQXPermissions();
        this.isWriterUUID = this.zidooQXPermissions.init();
        System.out.println("bob   isWriter = " + this.isWriterUUID);
    }

    public boolean writeUUID(String uuid) {
        if (this.isWriterUUID) {
            return this.zidooQXPermissions.writeUUID(uuid);
        }
        return ZidooMacPermissions.writeUUID(uuid);
    }

    public String readUUID() {
        if (this.isWriterUUID) {
            return this.zidooQXPermissions.readUUID();
        }
        return ZidooMacPermissions.readUUID();
    }

    public boolean isMac() {
        return !this.isWriterUUID;
    }
}
