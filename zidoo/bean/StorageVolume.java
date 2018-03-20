package zidoo.bean;

import java.io.File;
import java.lang.reflect.Field;

public class StorageVolume {
    private boolean allowMassStorage;
    private int descriptionId;
    private boolean emulated;
    private String label = null;
    private long maxFileSize;
    private int mtpReserveSpace;
    private File path;
    private boolean primary;
    private boolean removable;
    private String state;
    private int storageId;
    private String uuid = null;

    public StorageVolume(Object src) {
        try {
            for (Field field : src.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    String name = field.getName();
                    if (name.equals("mAllowMassStorage")) {
                        this.allowMassStorage = field.getBoolean(src);
                    } else if (name.equals("mDescriptionId")) {
                        this.descriptionId = field.getInt(src);
                    } else if (name.equals("mEmulated")) {
                        this.emulated = field.getBoolean(src);
                    } else if (name.equals("mMaxFileSize")) {
                        this.maxFileSize = field.getLong(src);
                    } else if (name.equals("mMtpReserveSpace")) {
                        this.mtpReserveSpace = field.getInt(src);
                    } else if (name.equals("mPath")) {
                        this.path = (File) field.get(src);
                    } else if (name.equals("mPrimary")) {
                        this.primary = field.getBoolean(src);
                    } else if (name.equals("mRemovable")) {
                        this.removable = field.getBoolean(src);
                    } else if (name.equals("mState")) {
                        this.state = (String) field.get(src);
                    } else if (name.equals("mStorageId")) {
                        this.storageId = field.getInt(src);
                    } else if (name.equals("mUserLabel")) {
                        this.label = (String) field.get(src);
                    } else if (name.equals("mUuid")) {
                        this.uuid = (String) field.get(src);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public boolean isAllowMassStorage() {
        return this.allowMassStorage;
    }

    public int getDescriptionId() {
        return this.descriptionId;
    }

    public boolean isEmulated() {
        return this.emulated;
    }

    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public int getMtpReserveSpace() {
        return this.mtpReserveSpace;
    }

    public File getPath() {
        return this.path;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public boolean isRemovable() {
        return this.removable;
    }

    public String getState() {
        return this.state;
    }

    public int getStorageId() {
        return this.storageId;
    }

    public String getLabel() {
        return this.label;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String toString() {
        return "StorageVolume [allowMassStorage=" + this.allowMassStorage + ", descriptionId=" + this.descriptionId + ", emulated=" + this.emulated + ", maxFileSize=" + this.maxFileSize + ", mtpReserveSpace=" + this.mtpReserveSpace + ", path=" + this.path + ", primary=" + this.primary + ", removable=" + this.removable + ", state=" + this.state + ", storageId=" + this.storageId + ", label=" + this.label + ", uuid=" + this.uuid + "]";
    }
}
