package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import java.util.ArrayList;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.LaunchActivity;

public class LocationSharingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
    private Handler handler;
    private Runnable runnable;

    public LocationSharingService() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        this.runnable = new Runnable() {
            public void run() {
                LocationSharingService.this.handler.postDelayed(LocationSharingService.this.runnable, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        LocationController.getInstance().update();
                    }
                });
            }
        };
        this.handler.postDelayed(this.runnable, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    }

    public IBinder onBind(Intent arg2) {
        return null;
    }

    public void onDestroy() {
        if (this.handler != null) {
            this.handler.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.liveLocationsChanged && this.handler != null) {
            this.handler.post(new Runnable() {
                public void run() {
                    if (LocationController.getInstance().sharingLocationsUI.isEmpty()) {
                        LocationSharingService.this.stopSelf();
                    } else {
                        LocationSharingService.this.updateNotification();
                    }
                }
            });
        }
    }

    private void updateNotification() {
        if (this.builder != null) {
            String param;
            ArrayList<SharingLocationInfo> infos = LocationController.getInstance().sharingLocationsUI;
            if (infos.size() == 1) {
                int lower_id = (int) ((SharingLocationInfo) infos.get(0)).messageObject.getDialogId();
                if (lower_id > 0) {
                    param = UserObject.getFirstName(MessagesController.getInstance().getUser(Integer.valueOf(lower_id)));
                } else {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                }
            } else {
                param = LocaleController.formatPluralString("Chats", LocationController.getInstance().sharingLocationsUI.size());
            }
            String str = String.format(LocaleController.getString("AttachLiveLocationIsSharing", R.string.AttachLiveLocationIsSharing), new Object[]{LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), param});
            this.builder.setTicker(str);
            this.builder.setContentText(str);
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (LocationController.getInstance().sharingLocationsUI.isEmpty()) {
            stopSelf();
        }
        if (this.builder == null) {
            Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent2.setAction("org.tmessages.openlocations");
            intent2.setFlags(32768);
            PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, 0);
            this.builder = new Builder(ApplicationLoader.applicationContext);
            this.builder.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(R.drawable.notification);
            this.builder.setContentIntent(contentIntent);
            this.builder.setContentTitle(LocaleController.getString("AppName", R.string.AppName));
            this.builder.addAction(0, LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, new Intent(ApplicationLoader.applicationContext, StopLiveLocationReceiver.class), 134217728));
        }
        startForeground(6, this.builder.build());
        updateNotification();
        return 2;
    }
}
