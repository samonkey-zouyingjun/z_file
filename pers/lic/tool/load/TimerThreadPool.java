package pers.lic.tool.load;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TimerThreadPool {
    private int count;
    Timer mTimer = null;
    ArrayList<TimerThread> runningTasks;
    boolean stop = false;
    BlockingQueue<TimerRunnable> tasks = new LinkedBlockingDeque();
    private int timeOutSet;

    private class TimeOutCheckTask extends TimerTask {
        private TimeOutCheckTask() {
        }

        public void run() {
            TimerThreadPool.this.checkTimeOut();
        }
    }

    private class TimerThread extends Thread {
        TimerRunnable runnable;
        boolean stop = false;

        TimerThread(TimerRunnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            if (!this.stop) {
                this.runnable.run();
            }
            if (!this.stop) {
                TimerThreadPool.this.submit(this);
            }
        }

        boolean timeOut(int outTimeSet) {
            if (!this.runnable.timeOut(outTimeSet)) {
                return false;
            }
            this.stop = true;
            interrupt();
            return true;
        }

        void cancel() {
            this.runnable.stop();
            this.stop = true;
            interrupt();
        }
    }

    public void execute(TimerRunnable runnable) {
        this.tasks.offer(runnable);
        delayExeute();
    }

    public void reset(int count, int timeOut) {
        this.stop = false;
        this.count = count;
        this.timeOutSet = timeOut;
        this.runningTasks = new ArrayList(count);
        closeTimer();
    }

    public void shutdown() {
        this.stop = true;
        this.tasks.clear();
        synchronized (this) {
            Iterator it = this.runningTasks.iterator();
            while (it.hasNext()) {
                ((TimerThread) it.next()).cancel();
            }
        }
        this.runningTasks.clear();
        closeTimer();
    }

    private synchronized void delayExeute() {
        if (this.mTimer == null) {
            this.mTimer = new Timer();
            this.mTimer.schedule(new TimeOutCheckTask(), 1000, 1000);
        }
        while (this.runningTasks.size() < this.count && !this.stop) {
            TimerRunnable runnable = (TimerRunnable) this.tasks.poll();
            if (runnable == null) {
                break;
            }
            TimerThread timerThread = new TimerThread(runnable);
            this.runningTasks.add(timerThread);
            timerThread.start();
        }
    }

    private synchronized void submit(TimerThread thread) {
        this.runningTasks.remove(thread);
        delayExeute();
        if (this.runningTasks.size() == 0) {
            closeTimer();
        }
    }

    private void closeTimer() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }

    private synchronized void checkTimeOut() {
        if (!this.stop) {
            Iterator<TimerThread> iterator = this.runningTasks.iterator();
            while (iterator.hasNext()) {
                if (((TimerThread) iterator.next()).timeOut(this.timeOutSet)) {
                    iterator.remove();
                }
            }
            delayExeute();
            if (this.runningTasks.size() == 0) {
                closeTimer();
            }
        }
    }
}
