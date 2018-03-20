package com.zidoo.custom.down;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public final class ZidooBufferedRandomAccessFile extends RandomAccessFile {
    static final long BuffMask_ = -65536;
    public static final int BuffSz_ = 65536;
    static final int LogBuffSz_ = 16;
    private byte[] buff_;
    private long curr_;
    private boolean dirty_;
    private long diskPos_;
    private long hi_;
    private boolean hitEOF_;
    private long lo_;
    private long maxHi_;
    private String path_;
    private boolean syncNeeded_;

    public ZidooBufferedRandomAccessFile(File file, String mode) throws IOException {
        this(file, mode, 0);
    }

    public ZidooBufferedRandomAccessFile(File file, String mode, int size) throws IOException {
        super(file, mode);
        this.path_ = file.getAbsolutePath();
        init(size);
    }

    public ZidooBufferedRandomAccessFile(String name, String mode) throws IOException {
        this(name, mode, 0);
    }

    public ZidooBufferedRandomAccessFile(String name, String mode, int size) throws FileNotFoundException {
        super(name, mode);
        this.path_ = name;
        init(size);
    }

    private void init(int size) {
        this.dirty_ = false;
        this.hi_ = 0;
        this.curr_ = 0;
        this.lo_ = 0;
        this.buff_ = size > 65536 ? new byte[size] : new byte[65536];
        this.maxHi_ = PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH;
        this.hitEOF_ = false;
        this.diskPos_ = 0;
    }

    public String getPath() {
        return this.path_;
    }

    public void sync() throws IOException {
        if (this.syncNeeded_) {
            flush();
            getChannel().force(true);
            this.syncNeeded_ = false;
        }
    }

    public void close() throws IOException {
        flush();
        this.buff_ = null;
        super.close();
    }

    public void flush() throws IOException {
        flushBuffer();
    }

    private void flushBuffer() throws IOException {
        if (this.dirty_) {
            if (this.diskPos_ != this.lo_) {
                super.seek(this.lo_);
            }
            super.write(this.buff_, 0, (int) (this.curr_ - this.lo_));
            this.diskPos_ = this.curr_;
            this.dirty_ = false;
        }
    }

    private int fillBuffer() throws IOException {
        int cnt = 0;
        int rem = this.buff_.length;
        while (rem > 0) {
            int n = super.read(this.buff_, cnt, rem);
            if (n < 0) {
                break;
            }
            cnt += n;
            rem -= n;
        }
        if (cnt < 0) {
            boolean z = cnt < this.buff_.length;
            this.hitEOF_ = z;
            if (z) {
                Arrays.fill(this.buff_, cnt, this.buff_.length, (byte) -1);
            }
        }
        this.diskPos_ += (long) cnt;
        return cnt;
    }

    public void seek(long pos) throws IOException {
        if (pos >= this.hi_ || pos < this.lo_) {
            flushBuffer();
            this.lo_ = BuffMask_ & pos;
            this.maxHi_ = this.lo_ + ((long) this.buff_.length);
            if (this.diskPos_ != this.lo_) {
                super.seek(this.lo_);
                this.diskPos_ = this.lo_;
            }
            this.hi_ = this.lo_ + ((long) fillBuffer());
        } else if (pos < this.curr_) {
            flushBuffer();
        }
        this.curr_ = pos;
    }

    public long getFilePointer() {
        return this.curr_;
    }

    public long length() throws IOException {
        return Math.max(this.curr_, super.length());
    }

    public int read() throws IOException {
        if (this.curr_ >= this.hi_) {
            if (this.hitEOF_) {
                return -1;
            }
            seek(this.curr_);
            if (this.curr_ == this.hi_) {
                return -1;
            }
        }
        byte res = this.buff_[(int) (this.curr_ - this.lo_)];
        this.curr_++;
        return res & 255;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.curr_ >= this.hi_) {
            if (this.hitEOF_) {
                return -1;
            }
            seek(this.curr_);
            if (this.curr_ == this.hi_) {
                return -1;
            }
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        System.arraycopy(this.buff_, (int) (this.curr_ - this.lo_), b, off, len);
        this.curr_ += (long) len;
        return len;
    }

    public void write(int b) throws IOException {
        if (this.curr_ >= this.hi_) {
            if (!this.hitEOF_ || this.hi_ >= this.maxHi_) {
                seek(this.curr_);
                if (this.curr_ == this.hi_) {
                    this.hi_++;
                }
            } else {
                this.hi_++;
            }
        }
        this.buff_[(int) (this.curr_ - this.lo_)] = (byte) b;
        this.curr_++;
        this.dirty_ = true;
        this.syncNeeded_ = true;
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int n = writeAtMost(b, off, len);
            off += n;
            len -= n;
            this.dirty_ = true;
            this.syncNeeded_ = true;
        }
    }

    private int writeAtMost(byte[] b, int off, int len) throws IOException {
        if (this.curr_ >= this.hi_) {
            if (!this.hitEOF_ || this.hi_ >= this.maxHi_) {
                seek(this.curr_);
                if (this.curr_ == this.hi_) {
                    this.hi_ = this.maxHi_;
                }
            } else {
                this.hi_ = this.maxHi_;
            }
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        System.arraycopy(b, off, this.buff_, (int) (this.curr_ - this.lo_), len);
        this.curr_ += (long) len;
        return len;
    }
}
