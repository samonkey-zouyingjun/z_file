package com.zidoo.fileexplorer.main;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences.Editor;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.ApkVisibleSettingCursor;

public class FileContentProvider extends ContentProvider {
    private static final int APK_VISIBLE = 0;
    private static final UriMatcher MATCHER = new UriMatcher(-1);

    static {
        MATCHER.addURI("zidoo.fileexplorer", "apk/*", 0);
    }

    public boolean onCreate() {
        return false;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (MATCHER.match(uri) == 0) {
            return new ApkVisibleSettingCursor(getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getBoolean(AppConstant.PREFEREANCES_APK_VISIBLE, BoxModelConfig.DEFAULT_APK_VISIBLE_SET));
        }
        return null;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (MATCHER.match(uri) != 0) {
            return 0;
        }
        Editor editor = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit();
        editor.putBoolean(AppConstant.PREFEREANCES_APK_VISIBLE, values.getAsBoolean(AppConstant.PREFEREANCES_APK_VISIBLE).booleanValue());
        editor.commit();
        return 1;
    }
}
