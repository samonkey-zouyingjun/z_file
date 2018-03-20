package com.zidoo.fileexplorer.task;

import android.content.Context;
import android.os.Handler;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.FavoriteParam;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import com.zidoo.fileexplorer.tool.MyLog;
import java.io.File;
import zidoo.file.FileType;
import zidoo.samba.exs.SambaDevice;
import zidoo.tool.ZidooFileUtils;

public class FavoriteTask extends BaseTask<Result> {
    Context context;
    int listIndex;
    ListInfo listInfo;
    FavoriteParam param;

    public static final class Result {
        int code;
        Favorite favorite;

        public Result(int code, Favorite favorite) {
            this.code = code;
            this.favorite = favorite;
        }

        public int getCode() {
            return this.code;
        }

        public Favorite getFavorite() {
            return this.favorite;
        }
    }

    public FavoriteTask(Handler handler, int what, Context context, FavoriteParam param, ListInfo listInfo, int listIndex) {
        super(handler, what);
        this.context = context;
        this.param = param;
        this.listInfo = listInfo;
        this.listIndex = listIndex;
    }

    protected Result doInBackground() {
        int code;
        DeviceInfo device = this.param.getDeviceInfo();
        Favorite favorite = null;
        File file;
        int fy;
        SambaDevice smb;
        Favorite favorite2;
        String temp;
        int s;
        String mountName;
        String tails;
        String url;
        switch (this.param.getListType()) {
            case FILE:
                file = this.param.getFile();
                String abstractPath = file.getPath().substring(device.getPath().length());
                fy = FileType.getType(file);
                favorite = new Favorite(abstractPath, this.param.getTag(), this.param.getListIndex(), file.getName(), fy, fy > 0 ? file.length() : 0);
                if (device.getBlock() == null) {
                    favorite.setUuid(device.getPath() + "/");
                    break;
                }
                favorite.setUuid(device.getBlock().getUuid());
                break;
            case SMB_DEVICE:
                smb = this.param.getSmb();
                favorite2 = new Favorite(smb.getName(), smb.getUrl(), this.param.getTag(), this.param.getListIndex(), smb.getIp(), smb.getUser(), smb.getPassWord());
                break;
            case SMB_FILE:
            case SMB_SHARE:
                file = this.param.getFile();
                temp = file.getPath().substring(device.getPath().length() + 1);
                s = temp.indexOf(47);
                if (s == -1) {
                    mountName = temp;
                    tails = "";
                } else {
                    mountName = temp.substring(0, s);
                    tails = temp.substring(s);
                }
                String share = ZidooFileUtils.decodeCommand(mountName.substring(mountName.indexOf(35) + 1));
                smb = this.param.getSmb();
                SambaDevice smbs = this.listInfo.getSavedSmbShare(smb.getIp(), share);
                if (smbs != null) {
                    smb = smbs;
                }
                url = "smb://" + smb.getHost() + "/" + share + tails;
                fy = FileType.getType(file);
                Favorite favorite3 = new Favorite(url, this.param.getTag(), this.param.getListIndex(), file.getName(), smb.getIp(), smb.getUser(), smb.getPassWord(), fy, fy > 0 ? file.length() : 0);
                break;
            case NFS_DEVICE:
                favorite = new Favorite(this.param.getNfs().ip, this.param.getTag());
                break;
            case NFS_FILE:
                file = this.param.getFile();
                temp = file.getPath().substring(device.getPath().length() + 1);
                s = temp.indexOf(47);
                if (s == -1) {
                    mountName = temp;
                    tails = "";
                } else {
                    mountName = temp.substring(0, s);
                    tails = temp.substring(s);
                }
                url = this.param.getNfs().ip + "/" + ZidooFileUtils.decodeCommand(mountName.substring(mountName.indexOf(35) + 1)) + tails;
                fy = FileType.getType(file);
                favorite2 = new Favorite(file.getName(), url, this.param.getTag(), this.param.getListIndex(), fy, fy > 0 ? file.length() : 0);
                break;
            case FAVORITE:
                MyLog.e("Repeated Favor", new IllegalArgumentException("Favorite cannot be favor again!"));
                break;
        }
        MyLog.d(favorite.toString());
        if (FavoriteDatabase.helper(this.context).exist(favorite)) {
            code = -2;
        } else {
            favorite.setId((int) FavoriteDatabase.helper(this.context).insert(favorite));
            code = 0;
        }
        return new Result(code, favorite);
    }
}
