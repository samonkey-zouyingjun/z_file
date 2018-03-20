package zidoo.nfs;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import zidoo.nfs.scan.CmdNfsScan;
import zidoo.nfs.scan.IPNfsConnect;
import zidoo.nfs.scan.NfsScan;
import zidoo.nfs.scan.PortNfsScan;

public abstract class NfsManager implements NfsMount {
    static File sRootFile;
    protected ArrayList<NfsDevice> devices = new ArrayList();
    Context mContext;
    NfsScan mNfsScan = null;
    protected OnNfsSearchListener onNfsSearchListener;

    NfsManager(Context context) {
        this.mContext = context;
    }

    public final void scanDevices(int port) {
        this.mNfsScan = new PortNfsScan(this.mContext, port);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    public final void scanDevices() {
        this.mNfsScan = new CmdNfsScan(this.mContext);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    public final void scanDevices(String ip) {
        this.mNfsScan = new IPNfsConnect(ip);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    public final boolean isSearching() {
        return this.mNfsScan != null && this.mNfsScan.isScanning();
    }

    public void setOnNfsSearchListener(OnNfsSearchListener onNfsSearchListener) {
        this.onNfsSearchListener = onNfsSearchListener;
    }

    public ArrayList<NfsFolder> openDevice(NfsDevice nfsDevice) {
        Exception ex;
        ArrayList<NfsFolder> nfsFolders = new ArrayList();
        String line = "";
        try {
            NfsFolder folder;
            Process proc = Runtime.getRuntime().exec(new String[]{"/system/bin/nfsprobe", "-e", nfsDevice.ip});
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            NfsFolder folder2 = null;
            while (true) {
                try {
                    line = buf.readLine();
                    if (line == null) {
                        break;
                    }
                    int i = line.length() - 1;
                    while (i > 0 && line.charAt(i) != ' ') {
                        i--;
                    }
                    line = line.substring(0, i).trim();
                    folder = new NfsFolder();
                    folder.setFolderPath(line);
                    folder.ip = nfsDevice.ip;
                    nfsFolders.add(folder);
                    folder2 = folder;
                } catch (Exception e) {
                    ex = e;
                    folder = folder2;
                }
            }
            folder = folder2;
        } catch (Exception e2) {
            ex = e2;
        }
        return nfsFolders;
        ex.printStackTrace();
        return nfsFolders;
    }

    public static String getSelfIP(Context context) {
        Enumeration<NetworkInterface> allNetInterfaces = null;
        String ip = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            String if_name = netInterface.getName();
            if (if_name.equals("eth0") || if_name.equals("eth1")) {
                if (!cm.getNetworkInfo(9).isConnected()) {
                }
            } else if (if_name.equals("wlan0")) {
                if (!cm.getNetworkInfo(1).isConnected()) {
                }
            }
            while (addresses.hasMoreElements()) {
                InetAddress ia = (InetAddress) addresses.nextElement();
                if (ia != null && (ia instanceof Inet4Address)) {
                    ip = ia.getHostAddress();
                    break;
                }
            }
        }
        return ip;
    }

    public boolean isNfsMounted(String ip, String sharePath, String mountPath) {
        return isNfsMounted(mountPath, true);
    }

    public static boolean isNfsMounted(String prefix) {
        IOException e;
        Throwable th;
        File file = new File("/proc/mounts");
        if (file.canRead()) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                String line;
                do {
                    try {
                        line = reader2.readLine();
                        if (line == null) {
                            if (reader2 != null) {
                                try {
                                    reader2.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e3) {
                        e2 = e3;
                        reader = reader2;
                    } catch (Throwable th2) {
                        th = th2;
                        reader = reader2;
                    }
                } while (!line.startsWith(prefix));
                if (reader2 == null) {
                    return true;
                }
                try {
                    reader2.close();
                    return true;
                } catch (IOException e22) {
                    e22.printStackTrace();
                    return true;
                }
            } catch (IOException e4) {
                e22 = e4;
                try {
                    e22.printStackTrace();
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isNfsMounted(java.lang.String r10, java.lang.String r11) {
        /*
        r2 = new java.io.File;
        r7 = "/proc/mounts";
        r2.<init>(r7);
        r7 = " %s nfs ";
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r8[r9] = r11;
        r0 = java.lang.String.format(r7, r8);
        r3 = 0;
        r7 = r2.canRead();
        if (r7 == 0) goto L_0x003f;
    L_0x001c:
        r5 = 0;
        r6 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0045 }
        r7 = new java.io.FileReader;	 Catch:{ IOException -> 0x0045 }
        r7.<init>(r2);	 Catch:{ IOException -> 0x0045 }
        r6.<init>(r7);	 Catch:{ IOException -> 0x0045 }
    L_0x0027:
        r4 = r6.readLine();	 Catch:{ IOException -> 0x0063, all -> 0x0060 }
        if (r4 == 0) goto L_0x003a;
    L_0x002d:
        r7 = r4.startsWith(r10);	 Catch:{ IOException -> 0x0063, all -> 0x0060 }
        if (r7 == 0) goto L_0x0027;
    L_0x0033:
        r7 = r4.contains(r0);	 Catch:{ IOException -> 0x0063, all -> 0x0060 }
        if (r7 == 0) goto L_0x0027;
    L_0x0039:
        r3 = 1;
    L_0x003a:
        if (r6 == 0) goto L_0x003f;
    L_0x003c:
        r6.close();	 Catch:{ IOException -> 0x0040 }
    L_0x003f:
        return r3;
    L_0x0040:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x003f;
    L_0x0045:
        r1 = move-exception;
    L_0x0046:
        r1.printStackTrace();	 Catch:{ all -> 0x0054 }
        if (r5 == 0) goto L_0x003f;
    L_0x004b:
        r5.close();	 Catch:{ IOException -> 0x004f }
        goto L_0x003f;
    L_0x004f:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x003f;
    L_0x0054:
        r7 = move-exception;
    L_0x0055:
        if (r5 == 0) goto L_0x005a;
    L_0x0057:
        r5.close();	 Catch:{ IOException -> 0x005b }
    L_0x005a:
        throw r7;
    L_0x005b:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x005a;
    L_0x0060:
        r7 = move-exception;
        r5 = r6;
        goto L_0x0055;
    L_0x0063:
        r1 = move-exception;
        r5 = r6;
        goto L_0x0046;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.nfs.NfsManager.isNfsMounted(java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isNfsMounted(java.lang.String r10, boolean r11) {
        /*
        r9 = 2;
        r1 = new java.io.File;
        r7 = "/proc/mounts";
        r1.<init>(r7);
        r2 = 0;
        r7 = r1.canRead();
        if (r7 == 0) goto L_0x0048;
    L_0x0010:
        r4 = 0;
        r5 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0058 }
        r7 = new java.io.FileReader;	 Catch:{ IOException -> 0x0058 }
        r7.<init>(r1);	 Catch:{ IOException -> 0x0058 }
        r5.<init>(r7);	 Catch:{ IOException -> 0x0058 }
    L_0x001b:
        r3 = r5.readLine();	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        if (r3 == 0) goto L_0x0043;
    L_0x0021:
        r7 = " ";
        r6 = r3.split(r7);	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        r7 = r6.length;	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        if (r7 <= r9) goto L_0x001b;
    L_0x002b:
        r7 = 2;
        r7 = r6[r7];	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        r8 = "nfs";
        r7 = r7.equals(r8);	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        if (r7 == 0) goto L_0x001b;
    L_0x0037:
        if (r11 == 0) goto L_0x0049;
    L_0x0039:
        r7 = 1;
        r7 = r6[r7];	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        r7 = r7.equals(r10);	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        if (r7 == 0) goto L_0x001b;
    L_0x0042:
        r2 = 1;
    L_0x0043:
        if (r5 == 0) goto L_0x0048;
    L_0x0045:
        r5.close();	 Catch:{ IOException -> 0x0053 }
    L_0x0048:
        return r2;
    L_0x0049:
        r7 = 0;
        r7 = r6[r7];	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        r7 = r7.equals(r10);	 Catch:{ IOException -> 0x0076, all -> 0x0073 }
        if (r7 == 0) goto L_0x001b;
    L_0x0052:
        goto L_0x0042;
    L_0x0053:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0048;
    L_0x0058:
        r0 = move-exception;
    L_0x0059:
        r0.printStackTrace();	 Catch:{ all -> 0x0067 }
        if (r4 == 0) goto L_0x0048;
    L_0x005e:
        r4.close();	 Catch:{ IOException -> 0x0062 }
        goto L_0x0048;
    L_0x0062:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0048;
    L_0x0067:
        r7 = move-exception;
    L_0x0068:
        if (r4 == 0) goto L_0x006d;
    L_0x006a:
        r4.close();	 Catch:{ IOException -> 0x006e }
    L_0x006d:
        throw r7;
    L_0x006e:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x006d;
    L_0x0073:
        r7 = move-exception;
        r4 = r5;
        goto L_0x0068;
    L_0x0076:
        r0 = move-exception;
        r4 = r5;
        goto L_0x0059;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.nfs.NfsManager.isNfsMounted(java.lang.String, boolean):boolean");
    }
}
