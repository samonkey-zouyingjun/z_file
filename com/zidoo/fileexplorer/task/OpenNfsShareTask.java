package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.bean.StableFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsManager;

public class OpenNfsShareTask extends BaseTask<Result> {
    Context context;
    boolean copy;
    NfsDevice device;
    FileFilter fileFilter;
    MountFile share;

    public static final class Result {
        File[] children;
        boolean copy;
        File dir;

        public Result(File dir, File[] children, boolean copy) {
            this.dir = dir;
            this.children = children;
            this.copy = copy;
        }

        public File getDir() {
            return this.dir;
        }

        public File[] getFiles() {
            return this.children;
        }

        public boolean isCopy() {
            return this.copy;
        }
    }

    public OpenNfsShareTask(Handler handler, int what, Context context, MountFile share, NfsDevice device, boolean copy, FileFilter fileFilter) {
        super(handler, what);
        this.context = context;
        this.share = share;
        this.device = device;
        this.copy = copy;
        this.fileFilter = fileFilter;
    }

    protected Result doInBackground() {
        File[] children = null;
        NfsManager nfsManager = NfsFactory.getNfsManager(this.context, BoxModel.sModel);
        if (this.share.exists() && nfsManager.isNfsMounted(this.device.ip, this.share.getUrl(), this.share.getPath())) {
            children = this.share.listFiles(this.fileFilter);
        } else {
            try {
                String sharePath = this.share.getUrl();
                if (sharePath != null && nfsManager.mountNfs(this.device.ip, sharePath, this.share.getFileName())) {
                    MountHistoryDatabase.saveMountHistory(this.context, "nfs://" + this.device.ip + "/" + this.share.getShareName(), sharePath, null, null);
                    children = new StableFile(this.share.getPath()).listFiles(this.fileFilter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!this.share.exists()) {
                this.share.mkdirs();
            }
        }
        if (children == null) {
            children = new File[0];
        }
        Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
        return new Result(this.share, children, this.copy);
    }
}
