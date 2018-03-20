package zidoo.nfs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.io.File;
import zidoo.device.DeviceUtils;

@SuppressLint({"NewApi"})
public class AmlogicNfsManager extends NfsManager {
    AmlogicNfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/data/nfs";
    }

    public boolean mountNfs(String ip, String sharePath, String mountName) {
        boolean success;
        int n;
        String mountPoint = getNfsRoot() + "/" + mountName;
        creatMountPoint(mountPoint);
        DeviceUtils.execute("busybox mount -t nfs -o nolock \"" + ip + ":" + sharePath + "\" " + mountPoint);
        int n2 = 0;
        while (true) {
            sleep(50);
            success = isNfsMounted(ip, sharePath, mountPoint);
            if (success) {
                break;
            }
            n = n2 + 1;
            if (n2 >= 10) {
                break;
            }
            n2 = n;
        }
        n2 = n;
        if (!success) {
            new File(mountName).delete();
        }
        return success;
    }

    public boolean umountNfs(File file) {
        String mountPoint = file.getPath();
        if (!new File(mountPoint.replace("\"", "")).exists()) {
            return true;
        }
        int n;
        DeviceUtils.execute("busybox umount -fl " + mountPoint);
        int n2 = 0;
        while (true) {
            sleep(50);
            boolean exist = NfsManager.isNfsMounted(file.getPath(), true);
            if (!exist) {
                break;
            }
            n = n2 + 1;
            if (n2 >= 10) {
                break;
            }
            n2 = n;
        }
        n2 = n;
        if (!exist) {
            new File(mountPoint).delete();
        }
        if (exist) {
            return false;
        }
        return true;
    }

    public boolean creatMountPoint(String mountPoint) {
        try {
            File nfsRoot = new File(getNfsRoot());
            if (!nfsRoot.exists()) {
                if (!nfsRoot.mkdirs()) {
                    return false;
                }
                nfsRoot.setReadable(true, false);
                nfsRoot.setExecutable(true, false);
            }
            String abpath = new String(mountPoint).replace("\"", "");
            File mountFile = new File(abpath);
            if (!mountFile.exists()) {
                if (!mountFile.mkdirs()) {
                    return false;
                }
                Log.d("lisupan", "creat mount point:" + abpath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean deleteMountPoint(String mountPoint) {
        File mountFile = new File(new String(mountPoint).replace("\"", ""));
        if (mountFile.exists()) {
            if (!mountFile.delete()) {
                return false;
            }
            Log.i("lisupan", "delete mount point:" + mountPoint);
        }
        return true;
    }

    private void sleep(int time) {
        try {
            Thread.sleep((long) time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
