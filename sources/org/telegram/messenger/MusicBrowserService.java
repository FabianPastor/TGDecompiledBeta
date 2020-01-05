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
import android.graphics.Shader.TileMode;
import android.media.MediaDescription.Builder;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSession.QueueItem;
import android.media.session.PlaybackState;
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
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
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
            String[] split = str.split("_");
            if (split.length == 2) {
                try {
                    int parseInt = Integer.parseInt(split[0]);
                    int parseInt2 = Integer.parseInt(split[1]);
                    ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(parseInt);
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(parseInt);
                    if (arrayList != null && parseInt2 >= 0) {
                        if (parseInt2 < arrayList.size()) {
                            MusicBrowserService.this.lastSelectedDialog = parseInt;
                            MessagesController.getNotificationsSettings(MusicBrowserService.this.currentAccount).edit().putInt("auto_lastSelectedDialog", parseInt).commit();
                            MediaController.getInstance().setPlaylist(arrayList, (MessageObject) arrayList.get(parseInt2), false);
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
                } catch (Exception e) {
                    FileLog.e(e);
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
            if (str != null && str.length() != 0) {
                str = str.toLowerCase();
                for (int i = 0; i < MusicBrowserService.this.dialogs.size(); i++) {
                    int intValue = ((Integer) MusicBrowserService.this.dialogs.get(i)).intValue();
                    String str2 = "_";
                    String str3;
                    StringBuilder stringBuilder;
                    if (intValue > 0) {
                        User user = (User) MusicBrowserService.this.users.get(intValue);
                        if (user == null) {
                            continue;
                        } else {
                            String str4 = user.first_name;
                            if (str4 == null || !str4.startsWith(str)) {
                                str3 = user.last_name;
                                if (str3 != null && str3.startsWith(str)) {
                                }
                            }
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(intValue);
                            stringBuilder.append(str2);
                            stringBuilder.append(0);
                            onPlayFromMediaId(stringBuilder.toString(), null);
                            return;
                        }
                    }
                    Chat chat = (Chat) MusicBrowserService.this.chats.get(-intValue);
                    if (chat == null) {
                        continue;
                    } else {
                        str3 = chat.title;
                        if (str3 != null && str3.toLowerCase().contains(str)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(intValue);
                            stringBuilder.append(str2);
                            stringBuilder.append(0);
                            onPlayFromMediaId(stringBuilder.toString(), null);
                            return;
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
        this.mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 99, new Intent(applicationContext, LaunchActivity.class), NUM));
        Bundle bundle = new Bundle();
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE", true);
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS", true);
        bundle.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT", true);
        this.mediaSession.setExtras(bundle);
        updatePlaybackState(null);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    public void onDestroy() {
        super.onDestroy();
        handleStopRequest(null);
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.mediaSession.release();
    }

    public BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
        if (str == null || (1000 != i && Process.myUid() != i && !str.equals("com.google.android.mediasimulator") && !str.equals("com.google.android.projection.gearhead"))) {
            return null;
        }
        return new BrowserRoot("__ROOT__", null);
    }

    public void onLoadChildren(String str, Result<List<MediaItem>> result) {
        if (this.chatsLoaded) {
            loadChildrenImpl(str, result);
        } else {
            result.detach();
            if (!this.loadingChats) {
                this.loadingChats = true;
                MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
                instance.getStorageQueue().postRunnable(new -$$Lambda$MusicBrowserService$iS7bPWX5pXtbCNrWxnzS-j3JBYQ(this, instance, str, result));
            }
        }
    }

    public /* synthetic */ void lambda$onLoadChildren$1$MusicBrowserService(MessagesStorage messagesStorage, String str, Result result) {
        MessagesStorage messagesStorage2 = messagesStorage;
        String str2 = ",";
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            SQLiteDatabase database = messagesStorage.getDatabase();
            Object[] objArr = new Object[1];
            int i = 0;
            objArr[0] = Integer.valueOf(4);
            SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v2 WHERE uid != 0 AND mid > 0 AND type = %d", objArr), new Object[0]);
            while (queryFinalized.next()) {
                int longValue = (int) queryFinalized.longValue(0);
                if (longValue != 0) {
                    this.dialogs.add(Integer.valueOf(longValue));
                    if (longValue > 0) {
                        arrayList.add(Integer.valueOf(longValue));
                    } else {
                        arrayList2.add(Integer.valueOf(-longValue));
                    }
                }
            }
            queryFinalized.dispose();
            if (!this.dialogs.isEmpty()) {
                String join = TextUtils.join(str2, this.dialogs);
                queryFinalized = messagesStorage.getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v2 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[]{join, Integer.valueOf(4)}), new Object[0]);
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                        byteBufferValue.reuse();
                        if (MessageObject.isMusicMessage(TLdeserialize)) {
                            int intValue = queryFinalized.intValue(0);
                            TLdeserialize.id = queryFinalized.intValue(2);
                            TLdeserialize.dialog_id = (long) intValue;
                            ArrayList arrayList3 = (ArrayList) this.musicObjects.get(intValue);
                            ArrayList arrayList4 = (ArrayList) this.musicQueues.get(intValue);
                            if (arrayList3 == null) {
                                arrayList3 = new ArrayList();
                                this.musicObjects.put(intValue, arrayList3);
                                arrayList4 = new ArrayList();
                                this.musicQueues.put(intValue, arrayList4);
                            }
                            MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false);
                            arrayList3.add(0, messageObject);
                            Builder builder = new Builder();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(intValue);
                            stringBuilder.append("_");
                            stringBuilder.append(arrayList3.size());
                            Builder mediaId = builder.setMediaId(stringBuilder.toString());
                            mediaId.setTitle(messageObject.getMusicTitle());
                            mediaId.setSubtitle(messageObject.getMusicAuthor());
                            arrayList4.add(0, new QueueItem(mediaId.build(), (long) arrayList4.size()));
                        }
                    }
                }
                queryFinalized.dispose();
                if (!arrayList.isEmpty()) {
                    ArrayList arrayList5 = new ArrayList();
                    messagesStorage2.getUsersInternal(TextUtils.join(str2, arrayList), arrayList5);
                    for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                        User user = (User) arrayList5.get(i2);
                        this.users.put(user.id, user);
                    }
                }
                if (!arrayList2.isEmpty()) {
                    arrayList = new ArrayList();
                    messagesStorage2.getChatsInternal(TextUtils.join(str2, arrayList2), arrayList);
                    while (i < arrayList.size()) {
                        Chat chat = (Chat) arrayList.get(i);
                        this.chats.put(chat.id, chat);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MusicBrowserService$kPFLpCLASSNAMEuxOMtsW9zhDqaIzSat8(this, str, result));
    }

    public /* synthetic */ void lambda$null$0$MusicBrowserService(String str, Result result) {
        this.chatsLoaded = true;
        this.loadingChats = false;
        loadChildrenImpl(str, result);
        if (this.lastSelectedDialog == 0 && !this.dialogs.isEmpty()) {
            this.lastSelectedDialog = ((Integer) this.dialogs.get(0)).intValue();
        }
        int i = this.lastSelectedDialog;
        if (i != 0) {
            ArrayList arrayList = (ArrayList) this.musicObjects.get(i);
            ArrayList arrayList2 = (ArrayList) this.musicQueues.get(this.lastSelectedDialog);
            if (!(arrayList == null || arrayList.isEmpty())) {
                this.mediaSession.setQueue(arrayList2);
                int i2 = this.lastSelectedDialog;
                if (i2 > 0) {
                    User user = (User) this.users.get(i2);
                    if (user != null) {
                        this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                    } else {
                        this.mediaSession.setQueueTitle("DELETED USER");
                    }
                } else {
                    Chat chat = (Chat) this.chats.get(-i2);
                    if (chat != null) {
                        this.mediaSession.setQueueTitle(chat.title);
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
        updatePlaybackState(null);
    }

    /* JADX WARNING: Missing block: B:11:0x005c, code skipped:
            if ((r8 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable) == false) goto L_0x0086;
     */
    /* JADX WARNING: Missing block: B:18:0x007d, code skipped:
            if ((r8 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable) == false) goto L_0x0086;
     */
    private void loadChildrenImpl(java.lang.String r8, android.service.media.MediaBrowserService.Result<java.util.List<android.media.browse.MediaBrowser.MediaItem>> r9) {
        /*
        r7 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = "__ROOT__";
        r1 = r1.equals(r8);
        r2 = "__CHAT_";
        r3 = 0;
        if (r1 == 0) goto L_0x00cf;
    L_0x0010:
        r8 = r7.dialogs;
        r8 = r8.size();
        if (r3 >= r8) goto L_0x0138;
    L_0x0018:
        r8 = r7.dialogs;
        r8 = r8.get(r3);
        r8 = (java.lang.Integer) r8;
        r8 = r8.intValue();
        r1 = new android.media.MediaDescription$Builder;
        r1.<init>();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r8);
        r4 = r4.toString();
        r1 = r1.setMediaId(r4);
        r4 = 0;
        if (r8 <= 0) goto L_0x0065;
    L_0x003f:
        r5 = r7.users;
        r8 = r5.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.User) r8;
        if (r8 == 0) goto L_0x005f;
    L_0x0049:
        r5 = r8.first_name;
        r6 = r8.last_name;
        r5 = org.telegram.messenger.ContactsController.formatName(r5, r6);
        r1.setTitle(r5);
        r8 = r8.photo;
        if (r8 == 0) goto L_0x0085;
    L_0x0058:
        r8 = r8.photo_small;
        r5 = r8 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r5 != 0) goto L_0x0085;
    L_0x005e:
        goto L_0x0086;
    L_0x005f:
        r8 = "DELETED USER";
        r1.setTitle(r8);
        goto L_0x0085;
    L_0x0065:
        r5 = r7.chats;
        r8 = -r8;
        r8 = r5.get(r8);
        r8 = (org.telegram.tgnet.TLRPC.Chat) r8;
        if (r8 == 0) goto L_0x0080;
    L_0x0070:
        r5 = r8.title;
        r1.setTitle(r5);
        r8 = r8.photo;
        if (r8 == 0) goto L_0x0085;
    L_0x0079:
        r8 = r8.photo_small;
        r5 = r8 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r5 != 0) goto L_0x0085;
    L_0x007f:
        goto L_0x0086;
    L_0x0080:
        r8 = "DELETED CHAT";
        r1.setTitle(r8);
    L_0x0085:
        r8 = r4;
    L_0x0086:
        r5 = 1;
        if (r8 == 0) goto L_0x0096;
    L_0x0089:
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r5);
        r4 = r7.createRoundBitmap(r4);
        if (r4 == 0) goto L_0x0096;
    L_0x0093:
        r1.setIconBitmap(r4);
    L_0x0096:
        if (r8 == 0) goto L_0x009a;
    L_0x0098:
        if (r4 != 0) goto L_0x00bf;
    L_0x009a:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r4 = "android.resource://";
        r8.append(r4);
        r4 = r7.getApplicationContext();
        r4 = r4.getPackageName();
        r8.append(r4);
        r4 = "/drawable/contact_blue";
        r8.append(r4);
        r8 = r8.toString();
        r8 = android.net.Uri.parse(r8);
        r1.setIconUri(r8);
    L_0x00bf:
        r8 = new android.media.browse.MediaBrowser$MediaItem;
        r1 = r1.build();
        r8.<init>(r1, r5);
        r0.add(r8);
        r3 = r3 + 1;
        goto L_0x0010;
    L_0x00cf:
        if (r8 == 0) goto L_0x0138;
    L_0x00d1:
        r1 = r8.startsWith(r2);
        if (r1 == 0) goto L_0x0138;
    L_0x00d7:
        r1 = "";
        r8 = r8.replace(r2, r1);	 Catch:{ Exception -> 0x00e2 }
        r8 = java.lang.Integer.parseInt(r8);	 Catch:{ Exception -> 0x00e2 }
        goto L_0x00e7;
    L_0x00e2:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        r8 = 0;
    L_0x00e7:
        r1 = r7.musicObjects;
        r1 = r1.get(r8);
        r1 = (java.util.ArrayList) r1;
        if (r1 == 0) goto L_0x0138;
    L_0x00f1:
        r2 = r1.size();
        if (r3 >= r2) goto L_0x0138;
    L_0x00f7:
        r2 = r1.get(r3);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r4 = new android.media.MediaDescription$Builder;
        r4.<init>();
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r8);
        r6 = "_";
        r5.append(r6);
        r5.append(r3);
        r5 = r5.toString();
        r4 = r4.setMediaId(r5);
        r5 = r2.getMusicTitle();
        r4.setTitle(r5);
        r2 = r2.getMusicAuthor();
        r4.setSubtitle(r2);
        r2 = new android.media.browse.MediaBrowser$MediaItem;
        r4 = r4.build();
        r5 = 2;
        r2.<init>(r4, r5);
        r0.add(r2);
        r3 = r3 + 1;
        goto L_0x00f1;
    L_0x0138:
        r9.sendResult(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MusicBrowserService.loadChildrenImpl(java.lang.String, android.service.media.MediaBrowserService$Result):void");
    }

    private Bitmap createRoundBitmap(File file) {
        try {
            Options options = new Options();
            options.inSampleSize = 2;
            Bitmap decodeFile = BitmapFactory.decodeFile(file.toString(), options);
            if (decodeFile != null) {
                Bitmap createBitmap = Bitmap.createBitmap(decodeFile.getWidth(), decodeFile.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                BitmapShader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), this.roundPaint);
                return createBitmap;
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        return null;
    }

    private void updatePlaybackState(String str) {
        int i;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        long j = playingMessageObject != null ? ((long) playingMessageObject.audioProgressSec) * 1000 : -1;
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    }

    private void handlePlayRequest() {
        this.delayedStopHandler.removeCallbacksAndMessages(null);
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
        MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        updatePlaybackState(null);
        handlePlayRequest();
    }
}
