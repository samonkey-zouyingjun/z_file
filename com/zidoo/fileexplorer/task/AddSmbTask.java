package com.zidoo.fileexplorer.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.Utils;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import zidoo.samba.exs.SambaDevice;

public class AddSmbTask extends BaseTask<Result> {
    Context context;
    Dialog dialog;
    ListInfo listInfo;
    String pwd;
    String url;
    String user;

    public static final class Result {
        int code;
        SambaDevice device;
        Dialog dialog;

        public Result(int code, SambaDevice device, Dialog dialog) {
            this.code = code;
            this.device = device;
            this.dialog = dialog;
        }

        public int getCode() {
            return this.code;
        }

        public SambaDevice getDevice() {
            return this.device;
        }

        public Dialog getDialog() {
            return this.dialog;
        }
    }

    public AddSmbTask(Dialog dialog, Handler handler, int what, Context context, String url, String user, String pwd, ListInfo listInfo) {
        super(handler, what);
        this.dialog = dialog;
        this.context = context;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        this.listInfo = listInfo;
    }

    protected Result doInBackground() {
        SambaDevice device;
        UnknownHostException e;
        MalformedURLException e2;
        SmbException e3;
        int what = -1;
        try {
            if (!this.url.startsWith("smb://")) {
                String str;
                if (this.url.startsWith("//")) {
                    str = "smb:" + this.url;
                } else {
                    str = "smb://" + this.url;
                }
                this.url = str;
            }
            if (!this.url.endsWith("/")) {
                this.url += "/";
            }
            String server = this.url.substring(6);
            int s = server.indexOf("/");
            if (s != -1) {
                server = server.substring(0, s);
            }
            String hostName = null;
            NbtAddress nbt = NbtAddress.getByName(server);
            if (nbt != null && nbt.isActive()) {
                SmbFile smbFile;
                String name;
                String ip = nbt.getHostAddress();
                NbtAddress[] all = NbtAddress.getAllByAddress(nbt);
                int i = 0;
                while (i < all.length) {
                    NbtAddress n = all[i];
                    if (n.isGroupAddress() || n.getNameType() != 0) {
                        i++;
                    } else {
                        if (n.getHostName() != null) {
                            hostName = n.getHostName();
                        }
                        if (hostName == null) {
                            hostName = nbt.getHostName();
                        }
                        smbFile = new SmbFile(this.url.replace(server, ip), new NtlmPasswordAuthentication(ip, this.user, this.pwd));
                        if (smbFile.listFiles(Utils.getDefaultSmbFileFilter()) != null) {
                            this.url = this.url.replace(server, hostName);
                            if (smbFile.getType() != 4) {
                                name = hostName;
                            } else {
                                name = smbFile.getName();
                                if (name.endsWith("/")) {
                                    name = name.substring(0, name.length() - 1);
                                }
                            }
                            device = new SambaDevice(this.url, hostName, ip, this.user, this.pwd, smbFile.getType());
                            try {
                                if (this.listInfo.saveSmb(device)) {
                                    what = -11;
                                } else {
                                    what = 0;
                                    SmbDatabaseUtils.save(this.context, device);
                                }
                            } catch (UnknownHostException e4) {
                                e = e4;
                                what = -4;
                                MyLog.w("CheckSmbTask", e);
                                return new Result(what, device, this.dialog);
                            } catch (MalformedURLException e5) {
                                e2 = e5;
                                what = -3;
                                MyLog.w("CheckSmbTask", e2);
                                return new Result(what, device, this.dialog);
                            } catch (SmbException e6) {
                                e3 = e6;
                                what = -2;
                                MyLog.w("CheckSmbTask", e3);
                                return new Result(what, device, this.dialog);
                            }
                            return new Result(what, device, this.dialog);
                        }
                    }
                }
                if (hostName == null) {
                    hostName = nbt.getHostName();
                }
                smbFile = new SmbFile(this.url.replace(server, ip), new NtlmPasswordAuthentication(ip, this.user, this.pwd));
                if (smbFile.listFiles(Utils.getDefaultSmbFileFilter()) != null) {
                    this.url = this.url.replace(server, hostName);
                    if (smbFile.getType() != 4) {
                        name = smbFile.getName();
                        if (name.endsWith("/")) {
                            name = name.substring(0, name.length() - 1);
                        }
                    } else {
                        name = hostName;
                    }
                    device = new SambaDevice(this.url, hostName, ip, this.user, this.pwd, smbFile.getType());
                    if (this.listInfo.saveSmb(device)) {
                        what = 0;
                        SmbDatabaseUtils.save(this.context, device);
                    } else {
                        what = -11;
                    }
                    return new Result(what, device, this.dialog);
                }
            }
            device = null;
        } catch (UnknownHostException e7) {
            e = e7;
            device = null;
            what = -4;
            MyLog.w("CheckSmbTask", e);
            return new Result(what, device, this.dialog);
        } catch (MalformedURLException e8) {
            e2 = e8;
            device = null;
            what = -3;
            MyLog.w("CheckSmbTask", e2);
            return new Result(what, device, this.dialog);
        } catch (SmbException e9) {
            e3 = e9;
            device = null;
            what = -2;
            MyLog.w("CheckSmbTask", e3);
            return new Result(what, device, this.dialog);
        }
        return new Result(what, device, this.dialog);
    }
}
