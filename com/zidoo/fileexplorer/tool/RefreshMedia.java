package com.zidoo.fileexplorer.tool;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import com.zidoo.custom.db.DBConstants;
import java.io.File;

public class RefreshMedia {
    static final String EXTERNAL_VOLUME = "external";
    private static final String TAG = "RefreshMedia";
    private Context mContext;

    public RefreshMedia(Context c) {
        this.mContext = c;
    }

    public void notifyMediaAdd(String filePath) {
        notifyMediaAdd(new File(filePath));
    }

    public void notifyMediaAdd(File file) {
        if (file.exists()) {
            try {
                Uri mUri = Uri.fromFile(file);
                Intent mIntent = new Intent();
                mIntent.setData(mUri);
                mIntent.setAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                this.mContext.sendBroadcast(mIntent);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public static void notifyMediaAdd(Context context, String filePath) {
        notifyMediaAdd(context, new File(filePath));
    }

    public static void notifyMediaAdd(Context context, File file) {
        try {
            Uri mUri = Uri.fromFile(file);
            Intent mIntent = new Intent();
            mIntent.setData(mUri);
            mIntent.setAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            context.sendBroadcast(mIntent);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void notifyMediaDelete(String file) {
        String[] PROJECTION = new String[]{DBConstants.KEY_AOTO, "_data"};
        Uri[] mediatypes = new Uri[]{Media.getContentUri(EXTERNAL_VOLUME), Video.Media.getContentUri(EXTERNAL_VOLUME), Images.Media.getContentUri(EXTERNAL_VOLUME)};
        ContentResolver cr = this.mContext.getContentResolver();
        for (int i = 0; i < mediatypes.length; i++) {
            Cursor c = cr.query(mediatypes[i], PROJECTION, null, null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    try {
                        long rowId = c.getLong(0);
                        if (c.getString(1).startsWith(file)) {
                            Log.d(TAG, "delete row " + rowId + "in table " + mediatypes[i]);
                            cr.delete(ContentUris.withAppendedId(mediatypes[i], rowId), null, null);
                        }
                    } finally {
                        c.close();
                    }
                }
            }
        }
    }
}
