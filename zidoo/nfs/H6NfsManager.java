package zidoo.nfs;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class H6NfsManager extends Rock3328NfsManager {
    H6NfsManager(Context context) {
        super(context);
    }

    public boolean isNfsMounted(String ip, String sharePath, String mountPath) {
        return NfsManager.isNfsMounted(String.format("%s:%s %s nfs ", new Object[]{ip, replace(sharePath), replace(mountPath)}));
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        try {
            String mountPath = getNfsRoot() + '/' + mountPoint;
            boolean specialCharactersExist = false;
            String ss = replaceSpecialCharacters(sharePath);
            if (ss != null) {
                specialCharactersExist = true;
                sharePath = ss;
            }
            String sm = replaceSpecialCharacters(mountPoint);
            if (sm != null) {
                specialCharactersExist = true;
                mountPoint = sm;
            }
            if (specialCharactersExist) {
                mkdirs(mountPath);
                new File(getNfsRoot() + '/' + mountPoint).delete();
            } else {
                new File(mountPath).delete();
            }
            Class<?> cMStorageManager = Class.forName("android.os.storage.NFSManager");
            Object mMStorageManager = cMStorageManager.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            return ((Boolean) cMStorageManager.getDeclaredMethod("mountNfs", new Class[]{String.class, String.class, String.class}).invoke(mMStorageManager, new Object[]{ip, sharePath, mountPoint})).booleanValue();
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

    private String replaceSpecialCharacters(String input) {
        boolean exist = false;
        int length = input.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case ' ':
                    sb.append("\\ ");
                    exist = true;
                    break;
                case MotionEventCompat.AXIS_GENERIC_7 /*38*/:
                    sb.append("\\&");
                    exist = true;
                    break;
                case MotionEventCompat.AXIS_GENERIC_8 /*39*/:
                    sb.append("\\'");
                    exist = true;
                    break;
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                    sb.append("\\(");
                    exist = true;
                    break;
                case MotionEventCompat.AXIS_GENERIC_10 /*41*/:
                    sb.append("\\)");
                    exist = true;
                    break;
                case '`':
                    sb.append("\\`");
                    exist = true;
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return exist ? sb.toString() : null;
    }

    private void mkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean b = file.mkdirs();
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        }
    }

    private String replace(String input) {
        return input;
    }
}
