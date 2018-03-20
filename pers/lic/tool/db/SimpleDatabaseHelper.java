package pers.lic.tool.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import com.zidoo.custom.db.DBConstants;
import java.util.ArrayList;

public abstract class SimpleDatabaseHelper<T> extends DatabaseHelper<T> {
    private String path = null;

    public abstract ContentValues genContentValues(T t);

    public abstract long getId(T t);

    public SimpleDatabaseHelper(Context context) {
        super(context);
    }

    public int insert(T t) {
        return (int) insert(genContentValues(t));
    }

    public int delete(long id) {
        return delete("_id=?", new String[]{String.valueOf(id)});
    }

    public int update(T t) {
        return update(genContentValues(t), "_id=?", new String[]{String.valueOf(getId(t))});
    }

    public ArrayList<T> queryAll() {
        return query(null, null, null, null, null, null, null);
    }

    public ArrayList<T> queryAll(String column, Object value) {
        return query(null, column + "=?", new String[]{String.valueOf(value)}, null, null, null, null);
    }

    public T query(long id) {
        return query(DBConstants.KEY_AOTO, Long.valueOf(id));
    }

    public T query(String column, Object value) {
        return queryOne(null, column + "=?", new String[]{String.valueOf(value)}, null, null, null, null);
    }

    public SparseArray<T> queryToSparseArray() {
        SparseArray<T> list = new SparseArray();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = beginReadTransaction();
            cursor = db.query(getTableName(), null, null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    T t = construct(cursor);
                    list.put((int) getId(t), t);
                }
            }
            db.setTransactionSuccessful();
            endTransaction(db);
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return list;
        } finally {
            endTransaction(db);
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
}
