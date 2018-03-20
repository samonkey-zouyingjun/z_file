package com.zidoo.fileexplorer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.umeng.common.a;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.MyLog;
import java.util.ArrayList;
import java.util.Iterator;
import zidoo.samba.exs.SambaDevice;

public class SmbUserDB extends SQLiteOpenHelper {
    private String CREATE_TEMP_BOOK = "alter table smbuser rename to temp_smbuser";
    private String DROP_BOOK = "drop table temp_smbuser";

    public SmbUserDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppConstant.DB_SMB_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table smbuser");
        db.execSQL(AppConstant.DB_SMB_CREATE);
    }

    private ArrayList<SambaDevice> queryAllByVersion(SQLiteDatabase db, int version) {
        ArrayList<SambaDevice> devices = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, null, null, null, null, " id desc", null);
            if (cursor != null) {
                if (version == 1) {
                    while (cursor.moveToNext()) {
                        devices.add(new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                    }
                } else if (version == 2) {
                    while (cursor.moveToNext()) {
                        String ip = cursor.getString(1);
                        String hostName = cursor.getString(4);
                        if (hostName == null || hostName.equals("")) {
                            hostName = ip;
                        }
                        devices.add(new SambaDevice(ip, cursor.getString(2), cursor.getString(3), hostName));
                    }
                } else {
                    while (cursor.moveToNext()) {
                        devices.add(new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            MyLog.w("queryAllByVersion", e);
            return devices;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return devices;
    }

    private void save(SQLiteDatabase db, ArrayList<SambaDevice> devices) {
        Iterator it = devices.iterator();
        while (it.hasNext()) {
            SambaDevice device = (SambaDevice) it.next();
            ContentValues values = new ContentValues();
            values.put("url", device.getUrl());
            values.put("host", device.getHost());
            values.put(AppConstant.DB_SMB_IP, device.getIp());
            values.put("user", device.getUser());
            values.put("password", device.getPassWord());
            values.put(a.b, Integer.valueOf(device.getType()));
            db.insert(AppConstant.DB_SMB_TABLE_NAME, null, values);
        }
    }
}
