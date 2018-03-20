package pers.lic.tool.load;

import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public abstract class FileScanner<Media> {
    private int MAX_THREAD_NUMBER = 8;
    private ArrayList<Media> cms = new ArrayList();
    private float cp = 0.0f;
    private boolean scanning = false;
    private HashSet<ScanThread> threads = new HashSet();
    private Timer timer = null;

    private class FileTask {
        File dir;
        float proportion;

        public FileTask(float proportion, File dir) {
            this.proportion = proportion;
            this.dir = dir;
        }
    }

    private class ProgressTask extends TimerTask {
        private ProgressTask() {
        }

        public void run() {
            if (FileScanner.this.scanning) {
                float p = FileScanner.this.cp;
                int number = FileScanner.this.cms.size();
                if (FileScanner.this.threads.size() > 0) {
                    try {
                        Iterator it = FileScanner.this.threads.iterator();
                        while (it.hasNext()) {
                            ScanThread thread = (ScanThread) it.next();
                            p += thread.progress;
                            number += thread.medias.size();
                        }
                    } catch (Exception e) {
                        Log.e("FileScanner", "Compute progress error", e);
                    }
                }
                if (p <= 1.0f) {
                    FileScanner.this.onProgress(number, p);
                }
            }
        }
    }

    private class ScanThread extends Thread {
        private Stack<FileTask> fileStack = new Stack();
        private ArrayList<Media> medias = new ArrayList();
        private float progress;
        private boolean stop = false;
        private FileTask task;

        public ScanThread(FileTask task) {
            this.task = task;
        }

        public void run() {
            do {
                File[] files = this.task.dir.listFiles();
                if (files == null || files.length <= 0) {
                    this.progress += this.task.proportion;
                } else {
                    float p = this.task.proportion;
                    float percent = p / ((float) (files.length + 1));
                    for (File file : files) {
                        Media media = FileScanner.this.isMedia(file);
                        if (media != null) {
                            this.medias.add(media);
                            FileScanner.this.onFind(media);
                        } else if (file.isDirectory()) {
                            p -= percent;
                            this.fileStack.add(new FileTask(percent, file));
                        }
                    }
                    this.progress += p;
                }
                this.task = fission();
                if (this.task == null) {
                    break;
                }
            } while (!this.stop);
            FileScanner.this.endOne(this);
        }

        public void cancel() {
            this.stop = true;
            try {
                interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private FileTask fission() {
            FileTask task = null;
            if (this.fileStack.isEmpty()) {
                try {
                    synchronized (FileScanner.this.threads) {
                        Iterator it = FileScanner.this.threads.iterator();
                        while (it.hasNext()) {
                            task = ((ScanThread) it.next()).getDirectory();
                            if (task != null) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                task = getDirectory();
                if (FileScanner.this.threads.size() < FileScanner.this.MAX_THREAD_NUMBER) {
                    do {
                        FileTask fileTask = getDirectory();
                        if (fileTask == null) {
                            break;
                        }
                        FileScanner.this.startOne(fileTask);
                    } while (FileScanner.this.threads.size() < FileScanner.this.MAX_THREAD_NUMBER);
                }
            }
            return task;
        }

        public FileTask getDirectory() {
            return this.fileStack.isEmpty() ? null : (FileTask) this.fileStack.pop();
        }
    }

    protected abstract Media isMedia(File file);

    protected abstract void onProgress(int i, float f);

    protected abstract void onScanComplete(ArrayList<Media> arrayList);

    public void start(File dir) {
        this.scanning = true;
        startOne(new FileTask(1.0f, dir));
        this.timer = new Timer();
        this.timer.schedule(new ProgressTask(), 60, 60);
    }

    public void cancel() {
        this.scanning = false;
        synchronized (this.threads) {
            Iterator it = this.threads.iterator();
            while (it.hasNext()) {
                ((ScanThread) it.next()).cancel();
            }
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public boolean isScanning() {
        return this.scanning;
    }

    protected void onFind(Media media) {
    }

    private void startOne(FileTask task) {
        synchronized (this.threads) {
            ScanThread thread = new ScanThread(task);
            this.threads.add(thread);
            thread.start();
        }
    }

    private void endOne(ScanThread thread) {
        synchronized (this.threads) {
            this.threads.remove(thread);
            this.cp += thread.progress;
            this.cms.addAll(thread.medias);
            if (this.threads.size() == 0) {
                this.scanning = false;
                onProgress(this.cms.size(), 1.0f);
                onScanComplete(this.cms);
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                }
            }
        }
    }
}
