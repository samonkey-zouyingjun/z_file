package zidoo.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.model.BoxModel;

public abstract class ZidooUsbBroadcastReceiver extends BroadcastReceiver {
    protected abstract int getModel();

    protected abstract void onMount(Context context, ArrayList<ZDevice> arrayList, String str);

    protected abstract void onUmount(Context context, ArrayList<ZDevice> arrayList, String str);

    public void onReceive(final Context context, Intent intent) {
        try {
            final String path = intent.getData().getPath();
            String action = intent.getAction();
            if (!path.contains("null") && !path.startsWith("/dev/")) {
                if (action.equals("android.intent.action.MEDIA_REMOVED") || action.equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
                    new Thread(new Runnable() {
                        public void run() {
                            ZidooUsbBroadcastReceiver.this.umount(context, path);
                        }
                    }).start();
                } else if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                    new Thread(new Runnable() {
                        public void run() {
                            ZidooUsbBroadcastReceiver.this.mount(context, path);
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<ZDevice> getLastDevices(Context context) {
        return DeviceUtils.getDevicesFromFile(context, DeviceUtils.getDefaultDeviceConfigFile(context));
    }

    protected void saveDevices(Context context, ArrayList<ZDevice> devices) {
        DeviceUtils.saveUsbDeviceAsFile(context, devices, DeviceUtils.getDefaultDeviceConfigFile(context));
    }

    private synchronized void mount(Context context, String path) {
        ArrayList<ZDevice> existDevices = getLastDevices(context);
        ArrayList<ZDevice> all = BoxModel.getModel(context, getModel()).getDeviceList(2, true);
        ArrayList<ZDevice> mountDevices = new ArrayList();
        Iterator it = all.iterator();
        while (it.hasNext()) {
            ZDevice device = (ZDevice) it.next();
            boolean exist = false;
            Iterator it2 = existDevices.iterator();
            while (it2.hasNext()) {
                if (((ZDevice) it2.next()).getPath().equals(device.getPath())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                mountDevices.add(device);
            }
        }
        if (mountDevices.size() > 0) {
            onMount(context, mountDevices, path);
        }
    }

    private synchronized void umount(Context context, String path) {
        ArrayList<ZDevice> existDevices = getLastDevices(context);
        Iterator it = BoxModel.getModel(context, getModel()).getDeviceList(2, true).iterator();
        while (it.hasNext()) {
            ZDevice device = (ZDevice) it.next();
            Iterator<ZDevice> iterator = existDevices.iterator();
            while (iterator.hasNext()) {
                if (((ZDevice) iterator.next()).getPath().equals(device.getPath())) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (existDevices.size() > 0) {
            onUmount(context, existDevices, path);
        }
    }
}
