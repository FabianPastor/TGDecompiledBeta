package org.telegram.messenger;

import android.annotation.TargetApi;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.LaunchActivity;

@TargetApi(21)
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
    public LongSparseArray<TLRPC$Chat> chats = new LongSparseArray<>();
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
    public LongSparseArray<TLRPC$User> users = new LongSparseArray<>();

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();
        this.lastSelectedDialog = AndroidUtilities.getPrefIntOrLong(MessagesController.getNotificationsSettings(this.currentAccount), "auto_lastSelectedDialog", 0);
        MediaSession mediaSession2 = new MediaSession(this, "MusicService");
        this.mediaSession = mediaSession2;
        setSessionToken(mediaSession2.getSessionToken());
        this.mediaSession.setCallback(new MediaSessionCallback());
        this.mediaSession.setFlags(3);
        Context applicationContext = getApplicationContext();
        this.mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 99, new Intent(applicationContext, LaunchActivity.class), NUM));
        Bundle bundle = new Bundle();
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE", true);
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS", true);
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT", true);
        this.mediaSession.setExtras(bundle);
        updatePlaybackState((String) null);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    public void onDestroy() {
        super.onDestroy();
        handleStopRequest((String) null);
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.mediaSession.release();
    }

    public MediaBrowserService.BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
        if (str == null || (1000 != i && Process.myUid() != i && !str.equals("com.google.android.mediasimulator") && !str.equals("com.google.android.projection.gearhead"))) {
            return null;
        }
        return new MediaBrowserService.BrowserRoot("__ROOT__", (Bundle) null);
    }

    public void onLoadChildren(String str, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result) {
        if (!this.chatsLoaded) {
            result.detach();
            if (!this.loadingChats) {
                this.loadingChats = true;
                MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
                instance.getStorageQueue().postRunnable(new MusicBrowserService$$ExternalSyntheticLambda1(this, instance, str, result));
                return;
            }
            return;
        }
        loadChildrenImpl(str, result);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLoadChildren$1(MessagesStorage messagesStorage, String str, MediaBrowserService.Result result) {
        MessagesStorage messagesStorage2 = messagesStorage;
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = messagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v4 WHERE uid != 0 AND mid > 0 AND type = %d", new Object[]{4}), new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (!DialogObject.isEncryptedDialog(longValue)) {
                    this.dialogs.add(Long.valueOf(longValue));
                    if (DialogObject.isUserDialog(longValue)) {
                        arrayList.add(Long.valueOf(longValue));
                    } else {
                        arrayList2.add(Long.valueOf(-longValue));
                    }
                }
            }
            queryFinalized.dispose();
            if (!this.dialogs.isEmpty()) {
                String join = TextUtils.join(",", this.dialogs);
                SQLiteCursor queryFinalized2 = messagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v4 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[]{join, 4}), new Object[0]);
                while (queryFinalized2.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                        byteBufferValue.reuse();
                        if (MessageObject.isMusicMessage(TLdeserialize)) {
                            long longValue2 = queryFinalized2.longValue(0);
                            TLdeserialize.id = queryFinalized2.intValue(2);
                            TLdeserialize.dialog_id = longValue2;
                            ArrayList arrayList3 = this.musicObjects.get(longValue2);
                            ArrayList arrayList4 = this.musicQueues.get(longValue2);
                            if (arrayList3 == null) {
                                arrayList3 = new ArrayList();
                                this.musicObjects.put(longValue2, arrayList3);
                                arrayList4 = new ArrayList();
                                this.musicQueues.put(longValue2, arrayList4);
                            }
                            MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false, true);
                            arrayList3.add(0, messageObject);
                            MediaDescription.Builder builder = new MediaDescription.Builder();
                            MediaDescription.Builder mediaId = builder.setMediaId(longValue2 + "_" + arrayList3.size());
                            mediaId.setTitle(messageObject.getMusicTitle());
                            mediaId.setSubtitle(messageObject.getMusicAuthor());
                            arrayList4.add(0, new MediaSession.QueueItem(mediaId.build(), (long) arrayList4.size()));
                        }
                    }
                }
                queryFinalized2.dispose();
                if (!arrayList.isEmpty()) {
                    ArrayList arrayList5 = new ArrayList();
                    messagesStorage2.getUsersInternal(TextUtils.join(",", arrayList), arrayList5);
                    for (int i = 0; i < arrayList5.size(); i++) {
                        TLRPC$User tLRPC$User = (TLRPC$User) arrayList5.get(i);
                        this.users.put(tLRPC$User.id, tLRPC$User);
                    }
                }
                if (!arrayList2.isEmpty()) {
                    ArrayList arrayList6 = new ArrayList();
                    messagesStorage2.getChatsInternal(TextUtils.join(",", arrayList2), arrayList6);
                    for (int i2 = 0; i2 < arrayList6.size(); i2++) {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) arrayList6.get(i2);
                        this.chats.put(tLRPC$Chat.id, tLRPC$Chat);
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new MusicBrowserService$$ExternalSyntheticLambda0(this, str, result));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onLoadChildren$0(String str, MediaBrowserService.Result result) {
        this.chatsLoaded = true;
        this.loadingChats = false;
        loadChildrenImpl(str, result);
        if (this.lastSelectedDialog == 0 && !this.dialogs.isEmpty()) {
            this.lastSelectedDialog = this.dialogs.get(0).longValue();
        }
        long j = this.lastSelectedDialog;
        if (j != 0) {
            ArrayList arrayList = this.musicObjects.get(j);
            ArrayList arrayList2 = this.musicQueues.get(this.lastSelectedDialog);
            if (arrayList != null && !arrayList.isEmpty()) {
                this.mediaSession.setQueue(arrayList2);
                long j2 = this.lastSelectedDialog;
                if (j2 > 0) {
                    TLRPC$User tLRPC$User = this.users.get(j2);
                    if (tLRPC$User != null) {
                        this.mediaSession.setQueueTitle(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                    } else {
                        this.mediaSession.setQueueTitle("DELETED USER");
                    }
                } else {
                    TLRPC$Chat tLRPC$Chat = this.chats.get(-j2);
                    if (tLRPC$Chat != null) {
                        this.mediaSession.setQueueTitle(tLRPC$Chat.title);
                    } else {
                        this.mediaSession.setQueueTitle("DELETED CHAT");
                    }
                }
                MessageObject messageObject = (MessageObject) arrayList.get(0);
                MediaMetadata.Builder builder = new MediaMetadata.Builder();
                builder.putLong("android.media.metadata.DURATION", (long) (messageObject.getDuration() * 1000));
                builder.putString("android.media.metadata.ARTIST", messageObject.getMusicAuthor());
                builder.putString("android.media.metadata.TITLE", messageObject.getMusicTitle());
                this.mediaSession.setMetadata(builder.build());
            }
        }
        updatePlaybackState((String) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0060, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable) == false) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0081, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable) == false) goto L_0x008a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadChildrenImpl(java.lang.String r8, android.service.media.MediaBrowserService.Result<java.util.List<android.media.browse.MediaBrowser.MediaItem>> r9) {
        /*
            r7 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = "__ROOT__"
            boolean r1 = r1.equals(r8)
            java.lang.String r2 = "__CHAT_"
            r3 = 0
            if (r1 == 0) goto L_0x00d9
        L_0x0010:
            java.util.ArrayList<java.lang.Long> r8 = r7.dialogs
            int r8 = r8.size()
            if (r3 >= r8) goto L_0x0143
            java.util.ArrayList<java.lang.Long> r8 = r7.dialogs
            java.lang.Object r8 = r8.get(r3)
            java.lang.Long r8 = (java.lang.Long) r8
            long r4 = r8.longValue()
            android.media.MediaDescription$Builder r8 = new android.media.MediaDescription$Builder
            r8.<init>()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            android.media.MediaDescription$Builder r8 = r8.setMediaId(r1)
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            r6 = 0
            if (r1 == 0) goto L_0x0069
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$User> r1 = r7.users
            java.lang.Object r1 = r1.get(r4)
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1
            if (r1 == 0) goto L_0x0063
            java.lang.String r4 = r1.first_name
            java.lang.String r5 = r1.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r4, r5)
            r8.setTitle(r4)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo
            if (r1 == 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r4 != 0) goto L_0x0089
            goto L_0x008a
        L_0x0063:
            java.lang.String r1 = "DELETED USER"
            r8.setTitle(r1)
            goto L_0x0089
        L_0x0069:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Chat> r1 = r7.chats
            long r4 = -r4
            java.lang.Object r1 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Chat r1 = (org.telegram.tgnet.TLRPC$Chat) r1
            if (r1 == 0) goto L_0x0084
            java.lang.String r4 = r1.title
            r8.setTitle(r4)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r1.photo
            if (r1 == 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r4 != 0) goto L_0x0089
            goto L_0x008a
        L_0x0084:
            java.lang.String r1 = "DELETED CHAT"
            r8.setTitle(r1)
        L_0x0089:
            r1 = r6
        L_0x008a:
            r4 = 1
            if (r1 == 0) goto L_0x00a0
            int r5 = r7.currentAccount
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r5)
            java.io.File r5 = r5.getPathToAttach(r1, r4)
            android.graphics.Bitmap r6 = r7.createRoundBitmap(r5)
            if (r6 == 0) goto L_0x00a0
            r8.setIconBitmap(r6)
        L_0x00a0:
            if (r1 == 0) goto L_0x00a4
            if (r6 != 0) goto L_0x00c9
        L_0x00a4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "android.resource://"
            r1.append(r5)
            android.content.Context r5 = r7.getApplicationContext()
            java.lang.String r5 = r5.getPackageName()
            r1.append(r5)
            java.lang.String r5 = "/drawable/contact_blue"
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.net.Uri r1 = android.net.Uri.parse(r1)
            r8.setIconUri(r1)
        L_0x00c9:
            android.media.browse.MediaBrowser$MediaItem r1 = new android.media.browse.MediaBrowser$MediaItem
            android.media.MediaDescription r8 = r8.build()
            r1.<init>(r8, r4)
            r0.add(r1)
            int r3 = r3 + 1
            goto L_0x0010
        L_0x00d9:
            if (r8 == 0) goto L_0x0143
            boolean r1 = r8.startsWith(r2)
            if (r1 == 0) goto L_0x0143
            java.lang.String r1 = ""
            java.lang.String r8 = r8.replace(r2, r1)     // Catch:{ Exception -> 0x00ec }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x00ec }
            goto L_0x00f1
        L_0x00ec:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            r8 = 0
        L_0x00f1:
            androidx.collection.LongSparseArray<java.util.ArrayList<org.telegram.messenger.MessageObject>> r1 = r7.musicObjects
            long r4 = (long) r8
            java.lang.Object r1 = r1.get(r4)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 == 0) goto L_0x0143
        L_0x00fc:
            int r2 = r1.size()
            if (r3 >= r2) goto L_0x0143
            java.lang.Object r2 = r1.get(r3)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            android.media.MediaDescription$Builder r4 = new android.media.MediaDescription$Builder
            r4.<init>()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r8)
            java.lang.String r6 = "_"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            android.media.MediaDescription$Builder r4 = r4.setMediaId(r5)
            java.lang.String r5 = r2.getMusicTitle()
            r4.setTitle(r5)
            java.lang.String r2 = r2.getMusicAuthor()
            r4.setSubtitle(r2)
            android.media.browse.MediaBrowser$MediaItem r2 = new android.media.browse.MediaBrowser$MediaItem
            android.media.MediaDescription r4 = r4.build()
            r5 = 2
            r2.<init>(r4, r5)
            r0.add(r2)
            int r3 = r3 + 1
            goto L_0x00fc
        L_0x0143:
            r9.sendResult(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MusicBrowserService.loadChildrenImpl(java.lang.String, android.service.media.MediaBrowserService$Result):void");
    }

    private Bitmap createRoundBitmap(File file) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap decodeFile = BitmapFactory.decodeFile(file.toString(), options);
            if (decodeFile == null) {
                return null;
            }
            Bitmap createBitmap = Bitmap.createBitmap(decodeFile.getWidth(), decodeFile.getHeight(), Bitmap.Config.ARGB_8888);
            createBitmap.eraseColor(0);
            Canvas canvas = new Canvas(createBitmap);
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(decodeFile, tileMode, tileMode);
            if (this.roundPaint == null) {
                this.roundPaint = new Paint(1);
                this.bitmapRect = new RectF();
            }
            this.roundPaint.setShader(bitmapShader);
            this.bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), this.roundPaint);
            return createBitmap;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        private MediaSessionCallback() {
        }

        public void onPlay() {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject == null) {
                onPlayFromMediaId(MusicBrowserService.this.lastSelectedDialog + "_" + 0, (Bundle) null);
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
            String[] split = str.split("_");
            if (split.length == 2) {
                try {
                    long parseLong = Long.parseLong(split[0]);
                    int parseInt = Integer.parseInt(split[1]);
                    ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(parseLong);
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(parseLong);
                    if (arrayList != null && parseInt >= 0) {
                        if (parseInt < arrayList.size()) {
                            long unused = MusicBrowserService.this.lastSelectedDialog = parseLong;
                            MessagesController.getNotificationsSettings(MusicBrowserService.this.currentAccount).edit().putLong("auto_lastSelectedDialog", parseLong).commit();
                            MediaController.getInstance().setPlaylist(arrayList, (MessageObject) arrayList.get(parseInt), 0, false, (MediaController.PlaylistGlobalSearchParams) null);
                            MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                            if (parseLong > 0) {
                                TLRPC$User tLRPC$User = (TLRPC$User) MusicBrowserService.this.users.get(parseLong);
                                if (tLRPC$User != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                                } else {
                                    MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                                }
                            } else {
                                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) MusicBrowserService.this.chats.get(-parseLong);
                                if (tLRPC$Chat != null) {
                                    MusicBrowserService.this.mediaSession.setQueueTitle(tLRPC$Chat.title);
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

        public void onPlayFromSearch(String str, Bundle bundle) {
            String str2;
            String str3;
            String str4;
            if (str != null && str.length() != 0) {
                String lowerCase = str.toLowerCase();
                for (int i = 0; i < MusicBrowserService.this.dialogs.size(); i++) {
                    long longValue = ((Long) MusicBrowserService.this.dialogs.get(i)).longValue();
                    if (DialogObject.isUserDialog(longValue)) {
                        TLRPC$User tLRPC$User = (TLRPC$User) MusicBrowserService.this.users.get(longValue);
                        if (tLRPC$User != null && (((str3 = tLRPC$User.first_name) != null && str3.startsWith(lowerCase)) || ((str4 = tLRPC$User.last_name) != null && str4.startsWith(lowerCase)))) {
                            onPlayFromMediaId(longValue + "_" + 0, (Bundle) null);
                            return;
                        }
                    } else {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) MusicBrowserService.this.chats.get(-longValue);
                        if (!(tLRPC$Chat == null || (str2 = tLRPC$Chat.title) == null || !str2.toLowerCase().contains(lowerCase))) {
                            onPlayFromMediaId(longValue + "_" + 0, (Bundle) null);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void updatePlaybackState(String str) {
        int i;
        int i2;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        long j = playingMessageObject != null ? ((long) playingMessageObject.audioProgressSec) * 1000 : -1;
        PlaybackState.Builder actions = new PlaybackState.Builder().setActions(getAvailableActions());
        if (playingMessageObject == null) {
            i = 1;
        } else if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            i = 6;
        } else {
            i = MediaController.getInstance().isMessagePaused() ? 2 : 3;
        }
        if (str != null) {
            actions.setErrorMessage(str);
            i2 = 7;
        } else {
            i2 = i;
        }
        actions.setState(i2, j, 1.0f, SystemClock.elapsedRealtime());
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
        return j | 16 | 32;
    }

    /* access modifiers changed from: private */
    public void handleStopRequest(String str) {
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
        updatePlaybackState(str);
        stopSelf();
        this.serviceStarted = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    /* access modifiers changed from: private */
    public void handlePlayRequest() {
        Bitmap cover;
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        if (!this.serviceStarted) {
            try {
                startService(new Intent(getApplicationContext(), MusicBrowserService.class));
            } catch (Throwable th) {
                FileLog.e(th);
            }
            this.serviceStarted = true;
        }
        if (!this.mediaSession.isActive()) {
            this.mediaSession.setActive(true);
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            MediaMetadata.Builder builder = new MediaMetadata.Builder();
            builder.putLong("android.media.metadata.DURATION", (long) (playingMessageObject.getDuration() * 1000));
            builder.putString("android.media.metadata.ARTIST", playingMessageObject.getMusicAuthor());
            builder.putString("android.media.metadata.TITLE", playingMessageObject.getMusicTitle());
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (!(audioInfo == null || (cover = audioInfo.getCover()) == null)) {
                builder.putBitmap("android.media.metadata.ALBUM_ART", cover);
            }
            this.mediaSession.setMetadata(builder.build());
        }
    }

    /* access modifiers changed from: private */
    public void handlePauseRequest() {
        MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
        this.delayedStopHandler.removeCallbacksAndMessages((Object) null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        updatePlaybackState((String) null);
        handlePlayRequest();
    }

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicBrowserService> mWeakReference;

        private DelayedStopHandler(MusicBrowserService musicBrowserService) {
            this.mWeakReference = new WeakReference<>(musicBrowserService);
        }

        public void handleMessage(Message message) {
            MusicBrowserService musicBrowserService = (MusicBrowserService) this.mWeakReference.get();
            if (musicBrowserService == null) {
                return;
            }
            if (MediaController.getInstance().getPlayingMessageObject() == null || MediaController.getInstance().isMessagePaused()) {
                musicBrowserService.stopSelf();
                boolean unused = musicBrowserService.serviceStarted = false;
            }
        }
    }
}
