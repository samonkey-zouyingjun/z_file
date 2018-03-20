package com.zidoo.custom.file;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class ZApkInfo {
    public String mAppName = "";
    public int mCode = 0;
    public Drawable mIcon = null;
    public PackageInfo mInfo = null;
    public long mLength = 0;
    public String mPackageName = "";
    public String mPath = "";
    public String mVersion = "";

    public String toString() {
        return "ZApkInfo [mInfo=" + this.mInfo + ", mAppName=" + this.mAppName + ", mIcon=" + this.mIcon + ", mLength=" + this.mLength + ", mPath=" + this.mPath + ", mPackageName=" + this.mPackageName + ", mVersion=" + this.mVersion + ", mCode=" + this.mCode + "]";
    }
}
