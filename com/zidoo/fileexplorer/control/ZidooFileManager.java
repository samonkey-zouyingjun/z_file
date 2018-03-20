package com.zidoo.fileexplorer.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.umeng.common.a;
import com.zidoo.control.center.http.tool.ZidooHttpTool;
import com.zidoo.control.center.tool.ZidooHttpStatusContants;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.CompareTool;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import zidoo.browse.BrowseConstant;
import zidoo.device.BlockInfo;
import zidoo.device.DeviceType;
import zidoo.device.ZDevice;
import zidoo.file.FileType;
import zidoo.model.BoxModel;
import zidoo.tool.ZidooFileUtils;

public class ZidooFileManager {
    private static final String NFS_MOUNT_PAHT = "/mnt/nfs/";
    private static final String SMB_MOUNT_PAHT = "/tmp/ramfs/mnt/";
    private static final int TYPE_FLASH = 1000;
    private static final int TYPE_HDD = 1002;
    private static final int TYPE_NFS = 1004;
    private static final int TYPE_NF_HOST = 1006;
    private static final int TYPE_SMB = 1005;
    private static final int TYPE_SMB_HOST = 1007;
    private static final int TYPE_TF = 1003;
    private static final int TYPE_USB = 1001;

    public static String getAllMountDevices(Context context, boolean disHost) {
        try {
            ArrayList<ZDevice> zDevices = BoxModel.getModel(context, BoxModel.getBoxModel(context)).getDeviceList(15, true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(NotificationCompat.CATEGORY_STATUS, 200);
            JSONArray jsonArray = new JSONArray();
            jsonObject.put(AppConstant.EXTRA_USB_DEVICE, jsonArray);
            JSONObject nfsObject = null;
            JSONArray nfsjsonArray = null;
            JSONObject smbObject = null;
            JSONArray smbjsonArray = null;
            HashMap<String, String> nfsHashMap = new HashMap();
            HashMap<String, String> smbHashMap = new HashMap();
            for (int i = 0; i < zDevices.size(); i++) {
                ZDevice zDevice = (ZDevice) zDevices.get(i);
                BlockInfo block = zDevice.getBlock();
                Log.v("bob", "zDevice.getType() = " + zDevice.getType());
                String ip;
                JSONObject cObject;
                if (zDevice.getType() == DeviceType.NFS) {
                    if (nfsObject == null) {
                        nfsObject = new JSONObject();
                        nfsObject.put("name", "NFS");
                        nfsObject.put(a.b, 1004);
                        nfsObject.put("id", 1004);
                        nfsObject.put(BrowseConstant.EXTRA_PATH, NFS_MOUNT_PAHT);
                        if (disHost) {
                            nfsjsonArray = new JSONArray();
                            nfsObject.put("hosts", nfsjsonArray);
                        }
                    }
                    if (nfsjsonArray != null) {
                        ip = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(NFS_MOUNT_PAHT, "").trim().split("#")[0];
                        if (!nfsHashMap.containsKey(ip)) {
                            nfsHashMap.put(ip, ip);
                            cObject = new JSONObject();
                            nfsjsonArray.put(cObject);
                            cObject.put("name", ip);
                            cObject.put(AppConstant.DB_SMB_IP, ip);
                            cObject.put(a.b, 1006);
                        }
                    }
                } else {
                    if (zDevice.getType() == DeviceType.SMB) {
                        if (smbObject == null) {
                            smbObject = new JSONObject();
                            smbObject.put("name", "SMB");
                            smbObject.put(a.b, TYPE_SMB);
                            smbObject.put(BrowseConstant.EXTRA_PATH, SMB_MOUNT_PAHT);
                            if (disHost) {
                                smbjsonArray = new JSONArray();
                                smbObject.put("hosts", smbjsonArray);
                            }
                        }
                        if (smbjsonArray != null) {
                            ip = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(SMB_MOUNT_PAHT, "").trim().split("#")[0];
                            if (!smbHashMap.containsKey(ip)) {
                                smbHashMap.put(ip, ip);
                                cObject = new JSONObject();
                                smbjsonArray.put(cObject);
                                cObject.put("name", ip);
                                cObject.put(AppConstant.DB_SMB_IP, ip);
                                cObject.put(a.b, 1007);
                            }
                        }
                    } else {
                        String str;
                        String label;
                        if (zDevice.getType() == DeviceType.FLASH) {
                            cObject = new JSONObject();
                            str = "name";
                            label = block == null ? "Flash" : block.getLabel() == null ? "Flash" : block.getLabel();
                            cObject.put(str, label);
                            cObject.put(BrowseConstant.EXTRA_PATH, zDevice.getPath());
                            cObject.put(a.b, 1000);
                            jsonArray.put(cObject);
                        } else {
                            if (zDevice.getType() == DeviceType.SD) {
                                cObject = new JSONObject();
                                str = "name";
                                label = block == null ? "USB" : block.getLabel() == null ? "USB" : block.getLabel();
                                cObject.put(str, label);
                                cObject.put(BrowseConstant.EXTRA_PATH, zDevice.getPath());
                                cObject.put(a.b, 1001);
                                jsonArray.put(cObject);
                            } else {
                                if (zDevice.getType() == DeviceType.HDD) {
                                    cObject = new JSONObject();
                                    str = "name";
                                    label = block == null ? "HDD" : block.getLabel() == null ? "HDD" : block.getLabel();
                                    cObject.put(str, label);
                                    cObject.put(BrowseConstant.EXTRA_PATH, zDevice.getPath());
                                    cObject.put(a.b, 1002);
                                    jsonArray.put(cObject);
                                } else {
                                    if (zDevice.getType() == DeviceType.TF) {
                                        cObject = new JSONObject();
                                        str = "name";
                                        label = block == null ? "TF" : block.getLabel() == null ? "TF" : block.getLabel();
                                        cObject.put(str, label);
                                        cObject.put(BrowseConstant.EXTRA_PATH, zDevice.getPath());
                                        cObject.put(a.b, 1003);
                                        jsonArray.put(cObject);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (smbObject != null) {
                jsonArray.put(smbObject);
            }
            if (nfsObject != null) {
                jsonArray.put(nfsObject);
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getSendData(ArrayList<SendFileInfo> sendInfoList, boolean isExists, boolean isDevice, String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NotificationCompat.CATEGORY_STATUS, 200);
            int size = sendInfoList.size();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < size; i++) {
                JSONObject cObject = new JSONObject();
                SendFileInfo sendFileInfo = (SendFileInfo) sendInfoList.get(i);
                cObject.put("name", sendFileInfo.title);
                cObject.put(a.b, sendFileInfo.type);
                if (isDevice) {
                    cObject.put(AppConstant.DB_SMB_IP, URLEncoder.encode(sendFileInfo.path, "utf-8"));
                } else {
                    cObject.put(BrowseConstant.EXTRA_PATH, URLEncoder.encode(sendFileInfo.path, "utf-8"));
                    cObject.put("isBDMV", sendFileInfo.isBDMV);
                    cObject.put("isBluray", sendFileInfo.isBluray);
                    cObject.put("length", sendFileInfo.mlength);
                    cObject.put("modifyDate", sendFileInfo.mModifyDate);
                }
                jsonArray.put(cObject);
            }
            if (isDevice) {
                jsonObject.put("hosts", jsonArray);
            } else {
                jsonObject.put("isExists", isExists);
                jsonObject.put("perentPath", path);
                jsonObject.put("filelist", jsonArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getFileList(Context context, String path, int type, boolean isShowHidefile) {
        ArrayList<SendFileInfo> sendInfoList = new ArrayList();
        boolean isExists = true;
        boolean isDevices = false;
        ArrayList<ZDevice> zDevices;
        HashMap<String, String> nfsHashMap;
        int i;
        ZDevice zDevice;
        String ip;
        String[] pathIfno;
        String title;
        SendFileInfo sendFileInfo;
        if (type == 1004 || type == 1006) {
            try {
                zDevices = BoxModel.getModel(context, BoxModel.getBoxModel(context)).getDeviceList(15, true);
                if (path.equals(NFS_MOUNT_PAHT)) {
                    isDevices = true;
                    nfsHashMap = new HashMap();
                    int size = zDevices.size();
                    for (i = 0; i < size; i++) {
                        zDevice = (ZDevice) zDevices.get(i);
                        if (zDevice.getType() == DeviceType.NFS) {
                            ip = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(NFS_MOUNT_PAHT, "").trim().split("#")[0];
                            if (!nfsHashMap.containsKey(ip)) {
                                nfsHashMap.put(ip, ip);
                                sendInfoList.add(new SendFileInfo(ip, ip, 1006));
                            }
                        }
                    }
                } else {
                    for (i = 0; i < zDevices.size(); i++) {
                        zDevice = (ZDevice) zDevices.get(i);
                        if (zDevice.getType() == DeviceType.NFS) {
                            pathIfno = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(NFS_MOUNT_PAHT, "").trim().split("#");
                            ip = pathIfno[0];
                            title = pathIfno[1];
                            if (ip.equals(path)) {
                                sendFileInfo = new SendFileInfo(title, zDevice.getPath(), 0);
                                initFile(new File(zDevice.getPath()), sendFileInfo);
                                sendInfoList.add(sendFileInfo);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == TYPE_SMB || type == 1007) {
            zDevices = BoxModel.getModel(context, BoxModel.getBoxModel(context)).getDeviceList(15, true);
            if (path.equals(SMB_MOUNT_PAHT)) {
                isDevices = true;
                nfsHashMap = new HashMap();
                for (i = 0; i < zDevices.size(); i++) {
                    zDevice = (ZDevice) zDevices.get(i);
                    if (zDevice.getType() == DeviceType.SMB) {
                        ip = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(SMB_MOUNT_PAHT, "").trim().split("#")[0];
                        if (!nfsHashMap.containsKey(ip)) {
                            nfsHashMap.put(ip, ip);
                            sendInfoList.add(new SendFileInfo(ip, ip, 1007));
                        }
                    }
                }
            } else {
                for (i = 0; i < zDevices.size(); i++) {
                    zDevice = (ZDevice) zDevices.get(i);
                    if (zDevice.getType() == DeviceType.SMB) {
                        pathIfno = ZidooFileUtils.decodeCommand(zDevice.getPath()).replace(SMB_MOUNT_PAHT, "").trim().split("#");
                        ip = pathIfno[0];
                        title = pathIfno[1];
                        if (ip.equals(path)) {
                            sendFileInfo = new SendFileInfo(title, zDevice.getPath(), 0);
                            initFile(new File(zDevice.getPath()), sendFileInfo);
                            sendInfoList.add(sendFileInfo);
                        }
                    }
                }
            }
        } else {
            File file = new File(path);
            if (!file.exists()) {
                isExists = false;
            } else if (file.isDirectory()) {
                File[] chFile = file.listFiles();
                if (chFile != null) {
                    CompareTool.sortByName(chFile, true);
                    for (File cFile : chFile) {
                        if (isShowHidefile || !cFile.getName().startsWith(".")) {
                            boolean isBluray;
                            boolean isBDMV = false;
                            if (cFile.isDirectory()) {
                                if (FileType.isBDMV(cFile.getAbsolutePath())) {
                                    isBDMV = true;
                                }
                                isBluray = isBDMV;
                            } else {
                                isBluray = false;
                                try {
                                    if (FileType.isIsoMovie(cFile.getAbsolutePath())) {
                                        isBluray = true;
                                    }
                                } catch (Exception e2) {
                                }
                            }
                            sendFileInfo = new SendFileInfo(cFile.getName(), cFile.getAbsolutePath(), FileType.getType(cFile), isBDMV);
                            sendFileInfo.isBluray = isBluray;
                            initFile(cFile, sendFileInfo);
                            sendInfoList.add(sendFileInfo);
                        }
                    }
                }
            }
        }
        return getSendData(sendInfoList, isExists, isDevices, path);
    }

    private static void initFile(File file, SendFileInfo sendFileInfo) {
        if (file != null && file.exists() && sendFileInfo != null) {
            sendFileInfo.mModifyDate = file.lastModified();
            if (file.isFile()) {
                sendFileInfo.mlength = file.length();
            }
        }
    }

    public static String openFile(Context context, String path, int movetype) {
        File file = new File(path);
        if (file.exists()) {
            Intent intent;
            if (file.isDirectory() && FileType.isBDMV(path)) {
                BoxModel model = BoxModel.getModel(context, BoxModel.getBoxModel(context));
                intent = model.getBDMVOpenWith(file);
                if (intent != null) {
                    if (movetype == 1 && ZidooBoxPermissions.isBluray(context) && Utils.isAppSystemInstall(context, "com.zidoo.bluraynavigation")) {
                        intent.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                        intent.addFlags(335544320);
                        context.startActivity(intent);
                    } else {
                        model.openBDMV(file);
                    }
                    ZidooFileUtils.sendPauseBroadCast(context);
                    return ZidooHttpTool.getCallBackJson(200);
                }
            } else if (file.isFile()) {
                intent = BoxModel.getModel(context, BoxModel.getBoxModel(context)).getOpenWith(file);
                if (intent != null) {
                    if (movetype == 1 && ZidooBoxPermissions.isBluray(context) && Utils.isAppSystemInstall(context, "com.zidoo.bluraynavigation")) {
                        intent.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                    }
                    intent.addFlags(335544320);
                    context.startActivity(intent);
                    ZidooFileUtils.sendPauseBroadCast(context);
                    return ZidooHttpTool.getCallBackJson(200);
                }
            }
        }
        return ZidooHttpTool.getCallBackJson(ZidooHttpStatusContants.HTTP_PARAMETER_ERROR);
    }
}
