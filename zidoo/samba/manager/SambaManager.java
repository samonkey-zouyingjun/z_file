package zidoo.samba.manager;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFilenameFilter;
import jcifs.smb.SmbSession;
import zidoo.model.BoxModel;
import zidoo.samba.exs.AccurateSambaScan;
import zidoo.samba.exs.NormalSambaScan;
import zidoo.samba.exs.OnRecvMsgListener;
import zidoo.samba.exs.QuickSambaScan;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.exs.SambaScan;
import zidoo.tool.ZidooFileUtils;

public abstract class SambaManager implements SmbMount {
    Context mContext;
    SambaScan mSambaScan = null;
    OnRecvMsgListener onRecvMsgListener = null;

    SambaManager(Context context) {
        this.mContext = context;
    }

    public static SambaManager getManager(Context context, int model) {
        switch (model) {
            case 0:
                return new DefaultSmbManager(context);
            case 1:
                return new MstarSmbManager(context);
            case 2:
                return new H3SmbManager(context);
            case 3:
            case BoxModel.MODEL_ROCKCHIP_SDK_23 /*3001*/:
                return new RockSmbManager(context);
            case 4:
                return new AmlogicSmbManager(context);
            case 5:
                return new RTD1295SmbManager(context);
            case 6:
                return new Amlogic905xSmbManager(context);
            case 7:
                return new Rock3229SmbManager(context);
            case 8:
                return new Rock3328SmbManager(context);
            case 9:
                return new H6SmbManager(context);
            default:
                return new DefaultSmbManager(context);
        }
    }

    public static MstarSmbManager MSTAR_MANAGER(Context context) {
        return new MstarSmbManager(context);
    }

    public static DefaultSmbManager DEFAULT_MANAGER(Context context) {
        return new DefaultSmbManager(context);
    }

    public static H3SmbManager H3_MANAGER(Context context) {
        return new H3SmbManager(context);
    }

    public void setOnRecvMsgListener(OnRecvMsgListener onRecvMsglistener) {
        this.onRecvMsgListener = onRecvMsglistener;
    }

    public void searchSambaDevice(int model, final boolean incomplete, final ArrayList<SambaDevice> devices) {
        if (this.onRecvMsgListener != null) {
            if (this.mSambaScan != null) {
                this.mSambaScan.stop();
            }
            switch (model) {
                case 0:
                    this.mSambaScan = new AccurateSambaScan();
                    break;
                case 1:
                    this.mSambaScan = new NormalSambaScan();
                    break;
                case 2:
                    this.mSambaScan = new QuickSambaScan();
                    break;
            }
            this.mSambaScan.setOnRecvMsgListener(this.onRecvMsgListener);
            new Thread(new Runnable() {
                public void run() {
                    SambaManager.this.mSambaScan.scan(SambaManager.this.mContext, devices, incomplete);
                }
            }).start();
        }
    }

    public boolean isSearching() {
        return this.mSambaScan != null && this.mSambaScan.isScanning();
    }

    public void destory() {
    }

    public final boolean mountSmb(SmbFile smbFile, SambaDevice device) {
        return mountSmb(smbFile.getPath(), device.getIp(), device.getUser(), device.getPassWord());
    }

    public boolean mountSmb(String url, String ip, String user, String pwd) {
        String share = getFileName(url);
        String name = ZidooFileUtils.encodeCommand(share);
        return mountSmb("//" + ip + "/" + share, getSmbRoot() + "/" + ip + "#" + name, ip, user, pwd);
    }

    public static SmbFile[] openDevice(SambaDevice smbDevice) throws SmbException, MalformedURLException, UnknownHostException {
        UniAddress mydomaincontroller = UniAddress.getByName(smbDevice.getIp());
        NtlmPasswordAuthentication mycreds = new NtlmPasswordAuthentication(smbDevice.getIp(), smbDevice.getUser(), smbDevice.getPassWord());
        SmbSession.logon(mydomaincontroller, mycreds);
        return new SmbFile("smb://" + smbDevice.getIp(), mycreds).listFiles(new SmbFileFilter() {
            public boolean accept(SmbFile file) throws SmbException {
                return (file.getPath().endsWith("$") || file.getPath().endsWith("$/")) ? false : true;
            }
        });
    }

    public static String[] openSmbDevice(SambaDevice smbDevice) throws UnknownHostException, SmbException, MalformedURLException {
        UniAddress mydomaincontroller = UniAddress.getByName(smbDevice.getIp());
        NtlmPasswordAuthentication mycreds = new NtlmPasswordAuthentication(smbDevice.getIp(), smbDevice.getUser(), smbDevice.getPassWord());
        SmbSession.logon(mydomaincontroller, mycreds);
        return new SmbFile("smb://" + smbDevice.getIp(), mycreds).list(new SmbFilenameFilter() {
            public boolean accept(SmbFile file, String name) throws SmbException {
                return (name.endsWith("$") || name.endsWith("$/")) ? false : true;
            }
        });
    }

    public boolean mountSmbs(SmbFile[] smbFiles, SambaDevice device) {
        boolean success = false;
        for (SmbFile smbFile : smbFiles) {
            success |= mountSmb(smbFile, device);
        }
        return success;
    }

    public boolean unMountSmbs(File parent) {
        boolean success = true;
        File[] files = parent.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= unMountSmb(file);
                }
            }
        }
        return success;
    }

    public boolean isMounted(String... param) {
        return isMounted("//" + param[0] + "/" + param[1], param[2]);
    }

    public static boolean isMounted(String url, String path) {
        IOException e;
        Throwable th;
        File file = new File("/proc/mounts");
        boolean find = false;
        if (file.canRead()) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                try {
                    String pre = String.format("%s %s cifs ", new Object[]{url, path});
                    String line;
                    do {
                        line = reader2.readLine();
                        if (line == null) {
                            break;
                        }
                    } while (!line.startsWith(pre));
                    find = true;
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (IOException e3) {
                    e2 = e3;
                    reader = reader2;
                    try {
                        e2.printStackTrace();
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                        return find;
                    } catch (Throwable th2) {
                        th = th2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    reader = reader2;
                    if (reader != null) {
                        reader.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e222 = e4;
                e222.printStackTrace();
                if (reader != null) {
                    reader.close();
                }
                return find;
            }
        }
        return find;
    }

    public static boolean isMounted(String regularExpression) {
        IOException e;
        Throwable th;
        File file = new File("/proc/mounts");
        boolean find = false;
        if (file.canRead()) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                String line;
                do {
                    try {
                        line = reader2.readLine();
                        if (line == null) {
                            break;
                        }
                    } catch (IOException e2) {
                        e = e2;
                        reader = reader2;
                    } catch (Throwable th2) {
                        th = th2;
                        reader = reader2;
                    }
                } while (!line.matches("regularExpression"));
                find = true;
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (IOException e4) {
                e3 = e4;
                try {
                    e3.printStackTrace();
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    return find;
                } catch (Throwable th3) {
                    th = th3;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        }
        return find;
    }

    public static final boolean isSameDevice(SambaDevice ld, SambaDevice rd) {
        if (ld.getType() != rd.getType() || !ld.getIp().equals(rd.getIp()) || !ld.getUser().equals(rd.getUser()) || !ld.getPassWord().equals(rd.getPassWord())) {
            return false;
        }
        if (ld.getType() == 4) {
            return true;
        }
        String lu = ld.getUrl().substring(6);
        lu = lu.substring(lu.indexOf("/"));
        String ru = ld.getUrl().substring(6);
        return lu.equals(ru.substring(ru.indexOf("/")));
    }

    String getFileName(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        int e = url.lastIndexOf("/");
        if (e != -1) {
            return url.substring(e + 1);
        }
        return url;
    }
}
