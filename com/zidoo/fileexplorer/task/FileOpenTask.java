package com.zidoo.fileexplorer.task;

import android.os.Handler;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import zidoo.file.FileType;
import zidoo.model.BoxModel;

public class FileOpenTask extends BaseTask<Result> {
    private BrowseInfo browser;
    private FileFilter fileFilter;
    private File[] files;
    private int position;
    private ListType type;

    public static final class Result {
        String audioPath;
        int code;
        File file;
        String filePath;
        File[] files;
        int position;

        Result(int code) {
            this.code = code;
        }

        Result(int code, File file) {
            this.code = code;
            this.file = file;
        }

        Result(int code, File[] files, int position) {
            this.code = code;
            this.files = files;
            this.position = position;
        }

        Result(int code, File file, File[] files) {
            this.code = code;
            this.file = file;
            this.files = files;
        }

        Result(int code, String filePath, String audioPath) {
            this.code = code;
            this.filePath = filePath;
            this.audioPath = audioPath;
        }

        public int getCode() {
            return this.code;
        }

        public File getFile() {
            return this.file;
        }

        public File[] getFiles() {
            return this.files;
        }

        public String getFilePath() {
            return this.filePath;
        }

        public String getAudioPath() {
            return this.audioPath;
        }

        public int getPosition() {
            return this.position;
        }

        public String toString() {
            return "Result [code=" + this.code + ", file=" + this.file + ", files=" + Arrays.toString(this.files) + ", filePath=" + this.filePath + ", audioPath=" + this.audioPath + ", position=" + this.position + "]";
        }
    }

    public FileOpenTask(Handler handler, int what, File[] files, ListType type, FileFilter fileFilter, int position, BrowseInfo browser) {
        super(handler, what);
        this.files = files;
        this.type = type;
        this.fileFilter = fileFilter;
        this.position = position;
        this.browser = browser;
    }

    protected Result doInBackground() {
        File file = this.files[this.position];
        if (file.isDirectory()) {
            boolean open = this.browser == null || (this.browser.getClickModel() & 8) != 0;
            if (!open || !FileType.isBDMV(file.getPath())) {
                File[] children = null;
                if (!isStop()) {
                    children = file.listFiles(this.fileFilter);
                }
                if (children != null) {
                    Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                }
                return new Result(12, file, children);
            } else if (BoxModel.sModel == 2) {
                return new Result(10, file);
            } else {
                return new Result(11, this.files, this.position);
            }
        }
        boolean openable = true;
        boolean selectable = false;
        if (this.browser != null) {
            if ((this.browser.getClickModel() & 4) != 0) {
                openable = true;
            } else {
                openable = false;
            }
            if ((this.browser.getClickModel() & 1) != 0) {
                selectable = true;
            } else {
                selectable = false;
            }
        }
        String cue;
        if (selectable) {
            if (!openable) {
                return new Result(16);
            }
            cue = FileType.isCUE(file.getPath());
            if (cue != null) {
                return new Result(16, file.getPath(), cue);
            }
            return new Result(16, this.files, this.position);
        } else if (!openable) {
            return new Result(15);
        } else {
            cue = FileType.isCUE(file.getPath());
            if (cue != null) {
                return new Result(13, file.getPath(), cue);
            }
            return new Result(14, this.files, this.position);
        }
    }
}
