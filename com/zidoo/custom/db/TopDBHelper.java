package com.zidoo.custom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zidoo.custom.app.AppTopBaseManger;

public class TopDBHelper extends SQLiteOpenHelper {
    public TopDBHelper(Context context) {
        super(context, DBConstants.DATABASE_TOP_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppTopBaseManger.TOP_APP_DATABASE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
