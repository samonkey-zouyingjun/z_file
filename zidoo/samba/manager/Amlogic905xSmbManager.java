package zidoo.samba.manager;

import android.content.Context;

public class Amlogic905xSmbManager extends AmlogicSmbManager {
    Amlogic905xSmbManager(Context context) {
        super(context);
    }

    public String getSmbRoot() {
        return "/mnt/NetShareDirs";
    }
}
