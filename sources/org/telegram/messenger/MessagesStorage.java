package org.telegram.messenger;

import android.appwidget.AppWidgetManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import j$.util.function.Consumer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputChannel;
import org.telegram.tgnet.TLRPC$InputDialogPeer;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Poll;
import org.telegram.tgnet.TLRPC$PollResults;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_folderPeer;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached;
import org.telegram.tgnet.TLRPC$TL_messageActionTopicCreate;
import org.telegram.tgnet.TLRPC$TL_messageActionTopicEdit;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_botResults;
import org.telegram.tgnet.TLRPC$TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC$TL_messages_deleteScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photos_photos;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC$TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC$TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC$TL_userStatusRecently;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Dialogs;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$photos_Photos;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public class MessagesStorage extends BaseController {
    private static final int LAST_DB_VERSION = 106;
    private int archiveUnreadCount;
    private int[][] bots;
    private File cacheFile;
    private int[][] channels;
    private int[][] contacts;
    private SQLiteDatabase database;
    private boolean databaseMigrationInProgress;
    private ArrayList<MessagesController.DialogFilter> dialogFilters;
    private SparseArray<MessagesController.DialogFilter> dialogFiltersMap;
    private LongSparseIntArray dialogIsForum;
    private LongSparseArray<Integer> dialogsWithMentions;
    private LongSparseArray<Integer> dialogsWithUnread;
    private int[][] groups;
    private int lastDateValue;
    private int lastPtsValue;
    private int lastQtsValue;
    private int lastSavedDate;
    private int lastSavedPts;
    private int lastSavedQts;
    private int lastSavedSeq;
    private int lastSecretVersion;
    private int lastSeqValue;
    private AtomicLong lastTaskId;
    private int mainUnreadCount;
    private int malformedCleanupCount;
    private int[] mentionChannels;
    private int[] mentionGroups;
    private int[][] nonContacts;
    private CountDownLatch openSync;
    private volatile int pendingArchiveUnreadCount;
    private volatile int pendingMainUnreadCount;
    private int secretG;
    private byte[] secretPBytes;
    private File shmCacheFile;
    public boolean showClearDatabaseAlert;
    private DispatchQueue storageQueue;
    private SparseArray<ArrayList<Runnable>> tasks;
    private LongSparseArray<Boolean> unknownDialogsIds;
    private File walCacheFile;
    private static volatile MessagesStorage[] Instance = new MessagesStorage[4];
    private static final Object[] lockObjects = new Object[4];

    /* loaded from: classes.dex */
    public interface BooleanCallback {
        void run(boolean z);
    }

    /* loaded from: classes.dex */
    public interface IntCallback {
        void run(int i);
    }

    /* loaded from: classes.dex */
    public interface LongCallback {
        void run(long j);
    }

    /* loaded from: classes.dex */
    public interface StringCallback {
        void run(String str);
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage = Instance[i];
        if (messagesStorage == null) {
            synchronized (lockObjects[i]) {
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
        try {
            this.openSync.await();
        } catch (Throwable unused) {
        }
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

    public int getMainUnreadCount() {
        return this.mainUnreadCount;
    }

    public int getArchiveUnreadCount() {
        return this.archiveUnreadCount;
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
        super(i);
        this.lastTaskId = new AtomicLong(System.currentTimeMillis());
        this.tasks = new SparseArray<>();
        this.lastDateValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSeqValue = 0;
        this.lastSecretVersion = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.dialogFilters = new ArrayList<>();
        this.dialogFiltersMap = new SparseArray<>();
        this.unknownDialogsIds = new LongSparseArray<>();
        this.openSync = new CountDownLatch(1);
        this.dialogIsForum = new LongSparseIntArray();
        this.contacts = new int[][]{new int[2], new int[2]};
        this.nonContacts = new int[][]{new int[2], new int[2]};
        this.bots = new int[][]{new int[2], new int[2]};
        this.channels = new int[][]{new int[2], new int[2]};
        this.groups = new int[][]{new int[2], new int[2]};
        this.mentionChannels = new int[2];
        this.mentionGroups = new int[2];
        this.dialogsWithMentions = new LongSparseArray<>();
        this.dialogsWithUnread = new LongSparseArray<>();
        this.malformedCleanupCount = 0;
        DispatchQueue dispatchQueue = new DispatchQueue("storageQueue_" + i);
        this.storageQueue = dispatchQueue;
        dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        openDatabase(1);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public void bindTaskToGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.tasks.put(i, arrayList);
        }
        arrayList.add(runnable);
    }

    public void cancelTasksForGuid(int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.storageQueue.cancelRunnable(arrayList.get(i2));
        }
        this.tasks.remove(i);
    }

    public void completeTaskForGuid(Runnable runnable, int i) {
        ArrayList<Runnable> arrayList = this.tasks.get(i);
        if (arrayList == null) {
            return;
        }
        arrayList.remove(runnable);
        if (!arrayList.isEmpty()) {
            return;
        }
        this.tasks.remove(i);
    }

    public long getDatabaseSize() {
        File file = this.cacheFile;
        long j = 0;
        if (file != null) {
            j = 0 + file.length();
        }
        File file2 = this.shmCacheFile;
        return file2 != null ? j + file2.length() : j;
    }

    public void openDatabase(int i) {
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File file = new File(filesDirFixed, "account" + this.currentAccount + "/");
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "cache4.db");
        this.walCacheFile = new File(filesDirFixed, "cache4.db-wal");
        this.shmCacheFile = new File(filesDirFixed, "cache4.db-shm");
        boolean z = !this.cacheFile.exists();
        int i2 = 3;
        try {
            SQLiteDatabase sQLiteDatabase = new SQLiteDatabase(this.cacheFile.getPath());
            this.database = sQLiteDatabase;
            sQLiteDatabase.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = MEMORY").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_size_limit = 10485760").stepThis().dispose();
            if (z) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("create new database");
                }
                this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, reply_to_message_id INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, group_id INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_groupid_messages_v2 ON messages_v2(uid, mid, group_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, data BLOB, unread_reactions INTEGER, last_mid_group INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER, online INTEGER, inviter INTEGER, links INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER, emoji INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB, proximity INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(message_id, dialog_id))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE animated_emoji(document_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_statuses(data BLOB, type INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_holes_topics(uid INTEGER, topic_id INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes_topics(uid, topic_id, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, PRIMARY KEY(mid, topic_id, uid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_topics ON messages_topics(uid, mid, read_state, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_topics ON messages_topics(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_topics ON messages_topics(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_topics ON messages_topics(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_topics ON messages_topics(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_topics ON messages_topics(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_topics ON messages_topics(mid, is_channel);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_topics ON messages_topics(mid, reply_to_message_id);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, uid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, topic_id, uid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, topic_id, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_topics ON media_topics(uid, topic_id, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_topics(uid INTEGER, topic_id INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_topics ON media_holes_topics(uid, topic_id, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE topics(did INTEGER, topic_id INTEGER, data BLOB, top_message INTEGER, topic_message BLOB, unread_count INTEGER, max_read_id INTEGER, unread_mentions INTEGER, unread_reactions INTEGER, read_outbox INTEGER, pinned INTEGER, PRIMARY KEY(did, topic_id));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS did_top_message_topics ON topics(did, top_message);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_topics(uid INTEGER, topic_id INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, topic_id, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE reaction_mentions_topics(message_id INTEGER, state INTEGER, dialog_id INTEGER, topic_id INTEGER, PRIMARY KEY(message_id, dialog_id, topic_id))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_topics_did ON reaction_mentions_topics(dialog_id, topic_id);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 106").stepThis().dispose();
            } else {
                int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current db version = " + intValue);
                }
                if (intValue == 0) {
                    throw new Exception("malformed");
                }
                try {
                    SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (queryFinalized.next()) {
                        this.lastSeqValue = queryFinalized.intValue(0);
                        this.lastPtsValue = queryFinalized.intValue(1);
                        this.lastDateValue = queryFinalized.intValue(2);
                        this.lastQtsValue = queryFinalized.intValue(3);
                        this.lastSecretVersion = queryFinalized.intValue(4);
                        this.secretG = queryFinalized.intValue(5);
                        if (queryFinalized.isNull(6)) {
                            this.secretPBytes = null;
                        } else {
                            byte[] byteArrayValue = queryFinalized.byteArrayValue(6);
                            this.secretPBytes = byteArrayValue;
                            if (byteArrayValue != null && byteArrayValue.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Exception e) {
                    FileLog.e(e);
                    if (e.getMessage() != null && e.getMessage().contains("malformed")) {
                        throw new RuntimeException("malformed");
                    }
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (intValue < 106) {
                    try {
                        updateDbToLastVersion(intValue);
                    } catch (Exception e3) {
                        if (BuildVars.DEBUG_PRIVATE_VERSION) {
                            throw e3;
                        }
                        FileLog.e(e3);
                        throw new RuntimeException("malformed");
                    }
                }
            }
        } catch (Exception e4) {
            FileLog.e(e4);
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                throw new RuntimeException(e4);
            }
            if (i < 3 && e4.getMessage() != null && e4.getMessage().contains("malformed")) {
                if (i == 2) {
                    cleanupInternal(true);
                    for (int i3 = 0; i3 < 2; i3++) {
                        getUserConfig().setDialogsLoadOffset(i3, 0, 0, 0L, 0L, 0L, 0L);
                        getUserConfig().setTotalDialogsCount(i3, 0);
                    }
                    getUserConfig().saveConfig(false);
                } else {
                    cleanupInternal(false);
                }
                if (i == 1) {
                    i2 = 2;
                }
                openDatabase(i2);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$openDatabase$1();
            }
        });
        loadDialogFilters();
        loadUnreadMessages();
        loadPendingTasks();
        try {
            this.openSync.countDown();
        } catch (Throwable unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$openDatabase$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openDatabase$1() {
        if (this.databaseMigrationInProgress) {
            this.databaseMigrationInProgress = false;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openDatabase$2() {
        this.showClearDatabaseAlert = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseOpened, new Object[0]);
    }

    public boolean isDatabaseMigrationInProgress() {
        return this.databaseMigrationInProgress;
    }

    private void updateDbToLastVersion(int i) throws Exception {
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2;
        int i2;
        int i3;
        NativeByteBuffer nativeByteBuffer;
        SQLiteCursor sQLiteCursor3;
        SQLiteCursor sQLiteCursor4;
        SQLiteCursor sQLiteCursor5;
        SQLiteCursor sQLiteCursor6;
        SQLiteCursor sQLiteCursor7;
        SQLiteCursor sQLiteCursor8;
        final MessagesStorage messagesStorage = this;
        int i4 = i;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$3();
            }
        });
        FileLog.d("MessagesStorage start db migration from " + i4 + " to 106");
        int i5 = 4;
        if (i4 < 4) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            messagesStorage.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            fixNotificationSettings();
            messagesStorage.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i4 = 4;
        }
        int i6 = 6;
        int i7 = 2;
        int i8 = 1;
        int i9 = 0;
        if (i4 == 4) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            SQLiteCursor queryFinalized = messagesStorage.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int limit = byteBufferValue.limit();
                    for (int i10 = 0; i10 < limit / 4; i10++) {
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
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i4 = 6;
        }
        if (i4 == 6) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            i4 = 7;
        }
        if (i4 == 7 || i4 == 8 || i4 == 9) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i4 = 10;
        }
        if (i4 == 10) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            i4 = 11;
        }
        if (i4 == 11 || i4 == 12) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            i4 = 13;
        }
        if (i4 == 13) {
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i4 = 14;
        }
        if (i4 == 14) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i4 = 15;
        }
        if (i4 == 15) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i4 = 16;
        }
        if (i4 == 16) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i4 = 17;
        }
        if (i4 == 17) {
            messagesStorage.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i4 = 18;
        }
        if (i4 == 18) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i4 = 19;
        }
        if (i4 == 19) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i4 = 20;
        }
        if (i4 == 20) {
            messagesStorage.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i4 = 21;
        }
        if (i4 == 21) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            SQLiteCursor queryFinalized2 = messagesStorage.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement executeFast2 = messagesStorage.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (queryFinalized2.next()) {
                long intValue2 = queryFinalized2.intValue(0);
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                if (byteBufferValue2 != null) {
                    TLRPC$ChatParticipants TLdeserialize = TLRPC$ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    byteBufferValue2.reuse();
                    if (TLdeserialize != null) {
                        TLRPC$TL_chatFull tLRPC$TL_chatFull = new TLRPC$TL_chatFull();
                        tLRPC$TL_chatFull.id = intValue2;
                        tLRPC$TL_chatFull.chat_photo = new TLRPC$TL_photoEmpty();
                        tLRPC$TL_chatFull.notify_settings = new TLRPC$TL_peerNotifySettingsEmpty_layer77();
                        tLRPC$TL_chatFull.exported_invite = null;
                        tLRPC$TL_chatFull.participants = TLdeserialize;
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLRPC$TL_chatFull.getObjectSize());
                        tLRPC$TL_chatFull.serializeToStream(nativeByteBuffer2);
                        executeFast2.requery();
                        executeFast2.bindLong(1, intValue2);
                        executeFast2.bindByteBuffer(2, nativeByteBuffer2);
                        executeFast2.step();
                        nativeByteBuffer2.reuse();
                    }
                }
            }
            executeFast2.dispose();
            queryFinalized2.dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            i4 = 22;
        }
        if (i4 == 22) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            i4 = 23;
        }
        if (i4 == 23 || i4 == 24) {
            messagesStorage.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i4 = 25;
        }
        if (i4 == 25 || i4 == 26) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            i4 = 27;
        }
        if (i4 == 27) {
            messagesStorage.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            i4 = 28;
        }
        if (i4 == 28 || i4 == 29) {
            messagesStorage.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i4 = 30;
        }
        if (i4 == 30) {
            messagesStorage.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            i4 = 31;
        }
        if (i4 == 31) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            i4 = 32;
        }
        if (i4 == 32) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i4 = 33;
        }
        if (i4 == 33) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            i4 = 34;
        }
        if (i4 == 34) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i4 = 35;
        }
        if (i4 == 35) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            i4 = 36;
        }
        if (i4 == 36) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            i4 = 37;
        }
        if (i4 == 37) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i4 = 38;
        }
        if (i4 == 38) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            i4 = 39;
        }
        if (i4 == 39) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            i4 = 40;
        }
        if (i4 == 40) {
            fixNotificationSettings();
            messagesStorage.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i4 = 41;
        }
        if (i4 == 41) {
            messagesStorage.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            i4 = 42;
        }
        if (i4 == 42) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i4 = 43;
        }
        if (i4 == 43) {
            messagesStorage.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            i4 = 44;
        }
        if (i4 == 44) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i4 = 45;
        }
        if (i4 == 45) {
            messagesStorage.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            i4 = 46;
        }
        if (i4 == 46) {
            messagesStorage.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            i4 = 47;
        }
        if (i4 == 47) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            i4 = 48;
        }
        if (i4 == 48) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            i4 = 49;
        }
        if (i4 == 49) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            i4 = 50;
        }
        if (i4 == 50) {
            messagesStorage.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            i4 = 51;
        }
        if (i4 == 51) {
            messagesStorage.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            i4 = 52;
        }
        if (i4 == 52) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls_v2(id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            i4 = 53;
        }
        if (i4 == 53) {
            messagesStorage.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            i4 = 54;
        }
        if (i4 == 54) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            i4 = 55;
        }
        if (i4 == 55) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            i4 = 56;
        }
        if (i4 == 56 || i4 == 57) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            i4 = 58;
        }
        if (i4 == 58) {
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            i4 = 59;
        }
        if (i4 == 59) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
            i4 = 60;
        }
        if (i4 == 60) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS channel_admins;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS blocked_users;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 61").stepThis().dispose();
            i4 = 61;
        }
        if (i4 == 61) {
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
            i4 = 62;
        }
        if (i4 == 62) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 63").stepThis().dispose();
            i4 = 63;
        }
        if (i4 == 63) {
            messagesStorage.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
            i4 = 64;
        }
        if (i4 == 64) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 65").stepThis().dispose();
            i4 = 65;
        }
        if (i4 == 65) {
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 66").stepThis().dispose();
            i4 = 66;
        }
        if (i4 == 66) {
            messagesStorage.database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 67").stepThis().dispose();
            i4 = 67;
        }
        if (i4 == 67) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 68").stepThis().dispose();
            i4 = 68;
        }
        if (i4 == 68) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN forwards INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 69").stepThis().dispose();
            i4 = 69;
        }
        if (i4 == 69) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN replies_data BLOB default NULL");
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN thread_reply_id INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 70").stepThis().dispose();
            i4 = 70;
        }
        if (i4 == 70) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 71").stepThis().dispose();
            i4 = 71;
        }
        if (i4 == 71) {
            messagesStorage.executeNoException("ALTER TABLE sharing_locations ADD COLUMN proximity INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 72").stepThis().dispose();
            i4 = 72;
        }
        if (i4 == 72) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 73").stepThis().dispose();
            i4 = 73;
        }
        if (i4 == 73) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN inviter INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 74").stepThis().dispose();
            i4 = 74;
        }
        if (i4 == 74) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 75").stepThis().dispose();
            i4 = 75;
        }
        if (i4 == 75) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN links INTEGER default 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 76").stepThis().dispose();
            i4 = 76;
        }
        if (i4 == 76) {
            messagesStorage.executeNoException("ALTER TABLE enc_tasks_v2 ADD COLUMN media INTEGER default -1");
            messagesStorage.database.executeFast("PRAGMA user_version = 77").stepThis().dispose();
            i4 = 77;
        }
        if (i4 == 77) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS channel_admins_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 78").stepThis().dispose();
            i4 = 78;
        }
        if (i4 == 78) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS bot_info;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 79").stepThis().dispose();
            i4 = 79;
        }
        int i11 = 3;
        if (i4 == 79) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v3(mid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, media))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v3 ON enc_tasks_v3(date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            SQLiteCursor queryFinalized3 = messagesStorage.database.queryFinalized("SELECT mid, date, media FROM enc_tasks_v2 WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast3 = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v3 VALUES(?, ?, ?)");
            if (queryFinalized3.next()) {
                long longValue = queryFinalized3.longValue(0);
                int intValue3 = queryFinalized3.intValue(1);
                int intValue4 = queryFinalized3.intValue(2);
                executeFast3.requery();
                executeFast3.bindLong(1, longValue);
                executeFast3.bindInteger(2, intValue3);
                executeFast3.bindInteger(3, intValue4);
                executeFast3.step();
            }
            executeFast3.dispose();
            queryFinalized3.dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v2;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 80").stepThis().dispose();
            i4 = 80;
        }
        int i12 = 5;
        if (i4 == 80) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS bot_keyboard_idx_mid;").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor8 = messagesStorage.database.queryFinalized("SELECT mid, uid, send_state, date, data, ttl, replydata FROM scheduled_messages_v2 WHERE 1", new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                sQLiteCursor8 = null;
            }
            if (sQLiteCursor8 != null) {
                SQLitePreparedStatement executeFast4 = messagesStorage.database.executeFast("REPLACE INTO scheduled_messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor8.next()) {
                    NativeByteBuffer byteBufferValue3 = sQLiteCursor8.byteBufferValue(4);
                    if (byteBufferValue3 != null) {
                        int intValue5 = sQLiteCursor8.intValue(i9);
                        long longValue2 = sQLiteCursor8.longValue(1);
                        int intValue6 = sQLiteCursor8.intValue(2);
                        int intValue7 = sQLiteCursor8.intValue(3);
                        int intValue8 = sQLiteCursor8.intValue(i12);
                        NativeByteBuffer byteBufferValue4 = sQLiteCursor8.byteBufferValue(6);
                        executeFast4.requery();
                        executeFast4.bindInteger(1, intValue5);
                        executeFast4.bindLong(2, longValue2);
                        executeFast4.bindInteger(3, intValue6);
                        executeFast4.bindByteBuffer(4, byteBufferValue3);
                        executeFast4.bindInteger(5, intValue7);
                        executeFast4.bindInteger(6, intValue8);
                        if (byteBufferValue4 != null) {
                            executeFast4.bindByteBuffer(7, byteBufferValue4);
                        } else {
                            executeFast4.bindNull(7);
                        }
                        executeFast4.step();
                        if (byteBufferValue4 != null) {
                            byteBufferValue4.reuse();
                        }
                        byteBufferValue3.reuse();
                        i9 = 0;
                        i12 = 5;
                    }
                }
                sQLiteCursor8.dispose();
                executeFast4.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_scheduled_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_idx_scheduled_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS scheduled_messages;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 81").stepThis().dispose();
            i4 = 81;
        }
        if (i4 == 81) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v3(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v3 ON media_v3(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor7 = messagesStorage.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v2 WHERE 1", new Object[0]);
            } catch (Exception e2) {
                FileLog.e(e2);
                sQLiteCursor7 = null;
            }
            if (sQLiteCursor7 != null) {
                SQLitePreparedStatement executeFast5 = messagesStorage.database.executeFast("REPLACE INTO media_v3 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor7.next()) {
                    NativeByteBuffer byteBufferValue5 = sQLiteCursor7.byteBufferValue(4);
                    if (byteBufferValue5 != null) {
                        int intValue9 = sQLiteCursor7.intValue(0);
                        long longValue3 = sQLiteCursor7.longValue(1);
                        if (((int) longValue3) == 0) {
                            longValue3 = DialogObject.makeEncryptedDialogId((int) (longValue3 >> 32));
                        }
                        int intValue10 = sQLiteCursor7.intValue(2);
                        int intValue11 = sQLiteCursor7.intValue(3);
                        executeFast5.requery();
                        executeFast5.bindInteger(1, intValue9);
                        executeFast5.bindLong(2, longValue3);
                        executeFast5.bindInteger(3, intValue10);
                        executeFast5.bindInteger(4, intValue11);
                        executeFast5.bindByteBuffer(5, byteBufferValue5);
                        executeFast5.step();
                        byteBufferValue5.reuse();
                    }
                }
                sQLiteCursor7.dispose();
                executeFast5.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_v2;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 82").stepThis().dispose();
            i4 = 82;
        }
        if (i4 == 82) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor3 = messagesStorage.database.queryFinalized("SELECT r.random_id, r.mid, m.uid FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e3) {
                FileLog.e(e3);
                sQLiteCursor3 = null;
            }
            if (sQLiteCursor3 != null) {
                SQLitePreparedStatement executeFast6 = messagesStorage.database.executeFast("REPLACE INTO randoms_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor3.next()) {
                    long longValue4 = sQLiteCursor3.longValue(0);
                    int intValue12 = sQLiteCursor3.intValue(1);
                    long longValue5 = sQLiteCursor3.longValue(2);
                    if (((int) longValue5) == 0) {
                        longValue5 = DialogObject.makeEncryptedDialogId((int) (longValue5 >> 32));
                    }
                    executeFast6.requery();
                    executeFast6.bindLong(1, longValue4);
                    executeFast6.bindInteger(2, intValue12);
                    executeFast6.bindLong(3, longValue5);
                    executeFast6.step();
                }
                sQLiteCursor3.dispose();
                executeFast6.dispose();
            }
            try {
                sQLiteCursor4 = messagesStorage.database.queryFinalized("SELECT p.mid, m.uid, p.id FROM polls as p INNER JOIN messages as m ON p.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e4) {
                FileLog.e(e4);
                sQLiteCursor4 = null;
            }
            if (sQLiteCursor4 != null) {
                SQLitePreparedStatement executeFast7 = messagesStorage.database.executeFast("REPLACE INTO polls_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor4.next()) {
                    int intValue13 = sQLiteCursor4.intValue(0);
                    long longValue6 = sQLiteCursor4.longValue(1);
                    long longValue7 = sQLiteCursor4.longValue(2);
                    if (((int) longValue6) == 0) {
                        longValue6 = DialogObject.makeEncryptedDialogId((int) (longValue6 >> 32));
                    }
                    executeFast7.requery();
                    executeFast7.bindInteger(1, intValue13);
                    executeFast7.bindLong(2, longValue6);
                    executeFast7.bindLong(3, longValue7);
                    executeFast7.step();
                }
                sQLiteCursor4.dispose();
                executeFast7.dispose();
            }
            try {
                sQLiteCursor5 = messagesStorage.database.queryFinalized("SELECT wp.id, wp.mid, m.uid FROM webpage_pending as wp INNER JOIN messages as m ON wp.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e5) {
                FileLog.e(e5);
                sQLiteCursor5 = null;
            }
            if (sQLiteCursor5 != null) {
                SQLitePreparedStatement executeFast8 = messagesStorage.database.executeFast("REPLACE INTO webpage_pending_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor5.next()) {
                    long longValue8 = sQLiteCursor5.longValue(0);
                    int intValue14 = sQLiteCursor5.intValue(1);
                    long longValue9 = sQLiteCursor5.longValue(2);
                    if (((int) longValue9) == 0) {
                        longValue9 = DialogObject.makeEncryptedDialogId((int) (longValue9 >> 32));
                    }
                    executeFast8.requery();
                    executeFast8.bindLong(1, longValue8);
                    executeFast8.bindInteger(2, intValue14);
                    executeFast8.bindLong(3, longValue9);
                    executeFast8.step();
                }
                sQLiteCursor5.dispose();
                executeFast8.dispose();
            }
            try {
                sQLiteCursor6 = messagesStorage.database.queryFinalized("SELECT et.mid, m.uid, et.date, et.media FROM enc_tasks_v3 as et INNER JOIN messages as m ON et.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e6) {
                FileLog.e(e6);
                sQLiteCursor6 = null;
            }
            if (sQLiteCursor6 != null) {
                SQLitePreparedStatement executeFast9 = messagesStorage.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                while (sQLiteCursor6.next()) {
                    int intValue15 = sQLiteCursor6.intValue(0);
                    long longValue10 = sQLiteCursor6.longValue(1);
                    int intValue16 = sQLiteCursor6.intValue(2);
                    int intValue17 = sQLiteCursor6.intValue(3);
                    if (((int) longValue10) == 0) {
                        longValue10 = DialogObject.makeEncryptedDialogId((int) (longValue10 >> 32));
                    }
                    executeFast9.requery();
                    executeFast9.bindInteger(1, intValue15);
                    executeFast9.bindLong(2, longValue10);
                    executeFast9.bindInteger(3, intValue16);
                    executeFast9.bindInteger(4, intValue17);
                    executeFast9.step();
                }
                sQLiteCursor6.dispose();
                executeFast9.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_idx_randoms;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS randoms;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS enc_tasks_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS polls_id;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS polls;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS webpage_pending;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 83").stepThis().dispose();
            i4 = 83;
        }
        if (i4 == 83) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor2 = messagesStorage.database.queryFinalized("SELECT mid, uid, read_state, send_state, date, data, out, ttl, media, replydata, imp, mention, forwards, replies_data, thread_reply_id FROM messages WHERE 1", new Object[0]);
            } catch (Exception e7) {
                FileLog.e(e7);
                sQLiteCursor2 = null;
            }
            if (sQLiteCursor2 != null) {
                SQLitePreparedStatement executeFast10 = messagesStorage.database.executeFast("REPLACE INTO messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor2.next()) {
                    NativeByteBuffer byteBufferValue6 = sQLiteCursor2.byteBufferValue(5);
                    if (byteBufferValue6 != null) {
                        long intValue18 = sQLiteCursor2.intValue(0);
                        long longValue11 = sQLiteCursor2.longValue(i8);
                        if (((int) longValue11) == 0) {
                            longValue11 = DialogObject.makeEncryptedDialogId((int) (longValue11 >> 32));
                        }
                        int intValue19 = sQLiteCursor2.intValue(i7);
                        int intValue20 = sQLiteCursor2.intValue(i11);
                        int intValue21 = sQLiteCursor2.intValue(i5);
                        int intValue22 = sQLiteCursor2.intValue(i6);
                        int intValue23 = sQLiteCursor2.intValue(7);
                        int intValue24 = sQLiteCursor2.intValue(8);
                        NativeByteBuffer byteBufferValue7 = sQLiteCursor2.byteBufferValue(9);
                        int intValue25 = sQLiteCursor2.intValue(10);
                        int intValue26 = sQLiteCursor2.intValue(11);
                        int intValue27 = sQLiteCursor2.intValue(12);
                        NativeByteBuffer byteBufferValue8 = sQLiteCursor2.byteBufferValue(13);
                        int intValue28 = sQLiteCursor2.intValue(14);
                        SQLiteCursor sQLiteCursor9 = sQLiteCursor2;
                        int i13 = (int) (longValue11 >> 32);
                        if (intValue23 < 0) {
                            TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(byteBufferValue6, byteBufferValue6.readInt32(false), false);
                            if (TLdeserialize2 != null) {
                                i3 = intValue24;
                                TLdeserialize2.readAttachPath(byteBufferValue6, getUserConfig().clientUserId);
                                if (TLdeserialize2.params == null) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    TLdeserialize2.params = hashMap;
                                    StringBuilder sb = new StringBuilder();
                                    i2 = i13;
                                    sb.append("");
                                    sb.append(intValue23);
                                    hashMap.put("fwd_peer", sb.toString());
                                } else {
                                    i2 = i13;
                                }
                                byteBufferValue6.reuse();
                                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(TLdeserialize2.getObjectSize());
                                TLdeserialize2.serializeToStream(nativeByteBuffer3);
                                byteBufferValue6 = nativeByteBuffer3;
                            } else {
                                i2 = i13;
                                i3 = intValue24;
                            }
                            intValue23 = 0;
                        } else {
                            i2 = i13;
                            i3 = intValue24;
                        }
                        executeFast10.requery();
                        executeFast10.bindInteger(1, (int) intValue18);
                        executeFast10.bindLong(2, longValue11);
                        executeFast10.bindInteger(3, intValue19);
                        executeFast10.bindInteger(4, intValue20);
                        executeFast10.bindInteger(5, intValue21);
                        executeFast10.bindByteBuffer(6, byteBufferValue6);
                        executeFast10.bindInteger(7, intValue22);
                        executeFast10.bindInteger(8, intValue23);
                        executeFast10.bindInteger(9, i3);
                        if (byteBufferValue7 != null) {
                            executeFast10.bindByteBuffer(10, byteBufferValue7);
                        } else {
                            executeFast10.bindNull(10);
                        }
                        executeFast10.bindInteger(11, intValue25);
                        executeFast10.bindInteger(12, intValue26);
                        executeFast10.bindInteger(13, intValue27);
                        if (byteBufferValue8 != null) {
                            nativeByteBuffer = byteBufferValue8;
                            executeFast10.bindByteBuffer(14, nativeByteBuffer);
                        } else {
                            nativeByteBuffer = byteBufferValue8;
                            executeFast10.bindNull(14);
                        }
                        executeFast10.bindInteger(15, intValue28);
                        executeFast10.bindInteger(16, i2 > 0 ? 1 : 0);
                        executeFast10.step();
                        if (byteBufferValue7 != null) {
                            byteBufferValue7.reuse();
                        }
                        if (nativeByteBuffer != null) {
                            nativeByteBuffer.reuse();
                        }
                        byteBufferValue6.reuse();
                        sQLiteCursor2 = sQLiteCursor9;
                        i5 = 4;
                        i6 = 6;
                        i7 = 2;
                        i8 = 1;
                        i11 = 3;
                    }
                }
                sQLiteCursor2.dispose();
                executeFast10.dispose();
            }
            messagesStorage = this;
            int i14 = 0;
            SQLiteCursor queryFinalized4 = messagesStorage.database.queryFinalized("SELECT did, last_mid, last_mid_i FROM dialogs WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast11 = messagesStorage.database.executeFast("UPDATE dialogs SET last_mid = ?, last_mid_i = ? WHERE did = ?");
            ArrayList arrayList = null;
            ArrayList arrayList2 = null;
            while (queryFinalized4.next()) {
                long longValue12 = queryFinalized4.longValue(i14);
                int i15 = (int) longValue12;
                int i16 = (int) (longValue12 >> 32);
                if (i15 == 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(i16));
                } else if (i16 == 2) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(i15));
                }
                executeFast11.requery();
                executeFast11.bindInteger(1, queryFinalized4.intValue(1));
                executeFast11.bindInteger(2, queryFinalized4.intValue(2));
                executeFast11.bindLong(3, longValue12);
                executeFast11.step();
                i14 = 0;
            }
            executeFast11.dispose();
            queryFinalized4.dispose();
            int i17 = 0;
            SQLiteCursor queryFinalized5 = messagesStorage.database.queryFinalized("SELECT uid, mid FROM unread_push_messages WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast12 = messagesStorage.database.executeFast("UPDATE unread_push_messages SET mid = ? WHERE uid = ? AND mid = ?");
            while (queryFinalized5.next()) {
                long longValue13 = queryFinalized5.longValue(i17);
                int intValue29 = queryFinalized5.intValue(1);
                executeFast12.requery();
                executeFast12.bindInteger(1, intValue29);
                executeFast12.bindLong(2, longValue13);
                executeFast12.bindInteger(3, intValue29);
                executeFast12.step();
                i17 = 0;
            }
            executeFast12.dispose();
            queryFinalized5.dispose();
            if (arrayList != null) {
                SQLitePreparedStatement executeFast13 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                SQLitePreparedStatement executeFast14 = messagesStorage.database.executeFast("UPDATE dialog_filter_pin_v2 SET peer = ? WHERE peer = ?");
                SQLitePreparedStatement executeFast15 = messagesStorage.database.executeFast("UPDATE dialog_filter_ep SET peer = ? WHERE peer = ?");
                int size = arrayList.size();
                for (int i18 = 0; i18 < size; i18++) {
                    long intValue30 = ((Integer) arrayList.get(i18)).intValue();
                    long makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(intValue30);
                    long j = intValue30 << 32;
                    executeFast13.requery();
                    executeFast13.bindLong(1, makeEncryptedDialogId);
                    executeFast13.bindLong(2, j);
                    executeFast13.step();
                    executeFast14.requery();
                    executeFast14.bindLong(1, makeEncryptedDialogId);
                    executeFast14.bindLong(2, j);
                    executeFast14.step();
                    executeFast15.requery();
                    executeFast15.bindLong(1, makeEncryptedDialogId);
                    executeFast15.bindLong(2, j);
                    executeFast15.step();
                }
                executeFast13.dispose();
                executeFast14.dispose();
                executeFast15.dispose();
            }
            if (arrayList2 != null) {
                SQLitePreparedStatement executeFast16 = messagesStorage.database.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                int size2 = arrayList2.size();
                for (int i19 = 0; i19 < size2; i19++) {
                    int intValue31 = ((Integer) arrayList2.get(i19)).intValue();
                    long makeFolderDialogId = DialogObject.makeFolderDialogId(intValue31);
                    executeFast16.requery();
                    executeFast16.bindLong(1, makeFolderDialogId);
                    executeFast16.bindLong(2, 8589934592L | intValue31);
                    executeFast16.step();
                }
                executeFast16.dispose();
            }
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_read_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS mid_out_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS task_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages2;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mention_idx_messages;").stepThis().dispose();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS messages;").stepThis().dispose();
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("PRAGMA user_version = 84").stepThis().dispose();
            i4 = 84;
        }
        if (i4 == 84) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
            messagesStorage.database.beginTransaction();
            try {
                sQLiteCursor = messagesStorage.database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v3 WHERE 1", new Object[0]);
            } catch (Exception e8) {
                FileLog.e(e8);
                sQLiteCursor = null;
            }
            if (sQLiteCursor != null) {
                SQLitePreparedStatement executeFast17 = messagesStorage.database.executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue9 = sQLiteCursor.byteBufferValue(4);
                    if (byteBufferValue9 != null) {
                        int intValue32 = sQLiteCursor.intValue(0);
                        long longValue14 = sQLiteCursor.longValue(1);
                        if (((int) longValue14) == 0) {
                            longValue14 = DialogObject.makeEncryptedDialogId((int) (longValue14 >> 32));
                        }
                        int intValue33 = sQLiteCursor.intValue(2);
                        int intValue34 = sQLiteCursor.intValue(3);
                        executeFast17.requery();
                        executeFast17.bindInteger(1, intValue32);
                        executeFast17.bindLong(2, longValue14);
                        executeFast17.bindInteger(3, intValue33);
                        executeFast17.bindInteger(4, intValue34);
                        executeFast17.bindByteBuffer(5, byteBufferValue9);
                        executeFast17.step();
                        byteBufferValue9.reuse();
                    }
                }
                sQLiteCursor.dispose();
                executeFast17.dispose();
            }
            messagesStorage.database.commitTransaction();
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS media_v3;").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 85").stepThis().dispose();
            i4 = 85;
        }
        if (i4 == 85) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.executeNoException("ALTER TABLE scheduled_messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.executeNoException("UPDATE messages_v2 SET replydata = NULL");
            messagesStorage.executeNoException("UPDATE scheduled_messages_v2 SET replydata = NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 86").stepThis().dispose();
            i4 = 86;
        }
        if (i4 == 86) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 87").stepThis().dispose();
            i4 = 87;
        }
        if (i4 == 87) {
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_reactions INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER PRIMARY KEY, state INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 88").stepThis().dispose();
            i4 = 88;
        }
        if (i4 == 88 || i4 == 89) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS reaction_mentions;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(dialog_id, message_id));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
            messagesStorage.database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media_v3").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 90").stepThis().dispose();
            i4 = 90;
        }
        if (i4 == 90 || i4 == 91) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS downloading_documents;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 92").stepThis().dispose();
            i4 = 92;
        }
        if (i4 == 92) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 93").stepThis().dispose();
            i4 = 95;
        }
        if (i4 == 95 || i4 == 93) {
            messagesStorage.executeNoException("ALTER TABLE messages_v2 ADD COLUMN custom_params BLOB default NULL");
            messagesStorage.database.executeFast("PRAGMA user_version = 96").stepThis().dispose();
            i4 = 96;
        }
        if (i4 == 96) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("UPDATE stickers_v2 SET date = 0");
            messagesStorage.database.executeFast("PRAGMA user_version = 97").stepThis().dispose();
            i4 = 97;
        }
        if (i4 == 97) {
            messagesStorage.database.executeFast("DROP TABLE IF EXISTS stickers_featured;").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 98").stepThis().dispose();
            i4 = 98;
        }
        if (i4 == 98) {
            messagesStorage.database.executeFast("CREATE TABLE animated_emoji(document_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 99").stepThis().dispose();
            i4 = 99;
        }
        if (i4 == 99) {
            messagesStorage.database.executeFast("ALTER TABLE stickers_featured ADD COLUMN emoji INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 100").stepThis().dispose();
            i4 = 100;
        }
        if (i4 == 100) {
            messagesStorage.database.executeFast("CREATE TABLE emoji_statuses(data BLOB, type INTEGER);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 101").stepThis().dispose();
            i4 = 101;
        }
        if (i4 == 101) {
            messagesStorage.database.executeFast("ALTER TABLE messages_v2 ADD COLUMN group_id INTEGER default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_group INTEGER default NULL").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_groupid_messages_v2 ON messages_v2(uid, mid, group_id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 102").stepThis().dispose();
            i4 = 102;
        }
        if (i4 == 102) {
            messagesStorage.database.executeFast("CREATE TABLE messages_holes_topics(uid INTEGER, topic_id INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes_topics(uid, topic_id, end);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE messages_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, PRIMARY KEY(mid, topic_id, uid))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_topics ON messages_topics(uid, mid, read_state, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_topics ON messages_topics(uid, date, mid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_topics ON messages_topics(mid, out);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_topics ON messages_topics(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_topics ON messages_topics(mid, send_state, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_topics ON messages_topics(uid, mention, read_state);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_topics ON messages_topics(mid, is_channel);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_topics ON messages_topics(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, topic_id, uid);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE media_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, topic_id, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_topics ON media_topics(uid, topic_id, mid, type, date);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE media_holes_topics(uid INTEGER, topic_id INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, type, start));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_topics ON media_holes_topics(uid, topic_id, type, end);").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE topics(did INTEGER, topic_id INTEGER, data BLOB, top_message INTEGER, topic_message BLOB, unread_count INTEGER, max_read_id INTEGER, unread_mentions INTEGER, unread_reactions INTEGER, PRIMARY KEY(did, topic_id));").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS did_top_message_topics ON topics(did, top_message);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 103").stepThis().dispose();
            i4 = 103;
        }
        if (i4 == 103) {
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_topics(uid INTEGER, topic_id INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, topic_id, type))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions_topics(message_id INTEGER, state INTEGER, dialog_id INTEGER, topic_id INTEGER, PRIMARY KEY(message_id, dialog_id, topic_id))").stepThis().dispose();
            messagesStorage.database.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_topics_did ON reaction_mentions_topics(dialog_id, topic_id);").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 104").stepThis().dispose();
            i4 = 104;
        }
        if (i4 == 104) {
            messagesStorage.database.executeFast("ALTER TABLE topics ADD COLUMN read_outbox INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 105").stepThis().dispose();
            i4 = 105;
        }
        if (i4 == 105) {
            messagesStorage.database.executeFast("ALTER TABLE topics ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            messagesStorage.database.executeFast("PRAGMA user_version = 106").stepThis().dispose();
        }
        FileLog.d("MessagesStorage db migration finished");
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDbToLastVersion$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDbToLastVersion$3() {
        this.databaseMigrationInProgress = true;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.TRUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDbToLastVersion$4() {
        this.databaseMigrationInProgress = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
    }

    private void executeNoException(String str) {
        try {
            this.database.executeFast(str).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void cleanupInternal(boolean z) {
        this.lastDateValue = 0;
        this.lastSeqValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSecretVersion = 0;
        this.mainUnreadCount = 0;
        this.archiveUnreadCount = 0;
        this.pendingMainUnreadCount = 0;
        this.pendingArchiveUnreadCount = 0;
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.unknownDialogsIds.clear();
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        SQLiteDatabase sQLiteDatabase = this.database;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
            this.database = null;
        }
        if (z) {
            File file = this.cacheFile;
            if (file != null) {
                file.delete();
                this.cacheFile = null;
            }
            File file2 = this.walCacheFile;
            if (file2 != null) {
                file2.delete();
                this.walCacheFile = null;
            }
            File file3 = this.shmCacheFile;
            if (file3 == null) {
                return;
            }
            file3.delete();
            this.shmCacheFile = null;
        }
    }

    public void cleanup(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda194
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$cleanup$6(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$6(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$cleanup$5();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$5() {
        getMessagesController().getDifference();
    }

    public void saveSecretParams(final int i, final int i2, final byte[] bArr) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveSecretParams$7(i, i2, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveSecretParams$7(int i, int i2, byte[] bArr) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            int i3 = 1;
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            if (bArr != null) {
                i3 = bArr.length;
            }
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
            if (bArr != null) {
                nativeByteBuffer.writeBytes(bArr);
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$fixNotificationSettings$8();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fixNotificationSettings$8() {
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map<String, ?> all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("notify2_")) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() == 2 || num.intValue() == 3) {
                        String replace = key.replace("notify2_", "");
                        long j = 1;
                        if (num.intValue() != 2) {
                            Integer num2 = (Integer) all.get("notifyuntil_" + replace);
                            if (num2 != null) {
                                j = 1 | (num2.intValue() << 32);
                            }
                        }
                        try {
                            longSparseArray.put(Long.parseLong(replace), Long.valueOf(j));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    executeFast.requery();
                    executeFast.bindLong(1, longSparseArray.keyAt(i));
                    executeFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public long createPendingTask(final NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0L;
        }
        final long andAdd = this.lastTaskId.getAndAdd(1L);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda111
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createPendingTask$9(andAdd, nativeByteBuffer);
            }
        });
        return andAdd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPendingTask$9(long j, NativeByteBuffer nativeByteBuffer) {
        try {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
                executeFast.bindLong(1, j);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            nativeByteBuffer.reuse();
        }
    }

    public void removePendingTask(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda67
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$removePendingTask$10(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removePendingTask$10(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM pending_tasks WHERE id = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadPendingTasks$30();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$30() {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                final long longValue = queryFinalized.longValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int readInt32 = byteBufferValue.readInt32(false);
                    if (readInt32 != 100) {
                        switch (readInt32) {
                            case 0:
                                final TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize != null) {
                                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda168
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$11(TLdeserialize, longValue);
                                        }
                                    });
                                    break;
                                }
                                break;
                            case 1:
                                final long readInt322 = byteBufferValue.readInt32(false);
                                final int readInt323 = byteBufferValue.readInt32(false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda76
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$12(readInt322, readInt323, longValue);
                                    }
                                });
                                break;
                            case 2:
                            case 5:
                            case 8:
                            case 10:
                            case 14:
                                final TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                                tLRPC$TL_dialog.id = byteBufferValue.readInt64(false);
                                tLRPC$TL_dialog.top_message = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.read_inbox_max_id = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.read_outbox_max_id = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.unread_count = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.last_message_date = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.pts = byteBufferValue.readInt32(false);
                                tLRPC$TL_dialog.flags = byteBufferValue.readInt32(false);
                                if (readInt32 >= 5) {
                                    tLRPC$TL_dialog.pinned = byteBufferValue.readBool(false);
                                    tLRPC$TL_dialog.pinnedNum = byteBufferValue.readInt32(false);
                                }
                                if (readInt32 >= 8) {
                                    tLRPC$TL_dialog.unread_mentions_count = byteBufferValue.readInt32(false);
                                }
                                if (readInt32 >= 10) {
                                    tLRPC$TL_dialog.unread_mark = byteBufferValue.readBool(false);
                                }
                                if (readInt32 >= 14) {
                                    tLRPC$TL_dialog.folder_id = byteBufferValue.readInt32(false);
                                }
                                final TLRPC$InputPeer TLdeserialize2 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda173
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$13(tLRPC$TL_dialog, TLdeserialize2, longValue);
                                    }
                                });
                                break;
                            case 3:
                                getSendMessagesHelper().sendGame(TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), (TLRPC$TL_inputMediaGame) TLRPC$InputMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), byteBufferValue.readInt64(false), longValue);
                                break;
                            case 4:
                                final long readInt64 = byteBufferValue.readInt64(false);
                                final boolean readBool = byteBufferValue.readBool(false);
                                final TLRPC$InputPeer TLdeserialize3 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda121
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$14(readInt64, readBool, TLdeserialize3, longValue);
                                    }
                                });
                                break;
                            case 6:
                                final long readInt324 = byteBufferValue.readInt32(false);
                                final int readInt325 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize4 = TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda78
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$15(readInt324, readInt325, longValue, TLdeserialize4);
                                    }
                                });
                                break;
                            case 7:
                                final long readInt326 = byteBufferValue.readInt32(false);
                                int readInt327 = byteBufferValue.readInt32(false);
                                TLObject TLdeserialize5 = TLRPC$TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt327, false);
                                final TLObject TLdeserialize6 = TLdeserialize5 == null ? TLRPC$TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt327, false) : TLdeserialize5;
                                if (TLdeserialize6 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda96
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$17(readInt326, longValue, TLdeserialize6);
                                        }
                                    });
                                    break;
                                }
                            case 9:
                                final long readInt642 = byteBufferValue.readInt64(false);
                                final TLRPC$InputPeer TLdeserialize7 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda112
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$19(readInt642, TLdeserialize7, longValue);
                                    }
                                });
                                break;
                            case 11:
                                final int readInt328 = byteBufferValue.readInt32(false);
                                final long readInt329 = byteBufferValue.readInt32(false);
                                final int readInt3210 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize8 = readInt329 != 0 ? TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false) : null;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda80
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$20(readInt329, readInt328, TLdeserialize8, readInt3210, longValue);
                                    }
                                });
                                break;
                            case 12:
                            case 19:
                            case 20:
                                removePendingTask(longValue);
                                break;
                            case 13:
                                final long readInt643 = byteBufferValue.readInt64(false);
                                final boolean readBool2 = byteBufferValue.readBool(false);
                                final int readInt3211 = byteBufferValue.readInt32(false);
                                final int readInt3212 = byteBufferValue.readInt32(false);
                                final boolean readBool3 = byteBufferValue.readBool(false);
                                final TLRPC$InputPeer TLdeserialize9 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda120
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$23(readInt643, readBool2, readInt3211, readInt3212, readBool3, TLdeserialize9, longValue);
                                    }
                                });
                                break;
                            case 15:
                                final TLRPC$InputPeer TLdeserialize10 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda180
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$24(TLdeserialize10, longValue);
                                    }
                                });
                                break;
                            case 16:
                                final int readInt3213 = byteBufferValue.readInt32(false);
                                int readInt3214 = byteBufferValue.readInt32(false);
                                final ArrayList arrayList = new ArrayList();
                                for (int i = 0; i < readInt3214; i++) {
                                    arrayList.add(TLRPC$InputDialogPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda61
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$25(readInt3213, arrayList, longValue);
                                    }
                                });
                                break;
                            case 17:
                                final int readInt3215 = byteBufferValue.readInt32(false);
                                int readInt3216 = byteBufferValue.readInt32(false);
                                final ArrayList arrayList2 = new ArrayList();
                                for (int i2 = 0; i2 < readInt3216; i2++) {
                                    arrayList2.add(TLRPC$TL_inputFolderPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda60
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$26(readInt3215, arrayList2, longValue);
                                    }
                                });
                                break;
                            case 18:
                                final long readInt644 = byteBufferValue.readInt64(false);
                                byteBufferValue.readInt32(false);
                                final TLRPC$TL_messages_deleteScheduledMessages TLdeserialize11 = TLRPC$TL_messages_deleteScheduledMessages.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                if (TLdeserialize11 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda94
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$27(readInt644, longValue, TLdeserialize11);
                                        }
                                    });
                                    break;
                                }
                            case 21:
                                final Theme.OverrideWallpaperInfo overrideWallpaperInfo = new Theme.OverrideWallpaperInfo();
                                byteBufferValue.readInt64(false);
                                overrideWallpaperInfo.isBlurred = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.isMotion = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.color = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.gradientColor1 = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.rotation = byteBufferValue.readInt32(false);
                                overrideWallpaperInfo.intensity = (float) byteBufferValue.readDouble(false);
                                final boolean readBool4 = byteBufferValue.readBool(false);
                                overrideWallpaperInfo.slug = byteBufferValue.readString(false);
                                overrideWallpaperInfo.originalFileName = byteBufferValue.readString(false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda193
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$22(overrideWallpaperInfo, readBool4, longValue);
                                    }
                                });
                                break;
                            case 22:
                                final TLRPC$InputPeer TLdeserialize12 = TLRPC$InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda181
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$28(TLdeserialize12, longValue);
                                    }
                                });
                                break;
                            case 23:
                                final long readInt645 = byteBufferValue.readInt64(false);
                                final int readInt3217 = byteBufferValue.readInt32(false);
                                final int readInt3218 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize13 = (DialogObject.isEncryptedDialog(readInt645) || !DialogObject.isChatDialog(readInt645) || !byteBufferValue.hasRemaining()) ? null : TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda81
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$21(readInt645, readInt3217, TLdeserialize13, readInt3218, longValue);
                                    }
                                });
                                break;
                            case 24:
                                final long readInt646 = byteBufferValue.readInt64(false);
                                int readInt3219 = byteBufferValue.readInt32(false);
                                TLObject TLdeserialize14 = TLRPC$TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt3219, false);
                                final TLObject TLdeserialize15 = TLdeserialize14 == null ? TLRPC$TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt3219, false) : TLdeserialize14;
                                if (TLdeserialize15 == null) {
                                    removePendingTask(longValue);
                                    break;
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda95
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            MessagesStorage.this.lambda$loadPendingTasks$18(readInt646, longValue, TLdeserialize15);
                                        }
                                    });
                                    break;
                                }
                            case 25:
                                final long readInt647 = byteBufferValue.readInt64(false);
                                final int readInt3220 = byteBufferValue.readInt32(false);
                                final TLRPC$InputChannel TLdeserialize16 = TLRPC$InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda79
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        MessagesStorage.this.lambda$loadPendingTasks$16(readInt647, readInt3220, longValue, TLdeserialize16);
                                    }
                                });
                                break;
                        }
                    } else {
                        final int readInt3221 = byteBufferValue.readInt32(false);
                        final boolean readBool5 = byteBufferValue.readBool(false);
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda63
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesStorage.this.lambda$loadPendingTasks$29(readInt3221, readBool5, longValue);
                            }
                        });
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$11(TLRPC$Chat tLRPC$Chat, long j) {
        getMessagesController().loadUnknownChannel(tLRPC$Chat, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$12(long j, int i, long j2) {
        getMessagesController().getChannelDifference(j, i, j2, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$13(TLRPC$Dialog tLRPC$Dialog, TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().checkLastDialogMessage(tLRPC$Dialog, tLRPC$InputPeer, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$14(long j, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().pinDialog(j, z, tLRPC$InputPeer, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$15(long j, int i, long j2, TLRPC$InputChannel tLRPC$InputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, tLRPC$InputChannel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$16(long j, int i, long j2, TLRPC$InputChannel tLRPC$InputChannel) {
        getMessagesController().getChannelDifference(j, i, j2, tLRPC$InputChannel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$17(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, -j, true, false, false, j2, tLObject);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$18(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, false, false, j2, tLObject);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$19(long j, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().markDialogAsUnread(j, tLRPC$InputPeer, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$20(long j, int i, TLRPC$InputChannel tLRPC$InputChannel, int i2, long j2) {
        getMessagesController().markMessageAsRead2(-j, i, tLRPC$InputChannel, i2, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$21(long j, int i, TLRPC$InputChannel tLRPC$InputChannel, int i2, long j2) {
        getMessagesController().markMessageAsRead2(j, i, tLRPC$InputChannel, i2, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$22(Theme.OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        getMessagesController().saveWallpaperToServer(null, overrideWallpaperInfo, z, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$23(long j, boolean z, int i, int i2, boolean z2, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        getMessagesController().deleteDialog(j, z ? 1 : 0, i, i2, z2, tLRPC$InputPeer, j2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$24(TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().loadUnknownDialog(tLRPC$InputPeer, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$25(int i, ArrayList arrayList, long j) {
        getMessagesController().reorderPinnedDialogs(i, arrayList, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$26(int i, ArrayList arrayList, long j) {
        getMessagesController().addDialogToFolder(null, i, -1, arrayList, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$27(long j, long j2, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, j, true, true, false, j2, tLObject);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$28(TLRPC$InputPeer tLRPC$InputPeer, long j) {
        getMessagesController().reloadMentionsCountForChannel(tLRPC$InputPeer, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPendingTasks$29(int i, boolean z, long j) {
        getSecretChatHelper().declineSecretChat(i, z, j);
    }

    public void saveChannelPts(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChannelPts$31(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveChannelPts$31(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, -j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: saveDiffParamsInternal */
    public void lambda$saveDiffParams$32(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq == i && this.lastSavedPts == i2 && this.lastSavedDate == i3 && this.lastQtsValue == i4) {
                return;
            }
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveDiffParams(final int i, final int i2, final int i3, final int i4) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDiffParams$32(i, i2, i3, i4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMutedDialogsFiltersCounters$33() {
        resetAllUnreadCounters(true);
    }

    public void updateMutedDialogsFiltersCounters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMutedDialogsFiltersCounters$33();
            }
        });
    }

    public void setDialogFlags(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda89
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogFlags$34(j, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogFlags$34(long j, long j2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT flags FROM dialog_settings WHERE did = " + j, new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
            queryFinalized.dispose();
            if (j2 == intValue) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            resetAllUnreadCounters(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(final MessageObject messageObject) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda161
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putPushMessage$35(messageObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putPushMessage$35(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            int i = 0;
            if (messageObject.localType == 2) {
                i = 1;
            }
            if (messageObject.localChannel) {
                i |= 2;
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, messageObject.getDialogId());
            executeFast.bindInteger(2, messageObject.getId());
            executeFast.bindLong(3, messageObject.messageOwner.random_id);
            executeFast.bindInteger(4, messageObject.messageOwner.date);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            CharSequence charSequence = messageObject.messageText;
            if (charSequence == null) {
                executeFast.bindNull(6);
            } else {
                executeFast.bindString(6, charSequence.toString());
            }
            String str = messageObject.localName;
            if (str == null) {
                executeFast.bindNull(7);
            } else {
                executeFast.bindString(7, str);
            }
            String str2 = messageObject.localUserName;
            if (str2 == null) {
                executeFast.bindNull(8);
            } else {
                executeFast.bindString(8, str2);
            }
            executeFast.bindInteger(9, i);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearLocalDatabase() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearLocalDatabase$37();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(3:(4:7|8|(3:10|11|12)(1:14)|13)|4|5) */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x038f, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0390, code lost:
        r3 = null;
        r7 = null;
        r19 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0396, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:108:0x0397, code lost:
        r3 = null;
        r7 = null;
        r19 = r5;
     */
    /* JADX WARN: Removed duplicated region for block: B:116:0x03b1  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x03b6  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x03bb  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x03c0  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x03d1  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x03d6  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x03db  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x03e0  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x02e3 A[Catch: all -> 0x02fd, Exception -> 0x0300, TryCatch #12 {Exception -> 0x0300, blocks: (B:60:0x020a, B:61:0x020d, B:63:0x02e3, B:65:0x02f2), top: B:147:0x020a }] */
    /* JADX WARN: Type inference failed for: r6v12, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r6v19 */
    /* JADX WARN: Type inference failed for: r6v22 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$clearLocalDatabase$37() {
        /*
            Method dump skipped, instructions count: 1006
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$clearLocalDatabase$37():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearLocalDatabase$36() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didClearDatabase, new Object[0]);
        getMediaDataController().loadAttachMenuBots(false, true);
    }

    public void saveTopics(final long j, final List<TLRPC$TL_forumTopic> list, final boolean z, boolean z2) {
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda106
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$saveTopics$38(j, list, z);
                }
            });
        } else {
            saveTopicsInternal(j, list, z, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveTopics$38(long j, List list, boolean z) {
        saveTopicsInternal(j, list, z, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x00ad, code lost:
        if (r0 != null) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00b6, code lost:
        if (r0 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00b8, code lost:
        r0.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00bb, code lost:
        r7.database.commitTransaction();
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00c0, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void saveTopicsInternal(long r8, java.util.List<org.telegram.tgnet.TLRPC$TL_forumTopic> r10, boolean r11, boolean r12) {
        /*
            r7 = this;
            r0 = 0
            if (r11 == 0) goto L21
            org.telegram.SQLite.SQLiteDatabase r11 = r7.database     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r1.<init>()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            java.lang.String r2 = "DELETE FROM topics WHERE did = "
            r1.append(r2)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r1.append(r8)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.SQLite.SQLitePreparedStatement r11 = r11.executeFast(r1)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.SQLite.SQLitePreparedStatement r11 = r11.stepThis()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r11.dispose()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
        L21:
            org.telegram.SQLite.SQLiteDatabase r11 = r7.database     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            java.lang.String r1 = "REPLACE INTO topics VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r11.executeFast(r1)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            if (r12 == 0) goto L30
            org.telegram.SQLite.SQLiteDatabase r11 = r7.database     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r11.beginTransaction()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
        L30:
            r11 = 0
            r12 = 0
        L32:
            int r1 = r10.size()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            if (r12 >= r1) goto Lad
            java.lang.Object r1 = r10.get(r12)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.tgnet.TLRPC$TL_forumTopic r1 = (org.telegram.tgnet.TLRPC$TL_forumTopic) r1     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.requery()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r2 = 1
            r0.bindLong(r2, r8)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r3 = 2
            int r4 = r1.id     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r3, r4)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            int r4 = r1.getObjectSize()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r3.<init>(r4)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r1.serializeToStream(r3)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r4 = 3
            r0.bindByteBuffer(r4, r3)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r4 = 4
            int r5 = r1.top_message     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r4, r5)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.tgnet.TLRPC$Message r5 = r1.topicStartMessage     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            int r5 = r5.getObjectSize()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r4.<init>(r5)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            org.telegram.tgnet.TLRPC$Message r5 = r1.topicStartMessage     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5.serializeToStream(r4)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 5
            r0.bindByteBuffer(r5, r4)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 6
            int r6 = r1.unread_count     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r5, r6)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 7
            int r6 = r1.read_inbox_max_id     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r5, r6)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 8
            int r6 = r1.unread_mentions_count     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r5, r6)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 9
            int r6 = r1.unread_reactions_count     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r5, r6)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 10
            int r6 = r1.read_outbox_max_id     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.bindInteger(r5, r6)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r5 = 11
            boolean r1 = r1.pinned     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            if (r1 == 0) goto L9d
            goto L9e
        L9d:
            r2 = 0
        L9e:
            r0.bindInteger(r5, r2)     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r0.step()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r4.reuse()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            r3.reuse()     // Catch: java.lang.Throwable -> Lb0 java.lang.Exception -> Lb2
            int r12 = r12 + 1
            goto L32
        Lad:
            if (r0 == 0) goto Lbb
            goto Lb8
        Lb0:
            r8 = move-exception
            goto Lc1
        Lb2:
            r8 = move-exception
            org.telegram.messenger.FileLog.e(r8)     // Catch: java.lang.Throwable -> Lb0
            if (r0 == 0) goto Lbb
        Lb8:
            r0.dispose()
        Lbb:
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database
            r8.commitTransaction()
            return
        Lc1:
            if (r0 == 0) goto Lc6
            r0.dispose()
        Lc6:
            org.telegram.SQLite.SQLiteDatabase r9 = r7.database
            r9.commitTransaction()
            goto Lcd
        Lcc:
            throw r8
        Lcd:
            goto Lcc
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.saveTopicsInternal(long, java.util.List, boolean, boolean):void");
    }

    public void updateTopicData(final long j, final TLRPC$TL_forumTopic tLRPC$TL_forumTopic, final int i) {
        if (tLRPC$TL_forumTopic == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda115
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateTopicData$39(j, tLRPC$TL_forumTopic, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTopicData$39(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, int i) {
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2;
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic2;
        NativeByteBuffer byteBufferValue;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                int i2 = 0;
                sQLiteCursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(tLRPC$TL_forumTopic.id)), new Object[0]);
                try {
                    if (!sQLiteCursor2.next() || (byteBufferValue = sQLiteCursor2.byteBufferValue(0)) == null) {
                        tLRPC$TL_forumTopic2 = null;
                    } else {
                        tLRPC$TL_forumTopic2 = TLRPC$TL_forumTopic.TLdeserialize((AbstractSerializedData) byteBufferValue, byteBufferValue.readInt32(true), true);
                        byteBufferValue.reuse();
                    }
                    sQLiteCursor2.dispose();
                    if (tLRPC$TL_forumTopic2 != null) {
                        if ((i & 1) != 0) {
                            tLRPC$TL_forumTopic2.title = tLRPC$TL_forumTopic.title;
                        }
                        if ((i & 2) != 0) {
                            tLRPC$TL_forumTopic2.icon_emoji_id = tLRPC$TL_forumTopic.icon_emoji_id;
                            tLRPC$TL_forumTopic2.flags |= 1;
                        }
                        if ((i & 4) != 0) {
                            tLRPC$TL_forumTopic2.pinned = tLRPC$TL_forumTopic.pinned;
                        }
                        boolean z = tLRPC$TL_forumTopic2.pinned;
                        if ((i & 8) != 0) {
                            tLRPC$TL_forumTopic2.closed = tLRPC$TL_forumTopic.closed;
                        }
                        SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE topics SET data = ?, pinned = ? WHERE did = ? AND topic_id = ?");
                        try {
                            this.database.beginTransaction();
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_forumTopic2.getObjectSize());
                            tLRPC$TL_forumTopic2.serializeToStream(nativeByteBuffer);
                            executeFast.bindByteBuffer(1, nativeByteBuffer);
                            if (z) {
                                i2 = 1;
                            }
                            executeFast.bindInteger(2, i2);
                            executeFast.bindLong(3, j);
                            executeFast.bindInteger(4, tLRPC$TL_forumTopic2.id);
                            executeFast.step();
                            nativeByteBuffer.reuse();
                            sQLitePreparedStatement = executeFast;
                        } catch (Exception e) {
                            e = e;
                            sQLiteCursor2 = null;
                            sQLitePreparedStatement = executeFast;
                            FileLog.e(e);
                            if (sQLitePreparedStatement != null) {
                                sQLitePreparedStatement.dispose();
                            }
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            this.database.commitTransaction();
                        } catch (Throwable th) {
                            th = th;
                            sQLiteCursor = null;
                            sQLitePreparedStatement = executeFast;
                            if (sQLitePreparedStatement != null) {
                                sQLitePreparedStatement.dispose();
                            }
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            this.database.commitTransaction();
                            throw th;
                        }
                    }
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteCursor2 = null;
        } catch (Throwable th3) {
            th = th3;
            sQLiteCursor = null;
        }
        this.database.commitTransaction();
    }

    public void loadTopics(final long j, final Consumer<ArrayList<TLRPC$TL_forumTopic>> consumer) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda107
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadTopics$41(j, consumer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0220  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x022e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadTopics$41(long r20, j$.util.function.Consumer r22) {
        /*
            Method dump skipped, instructions count: 564
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadTopics$41(long, j$.util.function.Consumer):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadTopics$40(ArrayList arrayList, ArrayList arrayList2) {
        if (!arrayList.isEmpty()) {
            getMessagesController().putUsers(arrayList, true);
        }
        if (!arrayList2.isEmpty()) {
            getMessagesController().putChats(arrayList2, true);
        }
    }

    public void removeTopic(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda71
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$removeTopic$42(j, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeTopic$42(long j, int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
            this.database.executeFast(String.format(locale, "DELETE FROM messages_topics WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void updateTopicsWithReadMessages(final HashMap<TopicKey, Integer> hashMap) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda159
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateTopicsWithReadMessages$43(hashMap);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateTopicsWithReadMessages$43(HashMap hashMap) {
        for (TopicKey topicKey : hashMap.keySet()) {
            try {
                this.database.executeFast(String.format(Locale.US, "UPDATE topics SET read_outbox = max((SELECT read_outbox FROM topics WHERE did = %d AND topic_id = %d), %d) WHERE did = %d AND topic_id = %d", Long.valueOf(topicKey.dialogId), Integer.valueOf(topicKey.topicId), Integer.valueOf(((Integer) hashMap.get(topicKey)).intValue()), Long.valueOf(topicKey.dialogId), Integer.valueOf(topicKey.topicId))).stepThis().dispose();
            } catch (SQLiteException e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReadDialog {
        public int date;
        public int lastMid;
        public int unreadCount;

        private ReadDialog() {
        }
    }

    public void readAllDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$readAllDialogs$45(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readAllDialogs$45(int i) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList<Long> arrayList = new ArrayList<>();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                final LongSparseArray longSparseArray = new LongSparseArray();
                if (i >= 0) {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0 AND folder_id = %1$d", Integer.valueOf(i)), new Object[0]);
                } else {
                    queryFinalized = this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count > 0", new Object[0]);
                }
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (!DialogObject.isFolderDialogId(longValue)) {
                            ReadDialog readDialog = new ReadDialog();
                            readDialog.lastMid = queryFinalized.intValue(1);
                            readDialog.unreadCount = queryFinalized.intValue(2);
                            readDialog.date = queryFinalized.intValue(3);
                            longSparseArray.put(longValue, readDialog);
                            if (!DialogObject.isEncryptedDialog(longValue)) {
                                if (DialogObject.isChatDialog(longValue)) {
                                    long j = -longValue;
                                    if (!arrayList2.contains(Long.valueOf(j))) {
                                        arrayList2.add(Long.valueOf(j));
                                    }
                                } else if (!arrayList.contains(Long.valueOf(longValue))) {
                                    arrayList.add(Long.valueOf(longValue));
                                }
                            } else {
                                int encryptedChatId = DialogObject.getEncryptedChatId(longValue);
                                if (!arrayList3.contains(Integer.valueOf(encryptedChatId))) {
                                    arrayList3.add(Integer.valueOf(encryptedChatId));
                                }
                            }
                        }
                    } catch (Exception e) {
                        sQLiteCursor = queryFinalized;
                        e = e;
                        FileLog.e(e);
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                        return;
                    } catch (Throwable th) {
                        sQLiteCursor = queryFinalized;
                        th = th;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                queryFinalized.dispose();
                final ArrayList<TLRPC$User> arrayList4 = new ArrayList<>();
                final ArrayList<TLRPC$Chat> arrayList5 = new ArrayList<>();
                final ArrayList<TLRPC$EncryptedChat> arrayList6 = new ArrayList<>();
                if (!arrayList3.isEmpty()) {
                    getEncryptedChatsInternal(TextUtils.join(",", arrayList3), arrayList6, arrayList);
                }
                if (!arrayList.isEmpty()) {
                    getUsersInternal(TextUtils.join(",", arrayList), arrayList4);
                }
                if (!arrayList2.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", arrayList2), arrayList5);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda155
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$readAllDialogs$44(arrayList4, arrayList5, arrayList6, longSparseArray);
                    }
                });
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readAllDialogs$44(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            MessagesController messagesController = getMessagesController();
            int i2 = readDialog.lastMid;
            messagesController.markDialogAsRead(keyAt, i2, i2, readDialog.date, false, 0, readDialog.unreadCount, true, 0);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0089  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x03eb  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00a1  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00b5 A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00da A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00f5 A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01a5  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01ae A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01bd A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01d5 A[Catch: all -> 0x03da, Exception -> 0x03dd, TryCatch #3 {Exception -> 0x03dd, blocks: (B:4:0x002d, B:6:0x0035, B:8:0x005d, B:14:0x006d, B:18:0x008c, B:22:0x00a2, B:24:0x00b5, B:26:0x00bd, B:27:0x00c2, B:29:0x00da, B:31:0x00ea, B:33:0x00f5, B:35:0x0100, B:37:0x0125, B:39:0x012c, B:72:0x01a8, B:74:0x01ae, B:76:0x01b4, B:77:0x01b7, B:79:0x01bd, B:81:0x01cd, B:82:0x01d5, B:84:0x01dd, B:86:0x01e7, B:87:0x01ef, B:89:0x01fa, B:69:0x019a, B:70:0x019e, B:91:0x0208), top: B:192:0x002d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.tgnet.TLRPC$messages_Dialogs loadDialogsByIds(java.lang.String r20, java.util.ArrayList<java.lang.Long> r21, java.util.ArrayList<java.lang.Long> r22, java.util.ArrayList<java.lang.Integer> r23) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 1009
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadDialogsByIds(java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList):org.telegram.tgnet.TLRPC$messages_Dialogs");
    }

    private void loadDialogFilters() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadDialogFilters$47();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:101:0x0261  */
    /* JADX WARN: Removed duplicated region for block: B:126:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0250  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0255  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x025c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadDialogFilters$47() {
        /*
            Method dump skipped, instructions count: 615
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadDialogFilters$47():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadDialogFilters$46(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:232:0x04ce, code lost:
        if (r13.indexOfKey(r8.id) >= 0) goto L249;
     */
    /* JADX WARN: Removed duplicated region for block: B:103:0x02ba A[Catch: all -> 0x0656, Exception -> 0x065a, TryCatch #5 {Exception -> 0x065a, all -> 0x0656, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:32:0x00e2, B:35:0x0108, B:37:0x0118, B:39:0x013d, B:43:0x014c, B:44:0x0153, B:46:0x0157, B:54:0x0180, B:47:0x0162, B:49:0x0166, B:52:0x016b, B:53:0x0176, B:56:0x0196, B:58:0x01a4, B:60:0x01bb, B:62:0x01cb, B:63:0x01dd, B:65:0x01e6, B:83:0x0255, B:68:0x01fb, B:70:0x0213, B:74:0x0222, B:75:0x0229, B:77:0x022d, B:80:0x0232, B:82:0x0247, B:81:0x023d, B:86:0x0263, B:88:0x0269, B:90:0x027b, B:92:0x0287, B:95:0x028f, B:97:0x02a4, B:103:0x02ba, B:107:0x02c8, B:108:0x02d0, B:110:0x02d6, B:112:0x02da, B:114:0x02ef, B:116:0x030c, B:113:0x02e5, B:115:0x02f7, B:117:0x0315, B:118:0x031e, B:121:0x0324, B:124:0x0334, B:134:0x034e, B:136:0x0353, B:138:0x0358, B:140:0x0365, B:143:0x036f, B:145:0x0374, B:147:0x0382, B:149:0x0389, B:151:0x038e, B:153:0x0393, B:155:0x03a0, B:156:0x03a6, B:158:0x03ab, B:160:0x03b9, B:161:0x03be, B:163:0x03c3, B:165:0x03c8, B:167:0x03d5, B:168:0x03db, B:170:0x03e0, B:172:0x03ee, B:173:0x03f3, B:175:0x03f8, B:177:0x03fd, B:179:0x040a, B:180:0x0410, B:182:0x0415, B:184:0x0423, B:185:0x0428, B:187:0x042d, B:189:0x0432, B:191:0x043f, B:192:0x0445, B:194:0x044a, B:196:0x0458, B:199:0x0461, B:201:0x046a, B:208:0x048b, B:215:0x04a4, B:217:0x04a8, B:225:0x04b9, B:227:0x04bc, B:241:0x04e6, B:229:0x04c1, B:231:0x04c6, B:233:0x04d0, B:235:0x04d5, B:237:0x04da, B:218:0x04ab, B:220:0x04af, B:223:0x04b4, B:224:0x04b7, B:212:0x049a, B:266:0x0540, B:243:0x04f3, B:245:0x0502, B:247:0x0508, B:249:0x050c, B:251:0x0511, B:253:0x0514, B:254:0x0517, B:256:0x051c, B:258:0x0525, B:261:0x0531, B:263:0x0536, B:250:0x050f, B:267:0x054d, B:269:0x055a, B:277:0x0577, B:284:0x0590, B:286:0x0594, B:294:0x05a5, B:296:0x05a8, B:298:0x05ad, B:302:0x05ba, B:304:0x05bf, B:306:0x05c7, B:309:0x05ce, B:287:0x0597, B:289:0x059b, B:292:0x05a0, B:293:0x05a3, B:281:0x0586, B:332:0x0622, B:311:0x05da, B:313:0x05e7, B:315:0x05ed, B:317:0x05f1, B:319:0x05f6, B:321:0x05f9, B:323:0x05fe, B:325:0x0607, B:327:0x060c, B:329:0x0615, B:331:0x0620, B:318:0x05f4, B:333:0x062a, B:335:0x062e, B:339:0x0637, B:341:0x063b, B:342:0x063e, B:344:0x0642, B:346:0x0646, B:125:0x0337, B:127:0x033b, B:129:0x0343, B:130:0x0346, B:131:0x0348, B:132:0x034a), top: B:369:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:104:0x02c4  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x02c8 A[Catch: all -> 0x0656, Exception -> 0x065a, TryCatch #5 {Exception -> 0x065a, all -> 0x0656, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:32:0x00e2, B:35:0x0108, B:37:0x0118, B:39:0x013d, B:43:0x014c, B:44:0x0153, B:46:0x0157, B:54:0x0180, B:47:0x0162, B:49:0x0166, B:52:0x016b, B:53:0x0176, B:56:0x0196, B:58:0x01a4, B:60:0x01bb, B:62:0x01cb, B:63:0x01dd, B:65:0x01e6, B:83:0x0255, B:68:0x01fb, B:70:0x0213, B:74:0x0222, B:75:0x0229, B:77:0x022d, B:80:0x0232, B:82:0x0247, B:81:0x023d, B:86:0x0263, B:88:0x0269, B:90:0x027b, B:92:0x0287, B:95:0x028f, B:97:0x02a4, B:103:0x02ba, B:107:0x02c8, B:108:0x02d0, B:110:0x02d6, B:112:0x02da, B:114:0x02ef, B:116:0x030c, B:113:0x02e5, B:115:0x02f7, B:117:0x0315, B:118:0x031e, B:121:0x0324, B:124:0x0334, B:134:0x034e, B:136:0x0353, B:138:0x0358, B:140:0x0365, B:143:0x036f, B:145:0x0374, B:147:0x0382, B:149:0x0389, B:151:0x038e, B:153:0x0393, B:155:0x03a0, B:156:0x03a6, B:158:0x03ab, B:160:0x03b9, B:161:0x03be, B:163:0x03c3, B:165:0x03c8, B:167:0x03d5, B:168:0x03db, B:170:0x03e0, B:172:0x03ee, B:173:0x03f3, B:175:0x03f8, B:177:0x03fd, B:179:0x040a, B:180:0x0410, B:182:0x0415, B:184:0x0423, B:185:0x0428, B:187:0x042d, B:189:0x0432, B:191:0x043f, B:192:0x0445, B:194:0x044a, B:196:0x0458, B:199:0x0461, B:201:0x046a, B:208:0x048b, B:215:0x04a4, B:217:0x04a8, B:225:0x04b9, B:227:0x04bc, B:241:0x04e6, B:229:0x04c1, B:231:0x04c6, B:233:0x04d0, B:235:0x04d5, B:237:0x04da, B:218:0x04ab, B:220:0x04af, B:223:0x04b4, B:224:0x04b7, B:212:0x049a, B:266:0x0540, B:243:0x04f3, B:245:0x0502, B:247:0x0508, B:249:0x050c, B:251:0x0511, B:253:0x0514, B:254:0x0517, B:256:0x051c, B:258:0x0525, B:261:0x0531, B:263:0x0536, B:250:0x050f, B:267:0x054d, B:269:0x055a, B:277:0x0577, B:284:0x0590, B:286:0x0594, B:294:0x05a5, B:296:0x05a8, B:298:0x05ad, B:302:0x05ba, B:304:0x05bf, B:306:0x05c7, B:309:0x05ce, B:287:0x0597, B:289:0x059b, B:292:0x05a0, B:293:0x05a3, B:281:0x0586, B:332:0x0622, B:311:0x05da, B:313:0x05e7, B:315:0x05ed, B:317:0x05f1, B:319:0x05f6, B:321:0x05f9, B:323:0x05fe, B:325:0x0607, B:327:0x060c, B:329:0x0615, B:331:0x0620, B:318:0x05f4, B:333:0x062a, B:335:0x062e, B:339:0x0637, B:341:0x063b, B:342:0x063e, B:344:0x0642, B:346:0x0646, B:125:0x0337, B:127:0x033b, B:129:0x0343, B:130:0x0346, B:131:0x0348, B:132:0x034a), top: B:369:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0322  */
    /* JADX WARN: Removed duplicated region for block: B:199:0x0461 A[Catch: all -> 0x0656, Exception -> 0x065a, TryCatch #5 {Exception -> 0x065a, all -> 0x0656, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:32:0x00e2, B:35:0x0108, B:37:0x0118, B:39:0x013d, B:43:0x014c, B:44:0x0153, B:46:0x0157, B:54:0x0180, B:47:0x0162, B:49:0x0166, B:52:0x016b, B:53:0x0176, B:56:0x0196, B:58:0x01a4, B:60:0x01bb, B:62:0x01cb, B:63:0x01dd, B:65:0x01e6, B:83:0x0255, B:68:0x01fb, B:70:0x0213, B:74:0x0222, B:75:0x0229, B:77:0x022d, B:80:0x0232, B:82:0x0247, B:81:0x023d, B:86:0x0263, B:88:0x0269, B:90:0x027b, B:92:0x0287, B:95:0x028f, B:97:0x02a4, B:103:0x02ba, B:107:0x02c8, B:108:0x02d0, B:110:0x02d6, B:112:0x02da, B:114:0x02ef, B:116:0x030c, B:113:0x02e5, B:115:0x02f7, B:117:0x0315, B:118:0x031e, B:121:0x0324, B:124:0x0334, B:134:0x034e, B:136:0x0353, B:138:0x0358, B:140:0x0365, B:143:0x036f, B:145:0x0374, B:147:0x0382, B:149:0x0389, B:151:0x038e, B:153:0x0393, B:155:0x03a0, B:156:0x03a6, B:158:0x03ab, B:160:0x03b9, B:161:0x03be, B:163:0x03c3, B:165:0x03c8, B:167:0x03d5, B:168:0x03db, B:170:0x03e0, B:172:0x03ee, B:173:0x03f3, B:175:0x03f8, B:177:0x03fd, B:179:0x040a, B:180:0x0410, B:182:0x0415, B:184:0x0423, B:185:0x0428, B:187:0x042d, B:189:0x0432, B:191:0x043f, B:192:0x0445, B:194:0x044a, B:196:0x0458, B:199:0x0461, B:201:0x046a, B:208:0x048b, B:215:0x04a4, B:217:0x04a8, B:225:0x04b9, B:227:0x04bc, B:241:0x04e6, B:229:0x04c1, B:231:0x04c6, B:233:0x04d0, B:235:0x04d5, B:237:0x04da, B:218:0x04ab, B:220:0x04af, B:223:0x04b4, B:224:0x04b7, B:212:0x049a, B:266:0x0540, B:243:0x04f3, B:245:0x0502, B:247:0x0508, B:249:0x050c, B:251:0x0511, B:253:0x0514, B:254:0x0517, B:256:0x051c, B:258:0x0525, B:261:0x0531, B:263:0x0536, B:250:0x050f, B:267:0x054d, B:269:0x055a, B:277:0x0577, B:284:0x0590, B:286:0x0594, B:294:0x05a5, B:296:0x05a8, B:298:0x05ad, B:302:0x05ba, B:304:0x05bf, B:306:0x05c7, B:309:0x05ce, B:287:0x0597, B:289:0x059b, B:292:0x05a0, B:293:0x05a3, B:281:0x0586, B:332:0x0622, B:311:0x05da, B:313:0x05e7, B:315:0x05ed, B:317:0x05f1, B:319:0x05f6, B:321:0x05f9, B:323:0x05fe, B:325:0x0607, B:327:0x060c, B:329:0x0615, B:331:0x0620, B:318:0x05f4, B:333:0x062a, B:335:0x062e, B:339:0x0637, B:341:0x063b, B:342:0x063e, B:344:0x0642, B:346:0x0646, B:125:0x0337, B:127:0x033b, B:129:0x0343, B:130:0x0346, B:131:0x0348, B:132:0x034a), top: B:369:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:337:0x0633  */
    /* JADX WARN: Removed duplicated region for block: B:358:0x0662  */
    /* JADX WARN: Removed duplicated region for block: B:363:0x066a  */
    /* JADX WARN: Removed duplicated region for block: B:424:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0269 A[Catch: all -> 0x0656, Exception -> 0x065a, TryCatch #5 {Exception -> 0x065a, all -> 0x0656, blocks: (B:7:0x000a, B:8:0x002b, B:9:0x002e, B:32:0x00e2, B:35:0x0108, B:37:0x0118, B:39:0x013d, B:43:0x014c, B:44:0x0153, B:46:0x0157, B:54:0x0180, B:47:0x0162, B:49:0x0166, B:52:0x016b, B:53:0x0176, B:56:0x0196, B:58:0x01a4, B:60:0x01bb, B:62:0x01cb, B:63:0x01dd, B:65:0x01e6, B:83:0x0255, B:68:0x01fb, B:70:0x0213, B:74:0x0222, B:75:0x0229, B:77:0x022d, B:80:0x0232, B:82:0x0247, B:81:0x023d, B:86:0x0263, B:88:0x0269, B:90:0x027b, B:92:0x0287, B:95:0x028f, B:97:0x02a4, B:103:0x02ba, B:107:0x02c8, B:108:0x02d0, B:110:0x02d6, B:112:0x02da, B:114:0x02ef, B:116:0x030c, B:113:0x02e5, B:115:0x02f7, B:117:0x0315, B:118:0x031e, B:121:0x0324, B:124:0x0334, B:134:0x034e, B:136:0x0353, B:138:0x0358, B:140:0x0365, B:143:0x036f, B:145:0x0374, B:147:0x0382, B:149:0x0389, B:151:0x038e, B:153:0x0393, B:155:0x03a0, B:156:0x03a6, B:158:0x03ab, B:160:0x03b9, B:161:0x03be, B:163:0x03c3, B:165:0x03c8, B:167:0x03d5, B:168:0x03db, B:170:0x03e0, B:172:0x03ee, B:173:0x03f3, B:175:0x03f8, B:177:0x03fd, B:179:0x040a, B:180:0x0410, B:182:0x0415, B:184:0x0423, B:185:0x0428, B:187:0x042d, B:189:0x0432, B:191:0x043f, B:192:0x0445, B:194:0x044a, B:196:0x0458, B:199:0x0461, B:201:0x046a, B:208:0x048b, B:215:0x04a4, B:217:0x04a8, B:225:0x04b9, B:227:0x04bc, B:241:0x04e6, B:229:0x04c1, B:231:0x04c6, B:233:0x04d0, B:235:0x04d5, B:237:0x04da, B:218:0x04ab, B:220:0x04af, B:223:0x04b4, B:224:0x04b7, B:212:0x049a, B:266:0x0540, B:243:0x04f3, B:245:0x0502, B:247:0x0508, B:249:0x050c, B:251:0x0511, B:253:0x0514, B:254:0x0517, B:256:0x051c, B:258:0x0525, B:261:0x0531, B:263:0x0536, B:250:0x050f, B:267:0x054d, B:269:0x055a, B:277:0x0577, B:284:0x0590, B:286:0x0594, B:294:0x05a5, B:296:0x05a8, B:298:0x05ad, B:302:0x05ba, B:304:0x05bf, B:306:0x05c7, B:309:0x05ce, B:287:0x0597, B:289:0x059b, B:292:0x05a0, B:293:0x05a3, B:281:0x0586, B:332:0x0622, B:311:0x05da, B:313:0x05e7, B:315:0x05ed, B:317:0x05f1, B:319:0x05f6, B:321:0x05f9, B:323:0x05fe, B:325:0x0607, B:327:0x060c, B:329:0x0615, B:331:0x0620, B:318:0x05f4, B:333:0x062a, B:335:0x062e, B:339:0x0637, B:341:0x063b, B:342:0x063e, B:344:0x0642, B:346:0x0646, B:125:0x0337, B:127:0x033b, B:129:0x0343, B:130:0x0346, B:131:0x0348, B:132:0x034a), top: B:369:0x000a }] */
    /* JADX WARN: Type inference failed for: r5v24, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v79, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void calcUnreadCounters(boolean r25) {
        /*
            Method dump skipped, instructions count: 1648
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.calcUnreadCounters(boolean):void");
    }

    private void saveDialogFilterInternal(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        int i;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement;
        SQLitePreparedStatement sQLitePreparedStatement2;
        SQLitePreparedStatement sQLitePreparedStatement3 = null;
        try {
            try {
                if (!this.dialogFilters.contains(dialogFilter)) {
                    if (z) {
                        this.dialogFilters.add(0, dialogFilter);
                    } else {
                        this.dialogFilters.add(dialogFilter);
                    }
                    this.dialogFiltersMap.put(dialogFilter.id, dialogFilter);
                }
                executeFast = this.database.executeFast("REPLACE INTO dialog_filter VALUES(?, ?, ?, ?, ?)");
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            executeFast.bindInteger(1, dialogFilter.id);
            executeFast.bindInteger(2, dialogFilter.order);
            executeFast.bindInteger(3, dialogFilter.unreadCount);
            executeFast.bindInteger(4, dialogFilter.flags);
            executeFast.bindString(5, dialogFilter.id == 0 ? "ALL_CHATS" : dialogFilter.name);
            executeFast.step();
            executeFast.dispose();
            if (z2) {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialog_filter_pin_v2 VALUES(?, ?, ?)");
                int size = dialogFilter.alwaysShow.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long longValue = dialogFilter.alwaysShow.get(i2).longValue();
                    executeFast2.requery();
                    executeFast2.bindInteger(1, dialogFilter.id);
                    executeFast2.bindLong(2, longValue);
                    executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.get(longValue, Integer.MIN_VALUE));
                    executeFast2.step();
                }
                int size2 = dialogFilter.pinnedDialogs.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    long keyAt = dialogFilter.pinnedDialogs.keyAt(i3);
                    if (DialogObject.isEncryptedDialog(keyAt)) {
                        executeFast2.requery();
                        executeFast2.bindInteger(1, dialogFilter.id);
                        executeFast2.bindLong(2, keyAt);
                        executeFast2.bindInteger(3, dialogFilter.pinnedDialogs.valueAt(i3));
                        executeFast2.step();
                    }
                }
                executeFast2.dispose();
                SQLitePreparedStatement executeFast3 = this.database.executeFast("REPLACE INTO dialog_filter_ep VALUES(?, ?)");
                int size3 = dialogFilter.neverShow.size();
                for (i = 0; i < size3; i++) {
                    executeFast3.requery();
                    executeFast3.bindInteger(1, dialogFilter.id);
                    executeFast3.bindLong(2, dialogFilter.neverShow.get(i).longValue());
                    executeFast3.step();
                }
                executeFast3.dispose();
                this.database.commitTransaction();
            }
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 == null) {
                return;
            }
            sQLiteDatabase3.commitTransaction();
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement3 = sQLitePreparedStatement2;
            FileLog.e(e);
            SQLiteDatabase sQLiteDatabase4 = this.database;
            if (sQLiteDatabase4 != null) {
                sQLiteDatabase4.commitTransaction();
            }
            if (sQLitePreparedStatement3 == null) {
                return;
            }
            sQLitePreparedStatement3.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement3 = sQLitePreparedStatement;
            SQLiteDatabase sQLiteDatabase5 = this.database;
            if (sQLiteDatabase5 != null) {
                sQLiteDatabase5.commitTransaction();
            }
            if (sQLitePreparedStatement3 != null) {
                sQLitePreparedStatement3.dispose();
            }
            throw th;
        }
    }

    public void checkLoadedRemoteFilters(final TLRPC$Vector tLRPC$Vector) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda188
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkLoadedRemoteFilters$49(tLRPC$Vector);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0382 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:68:0x0177, B:67:0x0168, B:72:0x0189, B:74:0x0192, B:78:0x01ad, B:77:0x019f, B:83:0x01b8, B:87:0x01c1, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:95:0x01f3, B:99:0x01fc, B:98:0x01fa, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:154:0x0351, B:141:0x0310, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:144:0x031a, B:155:0x035d, B:88:0x01c4, B:84:0x01bb, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:224:0x0503, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:180:0x0413, B:184:0x041c, B:183:0x041a, B:194:0x0469, B:197:0x0470, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:221:0x04de, B:210:0x04a9, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:213:0x04b0, B:222:0x04e8, B:198:0x0473, B:195:0x046c, B:223:0x04fa, B:225:0x0519, B:228:0x052e), top: B:259:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:159:0x0391  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x039b A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:68:0x0177, B:67:0x0168, B:72:0x0189, B:74:0x0192, B:78:0x01ad, B:77:0x019f, B:83:0x01b8, B:87:0x01c1, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:95:0x01f3, B:99:0x01fc, B:98:0x01fa, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:154:0x0351, B:141:0x0310, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:144:0x031a, B:155:0x035d, B:88:0x01c4, B:84:0x01bb, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:224:0x0503, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:180:0x0413, B:184:0x041c, B:183:0x041a, B:194:0x0469, B:197:0x0470, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:221:0x04de, B:210:0x04a9, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:213:0x04b0, B:222:0x04e8, B:198:0x0473, B:195:0x046c, B:223:0x04fa, B:225:0x0519, B:228:0x052e), top: B:259:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:163:0x03a7  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x03ad A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:68:0x0177, B:67:0x0168, B:72:0x0189, B:74:0x0192, B:78:0x01ad, B:77:0x019f, B:83:0x01b8, B:87:0x01c1, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:95:0x01f3, B:99:0x01fc, B:98:0x01fa, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:154:0x0351, B:141:0x0310, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:144:0x031a, B:155:0x035d, B:88:0x01c4, B:84:0x01bb, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:224:0x0503, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:180:0x0413, B:184:0x041c, B:183:0x041a, B:194:0x0469, B:197:0x0470, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:221:0x04de, B:210:0x04a9, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:213:0x04b0, B:222:0x04e8, B:198:0x0473, B:195:0x046c, B:223:0x04fa, B:225:0x0519, B:228:0x052e), top: B:259:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:166:0x03b3  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03b7 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:68:0x0177, B:67:0x0168, B:72:0x0189, B:74:0x0192, B:78:0x01ad, B:77:0x019f, B:83:0x01b8, B:87:0x01c1, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:95:0x01f3, B:99:0x01fc, B:98:0x01fa, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:154:0x0351, B:141:0x0310, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:144:0x031a, B:155:0x035d, B:88:0x01c4, B:84:0x01bb, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:224:0x0503, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:180:0x0413, B:184:0x041c, B:183:0x041a, B:194:0x0469, B:197:0x0470, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:221:0x04de, B:210:0x04a9, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:213:0x04b0, B:222:0x04e8, B:198:0x0473, B:195:0x046c, B:223:0x04fa, B:225:0x0519, B:228:0x052e), top: B:259:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:169:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0192 A[Catch: Exception -> 0x05fc, TryCatch #1 {Exception -> 0x05fc, blocks: (B:10:0x0064, B:15:0x0087, B:19:0x008e, B:23:0x0095, B:27:0x009c, B:31:0x00a3, B:35:0x00aa, B:39:0x00b1, B:43:0x00b8, B:45:0x00c8, B:47:0x00d9, B:49:0x00e0, B:51:0x00e4, B:53:0x00ea, B:55:0x0109, B:57:0x011d, B:60:0x012d, B:62:0x013b, B:64:0x0153, B:68:0x0177, B:67:0x0168, B:72:0x0189, B:74:0x0192, B:78:0x01ad, B:77:0x019f, B:83:0x01b8, B:87:0x01c1, B:90:0x01c8, B:92:0x01da, B:94:0x01ee, B:100:0x0201, B:102:0x0209, B:103:0x020e, B:106:0x0218, B:108:0x0224, B:111:0x024b, B:113:0x025e, B:115:0x0264, B:117:0x026c, B:95:0x01f3, B:99:0x01fc, B:98:0x01fa, B:120:0x028e, B:121:0x0296, B:123:0x029c, B:125:0x02c9, B:127:0x02d2, B:129:0x02de, B:131:0x02e8, B:133:0x02ee, B:134:0x02f1, B:136:0x02f7, B:154:0x0351, B:141:0x0310, B:145:0x031c, B:147:0x0331, B:149:0x0337, B:150:0x033a, B:152:0x0342, B:144:0x031a, B:155:0x035d, B:88:0x01c4, B:84:0x01bb, B:156:0x0373, B:158:0x0382, B:160:0x0395, B:162:0x039b, B:165:0x03ad, B:168:0x03b7, B:224:0x0503, B:171:0x03cd, B:175:0x03f3, B:177:0x03fc, B:179:0x040e, B:185:0x0421, B:187:0x0429, B:188:0x042e, B:190:0x044b, B:191:0x0451, B:180:0x0413, B:184:0x041c, B:183:0x041a, B:194:0x0469, B:197:0x0470, B:199:0x0475, B:201:0x047c, B:203:0x0488, B:205:0x0492, B:206:0x0495, B:208:0x049b, B:221:0x04de, B:210:0x04a9, B:214:0x04b2, B:216:0x04c8, B:217:0x04cb, B:219:0x04d3, B:213:0x04b0, B:222:0x04e8, B:198:0x0473, B:195:0x046c, B:223:0x04fa, B:225:0x0519, B:228:0x052e), top: B:259:0x0064 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01b6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkLoadedRemoteFilters$49(org.telegram.tgnet.TLRPC$Vector r39) {
        /*
            Method dump skipped, instructions count: 1541
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkLoadedRemoteFilters$49(org.telegram.tgnet.TLRPC$Vector):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$checkLoadedRemoteFilters$48(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: processLoadedFilterPeersInternal */
    public void lambda$processLoadedFilterPeers$51(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList<TLRPC$User> arrayList, ArrayList<TLRPC$Chat> arrayList2, ArrayList<MessagesController.DialogFilter> arrayList3, SparseArray<MessagesController.DialogFilter> sparseArray, ArrayList<Integer> arrayList4, HashMap<Integer, HashSet<Long>> hashMap, HashMap<Integer, HashSet<Long>> hashMap2, HashSet<Integer> hashSet) {
        putUsersAndChats(arrayList, arrayList2, true, false);
        int size = sparseArray.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            lambda$deleteDialogFilter$52(sparseArray.valueAt(i));
            i++;
            z = true;
        }
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            MessagesController.DialogFilter dialogFilter = this.dialogFiltersMap.get(it.next().intValue());
            if (dialogFilter != null) {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry : hashMap2.entrySet()) {
            MessagesController.DialogFilter dialogFilter2 = this.dialogFiltersMap.get(entry.getKey().intValue());
            if (dialogFilter2 != null) {
                HashSet<Long> value = entry.getValue();
                dialogFilter2.alwaysShow.removeAll(value);
                dialogFilter2.neverShow.removeAll(value);
                z = true;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry2 : hashMap.entrySet()) {
            MessagesController.DialogFilter dialogFilter3 = this.dialogFiltersMap.get(entry2.getKey().intValue());
            if (dialogFilter3 != null) {
                Iterator<Long> it2 = entry2.getValue().iterator();
                while (it2.hasNext()) {
                    dialogFilter3.pinnedDialogs.delete(it2.next().longValue());
                }
                z = true;
            }
        }
        int size2 = arrayList3.size();
        int i2 = 0;
        while (i2 < size2) {
            saveDialogFilterInternal(arrayList3.get(i2), false, true);
            i2++;
            z = true;
        }
        int size3 = this.dialogFilters.size();
        boolean z2 = false;
        for (int i3 = 0; i3 < size3; i3++) {
            MessagesController.DialogFilter dialogFilter4 = this.dialogFilters.get(i3);
            int indexOf = arrayList4.indexOf(Integer.valueOf(dialogFilter4.id));
            if (dialogFilter4.order != indexOf) {
                dialogFilter4.order = indexOf;
                z2 = true;
                z = true;
            }
        }
        if (z2) {
            Collections.sort(this.dialogFilters, MessagesStorage$$ExternalSyntheticLambda210.INSTANCE);
            saveDialogFiltersOrderInternal();
        }
        int i4 = z ? 1 : 2;
        calcUnreadCounters(true);
        getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, null, i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$processLoadedFilterPeersInternal$50(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processLoadedFilterPeers(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final ArrayList<MessagesController.DialogFilter> arrayList3, final SparseArray<MessagesController.DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> hashMap, final HashMap<Integer, HashSet<Long>> hashMap2, final HashSet<Integer> hashSet) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda191
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$processLoadedFilterPeers$51(tLRPC$messages_Dialogs, tLRPC$messages_Dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, hashMap, hashMap2, hashSet);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: deleteDialogFilterInternal */
    public void lambda$deleteDialogFilter$52(MessagesController.DialogFilter dialogFilter) {
        try {
            this.dialogFilters.remove(dialogFilter);
            this.dialogFiltersMap.remove(dialogFilter.id);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM dialog_filter WHERE id = " + dialogFilter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase2 = this.database;
            sQLiteDatabase2.executeFast("DELETE FROM dialog_filter_ep WHERE id = " + dialogFilter.id).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase3 = this.database;
            sQLiteDatabase3.executeFast("DELETE FROM dialog_filter_pin_v2 WHERE id = " + dialogFilter.id).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteDialogFilter(final MessagesController.DialogFilter dialogFilter) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda163
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteDialogFilter$52(dialogFilter);
            }
        });
    }

    public void saveDialogFilter(final MessagesController.DialogFilter dialogFilter, final boolean z, final boolean z2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda164
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$54(dialogFilter, z, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDialogFilter$54(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) {
        saveDialogFilterInternal(dialogFilter, z, z2);
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFilter$53();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDialogFilter$53() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void saveDialogFiltersOrderInternal() {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialog_filter SET ord = ?, flags = ? WHERE id = ?");
                int size = this.dialogFilters.size();
                for (int i = 0; i < size; i++) {
                    MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindInteger(1, dialogFilter.order);
                    sQLitePreparedStatement.bindInteger(2, dialogFilter.flags);
                    sQLitePreparedStatement.bindInteger(3, dialogFilter.id);
                    sQLitePreparedStatement.step();
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void saveDialogFiltersOrder() {
        final ArrayList arrayList = new ArrayList(getMessagesController().dialogFilters);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveDialogFiltersOrder$55(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDialogFiltersOrder$55(ArrayList arrayList) {
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.dialogFilters.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ((MessagesController.DialogFilter) arrayList.get(i)).order = i;
            this.dialogFiltersMap.put(((MessagesController.DialogFilter) arrayList.get(i)).id, (MessagesController.DialogFilter) arrayList.get(i));
        }
        saveDialogFiltersOrderInternal();
    }

    protected static void addReplyMessages(TLRPC$Message tLRPC$Message, LongSparseArray<SparseArray<ArrayList<TLRPC$Message>>> longSparseArray, LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        int i = tLRPC$Message.reply_to.reply_to_msg_id;
        long replyToDialogId = MessageObject.getReplyToDialogId(tLRPC$Message);
        SparseArray<ArrayList<TLRPC$Message>> sparseArray = longSparseArray.get(replyToDialogId);
        ArrayList<Integer> arrayList = longSparseArray2.get(replyToDialogId);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            longSparseArray.put(replyToDialogId, sparseArray);
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            longSparseArray2.put(replyToDialogId, arrayList);
        }
        ArrayList<TLRPC$Message> arrayList2 = sparseArray.get(tLRPC$Message.reply_to.reply_to_msg_id);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            sparseArray.put(tLRPC$Message.reply_to.reply_to_msg_id, arrayList2);
            if (!arrayList.contains(Integer.valueOf(tLRPC$Message.reply_to.reply_to_msg_id))) {
                arrayList.add(Integer.valueOf(tLRPC$Message.reply_to.reply_to_msg_id));
            }
        }
        arrayList2.add(tLRPC$Message);
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00fd  */
    /* JADX WARN: Type inference failed for: r3v2, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void loadReplyMessages(androidx.collection.LongSparseArray<android.util.SparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>>> r18, androidx.collection.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r19, java.util.ArrayList<java.lang.Long> r20, java.util.ArrayList<java.lang.Long> r21, boolean r22) throws org.telegram.SQLite.SQLiteException {
        /*
            Method dump skipped, instructions count: 258
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadReplyMessages(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, java.util.ArrayList, boolean):void");
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadUnreadMessages$57();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:119:0x02d4 A[Catch: all -> 0x041c, Exception -> 0x041f, TryCatch #10 {Exception -> 0x041f, all -> 0x041c, blocks: (B:94:0x023d, B:96:0x0243, B:98:0x0249, B:102:0x0283, B:106:0x0292, B:110:0x02a2, B:112:0x02b2, B:114:0x02bc, B:117:0x02cc, B:119:0x02d4, B:121:0x02e0, B:127:0x0309, B:131:0x0316, B:135:0x031f, B:139:0x032b, B:122:0x02ea, B:124:0x02f2, B:126:0x02ff, B:109:0x029c, B:105:0x028c, B:101:0x027c, B:142:0x034b), top: B:207:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x02ea A[Catch: all -> 0x041c, Exception -> 0x041f, TryCatch #10 {Exception -> 0x041f, all -> 0x041c, blocks: (B:94:0x023d, B:96:0x0243, B:98:0x0249, B:102:0x0283, B:106:0x0292, B:110:0x02a2, B:112:0x02b2, B:114:0x02bc, B:117:0x02cc, B:119:0x02d4, B:121:0x02e0, B:127:0x0309, B:131:0x0316, B:135:0x031f, B:139:0x032b, B:122:0x02ea, B:124:0x02f2, B:126:0x02ff, B:109:0x029c, B:105:0x028c, B:101:0x027c, B:142:0x034b), top: B:207:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0311  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0314  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x031a  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x031d  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0326  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0329  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadUnreadMessages$57() {
        /*
            Method dump skipped, instructions count: 1116
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadUnreadMessages$57():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadUnreadMessages$56(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
    }

    public void putWallpapers(final ArrayList<TLRPC$WallPaper> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWallpapers$58(i, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0093  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:61:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putWallpapers$58(int r11, java.util.ArrayList r12) {
        /*
            r10 = this;
            r0 = 0
            r1 = 1
            if (r11 != r1) goto L13
            org.telegram.SQLite.SQLiteDatabase r2 = r10.database     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            java.lang.String r3 = "DELETE FROM wallpapers2 WHERE num >= -1"
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.executeFast(r3)     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.stepThis()     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            r2.dispose()     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
        L13:
            org.telegram.SQLite.SQLiteDatabase r2 = r10.database     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            r2.beginTransaction()     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            if (r11 == 0) goto L23
            org.telegram.SQLite.SQLiteDatabase r2 = r10.database     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            java.lang.String r3 = "REPLACE INTO wallpapers2 VALUES(?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.executeFast(r3)     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            goto L2b
        L23:
            org.telegram.SQLite.SQLiteDatabase r2 = r10.database     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            java.lang.String r3 = "UPDATE wallpapers2 SET data = ? WHERE uid = ?"
            org.telegram.SQLite.SQLitePreparedStatement r2 = r2.executeFast(r3)     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
        L2b:
            r3 = 0
            int r4 = r12.size()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
        L30:
            if (r3 >= r4) goto L73
            java.lang.Object r5 = r12.get(r3)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            org.telegram.tgnet.TLRPC$WallPaper r5 = (org.telegram.tgnet.TLRPC$WallPaper) r5     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r2.requery()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            org.telegram.tgnet.NativeByteBuffer r6 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            int r7 = r5.getObjectSize()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r6.<init>(r7)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r5.serializeToStream(r6)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r7 = 2
            if (r11 == 0) goto L62
            long r8 = r5.id     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r2.bindLong(r1, r8)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r2.bindByteBuffer(r7, r6)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r5 = 3
            if (r11 >= 0) goto L59
            r2.bindInteger(r5, r11)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            goto L6a
        L59:
            if (r11 != r7) goto L5d
            r7 = -1
            goto L5e
        L5d:
            r7 = r3
        L5e:
            r2.bindInteger(r5, r7)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            goto L6a
        L62:
            r2.bindByteBuffer(r1, r6)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            long r8 = r5.id     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r2.bindLong(r7, r8)     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
        L6a:
            r2.step()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            r6.reuse()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            int r3 = r3 + 1
            goto L30
        L73:
            r2.dispose()     // Catch: java.lang.Throwable -> L83 java.lang.Exception -> L86
            org.telegram.SQLite.SQLiteDatabase r11 = r10.database     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            r11.commitTransaction()     // Catch: java.lang.Throwable -> L89 java.lang.Exception -> L8b
            org.telegram.SQLite.SQLiteDatabase r11 = r10.database
            if (r11 == 0) goto L9b
            r11.commitTransaction()
            goto L9b
        L83:
            r11 = move-exception
            r0 = r2
            goto L9c
        L86:
            r11 = move-exception
            r0 = r2
            goto L8c
        L89:
            r11 = move-exception
            goto L9c
        L8b:
            r11 = move-exception
        L8c:
            org.telegram.messenger.FileLog.e(r11)     // Catch: java.lang.Throwable -> L89
            org.telegram.SQLite.SQLiteDatabase r11 = r10.database
            if (r11 == 0) goto L96
            r11.commitTransaction()
        L96:
            if (r0 == 0) goto L9b
            r0.dispose()
        L9b:
            return
        L9c:
            org.telegram.SQLite.SQLiteDatabase r12 = r10.database
            if (r12 == 0) goto La3
            r12.commitTransaction()
        La3:
            if (r0 == 0) goto La8
            r0.dispose()
        La8:
            goto Laa
        La9:
            throw r11
        Laa:
            goto La9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putWallpapers$58(int, java.util.ArrayList):void");
    }

    public void deleteWallpaper(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda69
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteWallpaper$59(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteWallpaper$59(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM wallpapers2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWallpapers$61();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getWallpapers$61() {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized("SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC", new Object[0]);
                final ArrayList arrayList = new ArrayList();
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC$WallPaper TLdeserialize = TLRPC$WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.lambda$getWallpapers$60(arrayList);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getWallpapers$60(ArrayList arrayList) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersDidLoad, arrayList);
    }

    public void addRecentLocalFile(final String str, final String str2, final TLRPC$Document tLRPC$Document) {
        if (str == null || str.length() == 0) {
            return;
        }
        if ((str2 == null || str2.length() == 0) && tLRPC$Document == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda174
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$addRecentLocalFile$62(tLRPC$Document, str, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentLocalFile$62(TLRPC$Document tLRPC$Document, String str, String str2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (tLRPC$Document != null) {
                    sQLitePreparedStatement = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                    sQLitePreparedStatement.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Document.getObjectSize());
                    tLRPC$Document.serializeToStream(nativeByteBuffer);
                    sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                    sQLitePreparedStatement.bindString(2, str);
                    sQLitePreparedStatement.step();
                    sQLitePreparedStatement.dispose();
                    nativeByteBuffer.reuse();
                } else {
                    sQLitePreparedStatement = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindString(1, str2);
                    sQLitePreparedStatement.bindString(2, str);
                    sQLitePreparedStatement.step();
                    sQLitePreparedStatement.dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void deleteUserChatHistory(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda88
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteUserChatHistory$65(j, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00c2  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00de A[Catch: all -> 0x00ed, Exception -> 0x00f0, TRY_LEAVE, TryCatch #7 {Exception -> 0x00f0, all -> 0x00ed, blocks: (B:3:0x0004, B:35:0x00a0, B:39:0x00c6, B:41:0x00de), top: B:69:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00fe  */
    /* JADX WARN: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$deleteUserChatHistory$65(final long r18, long r20) {
        /*
            Method dump skipped, instructions count: 260
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteUserChatHistory$65(long, long):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteUserChatHistory$63(ArrayList arrayList, long j, ArrayList arrayList2) {
        getFileLoader().cancelLoadFiles(arrayList);
        getMessagesController().markDialogMessageAsDeleted(j, arrayList2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteUserChatHistory$64(ArrayList arrayList, long j) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.messagesDeleted;
        Object[] objArr = new Object[3];
        objArr[0] = arrayList;
        objArr[1] = Long.valueOf(DialogObject.isChatDialog(j) ? -j : 0L);
        objArr[2] = Boolean.FALSE;
        notificationCenter.postNotificationName(i, objArr);
    }

    private boolean addFilesToDelete(TLRPC$Message tLRPC$Message, ArrayList<File> arrayList, ArrayList<Pair<Long, Integer>> arrayList2, ArrayList<String> arrayList3, boolean z) {
        long j;
        int i;
        int i2 = 0;
        if (tLRPC$Message == null) {
            return false;
        }
        TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
        TLRPC$Photo photo = MessageObject.getPhoto(tLRPC$Message);
        if (MessageObject.isVoiceMessage(tLRPC$Message)) {
            if (document == null || getMediaDataController().ringtoneDataStore.contains(document.id)) {
                return false;
            }
            j = document.id;
            i = 2;
        } else {
            if (MessageObject.isStickerMessage(tLRPC$Message) || MessageObject.isAnimatedStickerMessage(tLRPC$Message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
            } else if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isGifMessage(tLRPC$Message)) {
                if (document == null) {
                    return false;
                }
                j = document.id;
                i = 4;
            } else if (document != null) {
                if (getMediaDataController().ringtoneDataStore.contains(document.id)) {
                    return false;
                }
                j = document.id;
                i = 8;
            } else if (photo == null || FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()) == null) {
                j = 0;
                i = 0;
            } else {
                j = photo.id;
            }
            i = 1;
        }
        if (j != 0) {
            arrayList2.add(new Pair<>(Long.valueOf(j), Integer.valueOf(i)));
        }
        if (photo != null) {
            int size = photo.sizes.size();
            while (i2 < size) {
                TLRPC$PhotoSize tLRPC$PhotoSize = photo.sizes.get(i2);
                String attachFileName = FileLoader.getAttachFileName(tLRPC$PhotoSize);
                if (!TextUtils.isEmpty(attachFileName)) {
                    arrayList3.add(attachFileName);
                }
                File pathToAttach = getFileLoader().getPathToAttach(tLRPC$PhotoSize, z);
                if (pathToAttach.toString().length() > 0) {
                    arrayList.add(pathToAttach);
                }
                i2++;
            }
            return true;
        } else if (document == null) {
            return false;
        } else {
            String attachFileName2 = FileLoader.getAttachFileName(document);
            if (!TextUtils.isEmpty(attachFileName2)) {
                arrayList3.add(attachFileName2);
            }
            File pathToAttach2 = getFileLoader().getPathToAttach(document, z);
            if (pathToAttach2.toString().length() > 0) {
                arrayList.add(pathToAttach2);
            }
            int size2 = document.thumbs.size();
            while (i2 < size2) {
                File pathToAttach3 = getFileLoader().getPathToAttach(document.thumbs.get(i2));
                if (pathToAttach3.toString().length() > 0) {
                    arrayList.add(pathToAttach3);
                }
                i2++;
            }
            return true;
        }
    }

    public void deleteDialog(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteDialog$68(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:144:0x03ce A[Catch: all -> 0x0043, Exception -> 0x004c, TRY_ENTER, TryCatch #23 {Exception -> 0x004c, all -> 0x0043, blocks: (B:4:0x000c, B:65:0x0129, B:144:0x03ce, B:146:0x03d4), top: B:224:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x03f4 A[Catch: all -> 0x0512, Exception -> 0x051a, TRY_ENTER, TryCatch #28 {Exception -> 0x051a, all -> 0x0512, blocks: (B:26:0x0055, B:149:0x0416, B:142:0x0332, B:148:0x03f4, B:32:0x0068, B:57:0x0101), top: B:214:0x0055 }] */
    /* JADX WARN: Removed duplicated region for block: B:175:0x0526  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x052b  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x0530  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x0535  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x053d  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x0542  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0547  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x054c  */
    /* JADX WARN: Removed duplicated region for block: B:220:0x02c3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:237:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r15v38 */
    /* JADX WARN: Type inference failed for: r15v42 */
    /* JADX WARN: Type inference failed for: r24v0, types: [org.telegram.messenger.MessagesStorage, org.telegram.messenger.BaseController] */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v17 */
    /* JADX WARN: Type inference failed for: r2v30 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$deleteDialog$68(int r25, long r26) {
        /*
            Method dump skipped, instructions count: 1362
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$68(int, long):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteDialog$66(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteDialog$67() {
        getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$onDeleteQueryComplete$69(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDeleteQueryComplete$69(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogPhotos(final long j, final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogPhotos$71(i2, j, i, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogPhotos$71(final int i, final long j, final int i2, final int i3) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2 = null;
        try {
            try {
                if (i != 0) {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
                } else {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY rowid ASC LIMIT %d", Long.valueOf(j), Integer.valueOf(i2)), new Object[0]);
                }
                sQLiteCursor = queryFinalized;
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            final TLRPC$TL_photos_photos tLRPC$TL_photos_photos = new TLRPC$TL_photos_photos();
            final ArrayList arrayList = new ArrayList();
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Photo TLdeserialize = TLRPC$Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (byteBufferValue.remaining() > 0) {
                        arrayList.add(TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                    } else {
                        arrayList.add(null);
                    }
                    byteBufferValue.reuse();
                    tLRPC$TL_photos_photos.photos.add(TLdeserialize);
                }
            }
            sQLiteCursor.dispose();
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda192
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$getDialogPhotos$70(tLRPC$TL_photos_photos, arrayList, j, i2, i, i3);
                }
            });
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor2 = sQLiteCursor;
            FileLog.e(e);
            if (sQLiteCursor2 == null) {
                return;
            }
            sQLiteCursor2.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor2 = sQLiteCursor;
            if (sQLiteCursor2 != null) {
                sQLiteCursor2.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogPhotos$70(TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList, long j, int i, int i2, int i3) {
        getMessagesController().processLoadedUserPhotos(tLRPC$photos_Photos, arrayList, j, i, i2, true, i3);
    }

    public void clearUserPhotos(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda68
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhotos$72(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearUserPhotos$72(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearUserPhoto(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda86
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearUserPhoto$73(j, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearUserPhoto$73(long j, long j2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j + " AND id = " + j2).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetDialogs(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final int i, final int i2, final int i3, final int i4, final int i5, final LongSparseArray<TLRPC$Dialog> longSparseArray, final LongSparseArray<ArrayList<MessageObject>> longSparseArray2, final TLRPC$Message tLRPC$Message, final int i6) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda190
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetDialogs$75(tLRPC$messages_Dialogs, i6, i2, i3, i4, i5, tLRPC$Message, i, longSparseArray, longSparseArray2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:102:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0343  */
    /* JADX WARN: Removed duplicated region for block: B:134:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x02d0 A[Catch: all -> 0x0324, Exception -> 0x0327, LOOP:5: B:77:0x02cd->B:79:0x02d0, LOOP_END, TryCatch #5 {Exception -> 0x0327, all -> 0x0324, blocks: (B:3:0x0006, B:4:0x0024, B:6:0x002c, B:7:0x0040, B:21:0x0094, B:23:0x01bc, B:25:0x01ca, B:41:0x0210, B:28:0x01cf, B:32:0x01ea, B:34:0x01f2, B:35:0x01f5, B:37:0x0205, B:38:0x0207, B:40:0x020b, B:42:0x0216, B:45:0x0243, B:47:0x024b, B:49:0x0259, B:79:0x02d0, B:80:0x02ef, B:50:0x025c, B:54:0x0269, B:57:0x0272, B:59:0x027a, B:61:0x0288, B:62:0x028b, B:65:0x0299, B:68:0x02a2, B:70:0x02aa, B:72:0x02b8, B:73:0x02bf), top: B:110:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x031a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$resetDialogs$75(org.telegram.tgnet.TLRPC$messages_Dialogs r33, int r34, int r35, int r36, int r37, int r38, org.telegram.tgnet.TLRPC$Message r39, int r40, androidx.collection.LongSparseArray r41, androidx.collection.LongSparseArray r42) {
        /*
            Method dump skipped, instructions count: 841
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$resetDialogs$75(org.telegram.tgnet.TLRPC$messages_Dialogs, int, int, int, int, int, org.telegram.tgnet.TLRPC$Message, int, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$resetDialogs$74(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void putDialogPhotos(final long j, final TLRPC$photos_Photos tLRPC$photos_Photos, final ArrayList<TLRPC$Message> arrayList) {
        if (tLRPC$photos_Photos == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda118
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putDialogPhotos$76(j, tLRPC$photos_Photos, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putDialogPhotos$76(long j, TLRPC$photos_Photos tLRPC$photos_Photos, ArrayList arrayList) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + j).stepThis().dispose();
                sQLitePreparedStatement = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
                int size = tLRPC$photos_Photos.photos.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$Photo tLRPC$Photo = tLRPC$photos_Photos.photos.get(i);
                    if (!(tLRPC$Photo instanceof TLRPC$TL_photoEmpty)) {
                        sQLitePreparedStatement.requery();
                        int objectSize = tLRPC$Photo.getObjectSize();
                        if (arrayList != null) {
                            objectSize += ((TLRPC$Message) arrayList.get(i)).getObjectSize();
                        }
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(objectSize);
                        tLRPC$Photo.serializeToStream(nativeByteBuffer);
                        if (arrayList != null) {
                            ((TLRPC$Message) arrayList.get(i)).serializeToStream(nativeByteBuffer);
                        }
                        sQLitePreparedStatement.bindLong(1, j);
                        sQLitePreparedStatement.bindLong(2, tLRPC$Photo.id);
                        sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
                        sQLitePreparedStatement.step();
                        nativeByteBuffer.reuse();
                    }
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void emptyMessagesMedia(final long j, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda150
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$emptyMessagesMedia$79(arrayList, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:100:0x0210  */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0217  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x01c9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:126:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x012c A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0132 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x013f  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0151  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0162 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0176 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0180 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x018b A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01a0 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01a4 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x01b0 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01b6 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01c1 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01c6 A[Catch: all -> 0x01dc, Exception -> 0x01e1, TryCatch #6 {Exception -> 0x01e1, all -> 0x01dc, blocks: (B:29:0x00d0, B:31:0x00d6, B:33:0x0112, B:38:0x011a, B:40:0x012c, B:42:0x0139, B:48:0x0144, B:52:0x0152, B:54:0x0162, B:56:0x017a, B:58:0x0180, B:62:0x0187, B:64:0x018f, B:66:0x01a0, B:68:0x01a7, B:70:0x01b0, B:72:0x01b9, B:74:0x01c1, B:76:0x01c6, B:77:0x01c9, B:71:0x01b6, B:67:0x01a4, B:61:0x0185, B:63:0x018b, B:55:0x0176, B:41:0x0132, B:78:0x01d0), top: B:114:0x00d0 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x020b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$emptyMessagesMedia$79(java.util.ArrayList r19, long r20) {
        /*
            Method dump skipped, instructions count: 546
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$emptyMessagesMedia$79(java.util.ArrayList, long):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyMessagesMedia$77(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$emptyMessagesMedia$78(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void updateMessagePollResults(final long j, final TLRPC$Poll tLRPC$Poll, final TLRPC$PollResults tLRPC$PollResults) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda113
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessagePollResults$80(j, tLRPC$Poll, tLRPC$PollResults);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessagePollResults$80(long j, TLRPC$Poll tLRPC$Poll, TLRPC$PollResults tLRPC$PollResults) {
        Integer num;
        SQLiteDatabase sQLiteDatabase;
        Locale locale;
        SQLiteCursor sQLiteCursor;
        ArrayList arrayList;
        SQLiteCursor sQLiteCursor2 = null;
        try {
            try {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, mid FROM polls_v2 WHERE id = %d", Long.valueOf(j)), new Object[0]);
                LongSparseArray longSparseArray = null;
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (longSparseArray == null) {
                            longSparseArray = new LongSparseArray();
                        }
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(longValue, arrayList2);
                        }
                        arrayList2.add(Integer.valueOf(queryFinalized.intValue(1)));
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor2 = queryFinalized;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor2 = queryFinalized;
                    }
                }
                queryFinalized.dispose();
                if (longSparseArray != null) {
                    this.database.beginTransaction();
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
                    int size = longSparseArray.size();
                    for (int i = 0; i < size; i++) {
                        long keyAt = longSparseArray.keyAt(i);
                        ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i);
                        int size2 = arrayList3.size();
                        int i2 = 0;
                        while (i2 < size2) {
                            try {
                                num = (Integer) arrayList3.get(i2);
                                sQLiteDatabase = this.database;
                                locale = Locale.US;
                                sQLiteCursor = sQLiteCursor2;
                            } catch (Exception e2) {
                                e = e2;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                            try {
                                LongSparseArray longSparseArray2 = longSparseArray;
                                int i3 = size;
                                sQLiteCursor2 = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", num, Long.valueOf(keyAt)), new Object[0]);
                                if (sQLiteCursor2.next()) {
                                    NativeByteBuffer byteBufferValue = sQLiteCursor2.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        arrayList = arrayList3;
                                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                        byteBufferValue.reuse();
                                        TLRPC$MessageMedia tLRPC$MessageMedia = TLdeserialize.media;
                                        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPoll) {
                                            TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) tLRPC$MessageMedia;
                                            if (tLRPC$Poll != null) {
                                                tLRPC$TL_messageMediaPoll.poll = tLRPC$Poll;
                                            }
                                            if (tLRPC$PollResults != null) {
                                                MessageObject.updatePollResults(tLRPC$TL_messageMediaPoll, tLRPC$PollResults);
                                            }
                                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                                            TLdeserialize.serializeToStream(nativeByteBuffer);
                                            executeFast.requery();
                                            executeFast.bindByteBuffer(1, nativeByteBuffer);
                                            executeFast.bindInteger(2, num.intValue());
                                            executeFast.bindLong(3, keyAt);
                                            executeFast.step();
                                            nativeByteBuffer.reuse();
                                        }
                                    } else {
                                        arrayList = arrayList3;
                                    }
                                } else {
                                    arrayList = arrayList3;
                                    this.database.executeFast(String.format(locale, "DELETE FROM polls_v2 WHERE mid = %d AND uid = %d", num, Long.valueOf(keyAt))).stepThis().dispose();
                                }
                                sQLiteCursor2.dispose();
                                i2++;
                                longSparseArray = longSparseArray2;
                                size = i3;
                                arrayList3 = arrayList;
                            } catch (Exception e3) {
                                e = e3;
                                sQLiteCursor2 = sQLiteCursor;
                                FileLog.e(e);
                                if (sQLiteCursor2 == null) {
                                    return;
                                }
                                sQLiteCursor2.dispose();
                            } catch (Throwable th3) {
                                th = th3;
                                sQLiteCursor2 = sQLiteCursor;
                                if (sQLiteCursor2 != null) {
                                    sQLiteCursor2.dispose();
                                }
                                throw th;
                            }
                        }
                    }
                    executeFast.dispose();
                    this.database.commitTransaction();
                }
                if (sQLiteCursor2 == null) {
                    return;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        } catch (Exception e4) {
            e = e4;
        }
        sQLiteCursor2.dispose();
    }

    public void updateMessageReactions(final long j, final int i, final TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageReactions$81(i, j, tLRPC$TL_messageReactions);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageReactions$81(int i, long j, TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        SQLiteCursor queryFinalized;
        NativeByteBuffer byteBufferValue;
        SQLitePreparedStatement executeFast;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                this.database.beginTransaction();
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
                    } else {
                        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_topics WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
                    }
                    try {
                        if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            if (TLdeserialize != null) {
                                TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                byteBufferValue.reuse();
                                MessageObject.updateReactions(TLdeserialize, tLRPC$TL_messageReactions);
                                if (i2 == 0) {
                                    executeFast = this.database.executeFast("UPDATE messages_v2 SET data = ? WHERE mid = ? AND uid = ?");
                                } else {
                                    executeFast = this.database.executeFast("UPDATE messages_topics SET data = ? WHERE mid = ? AND uid = ?");
                                }
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                                TLdeserialize.serializeToStream(nativeByteBuffer);
                                executeFast.requery();
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindInteger(2, i);
                                executeFast.bindLong(3, j);
                                executeFast.step();
                                nativeByteBuffer.reuse();
                                executeFast.dispose();
                            } else {
                                byteBufferValue.reuse();
                            }
                        }
                        queryFinalized.dispose();
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 == null) {
                    return;
                }
                sQLiteDatabase3.commitTransaction();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void updateMessageVoiceTranscriptionOpen(final long j, final int i, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscriptionOpen$82(i, j, tLRPC$Message);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageVoiceTranscriptionOpen$82(int i, long j, TLRPC$Message tLRPC$Message) {
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
                messageWithCustomParamsOnly.voiceTranscriptionOpen = tLRPC$Message.voiceTranscriptionOpen;
                messageWithCustomParamsOnly.voiceTranscriptionRated = tLRPC$Message.voiceTranscriptionRated;
                messageWithCustomParamsOnly.voiceTranscriptionFinal = tLRPC$Message.voiceTranscriptionFinal;
                messageWithCustomParamsOnly.voiceTranscriptionForce = tLRPC$Message.voiceTranscriptionForce;
                messageWithCustomParamsOnly.voiceTranscriptionId = tLRPC$Message.voiceTranscriptionId;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
                    } else {
                        executeFast = this.database.executeFast("UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?");
                    }
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, i);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 == null) {
                    return;
                }
                sQLiteDatabase3.commitTransaction();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final long j2, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$83(i, j, z, j2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageVoiceTranscription$83(int i, long j, boolean z, long j2, String str) {
        TLRPC$Message messageWithCustomParamsOnly;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
                messageWithCustomParamsOnly.voiceTranscriptionFinal = z;
                messageWithCustomParamsOnly.voiceTranscriptionId = j2;
                messageWithCustomParamsOnly.voiceTranscription = str;
                executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            executeFast.requery();
            NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
            if (writeLocalParams != null) {
                executeFast.bindByteBuffer(1, writeLocalParams);
            } else {
                executeFast.bindNull(1);
            }
            executeFast.bindInteger(2, i);
            executeFast.bindLong(3, j);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
            if (writeLocalParams != null) {
                writeLocalParams.reuse();
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                return;
            }
            sQLiteDatabase.commitTransaction();
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            FileLog.e(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateMessageVoiceTranscription(final long j, final int i, final String str, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVoiceTranscription$84(i, j, tLRPC$Message, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageVoiceTranscription$84(int i, long j, TLRPC$Message tLRPC$Message, String str) {
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(i, j);
                messageWithCustomParamsOnly.voiceTranscriptionOpen = tLRPC$Message.voiceTranscriptionOpen;
                messageWithCustomParamsOnly.voiceTranscriptionRated = tLRPC$Message.voiceTranscriptionRated;
                messageWithCustomParamsOnly.voiceTranscriptionFinal = tLRPC$Message.voiceTranscriptionFinal;
                messageWithCustomParamsOnly.voiceTranscriptionForce = tLRPC$Message.voiceTranscriptionForce;
                messageWithCustomParamsOnly.voiceTranscriptionId = tLRPC$Message.voiceTranscriptionId;
                messageWithCustomParamsOnly.voiceTranscription = str;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
                    } else {
                        executeFast = this.database.executeFast("UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?");
                    }
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, i);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        this.database.commitTransaction();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 == null) {
                    return;
                }
                sQLiteDatabase3.commitTransaction();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void updateMessageCustomParams(final long j, final TLRPC$Message tLRPC$Message) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda182
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageCustomParams$85(tLRPC$Message, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageCustomParams$85(TLRPC$Message tLRPC$Message, long j) {
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC$Message messageWithCustomParamsOnly = getMessageWithCustomParamsOnly(tLRPC$Message.id, j);
                MessageCustomParamsHelper.copyParams(tLRPC$Message, messageWithCustomParamsOnly);
                for (int i = 0; i < 2; i++) {
                    if (i == 0) {
                        executeFast = this.database.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
                    } else {
                        executeFast = this.database.executeFast("UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?");
                    }
                    try {
                        executeFast.requery();
                        NativeByteBuffer writeLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnly);
                        if (writeLocalParams != null) {
                            executeFast.bindByteBuffer(1, writeLocalParams);
                        } else {
                            executeFast.bindNull(1);
                        }
                        executeFast.bindInteger(2, tLRPC$Message.id);
                        executeFast.bindLong(3, j);
                        executeFast.step();
                        executeFast.dispose();
                        if (writeLocalParams != null) {
                            writeLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 == null) {
                    return;
                }
                sQLiteDatabase3.commitTransaction();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    private TLRPC$Message getMessageWithCustomParamsOnly(int i, long j) {
        SQLiteCursor queryFinalized;
        boolean z;
        TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                queryFinalized = sQLiteDatabase.queryFinalized("SELECT custom_params FROM messages_v2 WHERE mid = " + i + " AND uid = " + j, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (SQLiteException e) {
            e = e;
        }
        try {
            if (queryFinalized.next()) {
                MessageCustomParamsHelper.readLocalParams(tLRPC$TL_message, queryFinalized.byteBufferValue(0));
                z = true;
            } else {
                z = false;
            }
            queryFinalized.dispose();
            if (!z) {
                SQLiteDatabase sQLiteDatabase2 = this.database;
                sQLiteCursor = sQLiteDatabase2.queryFinalized("SELECT custom_params FROM messages_topics WHERE mid = " + i + " AND uid = " + j, new Object[0]);
                if (sQLiteCursor.next()) {
                    MessageCustomParamsHelper.readLocalParams(tLRPC$TL_message, sQLiteCursor.byteBufferValue(0));
                }
                sQLiteCursor.dispose();
            }
        } catch (SQLiteException e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            return tLRPC$TL_message;
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
        return tLRPC$TL_message;
    }

    public void getNewTask(final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda128
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getNewTask$86(longSparseArray, longSparseArray2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00a5, code lost:
        if (r15 > 0) goto L16;
     */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00f3  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getNewTask$86(androidx.collection.LongSparseArray r14, androidx.collection.LongSparseArray r15) {
        /*
            Method dump skipped, instructions count: 255
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getNewTask$86(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public void markMentionMessageAsRead(final long j, final int i, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMentionMessageAsRead$87(i, j, j2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMentionMessageAsRead$87(int i, long j, long j2) {
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2;
        SQLiteCursor sQLiteCursor3 = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                SQLiteCursor queryFinalized = sQLiteDatabase2.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j2, new Object[0]);
                try {
                    int max = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
                    queryFinalized.dispose();
                    this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(max), Long.valueOf(j2))).stepThis().dispose();
                    LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
                    longSparseIntArray.put(j2, max);
                    if (max == 0) {
                        updateFiltersReadCounter(null, longSparseIntArray, true);
                    }
                    getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
                    this.database.executeFast(String.format(locale, "UPDATE messages_topics SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                    SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(locale, "SELECT data FROM messages_topics WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
                    int i2 = 0;
                    while (queryFinalized2.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                i2 = MessageObject.getTopicId(TLdeserialize);
                            }
                        } catch (Exception e) {
                            sQLiteCursor3 = sQLiteCursor2;
                            e = e;
                            FileLog.e(e);
                            if (sQLiteCursor3 == null) {
                                return;
                            }
                            sQLiteCursor3.dispose();
                            return;
                        } catch (Throwable th) {
                            sQLiteCursor3 = sQLiteCursor;
                            th = th;
                            if (sQLiteCursor3 != null) {
                                sQLiteCursor3.dispose();
                            }
                            throw th;
                        }
                    }
                    queryFinalized2.dispose();
                    if (i2 == 0) {
                        return;
                    }
                    SQLiteDatabase sQLiteDatabase3 = this.database;
                    Locale locale2 = Locale.US;
                    SQLiteCursor queryFinalized3 = sQLiteDatabase3.queryFinalized(String.format(locale2, "SELECT unread_mentions FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j2), Integer.valueOf(i2)), new Object[0]);
                    int max2 = queryFinalized3.next() ? Math.max(0, queryFinalized3.intValue(0) - 1) : 0;
                    queryFinalized3.dispose();
                    this.database.executeFast(String.format(locale2, "UPDATE topics SET unread_mentions = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(max2), Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
                    getMessagesController().getTopicsController().updateMentionsUnread(j, i2, max2);
                } catch (Exception e2) {
                    e = e2;
                    sQLiteCursor3 = queryFinalized;
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteCursor3 = queryFinalized;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void markMessageAsMention(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsMention$88(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessageAsMention$88(int i, long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET mention = 1, read_state = read_state & ~2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(final long j, final int i, final int i2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda45
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetMentionsCount$89(i, j, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetMentionsCount$89(int i, long j, int i2) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (i == 0) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j, new Object[0]);
                    try {
                        int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                        queryFinalized.dispose();
                        if (intValue == 0 && i2 == 0) {
                            return;
                        }
                        if (i2 == 0) {
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j))).stepThis().dispose();
                        }
                        this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(i2), Long.valueOf(j))).stepThis().dispose();
                        LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
                        longSparseIntArray.put(j, i2);
                        getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
                        if (i2 != 0) {
                            return;
                        }
                        updateFiltersReadCounter(null, longSparseIntArray, true);
                        return;
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                this.database.executeFast(String.format(Locale.US, "UPDATE topics SET unread_mentions = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
                TopicsController.TopicUpdate topicUpdate = new TopicsController.TopicUpdate();
                topicUpdate.dialogId = j;
                topicUpdate.topicId = i;
                topicUpdate.onlyCounters = true;
                topicUpdate.unreadMentions = i2;
                topicUpdate.unreadCount = -1;
                getMessagesController().getTopicsController().processUpdate(Collections.singletonList(topicUpdate));
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void createTaskForMid(final long j, final int i, final int i2, final int i3, final int i4, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createTaskForMid$91(i2, i3, i4, i, z, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createTaskForMid$91(int i, int i2, int i3, int i4, final boolean z, final long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                int max = Math.max(i, i2) + i3;
                SparseArray<ArrayList<Integer>> sparseArray = new SparseArray<>();
                final ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.valueOf(i4));
                sparseArray.put(max, arrayList);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda198
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$createTaskForMid$90(z, j, arrayList);
                    }
                });
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                for (int i5 = 0; i5 < sparseArray.size(); i5++) {
                    try {
                        int keyAt = sparseArray.keyAt(i5);
                        ArrayList<Integer> arrayList2 = sparseArray.get(keyAt);
                        for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                            executeFast.requery();
                            executeFast.bindInteger(1, arrayList2.get(i6).intValue());
                            executeFast.bindLong(2, j);
                            executeFast.bindInteger(3, keyAt);
                            executeFast.bindInteger(4, 1);
                            executeFast.step();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                executeFast.dispose();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET ttl = 0 WHERE mid = %d AND uid = %d", Integer.valueOf(i4), Long.valueOf(j))).stepThis().dispose();
                getMessagesController().didAddedNewTask(max, j, sparseArray);
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createTaskForMid$90(boolean z, long j, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(j, arrayList, 0);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    public void createTaskForSecretChat(final int i, final int i2, final int i3, final int i4, final ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$createTaskForSecretChat$93(i, arrayList, i4, i2, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:60:0x015b  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0160  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0165  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x016e  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0173  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0178  */
    /* JADX WARN: Removed duplicated region for block: B:95:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$createTaskForSecretChat$93(int r18, java.util.ArrayList r19, int r20, int r21, int r22) {
        /*
            Method dump skipped, instructions count: 382
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$createTaskForSecretChat$93(int, java.util.ArrayList, int, int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createTaskForSecretChat$92(long j, ArrayList arrayList) {
        markMessagesContentAsRead(j, arrayList, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    /* JADX WARN: Code restructure failed: missing block: B:165:0x03c6, code lost:
        if (r9.indexOfKey(-r4.id) >= 0) goto L168;
     */
    /* JADX WARN: Code restructure failed: missing block: B:330:0x0677, code lost:
        if (r1.dialogsWithMentions.indexOfKey(-r0.id) < 0) goto L345;
     */
    /* JADX WARN: Removed duplicated region for block: B:204:0x0486  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x04c1  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x0586  */
    /* JADX WARN: Removed duplicated region for block: B:428:0x07ea  */
    /* JADX WARN: Removed duplicated region for block: B:442:0x0820  */
    /* JADX WARN: Removed duplicated region for block: B:490:0x08d6  */
    /* JADX WARN: Removed duplicated region for block: B:610:0x0aa5  */
    /* JADX WARN: Removed duplicated region for block: B:611:0x0aac  */
    /* JADX WARN: Type inference failed for: r3v83, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v106, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v114, types: [boolean] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray r26, org.telegram.messenger.support.LongSparseIntArray r27, boolean r28) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 2772
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateFiltersReadCounter(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFiltersReadCounter$94() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
    }

    /* JADX WARN: Removed duplicated region for block: B:126:0x036e A[Catch: Exception -> 0x0376, TRY_LEAVE, TryCatch #0 {Exception -> 0x0376, blocks: (B:3:0x0008, B:7:0x0024, B:8:0x003c, B:10:0x0042, B:13:0x0049, B:16:0x0050, B:18:0x005a, B:19:0x005e, B:20:0x0064, B:93:0x029c, B:95:0x02a2, B:124:0x035e, B:126:0x036e, B:99:0x02ac, B:101:0x02b7, B:102:0x02c5, B:104:0x02cb, B:106:0x02f3, B:108:0x02f9, B:110:0x02fe, B:113:0x031b, B:112:0x0305, B:114:0x031d, B:116:0x0326, B:118:0x032c, B:119:0x0335, B:121:0x033b, B:122:0x0354, B:123:0x0357, B:21:0x0069, B:24:0x0070, B:26:0x0076, B:31:0x008b, B:33:0x0092, B:55:0x015f, B:36:0x00af, B:37:0x00d1, B:40:0x00d9, B:43:0x00e0, B:45:0x0108, B:47:0x0113, B:51:0x0143, B:48:0x012b, B:50:0x012f, B:52:0x0147, B:54:0x014b, B:29:0x0083, B:56:0x0187, B:58:0x018d, B:60:0x0194, B:61:0x01bd, B:63:0x01c3, B:65:0x01db, B:67:0x01e1, B:69:0x01e8, B:71:0x01ef, B:73:0x0211, B:75:0x0218, B:78:0x0235, B:76:0x0226, B:79:0x023f, B:83:0x0250, B:85:0x025a, B:86:0x0261, B:87:0x0267, B:90:0x026e, B:92:0x0274), top: B:131:0x0008 }] */
    /* JADX WARN: Removed duplicated region for block: B:164:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDialogsWithReadMessagesInternal(java.util.ArrayList<java.lang.Integer> r22, org.telegram.messenger.support.LongSparseIntArray r23, org.telegram.messenger.support.LongSparseIntArray r24, androidx.collection.LongSparseArray<java.util.ArrayList<java.lang.Integer>> r25, org.telegram.messenger.support.LongSparseIntArray r26) {
        /*
            Method dump skipped, instructions count: 891
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, androidx.collection.LongSparseArray, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseIntArray longSparseIntArray) {
        return longSparseIntArray == null || longSparseIntArray.size() == 0;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        return sparseIntArray == null || sparseIntArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> longSparseArray) {
        return longSparseArray == null || longSparseArray.size() == 0;
    }

    public void updateDialogsWithReadMessages(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseIntArray longSparseIntArray3, boolean z) {
        if (!isEmpty(longSparseIntArray) || !isEmpty(longSparseIntArray2) || !isEmpty(longSparseArray) || !isEmpty(longSparseIntArray3)) {
            if (z) {
                this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda166
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$updateDialogsWithReadMessages$95(longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
                    }
                });
            } else {
                updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDialogsWithReadMessages$95(LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray longSparseArray, LongSparseIntArray longSparseIntArray3) {
        updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
    }

    public void updateChatParticipants(final TLRPC$ChatParticipants tLRPC$ChatParticipants) {
        if (tLRPC$ChatParticipants == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda172
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatParticipants$97(tLRPC$ChatParticipants);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChatParticipants$97(TLRPC$ChatParticipants tLRPC$ChatParticipants) {
        SQLiteCursor queryFinalized;
        final TLRPC$ChatFull tLRPC$ChatFull;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + tLRPC$ChatParticipants.chat_id, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                tLRPC$ChatFull = null;
            } else {
                tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tLRPC$ChatFull.pinned_msg_id = queryFinalized.intValue(1);
                tLRPC$ChatFull.online_count = queryFinalized.intValue(2);
                tLRPC$ChatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (!(tLRPC$ChatFull instanceof TLRPC$TL_chatFull)) {
                return;
            }
            tLRPC$ChatFull.participants = tLRPC$ChatParticipants;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda169
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateChatParticipants$96(tLRPC$ChatFull);
                }
            });
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChatFull.getObjectSize());
            tLRPC$ChatFull.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$ChatFull.id);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.bindInteger(3, tLRPC$ChatFull.pinned_msg_id);
            executeFast.bindInteger(4, tLRPC$ChatFull.online_count);
            executeFast.bindLong(5, tLRPC$ChatFull.inviterId);
            executeFast.bindInteger(6, tLRPC$ChatFull.invitesCount);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
            sQLiteCursor.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChatParticipants$96(TLRPC$ChatFull tLRPC$ChatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationName(i, tLRPC$ChatFull, 0, bool, bool);
    }

    public void loadChannelAdmins(final long j) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda70
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadChannelAdmins$98(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChannelAdmins$98(long j) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                queryFinalized = sQLiteDatabase.queryFinalized("SELECT uid, data FROM channel_admins_v3 WHERE did = " + j, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            LongSparseArray<TLRPC$ChannelParticipant> longSparseArray = new LongSparseArray<>();
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    TLRPC$ChannelParticipant TLdeserialize = TLRPC$ChannelParticipant.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        longSparseArray.put(queryFinalized.longValue(0), TLdeserialize);
                    }
                }
            }
            queryFinalized.dispose();
            getMessagesController().processLoadedChannelAdmins(longSparseArray, j, true);
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
            sQLiteCursor.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void putChannelAdmins(final long j, final LongSparseArray<TLRPC$ChannelParticipant> longSparseArray) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda97
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putChannelAdmins$99(j, longSparseArray);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putChannelAdmins$99(long j, LongSparseArray longSparseArray) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteDatabase.executeFast("DELETE FROM channel_admins_v3 WHERE did = " + j).stepThis().dispose();
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_admins_v3 VALUES(?, ?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    try {
                        executeFast.requery();
                        executeFast.bindLong(1, j);
                        executeFast.bindLong(2, longSparseArray.keyAt(i));
                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) longSparseArray.valueAt(i);
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChannelParticipant.getObjectSize());
                        tLRPC$ChannelParticipant.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(3, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                executeFast.dispose();
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 == null) {
                    return;
                }
                sQLiteDatabase4.commitTransaction();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateChannelUsers(final long j, final ArrayList<TLRPC$ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda100
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChannelUsers$100(j, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChannelUsers$100(long j, ArrayList arrayList) {
        SQLitePreparedStatement executeFast;
        long j2 = -j;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + j2).stepThis().dispose();
                this.database.beginTransaction();
                executeFast = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) arrayList.get(i);
                executeFast.requery();
                executeFast.bindLong(1, j2);
                executeFast.bindLong(2, MessageObject.getPeerId(tLRPC$ChannelParticipant.peer));
                executeFast.bindInteger(3, currentTimeMillis);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChannelParticipant.getObjectSize());
                tLRPC$ChannelParticipant.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(4, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                currentTimeMillis--;
            }
            executeFast.dispose();
            this.database.commitTransaction();
            loadChatInfo(j, true, null, false, true);
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                return;
            }
            sQLiteDatabase.commitTransaction();
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            FileLog.e(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void saveBotCache(final String str, final TLObject tLObject) {
        if (tLObject == null || TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveBotCache$101(tLObject, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveBotCache$101(TLObject tLObject, String str) {
        int currentTime;
        int i;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                currentTime = getConnectionsManager().getCurrentTime();
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (tLObject instanceof TLRPC$TL_messages_botCallbackAnswer) {
                i = ((TLRPC$TL_messages_botCallbackAnswer) tLObject).cache_time;
            } else {
                if (tLObject instanceof TLRPC$TL_messages_botResults) {
                    i = ((TLRPC$TL_messages_botResults) tLObject).cache_time;
                }
                executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindInteger(2, currentTime);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                return;
            }
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject.serializeToStream(nativeByteBuffer2);
            executeFast.bindString(1, str);
            executeFast.bindInteger(2, currentTime);
            executeFast.bindByteBuffer(3, nativeByteBuffer2);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer2.reuse();
            return;
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
            sQLitePreparedStatement.dispose();
            return;
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
        currentTime += i;
        executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
    }

    public void getBotCache(final String str, final RequestDelegate requestDelegate) {
        if (str == null || requestDelegate == null) {
            return;
        }
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getBotCache$102(currentTime, str, requestDelegate);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:40:0x007f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getBotCache$102(int r5, java.lang.String r6, org.telegram.tgnet.RequestDelegate r7) {
        /*
            r4 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r4.database     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            r2.<init>()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            java.lang.String r3 = "DELETE FROM botcache WHERE date < "
            r2.append(r3)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            r2.append(r5)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            java.lang.String r5 = r2.toString()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            org.telegram.SQLite.SQLitePreparedStatement r5 = r1.executeFast(r5)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            org.telegram.SQLite.SQLitePreparedStatement r5 = r5.stepThis()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            r5.dispose()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            org.telegram.SQLite.SQLiteDatabase r5 = r4.database     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            java.lang.String r1 = "SELECT data FROM botcache WHERE id = ?"
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            r3 = 0
            r2[r3] = r6     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r1, r2)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L6a
            boolean r6 = r5.next()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L63
            if (r6 == 0) goto L56
            org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r3)     // Catch: java.lang.Exception -> L50 java.lang.Throwable -> L60
            if (r6 == 0) goto L56
            int r1 = r6.readInt32(r3)     // Catch: java.lang.Exception -> L50 java.lang.Throwable -> L60
            int r2 = org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer.constructor     // Catch: java.lang.Exception -> L50 java.lang.Throwable -> L60
            if (r1 != r2) goto L46
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r1 = org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer.TLdeserialize(r6, r1, r3)     // Catch: java.lang.Exception -> L50 java.lang.Throwable -> L60
            goto L4a
        L46:
            org.telegram.tgnet.TLRPC$messages_BotResults r1 = org.telegram.tgnet.TLRPC$messages_BotResults.TLdeserialize(r6, r1, r3)     // Catch: java.lang.Exception -> L50 java.lang.Throwable -> L60
        L4a:
            r6.reuse()     // Catch: java.lang.Exception -> L4e java.lang.Throwable -> L79
            goto L57
        L4e:
            r6 = move-exception
            goto L52
        L50:
            r6 = move-exception
            r1 = r0
        L52:
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L79
            goto L57
        L56:
            r1 = r0
        L57:
            r5.dispose()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L79
            r7.run(r1, r0)
            goto L78
        L5e:
            r6 = move-exception
            goto L6d
        L60:
            r6 = move-exception
            r1 = r0
            goto L7a
        L63:
            r6 = move-exception
            r1 = r0
            goto L6d
        L66:
            r6 = move-exception
            r5 = r0
            r1 = r5
            goto L7a
        L6a:
            r6 = move-exception
            r5 = r0
            r1 = r5
        L6d:
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L79
            r7.run(r1, r0)
            if (r5 == 0) goto L78
            r5.dispose()
        L78:
            return
        L79:
            r6 = move-exception
        L7a:
            r7.run(r1, r0)
            if (r5 == 0) goto L82
            r5.dispose()
        L82:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getBotCache$102(int, java.lang.String, org.telegram.tgnet.RequestDelegate):void");
    }

    public void loadUserInfo(final TLRPC$User tLRPC$User, final boolean z, final int i, int i2) {
        if (tLRPC$User == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda187
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadUserInfo$103(tLRPC$User, z, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:112:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x018c  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01a1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$loadUserInfo$103(org.telegram.tgnet.TLRPC$User r20, boolean r21, int r22) {
        /*
            Method dump skipped, instructions count: 423
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadUserInfo$103(org.telegram.tgnet.TLRPC$User, boolean, int):void");
    }

    public void updateUserInfo(final TLRPC$UserFull tLRPC$UserFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda203
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateUserInfo$104(z, tLRPC$UserFull);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:28:0x009b  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateUserInfo$104(boolean r8, org.telegram.tgnet.TLRPC$UserFull r9) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L2e
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            r1.<init>()     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            java.lang.String r2 = "SELECT uid FROM user_settings WHERE uid = "
            r1.append(r2)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            org.telegram.tgnet.TLRPC$User r2 = r9.user     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            long r2 = r2.id     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            r1.append(r2)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            org.telegram.SQLite.SQLiteCursor r8 = r8.queryFinalized(r1, r2)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            boolean r1 = r8.next()     // Catch: java.lang.Exception -> L2b java.lang.Throwable -> La4
            r8.dispose()     // Catch: java.lang.Exception -> L2b java.lang.Throwable -> La4
            if (r1 != 0) goto L2e
            return
        L2b:
            r9 = move-exception
            goto L96
        L2e:
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            java.lang.String r1 = "REPLACE INTO user_settings VALUES(?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.executeFast(r1)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            org.telegram.tgnet.NativeByteBuffer r1 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            int r2 = r9.getObjectSize()     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r9.serializeToStream(r1)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            org.telegram.tgnet.TLRPC$User r2 = r9.user     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            long r2 = r2.id     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r4 = 1
            r8.bindLong(r4, r2)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r2 = 2
            r8.bindByteBuffer(r2, r1)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r3 = 3
            int r5 = r9.pinned_msg_id     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.bindInteger(r3, r5)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.step()     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.dispose()     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r1.reuse()     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            int r8 = r9.flags     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            r8 = r8 & 2048(0x800, float:2.87E-42)
            if (r8 == 0) goto La3
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            java.lang.String r1 = "UPDATE dialogs SET folder_id = ? WHERE did = ?"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.executeFast(r1)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            int r1 = r9.folder_id     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.bindInteger(r4, r1)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            org.telegram.tgnet.TLRPC$User r1 = r9.user     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            long r3 = r1.id     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.bindLong(r2, r3)     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.step()     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            r8.dispose()     // Catch: java.lang.Throwable -> L87 java.lang.Exception -> L8c
            androidx.collection.LongSparseArray<java.lang.Boolean> r8 = r7.unknownDialogsIds     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            org.telegram.tgnet.TLRPC$User r9 = r9.user     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            long r1 = r9.id     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            r8.remove(r1)     // Catch: java.lang.Throwable -> L91 java.lang.Exception -> L94
            goto La3
        L87:
            r9 = move-exception
            r6 = r0
            r0 = r8
            r8 = r6
            goto La5
        L8c:
            r9 = move-exception
            r6 = r0
            r0 = r8
            r8 = r6
            goto L96
        L91:
            r9 = move-exception
            r8 = r0
            goto La5
        L94:
            r9 = move-exception
            r8 = r0
        L96:
            org.telegram.messenger.FileLog.e(r9)     // Catch: java.lang.Throwable -> La4
            if (r0 == 0) goto L9e
            r0.dispose()
        L9e:
            if (r8 == 0) goto La3
            r8.dispose()
        La3:
            return
        La4:
            r9 = move-exception
        La5:
            if (r0 == 0) goto Laa
            r0.dispose()
        Laa:
            if (r8 == 0) goto Laf
            r8.dispose()
        Laf:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateUserInfo$104(boolean, org.telegram.tgnet.TLRPC$UserFull):void");
    }

    public void saveChatInviter(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda87
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChatInviter$105(j2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveChatInviter$105(long j, long j2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET inviter = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                sQLitePreparedStatement.bindLong(2, j2);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void saveChatLinksCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$saveChatLinksCount$106(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveChatLinksCount$106(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET links = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateChatInfo(final TLRPC$ChatFull tLRPC$ChatFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda171
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$107(tLRPC$ChatFull, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0144  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0150  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateChatInfo$107(org.telegram.tgnet.TLRPC$ChatFull r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 345
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateChatInfo$107(org.telegram.tgnet.TLRPC$ChatFull, boolean):void");
    }

    public void updateChatOnlineCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatOnlineCount$108(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChatOnlineCount$108(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updatePinnedMessages(final long j, final ArrayList<Integer> arrayList, final boolean z, final int i, final int i2, final boolean z2, final HashMap<Integer, MessageObject> hashMap) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda202
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updatePinnedMessages$111(z, hashMap, i2, j, arrayList, i, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0330  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0335  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x033a  */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0343  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0348  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x034d  */
    /* JADX WARN: Removed duplicated region for block: B:157:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v29, types: [org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda102] */
    /* JADX WARN: Type inference failed for: r1v9, types: [org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda103] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updatePinnedMessages$111(boolean r19, final java.util.HashMap r20, final int r21, final long r22, final java.util.ArrayList r24, int r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 851
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updatePinnedMessages$111(boolean, java.util.HashMap, int, long, java.util.ArrayList, int, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePinnedMessages$109(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.TRUE, 0, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePinnedMessages$110(long j, ArrayList arrayList, HashMap hashMap, int i, int i2, boolean z) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.FALSE, 0, hashMap, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public void updateChatInfo(final long j, final long j2, final int i, final long j3, final int i2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda77
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatInfo$113(j, i, j2, j3, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChatInfo$113(long j, int i, long j2, long j3, int i2) {
        int i3;
        SQLiteCursor queryFinalized;
        final TLRPC$ChatFull tLRPC$ChatFull;
        TLRPC$ChatParticipant tLRPC$TL_chatParticipant;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                i3 = 0;
                queryFinalized = this.database.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                tLRPC$ChatFull = null;
            } else {
                tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tLRPC$ChatFull.pinned_msg_id = queryFinalized.intValue(1);
                tLRPC$ChatFull.online_count = queryFinalized.intValue(2);
                tLRPC$ChatFull.inviterId = queryFinalized.longValue(3);
            }
            queryFinalized.dispose();
            if (!(tLRPC$ChatFull instanceof TLRPC$TL_chatFull)) {
                return;
            }
            if (i == 1) {
                while (true) {
                    if (i3 >= tLRPC$ChatFull.participants.participants.size()) {
                        break;
                    } else if (tLRPC$ChatFull.participants.participants.get(i3).user_id == j2) {
                        tLRPC$ChatFull.participants.participants.remove(i3);
                        break;
                    } else {
                        i3++;
                    }
                }
            } else if (i == 0) {
                Iterator<TLRPC$ChatParticipant> it = tLRPC$ChatFull.participants.participants.iterator();
                while (it.hasNext()) {
                    if (it.next().user_id == j2) {
                        return;
                    }
                }
                TLRPC$TL_chatParticipant tLRPC$TL_chatParticipant2 = new TLRPC$TL_chatParticipant();
                tLRPC$TL_chatParticipant2.user_id = j2;
                tLRPC$TL_chatParticipant2.inviter_id = j3;
                tLRPC$TL_chatParticipant2.date = getConnectionsManager().getCurrentTime();
                tLRPC$ChatFull.participants.participants.add(tLRPC$TL_chatParticipant2);
            } else if (i == 2) {
                while (true) {
                    if (i3 >= tLRPC$ChatFull.participants.participants.size()) {
                        break;
                    }
                    TLRPC$ChatParticipant tLRPC$ChatParticipant = tLRPC$ChatFull.participants.participants.get(i3);
                    if (tLRPC$ChatParticipant.user_id == j2) {
                        if (j3 == 1) {
                            tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipantAdmin();
                        } else {
                            tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                        }
                        tLRPC$TL_chatParticipant.user_id = tLRPC$ChatParticipant.user_id;
                        tLRPC$TL_chatParticipant.date = tLRPC$ChatParticipant.date;
                        tLRPC$TL_chatParticipant.inviter_id = tLRPC$ChatParticipant.inviter_id;
                        tLRPC$ChatFull.participants.participants.set(i3, tLRPC$TL_chatParticipant);
                    } else {
                        i3++;
                    }
                }
            }
            tLRPC$ChatFull.participants.version = i2;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda170
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateChatInfo$112(tLRPC$ChatFull);
                }
            });
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$ChatFull.getObjectSize());
            tLRPC$ChatFull.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, j);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.bindInteger(3, tLRPC$ChatFull.pinned_msg_id);
            executeFast.bindInteger(4, tLRPC$ChatFull.online_count);
            executeFast.bindLong(5, tLRPC$ChatFull.inviterId);
            executeFast.bindInteger(6, tLRPC$ChatFull.invitesCount);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
            sQLiteCursor.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateChatInfo$112(TLRPC$ChatFull tLRPC$ChatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationName(i, tLRPC$ChatFull, 0, bool, bool);
    }

    public boolean isMigratedChat(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda123
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$isMigratedChat$114(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isMigratedChat$114(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor queryFinalized;
        TLRPC$ChatFull tLRPC$ChatFull;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + j, new Object[0]);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            new ArrayList();
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                tLRPC$ChatFull = null;
            } else {
                tLRPC$ChatFull = TLRPC$ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            zArr[0] = (tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.migrated_from_chat_id != 0;
            countDownLatch.countDown();
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
        countDownLatch.countDown();
    }

    public TLRPC$Message getMessage(final long j, final long j2) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda93
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getMessage$115(j, j2, atomicReference, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return (TLRPC$Message) atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMessage$115(long j, long j2, AtomicReference atomicReference, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteCursor = sQLiteDatabase.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + j + " AND mid = " + j2 + " LIMIT 1", new Object[0]);
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        atomicReference.set(TLdeserialize);
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    public boolean hasInviteMeMessage(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$hasInviteMeMessage$116(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hasInviteMeMessage$116(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                long clientUserId = getUserConfig().getClientUserId();
                SQLiteDatabase sQLiteDatabase = this.database;
                sQLiteCursor = sQLiteDatabase.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + (-j) + " AND out = 0 ORDER BY mid DESC LIMIT 100", new Object[0]);
                while (true) {
                    if (!sQLiteCursor.next()) {
                        break;
                    }
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        TLRPC$MessageAction tLRPC$MessageAction = TLdeserialize.action;
                        if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatAddUser) && tLRPC$MessageAction.users.contains(Long.valueOf(clientUserId))) {
                            zArr[0] = true;
                            break;
                        }
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(22:1|(3:2|3|(3:5|6|7))|(12:(3:270|271|(32:273|274|275|276|10|11|12|13|14|(6:241|242|(4:245|(2:247|248)(1:250)|249|243)|251|252|(1:254))(2:16|(12:172|173|174|175|(11:178|179|(2:212|213)(1:181)|182|183|184|(1:186)(1:208)|(1:190)|(2:206|207)(7:193|(1:195)|196|197|198|199|201)|202|176)|220|221|222|(4:225|(2:227|228)(1:230)|229|223)|231|232|(1:234)))|18|(1:22)|48|49|50|51|52|(5:56|57|58|53|54)|65|66|67|69|70|(6:135|136|137|138|(1:140)|141)(1:72)|73|74|(3:103|104|(10:106|(3:118|119|(5:121|77|(6:79|(1:81)(1:100)|82|83|84|(3:86|(2:88|89)|91))(1:102)|92|93))(1:108)|109|110|111|112|77|(0)(0)|92|93))|76|77|(0)(0)|92|93))|69|70|(0)(0)|73|74|(0)|76|77|(0)(0)|92|93)|9|10|11|12|13|14|(0)(0)|18|(2:20|22)|48|49|50|51|52|(2:53|54)|65|66|67|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(24:1|2|3|(3:5|6|7)|(12:(3:270|271|(32:273|274|275|276|10|11|12|13|14|(6:241|242|(4:245|(2:247|248)(1:250)|249|243)|251|252|(1:254))(2:16|(12:172|173|174|175|(11:178|179|(2:212|213)(1:181)|182|183|184|(1:186)(1:208)|(1:190)|(2:206|207)(7:193|(1:195)|196|197|198|199|201)|202|176)|220|221|222|(4:225|(2:227|228)(1:230)|229|223)|231|232|(1:234)))|18|(1:22)|48|49|50|51|52|(5:56|57|58|53|54)|65|66|67|69|70|(6:135|136|137|138|(1:140)|141)(1:72)|73|74|(3:103|104|(10:106|(3:118|119|(5:121|77|(6:79|(1:81)(1:100)|82|83|84|(3:86|(2:88|89)|91))(1:102)|92|93))(1:108)|109|110|111|112|77|(0)(0)|92|93))|76|77|(0)(0)|92|93))|69|70|(0)(0)|73|74|(0)|76|77|(0)(0)|92|93)|9|10|11|12|13|14|(0)(0)|18|(2:20|22)|48|49|50|51|52|(2:53|54)|65|66|67|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(3:(3:56|57|58)|53|54) */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x0355, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x0356, code lost:
        r19 = r15;
        r2 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x035b, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x0362, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x0364, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x0366, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x0369, code lost:
        r19 = r15;
        r2 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x036e, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x0371, code lost:
        r2 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x0376, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x0377, code lost:
        r19 = r15;
        r2 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x037c, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x0382, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:219:0x0383, code lost:
        r19 = r15;
        r2 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x038a, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x0399, code lost:
        r2 = r7;
     */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0282  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x02e6 A[Catch: all -> 0x0339, Exception -> 0x0344, TRY_LEAVE, TryCatch #36 {Exception -> 0x0344, all -> 0x0339, blocks: (B:137:0x0286, B:166:0x02e0, B:168:0x02e6), top: B:282:0x0286 }] */
    /* JADX WARN: Removed duplicated region for block: B:183:0x031f  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x03ad  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x03c4  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x028b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x0223 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:284:0x0263 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0086 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00ca A[Catch: all -> 0x0376, Exception -> 0x037c, TRY_ENTER, TRY_LEAVE, TryCatch #38 {Exception -> 0x037c, all -> 0x0376, blocks: (B:22:0x0080, B:41:0x00ca), top: B:278:0x0080 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private org.telegram.tgnet.TLRPC$ChatFull loadChatInfoInternal(long r25, boolean r27, boolean r28, boolean r29, int r30) {
        /*
            Method dump skipped, instructions count: 984
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadChatInfoInternal(long, boolean, boolean, boolean, int):org.telegram.tgnet.TLRPC$ChatFull");
    }

    public TLRPC$ChatFull loadChatInfo(long j, boolean z, CountDownLatch countDownLatch, boolean z2, boolean z3) {
        return loadChatInfo(j, z, countDownLatch, z2, z3, 0);
    }

    public TLRPC$ChatFull loadChatInfo(final long j, final boolean z, final CountDownLatch countDownLatch, final boolean z2, final boolean z3, final int i) {
        final TLRPC$ChatFull[] tLRPC$ChatFullArr = new TLRPC$ChatFull[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda205
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$loadChatInfo$117(tLRPC$ChatFullArr, j, z, z2, z3, i, countDownLatch);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable unused) {
            }
        }
        return tLRPC$ChatFullArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChatInfo$117(TLRPC$ChatFull[] tLRPC$ChatFullArr, long j, boolean z, boolean z2, boolean z3, int i, CountDownLatch countDownLatch) {
        tLRPC$ChatFullArr[0] = loadChatInfoInternal(j, z, z2, z3, i);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void processPendingRead(final long j, final int i, final int i2, final int i3) {
        final int i4 = this.lastSavedDate;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda75
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$processPendingRead$118(j, i, i3, i4, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:108:0x01a5  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x01aa  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x01b8  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x01bd  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x01c4  */
    /* JADX WARN: Removed duplicated region for block: B:151:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processPendingRead$118(long r19, int r21, int r22, int r23, int r24) {
        /*
            Method dump skipped, instructions count: 458
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$processPendingRead$118(long, int, int, int, int):void");
    }

    public void putContacts(ArrayList<TLRPC$TL_contact> arrayList, final boolean z) {
        if (!arrayList.isEmpty() || z) {
            final ArrayList arrayList2 = new ArrayList(arrayList);
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda201
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putContacts$119(z, arrayList2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0070  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0077  */
    /* JADX WARN: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putContacts$119(boolean r8, java.util.ArrayList r9) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L12
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            java.lang.String r1 = "DELETE FROM contacts WHERE 1"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.executeFast(r1)     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.stepThis()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            r8.dispose()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
        L12:
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            r8.beginTransaction()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            java.lang.String r1 = "REPLACE INTO contacts VALUES(?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.executeFast(r1)     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            r1 = 0
            r2 = 0
        L21:
            int r3 = r9.size()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            if (r2 >= r3) goto L46
            java.lang.Object r3 = r9.get(r2)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            org.telegram.tgnet.TLRPC$TL_contact r3 = (org.telegram.tgnet.TLRPC$TL_contact) r3     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            r8.requery()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            long r4 = r3.user_id     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            r6 = 1
            r8.bindLong(r6, r4)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            r4 = 2
            boolean r3 = r3.mutual     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            if (r3 == 0) goto L3c
            goto L3d
        L3c:
            r6 = 0
        L3d:
            r8.bindInteger(r4, r6)     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            r8.step()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            int r2 = r2 + 1
            goto L21
        L46:
            r8.dispose()     // Catch: java.lang.Throwable -> L53 java.lang.Exception -> L57
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            r8.commitTransaction()     // Catch: java.lang.Throwable -> L5b java.lang.Exception -> L5d
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database
            if (r8 == 0) goto L6d
            goto L6a
        L53:
            r9 = move-exception
            r0 = r8
            r8 = r9
            goto L6e
        L57:
            r9 = move-exception
            r0 = r8
            r8 = r9
            goto L5e
        L5b:
            r8 = move-exception
            goto L6e
        L5d:
            r8 = move-exception
        L5e:
            org.telegram.messenger.FileLog.e(r8)     // Catch: java.lang.Throwable -> L5b
            if (r0 == 0) goto L66
            r0.dispose()
        L66:
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database
            if (r8 == 0) goto L6d
        L6a:
            r8.commitTransaction()
        L6d:
            return
        L6e:
            if (r0 == 0) goto L73
            r0.dispose()
        L73:
            org.telegram.SQLite.SQLiteDatabase r9 = r7.database
            if (r9 == 0) goto L7a
            r9.commitTransaction()
        L7a:
            goto L7c
        L7b:
            throw r8
        L7c:
            goto L7b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putContacts$119(boolean, java.util.ArrayList):void");
    }

    public void deleteContacts(final ArrayList<Long> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda138
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$deleteContacts$120(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteContacts$120(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM contacts WHERE uid IN(" + join + ")").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void applyPhoneBookUpdates(final String str, final String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda133
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$applyPhoneBookUpdates$121(str, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyPhoneBookUpdates$121(String str, String str2) {
        try {
            if (str.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", str)).stepThis().dispose();
            }
            if (str2.length() == 0) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", str2)).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putCachedPhoneBook(final HashMap<String, ContactsController.Contact> hashMap, final boolean z, boolean z2) {
        if (hashMap != null) {
            if (hashMap.isEmpty() && !z && !z2) {
                return;
            }
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda160
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putCachedPhoneBook$122(hashMap, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:46:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:82:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putCachedPhoneBook$122(java.util.HashMap r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 336
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putCachedPhoneBook$122(java.util.HashMap, boolean):void");
    }

    public void getCachedPhoneBook(final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda195
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getCachedPhoneBook$123(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x013a, code lost:
        if (r10 != null) goto L75;
     */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00f9 A[Catch: all -> 0x0132, TRY_LEAVE, TryCatch #2 {all -> 0x0132, blocks: (B:51:0x00e9, B:53:0x00f9), top: B:132:0x00e9 }] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x012a  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0144 A[Catch: all -> 0x01ee, Exception -> 0x01f0, TRY_ENTER, TryCatch #0 {Exception -> 0x01f0, blocks: (B:76:0x0144, B:79:0x0169, B:81:0x016f, B:83:0x017b, B:85:0x019d, B:86:0x019f, B:88:0x01a3, B:89:0x01a5, B:90:0x01a8, B:93:0x01b0, B:96:0x01bc, B:98:0x01c2, B:100:0x01c8, B:101:0x01cc, B:103:0x01ea, B:77:0x015e), top: B:128:0x0142, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x015e A[Catch: all -> 0x01ee, Exception -> 0x01f0, TryCatch #0 {Exception -> 0x01f0, blocks: (B:76:0x0144, B:79:0x0169, B:81:0x016f, B:83:0x017b, B:85:0x019d, B:86:0x019f, B:88:0x01a3, B:89:0x01a5, B:90:0x01a8, B:93:0x01b0, B:96:0x01bc, B:98:0x01c2, B:100:0x01c8, B:101:0x01cc, B:103:0x01ea, B:77:0x015e), top: B:128:0x0142, outer: #8 }] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x016f A[Catch: all -> 0x01ee, Exception -> 0x01f0, TryCatch #0 {Exception -> 0x01f0, blocks: (B:76:0x0144, B:79:0x0169, B:81:0x016f, B:83:0x017b, B:85:0x019d, B:86:0x019f, B:88:0x01a3, B:89:0x01a5, B:90:0x01a8, B:93:0x01b0, B:96:0x01bc, B:98:0x01c2, B:100:0x01c8, B:101:0x01cc, B:103:0x01ea, B:77:0x015e), top: B:128:0x0142, outer: #8 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getCachedPhoneBook$123(boolean r25) {
        /*
            Method dump skipped, instructions count: 554
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$123(boolean):void");
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getContacts$124();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0080  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getContacts$124() {
        /*
            r11 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 1
            org.telegram.SQLite.SQLiteDatabase r4 = r11.database     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            java.lang.String r5 = "SELECT * FROM contacts WHERE 1"
            r6 = 0
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            r5.<init>()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
        L1c:
            boolean r7 = r4.next()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            if (r7 == 0) goto L4d
            int r7 = r4.intValue(r6)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            long r7 = (long) r7     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            org.telegram.tgnet.TLRPC$TL_contact r9 = new org.telegram.tgnet.TLRPC$TL_contact     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            r9.<init>()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            r9.user_id = r7     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            int r7 = r4.intValue(r3)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            if (r7 != r3) goto L36
            r7 = 1
            goto L37
        L36:
            r7 = 0
        L37:
            r9.mutual = r7     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            int r7 = r5.length()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            if (r7 == 0) goto L44
            java.lang.String r7 = ","
            r5.append(r7)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
        L44:
            r0.add(r9)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            long r7 = r9.user_id     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            r5.append(r7)     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            goto L1c
        L4d:
            r4.dispose()     // Catch: java.lang.Exception -> L5e java.lang.Throwable -> L7c
            int r4 = r5.length()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            if (r4 == 0) goto L74
            java.lang.String r4 = r5.toString()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            r11.getUsersInternal(r4, r1)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L62
            goto L74
        L5e:
            r2 = move-exception
            goto L66
        L60:
            r0 = move-exception
            goto L7e
        L62:
            r4 = move-exception
            r10 = r4
            r4 = r2
            r2 = r10
        L66:
            r0.clear()     // Catch: java.lang.Throwable -> L7c
            r1.clear()     // Catch: java.lang.Throwable -> L7c
            org.telegram.messenger.FileLog.e(r2)     // Catch: java.lang.Throwable -> L7c
            if (r4 == 0) goto L74
            r4.dispose()
        L74:
            org.telegram.messenger.ContactsController r2 = r11.getContactsController()
            r2.processLoadedContacts(r0, r1, r3)
            return
        L7c:
            r0 = move-exception
            r2 = r4
        L7e:
            if (r2 == 0) goto L83
            r2.dispose()
        L83:
            goto L85
        L84:
            throw r0
        L85:
            goto L84
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getContacts$124():void");
    }

    public void getUnsentMessages(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUnsentMessages$125(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:116:0x02bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getUnsentMessages$125(int r20) {
        /*
            Method dump skipped, instructions count: 706
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getUnsentMessages$125(int):void");
    }

    public boolean checkMessageByRandomId(final long j) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda124
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkMessageByRandomId$126(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x002a, code lost:
        if (r0 == null) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkMessageByRandomId$126(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
            r6 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r6.database     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.String r3 = "SELECT random_id FROM randoms_v2 WHERE random_id = %d"
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            r8 = 0
            r5[r8] = r7     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.String r7 = java.lang.String.format(r2, r3, r5)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            java.lang.Object[] r2 = new java.lang.Object[r8]     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r7, r2)     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            boolean r7 = r0.next()     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            if (r7 == 0) goto L2c
            r9[r8] = r4     // Catch: java.lang.Throwable -> L24 java.lang.Exception -> L26
            goto L2c
        L24:
            r7 = move-exception
            goto L33
        L26:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L24
            if (r0 == 0) goto L2f
        L2c:
            r0.dispose()
        L2f:
            r10.countDown()
            return
        L33:
            if (r0 == 0) goto L38
            r0.dispose()
        L38:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$126(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(final long j, final int i) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda85
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkMessageId$127(j, i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0031, code lost:
        if (r0 == null) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$checkMessageId$127(long r6, int r8, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
            r5 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r5.database     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r3 = "SELECT mid FROM messages_v2 WHERE uid = %d AND mid = %d"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r7 = 0
            r4[r7] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r8 = 1
            r4[r8] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r6 = java.lang.String.format(r2, r3, r4)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.Object[] r2 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r6, r2)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            boolean r6 = r0.next()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            if (r6 == 0) goto L33
            r9[r7] = r8     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            goto L33
        L2b:
            r6 = move-exception
            goto L3a
        L2d:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L2b
            if (r0 == 0) goto L36
        L33:
            r0.dispose()
        L36:
            r10.countDown()
            return
        L3a:
            if (r0 == 0) goto L3f
            r0.dispose()
        L3f:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$127(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(final long j, final int i, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda49
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUnreadMention$129(i, j, intCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getUnreadMention$129(int i, long j, final IntCallback intCallback) {
        SQLiteCursor queryFinalized;
        final int i2 = 0;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (i != 0) {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_topics WHERE uid = %d AND topic_id = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j), Integer.valueOf(i)), new Object[0]);
                } else {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_v2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j)), new Object[0]);
                }
                sQLiteCursor = queryFinalized;
                if (sQLiteCursor.next()) {
                    i2 = sQLiteCursor.intValue(0);
                }
                sQLiteCursor.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(i2);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void getMessagesCount(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda110
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getMessagesCount$131(j, intCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMessagesCount$131(long j, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                final int i = 0;
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages_v2 WHERE uid = %d", Long.valueOf(j)), new Object[0]);
                if (sQLiteCursor.next()) {
                    i = sQLiteCursor.intValue(0);
                }
                sQLiteCursor.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(i);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            sQLiteCursor.dispose();
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:189:0x0471
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:82)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:40)
        */
    public java.lang.Runnable getMessagesInternal(long r53, long r55, int r57, int r58, int r59, int r60, int r61, int r62, boolean r63, int r64, int r65, boolean r66, boolean r67) {
        /*
            Method dump skipped, instructions count: 8437
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessagesInternal(long, long, int, int, int, int, int, int, boolean, int, int, boolean, boolean):java.lang.Runnable");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getMessagesInternal$132(TLRPC$Message tLRPC$Message, TLRPC$Message tLRPC$Message2) {
        int i;
        int i2;
        int i3 = tLRPC$Message.id;
        if (i3 > 0 && (i2 = tLRPC$Message2.id) > 0) {
            if (i3 > i2) {
                return -1;
            }
            return i3 < i2 ? 1 : 0;
        } else if (i3 < 0 && (i = tLRPC$Message2.id) < 0) {
            if (i3 < i) {
                return -1;
            }
            return i3 > i ? 1 : 0;
        } else {
            int i4 = tLRPC$Message.date;
            int i5 = tLRPC$Message2.date;
            if (i4 > i5) {
                return -1;
            }
            return i4 < i5 ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMessagesInternal$133(TLRPC$TL_messages_messages tLRPC$TL_messages_messages, int i, long j, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z, boolean z2, int i11, int i12, boolean z3, int i13, boolean z4, boolean z5) {
        getMessagesController().processLoadedMessages(tLRPC$TL_messages_messages, i, j, j2, i2, i3, i4, true, i5, i6, i7, i8, i9, i10, z, z2 ? 1 : 0, i11, i12, z3, i13, z4, z5);
    }

    private void getAnimatedEmoji(String str, ArrayList<TLRPC$Document> arrayList) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                sQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM animated_emoji WHERE document_id IN (%s)", str), new Object[0]);
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    try {
                        TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (TLdeserialize != null && TLdeserialize.id != 0) {
                            arrayList.add(TLdeserialize);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (byteBufferValue != null) {
                        byteBufferValue.reuse();
                    }
                }
            } catch (Throwable th) {
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
                throw th;
            }
        } catch (SQLiteException e2) {
            e2.printStackTrace();
            if (sQLiteCursor == null) {
                return;
            }
        }
        sQLiteCursor.dispose();
    }

    public void getMessages(final long j, final long j2, boolean z, final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final boolean z2, final int i7, final int i8, final boolean z3, final boolean z4) {
        System.currentTimeMillis();
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda90
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getMessages$135(j, j2, i, i2, i3, i4, i5, i6, z2, i7, i8, z3, z4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getMessages$135(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, int i8, boolean z2, boolean z3) {
        final Runnable messagesInternal = getMessagesInternal(j, j2, i, i2, i3, i4, i5, i6, z, i7, i8, z2, z3);
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                messagesInternal.run();
            }
        });
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearSentMedia$136();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearSentMedia$136() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public Object[] getSentFile(final String str, final int i) {
        if (str == null || str.toLowerCase().endsWith("attheme")) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] objArr = new Object[2];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda132
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getSentFile$137(str, i, objArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (objArr[0] == null) {
            return null;
        }
        return objArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getSentFile$137(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        NativeByteBuffer byteBufferValue;
        try {
            try {
                String MD5 = Utilities.MD5(str);
                if (MD5 != null) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", MD5, Integer.valueOf(i)), new Object[0]);
                    if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                        TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize instanceof TLRPC$TL_messageMediaDocument) {
                            objArr[0] = ((TLRPC$TL_messageMediaDocument) TLdeserialize).document;
                        } else if (TLdeserialize instanceof TLRPC$TL_messageMediaPhoto) {
                            objArr[0] = ((TLRPC$TL_messageMediaPhoto) TLdeserialize).photo;
                        }
                        if (objArr[0] != null) {
                            objArr[1] = queryFinalized.stringValue(1);
                        }
                    }
                    queryFinalized.dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    private void updateWidgets(long j) {
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.valueOf(j));
        updateWidgets(arrayList);
    }

    private void updateWidgets(ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        AppWidgetManager appWidgetManager = null;
        try {
            TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT id FROM shortcut_widget WHERE did IN(%s,-1)", TextUtils.join(",", arrayList)), new Object[0]);
            while (queryFinalized.next()) {
                if (appWidgetManager == null) {
                    appWidgetManager = AppWidgetManager.getInstance(ApplicationLoader.applicationContext);
                }
                appWidgetManager.notifyAppWidgetViewDataChanged(queryFinalized.intValue(0), R.id.list_view);
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putWidgetDialogs(final int i, final ArrayList<TopicKey> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWidgetDialogs$138(i, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putWidgetDialogs$138(int i, ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO shortcut_widget VALUES(?, ?, ?)");
            if (arrayList.isEmpty()) {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, -1L);
                executeFast.bindInteger(3, 0);
                executeFast.step();
            } else {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long j = ((TopicKey) arrayList.get(i2)).dialogId;
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindLong(2, j);
                    executeFast.bindInteger(3, i2);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearWidgetDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearWidgetDialogs$139(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearWidgetDialogs$139(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            sQLiteDatabase.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getWidgetDialogIds(final int i, final int i2, final ArrayList<Long> arrayList, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3, final boolean z) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogIds$140(i, arrayList, arrayList2, arrayList3, z, i2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getWidgetDialogIds$140(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, boolean z, int i2, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        long longValue = queryFinalized.longValue(0);
                        if (longValue != -1) {
                            arrayList.add(Long.valueOf(longValue));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(longValue)) {
                                    arrayList4.add(Long.valueOf(longValue));
                                } else {
                                    arrayList5.add(Long.valueOf(-longValue));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        throw th;
                    }
                }
                queryFinalized.dispose();
                if (!z && arrayList.isEmpty()) {
                    if (i2 == 0) {
                        SQLiteCursor queryFinalized2 = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = 0 ORDER BY pinned DESC, date DESC LIMIT 0,10", new Object[0]);
                        while (queryFinalized2.next()) {
                            long longValue2 = queryFinalized2.longValue(0);
                            if (!DialogObject.isFolderDialogId(longValue2)) {
                                arrayList.add(Long.valueOf(longValue2));
                                if (arrayList2 != null && arrayList3 != null) {
                                    if (DialogObject.isUserDialog(longValue2)) {
                                        arrayList4.add(Long.valueOf(longValue2));
                                    } else {
                                        arrayList5.add(Long.valueOf(-longValue2));
                                    }
                                }
                            }
                        }
                        queryFinalized2.dispose();
                    } else {
                        SQLiteCursor queryFinalized3 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                        while (queryFinalized3.next()) {
                            long longValue3 = queryFinalized3.longValue(0);
                            arrayList.add(Long.valueOf(longValue3));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(longValue3)) {
                                    arrayList4.add(Long.valueOf(longValue3));
                                } else {
                                    arrayList5.add(Long.valueOf(-longValue3));
                                }
                            }
                        }
                        queryFinalized3.dispose();
                    }
                }
                if (arrayList2 != null && arrayList3 != null) {
                    if (!arrayList5.isEmpty()) {
                        getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                    }
                    if (!arrayList4.isEmpty()) {
                        getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        countDownLatch.countDown();
    }

    public void getWidgetDialogs(final int i, final int i2, final ArrayList<Long> arrayList, final LongSparseArray<TLRPC$Dialog> longSparseArray, final LongSparseArray<TLRPC$Message> longSparseArray2, final ArrayList<TLRPC$User> arrayList2, final ArrayList<TLRPC$Chat> arrayList3) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getWidgetDialogs$141(i, arrayList, i2, longSparseArray, longSparseArray2, arrayList3, arrayList2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getWidgetDialogs$141(int i, ArrayList arrayList, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, ArrayList arrayList2, ArrayList arrayList3, CountDownLatch countDownLatch) {
        SQLiteCursor queryFinalized;
        boolean z;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList arrayList4 = new ArrayList();
                ArrayList arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized2.next()) {
                    try {
                        long longValue = queryFinalized2.longValue(0);
                        if (longValue != -1) {
                            arrayList.add(Long.valueOf(longValue));
                            if (DialogObject.isUserDialog(longValue)) {
                                arrayList4.add(Long.valueOf(longValue));
                            } else {
                                arrayList5.add(Long.valueOf(-longValue));
                            }
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        throw th;
                    }
                }
                queryFinalized2.dispose();
                if (arrayList.isEmpty() && i2 == 1) {
                    SQLiteCursor queryFinalized3 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                    while (queryFinalized3.next()) {
                        long longValue2 = queryFinalized3.longValue(0);
                        arrayList.add(Long.valueOf(longValue2));
                        if (DialogObject.isUserDialog(longValue2)) {
                            arrayList4.add(Long.valueOf(longValue2));
                        } else {
                            arrayList5.add(Long.valueOf(-longValue2));
                        }
                    }
                    queryFinalized3.dispose();
                }
                if (arrayList.isEmpty()) {
                    queryFinalized = this.database.queryFinalized("SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.folder_id = 0 ORDER BY d.pinned DESC, d.date DESC LIMIT 0,10", new Object[0]);
                    z = true;
                } else {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date FROM dialogs as d LEFT JOIN messages_v2 as m ON d.last_mid = m.mid AND d.did = m.uid WHERE d.did IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
                    z = false;
                }
                while (queryFinalized.next()) {
                    long longValue3 = queryFinalized.longValue(0);
                    if (!DialogObject.isFolderDialogId(longValue3)) {
                        if (z) {
                            arrayList.add(Long.valueOf(longValue3));
                        }
                        TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
                        tLRPC$TL_dialog.id = longValue3;
                        tLRPC$TL_dialog.top_message = queryFinalized.intValue(1);
                        tLRPC$TL_dialog.unread_count = queryFinalized.intValue(2);
                        tLRPC$TL_dialog.last_message_date = queryFinalized.intValue(3);
                        longSparseArray.put(tLRPC$TL_dialog.id, tLRPC$TL_dialog);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(4);
                        if (byteBufferValue != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(5));
                            TLdeserialize.id = queryFinalized.intValue(6);
                            TLdeserialize.send_state = queryFinalized.intValue(7);
                            int intValue = queryFinalized.intValue(8);
                            if (intValue != 0) {
                                tLRPC$TL_dialog.last_message_date = intValue;
                            }
                            long j = tLRPC$TL_dialog.id;
                            TLdeserialize.dialog_id = j;
                            longSparseArray2.put(j, TLdeserialize);
                            addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5, null);
                        }
                    }
                }
                queryFinalized.dispose();
                if (!z && arrayList.size() > longSparseArray.size()) {
                    int size = arrayList.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        long longValue4 = ((Long) arrayList.get(i3)).longValue();
                        if (longSparseArray.get(((Long) arrayList.get(i3)).longValue()) == null) {
                            TLRPC$TL_dialog tLRPC$TL_dialog2 = new TLRPC$TL_dialog();
                            tLRPC$TL_dialog2.id = longValue4;
                            longSparseArray.put(longValue4, tLRPC$TL_dialog2);
                            if (DialogObject.isChatDialog(longValue4)) {
                                long j2 = -longValue4;
                                if (arrayList5.contains(Long.valueOf(j2))) {
                                    arrayList5.add(Long.valueOf(j2));
                                }
                            } else if (arrayList4.contains(Long.valueOf(longValue4))) {
                                arrayList4.add(Long.valueOf(longValue4));
                            }
                        }
                    }
                }
                if (!arrayList5.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", arrayList5), arrayList2);
                }
                if (!arrayList4.isEmpty()) {
                    getUsersInternal(TextUtils.join(",", arrayList4), arrayList3);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        countDownLatch.countDown();
    }

    public void putSentFile(final String str, final TLObject tLObject, final int i, final String str2) {
        if (str == null || tLObject == null || str2 == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda134
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putSentFile$142(str, tLObject, i, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putSentFile$142(String str, TLObject tLObject, int i, String str2) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                String MD5 = Utilities.MD5(str);
                if (MD5 != null) {
                    if (tLObject instanceof TLRPC$Photo) {
                        tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto();
                        tLRPC$MessageMedia.photo = (TLRPC$Photo) tLObject;
                        tLRPC$MessageMedia.flags |= 1;
                    } else if (tLObject instanceof TLRPC$Document) {
                        tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument();
                        tLRPC$MessageMedia.document = (TLRPC$Document) tLObject;
                        tLRPC$MessageMedia.flags |= 1;
                    } else {
                        tLRPC$MessageMedia = null;
                    }
                    if (tLRPC$MessageMedia == null) {
                        return;
                    }
                    sQLitePreparedStatement = this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)");
                    sQLitePreparedStatement.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$MessageMedia.getObjectSize());
                    tLRPC$MessageMedia.serializeToStream(nativeByteBuffer);
                    sQLitePreparedStatement.bindString(1, MD5);
                    sQLitePreparedStatement.bindInteger(2, i);
                    sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
                    sQLitePreparedStatement.bindString(4, str2);
                    sQLitePreparedStatement.step();
                    nativeByteBuffer.reuse();
                }
                if (sQLitePreparedStatement == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatSeq(final TLRPC$EncryptedChat tLRPC$EncryptedChat, final boolean z) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda179
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatSeq$143(tLRPC$EncryptedChat, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEncryptedChatSeq$143(TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.seq_in);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.seq_out);
                sQLitePreparedStatement.bindInteger(3, (tLRPC$EncryptedChat.key_use_count_in << 16) | tLRPC$EncryptedChat.key_use_count_out);
                sQLitePreparedStatement.bindInteger(4, tLRPC$EncryptedChat.in_seq_no);
                sQLitePreparedStatement.bindInteger(5, tLRPC$EncryptedChat.mtproto_seq);
                sQLitePreparedStatement.bindInteger(6, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
                if (z && tLRPC$EncryptedChat.in_seq_no != 0) {
                    long encryptedChatId = DialogObject.getEncryptedChatId(tLRPC$EncryptedChat.id);
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_v2 WHERE mid IN (SELECT m.mid FROM messages_v2 as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d) AND uid = %d", Long.valueOf(encryptedChatId), Integer.valueOf(tLRPC$EncryptedChat.in_seq_no), Long.valueOf(encryptedChatId))).stepThis().dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatTTL(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda176
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatTTL$144(tLRPC$EncryptedChat);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEncryptedChatTTL$144(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.ttl);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatLayer(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda177
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChatLayer$145(tLRPC$EncryptedChat);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEncryptedChatLayer$145(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                sQLitePreparedStatement.bindInteger(1, tLRPC$EncryptedChat.layer);
                sQLitePreparedStatement.bindInteger(2, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChat(final TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda175
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateEncryptedChat$146(tLRPC$EncryptedChat);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEncryptedChat$146(TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        byte[] bArr;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                byte[] bArr2 = tLRPC$EncryptedChat.key_hash;
                if ((bArr2 == null || bArr2.length < 16) && (bArr = tLRPC$EncryptedChat.auth_key) != null) {
                    tLRPC$EncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(bArr);
                }
                sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$EncryptedChat.getObjectSize());
                byte[] bArr3 = tLRPC$EncryptedChat.a_or_b;
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(bArr3 != null ? bArr3.length : 1);
                byte[] bArr4 = tLRPC$EncryptedChat.auth_key;
                NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(bArr4 != null ? bArr4.length : 1);
                byte[] bArr5 = tLRPC$EncryptedChat.future_auth_key;
                NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(bArr5 != null ? bArr5.length : 1);
                byte[] bArr6 = tLRPC$EncryptedChat.key_hash;
                NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(bArr6 != null ? bArr6.length : 1);
                tLRPC$EncryptedChat.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                byte[] bArr7 = tLRPC$EncryptedChat.a_or_b;
                if (bArr7 != null) {
                    nativeByteBuffer2.writeBytes(bArr7);
                }
                byte[] bArr8 = tLRPC$EncryptedChat.auth_key;
                if (bArr8 != null) {
                    nativeByteBuffer3.writeBytes(bArr8);
                }
                byte[] bArr9 = tLRPC$EncryptedChat.future_auth_key;
                if (bArr9 != null) {
                    nativeByteBuffer4.writeBytes(bArr9);
                }
                byte[] bArr10 = tLRPC$EncryptedChat.key_hash;
                if (bArr10 != null) {
                    nativeByteBuffer5.writeBytes(bArr10);
                }
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer2);
                sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer3);
                sQLitePreparedStatement.bindInteger(4, tLRPC$EncryptedChat.ttl);
                sQLitePreparedStatement.bindInteger(5, tLRPC$EncryptedChat.layer);
                sQLitePreparedStatement.bindInteger(6, tLRPC$EncryptedChat.seq_in);
                sQLitePreparedStatement.bindInteger(7, tLRPC$EncryptedChat.seq_out);
                sQLitePreparedStatement.bindInteger(8, (tLRPC$EncryptedChat.key_use_count_in << 16) | tLRPC$EncryptedChat.key_use_count_out);
                sQLitePreparedStatement.bindLong(9, tLRPC$EncryptedChat.exchange_id);
                sQLitePreparedStatement.bindInteger(10, tLRPC$EncryptedChat.key_create_date);
                sQLitePreparedStatement.bindLong(11, tLRPC$EncryptedChat.future_key_fingerprint);
                sQLitePreparedStatement.bindByteBuffer(12, nativeByteBuffer4);
                sQLitePreparedStatement.bindByteBuffer(13, nativeByteBuffer5);
                sQLitePreparedStatement.bindInteger(14, tLRPC$EncryptedChat.in_seq_no);
                sQLitePreparedStatement.bindLong(15, tLRPC$EncryptedChat.admin_id);
                sQLitePreparedStatement.bindInteger(16, tLRPC$EncryptedChat.mtproto_seq);
                sQLitePreparedStatement.bindInteger(17, tLRPC$EncryptedChat.id);
                sQLitePreparedStatement.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                nativeByteBuffer3.reuse();
                nativeByteBuffer4.reuse();
                nativeByteBuffer5.reuse();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void isDialogHasTopMessage(final long j, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$isDialogHasTopMessage$147(j, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0032, code lost:
        if (r1 == null) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$isDialogHasTopMessage$147(long r8, java.lang.Runnable r10) {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r7.database     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.util.Locale r3 = java.util.Locale.US     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.String r4 = "SELECT last_mid FROM dialogs WHERE did = %d"
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.Long r8 = java.lang.Long.valueOf(r8)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            r6[r0] = r8     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.String r8 = java.lang.String.format(r3, r4, r6)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.Object[] r9 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            org.telegram.SQLite.SQLiteCursor r1 = r2.queryFinalized(r8, r9)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            boolean r8 = r1.next()     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            if (r8 == 0) goto L28
            int r8 = r1.intValue(r0)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            if (r8 == 0) goto L28
            r0 = 1
        L28:
            r1.dispose()
            goto L35
        L2c:
            r8 = move-exception
            goto L3b
        L2e:
            r8 = move-exception
            org.telegram.messenger.FileLog.e(r8)     // Catch: java.lang.Throwable -> L2c
            if (r1 == 0) goto L35
            goto L28
        L35:
            if (r0 != 0) goto L3a
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r10)
        L3a:
            return
        L3b:
            if (r1 == 0) goto L40
            r1.dispose()
        L40:
            goto L42
        L41:
            throw r8
        L42:
            goto L41
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$isDialogHasTopMessage$147(long, java.lang.Runnable):void");
    }

    public boolean hasAuthMessage(final int i) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda65
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$hasAuthMessage$148(i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r0 == null) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$hasAuthMessage$148(int r7, boolean[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
            r6 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r6.database     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            java.util.Locale r2 = java.util.Locale.US     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            java.lang.String r3 = "SELECT mid FROM messages_v2 WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1"
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            r5 = 0
            r4[r5] = r7     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            java.lang.String r7 = java.lang.String.format(r2, r3, r4)     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            java.lang.Object[] r2 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r7, r2)     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            boolean r7 = r0.next()     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            r8[r5] = r7     // Catch: java.lang.Throwable -> L22 java.lang.Exception -> L24
            goto L2a
        L22:
            r7 = move-exception
            goto L31
        L24:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L22
            if (r0 == 0) goto L2d
        L2a:
            r0.dispose()
        L2d:
            r9.countDown()
            return
        L31:
            if (r0 == 0) goto L36
            r0.dispose()
        L36:
            r9.countDown()
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$hasAuthMessage$148(int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getEncryptedChat(final long j, final CountDownLatch countDownLatch, final ArrayList<TLObject> arrayList) {
        if (countDownLatch == null || arrayList == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda104
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getEncryptedChat$149(j, arrayList, countDownLatch);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getEncryptedChat$149(long j, ArrayList arrayList, CountDownLatch countDownLatch) {
        try {
            try {
                ArrayList<Long> arrayList2 = new ArrayList<>();
                ArrayList<TLRPC$EncryptedChat> arrayList3 = new ArrayList<>();
                getEncryptedChatsInternal("" + j, arrayList3, arrayList2);
                if (!arrayList3.isEmpty() && !arrayList2.isEmpty()) {
                    ArrayList<TLRPC$User> arrayList4 = new ArrayList<>();
                    getUsersInternal(TextUtils.join(",", arrayList2), arrayList4);
                    if (!arrayList4.isEmpty()) {
                        arrayList.add(arrayList3.get(0));
                        arrayList.add(arrayList4.get(0));
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    public void putEncryptedChat(final TLRPC$EncryptedChat tLRPC$EncryptedChat, final TLRPC$User tLRPC$User, final TLRPC$Dialog tLRPC$Dialog) {
        if (tLRPC$EncryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda178
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putEncryptedChat$150(tLRPC$EncryptedChat, tLRPC$User, tLRPC$Dialog);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0187  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putEncryptedChat$150(org.telegram.tgnet.TLRPC$EncryptedChat r17, org.telegram.tgnet.TLRPC$User r18, org.telegram.tgnet.TLRPC$Dialog r19) {
        /*
            Method dump skipped, instructions count: 395
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putEncryptedChat$150(org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Dialog):void");
    }

    private String formatUserSearchName(TLRPC$User tLRPC$User) {
        StringBuilder sb = new StringBuilder();
        String str = tLRPC$User.first_name;
        if (str != null && str.length() > 0) {
            sb.append(tLRPC$User.first_name);
        }
        String str2 = tLRPC$User.last_name;
        if (str2 != null && str2.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(tLRPC$User.last_name);
        }
        sb.append(";;;");
        String str3 = tLRPC$User.username;
        if (str3 != null && str3.length() > 0) {
            sb.append(tLRPC$User.username);
        }
        return sb.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<TLRPC$User> arrayList) throws Exception {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$User tLRPC$User = arrayList.get(i);
            if (tLRPC$User != null && tLRPC$User.min) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", Long.valueOf(tLRPC$User.id)), new Object[0]);
                if (queryFinalized.next()) {
                    try {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                String str = tLRPC$User.username;
                                if (str != null) {
                                    TLdeserialize.username = str;
                                    TLdeserialize.flags |= 8;
                                } else {
                                    TLdeserialize.username = null;
                                    TLdeserialize.flags &= -9;
                                }
                                if (tLRPC$User.apply_min_photo) {
                                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User.photo;
                                    if (tLRPC$UserProfilePhoto != null) {
                                        TLdeserialize.photo = tLRPC$UserProfilePhoto;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                }
                                tLRPC$User = TLdeserialize;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
            }
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$User.getObjectSize());
            tLRPC$User.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$User.id);
            executeFast.bindString(2, formatUserSearchName(tLRPC$User));
            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
            if (tLRPC$UserStatus != null) {
                if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusRecently) {
                    tLRPC$UserStatus.expires = -100;
                } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastWeek) {
                    tLRPC$UserStatus.expires = -101;
                } else if (tLRPC$UserStatus instanceof TLRPC$TL_userStatusLastMonth) {
                    tLRPC$UserStatus.expires = -102;
                }
                executeFast.bindInteger(3, tLRPC$UserStatus.expires);
            } else {
                executeFast.bindInteger(3, 0);
            }
            executeFast.bindByteBuffer(4, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
        }
        executeFast.dispose();
    }

    public void updateChatDefaultBannedRights(final long j, final TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, final int i) {
        if (tLRPC$TL_chatBannedRights == null || j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda82
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateChatDefaultBannedRights$151(j, i, tLRPC$TL_chatBannedRights);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v15, types: [org.telegram.SQLite.SQLitePreparedStatement] */
    /* JADX WARN: Type inference failed for: r8v5, types: [org.telegram.SQLite.SQLitePreparedStatement] */
    public /* synthetic */ void lambda$updateChatDefaultBannedRights$151(long j, int i, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        SQLiteCursor queryFinalized;
        TLRPC$Chat tLRPC$Chat;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(j)), new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
            j = 0;
        } catch (Throwable th2) {
            th = th2;
            j = 0;
        }
        try {
            if (!queryFinalized.next() || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
                tLRPC$Chat = null;
            } else {
                tLRPC$Chat = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Chat == null) {
                return;
            }
            if (tLRPC$Chat.default_banned_rights != null && i < tLRPC$Chat.version) {
                return;
            }
            tLRPC$Chat.default_banned_rights = tLRPC$TL_chatBannedRights;
            tLRPC$Chat.flags |= 262144;
            tLRPC$Chat.version = i;
            j = this.database.executeFast("UPDATE chats SET data = ? WHERE uid = ?");
            try {
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Chat.getObjectSize());
                tLRPC$Chat.serializeToStream(nativeByteBuffer);
                j.bindByteBuffer(1, nativeByteBuffer);
                j.bindLong(2, tLRPC$Chat.id);
                j.step();
                nativeByteBuffer.reuse();
                j.dispose();
            } catch (Exception e2) {
                e = e2;
                FileLog.e(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
                if (j == 0) {
                    return;
                }
                j.dispose();
            }
        } catch (Exception e3) {
            e = e3;
            sQLiteCursor = queryFinalized;
            j = 0;
        } catch (Throwable th3) {
            th = th3;
            sQLiteCursor = queryFinalized;
            j = 0;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            if (j != 0) {
                j.dispose();
            }
            throw th;
        }
    }

    private void putChatsInternal(ArrayList<TLRPC$Chat> arrayList) throws Exception {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$Chat tLRPC$Chat = arrayList.get(i);
            if (tLRPC$Chat.min) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", Long.valueOf(tLRPC$Chat.id)), new Object[0]);
                if (queryFinalized.next()) {
                    try {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (TLdeserialize != null) {
                                TLdeserialize.title = tLRPC$Chat.title;
                                TLdeserialize.photo = tLRPC$Chat.photo;
                                TLdeserialize.broadcast = tLRPC$Chat.broadcast;
                                TLdeserialize.verified = tLRPC$Chat.verified;
                                TLdeserialize.megagroup = tLRPC$Chat.megagroup;
                                TLdeserialize.call_not_empty = tLRPC$Chat.call_not_empty;
                                TLdeserialize.call_active = tLRPC$Chat.call_active;
                                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
                                if (tLRPC$TL_chatBannedRights != null) {
                                    TLdeserialize.default_banned_rights = tLRPC$TL_chatBannedRights;
                                    TLdeserialize.flags |= 262144;
                                }
                                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
                                if (tLRPC$TL_chatAdminRights != null) {
                                    TLdeserialize.admin_rights = tLRPC$TL_chatAdminRights;
                                    TLdeserialize.flags |= 16384;
                                }
                                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = tLRPC$Chat.banned_rights;
                                if (tLRPC$TL_chatBannedRights2 != null) {
                                    TLdeserialize.banned_rights = tLRPC$TL_chatBannedRights2;
                                    TLdeserialize.flags |= 32768;
                                }
                                String str = tLRPC$Chat.username;
                                if (str != null) {
                                    TLdeserialize.username = str;
                                    TLdeserialize.flags |= 64;
                                } else {
                                    TLdeserialize.username = null;
                                    TLdeserialize.flags &= -65;
                                }
                                tLRPC$Chat = TLdeserialize;
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
            }
            executeFast.requery();
            tLRPC$Chat.flags |= 131072;
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Chat.getObjectSize());
            tLRPC$Chat.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$Chat.id);
            String str2 = tLRPC$Chat.title;
            if (str2 != null) {
                executeFast.bindString(2, str2.toLowerCase());
            } else {
                executeFast.bindString(2, "");
            }
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            this.dialogIsForum.put(-tLRPC$Chat.id, tLRPC$Chat.forum ? 1 : 0);
        }
        executeFast.dispose();
    }

    public void checkMalformed(Exception exc) {
        int i;
        if (exc == null || exc.getMessage() == null || !exc.getMessage().contains("malformed") || (i = this.malformedCleanupCount) >= 3) {
            return;
        }
        this.malformedCleanupCount = i + 1;
        cleanup(false);
    }

    public void getUsersInternal(String str, ArrayList<TLRPC$User> arrayList) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        TLRPC$UserStatus tLRPC$UserStatus = TLdeserialize.status;
                        if (tLRPC$UserStatus != null) {
                            tLRPC$UserStatus.expires = queryFinalized.intValue(1);
                        }
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
                checkMalformed(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getChatsInternal(String str, ArrayList<TLRPC$Chat> arrayList) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Chat TLdeserialize = TLRPC$Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        queryFinalized.dispose();
    }

    public void getEncryptedChatsInternal(String str, ArrayList<TLRPC$EncryptedChat> arrayList, ArrayList<Long> arrayList2) throws Exception {
        if (str == null || str.length() == 0 || arrayList == null) {
            return;
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", str), new Object[0]);
        while (queryFinalized.next()) {
            try {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$EncryptedChat TLdeserialize = TLRPC$EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        long longValue = queryFinalized.longValue(1);
                        TLdeserialize.user_id = longValue;
                        if (arrayList2 != null && !arrayList2.contains(Long.valueOf(longValue))) {
                            arrayList2.add(Long.valueOf(TLdeserialize.user_id));
                        }
                        TLdeserialize.a_or_b = queryFinalized.byteArrayValue(2);
                        TLdeserialize.auth_key = queryFinalized.byteArrayValue(3);
                        TLdeserialize.ttl = queryFinalized.intValue(4);
                        TLdeserialize.layer = queryFinalized.intValue(5);
                        TLdeserialize.seq_in = queryFinalized.intValue(6);
                        TLdeserialize.seq_out = queryFinalized.intValue(7);
                        int intValue = queryFinalized.intValue(8);
                        TLdeserialize.key_use_count_in = (short) (intValue >> 16);
                        TLdeserialize.key_use_count_out = (short) intValue;
                        TLdeserialize.exchange_id = queryFinalized.longValue(9);
                        TLdeserialize.key_create_date = queryFinalized.intValue(10);
                        TLdeserialize.future_key_fingerprint = queryFinalized.longValue(11);
                        TLdeserialize.future_auth_key = queryFinalized.byteArrayValue(12);
                        TLdeserialize.key_hash = queryFinalized.byteArrayValue(13);
                        TLdeserialize.in_seq_no = queryFinalized.intValue(14);
                        long longValue2 = queryFinalized.longValue(15);
                        if (longValue2 != 0) {
                            TLdeserialize.admin_id = longValue2;
                        }
                        TLdeserialize.mtproto_seq = queryFinalized.intValue(16);
                        arrayList.add(TLdeserialize);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        queryFinalized.dispose();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: putUsersAndChatsInternal */
    public void lambda$putUsersAndChats$152(ArrayList<TLRPC$User> arrayList, ArrayList<TLRPC$Chat> arrayList2, boolean z) {
        SQLiteDatabase sQLiteDatabase;
        try {
            if (z) {
                try {
                    this.database.beginTransaction();
                } catch (Exception e) {
                    FileLog.e(e);
                    sQLiteDatabase = this.database;
                    if (sQLiteDatabase == null) {
                        return;
                    }
                }
            }
            putUsersInternal(arrayList);
            putChatsInternal(arrayList2);
            sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                return;
            }
            sQLiteDatabase.commitTransaction();
        } catch (Throwable th) {
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            throw th;
        }
    }

    public void putUsersAndChats(final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final boolean z, boolean z2) {
        if (arrayList == null || !arrayList.isEmpty() || arrayList2 == null || !arrayList2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda156
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$putUsersAndChats$152(arrayList, arrayList2, z);
                    }
                });
            } else {
                lambda$putUsersAndChats$152(arrayList, arrayList2, z);
            }
        }
    }

    public void removeFromDownloadQueue(final long j, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda196
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$removeFromDownloadQueue$153(z, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeFromDownloadQueue$153(boolean z, int i, long j) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (z) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    Locale locale = Locale.US;
                    SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT min(date) FROM download_queue WHERE type = %d", Integer.valueOf(i)), new Object[0]);
                    try {
                        int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                        queryFinalized.dispose();
                        if (intValue != -1) {
                            this.database.executeFast(String.format(locale, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", Integer.valueOf(intValue - 1), Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                } else {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x006f  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x007e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void deleteFromDownloadQueue(final java.util.ArrayList<android.util.Pair<java.lang.Long, java.lang.Integer>> r9, boolean r10) {
        /*
            r8 = this;
            if (r9 == 0) goto L8b
            boolean r0 = r9.isEmpty()
            if (r0 == 0) goto La
            goto L8b
        La:
            r0 = 0
            if (r10 == 0) goto L12
            org.telegram.SQLite.SQLiteDatabase r1 = r8.database     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            r1.beginTransaction()     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
        L12:
            org.telegram.SQLite.SQLiteDatabase r1 = r8.database     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            java.lang.String r2 = "DELETE FROM download_queue WHERE uid = ? AND type = ?"
            org.telegram.SQLite.SQLitePreparedStatement r1 = r1.executeFast(r2)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            r2 = 0
            int r3 = r9.size()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
        L1f:
            if (r2 >= r3) goto L48
            java.lang.Object r4 = r9.get(r2)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            android.util.Pair r4 = (android.util.Pair) r4     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            r1.requery()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            java.lang.Object r5 = r4.first     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            java.lang.Long r5 = (java.lang.Long) r5     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            long r5 = r5.longValue()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            r7 = 1
            r1.bindLong(r7, r5)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            r5 = 2
            java.lang.Object r4 = r4.second     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            int r4 = r4.intValue()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            r1.bindInteger(r5, r4)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            r1.step()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            int r2 = r2 + 1
            goto L1f
        L48:
            r1.dispose()     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L64
            if (r10 == 0) goto L52
            org.telegram.SQLite.SQLiteDatabase r1 = r8.database     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            r1.commitTransaction()     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
        L52:
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda136 r1 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda136     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            r1.<init>()     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch: java.lang.Throwable -> L67 java.lang.Exception -> L69
            if (r10 == 0) goto L7b
            org.telegram.SQLite.SQLiteDatabase r9 = r8.database
            if (r9 == 0) goto L7b
            goto L78
        L61:
            r9 = move-exception
            r0 = r1
            goto L7c
        L64:
            r9 = move-exception
            r0 = r1
            goto L6a
        L67:
            r9 = move-exception
            goto L7c
        L69:
            r9 = move-exception
        L6a:
            org.telegram.messenger.FileLog.e(r9)     // Catch: java.lang.Throwable -> L67
            if (r0 == 0) goto L72
            r0.dispose()
        L72:
            if (r10 == 0) goto L7b
            org.telegram.SQLite.SQLiteDatabase r9 = r8.database
            if (r9 == 0) goto L7b
        L78:
            r9.commitTransaction()
        L7b:
            return
        L7c:
            if (r0 == 0) goto L81
            r0.dispose()
        L81:
            if (r10 == 0) goto L8a
            org.telegram.SQLite.SQLiteDatabase r10 = r8.database
            if (r10 == 0) goto L8a
            r10.commitTransaction()
        L8a:
            throw r9
        L8b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.deleteFromDownloadQueue(java.util.ArrayList, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteFromDownloadQueue$154(ArrayList arrayList) {
        getDownloadController().cancelDownloading(arrayList);
    }

    public void clearDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$clearDownloadQueue$155(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearDownloadQueue$155(int i) {
        try {
            if (i == 0) {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", Integer.valueOf(i))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDownloadQueue$157(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDownloadQueue$157(final int i) {
        int i2;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                final ArrayList arrayList = new ArrayList();
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", Integer.valueOf(i)), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        DownloadObject downloadObject = new DownloadObject();
                        downloadObject.type = queryFinalized.intValue(1);
                        downloadObject.id = queryFinalized.longValue(0);
                        downloadObject.parent = queryFinalized.stringValue(3);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(2);
                        if (byteBufferValue != null) {
                            TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            TLRPC$Document tLRPC$Document = TLdeserialize.document;
                            if (tLRPC$Document != null) {
                                downloadObject.object = tLRPC$Document;
                                downloadObject.secret = MessageObject.isVideoDocument(tLRPC$Document) && (i2 = TLdeserialize.ttl_seconds) > 0 && i2 <= 60;
                            } else {
                                TLRPC$Photo tLRPC$Photo = TLdeserialize.photo;
                                if (tLRPC$Photo != null) {
                                    downloadObject.object = tLRPC$Photo;
                                    int i3 = TLdeserialize.ttl_seconds;
                                    downloadObject.secret = i3 > 0 && i3 <= 60;
                                }
                            }
                            downloadObject.forceCache = (TLdeserialize.flags & Integer.MIN_VALUE) != 0;
                        }
                        arrayList.add(downloadObject);
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                queryFinalized.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda55
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$getDownloadQueue$156(i, arrayList);
                    }
                });
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDownloadQueue$156(int i, ArrayList arrayList) {
        getDownloadController().processDownloadObjects(i, arrayList);
    }

    private int getMessageMediaType(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (!(tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) && !MessageObject.isGifMessage(tLRPC$Message) && !MessageObject.isVoiceMessage(tLRPC$Message) && !MessageObject.isVideoMessage(tLRPC$Message) && !MessageObject.isRoundVideoMessage(tLRPC$Message)) {
                return -1;
            }
            int i = tLRPC$Message.ttl;
            return (i <= 0 || i > 60) ? 0 : 1;
        }
        if (tLRPC$Message instanceof TLRPC$TL_message) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return 1;
            }
        }
        return ((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || MessageObject.isVideoMessage(tLRPC$Message)) ? 0 : -1;
    }

    public void putWebPages(final LongSparseArray<TLRPC$WebPage> longSparseArray) {
        if (isEmpty(longSparseArray)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda126
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putWebPages$159(longSparseArray);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:131:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x01a5  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01aa  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01af  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01bd  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x01c2  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01c7  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x01ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putWebPages$159(androidx.collection.LongSparseArray r18) {
        /*
            Method dump skipped, instructions count: 468
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putWebPages$159(androidx.collection.LongSparseArray):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putWebPages$158(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(final long j, final TLRPC$TL_updates_channelDifferenceTooLong tLRPC$TL_updates_channelDifferenceTooLong, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda83
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$overwriteChannel$161(j, i, tLRPC$TL_updates_channelDifferenceTooLong);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:14:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x01ef  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0251  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$overwriteChannel$161(long r20, int r22, final org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong r23) {
        /*
            Method dump skipped, instructions count: 599
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$161(long, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$overwriteChannel$160(long j, TLRPC$TL_updates_channelDifferenceTooLong tLRPC$TL_updates_channelDifferenceTooLong) {
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.TRUE, tLRPC$TL_updates_channelDifferenceTooLong);
    }

    public void putChannelViews(final LongSparseArray<SparseIntArray> longSparseArray, final LongSparseArray<SparseIntArray> longSparseArray2, final LongSparseArray<SparseArray<TLRPC$MessageReplies>> longSparseArray3, final boolean z) {
        if (!isEmpty(longSparseArray) || !isEmpty(longSparseArray2) || !isEmpty(longSparseArray3)) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda129
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putChannelViews$162(longSparseArray, longSparseArray2, longSparseArray3, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:136:0x023e  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x024e  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0257  */
    /* JADX WARN: Removed duplicated region for block: B:183:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0136 A[Catch: all -> 0x0215, Exception -> 0x0219, TRY_LEAVE, TryCatch #12 {Exception -> 0x0219, all -> 0x0215, blocks: (B:43:0x00d0, B:45:0x00d6, B:47:0x00e7, B:58:0x012b, B:61:0x0136, B:96:0x01d8), top: B:166:0x00d0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putChannelViews$162(androidx.collection.LongSparseArray r23, androidx.collection.LongSparseArray r24, androidx.collection.LongSparseArray r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 605
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putChannelViews$162(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:109:0x00e2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:119:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:120:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x011b  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x011f A[Catch: all -> 0x01ce, Exception -> 0x01d2, TRY_ENTER, TRY_LEAVE, TryCatch #11 {Exception -> 0x01d2, all -> 0x01ce, blocks: (B:3:0x0007, B:22:0x0081, B:36:0x00b9, B:51:0x011f), top: B:115:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01da  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01e6  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01eb  */
    /* renamed from: updateRepliesMaxReadIdInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$updateRepliesMaxReadId$164(final long r19, final int r21, final int r22, int r23) {
        /*
            Method dump skipped, instructions count: 497
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateRepliesMaxReadId$164(long, int, int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRepliesMaxReadIdInternal$163(long j, int i, int i2, int i3) {
        getMessagesController().getTopicsController().updateMaxReadId(j, i, i2, i3);
    }

    public void updateRepliesMaxReadId(final long j, final int i, final int i2, final int i3, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateRepliesMaxReadId$164(j, i, i2, i3);
                }
            });
        } else {
            lambda$updateRepliesMaxReadId$164(j, i, i2, i3);
        }
    }

    public void updateRepliesCount(final long j, final int i, final ArrayList<TLRPC$Peer> arrayList, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateRepliesCount$165(i, j, i3, arrayList, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00ae  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00b3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateRepliesCount$165(int r16, long r17, int r19, java.util.ArrayList r20, int r21) {
        /*
            r15 = this;
            r1 = r15
            r0 = r20
            r2 = r21
            r3 = 0
            org.telegram.SQLite.SQLiteDatabase r4 = r1.database     // Catch: java.lang.Throwable -> L98 java.lang.Exception -> L9b
            java.lang.String r5 = "UPDATE messages_v2 SET replies_data = ? WHERE mid = ? AND uid = ?"
            org.telegram.SQLite.SQLitePreparedStatement r4 = r4.executeFast(r5)     // Catch: java.lang.Throwable -> L98 java.lang.Exception -> L9b
            org.telegram.SQLite.SQLiteDatabase r5 = r1.database     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            java.util.Locale r6 = java.util.Locale.ENGLISH     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            java.lang.String r7 = "SELECT replies_data FROM messages_v2 WHERE mid = %d AND uid = %d"
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            java.lang.Integer r10 = java.lang.Integer.valueOf(r16)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r11 = 0
            r9[r11] = r10     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r12 = r17
            long r12 = -r12
            java.lang.Long r10 = java.lang.Long.valueOf(r12)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r14 = 1
            r9[r14] = r10     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            java.lang.String r6 = java.lang.String.format(r6, r7, r9)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            java.lang.Object[] r7 = new java.lang.Object[r11]     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            boolean r6 = r5.next()     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            if (r6 == 0) goto L4a
            org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r11)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            if (r6 == 0) goto L4a
            int r7 = r6.readInt32(r11)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            org.telegram.tgnet.TLRPC$MessageReplies r7 = org.telegram.tgnet.TLRPC$MessageReplies.TLdeserialize(r6, r7, r11)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            r6.reuse()     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            goto L4b
        L4a:
            r7 = r3
        L4b:
            r5.dispose()     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8e
            if (r7 == 0) goto L88
            int r5 = r7.replies     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            int r5 = r5 + r19
            r7.replies = r5     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            if (r5 >= 0) goto L5a
            r7.replies = r11     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
        L5a:
            if (r0 == 0) goto L63
            r7.recent_repliers = r0     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            int r0 = r7.flags     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r0 = r0 | r8
            r7.flags = r0     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
        L63:
            if (r2 == 0) goto L67
            r7.max_id = r2     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
        L67:
            r4.requery()     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            org.telegram.tgnet.NativeByteBuffer r0 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            int r2 = r7.getObjectSize()     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r0.<init>(r2)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r7.serializeToStream(r0)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r4.bindByteBuffer(r14, r0)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r2 = r16
            r4.bindInteger(r8, r2)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r2 = 3
            r4.bindLong(r2, r12)     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r4.step()     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            r0.reuse()     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
        L88:
            r4.dispose()     // Catch: java.lang.Throwable -> L90 java.lang.Exception -> L94
            goto Laa
        L8c:
            r0 = move-exception
            goto L92
        L8e:
            r0 = move-exception
            goto L96
        L90:
            r0 = move-exception
            r5 = r3
        L92:
            r3 = r4
            goto Lac
        L94:
            r0 = move-exception
            r5 = r3
        L96:
            r3 = r4
            goto L9d
        L98:
            r0 = move-exception
            r5 = r3
            goto Lac
        L9b:
            r0 = move-exception
            r5 = r3
        L9d:
            org.telegram.messenger.FileLog.e(r0)     // Catch: java.lang.Throwable -> Lab
            if (r3 == 0) goto La5
            r3.dispose()
        La5:
            if (r5 == 0) goto Laa
            r5.dispose()
        Laa:
            return
        Lab:
            r0 = move-exception
        Lac:
            if (r3 == 0) goto Lb1
            r3.dispose()
        Lb1:
            if (r5 == 0) goto Lb6
            r5.dispose()
        Lb6:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateRepliesCount$165(int, long, int, java.util.ArrayList, int):void");
    }

    private boolean isValidKeyboardToSave(TLRPC$Message tLRPC$Message) {
        TLRPC$ReplyMarkup tLRPC$ReplyMarkup = tLRPC$Message.reply_markup;
        return tLRPC$ReplyMarkup != null && !(tLRPC$ReplyMarkup instanceof TLRPC$TL_replyInlineMarkup) && (!tLRPC$ReplyMarkup.selective || tLRPC$Message.mentioned);
    }

    public void updateMessageVerifyFlags(final ArrayList<TLRPC$Message> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda147
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateMessageVerifyFlags$166(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageVerifyFlags$166(ArrayList arrayList) {
        SQLiteDatabase sQLiteDatabase;
        SQLiteDatabase sQLiteDatabase2;
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        boolean z = false;
        try {
            try {
                this.database.beginTransaction();
                try {
                    executeFast = this.database.executeFast("UPDATE messages_v2 SET imp = ? WHERE mid = ? AND uid = ?");
                } catch (Exception e) {
                    e = e;
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                executeFast.requery();
                int i2 = tLRPC$Message.stickerVerified;
                executeFast.bindInteger(1, i2 == 0 ? 1 : i2 == 2 ? 2 : 0);
                executeFast.bindInteger(2, tLRPC$Message.id);
                executeFast.bindLong(3, MessageObject.getDialogId(tLRPC$Message));
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e3) {
            e = e3;
            sQLitePreparedStatement = executeFast;
            z = true;
            FileLog.e(e);
            if (z && (sQLiteDatabase2 = this.database) != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th3) {
            th = th3;
            sQLitePreparedStatement = executeFast;
            z = true;
            if (z && (sQLiteDatabase = this.database) != null) {
                sQLiteDatabase.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x032a, code lost:
        if (r12 > r9) goto L300;
     */
    /* JADX WARN: Code restructure failed: missing block: B:425:0x0aef, code lost:
        if (r11.id <= r6) goto L538;
     */
    /* JADX WARN: Code restructure failed: missing block: B:440:0x0b21, code lost:
        if (r11.id <= r3) goto L551;
     */
    /* JADX WARN: Code restructure failed: missing block: B:630:0x0e31, code lost:
        if (r11.post != false) goto L711;
     */
    /* JADX WARN: Removed duplicated region for block: B:1187:0x1b20  */
    /* JADX WARN: Removed duplicated region for block: B:1191:0x1b29  */
    /* JADX WARN: Removed duplicated region for block: B:1193:0x1b2e  */
    /* JADX WARN: Removed duplicated region for block: B:1195:0x1b33  */
    /* JADX WARN: Removed duplicated region for block: B:1197:0x1b38  */
    /* JADX WARN: Removed duplicated region for block: B:1199:0x1b3d  */
    /* JADX WARN: Removed duplicated region for block: B:1201:0x1b42  */
    /* JADX WARN: Removed duplicated region for block: B:1203:0x1b47  */
    /* JADX WARN: Removed duplicated region for block: B:1205:0x1b4c  */
    /* JADX WARN: Removed duplicated region for block: B:1207:0x1b51  */
    /* JADX WARN: Removed duplicated region for block: B:1209:0x1b56  */
    /* JADX WARN: Removed duplicated region for block: B:1214:0x1b5e  */
    /* JADX WARN: Removed duplicated region for block: B:1218:0x1b67  */
    /* JADX WARN: Removed duplicated region for block: B:1220:0x1b6c  */
    /* JADX WARN: Removed duplicated region for block: B:1222:0x1b71  */
    /* JADX WARN: Removed duplicated region for block: B:1224:0x1b76  */
    /* JADX WARN: Removed duplicated region for block: B:1226:0x1b7b  */
    /* JADX WARN: Removed duplicated region for block: B:1228:0x1b80  */
    /* JADX WARN: Removed duplicated region for block: B:1230:0x1b85  */
    /* JADX WARN: Removed duplicated region for block: B:1232:0x1b8a  */
    /* JADX WARN: Removed duplicated region for block: B:1234:0x1b8f  */
    /* JADX WARN: Removed duplicated region for block: B:1236:0x1b94  */
    /* JADX WARN: Removed duplicated region for block: B:1264:0x0var_ A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1335:0x0cac A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1446:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x03af A[Catch: all -> 0x054b, Exception -> 0x0550, TryCatch #120 {Exception -> 0x0550, all -> 0x054b, blocks: (B:97:0x0267, B:99:0x027e, B:101:0x0282, B:103:0x028f, B:105:0x02a0, B:107:0x02a9, B:109:0x02b7, B:110:0x02bf, B:112:0x02cd, B:114:0x02d3, B:116:0x02d9, B:119:0x02df, B:121:0x02e3, B:123:0x02e9, B:126:0x02f3, B:128:0x02fc, B:130:0x0321, B:133:0x032d, B:135:0x033b, B:152:0x03af, B:158:0x03c6, B:160:0x03f4, B:162:0x03fa, B:164:0x040a, B:166:0x040e, B:168:0x0412, B:170:0x0416, B:172:0x041e, B:173:0x0423, B:177:0x043e, B:180:0x0446, B:182:0x045e, B:184:0x0468, B:186:0x0477, B:188:0x047d, B:189:0x0480, B:191:0x048f, B:193:0x04a2, B:195:0x04b3, B:196:0x04bd, B:198:0x04c6, B:200:0x04d6, B:201:0x04de, B:203:0x04e4, B:204:0x04e7, B:206:0x04f4, B:207:0x04fc, B:211:0x0517, B:213:0x051d, B:215:0x0529, B:217:0x052f, B:155:0x03bf, B:140:0x0359, B:142:0x0363, B:143:0x036d, B:145:0x0373, B:147:0x037b, B:149:0x0388, B:150:0x0392, B:228:0x056a, B:230:0x0580, B:232:0x0589, B:233:0x05e1, B:235:0x05e7, B:237:0x05f6, B:240:0x0602, B:242:0x060a, B:244:0x0612, B:246:0x061d, B:248:0x0622, B:250:0x0627, B:251:0x062c, B:253:0x0633, B:255:0x064b, B:259:0x0668, B:261:0x066f, B:263:0x0676, B:265:0x067e, B:270:0x0696, B:266:0x068a, B:256:0x0659, B:278:0x06e1, B:279:0x0723, B:281:0x0729, B:283:0x0743, B:286:0x0750, B:287:0x0755, B:289:0x075f, B:291:0x076c, B:293:0x0773, B:295:0x0784, B:297:0x07a0, B:303:0x07ba, B:305:0x07c5, B:307:0x07cc, B:309:0x07d4, B:317:0x07f3, B:310:0x07de, B:314:0x07eb, B:298:0x07a9, B:302:0x07b5, B:323:0x0812, B:325:0x0819, B:326:0x085f, B:328:0x0865, B:330:0x086f, B:332:0x0874, B:334:0x0879, B:337:0x0886, B:339:0x0890, B:342:0x089a, B:350:0x08bb, B:351:0x08c3, B:353:0x08c9, B:354:0x0911, B:356:0x0917, B:357:0x0924, B:361:0x0969, B:363:0x096f, B:364:0x097f, B:366:0x0985, B:368:0x098f, B:370:0x0999, B:371:0x09a1), top: B:1284:0x0267 }] */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0431  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x0444  */
    /* JADX WARN: Removed duplicated region for block: B:210:0x050d  */
    /* JADX WARN: Removed duplicated region for block: B:213:0x051d A[Catch: all -> 0x054b, Exception -> 0x0550, TryCatch #120 {Exception -> 0x0550, all -> 0x054b, blocks: (B:97:0x0267, B:99:0x027e, B:101:0x0282, B:103:0x028f, B:105:0x02a0, B:107:0x02a9, B:109:0x02b7, B:110:0x02bf, B:112:0x02cd, B:114:0x02d3, B:116:0x02d9, B:119:0x02df, B:121:0x02e3, B:123:0x02e9, B:126:0x02f3, B:128:0x02fc, B:130:0x0321, B:133:0x032d, B:135:0x033b, B:152:0x03af, B:158:0x03c6, B:160:0x03f4, B:162:0x03fa, B:164:0x040a, B:166:0x040e, B:168:0x0412, B:170:0x0416, B:172:0x041e, B:173:0x0423, B:177:0x043e, B:180:0x0446, B:182:0x045e, B:184:0x0468, B:186:0x0477, B:188:0x047d, B:189:0x0480, B:191:0x048f, B:193:0x04a2, B:195:0x04b3, B:196:0x04bd, B:198:0x04c6, B:200:0x04d6, B:201:0x04de, B:203:0x04e4, B:204:0x04e7, B:206:0x04f4, B:207:0x04fc, B:211:0x0517, B:213:0x051d, B:215:0x0529, B:217:0x052f, B:155:0x03bf, B:140:0x0359, B:142:0x0363, B:143:0x036d, B:145:0x0373, B:147:0x037b, B:149:0x0388, B:150:0x0392, B:228:0x056a, B:230:0x0580, B:232:0x0589, B:233:0x05e1, B:235:0x05e7, B:237:0x05f6, B:240:0x0602, B:242:0x060a, B:244:0x0612, B:246:0x061d, B:248:0x0622, B:250:0x0627, B:251:0x062c, B:253:0x0633, B:255:0x064b, B:259:0x0668, B:261:0x066f, B:263:0x0676, B:265:0x067e, B:270:0x0696, B:266:0x068a, B:256:0x0659, B:278:0x06e1, B:279:0x0723, B:281:0x0729, B:283:0x0743, B:286:0x0750, B:287:0x0755, B:289:0x075f, B:291:0x076c, B:293:0x0773, B:295:0x0784, B:297:0x07a0, B:303:0x07ba, B:305:0x07c5, B:307:0x07cc, B:309:0x07d4, B:317:0x07f3, B:310:0x07de, B:314:0x07eb, B:298:0x07a9, B:302:0x07b5, B:323:0x0812, B:325:0x0819, B:326:0x085f, B:328:0x0865, B:330:0x086f, B:332:0x0874, B:334:0x0879, B:337:0x0886, B:339:0x0890, B:342:0x089a, B:350:0x08bb, B:351:0x08c3, B:353:0x08c9, B:354:0x0911, B:356:0x0917, B:357:0x0924, B:361:0x0969, B:363:0x096f, B:364:0x097f, B:366:0x0985, B:368:0x098f, B:370:0x0999, B:371:0x09a1), top: B:1284:0x0267 }] */
    /* JADX WARN: Removed duplicated region for block: B:219:0x0535  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x07d4 A[Catch: all -> 0x054b, Exception -> 0x0550, TryCatch #120 {Exception -> 0x0550, all -> 0x054b, blocks: (B:97:0x0267, B:99:0x027e, B:101:0x0282, B:103:0x028f, B:105:0x02a0, B:107:0x02a9, B:109:0x02b7, B:110:0x02bf, B:112:0x02cd, B:114:0x02d3, B:116:0x02d9, B:119:0x02df, B:121:0x02e3, B:123:0x02e9, B:126:0x02f3, B:128:0x02fc, B:130:0x0321, B:133:0x032d, B:135:0x033b, B:152:0x03af, B:158:0x03c6, B:160:0x03f4, B:162:0x03fa, B:164:0x040a, B:166:0x040e, B:168:0x0412, B:170:0x0416, B:172:0x041e, B:173:0x0423, B:177:0x043e, B:180:0x0446, B:182:0x045e, B:184:0x0468, B:186:0x0477, B:188:0x047d, B:189:0x0480, B:191:0x048f, B:193:0x04a2, B:195:0x04b3, B:196:0x04bd, B:198:0x04c6, B:200:0x04d6, B:201:0x04de, B:203:0x04e4, B:204:0x04e7, B:206:0x04f4, B:207:0x04fc, B:211:0x0517, B:213:0x051d, B:215:0x0529, B:217:0x052f, B:155:0x03bf, B:140:0x0359, B:142:0x0363, B:143:0x036d, B:145:0x0373, B:147:0x037b, B:149:0x0388, B:150:0x0392, B:228:0x056a, B:230:0x0580, B:232:0x0589, B:233:0x05e1, B:235:0x05e7, B:237:0x05f6, B:240:0x0602, B:242:0x060a, B:244:0x0612, B:246:0x061d, B:248:0x0622, B:250:0x0627, B:251:0x062c, B:253:0x0633, B:255:0x064b, B:259:0x0668, B:261:0x066f, B:263:0x0676, B:265:0x067e, B:270:0x0696, B:266:0x068a, B:256:0x0659, B:278:0x06e1, B:279:0x0723, B:281:0x0729, B:283:0x0743, B:286:0x0750, B:287:0x0755, B:289:0x075f, B:291:0x076c, B:293:0x0773, B:295:0x0784, B:297:0x07a0, B:303:0x07ba, B:305:0x07c5, B:307:0x07cc, B:309:0x07d4, B:317:0x07f3, B:310:0x07de, B:314:0x07eb, B:298:0x07a9, B:302:0x07b5, B:323:0x0812, B:325:0x0819, B:326:0x085f, B:328:0x0865, B:330:0x086f, B:332:0x0874, B:334:0x0879, B:337:0x0886, B:339:0x0890, B:342:0x089a, B:350:0x08bb, B:351:0x08c3, B:353:0x08c9, B:354:0x0911, B:356:0x0917, B:357:0x0924, B:361:0x0969, B:363:0x096f, B:364:0x097f, B:366:0x0985, B:368:0x098f, B:370:0x0999, B:371:0x09a1), top: B:1284:0x0267 }] */
    /* JADX WARN: Removed duplicated region for block: B:310:0x07de A[Catch: all -> 0x054b, Exception -> 0x0550, TryCatch #120 {Exception -> 0x0550, all -> 0x054b, blocks: (B:97:0x0267, B:99:0x027e, B:101:0x0282, B:103:0x028f, B:105:0x02a0, B:107:0x02a9, B:109:0x02b7, B:110:0x02bf, B:112:0x02cd, B:114:0x02d3, B:116:0x02d9, B:119:0x02df, B:121:0x02e3, B:123:0x02e9, B:126:0x02f3, B:128:0x02fc, B:130:0x0321, B:133:0x032d, B:135:0x033b, B:152:0x03af, B:158:0x03c6, B:160:0x03f4, B:162:0x03fa, B:164:0x040a, B:166:0x040e, B:168:0x0412, B:170:0x0416, B:172:0x041e, B:173:0x0423, B:177:0x043e, B:180:0x0446, B:182:0x045e, B:184:0x0468, B:186:0x0477, B:188:0x047d, B:189:0x0480, B:191:0x048f, B:193:0x04a2, B:195:0x04b3, B:196:0x04bd, B:198:0x04c6, B:200:0x04d6, B:201:0x04de, B:203:0x04e4, B:204:0x04e7, B:206:0x04f4, B:207:0x04fc, B:211:0x0517, B:213:0x051d, B:215:0x0529, B:217:0x052f, B:155:0x03bf, B:140:0x0359, B:142:0x0363, B:143:0x036d, B:145:0x0373, B:147:0x037b, B:149:0x0388, B:150:0x0392, B:228:0x056a, B:230:0x0580, B:232:0x0589, B:233:0x05e1, B:235:0x05e7, B:237:0x05f6, B:240:0x0602, B:242:0x060a, B:244:0x0612, B:246:0x061d, B:248:0x0622, B:250:0x0627, B:251:0x062c, B:253:0x0633, B:255:0x064b, B:259:0x0668, B:261:0x066f, B:263:0x0676, B:265:0x067e, B:270:0x0696, B:266:0x068a, B:256:0x0659, B:278:0x06e1, B:279:0x0723, B:281:0x0729, B:283:0x0743, B:286:0x0750, B:287:0x0755, B:289:0x075f, B:291:0x076c, B:293:0x0773, B:295:0x0784, B:297:0x07a0, B:303:0x07ba, B:305:0x07c5, B:307:0x07cc, B:309:0x07d4, B:317:0x07f3, B:310:0x07de, B:314:0x07eb, B:298:0x07a9, B:302:0x07b5, B:323:0x0812, B:325:0x0819, B:326:0x085f, B:328:0x0865, B:330:0x086f, B:332:0x0874, B:334:0x0879, B:337:0x0886, B:339:0x0890, B:342:0x089a, B:350:0x08bb, B:351:0x08c3, B:353:0x08c9, B:354:0x0911, B:356:0x0917, B:357:0x0924, B:361:0x0969, B:363:0x096f, B:364:0x097f, B:366:0x0985, B:368:0x098f, B:370:0x0999, B:371:0x09a1), top: B:1284:0x0267 }] */
    /* JADX WARN: Removed duplicated region for block: B:316:0x07f2  */
    /* JADX WARN: Removed duplicated region for block: B:410:0x0aba A[Catch: all -> 0x0a6a, Exception -> 0x0a71, TryCatch #104 {Exception -> 0x0a71, all -> 0x0a6a, blocks: (B:384:0x0a48, B:398:0x0a9d, B:400:0x0aa5, B:407:0x0ab2, B:410:0x0aba, B:411:0x0ac0, B:415:0x0acb, B:416:0x0ad0, B:418:0x0ad5, B:420:0x0ae1, B:422:0x0ae9, B:424:0x0aed, B:427:0x0af3, B:433:0x0b01, B:435:0x0b11, B:437:0x0b1b, B:439:0x0b1f, B:442:0x0b25, B:469:0x0b7b, B:475:0x0ba5, B:484:0x0bbf, B:501:0x0bf6, B:446:0x0b2e, B:431:0x0afa), top: B:1316:0x0a48 }] */
    /* JADX WARN: Removed duplicated region for block: B:414:0x0ac9  */
    /* JADX WARN: Removed duplicated region for block: B:418:0x0ad5 A[Catch: all -> 0x0a6a, Exception -> 0x0a71, TryCatch #104 {Exception -> 0x0a71, all -> 0x0a6a, blocks: (B:384:0x0a48, B:398:0x0a9d, B:400:0x0aa5, B:407:0x0ab2, B:410:0x0aba, B:411:0x0ac0, B:415:0x0acb, B:416:0x0ad0, B:418:0x0ad5, B:420:0x0ae1, B:422:0x0ae9, B:424:0x0aed, B:427:0x0af3, B:433:0x0b01, B:435:0x0b11, B:437:0x0b1b, B:439:0x0b1f, B:442:0x0b25, B:469:0x0b7b, B:475:0x0ba5, B:484:0x0bbf, B:501:0x0bf6, B:446:0x0b2e, B:431:0x0afa), top: B:1316:0x0a48 }] */
    /* JADX WARN: Removed duplicated region for block: B:433:0x0b01 A[Catch: all -> 0x0a6a, Exception -> 0x0a71, TryCatch #104 {Exception -> 0x0a71, all -> 0x0a6a, blocks: (B:384:0x0a48, B:398:0x0a9d, B:400:0x0aa5, B:407:0x0ab2, B:410:0x0aba, B:411:0x0ac0, B:415:0x0acb, B:416:0x0ad0, B:418:0x0ad5, B:420:0x0ae1, B:422:0x0ae9, B:424:0x0aed, B:427:0x0af3, B:433:0x0b01, B:435:0x0b11, B:437:0x0b1b, B:439:0x0b1f, B:442:0x0b25, B:469:0x0b7b, B:475:0x0ba5, B:484:0x0bbf, B:501:0x0bf6, B:446:0x0b2e, B:431:0x0afa), top: B:1316:0x0a48 }] */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0b32  */
    /* JADX WARN: Removed duplicated region for block: B:449:0x0b39  */
    /* JADX WARN: Removed duplicated region for block: B:453:0x0b45  */
    /* JADX WARN: Removed duplicated region for block: B:564:0x0cd1  */
    /* JADX WARN: Removed duplicated region for block: B:567:0x0cd9 A[Catch: all -> 0x0cc5, Exception -> 0x0cc8, TRY_ENTER, TRY_LEAVE, TryCatch #81 {Exception -> 0x0cc8, all -> 0x0cc5, blocks: (B:554:0x0cb4, B:567:0x0cd9, B:571:0x0d07, B:572:0x0d10, B:580:0x0d5d, B:583:0x0d63), top: B:1361:0x0cb4 }] */
    /* JADX WARN: Removed duplicated region for block: B:595:0x0da8  */
    /* JADX WARN: Removed duplicated region for block: B:607:0x0de5  */
    /* JADX WARN: Removed duplicated region for block: B:626:0x0e25 A[Catch: all -> 0x101b, Exception -> 0x1026, TRY_LEAVE, TryCatch #73 {Exception -> 0x1026, all -> 0x101b, blocks: (B:624:0x0e20, B:626:0x0e25, B:636:0x0e3a, B:638:0x0e48, B:640:0x0e53, B:646:0x0e61, B:651:0x0e86, B:677:0x0f0b), top: B:1377:0x0e20 }] */
    /* JADX WARN: Removed duplicated region for block: B:714:0x0fee  */
    /* renamed from: putMessagesInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$putMessages$170(java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r53, boolean r54, boolean r55, int r56, boolean r57, boolean r58, int r59) {
        /*
            Method dump skipped, instructions count: 7066
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$170(java.util.ArrayList, boolean, boolean, int, boolean, boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putMessagesInternal$167(int i) {
        getDownloadController().newDownloadObjectsAvailable(i);
    }

    private void createOrEditTopic(final long j, TLRPC$Message tLRPC$Message) {
        final TLRPC$TL_forumTopic tLRPC$TL_forumTopic = new TLRPC$TL_forumTopic();
        tLRPC$TL_forumTopic.topicStartMessage = tLRPC$Message;
        tLRPC$TL_forumTopic.top_message = tLRPC$Message.id;
        tLRPC$TL_forumTopic.topMessage = tLRPC$Message;
        tLRPC$TL_forumTopic.from_id = getMessagesController().getPeer(getUserConfig().clientUserId);
        tLRPC$TL_forumTopic.notify_settings = new TLRPC$TL_peerNotifySettings();
        int i = 0;
        tLRPC$TL_forumTopic.unread_count = 0;
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionTopicCreate) {
            TLRPC$TL_messageActionTopicCreate tLRPC$TL_messageActionTopicCreate = (TLRPC$TL_messageActionTopicCreate) tLRPC$MessageAction;
            tLRPC$TL_forumTopic.id = tLRPC$Message.id;
            long j2 = tLRPC$TL_messageActionTopicCreate.icon_emoji_id;
            tLRPC$TL_forumTopic.icon_emoji_id = j2;
            tLRPC$TL_forumTopic.title = tLRPC$TL_messageActionTopicCreate.title;
            tLRPC$TL_forumTopic.icon_color = tLRPC$TL_messageActionTopicCreate.icon_color;
            if (j2 != 0) {
                tLRPC$TL_forumTopic.flags |= 1;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(tLRPC$TL_forumTopic);
            saveTopics(j, arrayList, false, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda114
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$createOrEditTopic$168(j, tLRPC$TL_forumTopic);
                }
            });
        } else if (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionTopicEdit)) {
        } else {
            TLRPC$TL_messageActionTopicEdit tLRPC$TL_messageActionTopicEdit = (TLRPC$TL_messageActionTopicEdit) tLRPC$MessageAction;
            tLRPC$TL_forumTopic.id = MessageObject.getTopicId(tLRPC$Message);
            tLRPC$TL_forumTopic.icon_emoji_id = tLRPC$TL_messageActionTopicEdit.icon_emoji_id;
            tLRPC$TL_forumTopic.title = tLRPC$TL_messageActionTopicEdit.title;
            tLRPC$TL_forumTopic.closed = tLRPC$TL_messageActionTopicEdit.closed;
            int i2 = tLRPC$TL_messageActionTopicEdit.flags;
            if ((i2 & 1) != 0) {
                i = 1;
            }
            if ((i2 & 2) != 0) {
                i += 2;
            }
            if ((i2 & 4) != 0) {
                i += 8;
            }
            final int i3 = i;
            updateTopicData(j, tLRPC$TL_forumTopic, i3);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda116
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$createOrEditTopic$169(j, tLRPC$TL_forumTopic, i3);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOrEditTopic$168(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
        getMessagesController().getTopicsController().onTopicCreated(j, tLRPC$TL_forumTopic, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOrEditTopic$169(long j, TLRPC$TL_forumTopic tLRPC$TL_forumTopic, int i) {
        getMessagesController().getTopicsController().updateTopicInUi(j, tLRPC$TL_forumTopic, i);
    }

    public void putMessages(ArrayList<TLRPC$Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4, int i2) {
        putMessages(arrayList, z, z2, z3, i, false, z4, i2);
    }

    public void putMessages(final ArrayList<TLRPC$Message> arrayList, final boolean z, boolean z2, final boolean z3, final int i, final boolean z4, final boolean z5, final int i2) {
        if (arrayList.size() == 0) {
            return;
        }
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda158
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$putMessages$170(arrayList, z, z3, i, z4, z5, i2);
                }
            });
        } else {
            lambda$putMessages$170(arrayList, z, z3, i, z4, z5, i2);
        }
    }

    public void markMessageAsSendError(final TLRPC$Message tLRPC$Message, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda183
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessageAsSendError$171(tLRPC$Message, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessageAsSendError$171(TLRPC$Message tLRPC$Message, boolean z) {
        try {
            long j = tLRPC$Message.id;
            if (z) {
                this.database.executeFast(String.format(Locale.US, "UPDATE scheduled_messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(tLRPC$Message)))).stepThis().dispose();
            } else {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(tLRPC$Message)))).stepThis().dispose();
                this.database.executeFast(String.format(locale, "UPDATE messages_topics SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(tLRPC$Message)))).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(final int i, final int i2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setMessageSeq$172(i, i2, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setMessageSeq$172(int i, int i2, int i3) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindInteger(2, i2);
                sQLitePreparedStatement.bindInteger(3, i3);
                sQLitePreparedStatement.step();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(21:241|242|(1:244)|245|(0)|4|(0)|6|(0)|20|(0)|178|179|(4:180|181|(0)(0)|185)|(0)(0)|162|163|(0)|168|28|(0)(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(22:1|(7:229|230|231|232|(3:234|235|236)(1:248)|237|(1:239))(1:3)|4|(2:202|(18:204|205|206|207|208|209|210|211|20|(13:178|179|180|181|(2:183|184)(1:187)|185|(1:26)(1:177)|162|163|(2:165|166)|168|28|(1:30)(1:(4:70|(18:83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|100)(3:72|73|74)|75|76)(10:33|(1:35)(1:65)|36|37|38|(1:40)|42|(1:44)|45|46)))(1:23)|24|(0)(0)|162|163|(0)|168|28|(0)(0)))|6|(6:8|(1:10)(1:19)|11|12|13|14)|20|(0)|178|179|180|181|(0)(0)|185|(0)(0)|162|163|(0)|168|28|(0)(0)|(10:(0)|(1:172)|(1:80)|(1:105)|(1:128)|(0)|(0)|(0)|(0)|(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(5:(2:133|134)|92|93|94|(7:95|96|97|(2:98|99)|100|75|76)) */
    /* JADX WARN: Code restructure failed: missing block: B:101:0x019a, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x019b, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x019e, code lost:
        if (r8 == null) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x0320, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x0321, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x0324, code lost:
        if (r3 != null) goto L94;
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x038f, code lost:
        if (r5 == null) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x03f6, code lost:
        if (r5 != null) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x009c, code lost:
        if (r10 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x0158, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0159, code lost:
        r8 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x015d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x015e, code lost:
        r8 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x016c, code lost:
        if (r4 == 1) goto L162;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:107:0x01a9 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:108:0x01aa  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x021f  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x02d6  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x02dc  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x0374  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x03a9  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x03ae  */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0418  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x0072 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0058 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00b5  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0122 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0149 A[Catch: Exception -> 0x0156, all -> 0x0415, TRY_LEAVE, TryCatch #24 {Exception -> 0x0156, blocks: (B:75:0x0143, B:77:0x0149), top: B:255:0x0143 }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x016f  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x018f A[Catch: all -> 0x0197, Exception -> 0x019a, TRY_LEAVE, TryCatch #10 {Exception -> 0x019a, blocks: (B:95:0x0170, B:97:0x018f), top: B:236:0x0170, outer: #32 }] */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v10, types: [org.telegram.SQLite.SQLitePreparedStatement] */
    /* JADX WARN: Type inference failed for: r10v25 */
    /* JADX WARN: Type inference failed for: r10v26 */
    /* JADX WARN: Type inference failed for: r10v27 */
    /* JADX WARN: Type inference failed for: r10v6, types: [long] */
    /* JADX WARN: Type inference failed for: r10v7 */
    /* renamed from: updateMessageStateAndIdInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long[] lambda$updateMessageStateAndId$174(long r20, long r22, java.lang.Integer r24, int r25, int r26, int r27) {
        /*
            Method dump skipped, instructions count: 1054
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateMessageStateAndId$174(long, long, java.lang.Integer, int, int, int):long[]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateMessageStateAndIdInternal$173(TLRPC$TL_updates tLRPC$TL_updates) {
        getMessagesController().processUpdates(tLRPC$TL_updates, false);
    }

    public long[] updateMessageStateAndId(final long j, final long j2, final Integer num, final int i, final int i2, boolean z, final int i3) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda91
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateMessageStateAndId$174(j, j2, num, i, i2, i3);
                }
            });
            return null;
        }
        return lambda$updateMessageStateAndId$174(j, j2, num, i, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateUsersInternal */
    public void lambda$updateUsers$175(ArrayList<TLRPC$User> arrayList, boolean z, boolean z2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (z) {
                    if (z2) {
                        this.database.beginTransaction();
                    }
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
                    try {
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            TLRPC$User tLRPC$User = arrayList.get(i);
                            executeFast.requery();
                            TLRPC$UserStatus tLRPC$UserStatus = tLRPC$User.status;
                            if (tLRPC$UserStatus != null) {
                                executeFast.bindInteger(1, tLRPC$UserStatus.expires);
                            } else {
                                executeFast.bindInteger(1, 0);
                            }
                            executeFast.bindLong(2, tLRPC$User.id);
                            executeFast.step();
                        }
                        executeFast.dispose();
                        if (z2) {
                            this.database.commitTransaction();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = executeFast;
                        FileLog.e(e);
                        checkMalformed(e);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        if (sQLiteDatabase != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement == null) {
                            return;
                        }
                        sQLitePreparedStatement.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = executeFast;
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    LongSparseArray longSparseArray = new LongSparseArray();
                    int size2 = arrayList.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC$User tLRPC$User2 = arrayList.get(i2);
                        if (sb.length() != 0) {
                            sb.append(",");
                        }
                        sb.append(tLRPC$User2.id);
                        longSparseArray.put(tLRPC$User2.id, tLRPC$User2);
                    }
                    ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
                    getUsersInternal(sb.toString(), arrayList2);
                    int size3 = arrayList2.size();
                    for (int i3 = 0; i3 < size3; i3++) {
                        TLRPC$User tLRPC$User3 = arrayList2.get(i3);
                        TLRPC$User tLRPC$User4 = (TLRPC$User) longSparseArray.get(tLRPC$User3.id);
                        if (tLRPC$User4 != null) {
                            if (tLRPC$User4.first_name != null && tLRPC$User4.last_name != null) {
                                if (!UserObject.isContact(tLRPC$User3)) {
                                    tLRPC$User3.first_name = tLRPC$User4.first_name;
                                    tLRPC$User3.last_name = tLRPC$User4.last_name;
                                }
                                tLRPC$User3.username = tLRPC$User4.username;
                            } else {
                                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = tLRPC$User4.photo;
                                if (tLRPC$UserProfilePhoto != null) {
                                    tLRPC$User3.photo = tLRPC$UserProfilePhoto;
                                } else {
                                    String str = tLRPC$User4.phone;
                                    if (str != null) {
                                        tLRPC$User3.phone = str;
                                    }
                                }
                            }
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        if (z2) {
                            this.database.beginTransaction();
                        }
                        putUsersInternal(arrayList2);
                        if (z2) {
                            this.database.commitTransaction();
                        }
                    }
                }
                SQLiteDatabase sQLiteDatabase3 = this.database;
                if (sQLiteDatabase3 == null) {
                    return;
                }
                sQLiteDatabase3.commitTransaction();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateUsers(final ArrayList<TLRPC$User> arrayList, final boolean z, final boolean z2, boolean z3) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (z3) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda157
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateUsers$175(arrayList, z, z2);
                }
            });
        } else {
            lambda$updateUsers$175(arrayList, z, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:61:? A[RETURN, SYNTHETIC] */
    /* renamed from: markMessagesAsReadInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$markMessagesAsRead$177(org.telegram.messenger.support.LongSparseIntArray r18, org.telegram.messenger.support.LongSparseIntArray r19, android.util.SparseIntArray r20) {
        /*
            Method dump skipped, instructions count: 243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsRead$177(org.telegram.messenger.support.LongSparseIntArray, org.telegram.messenger.support.LongSparseIntArray, android.util.SparseIntArray):void");
    }

    private void markMessagesContentAsReadInternal(long j, ArrayList<Integer> arrayList, int i) {
        SQLiteCursor sQLiteCursor = null;
        ArrayList<Integer> arrayList2 = null;
        sQLiteCursor = null;
        try {
            try {
                String join = TextUtils.join(",", arrayList);
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid IN (%s) AND uid = %d", join, Long.valueOf(j))).stepThis().dispose();
                if (i == 0) {
                    return;
                }
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(locale, "SELECT mid, ttl FROM messages_v2 WHERE mid IN (%s) AND uid = %d AND ttl > 0", join, Long.valueOf(j)), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList<>();
                        }
                        arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = queryFinalized;
                        FileLog.e(e);
                        if (sQLiteCursor == null) {
                            return;
                        }
                        sQLiteCursor.dispose();
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = queryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                if (arrayList2 != null) {
                    emptyMessagesMedia(j, arrayList2);
                }
                queryFinalized.dispose();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void markMessagesContentAsRead(final long j, final ArrayList<Integer> arrayList, final int i) {
        if (isEmpty(arrayList)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda101
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessagesContentAsRead$176(j, arrayList, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:28:0x007c  */
    /* JADX WARN: Type inference failed for: r9v1 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$markMessagesContentAsRead$176(long r7, java.util.ArrayList r9, int r10) {
        /*
            r6 = this;
            r0 = 0
            int r2 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r2 != 0) goto L80
            r7 = 0
            androidx.collection.LongSparseArray r8 = new androidx.collection.LongSparseArray     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            r8.<init>()     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            org.telegram.SQLite.SQLiteDatabase r0 = r6.database     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.util.Locale r1 = java.util.Locale.US     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.lang.String r2 = "SELECT uid, mid FROM messages_v2 WHERE mid IN (%s) AND is_channel = 0"
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.lang.String r5 = ","
            java.lang.String r9 = android.text.TextUtils.join(r5, r9)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            r5 = 0
            r4[r5] = r9     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.lang.String r9 = java.lang.String.format(r1, r2, r4)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.lang.Object[] r1 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            org.telegram.SQLite.SQLiteCursor r9 = r0.queryFinalized(r9, r1)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
        L28:
            boolean r0 = r9.next()     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            if (r0 == 0) goto L4e
            long r0 = r9.longValue(r5)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            java.lang.Object r2 = r8.get(r0)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            if (r2 != 0) goto L42
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            r2.<init>()     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            r8.put(r0, r2)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
        L42:
            int r0 = r9.intValue(r3)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            r2.add(r0)     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            goto L28
        L4e:
            r9.dispose()     // Catch: java.lang.Exception -> L67 java.lang.Throwable -> L79
            int r9 = r8.size()     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
        L55:
            if (r5 >= r9) goto L83
            long r0 = r8.keyAt(r5)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.lang.Object r2 = r8.valueAt(r5)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            r6.markMessagesContentAsReadInternal(r0, r2, r10)     // Catch: java.lang.Throwable -> L69 java.lang.Exception -> L6d
            int r5 = r5 + 1
            goto L55
        L67:
            r7 = move-exception
            goto L70
        L69:
            r8 = move-exception
            r9 = r7
            r7 = r8
            goto L7a
        L6d:
            r8 = move-exception
            r9 = r7
            r7 = r8
        L70:
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L79
            if (r9 == 0) goto L83
            r9.dispose()
            goto L83
        L79:
            r7 = move-exception
        L7a:
            if (r9 == 0) goto L7f
            r9.dispose()
        L7f:
            throw r7
        L80:
            r6.markMessagesContentAsReadInternal(r7, r9, r10)
        L83:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesContentAsRead$176(long, java.util.ArrayList, int):void");
    }

    public void markMessagesAsRead(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda165
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsRead$177(longSparseIntArray, longSparseIntArray2, sparseIntArray);
                }
            });
        } else {
            lambda$markMessagesAsRead$177(longSparseIntArray, longSparseIntArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(final ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda135
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$179(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$179(ArrayList arrayList) {
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, uid FROM randoms_v2 WHERE random_id IN(%s)", TextUtils.join(",", arrayList)), new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(1);
                ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    longSparseArray.put(longValue, arrayList2);
                }
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (longSparseArray.isEmpty()) {
                return;
            }
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                long keyAt = longSparseArray.keyAt(i);
                final ArrayList<Integer> arrayList3 = (ArrayList) longSparseArray.valueAt(i);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda143
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.this.lambda$markMessagesAsDeletedByRandoms$178(arrayList3);
                    }
                });
                updateDialogsWithReadMessagesInternal(arrayList3, null, null, null, null);
                lambda$markMessagesAsDeleted$183(keyAt, arrayList3, true, false);
                lambda$updateDialogsWithDeletedMessages$182(keyAt, 0L, arrayList3, null);
            }
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
            sQLiteCursor.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$178(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, 0L, Boolean.FALSE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", Long.valueOf(j), TextUtils.join(",", arrayList))).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastScheduledMessagesChange(final Long l) {
        final int i;
        SQLiteCursor queryFinalized;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                i = 0;
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM scheduled_messages_v2 WHERE uid = %d", l), new Object[0]);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda131
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$broadcastScheduledMessagesChange$180(l, i);
                }
            });
        } catch (Exception e2) {
            e = e2;
            sQLiteCursor = queryFinalized;
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
            sQLiteCursor.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLiteCursor = queryFinalized;
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastScheduledMessagesChange$180(Long l, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, l, Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(20:(2:46|(8:47|48|49|50|51|(2:560|561)|53|54))|(3:56|57|(14:59|(3:61|62|63)(1:558)|64|(1:66)|(1:553)|70|71|(7:78|79|80|81|82|83|84)(1:74)|75|41|42|43|44|(28:571|90|91|(6:93|94|(9:98|99|100|101|(10:504|505|506|507|508|509|510|511|(2:(1:514)|515)|516)(1:103)|(9:108|109|110|111|112|113|114|115|(7:117|118|119|(1:121)|122|(1:124)|(2:129|130)(1:128))(2:489|490))(2:105|106)|107|95|96)|535|536|136)(1:542)|137|(8:(2:140|(1:142)(1:175))(2:176|(2:178|(1:180)(1:181))(2:182|(3:184|185|150)(1:186)))|143|144|(1:146)|147|148|149|150)|187|188|(9:191|192|(1:194)(1:205)|195|196|197|198|199|189)|206|207|208|(4:210|(12:213|214|215|(1:217)(1:233)|218|219|220|221|222|(2:224|225)(1:227)|226|211)|241|242)(1:485)|243|244|(19:246|247|248|(2:250|(7:252|253|254|255|256|257|258)(1:449))(1:450)|259|260|261|(1:263)(1:432)|264|(5:413|414|415|(4:417|418|419|420)|426)|266|(6:268|269|(7:272|(1:274)|275|(1:277)(1:283)|(2:279|280)(1:282)|281|270)|284|285|(14:287|288|289|(6:291|292|293|(9:295|296|297|299|300|(1:302)(1:309)|303|(2:305|306)(1:308)|307)|319|320)|326|327|328|329|330|331|(5:333|(7:336|(1:338)|339|(1:341)(1:347)|(2:343|344)(1:346)|345|334)|348|349|(6:351|352|(5:354|355|(11:360|361|362|363|364|(1:366)(1:374)|367|(2:369|370)(2:372|373)|371|357|358)|382|383)|394|395|396)(1:406))(1:407)|397|398|399))|412|330|331|(0)(0)|397|398|399)|456|457|458|(2:460|(1:462)(1:463))|464|(2:466|(1:468)(1:469))|470|(1:472)|473|(4:475|(2:478|476)|479|480)|(1:482)|484)(0)))|559|70|71|(0)|76|78|79|80|81|82|83|84|75|41|42|43|44|(0)(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(7:246|(10:247|248|(2:250|(7:252|253|254|255|256|257|258)(1:449))(1:450)|259|260|261|(1:263)(1:432)|264|(5:413|414|415|(4:417|418|419|420)|426)|266)|(4:(6:268|269|(7:272|(1:274)|275|(1:277)(1:283)|(2:279|280)(1:282)|281|270)|284|285|(14:287|288|289|(6:291|292|293|(9:295|296|297|299|300|(1:302)(1:309)|303|(2:305|306)(1:308)|307)|319|320)|326|327|328|329|330|331|(5:333|(7:336|(1:338)|339|(1:341)(1:347)|(2:343|344)(1:346)|345|334)|348|349|(6:351|352|(5:354|355|(11:360|361|362|363|364|(1:366)(1:374)|367|(2:369|370)(2:372|373)|371|357|358)|382|383)|394|395|396)(1:406))(1:407)|397|398|399))|397|398|399)|412|330|331|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:408:0x0ad5, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0216, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0229, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x022a, code lost:
        r25 = r42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0232, code lost:
        r42 = r3;
        r18 = r6;
        r29 = r24;
        r24 = r25;
        r13 = 0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 1, insn: 0x0CLASSNAME: MOVE  (r16 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:481:0x0CLASSNAME */
    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0053: MOVE  (r11 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:14:0x0052 */
    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0CLASSNAME: MOVE  (r11 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:481:0x0CLASSNAME */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0279 A[Catch: all -> 0x0CLASSNAME, Exception -> 0x0CLASSNAME, TRY_ENTER, TRY_LEAVE, TryCatch #57 {Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME, blocks: (B:3:0x0006, B:6:0x0012, B:19:0x005e, B:21:0x0083, B:26:0x009a, B:28:0x00d8, B:102:0x0279, B:186:0x03cd, B:191:0x03dd, B:207:0x0466, B:208:0x046e, B:192:0x03ec, B:197:0x0404, B:198:0x0413, B:202:0x042b, B:213:0x047e, B:214:0x0499, B:216:0x049f, B:222:0x04da, B:231:0x0538, B:233:0x053e, B:234:0x0547, B:236:0x054d, B:242:0x0590, B:244:0x05cc, B:246:0x05e0, B:247:0x05e5, B:258:0x0607, B:30:0x00fd), top: B:520:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:185:0x03c3  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x03d5  */
    /* JADX WARN: Removed duplicated region for block: B:216:0x049f A[Catch: all -> 0x0CLASSNAME, Exception -> 0x0CLASSNAME, TRY_LEAVE, TryCatch #57 {Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME, blocks: (B:3:0x0006, B:6:0x0012, B:19:0x005e, B:21:0x0083, B:26:0x009a, B:28:0x00d8, B:102:0x0279, B:186:0x03cd, B:191:0x03dd, B:207:0x0466, B:208:0x046e, B:192:0x03ec, B:197:0x0404, B:198:0x0413, B:202:0x042b, B:213:0x047e, B:214:0x0499, B:216:0x049f, B:222:0x04da, B:231:0x0538, B:233:0x053e, B:234:0x0547, B:236:0x054d, B:242:0x0590, B:244:0x05cc, B:246:0x05e0, B:247:0x05e5, B:258:0x0607, B:30:0x00fd), top: B:520:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:233:0x053e A[Catch: all -> 0x0CLASSNAME, Exception -> 0x0CLASSNAME, TryCatch #57 {Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME, blocks: (B:3:0x0006, B:6:0x0012, B:19:0x005e, B:21:0x0083, B:26:0x009a, B:28:0x00d8, B:102:0x0279, B:186:0x03cd, B:191:0x03dd, B:207:0x0466, B:208:0x046e, B:192:0x03ec, B:197:0x0404, B:198:0x0413, B:202:0x042b, B:213:0x047e, B:214:0x0499, B:216:0x049f, B:222:0x04da, B:231:0x0538, B:233:0x053e, B:234:0x0547, B:236:0x054d, B:242:0x0590, B:244:0x05cc, B:246:0x05e0, B:247:0x05e5, B:258:0x0607, B:30:0x00fd), top: B:520:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:257:0x0604  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0610  */
    /* JADX WARN: Removed duplicated region for block: B:349:0x0920 A[Catch: Exception -> 0x0ad5, all -> 0x0CLASSNAME, TryCatch #45 {all -> 0x0CLASSNAME, blocks: (B:470:0x0c5d, B:347:0x091a, B:349:0x0920, B:350:0x093e, B:352:0x0944, B:354:0x0958, B:355:0x095d, B:357:0x0965, B:360:0x097c, B:361:0x0981, B:358:0x0973, B:362:0x0990), top: B:520:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x011f  */
    /* JADX WARN: Removed duplicated region for block: B:400:0x0a74  */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0b28  */
    /* JADX WARN: Removed duplicated region for block: B:433:0x0b61  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0ba5 A[Catch: all -> 0x0CLASSNAME, Exception -> 0x0CLASSNAME, TryCatch #55 {Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME, blocks: (B:270:0x0638, B:277:0x0699, B:275:0x0662, B:425:0x0b07, B:429:0x0b2e, B:430:0x0b3e, B:431:0x0b5b, B:435:0x0b67, B:436:0x0b77, B:437:0x0b94, B:439:0x0ba5, B:440:0x0ba9, B:443:0x0bb1, B:445:0x0bb7, B:446:0x0CLASSNAME, B:448:0x0c1f), top: B:553:0x0638 }] */
    /* JADX WARN: Removed duplicated region for block: B:442:0x0bb0  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0c1f A[Catch: all -> 0x0CLASSNAME, Exception -> 0x0CLASSNAME, TRY_LEAVE, TryCatch #55 {Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME, blocks: (B:270:0x0638, B:277:0x0699, B:275:0x0662, B:425:0x0b07, B:429:0x0b2e, B:430:0x0b3e, B:431:0x0b5b, B:435:0x0b67, B:436:0x0b77, B:437:0x0b94, B:439:0x0ba5, B:440:0x0ba9, B:443:0x0bb1, B:445:0x0bb7, B:446:0x0CLASSNAME, B:448:0x0c1f), top: B:553:0x0638 }] */
    /* JADX WARN: Removed duplicated region for block: B:452:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:456:0x0c3c  */
    /* JADX WARN: Removed duplicated region for block: B:473:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:475:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x0c6e  */
    /* JADX WARN: Removed duplicated region for block: B:485:0x0c7c  */
    /* JADX WARN: Removed duplicated region for block: B:487:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:489:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:605:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x024b  */
    /* JADX WARN: Type inference failed for: r13v37, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r13v40 */
    /* JADX WARN: Type inference failed for: r13v56 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:83:0x021b -> B:29:0x00fb). Please submit an issue!!! */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$183(long r38, java.util.ArrayList<java.lang.Integer> r40, boolean r41, boolean r42) {
        /*
            Method dump skipped, instructions count: 3212
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$183(long, java.util.ArrayList, boolean, boolean):java.util.ArrayList");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessagesAsDeletedInternal$181(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:193:0x040b  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x0410  */
    /* JADX WARN: Removed duplicated region for block: B:197:0x0415  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x041e  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x0423  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x0428  */
    /* JADX WARN: Removed duplicated region for block: B:257:? A[RETURN, SYNTHETIC] */
    /* renamed from: updateDialogsWithDeletedMessagesInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$updateDialogsWithDeletedMessages$182(long r24, long r26, java.util.ArrayList<java.lang.Integer> r28, java.util.ArrayList<java.lang.Long> r29) {
        /*
            Method dump skipped, instructions count: 1070
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateDialogsWithDeletedMessages$182(long, long, java.util.ArrayList, java.util.ArrayList):void");
    }

    public void updateDialogsWithDeletedMessages(final long j, final long j2, final ArrayList<Integer> arrayList, final ArrayList<Long> arrayList2, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda92
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$updateDialogsWithDeletedMessages$182(j, j2, arrayList, arrayList2);
                }
            });
        } else {
            lambda$updateDialogsWithDeletedMessages$182(j, j2, arrayList, arrayList2);
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final ArrayList<Integer> arrayList, boolean z, final boolean z2, final boolean z3) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda105
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsDeleted$183(j, arrayList, z2, z3);
                }
            });
            return null;
        }
        return lambda$markMessagesAsDeleted$183(j, arrayList, z2, z3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0331  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0336  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x0341  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x0346  */
    /* JADX WARN: Removed duplicated region for block: B:156:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00cb A[Catch: all -> 0x0105, Exception -> 0x0110, TRY_LEAVE, TryCatch #1 {Exception -> 0x0110, blocks: (B:5:0x004b, B:7:0x0051, B:11:0x005a, B:13:0x0064, B:15:0x006c, B:18:0x0083, B:27:0x00b7, B:31:0x00c4, B:33:0x00cb, B:22:0x0099), top: B:127:0x004b }] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00f4  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0136 A[Catch: all -> 0x0312, Exception -> 0x0314, TRY_LEAVE, TryCatch #16 {Exception -> 0x0314, all -> 0x0312, blocks: (B:52:0x011c, B:53:0x0130, B:55:0x0136, B:61:0x016e, B:73:0x01c4, B:80:0x0226, B:89:0x026b), top: B:140:0x011c }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x021b A[Catch: all -> 0x030d, Exception -> 0x0310, TryCatch #19 {Exception -> 0x0310, all -> 0x030d, blocks: (B:74:0x0215, B:76:0x021b, B:78:0x0221, B:81:0x023d, B:83:0x0243, B:88:0x0268), top: B:134:0x0215 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0220  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0226 A[Catch: all -> 0x0312, Exception -> 0x0314, TRY_ENTER, TRY_LEAVE, TryCatch #16 {Exception -> 0x0314, all -> 0x0312, blocks: (B:52:0x011c, B:53:0x0130, B:55:0x0136, B:61:0x016e, B:73:0x01c4, B:80:0x0226, B:89:0x026b), top: B:140:0x011c }] */
    /* JADX WARN: Type inference failed for: r7v10 */
    /* JADX WARN: Type inference failed for: r7v4 */
    /* renamed from: markMessagesAsDeletedInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$185(long r24, int r26, boolean r27) {
        /*
            Method dump skipped, instructions count: 844
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessagesAsDeleted$185(long, int, boolean):java.util.ArrayList");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markMessagesAsDeletedInternal$184(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final int i, boolean z, final boolean z2) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda84
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessagesAsDeleted$185(j, i, z2);
                }
            });
            return null;
        }
        return lambda$markMessagesAsDeleted$185(j, i, z2);
    }

    private void fixUnsupportedMedia(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return;
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaUnsupported_old) {
            if (tLRPC$MessageMedia.bytes.length != 0) {
                return;
            }
            tLRPC$MessageMedia.bytes = Utilities.intToBytes(148);
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaUnsupported)) {
        } else {
            TLRPC$TL_messageMediaUnsupported_old tLRPC$TL_messageMediaUnsupported_old = new TLRPC$TL_messageMediaUnsupported_old();
            tLRPC$Message.media = tLRPC$TL_messageMediaUnsupported_old;
            tLRPC$TL_messageMediaUnsupported_old.bytes = Utilities.intToBytes(148);
            tLRPC$Message.flags |= 512;
        }
    }

    private void doneHolesInTable(String str, long j, int i, int i2) throws Exception {
        SQLitePreparedStatement executeFast;
        int i3 = 2;
        if (i2 != 0) {
            if (i == 0) {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
            } else {
                SQLiteDatabase sQLiteDatabase2 = this.database;
                Locale locale2 = Locale.US;
                sQLiteDatabase2.executeFast(String.format(locale2, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d AND start = 0", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
            }
        } else if (i == 0) {
            SQLiteDatabase sQLiteDatabase3 = this.database;
            Locale locale3 = Locale.US;
            sQLiteDatabase3.executeFast(String.format(locale3, "DELETE FROM " + str + " WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
        } else {
            SQLiteDatabase sQLiteDatabase4 = this.database;
            Locale locale4 = Locale.US;
            sQLiteDatabase4.executeFast(String.format(locale4, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", Long.valueOf(j))).stepThis().dispose();
        }
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (i2 != 0) {
                    SQLiteDatabase sQLiteDatabase5 = this.database;
                    executeFast = sQLiteDatabase5.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?, ?)");
                } else {
                    SQLiteDatabase sQLiteDatabase6 = this.database;
                    executeFast = sQLiteDatabase6.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
                }
                sQLitePreparedStatement = executeFast;
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                if (i2 != 0) {
                    sQLitePreparedStatement.bindInteger(2, i2);
                    i3 = 3;
                }
                sQLitePreparedStatement.bindInteger(i3, 1);
                sQLitePreparedStatement.bindInteger(i3 + 1, 1);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                throw e;
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void doneHolesInMedia(long j, int i, int i2, int i3) throws Exception {
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement executeFast2;
        int i4;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        int i5 = 3;
        if (i2 == -1) {
            if (i3 != 0) {
                if (i == 0) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_topics WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(i3))).stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_topics WHERE uid = %d AND topic_id = %d AND start = 0", Long.valueOf(j), Integer.valueOf(i3))).stepThis().dispose();
                }
            } else if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", Long.valueOf(j))).stepThis().dispose();
            }
            try {
                try {
                    if (i3 != 0) {
                        executeFast2 = this.database.executeFast("REPLACE INTO media_holes_topics VALUES(?, ?, ?, ?, ?)");
                    } else {
                        executeFast2 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    }
                    sQLitePreparedStatement = executeFast2;
                    for (int i6 = 0; i6 < 8; i6++) {
                        sQLitePreparedStatement.requery();
                        sQLitePreparedStatement.bindLong(1, j);
                        if (i3 != 0) {
                            sQLitePreparedStatement.bindInteger(2, i3);
                            i4 = 3;
                        } else {
                            i4 = 2;
                        }
                        int i7 = i4 + 1;
                        sQLitePreparedStatement.bindInteger(i4, i6);
                        sQLitePreparedStatement.bindInteger(i7, 1);
                        sQLitePreparedStatement.bindInteger(i7 + 1, 1);
                        sQLitePreparedStatement.step();
                    }
                    if (sQLitePreparedStatement == null) {
                        return;
                    }
                    sQLitePreparedStatement.dispose();
                    return;
                } catch (Exception e) {
                    throw e;
                }
            } finally {
            }
        }
        if (i3 != 0) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_topics WHERE uid = %d AND topic_id = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2))).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_topics WHERE uid = %d AND topic_id = %d AND type = %d AND start = 0", Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2))).stepThis().dispose();
            }
        } else if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", Long.valueOf(j), Integer.valueOf(i2))).stepThis().dispose();
        }
        try {
            try {
                if (i3 != 0) {
                    executeFast = this.database.executeFast("REPLACE INTO media_holes_topics VALUES(?, ?, ?, ?, ?)");
                } else {
                    executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                }
                sQLitePreparedStatement = executeFast;
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                if (i3 != 0) {
                    sQLitePreparedStatement.bindInteger(2, i3);
                } else {
                    i5 = 2;
                }
                int i8 = i5 + 1;
                sQLitePreparedStatement.bindInteger(i5, i2);
                sQLitePreparedStatement.bindInteger(i8, 1);
                sQLitePreparedStatement.bindInteger(i8 + 1, 1);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
                sQLitePreparedStatement.dispose();
            } finally {
            }
        } catch (Exception e2) {
            throw e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Hole {
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

    /* JADX WARN: Removed duplicated region for block: B:114:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x048d  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0492  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x049a  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x049f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void closeHolesInMedia(long r25, int r27, int r28, int r29, int r30) {
        /*
            Method dump skipped, instructions count: 1189
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.closeHolesInMedia(long, int, int, int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:122:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x046f  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0474  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x047c  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0481  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void closeHolesInTable(java.lang.String r25, long r26, int r28, int r29, int r30) {
        /*
            Method dump skipped, instructions count: 1159
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.closeHolesInTable(java.lang.String, long, int, int, int):void");
    }

    public void replaceMessageIfExists(final TLRPC$Message tLRPC$Message, final ArrayList<TLRPC$User> arrayList, final ArrayList<TLRPC$Chat> arrayList2, final boolean z) {
        if (tLRPC$Message == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda184
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$replaceMessageIfExists$187(tLRPC$Message, z, arrayList, arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:118:0x01c7 A[Catch: all -> 0x0268, Exception -> 0x026c, TRY_LEAVE, TryCatch #18 {Exception -> 0x026c, all -> 0x0268, blocks: (B:116:0x01c1, B:118:0x01c7), top: B:197:0x01c1 }] */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01ec  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x01f0 A[Catch: all -> 0x0264, Exception -> 0x0266, TryCatch #19 {Exception -> 0x0266, all -> 0x0264, blocks: (B:120:0x01cf, B:123:0x01f0, B:124:0x01f3), top: B:195:0x01cf }] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0200 A[Catch: all -> 0x027c, Exception -> 0x0281, TryCatch #14 {Exception -> 0x0281, all -> 0x027c, blocks: (B:6:0x0031, B:13:0x0044, B:28:0x005c, B:125:0x01f9, B:127:0x0200, B:128:0x020b, B:130:0x0211, B:132:0x0226, B:134:0x022c, B:135:0x0240, B:26:0x0058, B:161:0x0289, B:162:0x028c), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0260  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0294  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x0299  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x029e  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x02a7  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x02ac  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x02b1  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x019f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:203:0x0071 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x01bf A[EDGE_INSN: B:205:0x01bf->B:115:0x01bf ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:207:0x01a9 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:212:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0058 A[Catch: all -> 0x027c, Exception -> 0x0281, TRY_ENTER, TryCatch #14 {Exception -> 0x0281, all -> 0x027c, blocks: (B:6:0x0031, B:13:0x0044, B:28:0x005c, B:125:0x01f9, B:127:0x0200, B:128:0x020b, B:130:0x0211, B:132:0x0226, B:134:0x022c, B:135:0x0240, B:26:0x0058, B:161:0x0289, B:162:0x028c), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0093  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0105 A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x010d A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x011c  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x012c  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x012e  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x013d A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0153 A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x015d A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x016a A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x017a A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0180 A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0187 A[Catch: Exception -> 0x01b2, all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:54:0x00c1, B:56:0x00c8, B:58:0x00cd, B:60:0x00ed, B:65:0x00f5, B:67:0x0105, B:69:0x0116, B:75:0x0121, B:79:0x012f, B:81:0x013d, B:83:0x0159, B:85:0x015d, B:89:0x0166, B:91:0x016f, B:93:0x017a, B:96:0x0187, B:98:0x018e, B:99:0x0194, B:100:0x0197, B:163:0x028d, B:94:0x0180, B:88:0x0164, B:90:0x016a, B:82:0x0153, B:68:0x010d), top: B:183:0x0007 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$replaceMessageIfExists$187(org.telegram.tgnet.TLRPC$Message r18, boolean r19, java.util.ArrayList r20, java.util.ArrayList r21) {
        /*
            Method dump skipped, instructions count: 695
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$187(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$replaceMessageIfExists$186(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void putMessages(final TLRPC$messages_Messages tLRPC$messages_Messages, final long j, final int i, final int i2, final boolean z, final boolean z2, final int i3) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda199
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putMessages$189(z2, j, tLRPC$messages_Messages, i3, i, i2, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(11:(3:120|121|122)|(19:(24:(2:478|479)(1:481)|131|(2:133|134)|135|(11:367|368|369|370|371|373|374|(6:376|(12:378|379|(4:445|446|(1:448)|449)|381|(2:383|(2:389|390))|433|434|(2:436|(2:438|(2:440|(2:442|443))))|432|(3:394|395|396)(1:431)|397|(1:399))(1:455)|400|(1:402)(1:425)|403|(5:405|(3:407|(1:409)|410)(1:422)|(2:412|(1:414))(2:419|(1:421))|415|(3:417|418|300))(1:423))(1:456)|424|415|(0))(1:137)|(9:140|141|142|143|(1:145)(1:178)|146|147|(5:149|150|151|(1:153)(1:156)|154)(7:164|165|166|167|(1:169)(1:172)|170|171)|155)|195|196|197|(5:199|(1:201)(1:269)|(1:268)(1:204)|(22:(1:211)(1:267)|212|(1:214)(1:266)|215|(17:220|221|(1:223)(1:264)|224|(1:226)(2:260|(1:262)(1:263))|227|(1:229)(1:259)|230|(1:232)(1:258)|233|(3:235|(1:237)(1:256)|238)(1:257)|239|(1:241)(1:255)|(2:243|(1:245)(1:253))(1:254)|246|(1:248)|(2:250|251)(1:252))|265|221|(0)(0)|224|(0)(0)|227|(0)(0)|230|(0)(0)|233|(0)(0)|239|(0)(0)|(0)(0)|246|(0)|(0)(0))(2:207|208)|209)|270|271|(2:273|(4:275|276|277|278)(2:349|(2:353|354)))(2:359|(10:361|280|(1:343)(4:(1:285)(1:342)|286|287|288)|289|(4:(1:292)(1:322)|293|294|295)(3:323|(4:(1:326)(1:335)|327|328|329)|336)|(3:301|302|(5:304|(3:308|309|300)|298|299|300))|297|298|299|300))|279|280|(1:282)|343|289|(0)(0)|(0)|297|298|299|300)(1:125)|195|196|197|(0)|270|271|(0)(0)|279|280|(0)|343|289|(0)(0)|(0)|297|298|299|300)|126|127|(1:129)(1:471)|130|131|(0)|135|(0)(0)|(9:140|141|142|143|(0)(0)|146|147|(0)(0)|155)) */
    /* JADX WARN: Can't wrap try/catch for region: R(17:105|(2:106|107)|(18:(1:109)(2:578|(1:580)(33:581|(2:583|(0))(1:591)|(1:590)(1:588)|589|111|112|113|114|115|116|117|118|(31:120|121|122|(24:(2:478|479)(1:481)|131|(2:133|134)|135|(11:367|368|369|370|371|373|374|(6:376|(12:378|379|(4:445|446|(1:448)|449)|381|(2:383|(2:389|390))|433|434|(2:436|(2:438|(2:440|(2:442|443))))|432|(3:394|395|396)(1:431)|397|(1:399))(1:455)|400|(1:402)(1:425)|403|(5:405|(3:407|(1:409)|410)(1:422)|(2:412|(1:414))(2:419|(1:421))|415|(3:417|418|300))(1:423))(1:456)|424|415|(0))(1:137)|(9:140|141|142|143|(1:145)(1:178)|146|147|(5:149|150|151|(1:153)(1:156)|154)(7:164|165|166|167|(1:169)(1:172)|170|171)|155)|195|196|197|(5:199|(1:201)(1:269)|(1:268)(1:204)|(22:(1:211)(1:267)|212|(1:214)(1:266)|215|(17:220|221|(1:223)(1:264)|224|(1:226)(2:260|(1:262)(1:263))|227|(1:229)(1:259)|230|(1:232)(1:258)|233|(3:235|(1:237)(1:256)|238)(1:257)|239|(1:241)(1:255)|(2:243|(1:245)(1:253))(1:254)|246|(1:248)|(2:250|251)(1:252))|265|221|(0)(0)|224|(0)(0)|227|(0)(0)|230|(0)(0)|233|(0)(0)|239|(0)(0)|(0)(0)|246|(0)|(0)(0))(2:207|208)|209)|270|271|(2:273|(4:275|276|277|278)(2:349|(2:353|354)))(2:359|(10:361|280|(1:343)(4:(1:285)(1:342)|286|287|288)|289|(4:(1:292)(1:322)|293|294|295)(3:323|(4:(1:326)(1:335)|327|328|329)|336)|(3:301|302|(5:304|(3:308|309|300)|298|299|300))|297|298|299|300))|279|280|(1:282)|343|289|(0)(0)|(0)|297|298|299|300)(1:125)|126|127|(1:129)(1:471)|130|131|(0)|135|(0)(0)|(9:140|141|142|143|(0)(0)|146|147|(0)(0)|155)|195|196|197|(0)|270|271|(0)(0)|279|280|(0)|343|289|(0)(0)|(0)|297|298|299|300)|487|488|489|490|492|493|494|495|(2:542|543)|(1:498)|(1:500)|(1:502)|510|511|512|(4:514|515|516|517)(1:537)|(4:519|(1:521)(1:530)|522|523)(1:531)|524|(1:528)|527))|492|493|494|495|(0)|(0)|(0)|(0)|510|511|512|(0)(0)|(0)(0)|524|(0)|528|527)|110|111|112|113|114|115|116|117|118|(0)|487|488|489|490) */
    /* JADX WARN: Can't wrap try/catch for region: R(18:105|106|107|(18:(1:109)(2:578|(1:580)(33:581|(2:583|(0))(1:591)|(1:590)(1:588)|589|111|112|113|114|115|116|117|118|(31:120|121|122|(24:(2:478|479)(1:481)|131|(2:133|134)|135|(11:367|368|369|370|371|373|374|(6:376|(12:378|379|(4:445|446|(1:448)|449)|381|(2:383|(2:389|390))|433|434|(2:436|(2:438|(2:440|(2:442|443))))|432|(3:394|395|396)(1:431)|397|(1:399))(1:455)|400|(1:402)(1:425)|403|(5:405|(3:407|(1:409)|410)(1:422)|(2:412|(1:414))(2:419|(1:421))|415|(3:417|418|300))(1:423))(1:456)|424|415|(0))(1:137)|(9:140|141|142|143|(1:145)(1:178)|146|147|(5:149|150|151|(1:153)(1:156)|154)(7:164|165|166|167|(1:169)(1:172)|170|171)|155)|195|196|197|(5:199|(1:201)(1:269)|(1:268)(1:204)|(22:(1:211)(1:267)|212|(1:214)(1:266)|215|(17:220|221|(1:223)(1:264)|224|(1:226)(2:260|(1:262)(1:263))|227|(1:229)(1:259)|230|(1:232)(1:258)|233|(3:235|(1:237)(1:256)|238)(1:257)|239|(1:241)(1:255)|(2:243|(1:245)(1:253))(1:254)|246|(1:248)|(2:250|251)(1:252))|265|221|(0)(0)|224|(0)(0)|227|(0)(0)|230|(0)(0)|233|(0)(0)|239|(0)(0)|(0)(0)|246|(0)|(0)(0))(2:207|208)|209)|270|271|(2:273|(4:275|276|277|278)(2:349|(2:353|354)))(2:359|(10:361|280|(1:343)(4:(1:285)(1:342)|286|287|288)|289|(4:(1:292)(1:322)|293|294|295)(3:323|(4:(1:326)(1:335)|327|328|329)|336)|(3:301|302|(5:304|(3:308|309|300)|298|299|300))|297|298|299|300))|279|280|(1:282)|343|289|(0)(0)|(0)|297|298|299|300)(1:125)|126|127|(1:129)(1:471)|130|131|(0)|135|(0)(0)|(9:140|141|142|143|(0)(0)|146|147|(0)(0)|155)|195|196|197|(0)|270|271|(0)(0)|279|280|(0)|343|289|(0)(0)|(0)|297|298|299|300)|487|488|489|490|492|493|494|495|(2:542|543)|(1:498)|(1:500)|(1:502)|510|511|512|(4:514|515|516|517)(1:537)|(4:519|(1:521)(1:530)|522|523)(1:531)|524|(1:528)|527))|492|493|494|495|(0)|(0)|(0)|(0)|510|511|512|(0)(0)|(0)(0)|524|(0)|528|527)|110|111|112|113|114|115|116|117|118|(0)|487|488|489|490) */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x0334, code lost:
        if (r4.id == r2.id) goto L392;
     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x0336, code lost:
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x0352, code lost:
        if (r1.id == r2.id) goto L392;
     */
    /* JADX WARN: Code restructure failed: missing block: B:475:0x0a2c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:476:0x0a2d, code lost:
        r12 = null;
        r1 = r0;
        r32 = r4;
        r16 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x0a34, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:478:0x0a35, code lost:
        r12 = null;
        r1 = r0;
        r32 = r4;
        r16 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:479:0x0a3d, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:480:0x0a3e, code lost:
        r12 = null;
        r1 = r0;
        r32 = r3;
        r16 = null;
        r23 = null;
        r25 = null;
        r27 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:481:0x0a4c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:482:0x0a4d, code lost:
        r12 = null;
        r1 = r0;
        r32 = r3;
        r16 = null;
        r23 = null;
        r25 = null;
        r27 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:483:0x0a5b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:484:0x0a5c, code lost:
        r12 = null;
        r1 = r0;
        r16 = null;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:485:0x0a63, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:486:0x0a64, code lost:
        r12 = null;
        r1 = r0;
        r16 = null;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:487:0x0a6b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:488:0x0a6c, code lost:
        r12 = null;
        r1 = r0;
        r16 = null;
        r19 = null;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:489:0x0a74, code lost:
        r25 = r23;
        r27 = r25;
        r32 = r27;
        r12 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:491:0x0a7e, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:492:0x0a7f, code lost:
        r12 = null;
        r1 = r0;
        r16 = null;
        r19 = null;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:493:0x0a87, code lost:
        r25 = r23;
        r27 = r25;
        r32 = r27;
        r12 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x01a1, code lost:
        if (r13 != 4) goto L110;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0256, code lost:
        if (r24.intValue() < r15.id) goto L126;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0273, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0274, code lost:
        r1 = r0;
        r32 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0279, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x027a, code lost:
        r1 = r0;
        r32 = r3;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0412  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x0445  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x0479 A[Catch: all -> 0x056d, Exception -> 0x0575, TryCatch #63 {Exception -> 0x0575, all -> 0x056d, blocks: (B:202:0x0473, B:204:0x0479, B:206:0x048d), top: B:560:0x0473 }] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x048a  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x0495 A[Catch: all -> 0x057d, Exception -> 0x0580, TRY_ENTER, TRY_LEAVE, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:220:0x04e2 A[Catch: all -> 0x057d, Exception -> 0x0580, TRY_ENTER, TRY_LEAVE, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x0596  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x0606 A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:277:0x060e A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:280:0x061b  */
    /* JADX WARN: Removed duplicated region for block: B:281:0x061d  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x062e  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0630  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x063f A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:292:0x0655 A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:295:0x065f A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:300:0x0670 A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:303:0x0685 A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:304:0x068b A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0692 A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:310:0x06a4  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x06aa A[Catch: all -> 0x057d, Exception -> 0x0580, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:315:0x06af A[Catch: all -> 0x057d, Exception -> 0x0580, TRY_LEAVE, TryCatch #44 {Exception -> 0x0580, all -> 0x057d, blocks: (B:201:0x0459, B:209:0x0495, B:220:0x04e2, B:255:0x05a0, B:263:0x05b4, B:265:0x05c3, B:267:0x05ca, B:269:0x05ee, B:274:0x05f6, B:276:0x0606, B:278:0x0617, B:285:0x0623, B:289:0x0631, B:291:0x063f, B:293:0x065b, B:295:0x065f, B:299:0x066a, B:301:0x0676, B:303:0x0685, B:306:0x0692, B:308:0x0698, B:311:0x06a5, B:313:0x06aa, B:315:0x06af, B:309:0x069f, B:304:0x068b, B:298:0x0668, B:300:0x0670, B:292:0x0655, B:277:0x060e), top: B:597:0x0459 }] */
    /* JADX WARN: Removed duplicated region for block: B:319:0x06bd A[Catch: all -> 0x0895, Exception -> 0x089a, TRY_ENTER, TryCatch #39 {Exception -> 0x089a, all -> 0x0895, blocks: (B:246:0x0583, B:319:0x06bd, B:321:0x06c3), top: B:607:0x0583 }] */
    /* JADX WARN: Removed duplicated region for block: B:333:0x0742 A[Catch: all -> 0x0891, Exception -> 0x0893, TryCatch #18 {Exception -> 0x0893, blocks: (B:323:0x06cb, B:337:0x0778, B:339:0x077f, B:342:0x0785, B:352:0x07d7, B:355:0x07df, B:364:0x081a, B:367:0x0820, B:324:0x06e9, B:326:0x06ef, B:332:0x073e, B:333:0x0742, B:335:0x074a), top: B:552:0x06cb }] */
    /* JADX WARN: Removed duplicated region for block: B:339:0x077f A[Catch: all -> 0x0891, Exception -> 0x0893, TryCatch #18 {Exception -> 0x0893, blocks: (B:323:0x06cb, B:337:0x0778, B:339:0x077f, B:342:0x0785, B:352:0x07d7, B:355:0x07df, B:364:0x081a, B:367:0x0820, B:324:0x06e9, B:326:0x06ef, B:332:0x073e, B:333:0x0742, B:335:0x074a), top: B:552:0x06cb }] */
    /* JADX WARN: Removed duplicated region for block: B:354:0x07dd  */
    /* JADX WARN: Removed duplicated region for block: B:364:0x081a A[Catch: all -> 0x0891, Exception -> 0x0893, TRY_ENTER, TryCatch #18 {Exception -> 0x0893, blocks: (B:323:0x06cb, B:337:0x0778, B:339:0x077f, B:342:0x0785, B:352:0x07d7, B:355:0x07df, B:364:0x081a, B:367:0x0820, B:324:0x06e9, B:326:0x06ef, B:332:0x073e, B:333:0x0742, B:335:0x074a), top: B:552:0x06cb }] */
    /* JADX WARN: Removed duplicated region for block: B:423:0x08e9 A[Catch: all -> 0x08d3, Exception -> 0x08dd, TryCatch #58 {Exception -> 0x08dd, all -> 0x08d3, blocks: (B:417:0x08cd, B:423:0x08e9, B:425:0x08fa, B:427:0x0901), top: B:570:0x08cd }] */
    /* JADX WARN: Removed duplicated region for block: B:425:0x08fa A[Catch: all -> 0x08d3, Exception -> 0x08dd, TryCatch #58 {Exception -> 0x08dd, all -> 0x08d3, blocks: (B:417:0x08cd, B:423:0x08e9, B:425:0x08fa, B:427:0x0901), top: B:570:0x08cd }] */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0901 A[Catch: all -> 0x08d3, Exception -> 0x08dd, TRY_LEAVE, TryCatch #58 {Exception -> 0x08dd, all -> 0x08d3, blocks: (B:417:0x08cd, B:423:0x08e9, B:425:0x08fa, B:427:0x0901), top: B:570:0x08cd }] */
    /* JADX WARN: Removed duplicated region for block: B:431:0x0932 A[Catch: all -> 0x09e6, Exception -> 0x09f2, TRY_LEAVE, TryCatch #60 {Exception -> 0x09f2, all -> 0x09e6, blocks: (B:429:0x090b, B:431:0x0932), top: B:566:0x090b }] */
    /* JADX WARN: Removed duplicated region for block: B:434:0x096a  */
    /* JADX WARN: Removed duplicated region for block: B:436:0x096d A[Catch: all -> 0x09e2, Exception -> 0x09e4, TryCatch #55 {Exception -> 0x09e4, all -> 0x09e2, blocks: (B:433:0x0966, B:436:0x096d, B:441:0x0990, B:443:0x09af, B:446:0x09b8, B:440:0x097e), top: B:576:0x0966 }] */
    /* JADX WARN: Removed duplicated region for block: B:442:0x09ad  */
    /* JADX WARN: Removed duplicated region for block: B:510:0x0abe  */
    /* JADX WARN: Removed duplicated region for block: B:512:0x0ac3  */
    /* JADX WARN: Removed duplicated region for block: B:514:0x0ac8  */
    /* JADX WARN: Removed duplicated region for block: B:516:0x0acd  */
    /* JADX WARN: Removed duplicated region for block: B:518:0x0ad2  */
    /* JADX WARN: Removed duplicated region for block: B:520:0x0ad7  */
    /* JADX WARN: Removed duplicated region for block: B:522:0x0adc  */
    /* JADX WARN: Removed duplicated region for block: B:524:0x0ae1  */
    /* JADX WARN: Removed duplicated region for block: B:526:0x0ae6  */
    /* JADX WARN: Removed duplicated region for block: B:532:0x0af0  */
    /* JADX WARN: Removed duplicated region for block: B:534:0x0af5  */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0afa  */
    /* JADX WARN: Removed duplicated region for block: B:538:0x0aff  */
    /* JADX WARN: Removed duplicated region for block: B:540:0x0b04  */
    /* JADX WARN: Removed duplicated region for block: B:542:0x0b09  */
    /* JADX WARN: Removed duplicated region for block: B:544:0x0b0e  */
    /* JADX WARN: Removed duplicated region for block: B:546:0x0b13  */
    /* JADX WARN: Removed duplicated region for block: B:548:0x0b18  */
    /* JADX WARN: Removed duplicated region for block: B:564:0x085a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:570:0x08cd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:574:0x0292 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:603:0x0236 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:627:0x06b2 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:631:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0289 A[Catch: all -> 0x0273, Exception -> 0x0279, TRY_LEAVE, TryCatch #42 {Exception -> 0x0279, all -> 0x0273, blocks: (B:83:0x0258, B:85:0x0264, B:95:0x0289, B:81:0x024e), top: B:601:0x0258 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$putMessages$189(boolean r43, long r44, org.telegram.tgnet.TLRPC$messages_Messages r46, int r47, int r48, int r49, boolean r50) {
        /*
            Method dump skipped, instructions count: 2846
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$189(boolean, long, org.telegram.tgnet.TLRPC$messages_Messages, int, int, int, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putMessages$188(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public static void addUsersAndChatsFromMessage(TLRPC$Message tLRPC$Message, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, ArrayList<Long> arrayList3) {
        String str;
        TLRPC$Peer tLRPC$Peer;
        long fromChatId = MessageObject.getFromChatId(tLRPC$Message);
        if (DialogObject.isUserDialog(fromChatId)) {
            if (!arrayList.contains(Long.valueOf(fromChatId))) {
                arrayList.add(Long.valueOf(fromChatId));
            }
        } else if (DialogObject.isChatDialog(fromChatId)) {
            long j = -fromChatId;
            if (!arrayList2.contains(Long.valueOf(j))) {
                arrayList2.add(Long.valueOf(j));
            }
        }
        long j2 = tLRPC$Message.via_bot_id;
        if (j2 != 0 && !arrayList.contains(Long.valueOf(j2))) {
            arrayList.add(Long.valueOf(tLRPC$Message.via_bot_id));
        }
        TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
        if (tLRPC$MessageAction != null) {
            long j3 = tLRPC$MessageAction.user_id;
            if (j3 != 0 && !arrayList.contains(Long.valueOf(j3))) {
                arrayList.add(Long.valueOf(tLRPC$Message.action.user_id));
            }
            long j4 = tLRPC$Message.action.channel_id;
            if (j4 != 0 && !arrayList2.contains(Long.valueOf(j4))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.action.channel_id));
            }
            long j5 = tLRPC$Message.action.chat_id;
            if (j5 != 0 && !arrayList2.contains(Long.valueOf(j5))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.action.chat_id));
            }
            TLRPC$MessageAction tLRPC$MessageAction2 = tLRPC$Message.action;
            if (tLRPC$MessageAction2 instanceof TLRPC$TL_messageActionGeoProximityReached) {
                TLRPC$TL_messageActionGeoProximityReached tLRPC$TL_messageActionGeoProximityReached = (TLRPC$TL_messageActionGeoProximityReached) tLRPC$MessageAction2;
                long peerId = MessageObject.getPeerId(tLRPC$TL_messageActionGeoProximityReached.from_id);
                if (DialogObject.isUserDialog(peerId)) {
                    if (!arrayList.contains(Long.valueOf(peerId))) {
                        arrayList.add(Long.valueOf(peerId));
                    }
                } else {
                    long j6 = -peerId;
                    if (!arrayList2.contains(Long.valueOf(j6))) {
                        arrayList2.add(Long.valueOf(j6));
                    }
                }
                long peerId2 = MessageObject.getPeerId(tLRPC$TL_messageActionGeoProximityReached.to_id);
                if (peerId2 > 0) {
                    if (!arrayList.contains(Long.valueOf(peerId2))) {
                        arrayList.add(Long.valueOf(peerId2));
                    }
                } else {
                    long j7 = -peerId2;
                    if (!arrayList2.contains(Long.valueOf(j7))) {
                        arrayList2.add(Long.valueOf(j7));
                    }
                }
            }
            if (!tLRPC$Message.action.users.isEmpty()) {
                for (int i = 0; i < tLRPC$Message.action.users.size(); i++) {
                    Long l = tLRPC$Message.action.users.get(i);
                    if (!arrayList.contains(l)) {
                        arrayList.add(l);
                    }
                }
            }
        }
        if (!tLRPC$Message.entities.isEmpty()) {
            for (int i2 = 0; i2 < tLRPC$Message.entities.size(); i2++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$Message.entities.get(i2);
                if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                    arrayList.add(Long.valueOf(((TLRPC$TL_messageEntityMentionName) tLRPC$MessageEntity).user_id));
                } else if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                    arrayList.add(Long.valueOf(((TLRPC$TL_inputMessageEntityMentionName) tLRPC$MessageEntity).user_id.user_id));
                } else if (arrayList3 != null && (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji)) {
                    arrayList3.add(Long.valueOf(((TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity).document_id));
                }
            }
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia != null) {
            long j8 = tLRPC$MessageMedia.user_id;
            if (j8 != 0 && !arrayList.contains(Long.valueOf(j8))) {
                arrayList.add(Long.valueOf(tLRPC$Message.media.user_id));
            }
            TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
            if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaPoll) {
                TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll = (TLRPC$TL_messageMediaPoll) tLRPC$MessageMedia2;
                if (!tLRPC$TL_messageMediaPoll.results.recent_voters.isEmpty()) {
                    arrayList.addAll(tLRPC$TL_messageMediaPoll.results.recent_voters);
                }
            }
        }
        TLRPC$MessageReplies tLRPC$MessageReplies = tLRPC$Message.replies;
        if (tLRPC$MessageReplies != null) {
            int size = tLRPC$MessageReplies.recent_repliers.size();
            for (int i3 = 0; i3 < size; i3++) {
                long peerId3 = MessageObject.getPeerId(tLRPC$Message.replies.recent_repliers.get(i3));
                if (DialogObject.isUserDialog(peerId3)) {
                    if (!arrayList.contains(Long.valueOf(peerId3))) {
                        arrayList.add(Long.valueOf(peerId3));
                    }
                } else if (DialogObject.isChatDialog(peerId3)) {
                    long j9 = -peerId3;
                    if (!arrayList2.contains(Long.valueOf(j9))) {
                        arrayList2.add(Long.valueOf(j9));
                    }
                }
            }
        }
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = tLRPC$Message.reply_to;
        if (tLRPC$TL_messageReplyHeader != null && (tLRPC$Peer = tLRPC$TL_messageReplyHeader.reply_to_peer_id) != null) {
            long peerId4 = MessageObject.getPeerId(tLRPC$Peer);
            if (DialogObject.isUserDialog(peerId4)) {
                if (!arrayList.contains(Long.valueOf(peerId4))) {
                    arrayList.add(Long.valueOf(peerId4));
                }
            } else if (DialogObject.isChatDialog(peerId4)) {
                long j10 = -peerId4;
                if (!arrayList2.contains(Long.valueOf(j10))) {
                    arrayList2.add(Long.valueOf(j10));
                }
            }
        }
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from;
        if (tLRPC$MessageFwdHeader != null) {
            TLRPC$Peer tLRPC$Peer2 = tLRPC$MessageFwdHeader.from_id;
            if (tLRPC$Peer2 instanceof TLRPC$TL_peerUser) {
                if (!arrayList.contains(Long.valueOf(tLRPC$Peer2.user_id))) {
                    arrayList.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.user_id));
                }
            } else if (tLRPC$Peer2 instanceof TLRPC$TL_peerChannel) {
                if (!arrayList2.contains(Long.valueOf(tLRPC$Peer2.channel_id))) {
                    arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.channel_id));
                }
            } else if ((tLRPC$Peer2 instanceof TLRPC$TL_peerChat) && !arrayList2.contains(Long.valueOf(tLRPC$Peer2.chat_id))) {
                arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.from_id.chat_id));
            }
            TLRPC$Peer tLRPC$Peer3 = tLRPC$Message.fwd_from.saved_from_peer;
            if (tLRPC$Peer3 != null) {
                long j11 = tLRPC$Peer3.user_id;
                if (j11 != 0) {
                    if (!arrayList2.contains(Long.valueOf(j11))) {
                        arrayList.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.user_id));
                    }
                } else {
                    long j12 = tLRPC$Peer3.channel_id;
                    if (j12 != 0) {
                        if (!arrayList2.contains(Long.valueOf(j12))) {
                            arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.channel_id));
                        }
                    } else {
                        long j13 = tLRPC$Peer3.chat_id;
                        if (j13 != 0 && !arrayList2.contains(Long.valueOf(j13))) {
                            arrayList2.add(Long.valueOf(tLRPC$Message.fwd_from.saved_from_peer.chat_id));
                        }
                    }
                }
            }
        }
        HashMap<String, String> hashMap = tLRPC$Message.params;
        if (hashMap == null || (str = hashMap.get("fwd_peer")) == null) {
            return;
        }
        long longValue = Utilities.parseLong(str).longValue();
        if (longValue >= 0) {
            return;
        }
        long j14 = -longValue;
        if (arrayList2.contains(Long.valueOf(j14))) {
            return;
        }
        arrayList2.add(Long.valueOf(j14));
    }

    public void getDialogs(final int i, final int i2, final int i3, boolean z) {
        LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts;
        int size;
        long[] jArr = null;
        if (z && (size = (drafts = getMediaDataController().getDrafts()).size()) > 0) {
            jArr = new long[size];
            for (int i4 = 0; i4 < size; i4++) {
                if (drafts.valueAt(i4).get(0) != null) {
                    jArr[i4] = drafts.keyAt(i4);
                }
            }
        }
        final long[] jArr2 = jArr;
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogs$191(i, i2, i3, jArr2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(11:221|(3:257|258|(1:260))|223|224|(3:243|244|(2:246|(8:248|249|250|227|228|(3:230|231|232)(1:239)|233|234)))|226|227|228|(0)(0)|233|234) */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x041f, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x046d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:210:0x046e, code lost:
        r3 = r26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0224, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L155;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:109:0x0296  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x02b4 A[Catch: Exception -> 0x04d0, all -> 0x04d9, TryCatch #26 {Exception -> 0x04d0, blocks: (B:112:0x029f, B:114:0x02a5, B:116:0x02ab, B:117:0x02ae, B:119:0x02b4, B:121:0x02c4, B:122:0x02cc, B:124:0x02d4, B:126:0x02de, B:127:0x02e6, B:129:0x02ec, B:131:0x02f7, B:107:0x0285, B:108:0x0289, B:133:0x0311), top: B:391:0x02a5 }] */
    /* JADX WARN: Removed duplicated region for block: B:122:0x02cc A[Catch: Exception -> 0x04d0, all -> 0x04d9, TryCatch #26 {Exception -> 0x04d0, blocks: (B:112:0x029f, B:114:0x02a5, B:116:0x02ab, B:117:0x02ae, B:119:0x02b4, B:121:0x02c4, B:122:0x02cc, B:124:0x02d4, B:126:0x02de, B:127:0x02e6, B:129:0x02ec, B:131:0x02f7, B:107:0x0285, B:108:0x0289, B:133:0x0311), top: B:391:0x02a5 }] */
    /* JADX WARN: Removed duplicated region for block: B:203:0x0462  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x046a  */
    /* JADX WARN: Removed duplicated region for block: B:339:0x067d  */
    /* JADX WARN: Removed duplicated region for block: B:344:0x0685  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0146  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x02a5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x015d  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x015f  */
    /* JADX WARN: Removed duplicated region for block: B:443:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0178 A[Catch: all -> 0x04d9, Exception -> 0x04de, TryCatch #10 {Exception -> 0x04de, blocks: (B:14:0x00a6, B:16:0x00ac, B:18:0x00b9, B:20:0x00c6, B:22:0x00cc, B:23:0x00db, B:25:0x00ea, B:27:0x00fb, B:29:0x011c, B:35:0x012a, B:39:0x0149, B:43:0x0160, B:45:0x0178, B:47:0x0180, B:48:0x0185, B:50:0x019d, B:51:0x01b1, B:53:0x01b8, B:55:0x01c4, B:57:0x01cb, B:59:0x01d6, B:61:0x01fb, B:62:0x01fd, B:26:0x00f6), top: B:365:0x00a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x019d A[Catch: all -> 0x04d9, Exception -> 0x04de, TryCatch #10 {Exception -> 0x04de, blocks: (B:14:0x00a6, B:16:0x00ac, B:18:0x00b9, B:20:0x00c6, B:22:0x00cc, B:23:0x00db, B:25:0x00ea, B:27:0x00fb, B:29:0x011c, B:35:0x012a, B:39:0x0149, B:43:0x0160, B:45:0x0178, B:47:0x0180, B:48:0x0185, B:50:0x019d, B:51:0x01b1, B:53:0x01b8, B:55:0x01c4, B:57:0x01cb, B:59:0x01d6, B:61:0x01fb, B:62:0x01fd, B:26:0x00f6), top: B:365:0x00a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x01b8 A[Catch: all -> 0x04d9, Exception -> 0x04de, TryCatch #10 {Exception -> 0x04de, blocks: (B:14:0x00a6, B:16:0x00ac, B:18:0x00b9, B:20:0x00c6, B:22:0x00cc, B:23:0x00db, B:25:0x00ea, B:27:0x00fb, B:29:0x011c, B:35:0x012a, B:39:0x0149, B:43:0x0160, B:45:0x0178, B:47:0x0180, B:48:0x0185, B:50:0x019d, B:51:0x01b1, B:53:0x01b8, B:55:0x01c4, B:57:0x01cb, B:59:0x01d6, B:61:0x01fb, B:62:0x01fd, B:26:0x00f6), top: B:365:0x00a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x01c2  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x01cb A[Catch: all -> 0x04d9, Exception -> 0x04de, TryCatch #10 {Exception -> 0x04de, blocks: (B:14:0x00a6, B:16:0x00ac, B:18:0x00b9, B:20:0x00c6, B:22:0x00cc, B:23:0x00db, B:25:0x00ea, B:27:0x00fb, B:29:0x011c, B:35:0x012a, B:39:0x0149, B:43:0x0160, B:45:0x0178, B:47:0x0180, B:48:0x0185, B:50:0x019d, B:51:0x01b1, B:53:0x01b8, B:55:0x01c4, B:57:0x01cb, B:59:0x01d6, B:61:0x01fb, B:62:0x01fd, B:26:0x00f6), top: B:365:0x00a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0270  */
    /* JADX WARN: Type inference failed for: r5v31, types: [org.telegram.tgnet.TLRPC$TL_dialog] */
    /* JADX WARN: Type inference failed for: r5v32, types: [java.lang.Object, org.telegram.tgnet.TLRPC$Dialog] */
    /* JADX WARN: Type inference failed for: r5v33, types: [org.telegram.tgnet.TLRPC$TL_dialogFolder] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogs$191(int r33, int r34, int r35, long[] r36) {
        /*
            Method dump skipped, instructions count: 1675
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$191(int, int, int, long[]):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogs$190(LongSparseArray longSparseArray) {
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearDraftsFolderIds();
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                mediaDataController.setDraftFolderId(longSparseArray.keyAt(i), ((Integer) longSparseArray.valueAt(i)).intValue());
            }
        }
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) throws Exception {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (int i2 = 0; i2 < 8; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00f5, code lost:
        if (r4 != false) goto L406;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00f7, code lost:
        r5 = r14;
        r14 = r15;
        r19 = r17;
        r3 = r21;
        r4 = r22;
        r21 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01aa, code lost:
        if (r4 < 0) goto L406;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:119:0x026b A[Catch: all -> 0x0244, Exception -> 0x024f, TRY_ENTER, TRY_LEAVE, TryCatch #67 {Exception -> 0x024f, all -> 0x0244, blocks: (B:106:0x023d, B:119:0x026b, B:137:0x029d, B:143:0x02be, B:147:0x02c5, B:154:0x02e2, B:146:0x02c3), top: B:536:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:121:0x026e  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0278  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x027c  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x028a  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x028c  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x029d A[Catch: all -> 0x0244, Exception -> 0x024f, TRY_ENTER, TRY_LEAVE, TryCatch #67 {Exception -> 0x024f, all -> 0x0244, blocks: (B:106:0x023d, B:119:0x026b, B:137:0x029d, B:143:0x02be, B:147:0x02c5, B:154:0x02e2, B:146:0x02c3), top: B:536:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:139:0x02b4  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x02be A[Catch: all -> 0x0244, Exception -> 0x024f, TRY_ENTER, TryCatch #67 {Exception -> 0x024f, all -> 0x0244, blocks: (B:106:0x023d, B:119:0x026b, B:137:0x029d, B:143:0x02be, B:147:0x02c5, B:154:0x02e2, B:146:0x02c3), top: B:536:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:149:0x02cb  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x02e2 A[Catch: all -> 0x0244, Exception -> 0x024f, TRY_ENTER, TRY_LEAVE, TryCatch #67 {Exception -> 0x024f, all -> 0x0244, blocks: (B:106:0x023d, B:119:0x026b, B:137:0x029d, B:143:0x02be, B:147:0x02c5, B:154:0x02e2, B:146:0x02c3), top: B:536:0x023d }] */
    /* JADX WARN: Removed duplicated region for block: B:156:0x02ec  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x036f  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x037a A[Catch: all -> 0x037e, Exception -> 0x0380, TRY_LEAVE, TryCatch #60 {Exception -> 0x0380, all -> 0x037e, blocks: (B:166:0x030f, B:187:0x037a, B:195:0x0389, B:198:0x038f, B:199:0x0397), top: B:550:0x030f }] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x03ca  */
    /* JADX WARN: Removed duplicated region for block: B:219:0x041f  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x047d  */
    /* JADX WARN: Removed duplicated region for block: B:440:0x084f  */
    /* JADX WARN: Removed duplicated region for block: B:442:0x0854  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0859  */
    /* JADX WARN: Removed duplicated region for block: B:446:0x085e  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0863  */
    /* JADX WARN: Removed duplicated region for block: B:450:0x0868  */
    /* JADX WARN: Removed duplicated region for block: B:452:0x086d  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x0872  */
    /* JADX WARN: Removed duplicated region for block: B:456:0x0877  */
    /* JADX WARN: Removed duplicated region for block: B:458:0x087c  */
    /* JADX WARN: Removed duplicated region for block: B:466:0x088b  */
    /* JADX WARN: Removed duplicated region for block: B:468:0x0890  */
    /* JADX WARN: Removed duplicated region for block: B:470:0x0895  */
    /* JADX WARN: Removed duplicated region for block: B:472:0x089a  */
    /* JADX WARN: Removed duplicated region for block: B:474:0x089f  */
    /* JADX WARN: Removed duplicated region for block: B:476:0x08a4  */
    /* JADX WARN: Removed duplicated region for block: B:478:0x08a9  */
    /* JADX WARN: Removed duplicated region for block: B:480:0x08ae  */
    /* JADX WARN: Removed duplicated region for block: B:482:0x08b3  */
    /* JADX WARN: Removed duplicated region for block: B:484:0x08b8  */
    /* JADX WARN: Removed duplicated region for block: B:486:0x08bd  */
    /* JADX WARN: Removed duplicated region for block: B:504:0x0422 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:524:0x02fa A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:589:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs r30, int r31) {
        /*
            Method dump skipped, instructions count: 2243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void getDialogFolderId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda108
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogFolderId$193(j, intCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialogFolderId$193(long j, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                final int i = -1;
                if (this.unknownDialogsIds.get(j) == null) {
                    sQLiteCursor = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
                    if (sQLiteCursor.next()) {
                        i = sQLiteCursor.intValue(0);
                    }
                    sQLiteCursor.dispose();
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.IntCallback.this.run(i);
                    }
                });
                if (sQLiteCursor == null) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void setDialogsFolderId(final ArrayList<TLRPC$TL_folderPeer> arrayList, final ArrayList<TLRPC$TL_inputFolderPeer> arrayList2, final long j, final int i) {
        if (arrayList == null && arrayList2 == null && j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda154
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogsFolderId$194(arrayList, arrayList2, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogsFolderId$194(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        SQLitePreparedStatement executeFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$TL_folderPeer tLRPC$TL_folderPeer = (TLRPC$TL_folderPeer) arrayList.get(i2);
                    long peerDialogId = DialogObject.getPeerDialogId(tLRPC$TL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tLRPC$TL_folderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId);
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    TLRPC$TL_inputFolderPeer tLRPC$TL_inputFolderPeer = (TLRPC$TL_inputFolderPeer) arrayList2.get(i3);
                    long peerDialogId2 = DialogObject.getPeerDialogId(tLRPC$TL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tLRPC$TL_inputFolderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId2);
                    executeFast.step();
                    this.unknownDialogsIds.remove(peerDialogId2);
                }
            } else {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, 0);
                executeFast.bindLong(3, j);
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
            lambda$checkIfFolderEmpty$196(1);
            resetAllUnreadCounters(false);
            SQLiteDatabase sQLiteDatabase = this.database;
            if (sQLiteDatabase == null) {
                return;
            }
            sQLiteDatabase.commitTransaction();
        } catch (Exception e2) {
            e = e2;
            sQLitePreparedStatement = executeFast;
            FileLog.e(e);
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (sQLiteDatabase2 != null) {
                sQLiteDatabase2.commitTransaction();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
            sQLitePreparedStatement.dispose();
        } catch (Throwable th2) {
            th = th2;
            sQLitePreparedStatement = executeFast;
            SQLiteDatabase sQLiteDatabase3 = this.database;
            if (sQLiteDatabase3 != null) {
                sQLiteDatabase3.commitTransaction();
            }
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: checkIfFolderEmptyInternal */
    public void lambda$checkIfFolderEmpty$196(final int i) {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                boolean z = true;
                sQLiteCursor = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(i));
                while (sQLiteCursor.next()) {
                    long longValue = sQLiteCursor.longValue(0);
                    if (!DialogObject.isUserDialog(longValue) && !DialogObject.isEncryptedDialog(longValue)) {
                        TLRPC$Chat chat = getChat(-longValue);
                        if (ChatObject.isNotInChat(chat) || chat.migrated_to != null) {
                        }
                    }
                    z = false;
                }
                sQLiteCursor.dispose();
                if (z) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda26
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesStorage.this.lambda$checkIfFolderEmptyInternal$195(i);
                        }
                    });
                    SQLiteDatabase sQLiteDatabase = this.database;
                    sQLiteDatabase.executeFast("DELETE FROM dialogs WHERE did = " + DialogObject.makeFolderDialogId(i)).stepThis().dispose();
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor == null) {
                    return;
                }
            }
            sQLiteCursor.dispose();
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkIfFolderEmptyInternal$195(int i) {
        getMessagesController().onFolderEmpty(i);
    }

    public void checkIfFolderEmpty(final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$checkIfFolderEmpty$196(i);
            }
        });
    }

    public void unpinAllDialogsExceptNew(final ArrayList<Long> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda148
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$unpinAllDialogsExceptNew$197(arrayList, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00a2  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$unpinAllDialogsExceptNew$197(java.util.ArrayList r10, int r11) {
        /*
            r9 = this;
            r0 = 0
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            r1.<init>()     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            org.telegram.SQLite.SQLiteDatabase r2 = r9.database     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.util.Locale r3 = java.util.Locale.US     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.lang.String r4 = "SELECT did, folder_id FROM dialogs WHERE pinned > 0 AND did NOT IN (%s)"
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.lang.String r7 = ","
            java.lang.String r10 = android.text.TextUtils.join(r7, r10)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            r7 = 0
            r6[r7] = r10     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.lang.String r10 = java.lang.String.format(r3, r4, r6)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.lang.Object[] r3 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            org.telegram.SQLite.SQLiteCursor r10 = r2.queryFinalized(r10, r3)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
        L22:
            boolean r2 = r10.next()     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            if (r2 == 0) goto L4a
            long r2 = r10.longValue(r7)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            int r4 = r10.intValue(r5)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            if (r4 != r11) goto L22
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            if (r4 != 0) goto L22
            boolean r2 = org.telegram.messenger.DialogObject.isFolderDialogId(r2)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            if (r2 != 0) goto L22
            long r2 = r10.longValue(r7)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            r1.add(r2)     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            goto L22
        L4a:
            r10.dispose()     // Catch: java.lang.Throwable -> L82 java.lang.Exception -> L87
            boolean r10 = r1.isEmpty()     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            if (r10 != 0) goto L9e
            org.telegram.SQLite.SQLiteDatabase r10 = r9.database     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            java.lang.String r11 = "UPDATE dialogs SET pinned = ? WHERE did = ?"
            org.telegram.SQLite.SQLitePreparedStatement r10 = r10.executeFast(r11)     // Catch: java.lang.Throwable -> L8c java.lang.Exception -> L8f
            r11 = 0
        L5c:
            int r2 = r1.size()     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            if (r11 >= r2) goto L7c
            java.lang.Object r2 = r1.get(r11)     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            java.lang.Long r2 = (java.lang.Long) r2     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            long r2 = r2.longValue()     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            r10.requery()     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            r10.bindInteger(r5, r7)     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            r4 = 2
            r10.bindLong(r4, r2)     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            r10.step()     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            int r11 = r11 + 1
            goto L5c
        L7c:
            r10.dispose()     // Catch: java.lang.Exception -> L80 java.lang.Throwable -> L9f
            goto L9e
        L80:
            r11 = move-exception
            goto L91
        L82:
            r11 = move-exception
            r8 = r0
            r0 = r10
            r10 = r8
            goto La0
        L87:
            r11 = move-exception
            r8 = r0
            r0 = r10
            r10 = r8
            goto L91
        L8c:
            r11 = move-exception
            r10 = r0
            goto La0
        L8f:
            r11 = move-exception
            r10 = r0
        L91:
            org.telegram.messenger.FileLog.e(r11)     // Catch: java.lang.Throwable -> L9f
            if (r0 == 0) goto L99
            r0.dispose()
        L99:
            if (r10 == 0) goto L9e
            r10.dispose()
        L9e:
            return
        L9f:
            r11 = move-exception
        La0:
            if (r0 == 0) goto La5
            r0.dispose()
        La5:
            if (r10 == 0) goto Laa
            r10.dispose()
        Laa:
            goto Lac
        Lab:
            throw r11
        Lac:
            goto Lab
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$unpinAllDialogsExceptNew$197(java.util.ArrayList, int):void");
    }

    public void setDialogUnread(final long j, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda119
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogUnread$198(j, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0043 A[Catch: all -> 0x003b, Exception -> 0x003d, TryCatch #2 {Exception -> 0x003d, blocks: (B:9:0x0027, B:27:0x0043, B:29:0x0048, B:28:0x0046, B:19:0x0037, B:32:0x0064, B:33:0x0067), top: B:42:0x0002, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0046 A[Catch: all -> 0x003b, Exception -> 0x003d, TryCatch #2 {Exception -> 0x003d, blocks: (B:9:0x0027, B:27:0x0043, B:29:0x0048, B:28:0x0046, B:19:0x0037, B:32:0x0064, B:33:0x0067), top: B:42:0x0002, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0064 A[Catch: all -> 0x003b, Exception -> 0x003d, TryCatch #2 {Exception -> 0x003d, blocks: (B:9:0x0027, B:27:0x0043, B:29:0x0048, B:28:0x0046, B:19:0x0037, B:32:0x0064, B:33:0x0067), top: B:42:0x0002, outer: #4 }] */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.telegram.SQLite.SQLitePreparedStatement, org.telegram.SQLite.SQLiteCursor] */
    /* JADX WARN: Type inference failed for: r0v3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$setDialogUnread$198(long r6, boolean r8) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r5.database     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            r3.<init>()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            java.lang.String r4 = "SELECT flags FROM dialogs WHERE did = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            r3.append(r6)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L30
            boolean r3 = r2.next()     // Catch: java.lang.Exception -> L2b java.lang.Throwable -> L61
            if (r3 == 0) goto L26
            int r3 = r2.intValue(r1)     // Catch: java.lang.Exception -> L2b java.lang.Throwable -> L61
            goto L27
        L26:
            r3 = 0
        L27:
            r2.dispose()     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            goto L40
        L2b:
            r3 = move-exception
            goto L32
        L2d:
            r6 = move-exception
            r2 = r0
            goto L62
        L30:
            r3 = move-exception
            r2 = r0
        L32:
            org.telegram.messenger.FileLog.e(r3)     // Catch: java.lang.Throwable -> L61
            if (r2 == 0) goto L3f
            r2.dispose()     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            goto L3f
        L3b:
            r6 = move-exception
            goto L71
        L3d:
            r6 = move-exception
            goto L68
        L3f:
            r3 = 0
        L40:
            r2 = 1
            if (r8 == 0) goto L46
            r8 = r3 | 1
            goto L48
        L46:
            r8 = r3 & (-2)
        L48:
            org.telegram.SQLite.SQLiteDatabase r3 = r5.database     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            java.lang.String r4 = "UPDATE dialogs SET flags = ? WHERE did = ?"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r3.executeFast(r4)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            r0.bindInteger(r2, r8)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            r8 = 2
            r0.bindLong(r8, r6)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            r0.step()     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            r0.dispose()     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            r5.resetAllUnreadCounters(r1)     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
            goto L6d
        L61:
            r6 = move-exception
        L62:
            if (r2 == 0) goto L67
            r2.dispose()     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
        L67:
            throw r6     // Catch: java.lang.Throwable -> L3b java.lang.Exception -> L3d
        L68:
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L3b
            if (r0 == 0) goto L70
        L6d:
            r0.dispose()
        L70:
            return
        L71:
            if (r0 == 0) goto L76
            r0.dispose()
        L76:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$setDialogUnread$198(long, boolean):void");
    }

    private void resetAllUnreadCounters(boolean z) {
        int size = this.dialogFilters.size();
        for (int i = 0; i < size; i++) {
            MessagesController.DialogFilter dialogFilter = this.dialogFilters.get(i);
            if (z) {
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                    dialogFilter.pendingUnreadCount = -1;
                }
            } else {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$resetAllUnreadCounters$199();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetAllUnreadCounters$199() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void setDialogPinned(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogPinned$200(i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogPinned$200(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                sQLitePreparedStatement.bindInteger(1, i);
                sQLitePreparedStatement.bindLong(2, j);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void setDialogsPinned(final ArrayList<Long> arrayList, final ArrayList<Integer> arrayList2) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda153
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$setDialogsPinned$201(arrayList, arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDialogsPinned$201(ArrayList arrayList, ArrayList arrayList2) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    sQLitePreparedStatement.requery();
                    sQLitePreparedStatement.bindInteger(1, ((Integer) arrayList2.get(i)).intValue());
                    sQLitePreparedStatement.bindLong(2, ((Long) arrayList.get(i)).longValue());
                    sQLitePreparedStatement.step();
                }
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
                sQLitePreparedStatement.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void putDialogs(final TLRPC$messages_Dialogs tLRPC$messages_Dialogs, final int i) {
        if (tLRPC$messages_Dialogs.dialogs.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda189
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$putDialogs$202(tLRPC$messages_Dialogs, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$putDialogs$202(TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i) {
        putDialogsInternal(tLRPC$messages_Dialogs, i);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogMaxMessageId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogMaxMessageId$204(j, intCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0031, code lost:
        if (r1 == null) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogMaxMessageId$204(long r6, final org.telegram.messenger.MessagesStorage.IntCallback r8) {
        /*
            r5 = this;
            r0 = 1
            int[] r0 = new int[r0]
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r5.database     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r3.<init>()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r4 = "SELECT MAX(mid) FROM messages_v2 WHERE uid = "
            r3.append(r4)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r3.append(r6)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            java.lang.String r6 = r3.toString()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r7 = 0
            java.lang.Object[] r3 = new java.lang.Object[r7]     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            org.telegram.SQLite.SQLiteCursor r1 = r2.queryFinalized(r6, r3)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            boolean r6 = r1.next()     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            if (r6 == 0) goto L33
            int r6 = r1.intValue(r7)     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            r0[r7] = r6     // Catch: java.lang.Throwable -> L2b java.lang.Exception -> L2d
            goto L33
        L2b:
            r6 = move-exception
            goto L3f
        L2d:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)     // Catch: java.lang.Throwable -> L2b
            if (r1 == 0) goto L36
        L33:
            r1.dispose()
        L36:
            org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda5 r6 = new org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda5
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
            return
        L3f:
            if (r1 == 0) goto L44
            r1.dispose()
        L44:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogMaxMessageId$204(long, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getDialogMaxMessageId$203(IntCallback intCallback, int[] iArr) {
        intCallback.run(iArr[0]);
    }

    public int getDialogReadMax(final boolean z, final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda200
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getDialogReadMax$205(z, j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x006c, code lost:
        if (r1 == null) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getDialogReadMax$205(boolean r5, long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
            if (r5 == 0) goto L2e
            org.telegram.SQLite.SQLiteDatabase r5 = r4.database     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r2.<init>()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.String r3 = "SELECT outbox_max FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r2.append(r6)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.String r6 = r2.toString()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.Object[] r7 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            org.telegram.SQLite.SQLiteCursor r1 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            boolean r5 = r1.next()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            if (r5 == 0) goto L6e
            int r5 = r1.intValue(r0)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r8[r0] = r5     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            goto L6e
        L2e:
            org.telegram.SQLite.SQLiteDatabase r5 = r4.database     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r2.<init>()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.String r3 = "SELECT last_mid, inbox_max FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r2.append(r6)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.String r6 = r2.toString()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            java.lang.Object[] r7 = new java.lang.Object[r0]     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            org.telegram.SQLite.SQLiteCursor r1 = r5.queryFinalized(r6, r7)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            boolean r5 = r1.next()     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            if (r5 == 0) goto L6e
            int r5 = r1.intValue(r0)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r6 = 1
            int r6 = r1.intValue(r6)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            if (r6 <= r5) goto L5f
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r8[r0] = r5     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            goto L6e
        L5f:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r6)     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            r8[r0] = r5     // Catch: java.lang.Throwable -> L66 java.lang.Exception -> L68
            goto L6e
        L66:
            r5 = move-exception
            goto L75
        L68:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Throwable -> L66
            if (r1 == 0) goto L71
        L6e:
            r1.dispose()
        L71:
            r9.countDown()
            return
        L75:
            if (r1 == 0) goto L7a
            r1.dispose()
        L7a:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogReadMax$205(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public int getChannelPtsSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getChannelPtsSync$206(j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0033, code lost:
        if (r0 == null) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$getChannelPtsSync$206(long r5, java.lang.Integer[] r7, java.util.concurrent.CountDownLatch r8) {
        /*
            r4 = this;
            r0 = 0
            org.telegram.SQLite.SQLiteDatabase r1 = r4.database     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r2.<init>()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.String r3 = "SELECT pts FROM dialogs WHERE did = "
            r2.append(r3)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            long r5 = -r5
            r2.append(r5)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.String r5 = r2.toString()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r6 = 0
            java.lang.Object[] r2 = new java.lang.Object[r6]     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            org.telegram.SQLite.SQLiteCursor r0 = r1.queryFinalized(r5, r2)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            boolean r5 = r0.next()     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            if (r5 == 0) goto L35
            int r5 = r0.intValue(r6)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            r7[r6] = r5     // Catch: java.lang.Throwable -> L2d java.lang.Exception -> L2f
            goto L35
        L2d:
            r5 = move-exception
            goto L41
        L2f:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Throwable -> L2d
            if (r0 == 0) goto L38
        L35:
            r0.dispose()
        L38:
            r8.countDown()     // Catch: java.lang.Exception -> L3c
            goto L40
        L3c:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)
        L40:
            return
        L41:
            if (r0 == 0) goto L46
            r0.dispose()
        L46:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getChannelPtsSync$206(long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public TLRPC$User getUserSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC$User[] tLRPC$UserArr = new TLRPC$User[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda206
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getUserSync$207(tLRPC$UserArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return tLRPC$UserArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getUserSync$207(TLRPC$User[] tLRPC$UserArr, long j, CountDownLatch countDownLatch) {
        tLRPC$UserArr[0] = getUser(j);
        countDownLatch.countDown();
    }

    public TLRPC$Chat getChatSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC$Chat[] tLRPC$ChatArr = new TLRPC$Chat[1];
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda204
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$getChatSync$208(tLRPC$ChatArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return tLRPC$ChatArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getChatSync$208(TLRPC$Chat[] tLRPC$ChatArr, long j, CountDownLatch countDownLatch) {
        tLRPC$ChatArr[0] = getChat(j);
        countDownLatch.countDown();
    }

    public TLRPC$User getUser(long j) {
        try {
            ArrayList<TLRPC$User> arrayList = new ArrayList<>();
            getUsersInternal("" + j, arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public ArrayList<TLRPC$User> getUsers(ArrayList<Long> arrayList) {
        ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            FileLog.e(e);
        }
        return arrayList2;
    }

    public TLRPC$Chat getChat(long j) {
        try {
            ArrayList<TLRPC$Chat> arrayList = new ArrayList<>();
            getChatsInternal("" + j, arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public TLRPC$EncryptedChat getEncryptedChat(long j) {
        try {
            ArrayList<TLRPC$EncryptedChat> arrayList = new ArrayList<>();
            getEncryptedChatsInternal("" + j, arrayList, null);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x004f, code lost:
        if (r14.length() == 0) goto L9;
     */
    /* JADX WARN: Removed duplicated region for block: B:130:0x02d4 A[Catch: all -> 0x0302, Exception -> 0x0306, LOOP:2: B:100:0x021e->B:130:0x02d4, LOOP_END, TryCatch #9 {Exception -> 0x0306, all -> 0x0302, blocks: (B:90:0x01f0, B:92:0x01f6, B:95:0x020a, B:97:0x0211, B:101:0x0220, B:103:0x0228, B:106:0x0241, B:108:0x0247, B:112:0x025f, B:119:0x026f, B:121:0x0276, B:123:0x0290, B:126:0x029a, B:128:0x02cc, B:127:0x02a5, B:130:0x02d4, B:134:0x02f7, B:144:0x0336, B:146:0x033c, B:151:0x0353, B:153:0x035b, B:156:0x0372, B:158:0x0378, B:161:0x038e, B:162:0x0391, B:164:0x0398, B:166:0x03a5, B:168:0x03a9, B:170:0x03af, B:172:0x03b5, B:173:0x03cd, B:177:0x03f4, B:179:0x03fa, B:183:0x0410, B:185:0x0419, B:189:0x0425, B:191:0x042d, B:194:0x0444, B:196:0x044a, B:200:0x0462, B:205:0x046d, B:207:0x0474, B:209:0x0482, B:211:0x0489, B:215:0x049b, B:217:0x0526, B:218:0x0528, B:220:0x0534, B:223:0x053e, B:225:0x058e, B:224:0x0567, B:226:0x0598, B:229:0x05a4, B:249:0x0605, B:251:0x060b, B:254:0x0617, B:257:0x062b, B:259:0x0632, B:263:0x063e, B:265:0x0646, B:268:0x065d, B:270:0x0663, B:274:0x067b, B:279:0x0686, B:281:0x068d, B:283:0x069c, B:286:0x06a6, B:288:0x06db, B:287:0x06b3, B:290:0x06e2, B:293:0x06f2), top: B:316:0x01f0 }] */
    /* JADX WARN: Removed duplicated region for block: B:226:0x0598 A[Catch: all -> 0x0302, Exception -> 0x0306, LOOP:6: B:188:0x0423->B:226:0x0598, LOOP_END, TryCatch #9 {Exception -> 0x0306, all -> 0x0302, blocks: (B:90:0x01f0, B:92:0x01f6, B:95:0x020a, B:97:0x0211, B:101:0x0220, B:103:0x0228, B:106:0x0241, B:108:0x0247, B:112:0x025f, B:119:0x026f, B:121:0x0276, B:123:0x0290, B:126:0x029a, B:128:0x02cc, B:127:0x02a5, B:130:0x02d4, B:134:0x02f7, B:144:0x0336, B:146:0x033c, B:151:0x0353, B:153:0x035b, B:156:0x0372, B:158:0x0378, B:161:0x038e, B:162:0x0391, B:164:0x0398, B:166:0x03a5, B:168:0x03a9, B:170:0x03af, B:172:0x03b5, B:173:0x03cd, B:177:0x03f4, B:179:0x03fa, B:183:0x0410, B:185:0x0419, B:189:0x0425, B:191:0x042d, B:194:0x0444, B:196:0x044a, B:200:0x0462, B:205:0x046d, B:207:0x0474, B:209:0x0482, B:211:0x0489, B:215:0x049b, B:217:0x0526, B:218:0x0528, B:220:0x0534, B:223:0x053e, B:225:0x058e, B:224:0x0567, B:226:0x0598, B:229:0x05a4, B:249:0x0605, B:251:0x060b, B:254:0x0617, B:257:0x062b, B:259:0x0632, B:263:0x063e, B:265:0x0646, B:268:0x065d, B:270:0x0663, B:274:0x067b, B:279:0x0686, B:281:0x068d, B:283:0x069c, B:286:0x06a6, B:288:0x06db, B:287:0x06b3, B:290:0x06e2, B:293:0x06f2), top: B:316:0x01f0 }] */
    /* JADX WARN: Removed duplicated region for block: B:290:0x06e2 A[Catch: all -> 0x0302, Exception -> 0x0306, LOOP:10: B:262:0x063c->B:290:0x06e2, LOOP_END, TryCatch #9 {Exception -> 0x0306, all -> 0x0302, blocks: (B:90:0x01f0, B:92:0x01f6, B:95:0x020a, B:97:0x0211, B:101:0x0220, B:103:0x0228, B:106:0x0241, B:108:0x0247, B:112:0x025f, B:119:0x026f, B:121:0x0276, B:123:0x0290, B:126:0x029a, B:128:0x02cc, B:127:0x02a5, B:130:0x02d4, B:134:0x02f7, B:144:0x0336, B:146:0x033c, B:151:0x0353, B:153:0x035b, B:156:0x0372, B:158:0x0378, B:161:0x038e, B:162:0x0391, B:164:0x0398, B:166:0x03a5, B:168:0x03a9, B:170:0x03af, B:172:0x03b5, B:173:0x03cd, B:177:0x03f4, B:179:0x03fa, B:183:0x0410, B:185:0x0419, B:189:0x0425, B:191:0x042d, B:194:0x0444, B:196:0x044a, B:200:0x0462, B:205:0x046d, B:207:0x0474, B:209:0x0482, B:211:0x0489, B:215:0x049b, B:217:0x0526, B:218:0x0528, B:220:0x0534, B:223:0x053e, B:225:0x058e, B:224:0x0567, B:226:0x0598, B:229:0x05a4, B:249:0x0605, B:251:0x060b, B:254:0x0617, B:257:0x062b, B:259:0x0632, B:263:0x063e, B:265:0x0646, B:268:0x065d, B:270:0x0663, B:274:0x067b, B:279:0x0686, B:281:0x068d, B:283:0x069c, B:286:0x06a6, B:288:0x06db, B:287:0x06b3, B:290:0x06e2, B:293:0x06f2), top: B:316:0x01f0 }] */
    /* JADX WARN: Removed duplicated region for block: B:305:0x070c  */
    /* JADX WARN: Removed duplicated region for block: B:310:0x0714  */
    /* JADX WARN: Removed duplicated region for block: B:339:0x026f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:364:0x046d A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:376:0x0686 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:379:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void localSearch(int r26, java.lang.String r27, java.util.ArrayList<java.lang.Object> r28, java.util.ArrayList<java.lang.CharSequence> r29, java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r30, int r31) {
        /*
            Method dump skipped, instructions count: 1818
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.localSearch(int, java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$localSearch$209(DialogsSearchAdapter.DialogSearchResult dialogSearchResult, DialogsSearchAdapter.DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x004e, code lost:
        if (0 == 0) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<java.lang.Integer> getCachedMessagesInRange(long r7, int r9, int r10) {
        /*
            r6 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            org.telegram.SQLite.SQLiteDatabase r2 = r6.database     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.util.Locale r3 = java.util.Locale.US     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.String r4 = "SELECT mid FROM messages_v2 WHERE uid = %d AND date >= %d AND date <= %d"
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            r8 = 0
            r5[r8] = r7     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            r7 = 1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            r5[r7] = r9     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            r7 = 2
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            r5[r7] = r9     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.String r7 = java.lang.String.format(r3, r4, r5)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            org.telegram.SQLite.SQLiteCursor r1 = r2.queryFinalized(r7, r9)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
        L2e:
            boolean r7 = r1.next()     // Catch: java.lang.Exception -> L40 java.lang.Throwable -> L48
            if (r7 == 0) goto L44
            int r7 = r1.intValue(r8)     // Catch: java.lang.Exception -> L40 java.lang.Throwable -> L48
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch: java.lang.Exception -> L40 java.lang.Throwable -> L48
            r0.add(r7)     // Catch: java.lang.Exception -> L40 java.lang.Throwable -> L48
            goto L2e
        L40:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
        L44:
            r1.dispose()     // Catch: java.lang.Throwable -> L48 java.lang.Exception -> L4a
            goto L50
        L48:
            r7 = move-exception
            goto L54
        L4a:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L48
            if (r1 == 0) goto L53
        L50:
            r1.dispose()
        L53:
            return r0
        L54:
            if (r1 == 0) goto L59
            r1.dispose()
        L59:
            goto L5b
        L5a:
            throw r7
        L5b:
            goto L5a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getCachedMessagesInRange(long, int, int):java.util.ArrayList");
    }

    public void updateUnreadReactionsCount(long j, int i, int i2) {
        updateUnreadReactionsCount(j, i, i2, false);
    }

    public void updateUnreadReactionsCount(final long j, final int i, final int i2, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda64
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateUnreadReactionsCount$210(i, z, j, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0077  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:62:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateUnreadReactionsCount$210(int r8, boolean r9, long r10, int r12) {
        /*
            r7 = this;
            r0 = 0
            r1 = 2
            r2 = 1
            r3 = 0
            if (r8 == 0) goto L81
            if (r9 == 0) goto L34
            org.telegram.SQLite.SQLiteDatabase r9 = r7.database     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.String r4 = "SELECT unread_reactions FROM topics WHERE did = %d AND topic_id = %d"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.Long r6 = java.lang.Long.valueOf(r10)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r5[r3] = r6     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.Integer r6 = java.lang.Integer.valueOf(r8)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r5[r2] = r6     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            org.telegram.SQLite.SQLiteCursor r9 = r9.queryFinalized(r4, r5)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            boolean r4 = r9.next()     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            if (r4 == 0) goto L2f
            int r4 = r9.intValue(r3)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            goto L30
        L2f:
            r4 = 0
        L30:
            r9.dispose()     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            goto L35
        L34:
            r4 = 0
        L35:
            org.telegram.SQLite.SQLiteDatabase r9 = r7.database     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.String r5 = "UPDATE topics SET unread_reactions = ? WHERE did = ? AND topic_id = ?"
            org.telegram.SQLite.SQLitePreparedStatement r9 = r9.executeFast(r5)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            int r4 = r4 + r12
            int r3 = java.lang.Math.max(r4, r3)     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            r9.bindInteger(r2, r3)     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            r9.bindLong(r1, r10)     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            r3 = 3
            r9.bindInteger(r3, r8)     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            r9.step()     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            r9.dispose()     // Catch: java.lang.Throwable -> L69 org.telegram.SQLite.SQLiteException -> L6c
            if (r12 != 0) goto Lc0
            org.telegram.SQLite.SQLiteDatabase r9 = r7.database     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            java.lang.String r12 = "UPDATE reaction_mentions_topics SET state = 0 WHERE dialog_id = ? AND topic_id = ? "
            org.telegram.SQLite.SQLitePreparedStatement r0 = r9.executeFast(r12)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r0.bindLong(r2, r10)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r0.bindInteger(r1, r8)     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r0.step()     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            r0.dispose()     // Catch: java.lang.Throwable -> L6f org.telegram.SQLite.SQLiteException -> L71
            goto Lc0
        L69:
            r8 = move-exception
            r0 = r9
            goto L7b
        L6c:
            r8 = move-exception
            r0 = r9
            goto L72
        L6f:
            r8 = move-exception
            goto L7b
        L71:
            r8 = move-exception
        L72:
            r8.printStackTrace()     // Catch: java.lang.Throwable -> L6f
            if (r0 == 0) goto Lc0
            r0.dispose()
            goto Lc0
        L7b:
            if (r0 == 0) goto L80
            r0.dispose()
        L80:
            throw r8
        L81:
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            java.lang.String r9 = "UPDATE dialogs SET unread_reactions = ? WHERE did = ?"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r8.executeFast(r9)     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            int r9 = java.lang.Math.max(r12, r3)     // Catch: java.lang.Throwable -> Lad org.telegram.SQLite.SQLiteException -> Lb1
            r8.bindInteger(r2, r9)     // Catch: java.lang.Throwable -> Lad org.telegram.SQLite.SQLiteException -> Lb1
            r8.bindLong(r1, r10)     // Catch: java.lang.Throwable -> Lad org.telegram.SQLite.SQLiteException -> Lb1
            r8.step()     // Catch: java.lang.Throwable -> Lad org.telegram.SQLite.SQLiteException -> Lb1
            r8.dispose()     // Catch: java.lang.Throwable -> Lad org.telegram.SQLite.SQLiteException -> Lb1
            if (r12 != 0) goto Lc0
            org.telegram.SQLite.SQLiteDatabase r8 = r7.database     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            java.lang.String r9 = "UPDATE reaction_mentions SET state = 0 WHERE dialog_id = ?"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r8.executeFast(r9)     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            r0.bindLong(r2, r10)     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            r0.step()     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            r0.dispose()     // Catch: java.lang.Throwable -> Lb5 org.telegram.SQLite.SQLiteException -> Lb7
            goto Lc0
        Lad:
            r9 = move-exception
            r0 = r8
            r8 = r9
            goto Lc1
        Lb1:
            r9 = move-exception
            r0 = r8
            r8 = r9
            goto Lb8
        Lb5:
            r8 = move-exception
            goto Lc1
        Lb7:
            r8 = move-exception
        Lb8:
            r8.printStackTrace()     // Catch: java.lang.Throwable -> Lb5
            if (r0 == 0) goto Lc0
            r0.dispose()
        Lc0:
            return
        Lc1:
            if (r0 == 0) goto Lc6
            r0.dispose()
        Lc6:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateUnreadReactionsCount$210(int, boolean, long, int):void");
    }

    public void markMessageReactionsAsRead(final long j, final int i, final int i2, boolean z) {
        if (z) {
            getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesStorage.this.lambda$markMessageReactionsAsRead$211(j, i, i2);
                }
            });
        } else {
            lambda$markMessageReactionsAsRead$211(j, i, i2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x0161  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0166  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x016d  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0172  */
    /* JADX WARN: Removed duplicated region for block: B:83:? A[RETURN, SYNTHETIC] */
    /* renamed from: markMessageReactionsAsReadInternal */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$markMessageReactionsAsRead$211(long r18, int r20, int r21) {
        /*
            Method dump skipped, instructions count: 380
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$markMessageReactionsAsRead$211(long, int, int):void");
    }

    public void updateDialogUnreadReactions(final long j, final int i, final int i2, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.MessagesStorage$$ExternalSyntheticLambda197
            @Override // java.lang.Runnable
            public final void run() {
                MessagesStorage.this.lambda$updateDialogUnreadReactions$212(z, j, i2, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00ca  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00db  */
    /* JADX WARN: Removed duplicated region for block: B:72:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$updateDialogUnreadReactions$212(boolean r10, long r11, int r13, int r14) {
        /*
            Method dump skipped, instructions count: 223
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$updateDialogUnreadReactions$212(boolean, long, int, int):void");
    }

    private boolean isForum(long j) {
        int i = this.dialogIsForum.get(j, -1);
        if (i == -1) {
            TLRPC$Chat chat = getChat(-j);
            i = (chat == null || !chat.forum) ? 0 : 1;
            this.dialogIsForum.put(j, i);
        }
        return i == 1;
    }

    /* loaded from: classes.dex */
    public static class TopicKey {
        public long dialogId;
        public int topicId;

        public static TopicKey of(long j, int i) {
            TopicKey topicKey = new TopicKey();
            topicKey.dialogId = j;
            topicKey.topicId = i;
            return topicKey;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            TopicKey topicKey = (TopicKey) obj;
            return this.dialogId == topicKey.dialogId && this.topicId == topicKey.topicId;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{Long.valueOf(this.dialogId), Integer.valueOf(this.topicId)});
        }
    }
}
