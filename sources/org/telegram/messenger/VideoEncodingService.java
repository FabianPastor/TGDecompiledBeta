package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("destroy video service");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        boolean z = true;
        String str;
        if (i == NotificationCenter.FileUploadProgressChanged) {
            str = (String) objArr[0];
            if (i2 == this.currentAccount && this.path != 0 && this.path.equals(str) != 0) {
                Boolean bool = (Boolean) objArr[2];
                this.currentProgress = (int) (((Float) objArr[1]).floatValue() * NUM);
                i = this.builder;
                objArr = this.currentProgress;
                if (this.currentProgress != 0) {
                    z = false;
                }
                i.setProgress(100, objArr, z);
                try {
                    NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        } else if (i == NotificationCenter.stopEncodingService) {
            str = (String) objArr[0];
            if (((Integer) objArr[1]).intValue() != this.currentAccount) {
                return;
            }
            if (str == null || str.equals(this.path) != 0) {
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
        i2 = 0;
        intent = intent.getBooleanExtra("gif", false);
        if (this.path == 0) {
            stopSelf();
            return 2;
        }
        if (BuildVars.LOGS_ENABLED != 0) {
            FileLog.m0d("start video service");
        }
        if (this.builder == 0) {
            this.builder = new Builder(ApplicationLoader.applicationContext);
            this.builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (intent != null) {
                this.builder.setTicker(LocaleController.getString("SendingGif", C0446R.string.SendingGif));
                this.builder.setContentText(LocaleController.getString("SendingGif", C0446R.string.SendingGif));
            } else {
                this.builder.setTicker(LocaleController.getString("SendingVideo", C0446R.string.SendingVideo));
                this.builder.setContentText(LocaleController.getString("SendingVideo", C0446R.string.SendingVideo));
            }
        }
        this.currentProgress = 0;
        intent = this.builder;
        int i3 = this.currentProgress;
        if (this.currentProgress == 0) {
            i2 = 1;
        }
        intent.setProgress(100, i3, i2);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
