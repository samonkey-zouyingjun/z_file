package com.zidoo.fileexplorer.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.SoundTool;
import com.zidoo.fileexplorer.view.SwitchButton;

public class SettingDialog extends Dialog implements OnClickListener, OnCheckedChangeListener, OnFocusChangeListener {
    FileMolder mFileMolder;
    int mFocusId = -1;
    int mScanModel = 0;
    RelativeLayout[] scanModelViews;

    @SuppressLint({"Instantiatable"})
    public SettingDialog(Context context, FileMolder molder) {
        super(context, R.style.dialog_full_screen);
        this.mFileMolder = molder;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        initView();
    }

    private void initView() {
        View vScrollMode = findViewById(R.id.linear_scroll_mode);
        View vHiddenFiles = findViewById(R.id.linear_hidden_files);
        View vUsbTips = findViewById(R.id.linear_usb_tips);
        View vAutoScan = findViewById(R.id.linear_auto_scan_smb);
        vScrollMode.setOnClickListener(this);
        vScrollMode.setOnFocusChangeListener(this);
        vHiddenFiles.setOnClickListener(this);
        vHiddenFiles.setOnFocusChangeListener(this);
        vUsbTips.setOnClickListener(this);
        vUsbTips.setOnFocusChangeListener(this);
        vAutoScan.setOnClickListener(this);
        vAutoScan.setOnFocusChangeListener(this);
        SharedPreferences preferences = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
        boolean autoScan = preferences.getBoolean(AppConstant.PREFEREANCES_AUTO_SCAN_SMB, false);
        this.mScanModel = preferences.getInt(AppConstant.PREFEREANCES_DEFAULT_SMB_SCAN_MODEL, 0);
        SwitchButton swbScroll = (SwitchButton) findViewById(R.id.swb_scroll);
        SwitchButton swbHidden = (SwitchButton) findViewById(R.id.swb_hidden);
        SwitchButton swbUsbTips = (SwitchButton) findViewById(R.id.swb_usb_tips);
        SwitchButton swbAutoScan = (SwitchButton) findViewById(R.id.swb_auto_scan_smb);
        swbScroll.setChecked(AppConstant.sPrefereancesOperateMode);
        swbHidden.setChecked(AppConstant.sPrefereancesHidden);
        swbUsbTips.setChecked(AppConstant.sPrefereancesUsbTips);
        swbAutoScan.setChecked(autoScan);
        swbScroll.setOnCheckedChangeListener(this);
        swbHidden.setOnCheckedChangeListener(this);
        swbUsbTips.setOnCheckedChangeListener(this);
        swbAutoScan.setOnCheckedChangeListener(this);
        View modelLayout = findViewById(R.id.ln_scan_model);
        this.scanModelViews = new RelativeLayout[3];
        this.scanModelViews[0] = (RelativeLayout) modelLayout.findViewById(R.id.rl_accurate_search);
        this.scanModelViews[1] = (RelativeLayout) modelLayout.findViewById(R.id.rl_normal_search);
        this.scanModelViews[2] = (RelativeLayout) modelLayout.findViewById(R.id.rl_quick_search);
        this.scanModelViews[0].setOnClickListener(this);
        this.scanModelViews[1].setOnClickListener(this);
        this.scanModelViews[2].setOnClickListener(this);
        findViewById(R.id.bt_ok).setOnClickListener(this);
        this.scanModelViews[this.mScanModel].getChildAt(1).setVisibility(0);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = -1;
        lp.height = -1;
        getWindow().setAttributes(lp);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                dismiss();
                return;
            case R.id.linear_auto_scan_smb:
                changeSet(v, R.id.swb_auto_scan_smb);
                return;
            case R.id.linear_hidden_files:
                changeSet(v, R.id.swb_hidden);
                return;
            case R.id.linear_scroll_mode:
                changeSet(v, R.id.swb_scroll);
                return;
            case R.id.linear_usb_tips:
                changeSet(v, R.id.swb_usb_tips);
                return;
            case R.id.rl_accurate_search:
                changeScanModel(v, 0);
                return;
            case R.id.rl_normal_search:
                changeScanModel(v, 1);
                return;
            case R.id.rl_quick_search:
                changeScanModel(v, 2);
                return;
            default:
                return;
        }
    }

    private void changeSet(View v, int resid) {
        SwitchButton swb = (SwitchButton) v.findViewById(resid);
        swb.setChecked(!swb.isChecked());
    }

    private void changeScanModel(View v, int model) {
        if (this.mScanModel != model) {
            this.scanModelViews[this.mScanModel].getChildAt(1).setVisibility(8);
            this.mScanModel = model;
            this.scanModelViews[this.mScanModel].getChildAt(1).setVisibility(0);
            Editor editor = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit();
            editor.putInt(AppConstant.PREFEREANCES_DEFAULT_SMB_SCAN_MODEL, this.mScanModel);
            editor.commit();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            SoundTool.soundKey(event.getKeyCode());
        }
        return super.dispatchKeyEvent(event);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = 0;
        if (buttonView.getId() == R.id.swb_auto_scan_smb) {
            Editor editor = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit();
            editor.putBoolean(AppConstant.PREFEREANCES_AUTO_SCAN_SMB, isChecked);
            editor.commit();
            View findViewById = findViewById(R.id.ln_scan_model);
            if (!isChecked) {
                i = 8;
            }
            findViewById.setVisibility(i);
            return;
        }
        this.mFileMolder.changeSetting(buttonView, isChecked);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        v.setSelected(hasFocus);
        if (hasFocus) {
            int newId = v.getId();
            if (this.mFocusId != newId) {
                this.mFocusId = newId;
                if (this.mFocusId == R.id.linear_auto_scan_smb && ((CompoundButton) v.findViewById(R.id.swb_auto_scan_smb)).isChecked()) {
                    findViewById(R.id.ln_scan_model).setVisibility(0);
                } else {
                    findViewById(R.id.ln_scan_model).setVisibility(8);
                }
            }
        }
    }
}
