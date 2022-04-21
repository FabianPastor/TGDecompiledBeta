package org.telegram.messenger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.text.TextUtils;
import androidx.collection.LongSparseArray;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public class MusicBrowserService extends MediaBrowserService implements NotificationCenter.NotificationCenterDelegate {
    public static final String ACTION_CMD = "com.example.android.mediabrowserservice.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";
    private static final String MEDIA_ID_ROOT = "__ROOT__";
    private static final String SLOT_RESERVATION_QUEUE = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";
    private static final String SLOT_RESERVATION_SKIP_TO_NEXT = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
    private static final String SLOT_RESERVATION_SKIP_TO_PREV = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
    private static final int STOP_DELAY = 30000;
    private RectF bitmapRect;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.Chat> chats = new LongSparseArray<>();
    private boolean chatsLoaded;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private DelayedStopHandler delayedStopHandler = new DelayedStopHandler();
    /* access modifiers changed from: private */
    public ArrayList<Long> dialogs = new ArrayList<>();
    /* access modifiers changed from: private */
    public long lastSelectedDialog;
    private boolean loadingChats;
    /* access modifiers changed from: private */
    public MediaSession mediaSession;
    /* access modifiers changed from: private */
    public LongSparseArray<ArrayList<MessageObject>> musicObjects = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public LongSparseArray<ArrayList<MediaSession.QueueItem>> musicQueues = new LongSparseArray<>();
    private Paint roundPaint;
    /* access modifiers changed from: private */
    public boolean serviceStarted;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.User> users = new LongSparseArray<>();

    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();
        this.lastSelectedDialog = AndroidUtilities.getPrefIntOrLong(MessagesController.getNotificationsSettings(this.currentAccount), "auto_lastSelectedDialog", 0);
        MediaSession mediaSession2 = new MediaSession(this, "MusicService");
        this.mediaSession = mediaSession2;
        setSessionToken(mediaSession2.getSessionToken());
        this.mediaSession.setCallback(new MediaSessionCallback());
        this.mediaSession.setFlags(3);
        Context context = getApplicationContext();
        this.mediaSession.setSessionActivity(PendingIntent.getActivity(context, 99, new Intent(context, LaunchActivity.class), NUM));
        Bundle extras = new Bundle();
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE", true);
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS", true);
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT", true);
        this.mediaSession.setExtras(extras);
        updatePlaybackState((String) null);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    public int onStartCommand(Intent startIntent, int flags, int startId) {
        return 1;
    }

    public void onDestroy() {
        super.onDestroy();
        handleStopRequest((String) null);
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.mediaSession.release();
    }

    public MediaBrowserService.BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        if (clientPackageName == null || (1000 != clientUid && Process.myUid() != clientUid && !clientPackageName.equals("com.google.android.mediasimulator") && !clientPackageName.equals("com.google.android.projection.gearhead"))) {
            return null;
        }
        return new MediaBrowserService.BrowserRoot("__ROOT__", (Bundle) null);
    }

    public void onLoadChildren(String parentMediaId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result) {
        if (!this.chatsLoaded) {
            result.detach();
            if (!this.loadingChats) {
                this.loadingChats = true;
                MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                messagesStorage.getStorageQueue().postRunnable(new MusicBrowserService$$ExternalSyntheticLambda1(this, messagesStorage, parentMediaId, result));
                return;
            }
            return;
        }
        loadChildrenImpl(parentMediaId, result);
    }

    /* renamed from: lambda$onLoadChildren$1$org-telegram-messenger-MusicBrowserService  reason: not valid java name */
    public /* synthetic */ void m1033x85bd046b(MessagesStorage messagesStorage, String parentMediaId, MediaBrowserService.Result result) {
        String ids;
        MessagesStorage messagesStorage2 = messagesStorage;
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            int i = 1;
            boolean z = false;
            SQLiteCursor cursor = messagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v4 WHERE uid != 0 AND mid > 0 AND type = %d", new Object[]{4}), new Object[0]);
            while (cursor.next()) {
                long dialogId = cursor.longValue(0);
                if (!DialogObject.isEncryptedDialog(dialogId)) {
                    this.dialogs.add(Long.valueOf(dialogId));
                    if (DialogObject.isUserDialog(dialogId)) {
                        usersToLoad.add(Long.valueOf(dialogId));
                    } else {
                        chatsToLoad.add(Long.valueOf(-dialogId));
                    }
                }
            }
            cursor.dispose();
            if (!this.dialogs.isEmpty()) {
                String ids2 = TextUtils.join(",", this.dialogs);
                int i2 = 2;
                SQLiteCursor cursor2 = messagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v4 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[]{ids2, 4}), new Object[0]);
                while (cursor2.next()) {
                    NativeByteBuffer data = cursor2.byteBufferValue(i);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        if (MessageObject.isMusicMessage(message)) {
                            long did = cursor2.longValue(z ? 1 : 0);
                            message.id = cursor2.intValue(i2);
                            message.dialog_id = did;
                            ArrayList<MessageObject> arrayList = this.musicObjects.get(did);
                            ArrayList<MediaSession.QueueItem> arrayList1 = this.musicQueues.get(did);
                            if (arrayList == null) {
                                arrayList = new ArrayList<>();
                                this.musicObjects.put(did, arrayList);
                                arrayList1 = new ArrayList<>();
                                this.musicQueues.put(did, arrayList1);
                            }
                            ids = ids2;
                            MessageObject messageObject = new MessageObject(this.currentAccount, message, z, true);
                            arrayList.add(z, messageObject);
                            MediaDescription.Builder builder = new MediaDescription.Builder();
                            MediaDescription.Builder builder2 = builder.setMediaId(did + "_" + arrayList.size());
                            builder2.setTitle(messageObject.getMusicTitle());
                            builder2.setSubtitle(messageObject.getMusicAuthor());
                            MediaDescription.Builder builder3 = builder2;
                            NativeByteBuffer nativeByteBuffer = data;
                            arrayList1.add(0, new MediaSession.QueueItem(builder2.build(), (long) arrayList1.size()));
                        } else {
                            ids = ids2;
                            NativeByteBuffer nativeByteBuffer2 = data;
                        }
                    } else {
                        ids = ids2;
                        NativeByteBuffer nativeByteBuffer3 = data;
                    }
                    ids2 = ids;
                    i = 1;
                    z = false;
                    i2 = 2;
                }
                cursor2.dispose();
                if (!usersToLoad.isEmpty()) {
                    ArrayList<TLRPC.User> usersArrayList = new ArrayList<>();
                    messagesStorage2.getUsersInternal(TextUtils.join(",", usersToLoad), usersArrayList);
                    for (int a = 0; a < usersArrayList.size(); a++) {
                        TLRPC.User user = usersArrayList.get(a);
                        this.users.put(user.id, user);
                    }
                }
                if (!chatsToLoad.isEmpty()) {
                    ArrayList<TLRPC.Chat> chatsArrayList = new ArrayList<>();
                    messagesStorage2.getChatsInternal(TextUtils.join(",", chatsToLoad), chatsArrayList);
                    for (int a2 = 0; a2 < chatsArrayList.size(); a2++) {
                        TLRPC.Chat chat = chatsArrayList.get(a2);
                        this.chats.put(chat.id, chat);
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new MusicBrowserService$$ExternalSyntheticLambda0(this, parentMediaId, result));
    }

    /* renamed from: lambda$onLoadChildren$0$org-telegram-messenger-MusicBrowserService  reason: not valid java name */
    public /* synthetic */ void m1032xc2d09b0c(String parentMediaId, MediaBrowserService.Result result) {
        this.chatsLoaded = true;
        this.loadingChats = false;
        loadChildrenImpl(parentMediaId, result);
        if (this.lastSelectedDialog == 0 && !this.dialogs.isEmpty()) {
            this.lastSelectedDialog = this.dialogs.get(0).longValue();
        }
        long j = this.lastSelectedDialog;
        if (j != 0) {
            ArrayList<MessageObject> arrayList = this.musicObjects.get(j);
            ArrayList<MediaSession.QueueItem> arrayList1 = this.musicQueues.get(this.lastSelectedDialog);
            if (arrayList != null && !arrayList.isEmpty()) {
                this.mediaSession.setQueue(arrayList1);
                long j2 = this.lastSelectedDialog;
                if (j2 > 0) {
                    TLRPC.User user = this.users.get(j2);
                    if (user != null) {
                        this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                    } else {
                        this.mediaSession.setQueueTitle("DELETED USER");
                    }
                } else {
                    TLRPC.Chat chat = this.chats.get(-j2);
                    if (chat != null) {
                        this.mediaSession.setQueueTitle(chat.title);
                    } else {
                        this.mediaSession.setQueueTitle("DELETED CHAT");
                    }
                }
                MessageObject messageObject = arrayList.get(0);
                MediaMetadata.Builder builder = new MediaMetadata.Builder();
                builder.putLong("android.media.metadata.DURATION", (long) (messageObject.getDuration() * 1000));
                builder.putString("android.media.metadata.ARTIST", messageObject.getMusicAuthor());
                builder.putString("android.media.metadata.TITLE", messageObject.getMusicTitle());
                this.mediaSession.setMetadata(builder.build());
            }
        }
        updatePlaybackState((String) null);
    }

    private void loadChildrenImpl(String parentMediaId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result) {
        List<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();
        if ("__ROOT__".equals(parentMediaId)) {
            for (int a = 0; a < this.dialogs.size(); a++) {
                long dialogId = this.dialogs.get(a).longValue();
                MediaDescription.Builder builder = new MediaDescription.Builder();
                MediaDescription.Builder builder2 = builder.setMediaId("__CHAT_" + dialogId);
                TLRPC.FileLocation avatar = null;
                if (DialogObject.isUserDialog(dialogId)) {
                    TLRPC.User user = this.users.get(dialogId);
                    if (user != null) {
                        builder2.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        if (user.photo != null && !(user.photo.photo_small instanceof TLRPC.TL_fileLocationUnavailable)) {
                            avatar = user.photo.photo_small;
                        }
                    } else {
                        builder2.setTitle("DELETED USER");
                    }
                } else {
                    TLRPC.Chat chat = this.chats.get(-dialogId);
                    if (chat != null) {
                        builder2.setTitle(chat.title);
                        if (chat.photo != null && !(chat.photo.photo_small instanceof TLRPC.TL_fileLocationUnavailable)) {
                            avatar = chat.photo.photo_small;
                        }
                    } else {
                        builder2.setTitle("DELETED CHAT");
                    }
                }
                Bitmap bitmap = null;
                if (!(avatar == null || (bitmap = createRoundBitmap(FileLoader.getPathToAttach(avatar, true))) == null)) {
                    builder2.setIconBitmap(bitmap);
                }
                if (avatar == null || bitmap == null) {
                    builder2.setIconUri(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/drawable/contact_blue"));
                }
                mediaItems.add(new MediaBrowser.MediaItem(builder2.build(), 1));
            }
        } else if (parentMediaId != null && parentMediaId.startsWith("__CHAT_")) {
            int did = 0;
            try {
                did = Integer.parseInt(parentMediaId.replace("__CHAT_", ""));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ArrayList<MessageObject> arrayList = this.musicObjects.get((long) did);
            if (arrayList != null) {
                for (int a2 = 0; a2 < arrayList.size(); a2++) {
                    MessageObject messageObject = arrayList.get(a2);
                    MediaDescription.Builder builder3 = new MediaDescription.Builder();
                    MediaDescription.Builder builder4 = builder3.setMediaId(did + "_" + a2);
                    builder4.setTitle(messageObject.getMusicTitle());
                    builder4.setSubtitle(messageObject.getMusicAuthor());
                    mediaItems.add(new MediaBrowser.MediaItem(builder4.build(), 2));
                }
            }
        }
        result.sendResult(mediaItems);
    }

    private Bitmap createRoundBitmap(File path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString(), options);
            if (bitmap == null) {
                return null;
            }
            Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            if (this.roundPaint == null) {
                this.roundPaint = new Paint(1);
                this.bitmapRect = new RectF();
            }
            this.roundPaint.setShader(shader);
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), this.roundPaint);
            return result;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        private MediaSessionCallback() {
        }

        public void onPlay() {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject == null) {
                onPlayFromMediaId(MusicBrowserService.this.lastSelectedDialog + "_" + 0, (Bundle) null);
                return;
            }
            MediaController.getInstance().playMessage(messageObject);
        }

        public void onSkipToQueueItem(long queueId) {
            MediaController.getInstance().playMessageAtIndex((int) queueId);
            MusicBrowserService.this.handlePlayRequest();
        }

        public void onSeekTo(long position) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                MediaController.getInstance().seekToProgress(messageObject, ((float) (position / 1000)) / ((float) messageObject.getDuration()));
            }
        }

        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            String[] args = mediaId.split("_");
            if (args.length == 2) {
                try {
                    long did = Long.parseLong(args[0]);
                    int id = Integer.parseInt(args[1]);
                    ArrayList<MessageObject> arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(did);
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(did);
                    if (arrayList != null && id >= 0) {
                        if (id < arrayList.size()) {
                            long unused = MusicBrowserService.this.lastSelectedDialog = did;
                            MessagesController.getNotificationsSettings(MusicBrowserService.this.currentAccount).edit().putLong("auto_lastSelectedDialog", did).commit();
                            MediaController.getInstance().setPlaylist(arrayList, arrayList.get(id), 0, false, (MediaController.PlaylistGlobalSearchParams) null);
                            MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                            if (did > 0) {
                                TLRPC.User user = (TLRPC.User) MusicBrowserService.this.users.get(did);
                                if (user != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                                } else {
                                    MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                                }
                            } else {
                                TLRPC.Chat chat = (TLRPC.Chat) MusicBrowserService.this.chats.get(-did);
                                if (chat != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(chat.title);
                                } else {
                                    MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                                }
                            }
                            MusicBrowserService.this.handlePlayRequest();
                        }
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }

        public void onPause() {
            MusicBrowserService.this.handlePauseRequest();
        }

        public void onStop() {
            MusicBrowserService.this.handleStopRequest((String) null);
        }

        public void onSkipToNext() {
            MediaController.getInstance().playNextMessage();
        }

        public void onSkipToPrevious() {
            MediaController.getInstance().playPreviousMessage();
        }

        public void onPlayFromSearch(String query, Bundle extras) {
            if (query != null && query.length() != 0) {
                String query2 = query.toLowerCase();
                for (int a = 0; a < MusicBrowserService.this.dialogs.size(); a++) {
                    long did = ((Long) MusicBrowserService.this.dialogs.get(a)).longValue();
                    if (DialogObject.isUserDialog(did)) {
                        TLRPC.User user = (TLRPC.User) MusicBrowserService.this.users.get(did);
                        if (user != null && ((user.first_name != null && user.first_name.startsWith(query2)) || (user.last_name != null && user.last_name.startsWith(query2)))) {
                            onPlayFromMediaId(did + "_" + 0, (Bundle) null);
                            return;
                        }
                    } else {
                        TLRPC.Chat chat = (TLRPC.Chat) MusicBrowserService.this.chats.get(-did);
                        if (!(chat == null || chat.title == null || !chat.title.toLowerCase().contains(query2))) {
                            onPlayFromMediaId(did + "_" + 0, (Bundle) null);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void updatePlaybackState(String error) {
        int state;
        int state2;
        long position = -1;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            position = ((long) playingMessageObject.audioProgressSec) * 1000;
        }
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder().setActions(getAvailableActions());
        if (playingMessageObject == null) {
            state = 1;
        } else if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            state = 6;
        } else {
            state = MediaController.getInstance().isMessagePaused() ? 2 : 3;
        }
        if (error != null) {
            stateBuilder.setErrorMessage(error);
            state2 = 7;
        } else {
            state2 = state;
        }
        stateBuilder.setState(state2, position, 1.0f, SystemClock.elapsedRealtime());
        if (playingMessageObject != null) {
            stateBuilder.setActiveQueueItemId((long) MediaController.getInstance().getPlayingMessageObjectNum());
        } else {
            stateBuilder.setActiveQueueItemId(0);
        }
        this.mediaSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = 3076;
        if (MediaController.getInstance().getPlayingMessageObject() == null) {
            return 3076;
        }
        if (!MediaController.getInstance().isMessagePaused()) {
            actions = 3076 | 2;
        }
        return actions | 16 | 32;
    }

    /* access modifiers changed from: private */
    public void handleStopRequest(String withError) {
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
        updatePlaybackState(withError);
        stopSelf();
        this.serviceStarted = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    /* access modifiers changed from: private */
    public void handlePlayRequest() {
        Bitmap bitmap;
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        if (!this.serviceStarted) {
            try {
                startService(new Intent(getApplicationContext(), MusicBrowserService.class));
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.serviceStarted = true;
        }
        if (!this.mediaSession.isActive()) {
            this.mediaSession.setActive(true);
        }
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null) {
            MediaMetadata.Builder builder = new MediaMetadata.Builder();
            builder.putLong("android.media.metadata.DURATION", (long) (messageObject.getDuration() * 1000));
            builder.putString("android.media.metadata.ARTIST", messageObject.getMusicAuthor());
            builder.putString("android.media.metadata.TITLE", messageObject.getMusicTitle());
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (!(audioInfo == null || (bitmap = audioInfo.getCover()) == null)) {
                builder.putBitmap("android.media.metadata.ALBUM_ART", bitmap);
            }
            this.mediaSession.setMetadata(builder.build());
        }
    }

    /* access modifiers changed from: private */
    public void handlePauseRequest() {
        MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        updatePlaybackState((String) null);
        handlePlayRequest();
    }

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicBrowserService> mWeakReference;

        private DelayedStopHandler(MusicBrowserService service) {
            this.mWeakReference = new WeakReference<>(service);
        }

        public void handleMessage(Message msg) {
            MusicBrowserService service = (MusicBrowserService) this.mWeakReference.get();
            if (service == null) {
                return;
            }
            if (MediaController.getInstance().getPlayingMessageObject() == null || MediaController.getInstance().isMessagePaused()) {
                service.stopSelf();
                boolean unused = service.serviceStarted = false;
            }
        }
    }
}
