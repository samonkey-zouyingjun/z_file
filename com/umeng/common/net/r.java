package com.umeng.common.net;

import com.umeng.common.Log;
import com.umeng.common.util.e;
import com.umeng.common.util.f;
import com.umeng.common.util.g;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

/* compiled from: UClient */
public class r {
    private static final String a = r.class.getName();
    private Map<String, String> b;

    public <T extends t> T a(s sVar, Class<T> cls) {
        JSONObject a;
        String trim = sVar.c().trim();
        b(trim);
        if (s.c.equals(trim)) {
            a = a(sVar.b());
        } else if (s.b.equals(trim)) {
            a = a(sVar.d, sVar.a());
        } else {
            a = null;
        }
        if (a == null) {
            return null;
        }
        try {
            return (t) cls.getConstructor(new Class[]{JSONObject.class}).newInstance(new Object[]{a});
        } catch (Exception e) {
            Log.b(a, "SecurityException", e);
            return null;
        } catch (Exception e2) {
            Log.b(a, "NoSuchMethodException", e2);
            return null;
        } catch (Exception e22) {
            Log.b(a, "IllegalArgumentException", e22);
            return null;
        } catch (Exception e222) {
            Log.b(a, "InstantiationException", e222);
            return null;
        } catch (Exception e2222) {
            Log.b(a, "IllegalAccessException", e2222);
            return null;
        } catch (Exception e22222) {
            Log.b(a, "InvocationTargetException", e22222);
            return null;
        }
    }

    private JSONObject a(String str, JSONObject jSONObject) {
        String jSONObject2 = jSONObject.toString();
        int nextInt = new Random().nextInt(1000);
        Log.c(a, nextInt + ":\trequest: " + str + g.a + jSONObject2);
        Object httpPost = new HttpPost(str);
        HttpClient defaultHttpClient = new DefaultHttpClient(b());
        try {
            if (a()) {
                byte[] a = f.a("content=" + jSONObject2, Charset.defaultCharset().toString());
                httpPost.addHeader("Content-Encoding", "deflate");
                httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(a), (long) a.length));
            } else {
                List arrayList = new ArrayList(1);
                arrayList.add(new BasicNameValuePair("content", jSONObject2));
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, e.f));
            }
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = execute.getEntity();
                if (entity == null) {
                    return null;
                }
                InputStream inputStream;
                InputStream content = entity.getContent();
                Header firstHeader = execute.getFirstHeader("Content-Encoding");
                if (firstHeader == null || !firstHeader.getValue().equalsIgnoreCase("deflate")) {
                    inputStream = content;
                } else {
                    inputStream = new InflaterInputStream(content);
                }
                String a2 = a(inputStream);
                Log.a(a, nextInt + ":\tresponse: " + g.a + a2);
                if (a2 == null) {
                    return null;
                }
                return new JSONObject(a2);
            }
            Log.c(a, nextInt + ":\tFailed to send message. StatusCode = " + execute.getStatusLine().getStatusCode() + g.a + str);
            return null;
        } catch (Exception e) {
            Log.c(a, nextInt + ":\tClientProtocolException,Failed to send message." + str, e);
            return null;
        } catch (Exception e2) {
            Log.c(a, nextInt + ":\tIOException,Failed to send message." + str, e2);
            return null;
        } catch (Exception e22) {
            Log.c(a, nextInt + ":\tIOException,Failed to send message." + str, e22);
            return null;
        }
    }

    public boolean a() {
        return false;
    }

    private static String a(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8192);
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine + "\n");
            } catch (Exception e) {
                stringBuilder = a;
                Log.b(stringBuilder, "Caught IOException in convertStreamToString()", e);
                return null;
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e2) {
                    Log.b(a, "Caught IOException in convertStreamToString()", e2);
                    return null;
                }
            }
        }
        return stringBuilder.toString();
    }

    private JSONObject a(String str) {
        int nextInt = new Random().nextInt(1000);
        try {
            String property = System.getProperty("line.separator");
            if (str.length() <= 1) {
                Log.b(a, nextInt + ":\tInvalid baseUrl.");
                return null;
            }
            Log.a(a, nextInt + ":\tget: " + str);
            HttpUriRequest httpGet = new HttpGet(str);
            if (this.b != null && this.b.size() > 0) {
                for (String str2 : this.b.keySet()) {
                    httpGet.addHeader(str2, (String) this.b.get(str2));
                }
            }
            HttpResponse execute = new DefaultHttpClient(b()).execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = execute.getEntity();
                if (entity != null) {
                    InputStream gZIPInputStream;
                    InputStream content = entity.getContent();
                    Header firstHeader = execute.getFirstHeader("Content-Encoding");
                    if (firstHeader != null && firstHeader.getValue().equalsIgnoreCase("gzip")) {
                        Log.a(a, nextInt + "  Use GZIPInputStream get data....");
                        gZIPInputStream = new GZIPInputStream(content);
                    } else if (firstHeader == null || !firstHeader.getValue().equalsIgnoreCase("deflate")) {
                        gZIPInputStream = content;
                    } else {
                        Log.a(a, nextInt + "  Use InflaterInputStream get data....");
                        gZIPInputStream = new InflaterInputStream(content);
                    }
                    String a = a(gZIPInputStream);
                    Log.a(a, nextInt + ":\tresponse: " + property + a);
                    if (a == null) {
                        return null;
                    }
                    return new JSONObject(a);
                }
            }
            Log.c(a, nextInt + ":\tFailed to send message. StatusCode = " + execute.getStatusLine().getStatusCode() + g.a + str);
            return null;
        } catch (Exception e) {
            Log.c(a, nextInt + ":\tClientProtocolException,Failed to send message." + str, e);
            return null;
        } catch (Exception e2) {
            Log.c(a, nextInt + ":\tIOException,Failed to send message." + str, e2);
            return null;
        }
    }

    private HttpParams b() {
        HttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 20000);
        HttpProtocolParams.setUserAgent(basicHttpParams, System.getProperty("http.agent"));
        return basicHttpParams;
    }

    public r a(Map<String, String> map) {
        this.b = map;
        return this;
    }

    private void b(String str) {
        if (g.d(str) || (s.c.equals(str.trim()) ^ s.b.equals(str.trim())) == 0) {
            throw new RuntimeException("验证请求方式失败[" + str + "]");
        }
    }
}
