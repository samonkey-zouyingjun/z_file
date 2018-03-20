package com.zidoo.custom.down;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.zidoo.custom.db.DBConstants.Constant;
import com.zidoo.custom.db.DBHelper;
import java.io.File;
import java.util.HashMap;

public class ZidooDownApkBaseManger {
    private static final String APP_ID = "app_id";
    private static final String DOWNLOAD_ICON_URL = "icon_url";
    private static final String DOWNLOAD_KBS = "kbs";
    private static final String DOWNLOAD_SIZE = "size";
    private static final String DOWNLOAD_STATE = "state";
    private static final String DOWNLOAD_URL = "url";
    private static final String DOWNLOAD_VERSION = "version";
    private static final String KEY_DOWNLOAD_LENGTH = "downloadlength";
    private static final String KEY_DOWNLOND_NUM = "downloadnum";
    private static final String KEY_FILEPATH = "filepath";
    private static final String KEY_NAME = "name";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_SOFT_CODE = "code";
    private static final String KEY_TOTAL_LENGTH = "totallength";
    public static final String SOFT_DOAN_DB = "create table if not exists softdowntable (_id integer primary key autoincrement, packageName text not null, name text, app_id text, downloadnum integer, url text not null, state integer, filepath text, icon_url text, kbs text, version text, size text, code text, totallength integer, downloadlength integer);";
    private static final String TABLE_SOFT_NAME = "softdowntable";

    public ZidooDownApkBaseManger(Context context) {
        if (Constant.db == null) {
            Constant.db = new DBHelper(context).getWritableDatabase();
        }
    }

    public long insertdata(ZidooDownApkInfo zidooDownApkInfo) {
        if (isdb(zidooDownApkInfo.getpName())) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put("name", zidooDownApkInfo.getName());
        values.put(KEY_PACKAGE_NAME, zidooDownApkInfo.getpName());
        values.put(APP_ID, zidooDownApkInfo.getId());
        values.put(KEY_DOWNLOND_NUM, Integer.valueOf(0));
        if (zidooDownApkInfo.getApkurl() == null) {
            values.put("url", "");
        } else {
            values.put("url", zidooDownApkInfo.getApkurl());
        }
        if (zidooDownApkInfo.getApkpath() == null) {
            values.put(KEY_FILEPATH, "");
        } else {
            values.put(KEY_FILEPATH, zidooDownApkInfo.getApkpath());
        }
        values.put(DOWNLOAD_ICON_URL, zidooDownApkInfo.getIconUrl());
        values.put("version", zidooDownApkInfo.getVersion());
        values.put(DOWNLOAD_SIZE, zidooDownApkInfo.getSize());
        values.put(DOWNLOAD_STATE, Integer.valueOf(1));
        values.put(KEY_DOWNLOAD_LENGTH, Integer.valueOf(0));
        values.put(KEY_TOTAL_LENGTH, Integer.valueOf(0));
        values.put(KEY_SOFT_CODE, zidooDownApkInfo.getCode());
        values.put(DOWNLOAD_KBS, zidooDownApkInfo.getKbs());
        return Constant.db.insert(TABLE_SOFT_NAME, null, values);
    }

    public HashMap<String, ZidooDownApkInfo> queryData() {
        Cursor cursor = Constant.db.query(TABLE_SOFT_NAME, null, null, null, null, null, null);
        HashMap<String, ZidooDownApkInfo> marketSoftInfoHash = new HashMap();
        if (cursor != null) {
            int c_name = cursor.getColumnIndex("name");
            int c_pname = cursor.getColumnIndex(KEY_PACKAGE_NAME);
            int c_id = cursor.getColumnIndex(APP_ID);
            int c_num = cursor.getColumnIndex(KEY_DOWNLOND_NUM);
            int c_apkurl = cursor.getColumnIndex("url");
            int c_iconurl = cursor.getColumnIndex(DOWNLOAD_ICON_URL);
            int c_statu = cursor.getColumnIndex(DOWNLOAD_STATE);
            int c_apkpath = cursor.getColumnIndex(KEY_FILEPATH);
            int c_length = cursor.getColumnIndex(KEY_DOWNLOAD_LENGTH);
            int c_total_length = cursor.getColumnIndex(KEY_TOTAL_LENGTH);
            int c_vesion = cursor.getColumnIndex("version");
            int c_code = cursor.getColumnIndex(KEY_SOFT_CODE);
            int c_size = cursor.getColumnIndex(DOWNLOAD_SIZE);
            int c_kbs = cursor.getColumnIndex(DOWNLOAD_KBS);
            while (cursor.moveToNext()) {
                ZidooDownApkInfo marketSoftInfo = new ZidooDownApkInfo();
                marketSoftInfo.setName(cursor.getString(c_name));
                marketSoftInfo.setpName(cursor.getString(c_pname));
                marketSoftInfo.setId(cursor.getString(c_id));
                marketSoftInfo.setDownNum(cursor.getInt(c_num));
                marketSoftInfo.setApkurl(cursor.getString(c_apkurl));
                marketSoftInfo.setIconUrl(cursor.getString(c_iconurl));
                marketSoftInfo.setDownStatu(cursor.getInt(c_statu));
                marketSoftInfo.setApkpath(cursor.getString(c_apkpath));
                marketSoftInfo.setDownLength(cursor.getInt(c_length));
                marketSoftInfo.setTotalLength(cursor.getInt(c_total_length));
                marketSoftInfo.setVersion(cursor.getString(c_vesion));
                marketSoftInfo.setCode(cursor.getString(c_code));
                marketSoftInfo.setSize(cursor.getString(c_size));
                marketSoftInfo.setKbs(cursor.getString(c_kbs));
                if (marketSoftInfo.getDownStatu() == 6 || marketSoftInfo.getDownStatu() == 8) {
                    deleteDownRecord(marketSoftInfo.getpName(), false);
                } else {
                    if (marketSoftInfo.getDownStatu() == 1) {
                        updataDownFlag(marketSoftInfo.getpName(), 2);
                        marketSoftInfo.setDownStatu(2);
                    } else if (marketSoftInfo.getDownStatu() == 7) {
                        updataDownFlag(marketSoftInfo.getpName(), 3);
                        marketSoftInfo.setDownStatu(3);
                    }
                    marketSoftInfoHash.put(marketSoftInfo.getpName(), marketSoftInfo);
                }
            }
            cursor.close();
        }
        return marketSoftInfoHash;
    }

    public int queryDownStatus(String pname) {
        int type = 0;
        Cursor cursor = queryATsoftPackName(pname);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                type = cursor.getInt(cursor.getColumnIndex(DOWNLOAD_STATE));
            }
            cursor.close();
        }
        return type;
    }

    public int queryDownLoadFlag(String pname, String version, String size) {
        int type = 0;
        Cursor cursor = Constant.db.query(TABLE_SOFT_NAME, null, "packageName =?AND version =?AND size =?", new String[]{pname, version, size}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                type = cursor.getInt(cursor.getColumnIndex(DOWNLOAD_STATE));
            }
            cursor.close();
        }
        return type;
    }

    public boolean isdb(String pname) {
        if (pname == null || pname.equals("")) {
            return false;
        }
        boolean type = false;
        Cursor cursor = queryATsoftPackName(pname);
        if (cursor == null) {
            return false;
        }
        while (cursor.moveToNext()) {
            type = true;
        }
        cursor.close();
        return type;
    }

    public String queryFilepath(String pname) {
        String type = null;
        Cursor cursor = queryATsoftPackName(pname);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                type = cursor.getString(cursor.getColumnIndex(KEY_FILEPATH));
            }
            cursor.close();
        }
        return type;
    }

    public void updataDownFlag(String softPackName, int flag) {
        ContentValues values = new ContentValues();
        values.put(DOWNLOAD_STATE, Integer.valueOf(flag));
        Constant.db.update(TABLE_SOFT_NAME, values, "packageName =?", new String[]{softPackName});
    }

    public int getUpdateDownloadLength(String softPackName) {
        int length = 0;
        Cursor cursor = queryATsoftPackName(softPackName);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                length = cursor.getInt(cursor.getColumnIndex(KEY_DOWNLOAD_LENGTH));
            }
            cursor.close();
        }
        return length;
    }

    public Object[] getDownloadData(String softPackName) {
        Object[] result = new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), "", "0kb/s"};
        Cursor cursor = queryATsoftPackName(softPackName);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int type = cursor.getInt(cursor.getColumnIndex(DOWNLOAD_STATE));
                int downPercent = cursor.getInt(cursor.getColumnIndex(KEY_DOWNLOND_NUM));
                int downLength = cursor.getInt(cursor.getColumnIndex(KEY_DOWNLOAD_LENGTH));
                String totalSize = cursor.getString(cursor.getColumnIndex(DOWNLOAD_SIZE));
                String kbs = cursor.getString(cursor.getColumnIndex(DOWNLOAD_KBS));
                result[0] = Integer.valueOf(type);
                result[1] = Integer.valueOf(downPercent);
                result[2] = Integer.valueOf(downLength);
                result[3] = totalSize;
                result[4] = kbs;
            }
            cursor.close();
        }
        return result;
    }

    public void upDateTotalLength(String softPackName, long totalLength) {
        ContentValues values = new ContentValues();
        values.put(KEY_TOTAL_LENGTH, Long.valueOf(totalLength));
        Constant.db.update(TABLE_SOFT_NAME, values, "packageName =?", new String[]{softPackName});
    }

    public void updateDownLength(String softPackName, int momentlength, int momentnum, String kbs) {
        ContentValues values = new ContentValues();
        values.put(KEY_DOWNLOND_NUM, Integer.valueOf(momentnum));
        values.put(KEY_DOWNLOAD_LENGTH, Integer.valueOf(momentlength));
        values.put(DOWNLOAD_KBS, kbs);
        Constant.db.update(TABLE_SOFT_NAME, values, "packageName =?", new String[]{softPackName});
    }

    public void updateDownPath(String softPackName, String urlpath) {
        ContentValues values = new ContentValues();
        values.put(KEY_FILEPATH, urlpath);
        Constant.db.update(TABLE_SOFT_NAME, values, "packageName =?", new String[]{softPackName});
    }

    public void updateDownUrl(String softPackName, String url) {
        ContentValues values = new ContentValues();
        values.put("url", url);
        Constant.db.update(TABLE_SOFT_NAME, values, "packageName =?", new String[]{softPackName});
    }

    public Cursor queryATsoftPackName(String softPackName) {
        return Constant.db.query(TABLE_SOFT_NAME, null, "packageName =?", new String[]{softPackName}, null, null, null);
    }

    public void deleteDownRecord(String softPackName, boolean isJudge) {
        Cursor cursor = queryATsoftPackName(softPackName);
        if (cursor != null) {
            if (!isJudge || ZidooDownApkService.isDeleteApkFile) {
                int c_apkpath = cursor.getColumnIndex(KEY_FILEPATH);
                while (cursor.moveToNext()) {
                    try {
                        File file = new File(cursor.getString(c_apkpath));
                        if (file.exists()) {
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Constant.db.delete(TABLE_SOFT_NAME, "packageName =?", new String[]{softPackName});
            cursor.close();
        }
    }
}
