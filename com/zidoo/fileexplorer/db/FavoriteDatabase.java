package com.zidoo.fileexplorer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.tool.DatabaseHelper;
import java.util.ArrayList;

public class FavoriteDatabase extends SQLiteOpenHelper {
    public static final String FILE_TYPE = "ftp";
    public static final String ID = "id";
    public static final String LENGTH = "len";
    public static final String LIST = "list";
    public static final String NAME = "name";
    public static final String PASSWORD = "pwd";
    public static final String TABLE_NAME = "favorite";
    public static final String TAG = "tag";
    public static final String URI = "uri";
    public static final String USER = "user";
    public static final String UUID = "uuid";
    public static final int VERSION = 1;

    public static final class Helper extends DatabaseHelper<Favorite> {
        Helper(Context context) {
            super(context);
        }

        public long insert(Favorite favorite) {
            ContentValues values = new ContentValues();
            values.put("name", favorite.getName());
            values.put(FavoriteDatabase.URI, favorite.getUri());
            values.put(FavoriteDatabase.TAG, Integer.valueOf(favorite.getTag()));
            values.put(FavoriteDatabase.LIST, Integer.valueOf(favorite.getListIndex()));
            values.put(FavoriteDatabase.UUID, favorite.getUuid());
            values.put("user", favorite.getUser());
            values.put(FavoriteDatabase.PASSWORD, favorite.getPassword());
            values.put(FavoriteDatabase.FILE_TYPE, Integer.valueOf(favorite.getFileType()));
            values.put(FavoriteDatabase.LENGTH, Long.valueOf(favorite.getFileLength()));
            return insert(values);
        }

        public int delete(int id) {
            return delete("id = ?", new String[]{String.valueOf(id)});
        }

        public boolean exist(Favorite favorite) {
            return exist(favorite.getUri(), favorite.getUuid(), favorite.getTag());
        }

        public boolean exist(String uri, String uuid, int tag) {
            return exist(uri, uuid, String.valueOf(tag));
        }

        public boolean exist(String uri, String uuid, String tag) {
            return exist("uri=? and uuid=? and tag=?", new String[]{uri, uuid, tag});
        }

        public boolean exist(String selection, String[] selectionArgs) {
            boolean exist = false;
            String table = getTableName();
            SQLiteDatabase db = createDatabase().getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = null;
            try {
                cursor = db.query(table, new String[]{FavoriteDatabase.URI, FavoriteDatabase.UUID, FavoriteDatabase.TAG}, selection, selectionArgs, null, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    exist = true;
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
                db.endTransaction();
                db.close();
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                db.endTransaction();
                db.close();
                if (cursor != null) {
                    cursor.close();
                }
            }
            return exist;
        }

        public int delete(Favorite[] favorites) {
            int number = 0;
            String table = getTableName();
            SQLiteDatabase db = null;
            try {
                db = createDatabase().getWritableDatabase();
                db.beginTransaction();
                for (int i = 0; i < favorites.length; i++) {
                    number += db.delete(table, "id=?", new String[]{String.valueOf(favorites[i].getId())});
                }
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

        public ArrayList<Favorite> query() {
            return query(null, null, null, null, null, null, null);
        }

        public int update(int id, String name) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            return update(values, "id=?", new String[]{String.valueOf(id)});
        }

        protected String getTableName() {
            return FavoriteDatabase.TABLE_NAME;
        }

        protected SQLiteOpenHelper createDatabase() {
            return new FavoriteDatabase(this.context, FavoriteDatabase.TABLE_NAME, null, 1);
        }

        protected Favorite construct(Cursor cursor) {
            return new Favorite(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getLong(9));
        }
    }

    public FavoriteDatabase(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists favorite( [id] integer PRIMARY KEY AUTOINCREMENT ,[name] nvarchar(128),[uri] nvarchar(256),[tag] integer(16),[list] integer(16) ,[uuid] varchar(16),[user] nvarchar(16),[pwd] nvarchar(16),[ftp] integer(16),[len] long(16) )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static Helper helper(Context context) {
        return new Helper(context);
    }
}
