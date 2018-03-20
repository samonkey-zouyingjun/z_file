package zidoo.browse;

public class IdentifyResult {
    final String device;
    final String path;
    final int result;

    public IdentifyResult(int result, String path, String device) {
        this.result = result;
        this.path = path;
        this.device = device;
    }

    public int getResult() {
        return this.result;
    }

    public String getPath() {
        return this.path;
    }

    public String getDevice() {
        return this.device;
    }
}
