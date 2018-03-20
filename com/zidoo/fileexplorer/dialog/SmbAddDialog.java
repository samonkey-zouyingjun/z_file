package com.zidoo.fileexplorer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.task.AddSmbTask;
import com.zidoo.fileexplorer.tool.SoundTool;
import com.zidoo.fileexplorer.tool.Utils;
import pers.lic.tool.Toolc;

public class SmbAddDialog extends Dialog implements OnCheckedChangeListener, OnClickListener, OnKeyListener, OnHoverListener, OnFocusChangeListener {
    Handler handler;
    ListInfo listInfo;
    FileMolder mFileMolder;
    EditText passwordEdit;
    TextView passwordText;
    EditText pathEdit;
    PopupWindow tipPopupWindow = null;
    EditText userEdit;
    TextView userText;

    public SmbAddDialog(Context context, int theme, Handler handler, FileMolder molder, ListInfo listInfo) {
        super(context, theme);
        setContentView(R.layout.dialog_add_smb);
        setOnKeyListener(this);
        this.handler = handler;
        this.mFileMolder = molder;
        this.listInfo = listInfo;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pathEdit = (EditText) findViewById(R.id.et_server);
        this.userEdit = (EditText) findViewById(R.id.et_user);
        this.passwordEdit = (EditText) findViewById(R.id.et_password);
        this.userText = (TextView) findViewById(R.id.tv_user);
        this.passwordText = (TextView) findViewById(R.id.tv_password);
        ((CheckBox) findViewById(R.id.cb_anonymous)).setOnCheckedChangeListener(this);
        findViewById(R.id.bt_add).setOnClickListener(this);
        findViewById(R.id.bt_cancel).setOnClickListener(this);
        View tip = findViewById(R.id.bt_url_tip);
        tip.setOnFocusChangeListener(this);
        tip.setOnHoverListener(this);
        this.pathEdit.requestFocus();
        if (getWindow() != null) {
            LayoutParams lp = getWindow().getAttributes();
            lp.flags |= 134217728;
            lp.width = -1;
            lp.height = -2;
            lp.gravity = 17;
            getWindow().setAttributes(lp);
            Toolc.fullScreen(getWindow().getDecorView());
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            this.userEdit.setFocusable(false);
            this.userEdit.setEnabled(false);
            this.passwordEdit.setFocusable(false);
            this.passwordEdit.setEnabled(false);
            this.userText.setTextColor(-12303292);
            this.passwordText.setTextColor(-12303292);
            return;
        }
        this.userEdit.setFocusable(true);
        this.userEdit.setEnabled(true);
        this.passwordEdit.setFocusable(true);
        this.passwordEdit.setEnabled(true);
        this.userText.setTextColor(-1);
        this.passwordText.setTextColor(-1);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                String url = this.pathEdit.getText().toString().trim();
                if (url.isEmpty()) {
                    Utils.toast(getContext(), getContext().getString(R.string.input_server), 0);
                    return;
                }
                String user = this.userEdit.getText().toString().trim();
                String password = this.passwordEdit.getText().toString().trim();
                if (((CheckBox) findViewById(R.id.cb_anonymous)).isChecked() || (user.equals("") && password.equals(""))) {
                    user = "guest";
                    password = "";
                }
                this.mFileMolder.startTask(new AddSmbTask(this, this.handler, AppConstant.HANDLER_TASK_ADD_SMB, getContext(), url, user, password, this.listInfo));
                return;
            case R.id.bt_cancel:
                dismiss();
                return;
            default:
                return;
        }
    }

    public boolean onHover(View v, MotionEvent event) {
        if (event.getAction() == 9) {
            showSmbUrlTip(v);
        } else if (event.getAction() == 10 && this.tipPopupWindow != null) {
            this.tipPopupWindow.dismiss();
        }
        return false;
    }

    private void showSmbUrlTip(View parent) {
        if (this.tipPopupWindow == null || !this.tipPopupWindow.isShowing()) {
            PopupWindow pp = new PopupWindow(getContext());
            pp.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.pop_smb_url_tip, null));
            pp.setFocusable(false);
            pp.setWindowLayoutMode(-2, -2);
            pp.showAsDropDown(parent, getContext().getResources().getDimensionPixelOffset(R.dimen.dp_30), -getContext().getResources().getDimensionPixelOffset(R.dimen.dp_15));
            this.tipPopupWindow = pp;
        }
    }

    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            SoundTool.soundKey(keyCode);
        }
        return false;
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            showSmbUrlTip(v);
        } else if (this.tipPopupWindow != null) {
            this.tipPopupWindow.dismiss();
        }
    }
}
