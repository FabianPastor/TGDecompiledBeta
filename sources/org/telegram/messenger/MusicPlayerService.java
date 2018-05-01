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
        public void onStop() {
        }

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
    }

    /* renamed from: org.telegram.messenger.MusicPlayerService$2 */
    class C04112 implements Runnable {
        C04112() {
        }

        public void run() {
            MusicPlayerService.this.stopSelf();
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        boolean z = false;
        if (VERSION.SDK_INT < 21) {
            z = true;
        }
        supportLockScreenControls = z;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        }
        if (VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Config.ARGB_8888);
            Drawable drawable = getResources().getDrawable(C0446R.drawable.nocover_big);
            drawable.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            drawable.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new C04101());
            this.mediaSession.setActive(true);
        }
        super.onCreate();
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            try {
                i2 = new StringBuilder();
                i2.append(getPackageName());
                i2.append(".STOP_PLAYER");
                if (i2.toString().equals(intent.getAction()) != null) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Intent intent2) {
                intent2.printStackTrace();
            }
        }
        intent2 = MediaController.getInstance().getPlayingMessageObject();
        if (intent2 == null) {
            AndroidUtilities.runOnUIThread(new C04112());
            return 1;
        }
        if (supportLockScreenControls != 0) {
            i2 = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
            try {
                if (this.remoteControlClient == null) {
                    this.audioManager.registerMediaButtonEventReceiver(i2);
                    Intent intent3 = new Intent("android.intent.action.MEDIA_BUTTON");
                    intent3.setComponent(i2);
                    this.remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, intent3, 0));
                    this.audioManager.registerRemoteControlClient(this.remoteControlClient);
                }
                this.remoteControlClient.setTransportControlFlags(PsExtractor.PRIVATE_STREAM_1);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        createNotification(intent2);
        return 1;
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject) {
        String str;
        String str2;
        int i;
        Context context = this;
        CharSequence musicTitle = messageObject.getMusicTitle();
        CharSequence musicAuthor = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.setFlags(32768);
        PendingIntent activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        if (VERSION.SDK_INT >= 21) {
            PendingIntent pendingIntent;
            CharSequence charSequence;
            CharSequence charSequence2;
            Bitmap smallCover = audioInfo != null ? audioInfo.getSmallCover() : null;
            Bitmap cover = audioInfo != null ? audioInfo.getCover() : null;
            boolean isMessagePaused = MediaController.getInstance().isMessagePaused() ^ true;
            PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            Context applicationContext = getApplicationContext();
            Intent intent2 = new Intent(context, getClass());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getPackageName());
            stringBuilder.append(".STOP_PLAYER");
            PendingIntent service = PendingIntent.getService(applicationContext, 0, intent2.setAction(stringBuilder.toString()), 268435456);
            PendingIntent broadcast2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(isMessagePaused ? NOTIFY_PAUSE : NOTIFY_PLAY).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            PendingIntent broadcast3 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT).setComponent(new ComponentName(context, MusicPlayerReceiver.class)), 268435456);
            Notification.Builder builder = new Notification.Builder(context);
            int i2 = 3;
            builder.setSmallIcon(C0446R.drawable.player).setOngoing(isMessagePaused).setContentTitle(musicTitle).setContentText(musicAuthor).setSubText(audioInfo != null ? audioInfo.getAlbum() : null).setContentIntent(activity).setDeleteIntent(service).setShowWhen(false).setCategory("transport").setPriority(2).setStyle(new MediaStyle().setMediaSession(context.mediaSession.getSessionToken()).setShowActionsInCompactView(new int[]{0, 1, 2}));
            if (VERSION.SDK_INT >= 26) {
                builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (smallCover != null) {
                builder.setLargeIcon(smallCover);
            } else {
                builder.setLargeIcon(context.albumArtPlaceholder);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                context.playbackState.setState(6, 0, 1.0f).setActions(0);
                pendingIntent = null;
                builder.addAction(new Action.Builder(C0446R.drawable.ic_action_previous, TtmlNode.ANONYMOUS_REGION_ID, broadcast).build()).addAction(new Action.Builder(C0446R.drawable.loading_animation2, TtmlNode.ANONYMOUS_REGION_ID, null).build()).addAction(new Action.Builder(C0446R.drawable.ic_action_next, TtmlNode.ANONYMOUS_REGION_ID, broadcast3).build());
                charSequence = musicTitle;
                charSequence2 = musicAuthor;
            } else {
                pendingIntent = null;
                Builder builder2 = context.playbackState;
                if (!isMessagePaused) {
                    i2 = 2;
                }
                charSequence = musicTitle;
                charSequence2 = musicAuthor;
                builder2.setState(i2, ((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, isMessagePaused ? 1.0f : 0.0f).setActions(566);
                builder.addAction(new Action.Builder(C0446R.drawable.ic_action_previous, TtmlNode.ANONYMOUS_REGION_ID, broadcast).build()).addAction(new Action.Builder(isMessagePaused ? C0446R.drawable.ic_action_pause : C0446R.drawable.ic_action_play, TtmlNode.ANONYMOUS_REGION_ID, broadcast2).build()).addAction(new Action.Builder(C0446R.drawable.ic_action_next, TtmlNode.ANONYMOUS_REGION_ID, broadcast3).build());
            }
            context.mediaSession.setPlaybackState(context.playbackState.build());
            str = charSequence2;
            str2 = charSequence;
            context.mediaSession.setMetadata(new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", cover).putString("android.media.metadata.ALBUM_ARTIST", str).putString("android.media.metadata.TITLE", str2).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : pendingIntent).build());
            builder.setVisibility(1);
            Notification build = builder.build();
            if (isMessagePaused) {
                startForeground(5, build);
            } else {
                stopForeground(false);
                ((NotificationManager) getSystemService("notification")).notify(5, build);
            }
            i = 2;
        } else {
            str2 = musicTitle;
            str = musicAuthor;
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), C0446R.layout.player_small_notification);
            RemoteViews remoteViews2 = supportBigNotifications ? new RemoteViews(getApplicationContext().getPackageName(), C0446R.layout.player_big_notification) : null;
            Notification build2 = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(C0446R.drawable.player).setContentIntent(activity).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(str2).build();
            build2.contentView = remoteViews;
            if (supportBigNotifications) {
                build2.bigContentView = remoteViews2;
            }
            setListeners(remoteViews);
            if (supportBigNotifications) {
                setListeners(remoteViews2);
            }
            Bitmap smallCover2 = audioInfo != null ? audioInfo.getSmallCover() : null;
            if (smallCover2 != null) {
                build2.contentView.setImageViewBitmap(C0446R.id.player_album_art, smallCover2);
                if (supportBigNotifications) {
                    build2.bigContentView.setImageViewBitmap(C0446R.id.player_album_art, smallCover2);
                }
            } else {
                build2.contentView.setImageViewResource(C0446R.id.player_album_art, C0446R.drawable.nocover_small);
                if (supportBigNotifications) {
                    build2.bigContentView.setImageViewResource(C0446R.id.player_album_art, C0446R.drawable.nocover_big);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                build2.contentView.setViewVisibility(C0446R.id.player_pause, 8);
                build2.contentView.setViewVisibility(C0446R.id.player_play, 8);
                build2.contentView.setViewVisibility(C0446R.id.player_next, 8);
                build2.contentView.setViewVisibility(C0446R.id.player_previous, 8);
                build2.contentView.setViewVisibility(C0446R.id.player_progress_bar, 0);
                if (supportBigNotifications) {
                    build2.bigContentView.setViewVisibility(C0446R.id.player_pause, 8);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_play, 8);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_next, 8);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_previous, 8);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_progress_bar, 0);
                }
            } else {
                build2.contentView.setViewVisibility(C0446R.id.player_progress_bar, 8);
                build2.contentView.setViewVisibility(C0446R.id.player_next, 0);
                build2.contentView.setViewVisibility(C0446R.id.player_previous, 0);
                if (supportBigNotifications) {
                    build2.bigContentView.setViewVisibility(C0446R.id.player_next, 0);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_previous, 0);
                    build2.bigContentView.setViewVisibility(C0446R.id.player_progress_bar, 8);
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    build2.contentView.setViewVisibility(C0446R.id.player_pause, 8);
                    build2.contentView.setViewVisibility(C0446R.id.player_play, 0);
                    if (supportBigNotifications) {
                        build2.bigContentView.setViewVisibility(C0446R.id.player_pause, 8);
                        build2.bigContentView.setViewVisibility(C0446R.id.player_play, 0);
                    }
                } else {
                    build2.contentView.setViewVisibility(C0446R.id.player_pause, 0);
                    build2.contentView.setViewVisibility(C0446R.id.player_play, 8);
                    if (supportBigNotifications) {
                        build2.bigContentView.setViewVisibility(C0446R.id.player_pause, 0);
                        build2.bigContentView.setViewVisibility(C0446R.id.player_play, 8);
                    }
                }
            }
            build2.contentView.setTextViewText(C0446R.id.player_song_name, str2);
            build2.contentView.setTextViewText(C0446R.id.player_author_name, str);
            if (supportBigNotifications) {
                build2.bigContentView.setTextViewText(C0446R.id.player_song_name, str2);
                build2.bigContentView.setTextViewText(C0446R.id.player_author_name, str);
                remoteViews = build2.bigContentView;
                CharSequence album = (audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum())) ? TtmlNode.ANONYMOUS_REGION_ID : audioInfo.getAlbum();
                remoteViews.setTextViewText(C0446R.id.player_album_title, album);
            }
            i = 2;
            build2.flags |= 2;
            startForeground(5, build2);
        }
        if (context.remoteControlClient != null) {
            MetadataEditor editMetadata = context.remoteControlClient.editMetadata(true);
            editMetadata.putString(i, str);
            editMetadata.putString(7, str2);
            if (!(audioInfo == null || audioInfo.getCover() == null)) {
                try {
                    editMetadata.putBitmap(100, audioInfo.getCover());
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
            editMetadata.apply();
        }
    }

    public void setListeners(RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(C0446R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), 134217728));
        remoteViews.setOnClickPendingIntent(C0446R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), 134217728));
        remoteViews.setOnClickPendingIntent(C0446R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), 134217728));
        remoteViews.setOnClickPendingIntent(C0446R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), 134217728));
        remoteViews.setOnClickPendingIntent(C0446R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), 134217728));
    }

    @SuppressLint({"NewApi"})
    public void onDestroy() {
        super.onDestroy();
        if (this.remoteControlClient != null) {
            MetadataEditor editMetadata = this.remoteControlClient.editMetadata(true);
            editMetadata.clear();
            editMetadata.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        if (VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagePlayingPlayStateChanged) {
            i = MediaController.getInstance().getPlayingMessageObject();
            if (i != 0) {
                createNotification(i);
            } else {
                stopSelf();
            }
        }
    }
}
