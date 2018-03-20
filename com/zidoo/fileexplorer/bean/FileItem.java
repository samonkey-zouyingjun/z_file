package com.zidoo.fileexplorer.bean;

public class FileItem {
    String date;
    int type;
    String typeOrSize;

    public FileItem(int type, String typeOrSize, String date) {
        this.type = type;
        this.typeOrSize = typeOrSize;
        this.date = date;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeOrSize() {
        return this.typeOrSize;
    }

    public void setTypeOrSize(String typeOrSize) {
        this.typeOrSize = typeOrSize;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return "BrowseItem [type=" + this.type + ", typeOrSize=" + this.typeOrSize + ", date=" + this.date + "]";
    }
}
