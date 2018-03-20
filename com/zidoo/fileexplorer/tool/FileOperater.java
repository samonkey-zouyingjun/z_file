package com.zidoo.fileexplorer.tool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import zidoo.device.DeviceType;
import zidoo.file.FileType;
import zidoo.model.BoxModel;

public class FileOperater {
    long copyCurrentSize = 0;
    long copyTotalSize = 0;
    int count = 0;
    private Handler handler = null;
    Timer mTimer = null;
    int num = 0;
    OnFileCopyListener onFileCopyListener = null;
    OnFileDeleteListener onFileDeleteListener = null;
    String operatePath = "";
    boolean stop = false;

    private class CopyTask extends TimerTask {
        private CopyTask() {
        }

        public void run() {
            FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_INFO);
        }
    }

    public interface OnFileCopyListener {
        void onError();

        void onInfo(String str, long j, long j2);

        void onInit(long j);

        void onNewFile(String str);

        void onSameFileExist(ArrayList<File> arrayList);

        void onSpaceNotEnough();

        void onSuccess(boolean z);
    }

    public interface OnFileDeleteListener {
        void onError();

        void onInfo(String str, int i, int i2);

        void onInit(int i);

        void onSuccess(boolean z);
    }

    public FileOperater() {
        init();
    }

    public int getFileIconResource(String path, String name) {
        if (new File(path + "/" + name).isDirectory()) {
            return R.drawable.icon_dir;
        }
        switch (FileType.getFileType(name)) {
            case 1:
                return R.drawable.icon_music;
            case 2:
                return R.drawable.icon_video;
            case 3:
                return R.drawable.icon_pic;
            case 4:
                return R.drawable.icon_txt;
            case 5:
                return R.drawable.icon_apk;
            case 6:
                return R.drawable.icon_pdf;
            case 7:
                return R.drawable.icon_word;
            case 8:
                return R.drawable.icon_exel;
            case 9:
                return R.drawable.icon_ppt;
            case 10:
                return R.drawable.icon_html;
            case 11:
                return R.drawable.icon_rar;
            case 12:
                return R.drawable.icon_other;
            default:
                return R.drawable.icon_other;
        }
    }

    public static int getFileIconResource(File file) {
        switch (FileType.getType(file)) {
            case 0:
                return R.drawable.icon_dir;
            case 1:
                return R.drawable.icon_music;
            case 2:
                return R.drawable.icon_video;
            case 3:
                return R.drawable.icon_pic;
            case 4:
                return R.drawable.icon_txt;
            case 5:
                return R.drawable.icon_apk;
            case 6:
                return R.drawable.icon_pdf;
            case 7:
                return R.drawable.icon_word;
            case 8:
                return R.drawable.icon_exel;
            case 9:
                return R.drawable.icon_ppt;
            case 10:
                return R.drawable.icon_html;
            case 11:
                return R.drawable.icon_rar;
            default:
                return R.drawable.icon_other;
        }
    }

    public static int getFileIconResource(int type) {
        switch (type) {
            case 0:
                return R.drawable.icon_dir;
            case 1:
                return R.drawable.icon_music;
            case 2:
                return R.drawable.icon_video;
            case 3:
                return R.drawable.icon_pic;
            case 4:
                return R.drawable.icon_txt;
            case 5:
                return R.drawable.icon_apk;
            case 6:
                return R.drawable.icon_pdf;
            case 7:
                return R.drawable.icon_word;
            case 8:
                return R.drawable.icon_exel;
            case 9:
                return R.drawable.icon_ppt;
            case 10:
                return R.drawable.icon_html;
            case 11:
                return R.drawable.icon_rar;
            default:
                return R.drawable.icon_other;
        }
    }

    public static int getFileBigIconResource(File file) {
        switch (FileType.getType(file)) {
            case 0:
                return R.drawable.icon_dir;
            case 1:
                return R.drawable.icon_music;
            case 2:
                return R.drawable.icon_video;
            case 3:
                return R.drawable.icon_pic;
            case 4:
                return R.drawable.icon_txt;
            case 5:
                return R.drawable.icon_apk;
            case 6:
                return R.drawable.icon_pdf;
            case 7:
                return R.drawable.icon_word;
            case 8:
                return R.drawable.icon_exel;
            case 9:
                return R.drawable.icon_ppt;
            case 10:
                return R.drawable.icon_html;
            case 11:
                return R.drawable.icon_rar;
            default:
                return R.drawable.icon_other;
        }
    }

    public static int getDeviceIconResource(DeviceType type) {
        switch (type) {
            case SPECIAL_A:
                return R.drawable.ic_device_favorite;
            case TF:
            case SD:
            case HDD:
                return R.drawable.icon_device_usb;
            case SMB:
                return R.drawable.icon_device_smb;
            case NFS:
                return R.drawable.icon_device_nfs;
            default:
                return R.drawable.icon_device_flash;
        }
    }

    public void setOnFileCopyListener(OnFileCopyListener onFileCopyListener) {
        this.onFileCopyListener = onFileCopyListener;
    }

    public void setOnFileDeleteListener(OnFileDeleteListener onFileDeleteListener) {
        this.onFileDeleteListener = onFileDeleteListener;
    }

    private void init() {
        this.mTimer = new Timer();
        this.handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AppConstant.HANDLER_OPERATE_COPY_NEW_FILE /*4101*/:
                        if (FileOperater.this.onFileCopyListener != null) {
                            FileOperater.this.onFileCopyListener.onNewFile(FileOperater.this.operatePath);
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_COPY_SUCCESS /*4102*/:
                        FileOperater.this.mTimer.cancel();
                        if (FileOperater.this.onFileCopyListener != null) {
                            FileOperater.this.onFileCopyListener.onSuccess(FileOperater.this.stop);
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_COPY_INIT /*4103*/:
                        if (FileOperater.this.onFileCopyListener != null) {
                            FileOperater.this.onFileCopyListener.onInit(FileOperater.this.copyTotalSize);
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_COPY_ERROR /*4104*/:
                        FileOperater.this.mTimer.cancel();
                        if (FileOperater.this.onFileCopyListener != null) {
                            FileOperater.this.onFileCopyListener.onError();
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_COPY_INFO /*4105*/:
                        if (!(FileOperater.this.onFileCopyListener == null || FileOperater.this.stop)) {
                            FileOperater.this.onFileCopyListener.onInfo(FileOperater.this.operatePath, FileOperater.this.copyCurrentSize, FileOperater.this.copyTotalSize);
                            break;
                        }
                    case AppConstant.HANDLER_OPERATE_DELETE_SUCCESS /*4112*/:
                        if (FileOperater.this.onFileDeleteListener != null) {
                            FileOperater.this.onFileDeleteListener.onSuccess(FileOperater.this.stop);
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_DELETE_INIT /*4113*/:
                        if (FileOperater.this.onFileDeleteListener != null) {
                            FileOperater.this.onFileDeleteListener.onInit(FileOperater.this.count);
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_DELETE_ERROR /*4114*/:
                        if (FileOperater.this.onFileDeleteListener != null) {
                            FileOperater.this.onFileDeleteListener.onError();
                            break;
                        }
                        break;
                    case AppConstant.HANDLER_OPERATE_DELETE_INFO /*4115*/:
                        if (!(FileOperater.this.onFileDeleteListener == null || FileOperater.this.stop)) {
                            FileOperater.this.onFileDeleteListener.onInfo(FileOperater.this.operatePath, ((Integer) msg.obj).intValue(), FileOperater.this.count);
                            break;
                        }
                }
                super.handleMessage(msg);
            }
        };
    }

    public boolean copyForld(String srcPath, String desPath) {
        return copyForld(new File(srcPath), new File(desPath));
    }

    public boolean copyForld(File srcFile, File desFile) {
        if (!srcFile.isDirectory()) {
            return copyFile(srcFile, desFile);
        }
        if (!desFile.exists()) {
            desFile.mkdirs();
        }
        File[] files = srcFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!copyForld(files[i], new File(desFile, files[i].getName()))) {
                return false;
            }
        }
        return true;
    }

    public boolean copyFile(String srcPath, String desPath) {
        return copyFile(new File(srcPath), new File(desPath));
    }

    public boolean copyFile(File srcFile, File desFile) {
        try {
            this.operatePath = srcFile.getPath();
            this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_NEW_FILE);
            FileInputStream in = new FileInputStream(srcFile);
            FileOutputStream out = new FileOutputStream(desFile);
            byte[] b = new byte[4194304];
            while (true) {
                int len = in.read(b);
                if (len == -1 || this.stop) {
                    break;
                }
                out.write(b, 0, len);
                this.copyCurrentSize += (long) len;
            }
            out.flush();
            in.close();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("FileOperater", "copy file error:FileNotFoundException");
            return false;
        } catch (IOException e2) {
            Log.e("FileOperater", "copy file error:IOException");
            return false;
        }
    }

    public void copyFilesTo(final String[] files, final String path) {
        new Thread(new Runnable() {
            public void run() {
                FileOperater.this.stop = false;
                FileOperater.this.copyCurrentSize = 0;
                long size = 0;
                for (String file : files) {
                    size += FileOperater.getFileSize(new File(file));
                }
                FileOperater.this.copyTotalSize = size;
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_INIT);
                FileOperater.this.mTimer = new Timer();
                FileOperater.this.mTimer.schedule(new CopyTask(), 0, 500);
                int i = 0;
                while (i < files.length && !FileOperater.this.stop) {
                    String srcPath = files[i];
                    if (FileOperater.this.copyForld(srcPath, path + "/" + srcPath.substring(srcPath.lastIndexOf("/") + 1))) {
                        i++;
                    } else {
                        FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_ERROR);
                        return;
                    }
                }
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_SUCCESS);
            }
        }).start();
    }

    public void copyFilesTo(File dir, File[] files) {
        ArrayList<File> sameFiles = new ArrayList();
        for (File name : files) {
            File file = new File(dir, name.getName());
            if (file.exists()) {
                sameFiles.add(file);
            }
        }
        if (sameFiles.size() <= 0) {
        }
    }

    public void copyFilesTo(final File[] files, final String path) {
        new Thread(new Runnable() {
            public void run() {
                FileOperater.this.stop = false;
                FileOperater.this.copyCurrentSize = 0;
                long size = 0;
                for (File fileSize : files) {
                    size += FileOperater.getFileSize(fileSize);
                }
                FileOperater.this.copyTotalSize = size;
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_INIT);
                FileOperater.this.mTimer = new Timer();
                FileOperater.this.mTimer.schedule(new CopyTask(), 0, 500);
                int i = 0;
                while (i < files.length && !FileOperater.this.stop) {
                    if (FileOperater.this.copyForld(files[i], new File(path, files[i].getName()))) {
                        i++;
                    } else {
                        FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_ERROR);
                        return;
                    }
                }
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_COPY_SUCCESS);
            }
        }).start();
    }

    public boolean renameFile(File srcFile, File newFile) {
        try {
            return srcFile.renameTo(newFile);
        } catch (Exception e) {
            Log.e("FileOperater", "rename file error!");
            return false;
        }
    }

    public boolean renameFile(String srcPath, String newPath) {
        return renameFile(new File(srcPath), new File(newPath));
    }

    public boolean cutFilesTo(String[] files, String path) {
        for (String srcPath : files) {
            if (!renameFile(srcPath, path + "/" + srcPath.substring(srcPath.lastIndexOf("/") + 1))) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public boolean cutFilesTo(File[] files, File parent) {
        for (File src : files) {
            if (!src.renameTo(new File(parent.getPath(), src.getName()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteFile(File file) {
        try {
            if (!file.isDirectory()) {
                return file.delete();
            }
            File[] files = file.listFiles();
            for (File deleteFile : files) {
                if (!deleteFile(deleteFile)) {
                    return false;
                }
            }
            return file.delete();
        } catch (Exception e) {
            Log.e("FileOperater", "delete file error!");
            return false;
        }
    }

    public boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    public void deleteFiles(final ArrayList<String> paths) {
        new Thread(new Runnable() {
            public void run() {
                FileOperater.this.stop = false;
                FileOperater.this.count = paths.size();
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_INIT);
                FileOperater.this.num = 0;
                while (FileOperater.this.num < paths.size() && !FileOperater.this.stop) {
                    FileOperater.this.operatePath = (String) paths.get(FileOperater.this.num);
                    FileOperater.this.handler.sendMessage(FileOperater.this.handler.obtainMessage(AppConstant.HANDLER_OPERATE_DELETE_INFO, Integer.valueOf(FileOperater.this.num)));
                    if (FileOperater.this.deleteFile((String) paths.get(FileOperater.this.num))) {
                        FileOperater fileOperater = FileOperater.this;
                        fileOperater.num++;
                    } else {
                        FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_ERROR);
                        return;
                    }
                }
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_SUCCESS);
            }
        }).start();
    }

    public void deleteFiles_g(final File[] files) {
        new Thread(new Runnable() {
            public void run() {
                FileOperater.this.stop = false;
                FileOperater.this.count = files.length;
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_INIT);
                FileOperater.this.num = 0;
                while (FileOperater.this.num < FileOperater.this.count && !FileOperater.this.stop) {
                    FileOperater.this.operatePath = files[FileOperater.this.num].getPath();
                    FileOperater.this.handler.sendMessage(FileOperater.this.handler.obtainMessage(AppConstant.HANDLER_OPERATE_DELETE_INFO, Integer.valueOf(FileOperater.this.num)));
                    if (FileOperater.deleteFile(files[FileOperater.this.num])) {
                        FileOperater fileOperater = FileOperater.this;
                        fileOperater.num++;
                    } else {
                        FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_ERROR);
                        return;
                    }
                }
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_SUCCESS);
                if (BoxModel.sModel == 5) {
                    Utils.execute("sync");
                }
            }
        }).start();
    }

    public void deleteFilesByCommand(final File[] files) {
        new Thread(new Runnable() {
            public void run() {
                FileOperater.this.stop = false;
                FileOperater.this.count = files.length;
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_INIT);
                FileOperater.this.num = 0;
                while (FileOperater.this.num < FileOperater.this.count && !FileOperater.this.stop) {
                    boolean delete;
                    FileOperater.this.operatePath = files[FileOperater.this.num].getPath();
                    FileOperater.this.handler.sendMessage(FileOperater.this.handler.obtainMessage(AppConstant.HANDLER_OPERATE_DELETE_INFO, Integer.valueOf(FileOperater.this.num)));
                    if (files[FileOperater.this.num].isDirectory()) {
                        delete = Utils.execute("rm", "-rf", FileOperater.this.operatePath);
                    } else {
                        delete = Utils.execute("rm", FileOperater.this.operatePath);
                    }
                    if (delete) {
                        FileOperater fileOperater = FileOperater.this;
                        fileOperater.num++;
                    } else {
                        FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_ERROR);
                        return;
                    }
                }
                FileOperater.this.handler.sendEmptyMessage(AppConstant.HANDLER_OPERATE_DELETE_SUCCESS);
                if (BoxModel.sModel == 5) {
                    Utils.execute("sync");
                }
            }
        }).start();
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (!file.isDirectory()) {
            return file.length();
        }
        for (File fileSize : file.listFiles()) {
            size += getFileSize(fileSize);
        }
        return size;
    }

    public void stop() {
        this.stop = true;
    }

    public static boolean isChildFile(String path, File[] children) {
        if (children.length > 0) {
            if (children[0].getParent().equals(path)) {
                return true;
            }
            String dirPath = path + "/";
            for (File path2 : children) {
                if (dirPath.startsWith(path2.getPath() + "/")) {
                    return true;
                }
            }
        }
        return false;
    }

    public FileFilter buildFileFilter(int screen, boolean showHidden, boolean apkVisible) {
        FileFilter fileFilter = FileFilterFactory.obtainReadableFilter();
        if (!showHidden) {
            fileFilter = FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainDisHiddenFilter(), true);
        }
        switch (screen) {
            case 1:
                return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainCombinationFilter(FileFilterFactory.obtainSingleFilter(0), FileFilterFactory.obtainSingleFilter(2), false), true);
            case 2:
                return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainCombinationFilter(FileFilterFactory.obtainSingleFilter(0), FileFilterFactory.obtainSingleFilter(1), false), true);
            case 3:
                return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainCombinationFilter(FileFilterFactory.obtainSingleFilter(0), FileFilterFactory.obtainSingleFilter(3), false), true);
            case 4:
                if (apkVisible) {
                    return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainCombinationFilter(FileFilterFactory.obtainSingleFilter(0), FileFilterFactory.obtainSingleFilter(5), false), true);
                }
                return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainSingleFilter(0), true);
            default:
                if (apkVisible) {
                    return fileFilter;
                }
                return FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainCombinationFilter(FileFilterFactory.obtainSingleFilter(0), FileFilterFactory.obtainNotFilter(FileFilterFactory.obtainSingleFilter(5)), false), true);
        }
    }

    public static FileFilter buildBrowsingFileFilter(int type) {
        FileFilter fileFilter = FileFilterFactory.obtainReadableFilter();
        if ((2097152 & type) == 0) {
            fileFilter = FileFilterFactory.obtainCombinationFilter(fileFilter, FileFilterFactory.obtainDisHiddenFilter(), true);
        } else {
            type &= -2097153;
        }
        if (type == 0) {
            return fileFilter;
        }
        FileFilter typeFilter;
        int st = -1;
        boolean multi = false;
        for (int i = 0; i <= 12; i++) {
            if (((1 << i) & type) != 0) {
                if (st == -1) {
                    multi = true;
                    break;
                }
                st = i;
            }
        }
        if (multi) {
            typeFilter = FileFilterFactory.obtainMultiFilter(type | 1);
        } else {
            FileFilter dirFilter = FileFilterFactory.obtainSingleFilter(0);
            if (st == 0) {
                typeFilter = dirFilter;
            } else {
                typeFilter = FileFilterFactory.obtainCombinationFilter(dirFilter, FileFilterFactory.obtainSingleFilter(type), false);
            }
        }
        return FileFilterFactory.obtainCombinationFilter(fileFilter, typeFilter, true);
    }
}
