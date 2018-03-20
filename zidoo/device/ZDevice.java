package zidoo.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.File;

public class ZDevice extends File implements Parcelable {
    public static final Creator<ZDevice> CREATOR = new Creator<ZDevice>() {
        public ZDevice[] newArray(int size) {
            return new ZDevice[size];
        }

        public ZDevice createFromParcel(Parcel source) {
            return new ZDevice(source);
        }
    };
    public static final long serialVersionUID = 1;
    BlockInfo block;
    String format;
    DeviceType type;

    public ZDevice(String path) {
        super(path);
    }

    public ZDevice(Parcel src) {
        super(src.readString());
        this.block = (BlockInfo) src.readParcelable(BlockInfo.class.getClassLoader());
        this.format = src.readString();
        this.type = DeviceType.valueOf(src.readString());
    }

    public ZDevice(String path, DeviceType type) {
        super(path);
        this.type = type;
    }

    public BlockInfo getBlock() {
        return this.block;
    }

    public void setBlock(BlockInfo block) {
        this.block = block;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public DeviceType getType() {
        return this.type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String toString() {
        return "ZDevice [path=" + getPath() + ", block=" + this.block + ",  format=" + this.format + ", type=" + this.type + "]";
    }

    public int describeContents() {
        return 4;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getPath());
        dest.writeParcelable(this.block, 0);
        dest.writeString(this.format);
        dest.writeString(this.type.name());
    }
}
