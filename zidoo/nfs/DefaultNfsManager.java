package zidoo.nfs;

import android.content.Context;
import java.io.File;

public class DefaultNfsManager extends NfsManager {
    DefaultNfsManager(Context context) {
        super(context);
    }

    public String getNfsRoot() {
        return null;
    }

    public boolean mountNfs(String ip, String sharePath, String mountPoint) {
        return false;
    }

    public boolean umountNfs(File file) {
        return false;
    }
}
