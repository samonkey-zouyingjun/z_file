package com.zidoo.custom.down;

import java.io.Serializable;

public class ZidooDownApkInfo implements Serializable {
    private Object[] ObjectTag = null;
    private String apkpath = null;
    private String apkurl = null;
    private String code = "-1";
    private int downLength = 0;
    private int downNum = 0;
    private int downStatu = 0;
    private String iconUrl = "";
    private String id = "";
    private String kbs = "0kb/s";
    private String name = "";
    private String pName = "";
    private String size = "";
    private Object tag = null;
    private int totalLength = 0;
    private String version = "";

    public ZidooDownApkInfo(String id, String name, String iconUrl, String version, String code, String pName, String size, String apkurl, String apkpath) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.version = version;
        this.code = code;
        this.pName = pName;
        this.size = size;
        this.apkurl = apkurl;
        this.apkpath = apkpath;
    }

    public ZidooDownApkInfo(String id, String name, String iconUrl, String version, String code, String pName, String size, String apkurl, String apkpath, int downNum, int downLength, int downStatu, String kbs) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.version = version;
        this.code = code;
        this.pName = pName;
        this.size = size;
        this.apkurl = apkurl;
        this.apkpath = apkpath;
        this.downNum = downNum;
        this.downLength = downLength;
        this.downStatu = downStatu;
        this.kbs = kbs;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getpName() {
        return this.pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getApkurl() {
        return this.apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getApkpath() {
        return this.apkpath;
    }

    public void setApkpath(String apkpath) {
        this.apkpath = apkpath;
    }

    public int getDownNum() {
        return this.downNum;
    }

    public void setDownNum(int downNum) {
        this.downNum = downNum;
    }

    public int getDownLength() {
        return this.downLength;
    }

    public void setDownLength(int downLength) {
        this.downLength = downLength;
    }

    public int getDownStatu() {
        return this.downStatu;
    }

    public void setDownStatu(int downStatu) {
        this.downStatu = downStatu;
    }

    public String getKbs() {
        return this.kbs;
    }

    public void setKbs(String kbs) {
        this.kbs = kbs;
    }

    public String toString() {
        return "ZidooDownApkInfo [id=" + this.id + ", name=" + this.name + ", iconUrl=" + this.iconUrl + ", version=" + this.version + ", code=" + this.code + ", pName=" + this.pName + ", size=" + this.size + ", apkurl=" + this.apkurl + ", apkpath=" + this.apkpath + ", downNum=" + this.downNum + ", downLength=" + this.downLength + ", downStatu=" + this.downStatu + ", kbs=" + this.kbs + "]";
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object[] getObjectTag() {
        return this.ObjectTag;
    }

    public void setObjectTag(Object[] objectTag) {
        this.ObjectTag = objectTag;
    }

    public int getTotalLength() {
        return this.totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }
}
