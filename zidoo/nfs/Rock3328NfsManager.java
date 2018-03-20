package zidoo.nfs;

import android.annotation.SuppressLint;
import android.content.Context;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint({"NewApi"})
public class Rock3328NfsManager extends NfsManager {
    Rock3328NfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/mnt/nfs";
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        try {
            Class<?> cMStorageManager = Class.forName("android.os.storage.NFSManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Method mountNfs = cMStorageManager.getDeclaredMethod("mountNfs", new Class[]{String.class, String.class, String.class});
            sharePath = sharePath.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll(" ", "\\\\ ");
            mountPoint = mountPoint.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
            return ((Boolean) mountNfs.invoke(mMStorageManager, new Object[]{ip, sharePath, mountPoint})).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return false;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return false;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return false;
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return false;
        }
    }

    public boolean umountNfs(File file) {
        try {
            Class<?> cMStorageManager = Class.forName("android.os.storage.NFSManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            return ((Boolean) cMStorageManager.getDeclaredMethod("unmountNfs", new Class[]{String.class, Boolean.TYPE}).invoke(mMStorageManager, new Object[]{file.getName(), Boolean.valueOf(true)})).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return false;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return false;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return false;
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return false;
        }
    }
}
