package zidoo.samba.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.umeng.common.util.e;
import com.zidoo.custom.usb.FileTypeManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import zidoo.samba.exs.OnOpenSmbFileListener;
import zidoo.samba.player.service.PlayFileService;
import zidoo.samba.player.util.FileUtil;

public class DefaultSmbManager extends SambaManager {
    private static String sPath;
    Handler mSmbFileHandler;
    private OnOpenSmbFileListener onOpenSmbFileListener = null;

    class openTask extends TimerTask {
        public boolean openSuccess = false;
        SmbFile[] smbFiles = null;
        private String url;

        public openTask(String url) {
            this.url = url;
        }

        public void run() {
            try {
                SmbFile file = new SmbFile(this.url);
                if (file.exists() && file.isDirectory()) {
                    if (this.url.charAt(this.url.length() - 1) != '/') {
                        this.url += "/";
                    }
                    this.smbFiles = new SmbFile(this.url).listFiles();
                }
                this.openSuccess = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SmbException e2) {
                e2.printStackTrace();
            }
        }

        public SmbFile[] getSmbFiles() {
            return this.smbFiles;
        }
    }

    public String getSmbRoot() {
        return null;
    }

    DefaultSmbManager(Context context) {
        super(context);
        init();
    }

    @SuppressLint({"HandlerLeak"})
    private void init() {
        this.mSmbFileHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        SmbFile[] smbFiles = (SmbFile[]) msg.obj;
                        if (DefaultSmbManager.this.onOpenSmbFileListener != null) {
                            DefaultSmbManager.this.onOpenSmbFileListener.OnOpenSmbFileSuccess(smbFiles);
                            return;
                        }
                        return;
                    case 2:
                        if (DefaultSmbManager.this.onOpenSmbFileListener != null) {
                            DefaultSmbManager.this.onOpenSmbFileListener.OnOpenSmbFileFail();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
    }

    public void startService() {
        this.mContext.startService(new Intent(this.mContext, PlayFileService.class));
    }

    public void stopService() {
        this.mContext.stopService(new Intent(this.mContext, PlayFileService.class));
    }

    public void openSmbFolder(String url) {
        Timer timer = new Timer();
        final openTask task = new openTask(url);
        timer.schedule(task, 0);
        new Thread(new Runnable() {
            public void run() {
                int openTime = 0;
                while (!task.openSuccess) {
                    try {
                        Thread.sleep(50);
                        openTime += 50;
                        if (openTime > 5000) {
                            DefaultSmbManager.this.mSmbFileHandler.sendEmptyMessage(2);
                            task.cancel();
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SmbFile[] smbFiles = task.getSmbFiles();
                Message msg = DefaultSmbManager.this.mSmbFileHandler.obtainMessage();
                msg.what = 1;
                msg.obj = smbFiles;
                DefaultSmbManager.this.mSmbFileHandler.sendMessage(msg);
            }
        }).start();
    }

    public SmbFile[] openSmbFolder(SmbFile smbFile) throws IOException {
        if (!smbFile.isDirectory()) {
            return null;
        }
        String url = smbFile.getPath();
        if (url.charAt(url.length() - 1) != '/') {
            url = url + "/";
        }
        return new SmbFile(url).listFiles();
    }

    public String[] openSmbFolderByStr(SmbFile smbFile) throws IOException {
        if (smbFile.isDirectory()) {
            return new SmbFile(smbFile.getPath() + "/").list();
        }
        return null;
    }

    public String[] openSmbFolderByStr(String url) throws IOException {
        if (new SmbFile(url).isDirectory()) {
            return new SmbFile(url + "/").list();
        }
        return null;
    }

    public boolean openAudeoFile(SmbFile smbFile) {
        return openAudeoFile(smbFile.getPath());
    }

    public boolean openAudeoFile(String filePath) {
        String kzm = filePath.substring(filePath.lastIndexOf(46) + 1);
        if (!kzm.equals("mp3") && !kzm.equals("mp4") && !kzm.equals("3gp") && !kzm.equals("wav") && !kzm.equals("m4a") && !kzm.equals("mid") && !kzm.equals("xmf") && !kzm.equals("ogg")) {
            return false;
        }
        String ipVal = FileUtil.ip;
        String path = filePath;
        String httpReq = "http://" + ipVal + ":" + FileUtil.port + "/smb=";
        path = path.substring(6);
        try {
            path = URLEncoder.encode(path, e.f);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = httpReq + path;
        Intent it = new Intent("android.intent.action.VIEW");
        it.setDataAndType(Uri.parse(url), "audio/*");
        this.mContext.startActivity(it);
        return true;
    }

    public ByteArrayOutputStream openTextSmbFile(SmbFile smbFile) throws IOException {
        InputStream in = new SmbFileInputStream(smbFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        while (true) {
            int len = in.read(b);
            if (len == -1) {
                break;
            }
            out.write(b, 0, len);
        }
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        return out;
    }

    public boolean openFileWithChoose(SmbFile smbFile) throws IOException {
        if (smbFile.getContentLength() > 10485760) {
            return false;
        }
        String localPath = sPath + "/" + smbFile.getName();
        File file = new File(localPath);
        if (file.exists()) {
            file.delete();
        }
        String kzm = localPath.substring(localPath.lastIndexOf(46) + 1);
        InputStream in = new SmbFileInputStream(smbFile);
        OutputStream out = new FileOutputStream(localPath);
        byte[] b = new byte[1024];
        while (true) {
            int len = in.read(b);
            if (len == -1) {
                break;
            }
            out.write(b, 0, len);
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        Uri uri = Uri.fromFile(new File(localPath));
        String type = "";
        intent.addCategory("android.intent.category.DEFAULT");
        if (kzm.equals(FileTypeManager.open_type_apk)) {
            intent.addFlags(268435456);
            type = "application/vnd.android.package-archive";
        } else if (kzm.equals("jpg") || kzm.equals("gif") || kzm.equals("png") || kzm.equals("jpeg") || kzm.equals("bmp")) {
            intent.addFlags(268435456);
            type = "image/*";
        } else if (kzm.equals("m4a") || kzm.equals("mp3") || kzm.equals("mid") || kzm.equals("xmf") || kzm.equals("ogg") || kzm.equals("wav") || kzm.equals("3gp") || kzm.equals("mp4")) {
            intent.addFlags(67108864);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            type = "audio/*";
        } else if (kzm.equals(FileTypeManager.open_type_ppt)) {
            intent.addFlags(268435456);
            type = "application/vnd.ms-powerpoint";
        } else if (kzm.equals("xls")) {
            intent.addFlags(268435456);
            type = "application/vnd.ms-excel";
        } else if (kzm.equals("doc")) {
            intent.addFlags(268435456);
            type = "application/msword";
        } else if (kzm.equals(FileTypeManager.open_type_pdf)) {
            intent.addFlags(268435456);
            type = "application/pdf";
        } else if (kzm.equals("chm")) {
            intent.addFlags(268435456);
            type = "application/x-chm";
        } else {
            intent.addFlags(268435456);
            type = "*/*";
        }
        intent.setDataAndType(uri, type);
        this.mContext.startActivity(intent);
        return true;
    }

    private void createDirectory() {
        sPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + this.mContext.getApplicationContext().getPackageName() + "/smb/temp";
        new File(sPath).mkdirs();
    }

    public String getLocalPath(Context context) {
        return sPath;
    }

    public void setOnOpenSmbFileListener(OnOpenSmbFileListener onOpenSmbFileListener) {
        this.onOpenSmbFileListener = onOpenSmbFileListener;
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        return false;
    }

    public boolean unMountSmb(File file) {
        return false;
    }
}
