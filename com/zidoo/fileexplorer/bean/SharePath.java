package com.zidoo.fileexplorer.bean;

import java.io.File;

public class SharePath extends PathInfo {
    File dir;
    String name;
    File parent;
    boolean smb;

    public SharePath(File parent, File dir, String name) {
        this.parent = parent;
        this.dir = dir;
        this.name = name;
        this.type = 3;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.parent;
    }

    public File getDir() {
        return this.dir;
    }
}
