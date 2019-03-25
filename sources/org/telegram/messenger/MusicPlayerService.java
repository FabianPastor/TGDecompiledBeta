package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.Notification.MediaStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.File;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    private static boolean supportBigNotifications;
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    };
    private ImageReceiver imageReceiver;
    private String loadingFilePath;
    private MediaSession mediaSession;
    private int notificationMessageID;
    private Builder playbackState;
    private RemoteControlClient remoteControlClient;

    static {
        boolean z;
        boolean z2 = false;
        if (VERSION.SDK_INT >= 16) {
            z = true;
        } else {
            z = false;
        }
        supportBigNotifications = z;
        if (VERSION.SDK_INT < 21 || !TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.miui.ui.version.code"))) {
            z2 = true;
        }
        supportLockScreenControls = z2;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService("audio");
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad);
        }
        this.imageReceiver = new ImageReceiver(null);
        this.imageReceiver.setDelegate(new MusicPlayerService$$Lambda$0(this));
        if (VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Config.ARGB_8888);
            Drawable placeholder = getResources().getDrawable(NUM);
            placeholder.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            placeholder.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new Callback() {
                public void onPlay() {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onPause() {
                    MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onSkipToNext() {
                    MediaController.getInstance().playNextMessage();
                }

                public void onSkipToPrevious() {
                    MediaController.getInstance().playPreviousMessage();
                }

                public void onStop() {
                }
            });
            this.mediaSession.setActive(true);
        }
        registerReceiver(this.headsetPlugReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        super.onCreate();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onCreate$0$MusicPlayerService(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (set && !TextUtils.isEmpty(this.loadingFilePath)) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, true);
            }
            this.loadingFilePath = null;
        }
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                if ((getPackageName() + ".STOP_PLAYER").equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                ThrowableExtension.printStackTrace(e);
                return 1;
            }
        }
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject == null) {
            AndroidUtilities.runOnUIThread(new MusicPlayerService$$Lambda$1(this));
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
                this.remoteControlClient.setTransportControlFlags(189);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        createNotification(messageObject, false);
        return 1;
    }

    private Bitmap loadArtworkFromUrl(String artworkUrl, boolean big, boolean tryLoad) {
        float f = 600.0f;
        String name = ImageLoader.getHttpFileName(artworkUrl);
        File path = ImageLoader.getHttpFilePath(artworkUrl, "jpg");
        if (path.exists()) {
            String absolutePath = path.getAbsolutePath();
            float f2 = big ? 600.0f : 100.0f;
            if (!big) {
                f = 100.0f;
            }
            return ImageLoader.loadBitmap(absolutePath, null, f2, f, false);
        } else if (tryLoad) {
            this.loadingFilePath = path.getAbsolutePath();
            if (big) {
                return null;
            }
            this.imageReceiver.setImage(artworkUrl, "48_48", null, null, 0);
            return null;
        } else {
            this.loadingFilePath = null;
            return null;
        }
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject, boolean forBitmap) {
        String songName = messageObject.getMusicTitle();
        String authorName = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        String artworkUrl = messageObject.getArtworkUrl(true);
        String artworkUrlBig = messageObject.getArtworkUrl(false);
        Bitmap albumArt = audioInfo != null ? audioInfo.getSmallCover() : null;
        Bitmap fullAlbumArt = audioInfo != null ? audioInfo.getCover() : null;
        this.loadingFilePath = null;
        this.imageReceiver.setImageBitmap((BitmapDrawable) null);
        if (albumArt != null || TextUtils.isEmpty(artworkUrl)) {
            this.loadingFilePath = FileLoader.getPathToAttach(messageObject.getDocument()).getAbsolutePath();
        } else {
            fullAlbumArt = loadArtworkFromUrl(artworkUrlBig, true, !forBitmap);
            if (fullAlbumArt == null) {
                albumArt = loadArtworkFromUrl(artworkUrl, false, !forBitmap);
                fullAlbumArt = albumArt;
            } else {
                albumArt = loadArtworkFromUrl(artworkUrlBig, false, !forBitmap);
            }
        }
        Notification notification;
        if (VERSION.SDK_INT >= 21) {
            boolean isPlaying = !MediaController.getInstance().isMessagePaused();
            PendingIntent pendingPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            PendingIntent pendingStop = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, getClass()).setAction(getPackageName() + ".STOP_PLAYER"), NUM);
            PendingIntent pendingPlaypause = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(isPlaying ? "org.telegram.android.musicplayer.pause" : "org.telegram.android.musicplayer.play").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            PendingIntent pendingNext = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            Notification.Builder bldr = new Notification.Builder(this);
            int[] iArr = new int[3];
            bldr.setSmallIcon(NUM).setOngoing(isPlaying).setContentTitle(songName).setContentText(authorName).setSubText(audioInfo != null ? audioInfo.getAlbum() : null).setContentIntent(contentIntent).setDeleteIntent(pendingStop).setShowWhen(false).setCategory("transport").setPriority(2).setStyle(new MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(new int[]{0, 1, 2}));
            if (VERSION.SDK_INT >= 26) {
                NotificationsController.checkOtherNotificationsChannel();
                bldr.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (albumArt != null) {
                bldr.setLargeIcon(albumArt);
            } else {
                bldr.setLargeIcon(this.albumArtPlaceholder);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.playbackState.setState(6, 0, 1.0f).setActions(0);
                bldr.addAction(new Action.Builder(NUM, "", pendingPrev).build()).addAction(new Action.Builder(NUM, "", null).build()).addAction(new Action.Builder(NUM, "", pendingNext).build());
            } else {
                this.playbackState.setState(isPlaying ? 3 : 2, ((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, isPlaying ? 1.0f : 0.0f).setActions(566);
                bldr.addAction(new Action.Builder(NUM, "", pendingPrev).build()).addAction(new Action.Builder(isPlaying ? NUM : NUM, "", pendingPlaypause).build()).addAction(new Action.Builder(NUM, "", pendingNext).build());
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
            this.mediaSession.setMetadata(new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", fullAlbumArt).putString("android.media.metadata.ALBUM_ARTIST", authorName).putString("android.media.metadata.TITLE", songName).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : null).build());
            bldr.setVisibility(1);
            notification = bldr.build();
            if (isPlaying) {
                startForeground(5, notification);
            } else {
                stopForeground(false);
                ((NotificationManager) getSystemService("notification")).notify(5, notification);
            }
        } else {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), NUM);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), NUM);
            }
            notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(NUM).setContentIntent(contentIntent).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(songName).build();
            notification.contentView = remoteViews;
            if (supportBigNotifications) {
                notification.bigContentView = expandedView;
            }
            setListeners(remoteViews);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }
            if (albumArt != null) {
                notification.contentView.setImageViewBitmap(NUM, albumArt);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewBitmap(NUM, albumArt);
                }
            } else {
                notification.contentView.setImageViewResource(NUM, NUM);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewResource(NUM, NUM);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                notification.contentView.setViewVisibility(NUM, 8);
                notification.contentView.setViewVisibility(NUM, 8);
                notification.contentView.setViewVisibility(NUM, 8);
                notification.contentView.setViewVisibility(NUM, 8);
                notification.contentView.setViewVisibility(NUM, 0);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(NUM, 8);
                    notification.bigContentView.setViewVisibility(NUM, 8);
                    notification.bigContentView.setViewVisibility(NUM, 8);
                    notification.bigContentView.setViewVisibility(NUM, 8);
                    notification.bigContentView.setViewVisibility(NUM, 0);
                }
            } else {
                notification.contentView.setViewVisibility(NUM, 8);
                notification.contentView.setViewVisibility(NUM, 0);
                notification.contentView.setViewVisibility(NUM, 0);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(NUM, 0);
                    notification.bigContentView.setViewVisibility(NUM, 0);
                    notification.bigContentView.setViewVisibility(NUM, 8);
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    notification.contentView.setViewVisibility(NUM, 8);
                    notification.contentView.setViewVisibility(NUM, 0);
                    if (supportBigNotifications) {
                        notification.bigContentView.setViewVisibility(NUM, 8);
                        notification.bigContentView.setViewVisibility(NUM, 0);
                    }
                } else {
                    notification.contentView.setViewVisibility(NUM, 0);
                    notification.contentView.setViewVisibility(NUM, 8);
                    if (supportBigNotifications) {
                        notification.bigContentView.setViewVisibility(NUM, 0);
                        notification.bigContentView.setViewVisibility(NUM, 8);
                    }
                }
            }
            notification.contentView.setTextViewText(NUM, songName);
            notification.contentView.setTextViewText(NUM, authorName);
            if (supportBigNotifications) {
                notification.bigContentView.setTextViewText(NUM, songName);
                notification.bigContentView.setTextViewText(NUM, authorName);
                RemoteViews remoteViews2 = notification.bigContentView;
                String album = (audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum())) ? "" : audioInfo.getAlbum();
                remoteViews2.setTextViewText(NUM, album);
            }
            notification.flags |= 2;
            startForeground(5, notification);
        }
        if (this.remoteControlClient != null) {
            MetadataEditor metadataEditor;
            int currentID = MediaController.getInstance().getPlayingMessageObject().getId();
            if (this.notificationMessageID != currentID) {
                this.notificationMessageID = currentID;
                metadataEditor = this.remoteControlClient.editMetadata(true);
                metadataEditor.putString(2, authorName);
                metadataEditor.putString(7, songName);
                if (!(audioInfo == null || TextUtils.isEmpty(audioInfo.getAlbum()))) {
                    metadataEditor.putString(1, audioInfo.getAlbum());
                }
                metadataEditor.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                if (fullAlbumArt != null) {
                    try {
                        metadataEditor.putBitmap(100, fullAlbumArt);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                metadataEditor.apply();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int i = 2;
                        if (MusicPlayerService.this.remoteControlClient != null && MediaController.getInstance().getPlayingMessageObject() != null) {
                            if (((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) == -9223372036854775807L) {
                                AndroidUtilities.runOnUIThread(this, 500);
                                return;
                            }
                            MetadataEditor metadataEditor = MusicPlayerService.this.remoteControlClient.editMetadata(false);
                            metadataEditor.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                            metadataEditor.apply();
                            RemoteControlClient access$000;
                            if (VERSION.SDK_INT >= 18) {
                                access$000 = MusicPlayerService.this.remoteControlClient;
                                if (!MediaController.getInstance().isMessagePaused()) {
                                    i = 3;
                                }
                                access$000.setPlaybackState(i, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                                return;
                            }
                            access$000 = MusicPlayerService.this.remoteControlClient;
                            if (!MediaController.getInstance().isMessagePaused()) {
                                i = 3;
                            }
                            access$000.setPlaybackState(i);
                        }
                    }
                }, 1000);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.remoteControlClient.setPlaybackState(8);
                return;
            }
            metadataEditor = this.remoteControlClient.editMetadata(false);
            metadataEditor.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
            metadataEditor.apply();
            if (VERSION.SDK_INT >= 18) {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            } else {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3);
            }
        }
    }

    public void setListeners(RemoteViews view) {
        view.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous"), NUM));
        view.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.close"), NUM));
        view.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.pause"), NUM));
        view.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next"), NUM));
        view.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.play"), NUM));
    }

    @SuppressLint({"NewApi"})
    public void onDestroy() {
        unregisterReceiver(this.headsetPlugReceiver);
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
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileDidLoad);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MessageObject messageObject;
        String path;
        if (id == NotificationCenter.messagePlayingPlayStateChanged) {
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, false);
            } else {
                stopSelf();
            }
        } else if (id == NotificationCenter.messagePlayingDidSeek) {
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.remoteControlClient != null && VERSION.SDK_INT >= 18) {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, ((long) Math.round(((Float) args[1]).floatValue() * ((float) messageObject.audioPlayerDuration))) * 1000, MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            }
        } else if (id == NotificationCenter.httpFileDidLoad) {
            path = args[0];
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null && this.loadingFilePath != null && this.loadingFilePath.equals(path)) {
                createNotification(messageObject, false);
            }
        } else if (id == NotificationCenter.fileDidLoad) {
            path = (String) args[0];
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null && this.loadingFilePath != null && this.loadingFilePath.equals(path)) {
                createNotification(messageObject, false);
            }
        }
    }
}
