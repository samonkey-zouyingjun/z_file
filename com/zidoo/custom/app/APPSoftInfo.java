package com.zidoo.custom.app;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import java.io.Serializable;

public class APPSoftInfo implements Serializable {
    private Bitmap appIconBitmap = null;
    private Drawable appIconDrawable = null;
    private long clickCount = 0;
    private long firstInstallTime = 0;
    private boolean isBoot = false;
    private boolean isTop = false;
    private boolean isWebData = false;
    private String labelName = "";
    private Object[] object = null;
    private String packageName = "";
    private Bitmap reAppIconBitmap = null;
    private Object tag = null;
    private String type = "";
    private int versionCode = 0;
    private String versionName = "";

    public boolean isTop() {
        return this.isTop;
    }

    public void setTop(boolean isTop) {
        this.isTop = isTop;
    }

    public boolean isBoot() {
        return this.isBoot;
    }

    public void setBoot(boolean isBoot) {
        this.isBoot = isBoot;
    }

    public long getClickCount() {
        return this.clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public long getFirstInstallTime() {
        return this.firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public Bitmap getReAppIconBitmap() {
        return this.reAppIconBitmap;
    }

    public void setReAppIconBitmap(Bitmap reAppIconBitmap) {
        this.reAppIconBitmap = reAppIconBitmap;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabelName() {
        return this.labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getAppIconDrawable() {
        return this.appIconDrawable;
    }

    public void setAppIconDrawable(Drawable appIconDrawable) {
        this.appIconDrawable = appIconDrawable;
    }

    public Bitmap getAppIconBitmap() {
        return this.appIconBitmap;
    }

    public void setAppIconBitmap(Bitmap appIconBitmap) {
        this.appIconBitmap = appIconBitmap;
    }

    public Object[] getObject() {
        return this.object;
    }

    public void setObject(Object[] object) {
        this.object = object;
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean isWebData() {
        return this.isWebData;
    }

    public void setWebData(boolean isWebData) {
        this.isWebData = isWebData;
    }
}
