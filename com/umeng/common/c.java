package com.umeng.common;

import android.content.Context;

/* compiled from: Res */
public class c {
    private static final String a = c.class.getName();
    private static c b = null;
    private static String c = null;
    private static Class d = null;
    private static Class e = null;
    private static Class f = null;
    private static Class g = null;
    private static Class h = null;
    private static Class i = null;
    private static Class j = null;

    private c(String str) {
        try {
            e = Class.forName(str + ".R$drawable");
        } catch (ClassNotFoundException e) {
            Log.b(a, e.getMessage());
        }
        try {
            f = Class.forName(str + ".R$layout");
        } catch (ClassNotFoundException e2) {
            Log.b(a, e2.getMessage());
        }
        try {
            d = Class.forName(str + ".R$id");
        } catch (ClassNotFoundException e22) {
            Log.b(a, e22.getMessage());
        }
        try {
            g = Class.forName(str + ".R$anim");
        } catch (ClassNotFoundException e222) {
            Log.b(a, e222.getMessage());
        }
        try {
            h = Class.forName(str + ".R$style");
        } catch (ClassNotFoundException e2222) {
            Log.b(a, e2222.getMessage());
        }
        try {
            i = Class.forName(str + ".R$string");
        } catch (ClassNotFoundException e22222) {
            Log.b(a, e22222.getMessage());
        }
        try {
            j = Class.forName(str + ".R$array");
        } catch (ClassNotFoundException e222222) {
            Log.b(a, e222222.getMessage());
        }
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (b == null) {
                c = c != null ? c : context.getPackageName();
                b = new c(c);
            }
            cVar = b;
        }
        return cVar;
    }

    public static void a(String str) {
        c = str;
    }

    public int b(String str) {
        return a(g, str);
    }

    public int c(String str) {
        return a(d, str);
    }

    public int d(String str) {
        return a(e, str);
    }

    public int e(String str) {
        return a(f, str);
    }

    public int f(String str) {
        return a(h, str);
    }

    public int g(String str) {
        return a(i, str);
    }

    public int h(String str) {
        return a(j, str);
    }

    private int a(Class<?> cls, String str) {
        if (cls == null) {
            Log.b(a, "getRes(null," + str + ")");
            throw new IllegalArgumentException("ResClass is not initialized. Please make sure you have added neccessary resources. Also make sure you have " + c + ".R$* configured in obfuscation. field=" + str);
        }
        try {
            return cls.getField(str).getInt(str);
        } catch (Exception e) {
            Log.b(a, "getRes(" + cls.getName() + ", " + str + ")");
            Log.b(a, "Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
            Log.b(a, e.getMessage());
            return -1;
        }
    }
}
