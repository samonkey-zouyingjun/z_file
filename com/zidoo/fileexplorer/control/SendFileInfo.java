package com.zidoo.fileexplorer.control;

public class SendFileInfo {
    public boolean isBDMV = false;
    public boolean isBluray = false;
    public boolean isFileList = false;
    public long mModifyDate;
    public long mlength;
    public String path = "";
    public String title = "";
    public int type;

    public SendFileInfo(String title, String path, int type) {
        this.title = title;
        this.path = path;
        this.type = type;
    }

    public SendFileInfo(String title, String path, int type, boolean isBDMV) {
        this.title = title;
        this.path = path;
        this.type = type;
        this.isBDMV = isBDMV;
    }
}
