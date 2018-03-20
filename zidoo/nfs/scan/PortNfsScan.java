package zidoo.nfs.scan;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import zidoo.nfs.NfsDevice;
import zidoo.tool.ZidooFileUtils;

public class PortNfsScan extends NfsScan {
    private Context context;
    private ExecutorService mQuickExecutorService = Executors.newFixedThreadPool(16);
    private int mQuickScanNumber = 0;
    private boolean[] mScanedIps = new boolean[256];
    private ExecutorService mSuperExecutorService = Executors.newFixedThreadPool(8);
    private int port;
    private int progress = 0;
    private boolean stop = true;

    private class QueckScan implements Runnable {
        String ipHead;
        int portNum;
        int position;

        QueckScan(int position, String ipHead, int portNum) {
            this.ipHead = ipHead;
            this.position = position;
            this.portNum = portNum;
        }

        public void run() {
            if (!PortNfsScan.this.stop) {
                Socket socket = null;
                boolean isReachable = false;
                String ip = this.ipHead + this.position;
                try {
                    InetAddress ia = InetAddress.getByName(ip);
                    if (ia.isReachable(50)) {
                        isReachable = true;
                        socket = new Socket(ia, this.portNum);
                    }
                    synchronized (PortNfsScan.this) {
                        if (isReachable) {
                            PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                            PortNfsScan.this.mScanedIps[this.position] = true;
                            PortNfsScan.this.onAdd(new NfsDevice(ip));
                            PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        }
                        PortNfsScan.this.mQuickScanNumber = PortNfsScan.this.mQuickScanNumber + 1;
                        if (PortNfsScan.this.mQuickScanNumber == 256) {
                            PortNfsScan.this.superSearch(this.ipHead, this.portNum);
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnknownHostException e2) {
                    e2.printStackTrace();
                    synchronized (PortNfsScan.this) {
                        if (null != null) {
                            PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                            PortNfsScan.this.mScanedIps[this.position] = true;
                            PortNfsScan.this.onAdd(new NfsDevice(ip));
                            PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        }
                        PortNfsScan.this.mQuickScanNumber = PortNfsScan.this.mQuickScanNumber + 1;
                        if (PortNfsScan.this.mQuickScanNumber == 256) {
                            PortNfsScan.this.superSearch(this.ipHead, this.portNum);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (UnknownHostException e22) {
                                e22.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                    synchronized (PortNfsScan.this) {
                        if (null != null) {
                            PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                            PortNfsScan.this.mScanedIps[this.position] = true;
                            PortNfsScan.this.onAdd(new NfsDevice(ip));
                            PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        }
                        PortNfsScan.this.mQuickScanNumber = PortNfsScan.this.mQuickScanNumber + 1;
                        if (PortNfsScan.this.mQuickScanNumber == 256) {
                            PortNfsScan.this.superSearch(this.ipHead, this.portNum);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (PortNfsScan.this) {
                        if (null != null) {
                            PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                            PortNfsScan.this.mScanedIps[this.position] = true;
                            PortNfsScan.this.onAdd(new NfsDevice(ip));
                            PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        }
                        PortNfsScan.this.mQuickScanNumber = PortNfsScan.this.mQuickScanNumber + 1;
                        if (PortNfsScan.this.mQuickScanNumber == 256) {
                            PortNfsScan.this.superSearch(this.ipHead, this.portNum);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e322) {
                                e322.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private class ScanHostByPing implements Runnable {
        String ip = null;
        int portNum;

        ScanHostByPing(String ip, int portNum) {
            this.ip = ip;
            this.portNum = portNum;
        }

        public void run() {
            if (!PortNfsScan.this.stop) {
                Socket socket = null;
                boolean isReachable = false;
                try {
                    InetAddress ia = InetAddress.getByName(this.ip);
                    if (ia.isReachable(50)) {
                        isReachable = true;
                        socket = new Socket(ia, this.portNum);
                    }
                    synchronized (PortNfsScan.this) {
                        PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                        if (isReachable) {
                            PortNfsScan.this.onAdd(new NfsDevice(this.ip));
                        }
                        PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        if (PortNfsScan.this.progress == 256) {
                            PortNfsScan.this.stop = true;
                            PortNfsScan.this.onComplete(0, true);
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnknownHostException e2) {
                    e2.printStackTrace();
                    synchronized (PortNfsScan.this) {
                        PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                        if (null != null) {
                            PortNfsScan.this.onAdd(new NfsDevice(this.ip));
                        }
                        PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        if (PortNfsScan.this.progress == 256) {
                            PortNfsScan.this.stop = true;
                            PortNfsScan.this.onComplete(0, true);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (UnknownHostException e22) {
                                e22.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                    synchronized (PortNfsScan.this) {
                        PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                        if (null != null) {
                            PortNfsScan.this.onAdd(new NfsDevice(this.ip));
                        }
                        PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        if (PortNfsScan.this.progress == 256) {
                            PortNfsScan.this.stop = true;
                            PortNfsScan.this.onComplete(0, true);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (PortNfsScan.this) {
                        PortNfsScan.this.progress = PortNfsScan.this.progress + 1;
                        if (null != null) {
                            PortNfsScan.this.onAdd(new NfsDevice(this.ip));
                        }
                        PortNfsScan.this.onProgress(PortNfsScan.this.progress);
                        if (PortNfsScan.this.progress == 256) {
                            PortNfsScan.this.stop = true;
                            PortNfsScan.this.onComplete(0, true);
                        }
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e322) {
                                e322.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public PortNfsScan(Context context, int port) {
        this.context = context;
        this.port = port;
    }

    public void start() {
        String localURL = ZidooFileUtils.getSelfAddress(this.context);
        if (TextUtils.isEmpty(localURL)) {
            onComplete(0, false);
            return;
        }
        this.stop = false;
        onStart(0);
        String ipHead = localURL.substring(0, localURL.lastIndexOf(".") + 1);
        for (int i = 0; i < 256; i++) {
            this.mQuickExecutorService.execute(new QueckScan(i, ipHead, this.port));
        }
    }

    public boolean isScanning() {
        return !this.stop;
    }

    void onProgress(int progress) {
        super.onProgress((progress * 100) / 256);
    }

    private void superSearch(String ipHead, int portNum) {
        for (int i = 0; i < 256; i++) {
            if (!this.mScanedIps[i]) {
                this.mSuperExecutorService.execute(new ScanHostByPing(ipHead + i, portNum));
            }
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
    }
}
