package zidoo.nfs;

public class NfsFolder {
    private String folderPath = "";
    public String ip = "";
    private boolean isMounted = false;
    private String mountedPoint = "";

    public NfsFolder(String ip, String path) {
        this.ip = ip;
        this.folderPath = path;
    }

    public String getPath() {
        return this.folderPath;
    }

    public void setFolderPath(String folderPath) {
        if (folderPath != null) {
            this.folderPath = folderPath;
        }
    }

    public String getMountedPoint() {
        return this.mountedPoint;
    }

    public void setMountedPoint(String mountedPoint) {
        this.mountedPoint = mountedPoint;
    }

    @Deprecated
    public boolean isMounted() {
        return this.isMounted;
    }

    @Deprecated
    public void setMounted(boolean isMounted) {
        this.isMounted = isMounted;
    }
}
