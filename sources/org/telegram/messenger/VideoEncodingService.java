package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;

public class VideoEncodingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
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
        stopForeground(true);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        boolean z = true;
        String str;
        if (i == NotificationCenter.FileUploadProgressChanged) {
            str = (String) objArr[0];
            if (i2 == this.currentAccount) {
                String str2 = this.path;
                if (str2 != null && str2.equals(str)) {
                    Boolean bool = (Boolean) objArr[2];
                    this.currentProgress = (int) (((Float) objArr[1]).floatValue() * 100.0f);
                    Builder builder = this.builder;
                    int i3 = this.currentProgress;
                    if (i3 != 0) {
                        z = false;
                    }
                    builder.setProgress(100, i3, z);
                    try {
                        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
            }
        } else if (i == NotificationCenter.stopEncodingService) {
            str = (String) objArr[0];
            if (((Integer) objArr[1]).intValue() != this.currentAccount) {
                return;
            }
            if (str == null || str.equals(this.path)) {
                stopSelf();
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        this.path = intent.getStringExtra("path");
        i = this.currentAccount;
        this.currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (i != this.currentAccount) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
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
            this.builder = new Builder(ApplicationLoader.applicationContext);
            this.builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
            String str;
            if (booleanExtra) {
                str = "SendingGif";
                this.builder.setTicker(LocaleController.getString(str, NUM));
                this.builder.setContentText(LocaleController.getString(str, NUM));
            } else {
                str = "SendingVideo";
                this.builder.setTicker(LocaleController.getString(str, NUM));
                this.builder.setContentText(LocaleController.getString(str, NUM));
            }
        }
        this.currentProgress = 0;
        Builder builder = this.builder;
        int i3 = this.currentProgress;
        if (i3 == 0) {
            z = true;
        }
        builder.setProgress(100, i3, z);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
