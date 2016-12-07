package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenterDelegate {
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    private static boolean supportBigNotifications;
    private static boolean supportLockScreenControls;
    private AudioManager audioManager;
    private RemoteControlClient remoteControlClient;

    static {
        boolean z;
        boolean z2 = true;
        if (VERSION.SDK_INT >= 16) {
            z = true;
        } else {
            z = false;
        }
        supportBigNotifications = z;
        if (VERSION.SDK_INT < 14) {
            z2 = false;
        }
        supportLockScreenControls = z2;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        super.onCreate();
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject == null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MusicPlayerService.this.stopSelf();
                    }
                });
            } else {
                if (supportLockScreenControls) {
                    ComponentName remoteComponentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
                    try {
                        if (this.remoteControlClient == null) {
                            this.audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                            Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
                            mediaButtonIntent.setComponent(remoteComponentName);
                            this.remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0));
                            this.audioManager.registerRemoteControlClient(this.remoteControlClient);
                        }
                        this.remoteControlClient.setTransportControlFlags(PsExtractor.PRIVATE_STREAM_1);
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
                createNotification(messageObject);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return 1;
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject) {
        String songName = messageObject.getMusicTitle();
        String authorName = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification);
        RemoteViews expandedView = null;
        if (supportBigNotifications) {
            expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification);
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.setFlags(32768);
        Notification notification = new Builder(getApplicationContext()).setSmallIcon(R.drawable.player).setContentIntent(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0)).setContentTitle(songName).build();
        notification.contentView = simpleContentView;
        if (supportBigNotifications) {
            notification.bigContentView = expandedView;
        }
        setListeners(simpleContentView);
        if (supportBigNotifications) {
            setListeners(expandedView);
        }
        Bitmap albumArt = audioInfo != null ? audioInfo.getSmallCover() : null;
        if (albumArt != null) {
            notification.contentView.setImageViewBitmap(R.id.player_album_art, albumArt);
            if (supportBigNotifications) {
                notification.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt);
            }
        } else {
            notification.contentView.setImageViewResource(R.id.player_album_art, R.drawable.nocover_small);
            if (supportBigNotifications) {
                notification.bigContentView.setImageViewResource(R.id.player_album_art, R.drawable.nocover_big);
            }
        }
        if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            notification.contentView.setViewVisibility(R.id.player_pause, 8);
            notification.contentView.setViewVisibility(R.id.player_play, 8);
            notification.contentView.setViewVisibility(R.id.player_next, 8);
            notification.contentView.setViewVisibility(R.id.player_previous, 8);
            notification.contentView.setViewVisibility(R.id.player_progress_bar, 0);
            if (supportBigNotifications) {
                notification.bigContentView.setViewVisibility(R.id.player_pause, 8);
                notification.bigContentView.setViewVisibility(R.id.player_play, 8);
                notification.bigContentView.setViewVisibility(R.id.player_next, 8);
                notification.bigContentView.setViewVisibility(R.id.player_previous, 8);
                notification.bigContentView.setViewVisibility(R.id.player_progress_bar, 0);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.player_progress_bar, 8);
            notification.contentView.setViewVisibility(R.id.player_next, 0);
            notification.contentView.setViewVisibility(R.id.player_previous, 0);
            if (supportBigNotifications) {
                notification.bigContentView.setViewVisibility(R.id.player_next, 0);
                notification.bigContentView.setViewVisibility(R.id.player_previous, 0);
                notification.bigContentView.setViewVisibility(R.id.player_progress_bar, 8);
            }
            if (MediaController.getInstance().isAudioPaused()) {
                notification.contentView.setViewVisibility(R.id.player_pause, 8);
                notification.contentView.setViewVisibility(R.id.player_play, 0);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, 8);
                    notification.bigContentView.setViewVisibility(R.id.player_play, 0);
                }
            } else {
                notification.contentView.setViewVisibility(R.id.player_pause, 0);
                notification.contentView.setViewVisibility(R.id.player_play, 8);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, 0);
                    notification.bigContentView.setViewVisibility(R.id.player_play, 8);
                }
            }
        }
        notification.contentView.setTextViewText(R.id.player_song_name, songName);
        notification.contentView.setTextViewText(R.id.player_author_name, authorName);
        if (supportBigNotifications) {
            notification.bigContentView.setTextViewText(R.id.player_song_name, songName);
            notification.bigContentView.setTextViewText(R.id.player_author_name, authorName);
        }
        notification.flags |= 2;
        startForeground(5, notification);
        if (this.remoteControlClient != null) {
            MetadataEditor metadataEditor = this.remoteControlClient.editMetadata(true);
            metadataEditor.putString(2, authorName);
            metadataEditor.putString(7, songName);
            if (!(audioInfo == null || audioInfo.getCover() == null)) {
                try {
                    metadataEditor.putBitmap(100, audioInfo.getCover());
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
            metadataEditor.apply();
        }
    }

    public void setListeners(RemoteViews view) {
        view.setOnClickPendingIntent(R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), C.SAMPLE_FLAG_DECODE_ONLY));
        view.setOnClickPendingIntent(R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), C.SAMPLE_FLAG_DECODE_ONLY));
        view.setOnClickPendingIntent(R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), C.SAMPLE_FLAG_DECODE_ONLY));
        view.setOnClickPendingIntent(R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), C.SAMPLE_FLAG_DECODE_ONLY));
        view.setOnClickPendingIntent(R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), C.SAMPLE_FLAG_DECODE_ONLY));
    }

    @SuppressLint({"NewApi"})
    public void onDestroy() {
        super.onDestroy();
        if (this.remoteControlClient != null) {
            MetadataEditor metadataEditor = this.remoteControlClient.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.audioPlayStateChanged) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject);
            } else {
                stopSelf();
            }
        }
    }
}
