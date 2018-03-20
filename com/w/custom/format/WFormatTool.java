package com.w.custom.format;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class WFormatTool {
    public static String getFloatStr(float vales, int count) {
        String format = "#.";
        if (vales < 1.0f) {
            format = "#0.";
        }
        for (int i = 0; i < count; i++) {
            format = new StringBuilder(String.valueOf(format)).append("0").toString();
        }
        return new DecimalFormat(format).format((double) vales);
    }

    public static float getFloat(float vales, int count) {
        return new BigDecimal((double) vales).setScale(count, 4).floatValue();
    }
}
