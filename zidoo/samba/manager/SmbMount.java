package zidoo.samba.manager;

import java.io.File;

public interface SmbMount {
    String getSmbRoot();

    boolean mountSmb(String str, String str2, String str3, String str4, String str5);

    boolean unMountSmb(File file);
}
