package zidoo.samba.manager;

import android.content.Context;
import com.amlogic.netfilebrowser.smbmnt.SmbClientMnt;
import java.io.File;

public class AmlogicSmbManager extends SambaManager {
    SmbClientMnt mSmbClientMnt = new SmbClientMnt();

    public String getSmbRoot() {
        return "/data/NetShareDirs";
    }

    AmlogicSmbManager(Context context) {
        super(context);
    }

    public boolean mountSmb(String sharePath, String mountPoint, String ip, String user, String pwd) {
        return this.mSmbClientMnt.SmbMount(new StringBuilder().append("mount ").append(sharePath).append(" ").append(mountPoint).append(" username=").append(user).append(",password=").append(pwd).toString()) == 0;
    }

    public boolean unMountSmb(File file) {
        return (this.mSmbClientMnt.SmbUnMount(new StringBuilder().append("umount ").append(file.getPath()).toString()) == 0 ? 1 : 0) & file.delete();
    }

    public boolean isMounted(String... param) {
        return SambaManager.isMounted("\\134\\134" + param[0] + "\\134" + param[1], param[2]);
    }
}
