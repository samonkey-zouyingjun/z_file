package com.zidoo.custom.app;

public class AppDBConstants {
    public static final String CREATE_ORDER_APP_DATABASE = "create table if not exists appordertable (_id integer primary key autoincrement, packname text not null, classtype text, onclicktime text, softorder Long);";
    public static final String KEY_COUNT = "onclicktime";
    public static final String KEY_ORDER = "softorder";
    public static final String KEY_PACKNAME = "packname";
    public static final String KEY_TYPE = "classtype";
    public static final String TABLE_ORDER_APP_NAME = "appordertable";
}
