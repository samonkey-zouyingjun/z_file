package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.FastIdentifier;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.bean.StableFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import zidoo.device.DeviceType;
import zidoo.file.FileType;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsFolder;
import zidoo.nfs.NfsManager;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public class FastIdentifyTask extends BaseTask<Result> {
    Context context;
    ArrayList<DeviceInfo> devices;
    FileFilter fileFilter;
    FastIdentifier identifier;
    boolean isFavor;
    ListInfo listInfo;
    String nfsRoot;
    HashMap<String, MountFile[]> nfsShareDirs;
    OnShowBdmvOpenWith onShowBdmvOpenWith;
    int openWith = 0;
    SambaManager sambaManager;
    String smbRoot;
    HashMap<String, MountFile[]> smbShareDirs;

    private class Dex {
        DeviceInfo device;
        int index;

        Dex(DeviceInfo device, int index) {
            this.device = device;
            this.index = index;
        }
    }

    public interface OnShowBdmvOpenWith {
        boolean onShowBdmvOpenWith(FastIdentifyTask fastIdentifyTask);
    }

    public static final class Result {
        File[] children;
        int code;
        int deviceIndex;
        DeviceInfo deviceInfo;
        File dir;
        int enterPosition;
        boolean isFavor;
        int listIndex;
        String nfsIp;
        int openWith = 0;
        File parent;
        SambaDevice sambaDevice;
        ListType type;

        public Result(int code) {
            this.code = code;
        }

        public Result(File file, int openWith) {
            this.parent = file;
            this.openWith = openWith;
            this.code = 1;
        }

        public Result(int listIndex, File file, File[] files, ListType type, int index, DeviceInfo device) {
            this.listIndex = listIndex;
            this.parent = file;
            this.children = files;
            this.type = type;
            this.deviceIndex = index;
            this.deviceInfo = device;
            this.code = 1;
            this.openWith = 2;
        }

        public Result(int listIndex, File file, File dir, File[] files, ListType type, int index, DeviceInfo device, int position, SambaDevice smbd) {
            this.listIndex = listIndex;
            this.parent = file;
            this.dir = dir;
            this.children = files;
            this.type = type;
            this.deviceIndex = index;
            this.deviceInfo = device;
            this.enterPosition = position;
            this.sambaDevice = smbd;
            this.code = 1;
            this.openWith = 2;
        }

        public Result(int listIndex, File file, File dir, File[] files, ListType type, int index, DeviceInfo device, int position, String ip) {
            this.listIndex = listIndex;
            this.parent = file;
            this.dir = dir;
            this.children = files;
            this.type = type;
            this.deviceIndex = index;
            this.deviceInfo = device;
            this.enterPosition = position;
            this.nfsIp = ip;
            this.code = 1;
            this.openWith = 2;
        }

        public boolean isFail() {
            return this.code == 0;
        }

        public boolean isCancelled() {
            return this.code == 2;
        }

        public ListType getListType() {
            return this.type;
        }

        public File[] getChildren() {
            return this.children;
        }

        public File getParent() {
            return this.parent;
        }

        public File getDir() {
            return this.dir;
        }

        public int getListIndex() {
            return this.listIndex;
        }

        public int getDeviceIndex() {
            return this.deviceIndex;
        }

        public DeviceInfo getDevice() {
            return this.deviceInfo;
        }

        public boolean isFile() {
            return this.openWith == 0;
        }

        public boolean isBdmv() {
            return this.openWith == 1;
        }

        public boolean isBDNG() {
            return this.openWith == 3;
        }

        public SambaDevice getSmbDevice() {
            return this.sambaDevice;
        }

        public int getPosition() {
            return this.enterPosition;
        }

        public String getNfsIp() {
            return this.nfsIp;
        }

        public void setFavor(boolean isFavor) {
            this.isFavor = isFavor;
        }

        public boolean isFavor() {
            return this.isFavor;
        }
    }

    public FastIdentifyTask(Handler handler, int what, Context context, ArrayList<DeviceInfo> devices, FastIdentifier identifier, OnShowBdmvOpenWith onShowBdmvOpenWith, ListInfo listInfo, FileFilter fileFilter, SambaManager sambaManager, String smbRoot, String nfsRoot, int openWith, boolean isFavor) {
        super(handler, what);
        this.context = context;
        this.devices = devices;
        this.identifier = identifier;
        this.onShowBdmvOpenWith = onShowBdmvOpenWith;
        this.listInfo = listInfo;
        this.openWith = openWith;
        this.isFavor = isFavor;
        this.fileFilter = fileFilter;
        this.sambaManager = sambaManager;
        this.smbRoot = smbRoot;
        this.nfsRoot = nfsRoot;
        this.smbShareDirs = listInfo.getSmbShareDirs();
        this.nfsShareDirs = listInfo.getNfsShareDirs();
    }

    public void setOpenWith(int way) {
        this.openWith = way;
    }

    protected Result doInBackground() {
        MyLog.d(this.identifier.toString());
        Result result = null;
        switch (this.identifier.getTag()) {
            case 0:
                result = loadFlash(this.identifier);
                break;
            case 1:
                result = loadUsb(this.identifier);
                break;
            case 2:
                result = loadSmbDevice(this.identifier);
                break;
            case 3:
                result = loadSmbFile(this.identifier, false);
                break;
            case 4:
                result = loadNfsDevice(this.identifier);
                break;
            case 5:
                result = loadNfsFile(this.identifier);
                break;
            case 6:
            case 7:
                result = loadSmbFile(this.identifier, true);
                break;
        }
        if (result == null) {
            result = new Result(0);
        }
        result.setFavor(this.isFavor);
        return result;
    }

    private Result loadFlash(FastIdentifier identifier) {
        Result result = null;
        File file = new File(Environment.getExternalStorageDirectory(), identifier.getUri());
        if (file.exists()) {
            if (file.isFile()) {
                result = new Result(file, 0);
            } else if (FileType.isBDMV(file.getPath())) {
                if (checkBdmv()) {
                    result = new Result(file, 2);
                }
                if (this.openWith == 0) {
                    result = new Result(2);
                } else if (this.openWith == 1) {
                    result = new Result(file, 1);
                } else if (this.openWith == 3) {
                    return new Result(file, 3);
                } else {
                    files = file.listFiles();
                    Utils.sortFiles(files, AppConstant.sPrefereancesSortWay);
                    dex = getDeviceAndIndex(this.devices, DeviceType.FLASH);
                    if (dex != null) {
                        result = new Result(identifier.getListIndex(), file, files, ListType.FILE, dex.index, dex.device);
                    }
                }
            } else {
                files = file.listFiles();
                Utils.sortFiles(files, AppConstant.sPrefereancesSortWay);
                dex = getDeviceAndIndex(this.devices, DeviceType.FLASH);
                if (dex != null) {
                    result = new Result(identifier.getListIndex(), file, files, ListType.FILE, dex.index, dex.device);
                }
            }
        }
        return result;
    }

    private Result loadUsb(FastIdentifier identifier) {
        Dex dex = getDeviceAndIndex(this.devices, identifier.getUuid(), identifier.getUri());
        if (dex == null) {
            return null;
        }
        File file = new File(dex.device, identifier.getUri());
        if (file.isFile()) {
            return new Result(file, 0);
        }
        if (FileType.isBDMV(file.getPath())) {
            if (checkBdmv()) {
                Result result = new Result(file, 2);
            }
            if (this.openWith == 0) {
                return new Result(2);
            }
            if (this.openWith == 1) {
                return new Result(file, 1);
            }
            if (this.openWith == 3) {
                return new Result(file, 3);
            }
            File[] files = file.listFiles();
            Utils.sortFiles(files, AppConstant.sPrefereancesSortWay);
            return new Result(identifier.getListIndex(), file, files, ListType.FILE, dex.index, dex.device);
        }
        files = file.listFiles();
        Utils.sortFiles(files, AppConstant.sPrefereancesSortWay);
        return new Result(identifier.getListIndex(), file, files, ListType.FILE, dex.index, dex.device);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.zidoo.fileexplorer.task.FastIdentifyTask.Result loadSmbDevice(com.zidoo.fileexplorer.bean.FastIdentifier r43) {
        /*
        r42 = this;
        r34 = 0;
        r0 = r42;
        r9 = r0.devices;
        r10 = zidoo.device.DeviceType.SMB;
        r0 = r42;
        r22 = r0.getDeviceAndIndex(r9, r10);
        r5 = r43.getUri();
        r7 = r43.getUuid();
        r9 = 6;
        r6 = r5.substring(r9);
        r9 = 47;
        r39 = r6.indexOf(r9);
        r9 = -1;
        r0 = r39;
        if (r0 == r9) goto L_0x002d;
    L_0x0026:
        r9 = 0;
        r0 = r39;
        r6 = r6.substring(r9, r0);
    L_0x002d:
        r4 = new zidoo.samba.exs.SambaDevice;
        r8 = r43.getUser();
        r9 = r43.getPassword();
        r10 = 4;
        r4.<init>(r5, r6, r7, r8, r9, r10);
        r0 = r42;
        r9 = r0.listInfo;
        r0 = r42;
        r10 = r0.context;
        r33 = r9.getDeviceAndPosition(r10, r4);
        r9 = 0;
        r4 = r33[r9];
        r4 = (zidoo.samba.exs.SambaDevice) r4;
        r9 = 1;
        r9 = r33[r9];
        r9 = (java.lang.Integer) r9;
        r16 = r9.intValue();
        r9 = 2;
        r9 = r33[r9];
        r9 = (java.lang.Boolean) r9;
        r35 = r9.booleanValue();
        r0 = r42;
        r9 = r0.smbShareDirs;
        r28 = r9.get(r7);
        r28 = (com.zidoo.fileexplorer.bean.MountFile[]) r28;
        if (r28 == 0) goto L_0x00e8;
    L_0x006a:
        r25 = new java.util.ArrayList;
        r25.<init>();
        r0 = r28;
        r10 = r0.length;
        r9 = 0;
    L_0x0073:
        if (r9 >= r10) goto L_0x008d;
    L_0x0075:
        r40 = r28[r9];
        r0 = r42;
        r11 = r0.fileFilter;
        r0 = r40;
        r11 = r11.accept(r0);
        if (r11 == 0) goto L_0x008a;
    L_0x0083:
        r0 = r25;
        r1 = r40;
        r0.add(r1);
    L_0x008a:
        r9 = r9 + 1;
        goto L_0x0073;
    L_0x008d:
        r9 = r25.size();
        r0 = new java.io.File[r9];
        r20 = r0;
        r0 = r25;
        r1 = r20;
        r0.toArray(r1);
        r9 = com.zidoo.fileexplorer.config.AppConstant.sPrefereancesSortWay;
        r0 = r20;
        com.zidoo.fileexplorer.tool.Utils.sortFiles(r0, r9);
        if (r4 != 0) goto L_0x00c8;
    L_0x00a5:
        r4 = new zidoo.samba.exs.SambaDevice;
        r12 = r43.getUser();
        r13 = r43.getPassword();
        r14 = 4;
        r8 = r4;
        r9 = r5;
        r10 = r7;
        r11 = r7;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        r0 = r42;
        r9 = r0.context;
        r9 = com.zidoo.fileexplorer.tool.SmbDatabaseUtils.exist(r9, r4);
        if (r9 != 0) goto L_0x00c8;
    L_0x00c1:
        r0 = r42;
        r9 = r0.context;
        com.zidoo.fileexplorer.tool.SmbDatabaseUtils.save(r9, r4);
    L_0x00c8:
        r8 = new com.zidoo.fileexplorer.task.FastIdentifyTask$Result;
        r9 = r43.getListIndex();
        r0 = r22;
        r10 = r0.device;
        r11 = 0;
        r13 = com.zidoo.fileexplorer.bean.ListType.SMB_FILE;
        r0 = r22;
        r14 = r0.index;
        r0 = r22;
        r15 = r0.device;
        r12 = r20;
        r17 = r4;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15, r16, r17);
        r9 = r8;
        r8 = r34;
    L_0x00e7:
        return r9;
    L_0x00e8:
        r19 = 0;
        r26 = r7;
        if (r4 == 0) goto L_0x00f8;
    L_0x00ee:
        r9 = r4.getHost();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r7.equals(r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 == 0) goto L_0x0123;
    L_0x00f8:
        r32 = jcifs.netbios.NbtAddress.getByName(r7);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r18 = jcifs.netbios.NbtAddress.getAllByAddress(r32);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r27 = 0;
    L_0x0102:
        r0 = r18;
        r9 = r0.length;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r27;
        if (r0 >= r9) goto L_0x0123;
    L_0x0109:
        r30 = r18[r27];	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r30.isGroupAddress();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 != 0) goto L_0x01f8;
    L_0x0111:
        r9 = r30.getNameType();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 != 0) goto L_0x01f8;
    L_0x0117:
        r9 = r30.getHostName();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 == 0) goto L_0x0123;
    L_0x011d:
        r26 = r30.getHostName();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r19 = 1;
    L_0x0123:
        r29 = new jcifs.smb.NtlmPasswordAuthentication;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r43.getUser();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = r43.getPassword();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r29;
        r0.<init>(r7, r9, r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r37 = new jcifs.smb.SmbFile;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9.<init>();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = "smb://";
        r9 = r9.append(r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r9.append(r7);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = "/";
        r9 = r9.append(r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r9.toString();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r37;
        r1 = r29;
        r0.<init>(r9, r1);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = com.zidoo.fileexplorer.tool.Utils.getDefaultSmbFileFilter();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r37;
        r38 = r0.listFiles(r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r38 != 0) goto L_0x01fc;
    L_0x0162:
        r21 = 0;
    L_0x0164:
        if (r21 <= 0) goto L_0x0260;
    L_0x0166:
        r0 = r21;
        r0 = new com.zidoo.fileexplorer.bean.MountFile[r0];	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r28 = r0;
        r25 = new java.util.ArrayList;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r25.<init>();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r27 = 0;
    L_0x0173:
        r0 = r27;
        r1 = r21;
        if (r0 >= r1) goto L_0x0203;
    L_0x0179:
        r9 = r38[r27];	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r41 = r9.getPath();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r36 = r41;
        r9 = "/";
        r0 = r36;
        r9 = r0.endsWith(r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 == 0) goto L_0x0199;
    L_0x018c:
        r9 = 0;
        r10 = r36.length();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = r10 + -1;
        r0 = r36;
        r36 = r0.substring(r9, r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
    L_0x0199:
        r9 = 47;
        r0 = r36;
        r23 = r0.lastIndexOf(r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = -1;
        r0 = r23;
        if (r0 == r9) goto L_0x01ae;
    L_0x01a6:
        r9 = r23 + 1;
        r0 = r36;
        r36 = r0.substring(r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
    L_0x01ae:
        r9 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9.<init>();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = r4.getIp();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r9.append(r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = "#";
        r9 = r9.append(r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = zidoo.tool.ZidooFileUtils.encodeCommand(r36);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r9.append(r10);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r31 = r9.toString();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r24 = new com.zidoo.fileexplorer.bean.MountFile;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r42;
        r9 = r0.smbRoot;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r24;
        r1 = r31;
        r2 = r41;
        r3 = r36;
        r0.<init>(r9, r1, r2, r3);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r28[r27] = r24;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r42;
        r9 = r0.fileFilter;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r24;
        r9 = r9.accept(r0);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r9 == 0) goto L_0x01f4;
    L_0x01ed:
        r0 = r25;
        r1 = r24;
        r0.add(r1);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
    L_0x01f4:
        r27 = r27 + 1;
        goto L_0x0173;
    L_0x01f8:
        r27 = r27 + 1;
        goto L_0x0102;
    L_0x01fc:
        r0 = r38;
        r0 = r0.length;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r21 = r0;
        goto L_0x0164;
    L_0x0203:
        r0 = r42;
        r9 = r0.smbShareDirs;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r10 = r4.getIp();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r28;
        r9.put(r10, r0);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r25.size();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r12 = new java.io.File[r9];	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r25;
        r0.toArray(r12);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        if (r35 == 0) goto L_0x024d;
    L_0x021d:
        if (r19 == 0) goto L_0x022b;
    L_0x021f:
        r0 = r26;
        r4.setHost(r0);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r42;
        r9 = r0.context;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        com.zidoo.fileexplorer.tool.SmbDatabaseUtils.update(r9, r4);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
    L_0x022b:
        r9 = com.zidoo.fileexplorer.config.AppConstant.sPrefereancesSortWay;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        com.zidoo.fileexplorer.tool.Utils.sortFiles(r12, r9);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r8 = new com.zidoo.fileexplorer.task.FastIdentifyTask$Result;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r9 = r43.getListIndex();	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r22;
        r10 = r0.device;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r11 = 0;
        r13 = com.zidoo.fileexplorer.bean.ListType.SMB_FILE;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r22;
        r14 = r0.index;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r0 = r22;
        r15 = r0.device;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        r17 = r4;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15, r16, r17);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
    L_0x024a:
        r9 = r8;
        goto L_0x00e7;
    L_0x024d:
        r0 = r42;
        r9 = r0.context;	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        com.zidoo.fileexplorer.tool.SmbDatabaseUtils.save(r9, r4);	 Catch:{ SmbException -> 0x0255, MalformedURLException -> 0x025c }
        goto L_0x022b;
    L_0x0255:
        r23 = move-exception;
        r23.printStackTrace();	 Catch:{ Exception -> 0x0263 }
        r8 = r34;
        goto L_0x024a;
    L_0x025c:
        r23 = move-exception;
        r23.printStackTrace();	 Catch:{ Exception -> 0x0263 }
    L_0x0260:
        r8 = r34;
        goto L_0x024a;
    L_0x0263:
        r23 = move-exception;
        r23.printStackTrace();
        goto L_0x0260;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.task.FastIdentifyTask.loadSmbDevice(com.zidoo.fileexplorer.bean.FastIdentifier):com.zidoo.fileexplorer.task.FastIdentifyTask$Result");
    }

    private Result loadSmbFile(FastIdentifier identifier, boolean isShareDevice) {
        Dex dex = getDeviceAndIndex(this.devices, DeviceType.SMB);
        String url = identifier.getUri();
        String temp = url.substring(6);
        int s = temp.indexOf("/");
        String server = temp.substring(0, s);
        String uri = temp.substring(s + 1);
        int sa = uri.indexOf("/");
        String share = sa == -1 ? uri : uri.substring(0, sa);
        String ip = identifier.getUuid();
        try {
            String mountPoint = dex.device.getPath() + "/" + ip + "#" + ZidooFileUtils.encodeCommand(share);
            boolean mount = this.sambaManager.isMounted(ip, share, mountPoint);
            MyLog.d("is mounted ? " + mount);
            File real;
            File[] children;
            Result result;
            SambaDevice smbd;
            ArrayList<SambaDevice> savedDevices;
            ArrayList<SambaDevice> devices;
            Iterator it;
            SambaDevice sambaDevice;
            boolean exist;
            MountFile[] mountFiles;
            SmbFile[] smbFiles;
            int count;
            Object mountFiles2;
            int i;
            String fileUrl;
            String fileShare;
            int e;
            int type;
            Object[] oo;
            if (mount) {
                if (sa == -1) {
                    real = new MountFile(mountPoint, url, share);
                } else {
                    real = new StableFile(mountPoint + uri.substring(sa));
                }
                if (real.exists()) {
                    if (real.isFile()) {
                        return new Result(real, 0);
                    }
                    if (!FileType.isBDMV(real.getPath())) {
                        children = real.listFiles(this.fileFilter);
                        if (children != null) {
                            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                            result = new Result(identifier.getListIndex(), real, children, identifier.getTag() == 3 ? ListType.SMB_FILE : ListType.SMB_SHARE, dex.index, dex.device);
                            if (result == null) {
                                return new Result(0);
                            }
                            smbd = null;
                            savedDevices = this.listInfo.getSavedSmbDevices();
                            if (savedDevices.size() == 0) {
                                savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                                this.listInfo.setSavedSmb(savedDevices);
                            }
                            devices = new ArrayList();
                            devices.addAll(savedDevices);
                            devices.addAll(this.listInfo.getSmbDevices());
                            it = devices.iterator();
                            while (it.hasNext()) {
                                sambaDevice = (SambaDevice) it.next();
                                if (sambaDevice.getType() != 4) {
                                }
                            }
                            exist = false;
                            mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
                            if (smbd != null) {
                                if (mountFiles != null) {
                                    for (File file : mountFiles) {
                                        if (file.getName().equals(share)) {
                                            exist = true;
                                            break;
                                        }
                                    }
                                } else {
                                    smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                                    if (smbFiles != null) {
                                        count = smbFiles.length;
                                    } else {
                                        count = 0;
                                    }
                                    if (count > 0) {
                                        mountFiles2 = new MountFile[count];
                                        for (i = 0; i < count; i++) {
                                            fileUrl = smbFiles[i].getPath();
                                            fileShare = fileUrl;
                                            if (fileUrl.endsWith("/")) {
                                                fileShare = fileShare.substring(0, fileShare.length() - 1);
                                            }
                                            e = fileShare.lastIndexOf(47);
                                            if (e != -1) {
                                                fileShare = fileShare.substring(e + 1);
                                            }
                                            mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                                            if (fileShare.equals(share)) {
                                                exist = true;
                                            }
                                        }
                                        this.smbShareDirs.put(ip, mountFiles2);
                                    }
                                }
                            }
                            if (exist) {
                            }
                            if (isShareDevice) {
                                if (sa != -1) {
                                }
                                oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
                            } else {
                                oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
                            }
                            result.enterPosition = ((Integer) oo[1]).intValue();
                            result.sambaDevice = (SambaDevice) oo[0];
                            if (!isShareDevice) {
                                result.listIndex = uri.split("/").length - 1;
                            }
                            result.type = ListType.SMB_SHARE;
                            if (!((Boolean) oo[2]).booleanValue()) {
                                SmbDatabaseUtils.save(this.context, result.sambaDevice);
                            }
                            return result;
                        }
                    } else if (checkBdmv()) {
                        return new Result(real, 2);
                    } else {
                        if (this.openWith == 0) {
                            return new Result(2);
                        }
                        if (this.openWith == 1) {
                            return new Result(real, 1);
                        }
                        if (this.openWith == 3) {
                            result = new Result(real, 3);
                        } else {
                            children = real.listFiles(this.fileFilter);
                            if (children != null) {
                                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                                result = new Result(identifier.getListIndex(), real, children, identifier.getTag() == 3 ? ListType.SMB_FILE : ListType.SMB_SHARE, dex.index, dex.device);
                            }
                        }
                        if (result == null) {
                            return new Result(0);
                        }
                        smbd = null;
                        savedDevices = this.listInfo.getSavedSmbDevices();
                        if (savedDevices.size() == 0) {
                            savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                            this.listInfo.setSavedSmb(savedDevices);
                        }
                        devices = new ArrayList();
                        devices.addAll(savedDevices);
                        devices.addAll(this.listInfo.getSmbDevices());
                        it = devices.iterator();
                        while (it.hasNext()) {
                            sambaDevice = (SambaDevice) it.next();
                            if (sambaDevice.getType() != 4 && sambaDevice.getIp().equals(ip)) {
                                smbd = sambaDevice;
                                break;
                            }
                        }
                        exist = false;
                        mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
                        if (smbd != null) {
                            if (mountFiles != null) {
                                while (r5 < r8) {
                                    if (file.getName().equals(share)) {
                                        exist = true;
                                        break;
                                    }
                                }
                            } else {
                                try {
                                    smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                                    if (smbFiles != null) {
                                        count = 0;
                                    } else {
                                        count = smbFiles.length;
                                    }
                                    if (count > 0) {
                                        mountFiles2 = new MountFile[count];
                                        for (i = 0; i < count; i++) {
                                            fileUrl = smbFiles[i].getPath();
                                            fileShare = fileUrl;
                                            if (fileUrl.endsWith("/")) {
                                                fileShare = fileShare.substring(0, fileShare.length() - 1);
                                            }
                                            e = fileShare.lastIndexOf(47);
                                            if (e != -1) {
                                                fileShare = fileShare.substring(e + 1);
                                            }
                                            mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                                            if (fileShare.equals(share)) {
                                                exist = true;
                                            }
                                        }
                                        this.smbShareDirs.put(ip, mountFiles2);
                                    }
                                } catch (MalformedURLException e2) {
                                    e2.printStackTrace();
                                } catch (SmbException e3) {
                                    e3.printStackTrace();
                                }
                            }
                        }
                        if (exist || isShareDevice) {
                            if (isShareDevice) {
                                type = (sa != -1 || sa == uri.length() - 1) ? 8 : 1;
                                oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
                            } else {
                                oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
                            }
                            result.enterPosition = ((Integer) oo[1]).intValue();
                            result.sambaDevice = (SambaDevice) oo[0];
                            if (isShareDevice) {
                                result.listIndex = uri.split("/").length - 1;
                            }
                            result.type = ListType.SMB_SHARE;
                            if (((Boolean) oo[2]).booleanValue()) {
                                SmbDatabaseUtils.save(this.context, result.sambaDevice);
                            }
                        } else {
                            oo = this.listInfo.getDeviceAndPosition(this.context, smbd);
                            result.enterPosition = ((Integer) oo[1]).intValue();
                            result.sambaDevice = (SambaDevice) oo[0];
                            result.dir = null;
                            if (!((Boolean) oo[2]).booleanValue()) {
                                SmbDatabaseUtils.save(this.context, result.sambaDevice);
                            }
                        }
                        return result;
                    }
                }
                result = null;
                if (result == null) {
                    return new Result(0);
                }
                smbd = null;
                savedDevices = this.listInfo.getSavedSmbDevices();
                if (savedDevices.size() == 0) {
                    savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                    this.listInfo.setSavedSmb(savedDevices);
                }
                devices = new ArrayList();
                devices.addAll(savedDevices);
                devices.addAll(this.listInfo.getSmbDevices());
                it = devices.iterator();
                while (it.hasNext()) {
                    sambaDevice = (SambaDevice) it.next();
                    if (sambaDevice.getType() != 4) {
                    }
                }
                exist = false;
                mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
                if (smbd != null) {
                    if (mountFiles != null) {
                        while (r5 < r8) {
                            if (file.getName().equals(share)) {
                                exist = true;
                                break;
                            }
                        }
                    } else {
                        smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                        if (smbFiles != null) {
                            count = 0;
                        } else {
                            count = smbFiles.length;
                        }
                        if (count > 0) {
                            mountFiles2 = new MountFile[count];
                            for (i = 0; i < count; i++) {
                                fileUrl = smbFiles[i].getPath();
                                fileShare = fileUrl;
                                if (fileUrl.endsWith("/")) {
                                    fileShare = fileShare.substring(0, fileShare.length() - 1);
                                }
                                e = fileShare.lastIndexOf(47);
                                if (e != -1) {
                                    fileShare = fileShare.substring(e + 1);
                                }
                                mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                                if (fileShare.equals(share)) {
                                    exist = true;
                                }
                            }
                            this.smbShareDirs.put(ip, mountFiles2);
                        }
                    }
                }
                if (exist) {
                }
                if (isShareDevice) {
                    if (sa != -1) {
                    }
                    oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
                } else {
                    oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
                }
                result.enterPosition = ((Integer) oo[1]).intValue();
                result.sambaDevice = (SambaDevice) oo[0];
                if (isShareDevice) {
                    result.listIndex = uri.split("/").length - 1;
                }
                result.type = ListType.SMB_SHARE;
                if (((Boolean) oo[2]).booleanValue()) {
                    SmbDatabaseUtils.save(this.context, result.sambaDevice);
                }
                return result;
            }
            if (this.sambaManager.mountSmb("//" + ip + "/" + share, mountPoint, ip, identifier.getUser(), identifier.getPassword())) {
                MyLog.d("mount success");
                MountHistoryDatabase.saveMountHistory(this.context, "smb://" + ip + "/" + share, null, identifier.getUser(), identifier.getPassword());
                real = sa == -1 ? new MountFile(mountPoint, url, share) : new StableFile(mountPoint + uri.substring(sa));
                if (real.exists()) {
                    if (real.isFile()) {
                        return new Result(real, 0);
                    }
                    Result result2;
                    if (!FileType.isBDMV(real.getPath())) {
                        children = real.listFiles(this.fileFilter);
                        if (children != null) {
                            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                            result2 = new Result(identifier.getListIndex(), real, children, identifier.getTag() == 3 ? ListType.SMB_FILE : ListType.SMB_SHARE, dex.index, dex.device);
                            if (result == null) {
                                return new Result(0);
                            }
                            smbd = null;
                            savedDevices = this.listInfo.getSavedSmbDevices();
                            if (savedDevices.size() == 0) {
                                savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                                this.listInfo.setSavedSmb(savedDevices);
                            }
                            devices = new ArrayList();
                            devices.addAll(savedDevices);
                            devices.addAll(this.listInfo.getSmbDevices());
                            it = devices.iterator();
                            while (it.hasNext()) {
                                sambaDevice = (SambaDevice) it.next();
                                if (sambaDevice.getType() != 4) {
                                }
                            }
                            exist = false;
                            mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
                            if (smbd != null) {
                                if (mountFiles != null) {
                                    while (r5 < r8) {
                                        if (file.getName().equals(share)) {
                                            exist = true;
                                            break;
                                        }
                                    }
                                } else {
                                    smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                                    if (smbFiles != null) {
                                        count = 0;
                                    } else {
                                        count = smbFiles.length;
                                    }
                                    if (count > 0) {
                                        mountFiles2 = new MountFile[count];
                                        for (i = 0; i < count; i++) {
                                            fileUrl = smbFiles[i].getPath();
                                            fileShare = fileUrl;
                                            if (fileUrl.endsWith("/")) {
                                                fileShare = fileShare.substring(0, fileShare.length() - 1);
                                            }
                                            e = fileShare.lastIndexOf(47);
                                            if (e != -1) {
                                                fileShare = fileShare.substring(e + 1);
                                            }
                                            mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                                            if (fileShare.equals(share)) {
                                                exist = true;
                                            }
                                        }
                                        this.smbShareDirs.put(ip, mountFiles2);
                                    }
                                }
                            }
                            if (exist) {
                            }
                            if (isShareDevice) {
                                if (sa != -1) {
                                }
                                oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
                            } else {
                                oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
                            }
                            result.enterPosition = ((Integer) oo[1]).intValue();
                            result.sambaDevice = (SambaDevice) oo[0];
                            if (isShareDevice) {
                                result.listIndex = uri.split("/").length - 1;
                            }
                            result.type = ListType.SMB_SHARE;
                            if (((Boolean) oo[2]).booleanValue()) {
                                SmbDatabaseUtils.save(this.context, result.sambaDevice);
                            }
                            return result;
                        }
                    } else if (checkBdmv()) {
                        return new Result(real, 2);
                    } else {
                        if (this.openWith == 0) {
                            return new Result(2);
                        }
                        if (this.openWith == 1) {
                            return new Result(real, 1);
                        }
                        if (this.openWith == 3) {
                            result = new Result(real, 3);
                        } else {
                            children = real.listFiles(this.fileFilter);
                            if (children != null) {
                                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                                result2 = new Result(identifier.getListIndex(), real, children, identifier.getTag() == 3 ? ListType.SMB_FILE : ListType.SMB_SHARE, dex.index, dex.device);
                            }
                        }
                        if (result == null) {
                            return new Result(0);
                        }
                        smbd = null;
                        savedDevices = this.listInfo.getSavedSmbDevices();
                        if (savedDevices.size() == 0) {
                            savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                            this.listInfo.setSavedSmb(savedDevices);
                        }
                        devices = new ArrayList();
                        devices.addAll(savedDevices);
                        devices.addAll(this.listInfo.getSmbDevices());
                        it = devices.iterator();
                        while (it.hasNext()) {
                            sambaDevice = (SambaDevice) it.next();
                            if (sambaDevice.getType() != 4) {
                            }
                        }
                        exist = false;
                        mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
                        if (smbd != null) {
                            if (mountFiles != null) {
                                while (r5 < r8) {
                                    if (file.getName().equals(share)) {
                                        exist = true;
                                        break;
                                    }
                                }
                            } else {
                                smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                                if (smbFiles != null) {
                                    count = smbFiles.length;
                                } else {
                                    count = 0;
                                }
                                if (count > 0) {
                                    mountFiles2 = new MountFile[count];
                                    for (i = 0; i < count; i++) {
                                        fileUrl = smbFiles[i].getPath();
                                        fileShare = fileUrl;
                                        if (fileUrl.endsWith("/")) {
                                            fileShare = fileShare.substring(0, fileShare.length() - 1);
                                        }
                                        e = fileShare.lastIndexOf(47);
                                        if (e != -1) {
                                            fileShare = fileShare.substring(e + 1);
                                        }
                                        mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                                        if (fileShare.equals(share)) {
                                            exist = true;
                                        }
                                    }
                                    this.smbShareDirs.put(ip, mountFiles2);
                                }
                            }
                        }
                        if (exist) {
                        }
                        if (isShareDevice) {
                            oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
                        } else {
                            if (sa != -1) {
                            }
                            oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
                        }
                        result.enterPosition = ((Integer) oo[1]).intValue();
                        result.sambaDevice = (SambaDevice) oo[0];
                        if (isShareDevice) {
                            result.listIndex = uri.split("/").length - 1;
                        }
                        result.type = ListType.SMB_SHARE;
                        if (((Boolean) oo[2]).booleanValue()) {
                            SmbDatabaseUtils.save(this.context, result.sambaDevice);
                        }
                        return result;
                    }
                }
            }
            result = null;
            if (result == null) {
                return new Result(0);
            }
            smbd = null;
            savedDevices = this.listInfo.getSavedSmbDevices();
            if (savedDevices.size() == 0) {
                savedDevices = SmbDatabaseUtils.selectByAll(this.context);
                this.listInfo.setSavedSmb(savedDevices);
            }
            devices = new ArrayList();
            devices.addAll(savedDevices);
            devices.addAll(this.listInfo.getSmbDevices());
            it = devices.iterator();
            while (it.hasNext()) {
                sambaDevice = (SambaDevice) it.next();
                if (sambaDevice.getType() != 4) {
                }
            }
            exist = false;
            mountFiles = (MountFile[]) this.smbShareDirs.get(ip);
            if (smbd != null) {
                if (mountFiles != null) {
                    while (r5 < r8) {
                        if (file.getName().equals(share)) {
                            exist = true;
                            break;
                        }
                    }
                } else {
                    smbFiles = new SmbFile("smb://" + ip + "/", new NtlmPasswordAuthentication(ip, smbd.getUser(), smbd.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
                    if (smbFiles != null) {
                        count = smbFiles.length;
                    } else {
                        count = 0;
                    }
                    if (count > 0) {
                        mountFiles2 = new MountFile[count];
                        for (i = 0; i < count; i++) {
                            fileUrl = smbFiles[i].getPath();
                            fileShare = fileUrl;
                            if (fileUrl.endsWith("/")) {
                                fileShare = fileShare.substring(0, fileShare.length() - 1);
                            }
                            e = fileShare.lastIndexOf(47);
                            if (e != -1) {
                                fileShare = fileShare.substring(e + 1);
                            }
                            mountFiles2[i] = new MountFile(this.smbRoot, ip + "#" + ZidooFileUtils.encodeCommand(fileShare), fileUrl, fileShare);
                            if (fileShare.equals(share)) {
                                exist = true;
                            }
                        }
                        this.smbShareDirs.put(ip, mountFiles2);
                    }
                }
            }
            if (exist) {
            }
            if (isShareDevice) {
                oo = this.listInfo.getDeviceAndPosition(this.context, new SambaDevice("smb://" + server + "/" + share + "/", server, ip, identifier.getUser(), identifier.getPassword(), 8));
            } else {
                if (sa != -1) {
                }
                oo = this.listInfo.getSmbShareDeviceAndPosition(this.context, new SambaDevice(url, server, ip, identifier.getUser(), identifier.getPassword(), type));
            }
            result.enterPosition = ((Integer) oo[1]).intValue();
            result.sambaDevice = (SambaDevice) oo[0];
            if (isShareDevice) {
                result.listIndex = uri.split("/").length - 1;
            }
            result.type = ListType.SMB_SHARE;
            if (((Boolean) oo[2]).booleanValue()) {
                SmbDatabaseUtils.save(this.context, result.sambaDevice);
            }
            return result;
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    private Result loadNfsDevice(FastIdentifier identifier) {
        Dex dex = getDeviceAndIndex(this.devices, DeviceType.NFS);
        String ip = identifier.getUri();
        MountFile[] mountFiles = (MountFile[]) this.nfsShareDirs.get(ip);
        ArrayList<File> files;
        if (mountFiles != null) {
            files = new ArrayList();
            for (File mountFile : mountFiles) {
                if (this.fileFilter.accept(mountFile)) {
                    files.add(mountFile);
                }
            }
            File[] children = new File[files.size()];
            files.toArray(children);
            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
            int enterPosition = this.listInfo.addToList(ip);
            File parent = new File(this.nfsRoot);
            Result result = new Result(identifier.getListIndex(), parent, parent, children, ListType.NFS_FILE, dex.index, dex.device, enterPosition, ip);
            Result result2 = null;
            return result;
        }
        try {
            ArrayList<NfsFolder> folders = NfsFactory.getNfsManager(this.context, BoxModel.sModel).openDevice(new NfsDevice(ip));
            int size = folders.size();
            if (size > 0) {
                Object mountFiles2 = new MountFile[folders.size()];
                files = new ArrayList();
                for (int i = 0; i < size; i++) {
                    String path = ((NfsFolder) folders.get(i)).getPath();
                    String share = path;
                    if (share.endsWith("/")) {
                        share = share.substring(0, path.length() - 1);
                    }
                    int e = share.lastIndexOf("/");
                    if (e != -1) {
                        share = path.substring(e + 1);
                    }
                    MountFile file = new MountFile(this.nfsRoot, ip + "#" + ZidooFileUtils.encodeCommand(share), path, share);
                    mountFiles2[i] = file;
                    if (this.fileFilter.accept(file)) {
                        files.add(file);
                    }
                }
                this.nfsShareDirs.put(ip, mountFiles2);
                children = new File[files.size()];
                files.toArray(children);
                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                enterPosition = this.listInfo.addToList(ip);
                parent = new File(this.nfsRoot);
                result2 = new Result(identifier.getListIndex(), parent, parent, children, ListType.NFS_FILE, dex.index, dex.device, enterPosition, ip);
                return result2;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        result2 = null;
        return result2;
    }

    private Result loadNfsFile(FastIdentifier identifier) {
        String share;
        Result result;
        Result result2 = null;
        Dex dex = getDeviceAndIndex(this.devices, DeviceType.NFS);
        NfsManager nfsManager = NfsFactory.getNfsManager(this.context, BoxModel.sModel);
        String url = identifier.getUri();
        int e = url.indexOf("/");
        String ip = url.substring(0, e);
        String uri = url.substring(e + 1);
        int p = uri.indexOf("/");
        if (p == -1) {
            share = uri;
        } else {
            share = uri.substring(0, p);
        }
        MountFile[] mountFiles = (MountFile[]) this.nfsShareDirs.get(ip);
        if (mountFiles == null) {
            try {
                ArrayList<NfsFolder> folders = nfsManager.openDevice(new NfsDevice(ip));
                int size = folders.size();
                if (size > 0) {
                    mountFiles = new MountFile[folders.size()];
                    ArrayList<File> files = new ArrayList();
                    for (int i = 0; i < size; i++) {
                        String path = ((NfsFolder) folders.get(i)).getPath();
                        String sn = path;
                        if (sn.endsWith("/")) {
                            sn = sn.substring(0, sn.length() - 1);
                        }
                        int ee = sn.lastIndexOf("/");
                        if (ee != -1) {
                            sn = path.substring(ee + 1);
                        }
                        File mountFile = new MountFile(this.nfsRoot, ip + "#" + ZidooFileUtils.encodeCommand(sn), path, sn);
                        mountFiles[i] = mountFile;
                        if (this.fileFilter.accept(mountFile)) {
                            files.add(mountFile);
                        }
                    }
                    this.nfsShareDirs.put(ip, mountFiles);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String mountName = ip + "#" + ZidooFileUtils.encodeCommand(share);
        String mountPath = dex.device.getPath() + "/" + mountName;
        File real;
        Result result3;
        File[] files2;
        File[] children;
        if (mountFiles != null && new File(mountPath).exists() && NfsManager.isNfsMounted(ip, mountPath)) {
            MyLog.d("has mounted");
            if (p == -1) {
                real = new MountFile(mountPath, url, share);
            } else {
                real = new StableFile(dex.device.getPath() + "/" + mountName + uri.substring(p));
            }
            if (real.exists()) {
                if (real.isFile()) {
                    result3 = new Result(real, 0);
                } else if (FileType.isBDMV(real.getPath())) {
                    if (checkBdmv()) {
                        result3 = new Result(real, 2);
                    } else {
                        result = null;
                    }
                    if (this.openWith == 0) {
                        result3 = new Result(2);
                    } else if (this.openWith == 1) {
                        result3 = new Result(real, 1);
                    } else if (this.openWith == 3) {
                        result3 = new Result(real, 3);
                    } else {
                        files2 = real.listFiles();
                        if (files2 != null) {
                            Utils.sortFiles(files2, AppConstant.sPrefereancesSortWay);
                            return new Result(identifier.getListIndex(), real, new File(this.nfsRoot), files2, ListType.NFS_FILE, dex.index, dex.device, this.listInfo.addToList(ip), ip);
                        }
                    }
                } else {
                    children = real.listFiles();
                    if (children != null) {
                        Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                        result = null;
                        return new Result(identifier.getListIndex(), real, new File(this.nfsRoot), children, ListType.NFS_FILE, dex.index, dex.device, this.listInfo.addToList(ip), ip);
                    }
                }
            }
            result = null;
        } else {
            MyLog.d("need mount");
            MountFile mf = null;
            try {
                for (MountFile mountFile2 : mountFiles) {
                    if (mountFile2.getShareName().equals(share)) {
                        mf = mountFile2;
                        break;
                    }
                }
                if (mf != null) {
                    boolean mount = nfsManager.mountNfs(ip, mf.getUrl(), mountName);
                    MyLog.d("mount favorite nfs " + (mount ? "success" : "fail!"));
                    if (mount) {
                        MountHistoryDatabase.saveMountHistory(this.context, "nfs://" + ip + "/" + share, mf.getUrl(), null, null);
                        if (p == -1) {
                            real = new MountFile(mountPath, url, share);
                        } else {
                            real = new StableFile(dex.device.getPath() + "/" + mountName + uri.substring(p));
                        }
                        if (real.exists()) {
                            if (real.isFile()) {
                                result3 = new Result(real, 0);
                            } else if (FileType.isBDMV(real.getPath())) {
                                if (this.onShowBdmvOpenWith.onShowBdmvOpenWith(this)) {
                                    synchronized (this) {
                                        try {
                                            wait();
                                        } catch (InterruptedException ex2) {
                                            ex2.printStackTrace();
                                        }
                                    }
                                } else {
                                    result2 = new Result(real, 2);
                                }
                                if (this.openWith == 0) {
                                    result3 = new Result(2);
                                } else if (this.openWith == 1) {
                                    result3 = new Result(real, 1);
                                } else if (this.openWith == 3) {
                                    result3 = new Result(real, 3);
                                } else {
                                    files2 = real.listFiles();
                                    Utils.sortFiles(files2, AppConstant.sPrefereancesSortWay);
                                    result = new Result(identifier.getListIndex(), real, files2, ListType.FILE, dex.index, dex.device);
                                }
                            } else {
                                children = real.listFiles();
                                if (children != null) {
                                    Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                                    result = new Result(identifier.getListIndex(), real, new File(this.nfsRoot), children, ListType.NFS_FILE, dex.index, dex.device, this.listInfo.addToList(ip), ip);
                                }
                            }
                        }
                    }
                }
                result = null;
            } catch (Exception ex3) {
                result = result2;
                ex3.printStackTrace();
            }
        }
        return result;
    }

    private Dex getDeviceAndIndex(ArrayList<DeviceInfo> devices, DeviceType type) {
        for (int i = 0; i < devices.size(); i++) {
            DeviceInfo device = (DeviceInfo) devices.get(i);
            if (device.getType() == type) {
                return new Dex(device, i);
            }
        }
        return null;
    }

    private Dex getDeviceAndIndex(ArrayList<DeviceInfo> devices, String uuid, String uri) {
        Dex dex = null;
        for (int i = 0; i < devices.size(); i++) {
            DeviceInfo device = (DeviceInfo) devices.get(i);
            DeviceType type = device.getType();
            if (type == DeviceType.SD || type == DeviceType.HDD || type == DeviceType.TF) {
                if (device.getBlock() == null || device.getBlock().getUuid() == null) {
                    if ((device.getPath() + "/").equals(uuid) && new File(device, uri).exists()) {
                        dex = new Dex(device, i);
                    }
                } else if (device.getBlock().getUuid().equals(uuid)) {
                    return new Dex(device, i);
                }
            }
        }
        return dex;
    }

    private boolean checkBdmv() {
        if (this.openWith != -1) {
            return false;
        }
        if (!this.onShowBdmvOpenWith.onShowBdmvOpenWith(this)) {
            return true;
        }
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
