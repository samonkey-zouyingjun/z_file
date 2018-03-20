package zidoo.samba.exs;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zidoo.tool.ZidooFileUtils;

public class QuickSambaScan extends SambaScan {
    int mProgress;
    ExecutorService mQuickExecutorService;
    int mQuickScanNumber;
    boolean[] mScanedIps;
    ExecutorService mSuperExecutorService;

    class QueckScan implements Runnable {
        String ipHead;
        int position;

        QueckScan(int position, String ipHead) {
            this.ipHead = ipHead;
            this.position = position;
        }

        public void run() {
            if (QuickSambaScan.this.mIsScanning) {
                String ip = this.ipHead + this.position;
                boolean isReachable = QuickSambaScan.this.pingHost(ip, 50);
                synchronized (QuickSambaScan.this) {
                    QuickSambaScan quickSambaScan;
                    if (isReachable) {
                        quickSambaScan = QuickSambaScan.this;
                        quickSambaScan.mProgress++;
                        QuickSambaScan.this.mScanedIps[this.position] = true;
                        SambaDevice device = new SambaDevice(ip, ip);
                        device.setUrl("smb://" + ip + "/");
                        QuickSambaScan.this.onAdd(device);
                        QuickSambaScan.this.onProgress((QuickSambaScan.this.mProgress * 100) / 256);
                    }
                    quickSambaScan = QuickSambaScan.this;
                    quickSambaScan.mQuickScanNumber++;
                    if (QuickSambaScan.this.mQuickScanNumber == 256) {
                        QuickSambaScan.this.superSearch(this.ipHead);
                    }
                }
            }
        }
    }

    class ScanHostByPing implements Runnable {
        int index;
        String ipHead;

        ScanHostByPing(String ipHead, int index) {
            this.ipHead = ipHead;
            this.index = index;
        }

        public void run() {
            if (QuickSambaScan.this.mIsScanning) {
                String ip = this.ipHead + this.index;
                boolean isReachable = QuickSambaScan.this.pingHost(ip, 500);
                synchronized (QuickSambaScan.this) {
                    QuickSambaScan quickSambaScan = QuickSambaScan.this;
                    quickSambaScan.mProgress++;
                    if (isReachable) {
                        SambaDevice device = new SambaDevice(ip, ip);
                        device.setUrl("smb://" + ip + "/");
                        QuickSambaScan.this.onAdd(device);
                    }
                    QuickSambaScan.this.onProgress((QuickSambaScan.this.mProgress * 100) / 256);
                    if (QuickSambaScan.this.mProgress == 256) {
                        QuickSambaScan.this.onComplete(false);
                    }
                }
            }
        }
    }

    public void onScan(Context context, ArrayList<SambaDevice> savedDevices, boolean incomplete) {
        String ownIp = ZidooFileUtils.getSelfAddress(context);
        if (TextUtils.isEmpty(ownIp) || ownIp.contains(":")) {
            onComplete(false);
        } else if (!incomplete || savedDevices.size() <= 0) {
            int i;
            onStart();
            this.mIsScanning = true;
            this.mScanedIps = new boolean[256];
            this.mProgress = 0;
            this.mQuickScanNumber = 0;
            this.mQuickExecutorService = Executors.newFixedThreadPool(16);
            this.mSuperExecutorService = Executors.newFixedThreadPool(32);
            int size = savedDevices.size();
            for (i = 0; i < size; i++) {
                SambaDevice device = (SambaDevice) savedDevices.get(i);
                if (device.getType() == 4) {
                    String ip = device.getIp();
                    this.mScanedIps[Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1))] = true;
                    this.mProgress++;
                    this.mQuickScanNumber++;
                }
            }
            String ipHead = ownIp.substring(0, ownIp.lastIndexOf(".") + 1);
            for (i = 0; i < 256; i++) {
                if (!this.mScanedIps[i]) {
                    this.mQuickExecutorService.execute(new QueckScan(i, ipHead));
                }
            }
        } else {
            onComplete(true);
        }
    }

    public void stop() {
        if (this.mQuickExecutorService != null) {
            this.mQuickExecutorService.shutdown();
            this.mQuickExecutorService.shutdownNow();
        }
        if (this.mSuperExecutorService != null) {
            this.mSuperExecutorService.shutdown();
            this.mSuperExecutorService.shutdownNow();
        }
        this.mIsScanning = false;
    }

    private void superSearch(String ipHead) {
        for (int i = 0; i < 256; i++) {
            if (!this.mScanedIps[i]) {
                this.mSuperExecutorService.execute(new ScanHostByPing(ipHead, i));
            }
        }
    }

    private boolean pingHost(String host, int timeout) {
        try {
            return InetAddress.getByName(host).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
