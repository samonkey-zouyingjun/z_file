package zidoo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import java.io.File;
import zidoo.tool.ZidooFileUtils;

public class H6 extends RockChip3328 {
    public H6(Context context) {
        super(context);
    }

    protected Intent openVideo(File file) {
        Intent intent = getVideoPlayerIntent(file);
        if (intent != null) {
            return intent;
        }
        intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        return intent;
    }

    public Intent getBDMVOpenWith(File dir) {
        return getVideoPlayerIntent(dir);
    }

    private Intent getVideoPlayerIntent(File file) {
        ComponentName component = new ComponentName("com.softwinner.TvdVideo", "com.softwinner.TvdVideo.TvdVideoActivity");
        try {
            if (this.mContext.getPackageManager().getActivityInfo(component, 0) != null) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addFlags(268468224);
                intent.setDataAndType(Uri.fromFile(file), "video/*");
                intent.setComponent(component);
                ZidooFileUtils.sendPauseBroadCast(this.mContext);
                return intent;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
