package com.zidoo.fileexplorer.tool;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import zidoo.http.HTTP;

public class MyLog {
    private static String TAG = "lisupan";
    private static ArrayList<String> msgs = new ArrayList();
    private static boolean sDebug = true;
    private static QueueTextView sLogText = null;

    private static final class LogRunn implements Runnable {
        String msg;
        QueueTextView textView;

        public LogRunn(QueueTextView textView, String msg) {
            this.textView = textView;
            this.msg = msg;
        }

        public void run() {
            this.textView.setMessage(this.msg);
        }
    }

    private static final class QueueTextView extends TextView {
        final int MAX = 10;
        Queue<String> msgs = new LinkedBlockingDeque();

        public QueueTextView(Context context) {
            super(context);
        }

        public void setMessage(String msg) {
            if (this.msgs.size() >= 10) {
                this.msgs.poll();
            }
            this.msgs.offer(msg);
            String text = "";
            for (String str : this.msgs) {
                text = text + str + HTTP.CRLF;
            }
            setText(text);
        }
    }

    public static void setDebug(Boolean debug) {
        sDebug = debug.booleanValue();
    }

    public static void log(Object msg) {
        if (sDebug) {
            Log.i(TAG, String.valueOf(msg) + ":" + msg);
        }
    }

    public static void i(String msg) {
        if (sDebug) {
            Log.i(TAG, msg);
            if (sLogText != null) {
                sLogText.post(new LogRunn(sLogText, msg));
            }
        }
    }

    public static void i(String msg, Throwable e) {
        if (sDebug) {
            Log.i(TAG, msg, e);
        }
    }

    public static void e(String msg) {
        if (sDebug) {
            Log.e(TAG, msg);
            if (sLogText != null) {
                sLogText.post(new LogRunn(sLogText, msg));
            }
        }
    }

    public static void e(String msg, Throwable e) {
        if (sDebug) {
            Log.e(TAG, msg, e);
        }
    }

    public static void w(String msg) {
        if (sDebug) {
            Log.w(TAG, msg);
            if (sLogText != null) {
                sLogText.post(new LogRunn(sLogText, msg));
            }
        }
    }

    public static void w(String msg, Throwable e) {
        if (sDebug) {
            Log.w(TAG, msg, e);
        }
    }

    public static void v(String msg) {
        if (sDebug) {
            Log.v(TAG, msg);
        }
    }

    public static void v(String msg, Throwable e) {
        if (sDebug) {
            Log.v(TAG, msg, e);
        }
    }

    public static void d(String msg) {
        if (sDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String msg, Throwable e) {
        if (sDebug) {
            Log.d(TAG, msg, e);
        }
    }

    public static void clear() {
        msgs.clear();
    }

    public static void put(String msg) {
        msgs.add(msg);
    }

    public static void log() {
        for (int i = 0; i < msgs.size(); i++) {
            Log.i(TAG, (String) msgs.get(i));
        }
        msgs.clear();
    }

    public static void showLogView(Context context) {
        if (sLogText == null) {
            sLogText = new QueueTextView(context);
            sLogText.setWidth(1000);
            sLogText.setMinHeight(40);
            sLogText.setBackgroundColor(Color.parseColor("#88000000"));
            sLogText.setTextSize(12.0f);
            sLogText.setTextColor(-1);
            sLogText.setMaxWidth(120);
        }
        WindowManager wm = (WindowManager) context.getSystemService("window");
        LayoutParams params = new LayoutParams();
        params.type = 2006;
        params.flags = 8;
        params.x = 10;
        params.y = 10;
        params.width = -2;
        params.height = -2;
        params.gravity = 51;
        wm.addView(sLogText, params);
    }

    public static void removeLogView(Context context) {
        if (sLogText != null) {
            ((WindowManager) context.getSystemService("window")).removeView(sLogText);
            sLogText = null;
        }
    }
}
