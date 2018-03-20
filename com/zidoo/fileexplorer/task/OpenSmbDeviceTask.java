package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import zidoo.samba.exs.SambaDevice;

public class OpenSmbDeviceTask extends BaseTask<Result> {
    Context context;
    SambaDevice device;
    FileFilter fileFilter;
    int flag;
    boolean login;
    int position;
    boolean saved;
    HashMap<String, MountFile[]> shareDirs;
    File smbRoot;

    public static final class Result {
        File[] children;
        int code;
        SambaDevice device;
        int flag;
        File parent;
        int position;
        boolean saved;

        public Result(int code, SambaDevice device, File parent, File[] children, int position, boolean saved, int flag) {
            this.code = code;
            this.device = device;
            this.parent = parent;
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

        public File getParent() {
            return this.parent;
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

    public OpenSmbDeviceTask(Handler handler, int what, Context context, SambaDevice device, int position, boolean saved, boolean login, int flag, FileFilter fileFilter, HashMap<String, MountFile[]> shareDirs, File smbRoot) {
        super(handler, what);
        this.context = context;
        this.device = device;
        this.position = position;
        this.saved = saved;
        this.login = login;
        this.flag = flag;
        this.fileFilter = fileFilter;
        this.shareDirs = shareDirs;
        this.smbRoot = smbRoot;
    }

    public Result doInBackground() {
        Throwable e;
        MyLog.d("Open device - " + this.device.toString());
        MountFile[] mountFiles = (MountFile[]) this.shareDirs.get(this.device.getIp());
        ArrayList<File> files;
        if (mountFiles != null) {
            files = new ArrayList();
            for (File src : mountFiles) {
                if (this.fileFilter.accept(src)) {
                    files.add(src);
                }
            }
            File[] children = new File[files.size()];
            files.toArray(children);
            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
            int what = -1;
            return new Result(0, this.device, this.smbRoot, children, this.position, this.saved, this.flag);
        }
        boolean change = false;
        try {
            int i;
            int count;
            if (this.device.getIp().equals(this.device.getHost())) {
                NbtAddress[] all = NbtAddress.getAllByAddress(NbtAddress.getByName(this.device.getHost()));
                i = 0;
                while (i < all.length) {
                    NbtAddress n = all[i];
                    if (n.isGroupAddress() || n.getNameType() != 0) {
                        i++;
                    } else if (n.getHostName() != null) {
                        this.device.setHost(n.getHostName());
                        change = true;
                    }
                }
            }
            SmbFile[] smbFiles = new SmbFile("smb://" + this.device.getIp() + "/", new NtlmPasswordAuthentication(this.device.getIp(), this.device.getUser(), this.device.getPassWord())).listFiles(Utils.getDefaultSmbFileFilter());
            if (smbFiles == null) {
                count = 0;
            } else {
                count = smbFiles.length;
            }
            MyLog.v("count:" + count);
            if (count > 0) {
                Object mountFiles2 = new MountFile[count];
                files = new ArrayList();
                for (i = 0; i < count; i++) {
                    String url = smbFiles[i].getPath();
                    String share = url;
                    if (url.endsWith("/")) {
                        share = share.substring(0, share.length() - 1);
                    }
                    int e2 = share.lastIndexOf(47);
                    if (e2 != -1) {
                        share = share.substring(e2 + 1);
                    }
                    File mountFile = new MountFile(this.smbRoot.getPath(), this.device.getIp() + "#" + share, url, share);
                    mountFiles2[i] = mountFile;
                    if (this.fileFilter.accept(mountFile)) {
                        files.add(mountFile);
                    }
                }
                this.shareDirs.put(this.device.getIp(), mountFiles2);
                children = new File[files.size()];
                files.toArray(children);
                if (!this.saved) {
                    SmbDatabaseUtils.save(this.context, this.device);
                } else if (this.login || change) {
                    SmbDatabaseUtils.update(this.context, this.device);
                }
                Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                what = -1;
                return new Result(0, this.device, this.smbRoot, children, this.position, this.saved, this.flag);
            }
            what = -1;
            return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
        } catch (Throwable e3) {
            what = -2;
            try {
                MyLog.w("OpenSmbDevice", e3);
            } catch (Exception e4) {
                e3 = e4;
                e3.printStackTrace();
                MyLog.w("last enSmbDevice", e3);
                return new Result(what, this.device, null, null, this.position, this.saved, this.flag);
            }
        } catch (Throwable e32) {
            what = -3;
            MyLog.w("OpenSmbDevice", e32);
        } catch (Throwable e322) {
            what = -4;
            MyLog.w("OpenSmbDevice", e322);
        } catch (Exception e5) {
            e322 = e5;
            what = -1;
            e322.printStackTrace();
            MyLog.w("last enSmbDevice", e322);
        }
    }
}
