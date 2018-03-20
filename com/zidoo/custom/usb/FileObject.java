package com.zidoo.custom.usb;

import java.util.ArrayList;

public class FileObject {
    private int currentIndex = 0;
    private ArrayList<FileInfo> fileInfo_List = new ArrayList();
    private String rootPath = "";

    public FileObject(int currentIndex, String rootPath, ArrayList<FileInfo> fileInfo_List) {
        this.currentIndex = currentIndex;
        this.rootPath = rootPath;
        this.fileInfo_List = fileInfo_List;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public ArrayList<FileInfo> getFileInfo_List() {
        return this.fileInfo_List;
    }

    public void setFileInfo_List(ArrayList<FileInfo> fileInfo_List) {
        this.fileInfo_List = fileInfo_List;
    }

    public String toString() {
        return "FileListObject [currentIndex=" + this.currentIndex + ", rootPath=" + this.rootPath + ", fileInfo_List=" + this.fileInfo_List + "]";
    }
}
