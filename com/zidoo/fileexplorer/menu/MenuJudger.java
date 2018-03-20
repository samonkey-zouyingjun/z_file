package com.zidoo.fileexplorer.menu;

import com.zidoo.fileexplorer.bean.ListType;
import java.io.File;

public class MenuJudger {
    private Boolean canBrowse = null;
    private Boolean canRead = null;
    private Boolean canWrite = null;
    private int checkedNum = -1;
    private Boolean isChildFile = null;
    private Boolean isVirtual = null;
    private MenuPermission permission;

    public MenuJudger(MenuPermission permission) {
        this.permission = permission;
    }

    public boolean isMulti() {
        return this.permission.isMulti();
    }

    public int getCheckedNumber() {
        if (this.checkedNum == -1) {
            this.checkedNum = this.permission.getCheckedNumber();
        }
        return this.checkedNum;
    }

    public int getContentCount() {
        return this.permission.getContentCount();
    }

    public boolean isVirtual() {
        if (this.isVirtual == null) {
            this.isVirtual = this.permission.isVirtual();
        }
        return this.isVirtual.booleanValue();
    }

    public boolean isCanRead() {
        checkFilePermission();
        return this.canRead.booleanValue();
    }

    public boolean isCanWrite() {
        checkFilePermission();
        return this.canWrite.booleanValue();
    }

    private void checkFilePermission() {
        if (this.canRead == null || this.canWrite == null) {
            File file = this.permission.getParent();
            this.canRead = Boolean.valueOf(file.canRead());
            this.canWrite = Boolean.valueOf(file.canWrite());
        }
    }

    public int moveFilesCount() {
        return this.permission.getMoveFilesCount();
    }

    public boolean isChildFile() {
        if (this.isChildFile == null) {
            this.isChildFile = this.permission.isChildFile();
        }
        return this.isChildFile.booleanValue();
    }

    public boolean canBrowse() {
        if (this.canBrowse == null) {
            this.canBrowse = this.permission.canBrowse();
        }
        return this.canBrowse.booleanValue();
    }

    public ListType getListType() {
        return this.permission.getListType();
    }

    public boolean isSavedSmb() {
        return this.permission.isSavedSmb();
    }

    public File getFile() {
        return this.permission.getFile();
    }
}
