package com.zidoo.control.center.http.tool;

import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import com.zidoo.control.center.tool.HttpFileReadInfo;
import com.zidoo.control.center.tool.ZidooHttpStatusContants;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONObject;
import zidoo.http.HTTP;

public class ZidooHttpTool {

    public static class HttpFileRead {
        public static final long CACHE_SPACE = 10485760;
        static final int READ_BUF = 8192;

        public static HttpFileReadInfo readData(BasicHttpEntityEnclosingRequest br) {
            Exception e;
            HttpFileReadInfo httpFileReadInfo = null;
            try {
                InputStream is = br.getEntity().getContent();
                long size = br.getEntity().getContentLength();
                if (size > CACHE_SPACE) {
                    return null;
                }
                byte[] buf = new byte[1024];
                int splitbyte = 0;
                int rlen = 0;
                int read = is.read(buf, 0, 1024);
                while (read > 0) {
                    rlen += read;
                    splitbyte = findHeaderEnd(buf, rlen);
                    if (splitbyte > 0) {
                        break;
                    }
                    read = is.read(buf, rlen, 1024 - rlen);
                }
                ByteArrayOutputStream f = new ByteArrayOutputStream();
                if (splitbyte < rlen) {
                    f.write(buf, splitbyte, rlen - splitbyte);
                }
                if (splitbyte < rlen) {
                    size -= (long) ((rlen - splitbyte) + 1);
                } else if (splitbyte == 0) {
                    size = 0;
                }
                buf = new byte[8192];
                while (rlen >= 0 && size > 0) {
                    rlen = is.read(buf, 0, 8192);
                    size -= (long) rlen;
                    if (rlen > 0) {
                        f.write(buf, 0, rlen);
                    }
                }
                byte[] readBuf = f.toByteArray();
                f.close();
                is.close();
                HttpFileReadInfo httpFileReadInfo2 = new HttpFileReadInfo();
                try {
                    httpFileReadInfo2.mFileByte = readBuf;
                    httpFileReadInfo = httpFileReadInfo2;
                } catch (Exception e2) {
                    e = e2;
                    httpFileReadInfo = httpFileReadInfo2;
                    e.printStackTrace();
                    return httpFileReadInfo;
                }
                return httpFileReadInfo;
            } catch (Exception e3) {
                e = e3;
            }
        }

        private static int findHeaderEnd(byte[] buf, int rlen) {
            int splitbyte = 0;
            while (splitbyte + 3 < rlen) {
                if (buf[splitbyte] == HTTP.CR && buf[splitbyte + 1] == (byte) 10 && buf[splitbyte + 2] == HTTP.CR && buf[splitbyte + 3] == (byte) 10) {
                    return splitbyte + 4;
                }
                splitbyte++;
            }
            return 0;
        }
    }

    public static ZidooHttpParmInfo praserHttpUri(String uri) {
        try {
            ZidooHttpParmInfo zidooHttpParmInfo = new ZidooHttpParmInfo();
            int qmi = uri.indexOf(63);
            if (qmi >= 0) {
                zidooHttpParmInfo.mParmsHashMap = getHttpParms(uri.substring(qmi + 1));
            }
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
                int s = uri.indexOf(47);
                if (s >= 0) {
                    zidooHttpParmInfo.mElementName = uri.substring(0, s);
                    zidooHttpParmInfo.mUri = uri.substring(s + 1);
                    return zidooHttpParmInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> getHttpParms(String uri) {
        HashMap<String, String> parmsHashMap = new HashMap();
        if (uri != null) {
            try {
                int qmi = uri.indexOf(63);
                if (qmi >= 0) {
                    uri = uri.substring(qmi + 1);
                }
                StringTokenizer st = new StringTokenizer(uri, "&");
                while (st.hasMoreTokens()) {
                    String e = st.nextToken();
                    int sep = e.indexOf(61);
                    if (sep >= 0) {
                        String key = e.substring(0, sep).trim();
                        String value = URLDecoder.decode(e.substring(sep + 1), "utf-8");
                        if (!(key == null || value == null)) {
                            parmsHashMap.put(key, value);
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return parmsHashMap;
    }

    public static String getCallBackJson(int status) {
        JSONObject object = new JSONObject();
        try {
            object.put(NotificationCompat.CATEGORY_STATUS, status);
            if (status != 200) {
                String msg = "";
                switch (status) {
                    case ZidooHttpStatusContants.HTTP_ELEMENT_NOT_REGISTER /*801*/:
                        msg = "The application element is not registered";
                        break;
                    case ZidooHttpStatusContants.HTTP_ELEMENT_BIND_ERROR /*802*/:
                        msg = "The application element bind error";
                        break;
                    case ZidooHttpStatusContants.HTTP_ELEMENT_DATA_ERROR /*803*/:
                        msg = "The application element returns data that is empty or incorrect";
                        break;
                    case ZidooHttpStatusContants.HTTP_URL_ERROR /*804*/:
                        msg = "Url error";
                        break;
                    case ZidooHttpStatusContants.HTTP_PARAMETER_ERROR /*805*/:
                        msg = "The parameter is error or no parameter is submitted";
                        break;
                    case ZidooHttpStatusContants.HTTP_RESOURCE_NOT_EXIST /*806*/:
                        msg = "The resource does not exist";
                        break;
                    case ZidooHttpStatusContants.HTTP_EXECUTE_TIME_OUT /*807*/:
                        msg = "Execute timeout";
                        break;
                }
                object.put(NotificationCompat.CATEGORY_MESSAGE, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private static String decodePercent(String str) {
        try {
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (i < str.length()) {
                char c = str.charAt(i);
                switch (c) {
                    case MotionEventCompat.AXIS_GENERIC_6 /*37*/:
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    case MotionEventCompat.AXIS_GENERIC_12 /*43*/:
                        sb.append(' ');
                        break;
                    default:
                        sb.append(c);
                        break;
                }
                i++;
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
