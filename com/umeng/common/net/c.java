package com.umeng.common.net;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.umeng.common.Log;
import com.umeng.common.util.g;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* compiled from: DownloadTaskList */
public class c {
    private static final String a = c.class.getName();
    private static final String b = "umeng_download_task_list";
    private static final String c = "UMENG_DATA";
    private static final String d = "cp";
    private static final String e = "url";
    private static final String f = "progress";
    private static final String g = "last_modified";
    private static final String h = "extra";
    private static Context i = null;
    private static final String j = "yyyy-MM-dd HH:mm:ss";
    private a k;

    /* compiled from: DownloadTaskList */
    class a extends SQLiteOpenHelper {
        private static final int b = 2;
        private static final String c = "CREATE TABLE umeng_download_task_list (cp TEXT, url TEXT, progress INTEGER, extra TEXT, last_modified TEXT, UNIQUE (cp,url) ON CONFLICT ABORT);";
        final /* synthetic */ c a;

        a(c cVar, Context context) {
            this.a = cVar;
            super(context, c.c, null, 2);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            Log.c(c.a, c);
            sQLiteDatabase.execSQL(c);
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    /* compiled from: DownloadTaskList */
    private static class b {
        public static final c a = new c();

        private b() {
        }
    }

    private c() {
        this.k = new a(this, i);
    }

    public static c a(Context context) {
        if (i == null && context == null) {
            throw new NullPointerException();
        }
        if (i == null) {
            i = context;
        }
        return b.a;
    }

    public boolean a(String str, String str2) {
        boolean z;
        Exception exception;
        ContentValues contentValues = new ContentValues();
        contentValues.put(d, str);
        contentValues.put("url", str2);
        contentValues.put("progress", Integer.valueOf(0));
        contentValues.put(g, g.a());
        try {
            String[] strArr = new String[]{str, str2};
            Cursor query = this.k.getReadableDatabase().query(b, new String[]{"progress"}, "cp=? and url=?", strArr, null, null, null, "1");
            if (query.getCount() > 0) {
                Log.c(a, "insert(" + str + ", " + str2 + "): " + " already exists in the db. Insert is cancelled.");
                z = false;
            } else {
                boolean z2;
                long insert = this.k.getWritableDatabase().insert(b, null, contentValues);
                if (insert == -1) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                try {
                    Log.c(a, "insert(" + str + ", " + str2 + "): " + "rowid=" + insert);
                    z = z2;
                } catch (Exception e) {
                    Exception exception2 = e;
                    z = z2;
                    exception = exception2;
                    Log.c(a, "insert(" + str + ", " + str2 + "): " + exception.getMessage(), exception);
                    return z;
                }
            }
            try {
                query.close();
            } catch (Exception e2) {
                exception = e2;
                Log.c(a, "insert(" + str + ", " + str2 + "): " + exception.getMessage(), exception);
                return z;
            }
        } catch (Exception e3) {
            exception = e3;
            z = false;
            Log.c(a, "insert(" + str + ", " + str2 + "): " + exception.getMessage(), exception);
            return z;
        }
        return z;
    }

    public void a(String str, String str2, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("progress", Integer.valueOf(i));
        contentValues.put(g, g.a());
        String[] strArr = new String[]{str, str2};
        this.k.getWritableDatabase().update(b, contentValues, "cp=? and url=?", strArr);
        Log.c(a, "updateProgress(" + str + ", " + str2 + ", " + i + ")");
    }

    public void a(String str, String str2, String str3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(h, str3);
        contentValues.put(g, g.a());
        String[] strArr = new String[]{str, str2};
        this.k.getWritableDatabase().update(b, contentValues, "cp=? and url=?", strArr);
        Log.c(a, "updateExtra(" + str + ", " + str2 + ", " + str3 + ")");
    }

    public int b(String str, String str2) {
        int i;
        String[] strArr = new String[]{str, str2};
        Cursor query = this.k.getReadableDatabase().query(b, new String[]{"progress"}, "cp=? and url=?", strArr, null, null, null, "1");
        if (query.getCount() > 0) {
            query.moveToFirst();
            i = query.getInt(0);
        } else {
            i = -1;
        }
        query.close();
        return i;
    }

    public String c(String str, String str2) {
        String str3 = null;
        String[] strArr = new String[]{str, str2};
        Cursor query = this.k.getReadableDatabase().query(b, new String[]{h}, "cp=? and url=?", strArr, null, null, null, "1");
        if (query.getCount() > 0) {
            query.moveToFirst();
            str3 = query.getString(0);
        }
        query.close();
        return str3;
    }

    public Date d(String str, String str2) {
        Date date = null;
        String[] strArr = new String[]{str, str2};
        Cursor query = this.k.getReadableDatabase().query(b, new String[]{g}, "cp=? and url=?", strArr, date, date, date, date);
        if (query.getCount() > 0) {
            query.moveToFirst();
            String string = query.getString(0);
            Log.c(a, "getLastModified(" + str + ", " + str2 + "): " + string);
            try {
                date = new SimpleDateFormat(j).parse(string);
            } catch (Exception e) {
                Log.c(a, e.getMessage());
            }
        }
        query.close();
        return date;
    }

    public void e(String str, String str2) {
        String[] strArr = new String[]{str, str2};
        this.k.getWritableDatabase().delete(b, "cp=? and url=?", strArr);
        Log.c(a, "delete(" + str + ", " + str2 + ")");
    }

    public List<String> a(String str) {
        String[] strArr = new String[]{str};
        Cursor query = this.k.getReadableDatabase().query(b, new String[]{"url"}, "cp=?", strArr, null, null, null, "1");
        List<String> arrayList = new ArrayList();
        query.moveToFirst();
        while (!query.isAfterLast()) {
            arrayList.add(query.getString(0));
            query.moveToNext();
        }
        query.close();
        return arrayList;
    }

    public void a(int i) {
        try {
            Date date = new Date(new Date().getTime() - ((long) (i * 1000)));
            this.k.getWritableDatabase().execSQL(" DELETE FROM umeng_download_task_list WHERE strftime('yyyy-MM-dd HH:mm:ss', last_modified)<=strftime('yyyy-MM-dd HH:mm:ss', '" + new SimpleDateFormat(j).format(date) + "')");
            Log.c(a, "clearOverdueTasks(" + i + ")" + " remove all tasks before " + new SimpleDateFormat(j).format(date));
        } catch (Exception e) {
            Log.b(a, e.getMessage());
        }
    }

    public void finalize() {
        this.k.close();
    }
}
