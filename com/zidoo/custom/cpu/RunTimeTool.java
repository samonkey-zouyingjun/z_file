package com.zidoo.custom.cpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunTimeTool {
    public static String getSystemProperties(String key) {
        try {
            return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop " + key).getInputStream())).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean setSystemProperties(String key, String values) {
        try {
            Runtime.getRuntime().exec("setprop " + key + " " + values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getRunTimeStr(String path) {
        String mac = null;
        try {
            InputStream is = Runtime.getRuntime().exec("cat " + path).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line = bf.readLine();
            if (line != null) {
                mac = line;
            }
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static boolean writeCommand(String command, String filePath) {
        Exception e;
        Throwable th;
        boolean z = false;
        File file = new File(filePath);
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
                    if (out2 != null) {
                        try {
                            out2.close();
                            out = out2;
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    out = out2;
                } catch (Exception e3) {
                    e2 = e3;
                    out = out2;
                    try {
                        e2.printStackTrace();
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e22) {
                                e22.printStackTrace();
                            }
                        }
                        return z;
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e222 = e4;
                e222.printStackTrace();
                if (out != null) {
                    out.close();
                }
                return z;
            }
        }
        return z;
    }
}
