package com.zidoo.fileexplorer.gl;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.config.GR;
import com.zidoo.fileexplorer.view.DetailsLayout;
import com.zidoo.fileexplorer.view.DeviceFlickerLayout;
import com.zidoo.fileexplorer.view.ExpandPathView;
import com.zidoo.fileexplorer.view.ExpandPathView.OnPathClickListener;
import com.zidoo.fileexplorer.view.RemindsLayout;
import com.zidoo.fileexplorer.view.Screens;
import com.zidoo.fileexplorer.view.StateImageView;
import java.lang.reflect.Field;
import zidoo.tarot.Config.DisplayConfig;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.GameObject.OnClickListener;
import zidoo.tarot.kernel.GameObject.OnKeyListener;
import zidoo.tarot.kernel.GameObject.OnLongClickListener;
import zidoo.tarot.kernel.Vector3;
import zidoo.tarot.kernel.anim.GlAlphaAnimation;
import zidoo.tarot.kernel.anim.GlAnimation;
import zidoo.tarot.kernel.anim.GlAnimation.AnimatorListener;
import zidoo.tarot.kernel.anim.GlRotateAnimation;
import zidoo.tarot.kernel.anim.GlTransformAnimation;
import zidoo.tarot.kernel.anim.GlTranslateAnimation;
import zidoo.tarot.widget.AdaperView.OnItemClickListener;
import zidoo.tarot.widget.AdaperView.OnItemSelectedListener;
import zidoo.tarot.widget.AdjustableAdapterView;
import zidoo.tarot.widget.AdjustableAdapterView.AdjusterAnimator;
import zidoo.tarot.widget.Layout;
import zidoo.tarot.widget.RotateProgressView;
import zidoo.tarot.widget.ScrollBar;
import zidoo.tarot.widget.SimplifyProgress;
import zidoo.tarot.widget.TImageView;
import zidoo.tarot.widget.TTextView;
import zidoo.tarot.widget.ViewGroup;
import zidoo.tarot.widget.WrapListView;
import zidoo.tarot.widget.WrapMarqueeTextView;
import zidoo.tarot.widget.WrapSingleLineTextView;
import zidoo.tarot.widget.XGridview;
import zidoo.tarot.widget.XListView;

public class MyView {
    public DeviceFlickerLayout deviceState;
    public AdjustableAdapterView fileAdaperView;
    public XGridview gridview;
    public TImageView imgBack;
    public StateImageView imgDeviceDown;
    public StateImageView imgDeviceUp;
    public TImageView imgExit;
    public TImageView imgLogo;
    public TImageView imgMenu;
    public StateImageView imgMenuDown;
    public StateImageView imgMenuUp;
    public TImageView imgUp;
    private boolean isHideMenu = false;
    public DetailsLayout layoutDetails;
    public ViewGroup layoutDevices;
    public ViewGroup layoutFile;
    public ViewGroup layoutMain;
    public ViewGroup layoutMenu;
    public ViewGroup layoutMenuBgd;
    public Layout layoutProgress;
    public RemindsLayout layoutReminds;
    public Layout layoutRotateProgress;
    @Deprecated
    public ViewGroup layoutTouch = null;
    public WrapListView listDevices;
    public XListView listView;
    public WrapListView menu;
    public Dialog netErrorDialog;
    public OnClickListener onClickListener;
    public OnItemClickListener onItemClickListener;
    public OnItemSelectedListener onItemSelectedListener;
    public OnKeyListener onKeyListener;
    public OnLongClickListener onLongClickListener;
    public OnPathClickListener onPathClickListener;
    public ExpandPathView pathView;
    public SimplifyProgress progressBar;
    public RotateProgressView rotateProgressBar;
    public WrapSingleLineTextView tvBrowing;
    public TTextView tvIndexAndCount;
    public WrapMarqueeTextView tvProgress;
    public TTextView tvTitle;
    public TImageView tvTitleBack;
    public Screens vgScreens;

    public void initView(GLContext glContext, BrowseInfo browser, boolean isHideMenu) {
        this.isHideMenu = isHideMenu;
        this.layoutMain = new ViewGroup(glContext);
        this.layoutMain.setWidth(1920.0f);
        this.layoutMain.setHeight(1080.0f);
        this.layoutFile = new ViewGroup(glContext);
        this.layoutFile.setWidth(1600.0f);
        this.layoutFile.setHeight(850.0f);
        if (AppConstant.sPrefereancesViewPort == 0) {
            this.fileAdaperView = getGridView(glContext);
        } else {
            this.fileAdaperView = getListView(glContext);
        }
        this.fileAdaperView.setPageturnEnable(AppConstant.sPrefereancesOperateMode);
        this.tvTitle = new TTextView(glContext);
        this.tvTitle.setWidth(150.0f);
        this.tvTitle.setHeight(40.0f);
        this.tvTitle.setPositionPixel(-750.0f, 380.0f);
        this.tvTitle.setTextSize(36.0f);
        this.tvTitle.setTextColor(-1);
        this.tvTitle.setTextGravity(17);
        this.imgUp = new TImageView(glContext);
        this.imgUp.setId(GR.img_up);
        this.imgUp.setWidth(205.0f);
        this.imgUp.setHeight(64.0f);
        this.imgUp.setY(509.0f);
        this.imgUp.setImageResource(R.drawable.img_up);
        this.imgUp.setOnClickListener(this.onClickListener);
        this.layoutReminds = new RemindsLayout(glContext, isHideMenu);
        this.layoutReminds.setId(GR.layout_reminds);
        this.layoutReminds.setWidth(600.0f);
        this.layoutReminds.setHeight(80.0f);
        this.layoutReminds.setPositionPixel(600.0f, -470.0f);
        this.layoutReminds.setOnClickListener(this.onClickListener);
        this.deviceState = new DeviceFlickerLayout(glContext);
        this.deviceState.setId(GR.device_state);
        this.deviceState.setWidth(57.0f);
        this.deviceState.setHeight(300.0f);
        this.deviceState.setX(-900.0f);
        this.deviceState.setOnClickListener(this.onClickListener);
        this.tvIndexAndCount = new TTextView(glContext);
        this.tvIndexAndCount.setTextGravity(21);
        this.tvIndexAndCount.setTextColor(-1);
        this.tvIndexAndCount.setWidth(200.0f);
        this.tvIndexAndCount.setTextSize(29.0f);
        this.tvIndexAndCount.setHeight(35.0f);
        this.tvIndexAndCount.setPositionPixel(640.0f, 380.0f);
        this.pathView = new ExpandPathView(glContext);
        this.pathView.setWidth(1200.0f);
        this.pathView.setHeight(32.0f);
        this.pathView.setPositionPixel(-90.0f, 380.0f);
        this.pathView.setOnPathClickListener(this.onPathClickListener);
        this.tvTitleBack = new TImageView(glContext);
        this.tvTitleBack.setWidth(2500.0f);
        this.tvTitleBack.setHeight(48.0f);
        this.tvTitleBack.setY(380.0f);
        this.tvTitleBack.setImageResource(R.drawable.title_back);
        this.layoutDetails = new DetailsLayout(glContext);
        this.layoutDetails.setWidth(1600.0f);
        this.layoutDetails.setHeight(75.0f);
        this.layoutDetails.setY(-440.0f);
        this.layoutDetails.setX(30.0f);
        this.layoutFile.addGameObject(this.fileAdaperView);
        this.layoutFile.addGameObject(this.tvTitleBack);
        this.layoutFile.addGameObject(this.pathView);
        this.layoutFile.addGameObject(this.tvIndexAndCount);
        this.layoutFile.addGameObject(this.tvTitle);
        if (AppConstant.sShowLogo && browser == null) {
            this.imgLogo = new TImageView(glContext);
            this.imgLogo.setId(GR.device_state);
            this.imgLogo.setWidth(429.0f);
            this.imgLogo.setHeight(101.0f);
            this.imgLogo.setPositionPixel(-816.0f, 488.0f);
            this.imgLogo.setImageResource(R.drawable.img_logo);
            this.layoutMain.addGameObject(this.imgLogo);
        }
        this.layoutMain.addGameObject(this.layoutFile);
        this.layoutMain.addGameObject(this.deviceState);
        if (browser != null) {
            String name = browser.getName();
            if (name == null) {
                name = glContext.getResources().getStringArray(R.array.screens)[0];
            }
            this.tvTitle.setText(name);
            String title = browser.getTitle();
            if (title != null) {
                this.tvBrowing = new WrapSingleLineTextView(glContext);
                this.tvBrowing.setMaxWidth(1200.0f);
                this.tvBrowing.setHeight(60.0f);
                this.tvBrowing.setY(468.0f);
                this.tvBrowing.setTextSize(40.0f);
                this.tvBrowing.setTextColor(-1);
                this.tvBrowing.setText(title);
                this.layoutMain.addGameObject(this.tvBrowing);
            }
        } else {
            this.layoutMain.addGameObject(this.imgUp);
        }
        this.layoutMain.addGameObject(this.layoutReminds);
        this.layoutMain.addGameObject(this.layoutDetails);
    }

    public XGridview getGridView(GLContext glContext) {
        if (this.gridview == null) {
            this.gridview = new XGridview(glContext);
            this.gridview.setId(GR.grid_file);
            this.gridview.setWidth(1680.0f);
            this.gridview.setHeight(720.0f);
            this.gridview.setY(-23.0f);
            this.gridview.setDrawSelectorOnTop(true);
            this.gridview.setOnItemClickListener(this.onItemClickListener);
            this.gridview.setOnLongClickListener(this.onLongClickListener);
            this.gridview.setOnItemSelectedListener(this.onItemSelectedListener);
            this.gridview.setOnKeyListener(this.onKeyListener);
            TImageView selector = new TImageView(glContext);
            selector.setWidth(280.0f);
            selector.setHeight(230.0f);
            selector.setImageResource(R.drawable.selector);
            this.gridview.setSelector(selector);
            ScrollBar scrollBar = new ScrollBar(glContext);
            scrollBar.setWidth(20.0f);
            scrollBar.setHeight(80.0f);
            scrollBar.setNinePathResource(R.drawable.img_scroll);
            this.gridview.setScrollBar(scrollBar);
            this.gridview.setAdjusterAnimator(new AdjusterAnimator() {
                public void setRemoveAnimation(GameObject item) {
                    GlAlphaAnimation removeAnimation = new GlAlphaAnimation(1.0f, 0.0f);
                    removeAnimation.setFillAfter(true);
                    removeAnimation.setDuration(200);
                    item.startAnimation(removeAnimation);
                }

                public long getRemoveDuration() {
                    return 200;
                }

                public void setAddAnimation(GameObject item) {
                    GlAlphaAnimation addAnimation = new GlAlphaAnimation(0.0f, 1.0f);
                    addAnimation.setDuration(500);
                    addAnimation.setFillAfter(true);
                    item.startAnimation(addAnimation);
                }

                public void setNotifyGoneAnimation(GameObject item) {
                    GlRotateAnimation goneAnimation = new GlRotateAnimation(0.0f, 4.712389f, new Vector3(0.0f, 1.0f, 0.0f));
                    goneAnimation.setDuration(300);
                    goneAnimation.setFillAfter(true);
                    item.startAnimation(goneAnimation);
                }

                public void setNotifyShowAnimation(GameObject item) {
                    GlAlphaAnimation showAnimation = new GlAlphaAnimation(0.0f, 1.0f);
                    showAnimation.setDuration(200);
                    showAnimation.setFillAfter(true);
                    item.startAnimation(showAnimation);
                }
            });
        }
        return this.gridview;
    }

    public XListView getListView(GLContext glContext) {
        if (this.listView == null) {
            this.listView = new XListView(glContext);
            this.listView.setId(GR.list_file);
            this.listView.setWidth(1680.0f);
            this.listView.setHeight(720.0f);
            this.listView.setRowHeigth(80.0f);
            this.listView.setY(-23.0f);
            this.listView.setOnItemClickListener(this.onItemClickListener);
            this.listView.setOnLongClickListener(this.onLongClickListener);
            this.listView.setOnItemSelectedListener(this.onItemSelectedListener);
            this.listView.setOnKeyListener(this.onKeyListener);
            TImageView selector = new TImageView(glContext);
            selector.setWidth(1617.0f);
            selector.setHeight(126.0f);
            selector.setImageResource(R.drawable.selector_file_list);
            this.listView.setSelector(selector);
            ScrollBar scrollBar = new ScrollBar(glContext);
            scrollBar.setWidth(20.0f);
            scrollBar.setHeight(80.0f);
            scrollBar.setNinePathResource(R.drawable.img_scroll);
            this.listView.setScrollBar(scrollBar);
            this.listView.setAdjusterAnimator(new AdjusterAnimator() {
                public void setRemoveAnimation(GameObject item) {
                    GlAlphaAnimation removeAnimation = new GlAlphaAnimation(1.0f, 0.0f);
                    removeAnimation.setFillAfter(true);
                    removeAnimation.setDuration(200);
                    item.startAnimation(removeAnimation);
                }

                public long getRemoveDuration() {
                    return 200;
                }

                public void setAddAnimation(GameObject item) {
                    GlAlphaAnimation addAnimation = new GlAlphaAnimation(0.0f, 1.0f);
                    addAnimation.setDuration(500);
                    addAnimation.setFillAfter(true);
                    item.startAnimation(addAnimation);
                }

                public void setNotifyGoneAnimation(GameObject item) {
                    GlRotateAnimation goneAnimation = new GlRotateAnimation(0.0f, 4.712389f, new Vector3(0.0f, 1.0f, 0.0f));
                    goneAnimation.setDuration(300);
                    goneAnimation.setFillAfter(true);
                    item.startAnimation(goneAnimation);
                }

                public void setNotifyShowAnimation(GameObject item) {
                }
            });
        }
        return this.listView;
    }

    public void initMenu(GLContext glContext) {
        this.layoutMenu = new ViewGroup(glContext);
        this.layoutMenu.setWidth(1120.0f);
        this.layoutMenu.setHeight(1085.0f);
        this.layoutMenu.setMask(true);
        this.layoutMenu.setX(400.0f);
        this.layoutMenuBgd = new ViewGroup(glContext);
        this.layoutMenuBgd.setWidth(1120.0f);
        this.layoutMenuBgd.setHeight(1085.0f);
        this.layoutMenuBgd.setMask(true);
        this.layoutMenuBgd.setX(1120.0f);
        this.layoutMenuBgd.setBackgroundResource(R.drawable.bg_menu);
        this.menu = new WrapListView(glContext);
        this.menu.setId(GR.list_menu);
        this.menu.setWidth(428.0f);
        this.menu.setMaxHeight(820.0f);
        this.menu.setPadding(0, 20, 0, 20);
        this.menu.setRowHeight(132.0f);
        this.menu.setX(586.0f);
        this.menu.scale(0.5f, 0.5f, 1.0f);
        this.menu.setAlpha(0.5f);
        this.menu.setOnItemClickListener(this.onItemClickListener);
        this.menu.setOnItemSelectedListener(this.onItemSelectedListener);
        this.menu.setOnKeyListener(this.onKeyListener);
        TImageView selectorMenu = new TImageView(glContext);
        selectorMenu.setWidth(420.0f);
        selectorMenu.setHeight(128.0f);
        selectorMenu.setImageResource(R.drawable.img_list_selector);
        selectorMenu.setAlpha(0.0f);
        this.menu.setSelector(selectorMenu);
        this.imgMenuUp = new StateImageView(glContext);
        this.imgMenuUp.setWidth(100.0f);
        this.imgMenuUp.setHeight(40.0f);
        this.imgMenuUp.setPositionPixel(356.0f, 472.0f);
        this.imgMenuUp.setImageResource(R.drawable.img_menu_up, R.drawable.img_menu_up_s);
        this.imgMenuDown = new StateImageView(glContext);
        this.imgMenuDown.setWidth(100.0f);
        this.imgMenuDown.setHeight(40.0f);
        this.imgMenuDown.setPositionPixel(356.0f, -472.0f);
        this.imgMenuDown.setImageResource(R.drawable.img_menu_down, R.drawable.img_menu_down_s);
        this.layoutMenuBgd.addGameObject(this.imgMenuUp);
        this.layoutMenuBgd.addGameObject(this.imgMenuDown);
        this.layoutMenu.addGameObject(this.layoutMenuBgd);
        this.layoutMenu.addGameObject(this.menu);
        if (!this.isHideMenu) {
            this.layoutMain.addGameObject(this.layoutMenu);
        }
    }

    public void showMenu(GLContext context) {
        this.layoutMenuBgd.setVisibility(true);
        DisplayConfig display = context.getConfig().getDisplay();
        GlTranslateAnimation bgdGta = new GlTranslateAnimation(new Vector3(1120.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(0.0f * display.sWidthRatio, 0.0f, 0.0f));
        bgdGta.setDuration(300);
        bgdGta.setFillAfter(true);
        this.layoutMenuBgd.startAnimation(bgdGta);
        GlTransformAnimation gtfa = new GlTransformAnimation(new Vector3(586.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(0.5f, 0.5f, 1.0f), 0.5f, new Vector3(346.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(1.0f, 1.0f, 1.0f), 1.0f, 0.0f, 0.0f, new Vector3());
        gtfa.setDuration(93);
        gtfa.setStartDelay(207);
        gtfa.setFillAfter(true);
        this.menu.startAnimation(gtfa);
        GlAlphaAnimation msgaa = new GlAlphaAnimation(0.0f, 1.0f);
        msgaa.setDuration(93);
        msgaa.setStartDelay(207);
        msgaa.setFillAfter(true);
        this.menu.getSelector().startAnimation(msgaa);
    }

    public void hideMenu(GLContext context) {
        DisplayConfig display = context.getConfig().getDisplay();
        GlTranslateAnimation bgdGta = new GlTranslateAnimation(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(1120.0f * display.sWidthRatio, 0.0f, 0.0f));
        bgdGta.setDuration(300);
        bgdGta.setFillAfter(true);
        this.layoutMenuBgd.startAnimation(bgdGta);
        GlTransformAnimation gtfa = new GlTransformAnimation(new Vector3(346.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(1.0f, 1.0f, 1.0f), 1.0f, new Vector3(586.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(0.5f, 0.5f, 1.0f), 0.5f, 0.0f, 0.0f, new Vector3());
        gtfa.setDuration(93);
        gtfa.setFillAfter(true);
        this.menu.startAnimation(gtfa);
        GlAlphaAnimation msgaa = new GlAlphaAnimation(1.0f, 0.0f);
        msgaa.setDuration(93);
        msgaa.setFillAfter(true);
        this.menu.getSelector().startAnimation(msgaa);
        gtfa.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                MyView.this.layoutMenuBgd.setVisibility(false);
                MyView.this.imgUp.setVisibility(true);
                MyView.this.deviceState.setVisibility(true);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
    }

    public void initDeviceLists(GLContext glContext) {
        this.layoutDevices = new ViewGroup(glContext);
        this.layoutDevices.setMask(true);
        this.layoutDevices.setWidth(1123.0f);
        this.layoutDevices.setHeight(1080.0f);
        this.layoutDevices.setX(-1500.0f);
        this.layoutDevices.setBackgroundResource(R.drawable.bg_device);
        WrapMarqueeTextView tvDeviceTitle = new WrapMarqueeTextView(glContext);
        tvDeviceTitle.setHeight(50.0f);
        tvDeviceTitle.setMaxWidth(400.0f);
        tvDeviceTitle.setPositionPixel(-346.0f, 468.0f);
        tvDeviceTitle.setTextColor(-3355444);
        tvDeviceTitle.setTextSize(48.0f);
        tvDeviceTitle.getTextPaint().setFakeBoldText(true);
        tvDeviceTitle.setText(glContext.getString(R.string.device_list));
        this.listDevices = new WrapListView(glContext);
        this.listDevices.setId(GR.list_devices);
        this.listDevices.setWidth(428.0f);
        this.listDevices.setMaxHeight(720.0f);
        this.listDevices.setRowHeight(115.0f);
        this.listDevices.setPadding(0, 20, 0, 20);
        this.listDevices.setX(-347.0f);
        this.listDevices.setOnItemClickListener(this.onItemClickListener);
        this.listDevices.setOnItemSelectedListener(this.onItemSelectedListener);
        this.listDevices.setOnLongClickListener(this.onLongClickListener);
        this.listDevices.setOnKeyListener(this.onKeyListener);
        TImageView selectorNavigation = new TImageView(glContext);
        selectorNavigation.setWidth(428.0f);
        selectorNavigation.setHeight(115.0f);
        selectorNavigation.setImageResource(R.drawable.img_list_selector);
        this.listDevices.setSelector(selectorNavigation);
        this.layoutDevices.addGameObject(tvDeviceTitle);
        this.layoutDevices.addGameObject(this.listDevices);
        this.layoutMain.addGameObject(this.layoutDevices);
    }

    public void setDeviceUpDownReminds(GLContext glContext, int size, int position) {
        boolean z = true;
        if (size > 6) {
            boolean z2;
            if (this.imgDeviceUp == null || this.imgDeviceDown == null) {
                this.imgDeviceUp = new StateImageView(glContext);
                this.imgDeviceUp.setWidth(100.0f);
                this.imgDeviceUp.setHeight(40.0f);
                this.imgDeviceUp.setPositionPixel(-300.0f, 380.0f);
                this.imgDeviceUp.setImageResource(R.drawable.img_menu_up, R.drawable.img_menu_up_s);
                this.imgDeviceDown = new StateImageView(glContext);
                this.imgDeviceDown.setWidth(100.0f);
                this.imgDeviceDown.setHeight(40.0f);
                this.imgDeviceDown.setPositionPixel(-300.0f, -380.0f);
                this.imgDeviceDown.setImageResource(R.drawable.img_menu_down, R.drawable.img_menu_down_s);
                this.layoutDevices.addGameObject(this.imgDeviceUp);
                this.layoutDevices.addGameObject(this.imgDeviceDown);
            } else {
                this.imgDeviceUp.setVisibility(true);
                this.imgDeviceDown.setVisibility(true);
            }
            StateImageView stateImageView = this.imgDeviceUp;
            if (position != 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            stateImageView.setSelected(z2);
            StateImageView stateImageView2 = this.imgDeviceDown;
            if (position == size - 1) {
                z = false;
            }
            stateImageView2.setSelected(z);
            return;
        }
        if (this.imgDeviceUp != null) {
            this.imgDeviceUp.setVisibility(false);
        }
        if (this.imgDeviceDown != null) {
            this.imgDeviceDown.setVisibility(false);
        }
    }

    public void initScreens(GLContext glContext, int screenIndex, String[] screensNames) {
        this.vgScreens = new Screens(glContext, screenIndex, screensNames);
        this.vgScreens.setId(GR.screens);
        this.vgScreens.setWidth(1920.0f);
        this.vgScreens.setHeight(80.0f);
        this.vgScreens.setY(400.0f);
        this.vgScreens.setOnClickListener(this.onClickListener);
        this.vgScreens.setOnKeyListener(this.onKeyListener);
        this.layoutMain.addGameObject(this.vgScreens);
    }

    public void initProgressBar(GLContext glContext) {
        this.layoutProgress = new Layout(glContext);
        this.layoutProgress.setWidth(1920.0f);
        this.layoutProgress.setHeight(1080.0f);
        this.layoutRotateProgress = new Layout(glContext);
        this.layoutRotateProgress.setWidth(1920.0f);
        this.layoutRotateProgress.setHeight(1080.0f);
        this.rotateProgressBar = new RotateProgressView(glContext);
        this.rotateProgressBar.setWidth(80.0f);
        this.rotateProgressBar.setHeight(80.0f);
        this.rotateProgressBar.setY(20.0f);
        try {
            Field barPaint = RotateProgressView.class.getDeclaredField("barPaint");
            barPaint.setAccessible(true);
            ((Paint) barPaint.get(this.rotateProgressBar)).setColor(Color.parseColor("#00aeff"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tvProgress = new WrapMarqueeTextView(glContext);
        this.tvProgress.setHeight(30.0f);
        this.tvProgress.setTextSize(24.0f);
        this.tvProgress.setY(-45.0f);
        this.tvProgress.setMaxWidth(400.0f);
        this.tvProgress.setTextColor(-1);
        this.progressBar = new SimplifyProgress(glContext);
        this.progressBar.setBackgroundResource(R.drawable.img_progress_bg);
        this.progressBar.setProgressResource(R.drawable.img_progress_fore);
        this.layoutRotateProgress.addGameObject(this.rotateProgressBar);
        this.layoutRotateProgress.addGameObject(this.tvProgress);
        this.layoutProgress.addGameObject(this.layoutRotateProgress);
        this.layoutProgress.addGameObject(this.progressBar);
        this.layoutMain.addGameObject(this.layoutProgress);
    }

    @Deprecated
    public void initTouchLayout(GLContext glContext) {
        this.layoutTouch = new ViewGroup(glContext);
        this.layoutTouch.setMask(true);
        this.layoutTouch.setWidth(220.0f);
        this.layoutTouch.setHeight(64.0f);
        this.layoutTouch.setPositionPixel(840.0f, 450.0f);
        this.layoutTouch.setBackgroundResource(R.drawable.bg_touch);
        this.layoutTouch.setVisibility(false);
        this.imgBack = new TImageView(glContext);
        this.imgBack.setId(GR.img_back);
        this.imgBack.setWidth(50.0f);
        this.imgBack.setHeight(50.0f);
        this.imgBack.setX(-70.0f);
        this.imgBack.setImageResource(R.drawable.ic_back);
        this.imgBack.setOnClickListener(this.onClickListener);
        this.imgMenu = new TImageView(glContext);
        this.imgMenu.setId(GR.img_menu);
        this.imgMenu.setWidth(50.0f);
        this.imgMenu.setHeight(50.0f);
        this.imgMenu.setImageResource(R.drawable.ic_menu);
        this.imgMenu.setOnClickListener(this.onClickListener);
        this.imgExit = new TImageView(glContext);
        this.imgExit.setId(GR.img_exit);
        this.imgExit.setWidth(50.0f);
        this.imgExit.setHeight(50.0f);
        this.imgExit.setX(70.0f);
        this.imgExit.setImageResource(R.drawable.ic_exit);
        this.imgExit.setOnClickListener(this.onClickListener);
        this.layoutTouch.addGameObject(this.imgBack);
        this.layoutTouch.addGameObject(this.imgMenu);
        this.layoutTouch.addGameObject(this.imgExit);
        this.layoutMain.addGameObject(this.layoutTouch);
    }
}
