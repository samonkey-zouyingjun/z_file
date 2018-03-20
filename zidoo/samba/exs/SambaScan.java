package zidoo.samba.exs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.InputDeviceCompat;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class SambaScan {
    private final int HANDLER_SMB_ADD = 4097;
    private final int HANDLER_SMB_COMPLETE = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    private final int HANDLER_SMB_PROGRESS = InputDeviceCompat.SOURCE_TOUCHSCREEN;
    private final int HANDLER_SMB_QUERY_DB = 4100;
    private final int HANDLER_SMB_START = 4096;
    private ScanHandler mHandler = new ScanHandler(Looper.getMainLooper());
    protected boolean mIsScanning = false;
    private OnRecvMsgListener mOnRecvMsgListener = null;

    @SuppressLint({"HandlerLeak"})
    private final class ScanHandler extends Handler {
        ScanHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4096:
                    SambaScan.this.mOnRecvMsgListener.onStartScan();
                    break;
                case 4097:
                    SambaScan.this.mOnRecvMsgListener.onAdd((SambaDevice) msg.obj);
                    break;
                case InputDeviceCompat.SOURCE_TOUCHSCREEN /*4098*/:
                    SambaScan.this.mOnRecvMsgListener.onProgress(msg.arg1);
                    break;
                case FragmentTransaction.TRANSIT_FRAGMENT_FADE /*4099*/:
                    SambaScan.this.mOnRecvMsgListener.onComplete(((Boolean) msg.obj).booleanValue());
                    break;
                case 4100:
                    SambaScan.this.mOnRecvMsgListener.onSavedSmbDevices((ArrayList) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public abstract void onScan(Context context, ArrayList<SambaDevice> arrayList, boolean z);

    public abstract void stop();

    public void setOnRecvMsgListener(OnRecvMsgListener onRecvMsgListener) {
        this.mOnRecvMsgListener = onRecvMsgListener;
    }

    public OnRecvMsgListener getOnRecvMsgListener() {
        return this.mOnRecvMsgListener;
    }

    public final void scan(Context context, ArrayList<SambaDevice> savedDevices, boolean incomplete) {
        if (savedDevices == null) {
            savedDevices = this.mOnRecvMsgListener.onQuery();
            onQuery(savedDevices);
        }
        onScan(context, savedDevices, incomplete);
    }

    public boolean isScanning() {
        return this.mIsScanning;
    }

    protected final void onStart() {
        this.mHandler.sendEmptyMessage(4096);
    }

    protected final void onAdd(SambaDevice device) {
        this.mHandler.obtainMessage(4097, device).sendToTarget();
    }

    protected final void onProgress(int progress) {
        this.mHandler.obtainMessage(InputDeviceCompat.SOURCE_TOUCHSCREEN, progress, 0).sendToTarget();
    }

    private void onQuery(ArrayList<SambaDevice> devices) {
        this.mHandler.obtainMessage(4100, devices).sendToTarget();
    }

    protected final void onComplete(boolean incomplete) {
        this.mHandler.obtainMessage(FragmentTransaction.TRANSIT_FRAGMENT_FADE, Boolean.valueOf(incomplete)).sendToTarget();
        this.mIsScanning = false;
        System.gc();
    }

    boolean isSavedDevice(SambaDevice device, ArrayList<SambaDevice> savedDevices) {
        Iterator it = savedDevices.iterator();
        while (it.hasNext()) {
            SambaDevice sambaDevice = (SambaDevice) it.next();
            if (sambaDevice.getType() == 4 && device.getIp().equals(sambaDevice.getIp())) {
                return true;
            }
        }
        return false;
    }
}
