package zidoo.samba.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import zidoo.http.HTTPServerList;
import zidoo.samba.player.util.FileServer;

public class PlayFileService extends Service {
    private FileServer fileServer = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.fileServer = new FileServer();
        this.fileServer.start();
    }

    public void onDestroy() {
        super.onDestroy();
        HTTPServerList httpServerList = this.fileServer.getHttpServerList();
        httpServerList.stop();
        httpServerList.close();
        httpServerList.clear();
        this.fileServer.interrupt();
    }
}
