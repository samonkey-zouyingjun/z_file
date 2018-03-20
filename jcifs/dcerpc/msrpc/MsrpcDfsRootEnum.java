package jcifs.dcerpc.msrpc;

import android.support.v4.internal.view.SupportMenu;
import jcifs.dcerpc.msrpc.netdfs.DfsEnumArray200;
import jcifs.dcerpc.msrpc.netdfs.DfsEnumStruct;
import jcifs.dcerpc.msrpc.netdfs.NetrDfsEnumEx;
import jcifs.dcerpc.ndr.NdrLong;
import jcifs.smb.FileEntry;
import jcifs.smb.SmbShareInfo;

public class MsrpcDfsRootEnum extends NetrDfsEnumEx {
    public MsrpcDfsRootEnum(String server) {
        super(server, 200, SupportMenu.USER_MASK, new DfsEnumStruct(), new NdrLong(0));
        this.info.level = this.level;
        this.info.e = new DfsEnumArray200();
        this.ptype = 0;
        this.flags = 3;
    }

    public FileEntry[] getEntries() {
        DfsEnumArray200 a200 = this.info.e;
        SmbShareInfo[] entries = new SmbShareInfo[a200.count];
        for (int i = 0; i < a200.count; i++) {
            entries[i] = new SmbShareInfo(a200.s[i].dfs_name, 0, null);
        }
        return entries;
    }
}
