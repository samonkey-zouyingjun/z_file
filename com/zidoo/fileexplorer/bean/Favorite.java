package com.zidoo.fileexplorer.bean;

public class Favorite extends FastIdentifier {
    private static final long serialVersionUID = 1;
    int fileType;
    int id;
    long length;
    String name;

    public Favorite(String abstractPath, int tag, int listIndex, String name, int ftype, long length) {
        super(abstractPath, tag, listIndex);
        this.name = name;
        this.fileType = ftype;
        this.length = length;
    }

    public Favorite(String name, String uri, int tag, int index, String ip, String user, String pwd) {
        super(uri, tag, index);
        this.name = name;
        this.fileType = -1;
        setUuid(ip);
        setUser(user);
        setPassword(pwd);
    }

    public Favorite(String abstractPath, int tag, int listIndex, String name, String ip, String user, String pwd, int ftype, long length) {
        super(abstractPath, tag, listIndex);
        this.name = name;
        this.fileType = ftype;
        this.length = length;
        setUuid(ip);
        setUser(user);
        setPassword(pwd);
    }

    public Favorite(String ip, int tag) {
        super(ip, tag, 0);
        this.fileType = -1;
        this.name = ip;
    }

    public Favorite(String name, String url, int tag, int listIndex, int ftype, long length) {
        super(url, tag, listIndex);
        this.name = name;
        this.fileType = ftype;
        this.length = length;
    }

    public Favorite(int id, String name, String uri, int tag, int index, String uuid, String user, String pwd, int ftype, long length) {
        super(uri, tag, index);
        this.id = id;
        this.name = name;
        this.fileType = ftype;
        this.length = length;
        setUuid(uuid);
        setUser(user);
        setPassword(pwd);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileType() {
        return this.fileType;
    }

    public long getFileLength() {
        return this.length;
    }

    public String toString() {
        return "Favorite [id=" + this.id + ", name=" + this.name + ", fileType=" + this.fileType + ", length=" + this.length + " " + super.toString() + "]";
    }
}
