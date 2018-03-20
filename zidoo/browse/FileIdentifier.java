package zidoo.browse;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.umeng.common.a;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class FileIdentifier implements Serializable, Parcelable {
    public static final Creator<FileIdentifier> CREATOR = new Creator<FileIdentifier>() {
        public FileIdentifier[] newArray(int size) {
            return new FileIdentifier[size];
        }

        public FileIdentifier createFromParcel(Parcel source) {
            return new FileIdentifier(source);
        }
    };
    public static final int TYPE_FLASH = 0;
    public static final int TYPE_NFS = 3;
    public static final int TYPE_SMB = 2;
    public static final int TYPE_USB = 1;
    private static final long serialVersionUID = 1;
    String extra;
    String password;
    int type;
    String uri;
    String user;
    String uuid;

    public FileIdentifier(int type, String uri, String uuid) {
        this.type = type;
        this.uri = uri;
        this.uuid = uuid;
    }

    public FileIdentifier(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.type = object.getInt(a.b);
            this.uri = object.getString(FavoriteDatabase.URI);
            this.uuid = object.getString(FavoriteDatabase.UUID);
            if (object.has("user")) {
                this.user = object.getString("user");
            }
            if (object.has("password")) {
                this.password = object.getString("password");
            }
            if (object.has("extra")) {
                this.extra = object.getString("extra");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private FileIdentifier(Parcel source) {
        this.type = source.readInt();
        this.uri = source.readString();
        this.uuid = source.readString();
        this.user = source.readString();
        this.password = source.readString();
        this.extra = source.readString();
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int describeContents() {
        return 6;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.uri);
        dest.writeString(this.uuid);
        dest.writeString(this.user);
        dest.writeString(this.password);
        dest.writeString(this.extra);
    }

    public String toJson() {
        try {
            JSONObject object = new JSONObject();
            object.put(a.b, this.type);
            object.put(FavoriteDatabase.URI, this.uri);
            object.put(FavoriteDatabase.UUID, this.uuid);
            object.put("user", this.user);
            object.put("password", this.password);
            object.put("extra", this.extra);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String toString() {
        return "FileIdentifier [type=" + this.type + ", uri=" + this.uri + ", uuid=" + this.uuid + ", user=" + this.user + ", password=" + this.password + ", extra=" + this.extra + "]";
    }
}
