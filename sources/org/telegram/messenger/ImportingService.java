package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter;

public class ImportingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public ImportingService() {
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Throwable unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(5);
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy import service");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if ((i == NotificationCenter.historyImportProgressChanged || i == NotificationCenter.stickersImportProgressChanged) && !hasImportingStickers() && !hasImportingStickers()) {
            stopSelf();
        }
    }

    private boolean hasImportingHistory() {
        for (int i = 0; i < 4; i++) {
            if (SendMessagesHelper.getInstance(i).isImportingHistory()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasImportingStickers() {
        for (int i = 0; i < 4; i++) {
            if (SendMessagesHelper.getInstance(i).isImportingStickers()) {
                return true;
            }
        }
        return false;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (hasImportingStickers() || hasImportingHistory()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start import service");
            }
            if (this.builder == null) {
                NotificationsController.checkOtherNotificationsChannel();
                NotificationCompat.Builder builder2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
                this.builder = builder2;
                builder2.setSmallIcon(17301640);
                this.builder.setWhen(System.currentTimeMillis());
                this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
                this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
                if (hasImportingHistory()) {
                    this.builder.setTicker(LocaleController.getString("ImporImportingService", NUM));
                    this.builder.setContentText(LocaleController.getString("ImporImportingService", NUM));
                } else {
                    this.builder.setTicker(LocaleController.getString("ImporImportingStickersService", NUM));
                    this.builder.setContentText(LocaleController.getString("ImporImportingStickersService", NUM));
                }
            }
            this.builder.setProgress(100, 0, true);
            startForeground(5, this.builder.build());
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(5, this.builder.build());
            return 2;
        }
        stopSelf();
        return 2;
    }
}
