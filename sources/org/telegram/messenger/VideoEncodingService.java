package org.telegram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes.dex */
public class VideoEncodingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private int currentAccount;
    private int currentProgress;
    private String path;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public VideoEncodingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopEncodingService);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        try {
            stopForeground(true);
        } catch (Throwable unused) {
        }
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(4);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("destroy video service");
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        boolean z = true;
        if (i == NotificationCenter.fileUploadProgressChanged) {
            String str2 = (String) objArr[0];
            if (i2 != this.currentAccount || (str = this.path) == null || !str.equals(str2)) {
                return;
            }
            float min = Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue()));
            Boolean bool = (Boolean) objArr[3];
            int i3 = (int) (min * 100.0f);
            this.currentProgress = i3;
            NotificationCompat.Builder builder = this.builder;
            if (i3 != 0) {
                z = false;
            }
            builder.setProgress(100, i3, z);
            try {
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (i != NotificationCenter.stopEncodingService) {
        } else {
            String str3 = (String) objArr[0];
            if (((Integer) objArr[1]).intValue() != this.currentAccount) {
                return;
            }
            if (str3 != null && !str3.equals(this.path)) {
                return;
            }
            stopSelf();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        this.path = intent.getStringExtra("path");
        int i3 = this.currentAccount;
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        this.currentAccount = intExtra;
        if (!UserConfig.isValidAccount(intExtra)) {
            stopSelf();
            return 2;
        }
        if (i3 != this.currentAccount) {
            NotificationCenter notificationCenter = NotificationCenter.getInstance(i3);
            int i4 = NotificationCenter.fileUploadProgressChanged;
            notificationCenter.removeObserver(this, i4);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, i4);
        }
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
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder;
            builder.setSmallIcon(17301640);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", R.string.AppName));
            if (booleanExtra) {
                NotificationCompat.Builder builder2 = this.builder;
                int i5 = R.string.SendingGif;
                builder2.setTicker(LocaleController.getString("SendingGif", i5));
                this.builder.setContentText(LocaleController.getString("SendingGif", i5));
            } else {
                NotificationCompat.Builder builder3 = this.builder;
                int i6 = R.string.SendingVideo;
                builder3.setTicker(LocaleController.getString("SendingVideo", i6));
                this.builder.setContentText(LocaleController.getString("SendingVideo", i6));
            }
        }
        this.currentProgress = 0;
        this.builder.setProgress(100, 0, true);
        startForeground(4, this.builder.build());
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        return 2;
    }
}
