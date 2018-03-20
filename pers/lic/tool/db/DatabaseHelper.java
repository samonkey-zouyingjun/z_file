package pers.lic.tool.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.zidoo.custom.db.DBConstants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class DatabaseHelper<T> {
    protected Context context;

    public static final class CreateSql {
        private String primaryKey = DBConstants.KEY_AOTO;
        private ArrayList<String> rows = new ArrayList();
        private String table;

        CreateSql(String table) {
            this.table = table;
        }

        public CreateSql setPrimaryKey(String key) {
            this.primaryKey = key;
            return this;
        }

        public CreateSql appendShort(String key) {
            this.rows.add("[" + key + "] short");
            return this;
        }

        public CreateSql appendInt(String key) {
            this.rows.add("[" + key + "] integer");
            return this;
        }

        public CreateSql appendLong(String key) {
            this.rows.add("[" + key + "] long");
            return this;
        }

        public CreateSql appendFloat(String key) {
            this.rows.add("[" + key + "] float");
            return this;
        }

        public CreateSql appendDouble(String key) {
            this.rows.add("[" + key + "] double");
            return this;
        }

        public CreateSql appendChar(String key, int size) {
            this.rows.add("[" + key + "] char(" + size + ")");
            return this;
        }

        public CreateSql appendString(String key) {
            this.rows.add("[" + key + "] varchar");
            return this;
        }

        public CreateSql appendText(String key) {
            this.rows.add("[" + key + "] text");
            return this;
        }

        public CreateSql appendUnicodeChar(String key, int size) {
            this.rows.add("[" + key + "] nchar(" + size + ")");
            return this;
        }

        public CreateSql appendUnicodeString(String key) {
            this.rows.add("[" + key + "] nvarchar");
            return this;
        }

        public CreateSql appendUnicodeText(String key) {
            this.rows.add("[" + key + "] ntext");
            return this;
        }

        public String sql() {
            String sql = "create table if not exists " + this.table;
            if (this.rows.size() == 0) {
                return sql + "([" + this.primaryKey + "] integer PRIMARY KEY AUTOINCREMENT )";
            }
            String s = "";
            Iterator it = this.rows.iterator();
            while (it.hasNext()) {
                s = s + "," + ((String) it.next());
            }
            return sql + "([" + this.primaryKey + "] integer PRIMARY KEY AUTOINCREMENT" + s + ")";
        }
    }

    protected abstract T construct(Cursor cursor);

    protected abstract SQLiteOpenHelper createDatabase();

    protected abstract String getTableName();

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    protected long insert(ContentValues values) {
        long id = -1;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = beginWriteTransaction();
            id = sQLiteDatabase.insert(getTableName(), null, values);
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(sQLiteDatabase);
        }
        return id;
    }

    protected int delete(String whereClause, String[] whereArgs) {
        int number = 0;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = beginWriteTransaction();
            number = sQLiteDatabase.delete(getTableName(), whereClause, whereArgs);
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(sQLiteDatabase);
        }
        return number;
    }

    protected int update(ContentValues values, String whereClause, String[] whereArgs) {
        int number = 0;
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = beginWriteTransaction();
            number = sQLiteDatabase.update(getTableName(), values, whereClause, whereArgs);
            sQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(sQLiteDatabase);
        }
        return number;
    }

    protected ArrayList<T> query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        ArrayList<T> list = new ArrayList();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = beginReadTransaction();
            cursor = db.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(construct(cursor));
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

    protected T queryOne(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        T t = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = beginReadTransaction();
            cursor = db.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            if (cursor != null && cursor.moveToNext()) {
                t = construct(cursor);
            }
            db.setTransactionSuccessful();
            endTransaction(db);
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            endTransaction(null);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            endTransaction(null);
            if (cursor != null) {
                cursor.close();
            }
        }
        return t;
    }

    public static void upgradeTables(SQLiteOpenHelper helper, SQLiteDatabase db, String tableName, String columns) {
        try {
            String tempTableName = tableName + "_temp";
            db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tempTableName);
            helper.onCreate(db);
            db.execSQL("INSERT INTO " + tableName + " (" + columns + ")  SELECT " + columns + " FROM " + tempTableName);
            db.execSQL("DROP TABLE IF EXISTS " + tempTableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected SQLiteDatabase beginReadTransaction() {
        SQLiteDatabase db = createDatabase().getReadableDatabase();
        db.beginTransaction();
        return db;
    }

    protected SQLiteDatabase beginWriteTransaction() {
        SQLiteDatabase db = createDatabase().getWritableDatabase();
        db.beginTransaction();
        return db;
    }

    protected void endTransaction(SQLiteDatabase db) {
        if (db != null) {
            db.endTransaction();
            db.close();
        }
    }

    public static <E extends SQLiteOpenHelper> E openOrCreateDatabase(E input, String path, CursorFactory cursorFactory) {
        try {
            Field filed = SQLiteOpenHelper.class.getDeclaredField("mDatabase");
            filed.setAccessible(true);
            filed.set(input, SQLiteDatabase.openOrCreateDatabase(path, cursorFactory));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    public static CreateSql createSql(String table) {
        return new CreateSql(table);
    }
}
