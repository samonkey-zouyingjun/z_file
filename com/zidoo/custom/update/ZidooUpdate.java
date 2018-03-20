package com.zidoo.custom.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.umeng.common.util.e;
import com.zidoo.custom.log.MyLog;
import com.zidoo.custom.net.ZidooNetDataTool;
import com.zidoo.custom.net.ZidooNetStatusTool;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import zidoo.browse.BrowseConstant;
import zidoo.http.HTTP;
import zidoo.http.HTTPStatus;

public class ZidooUpdate {
    private static final String APK_NAME = "update.apk";
    public static final int CHINA = 1;
    static final int DIALOG = 4;
    public static final String Install_hit_pg = "com.zidoo.busybox";
    public static final String Install_hit_pg_old = "com.android.install.apk";
    private static final String PIC_NAME = "bg.png";
    private static final String PUSHPICSHARE = "pushpicshare";
    static final int UPDATE = 0;
    static final int UPDATE_ERROR = 2;
    static final int UPDATE_OVER = 3;
    static final int UPDATE_VIEW = 1;
    private static final String URL_HEAND_CHINA = "http://oldota.zidootv.com/";
    private static final String URL_HEAND_WORLD = "http://update.zidoo.tv/";
    public static final int WORLD = 0;
    private static long gb = 1073741824;
    private static long kb = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    private static long mb = PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
    private String UPDATA_URL = "index.php?m=Api&pn=";
    boolean isDownPic = false;
    private String[] language;
    private Context mContext = null;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        UpdateInfo updateInfo = msg.obj;
                        if (updateInfo != null) {
                            ZidooUpdate.this.mInfo = updateInfo;
                            if (updateInfo.install == 0) {
                                ZidooUpdate.this.updateDialog(updateInfo);
                                return;
                            } else {
                                ZidooUpdate.this.startDownApk(updateInfo);
                                return;
                            }
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                case 1:
                    try {
                        DownApkInfo downApkInfo = msg.obj;
                        if (downApkInfo != null && ZidooUpdate.this.mUpdateView != null) {
                            ZidooUpdate.this.mUpdateView.tvSpeed.setText(downApkInfo.dwonSpeed);
                            ZidooUpdate.this.mUpdateView.tvSize.setText(downApkInfo.downSize + "/" + downApkInfo.totalSize);
                            ZidooUpdate.this.mUpdateView.tvPercent.setText(downApkInfo.downPercent);
                            ZidooUpdate.this.mUpdateView.progressBar.setProgress(downApkInfo.downPercent);
                            ZidooUpdate.this.mUpdateView.progressBar.setSecondaryProgress(downApkInfo.downPercent);
                            return;
                        }
                        return;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
                case 2:
                    try {
                        if (ZidooUpdate.this.mUpdateView != null) {
                            ZidooUpdate.this.mUpdateView.state = 1;
                            ZidooUpdate.this.mUpdateView.progressBar.setProgress(0);
                            ZidooUpdate.this.mUpdateView.btUpdate.setText(ZidooUpdate.this.language[5]);
                            ZidooUpdate.this.mUpdateView.btCancel.setText(ZidooUpdate.this.language[6]);
                            ZidooUpdate.this.mUpdateView.tvError.setVisibility(0);
                            return;
                        }
                        return;
                    } catch (Exception e22) {
                        e22.printStackTrace();
                        return;
                    }
                case 3:
                    try {
                        if (ZidooUpdate.this.mUpdateView != null) {
                            ZidooUpdate.this.mUpdateView.state = 2;
                            ZidooUpdate.this.mUpdateView.btCancel.performClick();
                            return;
                        }
                        return;
                    } catch (Exception e222) {
                        e222.printStackTrace();
                        return;
                    }
                case 4:
                    try {
                        ZidooUpdate.this.updateDialog(ZidooUpdate.this.mInfo);
                        return;
                    } catch (Exception e2222) {
                        e2222.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    };
    float mHeightRatio = 1.0f;
    private String mHintModel = null;
    private String mHintPname = null;
    private String mHintVersion = null;
    private UpdateInfo mInfo = null;
    private File mInstallFile = null;
    boolean mIsDowning = true;
    private boolean mIsGetPic = false;
    private PackageManager mPackageManager = null;
    private UpdateView mUpdateView = null;
    float mWidthRatio = 1.0f;

    class DownApkInfo {
        int downPercent = 0;
        String downSize = "0 KB";
        String dwonSpeed = "0 KB/s";
        String totalSize = "0 MB";

        DownApkInfo() {
        }

        public String toString() {
            return "DownApkInfo [downPercent=" + this.downPercent + ", dwonSpeed=" + this.dwonSpeed + ", totalSize=" + this.totalSize + ", dwonSize=" + this.downSize + "]";
        }
    }

    class DownApkRu implements Runnable {
        private String apkUrl = "";
        private long currentLength = 0;
        private long lastLength = 0;
        int mEveryTime = 1;
        Timer speedTimer = new Timer();
        TimerTask speedTimerTask = null;
        private long totalLength = 0;

        public DownApkRu(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r24 = this;
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;
            r20 = r0;
            r21 = 1;
            r0 = r21;
            r1 = r20;
            r1.mIsDowning = r0;
            r15 = 0;
            r5 = 0;
            r0 = r24;
            r0 = r0.apkUrl;	 Catch:{ Exception -> 0x026b }
            r18 = r0;
            r19 = r18.trim();	 Catch:{ Exception -> 0x026b }
            r10 = com.zidoo.custom.update.ZidooUpdate.toUtf8Strings(r19);	 Catch:{ Exception -> 0x026b }
            r9 = new java.net.URL;	 Catch:{ Exception -> 0x026b }
            r9.<init>(r10);	 Catch:{ Exception -> 0x026b }
            r13 = r9.openConnection();	 Catch:{ Exception -> 0x026b }
            r13 = (java.net.HttpURLConnection) r13;	 Catch:{ Exception -> 0x026b }
            r20 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
            r0 = r20;
            r13.setConnectTimeout(r0);	 Catch:{ Exception -> 0x026b }
            r20 = 20000; // 0x4e20 float:2.8026E-41 double:9.8813E-320;
            r0 = r20;
            r13.setReadTimeout(r0);	 Catch:{ Exception -> 0x026b }
            r14 = r13.getInputStream();	 Catch:{ Exception -> 0x026b }
            r20 = r13.getContentLength();	 Catch:{ Exception -> 0x026b }
            r0 = r20;
            r0 = (long) r0;	 Catch:{ Exception -> 0x026b }
            r20 = r0;
            r0 = r20;
            r2 = r24;
            r2.totalLength = r0;	 Catch:{ Exception -> 0x026b }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x026b }
            r20 = r0;
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x026b }
            r21 = r0;
            r21 = r21.mContext;	 Catch:{ Exception -> 0x026b }
            r22 = "update.apk";
            r4 = r20.getSDPath(r21, r22);	 Catch:{ Exception -> 0x026b }
            r17 = new java.io.File;	 Catch:{ Exception -> 0x026b }
            r0 = r17;
            r0.<init>(r4);	 Catch:{ Exception -> 0x026b }
            if (r17 == 0) goto L_0x0073;
        L_0x006a:
            r20 = r17.exists();	 Catch:{ Exception -> 0x026b }
            if (r20 == 0) goto L_0x0073;
        L_0x0070:
            r17.delete();	 Catch:{ Exception -> 0x026b }
        L_0x0073:
            r17.createNewFile();	 Catch:{ Exception -> 0x026b }
            r20 = 1;
            r21 = 0;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r0.setReadable(r1, r2);	 Catch:{ Exception -> 0x026b }
            r20 = 1;
            r21 = 0;
            r0 = r17;
            r1 = r20;
            r2 = r21;
            r0.setWritable(r1, r2);	 Catch:{ Exception -> 0x026b }
            r16 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x026b }
            r16.<init>(r17);	 Catch:{ Exception -> 0x026b }
            r6 = new java.io.BufferedInputStream;	 Catch:{ Exception -> 0x026e, all -> 0x0266 }
            r20 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r0 = r20;
            r6.<init>(r14, r0);	 Catch:{ Exception -> 0x026e, all -> 0x0266 }
            if (r6 == 0) goto L_0x0111;
        L_0x00a0:
            r20 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            r0 = r20;
            r7 = new byte[r0];	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r8 = -1;
            r20 = 0;
            r0 = r20;
            r2 = r24;
            r2.currentLength = r0;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r24.updateData();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
        L_0x00b2:
            r8 = r6.read(r7);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = -1;
            r0 = r20;
            if (r8 != r0) goto L_0x011f;
        L_0x00bc:
            r0 = r24;
            r0 = r0.currentLength;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r24;
            r0 = r0.totalLength;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r22 = r0;
            r20 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1));
            if (r20 != 0) goto L_0x023f;
        L_0x00cc:
            r12 = new java.io.File;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r12.<init>(r4);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            if (r12 == 0) goto L_0x0229;
        L_0x00d3:
            r20 = r12.exists();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            if (r20 == 0) goto L_0x0229;
        L_0x00d9:
            r24.cancel();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r24.update();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 3;
            r20.sendEmptyMessage(r21);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mInfo;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            r0 = r0.install;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            if (r20 != 0) goto L_0x01a9;
        L_0x0102:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mContext;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            com.zidoo.custom.update.ZidooUpdate.installFile(r12, r0);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
        L_0x0111:
            if (r6 == 0) goto L_0x0116;
        L_0x0113:
            r6.close();	 Catch:{ Exception -> 0x025a }
        L_0x0116:
            if (r16 == 0) goto L_0x025e;
        L_0x0118:
            r16.close();	 Catch:{ Exception -> 0x025a }
            r5 = r6;
            r15 = r16;
        L_0x011e:
            return;
        L_0x011f:
            r20 = 0;
            r0 = r16;
            r1 = r20;
            r0.write(r7, r1, r8);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = r0.currentLength;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = (long) r8;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r22 = r0;
            r20 = r20 + r22;
            r0 = r20;
            r2 = r24;
            r2.currentLength = r0;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mIsDowning;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            if (r20 != 0) goto L_0x00b2;
        L_0x0147:
            r24.cancel();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            if (r6 == 0) goto L_0x014f;
        L_0x014c:
            r6.close();	 Catch:{ Exception -> 0x0173, all -> 0x01df }
        L_0x014f:
            if (r16 == 0) goto L_0x0154;
        L_0x0151:
            r16.close();	 Catch:{ Exception -> 0x0173, all -> 0x01df }
        L_0x0154:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 3;
            r20.sendEmptyMessage(r21);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            if (r6 == 0) goto L_0x016a;
        L_0x0167:
            r6.close();	 Catch:{ Exception -> 0x01a4 }
        L_0x016a:
            if (r16 == 0) goto L_0x016f;
        L_0x016c:
            r16.close();	 Catch:{ Exception -> 0x01a4 }
        L_0x016f:
            r5 = r6;
            r15 = r16;
            goto L_0x011e;
        L_0x0173:
            r11 = move-exception;
            r11.printStackTrace();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            goto L_0x0154;
        L_0x0178:
            r11 = move-exception;
            r5 = r6;
            r15 = r16;
        L_0x017c:
            r11.printStackTrace();	 Catch:{ all -> 0x0263 }
            r24.cancel();	 Catch:{ all -> 0x0263 }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ all -> 0x0263 }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ all -> 0x0263 }
            r20 = r0;
            r21 = 2;
            r20.sendEmptyMessage(r21);	 Catch:{ all -> 0x0263 }
            if (r5 == 0) goto L_0x0198;
        L_0x0195:
            r5.close();	 Catch:{ Exception -> 0x019e }
        L_0x0198:
            if (r15 == 0) goto L_0x011e;
        L_0x019a:
            r15.close();	 Catch:{ Exception -> 0x019e }
            goto L_0x011e;
        L_0x019e:
            r11 = move-exception;
            r11.printStackTrace();
            goto L_0x011e;
        L_0x01a4:
            r11 = move-exception;
            r11.printStackTrace();
            goto L_0x016f;
        L_0x01a9:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mInfo;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            r0 = r0.install;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 1;
            r0 = r20;
            r1 = r21;
            if (r0 != r1) goto L_0x01ee;
        L_0x01c1:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0.mInstallFile = r12;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 4;
            r20.sendEmptyMessage(r21);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            goto L_0x0111;
        L_0x01df:
            r20 = move-exception;
            r5 = r6;
            r15 = r16;
        L_0x01e3:
            if (r5 == 0) goto L_0x01e8;
        L_0x01e5:
            r5.close();	 Catch:{ Exception -> 0x0255 }
        L_0x01e8:
            if (r15 == 0) goto L_0x01ed;
        L_0x01ea:
            r15.close();	 Catch:{ Exception -> 0x0255 }
        L_0x01ed:
            throw r20;
        L_0x01ee:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mInfo;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            r0 = r0.install;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 2;
            r0 = r20;
            r1 = r21;
            if (r0 != r1) goto L_0x0111;
        L_0x0206:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mContext;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            r20 = com.zidoo.custom.update.ZidooUpdate.hitInstallApk(r12, r0);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            if (r20 != 0) goto L_0x0111;
        L_0x0218:
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r20 = r20.mContext;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r20;
            com.zidoo.custom.update.ZidooUpdate.installFile(r12, r0);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            goto L_0x0111;
        L_0x0229:
            r24.cancel();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 2;
            r20.sendEmptyMessage(r21);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            goto L_0x0111;
        L_0x023f:
            r24.cancel();	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r0 = r24;
            r0 = com.zidoo.custom.update.ZidooUpdate.this;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r0 = r20;
            r0 = r0.mHandler;	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            r20 = r0;
            r21 = 2;
            r20.sendEmptyMessage(r21);	 Catch:{ Exception -> 0x0178, all -> 0x01df }
            goto L_0x0111;
        L_0x0255:
            r11 = move-exception;
            r11.printStackTrace();
            goto L_0x01ed;
        L_0x025a:
            r11 = move-exception;
            r11.printStackTrace();
        L_0x025e:
            r5 = r6;
            r15 = r16;
            goto L_0x011e;
        L_0x0263:
            r20 = move-exception;
            goto L_0x01e3;
        L_0x0266:
            r20 = move-exception;
            r15 = r16;
            goto L_0x01e3;
        L_0x026b:
            r11 = move-exception;
            goto L_0x017c;
        L_0x026e:
            r11 = move-exception;
            r15 = r16;
            goto L_0x017c;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.zidoo.custom.update.ZidooUpdate.DownApkRu.run():void");
        }

        private void cancel() {
            if (this.speedTimerTask != null) {
                this.speedTimerTask.cancel();
                this.speedTimerTask = null;
            }
        }

        private void updateData() {
            cancel();
            this.speedTimerTask = new TimerTask() {
                public void run() {
                    try {
                        DownApkRu.this.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            this.speedTimer.schedule(this.speedTimerTask, 1, (long) (this.mEveryTime * 1000));
        }

        private void update() {
            DownApkInfo downApkInfo = new DownApkInfo();
            int momentnum = (int) ((((float) this.currentLength) / ((float) this.totalLength)) * 100.0f);
            String speed_str = new StringBuilder(String.valueOf(ZidooUpdate.toSize((this.currentLength - this.lastLength) / ((long) this.mEveryTime)))).append("/s").toString();
            this.lastLength = this.currentLength;
            downApkInfo.downPercent = momentnum;
            downApkInfo.dwonSpeed = speed_str;
            downApkInfo.totalSize = ZidooUpdate.toSize(this.totalLength);
            downApkInfo.downSize = ZidooUpdate.toSize(this.currentLength);
            ZidooUpdate.this.mHandler.sendMessage(ZidooUpdate.this.mHandler.obtainMessage(1, downApkInfo));
        }
    }

    private class Unit1 {
        int h120;
        int h200;
        int h22;
        int h30;
        int h480;
        int h54;
        int h832;
        int h90;
        int s10;
        public float s19;
        public float s22;
        public float s25;
        public float s28;
        int w120;
        int w20;
        int w25;
        int w289;
        int w42;
        int w60;
        int w831;
        int w955;

        Unit1(float wRatio, float hRatio) {
            this.w955 = (int) (955.0f * wRatio);
            this.h832 = (int) (832.0f * hRatio);
            this.w42 = (int) (42.0f * wRatio);
            this.h54 = (int) (54.0f * hRatio);
            this.w25 = (int) (25.0f * hRatio);
            this.h30 = (int) (30.0f * hRatio);
            this.w60 = (int) (60.0f * wRatio);
            this.w831 = (int) (831.0f * wRatio);
            this.h480 = (int) (480.0f * hRatio);
            this.w20 = (int) (10.0f * wRatio);
            this.w289 = (int) (289.0f * wRatio);
            this.h120 = (int) (120.0f * hRatio);
            this.s10 = (int) (5.0f * hRatio);
            this.h90 = (int) (90.0f * hRatio);
            this.w120 = (int) (120.0f * wRatio);
            this.h200 = (int) (200.0f * hRatio);
            this.h22 = (int) (24.0f * hRatio);
            this.s19 = (13.0f * hRatio) + 12.0f;
            this.s22 = (8.0f * hRatio) + 12.0f;
            this.s25 = (14.0f * hRatio) + 12.0f;
            this.s28 = (24.0f * hRatio) + 12.0f;
        }
    }

    private class Unit2 {
        int h12;
        int h120;
        int h21;
        int h42;
        int h45;
        int h46;
        int h493;
        int h90;
        int s10;
        float s17;
        float s18;
        float s25;
        float s28;
        float s8;
        int w280;
        int w289;
        int w30;
        int w68;
        int w70;
        int w723;
        int w853;
        int w90;

        Unit2(float wRatio, float hRatio) {
            this.w853 = (int) (853.0f * wRatio);
            this.h493 = (int) (493.0f * hRatio);
            this.w90 = (int) (90.0f * wRatio);
            this.h90 = (int) (90.0f * hRatio);
            this.w30 = (int) (30.0f * wRatio);
            this.h42 = (int) (42.0f * hRatio);
            this.h21 = (int) (21.0f * hRatio);
            this.w68 = (int) (68.0f * wRatio);
            this.w280 = (int) (280.0f * wRatio);
            this.w70 = (int) (70.0f * wRatio);
            this.w723 = (int) (723.0f * wRatio);
            this.h46 = (int) (46.0f * hRatio);
            this.h12 = (int) (12.0f * hRatio);
            this.h45 = (int) (45.0f * hRatio);
            this.s10 = (int) (5.0f * hRatio);
            this.w289 = (int) (289.0f * wRatio);
            this.h120 = (int) (120.0f * hRatio);
            this.s8 = (8.0f * hRatio) + 12.0f;
            this.s17 = (13.0f * hRatio) + 12.0f;
            this.s18 = (14.0f * hRatio) + 10.0f;
            this.s25 = (21.0f * hRatio) + 10.0f;
            this.s28 = (24.0f * hRatio) + 10.0f;
        }
    }

    private class UpdateInfo {
        String apkUrl;
        int install;
        String name;
        String pName;
        String picUrl;
        int push;
        String updateContent;
        String version;

        private UpdateInfo() {
            this.pName = "";
            this.updateContent = "";
            this.version = "";
            this.apkUrl = "";
            this.push = -1;
            this.picUrl = "";
            this.name = "";
            this.install = 0;
        }
    }

    class UpdateView {
        Button btCancel;
        Button btUpdate;
        ProgressBar progressBar;
        int state = 0;
        TextView tvError;
        TextView tvPercent;
        TextView tvSize;
        TextView tvSpeed;

        UpdateView() {
        }
    }

    public ZidooUpdate(Context mContext, int type) {
        this.mContext = mContext;
        try {
            Log.v("bob", "zidoo update version 1.1.8");
            if (type == 0) {
                this.UPDATA_URL = new StringBuilder(URL_HEAND_WORLD).append(this.UPDATA_URL).toString();
            } else {
                this.UPDATA_URL = new StringBuilder(URL_HEAND_CHINA).append(this.UPDATA_URL).toString();
            }
            this.mPackageManager = mContext.getPackageManager();
            DisplayMetrics mDMs = new DisplayMetrics();
            ((WindowManager) mContext.getSystemService("window")).getDefaultDisplay().getMetrics(mDMs);
            if (mDMs.widthPixels > mDMs.heightPixels) {
                this.mWidthRatio = ((float) mDMs.widthPixels) / 1920.0f;
                this.mHeightRatio = ((float) mDMs.heightPixels) / 1080.0f;
            } else {
                this.mWidthRatio = ((float) mDMs.heightPixels) / 1920.0f;
                this.mHeightRatio = ((float) mDMs.widthPixels) / 1080.0f;
            }
            this.language = getLanguageString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getImg() {
        getImg(false);
    }

    public Bitmap getImg(boolean downPic) {
        try {
            this.isDownPic = downPic;
            Bitmap bitmap = null;
            if (this.isDownPic) {
                this.mIsGetPic = false;
                bitmap = getBitmap(getSDPath(this.mContext, PIC_NAME));
                if (bitmap != null) {
                    this.mIsGetPic = true;
                }
            }
            startUpdate();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getImg(String pName, String model, String Version, boolean downPic) {
        this.mHintPname = pName;
        this.mHintModel = model;
        this.mHintVersion = Version;
        return getImg(downPic);
    }

    public Bitmap getImgVersion(String Version, boolean downPic) {
        this.mHintVersion = Version;
        return getImg(downPic);
    }

    public Bitmap getImgPname(String pName, boolean downPic) {
        this.mHintPname = pName;
        return getImg(downPic);
    }

    public Bitmap getImgModel(String model, boolean downPic) {
        this.mHintModel = model;
        return getImg(downPic);
    }

    public void startUpdate() {
        if (!isConnected(this.mContext)) {
            return;
        }
        if (ZidooBoxPermissions.isUpdateEnable(this.mContext)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String pName = ZidooUpdate.this.mContext.getPackageName();
                        PackageInfo info = ZidooUpdate.this.mPackageManager.getPackageInfo(pName, 1);
                        String version = info.versionName;
                        String name = ZidooUpdate.this.mPackageManager.getApplicationLabel(info.applicationInfo).toString();
                        if (ZidooUpdate.this.mHintPname == null) {
                            ZidooUpdate.this.mHintPname = pName;
                        }
                        if (ZidooUpdate.this.mHintModel == null) {
                            ZidooUpdate.this.mHintModel = URLEncoder.encode(Build.MODEL, e.f);
                        }
                        if (ZidooUpdate.this.mHintVersion == null) {
                            ZidooUpdate.this.mHintVersion = version;
                        }
                        String url = new StringBuilder(String.valueOf(ZidooUpdate.this.UPDATA_URL)).append(ZidooUpdate.this.mHintPname).append("&oemid=").append(ZidooUpdate.this.mHintModel).append("&ver=").append(ZidooUpdate.this.mHintVersion).append("&mac=").append(ZidooNetStatusTool.getEthernetMac()).toString();
                        MyLog.v("update -  mHintPname  " + ZidooUpdate.this.mHintPname);
                        String jsonString = ZidooNetDataTool.getURLContentByHttpGet(url, 40000, 40000);
                        if (jsonString != null) {
                            UpdateInfo updateInfo = new UpdateInfo();
                            JSONObject jObject = new JSONObject(jsonString);
                            updateInfo.name = name;
                            updateInfo.version = jObject.getString(BrowseConstant.EXTRA_VERSION);
                            updateInfo.pName = jObject.getString("pn");
                            updateInfo.updateContent = jObject.getString("tip");
                            updateInfo.apkUrl = jObject.getString("url");
                            updateInfo.picUrl = jObject.getString("loadpic");
                            updateInfo.push = Integer.valueOf(jObject.getString("push")).intValue();
                            updateInfo.install = Integer.valueOf(jObject.getString("install")).intValue();
                            if (ZidooUpdate.this.isUpdata(updateInfo.version, ZidooUpdate.this.mHintVersion)) {
                                updateInfo.updateContent = updateInfo.updateContent.replace("|", "\n");
                                ZidooUpdate.this.mHandler.sendMessage(ZidooUpdate.this.mHandler.obtainMessage(0, updateInfo));
                            }
                            if (ZidooUpdate.this.isDownPic) {
                                boolean isDownPic = true;
                                if (ZidooUpdate.this.mIsGetPic && ZidooUpdate.this.getValue(ZidooUpdate.this.mContext, ZidooUpdate.PUSHPICSHARE, -1) >= updateInfo.push) {
                                    isDownPic = false;
                                }
                                ZidooUpdate.this.putValue(ZidooUpdate.this.mContext, ZidooUpdate.PUSHPICSHARE, updateInfo.push);
                                if (isDownPic) {
                                    HttpURLConnection http = (HttpURLConnection) new URL(updateInfo.picUrl).openConnection();
                                    http.setConnectTimeout(10000);
                                    http.setReadTimeout(10000);
                                    http.setDoInput(true);
                                    http.connect();
                                    int code = http.getResponseCode();
                                    if (code == 200 || code == 206) {
                                        InputStream inputStream = http.getInputStream();
                                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                        if (bitmap != null) {
                                            ZidooUpdate.this.saveBitmap(bitmap, ZidooUpdate.this.getSDPath(ZidooUpdate.this.mContext, ZidooUpdate.PIC_NAME));
                                            bitmap.recycle();
                                            System.gc();
                                        }
                                        inputStream.close();
                                    }
                                    http.disconnect();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            MyLog.v("startUpdate  isUpdateEnable = false");
        }
    }

    private void updateDialog(UpdateInfo updateInfo) {
        try {
            final Dialog dialog = new Dialog(this.mContext, 16973840);
            dialog.getWindow().setType(2003);
            AssetManager assetManager = this.mContext.getAssets();
            Bitmap bitmap = BitmapFactory.decodeStream(this.mContext.getAssets().open("bg.9.png"));
            Drawable ninePatchDrawable = new NinePatchDrawable(this.mContext.getResources(), new NinePatch(bitmap, bitmap.getNinePatchChunk(), null));
            final BitmapDrawable drawableInfoBg = new BitmapDrawable(this.mContext.getResources(), assetManager.open("bg_info.png"));
            BitmapDrawable drawableBtFocused = new BitmapDrawable(this.mContext.getResources(), assetManager.open("bt_focus.png"));
            BitmapDrawable drawableBtNormal = new BitmapDrawable(this.mContext.getResources(), assetManager.open("bt_normal.png"));
            Drawable selectorBtUpdate = new StateListDrawable();
            selectorBtUpdate.addState(new int[]{16842908}, drawableBtFocused);
            selectorBtUpdate.addState(new int[0], drawableBtNormal);
            Drawable selectorBtCancel = new StateListDrawable();
            selectorBtCancel.addState(new int[]{16842908}, drawableBtFocused);
            selectorBtCancel.addState(new int[0], drawableBtNormal);
            Unit1 unit1 = new Unit1(this.mWidthRatio, this.mHeightRatio);
            View relativeLayout = new RelativeLayout(this.mContext);
            relativeLayout.setGravity(17);
            relativeLayout.setLayoutParams(new LayoutParams(-1, -1));
            relativeLayout = new LinearLayout(this.mContext);
            relativeLayout.setBackgroundDrawable(ninePatchDrawable);
            relativeLayout.setOrientation(1);
            relativeLayout.setGravity(1);
            relativeLayout.addView(relativeLayout, new LayoutParams(unit1.w955, -2));
            relativeLayout = new TextView(this.mContext);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, unit1.h90);
            layoutParams.setMargins(unit1.w42, unit1.h54, unit1.w42, unit1.h22);
            relativeLayout.setGravity(19);
            relativeLayout.setTextSize(unit1.s25);
            relativeLayout.setTextColor(Color.rgb(146, 208, 41));
            relativeLayout.setText(updateInfo.name + HTTP.TAB + "(V" + updateInfo.version + ")");
            relativeLayout.setSingleLine(true);
            relativeLayout.setSelected(true);
            relativeLayout.setEllipsize(TruncateAt.MARQUEE);
            relativeLayout.addView(relativeLayout, layoutParams);
            final LinearLayout contentLayout = new LinearLayout(this.mContext);
            contentLayout.setOrientation(1);
            contentLayout.setPadding(unit1.h30, unit1.w25, unit1.h30, unit1.w25);
            layoutParams = new LinearLayout.LayoutParams(unit1.w831, -2);
            layoutParams.bottomMargin = unit1.h30;
            relativeLayout.addView(contentLayout, layoutParams);
            relativeLayout = new ScrollView(this.mContext);
            relativeLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        contentLayout.setBackgroundDrawable(drawableInfoBg);
                    } else {
                        contentLayout.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    }
                }
            });
            contentLayout.addView(relativeLayout);
            relativeLayout = new TextView(this.mContext);
            relativeLayout.setTextSize(unit1.s22);
            relativeLayout.setTextColor(-1);
            relativeLayout.setText(updateInfo.updateContent);
            relativeLayout.setLineSpacing(11.0f, 1.0f);
            relativeLayout.setPadding(unit1.w20, 0, unit1.w20, 0);
            relativeLayout.addView(relativeLayout);
            LinearLayout buttonLayout = new LinearLayout(this.mContext);
            buttonLayout.setGravity(17);
            buttonLayout.setOrientation(0);
            LinearLayout.LayoutParams buttLayoutParams = new LinearLayout.LayoutParams(-2, -2);
            buttLayoutParams.bottomMargin = unit1.h30;
            relativeLayout.addView(buttonLayout, buttLayoutParams);
            Button btUpdate = new Button(this.mContext);
            btUpdate.setGravity(17);
            btUpdate.setText(this.language[2]);
            layoutParams = new LinearLayout.LayoutParams(unit1.w289, unit1.h120);
            btUpdate.setTextSize(unit1.s19);
            btUpdate.setBackgroundDrawable(selectorBtUpdate);
            btUpdate.setPadding(0, unit1.s10, 0, 0);
            btUpdate.setTextColor(Color.parseColor("#6f6f6e"));
            btUpdate.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((Button) v).setTextColor(Color.parseColor("#fbfdfa"));
                    } else {
                        ((Button) v).setTextColor(Color.parseColor("#6f6f6e"));
                    }
                }
            });
            btUpdate.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            buttonLayout.addView(btUpdate, layoutParams);
            Button btCancel = new Button(this.mContext);
            btCancel.setText(this.language[1]);
            btCancel.setGravity(17);
            btCancel.setBackgroundDrawable(selectorBtCancel);
            btCancel.setTextSize(unit1.s19);
            btCancel.setPadding(0, unit1.s10, 0, 0);
            btCancel.setTextColor(Color.parseColor("#6f6f6e"));
            btCancel.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((Button) v).setTextColor(Color.parseColor("#fbfdfa"));
                    } else {
                        ((Button) v).setTextColor(Color.parseColor("#6f6f6e"));
                    }
                }
            });
            final UpdateInfo updateInfo2 = updateInfo;
            btCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ZidooUpdate.this.mInstallFile == null) {
                        ZidooUpdate.this.updateView();
                        ZidooUpdate.this.startDownApk(updateInfo2);
                    } else {
                        ZidooUpdate.installFile(ZidooUpdate.this.mInstallFile, ZidooUpdate.this.mContext);
                    }
                    dialog.cancel();
                }
            });
            layoutParams = new LinearLayout.LayoutParams(unit1.w289, unit1.h120);
            layoutParams.leftMargin = unit1.w120;
            buttonLayout.addView(btCancel, layoutParams);
            dialog.setContentView(relativeLayout);
            dialog.show();
            btCancel.requestFocus();
            final View view = relativeLayout;
            final Unit1 unit12 = unit1;
            relativeLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    int tvContentHeight = view.getHeight();
                    LinearLayout.LayoutParams contentLayoutParams;
                    if (tvContentHeight < unit12.h200) {
                        contentLayoutParams = new LinearLayout.LayoutParams(unit12.w831, unit12.h200);
                        contentLayoutParams.bottomMargin = unit12.h30;
                        contentLayout.setLayoutParams(contentLayoutParams);
                    } else if (tvContentHeight > unit12.h200) {
                        contentLayoutParams = new LinearLayout.LayoutParams(unit12.w831, unit12.h480);
                        contentLayoutParams.bottomMargin = unit12.h30;
                        contentLayout.setLayoutParams(contentLayoutParams);
                    } else {
                        contentLayoutParams = new LinearLayout.LayoutParams(unit12.w831, tvContentHeight);
                        contentLayoutParams.bottomMargin = unit12.h30;
                        contentLayout.setLayoutParams(contentLayoutParams);
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        try {
            final Dialog dialog = new Dialog(this.mContext, 16973840);
            dialog.getWindow().setType(2003);
            AssetManager assetManager = this.mContext.getAssets();
            Bitmap bitmap = BitmapFactory.decodeStream(this.mContext.getAssets().open("bg.9.png"));
            Drawable ninePatchDrawable = new NinePatchDrawable(this.mContext.getResources(), new NinePatch(bitmap, bitmap.getNinePatchChunk(), null));
            BitmapDrawable drawableBtFocused = new BitmapDrawable(this.mContext.getResources(), assetManager.open("bt_focus.png"));
            BitmapDrawable drawableBtNormal = new BitmapDrawable(this.mContext.getResources(), assetManager.open("bt_normal.png"));
            BitmapDrawable drawablePgBg = new BitmapDrawable(this.mContext.getResources(), assetManager.open("seekbar_bg.png"));
            ninePatchDrawable = new BitmapDrawable(this.mContext.getResources(), assetManager.open("seekbar_error.png"));
            ninePatchDrawable = new BitmapDrawable(this.mContext.getResources(), assetManager.open("seekbar_hitlight.png"));
            ClipDrawable clipPgError = new ClipDrawable(ninePatchDrawable, 3, 1);
            ClipDrawable clipPgProgress = new ClipDrawable(ninePatchDrawable, 3, 1);
            Drawable selectorBtUpdate = new StateListDrawable();
            selectorBtUpdate.addState(new int[]{16842908}, drawableBtFocused);
            selectorBtUpdate.addState(new int[0], drawableBtNormal);
            Drawable selectorBtCancel = new StateListDrawable();
            selectorBtCancel.addState(new int[]{16842908}, drawableBtFocused);
            selectorBtCancel.addState(new int[0], drawableBtNormal);
            this.mUpdateView = new UpdateView();
            Unit2 unit2 = new Unit2(this.mWidthRatio, this.mHeightRatio);
            View relativeLayout = new RelativeLayout(this.mContext);
            relativeLayout.setGravity(17);
            relativeLayout.setLayoutParams(new LayoutParams(-1, -1));
            relativeLayout = new LinearLayout(this.mContext);
            relativeLayout.setBackgroundDrawable(ninePatchDrawable);
            relativeLayout.setOrientation(1);
            relativeLayout.setGravity(1);
            relativeLayout.addView(relativeLayout, new LayoutParams(unit2.w853, unit2.h493));
            relativeLayout = new TextView(this.mContext);
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, unit2.h90);
            layoutParams.setMargins(unit2.w30 * 2, unit2.h42, 0, unit2.h21);
            relativeLayout.setLayoutParams(layoutParams);
            relativeLayout.setGravity(19);
            relativeLayout.setTextSize(unit2.s17);
            relativeLayout.setTextColor(Color.rgb(146, 208, 41));
            relativeLayout.setText(this.language[3]);
            relativeLayout.addView(relativeLayout);
            relativeLayout = new LinearLayout(this.mContext);
            relativeLayout.setGravity(21);
            relativeLayout.setOrientation(0);
            relativeLayout.setPadding(0, 0, unit2.w68, 0);
            relativeLayout.addView(relativeLayout);
            relativeLayout = new TextView(this.mContext);
            relativeLayout.setSingleLine();
            relativeLayout.setGravity(5);
            relativeLayout.setTextSize(unit2.s8);
            relativeLayout.setText("0 Mb/s");
            relativeLayout.setTextColor(Color.rgb(168, 168, 168));
            relativeLayout.addView(relativeLayout);
            relativeLayout = new TextView(this.mContext);
            relativeLayout.setSingleLine();
            relativeLayout.setGravity(5);
            relativeLayout.setWidth(unit2.w280);
            relativeLayout.setTextSize(unit2.s8);
            relativeLayout.setText("0 Mb/0Mb");
            relativeLayout.setTextColor(Color.rgb(168, 168, 168));
            relativeLayout.addView(relativeLayout);
            relativeLayout = new TextView(this.mContext);
            relativeLayout.setSingleLine();
            relativeLayout.setWidth(unit2.w70);
            relativeLayout.setGravity(5);
            relativeLayout.setTextSize(unit2.s8);
            relativeLayout.setText("0");
            relativeLayout.setTextColor(Color.rgb(168, 168, 168));
            relativeLayout.addView(relativeLayout);
            relativeLayout = new TextView(this.mContext);
            relativeLayout.setTextSize(unit2.s8);
            relativeLayout.setText("%");
            relativeLayout.setTextColor(Color.rgb(168, 168, 168));
            relativeLayout.addView(relativeLayout);
            relativeLayout = new ProgressBar(this.mContext, null, 16842872);
            LayerDrawable layerDrawable = (LayerDrawable) relativeLayout.getProgressDrawable();
            layerDrawable.setDrawableByLayerId(layerDrawable.getId(0), drawablePgBg);
            layerDrawable.setDrawableByLayerId(layerDrawable.getId(1), clipPgError);
            layerDrawable.setDrawableByLayerId(layerDrawable.getId(2), clipPgProgress);
            layoutParams = new LinearLayout.LayoutParams(unit2.w723, unit2.h46);
            layoutParams.setMargins(0, unit2.h12, 0, unit2.h12);
            relativeLayout.addView(relativeLayout, layoutParams);
            relativeLayout = new TextView(this.mContext);
            layoutParams = new LinearLayout.LayoutParams(-1, -2);
            layoutParams.setMargins(unit2.w68, 0, 0, unit2.h45);
            relativeLayout.setTextSize(unit2.s17);
            relativeLayout.setText(this.language[7]);
            relativeLayout.setVisibility(4);
            relativeLayout.addView(relativeLayout, layoutParams);
            LinearLayout buttonLayout = new LinearLayout(this.mContext);
            buttonLayout.setGravity(17);
            buttonLayout.setOrientation(0);
            relativeLayout.addView(buttonLayout);
            layoutParams = new LinearLayout.LayoutParams(unit2.w289, unit2.h120);
            Button btUpdate = new Button(this.mContext);
            btUpdate.setGravity(17);
            btUpdate.setText(this.language[4]);
            btUpdate.setTextSize(unit2.s17);
            btUpdate.setPadding(0, unit2.s10, 0, 0);
            btUpdate.setBackgroundDrawable(selectorBtUpdate);
            btUpdate.setTextColor(Color.parseColor("#6f6f6e"));
            btUpdate.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((Button) v).setTextColor(Color.parseColor("#fbfdfa"));
                    } else {
                        ((Button) v).setTextColor(Color.parseColor("#6f6f6e"));
                    }
                }
            });
            btUpdate.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ZidooUpdate.this.mUpdateView.state == 0) {
                        dialog.cancel();
                    } else if (ZidooUpdate.this.mUpdateView.state == 1) {
                        ZidooUpdate.this.startDownApk(ZidooUpdate.this.mInfo);
                        ZidooUpdate.this.mUpdateView.progressBar.setSecondaryProgress(0);
                        ZidooUpdate.this.mUpdateView.btUpdate.setText(ZidooUpdate.this.language[4]);
                        ZidooUpdate.this.mUpdateView.btCancel.setText(ZidooUpdate.this.language[2]);
                        ZidooUpdate.this.mUpdateView.tvError.setVisibility(4);
                    } else {
                        dialog.cancel();
                    }
                }
            });
            buttonLayout.addView(btUpdate, layoutParams);
            Button btCancel = new Button(this.mContext);
            btCancel.setText(this.language[2]);
            btCancel.setTextSize(unit2.s17);
            btCancel.setPadding(0, unit2.s10, 0, 0);
            btCancel.setGravity(17);
            btCancel.setBackgroundDrawable(selectorBtCancel);
            btCancel.setTextColor(Color.parseColor("#6f6f6e"));
            btCancel.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((Button) v).setTextColor(Color.parseColor("#fbfdfa"));
                    } else {
                        ((Button) v).setTextColor(Color.parseColor("#6f6f6e"));
                    }
                }
            });
            btCancel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ZidooUpdate.this.mUpdateView.state == 0) {
                        ZidooUpdate.this.mIsDowning = false;
                    }
                    dialog.cancel();
                }
            });
            layoutParams = new LinearLayout.LayoutParams(unit2.w289, unit2.h120);
            layoutParams.leftMargin = unit2.w90;
            buttonLayout.addView(btCancel, layoutParams);
            this.mUpdateView.tvSpeed = relativeLayout;
            this.mUpdateView.tvSize = relativeLayout;
            this.mUpdateView.tvPercent = relativeLayout;
            this.mUpdateView.tvError = relativeLayout;
            this.mUpdateView.progressBar = relativeLayout;
            this.mUpdateView.btUpdate = btUpdate;
            this.mUpdateView.btCancel = btCancel;
            dialog.setContentView(relativeLayout);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDownApk(UpdateInfo updateInfo) {
        new Thread(new DownApkRu(updateInfo.apkUrl)).start();
    }

    public boolean isUpdata(String newVersion, String oldVersion) {
        try {
            Log.v("bob", "--web == " + newVersion + "---local == " + oldVersion);
            if (newVersion != null) {
                if (!newVersion.equals("")) {
                    if (newVersion.equals(oldVersion)) {
                        return false;
                    }
                    int i;
                    newVersion = newVersion.replaceAll("-", "\\.");
                    oldVersion = oldVersion.replaceAll("-", "\\.");
                    String[] sNewVersion = newVersion.split("\\.");
                    String[] sOldVersion = oldVersion.split("\\.");
                    ArrayList<String> newVersionArray = new ArrayList();
                    ArrayList<String> oldVersionArray = new ArrayList();
                    newVersionArray.addAll(Arrays.asList(sNewVersion));
                    oldVersionArray.addAll(Arrays.asList(sOldVersion));
                    int difference;
                    if (newVersionArray.size() > oldVersionArray.size()) {
                        difference = newVersionArray.size() - oldVersionArray.size();
                        for (i = 0; i < difference; i++) {
                            oldVersionArray.add("0");
                        }
                    } else {
                        difference = oldVersionArray.size() - newVersionArray.size();
                        for (i = 0; i < difference; i++) {
                            newVersionArray.add("0");
                        }
                    }
                    i = 0;
                    Iterator it = newVersionArray.iterator();
                    while (it.hasNext()) {
                        String s = (String) it.next();
                        String old = (String) oldVersionArray.get(i);
                        try {
                            int newVer = Integer.parseInt(s);
                            int oldVer = Integer.parseInt(old);
                            if (newVer > oldVer) {
                                return true;
                            }
                            if (newVer < oldVer) {
                                return false;
                            }
                            i++;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            int temp = s.compareToIgnoreCase(old);
                            if (temp < 0) {
                                return false;
                            }
                            if (temp > 0) {
                                return true;
                            }
                            i++;
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getWebMsg(String webpath) {
        String urlString = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(SmbConstants.DEFAULT_RESPONSE_TIMEOUT));
            HttpResponse response = client.execute(new HttpGet(webpath));
            HttpEntity entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            if (code == 200 || code == HTTPStatus.PARTIAL_CONTENT) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
        }
        return urlString;
    }

    private void putValue(Context context, String key, int value) {
        Editor sp = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    private int getValue(Context context, String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    private boolean isConnected(Context context) {
        if (isWifiConnected(context) || isNetConnected(context)) {
            return true;
        }
        return false;
    }

    private boolean isWifiConnected(Context context) {
        boolean z = true;
        try {
            NetworkInfo wifiInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
            if (wifiInfo != null) {
                z = wifiInfo.isConnected();
            }
        } catch (Exception e) {
        }
        return z;
    }

    private boolean isNetConnected(Context context) {
        try {
            NetworkInfo etherInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(9);
            if (etherInfo != null) {
                return etherInfo.isConnected();
            }
        } catch (Exception e) {
        }
        return true;
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            if (file != null && file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File bitfile = new File(path);
            if (bitfile.exists()) {
                bitmap = BitmapFactory.decodeFile(bitfile.toString());
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    private String getSDPath(Context context, String pathName) {
        return new StringBuilder(String.valueOf(context.getFilesDir().getAbsolutePath())).append("/").append(pathName).toString();
    }

    public static String toUtf8Strings(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '\u0000' || c > 'ÿ') {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception e) {
                    b = new byte[0];
                }
                for (int k : b) {
                    int k2;
                    if (k2 < 0) {
                        k2 += 256;
                    }
                    sb.append("%" + Integer.toHexString(k2).toUpperCase());
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void installFile(File file, Context context) {
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

    public static boolean hitInstallApk(File file, Context context) {
        Intent intent;
        if (isInstall(context, "com.zidoo.busybox")) {
            Bundle bundle = new Bundle();
            bundle.putString("InstallPath", file.getAbsolutePath());
            intent = new Intent();
            intent.setAction("zidoo.busybox.action");
            intent.putExtra("cmd", "InstallApk");
            intent.putExtra("parameter", bundle);
            context.sendBroadcast(intent);
            return true;
        } else if (!isInstall(context, "com.android.install.apk")) {
            return false;
        } else {
            intent = new Intent();
            intent.setAction("install_apk_receiver.action");
            intent.putExtra("installPath", file.getAbsolutePath());
            context.sendBroadcast(intent);
            return true;
        }
    }

    public static boolean isInstall(Context context, String packageName) {
        if (isAppLaunchInstall(context, packageName) || isAppSystemInstall(context, packageName)) {
            return true;
        }
        return false;
    }

    public static boolean isAppLaunchInstall(Context context, String packageName) {
        if (packageName == null || context.getPackageManager().getLaunchIntentForPackage(packageName) == null) {
            return false;
        }
        return true;
    }

    public static boolean isAppSystemInstall(Context context, String pName) {
        if (pName == null) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pName, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }
        return false;
    }

    public static String toSize(long mbyte) {
        if (mbyte >= gb) {
            if (((double) mbyte) / ((double) gb) >= 1024.0d) {
                return String.format("%.2f T ", new Object[]{Double.valueOf((((double) mbyte) / ((double) gb)) / 1024.0d)});
            }
            return String.format("%.2f GB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) gb))});
        } else if (mbyte >= mb) {
            return String.format("%.2f MB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) mb))});
        } else if (mbyte >= kb) {
            return String.format("%.2f KB ", new Object[]{Double.valueOf(((double) mbyte) / ((double) kb))});
        } else {
            return String.format("%d B", new Object[]{Long.valueOf(mbyte)});
        }
    }

    private String[] getLanguageString() {
        String language = this.mContext.getResources().getConfiguration().locale.getLanguage();
        if (language.equals("zh")) {
            return new String[]{"软件升级", "升级", "取消", "升级中...", "后台运行", "重试", "退出", "升级出错啦!"};
        } else if (language.equals("en")) {
            return new String[]{"Software Upgrading", "Update", "Cancel", "Updating...", "Background", "Try Again", "Quit", "Update Error!"};
        } else {
            return new String[]{"Software Upgrading", "Update", "Cancel", "Updating...", "Background", "Try Again", "Quit", "Update Error!"};
        }
    }
}
