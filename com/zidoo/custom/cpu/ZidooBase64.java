package com.zidoo.custom.cpu;

import java.io.ByteArrayOutputStream;
import zidoo.http.HTTP;

public class ZidooBase64 {
    private static byte[] base64DecodeChars;
    private static final char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    static {
        byte[] bArr = new byte[128];
        bArr[0] = (byte) -1;
        bArr[1] = (byte) -1;
        bArr[2] = (byte) -1;
        bArr[3] = (byte) -1;
        bArr[4] = (byte) -1;
        bArr[5] = (byte) -1;
        bArr[6] = (byte) -1;
        bArr[7] = (byte) -1;
        bArr[8] = (byte) -1;
        bArr[9] = (byte) -1;
        bArr[10] = (byte) -1;
        bArr[11] = (byte) -1;
        bArr[12] = (byte) -1;
        bArr[13] = (byte) -1;
        bArr[14] = (byte) -1;
        bArr[15] = (byte) -1;
        bArr[16] = (byte) -1;
        bArr[17] = (byte) -1;
        bArr[18] = (byte) -1;
        bArr[19] = (byte) -1;
        bArr[20] = (byte) -1;
        bArr[21] = (byte) -1;
        bArr[22] = (byte) -1;
        bArr[23] = (byte) -1;
        bArr[24] = (byte) -1;
        bArr[25] = (byte) -1;
        bArr[26] = (byte) -1;
        bArr[27] = (byte) -1;
        bArr[28] = (byte) -1;
        bArr[29] = (byte) -1;
        bArr[30] = (byte) -1;
        bArr[31] = (byte) -1;
        bArr[32] = (byte) -1;
        bArr[33] = (byte) -1;
        bArr[34] = (byte) -1;
        bArr[35] = (byte) -1;
        bArr[36] = (byte) -1;
        bArr[37] = (byte) -1;
        bArr[38] = (byte) -1;
        bArr[39] = (byte) -1;
        bArr[40] = (byte) -1;
        bArr[41] = (byte) -1;
        bArr[42] = (byte) -1;
        bArr[43] = (byte) 62;
        bArr[44] = (byte) -1;
        bArr[45] = (byte) -1;
        bArr[46] = (byte) -1;
        bArr[47] = (byte) 63;
        bArr[48] = (byte) 52;
        bArr[49] = (byte) 53;
        bArr[50] = (byte) 54;
        bArr[51] = (byte) 55;
        bArr[52] = (byte) 56;
        bArr[53] = (byte) 57;
        bArr[54] = (byte) 58;
        bArr[55] = (byte) 59;
        bArr[56] = (byte) 60;
        bArr[57] = (byte) 61;
        bArr[58] = (byte) -1;
        bArr[59] = (byte) -1;
        bArr[60] = (byte) -1;
        bArr[61] = (byte) -1;
        bArr[62] = (byte) -1;
        bArr[63] = (byte) -1;
        bArr[64] = (byte) -1;
        bArr[66] = (byte) 1;
        bArr[67] = (byte) 2;
        bArr[68] = (byte) 3;
        bArr[69] = (byte) 4;
        bArr[70] = (byte) 5;
        bArr[71] = (byte) 6;
        bArr[72] = (byte) 7;
        bArr[73] = (byte) 8;
        bArr[74] = (byte) 9;
        bArr[75] = (byte) 10;
        bArr[76] = (byte) 11;
        bArr[77] = (byte) 12;
        bArr[78] = HTTP.CR;
        bArr[79] = (byte) 14;
        bArr[80] = (byte) 15;
        bArr[81] = (byte) 16;
        bArr[82] = (byte) 17;
        bArr[83] = (byte) 18;
        bArr[84] = (byte) 19;
        bArr[85] = (byte) 20;
        bArr[86] = (byte) 21;
        bArr[87] = (byte) 22;
        bArr[88] = (byte) 23;
        bArr[89] = (byte) 24;
        bArr[90] = (byte) 25;
        bArr[91] = (byte) -1;
        bArr[92] = (byte) -1;
        bArr[93] = (byte) -1;
        bArr[94] = (byte) -1;
        bArr[95] = (byte) -1;
        bArr[96] = (byte) -1;
        bArr[97] = (byte) 26;
        bArr[98] = (byte) 27;
        bArr[99] = (byte) 28;
        bArr[100] = (byte) 29;
        bArr[101] = (byte) 30;
        bArr[102] = (byte) 31;
        bArr[103] = (byte) 32;
        bArr[104] = (byte) 33;
        bArr[105] = (byte) 34;
        bArr[106] = (byte) 35;
        bArr[107] = (byte) 36;
        bArr[108] = (byte) 37;
        bArr[109] = (byte) 38;
        bArr[110] = (byte) 39;
        bArr[111] = (byte) 40;
        bArr[112] = (byte) 41;
        bArr[113] = (byte) 42;
        bArr[114] = (byte) 43;
        bArr[115] = (byte) 44;
        bArr[116] = (byte) 45;
        bArr[117] = (byte) 46;
        bArr[118] = (byte) 47;
        bArr[119] = (byte) 48;
        bArr[120] = (byte) 49;
        bArr[121] = (byte) 50;
        bArr[122] = (byte) 51;
        bArr[123] = (byte) -1;
        bArr[124] = (byte) -1;
        bArr[125] = (byte) -1;
        bArr[126] = (byte) -1;
        bArr[127] = (byte) -1;
        base64DecodeChars = bArr;
    }

    public static String encode(byte[] data) {
        int i;
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i2 = 0;
        while (i2 < len) {
            i = i2 + 1;
            int b1 = data[i2] & 255;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 3) << 4]);
                sb.append("==");
                break;
            }
            i2 = i + 1;
            int b2 = data[i] & 255;
            if (i2 == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 3) << 4) | ((b2 & 240) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 15) << 2]);
                sb.append("=");
                i = i2;
                break;
            }
            i = i2 + 1;
            int b3 = data[i2] & 255;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 3) << 4) | ((b2 & 240) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 15) << 2) | ((b3 & 192) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 63]);
            i2 = i;
        }
        i = i2;
        return sb.toString();
    }

    public static byte[] decode(String str) {
        byte[] data = str.getBytes();
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        while (i < len) {
            int b3;
            int b4;
            while (true) {
                int b2;
                int i2 = i + 1;
                int b1 = base64DecodeChars[data[i]];
                if (i2 < len && b1 == -1) {
                    i = i2;
                } else if (b1 == -1) {
                    i = i2;
                    break;
                } else {
                    do {
                        i = i2;
                        i2 = i + 1;
                        b2 = base64DecodeChars[data[i]];
                        if (i2 >= len) {
                            break;
                        }
                    } while (b2 == -1);
                    if (b2 == -1) {
                        i = i2;
                        break;
                    }
                    buf.write((b1 << 2) | ((b2 & 48) >>> 4));
                    do {
                        i = i2;
                        i2 = i + 1;
                        b3 = data[i];
                        if (b3 == 61) {
                            b3 = base64DecodeChars[b3];
                            if (i2 >= len) {
                                break;
                            }
                        } else {
                            i = i2;
                            return buf.toByteArray();
                        }
                    } while (b3 == -1);
                    if (b3 == -1) {
                        i = i2;
                        break;
                    }
                    buf.write(((b2 & 15) << 4) | ((b3 & 60) >>> 2));
                    do {
                        i = i2;
                        i2 = i + 1;
                        b4 = data[i];
                        if (b4 == 61) {
                            b4 = base64DecodeChars[b4];
                            if (i2 >= len) {
                                break;
                            }
                        } else {
                            i = i2;
                            return buf.toByteArray();
                        }
                    } while (b4 == -1);
                    if (b4 == -1) {
                        i = i2;
                        break;
                    }
                    buf.write(((b3 & 3) << 6) | b4);
                    i = i2;
                }
            }
            if (b1 == -1) {
                i = i2;
                break;
            }
            do {
                i = i2;
                i2 = i + 1;
                b2 = base64DecodeChars[data[i]];
                if (i2 >= len) {
                    break;
                }
                break;
            } while (b2 == -1);
            if (b2 == -1) {
                i = i2;
                break;
            }
            buf.write((b1 << 2) | ((b2 & 48) >>> 4));
            do {
                i = i2;
                i2 = i + 1;
                b3 = data[i];
                if (b3 == 61) {
                    b3 = base64DecodeChars[b3];
                    if (i2 >= len) {
                        break;
                    }
                    break;
                }
                i = i2;
                return buf.toByteArray();
            } while (b3 == -1);
            if (b3 == -1) {
                i = i2;
                break;
            }
            buf.write(((b2 & 15) << 4) | ((b3 & 60) >>> 2));
            do {
                i = i2;
                i2 = i + 1;
                b4 = data[i];
                if (b4 == 61) {
                    b4 = base64DecodeChars[b4];
                    if (i2 >= len) {
                        break;
                    }
                    break;
                }
                i = i2;
                return buf.toByteArray();
            } while (b4 == -1);
            if (b4 == -1) {
                i = i2;
                break;
            }
            buf.write(((b3 & 3) << 6) | b4);
            i = i2;
        }
        return buf.toByteArray();
    }
}
