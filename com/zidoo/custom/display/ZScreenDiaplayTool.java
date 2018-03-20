package com.zidoo.custom.display;

import android.app.Activity;
import android.graphics.Point;

public class ZScreenDiaplayTool {
    public static Point getScreenWH(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }

    public static boolean is1080Screen(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x == 1920;
    }

    public static boolean is720Screen(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x == 1280;
    }

    public static float valuesByScreen(Activity activity, float values) {
        if (is1080Screen(activity)) {
            return values * 1.5f;
        }
        return values;
    }

    public static int valuesByScreen(Activity activity, int values) {
        if (is1080Screen(activity)) {
            return (int) (((float) values) * 1.5f);
        }
        return values;
    }

    public static long valuesByScreen(Activity activity, long values) {
        if (is1080Screen(activity)) {
            return (long) (((float) values) * 1.5f);
        }
        return values;
    }
}
