package zidoo.model;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import zidoo.device.DeviceType;
import zidoo.device.DeviceUtils;
import zidoo.device.ZDevice;

public class Amlogic905x extends Amlogic {
    public Amlogic905x(Context context) {
        super(context);
    }

    public ArrayList<ZDevice> getDeviceList(int tag, boolean getBlock) {
        final ArrayList<ZDevice> devices = new ArrayList();
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
        if ((tag & 1) != 0) {
            devices.add(new ZDevice(externalStorageDirectory, DeviceType.FLASH));
        }
        if ((tag & 2) != 0) {
            devices.addAll(DeviceUtils.getVolumesForMarshmallow(this.mContext));
        }
        if ((tag & 4) != 0 || (tag & 8) != 0) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/mounts"))));
                while (true) {
                    String line = in.readLine();
                    if (line != null) {
                        String[] split = line.split(" ");
                        if ((tag & 4) != 0 && split[2].equals("cifs")) {
                            devices.add(new ZDevice(split[1], DeviceType.SMB));
                        } else if ((tag & 8) == 0) {
                            continue;
                        } else if (split[2].equals("nfs")) {
                            devices.add(new ZDevice(split[1], DeviceType.NFS));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (this.mAutoSaveDevice) {
                new Thread(new Runnable() {
                    public void run() {
                        DeviceUtils.saveUsbDeviceAsFile(Amlogic905x.this.mContext, devices, DeviceUtils.getDefaultDeviceConfigFile(Amlogic905x.this.mContext));
                    }
                }).start();
            }
            return devices;
        }
        try {
            if (this.mAutoSaveDevice) {
                new Thread(/* anonymous class already generated */).start();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return devices;
    }
}
