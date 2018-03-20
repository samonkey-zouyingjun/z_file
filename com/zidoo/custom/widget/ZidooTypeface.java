package com.zidoo.custom.widget;

import android.content.Context;
import android.graphics.Typeface;
import com.zidoo.custom.init.ZidooJarPermissions;

public class ZidooTypeface {
    private static String CURRENT_TEXT = null;
    private static Typeface IOS_TYPEFACE = null;

    public static Typeface getIsoTypeface(Context context) {
        ZidooJarPermissions.checkZidooPermissions();
        if (IOS_TYPEFACE != null) {
            return IOS_TYPEFACE;
        }
        try {
            if (CURRENT_TEXT != null) {
                IOS_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/" + CURRENT_TEXT.trim());
            }
            if (IOS_TYPEFACE != null) {
                return IOS_TYPEFACE;
            }
            try {
                String locale = ZidooJarPermissions.LANGUAGE;
                if (locale.equals("en") || locale.equals("zh")) {
                    IOS_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/drod.ttf");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return IOS_TYPEFACE;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static void reset(String currentText) {
        CURRENT_TEXT = currentText;
        IOS_TYPEFACE = null;
    }
}
