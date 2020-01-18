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
import org.telegram.tgnet.TLRPC.InputDialogPeer;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.PollResults;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogFolder;
import org.telegram.tgnet.TLRPC.TL_folder;
import org.telegram.tgnet.TLRPC.TL_folderPeer;
import org.telegram.tgnet.TLRPC.TL_inputFolderPeer;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
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
import org.telegram.tgnet.TLRPC.TL_messageReactions;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_deleteScheduledMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_poll;
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
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;

public class MessagesStorage extends BaseController {
    private static volatile MessagesStorage[] Instance = new MessagesStorage[3];
    private static final int LAST_DB_VERSION = 64;
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
                this.database.executeFast("CREATE TABLE scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
                this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
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
                this.database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
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
                if (intValue < 64) {
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
        String str = "DELETE FROM download_queue WHERE 1";
        if (i == 28 || i == 29) {
            this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            this.database.executeFast(str).stepThis().dispose();
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
            this.database.executeFast(str).stepThis().dispose();
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
            i = 62;
        }
        if (i == 62) {
            this.database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
            this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 63").stepThis().dispose();
            i = 63;
        }
        if (i == 63) {
            this.database.executeFast(str).stepThis().dispose();
            this.database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$z3HUCafabuAW6qPxQHj_RTk1MGw(this));
    }

    public /* synthetic */ void lambda$loadPendingTasks$22$MessagesStorage() {
        try {
            SQLiteCursor queryFinalized = this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int readInt32 = byteBufferValue.readInt32(false);
                    int readInt322;
                    ArrayList arrayList;
                    int i;
                    switch (readInt32) {
                        case 0:
                            Chat TLdeserialize = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            if (TLdeserialize != null) {
                                Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$X8wmcVmkWlOOmT7hyCwq34C_3HA(this, TLdeserialize, longValue));
                                break;
                            }
                            break;
                        case 1:
                            Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$0pEDcAvar_KSwzKWD9sU7FMqhzD0(this, byteBufferValue.readInt32(false), byteBufferValue.readInt32(false), longValue));
                            break;
                        case 2:
                        case 5:
                        case 8:
                        case 10:
                        case 14:
                            TL_dialog tL_dialog = new TL_dialog();
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
                            if (readInt32 >= 10) {
                                tL_dialog.unread_mark = byteBufferValue.readBool(false);
                            }
                            if (readInt32 >= 14) {
                                tL_dialog.folder_id = byteBufferValue.readInt32(false);
                            }
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$uwK8wtI_v95kHeLcf4gXEkrDfLc(this, tL_dialog, InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                            break;
                        case 3:
                            getSendMessagesHelper().sendGame(InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), (TL_inputMediaGame) InputMedia.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), byteBufferValue.readInt64(false), longValue);
                            break;
                        case 4:
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$AuH1gRs2iXmoSCC0c3xvFDAEwcM(this, byteBufferValue.readInt64(false), byteBufferValue.readBool(false), InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                            break;
                        case 6:
                            Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$veltQ-QzWYSSmgAGGDUTY-jvHoM(this, byteBufferValue.readInt32(false), byteBufferValue.readInt32(false), longValue, InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false)));
                            break;
                        case 7:
                            readInt322 = byteBufferValue.readInt32(false);
                            readInt32 = byteBufferValue.readInt32(false);
                            TLObject TLdeserialize2 = TL_messages_deleteMessages.TLdeserialize(byteBufferValue, readInt32, false);
                            TLObject TLdeserialize3 = TLdeserialize2 == null ? TL_channels_deleteMessages.TLdeserialize(byteBufferValue, readInt32, false) : TLdeserialize2;
                            if (TLdeserialize3 != null) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$EuuPlYsIg_jeReoMzIbm6stO0ag(this, readInt322, longValue, TLdeserialize3));
                                break;
                            } else {
                                removePendingTask(longValue);
                                break;
                            }
                        case 9:
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$OTCbnyNXoirwhvbsu8TFVflzodM(this, byteBufferValue.readInt64(false), InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                            break;
                        case 11:
                            readInt322 = byteBufferValue.readInt32(false);
                            int readInt323 = byteBufferValue.readInt32(false);
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$TqvcAvYPfh-Cio-d66SYceSFjMQ(this, readInt322, readInt323, readInt323 != 0 ? InputChannel.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false) : null, byteBufferValue.readInt32(false), longValue));
                            break;
                        case 12:
                        case 19:
                        case 20:
                            removePendingTask(longValue);
                            break;
                        case 13:
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$gZU7uqq34_u60ckwe65AH41ydVA(this, byteBufferValue.readInt64(false), byteBufferValue.readBool(false), byteBufferValue.readInt32(false), byteBufferValue.readInt32(false), byteBufferValue.readBool(false), InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                            break;
                        case 15:
                            Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$tZ31OdkMW8PKLVCYnMiDI_DfRTs(this, InputPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), longValue));
                            break;
                        case 16:
                            readInt322 = byteBufferValue.readInt32(false);
                            readInt32 = byteBufferValue.readInt32(false);
                            arrayList = new ArrayList();
                            for (i = 0; i < readInt32; i++) {
                                arrayList.add(InputDialogPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                            }
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$KOMsQ0FDjAutn-YKgmf9ukUIQZQ(this, readInt322, arrayList, longValue));
                            break;
                        case 17:
                            readInt322 = byteBufferValue.readInt32(false);
                            readInt32 = byteBufferValue.readInt32(false);
                            arrayList = new ArrayList();
                            for (i = 0; i < readInt32; i++) {
                                arrayList.add(TL_inputFolderPeer.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                            }
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$LtM1spYCOokckYn9GalfKw21eVw(this, readInt322, arrayList, longValue));
                            break;
                        case 18:
                            long readInt64 = byteBufferValue.readInt64(false);
                            int readInt324 = byteBufferValue.readInt32(false);
                            TL_messages_deleteScheduledMessages TLdeserialize4 = TL_messages_deleteScheduledMessages.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            if (TLdeserialize4 != null) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$l0j4dGsiYqNq856M-GFXUg5lJ8Y(this, readInt64, readInt324, longValue, TLdeserialize4));
                                break;
                            } else {
                                removePendingTask(longValue);
                                break;
                            }
                        case 21:
                            OverrideWallpaperInfo overrideWallpaperInfo = new OverrideWallpaperInfo();
                            byteBufferValue.readInt64(false);
                            overrideWallpaperInfo.isBlurred = byteBufferValue.readBool(false);
                            overrideWallpaperInfo.isMotion = byteBufferValue.readBool(false);
                            overrideWallpaperInfo.color = byteBufferValue.readInt32(false);
                            overrideWallpaperInfo.gradientColor = byteBufferValue.readInt32(false);
                            overrideWallpaperInfo.rotation = byteBufferValue.readInt32(false);
                            overrideWallpaperInfo.intensity = (float) byteBufferValue.readDouble(false);
                            boolean readBool = byteBufferValue.readBool(false);
                            overrideWallpaperInfo.slug = byteBufferValue.readString(false);
                            overrideWallpaperInfo.originalFileName = byteBufferValue.readString(false);
                            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$bGlnbD0Mf3yn2_2a_iqo-KIMiFo(this, overrideWallpaperInfo, readBool, longValue));
                            break;
                        default:
                            break;
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
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
        getMessagesController().deleteMessages(null, null, null, 0, i, true, false, j, tLObject);
    }

    public /* synthetic */ void lambda$null$14$MessagesStorage(long j, InputPeer inputPeer, long j2) {
        getMessagesController().markDialogAsUnread(j, inputPeer, j2);
    }

    public /* synthetic */ void lambda$null$15$MessagesStorage(int i, int i2, InputChannel inputChannel, int i3, long j) {
        getMessagesController().markMessageAsRead(i, i2, inputChannel, i3, j);
    }

    public /* synthetic */ void lambda$null$16$MessagesStorage(OverrideWallpaperInfo overrideWallpaperInfo, boolean z, long j) {
        getMessagesController().saveWallpaperToServer(null, overrideWallpaperInfo, z, j);
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

    public /* synthetic */ void lambda$null$21$MessagesStorage(long j, int i, long j2, TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).deleteMessages(null, null, null, j, i, true, true, j2, tLObject);
    }

    public void saveChannelPts(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mo3K0WvFqR9SZPhToK39OSbqvKk(this, i2, i));
    }

    public /* synthetic */ void lambda$saveChannelPts$23$MessagesStorage(int i, int i2) {
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

    public /* synthetic */ void lambda$saveDiffParams$24$MessagesStorage(int i, int i2, int i3, int i4) {
        saveDiffParamsInternal(i, i2, i3, i4);
    }

    public void saveDiffParams(int i, int i2, int i3, int i4) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6fSaPv7mRdf3T5hKF_Cw6K9aq6I(this, i, i2, i3, i4));
    }

    public void setDialogFlags(long j, long j2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$URLUIjBQTkBjjeSdhj28q197uCc(this, j, j2));
    }

    public /* synthetic */ void lambda$setDialogFlags$25$MessagesStorage(long j, long j2) {
        try {
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[]{Long.valueOf(j), Long.valueOf(j2)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putPushMessage(MessageObject messageObject) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$zwXoHpHp9i0kkboizEaN6aAr85w(this, messageObject));
    }

    public /* synthetic */ void lambda$putPushMessage$26$MessagesStorage(MessageObject messageObject) {
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

    public void readAllDialogs(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$EpmQIkk6PuL3OpgBne4qYChFLRg(this, i));
    }

    public /* synthetic */ void lambda$readAllDialogs$28$MessagesStorage(int i) {
        try {
            SQLiteCursor queryFinalized;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            if (i >= 0) {
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count != 0 AND folder_id = %1$d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            } else {
                queryFinalized = this.database.queryFinalized("SELECT did, last_mid, unread_count, date FROM dialogs WHERE unread_count != 0", new Object[0]);
            }
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(0);
                if (!DialogObject.isFolderDialogId(longValue)) {
                    ReadDialog readDialog = new ReadDialog();
                    readDialog.lastMid = queryFinalized.intValue(1);
                    readDialog.unreadCount = queryFinalized.intValue(2);
                    readDialog.date = queryFinalized.intValue(3);
                    longSparseArray.put(longValue, readDialog);
                    int i2 = (int) longValue;
                    int i3 = (int) (longValue >> 32);
                    if (i2 != 0) {
                        if (i2 < 0) {
                            int i4 = -i2;
                            if (!arrayList2.contains(Integer.valueOf(i4))) {
                                arrayList2.add(Integer.valueOf(i4));
                            }
                        } else if (!arrayList.contains(Integer.valueOf(i2))) {
                            arrayList.add(Integer.valueOf(i2));
                        }
                    } else if (!arrayList3.contains(Integer.valueOf(i3))) {
                        arrayList3.add(Integer.valueOf(i3));
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$EOPIS8zqBund7flgg_OAgV1TzCA(this, arrayList4, arrayList5, arrayList6, longSparseArray));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$27$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long keyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            MessagesController messagesController = getMessagesController();
            int i2 = readDialog.lastMid;
            messagesController.markDialogAsRead(keyAt, i2, i2, readDialog.date, false, readDialog.unreadCount, true, 0);
        }
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$CP8U6OAQ-rUNfxsK0KOEt-F2Lb4(this));
    }

    public /* synthetic */ void lambda$loadUnreadMessages$30$MessagesStorage() {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$82Ej4rDj5eul9KY9u3PJ3wE6Pbg(this, longSparseArray, list, arrayList11, arrayList9, arrayList12, arrayList5));
        } catch (Exception e4) {
            FileLog.e(e4);
        }
    }

    public /* synthetic */ void lambda$null$29$MessagesStorage(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5);
    }

    public void putWallpapers(ArrayList<WallPaper> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$6sdDEHgazj6hnts4taQOPjHiK-U(this, i, arrayList));
    }

    public /* synthetic */ void lambda$putWallpapers$31$MessagesStorage(int i, ArrayList arrayList) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$jEbrH8iJC0foqhOGkS2RXxehCVA(this));
    }

    public /* synthetic */ void lambda$getWallpapers$33$MessagesStorage() {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$jYcjKkBYP3uscelAyyOvq_YDbAQ(arrayList));
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

    public void addRecentLocalFile(String str, String str2, Document document) {
        if (str != null && str.length() != 0) {
            if ((str2 != null && str2.length() != 0) || document != null) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$PYdreL1lZbKyAVmwgtRlBdpecdo(this, document, str, str2));
            }
        }
    }

    public /* synthetic */ void lambda$addRecentLocalFile$34$MessagesStorage(Document document, String str, String str2) {
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

    public void deleteUserChannelHistory(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$N0a2D7Yb4WGy9pp3z3CdtYpSvKY(this, i, i2));
    }

    public /* synthetic */ void lambda$deleteUserChannelHistory$37$MessagesStorage(int i, int i2) {
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
                            addFilesToDelete(TLdeserialize, arrayList2, false);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$_fS5U8KeZdvYlLJfOHQbUAQTxNw(this, arrayList, i));
            markMessagesAsDeletedInternal(arrayList, i, false, false);
            updateDialogsWithDeletedMessagesInternal(arrayList, null, i);
            getFileLoader().deleteFiles(arrayList2, 0);
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$zT0UAgVq68Ldm9wBDUe4m52D_tA(this, arrayList, i));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public /* synthetic */ void lambda$null$35$MessagesStorage(ArrayList arrayList, int i) {
        getMessagesController().markChannelDialogMessageAsDeleted(arrayList, i);
    }

    public /* synthetic */ void lambda$null$36$MessagesStorage(ArrayList arrayList, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(i), Boolean.valueOf(false));
    }

    private boolean addFilesToDelete(Message message, ArrayList<File> arrayList, boolean z) {
        int i = 0;
        if (message == null) {
            return false;
        }
        int size;
        File pathToAttach;
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaPhoto) {
            Photo photo = messageMedia.photo;
            if (photo != null) {
                size = photo.sizes.size();
                while (i < size) {
                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) message.media.photo.sizes.get(i));
                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                        arrayList.add(pathToAttach);
                    }
                    i++;
                }
                return true;
            }
        }
        messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaDocument) {
            Document document = messageMedia.document;
            if (document != null) {
                File pathToAttach2 = FileLoader.getPathToAttach(document, z);
                if (pathToAttach2 != null && pathToAttach2.toString().length() > 0) {
                    arrayList.add(pathToAttach2);
                }
                size = message.media.document.thumbs.size();
                while (i < size) {
                    pathToAttach = FileLoader.getPathToAttach((PhotoSize) message.media.document.thumbs.get(i));
                    if (pathToAttach != null && pathToAttach.toString().length() > 0) {
                        arrayList.add(pathToAttach);
                    }
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    public void deleteDialog(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$GarsbEqAallfb3M0ugm5VCrBKBE(this, i, j));
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x0212 A:{Catch:{ Exception -> 0x0037 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0212 A:{Catch:{ Exception -> 0x0037 }} */
    public /* synthetic */ void lambda$deleteDialog$39$MessagesStorage(int r20, long r21) {
        /*
        r19 = this;
        r1 = r19;
        r2 = r20;
        r3 = r21;
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
        goto L_0x03bb;
    L_0x003a:
        r9 = (int) r3;
        r10 = "SELECT data FROM messages WHERE uid = ";
        r11 = 2;
        if (r9 == 0) goto L_0x0042;
    L_0x0040:
        if (r2 != r11) goto L_0x0090;
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
        r0 = r12.next();	 Catch:{ Exception -> 0x0082 }
        if (r0 == 0) goto L_0x0086;
    L_0x0064:
        r0 = r12.byteBufferValue(r8);	 Catch:{ Exception -> 0x0082 }
        if (r0 == 0) goto L_0x005e;
    L_0x006a:
        r14 = r0.readInt32(r8);	 Catch:{ Exception -> 0x0082 }
        r14 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r14, r8);	 Catch:{ Exception -> 0x0082 }
        r15 = r19.getUserConfig();	 Catch:{ Exception -> 0x0082 }
        r15 = r15.clientUserId;	 Catch:{ Exception -> 0x0082 }
        r14.readAttachPath(r0, r15);	 Catch:{ Exception -> 0x0082 }
        r0.reuse();	 Catch:{ Exception -> 0x0082 }
        r1.addFilesToDelete(r14, r13, r8);	 Catch:{ Exception -> 0x0082 }
        goto L_0x005e;
    L_0x0082:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x0086:
        r12.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r19.getFileLoader();	 Catch:{ Exception -> 0x0037 }
        r0.deleteFiles(r13, r2);	 Catch:{ Exception -> 0x0037 }
    L_0x0090:
        r13 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r14 = "DELETE FROM messages_holes WHERE uid = ";
        r15 = "DELETE FROM media_v2 WHERE uid = ";
        r7 = "DELETE FROM media_counts_v2 WHERE uid = ";
        r12 = "DELETE FROM bot_keyboard WHERE uid = ";
        r8 = "DELETE FROM messages WHERE uid = ";
        if (r2 == 0) goto L_0x0226;
    L_0x009e:
        if (r2 != r6) goto L_0x00a2;
    L_0x00a0:
        goto L_0x0226;
    L_0x00a2:
        if (r2 != r11) goto L_0x0222;
    L_0x00a4:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r6 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = ";
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r6 = 0;
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0037 }
        r2 = r0.queryFinalized(r2, r9);	 Catch:{ Exception -> 0x0037 }
        r0 = r2.next();	 Catch:{ Exception -> 0x0037 }
        if (r0 == 0) goto L_0x021c;
    L_0x00c4:
        r17 = r12;
        r11 = r2.longValue(r6);	 Catch:{ Exception -> 0x0037 }
        r0 = 1;
        r18 = r7;
        r6 = r2.longValue(r0);	 Catch:{ Exception -> 0x0037 }
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
        r9.append(r6);	 Catch:{ Exception -> 0x0037 }
        r10 = ")";
        r9.append(r10);	 Catch:{ Exception -> 0x0037 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0037 }
        r20 = r2;
        r10 = 0;
        r2 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0037 }
        r2 = r0.queryFinalized(r9, r2);	 Catch:{ Exception -> 0x0037 }
        r9 = -1;
    L_0x0101:
        r0 = r2.next();	 Catch:{ Exception -> 0x0134 }
        if (r0 == 0) goto L_0x0131;
    L_0x0107:
        r0 = r2.byteBufferValue(r10);	 Catch:{ Exception -> 0x0134 }
        if (r0 == 0) goto L_0x012b;
    L_0x010d:
        r16 = r9;
        r9 = r0.readInt32(r10);	 Catch:{ Exception -> 0x0129 }
        r9 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r9, r10);	 Catch:{ Exception -> 0x0129 }
        r10 = r19.getUserConfig();	 Catch:{ Exception -> 0x0129 }
        r10 = r10.clientUserId;	 Catch:{ Exception -> 0x0129 }
        r9.readAttachPath(r0, r10);	 Catch:{ Exception -> 0x0129 }
        r0.reuse();	 Catch:{ Exception -> 0x0129 }
        if (r9 == 0) goto L_0x012d;
    L_0x0125:
        r0 = r9.id;	 Catch:{ Exception -> 0x0129 }
        r9 = r0;
        goto L_0x012f;
    L_0x0129:
        r0 = move-exception;
        goto L_0x0137;
    L_0x012b:
        r16 = r9;
    L_0x012d:
        r9 = r16;
    L_0x012f:
        r10 = 0;
        goto L_0x0101;
    L_0x0131:
        r16 = r9;
        goto L_0x013a;
    L_0x0134:
        r0 = move-exception;
        r16 = r9;
    L_0x0137:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0037 }
    L_0x013a:
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r8);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r11);	 Catch:{ Exception -> 0x0037 }
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r14);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r5 = r17;
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r6 = r18;
        r2.append(r6);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r15);	 Catch:{ Exception -> 0x0037 }
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
        r0 = r19.getMediaDataController();	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r5 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r5);	 Catch:{ Exception -> 0x0037 }
        r9 = r16;
        r5 = -1;
        if (r9 == r5) goto L_0x0215;
    L_0x0212:
        createFirstHoles(r3, r0, r2, r9);	 Catch:{ Exception -> 0x0037 }
    L_0x0215:
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r2.dispose();	 Catch:{ Exception -> 0x0037 }
        goto L_0x021e;
    L_0x021c:
        r20 = r2;
    L_0x021e:
        r20.dispose();	 Catch:{ Exception -> 0x0037 }
        return;
    L_0x0222:
        r6 = r7;
        r5 = r12;
        goto L_0x02e4;
    L_0x0226:
        r6 = r7;
        r5 = r12;
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "DELETE FROM dialogs WHERE did = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "DELETE FROM chat_settings_v2 WHERE uid = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "DELETE FROM chat_pinned WHERE uid = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "DELETE FROM channel_users_v2 WHERE did = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "DELETE FROM search_recent WHERE did = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = 32;
        r10 = r3 >> r0;
        r0 = (int) r10;	 Catch:{ Exception -> 0x0037 }
        if (r9 == 0) goto L_0x02c6;
    L_0x02c5:
        goto L_0x02e4;
    L_0x02c6:
        r2 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r7.<init>();	 Catch:{ Exception -> 0x0037 }
        r9 = "DELETE FROM enc_chats WHERE uid = ";
        r7.append(r9);	 Catch:{ Exception -> 0x0037 }
        r7.append(r0);	 Catch:{ Exception -> 0x0037 }
        r0 = r7.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r2.executeFast(r0);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
    L_0x02e4:
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r7 = "UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = ";
        r2.append(r7);	 Catch:{ Exception -> 0x0037 }
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
        r2.append(r5);	 Catch:{ Exception -> 0x0037 }
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
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r15);	 Catch:{ Exception -> 0x0037 }
        r2.append(r3);	 Catch:{ Exception -> 0x0037 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0037 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0037 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0037 }
        r0.dispose();	 Catch:{ Exception -> 0x0037 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0037 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0037 }
        r2.<init>();	 Catch:{ Exception -> 0x0037 }
        r2.append(r14);	 Catch:{ Exception -> 0x0037 }
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
        r0 = r19.getMediaDataController();	 Catch:{ Exception -> 0x0037 }
        r2 = 0;
        r0.clearBotKeyboard(r3, r2);	 Catch:{ Exception -> 0x0037 }
        r0 = new org.telegram.messenger.-$$Lambda$MessagesStorage$tBITSP4uIVXs3MrFSaqwnTCbLms;	 Catch:{ Exception -> 0x0037 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0037 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0037 }
        goto L_0x03be;
    L_0x03bb:
        org.telegram.messenger.FileLog.e(r0);
    L_0x03be:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$deleteDialog$39$MessagesStorage(int, long):void");
    }

    public /* synthetic */ void lambda$null$38$MessagesStorage() {
        getNotificationCenter().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
    }

    public void onDeleteQueryComplete(long j) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7B7zYr_TXDhl6iWXwpWKnHe2Ihk(this, j));
    }

    public /* synthetic */ void lambda$onDeleteQueryComplete$40$MessagesStorage(long j) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$RR0VoVcZWe3On5GCmEaULrWKi4o(this, j, i, i2, i3));
    }

    public /* synthetic */ void lambda$getDialogPhotos$42$MessagesStorage(long j, int i, int i2, int i3) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$MessagesStorage$y-eXdDdnoHYRbr9uIJJn0XF7oIQ(this, tL_photos_photos, i, i2, j, i3));
    }

    public /* synthetic */ void lambda$null$41$MessagesStorage(photos_Photos photos_photos, int i, int i2, long j, int i3) {
        getMessagesController().processLoadedUserPhotos(photos_photos, i, i2, j, true, i3);
    }

    public void clearUserPhotos(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$xvrHjnuIRs85JaHJQkkpvc__sGg(this, i));
    }

    public /* synthetic */ void lambda$clearUserPhotos$43$MessagesStorage(int i) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$gsmhH-OZS8JhtOYu-wVne1BaVnU(this, i, j));
    }

    public /* synthetic */ void lambda$clearUserPhoto$44$MessagesStorage(int i, long j) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$ybAdD4WJglgD5KNxMkNRIpaEu_c(this, messages_dialogs, i6, i2, i3, i4, i5, message, i, longSparseArray, longSparseArray2));
    }

    public /* synthetic */ void lambda$resetDialogs$46$MessagesStorage(messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
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
            Collections.sort(arrayList2, new -$$Lambda$MessagesStorage$E1-iJ1217-dt315WyL33fblo70c(longSparseArray3));
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
            getUserConfig().draftsLoaded = false;
            getUserConfig().saveConfig(false);
            getMessagesController().completeDialogsReset(messages_dialogs, i6, i2, i3, i4, i5, longSparseArray, longSparseArray2, message);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ int lambda$null$45(LongSparseArray longSparseArray, Long l, Long l2) {
        Integer num = (Integer) longSparseArray.get(l.longValue());
        Integer num2 = (Integer) longSparseArray.get(l2.longValue());
        if (num.intValue() < num2.intValue()) {
            return 1;
        }
        return num.intValue() > num2.intValue() ? -1 : 0;
    }

    public void putDialogPhotos(int i, photos_Photos photos_photos) {
        if (photos_photos != null && !photos_photos.photos.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$totVTzJXq8gUob5gVQ81j018btU(this, i, photos_photos));
        }
    }

    public /* synthetic */ void lambda$putDialogPhotos$47$MessagesStorage(int i, photos_Photos photos_photos) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$nXns8IPTByyel2e-T78aQHNV2m0(this, arrayList));
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0108 A:{Catch:{ Exception -> 0x013d }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0102 A:{Catch:{ Exception -> 0x013d }} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x011c A:{Catch:{ Exception -> 0x013d }} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x011a A:{Catch:{ Exception -> 0x013d }} */
    public /* synthetic */ void lambda$emptyMessagesMedia$49$MessagesStorage(java.util.ArrayList r12) {
        /*
        r11 = this;
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x013d }
        r0.<init>();	 Catch:{ Exception -> 0x013d }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x013d }
        r1.<init>();	 Catch:{ Exception -> 0x013d }
        r2 = r11.database;	 Catch:{ Exception -> 0x013d }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x013d }
        r4 = "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)";
        r5 = 1;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x013d }
        r7 = ",";
        r12 = android.text.TextUtils.join(r7, r12);	 Catch:{ Exception -> 0x013d }
        r7 = 0;
        r6[r7] = r12;	 Catch:{ Exception -> 0x013d }
        r12 = java.lang.String.format(r3, r4, r6);	 Catch:{ Exception -> 0x013d }
        r3 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x013d }
        r12 = r2.queryFinalized(r12, r3);	 Catch:{ Exception -> 0x013d }
    L_0x0026:
        r2 = r12.next();	 Catch:{ Exception -> 0x013d }
        r3 = 3;
        r4 = 2;
        if (r2 == 0) goto L_0x0092;
    L_0x002e:
        r2 = r12.byteBufferValue(r7);	 Catch:{ Exception -> 0x013d }
        if (r2 == 0) goto L_0x0026;
    L_0x0034:
        r6 = r2.readInt32(r7);	 Catch:{ Exception -> 0x013d }
        r6 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r2, r6, r7);	 Catch:{ Exception -> 0x013d }
        r8 = r11.getUserConfig();	 Catch:{ Exception -> 0x013d }
        r8 = r8.clientUserId;	 Catch:{ Exception -> 0x013d }
        r6.readAttachPath(r2, r8);	 Catch:{ Exception -> 0x013d }
        r2.reuse();	 Catch:{ Exception -> 0x013d }
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        if (r2 == 0) goto L_0x0026;
    L_0x004c:
        r2 = r11.addFilesToDelete(r6, r0, r5);	 Catch:{ Exception -> 0x013d }
        if (r2 != 0) goto L_0x0053;
    L_0x0052:
        goto L_0x0026;
    L_0x0053:
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        r2 = r2.document;	 Catch:{ Exception -> 0x013d }
        if (r2 == 0) goto L_0x0063;
    L_0x0059:
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        r8 = new org.telegram.tgnet.TLRPC$TL_documentEmpty;	 Catch:{ Exception -> 0x013d }
        r8.<init>();	 Catch:{ Exception -> 0x013d }
        r2.document = r8;	 Catch:{ Exception -> 0x013d }
        goto L_0x0072;
    L_0x0063:
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        r2 = r2.photo;	 Catch:{ Exception -> 0x013d }
        if (r2 == 0) goto L_0x0072;
    L_0x0069:
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        r8 = new org.telegram.tgnet.TLRPC$TL_photoEmpty;	 Catch:{ Exception -> 0x013d }
        r8.<init>();	 Catch:{ Exception -> 0x013d }
        r2.photo = r8;	 Catch:{ Exception -> 0x013d }
    L_0x0072:
        r2 = r6.media;	 Catch:{ Exception -> 0x013d }
        r8 = r6.media;	 Catch:{ Exception -> 0x013d }
        r8 = r8.flags;	 Catch:{ Exception -> 0x013d }
        r8 = r8 & -2;
        r2.flags = r8;	 Catch:{ Exception -> 0x013d }
        r2 = r12.intValue(r5);	 Catch:{ Exception -> 0x013d }
        r6.id = r2;	 Catch:{ Exception -> 0x013d }
        r2 = r12.intValue(r4);	 Catch:{ Exception -> 0x013d }
        r6.date = r2;	 Catch:{ Exception -> 0x013d }
        r2 = r12.longValue(r3);	 Catch:{ Exception -> 0x013d }
        r6.dialog_id = r2;	 Catch:{ Exception -> 0x013d }
        r1.add(r6);	 Catch:{ Exception -> 0x013d }
        goto L_0x0026;
    L_0x0092:
        r12.dispose();	 Catch:{ Exception -> 0x013d }
        r12 = r1.isEmpty();	 Catch:{ Exception -> 0x013d }
        if (r12 != 0) goto L_0x0135;
    L_0x009b:
        r12 = r11.database;	 Catch:{ Exception -> 0x013d }
        r2 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r12 = r12.executeFast(r2);	 Catch:{ Exception -> 0x013d }
        r2 = 0;
    L_0x00a4:
        r6 = r1.size();	 Catch:{ Exception -> 0x013d }
        if (r2 >= r6) goto L_0x012a;
    L_0x00aa:
        r6 = r1.get(r2);	 Catch:{ Exception -> 0x013d }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x013d }
        r8 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x013d }
        r9 = r6.getObjectSize();	 Catch:{ Exception -> 0x013d }
        r8.<init>(r9);	 Catch:{ Exception -> 0x013d }
        r6.serializeToStream(r8);	 Catch:{ Exception -> 0x013d }
        r12.requery();	 Catch:{ Exception -> 0x013d }
        r9 = r6.id;	 Catch:{ Exception -> 0x013d }
        r9 = (long) r9;	 Catch:{ Exception -> 0x013d }
        r12.bindLong(r5, r9);	 Catch:{ Exception -> 0x013d }
        r9 = r6.dialog_id;	 Catch:{ Exception -> 0x013d }
        r12.bindLong(r4, r9);	 Catch:{ Exception -> 0x013d }
        r9 = org.telegram.messenger.MessageObject.getUnreadFlags(r6);	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r3, r9);	 Catch:{ Exception -> 0x013d }
        r9 = 4;
        r10 = r6.send_state;	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r9, r10);	 Catch:{ Exception -> 0x013d }
        r9 = 5;
        r10 = r6.date;	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r9, r10);	 Catch:{ Exception -> 0x013d }
        r9 = 6;
        r12.bindByteBuffer(r9, r8);	 Catch:{ Exception -> 0x013d }
        r9 = 7;
        r10 = org.telegram.messenger.MessageObject.isOut(r6);	 Catch:{ Exception -> 0x013d }
        if (r10 != 0) goto L_0x00ef;
    L_0x00e8:
        r10 = r6.from_scheduled;	 Catch:{ Exception -> 0x013d }
        if (r10 == 0) goto L_0x00ed;
    L_0x00ec:
        goto L_0x00ef;
    L_0x00ed:
        r10 = 0;
        goto L_0x00f0;
    L_0x00ef:
        r10 = 1;
    L_0x00f0:
        r12.bindInteger(r9, r10);	 Catch:{ Exception -> 0x013d }
        r9 = 8;
        r10 = r6.ttl;	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r9, r10);	 Catch:{ Exception -> 0x013d }
        r9 = r6.flags;	 Catch:{ Exception -> 0x013d }
        r9 = r9 & 1024;
        r10 = 9;
        if (r9 == 0) goto L_0x0108;
    L_0x0102:
        r9 = r6.views;	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r10, r9);	 Catch:{ Exception -> 0x013d }
        goto L_0x010f;
    L_0x0108:
        r9 = r11.getMessageMediaType(r6);	 Catch:{ Exception -> 0x013d }
        r12.bindInteger(r10, r9);	 Catch:{ Exception -> 0x013d }
    L_0x010f:
        r9 = 10;
        r12.bindInteger(r9, r7);	 Catch:{ Exception -> 0x013d }
        r9 = 11;
        r6 = r6.mentioned;	 Catch:{ Exception -> 0x013d }
        if (r6 == 0) goto L_0x011c;
    L_0x011a:
        r6 = 1;
        goto L_0x011d;
    L_0x011c:
        r6 = 0;
    L_0x011d:
        r12.bindInteger(r9, r6);	 Catch:{ Exception -> 0x013d }
        r12.step();	 Catch:{ Exception -> 0x013d }
        r8.reuse();	 Catch:{ Exception -> 0x013d }
        r2 = r2 + 1;
        goto L_0x00a4;
    L_0x012a:
        r12.dispose();	 Catch:{ Exception -> 0x013d }
        r12 = new org.telegram.messenger.-$$Lambda$MessagesStorage$t7zI5TWNg08Mg1HnN5nF6Kp_mPY;	 Catch:{ Exception -> 0x013d }
        r12.<init>(r11, r1);	 Catch:{ Exception -> 0x013d }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);	 Catch:{ Exception -> 0x013d }
    L_0x0135:
        r12 = r11.getFileLoader();	 Catch:{ Exception -> 0x013d }
        r12.deleteFiles(r0, r7);	 Catch:{ Exception -> 0x013d }
        goto L_0x0141;
    L_0x013d:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
    L_0x0141:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$emptyMessagesMedia$49$MessagesStorage(java.util.ArrayList):void");
    }

    public /* synthetic */ void lambda$null$48$MessagesStorage(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public void updateMessagePollResults(long j, TL_poll tL_poll, PollResults pollResults) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$leglyP8xKeJ-Yq7SI6RTdzWLoSg(this, j, tL_poll, pollResults));
    }

    public /* synthetic */ void lambda$updateMessagePollResults$50$MessagesStorage(long j, TL_poll tL_poll, PollResults pollResults) {
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
                                if (pollResults != null) {
                                    MessageObject.updatePollResults(tL_messageMediaPoll, pollResults);
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

    public void updateMessageReactions(long j, int i, int i2, TL_messageReactions tL_messageReactions) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7NdoJnf6C2xsW-CLASSNAMEBc6nDcAwX4(this, i, i2, tL_messageReactions));
    }

    public /* synthetic */ void lambda$updateMessageReactions$51$MessagesStorage(int i, int i2, TL_messageReactions tL_messageReactions) {
        try {
            this.database.beginTransaction();
            long j = (long) i;
            if (i2 != 0) {
                j |= ((long) i2) << 32;
            }
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (TLdeserialize != null) {
                        MessageObject.updateReactions(TLdeserialize, tL_messageReactions);
                        SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(TLdeserialize.getObjectSize());
                        TLdeserialize.serializeToStream(nativeByteBuffer);
                        executeFast.requery();
                        executeFast.bindByteBuffer(1, nativeByteBuffer);
                        executeFast.bindLong(2, j);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        executeFast.dispose();
                    }
                }
            }
            queryFinalized.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getNewTask(ArrayList<Integer> arrayList, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$IpjsIPwhy40OH5HZeDZzph1Hii8(this, arrayList));
    }

    public /* synthetic */ void lambda$getNewTask$52$MessagesStorage(ArrayList arrayList) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$c5ipfP7EHe-ksDzzWqn1xdh2Z5U(this, i, i2, j));
    }

    public /* synthetic */ void lambda$markMentionMessageAsRead$53$MessagesStorage(int i, int i2, long j) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Vqha-Pcq8qKqFi-2uTj2MzZCpm0(this, j));
    }

    public /* synthetic */ void lambda$markMessageAsMention$54$MessagesStorage(long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[]{Long.valueOf(j)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetMentionsCount(long j, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$nr_6UagYOQ8kdWHEioIjW9iQZwc(this, i, j));
    }

    public /* synthetic */ void lambda$resetMentionsCount$55$MessagesStorage(int i, long j) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$zt059g3skDW_QXIKY21511Dqqzo(this, i3, i4, i5, i, i2, z));
    }

    public /* synthetic */ void lambda$createTaskForMid$57$MessagesStorage(int i, int i2, int i3, int i4, int i5, boolean z) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$eu1e71QBBEV1jIwQjLSHzAFKcx8(this, z, arrayList));
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

    public /* synthetic */ void lambda$null$56$MessagesStorage(boolean z, ArrayList arrayList) {
        if (!z) {
            markMessagesContentAsRead(arrayList, 0);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.messagesReadContent, arrayList);
    }

    public void createTaskForSecretChat(int i, int i2, int i3, int i4, ArrayList<Long> arrayList) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$pKLdtsNRrW-aqkZwfyedv91tGr4(this, arrayList, i, i4, i2, i3));
    }

    public /* synthetic */ void lambda$createTaskForSecretChat$59$MessagesStorage(ArrayList arrayList, int i, int i2, int i3, int i4) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$uS_-fU-YkDLrhjw80yT-Vy-D-6Y(this, arrayList4));
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

    public /* synthetic */ void lambda$null$58$MessagesStorage(ArrayList arrayList) {
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
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5dLRM1qp_VR1x9Q3XyM-etp_2z0(this, sparseLongArray, sparseLongArray2, arrayList));
            } else {
                updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithReadMessages$60$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, ArrayList arrayList) {
        updateDialogsWithReadMessagesInternal(null, sparseLongArray, sparseLongArray2, arrayList);
    }

    public void updateChatParticipants(ChatParticipants chatParticipants) {
        if (chatParticipants != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Yq9-fpKxa5n0haPp4o0Z8jTg-vk(this, chatParticipants));
        }
    }

    public /* synthetic */ void lambda$updateChatParticipants$62$MessagesStorage(ChatParticipants chatParticipants) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$rVdPp8-2cV7pvYEafBpoyisc_us(this, chatFull));
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

    public /* synthetic */ void lambda$null$61$MessagesStorage(ChatFull chatFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void loadChannelAdmins(int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$a59pu_hx9p55_fdXE4qUsxZy2B8(this, i));
    }

    public /* synthetic */ void lambda$loadChannelAdmins$63$MessagesStorage(int i) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$eaNnWBFjekCL7KdiuyjiaLqiQiA(this, i, sparseArray));
    }

    public /* synthetic */ void lambda$putChannelAdmins$64$MessagesStorage(int i, SparseArray sparseArray) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$wwYDv37pXXhF1XY56cnq19o79Hc(this, i, arrayList));
    }

    public /* synthetic */ void lambda$updateChannelUsers$65$MessagesStorage(int i, ArrayList arrayList) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$hoAFhgcrjiE3Gv05PRpzj4gfkG8(this, tLObject, str));
        }
    }

    public /* synthetic */ void lambda$saveBotCache$66$MessagesStorage(TLObject tLObject, String str) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$4ir8D3T5kZJa2nLYE16z7mgxnu8(this, getConnectionsManager().getCurrentTime(), str, requestDelegate));
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
    public /* synthetic */ void lambda$getBotCache$67$MessagesStorage(int r5, java.lang.String r6, org.telegram.tgnet.RequestDelegate r7) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getBotCache$67$MessagesStorage(int, java.lang.String, org.telegram.tgnet.RequestDelegate):void");
    }

    public void loadUserInfo(User user, boolean z, int i) {
        if (user != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$9QHORNBJg-ha24nvK8ijpO74v9o(this, user, z, i));
        }
    }

    public /* synthetic */ void lambda$loadUserInfo$68$MessagesStorage(User user, boolean z, int i) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Dnp_vJvk-4ql9AMId2Ud90xTY14(this, z, userFull));
    }

    public /* synthetic */ void lambda$updateUserInfo$69$MessagesStorage(boolean z, UserFull userFull) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$deiTLwS-_0g7ZFdhK02-HEjxAck(this, chatFull, z));
    }

    public /* synthetic */ void lambda$updateChatInfo$70$MessagesStorage(ChatFull chatFull, boolean z) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0FrciOzlcO50l1LlICryPocaC1g(this, i, i2));
    }

    public /* synthetic */ void lambda$updateUserPinnedMessage$72$MessagesStorage(int i, int i2) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$7-c1xsqEG7aU4MgV3PEsKt5DG9I(this, i, userFull));
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

    public /* synthetic */ void lambda$null$71$MessagesStorage(int i, UserFull userFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(i), userFull, null);
    }

    public void updateChatOnlineCount(int i, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$rX8NPXUX71ELm9pvrdZ31sSfuT0(this, i2, i));
    }

    public /* synthetic */ void lambda$updateChatOnlineCount$73$MessagesStorage(int i, int i2) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$WOXJb-2D3t0k8sPw11diqzgSl3E(this, i, i2));
    }

    public /* synthetic */ void lambda$updateChatPinnedMessage$75$MessagesStorage(int i, int i2) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$88NEHANjh8UPEvk6Jyh6UYlbUlw(this, chatFull));
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

    public /* synthetic */ void lambda$null$74$MessagesStorage(ChatFull chatFull) {
        getNotificationCenter().postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChatInfo(int i, int i2, int i3, int i4, int i5) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$NFbnvWxdE5EucMmfRVp_zX8lc2Y(this, i, i3, i2, i4, i5));
    }

    public /* synthetic */ void lambda$updateChatInfo$77$MessagesStorage(int i, int i2, int i3, int i4, int i5) {
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

    public boolean isMigratedChat(int i) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$rUfIFQ94z-TkLSzTLQNSjW0nmzo(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isMigratedChat$78$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$bFUukskWZw3OKUlOSgkXj-ylMEo(this, i, chatFullArr, z, z2, countDownLatch));
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
    public /* synthetic */ void lambda$loadChatInfo$79$MessagesStorage(int r18, org.telegram.tgnet.TLRPC.ChatFull[] r19, boolean r20, boolean r21, java.util.concurrent.CountDownLatch r22) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$loadChatInfo$79$MessagesStorage(int, org.telegram.tgnet.TLRPC$ChatFull[], boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    public void processPendingRead(long j, long j2, long j3, boolean z, int i) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$S9FGmn9HsOM2QGUu6wolOcVtrko(this, j, j2, z, i, this.lastSavedDate, j3));
    }

    public /* synthetic */ void lambda$processPendingRead$80$MessagesStorage(long j, long j2, boolean z, int i, int i2, long j3) {
        long j4 = j;
        try {
            int intValue;
            long longValue;
            SQLitePreparedStatement executeFast;
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = ");
            stringBuilder.append(j4);
            int i3 = 0;
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
            int i4 = (int) j4;
            String str = "SELECT changes()";
            int intValue2;
            if (i4 != 0) {
                j5 = Math.max(j5, (long) ((int) j2));
                if (z) {
                    j5 |= ((long) (-i4)) << 32;
                }
                executeFast = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
                executeFast.requery();
                executeFast.bindLong(1, j4);
                executeFast.bindLong(2, j5);
                executeFast.step();
                executeFast.dispose();
                if (j5 < longValue) {
                    queryFinalized = this.database.queryFinalized(str, new Object[0]);
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) + i : 0;
                    queryFinalized.dispose();
                    i3 = Math.max(0, intValue - intValue2);
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
                executeFast.bindLong(2, (long) i2);
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
                    intValue2 = queryFinalized.next() ? queryFinalized.intValue(0) + i : 0;
                    queryFinalized.dispose();
                    i3 = Math.max(0, intValue - intValue2);
                }
            }
            executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i3);
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$TIaGKRTPHd_tiIuS2RGjBU6Fxuk(this, z, new ArrayList(arrayList)));
        }
    }

    public /* synthetic */ void lambda$putContacts$81$MessagesStorage(boolean z, ArrayList arrayList) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$yWTuqu9gZCV4fHP1r0yQzVv8dug(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$deleteContacts$82$MessagesStorage(ArrayList arrayList) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$VTt7HhG_hmdpnE_Uihu1A5_J8Jk(this, str, str2));
        }
    }

    public /* synthetic */ void lambda$applyPhoneBookUpdates$83$MessagesStorage(String str, String str2) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$YxED77Whr25iDDDFFAGpkOI6cbg(this, hashMap, z));
        }
    }

    public /* synthetic */ void lambda$putCachedPhoneBook$84$MessagesStorage(HashMap hashMap, boolean z) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$yyil4fQcoHX5btPClt4-OEoOwqI(this, z));
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00fb A:{Catch:{ all -> 0x0137 }} */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00fb A:{Catch:{ all -> 0x0137 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00fb A:{Catch:{ all -> 0x0137 }} */
    /* JADX WARNING: Missing block: B:76:0x0134, code skipped:
            if (r3 == null) goto L_0x0148;
     */
    /* JADX WARNING: Missing block: B:84:0x0143, code skipped:
            if (r3 != null) goto L_0x0145;
     */
    /* JADX WARNING: Missing block: B:85:0x0145, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:86:0x0148, code skipped:
            r10 = new java.util.HashMap(r16);
     */
    /* JADX WARNING: Missing block: B:87:0x014f, code skipped:
            if (r14 == 0) goto L_0x016b;
     */
    /* JADX WARNING: Missing block: B:89:?, code skipped:
            r0 = r1.database;
            r14 = new java.lang.StringBuilder();
            r14.append("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0,");
            r14.append(r8);
            r0 = r0.queryFinalized(r14.toString(), new java.lang.Object[0]);
     */
    /* JADX WARNING: Missing block: B:90:0x016b, code skipped:
            r0 = r1.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1", new java.lang.Object[0]);
     */
    /* JADX WARNING: Missing block: B:91:0x0175, code skipped:
            r3 = r0;
     */
    /* JADX WARNING: Missing block: B:93:0x017a, code skipped:
            if (r3.next() == false) goto L_0x01f7;
     */
    /* JADX WARNING: Missing block: B:94:0x017c, code skipped:
            r0 = r3.stringValue(0);
            r8 = (org.telegram.messenger.ContactsController.Contact) r10.get(r0);
     */
    /* JADX WARNING: Missing block: B:95:0x0186, code skipped:
            if (r8 != null) goto L_0x01b5;
     */
    /* JADX WARNING: Missing block: B:96:0x0188, code skipped:
            r8 = new org.telegram.messenger.ContactsController.Contact();
            r8.contact_id = r3.intValue(1);
            r8.first_name = r3.stringValue(2);
            r8.last_name = r3.stringValue(3);
            r8.imported = r3.intValue(7);
     */
    /* JADX WARNING: Missing block: B:97:0x01a8, code skipped:
            if (r8.first_name != null) goto L_0x01ac;
     */
    /* JADX WARNING: Missing block: B:98:0x01aa, code skipped:
            r8.first_name = r2;
     */
    /* JADX WARNING: Missing block: B:100:0x01ae, code skipped:
            if (r8.last_name != null) goto L_0x01b2;
     */
    /* JADX WARNING: Missing block: B:101:0x01b0, code skipped:
            r8.last_name = r2;
     */
    /* JADX WARNING: Missing block: B:102:0x01b2, code skipped:
            r10.put(r0, r8);
     */
    /* JADX WARNING: Missing block: B:103:0x01b5, code skipped:
            r0 = r3.stringValue(4);
     */
    /* JADX WARNING: Missing block: B:104:0x01b9, code skipped:
            if (r0 != null) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:106:0x01bd, code skipped:
            r8.phones.add(r0);
            r14 = r3.stringValue(5);
     */
    /* JADX WARNING: Missing block: B:107:0x01c6, code skipped:
            if (r14 != null) goto L_0x01c9;
     */
    /* JADX WARNING: Missing block: B:110:0x01cd, code skipped:
            if (r14.length() != 8) goto L_0x01d9;
     */
    /* JADX WARNING: Missing block: B:112:0x01d3, code skipped:
            if (r0.length() == 8) goto L_0x01d9;
     */
    /* JADX WARNING: Missing block: B:113:0x01d5, code skipped:
            r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);
     */
    /* JADX WARNING: Missing block: B:114:0x01d9, code skipped:
            r8.shortPhones.add(r14);
            r8.phoneDeleted.add(java.lang.Integer.valueOf(r3.intValue(6)));
            r8.phoneTypes.add(r2);
     */
    /* JADX WARNING: Missing block: B:115:0x01f5, code skipped:
            if (r10.size() != 5000) goto L_0x0176;
     */
    /* JADX WARNING: Missing block: B:116:0x01f7, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:118:0x01fd, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:120:?, code skipped:
            r10.clear();
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:121:0x0209, code skipped:
            getContactsController().performSyncPhoneBook(r10, true, true, false, false, r27 ^ 1, false);
     */
    /* JADX WARNING: Missing block: B:122:0x021e, code skipped:
            return;
     */
    /* JADX WARNING: Missing block: B:123:0x021f, code skipped:
            if (r3 != null) goto L_0x0221;
     */
    /* JADX WARNING: Missing block: B:124:0x0221, code skipped:
            r3.dispose();
     */
    public /* synthetic */ void lambda$getCachedPhoneBook$85$MessagesStorage(boolean r27) {
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
        r0 = r1.database;	 Catch:{ all -> 0x00df }
        r14 = "SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'";
        r15 = new java.lang.Object[r13];	 Catch:{ all -> 0x00df }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ all -> 0x00df }
        r0 = r14.next();	 Catch:{ all -> 0x00dc }
        r14.dispose();	 Catch:{ all -> 0x00dc }
        if (r0 == 0) goto L_0x00d9;
    L_0x0022:
        r0 = r1.database;	 Catch:{ all -> 0x00df }
        r14 = "SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1";
        r15 = new java.lang.Object[r13];	 Catch:{ all -> 0x00df }
        r14 = r0.queryFinalized(r14, r15);	 Catch:{ all -> 0x00df }
        r0 = r14.next();	 Catch:{ all -> 0x00dc }
        if (r0 == 0) goto L_0x003b;
    L_0x0032:
        r0 = r14.intValue(r13);	 Catch:{ all -> 0x00dc }
        r0 = java.lang.Math.min(r12, r0);	 Catch:{ all -> 0x00dc }
        goto L_0x003d;
    L_0x003b:
        r0 = 16;
    L_0x003d:
        r14.dispose();	 Catch:{ all -> 0x00dc }
        r15 = new android.util.SparseArray;	 Catch:{ all -> 0x00dc }
        r15.<init>(r0);	 Catch:{ all -> 0x00dc }
        r0 = r1.database;	 Catch:{ all -> 0x00dc }
        r8 = "SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1";
        r10 = new java.lang.Object[r13];	 Catch:{ all -> 0x00dc }
        r10 = r0.queryFinalized(r8, r10);	 Catch:{ all -> 0x00dc }
    L_0x004f:
        r0 = r10.next();	 Catch:{ all -> 0x00d7 }
        if (r0 == 0) goto L_0x00cc;
    L_0x0055:
        r0 = r10.intValue(r13);	 Catch:{ all -> 0x00d7 }
        r8 = r15.get(r0);	 Catch:{ all -> 0x00d7 }
        r8 = (org.telegram.messenger.ContactsController.Contact) r8;	 Catch:{ all -> 0x00d7 }
        if (r8 != 0) goto L_0x0089;
    L_0x0061:
        r8 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ all -> 0x00d7 }
        r8.<init>();	 Catch:{ all -> 0x00d7 }
        r14 = r10.stringValue(r9);	 Catch:{ all -> 0x00d7 }
        r8.first_name = r14;	 Catch:{ all -> 0x00d7 }
        r14 = r10.stringValue(r5);	 Catch:{ all -> 0x00d7 }
        r8.last_name = r14;	 Catch:{ all -> 0x00d7 }
        r14 = r10.intValue(r3);	 Catch:{ all -> 0x00d7 }
        r8.imported = r14;	 Catch:{ all -> 0x00d7 }
        r14 = r8.first_name;	 Catch:{ all -> 0x00d7 }
        if (r14 != 0) goto L_0x007e;
    L_0x007c:
        r8.first_name = r2;	 Catch:{ all -> 0x00d7 }
    L_0x007e:
        r14 = r8.last_name;	 Catch:{ all -> 0x00d7 }
        if (r14 != 0) goto L_0x0084;
    L_0x0082:
        r8.last_name = r2;	 Catch:{ all -> 0x00d7 }
    L_0x0084:
        r8.contact_id = r0;	 Catch:{ all -> 0x00d7 }
        r15.put(r0, r8);	 Catch:{ all -> 0x00d7 }
    L_0x0089:
        r0 = r10.stringValue(r4);	 Catch:{ all -> 0x00d7 }
        if (r0 != 0) goto L_0x0090;
    L_0x008f:
        goto L_0x004f;
    L_0x0090:
        r14 = r8.phones;	 Catch:{ all -> 0x00d7 }
        r14.add(r0);	 Catch:{ all -> 0x00d7 }
        r14 = r10.stringValue(r7);	 Catch:{ all -> 0x00d7 }
        if (r14 != 0) goto L_0x009c;
    L_0x009b:
        goto L_0x004f;
    L_0x009c:
        r3 = r14.length();	 Catch:{ all -> 0x00d7 }
        if (r3 != r11) goto L_0x00ac;
    L_0x00a2:
        r3 = r0.length();	 Catch:{ all -> 0x00d7 }
        if (r3 == r11) goto L_0x00ac;
    L_0x00a8:
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ all -> 0x00d7 }
    L_0x00ac:
        r0 = r8.shortPhones;	 Catch:{ all -> 0x00d7 }
        r0.add(r14);	 Catch:{ all -> 0x00d7 }
        r0 = r8.phoneDeleted;	 Catch:{ all -> 0x00d7 }
        r3 = r10.intValue(r6);	 Catch:{ all -> 0x00d7 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x00d7 }
        r0.add(r3);	 Catch:{ all -> 0x00d7 }
        r0 = r8.phoneTypes;	 Catch:{ all -> 0x00d7 }
        r0.add(r2);	 Catch:{ all -> 0x00d7 }
        r0 = r15.size();	 Catch:{ all -> 0x00d7 }
        if (r0 != r12) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00cc;
    L_0x00ca:
        r3 = 6;
        goto L_0x004f;
    L_0x00cc:
        r10.dispose();	 Catch:{ all -> 0x00d7 }
        r0 = r26.getContactsController();	 Catch:{ all -> 0x00df }
        r0.migratePhoneBookToV7(r15);	 Catch:{ all -> 0x00df }
        return;
    L_0x00d7:
        r0 = move-exception;
        goto L_0x00e1;
    L_0x00d9:
        r17 = 0;
        goto L_0x00eb;
    L_0x00dc:
        r0 = move-exception;
        r10 = r14;
        goto L_0x00e1;
    L_0x00df:
        r0 = move-exception;
        r10 = 0;
    L_0x00e1:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x022d }
        if (r10 == 0) goto L_0x00e9;
    L_0x00e6:
        r10.dispose();
    L_0x00e9:
        r17 = r10;
    L_0x00eb:
        r0 = r1.database;	 Catch:{ all -> 0x0139 }
        r3 = "SELECT COUNT(key) FROM user_contacts_v7 WHERE 1";
        r8 = new java.lang.Object[r13];	 Catch:{ all -> 0x0139 }
        r3 = r0.queryFinalized(r3, r8);	 Catch:{ all -> 0x0139 }
        r0 = r3.next();	 Catch:{ all -> 0x0137 }
        if (r0 == 0) goto L_0x0130;
    L_0x00fb:
        r8 = r3.intValue(r13);	 Catch:{ all -> 0x0137 }
        r10 = java.lang.Math.min(r12, r8);	 Catch:{ all -> 0x012e }
        if (r8 <= r12) goto L_0x0109;
    L_0x0105:
        r0 = r8 + -5000;
        r14 = r0;
        goto L_0x010a;
    L_0x0109:
        r14 = 0;
    L_0x010a:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ all -> 0x012a }
        if (r0 == 0) goto L_0x0127;
    L_0x010e:
        r0 = new java.lang.StringBuilder;	 Catch:{ all -> 0x012a }
        r0.<init>();	 Catch:{ all -> 0x012a }
        r15 = r1.currentAccount;	 Catch:{ all -> 0x012a }
        r0.append(r15);	 Catch:{ all -> 0x012a }
        r15 = " current cached contacts count = ";
        r0.append(r15);	 Catch:{ all -> 0x012a }
        r0.append(r8);	 Catch:{ all -> 0x012a }
        r0 = r0.toString();	 Catch:{ all -> 0x012a }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ all -> 0x012a }
    L_0x0127:
        r16 = r10;
        goto L_0x0134;
    L_0x012a:
        r0 = move-exception;
        r16 = r10;
        goto L_0x0140;
    L_0x012e:
        r0 = move-exception;
        goto L_0x013d;
    L_0x0130:
        r8 = 0;
        r14 = 0;
        r16 = 16;
    L_0x0134:
        if (r3 == 0) goto L_0x0148;
    L_0x0136:
        goto L_0x0145;
    L_0x0137:
        r0 = move-exception;
        goto L_0x013c;
    L_0x0139:
        r0 = move-exception;
        r3 = r17;
    L_0x013c:
        r8 = 0;
    L_0x013d:
        r14 = 0;
        r16 = 16;
    L_0x0140:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0225 }
        if (r3 == 0) goto L_0x0148;
    L_0x0145:
        r3.dispose();
    L_0x0148:
        r0 = r16;
        r10 = new java.util.HashMap;
        r10.<init>(r0);
        if (r14 == 0) goto L_0x016b;
    L_0x0151:
        r0 = r1.database;	 Catch:{ Exception -> 0x01fd }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01fd }
        r14.<init>();	 Catch:{ Exception -> 0x01fd }
        r15 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0,";
        r14.append(r15);	 Catch:{ Exception -> 0x01fd }
        r14.append(r8);	 Catch:{ Exception -> 0x01fd }
        r8 = r14.toString();	 Catch:{ Exception -> 0x01fd }
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x01fd }
        r0 = r0.queryFinalized(r8, r14);	 Catch:{ Exception -> 0x01fd }
        goto L_0x0175;
    L_0x016b:
        r0 = r1.database;	 Catch:{ Exception -> 0x01fd }
        r8 = "SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1";
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x01fd }
        r0 = r0.queryFinalized(r8, r14);	 Catch:{ Exception -> 0x01fd }
    L_0x0175:
        r3 = r0;
    L_0x0176:
        r0 = r3.next();	 Catch:{ Exception -> 0x01fd }
        if (r0 == 0) goto L_0x01f7;
    L_0x017c:
        r0 = r3.stringValue(r13);	 Catch:{ Exception -> 0x01fd }
        r8 = r10.get(r0);	 Catch:{ Exception -> 0x01fd }
        r8 = (org.telegram.messenger.ContactsController.Contact) r8;	 Catch:{ Exception -> 0x01fd }
        if (r8 != 0) goto L_0x01b5;
    L_0x0188:
        r8 = new org.telegram.messenger.ContactsController$Contact;	 Catch:{ Exception -> 0x01fd }
        r8.<init>();	 Catch:{ Exception -> 0x01fd }
        r14 = r3.intValue(r9);	 Catch:{ Exception -> 0x01fd }
        r8.contact_id = r14;	 Catch:{ Exception -> 0x01fd }
        r14 = r3.stringValue(r5);	 Catch:{ Exception -> 0x01fd }
        r8.first_name = r14;	 Catch:{ Exception -> 0x01fd }
        r14 = r3.stringValue(r4);	 Catch:{ Exception -> 0x01fd }
        r8.last_name = r14;	 Catch:{ Exception -> 0x01fd }
        r14 = 7;
        r14 = r3.intValue(r14);	 Catch:{ Exception -> 0x01fd }
        r8.imported = r14;	 Catch:{ Exception -> 0x01fd }
        r14 = r8.first_name;	 Catch:{ Exception -> 0x01fd }
        if (r14 != 0) goto L_0x01ac;
    L_0x01aa:
        r8.first_name = r2;	 Catch:{ Exception -> 0x01fd }
    L_0x01ac:
        r14 = r8.last_name;	 Catch:{ Exception -> 0x01fd }
        if (r14 != 0) goto L_0x01b2;
    L_0x01b0:
        r8.last_name = r2;	 Catch:{ Exception -> 0x01fd }
    L_0x01b2:
        r10.put(r0, r8);	 Catch:{ Exception -> 0x01fd }
    L_0x01b5:
        r0 = r3.stringValue(r7);	 Catch:{ Exception -> 0x01fd }
        if (r0 != 0) goto L_0x01bd;
    L_0x01bb:
        r14 = 6;
        goto L_0x0176;
    L_0x01bd:
        r14 = r8.phones;	 Catch:{ Exception -> 0x01fd }
        r14.add(r0);	 Catch:{ Exception -> 0x01fd }
        r14 = r3.stringValue(r6);	 Catch:{ Exception -> 0x01fd }
        if (r14 != 0) goto L_0x01c9;
    L_0x01c8:
        goto L_0x01bb;
    L_0x01c9:
        r15 = r14.length();	 Catch:{ Exception -> 0x01fd }
        if (r15 != r11) goto L_0x01d9;
    L_0x01cf:
        r15 = r0.length();	 Catch:{ Exception -> 0x01fd }
        if (r15 == r11) goto L_0x01d9;
    L_0x01d5:
        r14 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r0);	 Catch:{ Exception -> 0x01fd }
    L_0x01d9:
        r0 = r8.shortPhones;	 Catch:{ Exception -> 0x01fd }
        r0.add(r14);	 Catch:{ Exception -> 0x01fd }
        r0 = r8.phoneDeleted;	 Catch:{ Exception -> 0x01fd }
        r14 = 6;
        r15 = r3.intValue(r14);	 Catch:{ Exception -> 0x01fd }
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x01fd }
        r0.add(r15);	 Catch:{ Exception -> 0x01fd }
        r0 = r8.phoneTypes;	 Catch:{ Exception -> 0x01fd }
        r0.add(r2);	 Catch:{ Exception -> 0x01fd }
        r0 = r10.size();	 Catch:{ Exception -> 0x01fd }
        if (r0 != r12) goto L_0x0176;
    L_0x01f7:
        r3.dispose();	 Catch:{ Exception -> 0x01fd }
        goto L_0x0209;
    L_0x01fb:
        r0 = move-exception;
        goto L_0x021f;
    L_0x01fd:
        r0 = move-exception;
        r10.clear();	 Catch:{ all -> 0x01fb }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01fb }
        if (r3 == 0) goto L_0x0209;
    L_0x0206:
        r3.dispose();
    L_0x0209:
        r18 = r26.getContactsController();
        r20 = 1;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r24 = r27 ^ 1;
        r25 = 0;
        r19 = r10;
        r18.performSyncPhoneBook(r19, r20, r21, r22, r23, r24, r25);
        return;
    L_0x021f:
        if (r3 == 0) goto L_0x0224;
    L_0x0221:
        r3.dispose();
    L_0x0224:
        throw r0;
    L_0x0225:
        r0 = move-exception;
        r2 = r0;
        if (r3 == 0) goto L_0x022c;
    L_0x0229:
        r3.dispose();
    L_0x022c:
        throw r2;
    L_0x022d:
        r0 = move-exception;
        r2 = r0;
        if (r10 == 0) goto L_0x0234;
    L_0x0231:
        r10.dispose();
    L_0x0234:
        goto L_0x0236;
    L_0x0235:
        throw r2;
    L_0x0236:
        goto L_0x0235;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getCachedPhoneBook$85$MessagesStorage(boolean):void");
    }

    public void getContacts() {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5zHvct2d9Zg3BaO2FKDse8bk3gc(this));
    }

    public /* synthetic */ void lambda$getContacts$86$MessagesStorage() {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$C1cOzbvtNsbNZV4b19cMYFCSS2Q(this, i));
    }

    public /* synthetic */ void lambda$getUnsentMessages$87$MessagesStorage(int i) {
        try {
            int i2;
            NativeByteBuffer byteBufferValue;
            int i3;
            ArrayList arrayList;
            ArrayList arrayList2;
            int i4;
            SparseArray sparseArray = new SparseArray();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            ArrayList arrayList8 = new ArrayList();
            ArrayList arrayList9 = new ArrayList();
            ArrayList arrayList10 = new ArrayList();
            SQLiteDatabase sQLiteDatabase = this.database;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY m.mid DESC LIMIT ");
            stringBuilder.append(i);
            int i5 = 0;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(stringBuilder.toString(), new Object[0]);
            while (true) {
                i2 = 1;
                if (!queryFinalized.next()) {
                    break;
                }
                byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.send_state = queryFinalized.intValue(2);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize.id) < 0) {
                        MessageObject.setUnreadFlags(TLdeserialize, queryFinalized.intValue(0));
                        TLdeserialize.id = queryFinalized.intValue(3);
                        TLdeserialize.date = queryFinalized.intValue(4);
                        if (!queryFinalized.isNull(5)) {
                            TLdeserialize.random_id = queryFinalized.longValue(5);
                        }
                        TLdeserialize.dialog_id = queryFinalized.longValue(6);
                        TLdeserialize.seq_in = queryFinalized.intValue(7);
                        TLdeserialize.seq_out = queryFinalized.intValue(8);
                        TLdeserialize.ttl = queryFinalized.intValue(9);
                        arrayList3.add(TLdeserialize);
                        sparseArray.put(TLdeserialize.id, TLdeserialize);
                        i3 = (int) TLdeserialize.dialog_id;
                        int i6 = (int) (TLdeserialize.dialog_id >> 32);
                        if (i3 != 0) {
                            if (i3 < 0) {
                                int i7 = -i3;
                                if (!arrayList9.contains(Integer.valueOf(i7))) {
                                    arrayList9.add(Integer.valueOf(i7));
                                }
                            } else if (!arrayList8.contains(Integer.valueOf(i3))) {
                                arrayList8.add(Integer.valueOf(i3));
                            }
                        } else if (!arrayList10.contains(Integer.valueOf(i6))) {
                            arrayList10.add(Integer.valueOf(i6));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize, arrayList8, arrayList9);
                        if (TLdeserialize.send_state != 3 && (!(TLdeserialize.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize) || i3 == 0) || TLdeserialize.id > 0)) {
                            TLdeserialize.send_state = 0;
                        }
                    }
                }
            }
            queryFinalized.dispose();
            queryFinalized = this.database.queryFinalized("SELECT m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, m.ttl FROM scheduled_messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE (m.mid < 0 AND m.send_state = 1) OR (m.mid > 0 AND m.send_state = 3) ORDER BY date ASC", new Object[0]);
            while (queryFinalized.next()) {
                byteBufferValue = queryFinalized.byteBufferValue(i5);
                if (byteBufferValue != null) {
                    Message TLdeserialize2 = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i5), i5);
                    TLdeserialize2.send_state = queryFinalized.intValue(i2);
                    TLdeserialize2.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (sparseArray.indexOfKey(TLdeserialize2.id) < 0) {
                        TLdeserialize2.id = queryFinalized.intValue(2);
                        TLdeserialize2.date = queryFinalized.intValue(3);
                        if (!queryFinalized.isNull(4)) {
                            TLdeserialize2.random_id = queryFinalized.longValue(4);
                        }
                        arrayList = arrayList5;
                        arrayList2 = arrayList6;
                        TLdeserialize2.dialog_id = queryFinalized.longValue(5);
                        TLdeserialize2.ttl = queryFinalized.intValue(6);
                        arrayList4.add(TLdeserialize2);
                        sparseArray.put(TLdeserialize2.id, TLdeserialize2);
                        i4 = (int) TLdeserialize2.dialog_id;
                        i5 = (int) (TLdeserialize2.dialog_id >> 32);
                        if (i4 != 0) {
                            if (i4 < 0) {
                                i3 = -i4;
                                if (!arrayList9.contains(Integer.valueOf(i3))) {
                                    arrayList9.add(Integer.valueOf(i3));
                                }
                            } else if (!arrayList8.contains(Integer.valueOf(i4))) {
                                arrayList8.add(Integer.valueOf(i4));
                            }
                        } else if (!arrayList10.contains(Integer.valueOf(i5))) {
                            arrayList10.add(Integer.valueOf(i5));
                        }
                        addUsersAndChatsFromMessage(TLdeserialize2, arrayList8, arrayList9);
                        if (TLdeserialize2.send_state == 3 || ((TLdeserialize2.to_id.channel_id != 0 || MessageObject.isUnread(TLdeserialize2) || i4 == 0) && TLdeserialize2.id <= 0)) {
                            arrayList5 = arrayList;
                            arrayList6 = arrayList2;
                            i5 = 0;
                            i2 = 1;
                        } else {
                            TLdeserialize2.send_state = 0;
                            arrayList5 = arrayList;
                            arrayList6 = arrayList2;
                            i5 = 0;
                            i2 = 1;
                        }
                    }
                }
                arrayList = arrayList5;
                arrayList2 = arrayList6;
                arrayList5 = arrayList;
                arrayList6 = arrayList2;
                i5 = 0;
                i2 = 1;
            }
            arrayList = arrayList5;
            arrayList2 = arrayList6;
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList10.isEmpty()) {
                getEncryptedChatsInternal(TextUtils.join(str, arrayList10), arrayList7, arrayList8);
            }
            if (!arrayList8.isEmpty()) {
                getUsersInternal(TextUtils.join(str, arrayList8), arrayList);
            }
            if (arrayList9.isEmpty()) {
                arrayList6 = arrayList2;
            } else {
                StringBuilder stringBuilder2 = new StringBuilder();
                for (i4 = 0; i4 < arrayList9.size(); i4++) {
                    Integer num = (Integer) arrayList9.get(i4);
                    if (stringBuilder2.length() != 0) {
                        stringBuilder2.append(str);
                    }
                    stringBuilder2.append(num);
                }
                arrayList6 = arrayList2;
                getChatsInternal(stringBuilder2.toString(), arrayList6);
            }
            getSendMessagesHelper().processUnsentMessages(arrayList3, arrayList4, arrayList, arrayList6, arrayList7);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean checkMessageByRandomId(long j) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$8LScnlQZslO0EbqFvovKSsTTFc0(this, j, zArr, countDownLatch));
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
    public /* synthetic */ void lambda$checkMessageByRandomId$88$MessagesStorage(long r7, boolean[] r9, java.util.concurrent.CountDownLatch r10) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageByRandomId$88$MessagesStorage(long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public boolean checkMessageId(long j, boolean z, int i) {
        boolean[] zArr = new boolean[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$h0vkrdxti-V1B_YELNc1_e0rGWw(this, i, z, j, zArr, countDownLatch));
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
    public /* synthetic */ void lambda$checkMessageId$89$MessagesStorage(int r6, boolean r7, long r8, boolean[] r10, java.util.concurrent.CountDownLatch r11) {
        /*
        r5 = this;
        r0 = (long) r6;
        if (r7 == 0) goto L_0x0008;
    L_0x0003:
        r6 = -r8;
        r2 = 32;
        r6 = r6 << r2;
        r0 = r0 | r6;
    L_0x0008:
        r6 = 0;
        r7 = r5.database;	 Catch:{ Exception -> 0x0037 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0037 }
        r3 = "SELECT mid FROM messages WHERE uid = %d AND mid = %d";	 Catch:{ Exception -> 0x0037 }
        r4 = 2;	 Catch:{ Exception -> 0x0037 }
        r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0037 }
        r8 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0037 }
        r9 = 0;	 Catch:{ Exception -> 0x0037 }
        r4[r9] = r8;	 Catch:{ Exception -> 0x0037 }
        r8 = java.lang.Long.valueOf(r0);	 Catch:{ Exception -> 0x0037 }
        r0 = 1;	 Catch:{ Exception -> 0x0037 }
        r4[r0] = r8;	 Catch:{ Exception -> 0x0037 }
        r8 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x0037 }
        r1 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0037 }
        r6 = r7.queryFinalized(r8, r1);	 Catch:{ Exception -> 0x0037 }
        r7 = r6.next();	 Catch:{ Exception -> 0x0037 }
        if (r7 == 0) goto L_0x0032;	 Catch:{ Exception -> 0x0037 }
    L_0x0030:
        r10[r9] = r0;	 Catch:{ Exception -> 0x0037 }
    L_0x0032:
        if (r6 == 0) goto L_0x0040;
    L_0x0034:
        goto L_0x003d;
    L_0x0035:
        r7 = move-exception;
        goto L_0x0044;
    L_0x0037:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x0035 }
        if (r6 == 0) goto L_0x0040;
    L_0x003d:
        r6.dispose();
    L_0x0040:
        r11.countDown();
        return;
    L_0x0044:
        if (r6 == 0) goto L_0x0049;
    L_0x0046:
        r6.dispose();
    L_0x0049:
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$checkMessageId$89$MessagesStorage(int, boolean, long, boolean[], java.util.concurrent.CountDownLatch):void");
    }

    public void getUnreadMention(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$aY9xMvh-rzPL-R1D-_XrV6Vd9Hg(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getUnreadMention$91$MessagesStorage(long j, IntCallback intCallback) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$bAJRVxJL-F8T3vg8lkbGW9uJ6ck(intCallback, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessagesCount(long j, IntCallback intCallback) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Ek9AwSToKVzhFSLd8S5b9_EIPuk(this, j, intCallback));
    }

    public /* synthetic */ void lambda$getMessagesCount$93$MessagesStorage(long j, IntCallback intCallback) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$uKoT1QRUcZ9KZW59r0X9RxYbAXY(intCallback, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void getMessages(long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2, int i7) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$1ckIoqYQ8Nm11SUQ6RtmYLxVDXE(this, i, i2, z, j, z2, i6, i4, i3, i5, i7));
    }

    /* JADX WARNING: Removed duplicated region for block: B:826:0x13b5 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x13a7 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x13fe A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:829:0x13bc A:{SYNTHETIC, Splitter:B:829:0x13bc} */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x13a7 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:826:0x13b5 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:829:0x13bc A:{SYNTHETIC, Splitter:B:829:0x13bc} */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x13fe A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0ba8  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0bfc  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0bab A:{SYNTHETIC, Splitter:B:505:0x0bab} */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0d33 A:{Catch:{ Exception -> 0x0db1, all -> 0x0daf }} */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:523:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0ba8  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0bab A:{SYNTHETIC, Splitter:B:505:0x0bab} */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0bfc  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:523:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0d33 A:{Catch:{ Exception -> 0x0db1, all -> 0x0daf }} */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x11ce  */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x14a8 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:882:0x14c9 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:904:0x1509 A:{Catch:{ Exception -> 0x1733, all -> 0x1730 }} */
    /* JADX WARNING: Removed duplicated region for block: B:971:0x165f  */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x15a3 A:{SYNTHETIC, Splitter:B:935:0x15a3} */
    /* JADX WARNING: Removed duplicated region for block: B:1019:0x1730 A:{Splitter:B:724:0x11d0, PHI: r3 r22 r43 r44 r50 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:1019:0x1730 A:{Splitter:B:724:0x11d0, PHI: r3 r22 r43 r44 r50 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:1019:0x1730 A:{Splitter:B:724:0x11d0, PHI: r3 r22 r43 r44 r50 , ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:840:0x13ee, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:842:0x13f2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:843:0x13f3, code skipped:
            r29 = r5;
            r28 = r7;
     */
    /* JADX WARNING: Missing block: B:865:0x1468, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:866:0x1469, code skipped:
            r43 = r4;
     */
    /* JADX WARNING: Missing block: B:872:0x1488, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:873:0x1489, code skipped:
            r43 = r4;
     */
    /* JADX WARNING: Missing block: B:1019:0x1730, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:1020:0x1733, code skipped:
            r0 = e;
     */
    public /* synthetic */ void lambda$getMessages$95$MessagesStorage(int r43, int r44, boolean r45, long r46, boolean r48, int r49, int r50, int r51, int r52, int r53) {
        /*
        r42 = this;
        r1 = r42;
        r2 = r43;
        r3 = r44;
        r4 = r46;
        r15 = r49;
        r6 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r6.<init>();
        r7 = r42.getUserConfig();
        r7 = r7.clientUserId;
        r8 = (long) r3;
        if (r45 == 0) goto L_0x001b;
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
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r9.<init>();	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r10.<init>();	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r12.<init>();	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r21 = r13;
        r13 = new android.util.SparseArray;	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r13.<init>();	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r14 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r14.<init>();	 Catch:{ Exception -> 0x1788, all -> 0x1776 }
        r23 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r48 == 0) goto L_0x01ec;
    L_0x0054:
        r8 = r1.database;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r11 = java.util.Locale.US;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3 = "SELECT m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.ttl FROM scheduled_messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC";
        r2 = 1;
        r1 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r15 = 0;
        r1[r15] = r2;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r1 = java.lang.String.format(r11, r3, r1);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r1 = r8.queryFinalized(r1, r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x006e:
        r2 = r1.next();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x01a0;
    L_0x0074:
        r2 = r1.byteBufferValue(r15);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x0197;
    L_0x007a:
        r3 = r2.readInt32(r15);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r2, r3, r15);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = 1;
        r11 = r1.intValue(r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.send_state = r11;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = 2;
        r11 = r1.intValue(r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.id = r11;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = r3.id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r8 <= 0) goto L_0x00a0;
    L_0x0094:
        r8 = r3.send_state;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r8 == 0) goto L_0x00a0;
    L_0x0098:
        r8 = r3.send_state;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r11 = 3;
        if (r8 == r11) goto L_0x00a0;
    L_0x009d:
        r8 = 0;
        r3.send_state = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x00a0:
        r26 = r14;
        r14 = (long) r7;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r8 != 0) goto L_0x00ae;
    L_0x00a7:
        r8 = 1;
        r3.out = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = 0;
        r3.unread = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        goto L_0x00b1;
    L_0x00ae:
        r8 = 1;
        r3.unread = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x00b1:
        r3.readAttachPath(r2, r7);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2.reuse();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = 3;
        r8 = r1.intValue(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.date = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.dialog_id = r4;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r3.ttl;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x00cb;
    L_0x00c4:
        r2 = 6;
        r8 = r1.intValue(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.ttl = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x00cb:
        r2 = r6.messages;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2.add(r3);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        addUsersAndChatsFromMessage(r3, r9, r10);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x00e6;
    L_0x00d7:
        r14 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r16 = 0;
        r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x00e0;
    L_0x00df:
        goto L_0x00e6;
    L_0x00e0:
        r27 = r9;
    L_0x00e2:
        r2 = r26;
        goto L_0x019a;
    L_0x00e6:
        r2 = 5;
        r8 = r1.isNull(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r8 != 0) goto L_0x011d;
    L_0x00ed:
        r8 = r1.byteBufferValue(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r8 == 0) goto L_0x011d;
    L_0x00f3:
        r2 = 0;
        r11 = r8.readInt32(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r11 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r11, r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r3.replyMessage = r11;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r3.replyMessage;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2.readAttachPath(r8, r7);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8.reuse();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r3.replyMessage;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x011d;
    L_0x010a:
        r2 = org.telegram.messenger.MessageObject.isMegagroup(r3);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x0118;
    L_0x0110:
        r2 = r3.replyMessage;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = r2.flags;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = r8 | r23;
        r2.flags = r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x0118:
        r2 = r3.replyMessage;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        addUsersAndChatsFromMessage(r2, r9, r10);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x011d:
        r2 = r3.replyMessage;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x00e0;
    L_0x0121:
        r2 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x0166;
    L_0x0125:
        r2 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r14 = (long) r2;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r3.to_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 == 0) goto L_0x013a;
    L_0x012e:
        r2 = r3.to_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r27 = r9;
        r8 = (long) r2;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = 32;
        r8 = r8 << r2;
        r14 = r14 | r8;
        goto L_0x013c;
    L_0x013a:
        r27 = r9;
    L_0x013c:
        r2 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r12.contains(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x014d;
    L_0x0146:
        r2 = java.lang.Long.valueOf(r14);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r12.add(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x014d:
        r2 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r13.get(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = (java.util.ArrayList) r2;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x0161;
    L_0x0157:
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2.<init>();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r13.put(r8, r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x0161:
        r2.add(r3);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        goto L_0x00e2;
    L_0x0166:
        r27 = r9;
        r8 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r12.contains(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r2 != 0) goto L_0x017d;
    L_0x0174:
        r8 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r12.add(r2);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x017d:
        r8 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2 = r26;
        r8 = r2.get(r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8 = (java.util.ArrayList) r8;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        if (r8 != 0) goto L_0x0193;
    L_0x0189:
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r8.<init>();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r14 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r2.put(r14, r8);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
    L_0x0193:
        r8.add(r3);	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        goto L_0x019a;
    L_0x0197:
        r27 = r9;
        r2 = r14;
    L_0x019a:
        r14 = r2;
        r9 = r27;
        r15 = 0;
        goto L_0x006e;
    L_0x01a0:
        r27 = r9;
        r2 = r14;
        r1.dispose();	 Catch:{ Exception -> 0x01d9, all -> 0x01c6 }
        r14 = r42;
        r15 = r49;
        r4 = r2;
        r3 = r6;
        r29 = r7;
        r32 = r12;
        r36 = r13;
        r33 = r27;
        r1 = 0;
        r11 = 0;
        r12 = 0;
        r18 = 1;
        r21 = 0;
        r22 = 0;
        r35 = 0;
        r6 = r43;
        r7 = r44;
        r13 = r10;
        goto L_0x1541;
    L_0x01c6:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
    L_0x01d1:
        r20 = 0;
        r21 = 0;
    L_0x01d5:
        r6 = r43;
        goto L_0x17c4;
    L_0x01d9:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 1;
    L_0x01e4:
        r20 = 0;
        r21 = 0;
    L_0x01e8:
        r6 = r43;
        goto L_0x1799;
    L_0x01ec:
        r27 = r9;
        r2 = r14;
        r1 = (int) r4;
        if (r1 == 0) goto L_0x0e4e;
    L_0x01f2:
        r9 = "SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = ";
        r15 = r49;
        r14 = 3;
        if (r15 != r14) goto L_0x02c9;
    L_0x01f9:
        if (r50 != 0) goto L_0x02c9;
    L_0x01fb:
        r14 = r42;
        r8 = r14.database;	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.<init>();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.append(r9);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.append(r4);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = r3.toString();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r28 = r2;
        r9 = 0;
        r2 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r2 = r8.queryFinalized(r3, r2);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = r2.next();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        if (r3 == 0) goto L_0x026e;
    L_0x021d:
        r3 = r2.intValue(r9);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r8 = 1;
        r3 = r3 + r8;
        r9 = r2.intValue(r8);	 Catch:{ Exception -> 0x0266, all -> 0x025e }
        r8 = 2;
        r29 = r2.intValue(r8);	 Catch:{ Exception -> 0x0254, all -> 0x024a }
        r8 = 3;
        r30 = r2.intValue(r8);	 Catch:{ Exception -> 0x023e, all -> 0x0232 }
        goto L_0x0274;
    L_0x0232:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
        r14 = r29;
    L_0x023b:
        r12 = 0;
        goto L_0x02b8;
    L_0x023e:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
        r14 = r29;
    L_0x0247:
        r12 = 0;
        goto L_0x02c5;
    L_0x024a:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
    L_0x0251:
        r12 = 0;
        goto L_0x02b7;
    L_0x0254:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
    L_0x025b:
        r12 = 0;
        goto L_0x02c4;
    L_0x025e:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        goto L_0x02b5;
    L_0x0266:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        goto L_0x02c2;
    L_0x026e:
        r3 = 0;
        r9 = 0;
        r29 = 0;
        r30 = 0;
    L_0x0274:
        r2.dispose();	 Catch:{ Exception -> 0x029d, all -> 0x028b }
        r2 = r43;
        r34 = r3;
        r37 = r6;
        r36 = r10;
        r32 = r12;
        r35 = r29;
        r3 = 0;
        r10 = 0;
        r6 = r44;
        r29 = r13;
        goto L_0x0642;
    L_0x028b:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
        r14 = r29;
        r21 = r30;
        r12 = 0;
        r17 = 0;
        r20 = 0;
        goto L_0x01d5;
    L_0x029d:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r3 = r6;
        r13 = r9;
        r14 = r29;
        r21 = r30;
        r12 = 0;
        r17 = 0;
        r20 = 0;
        goto L_0x01e8;
    L_0x02af:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = 0;
    L_0x02b5:
        r12 = 0;
        r13 = 0;
    L_0x02b7:
        r14 = 0;
    L_0x02b8:
        r17 = 0;
        goto L_0x01d1;
    L_0x02bc:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = 0;
    L_0x02c2:
        r12 = 0;
        r13 = 0;
    L_0x02c4:
        r14 = 0;
    L_0x02c5:
        r17 = 0;
        goto L_0x01e4;
    L_0x02c9:
        r14 = r42;
        r28 = r2;
        r2 = 1;
        if (r15 == r2) goto L_0x062d;
    L_0x02d0:
        r2 = 3;
        if (r15 == r2) goto L_0x062d;
    L_0x02d3:
        r2 = 4;
        if (r15 == r2) goto L_0x062d;
    L_0x02d6:
        if (r50 != 0) goto L_0x062d;
    L_0x02d8:
        r2 = 2;
        if (r15 != r2) goto L_0x05c2;
    L_0x02db:
        r2 = r14.database;	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.<init>();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.append(r9);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3.append(r4);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = r3.toString();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r29 = r13;
        r9 = 0;
        r13 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r2 = r2.queryFinalized(r3, r13);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r3 = r2.next();	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        if (r3 == 0) goto L_0x0386;
    L_0x02fb:
        r3 = r2.intValue(r9);	 Catch:{ Exception -> 0x02bc, all -> 0x02af }
        r9 = r12;
        r12 = (long) r3;
        r21 = r3;
        r3 = 1;
        r22 = r2.intValue(r3);	 Catch:{ Exception -> 0x037d, all -> 0x0374 }
        r3 = 2;
        r30 = r2.intValue(r3);	 Catch:{ Exception -> 0x0369, all -> 0x035e }
        r3 = 3;
        r31 = r2.intValue(r3);	 Catch:{ Exception -> 0x0351, all -> 0x0344 }
        r16 = 0;
        r3 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x0324;
    L_0x0318:
        if (r11 == 0) goto L_0x0324;
    L_0x031a:
        r32 = r9;
        r3 = r10;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r9 = r9 | r12;
        goto L_0x0328;
    L_0x0324:
        r32 = r9;
        r3 = r10;
        r9 = r12;
    L_0x0328:
        r12 = (long) r7;
        r33 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        r12 = r21;
        r13 = r30;
        if (r33 != 0) goto L_0x0339;
    L_0x0331:
        r22 = r31;
        r30 = r9;
        r9 = 1;
        r10 = 0;
        goto L_0x0393;
    L_0x0339:
        r40 = r9;
        r10 = r22;
        r22 = r31;
        r9 = 1;
        r30 = r40;
        goto L_0x0393;
    L_0x0344:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        r13 = r22;
        r14 = r30;
        goto L_0x023b;
    L_0x0351:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        r13 = r22;
        r14 = r30;
        goto L_0x0247;
    L_0x035e:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        r13 = r22;
        goto L_0x0251;
    L_0x0369:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        r13 = r22;
        goto L_0x025b;
    L_0x0374:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        goto L_0x02b5;
    L_0x037d:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r11 = r21;
        goto L_0x02c2;
    L_0x0386:
        r3 = r10;
        r32 = r12;
        r30 = r21;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r13 = 0;
        r22 = 0;
        r21 = r44;
    L_0x0393:
        r2.dispose();	 Catch:{ Exception -> 0x05a5, all -> 0x0588 }
        if (r9 != 0) goto L_0x046d;
    L_0x0398:
        r2 = r14.database;	 Catch:{ Exception -> 0x0457, all -> 0x0441 }
        r33 = r9;
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x043f, all -> 0x043d }
        r34 = r12;
        r12 = "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";
        r36 = r3;
        r35 = r13;
        r13 = 1;
        r3 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0437, all -> 0x0435 }
        r13 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0437, all -> 0x0435 }
        r37 = r6;
        r6 = 0;
        r3[r6] = r13;	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r3 = java.lang.String.format(r9, r12, r3);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r2 = r2.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r3 = r2.next();	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        if (r3 == 0) goto L_0x03ec;
    L_0x03c2:
        r3 = r2.intValue(r6);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r6 = 1;
        r9 = r2.intValue(r6);	 Catch:{ Exception -> 0x03de, all -> 0x03d0 }
        r34 = r3;
        r35 = r9;
        goto L_0x03ec;
    L_0x03d0:
        r0 = move-exception;
        r6 = r43;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r13 = r10;
        r21 = r22;
        r20 = r33;
        goto L_0x0615;
    L_0x03de:
        r0 = move-exception;
        r6 = r43;
        r7 = r44;
        r1 = r0;
        r11 = r3;
        r13 = r10;
        r21 = r22;
        r20 = r33;
        goto L_0x0627;
    L_0x03ec:
        r2.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        if (r34 == 0) goto L_0x0421;
    L_0x03f1:
        r2 = r14.database;	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r6 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)";
        r9 = 2;
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r9 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r13 = 0;
        r12[r13] = r9;	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r9 = java.lang.Integer.valueOf(r34);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r19 = 1;
        r12[r19] = r9;	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r3 = java.lang.String.format(r3, r6, r12);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r2 = r2.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r3 = r2.next();	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        if (r3 == 0) goto L_0x041e;
    L_0x0419:
        r3 = r2.intValue(r13);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        r10 = r3;
    L_0x041e:
        r2.dispose();	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
    L_0x0421:
        r2 = r43;
        goto L_0x05d9;
    L_0x0425:
        r0 = move-exception;
        r6 = r43;
        r7 = r44;
        r1 = r0;
        goto L_0x060e;
    L_0x042d:
        r0 = move-exception;
        r6 = r43;
        r7 = r44;
        r1 = r0;
        goto L_0x0620;
    L_0x0435:
        r0 = move-exception;
        goto L_0x0448;
    L_0x0437:
        r0 = move-exception;
        goto L_0x045e;
    L_0x0439:
        r0 = move-exception;
        goto L_0x0446;
    L_0x043b:
        r0 = move-exception;
        goto L_0x045c;
    L_0x043d:
        r0 = move-exception;
        goto L_0x0444;
    L_0x043f:
        r0 = move-exception;
        goto L_0x045a;
    L_0x0441:
        r0 = move-exception;
        r33 = r9;
    L_0x0444:
        r34 = r12;
    L_0x0446:
        r35 = r13;
    L_0x0448:
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r13 = r10;
        r21 = r22;
        r20 = r33;
        r11 = r34;
        r14 = r35;
        goto L_0x05a0;
    L_0x0457:
        r0 = move-exception;
        r33 = r9;
    L_0x045a:
        r34 = r12;
    L_0x045c:
        r35 = r13;
    L_0x045e:
        r7 = r44;
        r1 = r0;
        r3 = r6;
        r13 = r10;
        r21 = r22;
        r20 = r33;
        r11 = r34;
        r14 = r35;
        goto L_0x05bd;
    L_0x046d:
        r36 = r3;
        r37 = r6;
        r33 = r9;
        r34 = r12;
        r35 = r13;
        if (r21 != 0) goto L_0x04fd;
    L_0x0479:
        r2 = r14.database;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r6 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)";
        r9 = 1;
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r9 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r13 = 0;
        r12[r13] = r9;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = java.lang.String.format(r3, r6, r12);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r2 = r2.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = r2.next();	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        if (r3 == 0) goto L_0x049e;
    L_0x0499:
        r3 = r2.intValue(r13);	 Catch:{ Exception -> 0x042d, all -> 0x0425 }
        goto L_0x049f;
    L_0x049e:
        r3 = 0;
    L_0x049f:
        r2.dispose();	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        if (r3 != r10) goto L_0x04eb;
    L_0x04a4:
        r2 = r14.database;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r6 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0";
        r9 = 1;
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r9 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r13 = 0;
        r12[r13] = r9;	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = java.lang.String.format(r3, r6, r12);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r2 = r2.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r3 = r2.next();	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        if (r3 == 0) goto L_0x04e2;
    L_0x04c4:
        r3 = r2.intValue(r13);	 Catch:{ Exception -> 0x04f9, all -> 0x04f5 }
        r12 = (long) r3;
        r16 = 0;
        r6 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r6 == 0) goto L_0x04dc;
    L_0x04cf:
        if (r11 == 0) goto L_0x04dc;
    L_0x04d1:
        r6 = r10;
        r9 = (long) r11;
        r18 = 32;
        r9 = r9 << r18;
        r30 = r12 | r9;
        r34 = r3;
        goto L_0x04e5;
    L_0x04dc:
        r6 = r10;
        r34 = r3;
        r30 = r12;
        goto L_0x04e5;
    L_0x04e2:
        r6 = r10;
        r3 = r21;
    L_0x04e5:
        r2.dispose();	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r21 = r3;
        goto L_0x04ec;
    L_0x04eb:
        r6 = r10;
    L_0x04ec:
        r10 = r34;
        r2 = r43;
        r34 = r10;
    L_0x04f2:
        r10 = r6;
        goto L_0x05d9;
    L_0x04f5:
        r0 = move-exception;
        r6 = r10;
        goto L_0x0592;
    L_0x04f9:
        r0 = move-exception;
        r6 = r10;
        goto L_0x05af;
    L_0x04fd:
        r6 = r10;
        r2 = r14.database;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r9 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";
        r10 = 3;
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r13 = 0;
        r12[r13] = r10;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r10 = java.lang.Integer.valueOf(r21);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r13 = 1;
        r12[r13] = r10;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r10 = java.lang.Integer.valueOf(r21);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r13 = 2;
        r12[r13] = r10;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = java.lang.String.format(r3, r9, r12);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r9 = 0;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r2 = r2.queryFinalized(r3, r10);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = r2.next();	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        if (r3 != 0) goto L_0x052f;
    L_0x052d:
        r3 = 1;
        goto L_0x0530;
    L_0x052f:
        r3 = 0;
    L_0x0530:
        r2.dispose();	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        if (r3 == 0) goto L_0x0580;
    L_0x0535:
        r2 = r14.database;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r9 = "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d";
        r10 = 2;
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r13 = 0;
        r12[r13] = r10;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r10 = java.lang.Integer.valueOf(r21);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r19 = 1;
        r12[r19] = r10;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = java.lang.String.format(r3, r9, r12);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r9 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r2 = r2.queryFinalized(r3, r9);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r3 = r2.next();	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        if (r3 == 0) goto L_0x0575;
    L_0x055d:
        r3 = r2.intValue(r13);	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r9 = (long) r3;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r12 = 0;
        r21 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r21 == 0) goto L_0x0572;
    L_0x0568:
        if (r11 == 0) goto L_0x0572;
    L_0x056a:
        r12 = (long) r11;	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r18 = 32;
        r12 = r12 << r18;
        r30 = r9 | r12;
        goto L_0x0577;
    L_0x0572:
        r30 = r9;
        goto L_0x0577;
    L_0x0575:
        r3 = r21;
    L_0x0577:
        r2.dispose();	 Catch:{ Exception -> 0x0586, all -> 0x0584 }
        r2 = r43;
        r21 = r3;
        goto L_0x04f2;
    L_0x0580:
        r2 = r43;
        goto L_0x04f2;
    L_0x0584:
        r0 = move-exception;
        goto L_0x0592;
    L_0x0586:
        r0 = move-exception;
        goto L_0x05af;
    L_0x0588:
        r0 = move-exception;
        r37 = r6;
        r33 = r9;
        r6 = r10;
        r34 = r12;
        r35 = r13;
    L_0x0592:
        r7 = r44;
        r1 = r0;
        r13 = r6;
        r21 = r22;
        r20 = r33;
        r11 = r34;
        r14 = r35;
        r3 = r37;
    L_0x05a0:
        r12 = 0;
        r17 = 0;
        goto L_0x01d5;
    L_0x05a5:
        r0 = move-exception;
        r37 = r6;
        r33 = r9;
        r6 = r10;
        r34 = r12;
        r35 = r13;
    L_0x05af:
        r7 = r44;
        r1 = r0;
        r13 = r6;
        r21 = r22;
        r20 = r33;
        r11 = r34;
        r14 = r35;
        r3 = r37;
    L_0x05bd:
        r12 = 0;
        r17 = 0;
        goto L_0x01e8;
    L_0x05c2:
        r37 = r6;
        r36 = r10;
        r32 = r12;
        r29 = r13;
        r2 = r43;
        r30 = r21;
        r10 = 0;
        r22 = 0;
        r33 = 0;
        r34 = 0;
        r35 = 0;
        r21 = r44;
    L_0x05d9:
        if (r2 > r10) goto L_0x05e8;
    L_0x05db:
        if (r10 >= r8) goto L_0x05de;
    L_0x05dd:
        goto L_0x05e8;
    L_0x05de:
        r3 = r10 - r2;
        r2 = r2 + 10;
        r9 = r10;
        r6 = r21;
        r10 = r33;
        goto L_0x0602;
    L_0x05e8:
        r3 = r10 + 10;
        r2 = java.lang.Math.max(r2, r3);	 Catch:{ Exception -> 0x061b, all -> 0x0609 }
        if (r10 >= r8) goto L_0x05fc;
    L_0x05f0:
        r6 = r21;
        r30 = r22;
        r3 = 0;
        r9 = 0;
        r10 = 0;
        r21 = 0;
        r34 = 0;
        goto L_0x0642;
    L_0x05fc:
        r9 = r10;
        r6 = r21;
        r10 = r33;
        r3 = 0;
    L_0x0602:
        r40 = r30;
        r30 = r22;
        r21 = r40;
        goto L_0x0642;
    L_0x0609:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
    L_0x060e:
        r13 = r10;
        r21 = r22;
        r20 = r33;
    L_0x0613:
        r11 = r34;
    L_0x0615:
        r14 = r35;
        r3 = r37;
        goto L_0x0e32;
    L_0x061b:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
    L_0x0620:
        r13 = r10;
        r21 = r22;
        r20 = r33;
    L_0x0625:
        r11 = r34;
    L_0x0627:
        r14 = r35;
        r3 = r37;
        goto L_0x0e49;
    L_0x062d:
        r2 = r43;
        r37 = r6;
        r36 = r10;
        r32 = r12;
        r29 = r13;
        r6 = r44;
        r3 = 0;
        r9 = 0;
        r10 = 0;
        r30 = 0;
        r34 = 0;
        r35 = 0;
    L_0x0642:
        r8 = r14.database;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r13 = "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)";
        r31 = r1;
        r43 = r6;
        r6 = 1;
        r1 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r33 = r7;
        r7 = 0;
        r1[r7] = r6;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r1 = java.lang.String.format(r12, r13, r1);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r1 = r8.queryFinalized(r1, r6);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = r1.next();	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        if (r6 == 0) goto L_0x06bb;
    L_0x0668:
        r6 = r1.intValue(r7);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r7 = 1;
        if (r6 != r7) goto L_0x0671;
    L_0x066f:
        r6 = 1;
        goto L_0x0672;
    L_0x0671:
        r6 = 0;
    L_0x0672:
        r1.dispose();	 Catch:{ Exception -> 0x068e, all -> 0x0679 }
        r12 = r6;
        r1 = 3;
        goto L_0x0707;
    L_0x0679:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r17 = r6;
        r13 = r9;
        r20 = r10;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r12 = 0;
        r6 = r2;
        goto L_0x17c4;
    L_0x068e:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r17 = r6;
        r13 = r9;
        r20 = r10;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r12 = 0;
        r6 = r2;
        goto L_0x1799;
    L_0x06a3:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r20 = r10;
        r21 = r30;
        goto L_0x0613;
    L_0x06af:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r20 = r10;
        r21 = r30;
        goto L_0x0625;
    L_0x06bb:
        r1.dispose();	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r1 = r14.database;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r7 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0";
        r8 = 1;
        r12 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r13 = 0;
        r12[r13] = r8;	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = java.lang.String.format(r6, r7, r12);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r7 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r1 = r1.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r6 = r1.next();	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        if (r6 == 0) goto L_0x0702;
    L_0x06de:
        r6 = r1.intValue(r13);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        if (r6 == 0) goto L_0x0702;
    L_0x06e4:
        r7 = r14.database;	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r8 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r7 = r7.executeFast(r8);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r7.requery();	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r8 = 1;
        r7.bindLong(r8, r4);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r8 = 2;
        r12 = 0;
        r7.bindInteger(r8, r12);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r8 = 3;
        r7.bindInteger(r8, r6);	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r7.step();	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
        r7.dispose();	 Catch:{ Exception -> 0x06af, all -> 0x06a3 }
    L_0x0702:
        r1.dispose();	 Catch:{ Exception -> 0x0e37, all -> 0x0e20 }
        r1 = 3;
        r12 = 0;
    L_0x0707:
        if (r15 == r1) goto L_0x0a05;
    L_0x0709:
        r1 = 4;
        if (r15 == r1) goto L_0x0a05;
    L_0x070c:
        r1 = 2;
        if (r10 == 0) goto L_0x0713;
    L_0x070f:
        if (r15 != r1) goto L_0x0713;
    L_0x0711:
        goto L_0x0a05;
    L_0x0713:
        r6 = 1;
        if (r15 != r6) goto L_0x07ef;
    L_0x0716:
        r3 = r14.database;	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r7 = "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1";
        r8 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r1 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r13 = 0;
        r8[r13] = r1;	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r1 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r19 = 1;
        r8[r19] = r1;	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r1 = java.lang.String.format(r6, r7, r8);	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r1 = r3.queryFinalized(r1, r6);	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r3 = r1.next();	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        if (r3 == 0) goto L_0x074b;
    L_0x073d:
        r3 = r1.intValue(r13);	 Catch:{ Exception -> 0x07da, all -> 0x07c5 }
        r6 = (long) r3;
        r8 = r10;
        if (r11 == 0) goto L_0x074e;
    L_0x0745:
        r10 = (long) r11;
        r3 = 32;
        r10 = r10 << r3;
        r6 = r6 | r10;
        goto L_0x074e;
    L_0x074b:
        r8 = r10;
        r6 = 0;
    L_0x074e:
        r1.dispose();	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = 0;
        r1 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r1 == 0) goto L_0x0793;
    L_0x0757:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";
        r11 = 5;
        r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r19 = 0;
        r13[r19] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r20 = 1;
        r13[r20] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r21 = 2;
        r13[r21] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = 3;
        r13[r7] = r6;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = 4;
        r13[r7] = r6;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r3, r10, r13);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        goto L_0x08d4;
    L_0x0793:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d";
        r7 = 4;
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 0;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 1;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 2;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 3;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r3, r6, r10);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        goto L_0x08d4;
    L_0x07c5:
        r0 = move-exception;
        r8 = r10;
    L_0x07c7:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r20 = r8;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x0e06;
    L_0x07da:
        r0 = move-exception;
        r8 = r10;
    L_0x07dc:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r20 = r8;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x0e1d;
    L_0x07ef:
        r8 = r10;
        if (r50 == 0) goto L_0x08e5;
    L_0x07f2:
        r6 = 0;
        r1 = (r21 > r6 ? 1 : (r21 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x08a4;
    L_0x07f8:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
        r7 = 2;
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r13 = 0;
        r10[r13] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r19 = 1;
        r10[r19] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r3, r6, r10);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = r1.next();	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        if (r3 == 0) goto L_0x082d;
    L_0x0820:
        r3 = r1.intValue(r13);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = (long) r3;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        if (r11 == 0) goto L_0x082f;
    L_0x0827:
        r10 = (long) r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = 32;
        r10 = r10 << r3;
        r6 = r6 | r10;
        goto L_0x082f;
    L_0x082d:
        r6 = 0;
    L_0x082f:
        r1.dispose();	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = 0;
        r1 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r1 == 0) goto L_0x0873;
    L_0x0838:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d";
        r11 = 5;
        r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r19 = 0;
        r13[r19] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r20 = 1;
        r13[r20] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r21 = 2;
        r13[r21] = r11;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = 3;
        r13[r7] = r6;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = 4;
        r13[r7] = r6;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r3, r10, r13);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        goto L_0x08d4;
    L_0x0873:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d";
        r7 = 4;
        r10 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 0;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 1;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 2;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r11 = 3;
        r10[r11] = r7;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r3, r6, r10);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        goto L_0x08d4;
    L_0x08a4:
        r1 = r14.database;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r10 = 4;
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r13 = 0;
        r11[r13] = r10;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r13 = 1;
        r11[r13] = r10;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = 2;
        r11[r10] = r3;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r10 = 3;
        r11[r10] = r3;	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r3 = java.lang.String.format(r6, r7, r11);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x08e2, all -> 0x08df }
    L_0x08d4:
        r6 = r43;
        r4 = r44;
        r5 = r2;
        r2 = r30;
        r3 = 6;
        r10 = 0;
        goto L_0x0fb5;
    L_0x08df:
        r0 = move-exception;
        goto L_0x07c7;
    L_0x08e2:
        r0 = move-exception;
        goto L_0x07dc;
    L_0x08e5:
        r1 = r14.database;	 Catch:{ Exception -> 0x09f9, all -> 0x09ed }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x09f9, all -> 0x09ed }
        r7 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";
        r10 = 1;
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x09f9, all -> 0x09ed }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x09f9, all -> 0x09ed }
        r38 = r8;
        r8 = 0;
        r13[r8] = r10;	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r6 = java.lang.String.format(r6, r7, r13);	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r7 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r1 = r1.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r6 = r1.next();	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        if (r6 == 0) goto L_0x090d;
    L_0x0907:
        r6 = r1.intValue(r8);	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r10 = r6;
        goto L_0x090e;
    L_0x090d:
        r10 = 0;
    L_0x090e:
        r1.dispose();	 Catch:{ Exception -> 0x09d1, all -> 0x09b9 }
        r1 = r14.database;	 Catch:{ Exception -> 0x09d1, all -> 0x09b9 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x09d1, all -> 0x09b9 }
        r7 = "SELECT max(end) FROM messages_holes WHERE uid = %d";
        r8 = 1;
        r13 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x09d1, all -> 0x09b9 }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x09d1, all -> 0x09b9 }
        r50 = r10;
        r10 = 0;
        r13[r10] = r8;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = java.lang.String.format(r6, r7, r13);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r7 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r1 = r1.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = r1.next();	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        if (r6 == 0) goto L_0x0940;
    L_0x0933:
        r6 = r1.intValue(r10);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        if (r11 == 0) goto L_0x0942;
    L_0x093a:
        r10 = (long) r11;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r8 = 32;
        r10 = r10 << r8;
        r6 = r6 | r10;
        goto L_0x0942;
    L_0x0940:
        r6 = 0;
    L_0x0942:
        r1.dispose();	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r10 = 0;
        r1 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r1 == 0) goto L_0x097d;
    L_0x094b:
        r1 = r14.database;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r10 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r11 = 4;
        r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r11 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r19 = 0;
        r13[r19] = r11;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r7 = 1;
        r13[r7] = r6;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = 2;
        r13[r6] = r3;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = 3;
        r13[r6] = r3;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.String.format(r8, r10, r13);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        goto L_0x09a6;
    L_0x097d:
        r1 = r14.database;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r7 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d";
        r8 = 3;
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r11 = 0;
        r10[r11] = r8;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r8 = 1;
        r10[r8] = r3;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r8 = 2;
        r10[r8] = r3;	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r3 = java.lang.String.format(r6, r7, r10);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x09b7, all -> 0x09b5 }
    L_0x09a6:
        r6 = r43;
        r4 = r44;
        r39 = r50;
        r5 = r2;
        r2 = r30;
        r8 = r38;
        r3 = 6;
        r10 = 0;
        goto L_0x11c9;
    L_0x09b5:
        r0 = move-exception;
        goto L_0x09bc;
    L_0x09b7:
        r0 = move-exception;
        goto L_0x09d4;
    L_0x09b9:
        r0 = move-exception;
        r50 = r10;
    L_0x09bc:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r50;
        goto L_0x17c4;
    L_0x09d1:
        r0 = move-exception;
        r50 = r10;
    L_0x09d4:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r50;
        goto L_0x1799;
    L_0x09e9:
        r0 = move-exception;
        goto L_0x09f0;
    L_0x09eb:
        r0 = move-exception;
        goto L_0x09fc;
    L_0x09ed:
        r0 = move-exception;
        r38 = r8;
    L_0x09f0:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        goto L_0x0dfc;
    L_0x09f9:
        r0 = move-exception;
        r38 = r8;
    L_0x09fc:
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        goto L_0x0e13;
    L_0x0a05:
        r38 = r10;
        r1 = r14.database;	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r6 = "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0";
        r7 = 1;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r10 = 0;
        r8[r10] = r7;	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r3 = java.lang.String.format(r3, r6, r8);	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r6 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r1 = r1.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        r3 = r1.next();	 Catch:{ Exception -> 0x0e09, all -> 0x0df2 }
        if (r3 == 0) goto L_0x0a2d;
    L_0x0a27:
        r3 = r1.intValue(r10);	 Catch:{ Exception -> 0x09eb, all -> 0x09e9 }
        r10 = r3;
        goto L_0x0a2e;
    L_0x0a2d:
        r10 = 0;
    L_0x0a2e:
        r1.dispose();	 Catch:{ Exception -> 0x0dda, all -> 0x0dc2 }
        r1 = 4;
        if (r15 != r1) goto L_0x0b9d;
    L_0x0a34:
        if (r51 == 0) goto L_0x0b9d;
    L_0x0a36:
        r1 = r14.database;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r6 = "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0";
        r7 = 2;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r13 = 0;
        r8[r13] = r7;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r7 = java.lang.Integer.valueOf(r51);	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r19 = 1;
        r8[r19] = r7;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r3 = java.lang.String.format(r3, r6, r8);	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r1 = r1.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r3 = r1.next();	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        if (r3 == 0) goto L_0x0a8d;
    L_0x0a5e:
        r3 = r1.intValue(r13);	 Catch:{ Exception -> 0x0a78, all -> 0x0a63 }
        goto L_0x0a8e;
    L_0x0a63:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r10;
        goto L_0x17c4;
    L_0x0a78:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r10;
        goto L_0x1799;
    L_0x0a8d:
        r3 = -1;
    L_0x0a8e:
        r1.dispose();	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r1 = r14.database;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r8 = "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0";
        r13 = 2;
        r6 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r13 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0b85, all -> 0x0b6d }
        r39 = r10;
        r10 = 0;
        r6[r10] = r13;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r13 = java.lang.Integer.valueOf(r51);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r19 = 1;
        r6[r19] = r13;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r6 = java.lang.String.format(r7, r8, r6);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = r1.queryFinalized(r6, r7);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r6 = r1.next();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        if (r6 == 0) goto L_0x0ad4;
    L_0x0abb:
        r6 = r1.intValue(r10);	 Catch:{ Exception -> 0x0aca, all -> 0x0ac0 }
        goto L_0x0ad5;
    L_0x0ac0:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        goto L_0x0dce;
    L_0x0aca:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r17 = r12;
        goto L_0x0de6;
    L_0x0ad4:
        r6 = -1;
    L_0x0ad5:
        r1.dispose();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = -1;
        if (r3 == r1) goto L_0x0b9f;
    L_0x0adb:
        if (r6 == r1) goto L_0x0b9f;
    L_0x0add:
        if (r3 != r6) goto L_0x0ae3;
    L_0x0adf:
        r6 = r3;
        r1 = r12;
        goto L_0x0ba2;
    L_0x0ae3:
        r1 = r14.database;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r8 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";
        r10 = 3;
        r13 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r19 = 0;
        r13[r19] = r10;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r10 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r20 = 1;
        r13[r20] = r10;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r10 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r25 = 2;
        r13[r25] = r10;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = java.lang.String.format(r7, r8, r13);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r8 = 0;
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = r1.queryFinalized(r7, r10);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = r1.next();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        if (r7 == 0) goto L_0x0b16;
    L_0x0b15:
        r3 = -1;
    L_0x0b16:
        r1.dispose();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = -1;
        if (r3 == r1) goto L_0x0b9f;
    L_0x0b1c:
        r1 = r14.database;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d";
        r8 = 3;
        r10 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r13 = 0;
        r10[r13] = r8;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r8 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r13 = 1;
        r10[r13] = r8;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r8 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r13 = 2;
        r10[r13] = r8;	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r3 = java.lang.String.format(r3, r7, r10);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = r1.queryFinalized(r3, r8);	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r3 = r1.next();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        if (r3 == 0) goto L_0x0b4c;
    L_0x0b4b:
        r6 = -1;
    L_0x0b4c:
        r1.dispose();	 Catch:{ Exception -> 0x0b6b, all -> 0x0b69 }
        r1 = -1;
        if (r6 == r1) goto L_0x0b9f;
    L_0x0b52:
        r7 = (long) r6;
        r16 = 0;
        r1 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x0b64;
    L_0x0b59:
        if (r11 == 0) goto L_0x0b64;
    L_0x0b5b:
        r1 = r12;
        r12 = (long) r11;
        r3 = 32;
        r12 = r12 << r3;
        r21 = r7 | r12;
        r3 = r6;
        goto L_0x0ba4;
    L_0x0b64:
        r1 = r12;
        r3 = r6;
        r21 = r7;
        goto L_0x0ba4;
    L_0x0b69:
        r0 = move-exception;
        goto L_0x0b70;
    L_0x0b6b:
        r0 = move-exception;
        goto L_0x0b88;
    L_0x0b6d:
        r0 = move-exception;
        r39 = r10;
    L_0x0b70:
        r1 = r12;
        r7 = r44;
        r17 = r1;
        r6 = r2;
        r13 = r9;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r39;
        goto L_0x17c3;
    L_0x0b85:
        r0 = move-exception;
        r39 = r10;
    L_0x0b88:
        r1 = r12;
        r7 = r44;
        r17 = r1;
        r6 = r2;
        r13 = r9;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        r12 = r39;
        goto L_0x115b;
    L_0x0b9d:
        r39 = r10;
    L_0x0b9f:
        r1 = r12;
        r6 = r43;
    L_0x0ba2:
        r3 = r44;
    L_0x0ba4:
        if (r6 == 0) goto L_0x0ba8;
    L_0x0ba6:
        r10 = 1;
        goto L_0x0ba9;
    L_0x0ba8:
        r10 = 0;
    L_0x0ba9:
        if (r10 == 0) goto L_0x0bfc;
    L_0x0bab:
        r7 = r14.database;	 Catch:{ Exception -> 0x0bf2, all -> 0x0be8 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0bf2, all -> 0x0be8 }
        r12 = "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d";
        r43 = r1;
        r13 = 3;
        r1 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r13 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r19 = 0;
        r1[r19] = r13;	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r13 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r20 = 1;
        r1[r20] = r13;	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r13 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r25 = 2;
        r1[r25] = r13;	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r1 = java.lang.String.format(r8, r12, r1);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r8 = 0;
        r12 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r1 = r7.queryFinalized(r1, r12);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r7 = r1.next();	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        if (r7 == 0) goto L_0x0be0;
    L_0x0bdf:
        r10 = 0;
    L_0x0be0:
        r1.dispose();	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        goto L_0x0bfe;
    L_0x0be4:
        r0 = move-exception;
        goto L_0x0beb;
    L_0x0be6:
        r0 = move-exception;
        goto L_0x0bf5;
    L_0x0be8:
        r0 = move-exception;
        r43 = r1;
    L_0x0beb:
        r17 = r43;
        r1 = r0;
        r6 = r2;
        r7 = r3;
        goto L_0x0dcd;
    L_0x0bf2:
        r0 = move-exception;
        r43 = r1;
    L_0x0bf5:
        r17 = r43;
        r1 = r0;
        r6 = r2;
        r7 = r3;
        goto L_0x0de5;
    L_0x0bfc:
        r43 = r1;
    L_0x0bfe:
        if (r10 == 0) goto L_0x0d33;
    L_0x0CLASSNAME:
        r1 = r14.database;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r12 = "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1";
        r13 = 2;
        r7 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r13 = 0;
        r7[r13] = r8;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r8 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r19 = 1;
        r7[r19] = r8;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r7 = java.lang.String.format(r10, r12, r7);	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r8 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r1 = r1.queryFinalized(r7, r8);	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r7 = r1.next();	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        if (r7 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r7 = r1.intValue(r13);	 Catch:{ Exception -> 0x0be6, all -> 0x0be4 }
        r7 = (long) r7;
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0c2f:
        r12 = (long) r11;
        r10 = 32;
        r12 = r12 << r10;
        r7 = r7 | r12;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r7 = 0;
    L_0x0CLASSNAME:
        r1.dispose();	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r1 = r14.database;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d2e, all -> 0x0d29 }
        r12 = "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1";
        r44 = r3;
        r13 = 2;
        r3 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r13 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 0;
        r3[r4] = r13;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r13 = 1;
        r3[r13] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.String.format(r10, r12, r3);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = r1.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = r1.next();	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = r1.intValue(r4);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = (long) r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        if (r11 == 0) goto L_0x0CLASSNAME;
    L_0x0c6a:
        r12 = (long) r11;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = 32;
        r12 = r12 << r5;
        r3 = r3 | r12;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = 1;
    L_0x0CLASSNAME:
        r1.dispose();	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r12 = 0;
        r1 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1));
        if (r1 != 0) goto L_0x0cc5;
    L_0x0c7b:
        r12 = 1;
        r1 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1));
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        goto L_0x0cc5;
    L_0x0CLASSNAME:
        r1 = r14.database;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r5 = 6;
        r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 0;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 1;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = r2 / 2;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 2;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 3;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 4;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = r2 / 2;
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 5;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.String.format(r3, r4, r7);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 0;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = r1.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        goto L_0x0d26;
    L_0x0cc5:
        r12 = 0;
        r1 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1));
        if (r1 != 0) goto L_0x0cd5;
    L_0x0ccb:
        r7 = NUM; // 0x3b9aca00 float:0.NUM double:4.94065646E-315;
        if (r11 == 0) goto L_0x0cd5;
    L_0x0cd0:
        r10 = (long) r11;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = 32;
        r10 = r10 << r1;
        r7 = r7 | r10;
    L_0x0cd5:
        r1 = r14.database;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND (m.mid <= %d OR m.mid < 0) ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r11 = 8;
        r11 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r12 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r13 = 0;
        r11[r13] = r12;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r12 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r13 = 1;
        r11[r13] = r12;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.Long.valueOf(r3);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 2;
        r11[r4] = r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = r2 / 2;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 3;
        r11[r4] = r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 4;
        r11[r4] = r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 5;
        r11[r4] = r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 6;
        r11[r4] = r3;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = 7;
        r4 = r2 / 2;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r11[r3] = r4;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.String.format(r5, r10, r11);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 0;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = r1.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
    L_0x0d26:
        r5 = 6;
        goto L_0x0db5;
    L_0x0d29:
        r0 = move-exception;
        r44 = r3;
        goto L_0x0dc7;
    L_0x0d2e:
        r0 = move-exception;
        r44 = r3;
        goto L_0x0ddf;
    L_0x0d33:
        r44 = r3;
        r1 = 2;
        if (r15 != r1) goto L_0x0db3;
    L_0x0d38:
        r1 = r14.database;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)";
        r5 = 1;
        r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r5 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = 0;
        r7[r8] = r5;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.String.format(r3, r4, r7);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = r1.queryFinalized(r3, r4);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = r1.next();	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        if (r3 == 0) goto L_0x0d5d;
    L_0x0d58:
        r10 = r1.intValue(r8);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        goto L_0x0d5e;
    L_0x0d5d:
        r10 = 0;
    L_0x0d5e:
        r1.dispose();	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        if (r10 != r9) goto L_0x0da8;
    L_0x0d63:
        r1 = r14.database;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.util.Locale.US;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)";
        r5 = 6;
        r7 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 0;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 1;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = r2 / 2;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 2;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 3;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 4;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r8 = r2 / 2;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r10 = 5;
        r7[r10] = r8;	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = java.lang.String.format(r3, r4, r7);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r4 = 0;
        r7 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r1 = r1.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x0db1, all -> 0x0daf }
        r3 = r1;
        r1 = 1;
        goto L_0x0dac;
    L_0x0da8:
        r5 = 6;
        r1 = 0;
        r3 = r1;
        r1 = 0;
    L_0x0dac:
        r10 = r1;
        r1 = r3;
        goto L_0x0db6;
    L_0x0daf:
        r0 = move-exception;
        goto L_0x0dc7;
    L_0x0db1:
        r0 = move-exception;
        goto L_0x0ddf;
    L_0x0db3:
        r5 = 6;
        r1 = 0;
    L_0x0db5:
        r10 = 0;
    L_0x0db6:
        r12 = r43;
        r4 = r44;
        r5 = r2;
        r2 = r30;
        r8 = r38;
        r3 = 6;
        goto L_0x11c9;
    L_0x0dc2:
        r0 = move-exception;
        r39 = r10;
        r43 = r12;
    L_0x0dc7:
        r17 = r43;
        r7 = r44;
        r1 = r0;
        r6 = r2;
    L_0x0dcd:
        r13 = r9;
    L_0x0dce:
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        goto L_0x1484;
    L_0x0dda:
        r0 = move-exception;
        r39 = r10;
        r43 = r12;
    L_0x0ddf:
        r17 = r43;
        r7 = r44;
        r1 = r0;
        r6 = r2;
    L_0x0de5:
        r13 = r9;
    L_0x0de6:
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
        goto L_0x14a4;
    L_0x0df2:
        r0 = move-exception;
        r43 = r12;
        r17 = r43;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
    L_0x0dfc:
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
    L_0x0e06:
        r12 = 0;
        goto L_0x17c4;
    L_0x0e09:
        r0 = move-exception;
        r43 = r12;
        r17 = r43;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
    L_0x0e13:
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
    L_0x0e1d:
        r12 = 0;
        goto L_0x1799;
    L_0x0e20:
        r0 = move-exception;
        r38 = r10;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
    L_0x0e32:
        r12 = 0;
        r17 = 0;
        goto L_0x17c4;
    L_0x0e37:
        r0 = move-exception;
        r38 = r10;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r21 = r30;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r20 = r38;
    L_0x0e49:
        r12 = 0;
        r17 = 0;
        goto L_0x1799;
    L_0x0e4e:
        r14 = r42;
        r15 = r49;
        r31 = r1;
        r28 = r2;
        r37 = r6;
        r33 = r7;
        r36 = r10;
        r32 = r12;
        r29 = r13;
        r5 = 6;
        r2 = r43;
        r1 = "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0";
        r3 = 3;
        if (r15 != r3) goto L_0x0var_;
    L_0x0e68:
        if (r50 != 0) goto L_0x0var_;
    L_0x0e6a:
        r3 = r14.database;	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r9 = 0;
        r7[r9] = r6;	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r4 = java.lang.String.format(r4, r1, r7);	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r6 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        r4 = r3.next();	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        if (r4 == 0) goto L_0x0e8d;
    L_0x0e88:
        r10 = r3.intValue(r9);	 Catch:{ Exception -> 0x0var_, all -> 0x0f5a }
        goto L_0x0e8e;
    L_0x0e8d:
        r10 = 0;
    L_0x0e8e:
        r3.dispose();	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r3 = r14.database;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r6 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";
        r7 = 1;
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r11 = 0;
        r9[r11] = r7;	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r4 = java.lang.String.format(r4, r6, r9);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r6 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r3 = r3.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r4 = r3.next();	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        if (r4 == 0) goto L_0x0ebb;
    L_0x0eb1:
        r4 = r3.intValue(r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        r6 = 1;
        r7 = r3.intValue(r6);	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        goto L_0x0ebd;
    L_0x0ebb:
        r4 = 0;
        r7 = 0;
    L_0x0ebd:
        r3.dispose();	 Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        if (r4 == 0) goto L_0x0f1a;
    L_0x0ec2:
        r3 = r14.database;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r6 = java.util.Locale.US;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r9 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";
        r10 = 2;
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r10 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r12 = 0;
        r11[r12] = r10;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r10 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r13 = 1;
        r11[r13] = r10;	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r6 = java.lang.String.format(r6, r9, r11);	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r9 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r3 = r3.queryFinalized(r6, r9);	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        r6 = r3.next();	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        if (r6 == 0) goto L_0x0eee;
    L_0x0ee9:
        r10 = r3.intValue(r12);	 Catch:{ Exception -> 0x0var_, all -> 0x0f0e }
        goto L_0x0eef;
    L_0x0eee:
        r10 = 0;
    L_0x0eef:
        r3.dispose();	 Catch:{ Exception -> 0x0var_, all -> 0x0efa }
        r34 = r4;
        r35 = r7;
        r9 = r10;
        r3 = 3;
        goto L_0x0f7a;
    L_0x0efa:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r11 = r4;
        r14 = r7;
        r13 = r10;
        r3 = r37;
        r12 = 0;
        goto L_0x0f2b;
    L_0x0var_:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r11 = r4;
        r14 = r7;
        r13 = r10;
        r3 = r37;
        r12 = 0;
        goto L_0x0f3e;
    L_0x0f0e:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r11 = r4;
        r14 = r7;
        goto L_0x0var_;
    L_0x0var_:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r11 = r4;
        r14 = r7;
        goto L_0x0f3a;
    L_0x0f1a:
        r35 = r7;
        r34 = r10;
        r3 = 3;
        r9 = 0;
        goto L_0x0f7a;
    L_0x0var_:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r14 = r7;
        r11 = r10;
    L_0x0var_:
        r3 = r37;
        r12 = 0;
        r13 = 0;
    L_0x0f2b:
        r17 = 1;
        r20 = 0;
        r21 = 0;
        r7 = r44;
        goto L_0x17c4;
    L_0x0var_:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r14 = r7;
        r11 = r10;
    L_0x0f3a:
        r3 = r37;
        r12 = 0;
        r13 = 0;
    L_0x0f3e:
        r17 = 1;
        r20 = 0;
        r21 = 0;
        r7 = r44;
        goto L_0x1799;
    L_0x0var_:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r11 = r10;
        r3 = r37;
        goto L_0x0var_;
    L_0x0var_:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r11 = r10;
        r3 = r37;
        goto L_0x0f6f;
    L_0x0f5a:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r3 = r37;
        r11 = 0;
    L_0x0var_:
        r12 = 0;
        r13 = 0;
        r14 = 0;
        goto L_0x1762;
    L_0x0var_:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r3 = r37;
        r11 = 0;
    L_0x0f6f:
        r12 = 0;
        r13 = 0;
        r14 = 0;
        goto L_0x1773;
    L_0x0var_:
        r3 = 3;
        r9 = 0;
        r34 = 0;
        r35 = 0;
    L_0x0f7a:
        if (r15 == r3) goto L_0x115e;
    L_0x0f7c:
        r4 = 4;
        if (r15 != r4) goto L_0x0var_;
    L_0x0f7f:
        goto L_0x115e;
    L_0x0var_:
        r4 = 1;
        if (r15 != r4) goto L_0x0fd5;
    L_0x0var_:
        r1 = r14.database;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d";
        r7 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r3 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 0;
        r7[r8] = r3;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r3 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 1;
        r7[r8] = r3;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r3 = java.lang.Integer.valueOf(r43);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 2;
        r7[r8] = r3;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r3 = java.lang.String.format(r4, r6, r7);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = 0;
        r6 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r1 = r1.queryFinalized(r3, r6);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = r44;
        r6 = r4;
        r5 = r2;
        r2 = 0;
        r3 = 6;
    L_0x0fb2:
        r8 = 0;
        r10 = 0;
        r12 = 1;
    L_0x0fb5:
        r39 = 0;
        goto L_0x11c9;
    L_0x0fb9:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x1761;
    L_0x0fc7:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x1772;
    L_0x0fd5:
        if (r50 == 0) goto L_0x1039;
    L_0x0fd7:
        r3 = 6;
        if (r44 == 0) goto L_0x1009;
    L_0x0fda:
        r1 = r14.database;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d";
        r6 = 3;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 0;
        r7[r8] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Integer.valueOf(r44);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 1;
        r7[r8] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Integer.valueOf(r43);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 2;
        r7[r8] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r1 = r1.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
    L_0x1003:
        r4 = r44;
        r6 = r4;
        r5 = r2;
        r2 = 0;
        goto L_0x0fb2;
    L_0x1009:
        r1 = r14.database;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d";
        r6 = 4;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r8 = 0;
        r7[r8] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Integer.valueOf(r50);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r10 = 1;
        r7[r10] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r10 = 2;
        r7[r10] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Integer.valueOf(r43);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r10 = 3;
        r7[r10] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r1 = r1.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        goto L_0x1003;
    L_0x1039:
        r3 = 6;
        r4 = 2;
        if (r15 != r4) goto L_0x10c4;
    L_0x103d:
        r4 = r14.database;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r10 = 0;
        r7[r10] = r6;	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r1 = java.lang.String.format(r5, r1, r7);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r5 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r1 = r4.queryFinalized(r1, r5);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r4 = r1.next();	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        if (r4 == 0) goto L_0x1061;
    L_0x105b:
        r4 = r1.intValue(r10);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        r10 = r4;
        goto L_0x1062;
    L_0x1061:
        r10 = 0;
    L_0x1062:
        r1.dispose();	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r1 = r14.database;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0";
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r11 = 0;
        r7[r11] = r6;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r1 = r1.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = r1.next();	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        if (r4 == 0) goto L_0x108e;
    L_0x1085:
        r34 = r1.intValue(r11);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = 1;
        r35 = r1.intValue(r4);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
    L_0x108e:
        r1.dispose();	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        if (r34 == 0) goto L_0x10c1;
    L_0x1093:
        r1 = r14.database;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)";
        r6 = 2;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r11 = 0;
        r7[r11] = r6;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r6 = java.lang.Integer.valueOf(r34);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r12 = 1;
        r7[r12] = r6;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.lang.String.format(r4, r5, r7);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r1 = r1.queryFinalized(r4, r5);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = r1.next();	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        if (r4 == 0) goto L_0x10be;
    L_0x10ba:
        r9 = r1.intValue(r11);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
    L_0x10be:
        r1.dispose();	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
    L_0x10c1:
        r1 = r10;
        r10 = r9;
        goto L_0x10c6;
    L_0x10c4:
        r10 = r9;
        r1 = 0;
    L_0x10c6:
        if (r2 > r10) goto L_0x10d1;
    L_0x10c8:
        if (r10 >= r8) goto L_0x10cb;
    L_0x10ca:
        goto L_0x10d1;
    L_0x10cb:
        r4 = r10 - r2;
        r2 = r2 + 10;
        r9 = r10;
        goto L_0x10e1;
    L_0x10d1:
        r4 = r10 + 10;
        r2 = java.lang.Math.max(r2, r4);	 Catch:{ Exception -> 0x1149, all -> 0x1135 }
        if (r10 >= r8) goto L_0x10df;
    L_0x10d9:
        r4 = 0;
        r9 = 0;
        r10 = 0;
        r34 = 0;
        goto L_0x10e2;
    L_0x10df:
        r9 = r10;
        r4 = 0;
    L_0x10e1:
        r10 = r1;
    L_0x10e2:
        r1 = r14.database;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r6 = "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d";
        r7 = 3;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r11 = 0;
        r8[r11] = r7;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r7 = 1;
        r8[r7] = r4;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r7 = 2;
        r8[r7] = r4;	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r4 = java.lang.String.format(r5, r6, r8);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
        r1 = r1.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x1126, all -> 0x1117 }
    L_0x110b:
        r4 = r44;
        r6 = r4;
        r5 = r2;
        r39 = r10;
        r2 = 0;
        r8 = 0;
        r10 = 0;
        r12 = 1;
        goto L_0x11c9;
    L_0x1117:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r12 = r10;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x1762;
    L_0x1126:
        r0 = move-exception;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r12 = r10;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x1773;
    L_0x1135:
        r0 = move-exception;
        r7 = r44;
        r12 = r1;
        r6 = r2;
        r13 = r10;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r17 = 1;
        r20 = 0;
        r21 = 0;
        goto L_0x17c3;
    L_0x1149:
        r0 = move-exception;
        r7 = r44;
        r12 = r1;
        r6 = r2;
        r13 = r10;
        r11 = r34;
        r14 = r35;
        r3 = r37;
        r17 = 1;
        r20 = 0;
        r21 = 0;
    L_0x115b:
        r1 = r0;
        goto L_0x1799;
    L_0x115e:
        r3 = 6;
        r4 = r14.database;	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r6 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r8 = 0;
        r7[r8] = r6;	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r1 = java.lang.String.format(r5, r1, r7);	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r5 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r1 = r4.queryFinalized(r1, r5);	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        r4 = r1.next();	 Catch:{ Exception -> 0x1765, all -> 0x1754 }
        if (r4 == 0) goto L_0x1182;
    L_0x117d:
        r10 = r1.intValue(r8);	 Catch:{ Exception -> 0x0fc7, all -> 0x0fb9 }
        goto L_0x1183;
    L_0x1182:
        r10 = 0;
    L_0x1183:
        r1.dispose();	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r1 = r14.database;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r5 = "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)";
        r6 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 0;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 1;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = r2 / 2;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 2;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 3;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = java.lang.Long.valueOf(r21);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 4;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r7 = r2 / 2;
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r8 = 5;
        r6[r8] = r7;	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r4 = java.lang.String.format(r4, r5, r6);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r5 = 0;
        r6 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        r1 = r1.queryFinalized(r4, r6);	 Catch:{ Exception -> 0x1745, all -> 0x1736 }
        goto L_0x110b;
    L_0x11c9:
        r7 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r1 == 0) goto L_0x14a8;
    L_0x11ce:
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x11d0:
        r13 = r1.next();	 Catch:{ Exception -> 0x1488, all -> 0x1468 }
        if (r13 == 0) goto L_0x144d;
    L_0x11d6:
        r13 = 1;
        r3 = r1.byteBufferValue(r13);	 Catch:{ Exception -> 0x1488, all -> 0x1468 }
        if (r3 == 0) goto L_0x141c;
    L_0x11dd:
        r43 = r4;
        r13 = 0;
        r4 = r3.readInt32(r13);	 Catch:{ Exception -> 0x1419, all -> 0x1417 }
        r4 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r3, r4, r13);	 Catch:{ Exception -> 0x1419, all -> 0x1417 }
        r44 = r5;
        r13 = 2;
        r5 = r1.intValue(r13);	 Catch:{ Exception -> 0x1414, all -> 0x1411 }
        r4.send_state = r5;	 Catch:{ Exception -> 0x1414, all -> 0x1411 }
        r5 = 3;
        r13 = r1.intValue(r5);	 Catch:{ Exception -> 0x1414, all -> 0x1411 }
        r4.id = r13;	 Catch:{ Exception -> 0x1414, all -> 0x1411 }
        r5 = r4.id;	 Catch:{ Exception -> 0x1414, all -> 0x1411 }
        if (r5 <= 0) goto L_0x1233;
    L_0x11fc:
        r5 = r4.send_state;	 Catch:{ Exception -> 0x121e, all -> 0x1209 }
        if (r5 == 0) goto L_0x1233;
    L_0x1200:
        r5 = r4.send_state;	 Catch:{ Exception -> 0x121e, all -> 0x1209 }
        r13 = 3;
        if (r5 == r13) goto L_0x1233;
    L_0x1205:
        r5 = 0;
        r4.send_state = r5;	 Catch:{ Exception -> 0x121e, all -> 0x1209 }
        goto L_0x1233;
    L_0x1209:
        r0 = move-exception;
        r7 = r43;
        r6 = r44;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r9;
        r17 = r12;
    L_0x1216:
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x1484;
    L_0x121e:
        r0 = move-exception;
        r7 = r43;
        r6 = r44;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r9;
        r17 = r12;
    L_0x122b:
        r11 = r34;
        r14 = r35;
        r3 = r37;
        goto L_0x14a4;
    L_0x1233:
        r50 = r12;
        r5 = r33;
        r12 = (long) r5;
        r22 = r9;
        r21 = r10;
        r9 = r46;
        r30 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
        if (r30 != 0) goto L_0x1264;
    L_0x1242:
        r12 = 1;
        r4.out = r12;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        goto L_0x1264;
    L_0x1246:
        r0 = move-exception;
        r7 = r43;
        r6 = r44;
        r17 = r50;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r22;
        goto L_0x1216;
    L_0x1255:
        r0 = move-exception;
        r7 = r43;
        r6 = r44;
        r17 = r50;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r22;
        goto L_0x122b;
    L_0x1264:
        r4.readAttachPath(r3, r5);	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r3.reuse();	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r3 = 0;
        r12 = r1.intValue(r3);	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        org.telegram.messenger.MessageObject.setUnreadFlags(r4, r12);	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r3 = r4.id;	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        if (r3 <= 0) goto L_0x1282;
    L_0x1276:
        r3 = r4.id;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        r7 = java.lang.Math.min(r3, r7);	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        r3 = r4.id;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        r11 = java.lang.Math.max(r3, r11);	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
    L_0x1282:
        r3 = 4;
        r12 = r1.intValue(r3);	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r4.date = r12;	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r4.dialog_id = r9;	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r3 = r4.flags;	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        r3 = r3 & 1024;
        if (r3 == 0) goto L_0x1298;
    L_0x1291:
        r3 = 7;
        r3 = r1.intValue(r3);	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        r4.views = r3;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
    L_0x1298:
        if (r31 == 0) goto L_0x12a6;
    L_0x129a:
        r3 = r4.ttl;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        if (r3 != 0) goto L_0x12a6;
    L_0x129e:
        r3 = 8;
        r3 = r1.intValue(r3);	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
        r4.ttl = r3;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
    L_0x12a6:
        r3 = 9;
        r3 = r1.intValue(r3);	 Catch:{ Exception -> 0x140e, all -> 0x140b }
        if (r3 == 0) goto L_0x12b1;
    L_0x12ae:
        r3 = 1;
        r4.mentioned = r3;	 Catch:{ Exception -> 0x1255, all -> 0x1246 }
    L_0x12b1:
        r3 = r37;
        r12 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r12.add(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r12 = r27;
        r13 = r36;
        addUsersAndChatsFromMessage(r4, r12, r13);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r27 = r7;
        r7 = r4.reply_to_msg_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x12da;
    L_0x12c5:
        r9 = r4.reply_to_random_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r16 = 0;
        r7 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));
        if (r7 == 0) goto L_0x12ce;
    L_0x12cd:
        goto L_0x12da;
    L_0x12ce:
        r30 = r11;
        r33 = r12;
        r7 = r28;
        r9 = r29;
        r18 = 32;
        goto L_0x13a5;
    L_0x12da:
        r9 = 6;
        r7 = r1.isNull(r9);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x1311;
    L_0x12e1:
        r7 = r1.byteBufferValue(r9);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 == 0) goto L_0x1311;
    L_0x12e7:
        r10 = 0;
        r9 = r7.readInt32(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r7, r9, r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r4.replyMessage = r9;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = r4.replyMessage;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9.readAttachPath(r7, r5);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7.reuse();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r4.replyMessage;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 == 0) goto L_0x1311;
    L_0x12fe:
        r7 = org.telegram.messenger.MessageObject.isMegagroup(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 == 0) goto L_0x130c;
    L_0x1304:
        r7 = r4.replyMessage;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = r7.flags;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = r9 | r23;
        r7.flags = r9;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x130c:
        r7 = r4.replyMessage;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        addUsersAndChatsFromMessage(r7, r12, r13);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x1311:
        r7 = r4.replyMessage;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x12ce;
    L_0x1315:
        r7 = r4.reply_to_msg_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 == 0) goto L_0x1368;
    L_0x1319:
        r7 = r4.reply_to_msg_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = (long) r7;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r4.to_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 == 0) goto L_0x1331;
    L_0x1322:
        r7 = r4.to_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r7.channel_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r30 = r11;
        r33 = r12;
        r11 = (long) r7;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r18 = 32;
        r11 = r11 << r18;
        r9 = r9 | r11;
        goto L_0x1337;
    L_0x1331:
        r30 = r11;
        r33 = r12;
        r18 = 32;
    L_0x1337:
        r7 = java.lang.Long.valueOf(r9);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r12 = r32;
        r7 = r12.contains(r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x134a;
    L_0x1343:
        r7 = java.lang.Long.valueOf(r9);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r12.add(r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x134a:
        r7 = r4.reply_to_msg_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9 = r29;
        r7 = r9.get(r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = (java.util.ArrayList) r7;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x1360;
    L_0x1356:
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7.<init>();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r10 = r4.reply_to_msg_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r9.put(r10, r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x1360:
        r7.add(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r32 = r12;
        r7 = r28;
        goto L_0x13a5;
    L_0x1368:
        r30 = r11;
        r33 = r12;
        r9 = r29;
        r12 = r32;
        r18 = 32;
        r10 = r4.reply_to_random_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r12.contains(r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r7 != 0) goto L_0x1387;
    L_0x137e:
        r10 = r4.reply_to_random_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r12.add(r7);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x1387:
        r10 = r4.reply_to_random_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = r28;
        r10 = r7.get(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r10 = (java.util.ArrayList) r10;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r10 != 0) goto L_0x13a0;
    L_0x1393:
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r10.<init>();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r32 = r12;
        r11 = r4.reply_to_random_id;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7.put(r11, r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        goto L_0x13a2;
    L_0x13a0:
        r32 = r12;
    L_0x13a2:
        r10.add(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x13a5:
        if (r31 != 0) goto L_0x13b5;
    L_0x13a7:
        r10 = 5;
        r11 = r1.isNull(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r11 != 0) goto L_0x13b6;
    L_0x13ae:
        r11 = r1.longValue(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r4.random_id = r11;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        goto L_0x13b6;
    L_0x13b5:
        r10 = 5;
    L_0x13b6:
        r11 = org.telegram.messenger.MessageObject.isSecretMedia(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r11 == 0) goto L_0x13fe;
    L_0x13bc:
        r11 = r14.database;	 Catch:{ Exception -> 0x13f2, all -> 0x1730 }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x13f2, all -> 0x1730 }
        r10 = "SELECT date FROM enc_tasks_v2 WHERE mid = %d";
        r29 = r5;
        r28 = r7;
        r7 = 1;
        r5 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x13f0, all -> 0x1730 }
        r7 = r4.id;	 Catch:{ Exception -> 0x13f0, all -> 0x1730 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x13f0, all -> 0x1730 }
        r36 = r9;
        r9 = 0;
        r5[r9] = r7;	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        r5 = java.lang.String.format(r12, r10, r5);	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        r7 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        r5 = r11.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        r7 = r5.next();	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        if (r7 == 0) goto L_0x13ea;
    L_0x13e4:
        r7 = r5.intValue(r9);	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        r4.destroyTime = r7;	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
    L_0x13ea:
        r5.dispose();	 Catch:{ Exception -> 0x13ee, all -> 0x1730 }
        goto L_0x1404;
    L_0x13ee:
        r0 = move-exception;
        goto L_0x13f9;
    L_0x13f0:
        r0 = move-exception;
        goto L_0x13f7;
    L_0x13f2:
        r0 = move-exception;
        r29 = r5;
        r28 = r7;
    L_0x13f7:
        r36 = r9;
    L_0x13f9:
        r4 = r0;
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        goto L_0x1404;
    L_0x13fe:
        r29 = r5;
        r28 = r7;
        r36 = r9;
    L_0x1404:
        r7 = r27;
        r4 = r28;
        r11 = r30;
        goto L_0x1434;
    L_0x140b:
        r0 = move-exception;
        goto L_0x1471;
    L_0x140e:
        r0 = move-exception;
        goto L_0x1491;
    L_0x1411:
        r0 = move-exception;
        goto L_0x146d;
    L_0x1414:
        r0 = move-exception;
        goto L_0x148d;
    L_0x1417:
        r0 = move-exception;
        goto L_0x146b;
    L_0x1419:
        r0 = move-exception;
        goto L_0x148b;
    L_0x141c:
        r43 = r4;
        r44 = r5;
        r22 = r9;
        r21 = r10;
        r50 = r12;
        r4 = r28;
        r13 = r36;
        r3 = r37;
        r18 = 32;
        r36 = r29;
        r29 = r33;
        r33 = r27;
    L_0x1434:
        r5 = r44;
        r12 = r50;
        r37 = r3;
        r28 = r4;
        r10 = r21;
        r9 = r22;
        r27 = r33;
        r3 = 6;
        r4 = r43;
        r33 = r29;
        r29 = r36;
        r36 = r13;
        goto L_0x11d0;
    L_0x144d:
        r43 = r4;
        r44 = r5;
        r22 = r9;
        r21 = r10;
        r50 = r12;
        r4 = r28;
        r13 = r36;
        r3 = r37;
        r36 = r29;
        r29 = r33;
        r33 = r27;
        r1.dispose();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        goto L_0x14c0;
    L_0x1468:
        r0 = move-exception;
        r43 = r4;
    L_0x146b:
        r44 = r5;
    L_0x146d:
        r22 = r9;
        r50 = r12;
    L_0x1471:
        r3 = r37;
    L_0x1473:
        r7 = r43;
        r6 = r44;
        r17 = r50;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r22;
        r11 = r34;
        r14 = r35;
    L_0x1484:
        r12 = r39;
        goto L_0x17c4;
    L_0x1488:
        r0 = move-exception;
        r43 = r4;
    L_0x148b:
        r44 = r5;
    L_0x148d:
        r22 = r9;
        r50 = r12;
    L_0x1491:
        r3 = r37;
    L_0x1493:
        r7 = r43;
        r6 = r44;
        r17 = r50;
        r1 = r0;
        r21 = r2;
        r20 = r8;
        r13 = r22;
        r11 = r34;
        r14 = r35;
    L_0x14a4:
        r12 = r39;
        goto L_0x1799;
    L_0x14a8:
        r43 = r4;
        r44 = r5;
        r22 = r9;
        r21 = r10;
        r50 = r12;
        r4 = r28;
        r13 = r36;
        r3 = r37;
        r36 = r29;
        r29 = r33;
        r33 = r27;
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x14c0:
        r1 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r5 = org.telegram.messenger.-$$Lambda$MessagesStorage$w-CS_xp2CSQ-MvRi_vpbZTsK6rk.INSTANCE;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        java.util.Collections.sort(r1, r5);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r31 == 0) goto L_0x1507;
    L_0x14c9:
        r1 = 3;
        if (r15 == r1) goto L_0x14d9;
    L_0x14cc:
        r1 = 4;
        if (r15 == r1) goto L_0x14d9;
    L_0x14cf:
        r1 = 2;
        if (r15 != r1) goto L_0x14d7;
    L_0x14d2:
        if (r8 == 0) goto L_0x14d7;
    L_0x14d4:
        if (r21 != 0) goto L_0x14d7;
    L_0x14d6:
        goto L_0x14d9;
    L_0x14d7:
        r1 = 4;
        goto L_0x14f4;
    L_0x14d9:
        r1 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r1 != 0) goto L_0x14d7;
    L_0x14e1:
        if (r7 > r6) goto L_0x14e5;
    L_0x14e3:
        if (r11 >= r6) goto L_0x14d7;
    L_0x14e5:
        r32.clear();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r33.clear();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r13.clear();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1.clear();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        goto L_0x14d7;
    L_0x14f4:
        if (r15 == r1) goto L_0x14f9;
    L_0x14f6:
        r1 = 3;
        if (r15 != r1) goto L_0x1507;
    L_0x14f9:
        r1 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1 = r1.size();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r5 = 1;
        if (r1 != r5) goto L_0x1507;
    L_0x1502:
        r1 = r3.messages;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1.clear();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x1507:
        if (r2 == 0) goto L_0x1534;
    L_0x1509:
        r1 = r14.database;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r6 = "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)";
        r7 = 1;
        r9 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r7 = java.lang.Long.valueOf(r46);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r10 = 0;
        r9[r10] = r7;	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r5 = java.lang.String.format(r5, r6, r9);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r6 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r1 = r1.queryFinalized(r5, r6);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        r5 = r1.next();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r5 == 0) goto L_0x152f;
    L_0x1529:
        r5 = r1.intValue(r10);	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
        if (r2 == r5) goto L_0x1531;
    L_0x152f:
        r2 = r2 * -1;
    L_0x1531:
        r1.dispose();	 Catch:{ Exception -> 0x1733, all -> 0x1730 }
    L_0x1534:
        r7 = r43;
        r6 = r44;
        r18 = r50;
        r21 = r2;
        r1 = r8;
        r11 = r34;
        r12 = r39;
    L_0x1541:
        r2 = r32.isEmpty();	 Catch:{ Exception -> 0x171e, all -> 0x170c }
        r5 = ",";
        if (r2 != 0) goto L_0x16b5;
    L_0x1549:
        r2 = r36.size();	 Catch:{ Exception -> 0x171e, all -> 0x170c }
        if (r2 <= 0) goto L_0x157f;
    L_0x154f:
        r2 = r14.database;	 Catch:{ Exception -> 0x157a, all -> 0x1575 }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x157a, all -> 0x1575 }
        r9 = "SELECT data, mid, date FROM messages WHERE mid IN(%s)";
        r10 = 1;
        r15 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x157a, all -> 0x1575 }
        r10 = r32;
        r10 = android.text.TextUtils.join(r5, r10);	 Catch:{ Exception -> 0x157a, all -> 0x1575 }
        r43 = r1;
        r1 = 0;
        r15[r1] = r10;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r8 = java.lang.String.format(r8, r9, r15);	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r9 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r1 = r2.queryFinalized(r8, r9);	 Catch:{ Exception -> 0x1572, all -> 0x156f }
    L_0x156d:
        r10 = 0;
        goto L_0x159d;
    L_0x156f:
        r0 = move-exception;
        goto L_0x1713;
    L_0x1572:
        r0 = move-exception;
        goto L_0x1725;
    L_0x1575:
        r0 = move-exception;
        r43 = r1;
        goto L_0x1713;
    L_0x157a:
        r0 = move-exception;
        r43 = r1;
        goto L_0x1725;
    L_0x157f:
        r43 = r1;
        r10 = r32;
        r1 = r14.database;	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r8 = "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)";
        r9 = 1;
        r15 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r9 = android.text.TextUtils.join(r5, r10);	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r10 = 0;
        r15[r10] = r9;	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r2 = java.lang.String.format(r2, r8, r15);	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        r1 = r1.queryFinalized(r2, r8);	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
    L_0x159d:
        r2 = r1.next();	 Catch:{ Exception -> 0x16b2, all -> 0x16af }
        if (r2 == 0) goto L_0x165f;
    L_0x15a3:
        r2 = r1.byteBufferValue(r10);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        if (r2 == 0) goto L_0x164b;
    L_0x15a9:
        r8 = r2.readInt32(r10);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r2, r8, r10);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r9 = r29;
        r8.readAttachPath(r2, r9);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r2.reuse();	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r15 = 1;
        r2 = r1.intValue(r15);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r8.id = r2;	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r2 = 2;
        r10 = r1.intValue(r2);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r8.date = r10;	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r29 = r9;
        r9 = r46;
        r8.dialog_id = r9;	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r2 = r33;
        addUsersAndChatsFromMessage(r8, r2, r13);	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        r20 = r36.size();	 Catch:{ Exception -> 0x165a, all -> 0x1655 }
        if (r20 <= 0) goto L_0x1610;
    L_0x15d8:
        r15 = r8.id;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r9 = r36;
        r10 = r9.get(r15);	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r10 = (java.util.ArrayList) r10;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r36 = r9;
        if (r10 == 0) goto L_0x160d;
    L_0x15e6:
        r15 = 0;
    L_0x15e7:
        r9 = r10.size();	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        if (r15 >= r9) goto L_0x160d;
    L_0x15ed:
        r9 = r10.get(r15);	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r9 = (org.telegram.tgnet.TLRPC.Message) r9;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r9.replyMessage = r8;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r24 = org.telegram.messenger.MessageObject.isMegagroup(r9);	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        if (r24 == 0) goto L_0x1606;
    L_0x15fb:
        r9 = r9.replyMessage;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r44 = r10;
        r10 = r9.flags;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        r10 = r10 | r23;
        r9.flags = r10;	 Catch:{ Exception -> 0x1572, all -> 0x156f }
        goto L_0x1608;
    L_0x1606:
        r44 = r10;
    L_0x1608:
        r15 = r15 + 1;
        r10 = r44;
        goto L_0x15e7;
    L_0x160d:
        r44 = r11;
        goto L_0x164f;
    L_0x1610:
        r44 = r11;
        r9 = 3;
        r10 = r1.longValue(r9);	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r15 = r4.get(r10);	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r15 = (java.util.ArrayList) r15;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r4.remove(r10);	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        if (r15 == 0) goto L_0x164f;
    L_0x1622:
        r10 = 0;
    L_0x1623:
        r11 = r15.size();	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        if (r10 >= r11) goto L_0x164f;
    L_0x1629:
        r11 = r15.get(r10);	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r11.replyMessage = r8;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r9 = r8.id;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r11.reply_to_msg_id = r9;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r9 = org.telegram.messenger.MessageObject.isMegagroup(r11);	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        if (r9 == 0) goto L_0x1643;
    L_0x163b:
        r9 = r11.replyMessage;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r11 = r9.flags;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
        r11 = r11 | r23;
        r9.flags = r11;	 Catch:{ Exception -> 0x1649, all -> 0x1647 }
    L_0x1643:
        r10 = r10 + 1;
        r9 = 3;
        goto L_0x1623;
    L_0x1647:
        r0 = move-exception;
        goto L_0x16a0;
    L_0x1649:
        r0 = move-exception;
        goto L_0x16a9;
    L_0x164b:
        r44 = r11;
        r2 = r33;
    L_0x164f:
        r11 = r44;
        r33 = r2;
        goto L_0x156d;
    L_0x1655:
        r0 = move-exception;
        r44 = r11;
        goto L_0x1713;
    L_0x165a:
        r0 = move-exception;
        r44 = r11;
        goto L_0x1725;
    L_0x165f:
        r44 = r11;
        r2 = r33;
        r1.dispose();	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        r1 = r4.size();	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        if (r1 <= 0) goto L_0x169a;
    L_0x166c:
        r1 = 0;
    L_0x166d:
        r8 = r4.size();	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        if (r1 >= r8) goto L_0x169a;
    L_0x1673:
        r8 = r4.valueAt(r1);	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        r8 = (java.util.ArrayList) r8;	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        r9 = 0;
    L_0x167a:
        r10 = r8.size();	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        if (r9 >= r10) goto L_0x1691;
    L_0x1680:
        r10 = r8.get(r9);	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;	 Catch:{ Exception -> 0x16a6, all -> 0x169d }
        r50 = r12;
        r11 = 0;
        r10.reply_to_random_id = r11;	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        r9 = r9 + 1;
        r12 = r50;
        goto L_0x167a;
    L_0x1691:
        r50 = r12;
        r11 = 0;
        r1 = r1 + 1;
        r12 = r50;
        goto L_0x166d;
    L_0x169a:
        r50 = r12;
        goto L_0x16bd;
    L_0x169d:
        r0 = move-exception;
        r50 = r12;
    L_0x16a0:
        r20 = r43;
        r11 = r44;
        goto L_0x1715;
    L_0x16a6:
        r0 = move-exception;
        r50 = r12;
    L_0x16a9:
        r20 = r43;
        r11 = r44;
        goto L_0x1727;
    L_0x16af:
        r0 = move-exception;
        goto L_0x170f;
    L_0x16b2:
        r0 = move-exception;
        goto L_0x1721;
    L_0x16b5:
        r43 = r1;
        r44 = r11;
        r50 = r12;
        r2 = r33;
    L_0x16bd:
        r1 = r2.isEmpty();	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        if (r1 != 0) goto L_0x16cc;
    L_0x16c3:
        r1 = android.text.TextUtils.join(r5, r2);	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        r2 = r3.users;	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        r14.getUsersInternal(r1, r2);	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
    L_0x16cc:
        r1 = r13.isEmpty();	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        if (r1 != 0) goto L_0x16db;
    L_0x16d2:
        r1 = android.text.TextUtils.join(r5, r13);	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        r2 = r3.chats;	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
        r14.getChatsInternal(r1, r2);	 Catch:{ Exception -> 0x1704, all -> 0x16fc }
    L_0x16db:
        r2 = r42.getMessagesController();
        r9 = 1;
        r4 = r46;
        r8 = r51;
        r10 = r52;
        r11 = r44;
        r12 = r50;
        r13 = r22;
        r14 = r35;
        r15 = r49;
        r16 = r45;
        r17 = r18;
        r18 = r48;
        r19 = r53;
        r20 = r43;
        goto L_0x17be;
    L_0x16fc:
        r0 = move-exception;
        r20 = r43;
        r11 = r44;
        r12 = r50;
        goto L_0x1715;
    L_0x1704:
        r0 = move-exception;
        r20 = r43;
        r11 = r44;
        r12 = r50;
        goto L_0x1727;
    L_0x170c:
        r0 = move-exception;
        r43 = r1;
    L_0x170f:
        r44 = r11;
        r50 = r12;
    L_0x1713:
        r20 = r43;
    L_0x1715:
        r1 = r0;
        r17 = r18;
        r13 = r22;
        r14 = r35;
        goto L_0x17c4;
    L_0x171e:
        r0 = move-exception;
        r43 = r1;
    L_0x1721:
        r44 = r11;
        r50 = r12;
    L_0x1725:
        r20 = r43;
    L_0x1727:
        r1 = r0;
        r17 = r18;
        r13 = r22;
        r14 = r35;
        goto L_0x1799;
    L_0x1730:
        r0 = move-exception;
        goto L_0x1473;
    L_0x1733:
        r0 = move-exception;
        goto L_0x1493;
    L_0x1736:
        r0 = move-exception;
        r3 = r37;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r12 = r10;
        r11 = r34;
        r14 = r35;
        goto L_0x1762;
    L_0x1745:
        r0 = move-exception;
        r3 = r37;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r12 = r10;
        r11 = r34;
        r14 = r35;
        goto L_0x1773;
    L_0x1754:
        r0 = move-exception;
        r3 = r37;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r11 = r34;
        r14 = r35;
    L_0x1761:
        r12 = 0;
    L_0x1762:
        r17 = 1;
        goto L_0x1783;
    L_0x1765:
        r0 = move-exception;
        r3 = r37;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r13 = r9;
        r11 = r34;
        r14 = r35;
    L_0x1772:
        r12 = 0;
    L_0x1773:
        r17 = 1;
        goto L_0x1795;
    L_0x1776:
        r0 = move-exception;
        r3 = r6;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
    L_0x1783:
        r20 = 0;
        r21 = 0;
        goto L_0x17c4;
    L_0x1788:
        r0 = move-exception;
        r3 = r6;
        r1 = 0;
        r7 = r44;
        r1 = r0;
        r6 = r2;
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r14 = 0;
        r17 = 0;
    L_0x1795:
        r20 = 0;
        r21 = 0;
    L_0x1799:
        r2 = r3.messages;	 Catch:{ all -> 0x17c2 }
        r2.clear();	 Catch:{ all -> 0x17c2 }
        r2 = r3.chats;	 Catch:{ all -> 0x17c2 }
        r2.clear();	 Catch:{ all -> 0x17c2 }
        r2 = r3.users;	 Catch:{ all -> 0x17c2 }
        r2.clear();	 Catch:{ all -> 0x17c2 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x17c2 }
        r2 = r42.getMessagesController();
        r9 = 1;
        r4 = r46;
        r8 = r51;
        r10 = r52;
        r15 = r49;
        r16 = r45;
        r18 = r48;
        r19 = r53;
    L_0x17be:
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);
        return;
    L_0x17c2:
        r0 = move-exception;
    L_0x17c3:
        r1 = r0;
    L_0x17c4:
        r2 = r42.getMessagesController();
        r9 = 1;
        r4 = r46;
        r8 = r51;
        r10 = r52;
        r15 = r49;
        r16 = r45;
        r18 = r48;
        r19 = r53;
        r2.processLoadedMessages(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);
        goto L_0x17dc;
    L_0x17db:
        throw r1;
    L_0x17dc:
        goto L_0x17db;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getMessages$95$MessagesStorage(int, int, boolean, long, boolean, int, int, int, int, int):void");
    }

    static /* synthetic */ int lambda$null$94(Message message, Message message2) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$njxbifD6AXEPiNOzHZsIqSblVS8(this));
    }

    public /* synthetic */ void lambda$clearSentMedia$96$MessagesStorage() {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$s-45P4D9OAiSlKuJqqgnARiK_mM(this, str, i, objArr, countDownLatch));
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

    public /* synthetic */ void lambda$getSentFile$97$MessagesStorage(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$mTGwtSWOg-fOMb8bNazzz_gdl0s(this, str, tLObject, i, str2));
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
    public /* synthetic */ void lambda$putSentFile$98$MessagesStorage(java.lang.String r5, org.telegram.tgnet.TLObject r6, int r7, java.lang.String r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putSentFile$98$MessagesStorage(java.lang.String, org.telegram.tgnet.TLObject, int, java.lang.String):void");
    }

    public void updateEncryptedChatSeq(EncryptedChat encryptedChat, boolean z) {
        if (encryptedChat != null) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$vx1OpWNdAe5jIgeIshD6Cyduj80(this, encryptedChat, z));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatSeq$99$MessagesStorage(EncryptedChat encryptedChat, boolean z) {
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
            if (z && encryptedChat.in_seq_no != 0) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$DGY8npr5gioqJLEAmLJ7LcMehr8(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatTTL$100$MessagesStorage(EncryptedChat encryptedChat) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$QyJdkRbwRrKR-Ts-lvar_e8NW3c(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChatLayer$101$MessagesStorage(EncryptedChat encryptedChat) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$XFQmW8MwiJ7mArJFrvtIZIIp-j8(this, encryptedChat));
        }
    }

    public /* synthetic */ void lambda$updateEncryptedChat$102$MessagesStorage(EncryptedChat encryptedChat) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$xVnKpRJTs-1vA4217QyeJWYXb54(this, j, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$isDialogHasMessages$103$MessagesStorage(long j, boolean[] zArr, CountDownLatch countDownLatch) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$0WkebghSVZdScodImy6u-4DrYzc(this, i, zArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return zArr[0];
    }

    public /* synthetic */ void lambda$hasAuthMessage$104$MessagesStorage(int i, boolean[] zArr, CountDownLatch countDownLatch) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$Ys9BmtDj5yAwfuNo_Yj_T3FtYnM(this, i, arrayList, countDownLatch));
        }
    }

    public /* synthetic */ void lambda$getEncryptedChat$105$MessagesStorage(int i, ArrayList arrayList, CountDownLatch countDownLatch) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$DnelZa1G0UoWk1ZgHtU-AICVVxQ(this, encryptedChat, user, dialog));
        }
    }

    public /* synthetic */ void lambda$putEncryptedChat$106$MessagesStorage(EncryptedChat encryptedChat, User user, Dialog dialog) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$u73NEAoH0FZeksfL9eeIz4uLrL0(this, i, i2, tL_chatBannedRights));
        }
    }

    public /* synthetic */ void lambda$updateChatDefaultBannedRights$107$MessagesStorage(int i, int i2, TL_chatBannedRights tL_chatBannedRights) {
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
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$FRdthxn_PMzsOPnqC6D8MtAH2HI(this, arrayList, arrayList2, z));
            } else {
                putUsersAndChatsInternal(arrayList, arrayList2, z);
            }
        }
    }

    public /* synthetic */ void lambda$putUsersAndChats$108$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, boolean z) {
        putUsersAndChatsInternal(arrayList, arrayList2, z);
    }

    public void removeFromDownloadQueue(long j, int i, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$aTq1I_sTlcC3aX3sCf-OY6rwxXc(this, z, i, j));
    }

    public /* synthetic */ void lambda$removeFromDownloadQueue$109$MessagesStorage(boolean z, int i, long j) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$2j00OtO7VT4auLIM_kQx7Z3LLC0(this, i));
    }

    public /* synthetic */ void lambda$clearDownloadQueue$110$MessagesStorage(int i) {
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
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$MDhQVfNZmNZBYCBBl31q2G9pbbg(this, i));
    }

    public /* synthetic */ void lambda$getDownloadQueue$112$MessagesStorage(int i) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$f4Ywh1aQsEB6vnx8exhHF-VMhzM(this, i, arrayList));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$111$MessagesStorage(int i, ArrayList arrayList) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$H0qvW6G0XWu1E5Qhn_LekFbVPFE(this, longSparseArray));
        }
    }

    public /* synthetic */ void lambda$putWebPages$114$MessagesStorage(LongSparseArray longSparseArray) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$Jywv3U_eM6e1Ryz8g--xCCRrzLk(this, arrayList));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$113$MessagesStorage(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(int i, TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, int i2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$raA2_pcbxTe4GCBWKMdSRUof4ow(this, i, i2, tL_updates_channelDifferenceTooLong));
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0114 A:{Catch:{ Exception -> 0x0145 }} */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0112 A:{Catch:{ Exception -> 0x0145 }} */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0133 A:{Catch:{ Exception -> 0x0145 }} */
    public /* synthetic */ void lambda$overwriteChannel$116$MessagesStorage(int r11, int r12, org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong r13) {
        /*
        r10 = this;
        r0 = -r11;
        r0 = (long) r0;
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r3.<init>();	 Catch:{ Exception -> 0x0145 }
        r4 = "SELECT pinned FROM dialogs WHERE did = ";
        r3.append(r4);	 Catch:{ Exception -> 0x0145 }
        r3.append(r0);	 Catch:{ Exception -> 0x0145 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0145 }
        r4 = 0;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0145 }
        r2 = r2.queryFinalized(r3, r5);	 Catch:{ Exception -> 0x0145 }
        r3 = r2.next();	 Catch:{ Exception -> 0x0145 }
        r5 = 1;
        if (r3 != 0) goto L_0x002a;
    L_0x0023:
        r3 = 0;
        if (r12 == 0) goto L_0x0028;
    L_0x0026:
        r6 = 1;
        goto L_0x002f;
    L_0x0028:
        r6 = 0;
        goto L_0x002f;
    L_0x002a:
        r3 = r2.intValue(r4);	 Catch:{ Exception -> 0x0145 }
        goto L_0x0028;
    L_0x002f:
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "DELETE FROM messages WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "DELETE FROM bot_keyboard WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "UPDATE media_counts_v2 SET old = 1 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "DELETE FROM media_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "DELETE FROM messages_holes WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.database;	 Catch:{ Exception -> 0x0145 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0145 }
        r7.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = "DELETE FROM media_holes_v2 WHERE uid = ";
        r7.append(r8);	 Catch:{ Exception -> 0x0145 }
        r7.append(r0);	 Catch:{ Exception -> 0x0145 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0145 }
        r2 = r2.executeFast(r7);	 Catch:{ Exception -> 0x0145 }
        r2 = r2.stepThis();	 Catch:{ Exception -> 0x0145 }
        r2.dispose();	 Catch:{ Exception -> 0x0145 }
        r2 = r10.getMediaDataController();	 Catch:{ Exception -> 0x0145 }
        r7 = 0;
        r2.clearBotKeyboard(r0, r7);	 Catch:{ Exception -> 0x0145 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;	 Catch:{ Exception -> 0x0145 }
        r2.<init>();	 Catch:{ Exception -> 0x0145 }
        r8 = r2.chats;	 Catch:{ Exception -> 0x0145 }
        r9 = r13.chats;	 Catch:{ Exception -> 0x0145 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0145 }
        r8 = r2.users;	 Catch:{ Exception -> 0x0145 }
        r9 = r13.users;	 Catch:{ Exception -> 0x0145 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0145 }
        r8 = r2.messages;	 Catch:{ Exception -> 0x0145 }
        r9 = r13.messages;	 Catch:{ Exception -> 0x0145 }
        r8.addAll(r9);	 Catch:{ Exception -> 0x0145 }
        r13 = r13.dialog;	 Catch:{ Exception -> 0x0145 }
        r13.id = r0;	 Catch:{ Exception -> 0x0145 }
        r13.flags = r5;	 Catch:{ Exception -> 0x0145 }
        r13.notify_settings = r7;	 Catch:{ Exception -> 0x0145 }
        if (r3 == 0) goto L_0x0114;
    L_0x0112:
        r8 = 1;
        goto L_0x0115;
    L_0x0114:
        r8 = 0;
    L_0x0115:
        r13.pinned = r8;	 Catch:{ Exception -> 0x0145 }
        r13.pinnedNum = r3;	 Catch:{ Exception -> 0x0145 }
        r3 = r2.dialogs;	 Catch:{ Exception -> 0x0145 }
        r3.add(r13);	 Catch:{ Exception -> 0x0145 }
        r10.putDialogsInternal(r2, r4);	 Catch:{ Exception -> 0x0145 }
        r13 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0145 }
        r13.<init>();	 Catch:{ Exception -> 0x0145 }
        r10.updateDialogsWithDeletedMessages(r13, r7, r4, r11);	 Catch:{ Exception -> 0x0145 }
        r13 = new org.telegram.messenger.-$$Lambda$MessagesStorage$orNkObitaRfQdOiVK8wmjhp7nhM;	 Catch:{ Exception -> 0x0145 }
        r13.<init>(r10, r0);	 Catch:{ Exception -> 0x0145 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);	 Catch:{ Exception -> 0x0145 }
        if (r6 == 0) goto L_0x0149;
    L_0x0133:
        if (r12 != r5) goto L_0x013d;
    L_0x0135:
        r12 = r10.getMessagesController();	 Catch:{ Exception -> 0x0145 }
        r12.checkChannelInviter(r11);	 Catch:{ Exception -> 0x0145 }
        goto L_0x0149;
    L_0x013d:
        r12 = r10.getMessagesController();	 Catch:{ Exception -> 0x0145 }
        r12.generateJoinMessage(r11, r4);	 Catch:{ Exception -> 0x0145 }
        goto L_0x0149;
    L_0x0145:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
    L_0x0149:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$overwriteChannel$116$MessagesStorage(int, int, org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong):void");
    }

    public /* synthetic */ void lambda$null$115$MessagesStorage(long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.valueOf(true));
    }

    public void putChannelViews(SparseArray<SparseIntArray> sparseArray, boolean z) {
        if (!isEmpty((SparseArray) sparseArray)) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$V4GicTEsvEKlkqYc2NvS_f_Cy2o(this, sparseArray, z));
        }
    }

    public /* synthetic */ void lambda$putChannelViews$117$MessagesStorage(SparseArray sparseArray, boolean z) {
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

    /* JADX WARNING: Removed duplicated region for block: B:250:0x06a6 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0694 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0730 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0592 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x058a A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05a9 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x05a7 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x05cd A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x05b8 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x05d7 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x062e A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0609 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0694 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06a6 A:{Catch:{ Exception -> 0x0113 }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0730 A:{Catch:{ Exception -> 0x0113 }} */
    private void putMessagesInternal(java.util.ArrayList<org.telegram.tgnet.TLRPC.Message> r33, boolean r34, boolean r35, int r36, boolean r37, boolean r38) {
        /*
        r32 = this;
        r1 = r32;
        r2 = r33;
        r3 = "REPLACE INTO randoms VALUES(?, ?)";
        r5 = 32;
        r6 = 5;
        if (r38 == 0) goto L_0x00d1;
    L_0x000b:
        if (r34 == 0) goto L_0x0012;
    L_0x000d:
        r14 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r14.beginTransaction();	 Catch:{ Exception -> 0x0113 }
    L_0x0012:
        r14 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r15 = "REPLACE INTO scheduled_messages VALUES(?, ?, ?, ?, ?, ?, NULL)";
        r14 = r14.executeFast(r15);	 Catch:{ Exception -> 0x0113 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = r15.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
        r15 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0113 }
        r15.<init>();	 Catch:{ Exception -> 0x0113 }
        r13 = 0;
    L_0x0026:
        r7 = r33.size();	 Catch:{ Exception -> 0x0113 }
        if (r13 >= r7) goto L_0x00b1;
    L_0x002c:
        r7 = r2.get(r13);	 Catch:{ Exception -> 0x0113 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x0113 }
        r1.fixUnsupportedMedia(r7);	 Catch:{ Exception -> 0x0113 }
        r14.requery();	 Catch:{ Exception -> 0x0113 }
        r8 = r7.id;	 Catch:{ Exception -> 0x0113 }
        r9 = (long) r8;	 Catch:{ Exception -> 0x0113 }
        r8 = r7.local_id;	 Catch:{ Exception -> 0x0113 }
        if (r8 == 0) goto L_0x0042;
    L_0x003f:
        r8 = r7.local_id;	 Catch:{ Exception -> 0x0113 }
        r9 = (long) r8;	 Catch:{ Exception -> 0x0113 }
    L_0x0042:
        r8 = r7.to_id;	 Catch:{ Exception -> 0x0113 }
        r8 = r8.channel_id;	 Catch:{ Exception -> 0x0113 }
        if (r8 == 0) goto L_0x004f;
    L_0x0048:
        r8 = r7.to_id;	 Catch:{ Exception -> 0x0113 }
        r8 = r8.channel_id;	 Catch:{ Exception -> 0x0113 }
        r11 = (long) r8;	 Catch:{ Exception -> 0x0113 }
        r11 = r11 << r5;
        r9 = r9 | r11;
    L_0x004f:
        r8 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0113 }
        r11 = r7.getObjectSize();	 Catch:{ Exception -> 0x0113 }
        r8.<init>(r11);	 Catch:{ Exception -> 0x0113 }
        r7.serializeToStream(r8);	 Catch:{ Exception -> 0x0113 }
        r11 = org.telegram.messenger.MessageObject.getDialogId(r7);	 Catch:{ Exception -> 0x0113 }
        r5 = 1;
        r14.bindLong(r5, r9);	 Catch:{ Exception -> 0x0113 }
        r5 = 2;
        r14.bindLong(r5, r11);	 Catch:{ Exception -> 0x0113 }
        r5 = r7.send_state;	 Catch:{ Exception -> 0x0113 }
        r4 = 3;
        r14.bindInteger(r4, r5);	 Catch:{ Exception -> 0x0113 }
        r4 = r7.date;	 Catch:{ Exception -> 0x0113 }
        r5 = 4;
        r14.bindInteger(r5, r4);	 Catch:{ Exception -> 0x0113 }
        r14.bindByteBuffer(r6, r8);	 Catch:{ Exception -> 0x0113 }
        r4 = r7.ttl;	 Catch:{ Exception -> 0x0113 }
        r5 = 6;
        r14.bindInteger(r5, r4);	 Catch:{ Exception -> 0x0113 }
        r14.step();	 Catch:{ Exception -> 0x0113 }
        r4 = r7.random_id;	 Catch:{ Exception -> 0x0113 }
        r17 = 0;
        r21 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1));
        if (r21 == 0) goto L_0x0097;
    L_0x0087:
        r3.requery();	 Catch:{ Exception -> 0x0113 }
        r4 = r7.random_id;	 Catch:{ Exception -> 0x0113 }
        r7 = 1;
        r3.bindLong(r7, r4);	 Catch:{ Exception -> 0x0113 }
        r4 = 2;
        r3.bindLong(r4, r9);	 Catch:{ Exception -> 0x0113 }
        r3.step();	 Catch:{ Exception -> 0x0113 }
    L_0x0097:
        r8.reuse();	 Catch:{ Exception -> 0x0113 }
        r4 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
        r4 = r15.contains(r4);	 Catch:{ Exception -> 0x0113 }
        if (r4 != 0) goto L_0x00ab;
    L_0x00a4:
        r4 = java.lang.Long.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
        r15.add(r4);	 Catch:{ Exception -> 0x0113 }
    L_0x00ab:
        r13 = r13 + 1;
        r5 = 32;
        goto L_0x0026;
    L_0x00b1:
        r14.dispose();	 Catch:{ Exception -> 0x0113 }
        r3.dispose();	 Catch:{ Exception -> 0x0113 }
        if (r34 == 0) goto L_0x00be;
    L_0x00b9:
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r2.commitTransaction();	 Catch:{ Exception -> 0x0113 }
    L_0x00be:
        r2 = r15.size();	 Catch:{ Exception -> 0x0113 }
        r3 = 0;
    L_0x00c3:
        if (r3 >= r2) goto L_0x0a5c;
    L_0x00c5:
        r4 = r15.get(r3);	 Catch:{ Exception -> 0x0113 }
        r4 = (java.lang.Long) r4;	 Catch:{ Exception -> 0x0113 }
        r1.broadcastScheduledMessagesChange(r4);	 Catch:{ Exception -> 0x0113 }
        r3 = r3 + 1;
        goto L_0x00c3;
    L_0x00d1:
        if (r37 == 0) goto L_0x0117;
    L_0x00d3:
        r5 = 0;
        r7 = r2.get(r5);	 Catch:{ Exception -> 0x0113 }
        r7 = (org.telegram.tgnet.TLRPC.Message) r7;	 Catch:{ Exception -> 0x0113 }
        r8 = r7.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 != 0) goto L_0x00e5;
    L_0x00e2:
        org.telegram.messenger.MessageObject.getDialogId(r7);	 Catch:{ Exception -> 0x0113 }
    L_0x00e5:
        r5 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r8.<init>();	 Catch:{ Exception -> 0x0113 }
        r9 = "SELECT last_mid FROM dialogs WHERE did = ";
        r8.append(r9);	 Catch:{ Exception -> 0x0113 }
        r9 = r7.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r8.append(r9);	 Catch:{ Exception -> 0x0113 }
        r7 = r8.toString();	 Catch:{ Exception -> 0x0113 }
        r8 = 0;
        r9 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0113 }
        r5 = r5.queryFinalized(r7, r9);	 Catch:{ Exception -> 0x0113 }
        r7 = r5.next();	 Catch:{ Exception -> 0x0113 }
        if (r7 == 0) goto L_0x010c;
    L_0x0107:
        r7 = r5.intValue(r8);	 Catch:{ Exception -> 0x0113 }
        goto L_0x010d;
    L_0x010c:
        r7 = -1;
    L_0x010d:
        r5.dispose();	 Catch:{ Exception -> 0x0113 }
        if (r7 == 0) goto L_0x0117;
    L_0x0112:
        return;
    L_0x0113:
        r0 = move-exception;
    L_0x0114:
        r2 = r0;
        goto L_0x0a59;
    L_0x0117:
        if (r34 == 0) goto L_0x011e;
    L_0x0119:
        r5 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r5.beginTransaction();	 Catch:{ Exception -> 0x0113 }
    L_0x011e:
        r5 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r5.<init>();	 Catch:{ Exception -> 0x0113 }
        r7 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r7.<init>();	 Catch:{ Exception -> 0x0113 }
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r8.<init>();	 Catch:{ Exception -> 0x0113 }
        r9 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r9.<init>();	 Catch:{ Exception -> 0x0113 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r10.<init>();	 Catch:{ Exception -> 0x0113 }
        r11 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r11.<init>();	 Catch:{ Exception -> 0x0113 }
        r12 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r12.<init>();	 Catch:{ Exception -> 0x0113 }
        r13 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r13.<init>();	 Catch:{ Exception -> 0x0113 }
        r14 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r15 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r14 = r14.executeFast(r15);	 Catch:{ Exception -> 0x0113 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = r15.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r6 = "REPLACE INTO download_queue VALUES(?, ?, ?, ?, ?)";
        r6 = r15.executeFast(r6);	 Catch:{ Exception -> 0x0113 }
        r15 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r4 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r4 = r15.executeFast(r4);	 Catch:{ Exception -> 0x0113 }
        r25 = r3;
        r24 = r4;
        r23 = r6;
        r4 = 0;
        r6 = 0;
        r15 = 0;
        r22 = 0;
    L_0x016f:
        r3 = r33.size();	 Catch:{ Exception -> 0x0113 }
        if (r15 >= r3) goto L_0x02b0;
    L_0x0175:
        r3 = r2.get(r15);	 Catch:{ Exception -> 0x0113 }
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;	 Catch:{ Exception -> 0x0113 }
        r26 = r5;
        r5 = r3.id;	 Catch:{ Exception -> 0x0113 }
        r27 = r7;
        r28 = r8;
        r7 = (long) r5;	 Catch:{ Exception -> 0x0113 }
        r29 = r6;
        r5 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r17 = 0;
        r30 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1));
        if (r30 != 0) goto L_0x0191;
    L_0x018e:
        org.telegram.messenger.MessageObject.getDialogId(r3);	 Catch:{ Exception -> 0x0113 }
    L_0x0191:
        r5 = r3.to_id;	 Catch:{ Exception -> 0x0113 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x01a1;
    L_0x0197:
        r5 = r3.to_id;	 Catch:{ Exception -> 0x0113 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0113 }
        r5 = (long) r5;	 Catch:{ Exception -> 0x0113 }
        r20 = 32;
        r5 = r5 << r20;
        r7 = r7 | r5;
    L_0x01a1:
        r5 = r3.mentioned;	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x01b2;
    L_0x01a5:
        r5 = r3.media_unread;	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x01b2;
    L_0x01a9:
        r5 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ Exception -> 0x0113 }
        r13.put(r7, r5);	 Catch:{ Exception -> 0x0113 }
    L_0x01b2:
        r5 = r3.action;	 Catch:{ Exception -> 0x0113 }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x0113 }
        if (r5 != 0) goto L_0x023b;
    L_0x01b8:
        r5 = org.telegram.messenger.MessageObject.isOut(r3);	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x01c2;
    L_0x01be:
        r5 = r3.from_scheduled;	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x023b;
    L_0x01c2:
        r5 = r3.id;	 Catch:{ Exception -> 0x0113 }
        if (r5 > 0) goto L_0x01cc;
    L_0x01c6:
        r5 = org.telegram.messenger.MessageObject.isUnread(r3);	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x023b;
    L_0x01cc:
        r5 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r5 = r11.get(r5);	 Catch:{ Exception -> 0x0113 }
        r5 = (java.lang.Integer) r5;	 Catch:{ Exception -> 0x0113 }
        if (r5 != 0) goto L_0x0213;
    L_0x01d6:
        r5 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r6.<init>();	 Catch:{ Exception -> 0x0113 }
        r30 = r14;
        r14 = "SELECT inbox_max FROM dialogs WHERE did = ";
        r6.append(r14);	 Catch:{ Exception -> 0x0113 }
        r31 = r13;
        r13 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r6.append(r13);	 Catch:{ Exception -> 0x0113 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0113 }
        r13 = 0;
        r14 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0113 }
        r5 = r5.queryFinalized(r6, r14);	 Catch:{ Exception -> 0x0113 }
        r6 = r5.next();	 Catch:{ Exception -> 0x0113 }
        if (r6 == 0) goto L_0x0205;
    L_0x01fc:
        r6 = r5.intValue(r13);	 Catch:{ Exception -> 0x0113 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0209;
    L_0x0205:
        r6 = java.lang.Integer.valueOf(r13);	 Catch:{ Exception -> 0x0113 }
    L_0x0209:
        r5.dispose();	 Catch:{ Exception -> 0x0113 }
        r13 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r11.put(r13, r6);	 Catch:{ Exception -> 0x0113 }
        r5 = r6;
        goto L_0x0217;
    L_0x0213:
        r31 = r13;
        r30 = r14;
    L_0x0217:
        r6 = r3.id;	 Catch:{ Exception -> 0x0113 }
        if (r6 < 0) goto L_0x0223;
    L_0x021b:
        r5 = r5.intValue();	 Catch:{ Exception -> 0x0113 }
        r6 = r3.id;	 Catch:{ Exception -> 0x0113 }
        if (r5 >= r6) goto L_0x023f;
    L_0x0223:
        r5 = r10.length();	 Catch:{ Exception -> 0x0113 }
        if (r5 <= 0) goto L_0x022e;
    L_0x0229:
        r5 = ",";
        r10.append(r5);	 Catch:{ Exception -> 0x0113 }
    L_0x022e:
        r10.append(r7);	 Catch:{ Exception -> 0x0113 }
        r5 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ Exception -> 0x0113 }
        r12.put(r7, r5);	 Catch:{ Exception -> 0x0113 }
        goto L_0x023f;
    L_0x023b:
        r31 = r13;
        r30 = r14;
    L_0x023f:
        r5 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r3);	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x0285;
    L_0x0245:
        if (r22 != 0) goto L_0x025a;
    L_0x0247:
        r22 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r22.<init>();	 Catch:{ Exception -> 0x0113 }
        r6 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r6.<init>();	 Catch:{ Exception -> 0x0113 }
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r4.<init>();	 Catch:{ Exception -> 0x0113 }
        r5 = r4;
        r4 = r22;
        goto L_0x025f;
    L_0x025a:
        r5 = r4;
        r4 = r22;
        r6 = r29;
    L_0x025f:
        r13 = r4.length();	 Catch:{ Exception -> 0x0113 }
        if (r13 <= 0) goto L_0x026a;
    L_0x0265:
        r13 = ",";
        r4.append(r13);	 Catch:{ Exception -> 0x0113 }
    L_0x026a:
        r4.append(r7);	 Catch:{ Exception -> 0x0113 }
        r13 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r13 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0113 }
        r6.put(r7, r13);	 Catch:{ Exception -> 0x0113 }
        r13 = org.telegram.messenger.MediaDataController.getMediaType(r3);	 Catch:{ Exception -> 0x0113 }
        r13 = java.lang.Integer.valueOf(r13);	 Catch:{ Exception -> 0x0113 }
        r5.put(r7, r13);	 Catch:{ Exception -> 0x0113 }
        r22 = r4;
        r4 = r5;
        goto L_0x0287;
    L_0x0285:
        r6 = r29;
    L_0x0287:
        r5 = r1.isValidKeyboardToSave(r3);	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x02a2;
    L_0x028d:
        r7 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r5 = r9.get(r7);	 Catch:{ Exception -> 0x0113 }
        r5 = (org.telegram.tgnet.TLRPC.Message) r5;	 Catch:{ Exception -> 0x0113 }
        if (r5 == 0) goto L_0x029d;
    L_0x0297:
        r5 = r5.id;	 Catch:{ Exception -> 0x0113 }
        r7 = r3.id;	 Catch:{ Exception -> 0x0113 }
        if (r5 >= r7) goto L_0x02a2;
    L_0x029d:
        r7 = r3.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r9.put(r7, r3);	 Catch:{ Exception -> 0x0113 }
    L_0x02a2:
        r15 = r15 + 1;
        r5 = r26;
        r7 = r27;
        r8 = r28;
        r14 = r30;
        r13 = r31;
        goto L_0x016f;
    L_0x02b0:
        r26 = r5;
        r29 = r6;
        r27 = r7;
        r28 = r8;
        r31 = r13;
        r30 = r14;
        r3 = 0;
    L_0x02bd:
        r5 = r9.size();	 Catch:{ Exception -> 0x0113 }
        if (r3 >= r5) goto L_0x02d7;
    L_0x02c3:
        r5 = r32.getMediaDataController();	 Catch:{ Exception -> 0x0113 }
        r6 = r9.keyAt(r3);	 Catch:{ Exception -> 0x0113 }
        r8 = r9.valueAt(r3);	 Catch:{ Exception -> 0x0113 }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x0113 }
        r5.putBotKeyboard(r6, r8);	 Catch:{ Exception -> 0x0113 }
        r3 = r3 + 1;
        goto L_0x02bd;
    L_0x02d7:
        r3 = ")";
        if (r22 == 0) goto L_0x03e0;
    L_0x02db:
        r5 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r6.<init>();	 Catch:{ Exception -> 0x0113 }
        r7 = "SELECT mid, type FROM media_v2 WHERE mid IN(";
        r6.append(r7);	 Catch:{ Exception -> 0x0113 }
        r7 = r22.toString();	 Catch:{ Exception -> 0x0113 }
        r6.append(r7);	 Catch:{ Exception -> 0x0113 }
        r6.append(r3);	 Catch:{ Exception -> 0x0113 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0113 }
        r7 = 0;
        r8 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0113 }
        r5 = r5.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x0113 }
        r6 = 0;
    L_0x02fd:
        r8 = r5.next();	 Catch:{ Exception -> 0x0113 }
        if (r8 == 0) goto L_0x0332;
    L_0x0303:
        r8 = r5.longValue(r7);	 Catch:{ Exception -> 0x0113 }
        r7 = 1;
        r11 = r5.intValue(r7);	 Catch:{ Exception -> 0x0113 }
        r7 = r4.get(r8);	 Catch:{ Exception -> 0x0113 }
        r7 = (java.lang.Integer) r7;	 Catch:{ Exception -> 0x0113 }
        r7 = r7.intValue();	 Catch:{ Exception -> 0x0113 }
        if (r11 != r7) goto L_0x031e;
    L_0x0318:
        r15 = r29;
        r15.remove(r8);	 Catch:{ Exception -> 0x0113 }
        goto L_0x032e;
    L_0x031e:
        r15 = r29;
        if (r6 != 0) goto L_0x0327;
    L_0x0322:
        r6 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r6.<init>();	 Catch:{ Exception -> 0x0113 }
    L_0x0327:
        r7 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
        r6.put(r8, r7);	 Catch:{ Exception -> 0x0113 }
    L_0x032e:
        r29 = r15;
        r7 = 0;
        goto L_0x02fd;
    L_0x0332:
        r15 = r29;
        r5.dispose();	 Catch:{ Exception -> 0x0113 }
        r5 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0113 }
        r5.<init>();	 Catch:{ Exception -> 0x0113 }
        r7 = 0;
    L_0x033d:
        r8 = r15.size();	 Catch:{ Exception -> 0x0113 }
        if (r7 >= r8) goto L_0x03e1;
    L_0x0343:
        r8 = r15.keyAt(r7);	 Catch:{ Exception -> 0x0113 }
        r11 = r15.valueAt(r7);	 Catch:{ Exception -> 0x0113 }
        r11 = (java.lang.Long) r11;	 Catch:{ Exception -> 0x0113 }
        r13 = r11.longValue();	 Catch:{ Exception -> 0x0113 }
        r11 = r4.get(r8);	 Catch:{ Exception -> 0x0113 }
        r11 = (java.lang.Integer) r11;	 Catch:{ Exception -> 0x0113 }
        r22 = r4;
        r4 = r11.intValue();	 Catch:{ Exception -> 0x0113 }
        r4 = r5.get(r4);	 Catch:{ Exception -> 0x0113 }
        r4 = (android.util.LongSparseArray) r4;	 Catch:{ Exception -> 0x0113 }
        if (r4 != 0) goto L_0x0378;
    L_0x0365:
        r4 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r4.<init>();	 Catch:{ Exception -> 0x0113 }
        r16 = 0;
        r29 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0113 }
        r11 = r11.intValue();	 Catch:{ Exception -> 0x0113 }
        r5.put(r11, r4);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0380;
    L_0x0378:
        r11 = r4.get(r13);	 Catch:{ Exception -> 0x0113 }
        r29 = r11;
        r29 = (java.lang.Integer) r29;	 Catch:{ Exception -> 0x0113 }
    L_0x0380:
        if (r29 != 0) goto L_0x0387;
    L_0x0382:
        r11 = 0;
        r29 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
    L_0x0387:
        r11 = r29.intValue();	 Catch:{ Exception -> 0x0113 }
        r19 = 1;
        r11 = r11 + 1;
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
        r4.put(r13, r11);	 Catch:{ Exception -> 0x0113 }
        if (r6 == 0) goto L_0x03da;
    L_0x0398:
        r4 = -1;
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0113 }
        r4 = r6.get(r8, r11);	 Catch:{ Exception -> 0x0113 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0113 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0113 }
        if (r4 < 0) goto L_0x03da;
    L_0x03a9:
        r8 = r5.get(r4);	 Catch:{ Exception -> 0x0113 }
        r8 = (android.util.LongSparseArray) r8;	 Catch:{ Exception -> 0x0113 }
        if (r8 != 0) goto L_0x03bf;
    L_0x03b1:
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0113 }
        r8.<init>();	 Catch:{ Exception -> 0x0113 }
        r9 = 0;
        r11 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0113 }
        r5.put(r4, r8);	 Catch:{ Exception -> 0x0113 }
        goto L_0x03c6;
    L_0x03bf:
        r4 = r8.get(r13);	 Catch:{ Exception -> 0x0113 }
        r11 = r4;
        r11 = (java.lang.Integer) r11;	 Catch:{ Exception -> 0x0113 }
    L_0x03c6:
        if (r11 != 0) goto L_0x03cd;
    L_0x03c8:
        r4 = 0;
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0113 }
    L_0x03cd:
        r4 = r11.intValue();	 Catch:{ Exception -> 0x0113 }
        r9 = 1;
        r4 = r4 - r9;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0113 }
        r8.put(r13, r4);	 Catch:{ Exception -> 0x0113 }
    L_0x03da:
        r7 = r7 + 1;
        r4 = r22;
        goto L_0x033d;
    L_0x03e0:
        r5 = 0;
    L_0x03e1:
        r4 = r10.length();	 Catch:{ Exception -> 0x0113 }
        if (r4 <= 0) goto L_0x0489;
    L_0x03e7:
        r4 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r6.<init>();	 Catch:{ Exception -> 0x0113 }
        r7 = "SELECT mid FROM messages WHERE mid IN(";
        r6.append(r7);	 Catch:{ Exception -> 0x0113 }
        r7 = r10.toString();	 Catch:{ Exception -> 0x0113 }
        r6.append(r7);	 Catch:{ Exception -> 0x0113 }
        r6.append(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = r6.toString();	 Catch:{ Exception -> 0x0113 }
        r6 = 0;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0113 }
        r3 = r4.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x0113 }
    L_0x0408:
        r4 = r3.next();	 Catch:{ Exception -> 0x0113 }
        if (r4 == 0) goto L_0x041e;
    L_0x040e:
        r7 = r3.longValue(r6);	 Catch:{ Exception -> 0x0113 }
        r12.remove(r7);	 Catch:{ Exception -> 0x0113 }
        r4 = r31;
        r4.remove(r7);	 Catch:{ Exception -> 0x0113 }
        r31 = r4;
        r6 = 0;
        goto L_0x0408;
    L_0x041e:
        r4 = r31;
        r3.dispose();	 Catch:{ Exception -> 0x0113 }
        r3 = 0;
    L_0x0424:
        r6 = r12.size();	 Catch:{ Exception -> 0x0113 }
        if (r3 >= r6) goto L_0x0455;
    L_0x042a:
        r6 = r12.valueAt(r3);	 Catch:{ Exception -> 0x0113 }
        r6 = (java.lang.Long) r6;	 Catch:{ Exception -> 0x0113 }
        r6 = r6.longValue();	 Catch:{ Exception -> 0x0113 }
        r8 = r27;
        r9 = r8.get(r6);	 Catch:{ Exception -> 0x0113 }
        r9 = (java.lang.Integer) r9;	 Catch:{ Exception -> 0x0113 }
        if (r9 != 0) goto L_0x0443;
    L_0x043e:
        r10 = 0;
        r9 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0113 }
    L_0x0443:
        r9 = r9.intValue();	 Catch:{ Exception -> 0x0113 }
        r10 = 1;
        r9 = r9 + r10;
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0113 }
        r8.put(r6, r9);	 Catch:{ Exception -> 0x0113 }
        r3 = r3 + 1;
        r27 = r8;
        goto L_0x0424;
    L_0x0455:
        r8 = r27;
        r3 = 0;
    L_0x0458:
        r6 = r4.size();	 Catch:{ Exception -> 0x0113 }
        if (r3 >= r6) goto L_0x048b;
    L_0x045e:
        r6 = r4.valueAt(r3);	 Catch:{ Exception -> 0x0113 }
        r6 = (java.lang.Long) r6;	 Catch:{ Exception -> 0x0113 }
        r6 = r6.longValue();	 Catch:{ Exception -> 0x0113 }
        r9 = r28;
        r10 = r9.get(r6);	 Catch:{ Exception -> 0x0113 }
        r10 = (java.lang.Integer) r10;	 Catch:{ Exception -> 0x0113 }
        if (r10 != 0) goto L_0x0477;
    L_0x0472:
        r11 = 0;
        r10 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x0113 }
    L_0x0477:
        r10 = r10.intValue();	 Catch:{ Exception -> 0x0113 }
        r11 = 1;
        r10 = r10 + r11;
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0113 }
        r9.put(r6, r10);	 Catch:{ Exception -> 0x0113 }
        r3 = r3 + 1;
        r28 = r9;
        goto L_0x0458;
    L_0x0489:
        r8 = r27;
    L_0x048b:
        r9 = r28;
        r3 = 0;
        r4 = 0;
        r6 = 0;
        r7 = 0;
    L_0x0491:
        r10 = r33.size();	 Catch:{ Exception -> 0x0113 }
        if (r3 >= r10) goto L_0x07b0;
    L_0x0497:
        r10 = r2.get(r3);	 Catch:{ Exception -> 0x0113 }
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;	 Catch:{ Exception -> 0x0113 }
        r1.fixUnsupportedMedia(r10);	 Catch:{ Exception -> 0x0113 }
        r30.requery();	 Catch:{ Exception -> 0x0113 }
        r13 = r10.id;	 Catch:{ Exception -> 0x0113 }
        r13 = (long) r13;	 Catch:{ Exception -> 0x0113 }
        r15 = r10.local_id;	 Catch:{ Exception -> 0x0113 }
        if (r15 == 0) goto L_0x04ad;
    L_0x04aa:
        r13 = r10.local_id;	 Catch:{ Exception -> 0x0113 }
        r13 = (long) r13;	 Catch:{ Exception -> 0x0113 }
    L_0x04ad:
        r15 = r10.to_id;	 Catch:{ Exception -> 0x0113 }
        r15 = r15.channel_id;	 Catch:{ Exception -> 0x0113 }
        if (r15 == 0) goto L_0x04bc;
    L_0x04b3:
        r15 = r10.to_id;	 Catch:{ Exception -> 0x0113 }
        r15 = r15.channel_id;	 Catch:{ Exception -> 0x0113 }
        r11 = (long) r15;	 Catch:{ Exception -> 0x0113 }
        r15 = 32;
        r11 = r11 << r15;
        r13 = r13 | r11;
    L_0x04bc:
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0113 }
        r12 = r10.getObjectSize();	 Catch:{ Exception -> 0x0113 }
        r11.<init>(r12);	 Catch:{ Exception -> 0x0113 }
        r10.serializeToStream(r11);	 Catch:{ Exception -> 0x0113 }
        r12 = r10.action;	 Catch:{ Exception -> 0x0113 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;	 Catch:{ Exception -> 0x0113 }
        if (r12 == 0) goto L_0x04e2;
    L_0x04ce:
        r12 = r10.action;	 Catch:{ Exception -> 0x0113 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0113 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;	 Catch:{ Exception -> 0x0113 }
        if (r12 != 0) goto L_0x04e2;
    L_0x04d6:
        r12 = r10.action;	 Catch:{ Exception -> 0x0113 }
        r12 = r12.encryptedAction;	 Catch:{ Exception -> 0x0113 }
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;	 Catch:{ Exception -> 0x0113 }
        if (r12 != 0) goto L_0x04e2;
    L_0x04de:
        r28 = r5;
        r5 = 0;
        goto L_0x050f;
    L_0x04e2:
        r12 = r10.out;	 Catch:{ Exception -> 0x0113 }
        if (r12 == 0) goto L_0x050c;
    L_0x04e6:
        r12 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r15.<init>();	 Catch:{ Exception -> 0x0113 }
        r2 = "SELECT mid FROM messages WHERE mid = ";
        r15.append(r2);	 Catch:{ Exception -> 0x0113 }
        r15.append(r13);	 Catch:{ Exception -> 0x0113 }
        r2 = r15.toString();	 Catch:{ Exception -> 0x0113 }
        r28 = r5;
        r15 = 0;
        r5 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0113 }
        r2 = r12.queryFinalized(r2, r5);	 Catch:{ Exception -> 0x0113 }
        r5 = r2.next();	 Catch:{ Exception -> 0x0113 }
        r12 = 1;
        r5 = r5 ^ r12;
        r2.dispose();	 Catch:{ Exception -> 0x0113 }
        goto L_0x050f;
    L_0x050c:
        r28 = r5;
        r5 = 1;
    L_0x050f:
        if (r5 == 0) goto L_0x053f;
    L_0x0511:
        r2 = r8;
        r5 = r9;
        r8 = r10.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r12 = r26;
        r8 = r12.get(r8);	 Catch:{ Exception -> 0x0113 }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x0113 }
        if (r8 == 0) goto L_0x0539;
    L_0x051f:
        r9 = r10.date;	 Catch:{ Exception -> 0x0113 }
        r15 = r8.date;	 Catch:{ Exception -> 0x0113 }
        if (r9 > r15) goto L_0x0539;
    L_0x0525:
        r9 = r8.id;	 Catch:{ Exception -> 0x0113 }
        if (r9 <= 0) goto L_0x052f;
    L_0x0529:
        r9 = r10.id;	 Catch:{ Exception -> 0x0113 }
        r15 = r8.id;	 Catch:{ Exception -> 0x0113 }
        if (r9 > r15) goto L_0x0539;
    L_0x052f:
        r9 = r8.id;	 Catch:{ Exception -> 0x0113 }
        if (r9 >= 0) goto L_0x0543;
    L_0x0533:
        r9 = r10.id;	 Catch:{ Exception -> 0x0113 }
        r8 = r8.id;	 Catch:{ Exception -> 0x0113 }
        if (r9 >= r8) goto L_0x0543;
    L_0x0539:
        r8 = r10.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r12.put(r8, r10);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0543;
    L_0x053f:
        r2 = r8;
        r5 = r9;
        r12 = r26;
    L_0x0543:
        r8 = r30;
        r9 = 1;
        r8.bindLong(r9, r13);	 Catch:{ Exception -> 0x0113 }
        r15 = r2;
        r9 = r3;
        r2 = r10.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r26 = r15;
        r15 = 2;
        r8.bindLong(r15, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = org.telegram.messenger.MessageObject.getUnreadFlags(r10);	 Catch:{ Exception -> 0x0113 }
        r3 = 3;
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.send_state;	 Catch:{ Exception -> 0x0113 }
        r3 = 4;
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.date;	 Catch:{ Exception -> 0x0113 }
        r3 = 5;
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = 6;
        r8.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x0113 }
        r2 = 7;
        r3 = org.telegram.messenger.MessageObject.isOut(r10);	 Catch:{ Exception -> 0x0113 }
        if (r3 != 0) goto L_0x0579;
    L_0x0572:
        r3 = r10.from_scheduled;	 Catch:{ Exception -> 0x0113 }
        if (r3 == 0) goto L_0x0577;
    L_0x0576:
        goto L_0x0579;
    L_0x0577:
        r3 = 0;
        goto L_0x057a;
    L_0x0579:
        r3 = 1;
    L_0x057a:
        r8.bindInteger(r2, r3);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.ttl;	 Catch:{ Exception -> 0x0113 }
        r3 = 8;
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.flags;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x0592;
    L_0x058a:
        r2 = r10.views;	 Catch:{ Exception -> 0x0113 }
        r3 = 9;
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        goto L_0x059b;
    L_0x0592:
        r3 = 9;
        r2 = r1.getMessageMediaType(r10);	 Catch:{ Exception -> 0x0113 }
        r8.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
    L_0x059b:
        r2 = 10;
        r3 = 0;
        r8.bindInteger(r2, r3);	 Catch:{ Exception -> 0x0113 }
        r2 = 11;
        r3 = r10.mentioned;	 Catch:{ Exception -> 0x0113 }
        if (r3 == 0) goto L_0x05a9;
    L_0x05a7:
        r3 = 1;
        goto L_0x05aa;
    L_0x05a9:
        r3 = 0;
    L_0x05aa:
        r8.bindInteger(r2, r3);	 Catch:{ Exception -> 0x0113 }
        r8.step();	 Catch:{ Exception -> 0x0113 }
        r2 = r10.random_id;	 Catch:{ Exception -> 0x0113 }
        r17 = 0;
        r15 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1));
        if (r15 == 0) goto L_0x05cd;
    L_0x05b8:
        r25.requery();	 Catch:{ Exception -> 0x0113 }
        r2 = r10.random_id;	 Catch:{ Exception -> 0x0113 }
        r15 = r25;
        r25 = r5;
        r5 = 1;
        r15.bindLong(r5, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = 2;
        r15.bindLong(r2, r13);	 Catch:{ Exception -> 0x0113 }
        r15.step();	 Catch:{ Exception -> 0x0113 }
        goto L_0x05d1;
    L_0x05cd:
        r15 = r25;
        r25 = r5;
    L_0x05d1:
        r2 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r10);	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x0603;
    L_0x05d7:
        if (r4 != 0) goto L_0x05e1;
    L_0x05d9:
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r4 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
    L_0x05e1:
        r4.requery();	 Catch:{ Exception -> 0x0113 }
        r2 = 1;
        r4.bindLong(r2, r13);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.dialog_id;	 Catch:{ Exception -> 0x0113 }
        r5 = 2;
        r4.bindLong(r5, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.date;	 Catch:{ Exception -> 0x0113 }
        r3 = 3;
        r4.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = org.telegram.messenger.MediaDataController.getMediaType(r10);	 Catch:{ Exception -> 0x0113 }
        r3 = 4;
        r4.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = 5;
        r4.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x0113 }
        r4.step();	 Catch:{ Exception -> 0x0113 }
    L_0x0603:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x062e;
    L_0x0609:
        if (r6 != 0) goto L_0x0613;
    L_0x060b:
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = "REPLACE INTO polls VALUES(?, ?)";
        r6 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
    L_0x0613:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2;	 Catch:{ Exception -> 0x0113 }
        r6.requery();	 Catch:{ Exception -> 0x0113 }
        r3 = 1;
        r6.bindLong(r3, r13);	 Catch:{ Exception -> 0x0113 }
        r2 = r2.poll;	 Catch:{ Exception -> 0x0113 }
        r2 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r5 = 2;
        r6.bindLong(r5, r2);	 Catch:{ Exception -> 0x0113 }
        r6.step();	 Catch:{ Exception -> 0x0113 }
    L_0x0629:
        r22 = r4;
        r5 = r24;
        goto L_0x064c;
    L_0x062e:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x0629;
    L_0x0634:
        r24.requery();	 Catch:{ Exception -> 0x0113 }
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2.webpage;	 Catch:{ Exception -> 0x0113 }
        r2 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r22 = r4;
        r5 = r24;
        r4 = 1;
        r5.bindLong(r4, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = 2;
        r5.bindLong(r2, r13);	 Catch:{ Exception -> 0x0113 }
        r5.step();	 Catch:{ Exception -> 0x0113 }
    L_0x064c:
        r11.reuse();	 Catch:{ Exception -> 0x0113 }
        if (r36 == 0) goto L_0x0796;
    L_0x0651:
        r2 = r10.to_id;	 Catch:{ Exception -> 0x0113 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x065b;
    L_0x0657:
        r2 = r10.post;	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x0796;
    L_0x065b:
        r2 = r10.date;	 Catch:{ Exception -> 0x0113 }
        r3 = r32.getConnectionsManager();	 Catch:{ Exception -> 0x0113 }
        r3 = r3.getCurrentTime();	 Catch:{ Exception -> 0x0113 }
        r3 = r3 + -3600;
        if (r2 < r3) goto L_0x0796;
    L_0x0669:
        r2 = r32.getDownloadController();	 Catch:{ Exception -> 0x0113 }
        r2 = r2.canDownloadMedia(r10);	 Catch:{ Exception -> 0x0113 }
        r3 = 1;
        if (r2 != r3) goto L_0x0796;
    L_0x0674:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0113 }
        if (r2 != 0) goto L_0x0686;
    L_0x067a:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0113 }
        if (r2 != 0) goto L_0x0686;
    L_0x0680:
        r2 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x0796;
    L_0x0686:
        r2 = org.telegram.messenger.MessageObject.getDocument(r10);	 Catch:{ Exception -> 0x0113 }
        r3 = org.telegram.messenger.MessageObject.getPhoto(r10);	 Catch:{ Exception -> 0x0113 }
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 == 0) goto L_0x06a6;
    L_0x0694:
        r3 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0113 }
        r11.<init>();	 Catch:{ Exception -> 0x0113 }
        r11.document = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = r11.flags;	 Catch:{ Exception -> 0x0113 }
        r13 = 1;
        r2 = r2 | r13;
        r11.flags = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = 2;
        goto L_0x072e;
    L_0x06a6:
        r4 = org.telegram.messenger.MessageObject.isStickerMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 != 0) goto L_0x071e;
    L_0x06ac:
        r4 = org.telegram.messenger.MessageObject.isAnimatedStickerMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 == 0) goto L_0x06b4;
    L_0x06b2:
        goto L_0x071e;
    L_0x06b4:
        r4 = org.telegram.messenger.MessageObject.isVideoMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 != 0) goto L_0x070d;
    L_0x06ba:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 != 0) goto L_0x070d;
    L_0x06c0:
        r4 = org.telegram.messenger.MessageObject.isGifMessage(r10);	 Catch:{ Exception -> 0x0113 }
        if (r4 == 0) goto L_0x06c7;
    L_0x06c6:
        goto L_0x070d;
    L_0x06c7:
        if (r2 == 0) goto L_0x06db;
    L_0x06c9:
        r3 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0113 }
        r11.<init>();	 Catch:{ Exception -> 0x0113 }
        r11.document = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = r11.flags;	 Catch:{ Exception -> 0x0113 }
        r13 = 1;
        r2 = r2 | r13;
        r11.flags = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = 8;
        goto L_0x072e;
    L_0x06db:
        if (r3 == 0) goto L_0x0708;
    L_0x06dd:
        r2 = r3.sizes;	 Catch:{ Exception -> 0x0113 }
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0113 }
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r4);	 Catch:{ Exception -> 0x0113 }
        if (r2 == 0) goto L_0x0708;
    L_0x06e9:
        r13 = r3.id;	 Catch:{ Exception -> 0x0113 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0113 }
        r2.<init>();	 Catch:{ Exception -> 0x0113 }
        r2.photo = r3;	 Catch:{ Exception -> 0x0113 }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0113 }
        r4 = 1;
        r3 = r3 | r4;
        r2.flags = r3;	 Catch:{ Exception -> 0x0113 }
        r3 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0113 }
        if (r3 == 0) goto L_0x0705;
    L_0x06fe:
        r3 = r2.flags;	 Catch:{ Exception -> 0x0113 }
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r3 = r3 | r4;
        r2.flags = r3;	 Catch:{ Exception -> 0x0113 }
    L_0x0705:
        r11 = r2;
        r3 = r13;
        goto L_0x072d;
    L_0x0708:
        r2 = 0;
        r3 = 0;
        r11 = 0;
        goto L_0x072e;
    L_0x070d:
        r3 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0113 }
        r11.<init>();	 Catch:{ Exception -> 0x0113 }
        r11.document = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = r11.flags;	 Catch:{ Exception -> 0x0113 }
        r13 = 1;
        r2 = r2 | r13;
        r11.flags = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = 4;
        goto L_0x072e;
    L_0x071e:
        r3 = r2.id;	 Catch:{ Exception -> 0x0113 }
        r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0113 }
        r11.<init>();	 Catch:{ Exception -> 0x0113 }
        r11.document = r2;	 Catch:{ Exception -> 0x0113 }
        r2 = r11.flags;	 Catch:{ Exception -> 0x0113 }
        r13 = 1;
        r2 = r2 | r13;
        r11.flags = r2;	 Catch:{ Exception -> 0x0113 }
    L_0x072d:
        r2 = 1;
    L_0x072e:
        if (r11 == 0) goto L_0x0796;
    L_0x0730:
        r13 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r13 = r13.ttl_seconds;	 Catch:{ Exception -> 0x0113 }
        if (r13 == 0) goto L_0x0742;
    L_0x0736:
        r13 = r10.media;	 Catch:{ Exception -> 0x0113 }
        r13 = r13.ttl_seconds;	 Catch:{ Exception -> 0x0113 }
        r11.ttl_seconds = r13;	 Catch:{ Exception -> 0x0113 }
        r13 = r11.flags;	 Catch:{ Exception -> 0x0113 }
        r14 = 4;
        r13 = r13 | r14;
        r11.flags = r13;	 Catch:{ Exception -> 0x0113 }
    L_0x0742:
        r7 = r7 | r2;
        r23.requery();	 Catch:{ Exception -> 0x0113 }
        r13 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0113 }
        r14 = r11.getObjectSize();	 Catch:{ Exception -> 0x0113 }
        r13.<init>(r14);	 Catch:{ Exception -> 0x0113 }
        r11.serializeToStream(r13);	 Catch:{ Exception -> 0x0113 }
        r11 = r23;
        r14 = 1;
        r11.bindLong(r14, r3);	 Catch:{ Exception -> 0x0113 }
        r3 = 2;
        r11.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = r10.date;	 Catch:{ Exception -> 0x0113 }
        r3 = 3;
        r11.bindInteger(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r2 = 4;
        r11.bindByteBuffer(r2, r13);	 Catch:{ Exception -> 0x0113 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0113 }
        r2.<init>();	 Catch:{ Exception -> 0x0113 }
        r3 = "sent_";
        r2.append(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = r10.to_id;	 Catch:{ Exception -> 0x0113 }
        if (r3 == 0) goto L_0x0779;
    L_0x0774:
        r3 = r10.to_id;	 Catch:{ Exception -> 0x0113 }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x0113 }
        goto L_0x077a;
    L_0x0779:
        r3 = 0;
    L_0x077a:
        r2.append(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = "_";
        r2.append(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = r10.id;	 Catch:{ Exception -> 0x0113 }
        r2.append(r3);	 Catch:{ Exception -> 0x0113 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0113 }
        r3 = 5;
        r11.bindString(r3, r2);	 Catch:{ Exception -> 0x0113 }
        r11.step();	 Catch:{ Exception -> 0x0113 }
        r13.reuse();	 Catch:{ Exception -> 0x0113 }
        goto L_0x0798;
    L_0x0796:
        r11 = r23;
    L_0x0798:
        r3 = r9 + 1;
        r2 = r33;
        r24 = r5;
        r30 = r8;
        r23 = r11;
        r4 = r22;
        r9 = r25;
        r8 = r26;
        r5 = r28;
        r26 = r12;
        r25 = r15;
        goto L_0x0491;
    L_0x07b0:
        r28 = r5;
        r11 = r23;
        r5 = r24;
        r15 = r25;
        r12 = r26;
        r26 = r8;
        r25 = r9;
        r8 = r30;
        r8.dispose();	 Catch:{ Exception -> 0x0113 }
        if (r4 == 0) goto L_0x07c8;
    L_0x07c5:
        r4.dispose();	 Catch:{ Exception -> 0x0113 }
    L_0x07c8:
        if (r6 == 0) goto L_0x07cd;
    L_0x07ca:
        r6.dispose();	 Catch:{ Exception -> 0x0113 }
    L_0x07cd:
        r15.dispose();	 Catch:{ Exception -> 0x0113 }
        r11.dispose();	 Catch:{ Exception -> 0x0113 }
        r5.dispose();	 Catch:{ Exception -> 0x0113 }
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r4 = "UPDATE dialogs SET date = ?, unread_count = ?, last_mid = ?, unread_count_i = ? WHERE did = ?";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0113 }
        r4 = 0;
    L_0x07e7:
        r5 = r12.size();	 Catch:{ Exception -> 0x0113 }
        if (r4 >= r5) goto L_0x0975;
    L_0x07ed:
        r5 = r12.keyAt(r4);	 Catch:{ Exception -> 0x0a54 }
        r8 = 0;
        r10 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r10 != 0) goto L_0x080b;
    L_0x07f7:
        r10 = r2;
        r14 = r4;
        r37 = r7;
        r23 = r12;
        r9 = r25;
        r7 = r26;
    L_0x0801:
        r1 = 5;
        r2 = 8;
        r4 = 6;
        r5 = 0;
        r11 = 9;
        goto L_0x0965;
    L_0x080b:
        r8 = r12.valueAt(r4);	 Catch:{ Exception -> 0x0a54 }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x0a54 }
        if (r8 == 0) goto L_0x0818;
    L_0x0813:
        r9 = r8.to_id;	 Catch:{ Exception -> 0x0113 }
        r13 = r9.channel_id;	 Catch:{ Exception -> 0x0113 }
        goto L_0x0819;
    L_0x0818:
        r13 = 0;
    L_0x0819:
        r9 = r1.database;	 Catch:{ Exception -> 0x0a54 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a54 }
        r10.<init>();	 Catch:{ Exception -> 0x0a54 }
        r11 = "SELECT date, unread_count, last_mid, unread_count_i FROM dialogs WHERE did = ";
        r10.append(r11);	 Catch:{ Exception -> 0x0a54 }
        r10.append(r5);	 Catch:{ Exception -> 0x0a54 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x0a54 }
        r11 = 0;
        r14 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0a54 }
        r9 = r9.queryFinalized(r10, r14);	 Catch:{ Exception -> 0x0a54 }
        r10 = r9.next();	 Catch:{ Exception -> 0x0a54 }
        if (r10 == 0) goto L_0x085e;
    L_0x0839:
        r14 = r9.intValue(r11);	 Catch:{ Exception -> 0x0113 }
        r23 = r12;
        r15 = 1;
        r12 = r9.intValue(r15);	 Catch:{ Exception -> 0x0113 }
        r12 = java.lang.Math.max(r11, r12);	 Catch:{ Exception -> 0x0113 }
        r15 = 2;
        r24 = r9.intValue(r15);	 Catch:{ Exception -> 0x0113 }
        r33 = r12;
        r15 = 3;
        r12 = r9.intValue(r15);	 Catch:{ Exception -> 0x0113 }
        r12 = java.lang.Math.max(r11, r12);	 Catch:{ Exception -> 0x0113 }
        r11 = r33;
        r15 = r14;
        r14 = r24;
        goto L_0x086d;
    L_0x085e:
        r23 = r12;
        if (r13 == 0) goto L_0x0869;
    L_0x0862:
        r11 = r32.getMessagesController();	 Catch:{ Exception -> 0x0113 }
        r11.checkChannelInviter(r13);	 Catch:{ Exception -> 0x0113 }
    L_0x0869:
        r11 = 0;
        r12 = 0;
        r14 = 0;
        r15 = 0;
    L_0x086d:
        r9.dispose();	 Catch:{ Exception -> 0x0a54 }
        r9 = r25;
        r24 = r9.get(r5);	 Catch:{ Exception -> 0x0a54 }
        r24 = (java.lang.Integer) r24;	 Catch:{ Exception -> 0x0a54 }
        r37 = r7;
        r7 = r26;
        r25 = r7.get(r5);	 Catch:{ Exception -> 0x0a54 }
        r25 = (java.lang.Integer) r25;	 Catch:{ Exception -> 0x0a54 }
        if (r25 != 0) goto L_0x088b;
    L_0x0884:
        r16 = 0;
        r25 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0898;
    L_0x088b:
        r26 = r25.intValue();	 Catch:{ Exception -> 0x0a54 }
        r26 = r26 + r11;
        r1 = java.lang.Integer.valueOf(r26);	 Catch:{ Exception -> 0x0a54 }
        r7.put(r5, r1);	 Catch:{ Exception -> 0x0a54 }
    L_0x0898:
        if (r24 != 0) goto L_0x08a0;
    L_0x089a:
        r1 = 0;
        r24 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0a54 }
        goto L_0x08ac;
    L_0x08a0:
        r1 = r24.intValue();	 Catch:{ Exception -> 0x0a54 }
        r1 = r1 + r12;
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0a54 }
        r9.put(r5, r1);	 Catch:{ Exception -> 0x0a54 }
    L_0x08ac:
        if (r8 == 0) goto L_0x08b4;
    L_0x08ae:
        r1 = r8.id;	 Catch:{ Exception -> 0x0a54 }
        r33 = r2;
        r1 = (long) r1;	 Catch:{ Exception -> 0x0a54 }
        goto L_0x08b7;
    L_0x08b4:
        r33 = r2;
        r1 = (long) r14;	 Catch:{ Exception -> 0x0a54 }
    L_0x08b7:
        if (r8 == 0) goto L_0x08c0;
    L_0x08b9:
        r14 = r8.local_id;	 Catch:{ Exception -> 0x0a54 }
        if (r14 == 0) goto L_0x08c0;
    L_0x08bd:
        r1 = r8.local_id;	 Catch:{ Exception -> 0x0a54 }
        r1 = (long) r1;	 Catch:{ Exception -> 0x0a54 }
    L_0x08c0:
        r14 = r4;
        r29 = r5;
        if (r13 == 0) goto L_0x08cb;
    L_0x08c5:
        r4 = (long) r13;	 Catch:{ Exception -> 0x0a54 }
        r6 = 32;
        r4 = r4 << r6;
        r1 = r1 | r4;
        goto L_0x08cd;
    L_0x08cb:
        r6 = 32;
    L_0x08cd:
        if (r10 == 0) goto L_0x0901;
    L_0x08cf:
        r3.requery();	 Catch:{ Exception -> 0x0a54 }
        if (r8 == 0) goto L_0x08da;
    L_0x08d4:
        if (r35 == 0) goto L_0x08d8;
    L_0x08d6:
        if (r15 != 0) goto L_0x08da;
    L_0x08d8:
        r15 = r8.date;	 Catch:{ Exception -> 0x0a54 }
    L_0x08da:
        r4 = 1;
        r3.bindInteger(r4, r15);	 Catch:{ Exception -> 0x0a54 }
        r4 = r25.intValue();	 Catch:{ Exception -> 0x0a54 }
        r11 = r11 + r4;
        r4 = 2;
        r3.bindInteger(r4, r11);	 Catch:{ Exception -> 0x0a54 }
        r4 = 3;
        r3.bindLong(r4, r1);	 Catch:{ Exception -> 0x0a54 }
        r1 = r24.intValue();	 Catch:{ Exception -> 0x0a54 }
        r12 = r12 + r1;
        r1 = 4;
        r3.bindInteger(r1, r12);	 Catch:{ Exception -> 0x0a54 }
        r4 = r29;
        r1 = 5;
        r3.bindLong(r1, r4);	 Catch:{ Exception -> 0x0a54 }
        r3.step();	 Catch:{ Exception -> 0x0a54 }
        r10 = r33;
        goto L_0x0801;
    L_0x0901:
        r4 = r29;
        r33.requery();	 Catch:{ Exception -> 0x0a54 }
        r10 = r33;
        r6 = 1;
        r10.bindLong(r6, r4);	 Catch:{ Exception -> 0x0a54 }
        if (r8 == 0) goto L_0x0914;
    L_0x090e:
        if (r35 == 0) goto L_0x0912;
    L_0x0910:
        if (r15 != 0) goto L_0x0914;
    L_0x0912:
        r15 = r8.date;	 Catch:{ Exception -> 0x0a54 }
    L_0x0914:
        r4 = 2;
        r10.bindInteger(r4, r15);	 Catch:{ Exception -> 0x0a54 }
        r4 = r25.intValue();	 Catch:{ Exception -> 0x0a54 }
        r11 = r11 + r4;
        r4 = 3;
        r10.bindInteger(r4, r11);	 Catch:{ Exception -> 0x0a54 }
        r4 = 4;
        r10.bindLong(r4, r1);	 Catch:{ Exception -> 0x0a54 }
        r1 = 5;
        r2 = 0;
        r10.bindInteger(r1, r2);	 Catch:{ Exception -> 0x0a54 }
        r4 = 6;
        r10.bindInteger(r4, r2);	 Catch:{ Exception -> 0x0a54 }
        r2 = 7;
        r5 = 0;
        r10.bindLong(r2, r5);	 Catch:{ Exception -> 0x0a54 }
        r2 = r24.intValue();	 Catch:{ Exception -> 0x0a54 }
        r12 = r12 + r2;
        r2 = 8;
        r10.bindInteger(r2, r12);	 Catch:{ Exception -> 0x0a54 }
        if (r13 == 0) goto L_0x0942;
    L_0x0940:
        r8 = 1;
        goto L_0x0943;
    L_0x0942:
        r8 = 0;
    L_0x0943:
        r11 = 9;
        r10.bindInteger(r11, r8);	 Catch:{ Exception -> 0x0a54 }
        r8 = 10;
        r12 = 0;
        r10.bindInteger(r8, r12);	 Catch:{ Exception -> 0x0a54 }
        r8 = 11;
        r10.bindInteger(r8, r12);	 Catch:{ Exception -> 0x0a54 }
        r8 = 12;
        r10.bindInteger(r8, r12);	 Catch:{ Exception -> 0x0a54 }
        r8 = 13;
        r10.bindInteger(r8, r12);	 Catch:{ Exception -> 0x0a54 }
        r8 = 14;
        r10.bindNull(r8);	 Catch:{ Exception -> 0x0a54 }
        r10.step();	 Catch:{ Exception -> 0x0a54 }
    L_0x0965:
        r8 = r14 + 1;
        r1 = r32;
        r26 = r7;
        r4 = r8;
        r25 = r9;
        r2 = r10;
        r12 = r23;
        r7 = r37;
        goto L_0x07e7;
    L_0x0975:
        r10 = r2;
        r37 = r7;
        r9 = r25;
        r7 = r26;
        r3.dispose();	 Catch:{ Exception -> 0x0a54 }
        r10.dispose();	 Catch:{ Exception -> 0x0a54 }
        if (r28 == 0) goto L_0x0a33;
    L_0x0984:
        r1 = r32;
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r3 = "REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x0113 }
        r3 = 0;
    L_0x098f:
        r4 = r28.size();	 Catch:{ Exception -> 0x0113 }
        if (r3 >= r4) goto L_0x0a2d;
    L_0x0995:
        r5 = r28;
        r4 = r5.keyAt(r3);	 Catch:{ Exception -> 0x0113 }
        r6 = r5.valueAt(r3);	 Catch:{ Exception -> 0x0113 }
        r6 = (android.util.LongSparseArray) r6;	 Catch:{ Exception -> 0x0113 }
        r8 = 0;
    L_0x09a2:
        r10 = r6.size();	 Catch:{ Exception -> 0x0113 }
        if (r8 >= r10) goto L_0x0a1d;
    L_0x09a8:
        r10 = r6.keyAt(r8);	 Catch:{ Exception -> 0x0113 }
        r12 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x0113 }
        r14 = "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r28 = r5;
        r15 = 2;
        r5 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0113 }
        r15 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x0113 }
        r27 = r7;
        r7 = 0;
        r5[r7] = r15;	 Catch:{ Exception -> 0x0113 }
        r15 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x0113 }
        r16 = 1;
        r5[r16] = r15;	 Catch:{ Exception -> 0x0113 }
        r5 = java.lang.String.format(r13, r14, r5);	 Catch:{ Exception -> 0x0113 }
        r13 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x0113 }
        r5 = r12.queryFinalized(r5, r13);	 Catch:{ Exception -> 0x0113 }
        r12 = r5.next();	 Catch:{ Exception -> 0x0113 }
        if (r12 == 0) goto L_0x09e2;
    L_0x09d8:
        r12 = r5.intValue(r7);	 Catch:{ Exception -> 0x0113 }
        r7 = 1;
        r13 = r5.intValue(r7);	 Catch:{ Exception -> 0x0113 }
        goto L_0x09e4;
    L_0x09e2:
        r12 = -1;
        r13 = 0;
    L_0x09e4:
        r5.dispose();	 Catch:{ Exception -> 0x0113 }
        r5 = -1;
        if (r12 == r5) goto L_0x0a11;
    L_0x09ea:
        r2.requery();	 Catch:{ Exception -> 0x0113 }
        r7 = r6.valueAt(r8);	 Catch:{ Exception -> 0x0113 }
        r7 = (java.lang.Integer) r7;	 Catch:{ Exception -> 0x0113 }
        r7 = r7.intValue();	 Catch:{ Exception -> 0x0113 }
        r12 = r12 + r7;
        r7 = 1;
        r2.bindLong(r7, r10);	 Catch:{ Exception -> 0x0113 }
        r10 = 2;
        r2.bindInteger(r10, r4);	 Catch:{ Exception -> 0x0113 }
        r11 = 0;
        r12 = java.lang.Math.max(r11, r12);	 Catch:{ Exception -> 0x0113 }
        r14 = 3;
        r2.bindInteger(r14, r12);	 Catch:{ Exception -> 0x0113 }
        r12 = 4;
        r2.bindInteger(r12, r13);	 Catch:{ Exception -> 0x0113 }
        r2.step();	 Catch:{ Exception -> 0x0113 }
        goto L_0x0a16;
    L_0x0a11:
        r7 = 1;
        r10 = 2;
        r11 = 0;
        r12 = 4;
        r14 = 3;
    L_0x0a16:
        r8 = r8 + 1;
        r7 = r27;
        r5 = r28;
        goto L_0x09a2;
    L_0x0a1d:
        r28 = r5;
        r27 = r7;
        r5 = -1;
        r7 = 1;
        r10 = 2;
        r11 = 0;
        r12 = 4;
        r14 = 3;
        r3 = r3 + 1;
        r7 = r27;
        goto L_0x098f;
    L_0x0a2d:
        r27 = r7;
        r2.dispose();	 Catch:{ Exception -> 0x0113 }
        goto L_0x0a37;
    L_0x0a33:
        r1 = r32;
        r27 = r7;
    L_0x0a37:
        if (r34 == 0) goto L_0x0a3e;
    L_0x0a39:
        r2 = r1.database;	 Catch:{ Exception -> 0x0113 }
        r2.commitTransaction();	 Catch:{ Exception -> 0x0113 }
    L_0x0a3e:
        r2 = r32.getMessagesController();	 Catch:{ Exception -> 0x0113 }
        r3 = r27;
        r2.processDialogsUpdateRead(r3, r9);	 Catch:{ Exception -> 0x0113 }
        if (r37 == 0) goto L_0x0a5c;
    L_0x0a49:
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$Bw9l9LNNVmXi_Z-kQjR6piu3DAs;	 Catch:{ Exception -> 0x0113 }
        r7 = r37;
        r2.<init>(r1, r7);	 Catch:{ Exception -> 0x0113 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0a5c;
    L_0x0a54:
        r0 = move-exception;
        r1 = r32;
        goto L_0x0114;
    L_0x0a59:
        org.telegram.messenger.FileLog.e(r2);
    L_0x0a5c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putMessagesInternal(java.util.ArrayList, boolean, boolean, int, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$putMessagesInternal$118$MessagesStorage(int i) {
        getDownloadController().newDownloadObjectsAvailable(i);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        putMessages(arrayList, z, z2, z3, i, false, z4);
    }

    public void putMessages(ArrayList<Message> arrayList, boolean z, boolean z2, boolean z3, int i, boolean z4, boolean z5) {
        if (arrayList.size() != 0) {
            if (z2) {
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$YFwsLfv6ccqp08M2WGcFgcOROdk(this, arrayList, z, z3, i, z4, z5));
            } else {
                putMessagesInternal(arrayList, z, z3, i, z4, z5);
            }
        }
    }

    public /* synthetic */ void lambda$putMessages$119$MessagesStorage(ArrayList arrayList, boolean z, boolean z2, int i, boolean z3, boolean z4) {
        putMessagesInternal(arrayList, z, z2, i, z3, z4);
    }

    public void markMessageAsSendError(Message message, boolean z) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$iv61JbFyQ2jGaIur6_FKhwhb9HE(this, message, z));
    }

    public /* synthetic */ void lambda$markMessageAsSendError$120$MessagesStorage(Message message, boolean z) {
        try {
            long j = (long) message.id;
            if (message.to_id.channel_id != 0) {
                j |= ((long) message.to_id.channel_id) << 32;
            }
            SQLiteDatabase sQLiteDatabase;
            StringBuilder stringBuilder;
            if (z) {
                sQLiteDatabase = this.database;
                stringBuilder = new StringBuilder();
                stringBuilder.append("UPDATE scheduled_messages SET send_state = 2 WHERE mid = ");
                stringBuilder.append(j);
                sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
                return;
            }
            sQLiteDatabase = this.database;
            stringBuilder = new StringBuilder();
            stringBuilder.append("UPDATE messages SET send_state = 2 WHERE mid = ");
            stringBuilder.append(j);
            sQLiteDatabase.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setMessageSeq(int i, int i2, int i3) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$tfio61Jg7EwU_lYu8vz8qjAm11M(this, i, i2, i3));
    }

    public /* synthetic */ void lambda$setMessageSeq$121$MessagesStorage(int i, int i2, int i3) {
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

    /* JADX WARNING: Removed duplicated region for block: B:88:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0118  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:119:0x0177 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:162:0x0253 */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0051 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0118  */
    /* JADX WARNING: Missing exception handler attribute for start block: B:131:0x01dc */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0051 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0059  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:96:0x013e, code skipped:
            if (r3 != null) goto L_0x0149;
     */
    /* JADX WARNING: Missing block: B:101:0x0147, code skipped:
            if (r3 == null) goto L_0x014c;
     */
    /* JADX WARNING: Missing block: B:102:0x0149, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:104:0x0154, code skipped:
            return new long[]{r7, r5};
     */
    /* JADX WARNING: Missing block: B:114:0x016f, code skipped:
            if (r2 != null) goto L_0x01c1;
     */
    /* JADX WARNING: Missing block: B:120:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r12)})).stepThis().dispose();
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r12)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:121:0x01b8, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:122:0x01bb, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:124:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:125:0x01bf, code skipped:
            if (r2 == null) goto L_0x01c5;
     */
    /* JADX WARNING: Missing block: B:126:0x01c1, code skipped:
            r2.dispose();
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:128:?, code skipped:
            r2 = r1.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
            r2.bindLong(1, r14);
            r2.bindLong(2, r12);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:129:0x01d7, code skipped:
            if (r2 == null) goto L_0x0207;
     */
    /* JADX WARNING: Missing block: B:130:0x01da, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:132:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r12)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:133:0x01fd, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:135:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:136:0x0201, code skipped:
            if (r2 == null) goto L_0x0207;
     */
    /* JADX WARNING: Missing block: B:137:0x0203, code skipped:
            r2.dispose();
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:139:?, code skipped:
            r2 = r1.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
            r2.bindLong(1, r14);
            r2.bindLong(2, r12);
            r2.step();
     */
    /* JADX WARNING: Missing block: B:140:0x0219, code skipped:
            if (r2 == null) goto L_0x0283;
     */
    /* JADX WARNING: Missing block: B:141:0x021c, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:142:0x021e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:144:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:145:0x0222, code skipped:
            if (r2 == null) goto L_0x0283;
     */
    /* JADX WARNING: Missing block: B:146:0x0224, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:147:0x0228, code skipped:
            if (r2 != null) goto L_0x022a;
     */
    /* JADX WARNING: Missing block: B:148:0x022a, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:149:0x022d, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:150:0x022e, code skipped:
            if (r2 != null) goto L_0x0230;
     */
    /* JADX WARNING: Missing block: B:151:0x0230, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:152:0x0233, code skipped:
            throw r0;
     */
    /* JADX WARNING: Missing block: B:154:0x0236, code skipped:
            r2.dispose();
     */
    /* JADX WARNING: Missing block: B:160:0x024e, code skipped:
            if (r3 != null) goto L_0x0279;
     */
    /* JADX WARNING: Missing block: B:161:0x0251, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:163:?, code skipped:
            r1.database.executeFast(java.lang.String.format(java.util.Locale.US, "DELETE FROM scheduled_messages WHERE mid = %d", new java.lang.Object[]{java.lang.Long.valueOf(r12)})).stepThis().dispose();
     */
    /* JADX WARNING: Missing block: B:164:0x0273, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:166:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:167:0x0277, code skipped:
            if (r3 == null) goto L_0x0283;
     */
    /* JADX WARNING: Missing block: B:168:0x0279, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:169:0x027d, code skipped:
            if (r3 != null) goto L_0x027f;
     */
    /* JADX WARNING: Missing block: B:170:0x027f, code skipped:
            r3.dispose();
     */
    /* JADX WARNING: Missing block: B:171:0x0282, code skipped:
            throw r0;
     */
    private long[] updateMessageStateAndIdInternal(long r20, java.lang.Integer r22, int r23, int r24, int r25, int r26) {
        /*
        r19 = this;
        r1 = r19;
        r2 = r24;
        r3 = r25;
        r0 = r23;
        r4 = r26;
        r5 = (long) r0;
        r7 = 0;
        r8 = 0;
        r9 = 1;
        if (r22 != 0) goto L_0x005d;
    L_0x0010:
        r0 = r1.database;	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r10 = java.util.Locale.US;	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r11 = "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1";
        r12 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r13 = java.lang.Long.valueOf(r20);	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r12[r8] = r13;	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r10 = java.lang.String.format(r10, r11, r12);	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r11 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r10 = r0.queryFinalized(r10, r11);	 Catch:{ Exception -> 0x0043, all -> 0x0041 }
        r0 = r10.next();	 Catch:{ Exception -> 0x003f }
        if (r0 == 0) goto L_0x0037;
    L_0x002e:
        r0 = r10.intValue(r8);	 Catch:{ Exception -> 0x003f }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x003f }
        goto L_0x0039;
    L_0x0037:
        r0 = r22;
    L_0x0039:
        if (r10 == 0) goto L_0x004f;
    L_0x003b:
        r10.dispose();
        goto L_0x004f;
    L_0x003f:
        r0 = move-exception;
        goto L_0x0045;
    L_0x0041:
        r0 = move-exception;
        goto L_0x0057;
    L_0x0043:
        r0 = move-exception;
        r10 = r7;
    L_0x0045:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0055 }
        if (r10 == 0) goto L_0x004d;
    L_0x004a:
        r10.dispose();
    L_0x004d:
        r0 = r22;
    L_0x004f:
        if (r0 != 0) goto L_0x0052;
    L_0x0051:
        return r7;
    L_0x0052:
        r11 = r10;
        r10 = r0;
        goto L_0x0060;
    L_0x0055:
        r0 = move-exception;
        r7 = r10;
    L_0x0057:
        if (r7 == 0) goto L_0x005c;
    L_0x0059:
        r7.dispose();
    L_0x005c:
        throw r0;
    L_0x005d:
        r10 = r22;
        r11 = r7;
    L_0x0060:
        r0 = r10.intValue();
        r12 = (long) r0;
        if (r3 == 0) goto L_0x006e;
    L_0x0067:
        r14 = (long) r3;
        r0 = 32;
        r14 = r14 << r0;
        r12 = r12 | r14;
        r14 = r14 | r5;
        goto L_0x006f;
    L_0x006e:
        r14 = r5;
    L_0x006f:
        r3 = -1;
        r16 = 0;
        if (r4 == r3) goto L_0x007b;
    L_0x0074:
        if (r4 != 0) goto L_0x0077;
    L_0x0076:
        goto L_0x007b;
    L_0x0077:
        r9 = r4;
        r7 = r16;
        goto L_0x00ba;
    L_0x007b:
        r0 = r1.database;	 Catch:{ Exception -> 0x00ad }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x00ad }
        r3 = "SELECT uid FROM messages WHERE mid = %d LIMIT 1";
        r8 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x00ad }
        r18 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x00ad }
        r9 = 0;
        r8[r9] = r18;	 Catch:{ Exception -> 0x00ad }
        r3 = java.lang.String.format(r7, r3, r8);	 Catch:{ Exception -> 0x00ad }
        r7 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x00ad }
        r11 = r0.queryFinalized(r3, r7);	 Catch:{ Exception -> 0x00ad }
        r0 = r11.next();	 Catch:{ Exception -> 0x00ad }
        if (r0 == 0) goto L_0x00a1;
    L_0x009a:
        r3 = r11.longValue(r9);	 Catch:{ Exception -> 0x00ad }
        r7 = r3;
        r4 = 0;
        goto L_0x00a3;
    L_0x00a1:
        r7 = r16;
    L_0x00a3:
        if (r11 == 0) goto L_0x00a8;
    L_0x00a5:
        r11.dispose();
    L_0x00a8:
        r9 = r4;
        goto L_0x00b9;
    L_0x00aa:
        r0 = move-exception;
        goto L_0x0297;
    L_0x00ad:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00aa }
        if (r11 == 0) goto L_0x00b6;
    L_0x00b3:
        r11.dispose();
    L_0x00b6:
        r9 = r4;
        r7 = r16;
    L_0x00b9:
        r3 = -1;
    L_0x00ba:
        if (r9 == r3) goto L_0x00c5;
    L_0x00bc:
        r3 = 1;
        if (r9 != r3) goto L_0x00c0;
    L_0x00bf:
        goto L_0x00c6;
    L_0x00c0:
        r20 = r7;
        r18 = r9;
        goto L_0x010e;
    L_0x00c5:
        r3 = 1;
    L_0x00c6:
        r0 = r1.database;	 Catch:{ Exception -> 0x0101 }
        r4 = java.util.Locale.US;	 Catch:{ Exception -> 0x0101 }
        r20 = r7;
        r7 = "SELECT uid FROM scheduled_messages WHERE mid = %d LIMIT 1";
        r8 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x00fc }
        r3 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x00fc }
        r18 = r9;
        r9 = 0;
        r8[r9] = r3;	 Catch:{ Exception -> 0x00fa }
        r3 = java.lang.String.format(r4, r7, r8);	 Catch:{ Exception -> 0x00fa }
        r4 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x00fa }
        r11 = r0.queryFinalized(r3, r4);	 Catch:{ Exception -> 0x00fa }
        r0 = r11.next();	 Catch:{ Exception -> 0x00fa }
        if (r0 == 0) goto L_0x00f0;
    L_0x00e9:
        r3 = r11.longValue(r9);	 Catch:{ Exception -> 0x00fa }
        r7 = r3;
        r9 = 1;
        goto L_0x00f4;
    L_0x00f0:
        r7 = r20;
        r9 = r18;
    L_0x00f4:
        if (r11 == 0) goto L_0x0112;
    L_0x00f6:
        r11.dispose();
        goto L_0x0112;
    L_0x00fa:
        r0 = move-exception;
        goto L_0x0106;
    L_0x00fc:
        r0 = move-exception;
        goto L_0x0104;
    L_0x00fe:
        r0 = move-exception;
        goto L_0x0291;
    L_0x0101:
        r0 = move-exception;
        r20 = r7;
    L_0x0104:
        r18 = r9;
    L_0x0106:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x00fe }
        if (r11 == 0) goto L_0x010e;
    L_0x010b:
        r11.dispose();
    L_0x010e:
        r7 = r20;
        r9 = r18;
    L_0x0112:
        r0 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r0 != 0) goto L_0x0118;
    L_0x0116:
        r3 = 0;
        return r3;
    L_0x0118:
        r3 = 0;
        r4 = 2;
        r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r0 != 0) goto L_0x015b;
    L_0x011e:
        if (r2 == 0) goto L_0x015b;
    L_0x0120:
        if (r9 != 0) goto L_0x012b;
    L_0x0122:
        r0 = r1.database;	 Catch:{ Exception -> 0x0143 }
        r9 = "UPDATE messages SET send_state = 0, date = ? WHERE mid = ?";
        r0 = r0.executeFast(r9);	 Catch:{ Exception -> 0x0143 }
        goto L_0x0133;
    L_0x012b:
        r0 = r1.database;	 Catch:{ Exception -> 0x0143 }
        r9 = "UPDATE scheduled_messages SET send_state = 0, date = ? WHERE mid = ?";
        r0 = r0.executeFast(r9);	 Catch:{ Exception -> 0x0143 }
    L_0x0133:
        r3 = r0;
        r9 = 1;
        r3.bindInteger(r9, r2);	 Catch:{ Exception -> 0x0143 }
        r3.bindLong(r4, r14);	 Catch:{ Exception -> 0x0143 }
        r3.step();	 Catch:{ Exception -> 0x0143 }
        if (r3 == 0) goto L_0x014c;
    L_0x0140:
        goto L_0x0149;
    L_0x0141:
        r0 = move-exception;
        goto L_0x0155;
    L_0x0143:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0141 }
        if (r3 == 0) goto L_0x014c;
    L_0x0149:
        r3.dispose();
    L_0x014c:
        r0 = new long[r4];
        r2 = 0;
        r0[r2] = r7;
        r2 = 1;
        r0[r2] = r5;
        return r0;
    L_0x0155:
        if (r3 == 0) goto L_0x015a;
    L_0x0157:
        r3.dispose();
    L_0x015a:
        throw r0;
    L_0x015b:
        if (r9 != 0) goto L_0x023a;
    L_0x015d:
        r0 = r1.database;	 Catch:{ Exception -> 0x0176, all -> 0x0172 }
        r2 = "UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?";
        r2 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0176, all -> 0x0172 }
        r5 = 1;
        r2.bindLong(r5, r14);	 Catch:{ Exception -> 0x0177 }
        r2.bindLong(r4, r12);	 Catch:{ Exception -> 0x0177 }
        r2.step();	 Catch:{ Exception -> 0x0177 }
        if (r2 == 0) goto L_0x01c5;
    L_0x0171:
        goto L_0x01c1;
    L_0x0172:
        r0 = move-exception;
        r2 = r3;
        goto L_0x0234;
    L_0x0176:
        r2 = r3;
    L_0x0177:
        r0 = r1.database;	 Catch:{ Exception -> 0x01bb }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x01bb }
        r6 = "DELETE FROM messages WHERE mid = %d";
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x01bb }
        r9 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x01bb }
        r16 = 0;
        r11[r16] = r9;	 Catch:{ Exception -> 0x01bb }
        r5 = java.lang.String.format(r5, r6, r11);	 Catch:{ Exception -> 0x01bb }
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x01bb }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x01bb }
        r0.dispose();	 Catch:{ Exception -> 0x01bb }
        r0 = r1.database;	 Catch:{ Exception -> 0x01bb }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x01bb }
        r6 = "DELETE FROM messages_seq WHERE mid = %d";
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x01bb }
        r9 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x01bb }
        r16 = 0;
        r11[r16] = r9;	 Catch:{ Exception -> 0x01bb }
        r5 = java.lang.String.format(r5, r6, r11);	 Catch:{ Exception -> 0x01bb }
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x01bb }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x01bb }
        r0.dispose();	 Catch:{ Exception -> 0x01bb }
        goto L_0x01bf;
    L_0x01b8:
        r0 = move-exception;
        goto L_0x0234;
    L_0x01bb:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01b8 }
    L_0x01bf:
        if (r2 == 0) goto L_0x01c5;
    L_0x01c1:
        r2.dispose();
        r2 = r3;
    L_0x01c5:
        r0 = r1.database;	 Catch:{ Exception -> 0x01dc }
        r5 = "UPDATE media_v2 SET mid = ? WHERE mid = ?";
        r2 = r0.executeFast(r5);	 Catch:{ Exception -> 0x01dc }
        r5 = 1;
        r2.bindLong(r5, r14);	 Catch:{ Exception -> 0x01dc }
        r2.bindLong(r4, r12);	 Catch:{ Exception -> 0x01dc }
        r2.step();	 Catch:{ Exception -> 0x01dc }
        if (r2 == 0) goto L_0x0207;
    L_0x01d9:
        goto L_0x0203;
    L_0x01da:
        r0 = move-exception;
        goto L_0x022e;
    L_0x01dc:
        r0 = r1.database;	 Catch:{ Exception -> 0x01fd }
        r5 = java.util.Locale.US;	 Catch:{ Exception -> 0x01fd }
        r6 = "DELETE FROM media_v2 WHERE mid = %d";
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x01fd }
        r9 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x01fd }
        r16 = 0;
        r11[r16] = r9;	 Catch:{ Exception -> 0x01fd }
        r5 = java.lang.String.format(r5, r6, r11);	 Catch:{ Exception -> 0x01fd }
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x01fd }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x01fd }
        r0.dispose();	 Catch:{ Exception -> 0x01fd }
        goto L_0x0201;
    L_0x01fd:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x01da }
    L_0x0201:
        if (r2 == 0) goto L_0x0207;
    L_0x0203:
        r2.dispose();
        r2 = r3;
    L_0x0207:
        r0 = r1.database;	 Catch:{ Exception -> 0x021e }
        r3 = "UPDATE dialogs SET last_mid = ? WHERE last_mid = ?";
        r2 = r0.executeFast(r3);	 Catch:{ Exception -> 0x021e }
        r3 = 1;
        r2.bindLong(r3, r14);	 Catch:{ Exception -> 0x021e }
        r2.bindLong(r4, r12);	 Catch:{ Exception -> 0x021e }
        r2.step();	 Catch:{ Exception -> 0x021e }
        if (r2 == 0) goto L_0x0283;
    L_0x021b:
        goto L_0x0224;
    L_0x021c:
        r0 = move-exception;
        goto L_0x0228;
    L_0x021e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x021c }
        if (r2 == 0) goto L_0x0283;
    L_0x0224:
        r2.dispose();
        goto L_0x0283;
    L_0x0228:
        if (r2 == 0) goto L_0x022d;
    L_0x022a:
        r2.dispose();
    L_0x022d:
        throw r0;
    L_0x022e:
        if (r2 == 0) goto L_0x0233;
    L_0x0230:
        r2.dispose();
    L_0x0233:
        throw r0;
    L_0x0234:
        if (r2 == 0) goto L_0x0239;
    L_0x0236:
        r2.dispose();
    L_0x0239:
        throw r0;
    L_0x023a:
        r2 = 1;
        if (r9 != r2) goto L_0x0283;
    L_0x023d:
        r0 = r1.database;	 Catch:{ Exception -> 0x0253 }
        r5 = "UPDATE scheduled_messages SET mid = ?, send_state = 0 WHERE mid = ?";
        r3 = r0.executeFast(r5);	 Catch:{ Exception -> 0x0253 }
        r3.bindLong(r2, r14);	 Catch:{ Exception -> 0x0253 }
        r3.bindLong(r4, r12);	 Catch:{ Exception -> 0x0253 }
        r3.step();	 Catch:{ Exception -> 0x0253 }
        if (r3 == 0) goto L_0x0283;
    L_0x0250:
        goto L_0x0279;
    L_0x0251:
        r0 = move-exception;
        goto L_0x027d;
    L_0x0253:
        r0 = r1.database;	 Catch:{ Exception -> 0x0273 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0273 }
        r5 = "DELETE FROM scheduled_messages WHERE mid = %d";
        r6 = 1;
        r9 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0273 }
        r6 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0273 }
        r11 = 0;
        r9[r11] = r6;	 Catch:{ Exception -> 0x0273 }
        r2 = java.lang.String.format(r2, r5, r9);	 Catch:{ Exception -> 0x0273 }
        r0 = r0.executeFast(r2);	 Catch:{ Exception -> 0x0273 }
        r0 = r0.stepThis();	 Catch:{ Exception -> 0x0273 }
        r0.dispose();	 Catch:{ Exception -> 0x0273 }
        goto L_0x0277;
    L_0x0273:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0251 }
    L_0x0277:
        if (r3 == 0) goto L_0x0283;
    L_0x0279:
        r3.dispose();
        goto L_0x0283;
    L_0x027d:
        if (r3 == 0) goto L_0x0282;
    L_0x027f:
        r3.dispose();
    L_0x0282:
        throw r0;
    L_0x0283:
        r0 = new long[r4];
        r2 = 0;
        r0[r2] = r7;
        r2 = r10.intValue();
        r2 = (long) r2;
        r4 = 1;
        r0[r4] = r2;
        return r0;
    L_0x0291:
        if (r11 == 0) goto L_0x0296;
    L_0x0293:
        r11.dispose();
    L_0x0296:
        throw r0;
    L_0x0297:
        if (r11 == 0) goto L_0x029c;
    L_0x0299:
        r11.dispose();
    L_0x029c:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.updateMessageStateAndIdInternal(long, java.lang.Integer, int, int, int, int):long[]");
    }

    public /* synthetic */ void lambda$updateMessageStateAndId$122$MessagesStorage(long j, Integer num, int i, int i2, int i3, int i4) {
        updateMessageStateAndIdInternal(j, num, i, i2, i3, i4);
    }

    public long[] updateMessageStateAndId(long j, Integer num, int i, int i2, boolean z, int i3, int i4) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$8trnP44k-Zj6kUr2bLUzz-qMuHc(this, j, num, i, i2, i3, i4));
            return null;
        }
        return updateMessageStateAndIdInternal(j, num, i, i2, i3, i4);
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
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$f8uWvtGd5y8flPqv-Ei_D6-ZZiM(this, arrayList, z, z2));
            } else {
                updateUsersInternal(arrayList, z, z2);
            }
        }
    }

    public /* synthetic */ void lambda$updateUsers$123$MessagesStorage(ArrayList arrayList, boolean z, boolean z2) {
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$KmlxaGLCxdF9itv8SdAamxQs34s(this, arrayList, i));
        }
    }

    public /* synthetic */ void lambda$markMessagesContentAsRead$124$MessagesStorage(ArrayList arrayList, int i) {
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

    public /* synthetic */ void lambda$markMessagesAsRead$125$MessagesStorage(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray) {
        markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
    }

    public void markMessagesAsRead(SparseLongArray sparseLongArray, SparseLongArray sparseLongArray2, SparseIntArray sparseIntArray, boolean z) {
        if (z) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$KkoZhkq6PjNpGY0bOzZH8M7tawE(this, sparseLongArray, sparseLongArray2, sparseIntArray));
        } else {
            markMessagesAsReadInternal(sparseLongArray, sparseLongArray2, sparseIntArray);
        }
    }

    public void markMessagesAsDeletedByRandoms(ArrayList<Long> arrayList) {
        if (!arrayList.isEmpty()) {
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$oZWR0RwulVJnhLveiz-L711j8Fg(this, arrayList));
        }
    }

    public /* synthetic */ void lambda$markMessagesAsDeletedByRandoms$127$MessagesStorage(ArrayList arrayList) {
        try {
            String join = TextUtils.join(",", arrayList);
            SQLiteCursor queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[]{join}), new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            while (queryFinalized.next()) {
                arrayList2.add(Integer.valueOf(queryFinalized.intValue(0)));
            }
            queryFinalized.dispose();
            if (!arrayList2.isEmpty()) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$rXHoyNwd2Kd96mOnp948GW2nXjY(this, arrayList2));
                updateDialogsWithReadMessagesInternal(arrayList2, null, null, null);
                markMessagesAsDeletedInternal(arrayList2, 0, true, false);
                updateDialogsWithDeletedMessagesInternal(arrayList2, null, 0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$126$MessagesStorage(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(0), Boolean.valueOf(false));
    }

    /* Access modifiers changed, original: protected */
    public void deletePushMessages(long j, ArrayList<Integer> arrayList) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM unread_push_messages WHERE uid = %d AND mid IN(%s)", new Object[]{Long.valueOf(j), TextUtils.join(",", arrayList)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastScheduledMessagesChange(Long l) {
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Object[] objArr = new Object[1];
            int i = 0;
            objArr[0] = l;
            SQLiteCursor queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM scheduled_messages WHERE uid = %d", objArr), new Object[0]);
            if (queryFinalized.next()) {
                i = queryFinalized.intValue(0);
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MessagesStorage$Q9bV47vHWumXBIHs7iypkp_SqNw(this, l, i));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$broadcastScheduledMessagesChange$128$MessagesStorage(Long l, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.scheduledMessagesUpdated, l, Integer.valueOf(i));
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(ArrayList<Integer> arrayList, int i, boolean z, boolean z2) {
        ArrayList<Integer> arrayList2 = arrayList;
        int i2 = i;
        try {
            ArrayList<Long> arrayList3;
            ArrayList<Long> arrayList4 = new ArrayList();
            String str = ",";
            int i3 = 0;
            StringBuilder stringBuilder;
            String stringBuilder2;
            SQLiteCursor queryFinalized;
            long longValue;
            int size;
            if (z2) {
                if (i2 != 0) {
                    stringBuilder = new StringBuilder(arrayList.size());
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        long intValue = ((long) ((Integer) arrayList2.get(i4)).intValue()) | (((long) i2) << 32);
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(',');
                        }
                        stringBuilder.append(intValue);
                    }
                    stringBuilder2 = stringBuilder.toString();
                } else {
                    stringBuilder2 = TextUtils.join(str, arrayList2);
                }
                String str2 = stringBuilder2;
                ArrayList arrayList5 = new ArrayList();
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid FROM scheduled_messages WHERE mid IN(%s)", new Object[]{str2}), new Object[0]);
                while (queryFinalized.next()) {
                    try {
                        longValue = queryFinalized.longValue(0);
                        if (!arrayList5.contains(Long.valueOf(longValue))) {
                            arrayList5.add(Long.valueOf(longValue));
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                queryFinalized.dispose();
                this.database.executeFast(String.format(Locale.US, "DELETE FROM scheduled_messages WHERE mid IN(%s)", new Object[]{str2})).stepThis().dispose();
                size = arrayList5.size();
                while (i3 < size) {
                    broadcastScheduledMessagesChange((Long) arrayList5.get(i3));
                    i3++;
                }
                arrayList3 = arrayList4;
            } else {
                int i5;
                ArrayList<Long> arrayList6;
                ArrayList arrayList7 = new ArrayList(arrayList2);
                LongSparseArray longSparseArray = new LongSparseArray();
                if (i2 != 0) {
                    stringBuilder = new StringBuilder(arrayList.size());
                    i5 = 0;
                    while (i5 < arrayList.size()) {
                        arrayList6 = arrayList4;
                        long intValue2 = (((long) i2) << 32) | ((long) ((Integer) arrayList2.get(i5)).intValue());
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(',');
                        }
                        stringBuilder.append(intValue2);
                        i5++;
                        arrayList4 = arrayList6;
                    }
                    arrayList6 = arrayList4;
                    stringBuilder2 = stringBuilder.toString();
                } else {
                    arrayList6 = arrayList4;
                    stringBuilder2 = TextUtils.join(str, arrayList2);
                }
                String str3 = stringBuilder2;
                ArrayList arrayList8 = new ArrayList();
                size = getUserConfig().getClientUserId();
                queryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", new Object[]{str3}), new Object[0]);
                while (true) {
                    i5 = 2;
                    try {
                        if (!queryFinalized.next()) {
                            break;
                        }
                        long longValue2 = queryFinalized.longValue(i3);
                        arrayList7.remove(Integer.valueOf(queryFinalized.intValue(5)));
                        if (longValue2 != ((long) size)) {
                            int intValue3 = queryFinalized.intValue(2);
                            if (queryFinalized.intValue(3) == 0) {
                                Integer[] numArr = (Integer[]) longSparseArray.get(longValue2);
                                if (numArr == null) {
                                    numArr = new Integer[]{Integer.valueOf(0), Integer.valueOf(0)};
                                    longSparseArray.put(longValue2, numArr);
                                }
                                if (intValue3 < 2) {
                                    Integer num = numArr[1];
                                    numArr[1] = Integer.valueOf(numArr[1].intValue() + 1);
                                }
                                if (intValue3 == 0 || intValue3 == 2) {
                                    Integer num2 = numArr[0];
                                    numArr[0] = Integer.valueOf(numArr[0].intValue() + 1);
                                }
                            }
                        }
                        if (((int) longValue2) == 0 || z) {
                            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                            if (byteBufferValue != null) {
                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                                byteBufferValue.reuse();
                                addFilesToDelete(TLdeserialize, arrayList8, false);
                            }
                        }
                        i3 = 0;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                queryFinalized.dispose();
                getFileLoader().deleteFiles(arrayList8, 0);
                size = 0;
                while (size < longSparseArray.size()) {
                    int intValue4;
                    int intValue5;
                    long keyAt = longSparseArray.keyAt(size);
                    Integer[] numArr2 = (Integer[]) longSparseArray.valueAt(size);
                    SQLiteDatabase sQLiteDatabase = this.database;
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("SELECT unread_count, unread_count_i FROM dialogs WHERE did = ");
                    stringBuilder3.append(keyAt);
                    SQLiteCursor queryFinalized2 = sQLiteDatabase.queryFinalized(stringBuilder3.toString(), new Object[0]);
                    if (queryFinalized2.next()) {
                        intValue4 = queryFinalized2.intValue(0);
                        intValue5 = queryFinalized2.intValue(1);
                    } else {
                        intValue4 = 0;
                        intValue5 = 0;
                    }
                    queryFinalized2.dispose();
                    arrayList3 = arrayList6;
                    arrayList3.add(Long.valueOf(keyAt));
                    SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
                    executeFast.requery();
                    executeFast.bindInteger(1, Math.max(0, intValue4 - numArr2[0].intValue()));
                    executeFast.bindInteger(2, Math.max(0, intValue5 - numArr2[1].intValue()));
                    executeFast.bindLong(3, keyAt);
                    executeFast.step();
                    executeFast.dispose();
                    size++;
                    arrayList6 = arrayList3;
                }
                arrayList3 = arrayList6;
                this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[]{str3})).stepThis().dispose();
                this.database.executeFast(String.format(Locale.US, "DELETE FROM polls WHERE mid IN(%s)", new Object[]{str3})).stepThis().dispose();
                this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[]{str3})).stepThis().dispose();
                this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[]{str3})).stepThis().dispose();
                if (arrayList7.isEmpty()) {
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    Object[] objArr = new Object[1];
                    int i6 = 0;
                    objArr[0] = str3;
                    SQLiteCursor queryFinalized3 = sQLiteDatabase2.queryFinalized(String.format(Locale.US, "SELECT uid, type FROM media_v2 WHERE mid IN(%s)", objArr), new Object[0]);
                    SparseArray sparseArray = null;
                    while (queryFinalized3.next()) {
                        Integer valueOf;
                        long longValue3 = queryFinalized3.longValue(i6);
                        i6 = queryFinalized3.intValue(1);
                        if (sparseArray == null) {
                            sparseArray = new SparseArray();
                        }
                        LongSparseArray longSparseArray2 = (LongSparseArray) sparseArray.get(i6);
                        if (longSparseArray2 == null) {
                            longSparseArray2 = new LongSparseArray();
                            valueOf = Integer.valueOf(0);
                            sparseArray.put(i6, longSparseArray2);
                        } else {
                            valueOf = (Integer) longSparseArray2.get(longValue3);
                        }
                        if (valueOf == null) {
                            valueOf = Integer.valueOf(0);
                        }
                        longSparseArray2.put(longValue3, Integer.valueOf(valueOf.intValue() + 1));
                        i6 = 0;
                    }
                    queryFinalized3.dispose();
                    if (sparseArray != null) {
                        SQLitePreparedStatement executeFast2 = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
                        int i7 = 0;
                        while (i7 < sparseArray.size()) {
                            i6 = sparseArray.keyAt(i7);
                            LongSparseArray longSparseArray3 = (LongSparseArray) sparseArray.valueAt(i7);
                            i3 = 0;
                            while (i3 < longSparseArray3.size()) {
                                int intValue6;
                                longValue = longSparseArray3.keyAt(i3);
                                SQLiteDatabase sQLiteDatabase3 = this.database;
                                SparseArray sparseArray2 = sparseArray;
                                Object[] objArr2 = new Object[i5];
                                objArr2[0] = Long.valueOf(longValue);
                                objArr2[1] = Integer.valueOf(i6);
                                SQLiteCursor queryFinalized4 = sQLiteDatabase3.queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", objArr2), new Object[0]);
                                if (queryFinalized4.next()) {
                                    intValue6 = queryFinalized4.intValue(0);
                                    i5 = queryFinalized4.intValue(1);
                                } else {
                                    intValue6 = -1;
                                    i5 = 0;
                                }
                                queryFinalized4.dispose();
                                if (intValue6 != -1) {
                                    executeFast2.requery();
                                    intValue6 = Math.max(0, intValue6 - ((Integer) longSparseArray3.valueAt(i3)).intValue());
                                    executeFast2.bindLong(1, longValue);
                                    executeFast2.bindInteger(2, i6);
                                    executeFast2.bindInteger(3, intValue6);
                                    executeFast2.bindInteger(4, i5);
                                    executeFast2.step();
                                }
                                i3++;
                                sparseArray = sparseArray2;
                                i5 = 2;
                            }
                            i7++;
                            sparseArray = sparseArray;
                            i5 = 2;
                        }
                        executeFast2.dispose();
                    }
                } else if (i2 == 0) {
                    this.database.executeFast("UPDATE media_counts_v2 SET old = 1 WHERE 1").stepThis().dispose();
                } else {
                    this.database.executeFast(String.format(Locale.US, "UPDATE media_counts_v2 SET old = 1 WHERE uid = %d", new Object[]{Integer.valueOf(-i2)})).stepThis().dispose();
                }
                this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[]{str3})).stepThis().dispose();
                getMediaDataController().clearBotKeyboard(0, arrayList2);
            }
            return arrayList3;
        } catch (Exception e22) {
            FileLog.e(e22);
            return null;
        }
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
                int intValue;
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
                    intValue = queryFinalized2.intValue(8);
                    if (intValue != 0) {
                        tL_dialogFolder.last_message_date = intValue;
                    }
                    TLdeserialize.dialog_id = tL_dialogFolder.id;
                    tL_messages_dialogs.messages.add(TLdeserialize);
                    addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                }
                intValue = (int) tL_dialogFolder.id;
                int i6 = (int) (tL_dialogFolder.id >> 32);
                if (intValue != 0) {
                    if (intValue <= 0) {
                        i6 = -intValue;
                        if (!arrayList7.contains(Integer.valueOf(i6))) {
                            arrayList7.add(Integer.valueOf(i6));
                        }
                    } else if (!arrayList6.contains(Integer.valueOf(intValue))) {
                        arrayList6.add(Integer.valueOf(intValue));
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
                this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$iK8A-SAdCdfVOMqr20ENyrWEKkc(this, arrayList, arrayList2, i));
            } else {
                updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
            }
        }
    }

    public /* synthetic */ void lambda$updateDialogsWithDeletedMessages$129$MessagesStorage(ArrayList arrayList, ArrayList arrayList2, int i) {
        updateDialogsWithDeletedMessagesInternal(arrayList, arrayList2, i);
    }

    public ArrayList<Long> markMessagesAsDeleted(ArrayList<Integer> arrayList, boolean z, int i, boolean z2, boolean z3) {
        if (arrayList.isEmpty()) {
            return null;
        }
        if (!z) {
            return markMessagesAsDeletedInternal(arrayList, i, z2, z3);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$var_l5l4YmV9xGDKz8o9vxI8SwU(this, arrayList, i, z2, z3));
        return null;
    }

    public /* synthetic */ void lambda$markMessagesAsDeleted$130$MessagesStorage(ArrayList arrayList, int i, boolean z, boolean z2) {
        markMessagesAsDeletedInternal(arrayList, i, z, z2);
    }

    private ArrayList<Long> markMessagesAsDeletedInternal(int i, int i2, boolean z) {
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
                    }
                    if (((int) longValue) == 0 || z) {
                        NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            addFilesToDelete(TLdeserialize, arrayList2, false);
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

    public /* synthetic */ void lambda$markMessagesAsDeleted$131$MessagesStorage(int i, int i2, boolean z) {
        markMessagesAsDeletedInternal(i, i2, z);
    }

    public ArrayList<Long> markMessagesAsDeleted(int i, int i2, boolean z, boolean z2) {
        if (!z) {
            return markMessagesAsDeletedInternal(i, i2, z2);
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$h5YyXRaxJqni75pbOUW0YJmqvQg(this, i, i2, z2));
        return null;
    }

    private void fixUnsupportedMedia(Message message) {
        if (message != null) {
            MessageMedia messageMedia = message.media;
            if (messageMedia instanceof TL_messageMediaUnsupported_old) {
                if (messageMedia.bytes.length == 0) {
                    messageMedia.bytes = new byte[1];
                    messageMedia.bytes[0] = (byte) 109;
                }
            } else if (messageMedia instanceof TL_messageMediaUnsupported) {
                message.media = new TL_messageMediaUnsupported_old();
                messageMedia = message.media;
                messageMedia.bytes = new byte[1];
                messageMedia.bytes[0] = (byte) 109;
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
            this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$7WTSHIQPfcUMYxwWAQonwMJHQII(this, message, z, arrayList, arrayList2, i));
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
            if (org.telegram.messenger.MessageObject.isOut(r16) != false) goto L_0x00ad;
     */
    /* JADX WARNING: Missing block: B:27:0x00a8, code skipped:
            if (r4.from_scheduled == false) goto L_0x00ab;
     */
    /* JADX WARNING: Missing block: B:29:0x00ab, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:30:0x00ad, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:31:0x00ae, code skipped:
            r0.bindInteger(7, r14);
            r0.bindInteger(8, r4.ttl);
     */
    /* JADX WARNING: Missing block: B:32:0x00be, code skipped:
            if ((r4.flags & 1024) == 0) goto L_0x00c6;
     */
    /* JADX WARNING: Missing block: B:33:0x00c0, code skipped:
            r0.bindInteger(9, r4.views);
     */
    /* JADX WARNING: Missing block: B:34:0x00c6, code skipped:
            r0.bindInteger(9, getMessageMediaType(r16));
     */
    /* JADX WARNING: Missing block: B:35:0x00cd, code skipped:
            r0.bindInteger(10, 0);
     */
    /* JADX WARNING: Missing block: B:36:0x00d6, code skipped:
            if (r4.mentioned == false) goto L_0x00da;
     */
    /* JADX WARNING: Missing block: B:37:0x00d8, code skipped:
            r14 = 1;
     */
    /* JADX WARNING: Missing block: B:38:0x00da, code skipped:
            r14 = 0;
     */
    /* JADX WARNING: Missing block: B:39:0x00db, code skipped:
            r0.bindInteger(11, r14);
            r0.step();
     */
    /* JADX WARNING: Missing block: B:40:0x00e5, code skipped:
            if (org.telegram.messenger.MediaDataController.canAddMessageToMedia(r16) == false) goto L_0x0104;
     */
    /* JADX WARNING: Missing block: B:41:0x00e7, code skipped:
            r5.requery();
            r5.bindLong(1, r2);
            r5.bindLong(2, r4.dialog_id);
            r5.bindInteger(3, r4.date);
            r5.bindInteger(4, org.telegram.messenger.MediaDataController.getMediaType(r16));
            r5.bindByteBuffer(5, r8);
            r5.step();
     */
    /* JADX WARNING: Missing block: B:42:0x0104, code skipped:
            r8.reuse();
            r0.dispose();
            r5.dispose();
            r1.database.commitTransaction();
     */
    /* JADX WARNING: Missing block: B:43:0x0112, code skipped:
            if (r17 == false) goto L_0x017a;
     */
    /* JADX WARNING: Missing block: B:44:0x0114, code skipped:
            r5 = new java.util.HashMap();
            r0 = new java.util.HashMap();
            r2 = 0;
     */
    /* JADX WARNING: Missing block: B:46:0x0123, code skipped:
            if (r2 >= r18.size()) goto L_0x0139;
     */
    /* JADX WARNING: Missing block: B:47:0x0125, code skipped:
            r7 = (org.telegram.tgnet.TLRPC.User) r18.get(r2);
            r5.put(java.lang.Integer.valueOf(r7.id), r7);
            r2 = r2 + 1;
     */
    /* JADX WARNING: Missing block: B:49:0x013d, code skipped:
            if (r6 >= r19.size()) goto L_0x0153;
     */
    /* JADX WARNING: Missing block: B:50:0x013f, code skipped:
            r3 = (org.telegram.tgnet.TLRPC.Chat) r19.get(r6);
            r0.put(java.lang.Integer.valueOf(r3.id), r3);
            r6 = r6 + 1;
     */
    /* JADX WARNING: Missing block: B:51:0x0153, code skipped:
            r2 = new org.telegram.messenger.MessageObject(r20, r16, r5, r0, true);
            r0 = new java.util.ArrayList();
            r0.add(r2);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.-$$Lambda$MessagesStorage$Igy6jdP8JDjFz2_TGYETpcN3X6Q(r15, r2, r0));
     */
    public /* synthetic */ void lambda$replaceMessageIfExists$133$MessagesStorage(org.telegram.tgnet.TLRPC.Message r16, boolean r17, java.util.ArrayList r18, java.util.ArrayList r19, int r20) {
        /*
        r15 = this;
        r1 = r15;
        r4 = r16;
        r0 = r4.id;	 Catch:{ Exception -> 0x0176 }
        r2 = (long) r0;	 Catch:{ Exception -> 0x0176 }
        r0 = r4.to_id;	 Catch:{ Exception -> 0x0176 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0176 }
        r5 = r4.to_id;	 Catch:{ Exception -> 0x0176 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0176 }
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
        r5.dispose();	 Catch:{ Exception -> 0x0176 }
    L_0x003b:
        return;
    L_0x003c:
        if (r5 == 0) goto L_0x004c;
    L_0x003e:
        r5.dispose();	 Catch:{ Exception -> 0x0176 }
        goto L_0x004c;
    L_0x0042:
        r0 = move-exception;
        goto L_0x0170;
    L_0x0045:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0042 }
        if (r5 == 0) goto L_0x004c;
    L_0x004b:
        goto L_0x003e;
    L_0x004c:
        r0 = r1.database;	 Catch:{ Exception -> 0x0176 }
        r0.beginTransaction();	 Catch:{ Exception -> 0x0176 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0176 }
        r5 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r0 = r0.executeFast(r5);	 Catch:{ Exception -> 0x0176 }
        r5 = r1.database;	 Catch:{ Exception -> 0x0176 }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r5 = r5.executeFast(r8);	 Catch:{ Exception -> 0x0176 }
        r8 = r4.dialog_id;	 Catch:{ Exception -> 0x0176 }
        r10 = 0;
        r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x006c;
    L_0x0069:
        org.telegram.messenger.MessageObject.getDialogId(r16);	 Catch:{ Exception -> 0x0176 }
    L_0x006c:
        r15.fixUnsupportedMedia(r16);	 Catch:{ Exception -> 0x0176 }
        r0.requery();	 Catch:{ Exception -> 0x0176 }
        r8 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0176 }
        r9 = r16.getObjectSize();	 Catch:{ Exception -> 0x0176 }
        r8.<init>(r9);	 Catch:{ Exception -> 0x0176 }
        r4.serializeToStream(r8);	 Catch:{ Exception -> 0x0176 }
        r0.bindLong(r7, r2);	 Catch:{ Exception -> 0x0176 }
        r9 = r4.dialog_id;	 Catch:{ Exception -> 0x0176 }
        r11 = 2;
        r0.bindLong(r11, r9);	 Catch:{ Exception -> 0x0176 }
        r9 = org.telegram.messenger.MessageObject.getUnreadFlags(r16);	 Catch:{ Exception -> 0x0176 }
        r10 = 3;
        r0.bindInteger(r10, r9);	 Catch:{ Exception -> 0x0176 }
        r9 = r4.send_state;	 Catch:{ Exception -> 0x0176 }
        r12 = 4;
        r0.bindInteger(r12, r9);	 Catch:{ Exception -> 0x0176 }
        r9 = r4.date;	 Catch:{ Exception -> 0x0176 }
        r13 = 5;
        r0.bindInteger(r13, r9);	 Catch:{ Exception -> 0x0176 }
        r9 = 6;
        r0.bindByteBuffer(r9, r8);	 Catch:{ Exception -> 0x0176 }
        r9 = 7;
        r14 = org.telegram.messenger.MessageObject.isOut(r16);	 Catch:{ Exception -> 0x0176 }
        if (r14 != 0) goto L_0x00ad;
    L_0x00a6:
        r14 = r4.from_scheduled;	 Catch:{ Exception -> 0x0176 }
        if (r14 == 0) goto L_0x00ab;
    L_0x00aa:
        goto L_0x00ad;
    L_0x00ab:
        r14 = 0;
        goto L_0x00ae;
    L_0x00ad:
        r14 = 1;
    L_0x00ae:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0176 }
        r9 = 8;
        r14 = r4.ttl;	 Catch:{ Exception -> 0x0176 }
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0176 }
        r9 = r4.flags;	 Catch:{ Exception -> 0x0176 }
        r9 = r9 & 1024;
        r14 = 9;
        if (r9 == 0) goto L_0x00c6;
    L_0x00c0:
        r9 = r4.views;	 Catch:{ Exception -> 0x0176 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0176 }
        goto L_0x00cd;
    L_0x00c6:
        r9 = r15.getMessageMediaType(r16);	 Catch:{ Exception -> 0x0176 }
        r0.bindInteger(r14, r9);	 Catch:{ Exception -> 0x0176 }
    L_0x00cd:
        r9 = 10;
        r0.bindInteger(r9, r6);	 Catch:{ Exception -> 0x0176 }
        r9 = 11;
        r14 = r4.mentioned;	 Catch:{ Exception -> 0x0176 }
        if (r14 == 0) goto L_0x00da;
    L_0x00d8:
        r14 = 1;
        goto L_0x00db;
    L_0x00da:
        r14 = 0;
    L_0x00db:
        r0.bindInteger(r9, r14);	 Catch:{ Exception -> 0x0176 }
        r0.step();	 Catch:{ Exception -> 0x0176 }
        r9 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r16);	 Catch:{ Exception -> 0x0176 }
        if (r9 == 0) goto L_0x0104;
    L_0x00e7:
        r5.requery();	 Catch:{ Exception -> 0x0176 }
        r5.bindLong(r7, r2);	 Catch:{ Exception -> 0x0176 }
        r2 = r4.dialog_id;	 Catch:{ Exception -> 0x0176 }
        r5.bindLong(r11, r2);	 Catch:{ Exception -> 0x0176 }
        r2 = r4.date;	 Catch:{ Exception -> 0x0176 }
        r5.bindInteger(r10, r2);	 Catch:{ Exception -> 0x0176 }
        r2 = org.telegram.messenger.MediaDataController.getMediaType(r16);	 Catch:{ Exception -> 0x0176 }
        r5.bindInteger(r12, r2);	 Catch:{ Exception -> 0x0176 }
        r5.bindByteBuffer(r13, r8);	 Catch:{ Exception -> 0x0176 }
        r5.step();	 Catch:{ Exception -> 0x0176 }
    L_0x0104:
        r8.reuse();	 Catch:{ Exception -> 0x0176 }
        r0.dispose();	 Catch:{ Exception -> 0x0176 }
        r5.dispose();	 Catch:{ Exception -> 0x0176 }
        r0 = r1.database;	 Catch:{ Exception -> 0x0176 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0176 }
        if (r17 == 0) goto L_0x017a;
    L_0x0114:
        r5 = new java.util.HashMap;	 Catch:{ Exception -> 0x0176 }
        r5.<init>();	 Catch:{ Exception -> 0x0176 }
        r0 = new java.util.HashMap;	 Catch:{ Exception -> 0x0176 }
        r0.<init>();	 Catch:{ Exception -> 0x0176 }
        r2 = 0;
    L_0x011f:
        r3 = r18.size();	 Catch:{ Exception -> 0x0176 }
        if (r2 >= r3) goto L_0x0139;
    L_0x0125:
        r3 = r18;
        r7 = r3.get(r2);	 Catch:{ Exception -> 0x0176 }
        r7 = (org.telegram.tgnet.TLRPC.User) r7;	 Catch:{ Exception -> 0x0176 }
        r8 = r7.id;	 Catch:{ Exception -> 0x0176 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0176 }
        r5.put(r8, r7);	 Catch:{ Exception -> 0x0176 }
        r2 = r2 + 1;
        goto L_0x011f;
    L_0x0139:
        r2 = r19.size();	 Catch:{ Exception -> 0x0176 }
        if (r6 >= r2) goto L_0x0153;
    L_0x013f:
        r2 = r19;
        r3 = r2.get(r6);	 Catch:{ Exception -> 0x0176 }
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;	 Catch:{ Exception -> 0x0176 }
        r7 = r3.id;	 Catch:{ Exception -> 0x0176 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0176 }
        r0.put(r7, r3);	 Catch:{ Exception -> 0x0176 }
        r6 = r6 + 1;
        goto L_0x0139;
    L_0x0153:
        r8 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x0176 }
        r7 = 1;
        r2 = r8;
        r3 = r20;
        r4 = r16;
        r6 = r0;
        r2.<init>(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0176 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0176 }
        r0.<init>();	 Catch:{ Exception -> 0x0176 }
        r0.add(r8);	 Catch:{ Exception -> 0x0176 }
        r2 = new org.telegram.messenger.-$$Lambda$MessagesStorage$Igy6jdP8JDjFz2_TGYETpcN3X6Q;	 Catch:{ Exception -> 0x0176 }
        r2.<init>(r15, r8, r0);	 Catch:{ Exception -> 0x0176 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Exception -> 0x0176 }
        goto L_0x017a;
    L_0x0170:
        if (r5 == 0) goto L_0x0175;
    L_0x0172:
        r5.dispose();	 Catch:{ Exception -> 0x0176 }
    L_0x0175:
        throw r0;	 Catch:{ Exception -> 0x0176 }
    L_0x0176:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x017a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$replaceMessageIfExists$133$MessagesStorage(org.telegram.tgnet.TLRPC$Message, boolean, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public /* synthetic */ void lambda$null$132$MessagesStorage(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void putMessages(messages_Messages messages_messages, long j, int i, int i2, boolean z, boolean z2) {
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$8C3Kex6g8Avar_H71IxnDhbVs7iA(this, z2, j, messages_messages, i, i2, z));
    }

    /* JADX WARNING: Removed duplicated region for block: B:80:0x0260 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x03fe A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x03f6 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0415 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0413 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0445 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0422 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0478 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0453 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x018b A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x04dc A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x04e1 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x04e6 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0508 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x054a A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x018b A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x04dc A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x04e1 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x04e6 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0508 A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x054a A:{Catch:{ Exception -> 0x0555 }} */
    /* JADX WARNING: Missing block: B:65:0x022e, code skipped:
            if (r10.media.photo.id == r15.media.photo.id) goto L_0x0230;
     */
    public /* synthetic */ void lambda$putMessages$134$MessagesStorage(boolean r31, long r32, org.telegram.tgnet.TLRPC.messages_Messages r34, int r35, int r36, boolean r37) {
        /*
        r30 = this;
        r7 = r30;
        r8 = r32;
        r0 = r34;
        r10 = r35;
        r11 = r36;
        r12 = 32;
        r3 = 1;
        r4 = 0;
        if (r31 == 0) goto L_0x00ab;
    L_0x0010:
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0555 }
        r10 = "DELETE FROM scheduled_messages WHERE uid = %d AND mid > 0";
        r11 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x0555 }
        r16 = java.lang.Long.valueOf(r32);	 Catch:{ Exception -> 0x0555 }
        r11[r4] = r16;	 Catch:{ Exception -> 0x0555 }
        r2 = java.lang.String.format(r2, r10, r11);	 Catch:{ Exception -> 0x0555 }
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0555 }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x0555 }
        r1.dispose();	 Catch:{ Exception -> 0x0555 }
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r2 = "REPLACE INTO scheduled_messages VALUES(?, ?, ?, ?, ?, ?, NULL)";
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0555 }
        r2 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0555 }
        r10 = 0;
    L_0x003c:
        if (r4 >= r2) goto L_0x0090;
    L_0x003e:
        r11 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r11 = r11.get(r4);	 Catch:{ Exception -> 0x0555 }
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;	 Catch:{ Exception -> 0x0555 }
        r13 = r11.id;	 Catch:{ Exception -> 0x0555 }
        r14 = (long) r13;	 Catch:{ Exception -> 0x0555 }
        if (r10 != 0) goto L_0x004f;
    L_0x004b:
        r10 = r11.to_id;	 Catch:{ Exception -> 0x0555 }
        r10 = r10.channel_id;	 Catch:{ Exception -> 0x0555 }
    L_0x004f:
        r13 = r11.to_id;	 Catch:{ Exception -> 0x0555 }
        r13 = r13.channel_id;	 Catch:{ Exception -> 0x0555 }
        if (r13 == 0) goto L_0x0058;
    L_0x0055:
        r5 = (long) r10;	 Catch:{ Exception -> 0x0555 }
        r5 = r5 << r12;
        r14 = r14 | r5;
    L_0x0058:
        r7.fixUnsupportedMedia(r11);	 Catch:{ Exception -> 0x0555 }
        r1.requery();	 Catch:{ Exception -> 0x0555 }
        r5 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0555 }
        r6 = r11.getObjectSize();	 Catch:{ Exception -> 0x0555 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x0555 }
        r11.serializeToStream(r5);	 Catch:{ Exception -> 0x0555 }
        r1.bindLong(r3, r14);	 Catch:{ Exception -> 0x0555 }
        r6 = 2;
        r1.bindLong(r6, r8);	 Catch:{ Exception -> 0x0555 }
        r14 = r11.send_state;	 Catch:{ Exception -> 0x0555 }
        r13 = 3;
        r1.bindInteger(r13, r14);	 Catch:{ Exception -> 0x0555 }
        r14 = r11.date;	 Catch:{ Exception -> 0x0555 }
        r15 = 4;
        r1.bindInteger(r15, r14);	 Catch:{ Exception -> 0x0555 }
        r14 = 5;
        r1.bindByteBuffer(r14, r5);	 Catch:{ Exception -> 0x0555 }
        r11 = r11.ttl;	 Catch:{ Exception -> 0x0555 }
        r14 = 6;
        r1.bindInteger(r14, r11);	 Catch:{ Exception -> 0x0555 }
        r1.step();	 Catch:{ Exception -> 0x0555 }
        r5.reuse();	 Catch:{ Exception -> 0x0555 }
        r4 = r4 + 1;
        goto L_0x003c;
    L_0x0090:
        r1.dispose();	 Catch:{ Exception -> 0x0555 }
        r1 = r0.users;	 Catch:{ Exception -> 0x0555 }
        r7.putUsersInternal(r1);	 Catch:{ Exception -> 0x0555 }
        r0 = r0.chats;	 Catch:{ Exception -> 0x0555 }
        r7.putChatsInternal(r0);	 Catch:{ Exception -> 0x0555 }
        r0 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0555 }
        r0 = java.lang.Long.valueOf(r32);	 Catch:{ Exception -> 0x0555 }
        r7.broadcastScheduledMessagesChange(r0);	 Catch:{ Exception -> 0x0555 }
        goto L_0x0559;
    L_0x00ab:
        r6 = 2;
        r13 = 3;
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x0555 }
        if (r1 == 0) goto L_0x00c1;
    L_0x00b5:
        if (r10 != 0) goto L_0x00c0;
    L_0x00b7:
        r0 = "messages_holes";
        r7.doneHolesInTable(r0, r8, r11);	 Catch:{ Exception -> 0x0555 }
        r0 = -1;
        r7.doneHolesInMedia(r8, r11, r0);	 Catch:{ Exception -> 0x0555 }
    L_0x00c0:
        return;
    L_0x00c1:
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r1.beginTransaction();	 Catch:{ Exception -> 0x0555 }
        if (r10 != 0) goto L_0x00f5;
    L_0x00c8:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r2 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0555 }
        r2 = r2 - r3;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x0555 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0555 }
        r15 = r1.id;	 Catch:{ Exception -> 0x0555 }
        r2 = "messages_holes";
        r1 = r30;
        r5 = 1;
        r13 = 0;
        r3 = r32;
        r14 = 1;
        r5 = r15;
        r12 = 3;
        r6 = r36;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0555 }
        r6 = -1;
        r1 = r30;
        r2 = r32;
        r4 = r15;
        r5 = r36;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0555 }
        goto L_0x011b;
    L_0x00f5:
        r12 = 3;
        r13 = 0;
        r14 = 1;
        if (r10 != r14) goto L_0x011d;
    L_0x00fa:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r1 = r1.get(r13);	 Catch:{ Exception -> 0x0555 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0555 }
        r15 = r1.id;	 Catch:{ Exception -> 0x0555 }
        r2 = "messages_holes";
        r1 = r30;
        r3 = r32;
        r5 = r36;
        r6 = r15;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0555 }
        r6 = -1;
        r1 = r30;
        r2 = r32;
        r4 = r36;
        r5 = r15;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0555 }
    L_0x011b:
        r15 = 2;
        goto L_0x0164;
    L_0x011d:
        if (r10 == r12) goto L_0x0126;
    L_0x011f:
        r15 = 2;
        r1 = 4;
        if (r10 == r15) goto L_0x0128;
    L_0x0123:
        if (r10 != r1) goto L_0x0164;
    L_0x0125:
        goto L_0x0128;
    L_0x0126:
        r1 = 4;
        r15 = 2;
    L_0x0128:
        if (r11 != 0) goto L_0x0130;
    L_0x012a:
        if (r10 == r1) goto L_0x0130;
    L_0x012c:
        r11 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x013b;
    L_0x0130:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r1 = r1.get(r13);	 Catch:{ Exception -> 0x0555 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0555 }
        r1 = r1.id;	 Catch:{ Exception -> 0x0555 }
        r11 = r1;
    L_0x013b:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r2 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0555 }
        r2 = r2 - r14;
        r1 = r1.get(r2);	 Catch:{ Exception -> 0x0555 }
        r1 = (org.telegram.tgnet.TLRPC.Message) r1;	 Catch:{ Exception -> 0x0555 }
        r6 = r1.id;	 Catch:{ Exception -> 0x0555 }
        r2 = "messages_holes";
        r1 = r30;
        r3 = r32;
        r5 = r6;
        r17 = r6;
        r6 = r11;
        r1.closeHolesInTable(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0555 }
        r6 = -1;
        r1 = r30;
        r2 = r32;
        r4 = r17;
        r5 = r11;
        r1.closeHolesInMedia(r2, r4, r5, r6);	 Catch:{ Exception -> 0x0555 }
    L_0x0164:
        r1 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0555 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0555 }
        r2.<init>();	 Catch:{ Exception -> 0x0555 }
        r3 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r4 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r3 = r3.executeFast(r4);	 Catch:{ Exception -> 0x0555 }
        r4 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r5 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r4 = r4.executeFast(r5);	 Catch:{ Exception -> 0x0555 }
        r5 = 0;
        r6 = 0;
        r11 = 0;
        r17 = 0;
        r19 = 0;
        r20 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0189:
        if (r6 >= r1) goto L_0x04cd;
    L_0x018b:
        r15 = r0.messages;	 Catch:{ Exception -> 0x0555 }
        r15 = r15.get(r6);	 Catch:{ Exception -> 0x0555 }
        r15 = (org.telegram.tgnet.TLRPC.Message) r15;	 Catch:{ Exception -> 0x0555 }
        r12 = r15.id;	 Catch:{ Exception -> 0x0555 }
        r13 = (long) r12;	 Catch:{ Exception -> 0x0555 }
        if (r5 != 0) goto L_0x019c;
    L_0x0198:
        r5 = r15.to_id;	 Catch:{ Exception -> 0x0555 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0555 }
    L_0x019c:
        r12 = r15.to_id;	 Catch:{ Exception -> 0x0555 }
        r12 = r12.channel_id;	 Catch:{ Exception -> 0x0555 }
        r21 = r11;
        if (r12 == 0) goto L_0x01ab;
    L_0x01a4:
        r11 = (long) r5;	 Catch:{ Exception -> 0x0555 }
        r18 = 32;
        r11 = r11 << r18;
        r13 = r13 | r11;
        goto L_0x01ad;
    L_0x01ab:
        r18 = 32;
    L_0x01ad:
        r11 = -2;
        if (r10 != r11) goto L_0x02d7;
    L_0x01b0:
        r11 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x0555 }
        r22 = r1;
        r1 = "SELECT mid, data, ttl, mention, read_state, send_state FROM messages WHERE mid = %d";
        r23 = r5;
        r5 = 1;
        r10 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0555 }
        r5 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0555 }
        r24 = r4;
        r4 = 0;
        r10[r4] = r5;	 Catch:{ Exception -> 0x0555 }
        r1 = java.lang.String.format(r12, r1, r10);	 Catch:{ Exception -> 0x0555 }
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0555 }
        r1 = r11.queryFinalized(r1, r5);	 Catch:{ Exception -> 0x0555 }
        r5 = r1.next();	 Catch:{ Exception -> 0x0555 }
        if (r5 == 0) goto L_0x02c5;
    L_0x01d6:
        r10 = 1;
        r11 = r1.byteBufferValue(r10);	 Catch:{ Exception -> 0x0555 }
        if (r11 == 0) goto L_0x0265;
    L_0x01dd:
        r10 = r11.readInt32(r4);	 Catch:{ Exception -> 0x0555 }
        r10 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r11, r10, r4);	 Catch:{ Exception -> 0x0555 }
        r4 = r30.getUserConfig();	 Catch:{ Exception -> 0x0555 }
        r4 = r4.clientUserId;	 Catch:{ Exception -> 0x0555 }
        r10.readAttachPath(r11, r4);	 Catch:{ Exception -> 0x0555 }
        r11.reuse();	 Catch:{ Exception -> 0x0555 }
        r4 = 5;
        r11 = r1.intValue(r4);	 Catch:{ Exception -> 0x0555 }
        if (r10 == 0) goto L_0x0206;
    L_0x01f8:
        r4 = 3;
        if (r11 == r4) goto L_0x0206;
    L_0x01fb:
        r4 = r10.attachPath;	 Catch:{ Exception -> 0x0555 }
        r15.attachPath = r4;	 Catch:{ Exception -> 0x0555 }
        r4 = 2;
        r11 = r1.intValue(r4);	 Catch:{ Exception -> 0x0555 }
        r15.ttl = r11;	 Catch:{ Exception -> 0x0555 }
    L_0x0206:
        r4 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0232;
    L_0x020c:
        r4 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0232;
    L_0x0212:
        r4 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0232;
    L_0x0218:
        r4 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0232;
    L_0x021e:
        r4 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x0555 }
        r11 = r4.id;	 Catch:{ Exception -> 0x0555 }
        r4 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x0555 }
        r25 = r3;
        r3 = r4.id;	 Catch:{ Exception -> 0x0555 }
        r26 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1));
        if (r26 != 0) goto L_0x025d;
    L_0x0230:
        r4 = 1;
        goto L_0x025e;
    L_0x0232:
        r25 = r3;
        r3 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0555 }
        if (r3 == 0) goto L_0x025d;
    L_0x023a:
        r3 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;	 Catch:{ Exception -> 0x0555 }
        if (r3 == 0) goto L_0x025d;
    L_0x0240:
        r3 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0555 }
        if (r3 == 0) goto L_0x025d;
    L_0x0246:
        r3 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0555 }
        if (r3 == 0) goto L_0x025d;
    L_0x024c:
        r3 = r10.media;	 Catch:{ Exception -> 0x0555 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0555 }
        r3 = r3.id;	 Catch:{ Exception -> 0x0555 }
        r11 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r11 = r11.document;	 Catch:{ Exception -> 0x0555 }
        r11 = r11.id;	 Catch:{ Exception -> 0x0555 }
        r26 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1));
        if (r26 != 0) goto L_0x025d;
    L_0x025c:
        goto L_0x0230;
    L_0x025d:
        r4 = 0;
    L_0x025e:
        if (r4 != 0) goto L_0x0267;
    L_0x0260:
        r3 = 0;
        r7.addFilesToDelete(r10, r2, r3);	 Catch:{ Exception -> 0x0555 }
        goto L_0x0267;
    L_0x0265:
        r25 = r3;
    L_0x0267:
        r3 = 3;
        r4 = r1.intValue(r3);	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0270;
    L_0x026e:
        r3 = 1;
        goto L_0x0271;
    L_0x0270:
        r3 = 0;
    L_0x0271:
        r4 = 4;
        r10 = r1.intValue(r4);	 Catch:{ Exception -> 0x0555 }
        r4 = r15.mentioned;	 Catch:{ Exception -> 0x0555 }
        if (r3 == r4) goto L_0x02c2;
    L_0x027a:
        r4 = r20;
        r11 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r4 != r11) goto L_0x02af;
    L_0x0281:
        r11 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0555 }
        r12.<init>();	 Catch:{ Exception -> 0x0555 }
        r20 = r4;
        r4 = "SELECT unread_count_i FROM dialogs WHERE did = ";
        r12.append(r4);	 Catch:{ Exception -> 0x0555 }
        r12.append(r8);	 Catch:{ Exception -> 0x0555 }
        r4 = r12.toString();	 Catch:{ Exception -> 0x0555 }
        r26 = r2;
        r12 = 0;
        r2 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0555 }
        r2 = r11.queryFinalized(r4, r2);	 Catch:{ Exception -> 0x0555 }
        r4 = r2.next();	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x02ab;
    L_0x02a5:
        r4 = r2.intValue(r12);	 Catch:{ Exception -> 0x0555 }
        r20 = r4;
    L_0x02ab:
        r2.dispose();	 Catch:{ Exception -> 0x0555 }
        goto L_0x02b3;
    L_0x02af:
        r26 = r2;
        r20 = r4;
    L_0x02b3:
        if (r3 == 0) goto L_0x02bb;
    L_0x02b5:
        r2 = 1;
        if (r10 > r2) goto L_0x02c9;
    L_0x02b8:
        r20 = r20 + -1;
        goto L_0x02c9;
    L_0x02bb:
        r2 = r15.media_unread;	 Catch:{ Exception -> 0x0555 }
        if (r2 == 0) goto L_0x02c9;
    L_0x02bf:
        r20 = r20 + 1;
        goto L_0x02c9;
    L_0x02c2:
        r26 = r2;
        goto L_0x02c9;
    L_0x02c5:
        r26 = r2;
        r25 = r3;
    L_0x02c9:
        r1.dispose();	 Catch:{ Exception -> 0x0555 }
        if (r5 != 0) goto L_0x02e1;
    L_0x02ce:
        r11 = r21;
        r2 = r24;
        r3 = r25;
        r5 = 6;
        goto L_0x04bc;
    L_0x02d7:
        r22 = r1;
        r26 = r2;
        r25 = r3;
        r24 = r4;
        r23 = r5;
    L_0x02e1:
        r2 = 8;
        r4 = 7;
        if (r6 != 0) goto L_0x03a6;
    L_0x02e6:
        if (r37 == 0) goto L_0x03a6;
    L_0x02e8:
        r5 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0555 }
        r10.<init>();	 Catch:{ Exception -> 0x0555 }
        r11 = "SELECT pinned, unread_count_i, flags FROM dialogs WHERE did = ";
        r10.append(r11);	 Catch:{ Exception -> 0x0555 }
        r10.append(r8);	 Catch:{ Exception -> 0x0555 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x0555 }
        r11 = 0;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x0555 }
        r5 = r5.queryFinalized(r10, r12);	 Catch:{ Exception -> 0x0555 }
        r10 = r5.next();	 Catch:{ Exception -> 0x0555 }
        if (r10 == 0) goto L_0x031b;
    L_0x0308:
        r12 = r5.intValue(r11);	 Catch:{ Exception -> 0x0555 }
        r11 = 1;
        r27 = r5.intValue(r11);	 Catch:{ Exception -> 0x0555 }
        r11 = 2;
        r28 = r5.intValue(r11);	 Catch:{ Exception -> 0x0555 }
        r11 = r27;
        r29 = r28;
        goto L_0x031f;
    L_0x031b:
        r11 = 0;
        r12 = 0;
        r29 = 0;
    L_0x031f:
        r5.dispose();	 Catch:{ Exception -> 0x0555 }
        if (r10 == 0) goto L_0x0350;
    L_0x0324:
        r5 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r10 = "UPDATE dialogs SET date = ?, last_mid = ?, inbox_max = ?, last_mid_i = ?, pts = ?, date_i = ? WHERE did = ?";
        r5 = r5.executeFast(r10);	 Catch:{ Exception -> 0x0555 }
        r10 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r11 = 1;
        r5.bindInteger(r11, r10);	 Catch:{ Exception -> 0x0555 }
        r10 = 2;
        r5.bindLong(r10, r13);	 Catch:{ Exception -> 0x0555 }
        r10 = r15.id;	 Catch:{ Exception -> 0x0555 }
        r11 = 3;
        r5.bindInteger(r11, r10);	 Catch:{ Exception -> 0x0555 }
        r10 = 4;
        r5.bindLong(r10, r13);	 Catch:{ Exception -> 0x0555 }
        r10 = r0.pts;	 Catch:{ Exception -> 0x0555 }
        r11 = 5;
        r5.bindInteger(r11, r10);	 Catch:{ Exception -> 0x0555 }
        r10 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r11 = 6;
        r5.bindInteger(r11, r10);	 Catch:{ Exception -> 0x0555 }
        r5.bindLong(r4, r8);	 Catch:{ Exception -> 0x0555 }
        goto L_0x03a0;
    L_0x0350:
        r5 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r10 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r5 = r5.executeFast(r10);	 Catch:{ Exception -> 0x0555 }
        r10 = 1;
        r5.bindLong(r10, r8);	 Catch:{ Exception -> 0x0555 }
        r10 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r1 = 2;
        r5.bindInteger(r1, r10);	 Catch:{ Exception -> 0x0555 }
        r1 = 3;
        r10 = 0;
        r5.bindInteger(r1, r10);	 Catch:{ Exception -> 0x0555 }
        r1 = 4;
        r5.bindLong(r1, r13);	 Catch:{ Exception -> 0x0555 }
        r1 = r15.id;	 Catch:{ Exception -> 0x0555 }
        r3 = 5;
        r5.bindInteger(r3, r1);	 Catch:{ Exception -> 0x0555 }
        r1 = 6;
        r5.bindInteger(r1, r10);	 Catch:{ Exception -> 0x0555 }
        r5.bindLong(r4, r13);	 Catch:{ Exception -> 0x0555 }
        r5.bindInteger(r2, r11);	 Catch:{ Exception -> 0x0555 }
        r1 = r0.pts;	 Catch:{ Exception -> 0x0555 }
        r3 = 9;
        r5.bindInteger(r3, r1);	 Catch:{ Exception -> 0x0555 }
        r1 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r3 = 10;
        r5.bindInteger(r3, r1);	 Catch:{ Exception -> 0x0555 }
        r1 = 11;
        r5.bindInteger(r1, r12);	 Catch:{ Exception -> 0x0555 }
        r1 = 12;
        r3 = r29;
        r5.bindInteger(r1, r3);	 Catch:{ Exception -> 0x0555 }
        r1 = 13;
        r3 = 0;
        r5.bindInteger(r1, r3);	 Catch:{ Exception -> 0x0555 }
        r1 = 14;
        r5.bindNull(r1);	 Catch:{ Exception -> 0x0555 }
    L_0x03a0:
        r5.step();	 Catch:{ Exception -> 0x0555 }
        r5.dispose();	 Catch:{ Exception -> 0x0555 }
    L_0x03a6:
        r7.fixUnsupportedMedia(r15);	 Catch:{ Exception -> 0x0555 }
        r25.requery();	 Catch:{ Exception -> 0x0555 }
        r1 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0555 }
        r3 = r15.getObjectSize();	 Catch:{ Exception -> 0x0555 }
        r1.<init>(r3);	 Catch:{ Exception -> 0x0555 }
        r15.serializeToStream(r1);	 Catch:{ Exception -> 0x0555 }
        r3 = r25;
        r5 = 1;
        r3.bindLong(r5, r13);	 Catch:{ Exception -> 0x0555 }
        r5 = 2;
        r3.bindLong(r5, r8);	 Catch:{ Exception -> 0x0555 }
        r5 = org.telegram.messenger.MessageObject.getUnreadFlags(r15);	 Catch:{ Exception -> 0x0555 }
        r10 = 3;
        r3.bindInteger(r10, r5);	 Catch:{ Exception -> 0x0555 }
        r5 = r15.send_state;	 Catch:{ Exception -> 0x0555 }
        r10 = 4;
        r3.bindInteger(r10, r5);	 Catch:{ Exception -> 0x0555 }
        r5 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r10 = 5;
        r3.bindInteger(r10, r5);	 Catch:{ Exception -> 0x0555 }
        r5 = 6;
        r3.bindByteBuffer(r5, r1);	 Catch:{ Exception -> 0x0555 }
        r10 = org.telegram.messenger.MessageObject.isOut(r15);	 Catch:{ Exception -> 0x0555 }
        if (r10 != 0) goto L_0x03e7;
    L_0x03e0:
        r10 = r15.from_scheduled;	 Catch:{ Exception -> 0x0555 }
        if (r10 == 0) goto L_0x03e5;
    L_0x03e4:
        goto L_0x03e7;
    L_0x03e5:
        r10 = 0;
        goto L_0x03e8;
    L_0x03e7:
        r10 = 1;
    L_0x03e8:
        r3.bindInteger(r4, r10);	 Catch:{ Exception -> 0x0555 }
        r4 = r15.ttl;	 Catch:{ Exception -> 0x0555 }
        r3.bindInteger(r2, r4);	 Catch:{ Exception -> 0x0555 }
        r2 = r15.flags;	 Catch:{ Exception -> 0x0555 }
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x03fe;
    L_0x03f6:
        r2 = r15.views;	 Catch:{ Exception -> 0x0555 }
        r4 = 9;
        r3.bindInteger(r4, r2);	 Catch:{ Exception -> 0x0555 }
        goto L_0x0407;
    L_0x03fe:
        r4 = 9;
        r2 = r7.getMessageMediaType(r15);	 Catch:{ Exception -> 0x0555 }
        r3.bindInteger(r4, r2);	 Catch:{ Exception -> 0x0555 }
    L_0x0407:
        r2 = 10;
        r4 = 0;
        r3.bindInteger(r2, r4);	 Catch:{ Exception -> 0x0555 }
        r2 = 11;
        r4 = r15.mentioned;	 Catch:{ Exception -> 0x0555 }
        if (r4 == 0) goto L_0x0415;
    L_0x0413:
        r4 = 1;
        goto L_0x0416;
    L_0x0415:
        r4 = 0;
    L_0x0416:
        r3.bindInteger(r2, r4);	 Catch:{ Exception -> 0x0555 }
        r3.step();	 Catch:{ Exception -> 0x0555 }
        r2 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r15);	 Catch:{ Exception -> 0x0555 }
        if (r2 == 0) goto L_0x0445;
    L_0x0422:
        r24.requery();	 Catch:{ Exception -> 0x0555 }
        r2 = r24;
        r4 = 1;
        r2.bindLong(r4, r13);	 Catch:{ Exception -> 0x0555 }
        r4 = 2;
        r2.bindLong(r4, r8);	 Catch:{ Exception -> 0x0555 }
        r4 = r15.date;	 Catch:{ Exception -> 0x0555 }
        r10 = 3;
        r2.bindInteger(r10, r4);	 Catch:{ Exception -> 0x0555 }
        r4 = org.telegram.messenger.MediaDataController.getMediaType(r15);	 Catch:{ Exception -> 0x0555 }
        r11 = 4;
        r2.bindInteger(r11, r4);	 Catch:{ Exception -> 0x0555 }
        r4 = 5;
        r2.bindByteBuffer(r4, r1);	 Catch:{ Exception -> 0x0555 }
        r2.step();	 Catch:{ Exception -> 0x0555 }
        goto L_0x044a;
    L_0x0445:
        r2 = r24;
        r4 = 5;
        r10 = 3;
        r11 = 4;
    L_0x044a:
        r1.reuse();	 Catch:{ Exception -> 0x0555 }
        r1 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x0555 }
        if (r1 == 0) goto L_0x0478;
    L_0x0453:
        if (r17 != 0) goto L_0x045d;
    L_0x0455:
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r12 = "REPLACE INTO polls VALUES(?, ?)";
        r17 = r1.executeFast(r12);	 Catch:{ Exception -> 0x0555 }
    L_0x045d:
        r1 = r17;
        r12 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r12 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r12;	 Catch:{ Exception -> 0x0555 }
        r1.requery();	 Catch:{ Exception -> 0x0555 }
        r4 = 1;
        r1.bindLong(r4, r13);	 Catch:{ Exception -> 0x0555 }
        r4 = r12.poll;	 Catch:{ Exception -> 0x0555 }
        r12 = r4.id;	 Catch:{ Exception -> 0x0555 }
        r4 = 2;
        r1.bindLong(r4, r12);	 Catch:{ Exception -> 0x0555 }
        r1.step();	 Catch:{ Exception -> 0x0555 }
        r17 = r1;
        goto L_0x04a1;
    L_0x0478:
        r1 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0555 }
        if (r1 == 0) goto L_0x04a1;
    L_0x047e:
        if (r21 != 0) goto L_0x0489;
    L_0x0480:
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r4 = "REPLACE INTO webpage_pending VALUES(?, ?)";
        r1 = r1.executeFast(r4);	 Catch:{ Exception -> 0x0555 }
        goto L_0x048b;
    L_0x0489:
        r1 = r21;
    L_0x048b:
        r1.requery();	 Catch:{ Exception -> 0x0555 }
        r4 = r15.media;	 Catch:{ Exception -> 0x0555 }
        r4 = r4.webpage;	 Catch:{ Exception -> 0x0555 }
        r10 = r4.id;	 Catch:{ Exception -> 0x0555 }
        r4 = 1;
        r1.bindLong(r4, r10);	 Catch:{ Exception -> 0x0555 }
        r4 = 2;
        r1.bindLong(r4, r13);	 Catch:{ Exception -> 0x0555 }
        r1.step();	 Catch:{ Exception -> 0x0555 }
        r21 = r1;
    L_0x04a1:
        if (r35 != 0) goto L_0x04b6;
    L_0x04a3:
        r1 = r7.isValidKeyboardToSave(r15);	 Catch:{ Exception -> 0x0555 }
        if (r1 == 0) goto L_0x04b6;
    L_0x04a9:
        r1 = r19;
        if (r1 == 0) goto L_0x04b3;
    L_0x04ad:
        r4 = r1.id;	 Catch:{ Exception -> 0x0555 }
        r10 = r15.id;	 Catch:{ Exception -> 0x0555 }
        if (r4 >= r10) goto L_0x04b8;
    L_0x04b3:
        r19 = r15;
        goto L_0x04ba;
    L_0x04b6:
        r1 = r19;
    L_0x04b8:
        r19 = r1;
    L_0x04ba:
        r11 = r21;
    L_0x04bc:
        r6 = r6 + 1;
        r10 = r35;
        r4 = r2;
        r1 = r22;
        r5 = r23;
        r2 = r26;
        r12 = 3;
        r13 = 0;
        r14 = 1;
        r15 = 2;
        goto L_0x0189;
    L_0x04cd:
        r26 = r2;
        r2 = r4;
        r21 = r11;
        r1 = r19;
        r3.dispose();	 Catch:{ Exception -> 0x0555 }
        r2.dispose();	 Catch:{ Exception -> 0x0555 }
        if (r21 == 0) goto L_0x04df;
    L_0x04dc:
        r21.dispose();	 Catch:{ Exception -> 0x0555 }
    L_0x04df:
        if (r17 == 0) goto L_0x04e4;
    L_0x04e1:
        r17.dispose();	 Catch:{ Exception -> 0x0555 }
    L_0x04e4:
        if (r1 == 0) goto L_0x04ed;
    L_0x04e6:
        r2 = r30.getMediaDataController();	 Catch:{ Exception -> 0x0555 }
        r2.putBotKeyboard(r8, r1);	 Catch:{ Exception -> 0x0555 }
    L_0x04ed:
        r1 = r30.getFileLoader();	 Catch:{ Exception -> 0x0555 }
        r2 = r26;
        r3 = 0;
        r1.deleteFiles(r2, r3);	 Catch:{ Exception -> 0x0555 }
        r1 = r0.users;	 Catch:{ Exception -> 0x0555 }
        r7.putUsersInternal(r1);	 Catch:{ Exception -> 0x0555 }
        r0 = r0.chats;	 Catch:{ Exception -> 0x0555 }
        r7.putChatsInternal(r0);	 Catch:{ Exception -> 0x0555 }
        r0 = r20;
        r1 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r0 == r1) goto L_0x0543;
    L_0x0508:
        r1 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0555 }
        r3 = "UPDATE dialogs SET unread_count_i = %d WHERE did = %d";
        r4 = 2;
        r4 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x0555 }
        r6 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0555 }
        r10 = 0;
        r4[r10] = r6;	 Catch:{ Exception -> 0x0555 }
        r6 = java.lang.Long.valueOf(r32);	 Catch:{ Exception -> 0x0555 }
        r10 = 1;
        r4[r10] = r6;	 Catch:{ Exception -> 0x0555 }
        r2 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x0555 }
        r1 = r1.executeFast(r2);	 Catch:{ Exception -> 0x0555 }
        r1 = r1.stepThis();	 Catch:{ Exception -> 0x0555 }
        r1.dispose();	 Catch:{ Exception -> 0x0555 }
        r1 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0555 }
        r2 = 1;
        r1.<init>(r2);	 Catch:{ Exception -> 0x0555 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0555 }
        r1.put(r8, r0);	 Catch:{ Exception -> 0x0555 }
        r0 = r30.getMessagesController();	 Catch:{ Exception -> 0x0555 }
        r2 = 0;
        r0.processDialogsUpdateRead(r2, r1);	 Catch:{ Exception -> 0x0555 }
    L_0x0543:
        r0 = r7.database;	 Catch:{ Exception -> 0x0555 }
        r0.commitTransaction();	 Catch:{ Exception -> 0x0555 }
        if (r37 == 0) goto L_0x0559;
    L_0x054a:
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0555 }
        r0.<init>();	 Catch:{ Exception -> 0x0555 }
        r1 = 0;
        r2 = 0;
        r7.updateDialogsWithDeletedMessages(r0, r1, r2, r5);	 Catch:{ Exception -> 0x0555 }
        goto L_0x0559;
    L_0x0555:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0559:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$134$MessagesStorage(boolean, long, org.telegram.tgnet.TLRPC$messages_Messages, int, int, boolean):void");
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
            messageMedia = message.media;
            if (messageMedia instanceof TL_messageMediaPoll) {
                TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) messageMedia;
                if (!tL_messageMediaPoll.results.recent_voters.isEmpty()) {
                    arrayList.addAll(tL_messageMediaPoll.results.recent_voters);
                }
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

    public void getDialogs(int i, int i2, int i3, boolean z) {
        long[] jArr = null;
        if (z) {
            LongSparseArray drafts = getMediaDataController().getDrafts();
            int size = drafts.size();
            if (size > 0) {
                jArr = new long[size];
                for (int i4 = 0; i4 < size; i4++) {
                    jArr[i4] = drafts.keyAt(i4);
                }
            }
        }
        this.storageQueue.postRunnable(new -$$Lambda$MessagesStorage$5X-GQzTUsL_TJG4ZKnsInj8fCJ4(this, i, i2, i3, jArr));
    }

    /* JADX WARNING: Removed duplicated region for block: B:118:0x02b3 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0141 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x013f A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0158 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0156 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0171 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0193 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x01a1 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02b3 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02b3 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02b3 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02b3 A:{Catch:{ Exception -> 0x0462 }} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028c A:{Catch:{ Exception -> 0x0462 }} */
    public /* synthetic */ void lambda$getDialogs$136$MessagesStorage(int r24, int r25, int r26, long[] r27) {
        /*
        r23 = this;
        r1 = r23;
        r2 = r27;
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;
        r12.<init>();
        r13 = new java.util.ArrayList;
        r13.<init>();
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r3.<init>();	 Catch:{ Exception -> 0x0468 }
        r0 = r23.getUserConfig();	 Catch:{ Exception -> 0x0468 }
        r0 = r0.getClientUserId();	 Catch:{ Exception -> 0x0468 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0468 }
        r3.add(r0);	 Catch:{ Exception -> 0x0468 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r4.<init>();	 Catch:{ Exception -> 0x0468 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r5.<init>();	 Catch:{ Exception -> 0x0468 }
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r6.<init>();	 Catch:{ Exception -> 0x0468 }
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r7.<init>();	 Catch:{ Exception -> 0x0468 }
        r8 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0468 }
        r8.<init>();	 Catch:{ Exception -> 0x0468 }
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0468 }
        r10 = 2;
        r9.<init>(r10);	 Catch:{ Exception -> 0x0468 }
        r0 = java.lang.Integer.valueOf(r24);	 Catch:{ Exception -> 0x0468 }
        r9.add(r0);	 Catch:{ Exception -> 0x0468 }
        r14 = 0;
    L_0x0049:
        r0 = r9.size();	 Catch:{ Exception -> 0x0468 }
        r15 = 3;
        if (r14 >= r0) goto L_0x02ee;
    L_0x0050:
        r0 = r9.get(r14);	 Catch:{ Exception -> 0x02e8 }
        r0 = (java.lang.Integer) r0;	 Catch:{ Exception -> 0x02e8 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x02e8 }
        if (r14 != 0) goto L_0x0061;
    L_0x005c:
        r17 = r25;
        r18 = r26;
        goto L_0x0067;
    L_0x0061:
        r17 = 50;
        r17 = 0;
        r18 = 50;
    L_0x0067:
        r10 = r1.database;	 Catch:{ Exception -> 0x02e8 }
        r11 = java.util.Locale.US;	 Catch:{ Exception -> 0x02e8 }
        r20 = r13;
        r13 = "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, s.flags, m.date, d.pts, d.inbox_max, d.outbox_max, m.replydata, d.pinned, d.unread_count_i, d.flags, d.folder_id, d.data FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.folder_id = %d ORDER BY d.pinned DESC, d.date DESC LIMIT %d,%d";
        r1 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0462 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0462 }
        r19 = 0;
        r1[r19] = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = java.lang.Integer.valueOf(r17);	 Catch:{ Exception -> 0x0462 }
        r17 = 1;
        r1[r17] = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = java.lang.Integer.valueOf(r18);	 Catch:{ Exception -> 0x0462 }
        r16 = 2;
        r1[r16] = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = java.lang.String.format(r11, r13, r1);	 Catch:{ Exception -> 0x0462 }
        r1 = 0;
        r11 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x0462 }
        r10 = r10.queryFinalized(r0, r11);	 Catch:{ Exception -> 0x0462 }
    L_0x0094:
        r0 = r10.next();	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x02cf;
    L_0x009a:
        r11 = r7;
        r13 = r8;
        r7 = r10.longValue(r1);	 Catch:{ Exception -> 0x0462 }
        r0 = org.telegram.messenger.DialogObject.isFolderDialogId(r7);	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x00e9;
    L_0x00a6:
        r0 = new org.telegram.tgnet.TLRPC$TL_dialogFolder;	 Catch:{ Exception -> 0x0462 }
        r0.<init>();	 Catch:{ Exception -> 0x0462 }
        r1 = 18;
        r17 = r10.isNull(r1);	 Catch:{ Exception -> 0x0462 }
        if (r17 != 0) goto L_0x00d9;
    L_0x00b3:
        r1 = r10.byteBufferValue(r1);	 Catch:{ Exception -> 0x0462 }
        if (r1 == 0) goto L_0x00c7;
    L_0x00b9:
        r18 = r11;
        r15 = 0;
        r11 = r1.readInt32(r15);	 Catch:{ Exception -> 0x0462 }
        r11 = org.telegram.tgnet.TLRPC.TL_folder.TLdeserialize(r1, r11, r15);	 Catch:{ Exception -> 0x0462 }
        r0.folder = r11;	 Catch:{ Exception -> 0x0462 }
        goto L_0x00d5;
    L_0x00c7:
        r18 = r11;
        r11 = new org.telegram.tgnet.TLRPC$TL_folder;	 Catch:{ Exception -> 0x0462 }
        r11.<init>();	 Catch:{ Exception -> 0x0462 }
        r0.folder = r11;	 Catch:{ Exception -> 0x0462 }
        r11 = r0.folder;	 Catch:{ Exception -> 0x0462 }
        r15 = (int) r7;	 Catch:{ Exception -> 0x0462 }
        r11.id = r15;	 Catch:{ Exception -> 0x0462 }
    L_0x00d5:
        r1.reuse();	 Catch:{ Exception -> 0x0462 }
        goto L_0x00db;
    L_0x00d9:
        r18 = r11;
    L_0x00db:
        if (r14 != 0) goto L_0x00f0;
    L_0x00dd:
        r1 = r0.folder;	 Catch:{ Exception -> 0x0462 }
        r1 = r1.id;	 Catch:{ Exception -> 0x0462 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0462 }
        r9.add(r1);	 Catch:{ Exception -> 0x0462 }
        goto L_0x00f0;
    L_0x00e9:
        r18 = r11;
        r0 = new org.telegram.tgnet.TLRPC$TL_dialog;	 Catch:{ Exception -> 0x0462 }
        r0.<init>();	 Catch:{ Exception -> 0x0462 }
    L_0x00f0:
        r1 = r0;
        r1.id = r7;	 Catch:{ Exception -> 0x0462 }
        r11 = 1;
        r0 = r10.intValue(r11);	 Catch:{ Exception -> 0x0462 }
        r1.top_message = r0;	 Catch:{ Exception -> 0x0462 }
        r11 = 2;
        r0 = r10.intValue(r11);	 Catch:{ Exception -> 0x0462 }
        r1.unread_count = r0;	 Catch:{ Exception -> 0x0462 }
        r11 = 3;
        r0 = r10.intValue(r11);	 Catch:{ Exception -> 0x0462 }
        r1.last_message_date = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 10;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.pts = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = r1.pts;	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x011e;
    L_0x0114:
        r15 = r13;
        r11 = r14;
        r13 = r1.id;	 Catch:{ Exception -> 0x0462 }
        r0 = (int) r13;	 Catch:{ Exception -> 0x0462 }
        if (r0 <= 0) goto L_0x011c;
    L_0x011b:
        goto L_0x0120;
    L_0x011c:
        r0 = 1;
        goto L_0x0121;
    L_0x011e:
        r15 = r13;
        r11 = r14;
    L_0x0120:
        r0 = 0;
    L_0x0121:
        r1.flags = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 11;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.read_inbox_max_id = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 12;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.read_outbox_max_id = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 14;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.pinnedNum = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = r1.pinnedNum;	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x0141;
    L_0x013f:
        r0 = 1;
        goto L_0x0142;
    L_0x0141:
        r0 = 0;
    L_0x0142:
        r1.pinned = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 15;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.unread_mentions_count = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 16;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r13 = 1;
        r0 = r0 & r13;
        if (r0 == 0) goto L_0x0158;
    L_0x0156:
        r0 = 1;
        goto L_0x0159;
    L_0x0158:
        r0 = 0;
    L_0x0159:
        r1.unread_mark = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 8;
        r13 = r10.longValue(r0);	 Catch:{ Exception -> 0x0462 }
        r0 = (int) r13;	 Catch:{ Exception -> 0x0462 }
        r21 = r9;
        r9 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings;	 Catch:{ Exception -> 0x0462 }
        r9.<init>();	 Catch:{ Exception -> 0x0462 }
        r1.notify_settings = r9;	 Catch:{ Exception -> 0x0462 }
        r9 = 1;
        r0 = r0 & r9;
        r9 = 32;
        if (r0 == 0) goto L_0x0184;
    L_0x0171:
        r0 = r1.notify_settings;	 Catch:{ Exception -> 0x0462 }
        r13 = r13 >> r9;
        r14 = (int) r13;	 Catch:{ Exception -> 0x0462 }
        r0.mute_until = r14;	 Catch:{ Exception -> 0x0462 }
        r0 = r1.notify_settings;	 Catch:{ Exception -> 0x0462 }
        r0 = r0.mute_until;	 Catch:{ Exception -> 0x0462 }
        if (r0 != 0) goto L_0x0184;
    L_0x017d:
        r0 = r1.notify_settings;	 Catch:{ Exception -> 0x0462 }
        r13 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0.mute_until = r13;	 Catch:{ Exception -> 0x0462 }
    L_0x0184:
        r0 = 17;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r1.folder_id = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = r12.dialogs;	 Catch:{ Exception -> 0x0462 }
        r0.add(r1);	 Catch:{ Exception -> 0x0462 }
        if (r2 == 0) goto L_0x019a;
    L_0x0193:
        r0 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0462 }
        r6.add(r0);	 Catch:{ Exception -> 0x0462 }
    L_0x019a:
        r0 = 4;
        r0 = r10.byteBufferValue(r0);	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x027d;
    L_0x01a1:
        r7 = 0;
        r8 = r0.readInt32(r7);	 Catch:{ Exception -> 0x0462 }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r8, r7);	 Catch:{ Exception -> 0x0462 }
        r7 = r23.getUserConfig();	 Catch:{ Exception -> 0x0462 }
        r7 = r7.clientUserId;	 Catch:{ Exception -> 0x0462 }
        r8.readAttachPath(r0, r7);	 Catch:{ Exception -> 0x0462 }
        r0.reuse();	 Catch:{ Exception -> 0x0462 }
        if (r8 == 0) goto L_0x027d;
    L_0x01b8:
        r0 = 5;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        org.telegram.messenger.MessageObject.setUnreadFlags(r8, r0);	 Catch:{ Exception -> 0x0462 }
        r0 = 6;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r8.id = r0;	 Catch:{ Exception -> 0x0462 }
        r0 = 9;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x01d1;
    L_0x01cf:
        r1.last_message_date = r0;	 Catch:{ Exception -> 0x0462 }
    L_0x01d1:
        r0 = 7;
        r0 = r10.intValue(r0);	 Catch:{ Exception -> 0x0462 }
        r8.send_state = r0;	 Catch:{ Exception -> 0x0462 }
        r13 = r1.id;	 Catch:{ Exception -> 0x0462 }
        r8.dialog_id = r13;	 Catch:{ Exception -> 0x0462 }
        r0 = r12.messages;	 Catch:{ Exception -> 0x0462 }
        r0.add(r8);	 Catch:{ Exception -> 0x0462 }
        addUsersAndChatsFromMessage(r8, r3, r4);	 Catch:{ Exception -> 0x0462 }
        r0 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x027d;
    L_0x01e8:
        r0 = r8.action;	 Catch:{ Exception -> 0x0273 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;	 Catch:{ Exception -> 0x0273 }
        if (r0 != 0) goto L_0x01fa;
    L_0x01ee:
        r0 = r8.action;	 Catch:{ Exception -> 0x0273 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;	 Catch:{ Exception -> 0x0273 }
        if (r0 != 0) goto L_0x01fa;
    L_0x01f4:
        r0 = r8.action;	 Catch:{ Exception -> 0x0273 }
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore;	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x027d;
    L_0x01fa:
        r0 = 13;
        r7 = r10.isNull(r0);	 Catch:{ Exception -> 0x0273 }
        if (r7 != 0) goto L_0x0239;
    L_0x0202:
        r0 = r10.byteBufferValue(r0);	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x0239;
    L_0x0208:
        r7 = 0;
        r13 = r0.readInt32(r7);	 Catch:{ Exception -> 0x0273 }
        r13 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r13, r7);	 Catch:{ Exception -> 0x0273 }
        r8.replyMessage = r13;	 Catch:{ Exception -> 0x0273 }
        r7 = r8.replyMessage;	 Catch:{ Exception -> 0x0273 }
        r13 = r23.getUserConfig();	 Catch:{ Exception -> 0x0273 }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x0273 }
        r7.readAttachPath(r0, r13);	 Catch:{ Exception -> 0x0273 }
        r0.reuse();	 Catch:{ Exception -> 0x0273 }
        r0 = r8.replyMessage;	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x0239;
    L_0x0225:
        r0 = org.telegram.messenger.MessageObject.isMegagroup(r8);	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x0234;
    L_0x022b:
        r0 = r8.replyMessage;	 Catch:{ Exception -> 0x0273 }
        r7 = r0.flags;	 Catch:{ Exception -> 0x0273 }
        r13 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r13;
        r0.flags = r7;	 Catch:{ Exception -> 0x0273 }
    L_0x0234:
        r0 = r8.replyMessage;	 Catch:{ Exception -> 0x0273 }
        addUsersAndChatsFromMessage(r0, r3, r4);	 Catch:{ Exception -> 0x0273 }
    L_0x0239:
        r0 = r8.replyMessage;	 Catch:{ Exception -> 0x0273 }
        if (r0 != 0) goto L_0x027d;
    L_0x023d:
        r0 = r8.reply_to_msg_id;	 Catch:{ Exception -> 0x0273 }
        r13 = (long) r0;	 Catch:{ Exception -> 0x0273 }
        r0 = r8.to_id;	 Catch:{ Exception -> 0x0273 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0273 }
        if (r0 == 0) goto L_0x0250;
    L_0x0246:
        r0 = r8.to_id;	 Catch:{ Exception -> 0x0273 }
        r0 = r0.channel_id;	 Catch:{ Exception -> 0x0273 }
        r22 = r6;
        r6 = (long) r0;
        r6 = r6 << r9;
        r13 = r13 | r6;
        goto L_0x0252;
    L_0x0250:
        r22 = r6;
    L_0x0252:
        r0 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0271 }
        r6 = r18;
        r0 = r6.contains(r0);	 Catch:{ Exception -> 0x026e }
        if (r0 != 0) goto L_0x0265;
    L_0x025e:
        r0 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x026e }
        r6.add(r0);	 Catch:{ Exception -> 0x026e }
    L_0x0265:
        r13 = r1.id;	 Catch:{ Exception -> 0x026e }
        r7 = r15;
        r7.put(r13, r8);	 Catch:{ Exception -> 0x026c }
        goto L_0x0282;
    L_0x026c:
        r0 = move-exception;
        goto L_0x0279;
    L_0x026e:
        r0 = move-exception;
        r7 = r15;
        goto L_0x0279;
    L_0x0271:
        r0 = move-exception;
        goto L_0x0276;
    L_0x0273:
        r0 = move-exception;
        r22 = r6;
    L_0x0276:
        r7 = r15;
        r6 = r18;
    L_0x0279:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0462 }
        goto L_0x0282;
    L_0x027d:
        r22 = r6;
        r7 = r15;
        r6 = r18;
    L_0x0282:
        r13 = r1.id;	 Catch:{ Exception -> 0x0462 }
        r0 = (int) r13;	 Catch:{ Exception -> 0x0462 }
        r13 = r1.id;	 Catch:{ Exception -> 0x0462 }
        r8 = r13 >> r9;
        r1 = (int) r8;	 Catch:{ Exception -> 0x0462 }
        if (r0 == 0) goto L_0x02b3;
    L_0x028c:
        if (r0 <= 0) goto L_0x02a0;
    L_0x028e:
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0462 }
        r1 = r3.contains(r1);	 Catch:{ Exception -> 0x0462 }
        if (r1 != 0) goto L_0x02c4;
    L_0x0298:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0462 }
        r3.add(r0);	 Catch:{ Exception -> 0x0462 }
        goto L_0x02c4;
    L_0x02a0:
        r0 = -r0;
        r1 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0462 }
        r1 = r4.contains(r1);	 Catch:{ Exception -> 0x0462 }
        if (r1 != 0) goto L_0x02c4;
    L_0x02ab:
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0462 }
        r4.add(r0);	 Catch:{ Exception -> 0x0462 }
        goto L_0x02c4;
    L_0x02b3:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0462 }
        r0 = r5.contains(r0);	 Catch:{ Exception -> 0x0462 }
        if (r0 != 0) goto L_0x02c4;
    L_0x02bd:
        r0 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x0462 }
        r5.add(r0);	 Catch:{ Exception -> 0x0462 }
    L_0x02c4:
        r8 = r7;
        r14 = r11;
        r9 = r21;
        r1 = 0;
        r15 = 3;
        r7 = r6;
        r6 = r22;
        goto L_0x0094;
    L_0x02cf:
        r22 = r6;
        r6 = r7;
        r7 = r8;
        r21 = r9;
        r11 = r14;
        r10.dispose();	 Catch:{ Exception -> 0x0462 }
        r14 = r11 + 1;
        r1 = r23;
        r8 = r7;
        r13 = r20;
        r9 = r21;
        r10 = 2;
        r7 = r6;
        r6 = r22;
        goto L_0x0049;
    L_0x02e8:
        r0 = move-exception;
        r14 = r13;
        r13 = r23;
        goto L_0x046b;
    L_0x02ee:
        r22 = r6;
        r6 = r7;
        r7 = r8;
        r20 = r13;
        r0 = r6.isEmpty();	 Catch:{ Exception -> 0x0462 }
        r1 = ",";
        if (r0 != 0) goto L_0x037d;
    L_0x02fc:
        r13 = r23;
        r0 = r13.database;	 Catch:{ Exception -> 0x037a }
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x037a }
        r9 = "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)";
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x037a }
        r6 = android.text.TextUtils.join(r1, r6);	 Catch:{ Exception -> 0x037a }
        r10 = 0;
        r11[r10] = r6;	 Catch:{ Exception -> 0x037a }
        r6 = java.lang.String.format(r8, r9, r11);	 Catch:{ Exception -> 0x037a }
        r8 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x037a }
        r0 = r0.queryFinalized(r6, r8);	 Catch:{ Exception -> 0x037a }
    L_0x0318:
        r6 = r0.next();	 Catch:{ Exception -> 0x037a }
        if (r6 == 0) goto L_0x0376;
    L_0x031e:
        r6 = r0.byteBufferValue(r10);	 Catch:{ Exception -> 0x037a }
        if (r6 == 0) goto L_0x0370;
    L_0x0324:
        r8 = r6.readInt32(r10);	 Catch:{ Exception -> 0x037a }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r6, r8, r10);	 Catch:{ Exception -> 0x037a }
        r9 = r23.getUserConfig();	 Catch:{ Exception -> 0x037a }
        r9 = r9.clientUserId;	 Catch:{ Exception -> 0x037a }
        r8.readAttachPath(r6, r9);	 Catch:{ Exception -> 0x037a }
        r6.reuse();	 Catch:{ Exception -> 0x037a }
        r6 = 1;
        r9 = r0.intValue(r6);	 Catch:{ Exception -> 0x037a }
        r8.id = r9;	 Catch:{ Exception -> 0x037a }
        r6 = 2;
        r9 = r0.intValue(r6);	 Catch:{ Exception -> 0x037a }
        r8.date = r9;	 Catch:{ Exception -> 0x037a }
        r9 = 3;
        r10 = r0.longValue(r9);	 Catch:{ Exception -> 0x037a }
        r8.dialog_id = r10;	 Catch:{ Exception -> 0x037a }
        addUsersAndChatsFromMessage(r8, r3, r4);	 Catch:{ Exception -> 0x037a }
        r10 = r8.dialog_id;	 Catch:{ Exception -> 0x037a }
        r10 = r7.get(r10);	 Catch:{ Exception -> 0x037a }
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;	 Catch:{ Exception -> 0x037a }
        if (r10 == 0) goto L_0x0372;
    L_0x035a:
        r10.replyMessage = r8;	 Catch:{ Exception -> 0x037a }
        r14 = r10.dialog_id;	 Catch:{ Exception -> 0x037a }
        r8.dialog_id = r14;	 Catch:{ Exception -> 0x037a }
        r8 = org.telegram.messenger.MessageObject.isMegagroup(r10);	 Catch:{ Exception -> 0x037a }
        if (r8 == 0) goto L_0x0372;
    L_0x0366:
        r8 = r10.replyMessage;	 Catch:{ Exception -> 0x037a }
        r10 = r8.flags;	 Catch:{ Exception -> 0x037a }
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r10 = r10 | r11;
        r8.flags = r10;	 Catch:{ Exception -> 0x037a }
        goto L_0x0374;
    L_0x0370:
        r6 = 2;
        r9 = 3;
    L_0x0372:
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
    L_0x0374:
        r10 = 0;
        goto L_0x0318;
    L_0x0376:
        r0.dispose();	 Catch:{ Exception -> 0x037a }
        goto L_0x037f;
    L_0x037a:
        r0 = move-exception;
        goto L_0x0465;
    L_0x037d:
        r13 = r23;
    L_0x037f:
        if (r2 == 0) goto L_0x041c;
    L_0x0381:
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x037a }
        r0.<init>();	 Catch:{ Exception -> 0x037a }
        r6 = 0;
    L_0x0387:
        r7 = r2.length;	 Catch:{ Exception -> 0x037a }
        if (r6 >= r7) goto L_0x03cf;
    L_0x038a:
        r7 = r2[r6];	 Catch:{ Exception -> 0x037a }
        r8 = (int) r7;	 Catch:{ Exception -> 0x037a }
        if (r8 <= 0) goto L_0x03a1;
    L_0x038f:
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x037a }
        r7 = r3.contains(r7);	 Catch:{ Exception -> 0x037a }
        if (r7 != 0) goto L_0x03b3;
    L_0x0399:
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x037a }
        r3.add(r7);	 Catch:{ Exception -> 0x037a }
        goto L_0x03b3;
    L_0x03a1:
        r7 = -r8;
        r8 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x037a }
        r8 = r4.contains(r8);	 Catch:{ Exception -> 0x037a }
        if (r8 != 0) goto L_0x03b3;
    L_0x03ac:
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x037a }
        r4.add(r7);	 Catch:{ Exception -> 0x037a }
    L_0x03b3:
        r7 = r2[r6];	 Catch:{ Exception -> 0x037a }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x037a }
        r8 = r22;
        r7 = r8.contains(r7);	 Catch:{ Exception -> 0x037a }
        if (r7 != 0) goto L_0x03ca;
    L_0x03c1:
        r9 = r2[r6];	 Catch:{ Exception -> 0x037a }
        r7 = java.lang.Long.valueOf(r9);	 Catch:{ Exception -> 0x037a }
        r0.add(r7);	 Catch:{ Exception -> 0x037a }
    L_0x03ca:
        r6 = r6 + 1;
        r22 = r8;
        goto L_0x0387;
    L_0x03cf:
        r2 = r0.isEmpty();	 Catch:{ Exception -> 0x037a }
        if (r2 != 0) goto L_0x0413;
    L_0x03d5:
        r2 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x037a }
        r6 = r0.size();	 Catch:{ Exception -> 0x037a }
        r2.<init>(r6);	 Catch:{ Exception -> 0x037a }
        r6 = r13.database;	 Catch:{ Exception -> 0x037a }
        r7 = java.util.Locale.US;	 Catch:{ Exception -> 0x037a }
        r8 = "SELECT did, folder_id FROM dialogs WHERE did IN(%s)";
        r9 = 1;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x037a }
        r0 = android.text.TextUtils.join(r1, r0);	 Catch:{ Exception -> 0x037a }
        r9 = 0;
        r10[r9] = r0;	 Catch:{ Exception -> 0x037a }
        r0 = java.lang.String.format(r7, r8, r10);	 Catch:{ Exception -> 0x037a }
        r7 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x037a }
        r0 = r6.queryFinalized(r0, r7);	 Catch:{ Exception -> 0x037a }
    L_0x03f8:
        r6 = r0.next();	 Catch:{ Exception -> 0x037a }
        if (r6 == 0) goto L_0x040f;
    L_0x03fe:
        r6 = r0.longValue(r9);	 Catch:{ Exception -> 0x037a }
        r8 = 1;
        r10 = r0.intValue(r8);	 Catch:{ Exception -> 0x037a }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x037a }
        r2.put(r6, r10);	 Catch:{ Exception -> 0x037a }
        goto L_0x03f8;
    L_0x040f:
        r0.dispose();	 Catch:{ Exception -> 0x037a }
        goto L_0x0414;
    L_0x0413:
        r2 = 0;
    L_0x0414:
        r0 = new org.telegram.messenger.-$$Lambda$MessagesStorage$ydIjALm1InJP9OKz1LdVNHK0_eY;	 Catch:{ Exception -> 0x037a }
        r0.<init>(r13, r2);	 Catch:{ Exception -> 0x037a }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x037a }
    L_0x041c:
        r0 = r5.isEmpty();	 Catch:{ Exception -> 0x037a }
        if (r0 != 0) goto L_0x042c;
    L_0x0422:
        r0 = android.text.TextUtils.join(r1, r5);	 Catch:{ Exception -> 0x037a }
        r14 = r20;
        r13.getEncryptedChatsInternal(r0, r14, r3);	 Catch:{ Exception -> 0x0460 }
        goto L_0x042e;
    L_0x042c:
        r14 = r20;
    L_0x042e:
        r0 = r4.isEmpty();	 Catch:{ Exception -> 0x0460 }
        if (r0 != 0) goto L_0x043d;
    L_0x0434:
        r0 = android.text.TextUtils.join(r1, r4);	 Catch:{ Exception -> 0x0460 }
        r2 = r12.chats;	 Catch:{ Exception -> 0x0460 }
        r13.getChatsInternal(r0, r2);	 Catch:{ Exception -> 0x0460 }
    L_0x043d:
        r0 = r3.isEmpty();	 Catch:{ Exception -> 0x0460 }
        if (r0 != 0) goto L_0x044c;
    L_0x0443:
        r0 = android.text.TextUtils.join(r1, r3);	 Catch:{ Exception -> 0x0460 }
        r1 = r12.users;	 Catch:{ Exception -> 0x0460 }
        r13.getUsersInternal(r0, r1);	 Catch:{ Exception -> 0x0460 }
    L_0x044c:
        r2 = r23.getMessagesController();	 Catch:{ Exception -> 0x0460 }
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r11 = 1;
        r3 = r12;
        r4 = r14;
        r5 = r24;
        r6 = r25;
        r7 = r26;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0460 }
        goto L_0x0492;
    L_0x0460:
        r0 = move-exception;
        goto L_0x046b;
    L_0x0462:
        r0 = move-exception;
        r13 = r23;
    L_0x0465:
        r14 = r20;
        goto L_0x046b;
    L_0x0468:
        r0 = move-exception;
        r14 = r13;
        r13 = r1;
    L_0x046b:
        r1 = r12.dialogs;
        r1.clear();
        r1 = r12.users;
        r1.clear();
        r1 = r12.chats;
        r1.clear();
        r14.clear();
        org.telegram.messenger.FileLog.e(r0);
        r2 = r23.getMessagesController();
        r6 = 0;
        r7 = 100;
        r8 = 1;
        r9 = 1;
        r10 = 0;
        r11 = 1;
        r3 = r12;
        r4 = r14;
        r5 = r24;
        r2.processLoadedDialogs(r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x0492:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$getDialogs$136$MessagesStorage(int, int, int, long[]):void");
    }

    public /* synthetic */ void lambda$null$135$MessagesStorage(LongSparseArray longSparseArray) {
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
        for (int i2 = 0; i2 < 5; i2++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            sQLitePreparedStatement2.bindInteger(2, i2);
            sQLitePreparedStatement2.bindInteger(3, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(4, i);
            sQLitePreparedStatement2.step();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x017d A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x017a A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x018f A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x018d A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x019e A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01ee A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01c9 A:{Catch:{ Exception -> 0x02f5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01fc  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00f5 A:{SYNTHETIC, Splitter:B:35:0x00f5} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0262 A:{Catch:{ Exception -> 0x02f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0260 A:{Catch:{ Exception -> 0x02f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x028c A:{Catch:{ Exception -> 0x02f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0275 A:{Catch:{ Exception -> 0x02f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0295 A:{Catch:{ Exception -> 0x02f1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02b4 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x029c A:{Catch:{ Exception -> 0x02f1 }} */
    private void putDialogsInternal(org.telegram.tgnet.TLRPC.messages_Dialogs r22, int r23) {
        /*
        r21 = this;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r3 = r1.database;	 Catch:{ Exception -> 0x02f5 }
        r3.beginTransaction();	 Catch:{ Exception -> 0x02f5 }
        r3 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x02f5 }
        r4 = r0.messages;	 Catch:{ Exception -> 0x02f5 }
        r4 = r4.size();	 Catch:{ Exception -> 0x02f5 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x02f5 }
        r5 = 0;
    L_0x0017:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f5 }
        r6 = r6.size();	 Catch:{ Exception -> 0x02f5 }
        if (r5 >= r6) goto L_0x0031;
    L_0x001f:
        r6 = r0.messages;	 Catch:{ Exception -> 0x02f5 }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x02f5 }
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;	 Catch:{ Exception -> 0x02f5 }
        r7 = org.telegram.messenger.MessageObject.getDialogId(r6);	 Catch:{ Exception -> 0x02f5 }
        r3.put(r7, r6);	 Catch:{ Exception -> 0x02f5 }
        r5 = r5 + 1;
        goto L_0x0017;
    L_0x0031:
        r5 = r0.dialogs;	 Catch:{ Exception -> 0x02f5 }
        r5 = r5.isEmpty();	 Catch:{ Exception -> 0x02f5 }
        if (r5 != 0) goto L_0x02db;
    L_0x0039:
        r5 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r6 = "REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)";
        r5 = r5.executeFast(r6);	 Catch:{ Exception -> 0x02f1 }
        r6 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r7 = "REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        r6 = r6.executeFast(r7);	 Catch:{ Exception -> 0x02f1 }
        r7 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r8 = "REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)";
        r7 = r7.executeFast(r8);	 Catch:{ Exception -> 0x02f1 }
        r8 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r9 = "REPLACE INTO dialog_settings VALUES(?, ?)";
        r8 = r8.executeFast(r9);	 Catch:{ Exception -> 0x02f1 }
        r9 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r10 = "REPLACE INTO messages_holes VALUES(?, ?, ?)";
        r9 = r9.executeFast(r10);	 Catch:{ Exception -> 0x02f1 }
        r10 = r1.database;	 Catch:{ Exception -> 0x02f1 }
        r11 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r10 = r10.executeFast(r11);	 Catch:{ Exception -> 0x02f1 }
        r12 = 0;
        r13 = 0;
    L_0x006b:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02f1 }
        r14 = r14.size();	 Catch:{ Exception -> 0x02f1 }
        if (r12 >= r14) goto L_0x02c1;
    L_0x0073:
        r14 = r0.dialogs;	 Catch:{ Exception -> 0x02f1 }
        r14 = r14.get(r12);	 Catch:{ Exception -> 0x02f1 }
        r14 = (org.telegram.tgnet.TLRPC.Dialog) r14;	 Catch:{ Exception -> 0x02f1 }
        org.telegram.messenger.DialogObject.initDialog(r14);	 Catch:{ Exception -> 0x02f1 }
        r11 = 1;
        if (r2 != r11) goto L_0x00b1;
    L_0x0081:
        r11 = r1.database;	 Catch:{ Exception -> 0x02f5 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f5 }
        r15.<init>();	 Catch:{ Exception -> 0x02f5 }
        r4 = "SELECT did FROM dialogs WHERE did = ";
        r15.append(r4);	 Catch:{ Exception -> 0x02f5 }
        r4 = r8;
        r16 = r9;
        r8 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r15.append(r8);	 Catch:{ Exception -> 0x02f5 }
        r8 = r15.toString();	 Catch:{ Exception -> 0x02f5 }
        r9 = 0;
        r15 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x02f5 }
        r8 = r11.queryFinalized(r8, r15);	 Catch:{ Exception -> 0x02f5 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f5 }
        r8.dispose();	 Catch:{ Exception -> 0x02f5 }
        if (r9 == 0) goto L_0x00e8;
    L_0x00a9:
        r19 = r3;
        r15 = r12;
        r9 = r16;
        r1 = 0;
        goto L_0x02b4;
    L_0x00b1:
        r4 = r8;
        r16 = r9;
        r8 = r14.pinned;	 Catch:{ Exception -> 0x02f1 }
        if (r8 == 0) goto L_0x00e8;
    L_0x00b8:
        r8 = 2;
        if (r2 != r8) goto L_0x00e8;
    L_0x00bb:
        r8 = r1.database;	 Catch:{ Exception -> 0x02f5 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x02f5 }
        r9.<init>();	 Catch:{ Exception -> 0x02f5 }
        r11 = "SELECT pinned FROM dialogs WHERE did = ";
        r9.append(r11);	 Catch:{ Exception -> 0x02f5 }
        r15 = r12;
        r11 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r9.append(r11);	 Catch:{ Exception -> 0x02f5 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x02f5 }
        r11 = 0;
        r12 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x02f5 }
        r8 = r8.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x02f5 }
        r9 = r8.next();	 Catch:{ Exception -> 0x02f5 }
        if (r9 == 0) goto L_0x00e4;
    L_0x00de:
        r9 = r8.intValue(r11);	 Catch:{ Exception -> 0x02f5 }
        r14.pinnedNum = r9;	 Catch:{ Exception -> 0x02f5 }
    L_0x00e4:
        r8.dispose();	 Catch:{ Exception -> 0x02f5 }
        goto L_0x00e9;
    L_0x00e8:
        r15 = r12;
    L_0x00e9:
        r8 = r14.id;	 Catch:{ Exception -> 0x02f1 }
        r8 = r3.get(r8);	 Catch:{ Exception -> 0x02f1 }
        r8 = (org.telegram.tgnet.TLRPC.Message) r8;	 Catch:{ Exception -> 0x02f1 }
        r17 = 32;
        if (r8 == 0) goto L_0x01fc;
    L_0x00f5:
        r9 = r8.date;	 Catch:{ Exception -> 0x02f5 }
        r11 = 0;
        r9 = java.lang.Math.max(r9, r11);	 Catch:{ Exception -> 0x02f5 }
        r11 = r1.isValidKeyboardToSave(r8);	 Catch:{ Exception -> 0x02f5 }
        if (r11 == 0) goto L_0x010e;
    L_0x0102:
        r11 = r21.getMediaDataController();	 Catch:{ Exception -> 0x02f5 }
        r18 = r13;
        r12 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r11.putBotKeyboard(r12, r8);	 Catch:{ Exception -> 0x02f5 }
        goto L_0x0110;
    L_0x010e:
        r18 = r13;
    L_0x0110:
        r1.fixUnsupportedMedia(r8);	 Catch:{ Exception -> 0x02f5 }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02f5 }
        r12 = r8.getObjectSize();	 Catch:{ Exception -> 0x02f5 }
        r11.<init>(r12);	 Catch:{ Exception -> 0x02f5 }
        r8.serializeToStream(r11);	 Catch:{ Exception -> 0x02f5 }
        r12 = r8.id;	 Catch:{ Exception -> 0x02f5 }
        r12 = (long) r12;	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f5 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f5 }
        if (r2 == 0) goto L_0x0133;
    L_0x0128:
        r2 = r8.to_id;	 Catch:{ Exception -> 0x02f5 }
        r2 = r2.channel_id;	 Catch:{ Exception -> 0x02f5 }
        r19 = r3;
        r2 = (long) r2;	 Catch:{ Exception -> 0x02f5 }
        r2 = r2 << r17;
        r12 = r12 | r2;
        goto L_0x0135;
    L_0x0133:
        r19 = r3;
    L_0x0135:
        r5.requery();	 Catch:{ Exception -> 0x02f5 }
        r2 = 1;
        r5.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f5 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r20 = r9;
        r9 = 2;
        r5.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = org.telegram.messenger.MessageObject.getUnreadFlags(r8);	 Catch:{ Exception -> 0x02f5 }
        r3 = 3;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.send_state;	 Catch:{ Exception -> 0x02f5 }
        r3 = 4;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f5 }
        r3 = 5;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = 6;
        r5.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f5 }
        r2 = org.telegram.messenger.MessageObject.isOut(r8);	 Catch:{ Exception -> 0x02f5 }
        if (r2 != 0) goto L_0x0169;
    L_0x0162:
        r2 = r8.from_scheduled;	 Catch:{ Exception -> 0x02f5 }
        if (r2 == 0) goto L_0x0167;
    L_0x0166:
        goto L_0x0169;
    L_0x0167:
        r2 = 0;
        goto L_0x016a;
    L_0x0169:
        r2 = 1;
    L_0x016a:
        r3 = 7;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = 8;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.flags;	 Catch:{ Exception -> 0x02f5 }
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x017d;
    L_0x017a:
        r2 = r8.views;	 Catch:{ Exception -> 0x02f5 }
        goto L_0x017e;
    L_0x017d:
        r2 = 0;
    L_0x017e:
        r3 = 9;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = 10;
        r3 = 0;
        r5.bindInteger(r2, r3);	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.mentioned;	 Catch:{ Exception -> 0x02f5 }
        if (r2 == 0) goto L_0x018f;
    L_0x018d:
        r2 = 1;
        goto L_0x0190;
    L_0x018f:
        r2 = 0;
    L_0x0190:
        r3 = 11;
        r5.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r5.step();	 Catch:{ Exception -> 0x02f5 }
        r2 = org.telegram.messenger.MediaDataController.canAddMessageToMedia(r8);	 Catch:{ Exception -> 0x02f5 }
        if (r2 == 0) goto L_0x01c0;
    L_0x019e:
        r7.requery();	 Catch:{ Exception -> 0x02f5 }
        r2 = 1;
        r7.bindLong(r2, r12);	 Catch:{ Exception -> 0x02f5 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r9 = 2;
        r7.bindLong(r9, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.date;	 Catch:{ Exception -> 0x02f5 }
        r3 = 3;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = org.telegram.messenger.MediaDataController.getMediaType(r8);	 Catch:{ Exception -> 0x02f5 }
        r3 = 4;
        r7.bindInteger(r3, r2);	 Catch:{ Exception -> 0x02f5 }
        r2 = 5;
        r7.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f5 }
        r7.step();	 Catch:{ Exception -> 0x02f5 }
    L_0x01c0:
        r11.reuse();	 Catch:{ Exception -> 0x02f5 }
        r2 = r8.media;	 Catch:{ Exception -> 0x02f5 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;	 Catch:{ Exception -> 0x02f5 }
        if (r2 == 0) goto L_0x01ee;
    L_0x01c9:
        if (r18 != 0) goto L_0x01d4;
    L_0x01cb:
        r2 = r1.database;	 Catch:{ Exception -> 0x02f5 }
        r3 = "REPLACE INTO polls VALUES(?, ?)";
        r2 = r2.executeFast(r3);	 Catch:{ Exception -> 0x02f5 }
        goto L_0x01d6;
    L_0x01d4:
        r2 = r18;
    L_0x01d6:
        r3 = r8.media;	 Catch:{ Exception -> 0x02f5 }
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;	 Catch:{ Exception -> 0x02f5 }
        r2.requery();	 Catch:{ Exception -> 0x02f5 }
        r9 = 1;
        r2.bindLong(r9, r12);	 Catch:{ Exception -> 0x02f5 }
        r3 = r3.poll;	 Catch:{ Exception -> 0x02f5 }
        r11 = r3.id;	 Catch:{ Exception -> 0x02f5 }
        r3 = 2;
        r2.bindLong(r3, r11);	 Catch:{ Exception -> 0x02f5 }
        r2.step();	 Catch:{ Exception -> 0x02f5 }
        r13 = r2;
        goto L_0x01f0;
    L_0x01ee:
        r13 = r18;
    L_0x01f0:
        r2 = r14.id;	 Catch:{ Exception -> 0x02f5 }
        r8 = r8.id;	 Catch:{ Exception -> 0x02f5 }
        r9 = r16;
        createFirstHoles(r2, r9, r10, r8);	 Catch:{ Exception -> 0x02f5 }
        r2 = r20;
        goto L_0x0203;
    L_0x01fc:
        r19 = r3;
        r18 = r13;
        r9 = r16;
        r2 = 0;
    L_0x0203:
        r3 = r14.top_message;	 Catch:{ Exception -> 0x02f1 }
        r11 = (long) r3;	 Catch:{ Exception -> 0x02f1 }
        r3 = r14.peer;	 Catch:{ Exception -> 0x02f1 }
        if (r3 == 0) goto L_0x0218;
    L_0x020a:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02f1 }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02f1 }
        if (r3 == 0) goto L_0x0218;
    L_0x0210:
        r3 = r14.peer;	 Catch:{ Exception -> 0x02f1 }
        r3 = r3.channel_id;	 Catch:{ Exception -> 0x02f1 }
        r0 = (long) r3;	 Catch:{ Exception -> 0x02f1 }
        r0 = r0 << r17;
        r11 = r11 | r0;
    L_0x0218:
        r6.requery();	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.id;	 Catch:{ Exception -> 0x02f1 }
        r3 = 1;
        r6.bindLong(r3, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = 2;
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.unread_count;	 Catch:{ Exception -> 0x02f1 }
        r1 = 3;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = 4;
        r6.bindLong(r0, r11);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.read_inbox_max_id;	 Catch:{ Exception -> 0x02f1 }
        r1 = 5;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.read_outbox_max_id;	 Catch:{ Exception -> 0x02f1 }
        r1 = 6;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = 0;
        r2 = 7;
        r6.bindLong(r2, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.unread_mentions_count;	 Catch:{ Exception -> 0x02f1 }
        r1 = 8;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.pts;	 Catch:{ Exception -> 0x02f1 }
        r1 = 9;
        r6.bindInteger(r1, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = 10;
        r1 = 0;
        r6.bindInteger(r0, r1);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.pinnedNum;	 Catch:{ Exception -> 0x02f1 }
        r2 = 11;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14.unread_mark;	 Catch:{ Exception -> 0x02f1 }
        if (r0 == 0) goto L_0x0262;
    L_0x0260:
        r0 = 1;
        goto L_0x0263;
    L_0x0262:
        r0 = 0;
    L_0x0263:
        r2 = 12;
        r6.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02f1 }
        r0 = 13;
        r2 = r14.folder_id;	 Catch:{ Exception -> 0x02f1 }
        r6.bindInteger(r0, r2);	 Catch:{ Exception -> 0x02f1 }
        r0 = r14 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder;	 Catch:{ Exception -> 0x02f1 }
        r2 = 14;
        if (r0 == 0) goto L_0x028c;
    L_0x0275:
        r0 = r14;
        r0 = (org.telegram.tgnet.TLRPC.TL_dialogFolder) r0;	 Catch:{ Exception -> 0x02f1 }
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x02f1 }
        r3 = r0.folder;	 Catch:{ Exception -> 0x02f1 }
        r3 = r3.getObjectSize();	 Catch:{ Exception -> 0x02f1 }
        r11.<init>(r3);	 Catch:{ Exception -> 0x02f1 }
        r0 = r0.folder;	 Catch:{ Exception -> 0x02f1 }
        r0.serializeToStream(r11);	 Catch:{ Exception -> 0x02f1 }
        r6.bindByteBuffer(r2, r11);	 Catch:{ Exception -> 0x02f1 }
        goto L_0x0290;
    L_0x028c:
        r6.bindNull(r2);	 Catch:{ Exception -> 0x02f1 }
        r11 = 0;
    L_0x0290:
        r6.step();	 Catch:{ Exception -> 0x02f1 }
        if (r11 == 0) goto L_0x0298;
    L_0x0295:
        r11.reuse();	 Catch:{ Exception -> 0x02f1 }
    L_0x0298:
        r0 = r14.notify_settings;	 Catch:{ Exception -> 0x02f1 }
        if (r0 == 0) goto L_0x02b4;
    L_0x029c:
        r4.requery();	 Catch:{ Exception -> 0x02f1 }
        r2 = r14.id;	 Catch:{ Exception -> 0x02f1 }
        r0 = 1;
        r4.bindLong(r0, r2);	 Catch:{ Exception -> 0x02f1 }
        r2 = r14.notify_settings;	 Catch:{ Exception -> 0x02f1 }
        r2 = r2.mute_until;	 Catch:{ Exception -> 0x02f1 }
        if (r2 == 0) goto L_0x02ac;
    L_0x02ab:
        goto L_0x02ad;
    L_0x02ac:
        r0 = 0;
    L_0x02ad:
        r2 = 2;
        r4.bindInteger(r2, r0);	 Catch:{ Exception -> 0x02f1 }
        r4.step();	 Catch:{ Exception -> 0x02f1 }
    L_0x02b4:
        r12 = r15 + 1;
        r1 = r21;
        r0 = r22;
        r2 = r23;
        r8 = r4;
        r3 = r19;
        goto L_0x006b;
    L_0x02c1:
        r4 = r8;
        r18 = r13;
        r5.dispose();	 Catch:{ Exception -> 0x02f1 }
        r6.dispose();	 Catch:{ Exception -> 0x02f1 }
        r7.dispose();	 Catch:{ Exception -> 0x02f1 }
        r4.dispose();	 Catch:{ Exception -> 0x02f1 }
        r9.dispose();	 Catch:{ Exception -> 0x02f1 }
        r10.dispose();	 Catch:{ Exception -> 0x02f1 }
        if (r18 == 0) goto L_0x02db;
    L_0x02d8:
        r18.dispose();	 Catch:{ Exception -> 0x02f1 }
    L_0x02db:
        r0 = r22;
        r1 = r0.users;	 Catch:{ Exception -> 0x02f1 }
        r2 = r21;
        r2.putUsersInternal(r1);	 Catch:{ Exception -> 0x02ef }
        r0 = r0.chats;	 Catch:{ Exception -> 0x02ef }
        r2.putChatsInternal(r0);	 Catch:{ Exception -> 0x02ef }
        r0 = r2.database;	 Catch:{ Exception -> 0x02ef }
        r0.commitTransaction();	 Catch:{ Exception -> 0x02ef }
        goto L_0x02fa;
    L_0x02ef:
        r0 = move-exception;
        goto L_0x02f7;
    L_0x02f1:
        r0 = move-exception;
        r2 = r21;
        goto L_0x02f7;
    L_0x02f5:
        r0 = move-exception;
        r2 = r1;
    L_0x02f7:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02fa:
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
            SQLitePreparedStatement executeFast = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
            long peerDialogId;
            if (arrayList != null) {
                int size = arrayList.size();
                for (i = 0; i < size; i++) {
                    TL_folderPeer tL_folderPeer = (TL_folderPeer) arrayList.get(i);
                    peerDialogId = DialogObject.getPeerDialogId(tL_folderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_folderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId);
                    executeFast.step();
                }
            } else if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (i = 0; i < size2; i++) {
                    TL_inputFolderPeer tL_inputFolderPeer = (TL_inputFolderPeer) arrayList2.get(i);
                    peerDialogId = DialogObject.getPeerDialogId(tL_inputFolderPeer.peer);
                    executeFast.requery();
                    executeFast.bindInteger(1, tL_inputFolderPeer.folder_id);
                    executeFast.bindInteger(2, 0);
                    executeFast.bindLong(3, peerDialogId);
                    executeFast.step();
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
