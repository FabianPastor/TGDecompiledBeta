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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public class LocationSharingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private Handler handler;
    private Runnable runnable;

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

    /* renamed from: lambda$onCreate$1$org-telegram-messenger-LocationSharingService  reason: not valid java name */
    public /* synthetic */ void m751lambda$onCreate$1$orgtelegrammessengerLocationSharingService() {
        this.handler.postDelayed(this.runnable, 1000);
        Utilities.stageQueue.postRunnable(LocationSharingService$$ExternalSyntheticLambda2.INSTANCE);
    }

    static /* synthetic */ void lambda$onCreate$0() {
        for (int a = 0; a < 3; a++) {
            LocationController.getInstance(a).update();
        }
    }

    public IBinder onBind(Intent arg2) {
        return null;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        Handler handler2;
        if (id == NotificationCenter.liveLocationsChanged && (handler2 = this.handler) != null) {
            handler2.post(new LocationSharingService$$ExternalSyntheticLambda0(this));
        }
    }

    /* renamed from: lambda$didReceivedNotification$2$org-telegram-messenger-LocationSharingService  reason: not valid java name */
    public /* synthetic */ void m750x7be92e23() {
        if (getInfos().isEmpty()) {
            stopSelf();
        } else {
            updateNotification(true);
        }
    }

    private ArrayList<LocationController.SharingLocationInfo> getInfos() {
        ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
        for (int a = 0; a < 3; a++) {
            ArrayList<LocationController.SharingLocationInfo> arrayList = LocationController.getInstance(a).sharingLocationsUI;
            if (!arrayList.isEmpty()) {
                infos.addAll(arrayList);
            }
        }
        return infos;
    }

    private void updateNotification(boolean post) {
        String param;
        String str;
        if (this.builder != null) {
            ArrayList<LocationController.SharingLocationInfo> infos = getInfos();
            if (infos.size() == 1) {
                LocationController.SharingLocationInfo info = infos.get(0);
                long dialogId = info.messageObject.getDialogId();
                int currentAccount = info.messageObject.currentAccount;
                if (DialogObject.isUserDialog(dialogId)) {
                    param = UserObject.getFirstName(MessagesController.getInstance(currentAccount).getUser(Long.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-dialogId));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", NUM);
                }
            } else {
                param = LocaleController.formatPluralString("Chats", infos.size());
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", NUM);
            }
            String text = String.format(str, new Object[]{LocaleController.getString("AttachLiveLocation", NUM), param});
            this.builder.setTicker(text);
            this.builder.setContentText(text);
            if (post) {
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getInfos().isEmpty()) {
            stopSelf();
        }
        if (this.builder == null) {
            Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent2.setAction("org.tmessages.openlocations");
            intent2.addCategory("android.intent.category.LAUNCHER");
            PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, 0);
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder2;
            builder2.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(NUM);
            this.builder.setContentIntent(contentIntent);
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
