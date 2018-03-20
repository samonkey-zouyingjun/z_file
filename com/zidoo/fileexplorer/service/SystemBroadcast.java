package com.zidoo.fileexplorer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.zidoo.fileexplorer.bean.MountHistory;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.db.MountHistoryDatabase.Helper;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import pers.lic.tool.Toolc;
import zidoo.device.DeviceUtils;
import zidoo.model.BoxModel;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public class SystemBroadcast extends BroadcastReceiver {

    private class MountThread extends Thread {
        private Context context;

        public MountThread(Context context) {
            this.context = context;
        }

        public void run() {
            int n;
            boolean netConnect = false;
            int n2 = 0;
            while (!netConnect) {
                n = n2 + 1;
                if (n2 >= 10) {
                    break;
                }
                netConnect = Toolc.isNetConnected(this.context);
                try {
                    Thread.sleep(7000);
                    n2 = n;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    n2 = n;
                }
            }
            n = n2;
            if (netConnect) {
                Helper helper = MountHistoryDatabase.helper(this.context);
                ArrayList<MountHistory> histories = helper.queryAll();
                ArrayList<MountHistory> invalidOrOutTimeHistories = new ArrayList(histories.size());
                BoxModel model = BoxModel.getModel(this.context, BoxModel.getBoxModel(this.context));
                long currentTime = System.currentTimeMillis();
                Iterator it = histories.iterator();
                while (it.hasNext()) {
                    MountHistory history = (MountHistory) it.next();
                    if (currentTime - history.getMountTime() > 259200000) {
                        invalidOrOutTimeHistories.add(history);
                    } else {
                        boolean mount;
                        String url = history.getUrl();
                        String uri = url.substring(6);
                        int i = uri.indexOf(47);
                        String ip = uri.substring(0, i);
                        String share = uri.substring(i + 1);
                        if (url.startsWith("smb://")) {
                            mount = model.mountSmb("//" + ip + "/" + share, model.getSmbRoot() + "/" + ip + "#" + ZidooFileUtils.encodeCommand(share), ip, history.getUser(), history.getPassword());
                        } else {
                            mount = model.mountNfs(ip, history.getSharePath(), ip + "#" + ZidooFileUtils.encodeCommand(share));
                        }
                        if (!mount) {
                            invalidOrOutTimeHistories.add(history);
                        }
                    }
                }
                if (invalidOrOutTimeHistories.size() > 0) {
                    helper.deleteByHistories(invalidOrOutTimeHistories);
                }
            }
        }
    }

    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AppConstant.sSystemBootTime = System.currentTimeMillis();
            SharedPreferences preferences = context.getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
            new MountThread(context.getApplicationContext()).start();
            if (BoxModel.getBoxModel(context) == 1) {
                final String ip = preferences.getString(AppConstant.PREFEREANCES_MOUNT, null);
                if (ip != null) {
                    final SambaManager manager = SambaManager.MSTAR_MANAGER(context);
                    new Thread(new Runnable() {
                        public void run() {
                            SambaDevice device = SmbDatabaseUtils.query(context, ip);
                            if (device != null) {
                                try {
                                    SmbFile[] smbFiles = SambaManager.openDevice(device);
                                    int count = smbFiles == null ? 0 : smbFiles.length;
                                    if (count > 0) {
                                        for (int i = 0; i < count; i++) {
                                            MyLog.d("system mount smb - " + manager.mountSmb(smbFiles[i], device));
                                        }
                                    }
                                } catch (SmbException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e2) {
                                    e2.printStackTrace();
                                } catch (UnknownHostException e3) {
                                    e3.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            }
            DeviceUtils.saveUsbDeviceAsFile(context, new ArrayList(), DeviceUtils.getDefaultDeviceConfigFile(context));
        }
    }
}
