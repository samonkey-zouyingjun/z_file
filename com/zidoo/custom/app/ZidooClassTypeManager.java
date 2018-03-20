package com.zidoo.custom.app;

import android.content.Context;
import com.zidoo.custom.db.ZidooSQliteManger;
import com.zidoo.custom.share.ZidooSharedPrefsUtil;
import java.util.ArrayList;
import java.util.HashMap;

public class ZidooClassTypeManager {
    private ArrayList<APPSoftInfo> mAPPSoftInfoList = new ArrayList();
    private HashMap<String, AppClassOnChangeListener> mApkClassOnChangeListenerMap = new HashMap();
    private Context mContext = null;
    private String mPName = null;
    private String mType = "";
    private ZidooAppManager mZidooAppManager = null;

    public interface AppClassOnChangeListener {
        void addSoft(String str, APPSoftInfo aPPSoftInfo);

        void changeClass(String str, APPSoftInfo aPPSoftInfo, String str2);

        void changePosition(String str, APPSoftInfo aPPSoftInfo, String str2);

        void deleteSoft(String str, String str2);
    }

    public ZidooClassTypeManager(Context mContext, ZidooAppManager mZidooAppManager) {
        this.mContext = mContext;
        this.mZidooAppManager = mZidooAppManager;
    }

    public void deleteApp(APPSoftInfo aPPSoftInfo) {
        String type = aPPSoftInfo.getType();
        if (!type.equals(ZidooAppManager.ZIDOOHINTTYPE) && this.mApkClassOnChangeListenerMap.containsKey(type)) {
            ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(type)).deleteSoft(type, aPPSoftInfo.getPackageName());
        }
    }

    public void registerOnChangeListener(String type, AppClassOnChangeListener appClassOnChangeListener) {
        this.mApkClassOnChangeListenerMap.put(type, appClassOnChangeListener);
    }

    public void unRegisterOnChangeListener(String type) {
        this.mApkClassOnChangeListenerMap.remove(type);
    }

    public ArrayList<APPSoftInfo> getTypeList(String type) {
        this.mAPPSoftInfoList.clear();
        this.mPName = null;
        this.mType = type;
        this.mAPPSoftInfoList.addAll(this.mZidooAppManager.getInstalledSofts());
        return this.mAPPSoftInfoList;
    }

    public ArrayList<APPSoftInfo> getTypeWebList(String type, ArrayList<APPSoftInfo> webApArrayList) {
        this.mAPPSoftInfoList.clear();
        this.mPName = null;
        this.mType = type;
        if (webApArrayList != null) {
            int size = webApArrayList.size();
            for (int i = 0; i < size; i++) {
                APPSoftInfo webAPPSoftInfo = (APPSoftInfo) webApArrayList.get(i);
                if (!ZidooAppTool.isInstall(this.mContext, webAPPSoftInfo.getPackageName())) {
                    this.mAPPSoftInfoList.add(webAPPSoftInfo);
                }
            }
        }
        this.mAPPSoftInfoList.addAll(this.mZidooAppManager.getInstalledSofts());
        return this.mAPPSoftInfoList;
    }

    public ArrayList<APPSoftInfo> getTypeList(String type, String currentPackageName) {
        this.mAPPSoftInfoList.clear();
        this.mPName = currentPackageName;
        this.mType = type;
        int size;
        int i;
        APPSoftInfo aPPSoftInfo;
        if (this.mPName != null) {
            size = this.mZidooAppManager.getInstalledSofts().size();
            ArrayList<APPSoftInfo> pAPPSoftInfoList = new ArrayList();
            ArrayList<APPSoftInfo> hAPPSoftInfoList = new ArrayList();
            for (i = 0; i < size; i++) {
                aPPSoftInfo = (APPSoftInfo) this.mZidooAppManager.getInstalledSofts().get(i);
                if (aPPSoftInfo.getPackageName().equals(this.mPName)) {
                    this.mAPPSoftInfoList.add(aPPSoftInfo);
                } else if (aPPSoftInfo.getType().equals(this.mType)) {
                    pAPPSoftInfoList.add(aPPSoftInfo);
                } else {
                    hAPPSoftInfoList.add(aPPSoftInfo);
                }
            }
            this.mAPPSoftInfoList.addAll(pAPPSoftInfoList);
            this.mAPPSoftInfoList.addAll(hAPPSoftInfoList);
        } else {
            size = this.mZidooAppManager.getInstalledSofts().size();
            for (i = 0; i < size; i++) {
                aPPSoftInfo = (APPSoftInfo) this.mZidooAppManager.getInstalledSofts().get(i);
                if (aPPSoftInfo.getType() != this.mType) {
                    this.mAPPSoftInfoList.add(aPPSoftInfo);
                }
            }
        }
        return this.mAPPSoftInfoList;
    }

    public void deleteClassApp(String type, APPSoftInfo aPPSoftInfo) {
        deleteClassType(this.mContext, aPPSoftInfo.getPackageName(), type);
        if (this.mApkClassOnChangeListenerMap.containsKey(type)) {
            ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(type)).deleteSoft(type, aPPSoftInfo.getPackageName());
        }
    }

    public void setTypeChange(int currentIndex) {
        int size = this.mAPPSoftInfoList.size();
        if (currentIndex < 0 || currentIndex >= size) {
            throw new RuntimeException("bob currentIndex error");
        }
        APPSoftInfo aPPSoftInfo = (APPSoftInfo) this.mAPPSoftInfoList.get(currentIndex);
        String currentType;
        if (this.mPName == null) {
            currentType = aPPSoftInfo.getType();
            changeClassType(this.mContext, aPPSoftInfo.getPackageName(), this.mType, null);
            if (!(currentType.equals(this.mType) || currentType.equals(ZidooAppManager.ZIDOOHINTTYPE) || !this.mApkClassOnChangeListenerMap.containsKey(currentType))) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(currentType)).deleteSoft(currentType, aPPSoftInfo.getPackageName());
            }
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).addSoft(this.mType, aPPSoftInfo);
            }
        } else if (aPPSoftInfo.getPackageName().equals(this.mPName)) {
            deleteClassType(this.mContext, this.mPName, this.mType);
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).deleteSoft(this.mType, this.mPName);
            }
        } else if (!aPPSoftInfo.getType().equals(this.mType) && !aPPSoftInfo.getType().equals(ZidooAppManager.ZIDOOHINTTYPE)) {
            currentType = aPPSoftInfo.getType();
            changeClassType(this.mContext, aPPSoftInfo.getPackageName(), this.mType, this.mPName);
            if (this.mApkClassOnChangeListenerMap.containsKey(currentType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(currentType)).deleteSoft(currentType, aPPSoftInfo.getPackageName());
            }
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).changeClass(this.mType, aPPSoftInfo, this.mPName);
            }
        } else if (aPPSoftInfo.getType().equals(this.mType)) {
            changePosition(this.mContext, aPPSoftInfo.getPackageName(), this.mPName, this.mType);
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).changePosition(this.mType, aPPSoftInfo, this.mPName);
            }
        } else if (aPPSoftInfo.getType().equals(ZidooAppManager.ZIDOOHINTTYPE)) {
            changeClassType(this.mContext, aPPSoftInfo.getPackageName(), this.mType, this.mPName);
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).changeClass(this.mType, aPPSoftInfo, this.mPName);
            }
        }
    }

    public boolean setTypeIndexChange(int currentIndex) {
        int size = this.mAPPSoftInfoList.size();
        if (currentIndex < 0 || currentIndex >= size) {
            throw new RuntimeException("bob currentIndex error");
        }
        APPSoftInfo aPPSoftInfo = (APPSoftInfo) this.mAPPSoftInfoList.get(currentIndex);
        this.mPName = aPPSoftInfo.getPackageName();
        if (aPPSoftInfo.isWebData()) {
            if (aPPSoftInfo.getType().equals(this.mType)) {
                aPPSoftInfo.setType(ZidooAppManager.ZIDOOHINTTYPE);
                ZidooSharedPrefsUtil.putValue(this.mContext, aPPSoftInfo.getPackageName(), false);
                if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                    ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).deleteSoft(this.mType, this.mPName);
                }
                return false;
            }
            aPPSoftInfo.setType(this.mType);
            ZidooSharedPrefsUtil.putValue(this.mContext, aPPSoftInfo.getPackageName(), true);
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).addSoft(this.mType, aPPSoftInfo);
            }
            return true;
        } else if (aPPSoftInfo.getType().equals(this.mType)) {
            deleteClassType(this.mContext, this.mPName, this.mType);
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).deleteSoft(this.mType, this.mPName);
            }
            return false;
        } else {
            String currentType = aPPSoftInfo.getType();
            changeClassType(this.mContext, aPPSoftInfo.getPackageName(), this.mType, null);
            if (!(currentType.equals(this.mType) || currentType.equals(ZidooAppManager.ZIDOOHINTTYPE) || !this.mApkClassOnChangeListenerMap.containsKey(currentType))) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(currentType)).deleteSoft(currentType, aPPSoftInfo.getPackageName());
            }
            if (this.mApkClassOnChangeListenerMap.containsKey(this.mType)) {
                ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(this.mType)).addSoft(this.mType, aPPSoftInfo);
            }
            return true;
        }
    }

    public void addDownApp(String type, APPSoftInfo aPPSoftInfo) {
        ZidooSQliteManger.getClassBaseManger(this.mContext).changeType(aPPSoftInfo.getPackageName(), type, null);
        if (this.mApkClassOnChangeListenerMap.containsKey(type)) {
            ((AppClassOnChangeListener) this.mApkClassOnChangeListenerMap.get(type)).addSoft(type, aPPSoftInfo);
        }
    }

    public void changePosition(Context context, String newPname, String oldPname, String type) {
        if (newPname != null && !newPname.equals("")) {
            ZidooSQliteManger.getClassBaseManger(context).changeTypePosition(newPname, oldPname);
            if (this.mZidooAppManager.getTypeSoftsHash().containsKey(type)) {
                ArrayList<APPSoftInfo> app_list_n = (ArrayList) this.mZidooAppManager.getTypeSoftsHash().get(type);
                int size = app_list_n.size();
                int newID = -1;
                APPSoftInfo newInfo = null;
                int oldID = -1;
                APPSoftInfo oldInfo = null;
                for (int i = 0; i < size; i++) {
                    APPSoftInfo aPPSoftInfo = (APPSoftInfo) app_list_n.get(i);
                    if (aPPSoftInfo.getPackageName().equals(newPname)) {
                        newID = i;
                        newInfo = aPPSoftInfo;
                    } else if (aPPSoftInfo.getPackageName().equals(oldPname)) {
                        oldID = i;
                        oldInfo = aPPSoftInfo;
                    }
                    if (newID != -1 && oldID != -1) {
                        break;
                    }
                }
                if (newID != -1 && oldID != -1) {
                    app_list_n.set(newID, oldInfo);
                    app_list_n.set(oldID, newInfo);
                }
            }
        }
    }

    public void deleteClassType(Context context, String pName, String type) {
        if (pName != null && !pName.equals("")) {
            if (this.mZidooAppManager.getInstalledSoftsHash().containsKey(pName) && this.mZidooAppManager.getTypeSoftsHash().containsKey(type)) {
                ArrayList<APPSoftInfo> app_list_p = (ArrayList) this.mZidooAppManager.getTypeSoftsHash().get(type);
                for (int i = 0; i < app_list_p.size(); i++) {
                    if (((APPSoftInfo) app_list_p.get(i)).getPackageName().equals(pName)) {
                        ((APPSoftInfo) app_list_p.get(i)).setType(ZidooAppManager.ZIDOOHINTTYPE);
                        app_list_p.remove(i);
                        break;
                    }
                }
            }
            ZidooSQliteManger.getClassBaseManger(context).deleteType(pName);
        }
    }

    public void changeClassType(Context context, String pName, String n_type, String oldPname) {
        if (pName != null && !pName.equals("")) {
            if (this.mZidooAppManager.getInstalledSoftsHash().containsKey(pName)) {
                int i;
                APPSoftInfo aPPSoftInfo = (APPSoftInfo) this.mZidooAppManager.getInstalledSoftsHash().get(pName);
                String type = aPPSoftInfo.getType();
                if (this.mZidooAppManager.getTypeSoftsHash().containsKey(type)) {
                    ArrayList<APPSoftInfo> app_list_p = (ArrayList) this.mZidooAppManager.getTypeSoftsHash().get(type);
                    for (i = 0; i < app_list_p.size(); i++) {
                        if (((APPSoftInfo) app_list_p.get(i)).getPackageName().equals(pName)) {
                            app_list_p.remove(i);
                            break;
                        }
                    }
                }
                aPPSoftInfo.setType(n_type);
                if (this.mZidooAppManager.getTypeSoftsHash().containsKey(n_type)) {
                    ArrayList<APPSoftInfo> app_list_n = (ArrayList) this.mZidooAppManager.getTypeSoftsHash().get(n_type);
                    if (oldPname != null) {
                        for (i = 0; i < app_list_n.size(); i++) {
                            if (((APPSoftInfo) app_list_n.get(i)).getPackageName().equals(oldPname)) {
                                ((APPSoftInfo) app_list_n.get(i)).setType(ZidooAppManager.ZIDOOHINTTYPE);
                                app_list_n.remove(i);
                                break;
                            }
                        }
                    }
                    app_list_n.add(aPPSoftInfo);
                }
            }
            ZidooSQliteManger.getClassBaseManger(context).changeType(pName, n_type, oldPname);
        }
    }
}
