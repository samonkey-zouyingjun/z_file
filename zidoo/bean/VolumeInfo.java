package zidoo.bean;

import com.umeng.common.a;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import zidoo.browse.BrowseConstant;

public class VolumeInfo {
    private static Field FSLABEL;
    private static Field FSTYPE;
    private static Field FSUUID;
    private static Field ID;
    private static Method ISMOUNTEDREADABLE;
    private static Field PATH;
    private static Field TYPE;
    String fsLabel;
    String fsType;
    String fsUuid;
    String id;
    boolean mountedReadable;
    String path;
    int type;

    public VolumeInfo(Object src) {
        try {
            this.id = (String) ID.get(src);
            this.fsLabel = (String) FSLABEL.get(src);
            this.fsType = (String) FSTYPE.get(src);
            this.fsUuid = (String) FSUUID.get(src);
            this.path = (String) PATH.get(src);
            this.type = TYPE.getInt(src);
            this.mountedReadable = ((Boolean) ISMOUNTEDREADABLE.invoke(src, new Object[0])).booleanValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.fsLabel;
    }

    public String getType() {
        return this.fsType;
    }

    public String getUuid() {
        return this.fsUuid;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isPublic() {
        return this.type == 0;
    }

    public boolean isMountedReadable() {
        return this.mountedReadable;
    }

    static {
        try {
            Class<?> c = Class.forName("android.os.storage.VolumeInfo");
            ID = c.getField("id");
            FSLABEL = c.getField("fsLabel");
            FSTYPE = c.getField("fsType");
            FSUUID = c.getField("fsUuid");
            PATH = c.getField(BrowseConstant.EXTRA_PATH);
            TYPE = c.getField(a.b);
            ISMOUNTEDREADABLE = c.getDeclaredMethod("isMountedReadable", new Class[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
