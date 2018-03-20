package jcifs.smb;

import jcifs.util.LogStream;

class SmbTree {
    private static int tree_conn_counter;
    int connectionState;
    boolean inDfs;
    boolean inDomainDfs;
    String service = "?????";
    String service0;
    SmbSession session;
    String share;
    int tid;
    int tree_num;

    SmbTree(SmbSession session, String share, String service) {
        this.session = session;
        this.share = share.toUpperCase();
        if (!(service == null || service.startsWith("??"))) {
            this.service = service;
        }
        this.service0 = this.service;
        this.connectionState = 0;
    }

    boolean matches(String share, String service) {
        return this.share.equalsIgnoreCase(share) && (service == null || service.startsWith("??") || this.service.equalsIgnoreCase(service));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SmbTree)) {
            return false;
        }
        SmbTree tree = (SmbTree) obj;
        return matches(tree.share, tree.service);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void send(jcifs.smb.ServerMessageBlock r7, jcifs.smb.ServerMessageBlock r8) throws jcifs.smb.SmbException {
        /*
        r6 = this;
        r2 = r6.session;
        r3 = r2.transport();
        monitor-enter(r3);
        if (r8 == 0) goto L_0x000c;
    L_0x0009:
        r2 = 0;
        r8.received = r2;	 Catch:{ all -> 0x0050 }
    L_0x000c:
        r6.treeConnect(r7, r8);	 Catch:{ all -> 0x0050 }
        if (r7 == 0) goto L_0x0017;
    L_0x0011:
        if (r8 == 0) goto L_0x0019;
    L_0x0013:
        r2 = r8.received;	 Catch:{ all -> 0x0050 }
        if (r2 == 0) goto L_0x0019;
    L_0x0017:
        monitor-exit(r3);	 Catch:{ all -> 0x0050 }
    L_0x0018:
        return;
    L_0x0019:
        r2 = r6.service;	 Catch:{ all -> 0x0050 }
        r4 = "A:";
        r2 = r2.equals(r4);	 Catch:{ all -> 0x0050 }
        if (r2 != 0) goto L_0x0081;
    L_0x0024:
        r2 = r7.command;	 Catch:{ all -> 0x0050 }
        switch(r2) {
            case -94: goto L_0x0081;
            case 4: goto L_0x0081;
            case 37: goto L_0x0053;
            case 45: goto L_0x0081;
            case 46: goto L_0x0081;
            case 47: goto L_0x0081;
            case 50: goto L_0x0053;
            case 113: goto L_0x0081;
            default: goto L_0x0029;
        };	 Catch:{ all -> 0x0050 }
    L_0x0029:
        r2 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x0050 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0050 }
        r4.<init>();	 Catch:{ all -> 0x0050 }
        r5 = "Invalid operation for ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r5 = r6.service;	 Catch:{ all -> 0x0050 }
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r5 = " service";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r4 = r4.append(r7);	 Catch:{ all -> 0x0050 }
        r4 = r4.toString();	 Catch:{ all -> 0x0050 }
        r2.<init>(r4);	 Catch:{ all -> 0x0050 }
        throw r2;	 Catch:{ all -> 0x0050 }
    L_0x0050:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0050 }
        throw r2;
    L_0x0053:
        r0 = r7;
        r0 = (jcifs.smb.SmbComTransaction) r0;	 Catch:{ all -> 0x0050 }
        r2 = r0;
        r2 = r2.subCommand;	 Catch:{ all -> 0x0050 }
        r2 = r2 & 255;
        switch(r2) {
            case 0: goto L_0x0081;
            case 16: goto L_0x0081;
            case 35: goto L_0x0081;
            case 38: goto L_0x0081;
            case 83: goto L_0x0081;
            case 84: goto L_0x0081;
            case 104: goto L_0x0081;
            case 215: goto L_0x0081;
            default: goto L_0x005e;
        };	 Catch:{ all -> 0x0050 }
    L_0x005e:
        r2 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x0050 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0050 }
        r4.<init>();	 Catch:{ all -> 0x0050 }
        r5 = "Invalid operation for ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r5 = r6.service;	 Catch:{ all -> 0x0050 }
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r5 = " service";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0050 }
        r4 = r4.toString();	 Catch:{ all -> 0x0050 }
        r2.<init>(r4);	 Catch:{ all -> 0x0050 }
        throw r2;	 Catch:{ all -> 0x0050 }
    L_0x0081:
        r2 = r6.tid;	 Catch:{ all -> 0x0050 }
        r7.tid = r2;	 Catch:{ all -> 0x0050 }
        r2 = r6.inDfs;	 Catch:{ all -> 0x0050 }
        if (r2 == 0) goto L_0x00d3;
    L_0x0089:
        r2 = r6.service;	 Catch:{ all -> 0x0050 }
        r4 = "IPC";
        r2 = r2.equals(r4);	 Catch:{ all -> 0x0050 }
        if (r2 != 0) goto L_0x00d3;
    L_0x0094:
        r2 = r7.path;	 Catch:{ all -> 0x0050 }
        if (r2 == 0) goto L_0x00d3;
    L_0x0098:
        r2 = r7.path;	 Catch:{ all -> 0x0050 }
        r2 = r2.length();	 Catch:{ all -> 0x0050 }
        if (r2 <= 0) goto L_0x00d3;
    L_0x00a0:
        r2 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r7.flags2 = r2;	 Catch:{ all -> 0x0050 }
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0050 }
        r2.<init>();	 Catch:{ all -> 0x0050 }
        r4 = 92;
        r2 = r2.append(r4);	 Catch:{ all -> 0x0050 }
        r4 = r6.session;	 Catch:{ all -> 0x0050 }
        r4 = r4.transport();	 Catch:{ all -> 0x0050 }
        r4 = r4.tconHostName;	 Catch:{ all -> 0x0050 }
        r2 = r2.append(r4);	 Catch:{ all -> 0x0050 }
        r4 = 92;
        r2 = r2.append(r4);	 Catch:{ all -> 0x0050 }
        r4 = r6.share;	 Catch:{ all -> 0x0050 }
        r2 = r2.append(r4);	 Catch:{ all -> 0x0050 }
        r4 = r7.path;	 Catch:{ all -> 0x0050 }
        r2 = r2.append(r4);	 Catch:{ all -> 0x0050 }
        r2 = r2.toString();	 Catch:{ all -> 0x0050 }
        r7.path = r2;	 Catch:{ all -> 0x0050 }
    L_0x00d3:
        r2 = r6.session;	 Catch:{ SmbException -> 0x00db }
        r2.send(r7, r8);	 Catch:{ SmbException -> 0x00db }
        monitor-exit(r3);	 Catch:{ all -> 0x0050 }
        goto L_0x0018;
    L_0x00db:
        r1 = move-exception;
        r2 = r1.getNtStatus();	 Catch:{ all -> 0x0050 }
        r4 = -1073741623; // 0xffffffffc00000c9 float:-2.000048 double:NaN;
        if (r2 != r4) goto L_0x00e9;
    L_0x00e5:
        r2 = 1;
        r6.treeDisconnect(r2);	 Catch:{ all -> 0x0050 }
    L_0x00e9:
        throw r1;	 Catch:{ all -> 0x0050 }
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SmbTree.send(jcifs.smb.ServerMessageBlock, jcifs.smb.ServerMessageBlock):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void treeConnect(jcifs.smb.ServerMessageBlock r10, jcifs.smb.ServerMessageBlock r11) throws jcifs.smb.SmbException {
        /*
        r9 = this;
        r8 = 2;
        r5 = r9.session;
        r6 = r5.transport();
        monitor-enter(r6);
    L_0x0008:
        r5 = r9.connectionState;	 Catch:{ all -> 0x002a }
        if (r5 == 0) goto L_0x002d;
    L_0x000c:
        r5 = r9.connectionState;	 Catch:{ all -> 0x002a }
        if (r5 == r8) goto L_0x0015;
    L_0x0010:
        r5 = r9.connectionState;	 Catch:{ all -> 0x002a }
        r7 = 3;
        if (r5 != r7) goto L_0x0017;
    L_0x0015:
        monitor-exit(r6);	 Catch:{ all -> 0x002a }
    L_0x0016:
        return;
    L_0x0017:
        r5 = r9.session;	 Catch:{ InterruptedException -> 0x001f }
        r5 = r5.transport;	 Catch:{ InterruptedException -> 0x001f }
        r5.wait();	 Catch:{ InterruptedException -> 0x001f }
        goto L_0x0008;
    L_0x001f:
        r0 = move-exception;
        r5 = new jcifs.smb.SmbException;	 Catch:{ all -> 0x002a }
        r7 = r0.getMessage();	 Catch:{ all -> 0x002a }
        r5.<init>(r7, r0);	 Catch:{ all -> 0x002a }
        throw r5;	 Catch:{ all -> 0x002a }
    L_0x002a:
        r5 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x002a }
        throw r5;
    L_0x002d:
        r5 = 1;
        r9.connectionState = r5;	 Catch:{ all -> 0x002a }
        r5 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r5.transport;	 Catch:{ SmbException -> 0x00c3 }
        r5.connect();	 Catch:{ SmbException -> 0x00c3 }
        r5 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x00c3 }
        r5.<init>();	 Catch:{ SmbException -> 0x00c3 }
        r7 = "\\\\";
        r5 = r5.append(r7);	 Catch:{ SmbException -> 0x00c3 }
        r7 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r7 = r7.transport;	 Catch:{ SmbException -> 0x00c3 }
        r7 = r7.tconHostName;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r5.append(r7);	 Catch:{ SmbException -> 0x00c3 }
        r7 = 92;
        r5 = r5.append(r7);	 Catch:{ SmbException -> 0x00c3 }
        r7 = r9.share;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r5.append(r7);	 Catch:{ SmbException -> 0x00c3 }
        r4 = r5.toString();	 Catch:{ SmbException -> 0x00c3 }
        r5 = r9.service0;	 Catch:{ SmbException -> 0x00c3 }
        r9.service = r5;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r5.transport;	 Catch:{ SmbException -> 0x00c3 }
        r5 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x00c3 }
        r5 = jcifs.util.LogStream.level;	 Catch:{ SmbException -> 0x00c3 }
        r7 = 4;
        if (r5 < r7) goto L_0x0096;
    L_0x006c:
        r5 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r5.transport;	 Catch:{ SmbException -> 0x00c3 }
        r5 = jcifs.smb.SmbTransport.log;	 Catch:{ SmbException -> 0x00c3 }
        r7 = new java.lang.StringBuilder;	 Catch:{ SmbException -> 0x00c3 }
        r7.<init>();	 Catch:{ SmbException -> 0x00c3 }
        r8 = "treeConnect: unc=";
        r7 = r7.append(r8);	 Catch:{ SmbException -> 0x00c3 }
        r7 = r7.append(r4);	 Catch:{ SmbException -> 0x00c3 }
        r8 = ",service=";
        r7 = r7.append(r8);	 Catch:{ SmbException -> 0x00c3 }
        r8 = r9.service;	 Catch:{ SmbException -> 0x00c3 }
        r7 = r7.append(r8);	 Catch:{ SmbException -> 0x00c3 }
        r7 = r7.toString();	 Catch:{ SmbException -> 0x00c3 }
        r5.println(r7);	 Catch:{ SmbException -> 0x00c3 }
    L_0x0096:
        r2 = new jcifs.smb.SmbComTreeConnectAndXResponse;	 Catch:{ SmbException -> 0x00c3 }
        r2.<init>(r11);	 Catch:{ SmbException -> 0x00c3 }
        r1 = new jcifs.smb.SmbComTreeConnectAndX;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r7 = r9.service;	 Catch:{ SmbException -> 0x00c3 }
        r1.<init>(r5, r4, r7, r10);	 Catch:{ SmbException -> 0x00c3 }
        r5 = r9.session;	 Catch:{ SmbException -> 0x00c3 }
        r5.send(r1, r2);	 Catch:{ SmbException -> 0x00c3 }
        r5 = r2.tid;	 Catch:{ SmbException -> 0x00c3 }
        r9.tid = r5;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r2.service;	 Catch:{ SmbException -> 0x00c3 }
        r9.service = r5;	 Catch:{ SmbException -> 0x00c3 }
        r5 = r2.shareIsInDfs;	 Catch:{ SmbException -> 0x00c3 }
        r9.inDfs = r5;	 Catch:{ SmbException -> 0x00c3 }
        r5 = tree_conn_counter;	 Catch:{ SmbException -> 0x00c3 }
        r7 = r5 + 1;
        tree_conn_counter = r7;	 Catch:{ SmbException -> 0x00c3 }
        r9.tree_num = r5;	 Catch:{ SmbException -> 0x00c3 }
        r5 = 2;
        r9.connectionState = r5;	 Catch:{ SmbException -> 0x00c3 }
        monitor-exit(r6);	 Catch:{ all -> 0x002a }
        goto L_0x0016;
    L_0x00c3:
        r3 = move-exception;
        r5 = 1;
        r9.treeDisconnect(r5);	 Catch:{ all -> 0x002a }
        r5 = 0;
        r9.connectionState = r5;	 Catch:{ all -> 0x002a }
        throw r3;	 Catch:{ all -> 0x002a }
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.smb.SmbTree.treeConnect(jcifs.smb.ServerMessageBlock, jcifs.smb.ServerMessageBlock):void");
    }

    void treeDisconnect(boolean inError) {
        SmbTransport smbTransport;
        synchronized (this.session.transport()) {
            if (this.connectionState != 2) {
                return;
            }
            this.connectionState = 3;
            if (!(inError || this.tid == 0)) {
                try {
                    send(new SmbComTreeDisconnect(), null);
                } catch (SmbException se) {
                    smbTransport = this.session.transport;
                    LogStream logStream = SmbTransport.log;
                    if (LogStream.level > 1) {
                        smbTransport = this.session.transport;
                        se.printStackTrace(SmbTransport.log);
                    }
                }
            }
            this.inDfs = false;
            this.inDomainDfs = false;
            this.connectionState = 0;
            this.session.transport.notifyAll();
        }
    }

    public String toString() {
        return "SmbTree[share=" + this.share + ",service=" + this.service + ",tid=" + this.tid + ",inDfs=" + this.inDfs + ",inDomainDfs=" + this.inDomainDfs + ",connectionState=" + this.connectionState + "]";
    }
}
