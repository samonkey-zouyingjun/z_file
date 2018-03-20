package com.zidoo.fileexplorer.menu;

import android.content.Context;
import android.net.Uri;

public class MenuManager {
    private final boolean mIsSupportShortcut;
    private PermissionThread mPermissionThread = null;

    private class PermissionThread extends Thread {
        private MenuPermission permission;
        private boolean stop;

        private PermissionThread(MenuPermission permission) {
            this.stop = false;
            this.permission = permission;
        }

        public void run() {
            int i = 0;
            int menuType = 0;
            FileMenu[] menus = null;
            switch (this.permission.getListType()) {
                case FILE:
                case SMB_FILE:
                case NFS_FILE:
                case SMB_SHARE:
                    menuType = 0;
                    if (!this.permission.isBrowseModel()) {
                        if (!MenuManager.this.mIsSupportShortcut) {
                            menus = new FileMenu[]{FileMenu.POSTER, FileMenu.CHOOSE, FileMenu.COPY, FileMenu.PASTE, FileMenu.DELETE, FileMenu.CUT, FileMenu.FAVOR, FileMenu.REFREASH, FileMenu.RENAME, FileMenu.CREATE, FileMenu.VIEWPORT, FileMenu.SETTING};
                            break;
                        } else {
                            menus = new FileMenu[]{FileMenu.POSTER, FileMenu.CHOOSE, FileMenu.COPY, FileMenu.PASTE, FileMenu.DELETE, FileMenu.CUT, FileMenu.FAVOR, FileMenu.REFREASH, FileMenu.RENAME, FileMenu.CREATE, FileMenu.SHORTCUT, FileMenu.VIEWPORT, FileMenu.SETTING};
                            break;
                        }
                    }
                    menus = new FileMenu[]{FileMenu.SELECT, FileMenu.VIEWPORT, FileMenu.SETTING};
                    break;
                case SMB_DEVICE:
                    menuType = 1;
                    if (!this.permission.isBrowseModel()) {
                        if (!MenuManager.this.mIsSupportShortcut) {
                            menus = new FileMenu[]{FileMenu.SEARCH, FileMenu.CREATE, FileMenu.DELETE, FileMenu.FAVOR, FileMenu.VIEWPORT, FileMenu.SETTING};
                            break;
                        } else {
                            menus = new FileMenu[]{FileMenu.SEARCH, FileMenu.CREATE, FileMenu.DELETE, FileMenu.FAVOR, FileMenu.SHORTCUT, FileMenu.VIEWPORT, FileMenu.SETTING};
                            break;
                        }
                    }
                    menus = new FileMenu[]{FileMenu.SELECT, FileMenu.SEARCH, FileMenu.CREATE, FileMenu.DELETE, FileMenu.VIEWPORT, FileMenu.SETTING};
                    break;
                case NFS_DEVICE:
                    menuType = 2;
                    if (!this.permission.isBrowseModel()) {
                        if (!MenuManager.this.mIsSupportShortcut) {
                            menus = new FileMenu[]{FileMenu.SEARCH, FileMenu.FAVOR, FileMenu.VIEWPORT, FileMenu.SETTING};
                            break;
                        } else {
                            menus = new FileMenu[]{FileMenu.SEARCH, FileMenu.FAVOR, FileMenu.VIEWPORT, FileMenu.SHORTCUT, FileMenu.SETTING};
                            break;
                        }
                    }
                    menus = new FileMenu[]{FileMenu.SELECT, FileMenu.SEARCH, FileMenu.VIEWPORT, FileMenu.SETTING};
                    break;
                case FAVORITE:
                    menuType = 3;
                    if (!this.permission.isBrowseModel()) {
                        menus = new FileMenu[]{FileMenu.CHOOSE, FileMenu.REMOVE, FileMenu.RENAME, FileMenu.VIEWPORT, FileMenu.SETTING};
                        break;
                    } else {
                        menus = new FileMenu[]{FileMenu.SELECT, FileMenu.VIEWPORT, FileMenu.SETTING};
                        break;
                    }
            }
            MenuJudger judger = new MenuJudger(this.permission);
            int length = menus.length;
            while (i < length) {
                menus[i].judge(judger);
                i++;
            }
            if (!this.stop) {
                this.permission.show(menuType, menus);
            }
        }

        public void stopRun() {
            this.stop = true;
            interrupt();
        }
    }

    public MenuManager(Context context) {
        this.mIsSupportShortcut = isVefRun(context);
    }

    private boolean isVefRun(Context context) {
        try {
            String AUTHORITY = "com.android.launcher2.settings";
            if (context.getContentResolver().query(Uri.parse("content://com.android.launcher2.settings/favorites?notify=true"), null, null, null, null) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void refresh(MenuPermission permission) {
        if (this.mPermissionThread != null) {
            this.mPermissionThread.stopRun();
        }
        this.mPermissionThread = new PermissionThread(permission);
        this.mPermissionThread.start();
    }
}
