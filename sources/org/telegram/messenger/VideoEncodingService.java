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

    public IBinder onBind(Intent intent) {
        return null;
    }

    public VideoEncodingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopEncodingService);
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Throwable unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        boolean z = true;
        if (i == NotificationCenter.FileUploadProgressChanged) {
            String str2 = objArr[0];
            if (i2 == this.currentAccount && (str = this.path) != null && str.equals(str2)) {
                float min = Math.min(1.0f, ((float) objArr[1].longValue()) / ((float) objArr[2].longValue()));
                Boolean bool = objArr[3];
                this.currentProgress = (int) (min * 100.0f);
                NotificationCompat.Builder builder2 = this.builder;
                int i3 = this.currentProgress;
                if (i3 != 0) {
                    z = false;
                }
                builder2.setProgress(100, i3, z);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        } else if (i == NotificationCenter.stopEncodingService) {
            String str3 = objArr[0];
            if (objArr[1].intValue() != this.currentAccount) {
                return;
            }
            if (str3 == null || str3.equals(this.path)) {
                stopSelf();
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        this.path = intent.getStringExtra("path");
        int i3 = this.currentAccount;
        this.currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (i3 != this.currentAccount) {
            NotificationCenter.getInstance(i3).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileUploadProgressChanged);
        }
        boolean z = false;
        boolean booleanExtra = intent.getBooleanExtra("gif", false);
        if (this.path == null) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start video service");
        }
        if (this.builder == null) {
            NotificationsController.checkOtherNotificationsChannel();
            this.builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
            if (booleanExtra) {
                this.builder.setTicker(LocaleController.getString("SendingGif", NUM));
                this.builder.setContentText(LocaleController.getString("SendingGif", NUM));
            } else {
                this.builder.setTicker(LocaleController.getString("SendingVideo", NUM));
                this.builder.setContentText(LocaleController.getString("SendingVideo", NUM));
            }
        }
        this.currentProgress = 0;
        NotificationCompat.Builder builder2 = this.builder;
        int i4 = this.currentProgress;
        if (i4 == 0) {
            z = true;
        }
        builder2.setProgress(100, i4, z);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
