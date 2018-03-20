package com.zidoo.custom.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import com.zidoo.custom.file.ZidooFileTool;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZidooBitmapThread {
    private static final int BITMAPDATA = 0;
    private static ZidooBitmapThread mDownBitmapTool = null;
    private Context mContext = null;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(8);
    private Handler mHandler = null;
    private HashMap<String, ArrayList<DownBitmapListener>> mListenerMap = new HashMap();
    private String mLocalDir = "zpic";
    private String mLocalPicPath = null;

    public interface DownBitmapListener {
        void downBitmap(String str, Bitmap bitmap);
    }

    private class DownRunnable implements Runnable {
        private Bitmap bitmap = null;
        private Boolean isSave = null;
        private int mRound = -1;
        private String tag = null;
        private String url = null;

        public DownRunnable(String url, String tag, Boolean isSave, int round) {
            this.url = url;
            this.tag = tag;
            this.isSave = isSave;
            this.mRound = round;
        }

        public void run() {
            try {
                this.bitmap = ZBitmapCache.getBitmap(this.url);
                if (this.bitmap == null) {
                    if (this.tag != null) {
                        this.bitmap = ZidooBitmapSynchronize.getBitmapFile(new StringBuilder(String.valueOf(ZidooBitmapThread.this.mLocalPicPath)).append(ZidooBitmapTool.splitLastName(this.tag, this.url)).toString());
                    } else {
                        this.bitmap = ZidooBitmapSynchronize.getBitmapFile(new StringBuilder(String.valueOf(ZidooBitmapThread.this.mLocalPicPath)).append(ZidooBitmapTool.splitLastName(this.url)).toString());
                    }
                    if (this.bitmap == null) {
                        this.bitmap = ZidooBitmapSynchronize.downloadBitmap(this.url, true, 921600);
                        if (this.bitmap != null) {
                            if (this.mRound > 0) {
                                this.bitmap = ZidooBitmapTool.getRoundedCornerBitmap(this.bitmap, (float) this.mRound);
                            }
                            if (this.tag != null) {
                                File file = new File(ZidooBitmapThread.this.mLocalPicPath);
                                if (file.exists() && file.isDirectory()) {
                                    File[] filelist = file.listFiles();
                                    if (filelist != null) {
                                        for (int i = 0; i < filelist.length; i++) {
                                            if (filelist[i].getName().startsWith(this.tag)) {
                                                filelist[i].delete();
                                            }
                                        }
                                    }
                                }
                                if (this.isSave.booleanValue()) {
                                    ZidooBitmapSynchronize.saveBitmap(this.bitmap, new StringBuilder(String.valueOf(ZidooBitmapThread.this.mLocalPicPath)).append(ZidooBitmapTool.splitLastName(this.tag, this.url)).toString());
                                }
                            } else if (this.isSave.booleanValue()) {
                                ZidooBitmapSynchronize.saveBitmap(this.bitmap, new StringBuilder(String.valueOf(ZidooBitmapThread.this.mLocalPicPath)).append(ZidooBitmapTool.splitLastName(this.url)).toString());
                            }
                        }
                    }
                    if (this.bitmap != null) {
                        ZBitmapCache.putBitmap(this.url, this.bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ZidooBitmapThread.this.mHandler.obtainMessage(0, this).sendToTarget();
        }
    }

    public static ZidooBitmapThread getInstance(Context context) {
        if (mDownBitmapTool == null) {
            mDownBitmapTool = new ZidooBitmapThread(context);
        }
        return mDownBitmapTool;
    }

    public static ZidooBitmapThread getInstance(Context context, String localDirName) {
        if (mDownBitmapTool == null) {
            mDownBitmapTool = new ZidooBitmapThread(context, localDirName);
        }
        return mDownBitmapTool;
    }

    public ZidooBitmapThread setBitmapSaveDirectory(String directoryPicPath) {
        if (!(directoryPicPath == null || directoryPicPath.trim().equals(""))) {
            File file = new File(directoryPicPath);
            if (file.exists() && file.isFile()) {
                throw new RuntimeException("Save bitmap path must directory .... ");
            }
            if (!file.exists()) {
                if (file.mkdirs()) {
                    ZidooFileTool.execMethod(directoryPicPath);
                } else {
                    throw new RuntimeException("Save bitmap path can not writer .... ");
                }
            }
            this.mLocalPicPath = directoryPicPath;
        }
        return mDownBitmapTool;
    }

    public static void relese() {
        mDownBitmapTool = null;
        ZBitmapCache.destroy();
    }

    public ZidooBitmapThread(Context mContext) {
        this.mContext = mContext;
        init();
    }

    public ZidooBitmapThread(Context mContext, String localDirName) {
        this.mContext = mContext;
        if (!(localDirName == null || localDirName.trim().equals(""))) {
            this.mLocalDir = localDirName;
        }
        init();
    }

    private void init() {
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        DownRunnable mDownRunnable = msg.obj;
                        if (ZidooBitmapThread.this.mListenerMap.containsKey(mDownRunnable.url)) {
                            ArrayList<DownBitmapListener> list = (ArrayList) ZidooBitmapThread.this.mListenerMap.get(mDownRunnable.url);
                            ZidooBitmapThread.this.mListenerMap.remove(mDownRunnable.url);
                            for (int i = 0; i < list.size(); i++) {
                                ((DownBitmapListener) list.get(i)).downBitmap(mDownRunnable.url, mDownRunnable.bitmap);
                            }
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        if (this.mLocalPicPath == null) {
            this.mLocalPicPath = ZidooFileTool.getDataDir(this.mContext, this.mLocalDir);
        }
    }

    public void startLoadingBitmap(String url, String tag, Boolean isSave, int round, DownBitmapListener downBitmapListener) {
        if (downBitmapListener != null) {
            if (url == null) {
                downBitmapListener.downBitmap("", null);
            } else if (this.mListenerMap.containsKey(url)) {
                ((ArrayList) this.mListenerMap.get(url)).add(downBitmapListener);
            } else {
                ArrayList<DownBitmapListener> list = new ArrayList();
                list.add(downBitmapListener);
                this.mListenerMap.put(url, list);
                this.mExecutorService.execute(new DownRunnable(url, tag, isSave, round));
            }
        }
    }

    public void startLoadingBitmap(String url, DownBitmapListener downBitmapListener) {
        startLoadingBitmap(url, null, Boolean.valueOf(false), -1, downBitmapListener);
    }

    public void startLoadingBitmap(String url, Boolean isSave, DownBitmapListener downBitmapListener) {
        startLoadingBitmap(url, null, isSave, -1, downBitmapListener);
    }
}
