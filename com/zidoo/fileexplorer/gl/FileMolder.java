package com.zidoo.fileexplorer.gl;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.adapter.BaseFileAdapter;
import com.zidoo.fileexplorer.adapter.DeviceAdapter;
import com.zidoo.fileexplorer.adapter.FavoriteGridAdapter;
import com.zidoo.fileexplorer.adapter.FavoriteListAdapter;
import com.zidoo.fileexplorer.adapter.FileGridAdapter;
import com.zidoo.fileexplorer.adapter.FileListAdapter;
import com.zidoo.fileexplorer.adapter.NfsGridAdapter;
import com.zidoo.fileexplorer.adapter.NfsListAdapter;
import com.zidoo.fileexplorer.adapter.SmbGridAdapter;
import com.zidoo.fileexplorer.adapter.SmbListAdapter;
import com.zidoo.fileexplorer.bean.BrowseInfo;
import com.zidoo.fileexplorer.bean.DeviceInfo;
import com.zidoo.fileexplorer.bean.FastIdentifier;
import com.zidoo.fileexplorer.bean.Favorite;
import com.zidoo.fileexplorer.bean.FavoriteParam;
import com.zidoo.fileexplorer.bean.FilePath;
import com.zidoo.fileexplorer.bean.ListInfo;
import com.zidoo.fileexplorer.bean.ListType;
import com.zidoo.fileexplorer.bean.ListViewHolder;
import com.zidoo.fileexplorer.bean.MountFile;
import com.zidoo.fileexplorer.bean.OperateInfo;
import com.zidoo.fileexplorer.bean.PathInfo;
import com.zidoo.fileexplorer.bean.ProtocolPath;
import com.zidoo.fileexplorer.bean.SharePath;
import com.zidoo.fileexplorer.bean.SmbViewHolder;
import com.zidoo.fileexplorer.bean.TaskParam.CheckedFavorites;
import com.zidoo.fileexplorer.bean.TaskParam.Data;
import com.zidoo.fileexplorer.bean.VirtualFile;
import com.zidoo.fileexplorer.config.AppConstant;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import com.zidoo.fileexplorer.dialog.NfsSearchDialog;
import com.zidoo.fileexplorer.dialog.OpenWithDialog;
import com.zidoo.fileexplorer.dialog.SearchDialog;
import com.zidoo.fileexplorer.dialog.SettingDialog;
import com.zidoo.fileexplorer.dialog.SmbAddDialog;
import com.zidoo.fileexplorer.dialog.TaskDialog;
import com.zidoo.fileexplorer.main.BoxModelConfig;
import com.zidoo.fileexplorer.menu.FileMenu;
import com.zidoo.fileexplorer.menu.MenuManager;
import com.zidoo.fileexplorer.menu.MenuPermission;
import com.zidoo.fileexplorer.service.DynamicBroadcast;
import com.zidoo.fileexplorer.task.AddSmbTask;
import com.zidoo.fileexplorer.task.BaseTask;
import com.zidoo.fileexplorer.task.CreateTask;
import com.zidoo.fileexplorer.task.DetailTask;
import com.zidoo.fileexplorer.task.DetailTask.Param;
import com.zidoo.fileexplorer.task.FastIdentifyTask;
import com.zidoo.fileexplorer.task.FastIdentifyTask.OnShowBdmvOpenWith;
import com.zidoo.fileexplorer.task.FavoriteTask;
import com.zidoo.fileexplorer.task.FileOpenTask;
import com.zidoo.fileexplorer.task.IdentifyUriTask;
import com.zidoo.fileexplorer.task.OpenDirTask;
import com.zidoo.fileexplorer.task.OpenNfsShareTask;
import com.zidoo.fileexplorer.task.OpenNfsTask;
import com.zidoo.fileexplorer.task.OpenSmbDeviceTask;
import com.zidoo.fileexplorer.task.OpenSmbDeviceTask.Result;
import com.zidoo.fileexplorer.task.OpenSmbShareDeviceTask;
import com.zidoo.fileexplorer.task.OpenSmbShareTask;
import com.zidoo.fileexplorer.task.QueryFavoriteTask;
import com.zidoo.fileexplorer.task.RemoveFavoriteTask;
import com.zidoo.fileexplorer.task.RemoveSmbTask;
import com.zidoo.fileexplorer.task.RenameFavoriteTask;
import com.zidoo.fileexplorer.task.RenameTask;
import com.zidoo.fileexplorer.task.ShortcutTask;
import com.zidoo.fileexplorer.tool.FileCopyTask;
import com.zidoo.fileexplorer.tool.FileCopyTask.OnCopyListener;
import com.zidoo.fileexplorer.tool.FileOperater;
import com.zidoo.fileexplorer.tool.FileOperater.OnFileDeleteListener;
import com.zidoo.fileexplorer.tool.MyLog;
import com.zidoo.fileexplorer.tool.OnLoadFileListener;
import com.zidoo.fileexplorer.tool.SmbDatabaseUtils;
import com.zidoo.fileexplorer.tool.SoundTool;
import com.zidoo.fileexplorer.tool.Utils;
import com.zidoo.fileexplorer.view.RelativeRadioGroup;
import com.zidoo.fileexplorer.view.Screens;
import com.zidoo.fileexplorer.view.StateImageView;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zidoo.browse.FileIdentifier;
import zidoo.device.DeviceType;
import zidoo.device.ZDevice;
import zidoo.file.FileType;
import zidoo.model.BoxModel;
import zidoo.nfs.NfsDevice;
import zidoo.nfs.NfsFactory;
import zidoo.nfs.NfsManager;
import zidoo.nfs.OnNfsSearchListener;
import zidoo.samba.exs.OnRecvMsgListener;
import zidoo.samba.exs.SambaDevice;
import zidoo.samba.manager.SambaManager;
import zidoo.tarot.Config.DisplayConfig;
import zidoo.tarot.GLContext;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Texture;
import zidoo.tarot.kernel.Vector3;
import zidoo.tarot.kernel.anim.GlAlphaAnimation;
import zidoo.tarot.kernel.anim.GlAnimation;
import zidoo.tarot.kernel.anim.GlAnimation.AnimatorListener;
import zidoo.tarot.kernel.anim.GlScaleAnimation;
import zidoo.tarot.kernel.anim.GlSetAnimation;
import zidoo.tarot.kernel.anim.GlTranslateAnimation;
import zidoo.tarot.utils.AsyncTextureThread;
import zidoo.tarot.widget.AdjustableAdapterView;
import zidoo.tarot.widget.WrapSingleLineTextView;
import zidoo.tool.ZidooFileUtils;

@SuppressLint({"SimpleDateFormat"})
public class FileMolder implements OnRecvMsgListener, OnNfsSearchListener, OnCancelListener, OnLoadFileListener, OnShowBdmvOpenWith, MenuPermission {
    DeviceAdapter mAdapterDevice;
    BaseFileAdapter mAdapterFile;
    BaseTask<?> mBaseTask = null;
    private BoxModel mBoxModel;
    BrowseInfo mBrowser;
    boolean mDeleteSrc = false;
    DetailTask mDetailTask = null;
    int mDeviceIndex = -1;
    DynamicBroadcast mDynamicBroadcast;
    int mEnterSmbOrNfsDevicePosition = 0;
    AdjustableAdapterView mFileAdapterView = null;
    FileFilter mFileFilter;
    FileOperater mFileOperater;
    GLContext mGLContext = null;
    InnerHandler mHandler;
    boolean mHasInitData = false;
    boolean mHasScanedSmb = false;
    private boolean mIsHideMenu = false;
    boolean mIsMenuShow = false;
    int mListIndex = 0;
    ListInfo mListInfo = new ListInfo();
    WrapSingleLineTextView mListItemName = null;
    ListType mListType = ListType.FILE;
    String[] mListTypesNames;
    TaskDialog mLoadDialog = null;
    int mLoadFailNumber = 0;
    boolean mManipulating = true;
    MenuView mMenu;
    MenuManager mMenuManager;
    File[] mMoveFiles = new File[0];
    int mMovedDevice = 0;
    NfsManager mNfsManager;
    int mNfsScanTime = 0;
    OperateInfo mOperateInfo = null;
    File mParent;
    int mResearchModel = -1;
    SambaManager mSambaManager;
    Bundle mSavedInstanceState = null;
    int mScanState = 0;
    int mScreen = 0;
    String[] mScreensNames;
    private MyView mV = null;

    class DialogClick implements OnClickListener {
        Dialog dialog;
        int tag;

        DialogClick(Dialog dialog, int tag) {
            this.dialog = dialog;
            this.tag = tag;
        }

        public void onClick(View v) {
            OpenWithDialog openWithDialog;
            switch (v.getId()) {
                case R.id.bt_bluray_navigation:
                    openWithDialog = (OpenWithDialog) this.dialog;
                    if (!openWithDialog.isTask()) {
                        Intent intent = FileMolder.this.mBoxModel.getBDMVOpenWith(openWithDialog.getFile());
                        if (intent != null && Utils.isAppSystemInstall(FileMolder.this.getContext(), "com.zidoo.bluraynavigation")) {
                            intent.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                            FileMolder.this.getContext().startActivity(intent);
                            break;
                        }
                    }
                    ((OpenWithDialog) this.dialog).setOpenWith(3);
                    FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_DIALOG, 500);
                    break;
                case R.id.bt_look:
                    openWithDialog = (OpenWithDialog) this.dialog;
                    if (!openWithDialog.isTask()) {
                        FileMolder.this.openDir(0, openWithDialog.getFiles()[openWithDialog.getPosition()], null);
                        break;
                    }
                    ((OpenWithDialog) this.dialog).setOpenWith(2);
                    FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_DIALOG, 500);
                    break;
                case R.id.bt_ok:
                    int position;
                    if (this.tag != 0) {
                        String name;
                        if (this.tag != 1) {
                            name = ((EditText) this.dialog.findViewById(R.id.et_name)).getText().toString();
                            if (!name.trim().equals("")) {
                                FileMolder.this.shortcutPretreatment(name);
                                break;
                            } else {
                                Utils.toast(FileMolder.this.mGLContext, (int) R.string.name_cannot_be_empty);
                                return;
                            }
                        }
                        name = ((EditText) this.dialog.findViewById(R.id.et_rename)).getText().toString();
                        if (!name.trim().equals("")) {
                            position = FileMolder.this.mFileAdapterView.getSelectedPosition();
                            Favorite favorite = FileMolder.this.mListInfo.getFavorite(position);
                            FileMolder.this.startTask(new RenameFavoriteTask(FileMolder.this.mHandler, AppConstant.HANDLER_TASK_RENAME_FAVORITE, FileMolder.this.getContext(), new CheckedFavorites(position, favorite, name)));
                            break;
                        }
                        Utils.toast(FileMolder.this.mGLContext, (int) R.string.name_cannot_be_empty);
                        return;
                    }
                    int checkNum;
                    Favorite[] params;
                    boolean[] checks = new boolean[FileMolder.this.mAdapterFile.getCount()];
                    if (FileMolder.this.mAdapterFile.isMultiChoose()) {
                        ArrayList<Favorite> favorites = new ArrayList();
                        boolean[] temp = FileMolder.this.mListInfo.getChecks();
                        for (int i = 0; i < temp.length; i++) {
                            if (temp[i]) {
                                checks[i] = true;
                                favorites.add(FileMolder.this.mListInfo.getFavorite(i));
                            }
                        }
                        checkNum = favorites.size();
                        params = new Favorite[favorites.size()];
                        favorites.toArray(params);
                    } else {
                        checks[FileMolder.this.mFileAdapterView.getSelectedPosition()] = true;
                        checkNum = 1;
                        params = new Favorite[]{FileMolder.this.mListInfo.getFavorite(position)};
                    }
                    FileMolder.this.startTask(new RemoveFavoriteTask(FileMolder.this.mHandler, AppConstant.HANDLER_TASK_REMOVE_FAVORITE, FileMolder.this.getContext(), params));
                    FileMolder.this.mAdapterFile.setMultiChoose(false);
                    FileMolder.this.mFileAdapterView.removeViews(checks, checkNum);
                    break;
                case R.id.bt_play:
                    openWithDialog = this.dialog;
                    if (!openWithDialog.isTask()) {
                        FileMolder.this.playBDMV(openWithDialog.getFile());
                        ZidooFileUtils.sendPauseBroadCast(FileMolder.this.getContext());
                        break;
                    }
                    openWithDialog.setOpenWith(1);
                    FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_DIALOG, 500);
                    break;
            }
            this.dialog.dismiss();
        }
    }

    private class InnerHandler extends Handler {
        static final int ADD_SMB = 262153;
        static final int CREATE_FILE = 262148;
        static final int DELETE = 262146;
        static final int NET_ERROR = 262165;
        static final int NFS_SEARCH = 262167;
        static final int OPEN_CUE = 262162;
        static final int PASTE = 262164;
        static final int POSTER = 262169;
        static final int REMOVE_FAVORITE = 262150;
        static final int REMOVE_SMB = 262160;
        static final int RENAME_FAVORITE = 262151;
        static final int RENAME_FILE = 262147;
        static final int SCAN_SMB = 262152;
        static final int SETTING = 262168;
        static final int SHORTCUT = 262166;
        static final int TOAST_ARG1 = 262161;
        static final int TOAST_OBJ = 262145;
        static final int VIEWPORT = 262149;

        public InnerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4096:
                    FileMolder.this.showFileList((Data) msg.obj);
                    return;
                case 4097:
                    if (FileMolder.this.mMenu == null) {
                        FileMolder.this.mMenu = new MenuView(FileMolder.this.mGLContext, FileMolder.this.mV);
                    }
                    FileMolder.this.mMenu.showMenu(msg.arg1, (FileMenu[]) msg.obj);
                    return;
                case AppConstant.HANDLER_OPEN_NFS_NET_ERROR /*8198*/:
                    Utils.toast(FileMolder.this.mGLContext, (int) R.string.open_host_error);
                    return;
                case AppConstant.HANDLER_DELAY_SHOW_OPERATER_DIALOG /*12289*/:
                    FileMolder.this.mOperateInfo.dialog.show();
                    return;
                case AppConstant.HANDLER_DELAY_SET_DETAILS /*12290*/:
                    if (AppConstant.sPrefereancesViewPort == 1) {
                        FileMolder.this.updateMarqueeName();
                    }
                    FileMolder.this.setDetails(msg.arg1, msg.obj);
                    return;
                case AppConstant.HANDLER_DELAY_SHOW_DIALOG /*12291*/:
                    FileMolder.this.showLoadDialog();
                    return;
                case AppConstant.HANDLER_DELAY_GONE_TOUCH /*12296*/:
                    FileMolder.this.goneTouchLayout();
                    return;
                case AppConstant.HANDLER_DELAY_SHOW_DIALOG_FOR_SCAN_SMB /*12297*/:
                    FileMolder.this.showScanRotateProgressBar();
                    return;
                case AppConstant.HANDLER_DELAY_GONE_DIALOG_FOR_SCAN_SMB /*12305*/:
                    FileMolder.this.mScanState = 0;
                    FileMolder.this.goneProgress();
                    return;
                case AppConstant.HANDLER_DELAY_REFRESH_MENU /*12306*/:
                    FileMolder.this.refreashMenu();
                    return;
                case AppConstant.HANDLER_TASK_OPEN_SMBDEVICE /*20481*/:
                    FileMolder.this.resultLoadSmbDevcie((Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_OPEN_SMB_SHARE /*20482*/:
                    FileMolder.this.resultLoadSmbShare((OpenSmbShareTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_OPEN_SMB_SHARE_DEVICE /*20483*/:
                    FileMolder.this.resultLoadSmbShareDevice((OpenSmbShareDeviceTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_OPEN_NFS /*20484*/:
                    FileMolder.this.resultLoadNfsDevice((OpenNfsTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_OPEN_NFS_SHARE /*20485*/:
                    FileMolder.this.resultLoadNfsShare((OpenNfsShareTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_ADD_SMB /*20486*/:
                    FileMolder.this.resultAddSmb((AddSmbTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_CREATE /*20487*/:
                    FileMolder.this.resultCreate((CreateTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_RENAME /*20488*/:
                    FileMolder.this.resultRename((RenameTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_DETAIL /*20489*/:
                    ((DetailTask.Result) msg.obj).send();
                    return;
                case AppConstant.HANDLER_TASK_OPEN_FILE /*20496*/:
                    FileMolder.this.resultOpenFile((FileOpenTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_OPEN_DIR /*20497*/:
                    FileMolder.this.resultOpenDir((OpenDirTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_QUERY_FAVORITE /*20498*/:
                    FileMolder.this.resultQueryFavorite((Favorite[]) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_REMOVE_FAVORITE /*20499*/:
                    FileMolder.this.resultRemoveFavorite(((Boolean) msg.obj).booleanValue());
                    return;
                case AppConstant.HANDLER_TASK_RENAME_FAVORITE /*20500*/:
                    FileMolder.this.resultRenameFavorite(((Boolean) msg.obj).booleanValue());
                    return;
                case AppConstant.HANDLER_TASK_FAST_IDENTIFY /*20501*/:
                    FileMolder.this.resultFastIdentify((FastIdentifyTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_REMOVE_SMB /*20502*/:
                    FileMolder.this.resultRemoveSmb(((Integer) msg.obj).intValue());
                    return;
                case AppConstant.HANDLER_TASK_FAVORITE /*20503*/:
                    FileMolder.this.resultFavorite((FavoriteTask.Result) msg.obj);
                    return;
                case AppConstant.HANDLER_TASK_SEND_SHORTCUT /*20504*/:
                    FileMolder.this.resultSendShortcut(((Boolean) msg.obj).booleanValue());
                    return;
                case AppConstant.HANDLER_TASK_IDENTIFY_INITIAL_PATH /*20505*/:
                    FileMolder.this.resultIdentifyInitialPath((IdentifyUriTask.Result) msg.obj);
                    return;
                case TOAST_OBJ /*262145*/:
                    Utils.toast(FileMolder.this.mGLContext, (String) msg.obj);
                    return;
                case DELETE /*262146*/:
                    FileMolder.this.showDeleteDialog(FileMolder.this.mGLContext);
                    return;
                case RENAME_FILE /*262147*/:
                    FileMolder.this.showRenameDialog(FileMolder.this.mGLContext);
                    return;
                case CREATE_FILE /*262148*/:
                    FileMolder.this.showCreateDialog(FileMolder.this.mGLContext);
                    return;
                case VIEWPORT /*262149*/:
                    FileMolder.this.showViewportDialog(FileMolder.this.mGLContext, ((Boolean) msg.obj).booleanValue());
                    return;
                case REMOVE_FAVORITE /*262150*/:
                    FileMolder.this.removeFavorite();
                    return;
                case RENAME_FAVORITE /*262151*/:
                    FileMolder.this.renameFavorite();
                    return;
                case SCAN_SMB /*262152*/:
                    new SearchDialog(FileMolder.this.getContext(), R.style.defaultDialog, FileMolder.this, FileMolder.this.mResearchModel).show();
                    return;
                case ADD_SMB /*262153*/:
                    new SmbAddDialog(FileMolder.this.getContext(), R.style.defaultDialog, FileMolder.this.mHandler, FileMolder.this, FileMolder.this.mListInfo).show();
                    return;
                case REMOVE_SMB /*262160*/:
                    FileMolder.this.removeSmb();
                    return;
                case TOAST_ARG1 /*262161*/:
                    Utils.toast(FileMolder.this.mGLContext, msg.arg1);
                    return;
                case OPEN_CUE /*262162*/:
                    String[] paths = (String[]) msg.obj;
                    FileMolder.this.openCUE(FileMolder.this.mGLContext, paths[0], paths[1]);
                    return;
                case PASTE /*262164*/:
                    FileMolder.this.paste();
                    return;
                case NET_ERROR /*262165*/:
                    FileMolder.this.showNetErrorDialog();
                    return;
                case SHORTCUT /*262166*/:
                    FileMolder.this.showShortcutDialog();
                    return;
                case NFS_SEARCH /*262167*/:
                    new NfsSearchDialog(FileMolder.this.getContext(), FileMolder.this.mNfsManager).show();
                    return;
                case SETTING /*262168*/:
                    new SettingDialog(FileMolder.this.getContext(), FileMolder.this).show();
                    FileMolder.this.goneMenu();
                    return;
                case POSTER /*262169*/:
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg.obj;
                    FileMolder.this.mGLContext.getGLView().notifyHandleMessage(message);
                    return;
                default:
                    return;
            }
        }
    }

    public FileMolder(GLContext glContext, BrowseInfo browseInfo, Bundle savedInstanceState) {
        boolean z = true;
        this.mGLContext = glContext;
        this.mBrowser = browseInfo;
        if (browseInfo == null || (browseInfo.getTarget() & 1) != 0) {
            z = false;
        }
        this.mIsHideMenu = z;
        this.mSavedInstanceState = savedInstanceState;
        this.mBoxModel = BoxModel.getModel(getContext(), BoxModel.sModel);
        this.mSambaManager = SambaManager.getManager(getContext(), BoxModel.sModel);
        this.mSambaManager.setOnRecvMsgListener(this);
        this.mNfsManager = NfsFactory.getNfsManager(getContext(), BoxModel.sModel);
        this.mNfsManager.setOnNfsSearchListener(this);
        this.mMenuManager = new MenuManager(glContext);
        this.mDynamicBroadcast = new DynamicBroadcast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("zidoo_usb_uninstall");
        filter.addAction(AppConstant.ACTION_INNER_USB_BROADCAST);
        glContext.registerReceiver(this.mDynamicBroadcast, filter);
    }

    public ListType getListType() {
        return this.mListType;
    }

    public Context getContext() {
        return this.mGLContext;
    }

    public BoxModel getBoxModel() {
        return this.mBoxModel;
    }

    public DeviceInfo getDevice() {
        int position = this.mV.listDevices.getSelectedPosition();
        if (position < 0 || position >= this.mAdapterDevice.getCount()) {
            return null;
        }
        return this.mAdapterDevice.getItem(position);
    }

    public HashMap<String, MountFile[]> getSmbShareDirs() {
        return this.mListInfo.getSmbShareDirs();
    }

    public void show(int menuType, FileMenu[] menus) {
        this.mHandler.obtainMessage(4097, menuType, 0, menus).sendToTarget();
    }

    public ArrayList<DeviceInfo> getDevices() {
        return this.mAdapterDevice.getDevices();
    }

    public void initView(MyView view) {
        this.mV = view;
        if (this.mSavedInstanceState != null) {
            this.mScreen = this.mSavedInstanceState.getInt(AppConstant.EXTRA_SCREEN);
        }
        AppConstant.sIsZH = Utils.isZh(getContext());
        SharedPreferences preferences = this.mGLContext.getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
        AppConstant.sPrefereancesViewPort = preferences.getInt(AppConstant.PREFEREANCES_VIEW_PORT, 1);
        AppConstant.sPrefereancesOperateMode = preferences.getBoolean(AppConstant.PREFEREANCES_OPERATE_MODE, true);
        AppConstant.sPrefereancesHidden = preferences.getBoolean(AppConstant.PREFEREANCES_HIDDEN, false);
        AppConstant.sPrefereancesUsbTips = preferences.getBoolean(AppConstant.PREFEREANCES_USB_TIPS, true);
        AppConstant.sPrefereancesSortWay = preferences.getInt(AppConstant.PREFEREANCES_SORT, 0);
        AppConstant.sPrefereancesSmbDisplay = preferences.getInt(AppConstant.PREFEREANCES_SMB_DISPLAY, 0);
        this.mHandler = new InnerHandler(getContext().getMainLooper());
        this.mAdapterDevice = new DeviceAdapter(this.mGLContext);
        this.mFileOperater = new FileOperater();
        if (isBrowseModel()) {
            FileOperater fileOperater = this.mFileOperater;
            this.mFileFilter = FileOperater.buildBrowsingFileFilter(this.mBrowser.getFilter());
        } else {
            this.mScreensNames = this.mGLContext.getResources().getStringArray(R.array.screens);
            this.mFileFilter = this.mFileOperater.buildFileFilter(this.mScreen, AppConstant.sPrefereancesHidden, preferences.getBoolean(AppConstant.PREFEREANCES_APK_VISIBLE, BoxModelConfig.DEFAULT_APK_VISIBLE_SET));
        }
        this.mFileOperater.setOnFileDeleteListener(new OnFileDeleteListener() {
            public void onInfo(String path, int num, int count) {
                FileMolder.this.mOperateInfo.currentFile.setText(path);
                FileMolder.this.mOperateInfo.currentSize.setText("" + (num + 1));
                FileMolder.this.mOperateInfo.progressBar.setProgress((int) ((((float) num) / ((float) count)) * 1000.0f));
            }

            public void onInit(int size) {
                FileMolder.this.mOperateInfo.totalSize.setText(" / " + size);
                FileMolder.this.mOperateInfo.title.setText(FileMolder.this.mGLContext.getString(R.string.delete_ing, new Object[]{Integer.valueOf(FileMolder.this.getCheckNum())}));
            }

            public void onSuccess(boolean stop) {
                FileMolder.this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SHOW_OPERATER_DIALOG);
                if (FileMolder.this.mOperateInfo.dialog.isShowing()) {
                    FileMolder.this.mOperateInfo.dialog.dismiss();
                }
                if (stop) {
                    FileMolder.this.mAdapterFile.setMultiChoose(false);
                    FileMolder.this.refreshList();
                    return;
                }
                FileMolder.this.mFileAdapterView.removeViews(FileMolder.this.getChecks(), FileMolder.this.getCheckNum());
                FileMolder.this.mAdapterFile.setMultiChoose(false);
                FileMolder.this.refreashMenu();
            }

            public void onError() {
                FileMolder.this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SHOW_OPERATER_DIALOG);
                if (FileMolder.this.mOperateInfo == null || !FileMolder.this.mOperateInfo.dialog.isShowing()) {
                    FileMolder.this.toast((int) R.string.operate_filed);
                } else {
                    FileMolder.this.mOperateInfo.title.setText(FileMolder.this.mGLContext.getString(R.string.operate_filed));
                }
                FileMolder.this.refreshList();
            }
        });
        this.mV.initView(this.mGLContext, this.mBrowser, this.mIsHideMenu);
    }

    public void getData() {
        this.mFileAdapterView = this.mV.fileAdaperView;
        initDevice();
        new AsyncTextureThread(this.mGLContext, new Texture[]{new Texture(R.drawable.icon_dir), new Texture(R.drawable.bg_menu), new Texture(R.drawable.bg_device), new Texture(R.drawable.selector), new Texture(R.drawable.selector_file_list), new Texture(R.drawable.img_list_selector), new Texture(R.drawable.selector_screen), new Texture(R.drawable.screen_selected), new Texture(R.drawable.title_back)}, this.mGLContext.getConfig().getGlEnvirnment()).start();
    }

    public boolean isHasInitData() {
        return this.mHasInitData;
    }

    public boolean isManipulating() {
        return this.mManipulating;
    }

    public boolean isMenuShowing() {
        return this.mIsMenuShow;
    }

    public FileFilter getFileFilter() {
        return this.mFileFilter;
    }

    private void initDevice() {
        new Thread(new Runnable() {
            public void run() {
                ArrayList<DeviceInfo> deviceInfos = new ArrayList();
                Favorite[] favorites = new Favorite[0];
                try {
                    FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_DIALOG, 500);
                    if (FileMolder.this.isShowDevice(1)) {
                        deviceInfos.add(new DeviceInfo(FileMolder.this.mGLContext.getString(R.string.favorite_device_title), DeviceType.SPECIAL_A));
                    }
                    if (FileMolder.this.isShowDevice(2)) {
                        deviceInfos.add(new DeviceInfo(Environment.getExternalStorageDirectory().getPath(), DeviceType.FLASH));
                    }
                    if (FileMolder.this.isShowDevice(4)) {
                        Iterator it = FileMolder.this.mBoxModel.getDeviceList(2, true).iterator();
                        while (it.hasNext()) {
                            deviceInfos.add(new DeviceInfo((ZDevice) it.next()));
                        }
                    }
                    if (FileMolder.this.isShowDevice(8)) {
                        deviceInfos.add(new DeviceInfo(FileMolder.this.mSambaManager.getSmbRoot(), DeviceType.SMB));
                    }
                    if (FileMolder.this.isShowDevice(16)) {
                        deviceInfos.add(new DeviceInfo(FileMolder.this.mNfsManager.getNfsRoot(), DeviceType.NFS));
                    }
                    ArrayList<Favorite> favoriteList = FavoriteDatabase.helper(FileMolder.this.getContext()).query();
                    favorites = new Favorite[favoriteList.size()];
                    favoriteList.toArray(favorites);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FileMolder.this.mHandler.obtainMessage(4096, new Data(deviceInfos, favorites)).sendToTarget();
            }
        }).start();
    }

    private synchronized void showFileList(Data data) {
        loadComplete();
        this.mLoadDialog = new TaskDialog(getContext());
        ArrayList<DeviceInfo> devices = data.getDevices();
        this.mListInfo.setFavorite(data.getFavorites());
        this.mAdapterDevice.setDeviceInfos(devices);
        this.mV.deviceState.refresh(devices);
        this.mManipulating = false;
        this.mFileAdapterView.requestFocus();
        this.mHasInitData = true;
        if (!isBrowseModel()) {
            this.mV.tvTitle.setText(this.mScreensNames[this.mScreen]);
        }
        int mode = this.mSavedInstanceState == null ? 0 : this.mSavedInstanceState.getInt(AppConstant.EXTRA_ENTRY_MODE);
        if (mode == 1) {
            ArrayList<ZDevice> mountedDevices = this.mSavedInstanceState.getParcelableArrayList(AppConstant.EXTRA_USB_DEVICE);
            if (mountedDevices == null || mountedDevices.size() <= 0) {
                defaultOpen(devices);
            } else {
                ZDevice device = (ZDevice) mountedDevices.get(0);
                int deviceIndex = 0;
                for (int i = 0; i < devices.size(); i++) {
                    File d = (DeviceInfo) devices.get(i);
                    if (d.getType() == device.getType() && d.getPath().equals(device.getPath())) {
                        this.mV.pathView.addPath(new ProtocolPath(d, 0));
                        this.mParent = d;
                        deviceIndex = i;
                        break;
                    }
                }
                openDevice(deviceIndex, true);
            }
        } else if (mode == 2) {
            String json = this.mSavedInstanceState.getString(AppConstant.EXTRA_FAST_IDENTIFIER);
            int openWith = this.mSavedInstanceState.getInt(AppConstant.EXTRA_OPEN_WITH);
            startTask(new FastIdentifyTask(this.mHandler, AppConstant.HANDLER_TASK_FAST_IDENTIFY, getContext(), this.mAdapterDevice.getDevices(), new FastIdentifier(json), this, this.mListInfo, this.mFileFilter, this.mSambaManager, this.mSambaManager.getSmbRoot(), this.mNfsManager.getNfsRoot(), openWith, false), false);
        } else if (this.mBrowser == null || this.mBrowser.getInitialPath() == null) {
            defaultOpen(devices);
        } else {
            startTask(new IdentifyUriTask(this.mHandler, AppConstant.HANDLER_TASK_IDENTIFY_INITIAL_PATH, this, this.mBrowser.getInitialPath()), false);
        }
    }

    private void defaultOpen(ArrayList<DeviceInfo> devices) {
        int deviceIndex = 0;
        for (int i = 0; i < devices.size(); i++) {
            DeviceInfo d = (DeviceInfo) devices.get(i);
            DeviceType type = d.getType();
            if (type == DeviceType.HDD || type == DeviceType.SD || type == DeviceType.TF) {
                this.mV.pathView.addPath(new ProtocolPath(d, 0));
                this.mParent = d;
                deviceIndex = i;
                break;
            }
        }
        openDevice(deviceIndex, true);
    }

    public boolean isBrowseModel() {
        return this.mBrowser != null;
    }

    public boolean isMulti() {
        return this.mAdapterFile.isMultiChoose();
    }

    public int getContentCount() {
        return this.mAdapterFile.contentCount();
    }

    public Boolean isVirtual() {
        boolean z = true;
        if (!(this.mListIndex == 1 && (this.mListType == ListType.SMB_FILE || this.mListType == ListType.NFS_FILE))) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    public Boolean isChildFile() {
        return Boolean.valueOf(FileOperater.isChildFile(this.mParent.getPath(), this.mMoveFiles));
    }

    public Boolean canBrowse() {
        boolean z = true;
        if (this.mAdapterFile.isEmpty()) {
            return Boolean.valueOf(false);
        }
        int position = this.mFileAdapterView.getSelectedPosition();
        switch (this.mListType) {
            case FAVORITE:
                if (position < 0 || position >= this.mAdapterFile.contentCount()) {
                    return Boolean.valueOf(false);
                }
                int type;
                Favorite favorite = this.mListInfo.getFavorite(position);
                if ((this.mBrowser.getFilter() & 1048576) != 0) {
                    type = Utils.getFavoriteType(favorite);
                } else {
                    type = favorite.getFileType();
                }
                if (((1 << type) & this.mBrowser.getTarget()) == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case SMB_DEVICE:
                if ((this.mBrowser.getTarget() & 2097152) == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case NFS_DEVICE:
                if ((this.mBrowser.getTarget() & 4194304) == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            default:
                if (position < 0 || position >= this.mAdapterFile.contentCount() || ((1 << FileType.getType(this.mListInfo.getChild(position))) & this.mBrowser.getTarget()) == 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
        }
    }

    public File getFile() {
        int position = this.mFileAdapterView.getSelectedPosition();
        File[] files = this.mListInfo.getChildren();
        if (files == null || position < 0 || position >= files.length) {
            return null;
        }
        return files[position];
    }

    public boolean isSavedSmb() {
        int position = this.mFileAdapterView.getSelectedPosition();
        if (this.mAdapterFile.isEmpty() || position < 0 || position >= this.mListInfo.smbSize(1)) {
            return false;
        }
        return true;
    }

    public File getParent() {
        return this.mParent;
    }

    public int getMoveFilesCount() {
        return this.mMoveFiles.length;
    }

    public int getCheckedNumber() {
        if (this.mAdapterFile.isMultiChoose()) {
            return this.mListInfo.getCheckNumber();
        }
        if (this.mAdapterFile.isEmpty() || this.mFileAdapterView.getSelectedPosition() == this.mAdapterFile.contentCount()) {
            return 0;
        }
        return 1;
    }

    private boolean isShowDevice(int tag) {
        return this.mBrowser == null || (this.mBrowser.getDeviceTag() & tag) != 0;
    }

    private void resetList() {
        switch (this.mListType) {
            case FAVORITE:
                this.mAdapterFile = AppConstant.sPrefereancesViewPort == 0 ? new FavoriteGridAdapter(this.mGLContext, this.mListInfo) : new FavoriteListAdapter(this.mGLContext, this.mListInfo);
                break;
            case SMB_DEVICE:
                this.mAdapterFile = AppConstant.sPrefereancesViewPort == 0 ? new SmbGridAdapter(this.mGLContext, this.mListInfo) : new SmbListAdapter(this.mGLContext, this.mListInfo);
                break;
            case NFS_DEVICE:
                this.mAdapterFile = AppConstant.sPrefereancesViewPort == 0 ? new NfsGridAdapter(this.mGLContext, this.mListInfo) : new NfsListAdapter(this.mGLContext, this.mListInfo);
                break;
            default:
                BaseFileAdapter fileGridAdapter;
                if (AppConstant.sPrefereancesViewPort == 0) {
                    fileGridAdapter = new FileGridAdapter(this.mGLContext, this.mListInfo);
                } else {
                    fileGridAdapter = new FileListAdapter(this.mGLContext, this.mListInfo);
                }
                this.mAdapterFile = fileGridAdapter;
                break;
        }
        this.mFileAdapterView.setAdapter(this.mAdapterFile);
    }

    public void openFile(int position) {
        if (this.mAdapterFile.isEmpty()) {
            if (this.mListIndex == 0) {
                showDeviceLists();
            } else {
                backUp();
            }
        } else if (position == this.mAdapterFile.contentCount()) {
            this.mFileAdapterView.setSelection(0);
        } else {
            switch (this.mListType) {
                case FAVORITE:
                    openFavorite(position);
                    return;
                case SMB_DEVICE:
                    openSmbDevice(position, false, 0);
                    return;
                case NFS_DEVICE:
                    openNfsDevice(position, false);
                    return;
                case SMB_FILE:
                    if (this.mListIndex == 1) {
                        openSmbShare(position, this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition), false, 0);
                        return;
                    }
                    break;
                case NFS_FILE:
                    break;
                case FILE:
                case SMB_SHARE:
                    break;
                default:
                    return;
            }
            if (this.mListIndex == 1) {
                openNfsShare(position, false);
                return;
            }
            if (this.mAdapterFile.isMultiChoose()) {
                this.mListInfo.check(position);
                this.mFileAdapterView.refreash();
                refreashMenu();
                return;
            }
            startTask(new FileOpenTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_FILE, this.mListInfo.getChildren(), this.mListType, this.mFileFilter, position, this.mBrowser));
        }
    }

    private void openFavorite(int position) {
        if (this.mAdapterFile.isMultiChoose()) {
            this.mListInfo.check(position);
            this.mFileAdapterView.refreash();
            refreashMenu();
            return;
        }
        int openWidth = (this.mBrowser == null || (this.mBrowser.getClickModel() & 8) != 0) ? -1 : 2;
        startTask(new FastIdentifyTask(this.mHandler, AppConstant.HANDLER_TASK_FAST_IDENTIFY, getContext(), this.mAdapterDevice.getDevices(), this.mListInfo.getFavorite(position), this, this.mListInfo, this.mFileFilter, this.mSambaManager, this.mSambaManager.getSmbRoot(), this.mNfsManager.getNfsRoot(), openWidth, true));
    }

    private void backUp() {
        switch (this.mListType) {
            case SMB_FILE:
                if (this.mListIndex == 1) {
                    this.mV.pathView.backUp();
                    this.mListIndex--;
                    backSmbDevice();
                    return;
                } else if (this.mListIndex == 2) {
                    openSmbDevice(this.mEnterSmbOrNfsDevicePosition, false, 4);
                    return;
                } else {
                    openDir(1, this.mParent, null);
                    return;
                }
            case NFS_FILE:
                if (this.mListIndex == 1) {
                    this.mV.pathView.backUp();
                    this.mListIndex--;
                    backNfsDevice();
                    return;
                } else if (this.mListIndex == 2) {
                    openNfsDevice(this.mEnterSmbOrNfsDevicePosition, true);
                    return;
                } else {
                    openDir(1, this.mParent, null);
                    return;
                }
            case FILE:
                openDir(1, this.mParent, null);
                return;
            case SMB_SHARE:
                if (this.mListIndex == 1) {
                    this.mV.pathView.backUp();
                    this.mListIndex--;
                    DeviceInfo smbDevice = getSmbDevice();
                    if (smbDevice != null) {
                        this.mParent = smbDevice;
                    }
                    backSmbDevice();
                    return;
                }
                openDir(1, this.mParent, null);
                return;
            default:
                return;
        }
    }

    private DeviceInfo getSmbDevice() {
        return this.mAdapterDevice.getSmbDevice();
    }

    private void openDir(int tag, File file, File extra) {
        if (this.mAdapterFile != null) {
            this.mAdapterFile.setLoading(true);
        }
        if (tag == 4) {
            startTask(new QueryFavoriteTask(this.mHandler, AppConstant.HANDLER_TASK_QUERY_FAVORITE, this.mListInfo.getFavorites(), this.mBrowser, this.mScreen));
            return;
        }
        startTask(new OpenDirTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_DIR, tag, file, extra, this.mFileFilter, this.mListType));
    }

    public void enterDir(File parent, File[] files) {
        this.mListInfo.setChildren(files);
        this.mParent = parent;
        this.mListIndex++;
        this.mAdapterFile.notifyDataSetChanged();
        this.mV.pathView.addPath(new FilePath(this.mParent));
    }

    public void startTask(BaseTask<?> task) {
        startTask(task, true);
    }

    public void startTask(BaseTask<?> task, boolean cancelable) {
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SHOW_DIALOG);
        if (!(this.mBaseTask == null || this.mBaseTask.isStop() || !this.mBaseTask.isAlive() || this.mBaseTask.isInterrupted())) {
            this.mBaseTask.cancel();
        }
        this.mBaseTask = task;
        this.mLoadDialog.setCancelable(cancelable);
        this.mLoadDialog.setTask(task);
        this.mLoadDialog.setOnCancelListener(this);
        this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_DIALOG, 500);
        task.start();
    }

    public void openCUE(Context context, String filePath, String audioPath) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_open_cue, null);
        final String str = filePath;
        final Context context2 = context;
        final String str2 = audioPath;
        OnClickListener onClickListener = new OnClickListener() {
            public void onClick(View v) {
                Uri uri;
                Intent intent;
                if (v.getId() == R.id.bt_open_cue) {
                    uri = Uri.fromFile(new File(str));
                    intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(uri, "text/plain");
                    context2.startActivity(intent);
                } else {
                    uri = Uri.fromFile(new File(str2));
                    intent = context2.getPackageManager().getLaunchIntentForPackage("com.zidoo.audioplayer");
                    if (intent == null) {
                        intent = new Intent("android.intent.action.VIEW");
                    }
                    intent.setDataAndType(uri, "audio/*");
                    context2.startActivity(intent);
                }
                dialog.dismiss();
            }
        };
        view.findViewById(R.id.bt_open_cue).setOnClickListener(onClickListener);
        view.findViewById(R.id.bt_play_cue).setOnClickListener(onClickListener);
        dialog.setContentView(view);
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
        view.findViewById(R.id.bt_play_cue).requestFocus();
    }

    private void openBDMV(OpenWithDialog dialog) {
        DialogClick click = new DialogClick(dialog, 1);
        dialog.findViewById(R.id.bt_look).setOnClickListener(click);
        dialog.findViewById(R.id.bt_play).setOnClickListener(click);
        if (AppConstant.sIsSupportBlurayNavigation) {
            View bn = dialog.findViewById(R.id.bt_bluray_navigation);
            bn.setVisibility(0);
            bn.setOnClickListener(click);
        }
        dialog.show();
    }

    public void openDevice(int position, boolean force) {
        if (this.mDeviceIndex != position || force) {
            PathInfo pathInfo;
            if (this.mAdapterFile != null) {
                this.mAdapterFile.setMultiChoose(false);
            }
            goneProgress();
            DeviceInfo deviceInfo = this.mAdapterDevice.getItem(position);
            switch (deviceInfo.getType()) {
                case SPECIAL_A:
                    this.mParent = deviceInfo;
                    pathInfo = new ProtocolPath(deviceInfo, 4);
                    this.mListType = ListType.FAVORITE;
                    openDir(4, this.mParent, null);
                    resetList();
                    refreashMenu();
                    break;
                case SMB:
                    if (!this.mHasScanedSmb || this.mListInfo.isSmbEmpty()) {
                        if (Utils.NetIsConnected(this.mGLContext)) {
                            scanSamba();
                        } else {
                            toast((int) R.string.net_error_reminds);
                            if (this.mAdapterDevice.getCount() == 1) {
                                this.mListType = ListType.SMB_DEVICE;
                                return;
                            }
                            return;
                        }
                    }
                    this.mParent = deviceInfo;
                    pathInfo = new ProtocolPath(deviceInfo, 1);
                    this.mListType = ListType.SMB_DEVICE;
                    resetList();
                    refreashMenu();
                    break;
                case NFS:
                    if (this.mNfsScanTime == 0 || this.mListInfo.nfsSize() == 0) {
                        if (Utils.NetIsConnected(this.mGLContext)) {
                            scanNfs();
                        } else {
                            toast((int) R.string.net_error_reminds);
                            return;
                        }
                    }
                    this.mParent = deviceInfo;
                    pathInfo = new ProtocolPath(deviceInfo, 2);
                    this.mListType = ListType.NFS_DEVICE;
                    resetList();
                    refreashMenu();
                    break;
                default:
                    this.mParent = deviceInfo;
                    pathInfo = new ProtocolPath(deviceInfo, 0);
                    this.mListType = ListType.FILE;
                    resetList();
                    refreshList();
                    break;
            }
            this.mDeviceIndex = position;
            this.mV.pathView.clear();
            this.mV.pathView.addPath(pathInfo);
            this.mV.deviceState.selectType(deviceInfo.getType());
            this.mListIndex = 0;
        }
        if (this.mV.layoutDevices != null) {
            goneDeviceLists();
        }
    }

    public void onNewIntent(Intent intent) {
        this.mSavedInstanceState = intent.getExtras();
        int mode = intent.getIntExtra(AppConstant.EXTRA_ENTRY_MODE, 0);
        if (mode == 1) {
            ArrayList<ZDevice> devices = intent.getParcelableArrayListExtra(AppConstant.EXTRA_USB_DEVICE);
            if (devices != null && devices.size() > 0) {
                AddUsb(devices, true);
            }
        } else if (mode == 2) {
            String json = this.mSavedInstanceState.getString(AppConstant.EXTRA_FAST_IDENTIFIER);
            int openWith = this.mSavedInstanceState.getInt(AppConstant.EXTRA_OPEN_WITH);
            FastIdentifier identifier = new FastIdentifier(json);
            startTask(new FastIdentifyTask(this.mHandler, AppConstant.HANDLER_TASK_FAST_IDENTIFY, getContext(), this.mAdapterDevice.getDevices(), identifier, this, this.mListInfo, this.mFileFilter, this.mSambaManager, this.mSambaManager.getSmbRoot(), this.mNfsManager.getNfsRoot(), openWith, false));
        }
    }

    public void onStartScan() {
        this.mScanState = 1;
        initScanProgress();
    }

    public void onAdd(SambaDevice smbDevice) {
        this.mListInfo.addSmb(smbDevice);
        this.mGLContext.postRender(new Runnable() {
            public void run() {
                if (FileMolder.this.mListType == ListType.SMB_DEVICE && !FileMolder.this.isLoading()) {
                    if (FileMolder.this.mListInfo.smbSize(2) == 1) {
                        FileMolder.this.mScanState = 2;
                        FileMolder.this.mV.tvProgress.setNextText(FileMolder.this.mGLContext.getString(R.string.find_out));
                        FileMolder.this.mHandler.postDelayed(new Runnable() {
                            public void run() {
                                FileMolder.this.mV.tvProgress.setNextText(FileMolder.this.mGLContext.getString(R.string.find_out));
                            }
                        }, 60);
                        FileMolder.this.setProgressAnim();
                    }
                    if (FileMolder.this.mListInfo.smbSize(0) == 1) {
                        FileMolder.this.mFileAdapterView.refreash();
                    } else {
                        FileMolder.this.mFileAdapterView.refreash();
                    }
                }
            }
        });
    }

    public void onProgress(int percent) {
        if (this.mListType == ListType.SMB_DEVICE && !isLoading()) {
            if (!this.mV.layoutProgress.isShow()) {
                this.mV.layoutProgress.setVisibility(true);
            }
            GlAnimation animation = this.mV.layoutRotateProgress.getAnimation();
            if (animation == null || animation.isFinish() || animation.isRunning()) {
                this.mV.tvProgress.setNextText(percent + " %");
            }
            this.mV.progressBar.setProgress(percent);
        }
    }

    public void onComplete(boolean incomplete) {
        if (this.mListType == ListType.SMB_DEVICE && !isLoading()) {
            if (!incomplete && this.mListInfo.smbSize(2) == 0) {
                this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_GONE_DIALOG_FOR_SCAN_SMB, 300);
                if (this.mListInfo.smbSize(1) == 0) {
                    if ((this.mV.layoutMenu == null || !isMenuShowing()) && !this.mV.layoutDevices.isShow()) {
                        showDeviceLists();
                    }
                    toast((int) R.string.not_find_smb);
                    return;
                }
                toast((int) R.string.not_find_new_smb);
            } else if (this.mScanState == 3 || this.mScanState == 1) {
                this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_GONE_DIALOG_FOR_SCAN_SMB, 300);
            } else {
                this.mScanState = 4;
            }
        }
    }

    public void onSavedSmbDevices(final ArrayList<SambaDevice> devices) {
        this.mGLContext.postRender(new Runnable() {
            public void run() {
                FileMolder.this.mListInfo.setSavedSmb(devices);
                FileMolder.this.mAdapterFile.notifyDataSetChanged();
            }
        });
    }

    public ArrayList<SambaDevice> onQuery() {
        return SmbDatabaseUtils.selectByAll(getContext());
    }

    private void scanSamba() {
        boolean z = true;
        this.mHasScanedSmb = true;
        SharedPreferences preferences = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
        boolean autoScan = preferences.getBoolean(AppConstant.PREFEREANCES_AUTO_SCAN_SMB, false);
        int model = preferences.getInt(AppConstant.PREFEREANCES_DEFAULT_SMB_SCAN_MODEL, 0);
        SambaManager sambaManager = this.mSambaManager;
        if (autoScan || this.mIsHideMenu) {
            z = false;
        }
        sambaManager.searchSambaDevice(model, z, null);
        new Thread(new Runnable() {
            public void run() {
                File[] mountFiles = FileMolder.this.mParent.listFiles();
                if (mountFiles != null && mountFiles.length > 0) {
                    for (File file : mountFiles) {
                        file.delete();
                    }
                }
            }
        }).start();
    }

    public void onNfsScanStart(int mode) {
        this.mV.tvProgress.setNextText("");
        if (mode != 2) {
            this.mListInfo.resetNfs();
        }
        this.mAdapterFile.notifyDataSetChanged();
        if (this.mIsMenuShow) {
            goneMenu();
        }
        showScanRotateProgressBar();
        refreashMenu();
    }

    public void onNfsDeveceChangeListener(int percent) {
        if (this.mListType == ListType.NFS_DEVICE) {
            GlAnimation animation = this.mV.layoutRotateProgress.getAnimation();
            if (animation == null || animation.isFinish() || animation.isRunning()) {
                this.mV.tvProgress.setNextText(percent + " %");
            }
            this.mV.progressBar.setProgress(percent);
        }
    }

    public void onCompleteListener(int mode, boolean success) {
        if (this.mListType == ListType.NFS_DEVICE) {
            goneProgress();
            if (mode == 2) {
                if (!success) {
                    Utils.toast(getContext(), (int) R.string.not_find_nfs);
                } else if (this.mNfsScanTime != 0) {
                    Utils.toast(getContext(), (int) R.string.add_nfs_success);
                } else if (!((this.mV.layoutMenu != null && isMenuShowing()) || this.mV.layoutDevices == null || this.mV.layoutDevices.isShow())) {
                    showDeviceLists();
                }
            } else if (this.mListInfo.nfsSize() == 0) {
                if (this.mNfsScanTime == 0 && ((this.mV.layoutMenu == null || !isMenuShowing()) && !this.mV.layoutDevices.isShow())) {
                    showDeviceLists();
                }
                toast((int) R.string.not_find_nfs);
            }
        }
        this.mNfsScanTime++;
    }

    public void OnNFSDeviceAddListener(NfsDevice device) {
        this.mListInfo.addNfs(device);
        if (this.mListType == ListType.NFS_DEVICE) {
            if (this.mListInfo.nfsSize() == 1) {
                this.mV.tvProgress.setNextText(this.mGLContext.getString(R.string.find_out));
                setProgressAnim();
                refreashMenu();
            }
            this.mGLContext.postRender(new Runnable() {
                public void run() {
                    FileMolder.this.mFileAdapterView.refreash();
                }
            });
            this.mV.fileAdaperView.invalidate();
        }
    }

    private void scanNfs() {
        if (this.mV.layoutProgress == null) {
            this.mV.initProgressBar(this.mGLContext);
        }
        initScanProgress();
        SharedPreferences sp = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0);
        switch (sp.getInt(AppConstant.PREFEREANCES_NFS_SCAN_MODE, 0)) {
            case 0:
                this.mNfsManager.scanDevices(sp.getInt(AppConstant.PREFEREANCES_NFS_SCAN_PORT, 111));
                return;
            case 1:
                this.mNfsManager.scanDevices();
                return;
            case 2:
                this.mNfsManager.scanDevices(sp.getString(AppConstant.PREFEREANCES_NFS_SCAN_IP, ""));
                return;
            default:
                return;
        }
    }

    private void setProgressAnim() {
        DisplayConfig display = this.mGLContext.getConfig().getDisplay();
        GlTranslateAnimation tar = new GlTranslateAnimation(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(-150.0f * display.sWidthRatio, -202.0f * display.sHeightRatio, 0.0f));
        tar.setDuration(100);
        tar.setFillAfter(true);
        tar.setStartDelay(500);
        GlScaleAnimation sar = new GlScaleAnimation(this.mV.layoutRotateProgress.Scale, new Vector3(0.0f, 0.0f, 1.0f));
        sar.setDuration(100);
        sar.setFillAfter(true);
        sar.setStartDelay(500);
        GlSetAnimation scar = new GlSetAnimation();
        scar.setDuration(100);
        scar.setFillAfter(true);
        scar.setStartDelay(500);
        scar.addAnimation(tar);
        scar.addAnimation(sar);
        scar.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                FileMolder.this.mV.layoutRotateProgress.setVisibility(false);
                FileMolder.this.mV.progressBar.setVisibility(true);
                DisplayConfig display = FileMolder.this.mGLContext.getConfig().getDisplay();
                GlTranslateAnimation glTranslateAnimation = new GlTranslateAnimation(new Vector3(-150.0f * display.sWidthRatio, -202.0f * display.sHeightRatio, 0.0f), new Vector3(-300.0f * display.sWidthRatio, -404.0f * display.sHeightRatio, 0.0f));
                glTranslateAnimation.setDuration(100);
                glTranslateAnimation.setFillAfter(true);
                GlScaleAnimation glScaleAnimation = new GlScaleAnimation(new Vector3(0.0f, 0.0f, 1.0f), new Vector3(1000.0f * display.sScaleX, 2.0f * display.sScaleY, 1.0f));
                glScaleAnimation.setDuration(100);
                glScaleAnimation.setFillAfter(true);
                GlSetAnimation sa = new GlSetAnimation();
                sa.setDuration(100);
                sa.setFillAfter(true);
                sa.addAnimation(glTranslateAnimation);
                sa.addAnimation(glScaleAnimation);
                sa.setAnimatorListener(new AnimatorListener() {
                    public void onAnimationStart(GlAnimation animation) {
                    }

                    public void onAnimationRepeat(GlAnimation animation) {
                    }

                    public void onAnimationEnd(GlAnimation animation) {
                        if (FileMolder.this.mScanState == 4) {
                            FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_GONE_DIALOG_FOR_SCAN_SMB, 300);
                        } else {
                            FileMolder.this.mScanState = 3;
                        }
                    }

                    public void onAnimationCancel(GlAnimation animation) {
                    }
                });
                FileMolder.this.mV.progressBar.startAnimation(sa);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        this.mV.layoutRotateProgress.startAnimation(scar);
    }

    private void openSmbDevice(int position, boolean login, int flag) {
        boolean saved = this.mListInfo.isSavedSmb(position);
        SambaDevice device = this.mListInfo.getSmbDevice(position);
        if (device.getType() == 4) {
            startTask(new OpenSmbDeviceTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_SMBDEVICE, getContext(), device, position, saved, login, flag, this.mFileFilter, this.mListInfo.getSmbShareDirs(), this.mAdapterDevice.getItem(this.mDeviceIndex)));
            return;
        }
        startTask(new OpenSmbShareDeviceTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_SMB_SHARE_DEVICE, getContext(), device, this.mParent, this.mFileFilter, position, saved, login, flag, this.mSambaManager, this.mListInfo));
    }

    private void openSmbShare(int position, SambaDevice device, boolean login, int flag) {
        MountFile file = null;
        File child = this.mListInfo.getChild(position);
        if (child instanceof MountFile) {
            file = (MountFile) MountFile.class.cast(child);
        } else {
            for (MountFile mountFile : (MountFile[]) this.mListInfo.getSmbShareDirs().get(device.getIp())) {
                if (mountFile.getName().equals(child.getName())) {
                    file = mountFile;
                    break;
                }
            }
        }
        if (file == null) {
            file = new MountFile(this.mParent.getPath(), ZidooFileUtils.encodeCommand(child.getName()), "smb://" + device.getIp() + "/" + child.getName() + "/", child.getName());
        }
        startTask(new OpenSmbShareTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_SMB_SHARE, getContext(), device, this.mListInfo.getSavedSmbShare(device.getIp(), child.getName()), this.mFileFilter, this.mParent, file, position, login, flag, this.mSambaManager));
    }

    private void showLoginSmbDialog(Context context, SambaDevice smbDevice, int position, boolean isServer, int flag) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.lock_input_dialog, null);
        final EditText etUser = (EditText) view.findViewById(R.id.et_user);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        Button btOk = (Button) view.findViewById(R.id.lock_input_ok);
        Button btCancel = (Button) view.findViewById(R.id.lock_input_set);
        final CheckBox prompt = (CheckBox) view.findViewById(R.id.lock_ac_isport);
        ((TextView) view.findViewById(R.id.lock_input_hit)).setText(context.getString(R.string.connect_smb, new Object[]{smbDevice.getName()}));
        if (!smbDevice.getUser().equals("guest")) {
            etUser.setText(smbDevice.getUser());
            etPassword.setText(smbDevice.getPassWord());
        }
        prompt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etUser.setEnabled(false);
                    etPassword.setEnabled(false);
                    etUser.setFocusable(false);
                    etPassword.setFocusable(false);
                    return;
                }
                etUser.setEnabled(true);
                etPassword.setEnabled(true);
                etUser.setFocusable(true);
                etPassword.setFocusable(true);
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        btCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        final SambaDevice sambaDevice = smbDevice;
        final boolean z = isServer;
        final int i = position;
        final int i2 = flag;
        btOk.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                String user;
                String password;
                if (prompt.isChecked()) {
                    user = "guest";
                    password = "";
                } else {
                    user = etUser.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    if (user.equals("")) {
                        FileMolder.this.toast((int) R.string.input_user);
                        return;
                    }
                }
                sambaDevice.setUser(user);
                sambaDevice.setPassWord(password);
                dialog.setOnDismissListener(null);
                if (z) {
                    FileMolder.this.openSmbDevice(i, true, i2);
                } else {
                    FileMolder.this.openSmbShare(i, sambaDevice, true, i2);
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        Window win = dialog.getWindow();
        LayoutParams params = win.getAttributes();
        params.x = 0;
        params.y = -20;
        win.setAttributes(params);
        dialog.show();
        etUser.requestFocus();
    }

    private void openNfsDevice(int position, boolean back) {
        NfsDevice device = this.mListInfo.getNfs(position);
        startTask(new OpenNfsTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_NFS, getContext(), device, position, this.mListInfo.getNfsShareDirs(), this.mFileFilter, this.mAdapterDevice.getItem(this.mDeviceIndex), back));
    }

    private void openNfsShare(int position, boolean copy) {
        MountFile mountFile = null;
        File file = this.mListInfo.getChild(position);
        NfsDevice nfsDevice = this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition);
        if (file instanceof MountFile) {
            mountFile = (MountFile) file;
        } else {
            for (MountFile mf : (MountFile[]) this.mListInfo.getNfsShareDirs().get(nfsDevice.ip)) {
                if (mf.getShareName().equals(file.getName())) {
                    mountFile = mf;
                    break;
                }
            }
        }
        if (mountFile == null) {
            mountFile = new MountFile(this.mParent.getPath(), ZidooFileUtils.encodeCommand(file.getName()), file.getPath(), file.getName());
        }
        startTask(new OpenNfsShareTask(this.mHandler, AppConstant.HANDLER_TASK_OPEN_NFS_SHARE, getContext(), mountFile, this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition), copy, this.mFileFilter));
    }

    private void initScanProgress() {
        if (this.mV.layoutProgress == null) {
            this.mV.initProgressBar(this.mGLContext);
        }
        this.mV.tvProgress.setNextText("0 %");
        this.mV.layoutRotateProgress.translate(0.0f, 0.0f, 0.0f);
        this.mV.layoutRotateProgress.scale(1.0f, 1.0f, 1.0f);
        this.mV.layoutRotateProgress.clearAnimation();
        this.mV.progressBar.clearAnimation();
        showScanRotateProgressBar();
    }

    private void showScanRotateProgressBar() {
        this.mV.layoutRotateProgress.translate(0.0f, 0.0f, 0.0f);
        this.mV.layoutRotateProgress.scale(1.0f, 1.0f, 1.0f);
        this.mV.layoutProgress.setVisibility(true);
        this.mV.layoutRotateProgress.setVisibility(true);
        this.mV.progressBar.setVisibility(false);
    }

    private void goneProgress() {
        if (this.mV.layoutProgress != null) {
            this.mV.layoutProgress.setVisibility(false);
            this.mManipulating = false;
        }
    }

    private void refreshList() {
        if (this.mListType == ListType.SMB_FILE && this.mListIndex == 1) {
            this.mListInfo.getSmbShareDirs().remove(this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition).getIp());
            openSmbDevice(this.mEnterSmbOrNfsDevicePosition, false, 0);
        } else if (this.mListType == ListType.NFS_FILE && this.mListIndex == 1) {
            this.mListInfo.getNfsShareDirs().remove(this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition).ip);
            openNfsDevice(this.mEnterSmbOrNfsDevicePosition, false);
        } else {
            openDir(this.mListType == ListType.FAVORITE ? 4 : 2, this.mParent, null);
        }
    }

    public void updatePageAndDetails(int position) {
        if (position >= 0 && position < this.mAdapterFile.getCount()) {
            this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SET_DETAILS);
            if (this.mDetailTask != null) {
                this.mDetailTask.cancel();
            }
            if (this.mAdapterFile.isEmpty()) {
                this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_REFRESH_MENU);
                this.mV.tvIndexAndCount.setText("0");
                this.mV.layoutDetails.setDetails(false, null, null, null, null, 0);
                refreashMenu();
            } else if (position == this.mAdapterFile.contentCount()) {
                this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_REFRESH_MENU);
                this.mV.tvIndexAndCount.setText((position + 1) + "/" + this.mAdapterFile.getCount());
                this.mV.layoutDetails.setDetails(false, null, null, null, null, 0);
                refreashMenu();
            } else {
                this.mV.tvIndexAndCount.setText((position + 1) + "/" + this.mAdapterFile.getCount());
                switch (this.mListType) {
                    case FAVORITE:
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mListType, this.mListInfo.getFavorite(position))), 500);
                        break;
                    case SMB_DEVICE:
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mListType, this.mListInfo.getSmbDevice(position))), 500);
                        break;
                    case SMB_FILE:
                        String share;
                        boolean isShare = this.mListIndex == 1;
                        File file = isShare ? new File(this.mParent, getSelectedFile(position).getName()) : getSelectedFile(position);
                        SambaDevice smb = this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition);
                        DeviceInfo device = this.mAdapterDevice.getItem(this.mDeviceIndex);
                        String uri = file.getPath().substring(device.getPath().length() + 1);
                        int s = uri.indexOf("/");
                        if (s == -1) {
                            share = uri;
                        } else {
                            share = uri.substring(0, s);
                        }
                        SambaDevice smbs = this.mListInfo.getSavedSmbShare(smb.getIp(), share);
                        if (smbs != null) {
                            smb = smbs;
                        }
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mListType, smb, device, file, isShare)), 500);
                        break;
                    case NFS_FILE:
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mListType, this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition), this.mAdapterDevice.getItem(this.mDeviceIndex), this.mListInfo.getChild(position), this.mListIndex == 1)), 500);
                        break;
                    case FILE:
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mAdapterDevice.getItem(this.mDeviceIndex), this.mListType, this.mListInfo.getChild(position))), 500);
                        break;
                    case SMB_SHARE:
                        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(AppConstant.HANDLER_DELAY_SET_DETAILS, new Param(getContext(), this.mV.layoutDetails, this.mListType, this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition), this.mAdapterDevice.getItem(this.mDeviceIndex), this.mListInfo.getChild(position), this.mListIndex == 1)), 500);
                        break;
                }
                delayUpdateBrowseMenu();
                delayUpdateMenu();
            }
        }
    }

    private void delayUpdateMenu() {
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_REFRESH_MENU);
        this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_REFRESH_MENU, 500);
    }

    private void delayUpdateBrowseMenu() {
        if (isBrowseModel()) {
            this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_REFRESH_MENU);
            this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_REFRESH_MENU, 500);
        }
    }

    public void nothingSelected() {
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SET_DETAILS);
        this.mV.layoutDetails.setDetails(false, null, null, null, null, 0);
    }

    private void updateMarqueeName() {
        if (this.mListItemName != null) {
            this.mListItemName.stopMarquee();
        }
        GameObject selectedView = this.mFileAdapterView.getSelectedView();
        if (selectedView != null) {
            Object tag = selectedView.getTag();
            if (tag == null) {
                return;
            }
            if (tag instanceof ListViewHolder) {
                this.mListItemName = ((ListViewHolder) tag).name;
                this.mListItemName.marquee();
            } else if (tag instanceof SmbViewHolder) {
                this.mListItemName = ((SmbViewHolder) tag).name;
                this.mListItemName.marquee();
            }
        }
    }

    public boolean backFileList() {
        if (this.mAdapterFile.isMultiChoose()) {
            this.mAdapterFile.setMultiChoose(false);
            this.mFileAdapterView.refreash();
            refreashMenu();
            return true;
        } else if (this.mListIndex == 0) {
            return false;
        } else {
            backUp();
            return true;
        }
    }

    public boolean exit() {
        return this.mAdapterFile.isMultiChoose() || this.mListIndex > 0;
    }

    private void backSmbDevice() {
        this.mListType = ListType.SMB_DEVICE;
        resetList();
        refreashMenu();
        this.mFileAdapterView.setSelection(this.mEnterSmbOrNfsDevicePosition);
        if (!this.mHasScanedSmb || this.mListInfo.isSmbEmpty()) {
            scanSamba();
        }
    }

    private void backNfsDevice() {
        this.mListType = ListType.NFS_DEVICE;
        resetList();
        refreashMenu();
        goneDetail();
        this.mFileAdapterView.setSelection(this.mEnterSmbOrNfsDevicePosition);
        if (this.mNfsScanTime == 0) {
            scanNfs();
        }
    }

    public void onLoadFail() {
        if (this.mLoadFailNumber != -1 && ListType.isNetFile(this.mListType) && this.mListIndex > 1) {
            if (this.mLoadFailNumber > 1) {
                this.mLoadFailNumber = -1;
                showRemoteDeviceDisconnectDialog();
                return;
            }
            this.mLoadFailNumber++;
        }
    }

    public void destroy() {
        loadComplete();
        if (this.mSambaManager != null) {
            this.mSambaManager.destory();
        }
        this.mBoxModel.destory();
        getContext().unregisterReceiver(this.mDynamicBroadcast);
        AppConstant.sUsb = null;
        AppConstant.sHdd = null;
    }

    public boolean isHideMenu() {
        return this.mIsHideMenu;
    }

    public void showMenu() {
        if (this.mIsHideMenu) {
            Message message = Message.obtain();
            message.what = 0;
            this.mGLContext.getGLView().notifyHandleMessage(message);
            return;
        }
        goneTouchLayout();
        this.mIsMenuShow = true;
        this.mV.showMenu(this.mGLContext);
        this.mV.imgUp.setVisibility(false);
        this.mV.deviceState.setVisibility(false);
        this.mV.menu.requestFocus();
        this.mV.layoutReminds.setReminds(-1);
        this.mFileAdapterView.setShowSelector(true);
        this.mFileAdapterView.getSelector().setAlpha(0.25f);
    }

    public void goneMenu() {
        this.mV.hideMenu(this.mGLContext);
        this.mIsMenuShow = false;
        this.mFileAdapterView.setShowSelector(false);
        this.mFileAdapterView.getSelector().setAlpha(1.0f);
        this.mFileAdapterView.requestFocus();
        this.mV.layoutReminds.setReminds(0);
    }

    public void showDeviceLists() {
        if (this.mV.layoutDevices == null) {
            this.mV.initDeviceLists(this.mGLContext);
            this.mV.setDeviceUpDownReminds(this.mGLContext, this.mAdapterDevice.getCount(), this.mV.listDevices.getSelectedPosition());
            this.mV.listDevices.setAdapter(this.mAdapterDevice);
            if (this.mDeviceIndex != 0) {
                this.mV.listDevices.setSelection(this.mDeviceIndex);
            }
        }
        this.mV.layoutDevices.setVisibility(true);
        this.mV.imgUp.setVisibility(false);
        this.mV.deviceState.setVisibility(false);
        this.mV.listDevices.requestFocus();
        goneDetail();
        goneTouchLayout();
        setUsbUnMountReminds(this.mV.listDevices.getSelectedPosition());
        DisplayConfig display = this.mGLContext.getConfig().getDisplay();
        GlTranslateAnimation translateAnimation = new GlTranslateAnimation(new Vector3(-1500.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(-402.0f * display.sWidthRatio, 0.0f, 0.0f));
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        this.mV.layoutDevices.startAnimation(translateAnimation);
    }

    @Deprecated
    void setDevicelistTitle(GameObject v, float y) {
        Vector3 p = v.Position;
        GlTranslateAnimation animation = new GlTranslateAnimation(p, new Vector3(p.X, this.mGLContext.getConfig().getDisplay().sHeightRatio * y, p.Z));
        animation.setDuration(100);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    public void goneDeviceLists() {
        DisplayConfig display = this.mGLContext.getConfig().getDisplay();
        GlTranslateAnimation translateAnimation = new GlTranslateAnimation(new Vector3(-402.0f * display.sWidthRatio, 0.0f, 0.0f), new Vector3(-1500.0f * display.sWidthRatio, 0.0f, 0.0f));
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        this.mV.layoutDevices.startAnimation(translateAnimation);
        translateAnimation.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                FileMolder.this.mV.layoutDevices.setVisibility(false);
                FileMolder.this.mV.imgUp.setVisibility(true);
                FileMolder.this.mV.deviceState.setVisibility(true);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        if (this.mListType != ListType.NFS_DEVICE) {
            showDetail();
        }
        this.mV.layoutReminds.setReminds(0);
        this.mFileAdapterView.requestFocus();
    }

    public void showScreens() {
        if (this.mV.vgScreens == null) {
            this.mV.initScreens(this.mGLContext, this.mScreen, this.mScreensNames);
        }
        Screens screensView = this.mV.vgScreens;
        this.mV.imgUp.setVisibility(false);
        this.mV.deviceState.setVisibility(false);
        screensView.setVisibility(true);
        DisplayConfig display = this.mGLContext.getConfig().getDisplay();
        GlTranslateAnimation screenAnimation = new GlTranslateAnimation(new Vector3(0.0f, 550.0f * display.sHeightRatio, 0.0f), new Vector3(0.0f, 420.0f * display.sHeightRatio, 0.0f));
        screenAnimation.setDuration(200);
        screenAnimation.setInterpolator(new DecelerateInterpolator());
        screenAnimation.setFillAfter(true);
        screensView.requestFocus();
        screensView.startAnimation(screenAnimation);
        goneDetail();
        goneTouchLayout();
        this.mV.layoutReminds.setReminds(-1);
        GlTranslateAnimation fileAnimation = new GlTranslateAnimation(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, -80.0f * display.sHeightRatio, -0.5f));
        fileAnimation.setDuration(200);
        fileAnimation.setInterpolator(new DecelerateInterpolator());
        fileAnimation.setFillAfter(true);
        this.mV.layoutFile.startAnimation(fileAnimation);
    }

    public void goneScreens() {
        final Screens screensView = this.mV.vgScreens;
        DisplayConfig display = this.mGLContext.getConfig().getDisplay();
        GlTranslateAnimation screenAnimation = new GlTranslateAnimation(new Vector3(0.0f, 420.0f * display.sHeightRatio, 0.0f), new Vector3(0.0f, 550.0f * display.sHeightRatio, 0.0f));
        screenAnimation.setDuration(200);
        screenAnimation.setInterpolator(new DecelerateInterpolator());
        screenAnimation.setFillAfter(true);
        screensView.requestFocus();
        screensView.startAnimation(screenAnimation);
        if (this.mListType != ListType.NFS_DEVICE) {
            showDetail();
        }
        this.mV.layoutReminds.setReminds(0);
        GlTranslateAnimation fileAnimation = new GlTranslateAnimation(new Vector3(0.0f, -80.0f * display.sHeightRatio, -0.5f), new Vector3(0.0f, 0.0f, 0.0f));
        fileAnimation.setDuration(200);
        fileAnimation.setInterpolator(new DecelerateInterpolator());
        fileAnimation.setFillAfter(true);
        this.mV.layoutFile.startAnimation(fileAnimation);
        screenAnimation.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                FileMolder.this.mV.imgUp.setVisibility(true);
                FileMolder.this.mV.deviceState.setVisibility(true);
                screensView.setVisibility(false);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        this.mFileAdapterView.requestFocus();
    }

    @Deprecated
    public void showTouchLayout() {
        if (this.mV.layoutTouch == null) {
            this.mV.initTouchLayout(this.mGLContext);
        }
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_GONE_TOUCH);
        this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_GONE_TOUCH, 5000);
        if (!this.mV.layoutTouch.isShow()) {
            this.mV.layoutTouch.setVisibility(true);
            GlAlphaAnimation alphaAnimation = new GlAlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(2000);
            alphaAnimation.setFillAfter(true);
            this.mV.layoutTouch.startAnimation(alphaAnimation);
        }
    }

    public void goneTouchLayout() {
        if (this.mV.layoutTouch != null && this.mV.layoutTouch.isShow()) {
            GlAlphaAnimation alphaAnimation = new GlAlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimatorListener(new AnimatorListener() {
                public void onAnimationStart(GlAnimation animation) {
                }

                public void onAnimationRepeat(GlAnimation animation) {
                }

                public void onAnimationEnd(GlAnimation animation) {
                    FileMolder.this.mV.layoutTouch.setVisibility(false);
                }

                public void onAnimationCancel(GlAnimation animation) {
                }
            });
            this.mV.layoutTouch.startAnimation(alphaAnimation);
        }
    }

    public void setScreen(int screen) {
        this.mScreen = screen;
        this.mV.tvTitle.setText(this.mScreensNames[screen]);
        this.mFileFilter = this.mFileOperater.buildFileFilter(this.mScreen, AppConstant.sPrefereancesHidden, getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getBoolean(AppConstant.PREFEREANCES_APK_VISIBLE, BoxModelConfig.DEFAULT_APK_VISIBLE_SET));
        if (!(this.mListIndex == 1 && (this.mListType == ListType.SMB_FILE || this.mListType == ListType.NFS_FILE))) {
            refreshList();
        }
        this.mV.tvTitle.invalidate();
        goneScreens();
    }

    public void operate(int position) {
        switch (this.mMenu.getType()) {
            case 0:
                operateFile(position);
                return;
            case 1:
                operateSmb(position);
                return;
            case 2:
                operateNfs(position);
                return;
            case 3:
                operateFavorite(position);
                return;
            default:
                return;
        }
    }

    private void operateFile(int position) {
        FileMenu menu = this.mMenu.getMenuInfo(position);
        if (menu.isAble()) {
            switch (menu.getType()) {
                case POSTER:
                    enterPoster();
                    return;
                case SELECT:
                    browsingFile(this.mV.fileAdaperView.getSelectedPosition());
                    return;
                case VIEWPORT:
                    viewport(false);
                    return;
                case CHOOSE:
                    multiChoose();
                    return;
                case COPY:
                    copy();
                    return;
                case CUT:
                    cut();
                    return;
                case DELETE:
                    delete();
                    return;
                case RENAME:
                    rename();
                    return;
                case PASTE:
                    this.mHandler.sendEmptyMessage(262164);
                    return;
                case CREATE:
                    createNewFile();
                    return;
                case SETTING:
                    this.mHandler.sendEmptyMessage(262168);
                    return;
                case FAVOR:
                    favorOrShorcut(true);
                    return;
                case SHORTCUT:
                    favorOrShorcut(false);
                    return;
                case REFREASH:
                    refreshList();
                    goneMenu();
                    return;
                default:
                    return;
            }
        }
    }

    private void operateSmb(int position) {
        FileMenu menu = this.mMenu.getMenuInfo(position);
        if (menu.isAble()) {
            switch (menu.getType()) {
                case SELECT:
                    browsingFile(this.mV.fileAdaperView.getSelectedPosition());
                    return;
                case VIEWPORT:
                    viewport(true);
                    return;
                case DELETE:
                    this.mHandler.sendEmptyMessage(262160);
                    return;
                case CREATE:
                    addSmb();
                    return;
                case SETTING:
                    this.mHandler.sendEmptyMessage(262168);
                    return;
                case FAVOR:
                    favorOrShorcut(true);
                    return;
                case SHORTCUT:
                    favorOrShorcut(false);
                    return;
                case SEARCH:
                    if (this.mResearchModel == -1) {
                        this.mResearchModel = getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getInt(AppConstant.PREFEREANCES_DEFAULT_SMB_SCAN_MODEL, 0);
                    }
                    this.mHandler.sendEmptyMessage(262152);
                    goneMenu();
                    return;
                default:
                    return;
            }
        }
    }

    private void operateNfs(int position) {
        FileMenu menu = this.mMenu.getMenuInfo(position);
        if (menu.isAble()) {
            switch (menu.getType()) {
                case SELECT:
                    browsingFile(this.mV.fileAdaperView.getSelectedPosition());
                    return;
                case VIEWPORT:
                    viewport(false);
                    return;
                case SETTING:
                    this.mHandler.sendEmptyMessage(262168);
                    return;
                case FAVOR:
                    favorOrShorcut(true);
                    return;
                case SHORTCUT:
                    this.mHandler.sendEmptyMessage(262166);
                    return;
                case SEARCH:
                    researchNfs();
                    return;
                default:
                    return;
            }
        }
    }

    private void operateFavorite(int position) {
        FileMenu menu = this.mMenu.getMenuInfo(position);
        if (menu.isAble()) {
            switch (menu.getType()) {
                case SELECT:
                    browsingFavorite();
                    return;
                case VIEWPORT:
                    viewport(false);
                    return;
                case CHOOSE:
                    multiChoose();
                    return;
                case RENAME:
                    this.mHandler.sendEmptyMessage(262151);
                    return;
                case SETTING:
                    this.mHandler.sendEmptyMessage(262168);
                    return;
                case REMOVE:
                    this.mHandler.sendEmptyMessage(262150);
                    return;
                default:
                    return;
            }
        }
    }

    public boolean researchSmb(int model) {
        this.mResearchModel = model;
        if (this.mSambaManager.isSearching()) {
            toast((int) R.string.msg_searching);
            return false;
        }
        if (Utils.NetIsConnected(this.mGLContext)) {
            this.mListInfo.resetSmb();
            this.mAdapterFile.notifyDataSetChanged();
            this.mSambaManager.searchSambaDevice(model, false, this.mListInfo.getSavedSmbDevices());
            goneMenu();
        } else {
            toast((int) R.string.net_error_reminds);
        }
        return true;
    }

    private void addSmb() {
        this.mHandler.sendEmptyMessage(262153);
        goneMenu();
    }

    private void removeSmb() {
        final int position = this.mFileAdapterView.getSelectedPosition();
        final boolean forget = this.mListInfo.isSavedSmb(position);
        final SambaDevice device = (SambaDevice) this.mAdapterFile.getItem(this.mFileAdapterView.getSelectedPosition());
        final Context context = this.mGLContext;
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_delete);
        Button btOk = (Button) dialog.findViewById(R.id.bt_ok);
        Button btCancel = (Button) dialog.findViewById(R.id.bt_cancel);
        if (forget) {
            ((TextView) dialog.findViewById(R.id.tv_title)).setText(context.getString(R.string.logoff));
            ((TextView) dialog.findViewById(R.id.tv_delete_msg)).setText(context.getString(R.string.logoff_smb_msg, new Object[]{device.getName()}));
        } else {
            ((TextView) dialog.findViewById(R.id.tv_title)).setText(context.getString(R.string.delete));
            ((TextView) dialog.findViewById(R.id.tv_delete_msg)).setText(context.getString(R.string.delete_smb_msg, new Object[]{device.getName()}));
        }
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_cancel:
                        dialog.dismiss();
                        return;
                    case R.id.bt_ok:
                        if (forget) {
                            FileMolder.this.startTask(new RemoveSmbTask(FileMolder.this.mHandler, AppConstant.HANDLER_TASK_REMOVE_SMB, context, device, FileMolder.this.mParent, position, FileMolder.this.mSambaManager, FileMolder.this.mListInfo.getSmbShareDirs(), FileMolder.this.mListInfo.getSavedSmbDevices()));
                        } else {
                            FileMolder.this.mFileAdapterView.removeView(position);
                            FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_REFRESH_MENU, 100);
                        }
                        dialog.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        btOk.setOnClickListener(clickListener);
        btCancel.setOnClickListener(clickListener);
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
        btCancel.requestFocus();
        goneMenu();
    }

    private void researchNfs() {
        if (this.mNfsManager.isSearching()) {
            toast((int) R.string.msg_searching);
        } else if (Utils.NetIsConnected(this.mGLContext)) {
            this.mHandler.sendEmptyMessage(262167);
        } else {
            toast((int) R.string.net_error_reminds);
        }
    }

    private void multiChoose() {
        boolean z = true;
        if (this.mAdapterFile.isMultiChoose()) {
            ListInfo listInfo = this.mListInfo;
            if (this.mListInfo.isCheckAll()) {
                z = false;
            }
            listInfo.fillCheck(z);
        } else {
            this.mAdapterFile.setMultiChoose(true);
            this.mListInfo.resetChecks(this.mAdapterFile.contentCount());
            goneMenu();
        }
        refreashMenu();
        this.mFileAdapterView.refreash();
    }

    private void copy() {
        int position = this.mFileAdapterView.getSelectedPosition();
        if (this.mListType == ListType.SMB_FILE && this.mListIndex == 1) {
            File realFile = new File(this.mParent, this.mListInfo.getChild(position).getName());
            if (realFile.exists()) {
                this.mDeleteSrc = false;
                this.mMoveFiles = new File[1];
                this.mMoveFiles[0] = realFile;
                this.mAdapterFile.setMultiChoose(false);
                this.mFileAdapterView.refreash();
                goneMenu();
                toast(this.mGLContext.getString(R.string.has_copied_files, new Object[]{Integer.valueOf(this.mMoveFiles.length)}));
                return;
            }
            openSmbShare(position, this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition), false, 1);
        } else if (this.mListType == ListType.NFS_FILE && this.mListIndex == 1) {
            openNfsShare(position, true);
        } else {
            this.mDeleteSrc = false;
            this.mMoveFiles = getCheckedFiles();
            this.mAdapterFile.setMultiChoose(false);
            this.mFileAdapterView.refreash();
            refreashMenu();
            goneMenu();
            toast(this.mGLContext.getString(R.string.has_copied_files, new Object[]{Integer.valueOf(this.mMoveFiles.length)}));
        }
    }

    private void cut() {
        this.mDeleteSrc = true;
        this.mMoveFiles = getCheckedFiles();
        this.mMovedDevice = this.mDeviceIndex;
        this.mAdapterFile.setMultiChoose(false);
        this.mFileAdapterView.refreash();
        goneMenu();
        toast(this.mGLContext.getString(R.string.has_cut_files, new Object[]{Integer.valueOf(this.mMoveFiles.length)}));
    }

    public void delete() {
        this.mHandler.sendEmptyMessage(262146);
        goneMenu();
    }

    public void rename() {
        this.mHandler.sendEmptyMessage(262147);
        goneMenu();
    }

    private void renameFavorite() {
        Favorite favorite = this.mListInfo.getFavorite(this.mFileAdapterView.getSelectedPosition());
        Dialog dialog = new Dialog(getContext(), R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_rename);
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(R.string.operate_rename);
        ((EditText) dialog.findViewById(R.id.et_rename)).setText(favorite.getName());
        DialogClick click = new DialogClick(dialog, 1);
        dialog.findViewById(R.id.bt_ok).setOnClickListener(click);
        dialog.findViewById(R.id.bt_cancel).setOnClickListener(click);
        dialog.show();
        goneMenu();
    }

    private void removeFavorite() {
        Dialog dialog = new Dialog(getContext(), R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_delete);
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(R.string.menu_favorite_remove);
        ((TextView) dialog.findViewById(R.id.tv_delete_msg)).setText(this.mGLContext.getString(R.string.msg_delete_favorite, new Object[]{Integer.valueOf(getCheckNum())}));
        DialogClick click = new DialogClick(dialog, 0);
        dialog.findViewById(R.id.bt_ok).setOnClickListener(click);
        dialog.findViewById(R.id.bt_cancel).setOnClickListener(click);
        dialog.show();
        goneMenu();
    }

    private void browsingFavorite() {
        Favorite favorite = this.mListInfo.getFavorite(this.mFileAdapterView.getSelectedPosition());
        FileIdentifier identifier = null;
        File file;
        int i;
        switch (favorite.getTag()) {
            case 0:
                file = new File(Environment.getExternalStorageDirectory(), favorite.getUri());
                if (file.exists()) {
                    identifier = new FileIdentifier(0, favorite.getUri(), "Flash");
                    identifier.setExtra(file.getPath());
                    break;
                }
                break;
            case 1:
                ArrayList<DeviceInfo> devices = this.mAdapterDevice.getDevices();
                String uuid = favorite.getUuid();
                for (i = 0; i < devices.size(); i++) {
                    DeviceInfo device = (DeviceInfo) devices.get(i);
                    DeviceType type = device.getType();
                    if (type == DeviceType.SD || type == DeviceType.HDD || type == DeviceType.TF) {
                        if (device.getBlock() == null || device.getBlock().getUuid() == null) {
                            if ((device.getPath() + "/").equals(uuid)) {
                                file = new File(device, favorite.getUri());
                                if (file.exists()) {
                                    identifier = new FileIdentifier(1, favorite.getUri(), favorite.getUuid());
                                    identifier.setExtra(file.getPath());
                                    break;
                                }
                            } else {
                                continue;
                            }
                        } else if (device.getBlock().getUuid().equals(uuid)) {
                            file = new File(device, favorite.getUri());
                            if (file.exists()) {
                                identifier = new FileIdentifier(1, favorite.getUri(), favorite.getUuid());
                                identifier.setExtra(file.getPath());
                                break;
                            }
                        }
                    }
                }
                break;
            case 2:
            case 3:
            case 6:
                identifier = new FileIdentifier(2, favorite.getUri(), favorite.getUuid());
                identifier.setUser(favorite.getUser());
                identifier.setPassword(favorite.getPassword());
                identifier.setExtra(favorite.getUuid());
                break;
            case 4:
            case 5:
                String uri = favorite.getUri();
                i = uri.indexOf("/");
                identifier = new FileIdentifier(3, uri, i == -1 ? uri : uri.substring(0, i));
                break;
        }
        if (identifier == null) {
            toast((int) R.string.invalid_favorite);
        } else {
            this.mBrowser.onBrowsing(identifier);
        }
    }

    public void viewport(boolean isSmb) {
        this.mHandler.obtainMessage(262149, Boolean.valueOf(isSmb)).sendToTarget();
        goneMenu();
    }

    private void favorOrShorcut(boolean favor) {
        int i = 3;
        int position = this.mFileAdapterView.getSelectedPosition();
        if (this.mListType == ListType.SMB_DEVICE && !this.mListInfo.isSavedSmb(position)) {
            if (favor) {
                i = 2;
            }
            openSmbDevice(position, false, i);
        } else if (this.mListType == ListType.SMB_FILE && this.mListIndex == 1) {
            if (!this.mListInfo.getChild(position).exists()) {
                SambaDevice device = this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition);
                if (!favor) {
                    i = 4;
                }
                openSmbShare(position, device, false, i);
            } else if (favor) {
                favor(position);
            } else {
                this.mHandler.sendEmptyMessage(262166);
            }
        } else if (favor) {
            favor(position);
        } else {
            this.mHandler.sendEmptyMessage(262166);
        }
    }

    private void favor(int position) {
        FavoriteParam param = new FavoriteParam(this.mAdapterDevice.getItem(this.mDeviceIndex), this.mListType);
        param.setListIndex(this.mListIndex);
        switch (this.mListType) {
            case SMB_DEVICE:
                SambaDevice smb = this.mListInfo.getSmbDevice(position);
                param.setSmb(smb);
                param.setTag(smb.getType() == 4 ? 2 : 6);
                break;
            case NFS_DEVICE:
                param.setNfs(this.mListInfo.getNfs(position));
                param.setTag(4);
                break;
            case SMB_FILE:
                param.setFile(getSelectedFile(position));
                param.setSmb(this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition));
                param.setTag(3);
                break;
            case NFS_FILE:
                param.setFile(getSelectedFile(position));
                param.setNfs(this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition));
                param.setTag(5);
                break;
            case FILE:
                param.setFile(getSelectedFile(position));
                param.setTag(this.mAdapterDevice.getItem(this.mDeviceIndex).getType() == DeviceType.FLASH ? 0 : 1);
                break;
            case SMB_SHARE:
                param.setFile(getSelectedFile(position));
                param.setSmb(this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition));
                param.setTag(7);
                break;
        }
        startTask(new FavoriteTask(this.mHandler, AppConstant.HANDLER_TASK_FAVORITE, getContext(), param, this.mListInfo, this.mListIndex));
    }

    private void showShortcutDialog() {
        int position = this.mFileAdapterView.getSelectedPosition();
        String name = "";
        switch (this.mListType) {
            case SMB_DEVICE:
                name = this.mListInfo.getSmbDevice(position).getName();
                break;
            case NFS_DEVICE:
                name = this.mListInfo.getNfs(position).ip;
                break;
            case SMB_FILE:
            case NFS_FILE:
            case FILE:
            case SMB_SHARE:
                name = this.mListInfo.getChild(position).getName();
                break;
        }
        Dialog dialog = new Dialog(getContext(), R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_shortcut);
        ((EditText) dialog.findViewById(R.id.et_name)).setText(name);
        DialogClick click = new DialogClick(dialog, 2);
        View ok = dialog.findViewById(R.id.bt_ok);
        ok.setOnClickListener(click);
        dialog.findViewById(R.id.bt_cancel).setOnClickListener(click);
        ok.requestFocus();
        dialog.show();
    }

    private void shortcutPretreatment(String name) {
        String str = name;
        startTask(new ShortcutTask(this.mHandler, AppConstant.HANDLER_TASK_SEND_SHORTCUT, getContext(), this.mBoxModel, this.mAdapterDevice.getItem(this.mDeviceIndex), this.mListInfo, this.mListType, str, this.mFileAdapterView.getSelectedPosition(), this.mListIndex, this.mEnterSmbOrNfsDevicePosition));
    }

    private File getSelectedFile(int position) {
        if (this.mAdapterFile.isMultiChoose() && this.mListInfo.getCheckNumber() == 1) {
            boolean[] checks = this.mListInfo.getChecks();
            for (int i = 0; i < checks.length; i++) {
                if (checks[i]) {
                    return this.mListInfo.getChild(i);
                }
            }
        }
        return this.mListInfo.getChild(position);
    }

    private File[] getCheckedFiles() {
        if (this.mAdapterFile.isMultiChoose()) {
            boolean[] checks = this.mListInfo.getChecks();
            File[] children = this.mListInfo.getChildren();
            ArrayList<File> fileList = new ArrayList();
            for (int i = 0; i < checks.length; i++) {
                if (checks[i]) {
                    fileList.add(children[i]);
                }
            }
            File[] array = new File[fileList.size()];
            fileList.toArray(array);
            return array;
        }
        int position = this.mFileAdapterView.getSelectedPosition();
        return new File[]{this.mListInfo.getChild(position)};
    }

    private boolean[] getChecks() {
        if (this.mAdapterFile.isMultiChoose()) {
            boolean[] checks = new boolean[this.mAdapterFile.getCount()];
            boolean[] temp = this.mListInfo.getChecks();
            System.arraycopy(temp, 0, checks, 0, temp.length);
            return checks;
        }
        checks = new boolean[this.mAdapterFile.getCount()];
        checks[this.mFileAdapterView.getSelectedPosition()] = true;
        return checks;
    }

    private int getCheckNum() {
        return this.mAdapterFile.isMultiChoose() ? this.mListInfo.getCheckNumber() : 1;
    }

    private void showDeleteDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_delete);
        Button btOk = (Button) dialog.findViewById(R.id.bt_ok);
        Button btCancel = (Button) dialog.findViewById(R.id.bt_cancel);
        ((TextView) dialog.findViewById(R.id.tv_delete_msg)).setText(context.getString(R.string.delete_file_msg, new Object[]{Integer.valueOf(getCheckNum())}));
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_cancel:
                        dialog.setOnDismissListener(null);
                        dialog.dismiss();
                        return;
                    case R.id.bt_ok:
                        FileMolder.this.mHandler.sendEmptyMessageDelayed(AppConstant.HANDLER_DELAY_SHOW_OPERATER_DIALOG, 500);
                        FileMolder.this.setFileOperateDialog(context, false);
                        FileMolder.this.mFileOperater.deleteFilesByCommand(FileMolder.this.getCheckedFiles());
                        dialog.setOnDismissListener(null);
                        dialog.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        btOk.setOnClickListener(clickListener);
        btCancel.setOnClickListener(clickListener);
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                FileMolder.this.mAdapterFile.setMultiChoose(false);
                FileMolder.this.mFileAdapterView.refreash();
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
        btCancel.requestFocus();
    }

    private void showRenameDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null);
        dialog.setContentView(view);
        final EditText etRename = (EditText) view.findViewById(R.id.et_rename);
        try {
            etRename.setText(this.mListInfo.getChild(this.mFileAdapterView.getSelectedPosition()).getName());
        } catch (Exception e) {
            Log.e("FileMolder", e.toString());
        }
        Button btOk = (Button) view.findViewById(R.id.bt_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_cancel);
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_cancel:
                        dialog.dismiss();
                        return;
                    case R.id.bt_ok:
                        FileMolder.this.startTask(new RenameTask(FileMolder.this.mHandler, AppConstant.HANDLER_TASK_RENAME, dialog, FileMolder.this.mListInfo.getChild(FileMolder.this.mFileAdapterView.getSelectedPosition()), FileMolder.this.mParent, etRename.getText().toString().trim()));
                        return;
                    default:
                        return;
                }
            }
        };
        btOk.setOnClickListener(clickListener);
        btCancel.setOnClickListener(clickListener);
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                FileMolder.this.mAdapterFile.setMultiChoose(false);
                FileMolder.this.mFileAdapterView.refreash();
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
        etRename.requestFocus();
    }

    private void setFileOperateDialog(Context context, boolean copy) {
        this.mOperateInfo = new OperateInfo();
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_file_operate_progress);
        this.mOperateInfo.dialog = dialog;
        this.mOperateInfo.title = (TextView) dialog.findViewById(R.id.tv_operate_title);
        if (copy) {
            this.mOperateInfo.title.setText(context.getString(R.string.copy_file));
            ((TextView) dialog.findViewById(R.id.tv_file_unit)).setText(context.getString(R.string.size));
        } else {
            this.mOperateInfo.title.setText(context.getString(R.string.delete_file));
            ((TextView) dialog.findViewById(R.id.tv_file_unit)).setText(context.getString(R.string.count));
        }
        this.mOperateInfo.currentFile = (TextView) dialog.findViewById(R.id.tv_current_operate_file);
        this.mOperateInfo.currentSize = (TextView) dialog.findViewById(R.id.tv_current_file_size);
        this.mOperateInfo.totalSize = (TextView) dialog.findViewById(R.id.tv_total_size);
        this.mOperateInfo.progressBar = (ProgressBar) dialog.findViewById(R.id.progress);
        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                FileMolder.this.mFileOperater.stop();
            }
        });
    }

    private void showViewportDialog(Context context, boolean isSmb) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_list_viewport);
        RadioGroup rgViewPort = (RadioGroup) dialog.findViewById(R.id.rg_list_viewport);
        RelativeRadioGroup rgSort = (RelativeRadioGroup) dialog.findViewById(R.id.rg_list_sort);
        Button btOk = (Button) dialog.findViewById(R.id.bt_viewport_ok);
        if (AppConstant.sPrefereancesViewPort == 0) {
            rgViewPort.check(R.id.rb_viewport_grid);
        } else {
            rgViewPort.check(R.id.rb_viewport_list);
        }
        switch (AppConstant.sPrefereancesSortWay) {
            case 0:
                rgSort.check(R.id.rb_sort_0);
                break;
            case 1:
                rgSort.check(R.id.rb_sort_1);
                break;
            case 2:
                rgSort.check(R.id.rb_sort_2);
                break;
            case 3:
                rgSort.check(R.id.rb_sort_3);
                break;
            case 4:
                rgSort.check(R.id.rb_sort_4);
                break;
            case 5:
                rgSort.check(R.id.rb_sort_5);
                break;
            case 6:
                rgSort.check(R.id.rb_sort_6);
                break;
            case 7:
                rgSort.check(R.id.rb_sort_7);
                break;
        }
        RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_smb_mode_address:
                        AppConstant.sPrefereancesSmbDisplay = 1;
                        FileMolder.this.mAdapterFile.notifyDataSetChanged();
                        return;
                    case R.id.rb_smb_mode_user:
                        AppConstant.sPrefereancesSmbDisplay = 0;
                        FileMolder.this.mAdapterFile.notifyDataSetChanged();
                        return;
                    case R.id.rb_viewport_grid:
                        FileMolder.this.changeViewport(0);
                        return;
                    case R.id.rb_viewport_list:
                        FileMolder.this.changeViewport(1);
                        return;
                    default:
                        return;
                }
            }
        };
        RelativeRadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RelativeRadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RelativeRadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sort_0:
                        FileMolder.this.changeSort(0);
                        return;
                    case R.id.rb_sort_1:
                        FileMolder.this.changeSort(1);
                        return;
                    case R.id.rb_sort_2:
                        FileMolder.this.changeSort(2);
                        return;
                    case R.id.rb_sort_3:
                        FileMolder.this.changeSort(3);
                        return;
                    case R.id.rb_sort_4:
                        FileMolder.this.changeSort(4);
                        return;
                    case R.id.rb_sort_5:
                        FileMolder.this.changeSort(5);
                        return;
                    case R.id.rb_sort_6:
                        FileMolder.this.changeSort(6);
                        return;
                    case R.id.rb_sort_7:
                        FileMolder.this.changeSort(7);
                        return;
                    default:
                        return;
                }
            }
        };
        if (isSmb) {
            dialog.findViewById(R.id.linear_smb_mode).setVisibility(0);
            RadioGroup rgSmbDisplay = (RadioGroup) dialog.findViewById(R.id.rg_smb_mode);
            rgSmbDisplay.check(AppConstant.sPrefereancesSmbDisplay == 0 ? R.id.rb_smb_mode_user : R.id.rb_smb_mode_address);
            rgSmbDisplay.setOnCheckedChangeListener(checkedChangeListener);
        }
        rgViewPort.setOnCheckedChangeListener(checkedChangeListener);
        rgSort.setOnCheckedChangeListener(onCheckedChangeListener);
        btOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
    }

    private void changeViewport(int viewport) {
        AppConstant.sPrefereancesViewPort = viewport;
        this.mV.layoutFile.removeFirst();
        if (viewport == 0) {
            this.mV.fileAdaperView = this.mV.getGridView(this.mGLContext);
        } else {
            this.mV.fileAdaperView = this.mV.getListView(this.mGLContext);
        }
        this.mFileAdapterView = this.mV.fileAdaperView;
        this.mFileAdapterView.setPageturnEnable(AppConstant.sPrefereancesOperateMode);
        this.mV.layoutFile.addFirst(this.mFileAdapterView);
        resetList();
        this.mFileAdapterView.requestFocus();
    }

    private void changeSort(int sort) {
        AppConstant.sPrefereancesSortWay = sort;
        if (this.mListIndex == 1 && (this.mListType == ListType.SMB_FILE || this.mListType == ListType.NFS_FILE)) {
            Utils.sortFiles(this.mListInfo.getChildren(), AppConstant.sPrefereancesSortWay);
            this.mAdapterFile.notifyDataSetChanged();
            return;
        }
        refreshList();
    }

    private void paste() {
        OnCopyListener listener = new OnCopyListener() {
            public void onEnd(int endType, boolean cut) {
                if (cut) {
                    FileMolder.this.mMoveFiles = new File[0];
                }
                FileMolder.this.refreshList();
            }
        };
        FileCopyTask task = new FileCopyTask(getContext());
        task.setOnCopyListener(listener);
        task.setTask(this.mParent, this.mMoveFiles, this.mDeleteSrc, this.mDeviceIndex == this.mMovedDevice);
        new Thread(task).start();
        goneMenu();
    }

    private void createNewFile() {
        this.mAdapterFile.setMultiChoose(false);
        this.mFileAdapterView.refreash();
        this.mHandler.sendEmptyMessage(262148);
        goneMenu();
    }

    private void enterPoster() {
        switch (this.mListType) {
            case SMB_FILE:
            case NFS_FILE:
            case FILE:
            case SMB_SHARE:
                File file = getFile();
                if (file != null) {
                    this.mHandler.obtainMessage(262169, file).sendToTarget();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void browsingFile(int position) {
        FileIdentifier identifier;
        NfsDevice nfs;
        SambaDevice smbDevice;
        File file;
        switch (this.mListType) {
            case SMB_DEVICE:
                if (this.mListInfo.isSavedSmb(position)) {
                    SambaDevice device = this.mListInfo.getSmbDevice(position);
                    identifier = new FileIdentifier(2, device.getUrl(), device.getIp());
                    identifier.setUser(device.getUser());
                    identifier.setPassword(device.getPassWord());
                    identifier.setExtra(device.getHost());
                    break;
                }
                openSmbDevice(position, false, 1);
                return;
            case NFS_DEVICE:
                nfs = this.mListInfo.getNfs(position);
                identifier = new FileIdentifier(3, nfs.ip, nfs.ip);
                break;
            case SMB_FILE:
                String url;
                String user;
                String password;
                String host;
                String ip;
                if (this.mListIndex != 1) {
                    url = this.mListInfo.getChild(position).getPath();
                    SambaDevice rootDevice = this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition);
                    String mountName = url.substring(this.mAdapterDevice.getItem(this.mDeviceIndex).getPath().length() + 1);
                    String other = "";
                    int s = mountName.indexOf("/");
                    if (s != -1) {
                        other = mountName.substring(s);
                        mountName = mountName.substring(0, s);
                    }
                    int jp = mountName.indexOf(35);
                    if (jp != -1) {
                        mountName = mountName.substring(jp + 1);
                    }
                    String share = ZidooFileUtils.decodeCommand(mountName);
                    url = rootDevice.getUrl() + share + other;
                    SambaDevice sambaDevice = getSavedDevice(rootDevice, share);
                    if (sambaDevice != null) {
                        user = sambaDevice.getUser();
                        password = sambaDevice.getPassWord();
                        host = sambaDevice.getHost();
                        ip = sambaDevice.getIp();
                    } else {
                        user = rootDevice.getUser();
                        password = rootDevice.getPassWord();
                        host = rootDevice.getHost();
                        ip = rootDevice.getIp();
                    }
                    identifier = new FileIdentifier(2, url, ip);
                    identifier.setUser(user);
                    identifier.setPassword(password);
                    identifier.setExtra(host);
                    break;
                }
                File child = this.mListInfo.getChild(position);
                File file2 = new File(this.mParent, child.getName());
                if (file2.exists()) {
                    url = file2.getPath();
                    smbDevice = this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition);
                    url = url.replace(this.mAdapterDevice.getItem(this.mDeviceIndex).getPath() + "/", smbDevice.getUrl());
                    SambaDevice mountDevice = getSavedDevice(smbDevice, file2.getName());
                    if (mountDevice != null) {
                        user = mountDevice.getUser();
                        password = mountDevice.getPassWord();
                        host = mountDevice.getHost();
                        ip = mountDevice.getIp();
                    } else {
                        user = smbDevice.getUser();
                        password = smbDevice.getPassWord();
                        host = smbDevice.getHost();
                        ip = smbDevice.getIp();
                    }
                    identifier = new FileIdentifier(2, url, ip);
                    identifier.setUser(user);
                    identifier.setPassword(password);
                    identifier.setExtra(host);
                    break;
                }
                openSmbShare(position, this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition), false, 2);
                return;
            case NFS_FILE:
                file = getSelectedFile(position);
                nfs = this.mListInfo.getNfs(this.mEnterSmbOrNfsDevicePosition);
                identifier = new FileIdentifier(3, nfs.ip + "/" + Utils.pathToUri(this.mAdapterDevice.getItem(this.mDeviceIndex).getPath(), file.getPath()), nfs.ip);
                break;
            case FILE:
                int type;
                String uuid;
                file = this.mListInfo.getChild(position);
                DeviceInfo device2 = this.mAdapterDevice.getItem(this.mDeviceIndex);
                if (device2.getType() == DeviceType.FLASH) {
                    type = 0;
                    uuid = "Flash";
                } else {
                    type = 1;
                    if (device2.getBlock() == null || device2.getBlock().getUuid() == null) {
                        uuid = device2.getPath() + "/";
                    } else {
                        uuid = device2.getBlock().getUuid();
                    }
                }
                identifier = new FileIdentifier(type, file.getPath().substring(device2.getPath().length()), uuid);
                identifier.setExtra(file.getPath());
                break;
            case SMB_SHARE:
                file = this.mListInfo.getChild(position);
                smbDevice = this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition);
                identifier = new FileIdentifier(2, "smb://" + smbDevice.getHost() + "/" + Utils.pathToUri(this.mAdapterDevice.getItem(this.mDeviceIndex).getPath(), file.getPath()), smbDevice.getIp());
                identifier.setUser(smbDevice.getUser());
                identifier.setPassword(smbDevice.getPassWord());
                identifier.setExtra(smbDevice.getHost());
                break;
            default:
                return;
        }
        if (identifier != null) {
            this.mBrowser.onBrowsing(identifier);
        }
    }

    public void changeSetting(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.swb_hidden:
                AppConstant.sPrefereancesHidden = isChecked;
                this.mFileFilter = this.mFileOperater.buildFileFilter(this.mScreen, AppConstant.sPrefereancesHidden, getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getBoolean(AppConstant.PREFEREANCES_APK_VISIBLE, BoxModelConfig.DEFAULT_APK_VISIBLE_SET));
                refreshList();
                return;
            case R.id.swb_scroll:
                AppConstant.sPrefereancesOperateMode = isChecked;
                this.mFileAdapterView.setPageturnEnable(AppConstant.sPrefereancesOperateMode);
                return;
            case R.id.swb_usb_tips:
                AppConstant.sPrefereancesUsbTips = isChecked;
                return;
            default:
                return;
        }
    }

    private void showCreateDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.defaultDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_create_new, null);
        dialog.setContentView(view);
        final EditText etNew = (EditText) view.findViewById(R.id.et_rename);
        Button btOk = (Button) view.findViewById(R.id.bt_create);
        Button btCancel = (Button) view.findViewById(R.id.bt_create_cancel);
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_create:
                        FileMolder.this.startTask(new CreateTask(FileMolder.this.mHandler, AppConstant.HANDLER_TASK_CREATE, dialog, etNew.getText().toString().trim(), FileMolder.this.mParent, ((RadioButton) view.findViewById(R.id.rb_new_folder)).isChecked()));
                        return;
                    case R.id.bt_create_cancel:
                        dialog.dismiss();
                        return;
                    default:
                        return;
                }
            }
        };
        btOk.setOnClickListener(clickListener);
        btCancel.setOnClickListener(clickListener);
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == 0) {
                    SoundTool.soundKey(keyCode);
                }
                return false;
            }
        });
        dialog.show();
        etNew.requestFocus();
    }

    public void backToPath(PathInfo path, int index) {
        this.mListIndex = index;
        this.mParent = path.getFile();
        switch (path.getType()) {
            case 0:
                refreshList();
                return;
            case 1:
                backSmbDevice();
                return;
            case 2:
                backNfsDevice();
                return;
            case 3:
                switch (this.mListType) {
                    case SMB_FILE:
                        openSmbDevice(this.mEnterSmbOrNfsDevicePosition, false, 4);
                        return;
                    case NFS_FILE:
                        openNfsDevice(this.mEnterSmbOrNfsDevicePosition, true);
                        return;
                    case SMB_SHARE:
                        openDir(2, ((SharePath) path).getDir(), null);
                        return;
                    default:
                        return;
                }
            default:
                return;
        }
    }

    private void setDetails(int code, Object param) {
        if (!(this.mDetailTask == null || !this.mDetailTask.isAlive() || this.mDetailTask.isInterrupted())) {
            this.mDetailTask.cancel();
        }
        this.mDetailTask = new DetailTask(this.mHandler, AppConstant.HANDLER_TASK_DETAIL, (Param) param);
        this.mDetailTask.start();
    }

    private void goneDetail() {
        GlAlphaAnimation alphaAnimation = new GlAlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimatorListener(new AnimatorListener() {
            public void onAnimationStart(GlAnimation animation) {
            }

            public void onAnimationRepeat(GlAnimation animation) {
            }

            public void onAnimationEnd(GlAnimation animation) {
                FileMolder.this.mV.layoutDetails.setVisibility(false);
            }

            public void onAnimationCancel(GlAnimation animation) {
            }
        });
        this.mV.layoutDetails.startAnimation(alphaAnimation);
    }

    private void showDetail() {
        this.mV.layoutDetails.setVisibility(true);
        GlAlphaAnimation alphaAnimation = new GlAlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        this.mV.layoutDetails.startAnimation(alphaAnimation);
    }

    public synchronized boolean removeUsb(ArrayList<ZDevice> unmountDevices) {
        boolean remove;
        remove = false;
        ArrayList<DeviceInfo> temp = new ArrayList(this.mAdapterDevice.getDevices());
        ArrayList<DeviceInfo> newList = new ArrayList();
        for (int i = 0; i < temp.size(); i++) {
            DeviceInfo deviceInfo = (DeviceInfo) temp.get(i);
            boolean find = false;
            Iterator it = unmountDevices.iterator();
            while (it.hasNext()) {
                if (deviceInfo.getPath().equals(((ZDevice) it.next()).getPath())) {
                    remove = true;
                    find = true;
                    if (this.mMovedDevice == i) {
                        this.mMovedDevice = 0;
                        this.mMoveFiles = new File[0];
                        if (ListType.isFile(this.mListType)) {
                            refreashMenu();
                        }
                    }
                    if (i == this.mDeviceIndex) {
                        this.mDeviceIndex = -1;
                        openDevice(0, false);
                        this.mAdapterDevice.notifyDataSetChanged();
                        showDeviceLists();
                    } else if (i < this.mDeviceIndex) {
                        this.mDeviceIndex--;
                    }
                    if (!find) {
                        newList.add(deviceInfo);
                    }
                }
            }
            if (!find) {
                newList.add(deviceInfo);
            }
        }
        if (remove) {
            this.mAdapterDevice.setDeviceInfos(newList);
            it = newList.iterator();
            while (it.hasNext()) {
                ((DeviceInfo) it.next()).destroyDeviceName();
            }
            if (this.mDeviceIndex >= newList.size()) {
                this.mDeviceIndex = newList.size() - 1;
            }
            AppConstant.sUsbIndex = 0;
            AppConstant.sHddIndex = 0;
            this.mAdapterDevice.notifyDataSetChanged();
            if (this.mV.listDevices != null) {
                this.mV.setDeviceUpDownReminds(this.mGLContext, this.mAdapterDevice.getCount(), this.mV.listDevices.getSelectedPosition());
            }
            this.mV.deviceState.refresh(this.mAdapterDevice.getDevices());
            this.mV.deviceState.selectType(this.mAdapterDevice.getItem(this.mDeviceIndex).getType());
            this.mV.deviceState.flickerUsb();
            this.mV.pathView.refreash();
        }
        if (getContext().getSharedPreferences(AppConstant.PREFEREANCES_NAME, 0).getBoolean(AppConstant.PREFEREANCES_USB_TIPS, true) && remove) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    Utils.toast(FileMolder.this.getContext(), (int) R.string.check_usb_remove);
                }
            });
        }
        return remove;
    }

    public synchronized void AddUsb(ArrayList<ZDevice> devices) {
        AddUsb(devices, false);
    }

    private void AddUsb(final ArrayList<ZDevice> devices, final boolean enter) {
        MyLog.d("Add " + devices.size() + " device");
        new Thread(new Runnable() {
            public void run() {
                while (!FileMolder.this.mHasInitData) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                boolean add = false;
                Iterator it = devices.iterator();
                while (it.hasNext()) {
                    ZDevice device = (ZDevice) it.next();
                    boolean exist = false;
                    Iterator it2 = FileMolder.this.mAdapterDevice.getDevices().iterator();
                    while (it2.hasNext()) {
                        DeviceInfo info = (DeviceInfo) it2.next();
                        info.destroyDeviceName();
                        if (info.getType() == device.getType() && info.getPath().equals(device.getPath())) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        add = true;
                        FileMolder.this.mAdapterDevice.add(new DeviceInfo(device));
                    }
                }
                AppConstant.sUsbIndex = 0;
                AppConstant.sHddIndex = 0;
                final boolean added = add;
                FileMolder.this.mGLContext.postRender(new Runnable() {
                    public void run() {
                        FileMolder.this.mV.deviceState.refresh(FileMolder.this.mAdapterDevice.getDevices());
                        FileMolder.this.mV.deviceState.selectType(FileMolder.this.mAdapterDevice.getItem(FileMolder.this.mDeviceIndex).getType());
                        FileMolder.this.mV.deviceState.flickerUsb();
                        if (FileMolder.this.mV.listDevices != null) {
                            FileMolder.this.mV.setDeviceUpDownReminds(FileMolder.this.mGLContext, FileMolder.this.mAdapterDevice.getCount(), FileMolder.this.mV.listDevices.getSelectedPosition());
                        }
                        FileMolder.this.mAdapterDevice.notifyDataSetChanged();
                        if (added && enter) {
                            FileMolder.this.openDevice(FileMolder.this.mAdapterDevice.getCount() - 1, false);
                        }
                    }
                });
                FileMolder.this.mV.deviceState.invalidate();
            }
        }).start();
    }

    private void showLoadDialog() {
        if (this.mLoadDialog == null) {
            this.mLoadDialog = new TaskDialog(getContext());
        }
        this.mLoadDialog.show();
        goneProgress();
    }

    public void loadComplete() {
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SHOW_DIALOG);
        goneLoadDialog();
    }

    private void resultRename(RenameTask.Result result) {
        loadComplete();
        switch (result.getCode()) {
            case AppConstant.RESULT_FAIL /*-15*/:
                toast((int) R.string.error);
                result.getDialog().dismiss();
                return;
            case AppConstant.RESULT_FILE_EXISTS /*-14*/:
                toast((int) R.string.exist_file_reminds);
                return;
            case AppConstant.RESULT_NAME_ILLEGAL /*-13*/:
                toast((int) R.string.name_illegal);
                return;
            case AppConstant.RESULT_NAME_EMPTY /*-12*/:
                toast((int) R.string.input);
                return;
            case 0:
                refreshList();
                result.getDialog().dismiss();
                return;
            default:
                return;
        }
    }

    private void resultCreate(CreateTask.Result result) {
        loadComplete();
        switch (result.getCode()) {
            case AppConstant.RESULT_FAIL /*-15*/:
                toast((int) R.string.error);
                result.getDialog().dismiss();
                return;
            case AppConstant.RESULT_FILE_EXISTS /*-14*/:
                toast((int) R.string.exist_file_reminds);
                return;
            case AppConstant.RESULT_NAME_ILLEGAL /*-13*/:
                toast((int) R.string.name_illegal);
                return;
            case AppConstant.RESULT_NAME_EMPTY /*-12*/:
                toast((int) R.string.input);
                return;
            case 0:
                toast((int) R.string.create_success);
                refreshList();
                int position = Utils.findSelectdPosition(this.mListInfo.getChildren(), result.getFile());
                if (position != -1) {
                    this.mFileAdapterView.setSelection(position);
                }
                result.getDialog().dismiss();
                return;
            default:
                return;
        }
    }

    private void resultQueryFavorite(final Favorite[] favorites) {
        loadComplete();
        this.mGLContext.getGLView().queueEvent(new Runnable() {
            public void run() {
                FileMolder.this.mListInfo.setFavorite(favorites);
                FileMolder.this.mAdapterFile.notifyDataSetChanged();
            }
        });
        refreashMenu();
    }

    private void resultRemoveSmb(int position) {
        loadComplete();
        if (position != -1) {
            SambaDevice device = this.mListInfo.removeSavedSmb(position);
            if (device != null) {
                device.setUser("guest");
                device.setPassWord("");
                this.mListInfo.addSmb(0, device);
                this.mAdapterFile.notifyDataSetChanged();
            }
        }
    }

    private void resultRemoveFavorite(boolean success) {
        loadComplete();
        if (this.mAdapterFile.isEmpty()) {
            refreashMenu();
        }
    }

    private void resultFavorite(FavoriteTask.Result result) {
        loadComplete();
        goneMenu();
        int code = result.getCode();
        if (code == -1) {
            toast((int) R.string.operate_filed);
        } else if (code == -2) {
            toast((int) R.string.exist_favorite);
        } else {
            this.mListInfo.addFavorite(result.getFavorite());
            toast((int) R.string.favor_success);
            updatePageAndDetails(this.mFileAdapterView.getSelectedPosition());
        }
    }

    private void resultRenameFavorite(boolean success) {
        loadComplete();
        this.mAdapterFile.setMultiChoose(false);
        this.mAdapterFile.notifyDataSetChanged();
        if (!success) {
            Utils.toast(this.mGLContext, (int) R.string.operate_filed);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void resultOpenDir(com.zidoo.fileexplorer.task.OpenDirTask.Result r8) {
        /*
        r7 = this;
        r6 = -1;
        r7.loadComplete();
        monitor-enter(r7);
        r3 = r8.getTag();	 Catch:{ all -> 0x004c }
        r4 = r8.getChildren();	 Catch:{ all -> 0x004c }
        if (r4 != 0) goto L_0x0044;
    L_0x000f:
        r4 = r7.mGLContext;	 Catch:{ all -> 0x004c }
        r5 = 2131493069; // 0x7f0c00cd float:1.8609608E38 double:1.0530974997E-314;
        com.zidoo.fileexplorer.tool.Utils.toast(r4, r5);	 Catch:{ all -> 0x004c }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004c }
        r4.<init>();	 Catch:{ all -> 0x004c }
        r5 = "unable open file - ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x004c }
        r5 = r8.getParent();	 Catch:{ all -> 0x004c }
        r4 = r4.append(r5);	 Catch:{ all -> 0x004c }
        r4 = r4.toString();	 Catch:{ all -> 0x004c }
        com.zidoo.fileexplorer.tool.MyLog.w(r4);	 Catch:{ all -> 0x004c }
        r4 = 2;
        if (r3 != r4) goto L_0x0042;
    L_0x0035:
        r4 = r7.mListInfo;	 Catch:{ all -> 0x004c }
        r5 = 0;
        r5 = new java.io.File[r5];	 Catch:{ all -> 0x004c }
        r4.setChildren(r5);	 Catch:{ all -> 0x004c }
        r4 = r7.mAdapterFile;	 Catch:{ all -> 0x004c }
        r4.notifyDataSetChanged();	 Catch:{ all -> 0x004c }
    L_0x0042:
        monitor-exit(r7);	 Catch:{ all -> 0x004c }
    L_0x0043:
        return;
    L_0x0044:
        switch(r3) {
            case 0: goto L_0x004f;
            case 1: goto L_0x005b;
            case 2: goto L_0x008e;
            case 3: goto L_0x00a2;
            default: goto L_0x0047;
        };	 Catch:{ all -> 0x004c }
    L_0x0047:
        r7.refreashMenu();	 Catch:{ all -> 0x004c }
        monitor-exit(r7);	 Catch:{ all -> 0x004c }
        goto L_0x0043;
    L_0x004c:
        r4 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x004c }
        throw r4;
    L_0x004f:
        r4 = r8.getParent();	 Catch:{ all -> 0x004c }
        r5 = r8.getChildren();	 Catch:{ all -> 0x004c }
        r7.enterDir(r4, r5);	 Catch:{ all -> 0x004c }
        goto L_0x0047;
    L_0x005b:
        r4 = r7.mListIndex;	 Catch:{ all -> 0x004c }
        r4 = r4 + -1;
        r7.mListIndex = r4;	 Catch:{ all -> 0x004c }
        r4 = r7.mListInfo;	 Catch:{ all -> 0x004c }
        r5 = r8.getChildren();	 Catch:{ all -> 0x004c }
        r4.setChildren(r5);	 Catch:{ all -> 0x004c }
        r4 = r8.getChildren();	 Catch:{ all -> 0x004c }
        r5 = r7.mParent;	 Catch:{ all -> 0x004c }
        r2 = com.zidoo.fileexplorer.tool.Utils.findSelectdPosition(r4, r5);	 Catch:{ all -> 0x004c }
        r4 = r8.getParent();	 Catch:{ all -> 0x004c }
        r7.mParent = r4;	 Catch:{ all -> 0x004c }
        r4 = r7.mV;	 Catch:{ all -> 0x004c }
        r4 = r4.pathView;	 Catch:{ all -> 0x004c }
        r4.backUp();	 Catch:{ all -> 0x004c }
        r4 = r7.mAdapterFile;	 Catch:{ all -> 0x004c }
        r4.notifyDataSetChanged();	 Catch:{ all -> 0x004c }
        if (r2 == r6) goto L_0x0047;
    L_0x0088:
        r4 = r7.mFileAdapterView;	 Catch:{ all -> 0x004c }
        r4.setSelection(r2);	 Catch:{ all -> 0x004c }
        goto L_0x0047;
    L_0x008e:
        r4 = r7.mListInfo;	 Catch:{ all -> 0x004c }
        r5 = r8.getChildren();	 Catch:{ all -> 0x004c }
        r4.setChildren(r5);	 Catch:{ all -> 0x004c }
        r4 = r7.mGLContext;	 Catch:{ all -> 0x004c }
        r5 = new com.zidoo.fileexplorer.gl.FileMolder$38;	 Catch:{ all -> 0x004c }
        r5.<init>();	 Catch:{ all -> 0x004c }
        r4.postRender(r5);	 Catch:{ all -> 0x004c }
        goto L_0x0047;
    L_0x00a2:
        r4 = r7.mListIndex;	 Catch:{ all -> 0x004c }
        r4 = r4 + -1;
        r7.mListIndex = r4;	 Catch:{ all -> 0x004c }
        r0 = r8.getChildren();	 Catch:{ all -> 0x004c }
        r4 = r7.mListInfo;	 Catch:{ all -> 0x004c }
        r4.setChildren(r0);	 Catch:{ all -> 0x004c }
        r2 = -1;
        r1 = 0;
    L_0x00b3:
        r4 = r0.length;	 Catch:{ all -> 0x004c }
        if (r1 >= r4) goto L_0x00cc;
    L_0x00b6:
        r4 = r0[r1];	 Catch:{ all -> 0x004c }
        r4 = r4.getName();	 Catch:{ all -> 0x004c }
        r5 = r7.mParent;	 Catch:{ all -> 0x004c }
        r5 = r5.getName();	 Catch:{ all -> 0x004c }
        r4 = r4.equals(r5);	 Catch:{ all -> 0x004c }
        if (r4 == 0) goto L_0x00c9;
    L_0x00c8:
        r2 = r1;
    L_0x00c9:
        r1 = r1 + 1;
        goto L_0x00b3;
    L_0x00cc:
        r4 = r8.getExtra();	 Catch:{ all -> 0x004c }
        r7.mParent = r4;	 Catch:{ all -> 0x004c }
        r4 = r7.mV;	 Catch:{ all -> 0x004c }
        r4 = r4.pathView;	 Catch:{ all -> 0x004c }
        r4.backUp();	 Catch:{ all -> 0x004c }
        r4 = r7.mAdapterFile;	 Catch:{ all -> 0x004c }
        r4.notifyDataSetChanged();	 Catch:{ all -> 0x004c }
        if (r2 == r6) goto L_0x0047;
    L_0x00e0:
        r4 = r7.mFileAdapterView;	 Catch:{ all -> 0x004c }
        r4.setSelection(r2);	 Catch:{ all -> 0x004c }
        goto L_0x0047;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zidoo.fileexplorer.gl.FileMolder.resultOpenDir(com.zidoo.fileexplorer.task.OpenDirTask$Result):void");
    }

    private void resultSendShortcut(boolean success) {
        loadComplete();
        goneMenu();
        if (!success) {
            MyLog.e("创建快捷方式失败");
        }
    }

    private void resultIdentifyInitialPath(IdentifyUriTask.Result result) {
        loadComplete();
        if (result.isSuccess()) {
            this.mV.pathView.clear();
            this.mEnterSmbOrNfsDevicePosition = result.getEnterIndex();
            this.mParent = result.getParent();
            this.mListInfo.setChildren(result.getChildren());
            this.mV.pathView.clear();
            Iterator it = result.getPathInfos().iterator();
            while (it.hasNext()) {
                this.mV.pathView.addPath((PathInfo) it.next());
            }
            this.mV.deviceState.selectType(result.getDevice().getType());
            this.mDeviceIndex = result.getDeviceIndex();
            if (this.mV.listDevices != null) {
                this.mV.listDevices.setSelection(this.mDeviceIndex);
            }
            this.mListIndex = result.getListIndex();
            this.mListType = result.getListType();
            resetList();
            refreashMenu();
            return;
        }
        defaultOpen(this.mAdapterDevice.getDevices());
    }

    private void goneLoadDialog() {
        if (this.mLoadDialog != null) {
            this.mLoadDialog.setOnDismissListener(null);
            if (this.mLoadDialog.isShowing()) {
                this.mLoadDialog.dismiss();
            }
        }
    }

    private boolean isLoading() {
        return this.mLoadDialog != null && this.mLoadDialog.isShowing();
    }

    public void netError() {
        this.mHandler.sendEmptyMessage(262165);
    }

    private void showNetErrorDialog() {
        if (this.mV.netErrorDialog == null) {
            final Dialog dialog = new Dialog(this.mGLContext, R.style.defaultDialog);
            dialog.setContentView(R.layout.dialog_net_unconnect);
            OnClickListener clickListener = new OnClickListener() {
                public void onClick(View v) {
                    if (v.getId() == R.id.bt_exit) {
                        dialog.setOnDismissListener(null);
                        Message message = Message.obtain();
                        message.what = 0;
                        FileMolder.this.mGLContext.getGLView().notifyHandleMessage(message);
                    }
                    dialog.dismiss();
                }
            };
            dialog.findViewById(R.id.bt_ok).setOnClickListener(clickListener);
            dialog.findViewById(R.id.bt_exit).setOnClickListener(clickListener);
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (FileMolder.this.mV.layoutMenu != null && FileMolder.this.isMenuShowing()) {
                        FileMolder.this.goneMenu();
                    }
                    if (FileMolder.this.mV.vgScreens != null && FileMolder.this.mV.vgScreens.isShow()) {
                        FileMolder.this.goneScreens();
                    }
                    if (FileMolder.this.mV.layoutDevices != null && !FileMolder.this.mV.layoutDevices.isShow()) {
                        FileMolder.this.showDeviceLists();
                    }
                }
            });
            this.mV.netErrorDialog = dialog;
        }
        if (!this.mV.netErrorDialog.isShowing()) {
            this.mV.netErrorDialog.show();
        }
    }

    private void showRemoteDeviceDisconnectDialog() {
        final Dialog dialog = new Dialog(this.mGLContext, R.style.defaultDialog);
        dialog.setContentView(R.layout.dialog_net_unconnect);
        ((TextView) dialog.findViewById(R.id.tv_msg)).setText(R.string.msg_remote_device_disconnect);
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.bt_exit) {
                    dialog.setOnDismissListener(null);
                    Process.killProcess(Process.myPid());
                }
                dialog.dismiss();
            }
        };
        dialog.findViewById(R.id.bt_ok).setOnClickListener(clickListener);
        dialog.findViewById(R.id.bt_exit).setOnClickListener(clickListener);
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                FileMolder.this.mLoadFailNumber = 0;
                if (FileMolder.this.mV.layoutMenu != null && FileMolder.this.isMenuShowing()) {
                    FileMolder.this.goneMenu();
                }
                if (FileMolder.this.mV.vgScreens != null && FileMolder.this.mV.vgScreens.isShow()) {
                    FileMolder.this.goneScreens();
                }
                if (FileMolder.this.mV.layoutDevices != null && !FileMolder.this.mV.layoutDevices.isShow()) {
                    FileMolder.this.showDeviceLists();
                }
            }
        });
        dialog.show();
    }

    public void unMountUsbOrHdd(DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            String path = deviceInfo.getPath();
            if (BoxModel.sModel == 3 && deviceInfo.getType() != DeviceType.TF) {
                path = path.substring(0, path.lastIndexOf("/"));
            }
            Intent intent;
            if (this.mGLContext.getPackageManager().getLaunchIntentForPackage("com.android.install.apk") != null) {
                intent = new Intent("install_apk_receiver.action");
                intent.putExtra("uninstallusb", path);
                this.mGLContext.sendBroadcast(intent);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("UninstallUsbPath", path);
            bundle.putBoolean("isPrompt", true);
            bundle.putBoolean("isClearmsg", true);
            intent = new Intent();
            intent.setAction("zidoo.busybox.action");
            intent.putExtra("cmd", "UninstallUsb");
            intent.putExtra("parameter", bundle);
            this.mGLContext.sendBroadcast(intent);
        }
    }

    public void setUsbUnMountReminds(int position) {
        boolean z = true;
        DeviceInfo deviceInfo = this.mAdapterDevice.getItem(position);
        if (deviceInfo != null) {
            boolean isUsb;
            if (deviceInfo.getType() == DeviceType.SD || deviceInfo.getType() == DeviceType.HDD || deviceInfo.getType() == DeviceType.TF) {
                isUsb = true;
            } else {
                isUsb = false;
            }
            this.mV.layoutReminds.setReminds(isUsb ? 1 : -1);
            if (this.mAdapterDevice.getCount() > 6) {
                boolean z2;
                StateImageView stateImageView = this.mV.imgDeviceUp;
                if (position != 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                stateImageView.setSelected(z2);
                StateImageView stateImageView2 = this.mV.imgDeviceDown;
                if (position == this.mAdapterDevice.getCount() - 1) {
                    z = false;
                }
                stateImageView2.setSelected(z);
            }
        }
    }

    public void setMenuRemindsImg(int position) {
        boolean z = true;
        if (this.mMenu.getCount() > 6) {
            boolean z2;
            this.mV.imgMenuUp.setSelected(position != 0);
            StateImageView stateImageView = this.mV.imgMenuDown;
            if (position != this.mMenu.getCount() - 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            stateImageView.setSelected(z2);
            stateImageView = this.mV.imgMenuUp;
            if (this.mV.menu.getFirstVisiblePosition() > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            stateImageView.setSelected(z2);
            StateImageView stateImageView2 = this.mV.imgMenuDown;
            if (this.mV.menu.getLastVisiblePosition() >= this.mV.menu.getAdapter().getCount() - 1) {
                z = false;
            }
            stateImageView2.setSelected(z);
        }
    }

    public ArrayList<SambaDevice> queryAndSavedSmbList() {
        ArrayList<SambaDevice> smbs = SmbDatabaseUtils.selectByAll(getContext());
        this.mListInfo.saveSmbList(smbs);
        this.mHasScanedSmb = true;
        return smbs;
    }

    public void saveNfs(String ip) {
        this.mListInfo.addNfs(new NfsDevice(ip));
    }

    public SambaDevice getSavedDevice(SambaDevice root, String name) {
        return this.mListInfo.getSavedDevice(root, name);
    }

    private void resultOpenFile(FileOpenTask.Result result) {
        loadComplete();
        switch (result.getCode()) {
            case 10:
                playBDMV(result.getFile());
                break;
            case 11:
                openBDMV(new OpenWithDialog(getContext(), result.getFiles(), result.getPosition()));
                break;
            case 12:
                if (result.getFile() != null && result.getFiles() != null) {
                    enterDir(result.getFile(), result.getFiles());
                    break;
                } else {
                    Utils.toast(getContext(), (int) R.string.unknow_error);
                    break;
                }
            case 13:
                openCUE(getContext(), result.getFilePath(), result.getAudioPath());
                break;
            case 16:
                if (!canBrowse().booleanValue()) {
                    if (result.getFiles() == null) {
                        if (result.getFilePath() == null) {
                            toast(this.mGLContext.getString(R.string.cannot_select_file));
                            break;
                        } else {
                            openCUE(getContext(), result.getFilePath(), result.getAudioPath());
                            break;
                        }
                    }
                }
                browsingFile(this.mV.fileAdaperView.getSelectedPosition());
                break;
            case 14:
                File[] files = result.getFiles();
                int position = result.getPosition();
                if (!AppConstant.sIsSupportBlurayNavigation || !FileType.isIsoMovie(files[position].getAbsolutePath())) {
                    playFile(result.getFiles()[result.getPosition()]);
                    break;
                }
                OpenWithDialog dialog = new OpenWithDialog(getContext(), files, position);
                dialog.findViewById(R.id.bt_look).setVisibility(8);
                DialogClick click = new DialogClick(dialog, 3);
                dialog.findViewById(R.id.bt_play).setOnClickListener(click);
                ((TextView) dialog.findViewById(R.id.tv_msg)).setText(R.string.video_support_bdmv_navigation);
                View bn = dialog.findViewById(R.id.bt_bluray_navigation);
                bn.setVisibility(0);
                bn.setOnClickListener(click);
                dialog.show();
                break;
                break;
        }
        refreashMenu();
    }

    private void resultLoadNfsDevice(OpenNfsTask.Result result) {
        loadComplete();
        goneProgress();
        if (result.isBack()) {
            this.mListIndex = 1;
            this.mListInfo.setChildren(result.getFiles());
            int position = Utils.findSelectdPosition(result.getFiles(), this.mParent);
            this.mParent = result.getDir();
            this.mV.pathView.backTo(1);
            this.mAdapterFile.notifyDataSetChanged();
            if (position != -1) {
                this.mFileAdapterView.setSelection(position);
                return;
            }
            return;
        }
        this.mV.pathView.addPath(new SharePath(this.mParent, result.getDir(), result.getIp()));
        this.mEnterSmbOrNfsDevicePosition = result.getPosition();
        this.mListType = ListType.NFS_FILE;
        this.mListIndex = 1;
        this.mListInfo.setChildren(result.getFiles());
        resetList();
        refreashMenu();
        showDetail();
    }

    private void resultLoadNfsShare(OpenNfsShareTask.Result result) {
        loadComplete();
        if (result.isCopy()) {
            this.mMoveFiles = new File[]{result.getDir()};
            this.mAdapterFile.setMultiChoose(false);
            this.mFileAdapterView.refreash();
            goneMenu();
            toast(this.mGLContext.getString(R.string.has_copied_files, new Object[]{Integer.valueOf(this.mMoveFiles.length)}));
        } else {
            this.mParent = result.getDir();
            this.mV.pathView.addPath(new FilePath(this.mParent));
            this.mListIndex++;
            this.mListInfo.setChildren(result.getFiles());
            this.mAdapterFile.notifyDataSetChanged();
        }
        refreashMenu();
    }

    public void onCancel(DialogInterface dialog) {
        if (this.mAdapterFile != null) {
            this.mAdapterFile.refreash();
        }
        onLoadFail();
    }

    public boolean onShowBdmvOpenWith(final FastIdentifyTask task) {
        if (BoxModel.sModel == 2) {
            return false;
        }
        this.mHandler.removeMessages(AppConstant.HANDLER_DELAY_SHOW_DIALOG);
        if (this.mLoadDialog.isShowing()) {
            this.mLoadDialog.hide();
        }
        this.mHandler.post(new Runnable() {
            public void run() {
                FileMolder.this.openBDMV(new OpenWithDialog(FileMolder.this.getContext(), task));
            }
        });
        return true;
    }

    private void resultFastIdentify(FastIdentifyTask.Result result) {
        loadComplete();
        if (result.isFail()) {
            if (result.isFavor()) {
                toast(R.string.invalid_favorite);
                return;
            }
            toast(R.string.invalid_shortcut);
            defaultOpen(this.mAdapterDevice.getDevices());
        } else if (!result.isCancelled()) {
            if (result.isFile()) {
                File file = result.getParent();
                if (this.mBrowser != null) {
                    boolean openable = (this.mBrowser.getClickModel() & 4) != 0;
                    if ((this.mBrowser.getClickModel() & 1) != 0) {
                        if (canBrowse().booleanValue()) {
                            browsingFavorite();
                            return;
                        } else if (!openable) {
                            toast(this.mGLContext.getString(R.string.cannot_select_file));
                            return;
                        }
                    } else if (!openable) {
                        return;
                    }
                }
                String cue = FileType.isCUE(file.getPath());
                if (cue != null) {
                    openCUE(getContext(), file.getPath(), cue);
                } else if (AppConstant.sIsSupportBlurayNavigation && FileType.isIsoMovie(file.getAbsolutePath())) {
                    OpenWithDialog dialog = new OpenWithDialog(getContext(), file);
                    dialog.findViewById(R.id.bt_look).setVisibility(8);
                    DialogClick click = new DialogClick(dialog, 3);
                    dialog.findViewById(R.id.bt_play).setOnClickListener(click);
                    ((TextView) dialog.findViewById(R.id.tv_msg)).setText(R.string.video_support_bdmv_navigation);
                    View bn = dialog.findViewById(R.id.bt_bluray_navigation);
                    bn.setVisibility(0);
                    bn.setOnClickListener(click);
                    dialog.show();
                } else {
                    playFile(result.getParent());
                }
            } else if (result.isBdmv()) {
                playBDMV(result.getParent());
                ZidooFileUtils.sendPauseBroadCast(getContext());
            } else if (result.isBDNG()) {
                Intent intent = this.mBoxModel.getBDMVOpenWith(result.getParent());
                if (intent != null && Utils.isAppSystemInstall(getContext(), "com.zidoo.bluraynavigation")) {
                    intent.setComponent(new ComponentName("com.zidoo.bluraynavigation", "com.zidoo.bluraynavigation.HomeActivity"));
                    getContext().startActivity(intent);
                }
            } else {
                this.mV.pathView.clear();
                SambaDevice smb;
                DeviceInfo device;
                String path;
                String temp;
                boolean first;
                boolean stop;
                int s;
                String name;
                int p;
                String ip;
                String share;
                FilePath filePath;
                int i;
                switch (result.getListType()) {
                    case SMB_FILE:
                        smb = result.getSmbDevice();
                        this.mEnterSmbOrNfsDevicePosition = result.getPosition();
                        device = result.getDevice();
                        this.mParent = result.getParent();
                        this.mListInfo.setChildren(result.getChildren());
                        this.mV.pathView.addPath(new ProtocolPath(device, 1));
                        this.mV.pathView.addPath(new SharePath(device, result.getDir(), smb.getName()));
                        if (result.getListIndex() > 0) {
                            path = device.getPath();
                            temp = this.mParent.getPath().substring(path.length() + 1);
                            first = true;
                            stop = false;
                            do {
                                s = temp.indexOf(47);
                                if (s == -1) {
                                    name = temp;
                                    stop = true;
                                } else {
                                    name = temp.substring(0, s);
                                    temp = temp.substring(s + 1);
                                }
                                if (first) {
                                    first = false;
                                    p = name.indexOf(35);
                                    ip = name.substring(0, p);
                                    share = ZidooFileUtils.decodeCommand(name.substring(p + 1));
                                    path = path + "/" + name;
                                    filePath = new FilePath(new MountFile(path, "smb://" + ip + "/" + share, share));
                                } else {
                                    path = path + "/" + name;
                                    filePath = new FilePath(new File(path));
                                }
                                this.mV.pathView.addPath(filePath);
                            } while (!stop);
                            break;
                        }
                        break;
                    case NFS_FILE:
                        this.mListIndex = result.getListIndex();
                        this.mParent = result.getParent();
                        this.mListInfo.setChildren(result.getChildren());
                        this.mEnterSmbOrNfsDevicePosition = result.getPosition();
                        File dir = result.getDir();
                        this.mV.pathView.addPath(new ProtocolPath(result.getDevice(), 2));
                        this.mV.pathView.addPath(new SharePath(result.getDevice(), dir, result.getNfsIp()));
                        if (result.getListIndex() > 0) {
                            path = result.getDevice().getPath();
                            temp = this.mParent.getPath().substring(path.length() + 1);
                            first = true;
                            stop = false;
                            do {
                                s = temp.indexOf(47);
                                if (s == -1) {
                                    name = temp;
                                    stop = true;
                                } else {
                                    name = temp.substring(0, s);
                                    temp = temp.substring(s + 1);
                                }
                                if (first) {
                                    first = false;
                                    p = name.indexOf(35);
                                    ip = name.substring(0, p);
                                    share = ZidooFileUtils.decodeCommand(name.substring(p + 1));
                                    path = path + "/" + name;
                                    filePath = new FilePath(new MountFile(path, ip + "/" + share, share));
                                } else {
                                    path = path + "/" + name;
                                    filePath = new FilePath(new File(path));
                                }
                                this.mV.pathView.addPath(filePath);
                            } while (!stop);
                            break;
                        }
                        break;
                    case FILE:
                        this.mParent = result.getParent();
                        this.mListInfo.setChildren(result.getChildren());
                        this.mV.pathView.addPath(new ProtocolPath(result.getDevice(), 0));
                        int listIndex = result.getListIndex();
                        PathInfo[] pathInfos = new PathInfo[(listIndex + 1)];
                        File parent = result.getParent();
                        for (i = listIndex; i >= 0; i--) {
                            pathInfos[i] = new FilePath(parent);
                            parent = parent.getParentFile();
                        }
                        for (PathInfo addPath : pathInfos) {
                            this.mV.pathView.addPath(addPath);
                        }
                        break;
                    case SMB_SHARE:
                        String sharePath;
                        smb = result.getSmbDevice();
                        this.mEnterSmbOrNfsDevicePosition = result.getPosition();
                        device = result.getDevice();
                        this.mParent = result.getParent();
                        this.mListInfo.setChildren(result.getChildren());
                        this.mV.pathView.addPath(new ProtocolPath(device, 1));
                        String str = smb.getUrl().substring(6);
                        String[] ss = new String[3];
                        int n = 0;
                        int start = 0;
                        for (i = 0; i < str.length(); i++) {
                            if (str.charAt(i) == 47) {
                                int n2 = n + 1;
                                ss[n] = str.substring(start, i);
                                if (n2 == 2) {
                                    ss[n2] = str.substring(i);
                                    n = n2;
                                    sharePath = device.getPath() + "/" + smb.getIp() + "#" + ZidooFileUtils.encodeCommand(ss[1]) + ss[2];
                                    if (!TextUtils.isEmpty(ss[2]) || ss[2].equals("/")) {
                                        this.mV.pathView.addPath(new SharePath(device, new MountFile(sharePath, "smb://" + smb.getIp() + "/" + ss[1], ss[1]), smb.getName()));
                                    } else {
                                        this.mV.pathView.addPath(new SharePath(device, new File(sharePath), smb.getName()));
                                    }
                                    temp = this.mParent.getPath();
                                    if (temp.length() > sharePath.length()) {
                                        temp = temp.substring(sharePath.length());
                                        if (temp.startsWith("/")) {
                                            temp = temp.substring(1, temp.length());
                                        }
                                        if (temp.length() > 0) {
                                            path = this.mParent.getPath();
                                            path = path.substring(0, path.length() - temp.length());
                                            stop = false;
                                            do {
                                                s = temp.indexOf(47);
                                                if (s != -1) {
                                                    name = temp;
                                                    stop = true;
                                                } else {
                                                    name = temp.substring(0, s);
                                                    temp = temp.substring(s + 1);
                                                }
                                                path = path + "/" + name;
                                                this.mV.pathView.addPath(new FilePath(new File(path)));
                                            } while (!stop);
                                            break;
                                        }
                                    }
                                }
                                start = i + 1;
                                n = n2;
                            }
                        }
                        sharePath = device.getPath() + "/" + smb.getIp() + "#" + ZidooFileUtils.encodeCommand(ss[1]) + ss[2];
                        if (TextUtils.isEmpty(ss[2])) {
                            break;
                        }
                        this.mV.pathView.addPath(new SharePath(device, new MountFile(sharePath, "smb://" + smb.getIp() + "/" + ss[1], ss[1]), smb.getName()));
                        temp = this.mParent.getPath();
                        if (temp.length() > sharePath.length()) {
                            temp = temp.substring(sharePath.length());
                            if (temp.startsWith("/")) {
                                temp = temp.substring(1, temp.length());
                            }
                            if (temp.length() > 0) {
                                path = this.mParent.getPath();
                                path = path.substring(0, path.length() - temp.length());
                                stop = false;
                                do {
                                    s = temp.indexOf(47);
                                    if (s != -1) {
                                        name = temp.substring(0, s);
                                        temp = temp.substring(s + 1);
                                    } else {
                                        name = temp;
                                        stop = true;
                                    }
                                    path = path + "/" + name;
                                    this.mV.pathView.addPath(new FilePath(new File(path)));
                                } while (!stop);
                            }
                        }
                        break;
                }
                this.mV.deviceState.selectType(result.getDevice().getType());
                this.mDeviceIndex = result.getDeviceIndex();
                if (this.mV.listDevices != null) {
                    this.mV.listDevices.setSelection(this.mDeviceIndex);
                }
                this.mListIndex = result.getListIndex() + 1;
                this.mListType = result.getListType();
                resetList();
                refreashMenu();
            }
        }
    }

    private void resultAddSmb(AddSmbTask.Result result) {
        loadComplete();
        switch (result.getCode()) {
            case AppConstant.RESULT_EXIST_ERROR /*-11*/:
                toast((int) R.string.exist_smb_reminds);
                return;
            case -4:
                toast((int) R.string.unable_connect);
                return;
            case -3:
                toast((int) R.string.invalid_url);
                return;
            case -2:
                toast((int) R.string.unable_connect);
                return;
            case -1:
                toast((int) R.string.unknow_error);
                return;
            case 0:
                this.mAdapterFile.notifyDataSetChanged();
                this.mFileAdapterView.setSelection(this.mListInfo.smbSize(1) - 1);
                this.mFileAdapterView.requestFocus();
                result.getDialog().dismiss();
                return;
            default:
                return;
        }
    }

    private void resultLoadSmbDevcie(Result result) {
        loadComplete();
        switch (result.getCode()) {
            case -4:
                toast((int) R.string.open_host_error);
                return;
            case -3:
                toast((int) R.string.invalid_url);
                return;
            case -2:
                toast((int) R.string.user_pass_error);
                showLoginSmbDialog(getContext(), result.getDevice(), result.getPosition(), true, result.getFlag());
                return;
            case -1:
                toast((int) R.string.unknow_error);
                return;
            case 0:
                SambaDevice device = result.getDevice();
                if (result.isSaved()) {
                    this.mEnterSmbOrNfsDevicePosition = result.getPosition();
                } else {
                    this.mListInfo.saveSmb(device);
                    this.mEnterSmbOrNfsDevicePosition = this.mListInfo.smbSize(1) - 1;
                }
                int flag = result.getFlag();
                if (flag == 0) {
                    this.mParent = result.getParent();
                    goneProgress();
                    this.mV.pathView.addPath(new SharePath(this.mParent, new VirtualFile(device.getIp(), device.getIp()), device.getHost()));
                    this.mListIndex = 1;
                    File[] children = result.getChildren();
                    this.mListInfo.setChildren(children);
                    Utils.sortFiles(children, AppConstant.sPrefereancesSortWay);
                    this.mListType = ListType.SMB_FILE;
                    resetList();
                    refreashMenu();
                    return;
                } else if (flag == 1) {
                    FileIdentifier identifier = new FileIdentifier(2, device.getUrl(), device.getIp());
                    identifier.setUser(device.getUser());
                    identifier.setPassword(device.getPassWord());
                    identifier.setExtra(device.getHost());
                    this.mBrowser.onBrowsing(identifier);
                    return;
                } else if (flag == 4) {
                    this.mListIndex = 1;
                    int position = Utils.findSelectdPosition(result.getChildren(), this.mParent);
                    this.mParent = result.getParent();
                    this.mListInfo.setChildren(result.getChildren());
                    this.mV.pathView.backTo(1);
                    this.mAdapterFile.notifyDataSetChanged();
                    if (position != -1) {
                        this.mFileAdapterView.setSelection(position);
                        return;
                    }
                    return;
                } else {
                    this.mAdapterFile.notifyDataSetChanged();
                    this.mFileAdapterView.setSelection(this.mEnterSmbOrNfsDevicePosition);
                    favor(this.mEnterSmbOrNfsDevicePosition);
                    return;
                }
            default:
                return;
        }
    }

    private void resultLoadSmbShareDevice(OpenSmbShareDeviceTask.Result result) {
        loadComplete();
        switch (result.getCode()) {
            case -4:
                toast((int) R.string.open_host_error);
                return;
            case -3:
                toast((int) R.string.invalid_url);
                return;
            case -2:
                toast((int) R.string.user_pass_error);
                showLoginSmbDialog(getContext(), result.getDevice(), result.getPosition(), true, result.getFlag());
                return;
            case -1:
                toast((int) R.string.unknow_error);
                return;
            case 0:
                this.mListType = ListType.SMB_SHARE;
                SambaDevice device = result.getDevice();
                this.mEnterSmbOrNfsDevicePosition = result.getPosition();
                if (result.getFlag() == 0) {
                    goneProgress();
                    File share = result.getShare();
                    this.mParent = share;
                    this.mV.pathView.addPath(new FilePath(share));
                    this.mListIndex = 1;
                    this.mListInfo.setChildren(result.getChildren());
                    resetList();
                    refreashMenu();
                    return;
                }
                FileIdentifier identifier = new FileIdentifier(2, device.getUrl(), device.getIp());
                identifier.setUser(device.getUser());
                identifier.setPassword(device.getPassWord());
                identifier.setExtra(device.getHost());
                this.mBrowser.onBrowsing(identifier);
                return;
            default:
                return;
        }
    }

    private void resultLoadSmbShare(OpenSmbShareTask.Result result) {
        SambaDevice device;
        loadComplete();
        switch (result.getCode()) {
            case -4:
                toast((int) R.string.open_host_error);
                return;
            case -3:
                toast((int) R.string.invalid_url);
                return;
            case -2:
                toast((int) R.string.user_pass_error);
                showLoginSmbDialog(getContext(), result.getDevice(), result.getPosition(), false, result.getFlag());
                return;
            case -1:
                toast((int) R.string.unknow_error);
                return;
            case 0:
                device = result.getDevice();
                int operate = result.getOperate();
                if (operate != 1) {
                    if (operate == 2) {
                        this.mListInfo.saveSmb(device);
                        break;
                    }
                }
                this.mListInfo.getSavedSmbDevice(device.getUrl());
                break;
                break;
            case 1:
                break;
            default:
                return;
        }
        this.mListType = ListType.SMB_FILE;
        int flag = result.getFlag();
        if (flag == 0) {
            this.mParent = result.getParent();
            this.mV.pathView.addPath(new FilePath(this.mParent));
            this.mListIndex++;
            this.mListInfo.setChildren(result.getChildren());
            resetList();
            refreashMenu();
        } else if (flag == 1) {
            this.mDeleteSrc = false;
            this.mMoveFiles = new File[1];
            this.mMoveFiles[0] = result.getParent();
            this.mAdapterFile.setMultiChoose(false);
            this.mFileAdapterView.refreash();
            goneMenu();
            toast(this.mGLContext.getString(R.string.has_copied_files, new Object[]{Integer.valueOf(this.mMoveFiles.length)}));
        } else if (flag == 2) {
            device = result.getDevice();
            File share = result.getParent();
            FileIdentifier identifier = new FileIdentifier(2, "smb://" + this.mListInfo.getSmbDevice(this.mEnterSmbOrNfsDevicePosition).getHost() + "/" + share.getName() + "/", device.getIp());
            identifier.setUser(device.getUser());
            identifier.setPassword(device.getPassWord());
            identifier.setExtra(device.getHost());
            this.mBrowser.onBrowsing(identifier);
        } else if (flag == 3) {
            favor(this.mFileAdapterView.getSelectedPosition());
        } else {
            this.mHandler.sendEmptyMessage(262166);
        }
    }

    private void playFile(File file) {
        if (BoxModel.sModel == 5 || BoxModel.sModel == 8) {
            Utils.startActivityForRTD(getContext(), this.mBoxModel.getOpenWith(file));
        } else {
            this.mBoxModel.openFile(file);
        }
    }

    private void playBDMV(File file) {
        if (BoxModel.sModel == 5) {
            Utils.startActivityForRTD(getContext(), this.mBoxModel.getBDMVOpenWith(file));
        } else {
            this.mBoxModel.openBDMV(file);
        }
    }

    private void refreashMenu() {
        this.mMenuManager.refresh(this);
    }

    private void toast(int resid) {
        this.mHandler.obtainMessage(262161, resid, 0).sendToTarget();
    }

    private void toast(String msg) {
        this.mHandler.obtainMessage(262145, msg).sendToTarget();
    }
}
