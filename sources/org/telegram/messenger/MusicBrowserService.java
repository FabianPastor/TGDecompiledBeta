package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.MediaDescription;
import android.media.MediaMetadata.Builder;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSession.QueueItem;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.service.media.MediaBrowserService.BrowserRoot;
import android.service.media.MediaBrowserService.Result;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;

@TargetApi(21)
public class MusicBrowserService extends MediaBrowserService implements NotificationCenterDelegate {
    public static final String ACTION_CMD = "com.example.android.mediabrowserservice.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";
    private static final String MEDIA_ID_ROOT = "__ROOT__";
    private static final String SLOT_RESERVATION_QUEUE = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";
    private static final String SLOT_RESERVATION_SKIP_TO_NEXT = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
    private static final String SLOT_RESERVATION_SKIP_TO_PREV = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
    private static final int STOP_DELAY = 30000;
    private RectF bitmapRect;
    private SparseArray<Chat> chats = new SparseArray();
    private boolean chatsLoaded;
    private int currentAccount = UserConfig.selectedAccount;
    private DelayedStopHandler delayedStopHandler = new DelayedStopHandler();
    private ArrayList<Integer> dialogs = new ArrayList();
    private int lastSelectedDialog;
    private boolean loadingChats;
    private MediaSession mediaSession;
    private SparseArray<ArrayList<MessageObject>> musicObjects = new SparseArray();
    private SparseArray<ArrayList<QueueItem>> musicQueues = new SparseArray();
    private Paint roundPaint;
    private boolean serviceStarted;
    private SparseArray<User> users = new SparseArray();

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicBrowserService> mWeakReference;

        private DelayedStopHandler(MusicBrowserService musicBrowserService) {
            this.mWeakReference = new WeakReference(musicBrowserService);
        }

        public void handleMessage(Message message) {
            MusicBrowserService musicBrowserService = (MusicBrowserService) this.mWeakReference.get();
            if (musicBrowserService != null && (MediaController.getInstance().getPlayingMessageObject() == null || MediaController.getInstance().isMessagePaused())) {
                musicBrowserService.stopSelf();
                musicBrowserService.serviceStarted = false;
            }
        }
    }

    private final class MediaSessionCallback extends Callback {
        private MediaSessionCallback() {
        }

        public void onPlay() {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MusicBrowserService.this.lastSelectedDialog);
                stringBuilder.append("_");
                stringBuilder.append(0);
                onPlayFromMediaId(stringBuilder.toString(), null);
                return;
            }
            MediaController.getInstance().playMessage(playingMessageObject);
        }

        public void onSkipToQueueItem(long j) {
            MediaController.getInstance().playMessageAtIndex((int) j);
            MusicBrowserService.this.handlePlayRequest();
        }

        public void onSeekTo(long j) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null) {
                MediaController.getInstance().seekToProgress(playingMessageObject, ((float) (j / 1000)) / ((float) playingMessageObject.getDuration()));
            }
        }

        public void onPlayFromMediaId(String str, Bundle bundle) {
            str = str.split("_");
            if (str.length == 2) {
                try {
                    int parseInt = Integer.parseInt(str[0]);
                    str = Integer.parseInt(str[1]);
                    ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(parseInt);
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(parseInt);
                    if (arrayList != null && str >= null) {
                        if (str < arrayList.size()) {
                            MusicBrowserService.this.lastSelectedDialog = parseInt;
                            MessagesController.getNotificationsSettings(MusicBrowserService.this.currentAccount).edit().putInt("auto_lastSelectedDialog", parseInt).commit();
                            MediaController.getInstance().setPlaylist(arrayList, (MessageObject) arrayList.get(str), false);
                            MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                            if (parseInt > 0) {
                                User user = (User) MusicBrowserService.this.users.get(parseInt);
                                if (user != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                                } else {
                                    MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                                }
                            } else {
                                Chat chat = (Chat) MusicBrowserService.this.chats.get(-parseInt);
                                if (chat != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(chat.title);
                                } else {
                                    MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                                }
                            }
                            MusicBrowserService.this.handlePlayRequest();
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        public void onPause() {
            MusicBrowserService.this.handlePauseRequest();
        }

        public void onStop() {
            MusicBrowserService.this.handleStopRequest(null);
        }

        public void onSkipToNext() {
            MediaController.getInstance().playNextMessage();
        }

        public void onSkipToPrevious() {
            MediaController.getInstance().playPreviousMessage();
        }

        public void onPlayFromSearch(String str, Bundle bundle) {
            if (str != null) {
                if (str.length() != null) {
                    str = str.toLowerCase();
                    for (int i = 0; i < MusicBrowserService.this.dialogs.size(); i++) {
                        int intValue = ((Integer) MusicBrowserService.this.dialogs.get(i)).intValue();
                        if (intValue <= 0) {
                            Chat chat = (Chat) MusicBrowserService.this.chats.get(-intValue);
                            if (chat != null) {
                                if (chat.title != null && chat.title.toLowerCase().contains(str)) {
                                    str = new StringBuilder();
                                    str.append(intValue);
                                    str.append("_");
                                    str.append(0);
                                    onPlayFromMediaId(str.toString(), null);
                                    break;
                                }
                            }
                        } else {
                            User user = (User) MusicBrowserService.this.users.get(intValue);
                            if (user != null) {
                                if ((user.first_name != null && user.first_name.startsWith(str)) || (user.last_name != null && user.last_name.startsWith(str))) {
                                    str = new StringBuilder();
                                    str.append(intValue);
                                    str.append("_");
                                    str.append(0);
                                    onPlayFromMediaId(str.toString(), null);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();
        this.lastSelectedDialog = MessagesController.getNotificationsSettings(this.currentAccount).getInt("auto_lastSelectedDialog", 0);
        this.mediaSession = new MediaSession(this, "MusicService");
        setSessionToken(this.mediaSession.getSessionToken());
        this.mediaSession.setCallback(new MediaSessionCallback());
        this.mediaSession.setFlags(3);
        Context applicationContext = getApplicationContext();
        this.mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 99, new Intent(applicationContext, LaunchActivity.class), 134217728));
        Bundle bundle = new Bundle();
        bundle.putBoolean(SLOT_RESERVATION_QUEUE, true);
        bundle.putBoolean(SLOT_RESERVATION_SKIP_TO_PREV, true);
        bundle.putBoolean(SLOT_RESERVATION_SKIP_TO_NEXT, true);
        this.mediaSession.setExtras(bundle);
        updatePlaybackState(null);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    public void onDestroy() {
        handleStopRequest(null);
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.mediaSession.release();
    }

    public BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
        if (str != null) {
            if (1000 == i || Process.myUid() == i || str.equals("com.google.android.mediasimulator") != 0 || str.equals("com.google.android.projection.gearhead") != null) {
                return new BrowserRoot(MEDIA_ID_ROOT, null);
            }
        }
        return null;
    }

    public void onLoadChildren(final String str, final Result<List<MediaItem>> result) {
        if (this.chatsLoaded) {
            loadChildrenImpl(str, result);
        } else {
            result.detach();
            if (!this.loadingChats) {
                this.loadingChats = true;
                final MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
                instance.getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MusicBrowserService$1$1 */
                    class C04081 implements Runnable {
                        C04081() {
                        }

                        public void run() {
                            MusicBrowserService.this.chatsLoaded = true;
                            MusicBrowserService.this.loadingChats = false;
                            MusicBrowserService.this.loadChildrenImpl(str, result);
                            if (MusicBrowserService.this.lastSelectedDialog == 0 && !MusicBrowserService.this.dialogs.isEmpty()) {
                                MusicBrowserService.this.lastSelectedDialog = ((Integer) MusicBrowserService.this.dialogs.get(0)).intValue();
                            }
                            if (MusicBrowserService.this.lastSelectedDialog != 0) {
                                ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(MusicBrowserService.this.lastSelectedDialog);
                                ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(MusicBrowserService.this.lastSelectedDialog);
                                if (!(arrayList == null || arrayList.isEmpty())) {
                                    MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                                    if (MusicBrowserService.this.lastSelectedDialog > 0) {
                                        User user = (User) MusicBrowserService.this.users.get(MusicBrowserService.this.lastSelectedDialog);
                                        if (user != null) {
                                            MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                                        } else {
                                            MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                                        }
                                    } else {
                                        Chat chat = (Chat) MusicBrowserService.this.chats.get(-MusicBrowserService.this.lastSelectedDialog);
                                        if (chat != null) {
                                            MusicBrowserService.this.mediaSession.setQueueTitle(chat.title);
                                        } else {
                                            MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                                        }
                                    }
                                    MessageObject messageObject = (MessageObject) arrayList.get(0);
                                    Builder builder = new Builder();
                                    builder.putLong("android.media.metadata.DURATION", (long) (messageObject.getDuration() * 1000));
                                    builder.putString("android.media.metadata.ARTIST", messageObject.getMusicAuthor());
                                    builder.putString("android.media.metadata.TITLE", messageObject.getMusicTitle());
                                    MusicBrowserService.this.mediaSession.setMetadata(builder.build());
                                }
                            }
                            MusicBrowserService.this.updatePlaybackState(null);
                        }
                    }

                    public void run() {
                        try {
                            int longValue;
                            Iterable arrayList = new ArrayList();
                            Iterable arrayList2 = new ArrayList();
                            SQLiteDatabase database = instance.getDatabase();
                            Object[] objArr = new Object[1];
                            int i = 0;
                            objArr[0] = Integer.valueOf(4);
                            SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v2 WHERE uid != 0 AND mid > 0 AND type = %d", objArr), new Object[0]);
                            while (queryFinalized.next()) {
                                longValue = (int) queryFinalized.longValue(0);
                                if (longValue != 0) {
                                    MusicBrowserService.this.dialogs.add(Integer.valueOf(longValue));
                                    if (longValue > 0) {
                                        arrayList.add(Integer.valueOf(longValue));
                                    } else {
                                        arrayList2.add(Integer.valueOf(-longValue));
                                    }
                                }
                            }
                            queryFinalized.dispose();
                            if (!MusicBrowserService.this.dialogs.isEmpty()) {
                                String join = TextUtils.join(",", MusicBrowserService.this.dialogs);
                                queryFinalized = instance.getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v2 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[]{join, Integer.valueOf(4)}), new Object[0]);
                                while (queryFinalized.next()) {
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                                    if (byteBufferValue != null) {
                                        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MusicBrowserService.this.currentAccount).clientUserId);
                                        byteBufferValue.reuse();
                                        if (MessageObject.isMusicMessage(TLdeserialize)) {
                                            longValue = queryFinalized.intValue(0);
                                            TLdeserialize.id = queryFinalized.intValue(2);
                                            TLdeserialize.dialog_id = (long) longValue;
                                            ArrayList arrayList3 = (ArrayList) MusicBrowserService.this.musicObjects.get(longValue);
                                            ArrayList arrayList4 = (ArrayList) MusicBrowserService.this.musicQueues.get(longValue);
                                            if (arrayList3 == null) {
                                                arrayList3 = new ArrayList();
                                                MusicBrowserService.this.musicObjects.put(longValue, arrayList3);
                                                arrayList4 = new ArrayList();
                                                MusicBrowserService.this.musicQueues.put(longValue, arrayList4);
                                            }
                                            MessageObject messageObject = new MessageObject(MusicBrowserService.this.currentAccount, TLdeserialize, false);
                                            arrayList3.add(0, messageObject);
                                            MediaDescription.Builder builder = new MediaDescription.Builder();
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(longValue);
                                            stringBuilder.append("_");
                                            stringBuilder.append(arrayList3.size());
                                            MediaDescription.Builder mediaId = builder.setMediaId(stringBuilder.toString());
                                            mediaId.setTitle(messageObject.getMusicTitle());
                                            mediaId.setSubtitle(messageObject.getMusicAuthor());
                                            arrayList4.add(0, new QueueItem(mediaId.build(), (long) arrayList4.size()));
                                        }
                                    }
                                }
                                queryFinalized.dispose();
                                if (!arrayList.isEmpty()) {
                                    ArrayList arrayList5 = new ArrayList();
                                    instance.getUsersInternal(TextUtils.join(",", arrayList), arrayList5);
                                    for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                                        User user = (User) arrayList5.get(i2);
                                        MusicBrowserService.this.users.put(user.id, user);
                                    }
                                }
                                if (!arrayList2.isEmpty()) {
                                    ArrayList arrayList6 = new ArrayList();
                                    instance.getChatsInternal(TextUtils.join(",", arrayList2), arrayList6);
                                    while (i < arrayList6.size()) {
                                        Chat chat = (Chat) arrayList6.get(i);
                                        MusicBrowserService.this.chats.put(chat.id, chat);
                                        i++;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        AndroidUtilities.runOnUIThread(new C04081());
                    }
                });
            }
        }
    }

    private void loadChildrenImpl(String str, Result<List<MediaItem>> result) {
        List arrayList = new ArrayList();
        int i = 0;
        if (MEDIA_ID_ROOT.equals(str)) {
            while (i < this.dialogs.size()) {
                str = ((Integer) this.dialogs.get(i)).intValue();
                MediaDescription.Builder builder = new MediaDescription.Builder();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("__CHAT_");
                stringBuilder.append(str);
                builder = builder.setMediaId(stringBuilder.toString());
                Bitmap bitmap = null;
                if (str > null) {
                    User user = (User) this.users.get(str);
                    if (user != null) {
                        builder.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        if (user.photo != null && (user.photo.photo_small instanceof TL_fileLocation)) {
                            str = user.photo.photo_small;
                            if (str != null) {
                                bitmap = createRoundBitmap(FileLoader.getPathToAttach(str, true));
                                if (bitmap != null) {
                                    builder.setIconBitmap(bitmap);
                                }
                            }
                            if (str != null || r3 == null) {
                                str = new StringBuilder();
                                str.append("android.resource://");
                                str.append(getApplicationContext().getPackageName());
                                str.append("/drawable/contact_blue");
                                builder.setIconUri(Uri.parse(str.toString()));
                            }
                            arrayList.add(new MediaItem(builder.build(), 1));
                            i++;
                        }
                    } else {
                        builder.setTitle("DELETED USER");
                    }
                } else {
                    Chat chat = (Chat) this.chats.get(-str);
                    if (chat != null) {
                        builder.setTitle(chat.title);
                        if (chat.photo != null && (chat.photo.photo_small instanceof TL_fileLocation)) {
                            str = chat.photo.photo_small;
                            if (str != null) {
                                bitmap = createRoundBitmap(FileLoader.getPathToAttach(str, true));
                                if (bitmap != null) {
                                    builder.setIconBitmap(bitmap);
                                }
                            }
                            if (str != null) {
                            }
                            str = new StringBuilder();
                            str.append("android.resource://");
                            str.append(getApplicationContext().getPackageName());
                            str.append("/drawable/contact_blue");
                            builder.setIconUri(Uri.parse(str.toString()));
                            arrayList.add(new MediaItem(builder.build(), 1));
                            i++;
                        }
                    } else {
                        builder.setTitle("DELETED CHAT");
                    }
                }
                str = null;
                if (str != null) {
                    bitmap = createRoundBitmap(FileLoader.getPathToAttach(str, true));
                    if (bitmap != null) {
                        builder.setIconBitmap(bitmap);
                    }
                }
                if (str != null) {
                }
                str = new StringBuilder();
                str.append("android.resource://");
                str.append(getApplicationContext().getPackageName());
                str.append("/drawable/contact_blue");
                builder.setIconUri(Uri.parse(str.toString()));
                arrayList.add(new MediaItem(builder.build(), 1));
                i++;
            }
        } else if (str != null && str.startsWith("__CHAT_")) {
            try {
                str = Integer.parseInt(str.replace("__CHAT_", TtmlNode.ANONYMOUS_REGION_ID));
            } catch (Throwable e) {
                FileLog.m3e(e);
                str = null;
            }
            ArrayList arrayList2 = (ArrayList) this.musicObjects.get(str);
            if (arrayList2 != null) {
                while (i < arrayList2.size()) {
                    MessageObject messageObject = (MessageObject) arrayList2.get(i);
                    MediaDescription.Builder builder2 = new MediaDescription.Builder();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append("_");
                    stringBuilder2.append(i);
                    builder2 = builder2.setMediaId(stringBuilder2.toString());
                    builder2.setTitle(messageObject.getMusicTitle());
                    builder2.setSubtitle(messageObject.getMusicAuthor());
                    arrayList.add(new MediaItem(builder2.build(), 2));
                    i++;
                }
            }
        }
        result.sendResult(arrayList);
    }

    private Bitmap createRoundBitmap(File file) {
        try {
            Options options = new Options();
            options.inSampleSize = 2;
            file = BitmapFactory.decodeFile(file.toString(), options);
            if (file != null) {
                Bitmap createBitmap = Bitmap.createBitmap(file.getWidth(), file.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Shader bitmapShader = new BitmapShader(file, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) file.getWidth(), (float) file.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) file.getWidth(), (float) file.getHeight(), this.roundPaint);
                return createBitmap;
            }
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
        return null;
    }

    private void updatePlaybackState(String str) {
        int i;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        long j = playingMessageObject != null ? (long) (playingMessageObject.audioProgressSec * 1000) : -1;
        PlaybackState.Builder actions = new PlaybackState.Builder().setActions(getAvailableActions());
        int i2 = playingMessageObject == null ? 1 : MediaController.getInstance().isDownloadingCurrentMessage() ? 6 : MediaController.getInstance().isMessagePaused() ? 2 : 3;
        if (str != null) {
            actions.setErrorMessage(str);
            i = 7;
        } else {
            i = i2;
        }
        actions.setState(i, j, 1.0f, SystemClock.elapsedRealtime());
        if (playingMessageObject != null) {
            actions.setActiveQueueItemId((long) MediaController.getInstance().getPlayingMessageObjectNum());
        } else {
            actions.setActiveQueueItemId(0);
        }
        this.mediaSession.setPlaybackState(actions.build());
    }

    private long getAvailableActions() {
        long j = 3076;
        if (MediaController.getInstance().getPlayingMessageObject() == null) {
            return 3076;
        }
        if (!MediaController.getInstance().isMessagePaused()) {
            j = 3078;
        }
        return (j | 16) | 32;
    }

    private void handleStopRequest(String str) {
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
        updatePlaybackState(str);
        stopSelf();
        this.serviceStarted = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    private void handlePlayRequest() {
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        if (!this.serviceStarted) {
            try {
                startService(new Intent(getApplicationContext(), MusicBrowserService.class));
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
            this.serviceStarted = true;
        }
        if (!this.mediaSession.isActive()) {
            this.mediaSession.setActive(true);
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            Builder builder = new Builder();
            builder.putLong("android.media.metadata.DURATION", (long) (playingMessageObject.getDuration() * 1000));
            builder.putString("android.media.metadata.ARTIST", playingMessageObject.getMusicAuthor());
            builder.putString("android.media.metadata.TITLE", playingMessageObject.getMusicTitle());
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo != null) {
                Bitmap cover = audioInfo.getCover();
                if (cover != null) {
                    builder.putBitmap("android.media.metadata.ALBUM_ART", cover);
                }
            }
            this.mediaSession.setMetadata(builder.build());
        }
    }

    private void handlePauseRequest() {
        MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        updatePlaybackState(0);
        handlePlayRequest();
    }
}
