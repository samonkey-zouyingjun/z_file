package com.zidoo.fileexplorer.bean;

import java.io.File;

public class FilePath extends PathInfo {
    File file;

    public FilePath(File file) {
        this.file = file;
    }

    public String getName() {
        return this.file.getName();
    }

    public File getFile() {
        return this.file;
    }
}
