package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public class RemoveSmbTask extends BaseTask<Integer> {
    Context context;
    SambaDevice device;
    File parent;
    int position;
    SambaManager sambaManager;
    ArrayList<SambaDevice> savedDevices;
    HashMap<String, MountFile[]> shareDirs;

    public RemoveSmbTask(Handler handler, int what, Context context, SambaDevice device, File parent, int position, SambaManager sambaManager, HashMap<String, MountFile[]> shareDirs, ArrayList<SambaDevice> savedDevices) {
        super(handler, what);
        this.context = context;
        this.device = device;
        this.parent = parent;
        this.position = position;
        this.sambaManager = sambaManager;
        this.shareDirs = shareDirs;
        this.savedDevices = savedDevices;
    }

    protected Integer doInBackground() {
        SmbDatabaseUtils.delete(this.context, this.device.getUrl());
        if (this.device.getType() == 4) {
            File[] files = (MountFile[]) this.shareDirs.remove(this.device.getIp());
            if (files != null) {
                for (MountFile file : files) {
                    if (file.exists()) {
                        this.sambaManager.unMountSmb(file);
                    }
                }
                MountHistoryDatabase.deleteMountHistory(this.context, files, true);
            }
        } else {
            String[] ss = this.device.getUrl().substring(6).split("/");
            if (ss.length > 1) {
                for (int i = 0; i < this.savedDevices.size(); i++) {
                    if (i != this.position) {
                        SambaDevice sambaDevice = (SambaDevice) this.savedDevices.get(i);
                        if (!sambaDevice.getIp().equals(this.device.getIp())) {
                            continue;
                        } else if (sambaDevice.getType() != 4) {
                            if (ss[1].equals(sambaDevice.getUrl().substring(6).split("/")[1])) {
                                return Integer.valueOf(this.position);
                            }
                        } else if (sambaDevice.getUser().equals(this.device.getUser()) && sambaDevice.getPassWord().equals(this.device.getPassWord())) {
                            return Integer.valueOf(this.position);
                        }
                    }
                }
                String mountPoint = this.parent.getPath() + "/" + this.device.getIp() + "#" + ZidooFileUtils.encodeCommand(ss[1]);
                File mountFile = new File(mountPoint);
                if (mountFile.exists()) {
                    this.sambaManager.unMountSmb(new File(mountPoint));
                    MountHistoryDatabase.deleteMountHistory(this.context, mountFile, true);
                }
            }
        }
        return Integer.valueOf(this.position);
    }
}
