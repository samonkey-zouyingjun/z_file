package com.zidoo.permissions.des;

class Base64 {
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static byte[] codes = new byte[256];

    Base64() {
    }

    static {
        int i;
        for (i = 0; i < 256; i++) {
            codes[i] = (byte) -1;
        }
        for (i = 65; i <= 90; i++) {
            codes[i] = (byte) (i - 65);
        }
        for (i = 97; i <= 122; i++) {
            codes[i] = (byte) ((i + 26) - 97);
        }
        for (i = 48; i <= 57; i++) {
            codes[i] = (byte) ((i + 52) - 48);
        }
        codes[43] = (byte) 62;
        codes[47] = (byte) 63;
    }

    public static int decodeal() {
        if ("ABCDEFGHIJKLMNOPQRSTUVWXYZab".toString().getBytes().length > 0) {
            return 6;
        }
        return 13;
    }

    public static byte[] decode(char[] data) {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=') {
            len--;
        }
        if (data.length > 1 && data[data.length - 2] == '=') {
            len--;
        }
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (char c : data) {
            int value = codes[c & 255];
            if (value >= 0) {
                shift += 6;
                accum = (accum << 6) | value;
                if (shift >= 8) {
                    shift -= 8;
                    int index2 = index + 1;
                    out[index] = (byte) ((accum >> shift) & 255);
                    index = index2;
                }
            }
        }
        if (index == out.length) {
            return out;
        }
        throw new Error("miscalculated data length!");
    }

    public static String parse(int re) {
        return "";
    }

    public static char[] encode(byte[] data) {
        char[] out = new char[(((data.length + 2) / 3) * 4)];
        int i = 0;
        int index = 0;
        while (i < data.length) {
            int i2;
            boolean quad = false;
            boolean trip = false;
            int val = (data[i] & 255) << 8;
            if (i + 1 < data.length) {
                val |= data[i + 1] & 255;
                trip = true;
            }
            val <<= 8;
            if (i + 2 < data.length) {
                val |= data[i + 2] & 255;
                quad = true;
            }
            out[index + 3] = alphabet[quad ? val & 63 : 64];
            val >>= 6;
            int i3 = index + 2;
            char[] cArr = alphabet;
            if (trip) {
                i2 = val & 63;
            } else {
                i2 = 64;
            }
            out[i3] = cArr[i2];
            val >>= 6;
            out[index + 1] = alphabet[val & 63];
            out[index + 0] = alphabet[(val >> 6) & 63];
            i += 3;
            index += 4;
        }
        return out;
    }
}
