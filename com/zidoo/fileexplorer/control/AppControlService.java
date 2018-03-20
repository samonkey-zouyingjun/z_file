package com.zidoo.fileexplorer.control;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.umeng.common.a;
import com.zidoo.control.center.http.tool.ZidooHttpTool;
import com.zidoo.control.center.tool.IElementService.Stub;
import com.zidoo.control.center.tool.ZidooControlInfo;
import com.zidoo.control.center.tool.ZidooHttpStatusContants;
import com.zidoo.fileexplorer.config.AppConstant;
import java.util.HashMap;
import zidoo.browse.BrowseConstant;

public class AppControlService extends Service {
    public static final String SERCIER_ACTION = "com.zidoo.fileexplorer.controlService.action";
    public static final String SERCIER_ELEMENTNAME = "ZidooFileControl";
    public static final int SERCIER_VERSION = 1;

    public class ServiceBinder extends Stub {
        public ZidooControlInfo handle(String uri) throws RemoteException {
            return AppControlService.this.serviceHandle(uri);
        }

        public Service getService() {
            return AppControlService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
    }

    public ZidooControlInfo serviceHandle(String uri) {
        boolean isShowHidefile = false;
        if (uri == null) {
            return null;
        }
        Log.v("bob", "ZidooFileControl --> uri = " + uri);
        ZidooControlInfo zidooControlInfo = new ZidooControlInfo(0);
        HashMap<String, String> parmsHashMap = ZidooHttpTool.getHttpParms(uri);
        if (uri.startsWith("getDevices")) {
            boolean disHost = false;
            if (parmsHashMap.containsKey("disHost")) {
                try {
                    disHost = ((String) parmsHashMap.get("disHost")).endsWith("1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            zidooControlInfo.mJson = ZidooFileManager.getAllMountDevices(this, disHost);
            if (zidooControlInfo.mJson == null) {
                zidooControlInfo.mJson = ZidooHttpTool.getCallBackJson(ZidooHttpStatusContants.HTTP_RESOURCE_NOT_EXIST);
            }
        } else if (uri.startsWith("getFileList") || uri.startsWith("getHost")) {
            String ip;
            if (parmsHashMap.containsKey(BrowseConstant.EXTRA_PATH)) {
                path = (String) parmsHashMap.get(BrowseConstant.EXTRA_PATH);
            } else {
                path = null;
            }
            if (parmsHashMap.containsKey(AppConstant.DB_SMB_IP)) {
                ip = (String) parmsHashMap.get(AppConstant.DB_SMB_IP);
            } else {
                ip = null;
            }
            int type = -1;
            if (parmsHashMap.containsKey("hidefile")) {
                isShowHidefile = ((String) parmsHashMap.get("hidefile")).equals("1");
            }
            if (parmsHashMap.containsKey(a.b)) {
                try {
                    type = Integer.valueOf((String) parmsHashMap.get(a.b)).intValue();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (!(path == null && ip == null) && type >= 0) {
                if (path != null) {
                    ip = path;
                }
                zidooControlInfo.mJson = ZidooFileManager.getFileList(this, ip, type, isShowHidefile);
            } else {
                zidooControlInfo.mJson = ZidooHttpTool.getCallBackJson(ZidooHttpStatusContants.HTTP_PARAMETER_ERROR);
            }
        } else if (uri.startsWith("openFile")) {
            if (parmsHashMap.containsKey(BrowseConstant.EXTRA_PATH)) {
                path = (String) parmsHashMap.get(BrowseConstant.EXTRA_PATH);
            } else {
                path = null;
            }
            int videoplaymode = 0;
            if (parmsHashMap.containsKey("videoplaymode")) {
                try {
                    videoplaymode = Integer.valueOf((String) parmsHashMap.get("videoplaymode")).intValue();
                } catch (Exception e22) {
                    e22.printStackTrace();
                }
            }
            if (path != null) {
                zidooControlInfo.mJson = ZidooFileManager.openFile(this, path, videoplaymode);
            } else {
                zidooControlInfo.mJson = ZidooHttpTool.getCallBackJson(ZidooHttpStatusContants.HTTP_PARAMETER_ERROR);
            }
        } else {
            zidooControlInfo.mJson = ZidooHttpTool.getCallBackJson(ZidooHttpStatusContants.HTTP_URL_ERROR);
        }
        return zidooControlInfo;
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
