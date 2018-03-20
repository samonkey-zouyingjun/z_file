package com.zidoo.fileexplorer.menu;

import com.zidoo.fileexplorer.R;
import com.zidoo.fileexplorer.bean.ListType;
import java.io.File;
import zidoo.file.FileType;

public abstract class FileMenu {
    public static final FileMenu CHOOSE = new FileMenu(MenuType.CHOOSE) {
        public void judge(MenuJudger judger) {
            if (judger.isMulti()) {
                this.able = true;
                if (judger.getCheckedNumber() < judger.getContentCount()) {
                    this.name = R.string.mutil_all;
                } else {
                    this.name = R.string.mutil_cancel;
                }
                this.icon = R.drawable.icon_operate_multichoos;
            } else if (judger.isVirtual()) {
                this.able = false;
                this.icon = R.drawable.icon_operate_multichoos_enable;
                this.name = R.string.menu_choices;
            } else {
                if (judger.getContentCount() > 0) {
                    this.able = true;
                    this.icon = R.drawable.icon_operate_multichoos;
                } else {
                    this.able = false;
                    this.icon = R.drawable.icon_operate_multichoos_enable;
                }
                this.name = R.string.menu_choices;
            }
        }
    };
    public static final FileMenu COPY = new BipolarityMenu(MenuType.COPY, R.string.menu_copy, R.drawable.icon_operate_copy, R.drawable.icon_operate_copy_enable) {
        boolean isAble(MenuJudger judger) {
            return judger.getCheckedNumber() > 0;
        }
    };
    public static final FileMenu CREATE = new BipolarityMenu(MenuType.CREATE, R.string.menu_new, R.drawable.icon_operate_create, R.drawable.icon_operate_create_enable) {
        boolean isAble(MenuJudger judger) {
            if (judger.getListType() == ListType.SMB_DEVICE) {
                this.name = R.string.menu_smb_new;
                return true;
            }
            this.name = R.string.menu_file_new;
            if (!judger.isCanWrite() || judger.isVirtual()) {
                return false;
            }
            return true;
        }
    };
    public static final FileMenu CUT = new BipolarityMenu(MenuType.CUT, R.string.menu_cut, R.drawable.icon_operate_cut, R.drawable.icon_operate_cut_enable) {
        boolean isAble(MenuJudger judger) {
            return judger.getCheckedNumber() > 0 && !judger.isVirtual() && judger.isCanWrite();
        }
    };
    public static final FileMenu DELETE = new FileMenu(MenuType.DELETE) {
        public void judge(MenuJudger judger) {
            boolean z = true;
            if (judger.getListType() == ListType.SMB_DEVICE) {
                if (judger.getCheckedNumber() != 1) {
                    z = false;
                }
                setAble(z);
                this.name = judger.isSavedSmb() ? R.string.logoff : R.string.delete;
                return;
            }
            this.name = R.string.menu_delete;
            if (judger.getCheckedNumber() <= 0 || !judger.isCanWrite() || judger.isVirtual()) {
                z = false;
            }
            setAble(z);
        }

        private void setAble(boolean able) {
            this.able = able;
            this.icon = able ? R.drawable.icon_operate_delete : R.drawable.icon_operate_delete_enable;
        }
    };
    public static final FileMenu FAVOR = new BipolarityMenu(MenuType.FAVOR, R.string.menu_favor, R.drawable.ic_favorite, R.drawable.ic_favorite_enable) {
        boolean isAble(MenuJudger judger) {
            return judger.getCheckedNumber() == 1;
        }
    };
    public static final FileMenu PASTE = new BipolarityMenu(MenuType.PASTE, R.string.menu_paste, R.drawable.icon_operate_paste, R.drawable.icon_operate_paste_enable) {
        boolean isAble(MenuJudger judger) {
            return !judger.isVirtual() && judger.moveFilesCount() > 0 && judger.isCanWrite() && !judger.isChildFile();
        }
    };
    public static final FileMenu POSTER = new BipolarityMenu(MenuType.POSTER, R.string.menu_poster, R.drawable.ic_poster_f, R.drawable.ic_poster) {
        boolean isAble(MenuJudger judger) {
            switch (judger.getListType()) {
                case FILE:
                case SMB_FILE:
                case NFS_FILE:
                case SMB_SHARE:
                    File file = judger.getFile();
                    if (file != null) {
                        if (file.isDirectory()) {
                            return FileType.isBDMV(file.getPath());
                        }
                        return FileType.isMovieFile(file);
                    }
                    break;
            }
            return false;
        }
    };
    public static final FileMenu REFREASH = new PerpetualMenu(MenuType.REFREASH, R.string.menu_refreash, R.drawable.icon_operate_refreash);
    public static final FileMenu REMOVE = new BipolarityMenu(MenuType.REMOVE, R.string.menu_remove, R.drawable.icon_operate_remove_favorite, R.drawable.icon_operate_remove_favorite_enable) {
        boolean isAble(MenuJudger judger) {
            return judger.getCheckedNumber() > 0;
        }
    };
    public static final FileMenu RENAME = new BipolarityMenu(MenuType.RENAME, R.string.menu_rename, R.drawable.icon_operate_rename, R.drawable.icon_operate_rename_enable) {
        boolean isAble(MenuJudger judger) {
            if (judger.getCheckedNumber() == 1) {
                if (judger.getListType() == ListType.FAVORITE) {
                    return true;
                }
                if (!judger.isVirtual() && judger.isCanWrite()) {
                    return true;
                }
            }
            return false;
        }
    };
    public static final FileMenu SEARCH = new PerpetualMenu(MenuType.SEARCH, R.string.menu_scan, R.drawable.icon_operate_search);
    public static final FileMenu SELECT = new BipolarityMenu(MenuType.SELECT, R.string.menu_select, R.drawable.icon_operate_select, R.drawable.icon_operate_select_enable) {
        boolean isAble(MenuJudger judger) {
            return judger.canBrowse();
        }
    };
    public static final FileMenu SETTING = new PerpetualMenu(MenuType.SETTING, R.string.menu_setting, R.drawable.icon_operate_setting);
    public static final FileMenu SHORTCUT = new BipolarityMenu(MenuType.SHORTCUT, R.string.menu_shortcut, R.drawable.ic_shortcut_f, R.drawable.ic_shortcut) {
        boolean isAble(MenuJudger judger) {
            return judger.getCheckedNumber() == 1;
        }
    };
    public static final FileMenu VIEWPORT = new PerpetualMenu(MenuType.VIEWPORT, R.string.menu_viewport, R.drawable.icon_operate_viewport);
    protected boolean able;
    protected int icon;
    protected int name;
    private final MenuType type;

    private static abstract class BipolarityMenu extends FileMenu {
        private int ableIcon;
        private int disableIcon;

        abstract boolean isAble(MenuJudger menuJudger);

        public BipolarityMenu(MenuType type, int name, int ableIcon, int disableIcon) {
            super(type);
            this.name = name;
            this.ableIcon = ableIcon;
            this.disableIcon = disableIcon;
        }

        public void judge(MenuJudger judger) {
            if (isAble(judger)) {
                this.able = true;
                this.icon = this.ableIcon;
                return;
            }
            this.able = false;
            this.icon = this.disableIcon;
        }
    }

    private static class PerpetualMenu extends FileMenu {
        public PerpetualMenu(MenuType type, int name, int icon) {
            super(type);
            this.name = name;
            this.icon = icon;
            this.able = true;
        }

        public void judge(MenuJudger judger) {
        }
    }

    public abstract void judge(MenuJudger menuJudger);

    public FileMenu(MenuType type) {
        this.type = type;
    }

    public final MenuType getType() {
        return this.type;
    }

    public final int getName() {
        return this.name;
    }

    public final int getIcon() {
        return this.icon;
    }

    public final boolean isAble() {
        return this.able;
    }

    public String toString() {
        return "FileMenu [type=" + this.type + ", name=" + this.name + ", icon=" + this.icon + ", able=" + this.able + "]";
    }
}
