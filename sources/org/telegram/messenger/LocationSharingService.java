package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.LaunchActivity;

public class LocationSharingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
    private Handler handler;
    private Runnable runnable;

    /* renamed from: org.telegram.messenger.LocationSharingService$1 */
    class C02461 implements Runnable {

        /* renamed from: org.telegram.messenger.LocationSharingService$1$1 */
        class C02451 implements Runnable {
            C02451() {
            }

            public void run() {
                for (int i = 0; i < 3; i++) {
                    LocationController.getInstance(i).update();
                }
            }
        }

        C02461() {
        }

        public void run() {
            LocationSharingService.this.handler.postDelayed(LocationSharingService.this.runnable, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            Utilities.stageQueue.postRunnable(new C02451());
        }
    }

    /* renamed from: org.telegram.messenger.LocationSharingService$2 */
    class C02472 implements Runnable {
        C02472() {
        }

        public void run() {
            if (LocationSharingService.this.getInfos().isEmpty()) {
                LocationSharingService.this.stopSelf();
            } else {
                LocationSharingService.this.updateNotification(true);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public LocationSharingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        this.runnable = new C02461();
        this.handler.postDelayed(this.runnable, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    }

    public void onDestroy() {
        if (this.handler != null) {
            this.handler.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.liveLocationsChanged && this.handler != 0) {
            this.handler.post(new C02472());
        }
    }

    private ArrayList<SharingLocationInfo> getInfos() {
        ArrayList<SharingLocationInfo> arrayList = new ArrayList();
        for (int i = 0; i < 3; i++) {
            Collection collection = LocationController.getInstance(i).sharingLocationsUI;
            if (!collection.isEmpty()) {
                arrayList.addAll(collection);
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
                    firstName = chat != null ? chat.title : TtmlNode.ANONYMOUS_REGION_ID;
                }
            } else {
                firstName = LocaleController.formatPluralString("Chats", infos.size());
            }
            CharSequence format = String.format(LocaleController.getString("AttachLiveLocationIsSharing", C0446R.string.AttachLiveLocationIsSharing), new Object[]{LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation), firstName});
            this.builder.setTicker(format);
            this.builder.setContentText(format);
            if (z) {
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (getInfos().isEmpty() != null) {
            stopSelf();
        }
        if (this.builder == null) {
            intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("org.tmessages.openlocations");
            intent.setFlags(32768);
            intent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
            this.builder = new Builder(ApplicationLoader.applicationContext);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(C0446R.drawable.live_loc);
            this.builder.setContentIntent(intent);
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            this.builder.addAction(0, LocaleController.getString("StopLiveLocation", C0446R.string.StopLiveLocation), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, new Intent(ApplicationLoader.applicationContext, StopLiveLocationReceiver.class), 134217728));
        }
        updateNotification(false);
        startForeground(6, this.builder.build());
        return 2;
    }
}
