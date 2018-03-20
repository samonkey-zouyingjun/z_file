package com.zidoo.fileexplorer.bean;

import java.io.File;

public class DevicePath extends PathInfo {
    File file;
    String name;

    public DevicePath(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }
}
