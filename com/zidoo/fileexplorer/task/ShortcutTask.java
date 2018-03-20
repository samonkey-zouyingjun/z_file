package com.zidoo.fileexplorer.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.FastIdentifier;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.main.HomeActivity;
import com.zidoo.fileexplorer.tool.FileOperater;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import zidoo.browse.FileIdentifier;
import zidoo.file.FileType;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.samba.exs.SambaDevice;
import zidoo.tool.ZidooFileUtils;

@SuppressLint({"DefaultLocale"})
public class ShortcutTask extends BaseTask<Boolean> {
    Context context;
    DeviceInfo device;
    int enterSmbOrNfsIndex;
    int listIndex;
    ListInfo listInfo;
    ListType listType;
    BoxModel model;
    String name;
    int position;

    public ShortcutTask(Handler handler, int what, Context context, BoxModel model, DeviceInfo device, ListInfo listInfo, ListType listType, String name, int position, int listIndex, int enterSmbOrNfsIndex) {
        super(handler, what);
        this.context = context;
        this.model = model;
        this.device = device;
        this.listInfo = listInfo;
        this.listType = listType;
        this.name = name;
        this.position = position;
        this.listIndex = listIndex;
        this.enterSmbOrNfsIndex = enterSmbOrNfsIndex;
    }

    protected Boolean doInBackground() {
        switch (this.device.getType()) {
            case FLASH:
                sendFlashShortcut();
                break;
            case TF:
            case SD:
            case HDD:
                sendUsbShortcut();
                break;
            case SMB:
                sendSmbShortcut();
                break;
            case NFS:
                sendNfsShortcut();
                break;
        }
        return Boolean.valueOf(true);
    }

    private void sendFlashShortcut() {
        File file = this.listInfo.getChild(this.position);
        if (file.isDirectory()) {
            String abstractPath = file.getPath().substring(this.device.getPath().length());
            if (this.model.isSupportBDMV() && FileType.isBDMV(file.getPath())) {
                FileIdentifier identifier = new FileIdentifier(0, abstractPath, this.device.getPath());
                identifier.setExtra(file.getPath());
                sendServiceShortcut(identifier, FileOperater.getFileBigIconResource(file));
                return;
            }
            FastIdentifier identifier2 = new FastIdentifier(abstractPath, 0, this.listIndex);
            identifier2.setUuid(this.device.getPath());
            sendIdentifierShortcut(identifier2, FileOperater.getFileBigIconResource(file));
            return;
        }
        identifier = new FileIdentifier(0, file.getPath().substring(this.device.getPath().length()), this.device.getPath());
        identifier.setExtra(file.getPath());
        sendServiceShortcut(identifier, FileOperater.getFileBigIconResource(file));
    }

    private void sendUsbShortcut() {
        File file = this.listInfo.getChild(this.position);
        if (!file.isDirectory() || (this.model.isSupportBDMV() && FileType.isBDMV(file.getPath()))) {
            String uuid;
            if (this.device.getBlock() == null || this.device.getBlock().getUuid() == null) {
                uuid = this.device.getPath() + "/";
            } else {
                uuid = this.device.getBlock().getUuid();
            }
            FileIdentifier identifier = new FileIdentifier(1, file.getPath().substring(this.device.getPath().length()), uuid);
            identifier.setExtra(file.getPath());
            sendServiceShortcut(identifier, FileOperater.getFileBigIconResource(file));
            return;
        }
        FastIdentifier identifier2 = new FastIdentifier(file.getPath().substring(this.device.getPath().length()), 1, this.listIndex);
        identifier2.setUuid(this.device.getPath() + "/");
        if (this.device.getBlock() == null || this.device.getBlock().getUuid() == null) {
            identifier2.setUuid(this.device.getPath() + "/");
        } else {
            identifier2.setUuid(this.device.getBlock().getUuid());
        }
        sendIdentifierShortcut(identifier2, FileOperater.getFileBigIconResource(file));
    }

    private void sendSmbShortcut() {
        int i = 2;
        SambaDevice smb;
        if (this.listType == ListType.SMB_DEVICE) {
            smb = this.listInfo.getSmbDevice(this.position);
            String url = smb.getUrl();
            if (smb.getType() != 4) {
                i = 6;
            }
            FastIdentifier identifier = new FastIdentifier(url, i, this.listIndex);
            identifier.setUuid(smb.getIp());
            identifier.setUser(smb.getUser());
            identifier.setPassword(smb.getPassWord());
            sendIdentifierShortcut(identifier, R.drawable.icon_net);
            return;
        }
        String mountName;
        String tails;
        File file = this.listInfo.getChild(this.position);
        String temp = file.getPath().substring(this.device.getPath().length() + 1);
        int s = temp.indexOf(47);
        if (s == -1) {
            mountName = temp;
            tails = "";
        } else {
            mountName = temp.substring(0, s);
            tails = temp.substring(s);
        }
        String share = ZidooFileUtils.decodeCommand(mountName.substring(mountName.indexOf(35) + 1));
        smb = this.listInfo.getSmbDevice(this.enterSmbOrNfsIndex);
        SambaDevice smbs = this.listInfo.getSavedSmbShare(smb.getIp(), share);
        if (smbs != null) {
            smb = smbs;
        }
        String url2 = "smb://" + smb.getHost() + "/" + share + tails;
        if (!file.isDirectory() || (this.model.isSupportBDMV() && FileType.isBDMV(file.getPath()))) {
            FileIdentifier identifier2 = new FileIdentifier(2, url2, smb.getIp());
            identifier2.setUser(smb.getUser());
            identifier2.setPassword(smb.getPassWord());
            identifier2.setExtra(smb.getHost());
            sendServiceShortcut(identifier2, FileOperater.getFileBigIconResource(file));
            return;
        }
        identifier = new FastIdentifier(url2, 3, this.listIndex);
        identifier.setUuid(smb.getIp());
        identifier.setUser(smb.getUser());
        identifier.setPassword(smb.getPassWord());
        sendIdentifierShortcut(identifier, FileOperater.getFileBigIconResource(file));
    }

    private void sendNfsShortcut() {
        if (this.listType == ListType.NFS_DEVICE) {
            NfsDevice nfs = this.listInfo.getNfs(this.position);
            FastIdentifier identifier = new FastIdentifier(nfs.ip, 4, this.listIndex);
            identifier.setUuid(nfs.ip);
            sendIdentifierShortcut(identifier, R.drawable.icon_net);
            return;
        }
        String mountName;
        String tails;
        File file = this.listInfo.getChild(this.position);
        String temp = file.getPath().substring(this.device.getPath().length() + 1);
        int s = temp.indexOf(47);
        if (s == -1) {
            mountName = temp;
            tails = "";
        } else {
            mountName = temp.substring(0, s);
            tails = temp.substring(s);
        }
        String share = ZidooFileUtils.decodeCommand(mountName.substring(mountName.indexOf(35) + 1));
        nfs = this.listInfo.getNfs(this.enterSmbOrNfsIndex);
        String url = nfs.ip + "/" + share + tails;
        if (!file.isDirectory() || (this.model.isSupportBDMV() && FileType.isBDMV(file.getPath()))) {
            sendServiceShortcut(new FileIdentifier(3, url, nfs.ip), FileOperater.getFileBigIconResource(file));
            return;
        }
        identifier = new FastIdentifier(url, 5, this.listIndex);
        identifier.setUuid(nfs.ip);
        sendIdentifierShortcut(identifier, FileOperater.getFileBigIconResource(file));
    }

    private void sendIdentifierShortcut(FastIdentifier identifier, int icon) {
        Intent intent = new Intent(this.context, HomeActivity.class);
        intent.putExtra(AppConstant.EXTRA_ENTRY_MODE, 2);
        intent.putExtra(AppConstant.EXTRA_FAST_IDENTIFIER, identifier.toJson());
        Utils.createShortcut(this.context, this.name, intent, icon, null);
    }

    private void sendServiceShortcut(FileIdentifier identifier, int icon) {
        Intent intent = new Intent("com.zidoo.service.shortcut");
        intent.setPackage(this.context.getPackageName());
        intent.putExtra(AppConstant.EXTRA_FILE_IDENTIFY, identifier.toJson());
        Utils.createShortcut(this.context, this.name, intent, icon, null);
    }
}
