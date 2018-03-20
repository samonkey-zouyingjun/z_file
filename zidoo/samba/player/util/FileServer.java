package zidoo.samba.player.util;

import com.umeng.common.util.e;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import zidoo.http.HTTPRequest;
import zidoo.http.HTTPRequestListener;
import zidoo.http.HTTPResponse;
import zidoo.http.HTTPServerList;

public class FileServer extends Thread implements HTTPRequestListener {
    public static final String CONTENT_EXPORT_URI = "/smb";
    public static final String tag = "dlna.certus.iptv.sharefile.server.ShareFileManager";
    private int HTTPPort = 2222;
    private String bindIP = null;
    private HTTPServerList httpServerList = new HTTPServerList();

    public String getBindIP() {
        return this.bindIP;
    }

    public void setBindIP(String bindIP) {
        this.bindIP = bindIP;
    }

    public HTTPServerList getHttpServerList() {
        return this.httpServerList;
    }

    public void setHttpServerList(HTTPServerList httpServerList) {
        this.httpServerList = httpServerList;
    }

    public int getHTTPPort() {
        return this.HTTPPort;
    }

    public void setHTTPPort(int hTTPPort) {
        this.HTTPPort = hTTPPort;
    }

    public void run() {
        super.run();
        int retryCnt = 0;
        int bindPort = getHTTPPort();
        HTTPServerList hsl = getHttpServerList();
        while (!hsl.open(bindPort)) {
            retryCnt++;
            if (100 >= retryCnt) {
                setHTTPPort(bindPort + 1);
                bindPort = getHTTPPort();
            } else {
                return;
            }
        }
        hsl.addRequestListener(this);
        hsl.start();
        FileUtil.ip = hsl.getHTTPServer(0).getBindAddress();
        FileUtil.port = hsl.getHTTPServer(0).getBindPort();
    }

    public void httpRequestRecieved(HTTPRequest httpReq) {
        String uri = httpReq.getURI();
        if (uri.startsWith(CONTENT_EXPORT_URI)) {
            try {
                uri = URLDecoder.decode(uri, e.f);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String filePaths = "smb://" + uri.substring(5);
            int indexOf = filePaths.indexOf("&");
            if (indexOf != -1) {
                filePaths = filePaths.substring(0, indexOf);
            }
            try {
                SmbFile file = new SmbFile(filePaths);
                long contentLen = file.length();
                String contentType = FileUtil.getFileType(filePaths);
                InputStream contentIn = file.getInputStream();
                if (contentLen <= 0 || contentType.length() <= 0 || contentIn == null) {
                    httpReq.returnBadRequest();
                    return;
                }
                HTTPResponse httpRes = new HTTPResponse();
                httpRes.setContentType(contentType);
                httpRes.setStatusCode(200);
                httpRes.setContentLength(contentLen);
                httpRes.setContentInputStream(contentIn);
                httpReq.post(httpRes);
                contentIn.close();
                return;
            } catch (MalformedURLException e) {
                httpReq.returnBadRequest();
                return;
            } catch (SmbException e2) {
                httpReq.returnBadRequest();
                return;
            } catch (IOException e3) {
                httpReq.returnBadRequest();
                return;
            }
        }
        httpReq.returnBadRequest();
    }
}
