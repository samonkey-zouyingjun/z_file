package com.zidoo.custom.cpu;

import android.content.Context;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.BufferedReader;
import java.io.FileReader;

public class CpuIdTool {
    public static String getCpuId(Context context) {
        if (ZidooBoxPermissions.isRockModel(context)) {
            return getRkCpuId();
        }
        if (ZidooBoxPermissions.isMstarModel(context)) {
            return getMstarCpuId();
        }
        if (ZidooBoxPermissions.isMlogicModel(context) || ZidooBoxPermissions.isMlogic905XModel(context)) {
            return getMlogicCpuId();
        }
        if (ZidooBoxPermissions.isH3Model(context) || ZidooBoxPermissions.isH6Model(context)) {
            return getH3CpuId();
        }
        if (ZidooBoxPermissions.isRealtekModel(context)) {
            return getRealtekCpuId();
        }
        return getH3CpuId();
    }

    public static boolean isFanControl(Context context) {
        return ZidooBoxPermissions.isFanControl(context);
    }

    public static String getMstarCpuId() {
        try {
            return new StringBuilder(String.valueOf(RunTimeTool.getSystemProperties("mstar.chipIDL"))).append(RunTimeTool.getSystemProperties("mstar.chipIDM")).append(RunTimeTool.getSystemProperties("mstar.chipIDH")).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMlogicCpuId() {
        try {
            return RunTimeTool.getSystemProperties("ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getH3CpuId() {
        return getRkCpuId();
    }

    public static String getH6CpuId() {
        return getRkCpuId();
    }

    public static String getRealtekCpuId() {
        try {
            return RunTimeTool.getSystemProperties("sys.cpuid");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRkCpuId() {
        try {
            String text;
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            do {
                text = br.readLine();
                if (text == null) {
                    break;
                }
            } while (!text.contains("Serial"));
            text = text.replace("Serial", "").replace(":", "").trim();
            if (br != null) {
                br.close();
            }
            if (fr == null) {
                return text;
            }
            fr.close();
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
