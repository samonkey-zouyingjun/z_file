package jcifs.smb;

import java.security.MessageDigest;
import jcifs.util.Hexdump;
import jcifs.util.LogStream;

public class SigningDigest implements SmbConstants {
    static LogStream log = LogStream.getInstance();
    private boolean bypass = false;
    private MessageDigest digest;
    private byte[] macSigningKey;
    private int signSequence;
    private int updates;

    public SigningDigest(byte[] macSigningKey, boolean bypass) throws SmbException {
        LogStream logStream;
        try {
            this.digest = MessageDigest.getInstance("MD5");
            this.macSigningKey = macSigningKey;
            this.bypass = bypass;
            this.updates = 0;
            this.signSequence = 0;
            logStream = log;
            if (LogStream.level >= 5) {
                log.println("macSigningKey:");
                Hexdump.hexdump(log, macSigningKey, 0, macSigningKey.length);
            }
        } catch (Throwable ex) {
            logStream = log;
            if (LogStream.level > 0) {
                ex.printStackTrace(log);
            }
            throw new SmbException("MD5", ex);
        }
    }

    public SigningDigest(SmbTransport transport, NtlmPasswordAuthentication auth) throws SmbException {
        LogStream logStream;
        try {
            this.digest = MessageDigest.getInstance("MD5");
            try {
                switch (LM_COMPATIBILITY) {
                    case 0:
                    case 1:
                    case 2:
                        this.macSigningKey = new byte[40];
                        auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                        System.arraycopy(auth.getUnicodeHash(transport.server.encryptionKey), 0, this.macSigningKey, 16, 24);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        this.macSigningKey = new byte[16];
                        auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                        break;
                    default:
                        this.macSigningKey = new byte[40];
                        auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                        System.arraycopy(auth.getUnicodeHash(transport.server.encryptionKey), 0, this.macSigningKey, 16, 24);
                        break;
                }
                logStream = log;
                if (LogStream.level >= 5) {
                    log.println("LM_COMPATIBILITY=" + LM_COMPATIBILITY);
                    Hexdump.hexdump(log, this.macSigningKey, 0, this.macSigningKey.length);
                }
            } catch (Throwable ex) {
                throw new SmbException("", ex);
            }
        } catch (Throwable ex2) {
            logStream = log;
            if (LogStream.level > 0) {
                ex2.printStackTrace(log);
            }
            throw new SmbException("MD5", ex2);
        }
    }

    public void update(byte[] input, int offset, int len) {
        LogStream logStream = log;
        if (LogStream.level >= 5) {
            log.println("update: " + this.updates + " " + offset + ":" + len);
            Hexdump.hexdump(log, input, offset, Math.min(len, 256));
            log.flush();
        }
        if (len != 0) {
            this.digest.update(input, offset, len);
            this.updates++;
        }
    }

    public byte[] digest() {
        byte[] b = this.digest.digest();
        LogStream logStream = log;
        if (LogStream.level >= 5) {
            log.println("digest: ");
            Hexdump.hexdump(log, b, 0, b.length);
            log.flush();
        }
        this.updates = 0;
        return b;
    }

    void sign(byte[] data, int offset, int length, ServerMessageBlock request, ServerMessageBlock response) {
        int i;
        request.signSeq = this.signSequence;
        if (response != null) {
            response.signSeq = this.signSequence + 1;
            response.verifyFailed = false;
        }
        try {
            update(this.macSigningKey, 0, this.macSigningKey.length);
            int index = offset + 14;
            for (int i2 = 0; i2 < 8; i2++) {
                data[index + i2] = (byte) 0;
            }
            ServerMessageBlock.writeInt4((long) this.signSequence, data, index);
            update(data, offset, length);
            System.arraycopy(digest(), 0, data, index, 8);
            if (this.bypass) {
                this.bypass = false;
                System.arraycopy("BSRSPYL ".getBytes(), 0, data, index, 8);
            }
            i = this.signSequence;
        } catch (Exception ex) {
            LogStream logStream = log;
            if (LogStream.level > 0) {
                ex.printStackTrace(log);
            }
            i = this.signSequence;
        } catch (Throwable th) {
            this.signSequence += 2;
        }
        this.signSequence = i + 2;
    }

    boolean verify(byte[] data, int offset, ServerMessageBlock response) {
        update(this.macSigningKey, 0, this.macSigningKey.length);
        int index = offset;
        update(data, index, 14);
        index += 14;
        byte[] sequence = new byte[8];
        ServerMessageBlock.writeInt4((long) response.signSeq, sequence, 0);
        update(sequence, 0, sequence.length);
        index += 8;
        if (response.command == (byte) 46) {
            SmbComReadAndXResponse raxr = (SmbComReadAndXResponse) response;
            update(data, index, ((response.length - raxr.dataLength) - 14) - 8);
            update(raxr.b, raxr.off, raxr.dataLength);
        } else {
            update(data, index, (response.length - 14) - 8);
        }
        byte[] signature = digest();
        for (int i = 0; i < 8; i++) {
            if (signature[i] != data[(offset + 14) + i]) {
                LogStream logStream = log;
                if (LogStream.level >= 2) {
                    log.println("signature verification failure");
                    Hexdump.hexdump(log, signature, 0, 8);
                    Hexdump.hexdump(log, data, offset + 14, 8);
                }
                response.verifyFailed = true;
                return true;
            }
        }
        response.verifyFailed = false;
        return false;
    }

    public String toString() {
        return "LM_COMPATIBILITY=" + LM_COMPATIBILITY + " MacSigningKey=" + Hexdump.toHexString(this.macSigningKey, 0, this.macSigningKey.length);
    }
}
