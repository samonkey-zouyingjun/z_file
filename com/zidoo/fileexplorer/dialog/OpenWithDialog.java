package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.task.FastIdentifyTask;
import com.zidoo.fileexplorer.tool.SoundTool;
import java.io.File;

public class OpenWithDialog extends Dialog {
    File file;
    File[] files;
    int position;
    FastIdentifyTask task;

    public OpenWithDialog(Context context, File[] files, int position) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_open_with);
        this.files = files;
        this.position = position;
    }

    public OpenWithDialog(Context context, File file) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_open_with);
        this.file = file;
    }

    public OpenWithDialog(Context context, FastIdentifyTask task) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_open_with);
        this.task = task;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SoundTool.soundKey(keyCode);
        return super.onKeyDown(keyCode, event);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setType(2003);
        LayoutParams lp = window.getAttributes();
        lp.width = -2;
        lp.height = -2;
        findViewById(R.id.bt_play).requestFocus();
    }

    public File[] getFiles() {
        return this.files;
    }

    public int getPosition() {
        return this.position;
    }

    public File getFile() {
        if (this.files != null) {
            return this.files[this.position];
        }
        return this.file;
    }

    public void setOpenWith(int way) {
        if (this.task != null) {
            this.task.setOpenWith(way);
        }
    }

    public boolean isTask() {
        return this.task != null;
    }

    public void cancel() {
        setOpenWith(0);
        super.cancel();
    }

    public void dismiss() {
        super.dismiss();
        if (this.task != null) {
            synchronized (this.task) {
                this.task.notify();
            }
        }
    }
}
