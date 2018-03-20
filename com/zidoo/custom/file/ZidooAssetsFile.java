package com.zidoo.custom.file;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ZidooAssetsFile {
    private static final int COPYEND = 0;
    private Context mContext = null;
    private CopyAssetsFileListener mCopyAssetsFileListener = null;
    private Handler mHandler = null;

    class AnonymousClass3 implements Runnable {
        private final /* synthetic */ String val$assetsName;
        private final /* synthetic */ Context val$context;
        private final /* synthetic */ boolean val$isDelete;
        private final /* synthetic */ boolean val$isZip;
        private final /* synthetic */ String val$savePath;

        AnonymousClass3(Context context, String str, String str2, boolean z, boolean z2) {
            this.val$context = context;
            this.val$savePath = str;
            this.val$assetsName = str2;
            this.val$isZip = z;
            this.val$isDelete = z2;
        }

        public void run() {
            ZidooAssetsFile.copyAssetsFile(this.val$context, this.val$savePath, this.val$assetsName, this.val$isZip, this.val$isDelete);
        }
    }

    public interface CopyAssetsFileListener {
        void end(boolean z);

        void start();
    }

    public ZidooAssetsFile(Context mContext, CopyAssetsFileListener copyAssetsFileListener) {
        this.mContext = mContext;
        this.mCopyAssetsFileListener = copyAssetsFileListener;
        init();
    }

    private void init() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (ZidooAssetsFile.this.mCopyAssetsFileListener != null) {
                            ZidooAssetsFile.this.mCopyAssetsFileListener.end(((Boolean) msg.obj).booleanValue());
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public void startCopy(final Context context, final String savePath, final String assetsName) {
        if (this.mCopyAssetsFileListener != null) {
            this.mCopyAssetsFileListener.start();
        }
        new Thread(new Runnable() {
            public void run() {
                boolean isSuccess = false;
                try {
                    isSuccess = ZidooAssetsFile.copyAssetsFile(context, savePath, assetsName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ZidooAssetsFile.this.mHandler.obtainMessage(0, Boolean.valueOf(isSuccess)).sendToTarget();
            }
        }).start();
    }

    public static boolean copyAssetsFile(Context context, String savePath, String assetsName) {
        boolean z = false;
        try {
            z = copyAssetsFile(context, assetsName, savePath, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    public static boolean copyAssetsFile(Context context, String savePath, String assetsName, boolean isZip) {
        boolean z = false;
        try {
            z = copyAssetsFile(context, assetsName, savePath, isZip, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    public static boolean copyAssetsFile(Context context, String savePath, String assetsName, boolean isZip, boolean isDelete) {
        try {
            if (retrieveApkFromAssets(context, assetsName, new StringBuilder(String.valueOf(savePath)).append(File.separator).append(assetsName).toString())) {
                if (isZip && ZidooZipTool.unZip(new StringBuilder(String.valueOf(savePath)).append(File.separator).append(assetsName).toString(), savePath) && isDelete) {
                    new File(new StringBuilder(String.valueOf(savePath)).append(File.separator).append(assetsName).toString()).delete();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void copyAssetsFileRunnable(Context context, String savePath, String assetsName) {
        copyAssetsFileRunnable(context, savePath, assetsName, false);
    }

    public static void copyAssetsFileRunnable(Context context, String savePath, String assetsName, boolean isZip) {
        copyAssetsFileRunnable(context, savePath, assetsName, isZip, false);
    }

    public static void copyAssetsFileRunnable(Context context, String savePath, String assetsName, boolean isZip, boolean isDelete) {
        new Thread(new AnonymousClass3(context, savePath, assetsName, isZip, isDelete)).start();
    }

    private static boolean retrieveApkFromAssets(Context context, String fileName, String path) {
        try {
            InputStream is = context.getAssets().open(fileName);
            String dir = path.substring(0, path.lastIndexOf("/"));
            if (!new File(dir).exists()) {
                new File(dir).mkdirs();
            }
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[4096];
            while (true) {
                int i = is.read(temp);
                if (i <= 0) {
                    fos.flush();
                    fos.close();
                    is.close();
                    return true;
                }
                fos.write(temp, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object getAssetsObject(Context mContext, String fileName) {
        Object obj = null;
        try {
            ObjectInputStream oin = new ObjectInputStream(mContext.getAssets().open(fileName));
            obj = oin.readObject();
            oin.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return obj;
        }
    }
}
