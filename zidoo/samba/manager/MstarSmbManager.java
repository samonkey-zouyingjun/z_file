package zidoo.samba.manager;

import android.content.Context;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class MstarSmbManager extends SambaManager {
    MstarSmbManager(Context context) {
        super(context);
    }

    public String getSmbRoot() {
        return "/mnt/samba";
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("//" + param[0] + "/" + param[1].replaceAll(" ", "\\\\040"), param[2]);
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        try {
            int ep = sharePath.lastIndexOf(47);
            String share = ep == -1 ? sharePath : sharePath.substring(ep + 1);
            ep = mountPoint.lastIndexOf(47);
            String mountName = ep == -1 ? mountPoint : mountPoint.substring(ep + 1);
            Class<?> cMStorageManager = Class.forName("com.mstar.android.storage.MStorageManager");
            return ((Boolean) cMStorageManager.getDeclaredMethod("mountSamba", new Class[]{String.class, String.class, String.class, String.class, String.class}).invoke(cMStorageManager.getMethod("getInstance", new Class[]{Context.class}).invoke(null, new Object[]{this.mContext}), new Object[]{ip, share, mountName, user, pwd})).booleanValue();
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

    public boolean unMountSmb(File file) {
        try {
            Class<?> cMStorageManager = Class.forName("com.mstar.android.storage.MStorageManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[]{Context.class}).invoke(null, new Object[]{this.mContext});
            return ((Boolean) cMStorageManager.getDeclaredMethod("unmountSamba", new Class[]{String.class, Boolean.TYPE}).invoke(mMStorageManager, new Object[]{file.getName(), Boolean.valueOf(true)})).booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e2) {
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
}
