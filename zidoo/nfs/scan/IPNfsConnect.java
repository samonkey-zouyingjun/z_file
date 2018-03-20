package zidoo.nfs.scan;

import android.os.Handler;
import android.os.Looper;
import zidoo.nfs.NfsDevice;

public class IPNfsConnect extends NfsScan {
    String ip;
    boolean stop = true;

    private class ConnectThread extends Thread {
        String ip;
        Process p = null;

        ConnectThread(String ip) {
            this.ip = ip;
        }

        public void run() {
            boolean z = true;
            int result = -1;
            try {
                this.p = Runtime.getRuntime().exec(new String[]{"/system/bin/nfsprobe", "-e", this.ip});
                result = this.p.waitFor();
                this.p.exitValue();
                this.p = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                IPNfsConnect iPNfsConnect = IPNfsConnect.this;
                if (result != 0) {
                    z = false;
                }
                iPNfsConnect.end(z);
            }
        }

        void stopRun() {
            try {
                if (this.p != null) {
                    this.p.destroy();
                }
                interrupt();
                IPNfsConnect.this.end(false);
            } catch (Exception e) {
                e.printStackTrace();
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

    public IPNfsConnect(String ip) {
        this.ip = ip;
    }

    public void start() {
        this.stop = false;
        onStart(2);
        ConnectThread thread = new ConnectThread(this.ip);
        new Handler(Looper.getMainLooper()).postDelayed(new StopRunn(thread), 8000);
        thread.start();
    }

    public boolean isScanning() {
        return !this.stop;
    }

    public void stop() {
        this.stop = true;
    }

    void end(boolean success) {
        if (!this.stop) {
            if (success) {
                onAdd(new NfsDevice(this.ip));
            }
            onProgress(100);
            this.stop = true;
            onComplete(2, success);
        }
    }
}
