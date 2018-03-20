package com.zidoo.fileexplorer.bean;

public class MountFile extends StableFile {
    private static final long serialVersionUID = 1;
    String share;
    String url;

    public MountFile(String path, String url, String share) {
        super(path);
        this.url = url;
        this.share = share;
    }

    public MountFile(String dir, String name, String url, String share) {
        super(dir, name);
        this.url = url;
        this.share = share;
    }

    public void stable() {
        this.size = 0;
        this.date = 0;
        this.isDirectory = true;
    }

    public String getUrl() {
        return this.url;
    }

    public String getName() {
        return this.share;
    }

    public String getShareName() {
        return this.share;
    }

    public String getFileName() {
        return super.getName();
    }

    public boolean canRead() {
        return true;
    }

    public String toString() {
        return "MountFile [path=" + getPath() + " url=" + this.url + ", share=" + this.share + "]";
    }
}
