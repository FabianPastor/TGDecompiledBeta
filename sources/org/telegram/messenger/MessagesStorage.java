package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
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
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_folder;
import org.telegram.tgnet.TLRPC.TL_folderPeer;
import org.telegram.tgnet.TLRPC.TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollResults;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage extends BaseController {
    private static volatile MessagesStorage[] Instance = new MessagesStorage[3];
    private static final int LAST_DB_VERSION = 62;
    private File cacheFile;
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

    public interface BooleanCallback {
        void run(boolean z);
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

    private class ReadDialog {
        public int date;
        public int lastMid;
        public int unreadCount;

        private ReadDialog() {
        }
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$l_EMBf4E_fIloSDlPGVY7ULYtZw(this));
    }

    public /* synthetic */ void lambda$new$0$MessagesStorage() {
        openDatabase(1);
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    public DispatchQueue getStorageQueue() {
        return this.storageQueue;
    }

    public long getDatabaseSize() {
        File file = this.cacheFile;
        long j = 0;
        if (file != null) {
            j = 0 + file.length();
        }
        file = this.shmCacheFile;
        return file != null ? j + file.length() : j;
    }

    public void openDatabase(int i) {
        int i2 = i;
        String str = "malformed";
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (this.currentAccount != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("account");
            stringBuilder.append(this.currentAccount);
            stringBuilder.append("/");
            File file = new File(filesDirFixed, stringBuilder.toString());
            file.mkdirs();
            filesDirFixed = file;
        }
        this.cacheFile = new File(filesDirFixed, "cache4.db");
        this.walCacheFile = new File(filesDirFixed, "cache4.db-wal");
        this.shmCacheFile = new File(filesDirFixed, "cache4.db-shm");
        int exists = this.cacheFile.exists() ^ 1;
        int i3 = 3;
        try {
            this.database = new SQLiteDatabase(this.cacheFile.getPath());
            this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
            this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
            this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
            String str2 = "INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)";
            if (exists != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("create new database");
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
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, parent TEXT, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                this.database.executeFast(str2).stepThis().dispose();
                this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER, online INTEGER)").stepThis().dispose();
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
                this.database.executeFast("CREATE TABLE channel_admins_v2(did INTEGER, uid INTEGER, rank TEXT, PRIMARY KEY(did, uid))").stepThis().dispose();
                this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
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
                this.database.executeFast("CREATE TABLE emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER, date INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE polls(mid INTEGER PRIMARY KEY, id INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls(id);").stepThis().dispose();
                this.database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
                loadUnreadMessages();
                loadPendingTasks();
                try {
                    this.openSync.countDown();
                    return;
                } catch (Throwable unused) {
                    return;
                }
            }
            int intValue = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("current db version = ");
                stringBuilder2.append(intValue);
                FileLog.d(stringBuilder2.toString());
            }
            if (intValue != 0) {
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
                            this.secretPBytes = queryFinalized.byteArrayValue(6);
                            if (this.secretPBytes != null && this.secretPBytes.length == 1) {
                                this.secretPBytes = null;
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Exception e) {
                    FileLog.e(e);
                    try {
                        this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
                        this.database.executeFast(str2).stepThis().dispose();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (intValue < 62) {
                    updateDbToLastVersion(intValue);
                }
                loadUnreadMessages();
                loadPendingTasks();
                this.openSync.countDown();
                return;
            }
            throw new Exception(str);
        } catch (Exception e22) {
            FileLog.e(e22);
            if (i2 < 3 && e22.getMessage().contains(str)) {
                if (i2 == 2) {
                    cleanupInternal(true);
                    for (exists = 0; exists < 2; exists++) {
                        getUserConfig().setDialogsLoadOffset(exists, 0, 0, 0, 0, 0, 0);
                        getUserConfig().setTotalDialogsCount(exists, 0);
                    }
                    getUserConfig().saveConfig(false);
                } else {
                    cleanupInternal(false);
                }
                if (i2 == 1) {
                    i3 = 2;
                }
                openDatabase(i3);
            }
        }
    }

    private void updateDbToLastVersion(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$UniaOWI4GiF_wLDJANnyTaeI1I0(this, i));
    }

    public /* synthetic */ void lambda$updateDbToLastVersion$1$MessagesStorage(int i) {
        SQLiteCursor queryFinalized;
        if (i < 4) {
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
                this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
                this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
                this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
                fixNotificationSettings();
                this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
                i = 4;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (i == 4) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            this.database.beginTransaction();
            queryFinalized = this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
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
            this.database.commitTransaction();
            this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i = 6;
        }
        if (i == 6) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            i = 7;
        }
        if (i == 7 || i == 8 || i == 9) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i = 10;
        }
        if (i == 10) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            i = 11;
        }
        if (i == 11 || i == 12) {
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
            i = 13;
        }
        if (i == 13) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i = 14;
        }
        if (i == 14) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i = 15;
        }
        if (i == 15) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i = 16;
        }
        if (i == 16) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i = 17;
        }
        if (i == 17) {
            this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i = 18;
        }
        if (i == 18) {
            this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i = 19;
        }
        if (i == 19) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i = 20;
        }
        if (i == 20) {
            this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i = 21;
        }
        if (i == 21) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            queryFinalized = this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (queryFinalized.next()) {
                int intValue2 = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue2 = queryFinalized.byteBufferValue(1);
                if (byteBufferValue2 != null) {
                    ChatParticipants TLdeserialize = ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    byteBufferValue2.reuse();
                    if (TLdeserialize != null) {
                        TL_chatFull tL_chatFull = new TL_chatFull();
                        tL_chatFull.id = intValue2;
                        tL_chatFull.chat_photo = new TL_photoEmpty();
                        tL_chatFull.notify_settings = new TL_peerNotifySettingsEmpty_layer77();
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
            i = 22;
        }
        if (i == 22) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            i = 23;
        }
        if (i == 23 || i == 24) {
            this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i = 25;
        }
        if (i == 25 || i == 26) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            i = 27;
        }
        if (i == 27) {
            this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            i = 28;
        }
        if (i == 28 || i == 29) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i = 30;
        }
        if (i == 30) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            i = 31;
        }
        if (i == 31) {
            this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            i = 32;
        }
        if (i == 32) {
            this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i = 33;
        }
        if (i == 33) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            i = 34;
        }
        if (i == 34) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i = 35;
        }
        if (i == 35) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            i = 36;
        }
        if (i == 36) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            i = 37;
        }
        if (i == 37) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i = 38;
        }
        if (i == 38) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            i = 39;
        }
        if (i == 39) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            i = 40;
        }
        if (i == 40) {
            fixNotificationSettings();
            this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i = 41;
        }
        if (i == 41) {
            this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            i = 42;
        }
        if (i == 42) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i = 43;
        }
        if (i == 43) {
            this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            i = 44;
        }
        if (i == 44) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i = 45;
        }
        if (i == 45) {
            this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            i = 46;
        }
        if (i == 46) {
            this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            i = 47;
        }
        if (i == 47) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            i = 48;
        }
        if (i == 48) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            i = 49;
        }
        if (i == 49) {
            this.database.executeFast("DELETE FROM chat_pinned WHERE uid = 1").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            i = 50;
        }
        if (i == 50) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            this.database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            i = 51;
        }
        if (i == 51) {
            this.database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            i = 52;
        }
        if (i == 52) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS polls(mid INTEGER PRIMARY KEY, id INTEGER);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls(id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            i = 53;
        }
        if (i == 53) {
            this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            i = 54;
        }
        if (i == 54) {
            this.database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            i = 55;
        }
        if (i == 55) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            i = 56;
        }
        if (i == 56 || i == 57) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            i = 58;
        }
        if (i == 58) {
            this.database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            this.database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            i = 59;
        }
        if (i == 59) {
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            this.database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
            i = 60;
        }
        if (i == 60) {
            this.database.executeFast("DROP TABLE IF EXISTS channel_admins;").stepThis().dispose();
            this.database.executeFast("DROP TABLE IF EXISTS blocked_users;").stepThis().dispose();
            this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins_v2(did INTEGER, uid INTEGER, rank TEXT, PRIMARY KEY(did, uid))").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 61").stepThis().dispose();
            i = 61;
        }
        if (i == 61) {
            this.database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages;").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
        }
    }

    private void cleanupInternal(boolean z) {
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
            file = this.walCacheFile;
            if (file != null) {
                file.delete();
                this.walCacheFile = null;
            }
            file = this.shmCacheFile;
            if (file != null) {
                file.delete();
                this.shmCacheFile = null;
            }
        }
    }

    public void cleanup(boolean z) {
        if (!z) {
            this.storageQueue.cleanupQueue();
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ekOTDfJDLwKOj7vRXd-V4MXUJZc(this, z));
    }

    public /* synthetic */ void lambda$cleanup$3$MessagesStorage(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$UEupzbErlOb7ycnNPm1lBZ9MmX8(this));
        }
    }

    public /* synthetic */ void lambda$null$2$MessagesStorage() {
        getMessagesController().getDifference();
    }

    public void saveSecretParams(int i, int i2, byte[] bArr) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$NtpBYQeXRJpUfal8CNuiE9yNR3Y(this, i, i2, bArr));
    }

    public /* synthetic */ void lambda$saveSecretParams$4$MessagesStorage(int i, int i2, byte[] bArr) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$H7ZjmdIrsBPnbbqD1cn5Lti86iY(this));
    }

    public /* synthetic */ void lambda$fixNotificationSettings$5$MessagesStorage() {
        String str = "notify2_";
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Entry entry : all.entrySet()) {
                String str2 = (String) entry.getKey();
                if (str2.startsWith(str)) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() == 2 || num.intValue() == 3) {
                        str2 = str2.replace(str, "");
                        long j = 1;
                        if (num.intValue() != 2) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("notifyuntil_");
                            stringBuilder.append(str2);
                            num = (Integer) all.get(stringBuilder.toString());
                            if (num != null) {
                                j = 1 | (((long) num.intValue()) << 32);
                            }
                        }
                        longSparseArray.put(Long.parseLong(str2), Long.valueOf(j));
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable e3) {
            FileLog.e(e3);
        }
    }

    public long createPendingTask(NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0;
        }
        long andAdd = this.lastTaskId.getAndAdd(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$TxzHvDLT8O_Bs3_-p-EFkrwY3ws(this, andAdd, nativeByteBuffer));
        return andAdd;
    }

    public /* synthetic */ void lambda$createPendingTask$6$MessagesStorage(long j, NativeByteBuffer nativeByteBuffer) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
            executeFast.bindLong(1, j);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            nativeByteBuffer.reuse();
        }
        nativeByteBuffer.reuse();
    }

    public void removePendingTask(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bLsQ9Zez-Ef0tQJtoMR54iM-bHA(this, j));
    }

    public /* synthetic */ void lambda$removePendingTask$7$MessagesStorage(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM pending_tasks WHERE id = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Mh1_3ksiRZzSQICRWjWNRIrSkV4(this));
    }

    /* JADX WARNING: Missing block: B:9:0x0025, code skipped:
            r15 = r10;
     */
    /* JADX WARNING: Missing block: B:56:0x0271, code skipped:
            r15.reuse();
     */
    public /* synthetic */ void lambda$loadPendingTasks$21$MessagesStorage() {
        /*
        r17 = this;
        r14 = r17;
        r0 = r14.database;	 Catch:{ Exception -> 0x027d }
        r1 = "SELECT id, data FROM pending_tasks WHERE 1";
        r15 = 0;
        r2 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x027d }
        r0 = r0.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x027d }
    L_0x000d:
        r1 = r0.next();	 Catch:{ Exception -> 0x027d }
        if (r1 == 0) goto L_0x0279;
    L_0x0013:
        r12 = r0.longValue(r15);	 Catch:{ Exception -> 0x027d }
        r1 = 1;
        r10 = r0.byteBufferValue(r1);	 Catch:{ Exception -> 0x027d }
        if (r10 == 0) goto L_0x0275;
    L_0x001e:
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        switch(r1) {
            case 0: goto L_0x025b;
            case 1: goto L_0x0241;
            case 2: goto L_0x01c2;
            case 3: goto L_0x019f;
            case 4: goto L_0x017f;
            case 5: goto L_0x01c2;
            case 6: goto L_0x015d;
            case 7: goto L_0x0131;
            case 8: goto L_0x01c2;
            case 9: goto L_0x0115;
            case 10: goto L_0x01c2;
            case 11: goto L_0x00eb;
            case 12: goto L_0x00be;
            case 13: goto L_0x0092;
            case 14: goto L_0x01c2;
            case 15: goto L_0x007f;
            case 16: goto L_0x0054;
            case 17: goto L_0x0029;
            default: goto L_0x0025;
        };	 Catch:{ Exception -> 0x027d }
    L_0x0025:
        r15 = r10;
    L_0x0026:
        r1 = 0;
        goto L_0x0271;
    L_0x0029:
        r3 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x027d }
        r4.<init>();	 Catch:{ Exception -> 0x027d }
        r2 = 0;
    L_0x0037:
        if (r2 >= r1) goto L_0x0047;
    L_0x0039:
        r5 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r5 = org.telegram.tgnet.TLRPC.TL_inputFolderPeer.TLdeserialize(r10, r5, r15);	 Catch:{ Exception -> 0x027d }
        r4.add(r5);	 Catch:{ Exception -> 0x027d }
        r2 = r2 + 1;
        goto L_0x0037;
    L_0x0047:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$LtM1spYCOokckYn9GalfKw21eVw;	 Catch:{ Exception -> 0x027d }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027d }
        goto L_0x0025;
    L_0x0054:
        r3 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x027d }
        r4.<init>();	 Catch:{ Exception -> 0x027d }
        r2 = 0;
    L_0x0062:
        if (r2 >= r1) goto L_0x0072;
    L_0x0064:
        r5 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r5 = org.telegram.tgnet.TLRPC.InputDialogPeer.TLdeserialize(r10, r5, r15);	 Catch:{ Exception -> 0x027d }
        r4.add(r5);	 Catch:{ Exception -> 0x027d }
        r2 = r2 + 1;
        goto L_0x0062;
    L_0x0072:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$KOMsQ0FDjAutn-YKgmf9ukUIQZQ;	 Catch:{ Exception -> 0x027d }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027d }
        goto L_0x0025;
    L_0x007f:
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r1 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r10, r1, r15);	 Catch:{ Exception -> 0x027d }
        r2 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027d }
        r3 = new org.telegram.messenger.-$$Lambda$MessagesStorage$tZ31OdkMW8PKLVCYnMiDI_DfRTs;	 Catch:{ Exception -> 0x027d }
        r3.<init>(r14, r1, r12);	 Catch:{ Exception -> 0x027d }
        r2.postRunnable(r3);	 Catch:{ Exception -> 0x027d }
        goto L_0x0025;
    L_0x0092:
        r3 = r10.readInt64(r15);	 Catch:{ Exception -> 0x027d }
        r5 = r10.readBool(r15);	 Catch:{ Exception -> 0x027d }
        r6 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r7 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r8 = r10.readBool(r15);	 Catch:{ Exception -> 0x027d }
        r1 = r10.readInt32(r15);	 Catch:{ Exception -> 0x027d }
        r9 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r10, r1, r15);	 Catch:{ Exception -> 0x027d }
        r16 = new org.telegram.messenger.-$$Lambda$MessagesStorage$gZU7uqq34_u60ckwe65AH41ydVA;	 Catch:{ Exception -> 0x027d }
        r1 = r16;
        r2 = r17;
        r15 = r10;
        r10 = r12;
        r1.<init>(r2, r3, r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x00be:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027d }
        r5 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027d }
        r7 = r15.readBool(r1);	 Catch:{ Exception -> 0x027d }
        r8 = r15.readBool(r1);	 Catch:{ Exception -> 0x027d }
        r9 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r10 = r15.readDouble(r1);	 Catch:{ Exception -> 0x027d }
        r10 = (float) r10;	 Catch:{ Exception -> 0x027d }
        r11 = r15.readBool(r1);	 Catch:{ Exception -> 0x027d }
        r16 = new org.telegram.messenger.-$$Lambda$MessagesStorage$86hFXP2JtkqWJ8sHKPYBQGuj0fs;	 Catch:{ Exception -> 0x027d }
        r1 = r16;
        r2 = r17;
        r1.<init>(r2, r3, r5, r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x00eb:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r6 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        if (r4 == 0) goto L_0x0105;
    L_0x00fb:
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r2 = org.telegram.tgnet.TLRPC.InputChannel.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r5 = r2;
        goto L_0x0107;
    L_0x0105:
        r1 = 0;
        r5 = r1;
    L_0x0107:
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$TqvcAvYPfh-Cio-d66SYceSFjMQ;	 Catch:{ Exception -> 0x027d }
        r1 = r9;
        r2 = r17;
        r7 = r12;
        r1.<init>(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x0115:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r5 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r8 = new org.telegram.messenger.-$$Lambda$MessagesStorage$OTCbnyNXoirwhvbsu8TFVflzodM;	 Catch:{ Exception -> 0x027d }
        r1 = r8;
        r2 = r17;
        r6 = r12;
        r1.<init>(r2, r3, r5, r6);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x0131:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r4 = org.telegram.tgnet.TLRPC.TL_messages_deleteMessages.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        if (r4 != 0) goto L_0x0147;
    L_0x0141:
        r2 = org.telegram.tgnet.TLRPC.TL_channels_deleteMessages.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r6 = r2;
        goto L_0x0148;
    L_0x0147:
        r6 = r4;
    L_0x0148:
        if (r6 != 0) goto L_0x014f;
    L_0x014a:
        r14.removePendingTask(r12);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x014f:
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$EuuPlYsIg_jeReoMzIbm6stO0ag;	 Catch:{ Exception -> 0x027d }
        r1 = r7;
        r2 = r17;
        r4 = r12;
        r1.<init>(r2, r3, r4, r6);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x015d:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r7 = org.telegram.tgnet.TLRPC.InputChannel.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r8 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027d }
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$veltQ-QzWYSSmgAGGDUTY-jvHoM;	 Catch:{ Exception -> 0x027d }
        r1 = r9;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5, r7);	 Catch:{ Exception -> 0x027d }
        r8.postRunnable(r9);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x017f:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027d }
        r5 = r15.readBool(r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r6 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r9 = new org.telegram.messenger.-$$Lambda$MessagesStorage$AuH1gRs2iXmoSCC0c3xvFDAEwcM;	 Catch:{ Exception -> 0x027d }
        r1 = r9;
        r2 = r17;
        r7 = r12;
        r1.<init>(r2, r3, r5, r6, r7);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x019f:
        r15 = r10;
        r1 = 0;
        r5 = r15.readInt64(r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r3 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r2 = org.telegram.tgnet.TLRPC.InputMedia.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r4 = r2;
        r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaGame) r4;	 Catch:{ Exception -> 0x027d }
        r2 = r17.getSendMessagesHelper();	 Catch:{ Exception -> 0x027d }
        r7 = r12;
        r2.sendGame(r3, r4, r5, r7);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x01c2:
        r15 = r10;
        r3 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x027d }
        r3.<init>();	 Catch:{ Exception -> 0x027d }
        r2 = 0;
        r4 = r15.readInt64(r2);	 Catch:{ Exception -> 0x027d }
        r3.id = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.top_message = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.read_inbox_max_id = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.read_outbox_max_id = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.unread_count = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.last_message_date = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.pts = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.flags = r4;	 Catch:{ Exception -> 0x027d }
        r4 = 5;
        if (r1 < r4) goto L_0x0208;
    L_0x01fc:
        r4 = r15.readBool(r2);	 Catch:{ Exception -> 0x027d }
        r3.pinned = r4;	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.pinnedNum = r4;	 Catch:{ Exception -> 0x027d }
    L_0x0208:
        r2 = 8;
        if (r1 < r2) goto L_0x0213;
    L_0x020c:
        r2 = 0;
        r4 = r15.readInt32(r2);	 Catch:{ Exception -> 0x027d }
        r3.unread_mentions_count = r4;	 Catch:{ Exception -> 0x027d }
    L_0x0213:
        r2 = 10;
        if (r1 < r2) goto L_0x021e;
    L_0x0217:
        r2 = 0;
        r4 = r15.readBool(r2);	 Catch:{ Exception -> 0x027d }
        r3.unread_mark = r4;	 Catch:{ Exception -> 0x027d }
    L_0x021e:
        r2 = 14;
        if (r1 < r2) goto L_0x022a;
    L_0x0222:
        r1 = 0;
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r3.folder_id = r2;	 Catch:{ Exception -> 0x027d }
        goto L_0x022b;
    L_0x022a:
        r1 = 0;
    L_0x022b:
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r4 = org.telegram.tgnet.TLRPC.InputPeer.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        r7 = new org.telegram.messenger.-$$Lambda$MessagesStorage$uwK8wtI_v95kHeLcf4gXEkrDfLc;	 Catch:{ Exception -> 0x027d }
        r1 = r7;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x0241:
        r15 = r10;
        r1 = 0;
        r3 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r4 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r7 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027d }
        r8 = new org.telegram.messenger.-$$Lambda$MessagesStorage$0pEDcAvar_KSwzKWD9sU7FMqhzD0;	 Catch:{ Exception -> 0x027d }
        r1 = r8;
        r2 = r17;
        r5 = r12;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x027d }
        r7.postRunnable(r8);	 Catch:{ Exception -> 0x027d }
        goto L_0x0026;
    L_0x025b:
        r15 = r10;
        r1 = 0;
        r2 = r15.readInt32(r1);	 Catch:{ Exception -> 0x027d }
        r2 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r15, r2, r1);	 Catch:{ Exception -> 0x027d }
        if (r2 == 0) goto L_0x0271;
    L_0x0267:
        r3 = org.telegram.messenger.Utilities.stageQueue;	 Catch:{ Exception -> 0x027d }
        r4 = new org.telegram.messenger.-$$Lambda$MessagesStorage$X8wmcVmkWlOOmT7hyCwq34C_3HA;	 Catch:{ Exception -> 0x027d }
        r4.<init>(r14, r2, r12);	 Catch:{ Exception -> 0x027d }
        r3.postRunnable(r4);	 Catch:{ Exception -> 0x027d }
    L_0x0271:
        r15.reuse();	 Catch:{ Exception -> 0x027d }
        goto L_0x0276;
    L_0x0275:
        r1 = 0;
    L_0x0276:
        r15 = 0;
        goto L_0x000d;
    L_0x0279:
        r0.dispose();	 Catch:{ Exception -> 0x027d }
        goto L_0x0281;
    L_0x027d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0281:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadPendingTasks$21$MessagesStorage():void");
    }

    public /* synthetic */ void lambda$null$8$MessagesStorage(Chat chat, long j) {
        getMessagesController().loadUnknownChannel(chat, j);
    }

    public /* synthetic */ void lambda$null$9$MessagesStorage(int i, int i2, long j) {
        getMessagesController().getChannelDifference(i, i2, j, null);
    }

    public /* synthetic */ void lambda$null$10$MessagesStorage(Dialog dialog, InputPeer inputPeer, long j) {
        getMessagesController().checkLastDialogMessage(dialog, inputPeer, j);
    }

    public /* synthetic */ void lambda$null$11$MessagesStorage(long j, boolean z, InputPeer inputPeer, long j2) {
        getMessagesController().pinDialog(j, z, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$12$MessagesStorage(int i, int i2, long j, InputChannel inputChannel) {
        getMessagesController().getChannelDifference(i, i2, j, inputChannel);
    }

    public /* synthetic */ void lambda$null$13$MessagesStorage(int i, long j, TLObject tLObject) {
        getMessagesController().deleteMessages(null, null, null, i, true, j, tLObject);
    }

    public /* synthetic */ void lambda$null$14$MessagesStorage(long j, InputPeer inputPeer, long j2) {
        getMessagesController().markDialogAsUnread(j, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$15$MessagesStorage(int i, int i2, InputChannel inputChannel, int i3, long j) {
        getMessagesController().markMessageAsRead(i, i2, inputChannel, i3, j);
    }

    public /* synthetic */ void lambda$null$16$MessagesStorage(long j, long j2, boolean z, boolean z2, int i, float f, boolean z3, long j3) {
        getMessagesController().saveWallpaperToServer(null, j, j2, z, z2, i, f, z3, j3);
    }

    public /* synthetic */ void lambda$null$17$MessagesStorage(long j, boolean z, int i, int i2, boolean z2, InputPeer inputPeer, long j2) {
        getMessagesController().deleteDialog(j, z, i, i2, z2, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$18$MessagesStorage(InputPeer inputPeer, long j) {
        getMessagesController().loadUnknownDialog(inputPeer, j);
    }

    public /* synthetic */ void lambda$null$19$MessagesStorage(int i, ArrayList arrayList, long j) {
        getMessagesController().reorderPinnedDialogs(i, arrayList, j);
    }

    public /* synthetic */ void lambda$null$20$MessagesStorage(int i, ArrayList arrayList, long j) {
        getMessagesController().addDialogToFolder(null, i, -1, arrayList, j);
    }

    public void saveChannelPts(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bE1TJhfMynV6-iw8er6RBG5GCDM(this, i2, i));
    }

    public /* synthetic */ void lambda$saveChannelPts$22$MessagesStorage(int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, -i2);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$saveDiffParams$23$MessagesStorage(int i, int i2, int i3, int i4) {
        saveDiffParamsInternal(i, i2, i3, i4);
    }

    public void saveDiffParams(int i, int i2, int i3, int i4) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_bpF9_lI_H0oz-1CAiE5SbDJe_c(this, i, i2, i3, i4));
    }

    public void setDialogFlags(long j, long j2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$uZgJttXUWPBitEWEPJJQPgaQByA(this, j, j2));
    }

    public /* synthetic */ void lambda$setDialogFlags$24$MessagesStorage(long j, long j2) {
        try {
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j2)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(MessageObject messageObject) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$lVMMHudh0dN8CKFv0xYu7ddEmi8(this, messageObject));
    }

    public /* synthetic */ void lambda$putPushMessage$25$MessagesStorage(MessageObject messageObject) {
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            long id = (long) messageObject.getId();
            if (messageObject.messageOwner.to_id.channel_id != 0) {
                id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
            }
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
            executeFast.bindLong(2, id);
            executeFast.bindLong(3, messageObject.messageOwner.random_id);
            executeFast.bindInteger(4, messageObject.messageOwner.date);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            if (messageObject.messageText == null) {
                executeFast.bindNull(6);
            } else {
                executeFast.bindString(6, messageObject.messageText.toString());
            }
            if (messageObject.localName == null) {
                executeFast.bindNull(7);
            } else {
                executeFast.bindString(7, messageObject.localName);
            }
            if (messageObject.localUserName == null) {
                executeFast.bindNull(8);
            } else {
                executeFast.bindString(8, messageObject.localUserName);
            }
            executeFast.bindInteger(9, i);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void readAllDialogs() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Uv7xGGpYr4xQqc_hYra_gjZD6hY(this));
    }

    public /* synthetic */ void lambda$readAllDialogs$27$MessagesStorage() {
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count != 0", new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (!DialogObject.isFolderDialogId(longValue)) {
                    ReadDialog readDialog = new ReadDialog();
                    readDialog.lastMid = queryFinalized.intValue(1);
                    readDialog.unreadCount = queryFinalized.intValue(2);
                    readDialog.date = queryFinalized.intValue(3);
                    longSparseArray.put(longValue, readDialog);
                    int i = (int) longValue;
                    int i2 = (int) (longValue >> 32);
                    if (i != 0) {
                        if (i < 0) {
                            i = -i;
                            if (!arrayList2.contains(Integer.valueOf(i))) {
                                arrayList2.add(Integer.valueOf(i));
                            }
                        } else if (!arrayList.contains(Integer.valueOf(i))) {
                            arrayList.add(Integer.valueOf(i));
                        }
                    } else if (!arrayList3.contains(Integer.valueOf(i2))) {
                        arrayList3.add(Integer.valueOf(i2));
                    }
                }
            }
            queryFinalized.dispose();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            String str = ",";
            if (!arrayList3.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(str, arrayList3), arrayList6, arrayList);
            }
            if (!arrayList.isEmpty()) {
                getUsersInternal(TextUtils.join(str, arrayList), arrayList4);
            }
            if (!arrayList2.isEmpty()) {
                getChatsInternal(TextUtils.join(str, arrayList2), arrayList5);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$8eKeJbrxLTdGP5YjS5xvuniLwx0(this, arrayList4, arrayList5, arrayList6, longSparseArray));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$26$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            MessagesController messagesController = getMessagesController();
            int i2 = readDialog.lastMid;
            messagesController.markDialogAsRead(keyAt, i2, i2, readDialog.date, false, readDialog.unreadCount, true);
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$joRU6ZuCNEib3vg1K0JvduLpCMk(this));
    }

    public /* synthetic */ void lambda$loadUnreadMessages$29$MessagesStorage() {
        ArrayList arrayList;
        ArrayList arrayList2;
        String str;
        int max;
        ArrayList arrayList3;
        ArrayList arrayList4;
        Throwable e;
        try {
            String str2;
            int i;
            ArrayList arrayList5;
            LongSparseArray longSparseArray;
            List list;
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            int currentTime = getConnectionsManager().getCurrentTime();
            while (true) {
                str2 = ",";
                if (!queryFinalized.next()) {
                    break;
                }
                long longValue = queryFinalized.longValue(2);
                Object obj = (longValue & 1) != 0 ? 1 : null;
                int i2 = (int) (longValue >> 32);
                if (queryFinalized.isNull(2) || obj == null || (i2 != 0 && i2 < currentTime)) {
                    longValue = queryFinalized.longValue(0);
                    if (!DialogObject.isFolderDialogId(longValue)) {
                        longSparseArray2.put(longValue, Integer.valueOf(queryFinalized.intValue(1)));
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append(str2);
                        }
                        stringBuilder.append(longValue);
                        int i3 = (int) longValue;
                        i = (int) (longValue >> 32);
                        if (i3 != 0) {
                            if (i3 < 0) {
                                i3 = -i3;
                                if (!arrayList7.contains(Integer.valueOf(i3))) {
                                    arrayList7.add(Integer.valueOf(i3));
                                }
                            } else if (!arrayList6.contains(Integer.valueOf(i3))) {
                                arrayList6.add(Integer.valueOf(i3));
                            }
                        } else if (!arrayList8.contains(Integer.valueOf(i))) {
                            arrayList8.add(Integer.valueOf(i));
                        }
                    }
                }
            }
            queryFinalized.dispose();
            ArrayList arrayList9 = new ArrayList();
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList10 = new ArrayList();
            ArrayList arrayList11 = new ArrayList();
            ArrayList arrayList12 = new ArrayList();
            ArrayList arrayList13 = new ArrayList();
            ArrayList arrayList14 = new ArrayList();
            if (stringBuilder.length() > 0) {
                Message TLdeserialize;
                NativeByteBuffer byteBufferValue;
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder2 = new StringBuilder();
                LongSparseArray longSparseArray3 = longSparseArray2;
                stringBuilder2.append("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (");
                stringBuilder2.append(stringBuilder.toString());
                stringBuilder2.append(") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50");
                SQLiteCursor queryFinalized2 = sQLiteDatabase.queryFinalized(stringBuilder2.toString(), new Object[0]);
                int i4 = 0;
                while (queryFinalized2.next()) {
                    NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                    if (byteBufferValue2 != null) {
                        arrayList = arrayList13;
                        TLdeserialize = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                        byteBufferValue2.reuse();
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(0));
                        TLdeserialize.id = queryFinalized2.intValue(3);
                        TLdeserialize.date = queryFinalized2.intValue(4);
                        arrayList2 = arrayList12;
                        str = str2;
                        TLdeserialize.dialog_id = queryFinalized2.longValue(5);
                        arrayList10.add(TLdeserialize);
                        max = Math.max(i4, TLdeserialize.date);
                        i4 = (int) TLdeserialize.dialog_id;
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
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
                                    NativeByteBuffer byteBufferValue3 = queryFinalized2.byteBufferValue(6);
                                    if (byteBufferValue3 != null) {
                                        TLdeserialize.replyMessage = Message.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(false), false);
                                        TLdeserialize.replyMessage.readAttachPath(byteBufferValue3, getUserConfig().clientUserId);
                                        byteBufferValue3.reuse();
                                        if (TLdeserialize.replyMessage != null) {
                                            if (MessageObject.isMegagroup(TLdeserialize)) {
                                                Message message = TLdeserialize.replyMessage;
                                                message.flags |= Integer.MIN_VALUE;
                                            }
                                            addUsersAndChatsFromMessage(TLdeserialize.replyMessage, arrayList6, arrayList7);
                                        }
                                    }
                                }
                                if (TLdeserialize.replyMessage == null) {
                                    long j = (long) TLdeserialize.reply_to_msg_id;
                                    if (TLdeserialize.to_id.channel_id != 0) {
                                        arrayList3 = arrayList14;
                                        arrayList4 = arrayList10;
                                        j |= ((long) TLdeserialize.to_id.channel_id) << 32;
                                    } else {
                                        arrayList3 = arrayList14;
                                        arrayList4 = arrayList10;
                                    }
                                    try {
                                        if (!arrayList9.contains(Long.valueOf(j))) {
                                            arrayList9.add(Long.valueOf(j));
                                        }
                                        ArrayList arrayList15 = (ArrayList) sparseArray.get(TLdeserialize.reply_to_msg_id);
                                        if (arrayList15 == null) {
                                            arrayList15 = new ArrayList();
                                            sparseArray.put(TLdeserialize.reply_to_msg_id, arrayList15);
                                        }
                                        arrayList15.add(TLdeserialize);
                                    } catch (Exception e2) {
                                        e = e2;
                                        FileLog.e(e);
                                        i4 = max;
                                        str2 = str;
                                        arrayList13 = arrayList;
                                        arrayList12 = arrayList2;
                                        arrayList10 = arrayList4;
                                        arrayList14 = arrayList3;
                                    }
                                    i4 = max;
                                }
                            }
                            arrayList3 = arrayList14;
                            arrayList4 = arrayList10;
                        } catch (Exception e3) {
                            e = e3;
                            arrayList3 = arrayList14;
                            arrayList4 = arrayList10;
                            FileLog.e(e);
                            i4 = max;
                            str2 = str;
                            arrayList13 = arrayList;
                            arrayList12 = arrayList2;
                            arrayList10 = arrayList4;
                            arrayList14 = arrayList3;
                        }
                        i4 = max;
                    } else {
                        arrayList2 = arrayList12;
                        str = str2;
                        arrayList = arrayList13;
                        arrayList3 = arrayList14;
                        arrayList4 = arrayList10;
                    }
                    str2 = str;
                    arrayList13 = arrayList;
                    arrayList12 = arrayList2;
                    arrayList10 = arrayList4;
                    arrayList14 = arrayList3;
                }
                arrayList2 = arrayList12;
                str = str2;
                arrayList = arrayList13;
                arrayList3 = arrayList14;
                arrayList4 = arrayList10;
                queryFinalized2.dispose();
                SQLiteDatabase sQLiteDatabase2 = this.database;
                stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM unread_push_messages WHERE date <= ");
                stringBuilder.append(i4);
                sQLiteDatabase2.executeFast(stringBuilder.toString()).stepThis().dispose();
                max = 0;
                queryFinalized = this.database.queryFinalized("SELECT data, mid, date, uid, random, fm, name, uname, flags FROM unread_push_messages WHERE 1 ORDER BY date DESC LIMIT 50", new Object[0]);
                while (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(max);
                    if (byteBufferValue != null) {
                        int i5;
                        Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(max), max);
                        byteBufferValue.reuse();
                        TLdeserialize2.id = queryFinalized.intValue(1);
                        TLdeserialize2.date = queryFinalized.intValue(2);
                        TLdeserialize2.dialog_id = queryFinalized.longValue(3);
                        TLdeserialize2.random_id = queryFinalized.longValue(4);
                        String str3 = null;
                        String stringValue = queryFinalized.isNull(5) ? null : queryFinalized.stringValue(5);
                        String stringValue2 = queryFinalized.isNull(6) ? null : queryFinalized.stringValue(6);
                        if (!queryFinalized.isNull(7)) {
                            str3 = queryFinalized.stringValue(7);
                        }
                        String str4 = str3;
                        int intValue = queryFinalized.intValue(8);
                        if (TLdeserialize2.from_id == 0) {
                            i5 = (int) TLdeserialize2.dialog_id;
                            if (i5 > 0) {
                                TLdeserialize2.from_id = i5;
                            }
                        }
                        i5 = (int) TLdeserialize2.dialog_id;
                        if (i5 > 0) {
                            if (!arrayList6.contains(Integer.valueOf(i5))) {
                                arrayList6.add(Integer.valueOf(i5));
                            }
                        } else if (i5 < 0) {
                            int i6 = -i5;
                            if (!arrayList7.contains(Integer.valueOf(i6))) {
                                arrayList7.add(Integer.valueOf(i6));
                            }
                        }
                        arrayList11.add(new MessageObject(this.currentAccount, TLdeserialize2, stringValue, stringValue2, str4, (intValue & 1) != 0, (intValue & 2) != 0, false));
                        addUsersAndChatsFromMessage(TLdeserialize2, arrayList6, arrayList7);
                    }
                    max = 0;
                }
                queryFinalized.dispose();
                if (!arrayList9.isEmpty()) {
                    sQLiteDatabase = this.database;
                    Object[] objArr = new Object[1];
                    int i7 = 0;
                    objArr[0] = TextUtils.join(str, arrayList9);
                    queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", objArr), new Object[0]);
                    while (queryFinalized.next()) {
                        byteBufferValue = queryFinalized.byteBufferValue(i7);
                        if (byteBufferValue != null) {
                            Message TLdeserialize3 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i7), i7);
                            TLdeserialize3.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            TLdeserialize3.id = queryFinalized.intValue(1);
                            TLdeserialize3.date = queryFinalized.intValue(2);
                            TLdeserialize3.dialog_id = queryFinalized.longValue(3);
                            addUsersAndChatsFromMessage(TLdeserialize3, arrayList6, arrayList7);
                            arrayList12 = (ArrayList) sparseArray.get(TLdeserialize3.id);
                            if (arrayList12 != null) {
                                for (i = 0; i < arrayList12.size(); i++) {
                                    TLdeserialize = (Message) arrayList12.get(i);
                                    TLdeserialize.replyMessage = TLdeserialize3;
                                    if (MessageObject.isMegagroup(TLdeserialize)) {
                                        TLdeserialize = TLdeserialize.replyMessage;
                                        TLdeserialize.flags |= Integer.MIN_VALUE;
                                    }
                                }
                            }
                        }
                        i7 = 0;
                    }
                    queryFinalized.dispose();
                }
                if (arrayList8.isEmpty()) {
                    arrayList5 = arrayList3;
                } else {
                    arrayList5 = arrayList3;
                    getEncryptedChatsInternal(TextUtils.join(str, arrayList8), arrayList5, arrayList6);
                }
                if (arrayList6.isEmpty()) {
                    arrayList9 = arrayList2;
                } else {
                    arrayList9 = arrayList2;
                    getUsersInternal(TextUtils.join(str, arrayList6), arrayList9);
                }
                if (arrayList7.isEmpty()) {
                    longSparseArray = longSparseArray3;
                    arrayList12 = arrayList;
                } else {
                    arrayList12 = arrayList;
                    getChatsInternal(TextUtils.join(str, arrayList7), arrayList12);
                    i4 = 0;
                    while (i4 < arrayList12.size()) {
                        ArrayList arrayList16;
                        Chat chat = (Chat) arrayList12.get(i4);
                        if (chat == null || (!ChatObject.isNotInChat(chat) && chat.migrated_to == null)) {
                            longSparseArray = longSparseArray3;
                            arrayList16 = arrayList4;
                        } else {
                            long j2 = (long) (-chat.id);
                            SQLiteDatabase sQLiteDatabase3 = this.database;
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("UPDATE dialogs SET unread_count = 0 WHERE did = ");
                            stringBuilder3.append(j2);
                            sQLiteDatabase3.executeFast(stringBuilder3.toString()).stepThis().dispose();
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[]{Long.valueOf(j2)})).stepThis().dispose();
                            arrayList12.remove(i4);
                            i4--;
                            longSparseArray = longSparseArray3;
                            longSparseArray.remove(j2);
                            int i8 = 0;
                            while (i8 < arrayList4.size()) {
                                arrayList16 = arrayList4;
                                if (((Message) arrayList16.get(i8)).dialog_id == j2) {
                                    arrayList16.remove(i8);
                                    i8--;
                                }
                                i8++;
                                arrayList4 = arrayList16;
                            }
                            arrayList16 = arrayList4;
                        }
                        i4++;
                        longSparseArray3 = longSparseArray;
                        arrayList4 = arrayList16;
                    }
                    longSparseArray = longSparseArray3;
                }
                list = arrayList4;
            } else {
                longSparseArray = longSparseArray2;
                arrayList9 = arrayList12;
                arrayList12 = arrayList13;
                arrayList5 = arrayList14;
                list = arrayList10;
            }
            Collections.reverse(list);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$S3bj_aGKTgw9UejHP75FZtyG2ms(this, longSparseArray, list, arrayList11, arrayList9, arrayList12, arrayList5));
        } catch (Exception e4) {
            FileLog.e(e4);
        }
    }

    public /* synthetic */ void lambda$null$28$MessagesStorage(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
    }

    public void putWallpapers(ArrayList<WallPaper> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$9BkX0ldup_qkhjHCNOhyXfgGwXY(this, i, arrayList));
    }

    public /* synthetic */ void lambda$putWallpapers$30$MessagesStorage(int i, ArrayList arrayList) {
        SQLitePreparedStatement executeFast;
        if (i == 1) {
            try {
                this.database.executeFast("DELETE FROM wallpapers2 WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        if (i != 0) {
            executeFast = this.database.executeFast("REPLACE INTO wallpapers2 VALUES(?, ?, ?)");
        } else {
            executeFast = this.database.executeFast("UPDATE wallpapers2 SET data = ? WHERE uid = ?");
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) arrayList.get(i2);
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_wallPaper.getObjectSize());
            tL_wallPaper.serializeToStream(nativeByteBuffer);
            if (i != 0) {
                executeFast.bindLong(1, tL_wallPaper.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i == 2 ? -1 : i2);
            } else {
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindLong(2, tL_wallPaper.id);
            }
            executeFast.step();
            nativeByteBuffer.reuse();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ANII5DCxsANA4ox20RDROdwMfjc(this));
    }

    public /* synthetic */ void lambda$getWallpapers$32$MessagesStorage() {
        SQLiteCursor sQLiteCursor = null;
        try {
            sQLiteCursor = this.database.queryFinalized("SELECT data FROM wallpapers2 WHERE 1 ORDER BY num ASC", new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TL_wallPaper tL_wallPaper = (TL_wallPaper) WallPaper.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (tL_wallPaper != null) {
                        arrayList.add(tL_wallPaper);
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$laGcyL2VMuNvphN9xrLjowzyS3Y(arrayList));
            if (sQLiteCursor == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLiteCursor == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw e2;
        }
        sQLiteCursor.dispose();
    }

    public void loadWebRecent(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$IRnMGvkA4nj7Im0gunHCNPyjjd0(this, i));
    }

    public /* synthetic */ void lambda$loadWebRecent$34$MessagesStorage(int i) {
        try {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$lXEFOLx65JNFN3MBWdpO9k0o0mA(this, i, new ArrayList()));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$33$MessagesStorage(int i, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.recentImagesDidLoad, Integer.valueOf(i), arrayList);
    }

    public void addRecentLocalFile(String str, String str2, Document document) {
        if (str != null && str.length() != 0) {
            if ((str2 != null && str2.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$sz8fEb44b64gI189wg0lApn07kU(this, document, str, str2));
            }
        }
    }

    public /* synthetic */ void lambda$addRecentLocalFile$35$MessagesStorage(Document document, String str, String str2) {
        if (document != null) {
            try {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindString(2, str);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
        executeFast2.requery();
        executeFast2.bindString(1, str2);
        executeFast2.bindString(2, str);
        executeFast2.step();
        executeFast2.dispose();
    }

    public void clearWebRecent(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$IAAcgw0MEJsvXeLksE-HqIMUybc(this, i));
    }

    public /* synthetic */ void lambda$clearWebRecent$36$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM web_recent_v3 WHERE type = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putWebRecent(ArrayList<SearchImage> arrayList) {
        if (!arrayList.isEmpty() && arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$2zgETsycS9dz5I652QRWAYabQH4(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$putWebRecent$37$MessagesStorage(ArrayList arrayList) {
        try {
            int i;
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int i2 = 0;
            while (true) {
                i = 200;
                if (i2 >= arrayList.size()) {
                    break;
                } else if (i2 == 200) {
                    break;
                } else {
                    SearchImage searchImage = (SearchImage) arrayList.get(i2);
                    executeFast.requery();
                    executeFast.bindString(1, searchImage.id);
                    executeFast.bindInteger(2, searchImage.type);
                    String str = "";
                    executeFast.bindString(3, searchImage.imageUrl != null ? searchImage.imageUrl : str);
                    executeFast.bindString(4, searchImage.thumbUrl != null ? searchImage.thumbUrl : str);
                    if (searchImage.localUrl != null) {
                        str = searchImage.localUrl;
                    }
                    executeFast.bindString(5, str);
                    executeFast.bindInteger(6, searchImage.width);
                    executeFast.bindInteger(7, searchImage.height);
                    executeFast.bindInteger(8, searchImage.size);
                    executeFast.bindInteger(9, searchImage.date);
                    NativeByteBuffer nativeByteBuffer = null;
                    if (searchImage.photo != null) {
                        nativeByteBuffer = new NativeByteBuffer(searchImage.photo.getObjectSize());
                        searchImage.photo.serializeToStream(nativeByteBuffer);
                        executeFast.bindByteBuffer(10, nativeByteBuffer);
                    } else if (searchImage.document != null) {
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
            this.database.commitTransaction();
            if (arrayList.size() >= 200) {
                this.database.beginTransaction();
                while (i < arrayList.size()) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                    stringBuilder.append(((SearchImage) arrayList.get(i)).id);
                    stringBuilder.append("'");
                    sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
                    i++;
                }
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteUserChannelHistory(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$3jQ0GNP_T0igRcNLlYcKgDYQ0dg(this, i, i2));
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$40$MessagesStorage(int i, int i2) {
        long j = (long) (-i);
        try {
            ArrayList arrayList = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT data FROM messages WHERE uid = ");
            stringBuilder.append(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                        byteBufferValue.reuse();
                        if (!(TLdeserialize == null || TLdeserialize.from_id != i2 || TLdeserialize.id == 1)) {
                            arrayList.add(Integer.valueOf(TLdeserialize.id));
                            int size;
                            int i3;
                            File pathToAttach;
                            if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                size = TLdeserialize.media.photo.sizes.size();
                                for (i3 = 0; i3 < size; i3++) {
                                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i3));
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList2.add(pathToAttach);
                                    }
                                }
                            } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                    arrayList2.add(pathToAttach2);
                                }
                                size = TLdeserialize.media.document.thumbs.size();
                                for (i3 = 0; i3 < size; i3++) {
                                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i3));
                                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                        arrayList2.add(pathToAttach);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$cPR5xMVYMtmyGYhYkqRZT2XUJMg(this, arrayList, i));
            markMessagesAsDeletedInternal(arrayList, i);
            updateDialogsWithDeletedMessagesInternal(arrayList, null, i);
            getFileLoader().deleteFiles(arrayList2, 0);
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$v2uVbmQ0FBcQTbwwzD5Ge9t6SKQ(this, arrayList, i));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public /* synthetic */ void lambda$null$38$MessagesStorage(ArrayList arrayList, int i) {
        getMessagesController().markChannelDialogMessageAsDeleted(arrayList, i);
    }

    public /* synthetic */ void lambda$null$39$MessagesStorage(ArrayList arrayList, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i));
    }

    public void deleteDialog(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$PCvjtni2lFtHY5xx730KMSURCtw(this, i, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:85:0x0296 A:{Catch:{ Exception -> 0x0037 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0296 A:{Catch:{ Exception -> 0x0037 }} */
    public /* synthetic */ void lambda$deleteDialog$42$MessagesStorage(int r21, long r22) {
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r5 = " AND mid != ";
        r6 = 3;
        r8 = 0;
        if (r2 != r6) goto L_0x003a;
    L_0x000c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r10 = "SELECT last_mid FROM dialogs WHERE did = ";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r3);	 Catch:{ Exception -> 0x0037 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0037 }
        r0 = r0.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x0037 }
        r9 = r0.next();	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x0030;
    L_0x002b:
        r9 = r0.intValue(r8);	 Catch:{ Exception -> 0x0037 }
        goto L_0x0031;
    L_0x0030:
        r9 = -1;
    L_0x0031:
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x003a;
    L_0x0036:
        return;
    L_0x0037:
        r0 = move-exception;
        goto L_0x0461;
    L_0x003a:
        r9 = (int) r3;
        r10 = "SELECT data FROM messages WHERE uid = ";
        r11 = 2;
        if (r9 == 0) goto L_0x0042;
    L_0x0040:
        if (r2 != r11) goto L_0x0113;
    L_0x0042:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r12.<init>();	 Catch:{ Exception -> 0x0037 }
        r12.append(r10);	 Catch:{ Exception -> 0x0037 }
        r12.append(r3);	 Catch:{ Exception -> 0x0037 }
        r12 = r12.toString();	 Catch:{ Exception -> 0x0037 }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0037 }
        r12 = r0.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x0037 }
        r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0037 }
        r13.<init>();	 Catch:{ Exception -> 0x0037 }
    L_0x005e:
        r0 = r12.next();	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x0109;
    L_0x0064:
        r0 = r12.byteBufferValue(r8);	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x005e;
    L_0x006a:
        r14 = r0.readInt32(r8);	 Catch:{ Exception -> 0x0105 }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r14, r8);	 Catch:{ Exception -> 0x0105 }
        r15 = r20.getUserConfig();	 Catch:{ Exception -> 0x0105 }
        r15 = r15.clientUserId;	 Catch:{ Exception -> 0x0105 }
        r14.readAttachPath(r0, r15);	 Catch:{ Exception -> 0x0105 }
        r0.reuse();	 Catch:{ Exception -> 0x0105 }
        if (r14 == 0) goto L_0x005e;
    L_0x0080:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x005e;
    L_0x0084:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x00b9;
    L_0x008a:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.photo;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.sizes;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0105 }
        r15 = 0;
    L_0x0095:
        if (r15 >= r0) goto L_0x005e;
    L_0x0097:
        r7 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r7 = r7.photo;	 Catch:{ Exception -> 0x0105 }
        r7 = r7.sizes;	 Catch:{ Exception -> 0x0105 }
        r7 = r7.get(r15);	 Catch:{ Exception -> 0x0105 }
        r7 = (org.telegram.tgnet.TLRPC.PhotoSize) r7;	 Catch:{ Exception -> 0x0105 }
        r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7);	 Catch:{ Exception -> 0x0105 }
        if (r7 == 0) goto L_0x00b6;
    L_0x00a9:
        r16 = r7.toString();	 Catch:{ Exception -> 0x0105 }
        r16 = r16.length();	 Catch:{ Exception -> 0x0105 }
        if (r16 <= 0) goto L_0x00b6;
    L_0x00b3:
        r13.add(r7);	 Catch:{ Exception -> 0x0105 }
    L_0x00b6:
        r15 = r15 + 1;
        goto L_0x0095;
    L_0x00b9:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x005e;
    L_0x00bf:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.document;	 Catch:{ Exception -> 0x0105 }
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);	 Catch:{ Exception -> 0x0105 }
        if (r0 == 0) goto L_0x00d6;
    L_0x00c9:
        r7 = r0.toString();	 Catch:{ Exception -> 0x0105 }
        r7 = r7.length();	 Catch:{ Exception -> 0x0105 }
        if (r7 <= 0) goto L_0x00d6;
    L_0x00d3:
        r13.add(r0);	 Catch:{ Exception -> 0x0105 }
    L_0x00d6:
        r0 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.document;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.thumbs;	 Catch:{ Exception -> 0x0105 }
        r0 = r0.size();	 Catch:{ Exception -> 0x0105 }
        r7 = 0;
    L_0x00e1:
        if (r7 >= r0) goto L_0x005e;
    L_0x00e3:
        r15 = r14.media;	 Catch:{ Exception -> 0x0105 }
        r15 = r15.document;	 Catch:{ Exception -> 0x0105 }
        r15 = r15.thumbs;	 Catch:{ Exception -> 0x0105 }
        r15 = r15.get(r7);	 Catch:{ Exception -> 0x0105 }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x0105 }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x0105 }
        if (r15 == 0) goto L_0x0102;
    L_0x00f5:
        r16 = r15.toString();	 Catch:{ Exception -> 0x0105 }
        r16 = r16.length();	 Catch:{ Exception -> 0x0105 }
        if (r16 <= 0) goto L_0x0102;
    L_0x00ff:
        r13.add(r15);	 Catch:{ Exception -> 0x0105 }
    L_0x0102:
        r7 = r7 + 1;
        goto L_0x00e1;
    L_0x0105:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x0109:
        r12.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r20.getFileLoader();	 Catch:{ Exception -> 0x0037 }
        r0.deleteFiles(r13, r2);	 Catch:{ Exception -> 0x0037 }
    L_0x0113:
        r12 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r13 = "DELETE FROM messages_holes WHERE uid = ";
        r14 = "DELETE FROM media_v2 WHERE uid = ";
        r15 = "DELETE FROM media_counts_v2 WHERE uid = ";
        r7 = "DELETE FROM bot_keyboard WHERE uid = ";
        r8 = "DELETE FROM messages WHERE uid = ";
        r0 = 1;
        if (r2 == 0) goto L_0x02ab;
    L_0x0122:
        if (r2 != r6) goto L_0x0126;
    L_0x0124:
        goto L_0x02ab;
    L_0x0126:
        if (r2 != r11) goto L_0x02a6;
    L_0x0128:
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r6.<init>();	 Catch:{ Exception -> 0x0037 }
        r9 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = ";
        r6.append(r9);	 Catch:{ Exception -> 0x0037 }
        r6.append(r3);	 Catch:{ Exception -> 0x0037 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0037 }
        r9 = 0;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0037 }
        r2 = r2.queryFinalized(r6, r11);	 Catch:{ Exception -> 0x0037 }
        r6 = r2.next();	 Catch:{ Exception -> 0x0037 }
        if (r6 == 0) goto L_0x02a0;
    L_0x0148:
        r6 = r12;
        r11 = r2.longValue(r9);	 Catch:{ Exception -> 0x0037 }
        r18 = r14;
        r19 = r15;
        r14 = r2.longValue(r0);	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r3);	 Catch:{ Exception -> 0x0037 }
        r10 = " AND mid IN (";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r11);	 Catch:{ Exception -> 0x0037 }
        r10 = ",";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9.append(r14);	 Catch:{ Exception -> 0x0037 }
        r10 = ")";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r21 = r2;
        r10 = 0;
        r2 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0037 }
        r2 = r0.queryFinalized(r9, r2);	 Catch:{ Exception -> 0x0037 }
        r9 = -1;
    L_0x0185:
        r0 = r2.next();	 Catch:{ Exception -> 0x01b8 }
        if (r0 == 0) goto L_0x01b5;
    L_0x018b:
        r0 = r2.byteBufferValue(r10);	 Catch:{ Exception -> 0x01b8 }
        if (r0 == 0) goto L_0x01af;
    L_0x0191:
        r17 = r9;
        r9 = r0.readInt32(r10);	 Catch:{ Exception -> 0x01ad }
        r9 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r9, r10);	 Catch:{ Exception -> 0x01ad }
        r10 = r20.getUserConfig();	 Catch:{ Exception -> 0x01ad }
        r10 = r10.clientUserId;	 Catch:{ Exception -> 0x01ad }
        r9.readAttachPath(r0, r10);	 Catch:{ Exception -> 0x01ad }
        r0.reuse();	 Catch:{ Exception -> 0x01ad }
        if (r9 == 0) goto L_0x01b1;
    L_0x01a9:
        r0 = r9.id;	 Catch:{ Exception -> 0x01ad }
        r9 = r0;
        goto L_0x01b3;
    L_0x01ad:
        r0 = move-exception;
        goto L_0x01bb;
    L_0x01af:
        r17 = r9;
    L_0x01b1:
        r9 = r17;
    L_0x01b3:
        r10 = 0;
        goto L_0x0185;
    L_0x01b5:
        r17 = r9;
        goto L_0x01be;
    L_0x01b8:
        r0 = move-exception;
        r17 = r9;
    L_0x01bb:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x01be:
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r8);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r11);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r14);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r13);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r5 = r19;
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r10 = r18;
        r2.append(r10);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r20.getMediaDataController();	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r5 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r5);	 Catch:{ Exception -> 0x0037 }
        r9 = r17;
        r5 = -1;
        if (r9 == r5) goto L_0x0299;
    L_0x0296:
        createFirstHoles(r3, r0, r2, r9);	 Catch:{ Exception -> 0x0037 }
    L_0x0299:
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        goto L_0x02a2;
    L_0x02a0:
        r21 = r2;
    L_0x02a2:
        r21.dispose();	 Catch:{ Exception -> 0x0037 }
        return;
    L_0x02a6:
        r6 = r12;
        r10 = r14;
        r5 = r15;
        goto L_0x038a;
    L_0x02ab:
        r6 = r12;
        r10 = r14;
        r5 = r15;
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM dialogs WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM chat_settings_v2 WHERE uid = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM chat_pinned WHERE uid = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM channel_users_v2 WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r11.<init>();	 Catch:{ Exception -> 0x0037 }
        r12 = "DELETE FROM search_recent WHERE did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0037 }
        r11.append(r3);	 Catch:{ Exception -> 0x0037 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0037 }
        r2 = r2.executeFast(r11);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r2 = 32;
        r11 = r3 >> r2;
        r2 = (int) r11;	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x036c;
    L_0x034b:
        if (r2 != r0) goto L_0x038a;
    L_0x034d:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r11 = "DELETE FROM chats WHERE uid = ";
        r2.append(r11);	 Catch:{ Exception -> 0x0037 }
        r2.append(r9);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        goto L_0x038a;
    L_0x036c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r9.<init>();	 Catch:{ Exception -> 0x0037 }
        r11 = "DELETE FROM enc_chats WHERE uid = ";
        r9.append(r11);	 Catch:{ Exception -> 0x0037 }
        r9.append(r2);	 Catch:{ Exception -> 0x0037 }
        r2 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
    L_0x038a:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r9 = "UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = ";
        r2.append(r9);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r8);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r10);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r13);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r20.getMediaDataController();	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = new org.telegram.messenger.-$$Lambda$MessagesStorage$taft69qXFHZ_1bEqsgURAe7JeTY;	 Catch:{ Exception -> 0x0037 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0037 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0037 }
        goto L_0x0464;
    L_0x0461:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0464:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$42$MessagesStorage(int, long):void");
    }

    public /* synthetic */ void lambda$null$41$MessagesStorage() {
        getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$l9YnvyCT2k4rQ6LtdN_b4yA5kC0(this, j));
    }

    public /* synthetic */ void lambda$onDeleteQueryComplete$43$MessagesStorage(long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM media_counts_v2 WHERE uid = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getDialogPhotos(int i, int i2, long j, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$W8lz_r0CI0wcBl4i7DvQpsQQqa8(this, j, i, i2, i3));
    }

    public /* synthetic */ void lambda$getDialogPhotos$45$MessagesStorage(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        if (j != 0) {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY rowid ASC LIMIT %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(i2)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY rowid ASC LIMIT %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        TL_photos_photos tL_photos_photos = new TL_photos_photos();
        while (queryFinalized.next()) {
            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
            if (byteBufferValue != null) {
                Photo TLdeserialize = Photo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
                tL_photos_photos.photos.add(TLdeserialize);
            }
        }
        queryFinalized.dispose();
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$oa6KZckQmC6yjYJ52qBSz2gUGvA(this, tL_photos_photos, i, i2, j, i3));
    }

    public /* synthetic */ void lambda$null$44$MessagesStorage(photos_Photos photos_photos, int i, int i2, long j, int i3) {
        getMessagesController().processLoadedUserPhotos(photos_photos, i, i2, j, true, i3);
    }

    public void clearUserPhotos(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6xDh4yAXlOKrnbJ1oAplhd5XKxc(this, i));
    }

    public /* synthetic */ void lambda$clearUserPhotos$46$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void clearUserPhoto(int i, long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$SZ9jbCeYmbRr44qpmB6ho-qi4sw(this, i, j));
    }

    public /* synthetic */ void lambda$clearUserPhoto$47$MessagesStorage(int i, long j) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            stringBuilder.append(" AND id = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetDialogs(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, LongSparseArray<Dialog> longSparseArray, LongSparseArray<MessageObject> longSparseArray2, Message message, int i6) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Lj84DfNt9J3vj65vYWkLH7VYVvU(this, messages_dialogs, i6, i2, i3, i4, i5, message, i, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$resetDialogs$49$MessagesStorage(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        messages_Dialogs messages_dialogs2 = messages_dialogs;
        Message message2 = message;
        try {
            long longValue;
            int intValue;
            int i7;
            int i8;
            int i9;
            long j;
            int i10;
            ArrayList arrayList = new ArrayList();
            int size = messages_dialogs2.dialogs.size() - i;
            LongSparseArray longSparseArray3 = new LongSparseArray();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (int i11 = i; i11 < messages_dialogs2.dialogs.size(); i11++) {
                arrayList3.add(Long.valueOf(((Dialog) messages_dialogs2.dialogs.get(i11)).id));
            }
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
            int i12 = 0;
            while (queryFinalized.next()) {
                longValue = queryFinalized.longValue(0);
                intValue = queryFinalized.intValue(1);
                i7 = (int) longValue;
                if (i7 != 0) {
                    arrayList.add(Integer.valueOf(i7));
                    if (intValue > 0) {
                        i12 = Math.max(intValue, i12);
                        longSparseArray3.put(longValue, Integer.valueOf(intValue));
                        arrayList2.add(Long.valueOf(longValue));
                    }
                }
            }
            Collections.sort(arrayList2, new -$$Lambda$MessagesStorage$9DYXcI0QEFw93jimOp1ua4TiOXo(longSparseArray3));
            while (arrayList2.size() < size) {
                arrayList2.add(0, Long.valueOf(0));
            }
            queryFinalized.dispose();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(TextUtils.join(",", arrayList));
            stringBuilder.append(")");
            String stringBuilder2 = stringBuilder.toString();
            this.database.beginTransaction();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM dialogs WHERE did IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM messages WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            this.database.executeFast("DELETE FROM polls WHERE 1").stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM bot_keyboard WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM media_v2 WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM messages_holes WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            sQLiteDatabase = this.database;
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append("DELETE FROM media_holes_v2 WHERE uid IN ");
            stringBuilder3.append(stringBuilder2);
            sQLiteDatabase.executeFast(stringBuilder3.toString()).stepThis().dispose();
            this.database.commitTransaction();
            for (i8 = 0; i8 < size; i8++) {
                Dialog dialog = (Dialog) messages_dialogs2.dialogs.get(i + i8);
                if (!(dialog instanceof TL_dialog) || dialog.pinned) {
                    intValue = arrayList2.indexOf(Long.valueOf(dialog.id));
                    i7 = arrayList3.indexOf(Long.valueOf(dialog.id));
                    if (!(intValue == -1 || i7 == -1)) {
                        Integer num;
                        if (intValue == i7) {
                            num = (Integer) longSparseArray3.get(dialog.id);
                            if (num != null) {
                                dialog.pinnedNum = num.intValue();
                            }
                        } else {
                            num = (Integer) longSparseArray3.get(((Long) arrayList2.get(i7)).longValue());
                            if (num != null) {
                                dialog.pinnedNum = num.intValue();
                            }
                        }
                    }
                    if (dialog.pinnedNum == 0) {
                        dialog.pinnedNum = (size - i8) + i12;
                    }
                }
            }
            putDialogsInternal(messages_dialogs2, 0);
            saveDiffParamsInternal(i2, i3, i4, i5);
            i8 = getUserConfig().getTotalDialogsCount(0);
            getUserConfig().getDialogLoadOffsets(0);
            i8 += messages_dialogs2.dialogs.size();
            size = message2.id;
            int i13 = message2.date;
            if (message2.to_id.channel_id == 0) {
                if (message2.to_id.chat_id != 0) {
                    intValue = message2.to_id.chat_id;
                    for (i9 = 0; i9 < messages_dialogs2.chats.size(); i9++) {
                        Chat chat = (Chat) messages_dialogs2.chats.get(i9);
                        if (chat.id == intValue) {
                            longValue = chat.access_hash;
                            break;
                        }
                    }
                    longValue = 0;
                    j = longValue;
                    i10 = 0;
                    i9 = intValue;
                } else {
                    if (message2.to_id.user_id != 0) {
                        intValue = message2.to_id.user_id;
                        i9 = 0;
                        while (i9 < messages_dialogs2.users.size()) {
                            User user = (User) messages_dialogs2.users.get(i9);
                            if (user.id == intValue) {
                                j = user.access_hash;
                                i9 = 0;
                                i10 = intValue;
                            } else {
                                i9++;
                            }
                        }
                        i10 = intValue;
                        intValue = 0;
                        i9 = 0;
                    } else {
                        intValue = 0;
                        i9 = 0;
                        i10 = 0;
                    }
                    j = 0;
                }
                intValue = 0;
                break;
            }
            intValue = message2.to_id.channel_id;
            for (i7 = 0; i7 < messages_dialogs2.chats.size(); i7++) {
                Chat chat2 = (Chat) messages_dialogs2.chats.get(i7);
                if (chat2.id == intValue) {
                    longValue = chat2.access_hash;
                    break;
                }
            }
            longValue = 0;
            j = longValue;
            i9 = 0;
            i10 = 0;
            int i14 = 0;
            while (i14 < 2) {
                i = i14;
                getUserConfig().setDialogsLoadOffset(i14, size, i13, i10, i9, intValue, j);
                i14 = i;
                getUserConfig().setTotalDialogsCount(i14, i8);
                i14++;
            }
            getUserConfig().saveConfig(false);
            getMessagesController().completeDialogsReset(messages_dialogs, i6, i2, i3, i4, i5, longSparseArray, longSparseArray2, message);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ int lambda$null$48(LongSparseArray longSparseArray, Long l, Long l2) {
        Integer num = (Integer) longSparseArray.get(l.longValue());
        Integer num2 = (Integer) longSparseArray.get(l2.longValue());
        if (num.intValue() < num2.intValue()) {
            return 1;
        }
        return num.intValue() > num2.intValue() ? -1 : 0;
    }

    public void putDialogPhotos(int i, photos_Photos photos_photos) {
        if (photos_photos != null && !photos_photos.photos.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$dmBaoEBVUXAfkurWt4XN1ZU7znw(this, i, photos_photos));
        }
    }

    public /* synthetic */ void lambda$putDialogPhotos$50$MessagesStorage(int i, photos_Photos photos_photos) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM user_photos WHERE uid = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            int size = photos_photos.photos.size();
            for (int i2 = 0; i2 < size; i2++) {
                Photo photo = (Photo) photos_photos.photos.get(i2);
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void emptyMessagesMedia(ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$JuuPaXTqDHxyOHyZv3yqLysoaxA(this, arrayList));
    }

    public /* synthetic */ void lambda$emptyMessagesMedia$52$MessagesStorage(ArrayList arrayList) {
        try {
            Message TLdeserialize;
            int size;
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (TLdeserialize.media != null) {
                        int i;
                        File pathToAttach;
                        if (TLdeserialize.media.document != null) {
                            File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document, true);
                            if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                arrayList2.add(pathToAttach2);
                            }
                            size = TLdeserialize.media.document.thumbs.size();
                            for (i = 0; i < size; i++) {
                                pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i));
                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                    arrayList2.add(pathToAttach);
                                }
                            }
                            TLdeserialize.media.document = new TL_documentEmpty();
                        } else if (TLdeserialize.media.photo != null) {
                            size = TLdeserialize.media.photo.sizes.size();
                            for (i = 0; i < size; i++) {
                                pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i));
                                if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                    arrayList2.add(pathToAttach);
                                }
                            }
                            TLdeserialize.media.photo = new TL_photoEmpty();
                        }
                        TLdeserialize.media.flags &= -2;
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.date = queryFinalized.intValue(2);
                        TLdeserialize.dialog_id = queryFinalized.longValue(3);
                        arrayList3.add(TLdeserialize);
                    }
                }
            }
            queryFinalized.dispose();
            if (!arrayList3.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
                for (size = 0; size < arrayList3.size(); size++) {
                    TLdeserialize = (Message) arrayList3.get(size);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                    TLdeserialize.serializeToStream(nativeByteBuffer);
                    executeFast.requery();
                    executeFast.bindLong(1, (long) TLdeserialize.id);
                    executeFast.bindLong(2, TLdeserialize.dialog_id);
                    executeFast.bindInteger(3, MessageObject.getUnreadFlags(TLdeserialize));
                    executeFast.bindInteger(4, TLdeserialize.send_state);
                    executeFast.bindInteger(5, TLdeserialize.date);
                    executeFast.bindByteBuffer(6, nativeByteBuffer);
                    executeFast.bindInteger(7, MessageObject.isOut(TLdeserialize) ? 1 : 0);
                    executeFast.bindInteger(8, TLdeserialize.ttl);
                    if ((TLdeserialize.flags & 1024) != 0) {
                        executeFast.bindInteger(9, TLdeserialize.views);
                    } else {
                        executeFast.bindInteger(9, getMessageMediaType(TLdeserialize));
                    }
                    executeFast.bindInteger(10, 0);
                    executeFast.bindInteger(11, TLdeserialize.mentioned ? 1 : 0);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
                executeFast.dispose();
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$P9-5g7-aJ7QBqc_VJ5jZ0esuH0c(this, arrayList3));
            }
            getFileLoader().deleteFiles(arrayList2, 0);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$51$MessagesStorage(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public void updateMessagePollResults(long j, TL_poll tL_poll, TL_pollResults tL_pollResults) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$2IwIgVnWW5Vs7XUL6n8u7Fprya0(this, j, tL_poll, tL_pollResults));
    }

    public /* synthetic */ void lambda$updateMessagePollResults$53$MessagesStorage(long j, TL_poll tL_poll, TL_pollResults tL_pollResults) {
        ArrayList arrayList = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM polls WHERE id = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            while (queryFinalized.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Long.valueOf(queryFinalized.longValue(0)));
            }
            queryFinalized.dispose();
            if (arrayList != null) {
                this.database.beginTransaction();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    Long l = (Long) arrayList.get(i);
                    SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{l}), new Object[0]);
                    if (queryFinalized2.next()) {
                        NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            if (TLdeserialize.media instanceof TL_messageMediaPoll) {
                                TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) TLdeserialize.media;
                                if (tL_poll != null) {
                                    tL_messageMediaPoll.poll = tL_poll;
                                }
                                if (tL_pollResults != null) {
                                    MessageObject.updatePollResults(tL_messageMediaPoll, tL_pollResults);
                                }
                                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                                TLdeserialize.serializeToStream(nativeByteBuffer);
                                executeFast.requery();
                                executeFast.bindByteBuffer(1, nativeByteBuffer);
                                executeFast.bindLong(2, l.longValue());
                                executeFast.step();
                                nativeByteBuffer.reuse();
                                executeFast.dispose();
                            }
                        }
                    } else {
                        this.database.executeFast(String.format(Locale.US, "DELETE FROM polls WHERE mid = %d", new Object[]{l})).stepThis().dispose();
                    }
                    queryFinalized2.dispose();
                }
                this.database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getNewTask(ArrayList<Integer> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$FTgTVajChXZ68bOKWpTrJMU_d_4(this, arrayList));
    }

    public /* synthetic */ void lambda$getNewTask$54$MessagesStorage(ArrayList arrayList) {
        if (arrayList != null) {
            try {
                String join = TextUtils.join(",", arrayList);
                this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[]{join})).stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
        ArrayList arrayList2 = null;
        int i = -1;
        int i2 = 0;
        while (queryFinalized.next()) {
            long longValue = queryFinalized.longValue(0);
            if (i == -1) {
                i = (int) (longValue >> 32);
                if (i < 0) {
                    i = 0;
                }
            }
            i2 = queryFinalized.intValue(1);
            if (arrayList2 == null) {
                arrayList2 = new ArrayList();
            }
            arrayList2.add(Integer.valueOf((int) longValue));
        }
        queryFinalized.dispose();
        getMessagesController().processLoadedDeleteTask(i2, arrayList2, i);
    }

    public void markMentionMessageAsRead(int i, int i2, long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$NdiRUhIfM2_omUwC7FXbkC7OzGU(this, i, i2, j));
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$55$MessagesStorage(int i, int i2, long j) {
        long j2 = (long) i;
        if (i2 != 0) {
            j2 |= ((long) i2) << 32;
        }
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid = %d", new Object[]{Long.valueOf(j2)})).stepThis().dispose();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT unread_count_i FROM dialogs WHERE did = ");
            stringBuilder.append(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            i2 = queryFinalized.next() ? Math.max(0, queryFinalized.intValue(0) - 1) : 0;
            queryFinalized.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j)})).stepThis().dispose();
            LongSparseArray longSparseArray = new LongSparseArray(1);
            longSparseArray.put(j, Integer.valueOf(i2));
            getMessagesController().processDialogsUpdateRead(null, longSparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessageAsMention(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7e91SgaCx3Tvuefq7j0Wss_4Grg(this, j));
    }

    public /* synthetic */ void lambda$markMessageAsMention$56$MessagesStorage(long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$oYdXH4GPpZucm58TC-q3bCmR46I(this, i, j));
    }

    public /* synthetic */ void lambda$resetMentionsCount$57$MessagesStorage(int i, long j) {
        if (i == 0) {
            try {
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j)})).stepThis().dispose();
        LongSparseArray longSparseArray = new LongSparseArray(1);
        longSparseArray.put(j, Integer.valueOf(i));
        getMessagesController().processDialogsUpdateRead(null, longSparseArray);
    }

    public void createTaskForMid(int i, int i2, int i3, int i4, int i5, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ZFchkNQfjY6PI9H93K3e_OqpvBI(this, i3, i4, i5, i, i2, z));
    }

    public /* synthetic */ void lambda$createTaskForMid$59$MessagesStorage(int i, int i2, int i3, int i4, int i5, boolean z) {
        if (i <= i2) {
            i = i2;
        }
        i += i3;
        try {
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList = new ArrayList();
            long j = (long) i4;
            if (i5 != 0) {
                j |= ((long) i5) << 32;
            }
            arrayList.add(Long.valueOf(j));
            sparseArray.put(i, arrayList);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$Cr8bLmThr8qfKRO5b1tbCS8nTYU(this, z, arrayList));
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            for (i5 = 0; i5 < sparseArray.size(); i5++) {
                int keyAt = sparseArray.keyAt(i5);
                ArrayList arrayList2 = (ArrayList) sparseArray.get(keyAt);
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    executeFast.requery();
                    executeFast.bindLong(1, ((Long) arrayList2.get(i6)).longValue());
                    executeFast.bindInteger(2, keyAt);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            getMessagesController().didAddedNewTask(i, sparseArray);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$58$MessagesStorage(boolean z, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(arrayList, 0);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
    }

    public void createTaskForSecretChat(int i, int i2, int i3, int i4, ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6Xpb1ARqvCA75HKnKlpk1c-S4-A(this, arrayList, i, i4, i2, i3));
    }

    public /* synthetic */ void lambda$createTaskForSecretChat$61$MessagesStorage(ArrayList arrayList, int i, int i2, int i3, int i4) {
        ArrayList arrayList2 = arrayList;
        int i5 = Integer.MAX_VALUE;
        try {
            SQLiteCursor queryFinalized;
            ArrayList arrayList3;
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList4 = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            String str = ",";
            int i6 = 0;
            if (arrayList2 == null) {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[]{Long.valueOf(((long) i) << 32), Integer.valueOf(i2), Integer.valueOf(i3)}), new Object[0]);
            } else {
                String join = TextUtils.join(str, arrayList2);
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[]{join}), new Object[0]);
            }
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(1);
                long intValue2 = (long) queryFinalized.intValue(i6);
                if (arrayList2 != null) {
                    arrayList4.add(Long.valueOf(intValue2));
                }
                if (intValue > 0) {
                    int i7 = i3;
                    int i8 = i4;
                    intValue = (i7 > i8 ? i7 : i8) + intValue;
                    i5 = Math.min(i5, intValue);
                    ArrayList arrayList5 = (ArrayList) sparseArray.get(intValue);
                    if (arrayList5 == null) {
                        arrayList3 = new ArrayList();
                        sparseArray.put(intValue, arrayList3);
                    } else {
                        arrayList3 = arrayList5;
                    }
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(str);
                    }
                    stringBuilder.append(intValue2);
                    arrayList3.add(Long.valueOf(intValue2));
                    i6 = 0;
                }
            }
            queryFinalized.dispose();
            if (arrayList2 != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$HaNGZilYB-ZCBxdRbbZWq9G6MCE(this, arrayList4));
            }
            if (sparseArray.size() != 0) {
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
                for (int i9 = 0; i9 < sparseArray.size(); i9++) {
                    int keyAt = sparseArray.keyAt(i9);
                    arrayList3 = (ArrayList) sparseArray.get(keyAt);
                    for (int i10 = 0; i10 < arrayList3.size(); i10++) {
                        executeFast.requery();
                        executeFast.bindLong(1, ((Long) arrayList3.get(i10)).longValue());
                        executeFast.bindInteger(2, keyAt);
                        executeFast.step();
                    }
                }
                executeFast.dispose();
                this.database.commitTransaction();
                this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[]{stringBuilder.toString()})).stepThis().dispose();
                getMessagesController().didAddedNewTask(i5, sparseArray);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$60$MessagesStorage(ArrayList arrayList) {
        markMessagesContentAsRead(arrayList, 0);
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> arrayList, SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList<Long> arrayList2) {
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        ArrayList<Long> arrayList3 = arrayList2;
        try {
            SQLitePreparedStatement executeFast;
            int i;
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            ArrayList arrayList4 = new ArrayList();
            String str = ",";
            int i2 = 2;
            SQLiteCursor queryFinalized;
            if (isEmpty((List) arrayList)) {
                long longValue;
                int i3;
                if (!isEmpty(sparseLongArray)) {
                    int i4 = 0;
                    while (i4 < sparseLongArray.size()) {
                        int keyAt = sparseLongArray3.keyAt(i4);
                        long j = sparseLongArray3.get(keyAt);
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Object[] objArr = new Object[i2];
                        objArr[0] = Integer.valueOf(keyAt);
                        objArr[1] = Long.valueOf(j);
                        queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", objArr), new Object[0]);
                        if (queryFinalized.next()) {
                            longSparseArray.put((long) keyAt, Integer.valueOf(queryFinalized.intValue(0)));
                        }
                        queryFinalized.dispose();
                        executeFast = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast.requery();
                        long j2 = (long) keyAt;
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, (int) j);
                        executeFast.bindLong(3, j2);
                        executeFast.step();
                        executeFast.dispose();
                        i4++;
                        sparseLongArray3 = sparseLongArray;
                        i2 = 2;
                    }
                }
                if (isEmpty((List) arrayList2)) {
                    i = 0;
                } else {
                    ArrayList arrayList5 = new ArrayList(arrayList3);
                    String join = TextUtils.join(str, arrayList3);
                    SQLiteDatabase sQLiteDatabase2 = this.database;
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
                                SQLiteDatabase sQLiteDatabase3 = this.database;
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
                    for (i3 = 0; i3 < arrayList5.size(); i3++) {
                        int longValue2 = (int) (((Long) arrayList5.get(i3)).longValue() >> 32);
                        if (longValue2 > 0 && !arrayList4.contains(Integer.valueOf(longValue2))) {
                            arrayList4.add(Integer.valueOf(longValue2));
                        }
                    }
                }
                if (!isEmpty(sparseLongArray2)) {
                    for (int i5 = 0; i5 < sparseLongArray2.size(); i5++) {
                        i3 = sparseLongArray4.keyAt(i5);
                        longValue = sparseLongArray4.get(i3);
                        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        executeFast2.requery();
                        long j3 = (long) i3;
                        executeFast2.bindLong(1, j3);
                        executeFast2.bindInteger(2, (int) longValue);
                        executeFast2.bindLong(3, j3);
                        executeFast2.step();
                        executeFast2.dispose();
                    }
                }
            } else {
                String join2 = TextUtils.join(str, arrayList);
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[]{join2}), new Object[0]);
                while (queryFinalized.next()) {
                    if (queryFinalized.intValue(2) == 0) {
                        if (queryFinalized.intValue(1) == 0) {
                            long longValue3 = queryFinalized.longValue(0);
                            Integer num2 = (Integer) longSparseArray.get(longValue3);
                            if (num2 == null) {
                                longSparseArray.put(longValue3, Integer.valueOf(1));
                            } else {
                                longSparseArray.put(longValue3, Integer.valueOf(num2.intValue() + 1));
                            }
                        }
                    }
                }
                queryFinalized.dispose();
                i = 0;
            }
            if (longSparseArray.size() > 0 || longSparseArray2.size() > 0) {
                this.database.beginTransaction();
                if (longSparseArray.size() > 0) {
                    executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                    for (int i6 = 0; i6 < longSparseArray.size(); i6++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, ((Integer) longSparseArray.valueAt(i6)).intValue());
                        executeFast.bindLong(2, longSparseArray.keyAt(i6));
                        executeFast.step();
                    }
                    executeFast.dispose();
                }
                if (longSparseArray2.size() > 0) {
                    executeFast = this.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
                    for (i = 
/*
Method generation error in method: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.SparseLongArray, org.telegram.messenger.support.SparseLongArray, java.util.ArrayList):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r10_26 'i' int) = (r10_1 'i' int), (r10_25 'i' int), (r10_25 'i' int) binds: {(r10_1 'i' int)=B:20:0x0071, (r10_25 'i' int)=B:61:0x01c0, (r10_25 'i' int)=B:107:0x01f3} in method: org.telegram.messenger.MessagesStorage.updateDialogsWithReadMessagesInternal(java.util.ArrayList, org.telegram.messenger.support.SparseLongArray, org.telegram.messenger.support.SparseLongArray, java.util.ArrayList):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 30 more

*/

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(SparseLongArray sparseLongArray) {
        return sparseLongArray == null || sparseLongArray.size() == 0;
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

    public void updateDialogsWithReadMessages(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList<Long> arrayList, boolean z) {
        if (!isEmpty(sparseLongArray) || !isEmpty((List) arrayList)) {
            if (z) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$nN3HdiEbpm1IQMdW7V3cpBVVldE(this, sparseLongArray, sparseLongArray2, arrayList));
            } else {
                updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithReadMessages$62$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
    }

    public void updateChatParticipants(ChatParticipants chatParticipants) {
        if (chatParticipants != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$D_ASTDdhwnJFq83QrFGrOPbuhQw(this, chatParticipants));
        }
    }

    public /* synthetic */ void lambda$updateChatParticipants$64$MessagesStorage(ChatParticipants chatParticipants) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(chatParticipants.chat_id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull instanceof TL_chatFull) {
                chatFull.participants = chatParticipants;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$RSCfHUqNo1GyDNuwKXrOvxwWC2A(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, chatFull.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$63$MessagesStorage(ChatFull chatFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void loadChannelAdmins(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$zWGYKlEbE3oxegAeAsXD1kylfA0(this, i));
    }

    public /* synthetic */ void lambda$loadChannelAdmins$65$MessagesStorage(int i) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT uid, rank FROM channel_admins_v2 WHERE did = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            SparseArray sparseArray = new SparseArray();
            while (queryFinalized.next()) {
                sparseArray.put(queryFinalized.intValue(0), queryFinalized.stringValue(1));
            }
            queryFinalized.dispose();
            getMessagesController().processLoadedChannelAdmins(sparseArray, i, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putChannelAdmins(int i, SparseArray<String> sparseArray) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$--jBiop6ROXWTt-Wbj_B5J9yz6c(this, i, sparseArray));
    }

    public /* synthetic */ void lambda$putChannelAdmins$66$MessagesStorage(int i, SparseArray sparseArray) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM channel_admins_v2 WHERE did = ");
            stringBuilder.append(i);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_admins_v2 VALUES(?, ?, ?)");
            long currentTimeMillis = System.currentTimeMillis() / 1000;
            for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindInteger(2, sparseArray.keyAt(i2));
                executeFast.bindString(3, (String) sparseArray.valueAt(i2));
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChannelUsers(int i, ArrayList<ChannelParticipant> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$gmh9sF-88S52zsqmaJlAuBxOllA(this, i, arrayList));
    }

    public /* synthetic */ void lambda$updateChannelUsers$67$MessagesStorage(int i, ArrayList arrayList) {
        long j = (long) (-i);
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM channel_users_v2 WHERE did = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                ChannelParticipant channelParticipant = (ChannelParticipant) arrayList.get(i2);
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
            this.database.commitTransaction();
            loadChatInfo(i, null, false, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveBotCache(String str, TLObject tLObject) {
        if (tLObject != null && !TextUtils.isEmpty(str)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$g0GDbKbBJBo4Mk-5LCkQxd1psZM(this, tLObject, str));
        }
    }

    public /* synthetic */ void lambda$saveBotCache$68$MessagesStorage(TLObject tLObject, String str) {
        try {
            int i;
            SQLitePreparedStatement executeFast;
            NativeByteBuffer nativeByteBuffer;
            int currentTime = getConnectionsManager().getCurrentTime();
            if (tLObject instanceof TL_messages_botCallbackAnswer) {
                i = ((TL_messages_botCallbackAnswer) tLObject).cache_time;
            } else {
                if (tLObject instanceof TL_messages_botResults) {
                    i = ((TL_messages_botResults) tLObject).cache_time;
                }
                executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindInteger(2, currentTime);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
            currentTime += i;
            executeFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject.serializeToStream(nativeByteBuffer);
            executeFast.bindString(1, str);
            executeFast.bindInteger(2, currentTime);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getBotCache(String str, RequestDelegate requestDelegate) {
        if (str != null && requestDelegate != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$YmYOyjs0tsBC1kvjpoQ8AIm3mXg(this, getConnectionsManager().getCurrentTime(), str, requestDelegate));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005d A:{Splitter:B:1:0x0001, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:21:0x005d, code skipped:
            r5 = th;
     */
    /* JADX WARNING: Missing block: B:22:0x005e, code skipped:
            r1 = null;
     */
    /* JADX WARNING: Missing block: B:23:0x0060, code skipped:
            r5 = e;
     */
    /* JADX WARNING: Missing block: B:24:0x0061, code skipped:
            r1 = null;
     */
    public /* synthetic */ void lambda$getBotCache$69$MessagesStorage(int r5, java.lang.String r6, org.telegram.tgnet.RequestDelegate r7) {
        /*
        r4 = this;
        r0 = 0;
        r1 = r4.database;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2.<init>();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r3 = "DELETE FROM botcache WHERE date < ";
        r2.append(r3);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r2.append(r5);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r2.toString();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r1.executeFast(r5);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r5.stepThis();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5.dispose();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r4.database;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r1 = "SELECT data FROM botcache WHERE id = ?";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r3 = 0;
        r2[r3] = r6;	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r5 = r5.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        r6 = r5.next();	 Catch:{ Exception -> 0x0060, all -> 0x005d }
        if (r6 == 0) goto L_0x0056;
    L_0x0033:
        r6 = r5.byteBufferValue(r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        if (r6 == 0) goto L_0x0056;
    L_0x0039:
        r1 = r6.readInt32(r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        r2 = org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer.constructor;	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        if (r1 != r2) goto L_0x0046;
    L_0x0041:
        r1 = org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer.TLdeserialize(r6, r1, r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
        goto L_0x004a;
    L_0x0046:
        r1 = org.telegram.tgnet.TLRPC.messages_BotResults.TLdeserialize(r6, r1, r3);	 Catch:{ Exception -> 0x0050, all -> 0x005d }
    L_0x004a:
        r6.reuse();	 Catch:{ Exception -> 0x004e }
        goto L_0x0057;
    L_0x004e:
        r6 = move-exception;
        goto L_0x0052;
    L_0x0050:
        r6 = move-exception;
        r1 = r0;
    L_0x0052:
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x005b }
        goto L_0x0057;
    L_0x0056:
        r1 = r0;
    L_0x0057:
        r5.dispose();	 Catch:{ Exception -> 0x005b }
        goto L_0x0065;
    L_0x005b:
        r5 = move-exception;
        goto L_0x0062;
    L_0x005d:
        r5 = move-exception;
        r1 = r0;
        goto L_0x006a;
    L_0x0060:
        r5 = move-exception;
        r1 = r0;
    L_0x0062:
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x0069 }
    L_0x0065:
        r7.run(r1, r0);
        return;
    L_0x0069:
        r5 = move-exception;
    L_0x006a:
        r7.run(r1, r0);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getBotCache$69$MessagesStorage(int, java.lang.String, org.telegram.tgnet.RequestDelegate):void");
    }

    public void loadUserInfo(User user, boolean z, int i) {
        if (user != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$tq7IpBeCYjYkwzm5phDtrpzCLmM(this, user, z, i));
        }
    }

    public /* synthetic */ void lambda$loadUserInfo$70$MessagesStorage(User user, boolean z, int i) {
        UserFull userFull;
        Throwable e;
        User user2 = user;
        MessageObject messageObject = null;
        UserFull userFull2 = null;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned FROM user_settings WHERE uid = ");
            stringBuilder.append(user2.id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    userFull2 = UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    userFull2.pinned_msg_id = queryFinalized.intValue(1);
                }
            }
            userFull = userFull2;
            try {
                queryFinalized.dispose();
                if (!(userFull == null || userFull.pinned_msg_id == 0)) {
                    messageObject = getMediaDataController().loadPinnedMessage((long) user2.id, 0, userFull.pinned_msg_id, false);
                }
            } catch (Exception e2) {
                e = e2;
                try {
                    FileLog.e(e);
                    getMessagesController().processUserInfo(user, userFull, true, z, messageObject, i);
                } catch (Throwable th) {
                    e = th;
                    getMessagesController().processUserInfo(user, userFull, true, z, null, i);
                    throw e;
                }
            }
        } catch (Exception e3) {
            e = e3;
            userFull = null;
            FileLog.e(e);
            getMessagesController().processUserInfo(user, userFull, true, z, messageObject, i);
        } catch (Throwable th2) {
            e = th2;
            userFull = null;
            getMessagesController().processUserInfo(user, userFull, true, z, null, i);
            throw e;
        }
        getMessagesController().processUserInfo(user, userFull, true, z, messageObject, i);
    }

    public void updateUserInfo(UserFull userFull, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$qUGoyr9OJRn0n887IISd6C5U99I(this, z, userFull));
    }

    public /* synthetic */ void lambda$updateUserInfo$71$MessagesStorage(boolean z, UserFull userFull) {
        if (z) {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT uid FROM user_settings WHERE uid = ");
                stringBuilder.append(userFull.user.id);
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                boolean next = queryFinalized.next();
                queryFinalized.dispose();
                if (!next) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(userFull.getObjectSize());
        userFull.serializeToStream(nativeByteBuffer);
        executeFast.bindInteger(1, userFull.user.id);
        executeFast.bindByteBuffer(2, nativeByteBuffer);
        executeFast.bindInteger(3, userFull.pinned_msg_id);
        executeFast.step();
        executeFast.dispose();
        nativeByteBuffer.reuse();
    }

    public void updateChatInfo(ChatFull chatFull, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$OKeP4q1v4z3JZ1_XKv68_dbHcic(this, chatFull, z));
    }

    public /* synthetic */ void lambda$updateChatInfo$72$MessagesStorage(ChatFull chatFull, boolean z) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(chatFull.id);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
            queryFinalized.dispose();
            if (!z || intValue != -1) {
                if (intValue >= 0 && (chatFull.flags & 8192) == 0) {
                    chatFull.online_count = intValue;
                }
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, chatFull.id);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
                if (chatFull instanceof TL_channelFull) {
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("SELECT inbox_max, outbox_max FROM dialogs WHERE did = ");
                    stringBuilder2.append(-chatFull.id);
                    SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(stringBuilder2.toString(), new Object[0]);
                    if (queryFinalized2.next() && queryFinalized2.intValue(0) < chatFull.read_inbox_max_id) {
                        int intValue2 = queryFinalized2.intValue(1);
                        SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ?, outbox_max = ? WHERE did = ?");
                        executeFast2.bindInteger(1, chatFull.unread_count);
                        executeFast2.bindInteger(2, chatFull.read_inbox_max_id);
                        executeFast2.bindInteger(3, Math.max(intValue2, chatFull.read_outbox_max_id));
                        executeFast2.bindLong(4, (long) (-chatFull.id));
                        executeFast2.step();
                        executeFast2.dispose();
                    }
                    queryFinalized2.dispose();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateUserPinnedMessage(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$JKTvar_IvqTKc0Qzs6wno7V8SYZ4(this, i, i2));
    }

    public /* synthetic */ void lambda$updateUserPinnedMessage$74$MessagesStorage(int i, int i2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned FROM user_settings WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            UserFull userFull = null;
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    userFull = UserFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    userFull.pinned_msg_id = queryFinalized.intValue(1);
                }
            }
            queryFinalized.dispose();
            if (userFull instanceof UserFull) {
                userFull.pinned_msg_id = i2;
                userFull.flags |= 64;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$N8JJaRJQ-XeOxmqOmE1N4vyrUOw(this, i, userFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_settings VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(userFull.getObjectSize());
                userFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, userFull.pinned_msg_id);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$73$MessagesStorage(int i, UserFull userFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(i), userFull, null);
    }

    public void updateChatOnlineCount(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$BICna-7VAaCSPqIfWWrzjWQ0ZfM(this, i2, i));
    }

    public /* synthetic */ void lambda$updateChatOnlineCount$75$MessagesStorage(int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateChatPinnedMessage(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ztmgJOylrHzKLmmfzc_zHyaG2N8(this, i, i2));
    }

    public /* synthetic */ void lambda$updateChatPinnedMessage$77$MessagesStorage(int i, int i2) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull != null) {
                if (chatFull instanceof TL_channelFull) {
                    chatFull.pinned_msg_id = i2;
                    chatFull.flags |= 32;
                } else if (chatFull instanceof TL_chatFull) {
                    chatFull.pinned_msg_id = i2;
                    chatFull.flags |= 64;
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$ZGUW3NfKCxYo6AlfLx9rvvpfRDk(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$76$MessagesStorage(ChatFull chatFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChatInfo(int i, int i2, int i3, int i4, int i5) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$LEdNU3Qn_un_qpBfsAic2DM_-_U(this, i, i3, i2, i4, i5));
    }

    public /* synthetic */ void lambda$updateChatInfo$79$MessagesStorage(int i, int i2, int i3, int i4, int i5) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            int i6 = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    chatFull.pinned_msg_id = queryFinalized.intValue(1);
                    chatFull.online_count = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (chatFull instanceof TL_chatFull) {
                if (i2 == 1) {
                    while (i6 < chatFull.participants.participants.size()) {
                        if (((ChatParticipant) chatFull.participants.participants.get(i6)).user_id == i3) {
                            chatFull.participants.participants.remove(i6);
                            break;
                        }
                        i6++;
                    }
                } else if (i2 == 0) {
                    Iterator it = chatFull.participants.participants.iterator();
                    while (it.hasNext()) {
                        if (((ChatParticipant) it.next()).user_id == i3) {
                            return;
                        }
                    }
                    TL_chatParticipant tL_chatParticipant = new TL_chatParticipant();
                    tL_chatParticipant.user_id = i3;
                    tL_chatParticipant.inviter_id = i4;
                    tL_chatParticipant.date = getConnectionsManager().getCurrentTime();
                    chatFull.participants.participants.add(tL_chatParticipant);
                } else if (i2 == 2) {
                    while (i6 < chatFull.participants.participants.size()) {
                        ChatParticipant chatParticipant = (ChatParticipant) chatFull.participants.participants.get(i6);
                        if (chatParticipant.user_id == i3) {
                            Object tL_chatParticipantAdmin;
                            if (i4 == 1) {
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
                            chatFull.participants.participants.set(i6, tL_chatParticipantAdmin);
                        } else {
                            i6++;
                        }
                    }
                }
                chatFull.participants.version = i5;
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$vSUND9_zfjgvr5DwoG-QrQICRyA(this, chatFull));
                SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFull.getObjectSize());
                chatFull.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, i);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, chatFull.pinned_msg_id);
                executeFast.bindInteger(4, chatFull.online_count);
                executeFast.step();
                executeFast.dispose();
                nativeByteBuffer.reuse();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$78$MessagesStorage(ChatFull chatFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public boolean isMigratedChat(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$96p47CIy_y9hYQtLjW2J811S5Nk(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isMigratedChat$80$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT info FROM chat_settings_v2 WHERE uid = ");
            stringBuilder.append(i);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            ChatFull chatFull = null;
            ArrayList arrayList = new ArrayList();
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chatFull = ChatFull.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            boolean z = (chatFull instanceof TL_channelFull) && chatFull.migrated_from_chat_id != 0;
            zArr[0] = z;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            if (countDownLatch == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (countDownLatch == null) {
                return;
            }
        } catch (Throwable e2) {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            throw e2;
        }
        countDownLatch.countDown();
    }

    public ChatFull loadChatInfo(int i, CountDownLatch countDownLatch, boolean z, boolean z2) {
        ChatFull[] chatFullArr = new ChatFull[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$dIozo8kfsDHYuHDxJ-yMkb7emM0(this, i, chatFullArr, z, z2, countDownLatch));
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Throwable unused) {
            }
        }
        return chatFullArr[0];
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x008f A:{Catch:{ Exception -> 0x0186 }} */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0055 A:{SYNTHETIC, Splitter:B:13:0x0055} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0168 A:{Catch:{ Exception -> 0x0186 }} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0166 A:{Catch:{ Exception -> 0x0186 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01bc  */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A:{SYNTHETIC, RETURN} */
    public /* synthetic */ void lambda$loadChatInfo$81$MessagesStorage(int r18, org.telegram.tgnet.TLRPC.ChatFull[] r19, boolean r20, boolean r21, java.util.concurrent.CountDownLatch r22) {
        /*
        r17 = this;
        r1 = r17;
        r3 = r18;
        r5 = new java.util.ArrayList;
        r5.<init>();
        r2 = 0;
        r4 = 0;
        r9 = 0;
        r0 = r1.database;	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6.<init>();	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r7 = "SELECT info, pinned, online FROM chat_settings_v2 WHERE uid = ";
        r6.append(r7);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6.append(r3);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r7 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r0 = r0.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6 = r0.next();	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r7 = 2;
        r8 = 1;
        if (r6 == 0) goto L_0x004b;
    L_0x002d:
        r6 = r0.byteBufferValue(r4);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        if (r6 == 0) goto L_0x004b;
    L_0x0033:
        r10 = r6.readInt32(r4);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r10 = org.telegram.tgnet.TLRPC.ChatFull.TLdeserialize(r6, r10, r4);	 Catch:{ Exception -> 0x018b, all -> 0x0188 }
        r6.reuse();	 Catch:{ Exception -> 0x0186 }
        r6 = r0.intValue(r8);	 Catch:{ Exception -> 0x0186 }
        r10.pinned_msg_id = r6;	 Catch:{ Exception -> 0x0186 }
        r6 = r0.intValue(r7);	 Catch:{ Exception -> 0x0186 }
        r10.online_count = r6;	 Catch:{ Exception -> 0x0186 }
        goto L_0x004c;
    L_0x004b:
        r10 = r2;
    L_0x004c:
        r0.dispose();	 Catch:{ Exception -> 0x0186 }
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_chatFull;	 Catch:{ Exception -> 0x0186 }
        r6 = ",";
        if (r0 == 0) goto L_0x008f;
    L_0x0055:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0186 }
        r0.<init>();	 Catch:{ Exception -> 0x0186 }
        r2 = 0;
    L_0x005b:
        r7 = r10.participants;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.participants;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0186 }
        if (r2 >= r7) goto L_0x0080;
    L_0x0065:
        r7 = r10.participants;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.participants;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.get(r2);	 Catch:{ Exception -> 0x0186 }
        r7 = (org.telegram.tgnet.TLRPC.ChatParticipant) r7;	 Catch:{ Exception -> 0x0186 }
        r8 = r0.length();	 Catch:{ Exception -> 0x0186 }
        if (r8 == 0) goto L_0x0078;
    L_0x0075:
        r0.append(r6);	 Catch:{ Exception -> 0x0186 }
    L_0x0078:
        r7 = r7.user_id;	 Catch:{ Exception -> 0x0186 }
        r0.append(r7);	 Catch:{ Exception -> 0x0186 }
        r2 = r2 + 1;
        goto L_0x005b;
    L_0x0080:
        r2 = r0.length();	 Catch:{ Exception -> 0x0186 }
        if (r2 == 0) goto L_0x0156;
    L_0x0086:
        r0 = r0.toString();	 Catch:{ Exception -> 0x0186 }
        r1.getUsersInternal(r0, r5);	 Catch:{ Exception -> 0x0186 }
        goto L_0x0156;
    L_0x008f:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channelFull;	 Catch:{ Exception -> 0x0186 }
        if (r0 == 0) goto L_0x0156;
    L_0x0093:
        r0 = r1.database;	 Catch:{ Exception -> 0x0186 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0186 }
        r11.<init>();	 Catch:{ Exception -> 0x0186 }
        r12 = "SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = ";
        r11.append(r12);	 Catch:{ Exception -> 0x0186 }
        r12 = -r3;
        r11.append(r12);	 Catch:{ Exception -> 0x0186 }
        r12 = " ORDER BY cu.date DESC";
        r11.append(r12);	 Catch:{ Exception -> 0x0186 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0186 }
        r12 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0186 }
        r11 = r0.queryFinalized(r11, r12);	 Catch:{ Exception -> 0x0186 }
        r0 = new org.telegram.tgnet.TLRPC$TL_chatParticipants;	 Catch:{ Exception -> 0x0186 }
        r0.<init>();	 Catch:{ Exception -> 0x0186 }
        r10.participants = r0;	 Catch:{ Exception -> 0x0186 }
    L_0x00b9:
        r0 = r11.next();	 Catch:{ Exception -> 0x0186 }
        if (r0 == 0) goto L_0x011f;
    L_0x00bf:
        r0 = r11.byteBufferValue(r4);	 Catch:{ Exception -> 0x011a }
        if (r0 == 0) goto L_0x00d1;
    L_0x00c5:
        r12 = r0.readInt32(r4);	 Catch:{ Exception -> 0x011a }
        r12 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r0, r12, r4);	 Catch:{ Exception -> 0x011a }
        r0.reuse();	 Catch:{ Exception -> 0x011a }
        goto L_0x00d2;
    L_0x00d1:
        r12 = r2;
    L_0x00d2:
        r0 = r11.byteBufferValue(r7);	 Catch:{ Exception -> 0x011a }
        if (r0 == 0) goto L_0x00e4;
    L_0x00d8:
        r13 = r0.readInt32(r4);	 Catch:{ Exception -> 0x011a }
        r13 = org.telegram.tgnet.TLRPC.ChannelParticipant.TLdeserialize(r0, r13, r4);	 Catch:{ Exception -> 0x011a }
        r0.reuse();	 Catch:{ Exception -> 0x011a }
        goto L_0x00e5;
    L_0x00e4:
        r13 = r2;
    L_0x00e5:
        if (r12 == 0) goto L_0x00b9;
    L_0x00e7:
        if (r13 == 0) goto L_0x00b9;
    L_0x00e9:
        r0 = r12.status;	 Catch:{ Exception -> 0x011a }
        if (r0 == 0) goto L_0x00f5;
    L_0x00ed:
        r0 = r12.status;	 Catch:{ Exception -> 0x011a }
        r14 = r11.intValue(r8);	 Catch:{ Exception -> 0x011a }
        r0.expires = r14;	 Catch:{ Exception -> 0x011a }
    L_0x00f5:
        r5.add(r12);	 Catch:{ Exception -> 0x011a }
        r0 = 3;
        r0 = r11.intValue(r0);	 Catch:{ Exception -> 0x011a }
        r13.date = r0;	 Catch:{ Exception -> 0x011a }
        r0 = new org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;	 Catch:{ Exception -> 0x011a }
        r0.<init>();	 Catch:{ Exception -> 0x011a }
        r12 = r13.user_id;	 Catch:{ Exception -> 0x011a }
        r0.user_id = r12;	 Catch:{ Exception -> 0x011a }
        r12 = r13.date;	 Catch:{ Exception -> 0x011a }
        r0.date = r12;	 Catch:{ Exception -> 0x011a }
        r12 = r13.inviter_id;	 Catch:{ Exception -> 0x011a }
        r0.inviter_id = r12;	 Catch:{ Exception -> 0x011a }
        r0.channelParticipant = r13;	 Catch:{ Exception -> 0x011a }
        r12 = r10.participants;	 Catch:{ Exception -> 0x011a }
        r12 = r12.participants;	 Catch:{ Exception -> 0x011a }
        r12.add(r0);	 Catch:{ Exception -> 0x011a }
        goto L_0x00b9;
    L_0x011a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0186 }
        goto L_0x00b9;
    L_0x011f:
        r11.dispose();	 Catch:{ Exception -> 0x0186 }
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0186 }
        r0.<init>();	 Catch:{ Exception -> 0x0186 }
        r2 = 0;
    L_0x0128:
        r7 = r10.bot_info;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0186 }
        if (r2 >= r7) goto L_0x0149;
    L_0x0130:
        r7 = r10.bot_info;	 Catch:{ Exception -> 0x0186 }
        r7 = r7.get(r2);	 Catch:{ Exception -> 0x0186 }
        r7 = (org.telegram.tgnet.TLRPC.BotInfo) r7;	 Catch:{ Exception -> 0x0186 }
        r8 = r0.length();	 Catch:{ Exception -> 0x0186 }
        if (r8 == 0) goto L_0x0141;
    L_0x013e:
        r0.append(r6);	 Catch:{ Exception -> 0x0186 }
    L_0x0141:
        r7 = r7.user_id;	 Catch:{ Exception -> 0x0186 }
        r0.append(r7);	 Catch:{ Exception -> 0x0186 }
        r2 = r2 + 1;
        goto L_0x0128;
    L_0x0149:
        r2 = r0.length();	 Catch:{ Exception -> 0x0186 }
        if (r2 == 0) goto L_0x0156;
    L_0x014f:
        r0 = r0.toString();	 Catch:{ Exception -> 0x0186 }
        r1.getUsersInternal(r0, r5);	 Catch:{ Exception -> 0x0186 }
    L_0x0156:
        if (r10 == 0) goto L_0x0172;
    L_0x0158:
        r0 = r10.pinned_msg_id;	 Catch:{ Exception -> 0x0186 }
        if (r0 == 0) goto L_0x0172;
    L_0x015c:
        r11 = r17.getMediaDataController();	 Catch:{ Exception -> 0x0186 }
        r0 = -r3;
        r12 = (long) r0;	 Catch:{ Exception -> 0x0186 }
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channelFull;	 Catch:{ Exception -> 0x0186 }
        if (r0 == 0) goto L_0x0168;
    L_0x0166:
        r14 = r3;
        goto L_0x0169;
    L_0x0168:
        r14 = 0;
    L_0x0169:
        r15 = r10.pinned_msg_id;	 Catch:{ Exception -> 0x0186 }
        r16 = 0;
        r0 = r11.loadPinnedMessage(r12, r14, r15, r16);	 Catch:{ Exception -> 0x0186 }
        r9 = r0;
    L_0x0172:
        r19[r4] = r10;
        r2 = r17.getMessagesController();
        r6 = 1;
        r3 = r18;
        r4 = r10;
        r7 = r20;
        r8 = r21;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r22 == 0) goto L_0x01a6;
    L_0x0185:
        goto L_0x01a3;
    L_0x0186:
        r0 = move-exception;
        goto L_0x018d;
    L_0x0188:
        r0 = move-exception;
        r10 = r2;
        goto L_0x01a8;
    L_0x018b:
        r0 = move-exception;
        r10 = r2;
    L_0x018d:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01a7 }
        r19[r4] = r10;
        r2 = r17.getMessagesController();
        r6 = 1;
        r3 = r18;
        r4 = r10;
        r7 = r20;
        r8 = r21;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r22 == 0) goto L_0x01a6;
    L_0x01a3:
        r22.countDown();
    L_0x01a6:
        return;
    L_0x01a7:
        r0 = move-exception;
    L_0x01a8:
        r19[r4] = r10;
        r2 = r17.getMessagesController();
        r6 = 1;
        r9 = 0;
        r3 = r18;
        r4 = r10;
        r7 = r20;
        r8 = r21;
        r2.processChatInfo(r3, r4, r5, r6, r7, r8, r9);
        if (r22 == 0) goto L_0x01bf;
    L_0x01bc:
        r22.countDown();
    L_0x01bf:
        goto L_0x01c1;
    L_0x01c0:
        throw r0;
    L_0x01c1:
        goto L_0x01c0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadChatInfo$81$MessagesStorage(int, org.telegram.tgnet.TLRPC$ChatFull[], boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public void processPendingRead(long j, long j2, long j3, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Zr2KjobVaHBebDYy8lG0JDq4N14(this, j, j2, z, this.lastSavedDate, j3));
    }

    public /* synthetic */ void lambda$processPendingRead$82$MessagesStorage(long j, long j2, boolean z, int i, long j3) {
        long j4 = j;
        try {
            int intValue;
            long longValue;
            SQLitePreparedStatement executeFast;
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = ");
            stringBuilder.append(j4);
            int i2 = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            long j5 = 0;
            if (queryFinalized.next()) {
                intValue = queryFinalized.intValue(0);
                j5 = (long) queryFinalized.intValue(1);
                longValue = queryFinalized.longValue(2);
            } else {
                longValue = 0;
                intValue = 0;
            }
            queryFinalized.dispose();
            this.database.beginTransaction();
            int i3 = (int) j4;
            String str = "SELECT changes()";
            int intValue2;
            if (i3 != 0) {
                j5 = Math.max(j5, (long) ((int) j2));
                if (z) {
                    j5 |= ((long) (-i3)) << 32;
                }
                executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                if (j5 < longValue) {
                    queryFinalized = this.database.queryFinalized(str, new Object[0]);
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                    queryFinalized.dispose();
                    i2 = Math.max(0, intValue - intValue2);
                }
                executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND date <= ?");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, (long) i);
                executeFast.step();
                executeFast.dispose();
            } else {
                j5 = (long) ((int) j3);
                executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                if (j5 > longValue) {
                    queryFinalized = this.database.queryFinalized(str, new Object[0]);
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) : 0;
                    queryFinalized.dispose();
                    i2 = Math.max(0, intValue - intValue2);
                }
            }
            executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i2);
            executeFast.bindInteger(2, (int) j5);
            executeFast.bindLong(3, j4);
            executeFast.step();
            executeFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putContacts(ArrayList<TL_contact> arrayList, boolean z) {
        if (!arrayList.isEmpty() || z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$O2JlfG9dSBl-P6Tes40TM0sWrhY(this, z, new ArrayList(arrayList)));
        }
    }

    public /* synthetic */ void lambda$putContacts$83$MessagesStorage(boolean z, ArrayList arrayList) {
        if (z) {
            try {
                this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.beginTransaction();
        SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
        for (int i = 0; i < arrayList.size(); i++) {
            TL_contact tL_contact = (TL_contact) arrayList.get(i);
            executeFast.requery();
            int i2 = 1;
            executeFast.bindInteger(1, tL_contact.user_id);
            if (!tL_contact.mutual) {
                i2 = 0;
            }
            executeFast.bindInteger(2, i2);
            executeFast.step();
        }
        executeFast.dispose();
        this.database.commitTransaction();
    }

    public void deleteContacts(ArrayList<Integer> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$V08xw7bRitoFTLsjmAh4o_aL_J8(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$deleteContacts$84$MessagesStorage(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM contacts WHERE uid IN(");
            stringBuilder.append(join);
            stringBuilder.append(")");
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void applyPhoneBookUpdates(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$CnEmXA6U_R4qXRXXhvRrbR0s9eU(this, str, str2));
        }
    }

    public /* synthetic */ void lambda$applyPhoneBookUpdates$85$MessagesStorage(String str, String str2) {
        try {
            if (str.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", new Object[]{str})).stepThis().dispose();
            }
            if (str2.length() != 0) {
                this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", new Object[]{str2})).stepThis().dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putCachedPhoneBook(HashMap<String, Contact> hashMap, boolean z, boolean z2) {
        if (hashMap == null) {
            return;
        }
        if (!hashMap.isEmpty() || z || z2) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$CYVrzStZJIUelApaffNB8fdYJyQ(this, hashMap, z));
        }
    }

    public /* synthetic */ void lambda$putCachedPhoneBook$86$MessagesStorage(HashMap hashMap, boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.currentAccount);
                stringBuilder.append(" save contacts to db ");
                stringBuilder.append(hashMap.size());
                FileLog.d(stringBuilder.toString());
            }
            this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
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
            this.database.commitTransaction();
            if (z) {
                this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
                this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
                getCachedPhoneBook(false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getCachedPhoneBook(boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$NTXmmyOdj1DXBWjwk-eh67iEfZc(this, z));
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0103 A:{Catch:{ Throwable -> 0x014b, all -> 0x0148 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0103 A:{Catch:{ Throwable -> 0x014b, all -> 0x0148 }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x024f  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0148 A:{Splitter:B:62:0x00fd, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0148 A:{Splitter:B:62:0x00fd, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0103 A:{Catch:{ Throwable -> 0x014b, all -> 0x0148 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0184 A:{Catch:{ Exception -> 0x021e }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x016a A:{SYNTHETIC, Splitter:B:98:0x016a} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0195 A:{Catch:{ Exception -> 0x0216, all -> 0x0214 }} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0227  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x024f  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0248  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:78:0x0138, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:79:0x0139, code skipped:
            r17 = r3;
     */
    /* JADX WARNING: Missing block: B:84:0x0148, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:85:0x014b, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:86:0x014c, code skipped:
            r17 = r3;
     */
    /* JADX WARNING: Missing block: B:95:0x015e, code skipped:
            r17.dispose();
     */
    /* JADX WARNING: Missing block: B:144:0x0248, code skipped:
            r3.dispose();
     */
    public /* synthetic */ void lambda$getCachedPhoneBook$87$MessagesStorage(boolean r27) {
        /*
        r26 = this;
        r1 = r26;
        r2 = "";
        r3 = 6;
        r4 = 3;
        r5 = 2;
        r6 = 5;
        r7 = 4;
        r9 = 1;
        r11 = 8;
        r12 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r13 = 0;
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r14 = "SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'";
        r15 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r0 = r14.next();	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r14.dispose();	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        if (r0 == 0) goto L_0x00d9;
    L_0x0022:
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r14 = "SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1";
        r15 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r0 = r14.next();	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        if (r0 == 0) goto L_0x003b;
    L_0x0032:
        r0 = r14.intValue(r13);	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r0 = java.lang.Math.min(r12, r0);	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        goto L_0x003d;
    L_0x003b:
        r0 = 16;
    L_0x003d:
        r14.dispose();	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r15 = new android.util.SparseArray;	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r15.<init>(r0);	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r0 = r1.database;	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r8 = "SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1";
        r10 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
        r10 = r0.queryFinalized(r8, r10);	 Catch:{ Throwable -> 0x00e0, all -> 0x00dc }
    L_0x004f:
        r0 = r10.next();	 Catch:{ Throwable -> 0x00d7 }
        if (r0 == 0) goto L_0x00cc;
    L_0x0055:
        r0 = r10.intValue(r13);	 Catch:{ Throwable -> 0x00d7 }
        r8 = r15.get(r0);	 Catch:{ Throwable -> 0x00d7 }
        r8 = (org.telegram.messenger.ContactsController.Contact) r8;	 Catch:{ Throwable -> 0x00d7 }
        if (r8 != 0) goto L_0x0089;
    L_0x0061:
        r8 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Throwable -> 0x00d7 }
        r8.<init>();	 Catch:{ Throwable -> 0x00d7 }
        r14 = r10.stringValue(r9);	 Catch:{ Throwable -> 0x00d7 }
        r8.first_name = r14;	 Catch:{ Throwable -> 0x00d7 }
        r14 = r10.stringValue(r5);	 Catch:{ Throwable -> 0x00d7 }
        r8.last_name = r14;	 Catch:{ Throwable -> 0x00d7 }
        r14 = r10.intValue(r3);	 Catch:{ Throwable -> 0x00d7 }
        r8.imported = r14;	 Catch:{ Throwable -> 0x00d7 }
        r14 = r8.first_name;	 Catch:{ Throwable -> 0x00d7 }
        if (r14 != 0) goto L_0x007e;
    L_0x007c:
        r8.first_name = r2;	 Catch:{ Throwable -> 0x00d7 }
    L_0x007e:
        r14 = r8.last_name;	 Catch:{ Throwable -> 0x00d7 }
        if (r14 != 0) goto L_0x0084;
    L_0x0082:
        r8.last_name = r2;	 Catch:{ Throwable -> 0x00d7 }
    L_0x0084:
        r8.contact_id = r0;	 Catch:{ Throwable -> 0x00d7 }
        r15.put(r0, r8);	 Catch:{ Throwable -> 0x00d7 }
    L_0x0089:
        r0 = r10.stringValue(r4);	 Catch:{ Throwable -> 0x00d7 }
        if (r0 != 0) goto L_0x0090;
    L_0x008f:
        goto L_0x004f;
    L_0x0090:
        r14 = r8.phones;	 Catch:{ Throwable -> 0x00d7 }
        r14.add(r0);	 Catch:{ Throwable -> 0x00d7 }
        r14 = r10.stringValue(r7);	 Catch:{ Throwable -> 0x00d7 }
        if (r14 != 0) goto L_0x009c;
    L_0x009b:
        goto L_0x004f;
    L_0x009c:
        r3 = r14.length();	 Catch:{ Throwable -> 0x00d7 }
        if (r3 != r11) goto L_0x00ac;
    L_0x00a2:
        r3 = r0.length();	 Catch:{ Throwable -> 0x00d7 }
        if (r3 == r11) goto L_0x00ac;
    L_0x00a8:
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ Throwable -> 0x00d7 }
    L_0x00ac:
        r0 = r8.shortPhones;	 Catch:{ Throwable -> 0x00d7 }
        r0.add(r14);	 Catch:{ Throwable -> 0x00d7 }
        r0 = r8.phoneDeleted;	 Catch:{ Throwable -> 0x00d7 }
        r3 = r10.intValue(r6);	 Catch:{ Throwable -> 0x00d7 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Throwable -> 0x00d7 }
        r0.add(r3);	 Catch:{ Throwable -> 0x00d7 }
        r0 = r8.phoneTypes;	 Catch:{ Throwable -> 0x00d7 }
        r0.add(r2);	 Catch:{ Throwable -> 0x00d7 }
        r0 = r15.size();	 Catch:{ Throwable -> 0x00d7 }
        if (r0 != r12) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00cc;
    L_0x00ca:
        r3 = 6;
        goto L_0x004f;
    L_0x00cc:
        r10.dispose();	 Catch:{ Throwable -> 0x00d7 }
        r0 = r26.getContactsController();	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        r0.migratePhoneBookToV7(r15);	 Catch:{ Throwable -> 0x00e7, all -> 0x00e3 }
        return;
    L_0x00d7:
        r0 = move-exception;
        goto L_0x00e9;
    L_0x00d9:
        r17 = 0;
        goto L_0x00f3;
    L_0x00dc:
        r0 = move-exception;
        r10 = r14;
        goto L_0x024d;
    L_0x00e0:
        r0 = move-exception;
        r10 = r14;
        goto L_0x00e9;
    L_0x00e3:
        r0 = move-exception;
        r10 = 0;
        goto L_0x024d;
    L_0x00e7:
        r0 = move-exception;
        r10 = 0;
    L_0x00e9:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x024c }
        if (r10 == 0) goto L_0x00f1;
    L_0x00ee:
        r10.dispose();
    L_0x00f1:
        r17 = r10;
    L_0x00f3:
        r0 = r1.database;	 Catch:{ Throwable -> 0x0154 }
        r3 = "SELECT COUNT(key) FROM user_contacts_v7 WHERE 1";
        r8 = new java.lang.Object[r13];	 Catch:{ Throwable -> 0x0154 }
        r3 = r0.queryFinalized(r3, r8);	 Catch:{ Throwable -> 0x0154 }
        r0 = r3.next();	 Catch:{ Throwable -> 0x014b, all -> 0x0148 }
        if (r0 == 0) goto L_0x013c;
    L_0x0103:
        r8 = r3.intValue(r13);	 Catch:{ Throwable -> 0x014b, all -> 0x0148 }
        r10 = java.lang.Math.min(r12, r8);	 Catch:{ Throwable -> 0x0138, all -> 0x0148 }
        if (r8 <= r12) goto L_0x0111;
    L_0x010d:
        r0 = r8 + -5000;
        r14 = r0;
        goto L_0x0112;
    L_0x0111:
        r14 = 0;
    L_0x0112:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        if (r0 == 0) goto L_0x012f;
    L_0x0116:
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r0.<init>();	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r15 = r1.currentAccount;	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r0.append(r15);	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r15 = " current cached contacts count = ";
        r0.append(r15);	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r0.append(r8);	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Throwable -> 0x0132, all -> 0x0148 }
    L_0x012f:
        r16 = r10;
        goto L_0x0140;
    L_0x0132:
        r0 = move-exception;
        r17 = r3;
        r16 = r10;
        goto L_0x0159;
    L_0x0138:
        r0 = move-exception;
        r17 = r3;
        goto L_0x0156;
    L_0x013c:
        r8 = 0;
        r14 = 0;
        r16 = 16;
    L_0x0140:
        if (r3 == 0) goto L_0x0145;
    L_0x0142:
        r3.dispose();
    L_0x0145:
        r17 = r3;
        goto L_0x0161;
    L_0x0148:
        r0 = move-exception;
        goto L_0x0246;
    L_0x014b:
        r0 = move-exception;
        r17 = r3;
        goto L_0x0155;
    L_0x014f:
        r0 = move-exception;
        r3 = r17;
        goto L_0x0246;
    L_0x0154:
        r0 = move-exception;
    L_0x0155:
        r8 = 0;
    L_0x0156:
        r14 = 0;
        r16 = 16;
    L_0x0159:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x014f }
        if (r17 == 0) goto L_0x0161;
    L_0x015e:
        r17.dispose();
    L_0x0161:
        r0 = r16;
        r3 = new java.util.HashMap;
        r3.<init>(r0);
        if (r14 == 0) goto L_0x0184;
    L_0x016a:
        r0 = r1.database;	 Catch:{ Exception -> 0x021e }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x021e }
        r10.<init>();	 Catch:{ Exception -> 0x021e }
        r14 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0,";
        r10.append(r14);	 Catch:{ Exception -> 0x021e }
        r10.append(r8);	 Catch:{ Exception -> 0x021e }
        r8 = r10.toString();	 Catch:{ Exception -> 0x021e }
        r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x021e }
        r0 = r0.queryFinalized(r8, r10);	 Catch:{ Exception -> 0x021e }
        goto L_0x018e;
    L_0x0184:
        r0 = r1.database;	 Catch:{ Exception -> 0x021e }
        r8 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1";
        r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x021e }
        r0 = r0.queryFinalized(r8, r10);	 Catch:{ Exception -> 0x021e }
    L_0x018e:
        r8 = r0;
    L_0x018f:
        r0 = r8.next();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r0 == 0) goto L_0x0210;
    L_0x0195:
        r0 = r8.stringValue(r13);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10 = r3.get(r0);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10 = (org.telegram.messenger.ContactsController.Contact) r10;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r10 != 0) goto L_0x01ce;
    L_0x01a1:
        r10 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10.<init>();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = r8.intValue(r9);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10.contact_id = r14;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = r8.stringValue(r5);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10.first_name = r14;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = r8.stringValue(r4);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10.last_name = r14;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = 7;
        r14 = r8.intValue(r14);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r10.imported = r14;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = r10.first_name;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r14 != 0) goto L_0x01c5;
    L_0x01c3:
        r10.first_name = r2;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
    L_0x01c5:
        r14 = r10.last_name;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r14 != 0) goto L_0x01cb;
    L_0x01c9:
        r10.last_name = r2;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
    L_0x01cb:
        r3.put(r0, r10);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
    L_0x01ce:
        r0 = r8.stringValue(r7);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r0 != 0) goto L_0x01d6;
    L_0x01d4:
        r14 = 6;
        goto L_0x018f;
    L_0x01d6:
        r14 = r10.phones;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14.add(r0);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = r8.stringValue(r6);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r14 != 0) goto L_0x01e2;
    L_0x01e1:
        goto L_0x01d4;
    L_0x01e2:
        r15 = r14.length();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r15 != r11) goto L_0x01f2;
    L_0x01e8:
        r15 = r0.length();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r15 == r11) goto L_0x01f2;
    L_0x01ee:
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
    L_0x01f2:
        r0 = r10.shortPhones;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0.add(r14);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0 = r10.phoneDeleted;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r14 = 6;
        r15 = r8.intValue(r14);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0.add(r15);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0 = r10.phoneTypes;	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0.add(r2);	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        r0 = r3.size();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        if (r0 != r12) goto L_0x018f;
    L_0x0210:
        r8.dispose();	 Catch:{ Exception -> 0x0216, all -> 0x0214 }
        goto L_0x022a;
    L_0x0214:
        r0 = move-exception;
        goto L_0x0240;
    L_0x0216:
        r0 = move-exception;
        r17 = r8;
        goto L_0x021f;
    L_0x021a:
        r0 = move-exception;
        r8 = r17;
        goto L_0x0240;
    L_0x021e:
        r0 = move-exception;
    L_0x021f:
        r3.clear();	 Catch:{ all -> 0x021a }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x021a }
        if (r17 == 0) goto L_0x022a;
    L_0x0227:
        r17.dispose();
    L_0x022a:
        r18 = r26.getContactsController();
        r20 = 1;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r24 = r27 ^ 1;
        r25 = 0;
        r19 = r3;
        r18.performSyncPhoneBook(r19, r20, r21, r22, r23, r24, r25);
        return;
    L_0x0240:
        if (r8 == 0) goto L_0x0245;
    L_0x0242:
        r8.dispose();
    L_0x0245:
        throw r0;
    L_0x0246:
        if (r3 == 0) goto L_0x024b;
    L_0x0248:
        r3.dispose();
    L_0x024b:
        throw r0;
    L_0x024c:
        r0 = move-exception;
    L_0x024d:
        if (r10 == 0) goto L_0x0252;
    L_0x024f:
        r10.dispose();
    L_0x0252:
        goto L_0x0254;
    L_0x0253:
        throw r0;
    L_0x0254:
        goto L_0x0253;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$87$MessagesStorage(boolean):void");
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$QBiuXeXpe1yiBm-Hc4D28Gmtx4k(this));
    }

    public /* synthetic */ void lambda$getContacts$88$MessagesStorage() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
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
                getUsersInternal(stringBuilder.toString(), arrayList2);
            }
        } catch (Exception e) {
            arrayList.clear();
            arrayList2.clear();
            FileLog.e(e);
        }
        getContactsController().processLoadedContacts(arrayList, arrayList2, 1);
    }

    public void getUnsentMessages(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mlAKLyZGNpqEJyAzLc0TujAy5OQ(this, i));
    }

    public /* synthetic */ void lambda$getUnsentMessages$89$MessagesStorage(int i) {
        try {
            int i2;
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT ");
            stringBuilder.append(i);
            boolean z = false;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z), z);
                    TLdeserialize.send_state = queryFinalized.intValue(2);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize.id) < 0) {
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
                        int i4 = (int) (TLdeserialize.dialog_id >> 32);
                        if (i3 != 0) {
                            if (i4 == 1) {
                                if (!arrayList7.contains(Integer.valueOf(i3))) {
                                    arrayList7.add(Integer.valueOf(i3));
                                }
                            } else if (i3 < 0) {
                                i2 = -i3;
                                if (!arrayList6.contains(Integer.valueOf(i2))) {
                                    arrayList6.add(Integer.valueOf(i2));
                                }
                            } else if (!arrayList5.contains(Integer.valueOf(i3))) {
                                arrayList5.add(Integer.valueOf(i3));
                            }
                        } else if (!arrayList8.contains(Integer.valueOf(i4))) {
                            arrayList8.add(Integer.valueOf(i4));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList5, arrayList6);
                        if (TLdeserialize.send_state != 3 && ((TLdeserialize.to_id.channel_id == 0 && !MessageObject.isUnread(TLdeserialize) && i3 != 0) || TLdeserialize.id > 0)) {
                            TLdeserialize.send_state = 0;
                        }
                        if (i3 == 0 && !queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        z = false;
                    }
                }
                z = false;
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList8.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(str, arrayList8), arrayList4, arrayList5);
            }
            if (!arrayList5.isEmpty()) {
                getUsersInternal(TextUtils.join(str, arrayList5), arrayList2);
            }
            if (!(arrayList6.isEmpty() && arrayList7.isEmpty())) {
                StringBuilder stringBuilder2 = new StringBuilder();
                for (int i5 = 0; i5 < arrayList6.size(); i5++) {
                    Integer num = (Integer) arrayList6.get(i5);
                    if (stringBuilder2.length() != 0) {
                        stringBuilder2.append(str);
                    }
                    stringBuilder2.append(num);
                }
                for (i2 = 0; i2 < arrayList7.size(); i2++) {
                    Integer num2 = (Integer) arrayList7.get(i2);
                    if (stringBuilder2.length() != 0) {
                        stringBuilder2.append(str);
                    }
                    stringBuilder2.append(-num2.intValue());
                }
                getChatsInternal(stringBuilder2.toString(), arrayList3);
            }
            getSendMessagesHelper().processUnsentMessages(arrayList, arrayList2, arrayList3, arrayList4);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkMessageByRandomId(long j) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ArrsTVIv3CTVEFBEj2jJUP-GaNI(this, j, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$checkMessageByRandomId$90$MessagesStorage(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
        r6 = this;
        r0 = 0;
        r1 = r6.database;	 Catch:{ Exception -> 0x0028 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0028 }
        r3 = "SELECT random_id FROM randoms WHERE random_id = %d";	 Catch:{ Exception -> 0x0028 }
        r4 = 1;	 Catch:{ Exception -> 0x0028 }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0028 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0028 }
        r8 = 0;	 Catch:{ Exception -> 0x0028 }
        r5[r8] = r7;	 Catch:{ Exception -> 0x0028 }
        r7 = java.lang.String.format(r2, r3, r5);	 Catch:{ Exception -> 0x0028 }
        r2 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0028 }
        r0 = r1.queryFinalized(r7, r2);	 Catch:{ Exception -> 0x0028 }
        r7 = r0.next();	 Catch:{ Exception -> 0x0028 }
        if (r7 == 0) goto L_0x0023;	 Catch:{ Exception -> 0x0028 }
    L_0x0021:
        r9[r8] = r4;	 Catch:{ Exception -> 0x0028 }
    L_0x0023:
        if (r0 == 0) goto L_0x0031;
    L_0x0025:
        goto L_0x002e;
    L_0x0026:
        r7 = move-exception;
        goto L_0x0035;
    L_0x0028:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x0026 }
        if (r0 == 0) goto L_0x0031;
    L_0x002e:
        r0.dispose();
    L_0x0031:
        r10.countDown();
        return;
    L_0x0035:
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r0.dispose();
    L_0x003a:
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$90$MessagesStorage(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(long j, int i) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Q2UrE6O36L99_JtVOjx_eT8r4k0(this, j, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$checkMessageId$91$MessagesStorage(long r6, int r8, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
        /*
        r5 = this;
        r0 = 0;
        r1 = r5.database;	 Catch:{ Exception -> 0x002f }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x002f }
        r3 = "SELECT mid FROM messages WHERE uid = %d AND mid = %d";	 Catch:{ Exception -> 0x002f }
        r4 = 2;	 Catch:{ Exception -> 0x002f }
        r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x002f }
        r7 = 0;	 Catch:{ Exception -> 0x002f }
        r4[r7] = r6;	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x002f }
        r8 = 1;	 Catch:{ Exception -> 0x002f }
        r4[r8] = r6;	 Catch:{ Exception -> 0x002f }
        r6 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x002f }
        r2 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x002f }
        r0 = r1.queryFinalized(r6, r2);	 Catch:{ Exception -> 0x002f }
        r6 = r0.next();	 Catch:{ Exception -> 0x002f }
        if (r6 == 0) goto L_0x002a;	 Catch:{ Exception -> 0x002f }
    L_0x0028:
        r9[r7] = r8;	 Catch:{ Exception -> 0x002f }
    L_0x002a:
        if (r0 == 0) goto L_0x0038;
    L_0x002c:
        goto L_0x0035;
    L_0x002d:
        r6 = move-exception;
        goto L_0x003c;
    L_0x002f:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x002d }
        if (r0 == 0) goto L_0x0038;
    L_0x0035:
        r0.dispose();
    L_0x0038:
        r10.countDown();
        return;
    L_0x003c:
        if (r0 == 0) goto L_0x0041;
    L_0x003e:
        r0.dispose();
    L_0x0041:
        throw r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$91$MessagesStorage(long, int, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$wf9l8PLpCsSFk1bfVCDmcB5ggTs(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getUnreadMention$93$MessagesStorage(long j, IntCallback intCallback) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[1];
            int i = 0;
            objArr[0] = Long.valueOf(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", objArr), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$uKoT1QRUcZ9KZW59r0X9RxYbAXY(intCallback, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessagesCount(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$BszdeX1wX_jSM-SB-hXRjrxJyEA(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getMessagesCount$95$MessagesStorage(long j, IntCallback intCallback) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[1];
            int i = 0;
            objArr[0] = Long.valueOf(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d", objArr), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$nXMoCrqLrakLlqQgsAWkSviQJ_c(intCallback, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessages(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$i87mOcxqGGIL0PIMCYcmnHioK4o(this, i, i2, z, j, i6, i4, i3, i5, i7));
    }

    /* JADX WARNING: Removed duplicated region for block: B:390:0x0917  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x095b A:{Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }} */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x091a A:{SYNTHETIC, Splitter:B:392:0x091a} */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x095f A:{Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }} */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0cc4 A:{Catch:{ Exception -> 0x0d10, all -> 0x0d04 }} */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0e88  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0e83 A:{SYNTHETIC, Splitter:B:582:0x0e83} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x10d0  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x10c2 A:{Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x111b A:{Catch:{ Exception -> 0x1214, all -> 0x1209 }} */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x10d7 A:{SYNTHETIC, Splitter:B:714:0x10d7} */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x1306 A:{Catch:{ Exception -> 0x1214, all -> 0x1209 }} */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x13ba A:{Catch:{ Exception -> 0x1214, all -> 0x1209 }} */
    /* JADX WARNING: Removed duplicated region for block: B:760:0x1209 A:{Splitter:B:631:0x0var_, PHI: r11 r27 r29 r30 r31 r33 r39 r44 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x1129 A:{Catch:{ Exception -> 0x1214, all -> 0x1209 }, Splitter:B:711:0x10d1, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x1129 A:{Catch:{ Exception -> 0x1214, all -> 0x1209 }, Splitter:B:711:0x10d1, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:273:0x04f3, code skipped:
            if (r15 == 2) goto L_0x0790;
     */
    /* JADX WARNING: Missing block: B:540:0x0d04, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:541:0x0d05, code skipped:
            r21 = r0;
            r6 = r2;
            r33 = r5;
            r14 = r7;
            r13 = r10;
            r11 = r27;
     */
    /* JADX WARNING: Missing block: B:542:0x0d10, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:543:0x0d11, code skipped:
            r6 = r2;
            r33 = r5;
            r14 = r7;
            r13 = r10;
            r11 = r27;
     */
    /* JADX WARNING: Missing block: B:662:0x0fd7, code skipped:
            if (r13.reply_to_random_id != 0) goto L_0x0ffa;
     */
    /* JADX WARNING: Missing block: B:725:0x1109, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:728:0x110f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:729:0x1110, code skipped:
            r27 = r2;
            r23 = r4;
            r26 = r9;
     */
    /* JADX WARNING: Missing block: B:735:0x1129, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:760:0x1209, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:762:0x1214, code skipped:
            r0 = e;
     */
    public /* synthetic */ void lambda$getMessages$97$MessagesStorage(int r38, int r39, boolean r40, long r41, int r43, int r44, int r45, int r46, int r47) {
        /*
        r37 = this;
        r1 = r37;
        r2 = r38;
        r3 = r39;
        r4 = r41;
        r15 = r43;
        r6 = r37.getUserConfig();
        r6 = r6.clientUserId;
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r7.<init>();
        r8 = (long) r3;
        if (r40 == 0) goto L_0x001b;
    L_0x0018:
        r11 = (int) r4;
        r11 = -r11;
        goto L_0x001c;
    L_0x001b:
        r11 = 0;
    L_0x001c:
        r12 = 32;
        r13 = 0;
        r16 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1));
        if (r16 == 0) goto L_0x0029;
    L_0x0024:
        if (r11 == 0) goto L_0x0029;
    L_0x0026:
        r13 = (long) r11;
        r13 = r13 << r12;
        r8 = r8 | r13;
    L_0x0029:
        r13 = r8;
        r8 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r19 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r19 != 0) goto L_0x0034;
    L_0x0031:
        r8 = 10;
        goto L_0x0035;
    L_0x0034:
        r8 = 1;
    L_0x0035:
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r9.<init>();	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r12.<init>();	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r10.<init>();	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r21 = r10;
        r10 = new android.util.SparseArray;	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r10.<init>();	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r22 = r10;
        r10 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r10.<init>();	 Catch:{ Exception -> 0x150e, all -> 0x14fa }
        r23 = r10;
        r10 = (int) r4;
        r24 = r9;
        if (r10 == 0) goto L_0x0bb5;
    L_0x0059:
        r9 = "SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = ";
        r26 = r12;
        r12 = 3;
        if (r15 != r12) goto L_0x014e;
    L_0x0060:
        if (r44 != 0) goto L_0x014e;
    L_0x0062:
        r8 = r1.database;	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r12.<init>();	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r12.append(r9);	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r12.append(r4);	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r9 = r12.toString();	 Catch:{ Exception -> 0x013c, all -> 0x0129 }
        r27 = r7;
        r12 = 0;
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r7 = r8.queryFinalized(r9, r7);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r8 = r7.next();	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        if (r8 == 0) goto L_0x00e6;
    L_0x0082:
        r8 = r7.intValue(r12);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r9 = 1;
        r8 = r8 + r9;
        r12 = r7.intValue(r9);	 Catch:{ Exception -> 0x00d8, all -> 0x00c8 }
        r9 = 2;
        r28 = r7.intValue(r9);	 Catch:{ Exception -> 0x00be, all -> 0x00b2 }
        r9 = 3;
        r29 = r7.intValue(r9);	 Catch:{ Exception -> 0x00a6, all -> 0x0098 }
        goto L_0x00ec;
    L_0x0098:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
    L_0x00a4:
        r12 = 0;
        goto L_0x00d4;
    L_0x00a6:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
    L_0x00b0:
        r12 = 0;
        goto L_0x00e2;
    L_0x00b2:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
    L_0x00bc:
        r12 = 0;
        goto L_0x00d3;
    L_0x00be:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
    L_0x00c6:
        r12 = 0;
        goto L_0x00e1;
    L_0x00c8:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r11 = r27;
    L_0x00d1:
        r12 = 0;
        r13 = 0;
    L_0x00d3:
        r14 = 0;
    L_0x00d4:
        r17 = 0;
        goto L_0x0CLASSNAME;
    L_0x00d8:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r11 = r27;
    L_0x00df:
        r12 = 0;
        r13 = 0;
    L_0x00e1:
        r14 = 0;
    L_0x00e2:
        r17 = 0;
        goto L_0x0c9c;
    L_0x00e6:
        r8 = 0;
        r12 = 0;
        r28 = 0;
        r29 = 0;
    L_0x00ec:
        r7.dispose();	 Catch:{ Exception -> 0x0116, all -> 0x0101 }
        r31 = r3;
        r36 = r6;
        r33 = r8;
        r30 = r29;
        r6 = 0;
        r32 = 0;
        r29 = r28;
        r28 = r10;
        r10 = r12;
        goto L_0x0423;
    L_0x0101:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r20 = r29;
        r12 = 0;
        r17 = 0;
        r19 = 0;
        goto L_0x154d;
    L_0x0116:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r8;
        r13 = r12;
        r11 = r27;
        r14 = r28;
        r20 = r29;
        r12 = 0;
        r17 = 0;
        r19 = 0;
        goto L_0x151f;
    L_0x0129:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r11 = r7;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        r7 = r3;
        goto L_0x154d;
    L_0x013c:
        r0 = move-exception;
        r6 = r2;
        r11 = r7;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
        r19 = 0;
        r20 = 0;
        r33 = 0;
        r2 = r0;
        r7 = r3;
        goto L_0x1520;
    L_0x014e:
        r27 = r7;
        r7 = 1;
        if (r15 == r7) goto L_0x0413;
    L_0x0153:
        r7 = 3;
        if (r15 == r7) goto L_0x0413;
    L_0x0156:
        r7 = 4;
        if (r15 == r7) goto L_0x0413;
    L_0x0159:
        if (r44 != 0) goto L_0x0413;
    L_0x015b:
        r7 = 2;
        if (r15 != r7) goto L_0x03dd;
    L_0x015e:
        r7 = r1.database;	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r12.<init>();	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r12.append(r9);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r12.append(r4);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r9 = r12.toString();	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r28 = r10;
        r12 = 0;
        r10 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r7 = r7.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r9 = r7.next();	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        if (r9 == 0) goto L_0x01fb;
    L_0x017e:
        r9 = r7.intValue(r12);	 Catch:{ Exception -> 0x03d6, all -> 0x03cd }
        r13 = (long) r9;
        r10 = 1;
        r12 = r7.intValue(r10);	 Catch:{ Exception -> 0x01f0, all -> 0x01e3 }
        r10 = 2;
        r29 = r7.intValue(r10);	 Catch:{ Exception -> 0x01d7, all -> 0x01c9 }
        r10 = 3;
        r30 = r7.intValue(r10);	 Catch:{ Exception -> 0x01bb, all -> 0x01ab }
        r16 = 0;
        r10 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r10 == 0) goto L_0x01a3;
    L_0x0198:
        if (r11 == 0) goto L_0x01a3;
    L_0x019a:
        r31 = r9;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r13 = r13 | r9;
        goto L_0x01a5;
    L_0x01a3:
        r31 = r9;
    L_0x01a5:
        r10 = r12;
        r12 = r31;
        r9 = 1;
        goto L_0x0204;
    L_0x01ab:
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r33 = r31;
        goto L_0x00a4;
    L_0x01bb:
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r33 = r31;
        goto L_0x00b0;
    L_0x01c9:
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r33 = r31;
        goto L_0x00bc;
    L_0x01d7:
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r13 = r12;
        r11 = r27;
        r33 = r31;
        goto L_0x00c6;
    L_0x01e3:
        r0 = move-exception;
        r31 = r9;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        r33 = r31;
        goto L_0x00d1;
    L_0x01f0:
        r0 = move-exception;
        r31 = r9;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        r33 = r31;
        goto L_0x00df;
    L_0x01fb:
        r31 = r3;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r29 = 0;
        r30 = 0;
    L_0x0204:
        r7.dispose();	 Catch:{ Exception -> 0x03c3, all -> 0x03b7 }
        if (r9 != 0) goto L_0x02a9;
    L_0x0209:
        r7 = r1.database;	 Catch:{ Exception -> 0x02a2, all -> 0x029b }
        r32 = r9;
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0299, all -> 0x0297 }
        r33 = r12;
        r12 = "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";
        r34 = r13;
        r13 = 1;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r13 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r36 = r6;
        r6 = 0;
        r14[r6] = r13;	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r9 = java.lang.String.format(r9, r12, r14);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r12 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r7 = r7.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r9 = r7.next();	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        if (r9 == 0) goto L_0x024d;
    L_0x0231:
        r9 = r7.intValue(r6);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r6 = 1;
        r12 = r7.intValue(r6);	 Catch:{ Exception -> 0x0246, all -> 0x023d }
        r33 = r9;
        goto L_0x024f;
    L_0x023d:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r33 = r9;
        goto L_0x040a;
    L_0x0246:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r33 = r9;
        goto L_0x0410;
    L_0x024d:
        r12 = r29;
    L_0x024f:
        r7.dispose();	 Catch:{ Exception -> 0x0292, all -> 0x028d }
        if (r33 == 0) goto L_0x0287;
    L_0x0254:
        r6 = r1.database;	 Catch:{ Exception -> 0x0292, all -> 0x028d }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0292, all -> 0x028d }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)";
        r13 = 2;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0292, all -> 0x028d }
        r13 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0292, all -> 0x028d }
        r29 = r12;
        r12 = 0;
        r14[r12] = r13;	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r13 = java.lang.Integer.valueOf(r33);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r19 = 1;
        r14[r19] = r13;	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r7 = java.lang.String.format(r7, r9, r14);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r9 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r7 = r6.next();	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        if (r7 == 0) goto L_0x0283;
    L_0x027e:
        r7 = r6.intValue(r12);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        r10 = r7;
    L_0x0283:
        r6.dispose();	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        goto L_0x0289;
    L_0x0287:
        r29 = r12;
    L_0x0289:
        r13 = r34;
        goto L_0x03ec;
    L_0x028d:
        r0 = move-exception;
        r29 = r12;
        goto L_0x0406;
    L_0x0292:
        r0 = move-exception;
        r29 = r12;
        goto L_0x040e;
    L_0x0297:
        r0 = move-exception;
        goto L_0x029e;
    L_0x0299:
        r0 = move-exception;
        goto L_0x02a5;
    L_0x029b:
        r0 = move-exception;
        r32 = r9;
    L_0x029e:
        r33 = r12;
        goto L_0x0406;
    L_0x02a2:
        r0 = move-exception;
        r32 = r9;
    L_0x02a5:
        r33 = r12;
        goto L_0x040e;
    L_0x02a9:
        r36 = r6;
        r32 = r9;
        r33 = r12;
        r34 = r13;
        if (r31 != 0) goto L_0x0334;
    L_0x02b3:
        r6 = r1.database;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)";
        r12 = 1;
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r14 = 0;
        r13[r14] = r12;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = r6.next();	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        if (r7 == 0) goto L_0x02d8;
    L_0x02d3:
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        goto L_0x02d9;
    L_0x02d8:
        r7 = 0;
    L_0x02d9:
        r6.dispose();	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        if (r7 != r10) goto L_0x0322;
    L_0x02de:
        r6 = r1.database;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";
        r12 = 1;
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r14 = 0;
        r13[r14] = r12;	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r7 = r6.next();	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        if (r7 == 0) goto L_0x0317;
    L_0x02fe:
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x0331, all -> 0x032e }
        r13 = (long) r7;
        r16 = 0;
        r9 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r9 == 0) goto L_0x0313;
    L_0x0309:
        if (r11 == 0) goto L_0x0313;
    L_0x030b:
        r12 = r10;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r13 = r13 | r9;
        goto L_0x0314;
    L_0x0313:
        r12 = r10;
    L_0x0314:
        r33 = r7;
        goto L_0x031c;
    L_0x0317:
        r12 = r10;
        r7 = r31;
        r13 = r34;
    L_0x031c:
        r6.dispose();	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r34 = r13;
        goto L_0x0325;
    L_0x0322:
        r12 = r10;
        r7 = r31;
    L_0x0325:
        r10 = r33;
        r31 = r7;
        r33 = r10;
    L_0x032b:
        r10 = r12;
        goto L_0x0289;
    L_0x032e:
        r0 = move-exception;
        goto L_0x03bc;
    L_0x0331:
        r0 = move-exception;
        goto L_0x03c8;
    L_0x0334:
        r12 = r10;
        r6 = r1.database;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r9 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";
        r10 = 3;
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r14 = 0;
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r14 = 1;
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r14 = 2;
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r9 = 0;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r6 = r6.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = r6.next();	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        if (r7 != 0) goto L_0x0366;
    L_0x0364:
        r7 = 1;
        goto L_0x0367;
    L_0x0366:
        r7 = 0;
    L_0x0367:
        r6.dispose();	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        if (r7 == 0) goto L_0x032b;
    L_0x036c:
        r6 = r1.database;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d";
        r10 = 2;
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r14 = 0;
        r13[r14] = r10;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r10 = java.lang.Integer.valueOf(r31);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r19 = 1;
        r13[r19] = r10;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = java.lang.String.format(r7, r9, r13);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r6 = r6.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r7 = r6.next();	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        if (r7 == 0) goto L_0x03a8;
    L_0x0394:
        r7 = r6.intValue(r14);	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r13 = (long) r7;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r9 = 0;
        r31 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r31 == 0) goto L_0x03ac;
    L_0x039f:
        if (r11 == 0) goto L_0x03ac;
    L_0x03a1:
        r9 = (long) r11;	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r18 = 32;
        r9 = r9 << r18;
        r13 = r13 | r9;
        goto L_0x03ac;
    L_0x03a8:
        r7 = r31;
        r13 = r34;
    L_0x03ac:
        r6.dispose();	 Catch:{ Exception -> 0x03b5, all -> 0x03b3 }
        r31 = r7;
        r10 = r12;
        goto L_0x03ec;
    L_0x03b3:
        r0 = move-exception;
        goto L_0x03bd;
    L_0x03b5:
        r0 = move-exception;
        goto L_0x03c9;
    L_0x03b7:
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
    L_0x03bc:
        r12 = r10;
    L_0x03bd:
        r21 = r0;
        r6 = r2;
        r7 = r3;
        goto L_0x0b94;
    L_0x03c3:
        r0 = move-exception;
        r32 = r9;
        r33 = r12;
    L_0x03c8:
        r12 = r10;
    L_0x03c9:
        r6 = r2;
        r7 = r3;
        goto L_0x0ba7;
    L_0x03cd:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        goto L_0x1502;
    L_0x03d6:
        r0 = move-exception;
        r6 = r2;
        r7 = r3;
        r11 = r27;
        goto L_0x1514;
    L_0x03dd:
        r36 = r6;
        r28 = r10;
        r31 = r3;
        r10 = 0;
        r29 = 0;
        r30 = 0;
        r32 = 0;
        r33 = 0;
    L_0x03ec:
        if (r2 > r10) goto L_0x03f6;
    L_0x03ee:
        if (r10 >= r8) goto L_0x03f1;
    L_0x03f0:
        goto L_0x03f6;
    L_0x03f1:
        r6 = r10 - r2;
        r2 = r2 + 10;
        goto L_0x0423;
    L_0x03f6:
        r6 = r10 + 10;
        r2 = java.lang.Math.max(r2, r6);	 Catch:{ Exception -> 0x040d, all -> 0x0405 }
        if (r10 >= r8) goto L_0x0403;
    L_0x03fe:
        r6 = 0;
        r10 = 0;
        r13 = 0;
        goto L_0x041f;
    L_0x0403:
        r6 = 0;
        goto L_0x0423;
    L_0x0405:
        r0 = move-exception;
    L_0x0406:
        r21 = r0;
        r6 = r2;
        r7 = r3;
    L_0x040a:
        r13 = r10;
        goto L_0x0b95;
    L_0x040d:
        r0 = move-exception;
    L_0x040e:
        r6 = r2;
        r7 = r3;
    L_0x0410:
        r13 = r10;
        goto L_0x0ba8;
    L_0x0413:
        r36 = r6;
        r28 = r10;
        r31 = r3;
        r6 = 0;
        r10 = 0;
        r29 = 0;
        r30 = 0;
    L_0x041f:
        r32 = 0;
        r33 = 0;
    L_0x0423:
        r7 = r1.database;	 Catch:{ Exception -> 0x0ba2, all -> 0x0b8d }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0ba2, all -> 0x0b8d }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)";
        r12 = 1;
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0ba2, all -> 0x0b8d }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0ba2, all -> 0x0b8d }
        r34 = r10;
        r10 = 0;
        r3[r10] = r12;	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r3 = java.lang.String.format(r8, r9, r3);	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r3 = r7.queryFinalized(r3, r8);	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r7 = r3.next();	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        if (r7 == 0) goto L_0x049f;
    L_0x0445:
        r7 = r3.intValue(r10);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r8 = 1;
        if (r7 != r8) goto L_0x044e;
    L_0x044c:
        r10 = 1;
        goto L_0x044f;
    L_0x044e:
        r10 = 0;
    L_0x044f:
        r3.dispose();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = 3;
        goto L_0x04eb;
    L_0x0455:
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
    L_0x0467:
        r12 = 0;
        goto L_0x154d;
    L_0x046a:
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
    L_0x047a:
        r12 = 0;
        goto L_0x151f;
    L_0x047d:
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0b9d;
    L_0x048f:
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0bb0;
    L_0x049f:
        r3.dispose();	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r8 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r12 = 0;
        r10[r12] = r9;	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r7 = r3.next();	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        if (r7 == 0) goto L_0x04e6;
    L_0x04c2:
        r7 = r3.intValue(r12);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        if (r7 == 0) goto L_0x04e6;
    L_0x04c8:
        r8 = r1.database;	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r9 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r8 = r8.executeFast(r9);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r8.requery();	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r9 = 1;
        r8.bindLong(r9, r4);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r9 = 2;
        r10 = 0;
        r8.bindInteger(r9, r10);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r9 = 3;
        r8.bindInteger(r9, r7);	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r8.step();	 Catch:{ Exception -> 0x048f, all -> 0x047d }
        r8.dispose();	 Catch:{ Exception -> 0x048f, all -> 0x047d }
    L_0x04e6:
        r3.dispose();	 Catch:{ Exception -> 0x0b89, all -> 0x0b85 }
        r3 = 3;
        r10 = 0;
    L_0x04eb:
        if (r15 == r3) goto L_0x0790;
    L_0x04ed:
        r3 = 4;
        if (r15 == r3) goto L_0x0790;
    L_0x04f0:
        if (r32 == 0) goto L_0x04f7;
    L_0x04f2:
        r3 = 2;
        if (r15 != r3) goto L_0x04f8;
    L_0x04f5:
        goto L_0x0790;
    L_0x04f7:
        r3 = 2;
    L_0x04f8:
        r7 = 1;
        if (r15 != r7) goto L_0x05a7;
    L_0x04fb:
        r6 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1";
        r9 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r12 = 0;
        r9[r12] = r3;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r19 = 1;
        r9[r19] = r3;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = java.lang.String.format(r7, r8, r9);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r6.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = r3.next();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        if (r6 == 0) goto L_0x052f;
    L_0x0522:
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        if (r11 == 0) goto L_0x0531;
    L_0x0529:
        r8 = (long) r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 32;
        r8 = r8 << r11;
        r6 = r6 | r8;
        goto L_0x0531;
    L_0x052f:
        r6 = 0;
    L_0x0531:
        r3.dispose();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = 0;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x0575;
    L_0x053a:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";
        r11 = 5;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r20 = 0;
        r12[r20] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r19 = 1;
        r12[r19] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r13 = 2;
        r12[r13] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 3;
        r12[r7] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 4;
        r12[r7] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x068a;
    L_0x0575:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";
        r8 = 4;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 0;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 1;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 2;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 3;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x068a;
    L_0x05a7:
        if (r44 == 0) goto L_0x069d;
    L_0x05a9:
        r7 = 0;
        r3 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1));
        if (r3 == 0) goto L_0x065a;
    L_0x05af:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
        r8 = 2;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r12 = 0;
        r9[r12] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r19 = 1;
        r9[r19] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = r3.next();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        if (r6 == 0) goto L_0x05e4;
    L_0x05d7:
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        if (r11 == 0) goto L_0x05e6;
    L_0x05de:
        r8 = (long) r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 32;
        r8 = r8 << r11;
        r6 = r6 | r8;
        goto L_0x05e6;
    L_0x05e4:
        r6 = 0;
    L_0x05e6:
        r3.dispose();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = 0;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x0629;
    L_0x05ef:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d";
        r11 = 5;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r20 = 0;
        r12[r20] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r19 = 1;
        r12[r19] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r13 = 2;
        r12[r13] = r11;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 3;
        r12[r7] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 4;
        r12[r7] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x068a;
    L_0x0629:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d";
        r8 = 4;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 0;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 1;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 2;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r11 = 3;
        r9[r11] = r8;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x068a;
    L_0x065a:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r9 = 4;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r12 = 0;
        r11[r12] = r9;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r12 = 1;
        r11[r12] = r9;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = 2;
        r11[r9] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = 3;
        r11[r9] = r6;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r6 = java.lang.String.format(r7, r8, r11);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
    L_0x068a:
        r7 = r39;
        r6 = r2;
        r35 = r10;
        r14 = r29;
        r2 = r30;
        r8 = r31;
        r11 = r33;
        r13 = r34;
        r10 = 0;
        r12 = 0;
        goto L_0x0ed0;
    L_0x069d:
        r3 = r1.database;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";
        r9 = 1;
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r13 = 0;
        r12[r13] = r9;	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = java.lang.String.format(r7, r8, r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r8 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        r7 = r3.next();	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        if (r7 == 0) goto L_0x06c2;
    L_0x06bd:
        r7 = r3.intValue(r13);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x06c3;
    L_0x06c2:
        r7 = 0;
    L_0x06c3:
        r3.dispose();	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r3 = r1.database;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r9 = "SELECT max(end) FROM messages_holes WHERE uid = %d";
        r12 = 1;
        r13 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r14 = 0;
        r13[r14] = r12;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = java.lang.String.format(r8, r9, r13);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r3 = r3.queryFinalized(r8, r9);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = r3.next();	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        if (r8 == 0) goto L_0x06f3;
    L_0x06e6:
        r8 = r3.intValue(r14);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r13 = (long) r8;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        if (r11 == 0) goto L_0x06f5;
    L_0x06ed:
        r8 = (long) r11;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = 32;
        r8 = r8 << r11;
        r13 = r13 | r8;
        goto L_0x06f5;
    L_0x06f3:
        r13 = 0;
    L_0x06f5:
        r3.dispose();	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = 0;
        r3 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
        if (r3 == 0) goto L_0x0730;
    L_0x06fe:
        r3 = r1.database;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r11 = 4;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r20 = 0;
        r12[r20] = r11;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r13 = 1;
        r12[r13] = r11;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = 2;
        r12[r11] = r6;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = 3;
        r12[r11] = r6;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        goto L_0x0759;
    L_0x0730:
        r3 = r1.database;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r11 = 3;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r13 = 0;
        r12[r13] = r11;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = 1;
        r12[r11] = r6;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r11 = 2;
        r12[r11] = r6;	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r6 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x077f, all -> 0x076c }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x077f, all -> 0x076c }
    L_0x0759:
        r6 = r2;
        r12 = r7;
        r35 = r10;
        r14 = r29;
        r2 = r30;
        r8 = r31;
        r11 = r33;
        r13 = r34;
        r10 = 0;
        r7 = r39;
        goto L_0x0ed0;
    L_0x076c:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r12 = r7;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x14e2;
    L_0x077f:
        r0 = move-exception;
        r6 = r2;
        r12 = r7;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x14f7;
    L_0x0790:
        r3 = r1.database;	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r7 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r12 = 0;
        r9[r12] = r8;	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r6 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        r6 = r3.next();	 Catch:{ Exception -> 0x0b70, all -> 0x0b59 }
        if (r6 == 0) goto L_0x07b5;
    L_0x07b0:
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x046a, all -> 0x0455 }
        goto L_0x07b6;
    L_0x07b5:
        r6 = 0;
    L_0x07b6:
        r3.dispose();	 Catch:{ Exception -> 0x0b40, all -> 0x0b25 }
        r3 = 4;
        if (r15 != r3) goto L_0x090b;
    L_0x07bc:
        if (r45 == 0) goto L_0x090b;
    L_0x07be:
        r3 = r1.database;	 Catch:{ Exception -> 0x08f5, all -> 0x08dd }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x08f5, all -> 0x08dd }
        r8 = "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0";
        r9 = 2;
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x08f5, all -> 0x08dd }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x08f5, all -> 0x08dd }
        r38 = r6;
        r6 = 0;
        r12[r6] = r9;	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r9 = java.lang.Integer.valueOf(r45);	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r19 = 1;
        r12[r19] = r9;	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r7 = java.lang.String.format(r7, r8, r12);	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r3 = r3.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r7 = r3.next();	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        if (r7 == 0) goto L_0x0817;
    L_0x07e8:
        r7 = r3.intValue(r6);	 Catch:{ Exception -> 0x0803, all -> 0x07ed }
        goto L_0x0818;
    L_0x07ed:
        r0 = move-exception;
        r12 = r38;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x154d;
    L_0x0803:
        r0 = move-exception;
        r12 = r38;
        r7 = r39;
        r6 = r2;
        r17 = r10;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x151f;
    L_0x0817:
        r7 = -1;
    L_0x0818:
        r3.dispose();	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r3 = r1.database;	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0";
        r12 = 2;
        r8 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x08db, all -> 0x08d9 }
        r35 = r10;
        r10 = 0;
        r8[r10] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Integer.valueOf(r45);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r8[r19] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.String.format(r6, r9, r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r6 == 0) goto L_0x084a;
    L_0x0845:
        r8 = r3.intValue(r10);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        goto L_0x084b;
    L_0x084a:
        r8 = -1;
    L_0x084b:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = -1;
        if (r7 == r3) goto L_0x090f;
    L_0x0851:
        if (r8 == r3) goto L_0x090f;
    L_0x0853:
        if (r7 != r8) goto L_0x085a;
    L_0x0855:
        r3 = r39;
        r8 = r7;
        goto L_0x0913;
    L_0x085a:
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";
        r10 = 3;
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r20 = 0;
        r12[r20] = r10;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r12[r19] = r10;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r25 = 2;
        r12[r25] = r10;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 0;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r6, r10);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r6 == 0) goto L_0x088d;
    L_0x088c:
        r7 = -1;
    L_0x088d:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = -1;
        if (r7 == r3) goto L_0x090f;
    L_0x0893:
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";
        r9 = 3;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 0;
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 1;
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 2;
        r10[r12] = r9;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.String.format(r6, r7, r10);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = 0;
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r6 == 0) goto L_0x08c3;
    L_0x08c2:
        r8 = -1;
    L_0x08c3:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = -1;
        if (r8 == r3) goto L_0x090f;
    L_0x08c9:
        r13 = (long) r8;
        r6 = 0;
        r3 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1));
        if (r3 == 0) goto L_0x08d7;
    L_0x08d0:
        if (r11 == 0) goto L_0x08d7;
    L_0x08d2:
        r6 = (long) r11;
        r3 = 32;
        r6 = r6 << r3;
        r13 = r13 | r6;
    L_0x08d7:
        r3 = r8;
        goto L_0x0913;
    L_0x08d9:
        r0 = move-exception;
        goto L_0x08e0;
    L_0x08db:
        r0 = move-exception;
        goto L_0x08f8;
    L_0x08dd:
        r0 = move-exception;
        r38 = r6;
    L_0x08e0:
        r35 = r10;
    L_0x08e2:
        r12 = r38;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0f1c;
    L_0x08f5:
        r0 = move-exception;
        r38 = r6;
    L_0x08f8:
        r35 = r10;
    L_0x08fa:
        r12 = r38;
        r7 = r39;
        r6 = r2;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r13 = r34;
        goto L_0x0f2f;
    L_0x090b:
        r38 = r6;
        r35 = r10;
    L_0x090f:
        r3 = r39;
        r8 = r31;
    L_0x0913:
        if (r8 == 0) goto L_0x0917;
    L_0x0915:
        r10 = 1;
        goto L_0x0918;
    L_0x0917:
        r10 = 0;
    L_0x0918:
        if (r10 == 0) goto L_0x095b;
    L_0x091a:
        r6 = r1.database;	 Catch:{ Exception -> 0x0957, all -> 0x0953 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0957, all -> 0x0953 }
        r9 = "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";
        r39 = r3;
        r12 = 3;
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r20 = 0;
        r3[r20] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r3[r19] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r25 = 2;
        r3[r25] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = java.lang.String.format(r7, r9, r3);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = 0;
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r6.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r6 == 0) goto L_0x094f;
    L_0x094e:
        r10 = 0;
    L_0x094f:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        goto L_0x095d;
    L_0x0953:
        r0 = move-exception;
        r39 = r3;
        goto L_0x08e2;
    L_0x0957:
        r0 = move-exception;
        r39 = r3;
        goto L_0x08fa;
    L_0x095b:
        r39 = r3;
    L_0x095d:
        if (r10 == 0) goto L_0x0a90;
    L_0x095f:
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";
        r12 = 2;
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 0;
        r6[r12] = r7;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r6[r19] = r7;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.String.format(r9, r10, r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r7 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r6 == 0) goto L_0x0994;
    L_0x0987:
        r6 = r3.intValue(r12);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = (long) r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r11 == 0) goto L_0x0996;
    L_0x098e:
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 32;
        r9 = r9 << r12;
        r6 = r6 | r9;
        goto L_0x0996;
    L_0x0994:
        r6 = 0;
    L_0x0996:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
        r12 = 2;
        r4 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 0;
        r4[r12] = r5;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r4[r19] = r5;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.String.format(r9, r10, r4);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = r3.next();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r4 == 0) goto L_0x09ce;
    L_0x09c1:
        r4 = r3.intValue(r12);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = (long) r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        if (r11 == 0) goto L_0x09d0;
    L_0x09c8:
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = 32;
        r9 = r9 << r12;
        r4 = r4 | r9;
        goto L_0x09d0;
    L_0x09ce:
        r4 = 1;
    L_0x09d0:
        r3.dispose();	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 0;
        r3 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r3 != 0) goto L_0x0a23;
    L_0x09d9:
        r9 = 1;
        r3 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
        if (r3 == 0) goto L_0x09e0;
    L_0x09df:
        goto L_0x0a23;
    L_0x09e0:
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r6 = 6;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 0;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 1;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r2 / 2;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 2;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 3;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 4;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r6 = r2 / 2;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = 5;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        goto L_0x0a86;
    L_0x0a23:
        r9 = 0;
        r3 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));
        if (r3 != 0) goto L_0x0a33;
    L_0x0a29:
        r6 = NUM; // 0x3b9aca00 float:0.NUM double:4.94065646E-315;
        if (r11 == 0) goto L_0x0a33;
    L_0x0a2e:
        r9 = (long) r11;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = 32;
        r9 = r9 << r3;
        r6 = r6 | r9;
    L_0x0a33:
        r3 = r1.database;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r10 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND (m.mid <= %d OR m.mid < 0) ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r11 = 8;
        r11 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r20 = 0;
        r11[r20] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r12 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r19 = 1;
        r11[r19] = r12;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 2;
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = r2 / 2;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 3;
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 4;
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 5;
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 6;
        r11[r5] = r4;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = 7;
        r5 = r2 / 2;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r11[r4] = r5;	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r4 = java.lang.String.format(r9, r10, r11);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
    L_0x0a86:
        r12 = r34;
        goto L_0x0b16;
    L_0x0a8a:
        r0 = move-exception;
        goto L_0x08e2;
    L_0x0a8d:
        r0 = move-exception;
        goto L_0x08fa;
    L_0x0a90:
        r3 = 2;
        if (r15 != r3) goto L_0x0b13;
    L_0x0a93:
        r3 = r1.database;	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r5 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)";
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r9 = 0;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r5 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r4 = r3.next();	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        if (r4 == 0) goto L_0x0ab8;
    L_0x0ab3:
        r10 = r3.intValue(r9);	 Catch:{ Exception -> 0x0a8d, all -> 0x0a8a }
        goto L_0x0ab9;
    L_0x0ab8:
        r10 = 0;
    L_0x0ab9:
        r3.dispose();	 Catch:{ Exception -> 0x0b11, all -> 0x0b0f }
        r12 = r34;
        if (r10 != r12) goto L_0x0b09;
    L_0x0ac0:
        r3 = r1.database;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r5 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r6 = 6;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 0;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 1;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = r2 / 2;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 2;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 3;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 4;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r6 = r2 / 2;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r9 = 5;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0b07, all -> 0x0b05 }
        r4 = r3;
        r3 = 1;
        goto L_0x0b0c;
    L_0x0b05:
        r0 = move-exception;
        goto L_0x0b2c;
    L_0x0b07:
        r0 = move-exception;
        goto L_0x0b47;
    L_0x0b09:
        r3 = 0;
        r4 = r3;
        r3 = 0;
    L_0x0b0c:
        r10 = r3;
        r3 = r4;
        goto L_0x0b17;
    L_0x0b0f:
        r0 = move-exception;
        goto L_0x0b2a;
    L_0x0b11:
        r0 = move-exception;
        goto L_0x0b45;
    L_0x0b13:
        r12 = r34;
        r3 = 0;
    L_0x0b16:
        r10 = 0;
    L_0x0b17:
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r14 = r29;
        r2 = r30;
        r11 = r33;
        r12 = r38;
        goto L_0x0ed0;
    L_0x0b25:
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
    L_0x0b2a:
        r12 = r34;
    L_0x0b2c:
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        r12 = r38;
        goto L_0x154d;
    L_0x0b40:
        r0 = move-exception;
        r38 = r6;
        r35 = r10;
    L_0x0b45:
        r12 = r34;
    L_0x0b47:
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        r12 = r38;
        goto L_0x151f;
    L_0x0b59:
        r0 = move-exception;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        goto L_0x0467;
    L_0x0b70:
        r0 = move-exception;
        r35 = r10;
        r12 = r34;
        r7 = r39;
        r6 = r2;
        r13 = r12;
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
        r17 = r35;
        goto L_0x047a;
    L_0x0b85:
        r0 = move-exception;
        r12 = r34;
        goto L_0x0b8f;
    L_0x0b89:
        r0 = move-exception;
        r12 = r34;
        goto L_0x0ba4;
    L_0x0b8d:
        r0 = move-exception;
        r12 = r10;
    L_0x0b8f:
        r7 = r39;
        r21 = r0;
        r6 = r2;
    L_0x0b94:
        r13 = r12;
    L_0x0b95:
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
    L_0x0b9d:
        r12 = 0;
        r17 = 0;
        goto L_0x154d;
    L_0x0ba2:
        r0 = move-exception;
        r12 = r10;
    L_0x0ba4:
        r7 = r39;
        r6 = r2;
    L_0x0ba7:
        r13 = r12;
    L_0x0ba8:
        r11 = r27;
        r14 = r29;
        r20 = r30;
        r19 = r32;
    L_0x0bb0:
        r12 = 0;
        r17 = 0;
        goto L_0x151f;
    L_0x0bb5:
        r36 = r6;
        r27 = r7;
        r28 = r10;
        r26 = r12;
        r3 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";
        r4 = 3;
        if (r15 != r4) goto L_0x0cbe;
    L_0x0bc2:
        if (r44 != 0) goto L_0x0cbe;
    L_0x0bc4:
        r4 = r1.database;	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r6 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r9 = 0;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r5 = java.lang.String.format(r5, r3, r7);	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r6 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        if (r5 == 0) goto L_0x0be7;
    L_0x0be2:
        r10 = r4.intValue(r9);	 Catch:{ Exception -> 0x0cb1, all -> 0x0ca2 }
        goto L_0x0be8;
    L_0x0be7:
        r10 = 0;
    L_0x0be8:
        r4.dispose();	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r4 = r1.database;	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r6 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";
        r7 = 1;
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r11 = 0;
        r9[r11] = r7;	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r5 = java.lang.String.format(r5, r6, r9);	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r4 = r4.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r5 = r4.next();	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c0b:
        r5 = r4.intValue(r11);	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        r6 = 1;
        r7 = r4.intValue(r6);	 Catch:{ Exception -> 0x0c8f, all -> 0x0c7a }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = 0;
        r7 = 0;
    L_0x0CLASSNAME:
        r4.dispose();	 Catch:{ Exception -> 0x0c6f, all -> 0x0CLASSNAME }
        if (r5 == 0) goto L_0x0c5e;
    L_0x0c1c:
        r4 = r1.database;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";
        r10 = 2;
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r10 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r12 = 0;
        r11[r12] = r10;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r10 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r19 = 1;
        r11[r19] = r10;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r6 = java.lang.String.format(r6, r9, r11);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r9 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r4 = r4.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        r6 = r4.next();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r10 = r4.intValue(r12);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r10 = 0;
    L_0x0c4a:
        r4.dispose();	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = 3;
        goto L_0x0cc2;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        goto L_0x0CLASSNAME;
    L_0x0c5e:
        r5 = r10;
        r4 = 3;
        goto L_0x0cc1;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r14 = r7;
        r33 = r10;
    L_0x0CLASSNAME:
        r11 = r27;
        r12 = 0;
        r13 = 0;
        goto L_0x14dc;
    L_0x0c6f:
        r0 = move-exception;
        r6 = r2;
        r14 = r7;
        r33 = r10;
    L_0x0CLASSNAME:
        r11 = r27;
        r12 = 0;
        r13 = 0;
        goto L_0x14f1;
    L_0x0c7a:
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r33 = r10;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
    L_0x0CLASSNAME:
        r19 = 0;
        r20 = 0;
        goto L_0x154d;
    L_0x0c8f:
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r33 = r10;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
    L_0x0c9c:
        r19 = 0;
        r20 = 0;
        goto L_0x151f;
    L_0x0ca2:
        r0 = move-exception;
        r7 = r39;
        r21 = r0;
        r6 = r2;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
        goto L_0x1507;
    L_0x0cb1:
        r0 = move-exception;
        r7 = r39;
        r6 = r2;
        r11 = r27;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
        goto L_0x1519;
    L_0x0cbe:
        r4 = 3;
        r5 = 0;
        r7 = 0;
    L_0x0cc1:
        r10 = 0;
    L_0x0cc2:
        if (r15 == r4) goto L_0x0e65;
    L_0x0cc4:
        r6 = 4;
        if (r15 != r6) goto L_0x0cc9;
    L_0x0cc7:
        goto L_0x0e65;
    L_0x0cc9:
        r6 = 1;
        if (r15 != r6) goto L_0x0d1a;
    L_0x0ccc:
        r3 = r1.database;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d";
        r9 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 0;
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 1;
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 2;
        r9[r11] = r4;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.String.format(r6, r8, r9);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = 0;
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
    L_0x0cf4:
        r8 = r39;
        r6 = r2;
        r11 = r5;
        r14 = r7;
        r13 = r10;
        r2 = 0;
        r10 = 0;
        r12 = 0;
    L_0x0cfd:
        r32 = 0;
        r35 = 1;
        r7 = r8;
        goto L_0x0ed0;
    L_0x0d04:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14db;
    L_0x0d10:
        r0 = move-exception;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14f0;
    L_0x0d1a:
        if (r44 == 0) goto L_0x0d79;
    L_0x0d1c:
        if (r39 == 0) goto L_0x0d48;
    L_0x0d1e:
        r3 = r1.database;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d";
        r8 = 3;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 0;
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Integer.valueOf(r39);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 1;
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 2;
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.String.format(r4, r6, r9);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = 0;
        r8 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        goto L_0x0cf4;
    L_0x0d48:
        r3 = r1.database;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d";
        r8 = 4;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r11 = 0;
        r9[r11] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r12 = 1;
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r12 = 2;
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r8 = java.lang.Integer.valueOf(r38);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r12 = 3;
        r9[r12] = r8;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = java.lang.String.format(r4, r6, r9);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        goto L_0x0cf4;
    L_0x0d79:
        r4 = 2;
        if (r15 != r4) goto L_0x0e00;
    L_0x0d7c:
        r4 = r1.database;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r12 = 0;
        r11[r12] = r9;	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r3 = java.lang.String.format(r6, r3, r11);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r3 = r4.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        r4 = r3.next();	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        if (r4 == 0) goto L_0x0d9f;
    L_0x0d9a:
        r4 = r3.intValue(r12);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        goto L_0x0da0;
    L_0x0d9f:
        r4 = 0;
    L_0x0da0:
        r3.dispose();	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = r1.database;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r9 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";
        r11 = 1;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r13 = 0;
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r9 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = r3.next();	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        if (r6 == 0) goto L_0x0dcc;
    L_0x0dc3:
        r5 = r3.intValue(r13);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = 1;
        r7 = r3.intValue(r6);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
    L_0x0dcc:
        r3.dispose();	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        if (r5 == 0) goto L_0x0e01;
    L_0x0dd1:
        r3 = r1.database;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";
        r11 = 2;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r13 = 0;
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r14 = 1;
        r12[r14] = r11;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = java.lang.String.format(r6, r9, r12);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r9 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r6 = r3.next();	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        if (r6 == 0) goto L_0x0dfc;
    L_0x0df8:
        r10 = r3.intValue(r13);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
    L_0x0dfc:
        r3.dispose();	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        goto L_0x0e01;
    L_0x0e00:
        r4 = 0;
    L_0x0e01:
        if (r2 > r10) goto L_0x0e0b;
    L_0x0e03:
        if (r10 >= r8) goto L_0x0e06;
    L_0x0e05:
        goto L_0x0e0b;
    L_0x0e06:
        r3 = r10 - r2;
        r2 = r2 + 10;
        goto L_0x0e19;
    L_0x0e0b:
        r3 = r10 + 10;
        r2 = java.lang.Math.max(r2, r3);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        if (r10 >= r8) goto L_0x0e18;
    L_0x0e13:
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r10 = 0;
        goto L_0x0e19;
    L_0x0e18:
        r3 = 0;
    L_0x0e19:
        r6 = r1.database;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r9 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d";
        r11 = 3;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r13 = 0;
        r12[r13] = r11;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = 1;
        r12[r11] = r3;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r11 = 2;
        r12[r11] = r3;	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = java.lang.String.format(r8, r9, r12);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
        r3 = r6.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x0e5a, all -> 0x0e4d }
    L_0x0e42:
        r8 = r39;
        r6 = r2;
        r12 = r4;
        r11 = r5;
        r14 = r7;
        r13 = r10;
        r2 = 0;
        r10 = 0;
        goto L_0x0cfd;
    L_0x0e4d:
        r0 = move-exception;
        r21 = r0;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14dc;
    L_0x0e5a:
        r0 = move-exception;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        r11 = r27;
        goto L_0x14f1;
    L_0x0e65:
        r4 = r1.database;	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r8 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r11 = 0;
        r9[r11] = r8;	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r3 = java.lang.String.format(r6, r3, r9);	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r3 = r4.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        r4 = r3.next();	 Catch:{ Exception -> 0x14e6, all -> 0x14cf }
        if (r4 == 0) goto L_0x0e88;
    L_0x0e83:
        r4 = r3.intValue(r11);	 Catch:{ Exception -> 0x0d10, all -> 0x0d04 }
        goto L_0x0e89;
    L_0x0e88:
        r4 = 0;
    L_0x0e89:
        r3.dispose();	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r3 = r1.database;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r8 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)";
        r9 = 6;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 0;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 1;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = r2 / 2;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 2;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 3;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 4;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r9 = r2 / 2;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r12 = 5;
        r11[r12] = r9;	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r6 = java.lang.String.format(r6, r8, r11);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x14b0, all -> 0x14ab }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x14c3, all -> 0x14b5 }
        goto L_0x0e42;
    L_0x0ed0:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r3 == 0) goto L_0x1257;
    L_0x0ed5:
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x0ed7:
        r29 = r3.next();	 Catch:{ Exception -> 0x123b, all -> 0x121e }
        if (r29 == 0) goto L_0x11ea;
    L_0x0edd:
        r39 = r14;
        r5 = 1;
        r14 = r3.byteBufferValue(r5);	 Catch:{ Exception -> 0x11d7, all -> 0x11c4 }
        if (r14 == 0) goto L_0x118e;
    L_0x0ee6:
        r44 = r13;
        r5 = 0;
        r13 = r14.readInt32(r5);	 Catch:{ Exception -> 0x117b, all -> 0x1168 }
        r13 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r14, r13, r5);	 Catch:{ Exception -> 0x117b, all -> 0x1168 }
        r29 = r12;
        r5 = 2;
        r12 = r3.intValue(r5);	 Catch:{ Exception -> 0x1152, all -> 0x113b }
        r13.send_state = r12;	 Catch:{ Exception -> 0x1152, all -> 0x113b }
        r5 = r13.id;	 Catch:{ Exception -> 0x1152, all -> 0x113b }
        if (r5 <= 0) goto L_0x0var_;
    L_0x0efe:
        r5 = r13.send_state;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0b }
        if (r5 == 0) goto L_0x0var_;
    L_0x0var_:
        r5 = r13.send_state;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0b }
        r12 = 3;
        if (r5 == r12) goto L_0x0var_;
    L_0x0var_:
        r5 = 0;
        r13.send_state = r5;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0b }
        goto L_0x0var_;
    L_0x0f0b:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        r33 = r11;
        r11 = r27;
        r12 = r29;
        r19 = r32;
    L_0x0f1c:
        r17 = r35;
        goto L_0x154d;
    L_0x0var_:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        r33 = r11;
        r11 = r27;
        r12 = r29;
        r19 = r32;
    L_0x0f2f:
        r17 = r35;
        goto L_0x151f;
    L_0x0var_:
        r30 = r11;
        r5 = r36;
        r11 = (long) r5;
        r33 = r6;
        r31 = r7;
        r6 = r41;
        r34 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r34 != 0) goto L_0x0var_;
    L_0x0var_:
        r11 = 1;
        r13.out = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        goto L_0x0var_;
    L_0x0var_:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        r11 = r27;
        goto L_0x148d;
    L_0x0var_:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        r11 = r27;
    L_0x0f5c:
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        r17 = r35;
        r2 = r0;
        goto L_0x1253;
    L_0x0var_:
        r13.readAttachPath(r14, r5);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r14.reuse();	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r11 = 0;
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        org.telegram.messenger.MessageObject.setUnreadFlags(r13, r12);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r11 = 3;
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r13.id = r12;	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r11 = r13.id;	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        if (r11 <= 0) goto L_0x0f8e;
    L_0x0var_:
        r11 = r13.id;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r4 = java.lang.Math.min(r11, r4);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = r13.id;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r9 = java.lang.Math.max(r11, r9);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
    L_0x0f8e:
        r11 = 4;
        r12 = r3.intValue(r11);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r13.date = r12;	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r13.dialog_id = r6;	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r11 = r13.flags;	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        r11 = r11 & 1024;
        if (r11 == 0) goto L_0x0fa4;
    L_0x0f9d:
        r11 = 7;
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r13.views = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
    L_0x0fa4:
        if (r28 == 0) goto L_0x0fb2;
    L_0x0fa6:
        r11 = r13.ttl;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        if (r11 != 0) goto L_0x0fb2;
    L_0x0faa:
        r11 = 8;
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r13.ttl = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
    L_0x0fb2:
        r11 = 9;
        r11 = r3.intValue(r11);	 Catch:{ Exception -> 0x1134, all -> 0x112d }
        if (r11 == 0) goto L_0x0fbd;
    L_0x0fba:
        r11 = 1;
        r13.mentioned = r11;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
    L_0x0fbd:
        r11 = r27;
        r12 = r11.messages;	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        r12.add(r13);	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        r12 = r24;
        r14 = r26;
        addUsersAndChatsFromMessage(r13, r12, r14);	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        r24 = r4;
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        if (r4 != 0) goto L_0x0ffa;
    L_0x0fd1:
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r16 = 0;
        r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x0fda;
    L_0x0fd9:
        goto L_0x0ffa;
    L_0x0fda:
        r36 = r5;
        r5 = r21;
        r4 = r23;
        r18 = 32;
        r21 = r8;
        goto L_0x10c0;
    L_0x0fe6:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r2;
        goto L_0x148d;
    L_0x0ff1:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r20 = r2;
        goto L_0x0f5c;
    L_0x0ffa:
        r6 = 6;
        r4 = r3.isNull(r6);	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        if (r4 != 0) goto L_0x1032;
    L_0x1001:
        r4 = r3.byteBufferValue(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 == 0) goto L_0x1032;
    L_0x1007:
        r7 = 0;
        r6 = r4.readInt32(r7);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r4, r6, r7);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r13.replyMessage = r6;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6.readAttachPath(r4, r5);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4.reuse();	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 == 0) goto L_0x1032;
    L_0x101e:
        r4 = org.telegram.messenger.MessageObject.isMegagroup(r13);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 == 0) goto L_0x102d;
    L_0x1024:
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = r4.flags;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r6 = r6 | r7;
        r4.flags = r6;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x102d:
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        addUsersAndChatsFromMessage(r4, r12, r14);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x1032:
        r4 = r13.replyMessage;	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        if (r4 != 0) goto L_0x0fda;
    L_0x1036:
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 == 0) goto L_0x1087;
    L_0x103a:
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = (long) r4;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r13.to_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 == 0) goto L_0x1050;
    L_0x1043:
        r4 = r13.to_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r36 = r5;
        r4 = (long) r4;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r18 = 32;
        r4 = r4 << r18;
        r6 = r6 | r4;
        goto L_0x1054;
    L_0x1050:
        r36 = r5;
        r18 = 32;
    L_0x1054:
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r5 = r21;
        r4 = r5.contains(r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 != 0) goto L_0x1067;
    L_0x1060:
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r5.add(r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x1067:
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = r22;
        r4 = r6.get(r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = (java.util.ArrayList) r4;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 != 0) goto L_0x107d;
    L_0x1073:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4.<init>();	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r7 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6.put(r7, r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x107d:
        r4.add(r13);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r22 = r6;
        r21 = r8;
        r4 = r23;
        goto L_0x10c0;
    L_0x1087:
        r36 = r5;
        r5 = r21;
        r18 = 32;
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r5.contains(r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r4 != 0) goto L_0x10a2;
    L_0x1099:
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r5.add(r4);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x10a2:
        r6 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4 = r23;
        r6 = r4.get(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6 = (java.util.ArrayList) r6;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r6 != 0) goto L_0x10bb;
    L_0x10ae:
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r6.<init>();	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r21 = r8;
        r7 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r4.put(r7, r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        goto L_0x10bd;
    L_0x10bb:
        r21 = r8;
    L_0x10bd:
        r6.add(r13);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
    L_0x10c0:
        if (r28 != 0) goto L_0x10d0;
    L_0x10c2:
        r6 = 5;
        r7 = r3.isNull(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        if (r7 != 0) goto L_0x10d1;
    L_0x10c9:
        r7 = r3.longValue(r6);	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        r13.random_id = r7;	 Catch:{ Exception -> 0x0ff1, all -> 0x0fe6 }
        goto L_0x10d1;
    L_0x10d0:
        r6 = 5;
    L_0x10d1:
        r7 = org.telegram.messenger.MessageObject.isSecretMedia(r13);	 Catch:{ Exception -> 0x112b, all -> 0x1129 }
        if (r7 == 0) goto L_0x111b;
    L_0x10d7:
        r7 = r1.database;	 Catch:{ Exception -> 0x110f, all -> 0x1129 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x110f, all -> 0x1129 }
        r6 = "SELECT date FROM enc_tasks_v2 WHERE mid = %d";
        r23 = r4;
        r26 = r9;
        r4 = 1;
        r9 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x110b, all -> 0x1129 }
        r4 = r13.id;	 Catch:{ Exception -> 0x110b, all -> 0x1129 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x110b, all -> 0x1129 }
        r27 = r2;
        r2 = 0;
        r9[r2] = r4;	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        r4 = java.lang.String.format(r8, r6, r9);	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        r6 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        r4 = r7.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        r6 = r4.next();	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        if (r6 == 0) goto L_0x1105;
    L_0x10ff:
        r6 = r4.intValue(r2);	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        r13.destroyTime = r6;	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
    L_0x1105:
        r4.dispose();	 Catch:{ Exception -> 0x1109, all -> 0x1209 }
        goto L_0x1121;
    L_0x1109:
        r0 = move-exception;
        goto L_0x1116;
    L_0x110b:
        r0 = move-exception;
        r27 = r2;
        goto L_0x1116;
    L_0x110f:
        r0 = move-exception;
        r27 = r2;
        r23 = r4;
        r26 = r9;
    L_0x1116:
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        goto L_0x1121;
    L_0x111b:
        r27 = r2;
        r23 = r4;
        r26 = r9;
    L_0x1121:
        r2 = r23;
        r4 = r24;
        r9 = r26;
        goto L_0x11a8;
    L_0x1129:
        r0 = move-exception;
        goto L_0x1130;
    L_0x112b:
        r0 = move-exception;
        goto L_0x1137;
    L_0x112d:
        r0 = move-exception;
        r11 = r27;
    L_0x1130:
        r27 = r2;
        goto L_0x120a;
    L_0x1134:
        r0 = move-exception;
        r11 = r27;
    L_0x1137:
        r27 = r2;
        goto L_0x1215;
    L_0x113b:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r27;
        r12 = r29;
        goto L_0x1233;
    L_0x1152:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r27;
        r12 = r29;
        goto L_0x124f;
    L_0x1168:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        r13 = r44;
        goto L_0x122f;
    L_0x117b:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        r13 = r44;
        goto L_0x124c;
    L_0x118e:
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r5 = r21;
        r12 = r24;
        r14 = r26;
        r11 = r27;
        r18 = 32;
        r27 = r2;
        r21 = r8;
        r2 = r23;
    L_0x11a8:
        r13 = r44;
        r23 = r2;
        r24 = r12;
        r26 = r14;
        r8 = r21;
        r2 = r27;
        r12 = r29;
        r7 = r31;
        r6 = r33;
        r14 = r39;
        r21 = r5;
        r27 = r11;
        r11 = r30;
        goto L_0x0ed7;
    L_0x11c4:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        goto L_0x122f;
    L_0x11d7:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r11 = r27;
        r27 = r2;
        r14 = r39;
        goto L_0x124c;
    L_0x11ea:
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r5 = r21;
        r12 = r24;
        r14 = r26;
        r11 = r27;
        r27 = r2;
        r21 = r8;
        r2 = r23;
        r3.dispose();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        goto L_0x1273;
    L_0x1209:
        r0 = move-exception;
    L_0x120a:
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r27;
        goto L_0x148d;
    L_0x1214:
        r0 = move-exception;
    L_0x1215:
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r27;
        goto L_0x14a1;
    L_0x121e:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r11 = r27;
        r27 = r2;
    L_0x122f:
        r21 = r0;
        r20 = r27;
    L_0x1233:
        r19 = r32;
    L_0x1235:
        r17 = r35;
        r33 = r30;
        goto L_0x154d;
    L_0x123b:
        r0 = move-exception;
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r11 = r27;
        r27 = r2;
    L_0x124c:
        r2 = r0;
        r20 = r27;
    L_0x124f:
        r19 = r32;
    L_0x1251:
        r17 = r35;
    L_0x1253:
        r33 = r30;
        goto L_0x1520;
    L_0x1257:
        r33 = r6;
        r31 = r7;
        r30 = r11;
        r29 = r12;
        r44 = r13;
        r39 = r14;
        r5 = r21;
        r12 = r24;
        r14 = r26;
        r11 = r27;
        r27 = r2;
        r21 = r8;
        r2 = r23;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x1273:
        r3 = r11.messages;	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r6 = org.telegram.messenger.-$$Lambda$MessagesStorage$CLASSNAMELZFrTLl65dkoQewWuR5V0NSU.INSTANCE;	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        java.util.Collections.sort(r3, r6);	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        if (r28 == 0) goto L_0x12bc;
    L_0x127c:
        r3 = 3;
        if (r15 == r3) goto L_0x128c;
    L_0x127f:
        r3 = 4;
        if (r15 == r3) goto L_0x128c;
    L_0x1282:
        r3 = 2;
        if (r15 != r3) goto L_0x128a;
    L_0x1285:
        if (r32 == 0) goto L_0x128a;
    L_0x1287:
        if (r10 != 0) goto L_0x128a;
    L_0x1289:
        goto L_0x128c;
    L_0x128a:
        r3 = 4;
        goto L_0x12a9;
    L_0x128c:
        r3 = r11.messages;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r3.isEmpty();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r3 != 0) goto L_0x128a;
    L_0x1294:
        r8 = r21;
        if (r4 > r8) goto L_0x129a;
    L_0x1298:
        if (r9 >= r8) goto L_0x128a;
    L_0x129a:
        r5.clear();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r12.clear();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r14.clear();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r11.messages;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3.clear();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        goto L_0x128a;
    L_0x12a9:
        if (r15 == r3) goto L_0x12ae;
    L_0x12ab:
        r3 = 3;
        if (r15 != r3) goto L_0x12bc;
    L_0x12ae:
        r3 = r11.messages;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r3.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r4 = 1;
        if (r3 != r4) goto L_0x12bc;
    L_0x12b7:
        r3 = r11.messages;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3.clear();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
    L_0x12bc:
        r3 = r5.isEmpty();	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r4 = ",";
        if (r3 != 0) goto L_0x13e0;
    L_0x12c4:
        r3 = r22.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r3 <= 0) goto L_0x12e6;
    L_0x12ca:
        r3 = r1.database;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7 = "SELECT data, mid, date FROM messages WHERE mid IN(%s)";
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = android.text.TextUtils.join(r4, r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = 0;
        r9[r8] = r5;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r3.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
    L_0x12e4:
        r8 = 0;
        goto L_0x1300;
    L_0x12e6:
        r3 = r1.database;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7 = "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)";
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = android.text.TextUtils.join(r4, r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = 0;
        r9[r8] = r5;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = java.lang.String.format(r6, r7, r9);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r3.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
    L_0x1300:
        r5 = r3.next();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r5 == 0) goto L_0x13b1;
    L_0x1306:
        r5 = r3.byteBufferValue(r8);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r5 == 0) goto L_0x13a8;
    L_0x130c:
        r6 = r5.readInt32(r8);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r6, r8);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7 = r36;
        r6.readAttachPath(r5, r7);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5.reuse();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = 1;
        r8 = r3.intValue(r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6.id = r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = 2;
        r8 = r3.intValue(r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6.date = r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = r41;
        r6.dialog_id = r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        addUsersAndChatsFromMessage(r6, r12, r14);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r10 = r22.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r10 <= 0) goto L_0x136b;
    L_0x1337:
        r10 = r6.id;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r13 = r22;
        r10 = r13.get(r10);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r10 = (java.util.ArrayList) r10;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r10 == 0) goto L_0x1368;
    L_0x1343:
        r36 = r7;
        r5 = 0;
    L_0x1346:
        r7 = r10.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r5 >= r7) goto L_0x13aa;
    L_0x134c:
        r7 = r10.get(r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7.replyMessage = r6;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r18 = org.telegram.messenger.MessageObject.isMegagroup(r7);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r18 == 0) goto L_0x1363;
    L_0x135a:
        r7 = r7.replyMessage;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = r7.flags;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r8 = r8 | r9;
        r7.flags = r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
    L_0x1363:
        r5 = r5 + 1;
        r8 = r41;
        goto L_0x1346;
    L_0x1368:
        r36 = r7;
        goto L_0x13aa;
    L_0x136b:
        r36 = r7;
        r13 = r22;
        r5 = 3;
        r7 = r3.longValue(r5);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r9 = r2.get(r7);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r9 = (java.util.ArrayList) r9;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r2.remove(r7);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r9 == 0) goto L_0x13ab;
    L_0x137f:
        r7 = 0;
    L_0x1380:
        r8 = r9.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r7 >= r8) goto L_0x13ab;
    L_0x1386:
        r8 = r9.get(r7);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8.replyMessage = r6;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r10 = r6.id;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8.reply_to_msg_id = r10;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r10 = org.telegram.messenger.MessageObject.isMegagroup(r8);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r10 == 0) goto L_0x13a3;
    L_0x1398:
        r8 = r8.replyMessage;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r10 = r8.flags;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r10 = r10 | r18;
        r8.flags = r10;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        goto L_0x13a5;
    L_0x13a3:
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x13a5:
        r7 = r7 + 1;
        goto L_0x1380;
    L_0x13a8:
        r13 = r22;
    L_0x13aa:
        r5 = 3;
    L_0x13ab:
        r18 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r22 = r13;
        goto L_0x12e4;
    L_0x13b1:
        r3.dispose();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r3 = r2.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r3 <= 0) goto L_0x13e0;
    L_0x13ba:
        r3 = 0;
    L_0x13bb:
        r5 = r2.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r3 >= r5) goto L_0x13e0;
    L_0x13c1:
        r5 = r2.valueAt(r3);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r5 = (java.util.ArrayList) r5;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = 0;
    L_0x13c8:
        r7 = r5.size();	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        if (r6 >= r7) goto L_0x13db;
    L_0x13ce:
        r7 = r5.get(r6);	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r8 = 0;
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x1214, all -> 0x1209 }
        r6 = r6 + 1;
        goto L_0x13c8;
    L_0x13db:
        r8 = 0;
        r3 = r3 + 1;
        goto L_0x13bb;
    L_0x13e0:
        if (r27 == 0) goto L_0x142d;
    L_0x13e2:
        r2 = r1.database;	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r5 = "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)";
        r6 = 1;
        r6 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r7 = java.lang.Long.valueOf(r41);	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r8 = 0;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r3 = java.lang.String.format(r3, r5, r6);	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r5 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r3 = r2.next();	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        if (r3 == 0) goto L_0x140f;
    L_0x1402:
        r3 = r2.intValue(r8);	 Catch:{ Exception -> 0x1497, all -> 0x1482 }
        r5 = r27;
        if (r5 == r3) goto L_0x140d;
    L_0x140a:
        r3 = r5 * -1;
        goto L_0x1412;
    L_0x140d:
        r3 = r5;
        goto L_0x1412;
    L_0x140f:
        r5 = r27;
        goto L_0x140a;
    L_0x1412:
        r2.dispose();	 Catch:{ Exception -> 0x1423, all -> 0x1418 }
        r20 = r3;
        goto L_0x1431;
    L_0x1418:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r3;
        goto L_0x148d;
    L_0x1423:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r3;
        goto L_0x14a1;
    L_0x142d:
        r5 = r27;
        r20 = r5;
    L_0x1431:
        r2 = r12.isEmpty();	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        if (r2 != 0) goto L_0x1440;
    L_0x1437:
        r2 = android.text.TextUtils.join(r4, r12);	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        r3 = r11.users;	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        r1.getUsersInternal(r2, r3);	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
    L_0x1440:
        r2 = r14.isEmpty();	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        if (r2 != 0) goto L_0x144f;
    L_0x1446:
        r2 = android.text.TextUtils.join(r4, r14);	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        r3 = r11.chats;	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
        r1.getChatsInternal(r2, r3);	 Catch:{ Exception -> 0x147b, all -> 0x1473 }
    L_0x144f:
        r2 = r37.getMessagesController();
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r6 = r33;
        r7 = r31;
        r8 = r45;
        r10 = r46;
        r11 = r30;
        r12 = r29;
        r13 = r44;
        r14 = r39;
        r15 = r43;
        r16 = r40;
        r17 = r35;
        r18 = r47;
        r19 = r32;
        goto L_0x1546;
    L_0x1473:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        goto L_0x148d;
    L_0x147b:
        r0 = move-exception;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        goto L_0x14a1;
    L_0x1482:
        r0 = move-exception;
        r5 = r27;
        r14 = r39;
        r13 = r44;
        r21 = r0;
        r20 = r5;
    L_0x148d:
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        goto L_0x1235;
    L_0x1497:
        r0 = move-exception;
        r5 = r27;
        r14 = r39;
        r13 = r44;
        r2 = r0;
        r20 = r5;
    L_0x14a1:
        r12 = r29;
        r7 = r31;
        r19 = r32;
        r6 = r33;
        goto L_0x1251;
    L_0x14ab:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        goto L_0x14ba;
    L_0x14b0:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        goto L_0x14c8;
    L_0x14b5:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
    L_0x14ba:
        r21 = r0;
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        goto L_0x14dc;
    L_0x14c3:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
    L_0x14c8:
        r6 = r2;
        r12 = r4;
        r33 = r5;
        r14 = r7;
        r13 = r10;
        goto L_0x14f1;
    L_0x14cf:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r21 = r0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
    L_0x14db:
        r12 = 0;
    L_0x14dc:
        r17 = 1;
        r19 = 0;
        r20 = 0;
    L_0x14e2:
        r7 = r39;
        goto L_0x154d;
    L_0x14e6:
        r0 = move-exception;
        r11 = r27;
        r6 = 1;
        r8 = 0;
        r6 = r2;
        r33 = r5;
        r14 = r7;
        r13 = r10;
    L_0x14f0:
        r12 = 0;
    L_0x14f1:
        r17 = 1;
        r19 = 0;
        r20 = 0;
    L_0x14f7:
        r7 = r39;
        goto L_0x151f;
    L_0x14fa:
        r0 = move-exception;
        r11 = r7;
        r8 = 0;
        r7 = r39;
        r21 = r0;
        r6 = r2;
    L_0x1502:
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
    L_0x1507:
        r19 = 0;
        r20 = 0;
        r33 = 0;
        goto L_0x154d;
    L_0x150e:
        r0 = move-exception;
        r11 = r7;
        r8 = 0;
        r7 = r39;
        r6 = r2;
    L_0x1514:
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
    L_0x1519:
        r19 = 0;
        r20 = 0;
        r33 = 0;
    L_0x151f:
        r2 = r0;
    L_0x1520:
        r3 = r11.messages;	 Catch:{ all -> 0x154a }
        r3.clear();	 Catch:{ all -> 0x154a }
        r3 = r11.chats;	 Catch:{ all -> 0x154a }
        r3.clear();	 Catch:{ all -> 0x154a }
        r3 = r11.users;	 Catch:{ all -> 0x154a }
        r3.clear();	 Catch:{ all -> 0x154a }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x154a }
        r2 = r37.getMessagesController();
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r8 = r45;
        r10 = r46;
        r11 = r33;
        r15 = r43;
        r16 = r40;
        r18 = r47;
    L_0x1546:
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        return;
    L_0x154a:
        r0 = move-exception;
        r21 = r0;
    L_0x154d:
        r2 = r37.getMessagesController();
        r9 = 1;
        r3 = r11;
        r4 = r41;
        r8 = r45;
        r10 = r46;
        r11 = r33;
        r15 = r43;
        r16 = r40;
        r18 = r47;
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x1566;
    L_0x1565:
        throw r21;
    L_0x1566:
        goto L_0x1565;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getMessages$97$MessagesStorage(int, int, boolean, long, int, int, int, int, int):void");
    }

    static /* synthetic */ int lambda$null$96(Message message, Message message2) {
        int i;
        int i2 = message.id;
        if (i2 > 0) {
            i = message2.id;
            if (i > 0) {
                if (i2 > i) {
                    return -1;
                }
                if (i2 < i) {
                    return 1;
                }
                return 0;
            }
        }
        i2 = message.id;
        if (i2 < 0) {
            i = message2.id;
            if (i < 0) {
                if (i2 < i) {
                    return -1;
                }
                if (i2 > i) {
                    return 1;
                }
                return 0;
            }
        }
        int i3 = message.date;
        int i4 = message2.date;
        if (i3 > i4) {
            return -1;
        }
        if (i3 < i4) {
            return 1;
        }
        return 0;
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$hEMt8kVa7_9jVBI_OcKzsRTNYfs(this));
    }

    public /* synthetic */ void lambda$clearSentMedia$98$MessagesStorage() {
        try {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public Object[] getSentFile(String str, int i) {
        if (str == null || str.toLowerCase().endsWith("attheme")) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Object[] objArr = new Object[2];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$QWdrq-4g4JYfZwNZOjI7cCjbrHw(this, str, i, objArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (objArr[0] != null) {
            return objArr;
        }
        return null;
    }

    public /* synthetic */ void lambda$getSentFile$99$MessagesStorage(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        try {
            if (Utilities.MD5(str) != null) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", new Object[]{str, Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize instanceof TL_messageMediaDocument) {
                            objArr[0] = ((TL_messageMediaDocument) TLdeserialize).document;
                        } else if (TLdeserialize instanceof TL_messageMediaPhoto) {
                            objArr[0] = ((TL_messageMediaPhoto) TLdeserialize).photo;
                        }
                        if (objArr[0] != null) {
                            objArr[1] = queryFinalized.stringValue(1);
                        }
                    }
                }
                queryFinalized.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void putSentFile(String str, TLObject tLObject, int i, String str2) {
        if (str != null && tLObject != null && str2 != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$xOcZGzs7NnxvCNaEtCc4rdeSwRk(this, str, tLObject, i, str2));
        }
    }

    /* JADX WARNING: Missing block: B:14:0x005e, code skipped:
            if (r0 != null) goto L_0x0060;
     */
    /* JADX WARNING: Missing block: B:15:0x0060, code skipped:
            r0.dispose();
     */
    /* JADX WARNING: Missing block: B:20:0x006a, code skipped:
            if (r0 == null) goto L_0x006d;
     */
    /* JADX WARNING: Missing block: B:21:0x006d, code skipped:
            return;
     */
    public /* synthetic */ void lambda$putSentFile$100$MessagesStorage(java.lang.String r5, org.telegram.tgnet.TLObject r6, int r7, java.lang.String r8) {
        /*
        r4 = this;
        r0 = 0;
        r5 = org.telegram.messenger.Utilities.MD5(r5);	 Catch:{ Exception -> 0x0066 }
        if (r5 == 0) goto L_0x005e;
    L_0x0007:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.Photo;	 Catch:{ Exception -> 0x0066 }
        r2 = 1;
        if (r1 == 0) goto L_0x001b;
    L_0x000c:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0066 }
        r1.<init>();	 Catch:{ Exception -> 0x0066 }
        r6 = (org.telegram.tgnet.TLRPC.Photo) r6;	 Catch:{ Exception -> 0x0066 }
        r1.photo = r6;	 Catch:{ Exception -> 0x0066 }
        r6 = r1.flags;	 Catch:{ Exception -> 0x0066 }
        r6 = r6 | r2;
        r1.flags = r6;	 Catch:{ Exception -> 0x0066 }
        goto L_0x002f;
    L_0x001b:
        r1 = r6 instanceof org.telegram.tgnet.TLRPC.Document;	 Catch:{ Exception -> 0x0066 }
        if (r1 == 0) goto L_0x002e;
    L_0x001f:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0066 }
        r1.<init>();	 Catch:{ Exception -> 0x0066 }
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;	 Catch:{ Exception -> 0x0066 }
        r1.document = r6;	 Catch:{ Exception -> 0x0066 }
        r6 = r1.flags;	 Catch:{ Exception -> 0x0066 }
        r6 = r6 | r2;
        r1.flags = r6;	 Catch:{ Exception -> 0x0066 }
        goto L_0x002f;
    L_0x002e:
        r1 = r0;
    L_0x002f:
        if (r1 != 0) goto L_0x0032;
    L_0x0031:
        return;
    L_0x0032:
        r6 = r4.database;	 Catch:{ Exception -> 0x0066 }
        r3 = "REPLACE INTO sent_files_v2 VALUES(?, ?, ?, ?)";
        r0 = r6.executeFast(r3);	 Catch:{ Exception -> 0x0066 }
        r0.requery();	 Catch:{ Exception -> 0x0066 }
        r6 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0066 }
        r3 = r1.getObjectSize();	 Catch:{ Exception -> 0x0066 }
        r6.<init>(r3);	 Catch:{ Exception -> 0x0066 }
        r1.serializeToStream(r6);	 Catch:{ Exception -> 0x0066 }
        r0.bindString(r2, r5);	 Catch:{ Exception -> 0x0066 }
        r5 = 2;
        r0.bindInteger(r5, r7);	 Catch:{ Exception -> 0x0066 }
        r5 = 3;
        r0.bindByteBuffer(r5, r6);	 Catch:{ Exception -> 0x0066 }
        r5 = 4;
        r0.bindString(r5, r8);	 Catch:{ Exception -> 0x0066 }
        r0.step();	 Catch:{ Exception -> 0x0066 }
        r6.reuse();	 Catch:{ Exception -> 0x0066 }
    L_0x005e:
        if (r0 == 0) goto L_0x006d;
    L_0x0060:
        r0.dispose();
        goto L_0x006d;
    L_0x0064:
        r5 = move-exception;
        goto L_0x006e;
    L_0x0066:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x0064 }
        if (r0 == 0) goto L_0x006d;
    L_0x006c:
        goto L_0x0060;
    L_0x006d:
        return;
    L_0x006e:
        if (r0 == 0) goto L_0x0073;
    L_0x0070:
        r0.dispose();
    L_0x0073:
        goto L_0x0075;
    L_0x0074:
        throw r5;
    L_0x0075:
        goto L_0x0074;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putSentFile$100$MessagesStorage(java.lang.String, org.telegram.tgnet.TLObject, int, java.lang.String):void");
    }

    public void updateEncryptedChatSeq(EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$esm2dGC2pXSFo51wo7BrznSXZJM(this, encryptedChat, z));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatSeq$101$MessagesStorage(EncryptedChat encryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.seq_in);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.seq_out);
            sQLitePreparedStatement.bindInteger(3, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
            sQLitePreparedStatement.bindInteger(4, encryptedChat.in_seq_no);
            sQLitePreparedStatement.bindInteger(5, encryptedChat.mtproto_seq);
            sQLitePreparedStatement.bindInteger(6, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (z) {
                long j = ((long) encryptedChat.id) << 32;
                this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN (SELECT m.mid FROM messages as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d)", new Object[]{Long.valueOf(j), Integer.valueOf(encryptedChat.in_seq_no)})).stepThis().dispose();
            }
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChatTTL(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$y5RQnW721KWh7voiDNWU2ZjRBjk(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatTTL$102$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.ttl);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChatLayer(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6MH-H3xeSx6r2by_lHmByy3IDXY(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatLayer$103$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
            sQLitePreparedStatement.bindInteger(1, encryptedChat.layer);
            sQLitePreparedStatement.bindInteger(2, encryptedChat.id);
            sQLitePreparedStatement.step();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public void updateEncryptedChat(EncryptedChat encryptedChat) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$YCSJgh405BoGE1vmmitbFzOGAi8(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChat$104$MessagesStorage(EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            if ((encryptedChat.key_hash == null || encryptedChat.key_hash.length < 16) && encryptedChat.auth_key != null) {
                encryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            }
            sQLitePreparedStatement = this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat.a_or_b != null ? encryptedChat.a_or_b.length : 1);
            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat.auth_key != null ? encryptedChat.auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat.future_auth_key != null ? encryptedChat.future_auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat.key_hash != null ? encryptedChat.key_hash.length : 1);
            encryptedChat.serializeToStream(nativeByteBuffer);
            sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
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
            sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer2);
            sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer3);
            sQLitePreparedStatement.bindInteger(4, encryptedChat.ttl);
            sQLitePreparedStatement.bindInteger(5, encryptedChat.layer);
            sQLitePreparedStatement.bindInteger(6, encryptedChat.seq_in);
            sQLitePreparedStatement.bindInteger(7, encryptedChat.seq_out);
            sQLitePreparedStatement.bindInteger(8, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
            sQLitePreparedStatement.bindLong(9, encryptedChat.exchange_id);
            sQLitePreparedStatement.bindInteger(10, encryptedChat.key_create_date);
            sQLitePreparedStatement.bindLong(11, encryptedChat.future_key_fingerprint);
            sQLitePreparedStatement.bindByteBuffer(12, nativeByteBuffer4);
            sQLitePreparedStatement.bindByteBuffer(13, nativeByteBuffer5);
            sQLitePreparedStatement.bindInteger(14, encryptedChat.in_seq_no);
            sQLitePreparedStatement.bindInteger(15, encryptedChat.admin_id);
            sQLitePreparedStatement.bindInteger(16, encryptedChat.mtproto_seq);
            sQLitePreparedStatement.bindInteger(17, encryptedChat.id);
            sQLitePreparedStatement.step();
            nativeByteBuffer.reuse();
            nativeByteBuffer2.reuse();
            nativeByteBuffer3.reuse();
            nativeByteBuffer4.reuse();
            nativeByteBuffer5.reuse();
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (sQLitePreparedStatement == null) {
                return;
            }
        } catch (Throwable e2) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw e2;
        }
        sQLitePreparedStatement.dispose();
    }

    public boolean isDialogHasMessages(long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$qIo_fG8walbFTkecJLzGBbX26tk(this, j, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isDialogHasMessages$105$MessagesStorage(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[]{Long.valueOf(j)}), new Object[0]);
            zArr[0] = queryFinalized.next();
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public boolean hasAuthMessage(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bENBORpUff-a_gNxIeqUyafFs_U(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$hasAuthMessage$106$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[]{Integer.valueOf(i)}), new Object[0]);
            zArr[0] = queryFinalized.next();
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void getEncryptedChat(int i, CountDownLatch countDownLatch, ArrayList<TLObject> arrayList) {
        if (countDownLatch != null && arrayList != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$D7qP0aL0P-pp7eont4fHIfs6kco(this, i, arrayList, countDownLatch));
        }
    }

    public /* synthetic */ void lambda$getEncryptedChat$107$MessagesStorage(int i, ArrayList arrayList, CountDownLatch countDownLatch) {
        try {
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getEncryptedChatsInternal(stringBuilder.toString(), arrayList3, arrayList2);
            if (!(arrayList3.isEmpty() || arrayList2.isEmpty())) {
                ArrayList arrayList4 = new ArrayList();
                getUsersInternal(TextUtils.join(",", arrayList2), arrayList4);
                if (!arrayList4.isEmpty()) {
                    arrayList.add(arrayList3.get(0));
                    arrayList.add(arrayList4.get(0));
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            countDownLatch.countDown();
        }
        countDownLatch.countDown();
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, User user, Dialog dialog) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Jr00C9mxclVVnBu_VvDFU5PvtcQ(this, encryptedChat, user, dialog));
        }
    }

    public /* synthetic */ void lambda$putEncryptedChat$108$MessagesStorage(EncryptedChat encryptedChat, User user, Dialog dialog) {
        EncryptedChat encryptedChat2 = encryptedChat;
        User user2 = user;
        Dialog dialog2 = dialog;
        try {
            if ((encryptedChat2.key_hash == null || encryptedChat2.key_hash.length < 16) && encryptedChat2.auth_key != null) {
                encryptedChat2.key_hash = AndroidUtilities.calcAuthKeyHash(encryptedChat2.auth_key);
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(encryptedChat.getObjectSize());
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(encryptedChat2.a_or_b != null ? encryptedChat2.a_or_b.length : 1);
            NativeByteBuffer nativeByteBuffer3 = new NativeByteBuffer(encryptedChat2.auth_key != null ? encryptedChat2.auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer4 = new NativeByteBuffer(encryptedChat2.future_auth_key != null ? encryptedChat2.future_auth_key.length : 1);
            NativeByteBuffer nativeByteBuffer5 = new NativeByteBuffer(encryptedChat2.key_hash != null ? encryptedChat2.key_hash.length : 1);
            encryptedChat2.serializeToStream(nativeByteBuffer);
            executeFast.bindInteger(1, encryptedChat2.id);
            executeFast.bindInteger(2, user2.id);
            executeFast.bindString(3, formatUserSearchName(user2));
            executeFast.bindByteBuffer(4, nativeByteBuffer);
            if (encryptedChat2.a_or_b != null) {
                nativeByteBuffer2.writeBytes(encryptedChat2.a_or_b);
            }
            if (encryptedChat2.auth_key != null) {
                nativeByteBuffer3.writeBytes(encryptedChat2.auth_key);
            }
            if (encryptedChat2.future_auth_key != null) {
                nativeByteBuffer4.writeBytes(encryptedChat2.future_auth_key);
            }
            if (encryptedChat2.key_hash != null) {
                nativeByteBuffer5.writeBytes(encryptedChat2.key_hash);
            }
            executeFast.bindByteBuffer(5, nativeByteBuffer2);
            executeFast.bindByteBuffer(6, nativeByteBuffer3);
            executeFast.bindInteger(7, encryptedChat2.ttl);
            executeFast.bindInteger(8, encryptedChat2.layer);
            executeFast.bindInteger(9, encryptedChat2.seq_in);
            executeFast.bindInteger(10, encryptedChat2.seq_out);
            executeFast.bindInteger(11, encryptedChat2.key_use_count_out | (encryptedChat2.key_use_count_in << 16));
            executeFast.bindLong(12, encryptedChat2.exchange_id);
            executeFast.bindInteger(13, encryptedChat2.key_create_date);
            executeFast.bindLong(14, encryptedChat2.future_key_fingerprint);
            executeFast.bindByteBuffer(15, nativeByteBuffer4);
            executeFast.bindByteBuffer(16, nativeByteBuffer5);
            executeFast.bindInteger(17, encryptedChat2.in_seq_no);
            executeFast.bindInteger(18, encryptedChat2.admin_id);
            executeFast.bindInteger(19, encryptedChat2.mtproto_seq);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
            nativeByteBuffer2.reuse();
            nativeByteBuffer3.reuse();
            nativeByteBuffer4.reuse();
            nativeByteBuffer5.reuse();
            if (dialog2 != null) {
                SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                executeFast2.bindLong(1, dialog2.id);
                executeFast2.bindInteger(2, dialog2.last_message_date);
                executeFast2.bindInteger(3, dialog2.unread_count);
                executeFast2.bindInteger(4, dialog2.top_message);
                executeFast2.bindInteger(5, dialog2.read_inbox_max_id);
                executeFast2.bindInteger(6, dialog2.read_outbox_max_id);
                executeFast2.bindInteger(7, 0);
                executeFast2.bindInteger(8, dialog2.unread_mentions_count);
                executeFast2.bindInteger(9, dialog2.pts);
                executeFast2.bindInteger(10, 0);
                executeFast2.bindInteger(11, dialog2.pinnedNum);
                executeFast2.bindInteger(12, dialog2.flags);
                executeFast2.bindInteger(13, dialog2.folder_id);
                executeFast2.bindNull(14);
                executeFast2.step();
                executeFast2.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private String formatUserSearchName(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = user.first_name;
        if (str != null && str.length() > 0) {
            stringBuilder.append(user.first_name);
        }
        str = user.last_name;
        if (str != null && str.length() > 0) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(user.last_name);
        }
        stringBuilder.append(";;;");
        str = user.username;
        if (str != null && str.length() > 0) {
            stringBuilder.append(user.username);
        }
        return stringBuilder.toString().toLowerCase();
    }

    private void putUsersInternal(ArrayList<User> arrayList) throws Exception {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = (User) arrayList.get(i);
                if (tLObject.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[]{Integer.valueOf(tLObject.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLObject TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    if (tLObject.username != null) {
                                        TLdeserialize.username = tLObject.username;
                                        TLdeserialize.flags |= 8;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -9;
                                    }
                                    if (tLObject.photo != null) {
                                        TLdeserialize.photo = tLObject.photo;
                                        TLdeserialize.flags |= 32;
                                    } else {
                                        TLdeserialize.photo = null;
                                        TLdeserialize.flags &= -33;
                                    }
                                    tLObject = TLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLObject.id);
                executeFast.bindString(2, formatUserSearchName(tLObject));
                UserStatus userStatus = tLObject.status;
                if (userStatus != null) {
                    if (userStatus instanceof TL_userStatusRecently) {
                        userStatus.expires = -100;
                    } else if (userStatus instanceof TL_userStatusLastWeek) {
                        userStatus.expires = -101;
                    } else if (userStatus instanceof TL_userStatusLastMonth) {
                        userStatus.expires = -102;
                    }
                    executeFast.bindInteger(3, tLObject.status.expires);
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

    public void updateChatDefaultBannedRights(int i, TL_chatBannedRights tL_chatBannedRights, int i2) {
        if (tL_chatBannedRights != null && i != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$q8e3IRJkycqkklVOmKbVmcIbgbw(this, i, i2, tL_chatBannedRights));
        }
    }

    public /* synthetic */ void lambda$updateChatDefaultBannedRights$109$MessagesStorage(int i, int i2, TL_chatBannedRights tL_chatBannedRights) {
        Chat chat = null;
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    chat = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (chat != null) {
                if (chat.default_banned_rights == null || i2 >= chat.version) {
                    chat.default_banned_rights = tL_chatBannedRights;
                    chat.flags |= 262144;
                    chat.version = i2;
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE chats SET data = ? WHERE uid = ?");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chat.getObjectSize());
                    chat.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(1, nativeByteBuffer);
                    executeFast.bindInteger(2, chat.id);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putChatsInternal(ArrayList<Chat> arrayList) throws Exception {
        if (arrayList != null && !arrayList.isEmpty()) {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
            for (int i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = (Chat) arrayList.get(i);
                if (tLObject.min) {
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[]{Integer.valueOf(tLObject.id)}), new Object[0]);
                    if (queryFinalized.next()) {
                        try {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                TLObject TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    TLdeserialize.title = tLObject.title;
                                    TLdeserialize.photo = tLObject.photo;
                                    TLdeserialize.broadcast = tLObject.broadcast;
                                    TLdeserialize.verified = tLObject.verified;
                                    TLdeserialize.megagroup = tLObject.megagroup;
                                    if (tLObject.default_banned_rights != null) {
                                        TLdeserialize.default_banned_rights = tLObject.default_banned_rights;
                                        TLdeserialize.flags |= 262144;
                                    }
                                    if (tLObject.admin_rights != null) {
                                        TLdeserialize.admin_rights = tLObject.admin_rights;
                                        TLdeserialize.flags |= 16384;
                                    }
                                    if (tLObject.banned_rights != null) {
                                        TLdeserialize.banned_rights = tLObject.banned_rights;
                                        TLdeserialize.flags |= 32768;
                                    }
                                    if (tLObject.username != null) {
                                        TLdeserialize.username = tLObject.username;
                                        TLdeserialize.flags |= 64;
                                    } else {
                                        TLdeserialize.username = null;
                                        TLdeserialize.flags &= -65;
                                    }
                                    tLObject = TLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    queryFinalized.dispose();
                }
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                tLObject.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLObject.id);
                String str = tLObject.title;
                if (str != null) {
                    executeFast.bindString(2, str.toLowerCase());
                } else {
                    executeFast.bindString(2, "");
                }
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
        }
    }

    public void getUsersInternal(String str, ArrayList<User> arrayList) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        User TLdeserialize = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            if (TLdeserialize.status != null) {
                                TLdeserialize.status.expires = queryFinalized.intValue(1);
                            }
                            arrayList.add(TLdeserialize);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
        }
    }

    public void getChatsInternal(String str, ArrayList<Chat> arrayList) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
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
    }

    public void getEncryptedChatsInternal(String str, ArrayList<EncryptedChat> arrayList, ArrayList<Integer> arrayList2) throws Exception {
        if (str != null && str.length() != 0 && arrayList != null) {
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", new Object[]{str}), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        EncryptedChat TLdeserialize = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        byteBufferValue.reuse();
                        if (TLdeserialize != null) {
                            TLdeserialize.user_id = queryFinalized.intValue(1);
                            if (!(arrayList2 == null || arrayList2.contains(Integer.valueOf(TLdeserialize.user_id)))) {
                                arrayList2.add(Integer.valueOf(TLdeserialize.user_id));
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
                            intValue = queryFinalized.intValue(15);
                            if (intValue != 0) {
                                TLdeserialize.admin_id = intValue;
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
    }

    private void putUsersAndChatsInternal(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        putUsersInternal(arrayList);
        putChatsInternal(arrayList2);
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void putUsersAndChats(ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        if (arrayList == null || !arrayList.isEmpty() || arrayList2 == null || !arrayList2.isEmpty()) {
            if (z2) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$43l1cpCRu2AD5GNncWPj-W6jQss(this, arrayList, arrayList2, z));
            } else {
                putUsersAndChatsInternal(arrayList, arrayList2, z);
            }
        }
    }

    public /* synthetic */ void lambda$putUsersAndChats$110$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, boolean z) {
        putUsersAndChatsInternal(arrayList, arrayList2, z);
    }

    public void removeFromDownloadQueue(long j, int i, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mSG0ikw8BxLc-Rvndm9n2kBCzwE(this, z, i, j));
    }

    public /* synthetic */ void lambda$removeFromDownloadQueue$111$MessagesStorage(boolean z, int i, long j) {
        if (z) {
            try {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                queryFinalized.dispose();
                if (intValue != -1) {
                    this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[]{Integer.valueOf(intValue - 1), Long.valueOf(j), Integer.valueOf(i)})).stepThis().dispose();
                    return;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i)})).stepThis().dispose();
    }

    public void clearDownloadQueue(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$jzQZHn2jZ6v27wU-Wrzs01lTzXo(this, i));
    }

    public /* synthetic */ void lambda$clearDownloadQueue$112$MessagesStorage(int i) {
        if (i == 0) {
            try {
                this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[]{Integer.valueOf(i)})).stepThis().dispose();
    }

    public void getDownloadQueue(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$9n3v8Fq21ITuCZdQBgDiWwqN8gs(this, i));
    }

    public /* synthetic */ void lambda$getDownloadQueue$114$MessagesStorage(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data, parent FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[]{Integer.valueOf(i)}), new Object[0]);
            while (queryFinalized.next()) {
                DownloadObject downloadObject = new DownloadObject();
                downloadObject.type = queryFinalized.intValue(1);
                downloadObject.id = queryFinalized.longValue(0);
                downloadObject.parent = queryFinalized.stringValue(3);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(2);
                if (byteBufferValue != null) {
                    MessageMedia TLdeserialize = MessageMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                    if (TLdeserialize.document != null) {
                        downloadObject.object = TLdeserialize.document;
                    } else if (TLdeserialize.photo != null) {
                        downloadObject.object = TLdeserialize.photo;
                    }
                    boolean z = TLdeserialize.ttl_seconds > 0 && TLdeserialize.ttl_seconds <= 60;
                    downloadObject.secret = z;
                    downloadObject.forceCache = (TLdeserialize.flags & Integer.MIN_VALUE) != 0;
                }
                arrayList.add(downloadObject);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$Gr96UcRkWeFDbifkO2SHN3-XHfA(this, i, arrayList));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$113$MessagesStorage(int i, ArrayList arrayList) {
        getDownloadController().processDownloadObjects(i, arrayList);
    }

    /* JADX WARNING: Missing block: B:9:0x0018, code skipped:
            if (r0 <= 60) goto L_0x003a;
     */
    private int getMessageMediaType(org.telegram.tgnet.TLRPC.Message r5) {
        /*
        r4 = this;
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x003b;
    L_0x0006:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0012;
    L_0x000c:
        r0 = org.telegram.messenger.MessageObject.isGifMessage(r5);
        if (r0 == 0) goto L_0x001a;
    L_0x0012:
        r0 = r5.ttl;
        if (r0 <= 0) goto L_0x001a;
    L_0x0016:
        r3 = 60;
        if (r0 <= r3) goto L_0x003a;
    L_0x001a:
        r0 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);
        if (r0 != 0) goto L_0x003a;
    L_0x0020:
        r0 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r0 != 0) goto L_0x003a;
    L_0x0026:
        r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);
        if (r0 == 0) goto L_0x002d;
    L_0x002c:
        goto L_0x003a;
    L_0x002d:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0039;
    L_0x0033:
        r5 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r5 == 0) goto L_0x005d;
    L_0x0039:
        return r2;
    L_0x003a:
        return r1;
    L_0x003b:
        r0 = r5 instanceof org.telegram.tgnet.TLRPC.TL_message;
        if (r0 == 0) goto L_0x0050;
    L_0x003f:
        r0 = r5.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 != 0) goto L_0x0049;
    L_0x0045:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x0050;
    L_0x0049:
        r0 = r5.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x0050;
    L_0x004f:
        return r1;
    L_0x0050:
        r0 = r5.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x005f;
    L_0x0056:
        r5 = org.telegram.messenger.MessageObject.isVideoMessage(r5);
        if (r5 == 0) goto L_0x005d;
    L_0x005c:
        goto L_0x005f;
    L_0x005d:
        r5 = -1;
        return r5;
    L_0x005f:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessageMediaType(org.telegram.tgnet.TLRPC$Message):int");
    }

    public void putWebPages(LongSparseArray<WebPage> longSparseArray) {
        if (!isEmpty((LongSparseArray) longSparseArray)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ZCLASSNAMEBg__blruiuNfZet6xS5RP-A(this, longSparseArray));
        }
    }

    public /* synthetic */ void lambda$putWebPages$116$MessagesStorage(LongSparseArray longSparseArray) {
        try {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT mid FROM webpage_pending WHERE id = ");
                stringBuilder.append(longSparseArray.keyAt(i2));
                SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
                ArrayList arrayList2 = new ArrayList();
                while (queryFinalized.next()) {
                    arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                }
                queryFinalized.dispose();
                if (!arrayList2.isEmpty()) {
                    queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                    while (queryFinalized.next()) {
                        int intValue = queryFinalized.intValue(0);
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
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
                this.database.beginTransaction();
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
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
                this.database.commitTransaction();
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$BWvcTxjLTEmmf8bv5a97fVBp6sQ(this, arrayList));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$115$MessagesStorage(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(int i, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$FcyfVSniBlbpWaGZXrc5XL_EKhE(this, i, i2, tL_updates_channelDifferenceTooLong));
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0115 A:{Catch:{ Exception -> 0x0146 }} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0113 A:{Catch:{ Exception -> 0x0146 }} */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0134 A:{Catch:{ Exception -> 0x0146 }} */
    public /* synthetic */ void lambda$overwriteChannel$118$MessagesStorage(int r11, int r12, org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong r13) {
        /*
        r10 = this;
        r0 = -r11;
        r0 = (long) r0;
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r3.<init>();	 Catch:{ Exception -> 0x0146 }
        r4 = "SELECT pinned FROM dialogs WHERE did = ";
        r3.append(r4);	 Catch:{ Exception -> 0x0146 }
        r3.append(r0);	 Catch:{ Exception -> 0x0146 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0146 }
        r4 = 0;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0146 }
        r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0146 }
        r3 = r2.next();	 Catch:{ Exception -> 0x0146 }
        r5 = 1;
        if (r3 != 0) goto L_0x002b;
    L_0x0023:
        if (r12 == 0) goto L_0x0028;
    L_0x0025:
        r3 = 0;
        r6 = 1;
        goto L_0x0030;
    L_0x0028:
        r3 = 0;
    L_0x0029:
        r6 = 0;
        goto L_0x0030;
    L_0x002b:
        r3 = r2.intValue(r4);	 Catch:{ Exception -> 0x0146 }
        goto L_0x0029;
    L_0x0030:
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "DELETE FROM messages WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "DELETE FROM bot_keyboard WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "UPDATE media_counts_v2 SET old = 1 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "DELETE FROM media_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "DELETE FROM messages_holes WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0146 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0146 }
        r7.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0146 }
        r7.append(r0);	 Catch:{ Exception -> 0x0146 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0146 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0146 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0146 }
        r2.dispose();	 Catch:{ Exception -> 0x0146 }
        r2 = r10.getMediaDataController();	 Catch:{ Exception -> 0x0146 }
        r7 = 0;
        r2.clearBotKeyboard(r0, r7);	 Catch:{ Exception -> 0x0146 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;	 Catch:{ Exception -> 0x0146 }
        r2.<init>();	 Catch:{ Exception -> 0x0146 }
        r8 = r2.chats;	 Catch:{ Exception -> 0x0146 }
        r9 = r13.chats;	 Catch:{ Exception -> 0x0146 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0146 }
        r8 = r2.users;	 Catch:{ Exception -> 0x0146 }
        r9 = r13.users;	 Catch:{ Exception -> 0x0146 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0146 }
        r8 = r2.messages;	 Catch:{ Exception -> 0x0146 }
        r9 = r13.messages;	 Catch:{ Exception -> 0x0146 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0146 }
        r13 = r13.dialog;	 Catch:{ Exception -> 0x0146 }
        r13.id = r0;	 Catch:{ Exception -> 0x0146 }
        r13.flags = r5;	 Catch:{ Exception -> 0x0146 }
        r13.notify_settings = r7;	 Catch:{ Exception -> 0x0146 }
        if (r3 == 0) goto L_0x0115;
    L_0x0113:
        r8 = 1;
        goto L_0x0116;
    L_0x0115:
        r8 = 0;
    L_0x0116:
        r13.pinned = r8;	 Catch:{ Exception -> 0x0146 }
        r13.pinnedNum = r3;	 Catch:{ Exception -> 0x0146 }
        r3 = r2.dialogs;	 Catch:{ Exception -> 0x0146 }
        r3.add(r13);	 Catch:{ Exception -> 0x0146 }
        r10.putDialogsInternal(r2, r4);	 Catch:{ Exception -> 0x0146 }
        r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0146 }
        r13.<init>();	 Catch:{ Exception -> 0x0146 }
        r10.updateDialogsWithDeletedMessages(r13, r7, r4, r11);	 Catch:{ Exception -> 0x0146 }
        r13 = new org.telegram.messenger.-$$Lambda$MessagesStorage$Ukt2MAzbTE2JKV5RgnN4f2A92KY;	 Catch:{ Exception -> 0x0146 }
        r13.<init>(r10, r0);	 Catch:{ Exception -> 0x0146 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);	 Catch:{ Exception -> 0x0146 }
        if (r6 == 0) goto L_0x014a;
    L_0x0134:
        if (r12 != r5) goto L_0x013e;
    L_0x0136:
        r12 = r10.getMessagesController();	 Catch:{ Exception -> 0x0146 }
        r12.checkChannelInviter(r11);	 Catch:{ Exception -> 0x0146 }
        goto L_0x014a;
    L_0x013e:
        r12 = r10.getMessagesController();	 Catch:{ Exception -> 0x0146 }
        r12.generateJoinMessage(r11, r4);	 Catch:{ Exception -> 0x0146 }
        goto L_0x014a;
    L_0x0146:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
    L_0x014a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$118$MessagesStorage(int, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong):void");
    }

    public /* synthetic */ void lambda$null$117$MessagesStorage(long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(true));
    }

    public void putChannelViews(SparseArray<SparseIntArray> sparseArray, boolean z) {
        if (!isEmpty((SparseArray) sparseArray)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_6d_mfmmRnodELRFw_V86A87quA(this, sparseArray, z));
        }
    }

    public /* synthetic */ void lambda$putChannelViews$119$MessagesStorage(SparseArray sparseArray, boolean z) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
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
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isValidKeyboardToSave(Message message) {
        ReplyMarkup replyMarkup = message.reply_markup;
        return (replyMarkup == null || (replyMarkup instanceof TL_replyInlineMarkup) || (replyMarkup.selective && !message.mentioned)) ? false : true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b9 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x017c A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x01d7 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01c2 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05a8 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0596 A:{Catch:{ Exception -> 0x0045 }} */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x062f A:{Catch:{ Exception -> 0x0045 }} */
    private void putMessagesInternal(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r31, boolean r32, boolean r33, int r34, boolean r35) {
        /*
        r30 = this;
        r1 = r30;
        r2 = r31;
        r4 = 0;
        r6 = 0;
        if (r35 == 0) goto L_0x0049;
    L_0x0009:
        r7 = r2.get(r6);	 Catch:{ Exception -> 0x0045 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x0045 }
        r8 = r7.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r10 != 0) goto L_0x0018;
    L_0x0015:
        org.telegram.messenger.MessageObject.getDialogId(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x0018:
        r8 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = "SELECT last_mid FROM dialogs WHERE did = ";
        r9.append(r10);	 Catch:{ Exception -> 0x0045 }
        r10 = r7.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r9.append(r10);	 Catch:{ Exception -> 0x0045 }
        r7 = r9.toString();	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0045 }
        r7 = r8.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0045 }
        r8 = r7.next();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x003e;
    L_0x0039:
        r8 = r7.intValue(r6);	 Catch:{ Exception -> 0x0045 }
        goto L_0x003f;
    L_0x003e:
        r8 = -1;
    L_0x003f:
        r7.dispose();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x0049;
    L_0x0044:
        return;
    L_0x0045:
        r0 = move-exception;
    L_0x0046:
        r2 = r0;
        goto L_0x0951;
    L_0x0049:
        if (r32 == 0) goto L_0x0050;
    L_0x004b:
        r7 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7.beginTransaction();	 Catch:{ Exception -> 0x0045 }
    L_0x0050:
        r7 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r7.<init>();	 Catch:{ Exception -> 0x0045 }
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r8.<init>();	 Catch:{ Exception -> 0x0045 }
        r9 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r10.<init>();	 Catch:{ Exception -> 0x0045 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r11.<init>();	 Catch:{ Exception -> 0x0045 }
        r12 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r12.<init>();	 Catch:{ Exception -> 0x0045 }
        r13 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r13.<init>();	 Catch:{ Exception -> 0x0045 }
        r14 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r14.<init>();	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r3 = r15.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r6 = "REPLACE INTO randoms VALUES(?, ?)";
        r6 = r15.executeFast(r6);	 Catch:{ Exception -> 0x0045 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = "REPLACE INTO download_queue VALUES(?, ?, ?, ?, ?)";
        r4 = r15.executeFast(r4);	 Catch:{ Exception -> 0x0045 }
        r5 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r15 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r5 = r5.executeFast(r15);	 Catch:{ Exception -> 0x0045 }
        r20 = r4;
        r21 = r5;
        r22 = r6;
        r4 = 0;
        r5 = 0;
        r15 = 0;
        r19 = 0;
    L_0x00a3:
        r6 = r31.size();	 Catch:{ Exception -> 0x0045 }
        r23 = 32;
        if (r15 >= r6) goto L_0x01e6;
    L_0x00ab:
        r6 = r2.get(r15);	 Catch:{ Exception -> 0x0045 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x0045 }
        r24 = r7;
        r7 = r6.id;	 Catch:{ Exception -> 0x0045 }
        r25 = r3;
        r2 = (long) r7;	 Catch:{ Exception -> 0x0045 }
        r26 = r8;
        r7 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r17 = 0;
        r27 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1));
        if (r27 != 0) goto L_0x00c5;
    L_0x00c2:
        org.telegram.messenger.MessageObject.getDialogId(r6);	 Catch:{ Exception -> 0x0045 }
    L_0x00c5:
        r7 = r6.to_id;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00d3;
    L_0x00cb:
        r7 = r6.to_id;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x0045 }
        r7 = (long) r7;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 << r23;
        r2 = r2 | r7;
    L_0x00d3:
        r7 = r6.mentioned;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00d7:
        r7 = r6.media_unread;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00db:
        r7 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r14.put(r2, r7);	 Catch:{ Exception -> 0x0045 }
    L_0x00e4:
        r7 = r6.action;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x0045 }
        r8 = ",";
        if (r7 != 0) goto L_0x016e;
    L_0x00ec:
        r7 = org.telegram.messenger.MessageObject.isOut(r6);	 Catch:{ Exception -> 0x0045 }
        if (r7 != 0) goto L_0x016e;
    L_0x00f2:
        r7 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r7 > 0) goto L_0x00fc;
    L_0x00f6:
        r7 = org.telegram.messenger.MessageObject.isUnread(r6);	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x016e;
    L_0x00fc:
        r7 = r4;
        r27 = r5;
        r4 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r4 = r12.get(r4);	 Catch:{ Exception -> 0x0045 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0045 }
        if (r4 != 0) goto L_0x0147;
    L_0x0109:
        r4 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r5.<init>();	 Catch:{ Exception -> 0x0045 }
        r28 = r9;
        r9 = "SELECT inbox_max FROM dialogs WHERE did = ";
        r5.append(r9);	 Catch:{ Exception -> 0x0045 }
        r9 = r14;
        r29 = r15;
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r5.append(r14);	 Catch:{ Exception -> 0x0045 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0045 }
        r14 = 0;
        r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0045 }
        r4 = r4.queryFinalized(r5, r15);	 Catch:{ Exception -> 0x0045 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0045 }
        if (r5 == 0) goto L_0x0139;
    L_0x0130:
        r5 = r4.intValue(r14);	 Catch:{ Exception -> 0x0045 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0045 }
        goto L_0x013d;
    L_0x0139:
        r5 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0045 }
    L_0x013d:
        r4.dispose();	 Catch:{ Exception -> 0x0045 }
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r12.put(r14, r5);	 Catch:{ Exception -> 0x0045 }
        r4 = r5;
        goto L_0x014c;
    L_0x0147:
        r28 = r9;
        r9 = r14;
        r29 = r15;
    L_0x014c:
        r5 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r5 < 0) goto L_0x0158;
    L_0x0150:
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0045 }
        r5 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r4 >= r5) goto L_0x0176;
    L_0x0158:
        r4 = r11.length();	 Catch:{ Exception -> 0x0045 }
        if (r4 <= 0) goto L_0x0161;
    L_0x015e:
        r11.append(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0161:
        r11.append(r2);	 Catch:{ Exception -> 0x0045 }
        r4 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x0045 }
        r13.put(r2, r4);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0176;
    L_0x016e:
        r7 = r4;
        r27 = r5;
        r28 = r9;
        r9 = r14;
        r29 = r15;
    L_0x0176:
        r4 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r6);	 Catch:{ Exception -> 0x0045 }
        if (r4 == 0) goto L_0x01b9;
    L_0x017c:
        if (r19 != 0) goto L_0x0191;
    L_0x017e:
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r19.<init>();	 Catch:{ Exception -> 0x0045 }
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r5 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r5.<init>();	 Catch:{ Exception -> 0x0045 }
        r7 = r4;
        r4 = r19;
        goto L_0x0195;
    L_0x0191:
        r4 = r19;
        r5 = r27;
    L_0x0195:
        r14 = r4.length();	 Catch:{ Exception -> 0x0045 }
        if (r14 <= 0) goto L_0x019e;
    L_0x019b:
        r4.append(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x019e:
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r14 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x0045 }
        r7.put(r2, r8);	 Catch:{ Exception -> 0x0045 }
        r8 = org.telegram.messenger.MediaDataController.getMediaType(r6);	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r5.put(r2, r8);	 Catch:{ Exception -> 0x0045 }
        r19 = r4;
        r4 = r7;
        goto L_0x01bc;
    L_0x01b9:
        r4 = r7;
        r5 = r27;
    L_0x01bc:
        r2 = r1.isValidKeyboardToSave(r6);	 Catch:{ Exception -> 0x0045 }
        if (r2 == 0) goto L_0x01d7;
    L_0x01c2:
        r2 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r2 = r10.get(r2);	 Catch:{ Exception -> 0x0045 }
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;	 Catch:{ Exception -> 0x0045 }
        if (r2 == 0) goto L_0x01d2;
    L_0x01cc:
        r2 = r2.id;	 Catch:{ Exception -> 0x0045 }
        r3 = r6.id;	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x01d7;
    L_0x01d2:
        r2 = r6.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10.put(r2, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x01d7:
        r15 = r29 + 1;
        r2 = r31;
        r14 = r9;
        r7 = r24;
        r3 = r25;
        r8 = r26;
        r9 = r28;
        goto L_0x00a3;
    L_0x01e6:
        r25 = r3;
        r27 = r5;
        r24 = r7;
        r26 = r8;
        r28 = r9;
        r9 = r14;
        r7 = r4;
        r2 = 0;
    L_0x01f3:
        r3 = r10.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x020d;
    L_0x01f9:
        r3 = r30.getMediaDataController();	 Catch:{ Exception -> 0x0045 }
        r4 = r10.keyAt(r2);	 Catch:{ Exception -> 0x0045 }
        r6 = r10.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x0045 }
        r3.putBotKeyboard(r4, r6);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        goto L_0x01f3;
    L_0x020d:
        r2 = ")";
        r6 = 1;
        if (r19 == 0) goto L_0x031b;
    L_0x0212:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r5 = "SELECT mid, type FROM media_v2 WHERE mid IN(";
        r4.append(r5);	 Catch:{ Exception -> 0x0045 }
        r5 = r19.toString();	 Catch:{ Exception -> 0x0045 }
        r4.append(r5);	 Catch:{ Exception -> 0x0045 }
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0045 }
        r5 = 0;
        r8 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0045 }
        r3 = r3.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
    L_0x0234:
        r8 = r3.next();	 Catch:{ Exception -> 0x0045 }
        if (r8 == 0) goto L_0x0266;
    L_0x023a:
        r14 = r3.longValue(r5);	 Catch:{ Exception -> 0x0045 }
        r5 = r3.intValue(r6);	 Catch:{ Exception -> 0x0045 }
        r8 = r27;
        r10 = r8.get(r14);	 Catch:{ Exception -> 0x0045 }
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x0045 }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x0045 }
        if (r5 != r10) goto L_0x0254;
    L_0x0250:
        r7.remove(r14);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0262;
    L_0x0254:
        if (r4 != 0) goto L_0x025b;
    L_0x0256:
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
    L_0x025b:
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0045 }
        r4.put(r14, r5);	 Catch:{ Exception -> 0x0045 }
    L_0x0262:
        r27 = r8;
        r5 = 0;
        goto L_0x0234;
    L_0x0266:
        r8 = r27;
        r3.dispose();	 Catch:{ Exception -> 0x0045 }
        r15 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r3 = 0;
    L_0x0271:
        r5 = r7.size();	 Catch:{ Exception -> 0x0045 }
        if (r3 >= r5) goto L_0x0319;
    L_0x0277:
        r5 = r9;
        r9 = r7.keyAt(r3);	 Catch:{ Exception -> 0x0045 }
        r12 = r7.valueAt(r3);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Long) r12;	 Catch:{ Exception -> 0x0045 }
        r14 = r7;
        r6 = r12.longValue();	 Catch:{ Exception -> 0x0045 }
        r12 = r8.get(r9);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
        r27 = r8;
        r8 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r8 = r15.get(r8);	 Catch:{ Exception -> 0x0045 }
        r8 = (android.util.LongSparseArray) r8;	 Catch:{ Exception -> 0x0045 }
        if (r8 != 0) goto L_0x02ae;
    L_0x029b:
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r8.<init>();	 Catch:{ Exception -> 0x0045 }
        r16 = 0;
        r29 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0045 }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r15.put(r12, r8);	 Catch:{ Exception -> 0x0045 }
        goto L_0x02b6;
    L_0x02ae:
        r12 = r8.get(r6);	 Catch:{ Exception -> 0x0045 }
        r29 = r12;
        r29 = (java.lang.Integer) r29;	 Catch:{ Exception -> 0x0045 }
    L_0x02b6:
        if (r29 != 0) goto L_0x02bd;
    L_0x02b8:
        r12 = 0;
        r29 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0045 }
    L_0x02bd:
        r12 = r29.intValue();	 Catch:{ Exception -> 0x0045 }
        r19 = 1;
        r12 = r12 + 1;
        r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x0045 }
        r8.put(r6, r12);	 Catch:{ Exception -> 0x0045 }
        if (r4 == 0) goto L_0x0310;
    L_0x02ce:
        r8 = -1;
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r8 = r4.get(r9, r12);	 Catch:{ Exception -> 0x0045 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Exception -> 0x0045 }
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0045 }
        if (r8 < 0) goto L_0x0310;
    L_0x02df:
        r9 = r15.get(r8);	 Catch:{ Exception -> 0x0045 }
        r9 = (android.util.LongSparseArray) r9;	 Catch:{ Exception -> 0x0045 }
        if (r9 != 0) goto L_0x02f5;
    L_0x02e7:
        r9 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0045 }
        r9.<init>();	 Catch:{ Exception -> 0x0045 }
        r10 = 0;
        r12 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0045 }
        r15.put(r8, r9);	 Catch:{ Exception -> 0x0045 }
        goto L_0x02fc;
    L_0x02f5:
        r8 = r9.get(r6);	 Catch:{ Exception -> 0x0045 }
        r12 = r8;
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
    L_0x02fc:
        if (r12 != 0) goto L_0x0303;
    L_0x02fe:
        r8 = 0;
        r12 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0303:
        r8 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r10 = 1;
        r8 = r8 - r10;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r9.put(r6, r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0310:
        r3 = r3 + 1;
        r9 = r5;
        r7 = r14;
        r8 = r27;
        r6 = 1;
        goto L_0x0271;
    L_0x0319:
        r5 = r9;
        goto L_0x031d;
    L_0x031b:
        r5 = r9;
        r15 = 0;
    L_0x031d:
        r3 = r11.length();	 Catch:{ Exception -> 0x0045 }
        if (r3 <= 0) goto L_0x03bf;
    L_0x0323:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r4.<init>();	 Catch:{ Exception -> 0x0045 }
        r6 = "SELECT mid FROM messages WHERE mid IN(";
        r4.append(r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.toString();	 Catch:{ Exception -> 0x0045 }
        r4.append(r6);	 Catch:{ Exception -> 0x0045 }
        r4.append(r2);	 Catch:{ Exception -> 0x0045 }
        r2 = r4.toString();	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
        r6 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0045 }
        r2 = r3.queryFinalized(r2, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x0344:
        r3 = r2.next();	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0356;
    L_0x034a:
        r6 = r2.longValue(r4);	 Catch:{ Exception -> 0x0045 }
        r13.remove(r6);	 Catch:{ Exception -> 0x0045 }
        r5.remove(r6);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
        goto L_0x0344;
    L_0x0356:
        r2.dispose();	 Catch:{ Exception -> 0x0045 }
        r2 = 0;
    L_0x035a:
        r3 = r13.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x038b;
    L_0x0360:
        r3 = r13.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r3 = (java.lang.Long) r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.longValue();	 Catch:{ Exception -> 0x0045 }
        r6 = r26;
        r7 = r6.get(r3);	 Catch:{ Exception -> 0x0045 }
        r7 = (java.lang.Integer) r7;	 Catch:{ Exception -> 0x0045 }
        if (r7 != 0) goto L_0x0379;
    L_0x0374:
        r8 = 0;
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
    L_0x0379:
        r7 = r7.intValue();	 Catch:{ Exception -> 0x0045 }
        r8 = 1;
        r7 = r7 + r8;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r6.put(r3, r7);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        r26 = r6;
        goto L_0x035a;
    L_0x038b:
        r6 = r26;
        r2 = 0;
    L_0x038e:
        r3 = r5.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r3) goto L_0x03c1;
    L_0x0394:
        r3 = r5.valueAt(r2);	 Catch:{ Exception -> 0x0045 }
        r3 = (java.lang.Long) r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.longValue();	 Catch:{ Exception -> 0x0045 }
        r7 = r28;
        r8 = r7.get(r3);	 Catch:{ Exception -> 0x0045 }
        r8 = (java.lang.Integer) r8;	 Catch:{ Exception -> 0x0045 }
        if (r8 != 0) goto L_0x03ad;
    L_0x03a8:
        r9 = 0;
        r8 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0045 }
    L_0x03ad:
        r8 = r8.intValue();	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r8 = r8 + r9;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0045 }
        r7.put(r3, r8);	 Catch:{ Exception -> 0x0045 }
        r2 = r2 + 1;
        r28 = r7;
        goto L_0x038e;
    L_0x03bf:
        r6 = r26;
    L_0x03c1:
        r7 = r28;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
    L_0x03c7:
        r8 = r31.size();	 Catch:{ Exception -> 0x0045 }
        if (r2 >= r8) goto L_0x06ad;
    L_0x03cd:
        r8 = r31;
        r27 = r8.get(r2);	 Catch:{ Exception -> 0x0045 }
        r11 = r27;
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x0045 }
        r1.fixUnsupportedMedia(r11);	 Catch:{ Exception -> 0x0045 }
        r25.requery();	 Catch:{ Exception -> 0x0045 }
        r9 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r29 = r15;
        r14 = (long) r9;	 Catch:{ Exception -> 0x0045 }
        r9 = r11.local_id;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x03e9;
    L_0x03e6:
        r9 = r11.local_id;	 Catch:{ Exception -> 0x0045 }
        r14 = (long) r9;	 Catch:{ Exception -> 0x0045 }
    L_0x03e9:
        r9 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x03f7;
    L_0x03ef:
        r9 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        r12 = (long) r9;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 << r23;
        r14 = r14 | r12;
    L_0x03f7:
        r9 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0045 }
        r12 = r11.getObjectSize();	 Catch:{ Exception -> 0x0045 }
        r9.<init>(r12);	 Catch:{ Exception -> 0x0045 }
        r11.serializeToStream(r9);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x041b;
    L_0x0409:
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x041b;
    L_0x0411:
        r12 = r11.action;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0045 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x041b;
    L_0x0419:
        r12 = 0;
        goto L_0x041c;
    L_0x041b:
        r12 = 1;
    L_0x041c:
        if (r12 == 0) goto L_0x044a;
    L_0x041e:
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10 = r24;
        r12 = r10.get(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = (org.telegram.tgnet.TLRPC.Message) r12;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x0444;
    L_0x042a:
        r13 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r8 = r12.date;	 Catch:{ Exception -> 0x0045 }
        if (r13 > r8) goto L_0x0444;
    L_0x0430:
        r8 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 <= 0) goto L_0x043a;
    L_0x0434:
        r8 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r13 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 > r13) goto L_0x0444;
    L_0x043a:
        r8 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 >= 0) goto L_0x044c;
    L_0x043e:
        r8 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.id;	 Catch:{ Exception -> 0x0045 }
        if (r8 >= r12) goto L_0x044c;
    L_0x0444:
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r10.put(r12, r11);	 Catch:{ Exception -> 0x0045 }
        goto L_0x044c;
    L_0x044a:
        r10 = r24;
    L_0x044c:
        r8 = r25;
        r12 = 1;
        r8.bindLong(r12, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r24 = r6;
        r6 = 2;
        r8.bindLong(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = org.telegram.messenger.MessageObject.getUnreadFlags(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.send_state;	 Catch:{ Exception -> 0x0045 }
        r12 = 4;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 5;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = 6;
        r8.bindByteBuffer(r6, r9);	 Catch:{ Exception -> 0x0045 }
        r6 = 7;
        r12 = org.telegram.messenger.MessageObject.isOut(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x047b;
    L_0x0479:
        r12 = 1;
        goto L_0x047c;
    L_0x047b:
        r12 = 0;
    L_0x047c:
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.ttl;	 Catch:{ Exception -> 0x0045 }
        r12 = 8;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        r6 = r11.flags;	 Catch:{ Exception -> 0x0045 }
        r6 = r6 & 1024;
        if (r6 == 0) goto L_0x0494;
    L_0x048c:
        r6 = r11.views;	 Catch:{ Exception -> 0x0045 }
        r12 = 9;
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
        goto L_0x049d;
    L_0x0494:
        r12 = 9;
        r6 = r1.getMessageMediaType(r11);	 Catch:{ Exception -> 0x0045 }
        r8.bindInteger(r12, r6);	 Catch:{ Exception -> 0x0045 }
    L_0x049d:
        r6 = 10;
        r12 = 0;
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r6 = 11;
        r12 = r11.mentioned;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x04ab;
    L_0x04a9:
        r12 = 1;
        goto L_0x04ac;
    L_0x04ab:
        r12 = 0;
    L_0x04ac:
        r8.bindInteger(r6, r12);	 Catch:{ Exception -> 0x0045 }
        r8.step();	 Catch:{ Exception -> 0x0045 }
        r12 = r11.random_id;	 Catch:{ Exception -> 0x0045 }
        r17 = 0;
        r6 = (r12 > r17 ? 1 : (r12 == r17 ? 0 : -1));
        if (r6 == 0) goto L_0x04cf;
    L_0x04ba:
        r22.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = r11.random_id;	 Catch:{ Exception -> 0x0045 }
        r6 = r22;
        r22 = r7;
        r7 = 1;
        r6.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r6.bindLong(r7, r14);	 Catch:{ Exception -> 0x0045 }
        r6.step();	 Catch:{ Exception -> 0x0045 }
        goto L_0x04d3;
    L_0x04cf:
        r6 = r22;
        r22 = r7;
    L_0x04d3:
        r7 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r11);	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x0505;
    L_0x04d9:
        if (r3 != 0) goto L_0x04e3;
    L_0x04db:
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r3 = r3.executeFast(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x04e3:
        r3.requery();	 Catch:{ Exception -> 0x0045 }
        r7 = 1;
        r3.bindLong(r7, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.dialog_id;	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r3.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r7 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r3.bindInteger(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = org.telegram.messenger.MediaDataController.getMediaType(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = 4;
        r3.bindInteger(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = 5;
        r3.bindByteBuffer(r7, r9);	 Catch:{ Exception -> 0x0045 }
        r3.step();	 Catch:{ Exception -> 0x0045 }
    L_0x0505:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x0530;
    L_0x050b:
        if (r4 != 0) goto L_0x0515;
    L_0x050d:
        r4 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r7 = "REPLACE INTO polls VALUES(?, ?)";
        r4 = r4.executeFast(r7);	 Catch:{ Exception -> 0x0045 }
    L_0x0515:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r7;	 Catch:{ Exception -> 0x0045 }
        r4.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = 1;
        r4.bindLong(r12, r14);	 Catch:{ Exception -> 0x0045 }
        r7 = r7.poll;	 Catch:{ Exception -> 0x0045 }
        r12 = r7.id;	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r4.bindLong(r7, r12);	 Catch:{ Exception -> 0x0045 }
        r4.step();	 Catch:{ Exception -> 0x0045 }
    L_0x052b:
        r7 = r21;
        r21 = r3;
        goto L_0x054e;
    L_0x0530:
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r7 == 0) goto L_0x052b;
    L_0x0536:
        r21.requery();	 Catch:{ Exception -> 0x0045 }
        r7 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r7 = r7.webpage;	 Catch:{ Exception -> 0x0045 }
        r12 = r7.id;	 Catch:{ Exception -> 0x0045 }
        r7 = r21;
        r21 = r3;
        r3 = 1;
        r7.bindLong(r3, r12);	 Catch:{ Exception -> 0x0045 }
        r3 = 2;
        r7.bindLong(r3, r14);	 Catch:{ Exception -> 0x0045 }
        r7.step();	 Catch:{ Exception -> 0x0045 }
    L_0x054e:
        r9.reuse();	 Catch:{ Exception -> 0x0045 }
        if (r34 == 0) goto L_0x0695;
    L_0x0553:
        r3 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x055d;
    L_0x0559:
        r3 = r11.post;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0695;
    L_0x055d:
        r3 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r9 = r30.getConnectionsManager();	 Catch:{ Exception -> 0x0045 }
        r9 = r9.getCurrentTime();	 Catch:{ Exception -> 0x0045 }
        r9 = r9 + -3600;
        if (r3 < r9) goto L_0x0695;
    L_0x056b:
        r3 = r30.getDownloadController();	 Catch:{ Exception -> 0x0045 }
        r3 = r3.canDownloadMedia(r11);	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        if (r3 != r9) goto L_0x0695;
    L_0x0576:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0045 }
        if (r3 != 0) goto L_0x0588;
    L_0x057c:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        if (r3 != 0) goto L_0x0588;
    L_0x0582:
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0695;
    L_0x0588:
        r3 = org.telegram.messenger.MessageObject.getDocument(r11);	 Catch:{ Exception -> 0x0045 }
        r9 = org.telegram.messenger.MessageObject.getPhoto(r11);	 Catch:{ Exception -> 0x0045 }
        r12 = org.telegram.messenger.MessageObject.isVoiceMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05a8;
    L_0x0596:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 2;
        goto L_0x062d;
    L_0x05a8:
        r12 = org.telegram.messenger.MessageObject.isStickerMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x061d;
    L_0x05ae:
        r12 = org.telegram.messenger.MessageObject.isAnimatedStickerMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05b5;
    L_0x05b4:
        goto L_0x061d;
    L_0x05b5:
        r12 = org.telegram.messenger.MessageObject.isVideoMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x060c;
    L_0x05bb:
        r12 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 != 0) goto L_0x060c;
    L_0x05c1:
        r12 = org.telegram.messenger.MessageObject.isGifMessage(r11);	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x05c8;
    L_0x05c7:
        goto L_0x060c;
    L_0x05c8:
        if (r3 == 0) goto L_0x05dc;
    L_0x05ca:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 8;
        goto L_0x062d;
    L_0x05dc:
        if (r9 == 0) goto L_0x0607;
    L_0x05de:
        r3 = r9.sizes;	 Catch:{ Exception -> 0x0045 }
        r12 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0045 }
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r12);	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x0607;
    L_0x05ea:
        r12 = r9.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.photo = r9;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x062c;
    L_0x05ff:
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        goto L_0x062c;
    L_0x0607:
        r3 = 0;
        r12 = 0;
        r15 = 0;
        goto L_0x062d;
    L_0x060c:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = 4;
        goto L_0x062d;
    L_0x061d:
        r12 = r3.id;	 Catch:{ Exception -> 0x0045 }
        r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0045 }
        r15.<init>();	 Catch:{ Exception -> 0x0045 }
        r15.document = r3;	 Catch:{ Exception -> 0x0045 }
        r3 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r3 = r3 | r9;
        r15.flags = r3;	 Catch:{ Exception -> 0x0045 }
    L_0x062c:
        r3 = 1;
    L_0x062d:
        if (r15 == 0) goto L_0x0695;
    L_0x062f:
        r9 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.ttl_seconds;	 Catch:{ Exception -> 0x0045 }
        if (r9 == 0) goto L_0x0641;
    L_0x0635:
        r9 = r11.media;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.ttl_seconds;	 Catch:{ Exception -> 0x0045 }
        r15.ttl_seconds = r9;	 Catch:{ Exception -> 0x0045 }
        r9 = r15.flags;	 Catch:{ Exception -> 0x0045 }
        r14 = 4;
        r9 = r9 | r14;
        r15.flags = r9;	 Catch:{ Exception -> 0x0045 }
    L_0x0641:
        r5 = r5 | r3;
        r20.requery();	 Catch:{ Exception -> 0x0045 }
        r9 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0045 }
        r14 = r15.getObjectSize();	 Catch:{ Exception -> 0x0045 }
        r9.<init>(r14);	 Catch:{ Exception -> 0x0045 }
        r15.serializeToStream(r9);	 Catch:{ Exception -> 0x0045 }
        r14 = r20;
        r15 = 1;
        r14.bindLong(r15, r12);	 Catch:{ Exception -> 0x0045 }
        r12 = 2;
        r14.bindInteger(r12, r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r11.date;	 Catch:{ Exception -> 0x0045 }
        r12 = 3;
        r14.bindInteger(r12, r3);	 Catch:{ Exception -> 0x0045 }
        r3 = 4;
        r14.bindByteBuffer(r3, r9);	 Catch:{ Exception -> 0x0045 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r3.<init>();	 Catch:{ Exception -> 0x0045 }
        r12 = "sent_";
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        if (r12 == 0) goto L_0x0678;
    L_0x0673:
        r12 = r11.to_id;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.channel_id;	 Catch:{ Exception -> 0x0045 }
        goto L_0x0679;
    L_0x0678:
        r12 = 0;
    L_0x0679:
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r12 = "_";
        r3.append(r12);	 Catch:{ Exception -> 0x0045 }
        r11 = r11.id;	 Catch:{ Exception -> 0x0045 }
        r3.append(r11);	 Catch:{ Exception -> 0x0045 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0045 }
        r11 = 5;
        r14.bindString(r11, r3);	 Catch:{ Exception -> 0x0045 }
        r14.step();	 Catch:{ Exception -> 0x0045 }
        r9.reuse();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0697;
    L_0x0695:
        r14 = r20;
    L_0x0697:
        r2 = r2 + 1;
        r25 = r8;
        r20 = r14;
        r3 = r21;
        r15 = r29;
        r21 = r7;
        r7 = r22;
        r22 = r6;
        r6 = r24;
        r24 = r10;
        goto L_0x03c7;
    L_0x06ad:
        r29 = r15;
        r14 = r20;
        r10 = r24;
        r8 = r25;
        r24 = r6;
        r6 = r22;
        r22 = r7;
        r7 = r21;
        r8.dispose();	 Catch:{ Exception -> 0x0045 }
        if (r3 == 0) goto L_0x06c5;
    L_0x06c2:
        r3.dispose();	 Catch:{ Exception -> 0x0045 }
    L_0x06c5:
        if (r4 == 0) goto L_0x06ca;
    L_0x06c7:
        r4.dispose();	 Catch:{ Exception -> 0x0045 }
    L_0x06ca:
        r6.dispose();	 Catch:{ Exception -> 0x0045 }
        r14.dispose();	 Catch:{ Exception -> 0x0045 }
        r7.dispose();	 Catch:{ Exception -> 0x0045 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r4 = "UPDATE dialogs SET date = ?, unread_count = ?, last_mid = ?, unread_count_i = ? WHERE did = ?";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0045 }
        r4 = 0;
    L_0x06e4:
        r6 = r10.size();	 Catch:{ Exception -> 0x0045 }
        if (r4 >= r6) goto L_0x0879;
    L_0x06ea:
        r6 = r10.keyAt(r4);	 Catch:{ Exception -> 0x094c }
        r8 = 0;
        r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r11 != 0) goto L_0x0709;
    L_0x06f4:
        r1 = r2;
        r35 = r5;
        r20 = r10;
        r11 = r22;
        r25 = r24;
        r2 = 5;
        r5 = 6;
        r6 = 0;
        r9 = 9;
        r24 = r4;
        r4 = 8;
        goto L_0x0869;
    L_0x0709:
        r8 = r10.valueAt(r4);	 Catch:{ Exception -> 0x094c }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x094c }
        if (r8 == 0) goto L_0x0716;
    L_0x0711:
        r9 = r8.to_id;	 Catch:{ Exception -> 0x0045 }
        r9 = r9.channel_id;	 Catch:{ Exception -> 0x0045 }
        goto L_0x0717;
    L_0x0716:
        r9 = 0;
    L_0x0717:
        r11 = r1.database;	 Catch:{ Exception -> 0x094c }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x094c }
        r12.<init>();	 Catch:{ Exception -> 0x094c }
        r13 = "SELECT date, unread_count, last_mid, unread_count_i FROM dialogs WHERE did = ";
        r12.append(r13);	 Catch:{ Exception -> 0x094c }
        r12.append(r6);	 Catch:{ Exception -> 0x094c }
        r12 = r12.toString();	 Catch:{ Exception -> 0x094c }
        r13 = 0;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x094c }
        r11 = r11.queryFinalized(r12, r14);	 Catch:{ Exception -> 0x094c }
        r12 = r11.next();	 Catch:{ Exception -> 0x094c }
        if (r12 == 0) goto L_0x075d;
    L_0x0737:
        r14 = r11.intValue(r13);	 Catch:{ Exception -> 0x0045 }
        r20 = r10;
        r15 = 1;
        r10 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.Math.max(r13, r10);	 Catch:{ Exception -> 0x0045 }
        r15 = 2;
        r21 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r31 = r10;
        r15 = 3;
        r10 = r11.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.Math.max(r13, r10);	 Catch:{ Exception -> 0x0045 }
        r13 = r10;
        r15 = r14;
        r14 = r21;
        r10 = r31;
        goto L_0x076c;
    L_0x075d:
        r20 = r10;
        if (r9 == 0) goto L_0x0768;
    L_0x0761:
        r10 = r30.getMessagesController();	 Catch:{ Exception -> 0x0045 }
        r10.checkChannelInviter(r9);	 Catch:{ Exception -> 0x0045 }
    L_0x0768:
        r10 = 0;
        r13 = 0;
        r14 = 0;
        r15 = 0;
    L_0x076c:
        r11.dispose();	 Catch:{ Exception -> 0x094c }
        r11 = r22;
        r21 = r11.get(r6);	 Catch:{ Exception -> 0x094c }
        r21 = (java.lang.Integer) r21;	 Catch:{ Exception -> 0x094c }
        r35 = r5;
        r5 = r24;
        r22 = r5.get(r6);	 Catch:{ Exception -> 0x094c }
        r22 = (java.lang.Integer) r22;	 Catch:{ Exception -> 0x094c }
        if (r22 != 0) goto L_0x078a;
    L_0x0783:
        r16 = 0;
        r22 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0797;
    L_0x078a:
        r24 = r22.intValue();	 Catch:{ Exception -> 0x094c }
        r24 = r24 + r10;
        r1 = java.lang.Integer.valueOf(r24);	 Catch:{ Exception -> 0x094c }
        r5.put(r6, r1);	 Catch:{ Exception -> 0x094c }
    L_0x0797:
        if (r21 != 0) goto L_0x079f;
    L_0x0799:
        r1 = 0;
        r21 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x094c }
        goto L_0x07ab;
    L_0x079f:
        r1 = r21.intValue();	 Catch:{ Exception -> 0x094c }
        r1 = r1 + r13;
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x094c }
        r11.put(r6, r1);	 Catch:{ Exception -> 0x094c }
    L_0x07ab:
        if (r8 == 0) goto L_0x07b5;
    L_0x07ad:
        r1 = r8.id;	 Catch:{ Exception -> 0x094c }
        r24 = r4;
        r25 = r5;
        r4 = (long) r1;	 Catch:{ Exception -> 0x094c }
        goto L_0x07ba;
    L_0x07b5:
        r24 = r4;
        r25 = r5;
        r4 = (long) r14;	 Catch:{ Exception -> 0x094c }
    L_0x07ba:
        if (r8 == 0) goto L_0x07c3;
    L_0x07bc:
        r1 = r8.local_id;	 Catch:{ Exception -> 0x094c }
        if (r1 == 0) goto L_0x07c3;
    L_0x07c0:
        r1 = r8.local_id;	 Catch:{ Exception -> 0x094c }
        r4 = (long) r1;	 Catch:{ Exception -> 0x094c }
    L_0x07c3:
        if (r9 == 0) goto L_0x07cc;
    L_0x07c5:
        r31 = r2;
        r1 = (long) r9;	 Catch:{ Exception -> 0x094c }
        r1 = r1 << r23;
        r4 = r4 | r1;
        goto L_0x07ce;
    L_0x07cc:
        r31 = r2;
    L_0x07ce:
        if (r12 == 0) goto L_0x0807;
    L_0x07d0:
        r3.requery();	 Catch:{ Exception -> 0x094c }
        if (r8 == 0) goto L_0x07db;
    L_0x07d5:
        if (r33 == 0) goto L_0x07d9;
    L_0x07d7:
        if (r15 != 0) goto L_0x07db;
    L_0x07d9:
        r15 = r8.date;	 Catch:{ Exception -> 0x094c }
    L_0x07db:
        r1 = 1;
        r3.bindInteger(r1, r15);	 Catch:{ Exception -> 0x094c }
        r1 = r22.intValue();	 Catch:{ Exception -> 0x094c }
        r10 = r10 + r1;
        r1 = 2;
        r3.bindInteger(r1, r10);	 Catch:{ Exception -> 0x094c }
        r1 = 3;
        r3.bindLong(r1, r4);	 Catch:{ Exception -> 0x094c }
        r1 = r21.intValue();	 Catch:{ Exception -> 0x094c }
        r13 = r13 + r1;
        r1 = 4;
        r3.bindInteger(r1, r13);	 Catch:{ Exception -> 0x094c }
        r1 = 5;
        r3.bindLong(r1, r6);	 Catch:{ Exception -> 0x094c }
        r3.step();	 Catch:{ Exception -> 0x094c }
        r1 = r31;
        r2 = 5;
        r4 = 8;
        r5 = 6;
        r6 = 0;
        r9 = 9;
        goto L_0x0869;
    L_0x0807:
        r31.requery();	 Catch:{ Exception -> 0x094c }
        r1 = r31;
        r2 = 1;
        r1.bindLong(r2, r6);	 Catch:{ Exception -> 0x094c }
        if (r8 == 0) goto L_0x0818;
    L_0x0812:
        if (r33 == 0) goto L_0x0816;
    L_0x0814:
        if (r15 != 0) goto L_0x0818;
    L_0x0816:
        r15 = r8.date;	 Catch:{ Exception -> 0x094c }
    L_0x0818:
        r2 = 2;
        r1.bindInteger(r2, r15);	 Catch:{ Exception -> 0x094c }
        r2 = r22.intValue();	 Catch:{ Exception -> 0x094c }
        r10 = r10 + r2;
        r2 = 3;
        r1.bindInteger(r2, r10);	 Catch:{ Exception -> 0x094c }
        r2 = 4;
        r1.bindLong(r2, r4);	 Catch:{ Exception -> 0x094c }
        r2 = 5;
        r4 = 0;
        r1.bindInteger(r2, r4);	 Catch:{ Exception -> 0x094c }
        r5 = 6;
        r1.bindInteger(r5, r4);	 Catch:{ Exception -> 0x094c }
        r4 = 7;
        r6 = 0;
        r1.bindLong(r4, r6);	 Catch:{ Exception -> 0x094c }
        r4 = r21.intValue();	 Catch:{ Exception -> 0x094c }
        r13 = r13 + r4;
        r4 = 8;
        r1.bindInteger(r4, r13);	 Catch:{ Exception -> 0x094c }
        if (r9 == 0) goto L_0x0846;
    L_0x0844:
        r8 = 1;
        goto L_0x0847;
    L_0x0846:
        r8 = 0;
    L_0x0847:
        r9 = 9;
        r1.bindInteger(r9, r8);	 Catch:{ Exception -> 0x094c }
        r8 = 10;
        r10 = 0;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094c }
        r8 = 11;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094c }
        r8 = 12;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094c }
        r8 = 13;
        r1.bindInteger(r8, r10);	 Catch:{ Exception -> 0x094c }
        r8 = 14;
        r1.bindNull(r8);	 Catch:{ Exception -> 0x094c }
        r1.step();	 Catch:{ Exception -> 0x094c }
    L_0x0869:
        r8 = r24 + 1;
        r5 = r35;
        r2 = r1;
        r4 = r8;
        r22 = r11;
        r10 = r20;
        r24 = r25;
        r1 = r30;
        goto L_0x06e4;
    L_0x0879:
        r1 = r2;
        r35 = r5;
        r11 = r22;
        r25 = r24;
        r3.dispose();	 Catch:{ Exception -> 0x094c }
        r1.dispose();	 Catch:{ Exception -> 0x094c }
        if (r29 == 0) goto L_0x092d;
    L_0x0888:
        r1 = r30;
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r3 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = 0;
    L_0x0893:
        r4 = r29.size();	 Catch:{ Exception -> 0x0045 }
        if (r3 >= r4) goto L_0x0929;
    L_0x0899:
        r15 = r29;
        r4 = r15.keyAt(r3);	 Catch:{ Exception -> 0x0045 }
        r5 = r15.valueAt(r3);	 Catch:{ Exception -> 0x0045 }
        r5 = (android.util.LongSparseArray) r5;	 Catch:{ Exception -> 0x0045 }
        r6 = 0;
    L_0x08a6:
        r7 = r5.size();	 Catch:{ Exception -> 0x0045 }
        if (r6 >= r7) goto L_0x091d;
    L_0x08ac:
        r7 = r5.keyAt(r6);	 Catch:{ Exception -> 0x0045 }
        r9 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0045 }
        r12 = "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r13 = 2;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0045 }
        r13 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0045 }
        r29 = r15;
        r15 = 0;
        r14[r15] = r13;	 Catch:{ Exception -> 0x0045 }
        r13 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0045 }
        r16 = 1;
        r14[r16] = r13;	 Catch:{ Exception -> 0x0045 }
        r10 = java.lang.String.format(r10, r12, r14);	 Catch:{ Exception -> 0x0045 }
        r12 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0045 }
        r9 = r9.queryFinalized(r10, r12);	 Catch:{ Exception -> 0x0045 }
        r10 = r9.next();	 Catch:{ Exception -> 0x0045 }
        if (r10 == 0) goto L_0x08e4;
    L_0x08da:
        r10 = r9.intValue(r15);	 Catch:{ Exception -> 0x0045 }
        r12 = 1;
        r13 = r9.intValue(r12);	 Catch:{ Exception -> 0x0045 }
        goto L_0x08e6;
    L_0x08e4:
        r10 = -1;
        r13 = 0;
    L_0x08e6:
        r9.dispose();	 Catch:{ Exception -> 0x0045 }
        r9 = -1;
        if (r10 == r9) goto L_0x0913;
    L_0x08ec:
        r2.requery();	 Catch:{ Exception -> 0x0045 }
        r12 = r5.valueAt(r6);	 Catch:{ Exception -> 0x0045 }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x0045 }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x0045 }
        r10 = r10 + r12;
        r12 = 1;
        r2.bindLong(r12, r7);	 Catch:{ Exception -> 0x0045 }
        r7 = 2;
        r2.bindInteger(r7, r4);	 Catch:{ Exception -> 0x0045 }
        r8 = 0;
        r10 = java.lang.Math.max(r8, r10);	 Catch:{ Exception -> 0x0045 }
        r14 = 3;
        r2.bindInteger(r14, r10);	 Catch:{ Exception -> 0x0045 }
        r10 = 4;
        r2.bindInteger(r10, r13);	 Catch:{ Exception -> 0x0045 }
        r2.step();	 Catch:{ Exception -> 0x0045 }
        goto L_0x0918;
    L_0x0913:
        r7 = 2;
        r8 = 0;
        r10 = 4;
        r12 = 1;
        r14 = 3;
    L_0x0918:
        r6 = r6 + 1;
        r15 = r29;
        goto L_0x08a6;
    L_0x091d:
        r29 = r15;
        r7 = 2;
        r8 = 0;
        r9 = -1;
        r10 = 4;
        r12 = 1;
        r14 = 3;
        r3 = r3 + 1;
        goto L_0x0893;
    L_0x0929:
        r2.dispose();	 Catch:{ Exception -> 0x0045 }
        goto L_0x092f;
    L_0x092d:
        r1 = r30;
    L_0x092f:
        if (r32 == 0) goto L_0x0936;
    L_0x0931:
        r2 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r2.commitTransaction();	 Catch:{ Exception -> 0x0045 }
    L_0x0936:
        r2 = r30.getMessagesController();	 Catch:{ Exception -> 0x0045 }
        r3 = r25;
        r2.processDialogsUpdateRead(r3, r11);	 Catch:{ Exception -> 0x0045 }
        if (r35 == 0) goto L_0x0954;
    L_0x0941:
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$kEM836O7XHnidmvk_F_LTvf7NK4;	 Catch:{ Exception -> 0x0045 }
        r5 = r35;
        r2.<init>(r1, r5);	 Catch:{ Exception -> 0x0045 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0045 }
        goto L_0x0954;
    L_0x094c:
        r0 = move-exception;
        r1 = r30;
        goto L_0x0046;
    L_0x0951:
        org.telegram.messenger.FileLog.e(r2);
    L_0x0954:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putMessagesInternal(java.util.ArrayList, boolean, boolean, int, boolean):void");
    }

    public /* synthetic */ void lambda$putMessagesInternal$120$MessagesStorage(int i) {
        getDownloadController().newDownloadObjectsAvailable(i);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i) {
        putMessages(arrayList, z, z2, z3, i, false);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        if (arrayList.size() != 0) {
            if (z2) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$C5MdBeaFh--zdZ5yyOm4jthA7xI(this, arrayList, z, z3, i, z4));
            } else {
                putMessagesInternal(arrayList, z, z3, i, z4);
            }
        }
    }

    public /* synthetic */ void lambda$putMessages$121$MessagesStorage(ArrayList arrayList, boolean z, boolean z2, int i, boolean z3) {
        putMessagesInternal(arrayList, z, z2, i, z3);
    }

    public void markMessageAsSendError(Message message) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$U1zBGSD7nENssAbAXgrCmtYpcyg(this, message));
    }

    public /* synthetic */ void lambda$markMessageAsSendError$122$MessagesStorage(Message message) {
        try {
            long j = (long) message.id;
            if (message.to_id.channel_id != 0) {
                j |= ((long) message.to_id.channel_id) << 32;
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE messages SET send_state = 2 WHERE mid = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$G8zL9NB-Fitb5bCdEHpkzqox5bg(this, i, i2, i3));
    }

    public /* synthetic */ void lambda$setMessageSeq$123$MessagesStorage(int i, int i2, int i3) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindInteger(3, i3);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004f A:{RETURN} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x00fb */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x01ba  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:89:0x0159 */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x004f A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:56:0x00c7, code skipped:
            if (r3 != null) goto L_0x00d2;
     */
    /* JADX WARNING: Missing block: B:61:0x00d0, code skipped:
            if (r3 == null) goto L_0x00d5;
     */
    /* JADX WARNING: Missing block: B:62:0x00d2, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:64:0x00db, code skipped:
            return new long[]{r15, r4};
     */
    /* JADX WARNING: Missing block: B:72:0x00f3, code skipped:
            if (r2 != null) goto L_0x013f;
     */
    /* JADX WARNING: Missing block: B:78:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:79:0x0136, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:80:0x0139, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:82:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:83:0x013d, code skipped:
            if (r2 == null) goto L_0x0143;
     */
    /* JADX WARNING: Missing block: B:84:0x013f, code skipped:
            r2.dispose();
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:86:?, code skipped:
            r2 = r1.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
            r2.bindLong(1, r13);
            r2.bindLong(2, r11);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:87:0x0154, code skipped:
            if (r2 == null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:88:0x0157, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:90:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r11)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:91:0x0177, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:93:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:94:0x017b, code skipped:
            if (r2 == null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:95:0x017d, code skipped:
            r2.dispose();
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:97:?, code skipped:
            r2 = r1.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
            r2.bindLong(1, r13);
            r2.bindLong(2, r11);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:98:0x0192, code skipped:
            if (r2 == null) goto L_0x01a0;
     */
    /* JADX WARNING: Missing block: B:99:0x0195, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:100:0x0197, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:102:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:103:0x019b, code skipped:
            if (r2 == null) goto L_0x01a0;
     */
    /* JADX WARNING: Missing block: B:104:0x019d, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:106:0x01ab, code skipped:
            return new long[]{r15, (long) r9.intValue()};
     */
    /* JADX WARNING: Missing block: B:107:0x01ac, code skipped:
            if (r2 != null) goto L_0x01ae;
     */
    /* JADX WARNING: Missing block: B:108:0x01ae, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:109:0x01b1, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:110:0x01b2, code skipped:
            if (r2 != null) goto L_0x01b4;
     */
    /* JADX WARNING: Missing block: B:111:0x01b4, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:112:0x01b7, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:114:0x01ba, code skipped:
            r2.dispose();
     */
    private long[] updateMessageStateAndIdInternal(long r20, java.lang.Integer r22, int r23, int r24, int r25) {
        /*
        r19 = this;
        r1 = r19;
        r2 = r24;
        r0 = r23;
        r3 = r25;
        r4 = (long) r0;
        r6 = 0;
        r7 = 0;
        r8 = 1;
        if (r22 != 0) goto L_0x005b;
    L_0x000e:
        r0 = r1.database;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r10 = "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1";
        r11 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r12 = java.lang.Long.valueOf(r20);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r11[r7] = r12;	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = java.lang.String.format(r9, r10, r11);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r9 = r0.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x0041, all -> 0x003f }
        r0 = r9.next();	 Catch:{ Exception -> 0x003d }
        if (r0 == 0) goto L_0x0035;
    L_0x002c:
        r0 = r9.intValue(r7);	 Catch:{ Exception -> 0x003d }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x003d }
        goto L_0x0037;
    L_0x0035:
        r0 = r22;
    L_0x0037:
        if (r9 == 0) goto L_0x004d;
    L_0x0039:
        r9.dispose();
        goto L_0x004d;
    L_0x003d:
        r0 = move-exception;
        goto L_0x0043;
    L_0x003f:
        r0 = move-exception;
        goto L_0x0055;
    L_0x0041:
        r0 = move-exception;
        r9 = r6;
    L_0x0043:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0053 }
        if (r9 == 0) goto L_0x004b;
    L_0x0048:
        r9.dispose();
    L_0x004b:
        r0 = r22;
    L_0x004d:
        if (r0 != 0) goto L_0x0050;
    L_0x004f:
        return r6;
    L_0x0050:
        r10 = r9;
        r9 = r0;
        goto L_0x005e;
    L_0x0053:
        r0 = move-exception;
        r6 = r9;
    L_0x0055:
        if (r6 == 0) goto L_0x005a;
    L_0x0057:
        r6.dispose();
    L_0x005a:
        throw r0;
    L_0x005b:
        r9 = r22;
        r10 = r6;
    L_0x005e:
        r0 = r9.intValue();
        r11 = (long) r0;
        if (r3 == 0) goto L_0x006c;
    L_0x0065:
        r13 = (long) r3;
        r0 = 32;
        r13 = r13 << r0;
        r11 = r11 | r13;
        r13 = r13 | r4;
        goto L_0x006d;
    L_0x006c:
        r13 = r4;
    L_0x006d:
        r0 = r1.database;	 Catch:{ Exception -> 0x009b }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x009b }
        r6 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r15 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x009b }
        r16 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x009b }
        r15[r7] = r16;	 Catch:{ Exception -> 0x009b }
        r3 = java.lang.String.format(r3, r6, r15);	 Catch:{ Exception -> 0x009b }
        r6 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x009b }
        r10 = r0.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x009b }
        r0 = r10.next();	 Catch:{ Exception -> 0x009b }
        if (r0 == 0) goto L_0x0090;
    L_0x008b:
        r15 = r10.longValue(r7);	 Catch:{ Exception -> 0x009b }
        goto L_0x0092;
    L_0x0090:
        r15 = 0;
    L_0x0092:
        if (r10 == 0) goto L_0x00a6;
    L_0x0094:
        r10.dispose();
        goto L_0x00a6;
    L_0x0098:
        r0 = move-exception;
        goto L_0x01be;
    L_0x009b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0098 }
        if (r10 == 0) goto L_0x00a4;
    L_0x00a1:
        r10.dispose();
    L_0x00a4:
        r15 = 0;
    L_0x00a6:
        r17 = 0;
        r0 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1));
        if (r0 != 0) goto L_0x00ae;
    L_0x00ac:
        r3 = 0;
        return r3;
    L_0x00ae:
        r3 = 0;
        r6 = 2;
        r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x00e2;
    L_0x00b4:
        if (r2 == 0) goto L_0x00e2;
    L_0x00b6:
        r0 = r1.database;	 Catch:{ Exception -> 0x00cc }
        r9 = "UPDATE messages SET send_state = 0, date = ? WHERE mid = ?";
        r3 = r0.executeFast(r9);	 Catch:{ Exception -> 0x00cc }
        r3.bindInteger(r8, r2);	 Catch:{ Exception -> 0x00cc }
        r3.bindLong(r6, r13);	 Catch:{ Exception -> 0x00cc }
        r3.step();	 Catch:{ Exception -> 0x00cc }
        if (r3 == 0) goto L_0x00d5;
    L_0x00c9:
        goto L_0x00d2;
    L_0x00ca:
        r0 = move-exception;
        goto L_0x00dc;
    L_0x00cc:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00ca }
        if (r3 == 0) goto L_0x00d5;
    L_0x00d2:
        r3.dispose();
    L_0x00d5:
        r0 = new long[r6];
        r0[r7] = r15;
        r0[r8] = r4;
        return r0;
    L_0x00dc:
        if (r3 == 0) goto L_0x00e1;
    L_0x00de:
        r3.dispose();
    L_0x00e1:
        throw r0;
    L_0x00e2:
        r0 = r1.database;	 Catch:{ Exception -> 0x00fa, all -> 0x00f6 }
        r2 = "UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?";
        r2 = r0.executeFast(r2);	 Catch:{ Exception -> 0x00fa, all -> 0x00f6 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x00fb }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x00fb }
        r2.step();	 Catch:{ Exception -> 0x00fb }
        if (r2 == 0) goto L_0x0143;
    L_0x00f5:
        goto L_0x013f;
    L_0x00f6:
        r0 = move-exception;
        r2 = r3;
        goto L_0x01b8;
    L_0x00fa:
        r2 = r3;
    L_0x00fb:
        r0 = r1.database;	 Catch:{ Exception -> 0x0139 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0139 }
        r5 = "DELETE FROM messages WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0139 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0139 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0139 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0139 }
        r0.dispose();	 Catch:{ Exception -> 0x0139 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0139 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0139 }
        r5 = "DELETE FROM messages_seq WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0139 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0139 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0139 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0139 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0139 }
        r0.dispose();	 Catch:{ Exception -> 0x0139 }
        goto L_0x013d;
    L_0x0136:
        r0 = move-exception;
        goto L_0x01b8;
    L_0x0139:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0136 }
    L_0x013d:
        if (r2 == 0) goto L_0x0143;
    L_0x013f:
        r2.dispose();
        r2 = r3;
    L_0x0143:
        r0 = r1.database;	 Catch:{ Exception -> 0x0159 }
        r4 = "UPDATE media_v2 SET mid = ? WHERE mid = ?";
        r2 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0159 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x0159 }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x0159 }
        r2.step();	 Catch:{ Exception -> 0x0159 }
        if (r2 == 0) goto L_0x0181;
    L_0x0156:
        goto L_0x017d;
    L_0x0157:
        r0 = move-exception;
        goto L_0x01b2;
    L_0x0159:
        r0 = r1.database;	 Catch:{ Exception -> 0x0177 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0177 }
        r5 = "DELETE FROM media_v2 WHERE mid = %d";
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0177 }
        r17 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0177 }
        r10[r7] = r17;	 Catch:{ Exception -> 0x0177 }
        r4 = java.lang.String.format(r4, r5, r10);	 Catch:{ Exception -> 0x0177 }
        r0 = r0.executeFast(r4);	 Catch:{ Exception -> 0x0177 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0177 }
        r0.dispose();	 Catch:{ Exception -> 0x0177 }
        goto L_0x017b;
    L_0x0177:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0157 }
    L_0x017b:
        if (r2 == 0) goto L_0x0181;
    L_0x017d:
        r2.dispose();
        r2 = r3;
    L_0x0181:
        r0 = r1.database;	 Catch:{ Exception -> 0x0197 }
        r3 = "UPDATE dialogs SET last_mid = ? WHERE last_mid = ?";
        r2 = r0.executeFast(r3);	 Catch:{ Exception -> 0x0197 }
        r2.bindLong(r8, r13);	 Catch:{ Exception -> 0x0197 }
        r2.bindLong(r6, r11);	 Catch:{ Exception -> 0x0197 }
        r2.step();	 Catch:{ Exception -> 0x0197 }
        if (r2 == 0) goto L_0x01a0;
    L_0x0194:
        goto L_0x019d;
    L_0x0195:
        r0 = move-exception;
        goto L_0x01ac;
    L_0x0197:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0195 }
        if (r2 == 0) goto L_0x01a0;
    L_0x019d:
        r2.dispose();
    L_0x01a0:
        r0 = new long[r6];
        r0[r7] = r15;
        r2 = r9.intValue();
        r2 = (long) r2;
        r0[r8] = r2;
        return r0;
    L_0x01ac:
        if (r2 == 0) goto L_0x01b1;
    L_0x01ae:
        r2.dispose();
    L_0x01b1:
        throw r0;
    L_0x01b2:
        if (r2 == 0) goto L_0x01b7;
    L_0x01b4:
        r2.dispose();
    L_0x01b7:
        throw r0;
    L_0x01b8:
        if (r2 == 0) goto L_0x01bd;
    L_0x01ba:
        r2.dispose();
    L_0x01bd:
        throw r0;
    L_0x01be:
        if (r10 == 0) goto L_0x01c3;
    L_0x01c0:
        r10.dispose();
    L_0x01c3:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateMessageStateAndIdInternal(long, java.lang.Integer, int, int, int):long[]");
    }

    public /* synthetic */ void lambda$updateMessageStateAndId$124$MessagesStorage(long j, Integer num, int i, int i2, int i3) {
        updateMessageStateAndIdInternal(j, num, i, i2, i3);
    }

    public long[] updateMessageStateAndId(long j, Integer num, int i, int i2, boolean z, int i3) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$WU_EaXX3SitcS_mgxT2w3G1G8N8(this, j, num, i, i2, i3));
            return null;
        }
        return updateMessageStateAndIdInternal(j, num, i, i2, i3);
    }

    private void updateUsersInternal(ArrayList<User> arrayList, boolean z, boolean z2) {
        int i = 0;
        int i2;
        User user;
        if (z) {
            if (z2) {
                try {
                    this.database.beginTransaction();
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
            int size = arrayList.size();
            for (i2 = 0; i2 < size; i2++) {
                user = (User) arrayList.get(i2);
                executeFast.requery();
                if (user.status != null) {
                    executeFast.bindInteger(1, user.status.expires);
                } else {
                    executeFast.bindInteger(1, 0);
                }
                executeFast.bindInteger(2, user.id);
                executeFast.step();
            }
            executeFast.dispose();
            if (z2) {
                this.database.commitTransaction();
                return;
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        SparseArray sparseArray = new SparseArray();
        i2 = arrayList.size();
        for (int i3 = 0; i3 < i2; i3++) {
            User user2 = (User) arrayList.get(i3);
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(user2.id);
            sparseArray.put(user2.id, user2);
        }
        ArrayList arrayList2 = new ArrayList();
        getUsersInternal(stringBuilder.toString(), arrayList2);
        int size2 = arrayList2.size();
        while (i < size2) {
            User user3 = (User) arrayList2.get(i);
            user = (User) sparseArray.get(user3.id);
            if (user != null) {
                if (user.first_name != null && user.last_name != null) {
                    if (!UserObject.isContact(user3)) {
                        user3.first_name = user.first_name;
                        user3.last_name = user.last_name;
                    }
                    user3.username = user.username;
                } else if (user.photo != null) {
                    user3.photo = user.photo;
                } else if (user.phone != null) {
                    user3.phone = user.phone;
                }
            }
            i++;
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

    public void updateUsers(ArrayList<User> arrayList, boolean z, boolean z2, boolean z3) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (z3) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$F8boba5oNhoqWMrqWxfVJfns2vk(this, arrayList, z, z2));
            } else {
                updateUsersInternal(arrayList, z, z2);
            }
        }
    }

    public /* synthetic */ void lambda$updateUsers$125$MessagesStorage(ArrayList arrayList, boolean z, boolean z2) {
        updateUsersInternal(arrayList, z, z2);
    }

    private void markMessagesAsReadInternal(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        SparseLongArray sparseLongArray3 = sparseLongArray;
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        SparseIntArray sparseIntArray2 = sparseIntArray;
        try {
            int i;
            long j;
            int i2 = 0;
            if (!isEmpty(sparseLongArray)) {
                SQLitePreparedStatement executeFast = this.database.executeFast("DELETE FROM unread_push_messages WHERE uid = ? AND mid <= ?");
                for (int i3 = 0; i3 < sparseLongArray.size(); i3++) {
                    int keyAt = sparseLongArray3.keyAt(i3);
                    long j2 = sparseLongArray3.get(keyAt);
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[]{Integer.valueOf(keyAt), Long.valueOf(j2)})).stepThis().dispose();
                    executeFast.requery();
                    executeFast.bindLong(1, (long) keyAt);
                    executeFast.bindLong(2, j2);
                    executeFast.step();
                }
                executeFast.dispose();
            }
            if (!isEmpty(sparseLongArray2)) {
                for (i = 0; i < sparseLongArray2.size(); i++) {
                    j = sparseLongArray4.get(sparseLongArray4.keyAt(i));
                    this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[]{Integer.valueOf(r4), Long.valueOf(j)})).stepThis().dispose();
                }
            }
            if (sparseIntArray2 != null && !isEmpty(sparseIntArray)) {
                while (i2 < sparseIntArray.size()) {
                    j = ((long) sparseIntArray2.keyAt(i2)) << 32;
                    i = sparseIntArray2.valueAt(i2);
                    SQLitePreparedStatement executeFast2 = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                    executeFast2.requery();
                    executeFast2.bindLong(1, j);
                    executeFast2.bindInteger(2, i);
                    executeFast2.step();
                    executeFast2.dispose();
                    i2++;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void markMessagesContentAsRead(ArrayList<Long> arrayList, int i) {
        if (!isEmpty((List) arrayList)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$tfugjIkfK7nBtnBOpdzgghpbtc8(this, arrayList, i));
        }
    }

    public /* synthetic */ void lambda$markMessagesContentAsRead$126$MessagesStorage(ArrayList arrayList, int i) {
        try {
            String join = TextUtils.join(",", arrayList);
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[]{join})).stepThis().dispose();
            if (i != 0) {
                SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE mid IN (%s) AND ttl > 0", new Object[]{join}), new Object[0]);
                ArrayList arrayList2 = null;
                while (queryFinalized.next()) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
                }
                if (arrayList2 != null) {
                    emptyMessagesMedia(arrayList2);
                }
                queryFinalized.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$markMessagesAsRead$127$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
    }

    public void markMessagesAsRead(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ZKMHEKHuJ6dOsqeHAeNsC8jKMsA(this, sparseLongArray, sparseLongArray2, sparseIntArray));
        } else {
            markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(ArrayList<Long> arrayList) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6zxGoXepQVJrSJhl_6ShJc-tecw(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$129$MessagesStorage(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{join}), new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            while (queryFinalized.next()) {
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (!arrayList2.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$luZOLUxYa704MNzM5dy271V47WU(this, arrayList2));
                updateDialogsWithReadMessagesInternal(arrayList2, null, null, null);
                markMessagesAsDeletedInternal(arrayList2, 0);
                updateDialogsWithDeletedMessagesInternal(arrayList2, null, 0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$128$MessagesStorage(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(0));
    }

    /* Access modifiers changed, original: protected */
    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", new Object[]{Long.valueOf(j), TextUtils.join(",", arrayList)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac A:{Catch:{ Exception -> 0x03ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0393 A:{Catch:{ Exception -> 0x03ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0295 A:{Catch:{ Exception -> 0x03ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac A:{Catch:{ Exception -> 0x03ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0295 A:{Catch:{ Exception -> 0x03ea }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0393 A:{Catch:{ Exception -> 0x03ea }} */
    private java.util.ArrayList<java.lang.Long> markMessagesAsDeletedInternal(java.util.ArrayList<java.lang.Integer> r21, int r22) {
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ea }
        r5.<init>(r2);	 Catch:{ Exception -> 0x03ea }
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ea }
        r6.<init>();	 Catch:{ Exception -> 0x03ea }
        r7 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03ea }
        r7.<init>();	 Catch:{ Exception -> 0x03ea }
        r8 = 0;
        if (r3 == 0) goto L_0x004e;
    L_0x0018:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03ea }
        r9 = r21.size();	 Catch:{ Exception -> 0x03ea }
        r0.<init>(r9);	 Catch:{ Exception -> 0x03ea }
        r9 = 0;
    L_0x0022:
        r10 = r21.size();	 Catch:{ Exception -> 0x03ea }
        if (r9 >= r10) goto L_0x0049;
    L_0x0028:
        r10 = r2.get(r9);	 Catch:{ Exception -> 0x03ea }
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x03ea }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x03ea }
        r10 = (long) r10;	 Catch:{ Exception -> 0x03ea }
        r12 = (long) r3;	 Catch:{ Exception -> 0x03ea }
        r14 = 32;
        r12 = r12 << r14;
        r10 = r10 | r12;
        r12 = r0.length();	 Catch:{ Exception -> 0x03ea }
        if (r12 <= 0) goto L_0x0043;
    L_0x003e:
        r12 = 44;
        r0.append(r12);	 Catch:{ Exception -> 0x03ea }
    L_0x0043:
        r0.append(r10);	 Catch:{ Exception -> 0x03ea }
        r9 = r9 + 1;
        goto L_0x0022;
    L_0x0049:
        r0 = r0.toString();	 Catch:{ Exception -> 0x03ea }
        goto L_0x0054;
    L_0x004e:
        r0 = ",";
        r0 = android.text.TextUtils.join(r0, r2);	 Catch:{ Exception -> 0x03ea }
    L_0x0054:
        r9 = r0;
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03ea }
        r10.<init>();	 Catch:{ Exception -> 0x03ea }
        r0 = r20.getUserConfig();	 Catch:{ Exception -> 0x03ea }
        r0 = r0.getClientUserId();	 Catch:{ Exception -> 0x03ea }
        r11 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r13 = "SELECT uid, data, read_state, out, mention, mid FROM messages WHERE mid IN(%s)";
        r14 = 1;
        r15 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x03ea }
        r15[r8] = r9;	 Catch:{ Exception -> 0x03ea }
        r12 = java.lang.String.format(r12, r13, r15);	 Catch:{ Exception -> 0x03ea }
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x03ea }
        r11 = r11.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x03ea }
    L_0x0077:
        r12 = 3;
        r13 = 2;
        r15 = r11.next();	 Catch:{ Exception -> 0x0194 }
        if (r15 == 0) goto L_0x0191;
    L_0x007f:
        r14 = r11.longValue(r8);	 Catch:{ Exception -> 0x0194 }
        r4 = 5;
        r4 = r11.intValue(r4);	 Catch:{ Exception -> 0x0194 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0194 }
        r5.remove(r4);	 Catch:{ Exception -> 0x0194 }
        r17 = r9;
        r8 = (long) r0;
        r18 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1));
        if (r18 != 0) goto L_0x009b;
    L_0x0096:
        r9 = r17;
        r8 = 0;
        r14 = 1;
        goto L_0x0077;
    L_0x009b:
        r8 = r11.intValue(r13);	 Catch:{ Exception -> 0x018f }
        r9 = r11.intValue(r12);	 Catch:{ Exception -> 0x018f }
        if (r9 != 0) goto L_0x00ec;
    L_0x00a5:
        r9 = r7.get(r14);	 Catch:{ Exception -> 0x018f }
        r9 = (java.lang.Integer[]) r9;	 Catch:{ Exception -> 0x018f }
        if (r9 != 0) goto L_0x00c1;
    L_0x00ad:
        r9 = new java.lang.Integer[r13];	 Catch:{ Exception -> 0x018f }
        r4 = 0;
        r18 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x018f }
        r9[r4] = r18;	 Catch:{ Exception -> 0x018f }
        r18 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x018f }
        r16 = 1;
        r9[r16] = r18;	 Catch:{ Exception -> 0x018f }
        r7.put(r14, r9);	 Catch:{ Exception -> 0x018f }
    L_0x00c1:
        if (r8 >= r13) goto L_0x00d5;
    L_0x00c3:
        r16 = 1;
        r18 = r9[r16];	 Catch:{ Exception -> 0x018f }
        r18 = r9[r16];	 Catch:{ Exception -> 0x018f }
        r18 = r18.intValue();	 Catch:{ Exception -> 0x018f }
        r18 = r18 + 1;
        r18 = java.lang.Integer.valueOf(r18);	 Catch:{ Exception -> 0x018f }
        r9[r16] = r18;	 Catch:{ Exception -> 0x018f }
    L_0x00d5:
        if (r8 == 0) goto L_0x00d9;
    L_0x00d7:
        if (r8 != r13) goto L_0x00ec;
    L_0x00d9:
        r4 = 0;
        r8 = r9[r4];	 Catch:{ Exception -> 0x018f }
        r8 = r9[r4];	 Catch:{ Exception -> 0x018f }
        r8 = r8.intValue();	 Catch:{ Exception -> 0x018f }
        r16 = 1;
        r8 = r8 + 1;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x018f }
        r9[r4] = r8;	 Catch:{ Exception -> 0x018f }
    L_0x00ec:
        r8 = (int) r14;	 Catch:{ Exception -> 0x018f }
        if (r8 == 0) goto L_0x00f0;
    L_0x00ef:
        goto L_0x0096;
    L_0x00f0:
        r8 = 1;
        r9 = r11.byteBufferValue(r8);	 Catch:{ Exception -> 0x018f }
        if (r9 == 0) goto L_0x0096;
    L_0x00f7:
        r4 = 0;
        r8 = r9.readInt32(r4);	 Catch:{ Exception -> 0x018f }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r9, r8, r4);	 Catch:{ Exception -> 0x018f }
        r14 = r20.getUserConfig();	 Catch:{ Exception -> 0x018f }
        r14 = r14.clientUserId;	 Catch:{ Exception -> 0x018f }
        r8.readAttachPath(r9, r14);	 Catch:{ Exception -> 0x018f }
        r9.reuse();	 Catch:{ Exception -> 0x018f }
        if (r8 == 0) goto L_0x0096;
    L_0x010e:
        r9 = r8.media;	 Catch:{ Exception -> 0x018f }
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x018f }
        if (r9 == 0) goto L_0x0143;
    L_0x0114:
        r9 = r8.media;	 Catch:{ Exception -> 0x018f }
        r9 = r9.photo;	 Catch:{ Exception -> 0x018f }
        r9 = r9.sizes;	 Catch:{ Exception -> 0x018f }
        r9 = r9.size();	 Catch:{ Exception -> 0x018f }
        r14 = 0;
    L_0x011f:
        if (r14 >= r9) goto L_0x0096;
    L_0x0121:
        r15 = r8.media;	 Catch:{ Exception -> 0x018f }
        r15 = r15.photo;	 Catch:{ Exception -> 0x018f }
        r15 = r15.sizes;	 Catch:{ Exception -> 0x018f }
        r15 = r15.get(r14);	 Catch:{ Exception -> 0x018f }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x018f }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x018f }
        if (r15 == 0) goto L_0x0140;
    L_0x0133:
        r18 = r15.toString();	 Catch:{ Exception -> 0x018f }
        r18 = r18.length();	 Catch:{ Exception -> 0x018f }
        if (r18 <= 0) goto L_0x0140;
    L_0x013d:
        r10.add(r15);	 Catch:{ Exception -> 0x018f }
    L_0x0140:
        r14 = r14 + 1;
        goto L_0x011f;
    L_0x0143:
        r9 = r8.media;	 Catch:{ Exception -> 0x018f }
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x018f }
        if (r9 == 0) goto L_0x0096;
    L_0x0149:
        r9 = r8.media;	 Catch:{ Exception -> 0x018f }
        r9 = r9.document;	 Catch:{ Exception -> 0x018f }
        r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9);	 Catch:{ Exception -> 0x018f }
        if (r9 == 0) goto L_0x0160;
    L_0x0153:
        r14 = r9.toString();	 Catch:{ Exception -> 0x018f }
        r14 = r14.length();	 Catch:{ Exception -> 0x018f }
        if (r14 <= 0) goto L_0x0160;
    L_0x015d:
        r10.add(r9);	 Catch:{ Exception -> 0x018f }
    L_0x0160:
        r9 = r8.media;	 Catch:{ Exception -> 0x018f }
        r9 = r9.document;	 Catch:{ Exception -> 0x018f }
        r9 = r9.thumbs;	 Catch:{ Exception -> 0x018f }
        r9 = r9.size();	 Catch:{ Exception -> 0x018f }
        r14 = 0;
    L_0x016b:
        if (r14 >= r9) goto L_0x0096;
    L_0x016d:
        r15 = r8.media;	 Catch:{ Exception -> 0x018f }
        r15 = r15.document;	 Catch:{ Exception -> 0x018f }
        r15 = r15.thumbs;	 Catch:{ Exception -> 0x018f }
        r15 = r15.get(r14);	 Catch:{ Exception -> 0x018f }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x018f }
        r15 = org.telegram.messenger.FileLoader.getPathToAttach(r15);	 Catch:{ Exception -> 0x018f }
        if (r15 == 0) goto L_0x018c;
    L_0x017f:
        r18 = r15.toString();	 Catch:{ Exception -> 0x018f }
        r18 = r18.length();	 Catch:{ Exception -> 0x018f }
        if (r18 <= 0) goto L_0x018c;
    L_0x0189:
        r10.add(r15);	 Catch:{ Exception -> 0x018f }
    L_0x018c:
        r14 = r14 + 1;
        goto L_0x016b;
    L_0x018f:
        r0 = move-exception;
        goto L_0x0197;
    L_0x0191:
        r17 = r9;
        goto L_0x019a;
    L_0x0194:
        r0 = move-exception;
        r17 = r9;
    L_0x0197:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x03ea }
    L_0x019a:
        r11.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r20.getFileLoader();	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r0.deleteFiles(r10, r4);	 Catch:{ Exception -> 0x03ea }
        r0 = 0;
    L_0x01a6:
        r8 = r7.size();	 Catch:{ Exception -> 0x03ea }
        if (r0 >= r8) goto L_0x0223;
    L_0x01ac:
        r8 = r7.keyAt(r0);	 Catch:{ Exception -> 0x03ea }
        r10 = r7.valueAt(r0);	 Catch:{ Exception -> 0x03ea }
        r10 = (java.lang.Integer[]) r10;	 Catch:{ Exception -> 0x03ea }
        r11 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x03ea }
        r14.<init>();	 Catch:{ Exception -> 0x03ea }
        r15 = "SELECT unread_count, unread_count_i FROM dialogs WHERE did = ";
        r14.append(r15);	 Catch:{ Exception -> 0x03ea }
        r14.append(r8);	 Catch:{ Exception -> 0x03ea }
        r14 = r14.toString();	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r15 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x03ea }
        r11 = r11.queryFinalized(r14, r15);	 Catch:{ Exception -> 0x03ea }
        r14 = r11.next();	 Catch:{ Exception -> 0x03ea }
        if (r14 == 0) goto L_0x01e0;
    L_0x01d6:
        r14 = r11.intValue(r4);	 Catch:{ Exception -> 0x03ea }
        r15 = 1;
        r18 = r11.intValue(r15);	 Catch:{ Exception -> 0x03ea }
        goto L_0x01e3;
    L_0x01e0:
        r14 = 0;
        r18 = 0;
    L_0x01e3:
        r11.dispose();	 Catch:{ Exception -> 0x03ea }
        r11 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x03ea }
        r6.add(r11);	 Catch:{ Exception -> 0x03ea }
        r11 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r15 = "UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?";
        r11 = r11.executeFast(r15);	 Catch:{ Exception -> 0x03ea }
        r11.requery();	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r15 = r10[r4];	 Catch:{ Exception -> 0x03ea }
        r15 = r15.intValue();	 Catch:{ Exception -> 0x03ea }
        r14 = r14 - r15;
        r14 = java.lang.Math.max(r4, r14);	 Catch:{ Exception -> 0x03ea }
        r15 = 1;
        r11.bindInteger(r15, r14);	 Catch:{ Exception -> 0x03ea }
        r10 = r10[r15];	 Catch:{ Exception -> 0x03ea }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x03ea }
        r10 = r18 - r10;
        r10 = java.lang.Math.max(r4, r10);	 Catch:{ Exception -> 0x03ea }
        r11.bindInteger(r13, r10);	 Catch:{ Exception -> 0x03ea }
        r11.bindLong(r12, r8);	 Catch:{ Exception -> 0x03ea }
        r11.step();	 Catch:{ Exception -> 0x03ea }
        r11.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r0 + 1;
        goto L_0x01a6;
    L_0x0223:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r8 = "DELETE FROM messages WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r8 = "DELETE FROM polls WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r8 = "DELETE FROM bot_keyboard WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r8 = "DELETE FROM messages_seq WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r10[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r7 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r7);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x03ea }
        if (r0 == 0) goto L_0x0393;
    L_0x0295:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r5 = "SELECT uid, type FROM media_v2 WHERE mid IN(%s)";
        r7 = 1;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r8[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r3 = java.lang.String.format(r3, r5, r8);	 Catch:{ Exception -> 0x03ea }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x03ea }
        r0 = r0.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x03ea }
        r3 = 0;
    L_0x02ac:
        r5 = r0.next();	 Catch:{ Exception -> 0x03ea }
        if (r5 == 0) goto L_0x02f5;
    L_0x02b2:
        r7 = r0.longValue(r4);	 Catch:{ Exception -> 0x03ea }
        r5 = 1;
        r9 = r0.intValue(r5);	 Catch:{ Exception -> 0x03ea }
        if (r3 != 0) goto L_0x02c2;
    L_0x02bd:
        r3 = new android.util.SparseArray;	 Catch:{ Exception -> 0x03ea }
        r3.<init>();	 Catch:{ Exception -> 0x03ea }
    L_0x02c2:
        r5 = r3.get(r9);	 Catch:{ Exception -> 0x03ea }
        r5 = (android.util.LongSparseArray) r5;	 Catch:{ Exception -> 0x03ea }
        if (r5 != 0) goto L_0x02d8;
    L_0x02ca:
        r5 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03ea }
        r5.<init>();	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r10 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x03ea }
        r3.put(r9, r5);	 Catch:{ Exception -> 0x03ea }
        goto L_0x02df;
    L_0x02d8:
        r9 = r5.get(r7);	 Catch:{ Exception -> 0x03ea }
        r10 = r9;
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x03ea }
    L_0x02df:
        if (r10 != 0) goto L_0x02e6;
    L_0x02e1:
        r4 = 0;
        r10 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x03ea }
    L_0x02e6:
        r9 = r10.intValue();	 Catch:{ Exception -> 0x03ea }
        r10 = 1;
        r9 = r9 + r10;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x03ea }
        r5.put(r7, r9);	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        goto L_0x02ac;
    L_0x02f5:
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        if (r3 == 0) goto L_0x03c5;
    L_0x02fa:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r5 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)";
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x03ea }
        r5 = 0;
    L_0x0303:
        r7 = r3.size();	 Catch:{ Exception -> 0x03ea }
        if (r5 >= r7) goto L_0x038f;
    L_0x0309:
        r7 = r3.keyAt(r5);	 Catch:{ Exception -> 0x03ea }
        r8 = r3.valueAt(r5);	 Catch:{ Exception -> 0x03ea }
        r8 = (android.util.LongSparseArray) r8;	 Catch:{ Exception -> 0x03ea }
        r9 = 0;
    L_0x0314:
        r10 = r8.size();	 Catch:{ Exception -> 0x03ea }
        if (r9 >= r10) goto L_0x0387;
    L_0x031a:
        r10 = r8.keyAt(r9);	 Catch:{ Exception -> 0x03ea }
        r14 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r15 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r4 = "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r12 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x03ea }
        r19 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x03ea }
        r13 = 0;
        r12[r13] = r19;	 Catch:{ Exception -> 0x03ea }
        r19 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x03ea }
        r16 = 1;
        r12[r16] = r19;	 Catch:{ Exception -> 0x03ea }
        r4 = java.lang.String.format(r15, r4, r12);	 Catch:{ Exception -> 0x03ea }
        r12 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x03ea }
        r12 = r14.queryFinalized(r4, r12);	 Catch:{ Exception -> 0x03ea }
        r4 = r12.next();	 Catch:{ Exception -> 0x03ea }
        r14 = -1;
        if (r4 == 0) goto L_0x0352;
    L_0x0346:
        r15 = r12.intValue(r13);	 Catch:{ Exception -> 0x03ea }
        r13 = 1;
        r19 = r12.intValue(r13);	 Catch:{ Exception -> 0x03ea }
        r13 = r19;
        goto L_0x0354;
    L_0x0352:
        r13 = 0;
        r15 = -1;
    L_0x0354:
        r12.dispose();	 Catch:{ Exception -> 0x03ea }
        if (r15 == r14) goto L_0x0380;
    L_0x0359:
        r0.requery();	 Catch:{ Exception -> 0x03ea }
        r12 = r8.valueAt(r9);	 Catch:{ Exception -> 0x03ea }
        r12 = (java.lang.Integer) r12;	 Catch:{ Exception -> 0x03ea }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x03ea }
        r15 = r15 - r12;
        r4 = 0;
        r12 = java.lang.Math.max(r4, r15);	 Catch:{ Exception -> 0x03ea }
        r14 = 1;
        r0.bindLong(r14, r10);	 Catch:{ Exception -> 0x03ea }
        r10 = 2;
        r0.bindInteger(r10, r7);	 Catch:{ Exception -> 0x03ea }
        r11 = 3;
        r0.bindInteger(r11, r12);	 Catch:{ Exception -> 0x03ea }
        r12 = 4;
        r0.bindInteger(r12, r13);	 Catch:{ Exception -> 0x03ea }
        r0.step();	 Catch:{ Exception -> 0x03ea }
        goto L_0x0382;
    L_0x0380:
        r10 = 2;
        r11 = 3;
    L_0x0382:
        r9 = r9 + 1;
        r12 = 3;
        r13 = 2;
        goto L_0x0314;
    L_0x0387:
        r10 = 2;
        r11 = 3;
        r5 = r5 + 1;
        r12 = 3;
        r13 = 2;
        goto L_0x0303;
    L_0x038f:
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        goto L_0x03c5;
    L_0x0393:
        if (r3 != 0) goto L_0x03a5;
    L_0x0395:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r3 = "UPDATE media_counts_v2 SET old = 1 WHERE 1";
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        goto L_0x03c5;
    L_0x03a5:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r7 = "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d";
        r8 = 1;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x03ea }
        r3 = -r3;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r9[r4] = r3;	 Catch:{ Exception -> 0x03ea }
        r3 = java.lang.String.format(r5, r7, r9);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
    L_0x03c5:
        r0 = r1.database;	 Catch:{ Exception -> 0x03ea }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x03ea }
        r5 = "DELETE FROM media_v2 WHERE mid IN(%s)";
        r7 = 1;
        r7 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x03ea }
        r4 = 0;
        r7[r4] = r17;	 Catch:{ Exception -> 0x03ea }
        r3 = java.lang.String.format(r3, r5, r7);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.executeFast(r3);	 Catch:{ Exception -> 0x03ea }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x03ea }
        r0.dispose();	 Catch:{ Exception -> 0x03ea }
        r0 = r20.getMediaDataController();	 Catch:{ Exception -> 0x03ea }
        r3 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x03ea }
        return r6;
    L_0x03ea:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r2 = 0;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.markMessagesAsDeletedInternal(java.util.ArrayList, int):java.util.ArrayList");
    }

    private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, int i) {
        ArrayList<Long> arrayList3 = arrayList2;
        int i2 = i;
        try {
            ArrayList arrayList4 = new ArrayList();
            String str = ",";
            int i3 = 0;
            if (arrayList.isEmpty()) {
                arrayList4.add(Long.valueOf((long) (-i2)));
            } else {
                SQLitePreparedStatement executeFast;
                if (i2 != 0) {
                    arrayList4.add(Long.valueOf((long) (-i2)));
                    executeFast = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?");
                } else {
                    String join = TextUtils.join(str, arrayList);
                    SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[]{join}), new Object[0]);
                    while (queryFinalized.next()) {
                        arrayList4.add(Long.valueOf(queryFinalized.longValue(0)));
                    }
                    queryFinalized.dispose();
                    executeFast = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
                }
                this.database.beginTransaction();
                for (int i4 = 0; i4 < arrayList4.size(); i4++) {
                    long longValue = ((Long) arrayList4.get(i4)).longValue();
                    executeFast.requery();
                    executeFast.bindLong(1, longValue);
                    executeFast.bindLong(2, longValue);
                    executeFast.bindLong(3, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
                this.database.commitTransaction();
            }
            if (arrayList3 != null) {
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    Long l = (Long) arrayList3.get(i5);
                    if (!arrayList4.contains(l)) {
                        arrayList4.add(l);
                    }
                }
            }
            String join2 = TextUtils.join(str, arrayList4);
            TL_messages_dialogs tL_messages_dialogs = new TL_messages_dialogs();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            SQLiteCursor queryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[]{join2}), new Object[0]);
            while (queryFinalized2.next()) {
                Dialog tL_dialogFolder;
                long longValue2 = queryFinalized2.longValue(i3);
                if (DialogObject.isFolderDialogId(longValue2)) {
                    tL_dialogFolder = new TL_dialogFolder();
                    if (!queryFinalized2.isNull(16)) {
                        NativeByteBuffer byteBufferValue = queryFinalized2.byteBufferValue(16);
                        if (byteBufferValue != null) {
                            tL_dialogFolder.folder = TL_folder.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i3), i3);
                        } else {
                            tL_dialogFolder.folder = new TL_folder();
                            tL_dialogFolder.folder.id = queryFinalized2.intValue(15);
                        }
                        byteBufferValue.reuse();
                    }
                } else {
                    tL_dialogFolder = new TL_dialog();
                }
                tL_dialogFolder.id = longValue2;
                tL_dialogFolder.top_message = queryFinalized2.intValue(1);
                tL_dialogFolder.read_inbox_max_id = queryFinalized2.intValue(10);
                tL_dialogFolder.read_outbox_max_id = queryFinalized2.intValue(11);
                tL_dialogFolder.unread_count = queryFinalized2.intValue(2);
                tL_dialogFolder.unread_mentions_count = queryFinalized2.intValue(13);
                tL_dialogFolder.last_message_date = queryFinalized2.intValue(3);
                tL_dialogFolder.pts = queryFinalized2.intValue(9);
                tL_dialogFolder.flags = i2 == 0 ? 0 : 1;
                tL_dialogFolder.pinnedNum = queryFinalized2.intValue(12);
                tL_dialogFolder.pinned = tL_dialogFolder.pinnedNum != 0;
                tL_dialogFolder.unread_mark = (queryFinalized2.intValue(14) & 1) != 0;
                tL_dialogFolder.folder_id = queryFinalized2.intValue(15);
                tL_messages_dialogs.dialogs.add(tL_dialogFolder);
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(4);
                if (byteBufferValue2 != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue2, getUserConfig().clientUserId);
                    byteBufferValue2.reuse();
                    MessageObject.setUnreadFlags(TLdeserialize, queryFinalized2.intValue(5));
                    TLdeserialize.id = queryFinalized2.intValue(6);
                    TLdeserialize.send_state = queryFinalized2.intValue(7);
                    i3 = queryFinalized2.intValue(8);
                    if (i3 != 0) {
                        tL_dialogFolder.last_message_date = i3;
                    }
                    TLdeserialize.dialog_id = tL_dialogFolder.id;
                    tL_messages_dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                }
                i3 = (int) tL_dialogFolder.id;
                int i6 = (int) (tL_dialogFolder.id >> 32);
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
                i3 = 0;
            }
            queryFinalized2.dispose();
            if (!arrayList8.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(str, arrayList8), arrayList5, arrayList6);
            }
            if (!arrayList7.isEmpty()) {
                getChatsInternal(TextUtils.join(str, arrayList7), tL_messages_dialogs.chats);
            }
            if (!arrayList6.isEmpty()) {
                getUsersInternal(TextUtils.join(str, arrayList6), tL_messages_dialogs.users);
            }
            if (!tL_messages_dialogs.dialogs.isEmpty() || !arrayList5.isEmpty()) {
                getMessagesController().processDialogsUpdate(tL_messages_dialogs, arrayList5);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void updateDialogsWithDeletedMessages(ArrayList<Integer> arrayList, ArrayList<Long> arrayList2, boolean z, int i) {
        if (!arrayList.isEmpty() || i != 0) {
            if (z) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ofWf0XUDnI-6IkGXY1H2v-H0H4Q(this, arrayList, arrayList2, i));
            } else {
                updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithDeletedMessages$130$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, int i) {
        updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
    }

    public ArrayList<Long> markMessagesAsDeleted(ArrayList<Integer> arrayList, boolean z, int i) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (!z) {
            return markMessagesAsDeletedInternal((ArrayList) arrayList, i);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ptTzJX6Pd_VNvDwScsxIkJSDi2g(this, arrayList, i));
        return null;
    }

    public /* synthetic */ void lambda$markMessagesAsDeleted$131$MessagesStorage(ArrayList arrayList, int i) {
        markMessagesAsDeletedInternal(arrayList, i);
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(int i, int i2) {
        int i3 = i;
        try {
            ArrayList arrayList = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            long j = ((long) i2) | (((long) i3) << 32);
            ArrayList arrayList2 = new ArrayList();
            int clientUserId = getUserConfig().getClientUserId();
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[2];
            int i4 = 0;
            objArr[0] = Integer.valueOf(-i3);
            objArr[1] = Long.valueOf(j);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE uid = %d AND mid <= %d", objArr), new Object[0]);
            while (queryFinalized.next()) {
                try {
                    long longValue = queryFinalized.longValue(i4);
                    if (longValue != ((long) clientUserId)) {
                        i3 = queryFinalized.intValue(2);
                        if (queryFinalized.intValue(3) == 0) {
                            Integer[] numArr = (Integer[]) longSparseArray.get(longValue);
                            if (numArr == null) {
                                numArr = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                longSparseArray.put(longValue, numArr);
                            }
                            if (i3 < 2) {
                                Integer num = numArr[1];
                                numArr[1] = Integer.valueOf(numArr[1].intValue() + 1);
                            }
                            if (i3 == 0 || i3 == 2) {
                                Integer num2 = numArr[0];
                                numArr[0] = Integer.valueOf(numArr[0].intValue() + 1);
                            }
                        }
                        if (((int) longValue) == 0) {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                byteBufferValue.reuse();
                                if (TLdeserialize != null) {
                                    int i5;
                                    File pathToAttach;
                                    if (TLdeserialize.media instanceof TL_messageMediaPhoto) {
                                        i3 = TLdeserialize.media.photo.sizes.size();
                                        for (i5 = 0; i5 < i3; i5++) {
                                            pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.photo.sizes.get(i5));
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList2.add(pathToAttach);
                                            }
                                        }
                                    } else if (TLdeserialize.media instanceof TL_messageMediaDocument) {
                                        File pathToAttach2 = FileLoader.getPathToAttach(TLdeserialize.media.document);
                                        if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                                            arrayList2.add(pathToAttach2);
                                        }
                                        i3 = TLdeserialize.media.document.thumbs.size();
                                        for (i5 = 0; i5 < i3; i5++) {
                                            pathToAttach = FileLoader.getPathToAttach((PhotoSize) TLdeserialize.media.document.thumbs.get(i5));
                                            if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                                                arrayList2.add(pathToAttach);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    i4 = 0;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
            getFileLoader().deleteFiles(arrayList2, 0);
            for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                int intValue;
                long keyAt = longSparseArray.keyAt(i3);
                Integer[] numArr2 = (Integer[]) longSparseArray.valueAt(i3);
                SQLiteDatabase sQLiteDatabase2 = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                stringBuilder.append(keyAt);
                SQLiteCursor queryFinalized2 = sQLiteDatabase2.queryFinalized(stringBuilder.toString(), new Object[0]);
                if (queryFinalized2.next()) {
                    intValue = queryFinalized2.intValue(0);
                    i4 = queryFinalized2.intValue(1);
                } else {
                    intValue = 0;
                    i4 = 0;
                }
                queryFinalized2.dispose();
                arrayList.add(Long.valueOf(keyAt));
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                executeFast.requery();
                executeFast.bindInteger(1, Math.max(0, intValue - numArr2[0].intValue()));
                executeFast.bindInteger(2, Math.max(0, i4 - numArr2[1].intValue()));
                executeFast.bindLong(3, keyAt);
                executeFast.step();
                executeFast.dispose();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(r13), Long.valueOf(j)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[]{Integer.valueOf(r13), Long.valueOf(j)})).stepThis().dispose();
            this.database.executeFast(String.format(Locale.US, "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d", new Object[]{Integer.valueOf(r13)})).stepThis().dispose();
            return arrayList;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeleted$132$MessagesStorage(int i, int i2) {
        markMessagesAsDeletedInternal(i, i2);
    }

    public ArrayList<Long> markMessagesAsDeleted(int i, int i2, boolean z) {
        if (!z) {
            return markMessagesAsDeletedInternal(i, i2);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$_8iebYtJdUIIvi0ekYJ9L0BlhVg(this, i, i2));
        return null;
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            MessageMedia messageMedia = message.media;
            if (messageMedia instanceof TL_messageMediaUnsupported_old) {
                if (messageMedia.bytes.length == 0) {
                    messageMedia.bytes = new byte[1];
                    messageMedia.bytes[0] = (byte) 104;
                }
            } else if (messageMedia instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                messageMedia = message.media;
                messageMedia.bytes = new byte[1];
                messageMedia.bytes[0] = (byte) 104;
                message.flags |= 512;
            }
        }
    }

    private void doneHolesInTable(String str, long j, int i) throws Exception {
        SQLiteDatabase sQLiteDatabase;
        String str2 = "DELETE FROM ";
        Locale locale;
        StringBuilder stringBuilder;
        if (i == 0) {
            sQLiteDatabase = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d");
            sQLiteDatabase.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } else {
            sQLiteDatabase = this.database;
            locale = Locale.US;
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(str);
            stringBuilder.append(" WHERE uid = %d AND start = 0");
            sQLiteDatabase.executeFast(String.format(locale, stringBuilder.toString(), new Object[]{Long.valueOf(j)})).stepThis().dispose();
        }
        sQLiteDatabase = this.database;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("REPLACE INTO ");
        stringBuilder2.append(str);
        stringBuilder2.append(" VALUES(?, ?, ?)");
        SQLitePreparedStatement executeFast = sQLiteDatabase.executeFast(stringBuilder2.toString());
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, 1);
        executeFast.bindInteger(3, 1);
        executeFast.step();
        executeFast.dispose();
    }

    public void doneHolesInMedia(long j, int i, int i2) throws Exception {
        String str = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        int i3 = 0;
        SQLitePreparedStatement executeFast;
        if (i2 == -1) {
            if (i == 0) {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            } else {
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[]{Long.valueOf(j)})).stepThis().dispose();
            }
            executeFast = this.database.executeFast(str);
            while (i3 < 5) {
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, i3);
                executeFast.bindInteger(3, 1);
                executeFast.bindInteger(4, 1);
                executeFast.step();
                i3++;
            }
            executeFast.dispose();
            return;
        }
        if (i == 0) {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        } else {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[]{Long.valueOf(j), Integer.valueOf(i2)})).stepThis().dispose();
        }
        executeFast = this.database.executeFast(str);
        executeFast.requery();
        executeFast.bindLong(1, j);
        executeFast.bindInteger(2, i2);
        executeFast.bindInteger(3, 1);
        executeFast.bindInteger(4, 1);
        executeFast.step();
        executeFast.dispose();
    }

    public void closeHolesInMedia(long j, int i, int i2, int i3) {
        SQLiteCursor queryFinalized;
        int intValue;
        long j2 = j;
        int i4 = i;
        int i5 = i2;
        int i6 = 4;
        if (i3 < 0) {
            try {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i), Integer.valueOf(i2)}), new Object[0]);
        ArrayList arrayList = null;
        while (queryFinalized.next()) {
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            intValue = queryFinalized.intValue(0);
            int intValue2 = queryFinalized.intValue(1);
            int intValue3 = queryFinalized.intValue(2);
            if (intValue2 != intValue3 || intValue2 != 1) {
                arrayList.add(new Hole(intValue, intValue2, intValue3));
            }
        }
        queryFinalized.dispose();
        if (arrayList != null) {
            intValue = 0;
            while (intValue < arrayList.size()) {
                Hole hole = (Hole) arrayList.get(intValue);
                String str = "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d";
                if (i5 >= hole.end - 1) {
                    if (i4 <= hole.start + 1) {
                        SQLiteDatabase sQLiteDatabase = this.database;
                        Locale locale = Locale.US;
                        Object[] objArr = new Object[i6];
                        objArr[0] = Long.valueOf(j);
                        objArr[1] = Integer.valueOf(hole.type);
                        objArr[2] = Integer.valueOf(hole.start);
                        objArr[3] = Integer.valueOf(hole.end);
                        sQLiteDatabase.executeFast(String.format(locale, str, objArr)).stepThis().dispose();
                        i6 = 4;
                        intValue++;
                    }
                }
                if (i5 >= hole.end - 1) {
                    if (hole.end != i4) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    i6 = 4;
                    intValue++;
                } else if (i4 <= hole.start + 1) {
                    if (hole.start != i5) {
                        try {
                            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        } catch (Exception e22) {
                            FileLog.e(e22);
                        }
                    }
                    i6 = 4;
                    intValue++;
                } else {
                    this.database.executeFast(String.format(Locale.US, str, new Object[]{Long.valueOf(j), Integer.valueOf(hole.type), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                    SQLitePreparedStatement executeFast = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, hole.start);
                    executeFast.bindInteger(4, i4);
                    executeFast.step();
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, hole.type);
                    executeFast.bindInteger(3, i5);
                    i6 = 4;
                    executeFast.bindInteger(4, hole.end);
                    executeFast.step();
                    executeFast.dispose();
                    intValue++;
                }
            }
        }
    }

    private void closeHolesInTable(String str, long j, int i, int i2) {
        String str2 = str;
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        try {
            int intValue;
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT start, end FROM ");
            stringBuilder.append(str2);
            stringBuilder.append(" WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))");
            String stringBuilder2 = stringBuilder.toString();
            r9 = new Object[9];
            int i5 = 1;
            r9[1] = Integer.valueOf(i);
            r9[2] = Integer.valueOf(i2);
            r9[3] = Integer.valueOf(i);
            r9[4] = Integer.valueOf(i2);
            r9[5] = Integer.valueOf(i);
            r9[6] = Integer.valueOf(i2);
            r9[7] = Integer.valueOf(i);
            r9[8] = Integer.valueOf(i2);
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, stringBuilder2, r9), new Object[0]);
            ArrayList arrayList = null;
            while (queryFinalized.next()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                intValue = queryFinalized.intValue(0);
                int intValue2 = queryFinalized.intValue(1);
                if (intValue != intValue2 || intValue != 1) {
                    arrayList.add(new Hole(intValue, intValue2));
                }
            }
            queryFinalized.dispose();
            if (arrayList != null) {
                intValue = 0;
                while (intValue < arrayList.size()) {
                    SQLiteDatabase sQLiteDatabase2;
                    Hole hole = (Hole) arrayList.get(intValue);
                    String str3 = " WHERE uid = %d AND start = %d AND end = %d";
                    String str4 = "DELETE FROM ";
                    if (i4 >= hole.end - i5) {
                        if (i3 <= hole.start + i5) {
                            sQLiteDatabase2 = this.database;
                            Locale locale2 = Locale.US;
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str4);
                            stringBuilder3.append(str2);
                            stringBuilder3.append(str3);
                            sQLiteDatabase2.executeFast(String.format(locale2, stringBuilder3.toString(), new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            intValue++;
                            i5 = 1;
                        }
                    }
                    String str5 = "UPDATE ";
                    Locale locale3;
                    StringBuilder stringBuilder4;
                    if (i4 >= hole.end - 1) {
                        if (hole.end != i3) {
                            try {
                                sQLiteDatabase2 = this.database;
                                locale3 = Locale.US;
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(str5);
                                stringBuilder4.append(str2);
                                stringBuilder4.append(" SET end = %d WHERE uid = %d AND start = %d AND end = %d");
                                sQLiteDatabase2.executeFast(String.format(locale3, stringBuilder4.toString(), new Object[]{Integer.valueOf(i), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        intValue++;
                        i5 = 1;
                    } else if (i3 <= hole.start + 1) {
                        if (hole.start != i4) {
                            try {
                                sQLiteDatabase2 = this.database;
                                locale3 = Locale.US;
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(str5);
                                stringBuilder4.append(str2);
                                stringBuilder4.append(" SET start = %d WHERE uid = %d AND start = %d AND end = %d");
                                sQLiteDatabase2.executeFast(String.format(locale3, stringBuilder4.toString(), new Object[]{Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                        }
                        intValue++;
                        i5 = 1;
                    } else {
                        sQLiteDatabase2 = this.database;
                        Locale locale4 = Locale.US;
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(str4);
                        stringBuilder4.append(str2);
                        stringBuilder4.append(str3);
                        sQLiteDatabase2.executeFast(String.format(locale4, stringBuilder4.toString(), new Object[]{Long.valueOf(j), Integer.valueOf(hole.start), Integer.valueOf(hole.end)})).stepThis().dispose();
                        sQLiteDatabase2 = this.database;
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
                        executeFast.bindLong(1, j2);
                        executeFast.bindInteger(2, i4);
                        executeFast.bindInteger(3, hole.end);
                        executeFast.step();
                        executeFast.dispose();
                        intValue++;
                        i5 = 1;
                    }
                }
            }
        } catch (Exception e22) {
            FileLog.e(e22);
        }
    }

    public void replaceMessageIfExists(Message message, int i, ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z) {
        if (message != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$KKkC2sGsXN_t6FMiw008La2Z6Ic(this, message, z, arrayList, arrayList2, i));
        }
    }

    /* JADX WARNING: Missing block: B:13:0x003c, code skipped:
            if (r5 != null) goto L_0x003e;
     */
    /* JADX WARNING: Missing block: B:14:0x003e, code skipped:
            r5.dispose();
     */
    /* JADX WARNING: Missing block: B:19:0x0049, code skipped:
            if (r5 == null) goto L_0x004c;
     */
    /* JADX WARNING: Missing block: B:21:?, code skipped:
            r1.database.beginTransaction();
            r0 = r1.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
            r5 = r1.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
     */
    /* JADX WARNING: Missing block: B:22:0x0067, code skipped:
            if (r4.dialog_id != 0) goto L_0x006c;
     */
    /* JADX WARNING: Missing block: B:23:0x0069, code skipped:
            org.telegram.messenger.MessageObject.getDialogId(r16);
     */
    /* JADX WARNING: Missing block: B:24:0x006c, code skipped:
            fixUnsupportedMedia(r16);
            r0.requery();
            r8 = new org.telegram.tgnet.NativeByteBuffer(r16.getObjectSize());
            r4.serializeToStream(r8);
            r0.bindLong(1, r2);
            r0.bindLong(2, r4.dialog_id);
            r0.bindInteger(3, org.telegram.messenger.MessageObject.getUnreadFlags(r16));
            r0.bindInteger(4, r4.send_state);
            r0.bindInteger(5, r4.date);
            r0.bindByteBuffer(6, r8);
     */
    /* JADX WARNING: Missing block: B:25:0x00a4, code skipped:
            if (org.telegram.messenger.MessageObject.isOut(r16) == false) goto L_0x00a8;
     */
    /* JADX WARNING: Missing block: B:26:0x00a6, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:27:0x00a8, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:28:0x00a9, code skipped:
            r0.bindInteger(7, r14);
            r0.bindInteger(8, r4.ttl);
     */
    /* JADX WARNING: Missing block: B:29:0x00b9, code skipped:
            if ((r4.flags & 1024) == 0) goto L_0x00c1;
     */
    /* JADX WARNING: Missing block: B:30:0x00bb, code skipped:
            r0.bindInteger(9, r4.views);
     */
    /* JADX WARNING: Missing block: B:31:0x00c1, code skipped:
            r0.bindInteger(9, getMessageMediaType(r16));
     */
    /* JADX WARNING: Missing block: B:32:0x00c8, code skipped:
            r0.bindInteger(10, 0);
     */
    /* JADX WARNING: Missing block: B:33:0x00d1, code skipped:
            if (r4.mentioned == false) goto L_0x00d5;
     */
    /* JADX WARNING: Missing block: B:34:0x00d3, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:35:0x00d5, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:36:0x00d6, code skipped:
            r0.bindInteger(11, r14);
            r0.step();
     */
    /* JADX WARNING: Missing block: B:37:0x00e0, code skipped:
            if (org.telegram.messenger.MediaDataController.canAddMessageToMedia(r16) == false) goto L_0x00ff;
     */
    /* JADX WARNING: Missing block: B:38:0x00e2, code skipped:
            r5.requery();
            r5.bindLong(1, r2);
            r5.bindLong(2, r4.dialog_id);
            r5.bindInteger(3, r4.date);
            r5.bindInteger(4, org.telegram.messenger.MediaDataController.getMediaType(r16));
            r5.bindByteBuffer(5, r8);
            r5.step();
     */
    /* JADX WARNING: Missing block: B:39:0x00ff, code skipped:
            r8.reuse();
            r0.dispose();
            r5.dispose();
            r1.database.commitTransaction();
     */
    /* JADX WARNING: Missing block: B:40:0x010d, code skipped:
            if (r17 == false) goto L_0x0175;
     */
    /* JADX WARNING: Missing block: B:41:0x010f, code skipped:
            r5 = new java.util.HashMap();
            r0 = new java.util.HashMap();
            r2 = 0;
     */
    /* JADX WARNING: Missing block: B:43:0x011e, code skipped:
            if (r2 >= r18.size()) goto L_0x0134;
     */
    /* JADX WARNING: Missing block: B:44:0x0120, code skipped:
            r7 = (org.telegram.tgnet.TLRPC.User) r18.get(r2);
            r5.put(java.lang.Integer.valueOf(r7.id), r7);
            r2 = r2 + 1;
     */
    /* JADX WARNING: Missing block: B:46:0x0138, code skipped:
            if (r6 >= r19.size()) goto L_0x014e;
     */
    /* JADX WARNING: Missing block: B:47:0x013a, code skipped:
            r3 = (org.telegram.tgnet.TLRPC.Chat) r19.get(r6);
            r0.put(java.lang.Integer.valueOf(r3.id), r3);
            r6 = r6 + 1;
     */
    /* JADX WARNING: Missing block: B:48:0x014e, code skipped:
            r2 = new org.telegram.messenger.MessageObject(r20, r16, r5, r0, true);
            r0 = new java.util.ArrayList();
            r0.add(r2);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.-$$Lambda$MessagesStorage$7AwElT-DLipQXVyMJVyZGZ2nitE(r15, r2, r0));
     */
    public /* synthetic */ void lambda$replaceMessageIfExists$134$MessagesStorage(org.telegram.tgnet.TLRPC.Message r16, boolean r17, java.util.ArrayList r18, java.util.ArrayList r19, int r20) {
        /*
        r15 = this;
        r1 = r15;
        r4 = r16;
        r0 = r4.id;	 Catch:{ Exception -> 0x0171 }
        r2 = (long) r0;	 Catch:{ Exception -> 0x0171 }
        r0 = r4.to_id;	 Catch:{ Exception -> 0x0171 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0171 }
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0171 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0171 }
        if (r5 == 0) goto L_0x0015;
    L_0x0010:
        r5 = (long) r0;
        r0 = 32;
        r5 = r5 << r0;
        r2 = r2 | r5;
    L_0x0015:
        r5 = 0;
        r6 = 0;
        r7 = 1;
        r0 = r1.database;	 Catch:{ Exception -> 0x0045 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0045 }
        r9 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0045 }
        r11 = java.lang.Long.valueOf(r2);	 Catch:{ Exception -> 0x0045 }
        r10[r6] = r11;	 Catch:{ Exception -> 0x0045 }
        r8 = java.lang.String.format(r8, r9, r10);	 Catch:{ Exception -> 0x0045 }
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0045 }
        r5 = r0.queryFinalized(r8, r9);	 Catch:{ Exception -> 0x0045 }
        r0 = r5.next();	 Catch:{ Exception -> 0x0045 }
        if (r0 != 0) goto L_0x003c;
    L_0x0036:
        if (r5 == 0) goto L_0x003b;
    L_0x0038:
        r5.dispose();	 Catch:{ Exception -> 0x0171 }
    L_0x003b:
        return;
    L_0x003c:
        if (r5 == 0) goto L_0x004c;
    L_0x003e:
        r5.dispose();	 Catch:{ Exception -> 0x0171 }
        goto L_0x004c;
    L_0x0042:
        r0 = move-exception;
        goto L_0x016b;
    L_0x0045:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0042 }
        if (r5 == 0) goto L_0x004c;
    L_0x004b:
        goto L_0x003e;
    L_0x004c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0171 }
        r0.beginTransaction();	 Catch:{ Exception -> 0x0171 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0171 }
        r5 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x0171 }
        r5 = r1.database;	 Catch:{ Exception -> 0x0171 }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r5 = r5.executeFast(r8);	 Catch:{ Exception -> 0x0171 }
        r8 = r4.dialog_id;	 Catch:{ Exception -> 0x0171 }
        r10 = 0;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x006c;
    L_0x0069:
        org.telegram.messenger.MessageObject.getDialogId(r16);	 Catch:{ Exception -> 0x0171 }
    L_0x006c:
        r15.fixUnsupportedMedia(r16);	 Catch:{ Exception -> 0x0171 }
        r0.requery();	 Catch:{ Exception -> 0x0171 }
        r8 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0171 }
        r9 = r16.getObjectSize();	 Catch:{ Exception -> 0x0171 }
        r8.<init>(r9);	 Catch:{ Exception -> 0x0171 }
        r4.serializeToStream(r8);	 Catch:{ Exception -> 0x0171 }
        r0.bindLong(r7, r2);	 Catch:{ Exception -> 0x0171 }
        r9 = r4.dialog_id;	 Catch:{ Exception -> 0x0171 }
        r11 = 2;
        r0.bindLong(r11, r9);	 Catch:{ Exception -> 0x0171 }
        r9 = org.telegram.messenger.MessageObject.getUnreadFlags(r16);	 Catch:{ Exception -> 0x0171 }
        r10 = 3;
        r0.bindInteger(r10, r9);	 Catch:{ Exception -> 0x0171 }
        r9 = r4.send_state;	 Catch:{ Exception -> 0x0171 }
        r12 = 4;
        r0.bindInteger(r12, r9);	 Catch:{ Exception -> 0x0171 }
        r9 = r4.date;	 Catch:{ Exception -> 0x0171 }
        r13 = 5;
        r0.bindInteger(r13, r9);	 Catch:{ Exception -> 0x0171 }
        r9 = 6;
        r0.bindByteBuffer(r9, r8);	 Catch:{ Exception -> 0x0171 }
        r9 = 7;
        r14 = org.telegram.messenger.MessageObject.isOut(r16);	 Catch:{ Exception -> 0x0171 }
        if (r14 == 0) goto L_0x00a8;
    L_0x00a6:
        r14 = 1;
        goto L_0x00a9;
    L_0x00a8:
        r14 = 0;
    L_0x00a9:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0171 }
        r9 = 8;
        r14 = r4.ttl;	 Catch:{ Exception -> 0x0171 }
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0171 }
        r9 = r4.flags;	 Catch:{ Exception -> 0x0171 }
        r9 = r9 & 1024;
        r14 = 9;
        if (r9 == 0) goto L_0x00c1;
    L_0x00bb:
        r9 = r4.views;	 Catch:{ Exception -> 0x0171 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0171 }
        goto L_0x00c8;
    L_0x00c1:
        r9 = r15.getMessageMediaType(r16);	 Catch:{ Exception -> 0x0171 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0171 }
    L_0x00c8:
        r9 = 10;
        r0.bindInteger(r9, r6);	 Catch:{ Exception -> 0x0171 }
        r9 = 11;
        r14 = r4.mentioned;	 Catch:{ Exception -> 0x0171 }
        if (r14 == 0) goto L_0x00d5;
    L_0x00d3:
        r14 = 1;
        goto L_0x00d6;
    L_0x00d5:
        r14 = 0;
    L_0x00d6:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0171 }
        r0.step();	 Catch:{ Exception -> 0x0171 }
        r9 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r16);	 Catch:{ Exception -> 0x0171 }
        if (r9 == 0) goto L_0x00ff;
    L_0x00e2:
        r5.requery();	 Catch:{ Exception -> 0x0171 }
        r5.bindLong(r7, r2);	 Catch:{ Exception -> 0x0171 }
        r2 = r4.dialog_id;	 Catch:{ Exception -> 0x0171 }
        r5.bindLong(r11, r2);	 Catch:{ Exception -> 0x0171 }
        r2 = r4.date;	 Catch:{ Exception -> 0x0171 }
        r5.bindInteger(r10, r2);	 Catch:{ Exception -> 0x0171 }
        r2 = org.telegram.messenger.MediaDataController.getMediaType(r16);	 Catch:{ Exception -> 0x0171 }
        r5.bindInteger(r12, r2);	 Catch:{ Exception -> 0x0171 }
        r5.bindByteBuffer(r13, r8);	 Catch:{ Exception -> 0x0171 }
        r5.step();	 Catch:{ Exception -> 0x0171 }
    L_0x00ff:
        r8.reuse();	 Catch:{ Exception -> 0x0171 }
        r0.dispose();	 Catch:{ Exception -> 0x0171 }
        r5.dispose();	 Catch:{ Exception -> 0x0171 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0171 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0171 }
        if (r17 == 0) goto L_0x0175;
    L_0x010f:
        r5 = new java.util.HashMap;	 Catch:{ Exception -> 0x0171 }
        r5.<init>();	 Catch:{ Exception -> 0x0171 }
        r0 = new java.util.HashMap;	 Catch:{ Exception -> 0x0171 }
        r0.<init>();	 Catch:{ Exception -> 0x0171 }
        r2 = 0;
    L_0x011a:
        r3 = r18.size();	 Catch:{ Exception -> 0x0171 }
        if (r2 >= r3) goto L_0x0134;
    L_0x0120:
        r3 = r18;
        r7 = r3.get(r2);	 Catch:{ Exception -> 0x0171 }
        r7 = (org.telegram.tgnet.TLRPC.User) r7;	 Catch:{ Exception -> 0x0171 }
        r8 = r7.id;	 Catch:{ Exception -> 0x0171 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0171 }
        r5.put(r8, r7);	 Catch:{ Exception -> 0x0171 }
        r2 = r2 + 1;
        goto L_0x011a;
    L_0x0134:
        r2 = r19.size();	 Catch:{ Exception -> 0x0171 }
        if (r6 >= r2) goto L_0x014e;
    L_0x013a:
        r2 = r19;
        r3 = r2.get(r6);	 Catch:{ Exception -> 0x0171 }
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;	 Catch:{ Exception -> 0x0171 }
        r7 = r3.id;	 Catch:{ Exception -> 0x0171 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0171 }
        r0.put(r7, r3);	 Catch:{ Exception -> 0x0171 }
        r6 = r6 + 1;
        goto L_0x0134;
    L_0x014e:
        r8 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x0171 }
        r7 = 1;
        r2 = r8;
        r3 = r20;
        r4 = r16;
        r6 = r0;
        r2.<init>(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0171 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0171 }
        r0.<init>();	 Catch:{ Exception -> 0x0171 }
        r0.add(r8);	 Catch:{ Exception -> 0x0171 }
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$7AwElT-DLipQXVyMJVyZGZ2nitE;	 Catch:{ Exception -> 0x0171 }
        r2.<init>(r15, r8, r0);	 Catch:{ Exception -> 0x0171 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0171 }
        goto L_0x0175;
    L_0x016b:
        if (r5 == 0) goto L_0x0170;
    L_0x016d:
        r5.dispose();	 Catch:{ Exception -> 0x0171 }
    L_0x0170:
        throw r0;	 Catch:{ Exception -> 0x0171 }
    L_0x0171:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0175:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$134$MessagesStorage(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public /* synthetic */ void lambda$null$133$MessagesStorage(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void putMessages(messages_Messages messages_messages, long j, int i, int i2, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0xu66QqOThg83pHODpCeoxHE76o(this, messages_messages, i, j, i2, z));
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x01cc A:{Catch:{ Exception -> 0x044a }} */
    public /* synthetic */ void lambda$putMessages$135$MessagesStorage(org.telegram.tgnet.TLRPC.messages_Messages r30, int r31, long r32, int r34, boolean r35) {
        /*
        r29 = this;
        r7 = r29;
        r0 = r30;
        r8 = r31;
        r9 = r32;
        r11 = r34;
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x044a }
        if (r1 == 0) goto L_0x001e;
    L_0x0012:
        if (r8 != 0) goto L_0x001d;
    L_0x0014:
        r0 = "messages_holes";
        r7.doneHolesInTable(r0, r9, r11);	 Catch:{ Exception -> 0x044a }
        r0 = -1;
        r7.doneHolesInMedia(r9, r11, r0);	 Catch:{ Exception -> 0x044a }
    L_0x001d:
        return;
    L_0x001e:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r1.beginTransaction();	 Catch:{ Exception -> 0x044a }
        r14 = 3;
        r15 = 2;
        r6 = 1;
        r5 = 0;
        if (r8 != 0) goto L_0x0059;
    L_0x0029:
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r2 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r2 = r2.size();	 Catch:{ Exception -> 0x044a }
        r2 = r2 - r6;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x044a }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x044a }
        r3 = r1.id;	 Catch:{ Exception -> 0x044a }
        r2 = "messages_holes";
        r1 = r29;
        r16 = r3;
        r3 = r32;
        r12 = 0;
        r5 = r16;
        r13 = 1;
        r6 = r34;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x044a }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r16;
        r5 = r34;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x044a }
        goto L_0x00c6;
    L_0x0059:
        r12 = 0;
        r13 = 1;
        if (r8 != r13) goto L_0x0081;
    L_0x005d:
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r1 = r1.get(r12);	 Catch:{ Exception -> 0x044a }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x044a }
        r6 = r1.id;	 Catch:{ Exception -> 0x044a }
        r2 = "messages_holes";
        r1 = r29;
        r3 = r32;
        r5 = r34;
        r16 = r6;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x044a }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r34;
        r5 = r16;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x044a }
        goto L_0x00c6;
    L_0x0081:
        if (r8 == r14) goto L_0x0089;
    L_0x0083:
        if (r8 == r15) goto L_0x0089;
    L_0x0085:
        r1 = 4;
        if (r8 != r1) goto L_0x00c6;
    L_0x0088:
        goto L_0x008a;
    L_0x0089:
        r1 = 4;
    L_0x008a:
        if (r11 != 0) goto L_0x0092;
    L_0x008c:
        if (r8 == r1) goto L_0x0092;
    L_0x008e:
        r11 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x009d;
    L_0x0092:
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r1 = r1.get(r12);	 Catch:{ Exception -> 0x044a }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x044a }
        r1 = r1.id;	 Catch:{ Exception -> 0x044a }
        r11 = r1;
    L_0x009d:
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r2 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r2 = r2.size();	 Catch:{ Exception -> 0x044a }
        r2 = r2 - r13;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x044a }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x044a }
        r6 = r1.id;	 Catch:{ Exception -> 0x044a }
        r2 = "messages_holes";
        r1 = r29;
        r3 = r32;
        r5 = r6;
        r16 = r6;
        r6 = r11;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x044a }
        r6 = -1;
        r1 = r29;
        r2 = r32;
        r4 = r16;
        r5 = r11;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x044a }
    L_0x00c6:
        r1 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r1 = r1.size();	 Catch:{ Exception -> 0x044a }
        r2 = r7.database;	 Catch:{ Exception -> 0x044a }
        r3 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x044a }
        r3 = r7.database;	 Catch:{ Exception -> 0x044a }
        r4 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x044a }
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r17 = 0;
        r18 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x00e5:
        if (r5 >= r1) goto L_0x03c8;
    L_0x00e7:
        r15 = r0.messages;	 Catch:{ Exception -> 0x044a }
        r15 = r15.get(r5);	 Catch:{ Exception -> 0x044a }
        r15 = (org.telegram.tgnet.TLRPC.Message) r15;	 Catch:{ Exception -> 0x044a }
        r14 = r15.id;	 Catch:{ Exception -> 0x044a }
        r12 = (long) r14;	 Catch:{ Exception -> 0x044a }
        if (r4 != 0) goto L_0x00f8;
    L_0x00f4:
        r4 = r15.to_id;	 Catch:{ Exception -> 0x044a }
        r4 = r4.channel_id;	 Catch:{ Exception -> 0x044a }
    L_0x00f8:
        r14 = r15.to_id;	 Catch:{ Exception -> 0x044a }
        r14 = r14.channel_id;	 Catch:{ Exception -> 0x044a }
        if (r14 == 0) goto L_0x0108;
    L_0x00fe:
        r14 = r1;
        r19 = r2;
        r1 = (long) r4;	 Catch:{ Exception -> 0x044a }
        r20 = 32;
        r1 = r1 << r20;
        r12 = r12 | r1;
        goto L_0x010b;
    L_0x0108:
        r14 = r1;
        r19 = r2;
    L_0x010b:
        r1 = -2;
        if (r8 != r1) goto L_0x01d5;
    L_0x010e:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x044a }
        r21 = r4;
        r4 = "SELECT mid, data, ttl, mention, read_state, send_state FROM messages WHERE mid = %d";
        r22 = r14;
        r14 = 1;
        r8 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x044a }
        r14 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x044a }
        r23 = r6;
        r6 = 0;
        r8[r6] = r14;	 Catch:{ Exception -> 0x044a }
        r2 = java.lang.String.format(r2, r4, r8);	 Catch:{ Exception -> 0x044a }
        r4 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x044a }
        r1 = r1.queryFinalized(r2, r4);	 Catch:{ Exception -> 0x044a }
        r2 = r1.next();	 Catch:{ Exception -> 0x044a }
        if (r2 == 0) goto L_0x01c3;
    L_0x0134:
        r4 = 1;
        r8 = r1.byteBufferValue(r4);	 Catch:{ Exception -> 0x044a }
        if (r8 == 0) goto L_0x0164;
    L_0x013b:
        r4 = r8.readInt32(r6);	 Catch:{ Exception -> 0x044a }
        r4 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r4, r6);	 Catch:{ Exception -> 0x044a }
        r6 = r29.getUserConfig();	 Catch:{ Exception -> 0x044a }
        r6 = r6.clientUserId;	 Catch:{ Exception -> 0x044a }
        r4.readAttachPath(r8, r6);	 Catch:{ Exception -> 0x044a }
        r8.reuse();	 Catch:{ Exception -> 0x044a }
        r6 = 5;
        r8 = r1.intValue(r6);	 Catch:{ Exception -> 0x044a }
        if (r4 == 0) goto L_0x0164;
    L_0x0156:
        r6 = 3;
        if (r8 == r6) goto L_0x0164;
    L_0x0159:
        r4 = r4.attachPath;	 Catch:{ Exception -> 0x044a }
        r15.attachPath = r4;	 Catch:{ Exception -> 0x044a }
        r4 = 2;
        r6 = r1.intValue(r4);	 Catch:{ Exception -> 0x044a }
        r15.ttl = r6;	 Catch:{ Exception -> 0x044a }
    L_0x0164:
        r4 = 3;
        r6 = r1.intValue(r4);	 Catch:{ Exception -> 0x044a }
        if (r6 == 0) goto L_0x016d;
    L_0x016b:
        r4 = 1;
        goto L_0x016e;
    L_0x016d:
        r4 = 0;
    L_0x016e:
        r6 = 4;
        r8 = r1.intValue(r6);	 Catch:{ Exception -> 0x044a }
        r6 = r15.mentioned;	 Catch:{ Exception -> 0x044a }
        if (r4 == r6) goto L_0x01c3;
    L_0x0177:
        r6 = r18;
        r14 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r6 != r14) goto L_0x01ae;
    L_0x017e:
        r14 = r7.database;	 Catch:{ Exception -> 0x044a }
        r18 = r6;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x044a }
        r6.<init>();	 Catch:{ Exception -> 0x044a }
        r24 = r11;
        r11 = "SELECT unread_count_i FROM dialogs WHERE did = ";
        r6.append(r11);	 Catch:{ Exception -> 0x044a }
        r6.append(r9);	 Catch:{ Exception -> 0x044a }
        r6 = r6.toString();	 Catch:{ Exception -> 0x044a }
        r25 = r3;
        r11 = 0;
        r3 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x044a }
        r3 = r14.queryFinalized(r6, r3);	 Catch:{ Exception -> 0x044a }
        r6 = r3.next();	 Catch:{ Exception -> 0x044a }
        if (r6 == 0) goto L_0x01aa;
    L_0x01a4:
        r6 = r3.intValue(r11);	 Catch:{ Exception -> 0x044a }
        r18 = r6;
    L_0x01aa:
        r3.dispose();	 Catch:{ Exception -> 0x044a }
        goto L_0x01b4;
    L_0x01ae:
        r25 = r3;
        r18 = r6;
        r24 = r11;
    L_0x01b4:
        if (r4 == 0) goto L_0x01bc;
    L_0x01b6:
        r3 = 1;
        if (r8 > r3) goto L_0x01c7;
    L_0x01b9:
        r18 = r18 + -1;
        goto L_0x01c7;
    L_0x01bc:
        r3 = r15.media_unread;	 Catch:{ Exception -> 0x044a }
        if (r3 == 0) goto L_0x01c7;
    L_0x01c0:
        r18 = r18 + 1;
        goto L_0x01c7;
    L_0x01c3:
        r25 = r3;
        r24 = r11;
    L_0x01c7:
        r1.dispose();	 Catch:{ Exception -> 0x044a }
        if (r2 != 0) goto L_0x01df;
    L_0x01cc:
        r2 = r19;
        r11 = r24;
        r3 = r25;
        r6 = 3;
        goto L_0x03b6;
    L_0x01d5:
        r25 = r3;
        r21 = r4;
        r23 = r6;
        r24 = r11;
        r22 = r14;
    L_0x01df:
        r6 = 7;
        r8 = 6;
        if (r5 != 0) goto L_0x02a4;
    L_0x01e3:
        if (r35 == 0) goto L_0x02a4;
    L_0x01e5:
        r11 = r7.database;	 Catch:{ Exception -> 0x044a }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x044a }
        r14.<init>();	 Catch:{ Exception -> 0x044a }
        r1 = "SELECT pinned, unread_count_i, flags FROM dialogs WHERE did = ";
        r14.append(r1);	 Catch:{ Exception -> 0x044a }
        r14.append(r9);	 Catch:{ Exception -> 0x044a }
        r1 = r14.toString();	 Catch:{ Exception -> 0x044a }
        r14 = 0;
        r2 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x044a }
        r1 = r11.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x044a }
        r2 = r1.next();	 Catch:{ Exception -> 0x044a }
        if (r2 == 0) goto L_0x0219;
    L_0x0205:
        r11 = r1.intValue(r14);	 Catch:{ Exception -> 0x044a }
        r14 = 1;
        r26 = r1.intValue(r14);	 Catch:{ Exception -> 0x044a }
        r14 = 2;
        r27 = r1.intValue(r14);	 Catch:{ Exception -> 0x044a }
        r14 = r11;
        r11 = r26;
        r28 = r27;
        goto L_0x021d;
    L_0x0219:
        r11 = 0;
        r14 = 0;
        r28 = 0;
    L_0x021d:
        r1.dispose();	 Catch:{ Exception -> 0x044a }
        if (r2 == 0) goto L_0x024d;
    L_0x0222:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r2 = "UPDATE dialogs SET date = ?, last_mid = ?, inbox_max = ?, last_mid_i = ?, pts = ?, date_i = ? WHERE did = ?";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x044a }
        r2 = r15.date;	 Catch:{ Exception -> 0x044a }
        r11 = 1;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x044a }
        r2 = 2;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x044a }
        r2 = r15.id;	 Catch:{ Exception -> 0x044a }
        r11 = 3;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x044a }
        r2 = 4;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x044a }
        r2 = r0.pts;	 Catch:{ Exception -> 0x044a }
        r11 = 5;
        r1.bindInteger(r11, r2);	 Catch:{ Exception -> 0x044a }
        r2 = r15.date;	 Catch:{ Exception -> 0x044a }
        r1.bindInteger(r8, r2);	 Catch:{ Exception -> 0x044a }
        r1.bindLong(r6, r9);	 Catch:{ Exception -> 0x044a }
        goto L_0x029e;
    L_0x024d:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r2 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x044a }
        r2 = 1;
        r1.bindLong(r2, r9);	 Catch:{ Exception -> 0x044a }
        r2 = r15.date;	 Catch:{ Exception -> 0x044a }
        r4 = 2;
        r1.bindInteger(r4, r2);	 Catch:{ Exception -> 0x044a }
        r2 = 3;
        r4 = 0;
        r1.bindInteger(r2, r4);	 Catch:{ Exception -> 0x044a }
        r2 = 4;
        r1.bindLong(r2, r12);	 Catch:{ Exception -> 0x044a }
        r2 = r15.id;	 Catch:{ Exception -> 0x044a }
        r3 = 5;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x044a }
        r1.bindInteger(r8, r4);	 Catch:{ Exception -> 0x044a }
        r1.bindLong(r6, r12);	 Catch:{ Exception -> 0x044a }
        r2 = 8;
        r1.bindInteger(r2, r11);	 Catch:{ Exception -> 0x044a }
        r2 = r0.pts;	 Catch:{ Exception -> 0x044a }
        r3 = 9;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x044a }
        r2 = r15.date;	 Catch:{ Exception -> 0x044a }
        r3 = 10;
        r1.bindInteger(r3, r2);	 Catch:{ Exception -> 0x044a }
        r2 = 11;
        r1.bindInteger(r2, r14);	 Catch:{ Exception -> 0x044a }
        r2 = 12;
        r3 = r28;
        r1.bindInteger(r2, r3);	 Catch:{ Exception -> 0x044a }
        r2 = 13;
        r3 = 0;
        r1.bindInteger(r2, r3);	 Catch:{ Exception -> 0x044a }
        r2 = 14;
        r1.bindNull(r2);	 Catch:{ Exception -> 0x044a }
    L_0x029e:
        r1.step();	 Catch:{ Exception -> 0x044a }
        r1.dispose();	 Catch:{ Exception -> 0x044a }
    L_0x02a4:
        r7.fixUnsupportedMedia(r15);	 Catch:{ Exception -> 0x044a }
        r19.requery();	 Catch:{ Exception -> 0x044a }
        r1 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x044a }
        r2 = r15.getObjectSize();	 Catch:{ Exception -> 0x044a }
        r1.<init>(r2);	 Catch:{ Exception -> 0x044a }
        r15.serializeToStream(r1);	 Catch:{ Exception -> 0x044a }
        r2 = r19;
        r3 = 1;
        r2.bindLong(r3, r12);	 Catch:{ Exception -> 0x044a }
        r3 = 2;
        r2.bindLong(r3, r9);	 Catch:{ Exception -> 0x044a }
        r3 = org.telegram.messenger.MessageObject.getUnreadFlags(r15);	 Catch:{ Exception -> 0x044a }
        r4 = 3;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        r3 = r15.send_state;	 Catch:{ Exception -> 0x044a }
        r4 = 4;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        r3 = r15.date;	 Catch:{ Exception -> 0x044a }
        r4 = 5;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        r2.bindByteBuffer(r8, r1);	 Catch:{ Exception -> 0x044a }
        r3 = org.telegram.messenger.MessageObject.isOut(r15);	 Catch:{ Exception -> 0x044a }
        if (r3 == 0) goto L_0x02df;
    L_0x02dd:
        r3 = 1;
        goto L_0x02e0;
    L_0x02df:
        r3 = 0;
    L_0x02e0:
        r2.bindInteger(r6, r3);	 Catch:{ Exception -> 0x044a }
        r3 = r15.ttl;	 Catch:{ Exception -> 0x044a }
        r4 = 8;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        r3 = r15.flags;	 Catch:{ Exception -> 0x044a }
        r3 = r3 & 1024;
        if (r3 == 0) goto L_0x02f8;
    L_0x02f0:
        r3 = r15.views;	 Catch:{ Exception -> 0x044a }
        r4 = 9;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        goto L_0x0301;
    L_0x02f8:
        r4 = 9;
        r3 = r7.getMessageMediaType(r15);	 Catch:{ Exception -> 0x044a }
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
    L_0x0301:
        r3 = 10;
        r4 = 0;
        r2.bindInteger(r3, r4);	 Catch:{ Exception -> 0x044a }
        r3 = r15.mentioned;	 Catch:{ Exception -> 0x044a }
        if (r3 == 0) goto L_0x030d;
    L_0x030b:
        r3 = 1;
        goto L_0x030e;
    L_0x030d:
        r3 = 0;
    L_0x030e:
        r4 = 11;
        r2.bindInteger(r4, r3);	 Catch:{ Exception -> 0x044a }
        r2.step();	 Catch:{ Exception -> 0x044a }
        r3 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r15);	 Catch:{ Exception -> 0x044a }
        if (r3 == 0) goto L_0x033f;
    L_0x031c:
        r25.requery();	 Catch:{ Exception -> 0x044a }
        r3 = r25;
        r4 = 1;
        r3.bindLong(r4, r12);	 Catch:{ Exception -> 0x044a }
        r4 = 2;
        r3.bindLong(r4, r9);	 Catch:{ Exception -> 0x044a }
        r4 = r15.date;	 Catch:{ Exception -> 0x044a }
        r6 = 3;
        r3.bindInteger(r6, r4);	 Catch:{ Exception -> 0x044a }
        r4 = org.telegram.messenger.MediaDataController.getMediaType(r15);	 Catch:{ Exception -> 0x044a }
        r8 = 4;
        r3.bindInteger(r8, r4);	 Catch:{ Exception -> 0x044a }
        r4 = 5;
        r3.bindByteBuffer(r4, r1);	 Catch:{ Exception -> 0x044a }
        r3.step();	 Catch:{ Exception -> 0x044a }
        goto L_0x0343;
    L_0x033f:
        r3 = r25;
        r6 = 3;
        r8 = 4;
    L_0x0343:
        r1.reuse();	 Catch:{ Exception -> 0x044a }
        r1 = r15.media;	 Catch:{ Exception -> 0x044a }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x044a }
        if (r1 == 0) goto L_0x0372;
    L_0x034c:
        if (r24 != 0) goto L_0x0357;
    L_0x034e:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r4 = "REPLACE INTO polls VALUES(?, ?)";
        r11 = r1.executeFast(r4);	 Catch:{ Exception -> 0x044a }
        goto L_0x0359;
    L_0x0357:
        r11 = r24;
    L_0x0359:
        r1 = r15.media;	 Catch:{ Exception -> 0x044a }
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1;	 Catch:{ Exception -> 0x044a }
        r11.requery();	 Catch:{ Exception -> 0x044a }
        r4 = 1;
        r11.bindLong(r4, r12);	 Catch:{ Exception -> 0x044a }
        r1 = r1.poll;	 Catch:{ Exception -> 0x044a }
        r12 = r1.id;	 Catch:{ Exception -> 0x044a }
        r1 = 2;
        r11.bindLong(r1, r12);	 Catch:{ Exception -> 0x044a }
        r11.step();	 Catch:{ Exception -> 0x044a }
        r24 = r11;
        goto L_0x039b;
    L_0x0372:
        r1 = r15.media;	 Catch:{ Exception -> 0x044a }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x044a }
        if (r1 == 0) goto L_0x039b;
    L_0x0378:
        if (r23 != 0) goto L_0x0383;
    L_0x037a:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r4 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r1 = r1.executeFast(r4);	 Catch:{ Exception -> 0x044a }
        goto L_0x0385;
    L_0x0383:
        r1 = r23;
    L_0x0385:
        r1.requery();	 Catch:{ Exception -> 0x044a }
        r4 = r15.media;	 Catch:{ Exception -> 0x044a }
        r4 = r4.webpage;	 Catch:{ Exception -> 0x044a }
        r8 = r4.id;	 Catch:{ Exception -> 0x044a }
        r4 = 1;
        r1.bindLong(r4, r8);	 Catch:{ Exception -> 0x044a }
        r4 = 2;
        r1.bindLong(r4, r12);	 Catch:{ Exception -> 0x044a }
        r1.step();	 Catch:{ Exception -> 0x044a }
        r23 = r1;
    L_0x039b:
        if (r31 != 0) goto L_0x03b0;
    L_0x039d:
        r1 = r7.isValidKeyboardToSave(r15);	 Catch:{ Exception -> 0x044a }
        if (r1 == 0) goto L_0x03b0;
    L_0x03a3:
        r1 = r17;
        if (r1 == 0) goto L_0x03ad;
    L_0x03a7:
        r4 = r1.id;	 Catch:{ Exception -> 0x044a }
        r8 = r15.id;	 Catch:{ Exception -> 0x044a }
        if (r4 >= r8) goto L_0x03b2;
    L_0x03ad:
        r17 = r15;
        goto L_0x03b4;
    L_0x03b0:
        r1 = r17;
    L_0x03b2:
        r17 = r1;
    L_0x03b4:
        r11 = r24;
    L_0x03b6:
        r5 = r5 + 1;
        r8 = r31;
        r9 = r32;
        r4 = r21;
        r1 = r22;
        r6 = r23;
        r12 = 0;
        r13 = 1;
        r14 = 3;
        r15 = 2;
        goto L_0x00e5;
    L_0x03c8:
        r23 = r6;
        r24 = r11;
        r1 = r17;
        r2.dispose();	 Catch:{ Exception -> 0x044a }
        r3.dispose();	 Catch:{ Exception -> 0x044a }
        if (r23 == 0) goto L_0x03d9;
    L_0x03d6:
        r23.dispose();	 Catch:{ Exception -> 0x044a }
    L_0x03d9:
        if (r24 == 0) goto L_0x03de;
    L_0x03db:
        r24.dispose();	 Catch:{ Exception -> 0x044a }
    L_0x03de:
        if (r1 == 0) goto L_0x03ea;
    L_0x03e0:
        r2 = r29.getMediaDataController();	 Catch:{ Exception -> 0x044a }
        r5 = r32;
        r2.putBotKeyboard(r5, r1);	 Catch:{ Exception -> 0x044a }
        goto L_0x03ec;
    L_0x03ea:
        r5 = r32;
    L_0x03ec:
        r1 = r0.users;	 Catch:{ Exception -> 0x044a }
        r7.putUsersInternal(r1);	 Catch:{ Exception -> 0x044a }
        r0 = r0.chats;	 Catch:{ Exception -> 0x044a }
        r7.putChatsInternal(r0);	 Catch:{ Exception -> 0x044a }
        r0 = r18;
        r1 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r0 == r1) goto L_0x0438;
    L_0x03fd:
        r1 = r7.database;	 Catch:{ Exception -> 0x044a }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x044a }
        r3 = "UPDATE dialogs SET unread_count_i = %d WHERE did = %d";
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x044a }
        r9 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x044a }
        r10 = 0;
        r8[r10] = r9;	 Catch:{ Exception -> 0x044a }
        r9 = java.lang.Long.valueOf(r32);	 Catch:{ Exception -> 0x044a }
        r10 = 1;
        r8[r10] = r9;	 Catch:{ Exception -> 0x044a }
        r2 = java.lang.String.format(r2, r3, r8);	 Catch:{ Exception -> 0x044a }
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x044a }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x044a }
        r1.dispose();	 Catch:{ Exception -> 0x044a }
        r1 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x044a }
        r2 = 1;
        r1.<init>(r2);	 Catch:{ Exception -> 0x044a }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x044a }
        r1.put(r5, r0);	 Catch:{ Exception -> 0x044a }
        r0 = r29.getMessagesController();	 Catch:{ Exception -> 0x044a }
        r2 = 0;
        r0.processDialogsUpdateRead(r2, r1);	 Catch:{ Exception -> 0x044a }
    L_0x0438:
        r0 = r7.database;	 Catch:{ Exception -> 0x044a }
        r0.commitTransaction();	 Catch:{ Exception -> 0x044a }
        if (r35 == 0) goto L_0x044e;
    L_0x043f:
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x044a }
        r0.<init>();	 Catch:{ Exception -> 0x044a }
        r1 = 0;
        r2 = 0;
        r7.updateDialogsWithDeletedMessages(r0, r1, r2, r4);	 Catch:{ Exception -> 0x044a }
        goto L_0x044e;
    L_0x044a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x044e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$135$MessagesStorage(org.telegram.tgnet.TLRPC$messages_Messages, int, long, int, boolean):void");
    }

    public static void addUsersAndChatsFromMessage(Message message, ArrayList<Integer> arrayList, ArrayList<Integer> arrayList2) {
        int i;
        int i2;
        int i3 = message.from_id;
        if (i3 != 0) {
            if (i3 > 0) {
                if (!arrayList.contains(Integer.valueOf(i3))) {
                    arrayList.add(Integer.valueOf(message.from_id));
                }
            } else if (!arrayList2.contains(Integer.valueOf(-i3))) {
                arrayList2.add(Integer.valueOf(-message.from_id));
            }
        }
        i3 = message.via_bot_id;
        if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
            arrayList.add(Integer.valueOf(message.via_bot_id));
        }
        MessageAction messageAction = message.action;
        if (messageAction != null) {
            i3 = messageAction.user_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.action.user_id));
            }
            i3 = message.action.channel_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.action.channel_id));
            }
            i3 = message.action.chat_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.action.chat_id));
            }
            if (!message.action.users.isEmpty()) {
                for (i3 = 0; i3 < message.action.users.size(); i3++) {
                    Integer num = (Integer) message.action.users.get(i3);
                    if (!arrayList.contains(num)) {
                        arrayList.add(num);
                    }
                }
            }
        }
        if (!message.entities.isEmpty()) {
            for (i = 0; i < message.entities.size(); i++) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                if (messageEntity instanceof TL_messageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_messageEntityMentionName) messageEntity).user_id));
                } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                    arrayList.add(Integer.valueOf(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id));
                }
            }
        }
        MessageMedia messageMedia = message.media;
        if (messageMedia != null) {
            i3 = messageMedia.user_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.media.user_id));
            }
        }
        MessageFwdHeader messageFwdHeader = message.fwd_from;
        if (messageFwdHeader != null) {
            i3 = messageFwdHeader.from_id;
            if (!(i3 == 0 || arrayList.contains(Integer.valueOf(i3)))) {
                arrayList.add(Integer.valueOf(message.fwd_from.from_id));
            }
            i3 = message.fwd_from.channel_id;
            if (!(i3 == 0 || arrayList2.contains(Integer.valueOf(i3)))) {
                arrayList2.add(Integer.valueOf(message.fwd_from.channel_id));
            }
            Peer peer = message.fwd_from.saved_from_peer;
            if (peer != null) {
                i = peer.user_id;
                if (i == 0) {
                    i2 = peer.channel_id;
                    if (i2 == 0) {
                        i2 = peer.chat_id;
                        if (!(i2 == 0 || arrayList2.contains(Integer.valueOf(i2)))) {
                            arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.chat_id));
                        }
                    } else if (!arrayList2.contains(Integer.valueOf(i2))) {
                        arrayList2.add(Integer.valueOf(message.fwd_from.saved_from_peer.channel_id));
                    }
                } else if (!arrayList2.contains(Integer.valueOf(i))) {
                    arrayList.add(Integer.valueOf(message.fwd_from.saved_from_peer.user_id));
                }
            }
        }
        i2 = message.ttl;
        if (i2 < 0 && !arrayList2.contains(Integer.valueOf(-i2))) {
            arrayList2.add(Integer.valueOf(-message.ttl));
        }
    }

    public void getDialogs(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$t0jaETfRSmP2LwBaraRxDHoXRvQ(this, i, i2, i3));
    }

    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a1 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0265 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0138 A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0136 A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x014f A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x014d A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0166 A:{SYNTHETIC, Splitter:B:54:0x0166} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x018d A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0265 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a1 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a1 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0265 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0265 A:{Catch:{ Exception -> 0x03aa }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02a1 A:{Catch:{ Exception -> 0x03aa }} */
    public /* synthetic */ void lambda$getDialogs$136$MessagesStorage(int r20, int r21, int r22) {
        /*
        r19 = this;
        r1 = r19;
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;
        r12.<init>();
        r13 = new java.util.ArrayList;
        r13.<init>();
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03b0 }
        r2.<init>();	 Catch:{ Exception -> 0x03b0 }
        r0 = r19.getUserConfig();	 Catch:{ Exception -> 0x03b0 }
        r0 = r0.getClientUserId();	 Catch:{ Exception -> 0x03b0 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03b0 }
        r2.add(r0);	 Catch:{ Exception -> 0x03b0 }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03b0 }
        r3.<init>();	 Catch:{ Exception -> 0x03b0 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03b0 }
        r4.<init>();	 Catch:{ Exception -> 0x03b0 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03b0 }
        r5.<init>();	 Catch:{ Exception -> 0x03b0 }
        r6 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x03b0 }
        r6.<init>();	 Catch:{ Exception -> 0x03b0 }
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03b0 }
        r8 = 2;
        r7.<init>(r8);	 Catch:{ Exception -> 0x03b0 }
        r0 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x03b0 }
        r7.add(r0);	 Catch:{ Exception -> 0x03b0 }
        r10 = 0;
    L_0x0042:
        r0 = r7.size();	 Catch:{ Exception -> 0x03b0 }
        r14 = 3;
        if (r10 >= r0) goto L_0x02d7;
    L_0x0049:
        r0 = r7.get(r10);	 Catch:{ Exception -> 0x02d0 }
        r0 = (java.lang.Integer) r0;	 Catch:{ Exception -> 0x02d0 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x02d0 }
        if (r10 != 0) goto L_0x005a;
    L_0x0055:
        r16 = r21;
        r17 = r22;
        goto L_0x0060;
    L_0x005a:
        r16 = 50;
        r16 = 0;
        r17 = 50;
    L_0x0060:
        r11 = r1.database;	 Catch:{ Exception -> 0x02d0 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x02d0 }
        r15 = "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.folder_id = %d ORDER BY d.pinned DESC, d.date DESC LIMIT %d,%d";
        r9 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x02d0 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x02d0 }
        r18 = 0;
        r9[r18] = r0;	 Catch:{ Exception -> 0x02d0 }
        r0 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x02d0 }
        r16 = 1;
        r9[r16] = r0;	 Catch:{ Exception -> 0x02d0 }
        r0 = java.lang.Integer.valueOf(r17);	 Catch:{ Exception -> 0x02d0 }
        r16 = 2;
        r9[r16] = r0;	 Catch:{ Exception -> 0x02d0 }
        r0 = java.lang.String.format(r8, r15, r9);	 Catch:{ Exception -> 0x02d0 }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x02d0 }
        r9 = r11.queryFinalized(r0, r9);	 Catch:{ Exception -> 0x02d0 }
    L_0x008b:
        r0 = r9.next();	 Catch:{ Exception -> 0x02d0 }
        if (r0 == 0) goto L_0x02c0;
    L_0x0091:
        r14 = r9.longValue(r8);	 Catch:{ Exception -> 0x02d0 }
        r0 = org.telegram.messenger.DialogObject.isFolderDialogId(r14);	 Catch:{ Exception -> 0x02d0 }
        if (r0 == 0) goto L_0x00e4;
    L_0x009b:
        r0 = new org.telegram.tgnet.TLRPC$TL_dialogFolder;	 Catch:{ Exception -> 0x03b0 }
        r0.<init>();	 Catch:{ Exception -> 0x03b0 }
        r8 = 18;
        r16 = r9.isNull(r8);	 Catch:{ Exception -> 0x03b0 }
        if (r16 != 0) goto L_0x00ce;
    L_0x00a8:
        r8 = r9.byteBufferValue(r8);	 Catch:{ Exception -> 0x03b0 }
        if (r8 == 0) goto L_0x00bc;
    L_0x00ae:
        r17 = r13;
        r11 = 0;
        r13 = r8.readInt32(r11);	 Catch:{ Exception -> 0x00de }
        r13 = org.telegram.tgnet.TLRPC.TL_folder.TLdeserialize(r8, r13, r11);	 Catch:{ Exception -> 0x00de }
        r0.folder = r13;	 Catch:{ Exception -> 0x00de }
        goto L_0x00ca;
    L_0x00bc:
        r17 = r13;
        r11 = new org.telegram.tgnet.TLRPC$TL_folder;	 Catch:{ Exception -> 0x00de }
        r11.<init>();	 Catch:{ Exception -> 0x00de }
        r0.folder = r11;	 Catch:{ Exception -> 0x00de }
        r11 = r0.folder;	 Catch:{ Exception -> 0x00de }
        r13 = (int) r14;	 Catch:{ Exception -> 0x00de }
        r11.id = r13;	 Catch:{ Exception -> 0x00de }
    L_0x00ca:
        r8.reuse();	 Catch:{ Exception -> 0x00de }
        goto L_0x00d0;
    L_0x00ce:
        r17 = r13;
    L_0x00d0:
        if (r10 != 0) goto L_0x00eb;
    L_0x00d2:
        r8 = r0.folder;	 Catch:{ Exception -> 0x00de }
        r8 = r8.id;	 Catch:{ Exception -> 0x00de }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x00de }
        r7.add(r8);	 Catch:{ Exception -> 0x00de }
        goto L_0x00eb;
    L_0x00de:
        r0 = move-exception;
        r13 = r12;
        r14 = r17;
        goto L_0x03b3;
    L_0x00e4:
        r17 = r13;
        r0 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x02bb }
        r0.<init>();	 Catch:{ Exception -> 0x02bb }
    L_0x00eb:
        r8 = r0;
        r8.id = r14;	 Catch:{ Exception -> 0x02bb }
        r11 = 1;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02bb }
        r8.top_message = r0;	 Catch:{ Exception -> 0x02bb }
        r11 = 2;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02bb }
        r8.unread_count = r0;	 Catch:{ Exception -> 0x02bb }
        r11 = 3;
        r0 = r9.intValue(r11);	 Catch:{ Exception -> 0x02bb }
        r8.last_message_date = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 10;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.pts = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = r8.pts;	 Catch:{ Exception -> 0x02bb }
        if (r0 == 0) goto L_0x0117;
    L_0x010f:
        r13 = r8.id;	 Catch:{ Exception -> 0x00de }
        r0 = (int) r13;
        if (r0 <= 0) goto L_0x0115;
    L_0x0114:
        goto L_0x0117;
    L_0x0115:
        r0 = 1;
        goto L_0x0118;
    L_0x0117:
        r0 = 0;
    L_0x0118:
        r8.flags = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 11;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.read_inbox_max_id = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 12;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.read_outbox_max_id = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 14;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.pinnedNum = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = r8.pinnedNum;	 Catch:{ Exception -> 0x02bb }
        if (r0 == 0) goto L_0x0138;
    L_0x0136:
        r0 = 1;
        goto L_0x0139;
    L_0x0138:
        r0 = 0;
    L_0x0139:
        r8.pinned = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 15;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.unread_mentions_count = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 16;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r13 = 1;
        r0 = r0 & r13;
        if (r0 == 0) goto L_0x014f;
    L_0x014d:
        r0 = 1;
        goto L_0x0150;
    L_0x014f:
        r0 = 0;
    L_0x0150:
        r8.unread_mark = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 8;
        r13 = r9.longValue(r0);	 Catch:{ Exception -> 0x02bb }
        r0 = (int) r13;	 Catch:{ Exception -> 0x02bb }
        r15 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings;	 Catch:{ Exception -> 0x02bb }
        r15.<init>();	 Catch:{ Exception -> 0x02bb }
        r8.notify_settings = r15;	 Catch:{ Exception -> 0x02bb }
        r15 = 1;
        r0 = r0 & r15;
        r15 = 32;
        if (r0 == 0) goto L_0x0179;
    L_0x0166:
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00de }
        r13 = r13 >> r15;
        r14 = (int) r13;	 Catch:{ Exception -> 0x00de }
        r0.mute_until = r14;	 Catch:{ Exception -> 0x00de }
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00de }
        r0 = r0.mute_until;	 Catch:{ Exception -> 0x00de }
        if (r0 != 0) goto L_0x0179;
    L_0x0172:
        r0 = r8.notify_settings;	 Catch:{ Exception -> 0x00de }
        r13 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0.mute_until = r13;	 Catch:{ Exception -> 0x00de }
    L_0x0179:
        r0 = 17;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r8.folder_id = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = r12.dialogs;	 Catch:{ Exception -> 0x02bb }
        r0.add(r8);	 Catch:{ Exception -> 0x02bb }
        r0 = 4;
        r0 = r9.byteBufferValue(r0);	 Catch:{ Exception -> 0x02bb }
        if (r0 == 0) goto L_0x025b;
    L_0x018d:
        r13 = 0;
        r14 = r0.readInt32(r13);	 Catch:{ Exception -> 0x02bb }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r14, r13);	 Catch:{ Exception -> 0x02bb }
        r13 = r19.getUserConfig();	 Catch:{ Exception -> 0x02bb }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x02bb }
        r14.readAttachPath(r0, r13);	 Catch:{ Exception -> 0x02bb }
        r0.reuse();	 Catch:{ Exception -> 0x02bb }
        if (r14 == 0) goto L_0x025b;
    L_0x01a4:
        r0 = 5;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        org.telegram.messenger.MessageObject.setUnreadFlags(r14, r0);	 Catch:{ Exception -> 0x02bb }
        r0 = 6;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r14.id = r0;	 Catch:{ Exception -> 0x02bb }
        r0 = 9;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        if (r0 == 0) goto L_0x01bd;
    L_0x01bb:
        r8.last_message_date = r0;	 Catch:{ Exception -> 0x00de }
    L_0x01bd:
        r0 = 7;
        r0 = r9.intValue(r0);	 Catch:{ Exception -> 0x02bb }
        r14.send_state = r0;	 Catch:{ Exception -> 0x02bb }
        r13 = r12;
        r11 = r8.id;	 Catch:{ Exception -> 0x03aa }
        r14.dialog_id = r11;	 Catch:{ Exception -> 0x03aa }
        r12 = r13;
        r0 = r12.messages;	 Catch:{ Exception -> 0x02bb }
        r0.add(r14);	 Catch:{ Exception -> 0x02bb }
        addUsersAndChatsFromMessage(r14, r2, r3);	 Catch:{ Exception -> 0x02bb }
        r0 = r14.reply_to_msg_id;	 Catch:{ Exception -> 0x0255 }
        if (r0 == 0) goto L_0x025b;
    L_0x01d6:
        r0 = r14.action;	 Catch:{ Exception -> 0x0255 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;	 Catch:{ Exception -> 0x0255 }
        if (r0 != 0) goto L_0x01e8;
    L_0x01dc:
        r0 = r14.action;	 Catch:{ Exception -> 0x0255 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;	 Catch:{ Exception -> 0x0255 }
        if (r0 != 0) goto L_0x01e8;
    L_0x01e2:
        r0 = r14.action;	 Catch:{ Exception -> 0x0255 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;	 Catch:{ Exception -> 0x0255 }
        if (r0 == 0) goto L_0x025b;
    L_0x01e8:
        r0 = 13;
        r11 = r9.isNull(r0);	 Catch:{ Exception -> 0x0255 }
        if (r11 != 0) goto L_0x0227;
    L_0x01f0:
        r0 = r9.byteBufferValue(r0);	 Catch:{ Exception -> 0x0255 }
        if (r0 == 0) goto L_0x0227;
    L_0x01f6:
        r11 = 0;
        r13 = r0.readInt32(r11);	 Catch:{ Exception -> 0x0255 }
        r13 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r13, r11);	 Catch:{ Exception -> 0x0255 }
        r14.replyMessage = r13;	 Catch:{ Exception -> 0x0255 }
        r11 = r14.replyMessage;	 Catch:{ Exception -> 0x0255 }
        r13 = r19.getUserConfig();	 Catch:{ Exception -> 0x0255 }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x0255 }
        r11.readAttachPath(r0, r13);	 Catch:{ Exception -> 0x0255 }
        r0.reuse();	 Catch:{ Exception -> 0x0255 }
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x0255 }
        if (r0 == 0) goto L_0x0227;
    L_0x0213:
        r0 = org.telegram.messenger.MessageObject.isMegagroup(r14);	 Catch:{ Exception -> 0x0255 }
        if (r0 == 0) goto L_0x0222;
    L_0x0219:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x0255 }
        r11 = r0.flags;	 Catch:{ Exception -> 0x0255 }
        r13 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r13;
        r0.flags = r11;	 Catch:{ Exception -> 0x0255 }
    L_0x0222:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x0255 }
        addUsersAndChatsFromMessage(r0, r2, r3);	 Catch:{ Exception -> 0x0255 }
    L_0x0227:
        r0 = r14.replyMessage;	 Catch:{ Exception -> 0x0255 }
        if (r0 != 0) goto L_0x025b;
    L_0x022b:
        r0 = r14.reply_to_msg_id;	 Catch:{ Exception -> 0x0255 }
        r13 = r12;
        r11 = (long) r0;
        r0 = r14.to_id;	 Catch:{ Exception -> 0x0253 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0253 }
        if (r0 == 0) goto L_0x023c;
    L_0x0235:
        r0 = r14.to_id;	 Catch:{ Exception -> 0x0253 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0253 }
        r0 = (long) r0;	 Catch:{ Exception -> 0x0253 }
        r0 = r0 << r15;
        r11 = r11 | r0;
    L_0x023c:
        r0 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0253 }
        r0 = r5.contains(r0);	 Catch:{ Exception -> 0x0253 }
        if (r0 != 0) goto L_0x024d;
    L_0x0246:
        r0 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0253 }
        r5.add(r0);	 Catch:{ Exception -> 0x0253 }
    L_0x024d:
        r0 = r8.id;	 Catch:{ Exception -> 0x0253 }
        r6.put(r0, r14);	 Catch:{ Exception -> 0x0253 }
        goto L_0x025c;
    L_0x0253:
        r0 = move-exception;
        goto L_0x0257;
    L_0x0255:
        r0 = move-exception;
        r13 = r12;
    L_0x0257:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x03aa }
        goto L_0x025c;
    L_0x025b:
        r13 = r12;
    L_0x025c:
        r0 = r8.id;	 Catch:{ Exception -> 0x03aa }
        r1 = (int) r0;	 Catch:{ Exception -> 0x03aa }
        r11 = r8.id;	 Catch:{ Exception -> 0x03aa }
        r11 = r11 >> r15;
        r0 = (int) r11;	 Catch:{ Exception -> 0x03aa }
        if (r1 == 0) goto L_0x02a1;
    L_0x0265:
        r8 = 1;
        if (r0 != r8) goto L_0x027a;
    L_0x0268:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03aa }
        r0 = r3.contains(r0);	 Catch:{ Exception -> 0x03aa }
        if (r0 != 0) goto L_0x02b2;
    L_0x0272:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03aa }
        r3.add(r0);	 Catch:{ Exception -> 0x03aa }
        goto L_0x02b2;
    L_0x027a:
        if (r1 <= 0) goto L_0x028e;
    L_0x027c:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03aa }
        r0 = r2.contains(r0);	 Catch:{ Exception -> 0x03aa }
        if (r0 != 0) goto L_0x02b2;
    L_0x0286:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x03aa }
        r2.add(r0);	 Catch:{ Exception -> 0x03aa }
        goto L_0x02b2;
    L_0x028e:
        r0 = -r1;
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03aa }
        r1 = r3.contains(r1);	 Catch:{ Exception -> 0x03aa }
        if (r1 != 0) goto L_0x02b2;
    L_0x0299:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03aa }
        r3.add(r0);	 Catch:{ Exception -> 0x03aa }
        goto L_0x02b2;
    L_0x02a1:
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03aa }
        r1 = r4.contains(r1);	 Catch:{ Exception -> 0x03aa }
        if (r1 != 0) goto L_0x02b2;
    L_0x02ab:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x03aa }
        r4.add(r0);	 Catch:{ Exception -> 0x03aa }
    L_0x02b2:
        r1 = r19;
        r12 = r13;
        r13 = r17;
        r8 = 0;
        r14 = 3;
        goto L_0x008b;
    L_0x02bb:
        r0 = move-exception;
        r13 = r12;
        r14 = r17;
        goto L_0x02d3;
    L_0x02c0:
        r17 = r13;
        r13 = r12;
        r9.dispose();	 Catch:{ Exception -> 0x03aa }
        r10 = r10 + 1;
        r1 = r19;
        r12 = r13;
        r13 = r17;
        r8 = 2;
        goto L_0x0042;
    L_0x02d0:
        r0 = move-exception;
        r14 = r13;
        r13 = r12;
    L_0x02d3:
        r12 = r19;
        goto L_0x03b4;
    L_0x02d7:
        r17 = r13;
        r13 = r12;
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x03aa }
        r1 = ",";
        if (r0 != 0) goto L_0x0360;
    L_0x02e2:
        r12 = r19;
        r0 = r12.database;	 Catch:{ Exception -> 0x03a8 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x03a8 }
        r8 = "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03a8 }
        r5 = android.text.TextUtils.join(r1, r5);	 Catch:{ Exception -> 0x03a8 }
        r9 = 0;
        r10[r9] = r5;	 Catch:{ Exception -> 0x03a8 }
        r5 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x03a8 }
        r7 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x03a8 }
        r0 = r0.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x03a8 }
    L_0x02fe:
        r5 = r0.next();	 Catch:{ Exception -> 0x03a8 }
        if (r5 == 0) goto L_0x035c;
    L_0x0304:
        r5 = r0.byteBufferValue(r9);	 Catch:{ Exception -> 0x03a8 }
        if (r5 == 0) goto L_0x0356;
    L_0x030a:
        r7 = r5.readInt32(r9);	 Catch:{ Exception -> 0x03a8 }
        r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r7, r9);	 Catch:{ Exception -> 0x03a8 }
        r8 = r19.getUserConfig();	 Catch:{ Exception -> 0x03a8 }
        r8 = r8.clientUserId;	 Catch:{ Exception -> 0x03a8 }
        r7.readAttachPath(r5, r8);	 Catch:{ Exception -> 0x03a8 }
        r5.reuse();	 Catch:{ Exception -> 0x03a8 }
        r5 = 1;
        r8 = r0.intValue(r5);	 Catch:{ Exception -> 0x03a8 }
        r7.id = r8;	 Catch:{ Exception -> 0x03a8 }
        r8 = 2;
        r10 = r0.intValue(r8);	 Catch:{ Exception -> 0x03a8 }
        r7.date = r10;	 Catch:{ Exception -> 0x03a8 }
        r10 = 3;
        r14 = r0.longValue(r10);	 Catch:{ Exception -> 0x03a8 }
        r7.dialog_id = r14;	 Catch:{ Exception -> 0x03a8 }
        addUsersAndChatsFromMessage(r7, r2, r3);	 Catch:{ Exception -> 0x03a8 }
        r14 = r7.dialog_id;	 Catch:{ Exception -> 0x03a8 }
        r11 = r6.get(r14);	 Catch:{ Exception -> 0x03a8 }
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x03a8 }
        if (r11 == 0) goto L_0x0359;
    L_0x0340:
        r11.replyMessage = r7;	 Catch:{ Exception -> 0x03a8 }
        r14 = r11.dialog_id;	 Catch:{ Exception -> 0x03a8 }
        r7.dialog_id = r14;	 Catch:{ Exception -> 0x03a8 }
        r7 = org.telegram.messenger.MessageObject.isMegagroup(r11);	 Catch:{ Exception -> 0x03a8 }
        if (r7 == 0) goto L_0x0359;
    L_0x034c:
        r7 = r11.replyMessage;	 Catch:{ Exception -> 0x03a8 }
        r11 = r7.flags;	 Catch:{ Exception -> 0x03a8 }
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r14;
        r7.flags = r11;	 Catch:{ Exception -> 0x03a8 }
        goto L_0x02fe;
    L_0x0356:
        r5 = 1;
        r8 = 2;
        r10 = 3;
    L_0x0359:
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        goto L_0x02fe;
    L_0x035c:
        r0.dispose();	 Catch:{ Exception -> 0x03a8 }
        goto L_0x0362;
    L_0x0360:
        r12 = r19;
    L_0x0362:
        r0 = r4.isEmpty();	 Catch:{ Exception -> 0x03a8 }
        if (r0 != 0) goto L_0x0372;
    L_0x0368:
        r0 = android.text.TextUtils.join(r1, r4);	 Catch:{ Exception -> 0x03a8 }
        r14 = r17;
        r12.getEncryptedChatsInternal(r0, r14, r2);	 Catch:{ Exception -> 0x03a6 }
        goto L_0x0374;
    L_0x0372:
        r14 = r17;
    L_0x0374:
        r0 = r3.isEmpty();	 Catch:{ Exception -> 0x03a6 }
        if (r0 != 0) goto L_0x0383;
    L_0x037a:
        r0 = android.text.TextUtils.join(r1, r3);	 Catch:{ Exception -> 0x03a6 }
        r3 = r13.chats;	 Catch:{ Exception -> 0x03a6 }
        r12.getChatsInternal(r0, r3);	 Catch:{ Exception -> 0x03a6 }
    L_0x0383:
        r0 = r2.isEmpty();	 Catch:{ Exception -> 0x03a6 }
        if (r0 != 0) goto L_0x0392;
    L_0x0389:
        r0 = android.text.TextUtils.join(r1, r2);	 Catch:{ Exception -> 0x03a6 }
        r1 = r13.users;	 Catch:{ Exception -> 0x03a6 }
        r12.getUsersInternal(r0, r1);	 Catch:{ Exception -> 0x03a6 }
    L_0x0392:
        r2 = r19.getMessagesController();	 Catch:{ Exception -> 0x03a6 }
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r11 = 1;
        r3 = r13;
        r4 = r14;
        r5 = r20;
        r6 = r21;
        r7 = r22;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03a6 }
        goto L_0x03db;
    L_0x03a6:
        r0 = move-exception;
        goto L_0x03b4;
    L_0x03a8:
        r0 = move-exception;
        goto L_0x03ad;
    L_0x03aa:
        r0 = move-exception;
        r12 = r19;
    L_0x03ad:
        r14 = r17;
        goto L_0x03b4;
    L_0x03b0:
        r0 = move-exception;
        r14 = r13;
        r13 = r12;
    L_0x03b3:
        r12 = r1;
    L_0x03b4:
        r1 = r13.dialogs;
        r1.clear();
        r1 = r13.users;
        r1.clear();
        r1 = r13.chats;
        r1.clear();
        r14.clear();
        org.telegram.messenger.FileLog.e(r0);
        r2 = r19.getMessagesController();
        r6 = 0;
        r7 = 100;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r11 = 1;
        r3 = r13;
        r4 = r14;
        r5 = r20;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x03db:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$136$MessagesStorage(int, int, int):void");
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i) throws Exception {
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        sQLitePreparedStatement.bindInteger(2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(3, i);
        sQLitePreparedStatement.step();
        for (int i2 = 0; i2 < 5; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f7  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f5 A:{SYNTHETIC, Splitter:B:35:0x00f5} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x025d A:{Catch:{ Exception -> 0x02ec }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x025b A:{Catch:{ Exception -> 0x02ec }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0287 A:{Catch:{ Exception -> 0x02ec }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0270 A:{Catch:{ Exception -> 0x02ec }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0290 A:{Catch:{ Exception -> 0x02ec }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02af A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0297 A:{Catch:{ Exception -> 0x02ec }} */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC.messages_Dialogs r22, int r23) {
        /*
        r21 = this;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r3 = r1.database;	 Catch:{ Exception -> 0x02f0 }
        r3.beginTransaction();	 Catch:{ Exception -> 0x02f0 }
        r3 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x02f0 }
        r4 = r0.messages;	 Catch:{ Exception -> 0x02f0 }
        r4 = r4.size();	 Catch:{ Exception -> 0x02f0 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x02f0 }
        r5 = 0;
    L_0x0017:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f0 }
        r6 = r6.size();	 Catch:{ Exception -> 0x02f0 }
        if (r5 >= r6) goto L_0x0031;
    L_0x001f:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f0 }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x02f0 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x02f0 }
        r7 = org.telegram.messenger.MessageObject.getDialogId(r6);	 Catch:{ Exception -> 0x02f0 }
        r3.put(r7, r6);	 Catch:{ Exception -> 0x02f0 }
        r5 = r5 + 1;
        goto L_0x0017;
    L_0x0031:
        r5 = r0.dialogs;	 Catch:{ Exception -> 0x02f0 }
        r5 = r5.isEmpty();	 Catch:{ Exception -> 0x02f0 }
        if (r5 != 0) goto L_0x02d6;
    L_0x0039:
        r5 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r6 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r5 = r5.executeFast(r6);	 Catch:{ Exception -> 0x02ec }
        r6 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r7 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r6 = r6.executeFast(r7);	 Catch:{ Exception -> 0x02ec }
        r7 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r7 = r7.executeFast(r8);	 Catch:{ Exception -> 0x02ec }
        r8 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r9 = "REPLACE INTO dialog_settings VALUES(?, ?)";
        r8 = r8.executeFast(r9);	 Catch:{ Exception -> 0x02ec }
        r9 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r10 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r9 = r9.executeFast(r10);	 Catch:{ Exception -> 0x02ec }
        r10 = r1.database;	 Catch:{ Exception -> 0x02ec }
        r11 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r10 = r10.executeFast(r11);	 Catch:{ Exception -> 0x02ec }
        r12 = 0;
        r13 = 0;
    L_0x006b:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02ec }
        r14 = r14.size();	 Catch:{ Exception -> 0x02ec }
        if (r12 >= r14) goto L_0x02bc;
    L_0x0073:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02ec }
        r14 = r14.get(r12);	 Catch:{ Exception -> 0x02ec }
        r14 = (org.telegram.tgnet.TLRPC.Dialog) r14;	 Catch:{ Exception -> 0x02ec }
        org.telegram.messenger.DialogObject.initDialog(r14);	 Catch:{ Exception -> 0x02ec }
        r11 = 1;
        if (r2 != r11) goto L_0x00b1;
    L_0x0081:
        r11 = r1.database;	 Catch:{ Exception -> 0x02f0 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f0 }
        r15.<init>();	 Catch:{ Exception -> 0x02f0 }
        r4 = "SELECT did FROM dialogs WHERE did = ";
        r15.append(r4);	 Catch:{ Exception -> 0x02f0 }
        r4 = r8;
        r16 = r9;
        r8 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r15.append(r8);	 Catch:{ Exception -> 0x02f0 }
        r8 = r15.toString();	 Catch:{ Exception -> 0x02f0 }
        r9 = 0;
        r15 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x02f0 }
        r8 = r11.queryFinalized(r8, r15);	 Catch:{ Exception -> 0x02f0 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f0 }
        r8.dispose();	 Catch:{ Exception -> 0x02f0 }
        if (r9 == 0) goto L_0x00e8;
    L_0x00a9:
        r19 = r3;
        r15 = r12;
        r9 = r16;
        r1 = 0;
        goto L_0x02af;
    L_0x00b1:
        r4 = r8;
        r16 = r9;
        r8 = r14.pinned;	 Catch:{ Exception -> 0x02ec }
        if (r8 == 0) goto L_0x00e8;
    L_0x00b8:
        r8 = 2;
        if (r2 != r8) goto L_0x00e8;
    L_0x00bb:
        r8 = r1.database;	 Catch:{ Exception -> 0x02f0 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f0 }
        r9.<init>();	 Catch:{ Exception -> 0x02f0 }
        r11 = "SELECT pinned FROM dialogs WHERE did = ";
        r9.append(r11);	 Catch:{ Exception -> 0x02f0 }
        r15 = r12;
        r11 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r9.append(r11);	 Catch:{ Exception -> 0x02f0 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x02f0 }
        r11 = 0;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x02f0 }
        r8 = r8.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x02f0 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f0 }
        if (r9 == 0) goto L_0x00e4;
    L_0x00de:
        r9 = r8.intValue(r11);	 Catch:{ Exception -> 0x02f0 }
        r14.pinnedNum = r9;	 Catch:{ Exception -> 0x02f0 }
    L_0x00e4:
        r8.dispose();	 Catch:{ Exception -> 0x02f0 }
        goto L_0x00e9;
    L_0x00e8:
        r15 = r12;
    L_0x00e9:
        r8 = r14.id;	 Catch:{ Exception -> 0x02ec }
        r8 = r3.get(r8);	 Catch:{ Exception -> 0x02ec }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x02ec }
        r17 = 32;
        if (r8 == 0) goto L_0x01f7;
    L_0x00f5:
        r9 = r8.date;	 Catch:{ Exception -> 0x02f0 }
        r11 = 0;
        r9 = java.lang.Math.max(r9, r11);	 Catch:{ Exception -> 0x02f0 }
        r11 = r1.isValidKeyboardToSave(r8);	 Catch:{ Exception -> 0x02f0 }
        if (r11 == 0) goto L_0x010e;
    L_0x0102:
        r11 = r21.getMediaDataController();	 Catch:{ Exception -> 0x02f0 }
        r18 = r13;
        r12 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r11.putBotKeyboard(r12, r8);	 Catch:{ Exception -> 0x02f0 }
        goto L_0x0110;
    L_0x010e:
        r18 = r13;
    L_0x0110:
        r1.fixUnsupportedMedia(r8);	 Catch:{ Exception -> 0x02f0 }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02f0 }
        r12 = r8.getObjectSize();	 Catch:{ Exception -> 0x02f0 }
        r11.<init>(r12);	 Catch:{ Exception -> 0x02f0 }
        r8.serializeToStream(r11);	 Catch:{ Exception -> 0x02f0 }
        r12 = r8.id;	 Catch:{ Exception -> 0x02f0 }
        r12 = (long) r12;	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f0 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f0 }
        if (r2 == 0) goto L_0x0133;
    L_0x0128:
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f0 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f0 }
        r19 = r3;
        r2 = (long) r2;	 Catch:{ Exception -> 0x02f0 }
        r2 = r2 << r17;
        r12 = r12 | r2;
        goto L_0x0135;
    L_0x0133:
        r19 = r3;
    L_0x0135:
        r5.requery();	 Catch:{ Exception -> 0x02f0 }
        r2 = 1;
        r5.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f0 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r20 = r9;
        r9 = 2;
        r5.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = org.telegram.messenger.MessageObject.getUnreadFlags(r8);	 Catch:{ Exception -> 0x02f0 }
        r3 = 3;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.send_state;	 Catch:{ Exception -> 0x02f0 }
        r3 = 4;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f0 }
        r3 = 5;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = 6;
        r5.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f0 }
        r2 = org.telegram.messenger.MessageObject.isOut(r8);	 Catch:{ Exception -> 0x02f0 }
        if (r2 == 0) goto L_0x0164;
    L_0x0162:
        r2 = 1;
        goto L_0x0165;
    L_0x0164:
        r2 = 0;
    L_0x0165:
        r3 = 7;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = 8;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.flags;	 Catch:{ Exception -> 0x02f0 }
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x0178;
    L_0x0175:
        r2 = r8.views;	 Catch:{ Exception -> 0x02f0 }
        goto L_0x0179;
    L_0x0178:
        r2 = 0;
    L_0x0179:
        r3 = 9;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = 10;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.mentioned;	 Catch:{ Exception -> 0x02f0 }
        if (r2 == 0) goto L_0x018a;
    L_0x0188:
        r2 = 1;
        goto L_0x018b;
    L_0x018a:
        r2 = 0;
    L_0x018b:
        r3 = 11;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r5.step();	 Catch:{ Exception -> 0x02f0 }
        r2 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r8);	 Catch:{ Exception -> 0x02f0 }
        if (r2 == 0) goto L_0x01bb;
    L_0x0199:
        r7.requery();	 Catch:{ Exception -> 0x02f0 }
        r2 = 1;
        r7.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f0 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r9 = 2;
        r7.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f0 }
        r3 = 3;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = org.telegram.messenger.MediaDataController.getMediaType(r8);	 Catch:{ Exception -> 0x02f0 }
        r3 = 4;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f0 }
        r2 = 5;
        r7.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f0 }
        r7.step();	 Catch:{ Exception -> 0x02f0 }
    L_0x01bb:
        r11.reuse();	 Catch:{ Exception -> 0x02f0 }
        r2 = r8.media;	 Catch:{ Exception -> 0x02f0 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x02f0 }
        if (r2 == 0) goto L_0x01e9;
    L_0x01c4:
        if (r18 != 0) goto L_0x01cf;
    L_0x01c6:
        r2 = r1.database;	 Catch:{ Exception -> 0x02f0 }
        r3 = "REPLACE INTO polls VALUES(?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x02f0 }
        goto L_0x01d1;
    L_0x01cf:
        r2 = r18;
    L_0x01d1:
        r3 = r8.media;	 Catch:{ Exception -> 0x02f0 }
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;	 Catch:{ Exception -> 0x02f0 }
        r2.requery();	 Catch:{ Exception -> 0x02f0 }
        r9 = 1;
        r2.bindLong(r9, r12);	 Catch:{ Exception -> 0x02f0 }
        r3 = r3.poll;	 Catch:{ Exception -> 0x02f0 }
        r11 = r3.id;	 Catch:{ Exception -> 0x02f0 }
        r3 = 2;
        r2.bindLong(r3, r11);	 Catch:{ Exception -> 0x02f0 }
        r2.step();	 Catch:{ Exception -> 0x02f0 }
        r13 = r2;
        goto L_0x01eb;
    L_0x01e9:
        r13 = r18;
    L_0x01eb:
        r2 = r14.id;	 Catch:{ Exception -> 0x02f0 }
        r8 = r8.id;	 Catch:{ Exception -> 0x02f0 }
        r9 = r16;
        createFirstHoles(r2, r9, r10, r8);	 Catch:{ Exception -> 0x02f0 }
        r2 = r20;
        goto L_0x01fe;
    L_0x01f7:
        r19 = r3;
        r18 = r13;
        r9 = r16;
        r2 = 0;
    L_0x01fe:
        r3 = r14.top_message;	 Catch:{ Exception -> 0x02ec }
        r11 = (long) r3;	 Catch:{ Exception -> 0x02ec }
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ec }
        if (r3 == 0) goto L_0x0213;
    L_0x0205:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ec }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02ec }
        if (r3 == 0) goto L_0x0213;
    L_0x020b:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02ec }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02ec }
        r0 = (long) r3;	 Catch:{ Exception -> 0x02ec }
        r0 = r0 << r17;
        r11 = r11 | r0;
    L_0x0213:
        r6.requery();	 Catch:{ Exception -> 0x02ec }
        r0 = r14.id;	 Catch:{ Exception -> 0x02ec }
        r3 = 1;
        r6.bindLong(r3, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = 2;
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.unread_count;	 Catch:{ Exception -> 0x02ec }
        r1 = 3;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = 4;
        r6.bindLong(r0, r11);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.read_inbox_max_id;	 Catch:{ Exception -> 0x02ec }
        r1 = 5;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.read_outbox_max_id;	 Catch:{ Exception -> 0x02ec }
        r1 = 6;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = 0;
        r2 = 7;
        r6.bindLong(r2, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.unread_mentions_count;	 Catch:{ Exception -> 0x02ec }
        r1 = 8;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.pts;	 Catch:{ Exception -> 0x02ec }
        r1 = 9;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = 10;
        r1 = 0;
        r6.bindInteger(r0, r1);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.pinnedNum;	 Catch:{ Exception -> 0x02ec }
        r2 = 11;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = r14.unread_mark;	 Catch:{ Exception -> 0x02ec }
        if (r0 == 0) goto L_0x025d;
    L_0x025b:
        r0 = 1;
        goto L_0x025e;
    L_0x025d:
        r0 = 0;
    L_0x025e:
        r2 = 12;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ec }
        r0 = 13;
        r2 = r14.folder_id;	 Catch:{ Exception -> 0x02ec }
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02ec }
        r0 = r14 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;	 Catch:{ Exception -> 0x02ec }
        r2 = 14;
        if (r0 == 0) goto L_0x0287;
    L_0x0270:
        r0 = r14;
        r0 = (org.telegram.tgnet.TLRPC.TL_dialogFolder) r0;	 Catch:{ Exception -> 0x02ec }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02ec }
        r3 = r0.folder;	 Catch:{ Exception -> 0x02ec }
        r3 = r3.getObjectSize();	 Catch:{ Exception -> 0x02ec }
        r11.<init>(r3);	 Catch:{ Exception -> 0x02ec }
        r0 = r0.folder;	 Catch:{ Exception -> 0x02ec }
        r0.serializeToStream(r11);	 Catch:{ Exception -> 0x02ec }
        r6.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02ec }
        goto L_0x028b;
    L_0x0287:
        r6.bindNull(r2);	 Catch:{ Exception -> 0x02ec }
        r11 = 0;
    L_0x028b:
        r6.step();	 Catch:{ Exception -> 0x02ec }
        if (r11 == 0) goto L_0x0293;
    L_0x0290:
        r11.reuse();	 Catch:{ Exception -> 0x02ec }
    L_0x0293:
        r0 = r14.notify_settings;	 Catch:{ Exception -> 0x02ec }
        if (r0 == 0) goto L_0x02af;
    L_0x0297:
        r4.requery();	 Catch:{ Exception -> 0x02ec }
        r2 = r14.id;	 Catch:{ Exception -> 0x02ec }
        r0 = 1;
        r4.bindLong(r0, r2);	 Catch:{ Exception -> 0x02ec }
        r2 = r14.notify_settings;	 Catch:{ Exception -> 0x02ec }
        r2 = r2.mute_until;	 Catch:{ Exception -> 0x02ec }
        if (r2 == 0) goto L_0x02a7;
    L_0x02a6:
        goto L_0x02a8;
    L_0x02a7:
        r0 = 0;
    L_0x02a8:
        r2 = 2;
        r4.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02ec }
        r4.step();	 Catch:{ Exception -> 0x02ec }
    L_0x02af:
        r12 = r15 + 1;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r8 = r4;
        r3 = r19;
        goto L_0x006b;
    L_0x02bc:
        r4 = r8;
        r18 = r13;
        r5.dispose();	 Catch:{ Exception -> 0x02ec }
        r6.dispose();	 Catch:{ Exception -> 0x02ec }
        r7.dispose();	 Catch:{ Exception -> 0x02ec }
        r4.dispose();	 Catch:{ Exception -> 0x02ec }
        r9.dispose();	 Catch:{ Exception -> 0x02ec }
        r10.dispose();	 Catch:{ Exception -> 0x02ec }
        if (r18 == 0) goto L_0x02d6;
    L_0x02d3:
        r18.dispose();	 Catch:{ Exception -> 0x02ec }
    L_0x02d6:
        r0 = r22;
        r1 = r0.users;	 Catch:{ Exception -> 0x02ec }
        r2 = r21;
        r2.putUsersInternal(r1);	 Catch:{ Exception -> 0x02ea }
        r0 = r0.chats;	 Catch:{ Exception -> 0x02ea }
        r2.putChatsInternal(r0);	 Catch:{ Exception -> 0x02ea }
        r0 = r2.database;	 Catch:{ Exception -> 0x02ea }
        r0.commitTransaction();	 Catch:{ Exception -> 0x02ea }
        goto L_0x02f5;
    L_0x02ea:
        r0 = move-exception;
        goto L_0x02f2;
    L_0x02ec:
        r0 = move-exception;
        r2 = r21;
        goto L_0x02f2;
    L_0x02f0:
        r0 = move-exception;
        r2 = r1;
    L_0x02f2:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02f5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    public void getDialogFolderId(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$t_jq6Paav5KKR5kzd1LlVrcehvI(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getDialogFolderId$138$MessagesStorage(long j, IntCallback intCallback) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
            int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$XsoFtSzS0YToICW3cjox1fH80xY(intCallback, intValue));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogsFolderId(ArrayList<TL_folderPeer> arrayList, ArrayList<TL_inputFolderPeer> arrayList2, long j, int i) {
        if (arrayList != null || arrayList2 != null || j != 0) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$R773_K8RtbzYzQJ4L6bSJxmWu-I(this, arrayList, arrayList2, i, j));
        }
    }

    public /* synthetic */ void lambda$setDialogsFolderId$139$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        try {
            this.database.beginTransaction();
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ? WHERE did = ?");
            int i2 = 0;
            if (arrayList != null) {
                int size = arrayList.size();
                while (i2 < size) {
                    TL_folderPeer tL_folderPeer = (TL_folderPeer) arrayList.get(i2);
                    j = DialogObject.getPeerDialogId(tL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_folderPeer.folder_id);
                    executeFast.bindLong(2, j);
                    executeFast.step();
                    i2++;
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                while (i2 < size2) {
                    TL_inputFolderPeer tL_inputFolderPeer = (TL_inputFolderPeer) arrayList2.get(i2);
                    j = DialogObject.getPeerDialogId(tL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_inputFolderPeer.folder_id);
                    executeFast.bindLong(2, j);
                    executeFast.step();
                    i2++;
                }
            } else {
                executeFast.requery();
                executeFast.bindInteger(1, i);
                executeFast.bindLong(2, j);
                executeFast.step();
            }
            executeFast.dispose();
            this.database.commitTransaction();
            checkIfFolderEmptyInternal(1);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void checkIfFolderEmptyInternal(int i) {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(i));
            if (!queryFinalized.next()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$pS4-TFYlNVgaHrQVJbRd8fOrGwo(this, i));
                SQLiteDatabase sQLiteDatabase = this.database;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM dialogs WHERE did = ");
                stringBuilder.append(DialogObject.makeFolderDialogId(i));
                sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$checkIfFolderEmptyInternal$140$MessagesStorage(int i) {
        getMessagesController().onFolderEmpty(i);
    }

    public void checkIfFolderEmpty(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$XOiv1SwjzIOrCmL5Vk0FOP6bXno(this, i));
    }

    public /* synthetic */ void lambda$checkIfFolderEmpty$141$MessagesStorage(int i) {
        checkIfFolderEmptyInternal(i);
    }

    public void unpinAllDialogsExceptNew(ArrayList<Long> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Ddew0CEJ_p2AJHgB5NoeH3eCdQw(this, arrayList, i));
    }

    public /* synthetic */ void lambda$unpinAllDialogsExceptNew$142$MessagesStorage(ArrayList arrayList, int i) {
        try {
            long longValue;
            ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did, folder_id FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                longValue = queryFinalized.longValue(0);
                if (!(queryFinalized.intValue(1) != i || ((int) longValue) == 0 || DialogObject.isFolderDialogId(longValue))) {
                    arrayList2.add(Long.valueOf(queryFinalized.longValue(0)));
                }
            }
            queryFinalized.dispose();
            if (!arrayList2.isEmpty()) {
                SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                for (i = 0; i < arrayList2.size(); i++) {
                    longValue = ((Long) arrayList2.get(i)).longValue();
                    executeFast.requery();
                    executeFast.bindInteger(1, 0);
                    executeFast.bindLong(2, longValue);
                    executeFast.step();
                }
                executeFast.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setDialogUnread(long j, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$CKiP2jQhP3bMHRkic2tVwmgJfig(this, j, z));
    }

    /* JADX WARNING: Missing block: B:5:0x0025, code skipped:
            if (r0 != null) goto L_0x0027;
     */
    /* JADX WARNING: Missing block: B:7:?, code skipped:
            r0.dispose();
     */
    /* JADX WARNING: Missing block: B:12:0x0031, code skipped:
            if (r0 == null) goto L_0x0034;
     */
    /* JADX WARNING: Missing block: B:14:0x0035, code skipped:
            if (r8 == false) goto L_0x003a;
     */
    /* JADX WARNING: Missing block: B:15:0x0037, code skipped:
            r8 = r1 | 1;
     */
    /* JADX WARNING: Missing block: B:16:0x003a, code skipped:
            r8 = r1 & -2;
     */
    /* JADX WARNING: Missing block: B:18:?, code skipped:
            r1 = r5.database.executeFast("UPDATE dialogs SET flags = ? WHERE did = ?");
            r1.bindInteger(1, r8);
            r1.bindLong(2, r6);
            r1.step();
            r1.dispose();
     */
    /* JADX WARNING: Missing block: B:25:?, code skipped:
            return;
     */
    public /* synthetic */ void lambda$setDialogUnread$143$MessagesStorage(long r6, boolean r8) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 0;
        r2 = r5.database;	 Catch:{ Exception -> 0x002d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x002d }
        r3.<init>();	 Catch:{ Exception -> 0x002d }
        r4 = "SELECT flags FROM dialogs WHERE did = ";
        r3.append(r4);	 Catch:{ Exception -> 0x002d }
        r3.append(r6);	 Catch:{ Exception -> 0x002d }
        r3 = r3.toString();	 Catch:{ Exception -> 0x002d }
        r4 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x002d }
        r0 = r2.queryFinalized(r3, r4);	 Catch:{ Exception -> 0x002d }
        r2 = r0.next();	 Catch:{ Exception -> 0x002d }
        if (r2 == 0) goto L_0x0025;
    L_0x0021:
        r1 = r0.intValue(r1);	 Catch:{ Exception -> 0x002d }
    L_0x0025:
        if (r0 == 0) goto L_0x0034;
    L_0x0027:
        r0.dispose();	 Catch:{ Exception -> 0x0058 }
        goto L_0x0034;
    L_0x002b:
        r6 = move-exception;
        goto L_0x0052;
    L_0x002d:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x002b }
        if (r0 == 0) goto L_0x0034;
    L_0x0033:
        goto L_0x0027;
    L_0x0034:
        r0 = 1;
        if (r8 == 0) goto L_0x003a;
    L_0x0037:
        r8 = r1 | 1;
        goto L_0x003c;
    L_0x003a:
        r8 = r1 & -2;
    L_0x003c:
        r1 = r5.database;	 Catch:{ Exception -> 0x0058 }
        r2 = "UPDATE dialogs SET flags = ? WHERE did = ?";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0058 }
        r1.bindInteger(r0, r8);	 Catch:{ Exception -> 0x0058 }
        r8 = 2;
        r1.bindLong(r8, r6);	 Catch:{ Exception -> 0x0058 }
        r1.step();	 Catch:{ Exception -> 0x0058 }
        r1.dispose();	 Catch:{ Exception -> 0x0058 }
        goto L_0x005c;
    L_0x0052:
        if (r0 == 0) goto L_0x0057;
    L_0x0054:
        r0.dispose();	 Catch:{ Exception -> 0x0058 }
    L_0x0057:
        throw r6;	 Catch:{ Exception -> 0x0058 }
    L_0x0058:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x005c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$setDialogUnread$143$MessagesStorage(long, boolean):void");
    }

    public void setDialogPinned(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$AiDYTHRLDJEhuW3_82Vb-TsqO_c(this, i, j));
    }

    public /* synthetic */ void lambda$setDialogPinned$144$MessagesStorage(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putDialogs(messages_Dialogs messages_dialogs, int i) {
        if (!messages_dialogs.dialogs.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$SP4NWDIhBs89x83JhEQbPgHfl_0(this, messages_dialogs, i));
        }
    }

    public /* synthetic */ void lambda$putDialogs$145$MessagesStorage(messages_Dialogs messages_dialogs, int i) {
        putDialogsInternal(messages_dialogs, i);
        try {
            loadUnreadMessages();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public int getDialogReadMax(boolean z, long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$oo0v__1RdN_rO0e5kqW6A04D4Ao(this, z, j, numArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$getDialogReadMax$146$MessagesStorage(boolean r5, long r6, java.lang.Integer[] r8, java.util.concurrent.CountDownLatch r9) {
        /*
        r4 = this;
        r0 = 0;
        r1 = 0;
        if (r5 == 0) goto L_0x001e;
    L_0x0004:
        r5 = r4.database;	 Catch:{ Exception -> 0x004d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x004d }
        r2.<init>();	 Catch:{ Exception -> 0x004d }
        r3 = "SELECT outbox_max FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x004d }
        r2.append(r3);	 Catch:{ Exception -> 0x004d }
        r2.append(r6);	 Catch:{ Exception -> 0x004d }
        r6 = r2.toString();	 Catch:{ Exception -> 0x004d }
        r7 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x004d }
        r5 = r5.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x004d }
        goto L_0x0037;	 Catch:{ Exception -> 0x004d }
    L_0x001e:
        r5 = r4.database;	 Catch:{ Exception -> 0x004d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x004d }
        r2.<init>();	 Catch:{ Exception -> 0x004d }
        r3 = "SELECT inbox_max FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x004d }
        r2.append(r3);	 Catch:{ Exception -> 0x004d }
        r2.append(r6);	 Catch:{ Exception -> 0x004d }
        r6 = r2.toString();	 Catch:{ Exception -> 0x004d }
        r7 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x004d }
        r5 = r5.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x004d }
    L_0x0037:
        r1 = r5;	 Catch:{ Exception -> 0x004d }
        r5 = r1.next();	 Catch:{ Exception -> 0x004d }
        if (r5 == 0) goto L_0x0048;	 Catch:{ Exception -> 0x004d }
    L_0x003e:
        r5 = r1.intValue(r0);	 Catch:{ Exception -> 0x004d }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x004d }
        r8[r0] = r5;	 Catch:{ Exception -> 0x004d }
    L_0x0048:
        if (r1 == 0) goto L_0x0056;
    L_0x004a:
        goto L_0x0053;
    L_0x004b:
        r5 = move-exception;
        goto L_0x005a;
    L_0x004d:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x004b }
        if (r1 == 0) goto L_0x0056;
    L_0x0053:
        r1.dispose();
    L_0x0056:
        r9.countDown();
        return;
    L_0x005a:
        if (r1 == 0) goto L_0x005f;
    L_0x005c:
        r1.dispose();
    L_0x005f:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogReadMax$146$MessagesStorage(boolean, long, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public int getChannelPtsSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Integer[] numArr = new Integer[]{Integer.valueOf(0)};
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$dKOeLXgsGhw-RvlccPAsrIwQZqA(this, i, numArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return numArr[0].intValue();
    }

    /*  JADX ERROR: NullPointerException in pass: ProcessVariables
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:278)
        	at jadx.core.dex.visitors.regions.ProcessVariables.access$000(ProcessVariables.java:31)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
        	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
        	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1082)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:57)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
        	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003c A:{SYNTHETIC, Splitter:B:11:0x003c} */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003c A:{SYNTHETIC, Splitter:B:11:0x003c} */
    /* JADX WARNING: Missing block: B:12:?, code skipped:
            r7.countDown();
     */
    /* JADX WARNING: Missing block: B:13:0x0040, code skipped:
            r5 = move-exception;
     */
    /* JADX WARNING: Missing block: B:14:0x0041, code skipped:
            org.telegram.messenger.FileLog.e(r5);
     */
    /* JADX WARNING: Missing block: B:17:?, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:19:?, code skipped:
            return;
     */
    public /* synthetic */ void lambda$getChannelPtsSync$147$MessagesStorage(int r5, java.lang.Integer[] r6, java.util.concurrent.CountDownLatch r7) {
        /*
        r4 = this;
        r0 = 0;
        r1 = r4.database;	 Catch:{ Exception -> 0x0031 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0031 }
        r2.<init>();	 Catch:{ Exception -> 0x0031 }
        r3 = "SELECT pts FROM dialogs WHERE did = ";	 Catch:{ Exception -> 0x0031 }
        r2.append(r3);	 Catch:{ Exception -> 0x0031 }
        r5 = -r5;	 Catch:{ Exception -> 0x0031 }
        r2.append(r5);	 Catch:{ Exception -> 0x0031 }
        r5 = r2.toString();	 Catch:{ Exception -> 0x0031 }
        r2 = 0;	 Catch:{ Exception -> 0x0031 }
        r3 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0031 }
        r0 = r1.queryFinalized(r5, r3);	 Catch:{ Exception -> 0x0031 }
        r5 = r0.next();	 Catch:{ Exception -> 0x0031 }
        if (r5 == 0) goto L_0x002c;	 Catch:{ Exception -> 0x0031 }
    L_0x0022:
        r5 = r0.intValue(r2);	 Catch:{ Exception -> 0x0031 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0031 }
        r6[r2] = r5;	 Catch:{ Exception -> 0x0031 }
    L_0x002c:
        if (r0 == 0) goto L_0x003a;
    L_0x002e:
        goto L_0x0037;
    L_0x002f:
        r5 = move-exception;
        goto L_0x0045;
    L_0x0031:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x002f }
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r0.dispose();
    L_0x003a:
        if (r7 == 0) goto L_0x0044;
    L_0x003c:
        r7.countDown();	 Catch:{ Exception -> 0x0040 }
        goto L_0x0044;
    L_0x0040:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
    L_0x0044:
        return;
    L_0x0045:
        if (r0 == 0) goto L_0x004a;
    L_0x0047:
        r0.dispose();
    L_0x004a:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getChannelPtsSync$147$MessagesStorage(int, java.lang.Integer[], java.util.concurrent.CountDownLatch):void");
    }

    public User getUserSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        User[] userArr = new User[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$LtS-FKOcgVH3YBCZPLDRfDpE-FQ(this, userArr, i, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return userArr[0];
    }

    public /* synthetic */ void lambda$getUserSync$148$MessagesStorage(User[] userArr, int i, CountDownLatch countDownLatch) {
        userArr[0] = getUser(i);
        countDownLatch.countDown();
    }

    public Chat getChatSync(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Chat[] chatArr = new Chat[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$CdZ7E32u1n6P7Cpm7mlm0LMfBhM(this, chatArr, i, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return chatArr[0];
    }

    public /* synthetic */ void lambda$getChatSync$149$MessagesStorage(Chat[] chatArr, int i, CountDownLatch countDownLatch) {
        chatArr[0] = getChat(i);
        countDownLatch.countDown();
    }

    public User getUser(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getUsersInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (User) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public ArrayList<User> getUsers(ArrayList<Integer> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        try {
            getUsersInternal(TextUtils.join(",", arrayList), arrayList2);
        } catch (Exception e) {
            arrayList2.clear();
            FileLog.e(e);
        }
        return arrayList2;
    }

    public Chat getChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getChatsInternal(stringBuilder.toString(), arrayList);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (Chat) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public EncryptedChat getEncryptedChat(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            getEncryptedChatsInternal(stringBuilder.toString(), arrayList, null);
            if (arrayList.isEmpty()) {
                return null;
            }
            return (EncryptedChat) arrayList.get(0);
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
