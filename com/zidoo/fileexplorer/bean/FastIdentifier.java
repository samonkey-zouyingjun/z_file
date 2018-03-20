package com.zidoo.fileexplorer.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.zidoo.fileexplorer.db.FavoriteDatabase;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class FastIdentifier implements Serializable, Parcelable {
    public static final Creator<FastIdentifier> CREATOR = new Creator<FastIdentifier>() {
        public FastIdentifier createFromParcel(Parcel source) {
            return new FastIdentifier(source);
        }

        public FastIdentifier[] newArray(int size) {
            return new FastIdentifier[size];
        }
    };
    private static final long serialVersionUID = 1;
    int listIndex;
    String password = "";
    int tag;
    String uri;
    String user = "";
    String uuid = "";

    public FastIdentifier(String uri, int tag, int listIndex) {
        this.uri = uri;
        this.tag = tag;
        this.listIndex = listIndex;
    }

    public FastIdentifier(Parcel source) {
        this.uri = source.readString();
        this.tag = source.readInt();
        this.listIndex = source.readInt();
        this.uuid = source.readString();
        this.user = source.readString();
        this.password = source.readString();
    }

    public FastIdentifier(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.uri = object.getString(FavoriteDatabase.URI);
            this.tag = object.getInt(FavoriteDatabase.TAG);
            this.listIndex = object.getInt("listIndex");
            this.uuid = object.getString(FavoriteDatabase.UUID);
            this.user = object.getString("user");
            this.password = object.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getListIndex() {
        return this.listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String toString() {
        return "FastIdentifier [uri=" + this.uri + ", tag=" + this.tag + ", listIndex=" + this.listIndex + ", uuid=" + this.uuid + ", user=" + this.user + ", password=" + this.password + "]";
    }

    public String toJson() {
        try {
            JSONObject object = new JSONObject();
            object.put(FavoriteDatabase.URI, this.uri);
            object.put(FavoriteDatabase.TAG, this.tag);
            object.put("listIndex", this.listIndex);
            object.put(FavoriteDatabase.UUID, this.uuid);
            object.put("user", this.user);
            object.put("password", this.password);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int describeContents() {
        return 6;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
        dest.writeInt(this.tag);
        dest.writeInt(this.listIndex);
        dest.writeString(this.uuid);
        dest.writeString(this.user);
        dest.writeString(this.password);
    }
}
