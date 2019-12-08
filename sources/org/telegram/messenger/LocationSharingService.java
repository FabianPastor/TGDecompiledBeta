package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.LaunchActivity;

public class LocationSharingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
    private Handler handler;
    private Runnable runnable;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public LocationSharingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        this.runnable = new -$$Lambda$LocationSharingService$nPgxbLYJUuL6mX_Yd5lVL7HSSeY(this);
        this.handler.postDelayed(this.runnable, 1000);
    }

    public /* synthetic */ void lambda$onCreate$1$LocationSharingService() {
        this.handler.postDelayed(this.runnable, 1000);
        Utilities.stageQueue.postRunnable(-$$Lambda$LocationSharingService$9a42Vs-_pZkbJD8v75PcBjP85zg.INSTANCE);
    }

    static /* synthetic */ void lambda$null$0() {
        for (int i = 0; i < 3; i++) {
            LocationController.getInstance(i).update();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Handler handler = this.handler;
        if (handler != null) {
            handler.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(6);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.liveLocationsChanged) {
            Handler handler = this.handler;
            if (handler != null) {
                handler.post(new -$$Lambda$LocationSharingService$Pgnt4LkJ9hM4FIwwB259oJzj6ug(this));
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$LocationSharingService() {
        if (getInfos().isEmpty()) {
            stopSelf();
        } else {
            updateNotification(true);
        }
    }

    private ArrayList<SharingLocationInfo> getInfos() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            ArrayList arrayList2 = LocationController.getInstance(i).sharingLocationsUI;
            if (!arrayList2.isEmpty()) {
                arrayList.addAll(arrayList2);
            }
        }
        return arrayList;
    }

    private void updateNotification(boolean z) {
        if (this.builder != null) {
            String firstName;
            ArrayList infos = getInfos();
            if (infos.size() == 1) {
                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) infos.get(0);
                int dialogId = (int) sharingLocationInfo.messageObject.getDialogId();
                int i = sharingLocationInfo.messageObject.currentAccount;
                if (dialogId > 0) {
                    firstName = UserObject.getFirstName(MessagesController.getInstance(i).getUser(Integer.valueOf(dialogId)));
                } else {
                    Chat chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-dialogId));
                    firstName = chat != null ? chat.title : "";
                }
            } else {
                firstName = LocaleController.formatPluralString("Chats", infos.size());
            }
            firstName = String.format(LocaleController.getString("AttachLiveLocationIsSharing", NUM), new Object[]{LocaleController.getString("AttachLiveLocation", NUM), firstName});
            this.builder.setTicker(firstName);
            this.builder.setContentText(firstName);
            if (z) {
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (getInfos().isEmpty()) {
            stopSelf();
        }
        if (this.builder == null) {
            intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("org.tmessages.openlocations");
            intent.addCategory("android.intent.category.LAUNCHER");
            PendingIntent activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
            this.builder = new Builder(ApplicationLoader.applicationContext);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(NUM);
            this.builder.setContentIntent(activity);
            NotificationsController.checkOtherNotificationsChannel();
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", NUM));
            this.builder.addAction(0, LocaleController.getString("StopLiveLocation", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, new Intent(ApplicationLoader.applicationContext, StopLiveLocationReceiver.class), NUM));
        }
        updateNotification(false);
        startForeground(6, this.builder.build());
        return 2;
    }
}
