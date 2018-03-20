package zidoo.nfs.scan;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zidoo.nfs.NfsDevice;
import zidoo.tool.ZidooFileUtils;

public class CmdNfsScan extends NfsScan {
    private final int TIMEOUT = 500;
    private Context context;
    private ExecutorService executorService = Executors.newFixedThreadPool(16);
    private Handler handler = new Handler(Looper.getMainLooper());
    private int progress = 0;
    private boolean stop = true;

    private class ConnectThread extends Thread {
        String ip;
        Process p = null;
        boolean stop = false;

        ConnectThread(String ip) {
            this.ip = ip;
        }

        public void run() {
            boolean z = true;
            CmdNfsScan.this.handler.postDelayed(new StopRunn(this), 500);
            int result = -1;
            try {
                this.p = Runtime.getRuntime().exec(new String[]{"/system/bin/nfsprobe", "-e", this.ip});
                result = this.p.waitFor();
                this.p.exitValue();
                this.p = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (result != 0) {
                    z = false;
                }
                end(z);
            }
        }

        void stopRun() {
            try {
                if (this.p != null) {
                    this.p.destroy();
                }
                interrupt();
                end(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        synchronized void end(boolean success) {
            if (!this.stop) {
                CmdNfsScan.this.onScaned(this.ip, success);
                this.stop = true;
            }
        }
    }

    private class StopRunn implements Runnable {
        ConnectThread thread;

        StopRunn(ConnectThread thread) {
            this.thread = thread;
        }

        public void run() {
            this.thread.stopRun();
        }
    }

    public CmdNfsScan(Context context) {
        this.context = context;
    }

    public void start() {
        new Thread(new Runnable() {
            public void run() {
                String localIp = ZidooFileUtils.getSelfAddress(CmdNfsScan.this.context);
                if (TextUtils.isEmpty(localIp)) {
                    CmdNfsScan.this.onComplete(1, false);
                    return;
                }
                CmdNfsScan.this.stop = false;
                CmdNfsScan.this.onStart(1);
                String ipHead = localIp.substring(0, localIp.lastIndexOf(".") + 1);
                int i = 0;
                while (!CmdNfsScan.this.stop && i < 256) {
                    CmdNfsScan.this.executorService.execute(new ConnectThread(ipHead + i));
                    i++;
                }
            }
        }).start();
    }

    synchronized void onScaned(String ip, boolean success) {
        if (!this.stop) {
            if (success) {
                onAdd(new NfsDevice(ip));
            }
            int i = this.progress + 1;
            this.progress = i;
            onProgress(i);
            if (this.progress == 256) {
                this.stop = true;
                onComplete(1, success);
            }
        }
    }

    public boolean isScanning() {
        return !this.stop;
    }

    void onProgress(int progress) {
        super.onProgress((progress * 100) / 256);
    }

    public void stop() {
        this.stop = true;
        this.executorService.shutdown();
        this.executorService.shutdownNow();
    }
}
