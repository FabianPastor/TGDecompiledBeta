package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter;

public class VideoEncodingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private int currentAccount;
    private int currentProgress;
    private String path;

    public VideoEncodingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopEncodingService);
    }

    public IBinder onBind(Intent arg2) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Throwable th) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String str;
        boolean z = true;
        if (id == NotificationCenter.fileUploadProgressChanged) {
            String fileName = args[0];
            if (account == this.currentAccount && (str = this.path) != null && str.equals(fileName)) {
                float progress = Math.min(1.0f, ((float) args[1].longValue()) / ((float) args[2].longValue()));
                Boolean bool = args[3];
                int i = (int) (100.0f * progress);
                this.currentProgress = i;
                NotificationCompat.Builder builder2 = this.builder;
                if (i != 0) {
                    z = false;
                }
                builder2.setProgress(100, i, z);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else if (id == NotificationCenter.stopEncodingService) {
            String filepath = args[0];
            if (args[1].intValue() != this.currentAccount) {
                return;
            }
            if (filepath == null || filepath.equals(this.path)) {
                stopSelf();
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.path = intent.getStringExtra("path");
        int oldAccount = this.currentAccount;
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        this.currentAccount = intExtra;
        if (!UserConfig.isValidAccount(intExtra)) {
            stopSelf();
            return 2;
        }
        if (oldAccount != this.currentAccount) {
            NotificationCenter.getInstance(oldAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadProgressChanged);
        }
        boolean isGif = intent.getBooleanExtra("gif", false);
        if (this.path == null) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start video service");
        }
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder2;
            builder2.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
            if (isGif) {
                this.builder.setTicker(LocaleController.getString("SendingGif", NUM));
                this.builder.setContentText(LocaleController.getString("SendingGif", NUM));
            } else {
                this.builder.setTicker(LocaleController.getString("SendingVideo", NUM));
                this.builder.setContentText(LocaleController.getString("SendingVideo", NUM));
            }
        }
        this.currentProgress = 0;
        this.builder.setProgress(100, 0, true);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
