package com.zidoo.fileexplorer.browse;

import android.content.Context;
import android.support.annotation.NonNull;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.db.MountHistoryDatabase;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jcifs.netbios.NbtAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import pers.lic.tool.PinyinCompareTool;
import zidoo.device.BlockInfo;
import zidoo.device.ZDevice;
import zidoo.file.FileType;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsFolder;
import zidoo.nfs.NfsManager;
import zidoo.nfs.OnNfsSearchListener;
import zidoo.nfs.scan.NfsScan;
import zidoo.nfs.scan.PortNfsScan;
import zidoo.samba.manager.SambaManager;
import zidoo.tool.ZidooFileUtils;

public abstract class BrowseItem implements Comparable<BrowseItem> {
    protected OnItemListListener onItemListListener;
    private final Type type;

    public static class BrowseResult {
        public static final int RESULT_ERROR = 4;
        public static final int RESULT_LOADING_NFS = 2;
        public static final int RESULT_SMB_AUTH_ERROR = 1;
        public static final int RESULT_SUCCESS = 0;
        private List<BrowseItem> items;
        private int requestPosition = 0;
        private int result;

        public BrowseResult(List<BrowseItem> items) {
            this.items = items;
        }

        public BrowseResult(int result, List<BrowseItem> items) {
            this.result = result;
            this.items = items;
        }

        public int getRequestPosition() {
            return this.requestPosition;
        }

        public void setRequestPosition(int requestPosition) {
            this.requestPosition = requestPosition;
        }

        public int getResult() {
            return this.result;
        }

        public List<BrowseItem> getItems() {
            return this.items;
        }
    }

    interface OnItemListListener {
        void onAddedOne();

        void onComplete();
    }

    enum Type {
        ROOT,
        FLASH,
        USB,
        SMB_MOUNT,
        NFS_MOUNT,
        SMB_ROOT,
        NFS_ROOT,
        SMB_SERVER,
        NFS_SERVER,
        SMB_SHARE,
        NFS_SHARE,
        FILE
    }

    public static class FileItem extends BrowseItem {
        private File file;
        private int fileType = -1;
        private int icon = -1;

        public FileItem(File file) {
            super(Type.FILE);
            this.file = file;
        }

        public FileItem(Type type, File file) {
            super(type);
            this.file = file;
        }

        public int getIcon() {
            if (this.icon == -1) {
                switch (getFileType()) {
                    case 0:
                        this.icon = R.drawable.ic_browse_dir;
                        break;
                    case 1:
                        this.icon = R.drawable.ic_browse_audio;
                        break;
                    case 2:
                        this.icon = R.drawable.ic_browse_video;
                        break;
                    case 3:
                        this.icon = R.drawable.ic_browse_image;
                        break;
                    case 4:
                        this.icon = R.drawable.ic_browse_txt;
                        break;
                    case 5:
                        this.icon = R.drawable.ic_browse_apk;
                        break;
                    case 6:
                        this.icon = R.drawable.ic_browse_ppt;
                        break;
                    case 7:
                        this.icon = R.drawable.ic_browse_word;
                        break;
                    case 8:
                        this.icon = R.drawable.ic_browse_excel;
                        break;
                    case 9:
                        this.icon = R.drawable.ic_browse_ppt;
                        break;
                    case 10:
                        this.icon = R.drawable.ic_browse_html;
                        break;
                    case 11:
                        this.icon = R.drawable.ic_browse_zip;
                        break;
                    case 12:
                        this.icon = R.drawable.ic_browse_others;
                        break;
                    default:
                        this.icon = R.drawable.ic_browse_others;
                        break;
                }
            }
            return this.icon;
        }

        public int getFileType() {
            if (this.fileType == -1) {
                this.fileType = FileType.getType(this.file);
            }
            return this.fileType;
        }

        public String getName() {
            return this.file.getName();
        }

        public File getFile() {
            return this.file;
        }

        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            String[] fileNames = this.file.list();
            if (fileNames == null) {
                return new BrowseResult(4, null);
            }
            ArrayList<BrowseItem> items = new ArrayList();
            for (String fileName : fileNames) {
                File child = new File(this.file, fileName);
                if (filter.accept(child)) {
                    items.add(new FileItem(child));
                }
            }
            return new BrowseResult(items);
        }

        public int compareTo(@NonNull BrowseItem other) {
            int dt = getType().ordinal() - other.getType().ordinal();
            if (dt != 0) {
                return dt;
            }
            if (getType() == Type.FILE) {
                if (this.file.isDirectory()) {
                    if (!other.isDirectory()) {
                        return -1;
                    }
                } else if (other.isDirectory()) {
                    return 1;
                }
            }
            return PinyinCompareTool.compareCharSequence(getName(), other.getName());
        }
    }

    public static final class NfsRootItem extends BrowseItem implements OnNfsSearchListener {
        private String ip;
        private ArrayList<BrowseItem> nfsServers = new ArrayList();
        private int state = 0;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        NfsRootItem(String ip) {
            super(Type.NFS_ROOT);
            this.ip = ip;
        }

        @NonNull
        public String getName() {
            return this.ip;
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            int code = 0;
            switch (this.state) {
                case 0:
                    scanNfs(context);
                    code = 2;
                    break;
                case 1:
                    code = 2;
                    break;
                case 2:
                    code = 0;
                    break;
            }
            return new BrowseResult(code, this.nfsServers);
        }

        private void scanNfs(Context context) {
            this.state = 1;
            NfsScan scan = new PortNfsScan(context, 111);
            scan.setOnNfsSearchListener(this);
            scan.start();
        }

        public String getIp() {
            return this.ip;
        }

        public int getIcon() {
            return R.drawable.ic_browse_nfs;
        }

        public void onNfsScanStart(int progress) {
        }

        public void onNfsDeveceChangeListener(int progress) {
        }

        public void onCompleteListener(int mode, boolean success) {
            this.state = 2;
            if (this.onItemListListener != null) {
                this.onItemListListener.onComplete();
            }
        }

        public void OnNFSDeviceAddListener(NfsDevice nfsDevice) {
            this.nfsServers.add(new NfsServerItem(nfsDevice.ip));
            if (this.onItemListListener != null) {
                this.onItemListListener.onAddedOne();
            }
        }
    }

    public static final class NfsServerItem extends BrowseItem {
        private String ip;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public NfsServerItem(String ip) {
            super(Type.NFS_SERVER);
            this.ip = ip;
        }

        public String getName() {
            return this.ip;
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            ArrayList<NfsFolder> folders = NfsFactory.getNfsManager(context, BoxModel.sModel).openDevice(new NfsDevice(this.ip));
            ArrayList<BrowseItem> items = new ArrayList(folders.size());
            Iterator it = folders.iterator();
            while (it.hasNext()) {
                items.add(new NfsShareItem((NfsFolder) it.next()));
            }
            return new BrowseResult(items);
        }

        public int getIcon() {
            return R.drawable.ic_browse_nfs;
        }
    }

    public static final class NfsShareItem extends BrowseItem {
        private NfsFolder folder;
        private FileItem mountFile = null;
        private String name = null;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public NfsShareItem(NfsFolder folder) {
            super(Type.NFS_SHARE);
            this.folder = folder;
        }

        @NonNull
        public String getName() {
            if (this.name == null) {
                this.name = this.folder.getPath();
                int e = this.name.lastIndexOf(47);
                if (e != -1) {
                    this.name = this.name.substring(e + 1);
                }
            }
            return this.name;
        }

        public FileItem getMountFile() {
            return this.mountFile;
        }

        public BrowseResult listFiles(Context context, FileFilter filter) {
            if (this.mountFile == null) {
                this.mountFile = mount(context);
            }
            if (this.mountFile != null) {
                return this.mountFile.listFiles(context, filter);
            }
            return new BrowseResult(new ArrayList(0));
        }

        private FileItem mount(Context context) {
            try {
                String ip = this.folder.ip;
                NfsManager nfsManager = NfsFactory.getNfsManager(context, BoxModel.sModel);
                String path = this.folder.getPath();
                String share = path;
                if (share.endsWith("/")) {
                    share = share.substring(0, path.length() - 1);
                }
                int e = share.lastIndexOf("/");
                if (e != -1) {
                    share = path.substring(e + 1);
                }
                String name = ip + "#" + ZidooFileUtils.encodeCommand(share);
                File mount = new File(nfsManager.getNfsRoot() + "/" + name);
                if (mount.exists()) {
                    return new FileItem(mount);
                }
                if (nfsManager.mountNfs(ip, this.folder.getPath(), name)) {
                    MountHistoryDatabase.saveMountHistory(context, "nfs://" + ip + "/" + share, this.folder.getPath(), null, null);
                    return new FileItem(mount);
                }
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        public int getIcon() {
            return R.drawable.ic_browse_nfs;
        }
    }

    public static final class RootItem extends BrowseItem {
        private int deviceTag;
        private NfsRootItem nfsRoot = null;
        private SmbRootItem smbRoot = null;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public RootItem(int deviceTag) {
            super(Type.ROOT);
            this.deviceTag = deviceTag;
        }

        public int getIcon() {
            return 0;
        }

        public String getName() {
            return null;
        }

        public String getDisplayPath() {
            return "";
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            int tag = 0;
            if (isShowDevice(2)) {
                tag = 0 | 1;
            }
            if (isShowDevice(4)) {
                tag |= 2;
            }
            if (isShowDevice(8)) {
                tag |= 4;
            }
            if (isShowDevice(16)) {
                tag |= 8;
            }
            ArrayList<ZDevice> devices = BoxModel.getModel(context).getDeviceList(tag, true);
            ArrayList<BrowseItem> items = new ArrayList(devices.size() + 2);
            for (int i = 0; i < devices.size(); i++) {
                ZDevice device = (ZDevice) devices.get(i);
                switch (device.getType()) {
                    case FLASH:
                        items.add(new FlashItem(device, context.getString(R.string.flash)));
                        break;
                    case TF:
                    case SD:
                    case HDD:
                        String label;
                        String uuid;
                        BlockInfo block = device.getBlock();
                        if (block == null) {
                            label = device.getName();
                            uuid = device.getPath();
                        } else {
                            label = block.getLabel();
                            uuid = block.getUuid();
                        }
                        items.add(new UsbItem(device, label, uuid));
                        break;
                    case SMB:
                        items.add(new SmbMountItem(device));
                        break;
                    case NFS:
                        items.add(new NfsMountItem(device));
                        break;
                    default:
                        break;
                }
            }
            items.add(getSmbRoot(context));
            items.add(getNfsRoot(context));
            return new BrowseResult(items);
        }

        private SmbRootItem getSmbRoot(Context context) {
            if (this.smbRoot == null) {
                this.smbRoot = new SmbRootItem(context.getString(R.string.smb));
            }
            return this.smbRoot;
        }

        private NfsRootItem getNfsRoot(Context context) {
            if (this.nfsRoot == null) {
                this.nfsRoot = new NfsRootItem(context.getString(R.string.nfs));
            }
            return this.nfsRoot;
        }

        private boolean isShowDevice(int tag) {
            return (this.deviceTag & tag) != 0;
        }
    }

    public static final class SmbRootItem extends BrowseItem {
        private String name;
        private ArrayList<BrowseItem> servers = null;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public SmbRootItem(String name) {
            super(Type.SMB_ROOT);
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            if (this.servers == null) {
                this.servers = listServers();
            }
            return new BrowseResult(this.servers);
        }

        private ArrayList<BrowseItem> listServers() {
            try {
                SmbFile[] groups = new SmbFile("smb://").listFiles();
                if (groups != null && groups.length > 0) {
                    ArrayList<SmbFile> smbFiles = new ArrayList();
                    for (SmbFile group : groups) {
                        try {
                            SmbFile[] servers = group.listFiles();
                            if (servers != null) {
                                Collections.addAll(smbFiles, servers);
                            }
                        } catch (SmbException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayList<BrowseItem> items = new ArrayList(smbFiles.size());
                    for (int i = 0; i < smbFiles.size(); i++) {
                        items.add(new SmbServerItem((SmbFile) smbFiles.get(i)));
                    }
                    return items;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return new ArrayList(0);
        }

        public int getIcon() {
            return R.drawable.ic_browse_smb;
        }
    }

    public static final class SmbServerItem extends BrowseItem {
        private String password = "";
        private SmbFile server;
        private String user = "guest";

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public SmbServerItem(SmbFile server) {
            super(Type.SMB_SERVER);
            this.server = server;
        }

        @NonNull
        public String getName() {
            return this.server.getServer();
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            try {
                ArrayList<BrowseItem> items = new ArrayList();
                SmbFile[] shares = new SmbFile(this.server.getURL(), new NtlmPasswordAuthentication(this.server.getServer(), this.user, this.password)).listFiles(Utils.getDefaultSmbFileFilter());
                if (shares != null) {
                    for (SmbFile share : shares) {
                        items.add(new SmbShareItem(share, this.user, this.password));
                    }
                }
                return new BrowseResult(items);
            } catch (SmbException e) {
                e.printStackTrace();
                return new BrowseResult(1, null);
            }
        }

        public String getUser() {
            return this.user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getIcon() {
            return R.drawable.ic_browse_smb;
        }
    }

    public static final class SmbShareItem extends BrowseItem {
        private String ip = null;
        private String name = null;
        private String password;
        private SmbFile smbShare;
        private String user;

        public /* bridge */ /* synthetic */ int compareTo(@NonNull Object obj) {
            return super.compareTo((BrowseItem) obj);
        }

        public SmbShareItem(SmbFile share, String user, String password) {
            super(Type.SMB_SHARE);
            this.smbShare = share;
            this.user = user;
            this.password = password;
        }

        public String getName() {
            if (this.name == null) {
                this.name = this.smbShare.getName();
                if (this.name.endsWith("/")) {
                    this.name = this.name.substring(0, this.name.length() - 1);
                }
            }
            return this.name;
        }

        @NonNull
        public BrowseResult listFiles(Context context, FileFilter filter) {
            boolean authError = false;
            try {
                String ip = getIp();
                SambaManager sambaManager = SambaManager.getManager(context, BoxModel.getBoxModel(context));
                String share = this.smbShare.getShare();
                File mount = new File(sambaManager.getSmbRoot() + "/" + (ip + "#" + ZidooFileUtils.encodeCommand(share)));
                if (mount.exists()) {
                    return new FileItem(mount).listFiles(context, filter);
                }
                try {
                    if (new SmbFile(this.smbShare.getURL(), new NtlmPasswordAuthentication(ip, this.user, this.password)).list() != null && sambaManager.mountSmb("//" + ip + "/" + share, mount.getPath(), ip, this.user, this.password)) {
                        MountHistoryDatabase.saveMountHistory(context, "smb://" + ip + "/" + share, null, this.user, this.password);
                        return new FileItem(mount).listFiles(context, filter);
                    }
                } catch (SmbException e) {
                    authError = true;
                    e.printStackTrace();
                }
                if (authError) {
                    return new BrowseResult(1, null);
                }
                return new BrowseResult(new ArrayList(0));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        public String getIp() throws UnknownHostException {
            if (this.ip == null) {
                this.ip = NbtAddress.getByName(this.smbShare.getServer()).getHostAddress();
            }
            return this.ip;
        }

        public SmbFile getSmbShare() {
            return this.smbShare;
        }

        public String getUser() {
            return this.user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getIcon() {
            return R.drawable.ic_browse_smb;
        }
    }

    public static final class FlashItem extends FileItem {
        private String name;

        FlashItem(File file, String name) {
            super(Type.FLASH, file);
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getIcon() {
            return R.drawable.ic_browse_flash;
        }
    }

    public static abstract class MountItem extends FileItem {
        protected String ip;
        protected String name;

        public MountItem(Type type, File file) {
            super(type, file);
            String fileName = file.getName();
            int i = fileName.indexOf(35);
            if (i == -1) {
                this.ip = "";
                this.name = fileName;
                return;
            }
            this.ip = fileName.substring(0, i);
            this.name = ZidooFileUtils.decodeCommand(fileName.substring(i + 1));
        }

        public String getName() {
            return "//" + this.ip + "/" + this.name;
        }
    }

    public static final class UsbItem extends FileItem {
        private String label;
        private String uuid;

        UsbItem(File file, String label, String uuid) {
            super(Type.USB, file);
            this.label = label;
            this.uuid = uuid;
        }

        public String getName() {
            return this.label;
        }

        public String getUuid() {
            return this.uuid;
        }

        public int getIcon() {
            return R.drawable.ic_browse_usb;
        }
    }

    public static final class NfsMountItem extends MountItem {
        NfsMountItem(File file) {
            super(Type.NFS_MOUNT, file);
        }

        public String getDisplayPath() {
            return "NFS://" + this.ip + "/" + this.name;
        }

        public int getIcon() {
            return R.drawable.ic_browse_nfs;
        }
    }

    public static final class SmbMountItem extends MountItem {
        SmbMountItem(File file) {
            super(Type.SMB_MOUNT, file);
        }

        public String getDisplayPath() {
            return "SMB://" + this.ip + "/" + this.name;
        }

        public int getIcon() {
            return R.drawable.ic_browse_smb;
        }
    }

    public abstract int getIcon();

    public abstract String getName();

    @NonNull
    public abstract BrowseResult listFiles(Context context, FileFilter fileFilter);

    public BrowseItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public void setOnItemListListener(OnItemListListener onItemListListener) {
        this.onItemListListener = onItemListListener;
    }

    public String getDisplayPath() {
        return getName();
    }

    public boolean isDirectory() {
        return true;
    }

    public int compareTo(@NonNull BrowseItem other) {
        int dt = this.type.ordinal() - other.getType().ordinal();
        return dt == 0 ? PinyinCompareTool.compareCharSequence(getName(), other.getName()) : dt;
    }
}
