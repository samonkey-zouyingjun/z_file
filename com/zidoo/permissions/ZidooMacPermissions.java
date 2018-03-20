package com.zidoo.permissions;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

class ZidooMacPermissions {
    ZidooMacPermissions() {
    }

    public static boolean writeUUID(String uuid) {
        FileWriter command;
        try {
            String mount_str = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/.android_bob").toString();
            File file = new File(mount_str);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            execMethod(mount_str);
            command = new FileWriter(file);
            command.write(uuid);
            command.write("\n");
            command.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } catch (Throwable th) {
            command.close();
        }
    }

    public static String readUUID() {
        try {
            File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/.android_bob").toString());
            if (!file.exists() || !file.canRead()) {
                return null;
            }
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReadernew = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReadernew);
            String key = br.readLine();
            inputStream.close();
            inputStreamReadernew.close();
            br.close();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void execMethod(String path) {
        try {
            Runtime.getRuntime().exec(new String[]{"chmod", "777", path});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
