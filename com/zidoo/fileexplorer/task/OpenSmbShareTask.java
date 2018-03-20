package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;

public class OpenSmbShareTask extends BaseTask<Result> {
    private Context context;
    private SambaDevice device;
    private MountFile file;
    private FileFilter fileFilter;
    private int flag;
    private boolean login;
    private int position;
    private SambaManager sambaManager;
    private SambaDevice savedDevice = null;
    private File smbRoot;

    public static class Result {
        File[] children;
        int code;
        SambaDevice device;
        int flag;
        int operate;
        File parent;
        int position;

        public Result(int code, File parent, File[] children, int flag, SambaDevice device, int operate, int position) {
            this.code = code;
            this.parent = parent;
            this.children = children;
            this.flag = flag;
            this.device = device;
            this.operate = operate;
            this.position = position;
        }

        public int getCode() {
            return this.code;
        }

        public File getParent() {
            return this.parent;
        }

        public File[] getChildren() {
            return this.children;
        }

        public int getFlag() {
            return this.flag;
        }

        public SambaDevice getDevice() {
            return this.device;
        }

        public int getOperate() {
            return this.operate;
        }

        public int getPosition() {
            return this.position;
        }
    }

    public OpenSmbShareTask(Handler handler, int what, Context context, SambaDevice device, SambaDevice savedDevice, FileFilter fileFilter, File smbRoot, MountFile file, int position, boolean login, int flag, SambaManager sambaManager) {
        super(handler, what);
        this.context = context;
        this.device = device;
        this.savedDevice = savedDevice;
        this.fileFilter = fileFilter;
        this.smbRoot = smbRoot;
        this.file = file;
        this.position = position;
        this.login = login;
        this.flag = flag;
        this.sambaManager = sambaManager;
    }

    protected Result doInBackground() {
        File[] children;
        int what;
        SambaDevice newDevice;
        MyLog.d("Open smb share - " + this.device);
        if (this.file.exists()) {
            if (this.sambaManager.isMounted(this.device.getIp(), this.file.getShareName(), this.file.getPath())) {
                children = this.file.listFiles(this.fileFilter);
                if (children != null) {
                    Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                    Object newDevice2 = null;
                    what = -1;
                    return new Result(1, this.file, children, this.flag, this.device, 0, this.position);
                }
                SambaDevice newDevice3 = null;
                what = -1;
                return new Result(what, this.file, null, this.flag, newDevice3, 0, this.position);
            }
        }
        if (isStop()) {
            newDevice3 = null;
            what = -1;
            return new Result(-1, this.file, null, this.flag, this.device, 0, this.position);
        }
        String url = this.file.getUrl();
        if (this.login || this.savedDevice == null) {
            newDevice = this.device;
        } else {
            newDevice = this.savedDevice;
        }
        try {
            if (new SmbFile(url, new NtlmPasswordAuthentication(newDevice.getIp(), newDevice.getUser(), newDevice.getPassWord())).list() != null) {
                boolean mount = this.sambaManager.mountSmb("//" + newDevice.getIp() + "/" + this.file.getShareName(), this.file.getPath(), newDevice.getIp(), newDevice.getUser(), newDevice.getPassWord());
                MyLog.d("Mount SMB " + (mount ? "success" : "fail"));
                if (mount) {
                    MountHistoryDatabase.saveMountHistory(this.context, "smb://" + newDevice.getIp() + "/" + this.file.getShareName(), null, this.device.getUser(), this.device.getPassWord());
                    int operate = 0;
                    if (this.savedDevice != null) {
                        if (this.login || !this.savedDevice.getIp().equals(this.device.getIp())) {
                            operate = 1;
                            SmbDatabaseUtils.update(this.context, newDevice);
                        }
                    } else if (this.login) {
                        operate = 2;
                        SmbDatabaseUtils.save(this.context, newDevice);
                    }
                    children = this.file.listFiles(this.fileFilter);
                    if (children != null) {
                        Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                        newDevice3 = newDevice;
                        what = -1;
                        return new Result(0, this.file, children, this.flag, newDevice, operate, this.position);
                    }
                }
            }
            newDevice3 = newDevice;
            what = -1;
        } catch (MalformedURLException e) {
            what = -3;
            newDevice3 = newDevice;
        } catch (SmbException e2) {
            newDevice3 = new SambaDevice(url, newDevice.getHost(), newDevice.getIp(), newDevice.getUser(), newDevice.getPassWord(), 8);
            what = -2;
        }
        return new Result(what, this.file, null, this.flag, newDevice3, 0, this.position);
    }
}
