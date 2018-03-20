package zidoo.nfs.scan;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.OnNfsSearchListener;

public abstract class NfsScan {
    private InnerHandler handler = new InnerHandler(Looper.getMainLooper());
    private OnNfsSearchListener onNfsSearchListener = null;

    private class InnerHandler extends Handler {
        static final int ADD = 1;
        static final int COMPLETE = 3;
        static final int PROGRESS = 2;
        static final int START = 0;

        public InnerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (NfsScan.this.onNfsSearchListener != null) {
                switch (msg.what) {
                    case 0:
                        NfsScan.this.onNfsSearchListener.onNfsScanStart(msg.arg1);
                        break;
                    case 1:
                        NfsScan.this.onNfsSearchListener.OnNFSDeviceAddListener((NfsDevice) msg.obj);
                        break;
                    case 2:
                        NfsScan.this.onNfsSearchListener.onNfsDeveceChangeListener(msg.arg1);
                        break;
                    case 3:
                        NfsScan.this.onNfsSearchListener.onCompleteListener(msg.arg1, ((Boolean) msg.obj).booleanValue());
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    public abstract boolean isScanning();

    public abstract void start();

    public abstract void stop();

    public void setOnNfsSearchListener(OnNfsSearchListener onNfsSearchListener) {
        this.onNfsSearchListener = onNfsSearchListener;
    }

    void onStart(int mode) {
        this.handler.obtainMessage(0, mode, 0).sendToTarget();
    }

    void onProgress(int progress) {
        this.handler.obtainMessage(2, progress, 0).sendToTarget();
    }

    void onAdd(NfsDevice device) {
        this.handler.obtainMessage(1, device).sendToTarget();
    }

    void onComplete(int mode, boolean success) {
        this.handler.obtainMessage(3, mode, 0, Boolean.valueOf(success)).sendToTarget();
    }
}
