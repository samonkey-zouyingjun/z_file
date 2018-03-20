package zidoo.nfs;

import java.io.File;

public interface NfsMount {
    String getNfsRoot();

    boolean isNfsMounted(String str, String str2, String str3);

    boolean mountNfs(String str, String str2, String str3);

    boolean umountNfs(File file);
}
