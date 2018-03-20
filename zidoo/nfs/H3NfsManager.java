package zidoo.nfs;

import android.content.Context;
import com.zidoo.fileexplorer.config.AppConstant;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class H3NfsManager extends NfsManager {
    H3NfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/mnt/nfs";
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        try {
            return ((Integer) Class.forName("com.softwinner.SystemMix").getMethod(AppConstant.PREFEREANCES_MOUNT, new Class[]{String.class, String.class, String.class, Integer.TYPE, String.class}).invoke(null, new Object[]{new StringBuilder().append(ip).append(":").append(sharePath).toString(), new StringBuilder().append(getNfsRoot()).append("/").append(mountPoint).toString(), "nfs", Integer.valueOf(32768), new StringBuilder().append("nolock,addr=").append(ip).toString()})).intValue() == 0;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return false;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return false;
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
            return false;
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            return false;
        }
    }

    public boolean umountNfs(File file) {
        try {
            int result = ((Integer) Class.forName("com.softwinner.SystemMix").getMethod("umount", new Class[]{String.class}).invoke(null, new Object[]{file.getPath()})).intValue();
            file.delete();
            return result == 0;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return false;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return false;
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return false;
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return false;
        }
    }

    public String createNewMountedPoint(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        int i = path.lastIndexOf("/");
        if (i != -1) {
            path = path.substring(i);
        }
        File file = new File("mnt/nfs", path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
