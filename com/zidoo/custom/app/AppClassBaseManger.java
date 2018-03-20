package com.zidoo.custom.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.zidoo.custom.db.DBConstants.Constant;
import com.zidoo.custom.db.DBHelper;
import java.util.ArrayList;

public class AppClassBaseManger {
    public AppClassBaseManger(Context context) {
        if (Constant.db == null) {
            Constant.db = new DBHelper(context).getWritableDatabase();
        }
    }

    public long changeInsertData(String packname, String type, long oredrTime) {
        if (packname == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        if (oredrTime > 0) {
            values.put(AppDBConstants.KEY_ORDER, Long.valueOf(oredrTime));
        } else {
            values.put(AppDBConstants.KEY_ORDER, Long.valueOf(System.currentTimeMillis()));
        }
        values.put(AppDBConstants.KEY_TYPE, type);
        if (isdb(packname)) {
            return (long) Constant.db.update(AppDBConstants.TABLE_ORDER_APP_NAME, values, "packname =?", new String[]{packname});
        }
        values.put("packname", packname);
        values.put(AppDBConstants.KEY_COUNT, Integer.valueOf(0));
        return Constant.db.insert(AppDBConstants.TABLE_ORDER_APP_NAME, null, values);
    }

    public long changeType(String newPname, String type, String oldPname) {
        long oldOrder = -1;
        if (oldPname != null) {
            Cursor oldcursor = queryCursor(oldPname);
            if (oldcursor != null) {
                while (oldcursor.moveToNext()) {
                    oldOrder = oldcursor.getLong(oldcursor.getColumnIndex(AppDBConstants.KEY_ORDER));
                }
                oldcursor.close();
            }
            deleteType(oldPname);
        }
        return changeInsertData(newPname, type, oldOrder);
    }

    public long addTypeApp(String pname, String type) {
        return changeInsertData(pname, type, -1);
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

    public ArrayList<APPSoftInfo> queryAppByType(String type) {
        ArrayList<APPSoftInfo> dblist = new ArrayList();
        Cursor cursor = Constant.db.query(AppDBConstants.TABLE_ORDER_APP_NAME, null, "classtype =?", new String[]{type}, null, null, "softorder asc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                APPSoftInfo aPPSoftInfo = new APPSoftInfo();
                aPPSoftInfo.setPackageName(cursor.getString(cursor.getColumnIndex("packname")));
                dblist.add(aPPSoftInfo);
            }
            cursor.close();
        }
        return dblist;
    }

    public long getOrderTime(String packname) {
        if (packname == null) {
            return -1;
        }
        Cursor cursor = queryCursor(packname);
        long orderTime = -1;
        if (cursor == null) {
            return -1;
        }
        while (cursor.moveToNext()) {
            orderTime = cursor.getLong(cursor.getColumnIndex(AppDBConstants.KEY_ORDER));
        }
        cursor.close();
        return orderTime;
    }

    public Cursor queryCursor(String packname) {
        if (packname == null) {
            return null;
        }
        return Constant.db.query(AppDBConstants.TABLE_ORDER_APP_NAME, null, "packname =?", new String[]{packname}, null, null, null);
    }

    public int deleteType(String packname) {
        if (packname == null) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(AppDBConstants.KEY_TYPE, ZidooAppManager.ZIDOOHINTTYPE);
        values.put(AppDBConstants.KEY_ORDER, Integer.valueOf(-1));
        return Constant.db.update(AppDBConstants.TABLE_ORDER_APP_NAME, values, "packname =?", new String[]{packname});
    }

    public int deleteApp(String packname) {
        if (packname == null) {
            return 0;
        }
        return Constant.db.delete(AppDBConstants.TABLE_ORDER_APP_NAME, "packname =?", new String[]{packname});
    }

    public boolean changeTypePosition(String newPname, String oldPname) {
        Cursor cursor = queryCursor(newPname);
        long newOrder = -1;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                newOrder = cursor.getLong(cursor.getColumnIndex(AppDBConstants.KEY_ORDER));
            }
            cursor.close();
        }
        Cursor oldcursor = queryCursor(oldPname);
        long oldOrder = -1;
        if (oldcursor != null) {
            while (oldcursor.moveToNext()) {
                oldOrder = oldcursor.getLong(oldcursor.getColumnIndex(AppDBConstants.KEY_ORDER));
            }
            oldcursor.close();
        }
        if (newOrder == -1 || oldOrder == -1) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(AppDBConstants.KEY_ORDER, Long.valueOf(newOrder));
        Constant.db.update(AppDBConstants.TABLE_ORDER_APP_NAME, values, "packname =?", new String[]{oldPname});
        values = new ContentValues();
        values.put(AppDBConstants.KEY_ORDER, Long.valueOf(oldOrder));
        Constant.db.update(AppDBConstants.TABLE_ORDER_APP_NAME, values, "packname =?", new String[]{newPname});
        return true;
    }

    public long onclickInsertData(String packname) {
        if (packname == null) {
            return -1;
        }
        long timeCount = getOnclikTime(packname);
        ContentValues values;
        if (timeCount == -1) {
            values = new ContentValues();
            values.put("packname", packname);
            values.put(AppDBConstants.KEY_TYPE, ZidooAppManager.ZIDOOHINTTYPE);
            values.put(AppDBConstants.KEY_COUNT, Integer.valueOf(1));
            values.put(AppDBConstants.KEY_ORDER, Integer.valueOf(-1));
            Constant.db.insert(AppDBConstants.TABLE_ORDER_APP_NAME, null, values);
        } else {
            long allTimeCount = timeCount + 1;
            values = new ContentValues();
            values.put(AppDBConstants.KEY_COUNT, Long.valueOf(allTimeCount));
            Constant.db.update(AppDBConstants.TABLE_ORDER_APP_NAME, values, "packname =?", new String[]{packname});
        }
        return 1;
    }

    public ArrayList<APPSoftInfo> queryAllApp() {
        ArrayList<APPSoftInfo> dblist = new ArrayList();
        Cursor cursor = Constant.db.query(AppDBConstants.TABLE_ORDER_APP_NAME, null, null, null, null, null, "onclicktime desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                APPSoftInfo aPPSoftInfo = new APPSoftInfo();
                aPPSoftInfo.setPackageName(cursor.getString(cursor.getColumnIndex("packname")));
                aPPSoftInfo.setClickCount(cursor.getLong(cursor.getColumnIndex(AppDBConstants.KEY_COUNT)));
                dblist.add(aPPSoftInfo);
            }
            cursor.close();
        }
        return dblist;
    }

    private long getOnclikTime(String packname) {
        try {
            Cursor cursor = queryCursor(packname);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    return cursor.getLong(cursor.getColumnIndex(AppDBConstants.KEY_COUNT));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
