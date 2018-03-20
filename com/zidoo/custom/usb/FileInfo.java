package com.zidoo.custom.usb;

import java.io.File;

public class FileInfo {
    private File file = null;
    private String fileName = "";
    private String fileType = FileTypeManager.open_type_back;
    private int flashType = -1;
    private String path = "";
    private int typeCount = -1;

    public FileInfo(File file, String path, String fileType, String fileName) {
        this.file = file;
        this.path = path;
        this.fileType = fileType;
        this.fileName = fileName;
    }

    public FileInfo(File file, String path, String fileType, String fileName, int flashType, int typeCount) {
        this.file = file;
        this.path = path;
        this.fileType = fileType;
        this.fileName = fileName;
        this.flashType = flashType;
        this.typeCount = typeCount;
    }

    public int getTypeCount() {
        return this.typeCount;
    }

    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }

    public int getFlashType() {
        return this.flashType;
    }

    public void setFlashType(int flashType) {
        this.flashType = flashType;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "FileInfo [file=" + this.file + ", path=" + this.path + ", fileType=" + this.fileType + ", fileName=" + this.fileName + "]";
    }
}
