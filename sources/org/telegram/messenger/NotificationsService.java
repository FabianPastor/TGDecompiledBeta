package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationsService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public void onDestroy() {
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            sendBroadcast(new Intent("org.telegram.start"));
        }
    }
}
