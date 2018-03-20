package zidoo.nfs;

import android.content.Context;

public class Amlogic905xNfsManager extends AmlogicNfsManager {
    Amlogic905xNfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return "/mnt/nfs";
    }
}
