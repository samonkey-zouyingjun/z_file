package com.zidoo.fileexplorer.task;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import com.zidoo.fileexplorer.bean.StableFile;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;

public class CreateTask extends BaseTask<Result> {
    Dialog dialog;
    boolean isDir;
    String name;
    File parent;

    public static final class Result {
        int code;
        Dialog dialog;
        File file;

        public Result(int code, File file, Dialog dialog) {
            this.code = code;
            this.file = file;
            this.dialog = dialog;
        }

        public int getCode() {
            return this.code;
        }

        public File getFile() {
            return this.file;
        }

        public Dialog getDialog() {
            return this.dialog;
        }
    }

    public CreateTask(Handler handler, int what, Dialog dialog, String name, File parent, boolean isDir) {
        super(handler, what);
        this.dialog = dialog;
        this.name = name;
        this.parent = parent;
        this.isDir = isDir;
    }

    protected Result doInBackground() {
        int code = 0;
        File newFile = new File(this.parent, this.name);
        if (TextUtils.isEmpty(this.name)) {
            code = -12;
        } else if (Utils.characterIllegal(this.name)) {
            code = -13;
        } else if (newFile.exists()) {
            code = -14;
        } else {
            boolean success = false;
            try {
                success = this.isDir ? newFile.mkdirs() : newFile.createNewFile();
                newFile.setReadable(true, false);
                newFile.setWritable(true, false);
                newFile.setExecutable(true, false);
                if (this.parent instanceof StableFile) {
                    newFile = new StableFile(this.parent, this.name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!success) {
                code = -15;
            }
        }
        return new Result(code, newFile, this.dialog);
    }
}
