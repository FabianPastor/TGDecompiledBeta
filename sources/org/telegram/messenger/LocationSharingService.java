package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.ArrayList;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.LaunchActivity;

public class LocationSharingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
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
        LocationSharingService$$ExternalSyntheticLambda1 locationSharingService$$ExternalSyntheticLambda1 = new LocationSharingService$$ExternalSyntheticLambda1(this);
        this.runnable = locationSharingService$$ExternalSyntheticLambda1;
        this.handler.postDelayed(locationSharingService$$ExternalSyntheticLambda1, 1000);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1() {
        this.handler.postDelayed(this.runnable, 1000);
        Utilities.stageQueue.postRunnable(LocationSharingService$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreate$0() {
        for (int i = 0; i < 3; i++) {
            LocationController.getInstance(i).update();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Handler handler2 = this.handler;
        if (handler2 != null) {
            handler2.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(6);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Handler handler2;
        if (i == NotificationCenter.liveLocationsChanged && (handler2 = this.handler) != null) {
            handler2.post(new LocationSharingService$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$2() {
        if (getInfos().isEmpty()) {
            stopSelf();
        } else {
            updateNotification(true);
        }
    }

    private ArrayList<LocationController.SharingLocationInfo> getInfos() {
        ArrayList<LocationController.SharingLocationInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ArrayList<LocationController.SharingLocationInfo> arrayList2 = LocationController.getInstance(i).sharingLocationsUI;
            if (!arrayList2.isEmpty()) {
                arrayList.addAll(arrayList2);
            }
        }
        return arrayList;
    }

    private void updateNotification(boolean z) {
        String str;
        String str2;
        if (this.builder != null) {
            ArrayList<LocationController.SharingLocationInfo> infos = getInfos();
            if (infos.size() == 1) {
                LocationController.SharingLocationInfo sharingLocationInfo = infos.get(0);
                int dialogId = (int) sharingLocationInfo.messageObject.getDialogId();
                int i = sharingLocationInfo.messageObject.currentAccount;
                if (dialogId > 0) {
                    str2 = UserObject.getFirstName(MessagesController.getInstance(i).getUser(Integer.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-dialogId));
                    str2 = chat != null ? chat.title : "";
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", NUM);
                }
            } else {
                str2 = LocaleController.formatPluralString("Chats", infos.size());
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", NUM);
            }
            String format = String.format(str, new Object[]{LocaleController.getString("AttachLiveLocation", NUM), str2});
            this.builder.setTicker(format);
            this.builder.setContentText(format);
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
            Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent2.setAction("org.tmessages.openlocations");
            intent2.addCategory("android.intent.category.LAUNCHER");
            PendingIntent activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, 0);
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder2;
            builder2.setWhen(System.currentTimeMillis());
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
