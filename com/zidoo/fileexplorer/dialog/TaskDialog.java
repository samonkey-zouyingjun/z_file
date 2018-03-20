package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.task.BaseTask;
import com.zidoo.fileexplorer.tool.SoundTool;

public class TaskDialog extends Dialog {
    BaseTask<?> mBaseTask = null;
    boolean mHide = false;

    public TaskDialog(Context context) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_load);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SoundTool.soundKey(keyCode);
        return super.onKeyDown(keyCode, event);
    }

    public void cancel() {
        super.cancel();
        if (!this.mHide && this.mBaseTask != null) {
            this.mBaseTask.cancel();
            this.mBaseTask = null;
        }
    }

    public void setTask(BaseTask<?> task) {
        this.mBaseTask = task;
    }

    public void hide() {
        this.mHide = true;
        dismiss();
    }

    public void show() {
        this.mHide = false;
        super.show();
    }
}
