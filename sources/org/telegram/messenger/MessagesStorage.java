package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage {
    private static volatile MessagesStorage[] Instance = new MessagesStorage[3];
    private File cacheFile;
    private int currentAccount;
    private SQLiteDatabase database;
    private int lastDateValue = 0;
    private int lastPtsValue = 0;
    private int lastQtsValue = 0;
    private int lastSavedDate = 0;
    private int lastSavedPts = 0;
    private int lastSavedQts = 0;
    private int lastSavedSeq = 0;
    private int lastSecretVersion = 0;
    private int lastSeqValue = 0;
    private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
    private CountDownLatch openSync = new CountDownLatch(1);
    private int secretG = 0;
    private byte[] secretPBytes = null;
    private File shmCacheFile;
    private DispatchQueue storageQueue = new DispatchQueue("storageQueue");
    private File walCacheFile;

    /* renamed from: org.telegram.messenger.MessagesStorage$1 */
    class C03751 implements Runnable {
        C03751() {
        }

        public void run() {
            MessagesStorage.this.openDatabase(true);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesStorage$5 */
    class C03935 implements Runnable {
        C03935() {
        }

        public void run() {
            try {
                LongSparseArray longSparseArray = new LongSparseArray();
                Map all = MessagesController.getNotificationsSettings(MessagesStorage.this.currentAccount).getAll();
                for (Entry entry : all.entrySet()) {
                    String str = (String) entry.getKey();
                    if (str.startsWith("notify2_")) {
                        Integer num = (Integer) entry.getValue();
                        if (num.intValue() != 2) {
                            if (num.intValue() != 3) {
                                num.intValue();
                            }
                        }
                        str = str.replace("notify2_", TtmlNode.ANONYMOUS_REGION_ID);
                        long j = 1;
                        if (num.intValue() != 2) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("notifyuntil_");
                            stringBuilder.append(str);
                            num = (Integer) all.get(stringBuilder.toString());
                            if (num != null) {
                                j = (((long) num.intValue()) << 32) | 1;
                            }
                        }
                        longSparseArray.put(Long.parseLong(str), Long.valueOf(j));
                    }
                }
                try {
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                    for (int i = 0; i < longSparseArray.size(); i++) {
                        executeFast.requery();
                        executeFast.bindLong(1, longSparseArray.keyAt(i));
                        executeFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                        executeFast.step();
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } catch (Throwable e3) {
                FileLog.m3e(e3);
            }
        }
    }

    /* renamed from: org.telegram.messenger.MessagesStorage$8 */
    class C04068 implements Runnable {
        C04068() {
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
                while (queryFinalized.next()) {
                    final long longValue = queryFinalized.longValue(0);
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                    if (byteBufferValue != null) {
                        int readInt32 = byteBufferValue.readInt32(false);
                        final int readInt322;
                        final int readInt323;
                        final long j;
                        switch (readInt32) {
                            case 0:
                                final Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize != null) {
                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(MessagesStorage.this.currentAccount).loadUnknownChannel(TLdeserialize, longValue);
                                        }
                                    });
                                    break;
                                }
                                break;
                            case 1:
                                readInt322 = byteBufferValue.readInt32(false);
                                readInt323 = byteBufferValue.readInt32(false);
                                j = longValue;
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(MessagesStorage.this.currentAccount).getChannelDifference(readInt322, readInt323, j, null);
                                    }
                                });
                                break;
                            case 2:
                            case 5:
                            case 8:
                                final TL_dialog tL_dialog = new TL_dialog();
                                tL_dialog.id = byteBufferValue.readInt64(false);
                                tL_dialog.top_message = byteBufferValue.readInt32(false);
                                tL_dialog.read_inbox_max_id = byteBufferValue.readInt32(false);
                                tL_dialog.read_outbox_max_id = byteBufferValue.readInt32(false);
                                tL_dialog.unread_count = byteBufferValue.readInt32(false);
                                tL_dialog.last_message_date = byteBufferValue.readInt32(false);
                                tL_dialog.pts = byteBufferValue.readInt32(false);
                                tL_dialog.flags = byteBufferValue.readInt32(false);
                                if (readInt32 >= 5) {
                                    tL_dialog.pinned = byteBufferValue.readBool(false);
                                    tL_dialog.pinnedNum = byteBufferValue.readInt32(false);
                                }
                                if (readInt32 >= 8) {
                                    tL_dialog.unread_mentions_count = byteBufferValue.readInt32(false);
                                }
                                final InputPeer TLdeserialize2 = InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                j = longValue;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(MessagesStorage.this.currentAccount).checkLastDialogMessage(tL_dialog, TLdeserialize2, j);
                                    }
                                });
                                break;
                            case 3:
                                SendMessagesHelper.getInstance(MessagesStorage.this.currentAccount).sendGame(InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), (TL_inputMediaGame) InputMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), byteBufferValue.readInt64(false), longValue);
                                break;
                            case 4:
                                final long readInt64 = byteBufferValue.readInt64(false);
                                final boolean readBool = byteBufferValue.readBool(false);
                                final InputPeer TLdeserialize3 = InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(MessagesStorage.this.currentAccount).pinDialog(readInt64, readBool, TLdeserialize3, longValue);
                                    }
                                });
                                break;
                            case 6:
                                readInt322 = byteBufferValue.readInt32(false);
                                readInt323 = byteBufferValue.readInt32(false);
                                j = longValue;
                                final InputChannel TLdeserialize4 = InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(MessagesStorage.this.currentAccount).getChannelDifference(readInt322, readInt323, j, TLdeserialize4);
                                    }
                                });
                                break;
                            case 7:
                                readInt322 = byteBufferValue.readInt32(false);
                                readInt32 = byteBufferValue.readInt32(false);
                                TLObject TLdeserialize5 = TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt32, false);
                                final TLObject TLdeserialize6 = TLdeserialize5 == null ? TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt32, false) : TLdeserialize5;
                                if (TLdeserialize6 != null) {
                                    final long j2 = longValue;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(MessagesStorage.this.currentAccount).deleteMessages(null, null, null, readInt322, true, j2, TLdeserialize6);
                                        }
                                    });
                                    break;
                                }
                                MessagesStorage.this.removePendingTask(longValue);
                                break;
                            default:
                                break;
                        }
                        byteBufferValue.reuse();
                    }
                }
                queryFinalized.dispose();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public Hole(int i, int i2, int i3) {
            this.type = i;
            this.start = i2;
            this.end = i3;
        }
    }

    public interface IntCallback {
        void run(int i);
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage = Instance[i];
        if (messagesStorage == null) {
            synchronized (MessagesStorage.class) {
                messagesStorage = Instance[i];
                if (messagesStorage == null) {
                    MessagesStorage[] messagesStorageArr = Instance;
                    MessagesStorage messagesStorage2 = new MessagesStorage(i);
                    messagesStorageArr[i] = messagesStorage2;
                    messagesStorage = messagesStorage2;
                }
            }
        }
        return messagesStorage;
    }

    private void ensureOpened() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = this;
        r0 = r1.openSync;	 Catch:{ Throwable -> 0x0005 }
        r0.await();	 Catch:{ Throwable -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.ensureOpened():void");
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int i) {
        ensureOpened();
        this.lastDateValue = i;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public void setLastPtsValue(int i) {
        ensureOpened();
        this.lastPtsValue = i;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int i) {
        ensureOpened();
        this.lastQtsValue = i;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int i) {
        ensureOpened();
        this.lastSeqValue = i;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int i) {
        ensureOpened();
        this.lastSecretVersion = i;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] bArr) {
        ensureOpened();
        this.secretPBytes = bArr;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int i) {
        ensureOpened();
        this.secretG = i;
    }

    public MessagesStorage(int i) {
        this.currentAccount = i;
        this.storageQueue.postRunnable(new C03751());
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public long getDatabaseSize() {
        long j = 0;
        if (this.cacheFile != null) {
            j = 0 + this.cacheFile.length();
        }
        return this.shmCacheFile != null ? j + this.shmCacheFile.length() : j;
    }

    public void openDatabase(boolean r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r7 = this;
        r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r1 = r7.currentAccount;
        if (r1 == 0) goto L_0x0029;
    L_0x0008:
        r1 = new java.io.File;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "account";
        r2.append(r3);
        r3 = r7.currentAccount;
        r2.append(r3);
        r3 = "/";
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r0, r2);
        r1.mkdirs();
        r0 = r1;
    L_0x0029:
        r1 = new java.io.File;
        r2 = "cache4.db";
        r1.<init>(r0, r2);
        r7.cacheFile = r1;
        r1 = new java.io.File;
        r2 = "cache4.db-wal";
        r1.<init>(r0, r2);
        r7.walCacheFile = r1;
        r1 = new java.io.File;
        r2 = "cache4.db-shm";
        r1.<init>(r0, r2);
        r7.shmCacheFile = r1;
        r0 = r7.cacheFile;
        r0 = r0.exists();
        r1 = 1;
        r0 = r0 ^ r1;
        r2 = 0;
        r3 = new org.telegram.SQLite.SQLiteDatabase;	 Catch:{ Exception -> 0x0538 }
        r4 = r7.cacheFile;	 Catch:{ Exception -> 0x0538 }
        r4 = r4.getPath();	 Catch:{ Exception -> 0x0538 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x0538 }
        r7.database = r3;	 Catch:{ Exception -> 0x0538 }
        r3 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r4 = "PRAGMA secure_delete = ON";	 Catch:{ Exception -> 0x0538 }
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0538 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x0538 }
        r3.dispose();	 Catch:{ Exception -> 0x0538 }
        r3 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r4 = "PRAGMA temp_store = 1";	 Catch:{ Exception -> 0x0538 }
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0538 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x0538 }
        r3.dispose();	 Catch:{ Exception -> 0x0538 }
        r3 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r4 = "PRAGMA journal_mode = WAL";	 Catch:{ Exception -> 0x0538 }
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0538 }
        r3 = r3.stepThis();	 Catch:{ Exception -> 0x0538 }
        r3.dispose();	 Catch:{ Exception -> 0x0538 }
        if (r0 == 0) goto L_0x0481;	 Catch:{ Exception -> 0x0538 }
    L_0x0089:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0538 }
        if (r0 == 0) goto L_0x0092;	 Catch:{ Exception -> 0x0538 }
    L_0x008d:
        r0 = "create new database";	 Catch:{ Exception -> 0x0538 }
        org.telegram.messenger.FileLog.m0d(r0);	 Catch:{ Exception -> 0x0538 }
    L_0x0092:
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r1 = "PRAGMA user_version = 47";	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeFast(r1);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0538 }
        r0.dispose();	 Catch:{ Exception -> 0x0538 }
        goto L_0x0593;	 Catch:{ Exception -> 0x0538 }
    L_0x0481:
        r0 = r7.database;	 Catch:{ Exception -> 0x0538 }
        r3 = "PRAGMA user_version";	 Catch:{ Exception -> 0x0538 }
        r4 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0538 }
        r0 = r0.executeInt(r3, r4);	 Catch:{ Exception -> 0x0538 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0538 }
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0538 }
        if (r3 == 0) goto L_0x04a7;	 Catch:{ Exception -> 0x0538 }
    L_0x0493:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0538 }
        r3.<init>();	 Catch:{ Exception -> 0x0538 }
        r4 = "current db version = ";	 Catch:{ Exception -> 0x0538 }
        r3.append(r4);	 Catch:{ Exception -> 0x0538 }
        r3.append(r0);	 Catch:{ Exception -> 0x0538 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0538 }
        org.telegram.messenger.FileLog.m0d(r3);	 Catch:{ Exception -> 0x0538 }
    L_0x04a7:
        if (r0 != 0) goto L_0x04b1;	 Catch:{ Exception -> 0x0538 }
    L_0x04a9:
        r0 = new java.lang.Exception;	 Catch:{ Exception -> 0x0538 }
        r1 = "malformed";	 Catch:{ Exception -> 0x0538 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0538 }
        throw r0;	 Catch:{ Exception -> 0x0538 }
    L_0x04b1:
        r3 = r7.database;	 Catch:{ Exception -> 0x0509 }
        r4 = "SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1";	 Catch:{ Exception -> 0x0509 }
        r5 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0509 }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0509 }
        r4 = r3.next();	 Catch:{ Exception -> 0x0509 }
        if (r4 == 0) goto L_0x0505;	 Catch:{ Exception -> 0x0509 }
    L_0x04c1:
        r4 = r3.intValue(r2);	 Catch:{ Exception -> 0x0509 }
        r7.lastSeqValue = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = r3.intValue(r1);	 Catch:{ Exception -> 0x0509 }
        r7.lastPtsValue = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = 2;	 Catch:{ Exception -> 0x0509 }
        r4 = r3.intValue(r4);	 Catch:{ Exception -> 0x0509 }
        r7.lastDateValue = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = 3;	 Catch:{ Exception -> 0x0509 }
        r4 = r3.intValue(r4);	 Catch:{ Exception -> 0x0509 }
        r7.lastQtsValue = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = 4;	 Catch:{ Exception -> 0x0509 }
        r4 = r3.intValue(r4);	 Catch:{ Exception -> 0x0509 }
        r7.lastSecretVersion = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = 5;	 Catch:{ Exception -> 0x0509 }
        r4 = r3.intValue(r4);	 Catch:{ Exception -> 0x0509 }
        r7.secretG = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = 6;	 Catch:{ Exception -> 0x0509 }
        r5 = r3.isNull(r4);	 Catch:{ Exception -> 0x0509 }
        r6 = 0;	 Catch:{ Exception -> 0x0509 }
        if (r5 == 0) goto L_0x04f4;	 Catch:{ Exception -> 0x0509 }
    L_0x04f1:
        r7.secretPBytes = r6;	 Catch:{ Exception -> 0x0509 }
        goto L_0x0505;	 Catch:{ Exception -> 0x0509 }
    L_0x04f4:
        r4 = r3.byteArrayValue(r4);	 Catch:{ Exception -> 0x0509 }
        r7.secretPBytes = r4;	 Catch:{ Exception -> 0x0509 }
        r4 = r7.secretPBytes;	 Catch:{ Exception -> 0x0509 }
        if (r4 == 0) goto L_0x0505;	 Catch:{ Exception -> 0x0509 }
    L_0x04fe:
        r4 = r7.secretPBytes;	 Catch:{ Exception -> 0x0509 }
        r4 = r4.length;	 Catch:{ Exception -> 0x0509 }
        if (r4 != r1) goto L_0x0505;	 Catch:{ Exception -> 0x0509 }
    L_0x0503:
        r7.secretPBytes = r6;	 Catch:{ Exception -> 0x0509 }
    L_0x0505:
        r3.dispose();	 Catch:{ Exception -> 0x0509 }
        goto L_0x0530;
    L_0x0509:
        r1 = move-exception;
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x0538 }
        r1 = r7.database;	 Catch:{ Exception -> 0x052c }
        r3 = "CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)";	 Catch:{ Exception -> 0x052c }
        r1 = r1.executeFast(r3);	 Catch:{ Exception -> 0x052c }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x052c }
        r1.dispose();	 Catch:{ Exception -> 0x052c }
        r1 = r7.database;	 Catch:{ Exception -> 0x052c }
        r3 = "INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)";	 Catch:{ Exception -> 0x052c }
        r1 = r1.executeFast(r3);	 Catch:{ Exception -> 0x052c }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x052c }
        r1.dispose();	 Catch:{ Exception -> 0x052c }
        goto L_0x0530;
    L_0x052c:
        r1 = move-exception;
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x0538 }
    L_0x0530:
        r1 = 47;	 Catch:{ Exception -> 0x0538 }
        if (r0 >= r1) goto L_0x0593;	 Catch:{ Exception -> 0x0538 }
    L_0x0534:
        r7.updateDbToLastVersion(r0);	 Catch:{ Exception -> 0x0538 }
        goto L_0x0593;
    L_0x0538:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
        if (r8 == 0) goto L_0x0593;
    L_0x053e:
        r8 = r0.getMessage();
        r0 = "malformed";
        r8 = r8.contains(r0);
        if (r8 == 0) goto L_0x0593;
    L_0x054a:
        r7.cleanupInternal();
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.dialogsLoadOffsetId = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.totalDialogsLoadCount = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.dialogsLoadOffsetDate = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.dialogsLoadOffsetUserId = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.dialogsLoadOffsetChatId = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.dialogsLoadOffsetChannelId = r2;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r0 = 0;
        r8.dialogsLoadOffsetAccess = r0;
        r8 = r7.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8.saveConfig(r2);
        r7.openDatabase(r2);
    L_0x0593:
        r7.loadUnreadMessages();
        r7.loadPendingTasks();
        r8 = r7.openSync;	 Catch:{ Throwable -> 0x059e }
        r8.countDown();	 Catch:{ Throwable -> 0x059e }
    L_0x059e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.openDatabase(boolean):void");
    }

    private void updateDbToLastVersion(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized;
                    int i = i;
                    if (i < 4) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                        MessagesStorage.this.fixNotificationSettings();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                        i = 4;
                    }
                    if (i == 4) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                        MessagesStorage.this.database.beginTransaction();
                        queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                        if (queryFinalized.next()) {
                            int intValue = queryFinalized.intValue(0);
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                int limit = byteBufferValue.limit();
                                for (int i2 = 0; i2 < limit / 4; i2++) {
                                    executeFast.requery();
                                    executeFast.bindInteger(1, byteBufferValue.readInt32(false));
                                    executeFast.bindInteger(2, intValue);
                                    executeFast.step();
                                }
                                byteBufferValue.reuse();
                            }
                        }
                        executeFast.dispose();
                        queryFinalized.dispose();
                        MessagesStorage.this.database.commitTransaction();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
                        i = 6;
                    }
                    if (i == 6) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
                        i = 7;
                    }
                    if (i == 7 || i == 8 || i == 9) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
                        i = 10;
                    }
                    if (i == 10) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
                        i = 11;
                    }
                    if (i == 11 || i == 12) {
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
                        i = 13;
                    }
                    if (i == 13) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
                        i = 14;
                    }
                    if (i == 14) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
                        i = 15;
                    }
                    if (i == 15) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
                        i = 16;
                    }
                    if (i == 16) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
                        i = 17;
                    }
                    if (i == 17) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
                        i = 18;
                    }
                    if (i == 18) {
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
                        i = 19;
                    }
                    if (i == 19) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
                        i = 20;
                    }
                    if (i == 20) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
                        i = 21;
                    }
                    if (i == 21) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                        queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
                        SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
                        while (queryFinalized.next()) {
                            int intValue2 = queryFinalized.intValue(0);
                            AbstractSerializedData byteBufferValue2 = queryFinalized.byteBufferValue(1);
                            if (byteBufferValue2 != null) {
                                ChatParticipants TLdeserialize = ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                byteBufferValue2.reuse();
                                if (TLdeserialize != null) {
                                    TL_chatFull tL_chatFull = new TL_chatFull();
                                    tL_chatFull.id = intValue2;
                                    tL_chatFull.chat_photo = new TL_photoEmpty();
                                    tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
                                    tL_chatFull.exported_invite = new TL_chatInviteEmpty();
                                    tL_chatFull.participants = TLdeserialize;
                                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_chatFull.getObjectSize());
                                    tL_chatFull.serializeToStream(nativeByteBuffer);
                                    executeFast2.requery();
                                    executeFast2.bindInteger(1, intValue2);
                                    executeFast2.bindByteBuffer(2, nativeByteBuffer);
                                    executeFast2.step();
                                    nativeByteBuffer.reuse();
                                }
                            }
                        }
                        executeFast2.dispose();
                        queryFinalized.dispose();
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
                        i = 22;
                    }
                    if (i == 22) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
                        i = 23;
                    }
                    if (i == 23 || i == 24) {
                        MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
                        i = 25;
                    }
                    if (i == 25 || i == 26) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
                        i = 27;
                    }
                    if (i == 27) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
                        i = 28;
                    }
                    if (i == 28 || i == 29) {
                        MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
                        i = 30;
                    }
                    if (i == 30) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
                        i = 31;
                    }
                    if (i == 31) {
                        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
                        i = 32;
                    }
                    if (i == 32) {
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
                        i = 33;
                    }
                    if (i == 33) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
                        i = 34;
                    }
                    if (i == 34) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
                        i = 35;
                    }
                    if (i == 35) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
                        i = 36;
                    }
                    if (i == 36) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
                        i = 37;
                    }
                    if (i == 37) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
                        i = 38;
                    }
                    if (i == 38) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
                        i = 39;
                    }
                    if (i == 39) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
                        i = 40;
                    }
                    if (i == 40) {
                        MessagesStorage.this.fixNotificationSettings();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
                        i = 41;
                    }
                    if (i == 41) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
                        i = 42;
                    }
                    if (i == 42) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
                        i = 43;
                    }
                    if (i == 43) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
                        i = 44;
                    }
                    if (i == 44) {
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
                        i = 45;
                    }
                    if (i == 45) {
                        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
                        i = 46;
                    }
                    if (i == 46) {
                        MessagesStorage.this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
                        MessagesStorage.this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void cleanupInternal() {
        this.lastDateValue = 0;
        this.lastSeqValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSecretVersion = 0;
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        if (this.database != null) {
            this.database.close();
            this.database = null;
        }
        if (this.cacheFile != null) {
            this.cacheFile.delete();
            this.cacheFile = null;
        }
        if (this.walCacheFile != null) {
            this.walCacheFile.delete();
            this.walCacheFile = null;
        }
        if (this.shmCacheFile != null) {
            this.shmCacheFile.delete();
            this.shmCacheFile = null;
        }
    }

    public void cleanup(final boolean z) {
        this.storageQueue.cleanupQueue();
        this.storageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesStorage$3$1 */
            class C03831 implements Runnable {
                C03831() {
                }

                public void run() {
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).getDifference();
                }
            }

            public void run() {
                MessagesStorage.this.cleanupInternal();
                MessagesStorage.this.openDatabase(false);
                if (z) {
                    Utilities.stageQueue.postRunnable(new C03831());
                }
            }
        });
    }

    public void saveSecretParams(final int i, final int i2, final byte[] bArr) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
                    int i = 1;
                    executeFast.bindInteger(1, i);
                    executeFast.bindInteger(2, i2);
                    if (bArr != null) {
                        i = bArr.length;
                    }
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i);
                    if (bArr != null) {
                        nativeByteBuffer.writeBytes(bArr);
                    }
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new C03935());
    }

    public long createPendingTask(final NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0;
        }
        final long andAdd = this.lastTaskId.getAndAdd(1);
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                    executeFast.bindLong(1, andAdd);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                } catch (Throwable th) {
                    nativeByteBuffer.reuse();
                }
                nativeByteBuffer.reuse();
            }
        });
        return andAdd;
    }

    public void removePendingTask(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM pending_tasks WHERE id = ");
                    stringBuilder.append(j);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new C04068());
    }

    public void saveChannelPts(final int i, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
                    executeFast.bindInteger(1, i2);
                    executeFast.bindInteger(2, -i);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void saveDiffParamsInternal(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq != i || this.lastSavedPts != i2 || this.lastSavedDate != i3 || this.lastQtsValue != i4) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, i2);
                executeFast.bindInteger(3, i3);
                executeFast.bindInteger(4, i4);
                executeFast.step();
                executeFast.dispose();
                this.lastSavedSeq = i;
                this.lastSavedPts = i2;
                this.lastSavedDate = i3;
                this.lastSavedQts = i4;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void saveDiffParams(int i, int i2, int i3, int i4) {
        final int i5 = i;
        final int i6 = i2;
        final int i7 = i3;
        final int i8 = i4;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesStorage.this.saveDiffParamsInternal(i5, i6, i7, i8);
            }
        });
    }

    public void setDialogFlags(long j, long j2) {
        final long j3 = j;
        final long j4 = j2;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(j3), Long.valueOf(j4)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                ArrayList arrayList;
                ArrayList arrayList2;
                ArrayList arrayList3;
                ArrayList arrayList4;
                Throwable e;
                AnonymousClass12 anonymousClass12 = this;
                try {
                    long longValue;
                    ArrayList arrayList5;
                    ArrayList arrayList6;
                    ArrayList arrayList7;
                    List list;
                    Iterable arrayList8 = new ArrayList();
                    Iterable arrayList9 = new ArrayList();
                    Iterable arrayList10 = new ArrayList();
                    LongSparseArray longSparseArray = new LongSparseArray();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    int currentTime = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
                    while (queryFinalized.next()) {
                        longValue = queryFinalized.longValue(2);
                        int i = (longValue & 1) != 0 ? 1 : 0;
                        int i2 = (int) (longValue >> 32);
                        if (queryFinalized.isNull(2) || i == 0 || (i2 != 0 && i2 < currentTime)) {
                            longValue = queryFinalized.longValue(0);
                            longSparseArray.put(longValue, Integer.valueOf(queryFinalized.intValue(1)));
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(longValue);
                            i = (int) longValue;
                            int i3 = (int) (longValue >> 32);
                            if (i != 0) {
                                if (i < 0) {
                                    i = -i;
                                    if (!arrayList9.contains(Integer.valueOf(i))) {
                                        arrayList9.add(Integer.valueOf(i));
                                    }
                                } else if (!arrayList8.contains(Integer.valueOf(i))) {
                                    arrayList8.add(Integer.valueOf(i));
                                }
                            } else if (!arrayList10.contains(Integer.valueOf(i3))) {
                                arrayList10.add(Integer.valueOf(i3));
                            }
                        }
                    }
                    queryFinalized.dispose();
                    Iterable arrayList11 = new ArrayList();
                    SparseArray sparseArray = new SparseArray();
                    ArrayList arrayList12 = new ArrayList();
                    ArrayList arrayList13 = new ArrayList();
                    ArrayList arrayList14 = new ArrayList();
                    ArrayList arrayList15 = new ArrayList();
                    if (stringBuilder.length() > 0) {
                        Message TLdeserialize;
                        int i4;
                        Message message;
                        SQLiteDatabase access$000 = MessagesStorage.this.database;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (");
                        stringBuilder2.append(stringBuilder.toString());
                        stringBuilder2.append(") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50");
                        SQLiteCursor queryFinalized2 = access$000.queryFinalized(stringBuilder2.toString(), new Object[0]);
                        while (queryFinalized2.next()) {
                            AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                byteBufferValue.reuse();
                                MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(0));
                                TLdeserialize.id = queryFinalized2.intValue(3);
                                TLdeserialize.date = queryFinalized2.intValue(4);
                                arrayList = arrayList13;
                                arrayList2 = arrayList14;
                                TLdeserialize.dialog_id = queryFinalized2.longValue(5);
                                arrayList12.add(TLdeserialize);
                                i4 = (int) TLdeserialize.dialog_id;
                                MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList8, arrayList9);
                                TLdeserialize.send_state = queryFinalized2.intValue(2);
                                if (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i4 == 0) || TLdeserialize.id > 0) {
                                    TLdeserialize.send_state = 0;
                                }
                                if (i4 == 0 && !queryFinalized2.isNull(5)) {
                                    TLdeserialize.random_id = queryFinalized2.longValue(5);
                                }
                                try {
                                    if (TLdeserialize.reply_to_msg_id != 0 && ((TLdeserialize.action instanceof TL_messageActionPinMessage) || (TLdeserialize.action instanceof TL_messageActionPaymentSent) || (TLdeserialize.action instanceof TL_messageActionGameScore))) {
                                        if (!queryFinalized2.isNull(6)) {
                                            AbstractSerializedData byteBufferValue2 = queryFinalized2.byteBufferValue(6);
                                            if (byteBufferValue2 != null) {
                                                TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                                TLdeserialize.replyMessage.readAttachPath(byteBufferValue2, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                                byteBufferValue2.reuse();
                                                if (TLdeserialize.replyMessage != null) {
                                                    if (MessageObject.isMegagroup(TLdeserialize)) {
                                                        message = TLdeserialize.replyMessage;
                                                        message.flags |= Integer.MIN_VALUE;
                                                    }
                                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList8, arrayList9);
                                                }
                                            }
                                        }
                                        if (TLdeserialize.replyMessage == null) {
                                            longValue = (long) TLdeserialize.reply_to_msg_id;
                                            if (TLdeserialize.to_id.channel_id != 0) {
                                                arrayList3 = arrayList12;
                                                arrayList4 = arrayList15;
                                                longValue |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                            } else {
                                                arrayList3 = arrayList12;
                                                arrayList4 = arrayList15;
                                            }
                                            try {
                                                if (!arrayList11.contains(Long.valueOf(longValue))) {
                                                    arrayList11.add(Long.valueOf(longValue));
                                                }
                                                arrayList12 = (ArrayList) sparseArray.get(TLdeserialize.reply_to_msg_id);
                                                if (arrayList12 == null) {
                                                    arrayList12 = new ArrayList();
                                                    sparseArray.put(TLdeserialize.reply_to_msg_id, arrayList12);
                                                }
                                                arrayList12.add(TLdeserialize);
                                            } catch (Exception e2) {
                                                e = e2;
                                                FileLog.m3e(e);
                                                arrayList13 = arrayList;
                                                arrayList14 = arrayList2;
                                                arrayList12 = arrayList3;
                                                arrayList15 = arrayList4;
                                            }
                                            arrayList13 = arrayList;
                                            arrayList14 = arrayList2;
                                            arrayList12 = arrayList3;
                                            arrayList15 = arrayList4;
                                        }
                                    }
                                    arrayList3 = arrayList12;
                                    arrayList4 = arrayList15;
                                } catch (Exception e3) {
                                    e = e3;
                                    arrayList3 = arrayList12;
                                    arrayList4 = arrayList15;
                                    FileLog.m3e(e);
                                    arrayList13 = arrayList;
                                    arrayList14 = arrayList2;
                                    arrayList12 = arrayList3;
                                    arrayList15 = arrayList4;
                                }
                            } else {
                                arrayList3 = arrayList12;
                                arrayList4 = arrayList15;
                                arrayList = arrayList13;
                                arrayList2 = arrayList14;
                            }
                            arrayList13 = arrayList;
                            arrayList14 = arrayList2;
                            arrayList12 = arrayList3;
                            arrayList15 = arrayList4;
                        }
                        arrayList3 = arrayList12;
                        arrayList4 = arrayList15;
                        arrayList = arrayList13;
                        arrayList2 = arrayList14;
                        queryFinalized2.dispose();
                        if (!arrayList11.isEmpty()) {
                            SQLiteDatabase access$0002 = MessagesStorage.this.database;
                            Object[] objArr = new Object[1];
                            boolean z = false;
                            objArr[0] = TextUtils.join(",", arrayList11);
                            queryFinalized = access$0002.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", objArr), new Object[0]);
                            while (queryFinalized.next()) {
                                AbstractSerializedData byteBufferValue3 = queryFinalized.byteBufferValue(z);
                                if (byteBufferValue3 != null) {
                                    message = Message.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(z), z);
                                    message.readAttachPath(byteBufferValue3, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                    byteBufferValue3.reuse();
                                    message.id = queryFinalized.intValue(1);
                                    message.date = queryFinalized.intValue(2);
                                    message.dialog_id = queryFinalized.longValue(3);
                                    MessagesStorage.addUsersAndChatsFromMessage(message, arrayList8, arrayList9);
                                    arrayList15 = (ArrayList) sparseArray.get(message.id);
                                    if (arrayList15 != null) {
                                        for (i4 = 0; i4 < arrayList15.size(); i4++) {
                                            TLdeserialize = (Message) arrayList15.get(i4);
                                            TLdeserialize.replyMessage = message;
                                            if (MessageObject.isMegagroup(TLdeserialize)) {
                                                TLdeserialize = TLdeserialize.replyMessage;
                                                TLdeserialize.flags |= Integer.MIN_VALUE;
                                            }
                                        }
                                    }
                                }
                                z = false;
                            }
                            queryFinalized.dispose();
                        }
                        if (arrayList10.isEmpty()) {
                            arrayList5 = arrayList4;
                        } else {
                            arrayList5 = arrayList4;
                            MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList10), arrayList5, arrayList8);
                        }
                        if (arrayList8.isEmpty()) {
                            arrayList6 = arrayList;
                        } else {
                            arrayList6 = arrayList;
                            MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList8), arrayList6);
                        }
                        if (arrayList9.isEmpty()) {
                            arrayList7 = arrayList2;
                        } else {
                            arrayList7 = arrayList2;
                            MessagesStorage.this.getChatsInternal(TextUtils.join(",", arrayList9), arrayList7);
                            int i5 = 0;
                            while (i5 < arrayList7.size()) {
                                ArrayList arrayList16;
                                Chat chat = (Chat) arrayList7.get(i5);
                                if (chat == null || (!chat.left && chat.migrated_to == null)) {
                                    arrayList16 = arrayList3;
                                } else {
                                    long j = (long) (-chat.id);
                                    SQLiteDatabase access$0003 = MessagesStorage.this.database;
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("UPDATE dialogs SET unread_count = 0 WHERE did = ");
                                    stringBuilder3.append(j);
                                    access$0003.executeFast(stringBuilder3.toString()).stepThis().dispose();
                                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
                                    arrayList7.remove(i5);
                                    i5--;
                                    longSparseArray.remove((long) (-chat.id));
                                    int i6 = 0;
                                    arrayList16 = arrayList3;
                                    while (i6 < arrayList16.size()) {
                                        if (((Message) arrayList16.get(i6)).dialog_id == ((long) (-chat.id))) {
                                            arrayList16.remove(i6);
                                            i6--;
                                        }
                                        i6++;
                                    }
                                }
                                i5++;
                                arrayList3 = arrayList16;
                            }
                        }
                        list = arrayList3;
                    } else {
                        list = arrayList12;
                        arrayList5 = arrayList15;
                        arrayList6 = arrayList13;
                        arrayList7 = arrayList14;
                    }
                    Collections.reverse(list);
                    final LongSparseArray longSparseArray2 = longSparseArray;
                    final List list2 = list;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationsController.getInstance(MessagesStorage.this.currentAccount).processLoadedUnreadMessages(longSparseArray2, list2, arrayList6, arrayList7, arrayList5);
                        }
                    });
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                }
            }
        });
    }

    public void putWallpapers(final ArrayList<WallPaper> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                int i = 0;
                try {
                    MessagesStorage.this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        WallPaper wallPaper = (WallPaper) it.next();
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(wallPaper.getObjectSize());
                        wallPaper.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, i);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.step();
                        i++;
                        nativeByteBuffer.reuse();
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadWebRecent(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT id, image_url, thumb_url, local_url, width, height, size, date, document FROM web_recent_v3 WHERE type = ");
                    stringBuilder.append(i);
                    stringBuilder.append(" ORDER BY date DESC");
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    final ArrayList arrayList = new ArrayList();
                    while (queryFinalized.next()) {
                        SearchImage searchImage = new SearchImage();
                        searchImage.id = queryFinalized.stringValue(0);
                        searchImage.imageUrl = queryFinalized.stringValue(1);
                        searchImage.thumbUrl = queryFinalized.stringValue(2);
                        searchImage.localUrl = queryFinalized.stringValue(3);
                        searchImage.width = queryFinalized.intValue(4);
                        searchImage.height = queryFinalized.intValue(5);
                        searchImage.size = queryFinalized.intValue(6);
                        searchImage.date = queryFinalized.intValue(7);
                        if (!queryFinalized.isNull(8)) {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(8);
                            if (byteBufferValue != null) {
                                searchImage.document = Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                            }
                        }
                        searchImage.type = i;
                        arrayList.add(searchImage);
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.recentImagesDidLoaded, Integer.valueOf(i), arrayList);
                        }
                    });
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        });
    }

    public void addRecentLocalFile(final String str, final String str2, final Document document) {
        if (!(str == null || str.length() == 0)) {
            if ((str2 != null && str2.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            SQLitePreparedStatement executeFast;
                            if (document != null) {
                                executeFast = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                                executeFast.requery();
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                                document.serializeToStream(nativeByteBuffer);
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindString(2, str);
                                executeFast.step();
                                executeFast.dispose();
                                nativeByteBuffer.reuse();
                                return;
                            }
                            executeFast = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                            executeFast.requery();
                            executeFast.bindString(1, str2);
                            executeFast.bindString(2, str);
                            executeFast.step();
                            executeFast.dispose();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void clearWebRecent(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM web_recent_v3 WHERE type = ");
                    stringBuilder.append(i);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putWebRecent(final ArrayList<SearchImage> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    int i;
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    int i2 = 0;
                    while (true) {
                        int size = arrayList.size();
                        i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                        if (i2 >= size) {
                            break;
                        } else if (i2 == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                            break;
                        } else {
                            SearchImage searchImage = (SearchImage) arrayList.get(i2);
                            executeFast.requery();
                            executeFast.bindString(1, searchImage.id);
                            executeFast.bindInteger(2, searchImage.type);
                            executeFast.bindString(3, searchImage.imageUrl != null ? searchImage.imageUrl : TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(4, searchImage.thumbUrl != null ? searchImage.thumbUrl : TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(5, searchImage.localUrl != null ? searchImage.localUrl : TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindInteger(6, searchImage.width);
                            executeFast.bindInteger(7, searchImage.height);
                            executeFast.bindInteger(8, searchImage.size);
                            executeFast.bindInteger(9, searchImage.date);
                            NativeByteBuffer nativeByteBuffer = null;
                            if (searchImage.document != null) {
                                nativeByteBuffer = new NativeByteBuffer(searchImage.document.getObjectSize());
                                searchImage.document.serializeToStream(nativeByteBuffer);
                                executeFast.bindByteBuffer(10, nativeByteBuffer);
                            } else {
                                executeFast.bindNull(10);
                            }
                            executeFast.step();
                            if (nativeByteBuffer != null) {
                                nativeByteBuffer.reuse();
                            }
                            i2++;
                        }
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                    if (arrayList.size() >= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        MessagesStorage.this.database.beginTransaction();
                        while (i < arrayList.size()) {
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                            stringBuilder.append(((SearchImage) arrayList.get(i)).id);
                            stringBuilder.append("'");
                            access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                            i++;
                        }
                        MessagesStorage.this.database.commitTransaction();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
                    final ArrayList arrayList = new ArrayList();
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            WallPaper TLdeserialize = WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            arrayList.add(TLdeserialize);
                        }
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoaded, arrayList);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void getBlockedUsers() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        arrayList.add(Integer.valueOf(intValue));
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(intValue);
                    }
                    queryFinalized.dispose();
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList2);
                    }
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedBlockedUsers(arrayList, arrayList2, true);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void deleteBlockedUser(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM blocked_users WHERE uid = ");
                    stringBuilder.append(i);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putBlockedUsers(final ArrayList<Integer> arrayList, final boolean z) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            if (z) {
                                MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
                            }
                            MessagesStorage.this.database.beginTransaction();
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                Integer num = (Integer) it.next();
                                executeFast.requery();
                                executeFast.bindInteger(1, num.intValue());
                                executeFast.step();
                            }
                            executeFast.dispose();
                            MessagesStorage.this.database.commitTransaction();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void deleteUserChannelHistory(final int i, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    long j = (long) (-i);
                    final ArrayList arrayList = new ArrayList();
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT data FROM messages WHERE uid = ");
                    stringBuilder.append(j);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    ArrayList arrayList2 = new ArrayList();
                    while (queryFinalized.next()) {
                        try {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                byteBufferValue.reuse();
                                if (!(TLdeserialize == null || TLdeserialize.from_id != i2 || TLdeserialize.id == 1)) {
                                    arrayList.add(Integer.valueOf(TLdeserialize.id));
                                    if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                        Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                        while (it.hasNext()) {
                                            File pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList2.add(pathToAttach);
                                            }
                                        }
                                    } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                        File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                        if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                            arrayList2.add(pathToAttach2);
                                        }
                                        pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                        if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                            arrayList2.add(pathToAttach2);
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.getInstance(MessagesStorage.this.currentAccount).markChannelDialogMessageAsDeleted(arrayList, i);
                        }
                    });
                    MessagesStorage.this.markMessagesAsDeletedInternal(arrayList, i);
                    MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(arrayList, null, i);
                    FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles(arrayList2, 0);
                    if (!arrayList.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i));
                            }
                        });
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        });
    }

    public void deleteDialog(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesStorage$23$1 */
            class C03781 implements Runnable {
                C03781() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                }
            }

            public void run() {
                try {
                    SQLiteDatabase access$000;
                    StringBuilder stringBuilder;
                    SQLiteCursor queryFinalized;
                    int intValue;
                    AbstractSerializedData byteBufferValue;
                    StringBuilder stringBuilder2;
                    SQLiteDatabase access$0002;
                    if (i == 3) {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT last_mid FROM dialogs WHERE did = ");
                        stringBuilder.append(j);
                        queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                        intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                        queryFinalized.dispose();
                        if (intValue != 0) {
                            return;
                        }
                    }
                    if (((int) j) == 0 || i == 2) {
                        access$000 = MessagesStorage.this.database;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("SELECT data FROM messages WHERE uid = ");
                        stringBuilder3.append(j);
                        queryFinalized = access$000.queryFinalized(stringBuilder3.toString(), new Object[0]);
                        ArrayList arrayList = new ArrayList();
                        while (queryFinalized.next()) {
                            try {
                                byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    if (!(TLdeserialize == null || TLdeserialize.media == null)) {
                                        if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                            Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                            while (it.hasNext()) {
                                                File pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                    arrayList.add(pathToAttach);
                                                }
                                            }
                                        } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                            File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                arrayList.add(pathToAttach2);
                                            }
                                            pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                arrayList.add(pathToAttach2);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        queryFinalized.dispose();
                        FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles(arrayList, i);
                    }
                    if (i != 0) {
                        if (i != 3) {
                            if (i == 2) {
                                access$000 = MessagesStorage.this.database;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("SELECT last_mid_i, last_mid FROM dialogs WHERE did = ");
                                stringBuilder2.append(j);
                                queryFinalized = access$000.queryFinalized(stringBuilder2.toString(), new Object[0]);
                                if (queryFinalized.next()) {
                                    long longValue = queryFinalized.longValue(0);
                                    long longValue2 = queryFinalized.longValue(1);
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("SELECT data FROM messages WHERE uid = ");
                                    stringBuilder.append(j);
                                    stringBuilder.append(" AND mid IN (");
                                    stringBuilder.append(longValue);
                                    stringBuilder.append(",");
                                    stringBuilder.append(longValue2);
                                    stringBuilder.append(")");
                                    SQLiteCursor queryFinalized2 = access$0002.queryFinalized(stringBuilder.toString(), new Object[0]);
                                    intValue = -1;
                                    while (queryFinalized2.next()) {
                                        try {
                                            byteBufferValue = queryFinalized2.byteBufferValue(0);
                                            if (byteBufferValue != null) {
                                                Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                TLdeserialize2.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                                byteBufferValue.reuse();
                                                if (TLdeserialize2 != null) {
                                                    intValue = TLdeserialize2.id;
                                                }
                                            }
                                        } catch (Throwable e2) {
                                            FileLog.m3e(e2);
                                        }
                                    }
                                    queryFinalized2.dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    StringBuilder stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM messages WHERE uid = ");
                                    stringBuilder4.append(j);
                                    stringBuilder4.append(" AND mid != ");
                                    stringBuilder4.append(longValue);
                                    stringBuilder4.append(" AND mid != ");
                                    stringBuilder4.append(longValue2);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM messages_holes WHERE uid = ");
                                    stringBuilder4.append(j);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM bot_keyboard WHERE uid = ");
                                    stringBuilder4.append(j);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM media_counts_v2 WHERE uid = ");
                                    stringBuilder4.append(j);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM media_v2 WHERE uid = ");
                                    stringBuilder4.append(j);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    access$0002 = MessagesStorage.this.database;
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("DELETE FROM media_holes_v2 WHERE uid = ");
                                    stringBuilder4.append(j);
                                    access$0002.executeFast(stringBuilder4.toString()).stepThis().dispose();
                                    DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(j, null);
                                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                                    SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                    if (intValue != -1) {
                                        MessagesStorage.createFirstHoles(j, executeFast, executeFast2, intValue);
                                    }
                                    executeFast.dispose();
                                    executeFast2.dispose();
                                }
                                queryFinalized.dispose();
                                return;
                            }
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("UPDATE dialogs SET unread_count = 0 WHERE did = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM messages WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM bot_keyboard WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM media_counts_v2 WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM media_v2 WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM messages_holes WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("DELETE FROM media_holes_v2 WHERE uid = ");
                            stringBuilder2.append(j);
                            access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(j, null);
                            AndroidUtilities.runOnUIThread(new C03781());
                        }
                    }
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM dialogs WHERE did = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM chat_settings_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM chat_pinned WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM channel_users_v2 WHERE did = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM search_recent WHERE did = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    int i = (int) j;
                    int i2 = (int) (j >> 32);
                    StringBuilder stringBuilder5;
                    if (i == 0) {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("DELETE FROM enc_chats WHERE uid = ");
                        stringBuilder5.append(i2);
                        access$000.executeFast(stringBuilder5.toString()).stepThis().dispose();
                    } else if (i2 == 1) {
                        access$0002 = MessagesStorage.this.database;
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("DELETE FROM chats WHERE uid = ");
                        stringBuilder5.append(i);
                        access$0002.executeFast(stringBuilder5.toString()).stepThis().dispose();
                    }
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("UPDATE dialogs SET unread_count = 0 WHERE did = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM messages WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM bot_keyboard WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_counts_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM messages_holes WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_holes_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(j, null);
                    AndroidUtilities.runOnUIThread(new C03781());
                } catch (Throwable e3) {
                    FileLog.m3e(e3);
                }
            }
        });
    }

    public void getDialogPhotos(int i, int i2, long j, int i3) {
        final long j2 = j;
        final int i4 = i;
        final int i5 = i2;
        final int i6 = i3;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized;
                    if (j2 != 0) {
                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[]{Integer.valueOf(i4), Long.valueOf(j2), Integer.valueOf(i5)}), new Object[0]);
                    } else {
                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)}), new Object[0]);
                    }
                    final photos_Photos tL_photos_photos = new TL_photos_photos();
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Photo TLdeserialize = Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            tL_photos_photos.photos.add(TLdeserialize);
                        }
                    }
                    queryFinalized.dispose();
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedUserPhotos(tL_photos_photos, i4, i5, j2, true, i6);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void clearUserPhotos(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
                    stringBuilder.append(i);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void clearUserPhoto(final int i, final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
                    stringBuilder.append(i);
                    stringBuilder.append(" AND id = ");
                    stringBuilder.append(j);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void resetDialogs(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<TL_dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message, int i6) {
        final messages_Dialogs messages_dialogs2 = messages_dialogs;
        final int i7 = i6;
        final int i8 = i2;
        final int i9 = i3;
        final int i10 = i4;
        final int i11 = i5;
        final Message message2 = message;
        final int i12 = i;
        final LongSparseArray<TL_dialog> longSparseArray3 = longSparseArray;
        final LongSparseArray<MessageObject> longSparseArray4 = longSparseArray2;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    int i;
                    Iterable arrayList = new ArrayList();
                    int size = messages_dialogs2.dialogs.size() - i7;
                    final LongSparseArray longSparseArray = new LongSparseArray();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    for (int i2 = i7; i2 < messages_dialogs2.dialogs.size(); i2++) {
                        arrayList3.add(Long.valueOf(((TL_dialog) messages_dialogs2.dialogs.get(i2)).id));
                    }
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
                    int i3 = 0;
                    while (queryFinalized.next()) {
                        long longValue = queryFinalized.longValue(0);
                        int intValue = queryFinalized.intValue(1);
                        int i4 = (int) longValue;
                        if (i4 != 0) {
                            arrayList.add(Integer.valueOf(i4));
                            if (intValue > 0) {
                                i3 = Math.max(intValue, i3);
                                longSparseArray.put(longValue, Integer.valueOf(intValue));
                                arrayList2.add(Long.valueOf(longValue));
                            }
                        }
                    }
                    Collections.sort(arrayList2, new Comparator<Long>() {
                        public int compare(Long l, Long l2) {
                            Integer num = (Integer) longSparseArray.get(l.longValue());
                            Integer num2 = (Integer) longSparseArray.get(l2.longValue());
                            if (num.intValue() < num2.intValue()) {
                                return 1;
                            }
                            return num.intValue() > num2.intValue() ? -1 : null;
                        }
                    });
                    while (arrayList2.size() < size) {
                        arrayList2.add(0, Long.valueOf(0));
                    }
                    queryFinalized.dispose();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("(");
                    stringBuilder.append(TextUtils.join(",", arrayList));
                    stringBuilder.append(")");
                    String stringBuilder2 = stringBuilder.toString();
                    MessagesStorage.this.database.beginTransaction();
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM dialogs WHERE did IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM messages WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM bot_keyboard WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM media_counts_v2 WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM media_v2 WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM messages_holes WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM media_holes_v2 WHERE uid IN ");
                    stringBuilder3.append(stringBuilder2);
                    access$000.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    MessagesStorage.this.database.commitTransaction();
                    for (i = 0; i < size; i++) {
                        TL_dialog tL_dialog = (TL_dialog) messages_dialogs2.dialogs.get(i7 + i);
                        int indexOf = arrayList2.indexOf(Long.valueOf(tL_dialog.id));
                        int indexOf2 = arrayList3.indexOf(Long.valueOf(tL_dialog.id));
                        if (!(indexOf == -1 || indexOf2 == -1)) {
                            Integer num;
                            if (indexOf == indexOf2) {
                                num = (Integer) longSparseArray.get(tL_dialog.id);
                                if (num != null) {
                                    tL_dialog.pinnedNum = num.intValue();
                                }
                            } else {
                                num = (Integer) longSparseArray.get(((Long) arrayList2.get(indexOf2)).longValue());
                                if (num != null) {
                                    tL_dialog.pinnedNum = num.intValue();
                                }
                            }
                        }
                        if (tL_dialog.pinnedNum == 0) {
                            tL_dialog.pinnedNum = (size - i) + i3;
                        }
                    }
                    MessagesStorage.this.putDialogsInternal(messages_dialogs2, false);
                    MessagesStorage.this.saveDiffParamsInternal(i8, i9, i10, i11);
                    if (message2 == null || message2.id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId) {
                        UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        UserConfig.getInstance(MessagesStorage.this.currentAccount).totalDialogsLoadCount = messages_dialogs2.dialogs.size();
                        UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId = message2.id;
                        UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetDate = message2.date;
                        Chat chat;
                        if (message2.to_id.channel_id != 0) {
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = message2.to_id.channel_id;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (i = 0; i < messages_dialogs2.chats.size(); i++) {
                                chat = (Chat) messages_dialogs2.chats.get(i);
                                if (chat.id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId) {
                                    UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                                    break;
                                }
                            }
                        } else if (message2.to_id.chat_id != 0) {
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = message2.to_id.chat_id;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (i = 0; i < messages_dialogs2.chats.size(); i++) {
                                chat = (Chat) messages_dialogs2.chats.get(i);
                                if (chat.id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId) {
                                    UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                                    break;
                                }
                            }
                        } else if (message2.to_id.user_id != 0) {
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = message2.to_id.user_id;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            for (i = 0; i < messages_dialogs2.users.size(); i++) {
                                User user = (User) messages_dialogs2.users.get(i);
                                if (user.id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId) {
                                    UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = user.access_hash;
                                    break;
                                }
                            }
                        }
                    }
                    UserConfig.getInstance(MessagesStorage.this.currentAccount).saveConfig(false);
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).completeDialogsReset(messages_dialogs2, i12, i8, i9, i10, i11, longSparseArray3, longSparseArray4, message2);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putDialogPhotos(final int i, final photos_Photos photos_photos) {
        if (photos_photos != null) {
            if (!photos_photos.photos.isEmpty()) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
                            Iterator it = photos_photos.photos.iterator();
                            while (it.hasNext()) {
                                Photo photo = (Photo) it.next();
                                if (!(photo instanceof TL_photoEmpty)) {
                                    executeFast.requery();
                                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(photo.getObjectSize());
                                    photo.serializeToStream(nativeByteBuffer);
                                    executeFast.bindInteger(1, i);
                                    executeFast.bindLong(2, photo.id);
                                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                                    executeFast.step();
                                    nativeByteBuffer.reuse();
                                }
                            }
                            executeFast.dispose();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void emptyMessagesMedia(final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    Message TLdeserialize;
                    ArrayList arrayList = new ArrayList();
                    final ArrayList arrayList2 = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            if (TLdeserialize.media != null) {
                                if (TLdeserialize.media.document != null) {
                                    File pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document, true);
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList.add(pathToAttach);
                                    }
                                    pathToAttach = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb, true);
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList.add(pathToAttach);
                                    }
                                    TLdeserialize.media.document = new TL_documentEmpty();
                                } else if (TLdeserialize.media.photo != null) {
                                    Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                    while (it.hasNext()) {
                                        File pathToAttach2 = FileLoader.getPathToAttach((PhotoSize) it.next(), true);
                                        if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                            arrayList.add(pathToAttach2);
                                        }
                                    }
                                    TLdeserialize.media.photo = new TL_photoEmpty();
                                }
                                TLdeserialize.media.flags &= -2;
                                TLdeserialize.id = queryFinalized.intValue(1);
                                TLdeserialize.date = queryFinalized.intValue(2);
                                TLdeserialize.dialog_id = queryFinalized.longValue(3);
                                arrayList2.add(TLdeserialize);
                            }
                        }
                    }
                    queryFinalized.dispose();
                    if (!arrayList2.isEmpty()) {
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                        for (int i = 0; i < arrayList2.size(); i++) {
                            TLdeserialize = (Message) arrayList2.get(i);
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                            TLdeserialize.serializeToStream(nativeByteBuffer);
                            executeFast.requery();
                            executeFast.bindLong(1, (long) TLdeserialize.id);
                            executeFast.bindLong(2, TLdeserialize.dialog_id);
                            executeFast.bindInteger(3, MessageObject.getUnreadFlags(TLdeserialize));
                            executeFast.bindInteger(4, TLdeserialize.send_state);
                            executeFast.bindInteger(5, TLdeserialize.date);
                            executeFast.bindByteBuffer(6, nativeByteBuffer);
                            executeFast.bindInteger(7, MessageObject.isOut(TLdeserialize));
                            executeFast.bindInteger(8, TLdeserialize.ttl);
                            if ((TLdeserialize.flags & 1024) != 0) {
                                executeFast.bindInteger(9, TLdeserialize.views);
                            } else {
                                executeFast.bindInteger(9, MessagesStorage.this.getMessageMediaType(TLdeserialize));
                            }
                            executeFast.bindInteger(10, 0);
                            executeFast.bindInteger(11, TLdeserialize.mentioned);
                            executeFast.step();
                            nativeByteBuffer.reuse();
                        }
                        executeFast.dispose();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                for (int i = 0; i < arrayList2.size(); i++) {
                                    NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, arrayList2.get(i));
                                }
                            }
                        });
                    }
                    FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles(arrayList, 0);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void getNewTask(final ArrayList<Integer> arrayList, int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    if (arrayList != null) {
                        String join = TextUtils.join(",", arrayList);
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[]{join})).stepThis().dispose();
                    }
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
                    ArrayList arrayList = null;
                    int i = 0;
                    int i2 = -1;
                    while (queryFinalized.next()) {
                        long longValue = queryFinalized.longValue(0);
                        if (i2 == -1) {
                            i2 = (int) (longValue >> 32);
                            if (i2 < 0) {
                                i2 = 0;
                            }
                        }
                        int intValue = queryFinalized.intValue(1);
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(Integer.valueOf((int) longValue));
                        i = intValue;
                    }
                    queryFinalized.dispose();
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDeleteTask(i, arrayList, i2);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        final int i3 = i;
        final int i4 = i2;
        final long j2 = j;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    long j = (long) i3;
                    if (i4 != 0) {
                        j |= ((long) i4) << 32;
                    }
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
                    stringBuilder.append(j2);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    int max = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
                    queryFinalized.dispose();
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(max), Long.valueOf(j2)})).stepThis().dispose();
                    LongSparseArray longSparseArray = new LongSparseArray(1);
                    longSparseArray.put(j2, Integer.valueOf(max));
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void markMessageAsMention(final long j) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void resetMentionsCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    if (i == 0) {
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(j)})).stepThis().dispose();
                    }
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j)})).stepThis().dispose();
                    LongSparseArray longSparseArray = new LongSparseArray(1);
                    longSparseArray.put(j, Integer.valueOf(i));
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void createTaskForMid(int i, int i2, int i3, int i4, int i5, boolean z) {
        final int i6 = i3;
        final int i7 = i4;
        final int i8 = i5;
        final int i9 = i;
        final int i10 = i2;
        final boolean z2 = z;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    int i = (i6 > i7 ? i6 : i7) + i8;
                    SparseArray sparseArray = new SparseArray();
                    final ArrayList arrayList = new ArrayList();
                    long j = (long) i9;
                    if (i10 != 0) {
                        j |= ((long) i10) << 32;
                    }
                    arrayList.add(Long.valueOf(j));
                    sparseArray.put(i, arrayList);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (!z2) {
                                MessagesStorage.this.markMessagesContentAsRead(arrayList, 0);
                            }
                            NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
                        }
                    });
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                    for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                        int keyAt = sparseArray.keyAt(i2);
                        ArrayList arrayList2 = (ArrayList) sparseArray.get(keyAt);
                        for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                            executeFast.requery();
                            executeFast.bindLong(1, ((Long) arrayList2.get(i3)).longValue());
                            executeFast.bindInteger(2, keyAt);
                            executeFast.step();
                        }
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).didAddedNewTask(i, sparseArray);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void createTaskForSecretChat(int i, int i2, int i3, int i4, ArrayList<Long> arrayList) {
        final ArrayList<Long> arrayList2 = arrayList;
        final int i5 = i;
        final int i6 = i4;
        final int i7 = i2;
        final int i8 = i3;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                try {
                    SQLiteCursor queryFinalized;
                    int intValue;
                    SparseArray sparseArray = new SparseArray();
                    final ArrayList arrayList = new ArrayList();
                    StringBuilder stringBuilder = new StringBuilder();
                    if (arrayList2 == null) {
                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[]{Long.valueOf(((long) i5) << 32), Integer.valueOf(i6), Integer.valueOf(i7)}), new Object[0]);
                    } else {
                        String join = TextUtils.join(",", arrayList2);
                        queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[]{join}), new Object[0]);
                    }
                    while (queryFinalized.next()) {
                        intValue = queryFinalized.intValue(1);
                        long intValue2 = (long) queryFinalized.intValue(0);
                        if (arrayList2 != null) {
                            arrayList.add(Long.valueOf(intValue2));
                        }
                        if (intValue > 0) {
                            int i2 = (i7 > i8 ? i7 : i8) + intValue;
                            i = Math.min(i, i2);
                            ArrayList arrayList2 = (ArrayList) sparseArray.get(i2);
                            if (arrayList2 == null) {
                                arrayList2 = new ArrayList();
                                sparseArray.put(i2, arrayList2);
                            }
                            if (stringBuilder.length() != 0) {
                                stringBuilder.append(",");
                            }
                            stringBuilder.append(intValue2);
                            arrayList2.add(Long.valueOf(intValue2));
                        }
                    }
                    queryFinalized.dispose();
                    if (arrayList2 != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesStorage.this.markMessagesContentAsRead(arrayList, 0);
                                NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
                            }
                        });
                    }
                    if (sparseArray.size() != 0) {
                        MessagesStorage.this.database.beginTransaction();
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                        for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                            intValue = sparseArray.keyAt(i3);
                            ArrayList arrayList3 = (ArrayList) sparseArray.get(intValue);
                            for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                                executeFast.requery();
                                executeFast.bindLong(1, ((Long) arrayList3.get(i4)).longValue());
                                executeFast.bindInteger(2, intValue);
                                executeFast.step();
                            }
                        }
                        executeFast.dispose();
                        MessagesStorage.this.database.commitTransaction();
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[]{stringBuilder.toString()})).stepThis().dispose();
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).didAddedNewTask(i, sparseArray);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> arrayList, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList<Long> arrayList2) {
        MessagesStorage messagesStorage = this;
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        ArrayList<Long> arrayList3 = arrayList2;
        try {
            SQLitePreparedStatement executeFast;
            int i;
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            ArrayList arrayList4 = new ArrayList();
            int i2 = 2;
            SQLiteCursor queryFinalized;
            if (isEmpty((List) arrayList)) {
                int i3;
                long longValue;
                int i4;
                if (!isEmpty(sparseLongArray)) {
                    i3 = 0;
                    while (i3 < sparseLongArray.size()) {
                        int keyAt = sparseLongArray3.keyAt(i3);
                        long j = sparseLongArray3.get(keyAt);
                        SQLiteDatabase sQLiteDatabase = messagesStorage.database;
                        Object[] objArr = new Object[i2];
                        objArr[0] = Integer.valueOf(keyAt);
                        objArr[1] = Long.valueOf(j);
                        queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            longSparseArray.put((long) keyAt, Integer.valueOf(queryFinalized.intValue(0)));
                        }
                        queryFinalized.dispose();
                        executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast.requery();
                        long j2 = (long) keyAt;
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, (int) j);
                        executeFast.bindLong(3, j2);
                        executeFast.step();
                        executeFast.dispose();
                        i3++;
                        sparseLongArray3 = sparseLongArray;
                        i2 = 2;
                    }
                }
                if (isEmpty((List) arrayList2)) {
                    i = 0;
                } else {
                    ArrayList arrayList5 = new ArrayList(arrayList3);
                    String join = TextUtils.join(",", arrayList3);
                    SQLiteDatabase sQLiteDatabase2 = messagesStorage.database;
                    Object[] objArr2 = new Object[1];
                    i = 0;
                    objArr2[0] = join;
                    SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", objArr2), new Object[0]);
                    while (queryFinalized2.next()) {
                        longValue = queryFinalized2.longValue(i);
                        arrayList5.remove(Long.valueOf(queryFinalized2.longValue(4)));
                        if (queryFinalized2.intValue(1) < 2 && queryFinalized2.intValue(2) == 0 && queryFinalized2.intValue(3) == 1) {
                            Integer num = (Integer) longSparseArray2.get(longValue);
                            if (num == null) {
                                SQLiteDatabase sQLiteDatabase3 = messagesStorage.database;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
                                stringBuilder.append(longValue);
                                SQLiteCursor queryFinalized3 = sQLiteDatabase3.queryFinalized(stringBuilder.toString(), new Object[0]);
                                i = queryFinalized3.next() ? queryFinalized3.intValue(0) : 0;
                                queryFinalized3.dispose();
                                longSparseArray2.put(longValue, Integer.valueOf(Math.max(0, i - 1)));
                            } else {
                                i = 0;
                                longSparseArray2.put(longValue, Integer.valueOf(Math.max(0, num.intValue() - 1)));
                            }
                        }
                        i = 0;
                    }
                    queryFinalized2.dispose();
                    for (i4 = i; i4 < arrayList5.size(); i4++) {
                        i3 = (int) (((Long) arrayList5.get(i4)).longValue() >> 32);
                        if (i3 > 0 && !arrayList4.contains(Integer.valueOf(i3))) {
                            arrayList4.add(Integer.valueOf(i3));
                        }
                    }
                }
                if (!isEmpty(sparseLongArray2)) {
                    for (int i5 = i; i5 < sparseLongArray2.size(); i5++) {
                        i4 = sparseLongArray4.keyAt(i5);
                        longValue = sparseLongArray4.get(i4);
                        SQLitePreparedStatement executeFast2 = messagesStorage.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast2.requery();
                        long j3 = (long) i4;
                        executeFast2.bindLong(1, j3);
                        executeFast2.bindInteger(2, (int) longValue);
                        executeFast2.bindLong(3, j3);
                        executeFast2.step();
                        executeFast2.dispose();
                    }
                }
            } else {
                String join2 = TextUtils.join(",", arrayList);
                queryFinalized = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[]{join2}), new Object[0]);
                while (queryFinalized.next()) {
                    if (queryFinalized.intValue(2) == 0) {
                        if (queryFinalized.intValue(1) == 0) {
                            long longValue2 = queryFinalized.longValue(0);
                            Integer num2 = (Integer) longSparseArray.get(longValue2);
                            if (num2 == null) {
                                longSparseArray.put(longValue2, Integer.valueOf(1));
                            } else {
                                longSparseArray.put(longValue2, Integer.valueOf(num2.intValue() + 1));
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                i = 0;
            }
            if (longSparseArray.size() > 0 || longSparseArray2.size() > 0) {
                messagesStorage.database.beginTransaction();
                if (longSparseArray.size() > 0) {
                    executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                    for (int i6 = i; i6 < longSparseArray.size(); i6++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, ((Integer) longSparseArray.valueAt(i6)).intValue());
                        executeFast.bindLong(2, longSparseArray.keyAt(i6));
                        executeFast.step();
                    }
                    executeFast.dispose();
                }
                if (longSparseArray2.size() > 0) {
                    executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
                    while (i < longSparseArray2.size()) {
                        executeFast.requery();
                        executeFast.bindInteger(1, ((Integer) longSparseArray2.valueAt(i)).intValue());
                        executeFast.bindLong(2, longSparseArray2.keyAt(i));
                        executeFast.step();
                        i++;
                    }
                    executeFast.dispose();
                }
                messagesStorage.database.commitTransaction();
            }
            MessagesController.getInstance(messagesStorage.currentAccount).processDialogsUpdateRead(longSparseArray, longSparseArray2);
            if (!arrayList4.isEmpty()) {
                MessagesController.getInstance(messagesStorage.currentAccount).reloadMentionsCountForChannels(arrayList4);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        if (sparseArray != null) {
            if (sparseArray.size() != null) {
                return null;
            }
        }
        return true;
    }

    private static boolean isEmpty(SparseLongArray sparseLongArray) {
        if (sparseLongArray != null) {
            if (sparseLongArray.size() != null) {
                return null;
            }
        }
        return true;
    }

    private static boolean isEmpty(List<?> list) {
        if (list != null) {
            if (list.isEmpty() == null) {
                return null;
            }
        }
        return true;
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        if (sparseIntArray != null) {
            if (sparseIntArray.size() != null) {
                return null;
            }
        }
        return true;
    }

    private static boolean isEmpty(LongSparseArray<?> longSparseArray) {
        if (longSparseArray != null) {
            if (longSparseArray.size() != null) {
                return null;
            }
        }
        return true;
    }

    public void updateDialogsWithReadMessages(final SparseLongArray sparseLongArray, final SparseLongArray sparseLongArray2, final ArrayList<Long> arrayList, boolean z) {
        if (!isEmpty(sparseLongArray) || !isEmpty((List) arrayList)) {
            if (z) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.this.updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
                    }
                });
            } else {
                updateDialogsWithReadMessagesInternal(false, sparseLongArray, sparseLongArray2, arrayList);
            }
        }
    }

    public void updateChatParticipants(final ChatParticipants chatParticipants) {
        if (chatParticipants != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase access$000 = MessagesStorage.this.database;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT info, pinned FROM chat_settings_v2 WHERE uid = ");
                        stringBuilder.append(chatParticipants.chat_id);
                        SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                        ChatFull chatFull = null;
                        ArrayList arrayList = new ArrayList();
                        if (queryFinalized.next()) {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                chatFull.pinned_msg_id = queryFinalized.intValue(1);
                            }
                        }
                        queryFinalized.dispose();
                        if (chatFull instanceof TL_chatFull) {
                            chatFull.participants = chatParticipants;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                                }
                            });
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                            chatFull.serializeToStream(nativeByteBuffer);
                            executeFast.bindInteger(1, chatFull.id);
                            executeFast.bindByteBuffer(2, nativeByteBuffer);
                            executeFast.bindInteger(3, chatFull.pinned_msg_id);
                            executeFast.step();
                            executeFast.dispose();
                            nativeByteBuffer.reuse();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void loadChannelAdmins(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT uid FROM channel_admins WHERE did = ");
                    stringBuilder.append(i);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    ArrayList arrayList = new ArrayList();
                    while (queryFinalized.next()) {
                        arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
                    }
                    queryFinalized.dispose();
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedChannelAdmins(arrayList, i, true);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putChannelAdmins(final int i, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM channel_admins WHERE did = ");
                    stringBuilder.append(i);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO channel_admins VALUES(?, ?)");
                    long currentTimeMillis = System.currentTimeMillis() / 1000;
                    for (int i = 0; i < arrayList.size(); i++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, i);
                        executeFast.bindInteger(2, ((Integer) arrayList.get(i)).intValue());
                        executeFast.step();
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void updateChannelUsers(final int i, final ArrayList<ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    long j = (long) (-i);
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM channel_users_v2 WHERE did = ");
                    stringBuilder.append(j);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                    MessagesStorage.this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
                    int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                    for (int i = 0; i < arrayList.size(); i++) {
                        ChannelParticipant channelParticipant = (ChannelParticipant) arrayList.get(i);
                        executeFast.requery();
                        executeFast.bindLong(1, j);
                        executeFast.bindInteger(2, channelParticipant.user_id);
                        executeFast.bindInteger(3, currentTimeMillis);
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(channelParticipant.getObjectSize());
                        channelParticipant.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(4, nativeByteBuffer);
                        nativeByteBuffer.reuse();
                        executeFast.step();
                        currentTimeMillis--;
                    }
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                    MessagesStorage.this.loadChatInfo(i, null, false, true);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void saveBotCache(final String str, final TLObject tLObject) {
        if (tLObject != null) {
            if (!TextUtils.isEmpty(str)) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            int currentTime = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
                            if (tLObject instanceof TL_messages_botCallbackAnswer) {
                                currentTime += ((TL_messages_botCallbackAnswer) tLObject).cache_time;
                            } else if (tLObject instanceof TL_messages_botResults) {
                                currentTime += ((TL_messages_botResults) tLObject).cache_time;
                            }
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                            tLObject.serializeToStream(nativeByteBuffer);
                            executeFast.bindString(1, str);
                            executeFast.bindInteger(2, currentTime);
                            executeFast.bindByteBuffer(3, nativeByteBuffer);
                            executeFast.step();
                            executeFast.dispose();
                            nativeByteBuffer.reuse();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void getBotCache(final String str, final RequestDelegate requestDelegate) {
        if (str != null) {
            if (requestDelegate != null) {
                final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                this.storageQueue.postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        SQLiteCursor queryFinalized;
                        TLObject TLdeserialize;
                        Throwable e;
                        Throwable e2;
                        try {
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM botcache WHERE date < ");
                            stringBuilder.append(currentTime);
                            access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                            queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM botcache WHERE id = '%s'", new Object[]{str}), new Object[0]);
                            if (queryFinalized.next()) {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    int readInt32 = byteBufferValue.readInt32(false);
                                    if (readInt32 == TL_messages_botCallbackAnswer.constructor) {
                                        TLdeserialize = TL_messages_botCallbackAnswer.TLdeserialize(byteBufferValue, readInt32, false);
                                    } else {
                                        TLdeserialize = messages_BotResults.TLdeserialize(byteBufferValue, readInt32, false);
                                    }
                                    try {
                                        byteBufferValue.reuse();
                                    } catch (Exception e3) {
                                        e = e3;
                                        try {
                                            FileLog.m3e(e);
                                            queryFinalized.dispose();
                                        } catch (Exception e4) {
                                            e2 = e4;
                                            try {
                                                FileLog.m3e(e2);
                                                requestDelegate.run(TLdeserialize, null);
                                            } catch (Throwable th) {
                                                e2 = th;
                                                requestDelegate.run(TLdeserialize, null);
                                                throw e2;
                                            }
                                        }
                                        requestDelegate.run(TLdeserialize, null);
                                    }
                                    queryFinalized.dispose();
                                    requestDelegate.run(TLdeserialize, null);
                                }
                            }
                            TLdeserialize = null;
                        } catch (Exception e5) {
                            e = e5;
                            TLdeserialize = null;
                            FileLog.m3e(e);
                            queryFinalized.dispose();
                            requestDelegate.run(TLdeserialize, null);
                        } catch (Throwable th2) {
                            e2 = th2;
                            TLdeserialize = null;
                            requestDelegate.run(TLdeserialize, null);
                            throw e2;
                        }
                        queryFinalized.dispose();
                        requestDelegate.run(TLdeserialize, null);
                    }
                });
            }
        }
    }

    public void updateChatInfo(final ChatFull chatFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000;
                    StringBuilder stringBuilder;
                    SQLiteCursor queryFinalized;
                    if (z) {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT uid FROM chat_settings_v2 WHERE uid = ");
                        stringBuilder.append(chatFull.id);
                        queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                        boolean next = queryFinalized.next();
                        queryFinalized.dispose();
                        if (!next) {
                            return;
                        }
                    }
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                    chatFull.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, chatFull.id);
                    executeFast.bindByteBuffer(2, nativeByteBuffer);
                    executeFast.bindInteger(3, chatFull.pinned_msg_id);
                    executeFast.step();
                    executeFast.dispose();
                    nativeByteBuffer.reuse();
                    if (chatFull instanceof TL_channelFull) {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT date, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i FROM dialogs WHERE did = ");
                        stringBuilder.append(-chatFull.id);
                        queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                        if (queryFinalized.next() && queryFinalized.intValue(3) < chatFull.read_inbox_max_id) {
                            int intValue = queryFinalized.intValue(0);
                            int intValue2 = queryFinalized.intValue(1);
                            long longValue = queryFinalized.longValue(2);
                            int intValue3 = queryFinalized.intValue(4);
                            int intValue4 = queryFinalized.intValue(5);
                            int intValue5 = queryFinalized.intValue(6);
                            SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            int i = intValue3;
                            executeFast2.bindLong(1, (long) (-chatFull.id));
                            executeFast2.bindInteger(2, intValue);
                            executeFast2.bindInteger(3, chatFull.unread_count);
                            executeFast2.bindLong(4, longValue);
                            executeFast2.bindInteger(5, chatFull.read_inbox_max_id);
                            executeFast2.bindInteger(6, Math.max(i, chatFull.read_outbox_max_id));
                            executeFast2.bindLong(7, 0);
                            executeFast2.bindInteger(8, intValue5);
                            executeFast2.bindInteger(9, intValue2);
                            executeFast2.bindInteger(10, 0);
                            executeFast2.bindInteger(11, intValue4);
                            executeFast2.step();
                            executeFast2.dispose();
                        }
                        queryFinalized.dispose();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void updateChannelPinnedMessage(final int i, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT info, pinned FROM chat_settings_v2 WHERE uid = ");
                    stringBuilder.append(i);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    ChatFull chatFull = null;
                    ArrayList arrayList = new ArrayList();
                    if (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            chatFull.pinned_msg_id = queryFinalized.intValue(1);
                        }
                    }
                    queryFinalized.dispose();
                    if (chatFull instanceof TL_channelFull) {
                        chatFull.pinned_msg_id = i2;
                        chatFull.flags |= 32;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                            }
                        });
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                        chatFull.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, i);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.bindInteger(3, chatFull.pinned_msg_id);
                        executeFast.step();
                        executeFast.dispose();
                        nativeByteBuffer.reuse();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void updateChatInfo(int i, int i2, int i3, int i4, int i5) {
        final int i6 = i;
        final int i7 = i3;
        final int i8 = i2;
        final int i9 = i4;
        final int i10 = i5;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT info, pinned FROM chat_settings_v2 WHERE uid = ");
                    stringBuilder.append(i6);
                    int i = 0;
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    ChatFull chatFull = null;
                    ArrayList arrayList = new ArrayList();
                    if (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            chatFull.pinned_msg_id = queryFinalized.intValue(1);
                        }
                    }
                    queryFinalized.dispose();
                    if (chatFull instanceof TL_chatFull) {
                        if (i7 == 1) {
                            while (i < chatFull.participants.participants.size()) {
                                if (((ChatParticipant) chatFull.participants.participants.get(i)).user_id == i8) {
                                    chatFull.participants.participants.remove(i);
                                    break;
                                }
                                i++;
                            }
                        } else if (i7 == 0) {
                            Iterator it = chatFull.participants.participants.iterator();
                            while (it.hasNext()) {
                                if (((ChatParticipant) it.next()).user_id == i8) {
                                    return;
                                }
                            }
                            TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                            tL_chatParticipant.user_id = i8;
                            tL_chatParticipant.inviter_id = i9;
                            tL_chatParticipant.date = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
                            chatFull.participants.participants.add(tL_chatParticipant);
                        } else if (i7 == 2) {
                            while (i < chatFull.participants.participants.size()) {
                                ChatParticipant chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i);
                                if (chatParticipant.user_id == i8) {
                                    Object tL_chatParticipantAdmin;
                                    if (i9 == 1) {
                                        tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                                        tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                        tL_chatParticipantAdmin.date = chatParticipant.date;
                                        tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                    } else {
                                        tL_chatParticipantAdmin = new TL_chatParticipant();
                                        tL_chatParticipantAdmin.user_id = chatParticipant.user_id;
                                        tL_chatParticipantAdmin.date = chatParticipant.date;
                                        tL_chatParticipantAdmin.inviter_id = chatParticipant.inviter_id;
                                    }
                                    chatFull.participants.participants.set(i, tL_chatParticipantAdmin);
                                } else {
                                    i++;
                                }
                            }
                        }
                        chatFull.participants.version = i10;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
                            }
                        });
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                        chatFull.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, i6);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.bindInteger(3, chatFull.pinned_msg_id);
                        executeFast.step();
                        executeFast.dispose();
                        nativeByteBuffer.reuse();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public boolean isMigratedChat(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x006c in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r4 = this;
                r0 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0 = r0.database;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1.<init>();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r2 = "SELECT info FROM chat_settings_v2 WHERE uid = ";	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1.append(r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r2 = r5;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1.append(r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1 = r1.toString();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r2 = 0;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0 = r0.queryFinalized(r1, r3);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1 = 0;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3.<init>();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3 = r0.next();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                if (r3 == 0) goto L_0x003d;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x002c:
                r3 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                if (r3 == 0) goto L_0x003d;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x0032:
                r1 = r3.readInt32(r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r1 = org.telegram.tgnet.TLRPC.ChatFull.TLdeserialize(r3, r1, r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3.reuse();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x003d:
                r0.dispose();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0 = r1;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelFull;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                if (r3 == 0) goto L_0x004c;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x0046:
                r1 = r1.migrated_from_chat_id;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                if (r1 == 0) goto L_0x004c;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x004a:
                r1 = 1;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                goto L_0x004d;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x004c:
                r1 = r2;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x004d:
                r0[r2] = r1;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0 = r0;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                if (r0 == 0) goto L_0x0058;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x0053:
                r0 = r0;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0.countDown();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
            L_0x0058:
                r0 = r0;
                if (r0 == 0) goto L_0x006c;
            L_0x005c:
                goto L_0x0067;
            L_0x005d:
                r0 = move-exception;
                goto L_0x006d;
            L_0x005f:
                r0 = move-exception;
                org.telegram.messenger.FileLog.m3e(r0);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
                r0 = r0;
                if (r0 == 0) goto L_0x006c;
            L_0x0067:
                r0 = r0;
                r0.countDown();
            L_0x006c:
                return;
            L_0x006d:
                r1 = r0;
                if (r1 == 0) goto L_0x0076;
            L_0x0071:
                r1 = r0;
                r1.countDown();
            L_0x0076:
                throw r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.46.run():void");
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return zArr[0];
    }

    public void loadChatInfo(int i, CountDownLatch countDownLatch, boolean z, boolean z2) {
        final int i2 = i;
        final CountDownLatch countDownLatch2 = countDownLatch;
        final boolean z3 = z;
        final boolean z4 = z2;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                Throwable e;
                Throwable th;
                ArrayList arrayList = new ArrayList();
                MessageObject messageObject = null;
                ChatFull chatFull;
                try {
                    StringBuilder stringBuilder;
                    int i;
                    ChatParticipant chatParticipant;
                    StringBuilder stringBuilder2;
                    AbstractSerializedData byteBufferValue;
                    User TLdeserialize;
                    ChannelParticipant TLdeserialize2;
                    TL_chatChannelParticipant tL_chatChannelParticipant;
                    BotInfo botInfo;
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("SELECT info, pinned FROM chat_settings_v2 WHERE uid = ");
                    stringBuilder3.append(i2);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder3.toString(), new Object[0]);
                    if (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue2 = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue2 != null) {
                            ChatFull TLdeserialize3 = ChatFull.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                            try {
                                byteBufferValue2.reuse();
                                TLdeserialize3.pinned_msg_id = queryFinalized.intValue(1);
                                chatFull = TLdeserialize3;
                                queryFinalized.dispose();
                                if (chatFull instanceof TL_chatFull) {
                                    stringBuilder = new StringBuilder();
                                    for (i = 0; i < chatFull.participants.participants.size(); i++) {
                                        chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i);
                                        if (stringBuilder.length() != 0) {
                                            stringBuilder.append(",");
                                        }
                                        stringBuilder.append(chatParticipant.user_id);
                                    }
                                    if (stringBuilder.length() != 0) {
                                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                                    }
                                } else if (chatFull instanceof TL_channelFull) {
                                    access$000 = MessagesStorage.this.database;
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = ");
                                    stringBuilder2.append(-i2);
                                    stringBuilder2.append(" ORDER BY cu.date DESC");
                                    queryFinalized = access$000.queryFinalized(stringBuilder2.toString(), new Object[0]);
                                    chatFull.participants = new TL_chatParticipants();
                                    while (queryFinalized.next()) {
                                        try {
                                            byteBufferValue = queryFinalized.byteBufferValue(0);
                                            if (byteBufferValue == null) {
                                                TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                byteBufferValue.reuse();
                                            } else {
                                                TLdeserialize = null;
                                            }
                                            byteBufferValue = queryFinalized.byteBufferValue(2);
                                            if (byteBufferValue == null) {
                                                TLdeserialize2 = ChannelParticipant.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                byteBufferValue.reuse();
                                            } else {
                                                TLdeserialize2 = null;
                                            }
                                            if (!(TLdeserialize == null || TLdeserialize2 == null)) {
                                                if (TLdeserialize.status != null) {
                                                    TLdeserialize.status.expires = queryFinalized.intValue(1);
                                                }
                                                arrayList.add(TLdeserialize);
                                                TLdeserialize2.date = queryFinalized.intValue(3);
                                                tL_chatChannelParticipant = new TL_chatChannelParticipant();
                                                tL_chatChannelParticipant.user_id = TLdeserialize2.user_id;
                                                tL_chatChannelParticipant.date = TLdeserialize2.date;
                                                tL_chatChannelParticipant.inviter_id = TLdeserialize2.inviter_id;
                                                tL_chatChannelParticipant.channelParticipant = TLdeserialize2;
                                                chatFull.participants.participants.add(tL_chatChannelParticipant);
                                            }
                                        } catch (Throwable e2) {
                                            FileLog.m3e(e2);
                                        }
                                    }
                                    queryFinalized.dispose();
                                    stringBuilder = new StringBuilder();
                                    for (i = 0; i < chatFull.bot_info.size(); i++) {
                                        botInfo = (BotInfo) chatFull.bot_info.get(i);
                                        if (stringBuilder.length() != 0) {
                                            stringBuilder.append(",");
                                        }
                                        stringBuilder.append(botInfo.user_id);
                                    }
                                    if (stringBuilder.length() != 0) {
                                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                                    }
                                }
                                if (countDownLatch2 != null) {
                                    countDownLatch2.countDown();
                                }
                                if ((chatFull instanceof TL_channelFull) && chatFull.pinned_msg_id != 0) {
                                    messageObject = DataQuery.getInstance(MessagesStorage.this.currentAccount).loadPinnedMessage(i2, chatFull.pinned_msg_id, false);
                                }
                                MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, messageObject);
                                if (countDownLatch2 != null) {
                                    countDownLatch2.countDown();
                                }
                            } catch (Exception e3) {
                                e = e3;
                                chatFull = TLdeserialize3;
                                try {
                                    FileLog.m3e(e);
                                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, null);
                                    if (countDownLatch2 == null) {
                                        return;
                                    }
                                    countDownLatch2.countDown();
                                } catch (Throwable e4) {
                                    th = e4;
                                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, null);
                                    if (countDownLatch2 != null) {
                                        countDownLatch2.countDown();
                                    }
                                    throw th;
                                }
                            } catch (Throwable e42) {
                                th = e42;
                                chatFull = TLdeserialize3;
                                MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, null);
                                if (countDownLatch2 != null) {
                                    countDownLatch2.countDown();
                                }
                                throw th;
                            }
                        }
                    }
                    chatFull = null;
                    try {
                        queryFinalized.dispose();
                        if (chatFull instanceof TL_chatFull) {
                            stringBuilder = new StringBuilder();
                            for (i = 0; i < chatFull.participants.participants.size(); i++) {
                                chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i);
                                if (stringBuilder.length() != 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(chatParticipant.user_id);
                            }
                            if (stringBuilder.length() != 0) {
                                MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                            }
                        } else if (chatFull instanceof TL_channelFull) {
                            access$000 = MessagesStorage.this.database;
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = ");
                            stringBuilder2.append(-i2);
                            stringBuilder2.append(" ORDER BY cu.date DESC");
                            queryFinalized = access$000.queryFinalized(stringBuilder2.toString(), new Object[0]);
                            chatFull.participants = new TL_chatParticipants();
                            while (queryFinalized.next()) {
                                byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue == null) {
                                    TLdeserialize = null;
                                } else {
                                    TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                }
                                byteBufferValue = queryFinalized.byteBufferValue(2);
                                if (byteBufferValue == null) {
                                    TLdeserialize2 = null;
                                } else {
                                    TLdeserialize2 = ChannelParticipant.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                }
                                if (TLdeserialize.status != null) {
                                    TLdeserialize.status.expires = queryFinalized.intValue(1);
                                }
                                arrayList.add(TLdeserialize);
                                TLdeserialize2.date = queryFinalized.intValue(3);
                                tL_chatChannelParticipant = new TL_chatChannelParticipant();
                                tL_chatChannelParticipant.user_id = TLdeserialize2.user_id;
                                tL_chatChannelParticipant.date = TLdeserialize2.date;
                                tL_chatChannelParticipant.inviter_id = TLdeserialize2.inviter_id;
                                tL_chatChannelParticipant.channelParticipant = TLdeserialize2;
                                chatFull.participants.participants.add(tL_chatChannelParticipant);
                            }
                            queryFinalized.dispose();
                            stringBuilder = new StringBuilder();
                            for (i = 0; i < chatFull.bot_info.size(); i++) {
                                botInfo = (BotInfo) chatFull.bot_info.get(i);
                                if (stringBuilder.length() != 0) {
                                    stringBuilder.append(",");
                                }
                                stringBuilder.append(botInfo.user_id);
                            }
                            if (stringBuilder.length() != 0) {
                                MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList);
                            }
                        }
                        if (countDownLatch2 != null) {
                            countDownLatch2.countDown();
                        }
                        messageObject = DataQuery.getInstance(MessagesStorage.this.currentAccount).loadPinnedMessage(i2, chatFull.pinned_msg_id, false);
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, messageObject);
                        if (countDownLatch2 != null) {
                            countDownLatch2.countDown();
                        }
                    } catch (Exception e5) {
                        e42 = e5;
                    }
                } catch (Throwable e6) {
                    chatFull = null;
                    e42 = e6;
                    FileLog.m3e(e42);
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, null);
                    if (countDownLatch2 == null) {
                        return;
                    }
                    countDownLatch2.countDown();
                } catch (Throwable e62) {
                    chatFull = null;
                    th = e62;
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(i2, chatFull, arrayList, true, z3, z4, null);
                    if (countDownLatch2 != null) {
                        countDownLatch2.countDown();
                    }
                    throw th;
                }
            }
        });
    }

    public void processPendingRead(long j, long j2, long j3, int i, boolean z) {
        final long j4 = j;
        final long j5 = j2;
        final boolean z2 = z;
        final long j6 = j3;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    int intValue;
                    long longValue;
                    SQLitePreparedStatement executeFast;
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = ");
                    stringBuilder.append(j4);
                    int i = 0;
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    long j = 0;
                    if (queryFinalized.next()) {
                        intValue = queryFinalized.intValue(0);
                        j = (long) queryFinalized.intValue(1);
                        longValue = queryFinalized.longValue(2);
                    } else {
                        intValue = 0;
                        longValue = 0;
                    }
                    queryFinalized.dispose();
                    MessagesStorage.this.database.beginTransaction();
                    int i2 = (int) j4;
                    int intValue2;
                    if (i2 != 0) {
                        j = Math.max(j, (long) ((int) j5));
                        if (z2) {
                            j |= ((long) (-i2)) << 32;
                        }
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                        executeFast.requery();
                        executeFast.bindLong(1, j4);
                        executeFast.bindLong(2, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (j < longValue) {
                            queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT changes()", new Object[0]);
                            intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                            queryFinalized.dispose();
                            i = Math.max(0, intValue - intValue2);
                        }
                    } else {
                        j = (long) ((int) j6);
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                        executeFast.requery();
                        executeFast.bindLong(1, j4);
                        executeFast.bindLong(2, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (j > longValue) {
                            queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT changes()", new Object[0]);
                            intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                            queryFinalized.dispose();
                            i = Math.max(0, intValue - intValue2);
                        }
                    }
                    executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindInteger(2, (int) j);
                    executeFast.bindLong(3, j4);
                    executeFast.step();
                    executeFast.dispose();
                    MessagesStorage.this.database.commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putContacts(ArrayList<TL_contact> arrayList, final boolean z) {
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList(arrayList);
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        if (z) {
                            MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
                        }
                        MessagesStorage.this.database.beginTransaction();
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
                        for (int i = 0; i < arrayList2.size(); i++) {
                            TL_contact tL_contact = (TL_contact) arrayList2.get(i);
                            executeFast.requery();
                            executeFast.bindInteger(1, tL_contact.user_id);
                            executeFast.bindInteger(2, tL_contact.mutual);
                            executeFast.step();
                        }
                        executeFast.dispose();
                        MessagesStorage.this.database.commitTransaction();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void deleteContacts(final ArrayList<Integer> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            String join = TextUtils.join(",", arrayList);
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM contacts WHERE uid IN(");
                            stringBuilder.append(join);
                            stringBuilder.append(")");
                            access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void applyPhoneBookUpdates(final String str, final String str2) {
        if (str.length() != 0 || str2.length() != 0) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        if (str.length() != 0) {
                            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", new Object[]{str})).stepThis().dispose();
                        }
                        if (str2.length() != 0) {
                            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", new Object[]{str2})).stepThis().dispose();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void putCachedPhoneBook(final HashMap<String, Contact> hashMap, final boolean z) {
        if (hashMap != null) {
            if (!hashMap.isEmpty() || z) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(MessagesStorage.this.currentAccount);
                                stringBuilder.append(" save contacts to db ");
                                stringBuilder.append(hashMap.size());
                                FileLog.m0d(stringBuilder.toString());
                            }
                            MessagesStorage.this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
                            MessagesStorage.this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
                            MessagesStorage.this.database.beginTransaction();
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
                            SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
                            Iterator it = hashMap.entrySet().iterator();
                            while (true) {
                                int i = 0;
                                if (!it.hasNext()) {
                                    break;
                                }
                                Contact contact = (Contact) ((Entry) it.next()).getValue();
                                if (!contact.phones.isEmpty()) {
                                    if (!contact.shortPhones.isEmpty()) {
                                        executeFast.requery();
                                        executeFast.bindString(1, contact.key);
                                        executeFast.bindInteger(2, contact.contact_id);
                                        executeFast.bindString(3, contact.first_name);
                                        executeFast.bindString(4, contact.last_name);
                                        executeFast.bindInteger(5, contact.imported);
                                        executeFast.step();
                                        while (i < contact.phones.size()) {
                                            executeFast2.requery();
                                            executeFast2.bindString(1, contact.key);
                                            executeFast2.bindString(2, (String) contact.phones.get(i));
                                            executeFast2.bindString(3, (String) contact.shortPhones.get(i));
                                            executeFast2.bindInteger(4, ((Integer) contact.phoneDeleted.get(i)).intValue());
                                            executeFast2.step();
                                            i++;
                                        }
                                    }
                                }
                            }
                            executeFast.dispose();
                            executeFast2.dispose();
                            MessagesStorage.this.database.commitTransaction();
                            if (z) {
                                MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
                                MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
                                MessagesStorage.this.getCachedPhoneBook(false);
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void getCachedPhoneBook(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                Throwable th;
                Throwable th2;
                SQLiteCursor sQLiteCursor;
                int i;
                SQLiteCursor sQLiteCursor2;
                int i2;
                HashMap hashMap;
                SQLiteDatabase access$000;
                StringBuilder stringBuilder;
                Throwable th3;
                String stringValue;
                Contact contact;
                Object stringValue2;
                int i3 = 6;
                SQLiteCursor queryFinalized;
                int intValue;
                SQLiteCursor queryFinalized2;
                int min;
                StringBuilder stringBuilder2;
                int i4;
                try {
                    SQLiteCursor queryFinalized3 = MessagesStorage.this.database.queryFinalized("SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'", new Object[0]);
                    try {
                        boolean next = queryFinalized3.next();
                        queryFinalized3.dispose();
                        if (next) {
                            queryFinalized3 = MessagesStorage.this.database.queryFinalized("SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1", new Object[0]);
                            int min2 = queryFinalized3.next() ? Math.min(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, queryFinalized3.intValue(0)) : 16;
                            queryFinalized3.dispose();
                            SparseArray sparseArray = new SparseArray(min2);
                            queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
                            while (queryFinalized.next()) {
                                try {
                                    intValue = queryFinalized.intValue(0);
                                    Contact contact2 = (Contact) sparseArray.get(intValue);
                                    if (contact2 == null) {
                                        contact2 = new Contact();
                                        contact2.first_name = queryFinalized.stringValue(1);
                                        contact2.last_name = queryFinalized.stringValue(2);
                                        contact2.imported = queryFinalized.intValue(i3);
                                        if (contact2.first_name == null) {
                                            contact2.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        }
                                        if (contact2.last_name == null) {
                                            contact2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        }
                                        contact2.contact_id = intValue;
                                        sparseArray.put(intValue, contact2);
                                    }
                                    String stringValue3 = queryFinalized.stringValue(3);
                                    if (stringValue3 != null) {
                                        contact2.phones.add(stringValue3);
                                        Object stringValue4 = queryFinalized.stringValue(4);
                                        if (stringValue4 != null) {
                                            if (stringValue4.length() == 8 && stringValue3.length() != 8) {
                                                stringValue4 = PhoneFormat.stripExceptNumbers(stringValue3);
                                            }
                                            contact2.shortPhones.add(stringValue4);
                                            contact2.phoneDeleted.add(Integer.valueOf(queryFinalized.intValue(5)));
                                            contact2.phoneTypes.add(TtmlNode.ANONYMOUS_REGION_ID);
                                            if (sparseArray.size() == DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS) {
                                                break;
                                            }
                                            i3 = 6;
                                        }
                                    }
                                } catch (Throwable th22) {
                                    th = th22;
                                }
                            }
                            queryFinalized.dispose();
                            ContactsController.getInstance(MessagesStorage.this.currentAccount).migratePhoneBookToV7(sparseArray);
                            return;
                        }
                        sQLiteCursor = null;
                        try {
                            queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT COUNT(key) FROM user_contacts_v7 WHERE 1", new Object[0]);
                            try {
                                if (queryFinalized2.next()) {
                                    intValue = queryFinalized2.intValue(0);
                                    min = Math.min(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, intValue);
                                    i = intValue > DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS ? intValue - 5000 : 0;
                                    if (BuildVars.LOGS_ENABLED) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(MessagesStorage.this.currentAccount);
                                        stringBuilder2.append(" current cached contacts count = ");
                                        stringBuilder2.append(intValue);
                                        FileLog.m0d(stringBuilder2.toString());
                                    }
                                    i4 = min;
                                } else {
                                    intValue = 0;
                                    i = intValue;
                                    i4 = 16;
                                }
                                if (queryFinalized2 != null) {
                                    queryFinalized2.dispose();
                                }
                                queryFinalized = queryFinalized2;
                                i3 = i4;
                            } catch (Throwable th222) {
                                th3 = th222;
                                if (queryFinalized2 != null) {
                                    queryFinalized2.dispose();
                                }
                                throw th3;
                            }
                        } catch (Throwable th2222) {
                            th3 = th2222;
                            queryFinalized2 = sQLiteCursor;
                            if (queryFinalized2 != null) {
                                queryFinalized2.dispose();
                            }
                            throw th3;
                        }
                        hashMap = new HashMap(i3);
                        if (i != 0) {
                            access$000 = MessagesStorage.this.database;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0,");
                            stringBuilder.append(intValue);
                            queryFinalized2 = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                        } else {
                            queryFinalized2 = MessagesStorage.this.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1", new Object[0]);
                        }
                        queryFinalized = queryFinalized2;
                        while (queryFinalized.next()) {
                            stringValue = queryFinalized.stringValue(0);
                            contact = (Contact) hashMap.get(stringValue);
                            if (contact == null) {
                                contact = new Contact();
                                contact.contact_id = queryFinalized.intValue(1);
                                contact.first_name = queryFinalized.stringValue(2);
                                contact.last_name = queryFinalized.stringValue(3);
                                contact.imported = queryFinalized.intValue(7);
                                if (contact.first_name == null) {
                                    contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                }
                                if (contact.last_name == null) {
                                    contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                }
                                hashMap.put(stringValue, contact);
                            }
                            stringValue = queryFinalized.stringValue(4);
                            if (stringValue != null) {
                                contact.phones.add(stringValue);
                                stringValue2 = queryFinalized.stringValue(5);
                                if (stringValue2 != null) {
                                    if (stringValue2.length() == 8 && stringValue.length() != 8) {
                                        stringValue2 = PhoneFormat.stripExceptNumbers(stringValue);
                                    }
                                    contact.shortPhones.add(stringValue2);
                                    contact.phoneDeleted.add(Integer.valueOf(queryFinalized.intValue(6)));
                                    contact.phoneTypes.add(TtmlNode.ANONYMOUS_REGION_ID);
                                    if (hashMap.size() == DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS) {
                                        break;
                                    }
                                }
                            }
                        }
                        queryFinalized.dispose();
                        ContactsController.getInstance(MessagesStorage.this.currentAccount).performSyncPhoneBook(hashMap, true, true, false, false, z ^ 1, false);
                    } catch (Throwable th22222) {
                        th3 = th22222;
                        queryFinalized = queryFinalized3;
                        if (queryFinalized != null) {
                            queryFinalized.dispose();
                        }
                        throw th3;
                    }
                } catch (Throwable th222222) {
                    th3 = th222222;
                    queryFinalized = null;
                    if (queryFinalized != null) {
                        queryFinalized.dispose();
                    }
                    throw th3;
                }
            }
        });
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
                    StringBuilder stringBuilder = new StringBuilder();
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        TL_contact tL_contact = new TL_contact();
                        tL_contact.user_id = intValue;
                        tL_contact.mutual = queryFinalized.intValue(1) == 1;
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(",");
                        }
                        arrayList.add(tL_contact);
                        stringBuilder.append(tL_contact.user_id);
                    }
                    queryFinalized.dispose();
                    if (stringBuilder.length() != 0) {
                        MessagesStorage.this.getUsersInternal(stringBuilder.toString(), arrayList2);
                    }
                } catch (Throwable e) {
                    arrayList.clear();
                    arrayList2.clear();
                    FileLog.m3e(e);
                }
                ContactsController.getInstance(MessagesStorage.this.currentAccount).processLoadedContacts(arrayList, arrayList2, 1);
            }
        });
    }

    public void getUnsentMessages(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                AnonymousClass55 anonymousClass55 = this;
                try {
                    int i;
                    SparseArray sparseArray = new SparseArray();
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    ArrayList arrayList4 = new ArrayList();
                    Iterable arrayList5 = new ArrayList();
                    ArrayList arrayList6 = new ArrayList();
                    ArrayList arrayList7 = new ArrayList();
                    Iterable arrayList8 = new ArrayList();
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.mid < 0 AND m.send_state = 1 ORDER BY m.mid DESC LIMIT ");
                    stringBuilder.append(i);
                    boolean z = false;
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    while (queryFinalized.next()) {
                        boolean z2;
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            if (sparseArray.indexOfKey(TLdeserialize.id) < 0) {
                                int i2;
                                MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(z));
                                TLdeserialize.id = queryFinalized.intValue(3);
                                TLdeserialize.date = queryFinalized.intValue(4);
                                if (!queryFinalized.isNull(5)) {
                                    TLdeserialize.random_id = queryFinalized.longValue(5);
                                }
                                TLdeserialize.dialog_id = queryFinalized.longValue(6);
                                TLdeserialize.seq_in = queryFinalized.intValue(7);
                                TLdeserialize.seq_out = queryFinalized.intValue(8);
                                TLdeserialize.ttl = queryFinalized.intValue(9);
                                arrayList.add(TLdeserialize);
                                sparseArray.put(TLdeserialize.id, TLdeserialize);
                                int i3 = (int) TLdeserialize.dialog_id;
                                i = (int) (TLdeserialize.dialog_id >> 32);
                                if (i3 == 0) {
                                    i2 = i3;
                                    if (!arrayList8.contains(Integer.valueOf(i))) {
                                        arrayList8.add(Integer.valueOf(i));
                                    }
                                } else if (i == 1) {
                                    i2 = i3;
                                    if (!arrayList7.contains(Integer.valueOf(i2))) {
                                        arrayList7.add(Integer.valueOf(i2));
                                    }
                                } else {
                                    i2 = i3;
                                    if (i2 < 0) {
                                        i = -i2;
                                        if (!arrayList6.contains(Integer.valueOf(i))) {
                                            arrayList6.add(Integer.valueOf(i));
                                        }
                                    } else if (!arrayList5.contains(Integer.valueOf(i2))) {
                                        arrayList5.add(Integer.valueOf(i2));
                                    }
                                }
                                MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                                TLdeserialize.send_state = queryFinalized.intValue(2);
                                if ((TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i2 == 0) && TLdeserialize.id <= 0) {
                                    z2 = false;
                                } else {
                                    z2 = false;
                                    TLdeserialize.send_state = 0;
                                }
                                if (i2 == 0 && !queryFinalized.isNull(5)) {
                                    TLdeserialize.random_id = queryFinalized.longValue(5);
                                }
                                z = z2;
                            }
                        }
                        z2 = z;
                        z = z2;
                    }
                    i = z;
                    queryFinalized.dispose();
                    if (!arrayList8.isEmpty()) {
                        MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList8), arrayList4, arrayList5);
                    }
                    if (!arrayList5.isEmpty()) {
                        MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList5), arrayList2);
                    }
                    if (!(arrayList6.isEmpty() && arrayList7.isEmpty())) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        for (int i4 = i; i4 < arrayList6.size(); i4++) {
                            Integer num = (Integer) arrayList6.get(i4);
                            if (stringBuilder2.length() != 0) {
                                stringBuilder2.append(",");
                            }
                            stringBuilder2.append(num);
                        }
                        while (i < arrayList7.size()) {
                            Integer num2 = (Integer) arrayList7.get(i);
                            if (stringBuilder2.length() != 0) {
                                stringBuilder2.append(",");
                            }
                            stringBuilder2.append(-num2.intValue());
                            i++;
                        }
                        MessagesStorage.this.getChatsInternal(stringBuilder2.toString(), arrayList3);
                    }
                    SendMessagesHelper.getInstance(MessagesStorage.this.currentAccount).processUnsentMessages(arrayList, arrayList2, arrayList3, arrayList4);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public boolean checkMessageId(long j, int i) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final long j2 = j;
        final int i2 = i;
        final boolean[] zArr2 = zArr;
        final CountDownLatch countDownLatch2 = countDownLatch;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                SQLiteCursor sQLiteCursor;
                Throwable th;
                SQLiteCursor sQLiteCursor2 = null;
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[]{Long.valueOf(j2), Integer.valueOf(i2)}), new Object[0]);
                    try {
                        if (queryFinalized.next()) {
                            zArr2[0] = true;
                        }
                        if (queryFinalized != null) {
                            queryFinalized.dispose();
                        }
                    } catch (Throwable e) {
                        sQLiteCursor = queryFinalized;
                        th = e;
                        sQLiteCursor2 = sQLiteCursor;
                        try {
                            FileLog.m3e(th);
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            countDownLatch2.countDown();
                        } catch (Throwable th2) {
                            th = th2;
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            throw th;
                        }
                    } catch (Throwable e2) {
                        sQLiteCursor = queryFinalized;
                        th = e2;
                        sQLiteCursor2 = sQLiteCursor;
                        if (sQLiteCursor2 != null) {
                            sQLiteCursor2.dispose();
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    th = e3;
                    FileLog.m3e(th);
                    if (sQLiteCursor2 != null) {
                        sQLiteCursor2.dispose();
                    }
                    countDownLatch2.countDown();
                }
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return zArr[0];
    }

    public void getUnreadMention(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    Object[] objArr = new Object[1];
                    int i = 0;
                    objArr[0] = Long.valueOf(j);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", objArr), new Object[0]);
                    if (queryFinalized.next()) {
                        i = queryFinalized.intValue(0);
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            intCallback.run(i);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void getMessages(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
        final int i8 = i;
        final int i9 = i2;
        final boolean z2 = z;
        final long j2 = j;
        final int i10 = i6;
        final int i11 = i4;
        final int i12 = i3;
        final int i13 = i5;
        final int i14 = i7;
        this.storageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesStorage$58$1 */
            class C03921 implements Comparator<Message> {
                C03921() {
                }

                public int compare(Message message, Message message2) {
                    if (message.id <= 0 || message2.id <= 0) {
                        if (message.id >= 0 || message2.id >= 0) {
                            if (message.date > message2.date) {
                                return -1;
                            }
                            if (message.date < message2.date) {
                                return 1;
                            }
                        } else if (message.id < message2.id) {
                            return -1;
                        } else {
                            if (message.id > message2.id) {
                                return 1;
                            }
                        }
                    } else if (message.id > message2.id) {
                        return -1;
                    } else {
                        if (message.id < message2.id) {
                            return 1;
                        }
                    }
                    return null;
                }
            }

            public void run() {
                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:37)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:61)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                /*
                r57 = this;
                r1 = r57;
                r3 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
                r3.<init>();
                r2 = r2;
                r4 = r3;
                r4 = (long) r4;
                r6 = r3;
                r7 = r3;
                r8 = r4;
                if (r8 == 0) goto L_0x0019;
            L_0x0014:
                r10 = r5;
                r8 = (int) r10;
                r8 = -r8;
                goto L_0x001a;
            L_0x0019:
                r8 = 0;
            L_0x001a:
                r10 = 0;
                r12 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
                r13 = 32;
                if (r12 == 0) goto L_0x002a;
            L_0x0022:
                if (r8 == 0) goto L_0x002a;
            L_0x0024:
                r14 = (long) r8;
                r14 = r14 << r13;
                r16 = r4 | r14;
                r4 = r16;
            L_0x002a:
                r14 = r5;
                r16 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
                r12 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
                if (r12 != 0) goto L_0x0036;
            L_0x0033:
                r12 = 10;
                goto L_0x0037;
            L_0x0036:
                r12 = 1;
            L_0x0037:
                r15 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r15.<init>();	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r13.<init>();	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r10.<init>();	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r11 = new android.util.SparseArray;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r11.<init>();	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r14 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r14.<init>();	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r24 = r10;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r9 = r5;	 Catch:{ Exception -> 0x1813, all -> 0x1800 }
                r9 = (int) r9;
                if (r9 == 0) goto L_0x0d3a;
            L_0x0057:
                r10 = r7;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r25 = r6;
                r6 = 3;
                if (r10 != r6) goto L_0x011b;
            L_0x005e:
                r6 = r8;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                if (r6 != 0) goto L_0x011b;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
            L_0x0062:
                r6 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r6 = r6.database;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10.<init>();	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r12 = "SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10.append(r12);	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r26 = r11;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r11 = r5;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10.append(r11);	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10 = r10.toString();	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r11 = 0;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r6 = r6.queryFinalized(r10, r12);	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10 = r6.next();	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                if (r10 == 0) goto L_0x00d5;	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
            L_0x008a:
                r10 = r6.intValue(r11);	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r11 = 1;
                r10 = r10 + r11;
                r12 = r6.intValue(r11);	 Catch:{ Exception -> 0x00cf, all -> 0x00c7 }
                r11 = 2;
                r16 = r6.intValue(r11);	 Catch:{ Exception -> 0x00bf, all -> 0x00b5 }
                r11 = 3;
                r17 = r6.intValue(r11);	 Catch:{ Exception -> 0x00ab, all -> 0x009f }
                goto L_0x00db;
            L_0x009f:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
                r14 = r16;
            L_0x00a8:
                r12 = 0;
                goto L_0x0d21;
            L_0x00ab:
                r0 = move-exception;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
                r14 = r16;
            L_0x00b2:
                r12 = 0;
                goto L_0x0d32;
            L_0x00b5:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
            L_0x00bc:
                r12 = 0;
                goto L_0x0d20;
            L_0x00bf:
                r0 = move-exception;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
            L_0x00c4:
                r12 = 0;
                goto L_0x0d31;
            L_0x00c7:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                goto L_0x0d1e;
            L_0x00cf:
                r0 = move-exception;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                goto L_0x0d2f;
            L_0x00d5:
                r10 = 0;
                r12 = 0;
                r16 = 0;
                r17 = 0;
            L_0x00db:
                r6.dispose();	 Catch:{ Exception -> 0x0105, all -> 0x00f3 }
                r6 = r2;
                r31 = r7;
                r29 = r10;
                r27 = r13;
                r28 = r14;
                r32 = r15;
                r33 = r16;
                r16 = r17;
                r2 = 0;
                r30 = 0;
                r13 = r12;
                goto L_0x0452;
            L_0x00f3:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
                r14 = r16;
                r20 = r17;
                r12 = 0;
                r17 = 0;
                r19 = 0;
                goto L_0x1863;
            L_0x0105:
                r0 = move-exception;
                r6 = r2;
                r15 = r3;
                r11 = r10;
                r13 = r12;
                r14 = r16;
                r20 = r17;
                r12 = 0;
                r17 = 0;
                r19 = 0;
                goto L_0x1823;
            L_0x0115:
                r0 = move-exception;
                goto L_0x0d19;
            L_0x0118:
                r0 = move-exception;
                goto L_0x0d2c;
            L_0x011b:
                r26 = r11;
                r6 = r7;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = 1;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r6 == r10) goto L_0x043f;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
            L_0x0122:
                r6 = r7;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = 3;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r6 == r10) goto L_0x043f;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
            L_0x0127:
                r6 = r7;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = 4;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r6 == r10) goto L_0x043f;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
            L_0x012c:
                r6 = r8;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r6 != 0) goto L_0x043f;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
            L_0x0130:
                r6 = r7;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = 2;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r6 != r10) goto L_0x03e4;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
            L_0x0135:
                r6 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r6 = r6.database;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10.<init>();	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r11 = "SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10.append(r11);	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r27 = r13;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r28 = r14;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r13 = r5;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10.append(r13);	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = r10.toString();	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r11 = 0;	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r6 = r6.queryFinalized(r10, r13);	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                r10 = r6.next();	 Catch:{ Exception -> 0x0d29, all -> 0x0d16 }
                if (r10 == 0) goto L_0x01c9;
            L_0x015f:
                r4 = r6.intValue(r11);	 Catch:{ Exception -> 0x0118, all -> 0x0115 }
                r10 = (long) r4;
                r5 = 1;
                r13 = r6.intValue(r5);	 Catch:{ Exception -> 0x01c0, all -> 0x01b5 }
                r5 = 2;
                r14 = r6.intValue(r5);	 Catch:{ Exception -> 0x01ac, all -> 0x01a1 }
                r5 = 3;
                r16 = r6.intValue(r5);	 Catch:{ Exception -> 0x0198, all -> 0x018d }
                r17 = 0;
                r5 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1));
                if (r5 == 0) goto L_0x0185;
            L_0x0179:
                if (r8 == 0) goto L_0x0185;
            L_0x017b:
                r29 = r4;
                r4 = (long) r8;
                r17 = 32;
                r4 = r4 << r17;
                r17 = r10 | r4;
                goto L_0x0189;
            L_0x0185:
                r29 = r4;
                r17 = r10;
            L_0x0189:
                r5 = r29;
                r4 = 1;
                goto L_0x01d4;
            L_0x018d:
                r0 = move-exception;
                r29 = r4;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x00a8;
            L_0x0198:
                r0 = move-exception;
                r29 = r4;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x00b2;
            L_0x01a1:
                r0 = move-exception;
                r29 = r4;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x00bc;
            L_0x01ac:
                r0 = move-exception;
                r29 = r4;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x00c4;
            L_0x01b5:
                r0 = move-exception;
                r29 = r4;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x0d1e;
            L_0x01c0:
                r0 = move-exception;
                r29 = r4;
                r6 = r2;
                r15 = r3;
                r11 = r29;
                goto L_0x0d2f;
            L_0x01c9:
                r17 = r4;
                r5 = r25;
                r4 = 0;
                r13 = 0;
                r14 = 0;
                r16 = 0;
                r29 = 0;
            L_0x01d4:
                r6.dispose();	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
                if (r4 != 0) goto L_0x02b7;	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
            L_0x01d9:
                r6 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
                r6 = r6.database;	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
                r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
                r11 = "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";	 Catch:{ Exception -> 0x03d3, all -> 0x03c0 }
                r30 = r4;
                r31 = r7;
                r4 = 1;
                r7 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x02a8, all -> 0x0297 }
                r33 = r14;
                r32 = r15;
                r14 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7[r14] = r4;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = java.lang.String.format(r10, r11, r7);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r6.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r4.next();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 == 0) goto L_0x022d;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0207:
                r6 = r4.intValue(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = 1;
                r14 = r4.intValue(r7);	 Catch:{ Exception -> 0x0220, all -> 0x0211 }
                goto L_0x0231;
            L_0x0211:
                r0 = move-exception;
                r21 = r0;
                r15 = r3;
                r11 = r6;
                r20 = r16;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                goto L_0x0284;
            L_0x0220:
                r0 = move-exception;
                r15 = r3;
                r11 = r6;
                r20 = r16;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                goto L_0x0292;
            L_0x022d:
                r6 = r29;
                r14 = r33;
            L_0x0231:
                r4.dispose();	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                if (r6 == 0) goto L_0x026f;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
            L_0x0236:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r4 = r4.database;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r10 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r11 = 2;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r15 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r34 = r12;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r11 = r5;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r11 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r12 = 0;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r15[r12] = r11;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r11 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r19 = 1;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r15[r19] = r11;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r7 = java.lang.String.format(r7, r10, r15);	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r10 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r4 = r4.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r7 = r4.next();	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                if (r7 == 0) goto L_0x026b;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
            L_0x0266:
                r7 = r4.intValue(r12);	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                r13 = r7;	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
            L_0x026b:
                r4.dispose();	 Catch:{ Exception -> 0x0289, all -> 0x0279 }
                goto L_0x0271;
            L_0x026f:
                r34 = r12;
            L_0x0271:
                r29 = r6;
                r33 = r14;
            L_0x0275:
                r10 = r17;
                goto L_0x03fa;
            L_0x0279:
                r0 = move-exception;
                r21 = r0;
                r15 = r3;
                r11 = r6;
                r20 = r16;
                r19 = r30;
                r7 = r31;
            L_0x0284:
                r12 = 0;
                r17 = 0;
                goto L_0x17bf;
            L_0x0289:
                r0 = move-exception;
                r15 = r3;
                r11 = r6;
                r20 = r16;
                r19 = r30;
                r7 = r31;
            L_0x0292:
                r12 = 0;
                r17 = 0;
                goto L_0x17d4;
            L_0x0297:
                r0 = move-exception;
                r33 = r14;
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                goto L_0x0cff;
            L_0x02a8:
                r0 = move-exception;
                r33 = r14;
                r6 = r2;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                goto L_0x043a;
            L_0x02b7:
                r30 = r4;
                r31 = r7;
                r34 = r12;
                r33 = r14;
                r32 = r15;
                if (r5 != 0) goto L_0x033d;
            L_0x02c3:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.database;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 1;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.lang.String.format(r6, r7, r11);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r4.next();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 == 0) goto L_0x02ee;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x02e9:
                r6 = r4.intValue(r12);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x02ef;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x02ee:
                r6 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x02ef:
                r4.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 != r13) goto L_0x0339;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x02f4:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.database;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 1;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.lang.String.format(r6, r7, r11);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r4.next();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 == 0) goto L_0x0336;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x031a:
                r5 = r4.intValue(r12);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = (long) r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r12 == 0) goto L_0x0332;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0325:
                if (r8 == 0) goto L_0x0332;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0327:
                r10 = (long) r8;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 32;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = r10 << r12;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = r6 | r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r29 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r17 = r14;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x0336;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0332:
                r29 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r17 = r6;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0336:
                r4.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0339:
                r6 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x033a:
                r5 = r6;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x0275;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x033d:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.database;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 3;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 1;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.lang.String.format(r6, r7, r11);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.queryFinalized(r6, r10);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r4.next();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = 1;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r6 ^ r7;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 == 0) goto L_0x0275;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0377:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.database;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d";	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r14 = 1;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11[r14] = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = java.lang.String.format(r6, r7, r11);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r4.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r4.next();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r6 == 0) goto L_0x03ba;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03a4:
                r5 = r4.intValue(r12);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = (long) r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r12 == 0) goto L_0x03b8;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03af:
                if (r8 == 0) goto L_0x03b8;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03b1:
                r10 = (long) r8;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r12 = 32;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = r10 << r12;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r17 = r6 | r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x03ba;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03b8:
                r17 = r6;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03ba:
                r6 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x033a;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03c0:
                r0 = move-exception;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r30 = r4;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r31 = r7;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r33 = r14;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r21 = r0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r15 = r3;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r20 = r16;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = r29;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r19 = r30;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x0cff;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03d3:
                r0 = move-exception;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r30 = r4;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r31 = r7;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r33 = r14;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r15 = r3;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r20 = r16;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r11 = r29;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r19 = r30;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x043a;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03e4:
                r31 = r7;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r34 = r12;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r27 = r13;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r28 = r14;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r32 = r15;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r10 = r4;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r5 = r25;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r13 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r16 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r29 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r30 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r33 = 0;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03fa:
                if (r2 > r13) goto L_0x040b;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x03fc:
                r14 = r34;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r13 >= r14) goto L_0x0401;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0400:
                goto L_0x040d;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x0401:
                r4 = r13 - r2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r2 = r2 + 10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r6 = r2;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r2 = r4;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r25 = r5;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = r10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                goto L_0x0452;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x040b:
                r14 = r34;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
            L_0x040d:
                r4 = r13 + 10;	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                r4 = java.lang.Math.max(r2, r4);	 Catch:{ Exception -> 0x042d, all -> 0x0427 }
                if (r13 >= r14) goto L_0x0421;
            L_0x0415:
                r6 = r4;
                r25 = r5;
                r2 = 0;
                r4 = 0;
                r13 = 0;
                r29 = 0;
                r30 = 0;
                goto L_0x0452;
            L_0x0421:
                r6 = r4;
                r25 = r5;
                r4 = r10;
                r2 = 0;
                goto L_0x0452;
            L_0x0427:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                goto L_0x0cf4;
            L_0x042d:
                r0 = move-exception;
                r6 = r2;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
            L_0x043a:
                r12 = 0;
                r17 = 0;
                goto L_0x1823;
            L_0x043f:
                r31 = r7;
                r27 = r13;
                r28 = r14;
                r32 = r15;
                r6 = r2;
                r2 = 0;
                r13 = 0;
                r16 = 0;
                r29 = 0;
                r30 = 0;
                r33 = 0;
            L_0x0452:
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r7 = r7.database;	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r11 = "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)";	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r12 = 1;	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r14 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0d04, all -> 0x0cf1 }
                r35 = r13;
                r12 = r5;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r12 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r13 = 0;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r14[r13] = r12;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = java.lang.String.format(r10, r11, r14);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r11 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r7 = r7.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = r7.next();	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                if (r10 == 0) goto L_0x04d5;
            L_0x047a:
                r10 = r7.intValue(r13);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11 = 1;
                if (r10 != r11) goto L_0x0483;
            L_0x0481:
                r10 = 1;
                goto L_0x0484;
            L_0x0483:
                r10 = 0;
            L_0x0484:
                r7.dispose();	 Catch:{ Exception -> 0x049e, all -> 0x0489 }
                goto L_0x052c;
            L_0x0489:
                r0 = move-exception;
                r21 = r0;
                r15 = r3;
                r17 = r10;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
            L_0x049b:
                r12 = 0;
                goto L_0x1863;
            L_0x049e:
                r0 = move-exception;
                r2 = r0;
                r15 = r3;
                r17 = r10;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
            L_0x04af:
                r12 = 0;
                goto L_0x1824;
            L_0x04b2:
                r0 = move-exception;
                r21 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                goto L_0x0cff;
            L_0x04c4:
                r0 = move-exception;
                r2 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                goto L_0x0d11;
            L_0x04d5:
                r7.dispose();	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r7 = r7.database;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r11 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r12 = 1;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r14 = r5;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r14 = 0;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = java.lang.String.format(r10, r11, r13);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r11 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r7 = r7.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = r7.next();	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                if (r10 == 0) goto L_0x0528;
            L_0x04fe:
                r10 = r7.intValue(r14);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                if (r10 == 0) goto L_0x0528;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
            L_0x0504:
                r11 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11 = r11.database;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r12 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11 = r11.executeFast(r12);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.requery();	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r12 = r5;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r14 = 1;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.bindLong(r14, r12);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r12 = 0;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r13 = 2;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.bindInteger(r13, r12);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r12 = 3;	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.bindInteger(r12, r10);	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.step();	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
                r11.dispose();	 Catch:{ Exception -> 0x04c4, all -> 0x04b2 }
            L_0x0528:
                r7.dispose();	 Catch:{ Exception -> 0x0ced, all -> 0x0ce9 }
                r10 = 0;
            L_0x052c:
                r7 = r7;	 Catch:{ Exception -> 0x0cd4, all -> 0x0cbe }
                r11 = 3;
                if (r7 == r11) goto L_0x084d;
            L_0x0531:
                r7 = r7;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r11 = 4;
                if (r7 == r11) goto L_0x084d;
            L_0x0536:
                if (r30 == 0) goto L_0x053f;
            L_0x0538:
                r7 = r7;	 Catch:{ Exception -> 0x049e, all -> 0x0489 }
                r11 = 2;
                if (r7 != r11) goto L_0x053f;
            L_0x053d:
                goto L_0x084d;
            L_0x053f:
                r7 = r7;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r11 = 1;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                if (r7 != r11) goto L_0x060d;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
            L_0x0544:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r2 = r2.database;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r11 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1";	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r12 = 2;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r14 = r5;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r14 = 0;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r12 = r3;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r15 = 1;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r13[r15] = r12;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r7 = java.lang.String.format(r7, r11, r13);	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r11 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r2 = r2.queryFinalized(r7, r11);	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r7 = r2.next();	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                if (r7 == 0) goto L_0x0583;
            L_0x0573:
                r7 = r2.intValue(r14);	 Catch:{ Exception -> 0x049e, all -> 0x0489 }
                r11 = (long) r7;
                if (r8 == 0) goto L_0x0581;
            L_0x057a:
                r7 = (long) r8;
                r13 = 32;
                r7 = r7 << r13;
                r13 = r11 | r7;
                goto L_0x0585;
            L_0x0581:
                r13 = r11;
                goto L_0x0585;
            L_0x0583:
                r13 = 0;
            L_0x0585:
                r2.dispose();	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r7 = 0;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r2 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1));	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                if (r2 == 0) goto L_0x05d1;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
            L_0x058e:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r2 = r2.database;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r11 = 5;	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x083a, all -> 0x0826 }
                r36 = r10;
                r10 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r11] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r11] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.String.format(r7, r8, r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0713;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x05d1:
                r36 = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = 4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0713;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x060d:
                r36 = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r7 == 0) goto L_0x072a;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0613:
                r10 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r7 == 0) goto L_0x06db;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0619:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r13] = r11;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = r3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r14 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12[r14] = r11;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = java.lang.String.format(r7, r10, r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = r2.next();	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r7 == 0) goto L_0x0657;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0648:
                r7 = r2.intValue(r13);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = (long) r7;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r8 == 0) goto L_0x0659;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x064f:
                r7 = (long) r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 32;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = r7 << r12;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = r10 | r7;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = r12;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0659;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0657:
                r10 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0659:
                r2.dispose();	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r2 == 0) goto L_0x06a2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0662:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r14 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r14 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r14 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.String.format(r7, r8, r13);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0713;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x06a2:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = 4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r5] = r4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r2.queryFinalized(r4, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0713;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x06db:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = r4.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = 4;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = 2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10[r8] = r2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r8 = 3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10[r8] = r2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = java.lang.String.format(r5, r7, r10);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = r4.queryFinalized(r2, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0713:
                r4 = r2;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r40 = r3;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = r16;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r3 = r25;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = r29;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r19 = r30;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = r31;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r14 = r33;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r13 = r35;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r17 = r36;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r2 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x1117;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x072a:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = r4.database;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = 1;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = r5;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r12 = 0;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = java.lang.String.format(r5, r7, r11);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r4 = r4.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                r5 = r4.next();	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                if (r5 == 0) goto L_0x0755;	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
            L_0x0750:
                r5 = r4.intValue(r12);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x0756;
            L_0x0755:
                r5 = 0;
            L_0x0756:
                r4.dispose();	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = r4.database;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = "SELECT max(end) FROM messages_holes WHERE uid = %d";	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11 = 1;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13 = r5;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12[r13] = r11;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = java.lang.String.format(r7, r10, r12);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = r4.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = r4.next();	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                if (r7 == 0) goto L_0x078e;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x077f:
                r7 = r4.intValue(r13);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = (long) r7;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                if (r8 == 0) goto L_0x0790;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x0786:
                r7 = (long) r8;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = 32;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = r7 << r12;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = r10 | r7;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = r12;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                goto L_0x0790;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x078e:
                r10 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x0790:
                r4.dispose();	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                if (r4 == 0) goto L_0x07d0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x0799:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = r4.database;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = 4;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r14 = r5;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r14 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11 = 1;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13[r11] = r10;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = 2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13[r10] = r2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = 3;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r13[r10] = r2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.String.format(r7, r8, r13);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = r4.queryFinalized(r2, r8);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                goto L_0x07ff;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x07d0:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r4 = r4.database;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = 3;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = r5;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r12 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = 1;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11[r10] = r2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r10 = 2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r11[r10] = r2;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r7 = 0;	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
                r2 = r4.queryFinalized(r2, r8);	 Catch:{ Exception -> 0x081c, all -> 0x0815 }
            L_0x07ff:
                r4 = r2;
                r40 = r3;
                r12 = r5;
                r5 = r16;
                r3 = r25;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                r17 = r36;
                goto L_0x0ed1;
            L_0x0815:
                r0 = move-exception;
                r21 = r0;
                r15 = r3;
                r12 = r5;
                goto L_0x099a;
            L_0x081c:
                r0 = move-exception;
                r2 = r0;
                r15 = r3;
                r12 = r5;
                goto L_0x09ae;
            L_0x0822:
                r0 = move-exception;
                goto L_0x0829;
            L_0x0824:
                r0 = move-exception;
                goto L_0x083d;
            L_0x0826:
                r0 = move-exception;
                r36 = r10;
            L_0x0829:
                r21 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                goto L_0x0cd0;
            L_0x083a:
                r0 = move-exception;
                r36 = r10;
            L_0x083d:
                r2 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                goto L_0x0ce5;
            L_0x084d:
                r36 = r10;
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r2 = r2.database;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r10 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r11 = 1;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r13 = r5;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r13 = 0;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r12[r13] = r11;	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r7 = java.lang.String.format(r7, r10, r12);	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r2 = r2.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                r7 = r2.next();	 Catch:{ Exception -> 0x0cbc, all -> 0x0cba }
                if (r7 == 0) goto L_0x087a;
            L_0x0875:
                r7 = r2.intValue(r13);	 Catch:{ Exception -> 0x0824, all -> 0x0822 }
                goto L_0x087b;
            L_0x087a:
                r7 = 0;
            L_0x087b:
                r2.dispose();	 Catch:{ Exception -> 0x0ca3, all -> 0x0c8b }
                r2 = r7;	 Catch:{ Exception -> 0x0ca3, all -> 0x0c8b }
                r10 = 4;
                if (r2 != r10) goto L_0x09be;
            L_0x0883:
                r2 = r9;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r2 == 0) goto L_0x09be;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0887:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.database;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0";	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = 2;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = r5;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = r9;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = 1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13[r15] = r12;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = java.lang.String.format(r10, r11, r13);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = r2.next();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r10 == 0) goto L_0x08bb;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08b6:
                r10 = r2.intValue(r14);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x08bc;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08bb:
                r10 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08bc:
                r2.dispose();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.database;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0";	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = 2;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r37 = r12;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r5;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15[r12] = r11;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r9;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = 1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15[r14] = r11;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r37;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = java.lang.String.format(r11, r13, r15);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.queryFinalized(r11, r13);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r2.next();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r11 == 0) goto L_0x08f7;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08f2:
                r11 = r2.intValue(r12);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x08f8;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08f7:
                r11 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08f8:
                r2.dispose();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r10 == r2) goto L_0x09be;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x08fe:
                if (r11 == r2) goto L_0x09be;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0900:
                if (r10 != r11) goto L_0x0905;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0902:
                r11 = r10;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x09c4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0905:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.database;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = 3;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r38 = r4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = r5;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15[r5] = r4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = 1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15[r5] = r4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = 2;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15[r5] = r4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.lang.String.format(r12, r13, r15);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.queryFinalized(r4, r12);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = r2.next();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r4 == 0) goto L_0x093d;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x093c:
                r10 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x093d:
                r2.dispose();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r10 == r2) goto L_0x09c0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0943:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.database;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = 3;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = r5;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12[r13] = r10;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = 1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12[r13] = r10;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = 2;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12[r13] = r10;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = java.lang.String.format(r4, r5, r12);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r5 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r2.queryFinalized(r4, r10);	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = r2.next();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r4 == 0) goto L_0x0979;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0978:
                r11 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0979:
                r2.dispose();	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = -1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r11 == r2) goto L_0x09c0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x097f:
                r4 = (long) r11;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                if (r2 == 0) goto L_0x0992;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0986:
                if (r8 == 0) goto L_0x0992;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0988:
                r12 = (long) r8;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = 32;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = r12 << r2;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = r4 | r12;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r31 = r11;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = r14;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x09c4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0992:
                r31 = r11;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x09c4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x0995:
                r0 = move-exception;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r21 = r0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = r3;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = r7;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x099a:
                r20 = r16;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r29;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r19 = r30;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r7 = r31;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = r33;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = r35;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r17 = r36;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x1863;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09aa:
                r0 = move-exception;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r2 = r0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = r3;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = r7;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09ae:
                r20 = r16;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r11 = r29;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r19 = r30;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r7 = r31;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = r33;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = r35;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r17 = r36;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x1824;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09be:
                r38 = r4;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09c0:
                r11 = r25;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r4 = r38;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09c4:
                if (r11 == 0) goto L_0x09c8;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09c6:
                r2 = 1;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                goto L_0x09c9;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09c8:
                r2 = 0;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09c9:
                if (r2 == 0) goto L_0x0a35;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
            L_0x09cb:
                r10 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r10 = r10.database;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r13 = "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r14 = 3;	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x09aa, all -> 0x0995 }
                r41 = r2;
                r40 = r3;
                r2 = r5;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = 0;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r15[r3] = r2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = 1;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r15[r3] = r2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = 2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r15[r3] = r2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = java.lang.String.format(r12, r13, r15);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = 0;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = r10.queryFinalized(r2, r12);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = r2.next();	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                if (r3 == 0) goto L_0x0a06;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
            L_0x0a04:
                r41 = 0;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
            L_0x0a06:
                r2.dispose();	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                goto L_0x0a39;
            L_0x0a0a:
                r0 = move-exception;
                r21 = r0;
                r12 = r7;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                r17 = r36;
                r15 = r40;
                goto L_0x1863;
            L_0x0a20:
                r0 = move-exception;
                r2 = r0;
                r12 = r7;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
                r17 = r36;
                r15 = r40;
                goto L_0x1824;
            L_0x0a35:
                r41 = r2;
                r40 = r3;
            L_0x0a39:
                if (r41 == 0) goto L_0x0bbf;
            L_0x0a3b:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r10 = "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = 2;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r14 = r5;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r14 = 0;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r15 = 1;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13[r15] = r12;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = java.lang.String.format(r3, r10, r13);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r10 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = r2.queryFinalized(r3, r10);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = r2.next();	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                if (r3 == 0) goto L_0x0a79;
            L_0x0a68:
                r3 = r2.intValue(r14);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12 = (long) r3;
                if (r8 == 0) goto L_0x0a76;
            L_0x0a6f:
                r14 = (long) r8;
                r3 = 32;
                r14 = r14 << r3;
                r17 = r12 | r14;
                goto L_0x0a7b;
            L_0x0a76:
                r17 = r12;
                goto L_0x0a7b;
            L_0x0a79:
                r17 = 0;
            L_0x0a7b:
                r2.dispose();	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r10 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = 2;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r14 = r5;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r14 = 0;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r12 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r15 = 1;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r13[r15] = r12;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = java.lang.String.format(r3, r10, r13);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r10 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = r2.queryFinalized(r3, r10);	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = r2.next();	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                if (r3 == 0) goto L_0x0abb;
            L_0x0aab:
                r3 = r2.intValue(r14);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r14 = (long) r3;
                if (r8 == 0) goto L_0x0abd;
            L_0x0ab2:
                r12 = (long) r8;
                r3 = 32;
                r12 = r12 << r3;
                r19 = r14 | r12;
                r14 = r19;
                goto L_0x0abd;
            L_0x0abb:
                r14 = 1;
            L_0x0abd:
                r2.dispose();	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = 0;
                r10 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1));
                if (r10 != 0) goto L_0x0b1c;
            L_0x0ac6:
                r2 = 1;
                r10 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));
                if (r10 == 0) goto L_0x0acd;
            L_0x0acc:
                goto L_0x0b1c;
            L_0x0acd:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = r2.database;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r8 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = 6;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = r5;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = 0;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r13] = r10;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = 1;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r13] = r10;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = r6 / 2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = 2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r13] = r10;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = r5;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r10 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r13 = 3;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r13] = r10;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r5 = 4;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r5] = r4;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r4 = r6 / 2;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r5 = 5;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r12[r5] = r4;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r3 = java.lang.String.format(r3, r8, r12);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r4 = 0;	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0a20, all -> 0x0a0a }
                r42 = r7;
                r43 = r11;
                goto L_0x0b8d;
            L_0x0b1c:
                r2 = 0;
                r10 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1));
                if (r10 != 0) goto L_0x0b2e;
            L_0x0b22:
                r17 = NUM; // 0x3b9aca00 float:0.NUM double:4.94065646E-315;
                if (r8 == 0) goto L_0x0b2e;
            L_0x0b27:
                r2 = (long) r8;
                r8 = 32;
                r2 = r2 << r8;
                r12 = r17 | r2;
                goto L_0x0b30;
            L_0x0b2e:
                r12 = r17;
            L_0x0b30:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r2 = r2.database;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r8 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0ba9, all -> 0x0b92 }
                r42 = r7;
                r10 = 8;
                r7 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r43 = r11;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = r5;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r11 = 0;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r11] = r10;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r11 = 1;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r11] = r10;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r11 = 2;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r11] = r10;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = r6 / 2;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r11 = 3;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r11] = r10;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = r5;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r11 = 4;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r11] = r10;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r5 = 5;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r5] = r4;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r4 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r5 = 6;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r5] = r4;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r4 = r6 / 2;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r5 = 7;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r7[r5] = r4;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r3 = java.lang.String.format(r3, r8, r7);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r4 = 0;	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
            L_0x0b8d:
                r3 = r2;
                r13 = r35;
                goto L_0x0c56;
            L_0x0b92:
                r0 = move-exception;
                r42 = r7;
            L_0x0b95:
                r21 = r0;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
            L_0x0ba3:
                r17 = r36;
                r15 = r40;
                goto L_0x0c9f;
            L_0x0ba9:
                r0 = move-exception;
                r42 = r7;
            L_0x0bac:
                r2 = r0;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r13 = r35;
            L_0x0bb9:
                r17 = r36;
                r15 = r40;
                goto L_0x0cb6;
            L_0x0bbf:
                r42 = r7;
                r43 = r11;
                r2 = r7;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r3 = 2;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                if (r2 != r3) goto L_0x0c52;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
            L_0x0bc8:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r2 = r2.database;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r7 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r8 = 1;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r11 = r5;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r11 = 0;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r3 = java.lang.String.format(r3, r7, r10);	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r7 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r2 = r2.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r3 = r2.next();	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                if (r3 == 0) goto L_0x0bf7;
            L_0x0bee:
                r3 = r2.intValue(r11);	 Catch:{ Exception -> 0x0bf5, all -> 0x0bf3 }
                goto L_0x0bf8;
            L_0x0bf3:
                r0 = move-exception;
                goto L_0x0b95;
            L_0x0bf5:
                r0 = move-exception;
                goto L_0x0bac;
            L_0x0bf7:
                r3 = 0;
            L_0x0bf8:
                r2.dispose();	 Catch:{ Exception -> 0x0c7b, all -> 0x0c6a }
                r13 = r35;
                if (r3 != r13) goto L_0x0c50;
            L_0x0bff:
                r2 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r2 = r2.database;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r7 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = 6;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = r5;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = 0;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = 1;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = r6 / 2;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = 2;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = r5;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r11 = 3;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r5 = 4;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r5] = r4;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r4 = r6 / 2;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r5 = 5;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r10[r5] = r4;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r3 = java.lang.String.format(r3, r7, r10);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r4 = 0;	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0c4e, all -> 0x0c4c }
                r3 = r2;
                r2 = 1;
                goto L_0x0c57;
            L_0x0c4c:
                r0 = move-exception;
                goto L_0x0c6d;
            L_0x0c4e:
                r0 = move-exception;
                goto L_0x0c7e;
            L_0x0c50:
                r2 = 0;
                goto L_0x0c55;
            L_0x0c52:
                r13 = r35;
                r2 = 0;
            L_0x0c55:
                r3 = r2;
            L_0x0c56:
                r2 = 0;
            L_0x0c57:
                r4 = r3;
                r5 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r17 = r36;
                r12 = r42;
                r3 = r43;
                goto L_0x1117;
            L_0x0c6a:
                r0 = move-exception;
                r13 = r35;
            L_0x0c6d:
                r21 = r0;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                goto L_0x0ba3;
            L_0x0c7b:
                r0 = move-exception;
                r13 = r35;
            L_0x0c7e:
                r2 = r0;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                goto L_0x0bb9;
            L_0x0c8b:
                r0 = move-exception;
                r42 = r7;
                r13 = r35;
                r21 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r17 = r36;
            L_0x0c9f:
                r12 = r42;
                goto L_0x1863;
            L_0x0ca3:
                r0 = move-exception;
                r42 = r7;
                r13 = r35;
                r2 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
                r17 = r36;
            L_0x0cb6:
                r12 = r42;
                goto L_0x1824;
            L_0x0cba:
                r0 = move-exception;
                goto L_0x0cc1;
            L_0x0cbc:
                r0 = move-exception;
                goto L_0x0cd7;
            L_0x0cbe:
                r0 = move-exception;
                r36 = r10;
            L_0x0cc1:
                r13 = r35;
                r21 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
            L_0x0cd0:
                r17 = r36;
                goto L_0x049b;
            L_0x0cd4:
                r0 = move-exception;
                r36 = r10;
            L_0x0cd7:
                r13 = r35;
                r2 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
            L_0x0ce5:
                r17 = r36;
                goto L_0x04af;
            L_0x0ce9:
                r0 = move-exception;
                r13 = r35;
                goto L_0x0cf2;
            L_0x0ced:
                r0 = move-exception;
                r13 = r35;
                goto L_0x0d05;
            L_0x0cf1:
                r0 = move-exception;
            L_0x0cf2:
                r21 = r0;
            L_0x0cf4:
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
            L_0x0cff:
                r12 = 0;
                r17 = 0;
                goto L_0x1863;
            L_0x0d04:
                r0 = move-exception;
            L_0x0d05:
                r2 = r0;
                r15 = r3;
                r20 = r16;
                r11 = r29;
                r19 = r30;
                r7 = r31;
                r14 = r33;
            L_0x0d11:
                r12 = 0;
                r17 = 0;
                goto L_0x1824;
            L_0x0d16:
                r0 = move-exception;
                r31 = r7;
            L_0x0d19:
                r21 = r0;
                r6 = r2;
                r15 = r3;
                r11 = 0;
            L_0x0d1e:
                r12 = 0;
                r13 = 0;
            L_0x0d20:
                r14 = 0;
            L_0x0d21:
                r17 = 0;
            L_0x0d23:
                r19 = 0;
                r20 = 0;
                goto L_0x1863;
            L_0x0d29:
                r0 = move-exception;
                r31 = r7;
            L_0x0d2c:
                r6 = r2;
                r15 = r3;
                r11 = 0;
            L_0x0d2f:
                r12 = 0;
                r13 = 0;
            L_0x0d31:
                r14 = 0;
            L_0x0d32:
                r17 = 0;
            L_0x0d34:
                r19 = 0;
                r20 = 0;
                goto L_0x1823;
            L_0x0d3a:
                r40 = r3;
                r25 = r6;
                r31 = r7;
                r26 = r11;
                r27 = r13;
                r28 = r14;
                r32 = r15;
                r14 = r12;
                r3 = r7;	 Catch:{ Exception -> 0x17ed, all -> 0x17d7 }
                r6 = 3;
                if (r3 != r6) goto L_0x0e7c;
            L_0x0d4e:
                r3 = r8;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                if (r3 != 0) goto L_0x0e7c;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
            L_0x0d52:
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r3 = r3.database;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r7 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r8 = 1;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r11 = r5;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r11 = 0;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r10[r11] = r8;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r6 = java.lang.String.format(r6, r7, r10);	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r7 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                r6 = r3.next();	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                if (r6 == 0) goto L_0x0d7d;	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
            L_0x0d78:
                r6 = r3.intValue(r11);	 Catch:{ Exception -> 0x0e6e, all -> 0x0e5e }
                goto L_0x0d7e;
            L_0x0d7d:
                r6 = 0;
            L_0x0d7e:
                r3.dispose();	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r3 = r3.database;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r8 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r10 = 1;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r12 = r5;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r12 = 0;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r11[r12] = r10;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r7 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r7 = r3.next();	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                if (r7 == 0) goto L_0x0db1;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
            L_0x0da7:
                r7 = r3.intValue(r12);	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r8 = 1;	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                r10 = r3.intValue(r8);	 Catch:{ Exception -> 0x0e53, all -> 0x0e46 }
                goto L_0x0db3;
            L_0x0db1:
                r7 = 0;
                r10 = 0;
            L_0x0db3:
                r3.dispose();	 Catch:{ Exception -> 0x0e38, all -> 0x0e28 }
                if (r7 == 0) goto L_0x0e23;
            L_0x0db8:
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r3 = r3.database;	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r8 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r11 = 2;	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e15, all -> 0x0e04 }
                r44 = r10;
                r10 = r5;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r11 = 0;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r12[r11] = r10;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r10 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r13 = 1;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r12[r13] = r10;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r6 = java.lang.String.format(r6, r8, r12);	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r8 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                r6 = r3.next();	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                if (r6 == 0) goto L_0x0dec;	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
            L_0x0de7:
                r6 = r3.intValue(r11);	 Catch:{ Exception -> 0x0e02, all -> 0x0e00 }
                goto L_0x0ded;
            L_0x0dec:
                r6 = 0;
            L_0x0ded:
                r3.dispose();	 Catch:{ Exception -> 0x0dfb, all -> 0x0df4 }
                r3 = r6;
                r6 = r7;
                goto L_0x0e80;
            L_0x0df4:
                r0 = move-exception;
                r21 = r0;
                r13 = r6;
                r11 = r7;
                goto L_0x1078;
            L_0x0dfb:
                r0 = move-exception;
                r13 = r6;
                r11 = r7;
                goto L_0x108a;
            L_0x0e00:
                r0 = move-exception;
                goto L_0x0e07;
            L_0x0e02:
                r0 = move-exception;
                goto L_0x0e18;
            L_0x0e04:
                r0 = move-exception;
                r44 = r10;
            L_0x0e07:
                r21 = r0;
                r6 = r2;
                r11 = r7;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
                r13 = 0;
                goto L_0x0e6a;
            L_0x0e15:
                r0 = move-exception;
                r44 = r10;
            L_0x0e18:
                r6 = r2;
                r11 = r7;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
                r13 = 0;
                goto L_0x0e78;
            L_0x0e23:
                r44 = r10;
                r3 = 0;
                goto L_0x0e80;
            L_0x0e28:
                r0 = move-exception;
                r44 = r10;
                r21 = r0;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
                r13 = 0;
                goto L_0x107f;
            L_0x0e38:
                r0 = move-exception;
                r44 = r10;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
                r13 = 0;
                goto L_0x1091;
            L_0x0e46:
                r0 = move-exception;
                r21 = r0;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r12 = 0;
                r13 = 0;
                r14 = 0;
                goto L_0x107f;
            L_0x0e53:
                r0 = move-exception;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r12 = 0;
                r13 = 0;
                r14 = 0;
                goto L_0x1091;
            L_0x0e5e:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r7 = r31;
                r15 = r40;
                r11 = 0;
                r12 = 0;
                r13 = 0;
                r14 = 0;
            L_0x0e6a:
                r17 = 1;
                goto L_0x0d23;
            L_0x0e6e:
                r0 = move-exception;
                r6 = r2;
                r7 = r31;
                r15 = r40;
                r11 = 0;
                r12 = 0;
                r13 = 0;
                r14 = 0;
            L_0x0e78:
                r17 = 1;
                goto L_0x0d34;
            L_0x0e7c:
                r3 = 0;
                r6 = 0;
                r44 = 0;
            L_0x0e80:
                r7 = r7;	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r8 = 3;
                if (r7 == r8) goto L_0x1099;
            L_0x0e85:
                r7 = r7;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = 4;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r7 != r8) goto L_0x0e8c;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0e8a:
                goto L_0x1099;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0e8c:
                r4 = r7;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r4 != r5) goto L_0x0ed4;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0e91:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d";	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = 3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = r5;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = r3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.lang.String.format(r5, r7, r10);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.queryFinalized(r5, r8);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0ec2:
                r13 = r3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = r6;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r3 = r25;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = r31;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r14 = r44;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r12 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0ecc:
                r17 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r19 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r6 = r2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0ed1:
                r2 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                goto L_0x1117;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0ed4:
                r4 = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r4 == 0) goto L_0x0f47;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0ed8:
                r4 = r3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r4 == 0) goto L_0x0f0e;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0edc:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d";	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = 3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = r5;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = r3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.lang.String.format(r5, r7, r10);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.queryFinalized(r5, r8);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                goto L_0x0ec2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0f0e:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d";	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = 4;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = r5;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r12 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r12] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r12 = 2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r12] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r12 = 3;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r12] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.lang.String.format(r5, r7, r10);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                goto L_0x0ec2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0f47:
                r4 = r7;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = 2;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r4 != r5) goto L_0x1005;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0f4c:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = 1;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = r5;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r11 = 0;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r10[r11] = r8;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = java.lang.String.format(r5, r7, r10);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r7 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r4 = r4.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                r5 = r4.next();	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                if (r5 == 0) goto L_0x0f77;	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
            L_0x0f72:
                r5 = r4.intValue(r11);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                goto L_0x0f78;
            L_0x0f77:
                r5 = 0;
            L_0x0f78:
                r4.dispose();	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = r4.database;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = 1;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12 = r5;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11[r12] = r10;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = r4.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = r4.next();	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                if (r7 == 0) goto L_0x0fca;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x0fa1:
                r7 = r4.intValue(r12);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = 1;
                r6 = r4.intValue(r8);	 Catch:{ Exception -> 0x0fbd, all -> 0x0fae }
                r44 = r6;
                r6 = r7;
                goto L_0x0fca;
            L_0x0fae:
                r0 = move-exception;
                r21 = r0;
                r6 = r2;
                r13 = r3;
                r12 = r5;
                r11 = r7;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                goto L_0x0e6a;
            L_0x0fbd:
                r0 = move-exception;
                r6 = r2;
                r13 = r3;
                r12 = r5;
                r11 = r7;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                goto L_0x0e78;
            L_0x0fca:
                r4.dispose();	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                if (r6 == 0) goto L_0x1006;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x0fcf:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = r4.database;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = 2;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12 = r5;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11[r12] = r10;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r13 = 1;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11[r13] = r10;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = r4.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = r4.next();	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                if (r7 == 0) goto L_0x1001;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x0ffc:
                r7 = r4.intValue(r12);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r3 = r7;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1001:
                r4.dispose();	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                goto L_0x1006;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1005:
                r5 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1006:
                if (r2 > r3) goto L_0x1010;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1008:
                if (r3 >= r14) goto L_0x100b;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x100a:
                goto L_0x1010;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x100b:
                r4 = r3 - r2;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r2 = r2 + 10;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                goto L_0x1020;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1010:
                r4 = r3 + 10;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = java.lang.Math.max(r2, r4);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                if (r3 >= r14) goto L_0x101e;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1018:
                r2 = r4;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r3 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r5 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r6 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                goto L_0x1020;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x101e:
                r2 = r4;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
            L_0x1020:
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r7 = r7.database;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d";	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = 3;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r13 = r5;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r13 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12[r13] = r11;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = 1;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12[r11] = r4;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r11 = 2;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r12[r11] = r4;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = java.lang.String.format(r8, r10, r12);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r8 = 0;	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r4 = r7.queryFinalized(r4, r10);	 Catch:{ Exception -> 0x1068, all -> 0x105b }
                r13 = r3;
                r12 = r5;
                r11 = r6;
            L_0x1052:
                r3 = r25;
                r7 = r31;
                r14 = r44;
                r5 = 0;
                goto L_0x0ecc;
            L_0x105b:
                r0 = move-exception;
                r21 = r0;
                r13 = r3;
                r12 = r5;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                goto L_0x107f;
            L_0x1068:
                r0 = move-exception;
                r13 = r3;
                r12 = r5;
                r11 = r6;
                r7 = r31;
                r15 = r40;
                r14 = r44;
                goto L_0x1091;
            L_0x1073:
                r0 = move-exception;
                r21 = r0;
                r13 = r3;
                r11 = r6;
            L_0x1078:
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
            L_0x107f:
                r17 = 1;
                r19 = 0;
                r20 = 0;
                goto L_0x17bf;
            L_0x1087:
                r0 = move-exception;
                r13 = r3;
                r11 = r6;
            L_0x108a:
                r7 = r31;
                r15 = r40;
                r14 = r44;
                r12 = 0;
            L_0x1091:
                r17 = 1;
                r19 = 0;
                r20 = 0;
                goto L_0x17d4;
            L_0x1099:
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r7 = r7.database;	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r10 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r11 = 1;
                r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x17a6, all -> 0x17a1 }
                r13 = r5;	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x17c2, all -> 0x17ab }
                r13 = 0;
                r12[r13] = r11;	 Catch:{ Exception -> 0x179b, all -> 0x1795 }
                r8 = java.lang.String.format(r8, r10, r12);	 Catch:{ Exception -> 0x179b, all -> 0x1795 }
                r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x179b, all -> 0x1795 }
                r7 = r7.queryFinalized(r8, r10);	 Catch:{ Exception -> 0x179b, all -> 0x1795 }
                r8 = r7.next();	 Catch:{ Exception -> 0x179b, all -> 0x1795 }
                if (r8 == 0) goto L_0x10c4;
            L_0x10bf:
                r8 = r7.intValue(r13);	 Catch:{ Exception -> 0x1087, all -> 0x1073 }
                goto L_0x10c5;
            L_0x10c4:
                r8 = 0;
            L_0x10c5:
                r7.dispose();	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r7 = r7.database;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r11 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)";	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r12 = 6;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = r5;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = 0;
                r13[r14] = r12;	 Catch:{ Exception -> 0x1773, all -> 0x176d }
                r12 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = 1;
                r13[r14] = r12;	 Catch:{ Exception -> 0x1768, all -> 0x1763 }
                r12 = r2 / 2;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = 2;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = r5;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r12 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r14 = 3;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13[r14] = r12;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r5 = 4;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13[r5] = r4;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r4 = r2 / 2;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r5 = 5;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13[r5] = r4;	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r4 = java.lang.String.format(r10, r11, r13);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r5 = 0;
                r10 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x1760, all -> 0x175d }
                r4 = r7.queryFinalized(r4, r10);	 Catch:{ Exception -> 0x1788, all -> 0x1779 }
                r13 = r3;
                r11 = r6;
                r12 = r8;
                goto L_0x1052;
            L_0x1117:
                if (r4 == 0) goto L_0x14d4;
            L_0x1119:
                r10 = r4.next();	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                if (r10 == 0) goto L_0x1469;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
            L_0x111f:
                r10 = 1;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r15 = r4.byteBufferValue(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                if (r15 == 0) goto L_0x1433;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
            L_0x1126:
                r10 = 0;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r8 = r15.readInt32(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r15, r8, r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = r10.currentAccount;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = org.telegram.messenger.UserConfig.getInstance(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = r10.clientUserId;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r8.readAttachPath(r15, r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r15.reuse();	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = 0;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r15 = r4.intValue(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                org.telegram.messenger.MessageObject.setUnreadFlags(r8, r15);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = 3;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r15 = r4.intValue(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r8.id = r15;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r10 = 4;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r15 = r4.intValue(r10);	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r8.date = r15;	 Catch:{ Exception -> 0x14be, all -> 0x14a7 }
                r45 = r14;
                r14 = r5;	 Catch:{ Exception -> 0x141f, all -> 0x140a }
                r8.dialog_id = r14;	 Catch:{ Exception -> 0x141f, all -> 0x140a }
                r10 = r8.flags;	 Catch:{ Exception -> 0x141f, all -> 0x140a }
                r10 = r10 & 1024;
                if (r10 == 0) goto L_0x1180;
            L_0x1163:
                r10 = 7;
                r14 = r4.intValue(r10);	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r8.views = r14;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                goto L_0x1181;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x116b:
                r0 = move-exception;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r21 = r0;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r20 = r5;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r15 = r40;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r14 = r45;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                goto L_0x1863;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x1176:
                r0 = move-exception;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r2 = r0;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r20 = r5;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r15 = r40;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r14 = r45;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                goto L_0x1824;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x1180:
                r10 = 7;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x1181:
                if (r9 == 0) goto L_0x1190;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x1183:
                r14 = r8.ttl;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                if (r14 != 0) goto L_0x1190;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x1187:
                r14 = 8;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r15 = r4.intValue(r14);	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                r8.ttl = r15;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
                goto L_0x1192;
            L_0x1190:
                r14 = 8;
            L_0x1192:
                r15 = 9;
                r15 = r4.intValue(r15);	 Catch:{ Exception -> 0x141f, all -> 0x140a }
                if (r15 == 0) goto L_0x119d;
            L_0x119a:
                r15 = 1;
                r8.mentioned = r15;	 Catch:{ Exception -> 0x1176, all -> 0x116b }
            L_0x119d:
                r15 = r40;
                r10 = r15.messages;	 Catch:{ Exception -> 0x13fc, all -> 0x13ee }
                r10.add(r8);	 Catch:{ Exception -> 0x13fc, all -> 0x13ee }
                r14 = r27;	 Catch:{ Exception -> 0x13fc, all -> 0x13ee }
                r10 = r32;	 Catch:{ Exception -> 0x13fc, all -> 0x13ee }
                org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r10, r14);	 Catch:{ Exception -> 0x13fc, all -> 0x13ee }
                r46 = r13;
                r13 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x13dc, all -> 0x13c9 }
                if (r13 != 0) goto L_0x11e6;
            L_0x11b1:
                r47 = r12;
                r12 = r8.reply_to_random_id;	 Catch:{ Exception -> 0x11da, all -> 0x11cd }
                r22 = 0;
                r16 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1));
                if (r16 == 0) goto L_0x11bc;
            L_0x11bb:
                goto L_0x11e8;
            L_0x11bc:
                r49 = r6;
                r50 = r7;
                r48 = r11;
            L_0x11c2:
                r51 = r14;
                r7 = r24;
                r11 = r26;
            L_0x11c8:
                r6 = r28;
            L_0x11ca:
                r12 = 2;
                goto L_0x12fd;
            L_0x11cd:
                r0 = move-exception;
            L_0x11ce:
                r21 = r0;
                r20 = r5;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                goto L_0x1863;
            L_0x11da:
                r0 = move-exception;
            L_0x11db:
                r2 = r0;
                r20 = r5;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                goto L_0x1824;
            L_0x11e6:
                r47 = r12;
            L_0x11e8:
                r12 = 6;
                r13 = r4.isNull(r12);	 Catch:{ Exception -> 0x13b7, all -> 0x13a4 }
                if (r13 != 0) goto L_0x1255;
            L_0x11ef:
                r13 = r4.byteBufferValue(r12);	 Catch:{ Exception -> 0x1251, all -> 0x124c }
                if (r13 == 0) goto L_0x1255;
            L_0x11f5:
                r48 = r11;
                r12 = 0;
                r11 = r13.readInt32(r12);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r11 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r13, r11, r12);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r8.replyMessage = r11;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r11 = r8.replyMessage;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = r12.currentAccount;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = org.telegram.messenger.UserConfig.getInstance(r12);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = r12.clientUserId;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r11.readAttachPath(r13, r12);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r13.reuse();	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r11 = r8.replyMessage;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                if (r11 == 0) goto L_0x1257;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
            L_0x121a:
                r11 = org.telegram.messenger.MessageObject.isMegagroup(r8);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                if (r11 == 0) goto L_0x1229;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
            L_0x1220:
                r11 = r8.replyMessage;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = r11.flags;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r13 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r12 = r12 | r13;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                r11.flags = r12;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
            L_0x1229:
                r11 = r8.replyMessage;	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r11, r10, r14);	 Catch:{ Exception -> 0x123e, all -> 0x122f }
                goto L_0x1257;
            L_0x122f:
                r0 = move-exception;
            L_0x1230:
                r21 = r0;
                r20 = r5;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                goto L_0x1863;
            L_0x123e:
                r0 = move-exception;
            L_0x123f:
                r2 = r0;
                r20 = r5;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                goto L_0x1824;
            L_0x124c:
                r0 = move-exception;
                r48 = r11;
                goto L_0x11ce;
            L_0x1251:
                r0 = move-exception;
                r48 = r11;
                goto L_0x11db;
            L_0x1255:
                r48 = r11;
            L_0x1257:
                r11 = r8.replyMessage;	 Catch:{ Exception -> 0x1392, all -> 0x137f }
                if (r11 != 0) goto L_0x12f7;
            L_0x125b:
                r11 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                if (r11 == 0) goto L_0x12af;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
            L_0x125f:
                r11 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                r11 = (long) r11;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                r13 = r8.to_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                r13 = r13.channel_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                if (r13 == 0) goto L_0x1279;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
            L_0x1268:
                r13 = r8.to_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                r13 = r13.channel_id;	 Catch:{ Exception -> 0x12f0, all -> 0x12e9 }
                r49 = r6;
                r50 = r7;
                r6 = (long) r13;
                r13 = 32;
                r6 = r6 << r13;
                r20 = r11 | r6;
                r11 = r20;
                goto L_0x127f;
            L_0x1279:
                r49 = r6;
                r50 = r7;
                r13 = 32;
            L_0x127f:
                r6 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r7 = r24;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = r7.contains(r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r6 != 0) goto L_0x1292;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x128b:
                r6 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r7.add(r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x1292:
                r6 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r11 = r26;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = r11.get(r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = (java.util.ArrayList) r6;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r6 != 0) goto L_0x12a8;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x129e:
                r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6.<init>();	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r12 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r11.put(r12, r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12a8:
                r6.add(r8);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r51 = r14;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                goto L_0x11c8;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12af:
                r49 = r6;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r50 = r7;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r51 = r14;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r7 = r24;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r11 = r26;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r13 = r8.reply_to_random_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = r7.contains(r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r6 != 0) goto L_0x12ce;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12c5:
                r12 = r8.reply_to_random_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r7.add(r6);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12ce:
                r12 = r8.reply_to_random_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6 = r28;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r12 = r6.get(r12);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r12 = (java.util.ArrayList) r12;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r12 != 0) goto L_0x12e4;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12da:
                r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r12.<init>();	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r13 = r8.reply_to_random_id;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r6.put(r13, r12);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x12e4:
                r12.add(r8);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                goto L_0x11ca;
            L_0x12e9:
                r0 = move-exception;
                r49 = r6;
                r50 = r7;
                goto L_0x1230;
            L_0x12f0:
                r0 = move-exception;
                r49 = r6;
                r50 = r7;
                goto L_0x123f;
            L_0x12f7:
                r49 = r6;
                r50 = r7;
                goto L_0x11c2;
            L_0x12fd:
                r13 = r4.intValue(r12);	 Catch:{ Exception -> 0x137a, all -> 0x1375 }
                r8.send_state = r13;	 Catch:{ Exception -> 0x137a, all -> 0x1375 }
                r12 = r8.id;	 Catch:{ Exception -> 0x137a, all -> 0x1375 }
                if (r12 <= 0) goto L_0x130e;
            L_0x1307:
                r12 = r8.send_state;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r12 == 0) goto L_0x130e;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x130b:
                r12 = 0;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r8.send_state = r12;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x130e:
                if (r9 != 0) goto L_0x131e;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x1310:
                r12 = 5;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r13 = r4.isNull(r12);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                if (r13 != 0) goto L_0x131f;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
            L_0x1317:
                r13 = r4.longValue(r12);	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                r8.random_id = r13;	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                goto L_0x131f;
            L_0x131e:
                r12 = 5;
            L_0x131f:
                r13 = org.telegram.messenger.MessageObject.isSecretPhotoOrVideo(r8);	 Catch:{ Exception -> 0x137a, all -> 0x1375 }
                if (r13 == 0) goto L_0x136d;
            L_0x1325:
                r13 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1360, all -> 0x1375 }
                r13 = r13.database;	 Catch:{ Exception -> 0x1360, all -> 0x1375 }
                r14 = java.util.Locale.US;	 Catch:{ Exception -> 0x1360, all -> 0x1375 }
                r12 = "SELECT date FROM enc_tasks_v2 WHERE mid = %d";	 Catch:{ Exception -> 0x1360, all -> 0x1375 }
                r52 = r5;
                r53 = r6;
                r5 = 1;
                r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x135e, all -> 0x1488 }
                r5 = r8.id;	 Catch:{ Exception -> 0x135e, all -> 0x1488 }
                r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x135e, all -> 0x1488 }
                r54 = r11;
                r11 = 0;
                r6[r11] = r5;	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                r5 = java.lang.String.format(r14, r12, r6);	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                r5 = r13.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                r6 = r5.next();	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                if (r6 == 0) goto L_0x1357;	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
            L_0x1351:
                r6 = r5.intValue(r11);	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                r8.destroyTime = r6;	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
            L_0x1357:
                r5.dispose();	 Catch:{ Exception -> 0x135c, all -> 0x1488 }
                goto L_0x144d;
            L_0x135c:
                r0 = move-exception;
                goto L_0x1367;
            L_0x135e:
                r0 = move-exception;
                goto L_0x1365;
            L_0x1360:
                r0 = move-exception;
                r52 = r5;
                r53 = r6;
            L_0x1365:
                r54 = r11;
            L_0x1367:
                r5 = r0;
                org.telegram.messenger.FileLog.m3e(r5);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x144d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x136d:
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r53 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r54 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x144d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1375:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1489;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x137a:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1499;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x137f:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r21 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r47;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r48;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14ba;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1392:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r47;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r48;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14d0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13a4:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r21 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r47;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14ba;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13b7:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r47;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14d0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13c9:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r21 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14ba;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13dc:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14d0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13ee:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1419;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x13fc:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x142e;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x140a:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r15 = r40;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1419:
                r21 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14ba;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x141f:
                r0 = move-exception;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r15 = r40;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x142e:
                r2 = r0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14d0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1433:
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r45 = r14;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r24;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r54 = r26;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r51 = r27;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r53 = r28;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r10 = r32;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r15 = r40;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x144d:
                r24 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r32 = r10;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r40 = r15;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = r45;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r46;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r47;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r48;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = r49;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r50;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r27 = r51;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = r52;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r28 = r53;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r26 = r54;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1119;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1469:
                r52 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r49 = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r50 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r48 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r47 = r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r46 = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r45 = r14;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r24;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r54 = r26;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r51 = r27;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r53 = r28;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r10 = r32;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r15 = r40;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4.dispose();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x14ee;
            L_0x1488:
                r0 = move-exception;
            L_0x1489:
                r21 = r0;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                r6 = r49;
                r7 = r50;
                goto L_0x14ba;
            L_0x1498:
                r0 = move-exception;
            L_0x1499:
                r2 = r0;
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                r6 = r49;
                r7 = r50;
                goto L_0x14d0;
            L_0x14a7:
                r0 = move-exception;
                r52 = r5;
                r49 = r6;
                r50 = r7;
                r48 = r11;
                r47 = r12;
                r46 = r13;
                r45 = r14;
                r15 = r40;
                r21 = r0;
            L_0x14ba:
                r20 = r52;
                goto L_0x1863;
            L_0x14be:
                r0 = move-exception;
                r52 = r5;
                r49 = r6;
                r50 = r7;
                r48 = r11;
                r47 = r12;
                r46 = r13;
                r45 = r14;
                r15 = r40;
                r2 = r0;
            L_0x14d0:
                r20 = r52;
                goto L_0x1824;
            L_0x14d4:
                r52 = r5;
                r49 = r6;
                r50 = r7;
                r48 = r11;
                r47 = r12;
                r46 = r13;
                r45 = r14;
                r7 = r24;
                r54 = r26;
                r51 = r27;
                r53 = r28;
                r10 = r32;
                r15 = r40;
            L_0x14ee:
                r4 = r15.messages;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r5 = new org.telegram.messenger.MessagesStorage$58$1;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r5.<init>();	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                java.util.Collections.sort(r4, r5);	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                if (r9 == 0) goto L_0x1562;
            L_0x14fa:
                r4 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 3;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r4 == r5) goto L_0x150d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x14ff:
                r4 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 4;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r4 == r5) goto L_0x150d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1504:
                r4 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 2;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r4 != r5) goto L_0x1547;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1509:
                if (r19 == 0) goto L_0x1547;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x150b:
                if (r2 != 0) goto L_0x1547;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x150d:
                r2 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r2.isEmpty();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r2 != 0) goto L_0x1547;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1515:
                r2 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4 - r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r2.get(r4);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = (org.telegram.tgnet.TLRPC.Message) r2;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r2.id;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.get(r5);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = (org.telegram.tgnet.TLRPC.Message) r4;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.id;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r2 > r3) goto L_0x1536;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1534:
                if (r4 >= r3) goto L_0x1547;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1536:
                r7.clear();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r10.clear();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2 = r51;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r2.clear();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r3 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r3.clear();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1549;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1547:
                r2 = r51;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1549:
                r3 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = 4;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r3 == r4) goto L_0x1553;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x154e:
                r3 = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = 3;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r3 != r4) goto L_0x1564;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1553:
                r3 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r3 = r3.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r3 != r4) goto L_0x1564;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x155c:
                r3 = r15.messages;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r3.clear();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1564;
            L_0x1562:
                r2 = r51;
            L_0x1564:
                r3 = r7.isEmpty();	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                if (r3 != 0) goto L_0x1698;
            L_0x156a:
                r3 = r54;
                r4 = r3.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r4 <= 0) goto L_0x1594;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1572:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = "SELECT data, mid, date FROM messages WHERE mid IN(%s)";	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = ",";	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = android.text.TextUtils.join(r8, r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9[r8] = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = java.lang.String.format(r5, r6, r9);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1592:
                r8 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x15b4;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1594:
                r4 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.database;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)";	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = ",";	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = android.text.TextUtils.join(r8, r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9[r8] = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = java.lang.String.format(r5, r6, r9);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x15b4:
                r5 = r4.next();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r5 == 0) goto L_0x1667;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x15ba:
                r5 = r4.byteBufferValue(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r5 == 0) goto L_0x165d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x15c0:
                r6 = r5.readInt32(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r6, r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r7.currentAccount;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = org.telegram.messenger.UserConfig.getInstance(r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r7.clientUserId;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6.readAttachPath(r5, r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5.reuse();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r4.intValue(r5);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6.id = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 2;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r4.intValue(r5);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6.date = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r5;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6.dialog_id = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r6, r10, r2);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r3.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r7 <= 0) goto L_0x1623;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x15f5:
                r7 = r6.id;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = r3.get(r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = (java.util.ArrayList) r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r7 == 0) goto L_0x1620;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x15ff:
                r8 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1600:
                r9 = r7.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r8 >= r9) goto L_0x1620;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1606:
                r9 = r7.get(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9 = (org.telegram.tgnet.TLRPC.Message) r9;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9.replyMessage = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = org.telegram.messenger.MessageObject.isMegagroup(r9);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r11 == 0) goto L_0x161d;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1614:
                r9 = r9.replyMessage;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r9.flags;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r11 | r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9.flags = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x161d:
                r8 = r8 + 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1600;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1620:
                r11 = r53;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1660;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1623:
                r7 = 3;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r8 = r4.longValue(r7);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11 = r53;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = r11.get(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r12 = (java.util.ArrayList) r12;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r11.remove(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r12 == 0) goto L_0x1661;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1635:
                r8 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1636:
                r9 = r12.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r8 >= r9) goto L_0x1661;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x163c:
                r9 = r12.get(r8);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9 = (org.telegram.tgnet.TLRPC.Message) r9;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9.replyMessage = r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r6.id;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9.reply_to_msg_id = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = org.telegram.messenger.MessageObject.isMegagroup(r9);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r13 == 0) goto L_0x1658;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x164e:
                r9 = r9.replyMessage;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r9.flags;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r14 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r13 = r13 | r14;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r9.flags = r13;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x165a;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1658:
                r14 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x165a:
                r8 = r8 + 1;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1636;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x165d:
                r11 = r53;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 2;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1660:
                r7 = 3;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1661:
                r14 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r53 = r11;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                goto L_0x1592;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1667:
                r11 = r53;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4.dispose();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r3 = r11.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r3 <= 0) goto L_0x1698;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1672:
                r3 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1673:
                r4 = r11.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r3 >= r4) goto L_0x1698;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1679:
                r4 = r11.valueAt(r3);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r4 = (java.util.ArrayList) r4;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1680:
                r6 = r4.size();	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                if (r5 >= r6) goto L_0x1693;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
            L_0x1686:
                r6 = r4.get(r5);	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r7 = 0;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r6.reply_to_random_id = r7;	 Catch:{ Exception -> 0x1498, all -> 0x1488 }
                r5 = r5 + 1;
                goto L_0x1680;
            L_0x1693:
                r7 = 0;
                r3 = r3 + 1;
                goto L_0x1673;
            L_0x1698:
                if (r52 == 0) goto L_0x16d8;
            L_0x169a:
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r3 = r3.database;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r5 = "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)";	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r7 = 1;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r6 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r7 = r5;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r9 = 0;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r6[r9] = r7;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r4 = java.lang.String.format(r4, r5, r6);	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r5 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r4 = r3.next();	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                if (r4 == 0) goto L_0x16cb;	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
            L_0x16c0:
                r4 = r3.intValue(r9);	 Catch:{ Exception -> 0x1749, all -> 0x1734 }
                r5 = r52;
                if (r5 == r4) goto L_0x16cf;
            L_0x16c8:
                r5 = r5 * -1;
                goto L_0x16cf;
            L_0x16cb:
                r5 = r52;
                r5 = r5 * -1;
            L_0x16cf:
                r3.dispose();	 Catch:{ Exception -> 0x16d5, all -> 0x16d3 }
                goto L_0x16da;
            L_0x16d3:
                r0 = move-exception;
                goto L_0x1737;
            L_0x16d5:
                r0 = move-exception;
                goto L_0x174c;
            L_0x16d8:
                r5 = r52;
            L_0x16da:
                r20 = r5;
                r3 = r10.isEmpty();	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                if (r3 != 0) goto L_0x16ef;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
            L_0x16e2:
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r4 = ",";	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r4 = android.text.TextUtils.join(r4, r10);	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r5 = r15.users;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r3.getUsersInternal(r4, r5);	 Catch:{ Exception -> 0x1731, all -> 0x172d }
            L_0x16ef:
                r3 = r2.isEmpty();	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                if (r3 != 0) goto L_0x1702;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
            L_0x16f5:
                r3 = org.telegram.messenger.MessagesStorage.this;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r4 = ",";	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r2 = android.text.TextUtils.join(r4, r2);	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r4 = r15.chats;	 Catch:{ Exception -> 0x1731, all -> 0x172d }
                r3.getChatsInternal(r2, r4);	 Catch:{ Exception -> 0x1731, all -> 0x172d }
            L_0x1702:
                r2 = org.telegram.messenger.MessagesStorage.this;
                r2 = r2.currentAccount;
                r2 = org.telegram.messenger.MessagesController.getInstance(r2);
                r4 = r5;
                r8 = r9;
                r9 = 1;
                r10 = r10;
                r14 = r7;
                r13 = r4;
                r12 = r11;
                r3 = r15;
                r6 = r49;
                r7 = r50;
                r11 = r48;
                r18 = r12;
                r12 = r47;
                r16 = r13;
                r13 = r46;
                r15 = r14;
                r14 = r45;
                goto L_0x185c;
            L_0x172d:
                r0 = move-exception;
                r21 = r0;
                goto L_0x173b;
            L_0x1731:
                r0 = move-exception;
                r2 = r0;
                goto L_0x174f;
            L_0x1734:
                r0 = move-exception;
                r5 = r52;
            L_0x1737:
                r21 = r0;
                r20 = r5;
            L_0x173b:
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                r6 = r49;
                r7 = r50;
                goto L_0x1863;
            L_0x1749:
                r0 = move-exception;
                r5 = r52;
            L_0x174c:
                r2 = r0;
                r20 = r5;
            L_0x174f:
                r14 = r45;
                r13 = r46;
                r12 = r47;
                r11 = r48;
                r6 = r49;
                r7 = r50;
                goto L_0x1824;
            L_0x175d:
                r0 = move-exception;
                r9 = r5;
                goto L_0x176f;
            L_0x1760:
                r0 = move-exception;
                r9 = r5;
                goto L_0x1775;
            L_0x1763:
                r0 = move-exception;
                r7 = r14;
                r15 = r40;
                goto L_0x177d;
            L_0x1768:
                r0 = move-exception;
                r7 = r14;
                r15 = r40;
                goto L_0x178c;
            L_0x176d:
                r0 = move-exception;
                r9 = r14;
            L_0x176f:
                r15 = r40;
                r7 = 1;
                goto L_0x177e;
            L_0x1773:
                r0 = move-exception;
                r9 = r14;
            L_0x1775:
                r15 = r40;
                r7 = 1;
                goto L_0x178d;
            L_0x1779:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
            L_0x177d:
                r9 = 0;
            L_0x177e:
                r21 = r0;
                r13 = r3;
                r11 = r6;
                r17 = r7;
                r12 = r8;
                r19 = r9;
                goto L_0x17b9;
            L_0x1788:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
            L_0x178c:
                r9 = 0;
            L_0x178d:
                r13 = r3;
                r11 = r6;
                r17 = r7;
                r12 = r8;
                r19 = r9;
                goto L_0x17ce;
            L_0x1795:
                r0 = move-exception;
                r9 = r13;
                r15 = r40;
                r7 = 1;
                goto L_0x17b0;
            L_0x179b:
                r0 = move-exception;
                r9 = r13;
                r15 = r40;
                r7 = 1;
                goto L_0x17c7;
            L_0x17a1:
                r0 = move-exception;
                r7 = r11;
                r15 = r40;
                goto L_0x17af;
            L_0x17a6:
                r0 = move-exception;
                r7 = r11;
                r15 = r40;
                goto L_0x17c6;
            L_0x17ab:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
            L_0x17af:
                r9 = 0;
            L_0x17b0:
                r21 = r0;
                r13 = r3;
                r11 = r6;
                r17 = r7;
                r12 = r9;
                r19 = r12;
            L_0x17b9:
                r20 = r19;
                r7 = r31;
                r14 = r44;
            L_0x17bf:
                r6 = r2;
                goto L_0x1863;
            L_0x17c2:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
            L_0x17c6:
                r9 = 0;
            L_0x17c7:
                r13 = r3;
                r11 = r6;
                r17 = r7;
                r12 = r9;
                r19 = r12;
            L_0x17ce:
                r20 = r19;
                r7 = r31;
                r14 = r44;
            L_0x17d4:
                r6 = r2;
                goto L_0x1823;
            L_0x17d7:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
                r9 = 0;
                r21 = r0;
                r6 = r2;
                r17 = r7;
                r11 = r9;
                r12 = r11;
                r13 = r12;
                r14 = r13;
                r19 = r14;
                r20 = r19;
                r7 = r31;
                goto L_0x1863;
            L_0x17ed:
                r0 = move-exception;
                r15 = r40;
                r7 = 1;
                r9 = 0;
                r6 = r2;
                r17 = r7;
                r11 = r9;
                r12 = r11;
                r13 = r12;
                r14 = r13;
                r19 = r14;
                r20 = r19;
                r7 = r31;
                goto L_0x1823;
            L_0x1800:
                r0 = move-exception;
                r15 = r3;
                r31 = r7;
                r9 = 0;
                r21 = r0;
                r6 = r2;
                r11 = r9;
                r12 = r11;
                r13 = r12;
                r14 = r13;
                r17 = r14;
                r19 = r17;
                r20 = r19;
                goto L_0x1863;
            L_0x1813:
                r0 = move-exception;
                r15 = r3;
                r31 = r7;
                r9 = 0;
                r6 = r2;
                r11 = r9;
                r12 = r11;
                r13 = r12;
                r14 = r13;
                r17 = r14;
                r19 = r17;
                r20 = r19;
            L_0x1823:
                r2 = r0;
            L_0x1824:
                r3 = r15.messages;	 Catch:{ all -> 0x1860 }
                r3.clear();	 Catch:{ all -> 0x1860 }
                r3 = r15.chats;	 Catch:{ all -> 0x1860 }
                r3.clear();	 Catch:{ all -> 0x1860 }
                r3 = r15.users;	 Catch:{ all -> 0x1860 }
                r3.clear();	 Catch:{ all -> 0x1860 }
                org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x1860 }
                r2 = org.telegram.messenger.MessagesStorage.this;
                r2 = r2.currentAccount;
                r2 = org.telegram.messenger.MessagesController.getInstance(r2);
                r4 = r5;
                r8 = r9;
                r10 = r10;
                r3 = r7;
                r9 = r4;
                r55 = r9;
                r9 = r11;
                r16 = r3;
                r3 = r15;
                r21 = r9;
                r18 = r55;
                r9 = 1;
                r15 = r16;
                r16 = r18;
                r18 = r21;
            L_0x185c:
                r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
                return;
            L_0x1860:
                r0 = move-exception;
                r21 = r0;
            L_0x1863:
                r2 = org.telegram.messenger.MessagesStorage.this;
                r2 = r2.currentAccount;
                r2 = org.telegram.messenger.MessagesController.getInstance(r2);
                r4 = r5;
                r8 = r9;
                r10 = r10;
                r3 = r7;
                r9 = r4;
                r56 = r9;
                r9 = r11;
                r16 = r3;
                r3 = r15;
                r22 = r9;
                r18 = r56;
                r9 = 1;
                r15 = r16;
                r16 = r18;
                r18 = r22;
                r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
                throw r21;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.58.run():void");
            }
        });
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public TLObject getSentFile(String str, int i) {
        TLObject tLObject = null;
        if (str != null) {
            if (!str.toLowerCase().endsWith("attheme")) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                ArrayList arrayList = new ArrayList();
                final String str2 = str;
                final int i2 = i;
                final ArrayList arrayList2 = arrayList;
                final CountDownLatch countDownLatch2 = countDownLatch;
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            if (Utilities.MD5(str2) != null) {
                                SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM sent_files_v2 WHERE uid = '%s' AND type = %d", new Object[]{r0, Integer.valueOf(i2)}), new Object[0]);
                                if (queryFinalized.next()) {
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize instanceof TL_messageMediaDocument) {
                                            arrayList2.add(((TL_messageMediaDocument) TLdeserialize).document);
                                        } else if (TLdeserialize instanceof TL_messageMediaPhoto) {
                                            arrayList2.add(((TL_messageMediaPhoto) TLdeserialize).photo);
                                        }
                                    }
                                }
                                queryFinalized.dispose();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        } catch (Throwable th) {
                            countDownLatch2.countDown();
                        }
                        countDownLatch2.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (arrayList.isEmpty() == null) {
                    tLObject = (TLObject) arrayList.get(null);
                }
                return tLObject;
            }
        }
        return null;
    }

    public void putSentFile(final String str, final TLObject tLObject, final int i) {
        if (str != null) {
            if (tLObject != null) {
                this.storageQueue.postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        Throwable e;
                        SQLitePreparedStatement sQLitePreparedStatement = null;
                        try {
                            String MD5 = Utilities.MD5(str);
                            if (MD5 != null) {
                                MessageMedia tL_messageMediaPhoto;
                                if (tLObject instanceof Photo) {
                                    tL_messageMediaPhoto = new TL_messageMediaPhoto();
                                    tL_messageMediaPhoto.photo = (Photo) tLObject;
                                    tL_messageMediaPhoto.flags |= 1;
                                } else if (tLObject instanceof Document) {
                                    tL_messageMediaPhoto = new TL_messageMediaDocument();
                                    tL_messageMediaPhoto.document = (Document) tLObject;
                                    tL_messageMediaPhoto.flags |= 1;
                                } else {
                                    tL_messageMediaPhoto = null;
                                }
                                if (tL_messageMediaPhoto != null) {
                                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?)");
                                    try {
                                        executeFast.requery();
                                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_messageMediaPhoto.getObjectSize());
                                        tL_messageMediaPhoto.serializeToStream(nativeByteBuffer);
                                        executeFast.bindString(1, MD5);
                                        executeFast.bindInteger(2, i);
                                        executeFast.bindByteBuffer(3, nativeByteBuffer);
                                        executeFast.step();
                                        nativeByteBuffer.reuse();
                                        sQLitePreparedStatement = executeFast;
                                    } catch (Exception e2) {
                                        e = e2;
                                        sQLitePreparedStatement = executeFast;
                                        try {
                                            FileLog.m3e(e);
                                        } catch (Throwable th) {
                                            e = th;
                                            if (sQLitePreparedStatement != null) {
                                                sQLitePreparedStatement.dispose();
                                            }
                                            throw e;
                                        }
                                    } catch (Throwable th2) {
                                        e = th2;
                                        sQLitePreparedStatement = executeFast;
                                        if (sQLitePreparedStatement != null) {
                                            sQLitePreparedStatement.dispose();
                                        }
                                        throw e;
                                    }
                                }
                                return;
                            }
                            if (sQLitePreparedStatement != null) {
                                sQLitePreparedStatement.dispose();
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }
    }

    public void updateEncryptedChatSeq(final EncryptedChat encryptedChat, final boolean z) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    Throwable e;
                    Throwable th;
                    SQLitePreparedStatement executeFast;
                    try {
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                        try {
                            executeFast.bindInteger(1, encryptedChat.seq_in);
                            executeFast.bindInteger(2, encryptedChat.seq_out);
                            executeFast.bindInteger(3, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
                            executeFast.bindInteger(4, encryptedChat.in_seq_no);
                            executeFast.bindInteger(5, encryptedChat.mtproto_seq);
                            executeFast.bindInteger(6, encryptedChat.id);
                            executeFast.step();
                            if (z) {
                                long j = ((long) encryptedChat.id) << 32;
                                MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN (SELECT m.mid FROM messages as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d)", new Object[]{Long.valueOf(j), Integer.valueOf(encryptedChat.in_seq_no)})).stepThis().dispose();
                            }
                            if (executeFast != null) {
                                executeFast.dispose();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            try {
                                FileLog.m3e(e);
                                if (executeFast == null) {
                                    return;
                                }
                                executeFast.dispose();
                            } catch (Throwable th2) {
                                e = th2;
                                if (executeFast != null) {
                                    executeFast.dispose();
                                }
                                throw e;
                            }
                        }
                    } catch (Throwable e3) {
                        th = e3;
                        executeFast = null;
                        e = th;
                        FileLog.m3e(e);
                        if (executeFast == null) {
                            return;
                        }
                        executeFast.dispose();
                    } catch (Throwable e32) {
                        th = e32;
                        executeFast = null;
                        e = th;
                        if (executeFast != null) {
                            executeFast.dispose();
                        }
                        throw e;
                    }
                }
            });
        }
    }

    public void updateEncryptedChatTTL(final EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    Throwable e;
                    Throwable th;
                    SQLitePreparedStatement executeFast;
                    try {
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                        try {
                            executeFast.bindInteger(1, encryptedChat.ttl);
                            executeFast.bindInteger(2, encryptedChat.id);
                            executeFast.step();
                            if (executeFast != null) {
                                executeFast.dispose();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            try {
                                FileLog.m3e(e);
                                if (executeFast == null) {
                                    return;
                                }
                                executeFast.dispose();
                            } catch (Throwable th2) {
                                e = th2;
                                if (executeFast != null) {
                                    executeFast.dispose();
                                }
                                throw e;
                            }
                        }
                    } catch (Throwable e3) {
                        th = e3;
                        executeFast = null;
                        e = th;
                        FileLog.m3e(e);
                        if (executeFast == null) {
                            return;
                        }
                        executeFast.dispose();
                    } catch (Throwable e32) {
                        th = e32;
                        executeFast = null;
                        e = th;
                        if (executeFast != null) {
                            executeFast.dispose();
                        }
                        throw e;
                    }
                }
            });
        }
    }

    public void updateEncryptedChatLayer(final EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    Throwable e;
                    Throwable th;
                    SQLitePreparedStatement executeFast;
                    try {
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                        try {
                            executeFast.bindInteger(1, encryptedChat.layer);
                            executeFast.bindInteger(2, encryptedChat.id);
                            executeFast.step();
                            if (executeFast != null) {
                                executeFast.dispose();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            try {
                                FileLog.m3e(e);
                                if (executeFast == null) {
                                    return;
                                }
                                executeFast.dispose();
                            } catch (Throwable th2) {
                                e = th2;
                                if (executeFast != null) {
                                    executeFast.dispose();
                                }
                                throw e;
                            }
                        }
                    } catch (Throwable e3) {
                        th = e3;
                        executeFast = null;
                        e = th;
                        FileLog.m3e(e);
                        if (executeFast == null) {
                            return;
                        }
                        executeFast.dispose();
                    } catch (Throwable e32) {
                        th = e32;
                        executeFast = null;
                        e = th;
                        if (executeFast != null) {
                            executeFast.dispose();
                        }
                        throw e;
                    }
                }
            });
        }
    }

    public void updateEncryptedChat(final EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    Throwable e;
                    Throwable th;
                    SQLitePreparedStatement executeFast;
                    try {
                        if ((encryptedChat.key_hash == null || encryptedChat.key_hash.length < 16) && encryptedChat.auth_key != null) {
                            encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
                        }
                        executeFast = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
                        try {
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
                            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat.a_or_b != null ? encryptedChat.a_or_b.length : 1);
                            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat.auth_key != null ? encryptedChat.auth_key.length : 1);
                            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat.future_auth_key != null ? encryptedChat.future_auth_key.length : 1);
                            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat.key_hash != null ? encryptedChat.key_hash.length : 1);
                            encryptedChat.serializeToStream(nativeByteBuffer);
                            executeFast.bindByteBuffer(1, nativeByteBuffer);
                            if (encryptedChat.a_or_b != null) {
                                nativeByteBuffer2.writeBytes(encryptedChat.a_or_b);
                            }
                            if (encryptedChat.auth_key != null) {
                                nativeByteBuffer3.writeBytes(encryptedChat.auth_key);
                            }
                            if (encryptedChat.future_auth_key != null) {
                                nativeByteBuffer4.writeBytes(encryptedChat.future_auth_key);
                            }
                            if (encryptedChat.key_hash != null) {
                                nativeByteBuffer5.writeBytes(encryptedChat.key_hash);
                            }
                            executeFast.bindByteBuffer(2, nativeByteBuffer2);
                            executeFast.bindByteBuffer(3, nativeByteBuffer3);
                            executeFast.bindInteger(4, encryptedChat.ttl);
                            executeFast.bindInteger(5, encryptedChat.layer);
                            executeFast.bindInteger(6, encryptedChat.seq_in);
                            executeFast.bindInteger(7, encryptedChat.seq_out);
                            executeFast.bindInteger(8, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
                            executeFast.bindLong(9, encryptedChat.exchange_id);
                            executeFast.bindInteger(10, encryptedChat.key_create_date);
                            executeFast.bindLong(11, encryptedChat.future_key_fingerprint);
                            executeFast.bindByteBuffer(12, nativeByteBuffer4);
                            executeFast.bindByteBuffer(13, nativeByteBuffer5);
                            executeFast.bindInteger(14, encryptedChat.in_seq_no);
                            executeFast.bindInteger(15, encryptedChat.admin_id);
                            executeFast.bindInteger(16, encryptedChat.mtproto_seq);
                            executeFast.bindInteger(17, encryptedChat.id);
                            executeFast.step();
                            nativeByteBuffer.reuse();
                            nativeByteBuffer2.reuse();
                            nativeByteBuffer3.reuse();
                            nativeByteBuffer4.reuse();
                            nativeByteBuffer5.reuse();
                            if (executeFast != null) {
                                executeFast.dispose();
                            }
                        } catch (Exception e2) {
                            e = e2;
                            try {
                                FileLog.m3e(e);
                                if (executeFast == null) {
                                    return;
                                }
                                executeFast.dispose();
                            } catch (Throwable th2) {
                                e = th2;
                                if (executeFast != null) {
                                    executeFast.dispose();
                                }
                                throw e;
                            }
                        }
                    } catch (Throwable e3) {
                        th = e3;
                        executeFast = null;
                        e = th;
                        FileLog.m3e(e);
                        if (executeFast == null) {
                            return;
                        }
                        executeFast.dispose();
                    } catch (Throwable e32) {
                        th = e32;
                        executeFast = null;
                        e = th;
                        if (executeFast != null) {
                            executeFast.dispose();
                        }
                        throw e;
                    }
                }
            });
        }
    }

    public boolean isDialogHasMessages(long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        final long j2 = j;
        final boolean[] zArr2 = zArr;
        final CountDownLatch countDownLatch2 = countDownLatch;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[]{Long.valueOf(j2)}), new Object[0]);
                    zArr2[0] = queryFinalized.next();
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                } catch (Throwable th) {
                    countDownLatch2.countDown();
                }
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return zArr[0];
    }

    public boolean hasAuthMessage(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[]{Integer.valueOf(i)}), new Object[0]);
                    zArr[0] = queryFinalized.next();
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                } catch (Throwable th) {
                    countDownLatch.countDown();
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return zArr[0];
    }

    public void getEncryptedChat(final int i, final CountDownLatch countDownLatch, final ArrayList<TLObject> arrayList) {
        if (countDownLatch != null) {
            if (arrayList != null) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        try {
                            Iterable arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            MessagesStorage messagesStorage = MessagesStorage.this;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(i);
                            messagesStorage.getEncryptedChatsInternal(stringBuilder.toString(), arrayList2, arrayList);
                            if (!(arrayList2.isEmpty() || arrayList.isEmpty())) {
                                ArrayList arrayList3 = new ArrayList();
                                MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList), arrayList3);
                                if (!arrayList3.isEmpty()) {
                                    arrayList.add(arrayList2.get(0));
                                    arrayList.add(arrayList3.get(0));
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        } catch (Throwable th) {
                            countDownLatch.countDown();
                        }
                        countDownLatch.countDown();
                    }
                });
            }
        }
    }

    public void putEncryptedChat(final EncryptedChat encryptedChat, final User user, final TL_dialog tL_dialog) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        if ((encryptedChat.key_hash == null || encryptedChat.key_hash.length < 16) && encryptedChat.auth_key != null) {
                            encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
                        }
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat.a_or_b != null ? encryptedChat.a_or_b.length : 1);
                        NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat.auth_key != null ? encryptedChat.auth_key.length : 1);
                        NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat.future_auth_key != null ? encryptedChat.future_auth_key.length : 1);
                        NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat.key_hash != null ? encryptedChat.key_hash.length : 1);
                        encryptedChat.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, encryptedChat.id);
                        executeFast.bindInteger(2, user.id);
                        executeFast.bindString(3, MessagesStorage.this.formatUserSearchName(user));
                        executeFast.bindByteBuffer(4, nativeByteBuffer);
                        if (encryptedChat.a_or_b != null) {
                            nativeByteBuffer2.writeBytes(encryptedChat.a_or_b);
                        }
                        if (encryptedChat.auth_key != null) {
                            nativeByteBuffer3.writeBytes(encryptedChat.auth_key);
                        }
                        if (encryptedChat.future_auth_key != null) {
                            nativeByteBuffer4.writeBytes(encryptedChat.future_auth_key);
                        }
                        if (encryptedChat.key_hash != null) {
                            nativeByteBuffer5.writeBytes(encryptedChat.key_hash);
                        }
                        executeFast.bindByteBuffer(5, nativeByteBuffer2);
                        executeFast.bindByteBuffer(6, nativeByteBuffer3);
                        executeFast.bindInteger(7, encryptedChat.ttl);
                        executeFast.bindInteger(8, encryptedChat.layer);
                        executeFast.bindInteger(9, encryptedChat.seq_in);
                        executeFast.bindInteger(10, encryptedChat.seq_out);
                        executeFast.bindInteger(11, encryptedChat.key_use_count_out | (encryptedChat.key_use_count_in << 16));
                        executeFast.bindLong(12, encryptedChat.exchange_id);
                        executeFast.bindInteger(13, encryptedChat.key_create_date);
                        executeFast.bindLong(14, encryptedChat.future_key_fingerprint);
                        executeFast.bindByteBuffer(15, nativeByteBuffer4);
                        executeFast.bindByteBuffer(16, nativeByteBuffer5);
                        executeFast.bindInteger(17, encryptedChat.in_seq_no);
                        executeFast.bindInteger(18, encryptedChat.admin_id);
                        executeFast.bindInteger(19, encryptedChat.mtproto_seq);
                        executeFast.step();
                        executeFast.dispose();
                        nativeByteBuffer.reuse();
                        nativeByteBuffer2.reuse();
                        nativeByteBuffer3.reuse();
                        nativeByteBuffer4.reuse();
                        nativeByteBuffer5.reuse();
                        if (tL_dialog != null) {
                            executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            executeFast.bindLong(1, tL_dialog.id);
                            executeFast.bindInteger(2, tL_dialog.last_message_date);
                            executeFast.bindInteger(3, tL_dialog.unread_count);
                            executeFast.bindInteger(4, tL_dialog.top_message);
                            executeFast.bindInteger(5, tL_dialog.read_inbox_max_id);
                            executeFast.bindInteger(6, tL_dialog.read_outbox_max_id);
                            executeFast.bindInteger(7, 0);
                            executeFast.bindInteger(8, tL_dialog.unread_mentions_count);
                            executeFast.bindInteger(9, tL_dialog.pts);
                            executeFast.bindInteger(10, 0);
                            executeFast.bindInteger(11, tL_dialog.pinnedNum);
                            executeFast.step();
                            executeFast.dispose();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private String formatUserSearchName(User user) {
        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        if (user.first_name != null && user.first_name.length() > 0) {
            stringBuilder.append(user.first_name);
        }
        if (user.last_name != null && user.last_name.length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(user.last_name);
        }
        stringBuilder.append(";;;");
        if (user.username != null && user.username.length() > 0) {
            stringBuilder.append(user.username);
        }
        return stringBuilder.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<User> arrayList) throws Exception {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
                for (int i = 0; i < arrayList.size(); i++) {
                    User user = (User) arrayList.get(i);
                    if (user.min) {
                        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(user.id)}), new Object[0]);
                        if (queryFinalized.next()) {
                            try {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        if (user.username != null) {
                                            TLdeserialize.username = user.username;
                                            TLdeserialize.flags |= 8;
                                        } else {
                                            TLdeserialize.username = null;
                                            TLdeserialize.flags &= -9;
                                        }
                                        if (user.photo != null) {
                                            TLdeserialize.photo = user.photo;
                                            TLdeserialize.flags |= 32;
                                        } else {
                                            TLdeserialize.photo = null;
                                            TLdeserialize.flags &= -33;
                                        }
                                        user = TLdeserialize;
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        queryFinalized.dispose();
                    }
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(user.getObjectSize());
                    user.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, user.id);
                    executeFast.bindString(2, formatUserSearchName(user));
                    if (user.status != null) {
                        if (user.status instanceof TL_userStatusRecently) {
                            user.status.expires = -100;
                        } else if (user.status instanceof TL_userStatusLastWeek) {
                            user.status.expires = -101;
                        } else if (user.status instanceof TL_userStatusLastMonth) {
                            user.status.expires = -102;
                        }
                        executeFast.bindInteger(3, user.status.expires);
                    } else {
                        executeFast.bindInteger(3, 0);
                    }
                    executeFast.bindByteBuffer(4, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
                executeFast.dispose();
            }
        }
    }

    private void putChatsInternal(ArrayList<Chat> arrayList) throws Exception {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
                for (int i = 0; i < arrayList.size(); i++) {
                    Chat chat = (Chat) arrayList.get(i);
                    if (chat.min) {
                        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(chat.id)}), new Object[0]);
                        if (queryFinalized.next()) {
                            try {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        TLdeserialize.title = chat.title;
                                        TLdeserialize.photo = chat.photo;
                                        TLdeserialize.broadcast = chat.broadcast;
                                        TLdeserialize.verified = chat.verified;
                                        TLdeserialize.megagroup = chat.megagroup;
                                        TLdeserialize.democracy = chat.democracy;
                                        if (chat.username != null) {
                                            TLdeserialize.username = chat.username;
                                            TLdeserialize.flags |= 64;
                                        } else {
                                            TLdeserialize.username = null;
                                            TLdeserialize.flags &= -65;
                                        }
                                        chat = TLdeserialize;
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        queryFinalized.dispose();
                    }
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize());
                    chat.serializeToStream(nativeByteBuffer);
                    executeFast.bindInteger(1, chat.id);
                    if (chat.title != null) {
                        executeFast.bindString(2, chat.title.toLowerCase());
                    } else {
                        executeFast.bindString(2, TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
                executeFast.dispose();
            }
        }
    }

    public void getUsersInternal(String str, ArrayList<User> arrayList) throws Exception {
        if (!(str == null || str.length() == 0)) {
            if (arrayList != null) {
                str = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
                while (str.next()) {
                    try {
                        AbstractSerializedData byteBufferValue = str.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                if (TLdeserialize.status != null) {
                                    TLdeserialize.status.expires = str.intValue(1);
                                }
                                arrayList.add(TLdeserialize);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                str.dispose();
            }
        }
    }

    public void getChatsInternal(String str, ArrayList<Chat> arrayList) throws Exception {
        if (!(str == null || str.length() == 0)) {
            if (arrayList != null) {
                str = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
                while (str.next()) {
                    try {
                        AbstractSerializedData byteBufferValue = str.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                arrayList.add(TLdeserialize);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                str.dispose();
            }
        }
    }

    public void getEncryptedChatsInternal(String str, ArrayList<EncryptedChat> arrayList, ArrayList<Integer> arrayList2) throws Exception {
        if (!(str == null || str.length() == 0)) {
            if (arrayList != null) {
                str = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
                while (str.next()) {
                    try {
                        AbstractSerializedData byteBufferValue = str.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            EncryptedChat TLdeserialize = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                TLdeserialize.user_id = str.intValue(1);
                                if (!(arrayList2 == null || arrayList2.contains(Integer.valueOf(TLdeserialize.user_id)))) {
                                    arrayList2.add(Integer.valueOf(TLdeserialize.user_id));
                                }
                                TLdeserialize.a_or_b = str.byteArrayValue(2);
                                TLdeserialize.auth_key = str.byteArrayValue(3);
                                TLdeserialize.ttl = str.intValue(4);
                                TLdeserialize.layer = str.intValue(5);
                                TLdeserialize.seq_in = str.intValue(6);
                                TLdeserialize.seq_out = str.intValue(7);
                                int intValue = str.intValue(8);
                                TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                                TLdeserialize.key_use_count_out = (short) intValue;
                                TLdeserialize.exchange_id = str.longValue(9);
                                TLdeserialize.key_create_date = str.intValue(10);
                                TLdeserialize.future_key_fingerprint = str.longValue(11);
                                TLdeserialize.future_auth_key = str.byteArrayValue(12);
                                TLdeserialize.key_hash = str.byteArrayValue(13);
                                TLdeserialize.in_seq_no = str.intValue(14);
                                intValue = str.intValue(15);
                                if (intValue != 0) {
                                    TLdeserialize.admin_id = intValue;
                                }
                                TLdeserialize.mtproto_seq = str.intValue(16);
                                arrayList.add(TLdeserialize);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                str.dispose();
            }
        }
    }

    private void putUsersAndChatsInternal(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Throwable e) {
                FileLog.m3e(e);
                return;
            }
        }
        putUsersInternal(arrayList);
        putChatsInternal(arrayList2);
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(final ArrayList<User> arrayList, final ArrayList<Chat> arrayList2, final boolean z, boolean z2) {
        if (arrayList == null || !arrayList.isEmpty() || arrayList2 == null || !arrayList2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.this.putUsersAndChatsInternal(arrayList, arrayList2, z);
                    }
                });
            } else {
                putUsersAndChatsInternal(arrayList, arrayList2, z);
            }
        }
    }

    public void removeFromDownloadQueue(long j, int i, boolean z) {
        final boolean z2 = z;
        final int i2 = i;
        final long j2 = j;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    if (z2) {
                        SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i2)}), new Object[0]);
                        int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                        queryFinalized.dispose();
                        if (intValue != -1) {
                            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[]{Integer.valueOf(intValue - 1), Long.valueOf(j2), Integer.valueOf(i2)})).stepThis().dispose();
                            return;
                        }
                        return;
                    }
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j2), Integer.valueOf(i2)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void clearDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    if (i == 0) {
                        MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                        return;
                    }
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void getDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    final ArrayList arrayList = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[]{Integer.valueOf(i)}), new Object[0]);
                    while (queryFinalized.next()) {
                        DownloadObject downloadObject = new DownloadObject();
                        downloadObject.type = queryFinalized.intValue(1);
                        downloadObject.id = queryFinalized.longValue(0);
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(2);
                        if (byteBufferValue != null) {
                            MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize.document != null) {
                                downloadObject.object = TLdeserialize.document;
                            } else if (TLdeserialize.photo != null) {
                                downloadObject.object = FileLoader.getClosestPhotoSizeWithSize(TLdeserialize.photo.sizes, AndroidUtilities.getPhotoSize());
                            }
                            downloadObject.secret = TLdeserialize.ttl_seconds != 0;
                        }
                        arrayList.add(downloadObject);
                    }
                    queryFinalized.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            DownloadController.getInstance(MessagesStorage.this.currentAccount).processDownloadObjects(i, arrayList);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private int getMessageMediaType(Message message) {
        if (message instanceof TL_message_secret) {
            if (!((((message.media instanceof TL_messageMediaPhoto) || MessageObject.isGifMessage(message)) && message.ttl > 0 && message.ttl <= 60) || MessageObject.isVoiceMessage(message) || MessageObject.isVideoMessage(message))) {
                if (!MessageObject.isRoundVideoMessage(message)) {
                    if ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) != null) {
                        return 0;
                    }
                }
            }
            return 1;
        } else if ((message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return 1;
        } else {
            if (!(message.media instanceof TL_messageMediaPhoto)) {
                if (MessageObject.isVideoMessage(message) != null) {
                }
            }
            return 0;
        }
        return -1;
    }

    public void putWebPages(final LongSparseArray<WebPage> longSparseArray) {
        if (!isEmpty((LongSparseArray) longSparseArray)) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        final ArrayList arrayList = new ArrayList();
                        int i = 0;
                        for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT mid FROM webpage_pending WHERE id = ");
                            stringBuilder.append(longSparseArray.keyAt(i2));
                            SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                            Iterable arrayList2 = new ArrayList();
                            while (queryFinalized.next()) {
                                arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                            }
                            queryFinalized.dispose();
                            if (!arrayList2.isEmpty()) {
                                queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                                while (queryFinalized.next()) {
                                    int intValue = queryFinalized.intValue(0);
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                                    if (byteBufferValue != null) {
                                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize.media instanceof TL_messageMediaWebPage) {
                                            TLdeserialize.id = intValue;
                                            TLdeserialize.media.webpage = (WebPage) longSparseArray.valueAt(i2);
                                            arrayList.add(TLdeserialize);
                                        }
                                    }
                                }
                                queryFinalized.dispose();
                            }
                        }
                        if (!arrayList.isEmpty()) {
                            MessagesStorage.this.database.beginTransaction();
                            SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                            SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
                            while (i < arrayList.size()) {
                                Message message = (Message) arrayList.get(i);
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                                message.serializeToStream(nativeByteBuffer);
                                long j = (long) message.id;
                                if (message.to_id.channel_id != 0) {
                                    j |= ((long) message.to_id.channel_id) << 32;
                                }
                                executeFast.requery();
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindLong(2, j);
                                executeFast.step();
                                executeFast2.requery();
                                executeFast2.bindByteBuffer(1, nativeByteBuffer);
                                executeFast2.bindLong(2, j);
                                executeFast2.step();
                                nativeByteBuffer.reuse();
                                i++;
                            }
                            executeFast.dispose();
                            executeFast2.dispose();
                            MessagesStorage.this.database.commitTransaction();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void overwriteChannel(final int i, final TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    int intValue;
                    int i;
                    final long j = (long) (-i);
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT pts, pinned FROM dialogs WHERE did = ");
                    stringBuilder.append(j);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    if (queryFinalized.next()) {
                        intValue = queryFinalized.intValue(1);
                        i = false;
                    } else if (i2 != 0) {
                        intValue = 0;
                        i = 1;
                    } else {
                        intValue = 0;
                        i = intValue;
                    }
                    queryFinalized.dispose();
                    access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM messages WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM bot_keyboard WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_counts_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM messages_holes WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    access$000 = MessagesStorage.this.database;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM media_holes_v2 WHERE uid = ");
                    stringBuilder2.append(j);
                    access$000.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(j, null);
                    messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                    tL_messages_dialogs.chats.addAll(tL_updates_channelDifferenceTooLong.chats);
                    tL_messages_dialogs.users.addAll(tL_updates_channelDifferenceTooLong.users);
                    tL_messages_dialogs.messages.addAll(tL_updates_channelDifferenceTooLong.messages);
                    TL_dialog tL_dialog = new TL_dialog();
                    tL_dialog.id = j;
                    tL_dialog.flags = 1;
                    tL_dialog.peer = new TL_peerChannel();
                    tL_dialog.peer.channel_id = i;
                    tL_dialog.top_message = tL_updates_channelDifferenceTooLong.top_message;
                    tL_dialog.read_inbox_max_id = tL_updates_channelDifferenceTooLong.read_inbox_max_id;
                    tL_dialog.read_outbox_max_id = tL_updates_channelDifferenceTooLong.read_outbox_max_id;
                    tL_dialog.unread_count = tL_updates_channelDifferenceTooLong.unread_count;
                    tL_dialog.unread_mentions_count = tL_updates_channelDifferenceTooLong.unread_mentions_count;
                    tL_dialog.notify_settings = null;
                    tL_dialog.pinned = intValue != 0;
                    tL_dialog.pinnedNum = intValue;
                    tL_dialog.pts = tL_updates_channelDifferenceTooLong.pts;
                    tL_messages_dialogs.dialogs.add(tL_dialog);
                    MessagesStorage.this.putDialogsInternal(tL_messages_dialogs, false);
                    MessagesStorage.this.updateDialogsWithDeletedMessages(new ArrayList(), null, false, i);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(true));
                        }
                    });
                    if (i == 0) {
                        return;
                    }
                    if (i2 == 1) {
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).checkChannelInviter(i);
                    } else {
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).generateJoinMessage(i, false);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putChannelViews(final SparseArray<SparseIntArray> sparseArray, final boolean z) {
        if (!isEmpty((SparseArray) sparseArray)) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        MessagesStorage.this.database.beginTransaction();
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
                        for (int i = 0; i < sparseArray.size(); i++) {
                            int keyAt = sparseArray.keyAt(i);
                            SparseIntArray sparseIntArray = (SparseIntArray) sparseArray.get(keyAt);
                            for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
                                int i3 = sparseIntArray.get(sparseIntArray.keyAt(i2));
                                long keyAt2 = (long) sparseIntArray.keyAt(i2);
                                if (z) {
                                    keyAt2 |= ((long) (-keyAt)) << 32;
                                }
                                executeFast.requery();
                                executeFast.bindLong(1, keyAt2);
                                executeFast.bindInteger(2, i3);
                                executeFast.bindLong(3, keyAt2);
                                executeFast.step();
                            }
                        }
                        executeFast.dispose();
                        MessagesStorage.this.database.commitTransaction();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private boolean isValidKeyboardToSave(Message message) {
        return (message.reply_markup == null || (message.reply_markup instanceof TL_replyInlineMarkup) || (message.reply_markup.selective && message.mentioned == null)) ? null : true;
    }

    private void putMessagesInternal(ArrayList<Message> arrayList, boolean z, boolean z2, int i, boolean z3) {
        Message message;
        int intValue;
        Throwable e;
        Throwable th;
        SQLitePreparedStatement sQLitePreparedStatement;
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2;
        LongSparseArray longSparseArray3;
        LongSparseArray longSparseArray4;
        LongSparseArray longSparseArray5;
        int i2;
        SparseArray sparseArray;
        long longValue;
        LongSparseArray longSparseArray6;
        int i3;
        SQLitePreparedStatement sQLitePreparedStatement2;
        LongSparseArray longSparseArray7;
        SQLitePreparedStatement sQLitePreparedStatement3;
        SparseArray sparseArray2;
        SQLitePreparedStatement sQLitePreparedStatement4;
        int i4;
        MessagesStorage messagesStorage = this;
        ArrayList arrayList2 = arrayList;
        if (z3) {
            try {
                message = (Message) arrayList2.get(0);
                if (message.dialog_id == 0) {
                    if (message.to_id.user_id != 0) {
                        message.dialog_id = (long) message.to_id.user_id;
                    } else if (message.to_id.chat_id != 0) {
                        message.dialog_id = (long) (-message.to_id.chat_id);
                    } else {
                        message.dialog_id = (long) (-message.to_id.channel_id);
                    }
                }
                SQLiteDatabase sQLiteDatabase = messagesStorage.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT last_mid FROM dialogs WHERE did = ");
                stringBuilder.append(message.dialog_id);
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                queryFinalized.dispose();
                if (intValue != 0) {
                    return;
                }
            } catch (Exception e2) {
                e = e2;
                th = e;
                FileLog.m3e(th);
            }
        }
        if (z) {
            messagesStorage.database.beginTransaction();
        }
        LongSparseArray longSparseArray8 = new LongSparseArray();
        LongSparseArray longSparseArray9 = new LongSparseArray();
        LongSparseArray longSparseArray10 = new LongSparseArray();
        LongSparseArray longSparseArray11 = new LongSparseArray();
        StringBuilder stringBuilder2 = new StringBuilder();
        LongSparseArray longSparseArray12 = new LongSparseArray();
        LongSparseArray longSparseArray13 = new LongSparseArray();
        LongSparseArray longSparseArray14 = new LongSparseArray();
        SQLitePreparedStatement executeFast = messagesStorage.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
        SQLitePreparedStatement executeFast2 = messagesStorage.database.executeFast("REPLACE INTO randoms VALUES(?, ?)");
        SQLitePreparedStatement executeFast3 = messagesStorage.database.executeFast("REPLACE INTO download_queue VALUES(?, ?, ?, ?)");
        SQLitePreparedStatement executeFast4 = messagesStorage.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
        SQLitePreparedStatement sQLitePreparedStatement5 = executeFast2;
        SQLitePreparedStatement sQLitePreparedStatement6 = executeFast3;
        LongSparseArray longSparseArray15 = longSparseArray8;
        LongSparseArray longSparseArray16 = null;
        LongSparseArray longSparseArray17 = null;
        StringBuilder stringBuilder3 = null;
        int i5 = 0;
        while (i5 < arrayList.size()) {
            SQLiteCursor queryFinalized2;
            LongSparseArray longSparseArray18;
            message = (Message) arrayList2.get(i5);
            sQLitePreparedStatement = executeFast;
            longSparseArray = longSparseArray9;
            longSparseArray2 = longSparseArray10;
            long j = (long) message.id;
            longSparseArray3 = longSparseArray17;
            if (message.dialog_id == 0) {
                if (message.to_id.user_id != 0) {
                    message.dialog_id = (long) message.to_id.user_id;
                } else if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                }
            }
            if (message.to_id.channel_id != 0) {
                j |= ((long) message.to_id.channel_id) << 32;
            }
            if (message.mentioned && message.media_unread) {
                longSparseArray14.put(j, Long.valueOf(message.dialog_id));
            }
            if ((message.action instanceof TL_messageActionHistoryClear) || MessageObject.isOut(message) || (message.id <= 0 && !MessageObject.isUnread(message))) {
                longSparseArray4 = longSparseArray16;
            } else {
                Integer num = (Integer) longSparseArray12.get(message.dialog_id);
                if (num == null) {
                    SQLiteDatabase sQLiteDatabase2 = messagesStorage.database;
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("SELECT inbox_max FROM dialogs WHERE did = ");
                    longSparseArray4 = longSparseArray16;
                    stringBuilder4.append(message.dialog_id);
                    queryFinalized2 = sQLiteDatabase2.queryFinalized(stringBuilder4.toString(), new Object[0]);
                    if (queryFinalized2.next()) {
                        num = Integer.valueOf(queryFinalized2.intValue(0));
                    } else {
                        num = Integer.valueOf(0);
                    }
                    queryFinalized2.dispose();
                    longSparseArray12.put(message.dialog_id, num);
                } else {
                    longSparseArray4 = longSparseArray16;
                }
                if (message.id < 0 || r4.intValue() < message.id) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(",");
                    }
                    stringBuilder2.append(j);
                    longSparseArray13.put(j, Long.valueOf(message.dialog_id));
                }
            }
            if (DataQuery.canAddMessageToMedia(message)) {
                if (stringBuilder3 == null) {
                    stringBuilder3 = new StringBuilder();
                    longSparseArray16 = new LongSparseArray();
                    longSparseArray17 = new LongSparseArray();
                } else {
                    longSparseArray17 = longSparseArray3;
                    longSparseArray16 = longSparseArray4;
                }
                if (stringBuilder3.length() > 0) {
                    stringBuilder3.append(",");
                }
                stringBuilder3.append(j);
                longSparseArray18 = longSparseArray12;
                longSparseArray5 = longSparseArray13;
                longSparseArray16.put(j, Long.valueOf(message.dialog_id));
                longSparseArray17.put(j, Integer.valueOf(DataQuery.getMediaType(message)));
            } else {
                longSparseArray18 = longSparseArray12;
                longSparseArray5 = longSparseArray13;
                longSparseArray17 = longSparseArray3;
                longSparseArray16 = longSparseArray4;
            }
            if (isValidKeyboardToSave(message)) {
                Message message2 = (Message) longSparseArray11.get(message.dialog_id);
                if (message2 == null || message2.id < message.id) {
                    longSparseArray11.put(message.dialog_id, message);
                }
            }
            i5++;
            executeFast = sQLitePreparedStatement;
            longSparseArray9 = longSparseArray;
            longSparseArray10 = longSparseArray2;
            longSparseArray12 = longSparseArray18;
            longSparseArray13 = longSparseArray5;
            ArrayList<Message> arrayList3 = arrayList;
        }
        longSparseArray4 = longSparseArray16;
        sQLitePreparedStatement = executeFast;
        longSparseArray3 = longSparseArray17;
        longSparseArray = longSparseArray9;
        longSparseArray2 = longSparseArray10;
        longSparseArray5 = longSparseArray13;
        for (int i6 = 0; i6 < longSparseArray11.size(); i6++) {
            DataQuery.getInstance(messagesStorage.currentAccount).putBotKeyboard(longSparseArray11.keyAt(i6), (Message) longSparseArray11.valueAt(i6));
        }
        if (stringBuilder3 != null) {
            SQLiteDatabase sQLiteDatabase3 = messagesStorage.database;
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append("SELECT mid FROM media_v2 WHERE mid IN(");
            stringBuilder5.append(stringBuilder3.toString());
            stringBuilder5.append(")");
            i2 = 0;
            queryFinalized2 = sQLiteDatabase3.queryFinalized(stringBuilder5.toString(), new Object[0]);
            while (queryFinalized2.next()) {
                longSparseArray16 = longSparseArray4;
                longSparseArray16.remove(queryFinalized2.longValue(i2));
                longSparseArray4 = longSparseArray16;
                i2 = 0;
            }
            longSparseArray16 = longSparseArray4;
            queryFinalized2.dispose();
            sparseArray = new SparseArray();
            i2 = 0;
            while (i2 < longSparseArray16.size()) {
                Integer valueOf;
                long keyAt = longSparseArray16.keyAt(i2);
                longValue = ((Long) longSparseArray16.valueAt(i2)).longValue();
                longSparseArray10 = longSparseArray3;
                Integer num2 = (Integer) longSparseArray10.get(keyAt);
                longSparseArray6 = (LongSparseArray) sparseArray.get(num2.intValue());
                if (longSparseArray6 == null) {
                    longSparseArray6 = new LongSparseArray();
                    valueOf = Integer.valueOf(0);
                    sparseArray.put(num2.intValue(), longSparseArray6);
                } else {
                    valueOf = (Integer) longSparseArray6.get(longValue);
                }
                if (valueOf == null) {
                    valueOf = Integer.valueOf(0);
                }
                longSparseArray6.put(longValue, Integer.valueOf(valueOf.intValue() + 1));
                i2++;
                longSparseArray3 = longSparseArray10;
            }
        } else {
            sparseArray = null;
        }
        if (stringBuilder2.length() > 0) {
            LongSparseArray longSparseArray19;
            Integer num3;
            SQLiteDatabase sQLiteDatabase4 = messagesStorage.database;
            StringBuilder stringBuilder6 = new StringBuilder();
            stringBuilder6.append("SELECT mid FROM messages WHERE mid IN(");
            stringBuilder6.append(stringBuilder2.toString());
            stringBuilder6.append(")");
            int i7 = 0;
            SQLiteCursor queryFinalized3 = sQLiteDatabase4.queryFinalized(stringBuilder6.toString(), new Object[0]);
            while (queryFinalized3.next()) {
                longValue = queryFinalized3.longValue(i7);
                longSparseArray19 = longSparseArray5;
                longSparseArray19.remove(longValue);
                longSparseArray14.remove(longValue);
                longSparseArray5 = longSparseArray19;
                i7 = 0;
            }
            longSparseArray19 = longSparseArray5;
            queryFinalized3.dispose();
            i3 = 0;
            while (i3 < longSparseArray19.size()) {
                keyAt = ((Long) longSparseArray19.valueAt(i3)).longValue();
                longSparseArray8 = longSparseArray;
                num3 = (Integer) longSparseArray8.get(keyAt);
                if (num3 == null) {
                    num3 = Integer.valueOf(0);
                }
                longSparseArray8.put(keyAt, Integer.valueOf(num3.intValue() + 1));
                i3++;
                longSparseArray = longSparseArray8;
            }
            longSparseArray8 = longSparseArray;
            i3 = 0;
            while (i3 < longSparseArray14.size()) {
                long longValue2 = ((Long) longSparseArray14.valueAt(i3)).longValue();
                longSparseArray6 = longSparseArray2;
                num3 = (Integer) longSparseArray6.get(longValue2);
                if (num3 == null) {
                    num3 = Integer.valueOf(0);
                }
                longSparseArray6.put(longValue2, Integer.valueOf(num3.intValue() + 1));
                i3++;
                longSparseArray2 = longSparseArray6;
            }
        } else {
            longSparseArray8 = longSparseArray;
        }
        longSparseArray6 = longSparseArray2;
        ArrayList arrayList4 = arrayList;
        i2 = 0;
        executeFast2 = null;
        intValue = 0;
        while (i2 < arrayList.size()) {
            SQLitePreparedStatement sQLitePreparedStatement7;
            SQLitePreparedStatement sQLitePreparedStatement8;
            int i8;
            ArrayList<Message> arrayList5;
            Message message3 = (Message) arrayList4.get(i2);
            fixUnsupportedMedia(message3);
            sQLitePreparedStatement2 = sQLitePreparedStatement;
            sQLitePreparedStatement2.requery();
            long j2 = (long) message3.id;
            if (message3.local_id != 0) {
                j2 = (long) message3.local_id;
            }
            if (message3.to_id.channel_id != 0) {
                longSparseArray7 = longSparseArray8;
                j2 |= ((long) message3.to_id.channel_id) << 32;
            } else {
                longSparseArray7 = longSparseArray8;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message3.getObjectSize());
            message3.serializeToStream(nativeByteBuffer);
            Object obj = (message3.action == null || !(message3.action instanceof TL_messageEncryptedAction) || (message3.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) || (message3.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages)) ? 1 : null;
            if (obj != null) {
                SQLitePreparedStatement sQLitePreparedStatement9 = sQLitePreparedStatement2;
                longSparseArray8 = longSparseArray15;
                Message message4 = (Message) longSparseArray8.get(message3.dialog_id);
                if (message4 == null || message3.date > message4.date || ((message3.id > 0 && message4.id > 0 && message3.id > message4.id) || (message3.id < 0 && message4.id < 0 && message3.id < message4.id))) {
                    longSparseArray8.put(message3.dialog_id, message3);
                }
                sQLitePreparedStatement7 = sQLitePreparedStatement9;
            } else {
                longSparseArray8 = longSparseArray15;
                sQLitePreparedStatement7 = sQLitePreparedStatement2;
            }
            sQLitePreparedStatement7.bindLong(1, j2);
            sQLitePreparedStatement7.bindLong(2, message3.dialog_id);
            sQLitePreparedStatement7.bindInteger(3, MessageObject.getUnreadFlags(message3));
            sQLitePreparedStatement7.bindInteger(4, message3.send_state);
            sQLitePreparedStatement7.bindInteger(5, message3.date);
            sQLitePreparedStatement7.bindByteBuffer(6, nativeByteBuffer);
            sQLitePreparedStatement7.bindInteger(7, MessageObject.isOut(message3));
            sQLitePreparedStatement7.bindInteger(8, message3.ttl);
            if ((message3.flags & 1024) != 0) {
                sQLitePreparedStatement7.bindInteger(9, message3.views);
            } else {
                sQLitePreparedStatement7.bindInteger(9, getMessageMediaType(message3));
            }
            sQLitePreparedStatement7.bindInteger(10, 0);
            sQLitePreparedStatement7.bindInteger(11, message3.mentioned);
            sQLitePreparedStatement7.step();
            if (message3.random_id != 0) {
                sQLitePreparedStatement3 = sQLitePreparedStatement5;
                sQLitePreparedStatement3.requery();
                sparseArray2 = sparseArray;
                sQLitePreparedStatement3.bindLong(1, message3.random_id);
                sQLitePreparedStatement3.bindLong(2, j2);
                sQLitePreparedStatement3.step();
            } else {
                sparseArray2 = sparseArray;
                sQLitePreparedStatement3 = sQLitePreparedStatement5;
            }
            if (DataQuery.canAddMessageToMedia(message3)) {
                if (executeFast2 == null) {
                    executeFast2 = messagesStorage.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                }
                executeFast2.requery();
                executeFast2.bindLong(1, j2);
                executeFast2.bindLong(2, message3.dialog_id);
                executeFast2.bindInteger(3, message3.date);
                executeFast2.bindInteger(4, DataQuery.getMediaType(message3));
                executeFast2.bindByteBuffer(5, nativeByteBuffer);
                executeFast2.step();
            }
            if (message3.media instanceof TL_messageMediaWebPage) {
                sQLitePreparedStatement4 = executeFast4;
                sQLitePreparedStatement4.requery();
                sQLitePreparedStatement8 = executeFast2;
                sQLitePreparedStatement4.bindLong(1, message3.media.webpage.id);
                sQLitePreparedStatement4.bindLong(2, j2);
                sQLitePreparedStatement4.step();
            } else {
                sQLitePreparedStatement8 = executeFast2;
                sQLitePreparedStatement4 = executeFast4;
            }
            nativeByteBuffer.reuse();
            if (i != 0 && ((message3.to_id.channel_id == 0 || message3.post) && message3.date >= ConnectionsManager.getInstance(messagesStorage.currentAccount).getCurrentTime() - 3600 && DownloadController.getInstance(messagesStorage.currentAccount).canDownloadMedia(message3) && ((message3.media instanceof TL_messageMediaPhoto) || (message3.media instanceof TL_messageMediaDocument)))) {
                MessageMedia tL_messageMediaDocument;
                if (MessageObject.isVoiceMessage(message3)) {
                    j2 = message3.media.document.id;
                    tL_messageMediaDocument = new TL_messageMediaDocument();
                    tL_messageMediaDocument.document = message3.media.document;
                    tL_messageMediaDocument.flags |= 1;
                    i8 = 2;
                } else if (MessageObject.isRoundVideoMessage(message3)) {
                    j2 = message3.media.document.id;
                    MessageMedia tL_messageMediaDocument2 = new TL_messageMediaDocument();
                    tL_messageMediaDocument2.document = message3.media.document;
                    tL_messageMediaDocument2.flags |= 1;
                    MessageMedia messageMedia = tL_messageMediaDocument2;
                    i8 = 64;
                    tL_messageMediaDocument = messageMedia;
                } else {
                    if (message3.media instanceof TL_messageMediaPhoto) {
                        if (FileLoader.getClosestPhotoSizeWithSize(message3.media.photo.sizes, AndroidUtilities.getPhotoSize()) != null) {
                            j2 = message3.media.photo.id;
                            tL_messageMediaDocument = new TL_messageMediaPhoto();
                            tL_messageMediaDocument.photo = message3.media.photo;
                            tL_messageMediaDocument.flags |= 1;
                            i8 = 1;
                        }
                    } else if (MessageObject.isVideoMessage(message3)) {
                        j2 = message3.media.document.id;
                        tL_messageMediaDocument = new TL_messageMediaDocument();
                        tL_messageMediaDocument.document = message3.media.document;
                        tL_messageMediaDocument.flags |= 1;
                        i8 = 4;
                    } else if (!(!(message3.media instanceof TL_messageMediaDocument) || MessageObject.isMusicMessage(message3) || MessageObject.isGifDocument(message3.media.document))) {
                        j2 = message3.media.document.id;
                        tL_messageMediaDocument = new TL_messageMediaDocument();
                        tL_messageMediaDocument.document = message3.media.document;
                        tL_messageMediaDocument.flags |= 1;
                        i8 = 8;
                    }
                    tL_messageMediaDocument = null;
                    j2 = 0;
                    i8 = 0;
                }
                if (tL_messageMediaDocument != null) {
                    if (message3.media.ttl_seconds != 0) {
                        tL_messageMediaDocument.ttl_seconds = message3.media.ttl_seconds;
                        tL_messageMediaDocument.flags |= 4;
                    }
                    intValue |= i8;
                    sQLitePreparedStatement2 = sQLitePreparedStatement6;
                    sQLitePreparedStatement2.requery();
                    int i9 = intValue;
                    NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tL_messageMediaDocument.getObjectSize());
                    tL_messageMediaDocument.serializeToStream(nativeByteBuffer2);
                    sQLitePreparedStatement2.bindLong(1, j2);
                    sQLitePreparedStatement2.bindInteger(2, i8);
                    sQLitePreparedStatement2.bindInteger(3, message3.date);
                    sQLitePreparedStatement2.bindByteBuffer(4, nativeByteBuffer2);
                    sQLitePreparedStatement2.step();
                    nativeByteBuffer2.reuse();
                    intValue = i9;
                    i2++;
                    executeFast4 = sQLitePreparedStatement4;
                    sQLitePreparedStatement5 = sQLitePreparedStatement3;
                    longSparseArray15 = longSparseArray8;
                    sQLitePreparedStatement = sQLitePreparedStatement7;
                    sQLitePreparedStatement6 = sQLitePreparedStatement2;
                    longSparseArray8 = longSparseArray7;
                    sparseArray = sparseArray2;
                    executeFast2 = sQLitePreparedStatement8;
                    arrayList5 = arrayList;
                }
            }
            sQLitePreparedStatement2 = sQLitePreparedStatement6;
            i2++;
            executeFast4 = sQLitePreparedStatement4;
            sQLitePreparedStatement5 = sQLitePreparedStatement3;
            longSparseArray15 = longSparseArray8;
            sQLitePreparedStatement = sQLitePreparedStatement7;
            sQLitePreparedStatement6 = sQLitePreparedStatement2;
            longSparseArray8 = longSparseArray7;
            sparseArray = sparseArray2;
            executeFast2 = sQLitePreparedStatement8;
            arrayList5 = arrayList;
        }
        sparseArray2 = sparseArray;
        longSparseArray7 = longSparseArray8;
        sQLitePreparedStatement2 = sQLitePreparedStatement6;
        sQLitePreparedStatement4 = executeFast4;
        sQLitePreparedStatement3 = sQLitePreparedStatement5;
        longSparseArray8 = longSparseArray15;
        sQLitePreparedStatement.dispose();
        if (executeFast2 != null) {
            executeFast2.dispose();
        }
        sQLitePreparedStatement3.dispose();
        sQLitePreparedStatement2.dispose();
        sQLitePreparedStatement4.dispose();
        sQLitePreparedStatement4 = messagesStorage.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        i3 = 0;
        while (i3 < longSparseArray8.size()) {
            int i10;
            LongSparseArray longSparseArray20;
            int i11;
            longValue2 = longSparseArray8.keyAt(i3);
            if (longValue2 == 0) {
                i10 = i3;
                longSparseArray20 = longSparseArray8;
                i4 = intValue;
                longSparseArray16 = longSparseArray7;
            } else {
                int intValue2;
                int intValue3;
                int i12;
                int i13;
                int i14;
                Message message5 = (Message) longSparseArray8.valueAt(i3);
                int i15 = message5 != null ? message5.to_id.channel_id : 0;
                SQLiteDatabase sQLiteDatabase5 = messagesStorage.database;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder2.append(longValue2);
                SQLiteCursor queryFinalized4 = sQLiteDatabase5.queryFinalized(stringBuilder2.toString(), new Object[0]);
                i11 = i15 != 0 ? 1 : 0;
                if (queryFinalized4.next()) {
                    i11 = queryFinalized4.intValue(0);
                    i8 = queryFinalized4.intValue(1);
                    intValue2 = queryFinalized4.intValue(2);
                    int intValue4 = queryFinalized4.intValue(3);
                    int intValue5 = queryFinalized4.intValue(4);
                    int intValue6 = queryFinalized4.intValue(5);
                    int intValue7 = queryFinalized4.intValue(6);
                    intValue3 = queryFinalized4.intValue(7);
                    longSparseArray20 = longSparseArray8;
                    i4 = intValue;
                    i12 = i11;
                    intValue = intValue2;
                    i11 = intValue4;
                    intValue2 = intValue5;
                    i13 = intValue6;
                    i14 = intValue7;
                } else {
                    if (i15 != 0) {
                        MessagesController.getInstance(messagesStorage.currentAccount).checkChannelInviter(i15);
                    }
                    longSparseArray20 = longSparseArray8;
                    i4 = intValue;
                    intValue = i11;
                    i14 = 0;
                    i13 = 0;
                    i11 = 0;
                    i12 = 0;
                    i8 = 0;
                    intValue2 = 0;
                    intValue3 = 0;
                }
                try {
                    int i16;
                    Integer num4;
                    long j3;
                    queryFinalized4.dispose();
                    Integer num5 = (Integer) longSparseArray6.get(longValue2);
                    i10 = i3;
                    longSparseArray16 = longSparseArray7;
                    int i17 = i14;
                    Integer num6 = (Integer) longSparseArray16.get(longValue2);
                    if (num6 == null) {
                        i16 = intValue;
                        num6 = Integer.valueOf(0);
                    } else {
                        i16 = intValue;
                        longSparseArray16.put(longValue2, Integer.valueOf(num6.intValue() + i8));
                    }
                    if (num5 == null) {
                        num5 = Integer.valueOf(0);
                    } else {
                        longSparseArray6.put(longValue2, Integer.valueOf(num5.intValue() + intValue3));
                    }
                    if (message5 != null) {
                        num4 = num5;
                        j3 = (long) message5.id;
                    } else {
                        num4 = num5;
                        j3 = (long) i11;
                    }
                    if (!(message5 == null || message5.local_id == 0)) {
                        j3 = (long) message5.local_id;
                    }
                    if (i15 != 0) {
                        j3 |= ((long) i15) << 32;
                    }
                    sQLitePreparedStatement4.requery();
                    sQLitePreparedStatement4.bindLong(1, longValue2);
                    if (message5 == null || (z2 && i12 != 0)) {
                        sQLitePreparedStatement4.bindInteger(2, i12);
                    } else {
                        sQLitePreparedStatement4.bindInteger(2, message5.date);
                    }
                    sQLitePreparedStatement4.bindInteger(3, i8 + num6.intValue());
                    sQLitePreparedStatement4.bindLong(4, j3);
                    sQLitePreparedStatement4.bindInteger(5, intValue2);
                    sQLitePreparedStatement4.bindInteger(6, i13);
                    sQLitePreparedStatement4.bindLong(7, 0);
                    sQLitePreparedStatement4.bindInteger(8, intValue3 + num4.intValue());
                    sQLitePreparedStatement4.bindInteger(9, i16);
                    sQLitePreparedStatement4.bindInteger(10, 0);
                    sQLitePreparedStatement4.bindInteger(11, i17);
                    sQLitePreparedStatement4.step();
                } catch (Throwable e3) {
                    th = e3;
                    messagesStorage = this;
                }
            }
            longSparseArray7 = longSparseArray16;
            i3 = i10 + 1;
            longSparseArray8 = longSparseArray20;
            intValue = i4;
            messagesStorage = this;
        }
        i4 = intValue;
        longSparseArray16 = longSparseArray7;
        try {
            LongSparseArray longSparseArray21;
            sQLitePreparedStatement4.dispose();
            if (sparseArray2 != null) {
                sQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
                SparseArray sparseArray3 = sparseArray2;
                i2 = 0;
                while (i2 < sparseArray3.size()) {
                    i5 = sparseArray3.keyAt(i2);
                    longSparseArray8 = (LongSparseArray) sparseArray3.valueAt(i2);
                    intValue = 0;
                    while (intValue < longSparseArray8.size()) {
                        long keyAt2 = longSparseArray8.keyAt(intValue);
                        SQLiteDatabase sQLiteDatabase6 = messagesStorage.database;
                        SparseArray sparseArray4 = sparseArray3;
                        r5 = new Object[2];
                        longSparseArray21 = longSparseArray16;
                        r5[0] = Long.valueOf(keyAt2);
                        r5[1] = Integer.valueOf(i5);
                        SQLiteCursor queryFinalized5 = sQLiteDatabase6.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", r5), new Object[0]);
                        i11 = queryFinalized5.next() ? queryFinalized5.intValue(0) : -1;
                        queryFinalized5.dispose();
                        if (i11 != -1) {
                            sQLitePreparedStatement4.requery();
                            i11 += ((Integer) longSparseArray8.valueAt(intValue)).intValue();
                            sQLitePreparedStatement4.bindLong(1, keyAt2);
                            sQLitePreparedStatement4.bindInteger(2, i5);
                            sQLitePreparedStatement4.bindInteger(3, i11);
                            sQLitePreparedStatement4.step();
                        }
                        intValue++;
                        sparseArray3 = sparseArray4;
                        longSparseArray16 = longSparseArray21;
                    }
                    i2++;
                    sparseArray3 = sparseArray3;
                    longSparseArray16 = longSparseArray16;
                }
                longSparseArray21 = longSparseArray16;
                sQLitePreparedStatement4.dispose();
            } else {
                longSparseArray21 = longSparseArray16;
                messagesStorage = this;
            }
            if (z) {
                messagesStorage.database.commitTransaction();
            }
            MessagesController.getInstance(messagesStorage.currentAccount).processDialogsUpdateRead(longSparseArray21, longSparseArray6);
            if (i4 != 0) {
                intValue = i4;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DownloadController.getInstance(MessagesStorage.this.currentAccount).newDownloadObjectsAvailable(intValue);
                    }
                });
            }
        } catch (Exception e4) {
            e3 = e4;
            messagesStorage = this;
            th = e3;
            FileLog.m3e(th);
        }
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i) {
        putMessages(arrayList, z, z2, z3, i, false);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        if (arrayList.size() != 0) {
            if (z2) {
                final ArrayList<Message> arrayList2 = arrayList;
                final boolean z5 = z;
                final boolean z6 = z3;
                final int i2 = i;
                final boolean z7 = z4;
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.this.putMessagesInternal(arrayList2, z5, z6, i2, z7);
                    }
                });
            } else {
                putMessagesInternal(arrayList, z, z3, i, z4);
            }
        }
    }

    public void markMessageAsSendError(final Message message) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    long j = (long) message.id;
                    if (message.to_id.channel_id != 0) {
                        j |= ((long) message.to_id.channel_id) << 32;
                    }
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("UPDATE messages SET send_state = 2 WHERE mid = ");
                    stringBuilder.append(j);
                    access$000.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void setMessageSeq(final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindInteger(2, i2);
                    executeFast.bindInteger(3, i3);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private long[] updateMessageStateAndIdInternal(long r20, java.lang.Integer r22, int r23, int r24, int r25) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r19 = this;
        r1 = r19;
        r2 = r24;
        r3 = r23;
        r4 = r25;
        r5 = (long) r3;
        r3 = 0;
        r7 = 0;
        r8 = 1;
        if (r22 != 0) goto L_0x0063;
    L_0x000e:
        r10 = r1.database;	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r11 = java.util.Locale.US;	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r12 = "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1";	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r14 = java.lang.Long.valueOf(r20);	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r13[r7] = r14;	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r11 = java.lang.String.format(r11, r12, r13);	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r12 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r10 = r10.queryFinalized(r11, r12);	 Catch:{ Exception -> 0x0049, all -> 0x0046 }
        r11 = r10.next();	 Catch:{ Exception -> 0x0042, all -> 0x003e }
        if (r11 == 0) goto L_0x0036;	 Catch:{ Exception -> 0x0042, all -> 0x003e }
    L_0x002c:
        r11 = r10.intValue(r7);	 Catch:{ Exception -> 0x0042, all -> 0x003e }
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0042, all -> 0x003e }
        r9 = r11;
        goto L_0x0038;
    L_0x0036:
        r9 = r22;
    L_0x0038:
        if (r10 == 0) goto L_0x0057;
    L_0x003a:
        r10.dispose();
        goto L_0x0057;
    L_0x003e:
        r0 = move-exception;
        r2 = r0;
        r3 = r10;
        goto L_0x005d;
    L_0x0042:
        r0 = move-exception;
        r11 = r10;
        r10 = r0;
        goto L_0x004c;
    L_0x0046:
        r0 = move-exception;
        r2 = r0;
        goto L_0x005d;
    L_0x0049:
        r0 = move-exception;
        r10 = r0;
        r11 = r3;
    L_0x004c:
        org.telegram.messenger.FileLog.m3e(r10);	 Catch:{ all -> 0x005a }
        if (r11 == 0) goto L_0x0054;
    L_0x0051:
        r11.dispose();
    L_0x0054:
        r9 = r22;
        r10 = r11;
    L_0x0057:
        if (r9 != 0) goto L_0x0066;
    L_0x0059:
        return r3;
    L_0x005a:
        r0 = move-exception;
        r2 = r0;
        r3 = r11;
    L_0x005d:
        if (r3 == 0) goto L_0x0062;
    L_0x005f:
        r3.dispose();
    L_0x0062:
        throw r2;
    L_0x0063:
        r9 = r22;
        r10 = r3;
    L_0x0066:
        r11 = r9.intValue();
        r11 = (long) r11;
        if (r4 == 0) goto L_0x0078;
    L_0x006d:
        r13 = (long) r4;
        r4 = 32;
        r13 = r13 << r4;
        r15 = r11 | r13;
        r11 = r5 | r13;
        r13 = r11;
        r11 = r15;
        goto L_0x0079;
    L_0x0078:
        r13 = r5;
    L_0x0079:
        r4 = r1.database;	 Catch:{ Exception -> 0x00b3 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x00b3 }
        r15 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";	 Catch:{ Exception -> 0x00b3 }
        r7 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x00b3 }
        r16 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x00b3 }
        r8 = 0;	 Catch:{ Exception -> 0x00b3 }
        r7[r8] = r16;	 Catch:{ Exception -> 0x00b3 }
        r3 = java.lang.String.format(r3, r15, r7);	 Catch:{ Exception -> 0x00b3 }
        r7 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x00b3 }
        r3 = r4.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x00b3 }
        r4 = r3.next();	 Catch:{ Exception -> 0x00ac, all -> 0x00a7 }
        if (r4 == 0) goto L_0x009d;	 Catch:{ Exception -> 0x00ac, all -> 0x00a7 }
    L_0x0098:
        r15 = r3.longValue(r8);	 Catch:{ Exception -> 0x00ac, all -> 0x00a7 }
        goto L_0x009f;
    L_0x009d:
        r15 = 0;
    L_0x009f:
        if (r3 == 0) goto L_0x00a4;
    L_0x00a1:
        r3.dispose();
    L_0x00a4:
        r3 = 0;
        goto L_0x00c1;
    L_0x00a7:
        r0 = move-exception;
        r2 = r0;
        r10 = r3;
        goto L_0x020f;
    L_0x00ac:
        r0 = move-exception;
        r10 = r3;
        goto L_0x00b4;
    L_0x00af:
        r0 = move-exception;
        r2 = r0;
        goto L_0x020f;
    L_0x00b3:
        r0 = move-exception;
    L_0x00b4:
        r3 = r0;
        org.telegram.messenger.FileLog.m3e(r3);	 Catch:{ all -> 0x00af }
        if (r10 == 0) goto L_0x00bd;
    L_0x00ba:
        r10.dispose();
    L_0x00bd:
        r3 = 0;
        r15 = 0;
    L_0x00c1:
        r7 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1));
        if (r7 != 0) goto L_0x00c7;
    L_0x00c5:
        r3 = 0;
        return r3;
    L_0x00c7:
        r3 = 0;
        r4 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        r7 = 2;
        if (r4 != 0) goto L_0x010b;
    L_0x00cd:
        if (r2 == 0) goto L_0x010b;
    L_0x00cf:
        r4 = r1.database;	 Catch:{ Exception -> 0x00f2 }
        r8 = "UPDATE messages SET send_state = 0, date = ? WHERE mid = ?";	 Catch:{ Exception -> 0x00f2 }
        r4 = r4.executeFast(r8);	 Catch:{ Exception -> 0x00f2 }
        r8 = 1;
        r4.bindInteger(r8, r2);	 Catch:{ Exception -> 0x00eb, all -> 0x00e7 }
        r4.bindLong(r7, r13);	 Catch:{ Exception -> 0x00eb, all -> 0x00e7 }
        r4.step();	 Catch:{ Exception -> 0x00eb, all -> 0x00e7 }
        if (r4 == 0) goto L_0x00fc;
    L_0x00e3:
        r4.dispose();
        goto L_0x00fc;
    L_0x00e7:
        r0 = move-exception;
        r2 = r0;
        r3 = r4;
        goto L_0x0105;
    L_0x00eb:
        r0 = move-exception;
        r2 = r0;
        r3 = r4;
        goto L_0x00f4;
    L_0x00ef:
        r0 = move-exception;
        r2 = r0;
        goto L_0x0105;
    L_0x00f2:
        r0 = move-exception;
        r2 = r0;
    L_0x00f4:
        org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x00ef }
        if (r3 == 0) goto L_0x00fc;
    L_0x00f9:
        r3.dispose();
    L_0x00fc:
        r2 = new long[r7];
        r3 = 0;
        r2[r3] = r15;
        r3 = 1;
        r2[r3] = r5;
        return r2;
    L_0x0105:
        if (r3 == 0) goto L_0x010a;
    L_0x0107:
        r3.dispose();
    L_0x010a:
        throw r2;
    L_0x010b:
        r2 = r1.database;	 Catch:{ Exception -> 0x0124, all -> 0x0120 }
        r4 = "UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?";	 Catch:{ Exception -> 0x0124, all -> 0x0120 }
        r2 = r2.executeFast(r4);	 Catch:{ Exception -> 0x0124, all -> 0x0120 }
        r4 = 1;
        r2.bindLong(r4, r13);	 Catch:{ Exception -> 0x0125 }
        r2.bindLong(r7, r11);	 Catch:{ Exception -> 0x0125 }
        r2.step();	 Catch:{ Exception -> 0x0125 }
        if (r2 == 0) goto L_0x0174;
    L_0x011f:
        goto L_0x0170;
    L_0x0120:
        r0 = move-exception;
    L_0x0121:
        r2 = r0;
        goto L_0x0209;
    L_0x0124:
        r2 = r3;
    L_0x0125:
        r4 = r1.database;	 Catch:{ Exception -> 0x0169 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0169 }
        r6 = "DELETE FROM messages WHERE mid = %d";	 Catch:{ Exception -> 0x0169 }
        r8 = 1;	 Catch:{ Exception -> 0x0169 }
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0169 }
        r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0169 }
        r17 = 0;	 Catch:{ Exception -> 0x0169 }
        r10[r17] = r8;	 Catch:{ Exception -> 0x0169 }
        r5 = java.lang.String.format(r5, r6, r10);	 Catch:{ Exception -> 0x0169 }
        r4 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0169 }
        r4 = r4.stepThis();	 Catch:{ Exception -> 0x0169 }
        r4.dispose();	 Catch:{ Exception -> 0x0169 }
        r4 = r1.database;	 Catch:{ Exception -> 0x0169 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0169 }
        r6 = "DELETE FROM messages_seq WHERE mid = %d";	 Catch:{ Exception -> 0x0169 }
        r8 = 1;	 Catch:{ Exception -> 0x0169 }
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0169 }
        r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0169 }
        r17 = 0;	 Catch:{ Exception -> 0x0169 }
        r10[r17] = r8;	 Catch:{ Exception -> 0x0169 }
        r5 = java.lang.String.format(r5, r6, r10);	 Catch:{ Exception -> 0x0169 }
        r4 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0169 }
        r4 = r4.stepThis();	 Catch:{ Exception -> 0x0169 }
        r4.dispose();	 Catch:{ Exception -> 0x0169 }
        goto L_0x016e;
    L_0x0166:
        r0 = move-exception;
        r3 = r2;
        goto L_0x0121;
    L_0x0169:
        r0 = move-exception;
        r4 = r0;
        org.telegram.messenger.FileLog.m3e(r4);	 Catch:{ all -> 0x0166 }
    L_0x016e:
        if (r2 == 0) goto L_0x0174;
    L_0x0170:
        r2.dispose();
        r2 = r3;
    L_0x0174:
        r4 = r1.database;	 Catch:{ Exception -> 0x0197 }
        r5 = "UPDATE media_v2 SET mid = ? WHERE mid = ?";	 Catch:{ Exception -> 0x0197 }
        r4 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0197 }
        r5 = 1;
        r4.bindLong(r5, r13);	 Catch:{ Exception -> 0x0190, all -> 0x018e }
        r4.bindLong(r7, r11);	 Catch:{ Exception -> 0x0190, all -> 0x018e }
        r4.step();	 Catch:{ Exception -> 0x0190, all -> 0x018e }
        if (r4 == 0) goto L_0x018c;
    L_0x0188:
        r4.dispose();
        goto L_0x01c4;
    L_0x018c:
        r3 = r4;
        goto L_0x01c4;
    L_0x018e:
        r0 = move-exception;
        goto L_0x0194;
    L_0x0190:
        r2 = r4;
        goto L_0x0197;
    L_0x0192:
        r0 = move-exception;
        r4 = r2;
    L_0x0194:
        r2 = r0;
        goto L_0x0203;
    L_0x0197:
        r4 = r1.database;	 Catch:{ Exception -> 0x01b8 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x01b8 }
        r6 = "DELETE FROM media_v2 WHERE mid = %d";	 Catch:{ Exception -> 0x01b8 }
        r8 = 1;	 Catch:{ Exception -> 0x01b8 }
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x01b8 }
        r8 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x01b8 }
        r17 = 0;	 Catch:{ Exception -> 0x01b8 }
        r10[r17] = r8;	 Catch:{ Exception -> 0x01b8 }
        r5 = java.lang.String.format(r5, r6, r10);	 Catch:{ Exception -> 0x01b8 }
        r4 = r4.executeFast(r5);	 Catch:{ Exception -> 0x01b8 }
        r4 = r4.stepThis();	 Catch:{ Exception -> 0x01b8 }
        r4.dispose();	 Catch:{ Exception -> 0x01b8 }
        goto L_0x01bd;
    L_0x01b8:
        r0 = move-exception;
        r4 = r0;
        org.telegram.messenger.FileLog.m3e(r4);	 Catch:{ all -> 0x0192 }
    L_0x01bd:
        if (r2 == 0) goto L_0x01c3;
    L_0x01bf:
        r2.dispose();
        goto L_0x01c4;
    L_0x01c3:
        r3 = r2;
    L_0x01c4:
        r2 = r1.database;	 Catch:{ Exception -> 0x01e5 }
        r4 = "UPDATE dialogs SET last_mid = ? WHERE last_mid = ?";	 Catch:{ Exception -> 0x01e5 }
        r2 = r2.executeFast(r4);	 Catch:{ Exception -> 0x01e5 }
        r4 = 1;
        r2.bindLong(r4, r13);	 Catch:{ Exception -> 0x01df, all -> 0x01dc }
        r2.bindLong(r7, r11);	 Catch:{ Exception -> 0x01df, all -> 0x01dc }
        r2.step();	 Catch:{ Exception -> 0x01df, all -> 0x01dc }
        if (r2 == 0) goto L_0x01ef;
    L_0x01d8:
        r2.dispose();
        goto L_0x01ef;
    L_0x01dc:
        r0 = move-exception;
        r3 = r2;
        goto L_0x01e3;
    L_0x01df:
        r0 = move-exception;
        r3 = r2;
        goto L_0x01e6;
    L_0x01e2:
        r0 = move-exception;
    L_0x01e3:
        r2 = r0;
        goto L_0x01fd;
    L_0x01e5:
        r0 = move-exception;
    L_0x01e6:
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x01e2 }
        if (r3 == 0) goto L_0x01ef;
    L_0x01ec:
        r3.dispose();
    L_0x01ef:
        r2 = new long[r7];
        r3 = 0;
        r2[r3] = r15;
        r3 = r9.intValue();
        r3 = (long) r3;
        r5 = 1;
        r2[r5] = r3;
        return r2;
    L_0x01fd:
        if (r3 == 0) goto L_0x0202;
    L_0x01ff:
        r3.dispose();
    L_0x0202:
        throw r2;
    L_0x0203:
        if (r4 == 0) goto L_0x0208;
    L_0x0205:
        r4.dispose();
    L_0x0208:
        throw r2;
    L_0x0209:
        if (r3 == 0) goto L_0x020e;
    L_0x020b:
        r3.dispose();
    L_0x020e:
        throw r2;
    L_0x020f:
        if (r10 == 0) goto L_0x0214;
    L_0x0211:
        r10.dispose();
    L_0x0214:
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateMessageStateAndIdInternal(long, java.lang.Integer, int, int, int):long[]");
    }

    public long[] updateMessageStateAndId(long j, Integer num, int i, int i2, boolean z, int i3) {
        if (!z) {
            return updateMessageStateAndIdInternal(j, num, i, i2, i3);
        }
        final long j2 = j;
        final Integer num2 = num;
        final int i4 = i;
        final int i5 = i2;
        final int i6 = i3;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesStorage.this.updateMessageStateAndIdInternal(j2, num2, i4, i5, i6);
            }
        });
        return null;
    }

    private void updateUsersInternal(ArrayList<User> arrayList, boolean z, boolean z2) {
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        } else if (z) {
            if (z2) {
                try {
                    this.database.beginTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    return;
                }
            }
            z = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
            arrayList = arrayList.iterator();
            while (arrayList.hasNext()) {
                User user = (User) arrayList.next();
                z.requery();
                if (user.status != null) {
                    z.bindInteger(1, user.status.expires);
                } else {
                    z.bindInteger(1, 0);
                }
                z.bindInteger(2, user.id);
                z.step();
            }
            z.dispose();
            if (z2) {
                this.database.commitTransaction();
            }
        } else {
            User user2;
            z = new StringBuilder();
            SparseArray sparseArray = new SparseArray();
            arrayList = arrayList.iterator();
            while (arrayList.hasNext()) {
                user2 = (User) arrayList.next();
                if (z.length() != 0) {
                    z.append(",");
                }
                z.append(user2.id);
                sparseArray.put(user2.id, user2);
            }
            arrayList = new ArrayList();
            getUsersInternal(z.toString(), arrayList);
            z = arrayList.iterator();
            while (z.hasNext()) {
                user2 = (User) z.next();
                User user3 = (User) sparseArray.get(user2.id);
                if (user3 != null) {
                    if (user3.first_name != null && user3.last_name != null) {
                        if (!UserObject.isContact(user2)) {
                            user2.first_name = user3.first_name;
                            user2.last_name = user3.last_name;
                        }
                        user2.username = user3.username;
                    } else if (user3.photo != null) {
                        user2.photo = user3.photo;
                    } else if (user3.phone != null) {
                        user2.phone = user3.phone;
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                if (z2) {
                    this.database.beginTransaction();
                }
                putUsersInternal(arrayList);
                if (z2) {
                    this.database.commitTransaction();
                }
            }
        }
    }

    public void updateUsers(final ArrayList<User> arrayList, final boolean z, final boolean z2, boolean z3) {
        if (!arrayList.isEmpty()) {
            if (z3) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.this.updateUsersInternal(arrayList, z, z2);
                    }
                });
            } else {
                updateUsersInternal(arrayList, z, z2);
            }
        }
    }

    private void markMessagesAsReadInternal(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        try {
            int i;
            int i2 = 0;
            if (!isEmpty(sparseLongArray)) {
                for (i = 0; i < sparseLongArray.size(); i++) {
                    long j = sparseLongArray.get(sparseLongArray.keyAt(i));
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(r4), Long.valueOf(j)})).stepThis().dispose();
                }
            }
            if (isEmpty(sparseLongArray2) == null) {
                for (sparseLongArray = null; sparseLongArray < sparseLongArray2.size(); sparseLongArray++) {
                    long j2 = sparseLongArray2.get(sparseLongArray2.keyAt(sparseLongArray));
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[]{Integer.valueOf(i), Long.valueOf(j2)})).stepThis().dispose();
                }
            }
            if (sparseIntArray != null && isEmpty(sparseIntArray) == null) {
                while (i2 < sparseIntArray.size()) {
                    sparseLongArray = ((long) sparseIntArray.keyAt(i2)) << 32;
                    i = sparseIntArray.valueAt(i2);
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                    executeFast.requery();
                    executeFast.bindLong(1, sparseLongArray);
                    executeFast.bindInteger(2, i);
                    executeFast.step();
                    executeFast.dispose();
                    i2++;
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void markMessagesContentAsRead(final ArrayList<Long> arrayList, final int i) {
        if (!isEmpty((List) arrayList)) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        String join = TextUtils.join(",", arrayList);
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[]{join})).stepThis().dispose();
                        if (i != 0) {
                            SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE mid IN (%s) AND ttl > 0", new Object[]{join}), new Object[0]);
                            ArrayList arrayList = null;
                            while (queryFinalized.next()) {
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                }
                                arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
                            }
                            if (arrayList != null) {
                                MessagesStorage.this.emptyMessagesMedia(arrayList);
                            }
                            queryFinalized.dispose();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public void markMessagesAsRead(final SparseLongArray sparseLongArray, final SparseLongArray sparseLongArray2, final SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    MessagesStorage.this.markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
                }
            });
        } else {
            markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(final ArrayList<Long> arrayList) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        String join = TextUtils.join(",", arrayList);
                        SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{join}), new Object[0]);
                        final ArrayList arrayList = new ArrayList();
                        while (queryFinalized.next()) {
                            arrayList.add(Integer.valueOf(queryFinalized.intValue(0)));
                        }
                        queryFinalized.dispose();
                        if (!arrayList.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesStorage.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(0));
                                }
                            });
                            MessagesStorage.this.updateDialogsWithReadMessagesInternal(arrayList, null, null, null);
                            MessagesStorage.this.markMessagesAsDeletedInternal(arrayList, 0);
                            MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(arrayList, null, 0);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(ArrayList<Integer> arrayList, int i) {
        Throwable e;
        MessagesStorage messagesStorage = this;
        ArrayList<Integer> arrayList2 = arrayList;
        int i2 = i;
        try {
            int i3;
            long intValue;
            String stringBuilder;
            Throwable th;
            ArrayList arrayList3;
            long keyAt;
            Integer[] numArr;
            SQLiteDatabase sQLiteDatabase;
            StringBuilder stringBuilder2;
            SQLiteCursor queryFinalized;
            int intValue2;
            int intValue3;
            SQLitePreparedStatement executeFast;
            int i4;
            ArrayList<Long> arrayList4 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            int i5 = 0;
            if (i2 != 0) {
                StringBuilder stringBuilder3 = new StringBuilder(arrayList.size());
                for (i3 = 0; i3 < arrayList.size(); i3++) {
                    intValue = ((long) ((Integer) arrayList2.get(i3)).intValue()) | (((long) i2) << 32);
                    if (stringBuilder3.length() > 0) {
                        stringBuilder3.append(',');
                    }
                    stringBuilder3.append(intValue);
                }
                stringBuilder = stringBuilder3.toString();
            } else {
                stringBuilder = TextUtils.join(",", arrayList2);
            }
            ArrayList arrayList5 = new ArrayList();
            i3 = UserConfig.getInstance(messagesStorage.currentAccount).getClientUserId();
            SQLiteCursor queryFinalized2 = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder}), new Object[0]);
            while (queryFinalized2.next()) {
                try {
                    intValue = queryFinalized2.longValue(i5);
                    ArrayList arrayList6 = arrayList5;
                    if (intValue != ((long) i3)) {
                        try {
                            i5 = queryFinalized2.intValue(2);
                            if (queryFinalized2.intValue(3) == 0) {
                                try {
                                    Integer[] numArr2 = (Integer[]) longSparseArray.get(intValue);
                                    if (numArr2 == null) {
                                        numArr2 = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                        longSparseArray.put(intValue, numArr2);
                                    }
                                    if (i5 < 2) {
                                        Integer num = numArr2[1];
                                        numArr2[1] = Integer.valueOf(numArr2[1].intValue() + 1);
                                    }
                                    if (i5 == 0 || i5 == 2) {
                                        Integer num2 = numArr2[0];
                                        numArr2[0] = Integer.valueOf(numArr2[0].intValue() + 1);
                                    }
                                } catch (Throwable e2) {
                                    th = e2;
                                    arrayList3 = arrayList6;
                                }
                            }
                            if (((int) intValue) == 0) {
                                AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(messagesStorage.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                            Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                            while (it.hasNext()) {
                                                File pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                                if (pathToAttach == null || pathToAttach.toString().length() <= 0) {
                                                    arrayList3 = arrayList6;
                                                } else {
                                                    arrayList3 = arrayList6;
                                                    try {
                                                        arrayList3.add(pathToAttach);
                                                    } catch (Exception e3) {
                                                        e2 = e3;
                                                    }
                                                }
                                                arrayList6 = arrayList3;
                                            }
                                        } else {
                                            arrayList3 = arrayList6;
                                            if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                                File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                                if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                    arrayList3.add(pathToAttach2);
                                                }
                                                pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                                if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                    arrayList3.add(pathToAttach2);
                                                }
                                            }
                                            arrayList5 = arrayList3;
                                            i5 = 0;
                                        }
                                    }
                                }
                                arrayList3 = arrayList6;
                                arrayList5 = arrayList3;
                                i5 = 0;
                            }
                        } catch (Exception e4) {
                            e2 = e4;
                            arrayList3 = arrayList6;
                        }
                    }
                    arrayList5 = arrayList6;
                    i5 = 0;
                } catch (Exception e5) {
                    e2 = e5;
                    arrayList3 = arrayList5;
                }
            }
            arrayList3 = arrayList5;
            queryFinalized2.dispose();
            FileLoader.getInstance(messagesStorage.currentAccount).deleteFiles(arrayList3, 0);
            i5 = 0;
            while (i5 < longSparseArray.size()) {
                keyAt = longSparseArray.keyAt(i5);
                numArr = (Integer[]) longSparseArray.valueAt(i5);
                sQLiteDatabase = messagesStorage.database;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder2.append(keyAt);
                queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder2.toString(), new Object[0]);
                if (queryFinalized.next()) {
                    intValue2 = queryFinalized.intValue(0);
                    intValue3 = queryFinalized.intValue(1);
                } else {
                    intValue2 = 0;
                    intValue3 = 0;
                }
                queryFinalized.dispose();
                arrayList4.add(Long.valueOf(keyAt));
                executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, intValue2 - numArr[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, intValue3 - numArr[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
                i5++;
                i4 = 3;
            }
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
            DataQuery.getInstance(messagesStorage.currentAccount).clearBotKeyboard(0, arrayList2);
            return arrayList4;
            th = e2;
            FileLog.m3e(th);
            queryFinalized2.dispose();
            FileLoader.getInstance(messagesStorage.currentAccount).deleteFiles(arrayList3, 0);
            i5 = 0;
            while (i5 < longSparseArray.size()) {
                keyAt = longSparseArray.keyAt(i5);
                numArr = (Integer[]) longSparseArray.valueAt(i5);
                sQLiteDatabase = messagesStorage.database;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder2.append(keyAt);
                queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder2.toString(), new Object[0]);
                if (queryFinalized.next()) {
                    intValue2 = 0;
                    intValue3 = 0;
                } else {
                    intValue2 = queryFinalized.intValue(0);
                    intValue3 = queryFinalized.intValue(1);
                }
                queryFinalized.dispose();
                arrayList4.add(Long.valueOf(keyAt));
                executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, intValue2 - numArr[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, intValue3 - numArr[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
                i5++;
                i4 = 3;
            }
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
            DataQuery.getInstance(messagesStorage.currentAccount).clearBotKeyboard(0, arrayList2);
            return arrayList4;
            FileLog.m3e(th);
            queryFinalized2.dispose();
            FileLoader.getInstance(messagesStorage.currentAccount).deleteFiles(arrayList3, 0);
            i5 = 0;
            while (i5 < longSparseArray.size()) {
                keyAt = longSparseArray.keyAt(i5);
                numArr = (Integer[]) longSparseArray.valueAt(i5);
                sQLiteDatabase = messagesStorage.database;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder2.append(keyAt);
                queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder2.toString(), new Object[0]);
                if (queryFinalized.next()) {
                    intValue2 = queryFinalized.intValue(0);
                    intValue3 = queryFinalized.intValue(1);
                } else {
                    intValue2 = 0;
                    intValue3 = 0;
                }
                queryFinalized.dispose();
                arrayList4.add(Long.valueOf(keyAt));
                executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, intValue2 - numArr[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, intValue3 - numArr[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
                i5++;
                i4 = 3;
            }
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{stringBuilder})).stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
            DataQuery.getInstance(messagesStorage.currentAccount).clearBotKeyboard(0, arrayList2);
            return arrayList4;
        } catch (Throwable e22) {
            FileLog.m3e(e22);
            return null;
        }
    }

    private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, int i) {
        ArrayList<Long> arrayList3 = arrayList2;
        int i2 = i;
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        }
        try {
            Iterable arrayList4 = new ArrayList();
            int i3 = 2;
            if (arrayList.isEmpty()) {
                arrayList4.add(Long.valueOf((long) (-i2)));
            } else {
                SQLitePreparedStatement executeFast;
                if (i2 != 0) {
                    arrayList4.add(Long.valueOf((long) (-i2)));
                    executeFast = r1.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?");
                } else {
                    String join = TextUtils.join(",", arrayList);
                    SQLiteCursor queryFinalized = r1.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[]{join}), new Object[0]);
                    while (queryFinalized.next()) {
                        arrayList4.add(Long.valueOf(queryFinalized.longValue(0)));
                    }
                    queryFinalized.dispose();
                    executeFast = r1.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                r1.database.beginTransaction();
                for (int i4 = 0; i4 < arrayList4.size(); i4++) {
                    long longValue = ((Long) arrayList4.get(i4)).longValue();
                    executeFast.requery();
                    executeFast.bindLong(1, longValue);
                    executeFast.bindLong(2, longValue);
                    executeFast.bindLong(3, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
                r1.database.commitTransaction();
            }
            if (arrayList3 != null) {
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    Long l = (Long) arrayList3.get(i5);
                    if (!arrayList4.contains(l)) {
                        arrayList4.add(l);
                    }
                }
            }
            String join2 = TextUtils.join(",", arrayList4);
            messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
            ArrayList arrayList5 = new ArrayList();
            Iterable arrayList6 = new ArrayList();
            Iterable arrayList7 = new ArrayList();
            Iterable arrayList8 = new ArrayList();
            SQLiteCursor queryFinalized2 = r1.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[]{join2}), new Object[0]);
            while (queryFinalized2.next()) {
                TL_dialog tL_dialog = new TL_dialog();
                tL_dialog.id = queryFinalized2.longValue(0);
                tL_dialog.top_message = queryFinalized2.intValue(1);
                tL_dialog.read_inbox_max_id = queryFinalized2.intValue(10);
                tL_dialog.read_outbox_max_id = queryFinalized2.intValue(11);
                tL_dialog.unread_count = queryFinalized2.intValue(i3);
                tL_dialog.unread_mentions_count = queryFinalized2.intValue(13);
                tL_dialog.last_message_date = queryFinalized2.intValue(3);
                tL_dialog.pts = queryFinalized2.intValue(9);
                tL_dialog.flags = i2 == 0 ? 0 : 1;
                tL_dialog.pinnedNum = queryFinalized2.intValue(12);
                tL_dialog.pinned = tL_dialog.pinnedNum != 0;
                tL_messages_dialogs.dialogs.add(tL_dialog);
                AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(4);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(r1.currentAccount).clientUserId);
                    byteBufferValue.reuse();
                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(5));
                    TLdeserialize.id = queryFinalized2.intValue(6);
                    TLdeserialize.send_state = queryFinalized2.intValue(7);
                    i3 = queryFinalized2.intValue(8);
                    if (i3 != 0) {
                        tL_dialog.last_message_date = i3;
                    }
                    TLdeserialize.dialog_id = tL_dialog.id;
                    tL_messages_dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                }
                i3 = (int) tL_dialog.id;
                int i6 = (int) (tL_dialog.id >> 32);
                if (i3 != 0) {
                    if (i6 == 1) {
                        if (!arrayList7.contains(Integer.valueOf(i3))) {
                            arrayList7.add(Integer.valueOf(i3));
                        }
                    } else if (i3 <= 0) {
                        i6 = -i3;
                        if (!arrayList7.contains(Integer.valueOf(i6))) {
                            arrayList7.add(Integer.valueOf(i6));
                        }
                    } else if (!arrayList6.contains(Integer.valueOf(i3))) {
                        arrayList6.add(Integer.valueOf(i3));
                    }
                } else if (!arrayList8.contains(Integer.valueOf(i6))) {
                    arrayList8.add(Integer.valueOf(i6));
                }
                i3 = 2;
            }
            queryFinalized2.dispose();
            if (!arrayList8.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", arrayList8), arrayList5, arrayList6);
            }
            if (!arrayList7.isEmpty()) {
                getChatsInternal(TextUtils.join(",", arrayList7), tL_messages_dialogs.chats);
            }
            if (!arrayList6.isEmpty()) {
                getUsersInternal(TextUtils.join(",", arrayList6), tL_messages_dialogs.users);
            }
            if (!tL_messages_dialogs.dialogs.isEmpty() || !arrayList5.isEmpty()) {
                MessagesController.getInstance(r1.currentAccount).processDialogsUpdate(tL_messages_dialogs, arrayList5);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void updateDialogsWithDeletedMessages(final ArrayList<Integer> arrayList, final ArrayList<Long> arrayList2, boolean z, final int i) {
        if (!arrayList.isEmpty() || i != 0) {
            if (z) {
                this.storageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
                    }
                });
            } else {
                updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
            }
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(final ArrayList<Integer> arrayList, boolean z, final int i) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (!z) {
            return markMessagesAsDeletedInternal((ArrayList) arrayList, i);
        }
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesStorage.this.markMessagesAsDeletedInternal(arrayList, i);
            }
        });
        return null;
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(int i, int i2) {
        MessagesStorage messagesStorage = this;
        int i3 = i;
        Throwable e;
        try {
            long j;
            int i4;
            long keyAt;
            Integer[] numArr;
            SQLiteDatabase sQLiteDatabase;
            StringBuilder stringBuilder;
            SQLiteCursor queryFinalized;
            int i5;
            int i6;
            SQLitePreparedStatement executeFast;
            ArrayList<Long> arrayList = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            long j2 = ((long) i2) | (((long) i3) << 32);
            ArrayList arrayList2 = new ArrayList();
            int clientUserId = UserConfig.getInstance(messagesStorage.currentAccount).getClientUserId();
            SQLiteDatabase sQLiteDatabase2 = messagesStorage.database;
            r13 = new Object[2];
            i3 = -i3;
            r13[0] = Integer.valueOf(i3);
            r13[1] = Long.valueOf(j2);
            SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE uid = %d AND mid <= %d", r13), new Object[0]);
            while (queryFinalized2.next()) {
                try {
                    long longValue = queryFinalized2.longValue(0);
                    j = j2;
                    if (longValue != ((long) clientUserId)) {
                        int i7 = 2;
                        try {
                            int intValue = queryFinalized2.intValue(2);
                            if (queryFinalized2.intValue(3) == 0) {
                                Integer num;
                                Integer[] numArr2 = (Integer[]) longSparseArray.get(longValue);
                                if (numArr2 == null) {
                                    numArr2 = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                    longSparseArray.put(longValue, numArr2);
                                    i7 = 2;
                                }
                                if (intValue < i7) {
                                    num = numArr2[1];
                                    numArr2[1] = Integer.valueOf(numArr2[1].intValue() + 1);
                                }
                                if (intValue == 0 || intValue == 2) {
                                    num = numArr2[0];
                                    numArr2[0] = Integer.valueOf(numArr2[0].intValue() + 1);
                                }
                            }
                            if (((int) longValue) == 0) {
                                AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(messagesStorage.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                            Iterator it = TLdeserialize.media.photo.sizes.iterator();
                                            while (it.hasNext()) {
                                                File pathToAttach = FileLoader.getPathToAttach((PhotoSize) it.next());
                                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                    arrayList2.add(pathToAttach);
                                                }
                                            }
                                        } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                            File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                arrayList2.add(pathToAttach2);
                                            }
                                            pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document.thumb);
                                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                                arrayList2.add(pathToAttach2);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                        }
                    }
                    j2 = j;
                } catch (Exception e3) {
                    e = e3;
                    j = j2;
                }
            }
            j = j2;
            queryFinalized2.dispose();
            FileLoader.getInstance(messagesStorage.currentAccount).deleteFiles(arrayList2, 0);
            for (i4 = 0; i4 < longSparseArray.size(); i4++) {
                keyAt = longSparseArray.keyAt(i4);
                numArr = (Integer[]) longSparseArray.valueAt(i4);
                sQLiteDatabase = messagesStorage.database;
                stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder.append(keyAt);
                queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                if (queryFinalized.next()) {
                    i5 = 0;
                    i6 = i5;
                } else {
                    i5 = queryFinalized.intValue(0);
                    i6 = queryFinalized.intValue(1);
                }
                queryFinalized.dispose();
                arrayList.add(Long.valueOf(keyAt));
                executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, i5 - numArr[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, i6 - numArr[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
            }
            SQLiteDatabase sQLiteDatabase3 = messagesStorage.database;
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(i3);
            objArr[1] = Long.valueOf(j);
            sQLiteDatabase3.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", objArr)).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(i3), Long.valueOf(j2)})).stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
            return arrayList;
            FileLog.m3e(e);
            queryFinalized2.dispose();
            FileLoader.getInstance(messagesStorage.currentAccount).deleteFiles(arrayList2, 0);
            for (i4 = 0; i4 < longSparseArray.size(); i4++) {
                keyAt = longSparseArray.keyAt(i4);
                numArr = (Integer[]) longSparseArray.valueAt(i4);
                sQLiteDatabase = messagesStorage.database;
                stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder.append(keyAt);
                queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                if (queryFinalized.next()) {
                    i5 = 0;
                    i6 = i5;
                } else {
                    i5 = queryFinalized.intValue(0);
                    i6 = queryFinalized.intValue(1);
                }
                queryFinalized.dispose();
                arrayList.add(Long.valueOf(keyAt));
                executeFast = messagesStorage.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, i5 - numArr[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, i6 - numArr[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
            }
            SQLiteDatabase sQLiteDatabase32 = messagesStorage.database;
            Object[] objArr2 = new Object[2];
            objArr2[0] = Integer.valueOf(i3);
            objArr2[1] = Long.valueOf(j);
            sQLiteDatabase32.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", objArr2)).stepThis().dispose();
            messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(i3), Long.valueOf(j2)})).stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
            return arrayList;
        } catch (Throwable e4) {
            FileLog.m3e(e4);
            return null;
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(final int i, final int i2, boolean z) {
        if (!z) {
            return markMessagesAsDeletedInternal(i, i2);
        }
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesStorage.this.markMessagesAsDeletedInternal(i, i2);
            }
        });
        return 0;
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            if (message.media instanceof TL_messageMediaUnsupported_old) {
                if (message.media.bytes.length == 0) {
                    message.media.bytes = new byte[1];
                    message.media.bytes[0] = 76;
                }
            } else if (message.media instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                message.media.bytes = new byte[1];
                message.media.bytes[0] = (byte) 76;
                message.flags |= 512;
            }
        }
    }

    private void doneHolesInTable(String str, long j, int i) throws Exception {
        Locale locale;
        StringBuilder stringBuilder;
        if (i == 0) {
            i = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM ");
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d");
            i.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } else {
            i = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM ");
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d AND start = 0");
            i.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        }
        i = this.database;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("REPLACE INTO ");
        stringBuilder2.append(str);
        stringBuilder2.append(" VALUES(?, ?, ?)");
        str = i.executeFast(stringBuilder2.toString());
        str.requery();
        str.bindLong(1, j);
        str.bindInteger(2, 1);
        str.bindInteger(3, 1);
        str.step();
        str.dispose();
    }

    public void doneHolesInMedia(long j, int i, int i2) throws Exception {
        int i3 = 0;
        if (i2 == -1) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            }
            i = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            while (i3 < 5) {
                i.requery();
                i.bindLong(1, j);
                i.bindInteger(2, i3);
                i.bindInteger(3, 1);
                i.bindInteger(4, 1);
                i.step();
                i3++;
            }
            i.dispose();
            return;
        }
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        }
        i = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        i.requery();
        i.bindLong(1, j);
        i.bindInteger(2, i2);
        i.bindInteger(3, 1);
        i.bindInteger(4, 1);
        i.step();
        i.dispose();
    }

    public void closeHolesInMedia(long j, int i, int i2, int i3) throws Exception {
        SQLiteCursor queryFinalized;
        MessagesStorage messagesStorage = this;
        long j2 = j;
        int i4 = i;
        int i5 = i2;
        int i6 = 4;
        if (i3 < 0) {
            queryFinalized = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        } else {
            queryFinalized = messagesStorage.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        }
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            int intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            int intValue3 = queryFinalized.intValue(2);
            if (intValue2 != intValue3 || intValue2 != 1) {
                arrayList.add(new Hole(intValue, intValue2, intValue3));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            int i7 = 0;
            while (i7 < arrayList.size()) {
                int i8;
                int i9;
                Hole hole = (Hole) arrayList.get(i7);
                if (i5 >= hole.end - 1 && i4 <= hole.start + 1) {
                    SQLiteDatabase sQLiteDatabase = messagesStorage.database;
                    Object[] objArr = new Object[i6];
                    objArr[0] = Long.valueOf(j);
                    objArr[1] = Integer.valueOf(hole.type);
                    objArr[2] = Integer.valueOf(hole.start);
                    objArr[3] = Integer.valueOf(hole.end);
                    sQLiteDatabase.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", objArr)).stepThis().dispose();
                } else if (i5 >= hole.end - 1) {
                    if (hole.end != i4) {
                        try {
                            messagesStorage.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Throwable e) {
                            try {
                                FileLog.m3e(e);
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                                return;
                            }
                        }
                    }
                } else if (i4 > hole.start + 1) {
                    messagesStorage.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    SQLitePreparedStatement executeFast = messagesStorage.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, hole.start);
                    executeFast.bindInteger(4, i4);
                    executeFast.step();
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    i6 = 2;
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, i5);
                    i8 = 4;
                    executeFast.bindInteger(4, hole.end);
                    executeFast.step();
                    executeFast.dispose();
                    i7++;
                    i9 = i6;
                    i6 = i8;
                } else if (hole.start != i5) {
                    try {
                        messagesStorage.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
                i6 = 2;
                i8 = 4;
                i7++;
                i9 = i6;
                i6 = i8;
            }
        }
    }

    private void closeHolesInTable(String str, long j, int i, int i2) throws Exception {
        String str2 = str;
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        SQLiteDatabase sQLiteDatabase = this.database;
        Locale locale = Locale.US;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT start, end FROM ");
        stringBuilder.append(str2);
        stringBuilder.append(" WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))");
        String stringBuilder2 = stringBuilder.toString();
        r10 = new Object[9];
        int i5 = 1;
        r10[1] = Integer.valueOf(i);
        r10[2] = Integer.valueOf(i2);
        int i6 = 3;
        r10[3] = Integer.valueOf(i);
        r10[4] = Integer.valueOf(i2);
        r10[5] = Integer.valueOf(i);
        r10[6] = Integer.valueOf(i2);
        r10[7] = Integer.valueOf(i);
        r10[8] = Integer.valueOf(i2);
        SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, stringBuilder2, r10), new Object[0]);
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            int intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            if (intValue != intValue2 || intValue != 1) {
                arrayList.add(new Hole(intValue, intValue2));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            int i7 = 0;
            while (i7 < arrayList.size()) {
                int i8;
                int i9;
                Hole hole = (Hole) arrayList.get(i7);
                SQLiteDatabase sQLiteDatabase2;
                Locale locale2;
                if (i4 >= hole.end - i5 && i3 <= hole.start + i5) {
                    sQLiteDatabase2 = r1.database;
                    locale2 = Locale.US;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM ");
                    stringBuilder3.append(str2);
                    stringBuilder3.append(" WHERE uid = %d AND start = %d AND end = %d");
                    String stringBuilder4 = stringBuilder3.toString();
                    Object[] objArr = new Object[i6];
                    objArr[0] = Long.valueOf(j);
                    objArr[1] = Integer.valueOf(hole.start);
                    objArr[2] = Integer.valueOf(hole.end);
                    sQLiteDatabase2.executeFast(String.format(locale2, stringBuilder4, objArr)).stepThis().dispose();
                } else if (i4 >= hole.end - 1) {
                    if (hole.end != i3) {
                        try {
                            sQLiteDatabase2 = r1.database;
                            locale2 = Locale.US;
                            r13 = new StringBuilder();
                            r13.append("UPDATE ");
                            r13.append(str2);
                            r13.append(" SET end = %d WHERE uid = %d AND start = %d AND end = %d");
                            sQLiteDatabase2.executeFast(String.format(locale2, r13.toString(), new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Throwable e) {
                            try {
                                FileLog.m3e(e);
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                                return;
                            }
                        }
                    }
                } else if (i3 > hole.start + 1) {
                    sQLiteDatabase2 = r1.database;
                    locale2 = Locale.US;
                    r13 = new StringBuilder();
                    r13.append("DELETE FROM ");
                    r13.append(str2);
                    r13.append(" WHERE uid = %d AND start = %d AND end = %d");
                    sQLiteDatabase2.executeFast(String.format(locale2, r13.toString(), new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    sQLiteDatabase2 = r1.database;
                    StringBuilder stringBuilder5 = new StringBuilder();
                    stringBuilder5.append("REPLACE INTO ");
                    stringBuilder5.append(str2);
                    stringBuilder5.append(" VALUES(?, ?, ?)");
                    SQLitePreparedStatement executeFast = sQLiteDatabase2.executeFast(stringBuilder5.toString());
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.start);
                    executeFast.bindInteger(3, i3);
                    executeFast.step();
                    executeFast.requery();
                    i8 = 1;
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, i4);
                    i9 = 3;
                    executeFast.bindInteger(3, hole.end);
                    executeFast.step();
                    executeFast.dispose();
                    i7++;
                    i5 = i8;
                    i6 = i9;
                } else if (hole.start != i4) {
                    try {
                        sQLiteDatabase2 = r1.database;
                        locale2 = Locale.US;
                        r13 = new StringBuilder();
                        r13.append("UPDATE ");
                        r13.append(str2);
                        r13.append(" SET start = %d WHERE uid = %d AND start = %d AND end = %d");
                        sQLiteDatabase2.executeFast(String.format(locale2, r13.toString(), new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                }
                i8 = 1;
                i9 = 3;
                i7++;
                i5 = i8;
                i6 = i9;
            }
        }
    }

    public void putMessages(messages_Messages messages_messages, long j, int i, int i2, boolean z) {
        final messages_Messages messages_messages2 = messages_messages;
        final int i3 = i;
        final long j2 = j;
        final int i4 = i2;
        final boolean z2 = z;
        this.storageQueue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                try {
                    if (messages_messages2.messages.isEmpty()) {
                        if (i3 == 0) {
                            MessagesStorage.this.doneHolesInTable("messages_holes", j2, i4);
                            MessagesStorage.this.doneHolesInMedia(j2, i4, -1);
                        }
                        return;
                    }
                    int i;
                    int i2;
                    int i3;
                    SQLitePreparedStatement sQLitePreparedStatement;
                    SQLitePreparedStatement sQLitePreparedStatement2;
                    Message message;
                    MessagesStorage.this.database.beginTransaction();
                    int i4 = 1;
                    if (i3 == 0) {
                        i = ((Message) messages_messages2.messages.get(messages_messages2.messages.size() - 1)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", j2, i, i4);
                        MessagesStorage.this.closeHolesInMedia(j2, i, i4, -1);
                    } else if (i3 == 1) {
                        i = ((Message) messages_messages2.messages.get(0)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", j2, i4, i);
                        MessagesStorage.this.closeHolesInMedia(j2, i4, i, -1);
                    } else if (i3 == 3 || i3 == 2 || i3 == 4) {
                        i = (i4 != 0 || i3 == 4) ? ((Message) messages_messages2.messages.get(0)).id : ConnectionsManager.DEFAULT_DATACENTER_ID;
                        i2 = ((Message) messages_messages2.messages.get(messages_messages2.messages.size() - 1)).id;
                        MessagesStorage.this.closeHolesInTable("messages_holes", j2, i2, i);
                        MessagesStorage.this.closeHolesInMedia(j2, i2, i, -1);
                    }
                    i = messages_messages2.messages.size();
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                    SQLitePreparedStatement executeFast2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    int i5 = 0;
                    i2 = i5;
                    int i6 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    SQLitePreparedStatement sQLitePreparedStatement3 = null;
                    Message message2 = null;
                    while (i5 < i) {
                        SQLitePreparedStatement sQLitePreparedStatement4;
                        int i7;
                        SQLiteCursor queryFinalized;
                        int intValue;
                        int i8;
                        Message message3;
                        SQLitePreparedStatement sQLitePreparedStatement5;
                        int i9;
                        int i10;
                        int i11;
                        SQLitePreparedStatement sQLitePreparedStatement6;
                        Message message4 = (Message) messages_messages2.messages.get(i5);
                        long j = (long) message4.id;
                        if (i2 == 0) {
                            i2 = message4.to_id.channel_id;
                        }
                        if (message4.to_id.channel_id != 0) {
                            sQLitePreparedStatement4 = executeFast;
                            j |= ((long) i2) << 32;
                        } else {
                            sQLitePreparedStatement4 = executeFast;
                        }
                        if (i3 == -2) {
                            SQLiteDatabase access$000 = MessagesStorage.this.database;
                            i7 = i;
                            Object[] objArr = new Object[i4];
                            objArr[0] = Long.valueOf(j);
                            queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl, mention, read_state FROM messages WHERE mid = %d", objArr), new Object[0]);
                            boolean next = queryFinalized.next();
                            if (next) {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(1);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    if (TLdeserialize != null) {
                                        message4.attachPath = TLdeserialize.attachPath;
                                        message4.ttl = queryFinalized.intValue(2);
                                    }
                                }
                                boolean z = queryFinalized.intValue(3) != 0;
                                intValue = queryFinalized.intValue(4);
                                if (z != message4.mentioned) {
                                    if (i6 == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        SQLiteDatabase access$0002 = MessagesStorage.this.database;
                                        i8 = i2;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        i3 = i6;
                                        stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
                                        sQLitePreparedStatement = sQLitePreparedStatement3;
                                        message3 = message2;
                                        stringBuilder.append(j2);
                                        SQLiteCursor queryFinalized2 = access$0002.queryFinalized(stringBuilder.toString(), new Object[0]);
                                        i6 = queryFinalized2.next() ? queryFinalized2.intValue(0) : i3;
                                        queryFinalized2.dispose();
                                    } else {
                                        i3 = i6;
                                        sQLitePreparedStatement = sQLitePreparedStatement3;
                                        message3 = message2;
                                        i8 = i2;
                                    }
                                    if (z) {
                                        if (intValue <= 1) {
                                            i6--;
                                        }
                                    } else if (message4.media_unread) {
                                        i6++;
                                    }
                                    queryFinalized.dispose();
                                    if (!next) {
                                        sQLitePreparedStatement2 = executeFast2;
                                        sQLitePreparedStatement5 = sQLitePreparedStatement4;
                                        sQLitePreparedStatement3 = sQLitePreparedStatement;
                                        message2 = message3;
                                        intValue = 3;
                                        i9 = 4;
                                        i5++;
                                        i10 = intValue;
                                        i11 = i9;
                                        i2 = i8;
                                        executeFast = sQLitePreparedStatement5;
                                        executeFast2 = sQLitePreparedStatement2;
                                        i = i7;
                                        i4 = 1;
                                    }
                                }
                            }
                            sQLitePreparedStatement = sQLitePreparedStatement3;
                            message3 = message2;
                            i8 = i2;
                            i6 = i6;
                            queryFinalized.dispose();
                            if (next) {
                                sQLitePreparedStatement2 = executeFast2;
                                sQLitePreparedStatement5 = sQLitePreparedStatement4;
                                sQLitePreparedStatement3 = sQLitePreparedStatement;
                                message2 = message3;
                                intValue = 3;
                                i9 = 4;
                                i5++;
                                i10 = intValue;
                                i11 = i9;
                                i2 = i8;
                                executeFast = sQLitePreparedStatement5;
                                executeFast2 = sQLitePreparedStatement2;
                                i = i7;
                                i4 = 1;
                            }
                        } else {
                            i7 = i;
                            i3 = i6;
                            sQLitePreparedStatement = sQLitePreparedStatement3;
                            message3 = message2;
                            i8 = i2;
                        }
                        if (i5 == 0 && z2) {
                            int intValue2;
                            SQLiteDatabase access$0003 = MessagesStorage.this.database;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("SELECT pinned, unread_count_i FROM dialogs WHERE did = ");
                            stringBuilder2.append(j2);
                            queryFinalized = access$0003.queryFinalized(stringBuilder2.toString(), new Object[0]);
                            if (queryFinalized.next()) {
                                intValue2 = queryFinalized.intValue(0);
                                i4 = queryFinalized.intValue(1);
                            } else {
                                i4 = 0;
                                intValue2 = 0;
                            }
                            queryFinalized.dispose();
                            sQLitePreparedStatement5 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            sQLitePreparedStatement6 = executeFast2;
                            sQLitePreparedStatement5.bindLong(1, j2);
                            sQLitePreparedStatement5.bindInteger(2, message4.date);
                            sQLitePreparedStatement5.bindInteger(3, 0);
                            sQLitePreparedStatement5.bindLong(4, j);
                            sQLitePreparedStatement5.bindInteger(5, message4.id);
                            sQLitePreparedStatement5.bindInteger(6, 0);
                            sQLitePreparedStatement5.bindLong(7, j);
                            sQLitePreparedStatement5.bindInteger(8, i4);
                            sQLitePreparedStatement5.bindInteger(9, messages_messages2.pts);
                            sQLitePreparedStatement5.bindInteger(10, message4.date);
                            sQLitePreparedStatement5.bindInteger(11, intValue2);
                            sQLitePreparedStatement5.step();
                            sQLitePreparedStatement5.dispose();
                        } else {
                            sQLitePreparedStatement6 = executeFast2;
                        }
                        MessagesStorage.this.fixUnsupportedMedia(message4);
                        sQLitePreparedStatement5 = sQLitePreparedStatement4;
                        sQLitePreparedStatement5.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message4.getObjectSize());
                        message4.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatement5.bindLong(1, j);
                        sQLitePreparedStatement5.bindLong(2, j2);
                        sQLitePreparedStatement5.bindInteger(3, MessageObject.getUnreadFlags(message4));
                        sQLitePreparedStatement5.bindInteger(4, message4.send_state);
                        sQLitePreparedStatement5.bindInteger(5, message4.date);
                        sQLitePreparedStatement5.bindByteBuffer(6, nativeByteBuffer);
                        sQLitePreparedStatement5.bindInteger(7, MessageObject.isOut(message4));
                        sQLitePreparedStatement5.bindInteger(8, message4.ttl);
                        if ((message4.flags & 1024) != 0) {
                            sQLitePreparedStatement5.bindInteger(9, message4.views);
                        } else {
                            sQLitePreparedStatement5.bindInteger(9, MessagesStorage.this.getMessageMediaType(message4));
                        }
                        sQLitePreparedStatement5.bindInteger(10, 0);
                        sQLitePreparedStatement5.bindInteger(11, message4.mentioned);
                        sQLitePreparedStatement5.step();
                        if (DataQuery.canAddMessageToMedia(message4)) {
                            sQLitePreparedStatement2 = sQLitePreparedStatement6;
                            sQLitePreparedStatement2.requery();
                            sQLitePreparedStatement2.bindLong(1, j);
                            sQLitePreparedStatement2.bindLong(2, j2);
                            intValue = 3;
                            sQLitePreparedStatement2.bindInteger(3, message4.date);
                            i9 = 4;
                            sQLitePreparedStatement2.bindInteger(4, DataQuery.getMediaType(message4));
                            sQLitePreparedStatement2.bindByteBuffer(5, nativeByteBuffer);
                            sQLitePreparedStatement2.step();
                        } else {
                            sQLitePreparedStatement2 = sQLitePreparedStatement6;
                            intValue = 3;
                            i9 = 4;
                        }
                        nativeByteBuffer.reuse();
                        if (message4.media instanceof TL_messageMediaWebPage) {
                            sQLitePreparedStatement3 = sQLitePreparedStatement == null ? MessagesStorage.this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)") : sQLitePreparedStatement;
                            sQLitePreparedStatement3.requery();
                            sQLitePreparedStatement3.bindLong(1, message4.media.webpage.id);
                            sQLitePreparedStatement3.bindLong(2, j);
                            sQLitePreparedStatement3.step();
                            sQLitePreparedStatement = sQLitePreparedStatement3;
                        }
                        if (i3 == 0 && MessagesStorage.this.isValidKeyboardToSave(message4)) {
                            if (message3 != null) {
                                message = message3;
                            }
                            message2 = message4;
                            sQLitePreparedStatement3 = sQLitePreparedStatement;
                            i5++;
                            i10 = intValue;
                            i11 = i9;
                            i2 = i8;
                            executeFast = sQLitePreparedStatement5;
                            executeFast2 = sQLitePreparedStatement2;
                            i = i7;
                            i4 = 1;
                        } else {
                            message = message3;
                        }
                        message2 = message;
                        sQLitePreparedStatement3 = sQLitePreparedStatement;
                        i5++;
                        i10 = intValue;
                        i11 = i9;
                        i2 = i8;
                        executeFast = sQLitePreparedStatement5;
                        executeFast2 = sQLitePreparedStatement2;
                        i = i7;
                        i4 = 1;
                    }
                    sQLitePreparedStatement2 = executeFast2;
                    i3 = i6;
                    sQLitePreparedStatement = sQLitePreparedStatement3;
                    message = message2;
                    executeFast.dispose();
                    sQLitePreparedStatement2.dispose();
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                    if (message != null) {
                        DataQuery.getInstance(MessagesStorage.this.currentAccount).putBotKeyboard(j2, message);
                    }
                    MessagesStorage.this.putUsersInternal(messages_messages2.users);
                    MessagesStorage.this.putChatsInternal(messages_messages2.chats);
                    int i12 = i3;
                    if (i12 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i12), Long.valueOf(j2)})).stepThis().dispose();
                        LongSparseArray longSparseArray = new LongSparseArray(1);
                        longSparseArray.put(j2, Integer.valueOf(i12));
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
                    }
                    MessagesStorage.this.database.commitTransaction();
                    if (z2) {
                        MessagesStorage.this.updateDialogsWithDeletedMessages(new ArrayList(), null, false, i2);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public static void addUsersAndChatsFromMessage(Message message, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
        if (message.from_id != 0) {
            if (message.from_id > 0) {
                if (!arrayList.contains(Integer.valueOf(message.from_id))) {
                    arrayList.add(Integer.valueOf(message.from_id));
                }
            } else if (!arrayList2.contains(Integer.valueOf(-message.from_id))) {
                arrayList2.add(Integer.valueOf(-message.from_id));
            }
        }
        if (!(message.via_bot_id == 0 || arrayList.contains(Integer.valueOf(message.via_bot_id)))) {
            arrayList.add(Integer.valueOf(message.via_bot_id));
        }
        int i = 0;
        if (message.action != null) {
            if (!(message.action.user_id == 0 || arrayList.contains(Integer.valueOf(message.action.user_id)))) {
                arrayList.add(Integer.valueOf(message.action.user_id));
            }
            if (!(message.action.channel_id == 0 || arrayList2.contains(Integer.valueOf(message.action.channel_id)))) {
                arrayList2.add(Integer.valueOf(message.action.channel_id));
            }
            if (!(message.action.chat_id == 0 || arrayList2.contains(Integer.valueOf(message.action.chat_id)))) {
                arrayList2.add(Integer.valueOf(message.action.chat_id));
            }
            if (!message.action.users.isEmpty()) {
                for (int i2 = 0; i2 < message.action.users.size(); i2++) {
                    Integer num = (Integer) message.action.users.get(i2);
                    if (!arrayList.contains(num)) {
                        arrayList.add(num);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            while (i < message.entities.size()) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                if (messageEntity instanceof TL_messageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_messageEntityMentionName) messageEntity).user_id));
                } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id));
                }
                i++;
            }
        }
        if (!(message.media == null || message.media.user_id == 0 || arrayList.contains(Integer.valueOf(message.media.user_id)))) {
            arrayList.add(Integer.valueOf(message.media.user_id));
        }
        if (message.fwd_from != null) {
            if (!(message.fwd_from.from_id == 0 || arrayList.contains(Integer.valueOf(message.fwd_from.from_id)))) {
                arrayList.add(Integer.valueOf(message.fwd_from.from_id));
            }
            if (!(message.fwd_from.channel_id == 0 || arrayList2.contains(Integer.valueOf(message.fwd_from.channel_id)))) {
                arrayList2.add(Integer.valueOf(message.fwd_from.channel_id));
            }
            if (message.fwd_from.saved_from_peer != null) {
                if (message.fwd_from.saved_from_peer.user_id != 0) {
                    if (!arrayList2.contains(Integer.valueOf(message.fwd_from.saved_from_peer.user_id))) {
                        arrayList.add(Integer.valueOf(message.fwd_from.saved_from_peer.user_id));
                    }
                } else if (message.fwd_from.saved_from_peer.channel_id != null) {
                    if (arrayList2.contains(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id)) == null) {
                        arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id));
                    }
                } else if (message.fwd_from.saved_from_peer.chat_id != null && arrayList2.contains(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id)) == null) {
                    arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id));
                }
            }
        }
        if (message.ttl < null && arrayList2.contains(Integer.valueOf(-message.ttl)) == null) {
            arrayList2.add(Integer.valueOf(-message.ttl));
        }
    }

    public void getDialogs(final int i, final int i2) {
        this.storageQueue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                Throwable th;
                Throwable e;
                messages_Dialogs messages_dialogs;
                ArrayList arrayList;
                int i;
                Throwable th2;
                ArrayList arrayList2;
                messages_Dialogs messages_dialogs2;
                AnonymousClass90 anonymousClass90 = this;
                messages_Dialogs tL_messages_dialogs = new TL_messages_dialogs();
                ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4;
                try {
                    Iterable arrayList5 = new ArrayList();
                    arrayList5.add(Integer.valueOf(UserConfig.getInstance(MessagesStorage.this.currentAccount).getClientUserId()));
                    Iterable arrayList6 = new ArrayList();
                    Iterable arrayList7 = new ArrayList();
                    Iterable arrayList8 = new ArrayList();
                    LongSparseArray longSparseArray = new LongSparseArray();
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    r13 = new Object[2];
                    boolean z = false;
                    r13[0] = Integer.valueOf(i);
                    boolean z2 = true;
                    r13[1] = Integer.valueOf(i2);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did ORDER BY d.pinned DESC, d.date DESC LIMIT %d,%d", r13), new Object[0]);
                    while (queryFinalized.next()) {
                        try {
                            int i2;
                            long longValue;
                            int i3;
                            AbstractSerializedData byteBufferValue;
                            Message TLdeserialize;
                            messages_Dialogs messages_dialogs3;
                            Message message;
                            long j;
                            TL_dialog tL_dialog = new TL_dialog();
                            tL_dialog.id = queryFinalized.longValue(z);
                            tL_dialog.top_message = queryFinalized.intValue(z2);
                            tL_dialog.unread_count = queryFinalized.intValue(2);
                            tL_dialog.last_message_date = queryFinalized.intValue(3);
                            tL_dialog.pts = queryFinalized.intValue(10);
                            if (tL_dialog.pts != 0) {
                                if (((int) tL_dialog.id) <= 0) {
                                    i2 = z2;
                                    tL_dialog.flags = i2;
                                    tL_dialog.read_inbox_max_id = queryFinalized.intValue(11);
                                    tL_dialog.read_outbox_max_id = queryFinalized.intValue(12);
                                    tL_dialog.pinnedNum = queryFinalized.intValue(14);
                                    tL_dialog.pinned = tL_dialog.pinnedNum == 0 ? z2 : z;
                                    tL_dialog.unread_mentions_count = queryFinalized.intValue(15);
                                    longValue = queryFinalized.longValue(8);
                                    i3 = (int) longValue;
                                    tL_dialog.notify_settings = new TL_peerNotifySettings();
                                    if ((i3 & z2) != 0) {
                                        tL_dialog.notify_settings.mute_until = (int) (longValue >> 32);
                                        if (tL_dialog.notify_settings.mute_until == 0) {
                                            tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        }
                                    }
                                    tL_messages_dialogs.dialogs.add(tL_dialog);
                                    byteBufferValue = queryFinalized.byteBufferValue(4);
                                    if (byteBufferValue != null) {
                                        TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                        byteBufferValue.reuse();
                                        if (TLdeserialize != null) {
                                            MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                                            TLdeserialize.id = queryFinalized.intValue(6);
                                            i3 = queryFinalized.intValue(9);
                                            if (i3 != 0) {
                                                tL_dialog.last_message_date = i3;
                                            }
                                            TLdeserialize.send_state = queryFinalized.intValue(7);
                                            messages_dialogs3 = tL_messages_dialogs;
                                            try {
                                                TLdeserialize.dialog_id = tL_dialog.id;
                                                tL_messages_dialogs = messages_dialogs3;
                                                tL_messages_dialogs.messages.add(TLdeserialize);
                                                MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                                                try {
                                                    if (TLdeserialize.reply_to_msg_id != 0) {
                                                        if (!(TLdeserialize.action instanceof TL_messageActionPinMessage)) {
                                                            try {
                                                                if (!(TLdeserialize.action instanceof TL_messageActionPaymentSent)) {
                                                                    if (TLdeserialize.action instanceof TL_messageActionGameScore) {
                                                                    }
                                                                }
                                                            } catch (Throwable e2) {
                                                                th = e2;
                                                                messages_dialogs = tL_messages_dialogs;
                                                                arrayList = arrayList3;
                                                                FileLog.m3e(th);
                                                                i3 = (int) tL_dialog.id;
                                                                i = (int) (tL_dialog.id >> 32);
                                                                if (i3 != 0) {
                                                                    if (arrayList7.contains(Integer.valueOf(i))) {
                                                                        arrayList7.add(Integer.valueOf(i));
                                                                    }
                                                                } else if (i == 1) {
                                                                    if (i3 > 0) {
                                                                        i = -i3;
                                                                        if (arrayList6.contains(Integer.valueOf(i))) {
                                                                            arrayList6.add(Integer.valueOf(i));
                                                                        }
                                                                    } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                                                        arrayList5.add(Integer.valueOf(i3));
                                                                    }
                                                                } else if (arrayList6.contains(Integer.valueOf(i3))) {
                                                                    arrayList6.add(Integer.valueOf(i3));
                                                                }
                                                                arrayList3 = arrayList;
                                                                tL_messages_dialogs = messages_dialogs;
                                                                z2 = true;
                                                                z = false;
                                                            }
                                                        }
                                                        if (!queryFinalized.isNull(13)) {
                                                            byteBufferValue = queryFinalized.byteBufferValue(13);
                                                            if (byteBufferValue != null) {
                                                                TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                                TLdeserialize.replyMessage.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                                                byteBufferValue.reuse();
                                                                if (TLdeserialize.replyMessage != null) {
                                                                    if (MessageObject.isMegagroup(TLdeserialize)) {
                                                                        message = TLdeserialize.replyMessage;
                                                                        message.flags |= Integer.MIN_VALUE;
                                                                    }
                                                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList5, arrayList6);
                                                                }
                                                            }
                                                        }
                                                        if (TLdeserialize.replyMessage == null) {
                                                            j = (long) TLdeserialize.reply_to_msg_id;
                                                            if (TLdeserialize.to_id.channel_id == 0) {
                                                                messages_dialogs = tL_messages_dialogs;
                                                                arrayList = arrayList3;
                                                                j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                                            } else {
                                                                messages_dialogs = tL_messages_dialogs;
                                                                arrayList = arrayList3;
                                                            }
                                                            try {
                                                                if (!arrayList8.contains(Long.valueOf(j))) {
                                                                    arrayList8.add(Long.valueOf(j));
                                                                }
                                                                longSparseArray.put(tL_dialog.id, TLdeserialize);
                                                            } catch (Exception e3) {
                                                                e2 = e3;
                                                                th = e2;
                                                                FileLog.m3e(th);
                                                                i3 = (int) tL_dialog.id;
                                                                i = (int) (tL_dialog.id >> 32);
                                                                if (i3 != 0) {
                                                                    if (i == 1) {
                                                                        if (arrayList6.contains(Integer.valueOf(i3))) {
                                                                            arrayList6.add(Integer.valueOf(i3));
                                                                        }
                                                                    } else if (i3 > 0) {
                                                                        i = -i3;
                                                                        if (arrayList6.contains(Integer.valueOf(i))) {
                                                                            arrayList6.add(Integer.valueOf(i));
                                                                        }
                                                                    } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                                                        arrayList5.add(Integer.valueOf(i3));
                                                                    }
                                                                } else if (arrayList7.contains(Integer.valueOf(i))) {
                                                                    arrayList7.add(Integer.valueOf(i));
                                                                }
                                                                arrayList3 = arrayList;
                                                                tL_messages_dialogs = messages_dialogs;
                                                                z2 = true;
                                                                z = false;
                                                            }
                                                            i3 = (int) tL_dialog.id;
                                                            i = (int) (tL_dialog.id >> 32);
                                                            if (i3 != 0) {
                                                                if (i == 1) {
                                                                    if (arrayList6.contains(Integer.valueOf(i3))) {
                                                                        arrayList6.add(Integer.valueOf(i3));
                                                                    }
                                                                } else if (i3 > 0) {
                                                                    i = -i3;
                                                                    if (arrayList6.contains(Integer.valueOf(i))) {
                                                                        arrayList6.add(Integer.valueOf(i));
                                                                    }
                                                                } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                                                    arrayList5.add(Integer.valueOf(i3));
                                                                }
                                                            } else if (arrayList7.contains(Integer.valueOf(i))) {
                                                                arrayList7.add(Integer.valueOf(i));
                                                            }
                                                            arrayList3 = arrayList;
                                                            tL_messages_dialogs = messages_dialogs;
                                                            z2 = true;
                                                            z = false;
                                                        }
                                                    }
                                                } catch (Exception e4) {
                                                    e2 = e4;
                                                    messages_dialogs = tL_messages_dialogs;
                                                    arrayList = arrayList3;
                                                    th = e2;
                                                    FileLog.m3e(th);
                                                    i3 = (int) tL_dialog.id;
                                                    i = (int) (tL_dialog.id >> 32);
                                                    if (i3 != 0) {
                                                        if (i == 1) {
                                                            if (arrayList6.contains(Integer.valueOf(i3))) {
                                                                arrayList6.add(Integer.valueOf(i3));
                                                            }
                                                        } else if (i3 > 0) {
                                                            i = -i3;
                                                            if (arrayList6.contains(Integer.valueOf(i))) {
                                                                arrayList6.add(Integer.valueOf(i));
                                                            }
                                                        } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                                            arrayList5.add(Integer.valueOf(i3));
                                                        }
                                                    } else if (arrayList7.contains(Integer.valueOf(i))) {
                                                        arrayList7.add(Integer.valueOf(i));
                                                    }
                                                    arrayList3 = arrayList;
                                                    tL_messages_dialogs = messages_dialogs;
                                                    z2 = true;
                                                    z = false;
                                                }
                                            } catch (Throwable e22) {
                                                th2 = e22;
                                                arrayList4 = arrayList;
                                            }
                                        }
                                    }
                                    messages_dialogs = tL_messages_dialogs;
                                    arrayList = arrayList3;
                                    i3 = (int) tL_dialog.id;
                                    i = (int) (tL_dialog.id >> 32);
                                    if (i3 != 0) {
                                        if (arrayList7.contains(Integer.valueOf(i))) {
                                            arrayList7.add(Integer.valueOf(i));
                                        }
                                    } else if (i == 1) {
                                        if (i3 > 0) {
                                            i = -i3;
                                            if (arrayList6.contains(Integer.valueOf(i))) {
                                                arrayList6.add(Integer.valueOf(i));
                                            }
                                        } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                            arrayList5.add(Integer.valueOf(i3));
                                        }
                                    } else if (arrayList6.contains(Integer.valueOf(i3))) {
                                        arrayList6.add(Integer.valueOf(i3));
                                    }
                                    arrayList3 = arrayList;
                                    tL_messages_dialogs = messages_dialogs;
                                    z2 = true;
                                    z = false;
                                }
                            }
                            i2 = z;
                            tL_dialog.flags = i2;
                            tL_dialog.read_inbox_max_id = queryFinalized.intValue(11);
                            tL_dialog.read_outbox_max_id = queryFinalized.intValue(12);
                            tL_dialog.pinnedNum = queryFinalized.intValue(14);
                            if (tL_dialog.pinnedNum == 0) {
                            }
                            tL_dialog.pinned = tL_dialog.pinnedNum == 0 ? z2 : z;
                            tL_dialog.unread_mentions_count = queryFinalized.intValue(15);
                            longValue = queryFinalized.longValue(8);
                            i3 = (int) longValue;
                            tL_dialog.notify_settings = new TL_peerNotifySettings();
                            if ((i3 & z2) != 0) {
                                tL_dialog.notify_settings.mute_until = (int) (longValue >> 32);
                                if (tL_dialog.notify_settings.mute_until == 0) {
                                    tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                }
                            }
                            tL_messages_dialogs.dialogs.add(tL_dialog);
                            byteBufferValue = queryFinalized.byteBufferValue(4);
                            if (byteBufferValue != null) {
                                TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                                    TLdeserialize.id = queryFinalized.intValue(6);
                                    i3 = queryFinalized.intValue(9);
                                    if (i3 != 0) {
                                        tL_dialog.last_message_date = i3;
                                    }
                                    TLdeserialize.send_state = queryFinalized.intValue(7);
                                    messages_dialogs3 = tL_messages_dialogs;
                                    TLdeserialize.dialog_id = tL_dialog.id;
                                    tL_messages_dialogs = messages_dialogs3;
                                    tL_messages_dialogs.messages.add(TLdeserialize);
                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                                    if (TLdeserialize.reply_to_msg_id != 0) {
                                        if (TLdeserialize.action instanceof TL_messageActionPinMessage) {
                                            if (TLdeserialize.action instanceof TL_messageActionPaymentSent) {
                                                if (TLdeserialize.action instanceof TL_messageActionGameScore) {
                                                }
                                            }
                                        }
                                        if (queryFinalized.isNull(13)) {
                                            byteBufferValue = queryFinalized.byteBufferValue(13);
                                            if (byteBufferValue != null) {
                                                TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                TLdeserialize.replyMessage.readAttachPath(byteBufferValue, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                                byteBufferValue.reuse();
                                                if (TLdeserialize.replyMessage != null) {
                                                    if (MessageObject.isMegagroup(TLdeserialize)) {
                                                        message = TLdeserialize.replyMessage;
                                                        message.flags |= Integer.MIN_VALUE;
                                                    }
                                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList5, arrayList6);
                                                }
                                            }
                                        }
                                        if (TLdeserialize.replyMessage == null) {
                                            j = (long) TLdeserialize.reply_to_msg_id;
                                            if (TLdeserialize.to_id.channel_id == 0) {
                                                messages_dialogs = tL_messages_dialogs;
                                                arrayList = arrayList3;
                                            } else {
                                                messages_dialogs = tL_messages_dialogs;
                                                arrayList = arrayList3;
                                                j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                            }
                                            if (arrayList8.contains(Long.valueOf(j))) {
                                                arrayList8.add(Long.valueOf(j));
                                            }
                                            longSparseArray.put(tL_dialog.id, TLdeserialize);
                                            i3 = (int) tL_dialog.id;
                                            i = (int) (tL_dialog.id >> 32);
                                            if (i3 != 0) {
                                                if (i == 1) {
                                                    if (arrayList6.contains(Integer.valueOf(i3))) {
                                                        arrayList6.add(Integer.valueOf(i3));
                                                    }
                                                } else if (i3 > 0) {
                                                    i = -i3;
                                                    if (arrayList6.contains(Integer.valueOf(i))) {
                                                        arrayList6.add(Integer.valueOf(i));
                                                    }
                                                } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                                    arrayList5.add(Integer.valueOf(i3));
                                                }
                                            } else if (arrayList7.contains(Integer.valueOf(i))) {
                                                arrayList7.add(Integer.valueOf(i));
                                            }
                                            arrayList3 = arrayList;
                                            tL_messages_dialogs = messages_dialogs;
                                            z2 = true;
                                            z = false;
                                        }
                                    }
                                }
                            }
                            messages_dialogs = tL_messages_dialogs;
                            arrayList = arrayList3;
                            i3 = (int) tL_dialog.id;
                            i = (int) (tL_dialog.id >> 32);
                            if (i3 != 0) {
                                if (arrayList7.contains(Integer.valueOf(i))) {
                                    arrayList7.add(Integer.valueOf(i));
                                }
                            } else if (i == 1) {
                                if (i3 > 0) {
                                    i = -i3;
                                    if (arrayList6.contains(Integer.valueOf(i))) {
                                        arrayList6.add(Integer.valueOf(i));
                                    }
                                } else if (arrayList5.contains(Integer.valueOf(i3))) {
                                    arrayList5.add(Integer.valueOf(i3));
                                }
                            } else if (arrayList6.contains(Integer.valueOf(i3))) {
                                arrayList6.add(Integer.valueOf(i3));
                            }
                            arrayList3 = arrayList;
                            tL_messages_dialogs = messages_dialogs;
                            z2 = true;
                            z = false;
                        } catch (Throwable e222) {
                            th2 = e222;
                            arrayList2 = arrayList3;
                            messages_dialogs2 = tL_messages_dialogs;
                            arrayList4 = arrayList2;
                        }
                    }
                    messages_dialogs = tL_messages_dialogs;
                    arrayList = arrayList3;
                    try {
                        queryFinalized.dispose();
                        if (!arrayList8.isEmpty()) {
                            SQLiteCursor queryFinalized2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", arrayList8)}), new Object[0]);
                            while (queryFinalized2.next()) {
                                AbstractSerializedData byteBufferValue2 = queryFinalized2.byteBufferValue(0);
                                if (byteBufferValue2 != null) {
                                    Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                    TLdeserialize2.readAttachPath(byteBufferValue2, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                                    byteBufferValue2.reuse();
                                    TLdeserialize2.id = queryFinalized2.intValue(1);
                                    TLdeserialize2.date = queryFinalized2.intValue(2);
                                    TLdeserialize2.dialog_id = queryFinalized2.longValue(3);
                                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize2, arrayList5, arrayList6);
                                    Message message2 = (Message) longSparseArray.get(TLdeserialize2.dialog_id);
                                    if (message2 != null) {
                                        message2.replyMessage = TLdeserialize2;
                                        TLdeserialize2.dialog_id = message2.dialog_id;
                                        if (MessageObject.isMegagroup(message2)) {
                                            TLdeserialize2 = message2.replyMessage;
                                            TLdeserialize2.flags |= Integer.MIN_VALUE;
                                        }
                                    }
                                }
                            }
                            queryFinalized2.dispose();
                        }
                        if (arrayList7.isEmpty()) {
                            arrayList4 = arrayList;
                        } else {
                            try {
                                arrayList4 = arrayList;
                                try {
                                    MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", arrayList7), arrayList4, arrayList5);
                                } catch (Exception e5) {
                                    e222 = e5;
                                    th2 = e222;
                                    messages_dialogs2 = messages_dialogs;
                                    messages_dialogs2.dialogs.clear();
                                    messages_dialogs2.users.clear();
                                    messages_dialogs2.chats.clear();
                                    arrayList4.clear();
                                    FileLog.m3e(th2);
                                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                                }
                            } catch (Exception e6) {
                                e222 = e6;
                                arrayList4 = arrayList;
                                th2 = e222;
                                messages_dialogs2 = messages_dialogs;
                                messages_dialogs2.dialogs.clear();
                                messages_dialogs2.users.clear();
                                messages_dialogs2.chats.clear();
                                arrayList4.clear();
                                FileLog.m3e(th2);
                                MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                            }
                        }
                        try {
                            if (arrayList6.isEmpty()) {
                                messages_dialogs2 = messages_dialogs;
                            } else {
                                messages_dialogs2 = messages_dialogs;
                                try {
                                    MessagesStorage.this.getChatsInternal(TextUtils.join(",", arrayList6), messages_dialogs2.chats);
                                } catch (Exception e7) {
                                    e222 = e7;
                                    th2 = e222;
                                    messages_dialogs2.dialogs.clear();
                                    messages_dialogs2.users.clear();
                                    messages_dialogs2.chats.clear();
                                    arrayList4.clear();
                                    FileLog.m3e(th2);
                                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                                }
                            }
                            if (!arrayList5.isEmpty()) {
                                MessagesStorage.this.getUsersInternal(TextUtils.join(",", arrayList5), messages_dialogs2.users);
                            }
                            MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, i, i2, 1, false, false, true);
                        } catch (Exception e8) {
                            e222 = e8;
                            messages_dialogs2 = messages_dialogs;
                            th2 = e222;
                            messages_dialogs2.dialogs.clear();
                            messages_dialogs2.users.clear();
                            messages_dialogs2.chats.clear();
                            arrayList4.clear();
                            FileLog.m3e(th2);
                            MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                        }
                    } catch (Exception e9) {
                        e222 = e9;
                        arrayList4 = arrayList;
                        messages_dialogs2 = messages_dialogs;
                        th2 = e222;
                        messages_dialogs2.dialogs.clear();
                        messages_dialogs2.users.clear();
                        messages_dialogs2.chats.clear();
                        arrayList4.clear();
                        FileLog.m3e(th2);
                        MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                    }
                } catch (Exception e10) {
                    e222 = e10;
                    arrayList2 = arrayList3;
                    messages_dialogs2 = tL_messages_dialogs;
                    arrayList4 = arrayList2;
                    th2 = e222;
                    messages_dialogs2.dialogs.clear();
                    messages_dialogs2.users.clear();
                    messages_dialogs2.chats.clear();
                    arrayList4.clear();
                    FileLog.m3e(th2);
                    MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDialogs(messages_dialogs2, arrayList4, 0, 100, 1, true, false, true);
                }
            }
        });
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) throws Exception {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (sQLitePreparedStatement = null; sQLitePreparedStatement < 5; sQLitePreparedStatement++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, sQLitePreparedStatement);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    private void putDialogsInternal(messages_Dialogs messages_dialogs, boolean z) {
        Throwable th;
        Throwable e;
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        MessagesStorage messagesStorage;
        MessagesStorage messagesStorage2;
        try {
            int i;
            messages_Dialogs messages_dialogs3;
            this.database.beginTransaction();
            LongSparseArray longSparseArray = new LongSparseArray(messages_dialogs2.messages.size());
            for (i = 0; i < messages_dialogs2.messages.size(); i++) {
                Message message = (Message) messages_dialogs2.messages.get(i);
                longSparseArray.put(MessageObject.getDialogId(message), message);
            }
            if (messages_dialogs2.dialogs.isEmpty()) {
                messages_dialogs3 = messages_dialogs2;
            } else {
                SQLitePreparedStatement executeFast = messagesStorage.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                SQLitePreparedStatement executeFast2 = messagesStorage.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                SQLitePreparedStatement executeFast3 = messagesStorage.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                SQLitePreparedStatement executeFast4 = messagesStorage.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                SQLitePreparedStatement executeFast5 = messagesStorage.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast6 = messagesStorage.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                int i2 = 0;
                while (i2 < messages_dialogs2.dialogs.size()) {
                    LongSparseArray longSparseArray2;
                    SQLitePreparedStatement sQLitePreparedStatement;
                    int i3;
                    TL_dialog tL_dialog = (TL_dialog) messages_dialogs2.dialogs.get(i2);
                    SQLitePreparedStatement sQLitePreparedStatement2 = executeFast;
                    if (tL_dialog.id == 0) {
                        if (tL_dialog.peer.user_id != 0) {
                            tL_dialog.id = (long) tL_dialog.peer.user_id;
                        } else if (tL_dialog.peer.chat_id != 0) {
                            tL_dialog.id = (long) (-tL_dialog.peer.chat_id);
                        } else {
                            tL_dialog.id = (long) (-tL_dialog.peer.channel_id);
                        }
                    }
                    if (z) {
                        SQLiteDatabase sQLiteDatabase = messagesStorage.database;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT did FROM dialogs WHERE did = ");
                        stringBuilder.append(tL_dialog.id);
                        SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                        boolean next = queryFinalized.next();
                        queryFinalized.dispose();
                        if (next) {
                            longSparseArray2 = longSparseArray;
                            sQLitePreparedStatement = sQLitePreparedStatement2;
                            i2++;
                            executeFast = sQLitePreparedStatement;
                            longSparseArray = longSparseArray2;
                            messagesStorage = this;
                            messages_dialogs2 = messages_dialogs;
                        }
                    }
                    Message message2 = (Message) longSparseArray.get(tL_dialog.id);
                    if (message2 != null) {
                        int i4;
                        int max = Math.max(message2.date, 0);
                        if (messagesStorage.isValidKeyboardToSave(message2)) {
                            i4 = max;
                            DataQuery.getInstance(messagesStorage.currentAccount).putBotKeyboard(tL_dialog.id, message2);
                        } else {
                            i4 = max;
                        }
                        messagesStorage.fixUnsupportedMedia(message2);
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message2.getObjectSize());
                        message2.serializeToStream(nativeByteBuffer);
                        long j = (long) message2.id;
                        longSparseArray2 = longSparseArray;
                        if (message2.to_id.channel_id != 0) {
                            sQLitePreparedStatement = sQLitePreparedStatement2;
                            j |= ((long) message2.to_id.channel_id) << 32;
                        } else {
                            sQLitePreparedStatement = sQLitePreparedStatement2;
                        }
                        try {
                            sQLitePreparedStatement.requery();
                            sQLitePreparedStatement.bindLong(1, j);
                            sQLitePreparedStatement.bindLong(2, tL_dialog.id);
                            sQLitePreparedStatement.bindInteger(3, MessageObject.getUnreadFlags(message2));
                            sQLitePreparedStatement.bindInteger(4, message2.send_state);
                            sQLitePreparedStatement.bindInteger(5, message2.date);
                            sQLitePreparedStatement.bindByteBuffer(6, nativeByteBuffer);
                            sQLitePreparedStatement.bindInteger(7, MessageObject.isOut(message2));
                            sQLitePreparedStatement.bindInteger(8, 0);
                            sQLitePreparedStatement.bindInteger(9, (message2.flags & 1024) != 0 ? message2.views : 0);
                            sQLitePreparedStatement.bindInteger(10, 0);
                            sQLitePreparedStatement.bindInteger(11, message2.mentioned);
                            sQLitePreparedStatement.step();
                            if (DataQuery.canAddMessageToMedia(message2)) {
                                executeFast3.requery();
                                executeFast3.bindLong(1, j);
                                executeFast3.bindLong(2, tL_dialog.id);
                                executeFast3.bindInteger(3, message2.date);
                                executeFast3.bindInteger(4, DataQuery.getMediaType(message2));
                                executeFast3.bindByteBuffer(5, nativeByteBuffer);
                                executeFast3.step();
                            }
                            nativeByteBuffer.reuse();
                            createFirstHoles(tL_dialog.id, executeFast5, executeFast6, message2.id);
                            i3 = i4;
                        } catch (Throwable e2) {
                            th = e2;
                            messagesStorage2 = this;
                        }
                    } else {
                        longSparseArray2 = longSparseArray;
                        sQLitePreparedStatement = sQLitePreparedStatement2;
                        i3 = 0;
                    }
                    long j2 = (long) tL_dialog.top_message;
                    if (tL_dialog.peer.channel_id != 0) {
                        j2 |= ((long) tL_dialog.peer.channel_id) << 32;
                    }
                    executeFast2.requery();
                    executeFast2.bindLong(1, tL_dialog.id);
                    executeFast2.bindInteger(2, i3);
                    executeFast2.bindInteger(3, tL_dialog.unread_count);
                    executeFast2.bindLong(4, j2);
                    executeFast2.bindInteger(5, tL_dialog.read_inbox_max_id);
                    executeFast2.bindInteger(6, tL_dialog.read_outbox_max_id);
                    executeFast2.bindLong(7, 0);
                    executeFast2.bindInteger(8, tL_dialog.unread_mentions_count);
                    executeFast2.bindInteger(9, tL_dialog.pts);
                    executeFast2.bindInteger(10, 0);
                    executeFast2.bindInteger(11, tL_dialog.pinnedNum);
                    executeFast2.step();
                    if (tL_dialog.notify_settings != null) {
                        executeFast4.requery();
                        i = 1;
                        executeFast4.bindLong(1, tL_dialog.id);
                        if (tL_dialog.notify_settings.mute_until == 0) {
                            i = 0;
                        }
                        executeFast4.bindInteger(2, i);
                        executeFast4.step();
                    }
                    i2++;
                    executeFast = sQLitePreparedStatement;
                    longSparseArray = longSparseArray2;
                    messagesStorage = this;
                    messages_dialogs2 = messages_dialogs;
                }
                executeFast.dispose();
                executeFast2.dispose();
                executeFast3.dispose();
                executeFast4.dispose();
                executeFast5.dispose();
                executeFast6.dispose();
                messages_dialogs3 = messages_dialogs;
            }
            try {
                try {
                    putUsersInternal(messages_dialogs3.users);
                    putChatsInternal(messages_dialogs3.chats);
                    messagesStorage2.database.commitTransaction();
                } catch (Exception e3) {
                    e2 = e3;
                    th = e2;
                    FileLog.m3e(th);
                }
            } catch (Exception e4) {
                e2 = e4;
                messagesStorage2 = this;
                th = e2;
                FileLog.m3e(th);
            }
        } catch (Exception e5) {
            e2 = e5;
            messagesStorage2 = messagesStorage;
            th = e2;
            FileLog.m3e(th);
        }
    }

    public void unpinAllDialogsExceptNew(final ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    ArrayList arrayList = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                    while (queryFinalized.next()) {
                        if (((int) queryFinalized.longValue(0)) != 0) {
                            arrayList.add(Long.valueOf(queryFinalized.longValue(0)));
                        }
                    }
                    queryFinalized.dispose();
                    if (!arrayList.isEmpty()) {
                        SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                        for (int i = 0; i < arrayList.size(); i++) {
                            long longValue = ((Long) arrayList.get(i)).longValue();
                            executeFast.requery();
                            executeFast.bindInteger(1, 0);
                            executeFast.bindLong(2, longValue);
                            executeFast.step();
                        }
                        executeFast.dispose();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void setDialogPinned(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                    executeFast.bindInteger(1, i);
                    executeFast.bindLong(2, j);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void putDialogs(final messages_Dialogs messages_dialogs, final boolean z) {
        if (!messages_dialogs.dialogs.isEmpty()) {
            this.storageQueue.postRunnable(new Runnable() {
                public void run() {
                    MessagesStorage.this.putDialogsInternal(messages_dialogs, z);
                    try {
                        MessagesStorage.this.loadUnreadMessages();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    public int getDialogReadMax(boolean z, long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        final boolean z2 = z;
        final long j2 = j;
        final Integer[] numArr2 = numArr;
        final CountDownLatch countDownLatch2 = countDownLatch;
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                SQLiteCursor sQLiteCursor = null;
                try {
                    SQLiteCursor queryFinalized;
                    SQLiteDatabase access$000;
                    StringBuilder stringBuilder;
                    if (z2) {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT outbox_max FROM dialogs WHERE did = ");
                        stringBuilder.append(j2);
                        queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    } else {
                        access$000 = MessagesStorage.this.database;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT inbox_max FROM dialogs WHERE did = ");
                        stringBuilder.append(j2);
                        queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    }
                    sQLiteCursor = queryFinalized;
                    if (sQLiteCursor.next()) {
                        numArr2[0] = Integer.valueOf(sQLiteCursor.intValue(0));
                    }
                    if (sQLiteCursor != null) {
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    countDownLatch2.countDown();
                } finally {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                }
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return numArr[0].intValue();
    }

    public int getChannelPtsSync(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                SQLiteCursor sQLiteCursor;
                Throwable th;
                SQLiteCursor sQLiteCursor2 = null;
                try {
                    SQLiteDatabase access$000 = MessagesStorage.this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("SELECT pts FROM dialogs WHERE did = ");
                    stringBuilder.append(-i);
                    SQLiteCursor queryFinalized = access$000.queryFinalized(stringBuilder.toString(), new Object[0]);
                    try {
                        if (queryFinalized.next()) {
                            numArr[0] = Integer.valueOf(queryFinalized.intValue(0));
                        }
                        if (queryFinalized != null) {
                            queryFinalized.dispose();
                        }
                    } catch (Throwable e) {
                        sQLiteCursor = queryFinalized;
                        th = e;
                        sQLiteCursor2 = sQLiteCursor;
                        try {
                            FileLog.m3e(th);
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            if (countDownLatch == null) {
                                countDownLatch.countDown();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            throw th;
                        }
                    } catch (Throwable e2) {
                        sQLiteCursor = queryFinalized;
                        th = e2;
                        sQLiteCursor2 = sQLiteCursor;
                        if (sQLiteCursor2 != null) {
                            sQLiteCursor2.dispose();
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    th = e3;
                    FileLog.m3e(th);
                    if (sQLiteCursor2 != null) {
                        sQLiteCursor2.dispose();
                    }
                    if (countDownLatch == null) {
                        countDownLatch.countDown();
                    }
                }
                try {
                    if (countDownLatch == null) {
                        countDownLatch.countDown();
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return numArr[0].intValue();
    }

    public User getUserSync(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final User[] userArr = new User[1];
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                userArr[0] = MessagesStorage.this.getUser(i);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return userArr[0];
    }

    public Chat getChatSync(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Chat[] chatArr = new Chat[1];
        this.storageQueue.postRunnable(new Runnable() {
            public void run() {
                chatArr[0] = MessagesStorage.this.getChat(i);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return chatArr[0];
    }

    public User getUser(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(i);
            getUsersInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty() == 0) {
                return (User) arrayList.get(0);
            }
            return null;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public ArrayList<User> getUsers(ArrayList<Integer> arrayList) {
        ArrayList<User> arrayList2 = new ArrayList();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Throwable e) {
            arrayList2.clear();
            FileLog.m3e(e);
        }
        return arrayList2;
    }

    public Chat getChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(i);
            getChatsInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty() == 0) {
                return (Chat) arrayList.get(0);
            }
            return null;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }

    public EncryptedChat getEncryptedChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(i);
            getEncryptedChatsInternal(stringBuilder.toString(), arrayList, null);
            if (arrayList.isEmpty() == 0) {
                return (EncryptedChat) arrayList.get(0);
            }
            return null;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return null;
        }
    }
}
