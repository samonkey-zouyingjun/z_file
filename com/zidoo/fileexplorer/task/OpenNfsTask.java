package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsFolder;

public class OpenNfsTask extends BaseTask<Result> {
    boolean back;
    Context context;
    NfsDevice device;
    FileFilter fileFilter;
    File nfsRoot;
    int position;
    HashMap<String, MountFile[]> shareDirs;

    public static final class Result {
        boolean back;
        File[] children;
        File dir;
        String ip;
        int position;

        public Result(File dir, String ip, File[] children, int position, boolean back) {
            this.dir = dir;
            this.ip = ip;
            this.children = children;
            this.position = position;
            this.back = back;
        }

        public File getDir() {
            return this.dir;
        }

        public String getIp() {
            return this.ip;
        }

        public File[] getFiles() {
            return this.children;
        }

        public int getPosition() {
            return this.position;
        }

        public boolean isBack() {
            return this.back;
        }
    }

    public OpenNfsTask(Handler handler, int what, Context context, NfsDevice device, int position, HashMap<String, MountFile[]> shareDirs, FileFilter fileFilter, File nfsRoot, boolean back) {
        super(handler, what);
        this.context = context;
        this.device = device;
        this.position = position;
        this.shareDirs = shareDirs;
        this.fileFilter = fileFilter;
        this.nfsRoot = nfsRoot;
        this.back = back;
    }

    protected Result doInBackground() {
        String ip = this.device.ip;
        MountFile[] mountFiles = (MountFile[]) this.shareDirs.get(ip);
        ArrayList<File> files;
        if (mountFiles != null) {
            files = new ArrayList();
            for (MountFile mountFile : mountFiles) {
                if (this.fileFilter.accept(mountFile)) {
                    files.add(mountFile);
                }
            }
            File[] children = new File[files.size()];
            files.toArray(children);
            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
            return new Result(this.nfsRoot, ip, children, this.position, this.back);
        }
        children = null;
        try {
            ArrayList<NfsFolder> folders = NfsFactory.getNfsManager(this.context, BoxModel.sModel).openDevice(this.device);
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
                    MountFile file = new MountFile(this.nfsRoot.getPath(), ip + "#" + share, path, share);
                    mountFiles2[i] = file;
                    if (this.fileFilter.accept(file)) {
                        files.add(file);
                    }
                }
                this.shareDirs.put(ip, mountFiles2);
                children = new File[files.size()];
                files.toArray(children);
                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (children == null) {
            children = new File[0];
        }
        return new Result(this.nfsRoot, ip, children, this.position, this.back);
    }
}
