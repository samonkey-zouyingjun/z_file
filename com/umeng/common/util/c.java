package com.umeng.common.util;

import java.math.BigInteger;
import zidoo.http.HTTP;

/* compiled from: Base64 */
public class c extends d {
    static final byte[] a = new byte[]{HTTP.CR, (byte) 10};
    private static final int m = 6;
    private static final int n = 3;
    private static final int o = 4;
    private static final byte[] p = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
    private static final byte[] q = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
    private static final byte[] r = new byte[]{(byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 62, (byte) -1, (byte) 62, (byte) -1, (byte) 63, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, (byte) 61, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, HTTP.CR, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 63, (byte) -1, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51};
    private static final int s = 63;
    private final byte[] t;
    private final byte[] u;
    private final byte[] v;
    private final int w;
    private final int x;
    private int y;

    public c() {
        this(0);
    }

    public c(boolean z) {
        this(76, a, z);
    }

    public c(int i) {
        this(i, a);
    }

    public c(int i, byte[] bArr) {
        this(i, bArr, false);
    }

    public c(int i, byte[] bArr, boolean z) {
        int i2;
        if (bArr == null) {
            i2 = 0;
        } else {
            i2 = bArr.length;
        }
        super(3, 4, i, i2);
        this.u = r;
        if (bArr == null) {
            this.x = 4;
            this.v = null;
        } else if (n(bArr)) {
            throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + a.f(bArr) + "]");
        } else if (i > 0) {
            this.x = bArr.length + 4;
            this.v = new byte[bArr.length];
            System.arraycopy(bArr, 0, this.v, 0, bArr.length);
        } else {
            this.x = 4;
            this.v = null;
        }
        this.w = this.x - 1;
        this.t = z ? q : p;
    }

    public boolean a() {
        return this.t == q;
    }

    void a(byte[] bArr, int i, int i2) {
        if (!this.j) {
            int i3;
            int i4;
            if (i2 < 0) {
                this.j = true;
                if (this.l != 0 || this.g != 0) {
                    a(this.x);
                    i3 = this.i;
                    byte[] bArr2;
                    switch (this.l) {
                        case 1:
                            bArr2 = this.h;
                            i4 = this.i;
                            this.i = i4 + 1;
                            bArr2[i4] = this.t[(this.y >> 2) & s];
                            bArr2 = this.h;
                            i4 = this.i;
                            this.i = i4 + 1;
                            bArr2[i4] = this.t[(this.y << 4) & s];
                            if (this.t == p) {
                                bArr2 = this.h;
                                i4 = this.i;
                                this.i = i4 + 1;
                                bArr2[i4] = (byte) 61;
                                bArr2 = this.h;
                                i4 = this.i;
                                this.i = i4 + 1;
                                bArr2[i4] = (byte) 61;
                                break;
                            }
                            break;
                        case 2:
                            bArr2 = this.h;
                            i4 = this.i;
                            this.i = i4 + 1;
                            bArr2[i4] = this.t[(this.y >> 10) & s];
                            bArr2 = this.h;
                            i4 = this.i;
                            this.i = i4 + 1;
                            bArr2[i4] = this.t[(this.y >> 4) & s];
                            bArr2 = this.h;
                            i4 = this.i;
                            this.i = i4 + 1;
                            bArr2[i4] = this.t[(this.y << 2) & s];
                            if (this.t == p) {
                                bArr2 = this.h;
                                i4 = this.i;
                                this.i = i4 + 1;
                                bArr2[i4] = (byte) 61;
                                break;
                            }
                            break;
                    }
                    this.k = (this.i - i3) + this.k;
                    if (this.g > 0 && this.k > 0) {
                        System.arraycopy(this.v, 0, this.h, this.i, this.v.length);
                        this.i += this.v.length;
                        return;
                    }
                    return;
                }
                return;
            }
            int i5 = 0;
            while (i5 < i2) {
                a(this.x);
                this.l = (this.l + 1) % 3;
                i4 = i + 1;
                i3 = bArr[i];
                if (i3 < 0) {
                    i3 += 256;
                }
                this.y = i3 + (this.y << 8);
                if (this.l == 0) {
                    byte[] bArr3 = this.h;
                    int i6 = this.i;
                    this.i = i6 + 1;
                    bArr3[i6] = this.t[(this.y >> 18) & s];
                    bArr3 = this.h;
                    i6 = this.i;
                    this.i = i6 + 1;
                    bArr3[i6] = this.t[(this.y >> 12) & s];
                    bArr3 = this.h;
                    i6 = this.i;
                    this.i = i6 + 1;
                    bArr3[i6] = this.t[(this.y >> 6) & s];
                    bArr3 = this.h;
                    i6 = this.i;
                    this.i = i6 + 1;
                    bArr3[i6] = this.t[this.y & s];
                    this.k += 4;
                    if (this.g > 0 && this.g <= this.k) {
                        System.arraycopy(this.v, 0, this.h, this.i, this.v.length);
                        this.i += this.v.length;
                        this.k = 0;
                    }
                }
                i5++;
                i = i4;
            }
        }
    }

    void b(byte[] bArr, int i, int i2) {
        if (!this.j) {
            int i3;
            if (i2 < 0) {
                this.j = true;
            }
            int i4 = 0;
            while (i4 < i2) {
                a(this.w);
                i3 = i + 1;
                byte b = bArr[i];
                if (b == (byte) 61) {
                    this.j = true;
                    break;
                }
                if (b >= (byte) 0 && b < r.length) {
                    b = r[b];
                    if (b >= (byte) 0) {
                        this.l = (this.l + 1) % 4;
                        this.y = b + (this.y << 6);
                        if (this.l == 0) {
                            byte[] bArr2 = this.h;
                            int i5 = this.i;
                            this.i = i5 + 1;
                            bArr2[i5] = (byte) ((this.y >> 16) & 255);
                            bArr2 = this.h;
                            i5 = this.i;
                            this.i = i5 + 1;
                            bArr2[i5] = (byte) ((this.y >> 8) & 255);
                            bArr2 = this.h;
                            i5 = this.i;
                            this.i = i5 + 1;
                            bArr2[i5] = (byte) (this.y & 255);
                        }
                    }
                }
                i4++;
                i = i3;
            }
            if (this.j && this.l != 0) {
                a(this.w);
                byte[] bArr3;
                switch (this.l) {
                    case 2:
                        this.y >>= 4;
                        bArr3 = this.h;
                        i3 = this.i;
                        this.i = i3 + 1;
                        bArr3[i3] = (byte) (this.y & 255);
                        return;
                    case 3:
                        this.y >>= 2;
                        bArr3 = this.h;
                        i3 = this.i;
                        this.i = i3 + 1;
                        bArr3[i3] = (byte) ((this.y >> 8) & 255);
                        bArr3 = this.h;
                        i3 = this.i;
                        this.i = i3 + 1;
                        bArr3[i3] = (byte) (this.y & 255);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static boolean a(byte b) {
        return b == (byte) 61 || (b >= (byte) 0 && b < r.length && r[b] != (byte) -1);
    }

    public static boolean a(String str) {
        return b(a.f(str));
    }

    public static boolean a(byte[] bArr) {
        return b(bArr);
    }

    public static boolean b(byte[] bArr) {
        int i = 0;
        while (i < bArr.length) {
            if (!a(bArr[i]) && !d.c(bArr[i])) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static byte[] c(byte[] bArr) {
        return a(bArr, false);
    }

    public static String d(byte[] bArr) {
        return a.f(a(bArr, false));
    }

    public static byte[] e(byte[] bArr) {
        return a(bArr, false, true);
    }

    public static String f(byte[] bArr) {
        return a.f(a(bArr, false, true));
    }

    public static byte[] g(byte[] bArr) {
        return a(bArr, true);
    }

    public static byte[] a(byte[] bArr, boolean z) {
        return a(bArr, z, false);
    }

    public static byte[] a(byte[] bArr, boolean z, boolean z2) {
        return a(bArr, z, z2, Integer.MAX_VALUE);
    }

    public static byte[] a(byte[] bArr, boolean z, boolean z2, int i) {
        if (bArr == null || bArr.length == 0) {
            return bArr;
        }
        c cVar = z ? new c(z2) : new c(0, a, z2);
        long o = cVar.o(bArr);
        if (o <= ((long) i)) {
            return cVar.l(bArr);
        }
        throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + o + ") than the specified maximum size of " + i);
    }

    public static byte[] b(String str) {
        return new c().c(str);
    }

    public static byte[] h(byte[] bArr) {
        return new c().k(bArr);
    }

    public static BigInteger i(byte[] bArr) {
        return new BigInteger(1, h(bArr));
    }

    public static byte[] a(BigInteger bigInteger) {
        if (bigInteger != null) {
            return a(b(bigInteger), false);
        }
        throw new NullPointerException("encodeInteger called with null parameter");
    }

    static byte[] b(BigInteger bigInteger) {
        int bitLength = ((bigInteger.bitLength() + 7) >> 3) << 3;
        Object toByteArray = bigInteger.toByteArray();
        if (bigInteger.bitLength() % 8 != 0 && (bigInteger.bitLength() / 8) + 1 == bitLength / 8) {
            return toByteArray;
        }
        int i = 0;
        int length = toByteArray.length;
        if (bigInteger.bitLength() % 8 == 0) {
            i = 1;
            length--;
        }
        Object obj = new byte[(bitLength / 8)];
        System.arraycopy(toByteArray, i, obj, (bitLength / 8) - length, length);
        return obj;
    }

    protected boolean b(byte b) {
        return b >= (byte) 0 && b < this.u.length && this.u[b] != (byte) -1;
    }
}
