package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import java.io.File;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
    public static final String NOTIFY_SEEK = "org.telegram.android.musicplayer.seek";
    private static boolean supportBigNotifications = (Build.VERSION.SDK_INT >= 16);
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    };
    private ImageReceiver imageReceiver;
    private String loadingFilePath;
    private MediaSession mediaSession;
    private int notificationMessageID;
    private PlaybackState.Builder playbackState;
    /* access modifiers changed from: private */
    public RemoteControlClient remoteControlClient;

    public IBinder onBind(Intent intent) {
        return null;
    }

    static {
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 21 && TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.miui.ui.version.code"))) {
            z = false;
        }
        supportLockScreenControls = z;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService("audio");
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidLoad);
        }
        this.imageReceiver = new ImageReceiver((View) null);
        this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                MusicPlayerService.this.lambda$onCreate$0$MusicPlayerService(imageReceiver, z, z2);
            }

            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "telegramAudioPlayer");
            this.playbackState = new PlaybackState.Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Bitmap.Config.ARGB_8888);
            Drawable drawable = getResources().getDrawable(NUM);
            drawable.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            drawable.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new MediaSession.Callback() {
                public void onStop() {
                }

                public void onPlay() {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onPause() {
                    MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onSkipToNext() {
                    MediaController.getInstance().playNextMessage();
                }

                public void onSkipToPrevious() {
                    MediaController.getInstance().playPreviousMessage();
                }

                public void onSeekTo(long j) {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject != null) {
                        MediaController.getInstance().seekToProgress(playingMessageObject, ((float) (j / 1000)) / ((float) playingMessageObject.getDuration()));
                        MusicPlayerService.this.updatePlaybackState(j);
                    }
                }
            });
            this.mediaSession.setActive(true);
        }
        registerReceiver(this.headsetPlugReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        super.onCreate();
    }

    public /* synthetic */ void lambda$onCreate$0$MusicPlayerService(ImageReceiver imageReceiver2, boolean z, boolean z2) {
        if (z && !TextUtils.isEmpty(this.loadingFilePath)) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null) {
                createNotification(playingMessageObject, true);
            }
            this.loadingFilePath = null;
        }
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            try {
                if ((getPackageName() + ".STOP_PLAYER").equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MusicPlayerService.this.stopSelf();
                }
            });
            return 1;
        }
        if (supportLockScreenControls) {
            ComponentName componentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
            try {
                if (this.remoteControlClient == null) {
                    this.audioManager.registerMediaButtonEventReceiver(componentName);
                    Intent intent2 = new Intent("android.intent.action.MEDIA_BUTTON");
                    intent2.setComponent(componentName);
                    this.remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, intent2, 0));
                    this.audioManager.registerRemoteControlClient(this.remoteControlClient);
                }
                this.remoteControlClient.setTransportControlFlags(189);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        createNotification(playingMessageObject, false);
        return 1;
    }

    private Bitmap loadArtworkFromUrl(String str, boolean z, boolean z2) {
        ImageLoader.getHttpFileName(str);
        File httpFilePath = ImageLoader.getHttpFilePath(str, "jpg");
        if (httpFilePath.exists()) {
            String absolutePath = httpFilePath.getAbsolutePath();
            float f = 600.0f;
            float f2 = z ? 600.0f : 100.0f;
            if (!z) {
                f = 100.0f;
            }
            return ImageLoader.loadBitmap(absolutePath, (Uri) null, f2, f, false);
        }
        if (z2) {
            this.loadingFilePath = httpFilePath.getAbsolutePath();
            if (!z) {
                this.imageReceiver.setImage(str, "48_48", (Drawable) null, (String) null, 0);
            }
        } else {
            this.loadingFilePath = null;
        }
        return null;
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject, boolean z) {
        int i;
        int i2;
        MessageObject messageObject2 = messageObject;
        String musicTitle = messageObject.getMusicTitle();
        String musicAuthor = messageObject.getMusicAuthor();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        String artworkUrl = messageObject2.getArtworkUrl(true);
        String artworkUrl2 = messageObject2.getArtworkUrl(false);
        long duration = (long) (messageObject.getDuration() * 1000);
        Bitmap smallCover = audioInfo != null ? audioInfo.getSmallCover() : null;
        Bitmap cover = audioInfo != null ? audioInfo.getCover() : null;
        this.loadingFilePath = null;
        this.imageReceiver.setImageBitmap((Drawable) null);
        if (smallCover != null || TextUtils.isEmpty(artworkUrl)) {
            this.loadingFilePath = FileLoader.getPathToAttach(messageObject.getDocument()).getAbsolutePath();
        } else {
            cover = loadArtworkFromUrl(artworkUrl2, true, !z);
            if (cover == null) {
                smallCover = loadArtworkFromUrl(artworkUrl, false, !z);
                cover = smallCover;
            } else {
                smallCover = loadArtworkFromUrl(artworkUrl2, false, !z);
            }
        }
        Bitmap bitmap = smallCover;
        if (Build.VERSION.SDK_INT >= 21) {
            boolean z2 = !MediaController.getInstance().isMessagePaused();
            PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            PendingIntent service = PendingIntent.getService(getApplicationContext(), 0, new Intent(this, MusicPlayerService.class).setAction(getPackageName() + ".STOP_PLAYER"), NUM);
            PendingIntent broadcast2 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(z2 ? "org.telegram.android.musicplayer.pause" : "org.telegram.android.musicplayer.play").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            long j = duration;
            PendingIntent broadcast3 = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.seek").setComponent(new ComponentName(this, MusicPlayerReceiver.class)), NUM);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(NUM).setOngoing(z2).setContentTitle(musicTitle).setContentText(musicAuthor).setSubText(audioInfo != null ? audioInfo.getAlbum() : null).setContentIntent(activity).setDeleteIntent(service).setShowWhen(false).setCategory("transport").setPriority(2).setStyle(new Notification.MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(new int[]{0, 1, 2}));
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationsController.checkOtherNotificationsChannel();
                builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (bitmap != null) {
                builder.setLargeIcon(bitmap);
            } else {
                builder.setLargeIcon(this.albumArtPlaceholder);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.playbackState.setState(6, 0, 1.0f).setActions(0);
                CharSequence charSequence = "";
                builder.addAction(new Notification.Action.Builder(NUM, charSequence, broadcast).build()).addAction(new Notification.Action.Builder(NUM, charSequence, (PendingIntent) null).build()).addAction(new Notification.Action.Builder(NUM, charSequence, broadcast3).build());
            } else {
                CharSequence charSequence2 = "";
                this.playbackState.setState(z2 ? 3 : 2, ((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, z2 ? 1.0f : 0.0f).setActions(822);
                builder.addAction(new Notification.Action.Builder(NUM, charSequence2, broadcast).build()).addAction(new Notification.Action.Builder(z2 ? NUM : NUM, charSequence2, broadcast2).build()).addAction(new Notification.Action.Builder(NUM, charSequence2, broadcast3).build());
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
            this.mediaSession.setMetadata(new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", cover).putString("android.media.metadata.ALBUM_ARTIST", musicAuthor).putLong("android.media.metadata.DURATION", j).putString("android.media.metadata.TITLE", musicTitle).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : null).build());
            builder.setVisibility(1);
            Notification build = builder.build();
            if (z2) {
                startForeground(5, build);
            } else {
                stopForeground(false);
                ((NotificationManager) getSystemService("notification")).notify(5, build);
            }
        } else {
            Bitmap bitmap2 = bitmap;
            String str = "";
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), NUM);
            RemoteViews remoteViews2 = supportBigNotifications ? new RemoteViews(getApplicationContext().getPackageName(), NUM) : null;
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(getApplicationContext());
            builder2.setSmallIcon(NUM);
            builder2.setContentIntent(activity);
            builder2.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            builder2.setContentTitle(musicTitle);
            Notification build2 = builder2.build();
            build2.contentView = remoteViews;
            if (supportBigNotifications) {
                build2.bigContentView = remoteViews2;
            }
            setListeners(remoteViews);
            if (supportBigNotifications) {
                setListeners(remoteViews2);
            }
            if (bitmap2 != null) {
                build2.contentView.setImageViewBitmap(NUM, bitmap2);
                if (supportBigNotifications) {
                    build2.bigContentView.setImageViewBitmap(NUM, bitmap2);
                }
            } else {
                build2.contentView.setImageViewResource(NUM, NUM);
                if (supportBigNotifications) {
                    build2.bigContentView.setImageViewResource(NUM, NUM);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                build2.contentView.setViewVisibility(NUM, 8);
                build2.contentView.setViewVisibility(NUM, 8);
                build2.contentView.setViewVisibility(NUM, 8);
                build2.contentView.setViewVisibility(NUM, 8);
                build2.contentView.setViewVisibility(NUM, 0);
                if (supportBigNotifications) {
                    build2.bigContentView.setViewVisibility(NUM, 8);
                    build2.bigContentView.setViewVisibility(NUM, 8);
                    build2.bigContentView.setViewVisibility(NUM, 8);
                    build2.bigContentView.setViewVisibility(NUM, 8);
                    build2.bigContentView.setViewVisibility(NUM, 0);
                }
            } else {
                build2.contentView.setViewVisibility(NUM, 8);
                build2.contentView.setViewVisibility(NUM, 0);
                build2.contentView.setViewVisibility(NUM, 0);
                if (supportBigNotifications) {
                    build2.bigContentView.setViewVisibility(NUM, 0);
                    build2.bigContentView.setViewVisibility(NUM, 0);
                    i2 = 8;
                    build2.bigContentView.setViewVisibility(NUM, 8);
                } else {
                    i2 = 8;
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    build2.contentView.setViewVisibility(NUM, i2);
                    build2.contentView.setViewVisibility(NUM, 0);
                    if (supportBigNotifications) {
                        build2.bigContentView.setViewVisibility(NUM, i2);
                        build2.bigContentView.setViewVisibility(NUM, 0);
                    }
                } else {
                    build2.contentView.setViewVisibility(NUM, 0);
                    build2.contentView.setViewVisibility(NUM, i2);
                    if (supportBigNotifications) {
                        build2.bigContentView.setViewVisibility(NUM, 0);
                        build2.bigContentView.setViewVisibility(NUM, i2);
                    }
                }
            }
            build2.contentView.setTextViewText(NUM, musicTitle);
            build2.contentView.setTextViewText(NUM, musicAuthor);
            if (supportBigNotifications) {
                build2.bigContentView.setTextViewText(NUM, musicTitle);
                build2.bigContentView.setTextViewText(NUM, musicAuthor);
                RemoteViews remoteViews3 = build2.bigContentView;
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    str = audioInfo.getAlbum();
                }
                remoteViews3.setTextViewText(NUM, str);
            }
            build2.flags |= 2;
            startForeground(5, build2);
        }
        if (this.remoteControlClient != null) {
            int id = MediaController.getInstance().getPlayingMessageObject().getId();
            if (this.notificationMessageID != id) {
                this.notificationMessageID = id;
                RemoteControlClient.MetadataEditor editMetadata = this.remoteControlClient.editMetadata(true);
                i = 2;
                editMetadata.putString(2, musicAuthor);
                editMetadata.putString(7, musicTitle);
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    editMetadata.putString(1, audioInfo.getAlbum());
                }
                editMetadata.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                if (cover != null) {
                    try {
                        editMetadata.putBitmap(100, cover);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                editMetadata.apply();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (MusicPlayerService.this.remoteControlClient != null && MediaController.getInstance().getPlayingMessageObject() != null) {
                            if (((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) == -9223372036854775807L) {
                                AndroidUtilities.runOnUIThread(this, 500);
                                return;
                            }
                            RemoteControlClient.MetadataEditor editMetadata = MusicPlayerService.this.remoteControlClient.editMetadata(false);
                            editMetadata.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                            editMetadata.apply();
                            int i = 2;
                            if (Build.VERSION.SDK_INT >= 18) {
                                RemoteControlClient access$100 = MusicPlayerService.this.remoteControlClient;
                                if (!MediaController.getInstance().isMessagePaused()) {
                                    i = 3;
                                }
                                access$100.setPlaybackState(i, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                                return;
                            }
                            RemoteControlClient access$1002 = MusicPlayerService.this.remoteControlClient;
                            if (!MediaController.getInstance().isMessagePaused()) {
                                i = 3;
                            }
                            access$1002.setPlaybackState(i);
                        }
                    }
                }, 1000);
            } else {
                i = 2;
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.remoteControlClient.setPlaybackState(8);
                return;
            }
            RemoteControlClient.MetadataEditor editMetadata2 = this.remoteControlClient.editMetadata(false);
            editMetadata2.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
            editMetadata2.apply();
            if (Build.VERSION.SDK_INT >= 18) {
                RemoteControlClient remoteControlClient2 = this.remoteControlClient;
                if (!MediaController.getInstance().isMessagePaused()) {
                    i = 3;
                }
                remoteControlClient2.setPlaybackState(i, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                return;
            }
            RemoteControlClient remoteControlClient3 = this.remoteControlClient;
            if (!MediaController.getInstance().isMessagePaused()) {
                i = 3;
            }
            remoteControlClient3.setPlaybackState(i);
        }
    }

    /* access modifiers changed from: private */
    public void updatePlaybackState(long j) {
        if (Build.VERSION.SDK_INT >= 21) {
            boolean z = !MediaController.getInstance().isMessagePaused();
            float f = 1.0f;
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.playbackState.setState(6, 0, 1.0f).setActions(0);
            } else {
                PlaybackState.Builder builder = this.playbackState;
                int i = z ? 3 : 2;
                if (!z) {
                    f = 0.0f;
                }
                builder.setState(i, j, f).setActions(822);
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
        }
    }

    public void setListeners(RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous"), NUM));
        remoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.close"), NUM));
        remoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.pause"), NUM));
        remoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next"), NUM));
        remoteViews.setOnClickPendingIntent(NUM, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.play"), NUM));
    }

    @SuppressLint({"NewApi"})
    public void onDestroy() {
        unregisterReceiver(this.headsetPlugReceiver);
        super.onDestroy();
        RemoteControlClient remoteControlClient2 = this.remoteControlClient;
        if (remoteControlClient2 != null) {
            RemoteControlClient.MetadataEditor editMetadata = remoteControlClient2.editMetadata(true);
            editMetadata.clear();
            editMetadata.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidLoad);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        String str2;
        if (i == NotificationCenter.messagePlayingPlayStateChanged) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null) {
                createNotification(playingMessageObject, false);
            } else {
                stopSelf();
            }
        } else if (i == NotificationCenter.messagePlayingDidSeek) {
            MessageObject playingMessageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (this.remoteControlClient != null && Build.VERSION.SDK_INT >= 18) {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, ((long) Math.round(((float) playingMessageObject2.audioPlayerDuration) * objArr[1].floatValue())) * 1000, MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            }
        } else if (i == NotificationCenter.httpFileDidLoad) {
            String str3 = objArr[0];
            MessageObject playingMessageObject3 = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject3 != null && (str2 = this.loadingFilePath) != null && str2.equals(str3)) {
                createNotification(playingMessageObject3, false);
            }
        } else if (i == NotificationCenter.fileDidLoad) {
            String str4 = objArr[0];
            MessageObject playingMessageObject4 = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject4 != null && (str = this.loadingFilePath) != null && str.equals(str4)) {
                createNotification(playingMessageObject4, false);
            }
        }
    }
}
