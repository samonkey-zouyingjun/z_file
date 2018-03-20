package com.zidoo.fileexplorer.config;

import zidoo.tarot.kernel.Texture;

public class AppConstant {
    public static final String ACTION_INNER_USB_BROADCAST = "Inner Usb Broadcast";
    public static final String DB_SMB_CREATE = "create table if not exists smbuser( [id] integer PRIMARY KEY AUTOINCREMENT ,[url] nvarchar(256),[host] nvarchar(64),[ip] nvarchar(20),[user] nvarchar(40) ,[password] nvarchar(16),[type] integer(4) )";
    public static final String DB_SMB_HOSTNAME = "hostname";
    public static final String DB_SMB_ID = "id";
    public static final String DB_SMB_IP = "ip";
    public static final String DB_SMB_PASSWORD = "password";
    public static final String DB_SMB_TABLE_NAME = "smbuser";
    public static final int DB_SMB_TABLE_VERSION = 3;
    public static final String DB_SMB_USER_NAME = "name";
    public static final String EXTRA_ENTRY_MODE = "entry mode";
    public static final String EXTRA_FAST_IDENTIFIER = "fast identifier";
    public static final String EXTRA_FILE_IDENTIFY = "file identify";
    public static final String EXTRA_IS_REMOVE_OR_ADD_USB = "remove or add";
    public static final String EXTRA_OPEN_WITH = "open with";
    public static final String EXTRA_SCREEN = "screen";
    public static final String EXTRA_USB_DEVICE = "devices";
    public static final int HANDLER_DELAY_GONE_DIALOG_FOR_SCAN_SMB = 12305;
    public static final int HANDLER_DELAY_GONE_TOUCH = 12296;
    public static final int HANDLER_DELAY_REFRESH_MENU = 12306;
    public static final int HANDLER_DELAY_SET_DETAILS = 12290;
    public static final int HANDLER_DELAY_SHOW_DIALOG = 12291;
    public static final int HANDLER_DELAY_SHOW_DIALOG_FOR_SCAN_SMB = 12297;
    public static final int HANDLER_DELAY_SHOW_OPERATER_DIALOG = 12289;
    public static final int HANDLER_DELAY_SHOW_PROGRESS_FOR_SCAN_SMB = 12304;
    public static final int HANDLER_OPEN_NFS_ERROR = 8199;
    public static final int HANDLER_OPEN_NFS_NET_ERROR = 8198;
    public static final int HANDLER_OPEN_NFS_SUCCESS = 8197;
    public static final int HANDLER_OPEN_SMB_ERROR = 8194;
    public static final int HANDLER_OPEN_SMB_PASSWORD_ERROR = 8193;
    public static final int HANDLER_OPEN_SMB_SUCCESS = 8192;
    public static final int HANDLER_OPERATE_COPY_ERROR = 4104;
    public static final int HANDLER_OPERATE_COPY_INFO = 4105;
    public static final int HANDLER_OPERATE_COPY_INIT = 4103;
    public static final int HANDLER_OPERATE_COPY_NEW_FILE = 4101;
    public static final int HANDLER_OPERATE_COPY_SUCCESS = 4102;
    public static final int HANDLER_OPERATE_DELETE_ERROR = 4114;
    public static final int HANDLER_OPERATE_DELETE_INFO = 4115;
    public static final int HANDLER_OPERATE_DELETE_INIT = 4113;
    public static final int HANDLER_OPERATE_DELETE_SUCCESS = 4112;
    public static final int HANDLER_SET_MENU = 4097;
    public static final int HANDLER_SHOW_FILE_BROWSER = 4096;
    public static final int HANDLER_TASK_ADD_SMB = 20486;
    public static final int HANDLER_TASK_CREATE = 20487;
    public static final int HANDLER_TASK_DETAIL = 20489;
    public static final int HANDLER_TASK_FAST_IDENTIFY = 20501;
    public static final int HANDLER_TASK_FAVORITE = 20503;
    public static final int HANDLER_TASK_IDENTIFY_INITIAL_PATH = 20505;
    public static final int HANDLER_TASK_OPEN_DIR = 20497;
    public static final int HANDLER_TASK_OPEN_FILE = 20496;
    public static final int HANDLER_TASK_OPEN_NFS = 20484;
    public static final int HANDLER_TASK_OPEN_NFS_SHARE = 20485;
    public static final int HANDLER_TASK_OPEN_SMBDEVICE = 20481;
    public static final int HANDLER_TASK_OPEN_SMB_SHARE = 20482;
    public static final int HANDLER_TASK_OPEN_SMB_SHARE_DEVICE = 20483;
    public static final int HANDLER_TASK_QUERY_FAVORITE = 20498;
    public static final int HANDLER_TASK_REMOVE_FAVORITE = 20499;
    public static final int HANDLER_TASK_REMOVE_SMB = 20502;
    public static final int HANDLER_TASK_RENAME = 20488;
    public static final int HANDLER_TASK_RENAME_FAVORITE = 20500;
    public static final int HANDLER_TASK_SEND_SHORTCUT = 20504;
    public static final String PREFEREANCES_APK_VISIBLE = "apk_visible";
    public static final String PREFEREANCES_AUTO_SCAN_SMB = "auto_scan";
    public static final String PREFEREANCES_DEFAULT_SMB_SCAN_MODEL = "scan_model";
    public static final String PREFEREANCES_HIDDEN = "hidden";
    public static final String PREFEREANCES_MOUNT = "mount";
    public static final String PREFEREANCES_NAME = "config";
    public static final String PREFEREANCES_NFS_SCAN_IP = "nfs scan ip";
    public static final String PREFEREANCES_NFS_SCAN_MODE = "nfs scan mode";
    public static final String PREFEREANCES_NFS_SCAN_PORT = "nfs scan port";
    public static final String PREFEREANCES_OPERATE_MODE = "operate_mode";
    public static final String PREFEREANCES_SMB_DISPLAY = "smb_display";
    public static final String PREFEREANCES_SORT = "sort";
    public static final String PREFEREANCES_USB_TIPS = "usb_tips";
    public static final String PREFEREANCES_VIEW_PORT = "viewport";
    public static final int RESULT_BDMV = 10;
    public static final int RESULT_BDMV_OPEN_WITH = 11;
    public static final int RESULT_EXIST_ERROR = -11;
    public static final int RESULT_FAIL = -15;
    public static final int RESULT_FILE_EXISTS = -14;
    public static final int RESULT_MALFORMED_URL_ERROR = -3;
    public static final int RESULT_NAME_EMPTY = -12;
    public static final int RESULT_NAME_ILLEGAL = -13;
    public static final int RESULT_OPEN_CUE = 13;
    public static final int RESULT_OPEN_DIR = 12;
    public static final int RESULT_OPEN_FILE = 14;
    public static final int RESULT_SELECT = 16;
    public static final int RESULT_SKIP = 15;
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_SUCCESS_SHARE = 1;
    public static final int RESULT_UNABLE_CONNECT_HOST_ERROR = -4;
    public static final int RESULT_UNKNOWN_ERROR = -1;
    public static final int RESULT_UNKNOWN_USER_OR_PASSWORD_ERROR = -2;
    public static int sAppRunState = 0;
    public static String sFlash;
    public static String[] sHdd;
    public static int sHddIndex = 0;
    public static Texture[] sIconHintTextures;
    public static boolean sIsSound = true;
    public static boolean sIsSupportBlurayNavigation = false;
    public static boolean sIsZH = false;
    public static String sNfs;
    public static boolean sPrefereancesHidden = false;
    public static boolean sPrefereancesOperateMode = true;
    public static int sPrefereancesSmbDisplay = 0;
    public static int sPrefereancesSortWay = 0;
    public static boolean sPrefereancesUsbTips = true;
    public static int sPrefereancesViewPort = 0;
    public static String sSdcard;
    public static boolean sShowLogo = false;
    public static String sSmb;
    public static long sSystemBootTime = -1;
    public static String[] sUsb;
    public static int sUsbIndex = 0;
}
