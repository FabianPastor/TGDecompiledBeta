package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;

public class VideoEncodingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
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
        stopForeground(true);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        boolean z = true;
        if (id == NotificationCenter.FileUploadProgressChanged) {
            String fileName = args[0];
            if (account == this.currentAccount && this.path != null && this.path.equals(fileName)) {
                Boolean enc = args[2];
                this.currentProgress = (int) (args[1].floatValue() * 100.0f);
                Builder builder = this.builder;
                int i = this.currentProgress;
                if (this.currentProgress != 0) {
                    z = false;
                }
                builder.setProgress(100, i, z);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else if (id == NotificationCenter.stopEncodingService) {
            String filepath = args[0];
            if (((Integer) args[1]).intValue() != this.currentAccount) {
                return;
            }
            if (filepath == null || filepath.equals(this.path)) {
                stopSelf();
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean z = false;
        this.path = intent.getStringExtra("path");
        int oldAccount = this.currentAccount;
        this.currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (oldAccount != this.currentAccount) {
            NotificationCenter.getInstance(oldAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileUploadProgressChanged);
        }
        boolean isGif = intent.getBooleanExtra("gif", false);
        if (this.path == null) {
            stopSelf();
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start video service");
            }
            if (this.builder == null) {
                this.builder = new Builder(ApplicationLoader.applicationContext);
                this.builder.setSmallIcon(17301640);
                this.builder.setWhen(System.currentTimeMillis());
                this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
                this.builder.setContentTitle(LocaleController.getString("AppName", R.string.AppName));
                if (isGif) {
                    this.builder.setTicker(LocaleController.getString("SendingGif", R.string.SendingGif));
                    this.builder.setContentText(LocaleController.getString("SendingGif", R.string.SendingGif));
                } else {
                    this.builder.setTicker(LocaleController.getString("SendingVideo", R.string.SendingVideo));
                    this.builder.setContentText(LocaleController.getString("SendingVideo", R.string.SendingVideo));
                }
            }
            this.currentProgress = 0;
            Builder builder = this.builder;
            int i = this.currentProgress;
            if (this.currentProgress == 0) {
                z = true;
            }
            builder.setProgress(100, i, z);
            startForeground(4, this.builder.build());
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        }
        return 2;
    }
}
