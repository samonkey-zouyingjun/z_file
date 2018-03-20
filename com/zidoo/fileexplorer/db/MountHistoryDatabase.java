package com.zidoo.fileexplorer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zidoo.fileexplorer.bean.MountHistory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pers.lic.tool.db.DatabaseHelper;
import pers.lic.tool.db.SimpleDatabaseHelper;
import zidoo.model.BoxModel;
import zidoo.tool.ZidooFileUtils;

public class MountHistoryDatabase extends SQLiteOpenHelper {
    public static final String KEY_MOUNT_TIME = "mountTime";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SHARE_PATH = "sharePath";
    public static final String KEY_URL = "url";
    public static final String KEY_USER = "user";
    public static final String TABLE_NAME = "mountHistory";
    public static final int VERSION = 1;

    private static class DeleteMountHistoryThread extends Thread {
        private Context context;
        private File[] files;
        private boolean isSmb;

        private DeleteMountHistoryThread(Context context, File[] files, boolean isSmb) {
            this.context = context;
            this.files = files;
            this.isSmb = isSmb;
        }

        public void run() {
            String root;
            String head;
            BoxModel model = BoxModel.getModel(this.context, BoxModel.getBoxModel(this.context));
            if (this.isSmb) {
                root = model.getSmbRoot();
                head = "smb://";
            } else {
                root = model.getNfsRoot();
                head = "nfs://";
            }
            ArrayList<String> urls = new ArrayList(this.files.length);
            for (File file : this.files) {
                String shareFileName;
                String temp = file.getPath().substring(root.length() + 1);
                int i = temp.indexOf(47);
                if (i == -1) {
                    shareFileName = temp;
                } else {
                    shareFileName = temp.substring(0, i);
                }
                int p = shareFileName.indexOf(35);
                urls.add(head + shareFileName.substring(0, p) + "/" + ZidooFileUtils.decodeCommand(shareFileName.substring(p + 1)));
            }
            new Helper(this.context).deleteByUrls(urls);
        }
    }

    private static class SaveMountHistoryThread extends Thread {
        private Context context;
        private MountHistory history;

        private SaveMountHistoryThread(Context context, MountHistory history) {
            this.context = context;
            this.history = history;
        }

        public void run() {
            Helper helper = MountHistoryDatabase.helper(this.context);
            MountHistory exist = (MountHistory) helper.query("url", this.history.getUrl());
            if (exist != null) {
                this.history.setId(exist.getId());
                helper.update(this.history);
                return;
            }
            helper.insert(this.history);
        }
    }

    public static final class Helper extends SimpleDatabaseHelper<MountHistory> {
        Helper(Context context) {
            super(context);
        }

        public void deleteByUrls(List<String> urls) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = beginWriteTransaction();
                for (String url : urls) {
                    sQLiteDatabase.delete(MountHistoryDatabase.TABLE_NAME, "url=?", new String[]{url});
                }
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endTransaction(sQLiteDatabase);
            }
        }

        public void deleteByHistories(List<MountHistory> histories) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = beginWriteTransaction();
                for (MountHistory history : histories) {
                    sQLiteDatabase.delete(MountHistoryDatabase.TABLE_NAME, "_id=?", new String[]{String.valueOf(history.getId())});
                }
                sQLiteDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endTransaction(sQLiteDatabase);
            }
        }

        public ContentValues genContentValues(MountHistory mountHistory) {
            ContentValues values = new ContentValues();
            values.put("url", mountHistory.getUrl());
            values.put(MountHistoryDatabase.KEY_SHARE_PATH, mountHistory.getSharePath());
            values.put("user", mountHistory.getUser());
            values.put("password", mountHistory.getPassword());
            values.put(MountHistoryDatabase.KEY_MOUNT_TIME, Long.valueOf(mountHistory.getMountTime()));
            return values;
        }

        public long getId(MountHistory mountHistory) {
            return (long) mountHistory.getId();
        }

        protected String getTableName() {
            return MountHistoryDatabase.TABLE_NAME;
        }

        protected SQLiteOpenHelper createDatabase() {
            return new MountHistoryDatabase(this.context);
        }

        protected MountHistory construct(Cursor cursor) {
            return new MountHistory(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5));
        }
    }

    public MountHistoryDatabase(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseHelper.createSql(TABLE_NAME).appendUnicodeString("url").appendUnicodeString(KEY_SHARE_PATH).appendUnicodeString("user").appendUnicodeString("password").appendLong(KEY_MOUNT_TIME).sql());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static Helper helper(Context context) {
        return new Helper(context);
    }

    public static void saveMountHistory(Context context, String url, String mountShare, String user, String password) {
        MountHistory history = new MountHistory();
        history.setUrl(url);
        history.setSharePath(mountShare);
        history.setUser(user);
        history.setPassword(password);
        history.setMountTime(System.currentTimeMillis());
        new SaveMountHistoryThread(context, history).start();
    }

    public static void deleteMountHistory(Context context, File file, boolean isSmb) {
        new DeleteMountHistoryThread(context, new File[]{file}, isSmb).start();
    }

    public static void deleteMountHistory(Context context, File[] files, boolean isSmb) {
        new DeleteMountHistoryThread(context, files, isSmb).start();
    }
}
