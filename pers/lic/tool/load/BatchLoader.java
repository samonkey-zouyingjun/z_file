package pers.lic.tool.load;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BatchLoader<Key, Receiver, Param, Result> {
    protected static final int LOAD_SUCCESS = -29764;
    ExecutorService executorService = Executors.newFixedThreadPool(8);
    LoadHanlder hanlder = new LoadHanlder();
    int outTime = 200;
    ArrayList<LoadRunn> tasks = new ArrayList();

    @SuppressLint({"HandlerLeak"})
    private class LoadHanlder extends Handler {
        private LoadHanlder() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BatchLoader.LOAD_SUCCESS /*-29764*/:
                    LoadRunn lr = msg.obj;
                    BatchLoader.this.onPostResult(lr.receiver, lr.result);
                    break;
                default:
                    BatchLoader.this.timeOut(msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private class LoadRunn implements Runnable {
        int id;
        Param param;
        Receiver receiver;
        Result result;
        boolean stop = false;

        LoadRunn(int id, Receiver receiver, Param param) {
            this.id = id;
            this.receiver = receiver;
            this.param = param;
        }

        void stop() {
            this.stop = true;
        }

        public void run() {
            if (!this.stop) {
                Key key = BatchLoader.this.get(this.param);
                if (BatchLoader.this.isValid(key)) {
                    this.result = BatchLoader.this.getResult(key);
                    if (this.result == null) {
                        this.result = BatchLoader.this.load(this.param);
                        if (this.result != null) {
                            BatchLoader.this.saveResult(key, this.result);
                        }
                    }
                }
                if (!this.stop && BatchLoader.this.end(this)) {
                    BatchLoader.this.hanlder.removeMessages(this.id);
                    BatchLoader.this.hanlder.obtainMessage(BatchLoader.LOAD_SUCCESS, this).sendToTarget();
                }
            }
        }
    }

    public abstract void destroy();

    protected abstract Key get(Param param);

    protected abstract Result getResult(Key key);

    protected abstract boolean isValid(Key key);

    protected abstract Result load(Param param);

    protected abstract void onPostResult(Receiver receiver, Result result);

    protected abstract void saveResult(Key key, Result result);

    protected abstract void timeOut(Receiver receiver);

    public void execute(int id, Receiver receiver, Param t) {
        LoadRunn runnable = new LoadRunn(id, receiver, t);
        check(receiver, runnable);
        this.hanlder.removeMessages(id);
        this.hanlder.sendMessageDelayed(this.hanlder.obtainMessage(id, receiver), (long) this.outTime);
        this.executorService.execute(runnable);
    }

    public void setOutTime(int outTime) {
        this.outTime = outTime;
    }

    synchronized void check(Receiver receiver, LoadRunn runn) {
        Iterator<LoadRunn> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            LoadRunn task = (LoadRunn) iterator.next();
            if (task.receiver.equals(receiver)) {
                task.stop();
                iterator.remove();
            }
        }
        this.tasks.add(runn);
    }

    synchronized boolean end(LoadRunn sample) {
        boolean z;
        Iterator<LoadRunn> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            if (((LoadRunn) iterator.next()).equals(sample)) {
                iterator.remove();
                z = true;
                break;
            }
        }
        z = false;
        return z;
    }
}
