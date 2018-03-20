package com.zidoo.fileexplorer.task;

import android.os.Handler;

public abstract class BaseTask<Result> extends Thread {
    private Handler handler;
    private boolean stop = false;
    private int what;

    protected abstract Result doInBackground();

    public BaseTask(Handler handler, int what) {
        this.handler = handler;
        this.what = what;
    }

    public void cancel() {
        this.stop = true;
        interrupt();
    }

    public boolean isStop() {
        return this.stop;
    }

    public final void run() {
        Result result = doInBackground();
        if (!(this.stop || isInterrupted())) {
            this.handler.obtainMessage(this.what, result).sendToTarget();
        }
        this.stop = true;
    }
}
