package jcifs.smb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import jcifs.Config;
import jcifs.UniAddress;
import jcifs.netbios.NbtAddress;
import jcifs.util.LogStream;

public final class SmbSession {
    private static final int CACHE_POLICY = (Config.getInt("jcifs.netbios.cachePolicy", 600) * 60);
    private static final String DOMAIN = Config.getProperty("jcifs.smb.client.domain", null);
    private static final String LOGON_SHARE = Config.getProperty("jcifs.smb.client.logonShare", null);
    private static final int LOOKUP_RESP_LIMIT = Config.getInt("jcifs.netbios.lookupRespLimit", 3);
    private static final String USERNAME = Config.getProperty("jcifs.smb.client.username", null);
    static NbtAddress[] dc_list = null;
    static int dc_list_counter;
    static long dc_list_expiration;
    private UniAddress address;
    NtlmPasswordAuthentication auth;
    int connectionState;
    long expiration;
    private InetAddress localAddr;
    private int localPort;
    String netbiosName = null;
    private int port;
    SmbTransport transport = null;
    Vector trees;
    int uid;

    private static NtlmChallenge interrogate(NbtAddress addr) throws SmbException {
        UniAddress dc = new UniAddress(addr);
        SmbTransport trans = SmbTransport.getSmbTransport(dc, 0);
        if (USERNAME == null) {
            trans.connect();
            LogStream logStream = SmbTransport.log;
            if (LogStream.level >= 3) {
                SmbTransport.log.println("Default credentials (jcifs.smb.client.username/password) not specified. SMB signing may not work propertly.  Skipping DC interrogation.");
            }
        } else {
            trans.getSmbSession(NtlmPasswordAuthentication.DEFAULT).getSmbTree(LOGON_SHARE, null).treeConnect(null, null);
        }
        return new NtlmChallenge(trans.server.encryptionKey, dc);
    }

    public static NtlmChallenge getChallengeForDomain() throws SmbException, UnknownHostException {
        LogStream logStream;
        if (DOMAIN == null) {
            throw new SmbException("A domain was not specified");
        }
        synchronized (DOMAIN) {
            int i;
            NtlmChallenge interrogate;
            long now = System.currentTimeMillis();
            int retry = 1;
            loop0:
            while (true) {
                if (dc_list_expiration < now) {
                    NbtAddress[] list = NbtAddress.getAllByName(DOMAIN, 28, null, null);
                    dc_list_expiration = (((long) CACHE_POLICY) * 1000) + now;
                    if (list == null || list.length <= 0) {
                        dc_list_expiration = 900000 + now;
                        logStream = SmbTransport.log;
                        if (LogStream.level >= 2) {
                            SmbTransport.log.println("Failed to retrieve DC list from WINS");
                        }
                    } else {
                        dc_list = list;
                    }
                }
                int max = Math.min(dc_list.length, LOOKUP_RESP_LIMIT);
                int j = 0;
                while (j < max) {
                    int i2 = dc_list_counter;
                    dc_list_counter = i2 + 1;
                    i = i2 % max;
                    if (dc_list[i] != null) {
                        try {
                            interrogate = interrogate(dc_list[i]);
                            break loop0;
                        } catch (SmbException se) {
                            logStream = SmbTransport.log;
                            if (LogStream.level >= 2) {
                                SmbTransport.log.println("Failed validate DC: " + dc_list[i]);
                                logStream = SmbTransport.log;
                                if (LogStream.level > 2) {
                                    se.printStackTrace(SmbTransport.log);
                                }
                            }
                            dc_list[i] = null;
                        }
                    } else {
                        j++;
                    }
                }
                dc_list_expiration = 0;
                int retry2 = retry - 1;
                if (retry <= 0) {
                    dc_list_expiration = 900000 + now;
                    throw new UnknownHostException("Failed to negotiate with a suitable domain controller for " + DOMAIN);
                }
                retry = retry2;
            }
            return interrogate;
        }
    }

    public static byte[] getChallenge(UniAddress dc) throws SmbException, UnknownHostException {
        return getChallenge(dc, 0);
    }

    public static byte[] getChallenge(UniAddress dc, int port) throws SmbException, UnknownHostException {
        SmbTransport trans = SmbTransport.getSmbTransport(dc, port);
        trans.connect();
        return trans.server.encryptionKey;
    }

    public static void logon(UniAddress dc, NtlmPasswordAuthentication auth) throws SmbException {
        logon(dc, 0, auth);
    }

    public static void logon(UniAddress dc, int port, NtlmPasswordAuthentication auth) throws SmbException {
        SmbTree tree = SmbTransport.getSmbTransport(dc, port).getSmbSession(auth).getSmbTree(LOGON_SHARE, null);
        if (LOGON_SHARE == null) {
            tree.treeConnect(null, null);
        } else {
            tree.send(new Trans2FindFirst2("\\", "*", 16), new Trans2FindFirst2Response());
        }
    }

    SmbSession(UniAddress address, int port, InetAddress localAddr, int localPort, NtlmPasswordAuthentication auth) {
        this.address = address;
        this.port = port;
        this.localAddr = localAddr;
        this.localPort = localPort;
        this.auth = auth;
        this.trees = new Vector();
        this.connectionState = 0;
    }

    synchronized SmbTree getSmbTree(String share, String service) {
        Object t;
        SmbTree t2;
        if (share == null) {
            share = "IPC$";
        }
        Enumeration e = this.trees.elements();
        while (e.hasMoreElements()) {
            t2 = (SmbTree) e.nextElement();
            if (t2.matches(share, service)) {
                t = t2;
                break;
            }
        }
        t2 = new SmbTree(this, share, service);
        this.trees.addElement(t2);
        SmbTree t3 = t2;
        return t;
    }

    boolean matches(NtlmPasswordAuthentication auth) {
        return this.auth == auth || this.auth.equals(auth);
    }

    synchronized SmbTransport transport() {
        if (this.transport == null) {
            this.transport = SmbTransport.getSmbTransport(this.address, this.port, this.localAddr, this.localPort, null);
        }
        return this.transport;
    }

    void send(ServerMessageBlock request, ServerMessageBlock response) throws SmbException {
        synchronized (transport()) {
            if (response != null) {
                response.received = false;
            }
            this.expiration = System.currentTimeMillis() + ((long) SmbTransport.SO_TIMEOUT);
            sessionSetup(request, response);
            if (response == null || !response.received) {
                if (request instanceof SmbComTreeConnectAndX) {
                    SmbComTreeConnectAndX tcax = (SmbComTreeConnectAndX) request;
                    if (this.netbiosName != null && tcax.path.endsWith("\\IPC$")) {
                        tcax.path = "\\\\" + this.netbiosName + "\\IPC$";
                    }
                }
                request.uid = this.uid;
                request.auth = this.auth;
                try {
                    this.transport.send(request, response);
                    return;
                } catch (SmbException se) {
                    if (request instanceof SmbComTreeConnectAndX) {
                        logoff(true);
                    }
                    request.digest = null;
                    throw se;
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void sessionSetup(jcifs.smb.ServerMessageBlock r19, jcifs.smb.ServerMessageBlock r20) throws jcifs.smb.SmbException {
        /*
        r18 = this;
        r15 = r18.transport();
        monitor-enter(r15);
        r5 = 0;
        r3 = 0;
        r14 = 0;
        r13 = new byte[r14];	 Catch:{ all -> 0x003d }
        r12 = 10;
    L_0x000c:
        r0 = r18;
        r14 = r0.connectionState;	 Catch:{ all -> 0x003d }
        if (r14 == 0) goto L_0x0040;
    L_0x0012:
        r0 = r18;
        r14 = r0.connectionState;	 Catch:{ all -> 0x003d }
        r16 = 2;
        r0 = r16;
        if (r14 == r0) goto L_0x0026;
    L_0x001c:
        r0 = r18;
        r14 = r0.connectionState;	 Catch:{ all -> 0x003d }
        r16 = 3;
        r0 = r16;
        if (r14 != r0) goto L_0x0028;
    L_0x0026:
        monitor-exit(r15);	 Catch:{ all -> 0x003d }
    L_0x0027:
        return;
    L_0x0028:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ InterruptedException -> 0x0030 }
        r14.wait();	 Catch:{ InterruptedException -> 0x0030 }
        goto L_0x000c;
    L_0x0030:
        r4 = move-exception;
        r14 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x003d }
        r16 = r4.getMessage();	 Catch:{ all -> 0x003d }
        r0 = r16;
        r14.<init>(r0, r4);	 Catch:{ all -> 0x003d }
        throw r14;	 Catch:{ all -> 0x003d }
    L_0x003d:
        r14 = move-exception;
        monitor-exit(r15);	 Catch:{ all -> 0x003d }
        throw r14;
    L_0x0040:
        r14 = 1;
        r0 = r18;
        r0.connectionState = r14;	 Catch:{ all -> 0x003d }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x02a9 }
        r14.connect();	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x02a9 }
        r14 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x02a9 }
        r14 = jcifs.util.LogStream.level;	 Catch:{ SmbException -> 0x02a9 }
        r16 = 4;
        r0 = r16;
        if (r14 < r0) goto L_0x009c;
    L_0x005a:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x02a9 }
        r14 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x02a9 }
        r16 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x02a9 }
        r16.<init>();	 Catch:{ SmbException -> 0x02a9 }
        r17 = "sessionSetup: accountName=";
        r16 = r16.append(r17);	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x02a9 }
        r17 = r0;
        r0 = r17;
        r0 = r0.username;	 Catch:{ SmbException -> 0x02a9 }
        r17 = r0;
        r16 = r16.append(r17);	 Catch:{ SmbException -> 0x02a9 }
        r17 = ",primaryDomain=";
        r16 = r16.append(r17);	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x02a9 }
        r17 = r0;
        r0 = r17;
        r0 = r0.domain;	 Catch:{ SmbException -> 0x02a9 }
        r17 = r0;
        r16 = r16.append(r17);	 Catch:{ SmbException -> 0x02a9 }
        r16 = r16.toString();	 Catch:{ SmbException -> 0x02a9 }
        r0 = r16;
        r14.println(r0);	 Catch:{ SmbException -> 0x02a9 }
    L_0x009c:
        r14 = 0;
        r0 = r18;
        r0.uid = r14;	 Catch:{ SmbException -> 0x02a9 }
    L_0x00a1:
        r6 = r5;
        switch(r12) {
            case 10: goto L_0x00dc;
            case 20: goto L_0x01f7;
            default: goto L_0x00a5;
        };
    L_0x00a5:
        r14 = new jcifs.smb.SmbException;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16.<init>();	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r17 = "Unexpected session setup state: ";
        r16 = r16.append(r17);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r16;
        r16 = r0.append(r12);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r16.toString();	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r16;
        r14.<init>(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        throw r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x00c3:
        r10 = move-exception;
        r5 = r6;
    L_0x00c5:
        r14 = 1;
        r0 = r18;
        r0.logoff(r14);	 Catch:{ all -> 0x00d1 }
        r14 = 0;
        r0 = r18;
        r0.connectionState = r14;	 Catch:{ all -> 0x00d1 }
        throw r10;	 Catch:{ all -> 0x00d1 }
    L_0x00d1:
        r14 = move-exception;
    L_0x00d2:
        r0 = r18;
        r0 = r0.transport;	 Catch:{ all -> 0x003d }
        r16 = r0;
        r16.notifyAll();	 Catch:{ all -> 0x003d }
        throw r14;	 Catch:{ all -> 0x003d }
    L_0x00dc:
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = jcifs.smb.NtlmPasswordAuthentication.ANONYMOUS;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r16;
        if (r14 == r0) goto L_0x0103;
    L_0x00e6:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r16;
        r14 = r14.hasCapability(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x0103;
    L_0x00f4:
        r12 = 20;
        r5 = r6;
    L_0x00f7:
        if (r12 != 0) goto L_0x00a1;
    L_0x00f9:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ all -> 0x003d }
        r14.notifyAll();	 Catch:{ all -> 0x003d }
        monitor-exit(r15);	 Catch:{ all -> 0x003d }
        goto L_0x0027;
    L_0x0103:
        r7 = new jcifs.smb.SmbComSessionSetupAndX;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r1 = r19;
        r7.<init>(r0, r1, r14);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r8 = new jcifs.smb.SmbComSessionSetupAndXResponse;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r20;
        r8.<init>(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r14 = r14.isSignatureSetupRequired(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x015d;
    L_0x0129:
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r14.hashesExternal;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x01a8;
    L_0x0131:
        r14 = jcifs.smb.NtlmPasswordAuthentication.DEFAULT_PASSWORD;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = "";
        r0 = r16;
        if (r14 == r0) goto L_0x01a8;
    L_0x013a:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = jcifs.smb.NtlmPasswordAuthentication.DEFAULT;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r16;
        r14 = r14.getSmbSession(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = LOGON_SHARE;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r17 = 0;
        r0 = r16;
        r1 = r17;
        r14 = r14.getSmbTree(r0, r1);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = 0;
        r17 = 0;
        r0 = r16;
        r1 = r17;
        r14.treeConnect(r0, r1);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x015d:
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r7.auth = r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbAuthException -> 0x01d0, SmbException -> 0x01d2, all -> 0x01a4 }
        r14.send(r7, r8);	 Catch:{ SmbAuthException -> 0x01d0, SmbException -> 0x01d2, all -> 0x01a4 }
    L_0x016a:
        r14 = r8.isLoggedInAsGuest;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x01d5;
    L_0x016e:
        r14 = "GUEST";
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r0 = r0.username;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r14 = r14.equalsIgnoreCase(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 != 0) goto L_0x01d5;
    L_0x0185:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r14.server;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r14.security;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x01d5;
    L_0x018f:
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = jcifs.smb.NtlmPasswordAuthentication.ANONYMOUS;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r16;
        if (r14 == r0) goto L_0x01d5;
    L_0x0199:
        r14 = new jcifs.smb.SmbAuthException;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = -1073741715; // 0xffffffffc000006d float:-2.000026 double:NaN;
        r0 = r16;
        r14.<init>(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        throw r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x01a4:
        r14 = move-exception;
        r5 = r6;
        goto L_0x00d2;
    L_0x01a8:
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r0 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r0 = r0.server;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r0 = r0.encryptionKey;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r11 = r14.getSigningKey(r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = new jcifs.smb.SigningDigest;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = 0;
        r0 = r16;
        r14.<init>(r11, r0);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r7.digest = r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        goto L_0x015d;
    L_0x01d0:
        r9 = move-exception;
        throw r9;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x01d2:
        r10 = move-exception;
        r3 = r10;
        goto L_0x016a;
    L_0x01d5:
        if (r3 == 0) goto L_0x01d8;
    L_0x01d7:
        throw r3;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x01d8:
        r14 = r8.uid;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r0.uid = r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r7.digest;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        if (r14 == 0) goto L_0x01ee;
    L_0x01e2:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r7.digest;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r16 = r0;
        r0 = r16;
        r14.digest = r0;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x01ee:
        r14 = 2;
        r0 = r18;
        r0.connectionState = r14;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r12 = 0;
        r5 = r6;
        goto L_0x00f7;
    L_0x01f7:
        if (r6 != 0) goto L_0x02ef;
    L_0x01f9:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r14.flags2;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r14 = r14 & 4;
        if (r14 == 0) goto L_0x0232;
    L_0x0203:
        r2 = 1;
    L_0x0204:
        r5 = new jcifs.smb.NtlmContext;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r0 = r18;
        r14 = r0.auth;	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
        r5.<init>(r14, r2);	 Catch:{ SmbException -> 0x00c3, all -> 0x01a4 }
    L_0x020d:
        r14 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x02a9 }
        r14 = jcifs.util.LogStream.level;	 Catch:{ SmbException -> 0x02a9 }
        r16 = 4;
        r0 = r16;
        if (r14 < r0) goto L_0x021c;
    L_0x0217:
        r14 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x02a9 }
        r14.println(r5);	 Catch:{ SmbException -> 0x02a9 }
    L_0x021c:
        r14 = r5.isEstablished();	 Catch:{ SmbException -> 0x02a9 }
        if (r14 == 0) goto L_0x0234;
    L_0x0222:
        r14 = r5.getNetbiosName();	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r0.netbiosName = r14;	 Catch:{ SmbException -> 0x02a9 }
        r14 = 2;
        r0 = r18;
        r0.connectionState = r14;	 Catch:{ SmbException -> 0x02a9 }
        r12 = 0;
        goto L_0x00f7;
    L_0x0232:
        r2 = 0;
        goto L_0x0204;
    L_0x0234:
        r14 = 0;
        r0 = r13.length;	 Catch:{ SmbException -> 0x02ac }
        r16 = r0;
        r0 = r16;
        r13 = r5.initSecContext(r13, r14, r0);	 Catch:{ SmbException -> 0x02ac }
        if (r13 == 0) goto L_0x00f7;
    L_0x0240:
        r7 = new jcifs.smb.SmbComSessionSetupAndX;	 Catch:{ SmbException -> 0x02a9 }
        r14 = 0;
        r0 = r18;
        r7.<init>(r0, r14, r13);	 Catch:{ SmbException -> 0x02a9 }
        r8 = new jcifs.smb.SmbComSessionSetupAndXResponse;	 Catch:{ SmbException -> 0x02a9 }
        r14 = 0;
        r8.<init>(r14);	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x02a9 }
        r16 = r0;
        r0 = r16;
        r14 = r14.isSignatureSetupRequired(r0);	 Catch:{ SmbException -> 0x02a9 }
        if (r14 == 0) goto L_0x0271;
    L_0x0260:
        r11 = r5.getSigningKey();	 Catch:{ SmbException -> 0x02a9 }
        if (r11 == 0) goto L_0x0271;
    L_0x0266:
        r14 = new jcifs.smb.SigningDigest;	 Catch:{ SmbException -> 0x02a9 }
        r16 = 1;
        r0 = r16;
        r14.<init>(r11, r0);	 Catch:{ SmbException -> 0x02a9 }
        r7.digest = r14;	 Catch:{ SmbException -> 0x02a9 }
    L_0x0271:
        r0 = r18;
        r14 = r0.uid;	 Catch:{ SmbException -> 0x02a9 }
        r7.uid = r14;	 Catch:{ SmbException -> 0x02a9 }
        r14 = 0;
        r0 = r18;
        r0.uid = r14;	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbAuthException -> 0x02be, SmbException -> 0x02c0 }
        r14.send(r7, r8);	 Catch:{ SmbAuthException -> 0x02be, SmbException -> 0x02c0 }
    L_0x0283:
        r14 = r8.isLoggedInAsGuest;	 Catch:{ SmbException -> 0x02a9 }
        if (r14 == 0) goto L_0x02d0;
    L_0x0287:
        r14 = "GUEST";
        r0 = r18;
        r0 = r0.auth;	 Catch:{ SmbException -> 0x02a9 }
        r16 = r0;
        r0 = r16;
        r0 = r0.username;	 Catch:{ SmbException -> 0x02a9 }
        r16 = r0;
        r0 = r16;
        r14 = r14.equalsIgnoreCase(r0);	 Catch:{ SmbException -> 0x02a9 }
        if (r14 != 0) goto L_0x02d0;
    L_0x029e:
        r14 = new jcifs.smb.SmbAuthException;	 Catch:{ SmbException -> 0x02a9 }
        r16 = -1073741715; // 0xffffffffc000006d float:-2.000026 double:NaN;
        r0 = r16;
        r14.<init>(r0);	 Catch:{ SmbException -> 0x02a9 }
        throw r14;	 Catch:{ SmbException -> 0x02a9 }
    L_0x02a9:
        r10 = move-exception;
        goto L_0x00c5;
    L_0x02ac:
        r10 = move-exception;
        r0 = r18;
        r14 = r0.transport;	 Catch:{ IOException -> 0x02ed }
        r16 = 1;
        r0 = r16;
        r14.disconnect(r0);	 Catch:{ IOException -> 0x02ed }
    L_0x02b8:
        r14 = 0;
        r0 = r18;
        r0.uid = r14;	 Catch:{ SmbException -> 0x02a9 }
        throw r10;	 Catch:{ SmbException -> 0x02a9 }
    L_0x02be:
        r9 = move-exception;
        throw r9;	 Catch:{ SmbException -> 0x02a9 }
    L_0x02c0:
        r10 = move-exception;
        r3 = r10;
        r0 = r18;
        r14 = r0.transport;	 Catch:{ Exception -> 0x02ce }
        r16 = 1;
        r0 = r16;
        r14.disconnect(r0);	 Catch:{ Exception -> 0x02ce }
        goto L_0x0283;
    L_0x02ce:
        r14 = move-exception;
        goto L_0x0283;
    L_0x02d0:
        if (r3 == 0) goto L_0x02d3;
    L_0x02d2:
        throw r3;	 Catch:{ SmbException -> 0x02a9 }
    L_0x02d3:
        r14 = r8.uid;	 Catch:{ SmbException -> 0x02a9 }
        r0 = r18;
        r0.uid = r14;	 Catch:{ SmbException -> 0x02a9 }
        r14 = r7.digest;	 Catch:{ SmbException -> 0x02a9 }
        if (r14 == 0) goto L_0x02e9;
    L_0x02dd:
        r0 = r18;
        r14 = r0.transport;	 Catch:{ SmbException -> 0x02a9 }
        r0 = r7.digest;	 Catch:{ SmbException -> 0x02a9 }
        r16 = r0;
        r0 = r16;
        r14.digest = r0;	 Catch:{ SmbException -> 0x02a9 }
    L_0x02e9:
        r13 = r8.blob;	 Catch:{ SmbException -> 0x02a9 }
        goto L_0x00f7;
    L_0x02ed:
        r14 = move-exception;
        goto L_0x02b8;
    L_0x02ef:
        r5 = r6;
        goto L_0x020d;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SmbSession.sessionSetup(jcifs.smb.ServerMessageBlock, jcifs.smb.ServerMessageBlock):void");
    }

    void logoff(boolean inError) {
        synchronized (transport()) {
            if (this.connectionState != 2) {
                return;
            }
            this.connectionState = 3;
            this.netbiosName = null;
            Enumeration e = this.trees.elements();
            while (e.hasMoreElements()) {
                ((SmbTree) e.nextElement()).treeDisconnect(inError);
            }
            if (!inError) {
                if (this.transport.server.security != 0) {
                    SmbComLogoffAndX request = new SmbComLogoffAndX(null);
                    request.uid = this.uid;
                    try {
                        this.transport.send(request, null);
                    } catch (SmbException e2) {
                    }
                    this.uid = 0;
                }
            }
            this.connectionState = 0;
            this.transport.notifyAll();
        }
    }

    public String toString() {
        return "SmbSession[accountName=" + this.auth.username + ",primaryDomain=" + this.auth.domain + ",uid=" + this.uid + ",connectionState=" + this.connectionState + "]";
    }
}
