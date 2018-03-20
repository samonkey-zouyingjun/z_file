package com.zidoo.fileexplorer.task;

import android.os.Handler;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;
import java.io.FileFilter;

public class OpenDirTask extends BaseTask<Result> {
    File extra;
    File file;
    FileFilter fileFilter;
    int tag;
    ListType type;

    public static final class Result {
        File[] children;
        File extra;
        File parent;
        int tag;

        public Result(int tag, File parent, File extra, File[] children) {
            this.tag = tag;
            this.parent = parent;
            this.extra = extra;
            this.children = children;
        }

        public int getTag() {
            return this.tag;
        }

        public File getParent() {
            return this.parent;
        }

        public File getExtra() {
            return this.extra;
        }

        public File[] getChildren() {
            return this.children;
        }
    }

    public OpenDirTask(Handler handler, int what, int tag, File file, File extra, FileFilter fileFilter, ListType type) {
        super(handler, what);
        this.tag = tag;
        this.file = file;
        this.extra = extra;
        this.fileFilter = fileFilter;
        this.type = type;
    }

    protected Result doInBackground() {
        File parent = this.tag == 1 ? this.file.getParentFile() : this.file;
        File[] children = parent.listFiles(this.fileFilter);
        if (children != null) {
            Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
        }
        return new Result(this.tag, parent, this.extra, children);
    }
}
