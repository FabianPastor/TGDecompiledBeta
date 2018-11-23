package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C0016C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.PhoneFormat.C0194PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.MediaController.SearchImage;
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
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userFull;
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

    private class Hole {
        public int end;
        public int start;
        public int type;

        public Hole(int s, int e) {
            this.start = s;
            this.end = e;
        }

        public Hole(int t, int s, int e) {
            this.type = t;
            this.start = s;
            this.end = e;
        }
    }

    public interface IntCallback {
        /* renamed from: run */
        void lambda$null$84$MessagesStorage(int i);
    }

    public static MessagesStorage getInstance(int num) {
        Throwable th;
        MessagesStorage localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesStorage.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        MessagesStorage[] messagesStorageArr = Instance;
                        MessagesStorage localInstance2 = new MessagesStorage(num);
                        try {
                            messagesStorageArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable th) {
        }
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int value) {
        ensureOpened();
        this.lastDateValue = value;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public void setLastPtsValue(int value) {
        ensureOpened();
        this.lastPtsValue = value;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int value) {
        ensureOpened();
        this.lastQtsValue = value;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int value) {
        ensureOpened();
        this.lastSeqValue = value;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int value) {
        ensureOpened();
        this.lastSecretVersion = value;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] value) {
        ensureOpened();
        this.secretPBytes = value;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int value) {
        ensureOpened();
        this.secretG = value;
    }

    public MessagesStorage(int instance) {
        this.currentAccount = instance;
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$new$0$MessagesStorage() {
        openDatabase(true);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public long getDatabaseSize() {
        long size = 0;
        if (this.cacheFile != null) {
            size = 0 + this.cacheFile.length();
        }
        if (this.shmCacheFile != null) {
            return size + this.shmCacheFile.length();
        }
        return size;
    }

    public void openDatabase(boolean first) {
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            File filesDir2 = new File(filesDir, "account" + this.currentAccount + "/");
            filesDir2.mkdirs();
            filesDir = filesDir2;
        }
        this.cacheFile = new File(filesDir, "cache4.db");
        this.walCacheFile = new File(filesDir, "cache4.db-wal");
        this.shmCacheFile = new File(filesDir, "cache4.db-shm");
        boolean createTable = false;
        if (!this.cacheFile.exists()) {
            createTable = true;
        }
        try {
            this.database = new SQLiteDatabase(this.cacheFile.getPath());
            this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            if (createTable) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("create new database");
                }
                this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
                this.database.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            } else {
                int version = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("current db version = " + version);
                }
                if (version == 0) {
                    throw new Exception("malformed");
                }
                try {
                    SQLiteCursor cursor = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
                    if (cursor.next()) {
                        this.lastSeqValue = cursor.intValue(0);
                        this.lastPtsValue = cursor.intValue(1);
                        this.lastDateValue = cursor.intValue(2);
                        this.lastQtsValue = cursor.intValue(3);
                        this.lastSecretVersion = cursor.intValue(4);
                        this.secretG = cursor.intValue(5);
                        if (cursor.isNull(6)) {
                            this.secretPBytes = null;
                        } else {
                            this.secretPBytes = cursor.byteArrayValue(6);
                            if (this.secretPBytes != null && this.secretPBytes.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    cursor.dispose();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                    }
                }
                if (version < 52) {
                    updateDbToLastVersion(version);
                }
            }
        } catch (Throwable e3) {
            FileLog.m13e(e3);
            if (first && e3.getMessage().contains("malformed")) {
                cleanupInternal();
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = 0;
                UserConfig.getInstance(this.currentAccount).totalDialogsLoadCount = 0;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = 0;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = 0;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                openDatabase(false);
            }
        }
        loadUnreadMessages();
        loadPendingTasks();
        try {
            this.openSync.countDown();
        } catch (Throwable th) {
        }
    }

    private void updateDbToLastVersion(int currentVersion) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$1(this, currentVersion));
    }

    final /* synthetic */ void lambda$updateDbToLastVersion$1$MessagesStorage(int currentVersion) {
        SQLiteCursor cursor;
        SQLitePreparedStatement state;
        NativeByteBuffer data;
        int version = currentVersion;
        if (version < 4) {
            try {
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
                this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                fixNotificationSettings();
                this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                version = 4;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        if (version == 4) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            this.database.beginTransaction();
            cursor = this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            state = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (cursor.next()) {
                int date = cursor.intValue(0);
                data = cursor.byteBufferValue(1);
                if (data != null) {
                    int length = data.limit();
                    for (int a = 0; a < length / 4; a++) {
                        state.requery();
                        state.bindInteger(1, data.readInt32(false));
                        state.bindInteger(2, date);
                        state.step();
                    }
                    data.reuse();
                }
            }
            state.dispose();
            cursor.dispose();
            this.database.commitTransaction();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            version = 6;
        }
        if (version == 6) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            version = 7;
        }
        if (version == 7 || version == 8 || version == 9) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            version = 10;
        }
        if (version == 10) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            version = 11;
        }
        if (version == 11 || version == 12) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            version = 13;
        }
        if (version == 13) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            version = 14;
        }
        if (version == 14) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            version = 15;
        }
        if (version == 15) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            version = 16;
        }
        if (version == 16) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            version = 17;
        }
        if (version == 17) {
            this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            version = 18;
        }
        if (version == 18) {
            this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            version = 19;
        }
        if (version == 19) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            version = 20;
        }
        if (version == 20) {
            this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            version = 21;
        }
        if (version == 21) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            cursor = this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (cursor.next()) {
                int chat_id = cursor.intValue(0);
                data = cursor.byteBufferValue(1);
                if (data != null) {
                    ChatParticipants participants = ChatParticipants.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (participants != null) {
                        TL_chatFull chatFull = new TL_chatFull();
                        chatFull.f79id = chat_id;
                        chatFull.chat_photo = new TL_photoEmpty();
                        chatFull.notify_settings = new TL_peerNotifySettingsEmpty_layer77();
                        chatFull.exported_invite = new TL_chatInviteEmpty();
                        chatFull.participants = participants;
                        NativeByteBuffer data2 = new NativeByteBuffer(chatFull.getObjectSize());
                        chatFull.serializeToStream(data2);
                        state.requery();
                        state.bindInteger(1, chat_id);
                        state.bindByteBuffer(2, data2);
                        state.step();
                        data2.reuse();
                    }
                }
            }
            state.dispose();
            cursor.dispose();
            this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            version = 22;
        }
        if (version == 22) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            version = 23;
        }
        if (version == 23 || version == 24) {
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            version = 25;
        }
        if (version == 25 || version == 26) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            version = 27;
        }
        if (version == 27) {
            this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            version = 28;
        }
        if (version == 28 || version == 29) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            version = 30;
        }
        if (version == 30) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            version = 31;
        }
        if (version == 31) {
            this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            version = 32;
        }
        if (version == 32) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            version = 33;
        }
        if (version == 33) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            version = 34;
        }
        if (version == 34) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            version = 35;
        }
        if (version == 35) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            version = 36;
        }
        if (version == 36) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            version = 37;
        }
        if (version == 37) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            version = 38;
        }
        if (version == 38) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            version = 39;
        }
        if (version == 39) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            version = 40;
        }
        if (version == 40) {
            fixNotificationSettings();
            this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            version = 41;
        }
        if (version == 41) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            version = 42;
        }
        if (version == 42) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            version = 43;
        }
        if (version == 43) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            version = 44;
        }
        if (version == 44) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            version = 45;
        }
        if (version == 45) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            version = 46;
        }
        if (version == 46) {
            this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            version = 47;
        }
        if (version == 47) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            version = 48;
        }
        if (version == 48) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            version = 49;
        }
        if (version == 49) {
            this.database.executeFast("DELETE FROM chat_pinned WHERE uid = 1").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            version = 50;
        }
        if (version == 50) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            version = 51;
        }
        if (version == 51) {
            this.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
        }
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

    public void cleanup(boolean isLogin) {
        if (!isLogin) {
            this.storageQueue.cleanupQueue();
        }
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$2(this, isLogin));
    }

    final /* synthetic */ void lambda$cleanup$3$MessagesStorage(boolean isLogin) {
        cleanupInternal();
        openDatabase(false);
        if (isLogin) {
            Utilities.stageQueue.postRunnable(new MessagesStorage$$Lambda$132(this));
        }
    }

    final /* synthetic */ void lambda$null$2$MessagesStorage() {
        MessagesController.getInstance(this.currentAccount).getDifference();
    }

    public void saveSecretParams(int lsv, int sg, byte[] pbytes) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$3(this, lsv, sg, pbytes));
    }

    final /* synthetic */ void lambda$saveSecretParams$4$MessagesStorage(int lsv, int sg, byte[] pbytes) {
        int i = 1;
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            state.bindInteger(1, lsv);
            state.bindInteger(2, sg);
            if (pbytes != null) {
                i = pbytes.length;
            }
            NativeByteBuffer data = new NativeByteBuffer(i);
            if (pbytes != null) {
                data.writeBytes(pbytes);
            }
            state.bindByteBuffer(3, data);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void fixNotificationSettings() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$4(this));
    }

    final /* synthetic */ void lambda$fixNotificationSettings$5$MessagesStorage() {
        try {
            LongSparseArray<Long> ids = new LongSparseArray();
            Map<String, ?> values = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Entry<String, ?> entry : values.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("notify2_")) {
                    Integer value = (Integer) entry.getValue();
                    if (value.intValue() == 2 || value.intValue() == 3) {
                        long flags;
                        key = key.replace("notify2_", TtmlNode.ANONYMOUS_REGION_ID);
                        if (value.intValue() == 2) {
                            flags = 1;
                        } else {
                            Integer time = (Integer) values.get("notifyuntil_" + key);
                            if (time != null) {
                                flags = (((long) time.intValue()) << 32) | 1;
                            } else {
                                flags = 1;
                            }
                        }
                        ids.put(Long.parseLong(key), Long.valueOf(flags));
                    } else if (value.intValue() == 3) {
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int a = 0; a < ids.size(); a++) {
                    state.requery();
                    state.bindLong(1, ids.keyAt(a));
                    state.bindLong(2, ((Long) ids.valueAt(a)).longValue());
                    state.step();
                }
                state.dispose();
                this.database.commitTransaction();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } catch (Exception e2) {
            ThrowableExtension.printStackTrace(e2);
        } catch (Throwable e3) {
            FileLog.m13e(e3);
        }
    }

    public long createPendingTask(NativeByteBuffer data) {
        if (data == null) {
            return 0;
        }
        long id = this.lastTaskId.getAndAdd(1);
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$5(this, id, data));
        return id;
    }

    final /* synthetic */ void lambda$createPendingTask$6$MessagesStorage(long id, NativeByteBuffer data) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
            state.bindLong(1, id);
            state.bindByteBuffer(2, data);
            state.step();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        } finally {
            data.reuse();
        }
    }

    public void removePendingTask(long id) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$6(this, id));
    }

    final /* synthetic */ void lambda$removePendingTask$7$MessagesStorage(long id) {
        try {
            this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + id).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$7(this));
    }

    final /* synthetic */ void lambda$loadPendingTasks$15$MessagesStorage() {
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            while (cursor.next()) {
                long taskId = cursor.longValue(0);
                AbstractSerializedData data = cursor.byteBufferValue(1);
                if (data != null) {
                    int type = data.readInt32(false);
                    switch (type) {
                        case 0:
                            Chat chat = Chat.TLdeserialize(data, data.readInt32(false), false);
                            if (chat != null) {
                                Utilities.stageQueue.postRunnable(new MessagesStorage$$Lambda$125(this, chat, taskId));
                                break;
                            }
                            break;
                        case 1:
                            Utilities.stageQueue.postRunnable(new MessagesStorage$$Lambda$126(this, data.readInt32(false), data.readInt32(false), taskId));
                            break;
                        case 2:
                        case 5:
                        case 8:
                        case 10:
                            TL_dialog dialog = new TL_dialog();
                            dialog.f128id = data.readInt64(false);
                            dialog.top_message = data.readInt32(false);
                            dialog.read_inbox_max_id = data.readInt32(false);
                            dialog.read_outbox_max_id = data.readInt32(false);
                            dialog.unread_count = data.readInt32(false);
                            dialog.last_message_date = data.readInt32(false);
                            dialog.pts = data.readInt32(false);
                            dialog.flags = data.readInt32(false);
                            if (type >= 5) {
                                dialog.pinned = data.readBool(false);
                                dialog.pinnedNum = data.readInt32(false);
                            }
                            if (type >= 8) {
                                dialog.unread_mentions_count = data.readInt32(false);
                            }
                            if (type >= 10) {
                                dialog.unread_mark = data.readBool(false);
                            }
                            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$127(this, dialog, InputPeer.TLdeserialize(data, data.readInt32(false), false), taskId));
                            break;
                        case 3:
                            long random_id = data.readInt64(false);
                            SendMessagesHelper.getInstance(this.currentAccount).sendGame(InputPeer.TLdeserialize(data, data.readInt32(false), false), (TL_inputMediaGame) InputMedia.TLdeserialize(data, data.readInt32(false), false), random_id, taskId);
                            break;
                        case 4:
                            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$128(this, data.readInt64(false), data.readBool(false), InputPeer.TLdeserialize(data, data.readInt32(false), false), taskId));
                            break;
                        case 6:
                            Utilities.stageQueue.postRunnable(new MessagesStorage$$Lambda$129(this, data.readInt32(false), data.readInt32(false), taskId, InputChannel.TLdeserialize(data, data.readInt32(false), false)));
                            break;
                        case 7:
                            int channelId = data.readInt32(false);
                            int constructor = data.readInt32(false);
                            TLObject request = TL_messages_deleteMessages.TLdeserialize(data, constructor, false);
                            if (request == null) {
                                request = TL_channels_deleteMessages.TLdeserialize(data, constructor, false);
                            }
                            if (request != null) {
                                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$130(this, channelId, taskId, request));
                                break;
                            }
                            removePendingTask(taskId);
                            break;
                        case 9:
                            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$131(this, data.readInt64(false), InputPeer.TLdeserialize(data, data.readInt32(false), false), taskId));
                            break;
                    }
                    data.reuse();
                }
            }
            cursor.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$8$MessagesStorage(Chat chat, long taskId) {
        MessagesController.getInstance(this.currentAccount).loadUnknownChannel(chat, taskId);
    }

    final /* synthetic */ void lambda$null$9$MessagesStorage(int channelId, int newDialogType, long taskId) {
        MessagesController.getInstance(this.currentAccount).getChannelDifference(channelId, newDialogType, taskId, null);
    }

    final /* synthetic */ void lambda$null$10$MessagesStorage(TL_dialog dialog, InputPeer peer, long taskId) {
        MessagesController.getInstance(this.currentAccount).checkLastDialogMessage(dialog, peer, taskId);
    }

    final /* synthetic */ void lambda$null$11$MessagesStorage(long did, boolean pin, InputPeer peer, long taskId) {
        MessagesController.getInstance(this.currentAccount).pinDialog(did, pin, peer, taskId);
    }

    final /* synthetic */ void lambda$null$12$MessagesStorage(int channelId, int newDialogType, long taskId, InputChannel inputChannel) {
        MessagesController.getInstance(this.currentAccount).getChannelDifference(channelId, newDialogType, taskId, inputChannel);
    }

    final /* synthetic */ void lambda$null$13$MessagesStorage(int channelId, long taskId, TLObject finalRequest) {
        MessagesController.getInstance(this.currentAccount).deleteMessages(null, null, null, channelId, true, taskId, finalRequest);
    }

    final /* synthetic */ void lambda$null$14$MessagesStorage(long did, InputPeer peer, long taskId) {
        MessagesController.getInstance(this.currentAccount).markDialogAsUnread(did, peer, taskId);
    }

    public void saveChannelPts(int channelId, int pts) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$8(this, pts, channelId));
    }

    final /* synthetic */ void lambda$saveChannelPts$16$MessagesStorage(int pts, int channelId) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            state.bindInteger(1, pts);
            state.bindInteger(2, -channelId);
            state.step();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void saveDiffParamsInternal(int seq, int pts, int date, int qts) {
        try {
            if (this.lastSavedSeq != seq || this.lastSavedPts != pts || this.lastSavedDate != date || this.lastQtsValue != qts) {
                SQLitePreparedStatement state = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
                state.bindInteger(1, seq);
                state.bindInteger(2, pts);
                state.bindInteger(3, date);
                state.bindInteger(4, qts);
                state.step();
                state.dispose();
                this.lastSavedSeq = seq;
                this.lastSavedPts = pts;
                this.lastSavedDate = date;
                this.lastSavedQts = qts;
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void saveDiffParams(int seq, int pts, int date, int qts) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$9(this, seq, pts, date, qts));
    }

    public void setDialogFlags(long did, long flags) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$10(this, did, flags));
    }

    final /* synthetic */ void lambda$setDialogFlags$18$MessagesStorage(long did, long flags) {
        try {
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(did), Long.valueOf(flags)})).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putPushMessage(MessageObject message) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$11(this, message));
    }

    final /* synthetic */ void lambda$putPushMessage$19$MessagesStorage(MessageObject message) {
        try {
            NativeByteBuffer data = new NativeByteBuffer(message.messageOwner.getObjectSize());
            message.messageOwner.serializeToStream(data);
            long messageId = (long) message.getId();
            if (message.messageOwner.to_id.channel_id != 0) {
                messageId |= ((long) message.messageOwner.to_id.channel_id) << 32;
            }
            int flags = 0;
            if (message.localType == 2) {
                flags = 0 | 1;
            }
            if (message.localChannel) {
                flags |= 2;
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO unread_push_messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindLong(1, message.getDialogId());
            state.bindLong(2, messageId);
            state.bindLong(3, message.messageOwner.random_id);
            state.bindInteger(4, message.messageOwner.date);
            state.bindByteBuffer(5, data);
            if (message.messageText == null) {
                state.bindNull(6);
            } else {
                state.bindString(6, message.messageText.toString());
            }
            if (message.localName == null) {
                state.bindNull(7);
            } else {
                state.bindString(7, message.localName);
            }
            if (message.localUserName == null) {
                state.bindNull(8);
            } else {
                state.bindString(8, message.localUserName);
            }
            state.bindInteger(9, flags);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$12(this));
    }

    final /* synthetic */ void lambda$loadUnreadMessages$21$MessagesStorage() {
        try {
            long did;
            int lower_id;
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            ArrayList<Integer> encryptedChatIds = new ArrayList();
            LongSparseArray<Integer> pushDialogs = new LongSparseArray();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder ids = new StringBuilder();
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            while (cursor.next()) {
                long flags = cursor.longValue(2);
                boolean muted = (1 & flags) != 0;
                int mutedUntil = (int) (flags >> 32);
                if (cursor.isNull(2) || !muted || (mutedUntil != 0 && mutedUntil < currentTime)) {
                    did = cursor.longValue(0);
                    pushDialogs.put(did, Integer.valueOf(cursor.intValue(1)));
                    if (ids.length() != 0) {
                        ids.append(",");
                    }
                    ids.append(did);
                    lower_id = (int) did;
                    int high_id = (int) (did >> 32);
                    if (lower_id == 0) {
                        if (!encryptedChatIds.contains(Integer.valueOf(high_id))) {
                            encryptedChatIds.add(Integer.valueOf(high_id));
                        }
                    } else if (lower_id < 0) {
                        if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                        }
                    } else {
                        if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                            usersToLoad.add(Integer.valueOf(lower_id));
                        }
                    }
                }
            }
            cursor.dispose();
            ArrayList<Long> replyMessages = new ArrayList();
            SparseArray<ArrayList<Message>> replyMessageOwners = new SparseArray();
            ArrayList<Message> messages = new ArrayList();
            ArrayList<MessageObject> pushMessages = new ArrayList();
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
            int maxDate = 0;
            if (ids.length() > 0) {
                AbstractSerializedData data;
                Message message;
                Message message2;
                ArrayList<Message> arrayList;
                int a;
                cursor = this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (" + ids.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
                while (cursor.next()) {
                    data = cursor.byteBufferValue(1);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        MessageObject.setUnreadFlags(message, cursor.intValue(0));
                        message.f104id = cursor.intValue(3);
                        message.date = cursor.intValue(4);
                        message.dialog_id = cursor.longValue(5);
                        messages.add(message);
                        maxDate = Math.max(maxDate, message.date);
                        lower_id = (int) message.dialog_id;
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        message.send_state = cursor.intValue(2);
                        if (!(message.to_id.channel_id != 0 || MessageObject.isUnread(message) || lower_id == 0) || message.f104id > 0) {
                            message.send_state = 0;
                        }
                        if (lower_id == 0 && !cursor.isNull(5)) {
                            message.random_id = cursor.longValue(5);
                        }
                        try {
                            if (message.reply_to_msg_id != 0 && ((message.action instanceof TL_messageActionPinMessage) || (message.action instanceof TL_messageActionPaymentSent) || (message.action instanceof TL_messageActionGameScore))) {
                                if (!cursor.isNull(6)) {
                                    data = cursor.byteBufferValue(6);
                                    if (data != null) {
                                        message.replyMessage = Message.TLdeserialize(data, data.readInt32(false), false);
                                        message.replyMessage.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                        data.reuse();
                                        if (message.replyMessage != null) {
                                            if (MessageObject.isMegagroup(message)) {
                                                message2 = message.replyMessage;
                                                message2.flags |= Integer.MIN_VALUE;
                                            }
                                            addUsersAndChatsFromMessage(message.replyMessage, usersToLoad, chatsToLoad);
                                        }
                                    }
                                }
                                if (message.replyMessage == null) {
                                    long messageId = (long) message.reply_to_msg_id;
                                    if (message.to_id.channel_id != 0) {
                                        messageId |= ((long) message.to_id.channel_id) << 32;
                                    }
                                    if (!replyMessages.contains(Long.valueOf(messageId))) {
                                        replyMessages.add(Long.valueOf(messageId));
                                    }
                                    arrayList = (ArrayList) replyMessageOwners.get(message.reply_to_msg_id);
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                        replyMessageOwners.put(message.reply_to_msg_id, arrayList);
                                    }
                                    arrayList.add(message);
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                }
                cursor.dispose();
                this.database.executeFast("DELETE FROM unread_push_messages WHERE date <= " + maxDate).stepThis().dispose();
                cursor = this.database.queryFinalized("SELECT data, mid, date, uid, random, fm, name, uname, flags FROM unread_push_messages WHERE 1 ORDER BY date DESC LIMIT 50", new Object[0]);
                while (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        message.f104id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = cursor.longValue(3);
                        message.random_id = cursor.longValue(4);
                        String messageText = cursor.isNull(5) ? null : cursor.stringValue(5);
                        String name = cursor.isNull(6) ? null : cursor.stringValue(6);
                        String userName = cursor.isNull(7) ? null : cursor.stringValue(7);
                        int flags2 = cursor.intValue(8);
                        pushMessages.add(new MessageObject(this.currentAccount, message, messageText, name, userName, (flags2 & 1) != 0, (flags2 & 2) != 0));
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                    }
                }
                cursor.dispose();
                if (!replyMessages.isEmpty()) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
                    while (cursor.next()) {
                        data = cursor.byteBufferValue(0);
                        if (data != null) {
                            message = Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                            data.reuse();
                            message.f104id = cursor.intValue(1);
                            message.date = cursor.intValue(2);
                            message.dialog_id = cursor.longValue(3);
                            addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                            arrayList = (ArrayList) replyMessageOwners.get(message.f104id);
                            if (arrayList != null) {
                                for (a = 0; a < arrayList.size(); a++) {
                                    Message m = (Message) arrayList.get(a);
                                    m.replyMessage = message;
                                    if (MessageObject.isMegagroup(m)) {
                                        message2 = m.replyMessage;
                                        message2.flags |= Integer.MIN_VALUE;
                                    }
                                }
                            }
                        }
                    }
                    cursor.dispose();
                }
                if (!encryptedChatIds.isEmpty()) {
                    getEncryptedChatsInternal(TextUtils.join(",", encryptedChatIds), encryptedChats, usersToLoad);
                }
                if (!usersToLoad.isEmpty()) {
                    getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    a = 0;
                    while (a < chats.size()) {
                        Chat chat = (Chat) chats.get(a);
                        if (chat != null && (chat.left || chat.migrated_to != null)) {
                            this.database.executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + ((long) (-chat.f78id))).stepThis().dispose();
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(did)})).stepThis().dispose();
                            chats.remove(a);
                            a--;
                            pushDialogs.remove((long) (-chat.f78id));
                            int b = 0;
                            while (b < messages.size()) {
                                if (((Message) messages.get(b)).dialog_id == ((long) (-chat.f78id))) {
                                    messages.remove(b);
                                    b--;
                                }
                                b++;
                            }
                        }
                        a++;
                    }
                }
            }
            Collections.reverse(messages);
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$124(this, pushDialogs, messages, pushMessages, users, chats, encryptedChats));
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
    }

    final /* synthetic */ void lambda$null$20$MessagesStorage(LongSparseArray pushDialogs, ArrayList messages, ArrayList pushMessages, ArrayList users, ArrayList chats, ArrayList encryptedChats) {
        NotificationsController.getInstance(this.currentAccount).processLoadedUnreadMessages(pushDialogs, messages, pushMessages, users, chats, encryptedChats);
    }

    public void putWallpapers(ArrayList<WallPaper> wallPapers) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$13(this, wallPapers));
    }

    final /* synthetic */ void lambda$putWallpapers$22$MessagesStorage(ArrayList wallPapers) {
        int num = 0;
        try {
            this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
            Iterator it = wallPapers.iterator();
            while (it.hasNext()) {
                WallPaper wallPaper = (WallPaper) it.next();
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(wallPaper.getObjectSize());
                wallPaper.serializeToStream(data);
                state.bindInteger(1, num);
                state.bindByteBuffer(2, data);
                state.step();
                num++;
                data.reuse();
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void loadWebRecent(int type) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$14(this, type));
    }

    final /* synthetic */ void lambda$loadWebRecent$24$MessagesStorage(int type) {
        try {
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$123(this, type, new ArrayList()));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$23$MessagesStorage(int type, ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentImagesDidLoad, Integer.valueOf(type), arrayList);
    }

    public void addRecentLocalFile(String imageUrl, String localUrl, Document document) {
        if (imageUrl != null && imageUrl.length() != 0) {
            if ((localUrl != null && localUrl.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new MessagesStorage$$Lambda$15(this, document, imageUrl, localUrl));
            }
        }
    }

    final /* synthetic */ void lambda$addRecentLocalFile$25$MessagesStorage(Document document, String imageUrl, String localUrl) {
        SQLitePreparedStatement state;
        if (document != null) {
            try {
                state = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(data);
                state.bindByteBuffer(1, data);
                state.bindString(2, imageUrl);
                state.step();
                state.dispose();
                data.reuse();
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        state = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
        state.requery();
        state.bindString(1, localUrl);
        state.bindString(2, imageUrl);
        state.step();
        state.dispose();
    }

    public void clearWebRecent(int type) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$16(this, type));
    }

    final /* synthetic */ void lambda$clearWebRecent$26$MessagesStorage(int type) {
        try {
            this.database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + type).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putWebRecent(ArrayList<SearchImage> arrayList) {
        if (!arrayList.isEmpty() && arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$17(this, arrayList));
        }
    }

    final /* synthetic */ void lambda$putWebRecent$27$MessagesStorage(ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int a = 0;
            while (a < arrayList.size() && a != Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                SearchImage searchImage = (SearchImage) arrayList.get(a);
                state.requery();
                state.bindString(1, searchImage.f53id);
                state.bindInteger(2, searchImage.type);
                state.bindString(3, searchImage.imageUrl != null ? searchImage.imageUrl : TtmlNode.ANONYMOUS_REGION_ID);
                state.bindString(4, searchImage.thumbUrl != null ? searchImage.thumbUrl : TtmlNode.ANONYMOUS_REGION_ID);
                state.bindString(5, searchImage.localUrl != null ? searchImage.localUrl : TtmlNode.ANONYMOUS_REGION_ID);
                state.bindInteger(6, searchImage.width);
                state.bindInteger(7, searchImage.height);
                state.bindInteger(8, searchImage.size);
                state.bindInteger(9, searchImage.date);
                NativeByteBuffer data = null;
                if (searchImage.photo != null) {
                    data = new NativeByteBuffer(searchImage.photo.getObjectSize());
                    searchImage.photo.serializeToStream(data);
                    state.bindByteBuffer(10, data);
                } else if (searchImage.document != null) {
                    data = new NativeByteBuffer(searchImage.document.getObjectSize());
                    searchImage.document.serializeToStream(data);
                    state.bindByteBuffer(10, data);
                } else {
                    state.bindNull(10);
                }
                state.step();
                if (data != null) {
                    data.reuse();
                }
                a++;
            }
            state.dispose();
            this.database.commitTransaction();
            if (arrayList.size() >= Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                this.database.beginTransaction();
                for (a = Callback.DEFAULT_DRAG_ANIMATION_DURATION; a < arrayList.size(); a++) {
                    this.database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((SearchImage) arrayList.get(a)).f53id + "'").stepThis().dispose();
                }
                this.database.commitTransaction();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$18(this));
    }

    final /* synthetic */ void lambda$getWallpapers$29$MessagesStorage() {
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
            ArrayList<WallPaper> wallPapers = new ArrayList();
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    WallPaper wallPaper = WallPaper.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    wallPapers.add(wallPaper);
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$122(wallPapers));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void getBlockedUsers() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$19(this));
    }

    final /* synthetic */ void lambda$getBlockedUsers$30$MessagesStorage() {
        try {
            SparseIntArray ids = new SparseIntArray();
            ArrayList<User> users = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
            StringBuilder usersToLoad = new StringBuilder();
            while (cursor.next()) {
                int user_id = cursor.intValue(0);
                ids.put(user_id, 1);
                if (usersToLoad.length() != 0) {
                    usersToLoad.append(",");
                }
                usersToLoad.append(user_id);
            }
            cursor.dispose();
            if (usersToLoad.length() != 0) {
                getUsersInternal(usersToLoad.toString(), users);
            }
            MessagesController.getInstance(this.currentAccount).processLoadedBlockedUsers(ids, users, true);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void deleteBlockedUser(int id) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$20(this, id));
    }

    final /* synthetic */ void lambda$deleteBlockedUser$31$MessagesStorage(int id) {
        try {
            this.database.executeFast("DELETE FROM blocked_users WHERE uid = " + id).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putBlockedUsers(SparseIntArray ids, boolean replace) {
        if (ids != null && ids.size() != 0) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$21(this, replace, ids));
        }
    }

    final /* synthetic */ void lambda$putBlockedUsers$32$MessagesStorage(boolean replace, SparseIntArray ids) {
        if (replace) {
            try {
                this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
        int size = ids.size();
        for (int a = 0; a < size; a++) {
            state.requery();
            state.bindInteger(1, ids.keyAt(a));
            state.step();
        }
        state.dispose();
        this.database.commitTransaction();
    }

    public void deleteUserChannelHistory(int channelId, int uid) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$22(this, channelId, uid));
    }

    final /* synthetic */ void lambda$deleteUserChannelHistory$35$MessagesStorage(int channelId, int uid) {
        long did = (long) (-channelId);
        try {
            ArrayList mids = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + did, new Object[0]);
            ArrayList<File> filesToDelete = new ArrayList();
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        if (!(message == null || message.from_id != uid || message.f104id == 1)) {
                            mids.add(Integer.valueOf(message.f104id));
                            File file;
                            if (message.media instanceof TL_messageMediaPhoto) {
                                Iterator it = message.media.photo.sizes.iterator();
                                while (it.hasNext()) {
                                    file = FileLoader.getPathToAttach((PhotoSize) it.next());
                                    if (file != null && file.toString().length() > 0) {
                                        filesToDelete.add(file);
                                    }
                                }
                            } else if (message.media instanceof TL_messageMediaDocument) {
                                file = FileLoader.getPathToAttach(message.media.document);
                                if (file != null && file.toString().length() > 0) {
                                    filesToDelete.add(file);
                                }
                                file = FileLoader.getPathToAttach(message.media.document.thumb);
                                if (file != null && file.toString().length() > 0) {
                                    filesToDelete.add(file);
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$120(this, mids, channelId));
            markMessagesAsDeletedInternal(mids, channelId);
            updateDialogsWithDeletedMessagesInternal(mids, null, channelId);
            FileLoader.getInstance(this.currentAccount).deleteFiles(filesToDelete, 0);
            if (!mids.isEmpty()) {
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$121(this, mids, channelId));
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
    }

    final /* synthetic */ void lambda$null$33$MessagesStorage(ArrayList mids, int channelId) {
        MessagesController.getInstance(this.currentAccount).markChannelDialogMessageAsDeleted(mids, channelId);
    }

    final /* synthetic */ void lambda$null$34$MessagesStorage(ArrayList mids, int channelId) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, mids, Integer.valueOf(channelId));
    }

    public void deleteDialog(long did, int messagesOnly) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$23(this, messagesOnly, did));
    }

    final /* synthetic */ void lambda$deleteDialog$37$MessagesStorage(int messagesOnly, long did) {
        SQLiteCursor cursor;
        NativeByteBuffer data;
        Message message;
        if (messagesOnly == 3) {
            int lastMid = -1;
            try {
                cursor = this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + did, new Object[0]);
                if (cursor.next()) {
                    lastMid = cursor.intValue(0);
                }
                cursor.dispose();
                if (lastMid != 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        if (((int) did) == 0 || messagesOnly == 2) {
            cursor = this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + did, new Object[0]);
            ArrayList<File> filesToDelete = new ArrayList();
            while (cursor.next()) {
                try {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        if (!(message == null || message.media == null)) {
                            File file;
                            if (message.media instanceof TL_messageMediaPhoto) {
                                Iterator it = message.media.photo.sizes.iterator();
                                while (it.hasNext()) {
                                    file = FileLoader.getPathToAttach((PhotoSize) it.next());
                                    if (file != null && file.toString().length() > 0) {
                                        filesToDelete.add(file);
                                    }
                                }
                            } else if (message.media instanceof TL_messageMediaDocument) {
                                file = FileLoader.getPathToAttach(message.media.document);
                                if (file != null && file.toString().length() > 0) {
                                    filesToDelete.add(file);
                                }
                                file = FileLoader.getPathToAttach(message.media.document.thumb);
                                if (file != null && file.toString().length() > 0) {
                                    filesToDelete.add(file);
                                }
                            }
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
            }
            cursor.dispose();
            FileLoader.getInstance(this.currentAccount).deleteFiles(filesToDelete, messagesOnly);
        }
        if (messagesOnly == 0 || messagesOnly == 3) {
            this.database.executeFast("DELETE FROM dialogs WHERE did = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM chat_settings_v2 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM chat_pinned WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM search_recent WHERE did = " + did).stepThis().dispose();
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            if (lower_id == 0) {
                this.database.executeFast("DELETE FROM enc_chats WHERE uid = " + high_id).stepThis().dispose();
            } else if (high_id == 1) {
                this.database.executeFast("DELETE FROM chats WHERE uid = " + lower_id).stepThis().dispose();
            } else if (lower_id < 0) {
            }
        } else if (messagesOnly == 2) {
            cursor = this.database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + did, new Object[0]);
            int messageId = -1;
            if (cursor.next()) {
                long last_mid_i = cursor.longValue(0);
                long last_mid = cursor.longValue(1);
                SQLiteCursor cursor2 = this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + did + " AND mid IN (" + last_mid_i + "," + last_mid + ")", new Object[0]);
                while (cursor2.next()) {
                    try {
                        data = cursor2.byteBufferValue(0);
                        if (data != null) {
                            message = Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                            data.reuse();
                            if (message != null) {
                                messageId = message.f104id;
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                    }
                }
                cursor2.dispose();
                this.database.executeFast("DELETE FROM messages WHERE uid = " + did + " AND mid != " + last_mid_i + " AND mid != " + last_mid).stepThis().dispose();
                this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + did).stepThis().dispose();
                this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did).stepThis().dispose();
                this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + did).stepThis().dispose();
                this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + did).stepThis().dispose();
                this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did).stepThis().dispose();
                DataQuery.getInstance(this.currentAccount).clearBotKeyboard(did, null);
                SQLitePreparedStatement state5 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                SQLitePreparedStatement state6 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                if (messageId != -1) {
                    createFirstHoles(did, state5, state6, messageId);
                }
                state5.dispose();
                state6.dispose();
            }
            cursor.dispose();
            return;
        }
        this.database.executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM messages WHERE uid = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + did).stepThis().dispose();
        this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did).stepThis().dispose();
        DataQuery.getInstance(this.currentAccount).clearBotKeyboard(did, null);
        AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$119(this));
    }

    final /* synthetic */ void lambda$null$36$MessagesStorage() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void getDialogPhotos(int did, int count, long max_id, int classGuid) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$24(this, max_id, did, count, classGuid));
    }

    final /* synthetic */ void lambda$getDialogPhotos$39$MessagesStorage(long max_id, int did, int count, int classGuid) {
        SQLiteCursor cursor;
        if (max_id != 0) {
            try {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[]{Integer.valueOf(did), Long.valueOf(max_id), Integer.valueOf(count)}), new Object[0]);
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d", new Object[]{Integer.valueOf(did), Integer.valueOf(count)}), new Object[0]);
        photos_Photos res = new TL_photos_photos();
        while (cursor.next()) {
            NativeByteBuffer data = cursor.byteBufferValue(0);
            if (data != null) {
                Photo photo = Photo.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
                res.photos.add(photo);
            }
        }
        cursor.dispose();
        Utilities.stageQueue.postRunnable(new MessagesStorage$$Lambda$118(this, res, did, count, max_id, classGuid));
    }

    final /* synthetic */ void lambda$null$38$MessagesStorage(photos_Photos res, int did, int count, long max_id, int classGuid) {
        MessagesController.getInstance(this.currentAccount).processLoadedUserPhotos(res, did, count, max_id, true, classGuid);
    }

    public void clearUserPhotos(int uid) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$25(this, uid));
    }

    final /* synthetic */ void lambda$clearUserPhotos$40$MessagesStorage(int uid) {
        try {
            this.database.executeFast("DELETE FROM user_photos WHERE uid = " + uid).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void clearUserPhoto(int uid, long pid) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$26(this, uid, pid));
    }

    final /* synthetic */ void lambda$clearUserPhoto$41$MessagesStorage(int uid, long pid) {
        try {
            this.database.executeFast("DELETE FROM user_photos WHERE uid = " + uid + " AND id = " + pid).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void resetDialogs(messages_Dialogs dialogsRes, int messagesCount, int seq, int newPts, int date, int qts, LongSparseArray<TL_dialog> new_dialogs_dict, LongSparseArray<MessageObject> new_dialogMessage, Message lastMessage, int dialogsCount) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$27(this, dialogsRes, dialogsCount, seq, newPts, date, qts, lastMessage, messagesCount, new_dialogs_dict, new_dialogMessage));
    }

    final /* synthetic */ void lambda$resetDialogs$43$MessagesStorage(messages_Dialogs dialogsRes, int dialogsCount, int seq, int newPts, int date, int qts, Message lastMessage, int messagesCount, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        int maxPinnedNum = 0;
        try {
            int a;
            ArrayList<Integer> dids = new ArrayList();
            int totalPinnedCount = dialogsRes.dialogs.size() - dialogsCount;
            LongSparseArray<Integer> oldPinnedDialogNums = new LongSparseArray();
            ArrayList<Long> oldPinnedOrder = new ArrayList();
            ArrayList<Long> orderArrayList = new ArrayList();
            for (a = dialogsCount; a < dialogsRes.dialogs.size(); a++) {
                orderArrayList.add(Long.valueOf(((TL_dialog) dialogsRes.dialogs.get(a)).f128id));
            }
            SQLiteCursor cursor = this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
            while (cursor.next()) {
                long did = cursor.longValue(0);
                int pinnedNum = cursor.intValue(1);
                int lower_id = (int) did;
                if (lower_id != 0) {
                    dids.add(Integer.valueOf(lower_id));
                    if (pinnedNum > 0) {
                        maxPinnedNum = Math.max(pinnedNum, maxPinnedNum);
                        oldPinnedDialogNums.put(did, Integer.valueOf(pinnedNum));
                        oldPinnedOrder.add(Long.valueOf(did));
                    }
                }
            }
            Collections.sort(oldPinnedOrder, new MessagesStorage$$Lambda$117(oldPinnedDialogNums));
            while (oldPinnedOrder.size() < totalPinnedCount) {
                oldPinnedOrder.add(0, Long.valueOf(0));
            }
            cursor.dispose();
            String ids = "(" + TextUtils.join(",", dids) + ")";
            this.database.beginTransaction();
            this.database.executeFast("DELETE FROM dialogs WHERE did IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages WHERE uid IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM bot_keyboard WHERE uid IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_v2 WHERE uid IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_holes WHERE uid IN " + ids).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid IN " + ids).stepThis().dispose();
            this.database.commitTransaction();
            for (a = 0; a < totalPinnedCount; a++) {
                TL_dialog dialog = (TL_dialog) dialogsRes.dialogs.get(dialogsCount + a);
                int oldIdx = oldPinnedOrder.indexOf(Long.valueOf(dialog.f128id));
                int newIdx = orderArrayList.indexOf(Long.valueOf(dialog.f128id));
                if (!(oldIdx == -1 || newIdx == -1)) {
                    Integer oldNum;
                    if (oldIdx == newIdx) {
                        oldNum = (Integer) oldPinnedDialogNums.get(dialog.f128id);
                        if (oldNum != null) {
                            dialog.pinnedNum = oldNum.intValue();
                        }
                    } else {
                        oldNum = (Integer) oldPinnedDialogNums.get(((Long) oldPinnedOrder.get(newIdx)).longValue());
                        if (oldNum != null) {
                            dialog.pinnedNum = oldNum.intValue();
                        }
                    }
                }
                if (dialog.pinnedNum == 0) {
                    dialog.pinnedNum = (totalPinnedCount - a) + maxPinnedNum;
                }
            }
            putDialogsInternal(dialogsRes, 0);
            saveDiffParamsInternal(seq, newPts, date, qts);
            if (lastMessage == null || lastMessage.f104id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId) {
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            } else {
                UserConfig.getInstance(this.currentAccount).totalDialogsLoadCount = dialogsRes.dialogs.size();
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = lastMessage.f104id;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = lastMessage.date;
                Chat chat;
                if (lastMessage.to_id.channel_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = lastMessage.to_id.channel_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
                    for (a = 0; a < dialogsRes.chats.size(); a++) {
                        chat = (Chat) dialogsRes.chats.get(a);
                        if (chat.f78id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                            break;
                        }
                    }
                } else if (lastMessage.to_id.chat_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = lastMessage.to_id.chat_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
                    for (a = 0; a < dialogsRes.chats.size(); a++) {
                        chat = (Chat) dialogsRes.chats.get(a);
                        if (chat.f78id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                            break;
                        }
                    }
                } else if (lastMessage.to_id.user_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = lastMessage.to_id.user_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
                    for (a = 0; a < dialogsRes.users.size(); a++) {
                        User user = (User) dialogsRes.users.get(a);
                        if (user.f176id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = user.access_hash;
                            break;
                        }
                    }
                }
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            MessagesController.getInstance(this.currentAccount).completeDialogsReset(dialogsRes, messagesCount, seq, newPts, date, qts, new_dialogs_dict, new_dialogMessage, lastMessage);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ int lambda$null$42$MessagesStorage(LongSparseArray oldPinnedDialogNums, Long o1, Long o2) {
        Integer val1 = (Integer) oldPinnedDialogNums.get(o1.longValue());
        Integer val2 = (Integer) oldPinnedDialogNums.get(o2.longValue());
        if (val1.intValue() < val2.intValue()) {
            return 1;
        }
        if (val1.intValue() > val2.intValue()) {
            return -1;
        }
        return 0;
    }

    public void putDialogPhotos(int did, photos_Photos photos) {
        if (photos != null && !photos.photos.isEmpty()) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$28(this, did, photos));
        }
    }

    final /* synthetic */ void lambda$putDialogPhotos$44$MessagesStorage(int did, photos_Photos photos) {
        try {
            this.database.executeFast("DELETE FROM user_photos WHERE uid = " + did).stepThis().dispose();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            Iterator it = photos.photos.iterator();
            while (it.hasNext()) {
                Photo photo = (Photo) it.next();
                if (!(photo instanceof TL_photoEmpty)) {
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(photo.getObjectSize());
                    photo.serializeToStream(data);
                    state.bindInteger(1, did);
                    state.bindLong(2, photo.f106id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                }
            }
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void emptyMessagesMedia(ArrayList<Integer> mids) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$29(this, mids));
    }

    final /* synthetic */ void lambda$emptyMessagesMedia$46$MessagesStorage(ArrayList mids) {
        try {
            NativeByteBuffer data;
            Message message;
            ArrayList<File> filesToDelete = new ArrayList();
            ArrayList<Message> messages = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", mids)}), new Object[0]);
            while (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    if (message.media != null) {
                        File file;
                        if (message.media.document != null) {
                            file = FileLoader.getPathToAttach(message.media.document, true);
                            if (file != null && file.toString().length() > 0) {
                                filesToDelete.add(file);
                            }
                            file = FileLoader.getPathToAttach(message.media.document.thumb, true);
                            if (file != null && file.toString().length() > 0) {
                                filesToDelete.add(file);
                            }
                            message.media.document = new TL_documentEmpty();
                        } else if (message.media.photo != null) {
                            Iterator it = message.media.photo.sizes.iterator();
                            while (it.hasNext()) {
                                file = FileLoader.getPathToAttach((PhotoSize) it.next(), true);
                                if (file != null && file.toString().length() > 0) {
                                    filesToDelete.add(file);
                                }
                            }
                            message.media.photo = new TL_photoEmpty();
                        }
                        message.media.flags &= -2;
                        message.f104id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = cursor.longValue(3);
                        messages.add(message);
                    }
                }
            }
            cursor.dispose();
            if (!messages.isEmpty()) {
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                for (int a = 0; a < messages.size(); a++) {
                    message = (Message) messages.get(a);
                    data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.requery();
                    state.bindLong(1, (long) message.f104id);
                    state.bindLong(2, message.dialog_id);
                    state.bindInteger(3, MessageObject.getUnreadFlags(message));
                    state.bindInteger(4, message.send_state);
                    state.bindInteger(5, message.date);
                    state.bindByteBuffer(6, data);
                    state.bindInteger(7, MessageObject.isOut(message) ? 1 : 0);
                    state.bindInteger(8, message.ttl);
                    if ((message.flags & 1024) != 0) {
                        state.bindInteger(9, message.views);
                    } else {
                        state.bindInteger(9, getMessageMediaType(message));
                    }
                    state.bindInteger(10, 0);
                    state.bindInteger(11, message.mentioned ? 1 : 0);
                    state.step();
                    data.reuse();
                }
                state.dispose();
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$116(this, messages));
            }
            FileLoader.getInstance(this.currentAccount).deleteFiles(filesToDelete, 0);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$45$MessagesStorage(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, messages.get(a));
        }
    }

    public void getNewTask(ArrayList<Integer> oldTask, int channelId) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$30(this, oldTask));
    }

    final /* synthetic */ void lambda$getNewTask$47$MessagesStorage(ArrayList oldTask) {
        if (oldTask != null) {
            try {
                String ids = TextUtils.join(",", oldTask);
                this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[]{ids})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        int date = 0;
        int channelId1 = -1;
        ArrayList<Integer> arr = null;
        SQLiteCursor cursor = this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
        while (cursor.next()) {
            long mid = cursor.longValue(0);
            if (channelId1 == -1) {
                channelId1 = (int) (mid >> 32);
                if (channelId1 < 0) {
                    channelId1 = 0;
                }
            }
            date = cursor.intValue(1);
            if (arr == null) {
                arr = new ArrayList();
            }
            arr.add(Integer.valueOf((int) mid));
        }
        cursor.dispose();
        MessagesController.getInstance(this.currentAccount).processLoadedDeleteTask(date, arr, channelId1);
    }

    public void markMentionMessageAsRead(int messageId, int channelId, long did) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$31(this, messageId, channelId, did));
    }

    final /* synthetic */ void lambda$markMentionMessageAsRead$48$MessagesStorage(int messageId, int channelId, long did) {
        long mid = (long) messageId;
        if (channelId != 0) {
            mid |= ((long) channelId) << 32;
        }
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid = %d", new Object[]{Long.valueOf(mid)})).stepThis().dispose();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
            int old_mentions_count = 0;
            if (cursor.next()) {
                old_mentions_count = Math.max(0, cursor.intValue(0) - 1);
            }
            cursor.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(old_mentions_count), Long.valueOf(did)})).stepThis().dispose();
            LongSparseArray<Integer> sparseArray = new LongSparseArray(1);
            sparseArray.put(did, Integer.valueOf(old_mentions_count));
            MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(null, sparseArray);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void markMessageAsMention(long mid) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$32(this, mid));
    }

    final /* synthetic */ void lambda$markMessageAsMention$49$MessagesStorage(long mid) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[]{Long.valueOf(mid)})).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void resetMentionsCount(long did, int count) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$33(this, count, did));
    }

    final /* synthetic */ void lambda$resetMentionsCount$50$MessagesStorage(int count, long did) {
        if (count == 0) {
            try {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(did)})).stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(count), Long.valueOf(did)})).stepThis().dispose();
        LongSparseArray<Integer> sparseArray = new LongSparseArray(1);
        sparseArray.put(did, Integer.valueOf(count));
        MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(null, sparseArray);
    }

    public void createTaskForMid(int messageId, int channelId, int time, int readTime, int ttl, boolean inner) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$34(this, time, readTime, ttl, messageId, channelId, inner));
    }

    final /* synthetic */ void lambda$createTaskForMid$52$MessagesStorage(int time, int readTime, int ttl, int messageId, int channelId, boolean inner) {
        if (time <= readTime) {
            time = readTime;
        }
        int minDate = time + ttl;
        try {
            SparseArray<ArrayList<Long>> messages = new SparseArray();
            ArrayList<Long> midsArray = new ArrayList();
            long mid = (long) messageId;
            if (channelId != 0) {
                mid |= ((long) channelId) << 32;
            }
            midsArray.add(Long.valueOf(mid));
            messages.put(minDate, midsArray);
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$115(this, inner, midsArray));
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            for (int a = 0; a < messages.size(); a++) {
                int key = messages.keyAt(a);
                ArrayList<Long> arr = (ArrayList) messages.get(key);
                for (int b = 0; b < arr.size(); b++) {
                    state.requery();
                    state.bindLong(1, ((Long) arr.get(b)).longValue());
                    state.bindInteger(2, key);
                    state.step();
                }
            }
            state.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid = %d", new Object[]{Long.valueOf(mid)})).stepThis().dispose();
            MessagesController.getInstance(this.currentAccount).didAddedNewTask(minDate, messages);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$51$MessagesStorage(boolean inner, ArrayList midsArray) {
        if (!inner) {
            markMessagesContentAsRead(midsArray, 0);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, midsArray);
    }

    public void createTaskForSecretChat(int chatId, int time, int readTime, int isOut, ArrayList<Long> random_ids) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$35(this, random_ids, chatId, isOut, time, readTime));
    }

    final /* synthetic */ void lambda$createTaskForSecretChat$54$MessagesStorage(ArrayList random_ids, int chatId, int isOut, int time, int readTime) {
        int minDate = ConnectionsManager.DEFAULT_DATACENTER_ID;
        try {
            SQLiteCursor cursor;
            ArrayList<Long> arr;
            SparseArray<ArrayList<Long>> messages = new SparseArray();
            ArrayList<Long> midsArray = new ArrayList();
            StringBuilder mids = new StringBuilder();
            if (random_ids == null) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[]{Long.valueOf(((long) chatId) << 32), Integer.valueOf(isOut), Integer.valueOf(time)}), new Object[0]);
            } else {
                String ids = TextUtils.join(",", random_ids);
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[]{ids}), new Object[0]);
            }
            while (cursor.next()) {
                int ttl = cursor.intValue(1);
                long mid = (long) cursor.intValue(0);
                if (random_ids != null) {
                    midsArray.add(Long.valueOf(mid));
                }
                if (ttl > 0) {
                    int i;
                    if (time > readTime) {
                        i = time;
                    } else {
                        i = readTime;
                    }
                    int date = i + ttl;
                    minDate = Math.min(minDate, date);
                    arr = (ArrayList) messages.get(date);
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(date, arr);
                    }
                    if (mids.length() != 0) {
                        mids.append(",");
                    }
                    mids.append(mid);
                    arr.add(Long.valueOf(mid));
                }
            }
            cursor.dispose();
            if (random_ids != null) {
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$114(this, midsArray));
            }
            if (messages.size() != 0) {
                this.database.beginTransaction();
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                for (int a = 0; a < messages.size(); a++) {
                    int key = messages.keyAt(a);
                    arr = (ArrayList) messages.get(key);
                    for (int b = 0; b < arr.size(); b++) {
                        state.requery();
                        state.bindLong(1, ((Long) arr.get(b)).longValue());
                        state.bindInteger(2, key);
                        state.step();
                    }
                }
                state.dispose();
                this.database.commitTransaction();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[]{mids.toString()})).stepThis().dispose();
                MessagesController.getInstance(this.currentAccount).didAddedNewTask(minDate, messages);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$53$MessagesStorage(ArrayList midsArray) {
        markMessagesContentAsRead(midsArray, 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, midsArray);
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> messages, SparseLongArray inbox, SparseLongArray outbox, ArrayList<Long> mentions) {
        try {
            SQLitePreparedStatement state;
            int a;
            LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray();
            LongSparseArray<Integer> dialogsToUpdateMentions = new LongSparseArray();
            ArrayList<Integer> channelMentionsToReload = new ArrayList();
            SQLiteCursor cursor;
            String ids;
            if (isEmpty((List) messages)) {
                int b;
                int key;
                long messageId;
                if (!isEmpty(inbox)) {
                    for (b = 0; b < inbox.size(); b++) {
                        key = inbox.keyAt(b);
                        messageId = inbox.get(key);
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(key), Long.valueOf(messageId)}), new Object[0]);
                        if (cursor.next()) {
                            dialogsToUpdate.put((long) key, Integer.valueOf(cursor.intValue(0)));
                        }
                        cursor.dispose();
                        state = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        state.requery();
                        state.bindLong(1, (long) key);
                        state.bindInteger(2, (int) messageId);
                        state.bindLong(3, (long) key);
                        state.step();
                        state.dispose();
                    }
                }
                if (!isEmpty((List) mentions)) {
                    ArrayList<Long> arrayList = new ArrayList(mentions);
                    ids = TextUtils.join(",", mentions);
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", new Object[]{ids}), new Object[0]);
                    while (cursor.next()) {
                        long did = cursor.longValue(0);
                        arrayList.remove(Long.valueOf(cursor.longValue(4)));
                        if (cursor.intValue(1) < 2 && cursor.intValue(2) == 0 && cursor.intValue(3) == 1) {
                            Integer unread_count = (Integer) dialogsToUpdateMentions.get(did);
                            if (unread_count == null) {
                                SQLiteCursor cursor2 = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
                                int old_mentions_count = 0;
                                if (cursor2.next()) {
                                    old_mentions_count = cursor2.intValue(0);
                                }
                                cursor2.dispose();
                                dialogsToUpdateMentions.put(did, Integer.valueOf(Math.max(0, old_mentions_count - 1)));
                            } else {
                                dialogsToUpdateMentions.put(did, Integer.valueOf(Math.max(0, unread_count.intValue() - 1)));
                            }
                        }
                    }
                    cursor.dispose();
                    for (a = 0; a < arrayList.size(); a++) {
                        int channelId = (int) (((Long) arrayList.get(a)).longValue() >> 32);
                        if (channelId > 0 && !channelMentionsToReload.contains(Integer.valueOf(channelId))) {
                            channelMentionsToReload.add(Integer.valueOf(channelId));
                        }
                    }
                }
                if (!isEmpty(outbox)) {
                    for (b = 0; b < outbox.size(); b++) {
                        key = outbox.keyAt(b);
                        messageId = outbox.get(key);
                        state = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        state.requery();
                        state.bindLong(1, (long) key);
                        state.bindInteger(2, (int) messageId);
                        state.bindLong(3, (long) key);
                        state.step();
                        state.dispose();
                    }
                }
            } else {
                ids = TextUtils.join(",", messages);
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[]{ids}), new Object[0]);
                while (cursor.next()) {
                    if (cursor.intValue(2) == 0 && cursor.intValue(1) == 0) {
                        long uid = cursor.longValue(0);
                        Integer currentCount = (Integer) dialogsToUpdate.get(uid);
                        if (currentCount == null) {
                            dialogsToUpdate.put(uid, Integer.valueOf(1));
                        } else {
                            dialogsToUpdate.put(uid, Integer.valueOf(currentCount.intValue() + 1));
                        }
                    }
                }
                cursor.dispose();
            }
            if (dialogsToUpdate.size() > 0 || dialogsToUpdateMentions.size() > 0) {
                this.database.beginTransaction();
                if (dialogsToUpdate.size() > 0) {
                    state = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                    for (a = 0; a < dialogsToUpdate.size(); a++) {
                        state.requery();
                        state.bindInteger(1, ((Integer) dialogsToUpdate.valueAt(a)).intValue());
                        state.bindLong(2, dialogsToUpdate.keyAt(a));
                        state.step();
                    }
                    state.dispose();
                }
                if (dialogsToUpdateMentions.size() > 0) {
                    state = this.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
                    for (a = 0; a < dialogsToUpdateMentions.size(); a++) {
                        state.requery();
                        state.bindInteger(1, ((Integer) dialogsToUpdateMentions.valueAt(a)).intValue());
                        state.bindLong(2, dialogsToUpdateMentions.keyAt(a));
                        state.step();
                    }
                    state.dispose();
                }
                this.database.commitTransaction();
            }
            MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate, dialogsToUpdateMentions);
            if (!channelMentionsToReload.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).reloadMentionsCountForChannels(channelMentionsToReload);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private static boolean isEmpty(SparseArray<?> array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(SparseLongArray array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(List<?> array) {
        return array == null || array.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray array) {
        return array == null || array.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> array) {
        return array == null || array.size() == 0;
    }

    public void updateDialogsWithReadMessages(SparseLongArray inbox, SparseLongArray outbox, ArrayList<Long> mentions, boolean useQueue) {
        if (!isEmpty(inbox) || !isEmpty((List) mentions)) {
            if (useQueue) {
                this.storageQueue.postRunnable(new MessagesStorage$$Lambda$36(this, inbox, outbox, mentions));
            } else {
                updateDialogsWithReadMessagesInternal(null, inbox, outbox, mentions);
            }
        }
    }

    final /* synthetic */ void lambda$updateDialogsWithReadMessages$55$MessagesStorage(SparseLongArray inbox, SparseLongArray outbox, ArrayList mentions) {
        updateDialogsWithReadMessagesInternal(null, inbox, outbox, mentions);
    }

    public void updateChatParticipants(ChatParticipants participants) {
        if (participants != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$37(this, participants));
        }
    }

    final /* synthetic */ void lambda$updateChatParticipants$57$MessagesStorage(ChatParticipants participants) {
        try {
            NativeByteBuffer data;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + participants.chat_id, new Object[0]);
            ChatFull info = null;
            ArrayList<User> loadedUsers = new ArrayList();
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            cursor.dispose();
            if (info instanceof TL_chatFull) {
                info.participants = participants;
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$113(this, info));
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                data = new NativeByteBuffer(info.getObjectSize());
                info.serializeToStream(data);
                state.bindInteger(1, info.f79id);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, info.pinned_msg_id);
                state.step();
                state.dispose();
                data.reuse();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$56$MessagesStorage(ChatFull finalInfo) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void loadChannelAdmins(int chatId) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$38(this, chatId));
    }

    final /* synthetic */ void lambda$loadChannelAdmins$58$MessagesStorage(int chatId) {
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT uid FROM channel_admins WHERE did = " + chatId, new Object[0]);
            ArrayList<Integer> ids = new ArrayList();
            while (cursor.next()) {
                ids.add(Integer.valueOf(cursor.intValue(0)));
            }
            cursor.dispose();
            MessagesController.getInstance(this.currentAccount).processLoadedChannelAdmins(ids, chatId, true);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putChannelAdmins(int chatId, ArrayList<Integer> ids) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$39(this, chatId, ids));
    }

    final /* synthetic */ void lambda$putChannelAdmins$59$MessagesStorage(int chatId, ArrayList ids) {
        try {
            this.database.executeFast("DELETE FROM channel_admins WHERE did = " + chatId).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO channel_admins VALUES(?, ?)");
            int date = (int) (System.currentTimeMillis() / 1000);
            for (int a = 0; a < ids.size(); a++) {
                state.requery();
                state.bindInteger(1, chatId);
                state.bindInteger(2, ((Integer) ids.get(a)).intValue());
                state.step();
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void updateChannelUsers(int channel_id, ArrayList<ChannelParticipant> participants) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$40(this, channel_id, participants));
    }

    final /* synthetic */ void lambda$updateChannelUsers$60$MessagesStorage(int channel_id, ArrayList participants) {
        long did = (long) (-channel_id);
        try {
            this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + did).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            int date = (int) (System.currentTimeMillis() / 1000);
            for (int a = 0; a < participants.size(); a++) {
                ChannelParticipant participant = (ChannelParticipant) participants.get(a);
                state.requery();
                state.bindLong(1, did);
                state.bindInteger(2, participant.user_id);
                state.bindInteger(3, date);
                NativeByteBuffer data = new NativeByteBuffer(participant.getObjectSize());
                participant.serializeToStream(data);
                state.bindByteBuffer(4, data);
                data.reuse();
                state.step();
                date--;
            }
            state.dispose();
            this.database.commitTransaction();
            loadChatInfo(channel_id, null, false, true);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void saveBotCache(String key, TLObject result) {
        if (result != null && !TextUtils.isEmpty(key)) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$41(this, result, key));
        }
    }

    final /* synthetic */ void lambda$saveBotCache$61$MessagesStorage(TLObject result, String key) {
        try {
            int currentDate = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (result instanceof TL_messages_botCallbackAnswer) {
                currentDate += ((TL_messages_botCallbackAnswer) result).cache_time;
            } else if (result instanceof TL_messages_botResults) {
                currentDate += ((TL_messages_botResults) result).cache_time;
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(result.getObjectSize());
            result.serializeToStream(data);
            state.bindString(1, key);
            state.bindInteger(2, currentDate);
            state.bindByteBuffer(3, data);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void getBotCache(String key, RequestDelegate requestDelegate) {
        if (key != null && requestDelegate != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$42(this, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), key, requestDelegate));
        }
    }

    final /* synthetic */ void lambda$getBotCache$62$MessagesStorage(int currentDate, String key, RequestDelegate requestDelegate) {
        TLObject result = null;
        try {
            this.database.executeFast("DELETE FROM botcache WHERE date < " + currentDate).stepThis().dispose();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT data FROM botcache WHERE id = ?", key);
            if (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        int constructor = data.readInt32(false);
                        if (constructor == TL_messages_botCallbackAnswer.constructor) {
                            result = TL_messages_botCallbackAnswer.TLdeserialize(data, constructor, false);
                        } else {
                            result = messages_BotResults.TLdeserialize(data, constructor, false);
                        }
                        data.reuse();
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        } finally {
            requestDelegate.run(result, null);
        }
    }

    public void loadUserInfo(User user, CountDownLatch countDownLatch, boolean force) {
        if (user != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$43(this, user, countDownLatch, force));
        }
    }

    final /* synthetic */ void lambda$loadUserInfo$63$MessagesStorage(User user, CountDownLatch countDownLatch, boolean force) {
        TL_userFull info = null;
        MessageObject pinnedMessageObject;
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM user_settings WHERE uid = " + user.f176id, new Object[0]);
            if (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = TL_userFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            cursor.dispose();
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            if (info == null || info.pinned_msg_id == 0) {
                pinnedMessageObject = null;
            } else {
                pinnedMessageObject = DataQuery.getInstance(this.currentAccount).loadPinnedMessage((long) user.f176id, 0, info.pinned_msg_id, false);
            }
            MessagesController.getInstance(this.currentAccount).processUserInfo(user, info, true, force, pinnedMessageObject);
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            MessagesController.getInstance(this.currentAccount).processUserInfo(user, null, true, force, null);
            if (countDownLatch != null) {
                countDownLatch.countDown();
                pinnedMessageObject = null;
                return;
            }
            pinnedMessageObject = null;
        } catch (Throwable th) {
            MessagesController.getInstance(this.currentAccount).processUserInfo(user, null, true, force, null);
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    public void updateUserInfo(TL_userFull info, boolean ifExist) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$44(this, ifExist, info));
    }

    final /* synthetic */ void lambda$updateUserInfo$64$MessagesStorage(boolean ifExist, TL_userFull info) {
        if (ifExist) {
            try {
                SQLiteCursor cursor = this.database.queryFinalized("SELECT uid FROM user_settings WHERE uid = " + info.user.f176id, new Object[0]);
                boolean exist = cursor.next();
                cursor.dispose();
                if (!exist) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
        NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
        info.serializeToStream(data);
        state.bindInteger(1, info.user.f176id);
        state.bindByteBuffer(2, data);
        state.bindInteger(3, info.pinned_msg_id);
        state.step();
        state.dispose();
        data.reuse();
    }

    public void updateChatInfo(ChatFull info, boolean ifExist) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$45(this, ifExist, info));
    }

    final /* synthetic */ void lambda$updateChatInfo$65$MessagesStorage(boolean ifExist, ChatFull info) {
        SQLiteCursor cursor;
        if (ifExist) {
            try {
                cursor = this.database.queryFinalized("SELECT uid FROM chat_settings_v2 WHERE uid = " + info.f79id, new Object[0]);
                boolean exist = cursor.next();
                cursor.dispose();
                if (!exist) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
        NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
        info.serializeToStream(data);
        state.bindInteger(1, info.f79id);
        state.bindByteBuffer(2, data);
        state.bindInteger(3, info.pinned_msg_id);
        state.step();
        state.dispose();
        data.reuse();
        if (info instanceof TL_channelFull) {
            cursor = this.database.queryFinalized("SELECT date, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i, flags FROM dialogs WHERE did = " + (-info.f79id), new Object[0]);
            if (cursor.next() && cursor.intValue(3) < info.read_inbox_max_id) {
                int dialog_date = cursor.intValue(0);
                int pts = cursor.intValue(1);
                long last_mid = cursor.longValue(2);
                int outbox_max = cursor.intValue(4);
                int pinned = cursor.intValue(5);
                int mentions = cursor.intValue(6);
                int flags = cursor.intValue(7);
                state = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                state.bindLong(1, (long) (-info.f79id));
                state.bindInteger(2, dialog_date);
                state.bindInteger(3, info.unread_count);
                state.bindLong(4, last_mid);
                state.bindInteger(5, info.read_inbox_max_id);
                state.bindInteger(6, Math.max(outbox_max, info.read_outbox_max_id));
                state.bindLong(7, 0);
                state.bindInteger(8, mentions);
                state.bindInteger(9, pts);
                state.bindInteger(10, 0);
                state.bindInteger(11, pinned);
                state.bindInteger(12, flags);
                state.step();
                state.dispose();
            }
            cursor.dispose();
        }
    }

    public void updateUserPinnedMessage(int userId, int messageId) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$46(this, userId, messageId));
    }

    final /* synthetic */ void lambda$updateUserPinnedMessage$67$MessagesStorage(int userId, int messageId) {
        try {
            NativeByteBuffer data;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM user_settings WHERE uid = " + userId, new Object[0]);
            TL_userFull info = null;
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = TL_userFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            cursor.dispose();
            if (info instanceof TL_userFull) {
                info.pinned_msg_id = messageId;
                info.flags |= 64;
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$112(this, userId, info));
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
                data = new NativeByteBuffer(info.getObjectSize());
                info.serializeToStream(data);
                state.bindInteger(1, userId);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, info.pinned_msg_id);
                state.step();
                state.dispose();
                data.reuse();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$66$MessagesStorage(int userId, TL_userFull finalInfo) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(userId), finalInfo, null);
    }

    public void updateChatPinnedMessage(int channelId, int messageId) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$47(this, channelId, messageId));
    }

    final /* synthetic */ void lambda$updateChatPinnedMessage$69$MessagesStorage(int channelId, int messageId) {
        try {
            NativeByteBuffer data;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + channelId, new Object[0]);
            ChatFull info = null;
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            cursor.dispose();
            if (info != null) {
                if (info instanceof TL_channelFull) {
                    info.pinned_msg_id = messageId;
                    info.flags |= 32;
                } else if (info instanceof TL_chatFull) {
                    info.pinned_msg_id = messageId;
                    info.flags |= 64;
                }
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$111(this, info));
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                data = new NativeByteBuffer(info.getObjectSize());
                info.serializeToStream(data);
                state.bindInteger(1, channelId);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, info.pinned_msg_id);
                state.step();
                state.dispose();
                data.reuse();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$68$MessagesStorage(ChatFull finalInfo) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChatInfo(int chat_id, int user_id, int what, int invited_id, int version) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$48(this, chat_id, what, user_id, invited_id, version));
    }

    final /* synthetic */ void lambda$updateChatInfo$71$MessagesStorage(int chat_id, int what, int user_id, int invited_id, int version) {
        try {
            NativeByteBuffer data;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + chat_id, new Object[0]);
            ChatFull info = null;
            ArrayList<User> loadedUsers = new ArrayList();
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            cursor.dispose();
            if (info instanceof TL_chatFull) {
                int a;
                if (what == 1) {
                    for (a = 0; a < info.participants.participants.size(); a++) {
                        if (((ChatParticipant) info.participants.participants.get(a)).user_id == user_id) {
                            info.participants.participants.remove(a);
                            break;
                        }
                    }
                } else if (what == 0) {
                    Iterator it = info.participants.participants.iterator();
                    while (it.hasNext()) {
                        if (((ChatParticipant) it.next()).user_id == user_id) {
                            return;
                        }
                    }
                    TL_chatParticipant participant = new TL_chatParticipant();
                    participant.user_id = user_id;
                    participant.inviter_id = invited_id;
                    participant.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    info.participants.participants.add(participant);
                } else if (what == 2) {
                    a = 0;
                    while (a < info.participants.participants.size()) {
                        ChatParticipant participant2 = (ChatParticipant) info.participants.participants.get(a);
                        if (participant2.user_id == user_id) {
                            ChatParticipant newParticipant;
                            if (invited_id == 1) {
                                newParticipant = new TL_chatParticipantAdmin();
                                newParticipant.user_id = participant2.user_id;
                                newParticipant.date = participant2.date;
                                newParticipant.inviter_id = participant2.inviter_id;
                            } else {
                                newParticipant = new TL_chatParticipant();
                                newParticipant.user_id = participant2.user_id;
                                newParticipant.date = participant2.date;
                                newParticipant.inviter_id = participant2.inviter_id;
                            }
                            info.participants.participants.set(a, newParticipant);
                        } else {
                            a++;
                        }
                    }
                }
                info.participants.version = version;
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$110(this, info));
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
                data = new NativeByteBuffer(info.getObjectSize());
                info.serializeToStream(data);
                state.bindInteger(1, chat_id);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, info.pinned_msg_id);
                state.step();
                state.dispose();
                data.reuse();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$70$MessagesStorage(ChatFull finalInfo) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public boolean isMigratedChat(int chat_id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$49(this, chat_id, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$isMigratedChat$72$MessagesStorage(int chat_id, boolean[] result, CountDownLatch countDownLatch) {
        boolean z = false;
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + chat_id, new Object[0]);
            ChatFull info = null;
            ArrayList<User> loadedUsers = new ArrayList();
            if (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                }
            }
            cursor.dispose();
            if ((info instanceof TL_channelFull) && info.migrated_from_chat_id != 0) {
                z = true;
            }
            result[0] = z;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        } catch (Throwable th) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            throw th;
        }
    }

    public void loadChatInfo(int chat_id, CountDownLatch countDownLatch, boolean force, boolean byChannelUsers) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$50(this, chat_id, countDownLatch, force, byChannelUsers));
    }

    /* JADX WARNING: Removed duplicated region for block: B:76:0x0205 A:{ExcHandler: all (r2_51 'th' java.lang.Throwable), Splitter: B:8:0x004b} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:58:0x019c, code:
            r16 = e;
     */
    /* JADX WARNING: Missing block: B:59:0x019d, code:
            r4 = r17;
     */
    /* JADX WARNING: Missing block: B:76:0x0205, code:
            r2 = move-exception;
     */
    /* JADX WARNING: Missing block: B:77:0x0206, code:
            r22 = r2;
            r4 = r17;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$loadChatInfo$73$MessagesStorage(int chat_id, CountDownLatch countDownLatch, boolean force, boolean byChannelUsers) {
        Throwable e;
        MessageObject pinnedMessageObject = null;
        ChatFull info = null;
        ArrayList<User> loadedUsers = new ArrayList();
        try {
            NativeByteBuffer data;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + chat_id, new Object[0]);
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    info = ChatFull.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    info.pinned_msg_id = cursor.intValue(1);
                }
            }
            ChatFull info2 = info;
            try {
                cursor.dispose();
                StringBuilder usersToLoad;
                int a;
                if (info2 instanceof TL_chatFull) {
                    usersToLoad = new StringBuilder();
                    for (a = 0; a < info2.participants.participants.size(); a++) {
                        ChatParticipant c = (ChatParticipant) info2.participants.participants.get(a);
                        if (usersToLoad.length() != 0) {
                            usersToLoad.append(",");
                        }
                        usersToLoad.append(c.user_id);
                    }
                    if (usersToLoad.length() != 0) {
                        getUsersInternal(usersToLoad.toString(), loadedUsers);
                    }
                } else if (info2 instanceof TL_channelFull) {
                    cursor = this.database.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + (-chat_id) + " ORDER BY cu.date DESC", new Object[0]);
                    info2.participants = new TL_chatParticipants();
                    while (cursor.next()) {
                        User user = null;
                        ChannelParticipant participant = null;
                        data = cursor.byteBufferValue(0);
                        if (data != null) {
                            user = User.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                        }
                        data = cursor.byteBufferValue(2);
                        if (data != null) {
                            participant = ChannelParticipant.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                        }
                        if (!(user == null || participant == null)) {
                            if (user.status != null) {
                                user.status.expires = cursor.intValue(1);
                            }
                            loadedUsers.add(user);
                            participant.date = cursor.intValue(3);
                            TL_chatChannelParticipant chatChannelParticipant = new TL_chatChannelParticipant();
                            chatChannelParticipant.user_id = participant.user_id;
                            chatChannelParticipant.date = participant.date;
                            chatChannelParticipant.inviter_id = participant.inviter_id;
                            chatChannelParticipant.channelParticipant = participant;
                            info2.participants.participants.add(chatChannelParticipant);
                        }
                    }
                    cursor.dispose();
                    usersToLoad = new StringBuilder();
                    for (a = 0; a < info2.bot_info.size(); a++) {
                        BotInfo botInfo = (BotInfo) info2.bot_info.get(a);
                        if (usersToLoad.length() != 0) {
                            usersToLoad.append(",");
                        }
                        usersToLoad.append(botInfo.user_id);
                    }
                    if (usersToLoad.length() != 0) {
                        getUsersInternal(usersToLoad.toString(), loadedUsers);
                    }
                }
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                if (!(info2 == null || info2.pinned_msg_id == 0)) {
                    pinnedMessageObject = DataQuery.getInstance(this.currentAccount).loadPinnedMessage((long) (-chat_id), info2 instanceof TL_channelFull ? chat_id : 0, info2.pinned_msg_id, false);
                }
                MessagesController.getInstance(this.currentAccount).processChatInfo(chat_id, info2, loadedUsers, true, force, byChannelUsers, pinnedMessageObject);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                    info = info2;
                    return;
                }
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            } catch (Throwable th) {
            }
        } catch (Exception e3) {
            e2 = e3;
            try {
                FileLog.m13e(e2);
                MessagesController.getInstance(this.currentAccount).processChatInfo(chat_id, info, loadedUsers, true, force, byChannelUsers, null);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            } catch (Throwable th2) {
                Throwable th3 = th2;
                MessagesController.getInstance(this.currentAccount).processChatInfo(chat_id, info, loadedUsers, true, force, byChannelUsers, null);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                throw th3;
            }
        }
    }

    public void processPendingRead(long dialog_id, long maxPositiveId, long maxNegativeId, int max_date, boolean isChannel) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$51(this, dialog_id, maxPositiveId, isChannel, maxNegativeId));
    }

    final /* synthetic */ void lambda$processPendingRead$74$MessagesStorage(long dialog_id, long maxPositiveId, boolean isChannel, long maxNegativeId) {
        long currentMaxId = 0;
        int unreadCount = 0;
        long last_mid = 0;
        try {
            SQLitePreparedStatement state;
            SQLiteCursor cursor = this.database.queryFinalized("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = " + dialog_id, new Object[0]);
            if (cursor.next()) {
                unreadCount = cursor.intValue(0);
                currentMaxId = (long) cursor.intValue(1);
                last_mid = cursor.longValue(2);
            }
            cursor.dispose();
            this.database.beginTransaction();
            int lower_id = (int) dialog_id;
            int updatedCount;
            if (lower_id != 0) {
                currentMaxId = Math.max(currentMaxId, (long) ((int) maxPositiveId));
                if (isChannel) {
                    currentMaxId |= ((long) (-lower_id)) << 32;
                }
                state = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                state.requery();
                state.bindLong(1, dialog_id);
                state.bindLong(2, currentMaxId);
                state.step();
                state.dispose();
                if (currentMaxId >= last_mid) {
                    unreadCount = 0;
                } else {
                    updatedCount = 0;
                    cursor = this.database.queryFinalized("SELECT changes()", new Object[0]);
                    if (cursor.next()) {
                        updatedCount = cursor.intValue(0);
                    }
                    cursor.dispose();
                    unreadCount = Math.max(0, unreadCount - updatedCount);
                }
                state = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                state.requery();
                state.bindLong(1, dialog_id);
                state.bindLong(2, currentMaxId);
                state.step();
                state.dispose();
            } else {
                currentMaxId = (long) ((int) maxNegativeId);
                state = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                state.requery();
                state.bindLong(1, dialog_id);
                state.bindLong(2, currentMaxId);
                state.step();
                state.dispose();
                if (currentMaxId <= last_mid) {
                    unreadCount = 0;
                } else {
                    updatedCount = 0;
                    cursor = this.database.queryFinalized("SELECT changes()", new Object[0]);
                    if (cursor.next()) {
                        updatedCount = cursor.intValue(0);
                    }
                    cursor.dispose();
                    unreadCount = Math.max(0, unreadCount - updatedCount);
                }
                state = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid >= ?");
                state.requery();
                state.bindLong(1, dialog_id);
                state.bindLong(2, currentMaxId);
                state.step();
                state.dispose();
            }
            state = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
            state.requery();
            state.bindInteger(1, unreadCount);
            state.bindInteger(2, (int) currentMaxId);
            state.bindLong(3, dialog_id);
            state.step();
            state.dispose();
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putContacts(ArrayList<TL_contact> contacts, boolean deleteAll) {
        if (!contacts.isEmpty() || deleteAll) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$52(this, deleteAll, new ArrayList(contacts)));
        }
    }

    final /* synthetic */ void lambda$putContacts$75$MessagesStorage(boolean deleteAll, ArrayList contactsCopy) {
        if (deleteAll) {
            try {
                this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
        for (int a = 0; a < contactsCopy.size(); a++) {
            TL_contact contact = (TL_contact) contactsCopy.get(a);
            state.requery();
            state.bindInteger(1, contact.user_id);
            state.bindInteger(2, contact.mutual ? 1 : 0);
            state.step();
        }
        state.dispose();
        this.database.commitTransaction();
    }

    public void deleteContacts(ArrayList<Integer> uids) {
        if (uids != null && !uids.isEmpty()) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$53(this, uids));
        }
    }

    final /* synthetic */ void lambda$deleteContacts$76$MessagesStorage(ArrayList uids) {
        try {
            this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + TextUtils.join(",", uids) + ")").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void applyPhoneBookUpdates(String adds, String deletes) {
        if (adds.length() != 0 || deletes.length() != 0) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$54(this, adds, deletes));
        }
    }

    final /* synthetic */ void lambda$applyPhoneBookUpdates$77$MessagesStorage(String adds, String deletes) {
        try {
            if (adds.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", new Object[]{adds})).stepThis().dispose();
            }
            if (deletes.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", new Object[]{deletes})).stepThis().dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putCachedPhoneBook(HashMap<String, Contact> contactHashMap, boolean migrate, boolean delete) {
        if (contactHashMap == null) {
            return;
        }
        if (!contactHashMap.isEmpty() || migrate || delete) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$55(this, contactHashMap, migrate));
        }
    }

    final /* synthetic */ void lambda$putCachedPhoneBook$78$MessagesStorage(HashMap contactHashMap, boolean migrate) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d(this.currentAccount + " save contacts to db " + contactHashMap.size());
            }
            this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
            for (Entry<String, Contact> entry : contactHashMap.entrySet()) {
                Contact contact = (Contact) entry.getValue();
                if (!(contact.phones.isEmpty() || contact.shortPhones.isEmpty())) {
                    state.requery();
                    state.bindString(1, contact.key);
                    state.bindInteger(2, contact.contact_id);
                    state.bindString(3, contact.first_name);
                    state.bindString(4, contact.last_name);
                    state.bindInteger(5, contact.imported);
                    state.step();
                    for (int a = 0; a < contact.phones.size(); a++) {
                        state2.requery();
                        state2.bindString(1, contact.key);
                        state2.bindString(2, (String) contact.phones.get(a));
                        state2.bindString(3, (String) contact.shortPhones.get(a));
                        state2.bindInteger(4, ((Integer) contact.phoneDeleted.get(a)).intValue());
                        state2.step();
                    }
                }
            }
            state.dispose();
            state2.dispose();
            this.database.commitTransaction();
            if (migrate) {
                this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
                this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
                getCachedPhoneBook(false);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void getCachedPhoneBook(boolean byError) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$56(this, byError));
    }

    final /* synthetic */ void lambda$getCachedPhoneBook$79$MessagesStorage(boolean byError) {
        SQLiteCursor cursor = null;
        try {
            cursor = this.database.queryFinalized("SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'", new Object[0]);
            boolean migrate = cursor.next();
            cursor.dispose();
            cursor = null;
            int count;
            Contact contact;
            String phone;
            String sphone;
            if (migrate) {
                count = 16;
                cursor = this.database.queryFinalized("SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1", new Object[0]);
                if (cursor.next()) {
                    count = Math.min(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, cursor.intValue(0));
                }
                cursor.dispose();
                SparseArray<Contact> contactHashMap = new SparseArray(count);
                cursor = this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
                while (cursor.next()) {
                    int uid = cursor.intValue(0);
                    contact = (Contact) contactHashMap.get(uid);
                    if (contact == null) {
                        contact = new Contact();
                        contact.first_name = cursor.stringValue(1);
                        contact.last_name = cursor.stringValue(2);
                        contact.imported = cursor.intValue(6);
                        if (contact.first_name == null) {
                            contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (contact.last_name == null) {
                            contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        contact.contact_id = uid;
                        contactHashMap.put(uid, contact);
                    }
                    phone = cursor.stringValue(3);
                    if (phone != null) {
                        contact.phones.add(phone);
                        sphone = cursor.stringValue(4);
                        if (sphone == null) {
                            continue;
                        } else {
                            if (sphone.length() == 8 && phone.length() != 8) {
                                sphone = C0194PhoneFormat.stripExceptNumbers(phone);
                            }
                            contact.shortPhones.add(sphone);
                            contact.phoneDeleted.add(Integer.valueOf(cursor.intValue(5)));
                            contact.phoneTypes.add(TtmlNode.ANONYMOUS_REGION_ID);
                            if (contactHashMap.size() == DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS) {
                                break;
                            }
                        }
                    }
                }
                cursor.dispose();
                cursor = null;
                ContactsController.getInstance(this.currentAccount).migratePhoneBookToV7(contactHashMap);
                if (cursor != null) {
                    cursor.dispose();
                    return;
                }
                return;
            }
            boolean z;
            if (cursor != null) {
                cursor.dispose();
            }
            count = 16;
            int currentContactsCount = 0;
            int start = 0;
            try {
                cursor = this.database.queryFinalized("SELECT COUNT(key) FROM user_contacts_v7 WHERE 1", new Object[0]);
                if (cursor.next()) {
                    currentContactsCount = cursor.intValue(0);
                    count = Math.min(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS, currentContactsCount);
                    if (currentContactsCount > DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS) {
                        start = currentContactsCount - 5000;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d(this.currentAccount + " current cached contacts count = " + currentContactsCount);
                    }
                }
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.dispose();
                }
            }
            HashMap<String, Contact> contactHashMap2 = new HashMap(count);
            if (start != 0) {
                try {
                    cursor = this.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0," + currentContactsCount, new Object[0]);
                } catch (Throwable e) {
                    contactHashMap2.clear();
                    FileLog.m13e(e);
                    if (cursor != null) {
                        cursor.dispose();
                    }
                } catch (Throwable th2) {
                    if (cursor != null) {
                        cursor.dispose();
                    }
                }
            } else {
                cursor = this.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1", new Object[0]);
            }
            while (cursor.next()) {
                String key = cursor.stringValue(0);
                contact = (Contact) contactHashMap2.get(key);
                if (contact == null) {
                    contact = new Contact();
                    contact.contact_id = cursor.intValue(1);
                    contact.first_name = cursor.stringValue(2);
                    contact.last_name = cursor.stringValue(3);
                    contact.imported = cursor.intValue(7);
                    if (contact.first_name == null) {
                        contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (contact.last_name == null) {
                        contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    contactHashMap2.put(key, contact);
                }
                phone = cursor.stringValue(4);
                if (phone != null) {
                    contact.phones.add(phone);
                    sphone = cursor.stringValue(5);
                    if (sphone == null) {
                        continue;
                    } else {
                        if (sphone.length() == 8 && phone.length() != 8) {
                            sphone = C0194PhoneFormat.stripExceptNumbers(phone);
                        }
                        contact.shortPhones.add(sphone);
                        contact.phoneDeleted.add(Integer.valueOf(cursor.intValue(6)));
                        contact.phoneTypes.add(TtmlNode.ANONYMOUS_REGION_ID);
                        if (contactHashMap2.size() == DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS) {
                            break;
                        }
                    }
                }
            }
            cursor.dispose();
            cursor = null;
            if (cursor != null) {
                cursor.dispose();
            }
            ContactsController instance = ContactsController.getInstance(this.currentAccount);
            if (byError) {
                z = false;
            } else {
                z = true;
            }
            instance.performSyncPhoneBook(contactHashMap2, true, true, false, false, z, false);
        } catch (Throwable th3) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$57(this));
    }

    final /* synthetic */ void lambda$getContacts$80$MessagesStorage() {
        ArrayList<TL_contact> contacts = new ArrayList();
        ArrayList<User> users = new ArrayList();
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
            StringBuilder uids = new StringBuilder();
            while (cursor.next()) {
                boolean z;
                int user_id = cursor.intValue(0);
                TL_contact contact = new TL_contact();
                contact.user_id = user_id;
                if (cursor.intValue(1) == 1) {
                    z = true;
                } else {
                    z = false;
                }
                contact.mutual = z;
                if (uids.length() != 0) {
                    uids.append(",");
                }
                contacts.add(contact);
                uids.append(contact.user_id);
            }
            cursor.dispose();
            if (uids.length() != 0) {
                getUsersInternal(uids.toString(), users);
            }
        } catch (Throwable e) {
            contacts.clear();
            users.clear();
            FileLog.m13e(e);
        }
        ContactsController.getInstance(this.currentAccount).processLoadedContacts(contacts, users, 1);
    }

    public void getUnsentMessages(int count) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$58(this, count));
    }

    final /* synthetic */ void lambda$getUnsentMessages$81$MessagesStorage(int count) {
        try {
            SparseArray<Message> messageHashMap = new SparseArray();
            ArrayList<Message> messages = new ArrayList();
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            ArrayList<Integer> broadcastIds = new ArrayList();
            ArrayList<Integer> encryptedChatIds = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT " + count, new Object[0]);
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(1);
                if (data != null) {
                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.send_state = cursor.intValue(2);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    if (messageHashMap.indexOfKey(message.f104id) < 0) {
                        MessageObject.setUnreadFlags(message, cursor.intValue(0));
                        message.f104id = cursor.intValue(3);
                        message.date = cursor.intValue(4);
                        if (!cursor.isNull(5)) {
                            message.random_id = cursor.longValue(5);
                        }
                        message.dialog_id = cursor.longValue(6);
                        message.seq_in = cursor.intValue(7);
                        message.seq_out = cursor.intValue(8);
                        message.ttl = cursor.intValue(9);
                        messages.add(message);
                        messageHashMap.put(message.f104id, message);
                        int lower_id = (int) message.dialog_id;
                        int high_id = (int) (message.dialog_id >> 32);
                        if (lower_id != 0) {
                            if (high_id == 1) {
                                if (!broadcastIds.contains(Integer.valueOf(lower_id))) {
                                    broadcastIds.add(Integer.valueOf(lower_id));
                                }
                            } else if (lower_id < 0) {
                                if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(-lower_id));
                                }
                            } else if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                                usersToLoad.add(Integer.valueOf(lower_id));
                            }
                        } else if (!encryptedChatIds.contains(Integer.valueOf(high_id))) {
                            encryptedChatIds.add(Integer.valueOf(high_id));
                        }
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        if (message.send_state != 3 && (!(message.to_id.channel_id != 0 || MessageObject.isUnread(message) || lower_id == 0) || message.f104id > 0)) {
                            message.send_state = 0;
                        }
                        if (lower_id == 0 && !cursor.isNull(5)) {
                            message.random_id = cursor.longValue(5);
                        }
                    }
                }
            }
            cursor.dispose();
            if (!encryptedChatIds.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedChatIds), encryptedChats, usersToLoad);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!(chatsToLoad.isEmpty() && broadcastIds.isEmpty())) {
                int a;
                Integer cid;
                StringBuilder stringToLoad = new StringBuilder();
                for (a = 0; a < chatsToLoad.size(); a++) {
                    cid = (Integer) chatsToLoad.get(a);
                    if (stringToLoad.length() != 0) {
                        stringToLoad.append(",");
                    }
                    stringToLoad.append(cid);
                }
                for (a = 0; a < broadcastIds.size(); a++) {
                    cid = (Integer) broadcastIds.get(a);
                    if (stringToLoad.length() != 0) {
                        stringToLoad.append(",");
                    }
                    stringToLoad.append(-cid.intValue());
                }
                getChatsInternal(stringToLoad.toString(), chats);
            }
            SendMessagesHelper.getInstance(this.currentAccount).processUnsentMessages(messages, users, chats, encryptedChats);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public boolean checkMessageByRandomId(long random_id) {
        boolean[] result = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$59(this, random_id, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$checkMessageByRandomId$82$MessagesStorage(long random_id, boolean[] result, CountDownLatch countDownLatch) {
        SQLiteCursor cursor = null;
        try {
            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT random_id FROM randoms WHERE random_id = %d", new Object[]{Long.valueOf(random_id)}), new Object[0]);
            if (cursor.next()) {
                result[0] = true;
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        countDownLatch.countDown();
    }

    public boolean checkMessageId(long dialog_id, int mid) {
        boolean[] result = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$60(this, dialog_id, mid, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$checkMessageId$83$MessagesStorage(long dialog_id, int mid, boolean[] result, CountDownLatch countDownLatch) {
        SQLiteCursor cursor = null;
        try {
            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(mid)}), new Object[0]);
            if (cursor.next()) {
                result[0] = true;
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        countDownLatch.countDown();
    }

    public void getUnreadMention(long dialog_id, IntCallback callback) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$61(this, dialog_id, callback));
    }

    final /* synthetic */ void lambda$getUnreadMention$85$MessagesStorage(long dialog_id, IntCallback callback) {
        try {
            int result;
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
            if (cursor.next()) {
                result = cursor.intValue(0);
            } else {
                result = 0;
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$109(callback, result));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void getMessages(long dialog_id, int count, int max_id, int offset_date, int minDate, int classGuid, int load_type, boolean isChannel, int loadIndex) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$62(this, count, max_id, isChannel, dialog_id, load_type, minDate, offset_date, classGuid, loadIndex));
    }

    final /* synthetic */ void lambda$getMessages$87$MessagesStorage(int count, int max_id, boolean isChannel, long dialog_id, int load_type, int minDate, int offset_date, int classGuid, int loadIndex) {
        TL_messages_messages res = new TL_messages_messages();
        int count_unread = 0;
        int mentions_unread = 0;
        int count_query = count;
        int offset_query = 0;
        int min_unread_id = 0;
        int last_message_id = 0;
        boolean queryFromServer = false;
        int max_unread_date = 0;
        long messageMaxId = (long) max_id;
        int max_id_query = max_id;
        boolean unreadCountIsLocal = false;
        int max_id_override = max_id;
        int channelId = 0;
        if (isChannel) {
            channelId = -((int) dialog_id);
        }
        if (!(messageMaxId == 0 || channelId == 0)) {
            messageMaxId |= ((long) channelId) << 32;
        }
        boolean isEnd = false;
        int num = dialog_id == 777000 ? 10 : 1;
        try {
            SQLiteCursor cursor;
            AbstractSerializedData data;
            Message message;
            Message message2;
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            ArrayList<Long> replyMessages = new ArrayList();
            SparseArray<ArrayList<Message>> replyMessageOwners = new SparseArray();
            LongSparseArray<ArrayList<Message>> replyMessageRandomOwners = new LongSparseArray();
            int lower_id = (int) dialog_id;
            if (lower_id != 0) {
                int existingUnreadCount;
                boolean containMessage;
                if (load_type == 3 && minDate == 0) {
                    cursor = this.database.queryFinalized("SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = " + dialog_id, new Object[0]);
                    if (cursor.next()) {
                        min_unread_id = cursor.intValue(0) + 1;
                        count_unread = cursor.intValue(1);
                        max_unread_date = cursor.intValue(2);
                        mentions_unread = cursor.intValue(3);
                    }
                    cursor.dispose();
                } else if (!(load_type == 1 || load_type == 3 || load_type == 4 || minDate != 0)) {
                    if (load_type == 2) {
                        cursor = this.database.queryFinalized("SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = " + dialog_id, new Object[0]);
                        if (cursor.next()) {
                            min_unread_id = cursor.intValue(0);
                            max_id_query = min_unread_id;
                            messageMaxId = (long) min_unread_id;
                            count_unread = cursor.intValue(1);
                            max_unread_date = cursor.intValue(2);
                            mentions_unread = cursor.intValue(3);
                            queryFromServer = true;
                            if (!(messageMaxId == 0 || channelId == 0)) {
                                messageMaxId |= ((long) channelId) << 32;
                            }
                        }
                        cursor.dispose();
                        if (!queryFromServer) {
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                            if (cursor.next()) {
                                min_unread_id = cursor.intValue(0);
                                max_unread_date = cursor.intValue(1);
                            }
                            cursor.dispose();
                            if (min_unread_id != 0) {
                                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(min_unread_id)}), new Object[0]);
                                if (cursor.next()) {
                                    count_unread = cursor.intValue(0);
                                }
                                cursor.dispose();
                            }
                        } else if (max_id_query == 0) {
                            existingUnreadCount = 0;
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                            if (cursor.next()) {
                                existingUnreadCount = cursor.intValue(0);
                            }
                            cursor.dispose();
                            if (existingUnreadCount == count_unread) {
                                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                                if (cursor.next()) {
                                    min_unread_id = cursor.intValue(0);
                                    max_id_query = min_unread_id;
                                    messageMaxId = (long) min_unread_id;
                                    if (!(messageMaxId == 0 || channelId == 0)) {
                                        messageMaxId |= ((long) channelId) << 32;
                                    }
                                }
                                cursor.dispose();
                            }
                        } else {
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id_query), Integer.valueOf(max_id_query)}), new Object[0]);
                            containMessage = !cursor.next();
                            cursor.dispose();
                            if (containMessage) {
                                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id_query)}), new Object[0]);
                                if (cursor.next()) {
                                    max_id_query = cursor.intValue(0);
                                    messageMaxId = (long) max_id_query;
                                    if (!(messageMaxId == 0 || channelId == 0)) {
                                        messageMaxId |= ((long) channelId) << 32;
                                    }
                                }
                                cursor.dispose();
                            }
                        }
                    }
                    if (count_query > count_unread || count_unread < num) {
                        count_query = Math.max(count_query, count_unread + 10);
                        if (count_unread < num) {
                            count_unread = 0;
                            min_unread_id = 0;
                            messageMaxId = 0;
                            last_message_id = 0;
                            queryFromServer = false;
                        }
                    } else {
                        offset_query = count_unread - count_query;
                        count_query += 10;
                    }
                }
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                if (cursor.next()) {
                    isEnd = cursor.intValue(0) == 1;
                    cursor.dispose();
                } else {
                    cursor.dispose();
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        int mid = cursor.intValue(0);
                        if (mid != 0) {
                            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                            state.requery();
                            state.bindLong(1, dialog_id);
                            state.bindInteger(2, 0);
                            state.bindInteger(3, mid);
                            state.step();
                            state.dispose();
                        }
                    }
                    cursor.dispose();
                }
                long holeMessageId;
                if (load_type == 3 || load_type == 4 || (queryFromServer && load_type == 2)) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        last_message_id = cursor.intValue(0);
                    }
                    cursor.dispose();
                    if (load_type == 4 && offset_date != 0) {
                        int startMid;
                        int endMid;
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(offset_date)}), new Object[0]);
                        if (cursor.next()) {
                            startMid = cursor.intValue(0);
                        } else {
                            startMid = -1;
                        }
                        cursor.dispose();
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(offset_date)}), new Object[0]);
                        if (cursor.next()) {
                            endMid = cursor.intValue(0);
                        } else {
                            endMid = -1;
                        }
                        cursor.dispose();
                        if (!(startMid == -1 || endMid == -1)) {
                            if (startMid == endMid) {
                                max_id_query = startMid;
                            } else {
                                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(startMid), Integer.valueOf(startMid)}), new Object[0]);
                                if (cursor.next()) {
                                    startMid = -1;
                                }
                                cursor.dispose();
                                if (startMid != -1) {
                                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(endMid), Integer.valueOf(endMid)}), new Object[0]);
                                    if (cursor.next()) {
                                        endMid = -1;
                                    }
                                    cursor.dispose();
                                    if (endMid != -1) {
                                        max_id_override = endMid;
                                        max_id_query = endMid;
                                        messageMaxId = (long) endMid;
                                        if (!(messageMaxId == 0 || channelId == 0)) {
                                            messageMaxId |= ((long) channelId) << 32;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    containMessage = max_id_query != 0;
                    if (containMessage) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id_query), Integer.valueOf(max_id_query)}), new Object[0]);
                        if (cursor.next()) {
                            containMessage = false;
                        }
                        cursor.dispose();
                    }
                    if (containMessage) {
                        long holeMessageMaxId = 0;
                        long holeMessageMinId = 1;
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id_query)}), new Object[0]);
                        if (cursor.next()) {
                            holeMessageMaxId = (long) cursor.intValue(0);
                            if (channelId != 0) {
                                holeMessageMaxId |= ((long) channelId) << 32;
                            }
                        }
                        cursor.dispose();
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id_query)}), new Object[0]);
                        if (cursor.next()) {
                            holeMessageMinId = (long) cursor.intValue(0);
                            if (channelId != 0) {
                                holeMessageMinId |= ((long) channelId) << 32;
                            }
                        }
                        cursor.dispose();
                        if (holeMessageMaxId == 0 && holeMessageMinId == 1) {
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2), Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2)}), new Object[0]);
                        } else {
                            if (holeMessageMaxId == 0) {
                                holeMessageMaxId = C0016C.NANOS_PER_SECOND;
                                if (channelId != 0) {
                                    holeMessageMaxId = C0016C.NANOS_PER_SECOND | (((long) channelId) << 32);
                                }
                            }
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND (m.mid <= %d OR m.mid < 0) ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Long.valueOf(holeMessageMinId), Integer.valueOf(count_query / 2), Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Long.valueOf(holeMessageMaxId), Integer.valueOf(count_query / 2)}), new Object[0]);
                        }
                    } else if (load_type == 2) {
                        existingUnreadCount = 0;
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                        if (cursor.next()) {
                            existingUnreadCount = cursor.intValue(0);
                        }
                        cursor.dispose();
                        if (existingUnreadCount == count_unread) {
                            unreadCountIsLocal = true;
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2), Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2)}), new Object[0]);
                        } else {
                            cursor = null;
                        }
                    } else {
                        cursor = null;
                    }
                } else if (load_type == 1) {
                    holeMessageId = 0;
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id)}), new Object[0]);
                    if (cursor.next()) {
                        holeMessageId = (long) cursor.intValue(0);
                        if (channelId != 0) {
                            holeMessageId |= ((long) channelId) << 32;
                        }
                    }
                    cursor.dispose();
                    if (holeMessageId != 0) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Long.valueOf(messageMaxId), Long.valueOf(holeMessageId), Integer.valueOf(count_query)}), new Object[0]);
                    } else {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Long.valueOf(messageMaxId), Integer.valueOf(count_query)}), new Object[0]);
                    }
                } else if (minDate == 0) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        last_message_id = cursor.intValue(0);
                    }
                    cursor.dispose();
                    holeMessageId = 0;
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM messages_holes WHERE uid = %d", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        holeMessageId = (long) cursor.intValue(0);
                        if (channelId != 0) {
                            holeMessageId |= ((long) channelId) << 32;
                        }
                    }
                    cursor.dispose();
                    if (holeMessageId != 0) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[]{Long.valueOf(dialog_id), Long.valueOf(holeMessageId), Integer.valueOf(offset_query), Integer.valueOf(count_query)}), new Object[0]);
                    } else {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(offset_query), Integer.valueOf(count_query)}), new Object[0]);
                    }
                } else if (messageMaxId != 0) {
                    holeMessageId = 0;
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id)}), new Object[0]);
                    if (cursor.next()) {
                        holeMessageId = (long) cursor.intValue(0);
                        if (channelId != 0) {
                            holeMessageId |= ((long) channelId) << 32;
                        }
                    }
                    cursor.dispose();
                    if (holeMessageId != 0) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Long.valueOf(messageMaxId), Long.valueOf(holeMessageId), Integer.valueOf(count_query)}), new Object[0]);
                    } else {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Long.valueOf(messageMaxId), Integer.valueOf(count_query)}), new Object[0]);
                    }
                } else {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Integer.valueOf(offset_query), Integer.valueOf(count_query)}), new Object[0]);
                }
            } else {
                isEnd = true;
                if (load_type == 3 && minDate == 0) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        min_unread_id = cursor.intValue(0);
                    }
                    cursor.dispose();
                    int min_unread_id2 = 0;
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        min_unread_id2 = cursor.intValue(0);
                        max_unread_date = cursor.intValue(1);
                    }
                    cursor.dispose();
                    if (min_unread_id2 != 0) {
                        min_unread_id = min_unread_id2;
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(min_unread_id2)}), new Object[0]);
                        if (cursor.next()) {
                            count_unread = cursor.intValue(0);
                        }
                        cursor.dispose();
                    }
                }
                if (load_type == 3 || load_type == 4) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                    if (cursor.next()) {
                        last_message_id = cursor.intValue(0);
                    }
                    cursor.dispose();
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)", new Object[]{Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2), Long.valueOf(dialog_id), Long.valueOf(messageMaxId), Integer.valueOf(count_query / 2)}), new Object[0]);
                } else if (load_type == 1) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id), Integer.valueOf(count_query)}), new Object[0]);
                } else if (minDate == 0) {
                    if (load_type == 2) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                        if (cursor.next()) {
                            last_message_id = cursor.intValue(0);
                        }
                        cursor.dispose();
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                        if (cursor.next()) {
                            min_unread_id = cursor.intValue(0);
                            max_unread_date = cursor.intValue(1);
                        }
                        cursor.dispose();
                        if (min_unread_id != 0) {
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(min_unread_id)}), new Object[0]);
                            if (cursor.next()) {
                                count_unread = cursor.intValue(0);
                            }
                            cursor.dispose();
                        }
                    }
                    if (count_query > count_unread || count_unread < num) {
                        count_query = Math.max(count_query, count_unread + 10);
                        if (count_unread < num) {
                            count_unread = 0;
                            min_unread_id = 0;
                            last_message_id = 0;
                        }
                    } else {
                        offset_query = count_unread - count_query;
                        count_query += 10;
                    }
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(offset_query), Integer.valueOf(count_query)}), new Object[0]);
                } else if (max_id != 0) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(max_id), Integer.valueOf(count_query)}), new Object[0]);
                } else {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[]{Long.valueOf(dialog_id), Integer.valueOf(minDate), Integer.valueOf(0), Integer.valueOf(count_query)}), new Object[0]);
                }
            }
            int minId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int maxId = Integer.MIN_VALUE;
            if (cursor != null) {
                while (cursor.next()) {
                    data = cursor.byteBufferValue(1);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.send_state = cursor.intValue(2);
                        if (!(message.f104id <= 0 || message.send_state == 0 || message.send_state == 3)) {
                            message.send_state = 0;
                        }
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        MessageObject.setUnreadFlags(message, cursor.intValue(0));
                        message.f104id = cursor.intValue(3);
                        if (message.f104id > 0) {
                            minId = Math.min(message.f104id, minId);
                            maxId = Math.max(message.f104id, maxId);
                        }
                        message.date = cursor.intValue(4);
                        message.dialog_id = dialog_id;
                        if ((message.flags & 1024) != 0) {
                            message.views = cursor.intValue(7);
                        }
                        if (lower_id != 0 && message.ttl == 0) {
                            message.ttl = cursor.intValue(8);
                        }
                        if (cursor.intValue(9) != 0) {
                            message.mentioned = true;
                        }
                        res.messages.add(message);
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        if (!(message.reply_to_msg_id == 0 && message.reply_to_random_id == 0)) {
                            if (!cursor.isNull(6)) {
                                data = cursor.byteBufferValue(6);
                                if (data != null) {
                                    message.replyMessage = Message.TLdeserialize(data, data.readInt32(false), false);
                                    message.replyMessage.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                    data.reuse();
                                    if (message.replyMessage != null) {
                                        if (MessageObject.isMegagroup(message)) {
                                            message2 = message.replyMessage;
                                            message2.flags |= Integer.MIN_VALUE;
                                        }
                                        addUsersAndChatsFromMessage(message.replyMessage, usersToLoad, chatsToLoad);
                                    }
                                }
                            }
                            if (message.replyMessage == null) {
                                ArrayList<Message> messages;
                                if (message.reply_to_msg_id != 0) {
                                    long messageId = (long) message.reply_to_msg_id;
                                    if (message.to_id.channel_id != 0) {
                                        messageId |= ((long) message.to_id.channel_id) << 32;
                                    }
                                    if (!replyMessages.contains(Long.valueOf(messageId))) {
                                        replyMessages.add(Long.valueOf(messageId));
                                    }
                                    messages = (ArrayList) replyMessageOwners.get(message.reply_to_msg_id);
                                    if (messages == null) {
                                        messages = new ArrayList();
                                        replyMessageOwners.put(message.reply_to_msg_id, messages);
                                    }
                                    messages.add(message);
                                } else {
                                    if (!replyMessages.contains(Long.valueOf(message.reply_to_random_id))) {
                                        replyMessages.add(Long.valueOf(message.reply_to_random_id));
                                    }
                                    messages = (ArrayList) replyMessageRandomOwners.get(message.reply_to_random_id);
                                    if (messages == null) {
                                        messages = new ArrayList();
                                        replyMessageRandomOwners.put(message.reply_to_random_id, messages);
                                    }
                                    messages.add(message);
                                }
                            }
                        }
                        if (lower_id == 0 && !cursor.isNull(5)) {
                            message.random_id = cursor.longValue(5);
                        }
                        if (MessageObject.isSecretPhotoOrVideo(message)) {
                            try {
                                SQLiteCursor cursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT date FROM enc_tasks_v2 WHERE mid = %d", new Object[]{Integer.valueOf(message.f104id)}), new Object[0]);
                                if (cursor2.next()) {
                                    message.destroyTime = cursor2.intValue(0);
                                }
                                cursor2.dispose();
                            } catch (Throwable e) {
                                FileLog.m13e(e);
                            }
                        } else {
                            continue;
                        }
                    }
                }
                cursor.dispose();
            }
            Collections.sort(res.messages, MessagesStorage$$Lambda$108.$instance);
            if (lower_id != 0) {
                if ((load_type == 3 || load_type == 4 || (load_type == 2 && queryFromServer && !unreadCountIsLocal)) && !res.messages.isEmpty() && (minId > max_id_query || maxId < max_id_query)) {
                    replyMessages.clear();
                    usersToLoad.clear();
                    chatsToLoad.clear();
                    res.messages.clear();
                }
                if ((load_type == 4 || load_type == 3) && res.messages.size() == 1) {
                    res.messages.clear();
                }
            }
            if (!replyMessages.isEmpty()) {
                ArrayList<Message> arrayList;
                int a;
                if (replyMessageOwners.size() > 0) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
                } else {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
                }
                while (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        message.f104id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = dialog_id;
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        Message object;
                        if (replyMessageOwners.size() > 0) {
                            arrayList = (ArrayList) replyMessageOwners.get(message.f104id);
                            if (arrayList != null) {
                                for (a = 0; a < arrayList.size(); a++) {
                                    object = (Message) arrayList.get(a);
                                    object.replyMessage = message;
                                    if (MessageObject.isMegagroup(object)) {
                                        message2 = object.replyMessage;
                                        message2.flags |= Integer.MIN_VALUE;
                                    }
                                }
                            }
                        } else {
                            long value = cursor.longValue(3);
                            arrayList = (ArrayList) replyMessageRandomOwners.get(value);
                            replyMessageRandomOwners.remove(value);
                            if (arrayList != null) {
                                for (a = 0; a < arrayList.size(); a++) {
                                    object = (Message) arrayList.get(a);
                                    object.replyMessage = message;
                                    object.reply_to_msg_id = message.f104id;
                                    if (MessageObject.isMegagroup(object)) {
                                        message2 = object.replyMessage;
                                        message2.flags |= Integer.MIN_VALUE;
                                    }
                                }
                            }
                        }
                    }
                }
                cursor.dispose();
                if (replyMessageRandomOwners.size() > 0) {
                    for (int b = 0; b < replyMessageRandomOwners.size(); b++) {
                        arrayList = (ArrayList) replyMessageRandomOwners.valueAt(b);
                        for (a = 0; a < arrayList.size(); a++) {
                            ((Message) arrayList.get(a)).reply_to_random_id = 0;
                        }
                    }
                }
            }
            if (mentions_unread != 0) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(dialog_id)}), new Object[0]);
                if (!cursor.next()) {
                    mentions_unread *= -1;
                } else if (mentions_unread != cursor.intValue(0)) {
                    mentions_unread *= -1;
                }
                cursor.dispose();
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), res.users);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), res.chats);
            }
            MessagesController.getInstance(this.currentAccount).processLoadedMessages(res, dialog_id, count_query, max_id_override, offset_date, true, classGuid, min_unread_id, last_message_id, count_unread, max_unread_date, load_type, isChannel, isEnd, loadIndex, queryFromServer, mentions_unread);
        } catch (Throwable e2) {
            res.messages.clear();
            res.chats.clear();
            res.users.clear();
            FileLog.m13e(e2);
            MessagesController.getInstance(this.currentAccount).processLoadedMessages(res, dialog_id, count_query, max_id_override, offset_date, true, classGuid, min_unread_id, last_message_id, count_unread, max_unread_date, load_type, isChannel, isEnd, loadIndex, queryFromServer, mentions_unread);
        } catch (Throwable th) {
            Throwable th2 = th;
            MessagesController.getInstance(this.currentAccount).processLoadedMessages(res, dialog_id, count_query, max_id_override, offset_date, true, classGuid, min_unread_id, last_message_id, count_unread, max_unread_date, load_type, isChannel, isEnd, loadIndex, queryFromServer, mentions_unread);
        }
    }

    static final /* synthetic */ int lambda$null$86$MessagesStorage(Message lhs, Message rhs) {
        if (lhs.f104id <= 0 || rhs.f104id <= 0) {
            if (lhs.f104id >= 0 || rhs.f104id >= 0) {
                if (lhs.date > rhs.date) {
                    return -1;
                }
                if (lhs.date < rhs.date) {
                    return 1;
                }
            } else if (lhs.f104id < rhs.f104id) {
                return -1;
            } else {
                if (lhs.f104id > rhs.f104id) {
                    return 1;
                }
            }
        } else if (lhs.f104id > rhs.f104id) {
            return -1;
        } else {
            if (lhs.f104id < rhs.f104id) {
                return 1;
            }
        }
        return 0;
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$63(this));
    }

    final /* synthetic */ void lambda$clearSentMedia$88$MessagesStorage() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public Object[] getSentFile(String path, int type) {
        if (path == null || path.toLowerCase().endsWith("attheme")) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Object[] result = new Object[2];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$64(this, path, type, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0] == null ? null : result;
    }

    final /* synthetic */ void lambda$getSentFile$89$MessagesStorage(String path, int type, Object[] result, CountDownLatch countDownLatch) {
        try {
            if (Utilities.MD5(path) != null) {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", new Object[]{id, Integer.valueOf(type)}), new Object[0]);
                if (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLObject file = MessageMedia.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (file instanceof TL_messageMediaDocument) {
                            result[0] = ((TL_messageMediaDocument) file).document;
                        } else if (file instanceof TL_messageMediaPhoto) {
                            result[0] = ((TL_messageMediaPhoto) file).photo;
                        }
                        if (result[0] != null) {
                            result[1] = cursor.stringValue(1);
                        }
                    }
                }
                cursor.dispose();
            }
            countDownLatch.countDown();
        } catch (Throwable e) {
            FileLog.m13e(e);
            countDownLatch.countDown();
        } catch (Throwable th) {
            countDownLatch.countDown();
            throw th;
        }
    }

    public void putSentFile(String path, TLObject file, int type, String parent) {
        if (path != null && file != null && parent != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$65(this, path, file, type, parent));
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$putSentFile$90$MessagesStorage(String path, TLObject file, int type, String parent) {
        SQLitePreparedStatement state = null;
        try {
            String id = Utilities.MD5(path);
            if (id != null) {
                MessageMedia messageMedia = null;
                if (file instanceof Photo) {
                    messageMedia = new TL_messageMediaPhoto();
                    messageMedia.photo = (Photo) file;
                    messageMedia.flags |= 1;
                } else if (file instanceof Document) {
                    messageMedia = new TL_messageMediaDocument();
                    messageMedia.document = (Document) file;
                    messageMedia.flags |= 1;
                }
                if (messageMedia != null) {
                    state = this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(messageMedia.getObjectSize());
                    messageMedia.serializeToStream(data);
                    state.bindString(1, id);
                    state.bindInteger(2, type);
                    state.bindByteBuffer(3, data);
                    state.bindString(4, parent);
                    state.step();
                    data.reuse();
                } else if (state != null) {
                    state.dispose();
                    return;
                } else {
                    return;
                }
            }
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatSeq(EncryptedChat chat, boolean cleanup) {
        if (chat != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$66(this, chat, cleanup));
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$updateEncryptedChatSeq$91$MessagesStorage(EncryptedChat chat, boolean cleanup) {
        SQLitePreparedStatement state = null;
        try {
            state = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
            state.bindInteger(1, chat.seq_in);
            state.bindInteger(2, chat.seq_out);
            state.bindInteger(3, (chat.key_use_count_in << 16) | chat.key_use_count_out);
            state.bindInteger(4, chat.in_seq_no);
            state.bindInteger(5, chat.mtproto_seq);
            state.bindInteger(6, chat.f88id);
            state.step();
            if (cleanup) {
                long did = ((long) chat.f88id) << 32;
                this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN (SELECT m.mid FROM messages as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d)", new Object[]{Long.valueOf(did), Integer.valueOf(chat.in_seq_no)})).stepThis().dispose();
            }
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatTTL(EncryptedChat chat) {
        if (chat != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$67(this, chat));
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$updateEncryptedChatTTL$92$MessagesStorage(EncryptedChat chat) {
        SQLitePreparedStatement state = null;
        try {
            state = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
            state.bindInteger(1, chat.ttl);
            state.bindInteger(2, chat.f88id);
            state.step();
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatLayer(EncryptedChat chat) {
        if (chat != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$68(this, chat));
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$updateEncryptedChatLayer$93$MessagesStorage(EncryptedChat chat) {
        SQLitePreparedStatement state = null;
        try {
            state = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
            state.bindInteger(1, chat.layer);
            state.bindInteger(2, chat.f88id);
            state.step();
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChat(EncryptedChat chat) {
        if (chat != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$69(this, chat));
        }
    }

    /* JADX WARNING: Failed to extract finally block: empty outs */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$updateEncryptedChat$94$MessagesStorage(EncryptedChat chat) {
        int i = 1;
        SQLitePreparedStatement state = null;
        try {
            int length;
            if ((chat.key_hash == null || chat.key_hash.length < 16) && chat.auth_key != null) {
                chat.key_hash = AndroidUtilities.calcAuthKeyHash(chat.auth_key);
            }
            state = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
            NativeByteBuffer data = new NativeByteBuffer(chat.getObjectSize());
            if (chat.a_or_b != null) {
                length = chat.a_or_b.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data2 = new NativeByteBuffer(length);
            if (chat.auth_key != null) {
                length = chat.auth_key.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data3 = new NativeByteBuffer(length);
            if (chat.future_auth_key != null) {
                length = chat.future_auth_key.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data4 = new NativeByteBuffer(length);
            if (chat.key_hash != null) {
                i = chat.key_hash.length;
            }
            NativeByteBuffer data5 = new NativeByteBuffer(i);
            chat.serializeToStream(data);
            state.bindByteBuffer(1, data);
            if (chat.a_or_b != null) {
                data2.writeBytes(chat.a_or_b);
            }
            if (chat.auth_key != null) {
                data3.writeBytes(chat.auth_key);
            }
            if (chat.future_auth_key != null) {
                data4.writeBytes(chat.future_auth_key);
            }
            if (chat.key_hash != null) {
                data5.writeBytes(chat.key_hash);
            }
            state.bindByteBuffer(2, data2);
            state.bindByteBuffer(3, data3);
            state.bindInteger(4, chat.ttl);
            state.bindInteger(5, chat.layer);
            state.bindInteger(6, chat.seq_in);
            state.bindInteger(7, chat.seq_out);
            state.bindInteger(8, (chat.key_use_count_in << 16) | chat.key_use_count_out);
            state.bindLong(9, chat.exchange_id);
            state.bindInteger(10, chat.key_create_date);
            state.bindLong(11, chat.future_key_fingerprint);
            state.bindByteBuffer(12, data4);
            state.bindByteBuffer(13, data5);
            state.bindInteger(14, chat.in_seq_no);
            state.bindInteger(15, chat.admin_id);
            state.bindInteger(16, chat.mtproto_seq);
            state.bindInteger(17, chat.f88id);
            state.step();
            data.reuse();
            data2.reuse();
            data3.reuse();
            data4.reuse();
            data5.reuse();
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th) {
            if (state != null) {
                state.dispose();
            }
            throw th;
        }
    }

    public boolean isDialogHasMessages(long did) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$70(this, did, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$isDialogHasMessages$95$MessagesStorage(long did, boolean[] result, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[]{Long.valueOf(did)}), new Object[0]);
            result[0] = cursor.next();
            cursor.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        } finally {
            countDownLatch.countDown();
        }
    }

    public boolean hasAuthMessage(int date) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] result = new boolean[1];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$71(this, date, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return result[0];
    }

    final /* synthetic */ void lambda$hasAuthMessage$96$MessagesStorage(int date, boolean[] result, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[]{Integer.valueOf(date)}), new Object[0]);
            result[0] = cursor.next();
            cursor.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        } finally {
            countDownLatch.countDown();
        }
    }

    public void getEncryptedChat(int chat_id, CountDownLatch countDownLatch, ArrayList<TLObject> result) {
        if (countDownLatch != null && result != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$72(this, chat_id, result, countDownLatch));
        }
    }

    final /* synthetic */ void lambda$getEncryptedChat$97$MessagesStorage(int chat_id, ArrayList result, CountDownLatch countDownLatch) {
        try {
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
            getEncryptedChatsInternal(TtmlNode.ANONYMOUS_REGION_ID + chat_id, encryptedChats, usersToLoad);
            if (!(encryptedChats.isEmpty() || usersToLoad.isEmpty())) {
                ArrayList<User> users = new ArrayList();
                getUsersInternal(TextUtils.join(",", usersToLoad), users);
                if (!users.isEmpty()) {
                    result.add(encryptedChats.get(0));
                    result.add(users.get(0));
                }
            }
            countDownLatch.countDown();
        } catch (Throwable e) {
            FileLog.m13e(e);
            countDownLatch.countDown();
        } catch (Throwable th) {
            countDownLatch.countDown();
            throw th;
        }
    }

    public void putEncryptedChat(EncryptedChat chat, User user, TL_dialog dialog) {
        if (chat != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$73(this, chat, user, dialog));
        }
    }

    final /* synthetic */ void lambda$putEncryptedChat$98$MessagesStorage(EncryptedChat chat, User user, TL_dialog dialog) {
        int i = 1;
        try {
            int length;
            if ((chat.key_hash == null || chat.key_hash.length < 16) && chat.auth_key != null) {
                chat.key_hash = AndroidUtilities.calcAuthKeyHash(chat.auth_key);
            }
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(chat.getObjectSize());
            if (chat.a_or_b != null) {
                length = chat.a_or_b.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data2 = new NativeByteBuffer(length);
            if (chat.auth_key != null) {
                length = chat.auth_key.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data3 = new NativeByteBuffer(length);
            if (chat.future_auth_key != null) {
                length = chat.future_auth_key.length;
            } else {
                length = 1;
            }
            NativeByteBuffer data4 = new NativeByteBuffer(length);
            if (chat.key_hash != null) {
                i = chat.key_hash.length;
            }
            NativeByteBuffer data5 = new NativeByteBuffer(i);
            chat.serializeToStream(data);
            state.bindInteger(1, chat.f88id);
            state.bindInteger(2, user.f176id);
            state.bindString(3, formatUserSearchName(user));
            state.bindByteBuffer(4, data);
            if (chat.a_or_b != null) {
                data2.writeBytes(chat.a_or_b);
            }
            if (chat.auth_key != null) {
                data3.writeBytes(chat.auth_key);
            }
            if (chat.future_auth_key != null) {
                data4.writeBytes(chat.future_auth_key);
            }
            if (chat.key_hash != null) {
                data5.writeBytes(chat.key_hash);
            }
            state.bindByteBuffer(5, data2);
            state.bindByteBuffer(6, data3);
            state.bindInteger(7, chat.ttl);
            state.bindInteger(8, chat.layer);
            state.bindInteger(9, chat.seq_in);
            state.bindInteger(10, chat.seq_out);
            state.bindInteger(11, (chat.key_use_count_in << 16) | chat.key_use_count_out);
            state.bindLong(12, chat.exchange_id);
            state.bindInteger(13, chat.key_create_date);
            state.bindLong(14, chat.future_key_fingerprint);
            state.bindByteBuffer(15, data4);
            state.bindByteBuffer(16, data5);
            state.bindInteger(17, chat.in_seq_no);
            state.bindInteger(18, chat.admin_id);
            state.bindInteger(19, chat.mtproto_seq);
            state.step();
            state.dispose();
            data.reuse();
            data2.reuse();
            data3.reuse();
            data4.reuse();
            data5.reuse();
            if (dialog != null) {
                state = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                state.bindLong(1, dialog.f128id);
                state.bindInteger(2, dialog.last_message_date);
                state.bindInteger(3, dialog.unread_count);
                state.bindInteger(4, dialog.top_message);
                state.bindInteger(5, dialog.read_inbox_max_id);
                state.bindInteger(6, dialog.read_outbox_max_id);
                state.bindInteger(7, 0);
                state.bindInteger(8, dialog.unread_mentions_count);
                state.bindInteger(9, dialog.pts);
                state.bindInteger(10, 0);
                state.bindInteger(11, dialog.pinnedNum);
                state.bindInteger(12, dialog.flags);
                state.step();
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private String formatUserSearchName(User user) {
        StringBuilder str = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        if (user.first_name != null && user.first_name.length() > 0) {
            str.append(user.first_name);
        }
        if (user.last_name != null && user.last_name.length() > 0) {
            if (str.length() > 0) {
                str.append(" ");
            }
            str.append(user.last_name);
        }
        str.append(";;;");
        if (user.username != null && user.username.length() > 0) {
            str.append(user.username);
        }
        return str.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<User> users) throws Exception {
        if (users != null && !users.isEmpty()) {
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
            for (int a = 0; a < users.size(); a++) {
                NativeByteBuffer data;
                User user = (User) users.get(a);
                if (user.min) {
                    SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(user.f176id)}), new Object[0]);
                    if (cursor.next()) {
                        try {
                            data = cursor.byteBufferValue(0);
                            if (data != null) {
                                User oldUser = User.TLdeserialize(data, data.readInt32(false), false);
                                data.reuse();
                                if (oldUser != null) {
                                    if (user.username != null) {
                                        oldUser.username = user.username;
                                        oldUser.flags |= 8;
                                    } else {
                                        oldUser.username = null;
                                        oldUser.flags &= -9;
                                    }
                                    if (user.photo != null) {
                                        oldUser.photo = user.photo;
                                        oldUser.flags |= 32;
                                    } else {
                                        oldUser.photo = null;
                                        oldUser.flags &= -33;
                                    }
                                    user = oldUser;
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                    cursor.dispose();
                }
                state.requery();
                data = new NativeByteBuffer(user.getObjectSize());
                user.serializeToStream(data);
                state.bindInteger(1, user.f176id);
                state.bindString(2, formatUserSearchName(user));
                if (user.status != null) {
                    if (user.status instanceof TL_userStatusRecently) {
                        user.status.expires = -100;
                    } else if (user.status instanceof TL_userStatusLastWeek) {
                        user.status.expires = -101;
                    } else if (user.status instanceof TL_userStatusLastMonth) {
                        user.status.expires = -102;
                    }
                    state.bindInteger(3, user.status.expires);
                } else {
                    state.bindInteger(3, 0);
                }
                state.bindByteBuffer(4, data);
                state.step();
                data.reuse();
            }
            state.dispose();
        }
    }

    private void putChatsInternal(ArrayList<Chat> chats) throws Exception {
        if (chats != null && !chats.isEmpty()) {
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
            for (int a = 0; a < chats.size(); a++) {
                NativeByteBuffer data;
                Chat chat = (Chat) chats.get(a);
                if (chat.min) {
                    SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(chat.f78id)}), new Object[0]);
                    if (cursor.next()) {
                        try {
                            data = cursor.byteBufferValue(0);
                            if (data != null) {
                                Chat oldChat = Chat.TLdeserialize(data, data.readInt32(false), false);
                                data.reuse();
                                if (oldChat != null) {
                                    oldChat.title = chat.title;
                                    oldChat.photo = chat.photo;
                                    oldChat.broadcast = chat.broadcast;
                                    oldChat.verified = chat.verified;
                                    oldChat.megagroup = chat.megagroup;
                                    oldChat.democracy = chat.democracy;
                                    if (chat.username != null) {
                                        oldChat.username = chat.username;
                                        oldChat.flags |= 64;
                                    } else {
                                        oldChat.username = null;
                                        oldChat.flags &= -65;
                                    }
                                    chat = oldChat;
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                    cursor.dispose();
                }
                state.requery();
                data = new NativeByteBuffer(chat.getObjectSize());
                chat.serializeToStream(data);
                state.bindInteger(1, chat.f78id);
                if (chat.title != null) {
                    state.bindString(2, chat.title.toLowerCase());
                } else {
                    state.bindString(2, TtmlNode.ANONYMOUS_REGION_ID);
                }
                state.bindByteBuffer(3, data);
                state.step();
                data.reuse();
            }
            state.dispose();
        }
    }

    public void getUsersInternal(String usersToLoad, ArrayList<User> result) throws Exception {
        if (usersToLoad != null && usersToLoad.length() != 0 && result != null) {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{usersToLoad}), new Object[0]);
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        User user = User.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (user != null) {
                            if (user.status != null) {
                                user.status.expires = cursor.intValue(1);
                            }
                            result.add(user);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
        }
    }

    public void getChatsInternal(String chatsToLoad, ArrayList<Chat> result) throws Exception {
        if (chatsToLoad != null && chatsToLoad.length() != 0 && result != null) {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{chatsToLoad}), new Object[0]);
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        Chat chat = Chat.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (chat != null) {
                            result.add(chat);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
        }
    }

    public void getEncryptedChatsInternal(String chatsToLoad, ArrayList<EncryptedChat> result, ArrayList<Integer> usersToLoad) throws Exception {
        if (chatsToLoad != null && chatsToLoad.length() != 0 && result != null) {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", new Object[]{chatsToLoad}), new Object[0]);
            while (cursor.next()) {
                try {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        EncryptedChat chat = EncryptedChat.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (chat != null) {
                            chat.user_id = cursor.intValue(1);
                            if (!(usersToLoad == null || usersToLoad.contains(Integer.valueOf(chat.user_id)))) {
                                usersToLoad.add(Integer.valueOf(chat.user_id));
                            }
                            chat.a_or_b = cursor.byteArrayValue(2);
                            chat.auth_key = cursor.byteArrayValue(3);
                            chat.ttl = cursor.intValue(4);
                            chat.layer = cursor.intValue(5);
                            chat.seq_in = cursor.intValue(6);
                            chat.seq_out = cursor.intValue(7);
                            int use_count = cursor.intValue(8);
                            chat.key_use_count_in = (short) (use_count >> 16);
                            chat.key_use_count_out = (short) use_count;
                            chat.exchange_id = cursor.longValue(9);
                            chat.key_create_date = cursor.intValue(10);
                            chat.future_key_fingerprint = cursor.longValue(11);
                            chat.future_auth_key = cursor.byteArrayValue(12);
                            chat.key_hash = cursor.byteArrayValue(13);
                            chat.in_seq_no = cursor.intValue(14);
                            int admin_id = cursor.intValue(15);
                            if (admin_id != 0) {
                                chat.admin_id = admin_id;
                            }
                            chat.mtproto_seq = cursor.intValue(16);
                            result.add(chat);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
        }
    }

    private void putUsersAndChatsInternal(ArrayList<User> users, ArrayList<Chat> chats, boolean withTransaction) {
        if (withTransaction) {
            try {
                this.database.beginTransaction();
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        putUsersInternal(users);
        putChatsInternal(chats);
        if (withTransaction) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(ArrayList<User> users, ArrayList<Chat> chats, boolean withTransaction, boolean useQueue) {
        if (users != null && users.isEmpty() && chats != null && chats.isEmpty()) {
            return;
        }
        if (useQueue) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$74(this, users, chats, withTransaction));
        } else {
            putUsersAndChatsInternal(users, chats, withTransaction);
        }
    }

    public void removeFromDownloadQueue(long id, int type, boolean move) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$75(this, move, type, id));
    }

    final /* synthetic */ void lambda$removeFromDownloadQueue$100$MessagesStorage(boolean move, int type, long id) {
        if (move) {
            int minDate = -1;
            try {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(type)}), new Object[0]);
                if (cursor.next()) {
                    minDate = cursor.intValue(0);
                }
                cursor.dispose();
                if (minDate != -1) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[]{Integer.valueOf(minDate - 1), Long.valueOf(id), Integer.valueOf(type)})).stepThis().dispose();
                    return;
                }
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(id), Integer.valueOf(type)})).stepThis().dispose();
    }

    public void clearDownloadQueue(int type) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$76(this, type));
    }

    final /* synthetic */ void lambda$clearDownloadQueue$101$MessagesStorage(int type) {
        if (type == 0) {
            try {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(type)})).stepThis().dispose();
    }

    public void getDownloadQueue(int type) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$77(this, type));
    }

    final /* synthetic */ void lambda$getDownloadQueue$103$MessagesStorage(int type) {
        try {
            ArrayList<DownloadObject> objects = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[]{Integer.valueOf(type)}), new Object[0]);
            while (cursor.next()) {
                DownloadObject downloadObject = new DownloadObject();
                downloadObject.type = cursor.intValue(1);
                downloadObject.f47id = cursor.longValue(0);
                downloadObject.parent = cursor.stringValue(3);
                NativeByteBuffer data = cursor.byteBufferValue(2);
                if (data != null) {
                    boolean z;
                    MessageMedia messageMedia = MessageMedia.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (messageMedia.document != null) {
                        downloadObject.object = messageMedia.document;
                    } else if (messageMedia.photo != null) {
                        downloadObject.object = FileLoader.getClosestPhotoSizeWithSize(messageMedia.photo.sizes, AndroidUtilities.getPhotoSize());
                    }
                    if (messageMedia.ttl_seconds != 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    downloadObject.secret = z;
                }
                objects.add(downloadObject);
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$107(this, type, objects));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$102$MessagesStorage(int type, ArrayList objects) {
        DownloadController.getInstance(this.currentAccount).processDownloadObjects(type, objects);
    }

    private int getMessageMediaType(Message message) {
        if (message instanceof TL_message_secret) {
            if ((((message.media instanceof TL_messageMediaPhoto) || MessageObject.isGifMessage(message)) && message.ttl > 0 && message.ttl <= 60) || MessageObject.isVoiceMessage(message) || MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message)) {
                return 1;
            }
            if ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) {
                return 0;
            }
        } else if ((message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return 1;
        } else {
            if ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) {
                return 0;
            }
        }
        return -1;
    }

    public void putWebPages(LongSparseArray<WebPage> webPages) {
        if (!isEmpty((LongSparseArray) webPages)) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$78(this, webPages));
        }
    }

    final /* synthetic */ void lambda$putWebPages$105$MessagesStorage(LongSparseArray webPages) {
        try {
            int a;
            NativeByteBuffer data;
            Message message;
            ArrayList<Message> messages = new ArrayList();
            for (a = 0; a < webPages.size(); a++) {
                SQLiteCursor cursor = this.database.queryFinalized("SELECT mid FROM webpage_pending WHERE id = " + webPages.keyAt(a), new Object[0]);
                ArrayList<Long> mids = new ArrayList();
                while (cursor.next()) {
                    mids.add(Long.valueOf(cursor.longValue(0)));
                }
                cursor.dispose();
                if (!mids.isEmpty()) {
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", mids)}), new Object[0]);
                    while (cursor.next()) {
                        int mid = cursor.intValue(0);
                        data = cursor.byteBufferValue(1);
                        if (data != null) {
                            message = Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                            data.reuse();
                            if (message.media instanceof TL_messageMediaWebPage) {
                                message.f104id = mid;
                                message.media.webpage = (WebPage) webPages.valueAt(a);
                                messages.add(message);
                            }
                        }
                    }
                    cursor.dispose();
                }
            }
            if (!messages.isEmpty()) {
                this.database.beginTransaction();
                SQLitePreparedStatement state = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                SQLitePreparedStatement state2 = this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
                for (a = 0; a < messages.size(); a++) {
                    message = (Message) messages.get(a);
                    data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    long messageId = (long) message.f104id;
                    if (message.to_id.channel_id != 0) {
                        messageId |= ((long) message.to_id.channel_id) << 32;
                    }
                    state.requery();
                    state.bindByteBuffer(1, data);
                    state.bindLong(2, messageId);
                    state.step();
                    state2.requery();
                    state2.bindByteBuffer(1, data);
                    state2.bindLong(2, messageId);
                    state2.step();
                    data.reuse();
                }
                state.dispose();
                state2.dispose();
                this.database.commitTransaction();
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$106(this, messages));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$104$MessagesStorage(ArrayList messages) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpages, messages);
    }

    public void overwriteChannel(int channel_id, TL_updates_channelDifferenceTooLong difference, int newDialogType) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$79(this, channel_id, newDialogType, difference));
    }

    final /* synthetic */ void lambda$overwriteChannel$107$MessagesStorage(int channel_id, int newDialogType, TL_updates_channelDifferenceTooLong difference) {
        boolean checkInvite = false;
        long did = (long) (-channel_id);
        int pinned = 0;
        try {
            SQLiteCursor cursor = this.database.queryFinalized("SELECT pts, pinned FROM dialogs WHERE did = " + did, new Object[0]);
            if (cursor.next()) {
                pinned = cursor.intValue(1);
            } else if (newDialogType != 0) {
                checkInvite = true;
            }
            cursor.dispose();
            this.database.executeFast("DELETE FROM messages WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("UPDATE media_counts_v2 SET old = 1 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + did).stepThis().dispose();
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + did).stepThis().dispose();
            DataQuery.getInstance(this.currentAccount).clearBotKeyboard(did, null);
            TL_messages_dialogs dialogs = new TL_messages_dialogs();
            dialogs.chats.addAll(difference.chats);
            dialogs.users.addAll(difference.users);
            dialogs.messages.addAll(difference.messages);
            TL_dialog dialog = new TL_dialog();
            dialog.f128id = did;
            dialog.flags = 1;
            dialog.peer = new TL_peerChannel();
            dialog.peer.channel_id = channel_id;
            dialog.top_message = difference.top_message;
            dialog.read_inbox_max_id = difference.read_inbox_max_id;
            dialog.read_outbox_max_id = difference.read_outbox_max_id;
            dialog.unread_count = difference.unread_count;
            dialog.unread_mentions_count = difference.unread_mentions_count;
            dialog.notify_settings = null;
            dialog.pinned = pinned != 0;
            dialog.pinnedNum = pinned;
            dialog.pts = difference.pts;
            dialogs.dialogs.add(dialog);
            putDialogsInternal(dialogs, 0);
            updateDialogsWithDeletedMessages(new ArrayList(), null, false, channel_id);
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$105(this, did));
            if (!checkInvite) {
                return;
            }
            if (newDialogType == 1) {
                MessagesController.getInstance(this.currentAccount).checkChannelInviter(channel_id);
            } else {
                MessagesController.getInstance(this.currentAccount).generateJoinMessage(channel_id, false);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$106$MessagesStorage(long did) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), Boolean.valueOf(true));
    }

    public void putChannelViews(SparseArray<SparseIntArray> channelViews, boolean isChannel) {
        if (!isEmpty((SparseArray) channelViews)) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$80(this, channelViews, isChannel));
        }
    }

    final /* synthetic */ void lambda$putChannelViews$108$MessagesStorage(SparseArray channelViews, boolean isChannel) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement state = this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
            for (int a = 0; a < channelViews.size(); a++) {
                int peer = channelViews.keyAt(a);
                SparseIntArray messages = (SparseIntArray) channelViews.get(peer);
                for (int b = 0; b < messages.size(); b++) {
                    int views = messages.get(messages.keyAt(b));
                    long messageId = (long) messages.keyAt(b);
                    if (isChannel) {
                        messageId |= ((long) (-peer)) << 32;
                    }
                    state.requery();
                    state.bindLong(1, messageId);
                    state.bindInteger(2, views);
                    state.bindLong(3, messageId);
                    state.step();
                }
            }
            state.dispose();
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private boolean isValidKeyboardToSave(Message message) {
        return (message.reply_markup == null || (message.reply_markup instanceof TL_replyInlineMarkup) || (message.reply_markup.selective && !message.mentioned)) ? false : true;
    }

    private void putMessagesInternal(ArrayList<Message> messages, boolean withTransaction, boolean doNotUpdateDialogDate, int downloadMask, boolean ifNoLastMessage) {
        Message lastMessage;
        SQLiteCursor cursor;
        int a;
        Message message;
        long messageId;
        long mid;
        int type;
        long key;
        Integer count;
        if (ifNoLastMessage) {
            try {
                lastMessage = (Message) messages.get(0);
                if (lastMessage.dialog_id == 0) {
                    MessageObject.getDialogId(lastMessage);
                }
                int lastMid = -1;
                cursor = this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + lastMessage.dialog_id, new Object[0]);
                if (cursor.next()) {
                    lastMid = cursor.intValue(0);
                }
                cursor.dispose();
                if (lastMid != 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        if (withTransaction) {
            this.database.beginTransaction();
        }
        LongSparseArray<Message> messagesMap = new LongSparseArray();
        LongSparseArray<Integer> messagesCounts = new LongSparseArray();
        LongSparseArray<Integer> mentionCounts = new LongSparseArray();
        SparseArray<LongSparseArray<Integer>> mediaCounts = null;
        LongSparseArray<Message> botKeyboards = new LongSparseArray();
        LongSparseArray<Long> messagesMediaIdsMap = null;
        LongSparseArray<Integer> mediaTypesChange = null;
        StringBuilder messageMediaIds = null;
        LongSparseArray<Integer> mediaTypes = null;
        StringBuilder messageIds = new StringBuilder();
        LongSparseArray<Integer> dialogsReadMax = new LongSparseArray();
        LongSparseArray<Long> messagesIdsMap = new LongSparseArray();
        LongSparseArray<Long> mentionsIdsMap = new LongSparseArray();
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
        SQLitePreparedStatement state2 = null;
        SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO randoms VALUES(?, ?)");
        SQLitePreparedStatement state4 = this.database.executeFast("REPLACE INTO download_queue VALUES(?, ?, ?, ?, ?)");
        SQLitePreparedStatement state5 = this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
        for (a = 0; a < messages.size(); a++) {
            message = (Message) messages.get(a);
            messageId = (long) message.f104id;
            if (message.dialog_id == 0) {
                MessageObject.getDialogId(message);
            }
            if (message.to_id.channel_id != 0) {
                messageId |= ((long) message.to_id.channel_id) << 32;
            }
            if (message.mentioned && message.media_unread) {
                mentionsIdsMap.put(messageId, Long.valueOf(message.dialog_id));
            }
            if (!((message.action instanceof TL_messageActionHistoryClear) || MessageObject.isOut(message) || (message.f104id <= 0 && !MessageObject.isUnread(message)))) {
                Integer currentMaxId = (Integer) dialogsReadMax.get(message.dialog_id);
                if (currentMaxId == null) {
                    cursor = this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + message.dialog_id, new Object[0]);
                    if (cursor.next()) {
                        currentMaxId = Integer.valueOf(cursor.intValue(0));
                    } else {
                        currentMaxId = Integer.valueOf(0);
                    }
                    cursor.dispose();
                    dialogsReadMax.put(message.dialog_id, currentMaxId);
                }
                if (message.f104id < 0 || currentMaxId.intValue() < message.f104id) {
                    if (messageIds.length() > 0) {
                        messageIds.append(",");
                    }
                    messageIds.append(messageId);
                    messagesIdsMap.put(messageId, Long.valueOf(message.dialog_id));
                }
            }
            if (DataQuery.canAddMessageToMedia(message)) {
                if (messageMediaIds == null) {
                    messageMediaIds = new StringBuilder();
                    messagesMediaIdsMap = new LongSparseArray();
                    mediaTypes = new LongSparseArray();
                }
                if (messageMediaIds.length() > 0) {
                    messageMediaIds.append(",");
                }
                messageMediaIds.append(messageId);
                messagesMediaIdsMap.put(messageId, Long.valueOf(message.dialog_id));
                mediaTypes.put(messageId, Integer.valueOf(DataQuery.getMediaType(message)));
            }
            if (isValidKeyboardToSave(message)) {
                Message oldMessage = (Message) botKeyboards.get(message.dialog_id);
                if (oldMessage == null || oldMessage.f104id < message.f104id) {
                    botKeyboards.put(message.dialog_id, message);
                }
            }
        }
        for (a = 0; a < botKeyboards.size(); a++) {
            DataQuery.getInstance(this.currentAccount).putBotKeyboard(botKeyboards.keyAt(a), (Message) botKeyboards.valueAt(a));
        }
        if (messageMediaIds != null) {
            cursor = this.database.queryFinalized("SELECT mid, type FROM media_v2 WHERE mid IN(" + messageMediaIds.toString() + ")", new Object[0]);
            while (cursor.next()) {
                mid = cursor.longValue(0);
                type = cursor.intValue(1);
                if (type == ((Integer) mediaTypes.get(mid)).intValue()) {
                    messagesMediaIdsMap.remove(mid);
                } else {
                    if (mediaTypesChange == null) {
                        mediaTypesChange = new LongSparseArray();
                    }
                    mediaTypesChange.put(mid, Integer.valueOf(type));
                }
            }
            cursor.dispose();
            mediaCounts = new SparseArray();
            for (a = 0; a < messagesMediaIdsMap.size(); a++) {
                key = messagesMediaIdsMap.keyAt(a);
                long value = ((Long) messagesMediaIdsMap.valueAt(a)).longValue();
                Integer type2 = (Integer) mediaTypes.get(key);
                LongSparseArray<Integer> counts = (LongSparseArray) mediaCounts.get(type2.intValue());
                if (counts == null) {
                    counts = new LongSparseArray();
                    count = Integer.valueOf(0);
                    mediaCounts.put(type2.intValue(), counts);
                } else {
                    count = (Integer) counts.get(value);
                }
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                counts.put(value, Integer.valueOf(count.intValue() + 1));
                if (mediaTypesChange != null) {
                    int previousType = ((Integer) mediaTypesChange.get(key, Integer.valueOf(-1))).intValue();
                    if (previousType >= 0) {
                        counts = (LongSparseArray) mediaCounts.get(previousType);
                        if (counts == null) {
                            counts = new LongSparseArray();
                            count = Integer.valueOf(0);
                            mediaCounts.put(previousType, counts);
                        } else {
                            count = (Integer) counts.get(value);
                        }
                        if (count == null) {
                            count = Integer.valueOf(0);
                        }
                        counts.put(value, Integer.valueOf(count.intValue() - 1));
                    }
                }
            }
        }
        if (messageIds.length() > 0) {
            long dialog_id;
            cursor = this.database.queryFinalized("SELECT mid FROM messages WHERE mid IN(" + messageIds.toString() + ")", new Object[0]);
            while (cursor.next()) {
                mid = cursor.longValue(0);
                messagesIdsMap.remove(mid);
                mentionsIdsMap.remove(mid);
            }
            cursor.dispose();
            for (a = 0; a < messagesIdsMap.size(); a++) {
                dialog_id = ((Long) messagesIdsMap.valueAt(a)).longValue();
                count = (Integer) messagesCounts.get(dialog_id);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                messagesCounts.put(dialog_id, Integer.valueOf(count.intValue() + 1));
            }
            for (a = 0; a < mentionsIdsMap.size(); a++) {
                dialog_id = ((Long) mentionsIdsMap.valueAt(a)).longValue();
                count = (Integer) mentionCounts.get(dialog_id);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                mentionCounts.put(dialog_id, Integer.valueOf(count.intValue() + 1));
            }
        }
        int downloadMediaMask = 0;
        for (a = 0; a < messages.size(); a++) {
            message = (Message) messages.get(a);
            fixUnsupportedMedia(message);
            state.requery();
            messageId = (long) message.f104id;
            if (message.local_id != 0) {
                messageId = (long) message.local_id;
            }
            if (message.to_id.channel_id != 0) {
                messageId |= ((long) message.to_id.channel_id) << 32;
            }
            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
            message.serializeToStream(data);
            boolean updateDialog = true;
            if (!(message.action == null || !(message.action instanceof TL_messageEncryptedAction) || (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) || (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages))) {
                updateDialog = false;
            }
            if (updateDialog) {
                lastMessage = (Message) messagesMap.get(message.dialog_id);
                if (lastMessage == null || message.date > lastMessage.date || ((message.f104id > 0 && lastMessage.f104id > 0 && message.f104id > lastMessage.f104id) || (message.f104id < 0 && lastMessage.f104id < 0 && message.f104id < lastMessage.f104id))) {
                    messagesMap.put(message.dialog_id, message);
                }
            }
            state.bindLong(1, messageId);
            state.bindLong(2, message.dialog_id);
            state.bindInteger(3, MessageObject.getUnreadFlags(message));
            state.bindInteger(4, message.send_state);
            state.bindInteger(5, message.date);
            state.bindByteBuffer(6, data);
            state.bindInteger(7, MessageObject.isOut(message) ? 1 : 0);
            state.bindInteger(8, message.ttl);
            if ((message.flags & 1024) != 0) {
                state.bindInteger(9, message.views);
            } else {
                state.bindInteger(9, getMessageMediaType(message));
            }
            state.bindInteger(10, 0);
            state.bindInteger(11, message.mentioned ? 1 : 0);
            state.step();
            if (message.random_id != 0) {
                state3.requery();
                state3.bindLong(1, message.random_id);
                state3.bindLong(2, messageId);
                state3.step();
            }
            if (DataQuery.canAddMessageToMedia(message)) {
                if (state2 == null) {
                    state2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                }
                state2.requery();
                state2.bindLong(1, messageId);
                state2.bindLong(2, message.dialog_id);
                state2.bindInteger(3, message.date);
                state2.bindInteger(4, DataQuery.getMediaType(message));
                state2.bindByteBuffer(5, data);
                state2.step();
            }
            if (message.media instanceof TL_messageMediaWebPage) {
                state5.requery();
                state5.bindLong(1, message.media.webpage.f182id);
                state5.bindLong(2, messageId);
                state5.step();
            }
            data.reuse();
            if (downloadMask != 0 && ((message.to_id.channel_id == 0 || message.post) && message.date >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - 3600 && DownloadController.getInstance(this.currentAccount).canDownloadMedia(message) && ((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)))) {
                type = 0;
                long id = 0;
                MessageMedia object = null;
                if (MessageObject.isVoiceMessage(message)) {
                    id = message.media.document.f84id;
                    type = 2;
                    object = new TL_messageMediaDocument();
                    object.document = message.media.document;
                    object.flags |= 1;
                } else if (MessageObject.isRoundVideoMessage(message)) {
                    id = message.media.document.f84id;
                    type = 64;
                    object = new TL_messageMediaDocument();
                    object.document = message.media.document;
                    object.flags |= 1;
                } else if (MessageObject.isStickerMessage(message)) {
                    id = message.media.document.f84id;
                    type = 1;
                    object = new TL_messageMediaDocument();
                    object.document = message.media.document;
                    object.flags |= 1;
                } else if (message.media instanceof TL_messageMediaPhoto) {
                    if (FileLoader.getClosestPhotoSizeWithSize(message.media.photo.sizes, AndroidUtilities.getPhotoSize()) != null) {
                        id = message.media.photo.f106id;
                        type = 1;
                        object = new TL_messageMediaPhoto();
                        object.photo = message.media.photo;
                        object.flags |= 1;
                    }
                } else if (MessageObject.isVideoMessage(message)) {
                    id = message.media.document.f84id;
                    type = 4;
                    object = new TL_messageMediaDocument();
                    object.document = message.media.document;
                    object.flags |= 1;
                } else if (!(!(message.media instanceof TL_messageMediaDocument) || MessageObject.isMusicMessage(message) || MessageObject.isGifDocument(message.media.document))) {
                    id = message.media.document.f84id;
                    type = 8;
                    object = new TL_messageMediaDocument();
                    object.document = message.media.document;
                    object.flags |= 1;
                }
                if (object != null) {
                    if (message.media.ttl_seconds != 0) {
                        object.ttl_seconds = message.media.ttl_seconds;
                        object.flags |= 4;
                    }
                    downloadMediaMask |= type;
                    state4.requery();
                    data = new NativeByteBuffer(object.getObjectSize());
                    object.serializeToStream(data);
                    state4.bindLong(1, id);
                    state4.bindInteger(2, type);
                    state4.bindInteger(3, message.date);
                    state4.bindByteBuffer(4, data);
                    state4.bindString(5, "sent_" + (message.to_id != null ? message.to_id.channel_id : 0) + "_" + message.f104id);
                    state4.step();
                    data.reuse();
                }
            }
        }
        state.dispose();
        if (state2 != null) {
            state2.dispose();
        }
        state3.dispose();
        state4.dispose();
        state5.dispose();
        state = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (a = 0; a < messagesMap.size(); a++) {
            key = messagesMap.keyAt(a);
            if (key != 0) {
                message = (Message) messagesMap.valueAt(a);
                int channelId = 0;
                if (message != null) {
                    channelId = message.to_id.channel_id;
                }
                cursor = this.database.queryFinalized("SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i, flags FROM dialogs WHERE did = " + key, new Object[0]);
                int dialog_date = 0;
                int last_mid = 0;
                int old_unread_count = 0;
                int pts = channelId != 0 ? 1 : 0;
                int inbox_max = 0;
                int outbox_max = 0;
                int pinned = 0;
                int old_mentions_count = 0;
                int flags = 0;
                if (cursor.next()) {
                    dialog_date = cursor.intValue(0);
                    old_unread_count = Math.max(0, cursor.intValue(1));
                    pts = cursor.intValue(2);
                    last_mid = cursor.intValue(3);
                    inbox_max = cursor.intValue(4);
                    outbox_max = cursor.intValue(5);
                    pinned = cursor.intValue(6);
                    old_mentions_count = Math.max(0, cursor.intValue(7));
                    flags = cursor.intValue(8);
                } else if (channelId != 0) {
                    MessagesController.getInstance(this.currentAccount).checkChannelInviter(channelId);
                }
                cursor.dispose();
                Integer mentions_count = (Integer) mentionCounts.get(key);
                Integer unread_count = (Integer) messagesCounts.get(key);
                if (unread_count == null) {
                    unread_count = Integer.valueOf(0);
                } else {
                    messagesCounts.put(key, Integer.valueOf(unread_count.intValue() + old_unread_count));
                }
                if (mentions_count == null) {
                    mentions_count = Integer.valueOf(0);
                } else {
                    mentionCounts.put(key, Integer.valueOf(mentions_count.intValue() + old_mentions_count));
                }
                if (message != null) {
                    messageId = (long) message.f104id;
                } else {
                    messageId = (long) last_mid;
                }
                if (!(message == null || message.local_id == 0)) {
                    messageId = (long) message.local_id;
                }
                if (channelId != 0) {
                    messageId |= ((long) channelId) << 32;
                }
                state.requery();
                state.bindLong(1, key);
                if (message == null || (doNotUpdateDialogDate && dialog_date != 0)) {
                    state.bindInteger(2, dialog_date);
                } else {
                    state.bindInteger(2, message.date);
                }
                state.bindInteger(3, unread_count.intValue() + old_unread_count);
                state.bindLong(4, messageId);
                state.bindInteger(5, inbox_max);
                state.bindInteger(6, outbox_max);
                state.bindLong(7, 0);
                state.bindInteger(8, mentions_count.intValue() + old_mentions_count);
                state.bindInteger(9, pts);
                state.bindInteger(10, 0);
                state.bindInteger(11, pinned);
                state.bindInteger(12, flags);
                state.step();
            }
        }
        state.dispose();
        if (mediaCounts != null) {
            state3 = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            for (a = 0; a < mediaCounts.size(); a++) {
                type = mediaCounts.keyAt(a);
                LongSparseArray<Integer> value2 = (LongSparseArray) mediaCounts.valueAt(a);
                for (int b = 0; b < value2.size(); b++) {
                    long uid = value2.keyAt(b);
                    int lower_part = (int) uid;
                    int count2 = -1;
                    int old = 0;
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                    if (cursor.next()) {
                        count2 = cursor.intValue(0);
                        old = cursor.intValue(1);
                    }
                    cursor.dispose();
                    if (count2 != -1) {
                        state3.requery();
                        count2 += ((Integer) value2.valueAt(b)).intValue();
                        state3.bindLong(1, uid);
                        state3.bindInteger(2, type);
                        state3.bindInteger(3, Math.max(0, count2));
                        state3.bindInteger(4, old);
                        state3.step();
                    }
                }
            }
            state3.dispose();
        }
        if (withTransaction) {
            this.database.commitTransaction();
        }
        MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(messagesCounts, mentionCounts);
        if (downloadMediaMask != 0) {
            AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$81(this, downloadMediaMask));
        }
    }

    final /* synthetic */ void lambda$putMessagesInternal$109$MessagesStorage(int downloadMediaMaskFinal) {
        DownloadController.getInstance(this.currentAccount).newDownloadObjectsAvailable(downloadMediaMaskFinal);
    }

    public void putMessages(ArrayList<Message> messages, boolean withTransaction, boolean useQueue, boolean doNotUpdateDialogDate, int downloadMask) {
        putMessages(messages, withTransaction, useQueue, doNotUpdateDialogDate, downloadMask, false);
    }

    public void putMessages(ArrayList<Message> messages, boolean withTransaction, boolean useQueue, boolean doNotUpdateDialogDate, int downloadMask, boolean ifNoLastMessage) {
        if (messages.size() != 0) {
            if (useQueue) {
                this.storageQueue.postRunnable(new MessagesStorage$$Lambda$82(this, messages, withTransaction, doNotUpdateDialogDate, downloadMask, ifNoLastMessage));
            } else {
                putMessagesInternal(messages, withTransaction, doNotUpdateDialogDate, downloadMask, ifNoLastMessage);
            }
        }
    }

    public void markMessageAsSendError(Message message) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$83(this, message));
    }

    final /* synthetic */ void lambda$markMessageAsSendError$111$MessagesStorage(Message message) {
        try {
            long messageId = (long) message.f104id;
            if (message.to_id.channel_id != 0) {
                messageId |= ((long) message.to_id.channel_id) << 32;
            }
            this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid = " + messageId).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void setMessageSeq(int mid, int seq_in, int seq_out) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$84(this, mid, seq_in, seq_out));
    }

    final /* synthetic */ void lambda$setMessageSeq$112$MessagesStorage(int mid, int seq_in, int seq_out) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
            state.requery();
            state.bindInteger(1, mid);
            state.bindInteger(2, seq_in);
            state.bindInteger(3, seq_out);
            state.step();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private long[] updateMessageStateAndIdInternal(long random_id, Integer _oldId, int newId, int date, int channelId) {
        SQLiteCursor cursor = null;
        long newMessageId = (long) newId;
        if (_oldId == null) {
            try {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1", new Object[]{Long.valueOf(random_id)}), new Object[0]);
                if (cursor.next()) {
                    _oldId = Integer.valueOf(cursor.intValue(0));
                }
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.dispose();
                }
            }
            if (_oldId == null) {
                return null;
            }
        }
        long oldMessageId = (long) _oldId.intValue();
        if (channelId != 0) {
            oldMessageId |= ((long) channelId) << 32;
            newMessageId |= ((long) channelId) << 32;
        }
        long did = 0;
        try {
            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid FROM messages WHERE mid = %d LIMIT 1", new Object[]{Long.valueOf(oldMessageId)}), new Object[0]);
            if (cursor.next()) {
                did = cursor.longValue(0);
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th2) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        if (did == 0) {
            return null;
        }
        SQLitePreparedStatement state;
        if (oldMessageId != newMessageId || date == 0) {
            state = null;
            try {
                state = this.database.executeFast("UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?");
                state.bindLong(1, newMessageId);
                state.bindLong(2, oldMessageId);
                state.step();
                if (state != null) {
                    state.dispose();
                    state = null;
                }
            } catch (Exception e3) {
                try {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid = %d", new Object[]{Long.valueOf(oldMessageId)})).stepThis().dispose();
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new Object[]{Long.valueOf(oldMessageId)})).stepThis().dispose();
                } catch (Throwable e22) {
                    FileLog.m13e(e22);
                } catch (Throwable th3) {
                    if (state != null) {
                        state.dispose();
                    }
                }
                if (state != null) {
                    state.dispose();
                    state = null;
                }
            }
            try {
                state = this.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
                state.bindLong(1, newMessageId);
                state.bindLong(2, oldMessageId);
                state.step();
                if (state != null) {
                    state.dispose();
                    state = null;
                }
            } catch (Exception e4) {
                try {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new Object[]{Long.valueOf(oldMessageId)})).stepThis().dispose();
                } catch (Throwable e222) {
                    FileLog.m13e(e222);
                } catch (Throwable th4) {
                    if (state != null) {
                        state.dispose();
                    }
                }
                if (state != null) {
                    state.dispose();
                    state = null;
                }
            }
            try {
                state = this.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
                state.bindLong(1, newMessageId);
                state.bindLong(2, oldMessageId);
                state.step();
                if (state != null) {
                    state.dispose();
                }
            } catch (Throwable e23) {
                FileLog.m13e(e23);
                if (state != null) {
                    state.dispose();
                }
            } catch (Throwable th5) {
                if (state != null) {
                    state.dispose();
                }
            }
            return new long[]{did, (long) _oldId.intValue()};
        }
        state = null;
        try {
            state = this.database.executeFast("UPDATE messages SET send_state = 0, date = ? WHERE mid = ?");
            state.bindInteger(1, date);
            state.bindLong(2, newMessageId);
            state.step();
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable e232) {
            FileLog.m13e(e232);
            if (state != null) {
                state.dispose();
            }
        } catch (Throwable th6) {
            if (state != null) {
                state.dispose();
            }
        }
        return new long[]{did, (long) newId};
    }

    public long[] updateMessageStateAndId(long random_id, Integer _oldId, int newId, int date, boolean useQueue, int channelId) {
        if (!useQueue) {
            return updateMessageStateAndIdInternal(random_id, _oldId, newId, date, channelId);
        }
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$85(this, random_id, _oldId, newId, date, channelId));
        return null;
    }

    private void updateUsersInternal(ArrayList<User> users, boolean onlyStatus, boolean withTransaction) {
        Iterator it;
        User user;
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        } else if (onlyStatus) {
            if (withTransaction) {
                try {
                    this.database.beginTransaction();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                    return;
                }
            }
            SQLitePreparedStatement state = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
            it = users.iterator();
            while (it.hasNext()) {
                user = (User) it.next();
                state.requery();
                if (user.status != null) {
                    state.bindInteger(1, user.status.expires);
                } else {
                    state.bindInteger(1, 0);
                }
                state.bindInteger(2, user.f176id);
                state.step();
            }
            state.dispose();
            if (withTransaction) {
                this.database.commitTransaction();
            }
        } else {
            StringBuilder ids = new StringBuilder();
            SparseArray<User> usersDict = new SparseArray();
            it = users.iterator();
            while (it.hasNext()) {
                user = (User) it.next();
                if (ids.length() != 0) {
                    ids.append(",");
                }
                ids.append(user.f176id);
                usersDict.put(user.f176id, user);
            }
            ArrayList<User> loadedUsers = new ArrayList();
            getUsersInternal(ids.toString(), loadedUsers);
            it = loadedUsers.iterator();
            while (it.hasNext()) {
                user = (User) it.next();
                User updateUser = (User) usersDict.get(user.f176id);
                if (updateUser != null) {
                    if (updateUser.first_name != null && updateUser.last_name != null) {
                        if (!UserObject.isContact(user)) {
                            user.first_name = updateUser.first_name;
                            user.last_name = updateUser.last_name;
                        }
                        user.username = updateUser.username;
                    } else if (updateUser.photo != null) {
                        user.photo = updateUser.photo;
                    } else if (updateUser.phone != null) {
                        user.phone = updateUser.phone;
                    }
                }
            }
            if (!loadedUsers.isEmpty()) {
                if (withTransaction) {
                    this.database.beginTransaction();
                }
                putUsersInternal(loadedUsers);
                if (withTransaction) {
                    this.database.commitTransaction();
                }
            }
        }
    }

    public void updateUsers(ArrayList<User> users, boolean onlyStatus, boolean withTransaction, boolean useQueue) {
        if (users != null && !users.isEmpty()) {
            if (useQueue) {
                this.storageQueue.postRunnable(new MessagesStorage$$Lambda$86(this, users, onlyStatus, withTransaction));
            } else {
                updateUsersInternal(users, onlyStatus, withTransaction);
            }
        }
    }

    private void markMessagesAsReadInternal(SparseLongArray inbox, SparseLongArray outbox, SparseIntArray encryptedMessages) {
        try {
            SQLitePreparedStatement state;
            int b;
            int key;
            long messageId;
            if (!isEmpty(inbox)) {
                state = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                for (b = 0; b < inbox.size(); b++) {
                    key = inbox.keyAt(b);
                    messageId = inbox.get(key);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(key), Long.valueOf(messageId)})).stepThis().dispose();
                    state.requery();
                    state.bindLong(1, (long) key);
                    state.bindLong(2, messageId);
                    state.step();
                }
                state.dispose();
            }
            if (!isEmpty(outbox)) {
                for (b = 0; b < outbox.size(); b++) {
                    messageId = outbox.get(outbox.keyAt(b));
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[]{Integer.valueOf(key), Long.valueOf(messageId)})).stepThis().dispose();
                }
            }
            if (encryptedMessages != null && !isEmpty(encryptedMessages)) {
                for (int a = 0; a < encryptedMessages.size(); a++) {
                    long dialog_id = ((long) encryptedMessages.keyAt(a)) << 32;
                    int max_date = encryptedMessages.valueAt(a);
                    state = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                    state.requery();
                    state.bindLong(1, dialog_id);
                    state.bindInteger(2, max_date);
                    state.step();
                    state.dispose();
                }
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void markMessagesContentAsRead(ArrayList<Long> mids, int date) {
        if (!isEmpty((List) mids)) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$87(this, mids, date));
        }
    }

    final /* synthetic */ void lambda$markMessagesContentAsRead$115$MessagesStorage(ArrayList mids, int date) {
        try {
            String midsStr = TextUtils.join(",", mids);
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[]{midsStr})).stepThis().dispose();
            if (date != 0) {
                SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE mid IN (%s) AND ttl > 0", new Object[]{midsStr}), new Object[0]);
                ArrayList<Integer> arrayList = null;
                while (cursor.next()) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(cursor.intValue(0)));
                }
                if (arrayList != null) {
                    emptyMessagesMedia(arrayList);
                }
                cursor.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void markMessagesAsRead(SparseLongArray inbox, SparseLongArray outbox, SparseIntArray encryptedMessages, boolean useQueue) {
        if (useQueue) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$88(this, inbox, outbox, encryptedMessages));
        } else {
            markMessagesAsReadInternal(inbox, outbox, encryptedMessages);
        }
    }

    public void markMessagesAsDeletedByRandoms(ArrayList<Long> messages) {
        if (!messages.isEmpty()) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$89(this, messages));
        }
    }

    final /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$118$MessagesStorage(ArrayList messages) {
        try {
            String ids = TextUtils.join(",", messages);
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{ids}), new Object[0]);
            ArrayList mids = new ArrayList();
            while (cursor.next()) {
                mids.add(Integer.valueOf(cursor.intValue(0)));
            }
            cursor.dispose();
            if (!mids.isEmpty()) {
                AndroidUtilities.runOnUIThread(new MessagesStorage$$Lambda$104(this, mids));
                updateDialogsWithReadMessagesInternal(mids, null, null, null);
                markMessagesAsDeletedInternal(mids, 0);
                updateDialogsWithDeletedMessagesInternal(mids, null, 0);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$117$MessagesStorage(ArrayList mids) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, mids, Integer.valueOf(0));
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(ArrayList<Integer> messages, int channelId) {
        try {
            int a;
            String ids;
            long did;
            SQLitePreparedStatement state;
            ArrayList<Integer> arrayList = new ArrayList(messages);
            ArrayList<Long> arrayList2 = new ArrayList();
            LongSparseArray<Integer[]> dialogsToUpdate = new LongSparseArray();
            if (channelId != 0) {
                StringBuilder builder = new StringBuilder(messages.size());
                for (a = 0; a < messages.size(); a++) {
                    long messageId = ((long) ((Integer) messages.get(a)).intValue()) | (((long) channelId) << 32);
                    if (builder.length() > 0) {
                        builder.append(',');
                    }
                    builder.append(messageId);
                }
                ids = builder.toString();
            } else {
                ids = TextUtils.join(",", messages);
            }
            ArrayList<File> filesToDelete = new ArrayList();
            int currentUser = UserConfig.getInstance(this.currentAccount).getClientUserId();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", new Object[]{ids}), new Object[0]);
            while (cursor.next()) {
                try {
                    did = cursor.longValue(0);
                    arrayList.remove(Integer.valueOf(cursor.intValue(5)));
                    if (did != ((long) currentUser)) {
                        int read_state = cursor.intValue(2);
                        if (cursor.intValue(3) == 0) {
                            Integer num;
                            Integer[] unread_count = (Integer[]) dialogsToUpdate.get(did);
                            if (unread_count == null) {
                                unread_count = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                dialogsToUpdate.put(did, unread_count);
                            }
                            if (read_state < 2) {
                                num = unread_count[1];
                                unread_count[1] = Integer.valueOf(unread_count[1].intValue() + 1);
                            }
                            if (read_state == 0 || read_state == 2) {
                                num = unread_count[0];
                                unread_count[0] = Integer.valueOf(unread_count[0].intValue() + 1);
                            }
                        }
                        if (((int) did) == 0) {
                            NativeByteBuffer data = cursor.byteBufferValue(1);
                            if (data != null) {
                                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                data.reuse();
                                if (message != null) {
                                    File file;
                                    if (message.media instanceof TL_messageMediaPhoto) {
                                        Iterator it = message.media.photo.sizes.iterator();
                                        while (it.hasNext()) {
                                            file = FileLoader.getPathToAttach((PhotoSize) it.next());
                                            if (file != null && file.toString().length() > 0) {
                                                filesToDelete.add(file);
                                            }
                                        }
                                    } else if (message.media instanceof TL_messageMediaDocument) {
                                        file = FileLoader.getPathToAttach(message.media.document);
                                        if (file != null && file.toString().length() > 0) {
                                            filesToDelete.add(file);
                                        }
                                        file = FileLoader.getPathToAttach(message.media.document.thumb);
                                        if (file != null && file.toString().length() > 0) {
                                            filesToDelete.add(file);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
            FileLoader.getInstance(this.currentAccount).deleteFiles(filesToDelete, 0);
            for (a = 0; a < dialogsToUpdate.size(); a++) {
                did = dialogsToUpdate.keyAt(a);
                Integer[] counts = (Integer[]) dialogsToUpdate.valueAt(a);
                cursor = this.database.queryFinalized("SELECT unread_count, unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
                int old_unread_count = 0;
                int old_mentions_count = 0;
                if (cursor.next()) {
                    old_unread_count = cursor.intValue(0);
                    old_mentions_count = cursor.intValue(1);
                }
                cursor.dispose();
                arrayList2.add(Long.valueOf(did));
                state = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                state.requery();
                state.bindInteger(1, Math.max(0, old_unread_count - counts[0].intValue()));
                state.bindInteger(2, Math.max(0, old_mentions_count - counts[1].intValue()));
                state.bindLong(3, did);
                state.step();
                state.dispose();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{ids})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{ids})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{ids})).stepThis().dispose();
            if (arrayList.isEmpty()) {
                long uid;
                int type;
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type FROM media_v2 WHERE mid IN(%s)", new Object[]{ids}), new Object[0]);
                SparseArray<LongSparseArray<Integer>> mediaCounts = null;
                while (cursor.next()) {
                    Integer count;
                    uid = cursor.longValue(0);
                    type = cursor.intValue(1);
                    if (mediaCounts == null) {
                        mediaCounts = new SparseArray();
                    }
                    LongSparseArray<Integer> counts2 = (LongSparseArray) mediaCounts.get(type);
                    if (counts2 == null) {
                        counts2 = new LongSparseArray();
                        count = Integer.valueOf(0);
                        mediaCounts.put(type, counts2);
                    } else {
                        count = (Integer) counts2.get(uid);
                    }
                    if (count == null) {
                        count = Integer.valueOf(0);
                    }
                    counts2.put(uid, Integer.valueOf(count.intValue() + 1));
                }
                cursor.dispose();
                if (mediaCounts != null) {
                    state = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
                    for (a = 0; a < mediaCounts.size(); a++) {
                        type = mediaCounts.keyAt(a);
                        LongSparseArray<Integer> value = (LongSparseArray) mediaCounts.valueAt(a);
                        for (int b = 0; b < value.size(); b++) {
                            uid = value.keyAt(b);
                            int lower_part = (int) uid;
                            int count2 = -1;
                            int old = 0;
                            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                            if (cursor.next()) {
                                count2 = cursor.intValue(0);
                                old = cursor.intValue(1);
                            }
                            cursor.dispose();
                            if (count2 != -1) {
                                state.requery();
                                count2 = Math.max(0, count2 - ((Integer) value.valueAt(b)).intValue());
                                state.bindLong(1, uid);
                                state.bindInteger(2, type);
                                state.bindInteger(3, count2);
                                state.bindInteger(4, old);
                                state.step();
                            }
                        }
                    }
                    state.dispose();
                }
            } else if (channelId == 0) {
                this.database.executeFast("UPDATE media_counts_v2 SET old = 1 WHERE 1").stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d", new Object[]{Integer.valueOf(-channelId)})).stepThis().dispose();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{ids})).stepThis().dispose();
            DataQuery.getInstance(this.currentAccount).clearBotKeyboard(0, messages);
            return arrayList2;
        } catch (Throwable e2) {
            FileLog.m13e(e2);
            return null;
        }
    }

    private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> messages, ArrayList<Long> additionalDialogsToUpdate, int channelId) {
        if (Thread.currentThread().getId() != this.storageQueue.getId()) {
            throw new RuntimeException("wrong db thread");
        }
        try {
            String ids;
            SQLiteCursor cursor;
            int a;
            ArrayList<Long> dialogsToUpdate = new ArrayList();
            if (messages.isEmpty()) {
                dialogsToUpdate.add(Long.valueOf((long) (-channelId)));
            } else {
                SQLitePreparedStatement state;
                if (channelId != 0) {
                    dialogsToUpdate.add(Long.valueOf((long) (-channelId)));
                    state = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?");
                } else {
                    ids = TextUtils.join(",", messages);
                    cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[]{ids}), new Object[0]);
                    while (cursor.next()) {
                        dialogsToUpdate.add(Long.valueOf(cursor.longValue(0)));
                    }
                    cursor.dispose();
                    state = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.database.beginTransaction();
                for (a = 0; a < dialogsToUpdate.size(); a++) {
                    long did = ((Long) dialogsToUpdate.get(a)).longValue();
                    state.requery();
                    state.bindLong(1, did);
                    state.bindLong(2, did);
                    state.bindLong(3, did);
                    state.step();
                }
                state.dispose();
                this.database.commitTransaction();
            }
            if (additionalDialogsToUpdate != null) {
                for (a = 0; a < additionalDialogsToUpdate.size(); a++) {
                    Long did2 = (Long) additionalDialogsToUpdate.get(a);
                    if (!dialogsToUpdate.contains(did2)) {
                        dialogsToUpdate.add(did2);
                    }
                }
            }
            ids = TextUtils.join(",", dialogsToUpdate);
            messages_Dialogs dialogs = new TL_messages_dialogs();
            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            ArrayList<Integer> encryptedToLoad = new ArrayList();
            cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i, d.flags FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[]{ids}), new Object[0]);
            while (cursor.next()) {
                TL_dialog dialog = new TL_dialog();
                dialog.f128id = cursor.longValue(0);
                dialog.top_message = cursor.intValue(1);
                dialog.read_inbox_max_id = cursor.intValue(10);
                dialog.read_outbox_max_id = cursor.intValue(11);
                dialog.unread_count = cursor.intValue(2);
                dialog.unread_mentions_count = cursor.intValue(13);
                dialog.last_message_date = cursor.intValue(3);
                dialog.pts = cursor.intValue(9);
                dialog.flags = channelId == 0 ? 0 : 1;
                dialog.pinnedNum = cursor.intValue(12);
                dialog.pinned = dialog.pinnedNum != 0;
                dialog.unread_mark = (cursor.intValue(14) & 1) != 0;
                dialogs.dialogs.add(dialog);
                NativeByteBuffer data = cursor.byteBufferValue(4);
                if (data != null) {
                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    MessageObject.setUnreadFlags(message, cursor.intValue(5));
                    message.f104id = cursor.intValue(6);
                    message.send_state = cursor.intValue(7);
                    int date = cursor.intValue(8);
                    if (date != 0) {
                        dialog.last_message_date = date;
                    }
                    message.dialog_id = dialog.f128id;
                    dialogs.messages.add(message);
                    addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                }
                int lower_id = (int) dialog.f128id;
                int high_id = (int) (dialog.f128id >> 32);
                if (lower_id != 0) {
                    if (high_id == 1) {
                        if (!chatsToLoad.contains(Integer.valueOf(lower_id))) {
                            chatsToLoad.add(Integer.valueOf(lower_id));
                        }
                    } else if (lower_id > 0) {
                        if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                            usersToLoad.add(Integer.valueOf(lower_id));
                        }
                    } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                        chatsToLoad.add(Integer.valueOf(-lower_id));
                    }
                } else if (!encryptedToLoad.contains(Integer.valueOf(high_id))) {
                    encryptedToLoad.add(Integer.valueOf(high_id));
                }
            }
            cursor.dispose();
            if (!encryptedToLoad.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), dialogs.chats);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), dialogs.users);
            }
            if (!dialogs.dialogs.isEmpty() || !encryptedChats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).processDialogsUpdate(dialogs, encryptedChats);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void updateDialogsWithDeletedMessages(ArrayList<Integer> messages, ArrayList<Long> additionalDialogsToUpdate, boolean useQueue, int channelId) {
        if (!messages.isEmpty() || channelId != 0) {
            if (useQueue) {
                this.storageQueue.postRunnable(new MessagesStorage$$Lambda$90(this, messages, additionalDialogsToUpdate, channelId));
            } else {
                updateDialogsWithDeletedMessagesInternal(messages, additionalDialogsToUpdate, channelId);
            }
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(ArrayList<Integer> messages, boolean useQueue, int channelId) {
        if (messages.isEmpty()) {
            return null;
        }
        if (!useQueue) {
            return markMessagesAsDeletedInternal((ArrayList) messages, channelId);
        }
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$91(this, messages, channelId));
        return null;
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(int channelId, int mid) {
        try {
            long did;
            ArrayList<Long> dialogsIds = new ArrayList();
            LongSparseArray<Integer[]> dialogsToUpdate = new LongSparseArray();
            long maxMessageId = ((long) mid) | (((long) channelId) << 32);
            ArrayList<File> filesToDelete = new ArrayList();
            int currentUser = UserConfig.getInstance(this.currentAccount).getClientUserId();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(-channelId), Long.valueOf(maxMessageId)}), new Object[0]);
            while (cursor.next()) {
                try {
                    did = cursor.longValue(0);
                    if (did != ((long) currentUser)) {
                        int read_state = cursor.intValue(2);
                        if (cursor.intValue(3) == 0) {
                            Integer num;
                            Integer[] unread_count = (Integer[]) dialogsToUpdate.get(did);
                            if (unread_count == null) {
                                unread_count = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                dialogsToUpdate.put(did, unread_count);
                            }
                            if (read_state < 2) {
                                num = unread_count[1];
                                unread_count[1] = Integer.valueOf(unread_count[1].intValue() + 1);
                            }
                            if (read_state == 0 || read_state == 2) {
                                num = unread_count[0];
                                unread_count[0] = Integer.valueOf(unread_count[0].intValue() + 1);
                            }
                        }
                        if (((int) did) == 0) {
                            NativeByteBuffer data = cursor.byteBufferValue(1);
                            if (data != null) {
                                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                data.reuse();
                                if (message != null) {
                                    File file;
                                    if (message.media instanceof TL_messageMediaPhoto) {
                                        Iterator it = message.media.photo.sizes.iterator();
                                        while (it.hasNext()) {
                                            file = FileLoader.getPathToAttach((PhotoSize) it.next());
                                            if (file != null && file.toString().length() > 0) {
                                                filesToDelete.add(file);
                                            }
                                        }
                                    } else if (message.media instanceof TL_messageMediaDocument) {
                                        file = FileLoader.getPathToAttach(message.media.document);
                                        if (file != null && file.toString().length() > 0) {
                                            filesToDelete.add(file);
                                        }
                                        file = FileLoader.getPathToAttach(message.media.document.thumb);
                                        if (file != null && file.toString().length() > 0) {
                                            filesToDelete.add(file);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            cursor.dispose();
            FileLoader.getInstance(this.currentAccount).deleteFiles(filesToDelete, 0);
            for (int a = 0; a < dialogsToUpdate.size(); a++) {
                did = dialogsToUpdate.keyAt(a);
                Integer[] counts = (Integer[]) dialogsToUpdate.valueAt(a);
                cursor = this.database.queryFinalized("SELECT unread_count, unread_count_i FROM dialogs WHERE did = " + did, new Object[0]);
                int old_unread_count = 0;
                int old_mentions_count = 0;
                if (cursor.next()) {
                    old_unread_count = cursor.intValue(0);
                    old_mentions_count = cursor.intValue(1);
                }
                cursor.dispose();
                dialogsIds.add(Long.valueOf(did));
                SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                state.requery();
                state.bindInteger(1, Math.max(0, old_unread_count - counts[0].intValue()));
                state.bindInteger(2, Math.max(0, old_mentions_count - counts[1].intValue()));
                state.bindLong(3, did);
                state.step();
                state.dispose();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(-channelId), Long.valueOf(maxMessageId)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(-channelId), Long.valueOf(maxMessageId)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d", new Object[]{Integer.valueOf(-channelId)})).stepThis().dispose();
            return dialogsIds;
        } catch (Throwable e2) {
            FileLog.m13e(e2);
            return null;
        }
    }

    public ArrayList<Long> markMessagesAsDeleted(int channelId, int mid, boolean useQueue) {
        if (!useQueue) {
            return markMessagesAsDeletedInternal(channelId, mid);
        }
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$92(this, channelId, mid));
        return null;
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            if (message.media instanceof TL_messageMediaUnsupported_old) {
                if (message.media.bytes.length == 0) {
                    message.media.bytes = new byte[1];
                    message.media.bytes[0] = (byte) 89;
                }
            } else if (message.media instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                message.media.bytes = new byte[1];
                message.media.bytes[0] = (byte) 89;
                message.flags |= 512;
            }
        }
    }

    private void doneHolesInTable(String table, long did, int max_id) throws Exception {
        if (max_id == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d", new Object[]{Long.valueOf(did)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(did)})).stepThis().dispose();
        }
        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO " + table + " VALUES(?, ?, ?)");
        state.requery();
        state.bindLong(1, did);
        state.bindInteger(2, 1);
        state.bindInteger(3, 1);
        state.step();
        state.dispose();
    }

    public void doneHolesInMedia(long did, int max_id, int type) throws Exception {
        SQLitePreparedStatement state;
        if (type == -1) {
            if (max_id == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(did)})).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(did)})).stepThis().dispose();
            }
            state = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            for (int a = 0; a < 5; a++) {
                state.requery();
                state.bindLong(1, did);
                state.bindInteger(2, a);
                state.bindInteger(3, 1);
                state.bindInteger(4, 1);
                state.step();
            }
            state.dispose();
            return;
        }
        if (max_id == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
        }
        state = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        state.requery();
        state.bindLong(1, did);
        state.bindInteger(2, type);
        state.bindInteger(3, 1);
        state.bindInteger(4, 1);
        state.step();
        state.dispose();
    }

    public void closeHolesInMedia(long did, int minId, int maxId, int type) {
        SQLiteCursor cursor;
        if (type < 0) {
            try {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(did), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)}), new Object[0]);
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(did), Integer.valueOf(type), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)}), new Object[0]);
        ArrayList<Hole> holes = null;
        while (cursor.next()) {
            if (holes == null) {
                holes = new ArrayList();
            }
            int holeType = cursor.intValue(0);
            int start = cursor.intValue(1);
            int end = cursor.intValue(2);
            if (start != end || start != 1) {
                holes.add(new Hole(holeType, start, end));
            }
        }
        cursor.dispose();
        if (holes != null) {
            for (int a = 0; a < holes.size(); a++) {
                Hole hole = (Hole) holes.get(a);
                if (maxId >= hole.end - 1 && minId <= hole.start + 1) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                } else if (maxId >= hole.end - 1) {
                    if (hole.end != minId) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(minId), Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                        }
                    }
                } else if (minId > hole.start + 1) {
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    state.requery();
                    state.bindLong(1, did);
                    state.bindInteger(2, hole.type);
                    state.bindInteger(3, hole.start);
                    state.bindInteger(4, minId);
                    state.step();
                    state.requery();
                    state.bindLong(1, did);
                    state.bindInteger(2, hole.type);
                    state.bindInteger(3, maxId);
                    state.bindInteger(4, hole.end);
                    state.step();
                    state.dispose();
                } else if (hole.start != maxId) {
                    try {
                        this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(maxId), Long.valueOf(did), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                    }
                }
            }
        }
    }

    private void closeHolesInTable(String table, long did, int minId, int maxId) {
        try {
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + table + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(did), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId), Integer.valueOf(minId), Integer.valueOf(maxId)}), new Object[0]);
            ArrayList<Hole> holes = null;
            while (cursor.next()) {
                if (holes == null) {
                    holes = new ArrayList();
                }
                int start = cursor.intValue(0);
                int end = cursor.intValue(1);
                if (start != end || start != 1) {
                    holes.add(new Hole(start, end));
                }
            }
            cursor.dispose();
            if (holes != null) {
                for (int a = 0; a < holes.size(); a++) {
                    Hole hole = (Hole) holes.get(a);
                    if (maxId >= hole.end - 1 && minId <= hole.start + 1) {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    } else if (maxId >= hole.end - 1) {
                        if (hole.end != minId) {
                            try {
                                this.database.executeFast(String.format(Locale.US, "UPDATE " + table + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(minId), Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Throwable e) {
                                FileLog.m13e(e);
                            }
                        }
                    } else if (minId > hole.start + 1) {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM " + table + " WHERE uid = %d AND start = %d AND end = %d", new Object[]{Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO " + table + " VALUES(?, ?, ?)");
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, hole.start);
                        state.bindInteger(3, minId);
                        state.step();
                        state.requery();
                        state.bindLong(1, did);
                        state.bindInteger(2, maxId);
                        state.bindInteger(3, hole.end);
                        state.step();
                        state.dispose();
                    } else if (hole.start != maxId) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE " + table + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(maxId), Long.valueOf(did), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                        }
                    }
                }
            }
        } catch (Throwable e22) {
            FileLog.m13e(e22);
        }
    }

    public void replaceMessageIfExists(Message message) {
        if (message != null) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$93(this, message));
        }
    }

    final /* synthetic */ void lambda$replaceMessageIfExists$122$MessagesStorage(Message message) {
        try {
            long messageId = (long) message.f104id;
            int channelId = 0;
            if (null == null) {
                channelId = message.to_id.channel_id;
            }
            if (message.to_id.channel_id != 0) {
                messageId |= ((long) channelId) << 32;
            }
            SQLiteCursor cursor = null;
            try {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid FROM messages WHERE mid = %d LIMIT 1", new Object[]{Long.valueOf(messageId)}), new Object[0]);
                if (cursor.next()) {
                    if (cursor != null) {
                        cursor.dispose();
                    }
                    this.database.beginTransaction();
                    SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                    SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    if (message.dialog_id == 0) {
                        MessageObject.getDialogId(message);
                    }
                    fixUnsupportedMedia(message);
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, messageId);
                    state.bindLong(2, message.dialog_id);
                    state.bindInteger(3, MessageObject.getUnreadFlags(message));
                    state.bindInteger(4, message.send_state);
                    state.bindInteger(5, message.date);
                    state.bindByteBuffer(6, data);
                    state.bindInteger(7, MessageObject.isOut(message) ? 1 : 0);
                    state.bindInteger(8, message.ttl);
                    if ((message.flags & 1024) != 0) {
                        state.bindInteger(9, message.views);
                    } else {
                        state.bindInteger(9, getMessageMediaType(message));
                    }
                    state.bindInteger(10, 0);
                    state.bindInteger(11, message.mentioned ? 1 : 0);
                    state.step();
                    if (DataQuery.canAddMessageToMedia(message)) {
                        state2.requery();
                        state2.bindLong(1, messageId);
                        state2.bindLong(2, message.dialog_id);
                        state2.bindInteger(3, message.date);
                        state2.bindInteger(4, DataQuery.getMediaType(message));
                        state2.bindByteBuffer(5, data);
                        state2.step();
                    }
                    data.reuse();
                    state.dispose();
                    state2.dispose();
                    this.database.commitTransaction();
                } else if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.dispose();
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
    }

    public void putMessages(messages_Messages messages, long dialog_id, int load_type, int max_id, boolean createDialog) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$94(this, messages, load_type, dialog_id, max_id, createDialog));
    }

    final /* synthetic */ void lambda$putMessages$123$MessagesStorage(messages_Messages messages, int load_type, long dialog_id, int max_id, boolean createDialog) {
        int mentionCountUpdate = ConnectionsManager.DEFAULT_DATACENTER_ID;
        try {
            if (!messages.messages.isEmpty()) {
                this.database.beginTransaction();
                int minId;
                int maxId;
                if (load_type == 0) {
                    minId = ((Message) messages.messages.get(messages.messages.size() - 1)).f104id;
                    closeHolesInTable("messages_holes", dialog_id, minId, max_id);
                    closeHolesInMedia(dialog_id, minId, max_id, -1);
                } else if (load_type == 1) {
                    maxId = ((Message) messages.messages.get(0)).f104id;
                    closeHolesInTable("messages_holes", dialog_id, max_id, maxId);
                    closeHolesInMedia(dialog_id, max_id, maxId, -1);
                } else if (load_type == 3 || load_type == 2 || load_type == 4) {
                    if (max_id != 0 || load_type == 4) {
                        maxId = ((Message) messages.messages.get(0)).f104id;
                    } else {
                        maxId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    minId = ((Message) messages.messages.get(messages.messages.size() - 1)).f104id;
                    closeHolesInTable("messages_holes", dialog_id, minId, maxId);
                    closeHolesInMedia(dialog_id, minId, maxId, -1);
                }
                int count = messages.messages.size();
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                SQLitePreparedStatement state5 = null;
                Message botKeyboard = null;
                int channelId = 0;
                for (int a = 0; a < count; a++) {
                    SQLiteCursor cursor;
                    Message message = (Message) messages.messages.get(a);
                    long messageId = (long) message.f104id;
                    if (channelId == 0) {
                        channelId = message.to_id.channel_id;
                    }
                    if (message.to_id.channel_id != 0) {
                        messageId |= ((long) channelId) << 32;
                    }
                    if (load_type == -2) {
                        cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl, mention, read_state, send_state FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageId)}), new Object[0]);
                        boolean exist = cursor.next();
                        if (exist) {
                            AbstractSerializedData data = cursor.byteBufferValue(1);
                            if (data != null) {
                                Message oldMessage = Message.TLdeserialize(data, data.readInt32(false), false);
                                oldMessage.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                data.reuse();
                                int send_state = cursor.intValue(5);
                                if (!(oldMessage == null || send_state == 3)) {
                                    message.attachPath = oldMessage.attachPath;
                                    message.ttl = cursor.intValue(2);
                                }
                            }
                            boolean oldMention = cursor.intValue(3) != 0;
                            int readState = cursor.intValue(4);
                            if (oldMention != message.mentioned) {
                                if (mentionCountUpdate == Integer.MAX_VALUE) {
                                    SQLiteCursor cursor2 = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + dialog_id, new Object[0]);
                                    if (cursor2.next()) {
                                        mentionCountUpdate = cursor2.intValue(0);
                                    }
                                    cursor2.dispose();
                                }
                                if (oldMention) {
                                    if (readState <= 1) {
                                        mentionCountUpdate--;
                                    }
                                } else if (message.media_unread) {
                                    mentionCountUpdate++;
                                }
                            }
                        }
                        cursor.dispose();
                        if (!exist) {
                        }
                    }
                    if (a == 0 && createDialog) {
                        int pinned = 0;
                        int mentions = 0;
                        int flags = 0;
                        cursor = this.database.queryFinalized("SELECT pinned, unread_count_i, flags FROM dialogs WHERE did = " + dialog_id, new Object[0]);
                        if (cursor.next()) {
                            pinned = cursor.intValue(0);
                            mentions = cursor.intValue(1);
                            flags = cursor.intValue(2);
                        }
                        cursor.dispose();
                        SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        state3.bindLong(1, dialog_id);
                        state3.bindInteger(2, message.date);
                        state3.bindInteger(3, 0);
                        state3.bindLong(4, messageId);
                        state3.bindInteger(5, message.f104id);
                        state3.bindInteger(6, 0);
                        state3.bindLong(7, messageId);
                        state3.bindInteger(8, mentions);
                        state3.bindInteger(9, messages.pts);
                        state3.bindInteger(10, message.date);
                        state3.bindInteger(11, pinned);
                        state3.bindInteger(12, flags);
                        state3.step();
                        state3.dispose();
                    }
                    fixUnsupportedMedia(message);
                    state.requery();
                    AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    state.bindLong(1, messageId);
                    state.bindLong(2, dialog_id);
                    state.bindInteger(3, MessageObject.getUnreadFlags(message));
                    state.bindInteger(4, message.send_state);
                    state.bindInteger(5, message.date);
                    state.bindByteBuffer(6, (NativeByteBuffer) nativeByteBuffer);
                    state.bindInteger(7, MessageObject.isOut(message) ? 1 : 0);
                    state.bindInteger(8, message.ttl);
                    if ((message.flags & 1024) != 0) {
                        state.bindInteger(9, message.views);
                    } else {
                        state.bindInteger(9, getMessageMediaType(message));
                    }
                    state.bindInteger(10, 0);
                    state.bindInteger(11, message.mentioned ? 1 : 0);
                    state.step();
                    if (DataQuery.canAddMessageToMedia(message)) {
                        state2.requery();
                        state2.bindLong(1, messageId);
                        state2.bindLong(2, dialog_id);
                        state2.bindInteger(3, message.date);
                        state2.bindInteger(4, DataQuery.getMediaType(message));
                        state2.bindByteBuffer(5, (NativeByteBuffer) nativeByteBuffer);
                        state2.step();
                    }
                    nativeByteBuffer.reuse();
                    if (message.media instanceof TL_messageMediaWebPage) {
                        if (state5 == null) {
                            state5 = this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
                        }
                        state5.requery();
                        state5.bindLong(1, message.media.webpage.f182id);
                        state5.bindLong(2, messageId);
                        state5.step();
                    }
                    if (load_type == 0 && isValidKeyboardToSave(message) && (botKeyboard == null || botKeyboard.f104id < message.f104id)) {
                        botKeyboard = message;
                    }
                }
                state.dispose();
                state2.dispose();
                if (state5 != null) {
                    state5.dispose();
                }
                if (botKeyboard != null) {
                    DataQuery.getInstance(this.currentAccount).putBotKeyboard(dialog_id, botKeyboard);
                }
                putUsersInternal(messages.users);
                putChatsInternal(messages.chats);
                if (mentionCountUpdate != Integer.MAX_VALUE) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(mentionCountUpdate), Long.valueOf(dialog_id)})).stepThis().dispose();
                    LongSparseArray<Integer> longSparseArray = new LongSparseArray(1);
                    longSparseArray.put(dialog_id, Integer.valueOf(mentionCountUpdate));
                    MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(null, longSparseArray);
                }
                this.database.commitTransaction();
                if (createDialog) {
                    updateDialogsWithDeletedMessages(new ArrayList(), null, false, channelId);
                }
            } else if (load_type == 0) {
                doneHolesInTable("messages_holes", dialog_id, max_id);
                doneHolesInMedia(dialog_id, max_id, -1);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public static void addUsersAndChatsFromMessage(Message message, ArrayList<Integer> usersToLoad, ArrayList<Integer> chatsToLoad) {
        int a;
        if (message.from_id != 0) {
            if (message.from_id > 0) {
                if (!usersToLoad.contains(Integer.valueOf(message.from_id))) {
                    usersToLoad.add(Integer.valueOf(message.from_id));
                }
            } else if (!chatsToLoad.contains(Integer.valueOf(-message.from_id))) {
                chatsToLoad.add(Integer.valueOf(-message.from_id));
            }
        }
        if (!(message.via_bot_id == 0 || usersToLoad.contains(Integer.valueOf(message.via_bot_id)))) {
            usersToLoad.add(Integer.valueOf(message.via_bot_id));
        }
        if (message.action != null) {
            if (!(message.action.user_id == 0 || usersToLoad.contains(Integer.valueOf(message.action.user_id)))) {
                usersToLoad.add(Integer.valueOf(message.action.user_id));
            }
            if (!(message.action.channel_id == 0 || chatsToLoad.contains(Integer.valueOf(message.action.channel_id)))) {
                chatsToLoad.add(Integer.valueOf(message.action.channel_id));
            }
            if (!(message.action.chat_id == 0 || chatsToLoad.contains(Integer.valueOf(message.action.chat_id)))) {
                chatsToLoad.add(Integer.valueOf(message.action.chat_id));
            }
            if (!message.action.users.isEmpty()) {
                for (a = 0; a < message.action.users.size(); a++) {
                    Integer uid = (Integer) message.action.users.get(a);
                    if (!usersToLoad.contains(uid)) {
                        usersToLoad.add(uid);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            for (a = 0; a < message.entities.size(); a++) {
                MessageEntity entity = (MessageEntity) message.entities.get(a);
                if (entity instanceof TL_messageEntityMentionName) {
                    usersToLoad.add(Integer.valueOf(((TL_messageEntityMentionName) entity).user_id));
                } else if (entity instanceof TL_inputMessageEntityMentionName) {
                    usersToLoad.add(Integer.valueOf(((TL_inputMessageEntityMentionName) entity).user_id.user_id));
                }
            }
        }
        if (!(message.media == null || message.media.user_id == 0 || usersToLoad.contains(Integer.valueOf(message.media.user_id)))) {
            usersToLoad.add(Integer.valueOf(message.media.user_id));
        }
        if (message.fwd_from != null) {
            if (!(message.fwd_from.from_id == 0 || usersToLoad.contains(Integer.valueOf(message.fwd_from.from_id)))) {
                usersToLoad.add(Integer.valueOf(message.fwd_from.from_id));
            }
            if (!(message.fwd_from.channel_id == 0 || chatsToLoad.contains(Integer.valueOf(message.fwd_from.channel_id)))) {
                chatsToLoad.add(Integer.valueOf(message.fwd_from.channel_id));
            }
            if (message.fwd_from.saved_from_peer != null) {
                if (message.fwd_from.saved_from_peer.user_id != 0) {
                    if (!chatsToLoad.contains(Integer.valueOf(message.fwd_from.saved_from_peer.user_id))) {
                        usersToLoad.add(Integer.valueOf(message.fwd_from.saved_from_peer.user_id));
                    }
                } else if (message.fwd_from.saved_from_peer.channel_id != 0) {
                    if (!chatsToLoad.contains(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id))) {
                        chatsToLoad.add(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id));
                    }
                } else if (!(message.fwd_from.saved_from_peer.chat_id == 0 || chatsToLoad.contains(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id)))) {
                    chatsToLoad.add(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id));
                }
            }
        }
        if (message.ttl < 0 && !chatsToLoad.contains(Integer.valueOf(-message.ttl))) {
            chatsToLoad.add(Integer.valueOf(-message.ttl));
        }
    }

    public void getDialogs(int offset, int count) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$95(this, offset, count));
    }

    final /* synthetic */ void lambda$getDialogs$124$MessagesStorage(int offset, int count) {
        messages_Dialogs dialogs = new TL_messages_dialogs();
        ArrayList<EncryptedChat> encryptedChats = new ArrayList();
        try {
            NativeByteBuffer data;
            Message message;
            Message message2;
            ArrayList<Integer> usersToLoad = new ArrayList();
            usersToLoad.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            ArrayList<Integer> chatsToLoad = new ArrayList();
            ArrayList<Integer> encryptedToLoad = new ArrayList();
            ArrayList<Long> replyMessages = new ArrayList();
            LongSparseArray<Message> replyMessageOwners = new LongSparseArray();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i, d.flags FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did ORDER BY d.pinned DESC, d.date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(offset), Integer.valueOf(count)}), new Object[0]);
            while (cursor.next()) {
                TL_dialog dialog = new TL_dialog();
                dialog.f128id = cursor.longValue(0);
                dialog.top_message = cursor.intValue(1);
                dialog.unread_count = cursor.intValue(2);
                dialog.last_message_date = cursor.intValue(3);
                dialog.pts = cursor.intValue(10);
                int i = (dialog.pts == 0 || ((int) dialog.f128id) > 0) ? 0 : 1;
                dialog.flags = i;
                dialog.read_inbox_max_id = cursor.intValue(11);
                dialog.read_outbox_max_id = cursor.intValue(12);
                dialog.pinnedNum = cursor.intValue(14);
                dialog.pinned = dialog.pinnedNum != 0;
                dialog.unread_mentions_count = cursor.intValue(15);
                dialog.unread_mark = (cursor.intValue(16) & 1) != 0;
                long flags = cursor.longValue(8);
                int low_flags = (int) flags;
                dialog.notify_settings = new TL_peerNotifySettings();
                if ((low_flags & 1) != 0) {
                    dialog.notify_settings.mute_until = (int) (flags >> 32);
                    if (dialog.notify_settings.mute_until == 0) {
                        dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                }
                dialogs.dialogs.add(dialog);
                data = cursor.byteBufferValue(4);
                if (data != null) {
                    message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    if (message != null) {
                        MessageObject.setUnreadFlags(message, cursor.intValue(5));
                        message.f104id = cursor.intValue(6);
                        int date = cursor.intValue(9);
                        if (date != 0) {
                            dialog.last_message_date = date;
                        }
                        message.send_state = cursor.intValue(7);
                        message.dialog_id = dialog.f128id;
                        dialogs.messages.add(message);
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        try {
                            if (message.reply_to_msg_id != 0 && ((message.action instanceof TL_messageActionPinMessage) || (message.action instanceof TL_messageActionPaymentSent) || (message.action instanceof TL_messageActionGameScore))) {
                                if (!cursor.isNull(13)) {
                                    data = cursor.byteBufferValue(13);
                                    if (data != null) {
                                        message.replyMessage = Message.TLdeserialize(data, data.readInt32(false), false);
                                        message.replyMessage.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                                        data.reuse();
                                        if (message.replyMessage != null) {
                                            if (MessageObject.isMegagroup(message)) {
                                                message2 = message.replyMessage;
                                                message2.flags |= Integer.MIN_VALUE;
                                            }
                                            addUsersAndChatsFromMessage(message.replyMessage, usersToLoad, chatsToLoad);
                                        }
                                    }
                                }
                                if (message.replyMessage == null) {
                                    long messageId = (long) message.reply_to_msg_id;
                                    if (message.to_id.channel_id != 0) {
                                        messageId |= ((long) message.to_id.channel_id) << 32;
                                    }
                                    if (!replyMessages.contains(Long.valueOf(messageId))) {
                                        replyMessages.add(Long.valueOf(messageId));
                                    }
                                    replyMessageOwners.put(dialog.f128id, message);
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                }
                int lower_id = (int) dialog.f128id;
                int high_id = (int) (dialog.f128id >> 32);
                if (lower_id == 0) {
                    if (!encryptedToLoad.contains(Integer.valueOf(high_id))) {
                        encryptedToLoad.add(Integer.valueOf(high_id));
                    }
                } else if (high_id == 1) {
                    if (!chatsToLoad.contains(Integer.valueOf(lower_id))) {
                        chatsToLoad.add(Integer.valueOf(lower_id));
                    }
                } else if (lower_id > 0) {
                    if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                        usersToLoad.add(Integer.valueOf(lower_id));
                    }
                } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                    chatsToLoad.add(Integer.valueOf(-lower_id));
                }
            }
            cursor.dispose();
            if (!replyMessages.isEmpty()) {
                cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
                while (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        message.f104id = cursor.intValue(1);
                        message.date = cursor.intValue(2);
                        message.dialog_id = cursor.longValue(3);
                        addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        Message owner = (Message) replyMessageOwners.get(message.dialog_id);
                        if (owner != null) {
                            owner.replyMessage = message;
                            message.dialog_id = owner.dialog_id;
                            if (MessageObject.isMegagroup(owner)) {
                                message2 = owner.replyMessage;
                                message2.flags |= Integer.MIN_VALUE;
                            }
                        }
                    }
                }
                cursor.dispose();
            }
            if (!encryptedToLoad.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
            }
            if (!chatsToLoad.isEmpty()) {
                getChatsInternal(TextUtils.join(",", chatsToLoad), dialogs.chats);
            }
            if (!usersToLoad.isEmpty()) {
                getUsersInternal(TextUtils.join(",", usersToLoad), dialogs.users);
            }
            MessagesController.getInstance(this.currentAccount).processLoadedDialogs(dialogs, encryptedChats, offset, count, 1, false, false, true);
        } catch (Throwable e2) {
            dialogs.dialogs.clear();
            dialogs.users.clear();
            dialogs.chats.clear();
            encryptedChats.clear();
            FileLog.m13e(e2);
            MessagesController.getInstance(this.currentAccount).processLoadedDialogs(dialogs, encryptedChats, 0, 100, 1, true, false, true);
        }
    }

    public static void createFirstHoles(long did, SQLitePreparedStatement state5, SQLitePreparedStatement state6, int messageId) throws Exception {
        int i;
        state5.requery();
        state5.bindLong(1, did);
        if (messageId == 1) {
            i = 1;
        } else {
            i = 0;
        }
        state5.bindInteger(2, i);
        state5.bindInteger(3, messageId);
        state5.step();
        for (int b = 0; b < 5; b++) {
            state6.requery();
            state6.bindLong(1, did);
            state6.bindInteger(2, b);
            if (messageId == 1) {
                i = 1;
            } else {
                i = 0;
            }
            state6.bindInteger(3, i);
            state6.bindInteger(4, messageId);
            state6.step();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x01c2 A:{Catch:{ Exception -> 0x0147 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x034f A:{Catch:{ Exception -> 0x0147 }} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0405 A:{Catch:{ Exception -> 0x0147 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x011e A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0419 A:{Catch:{ Exception -> 0x0147 }} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void putDialogsInternal(messages_Dialogs dialogs, int check) {
        try {
            int a;
            Message message;
            this.database.beginTransaction();
            LongSparseArray<Message> new_dialogMessage = new LongSparseArray(dialogs.messages.size());
            for (a = 0; a < dialogs.messages.size(); a++) {
                message = (Message) dialogs.messages.get(a);
                new_dialogMessage.put(MessageObject.getDialogId(message), message);
            }
            if (!dialogs.dialogs.isEmpty()) {
                SQLitePreparedStatement state = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                SQLitePreparedStatement state2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                SQLitePreparedStatement state3 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                SQLitePreparedStatement state4 = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                SQLitePreparedStatement state5 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                SQLitePreparedStatement state6 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                for (a = 0; a < dialogs.dialogs.size(); a++) {
                    TL_dialog dialog = (TL_dialog) dialogs.dialogs.get(a);
                    if (dialog.f128id == 0) {
                        if (dialog.peer.user_id != 0) {
                            dialog.f128id = (long) dialog.peer.user_id;
                        } else if (dialog.peer.chat_id != 0) {
                            dialog.f128id = (long) (-dialog.peer.chat_id);
                        } else {
                            dialog.f128id = (long) (-dialog.peer.channel_id);
                        }
                    }
                    SQLiteCursor cursor;
                    int messageDate;
                    long topMessage;
                    int flags;
                    if (check == 1) {
                        cursor = this.database.queryFinalized("SELECT did FROM dialogs WHERE did = " + dialog.f128id, new Object[0]);
                        boolean exists = cursor.next();
                        cursor.dispose();
                        if (exists) {
                        }
                        messageDate = 0;
                        message = (Message) new_dialogMessage.get(dialog.f128id);
                        if (message != null) {
                            messageDate = Math.max(message.date, 0);
                            if (isValidKeyboardToSave(message)) {
                                DataQuery.getInstance(this.currentAccount).putBotKeyboard(dialog.f128id, message);
                            }
                            fixUnsupportedMedia(message);
                            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(data);
                            long messageId = (long) message.f104id;
                            if (message.to_id.channel_id != 0) {
                                messageId |= ((long) message.to_id.channel_id) << 32;
                            }
                            state.requery();
                            state.bindLong(1, messageId);
                            state.bindLong(2, dialog.f128id);
                            state.bindInteger(3, MessageObject.getUnreadFlags(message));
                            state.bindInteger(4, message.send_state);
                            state.bindInteger(5, message.date);
                            state.bindByteBuffer(6, data);
                            state.bindInteger(7, MessageObject.isOut(message) ? 1 : 0);
                            state.bindInteger(8, 0);
                            state.bindInteger(9, (message.flags & 1024) != 0 ? message.views : 0);
                            state.bindInteger(10, 0);
                            state.bindInteger(11, message.mentioned ? 1 : 0);
                            state.step();
                            if (DataQuery.canAddMessageToMedia(message)) {
                                state3.requery();
                                state3.bindLong(1, messageId);
                                state3.bindLong(2, dialog.f128id);
                                state3.bindInteger(3, message.date);
                                state3.bindInteger(4, DataQuery.getMediaType(message));
                                state3.bindByteBuffer(5, data);
                                state3.step();
                            }
                            data.reuse();
                            createFirstHoles(dialog.f128id, state5, state6, message.f104id);
                        }
                        topMessage = (long) dialog.top_message;
                        if (dialog.peer.channel_id != 0) {
                            topMessage |= ((long) dialog.peer.channel_id) << 32;
                        }
                        state2.requery();
                        state2.bindLong(1, dialog.f128id);
                        state2.bindInteger(2, messageDate);
                        state2.bindInteger(3, dialog.unread_count);
                        state2.bindLong(4, topMessage);
                        state2.bindInteger(5, dialog.read_inbox_max_id);
                        state2.bindInteger(6, dialog.read_outbox_max_id);
                        state2.bindLong(7, 0);
                        state2.bindInteger(8, dialog.unread_mentions_count);
                        state2.bindInteger(9, dialog.pts);
                        state2.bindInteger(10, 0);
                        state2.bindInteger(11, dialog.pinnedNum);
                        flags = 0;
                        if (dialog.unread_mark) {
                            flags = 0 | 1;
                        }
                        state2.bindInteger(12, flags);
                        state2.step();
                        if (dialog.notify_settings == null) {
                            state4.requery();
                            state4.bindLong(1, dialog.f128id);
                            state4.bindInteger(2, dialog.notify_settings.mute_until != 0 ? 1 : 0);
                            state4.step();
                        }
                    } else {
                        if (dialog.pinned && check == 2) {
                            cursor = this.database.queryFinalized("SELECT pinned FROM dialogs WHERE did = " + dialog.f128id, new Object[0]);
                            if (cursor.next()) {
                                dialog.pinnedNum = cursor.intValue(0);
                            }
                            cursor.dispose();
                        }
                        messageDate = 0;
                        message = (Message) new_dialogMessage.get(dialog.f128id);
                        if (message != null) {
                        }
                        topMessage = (long) dialog.top_message;
                        if (dialog.peer.channel_id != 0) {
                        }
                        state2.requery();
                        state2.bindLong(1, dialog.f128id);
                        state2.bindInteger(2, messageDate);
                        state2.bindInteger(3, dialog.unread_count);
                        state2.bindLong(4, topMessage);
                        state2.bindInteger(5, dialog.read_inbox_max_id);
                        state2.bindInteger(6, dialog.read_outbox_max_id);
                        state2.bindLong(7, 0);
                        state2.bindInteger(8, dialog.unread_mentions_count);
                        state2.bindInteger(9, dialog.pts);
                        state2.bindInteger(10, 0);
                        state2.bindInteger(11, dialog.pinnedNum);
                        flags = 0;
                        if (dialog.unread_mark) {
                        }
                        state2.bindInteger(12, flags);
                        state2.step();
                        if (dialog.notify_settings == null) {
                        }
                    }
                }
                state.dispose();
                state2.dispose();
                state3.dispose();
                state4.dispose();
                state5.dispose();
                state6.dispose();
            }
            putUsersInternal(dialogs.users);
            putChatsInternal(dialogs.chats);
            this.database.commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void unpinAllDialogsExceptNew(ArrayList<Long> dids) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$96(this, dids));
    }

    final /* synthetic */ void lambda$unpinAllDialogsExceptNew$125$MessagesStorage(ArrayList dids) {
        try {
            ArrayList<Long> unpinnedDialogs = new ArrayList();
            SQLiteCursor cursor = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[]{TextUtils.join(",", dids)}), new Object[0]);
            while (cursor.next()) {
                if (((int) cursor.longValue(0)) != 0) {
                    unpinnedDialogs.add(Long.valueOf(cursor.longValue(0)));
                }
            }
            cursor.dispose();
            if (!unpinnedDialogs.isEmpty()) {
                SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                for (int a = 0; a < unpinnedDialogs.size(); a++) {
                    long did = ((Long) unpinnedDialogs.get(a)).longValue();
                    state.requery();
                    state.bindInteger(1, 0);
                    state.bindLong(2, did);
                    state.step();
                }
                state.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void setDialogUnread(long did, boolean unread) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$97(this, did, unread));
    }

    final /* synthetic */ void lambda$setDialogUnread$126$MessagesStorage(long did, boolean unread) {
        int flags = 0;
        SQLiteCursor cursor = null;
        try {
            cursor = this.database.queryFinalized("SELECT flags FROM dialogs WHERE did = " + did, new Object[0]);
            if (cursor.next()) {
                flags = cursor.intValue(0);
            }
            if (cursor != null) {
                try {
                    cursor.dispose();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                    return;
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e(e2);
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        if (unread) {
            flags |= 1;
        } else {
            flags &= -2;
        }
        SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET flags = ? WHERE did = ?");
        state.bindInteger(1, flags);
        state.bindLong(2, did);
        state.step();
        state.dispose();
    }

    public void setDialogPinned(long did, int pinned) {
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$98(this, pinned, did));
    }

    final /* synthetic */ void lambda$setDialogPinned$127$MessagesStorage(int pinned, long did) {
        try {
            SQLitePreparedStatement state = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            state.bindInteger(1, pinned);
            state.bindLong(2, did);
            state.step();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void putDialogs(messages_Dialogs dialogs, int check) {
        if (!dialogs.dialogs.isEmpty()) {
            this.storageQueue.postRunnable(new MessagesStorage$$Lambda$99(this, dialogs, check));
        }
    }

    final /* synthetic */ void lambda$putDialogs$128$MessagesStorage(messages_Dialogs dialogs, int check) {
        putDialogsInternal(dialogs, check);
        try {
            loadUnreadMessages();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public int getDialogReadMax(boolean outbox, long dialog_id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] max = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$100(this, outbox, dialog_id, max, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return max[0].intValue();
    }

    final /* synthetic */ void lambda$getDialogReadMax$129$MessagesStorage(boolean outbox, long dialog_id, Integer[] max, CountDownLatch countDownLatch) {
        SQLiteCursor cursor = null;
        if (outbox) {
            try {
                cursor = this.database.queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + dialog_id, new Object[0]);
            } catch (Throwable e) {
                FileLog.m13e(e);
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.dispose();
                }
            }
        } else {
            cursor = this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + dialog_id, new Object[0]);
        }
        if (cursor.next()) {
            max[0] = Integer.valueOf(cursor.intValue(0));
        }
        if (cursor != null) {
            cursor.dispose();
        }
        countDownLatch.countDown();
    }

    public int getChannelPtsSync(int channelId) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] pts = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$101(this, channelId, pts, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return pts[0].intValue();
    }

    final /* synthetic */ void lambda$getChannelPtsSync$130$MessagesStorage(int channelId, Integer[] pts, CountDownLatch countDownLatch) {
        SQLiteCursor cursor = null;
        try {
            cursor = this.database.queryFinalized("SELECT pts FROM dialogs WHERE did = " + (-channelId), new Object[0]);
            if (cursor.next()) {
                pts[0] = Integer.valueOf(cursor.intValue(0));
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.dispose();
            }
        }
        if (countDownLatch != null) {
            try {
                countDownLatch.countDown();
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
        }
    }

    public User getUserSync(int user_id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        User[] user = new User[1];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$102(this, user, user_id, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return user[0];
    }

    final /* synthetic */ void lambda$getUserSync$131$MessagesStorage(User[] user, int user_id, CountDownLatch countDownLatch) {
        user[0] = getUser(user_id);
        countDownLatch.countDown();
    }

    public Chat getChatSync(int chat_id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Chat[] chat = new Chat[1];
        this.storageQueue.postRunnable(new MessagesStorage$$Lambda$103(this, chat, chat_id, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return chat[0];
    }

    final /* synthetic */ void lambda$getChatSync$132$MessagesStorage(Chat[] chat, int chat_id, CountDownLatch countDownLatch) {
        chat[0] = getChat(chat_id);
        countDownLatch.countDown();
    }

    public User getUser(int user_id) {
        try {
            ArrayList<User> users = new ArrayList();
            getUsersInternal(TtmlNode.ANONYMOUS_REGION_ID + user_id, users);
            if (users.isEmpty()) {
                return null;
            }
            return (User) users.get(0);
        } catch (Throwable e) {
            FileLog.m13e(e);
            return null;
        }
    }

    public ArrayList<User> getUsers(ArrayList<Integer> uids) {
        ArrayList<User> users = new ArrayList();
        try {
            getUsersInternal(TextUtils.join(",", uids), users);
        } catch (Throwable e) {
            users.clear();
            FileLog.m13e(e);
        }
        return users;
    }

    public Chat getChat(int chat_id) {
        try {
            ArrayList<Chat> chats = new ArrayList();
            getChatsInternal(TtmlNode.ANONYMOUS_REGION_ID + chat_id, chats);
            if (chats.isEmpty()) {
                return null;
            }
            return (Chat) chats.get(0);
        } catch (Throwable e) {
            FileLog.m13e(e);
            return null;
        }
    }

    public EncryptedChat getEncryptedChat(int chat_id) {
        try {
            ArrayList<EncryptedChat> encryptedChats = new ArrayList();
            getEncryptedChatsInternal(TtmlNode.ANONYMOUS_REGION_ID + chat_id, encryptedChats, null);
            if (encryptedChats.isEmpty()) {
                return null;
            }
            return (EncryptedChat) encryptedChats.get(0);
        } catch (Throwable e) {
            FileLog.m13e(e);
            return null;
        }
    }
}
