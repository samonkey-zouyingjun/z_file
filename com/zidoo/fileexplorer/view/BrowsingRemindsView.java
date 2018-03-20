package com.zidoo.fileexplorer.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.Utils;

@TargetApi(17)
public class BrowsingRemindsView implements OnClickListener {
    Activity mActivity;
    BrowseInfo mBrowser;
    View mContentView;
    MyHandler mHandler = new MyHandler();
    int mHelpFlag;
    float mHorizontalDensity;
    FileMolder mMolder;
    Button mOk;
    ScatterBackgroudImageView mScatterView;
    Button mSkip;
    int mState = 0;
    TextView mTitle;
    float mVerticalDensity;

    @SuppressLint({"HandlerLeak"})
    private class MyHandler extends Handler {
        private MyHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    BrowsingRemindsView.this.initData();
                    break;
                case 1:
                    if (msg.arg1 <= 0) {
                        BrowsingRemindsView.this.mSkip.setText(BrowsingRemindsView.this.mActivity.getString(R.string.br_skip));
                        BrowsingRemindsView.this.mSkip.setEnabled(true);
                        break;
                    }
                    BrowsingRemindsView.this.mSkip.setText(BrowsingRemindsView.this.mActivity.getString(R.string.br_skip) + "(" + msg.arg1 + ")");
                    BrowsingRemindsView.this.mHandler.sendMessageDelayed(BrowsingRemindsView.this.mHandler.obtainMessage(1, msg.arg1 - 1, 0), 1000);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public BrowsingRemindsView(Activity activity, FileMolder fileMolder, BrowseInfo browser, int help) {
        this.mActivity = activity;
        this.mMolder = fileMolder;
        this.mBrowser = browser;
        this.mHelpFlag = help;
        this.mContentView = this.mActivity.getLayoutInflater().inflate(R.layout.view_browsing_reminds, null);
        this.mTitle = (TextView) this.mContentView.findViewById(R.id.tv_title);
        this.mOk = (Button) this.mContentView.findViewById(R.id.bt_ok);
        this.mSkip = (Button) this.mContentView.findViewById(R.id.bt_skip);
        this.mOk.setOnClickListener(this);
        this.mContentView.findViewById(R.id.bt_skip).setOnClickListener(this);
        this.mScatterView = (ScatterBackgroudImageView) this.mContentView.findViewById(R.id.sbiv);
    }

    @SuppressLint({"InlinedApi"})
    public void show() {
        WindowManager wm = (WindowManager) this.mActivity.getSystemService("window");
        this.mContentView.setSystemUiVisibility(5894);
        float scale = this.mBrowser.getScale();
        Point frame = new Point();
        wm.getDefaultDisplay().getRealSize(frame);
        float w = (((float) frame.x) * scale) - (this.mBrowser.getHorizontalPadding() * 2.0f);
        float h = (((float) frame.y) * scale) - (this.mBrowser.getVerticalPadding() * 2.0f);
        View main = this.mContentView.findViewById(R.id.rl_main);
        main.setScaleX(w / ((float) frame.x));
        main.setScaleY(h / ((float) frame.y));
        this.mHorizontalDensity = ((float) frame.x) / 1920.0f;
        this.mVerticalDensity = ((float) frame.y) / 1080.0f;
        LayoutParams lp = new LayoutParams();
        lp.format = 1;
        lp.flags = 1024;
        lp.width = -1;
        lp.height = -1;
        wm.addView(this.mContentView, lp);
        check();
    }

    private void check() {
        new Thread(new Runnable() {
            public void run() {
                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (BrowsingRemindsView.this.mMolder == null);
                BrowsingRemindsView.this.mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initData() {
        if (this.mHelpFlag == 0) {
            this.mSkip.setText(this.mActivity.getString(R.string.br_skip) + "(5)");
            this.mSkip.setVisibility(0);
            this.mSkip.setEnabled(false);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, 4, 0), 1000);
        } else if (this.mHelpFlag == 1) {
            this.mSkip.setVisibility(0);
            this.mContentView.findViewById(R.id.cb_nolonger_show).setVisibility(0);
        }
        this.mOk.setVisibility(0);
        this.mOk.requestFocus();
    }

    private void showNextReminds() {
        switch (this.mState) {
            case 0:
                this.mContentView.findViewById(R.id.rl_reminds_0).setVisibility(0);
                this.mScatterView.showRect(new Rect(0, 0, (int) (450.0f * this.mHorizontalDensity), (int) (this.mVerticalDensity * 1080.0f)));
                this.mContentView.findViewById(R.id.img_device_reminds).startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.translate_letf));
                this.mOk.setText(R.string.br_next);
                this.mTitle.setText(R.string.br_show_device);
                this.mMolder.showDeviceLists();
                break;
            case 1:
                this.mContentView.findViewById(R.id.rl_reminds_0).setVisibility(8);
                this.mContentView.findViewById(R.id.rl_reminds_1).setVisibility(0);
                this.mScatterView.showRect(new Rect((int) (this.mHorizontalDensity * 1470.0f), 0, (int) (this.mHorizontalDensity * 1920.0f), (int) (this.mVerticalDensity * 1080.0f)));
                this.mContentView.findViewById(R.id.img_menu_reminds).startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.translate_right));
                this.mTitle.setText(R.string.br_open_menu);
                this.mMolder.goneDeviceLists();
                this.mMolder.showMenu();
                break;
            case 2:
                this.mContentView.findViewById(R.id.rl_reminds_1).setVisibility(8);
                this.mContentView.findViewById(R.id.rl_reminds_2).setVisibility(0);
                this.mScatterView.showRect(new Rect((int) (this.mHorizontalDensity * 1470.0f), (int) (332.0f * this.mVerticalDensity), (int) (this.mHorizontalDensity * 1920.0f), (int) (486.0f * this.mVerticalDensity)));
                this.mContentView.findViewById(R.id.img_add_reminds).startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.translate_right));
                this.mTitle.setText(R.string.br_add);
                this.mOk.setText(R.string.br_know);
                break;
            case 3:
                this.mMolder.goneMenu();
                ((WindowManager) this.mActivity.getSystemService("window")).removeView(this.mContentView);
                checkNet();
                if (((CheckBox) this.mContentView.findViewById(R.id.cb_nolonger_show)).isChecked()) {
                    this.mBrowser.setHelp(2);
                    break;
                }
                break;
        }
        this.mState++;
    }

    private void checkNet() {
        if (this.mMolder.getDevices().size() == 1 && !Utils.NetIsConnected(this.mActivity)) {
            final Dialog dialog = new Dialog(this.mActivity, R.style.defaultDialog);
            dialog.setContentView(R.layout.dialog_net_unconnect);
            dialog.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    BrowsingRemindsView.this.mActivity.finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.bt_ok) {
            showNextReminds();
            return;
        }
        if (this.mState > 1) {
            this.mMolder.goneMenu();
        } else if (this.mState > 0) {
            this.mMolder.goneDeviceLists();
        }
        ((WindowManager) this.mActivity.getSystemService("window")).removeView(this.mContentView);
        if (((CheckBox) this.mContentView.findViewById(R.id.cb_nolonger_show)).isChecked()) {
            this.mBrowser.setHelp(2);
        }
        checkNet();
    }
}
