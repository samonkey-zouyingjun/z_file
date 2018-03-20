package com.zidoo.custom.lock;

public class LockDBConstants {
    public static final String KEY_LOCK = "islock";
    public static final String KEY_PACKNAME = "packname";
    public static final String LOCK_APP_DATABASE = "create table if not exists locktable (_id integer primary key autoincrement, packname text not null, islock integer);";
    public static final String TABLE_LOCK_NAME = "locktable";
}
