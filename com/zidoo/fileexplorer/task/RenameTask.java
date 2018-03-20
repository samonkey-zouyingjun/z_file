package com.zidoo.fileexplorer.task;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import com.zidoo.fileexplorer.bean.StableFile;
import com.zidoo.fileexplorer.tool.Utils;
import java.io.File;

public class RenameTask extends BaseTask<Result> {
    Dialog dialog;
    String name;
    File parent;
    File src;

    public static final class Result {
        int code;
        Dialog dialog;

        public Result(int code, Dialog dialog) {
            this.code = code;
            this.dialog = dialog;
        }

        public int getCode() {
            return this.code;
        }

        public Dialog getDialog() {
            return this.dialog;
        }
    }

    public RenameTask(Handler handler, int what, Dialog dialog, File src, File parent, String name) {
        super(handler, what);
        this.dialog = dialog;
        this.src = src;
        this.parent = parent;
        this.name = name;
    }

    protected Result doInBackground() {
        int code = -15;
        if (TextUtils.isEmpty(this.name)) {
            code = -12;
        } else if (Utils.characterIllegal(this.name)) {
            code = -13;
        } else {
            File newFile = this.parent instanceof StableFile ? new StableFile(this.parent, this.name) : new File(this.parent, this.name);
            if (newFile.exists()) {
                code = -14;
            } else {
                try {
                    if (this.src.renameTo(newFile)) {
                        code = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new Result(code, this.dialog);
    }
}
