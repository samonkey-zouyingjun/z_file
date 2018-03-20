package com.zidoo.custom.net;

import com.umeng.common.util.e;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import zidoo.http.HTTP;

public class ZidooNetDataTool {
    public static final int CONNECTTIMEOUT = 40000;
    public static final int READTIMEOUT = 40000;
    private static final String USERAGENT = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)";

    public static String getURLContentByInputStream(String webpath, int connectTimeout, int readTimeout) {
        String urlString = null;
        try {
            HttpURLConnection urlc = (HttpURLConnection) new URL(webpath.trim()).openConnection();
            urlc.setConnectTimeout(connectTimeout);
            urlc.setReadTimeout(readTimeout);
            if (urlc.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                String m = "";
                String n = "";
                while (true) {
                    m = br.readLine();
                    if (m == null) {
                        break;
                    }
                    n = new StringBuilder(String.valueOf(n)).append(m).toString();
                }
                urlString = n;
            }
            urlc.disconnect();
            return urlString;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static String getURLContentByInputStream(String webpath, int connectTimeout, int readTimeout, String text_code) {
        String urlString = "";
        InputStream inputstream = null;
        try {
            HttpURLConnection urlc = (HttpURLConnection) new URL(webpath.trim()).openConnection();
            urlc.setConnectTimeout(connectTimeout);
            urlc.setReadTimeout(readTimeout);
            if (urlc.getResponseCode() == 200) {
                inputstream = urlc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, text_code));
                StringBuffer sb = new StringBuffer();
                String str = "";
                while (true) {
                    str = br.readLine();
                    if (str == null) {
                        break;
                    }
                    sb.append(str);
                }
                urlString = sb.toString();
            }
            urlc.disconnect();
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
        }
        return urlString;
    }

    public static String getURLContentByAgent(String url, int connectTimeout, int readTimeout) {
        return getURLContentByAgent(url, connectTimeout, readTimeout, null);
    }

    public static String getURLContentByAgent(String url, int connectTimeout, int readTimeout, String textCode) {
        return getURLContentByAgent(url, USERAGENT, connectTimeout, readTimeout, textCode);
    }

    public static String getURLContentByAgent(String url, String userAgent, int connectTimeout, int readTimeout) {
        return getURLContentByAgent(url, userAgent, connectTimeout, readTimeout, null);
    }

    public static String getURLContentByAgent(String url, String userAgent, int connectTimeout, int readTimeout, String textCode) {
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
            HttpConnectionParams.setSoTimeout(httpParams, readTimeout);
            HttpClientParams.setRedirecting(httpParams, true);
            HttpProtocolParams.setUserAgent(httpParams, userAgent);
            HttpResponse response = new DefaultHttpClient(httpParams).execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                String urlString;
                if (textCode != null) {
                    urlString = EntityUtils.toString(entity, textCode);
                } else {
                    urlString = EntityUtils.toString(entity);
                }
                if (urlString == null || urlString.equals("")) {
                    return null;
                }
                return urlString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getURLContentByHttpGet(String webpath, int connectTimeout, int readTimeout) {
        String urlString = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(connectTimeout));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(readTimeout));
            HttpResponse response = client.execute(new HttpGet(webpath));
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
        }
        return urlString;
    }

    public static String getURLContentByHttpGet(String webpath, int connectTimeout, int readTimeout, String textCode) {
        String urlString = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(connectTimeout));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(readTimeout));
            HttpResponse response = client.execute(new HttpGet(webpath));
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(entity, textCode);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
        }
        return urlString;
    }

    public static String getURLContentByHttpPost(String webpath, List<NameValuePair> params, int connectTimeout, int readTimeout) {
        String urlString = null;
        try {
            HttpPost httpRequest = new HttpPost(webpath);
            if (params != null) {
                httpRequest.setEntity(new UrlEncodedFormEntity(params, e.f));
            }
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setParameter("http.connection.timeout", Integer.valueOf(connectTimeout));
            client.getParams().setParameter("http.socket.timeout", Integer.valueOf(readTimeout));
            HttpResponse response = client.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
        }
        return urlString;
    }

    public static String getURLContentByHttpPost(String webpath, String params, int connectTimeout, int readTimeout, String textCode) {
        String urlString = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(webpath).openConnection();
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setRequestMethod(HTTP.POST);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            byte[] myData = params.toString().getBytes();
            urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty(HTTP.CONTENT_LENGTH, String.valueOf(myData.length));
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(myData, 0, myData.length);
            outputStream.close();
            if (urlConnection.getResponseCode() == 200) {
                return changeInputStream(urlConnection.getInputStream(), textCode);
            }
            urlConnection.disconnect();
            return urlString;
        } catch (Exception e1) {
            e1.printStackTrace();
            urlString = null;
        }
    }

    private static String changeInputStream(InputStream inputStream, String encode) {
        String result = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            if (inputStream != null) {
                while (true) {
                    int len = inputStream.read(data);
                    if (len == -1) {
                        break;
                    }
                    try {
                        byteArrayOutputStream.write(data, 0, len);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);
                inputStream.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return result;
    }

    public static String getURLContentByGZIP(String webpath, int connectTimeout, int readTimeout) {
        String urlString = null;
        try {
            HttpURLConnection urlc = (HttpURLConnection) new URL(webpath.trim()).openConnection();
            urlc.setConnectTimeout(connectTimeout);
            urlc.setReadTimeout(readTimeout);
            if (urlc.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlc.getInputStream()), "GBK"));
                String m = "";
                String n = "";
                while (true) {
                    m = br.readLine();
                    if (m == null) {
                        break;
                    }
                    n = new StringBuilder(String.valueOf(n)).append(m).toString();
                }
                urlString = n;
            }
            urlc.disconnect();
            return urlString;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static InputStream getInputStream(String url, int connectTimeout, int readTimeout) {
        try {
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setConnectTimeout(connectTimeout);
            http.setReadTimeout(readTimeout);
            http.setDoInput(true);
            http.connect();
            if (http.getResponseCode() == 200) {
                return http.getInputStream();
            }
            http.disconnect();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getURLReallyFileName(String url) {
        HttpURLConnection conn = null;
        if (url == null || url.length() < 1) {
            return null;
        }
        String filename;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
            conn.getResponseCode();
            filename = conn.getHeaderField("Content-Disposition");
            if (filename == null || filename.length() <= 1) {
                String path = conn.getURL().toString();
                filename = path.substring(path.lastIndexOf("/") + 1, path.length());
            } else {
                filename = filename.substring(filename.indexOf("filename=") + "filename=".length(), filename.length()).trim();
            }
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            filename = null;
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return filename;
    }
}
