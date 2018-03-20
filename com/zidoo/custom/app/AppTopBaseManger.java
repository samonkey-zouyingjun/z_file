package com.zidoo.custom.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.zidoo.custom.db.DBConstants.Constant;
import com.zidoo.custom.db.TopDBHelper;
import java.util.ArrayList;

public class AppTopBaseManger {
    public static final String KEY_PACKNAME = "packname";
    public static final String KEY_TIME = "time";
    public static final String TABLE_TOP_NAME = "toptable";
    public static final String TOP_APP_DATABASE = "create table if not exists toptable (_id integer primary key autoincrement, packname text not null, time Long);";

    public AppTopBaseManger(Context context) {
        if (Constant.top_db == null) {
            Constant.top_db = new TopDBHelper(context).getWritableDatabase();
        }
    }

    public boolean insertData(String packname) {
        if (packname == null) {
            return false;
        }
        delete(packname);
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        values.put(KEY_TIME, Long.valueOf(System.currentTimeMillis()));
        if (Constant.top_db.insert(TABLE_TOP_NAME, null, values) > 0) {
            return true;
        }
        return false;
    }

    public boolean isdb(String packname) {
        boolean isdbh = false;
        Cursor cursor = queryCursor(packname);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isdbh = true;
            }
            cursor.close();
        }
        return isdbh;
    }

    public Cursor queryCursor(String packname) {
        if (packname == null) {
            return null;
        }
        return Constant.top_db.query(TABLE_TOP_NAME, null, "packname =?", new String[]{packname}, null, null, null);
    }

    public boolean delete(String packname) {
        if (packname == null) {
            return false;
        }
        if (Constant.top_db.delete(TABLE_TOP_NAME, "packname =?", new String[]{packname}) > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> queryAllApp(int maxCount) {
        ArrayList<String> dblist = new ArrayList();
        Cursor cursor = Constant.top_db.query(TABLE_TOP_NAME, null, null, null, null, null, "time desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String pName = cursor.getString(cursor.getColumnIndex("packname"));
                if (dblist.size() == maxCount) {
                    delete(pName);
                } else {
                    dblist.add(pName);
                }
            }
            cursor.close();
        }
        return dblist;
    }
}
