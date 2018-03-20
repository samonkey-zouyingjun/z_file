package com.umeng.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.media.session.PlaybackStateCompat;
import com.umeng.analytics.a.g;
import com.umeng.common.Log;
import com.umeng.common.util.e;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: UmengStoreHelper */
public final class i {
    static long a = 1209600000;
    static long b = PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE;
    private static final String c = "cache_version";
    private static final String d = "error";
    private static final String e = "mobclick_agent_user_";
    private static final String f = "mobclick_agent_online_setting_";
    private static final String g = "mobclick_agent_header_";
    private static final String h = "mobclick_agent_update_";
    private static final String i = "mobclick_agent_state_";
    private static final String j = "mobclick_agent_cached_";

    static SharedPreferences a(Context context) {
        return context.getSharedPreferences(e + context.getPackageName(), 0);
    }

    public static SharedPreferences b(Context context) {
        return context.getSharedPreferences(f + context.getPackageName(), 0);
    }

    public static SharedPreferences c(Context context) {
        return context.getSharedPreferences(g + context.getPackageName(), 0);
    }

    static SharedPreferences d(Context context) {
        return context.getSharedPreferences(h + context.getPackageName(), 0);
    }

    public static SharedPreferences e(Context context) {
        return context.getSharedPreferences(i + context.getPackageName(), 0);
    }

    static String f(Context context) {
        return g + context.getPackageName();
    }

    static String g(Context context) {
        return j + context.getPackageName();
    }

    static JSONObject h(Context context) {
        JSONObject jSONObject = new JSONObject();
        SharedPreferences a = a(context);
        try {
            if (a.getInt("gender", -1) != -1) {
                jSONObject.put("sex", a.getInt("gender", -1));
            }
            if (a.getInt("age", -1) != -1) {
                jSONObject.put("age", a.getInt("age", -1));
            }
            if (!"".equals(a.getString("user_id", ""))) {
                jSONObject.put("id", a.getString("user_id", ""));
            }
            if (!"".equals(a.getString("id_source", ""))) {
                jSONObject.put("url", URLEncoder.encode(a.getString("id_source", ""), e.f));
            }
            if (jSONObject.length() > 0) {
                return jSONObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int[] i(Context context) {
        SharedPreferences b = b(context);
        int[] iArr = new int[2];
        if (b.getInt(f.A, -1) != -1) {
            iArr[0] = b.getInt(f.A, 1);
            iArr[1] = (int) b.getLong(f.B, (long) f.h);
        } else {
            iArr[0] = b.getInt(f.D, 1);
            iArr[1] = (int) b.getLong(f.E, (long) f.h);
        }
        return iArr;
    }

    static boolean a(File file) {
        long length = file.length();
        if (!file.exists() || length <= b) {
            return false;
        }
        return true;
    }

    static JSONObject a(Context context, String str) {
        String g = g(context);
        try {
            File file = new File(context.getFilesDir(), g);
            String a = a(file);
            if (a != null) {
                file.delete();
                return null;
            }
            InputStream openFileInput = context.openFileInput(g);
            try {
                a = a(openFileInput);
                try {
                    JSONObject jSONObject = new JSONObject(a);
                    if (!jSONObject.optString(c).equals(str)) {
                        jSONObject.remove(d);
                    }
                    jSONObject.remove(c);
                    if (jSONObject.length() != 0) {
                        return jSONObject;
                    }
                    return null;
                } catch (JSONException e) {
                    j(context);
                    e.printStackTrace();
                    return null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            } finally {
                openFileInput.close();
            }
        } catch (FileNotFoundException e3) {
            return null;
        } catch (IOException e4) {
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    static String a(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] cArr = new char[1024];
        StringWriter stringWriter = new StringWriter();
        while (true) {
            int read = inputStreamReader.read(cArr);
            if (-1 == read) {
                return stringWriter.toString();
            }
            stringWriter.write(cArr, 0, read);
        }
    }

    static void a(Context context, JSONObject jSONObject, String str) throws Exception {
        if (jSONObject != null) {
            String g = g(context);
            jSONObject.put(c, str);
            FileOutputStream openFileOutput = context.openFileOutput(g, 0);
            openFileOutput.write(jSONObject.toString().getBytes());
            openFileOutput.flush();
            openFileOutput.close();
            Log.c(f.q, "cache buffer success");
        }
    }

    static void a(Context context, g gVar, String str) {
        if (gVar != null) {
            try {
                JSONObject jSONObject = new JSONObject();
                gVar.b(jSONObject);
                a(context, jSONObject, str);
            } catch (Exception e) {
                Log.b(f.q, "cache message error", e);
            }
        }
    }

    static void b(Context context, JSONObject jSONObject, String str) {
        if (jSONObject != null) {
            try {
                a(context, jSONObject.optJSONObject("body"), str);
            } catch (Exception e) {
                Log.b(f.q, "cache message error", e);
            }
        }
    }

    static void j(Context context) {
        context.deleteFile(f(context));
        context.deleteFile(g(context));
    }
}
