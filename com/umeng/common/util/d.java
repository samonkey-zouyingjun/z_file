package com.umeng.common.util;

/* compiled from: BaseNCodec */
public abstract class d {
    private static final int a = 2;
    public static final int b = 76;
    public static final int c = 64;
    protected static final int d = 255;
    protected static final byte e = (byte) 61;
    private static final int m = 8192;
    protected final byte f = e;
    protected final int g;
    protected byte[] h;
    protected int i;
    protected boolean j;
    protected int k;
    protected int l;
    private final int n;
    private final int o;
    private final int p;
    private int q;

    abstract void a(byte[] bArr, int i, int i2);

    abstract void b(byte[] bArr, int i, int i2);

    protected abstract boolean b(byte b);

    protected d(int i, int i2, int i3, int i4) {
        this.n = i;
        this.o = i2;
        int i5 = (i3 <= 0 || i4 <= 0) ? 0 : (i3 / i2) * i2;
        this.g = i5;
        this.p = i4;
    }

    boolean b() {
        return this.h != null;
    }

    int c() {
        return this.h != null ? this.i - this.q : 0;
    }

    protected int d() {
        return 8192;
    }

    private void a() {
        if (this.h == null) {
            this.h = new byte[d()];
            this.i = 0;
            this.q = 0;
            return;
        }
        Object obj = new byte[(this.h.length * 2)];
        System.arraycopy(this.h, 0, obj, 0, this.h.length);
        this.h = obj;
    }

    protected void a(int i) {
        if (this.h == null || this.h.length < this.i + i) {
            a();
        }
    }

    int c(byte[] bArr, int i, int i2) {
        if (this.h == null) {
            return this.j ? -1 : 0;
        } else {
            int min = Math.min(c(), i2);
            System.arraycopy(this.h, this.q, bArr, i, min);
            this.q += min;
            if (this.q < this.i) {
                return min;
            }
            this.h = null;
            return min;
        }
    }

    protected static boolean c(byte b) {
        switch (b) {
            case (byte) 9:
            case (byte) 10:
            case (byte) 13:
            case (byte) 32:
                return true;
            default:
                return false;
        }
    }

    private void e() {
        this.h = null;
        this.i = 0;
        this.q = 0;
        this.k = 0;
        this.l = 0;
        this.j = false;
    }

    public Object a(Object obj) throws Exception {
        if (obj instanceof byte[]) {
            return l((byte[]) obj);
        }
        throw new Exception("Parameter supplied to Base-N encode is not a byte[]");
    }

    public String j(byte[] bArr) {
        return a.f(l(bArr));
    }

    public Object b(Object obj) throws Exception {
        if (obj instanceof byte[]) {
            return k((byte[]) obj);
        }
        if (obj instanceof String) {
            return c((String) obj);
        }
        throw new Exception("Parameter supplied to Base-N decode is not a byte[] or a String");
    }

    public byte[] c(String str) {
        return k(a.f(str));
    }

    public byte[] k(byte[] bArr) {
        e();
        if (bArr == null || bArr.length == 0) {
            return bArr;
        }
        b(bArr, 0, bArr.length);
        b(bArr, 0, -1);
        bArr = new byte[this.i];
        c(bArr, 0, bArr.length);
        return bArr;
    }

    public byte[] l(byte[] bArr) {
        e();
        if (bArr == null || bArr.length == 0) {
            return bArr;
        }
        a(bArr, 0, bArr.length);
        a(bArr, 0, -1);
        bArr = new byte[(this.i - this.q)];
        c(bArr, 0, bArr.length);
        return bArr;
    }

    public String m(byte[] bArr) {
        return a.f(l(bArr));
    }

    public boolean b(byte[] bArr, boolean z) {
        int i = 0;
        while (i < bArr.length) {
            if (!b(bArr[i])) {
                if (!z) {
                    return false;
                }
                if (!(bArr[i] == e || c(bArr[i]))) {
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    public boolean d(String str) {
        return b(a.f(str), true);
    }

    protected boolean n(byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        int i = 0;
        while (i < bArr.length) {
            if (e == bArr[i] || b(bArr[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    public long o(byte[] bArr) {
        long length = ((long) (((bArr.length + this.n) - 1) / this.n)) * ((long) this.o);
        if (this.g > 0) {
            return length + ((((((long) this.g) + length) - 1) / ((long) this.g)) * ((long) this.p));
        }
        return length;
    }
}
