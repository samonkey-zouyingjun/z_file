package zidoo.tool;

import android.util.Log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlkidManager {
    private static BlkidManager sManager = null;
    private Method BLKID;
    private Object BLKIDMANAGER;

    public static BlkidManager getInstance() {
        if (sManager == null) {
            sManager = new BlkidManager();
        }
        return sManager;
    }

    BlkidManager() {
        try {
            Class<?> c = Class.forName("android.os.storage.BlkidManager");
            this.BLKIDMANAGER = c.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            this.BLKID = c.getDeclaredMethod("blkid", new Class[]{String.class});
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
    }

    public void mountSmb(String ip, String user, String password, String sharePath, String mountPath) {
        File file = new File(mountPath);
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.setReadable(true, false);
                file.setWritable(true, false);
                file.setExecutable(true, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!file.exists()) {
            execute("busybox mkdir " + mountPath);
            execute("busybox chmod 777 " + mountPath);
        }
        sharePath = ZidooFileUtils.escapeSequence(sharePath);
        execute("busybox mount -t cifs -o iocharset=utf8,username=" + user + ",password=" + password + ",uid=1000,gid=1015,file_mode=0775,dir_mode=0775,rw " + sharePath + " " + ZidooFileUtils.escapeSequence(mountPath));
    }

    public void umount(String path) {
        execute("umount " + path);
    }

    public String execute(String cmd) {
        try {
            if (this.BLKID == null || this.BLKIDMANAGER == null) {
                Log.e("BlkidManager", "Not find BlkidManager");
                return null;
            }
            return (String) this.BLKID.invoke(this.BLKIDMANAGER, new Object[]{cmd});
        } catch (Exception e) {
            Log.e("BlkidManager", "execute command : " + cmd, e);
        }
    }
}
