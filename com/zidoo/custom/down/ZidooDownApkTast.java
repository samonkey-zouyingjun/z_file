package com.zidoo.custom.down;

import android.annotation.SuppressLint;
import android.content.Context;
import com.zidoo.custom.db.ZidooSQliteManger;
import com.zidoo.custom.file.ZidooFileSizeFile;
import com.zidoo.custom.file.ZidooFileTool;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import zidoo.http.HTTP;
import zidoo.http.HTTPStatus;

@SuppressLint({"HandlerLeak"})
public class ZidooDownApkTast extends Thread {
    private String apkFileSavePath = "";
    private BufferedInputStream bis;
    private ZidooBufferedRandomAccessFile bufferedRandomAccessFile;
    private Context context;
    private ZidooDownApkBaseManger downdb = null;
    private int downloadlength = 0;
    long length;
    private int momentlength;
    private int momentlength_last = 0;
    private int momentlength_start = 0;
    private int momentnum = 0;
    private String softPackName;
    private String softUrl;
    Timer speedTimer = new Timer();
    TimerTask speedTimerTask = null;
    int type = 0;
    private ZidooDownApkInfo zidooDownApkInfo = null;

    public ZidooDownApkTast(Context context, ZidooDownApkInfo zidooDownApkInfo) {
        this.context = context;
        this.zidooDownApkInfo = zidooDownApkInfo;
        this.downdb = ZidooSQliteManger.getDownApkBaseManger(context);
        this.softPackName = zidooDownApkInfo.getpName();
        this.apkFileSavePath = zidooDownApkInfo.getApkpath();
        this.softUrl = zidooDownApkInfo.getApkurl();
    }

    public void run() {
        if (this.softUrl == null || this.softUrl.equals("")) {
            error(false);
            return;
        }
        if (this.apkFileSavePath == null || this.apkFileSavePath.equals("")) {
            String urlpath = apkDownSavePath(this.softUrl, this.context);
            if (urlpath == null) {
                error(false);
                return;
            }
            this.downdb.updateDownPath(this.softPackName, urlpath);
            this.apkFileSavePath = urlpath;
            this.zidooDownApkInfo.setApkpath(urlpath);
        }
        this.downloadlength = this.downdb.getUpdateDownloadLength(this.softPackName);
        this.momentlength = this.downloadlength;
        this.momentlength_start = 0;
        try {
            HttpURLConnection http = (HttpURLConnection) new URL(ZidooFileTool.toUtf8Strings(this.softUrl)).openConnection();
            http.setDoInput(true);
            http.setConnectTimeout(60000);
            http.setReadTimeout(40000);
            http.setRequestProperty(HTTP.RANGE, "bytes=" + this.downloadlength + "-");
            http.connect();
            int code = http.getResponseCode();
            if (code == 200 || code == HTTPStatus.PARTIAL_CONTENT) {
                InputStream inStream = http.getInputStream();
                this.length = Long.parseLong(http.getHeaderField(HTTP.CONTENT_LENGTH));
                if (this.length < 1) {
                    error(false);
                    return;
                }
                this.length += (long) this.downloadlength;
                this.downdb.upDateTotalLength(this.softPackName, this.length);
                File tempFile = new File(this.apkFileSavePath);
                this.bufferedRandomAccessFile = new ZidooBufferedRandomAccessFile(tempFile, "rwd");
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                ZidooFileTool.execMethod(this.apkFileSavePath);
                if (this.downloadlength > 0) {
                    this.bufferedRandomAccessFile.setLength((long) this.downloadlength);
                }
                this.bis = new BufferedInputStream(inStream, 4096);
                this.bufferedRandomAccessFile.seek((long) this.downloadlength);
                try {
                    if (this.bis != null) {
                        byte[] buf = new byte[4096];
                        updata();
                        while (true) {
                            int ch = this.bis.read(buf);
                            if (ch != -1) {
                                this.bufferedRandomAccessFile.write(buf, 0, ch);
                                this.momentlength += ch;
                                this.momentlength_start += ch;
                                if (this.type == 2) {
                                    break;
                                }
                            }
                            break;
                        }
                        if (((long) this.momentlength) == this.length) {
                            this.momentnum = (int) ((((float) this.momentlength) / ((float) this.length)) * 100.0f);
                            updateDabaseLength_DownloadNum("");
                            downOver(tempFile);
                        } else if (this.type == 2) {
                            error(true);
                        } else {
                            error(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error(true);
                } finally {
                    this.bis.close();
                    http.disconnect();
                    this.bufferedRandomAccessFile.close();
                }
            } else {
                error(false);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            error(true);
        }
    }

    private void downOver(File file) {
        if (file.exists()) {
            cancel();
            ZidooDownApkService.installFile(file, this.context, this.softPackName);
            return;
        }
        error(false);
    }

    public void error(boolean ispause) {
        cancel();
        if (ispause) {
            if (this.momentlength_start != 0) {
                this.momentnum = (int) ((((float) this.momentlength) / ((float) this.length)) * 100.0f);
                updateDabaseLength_DownloadNum("");
            }
            updataDownFlag(2);
            this.zidooDownApkInfo.setDownStatu(2);
            return;
        }
        this.zidooDownApkInfo.setDownStatu(6);
        updataDownFlag(6);
    }

    private void cancel() {
        if (this.speedTimerTask != null) {
            this.speedTimerTask.cancel();
            this.speedTimerTask = null;
        }
    }

    private void updata() {
        cancel();
        this.speedTimerTask = new TimerTask() {
            public void run() {
                try {
                    ZidooDownApkTast.this.momentnum = (int) ((((float) ZidooDownApkTast.this.momentlength) / ((float) ZidooDownApkTast.this.length)) * 100.0f);
                    ZidooDownApkTast.this.type = ZidooDownApkTast.this.downdb.queryDownStatus(ZidooDownApkTast.this.softPackName);
                    String speed_str = new StringBuilder(String.valueOf(ZidooFileSizeFile.toSize((long) (ZidooDownApkTast.this.momentlength_start - ZidooDownApkTast.this.momentlength_last)))).append("/s").toString();
                    ZidooDownApkTast.this.momentlength_last = ZidooDownApkTast.this.momentlength_start;
                    ZidooDownApkTast.this.zidooDownApkInfo.setKbs(speed_str);
                    ZidooDownApkTast.this.updateDabaseLength_DownloadNum(speed_str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.speedTimer.schedule(this.speedTimerTask, 1, 1000);
    }

    public void updateDabaseLength_DownloadNum(String kbs) {
        this.downdb.updateDownLength(this.softPackName, this.momentlength, this.momentnum, kbs);
        this.zidooDownApkInfo.setDownNum(this.momentnum);
    }

    public void updataDownFlag(int flag) {
        this.downdb.updataDownFlag(this.softPackName, flag);
    }

    public static String apkDownSavePath(String webpath, Context context) {
        try {
            String[] temp = new String[]{webpath.substring(webpath.lastIndexOf(".") + 1, webpath.length()).toLowerCase(), webpath.substring(webpath.lastIndexOf("/") + 1, webpath.lastIndexOf("."))};
            String name = temp[1] + "." + temp[0];
            String path = ZidooDownApkService.DOWNAPKPAHT + "/";
            String fapkpath = new StringBuilder(String.valueOf(path)).append(name).toString();
            File sdcardFile = new File(path);
            if (!sdcardFile.exists()) {
                sdcardFile.mkdirs();
            }
            if (sdcardFile == null || !sdcardFile.exists()) {
                throw new RuntimeException("zidoo downApkFoldersPath error");
            }
            ZidooFileTool.execMethod(path);
            ZidooFileTool.execMethod(fapkpath);
            return fapkpath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
