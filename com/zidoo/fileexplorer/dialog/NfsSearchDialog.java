package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import zidoo.nfs.NfsManager;

public class NfsSearchDialog extends Dialog implements OnClickListener, OnKeyListener {
    String ip;
    View[] items;
    NfsManager nfsManager;
    int port = 111;
    int selected = 0;
    int showItem = 0;

    public NfsSearchDialog(Context context, NfsManager nfsManager) {
        super(context, R.style.defaultDialog);
        setContentView(R.layout.dialog_nfs_scan);
        this.nfsManager = nfsManager;
    }

    protected void onCreate(Bundle savedInstanceState) {
        int i = 0;
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
        this.selected = sp.getInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 0);
        this.port = sp.getInt(AppConstant.PREFEREANCES_NFS_SCAN_PORT, 111);
        this.ip = sp.getString(AppConstant.PREFEREANCES_NFS_SCAN_IP, null);
        this.items = new View[3];
        this.items[0] = findViewById(R.id.rela_search_0);
        this.items[1] = findViewById(R.id.rela_search_1);
        this.items[2] = findViewById(R.id.rela_search_2);
        View[] viewArr = this.items;
        int length = viewArr.length;
        while (i < length) {
            View item = viewArr[i];
            item.setOnClickListener(this);
            item.setOnKeyListener(this);
            i++;
        }
        findViewById(R.id.v_set_scan_port).setOnClickListener(this);
        findViewById(R.id.v_set_add_ip).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_port)).setText(String.valueOf(this.port));
        if (this.ip != null) {
            ((TextView) findViewById(R.id.tv_ip)).setText(String.valueOf(this.ip));
        }
        this.items[this.selected].requestFocus();
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0 && keyCode == 22) {
            switch (v.getId()) {
                case R.id.rela_search_0:
                    new NfsPortScanDialog(getContext(), this.nfsManager).show();
                    dismiss();
                    return true;
                case R.id.rela_search_2:
                    new NfsAddDialog(getContext(), this.nfsManager).show();
                    dismiss();
                    return true;
            }
        }
        return false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rela_search_0:
                this.nfsManager.scanDevices(this.port);
                getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 0).commit();
                break;
            case R.id.rela_search_1:
                this.nfsManager.scanDevices();
                getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 1).commit();
                break;
            case R.id.rela_search_2:
                if (!TextUtils.isEmpty(this.ip)) {
                    this.nfsManager.scanDevices(this.ip);
                    getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit().putInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 2).commit();
                    break;
                }
                new NfsAddDialog(getContext(), this.nfsManager).show();
                break;
            case R.id.v_set_add_ip:
                new NfsAddDialog(getContext(), this.nfsManager).show();
                break;
            case R.id.v_set_scan_port:
                new NfsPortScanDialog(getContext(), this.nfsManager).show();
                break;
        }
        dismiss();
    }
}
