package com.zidoo.custom.down;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import com.umeng.common.a;
import com.zidoo.custom.app.ZidooAppTool;
import com.zidoo.custom.db.ZidooSQliteManger;
import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZidooDownApkService extends Service {
    public static final int DOWNAPKFLAG = 0;
    public static String DOWNAPKPAHT = "";
    public static final int INSTALLAPK = 2;
    public static final int SETDOWNMAXTHREAD = 1;
    public static boolean isDefaultInstall = true;
    public static boolean isDeleteApkFile = true;
    private static final int mThreadMax = 3;
    private BroadcastReceiver mAppReceiver = null;
    private Context mContext = null;
    private ZidooDownApkBaseManger mDowndb = null;
    private ExecutorService mDownloadThreadPool = null;
    private ExecutorService mSetDownloadThreadPool = null;
    private Timer mTimer = new Timer();
    private HashMap<String, TimerTask> mTimerTastMap = new HashMap();

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.mContext = this;
        initData();
        registerApkReceiver();
        super.onCreate();
    }

    private void initData() {
        this.mDowndb = ZidooSQliteManger.getDownApkBaseManger(this);
        new Thread(new Runnable() {
            public void run() {
                try {
                    ZidooDownApkService.this.mDowndb.queryData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        this.mDownloadThreadPool = Executors.newFixedThreadPool(3);
        this.mAppReceiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                try {
                    String action = arg1.getAction();
                    String pName = arg1.getData().getSchemeSpecificPart();
                    if ("android.intent.action.PACKAGE_ADDED".equals(action) && pName != null) {
                        int status = ZidooDownApkService.this.mDowndb.queryDownStatus(pName);
                        System.out.println("bob  service  pName = " + pName + "   status = " + status);
                        if (status != 0) {
                            Intent intent_faile = new Intent(ZidooDownConstants.downOverAction);
                            intent_faile.putExtra("pName", pName);
                            intent_faile.putExtra(a.b, 0);
                            ZidooDownApkService.this.sendBroadcast(intent_faile);
                            ZidooDownApkService.this.mDowndb.deleteDownRecord(pName, true);
                            if (ZidooDownApkService.this.mTimerTastMap.containsKey(pName)) {
                                TimerTask pTimerTask = (TimerTask) ZidooDownApkService.this.mTimerTastMap.get(pName);
                                ZidooDownApkService.this.mTimerTastMap.remove(pName);
                                if (pTimerTask != null) {
                                    pTimerTask.cancel();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void startDownApk(ZidooDownApkInfo zidooDownApkInfo) {
        startDownload(this.mContext, zidooDownApkInfo);
    }

    private void startDownload(Context context, ZidooDownApkInfo zidooDownApkInfo) {
        try {
            ZidooDownApkTast download = new ZidooDownApkTast(context, zidooDownApkInfo);
            if (this.mSetDownloadThreadPool != null) {
                this.mDownloadThreadPool = this.mSetDownloadThreadPool;
                this.mSetDownloadThreadPool = null;
            }
            this.mDownloadThreadPool.execute(download);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerApkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme(a.c);
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        this.mContext.registerReceiver(this.mAppReceiver, filter);
    }

    public void onDestroy() {
        try {
            unregisterReceiver(this.mAppReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(android.content.Intent r8, int r9, int r10) {
        /*
        r7 = this;
        if (r8 == 0) goto L_0x000d;
    L_0x0002:
        r5 = "cmd";
        r6 = -1;
        r0 = r8.getIntExtra(r5, r6);	 Catch:{ Exception -> 0x0021 }
        switch(r0) {
            case 0: goto L_0x0012;
            case 1: goto L_0x0026;
            case 2: goto L_0x0035;
            default: goto L_0x000d;
        };
    L_0x000d:
        r5 = super.onStartCommand(r8, r9, r10);
        return r5;
    L_0x0012:
        r5 = "ZidooDownApkInfo";
        r4 = r8.getSerializableExtra(r5);	 Catch:{ Exception -> 0x0021 }
        r4 = (com.zidoo.custom.down.ZidooDownApkInfo) r4;	 Catch:{ Exception -> 0x0021 }
        if (r4 == 0) goto L_0x000d;
    L_0x001d:
        r7.startDownApk(r4);	 Catch:{ Exception -> 0x0021 }
        goto L_0x000d;
    L_0x0021:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x000d;
    L_0x0026:
        r5 = "threaMax";
        r6 = 3;
        r3 = r8.getIntExtra(r5, r6);	 Catch:{ Exception -> 0x0021 }
        r5 = java.util.concurrent.Executors.newFixedThreadPool(r3);	 Catch:{ Exception -> 0x0021 }
        r7.mSetDownloadThreadPool = r5;	 Catch:{ Exception -> 0x0021 }
        goto L_0x000d;
    L_0x0035:
        r5 = "pName";
        r2 = r8.getStringExtra(r5);	 Catch:{ Exception -> 0x0021 }
        if (r2 == 0) goto L_0x000d;
    L_0x003e:
        r7.isInstallSuccess(r2);	 Catch:{ Exception -> 0x0021 }
        goto L_0x000d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.down.ZidooDownApkService.onStartCommand(android.content.Intent, int, int):int");
    }

    private void isInstallSuccess(final String pName) {
        try {
            TimerTask pTimerTask = new TimerTask() {
                public void run() {
                    try {
                        if (ZidooDownApkService.this.mDowndb.queryDownStatus(pName) == 7) {
                            ZidooDownApkService.this.mDowndb.updataDownFlag(pName, 8);
                            Intent intent_faile = new Intent(ZidooDownConstants.downOverAction);
                            intent_faile.putExtra("pName", pName);
                            intent_faile.putExtra(a.b, 1);
                            ZidooDownApkService.this.sendBroadcast(intent_faile);
                        }
                        if (ZidooDownApkService.this.mTimerTastMap.containsKey(pName)) {
                            ZidooDownApkService.this.mTimerTastMap.remove(pName);
                        }
                    } catch (Exception e) {
                    }
                }
            };
            if (this.mTimerTastMap.containsKey(pName)) {
                TimerTask pTimerTask_n = (TimerTask) this.mTimerTastMap.get(pName);
                this.mTimerTastMap.remove(pName);
                if (pTimerTask_n != null) {
                    pTimerTask_n.cancel();
                }
            }
            this.mTimerTastMap.put(pName, pTimerTask);
            this.mTimer.schedule(pTimerTask, 120000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean installFile(File file, Context context, String pName) {
        try {
            if (!file.exists()) {
                return false;
            }
            ZidooDownApkBaseManger downdb = ZidooSQliteManger.getDownApkBaseManger(context);
            if (!isDefaultInstall) {
                if (pName != null) {
                    downdb.updataDownFlag(pName, 3);
                }
                lastInstallApk(file, context);
                return false;
            } else if (ZidooAppTool.hitInstallApk(file, context)) {
                downdb.updataDownFlag(pName, 7);
                Intent intent_server = new Intent(context, ZidooDownApkService.class);
                intent_server.putExtra("pName", pName);
                intent_server.putExtra("cmd", 2);
                context.startService(intent_server);
                return true;
            } else {
                downdb.updataDownFlag(pName, 3);
                lastInstallApk(file, context);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void lastInstallApk(File file, Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(268435456);
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
