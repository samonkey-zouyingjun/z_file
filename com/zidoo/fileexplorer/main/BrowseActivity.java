package com.zidoo.fileexplorer.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.gl.BrowsingScene;
import com.zidoo.fileexplorer.gl.FileMolder;
import com.zidoo.fileexplorer.tool.BrowsingListener;
import com.zidoo.fileexplorer.tool.SoundTool;
import com.zidoo.fileexplorer.tool.ZidooTypeface;
import com.zidoo.fileexplorer.view.BrowsingRemindsView;
import zidoo.browse.BrowseConstant;
import zidoo.browse.FileIdentifier;
import zidoo.file.FileType;
import zidoo.tarot.GLContext;
import zidoo.tarot.TarotRenderer;
import zidoo.tarot.TarotView;
import zidoo.tarot.TarotView.TarotCallback;

@TargetApi(17)
public class BrowseActivity extends Activity implements BrowsingListener, TarotCallback {
    BrowseInfo mBrowser;
    FileMolder mMolder;
    TarotView mTarotView;

    protected void onCreate(Bundle savedInstanceState) {
        String title;
        String name;
        int device;
        int filter;
        int target;
        String pkg;
        int bgd;
        int help;
        float scale;
        int shade;
        int hrpd;
        int vtpd;
        int clickModel;
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).activityCreate(this);
        if (AppConstant.sSystemBootTime == -1) {
            AppConstant.sSystemBootTime = System.currentTimeMillis();
        }
        Bundle extras = getIntent().getExtras();
        int flag = extras.getInt(BrowseConstant.EXTRA_FLAG, 0);
        String[] customExtras = null;
        float density = getResources().getDisplayMetrics().density;
        String initialPath = null;
        if (flag == 0) {
            title = extras.getString(BrowseConstant.EXTRA_TITLE);
            name = extras.getString("name");
            device = 13;
            filter = 4;
            target = 2097153;
            pkg = extras.getString(BrowseConstant.EXTRA_PACKAGE_NAME);
            bgd = -1;
            help = extras.getInt(BrowseConstant.EXTRA_HELP, -1);
            scale = 1.0f;
            shade = -16777216;
            hrpd = 17;
            vtpd = 16;
            clickModel = 12;
        } else {
            title = extras.getString(BrowseConstant.EXTRA_TITLE);
            name = extras.getString("name");
            device = extras.getInt(BrowseConstant.EXTRA_DEVICE, 31);
            filter = extras.getInt(BrowseConstant.EXTRA_FILTER, 0);
            customExtras = extras.getStringArray(BrowseConstant.EXTRA_CUSTOM_EXTRAS);
            target = extras.getInt(BrowseConstant.EXTRA_TARGET, BrowseConstant.TARGET_ALL);
            pkg = extras.getString(BrowseConstant.EXTRA_PACKAGE_NAME);
            bgd = extras.getInt(BrowseConstant.EXTRA_BGD, -1);
            help = extras.getInt(BrowseConstant.EXTRA_HELP, 2);
            scale = extras.getFloat(BrowseConstant.EXTRA_SCALE, 0.8f);
            shade = extras.getInt(BrowseConstant.EXTRA_SHADE, BrowseConstant.SHADE_DEFAULT);
            hrpd = extras.getInt(BrowseConstant.EXTRA_HORIZONTAL_PADDING, 17);
            vtpd = extras.getInt(BrowseConstant.EXTRA_VERTICAL_PADDING, 16);
            initialPath = extras.getString(BrowseConstant.EXTRA_IDENTIFIER);
            clickModel = extras.getInt(BrowseConstant.EXTRA_CLICK_MODEL, 0);
        }
        this.mBrowser = new BrowseInfo(title, name, device, filter, target, customExtras, flag, pkg, bgd, help, scale, shade, ((float) hrpd) * density, ((float) vtpd) * density, initialPath, clickModel, 0);
        this.mBrowser.setListener(this);
        BoxModelConfig.check(this);
        if (customExtras != null) {
            FileType.registerFiletype(20, customExtras);
        }
        setContentView(R.layout.activity_browsing);
        View sv = findViewById(R.id.rl_shade);
        View main = findViewById(R.id.rl_main);
        this.mTarotView = (TarotView) findViewById(R.id.tarotView);
        this.mTarotView.getGlContext().getConfig().setTypeface(ZidooTypeface.SIMPLIFIEDSTYLE);
        GLContext glContext = this.mTarotView.getGlContext();
        this.mMolder = new FileMolder(glContext, this.mBrowser, getIntent().getExtras());
        TarotRenderer tarotRenderer = new TarotRenderer(glContext, new BrowsingScene(glContext, this.mMolder));
        this.mTarotView.setTarotCallback(this);
        this.mTarotView.setZOrderOnTop(true);
        this.mTarotView.getHolder().setFormat(-3);
        this.mTarotView.setOpenGLESAPI(1);
        this.mTarotView.setRenderer(tarotRenderer);
        this.mTarotView.setRenderMode(0);
        sv.setBackgroundColor(shade);
        Point frame = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(frame);
        LayoutParams lp = main.getLayoutParams();
        lp.width = (int) (((float) frame.x) * scale);
        lp.height = (int) (((float) frame.y) * scale);
        int hp = (int) this.mBrowser.getHorizontalPadding();
        int vp = (int) this.mBrowser.getVerticalPadding();
        main.setPadding(hp, vp, hp, vp);
        boolean set = false;
        if (!(TextUtils.isEmpty(this.mBrowser.getPackageName()) || this.mBrowser.getBackgroundResource() == -1)) {
            try {
                main.setBackground(createPackageContext(this.mBrowser.getPackageName(), 3).getResources().getDrawable(this.mBrowser.getBackgroundResource()));
                set = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!set) {
            main.setBackgroundResource(R.drawable.bg_browse);
        }
        if (help >= -1 && help < 2) {
            new BrowsingRemindsView(this, this.mMolder, this.mBrowser, help).show();
        }
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

    public void onBrowsing(FileIdentifier identifier) {
        Intent data = new Intent();
        if (this.mBrowser.getFlag() != 0) {
            data.putExtra(BrowseConstant.EXTRA_IDENTIFIER, identifier);
        } else if (identifier.getType() == 0 || identifier.getType() == 1) {
            data.putExtra(BrowseConstant.EXTRA_PATH, identifier.getExtra());
        } else {
            data.putExtra("url", identifier.getUri());
            data.putExtra("user", identifier.getUser());
            data.putExtra("password", identifier.getPassword());
            data.putExtra("host", identifier.getExtra());
            data.putExtra(AppConstant.DB_SMB_IP, identifier.getPassword());
        }
        int help = this.mBrowser.getHelp();
        if (help < 1) {
            help = 1;
        }
        data.putExtra(BrowseConstant.EXTRA_HELP, help);
        data.putExtra(BrowseConstant.EXTRA_RESULT, true);
        setResult(1, data);
        finish();
        this.mBrowser.setListener(null);
    }

    public void onBackPressed() {
        if (this.mBrowser.getHelp() == 2) {
            Intent data = new Intent();
            data.putExtra(BrowseConstant.EXTRA_HELP, 2);
            setResult(1, data);
        }
        super.onBackPressed();
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
        if (event.getKeyCode() == 4 || event.getKeyCode() == 111) {
            finish();
        }
    }

    public void notifyHandleMessage(Message msg) {
        finish();
    }
}
