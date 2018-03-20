package zidoo.browse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import jcifs.netbios.NbtAddress;
import zidoo.device.ZDevice;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsFolder;
import zidoo.nfs.NfsManager;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public class BrowseUtils {
    @Deprecated
    public static boolean browsing(Activity activity, String title, String name, int devices, int filter, int targets, String[] customExtras, int flag, int bgdResid, int help, float scale, int shade, int horizontalPadding, int verticalPadding, int requestCode, String initialPath, int clickModel) {
        try {
            PackageInfo pkgInfo = activity.getPackageManager().getPackageInfo("com.zidoo.fileexplorer", 0);
            if (pkgInfo != null && pkgInfo.versionCode > 100) {
                Intent intent = new Intent(BrowseConstant.FILE_EXPLORER_ACTION);
                intent.putExtra(BrowseConstant.EXTRA_TITLE, title);
                intent.putExtra("name", name);
                intent.putExtra(BrowseConstant.EXTRA_DEVICE, devices);
                intent.putExtra(BrowseConstant.EXTRA_FILTER, filter);
                intent.putExtra(BrowseConstant.EXTRA_CUSTOM_EXTRAS, customExtras);
                intent.putExtra(BrowseConstant.EXTRA_TARGET, targets);
                intent.putExtra(BrowseConstant.EXTRA_FLAG, flag);
                intent.putExtra(BrowseConstant.EXTRA_PACKAGE_NAME, activity.getPackageName());
                intent.putExtra(BrowseConstant.EXTRA_BGD, bgdResid);
                intent.putExtra(BrowseConstant.EXTRA_HELP, help);
                intent.putExtra(BrowseConstant.EXTRA_SCALE, scale);
                intent.putExtra(BrowseConstant.EXTRA_SHADE, shade);
                intent.putExtra(BrowseConstant.EXTRA_HORIZONTAL_PADDING, horizontalPadding);
                intent.putExtra(BrowseConstant.EXTRA_VERTICAL_PADDING, verticalPadding);
                intent.putExtra(BrowseConstant.EXTRA_IDENTIFIER, initialPath);
                intent.putExtra(BrowseConstant.EXTRA_CLICK_MODEL, clickModel);
                intent.addFlags(32768);
                activity.startActivityForResult(intent, requestCode);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static IdentifyResult identify(Context context, FileIdentifier identifier) {
        return identify(context, identifier, BoxModel.getModelCode(context));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zidoo.browse.IdentifyResult identify(android.content.Context r5, zidoo.browse.FileIdentifier r6, int r7) {
        /*
        r4 = 0;
        r1 = 0;
        r2 = r6.getType();	 Catch:{ Exception -> 0x0032 }
        switch(r2) {
            case 0: goto L_0x0012;
            case 1: goto L_0x0023;
            case 2: goto L_0x0028;
            case 3: goto L_0x002d;
            default: goto L_0x0009;
        };
    L_0x0009:
        if (r1 != 0) goto L_0x0011;
    L_0x000b:
        r1 = new zidoo.browse.IdentifyResult;
        r2 = -1;
        r1.<init>(r2, r4, r4);
    L_0x0011:
        return r1;
    L_0x0012:
        r2 = r6.getExtra();	 Catch:{ Exception -> 0x0032 }
        r3 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0032 }
        r3 = r3.getPath();	 Catch:{ Exception -> 0x0032 }
        r1 = checkFileExist(r2, r3);	 Catch:{ Exception -> 0x0032 }
        goto L_0x0009;
    L_0x0023:
        r1 = identifyUsb(r5, r6, r7);	 Catch:{ Exception -> 0x0032 }
        goto L_0x0009;
    L_0x0028:
        r1 = identifySmb(r5, r6, r7);	 Catch:{ Exception -> 0x0032 }
        goto L_0x0009;
    L_0x002d:
        r1 = identifyNfs(r5, r6, r7);	 Catch:{ Exception -> 0x0032 }
        goto L_0x0009;
    L_0x0032:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: zidoo.browse.BrowseUtils.identify(android.content.Context, zidoo.browse.FileIdentifier, int):zidoo.browse.IdentifyResult");
    }

    private static IdentifyResult identifyUsb(Context context, FileIdentifier identifier, int model) {
        BoxModel boxModel = BoxModel.getModel(context, model);
        boxModel.setAutoSaveDevice(false);
        ArrayList<ZDevice> devices = boxModel.getDeviceList(2, true);
        String uuid = identifier.getUuid();
        ArrayList<ZDevice> unrecognized = new ArrayList();
        Iterator it = devices.iterator();
        while (it.hasNext()) {
            ZDevice device = (ZDevice) it.next();
            if (device.getBlock() != null && device.getBlock().getUuid() != null && device.getBlock().getUuid().equals(uuid)) {
                return checkFileExist(new File(device, identifier.getUri()), device.getPath());
            }
            unrecognized.add(device);
        }
        it = unrecognized.iterator();
        while (it.hasNext()) {
            device = (ZDevice) it.next();
            if ((device.getPath() + "/").equals(uuid)) {
                File file = new File(device, identifier.getUri());
                if (file.exists()) {
                    return new IdentifyResult(0, file.getPath(), device.getPath());
                }
            }
        }
        return new IdentifyResult(-3, null, null);
    }

    private static IdentifyResult identifySmb(Context context, FileIdentifier identifier, int model) {
        String url = identifier.getUri();
        if (url.matches("^smb://[^/]+/$")) {
            return new IdentifyResult(1, url, null);
        }
        String ip;
        SambaManager sambaManager = SambaManager.getManager(context, model);
        String rootPath = sambaManager.getSmbRoot();
        String src = url.substring(6);
        int s = src.indexOf("/");
        String server = "";
        String share = "";
        String other = "";
        if (s != -1) {
            server = src.substring(0, s);
            share = src.substring(s + 1);
            int i = share.indexOf("/");
            if (i != -1) {
                other = share.substring(i);
                share = share.substring(0, i);
            }
        }
        try {
            ip = NbtAddress.getByName(server).getHostAddress();
        } catch (UnknownHostException e) {
            ip = identifier.getUuid();
        }
        String mountPoint = rootPath + "/" + (ip + "#" + ZidooFileUtils.encodeCommand(share));
        String mountPath = mountPoint + other;
        if (sambaManager.isMounted(ip, share, mountPoint)) {
            return checkFileExist(mountPath, mountPoint);
        }
        IdentifyResult result = null;
        if (!TextUtils.isEmpty(ip)) {
            if (sambaManager.mountSmb("smb://" + ip + "/" + share + "/", ip, identifier.getUser(), identifier.getPassword())) {
                result = checkFileExist(mountPath, mountPoint);
            }
        }
        if (result == null) {
            result = new IdentifyResult(-4, null, null);
        }
        sambaManager.destory();
        return result;
    }

    private static IdentifyResult identifyNfs(Context context, FileIdentifier identifier, int model) {
        String url = identifier.getUri();
        String ip = identifier.getUuid();
        if (url.equals(ip)) {
            return new IdentifyResult(2, ip, null);
        }
        String uri = url.substring(url.indexOf("/") + 1);
        int p = uri.indexOf("/");
        String share = p == -1 ? uri : uri.substring(0, p);
        NfsManager nfsManager = NfsFactory.getNfsManager(context);
        String nfsRoot = nfsManager.getNfsRoot();
        String mountName = ip + "#" + share;
        String mountPath = nfsRoot + "/" + mountName;
        if (new File(mountPath).exists() && NfsManager.isNfsMounted(ip, mountPath)) {
            String str;
            StringBuilder append = new StringBuilder().append(mountPath);
            if (p == -1) {
                str = "";
            } else {
                str = uri.substring(p);
            }
            return checkFileExist(append.append(str).toString(), mountPath);
        }
        String sharePath = null;
        try {
            ArrayList<NfsFolder> folders = NfsFactory.getNfsManager(context, model).openDevice(new NfsDevice(ip));
            if (folders.size() > 0) {
                Iterator it = folders.iterator();
                while (it.hasNext()) {
                    String path = ((NfsFolder) it.next()).getPath();
                    String fn = new String(path);
                    if (fn.endsWith("/")) {
                        fn = fn.substring(0, path.length() - 1);
                    }
                    int fe = fn.lastIndexOf("/");
                    if (fe != -1) {
                        fn = path.substring(fe + 1);
                    }
                    if (share.equals(fn)) {
                        sharePath = path;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (sharePath == null) {
            return new IdentifyResult(-5, null, null);
        }
        if (!nfsManager.mountNfs(ip, sharePath, mountName)) {
            return new IdentifyResult(-6, null, null);
        }
        return checkFileExist(mountPath + (p == -1 ? "" : uri.substring(p)), mountPath);
    }

    private static IdentifyResult checkFileExist(String path, String device) {
        return new IdentifyResult(new File(path).exists() ? 0 : -2, path, device);
    }

    private static IdentifyResult checkFileExist(File file, String device) {
        return new IdentifyResult(file.exists() ? 0 : -2, file.getPath(), device);
    }

    public static String identifierToUrl(FileIdentifier identifier) {
        switch (identifier.getType()) {
            case 0:
                return "flash://" + identifier.getUri();
            case 1:
                return "usb://" + identifier.getUuid() + identifier.getUri();
            case 2:
                String uri = identifier.getUri().replaceFirst("smb://[^/]*", "");
                return String.format(Locale.getDefault(), "smb://%s%s?user=%s&password=%s&ip=%s&host=%s", new Object[]{identifier.getUuid(), uri, identifier.getUser(), identifier.getPassword(), identifier.getUuid(), identifier.getExtra()});
            case 3:
                return String.format(Locale.getDefault(), "nfs://%s", new Object[]{identifier.getUri()});
            default:
                return null;
        }
    }

    public static FileIdentifier urlToIdentifier(String url) {
        String uri;
        int s;
        if (url.startsWith("usb:")) {
            String uuid;
            int p = url.indexOf(63);
            if (p != -1) {
                url = url.substring(0, p);
            }
            uri = url.substring(6);
            s = uri.indexOf(47);
            if (s == -1) {
                uuid = uri;
                uri = "";
            } else {
                uuid = uri.substring(0, s);
                uri = uri.substring(s);
            }
            FileIdentifier usb = new FileIdentifier(1, uri, uuid);
            usb.setExtra(uuid);
            return usb;
        }
        if (url.startsWith("smb:")) {
            s = url.indexOf(63);
            uri = url.substring(0, s);
            String[] ss = url.substring(s + 1).split("&");
            String user = ss[0].substring(5);
            String password = ss[1].substring(9);
            String ip = ss[2].substring(3);
            String host = ss[3].substring(5);
            FileIdentifier smb = new FileIdentifier(2, uri, ip);
            smb.setUser(user);
            smb.setPassword(password);
            smb.setExtra(host);
            return smb;
        }
        if (url.startsWith("nfs:")) {
            uri = url.substring(6);
            s = uri.indexOf(47);
            ip = s == -1 ? uri : uri.substring(0, s);
            FileIdentifier nfs = new FileIdentifier(3, uri, ip);
            nfs.setExtra(ip);
            return nfs;
        }
        uri = url.substring(8);
        FileIdentifier flash = new FileIdentifier(0, uri, "Flash");
        flash.setExtra(Environment.getExternalStorageDirectory().getPath() + uri);
        return flash;
    }
}
