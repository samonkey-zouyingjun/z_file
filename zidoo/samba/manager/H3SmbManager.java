package zidoo.samba.manager;

import android.content.Context;
import android.util.Log;
import com.zidoo.fileexplorer.config.AppConstant;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class H3SmbManager extends SambaManager {
    H3SmbManager(Context context) {
        super(context);
    }

    public String getSmbRoot() {
        return "/mnt/samba";
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("//" + param[0] + "/" + param[1].replaceAll(" ", "\\\\040"), param[2]);
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        int ep = sharePath.lastIndexOf(47);
        String share = ep == -1 ? sharePath : sharePath.substring(ep + 1);
        File mountFile = new File(mountPoint);
        if (!mountFile.exists()) {
            mountFile.mkdirs();
        }
        String unc = String.format("%s\\%s", new Object[]{"//" + ip, share});
        return mount(sharePath, mountPoint, "cifs", 64, String.format("unc=%s,ver=%s,user=%s,pass=%s,ip=%s,iocharset=%s", new Object[]{unc, "1", user, pwd, ip, "utf8"}));
    }

    private boolean mount(String src, String mountPoint, String fs, int flag, String options) {
        try {
            return ((Integer) Class.forName("com.softwinner.SystemMix").getMethod(AppConstant.PREFEREANCES_MOUNT, new Class[]{String.class, String.class, String.class, Integer.TYPE, String.class}).invoke(null, new Object[]{src, mountPoint, fs, Integer.valueOf(flag), options})).intValue() == 0;
        } catch (ClassNotFoundException e) {
            Log.e("H3SmbManager", "Mount Error", e);
            return false;
        } catch (NoSuchMethodException e2) {
            Log.e("H3SmbManager", "Mount Error", e2);
            return false;
        } catch (IllegalArgumentException e3) {
            Log.e("H3SmbManager", "Mount Error", e3);
            return false;
        } catch (IllegalAccessException e4) {
            Log.e("H3SmbManager", "Mount Error", e4);
            return false;
        } catch (InvocationTargetException e5) {
            Log.e("H3SmbManager", "Mount Error", e5);
            return false;
        }
    }

    public boolean unMountSmb(File file) {
        try {
            int result = ((Integer) Class.forName("com.softwinner.SystemMix").getMethod("umount", new Class[]{String.class}).invoke(null, new Object[]{file.getPath()})).intValue();
            file.delete();
            return result == 0;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return file.delete();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return file.delete();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return file.delete();
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return file.delete();
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return file.delete();
        }
    }

    public boolean deleteFile(File target) {
        File[] files = target.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteFile(file)) {
                        return false;
                    }
                } else if (!file.delete()) {
                    return false;
                }
            }
        }
        return target.delete();
    }
}
