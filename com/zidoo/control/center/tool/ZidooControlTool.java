package com.zidoo.control.center.tool;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import zidoo.browse.BrowseConstant;

public class ZidooControlTool {
    public static boolean registerElement(Context context, String elementName, String action, int version) {
        try {
            String pkg = context.getPackageName();
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse("content://tv.zidoo.control.server.ContentProvider/element/query"), null, " name =?  AND  action =?  AND  pkg=? ", new String[]{elementName, action, pkg}, null);
            if (cursor == null || !cursor.moveToNext()) {
                cursor = contentResolver.query(Uri.parse("content://tv.zidoo.control.server.ContentProvider/element/query"), null, " name =? ", new String[]{elementName}, null);
                ContentValues values;
                if (cursor == null || !cursor.moveToNext()) {
                    Uri insertUri = Uri.parse("content://tv.zidoo.control.server.ContentProvider/element/insert");
                    values = new ContentValues();
                    values.put("name", elementName);
                    values.put(BrowseConstant.EXTRA_PACKAGE_NAME, pkg);
                    values.put("action", action);
                    values.put(BrowseConstant.EXTRA_VERSION, Integer.valueOf(version));
                    values.put("launcher", "");
                    values.put(FavoriteDatabase.TAG, Integer.valueOf(0));
                    boolean insert = contentResolver.insert(insertUri, values) != null;
                    Log.v("bob", "registerElement -->register " + elementName + "  insert = " + insert);
                    return insert;
                }
                String pName = cursor.getString(cursor.getColumnIndex(BrowseConstant.EXTRA_PACKAGE_NAME));
                cursor.close();
                if (pName.equals(pkg)) {
                    Uri updateUri = Uri.parse("content://tv.zidoo.control.server.ContentProvider/element/update");
                    values = new ContentValues();
                    values.put("name", elementName);
                    values.put(BrowseConstant.EXTRA_PACKAGE_NAME, pkg);
                    values.put("action", action);
                    values.put(BrowseConstant.EXTRA_VERSION, Integer.valueOf(version));
                    values.put("launcher", "");
                    values.put(FavoriteDatabase.TAG, Integer.valueOf(0));
                    boolean isUpdate = contentResolver.update(updateUri, values, " name =? ", new String[]{elementName}) > 0;
                    Log.v("bob", "registerElement --> register " + elementName + "  update = " + isUpdate);
                    return isUpdate;
                }
                Log.v("bob", "registerElement --> register " + elementName + " faile , already registered...");
                return false;
            }
            Log.v("bob", "registerElement --> register " + elementName + "  already register ok");
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.v("bob", "registerElement --> register " + elementName + " error");
            Log.v("bob", e.getMessage());
            return false;
        }
    }
}
