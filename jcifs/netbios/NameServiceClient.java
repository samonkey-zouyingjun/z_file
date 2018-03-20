package jcifs.netbios;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.StringTokenizer;
import jcifs.Config;
import jcifs.util.Hexdump;
import jcifs.util.LogStream;

class NameServiceClient implements Runnable {
    static final int DEFAULT_RCV_BUF_SIZE = 576;
    static final int DEFAULT_RETRY_COUNT = 2;
    static final int DEFAULT_RETRY_TIMEOUT = 3000;
    static final int DEFAULT_SND_BUF_SIZE = 576;
    static final int DEFAULT_SO_TIMEOUT = 5000;
    private static final InetAddress LADDR = Config.getInetAddress("jcifs.netbios.laddr", null);
    private static final int LPORT = Config.getInt("jcifs.netbios.lport", 0);
    static final int NAME_SERVICE_UDP_PORT = 137;
    private static final int RCV_BUF_SIZE = Config.getInt("jcifs.netbios.rcv_buf_size", 576);
    static final int RESOLVER_BCAST = 2;
    static final int RESOLVER_LMHOSTS = 1;
    static final int RESOLVER_WINS = 3;
    private static final int RETRY_COUNT = Config.getInt("jcifs.netbios.retryCount", 2);
    private static final int RETRY_TIMEOUT = Config.getInt("jcifs.netbios.retryTimeout", DEFAULT_RETRY_TIMEOUT);
    private static final String RO = Config.getProperty("jcifs.resolveOrder");
    private static final int SND_BUF_SIZE = Config.getInt("jcifs.netbios.snd_buf_size", 576);
    private static final int SO_TIMEOUT = Config.getInt("jcifs.netbios.soTimeout", DEFAULT_SO_TIMEOUT);
    private static LogStream log = LogStream.getInstance();
    private final Object LOCK;
    InetAddress baddr;
    private int closeTimeout;
    private DatagramPacket in;
    InetAddress laddr;
    private int lport;
    private int nextNameTrnId;
    private DatagramPacket out;
    private byte[] rcv_buf;
    private int[] resolveOrder;
    private HashMap responseTable;
    private byte[] snd_buf;
    private DatagramSocket socket;
    private Thread thread;

    NameServiceClient() {
        this(LPORT, LADDR);
    }

    NameServiceClient(int lport, InetAddress laddr) {
        this.LOCK = new Object();
        this.responseTable = new HashMap();
        this.nextNameTrnId = 0;
        this.lport = lport;
        this.laddr = laddr;
        try {
            this.baddr = Config.getInetAddress("jcifs.netbios.baddr", InetAddress.getByName("255.255.255.255"));
        } catch (UnknownHostException e) {
        }
        this.snd_buf = new byte[SND_BUF_SIZE];
        this.rcv_buf = new byte[RCV_BUF_SIZE];
        this.out = new DatagramPacket(this.snd_buf, SND_BUF_SIZE, this.baddr, NAME_SERVICE_UDP_PORT);
        this.in = new DatagramPacket(this.rcv_buf, RCV_BUF_SIZE);
        if (RO != null && RO.length() != 0) {
            int[] tmp = new int[3];
            StringTokenizer st = new StringTokenizer(RO, ",");
            int i = 0;
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                int i2;
                if (s.equalsIgnoreCase("LMHOSTS")) {
                    i2 = i + 1;
                    tmp[i] = 1;
                    i = i2;
                } else if (s.equalsIgnoreCase("WINS")) {
                    if (NbtAddress.getWINSAddress() == null) {
                        r5 = log;
                        if (LogStream.level > 1) {
                            log.println("NetBIOS resolveOrder specifies WINS however the jcifs.netbios.wins property has not been set");
                        }
                    } else {
                        i2 = i + 1;
                        tmp[i] = 3;
                        i = i2;
                    }
                } else if (s.equalsIgnoreCase("BCAST")) {
                    i2 = i + 1;
                    tmp[i] = 2;
                    i = i2;
                } else if (!s.equalsIgnoreCase("DNS")) {
                    r5 = log;
                    if (LogStream.level > 1) {
                        log.println("unknown resolver method: " + s);
                    }
                }
            }
            this.resolveOrder = new int[i];
            System.arraycopy(tmp, 0, this.resolveOrder, 0, i);
        } else if (NbtAddress.getWINSAddress() == null) {
            this.resolveOrder = new int[2];
            this.resolveOrder[0] = 1;
            this.resolveOrder[1] = 2;
        } else {
            this.resolveOrder = new int[3];
            this.resolveOrder[0] = 1;
            this.resolveOrder[1] = 3;
            this.resolveOrder[2] = 2;
        }
    }

    int getNextNameTrnId() {
        int i = this.nextNameTrnId + 1;
        this.nextNameTrnId = i;
        if ((i & SupportMenu.USER_MASK) == 0) {
            this.nextNameTrnId = 1;
        }
        return this.nextNameTrnId;
    }

    void ensureOpen(int timeout) throws IOException {
        this.closeTimeout = 0;
        if (SO_TIMEOUT != 0) {
            this.closeTimeout = Math.max(SO_TIMEOUT, timeout);
        }
        if (this.socket == null) {
            this.socket = new DatagramSocket(this.lport, this.laddr);
            this.thread = new Thread(this, "JCIFS-NameServiceClient");
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    void tryClose() {
        synchronized (this.LOCK) {
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
            this.thread = null;
            this.responseTable.clear();
        }
    }

    public void run() {
        while (this.thread == Thread.currentThread()) {
            LogStream logStream;
            try {
                this.in.setLength(RCV_BUF_SIZE);
                this.socket.setSoTimeout(this.closeTimeout);
                this.socket.receive(this.in);
                logStream = log;
                if (LogStream.level > 3) {
                    log.println("NetBIOS: new data read from socket");
                }
                NameServicePacket response = (NameServicePacket) this.responseTable.get(new Integer(NameServicePacket.readNameTrnId(this.rcv_buf, 0)));
                if (!(response == null || response.received)) {
                    synchronized (response) {
                        response.readWireFormat(this.rcv_buf, 0);
                        response.received = true;
                        logStream = log;
                        if (LogStream.level > 3) {
                            log.println(response);
                            Hexdump.hexdump(log, this.rcv_buf, 0, this.in.getLength());
                        }
                        response.notify();
                    }
                }
            } catch (SocketTimeoutException e) {
                tryClose();
                return;
            } catch (Exception ex) {
                try {
                    logStream = log;
                    if (LogStream.level > 2) {
                        ex.printStackTrace(log);
                    }
                    tryClose();
                    return;
                } catch (Throwable th) {
                    tryClose();
                }
            }
        }
        tryClose();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void send(jcifs.netbios.NameServicePacket r13, jcifs.netbios.NameServicePacket r14, int r15) throws java.io.IOException {
        /*
        r12 = this;
        r3 = 0;
        r5 = jcifs.netbios.NbtAddress.NBNS;
        r1 = r5.length;
        if (r1 != 0) goto L_0x0007;
    L_0x0006:
        r1 = 1;
    L_0x0007:
        monitor-enter(r14);
        r2 = r1;
        r4 = r3;
    L_0x000a:
        r1 = r2 + -1;
        if (r2 <= 0) goto L_0x00d4;
    L_0x000e:
        r8 = r12.LOCK;	 Catch:{ InterruptedException -> 0x00cf, all -> 0x00cc }
        monitor-enter(r8);	 Catch:{ InterruptedException -> 0x00cf, all -> 0x00cc }
        r5 = r12.getNextNameTrnId();	 Catch:{ all -> 0x007b }
        r13.nameTrnId = r5;	 Catch:{ all -> 0x007b }
        r3 = new java.lang.Integer;	 Catch:{ all -> 0x007b }
        r5 = r13.nameTrnId;	 Catch:{ all -> 0x007b }
        r3.<init>(r5);	 Catch:{ all -> 0x007b }
        r5 = r12.out;	 Catch:{ all -> 0x00d2 }
        r9 = r13.addr;	 Catch:{ all -> 0x00d2 }
        r5.setAddress(r9);	 Catch:{ all -> 0x00d2 }
        r5 = r12.out;	 Catch:{ all -> 0x00d2 }
        r9 = r12.snd_buf;	 Catch:{ all -> 0x00d2 }
        r10 = 0;
        r9 = r13.writeWireFormat(r9, r10);	 Catch:{ all -> 0x00d2 }
        r5.setLength(r9);	 Catch:{ all -> 0x00d2 }
        r5 = 0;
        r14.received = r5;	 Catch:{ all -> 0x00d2 }
        r5 = r12.responseTable;	 Catch:{ all -> 0x00d2 }
        r5.put(r3, r14);	 Catch:{ all -> 0x00d2 }
        r5 = r15 + 1000;
        r12.ensureOpen(r5);	 Catch:{ all -> 0x00d2 }
        r5 = r12.socket;	 Catch:{ all -> 0x00d2 }
        r9 = r12.out;	 Catch:{ all -> 0x00d2 }
        r5.send(r9);	 Catch:{ all -> 0x00d2 }
        r5 = log;	 Catch:{ all -> 0x00d2 }
        r5 = jcifs.util.LogStream.level;	 Catch:{ all -> 0x00d2 }
        r9 = 3;
        if (r5 <= r9) goto L_0x005f;
    L_0x004c:
        r5 = log;	 Catch:{ all -> 0x00d2 }
        r5.println(r13);	 Catch:{ all -> 0x00d2 }
        r5 = log;	 Catch:{ all -> 0x00d2 }
        r9 = r12.snd_buf;	 Catch:{ all -> 0x00d2 }
        r10 = 0;
        r11 = r12.out;	 Catch:{ all -> 0x00d2 }
        r11 = r11.getLength();	 Catch:{ all -> 0x00d2 }
        jcifs.util.Hexdump.hexdump(r5, r9, r10, r11);	 Catch:{ all -> 0x00d2 }
    L_0x005f:
        monitor-exit(r8);	 Catch:{ all -> 0x00d2 }
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x007f }
    L_0x0064:
        if (r15 <= 0) goto L_0x00a0;
    L_0x0066:
        r8 = (long) r15;	 Catch:{ InterruptedException -> 0x007f }
        r14.wait(r8);	 Catch:{ InterruptedException -> 0x007f }
        r5 = r14.received;	 Catch:{ InterruptedException -> 0x007f }
        if (r5 == 0) goto L_0x0094;
    L_0x006e:
        r5 = r13.questionType;	 Catch:{ InterruptedException -> 0x007f }
        r8 = r14.recordType;	 Catch:{ InterruptedException -> 0x007f }
        if (r5 != r8) goto L_0x0094;
    L_0x0074:
        r5 = r12.responseTable;	 Catch:{ all -> 0x0091 }
        r5.remove(r3);	 Catch:{ all -> 0x0091 }
        monitor-exit(r14);	 Catch:{ all -> 0x0091 }
    L_0x007a:
        return;
    L_0x007b:
        r5 = move-exception;
        r3 = r4;
    L_0x007d:
        monitor-exit(r8);	 Catch:{ all -> 0x00d2 }
        throw r5;	 Catch:{ InterruptedException -> 0x007f }
    L_0x007f:
        r0 = move-exception;
    L_0x0080:
        r5 = new java.io.IOException;	 Catch:{ all -> 0x008a }
        r8 = r0.getMessage();	 Catch:{ all -> 0x008a }
        r5.<init>(r8);	 Catch:{ all -> 0x008a }
        throw r5;	 Catch:{ all -> 0x008a }
    L_0x008a:
        r5 = move-exception;
    L_0x008b:
        r8 = r12.responseTable;	 Catch:{ all -> 0x0091 }
        r8.remove(r3);	 Catch:{ all -> 0x0091 }
        throw r5;	 Catch:{ all -> 0x0091 }
    L_0x0091:
        r5 = move-exception;
        monitor-exit(r14);	 Catch:{ all -> 0x0091 }
        throw r5;
    L_0x0094:
        r5 = 0;
        r14.received = r5;	 Catch:{ InterruptedException -> 0x007f }
        r8 = (long) r15;	 Catch:{ InterruptedException -> 0x007f }
        r10 = java.lang.System.currentTimeMillis();	 Catch:{ InterruptedException -> 0x007f }
        r10 = r10 - r6;
        r8 = r8 - r10;
        r15 = (int) r8;
        goto L_0x0064;
    L_0x00a0:
        r5 = r12.responseTable;	 Catch:{ all -> 0x0091 }
        r5.remove(r3);	 Catch:{ all -> 0x0091 }
        r8 = r12.LOCK;	 Catch:{ all -> 0x0091 }
        monitor-enter(r8);	 Catch:{ all -> 0x0091 }
        r5 = r13.addr;	 Catch:{ all -> 0x00c9 }
        r5 = jcifs.netbios.NbtAddress.isWINS(r5);	 Catch:{ all -> 0x00c9 }
        if (r5 != 0) goto L_0x00b3;
    L_0x00b0:
        monitor-exit(r8);	 Catch:{ all -> 0x00c9 }
    L_0x00b1:
        monitor-exit(r14);	 Catch:{ all -> 0x0091 }
        goto L_0x007a;
    L_0x00b3:
        r5 = r13.addr;	 Catch:{ all -> 0x00c9 }
        r9 = jcifs.netbios.NbtAddress.getWINSAddress();	 Catch:{ all -> 0x00c9 }
        if (r5 != r9) goto L_0x00be;
    L_0x00bb:
        jcifs.netbios.NbtAddress.switchWINS();	 Catch:{ all -> 0x00c9 }
    L_0x00be:
        r5 = jcifs.netbios.NbtAddress.getWINSAddress();	 Catch:{ all -> 0x00c9 }
        r13.addr = r5;	 Catch:{ all -> 0x00c9 }
        monitor-exit(r8);	 Catch:{ all -> 0x00c9 }
        r2 = r1;
        r4 = r3;
        goto L_0x000a;
    L_0x00c9:
        r5 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x00c9 }
        throw r5;	 Catch:{ all -> 0x0091 }
    L_0x00cc:
        r5 = move-exception;
        r3 = r4;
        goto L_0x008b;
    L_0x00cf:
        r0 = move-exception;
        r3 = r4;
        goto L_0x0080;
    L_0x00d2:
        r5 = move-exception;
        goto L_0x007d;
    L_0x00d4:
        r3 = r4;
        goto L_0x00b1;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.netbios.NameServiceClient.send(jcifs.netbios.NameServicePacket, jcifs.netbios.NameServicePacket, int):void");
    }

    NbtAddress[] getAllByName(Name name, InetAddress addr) throws UnknownHostException {
        boolean z;
        int n;
        NameQueryRequest request = new NameQueryRequest(name);
        NameQueryResponse response = new NameQueryResponse();
        if (addr == null) {
            addr = NbtAddress.getWINSAddress();
        }
        request.addr = addr;
        if (request.addr == null) {
            z = true;
        } else {
            z = false;
        }
        request.isBroadcast = z;
        if (request.isBroadcast) {
            request.addr = this.baddr;
            n = RETRY_COUNT;
        } else {
            request.isBroadcast = false;
            n = 1;
        }
        do {
            try {
                send(request, response, RETRY_TIMEOUT);
                if (!response.received || response.resultCode != 0) {
                    n--;
                    if (n <= 0) {
                        break;
                    }
                } else {
                    return response.addrEntry;
                }
            } catch (IOException ioe) {
                LogStream logStream = log;
                if (LogStream.level > 1) {
                    ioe.printStackTrace(log);
                }
                throw new UnknownHostException(name.name);
            }
        } while (request.isBroadcast);
        throw new UnknownHostException(name.name);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    jcifs.netbios.NbtAddress getByName(jcifs.netbios.Name r13, java.net.InetAddress r14) throws java.net.UnknownHostException {
        /*
        r12 = this;
        r11 = 3;
        r9 = 1;
        r8 = 0;
        r6 = new jcifs.netbios.NameQueryRequest;
        r6.<init>(r13);
        r7 = new jcifs.netbios.NameQueryResponse;
        r7.<init>();
        if (r14 == 0) goto L_0x0066;
    L_0x000f:
        r6.addr = r14;
        r10 = r14.getAddress();
        r10 = r10[r11];
        r11 = -1;
        if (r10 != r11) goto L_0x001b;
    L_0x001a:
        r8 = r9;
    L_0x001b:
        r6.isBroadcast = r8;
        r4 = RETRY_COUNT;
    L_0x001f:
        r8 = RETRY_TIMEOUT;	 Catch:{ IOException -> 0x0042 }
        r12.send(r6, r7, r8);	 Catch:{ IOException -> 0x0042 }
        r8 = r7.received;
        if (r8 == 0) goto L_0x0056;
    L_0x0028:
        r8 = r7.resultCode;
        if (r8 != 0) goto L_0x0056;
    L_0x002c:
        r8 = r7.addrEntry;
        r8 = r8.length;
        r3 = r8 + -1;
        r8 = r7.addrEntry;
        r8 = r8[r3];
        r8 = r8.hostName;
        r9 = r14.hashCode();
        r8.srcHashCode = r9;
        r8 = r7.addrEntry;
        r0 = r8[r3];
    L_0x0041:
        return r0;
    L_0x0042:
        r2 = move-exception;
        r8 = log;
        r8 = jcifs.util.LogStream.level;
        if (r8 <= r9) goto L_0x004e;
    L_0x0049:
        r8 = log;
        r2.printStackTrace(r8);
    L_0x004e:
        r8 = new java.net.UnknownHostException;
        r9 = r13.name;
        r8.<init>(r9);
        throw r8;
    L_0x0056:
        r4 = r4 + -1;
        if (r4 <= 0) goto L_0x005e;
    L_0x005a:
        r8 = r6.isBroadcast;
        if (r8 != 0) goto L_0x001f;
    L_0x005e:
        r8 = new java.net.UnknownHostException;
        r9 = r13.name;
        r8.<init>(r9);
        throw r8;
    L_0x0066:
        r1 = 0;
    L_0x0067:
        r8 = r12.resolveOrder;
        r8 = r8.length;
        if (r1 >= r8) goto L_0x00ee;
    L_0x006c:
        r8 = r12.resolveOrder;	 Catch:{ IOException -> 0x0082 }
        r8 = r8[r1];	 Catch:{ IOException -> 0x0082 }
        switch(r8) {
            case 1: goto L_0x0076;
            case 2: goto L_0x0084;
            case 3: goto L_0x0084;
            default: goto L_0x0073;
        };	 Catch:{ IOException -> 0x0082 }
    L_0x0073:
        r1 = r1 + 1;
        goto L_0x0067;
    L_0x0076:
        r0 = jcifs.netbios.Lmhosts.getByName(r13);	 Catch:{ IOException -> 0x0082 }
        if (r0 == 0) goto L_0x0073;
    L_0x007c:
        r8 = r0.hostName;	 Catch:{ IOException -> 0x0082 }
        r10 = 0;
        r8.srcHashCode = r10;	 Catch:{ IOException -> 0x0082 }
        goto L_0x0041;
    L_0x0082:
        r8 = move-exception;
        goto L_0x0073;
    L_0x0084:
        r8 = r12.resolveOrder;	 Catch:{ IOException -> 0x0082 }
        r8 = r8[r1];	 Catch:{ IOException -> 0x0082 }
        if (r8 != r11) goto L_0x00ca;
    L_0x008a:
        r8 = r13.name;	 Catch:{ IOException -> 0x0082 }
        r10 = "\u0001\u0002__MSBROWSE__\u0002";
        if (r8 == r10) goto L_0x00ca;
    L_0x0091:
        r8 = r13.hexCode;	 Catch:{ IOException -> 0x0082 }
        r10 = 29;
        if (r8 == r10) goto L_0x00ca;
    L_0x0097:
        r8 = jcifs.netbios.NbtAddress.getWINSAddress();	 Catch:{ IOException -> 0x0082 }
        r6.addr = r8;	 Catch:{ IOException -> 0x0082 }
        r8 = 0;
        r6.isBroadcast = r8;	 Catch:{ IOException -> 0x0082 }
    L_0x00a0:
        r4 = RETRY_COUNT;	 Catch:{ IOException -> 0x0082 }
        r5 = r4;
    L_0x00a3:
        r4 = r5 + -1;
        if (r5 <= 0) goto L_0x0073;
    L_0x00a7:
        r8 = RETRY_TIMEOUT;	 Catch:{ IOException -> 0x00d2 }
        r12.send(r6, r7, r8);	 Catch:{ IOException -> 0x00d2 }
        r8 = r7.received;	 Catch:{ IOException -> 0x0082 }
        if (r8 == 0) goto L_0x00e6;
    L_0x00b0:
        r8 = r7.resultCode;	 Catch:{ IOException -> 0x0082 }
        if (r8 != 0) goto L_0x00e6;
    L_0x00b4:
        r8 = r7.addrEntry;	 Catch:{ IOException -> 0x0082 }
        r10 = 0;
        r8 = r8[r10];	 Catch:{ IOException -> 0x0082 }
        r8 = r8.hostName;	 Catch:{ IOException -> 0x0082 }
        r10 = r6.addr;	 Catch:{ IOException -> 0x0082 }
        r10 = r10.hashCode();	 Catch:{ IOException -> 0x0082 }
        r8.srcHashCode = r10;	 Catch:{ IOException -> 0x0082 }
        r8 = r7.addrEntry;	 Catch:{ IOException -> 0x0082 }
        r10 = 0;
        r0 = r8[r10];	 Catch:{ IOException -> 0x0082 }
        goto L_0x0041;
    L_0x00ca:
        r8 = r12.baddr;	 Catch:{ IOException -> 0x0082 }
        r6.addr = r8;	 Catch:{ IOException -> 0x0082 }
        r8 = 1;
        r6.isBroadcast = r8;	 Catch:{ IOException -> 0x0082 }
        goto L_0x00a0;
    L_0x00d2:
        r2 = move-exception;
        r8 = log;	 Catch:{ IOException -> 0x0082 }
        r8 = jcifs.util.LogStream.level;	 Catch:{ IOException -> 0x0082 }
        if (r8 <= r9) goto L_0x00de;
    L_0x00d9:
        r8 = log;	 Catch:{ IOException -> 0x0082 }
        r2.printStackTrace(r8);	 Catch:{ IOException -> 0x0082 }
    L_0x00de:
        r8 = new java.net.UnknownHostException;	 Catch:{ IOException -> 0x0082 }
        r10 = r13.name;	 Catch:{ IOException -> 0x0082 }
        r8.<init>(r10);	 Catch:{ IOException -> 0x0082 }
        throw r8;	 Catch:{ IOException -> 0x0082 }
    L_0x00e6:
        r8 = r12.resolveOrder;	 Catch:{ IOException -> 0x0082 }
        r8 = r8[r1];	 Catch:{ IOException -> 0x0082 }
        if (r8 == r11) goto L_0x0073;
    L_0x00ec:
        r5 = r4;
        goto L_0x00a3;
    L_0x00ee:
        r8 = new java.net.UnknownHostException;
        r9 = r13.name;
        r8.<init>(r9);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.netbios.NameServiceClient.getByName(jcifs.netbios.Name, java.net.InetAddress):jcifs.netbios.NbtAddress");
    }

    NbtAddress[] getNodeStatus(NbtAddress addr) throws UnknownHostException {
        NodeStatusResponse response = new NodeStatusResponse(addr);
        NodeStatusRequest request = new NodeStatusRequest(new Name("*\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000", 0, null));
        request.addr = addr.getInetAddress();
        int n = RETRY_COUNT;
        while (true) {
            int n2 = n - 1;
            if (n > 0) {
                try {
                    send(request, response, RETRY_TIMEOUT);
                    if (response.received && response.resultCode == 0) {
                        break;
                    }
                    n = n2;
                } catch (IOException ioe) {
                    LogStream logStream = log;
                    if (LogStream.level > 1) {
                        ioe.printStackTrace(log);
                    }
                    throw new UnknownHostException(addr.toString());
                }
            }
            throw new UnknownHostException(addr.hostName.name);
        }
        int srcHashCode = request.addr.hashCode();
        for (NbtAddress nbtAddress : response.addressArray) {
            nbtAddress.hostName.srcHashCode = srcHashCode;
        }
        return response.addressArray;
    }
}
