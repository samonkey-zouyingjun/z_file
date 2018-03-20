package com.zidoo.custom.lock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.zidoo.custom.db.DBConstants.Constant;
import com.zidoo.custom.db.DBHelper;

public class LockBaseManger {
    public LockBaseManger(Context context) {
        if (Constant.db == null) {
            Constant.db = new DBHelper(context).getWritableDatabase();
        }
    }

    public boolean isLock(String packname) {
        Cursor cursor = queryCursor(packname);
        boolean isLock = false;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                isLock = cursor.getInt(cursor.getColumnIndex(LockDBConstants.KEY_LOCK)) == 1;
            }
            cursor.close();
        }
        return isLock;
    }

    public void setLock(String packname, boolean isLock) {
        ContentValues values = new ContentValues();
        values.put(LockDBConstants.KEY_LOCK, Integer.valueOf(isLock ? 1 : 0));
        if (isdb(packname)) {
            Constant.db.update(LockDBConstants.TABLE_LOCK_NAME, values, "packname =?", new String[]{packname});
            return;
        }
        values.put("packname", packname);
        Constant.db.insert(LockDBConstants.TABLE_LOCK_NAME, null, values);
    }

    private boolean isdb(String packname) {
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

    private Cursor queryCursor(String packname) {
        if (packname == null) {
            return null;
        }
        return Constant.db.query(LockDBConstants.TABLE_LOCK_NAME, null, "packname =?", new String[]{packname}, null, null, null);
    }
}
