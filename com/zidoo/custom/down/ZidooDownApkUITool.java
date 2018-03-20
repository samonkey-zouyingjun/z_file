package com.zidoo.custom.down;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.umeng.common.a;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.app.ZidooStartAppInfo;
import com.zidoo.custom.db.ZidooSQliteManger;
import com.zidoo.custom.file.ZidooFileSizeFile;
import com.zidoo.custom.init.ZidooJarPermissions;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ZidooDownApkUITool {
    private static final int UPDATADATA = 0;
    private Context mContext = null;
    private DownApkStatusListener mDownApkStatusListener = null;
    private BroadcastReceiver mDownOverOKReceier = null;
    private ZidooDownApkBaseManger mDowndb = null;
    private Handler mHandler = null;
    private int mStatus = 0;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask = null;
    private ZidooDownApkInfo mZidooDownApkInfo = null;

    public interface DownApkStatusListener {
        void changStatus(int i);

        void status(int i, int i2, String str, String str2, String str3);
    }

    public ZidooDownApkUITool(Context mContext, DownApkStatusListener downApkStatusListener) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = mContext;
        this.mDownApkStatusListener = downApkStatusListener;
        this.mDowndb = ZidooSQliteManger.getDownApkBaseManger(mContext);
        init();
    }

    private void init() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Object[] object = msg.obj;
                        ZidooDownApkUITool.this.mStatus = ((Integer) object[0]).intValue();
                        if (((Integer) object[0]).intValue() != 1) {
                            ZidooDownApkUITool.this.canleRefreshUI();
                        }
                        if (ZidooDownApkUITool.this.mDownApkStatusListener != null) {
                            ZidooDownApkUITool.this.mDownApkStatusListener.status(((Integer) object[0]).intValue(), ((Integer) object[1]).intValue(), ZidooFileSizeFile.toSize((long) ((Integer) object[2]).intValue()), (String) object[3], (String) object[4]);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.mDownOverOKReceier = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String pName = intent.getStringExtra("pName");
                System.out.println("bob  ui  pName = " + pName + " mZidooDownApkInfo=" + ZidooDownApkUITool.this.mZidooDownApkInfo);
                if (pName != null && ZidooDownApkUITool.this.mZidooDownApkInfo != null && pName.equals(ZidooDownApkUITool.this.mZidooDownApkInfo.getpName())) {
                    int type = intent.getIntExtra(a.b, -1);
                    if (type == 0) {
                        ZidooDownApkUITool.this.mStatus = 4;
                        if (ZidooDownApkUITool.this.mDownApkStatusListener != null) {
                            ZidooDownApkUITool.this.mDownApkStatusListener.status(ZidooDownApkUITool.this.mStatus, 0, "", "", "");
                        }
                    } else if (type == 1) {
                        ZidooDownApkUITool.this.mStatus = 8;
                        if (ZidooDownApkUITool.this.mDownApkStatusListener != null) {
                            ZidooDownApkUITool.this.mDownApkStatusListener.status(ZidooDownApkUITool.this.mStatus, 0, "", "", "");
                        }
                    }
                }
            }
        };
        infiteReceier();
    }

    private void infiteReceier() {
        IntentFilter infilter = new IntentFilter();
        infilter.addAction(ZidooDownConstants.downOverAction);
        this.mContext.registerReceiver(this.mDownOverOKReceier, infilter);
    }

    public void start(ZidooDownApkInfo zidooDownApkInfo) {
        if (zidooDownApkInfo == null) {
            throw new RuntimeException("zidoo ZidooDownApkInfo null");
        }
        this.mZidooDownApkInfo = zidooDownApkInfo;
        initData();
    }

    private void initData() {
        int status;
        if (ZidooAppTool.isInstall(this.mContext, this.mZidooDownApkInfo.getpName())) {
            int code = -1;
            try {
                code = Integer.valueOf(this.mZidooDownApkInfo.getCode()).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ZidooAppTool.isAppUpdata(this.mContext, this.mZidooDownApkInfo.getpName(), this.mZidooDownApkInfo.getVersion(), code)) {
                this.mStatus = 5;
                status = this.mDowndb.queryDownStatus(this.mZidooDownApkInfo.getpName());
                if (status != 0) {
                    this.mStatus = status;
                } else {
                    this.mDowndb.deleteDownRecord(this.mZidooDownApkInfo.getpName(), true);
                }
            } else {
                this.mStatus = 4;
                this.mDowndb.deleteDownRecord(this.mZidooDownApkInfo.getpName(), true);
            }
        } else {
            status = this.mDowndb.queryDownStatus(this.mZidooDownApkInfo.getpName());
            this.mStatus = status;
            if (status == 0) {
                this.mDowndb.deleteDownRecord(this.mZidooDownApkInfo.getpName(), true);
            }
        }
        canleRefreshUI();
        initDownStatu();
    }

    private void initDownStatu() {
        switch (this.mStatus) {
            case 1:
                startRefreshUI();
                return;
            case 2:
                getData();
                return;
            case 6:
                getData();
                return;
            default:
                if (this.mDownApkStatusListener != null) {
                    this.mDownApkStatusListener.status(this.mStatus, 0, "", "", "");
                    return;
                }
                return;
        }
    }

    private void startRefreshUI() {
        canleRefreshUI();
        this.mTimerTask = new TimerTask() {
            public void run() {
                ZidooDownApkUITool.this.getData();
            }
        };
        this.mTimer.schedule(this.mTimerTask, 1, 1000);
    }

    private void getData() {
        try {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(0, this.mDowndb.getDownloadData(this.mZidooDownApkInfo.getpName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void canleRefreshUI() {
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
    }

    public void release() {
        try {
            canleRefreshUI();
            this.mContext.unregisterReceiver(this.mDownOverOKReceier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changStatus() {
        switch (this.mStatus) {
            case 0:
                reDownAPK(true);
                return;
            case 1:
                canleRefreshUI();
                this.mStatus = 2;
                this.mDowndb.updataDownFlag(this.mZidooDownApkInfo.getpName(), this.mStatus);
                getData();
                return;
            case 2:
                reDownAPK(false);
                return;
            case 3:
                String filepath = this.mDowndb.queryFilepath(this.mZidooDownApkInfo.getpName());
                if (filepath == null || filepath.equals("")) {
                    reDownAPK(true);
                    return;
                }
                File file = new File(filepath);
                if (!file.exists()) {
                    reDownAPK(true);
                    return;
                } else if (ZidooDownApkService.installFile(file, this.mContext, this.mZidooDownApkInfo.getpName())) {
                    this.mStatus = 7;
                    if (this.mDownApkStatusListener != null) {
                        this.mDownApkStatusListener.status(this.mStatus, 0, "", "", "");
                        return;
                    }
                    return;
                } else {
                    return;
                }
            case 4:
                ZidooAppTool.openApp(this.mContext, new ZidooStartAppInfo(this.mZidooDownApkInfo.getpName()));
                return;
            case 5:
                reDownAPK(true);
                return;
            case 6:
                reDownAPK(true);
                return;
            case 7:
                if (this.mDownApkStatusListener != null) {
                    this.mDownApkStatusListener.changStatus(this.mStatus);
                    return;
                }
                return;
            case 8:
                reDownAPK(true);
                return;
            default:
                return;
        }
    }

    private void reDownAPK(boolean isDeleteData) {
        if (isDeleteData) {
            this.mDowndb.deleteDownRecord(this.mZidooDownApkInfo.getpName(), false);
            this.mDowndb.insertdata(this.mZidooDownApkInfo);
        }
        this.mStatus = 1;
        this.mDowndb.updataDownFlag(this.mZidooDownApkInfo.getpName(), this.mStatus);
        Intent intent_server = new Intent(this.mContext, ZidooDownApkService.class);
        intent_server.putExtra("ZidooDownApkInfo", this.mZidooDownApkInfo);
        intent_server.putExtra("cmd", 0);
        this.mContext.startService(intent_server);
        startRefreshUI();
    }
}
