package zidoo.device;

import java.util.ArrayList;

public interface DeviceReorganise {
    public static final int TAG_FLASH = 1;
    public static final int TAG_NFS = 8;
    public static final int TAG_SMB = 4;
    public static final int TAG_USB = 2;

    ArrayList<ZDevice> getDeviceList(int i, boolean z);
}
