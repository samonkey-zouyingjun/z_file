package com.zidoo.custom.db;

import android.database.sqlite.SQLiteDatabase;
import com.zidoo.custom.app.AppClassBaseManger;
import com.zidoo.custom.app.AppTopBaseManger;
import com.zidoo.custom.down.ZidooDownApkBaseManger;
import com.zidoo.custom.lock.LockBaseManger;

public class DBConstants {
    public static final String DATABASE_NAME = "zidootool.db";
    public static final String DATABASE_TOP_NAME = "zidootop.db";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_AOTO = "_id";

    public static class Constant {
        public static AppClassBaseManger class_dataBase = null;
        public static SQLiteDatabase db = null;
        public static ZidooDownApkBaseManger down_apk_dataBase = null;
        public static AppTopBaseManger mAppTopBaseManger = null;
        public static LockBaseManger mLockBaseManger = null;
        public static SQLiteDatabase top_db = null;
    }
}
