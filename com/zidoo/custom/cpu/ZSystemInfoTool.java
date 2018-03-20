package com.zidoo.custom.cpu;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import com.zidoo.custom.net.ZidooNetStatusTool;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class ZSystemInfoTool {
    private static final long discOffset = 1073741824;
    private static final long oneGBytes = 1073741824;
    private static final long oneKBytes = 1024;
    private static final long oneMBytes = 1048576;

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getEthernetMac() {
        return ZidooNetStatusTool.getEthernetMac();
    }

    public static String getIp() {
        return ZidooNetStatusTool.getIp();
    }

    public static String getSoftVersion() {
        try {
            return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.firmware").getInputStream())).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAndroidVersion() {
        try {
            String versionString = VERSION.RELEASE;
            if (versionString.startsWith("4.4")) {
                return "KitKat " + versionString;
            }
            return versionString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getZidooWeb() {
        return "www.zidoo.tv";
    }

    public static boolean isDisplayZidooWeb() {
        return Build.MODEL.contains("ZIDOO");
    }

    public static String getRam() {
        String ram = "0";
        long gross = getGrossRam() * 1024;
        if (gross > 268435456 && gross < 536870912) {
            gross = 536870912;
        }
        if (gross > 536870912 && gross < 1073741824) {
            gross = 1073741824;
        }
        if (gross > 1073741824) {
            gross = 2147483648L;
        }
        return getFormatedNumHaveUnit(gross);
    }

    public static String getFlash() {
        String flash = "0";
        try {
            long grossOut = (long) ((float) getGrossDiscOutSpaceSize());
            long availableOut = getAvailableOutDiscSize();
            long availableInner = getAvailableInnerDiscSize();
            long gross = grossOut + 1073741824;
            flash = "";
            if (gross <= 2147483648L) {
                gross = 2147483648L;
            } else if (gross > 2147483648L && gross <= 4294967296L) {
                gross = 4294967296L;
            } else if (gross > 4294967296L && gross <= 8589934592L) {
                gross = 8589934592L;
            } else if (gross > 8589934592L && gross <= 17179869184L) {
                gross = 17179869184L;
            } else if (gross > 17179869184L && gross <= 34359738368L) {
                gross = 34359738368L;
            }
            return getFormatedNumHaveUnit(gross);
        } catch (Exception e) {
            return "0";
        }
    }

    private static long getAvailableRam(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService("activity");
        MemoryInfo info = new MemoryInfo();
        am.getMemoryInfo(info);
        return info.availMem;
    }

    private static long getGrossRam() {
        Throwable th;
        BufferedReader br = null;
        String content = null;
        try {
            BufferedReader br2 = new BufferedReader(new FileReader("/proc/meminfo"), 100);
            try {
                String line = br2.readLine();
                if (line != null) {
                    content = line;
                }
                if (br2 != null) {
                    try {
                        br2.close();
                        br = br2;
                    } catch (Exception e) {
                        br = br2;
                    }
                }
            } catch (Exception e2) {
                br = br2;
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e3) {
                    }
                }
                return (long) Integer.parseInt(content.substring(content.indexOf(58) + 1, content.indexOf(107)).trim());
            } catch (Throwable th2) {
                th = th2;
                br = br2;
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e4) {
                    }
                }
                throw th;
            }
        } catch (Exception e5) {
            if (br != null) {
                br.close();
            }
            return (long) Integer.parseInt(content.substring(content.indexOf(58) + 1, content.indexOf(107)).trim());
        } catch (Throwable th3) {
            th = th3;
            if (br != null) {
                br.close();
            }
            throw th;
        }
        return (long) Integer.parseInt(content.substring(content.indexOf(58) + 1, content.indexOf(107)).trim());
    }

    private static String getFormatedNumHaveUnit(long num) {
        String strss = "";
        if (num < 1024) {
            return " " + num + "Byte";
        }
        if (num >= 1024 && num < 1048576) {
            return " " + getFormatedNum(((float) num) / 1024.0f) + "K";
        } else if (num >= 1048576 && num < 1073741824) {
            return " " + getFormatedNum(((float) num) / 1048576.0f) + "M";
        } else if (num < 1073741824) {
            return strss;
        } else {
            return " " + getFormatedNum(((float) num) / 1.07374182E9f) + "G";
        }
    }

    private static String getFormatedNum(float num) {
        return String.valueOf(((float) Math.round(num * 100.0f)) / 100.0f);
    }

    private static long getAvailableOutDiscSize() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) stat.getBlockSize()) * ((long) stat.getAvailableBlocks());
    }

    private static long getGrossDiscOutSpaceSize() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
        } catch (Exception e) {
            return 1;
        }
    }

    private static long getAvailableInnerDiscSize() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) stat.getBlockSize()) * ((long) stat.getAvailableBlocks());
    }
}
