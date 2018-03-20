package zidoo.samba.player.util;

import android.os.Environment;
import android.util.Log;
import java.io.File;

public class FileUtil {
    public static String deviceDMRUDN = "0";
    public static String deviceDMSUDN = "0";
    public static String ip = "127.0.0.1";
    public static int port = 0;
    private static String type = "*/*";

    public static String getFileType(String uri) {
        if (uri == null) {
            return type;
        }
        if (uri.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        if (uri.endsWith(".mp4")) {
            return "video/mp4";
        }
        return type;
    }

    public static String getDeviceDMRUDN() {
        return deviceDMRUDN;
    }

    public static String getDeviceDMSUDN() {
        return deviceDMSUDN;
    }

    public static boolean mkdir(String name) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String dir = Environment.getExternalStorageDirectory().getPath() + "/" + name + "/";
            File file = new File(dir);
            if (!file.exists()) {
                return file.mkdir();
            }
            Log.i("", "-----------" + dir + "�Ѵ���----------------");
            return false;
        }
        Log.e("", "-----------------�ⲿ�洢��������----------------");
        return false;
    }
}
