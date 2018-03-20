package jcifs.util.transport;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import jcifs.util.LogStream;

public abstract class Transport implements Runnable {
    static int id = 0;
    static LogStream log = LogStream.getInstance();
    String name;
    protected HashMap response_map;
    int state = 0;
    TransportException te;
    Thread thread;

    protected abstract void doConnect() throws Exception;

    protected abstract void doDisconnect(boolean z) throws IOException;

    protected abstract void doRecv(Response response) throws IOException;

    protected abstract void doSend(Request request) throws IOException;

    protected abstract void doSkip() throws IOException;

    protected abstract void makeKey(Request request) throws IOException;

    protected abstract Request peekKey() throws IOException;

    public Transport() {
        StringBuilder append = new StringBuilder().append("Transport");
        int i = id;
        id = i + 1;
        this.name = append.append(i).toString();
        this.response_map = new HashMap(4);
    }

    public static int readn(InputStream in, byte[] b, int off, int len) throws IOException {
        int i = 0;
        while (i < len) {
            int n = in.read(b, off + i, len - i);
            if (n <= 0) {
                break;
            }
            i += n;
        }
        return i;
    }

    public synchronized void sendrecv(Request request, Response response, long timeout) throws IOException {
        makeKey(request);
        response.isReceived = false;
        try {
            this.response_map.put(request, response);
            doSend(request);
            response.expiration = System.currentTimeMillis() + timeout;
            while (!response.isReceived) {
                wait(timeout);
                timeout = response.expiration - System.currentTimeMillis();
                if (timeout <= 0) {
                    throw new TransportException(this.name + " timedout waiting for response to " + request);
                }
            }
            this.response_map.remove(request);
        } catch (IOException ioe) {
            LogStream logStream = log;
            if (LogStream.level > 2) {
                ioe.printStackTrace(log);
            }
            try {
                disconnect(true);
            } catch (IOException ioe2) {
                ioe2.printStackTrace(log);
            }
            throw ioe;
        } catch (Throwable ie) {
            throw new TransportException(ie);
        } catch (Throwable th) {
            this.response_map.remove(request);
        }
    }

    private void loop() {
        boolean timeout;
        boolean hard;
        while (this.thread == Thread.currentThread()) {
            LogStream logStream;
            try {
                Request key = peekKey();
                if (key == null) {
                    throw new IOException("end of stream");
                }
                synchronized (this) {
                    Response response = (Response) this.response_map.get(key);
                    if (response == null) {
                        logStream = log;
                        if (LogStream.level >= 4) {
                            log.println("Invalid key, skipping message");
                        }
                        doSkip();
                    } else {
                        doRecv(response);
                        response.isReceived = true;
                        notifyAll();
                    }
                }
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg == null || !msg.equals("Read timed out")) {
                    timeout = false;
                } else {
                    timeout = true;
                }
                if (timeout) {
                    hard = false;
                } else {
                    hard = true;
                }
                if (!timeout) {
                    logStream = log;
                    if (LogStream.level >= 3) {
                        ex.printStackTrace(log);
                    }
                }
                try {
                    disconnect(hard);
                } catch (IOException ioe) {
                    ioe.printStackTrace(log);
                }
            }
        }
    }

    public synchronized void connect(long timeout) throws TransportException {
        try {
            LogStream logStream;
            switch (this.state) {
                case 0:
                    this.state = 1;
                    this.te = null;
                    this.thread = new Thread(this, this.name);
                    this.thread.setDaemon(true);
                    synchronized (this.thread) {
                        this.thread.start();
                        this.thread.wait(timeout);
                        switch (this.state) {
                            case 1:
                                this.state = 0;
                                this.thread = null;
                                throw new TransportException("Connection timeout");
                            case 2:
                                if (this.te == null) {
                                    this.state = 3;
                                    if (!(this.state == 0 || this.state == 3 || this.state == 4)) {
                                        logStream = log;
                                        if (LogStream.level >= 1) {
                                            log.println("Invalid state: " + this.state);
                                        }
                                        this.state = 0;
                                        this.thread = null;
                                        break;
                                    }
                                }
                                this.state = 4;
                                this.thread = null;
                                throw this.te;
                            default:
                                if (!(this.state == 0 || this.state == 3 || this.state == 4)) {
                                    logStream = log;
                                    if (LogStream.level >= 1) {
                                        log.println("Invalid state: " + this.state);
                                    }
                                    this.state = 0;
                                    this.thread = null;
                                    break;
                                }
                        }
                    }
                case 3:
                    if (!(this.state == 0 || this.state == 3 || this.state == 4)) {
                        logStream = log;
                        if (LogStream.level >= 1) {
                            log.println("Invalid state: " + this.state);
                        }
                        this.state = 0;
                        this.thread = null;
                        break;
                    }
                case 4:
                    this.state = 0;
                    throw new TransportException("Connection in error", this.te);
                default:
                    TransportException te = new TransportException("Invalid state: " + this.state);
                    this.state = 0;
                    throw te;
            }
        } catch (Throwable ie) {
            this.state = 0;
            this.thread = null;
            throw new TransportException(ie);
        } catch (Throwable th) {
            if (!(this.state == 0 || this.state == 3 || this.state == 4)) {
                LogStream logStream2 = log;
                if (LogStream.level >= 1) {
                    log.println("Invalid state: " + this.state);
                }
                this.state = 0;
                this.thread = null;
            }
        }
    }

    public synchronized void disconnect(boolean hard) throws IOException {
        IOException ioe = null;
        switch (this.state) {
            case 0:
            case 2:
                hard = true;
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                LogStream logStream = log;
                if (LogStream.level >= 1) {
                    log.println("Invalid state: " + this.state);
                }
                this.thread = null;
                this.state = 0;
                break;
        }
        if (this.response_map.size() == 0 || hard) {
            try {
                doDisconnect(hard);
            } catch (IOException ioe0) {
                ioe = ioe0;
            }
            this.thread = null;
            this.state = 0;
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r6 = this;
        r5 = 2;
        r2 = java.lang.Thread.currentThread();
        r1 = 0;
        r6.doConnect();	 Catch:{ Exception -> 0x001d, all -> 0x005b }
        monitor-enter(r2);
        r3 = r6.thread;	 Catch:{ all -> 0x0084 }
        if (r2 == r3) goto L_0x0047;
    L_0x000e:
        if (r1 == 0) goto L_0x001b;
    L_0x0010:
        r3 = log;	 Catch:{ all -> 0x0084 }
        r3 = jcifs.util.LogStream.level;	 Catch:{ all -> 0x0084 }
        if (r3 < r5) goto L_0x001b;
    L_0x0016:
        r3 = log;	 Catch:{ all -> 0x0084 }
        r1.printStackTrace(r3);	 Catch:{ all -> 0x0084 }
    L_0x001b:
        monitor-exit(r2);	 Catch:{ all -> 0x0084 }
    L_0x001c:
        return;
    L_0x001d:
        r0 = move-exception;
        r1 = r0;
        monitor-enter(r2);
        r3 = r6.thread;	 Catch:{ all -> 0x0033 }
        if (r2 == r3) goto L_0x0073;
    L_0x0024:
        if (r1 == 0) goto L_0x0031;
    L_0x0026:
        r3 = log;	 Catch:{ all -> 0x0033 }
        r3 = jcifs.util.LogStream.level;	 Catch:{ all -> 0x0033 }
        if (r3 < r5) goto L_0x0031;
    L_0x002c:
        r3 = log;	 Catch:{ all -> 0x0033 }
        r1.printStackTrace(r3);	 Catch:{ all -> 0x0033 }
    L_0x0031:
        monitor-exit(r2);	 Catch:{ all -> 0x0033 }
        goto L_0x001c;
    L_0x0033:
        r3 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0033 }
        throw r3;
    L_0x0036:
        if (r1 == 0) goto L_0x003f;
    L_0x0038:
        r4 = new jcifs.util.transport.TransportException;	 Catch:{ all -> 0x0070 }
        r4.<init>(r1);	 Catch:{ all -> 0x0070 }
        r6.te = r4;	 Catch:{ all -> 0x0070 }
    L_0x003f:
        r4 = 2;
        r6.state = r4;	 Catch:{ all -> 0x0070 }
        r2.notify();	 Catch:{ all -> 0x0070 }
        monitor-exit(r2);	 Catch:{ all -> 0x0070 }
        throw r3;
    L_0x0047:
        if (r1 == 0) goto L_0x0050;
    L_0x0049:
        r3 = new jcifs.util.transport.TransportException;	 Catch:{ all -> 0x0084 }
        r3.<init>(r1);	 Catch:{ all -> 0x0084 }
        r6.te = r3;	 Catch:{ all -> 0x0084 }
    L_0x0050:
        r3 = 2;
        r6.state = r3;	 Catch:{ all -> 0x0084 }
        r2.notify();	 Catch:{ all -> 0x0084 }
        monitor-exit(r2);	 Catch:{ all -> 0x0084 }
        r6.loop();
        goto L_0x001c;
    L_0x005b:
        r3 = move-exception;
        monitor-enter(r2);
        r4 = r6.thread;	 Catch:{ all -> 0x0070 }
        if (r2 == r4) goto L_0x0036;
    L_0x0061:
        if (r1 == 0) goto L_0x006e;
    L_0x0063:
        r3 = log;	 Catch:{ all -> 0x0070 }
        r3 = jcifs.util.LogStream.level;	 Catch:{ all -> 0x0070 }
        if (r3 < r5) goto L_0x006e;
    L_0x0069:
        r3 = log;	 Catch:{ all -> 0x0070 }
        r1.printStackTrace(r3);	 Catch:{ all -> 0x0070 }
    L_0x006e:
        monitor-exit(r2);	 Catch:{ all -> 0x0070 }
        goto L_0x001c;
    L_0x0070:
        r3 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0070 }
        throw r3;
    L_0x0073:
        if (r1 == 0) goto L_0x007c;
    L_0x0075:
        r3 = new jcifs.util.transport.TransportException;	 Catch:{ all -> 0x0033 }
        r3.<init>(r1);	 Catch:{ all -> 0x0033 }
        r6.te = r3;	 Catch:{ all -> 0x0033 }
    L_0x007c:
        r3 = 2;
        r6.state = r3;	 Catch:{ all -> 0x0033 }
        r2.notify();	 Catch:{ all -> 0x0033 }
        monitor-exit(r2);	 Catch:{ all -> 0x0033 }
        goto L_0x001c;
    L_0x0084:
        r3 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0084 }
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: jcifs.util.transport.Transport.run():void");
    }

    public String toString() {
        return this.name;
    }
}
