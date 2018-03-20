package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.tool.Utils;
import zidoo.nfs.NfsManager;
import zidoo.tool.ZidooFileUtils;

public class NfsAddDialog extends Dialog implements OnClickListener {
    String ip;
    NfsManager nfsManager;

    public NfsAddDialog(Context context, NfsManager nfsManager) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_add_nfs);
        this.nfsManager = nfsManager;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ip = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getString(AppConstant.PREFEREANCES_NFS_SCAN_IP, ZidooFileUtils.getSelfAddress(getContext()));
        if (this.ip == null) {
            this.ip = "200.200.1.1";
        }
        ((EditText) findViewById(R.id.et_address)).setText(this.ip);
        findViewById(R.id.bt_ok).setOnClickListener(this);
        findViewById(R.id.bt_cancel).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.bt_ok) {
            String ip = ((EditText) findViewById(R.id.et_address)).getText().toString();
            if (ip.matches("(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$")) {
                this.nfsManager.scanDevices(ip);
                getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 2).putString(AppConstant.PREFEREANCES_NFS_SCAN_IP, ip).apply();
            } else {
                Utils.toast(getContext(), (int) R.string.msg_invalid_ip);
                return;
            }
        }
        getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putString(AppConstant.PREFEREANCES_NFS_SCAN_IP, this.ip).apply();
        dismiss();
    }
}
