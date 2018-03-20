package zidoo.samba.exs;

import android.content.Context;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jcifs.netbios.NbtAddress;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class NormalSambaScan extends SambaScan {
    ExecutorService mExecutorService = null;
    int mProgress;
    ArrayList<SambaDevice> mSavedDevices;
    int mServerCount;

    class MakeDeviceTask implements Runnable {
        SmbFile server;

        public MakeDeviceTask(SmbFile server) {
            this.server = server;
        }

        public void run() {
            UnknownHostException e;
            SambaDevice device = null;
            try {
                String hostName = this.server.getServer();
                SambaDevice device2 = new SambaDevice(NbtAddress.getByName(hostName).getHostAddress(), hostName);
                try {
                    device2.setUrl("smb://" + hostName + "/");
                    device = device2;
                } catch (UnknownHostException e2) {
                    e = e2;
                    device = device2;
                    e.printStackTrace();
                    NormalSambaScan.this.check(device);
                }
            } catch (UnknownHostException e3) {
                e = e3;
                e.printStackTrace();
                NormalSambaScan.this.check(device);
            }
            NormalSambaScan.this.check(device);
        }
    }

    public void onScan(Context context, final ArrayList<SambaDevice> devices, final boolean incomplete) {
        new Thread(new Runnable() {
            public void run() {
                NormalSambaScan.this.mIsScanning = true;
                NormalSambaScan.this.mProgress = 0;
                NormalSambaScan.this.mServerCount = 0;
                boolean scan = false;
                try {
                    NormalSambaScan.this.mSavedDevices = devices;
                    if (!incomplete || NormalSambaScan.this.mSavedDevices.size() <= 0) {
                        NormalSambaScan.this.onStart();
                        NormalSambaScan.this.onProgress(3);
                        SmbFile[] groups = new SmbFile("smb://").listFiles();
                        if (groups == null || groups.length <= 0) {
                            NormalSambaScan.this.onProgress(100);
                            if (!scan) {
                                NormalSambaScan.this.onComplete(false);
                                return;
                            }
                            return;
                        }
                        NormalSambaScan.this.onProgress(9);
                        SmbFile[] devices = new SmbFile[0];
                        for (SmbFile smbFile : groups) {
                            try {
                                SmbFile[] servers = smbFile.listFiles();
                                SmbFile[] temp = new SmbFile[(devices.length + servers.length)];
                                System.arraycopy(devices, 0, temp, 0, devices.length);
                                System.arraycopy(servers, 0, temp, devices.length, servers.length);
                                devices = temp;
                            } catch (SmbException e) {
                                Log.e("NormalSambaScan", "list server", e);
                            } catch (MalformedURLException e2) {
                                Log.e("NormalSambaScan", "startSearch", e2);
                            }
                        }
                        NormalSambaScan.this.onProgress(20);
                        NormalSambaScan.this.mServerCount = devices.length;
                        NormalSambaScan.this.mExecutorService = Executors.newFixedThreadPool(16);
                        for (SmbFile smbFile2 : devices) {
                            NormalSambaScan.this.mExecutorService.execute(new MakeDeviceTask(smbFile2));
                        }
                        scan = true;
                        if (!scan) {
                            NormalSambaScan.this.onComplete(false);
                            return;
                        }
                        return;
                    }
                    NormalSambaScan.this.onComplete(true);
                } catch (MalformedURLException e22) {
                    Log.e("NormalSambaScan", "startSearch", e22);
                } catch (SmbException e3) {
                    Log.e("NormalSambaScan", "startSearch", e3);
                }
            }
        }).start();
    }

    @Deprecated
    public void stop() {
        if (this.mExecutorService != null) {
            this.mExecutorService.shutdown();
            this.mExecutorService.shutdownNow();
        }
        this.mIsScanning = false;
    }

    private synchronized void check(SambaDevice device) {
        this.mProgress++;
        if (!(device == null || isSavedDevice(device, this.mSavedDevices))) {
            onAdd(device);
        }
        float p = ((float) this.mProgress) / ((float) this.mServerCount);
        onProgress(((int) (80.0f * (((-p) * p) + (2.0f * p)))) + 20);
        if (this.mProgress == this.mServerCount) {
            onComplete(false);
        }
    }
}
