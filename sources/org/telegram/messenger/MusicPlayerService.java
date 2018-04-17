package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.MediaStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.PlaybackState.Builder;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    private static boolean supportBigNotifications = (VERSION.SDK_INT >= 16);
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private MediaSession mediaSession;
    private Builder playbackState;
    private RemoteControlClient remoteControlClient;

    /* renamed from: org.telegram.messenger.MusicPlayerService$1 */
    class C04101 extends Callback {
        C04101() {
        }

        public void onPlay() {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        }

        public void onPause() {
            MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        }

        public void onSkipToNext() {
            MediaController.getInstance().playNextMessage();
        }

        public void onSkipToPrevious() {
            MediaController.getInstance().playPreviousMessage();
        }

        public void onStop() {
        }
    }

    /* renamed from: org.telegram.messenger.MusicPlayerService$2 */
    class C04112 implements Runnable {
        C04112() {
        }

        public void run() {
            MusicPlayerService.this.stopSelf();
        }
    }

    static {
        boolean z = false;
        if (VERSION.SDK_INT < 21) {
            z = true;
        }
        supportLockScreenControls = z;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        }
        if (VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Config.ARGB_8888);
            Drawable placeholder = getResources().getDrawable(R.drawable.nocover_big);
            placeholder.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            placeholder.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new C04101());
            this.mediaSession.setActive(true);
        }
        super.onCreate();
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getPackageName());
                stringBuilder.append(".STOP_PLAYER");
                if (stringBuilder.toString().equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Exception e2 = MediaController.getInstance().getPlayingMessageObject();
        if (e2 == null) {
            AndroidUtilities.runOnUIThread(new C04112());
            return 1;
        }
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
            } catch (Throwable e3) {
                FileLog.m3e(e3);
            }
        }
        createNotification(e2);
        return 1;
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject) {
        Context context = this;
        String songName = messageObject.getMusicTitle();
        String authorName = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.setFlags(32768);
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        PendingIntent contentIntent2;
        Notification notification;
        if (VERSION.SDK_INT >= 21) {
            Bitmap albumArt = audioInfo != null ? audioInfo.getSmallCover() : null;
            Bitmap fullAlbumArt = audioInfo != null ? audioInfo.getCover() : null;
            boolean isPlaying = MediaController.getInstance().isMessagePaused() ^ true;
            PendingIntent pendingPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            Context applicationContext = getApplicationContext();
            Intent intent2 = new Intent(context, getClass());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getPackageName());
            stringBuilder.append(".STOP_PLAYER");
            PendingIntent pendingStop = PendingIntent.getService(applicationContext, 0, intent2.setAction(stringBuilder.toString()), 268435456);
            PendingIntent pendingPlaypause = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(isPlaying ? NOTIFY_PAUSE : NOTIFY_PLAY).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            intent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            Notification.Builder bldr = new Notification.Builder(context);
            Notification.Builder priority = bldr.setSmallIcon(R.drawable.player).setOngoing(isPlaying).setContentTitle(songName).setContentText(authorName).setSubText(audioInfo != null ? audioInfo.getAlbum() : null).setContentIntent(contentIntent).setDeleteIntent(pendingStop).setShowWhen(false).setCategory("transport").setPriority(2);
            MediaStyle mediaSession = new MediaStyle().setMediaSession(context.mediaSession.getSessionToken());
            int i = 3;
            contentIntent2 = contentIntent;
            priority.setStyle(mediaSession.setShowActionsInCompactView(new int[]{0, 1, 2}));
            if (VERSION.SDK_INT >= 26) {
                bldr.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (albumArt != null) {
                bldr.setLargeIcon(albumArt);
            } else {
                bldr.setLargeIcon(context.albumArtPlaceholder);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                context.playbackState.setState(6, 0, 1.0f).setActions(0);
                bldr.addAction(new Action.Builder(R.drawable.ic_action_previous, TtmlNode.ANONYMOUS_REGION_ID, pendingPrev).build()).addAction(new Action.Builder(R.drawable.loading_animation2, TtmlNode.ANONYMOUS_REGION_ID, null).build()).addAction(new Action.Builder(R.drawable.ic_action_next, TtmlNode.ANONYMOUS_REGION_ID, intent).build());
            } else {
                Builder builder = context.playbackState;
                if (!isPlaying) {
                    i = 2;
                }
                builder.setState(i, ((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, isPlaying ? 1.0f : 0.0f).setActions(566);
                bldr.addAction(new Action.Builder(R.drawable.ic_action_previous, TtmlNode.ANONYMOUS_REGION_ID, pendingPrev).build()).addAction(new Action.Builder(isPlaying ? R.drawable.ic_action_pause : R.drawable.ic_action_play, TtmlNode.ANONYMOUS_REGION_ID, pendingPlaypause).build()).addAction(new Action.Builder(R.drawable.ic_action_next, TtmlNode.ANONYMOUS_REGION_ID, intent).build());
            }
            context.mediaSession.setPlaybackState(context.playbackState.build());
            context.mediaSession.setMetadata(new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", fullAlbumArt).putString("android.media.metadata.ALBUM_ARTIST", authorName).putString("android.media.metadata.TITLE", songName).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : null).build());
            bldr.setVisibility(1);
            notification = bldr.build();
            if (isPlaying) {
                startForeground(5, notification);
            } else {
                stopForeground(false);
                ((NotificationManager) getSystemService("notification")).notify(5, notification);
            }
            PendingIntent pendingIntent = contentIntent2;
        } else {
            contentIntent2 = contentIntent;
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification);
            }
            notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.player).setContentIntent(contentIntent2).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(songName).build();
            notification.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification.bigContentView = expandedView;
            }
            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }
            Bitmap albumArt2 = audioInfo != null ? audioInfo.getSmallCover() : null;
            if (albumArt2 != null) {
                notification.contentView.setImageViewBitmap(R.id.player_album_art, albumArt2);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt2);
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
                if (MediaController.getInstance().isMessagePaused()) {
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
                RemoteViews remoteViews = notification.bigContentView;
                CharSequence album = (audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum())) ? TtmlNode.ANONYMOUS_REGION_ID : audioInfo.getAlbum();
                remoteViews.setTextViewText(R.id.player_album_title, album);
            }
            notification.flags |= 2;
            startForeground(5, notification);
        }
        if (context.remoteControlClient != null) {
            MetadataEditor metadataEditor = context.remoteControlClient.editMetadata(true);
            metadataEditor.putString(2, authorName);
            metadataEditor.putString(7, songName);
            if (!(audioInfo == null || audioInfo.getCover() == null)) {
                try {
                    metadataEditor.putBitmap(100, audioInfo.getCover());
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
            metadataEditor.apply();
        }
    }

    public void setListeners(RemoteViews view) {
        view.setOnClickPendingIntent(R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), 134217728));
        view.setOnClickPendingIntent(R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), 134217728));
        view.setOnClickPendingIntent(R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), 134217728));
        view.setOnClickPendingIntent(R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), 134217728));
        view.setOnClickPendingIntent(R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), 134217728));
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
        if (VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagePlayingPlayStateChanged) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject);
            } else {
                stopSelf();
            }
        }
    }
}
