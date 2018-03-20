package com.zidoo.control.center.tool;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ZidooControlInfo implements Parcelable {
    public static final Creator<ZidooControlInfo> CREATOR = new Creator<ZidooControlInfo>() {
        public ZidooControlInfo createFromParcel(Parcel in) {
            return new ZidooControlInfo(in);
        }

        public ZidooControlInfo[] newArray(int size) {
            return new ZidooControlInfo[size];
        }
    };
    public static final int FILE = 2;
    public static final int IMAGE = 1;
    public static final int JSON = 0;
    public static final int UPLOAD = 3;
    public String mFilePath = null;
    public int mHigth = -1;
    public String mJson = null;
    public int mType = 0;
    public int mWith = -1;

    public ZidooControlInfo(int mType) {
        this.mType = mType;
    }

    public ZidooControlInfo(Parcel in) {
        readFromParcel(in);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeString(this.mJson);
        dest.writeString(this.mFilePath);
        dest.writeInt(this.mWith);
        dest.writeInt(this.mHigth);
    }

    public void readFromParcel(Parcel source) {
        this.mType = source.readInt();
        this.mJson = source.readString();
        this.mFilePath = source.readString();
        this.mWith = source.readInt();
        this.mHigth = source.readInt();
    }

    public String toString() {
        return "ZidooControlInfo [mType=" + this.mType + ", mJson=" + this.mJson + ", mFilePath=" + this.mFilePath + ", mWith=" + this.mWith + ", mHigth=" + this.mHigth + "]";
    }
}
