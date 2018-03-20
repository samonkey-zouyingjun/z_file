package com.zidoo.fileexplorer.service;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.FastIdentifier;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.dialog.OpenWithDialog;
import com.zidoo.fileexplorer.main.HomeActivity;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.permissions.ZidooBoxPermissions;
import java.io.File;
import zidoo.browse.BrowseConstant;
import zidoo.browse.BrowseUtils;
import zidoo.browse.FileIdentifier;
import zidoo.browse.IdentifyResult;
import zidoo.file.FileType;
import zidoo.model.BoxModel;

public class ShortcutBroadcast extends BroadcastReceiver {

    public class IdentifyThread extends Handler implements Runnable, OnClickListener, OnDismissListener {
        private final int END = 1283;
        private final int SHOW_DIALOG = 1280;
        private final int TOAST = 1282;
        private final int WAIT_OPERATE = 1281;
        Context context;
        FileIdentifier identifier;
        Dialog loadDialog;
        OpenWithDialog openWithDialog = null;
        int operate = -1;

        public IdentifyThread(Looper looper, Context context, FileIdentifier identifier) {
            super(looper);
            this.context = context;
            this.identifier = identifier;
        }

        public void start() {
            new Thread(this).start();
        }

        public void setOpenWith(int way) {
            this.operate = way;
        }

        public void run() {
            sendEmptyMessageDelayed(1280, 500);
            int model = BoxModel.getBoxModel(this.context);
            IdentifyResult result = BrowseUtils.identify(this.context, this.identifier, model);
            switch (result.getResult()) {
                case BrowseConstant.IDENTIFY_NFS_MOUNT_FAIL /*-6*/:
                    obtainMessage(1282, R.string.nfs_open_fail, 0).sendToTarget();
                    break;
                case -4:
                    obtainMessage(1282, R.string.smb_open_fail, 0).sendToTarget();
                    break;
                case -3:
                    obtainMessage(1282, R.string.usb_not_find, 0).sendToTarget();
                    break;
                case -2:
                    obtainMessage(1282, R.string.file_not_exist, 0).sendToTarget();
                    break;
                case -1:
                    obtainMessage(1282, R.string.url_not_find, 0).sendToTarget();
                    break;
                case 0:
                    File file = new File(result.getPath());
                    BoxModel boxModel = BoxModel.getModel(this.context, model);
                    Intent i;
                    if (!file.isDirectory()) {
                        if (!ZidooBoxPermissions.isBluray(this.context) || !FileType.isIsoMovie(file.getAbsolutePath())) {
                            playFile(boxModel, file);
                            break;
                        }
                        obtainMessage(1281, 1, 0).sendToTarget();
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        switch (this.operate) {
                            case 1:
                                playFile(boxModel, file);
                                break;
                            case 2:
                                i = boxModel.getBDMVOpenWith(file);
                                if (i != null) {
                                    i.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                                    this.context.startActivity(i);
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                    } else if (!boxModel.isSupportBDMV() || !FileType.isBDMV(file.getPath())) {
                        playBDMV(boxModel, file);
                        break;
                    } else {
                        sendEmptyMessage(1281);
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        switch (this.operate) {
                            case 0:
                                String uri = this.identifier.getUri();
                                int listIndex = 0;
                                int tag = 0;
                                String temp;
                                switch (this.identifier.getType()) {
                                    case 0:
                                        if (uri.startsWith("/")) {
                                            temp = uri.substring(1, uri.length());
                                        } else {
                                            temp = uri;
                                        }
                                        listIndex = temp.split("/").length;
                                        if (listIndex > 0) {
                                            listIndex--;
                                        }
                                        tag = 0;
                                        break;
                                    case 1:
                                        if (uri.startsWith("/")) {
                                            temp = uri.substring(1, uri.length());
                                        } else {
                                            temp = uri;
                                        }
                                        listIndex = temp.split("/").length;
                                        if (listIndex > 0) {
                                            listIndex--;
                                        }
                                        tag = 1;
                                        break;
                                    case 2:
                                        listIndex = file.getPath().substring(boxModel.getSmbRoot().length() + 1).split("/").length;
                                        tag = 3;
                                        break;
                                    case 3:
                                        listIndex = file.getPath().substring(boxModel.getNfsRoot().length() + 1).split("/").length;
                                        tag = 5;
                                        break;
                                }
                                FastIdentifier fastIdentifier = new FastIdentifier(uri, tag, listIndex);
                                fastIdentifier.setUuid(this.identifier.getUuid());
                                fastIdentifier.setUser(this.identifier.getUser());
                                fastIdentifier.setPassword(this.identifier.getPassword());
                                Intent intent = new Intent(this.context, HomeActivity.class);
                                intent.setFlags(335544320);
                                intent.putExtra(AppConstant.EXTRA_ENTRY_MODE, 2);
                                intent.putExtra(AppConstant.EXTRA_FAST_IDENTIFIER, fastIdentifier.toJson());
                                intent.putExtra(AppConstant.EXTRA_OPEN_WITH, 2);
                                this.context.startActivity(intent);
                                break;
                            case 1:
                                playBDMV(boxModel, file);
                                break;
                            case 2:
                                i = boxModel.getBDMVOpenWith(file);
                                if (i != null) {
                                    i.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                                    this.context.startActivity(i);
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
            sendEmptyMessage(1283);
        }

        private void showLoadDialog() {
            if (this.loadDialog == null) {
                this.loadDialog = new LoadDialog(this.context);
            }
            this.loadDialog.show();
        }

        private void dissmissLoadDialog() {
            removeMessages(1280);
            if (this.loadDialog != null && this.loadDialog.isShowing()) {
                this.loadDialog.dismiss();
                this.loadDialog = null;
            }
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1280:
                    showLoadDialog();
                    break;
                case 1281:
                    dissmissLoadDialog();
                    this.openWithDialog = new OpenWithDialog(this.context, null, 0);
                    View bn;
                    if (msg.arg1 == 1) {
                        this.openWithDialog.findViewById(R.id.bt_look).setVisibility(8);
                        ((TextView) this.openWithDialog.findViewById(R.id.tv_msg)).setText(R.string.video_support_bdmv_navigation);
                        bn = this.openWithDialog.findViewById(R.id.bt_bluray_navigation);
                        bn.setVisibility(0);
                        bn.setOnClickListener(this);
                    } else {
                        this.openWithDialog.findViewById(R.id.bt_look).setOnClickListener(this);
                        if (ZidooBoxPermissions.isBluray(this.context)) {
                            bn = this.openWithDialog.findViewById(R.id.bt_bluray_navigation);
                            bn.setVisibility(0);
                            bn.setOnClickListener(this);
                        }
                    }
                    this.openWithDialog.findViewById(R.id.bt_play).setOnClickListener(this);
                    this.openWithDialog.findViewById(R.id.bt_play).setOnClickListener(this);
                    this.openWithDialog.setOnDismissListener(this);
                    this.openWithDialog.show();
                    break;
                case 1282:
                    Utils.toast(this.context, msg.arg1);
                    break;
                case 1283:
                    dissmissLoadDialog();
                    break;
            }
            super.handleMessage(msg);
        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_bluray_navigation:
                    this.operate = 2;
                    break;
                case R.id.bt_look:
                    this.operate = 0;
                    break;
                case R.id.bt_play:
                    this.operate = 1;
                    break;
            }
            this.openWithDialog.dismiss();
        }

        public void onDismiss(DialogInterface dialog) {
            sendEmptyMessageDelayed(1280, 500);
            synchronized (this) {
                notify();
            }
        }

        private void playFile(BoxModel model, File file) {
            if (BoxModel.sModel == 5) {
                Utils.startActivityForRTD(this.context, model.getOpenWith(file));
            } else {
                model.openFile(file);
            }
        }

        private void playBDMV(BoxModel model, File file) {
            if (BoxModel.sModel == 5) {
                Utils.startActivityForRTD(this.context, model.getBDMVOpenWith(file));
            } else {
                model.openBDMV(file);
            }
        }
    }

    private class LoadDialog extends Dialog {
        public LoadDialog(Context context) {
            super(context, R.style.defaultDialog);
            setContentView(R.layout.dialog_load);
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Window window = getWindow();
            window.setType(2003);
            LayoutParams lp = window.getAttributes();
            lp.width = -2;
            lp.height = -2;
        }
    }

    public void onReceive(Context context, Intent intent) {
        MyLog.d("Receive shortcut " + intent.toString());
        String json = intent.getStringExtra(AppConstant.EXTRA_FILE_IDENTIFY);
        if (json != null) {
            new IdentifyThread(Looper.getMainLooper(), context, new FileIdentifier(json)).start();
        }
    }
}
