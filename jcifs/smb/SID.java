package jcifs.smb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import jcifs.dcerpc.DcerpcHandle;
import jcifs.dcerpc.UnicodeString;
import jcifs.dcerpc.msrpc.LsaPolicyHandle;
import jcifs.dcerpc.msrpc.MsrpcGetMembersInAlias;
import jcifs.dcerpc.msrpc.MsrpcLookupSids;
import jcifs.dcerpc.msrpc.SamrAliasHandle;
import jcifs.dcerpc.msrpc.SamrDomainHandle;
import jcifs.dcerpc.msrpc.lsarpc.LsarSidArray;
import jcifs.dcerpc.rpc.sid_t;
import jcifs.dcerpc.rpc.unicode_string;
import jcifs.util.Encdec;
import jcifs.util.Hexdump;

public class SID extends sid_t {
    public static SID CREATOR_OWNER = null;
    public static SID EVERYONE = null;
    public static final int SID_FLAG_RESOLVE_SIDS = 1;
    public static final int SID_TYPE_ALIAS = 4;
    public static final int SID_TYPE_DELETED = 6;
    public static final int SID_TYPE_DOMAIN = 3;
    public static final int SID_TYPE_DOM_GRP = 2;
    public static final int SID_TYPE_INVALID = 7;
    static final String[] SID_TYPE_NAMES = new String[]{"0", "User", "Domain group", "Domain", "Local group", "Builtin group", "Deleted", "Invalid", "Unknown"};
    public static final int SID_TYPE_UNKNOWN = 8;
    public static final int SID_TYPE_USER = 1;
    public static final int SID_TYPE_USE_NONE = 0;
    public static final int SID_TYPE_WKN_GRP = 5;
    public static SID SYSTEM;
    static Map sid_cache = new HashMap();
    String acctName = null;
    String domainName = null;
    NtlmPasswordAuthentication origin_auth = null;
    String origin_server = null;
    int type;

    static {
        EVERYONE = null;
        CREATOR_OWNER = null;
        SYSTEM = null;
        try {
            EVERYONE = new SID("S-1-1-0");
            CREATOR_OWNER = new SID("S-1-3-0");
            SYSTEM = new SID("S-1-5-18");
        } catch (SmbException e) {
        }
    }

    static void resolveSids(DcerpcHandle handle, LsaPolicyHandle policyHandle, SID[] sids) throws IOException {
        MsrpcLookupSids rpc = new MsrpcLookupSids(policyHandle, sids);
        handle.sendrecv(rpc);
        switch (rpc.retval) {
            case NtStatus.NT_STATUS_NONE_MAPPED /*-1073741709*/:
            case 0:
            case 263:
                for (int si = 0; si < sids.length; si++) {
                    sids[si].type = rpc.names.names[si].sid_type;
                    sids[si].domainName = null;
                    switch (sids[si].type) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            unicode_string ustr = rpc.domains.domains[rpc.names.names[si].sid_index].name;
                            sids[si].domainName = new UnicodeString(ustr, false).toString();
                            break;
                        default:
                            break;
                    }
                    sids[si].acctName = new UnicodeString(rpc.names.names[si].name, false).toString();
                    sids[si].origin_server = null;
                    sids[si].origin_auth = null;
                }
                return;
            default:
                throw new SmbException(rpc.retval, false);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void resolveSids0(java.lang.String r8, jcifs.smb.NtlmPasswordAuthentication r9, jcifs.smb.SID[] r10) throws java.io.IOException {
        /*
        r1 = 0;
        r2 = 0;
        r6 = sid_cache;
        monitor-enter(r6);
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0067 }
        r5.<init>();	 Catch:{ all -> 0x0067 }
        r7 = "ncacn_np:";
        r5 = r5.append(r7);	 Catch:{ all -> 0x0067 }
        r5 = r5.append(r8);	 Catch:{ all -> 0x0067 }
        r7 = "[\\PIPE\\lsarpc]";
        r5 = r5.append(r7);	 Catch:{ all -> 0x0067 }
        r5 = r5.toString();	 Catch:{ all -> 0x0067 }
        r1 = jcifs.dcerpc.DcerpcHandle.getHandle(r5, r9);	 Catch:{ all -> 0x0067 }
        r4 = r8;
        r5 = 46;
        r0 = r4.indexOf(r5);	 Catch:{ all -> 0x0067 }
        if (r0 <= 0) goto L_0x003d;
    L_0x002d:
        r5 = 0;
        r5 = r4.charAt(r5);	 Catch:{ all -> 0x0067 }
        r5 = java.lang.Character.isDigit(r5);	 Catch:{ all -> 0x0067 }
        if (r5 != 0) goto L_0x003d;
    L_0x0038:
        r5 = 0;
        r4 = r4.substring(r5, r0);	 Catch:{ all -> 0x0067 }
    L_0x003d:
        r3 = new jcifs.dcerpc.msrpc.LsaPolicyHandle;	 Catch:{ all -> 0x0067 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0067 }
        r5.<init>();	 Catch:{ all -> 0x0067 }
        r7 = "\\\\";
        r5 = r5.append(r7);	 Catch:{ all -> 0x0067 }
        r5 = r5.append(r4);	 Catch:{ all -> 0x0067 }
        r5 = r5.toString();	 Catch:{ all -> 0x0067 }
        r7 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r3.<init>(r1, r5, r7);	 Catch:{ all -> 0x0067 }
        resolveSids(r1, r3, r10);	 Catch:{ all -> 0x0079 }
        if (r1 == 0) goto L_0x0065;
    L_0x005d:
        if (r3 == 0) goto L_0x0062;
    L_0x005f:
        r3.close();	 Catch:{ all -> 0x0076 }
    L_0x0062:
        r1.close();	 Catch:{ all -> 0x0076 }
    L_0x0065:
        monitor-exit(r6);	 Catch:{ all -> 0x0076 }
        return;
    L_0x0067:
        r5 = move-exception;
    L_0x0068:
        if (r1 == 0) goto L_0x0072;
    L_0x006a:
        if (r2 == 0) goto L_0x006f;
    L_0x006c:
        r2.close();	 Catch:{ all -> 0x0073 }
    L_0x006f:
        r1.close();	 Catch:{ all -> 0x0073 }
    L_0x0072:
        throw r5;	 Catch:{ all -> 0x0073 }
    L_0x0073:
        r5 = move-exception;
    L_0x0074:
        monitor-exit(r6);	 Catch:{ all -> 0x0073 }
        throw r5;
    L_0x0076:
        r5 = move-exception;
        r2 = r3;
        goto L_0x0074;
    L_0x0079:
        r5 = move-exception;
        r2 = r3;
        goto L_0x0068;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SID.resolveSids0(java.lang.String, jcifs.smb.NtlmPasswordAuthentication, jcifs.smb.SID[]):void");
    }

    public static void resolveSids(String authorityServerName, NtlmPasswordAuthentication auth, SID[] sids, int offset, int length) throws IOException {
        ArrayList list = new ArrayList(sids.length);
        synchronized (sid_cache) {
            int si;
            for (si = 0; si < length; si++) {
                SID sid = (SID) sid_cache.get(sids[offset + si]);
                if (sid != null) {
                    sids[offset + si].type = sid.type;
                    sids[offset + si].domainName = sid.domainName;
                    sids[offset + si].acctName = sid.acctName;
                } else {
                    list.add(sids[offset + si]);
                }
            }
            if (list.size() > 0) {
                sids = (SID[]) list.toArray(new SID[0]);
                resolveSids0(authorityServerName, auth, sids);
                for (si = 0; si < sids.length; si++) {
                    sid_cache.put(sids[si], sids[si]);
                }
            }
        }
    }

    public static void resolveSids(String authorityServerName, NtlmPasswordAuthentication auth, SID[] sids) throws IOException {
        ArrayList list = new ArrayList(sids.length);
        synchronized (sid_cache) {
            int si;
            for (si = 0; si < sids.length; si++) {
                SID sid = (SID) sid_cache.get(sids[si]);
                if (sid != null) {
                    sids[si].type = sid.type;
                    sids[si].domainName = sid.domainName;
                    sids[si].acctName = sid.acctName;
                } else {
                    list.add(sids[si]);
                }
            }
            if (list.size() > 0) {
                sids = (SID[]) list.toArray(new SID[0]);
                resolveSids0(authorityServerName, auth, sids);
                for (si = 0; si < sids.length; si++) {
                    sid_cache.put(sids[si], sids[si]);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static jcifs.smb.SID getServerSid(java.lang.String r12, jcifs.smb.NtlmPasswordAuthentication r13) throws java.io.IOException {
        /*
        r6 = 0;
        r8 = 0;
        r7 = new jcifs.dcerpc.msrpc.lsarpc$LsarDomainInfo;
        r7.<init>();
        r11 = sid_cache;
        monitor-enter(r11);
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x007b }
        r0.<init>();	 Catch:{ all -> 0x007b }
        r1 = "ncacn_np:";
        r0 = r0.append(r1);	 Catch:{ all -> 0x007b }
        r0 = r0.append(r12);	 Catch:{ all -> 0x007b }
        r1 = "[\\PIPE\\lsarpc]";
        r0 = r0.append(r1);	 Catch:{ all -> 0x007b }
        r0 = r0.toString();	 Catch:{ all -> 0x007b }
        r6 = jcifs.dcerpc.DcerpcHandle.getHandle(r0, r13);	 Catch:{ all -> 0x007b }
        r9 = new jcifs.dcerpc.msrpc.LsaPolicyHandle;	 Catch:{ all -> 0x007b }
        r0 = 0;
        r1 = 1;
        r9.<init>(r6, r0, r1);	 Catch:{ all -> 0x007b }
        r10 = new jcifs.dcerpc.msrpc.MsrpcQueryInformationPolicy;	 Catch:{ all -> 0x0046 }
        r0 = 5;
        r10.<init>(r9, r0, r7);	 Catch:{ all -> 0x0046 }
        r6.sendrecv(r10);	 Catch:{ all -> 0x0046 }
        r0 = r10.retval;	 Catch:{ all -> 0x0046 }
        if (r0 == 0) goto L_0x0056;
    L_0x003d:
        r0 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x0046 }
        r1 = r10.retval;	 Catch:{ all -> 0x0046 }
        r2 = 0;
        r0.<init>(r1, r2);	 Catch:{ all -> 0x0046 }
        throw r0;	 Catch:{ all -> 0x0046 }
    L_0x0046:
        r0 = move-exception;
        r8 = r9;
    L_0x0048:
        if (r6 == 0) goto L_0x0052;
    L_0x004a:
        if (r8 == 0) goto L_0x004f;
    L_0x004c:
        r8.close();	 Catch:{ all -> 0x0053 }
    L_0x004f:
        r6.close();	 Catch:{ all -> 0x0053 }
    L_0x0052:
        throw r0;	 Catch:{ all -> 0x0053 }
    L_0x0053:
        r0 = move-exception;
    L_0x0054:
        monitor-exit(r11);	 Catch:{ all -> 0x0053 }
        throw r0;
    L_0x0056:
        r0 = new jcifs.smb.SID;	 Catch:{ all -> 0x0046 }
        r1 = r7.sid;	 Catch:{ all -> 0x0046 }
        r2 = 3;
        r3 = new jcifs.dcerpc.UnicodeString;	 Catch:{ all -> 0x0046 }
        r4 = r7.name;	 Catch:{ all -> 0x0046 }
        r5 = 0;
        r3.<init>(r4, r5);	 Catch:{ all -> 0x0046 }
        r3 = r3.toString();	 Catch:{ all -> 0x0046 }
        r4 = 0;
        r5 = 0;
        r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x0046 }
        if (r6 == 0) goto L_0x0076;
    L_0x006e:
        if (r9 == 0) goto L_0x0073;
    L_0x0070:
        r9.close();	 Catch:{ all -> 0x0078 }
    L_0x0073:
        r6.close();	 Catch:{ all -> 0x0078 }
    L_0x0076:
        monitor-exit(r11);	 Catch:{ all -> 0x0078 }
        return r0;
    L_0x0078:
        r0 = move-exception;
        r8 = r9;
        goto L_0x0054;
    L_0x007b:
        r0 = move-exception;
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SID.getServerSid(java.lang.String, jcifs.smb.NtlmPasswordAuthentication):jcifs.smb.SID");
    }

    public static byte[] toByteArray(sid_t sid) {
        byte[] dst = new byte[((sid.sub_authority_count * 4) + 8)];
        int i = 0 + 1;
        dst[0] = sid.revision;
        int di = i + 1;
        dst[i] = sid.sub_authority_count;
        System.arraycopy(sid.identifier_authority, 0, dst, di, 6);
        di += 6;
        for (byte ii = (byte) 0; ii < sid.sub_authority_count; ii++) {
            Encdec.enc_uint32le(sid.sub_authority[ii], dst, di);
            di += 4;
        }
        return dst;
    }

    public SID(byte[] src, int si) {
        int si2 = si + 1;
        this.revision = src[si];
        si = si2 + 1;
        this.sub_authority_count = src[si2];
        this.identifier_authority = new byte[6];
        System.arraycopy(src, si, this.identifier_authority, 0, 6);
        si += 6;
        if (this.sub_authority_count > (byte) 100) {
            throw new RuntimeException("Invalid SID sub_authority_count");
        }
        this.sub_authority = new int[this.sub_authority_count];
        for (byte i = (byte) 0; i < this.sub_authority_count; i++) {
            this.sub_authority[i] = ServerMessageBlock.readInt4(src, si);
            si += 4;
        }
    }

    public SID(String textual) throws SmbException {
        StringTokenizer st = new StringTokenizer(textual, "-");
        if (st.countTokens() < 3 || !st.nextToken().equals("S")) {
            throw new SmbException("Bad textual SID format: " + textual);
        }
        long id;
        this.revision = Byte.parseByte(st.nextToken());
        String tmp = st.nextToken();
        if (tmp.startsWith("0x")) {
            id = Long.parseLong(tmp.substring(2), 16);
        } else {
            id = Long.parseLong(tmp);
        }
        this.identifier_authority = new byte[6];
        int i = 5;
        while (id > 0) {
            this.identifier_authority[i] = (byte) ((int) (id % 256));
            id >>= 8;
            i--;
        }
        this.sub_authority_count = (byte) st.countTokens();
        if (this.sub_authority_count > (byte) 0) {
            this.sub_authority = new int[this.sub_authority_count];
            for (byte i2 = (byte) 0; i2 < this.sub_authority_count; i2++) {
                this.sub_authority[i2] = (int) (Long.parseLong(st.nextToken()) & 4294967295L);
            }
        }
    }

    public SID(SID domsid, int rid) {
        this.revision = domsid.revision;
        this.identifier_authority = domsid.identifier_authority;
        this.sub_authority_count = (byte) (domsid.sub_authority_count + 1);
        this.sub_authority = new int[this.sub_authority_count];
        byte i = (byte) 0;
        while (i < domsid.sub_authority_count) {
            this.sub_authority[i] = domsid.sub_authority[i];
            i++;
        }
        this.sub_authority[i] = rid;
    }

    public SID(sid_t sid, int type, String domainName, String acctName, boolean decrementAuthority) {
        this.revision = sid.revision;
        this.sub_authority_count = sid.sub_authority_count;
        this.identifier_authority = sid.identifier_authority;
        this.sub_authority = sid.sub_authority;
        this.type = type;
        this.domainName = domainName;
        this.acctName = acctName;
        if (decrementAuthority) {
            this.sub_authority_count = (byte) (this.sub_authority_count - 1);
            this.sub_authority = new int[this.sub_authority_count];
            for (byte i = (byte) 0; i < this.sub_authority_count; i++) {
                this.sub_authority[i] = sid.sub_authority[i];
            }
        }
    }

    public SID getDomainSid() {
        return new SID(this, 3, this.domainName, null, getType() != 3);
    }

    public int getRid() {
        if (getType() != 3) {
            return this.sub_authority[this.sub_authority_count - 1];
        }
        throw new IllegalArgumentException("This SID is a domain sid");
    }

    public int getType() {
        if (this.origin_server != null) {
            resolveWeak();
        }
        return this.type;
    }

    public String getTypeText() {
        if (this.origin_server != null) {
            resolveWeak();
        }
        return SID_TYPE_NAMES[this.type];
    }

    public String getDomainName() {
        if (this.origin_server != null) {
            resolveWeak();
        }
        if (this.type != 8) {
            return this.domainName;
        }
        String full = toString();
        return full.substring(0, (full.length() - getAccountName().length()) - 1);
    }

    public String getAccountName() {
        if (this.origin_server != null) {
            resolveWeak();
        }
        if (this.type == 8) {
            return "" + this.sub_authority[this.sub_authority_count - 1];
        }
        if (this.type == 3) {
            return "";
        }
        return this.acctName;
    }

    public int hashCode() {
        int hcode = this.identifier_authority[5];
        for (byte i = (byte) 0; i < this.sub_authority_count; i++) {
            hcode += 65599 * this.sub_authority[i];
        }
        return hcode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (!(obj instanceof SID)) {
            return false;
        }
        SID sid = (SID) obj;
        if (sid == this) {
            return true;
        }
        if (sid.sub_authority_count != this.sub_authority_count) {
            return false;
        }
        int i = this.sub_authority_count;
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            } else if (sid.sub_authority[i2] != this.sub_authority[i2]) {
                return false;
            } else {
                i = i2;
            }
        }
        for (i2 = 0; i2 < 6; i2++) {
            if (sid.identifier_authority[i2] != this.identifier_authority[i2]) {
                return false;
            }
        }
        if (sid.revision != this.revision) {
            z = false;
        }
        return z;
    }

    public String toString() {
        String ret = "S-" + (this.revision & 255) + "-";
        if (this.identifier_authority[0] == (byte) 0 && this.identifier_authority[1] == (byte) 0) {
            long shift = 0;
            long id = 0;
            for (int i = 5; i > 1; i--) {
                id += (((long) this.identifier_authority[i]) & 255) << ((int) shift);
                shift += 8;
            }
            ret = ret + id;
        } else {
            ret = (ret + "0x") + Hexdump.toHexString(this.identifier_authority, 0, 6);
        }
        for (byte i2 = (byte) 0; i2 < this.sub_authority_count; i2++) {
            ret = ret + "-" + (((long) this.sub_authority[i2]) & 4294967295L);
        }
        return ret;
    }

    public String toDisplayString() {
        if (this.origin_server != null) {
            resolveWeak();
        }
        if (this.domainName == null) {
            return toString();
        }
        if (this.type == 3) {
            return this.domainName;
        }
        if (this.type != 5 && !this.domainName.equals("BUILTIN")) {
            return this.domainName + "\\" + this.acctName;
        }
        if (this.type == 8) {
            return toString();
        }
        return this.acctName;
    }

    public void resolve(String authorityServerName, NtlmPasswordAuthentication auth) throws IOException {
        resolveSids(authorityServerName, auth, new SID[]{this});
    }

    void resolveWeak() {
        if (this.origin_server != null) {
            try {
                resolve(this.origin_server, this.origin_auth);
                this.origin_server = null;
            } catch (IOException e) {
                this.origin_server = null;
            } catch (Throwable th) {
                this.origin_server = null;
                this.origin_auth = null;
            }
            this.origin_auth = null;
        }
    }

    static SID[] getGroupMemberSids0(DcerpcHandle handle, SamrDomainHandle domainHandle, SID domsid, int rid, int flags) throws IOException {
        Throwable th;
        SamrAliasHandle aliasHandle = null;
        LsarSidArray sidarray = new LsarSidArray();
        try {
            MsrpcGetMembersInAlias rpc;
            SamrAliasHandle aliasHandle2 = new SamrAliasHandle(handle, domainHandle, 131084, rid);
            try {
                rpc = new MsrpcGetMembersInAlias(aliasHandle2, sidarray);
            } catch (Throwable th2) {
                th = th2;
                aliasHandle = aliasHandle2;
                if (aliasHandle != null) {
                    aliasHandle.close();
                }
                throw th;
            }
            try {
                handle.sendrecv(rpc);
                if (rpc.retval != 0) {
                    throw new SmbException(rpc.retval, false);
                }
                SID[] sids = new SID[rpc.sids.num_sids];
                String origin_server = handle.getServer();
                NtlmPasswordAuthentication origin_auth = (NtlmPasswordAuthentication) handle.getPrincipal();
                for (int i = 0; i < sids.length; i++) {
                    sids[i] = new SID(rpc.sids.sids[i].sid, 0, null, null, false);
                    sids[i].origin_server = origin_server;
                    sids[i].origin_auth = origin_auth;
                }
                if (sids.length > 0 && (flags & 1) != 0) {
                    resolveSids(origin_server, origin_auth, sids);
                }
                if (aliasHandle2 != null) {
                    aliasHandle2.close();
                }
                return sids;
            } catch (Throwable th3) {
                th = th3;
                MsrpcGetMembersInAlias msrpcGetMembersInAlias = rpc;
                aliasHandle = aliasHandle2;
                if (aliasHandle != null) {
                    aliasHandle.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (aliasHandle != null) {
                aliasHandle.close();
            }
            throw th;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public jcifs.smb.SID[] getGroupMemberSids(java.lang.String r10, jcifs.smb.NtlmPasswordAuthentication r11, int r12) throws java.io.IOException {
        /*
        r9 = this;
        r6 = r9.type;
        r7 = 2;
        if (r6 == r7) goto L_0x000e;
    L_0x0005:
        r6 = r9.type;
        r7 = 4;
        if (r6 == r7) goto L_0x000e;
    L_0x000a:
        r6 = 0;
        r6 = new jcifs.smb.SID[r6];
    L_0x000d:
        return r6;
    L_0x000e:
        r3 = 0;
        r4 = 0;
        r0 = 0;
        r2 = r9.getDomainSid();
        r7 = sid_cache;
        monitor-enter(r7);
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0063 }
        r6.<init>();	 Catch:{ all -> 0x0063 }
        r8 = "ncacn_np:";
        r6 = r6.append(r8);	 Catch:{ all -> 0x0063 }
        r6 = r6.append(r10);	 Catch:{ all -> 0x0063 }
        r8 = "[\\PIPE\\samr]";
        r6 = r6.append(r8);	 Catch:{ all -> 0x0063 }
        r6 = r6.toString();	 Catch:{ all -> 0x0063 }
        r3 = jcifs.dcerpc.DcerpcHandle.getHandle(r6, r11);	 Catch:{ all -> 0x0063 }
        r5 = new jcifs.dcerpc.msrpc.SamrPolicyHandle;	 Catch:{ all -> 0x0063 }
        r6 = 48;
        r5.<init>(r3, r10, r6);	 Catch:{ all -> 0x0063 }
        r1 = new jcifs.dcerpc.msrpc.SamrDomainHandle;	 Catch:{ all -> 0x0076 }
        r6 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r1.<init>(r3, r5, r6, r2);	 Catch:{ all -> 0x0076 }
        r6 = r9.getRid();	 Catch:{ all -> 0x0079 }
        r6 = getGroupMemberSids0(r3, r1, r2, r6, r12);	 Catch:{ all -> 0x0079 }
        if (r3 == 0) goto L_0x005c;
    L_0x004f:
        if (r5 == 0) goto L_0x0059;
    L_0x0051:
        if (r1 == 0) goto L_0x0056;
    L_0x0053:
        r1.close();	 Catch:{ all -> 0x005e }
    L_0x0056:
        r5.close();	 Catch:{ all -> 0x005e }
    L_0x0059:
        r3.close();	 Catch:{ all -> 0x005e }
    L_0x005c:
        monitor-exit(r7);	 Catch:{ all -> 0x005e }
        goto L_0x000d;
    L_0x005e:
        r6 = move-exception;
        r0 = r1;
        r4 = r5;
    L_0x0061:
        monitor-exit(r7);	 Catch:{ all -> 0x0074 }
        throw r6;
    L_0x0063:
        r6 = move-exception;
    L_0x0064:
        if (r3 == 0) goto L_0x0073;
    L_0x0066:
        if (r4 == 0) goto L_0x0070;
    L_0x0068:
        if (r0 == 0) goto L_0x006d;
    L_0x006a:
        r0.close();	 Catch:{ all -> 0x0074 }
    L_0x006d:
        r4.close();	 Catch:{ all -> 0x0074 }
    L_0x0070:
        r3.close();	 Catch:{ all -> 0x0074 }
    L_0x0073:
        throw r6;	 Catch:{ all -> 0x0074 }
    L_0x0074:
        r6 = move-exception;
        goto L_0x0061;
    L_0x0076:
        r6 = move-exception;
        r4 = r5;
        goto L_0x0064;
    L_0x0079:
        r6 = move-exception;
        r0 = r1;
        r4 = r5;
        goto L_0x0064;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SID.getGroupMemberSids(java.lang.String, jcifs.smb.NtlmPasswordAuthentication, int):jcifs.smb.SID[]");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.Map getLocalGroupsMap(java.lang.String r22, jcifs.smb.NtlmPasswordAuthentication r23, int r24) throws java.io.IOException {
        /*
        r5 = getServerSid(r22, r23);
        r10 = 0;
        r14 = 0;
        r3 = 0;
        r17 = new jcifs.dcerpc.msrpc.samr$SamrSamArray;
        r17.<init>();
        r19 = sid_cache;
        monitor-enter(r19);
        r18 = new java.lang.StringBuilder;	 Catch:{ all -> 0x013f }
        r18.<init>();	 Catch:{ all -> 0x013f }
        r20 = "ncacn_np:";
        r0 = r18;
        r1 = r20;
        r18 = r0.append(r1);	 Catch:{ all -> 0x013f }
        r0 = r18;
        r1 = r22;
        r18 = r0.append(r1);	 Catch:{ all -> 0x013f }
        r20 = "[\\PIPE\\samr]";
        r0 = r18;
        r1 = r20;
        r18 = r0.append(r1);	 Catch:{ all -> 0x013f }
        r18 = r18.toString();	 Catch:{ all -> 0x013f }
        r0 = r18;
        r1 = r23;
        r10 = jcifs.dcerpc.DcerpcHandle.getHandle(r0, r1);	 Catch:{ all -> 0x013f }
        r15 = new jcifs.dcerpc.msrpc.SamrPolicyHandle;	 Catch:{ all -> 0x013f }
        r18 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r0 = r22;
        r1 = r18;
        r15.<init>(r10, r0, r1);	 Catch:{ all -> 0x013f }
        r4 = new jcifs.dcerpc.msrpc.SamrDomainHandle;	 Catch:{ all -> 0x0142 }
        r18 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r0 = r18;
        r4.<init>(r10, r15, r0, r5);	 Catch:{ all -> 0x0142 }
        r16 = new jcifs.dcerpc.msrpc.MsrpcEnumerateAliasesInDomain;	 Catch:{ all -> 0x0081 }
        r18 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r0 = r16;
        r1 = r18;
        r2 = r17;
        r0.<init>(r4, r1, r2);	 Catch:{ all -> 0x0081 }
        r0 = r16;
        r10.sendrecv(r0);	 Catch:{ all -> 0x0081 }
        r0 = r16;
        r0 = r0.retval;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        if (r18 == 0) goto L_0x0097;
    L_0x006d:
        r18 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x0081 }
        r0 = r16;
        r0 = r0.retval;	 Catch:{ all -> 0x0081 }
        r20 = r0;
        r21 = 0;
        r0 = r18;
        r1 = r20;
        r2 = r21;
        r0.<init>(r1, r2);	 Catch:{ all -> 0x0081 }
        throw r18;	 Catch:{ all -> 0x0081 }
    L_0x0081:
        r18 = move-exception;
        r3 = r4;
        r14 = r15;
    L_0x0084:
        if (r10 == 0) goto L_0x0093;
    L_0x0086:
        if (r14 == 0) goto L_0x0090;
    L_0x0088:
        if (r3 == 0) goto L_0x008d;
    L_0x008a:
        r3.close();	 Catch:{ all -> 0x0094 }
    L_0x008d:
        r14.close();	 Catch:{ all -> 0x0094 }
    L_0x0090:
        r10.close();	 Catch:{ all -> 0x0094 }
    L_0x0093:
        throw r18;	 Catch:{ all -> 0x0094 }
    L_0x0094:
        r18 = move-exception;
    L_0x0095:
        monitor-exit(r19);	 Catch:{ all -> 0x0094 }
        throw r18;
    L_0x0097:
        r11 = new java.util.HashMap;	 Catch:{ all -> 0x0081 }
        r11.<init>();	 Catch:{ all -> 0x0081 }
        r6 = 0;
    L_0x009d:
        r0 = r16;
        r0 = r0.sam;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        r0 = r0.count;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        if (r6 >= r0) goto L_0x0129;
    L_0x00ad:
        r0 = r16;
        r0 = r0.sam;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        r0 = r0.entries;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r7 = r18[r6];	 Catch:{ all -> 0x0081 }
        r0 = r7.idx;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        r1 = r24;
        r12 = getGroupMemberSids0(r10, r4, r5, r0, r1);	 Catch:{ all -> 0x0081 }
        r8 = new jcifs.smb.SID;	 Catch:{ all -> 0x0081 }
        r0 = r7.idx;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        r8.<init>(r5, r0);	 Catch:{ all -> 0x0081 }
        r18 = 4;
        r0 = r18;
        r8.type = r0;	 Catch:{ all -> 0x0081 }
        r18 = r5.getDomainName();	 Catch:{ all -> 0x0081 }
        r0 = r18;
        r8.domainName = r0;	 Catch:{ all -> 0x0081 }
        r18 = new jcifs.dcerpc.UnicodeString;	 Catch:{ all -> 0x0081 }
        r0 = r7.name;	 Catch:{ all -> 0x0081 }
        r20 = r0;
        r21 = 0;
        r0 = r18;
        r1 = r20;
        r2 = r21;
        r0.<init>(r1, r2);	 Catch:{ all -> 0x0081 }
        r18 = r18.toString();	 Catch:{ all -> 0x0081 }
        r0 = r18;
        r8.acctName = r0;	 Catch:{ all -> 0x0081 }
        r13 = 0;
    L_0x00fa:
        r0 = r12.length;	 Catch:{ all -> 0x0081 }
        r18 = r0;
        r0 = r18;
        if (r13 >= r0) goto L_0x0125;
    L_0x0101:
        r18 = r12[r13];	 Catch:{ all -> 0x0081 }
        r0 = r18;
        r9 = r11.get(r0);	 Catch:{ all -> 0x0081 }
        r9 = (java.util.ArrayList) r9;	 Catch:{ all -> 0x0081 }
        if (r9 != 0) goto L_0x0119;
    L_0x010d:
        r9 = new java.util.ArrayList;	 Catch:{ all -> 0x0081 }
        r9.<init>();	 Catch:{ all -> 0x0081 }
        r18 = r12[r13];	 Catch:{ all -> 0x0081 }
        r0 = r18;
        r11.put(r0, r9);	 Catch:{ all -> 0x0081 }
    L_0x0119:
        r18 = r9.contains(r8);	 Catch:{ all -> 0x0081 }
        if (r18 != 0) goto L_0x0122;
    L_0x011f:
        r9.add(r8);	 Catch:{ all -> 0x0081 }
    L_0x0122:
        r13 = r13 + 1;
        goto L_0x00fa;
    L_0x0125:
        r6 = r6 + 1;
        goto L_0x009d;
    L_0x0129:
        if (r10 == 0) goto L_0x0138;
    L_0x012b:
        if (r15 == 0) goto L_0x0135;
    L_0x012d:
        if (r4 == 0) goto L_0x0132;
    L_0x012f:
        r4.close();	 Catch:{ all -> 0x013a }
    L_0x0132:
        r15.close();	 Catch:{ all -> 0x013a }
    L_0x0135:
        r10.close();	 Catch:{ all -> 0x013a }
    L_0x0138:
        monitor-exit(r19);	 Catch:{ all -> 0x013a }
        return r11;
    L_0x013a:
        r18 = move-exception;
        r3 = r4;
        r14 = r15;
        goto L_0x0095;
    L_0x013f:
        r18 = move-exception;
        goto L_0x0084;
    L_0x0142:
        r18 = move-exception;
        r14 = r15;
        goto L_0x0084;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SID.getLocalGroupsMap(java.lang.String, jcifs.smb.NtlmPasswordAuthentication, int):java.util.Map");
    }
}
