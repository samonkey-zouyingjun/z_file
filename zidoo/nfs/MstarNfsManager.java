package zidoo.nfs;

import android.content.Context;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public final class MstarNfsManager extends NfsManager {
    MstarNfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/mnt/nfs";
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        boolean z = false;
        try {
            Class<?> cMStorageManager = Class.forName("com.mstar.android.storage.MStorageManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[]{Context.class}).invoke(null, new Object[]{this.mContext});
            z = ((Boolean) cMStorageManager.getDeclaredMethod("mountNfs", new Class[]{String.class, String.class, String.class}).invoke(mMStorageManager, new Object[]{"\"" + ip, sharePath + "\"", mountPoint})).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
        }
        return z;
    }

    public boolean umountNfs(File file) {
        try {
            Class<?> cMStorageManager = Class.forName("com.mstar.android.storage.MStorageManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[]{Context.class}).invoke(null, new Object[]{this.mContext});
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
