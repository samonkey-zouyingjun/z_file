package com.zidoo.fileexplorer.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.ClassicScene;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.SoundTool;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.fileexplorer.tool.ZidooTypeface;
import zidoo.tarot.GLContext;
import zidoo.tarot.TarotRenderer;
import zidoo.tarot.TarotView;
import zidoo.tarot.TarotView.TarotCallback;

public class HomeActivity extends Activity implements TarotCallback {
    long mLastBackTime = -1;
    FileMolder mMolder;
    TarotView mTarotView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).activityCreate(this);
        if (AppConstant.sSystemBootTime == -1) {
            AppConstant.sSystemBootTime = System.currentTimeMillis();
        }
        int resId = BoxModelConfig.check(this);
        setContentView(R.layout.activity_home);
        ((ImageView) findViewById(R.id.img_bgd)).setImageResource(resId);
        this.mTarotView = (TarotView) findViewById(R.id.tarotView);
        this.mTarotView.getGlContext().getConfig().setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
        GLContext glContext = this.mTarotView.getGlContext();
        this.mMolder = new FileMolder(glContext, null, getIntent().getExtras());
        TarotRenderer renderer = new TarotRenderer(glContext, new ClassicScene(glContext, this.mMolder));
        this.mTarotView.setTarotCallback(this);
        this.mTarotView.setZOrderOnTop(true);
        this.mTarotView.getHolder().setFormat(-3);
        this.mTarotView.setOpenGLESAPI(1);
        this.mTarotView.setRenderer(renderer);
        this.mTarotView.setRenderMode(0);
    }

    public void finish() {
        Editor editor = getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).edit();
        editor.putInt(AppConstant.PREFEREANCES_VIEW_PORT, AppConstant.sPrefereancesViewPort);
        editor.putBoolean(AppConstant.PREFEREANCES_OPERATE_MODE, AppConstant.sPrefereancesOperateMode);
        editor.putBoolean(AppConstant.PREFEREANCES_HIDDEN, AppConstant.sPrefereancesHidden);
        editor.putBoolean(AppConstant.PREFEREANCES_USB_TIPS, AppConstant.sPrefereancesUsbTips);
        editor.putInt(AppConstant.PREFEREANCES_SORT, AppConstant.sPrefereancesSortWay);
        editor.putInt(AppConstant.PREFEREANCES_SMB_DISPLAY, AppConstant.sPrefereancesSmbDisplay);
        editor.commit();
        this.mMolder.destroy();
        super.finish();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mMolder.onNewIntent(intent);
    }

    protected void onResume() {
        ((MyApplication) getApplication()).activityResume(this);
        try {
            getWindow().getDecorView().setSystemUiVisibility(5894);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    protected void onPause() {
        ((MyApplication) getApplication()).activityPause(this);
        super.onPause();
    }

    protected void onDestroy() {
        ((MyApplication) getApplication()).activityDestroy(this);
        super.onDestroy();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 24 || event.getKeyCode() == 25) {
            return false;
        }
        if (event.getAction() == 0) {
            SoundTool.soundKey(event.getKeyCode());
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean exit() {
        long c = System.currentTimeMillis();
        if (c - this.mLastBackTime <= 3000) {
            return true;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Utils.toast(HomeActivity.this, HomeActivity.this.getString(R.string.exit_reminds), 0);
            }
        });
        this.mLastBackTime = c;
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return this.mTarotView.dispatchGenericMotionEvent(ev);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.mTarotView.dispatchTouchEvent(ev);
    }

    public void onDispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != 0) {
            return;
        }
        if ((event.getKeyCode() == 4 || event.getKeyCode() == 111) && exit()) {
            finish();
        }
    }

    public void notifyHandleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                finish();
                return;
            case 1:
                try {
                    Intent intent = new Intent("com.zidoo.poster.PosterWall");
                    intent.setDataAndType(Uri.fromFile(msg.obj), "video/*");
                    startActivity(intent);
                    return;
                } catch (Exception e) {
                    Utils.toast((Context) this, (int) R.string.msg_poster_wall_not_find);
                    return;
                }
            default:
                return;
        }
    }
}
