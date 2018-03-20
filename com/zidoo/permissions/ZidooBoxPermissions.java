package com.zidoo.permissions;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import com.zidoo.custom.log.MyLog;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import zidoo.browse.BrowseConstant;

public class ZidooBoxPermissions {
    private static final String APKBACKACTON = "zidoo.apk.result.action";
    private static final String APKRESULTACTON = "zidoo.apk.start.check.action";
    private static final String APKRESULTACTON_KAIBOER = "zidoo.apk.start.check.kaiboer.action";
    private static final String CHECK_BOOT_WIZARD = "boot_wizard";
    private static final String CHECK_BOX_MODEL = "BoxModel";
    private static final String CHECK_IS_AVAILABLEMODEL = "isAvailableModel";
    private static final String CHECK_IS_BLURAY = "isBluray";
    private static final String CHECK_IS_CANUPDATE = "isupdateenable";
    private static final String CHECK_IS_CPU_FAN = "isCPUFan";
    private static final String CHECK_IS_H3_MODEL = "isH3Model";
    private static final String CHECK_IS_H6_MODEL = "isH6Model";
    private static final String CHECK_IS_INSTALLUIAPP = "isInstallUIApp";
    private static final String CHECK_IS_MLOGIC_905_MODEL = "isMlogic905XModel";
    private static final String CHECK_IS_MLOGIC_MODEL = "isMlogicModel";
    private static final String CHECK_IS_MSTART_MODEL = "isMstarModel";
    private static final String CHECK_IS_PERMISSIONS = "isPermissions";
    private static final String CHECK_IS_REALTEK_MODEL = "isRealtekModel";
    private static final String CHECK_IS_ROCK_3229_MODEL = "isRock3229Model";
    private static final String CHECK_IS_ROCK_3328_MODEL = "isRock3328Model";
    private static final String CHECK_IS_ROCK_MODEL = "isRockModel";
    private static final String CHECK_IS_WORLDMODEL = "isWorldModel";
    private static int CURRENT_PERMISSIONS = 0;
    public static final int DAUFLAUT = 0;
    public static final int KAIBOER = 1;
    private static final int RESULTDATA = 0;
    private static final int RESULTDATAOUTTIMTE = 20000;
    private static final int RESULTWTODATA = 1;
    private static boolean isCheckAuthorized = false;
    private boolean isFramework = false;
    private Context mContext = null;
    private Handler mHandler = null;
    private BroadcastReceiver mReceiver = null;

    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ int[] val$drawID;
        private final /* synthetic */ Context val$mContext;

        AnonymousClass3(Context context, int[] iArr) {
            this.val$mContext = context;
            this.val$drawID = iArr;
        }

        public void run() {
            try {
                String[] md5 = ZidooBoxPermissions.getDrawMd5(this.val$mContext, this.val$drawID);
                for (String str : md5) {
                    MyLog.fv("check md5 = " + str);
                }
            } catch (Exception e) {
            }
        }
    }

    class AnonymousClass4 implements Runnable {
        private final /* synthetic */ int[] val$drawID;
        private final /* synthetic */ Context val$mContext;
        private final /* synthetic */ String[] val$md5;

        AnonymousClass4(int[] iArr, Context context, String[] strArr) {
            this.val$drawID = iArr;
            this.val$mContext = context;
            this.val$md5 = strArr;
        }

        public void run() {
            int length = this.val$drawID.length;
            int i = 0;
            while (i < length) {
                if (ZidooBoxPermissions.checkMd5(this.val$mContext, this.val$drawID[i], this.val$md5[i])) {
                    i++;
                } else {
                    throw new RuntimeException("ZidooBoxPermissions checkMd5 error Please contact Zidoo Mr.Bob");
                }
            }
        }
    }

    class AnonymousClass5 implements Runnable {
        private final /* synthetic */ Bitmap val$bitmap;

        AnonymousClass5(Bitmap bitmap) {
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

    public static void initPermissions(int flag) {
        CURRENT_PERMISSIONS = flag;
    }

    public static void checkAppPermissions() {
        if (!isCheckAuthorized) {
            throw new RuntimeException("ZidooBoxPermissions error Please contact Zidoo Mr.Bob");
        }
    }

    public ZidooBoxPermissions(Context mContext) {
        this.mContext = mContext;
        MyLog.fv(" BoxPermissions v1.0.8 - 2* 20 * 1000 ");
        initData();
    }

    private void initData() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        MyLog.fv("framework first = " + ZidooBoxPermissions.this.isFramework);
                        ZidooBoxPermissions.this.mHandler.removeMessages(1);
                        ZidooBoxPermissions.this.mHandler.sendEmptyMessageDelayed(1, 20000);
                        ZidooBoxPermissions.this.startSendApkResult();
                        return;
                    case 1:
                        MyLog.fv("framework two = " + ZidooBoxPermissions.this.isFramework);
                        if (!ZidooBoxPermissions.this.isFramework) {
                            ZidooBoxPermissions.this.release();
                            throw new RuntimeException("ZidooBoxPermissions framework anr error Please contact Zidoo Mr.Bob");
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                ZidooBoxPermissions.this.mHandler.removeMessages(0);
                ZidooBoxPermissions.this.mHandler.removeMessages(1);
                ZidooBoxPermissions.this.isFramework = true;
                if (arg1.getAction().equals(ZidooBoxPermissions.APKBACKACTON)) {
                    boolean result = arg1.getBooleanExtra(BrowseConstant.EXTRA_RESULT, true);
                    MyLog.fv("check rusult" + result);
                    ZidooBoxPermissions.this.release();
                    if (!result) {
                        throw new RuntimeException("ZidooBoxPermissions check rusult error Please contact Zidoo Mr.Bob");
                    }
                }
            }
        };
        inItBroadCast();
    }

    private void inItBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(APKBACKACTON);
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    private void sendAPkResult() {
        if (getHosts()) {
            throw new RuntimeException("ZidooBoxPermissions host error Please contact Zidoo Mr.Bob");
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessageDelayed(0, 20000);
        this.isFramework = false;
        startSendApkResult();
        isCheckAuthorized = true;
    }

    private void startSendApkResult() {
        String action = APKRESULTACTON;
        if (CURRENT_PERMISSIONS == 1) {
            action = APKRESULTACTON_KAIBOER;
        }
        this.mContext.sendBroadcast(new Intent(action));
    }

    private void release() {
        try {
            this.mHandler.removeMessages(0);
            this.mHandler.removeMessages(1);
            this.mContext.unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
        }
    }

    public boolean checkPermissionsByModel(Context mContext, int[] drawID, String[] md5) {
        if (isAvailableMODEL(mContext)) {
            initZidooBoxPermissions(mContext, drawID, md5);
            sendAPkResult();
            return true;
        }
        throw new RuntimeException("ZidooBoxPermissions model error Please contact Zidoo Mr.Bob");
    }

    public boolean checkPermissionsByUI(Context mContext, int[] drawID, String[] md5) {
        if (isInstallUIApp(mContext)) {
            initZidooBoxPermissions(mContext, drawID, md5);
            sendAPkResult();
            return true;
        }
        throw new RuntimeException("ZidooBoxPermissions ui error Please contact Zidoo Mr.Bob");
    }

    public boolean checkPermissionsByAll(Context mContext, int[] drawID, String[] md5) {
        if (isPermissions(mContext)) {
            initZidooBoxPermissions(mContext, drawID, md5);
            sendAPkResult();
            return true;
        }
        throw new RuntimeException("ZidooBoxPermissions all error Please contact Zidoo Mr.Bob");
    }

    public void checkPermissionsOnlyPictrue(Context mContext, int[] drawID, String[] md5) {
        initZidooBoxPermissions(mContext, drawID, md5);
        sendAPkResult();
    }

    public void checkPermissionsBluray(Context mContext, int[] drawID, String[] md5) {
        if (isBluray(mContext)) {
            initZidooBoxPermissions(mContext, drawID, md5);
            sendAPkResult();
            return;
        }
        throw new RuntimeException("ZidooBoxPermissions bluray error Please contact Zidoo Mr.Bob");
    }

    public static void getMd5(Context mContext, int[] drawID) {
        new Thread(new AnonymousClass3(mContext, drawID)).start();
    }

    private static boolean isPermissions(Context context) {
        return isCheckValue(context, CHECK_IS_PERMISSIONS, false);
    }

    private static boolean isAvailableMODEL(Context context) {
        return isCheckValue(context, CHECK_IS_AVAILABLEMODEL, false);
    }

    private static boolean isInstallUIApp(Context context) {
        return isCheckValue(context, CHECK_IS_INSTALLUIAPP, false);
    }

    public static boolean isWorldModel(Context context) {
        return isCheckValue(context, CHECK_IS_WORLDMODEL, false);
    }

    public static boolean isMstarModel(Context context) {
        return isCheckValue(context, CHECK_IS_MSTART_MODEL, false);
    }

    public static boolean isH3Model(Context context) {
        return isCheckValue(context, CHECK_IS_H3_MODEL, false);
    }

    public static boolean isH6Model(Context context) {
        return isCheckValue(context, CHECK_IS_H6_MODEL, false);
    }

    public static boolean isRockModel(Context context) {
        return isCheckValue(context, CHECK_IS_ROCK_MODEL, false);
    }

    public static boolean isRock3229Model(Context context) {
        return isCheckValue(context, CHECK_IS_ROCK_3229_MODEL, false);
    }

    public static boolean isRock3328Model(Context context) {
        return isCheckValue(context, CHECK_IS_ROCK_3328_MODEL, false);
    }

    public static boolean isMlogicModel(Context context) {
        return isCheckValue(context, CHECK_IS_MLOGIC_MODEL, false);
    }

    public static boolean isMlogic905XModel(Context context) {
        return isCheckValue(context, CHECK_IS_MLOGIC_905_MODEL, false);
    }

    public static boolean isRealtekModel(Context context) {
        return isCheckValue(context, CHECK_IS_REALTEK_MODEL, false);
    }

    public static int getBoxModel(Context context) {
        return getCheckValue(context, CHECK_BOX_MODEL, null);
    }

    private static int getCheckValue(Context mContext, String key, String[] valse) {
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri mUri = ContentUris.withAppendedId(Uri.parse("content://com.zidoo.busybox.check.provide/"), 1);
            if (mUri != null) {
                ContentValues contentValues = new ContentValues();
                if (valse == null) {
                    valse = new String[]{""};
                }
                return contentResolver.update(mUri, contentValues, key, valse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.fv("getCheckValue = com.zidoo.busybox connect error connect Mr.bob");
        return 0;
    }

    private static boolean isCheckValue(Context mContext, String key, boolean defaultValue) {
        boolean isResult = defaultValue;
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri mUri = ContentUris.withAppendedId(Uri.parse("content://com.zidoo.busybox.check.provide/"), 1);
            if (mUri != null) {
                return contentResolver.delete(mUri, key, null) == 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.fv("isCheckModel = com.zidoo.busybox connect error connect Mr.bob");
        return isResult;
    }

    public static boolean isFanControl(Context context) {
        return isCheckValue(context, CHECK_IS_CPU_FAN, false);
    }

    public static boolean isBluray(Context context) {
        return isCheckValue(context, CHECK_IS_BLURAY, false);
    }

    public static boolean isUpwizard(Context context) {
        return isCheckValue(context, CHECK_BOOT_WIZARD, false);
    }

    public static void setUpwizard(Context context, int valuse) {
        getCheckValue(context, CHECK_BOOT_WIZARD, new String[]{new StringBuilder(String.valueOf(valuse)).toString()});
    }

    public static boolean isUpdateEnable(Context context) {
        return isCheckValue(context, CHECK_IS_CANUPDATE, true);
    }

    private static void initZidooBoxPermissions(Context mContext, int[] drawID, String[] md5) {
        if (drawID != null && md5 != null) {
            startChechMd5(mContext, drawID, md5);
        }
    }

    private static void startChechMd5(Context mContext, int[] drawID, String[] md5) {
        new Thread(new AnonymousClass4(drawID, mContext, md5)).start();
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
        new Thread(new AnonymousClass5(bitmap)).start();
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
            } while (!line.toString().contains("zidoo.tv"));
            isError = true;
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isError;
    }
}
