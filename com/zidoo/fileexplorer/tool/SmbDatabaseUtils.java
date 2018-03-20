package com.zidoo.fileexplorer.tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.SmbUserDB;
import java.util.ArrayList;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;

public class SmbDatabaseUtils {
    public static void save(android.content.Context r7, zidoo.samba.exs.SambaDevice r8) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(Unknown Source)
	at java.util.HashMap$KeyIterator.next(Unknown Source)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r6 = 0;
        r2 = new com.zidoo.fileexplorer.db.SmbUserDB;
        r4 = "smbuser";
        r5 = 3;
        r2.<init>(r7, r4, r6, r5);
        r0 = 0;
        r0 = r2.getWritableDatabase();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r0.beginTransaction();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3 = new android.content.ContentValues;	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.<init>();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "url";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getUrl();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "host";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getHost();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "ip";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getIp();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "user";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getUser();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "password";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getPassWord();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "type";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = r8.getType();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r4 = "smbuser";	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r5 = 0;	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r0.insert(r4, r5, r3);	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        r0.setTransactionSuccessful();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        if (r0 == 0) goto L_0x0069;
    L_0x0063:
        r0.endTransaction();
        r0.close();
    L_0x0069:
        return;
    L_0x006a:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ Exception -> 0x006a, all -> 0x0077 }
        if (r0 == 0) goto L_0x0069;
    L_0x0070:
        r0.endTransaction();
        r0.close();
        goto L_0x0069;
    L_0x0077:
        r4 = move-exception;
        if (r0 == 0) goto L_0x0080;
    L_0x007a:
        r0.endTransaction();
        r0.close();
    L_0x0080:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.tool.SmbDatabaseUtils.save(android.content.Context, zidoo.samba.exs.SambaDevice):void");
    }

    public static void update(android.content.Context r9, zidoo.samba.exs.SambaDevice r10) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(Unknown Source)
	at java.util.HashMap$KeyIterator.next(Unknown Source)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r2 = new com.zidoo.fileexplorer.db.SmbUserDB;
        r4 = "smbuser";
        r5 = 0;
        r6 = 3;
        r2.<init>(r9, r4, r5, r6);
        r0 = 0;
        r0 = r2.getWritableDatabase();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r0.beginTransaction();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.<init>();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "url";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getUrl();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "host";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getHost();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "ip";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getIp();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "user";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getUser();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "password";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getPassWord();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "type";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = r10.getType();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r4 = "smbuser";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r5 = " url = ? ";	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r6 = 1;	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r6 = new java.lang.String[r6];	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r7 = 0;	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r8 = r10.getUrl();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r6[r7] = r8;	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r0.update(r4, r3, r5, r6);	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        r0.setTransactionSuccessful();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        if (r0 == 0) goto L_0x0075;
    L_0x006f:
        r0.endTransaction();
        r0.close();
    L_0x0075:
        return;
    L_0x0076:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ Exception -> 0x0076, all -> 0x0083 }
        if (r0 == 0) goto L_0x0075;
    L_0x007c:
        r0.endTransaction();
        r0.close();
        goto L_0x0075;
    L_0x0083:
        r4 = move-exception;
        if (r0 == 0) goto L_0x008c;
    L_0x0086:
        r0.endTransaction();
        r0.close();
    L_0x008c:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.tool.SmbDatabaseUtils.update(android.content.Context, zidoo.samba.exs.SambaDevice):void");
    }

    public static void updateIps(android.content.Context r9, zidoo.samba.exs.SambaDevice r10) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(Unknown Source)
	at java.util.HashMap$KeyIterator.next(Unknown Source)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r2 = new com.zidoo.fileexplorer.db.SmbUserDB;
        r4 = "smbuser";
        r5 = 0;
        r6 = 3;
        r2.<init>(r9, r4, r5, r6);
        r0 = 0;
        r0 = r2.getWritableDatabase();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r0.beginTransaction();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r3 = new android.content.ContentValues;	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r3.<init>();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r4 = "ip";	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r5 = r10.getIp();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r3.put(r4, r5);	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r4 = "smbuser";	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r5 = " url = ? ";	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r6 = 1;	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r6 = new java.lang.String[r6];	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r7 = 0;	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r8 = r10.getUrl();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r6[r7] = r8;	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r0.update(r4, r3, r5, r6);	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        r0.setTransactionSuccessful();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        if (r0 == 0) goto L_0x003f;
    L_0x0039:
        r0.endTransaction();
        r0.close();
    L_0x003f:
        return;
    L_0x0040:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ Exception -> 0x0040, all -> 0x004d }
        if (r0 == 0) goto L_0x003f;
    L_0x0046:
        r0.endTransaction();
        r0.close();
        goto L_0x003f;
    L_0x004d:
        r4 = move-exception;
        if (r0 == 0) goto L_0x0056;
    L_0x0050:
        r0.endTransaction();
        r0.close();
    L_0x0056:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.tool.SmbDatabaseUtils.updateIps(android.content.Context, zidoo.samba.exs.SambaDevice):void");
    }

    public static ArrayList<SambaDevice> selectByAll(Context context) {
        IllegalStateException e;
        SmbUserDB smbUserDB;
        Throwable th;
        ArrayList<SambaDevice> devices = new ArrayList();
        SQLiteDatabase db = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3).getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, null, null, null, null, " id desc", null);
            if (cursor != null) {
                if (cursor.getColumnCount() < 7) {
                    db.endTransaction();
                    db.close();
                    cursor.close();
                    SmbUserDB listDB = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3);
                    try {
                        db = listDB.getReadableDatabase();
                        db.beginTransaction();
                        cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, null, null, null, null, " id desc", null);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                devices.add(new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
                            }
                        }
                    } catch (IllegalStateException e2) {
                        e = e2;
                        smbUserDB = listDB;
                        try {
                            MyLog.w("IllegalStateException", e);
                            db.endTransaction();
                            db.close();
                            if (cursor != null) {
                                cursor.close();
                            }
                            return devices;
                        } catch (Throwable th2) {
                            th = th2;
                            db.endTransaction();
                            db.close();
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        smbUserDB = listDB;
                        db.endTransaction();
                        db.close();
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
                while (cursor.moveToNext()) {
                    devices.add(new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e3) {
            e = e3;
        }
        return devices;
    }

    public static SambaDevice query(Context context, String ip) {
        SambaDevice smbDevice;
        Exception e;
        Throwable th;
        SQLiteDatabase db = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3).getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, "ip = ? ", new String[]{ip}, null, null, null, null);
            if (cursor == null || !cursor.moveToNext()) {
                smbDevice = null;
            } else {
                smbDevice = new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
            }
            try {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e2) {
                e = e2;
                try {
                    e.printStackTrace();
                    db.endTransaction();
                    db.close();
                    if (cursor != null) {
                        cursor.close();
                    }
                    return smbDevice;
                } catch (Throwable th2) {
                    th = th2;
                    db.endTransaction();
                    db.close();
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
        } catch (Exception e3) {
            e = e3;
            smbDevice = null;
            e.printStackTrace();
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
            return smbDevice;
        } catch (Throwable th3) {
            th = th3;
            smbDevice = null;
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return smbDevice;
    }

    public static ArrayList<SambaDevice> queryAll(Context context, String ip) {
        ArrayList<SambaDevice> devices = new ArrayList();
        SQLiteDatabase db = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3).getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, "ip = ? ", new String[]{ip}, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    devices.add(new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
                }
                db.setTransactionSuccessful();
            }
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return devices;
        } finally {
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        }
        return devices;
    }

    public static int delete(Context context, String url) {
        int number = 0;
        SQLiteDatabase db = null;
        try {
            db = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3).getWritableDatabase();
            db.beginTransaction();
            number = db.delete(AppConstant.DB_SMB_TABLE_NAME, " url = ? ", new String[]{url});
            db.setTransactionSuccessful();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static boolean exist(Context context, SambaDevice device) {
        boolean find = false;
        SQLiteDatabase db = new SmbUserDB(context, AppConstant.DB_SMB_TABLE_NAME, null, 3).getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = db.query(AppConstant.DB_SMB_TABLE_NAME, null, null, null, null, null, " id desc", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    find = SambaManager.isSameDevice(device, new SambaDevice(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
                    if (find) {
                        break;
                    }
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            if (cursor != null) {
                cursor.close();
            }
        } catch (IllegalStateException e) {
            MyLog.w("IllegalStateException", e);
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
        return find;
    }
}
