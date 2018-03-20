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

public class NfsPortScanDialog extends Dialog implements OnClickListener {
    NfsManager nfsManager;
    int port;

    public NfsPortScanDialog(Context context, NfsManager nfsManager) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_scan_nfs_by_port);
        this.nfsManager = nfsManager;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.port = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getInt(AppConstant.PREFEREANCES_NFS_SCAN_PORT, 111);
        ((EditText) findViewById(R.id.et_port)).setText(String.valueOf(this.port));
        findViewById(R.id.bt_ok).setOnClickListener(this);
        findViewById(R.id.bt_cancel).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.bt_ok) {
            String index = ((EditText) findViewById(R.id.et_port)).getText().toString();
            boolean valid = false;
            try {
                int i = Integer.parseInt(index);
                if (i < 0 || i >= 65536) {
                    valid = false;
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
            if (valid) {
                try {
                    this.port = Integer.parseInt(index);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                this.nfsManager.scanDevices(this.port);
                getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 0).putInt(AppConstant.PREFEREANCES_NFS_SCAN_PORT, this.port).commit();
            } else {
                Utils.toast(getContext(), (int) R.string.port_invalid);
                return;
            }
        }
        getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_PORT, this.port).commit();
        dismiss();
    }
}
