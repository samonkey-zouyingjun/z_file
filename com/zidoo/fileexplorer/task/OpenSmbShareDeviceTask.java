package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public class OpenSmbShareDeviceTask extends BaseTask<Result> {
    Context context;
    SambaDevice device;
    FileFilter fileFilter;
    int flag;
    ListInfo listInfo;
    boolean login;
    File parent;
    int position;
    SambaManager sambaManager;
    boolean saved;

    public static class Result {
        File[] children;
        int code;
        SambaDevice device;
        int flag;
        int position;
        boolean saved;
        File share;

        public Result(int code, SambaDevice device, File share, File[] children, int position, boolean saved, int flag) {
            this.code = code;
            this.device = device;
            this.share = share;
            this.children = children;
            this.position = position;
            this.saved = saved;
            this.flag = flag;
        }

        public int getCode() {
            return this.code;
        }

        public SambaDevice getDevice() {
            return this.device;
        }

        public File getShare() {
            return this.share;
        }

        public File[] getChildren() {
            return this.children;
        }

        public int getPosition() {
            return this.position;
        }

        public boolean isSaved() {
            return this.saved;
        }

        public int getFlag() {
            return this.flag;
        }
    }

    public OpenSmbShareDeviceTask(Handler handler, int what, Context context, SambaDevice device, File parent, FileFilter fileFilter, int position, boolean saved, boolean login, int flag, SambaManager sambaManager, ListInfo listInfo) {
        super(handler, what);
        this.context = context;
        this.device = device;
        this.parent = parent;
        this.fileFilter = fileFilter;
        this.listInfo = listInfo;
        this.position = position;
        this.saved = saved;
        this.login = login;
        this.flag = flag;
        this.sambaManager = sambaManager;
    }

    protected Result doInBackground() {
        String share;
        String other;
        File shareFile;
        String mountPoint;
        File[] children;
        Object[] objects;
        int what;
        String ip;
        MyLog.d("Open smb share Device -" + this.device.toString());
        String url = this.device.getUrl();
        String temp = url.substring(6);
        int s = temp.indexOf("/");
        if (s != -1) {
            temp = temp.substring(s + 1);
            int e = temp.indexOf("/");
            if (e == -1) {
                share = temp;
                other = "";
            } else {
                share = temp.substring(0, e);
                other = temp.substring(e);
            }
        } else {
            share = temp;
            other = "";
        }
        String mountName = this.device.getIp() + "#" + ZidooFileUtils.encodeCommand(share);
        if (!other.equals("")) {
            if (!other.equals("/")) {
                shareFile = new File(this.parent.getPath() + "/" + mountName + other);
                mountPoint = this.parent.getPath() + "/" + mountName;
                if (new File(mountPoint).exists()) {
                    if (this.sambaManager.isMounted(this.device.getIp(), share, mountPoint)) {
                        children = shareFile.listFiles(this.fileFilter);
                        if (children == null) {
                            objects = this.listInfo.getDeviceAndPosition(this.context, this.device);
                            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                            if (!((Boolean) objects[2]).booleanValue()) {
                                SmbDatabaseUtils.save(this.context, this.device);
                            }
                            what = -1;
                            return new Result(0, this.device, shareFile, children, ((Integer) objects[1]).intValue(), this.saved, this.flag);
                        }
                        what = -1;
                        return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
                    }
                }
                String shareUrl = "smb://" + this.device.getIp() + "/" + share + "/";
                ip = this.device.getIp();
                try {
                    ip = NbtAddress.getByName(this.device.getHost()).getHostAddress();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (new SmbFile(shareUrl, new NtlmPasswordAuthentication(ip, this.device.getUser(), this.device.getPassWord())).list() != null && this.sambaManager.mountSmb("//" + ip + "/" + share, mountPoint, ip, this.device.getUser(), this.device.getPassWord())) {
                    MountHistoryDatabase.saveMountHistory(this.context, "smb://" + ip + "/" + share, null, this.device.getUser(), this.device.getPassWord());
                    children = shareFile.listFiles(this.fileFilter);
                    if (children != null) {
                        objects = this.listInfo.getDeviceAndPosition(this.context, this.device);
                        if (this.saved) {
                            SmbDatabaseUtils.save(this.context, this.device);
                        } else if (this.login || !this.device.getIp().equals(ip)) {
                            this.device.setIp(ip);
                            SmbDatabaseUtils.update(this.context, this.device);
                        }
                        Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                        what = -1;
                        return new Result(0, this.device, shareFile, children, ((Integer) objects[1]).intValue(), this.saved, this.flag);
                    }
                }
                what = -1;
                return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
            }
        }
        shareFile = new MountFile(this.parent.getPath(), mountName, url, share);
        mountPoint = this.parent.getPath() + "/" + mountName;
        if (new File(mountPoint).exists()) {
            if (this.sambaManager.isMounted(this.device.getIp(), share, mountPoint)) {
                children = shareFile.listFiles(this.fileFilter);
                if (children == null) {
                    what = -1;
                    return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
                }
                objects = this.listInfo.getDeviceAndPosition(this.context, this.device);
                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                if (((Boolean) objects[2]).booleanValue()) {
                    SmbDatabaseUtils.save(this.context, this.device);
                }
                what = -1;
                return new Result(0, this.device, shareFile, children, ((Integer) objects[1]).intValue(), this.saved, this.flag);
            }
        }
        try {
            String shareUrl2 = "smb://" + this.device.getIp() + "/" + share + "/";
            ip = this.device.getIp();
            ip = NbtAddress.getByName(this.device.getHost()).getHostAddress();
            MountHistoryDatabase.saveMountHistory(this.context, "smb://" + ip + "/" + share, null, this.device.getUser(), this.device.getPassWord());
            children = shareFile.listFiles(this.fileFilter);
            if (children != null) {
                objects = this.listInfo.getDeviceAndPosition(this.context, this.device);
                if (this.saved) {
                    SmbDatabaseUtils.save(this.context, this.device);
                } else {
                    this.device.setIp(ip);
                    SmbDatabaseUtils.update(this.context, this.device);
                }
                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                what = -1;
                return new Result(0, this.device, shareFile, children, ((Integer) objects[1]).intValue(), this.saved, this.flag);
            }
            what = -1;
        } catch (Throwable e3) {
            what = -3;
            MyLog.w("loadShareDevice", e3);
        } catch (Throwable e32) {
            what = -2;
            MyLog.w("loadShareDevice", e32);
        }
        return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
    }
}
