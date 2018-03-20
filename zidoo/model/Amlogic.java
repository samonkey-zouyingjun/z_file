package zidoo.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;

public class Amlogic extends BoxModel {
    public Amlogic(Context context) {
        super(context);
    }

    protected Intent openAudio(File file) {
        Intent zidooAudio = this.mContext.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
        if (zidooAudio == null) {
            return super.openAudio(file);
        }
        zidooAudio.setDataAndType(Uri.fromFile(file), "audio/*");
        return zidooAudio;
    }

    public Intent getBDMVOpenWith(File dir) {
        return null;
    }

    public boolean isSupportBDMV() {
        return false;
    }
}
