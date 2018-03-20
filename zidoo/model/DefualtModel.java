package zidoo.model;

import android.content.Context;
import android.content.Intent;
import java.io.File;

public class DefualtModel extends BoxModel {
    public DefualtModel(Context context) {
        super(context);
    }

    public Intent getBDMVOpenWith(File dir) {
        return null;
    }

    public boolean isSupportBDMV() {
        return false;
    }
}
