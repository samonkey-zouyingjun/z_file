package com.zidoo.fileexplorer.bean;

import java.io.File;
import java.util.Date;

public class VirtualFile extends File {
    private static final long serialVersionUID = 1;
    String name;
    long size = 0;

    public VirtualFile(String path) {
        super(path);
    }

    public VirtualFile(String path, String name) {
        super(path);
        this.name = name;
    }

    public VirtualFile(String path, String name, long size) {
        super(path);
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public long length() {
        return this.size;
    }

    public long lastModified() {
        return new Date().getTime();
    }
}
