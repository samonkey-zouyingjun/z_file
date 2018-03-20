package zidoo.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Serializable;

public class BlockInfo implements Serializable, Parcelable {
    public static final Creator<BlockInfo> CREATOR = new Creator<BlockInfo>() {
        public BlockInfo[] newArray(int size) {
            return new BlockInfo[size];
        }

        public BlockInfo createFromParcel(Parcel source) {
            return new BlockInfo(source);
        }
    };
    private static final long serialVersionUID = 1;
    String label;
    String path;
    String type;
    String uuid;

    public BlockInfo(String path, String label, String uuid, String type) {
        this.path = path;
        this.label = label;
        this.uuid = uuid;
        this.type = type;
    }

    public BlockInfo(Parcel src) {
        this.path = src.readString();
        this.label = src.readString();
        this.uuid = src.readString();
        this.type = src.readString();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "BlockInfo [path=" + this.path + ", label=" + this.label + ", uuid=" + this.uuid + ", type=" + this.type + "]";
    }

    public int describeContents() {
        return 4;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.label);
        dest.writeString(this.uuid);
        dest.writeString(this.type);
    }
}
