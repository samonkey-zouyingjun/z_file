package com.zidoo.permissions;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.umeng.common.util.e;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import com.zidoo.permissions.des.DESTool;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import zidoo.http.HTTPStatus;

public class ZidooPhonePermissions {
    private static final int AUTOSUCCESS = 2;
    private static final String ERRORUUID = "errorerrorerrore";
    private static final String LOCALUUID = "localuuid";
    private static final String NETUUID = "1777777777777777";
    private static final int STARTAUTO = 3;
    private static final int STARTAUTODISMISS = 6;
    private static final int STARTAUTOFAILE = 5;
    private static final int STARTAUTOSUCCESS = 4;
    private static final String[] UIAPP = new String[]{"com.zidoo.oldpeople.ui"};
    private static final String[] USEMODEL = new String[]{"7029c_J0918", "atm7059c_n3", "rk30sdk"};
    private static final String USTNAME = "/zidooAuth/auth.txt";
    private static boolean isCheckAuthorized = false;
    static boolean isPrintln = false;
    private Context mContext = null;
    private Dialog mDialog = null;
    private Handler mHandler = null;
    private String mUUID = "";
    private ZidooUuidTool mZidooUuidTool = null;

    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ int[] val$drawID;
        private final /* synthetic */ Context val$mContext;

        AnonymousClass1(Context context, int[] iArr) {
            this.val$mContext = context;
            this.val$drawID = iArr;
        }

        public void run() {
            try {
                String[] md5 = ZidooPhonePermissions.getDrawMd5(this.val$mContext, this.val$drawID);
                for (String str : md5) {
                    System.out.println("bob  MainActivity md5 = " + str);
                }
            } catch (Exception e) {
            }
        }
    }

    class AnonymousClass2 implements Runnable {
        private final /* synthetic */ int[] val$drawID;
        private final /* synthetic */ Context val$mContext;
        private final /* synthetic */ String[] val$md5;

        AnonymousClass2(int[] iArr, Context context, String[] strArr) {
            this.val$drawID = iArr;
            this.val$mContext = context;
            this.val$md5 = strArr;
        }

        public void run() {
            int length = this.val$drawID.length;
            int i = 0;
            while (i < length) {
                if (ZidooPhonePermissions.checkMd5(this.val$mContext, this.val$drawID[i], this.val$md5[i])) {
                    i++;
                } else {
                    throw new RuntimeException("ZidooPermissions checkMd5 error Please contact Zidoo Mr.Bob");
                }
            }
        }
    }

    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ Bitmap val$bitmap;

        AnonymousClass3(Bitmap bitmap) {
            this.val$bitmap = bitmap;
        }

        public void run() {
            try {
                if (this.val$bitmap != null) {
                    this.val$bitmap.recycle();
                    System.gc();
                }
            } catch (Exception e) {
            }
        }
    }

    public static void checkAppPermissions() {
        if (!isCheckAuthorized) {
            throw new RuntimeException("ZidooPhonePermissions error Please contact Zidoo Mr.Bob");
        }
    }

    public ZidooPhonePermissions(Context mContext) {
        this.mContext = mContext;
        this.mZidooUuidTool = new ZidooUuidTool(mContext);
    }

    public boolean checkPermissionsByModel(Context mContext, int[] drawID, String[] md5) {
        if (isAvailableMODEL()) {
            initZidooPermissions(mContext, drawID, md5);
            checkUUID();
            return true;
        }
        throw new RuntimeException("ZidooPermissions model error Please contact Zidoo Mr.Bob");
    }

    public boolean checkPermissionsByUI(Context mContext, int[] drawID, String[] md5) {
        if (isInstallUIApp(mContext)) {
            initZidooPermissions(mContext, drawID, md5);
            checkUUID();
            return true;
        }
        throw new RuntimeException("ZidooPermissions UI error Please contact Zidoo Mr.Bob");
    }

    public boolean checkPermissionsByAll(Context mContext, int[] drawID, String[] md5) {
        if (isPermissions(mContext)) {
            initZidooPermissions(mContext, drawID, md5);
            checkUUID();
            return true;
        }
        throw new RuntimeException("ZidooPermissions UI or model error Please contact Zidoo Mr.Bob");
    }

    public void checkPermissionsOnlyPictrue(Context mContext, int[] drawID, String[] md5) {
        initZidooPermissions(mContext, drawID, md5);
        checkUUID();
    }

    public static void getMd5(Context mContext, int[] drawID) {
        new Thread(new AnonymousClass1(mContext, drawID)).start();
    }

    private static boolean isPermissions(Context mContext) {
        if (isAvailableMODEL() && isInstallUIApp(mContext)) {
            return true;
        }
        return false;
    }

    private static boolean isAvailableMODEL() {
        try {
            String moder = Build.MODEL;
            for (Object equals : USEMODEL) {
                if (moder.equals(equals)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isInstallUIApp(Context context) {
        for (String isInstallapp : UIAPP) {
            if (isInstallapp(context, isInstallapp)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInstallapp(Context context, String pckName) {
        if (pckName != null) {
            try {
                if (context.getPackageManager().getLaunchIntentForPackage(pckName) != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void initZidooPermissions(Context mContext, int[] drawID, String[] md5) {
        if (drawID != null && md5 != null) {
            startChechMd5(mContext, drawID, md5);
        }
    }

    private static void startChechMd5(Context mContext, int[] drawID, String[] md5) {
        new Thread(new AnonymousClass2(drawID, mContext, md5)).start();
    }

    private static boolean checkMd5(Context context, int drawID, String md5) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawID);
            if (md5.toLowerCase().compareTo(createMd5(Bitmap2Bytes(bitmap))) == 0) {
                recycleBitmap(bitmap);
                return true;
            }
            recycleBitmap(bitmap);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void recycleBitmap(Bitmap bitmap) {
        new Thread(new AnonymousClass3(bitmap)).start();
    }

    private static String[] getDrawMd5(Context context, int[] drawID) {
        int length = drawID.length;
        String[] md5 = new String[length];
        for (int i = 0; i < length; i++) {
            String str = "";
            try {
                str = createMd5(Bitmap2Bytes(BitmapFactory.decodeResource(context.getResources(), drawID[i])));
            } catch (Exception e) {
                e.printStackTrace();
                str = "";
            }
            md5[i] = str;
        }
        return md5;
    }

    private static String createMd5(byte[] is) {
        try {
            MessageDigest mMDigest = MessageDigest.getInstance("MD5");
            mMDigest.update(is);
            return new BigInteger(1, mMDigest.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public String getID() {
        return this.mUUID;
    }

    private void checkUUID() {
        if (getHosts()) {
            this.mZidooUuidTool.writeUUID(ERRORUUID);
            throw new RuntimeException("ZidooPermissions uuid s error Please contact Zidoo Mr.Bob");
        }
        String uuid = this.mZidooUuidTool.readUUID();
        if (uuid == null || uuid.trim().equals("") || uuid.trim().equals(ERRORUUID)) {
            throw new RuntimeException("ZidooPermissions uuid error Please contact Zidoo Mr.Bob");
        }
        this.mUUID = uuid;
        if (uuid.trim().equals(NETUUID)) {
            if (netIsConnected(this.mContext)) {
                throw new RuntimeException("ZidooPermissions netIsConnected uuid error  Please contact Zidoo Mr.Bob");
            }
            this.mUUID = "";
        }
        isCheckAuthorized = true;
    }

    public boolean isAuth() {
        if (getHosts()) {
            return false;
        }
        String uuid = this.mZidooUuidTool.readUUID();
        if (uuid == null || uuid.trim().equals("") || uuid.trim().equals(ERRORUUID) || uuid.trim().equals(NETUUID)) {
            return false;
        }
        return true;
    }

    public boolean initcheckPermissionsByModel(Context mContext, int[] drawID, String[] md5) {
        if (isAvailableMODEL()) {
            initZidooPermissions(mContext, drawID, md5);
            startWeb();
            return true;
        }
        throw new RuntimeException("ZidooPermissions model error Please contact Zidoo Mr.Bob");
    }

    public boolean initcheckPermissionsByUI(Context mContext, int[] drawID, String[] md5) {
        if (isInstallUIApp(mContext)) {
            initZidooPermissions(mContext, drawID, md5);
            startWeb();
            return true;
        }
        throw new RuntimeException("ZidooPermissions UI error Please contact Zidoo Mr.Bob");
    }

    public boolean initcheckPermissionsByAll(Context mContext, int[] drawID, String[] md5) {
        if (isPermissions(mContext)) {
            initZidooPermissions(mContext, drawID, md5);
            startWeb();
            return true;
        }
        throw new RuntimeException("ZidooPermissions UI or model error Please contact Zidoo Mr.Bob");
    }

    public void initcheckPermissionsOnlyPictrue(Context mContext, int[] drawID, String[] md5) {
        initZidooPermissions(mContext, drawID, md5);
        startWeb();
    }

    private void startWeb() {
        if (getHosts()) {
            this.mZidooUuidTool.writeUUID(ERRORUUID);
            throw new RuntimeException("ZidooPermissions uuid web error Please contact Zidoo Mr.Bob");
        }
        this.mUUID = this.mZidooUuidTool.readUUID();
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 3:
                        if (ZidooPhonePermissions.this.mDialog != null && ZidooPhonePermissions.this.mDialog.isShowing()) {
                            ZidooPhonePermissions.this.mDialog.dismiss();
                        }
                        ZidooPhonePermissions.this.mDialog = new Builder(ZidooPhonePermissions.this.mContext).setTitle("授权提醒").setMessage("\n\n\n\t\t正在授权，请稍后...  (请确保网络已连接)\n\n\t\t型号：" + Build.MODEL + "\n\n\t\tMac：" + ZidooPhonePermissions.this.getWifiMac(ZidooPhonePermissions.this.mContext) + "\n\n\n").create();
                        ZidooPhonePermissions.this.mDialog.getWindow().setType(2003);
                        ZidooPhonePermissions.this.mDialog.show();
                        return;
                    case 4:
                        if (ZidooPhonePermissions.this.mDialog != null && ZidooPhonePermissions.this.mDialog.isShowing()) {
                            ZidooPhonePermissions.this.mDialog.dismiss();
                        }
                        ZidooPhonePermissions.this.mDialog = new Builder(ZidooPhonePermissions.this.mContext).setTitle("授权结果").setMessage("\n\n\n\t\t授权成功，感谢您使用正版软件！\n\n\t\t型号：" + Build.MODEL + "\n\n\t\tMac：" + ZidooPhonePermissions.this.getWifiMac(ZidooPhonePermissions.this.mContext) + "\n\n\n").create();
                        ZidooPhonePermissions.this.mDialog.getWindow().setType(2003);
                        ZidooPhonePermissions.this.mDialog.show();
                        return;
                    case 5:
                        if (ZidooPhonePermissions.this.mDialog != null && ZidooPhonePermissions.this.mDialog.isShowing()) {
                            ZidooPhonePermissions.this.mDialog.dismiss();
                        }
                        ZidooPhonePermissions.this.mDialog = new Builder(ZidooPhonePermissions.this.mContext).setTitle("授权结果").setMessage("\n\n\n\t\t授权失败，请使用正版软件！\n\n\t\t型号：" + Build.MODEL + "\n\n\t\tMac：" + ZidooPhonePermissions.this.getWifiMac(ZidooPhonePermissions.this.mContext) + "\n\n\n").create();
                        Window window = ZidooPhonePermissions.this.mDialog.getWindow();
                        ZidooPhonePermissions.this.mDialog.setCanceledOnTouchOutside(false);
                        ZidooPhonePermissions.this.mDialog.setOnKeyListener(new OnKeyListener() {
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                return true;
                            }
                        });
                        window.setType(2003);
                        LayoutParams lp = window.getAttributes();
                        lp.width = -1;
                        lp.height = -1;
                        ZidooPhonePermissions.this.mDialog.show();
                        return;
                    case 6:
                        if (ZidooPhonePermissions.this.mDialog != null && ZidooPhonePermissions.this.mDialog.isShowing()) {
                            ZidooPhonePermissions.this.mDialog.dismiss();
                        }
                        throw new RuntimeException("ZidooPermissions auto uuid error Please contact Zidoo Mr.Bob");
                    default:
                        return;
                }
            }
        };
        mySystemOut("  startWeb ");
        mySystemOut("startWeb uuid = " + this.mUUID);
        if (this.mUUID == null || this.mUUID.trim().equals("") || this.mUUID.trim().equals(NETUUID) || this.mUUID.trim().equals(ERRORUUID)) {
            start();
            return;
        }
        String localUUID = getValue(this.mContext, LOCALUUID, null);
        if (localUUID == null || !localUUID.equals(this.mUUID)) {
            check(this.mUUID);
        }
    }

    public static boolean getHosts() {
        boolean isError = false;
        try {
            InputStream is = Runtime.getRuntime().exec("cat /system/etc/hosts").getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line;
            do {
                line = bf.readLine();
                if (line == null) {
                    break;
                }
            } while (!line.contains("zidoo"));
            isError = true;
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isError;
    }

    private void start() {
        new Thread(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r10 = this;
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r8 = " start ";
                r7.mySystemOut(r8);
                r7 = com.zidoo.permissions.ZidooPhonePermissions.getHosts();
                if (r7 == 0) goto L_0x0055;
            L_0x000e:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r8 = " start 0";
                r7.mySystemOut(r8);
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r7 = r7.mUUID;
                if (r7 == 0) goto L_0x0040;
            L_0x001e:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r7 = r7.mUUID;
                r7 = r7.trim();
                r8 = "";
                r7 = r7.equals(r8);
                if (r7 != 0) goto L_0x0040;
            L_0x0031:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r7 = r7.mUUID;
                r8 = "errorerrorerrore";
                r7 = r7.equals(r8);
                if (r7 != 0) goto L_0x004c;
            L_0x0040:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r7 = r7.mZidooUuidTool;
                r8 = "errorerrorerrore";
                r7.writeUUID(r8);
            L_0x004c:
                r7 = new java.lang.RuntimeException;
                r8 = "ZidooPermissions uuid web error Please contact Zidoo Mr.Bob";
                r7.<init>(r8);
                throw r7;
            L_0x0055:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r8 = " start 1";
                r7.mySystemOut(r8);
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r7 = r7.mHandler;
                r8 = 3;
                r7.sendEmptyMessage(r8);
            L_0x0067:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = r8.mContext;	 Catch:{ Exception -> 0x014f }
                r7 = r7.netIsConnected(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 == 0) goto L_0x01b6;
            L_0x0075:
                r1 = 0;
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r3 = r7.getKey();	 Catch:{ Exception -> 0x014f }
                if (r3 == 0) goto L_0x008b;
            L_0x007e:
                r7 = r3.trim();	 Catch:{ Exception -> 0x014f }
                r8 = "";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 == 0) goto L_0x00e1;
            L_0x008b:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mZidooUuidTool;	 Catch:{ Exception -> 0x014f }
                r7 = r7.isMac();	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x00e0;
            L_0x0097:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                if (r7 == 0) goto L_0x00c1;
            L_0x009f:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r7 = r7.trim();	 Catch:{ Exception -> 0x014f }
                r8 = "";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x00c1;
            L_0x00b2:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r8 = "errorerrorerrore";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x00cd;
            L_0x00c1:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mZidooUuidTool;	 Catch:{ Exception -> 0x014f }
                r8 = "errorerrorerrore";
                r7.writeUUID(r8);	 Catch:{ Exception -> 0x014f }
            L_0x00cd:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mHandler;	 Catch:{ Exception -> 0x014f }
                r8 = 5;
                r7.sendEmptyMessage(r8);	 Catch:{ Exception -> 0x014f }
            L_0x00d7:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;
                r8 = "start over";
                r7.mySystemOut(r8);
                return;
            L_0x00e0:
                r1 = 1;
            L_0x00e1:
                r5 = 0;
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014f }
                r9 = " start isMac = ";
                r8.<init>(r9);	 Catch:{ Exception -> 0x014f }
                r8 = r8.append(r1);	 Catch:{ Exception -> 0x014f }
                r8 = r8.toString();	 Catch:{ Exception -> 0x014f }
                r7.mySystemOut(r8);	 Catch:{ Exception -> 0x014f }
                if (r1 == 0) goto L_0x0155;
            L_0x00f9:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r5 = r7.startMacAuthorized();	 Catch:{ Exception -> 0x014f }
            L_0x00ff:
                if (r5 == 0) goto L_0x01f4;
            L_0x0101:
                r2 = new org.json.JSONObject;	 Catch:{ Exception -> 0x014f }
                r2.<init>(r5);	 Catch:{ Exception -> 0x014f }
                r7 = "status";
                r4 = r2.getInt(r7);	 Catch:{ Exception -> 0x014f }
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014f }
                r9 = "  start stauts = ";
                r8.<init>(r9);	 Catch:{ Exception -> 0x014f }
                r8 = r8.append(r4);	 Catch:{ Exception -> 0x014f }
                r8 = r8.toString();	 Catch:{ Exception -> 0x014f }
                r7.mySystemOut(r8);	 Catch:{ Exception -> 0x014f }
                r7 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
                if (r4 != r7) goto L_0x0171;
            L_0x0126:
                r7 = "uuid";
                r6 = r2.getString(r7);	 Catch:{ Exception -> 0x014f }
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mZidooUuidTool;	 Catch:{ Exception -> 0x014f }
                r7.writeUUID(r6);	 Catch:{ Exception -> 0x014f }
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = r8.mContext;	 Catch:{ Exception -> 0x014f }
                r9 = "localuuid";
                r7.putValue(r8, r9, r6);	 Catch:{ Exception -> 0x014f }
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mHandler;	 Catch:{ Exception -> 0x014f }
                r8 = 4;
                r7.sendEmptyMessage(r8);	 Catch:{ Exception -> 0x014f }
                goto L_0x00d7;
            L_0x014f:
                r0 = move-exception;
                r0.printStackTrace();
                goto L_0x0067;
            L_0x0155:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x014f }
                r9 = " start key = ";
                r8.<init>(r9);	 Catch:{ Exception -> 0x014f }
                r8 = r8.append(r3);	 Catch:{ Exception -> 0x014f }
                r8 = r8.toString();	 Catch:{ Exception -> 0x014f }
                r7.mySystemOut(r8);	 Catch:{ Exception -> 0x014f }
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r5 = r7.startAuthorized(r3);	 Catch:{ Exception -> 0x014f }
                goto L_0x00ff;
            L_0x0171:
                r7 = -1;
                if (r4 != r7) goto L_0x00d7;
            L_0x0174:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                if (r7 == 0) goto L_0x019e;
            L_0x017c:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r7 = r7.trim();	 Catch:{ Exception -> 0x014f }
                r8 = "";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x019e;
            L_0x018f:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r8 = "errorerrorerrore";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x01aa;
            L_0x019e:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mZidooUuidTool;	 Catch:{ Exception -> 0x014f }
                r8 = "errorerrorerrore";
                r7.writeUUID(r8);	 Catch:{ Exception -> 0x014f }
            L_0x01aa:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mHandler;	 Catch:{ Exception -> 0x014f }
                r8 = 5;
                r7.sendEmptyMessage(r8);	 Catch:{ Exception -> 0x014f }
                goto L_0x00d7;
            L_0x01b6:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                if (r7 == 0) goto L_0x01e0;
            L_0x01be:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r7 = r7.trim();	 Catch:{ Exception -> 0x014f }
                r8 = "";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x01e0;
            L_0x01d1:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mUUID;	 Catch:{ Exception -> 0x014f }
                r8 = "1777777777777777";
                r7 = r7.equals(r8);	 Catch:{ Exception -> 0x014f }
                if (r7 != 0) goto L_0x01ec;
            L_0x01e0:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r7 = r7.mZidooUuidTool;	 Catch:{ Exception -> 0x014f }
                r8 = "1777777777777777";
                r7.writeUUID(r8);	 Catch:{ Exception -> 0x014f }
            L_0x01ec:
                r7 = com.zidoo.permissions.ZidooPhonePermissions.this;	 Catch:{ Exception -> 0x014f }
                r8 = "start net_disconnect";
                r7.mySystemOut(r8);	 Catch:{ Exception -> 0x014f }
            L_0x01f4:
                r8 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
                java.lang.Thread.sleep(r8);	 Catch:{ Exception -> 0x014f }
                goto L_0x0067;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.zidoo.permissions.ZidooPhonePermissions.5.run():void");
            }
        }).start();
    }

    private void check(final String uuid) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (ZidooPhonePermissions.getHosts()) {
                        if (ZidooPhonePermissions.this.mUUID == null || ZidooPhonePermissions.this.mUUID.trim().equals("") || !ZidooPhonePermissions.this.mUUID.equals(ZidooPhonePermissions.ERRORUUID)) {
                            ZidooPhonePermissions.this.putValue(ZidooPhonePermissions.this.mContext, "", uuid);
                            ZidooPhonePermissions.this.mZidooUuidTool.writeUUID(ZidooPhonePermissions.ERRORUUID);
                        }
                        throw new RuntimeException("ZidooPermissions uuid web error Please contact Zidoo Mr.Bob");
                    }
                    while (true) {
                        ZidooPhonePermissions.this.mySystemOut("check uuid = " + uuid);
                        if (ZidooPhonePermissions.this.netIsConnected(ZidooPhonePermissions.this.mContext)) {
                            String urlString = ZidooPhonePermissions.this.getCheck(uuid);
                            if (urlString != null) {
                                int status = new JSONObject(urlString).getInt(NotificationCompat.CATEGORY_STATUS);
                                ZidooPhonePermissions.this.mySystemOut("check status = " + status);
                                if (status == 200) {
                                    ZidooPhonePermissions.this.putValue(ZidooPhonePermissions.this.mContext, ZidooPhonePermissions.LOCALUUID, uuid);
                                    return;
                                } else if (status == -1) {
                                    ZidooPhonePermissions.this.putValue(ZidooPhonePermissions.this.mContext, "", uuid);
                                    ZidooPhonePermissions.this.mZidooUuidTool.writeUUID(ZidooPhonePermissions.ERRORUUID);
                                    ZidooPhonePermissions.this.mHandler.sendEmptyMessage(5);
                                    return;
                                } else {
                                    return;
                                }
                            }
                            try {
                                ZidooPhonePermissions.this.mySystemOut("check null");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ZidooPhonePermissions.this.mySystemOut("check net_disconnect");
                        }
                        Thread.sleep(5000);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }

    private String getKey() {
        String key = null;
        try {
            ArrayList<String> mount_list = new ArrayList();
            BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(AppConstant.PREFEREANCES_MOUNT).getInputStream()));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                String[] split = line.split(" ");
                if (split != null && split.length >= 3) {
                    if (split[2].equals("vfat") || split[2].equals("ntfs") || split[2].equals("ext4") || split[2].equals("ext3") || split[2].equals("ext2") || split[2].equals("extFat") || split[2].equals("exfat") || split[2].equals("hfsplus") || split[2].equals("fuse") || split[2].equals("ntfs3g") || split[2].equals("fuseblk")) {
                        mount_list.add(split[1]);
                    }
                }
            }
            int mount_size = mount_list.size();
            for (int i = 0; i < mount_size; i++) {
                File file = new File(new StringBuilder(String.valueOf((String) mount_list.get(i))).append(USTNAME).toString());
                if (file.exists() && file.canRead()) {
                    FileInputStream inputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReadernew = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(inputStreamReadernew);
                    key = br.readLine();
                    inputStream.close();
                    inputStreamReadernew.close();
                    br.close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private String getCheck(String uuid) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            HttpPost httpPost = new HttpPost("http://oldota.zidootv.com/index.php?m=Api&a=checkAuth");
            ArrayList<BasicNameValuePair> pairs = new ArrayList();
            pairs.add(new BasicNameValuePair("model", getWebDES(URLEncoder.encode(Build.MODEL, e.f))));
            pairs.add(new BasicNameValuePair(FavoriteDatabase.UUID, getWebDES(uuid)));
            mySystemOut("chenk pairs = " + pairs.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            mySystemOut("chenk code = " + code);
            if (code == 200 || code == HTTPStatus.PARTIAL_CONTENT) {
                String urlString = EntityUtils.toString(entity);
                mySystemOut("chenk urlString = " + urlString);
                urlString = getDecrypt(urlString);
                if (!(urlString == null || urlString.equals(""))) {
                    return urlString.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String startAuthorized(String key) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            HttpPost httpPost = new HttpPost("http://oldota.zidootv.com/index.php?m=Api&a=authPad");
            ArrayList<BasicNameValuePair> pairs = new ArrayList();
            pairs.add(new BasicNameValuePair("model", getWebDES(URLEncoder.encode(Build.MODEL, e.f))));
            pairs.add(new BasicNameValuePair("authkey", getWebDES(key)));
            pairs.add(new BasicNameValuePair("mac", getWebDES(getWifiMac(this.mContext))));
            mySystemOut("start pairs = " + pairs.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            mySystemOut("start code = " + code);
            if (code == 200 || code == HTTPStatus.PARTIAL_CONTENT) {
                String urlString = getDecrypt(EntityUtils.toString(entity));
                if (!(urlString == null || urlString.equals(""))) {
                    return urlString.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String startMacAuthorized() {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            HttpPost httpPost = new HttpPost("http://oldota.zidootv.com/index.php?m=Api&a=reauthPad");
            ArrayList<BasicNameValuePair> pairs = new ArrayList();
            pairs.add(new BasicNameValuePair("model", getWebDES(URLEncoder.encode(Build.MODEL, e.f))));
            pairs.add(new BasicNameValuePair("mac", getWebDES(getWifiMac(this.mContext))));
            mySystemOut("Mac pairs = " + pairs.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            mySystemOut("chenk mac code = " + code);
            if (code == 200 || code == HTTPStatus.PARTIAL_CONTENT) {
                String urlString = getDecrypt(EntityUtils.toString(entity));
                if (!(urlString == null || urlString.equals(""))) {
                    return urlString.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void enableDebug(boolean isEnable) {
        isPrintln = isEnable;
    }

    private void mySystemOut(String msg) {
        if (isPrintln) {
            System.out.println("bob--phone ==" + msg);
        }
    }

    private void putValue(Context context, String key, String value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putString(key, value);
        sp.commit();
    }

    private String getValue(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }

    private String getWebDES(String content) {
        try {
            return DESTool.encrypt(content, "flsjdfjzidoophonejoahdfohadf");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDecrypt(String content) {
        try {
            return DESTool.decrypt(content, "flsjdfjzidoophonejoahdfohadf");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getWifiMac(Context context) {
        String macAddress = "";
        try {
            WifiManager wifiMgr = (WifiManager) context.getSystemService("wifi");
            WifiInfo info = wifiMgr == null ? null : wifiMgr.getConnectionInfo();
            if (info != null) {
                return info.getMacAddress();
            }
            return macAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return macAddress;
        }
    }

    private boolean netIsConnected(Context context) {
        if (wifiIsConnected(context) || etherNetIsConnected(context)) {
            return true;
        }
        return false;
    }

    private boolean wifiIsConnected(Context context) {
        boolean z = true;
        try {
            NetworkInfo wifiInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            if (wifiInfo != null) {
                z = wifiInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    private boolean etherNetIsConnected(Context context) {
        try {
            NetworkInfo etherInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9);
            if (etherInfo != null) {
                return etherInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
