package com.zidoo.fileexplorer.bean;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class StableFile extends File {
    private static final long serialVersionUID = 1;
    protected long date = 0;
    protected boolean isDirectory = false;
    protected long size = 0;

    public StableFile(String path) {
        super(path);
        stable();
    }

    public StableFile(File dir, String name) {
        super(dir, name);
        stable();
    }

    public StableFile(String dir, String name) {
        super(dir, name);
        stable();
    }

    public void stable() {
        this.size = super.length();
        this.date = super.lastModified();
        this.isDirectory = super.isDirectory();
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public boolean isFile() {
        return !this.isDirectory;
    }

    public StableFile getParentFile() {
        String tempParent = getParent();
        if (tempParent == null) {
            return null;
        }
        return new StableFile(tempParent);
    }

    public StableFile[] listFiles() {
        String[] filenames = list();
        if (filenames == null) {
            return null;
        }
        StableFile[] result = new StableFile[filenames.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new StableFile((File) this, filenames[i]);
        }
        return result;
    }

    public StableFile[] listFiles(FileFilter filter) {
        String[] filenames = list();
        if (filenames == null || filter == null) {
            return null;
        }
        List<StableFile> result = new ArrayList(filenames.length);
        for (String filename : filenames) {
            StableFile file = new StableFile((File) this, filename);
            if (filter.accept(file)) {
                result.add(file);
            }
        }
        return (StableFile[]) result.toArray(new StableFile[result.size()]);
    }

    public long length() {
        return this.size;
    }

    public long lastModified() {
        return this.date;
    }
}
