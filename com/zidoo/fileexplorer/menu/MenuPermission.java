package com.zidoo.fileexplorer.menu;

import com.zidoo.fileexplorer.bean.ListType;
import java.io.File;

public interface MenuPermission {
    Boolean canBrowse();

    int getCheckedNumber();

    int getContentCount();

    File getFile();

    ListType getListType();

    int getMoveFilesCount();

    File getParent();

    boolean isBrowseModel();

    Boolean isChildFile();

    boolean isMulti();

    boolean isSavedSmb();

    Boolean isVirtual();

    void show(int i, FileMenu[] fileMenuArr);
}
