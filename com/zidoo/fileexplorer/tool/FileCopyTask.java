package com.zidoo.fileexplorer.tool;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import zidoo.model.BoxModel;

public class FileCopyTask implements Runnable {
    final int COPY_END = 4;
    final int COPY_INFO = 3;
    final int CUT_KEEP = 256;
    final int DELAY_SHOW_DIALOG = 0;
    final int DIR_EXIST = 7;
    final int FILE_EXIST = 5;
    final int FILE_NAME = 6;
    final int INFO_INIT = 1;
    final int INFO_NEW_FILE_START_COPY = 2;
    final int RESULT_CANCEL = 48;
    final int RESULT_FAIL = 16;
    final int RESULT_SPACE_NOT_ENOUGH = 32;
    final int RESULT_SUCCESS = 1;
    final int RESULT_SUCCESS_KEEP = 257;
    boolean mCanCut = false;
    Context mContext;
    long mCopiedSize;
    Dialog mCopyDirExistDialog = null;
    Dialog mCopyFileExistDialog = null;
    CopyInfo mCopyInfo;
    int mCopyingFileIndex;
    boolean mCut = false;
    File mDir;
    int mDirWay;
    int mFileWay;
    File[] mFiles;
    Handler mHandler;
    boolean mKeepDir;
    boolean mKeepFile;
    long mLastInfoTime = 0;
    OnClickListener mOnClickListener;
    OnCopyListener mOnCopyListener = null;
    boolean mStop = false;
    long mTotalSize;

    class CopyInfo {
        TextView currentSize;
        Dialog dialog;
        Button end;
        TextView name;
        ProgressBar progressBar;
        TextView title;
        TextView totalSize;

        CopyInfo() {
        }
    }

    public interface OnCopyListener {
        void onEnd(int i, boolean z);
    }

    public FileCopyTask(Context context) {
        this.mContext = context;
        init();
    }

    public void setTask(File dir, File[] files, boolean cut, boolean canCut) {
        this.mDir = dir;
        this.mFiles = files;
        this.mCut = cut;
        this.mCanCut = canCut;
        this.mCopiedSize = 0;
        this.mStop = false;
        this.mKeepFile = false;
        this.mKeepDir = false;
        initCopyInfoDialog();
    }

    public void setOnCopyListener(OnCopyListener onCopyListener) {
        this.mOnCopyListener = onCopyListener;
    }

    public void run() {
        synchronized (this) {
            this.mHandler.sendEmptyMessageDelayed(0, 500);
            this.mTotalSize = computeTotalSize(this.mFiles);
            this.mHandler.sendEmptyMessage(1);
            int length = this.mFiles.length;
            int resutl = 0;
            this.mCopyingFileIndex = 0;
            while (this.mCopyingFileIndex < length && !this.mStop) {
                try {
                    File srcFile = this.mFiles[this.mCopyingFileIndex];
                    File desFile = new File(this.mDir, srcFile.getName());
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(2, srcFile.getPath()));
                    resutl = (this.mCut && this.mCanCut) ? cutForld(srcFile, desFile) : copyForld(srcFile, desFile, this.mCut);
                    if ((resutl & 1) != 0) {
                        this.mCopyingFileIndex++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.mHandler.obtainMessage(4, Integer.valueOf(resutl)).sendToTarget();
            if (BoxModel.sModel == 5) {
                Utils.execute("sync");
            }
        }
    }

    private void init() {
        this.mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (!FileCopyTask.this.mCopyInfo.dialog.isShowing()) {
                            FileCopyTask.this.mCopyInfo.dialog.show();
                            return;
                        }
                        return;
                    case 1:
                        FileCopyTask.this.mCopyInfo.currentSize.setText(Utils.formatFileSize(FileCopyTask.this.mCopiedSize));
                        FileCopyTask.this.mCopyInfo.totalSize.setText("/" + Utils.formatFileSize(FileCopyTask.this.mTotalSize));
                        return;
                    case 2:
                        FileCopyTask.this.mCopyInfo.name.setText((CharSequence) msg.obj);
                        return;
                    case 3:
                        FileCopyTask.this.mCopyInfo.currentSize.setText(Utils.formatFileSize(FileCopyTask.this.mCopiedSize));
                        FileCopyTask.this.mCopyInfo.progressBar.setProgress((int) ((((float) FileCopyTask.this.mCopiedSize) / ((float) FileCopyTask.this.mTotalSize)) * 1000.0f));
                        return;
                    case 4:
                        FileCopyTask.this.mHandler.removeMessages(0);
                        FileCopyTask.this.end(((Integer) msg.obj).intValue());
                        return;
                    case 5:
                        FileCopyTask.this.mHandler.removeMessages(0);
                        if (!FileCopyTask.this.mCopyInfo.dialog.isShowing()) {
                            FileCopyTask.this.mCopyInfo.dialog.show();
                        }
                        File[] files = (File[]) msg.obj;
                        FileCopyTask.this.showFileExistDialog(files[0], files[1]);
                        return;
                    case 6:
                        FileCopyTask.this.mCopyInfo.name.setText((CharSequence) msg.obj);
                        return;
                    case 7:
                        FileCopyTask.this.mHandler.removeMessages(0);
                        if (!FileCopyTask.this.mCopyInfo.dialog.isShowing()) {
                            FileCopyTask.this.mCopyInfo.dialog.show();
                        }
                        File[] dirs = (File[]) msg.obj;
                        FileCopyTask.this.showDirExistDialog(dirs[0], dirs[1]);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mOnClickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.linear_dir_skip:
                        FileCopyTask.this.mDirWay = 2;
                        FileCopyTask.this.mCopyDirExistDialog.dismiss();
                        return;
                    case R.id.linear_merge:
                        FileCopyTask.this.mDirWay = 1;
                        FileCopyTask.this.mCopyDirExistDialog.dismiss();
                        return;
                    case R.id.linear_replace:
                        FileCopyTask.this.mFileWay = 1;
                        FileCopyTask.this.mCopyFileExistDialog.dismiss();
                        return;
                    case R.id.linear_reserve:
                        FileCopyTask.this.mFileWay = 3;
                        FileCopyTask.this.mCopyFileExistDialog.dismiss();
                        return;
                    case R.id.linear_skip:
                        FileCopyTask.this.mFileWay = 2;
                        FileCopyTask.this.mCopyFileExistDialog.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
    }

    private void initCopyInfoDialog() {
        if (this.mCopyInfo == null) {
            this.mCopyInfo = new CopyInfo();
            final Dialog dialog = new Dialog(this.mContext, R.style.defaultDialog);
            dialog.setContentView(R.layout.dialog_copy_info);
            this.mCopyInfo.dialog = dialog;
            this.mCopyInfo.title = (TextView) dialog.findViewById(R.id.tv_operate_title);
            this.mCopyInfo.name = (TextView) dialog.findViewById(R.id.tv_current_operate_file);
            this.mCopyInfo.currentSize = (TextView) dialog.findViewById(R.id.tv_current_file_size);
            this.mCopyInfo.totalSize = (TextView) dialog.findViewById(R.id.tv_total_size);
            this.mCopyInfo.progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
            this.mCopyInfo.end = (Button) dialog.findViewById(R.id.bt_end);
            if (this.mCut) {
                this.mCopyInfo.title.setText(this.mContext.getString(R.string.move_file));
            }
            this.mCopyInfo.end.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    FileCopyTask.this.stopRun();
                }
            });
            return;
        }
        this.mCopyInfo.title.setTextColor(this.mContext.getResources().getColorStateList(R.color.copy_title));
        this.mCopyInfo.title.setText(this.mContext.getString(this.mCut ? R.string.move_file : R.string.copy_file));
        this.mCopyInfo.name.setText("");
        this.mCopyInfo.progressBar.setProgress(0);
        this.mCopyInfo.progressBar.setSecondaryProgress(0);
        this.mCopyInfo.currentSize.setText("");
        this.mCopyInfo.totalSize.setText(this.mContext.getString(R.string.computing));
        this.mCopyInfo.end.setText(this.mContext.getString(R.string.cancel));
    }

    @SuppressLint({"SimpleDateFormat"})
    private void showFileExistDialog(File srcFile, File desFile) {
        if (this.mCopyFileExistDialog == null) {
            final Dialog dialog = new Dialog(this.mContext, R.style.defaultDialog);
            dialog.setContentView(R.layout.dialog_copy_file_exist);
            this.mCopyFileExistDialog = dialog;
            dialog.findViewById(R.id.linear_replace).setOnClickListener(this.mOnClickListener);
            dialog.findViewById(R.id.linear_skip).setOnClickListener(this.mOnClickListener);
            dialog.findViewById(R.id.linear_reserve).setOnClickListener(this.mOnClickListener);
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface d) {
                    if (((CheckBox) dialog.findViewById(R.id.cb_save_operate)).isChecked()) {
                        FileCopyTask.this.mKeepFile = true;
                    }
                    synchronized (FileCopyTask.this) {
                        FileCopyTask.this.notify();
                    }
                }
            });
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LinearLayout srcLayout = (LinearLayout) this.mCopyFileExistDialog.findViewById(R.id.linear_file_src);
        ((ImageView) srcLayout.findViewById(R.id.img_replace_file_icon)).setImageResource(FileOperater.getFileIconResource(srcFile));
        ((TextView) srcLayout.findViewById(R.id.tv_replace_name)).setText(this.mContext.getString(R.string.c_name, new Object[]{srcFile.getName()}));
        ((TextView) srcLayout.findViewById(R.id.tv_replace_size)).setText(this.mContext.getString(R.string.c_size, new Object[]{Utils.formatFileSize(srcFile.length())}));
        ((TextView) srcLayout.findViewById(R.id.tv_replace_date)).setText(this.mContext.getString(R.string.c_date, new Object[]{dateFormat.format(Long.valueOf(srcFile.lastModified()))}));
        LinearLayout desLayout = (LinearLayout) this.mCopyFileExistDialog.findViewById(R.id.linear_file_dec);
        ((ImageView) desLayout.findViewById(R.id.img_skip_file_icon)).setImageResource(FileOperater.getFileIconResource(desFile));
        ((TextView) desLayout.findViewById(R.id.tv_skip_name)).setText(this.mContext.getString(R.string.c_name, new Object[]{desFile.getName()}));
        ((TextView) desLayout.findViewById(R.id.tv_skip_size)).setText(this.mContext.getString(R.string.c_size, new Object[]{Utils.formatFileSize(desFile.length())}));
        ((TextView) desLayout.findViewById(R.id.tv_skip_date)).setText(this.mContext.getString(R.string.c_date, new Object[]{dateFormat.format(Long.valueOf(desFile.lastModified()))}));
        String name = setRename(desFile);
        if (name.length() > 25) {
            name = ".." + name.substring(name.length() - 25);
        }
        if (this.mCut) {
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_operate_title)).setText(R.string.move_file);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_replace_msg)).setText(R.string.replace_msg_cut);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_skip_msg)).setText(R.string.skip_msg_cut);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_reserve_title)).setText(R.string.save_cut);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_reserve_msg)).setText(this.mContext.getString(R.string.save_msg_cut, new Object[]{name}));
        } else {
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_operate_title)).setText(R.string.copy_file);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_replace_msg)).setText(R.string.replace_msg);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_skip_msg)).setText(R.string.skip_msg);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_reserve_title)).setText(R.string.save);
            ((TextView) this.mCopyFileExistDialog.findViewById(R.id.tv_reserve_msg)).setText(this.mContext.getString(R.string.save_msg, new Object[]{name}));
        }
        this.mCopyFileExistDialog.show();
        this.mCopyFileExistDialog.findViewById(R.id.linear_replace).requestFocus();
    }

    private String setRename(File desFile) {
        String name = desFile.getName();
        int p = name.lastIndexOf(".");
        String before = "";
        String ext = "";
        if (p > 0) {
            before = name.substring(0, p);
            ext = name.substring(p);
        } else {
            before = name;
        }
        int num = 1;
        String newName = "";
        while (true) {
            int num2 = num + 1;
            newName = before + "(" + num + ")" + ext;
            if (!new File(desFile.getParent(), newName).exists()) {
                return newName;
            }
            num = num2;
        }
    }

    @SuppressLint({"SimpleDateFormat"})
    private void showDirExistDialog(File srcDir, File desDir) {
        if (this.mCopyDirExistDialog == null) {
            final Dialog dialog = new Dialog(this.mContext, R.style.defaultDialog);
            dialog.setContentView(R.layout.dialog_copy_dir_exist);
            this.mCopyDirExistDialog = dialog;
            dialog.findViewById(R.id.linear_merge).setOnClickListener(this.mOnClickListener);
            dialog.findViewById(R.id.linear_dir_skip).setOnClickListener(this.mOnClickListener);
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface d) {
                    if (((CheckBox) dialog.findViewById(R.id.cb_save_operate)).isChecked()) {
                        FileCopyTask.this.mKeepDir = true;
                    }
                    synchronized (FileCopyTask.this) {
                        FileCopyTask.this.notify();
                    }
                }
            });
        }
        if (this.mCut) {
            ((TextView) this.mCopyDirExistDialog.findViewById(R.id.tv_operate_title)).setText(R.string.move_file);
            ((TextView) this.mCopyDirExistDialog.findViewById(R.id.tv_skip_msg)).setText(R.string.skip_msg_cut);
        } else {
            ((TextView) this.mCopyDirExistDialog.findViewById(R.id.tv_operate_title)).setText(R.string.copy_file);
            ((TextView) this.mCopyDirExistDialog.findViewById(R.id.tv_skip_msg)).setText(R.string.skip_msg);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        LinearLayout srcLayout = (LinearLayout) this.mCopyDirExistDialog.findViewById(R.id.linear_dir_src);
        ((TextView) ((LinearLayout) srcLayout.getChildAt(1)).getChildAt(0)).setText(this.mContext.getString(R.string.c_name, new Object[]{srcDir.getName()}));
        ((TextView) ((LinearLayout) srcLayout.getChildAt(1)).getChildAt(1)).setText(this.mContext.getString(R.string.c_date, new Object[]{dateFormat.format(Long.valueOf(srcDir.lastModified()))}));
        LinearLayout desLayout = (LinearLayout) this.mCopyDirExistDialog.findViewById(R.id.linear_dir_des);
        ((TextView) ((LinearLayout) desLayout.getChildAt(1)).getChildAt(0)).setText(this.mContext.getString(R.string.c_name, new Object[]{desDir.getName()}));
        ((TextView) ((LinearLayout) desLayout.getChildAt(1)).getChildAt(1)).setText(this.mContext.getString(R.string.c_date, new Object[]{dateFormat.format(Long.valueOf(desDir.lastModified()))}));
        this.mCopyDirExistDialog.show();
        this.mCopyDirExistDialog.findViewById(R.id.linear_merge).requestFocus();
    }

    private void end(int type) {
        switch (type) {
            case 1:
                break;
            case 16:
                this.mCopyInfo.title.setTextColor(SupportMenu.CATEGORY_MASK);
                ProgressBar pf = this.mCopyInfo.progressBar;
                pf.setSecondaryProgress(pf.getProgress());
                pf.setProgress(0);
                this.mCopyInfo.title.setText(this.mContext.getString(this.mCut ? R.string.move_filed : R.string.copy_filed));
                this.mCopyInfo.end.setText(this.mContext.getString(R.string.ok));
                if (!this.mCopyInfo.dialog.isShowing()) {
                    this.mCopyInfo.dialog.show();
                    break;
                }
                break;
            case 32:
                ProgressBar pc = this.mCopyInfo.progressBar;
                pc.setSecondaryProgress(pc.getProgress());
                pc.setProgress(0);
                this.mCopyInfo.title.setTextColor(SupportMenu.CATEGORY_MASK);
                this.mCopyInfo.title.setText(this.mContext.getString(R.string.space_not_enough));
                this.mCopyInfo.end.setText(this.mContext.getString(R.string.ok));
                if (!this.mCopyInfo.dialog.isShowing()) {
                    this.mCopyInfo.dialog.show();
                    break;
                }
                break;
            case 48:
                if (this.mCopyInfo.dialog.isShowing()) {
                    this.mCopyInfo.dialog.dismiss();
                    break;
                }
                break;
            case 257:
                type = 1;
                break;
        }
        this.mCopyInfo.title.setText(this.mContext.getString(this.mCut ? R.string.operate_complete : R.string.copy_success));
        if (this.mCopyInfo.dialog.isShowing()) {
            this.mCopyInfo.dialog.dismiss();
        }
        if (this.mOnCopyListener != null) {
            this.mOnCopyListener.onEnd(type, this.mCut);
        }
    }

    public void stopRun() {
        this.mStop = true;
        if (this.mCopyInfo != null && this.mCopyInfo.dialog.isShowing()) {
            this.mCopyInfo.dialog.dismiss();
        }
    }

    private int copyForld(File srcFile, File desFile, boolean cut) throws InterruptedException {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, srcFile.getPath()));
        if (srcFile.isDirectory()) {
            int i;
            if (desFile.exists()) {
                if (!this.mKeepDir) {
                    this.mDirWay = 0;
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(7, new File[]{srcFile, desFile}));
                    wait();
                }
                switch (this.mDirWay) {
                    case 0:
                        return 48;
                    case 2:
                        this.mCopiedSize += getFileSize(srcFile);
                        this.mHandler.sendEmptyMessage(3);
                        return 257;
                }
            } else if (!desFile.mkdirs()) {
                return 16;
            }
            boolean keep = false;
            for (File file : srcFile.listFiles()) {
                int result = copyForld(file, new File(desFile, file.getName()), cut);
                if ((result & 1) == 0) {
                    return result;
                }
                if (result == 257) {
                    i = 1;
                } else {
                    i = 0;
                }
                keep |= i;
            }
            i = cut ? keep ? 257 : srcFile.delete() ? 1 : 16 : 1;
            return i;
        }
        if (desFile.exists()) {
            if (!this.mKeepFile) {
                this.mFileWay = 0;
                this.mHandler.sendMessage(this.mHandler.obtainMessage(5, new File[]{srcFile, desFile}));
                wait();
            }
            switch (this.mFileWay) {
                case 0:
                    return 48;
                case 2:
                    this.mCopiedSize += srcFile.length();
                    this.mHandler.sendEmptyMessage(3);
                    return 257;
                case 3:
                    desFile = new File(desFile.getParent(), setRename(desFile));
                    break;
            }
        }
        return copyFile(srcFile, desFile, cut);
    }

    private int copyFile(File srcFile, File desFile, boolean cut) {
        try {
            long length = srcFile.length();
            if (length > this.mDir.getUsableSpace()) {
                return 32;
            }
            FileInputStream in = new FileInputStream(srcFile);
            FileOutputStream out = new FileOutputStream(desFile);
            long percent = (long) (((float) length) * 0.1f);
            byte[] b = new byte[(percent < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID ? 1024 : (int) Math.min(percent, 41943040))];
            while (true) {
                int len = in.read(b);
                if (len == -1 || this.mStop) {
                    out.flush();
                    in.close();
                    out.close();
                } else {
                    out.write(b, 0, len);
                    this.mCopiedSize += (long) len;
                    if (System.currentTimeMillis() - this.mLastInfoTime > 500) {
                        this.mHandler.sendEmptyMessage(3);
                    }
                }
            }
            out.flush();
            in.close();
            out.close();
            if (this.mStop) {
                return 48;
            }
            return (!cut || srcFile.delete()) ? 1 : 16;
        } catch (IOException e) {
            MyLog.e("Copy file exception!", e);
            return 16;
        }
    }

    private int cutForld(File srcFile, File desFile) throws InterruptedException {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, srcFile.getPath()));
        if (this.mStop) {
            return 48;
        }
        if (desFile.exists()) {
            if (!srcFile.isDirectory()) {
                if (!this.mKeepFile) {
                    this.mFileWay = 0;
                    this.mHandler.sendMessage(this.mHandler.obtainMessage(5, new File[]{srcFile, desFile}));
                    wait();
                }
                switch (this.mFileWay) {
                    case 0:
                        return 48;
                    case 1:
                        break;
                    case 2:
                        this.mCopiedSize += srcFile.length();
                        this.mHandler.sendEmptyMessage(3);
                        return 257;
                    case 3:
                        desFile = new File(desFile.getParent(), setRename(desFile));
                        break;
                    default:
                        break;
                }
            }
            if (!this.mKeepDir) {
                this.mDirWay = 0;
                this.mHandler.sendMessage(this.mHandler.obtainMessage(7, new File[]{srcFile, desFile}));
                wait();
            }
            switch (this.mDirWay) {
                case 0:
                    return 48;
                case 1:
                    boolean keep = false;
                    for (File file : srcFile.listFiles()) {
                        int result = cutForld(file, new File(desFile, file.getName()));
                        if ((result & 1) == 0) {
                            return result;
                        }
                        int i;
                        if (result == 257) {
                            i = 1;
                        } else {
                            i = 0;
                        }
                        keep |= i;
                    }
                    if (keep) {
                        return 257;
                    }
                    return srcFile.delete() ? 1 : 16;
                case 2:
                    this.mCopiedSize += getFileSize(srcFile);
                    this.mHandler.sendEmptyMessage(3);
                    return 257;
            }
        }
        if (srcFile.renameTo(desFile)) {
            return 1;
        }
        return 16;
    }

    private long computeTotalSize(File[] files) {
        long size = 0;
        for (File fileSize : files) {
            size += getFileSize(fileSize);
        }
        return size;
    }

    private long getFileSize(File file) {
        long size = 0;
        if (!file.isDirectory()) {
            return file.length();
        }
        File[] files = file.listFiles();
        for (int i = 0; i < (files == null ? 0 : files.length); i++) {
            size += getFileSize(files[i]);
        }
        return size;
    }
}
