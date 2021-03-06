package com.zidoo.fileexplorer.gl;

import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.bean.PathInfo;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.config.GR;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.fileexplorer.view.ExpandPathView.OnPathClickListener;
import com.zidoo.fileexplorer.view.RemindsLayout;
import zidoo.device.DeviceType;
import zidoo.tarot.GLContext;
import zidoo.tarot.TarotScene;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.GameObject.OnClickListener;
import zidoo.tarot.kernel.GameObject.OnKeyListener;
import zidoo.tarot.kernel.GameObject.OnLongClickListener;
import zidoo.tarot.widget.AdaperView;
import zidoo.tarot.widget.AdaperView.OnItemClickListener;
import zidoo.tarot.widget.AdaperView.OnItemSelectedListener;

public class ClassicScene extends TarotScene {
    int mBorderIndex = -1;
    boolean mHasPressDown = false;
    int mIntercept = 0;
    FileMolder mMolder = null;
    long mStartTouchTime;
    private MyView mV = null;

    public ClassicScene(GLContext glContext, FileMolder fileMolder) {
        super(glContext);
        this.mMolder = fileMolder;
    }

    protected void onCreate() {
        init();
        this.mV = new MyView();
        initListener();
        this.mMolder.initView(this.mV);
        setContentView(this.mV.layoutMain);
        this.mMolder.getData();
    }

    private void init() {
        AppConstant.sUsbIndex = 0;
        AppConstant.sHddIndex = 0;
        AppConstant.sFlash = this.mGlContext.getString(R.string.flash);
        AppConstant.sSdcard = this.mGlContext.getString(R.string.sdcard);
        AppConstant.sUsb = this.mGlContext.getResources().getStringArray(R.array.usb_devices);
        AppConstant.sHdd = this.mGlContext.getResources().getStringArray(R.array.hdd_devices);
        AppConstant.sSmb = this.mGlContext.getString(R.string.smb);
        AppConstant.sNfs = this.mGlContext.getString(R.string.nfs);
    }

    private void initListener() {
        this.mV.onClickListener = new OnClickListener() {
            public void onClick(GameObject v) {
                Message message;
                switch (v.getId()) {
                    case GR.screens_0 /*520224772*/:
                        ClassicScene.this.mMolder.setScreen(0);
                        return;
                    case GR.screens_1 /*520224773*/:
                        ClassicScene.this.mMolder.setScreen(1);
                        return;
                    case GR.screens_2 /*520224774*/:
                        ClassicScene.this.mMolder.setScreen(2);
                        return;
                    case GR.screens_3 /*520224775*/:
                        ClassicScene.this.mMolder.setScreen(3);
                        return;
                    case GR.screens_4 /*520224776*/:
                        ClassicScene.this.mMolder.setScreen(4);
                        return;
                    case GR.device_state /*520224784*/:
                        ClassicScene.this.mMolder.showDeviceLists();
                        return;
                    case GR.img_up /*520224785*/:
                        ClassicScene.this.mMolder.showScreens();
                        return;
                    case GR.layout_reminds /*520224786*/:
                        if (((RemindsLayout) v).getType() == 1) {
                            ClassicScene.this.mMolder.unMountUsbOrHdd(ClassicScene.this.mMolder.getDevice());
                            return;
                        }
                        return;
                    case GR.img_back /*520224787*/:
                        if (!ClassicScene.this.mMolder.backFileList()) {
                            message = Message.obtain();
                            message.what = 0;
                            ClassicScene.this.getContext().getGLView().notifyHandleMessage(message);
                            return;
                        }
                        return;
                    case GR.img_menu /*520224788*/:
                        ClassicScene.this.mMolder.showMenu();
                        return;
                    case GR.img_exit /*520224789*/:
                        message = Message.obtain();
                        message.what = 0;
                        ClassicScene.this.getContext().getGLView().notifyHandleMessage(message);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mV.onItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdaperView<?> parent, GameObject view, int position) {
                switch (parent.getId()) {
                    case GR.list_devices /*520224768*/:
                        ClassicScene.this.mMolder.openDevice(position, false);
                        return;
                    case GR.grid_file /*520224769*/:
                    case GR.list_file /*520224777*/:
                        ClassicScene.this.mMolder.openFile(position);
                        return;
                    case GR.list_menu /*520224770*/:
                        ClassicScene.this.mMolder.operate(position);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mV.onLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(GameObject v) {
                switch (v.getId()) {
                    case GR.list_devices /*520224768*/:
                        DeviceInfo device = ClassicScene.this.mMolder.getDevice();
                        if (device != null && (device.getType() == DeviceType.SD || device.getType() == DeviceType.HDD || device.getType() == DeviceType.TF)) {
                            ClassicScene.this.mMolder.unMountUsbOrHdd(device);
                            break;
                        }
                    case GR.grid_file /*520224769*/:
                    case GR.list_file /*520224777*/:
                        ClassicScene.this.mMolder.showMenu();
                        return true;
                }
                return false;
            }
        };
        this.mV.onItemSelectedListener = new OnItemSelectedListener() {
            public void onItemSelected(AdaperView<?> parent, GameObject view, int position) {
                switch (parent.getId()) {
                    case GR.list_devices /*520224768*/:
                        if (parent.isFocused()) {
                            ClassicScene.this.mMolder.setUsbUnMountReminds(position);
                            return;
                        }
                        return;
                    case GR.grid_file /*520224769*/:
                    case GR.list_file /*520224777*/:
                        ClassicScene.this.mMolder.updatePageAndDetails(position);
                        return;
                    case GR.list_menu /*520224770*/:
                        ClassicScene.this.mMolder.setMenuRemindsImg(position);
                        return;
                    default:
                        return;
                }
            }

            public void onNothingSelected(AdaperView<?> adaperView) {
                ClassicScene.this.mMolder.nothingSelected();
            }
        };
        this.mV.onKeyListener = new OnKeyListener() {
            public boolean onKey(GameObject v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    ClassicScene.this.mHasPressDown = true;
                    switch (keyCode) {
                        case 4:
                        case 111:
                            return ClassicScene.this.keyBackDown(v);
                        case 19:
                            return ClassicScene.this.keyUp(v);
                        case 20:
                            return ClassicScene.this.keyDown(v);
                        case MotionEventCompat.AXIS_WHEEL /*21*/:
                            return ClassicScene.this.keyLeft(v);
                        case MotionEventCompat.AXIS_GAS /*22*/:
                            return ClassicScene.this.keyRight(v);
                        case MotionEventCompat.AXIS_BRAKE /*23*/:
                        case 66:
                        case 160:
                            return ClassicScene.this.keyEnter(v);
                        case 82:
                            return ClassicScene.this.keyMenu(v);
                        default:
                            return false;
                    }
                } else if (event.getAction() != 1) {
                    return false;
                } else {
                    if (ClassicScene.this.mHasPressDown && (keyCode == 4 || keyCode == 111)) {
                        ClassicScene.this.mHasPressDown = false;
                        return ClassicScene.this.keyBackUp(v);
                    } else if (keyCode == 111) {
                        return false;
                    } else {
                        ClassicScene.this.mHasPressDown = false;
                        return false;
                    }
                }
            }
        };
        this.mV.onPathClickListener = new OnPathClickListener() {
            public void onPath(PathInfo path, int index) {
                ClassicScene.this.mMolder.backToPath(path, index);
            }
        };
    }

    private boolean keyRight(GameObject v) {
        switch (v.getId()) {
            case GR.list_devices /*520224768*/:
                this.mMolder.goneDeviceLists();
                return true;
            case GR.grid_file /*520224769*/:
                if (this.mV.gridview.getSelectedPosition() % 6 == 5) {
                    this.mMolder.showMenu();
                    return true;
                }
                break;
            case GR.list_file /*520224777*/:
                this.mMolder.showMenu();
                return true;
        }
        return false;
    }

    private boolean keyLeft(GameObject v) {
        switch (v.getId()) {
            case GR.list_devices /*520224768*/:
                DeviceInfo device = this.mMolder.getDevice();
                if (device != null && (device.getType() == DeviceType.SD || device.getType() == DeviceType.HDD || device.getType() == DeviceType.TF)) {
                    this.mMolder.unMountUsbOrHdd(device);
                    break;
                }
            case GR.grid_file /*520224769*/:
                if (this.mV.gridview.getSelectedPosition() % 6 == 0) {
                    this.mMolder.showDeviceLists();
                    return true;
                }
                break;
            case GR.list_menu /*520224770*/:
                this.mMolder.goneMenu();
                break;
            case GR.list_file /*520224777*/:
                this.mMolder.showDeviceLists();
                return true;
        }
        return false;
    }

    private boolean keyDown(GameObject v) {
        switch (v.getId()) {
            case GR.screens /*520224771*/:
                this.mMolder.goneScreens();
                return true;
            default:
                return false;
        }
    }

    private boolean keyUp(GameObject v) {
        switch (v.getId()) {
            case GR.grid_file /*520224769*/:
                if (this.mV.gridview.getSelectedPosition() / 6 == 0) {
                    this.mMolder.showScreens();
                    return true;
                }
                break;
            case GR.list_file /*520224777*/:
                if (this.mV.listView.getSelectedPosition() == 0) {
                    this.mMolder.showScreens();
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean keyBackDown(GameObject v) {
        switch (v.getId()) {
            case GR.list_devices /*520224768*/:
            case GR.list_menu /*520224770*/:
            case GR.screens /*520224771*/:
                return true;
            case GR.grid_file /*520224769*/:
            case GR.list_file /*520224777*/:
                return this.mMolder.exit();
            default:
                return false;
        }
    }

    private boolean keyBackUp(GameObject v) {
        switch (v.getId()) {
            case GR.list_devices /*520224768*/:
                this.mMolder.goneDeviceLists();
                return true;
            case GR.grid_file /*520224769*/:
            case GR.list_file /*520224777*/:
                return this.mMolder.backFileList();
            case GR.list_menu /*520224770*/:
                this.mMolder.goneMenu();
                return true;
            case GR.screens /*520224771*/:
                this.mMolder.goneScreens();
                return true;
            default:
                return false;
        }
    }

    private boolean keyMenu(GameObject v) {
        switch (v.getId()) {
            case GR.grid_file /*520224769*/:
            case GR.list_file /*520224777*/:
                this.mMolder.showMenu();
                break;
            case GR.list_menu /*520224770*/:
                this.mMolder.goneMenu();
                break;
        }
        return true;
    }

    private boolean keyEnter(GameObject v) {
        switch (v.getId()) {
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mMolder == null || this.mMolder.isManipulating()) {
            return true;
        }
        if (!ListType.isNet(this.mMolder.getListType()) || ((this.mV.listDevices != null && this.mV.listDevices.isFocused()) || Utils.NetIsConnected(getContext()))) {
            return super.dispatchKeyEvent(event);
        }
        this.mMolder.netError();
        return true;
    }

    private boolean check() {
        if (this.mMolder == null || this.mMolder.isManipulating()) {
            return true;
        }
        if (!ListType.isNet(this.mMolder.getListType()) || Utils.NetIsConnected(getContext())) {
            return false;
        }
        this.mMolder.netError();
        return true;
    }

    private boolean checkBord(MotionEvent event) {
        long cu = System.currentTimeMillis();
        if (event.getX() < 20.0f) {
            if (this.mBorderIndex != 0) {
                this.mBorderIndex = 0;
                this.mStartTouchTime = cu;
            } else if (cu - this.mStartTouchTime > 500) {
                this.mBorderIndex = -1;
                this.mMolder.showDeviceLists();
                return true;
            }
        } else if (event.getY() < 20.0f) {
            if (this.mBorderIndex != 1) {
                this.mBorderIndex = 1;
                this.mStartTouchTime = cu;
            } else if (cu - this.mStartTouchTime > 500) {
                this.mBorderIndex = -1;
                this.mMolder.showScreens();
                return true;
            }
        } else if (event.getX() > ((float) (getContext().getConfig().getDisplay().sScreenWidth - 20))) {
            if (this.mBorderIndex != 2) {
                this.mBorderIndex = 2;
                this.mStartTouchTime = cu;
            } else if (cu - this.mStartTouchTime > 500) {
                this.mBorderIndex = -1;
                this.mMolder.showMenu();
                return true;
            }
        } else if (cu - this.mStartTouchTime > 3000) {
            this.mBorderIndex = -1;
        }
        return false;
    }

    private int intercept(MotionEvent event) {
        if (this.mMolder != null && this.mMolder.isMenuShowing()) {
            return 1;
        }
        if (this.mV.layoutDevices != null && this.mV.layoutDevices.isShow()) {
            return 2;
        }
        if (this.mV.vgScreens == null || !this.mV.vgScreens.isShow()) {
            return 0;
        }
        return 3;
    }

    private boolean checkIntercept(MotionEvent event) {
        if (event.getAction() != 1) {
            if (event.getAction() == 0) {
                this.mIntercept = intercept(event);
            }
            switch (this.mIntercept) {
                case 0:
                    break;
                case 1:
                    this.mV.menu.dispatchTouchEvent(event);
                    return true;
                case 2:
                    if (this.mV.layoutDevices.dispatchTouchEvent(event)) {
                        return true;
                    }
                    this.mV.layoutReminds.dispatchTouchEvent(event);
                    return true;
                case 3:
                    this.mV.vgScreens.dispatchTouchEvent(event);
                    return true;
                default:
                    break;
            }
        }
        switch (this.mIntercept) {
            case 0:
                if (this.mV.menu != null && this.mV.menu.isTouched(event)) {
                    this.mMolder.showMenu();
                    return true;
                }
            case 1:
                if (this.mV.menu.isTouched(event)) {
                    this.mV.menu.dispatchTouchEvent(event);
                    return true;
                } else if (!this.mV.fileAdaperView.isTouched(event)) {
                    return true;
                } else {
                    this.mMolder.goneMenu();
                    return true;
                }
            case 2:
                if (this.mV.listDevices.isTouched(event)) {
                    this.mV.listDevices.dispatchTouchEvent(event);
                    return true;
                } else if (this.mV.layoutReminds.dispatchTouchEvent(event) || !this.mV.fileAdaperView.isTouched(event)) {
                    return true;
                } else {
                    this.mMolder.goneDeviceLists();
                    return true;
                }
            case 3:
                if (this.mV.vgScreens.isTouched(event)) {
                    this.mV.vgScreens.dispatchTouchEvent(event);
                    return true;
                } else if (!this.mV.fileAdaperView.isTouched(event)) {
                    return true;
                } else {
                    this.mMolder.goneScreens();
                    return true;
                }
        }
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return check() || checkIntercept(event) || checkBord(event) || super.dispatchTouchEvent(event);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (check()) {
            return true;
        }
        if (this.mMolder == null || !this.mMolder.isMenuShowing()) {
            if (this.mV.layoutDevices != null && this.mV.layoutDevices.isShow()) {
                return this.mV.listDevices.onGenericMotionEvent(event);
            }
            if (this.mV.vgScreens != null && this.mV.vgScreens.isShow()) {
                return this.mV.vgScreens.onGenericMotionEvent(event);
            }
            if (checkBord(event)) {
                return true;
            }
            return super.dispatchGenericMotionEvent(event);
        } else if (!this.mV.menu.onGenericMotionEvent(event)) {
            return false;
        } else {
            this.mV.menu.setTouchMode(true);
            return true;
        }
    }
}
