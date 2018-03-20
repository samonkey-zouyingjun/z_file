package com.example.zidootool.cmd;

import java.io.File;
import java.io.FileOutputStream;

public class ZCmdTool {
    public static boolean writeCommandByFile(String command, String filename) {
        Exception e;
        boolean z = false;
        File file = new File(filename);
        if (file.exists()) {
            FileOutputStream out = null;
            z = false;
            try {
                FileOutputStream out2 = new FileOutputStream(file, false);
                try {
                    StringBuffer sb = new StringBuffer();
                    sb.append(new StringBuilder(String.valueOf(command)).append("\n").toString());
                    out2.write(sb.toString().getBytes("utf-8"));
                    out2.flush();
                    z = true;
                    out = out2;
                } catch (Exception e2) {
                    e = e2;
                    out = out2;
                    try {
                        e.printStackTrace();
                    } catch (Throwable th) {
                    }
                } catch (Throwable th2) {
                    out = out2;
                }
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
                return z;
            }
            if (out != null) {
                out.close();
            }
        }
        return z;
    }

    public static boolean cmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cmd(String[] cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
