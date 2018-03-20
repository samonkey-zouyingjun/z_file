package com.zidoo.fileexplorer.task;

import android.os.Handler;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.FilePath;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.bean.PathInfo;
import com.zidoo.fileexplorer.bean.ProtocolPath;
import com.zidoo.fileexplorer.bean.SharePath;
import com.zidoo.fileexplorer.bean.StableFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import zidoo.samba.exs.SambaDevice;
import zidoo.tool.ZidooFileUtils;

public class IdentifyUriTask extends BaseTask<Result> {
    private FileMolder molder;
    private String uri;

    public static final class Result {
        DeviceInfo device;
        int deviceIndex;
        int enterIndex = 0;
        File file;
        File[] files;
        int listIndex;
        ListType listType;
        ArrayList<PathInfo> pathInfos;
        boolean success = false;

        public void setResult(int listIndex, File file, File[] files, ListType listType, int deviceIndex, DeviceInfo device, ArrayList<PathInfo> pathInfos) {
            this.listIndex = listIndex;
            this.file = file;
            this.files = files;
            this.listType = listType;
            this.deviceIndex = deviceIndex;
            this.device = device;
            this.pathInfos = pathInfos;
            this.success = true;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public DeviceInfo getDevice() {
            return this.device;
        }

        public int getDeviceIndex() {
            return this.deviceIndex;
        }

        public int getListIndex() {
            return this.listIndex;
        }

        public ListType getListType() {
            return this.listType;
        }

        public File getParent() {
            return this.file;
        }

        public File[] getChildren() {
            return this.files;
        }

        public ArrayList<PathInfo> getPathInfos() {
            return this.pathInfos;
        }

        public void setEnterIndex(int enterIndex) {
            this.enterIndex = enterIndex;
        }

        public int getEnterIndex() {
            return this.enterIndex;
        }
    }

    public IdentifyUriTask(Handler handler, int what, FileMolder molder, String uri) {
        super(handler, what);
        this.molder = molder;
        this.uri = uri;
    }

    protected Result doInBackground() {
        Result result = new Result();
        File file = new StableFile(this.uri);
        if (!file.exists() || !file.isDirectory()) {
            return result;
        }
        int i;
        int deviceIndex = 0;
        File device = null;
        ArrayList<DeviceInfo> devices = this.molder.getDevices();
        String path = file.getAbsolutePath();
        for (i = 0; i < devices.size(); i++) {
            if (path.startsWith(((DeviceInfo) devices.get(i)).getAbsolutePath())) {
                deviceIndex = i;
                device = (DeviceInfo) devices.get(i);
                break;
            }
        }
        if (device == null) {
            return result;
        }
        int listIndex = 0;
        ListType listType = ListType.FILE;
        ArrayList<PathInfo> paths = new ArrayList();
        if (path.length() > device.getPath().length()) {
            String[] names = path.substring(device.getPath().length() + 1).split("/");
            listIndex = names.length;
            File parent;
            File file2;
            String shareName;
            int s;
            String ip;
            String share;
            String url;
            switch (device.getType()) {
                case FLASH:
                case SD:
                case HDD:
                case TF:
                    paths.add(new ProtocolPath(device, 0));
                    parent = device;
                    int length = names.length;
                    int i2 = 0;
                    File parent2 = parent;
                    while (i2 < length) {
                        file2 = new File(parent2, names[i2]);
                        paths.add(new FilePath(file2));
                        i2++;
                        parent2 = file2;
                    }
                    break;
                case SMB:
                    listType = ListType.SMB_FILE;
                    paths.add(new ProtocolPath(device, 1));
                    shareName = names[0];
                    String mountPoint = device.getPath() + "/" + shareName;
                    s = shareName.indexOf("#");
                    if (s != -1) {
                        ip = shareName.substring(0, s);
                        share = ZidooFileUtils.decodeCommand(shareName.substring(s + 1));
                    } else {
                        share = shareName;
                        ip = shareName;
                    }
                    url = "smb://" + ip + "/" + share + path.substring((device.getPath().length() + 1) + shareName.length()) + "/";
                    SambaDevice smb = null;
                    int enterIndex = 0;
                    ArrayList<SambaDevice> smbs = this.molder.queryAndSavedSmbList();
                    int len = 0;
                    for (i = 0; i < smbs.size(); i++) {
                        SambaDevice sambaDevice = (SambaDevice) smbs.get(i);
                        String uuid = sambaDevice.getUrl().replaceFirst(sambaDevice.getHost(), sambaDevice.getIp());
                        if (uuid.length() > len && url.startsWith(uuid)) {
                            smb = sambaDevice;
                            len = uuid.length();
                            enterIndex = i;
                        }
                    }
                    if (smb != null) {
                        result.setEnterIndex(enterIndex);
                        if (smb.getType() == 4) {
                            listIndex = names.length + 1;
                            paths.add(new SharePath(device, null, smb.getName()));
                            paths.add(new FilePath(new MountFile(mountPoint, url, share)));
                            parent = device;
                            i = 1;
                            while (i < names.length) {
                                file2 = new File(parent, names[i]);
                                paths.add(new FilePath(file2));
                                i++;
                                parent = file2;
                            }
                            break;
                        }
                        listType = ListType.SMB_SHARE;
                        paths.add(new SharePath(device, new MountFile(mountPoint, url, share), share));
                        if (url.length() <= len) {
                            listIndex = 1;
                            break;
                        }
                        names = url.substring(len).split("/");
                        listIndex = names.length + 1;
                        parent = file;
                        ArrayList<PathInfo> temp = new ArrayList();
                        i = 0;
                        while (i < names.length) {
                            temp.add(new FilePath(parent));
                            i++;
                            parent = parent.getParentFile();
                        }
                        Collections.reverse(temp);
                        paths.addAll(temp);
                        break;
                    }
                    return new Result();
                case NFS:
                    listIndex = names.length + 1;
                    listType = ListType.NFS_FILE;
                    paths.add(new ProtocolPath(device, 2));
                    shareName = names[0];
                    s = shareName.indexOf("#");
                    if (s != -1) {
                        ip = shareName.substring(0, s);
                        share = ZidooFileUtils.decodeCommand(shareName.substring(s + 1));
                    } else {
                        share = shareName;
                        ip = shareName;
                    }
                    this.molder.saveNfs(ip);
                    url = "/" + ip + "/" + share + path.substring((device.getPath().length() + 1) + shareName.length()) + "/";
                    result.setEnterIndex(0);
                    paths.add(new SharePath(device, null, ip));
                    paths.add(new FilePath(new MountFile(device.getPath() + "/" + names[0], url, share)));
                    parent = device;
                    i = 1;
                    while (i < names.length) {
                        file2 = new File(parent, names[i]);
                        paths.add(new FilePath(file2));
                        i++;
                        parent = file2;
                    }
                    break;
                default:
                    return new Result();
            }
        }
        switch (device.getType()) {
            case FLASH:
            case SD:
            case HDD:
            case TF:
                paths.add(new ProtocolPath(device, 0));
                break;
            default:
                return new Result();
        }
        File[] files = file.listFiles(this.molder.getFileFilter());
        Utils.sortFiles(files, AppConstant.sPrefereancesSortWay);
        result.setResult(listIndex, file, files, listType, deviceIndex, device, paths);
        return result;
    }
}
