package com.zidoo.fileexplorer.bean;

import android.text.TextUtils;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.Utils;
import zidoo.device.DeviceType;
import zidoo.device.ZDevice;

public class DeviceInfo extends ZDevice {
    private static final long serialVersionUID = 1;
    private String deviceName = null;

    public DeviceInfo(String path) {
        super(path);
    }

    public DeviceInfo(ZDevice parent) {
        super(parent.getPath(), parent.getType());
        setBlock(parent.getBlock());
    }

    public DeviceInfo(String path, DeviceType type) {
        super(path, type);
    }

    public String getName() {
        if (this.deviceName == null) {
            initDeviceName();
        }
        return this.deviceName;
    }

    public void destroyDeviceName() {
        this.deviceName = null;
    }

    public String getLabel() {
        return getBlock() == null ? null : getBlock().getLabel();
    }

    private void initDeviceName() {
        if (TextUtils.isEmpty(getLabel()) || Utils.isIllegal(getLabel())) {
            int i;
            switch (getType()) {
                case FLASH:
                    this.deviceName = AppConstant.sFlash;
                    return;
                case TF:
                    this.deviceName = AppConstant.sSdcard;
                    return;
                case SD:
                    i = AppConstant.sUsbIndex;
                    AppConstant.sUsbIndex = i + 1;
                    this.deviceName = Utils.formatUsbDevice(i);
                    return;
                case HDD:
                    i = AppConstant.sHddIndex;
                    AppConstant.sHddIndex = i + 1;
                    this.deviceName = Utils.formatHddDevice(i);
                    return;
                case SMB:
                    this.deviceName = AppConstant.sSmb;
                    return;
                case NFS:
                    this.deviceName = AppConstant.sNfs;
                    return;
                default:
                    this.deviceName = super.getName();
                    return;
            }
        }
        this.deviceName = getLabel();
    }

    public String toString() {
        return "DeviceInfo [path=" + getPath() + " type=" + getType() + ", deviceName=" + this.deviceName + ", label=" + getLabel() + "]";
    }
}
