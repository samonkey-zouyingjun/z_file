package com.umeng.common.net;

import com.umeng.common.Log;
import com.umeng.common.util.g;
import java.util.Map;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* compiled from: DownloadingService */
final class i implements Runnable {
    final /* synthetic */ String[] a;
    final /* synthetic */ boolean b;
    final /* synthetic */ Map c;

    i(String[] strArr, boolean z, Map map) {
        this.a = strArr;
        this.b = z;
        this.c = map;
    }

    public void run() {
        int nextInt = new Random().nextInt(1000);
        if (this.a == null) {
            Log.a(DownloadingService.o, nextInt + "service report: urls is null");
            return;
        }
        String[] strArr = this.a;
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            String str = strArr[i];
            String a = g.a();
            String str2 = a.split(" ")[0];
            a = a.split(" ")[1];
            long currentTimeMillis = System.currentTimeMillis();
            StringBuilder stringBuilder = new StringBuilder(str);
            stringBuilder.append("&data=" + str2);
            stringBuilder.append("&time=" + a);
            stringBuilder.append("&ts=" + currentTimeMillis);
            if (this.b) {
                stringBuilder.append("&action_type=" + 1);
            } else {
                stringBuilder.append("&action_type=" + -2);
            }
            if (this.c != null) {
                for (String a2 : this.c.keySet()) {
                    stringBuilder.append("&" + a2 + "=" + ((String) this.c.get(a2)));
                }
            }
            try {
                Log.a(DownloadingService.o, nextInt + ": service report:\tget: " + stringBuilder.toString());
                HttpUriRequest httpGet = new HttpGet(stringBuilder.toString());
                HttpParams basicHttpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, 20000);
                HttpResponse execute = new DefaultHttpClient(basicHttpParams).execute(httpGet);
                Log.a(DownloadingService.o, nextInt + ": service report:status code:  " + execute.getStatusLine().getStatusCode());
                if (execute.getStatusLine().getStatusCode() != 200) {
                    i++;
                } else {
                    return;
                }
            } catch (Exception e) {
                Log.c(DownloadingService.o, nextInt + ": service report:\tClientProtocolException,Failed to send message." + str, e);
            } catch (Exception e2) {
                Log.c(DownloadingService.o, nextInt + ": service report:\tIOException,Failed to send message." + str, e2);
            }
        }
    }
}
