package com.zidoo.custom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zidoo.custom.app.AppDBConstants;
import com.zidoo.custom.down.ZidooDownApkBaseManger;
import com.zidoo.custom.lock.LockDBConstants;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppDBConstants.CREATE_ORDER_APP_DATABASE);
        db.execSQL(ZidooDownApkBaseManger.SOFT_DOAN_DB);
        db.execSQL(LockDBConstants.LOCK_APP_DATABASE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
