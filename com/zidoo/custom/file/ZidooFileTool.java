package com.zidoo.custom.file;

import android.content.Context;
import android.os.Environment;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ZidooFileTool {
    public static final int CONNECTTIMEOUT = 40000;
    public static final int READTIMEOUT = 40000;

    public static String toUtf8Strings(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '\u0000' || c > 'ÿ') {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception e) {
                    b = new byte[0];
                }
                for (int k : b) {
                    int k2;
                    if (k2 < 0) {
                        k2 += 256;
                    }
                    sb.append("%" + Integer.toHexString(k2).toUpperCase());
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void execMethod(String path) {
        try {
            Runtime.getRuntime().exec(new String[]{"chmod", "777", path});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getFlashPath() {
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                return Environment.getExternalStorageDirectory().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFlashPath(String savePath) {
        String flah = getFlashPath();
        String path = null;
        if (flah != null) {
            path = new StringBuilder(String.valueOf(flah)).append("/").append(savePath).append("/").toString();
            File sdcardFile = new File(path);
            if (!sdcardFile.exists()) {
                sdcardFile.mkdirs();
            }
        }
        return path;
    }

    public static String getDataDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getDataDir(Context context, String savePath) {
        String path = new StringBuilder(String.valueOf(context.getFilesDir().getAbsolutePath())).append("/").append(savePath).append("/").toString();
        File sdcardFile = new File(path);
        if (!sdcardFile.exists()) {
            sdcardFile.mkdirs();
        }
        return path;
    }

    public static String fileToString(String filePath) {
        try {
            return fileToString(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fileToString(File file) {
        try {
            if (!file.exists()) {
                return null;
            }
            InputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String m = "";
            String n = "";
            while (true) {
                m = br.readLine();
                if (m == null) {
                    inputStream.close();
                    br.close();
                    return n;
                }
                n = new StringBuilder(String.valueOf(n)).append(m).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] fileToStringByte(String filePath) {
        IOException e;
        byte[] buffer;
        FileNotFoundException e2;
        Throwable th;
        Exception e3;
        FileInputStream fi = null;
        try {
            File file = new File(filePath);
            long fileSize = file.length();
            if (fileSize > 2147483647L) {
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e4) {
                        e4.printStackTrace();
                    }
                }
                return null;
            }
            FileInputStream fi2 = new FileInputStream(file);
            try {
                buffer = new byte[((int) fileSize)];
                int offset = 0;
                while (offset < buffer.length) {
                    int numRead = fi2.read(buffer, offset, buffer.length - offset);
                    if (numRead < 0) {
                        break;
                    }
                    offset += numRead;
                }
                fi2.close();
                fi = null;
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e42) {
                        e42.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e5) {
                e2 = e5;
                fi = fi2;
                try {
                    e2.printStackTrace();
                    buffer = null;
                    if (fi != null) {
                        try {
                            fi.close();
                        } catch (IOException e422) {
                            e422.printStackTrace();
                        }
                    }
                    return buffer;
                } catch (Throwable th2) {
                    th = th2;
                    if (fi != null) {
                        try {
                            fi.close();
                        } catch (IOException e4222) {
                            e4222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e6) {
                e4222 = e6;
                fi = fi2;
                e4222.printStackTrace();
                buffer = null;
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e42222) {
                        e42222.printStackTrace();
                    }
                }
                return buffer;
            } catch (Exception e7) {
                e3 = e7;
                fi = fi2;
                e3.printStackTrace();
                buffer = null;
                if (fi != null) {
                    try {
                        fi.close();
                    } catch (IOException e422222) {
                        e422222.printStackTrace();
                    }
                }
                return buffer;
            } catch (Throwable th3) {
                th = th3;
                fi = fi2;
                if (fi != null) {
                    fi.close();
                }
                throw th;
            }
            return buffer;
        } catch (FileNotFoundException e8) {
            e2 = e8;
            e2.printStackTrace();
            buffer = null;
            if (fi != null) {
                fi.close();
            }
            return buffer;
        } catch (IOException e9) {
            e422222 = e9;
            e422222.printStackTrace();
            buffer = null;
            if (fi != null) {
                fi.close();
            }
            return buffer;
        } catch (Exception e10) {
            e3 = e10;
            e3.printStackTrace();
            buffer = null;
            if (fi != null) {
                fi.close();
            }
            return buffer;
        }
    }

    public static boolean downFile(String webpath, String savePath, int connectTimeout, int readTimeout) {
        Exception e1;
        Throwable th;
        BufferedInputStream bis = null;
        InputStream inputstream = null;
        OutputStream outputStream = null;
        try {
            File tempFile = new File(savePath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            tempFile.createNewFile();
            HttpURLConnection urlc = (HttpURLConnection) new URL(webpath.trim()).openConnection();
            urlc.setDoInput(true);
            urlc.setConnectTimeout(connectTimeout);
            urlc.setReadTimeout(readTimeout);
            urlc.connect();
            if (urlc.getResponseCode() == 200) {
                inputstream = urlc.getInputStream();
                BufferedInputStream bis2 = new BufferedInputStream(inputstream, 4096);
                if (bis2 != null) {
                    try {
                        OutputStream outputStream2 = new FileOutputStream(savePath);
                        try {
                            byte[] buf = new byte[4096];
                            while (true) {
                                int ch = bis2.read(buf);
                                if (ch == -1) {
                                    break;
                                }
                                outputStream2.write(buf, 0, ch);
                            }
                            if (outputStream2 != null) {
                                outputStream2.close();
                                outputStream = null;
                            } else {
                                outputStream = outputStream2;
                            }
                            if (inputstream != null) {
                                inputstream.close();
                                inputstream = null;
                            }
                            if (bis2 != null) {
                                bis2.close();
                                bis = null;
                            } else {
                                bis = bis2;
                            }
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (inputstream != null) {
                                inputstream.close();
                            }
                            if (bis != null) {
                                bis.close();
                            }
                            return true;
                        } catch (Exception e2) {
                            e1 = e2;
                            outputStream = outputStream2;
                            bis = bis2;
                        } catch (Throwable th2) {
                            th = th2;
                            outputStream = outputStream2;
                            bis = bis2;
                        }
                    } catch (Exception e3) {
                        e1 = e3;
                        bis = bis2;
                        try {
                            e1.printStackTrace();
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (Exception e4) {
                                    e4.printStackTrace();
                                }
                            }
                            if (inputstream != null) {
                                inputstream.close();
                            }
                            if (bis != null) {
                                bis.close();
                            }
                            return false;
                        } catch (Throwable th3) {
                            th = th3;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (Exception e42) {
                                    e42.printStackTrace();
                                    throw th;
                                }
                            }
                            if (inputstream != null) {
                                inputstream.close();
                            }
                            if (bis != null) {
                                bis.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        bis = bis2;
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (inputstream != null) {
                            inputstream.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        throw th;
                    }
                }
                bis = bis2;
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e422) {
                    e422.printStackTrace();
                }
            }
            if (inputstream != null) {
                inputstream.close();
            }
            if (bis != null) {
                bis.close();
            }
        } catch (Exception e5) {
            e1 = e5;
            e1.printStackTrace();
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputstream != null) {
                inputstream.close();
            }
            if (bis != null) {
                bis.close();
            }
            return false;
        }
        return false;
    }

    public static boolean copyAssetFileToDir(Context context, String fileName, String tarDir) {
        File file;
        IOException e;
        try {
            InputStream is = context.getAssets().open(fileName);
            File tarfile = new File(tarDir);
            if (!tarfile.exists()) {
                tarfile.mkdirs();
            }
            String tarPath = tarDir;
            if (!tarDir.endsWith("/")) {
                tarPath = new StringBuilder(String.valueOf(tarPath)).append("/").toString();
            }
            File file2 = new File(new StringBuilder(String.valueOf(tarPath)).append(fileName).toString());
            try {
                if (file2.exists()) {
                    file2.delete();
                }
                file2.createNewFile();
                FileOutputStream fos = new FileOutputStream(file2);
                byte[] temp = new byte[4096];
                while (true) {
                    int i = is.read(temp);
                    if (i <= 0) {
                        fos.flush();
                        fos.close();
                        is.close();
                        file = file2;
                        return true;
                    }
                    fos.write(temp, 0, i);
                }
            } catch (IOException e2) {
                e = e2;
                file = file2;
                e.printStackTrace();
                return false;
            }
        } catch (IOException e3) {
            e = e3;
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copeFileToDir(Context context, String copePath, String tarDir) {
        boolean bRet;
        try {
            File copeFile = new File(copePath);
            if (!copeFile.exists() || copeFile.isDirectory()) {
                return false;
            }
            File tarfile = new File(tarDir);
            if (!tarfile.exists()) {
                tarfile.mkdirs();
            }
            String tarPath = tarDir;
            if (!tarDir.endsWith("/")) {
                tarPath = new StringBuilder(String.valueOf(tarPath)).append("/").toString();
            }
            File file = new File(new StringBuilder(String.valueOf(tarPath)).append(copePath.substring(copePath.lastIndexOf("/") + 1, copePath.length())).toString());
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            FileInputStream in = new FileInputStream(copeFile);
            byte[] temp = new byte[8192];
            while (true) {
                int i = in.read(temp);
                if (i <= 0) {
                    break;
                }
                fos.write(temp, 0, i);
            }
            fos.flush();
            fos.close();
            in.close();
            bRet = true;
            return bRet;
        } catch (IOException e) {
            e.printStackTrace();
            bRet = false;
        }
    }

    public static void setLocalObject(Context mContext, Object mainDataInfo, String name) {
        try {
            File file = new File(new StringBuilder(String.valueOf(mContext.getFilesDir().getAbsolutePath())).append("/").append(name).toString());
            file.deleteOnExit();
            file.createNewFile();
            ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file));
            oout.writeObject(mainDataInfo);
            oout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getLocalObject(Context mContext, String name) {
        File file = new File(new StringBuilder(String.valueOf(mContext.getFilesDir().getAbsolutePath())).append("/").append(name).toString());
        if (!file.exists()) {
            return null;
        }
        try {
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
            Object newPerson = oin.readObject();
            oin.close();
            return newPerson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileMd5(String filePath) {
        try {
            File file = new File(filePath);
            byte[] buffer = new byte[1024];
            if (!file.exists()) {
                return null;
            }
            try {
                MessageDigest mMDigest = MessageDigest.getInstance("MD5");
                FileInputStream Input = new FileInputStream(file);
                while (true) {
                    int len = Input.read(buffer, 0, 1024);
                    if (len == -1) {
                        break;
                    }
                    mMDigest.update(buffer, 0, len);
                }
                Input.close();
                byte[] result = mMDigest.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : result) {
                    String hex = Integer.toHexString(b & 255);
                    if (hex.length() == 1) {
                        sb.append("0" + hex);
                    } else {
                        sb.append(hex);
                    }
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
                return null;
            } catch (IOException e3) {
                e3.printStackTrace();
                return null;
            }
        } catch (Exception e4) {
            e4.printStackTrace();
            return filePath;
        }
    }
}
