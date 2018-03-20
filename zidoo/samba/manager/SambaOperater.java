package zidoo.samba.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class SambaOperater {
    public void copyFile(String src, String targetPath) throws SmbException, MalformedURLException {
        preCreDir(targetPath);
        SmbFile smbFileSrc = new SmbFile(src);
        SmbFile toSmbFile = new SmbFile(targetPath);
        if (toSmbFile.exists()) {
            toSmbFile.delete();
        } else {
            toSmbFile.createNewFile();
        }
        smbFileSrc.copyTo(toSmbFile);
    }

    private void preCreDir(String url) throws SmbException, MalformedURLException {
        SmbFile file = new SmbFile(url.substring(0, url.lastIndexOf(47)));
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void deleteFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else {
            deleteDir(new File(path));
        }
    }

    private void deleteDir(File file) {
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            file.delete();
            return;
        }
        for (File eachFile : files) {
            if (eachFile.isFile()) {
                eachFile.delete();
            } else {
                deleteDir(eachFile);
            }
        }
        file.delete();
    }

    public void createSmbDir(String fileName) throws IOException {
        new SmbFile(fileName).mkdir();
    }

    public void downToLocal(String smbFilePath, String localPath) {
        try {
            if (new SmbFile(smbFilePath).isFile()) {
                downSmbFile(smbFilePath, localPath);
            } else {
                downSmbDir(smbFilePath, localPath);
            }
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
    }

    private void downSmbFile(String smbFilePath, String localPath) {
        Exception e;
        Throwable th;
        InputStream in = null;
        OutputStream out = null;
        try {
            OutputStream out2;
            SmbFile remoteFile = new SmbFile(smbFilePath);
            remoteFile.connect();
            if (!localPath.endsWith("/")) {
                localPath = localPath + "/";
            }
            File localFile = new File(localPath + remoteFile.getName());
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            InputStream in2 = new SmbFileInputStream(remoteFile);
            try {
                out2 = new FileOutputStream(localFile);
            } catch (Exception e2) {
                e = e2;
                in = in2;
                try {
                    e.printStackTrace();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (in == null) {
                        try {
                            in.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e3222) {
                            e3222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                in = in2;
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                throw th;
            }
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int len = in2.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    out2.write(buffer, 0, len);
                }
                out2.flush();
                if (out2 != null) {
                    try {
                        out2.close();
                    } catch (IOException e32222) {
                        e32222.printStackTrace();
                    }
                }
                if (in2 != null) {
                    try {
                        in2.close();
                        out = out2;
                        in = in2;
                        return;
                    } catch (IOException e322222) {
                        e322222.printStackTrace();
                        out = out2;
                        in = in2;
                        return;
                    }
                }
                in = in2;
            } catch (Exception e4) {
                e = e4;
                out = out2;
                in = in2;
                e.printStackTrace();
                if (out != null) {
                    out.close();
                }
                if (in == null) {
                    in.close();
                }
            } catch (Throwable th4) {
                th = th4;
                out = out2;
                in = in2;
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            e.printStackTrace();
            if (out != null) {
                out.close();
            }
            if (in == null) {
                in.close();
            }
        }
    }

    private void downSmbDir(String smbDirPath, String localPath) {
        if (!smbDirPath.endsWith("/")) {
            smbDirPath = smbDirPath + "/";
        }
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }
        try {
            SmbFile remoteFile = new SmbFile(smbDirPath);
            new File(localPath + remoteFile.getName()).mkdirs();
            String[] fileList = remoteFile.list();
            if (fileList != null && fileList.length > 0) {
                for (String filePath : fileList) {
                    if (new SmbFile(smbDirPath + filePath).isFile()) {
                        downSmbFile(smbDirPath + filePath, localPath + remoteFile.getName());
                    } else {
                        downSmbDir(smbDirPath + filePath, localPath + remoteFile.getName());
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e2) {
            e2.printStackTrace();
        }
    }

    public void shareToSmb(String localPath, String remotePath) {
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            return;
        }
        if (localFile.isFile()) {
            shareFileToSmb(localPath, remotePath);
        } else {
            shareDirToSmb(localPath, remotePath);
        }
    }

    private void shareFileToSmb(String localPath, String remotePath) {
        Exception e;
        Throwable th;
        InputStream in = null;
        OutputStream out = null;
        try {
            if (!new SmbFile(remotePath).exists()) {
                new SmbFile(remotePath).mkdirs();
            }
            File localFile = new File(localPath);
            SmbFile remoteFile = new SmbFile(remotePath + "/" + localFile.getName());
            remoteFile.createNewFile();
            remoteFile.connect();
            InputStream in2 = new FileInputStream(localFile);
            try {
                OutputStream out2 = new SmbFileOutputStream(remoteFile);
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int len = in2.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        out2.write(buffer, 0, len);
                    }
                    out2.flush();
                    if (out2 != null) {
                        try {
                            out2.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (in2 != null) {
                        try {
                            in2.close();
                            out = out2;
                            in = in2;
                            return;
                        } catch (IOException e22) {
                            e22.printStackTrace();
                            out = out2;
                            in = in2;
                            return;
                        }
                    }
                    in = in2;
                } catch (Exception e3) {
                    e = e3;
                    out = out2;
                    in = in2;
                    try {
                        e.printStackTrace();
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        if (in == null) {
                            try {
                                in.close();
                            } catch (IOException e2222) {
                                e2222.printStackTrace();
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e22222) {
                                e22222.printStackTrace();
                            }
                        }
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e222222) {
                                e222222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    out = out2;
                    in = in2;
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e = e4;
                in = in2;
                e.printStackTrace();
                if (out != null) {
                    out.close();
                }
                if (in == null) {
                    in.close();
                }
            } catch (Throwable th4) {
                th = th4;
                in = in2;
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            e.printStackTrace();
            if (out != null) {
                out.close();
            }
            if (in == null) {
                in.close();
            }
        }
    }

    private void shareDirToSmb(String localPath, String remotePath) {
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }
        if (!remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }
        try {
            File localFile = new File(localPath);
            new SmbFile(remotePath + localFile.getName()).mkdir();
            String[] fileList = localFile.list();
            if (fileList != null && fileList.length > 0) {
                for (String filePath : fileList) {
                    if (new File(filePath).isFile()) {
                        shareFileToSmb(localPath + filePath, remotePath + localFile.getName());
                    } else {
                        shareDirToSmb(localPath + filePath, remotePath + localFile.getName());
                    }
                }
            }
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
    }
}
