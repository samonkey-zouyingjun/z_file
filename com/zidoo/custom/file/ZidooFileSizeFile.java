package com.zidoo.custom.file;

import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;

public class ZidooFileSizeFile {
    private static long gb = 1073741824;
    private static long kb = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    private static long mb = PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;

    public static String getFileSize(String filePath) {
        String str = null;
        if (!(filePath == null || filePath.equals(""))) {
            try {
                long totalSize = getTotalSize(filePath);
                if (totalSize > 0) {
                    str = toSize(totalSize);
                }
            } catch (Exception e) {
            }
        }
        return str;
    }

    public static String getFileAvailableSize(String filePath) {
        String str = null;
        if (!(filePath == null || filePath.equals(""))) {
            try {
                long totalSize = getAvailableSize(filePath);
                if (totalSize > 0) {
                    str = toSize(totalSize);
                }
            } catch (Exception e) {
            }
        }
        return str;
    }

    public static String toSize(long mbyte) {
        if (mbyte >= gb) {
            if (((double) mbyte) / ((double) gb) >= 1024.0d) {
                return String.format("%.2f T ", new Object[]{Double.valueOf((((double) mbyte) / ((double) gb)) / 1024.0d)});
            }
            return String.format("%.2f GB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) gb))});
        } else if (mbyte >= mb) {
            return String.format("%.2f MB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) mb))});
        } else if (mbyte >= kb) {
            return String.format("%.2f KB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) kb))});
        } else {
            return String.format("%d B", new Object[]{Long.valueOf(mbyte)});
        }
    }

    public static long getTotalSize(String path) {
        try {
            StatFs statfs = new StatFs(path);
            return ((long) statfs.getBlockSize()) * ((long) statfs.getBlockCount());
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getAvailableSize(String path) {
        try {
            StatFs statfs = new StatFs(path);
            return ((long) statfs.getBlockSize()) * ((long) statfs.getAvailableBlocks());
        } catch (Exception e) {
            return 0;
        }
    }
}
