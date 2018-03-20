package zidoo.samba.manager;

import android.content.Context;

public class Rock3229SmbManager extends RockSmbManager {
    Rock3229SmbManager(Context context) {
        super(context);
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("\\134\\134" + param[0] + "\\134" + param[1].replaceAll(" ", "\\\\040"), param[2]);
    }
}
