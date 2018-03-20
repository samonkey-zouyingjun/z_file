package com.zidoo.custom.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import com.umeng.common.a;
import com.zidoo.custom.app.ZidooClassTypeManager.AppClassOnChangeListener;
import com.zidoo.custom.db.ZidooSQliteManger;
import com.zidoo.custom.init.ZidooJarPermissions;
import com.zidoo.custom.log.MyLog;
import com.zidoo.custom.share.ZidooSharedPrefsUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ZidooAppManager {
    private static final int ADD_APP = 0;
    private static final int DELETE_APP = 1;
    private static final int INITAPPOVER = 2;
    private static final String TYPEHINTDATASHARE = "typehintdata";
    public static final String ZIDOOHINTTYPE = "zidoohinttype";
    public final int APK_DELETE_TIME;
    private String[] DAULFATUPNAME;
    private APPManagerListener mAPPManagerListener;
    private AppClassBaseManger mAppClassBaseManger;
    private BroadcastReceiver mAppReceiver;
    private AppTopBaseManger mAppTopBaseManger;
    private Context mContext;
    private Handler mHandler;
    private ArrayList<APPSoftInfo> mInstalledSofts;
    private LinkedHashMap<String, APPSoftInfo> mInstalledSoftsHash;
    private PackageManager mPackageManager;
    private String mReInstallPname;
    private int mTopMax;
    private HashMap<String, ArrayList<APPSoftInfo>> mTypeSoftsHash;
    private ZidooClassTypeManager mZidooClassTypeManager;
    private String[] mtypeArray;

    public interface APPManagerListener {
        String getApptype(String str);

        String[] getTypeDefaultApp(String str);

        void initAppComplete(ArrayList<APPSoftInfo> arrayList, HashMap<String, ArrayList<APPSoftInfo>> hashMap);

        void initAppSort(ArrayList<APPSoftInfo> arrayList);

        void installSuccess(APPSoftInfo aPPSoftInfo, boolean z, boolean z2);

        boolean isHideApp(String str);

        void unInstallSuccess(APPSoftInfo aPPSoftInfo, boolean z);
    }

    public HashMap<String, ArrayList<APPSoftInfo>> getTypeSoftsHash() {
        return this.mTypeSoftsHash;
    }

    public LinkedHashMap<String, APPSoftInfo> getInstalledSoftsHash() {
        return this.mInstalledSoftsHash;
    }

    public ArrayList<APPSoftInfo> getInstalledSofts() {
        return this.mInstalledSofts;
    }

    public ZidooAppManager(Context context, APPManagerListener aPPManagerListener) {
        this.mContext = null;
        this.mPackageManager = null;
        this.mHandler = null;
        this.APK_DELETE_TIME = 350;
        this.mAppReceiver = null;
        this.mReInstallPname = "";
        this.mTypeSoftsHash = new HashMap();
        this.mInstalledSoftsHash = new LinkedHashMap();
        this.mInstalledSofts = new ArrayList();
        this.mAPPManagerListener = null;
        this.mtypeArray = null;
        this.mAppClassBaseManger = null;
        this.mZidooClassTypeManager = null;
        this.mAppTopBaseManger = null;
        this.mTopMax = 5;
        this.DAULFATUPNAME = new String[]{ZidooAppTool.FACTORY_KAIBOER, ZidooAppTool.FACTORY_ZIDOO};
        init(context, aPPManagerListener, this.mTopMax);
    }

    private void init(Context context, APPManagerListener aPPManagerListener, int mTopMax) {
        ZidooJarPermissions.checkZidooPermissions();
        this.mContext = context;
        this.mAPPManagerListener = aPPManagerListener;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mAppClassBaseManger = ZidooSQliteManger.getClassBaseManger(this.mContext);
        this.mAppTopBaseManger = ZidooSQliteManger.getAppTopBaseManger(this.mContext);
        this.mZidooClassTypeManager = new ZidooClassTypeManager(this.mContext, this);
        initData();
        registerApkReceiver();
    }

    public ZidooAppManager(Context context, APPManagerListener aPPManagerListener, int mTopMax) {
        this.mContext = null;
        this.mPackageManager = null;
        this.mHandler = null;
        this.APK_DELETE_TIME = 350;
        this.mAppReceiver = null;
        this.mReInstallPname = "";
        this.mTypeSoftsHash = new HashMap();
        this.mInstalledSoftsHash = new LinkedHashMap();
        this.mInstalledSofts = new ArrayList();
        this.mAPPManagerListener = null;
        this.mtypeArray = null;
        this.mAppClassBaseManger = null;
        this.mZidooClassTypeManager = null;
        this.mAppTopBaseManger = null;
        this.mTopMax = 5;
        this.DAULFATUPNAME = new String[]{ZidooAppTool.FACTORY_KAIBOER, ZidooAppTool.FACTORY_ZIDOO};
        this.mTopMax = mTopMax;
        init(context, aPPManagerListener, mTopMax);
    }

    public void resetTopSoft() {
        int i;
        ArrayList<APPSoftInfo> installedSofts = new ArrayList();
        ArrayList<String> topSofts = this.mAppTopBaseManger.queryAllApp(this.mTopMax);
        int size = topSofts.size();
        for (i = 0; i < size; i++) {
            String pName = (String) topSofts.get(i);
            if (this.mInstalledSoftsHash.containsKey(pName)) {
                APPSoftInfo aPPSoftInfo = (APPSoftInfo) this.mInstalledSoftsHash.get(pName);
                aPPSoftInfo.setTop(true);
                installedSofts.add(aPPSoftInfo);
                this.mInstalledSofts.remove(aPPSoftInfo);
            }
        }
        int allSize = this.mInstalledSofts.size();
        for (i = 0; i < allSize; i++) {
            ((APPSoftInfo) this.mInstalledSofts.get(i)).setTop(false);
        }
        this.mInstalledSofts.addAll(0, installedSofts);
    }

    public boolean setTopSoft(String pName) {
        if (!this.mAppTopBaseManger.insertData(pName) || !this.mInstalledSoftsHash.containsKey(pName)) {
            return false;
        }
        ((APPSoftInfo) this.mInstalledSoftsHash.get(pName)).setTop(true);
        return true;
    }

    public boolean cancelTopSoft(String pName) {
        if (!this.mAppTopBaseManger.delete(pName) || !this.mInstalledSoftsHash.containsKey(pName)) {
            return false;
        }
        ((APPSoftInfo) this.mInstalledSoftsHash.get(pName)).setTop(false);
        return true;
    }

    private boolean isHint(String pName) {
        if (this.mContext.getPackageName().equals(pName)) {
            return true;
        }
        for (String equals : this.DAULFATUPNAME) {
            if (equals.equals(pName)) {
                return true;
            }
        }
        if (this.mAPPManagerListener != null) {
            return this.mAPPManagerListener.isHideApp(pName);
        }
        return false;
    }

    public void release() {
        try {
            this.mContext.unregisterReceiver(this.mAppReceiver);
        } catch (Exception e) {
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

    private void initData() {
        this.mAppReceiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                String action = arg1.getAction();
                String pckName = arg1.getData().getSchemeSpecificPart();
                MyLog.v("mAppReceiver action = " + action + "  pckName = " + pckName);
                if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    int uninstallUpdate = 0;
                    if (ZidooAppManager.this.mReInstallPname.equals(pckName)) {
                        ZidooAppManager.this.mHandler.removeMessages(1);
                        ZidooAppManager.this.mReInstallPname = "";
                        uninstallUpdate = 1;
                    }
                    ZidooAppManager.this.mHandler.sendMessageDelayed(ZidooAppManager.this.mHandler.obtainMessage(0, uninstallUpdate, 0, pckName), 20);
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    ZidooAppManager.this.mReInstallPname = pckName;
                    ZidooAppManager.this.mHandler.sendMessageDelayed(ZidooAppManager.this.mHandler.obtainMessage(1, 0, 0, pckName), 350);
                }
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ZidooAppManager.this.installSueecss(msg.obj, msg.arg1);
                        return;
                    case 1:
                        String removeApkPckName = msg.obj;
                        APPSoftInfo aPPSoftInfo;
                        if (msg.arg1 == 1) {
                            if (!(removeApkPckName == null || removeApkPckName.equals("") || !ZidooAppManager.this.mInstalledSoftsHash.containsKey(removeApkPckName))) {
                                aPPSoftInfo = (APPSoftInfo) ZidooAppManager.this.mInstalledSoftsHash.get(removeApkPckName);
                                if (!(aPPSoftInfo == null || ZidooAppManager.this.mAPPManagerListener == null)) {
                                    ZidooAppManager.this.mAPPManagerListener.unInstallSuccess(aPPSoftInfo, true);
                                }
                            }
                        } else if (!(removeApkPckName == null || removeApkPckName.equals(""))) {
                            aPPSoftInfo = ZidooAppManager.this.removedSoft(removeApkPckName);
                            if (!(aPPSoftInfo == null || ZidooAppManager.this.mAPPManagerListener == null)) {
                                ZidooAppManager.this.mAPPManagerListener.unInstallSuccess(aPPSoftInfo, false);
                            }
                            if (aPPSoftInfo != null) {
                                ZidooAppManager.this.deleteApp(aPPSoftInfo);
                            }
                        }
                        ZidooAppManager.this.mReInstallPname = "";
                        return;
                    case 2:
                        if (ZidooAppManager.this.mAPPManagerListener != null) {
                            ZidooAppManager.this.mAPPManagerListener.initAppComplete(ZidooAppManager.this.mInstalledSofts, ZidooAppManager.this.mTypeSoftsHash);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
    }

    private void installSueecss(String addApkPckName, int uninstallUpdate) {
        if (addApkPckName != null && !addApkPckName.equals("")) {
            boolean[] flag = new boolean[]{false, false};
            APPSoftInfo aPPSoftInfo = addSoft(addApkPckName, flag);
            if (aPPSoftInfo != null && this.mAPPManagerListener != null) {
                if (uninstallUpdate == 1) {
                    boolean z;
                    APPManagerListener aPPManagerListener = this.mAPPManagerListener;
                    if (flag[0] && flag[1]) {
                        z = true;
                    } else {
                        z = false;
                    }
                    boolean z2 = flag[0] && !flag[1];
                    aPPManagerListener.installSuccess(aPPSoftInfo, z, z2);
                    if (flag[0] && !flag[1]) {
                        this.mAPPManagerListener.unInstallSuccess(aPPSoftInfo, true);
                        return;
                    }
                    return;
                }
                this.mAPPManagerListener.installSuccess(aPPSoftInfo, false, false);
            }
        }
    }

    private APPSoftInfo addSoft(String pckName, boolean[] flag) {
        synchronized (this.mInstalledSofts) {
            if (ZidooAppTool.isAppLaunchInstall(this.mContext, pckName)) {
                PackageInfo info;
                APPSoftInfo aPPSoftInfo;
                if (this.mInstalledSoftsHash.containsKey(pckName)) {
                    try {
                        info = this.mPackageManager.getPackageInfo(pckName, 1);
                        aPPSoftInfo = (APPSoftInfo) this.mInstalledSoftsHash.get(pckName);
                        aPPSoftInfo.setLabelName(this.mPackageManager.getApplicationLabel(info.applicationInfo).toString());
                        boolean isUpdate = false;
                        if (aPPSoftInfo.getVersionCode() < info.versionCode || ZidooAppTool.isUpdataVersions(info.versionName, aPPSoftInfo.getVersionName())) {
                            isUpdate = true;
                        }
                        aPPSoftInfo.setVersionName(info.versionName);
                        aPPSoftInfo.setVersionCode(info.versionCode);
                        aPPSoftInfo.setFirstInstallTime(info.firstInstallTime);
                        aPPSoftInfo.setAppIconBitmap(null);
                        flag[0] = true;
                        flag[1] = isUpdate;
                        return aPPSoftInfo;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                } else if (!isHint(pckName)) {
                    try {
                        info = this.mPackageManager.getPackageInfo(pckName, 1);
                        ApplicationInfo applicationInfo = info.applicationInfo;
                        aPPSoftInfo = new APPSoftInfo();
                        aPPSoftInfo.setLabelName(this.mPackageManager.getApplicationLabel(applicationInfo).toString());
                        aPPSoftInfo.setPackageName(pckName);
                        aPPSoftInfo.setVersionName(info.versionName);
                        aPPSoftInfo.setVersionCode(info.versionCode);
                        aPPSoftInfo.setFirstInstallTime(info.firstInstallTime);
                        aPPSoftInfo.setType(ZIDOOHINTTYPE);
                        this.mInstalledSofts.add(0, aPPSoftInfo);
                        this.mInstalledSoftsHash.put(pckName, aPPSoftInfo);
                        if (this.mAPPManagerListener != null) {
                            String type = this.mAPPManagerListener.getApptype(pckName);
                            if (type != null && this.mTypeSoftsHash.containsKey(type)) {
                                ArrayList<APPSoftInfo> appSoftInfolists = (ArrayList) this.mTypeSoftsHash.get(type);
                                int typeSiz = appSoftInfolists.size();
                                boolean isAdd = true;
                                for (int i = 0; i < typeSiz; i++) {
                                    if (((APPSoftInfo) appSoftInfolists.get(i)).getPackageName().equals(aPPSoftInfo.getPackageName())) {
                                        isAdd = false;
                                        break;
                                    }
                                }
                                if (isAdd) {
                                    aPPSoftInfo.setType(type);
                                    appSoftInfolists.add(aPPSoftInfo);
                                    this.mZidooClassTypeManager.addDownApp(type, aPPSoftInfo);
                                }
                            }
                        }
                        return aPPSoftInfo;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return null;
                    }
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.zidoo.custom.app.APPSoftInfo removedSoft(java.lang.String r9) {
        /*
        r8 = this;
        r7 = r8.mInstalledSofts;
        monitor-enter(r7);
        r6 = r8.mAppClassBaseManger;	 Catch:{ all -> 0x0064 }
        r6.deleteApp(r9);	 Catch:{ all -> 0x0064 }
        r6 = r8.mInstalledSoftsHash;	 Catch:{ all -> 0x0064 }
        r6 = r6.containsKey(r9);	 Catch:{ all -> 0x0064 }
        if (r6 == 0) goto L_0x0081;
    L_0x0010:
        r6 = r8.mInstalledSoftsHash;	 Catch:{ all -> 0x0064 }
        r0 = r6.get(r9);	 Catch:{ all -> 0x0064 }
        r0 = (com.zidoo.custom.app.APPSoftInfo) r0;	 Catch:{ all -> 0x0064 }
        r4 = r0.getType();	 Catch:{ all -> 0x0064 }
        r6 = r8.mInstalledSoftsHash;	 Catch:{ all -> 0x0064 }
        r6.remove(r9);	 Catch:{ all -> 0x0064 }
        r6 = r8.mInstalledSofts;	 Catch:{ all -> 0x0064 }
        r3 = r6.size();	 Catch:{ all -> 0x0064 }
        r2 = 0;
    L_0x0028:
        if (r2 < r3) goto L_0x004c;
    L_0x002a:
        r6 = "zidoohinttype";
        r6 = r4.equals(r6);	 Catch:{ all -> 0x0064 }
        if (r6 != 0) goto L_0x004a;
    L_0x0033:
        r6 = r8.mTypeSoftsHash;	 Catch:{ all -> 0x0064 }
        r6 = r6.containsKey(r4);	 Catch:{ all -> 0x0064 }
        if (r6 == 0) goto L_0x004a;
    L_0x003b:
        r6 = r8.mTypeSoftsHash;	 Catch:{ all -> 0x0064 }
        r1 = r6.get(r4);	 Catch:{ all -> 0x0064 }
        r1 = (java.util.ArrayList) r1;	 Catch:{ all -> 0x0064 }
        r5 = r1.size();	 Catch:{ all -> 0x0064 }
        r2 = 0;
    L_0x0048:
        if (r2 < r5) goto L_0x006a;
    L_0x004a:
        monitor-exit(r7);	 Catch:{ all -> 0x0064 }
    L_0x004b:
        return r0;
    L_0x004c:
        r6 = r8.mInstalledSofts;	 Catch:{ all -> 0x0064 }
        r6 = r6.get(r2);	 Catch:{ all -> 0x0064 }
        r6 = (com.zidoo.custom.app.APPSoftInfo) r6;	 Catch:{ all -> 0x0064 }
        r6 = r6.getPackageName();	 Catch:{ all -> 0x0064 }
        r6 = r6.equals(r9);	 Catch:{ all -> 0x0064 }
        if (r6 == 0) goto L_0x0067;
    L_0x005e:
        r6 = r8.mInstalledSofts;	 Catch:{ all -> 0x0064 }
        r6.remove(r2);	 Catch:{ all -> 0x0064 }
        goto L_0x002a;
    L_0x0064:
        r6 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x0064 }
        throw r6;
    L_0x0067:
        r2 = r2 + 1;
        goto L_0x0028;
    L_0x006a:
        r6 = r1.get(r2);	 Catch:{ all -> 0x0064 }
        r6 = (com.zidoo.custom.app.APPSoftInfo) r6;	 Catch:{ all -> 0x0064 }
        r6 = r6.getPackageName();	 Catch:{ all -> 0x0064 }
        r6 = r6.equals(r9);	 Catch:{ all -> 0x0064 }
        if (r6 == 0) goto L_0x007e;
    L_0x007a:
        r1.remove(r2);	 Catch:{ all -> 0x0064 }
        goto L_0x004a;
    L_0x007e:
        r2 = r2 + 1;
        goto L_0x0048;
    L_0x0081:
        monitor-exit(r7);	 Catch:{ all -> 0x0064 }
        r0 = 0;
        goto L_0x004b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.app.ZidooAppManager.removedSoft(java.lang.String):com.zidoo.custom.app.APPSoftInfo");
    }

    public synchronized void startScanAPP(String[] typeArray) {
        this.mtypeArray = typeArray;
        new Thread(new Runnable() {
            public void run() {
                int i;
                ZidooAppManager.this.mInstalledSoftsHash.clear();
                ZidooAppManager.this.mInstalledSofts.clear();
                ZidooAppManager.this.mTypeSoftsHash.clear();
                ArrayList<APPSoftInfo> dblist = ZidooAppManager.this.mAppClassBaseManger.queryAllApp();
                int allSize = dblist.size();
                for (i = 0; i < allSize; i++) {
                    APPSoftInfo aPPSoftInfo_s = (APPSoftInfo) dblist.get(i);
                    if (!ZidooAppTool.isAppLaunchInstall(ZidooAppManager.this.mContext, aPPSoftInfo_s.getPackageName())) {
                        ZidooAppManager.this.mAppClassBaseManger.deleteApp(aPPSoftInfo_s.getPackageName());
                    } else if (!ZidooAppManager.this.isHint(aPPSoftInfo_s.getPackageName())) {
                        try {
                            PackageInfo info = ZidooAppManager.this.mPackageManager.getPackageInfo(aPPSoftInfo_s.getPackageName(), 1);
                            ApplicationInfo applicationInfo = info.applicationInfo;
                            APPSoftInfo aPPSoftInfo = new APPSoftInfo();
                            aPPSoftInfo.setLabelName(ZidooAppManager.this.mPackageManager.getApplicationLabel(applicationInfo).toString());
                            aPPSoftInfo.setPackageName(aPPSoftInfo_s.getPackageName());
                            aPPSoftInfo.setVersionName(info.versionName);
                            aPPSoftInfo.setVersionCode(info.versionCode);
                            aPPSoftInfo.setFirstInstallTime(info.firstInstallTime);
                            aPPSoftInfo.setClickCount(aPPSoftInfo_s.getClickCount());
                            aPPSoftInfo.setType(ZidooAppManager.ZIDOOHINTTYPE);
                            ZidooAppManager.this.mInstalledSofts.add(aPPSoftInfo);
                            ZidooAppManager.this.mInstalledSoftsHash.put(aPPSoftInfo_s.getPackageName(), aPPSoftInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (ZidooAppManager.this.mtypeArray != null) {
                    int length;
                    int j;
                    int typeSize;
                    if (ZidooSharedPrefsUtil.getValue(ZidooAppManager.this.mContext, ZidooAppManager.TYPEHINTDATASHARE, true)) {
                        length = ZidooAppManager.this.mtypeArray.length;
                        for (i = 0; i < length; i++) {
                            if (ZidooAppManager.this.mAPPManagerListener != null) {
                                String[] typeData = ZidooAppManager.this.mAPPManagerListener.getTypeDefaultApp(ZidooAppManager.this.mtypeArray[i]);
                                if (typeData != null) {
                                    for (String addTypeApp : typeData) {
                                        ZidooAppManager.this.mAppClassBaseManger.addTypeApp(addTypeApp, ZidooAppManager.this.mtypeArray[i]);
                                    }
                                }
                            }
                        }
                        ZidooSharedPrefsUtil.putValue(ZidooAppManager.this.mContext, ZidooAppManager.TYPEHINTDATASHARE, false);
                    }
                    length = ZidooAppManager.this.mtypeArray.length;
                    for (i = 0; i < length; i++) {
                        ArrayList<APPSoftInfo> calss_list = new ArrayList();
                        ArrayList<APPSoftInfo> typelist = ZidooAppManager.this.mAppClassBaseManger.queryAppByType(ZidooAppManager.this.mtypeArray[i]);
                        typeSize = typelist.size();
                        for (j = 0; j < typeSize; j++) {
                            String pName = ((APPSoftInfo) typelist.get(j)).getPackageName();
                            if (ZidooAppManager.this.mInstalledSoftsHash.containsKey(pName)) {
                                aPPSoftInfo = (APPSoftInfo) ZidooAppManager.this.mInstalledSoftsHash.get(pName);
                                aPPSoftInfo.setType(ZidooAppManager.this.mtypeArray[i]);
                                calss_list.add(aPPSoftInfo);
                            } else if (ZidooAppTool.isAppLaunchInstall(ZidooAppManager.this.mContext, pName) && !ZidooAppManager.this.isHint(pName)) {
                                try {
                                    info = ZidooAppManager.this.mPackageManager.getPackageInfo(pName, 1);
                                    applicationInfo = info.applicationInfo;
                                    aPPSoftInfo = new APPSoftInfo();
                                    aPPSoftInfo.setLabelName(ZidooAppManager.this.mPackageManager.getApplicationLabel(applicationInfo).toString());
                                    aPPSoftInfo.setPackageName(pName);
                                    aPPSoftInfo.setVersionName(info.versionName);
                                    aPPSoftInfo.setVersionCode(info.versionCode);
                                    aPPSoftInfo.setFirstInstallTime(info.firstInstallTime);
                                    aPPSoftInfo.setType(ZidooAppManager.this.mtypeArray[i]);
                                    ZidooAppManager.this.mInstalledSofts.add(aPPSoftInfo);
                                    ZidooAppManager.this.mInstalledSoftsHash.put(pName, aPPSoftInfo);
                                    calss_list.add(aPPSoftInfo);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                        ZidooAppManager.this.mTypeSoftsHash.put(ZidooAppManager.this.mtypeArray[i], calss_list);
                    }
                }
                List<PackageInfo> packages = ZidooAppManager.this.mPackageManager.getInstalledPackages(0);
                int size = packages.size();
                for (i = 0; i < size; i++) {
                    PackageInfo packInfo = (PackageInfo) packages.get(i);
                    if (!(!ZidooAppTool.isAppLaunchInstall(ZidooAppManager.this.mContext, packInfo.packageName) || ZidooAppManager.this.mInstalledSoftsHash.containsKey(packInfo.packageName) || ZidooAppManager.this.isHint(packInfo.packageName))) {
                        try {
                            info = ZidooAppManager.this.mPackageManager.getPackageInfo(packInfo.packageName, 1);
                            applicationInfo = info.applicationInfo;
                            aPPSoftInfo = new APPSoftInfo();
                            aPPSoftInfo.setLabelName(ZidooAppManager.this.mPackageManager.getApplicationLabel(applicationInfo).toString());
                            aPPSoftInfo.setPackageName(packInfo.packageName);
                            aPPSoftInfo.setVersionName(info.versionName);
                            aPPSoftInfo.setVersionCode(info.versionCode);
                            aPPSoftInfo.setFirstInstallTime(info.firstInstallTime);
                            aPPSoftInfo.setType(ZidooAppManager.ZIDOOHINTTYPE);
                            ZidooAppManager.this.mInstalledSofts.add(aPPSoftInfo);
                            ZidooAppManager.this.mInstalledSoftsHash.put(packInfo.packageName, aPPSoftInfo);
                        } catch (Exception e22) {
                            e22.printStackTrace();
                        }
                    }
                }
                if (ZidooAppManager.this.mAPPManagerListener != null) {
                    ZidooAppManager.this.mAPPManagerListener.initAppSort(ZidooAppManager.this.mInstalledSofts);
                }
                ZidooAppManager.this.mHandler.sendEmptyMessage(2);
            }
        }).start();
    }

    public void addTypeApp(String pName, String type) {
        try {
            this.mAppClassBaseManger.addTypeApp(pName, type);
            if (this.mInstalledSoftsHash.containsKey(pName)) {
                APPSoftInfo aPPSoftInfo = (APPSoftInfo) this.mInstalledSoftsHash.get(pName);
                if (!aPPSoftInfo.getType().equals(type)) {
                    aPPSoftInfo.setType(type);
                    if (this.mTypeSoftsHash.containsKey(type)) {
                        ((ArrayList) this.mTypeSoftsHash.get(type)).add(aPPSoftInfo);
                        return;
                    }
                    ArrayList<APPSoftInfo> appSoftInfos = new ArrayList();
                    appSoftInfos.add(aPPSoftInfo);
                    this.mTypeSoftsHash.put(type, appSoftInfos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<APPSoftInfo> getTypeApp(String type) {
        if (type == null || type.trim().equals("") || !this.mTypeSoftsHash.containsKey(type)) {
            return null;
        }
        return (ArrayList) this.mTypeSoftsHash.get(type);
    }

    public void initTypeWebData(String type, ArrayList<APPSoftInfo> webApArrayList) {
        if (webApArrayList != null) {
            int size = webApArrayList.size();
            for (int i = 0; i < size; i++) {
                if (ZidooSharedPrefsUtil.getValue(this.mContext, ((APPSoftInfo) webApArrayList.get(i)).getPackageName(), true)) {
                    ((APPSoftInfo) webApArrayList.get(i)).setType(type);
                } else {
                    ((APPSoftInfo) webApArrayList.get(i)).setType(ZIDOOHINTTYPE);
                }
            }
        }
    }

    public ArrayList<APPSoftInfo> getTypeWebApp(String type, ArrayList<APPSoftInfo> webApArrayList) {
        if (type == null || type.trim().equals("")) {
            return null;
        }
        ArrayList<APPSoftInfo> aPPSoftInfoList = new ArrayList();
        ArrayList<APPSoftInfo> webAPPSoftInfoList = new ArrayList();
        if (this.mTypeSoftsHash.containsKey(type)) {
            aPPSoftInfoList.addAll((Collection) this.mTypeSoftsHash.get(type));
        }
        if (webApArrayList != null) {
            int size = webApArrayList.size();
            for (int i = 0; i < size; i++) {
                APPSoftInfo webAPPSoftInfo = (APPSoftInfo) webApArrayList.get(i);
                if (webAPPSoftInfo.getType().equals(type) && !ZidooAppTool.isInstall(this.mContext, webAPPSoftInfo.getPackageName())) {
                    webAPPSoftInfoList.add(webAPPSoftInfo);
                }
            }
        }
        aPPSoftInfoList.addAll(webAPPSoftInfoList);
        return aPPSoftInfoList;
    }

    public void registerOnChangeListener(String type, AppClassOnChangeListener appClassOnChangeListener) {
        this.mZidooClassTypeManager.registerOnChangeListener(type, appClassOnChangeListener);
    }

    public void unRegisterOnChangeListener(String type) {
        this.mZidooClassTypeManager.unRegisterOnChangeListener(type);
    }

    public ArrayList<APPSoftInfo> getTypeList(String type, String currentPackageName) {
        return this.mZidooClassTypeManager.getTypeList(type, currentPackageName);
    }

    public ArrayList<APPSoftInfo> getTypeList(String type) {
        return this.mZidooClassTypeManager.getTypeList(type);
    }

    public ArrayList<APPSoftInfo> getTypeWebList(String type, ArrayList<APPSoftInfo> webApArrayList) {
        return this.mZidooClassTypeManager.getTypeWebList(type, webApArrayList);
    }

    public void setTypeChange(int currentIndex) {
        this.mZidooClassTypeManager.setTypeChange(currentIndex);
    }

    public boolean setTypeIndexChange(int currentIndex) {
        return this.mZidooClassTypeManager.setTypeIndexChange(currentIndex);
    }

    public void deleteClassApp(String type, APPSoftInfo aPPSoftInfo) {
        this.mZidooClassTypeManager.deleteClassApp(type, aPPSoftInfo);
    }

    private void deleteApp(APPSoftInfo aPPSoftInfo) {
        this.mZidooClassTypeManager.deleteApp(aPPSoftInfo);
    }
}
