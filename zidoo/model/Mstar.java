package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.zidoo.custom.usb.FileTypeManager;
import java.io.File;
import zidoo.tool.ZidooFileUtils;

public class Mstar extends BoxModel {
    public Mstar(Context context) {
        super(context);
    }

    protected Intent openAudio(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        Intent zidooAudio;
        if (file.getPath().endsWith("dbs")) {
            zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.jrm.localmm");
            if (zidooAudio != null) {
                intent = zidooAudio;
            }
        } else if (this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer") != null) {
            zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
            if (zidooAudio != null) {
                intent = zidooAudio;
            }
        }
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    protected Intent openImage(File file) {
        Intent intent = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.imageplayer");
        if (intent == null) {
            intent = this.mContext.getPackageManager().getLaunchIntentForPackage("com.jrm.localmm");
            if (intent == null) {
                intent = new Intent("android.intent.action.VIEW");
            }
        }
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        return intent;
    }

    protected Intent openVideo(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        if (this.mContext.getPackageManager().getLaunchIntentForPackage("com.jrm.localmm") != null) {
            intent.setComponent(new ComponentName("com.jrm.localmm", "com.jrm.localmm.ui.video.VideoPlayerActivity"));
        } else if (ZidooFileUtils.isAppSystemInstall(this.mContext, "android.rk.RockVideoPlayer")) {
            intent.setComponent(new ComponentName("android.rk.RockVideoPlayer", "android.rk.RockVideoPlayer.VideoPlayActivity"));
            intent.putExtra("PlayMode", FileTypeManager.open_type_file);
            intent.putStringArrayListExtra("PlayList", ZidooFileUtils.getPalyList(file.getPath()));
        }
        intent.addFlags(268468224);
        intent.setDataAndType(uri, "video/*");
        ZidooFileUtils.sendPauseBroadCast(this.mContext);
        return intent;
    }

    public Intent getBDMVOpenWith(File dir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.jrm.localmm", "com.jrm.localmm.ui.video.VideoPlayerActivity"));
        intent.addFlags(268468224);
        intent.putExtra("bdmv_flag", true);
        intent.putExtra("bdmv_path", dir.getPath() + "//.bdmv");
        intent.putExtra("bdmv_name", dir.getName());
        return intent;
    }

    public boolean isSupportBDMV() {
        return true;
    }
}
