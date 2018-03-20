package com.zidoo.fileexplorer.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public abstract class DatabaseHelper<T> {
    protected Context context;

    protected abstract T construct(Cursor cursor);

    protected abstract SQLiteOpenHelper createDatabase();

    protected abstract String getTableName();

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    protected long insert(ContentValues values) {
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = createDatabase().getWritableDatabase();
            db.beginTransaction();
            id = db.insert(getTableName(), null, values);
            db.setTransactionSuccessful();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "insert", e);
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Throwable th) {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return id;
    }

    protected int delete(String whereClause, String[] whereArgs) {
        int number = 0;
        String table = getTableName();
        SQLiteDatabase db = null;
        try {
            db = createDatabase().getWritableDatabase();
            db.beginTransaction();
            number = db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "delete", e);
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Throwable th) {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return number;
    }

    protected int update(ContentValues values, String whereClause, String[] whereArgs) {
        int number = 0;
        SQLiteDatabase db = null;
        try {
            db = createDatabase().getWritableDatabase();
            db.beginTransaction();
            number = db.update(getTableName(), values, whereClause, whereArgs);
            db.setTransactionSuccessful();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "update", e);
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Throwable th) {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return number;
    }

    protected ArrayList<T> query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        ArrayList<T> list = new ArrayList();
        String table = getTableName();
        SQLiteDatabase db = createDatabase().getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(construct(cursor));
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e) {
            Log.e(getClass().getName(), "queryAll", e);
            e.printStackTrace();
            return list;
        } finally {
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}
