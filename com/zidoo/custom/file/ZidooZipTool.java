package com.zidoo.custom.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZidooZipTool {
    public static boolean unZip(String unZipfile, String destDir) {
        try {
            byte[] buf = new byte[4096];
            ZipFile zipFile = new ZipFile(unZipfile);
            File saveFile = new File(destDir);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File file = new File(new StringBuilder(String.valueOf(destDir)).append(File.separator).append(entry.getName()).toString());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    InputStream inputStream = zipFile.getInputStream(entry);
                    FileOutputStream fileOut = new FileOutputStream(file);
                    while (true) {
                        int readedBytes = inputStream.read(buf);
                        if (readedBytes <= 0) {
                            break;
                        }
                        fileOut.write(buf, 0, readedBytes);
                    }
                    fileOut.close();
                    inputStream.close();
                }
            }
            zipFile.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
}
