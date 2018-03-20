package com.umeng.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/* compiled from: DeflaterHelper */
public class f {
    public static int a;

    public static byte[] a(String str, String str2) throws IOException {
        Throwable th;
        if (g.d(str)) {
            return null;
        }
        Deflater deflater = new Deflater();
        deflater.setInput(str.getBytes(str2));
        deflater.finish();
        byte[] bArr = new byte[8192];
        a = 0;
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            while (!deflater.finished()) {
                try {
                    int deflate = deflater.deflate(bArr);
                    a += deflate;
                    byteArrayOutputStream.write(bArr, 0, deflate);
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            deflater.end();
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th3) {
            Throwable th4 = th3;
            byteArrayOutputStream = null;
            th = th4;
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            throw th;
        }
    }

    public static String a(byte[] bArr, String str) throws UnsupportedEncodingException, DataFormatException {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        Inflater inflater = new Inflater();
        byte[] bArr2 = new byte[100];
        inflater.setInput(bArr, 0, bArr.length);
        StringBuilder stringBuilder = new StringBuilder();
        while (!inflater.needsInput()) {
            stringBuilder.append(new String(bArr2, 0, inflater.inflate(bArr2), str));
        }
        inflater.end();
        return stringBuilder.toString();
    }
}
