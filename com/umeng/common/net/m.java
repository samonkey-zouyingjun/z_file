package com.umeng.common.net;

import android.os.AsyncTask;
import com.umeng.common.Log;

/* compiled from: ReportClient */
public class m extends r {
    private static final String a = m.class.getName();

    /* compiled from: ReportClient */
    public interface a {
        void a();

        void a(com.umeng.common.net.o.a aVar);
    }

    /* compiled from: ReportClient */
    private class b extends AsyncTask<Integer, Integer, com.umeng.common.net.o.a> {
        final /* synthetic */ m a;
        private n b;
        private a c;

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return a((Integer[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            a((com.umeng.common.net.o.a) obj);
        }

        public b(m mVar, n nVar, a aVar) {
            this.a = mVar;
            this.b = nVar;
            this.c = aVar;
        }

        protected void onPreExecute() {
            if (this.c != null) {
                this.c.a();
            }
        }

        protected void a(com.umeng.common.net.o.a aVar) {
            if (this.c != null) {
                this.c.a(aVar);
            }
        }

        protected com.umeng.common.net.o.a a(Integer... numArr) {
            return this.a.a(this.b);
        }
    }

    public com.umeng.common.net.o.a a(n nVar) {
        o oVar = (o) a((s) nVar, o.class);
        return oVar == null ? com.umeng.common.net.o.a.FAIL : oVar.a;
    }

    public void a(n nVar, a aVar) {
        try {
            new b(this, nVar, aVar).execute(new Integer[0]);
        } catch (Exception e) {
            Log.b(a, "", e);
            if (aVar != null) {
                aVar.a(com.umeng.common.net.o.a.FAIL);
            }
        }
    }
}
