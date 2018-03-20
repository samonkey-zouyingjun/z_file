package zidoo.samba.player.util;

import java.io.Serializable;

public class FileItem implements Serializable {
    private boolean isFile = false;
    private String name = "";
    private String path = "/";

    public FileItem(String name, String path, boolean isFile) {
        this.name = name;
        this.path = path;
        this.isFile = isFile;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFile() {
        return this.isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String toString() {
        return this.name;
    }
}
