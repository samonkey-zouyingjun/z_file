package com.zidoo.fileexplorer.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.fileexplorer.view.DetailsLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import zidoo.device.DeviceType;
import zidoo.file.FileType;
import zidoo.nfs.NfsDevice;
import zidoo.samba.exs.SambaDevice;
import zidoo.tool.ZidooFileUtils;

@SuppressLint({"SimpleDateFormat"})
public class DetailTask extends BaseTask<Result> {
    Param param;

    public static final class Param {
        Context context;
        DetailsLayout detailsLayout;
        DeviceInfo deviceInfo;
        Favorite favorite;
        File file;
        ListType listType;
        NfsDevice nfs;
        SambaDevice sambaDevice;
        boolean share;

        public Param(Context context, DetailsLayout detailsLayout, DeviceInfo deviceInfo, ListType listType, File file) {
            this.context = context;
            this.detailsLayout = detailsLayout;
            this.deviceInfo = deviceInfo;
            this.listType = listType;
            this.file = file;
        }

        public Param(Context context, DetailsLayout detailsLayout, ListType listType, SambaDevice smbDevice) {
            this.context = context;
            this.detailsLayout = detailsLayout;
            this.listType = listType;
            this.sambaDevice = smbDevice;
        }

        public Param(Context context, DetailsLayout detailsLayout, ListType listType, Favorite favorite) {
            this.context = context;
            this.detailsLayout = detailsLayout;
            this.listType = listType;
            this.favorite = favorite;
        }

        public Param(Context context, DetailsLayout detailsLayout, ListType listType, NfsDevice nfs, DeviceInfo deviceInfo, File file, boolean isShare) {
            this.context = context;
            this.detailsLayout = detailsLayout;
            this.listType = listType;
            this.nfs = nfs;
            this.deviceInfo = deviceInfo;
            this.file = file;
            this.share = isShare;
        }

        public Param(Context context, DetailsLayout detailsLayout, ListType listType, SambaDevice smbDevice, DeviceInfo deviceInfo, File file, boolean isShare) {
            this.context = context;
            this.detailsLayout = detailsLayout;
            this.listType = listType;
            this.sambaDevice = smbDevice;
            this.deviceInfo = deviceInfo;
            this.file = file;
            this.share = isShare;
        }

        public boolean isShare() {
            return this.share;
        }

        public NfsDevice getNfs() {
            return this.nfs;
        }

        public SambaDevice getSmb() {
            return this.sambaDevice;
        }

        public Favorite getFavorite() {
            return this.favorite;
        }

        public Context getContext() {
            return this.context;
        }

        public File getFile() {
            return this.file;
        }

        public DeviceInfo getDeviceInfo() {
            return this.deviceInfo;
        }

        public ListType getListType() {
            return this.listType;
        }

        public DetailsLayout getDetailsLayout() {
            return this.detailsLayout;
        }
    }

    public final class Result {
        DetailsLayout detailsLayout;
        int flag;
        boolean isFavor;
        String s1;
        String s2;
        String s3;
        String s4;

        public Result(DetailsLayout detailsLayout, boolean isFavor, String s1, String s2, String s3, String s4, int flag) {
            this.detailsLayout = detailsLayout;
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.s4 = s4;
            this.flag = flag;
            this.isFavor = isFavor;
        }

        public void send() {
            this.detailsLayout.setDetails(this.isFavor, this.s1, this.s2, this.s3, this.s4, this.flag);
        }
    }

    public DetailTask(Handler handler, int what, Param param) {
        super(handler, what);
        this.param = param;
    }

    protected Result doInBackground() {
        DeviceInfo device;
        File file;
        String uri;
        int gp;
        String mountName;
        String other;
        int jp;
        String share;
        SambaDevice smb;
        switch (this.param.getListType()) {
            case FAVORITE:
                return getFavoriteResult(this.param);
            case FILE:
                String uuid;
                device = this.param.getDeviceInfo();
                file = this.param.getFile();
                String abstractPath = file.getPath();
                try {
                    abstractPath = file.getPath().substring(device.getPath().length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (device.getBlock() != null) {
                    uuid = device.getBlock().getUuid();
                } else {
                    uuid = device.getPath() + "/";
                }
                return getFileResult(FavoriteDatabase.helper(this.param.getContext()).exist(abstractPath, uuid, device.getType() == DeviceType.FLASH ? 0 : 1), this.param);
            case SMB_FILE:
                uri = this.param.getFile().getPath().substring(this.param.getDeviceInfo().getPath().length() + 1);
                gp = uri.indexOf(47);
                if (gp == -1) {
                    mountName = uri;
                    other = "";
                } else {
                    mountName = uri.substring(0, gp);
                    other = uri.substring(gp);
                }
                jp = mountName.indexOf(35);
                if (jp != -1) {
                    mountName = mountName.substring(jp + 1);
                }
                share = ZidooFileUtils.decodeCommand(mountName);
                smb = this.param.getSmb();
                return getFileResult(FavoriteDatabase.helper(this.param.getContext()).exist("smb://" + smb.getHost() + "/" + share + other, smb.getIp(), 3), this.param);
            case SMB_SHARE:
                uri = this.param.getFile().getPath().substring(this.param.getDeviceInfo().getPath().length() + 1);
                gp = uri.indexOf(47);
                if (gp == -1) {
                    mountName = uri;
                    other = "";
                } else {
                    mountName = uri.substring(0, gp);
                    other = uri.substring(gp);
                }
                jp = mountName.indexOf(35);
                if (jp != -1) {
                    mountName = mountName.substring(jp + 1);
                }
                share = ZidooFileUtils.decodeCommand(mountName);
                smb = this.param.getSmb();
                return getFileResult(FavoriteDatabase.helper(this.param.getContext()).exist("smb://" + smb.getHost() + "/" + share + other, smb.getIp(), 7), this.param);
            case NFS_FILE:
                String url;
                file = this.param.getFile();
                device = this.param.getDeviceInfo();
                NfsDevice nfs = this.param.getNfs();
                if (this.param.isShare()) {
                    url = nfs.ip + "/" + file.getName();
                } else {
                    uri = file.getPath().substring(device.getPath().length() + 1);
                    gp = uri.indexOf(47);
                    if (gp == -1) {
                        mountName = uri;
                        other = "";
                    } else {
                        mountName = uri.substring(0, gp);
                        other = uri.substring(gp);
                    }
                    jp = mountName.indexOf(35);
                    if (jp != -1) {
                        mountName = mountName.substring(jp + 1);
                    }
                    url = nfs.ip + "/" + ZidooFileUtils.decodeCommand(mountName) + other;
                }
                return getFileResult(FavoriteDatabase.helper(this.param.getContext()).exist(url, "", 5), this.param);
            case SMB_DEVICE:
                return getSmbDeviceResult(this.param);
            default:
                return null;
        }
    }

    private Result getFavoriteResult(Param param) {
        String type;
        String size;
        Context context = param.getContext();
        Favorite favorite = param.getFavorite();
        String deviceType = Utils.getFastIdentifierType(context, favorite.getTag());
        String uri = favorite.getUri();
        int fy = favorite.getFileType();
        if (fy < 0) {
            type = context.getString(R.string.type_net);
            size = "";
        } else if (fy == 0) {
            type = context.getResources().getStringArray(R.array.file_type)[0];
            size = "";
        } else {
            type = context.getResources().getStringArray(R.array.file_type)[fy];
            size = Utils.formatFileSize(favorite.getFileLength());
        }
        return new Result(param.getDetailsLayout(), false, type, uri, deviceType, size, 4);
    }

    private Result getFileResult(boolean isFavor, Param param) {
        String s1;
        String s2;
        String s3;
        int flag;
        Context context = param.getContext();
        File file = param.getFile();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String s4 = null;
        if (param.isShare() && !file.exists()) {
            s1 = context.getResources().getStringArray(R.array.file_type)[0];
            s2 = file.getName();
            s3 = context.getString(R.string.file_date, new Object[]{dateFormat.format(new Date())});
            flag = 2;
        } else if (file.isDirectory()) {
            s1 = context.getResources().getStringArray(R.array.file_type)[0];
            s2 = file.getName();
            s3 = context.getString(R.string.file_date, new Object[]{dateFormat.format(Long.valueOf(file.lastModified()))});
            flag = 2;
        } else {
            int type = FileType.getFileType(file.getName());
            String[] stringArray = context.getResources().getStringArray(R.array.file_type);
            if (type > 12) {
                type = 12;
            }
            s1 = stringArray[type];
            s2 = file.getName();
            s3 = context.getString(R.string.file_date, new Object[]{dateFormat.format(Long.valueOf(file.lastModified()))});
            s4 = context.getString(R.string.file_size, new Object[]{Utils.formatFileSize(file.length())});
            flag = 1;
        }
        return new Result(param.getDetailsLayout(), isFavor, s1, s2, s3, s4, flag);
    }

    private Result getSmbDeviceResult(Param param) {
        Context context = param.getContext();
        SambaDevice smb = param.getSmb();
        return new Result(param.getDetailsLayout(), FavoriteDatabase.helper(context).exist(smb.getUrl(), smb.getIp(), smb.getType() == 4 ? 2 : 6), context.getString(R.string.host, new Object[]{smb.getHost()}), smb.getUrl(), context.getString(R.string.address, new Object[]{smb.getIp()}), null, 3);
    }
}
