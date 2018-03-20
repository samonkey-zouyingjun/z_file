package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings.System;
import java.io.File;
import zidoo.file.RefreshMedia;

public class H3 extends BoxModel {
    int mH3SystemSetEnableOpenBdmvFile = -7344131;

    public H3(Context context) {
        super(context);
    }

    protected Intent openAudio(File file) {
        Intent zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
        if (zidooAudio != null) {
            zidooAudio.setDataAndType(Uri.fromFile(file), "audio/*");
            return zidooAudio;
        }
        RefreshMedia.notifyMediaAdd(this.mContext, new File(file.getPath()).getParent());
        return super.openAudio(file);
    }

    protected Intent openVideo(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(268468224);
        intent.putExtra("android.intent.extra.playListType", "curFolder");
        intent.putExtra("android.intent.extra.finishOnCompletion", false);
        intent.putExtra("android.intent.extra.bdfolderplaymode", false);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    public Intent getBDMVOpenWith(File dir) {
        if (this.mH3SystemSetEnableOpenBdmvFile == -7344131) {
            try {
                this.mH3SystemSetEnableOpenBdmvFile = System.getInt(this.mContext.getContentResolver(), (String) System.class.getField("BD_FOLDER_PLAY_MODE").get(System.class), 0);
            } catch (NoSuchFieldException e) {
                this.mH3SystemSetEnableOpenBdmvFile = 0;
                e.printStackTrace();
            } catch (IllegalArgumentException e2) {
                this.mH3SystemSetEnableOpenBdmvFile = 0;
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                this.mH3SystemSetEnableOpenBdmvFile = 0;
                e3.printStackTrace();
            }
        }
        if (this.mH3SystemSetEnableOpenBdmvFile == 0) {
            return null;
        }
        Intent bdIntent = new Intent();
        bdIntent.putExtra("android.intent.extra.bdfolderplaymode", true);
        bdIntent.setComponent(new ComponentName("com.softwinner.TvdVideo", "com.softwinner.TvdVideo.TvdVideoActivity"));
        bdIntent.addFlags(268468224);
        bdIntent.setDataAndType(Uri.fromFile(dir), "video/*");
        return bdIntent;
    }

    public boolean isSupportBDMV() {
        return true;
    }
}
