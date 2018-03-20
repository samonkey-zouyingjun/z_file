package zidoo.samba.exs;

import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jcifs.netbios.NbtAddress;
import zidoo.tool.ZidooFileUtils;

public class AccurateSambaScan extends SambaScan {
    ExecutorService mHostService;
    int mProgress;
    ExecutorService mSuperExecutorService;

    class GetHostTask implements Runnable {
        int index;
        String ip;

        public GetHostTask(String ipHead, int index) {
            this.ip = ipHead + index;
            this.index = index;
        }

        public void run() {
            try {
                String host = AccurateSambaScan.this.getHostName(this.ip);
                synchronized (AccurateSambaScan.this) {
                    if (host != null) {
                        SambaDevice device = new SambaDevice(this.ip, host);
                        device.setUrl("smb://" + host + "/");
                        AccurateSambaScan.this.onAdd(device);
                    }
                    AccurateSambaScan accurateSambaScan = AccurateSambaScan.this;
                    accurateSambaScan.mProgress++;
                    AccurateSambaScan.this.onProgress((AccurateSambaScan.this.mProgress * 100) / 256);
                    if (AccurateSambaScan.this.mProgress == 256) {
                        AccurateSambaScan.this.onComplete(false);
                    }
                }
                Thread.interrupted();
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                } finally {
                    Thread.interrupted();
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
            if (!AccurateSambaScan.this.mIsScanning) {
                return;
            }
            if (pingIp(this.ipHead + this.index, 1)) {
                AccurateSambaScan.this.mHostService.execute(new GetHostTask(this.ipHead, this.index));
                return;
            }
            synchronized (AccurateSambaScan.this) {
                AccurateSambaScan accurateSambaScan = AccurateSambaScan.this;
                accurateSambaScan.mProgress++;
                AccurateSambaScan.this.onProgress((AccurateSambaScan.this.mProgress * 100) / 256);
                if (AccurateSambaScan.this.mProgress == 256) {
                    AccurateSambaScan.this.onComplete(false);
                }
            }
        }

        private boolean pingIp(String ip, int timeOut) {
            int result = -1;
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("ping -c 1 -w " + timeOut + " " + ip);
                result = process.waitFor();
                process.exitValue();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } finally {
                process.destroy();
            }
            if (result == 0) {
                return true;
            }
            return false;
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
            boolean[] scanedIps = new boolean[256];
            this.mProgress = 0;
            this.mSuperExecutorService = Executors.newFixedThreadPool(16);
            this.mHostService = Executors.newFixedThreadPool(32);
            int size = savedDevices.size();
            for (i = 0; i < size; i++) {
                SambaDevice device = (SambaDevice) savedDevices.get(i);
                if (device.getType() == 4) {
                    String ip = device.getIp();
                    scanedIps[Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1))] = true;
                    this.mProgress++;
                }
            }
            String ipHead = ownIp.substring(0, ownIp.lastIndexOf(".") + 1);
            for (i = 0; i < 256; i++) {
                if (!scanedIps[i]) {
                    this.mSuperExecutorService.execute(new ScanHostByPing(ipHead, i));
                }
            }
        } else {
            onComplete(true);
        }
    }

    public void stop() {
        if (this.mHostService != null) {
            this.mHostService.shutdown();
            this.mHostService.shutdownNow();
        }
        if (this.mSuperExecutorService != null) {
            this.mSuperExecutorService.shutdown();
            this.mSuperExecutorService.shutdownNow();
        }
        this.mIsScanning = false;
    }

    private String getHostName(String share) {
        try {
            NbtAddress nbt = NbtAddress.getByName(share);
            if (nbt != null && nbt.isActive()) {
                NbtAddress[] all = NbtAddress.getAllByAddress(nbt);
                for (NbtAddress n : all) {
                    if (!n.isGroupAddress() && n.getNameType() == 0 && n.getHostName() != null) {
                        return n.getHostName();
                    }
                }
            }
            return nbt.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}
