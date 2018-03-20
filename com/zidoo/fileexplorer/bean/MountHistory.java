package com.zidoo.fileexplorer.bean;

public class MountHistory {
    private int id;
    private long mountTime;
    private String password;
    private String sharePath;
    private String url;
    private String user;

    public MountHistory(int id, String url, String sharePath, String user, String password, long mountTime) {
        this.id = id;
        this.url = url;
        this.sharePath = sharePath;
        this.user = user;
        this.password = password;
        this.mountTime = mountTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSharePath() {
        return this.sharePath;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getMountTime() {
        return this.mountTime;
    }

    public void setMountTime(long mountTime) {
        this.mountTime = mountTime;
    }
}
