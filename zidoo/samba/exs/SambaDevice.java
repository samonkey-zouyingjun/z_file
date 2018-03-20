package zidoo.samba.exs;

public class SambaDevice {
    private String host;
    private String ip;
    private String name;
    private String passWord;
    private int type;
    private String url;
    private String user;

    public SambaDevice(String ip, String hostName) {
        this.url = "";
        this.host = "";
        this.ip = "";
        this.user = "guest";
        this.passWord = "";
        this.name = null;
        this.type = 4;
        this.ip = ip;
        this.host = hostName;
    }

    public SambaDevice(String ip, String username, String password) {
        this.url = "";
        this.host = "";
        this.ip = "";
        this.user = "guest";
        this.passWord = "";
        this.name = null;
        this.type = 4;
        this.ip = ip;
        this.user = username;
        this.passWord = password;
    }

    public SambaDevice(String ip, String username, String password, String hostname) {
        this(ip, username, password);
        this.host = hostname;
    }

    public SambaDevice(String url, String host, String ip, String user, String passWord, int type) {
        this.url = "";
        this.host = "";
        this.ip = "";
        this.user = "guest";
        this.passWord = "";
        this.name = null;
        this.type = 4;
        this.url = url;
        this.host = host;
        this.ip = ip;
        this.user = user;
        this.passWord = passWord;
        this.type = type;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setHost(String hostName) {
        this.url.replace(this.host, hostName);
        this.host = hostName;
        this.name = null;
    }

    public String getHost() {
        return this.host;
    }

    public String getName() {
        if (this.name == null) {
            if (this.type == 4) {
                this.name = this.host;
            } else if (this.url.isEmpty()) {
                this.url = "smb://" + this.host + "/";
                this.name = this.host;
            } else {
                String temp = this.url.endsWith("/") ? this.url.substring(0, this.url.length() - 1) : this.url;
                int e = temp.lastIndexOf("/");
                if (e != -1) {
                    temp = temp.substring(e + 1);
                }
                this.name = temp;
            }
        }
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return "SambaDevice [  url=" + this.url + ", host=" + this.host + ", ip=" + this.ip + ", user=" + this.user + ", passWord=" + this.passWord + ", type=" + this.type + "]";
    }
}
