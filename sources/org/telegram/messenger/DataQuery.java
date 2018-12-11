package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.StickersArchiveAlert;
import org.telegram.p005ui.Components.TypefaceSpan;
import org.telegram.p005ui.Components.URLSpanReplacement;
import org.telegram.p005ui.Components.URLSpanUserMention;
import org.telegram.p005ui.LaunchActivity;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeersDisabled;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_draftMessageEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC.TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllDrafts;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_saveDraft;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class DataQuery {
    private static volatile DataQuery[] Instance = new DataQuery[3];
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 5;
    public static final int MEDIA_URL = 3;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<MessageEntity> entityComparator = DataQuery$$Lambda$110.$instance;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<Document>> allStickers = new HashMap();
    private HashMap<String, ArrayList<Document>> allStickersFeatured = new HashMap();
    private int[] archivedStickersCount = new int[2];
    private SparseArray<BotInfo> botInfos = new SparseArray();
    private LongSparseArray<Message> botKeyboards = new LongSparseArray();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private int currentAccount;
    private LongSparseArray<Message> draftMessages = new LongSparseArray();
    private LongSparseArray<DraftMessage> drafts = new LongSparseArray();
    private ArrayList<StickerSetCovered> featuredStickerSets = new ArrayList();
    private LongSparseArray<StickerSetCovered> featuredStickerSetsById = new LongSparseArray();
    private boolean featuredStickersLoaded;
    private LongSparseArray<TL_messages_stickerSet> groupStickerSets = new LongSparseArray();
    public ArrayList<TL_topPeer> hints = new ArrayList();
    private boolean inTransaction;
    public ArrayList<TL_topPeer> inlineBots = new ArrayList();
    private LongSparseArray<TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray();
    private long lastMergeDialogId;
    private int lastReqId;
    private int lastReturnedNum;
    private String lastSearchQuery;
    private int[] loadDate = new int[4];
    private int loadFeaturedDate;
    private int loadFeaturedHash;
    private int[] loadHash = new int[4];
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[3];
    private boolean[] loadingStickers = new boolean[4];
    private int mergeReqId;
    private int[] messagesSearchCount = new int[]{0, 0};
    private boolean[] messagesSearchEndReached = new boolean[]{false, false};
    private SharedPreferences preferences;
    private ArrayList<Long> readingStickerSets = new ArrayList();
    private ArrayList<Document> recentGifs = new ArrayList();
    private boolean recentGifsLoaded;
    private ArrayList<Document>[] recentStickers = new ArrayList[]{new ArrayList(), new ArrayList(), new ArrayList()};
    private boolean[] recentStickersLoaded = new boolean[3];
    private int reqId;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList();
    private SparseArray<MessageObject>[] searchResultMessagesMap = new SparseArray[]{new SparseArray(), new SparseArray()};
    private ArrayList<TL_messages_stickerSet>[] stickerSets = new ArrayList[]{new ArrayList(), new ArrayList(), new ArrayList(0), new ArrayList()};
    private LongSparseArray<TL_messages_stickerSet> stickerSetsById = new LongSparseArray();
    private HashMap<String, TL_messages_stickerSet> stickerSetsByName = new HashMap();
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray();
    private boolean[] stickersLoaded = new boolean[4];
    private ArrayList<Long> unreadStickerSets = new ArrayList();

    public static DataQuery getInstance(int num) {
        Throwable th;
        DataQuery localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DataQuery.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        DataQuery[] dataQueryArr = Instance;
                        DataQuery localInstance2 = new DataQuery(num);
                        try {
                            dataQueryArr[num] = localInstance2;
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

    public DataQuery(int num) {
        this.currentAccount = num;
        if (this.currentAccount == 0) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Entry<String, ?> entry : this.preferences.getAll().entrySet()) {
            try {
                String key = (String) entry.getKey();
                long did = Utilities.parseLong(key).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                if (key.startsWith("r_")) {
                    Message message = Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    message.readAttachPath(serializedData, UserConfig.getInstance(this.currentAccount).clientUserId);
                    if (message != null) {
                        this.draftMessages.put(did, message);
                    }
                } else {
                    DraftMessage draftMessage = DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (draftMessage != null) {
                        this.drafts.put(did, draftMessage);
                    }
                }
                serializedData.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void cleanup() {
        int a;
        for (a = 0; a < 3; a++) {
            this.recentStickers[a].clear();
            this.loadingRecentStickers[a] = false;
            this.recentStickersLoaded[a] = false;
        }
        for (a = 0; a < 4; a++) {
            this.loadHash[a] = 0;
            this.loadDate[a] = 0;
            this.stickerSets[a].clear();
            this.loadingStickers[a] = false;
            this.stickersLoaded[a] = false;
        }
        this.featuredStickerSets.clear();
        this.loadFeaturedDate = 0;
        this.loadFeaturedHash = 0;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById.clear();
        this.featuredStickerSets.clear();
        this.unreadStickerSets.clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    public void checkStickers(int type) {
        if (!this.loadingStickers[type]) {
            if (!this.stickersLoaded[type] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[type])) >= 3600) {
                loadStickers(type, true, false);
            }
        }
    }

    public void checkFeaturedStickers() {
        if (!this.loadingFeaturedStickers) {
            if (!this.featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate)) >= 3600) {
                loadFeaturedStickers(true, false);
            }
        }
    }

    public ArrayList<Document> getRecentStickers(int type) {
        ArrayList<Document> arrayList = this.recentStickers[type];
        return new ArrayList(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<Document> getRecentStickersNoCopy(int type) {
        return this.recentStickers[type];
    }

    public boolean isStickerInFavorites(Document document) {
        for (int a = 0; a < this.recentStickers[2].size(); a++) {
            Document d = (Document) this.recentStickers[2].get(a);
            if (d.var_id == document.var_id && d.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentSticker(int type, Object parentObject, Document document, int date, boolean remove) {
        int maxCount;
        boolean found = false;
        for (int a = 0; a < this.recentStickers[type].size(); a++) {
            Document image = (Document) this.recentStickers[type].get(a);
            if (image.var_id == document.var_id) {
                this.recentStickers[type].remove(a);
                if (!remove) {
                    this.recentStickers[type].add(0, image);
                }
                found = true;
            }
        }
        if (!(found || remove)) {
            this.recentStickers[type].add(0, document);
        }
        if (type == 2) {
            if (remove) {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("RemovedFromFavorites", R.string.RemovedFromFavorites), 0).show();
            } else {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("AddedToFavorites", R.string.AddedToFavorites), 0).show();
            }
            TL_messages_faveSticker req = new TL_messages_faveSticker();
            req.var_id = new TL_inputDocument();
            req.var_id.var_id = document.var_id;
            req.var_id.access_hash = document.access_hash;
            req.var_id.file_reference = document.file_reference;
            if (req.var_id.file_reference == null) {
                req.var_id.file_reference = new byte[0];
            }
            req.unfave = remove;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$0(this, parentObject, req));
            maxCount = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
        } else {
            maxCount = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
        }
        if (this.recentStickers[type].size() > maxCount || remove) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$1(this, type, remove ? document : (Document) this.recentStickers[type].remove(this.recentStickers[type].size() - 1)));
        }
        if (!remove) {
            ArrayList<Document> arrayList = new ArrayList();
            arrayList.add(document);
            processLoadedRecentDocuments(type, arrayList, false, date);
        }
        if (type == 2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(false), Integer.valueOf(type));
        }
    }

    final /* synthetic */ void lambda$addRecentSticker$0$DataQuery(Object parentObject, TL_messages_faveSticker req, TLObject response, TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req);
        }
    }

    final /* synthetic */ void lambda$addRecentSticker$1$DataQuery(int type, Document old) {
        int cacheType;
        if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.var_id + "' AND type = " + cacheType).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public ArrayList<Document> getRecentGifs() {
        return new ArrayList(this.recentGifs);
    }

    public void removeRecentGif(Document document) {
        this.recentGifs.remove(document);
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.var_id = new TL_inputDocument();
        req.var_id.var_id = document.var_id;
        req.var_id.access_hash = document.access_hash;
        req.var_id.file_reference = document.file_reference;
        if (req.var_id.file_reference == null) {
            req.var_id.file_reference = new byte[0];
        }
        req.unsave = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$2(this, req));
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$3(this, document));
    }

    final /* synthetic */ void lambda$removeRecentGif$2$DataQuery(TL_messages_saveGif req, TLObject response, TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            FileRefController.getInstance(this.currentAccount).requestReference("gif", req);
        }
    }

    final /* synthetic */ void lambda$removeRecentGif$3$DataQuery(Document document) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.var_id + "' AND type = 2").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void addRecentGif(Document document, int date) {
        boolean found = false;
        for (int a = 0; a < this.recentGifs.size(); a++) {
            Document image = (Document) this.recentGifs.get(a);
            if (image.var_id == document.var_id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                found = true;
            }
        }
        if (!found) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > MessagesController.getInstance(this.currentAccount).maxRecentGifsCount) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$4(this, (Document) this.recentGifs.remove(this.recentGifs.size() - 1)));
        }
        ArrayList<Document> arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(0, arrayList, true, date);
    }

    final /* synthetic */ void lambda$addRecentGif$4$DataQuery(Document old) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.var_id + "' AND type = 2").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public boolean isLoadingStickers(int type) {
        return this.loadingStickers[type];
    }

    public void replaceStickerSet(TL_messages_stickerSet set) {
        TL_messages_stickerSet existingSet = (TL_messages_stickerSet) this.stickerSetsById.get(set.set.var_id);
        boolean isGroupSet = false;
        if (existingSet == null) {
            existingSet = (TL_messages_stickerSet) this.stickerSetsByName.get(set.set.short_name);
        }
        if (existingSet == null) {
            existingSet = (TL_messages_stickerSet) this.groupStickerSets.get(set.set.var_id);
            if (existingSet != null) {
                isGroupSet = true;
            }
        }
        if (existingSet != null) {
            int a;
            LongSparseArray<Document> documents = new LongSparseArray();
            int size = set.documents.size();
            for (a = 0; a < size; a++) {
                Document document = (Document) set.documents.get(a);
                documents.put(document.var_id, document);
            }
            boolean changed = false;
            size = existingSet.documents.size();
            for (a = 0; a < size; a++) {
                Document newDocument = (Document) documents.get(((Document) set.documents.get(a)).var_id);
                if (newDocument != null) {
                    existingSet.documents.set(a, newDocument);
                    changed = true;
                }
            }
            if (!changed) {
                return;
            }
            if (isGroupSet) {
                putSetToCache(existingSet);
                return;
            }
            int type = set.set.masks ? 1 : 0;
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        }
    }

    public TL_messages_stickerSet getStickerSetByName(String name) {
        return (TL_messages_stickerSet) this.stickerSetsByName.get(name);
    }

    public TL_messages_stickerSet getStickerSetById(long id) {
        return (TL_messages_stickerSet) this.stickerSetsById.get(id);
    }

    public TL_messages_stickerSet getGroupStickerSetById(StickerSet stickerSet) {
        TL_messages_stickerSet set = (TL_messages_stickerSet) this.stickerSetsById.get(stickerSet.var_id);
        if (set == null) {
            set = (TL_messages_stickerSet) this.groupStickerSets.get(stickerSet.var_id);
            if (set == null || set.set == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return set;
    }

    public void putGroupStickerSet(TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(stickerSet.set.var_id, stickerSet);
    }

    private void loadGroupStickerSet(StickerSet stickerSet, boolean cache) {
        if (cache) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$5(this, stickerSet));
            return;
        }
        TL_messages_getStickerSet req = new TL_messages_getStickerSet();
        req.stickerset = new TL_inputStickerSetID();
        req.stickerset.var_id = stickerSet.var_id;
        req.stickerset.access_hash = stickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$6(this));
    }

    final /* synthetic */ void lambda$loadGroupStickerSet$6$DataQuery(StickerSet stickerSet) {
        try {
            TL_messages_stickerSet set;
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.var_id + "'", new Object[0]);
            if (!cursor.next() || cursor.isNull(0)) {
                set = null;
            } else {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    set = TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                } else {
                    set = null;
                }
            }
            cursor.dispose();
            if (set == null || set.set == null || set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
            if (set != null && set.set != null) {
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$109(this, set));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$5$DataQuery(TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.var_id, set);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.var_id));
    }

    final /* synthetic */ void lambda$loadGroupStickerSet$8$DataQuery(TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$108(this, (TL_messages_stickerSet) response));
        }
    }

    final /* synthetic */ void lambda$null$7$DataQuery(TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.var_id, set);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.var_id));
    }

    private void putSetToCache(TL_messages_stickerSet set) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$7(this, set));
    }

    final /* synthetic */ void lambda$putSetToCache$9$DataQuery(TL_messages_stickerSet set) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindString(1, "s_" + set.set.var_id);
            state.bindInteger(2, 6);
            state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
            state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
            state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
            state.bindInteger(6, 0);
            state.bindInteger(7, 0);
            state.bindInteger(8, 0);
            state.bindInteger(9, 0);
            NativeByteBuffer data = new NativeByteBuffer(set.getObjectSize());
            set.serializeToStream(data);
            state.bindByteBuffer(10, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public HashMap<String, ArrayList<Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public boolean canAddStickerToFavorites() {
        return (this.stickersLoaded[0] && this.stickerSets[0].size() < 5 && this.recentStickers[2].isEmpty()) ? false : true;
    }

    public ArrayList<TL_messages_stickerSet> getStickerSets(int type) {
        if (type == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[type];
    }

    public ArrayList<StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean isStickerPackInstalled(long id) {
        return this.installedStickerSetsById.indexOfKey(id) >= 0;
    }

    public boolean isStickerPackUnread(long id) {
        return this.unreadStickerSets.contains(Long.valueOf(id));
    }

    public boolean isStickerPackInstalled(String name) {
        return this.stickerSetsByName.containsKey(name);
    }

    public String getEmojiForSticker(long id) {
        String value = (String) this.stickersByEmoji.get(id);
        return value != null ? value : TtmlNode.ANONYMOUS_REGION_ID;
    }

    private static int calcDocumentsHash(ArrayList<Document> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        long acc = 0;
        for (int a = 0; a < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size()); a++) {
            Document document = (Document) arrayList.get(a);
            if (document != null) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (document.var_id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) document.var_id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void loadRecents(int type, boolean gif, boolean cache, boolean force) {
        if (gif) {
            if (!this.loadingRecentGifs) {
                this.loadingRecentGifs = true;
                if (this.recentGifsLoaded) {
                    cache = false;
                }
            } else {
                return;
            }
        } else if (!this.loadingRecentStickers[type]) {
            this.loadingRecentStickers[type] = true;
            if (this.recentStickersLoaded[type]) {
                cache = false;
            }
        } else {
            return;
        }
        if (cache) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$8(this, gif, type));
            return;
        }
        SharedPreferences preferences = MessagesController.getEmojiSettings(this.currentAccount);
        if (!force) {
            long lastLoadTime;
            if (gif) {
                lastLoadTime = preferences.getLong("lastGifLoadTime", 0);
            } else if (type == 0) {
                lastLoadTime = preferences.getLong("lastStickersLoadTime", 0);
            } else if (type == 1) {
                lastLoadTime = preferences.getLong("lastStickersLoadTimeMask", 0);
            } else {
                lastLoadTime = preferences.getLong("lastStickersLoadTimeFavs", 0);
            }
            if (Math.abs(System.currentTimeMillis() - lastLoadTime) < 3600000) {
                if (gif) {
                    this.loadingRecentGifs = false;
                    return;
                } else {
                    this.loadingRecentStickers[type] = false;
                    return;
                }
            }
        }
        if (gif) {
            TL_messages_getSavedGifs req = new TL_messages_getSavedGifs();
            req.hash = calcDocumentsHash(this.recentGifs);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$9(this, type, gif));
            return;
        }
        TLObject request;
        TLObject req2;
        if (type == 2) {
            req2 = new TL_messages_getFavedStickers();
            req2.hash = calcDocumentsHash(this.recentStickers[type]);
            request = req2;
        } else {
            req2 = new TL_messages_getRecentStickers();
            req2.hash = calcDocumentsHash(this.recentStickers[type]);
            req2.attached = type == 1;
            request = req2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new DataQuery$$Lambda$10(this, type, gif));
    }

    final /* synthetic */ void lambda$loadRecents$11$DataQuery(boolean gif, int type) {
        int cacheType;
        if (gif) {
            cacheType = 2;
        } else if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + cacheType + " ORDER BY date DESC", new Object[0]);
            ArrayList<Document> arrayList = new ArrayList();
            while (cursor.next()) {
                if (!cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        Document document = Document.TLdeserialize(data, data.readInt32(false), false);
                        if (document != null) {
                            arrayList.add(document);
                        }
                        data.reuse();
                    }
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$107(this, gif, arrayList, type));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$10$DataQuery(boolean gif, ArrayList arrayList, int type) {
        if (gif) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[type] = arrayList;
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        loadRecents(type, gif, false, false);
    }

    final /* synthetic */ void lambda$loadRecents$12$DataQuery(int type, boolean gif, TLObject response, TL_error error) {
        ArrayList<Document> arrayList = null;
        if (response instanceof TL_messages_savedGifs) {
            arrayList = ((TL_messages_savedGifs) response).gifs;
        }
        processLoadedRecentDocuments(type, arrayList, gif, 0);
    }

    final /* synthetic */ void lambda$loadRecents$13$DataQuery(int type, boolean gif, TLObject response, TL_error error) {
        ArrayList<Document> arrayList = null;
        if (type == 2) {
            if (response instanceof TL_messages_favedStickers) {
                arrayList = ((TL_messages_favedStickers) response).stickers;
            }
        } else if (response instanceof TL_messages_recentStickers) {
            arrayList = ((TL_messages_recentStickers) response).stickers;
        }
        processLoadedRecentDocuments(type, arrayList, gif, 0);
    }

    protected void processLoadedRecentDocuments(int type, ArrayList<Document> documents, boolean gif, int date) {
        if (documents != null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$11(this, gif, type, documents, date));
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$12(this, gif, type, documents));
        }
    }

    final /* synthetic */ void lambda$processLoadedRecentDocuments$14$DataQuery(boolean gif, int type, ArrayList documents, int date) {
        try {
            int maxCount;
            int cacheType;
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            if (gif) {
                maxCount = MessagesController.getInstance(this.currentAccount).maxRecentGifsCount;
            } else if (type == 2) {
                maxCount = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
            } else {
                maxCount = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int count = documents.size();
            if (gif) {
                cacheType = 2;
            } else if (type == 0) {
                cacheType = 3;
            } else if (type == 1) {
                cacheType = 4;
            } else {
                cacheType = 5;
            }
            int a = 0;
            while (a < count && a != maxCount) {
                Document document = (Document) documents.get(a);
                state.requery();
                state.bindString(1, TtmlNode.ANONYMOUS_REGION_ID + document.var_id);
                state.bindInteger(2, cacheType);
                state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                state.bindInteger(6, 0);
                state.bindInteger(7, 0);
                state.bindInteger(8, 0);
                state.bindInteger(9, date != 0 ? date : count - a);
                NativeByteBuffer data = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(data);
                state.bindByteBuffer(10, data);
                state.step();
                if (data != null) {
                    data.reuse();
                }
                a++;
            }
            state.dispose();
            database.commitTransaction();
            if (documents.size() >= maxCount) {
                database.beginTransaction();
                for (a = maxCount; a < documents.size(); a++) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((Document) documents.get(a)).var_id + "' AND type = " + cacheType).stepThis().dispose();
                }
                database.commitTransaction();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$processLoadedRecentDocuments$15$DataQuery(boolean gif, int type, ArrayList documents) {
        Editor editor = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (gif) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            editor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
            if (type == 0) {
                editor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (type == 1) {
                editor.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else {
                editor.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (documents != null) {
            if (gif) {
                this.recentGifs = documents;
            } else {
                this.recentStickers[type] = documents;
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        }
    }

    public void reorderStickers(int type, ArrayList<Long> order) {
        Collections.sort(this.stickerSets[type], new DataQuery$$Lambda$13(order));
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    static final /* synthetic */ int lambda$reorderStickers$16$DataQuery(ArrayList order, TL_messages_stickerSet lhs, TL_messages_stickerSet rhs) {
        int index1 = order.indexOf(Long.valueOf(lhs.set.var_id));
        int index2 = order.indexOf(Long.valueOf(rhs.set.var_id));
        if (index1 > index2) {
            return 1;
        }
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    public void calcNewHash(int type) {
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
    }

    public void addNewStickerSet(TL_messages_stickerSet set) {
        if (this.stickerSetsById.indexOfKey(set.set.var_id) < 0 && !this.stickerSetsByName.containsKey(set.set.short_name)) {
            int a;
            int type = set.set.masks ? 1 : 0;
            this.stickerSets[type].add(0, set);
            this.stickerSetsById.put(set.set.var_id, set);
            this.installedStickerSetsById.put(set.set.var_id, set);
            this.stickerSetsByName.put(set.set.short_name, set);
            LongSparseArray<Document> stickersById = new LongSparseArray();
            for (a = 0; a < set.documents.size(); a++) {
                Document document = (Document) set.documents.get(a);
                stickersById.put(document.var_id, document);
            }
            for (a = 0; a < set.packs.size(); a++) {
                TL_stickerPack stickerPack = (TL_stickerPack) set.packs.get(a);
                stickerPack.emoticon = stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                ArrayList<Document> arrayList = (ArrayList) this.allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = (Long) stickerPack.documents.get(c);
                    if (this.stickersByEmoji.indexOfKey(id.longValue()) < 0) {
                        this.stickersByEmoji.put(id.longValue(), stickerPack.emoticon);
                    }
                    Document sticker = (Document) stickersById.get(id.longValue());
                    if (sticker != null) {
                        arrayList.add(sticker);
                    }
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
            loadStickers(type, false, true);
        }
    }

    public void loadFeaturedStickers(boolean cache, boolean force) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$14(this));
                return;
            }
            TL_messages_getFeaturedStickers req = new TL_messages_getFeaturedStickers();
            req.hash = force ? 0 : this.loadFeaturedHash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$15(this, req));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0092  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$loadFeaturedStickers$17$DataQuery() {
        Throwable e;
        Throwable th;
        ArrayList<StickerSetCovered> newStickerArray = null;
        ArrayList<Long> unread = new ArrayList();
        int date = 0;
        int hash = 0;
        SQLiteCursor cursor = null;
        try {
            cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT data, unread, date, hash FROM stickers_featured WHERE 1", new Object[0]);
            if (cursor.next()) {
                int count;
                int a;
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    ArrayList<StickerSetCovered> newStickerArray2 = new ArrayList();
                    try {
                        count = data.readInt32(false);
                        for (a = 0; a < count; a++) {
                            newStickerArray2.add(StickerSetCovered.TLdeserialize(data, data.readInt32(false), false));
                        }
                        data.reuse();
                        newStickerArray = newStickerArray2;
                    } catch (Throwable th2) {
                        th = th2;
                        newStickerArray = newStickerArray2;
                        if (cursor != null) {
                            cursor.dispose();
                        }
                        throw th;
                    }
                }
                data = cursor.byteBufferValue(1);
                if (data != null) {
                    count = data.readInt32(false);
                    for (a = 0; a < count; a++) {
                        unread.add(Long.valueOf(data.readInt64(false)));
                    }
                    data.reuse();
                }
                date = cursor.intValue(2);
                hash = calcFeaturedStickersHash(newStickerArray);
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th3) {
            e = th3;
            FileLog.m13e(e);
            if (cursor != null) {
            }
            processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
        }
        processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
    }

    final /* synthetic */ void lambda$loadFeaturedStickers$19$DataQuery(TL_messages_getFeaturedStickers req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$106(this, response, req));
    }

    final /* synthetic */ void lambda$null$18$DataQuery(TLObject response, TL_messages_getFeaturedStickers req) {
        if (response instanceof TL_messages_featuredStickers) {
            TL_messages_featuredStickers res = (TL_messages_featuredStickers) response;
            processLoadedFeaturedStickers(res.sets, res.unread, false, (int) (System.currentTimeMillis() / 1000), res.hash);
            return;
        }
        processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), req.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> res, ArrayList<Long> unreadStickers, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$16(this));
        Utilities.stageQueue.postRunnable(new DataQuery$$Lambda$17(this, cache, res, date, hash, unreadStickers));
    }

    final /* synthetic */ void lambda$processLoadedFeaturedStickers$20$DataQuery() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    final /* synthetic */ void lambda$processLoadedFeaturedStickers$24$DataQuery(boolean cache, ArrayList res, int date, int hash, ArrayList unreadStickers) {
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) date)) >= 3600)) || (!cache && res == null && hash == 0)) {
            Runnable dataQuery$$Lambda$103 = new DataQuery$$Lambda$103(this, res, hash);
            long j = (res != null || cache) ? 0 : 1000;
            AndroidUtilities.runOnUIThread(dataQuery$$Lambda$103, j);
            if (res == null) {
                return;
            }
        }
        if (res != null) {
            try {
                ArrayList<StickerSetCovered> stickerSetsNew = new ArrayList();
                LongSparseArray<StickerSetCovered> stickerSetsByIdNew = new LongSparseArray();
                for (int a = 0; a < res.size(); a++) {
                    StickerSetCovered stickerSet = (StickerSetCovered) res.get(a);
                    stickerSetsNew.add(stickerSet);
                    stickerSetsByIdNew.put(stickerSet.set.var_id, stickerSet);
                }
                if (!cache) {
                    putFeaturedStickersToCache(stickerSetsNew, unreadStickers, date, hash);
                }
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$104(this, unreadStickers, stickerSetsByIdNew, stickerSetsNew, hash, date));
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } else if (!cache) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$105(this, date));
            putFeaturedStickersToCache(null, null, date, 0);
        }
    }

    final /* synthetic */ void lambda$null$21$DataQuery(ArrayList res, int hash) {
        if (!(res == null || hash == 0)) {
            this.loadFeaturedHash = hash;
        }
        loadFeaturedStickers(false, false);
    }

    final /* synthetic */ void lambda$null$22$DataQuery(ArrayList unreadStickers, LongSparseArray stickerSetsByIdNew, ArrayList stickerSetsNew, int hash, int date) {
        this.unreadStickerSets = unreadStickers;
        this.featuredStickerSetsById = stickerSetsByIdNew;
        this.featuredStickerSets = stickerSetsNew;
        this.loadFeaturedHash = hash;
        this.loadFeaturedDate = date;
        loadStickers(3, true, false);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    final /* synthetic */ void lambda$null$23$DataQuery(int date) {
        this.loadFeaturedDate = date;
    }

    private void putFeaturedStickersToCache(ArrayList<StickerSetCovered> stickers, ArrayList<Long> unreadStickers, int date, int hash) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$18(this, stickers != null ? new ArrayList(stickers) : null, unreadStickers, date, hash));
    }

    final /* synthetic */ void lambda$putFeaturedStickersToCache$25$DataQuery(ArrayList stickersFinal, ArrayList unreadStickers, int date, int hash) {
        SQLitePreparedStatement state;
        if (stickersFinal != null) {
            try {
                int a;
                state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (a = 0; a < stickersFinal.size(); a++) {
                    size += ((StickerSetCovered) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                NativeByteBuffer data2 = new NativeByteBuffer((unreadStickers.size() * 8) + 4);
                data.writeInt32(stickersFinal.size());
                for (a = 0; a < stickersFinal.size(); a++) {
                    ((StickerSetCovered) stickersFinal.get(a)).serializeToStream(data);
                }
                data2.writeInt32(unreadStickers.size());
                for (a = 0; a < unreadStickers.size(); a++) {
                    data2.writeInt64(((Long) unreadStickers.get(a)).longValue());
                }
                state.bindInteger(1, 1);
                state.bindByteBuffer(2, data);
                state.bindByteBuffer(3, data2);
                state.bindInteger(4, date);
                state.bindInteger(5, hash);
                state.step();
                data.reuse();
                data2.reuse();
                state.dispose();
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
        state.requery();
        state.bindInteger(1, date);
        state.step();
        state.dispose();
    }

    private int calcFeaturedStickersHash(ArrayList<StickerSetCovered> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((StickerSetCovered) sets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.var_id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.var_id))) % 2147483648L;
                if (this.unreadStickerSets.contains(Long.valueOf(set.var_id))) {
                    acc = (((acc * 20261) + 2147483648L) + 1) % 2147483648L;
                }
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersAsRead(boolean query) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (query) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_readFeaturedStickers(), DataQuery$$Lambda$19.$instance);
            }
        }
    }

    static final /* synthetic */ void lambda$markFaturedStickersAsRead$26$DataQuery(TLObject response, TL_error error) {
    }

    public int getFeaturesStickersHashWithoutUnread() {
        long acc = 0;
        for (int a = 0; a < this.featuredStickerSets.size(); a++) {
            StickerSet set = ((StickerSetCovered) this.featuredStickerSets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.var_id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.var_id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersByIdAsRead(long id) {
        if (this.unreadStickerSets.contains(Long.valueOf(id)) && !this.readingStickerSets.contains(Long.valueOf(id))) {
            this.readingStickerSets.add(Long.valueOf(id));
            TL_messages_readFeaturedStickers req = new TL_messages_readFeaturedStickers();
            req.var_id.add(Long.valueOf(id));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, DataQuery$$Lambda$20.$instance);
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$21(this, id), 1000);
        }
    }

    static final /* synthetic */ void lambda$markFaturedStickersByIdAsRead$27$DataQuery(TLObject response, TL_error error) {
    }

    final /* synthetic */ void lambda$markFaturedStickersByIdAsRead$28$DataQuery(long id) {
        this.unreadStickerSets.remove(Long.valueOf(id));
        this.readingStickerSets.remove(Long.valueOf(id));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
    }

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void loadArchivedStickersCount(int type, boolean cache) {
        boolean z = true;
        if (cache) {
            int count = MessagesController.getNotificationsSettings(this.currentAccount).getInt("archivedStickersCount" + type, -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
                return;
            }
            this.archivedStickersCount[type] = count;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
            return;
        }
        TL_messages_getArchivedStickers req = new TL_messages_getArchivedStickers();
        req.limit = 0;
        if (type != 1) {
            z = false;
        }
        req.masks = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$22(this, type));
    }

    final /* synthetic */ void lambda$loadArchivedStickersCount$30$DataQuery(int type, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$102(this, error, response, type));
    }

    final /* synthetic */ void lambda$null$29$DataQuery(TL_error error, TLObject response, int type) {
        if (error == null) {
            TL_messages_archivedStickers res = (TL_messages_archivedStickers) response;
            this.archivedStickersCount[type] = res.count;
            MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("archivedStickersCount" + type, res.count).commit();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
        }
    }

    private void processLoadStickersResponse(int type, TL_messages_allStickers res) {
        ArrayList<TL_messages_stickerSet> newStickerArray = new ArrayList();
        if (res.sets.isEmpty()) {
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
            return;
        }
        LongSparseArray<TL_messages_stickerSet> newStickerSets = new LongSparseArray();
        for (int a = 0; a < res.sets.size(); a++) {
            StickerSet stickerSet = (StickerSet) res.sets.get(a);
            TL_messages_stickerSet oldSet = (TL_messages_stickerSet) this.stickerSetsById.get(stickerSet.var_id);
            if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                newStickerArray.add(null);
                int index = a;
                TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                req.stickerset = new TL_inputStickerSetID();
                req.stickerset.var_id = stickerSet.var_id;
                req.stickerset.access_hash = stickerSet.access_hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$23(this, newStickerArray, index, newStickerSets, stickerSet, res, type));
            } else {
                oldSet.set.archived = stickerSet.archived;
                oldSet.set.installed = stickerSet.installed;
                oldSet.set.official = stickerSet.official;
                newStickerSets.put(oldSet.set.var_id, oldSet);
                newStickerArray.add(oldSet);
                if (newStickerSets.size() == res.sets.size()) {
                    processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                }
            }
        }
    }

    final /* synthetic */ void lambda$processLoadStickersResponse$32$DataQuery(ArrayList newStickerArray, int index, LongSparseArray newStickerSets, StickerSet stickerSet, TL_messages_allStickers res, int type, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$101(this, response, newStickerArray, index, newStickerSets, stickerSet, res, type));
    }

    final /* synthetic */ void lambda$null$31$DataQuery(TLObject response, ArrayList newStickerArray, int index, LongSparseArray newStickerSets, StickerSet stickerSet, TL_messages_allStickers res, int type) {
        TL_messages_stickerSet res1 = (TL_messages_stickerSet) response;
        newStickerArray.set(index, res1);
        newStickerSets.put(stickerSet.var_id, res1);
        if (newStickerSets.size() == res.sets.size()) {
            for (int a1 = 0; a1 < newStickerArray.size(); a1++) {
                if (newStickerArray.get(a1) == null) {
                    newStickerArray.remove(a1);
                }
            }
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
        }
    }

    public void loadStickers(int type, boolean cache, boolean force) {
        int hash = 0;
        if (!this.loadingStickers[type]) {
            if (type != 3) {
                loadArchivedStickersCount(type, cache);
            } else if (this.featuredStickerSets.isEmpty() || !MessagesController.getInstance(this.currentAccount).preloadFeaturedStickers) {
                return;
            }
            this.loadingStickers[type] = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$24(this, type));
            } else if (type == 3) {
                TL_messages_allStickers response = new TL_messages_allStickers();
                response.hash = this.loadFeaturedHash;
                int size = this.featuredStickerSets.size();
                for (int a = 0; a < size; a++) {
                    response.sets.add(((StickerSetCovered) this.featuredStickerSets.get(a)).set);
                }
                processLoadStickersResponse(type, response);
            } else {
                TLObject req;
                if (type == 0) {
                    req = new TL_messages_getAllStickers();
                    TL_messages_getAllStickers tL_messages_getAllStickers = (TL_messages_getAllStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getAllStickers.hash = hash;
                } else {
                    req = new TL_messages_getMaskStickers();
                    TL_messages_getMaskStickers tL_messages_getMaskStickers = (TL_messages_getMaskStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getMaskStickers.hash = hash;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$25(this, type, hash));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x007b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$loadStickers$33$DataQuery(int type) {
        Throwable e;
        Throwable th;
        ArrayList<TL_messages_stickerSet> newStickerArray = null;
        int date = 0;
        int hash = 0;
        SQLiteCursor cursor = null;
        try {
            cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT data, date, hash FROM stickers_v2 WHERE id = " + (type + 1), new Object[0]);
            if (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    ArrayList<TL_messages_stickerSet> newStickerArray2 = new ArrayList();
                    try {
                        int count = data.readInt32(false);
                        for (int a = 0; a < count; a++) {
                            newStickerArray2.add(TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false));
                        }
                        data.reuse();
                        newStickerArray = newStickerArray2;
                    } catch (Throwable th2) {
                        th = th2;
                        newStickerArray = newStickerArray2;
                        if (cursor != null) {
                            cursor.dispose();
                        }
                        throw th;
                    }
                }
                date = cursor.intValue(1);
                hash = calcStickersHash(newStickerArray);
            }
            if (cursor != null) {
                cursor.dispose();
            }
        } catch (Throwable th3) {
            e = th3;
        }
        processLoadedStickers(type, newStickerArray, true, date, hash);
    }

    final /* synthetic */ void lambda$loadStickers$35$DataQuery(int type, int hash, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$100(this, response, type, hash));
    }

    final /* synthetic */ void lambda$null$34$DataQuery(TLObject response, int type, int hash) {
        if (response instanceof TL_messages_allStickers) {
            processLoadStickersResponse(type, (TL_messages_allStickers) response);
            return;
        }
        processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), hash);
    }

    private void putStickersToCache(int type, ArrayList<TL_messages_stickerSet> stickers, int date, int hash) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$26(this, stickers != null ? new ArrayList(stickers) : null, type, date, hash));
    }

    final /* synthetic */ void lambda$putStickersToCache$36$DataQuery(ArrayList stickersFinal, int type, int date, int hash) {
        SQLitePreparedStatement state;
        if (stickersFinal != null) {
            try {
                int a;
                state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (a = 0; a < stickersFinal.size(); a++) {
                    size += ((TL_messages_stickerSet) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                data.writeInt32(stickersFinal.size());
                for (a = 0; a < stickersFinal.size(); a++) {
                    ((TL_messages_stickerSet) stickersFinal.get(a)).serializeToStream(data);
                }
                state.bindInteger(1, type + 1);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.bindInteger(4, hash);
                state.step();
                data.reuse();
                state.dispose();
                return;
            } catch (Throwable e) {
                FileLog.m13e(e);
                return;
            }
        }
        state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
        state.requery();
        state.bindInteger(1, date);
        state.step();
        state.dispose();
    }

    public String getStickerSetName(long setId) {
        TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(setId);
        if (stickerSet != null) {
            return stickerSet.set.short_name;
        }
        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.featuredStickerSetsById.get(setId);
        if (stickerSetCovered != null) {
            return stickerSetCovered.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(Document document) {
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TL_inputStickerSetID) {
                    return attribute.stickerset.var_id;
                }
                return -1;
            }
        }
        return -1;
    }

    private static int calcStickersHash(ArrayList<TL_messages_stickerSet> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((TL_messages_stickerSet) sets.get(a)).set;
            if (!set.archived) {
                acc = (((20261 * acc) + 2147483648L) + ((long) set.hash)) % 2147483648L;
            }
        }
        return (int) acc;
    }

    private void processLoadedStickers(int type, ArrayList<TL_messages_stickerSet> res, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$27(this, type));
        Utilities.stageQueue.postRunnable(new DataQuery$$Lambda$28(this, cache, res, date, hash, type));
    }

    final /* synthetic */ void lambda$processLoadedStickers$37$DataQuery(int type) {
        this.loadingStickers[type] = false;
        this.stickersLoaded[type] = true;
    }

    final /* synthetic */ void lambda$processLoadedStickers$41$DataQuery(boolean cache, ArrayList res, int date, int hash, int type) {
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) date)) >= 3600)) || (!cache && res == null && hash == 0)) {
            Runnable dataQuery$$Lambda$97 = new DataQuery$$Lambda$97(this, res, hash, type);
            long j = (res != null || cache) ? 0 : 1000;
            AndroidUtilities.runOnUIThread(dataQuery$$Lambda$97, j);
            if (res == null) {
                return;
            }
        }
        if (res != null) {
            try {
                ArrayList<TL_messages_stickerSet> stickerSetsNew = new ArrayList();
                LongSparseArray<TL_messages_stickerSet> stickerSetsByIdNew = new LongSparseArray();
                HashMap<String, TL_messages_stickerSet> stickerSetsByNameNew = new HashMap();
                LongSparseArray<String> stickersByEmojiNew = new LongSparseArray();
                LongSparseArray<Document> stickersByIdNew = new LongSparseArray();
                HashMap<String, ArrayList<Document>> allStickersNew = new HashMap();
                for (int a = 0; a < res.size(); a++) {
                    TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) res.get(a);
                    if (stickerSet != null) {
                        int b;
                        stickerSetsNew.add(stickerSet);
                        stickerSetsByIdNew.put(stickerSet.set.var_id, stickerSet);
                        stickerSetsByNameNew.put(stickerSet.set.short_name, stickerSet);
                        for (b = 0; b < stickerSet.documents.size(); b++) {
                            Document document = (Document) stickerSet.documents.get(b);
                            if (!(document == null || (document instanceof TL_documentEmpty))) {
                                stickersByIdNew.put(document.var_id, document);
                            }
                        }
                        if (!stickerSet.set.archived) {
                            for (b = 0; b < stickerSet.packs.size(); b++) {
                                TL_stickerPack stickerPack = (TL_stickerPack) stickerSet.packs.get(b);
                                if (!(stickerPack == null || stickerPack.emoticon == null)) {
                                    stickerPack.emoticon = stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                    ArrayList<Document> arrayList = (ArrayList) allStickersNew.get(stickerPack.emoticon);
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                        allStickersNew.put(stickerPack.emoticon, arrayList);
                                    }
                                    for (int c = 0; c < stickerPack.documents.size(); c++) {
                                        Long id = (Long) stickerPack.documents.get(c);
                                        if (stickersByEmojiNew.indexOfKey(id.longValue()) < 0) {
                                            stickersByEmojiNew.put(id.longValue(), stickerPack.emoticon);
                                        }
                                        Document sticker = (Document) stickersByIdNew.get(id.longValue());
                                        if (sticker != null) {
                                            arrayList.add(sticker);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!cache) {
                    putStickersToCache(type, stickerSetsNew, date, hash);
                }
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$98(this, type, stickerSetsByIdNew, stickerSetsByNameNew, stickerSetsNew, hash, date, allStickersNew, stickersByEmojiNew));
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } else if (!cache) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$99(this, type, date));
            putStickersToCache(type, null, date, 0);
        }
    }

    final /* synthetic */ void lambda$null$38$DataQuery(ArrayList res, int hash, int type) {
        if (!(res == null || hash == 0)) {
            this.loadHash[type] = hash;
        }
        loadStickers(type, false, false);
    }

    final /* synthetic */ void lambda$null$39$DataQuery(int type, LongSparseArray stickerSetsByIdNew, HashMap stickerSetsByNameNew, ArrayList stickerSetsNew, int hash, int date, HashMap allStickersNew, LongSparseArray stickersByEmojiNew) {
        int a;
        for (a = 0; a < this.stickerSets[type].size(); a++) {
            StickerSet set = ((TL_messages_stickerSet) this.stickerSets[type].get(a)).set;
            this.stickerSetsById.remove(set.var_id);
            this.installedStickerSetsById.remove(set.var_id);
            this.stickerSetsByName.remove(set.short_name);
        }
        for (a = 0; a < stickerSetsByIdNew.size(); a++) {
            this.stickerSetsById.put(stickerSetsByIdNew.keyAt(a), stickerSetsByIdNew.valueAt(a));
            if (type != 3) {
                this.installedStickerSetsById.put(stickerSetsByIdNew.keyAt(a), stickerSetsByIdNew.valueAt(a));
            }
        }
        this.stickerSetsByName.putAll(stickerSetsByNameNew);
        this.stickerSets[type] = stickerSetsNew;
        this.loadHash[type] = hash;
        this.loadDate[type] = date;
        if (type == 0) {
            this.allStickers = allStickersNew;
            this.stickersByEmoji = stickersByEmojiNew;
        } else if (type == 3) {
            this.allStickersFeatured = allStickersNew;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    final /* synthetic */ void lambda$null$40$DataQuery(int type, int date) {
        this.loadDate[type] = date;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0085  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeStickersSet(Context context, StickerSet stickerSet, int hide, BaseFragment baseFragment, boolean showSettings) {
        int type = stickerSet.masks ? 1 : 0;
        TL_inputStickerSetID stickerSetID = new TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet.access_hash;
        stickerSetID.var_id = stickerSet.var_id;
        if (hide != 0) {
            TL_messages_installStickerSet req;
            stickerSet.archived = hide == 1;
            for (int a = 0; a < this.stickerSets[type].size(); a++) {
                TL_messages_stickerSet set = (TL_messages_stickerSet) this.stickerSets[type].get(a);
                if (set.set.var_id == stickerSet.var_id) {
                    this.stickerSets[type].remove(a);
                    if (hide == 2) {
                        this.stickerSets[type].add(0, set);
                    } else {
                        this.stickerSetsById.remove(set.set.var_id);
                        this.installedStickerSetsById.remove(set.set.var_id);
                        this.stickerSetsByName.remove(set.set.short_name);
                    }
                    this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
                    putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
                    req = new TL_messages_installStickerSet();
                    req.stickerset = stickerSetID;
                    req.archived = hide != 1;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$29(this, type, hide, baseFragment, showSettings));
                    return;
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
            req = new TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            if (hide != 1) {
            }
            req.archived = hide != 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$29(this, type, hide, baseFragment, showSettings));
            return;
        }
        TL_messages_uninstallStickerSet req2 = new TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new DataQuery$$Lambda$30(this, stickerSet, context, type));
    }

    final /* synthetic */ void lambda$removeStickersSet$44$DataQuery(int type, int hide, BaseFragment baseFragment, boolean showSettings, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$95(this, response, type, hide, baseFragment, showSettings));
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$96(this, type), 1000);
    }

    final /* synthetic */ void lambda$null$42$DataQuery(TLObject response, int type, int hide, BaseFragment baseFragment, boolean showSettings) {
        if (response instanceof TL_messages_stickerSetInstallResultArchive) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(type));
            if (hide != 1 && baseFragment != null && baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), showSettings ? baseFragment : null, ((TL_messages_stickerSetInstallResultArchive) response).sets).create());
            }
        }
    }

    final /* synthetic */ void lambda$null$43$DataQuery(int type) {
        loadStickers(type, false, false);
    }

    final /* synthetic */ void lambda$removeStickersSet$46$DataQuery(StickerSet stickerSet, Context context, int type, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$94(this, error, stickerSet, context, type));
    }

    final /* synthetic */ void lambda$null$45$DataQuery(TL_error error, StickerSet stickerSet, Context context, int type) {
        if (error == null) {
            try {
                if (stickerSet.masks) {
                    Toast.makeText(context, LocaleController.getString("MasksRemoved", R.string.MasksRemoved), 0).show();
                } else {
                    Toast.makeText(context, LocaleController.getString("StickersRemoved", R.string.StickersRemoved), 0).show();
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } else {
            Toast.makeText(context, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
        loadStickers(type, false, true);
    }

    private int getMask() {
        int mask = 0;
        if (!(this.lastReturnedNum >= this.searchResultMessages.size() - 1 && this.messagesSearchEndReached[0] && this.messagesSearchEndReached[1])) {
            mask = 0 | 1;
        }
        if (this.lastReturnedNum > 0) {
            return mask | 2;
        }
        return mask;
    }

    public boolean isMessageFound(int messageId, boolean mergeDialog) {
        return this.searchResultMessagesMap[mergeDialog ? 1 : 0].indexOfKey(messageId) >= 0;
    }

    public void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, User user) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false, user);
    }

    private void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal, User user) {
        TL_messages_search req;
        int max_id = 0;
        long queryWithDialog = dialog_id;
        boolean firstQuery = !internal;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (query == null) {
            if (!this.searchResultMessages.isEmpty()) {
                MessageObject messageObject;
                if (direction == 1) {
                    this.lastReturnedNum++;
                    if (this.lastReturnedNum < this.searchResultMessages.size()) {
                        messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(this.messagesSearchCount[0] + this.messagesSearchCount[1]));
                        return;
                    } else if (this.messagesSearchEndReached[0] && mergeDialogId == 0 && this.messagesSearchEndReached[1]) {
                        this.lastReturnedNum--;
                        return;
                    } else {
                        firstQuery = false;
                        query = this.lastSearchQuery;
                        messageObject = (MessageObject) this.searchResultMessages.get(this.searchResultMessages.size() - 1);
                        if (messageObject.getDialogId() != dialog_id || this.messagesSearchEndReached[0]) {
                            if (messageObject.getDialogId() == mergeDialogId) {
                                max_id = messageObject.getId();
                            }
                            queryWithDialog = mergeDialogId;
                            this.messagesSearchEndReached[1] = false;
                        } else {
                            max_id = messageObject.getId();
                            queryWithDialog = dialog_id;
                        }
                    }
                } else if (direction == 2) {
                    this.lastReturnedNum--;
                    if (this.lastReturnedNum < 0) {
                        this.lastReturnedNum = 0;
                        return;
                    }
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(this.messagesSearchCount[0] + this.messagesSearchCount[1]));
                    return;
                } else {
                    return;
                }
            }
            return;
        } else if (firstQuery) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
            boolean[] zArr = this.messagesSearchEndReached;
            this.messagesSearchEndReached[1] = false;
            zArr[0] = false;
            int[] iArr = this.messagesSearchCount;
            this.messagesSearchCount[1] = 0;
            iArr[0] = 0;
            this.searchResultMessages.clear();
            this.searchResultMessagesMap[0].clear();
            this.searchResultMessagesMap[1].clear();
        }
        if (!(!this.messagesSearchEndReached[0] || this.messagesSearchEndReached[1] || mergeDialogId == 0)) {
            queryWithDialog = mergeDialogId;
        }
        if (queryWithDialog == dialog_id && firstQuery) {
            if (mergeDialogId != 0) {
                InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) mergeDialogId);
                if (inputPeer != null) {
                    req = new TL_messages_search();
                    req.peer = inputPeer;
                    this.lastMergeDialogId = mergeDialogId;
                    req.limit = 1;
                    req.var_q = query != null ? query : TtmlNode.ANONYMOUS_REGION_ID;
                    if (user != null) {
                        req.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                        req.flags |= 1;
                    }
                    req.filter = new TL_inputMessagesFilterEmpty();
                    this.mergeReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$31(this, mergeDialogId, req, dialog_id, guid, direction, user), 2);
                    return;
                }
                return;
            }
            this.lastMergeDialogId = 0;
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        req = new TL_messages_search();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) queryWithDialog);
        if (req.peer != null) {
            req.limit = 21;
            req.var_q = query != null ? query : TtmlNode.ANONYMOUS_REGION_ID;
            req.offset_id = max_id;
            if (user != null) {
                req.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                req.flags |= 1;
            }
            req.filter = new TL_inputMessagesFilterEmpty();
            int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.lastSearchQuery = query;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$32(this, currentReqId, req, queryWithDialog, dialog_id, guid, mergeDialogId, user), 2);
        }
    }

    final /* synthetic */ void lambda$searchMessagesInChat$48$DataQuery(long mergeDialogId, TL_messages_search req, long dialog_id, int guid, int direction, User user, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$93(this, mergeDialogId, response, req, dialog_id, guid, direction, user));
    }

    final /* synthetic */ void lambda$null$47$DataQuery(long mergeDialogId, TLObject response, TL_messages_search req, long dialog_id, int guid, int direction, User user) {
        if (this.lastMergeDialogId == mergeDialogId) {
            this.mergeReqId = 0;
            if (response != null) {
                messages_Messages res = (messages_Messages) response;
                this.messagesSearchEndReached[1] = res.messages.isEmpty();
                this.messagesSearchCount[1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                searchMessagesInChat(req.var_q, dialog_id, mergeDialogId, guid, direction, true, user);
            }
        }
    }

    final /* synthetic */ void lambda$searchMessagesInChat$50$DataQuery(int currentReqId, TL_messages_search req, long queryWithDialogFinal, long dialog_id, int guid, long mergeDialogId, User user, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$92(this, currentReqId, response, req, queryWithDialogFinal, dialog_id, guid, mergeDialogId, user));
    }

    final /* synthetic */ void lambda$null$49$DataQuery(int currentReqId, TLObject response, TL_messages_search req, long queryWithDialogFinal, long dialog_id, int guid, long mergeDialogId, User user) {
        if (currentReqId == this.lastReqId) {
            this.reqId = 0;
            if (response != null) {
                MessageObject messageObject;
                int i;
                messages_Messages res = (messages_Messages) response;
                int a = 0;
                while (a < res.messages.size()) {
                    Message message = (Message) res.messages.get(a);
                    if ((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
                        res.messages.remove(a);
                        a--;
                    }
                    a++;
                }
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
                if (req.offset_id == 0 && queryWithDialogFinal == dialog_id) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                }
                boolean added = false;
                for (a = 0; a < Math.min(res.messages.size(), 20); a++) {
                    added = true;
                    messageObject = new MessageObject(this.currentAccount, (Message) res.messages.get(a), false);
                    this.searchResultMessages.add(messageObject);
                    SparseArray[] sparseArrayArr = this.searchResultMessagesMap;
                    if (queryWithDialogFinal == dialog_id) {
                        i = 0;
                    } else {
                        i = 1;
                    }
                    sparseArrayArr[i].put(messageObject.getId(), messageObject);
                }
                this.messagesSearchEndReached[queryWithDialogFinal == dialog_id ? 0 : 1] = res.messages.size() != 21;
                int[] iArr = this.messagesSearchCount;
                i = queryWithDialogFinal == dialog_id ? 0 : 1;
                int size = ((res instanceof TL_messages_messagesSlice) || (res instanceof TL_messages_channelMessages)) ? res.count : res.messages.size();
                iArr[i] = size;
                if (this.searchResultMessages.isEmpty()) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(0), Integer.valueOf(getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                } else if (added) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(this.messagesSearchCount[0] + this.messagesSearchCount[1]));
                }
                if (queryWithDialogFinal == dialog_id && this.messagesSearchEndReached[0] && mergeDialogId != 0 && !this.messagesSearchEndReached[1]) {
                    searchMessagesInChat(this.lastSearchQuery, dialog_id, mergeDialogId, guid, 0, true, user);
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    public void loadMedia(long uid, int count, int max_id, int type, int fromCache, int classGuid) {
        boolean isChannel = ((int) uid) < 0 && ChatObject.isChannel(-((int) uid), this.currentAccount);
        int lower_part = (int) uid;
        if (fromCache != 0 || lower_part == 0) {
            loadMediaDatabase(uid, count, max_id, type, classGuid, isChannel, fromCache);
            return;
        }
        TL_messages_search req = new TL_messages_search();
        req.limit = count + 1;
        req.offset_id = max_id;
        if (type == 0) {
            req.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (type == 1) {
            req.filter = new TL_inputMessagesFilterDocument();
        } else if (type == 2) {
            req.filter = new TL_inputMessagesFilterRoundVoice();
        } else if (type == 3) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == 4) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.var_q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
        if (req.peer != null) {
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$33(this, count, uid, max_id, type, classGuid, isChannel)), classGuid);
        }
    }

    final /* synthetic */ void lambda$loadMedia$51$DataQuery(int count, long uid, int max_id, int type, int classGuid, boolean isChannel, TLObject response, TL_error error) {
        if (error == null) {
            boolean topReached;
            messages_Messages res = (messages_Messages) response;
            if (res.messages.size() > count) {
                topReached = false;
                res.messages.remove(res.messages.size() - 1);
            } else {
                topReached = true;
            }
            processLoadedMedia(res, uid, count, max_id, type, 0, classGuid, isChannel, topReached);
        }
    }

    public void getMediaCounts(long uid, int classGuid) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$34(this, uid, classGuid));
    }

    final /* synthetic */ void lambda$getMediaCounts$56$DataQuery(long uid, int classGuid) {
        try {
            int type;
            int[] counts = new int[]{-1, -1, -1, -1, -1};
            int[] countsFinal = new int[]{-1, -1, -1, -1, -1};
            int[] iArr = new int[5];
            iArr = new int[]{0, 0, 0, 0, 0};
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", new Object[]{Long.valueOf(uid)}), new Object[0]);
            while (cursor.next()) {
                type = cursor.intValue(0);
                if (type >= 0 && type < 5) {
                    int intValue = cursor.intValue(1);
                    counts[type] = intValue;
                    countsFinal[type] = intValue;
                    iArr[type] = cursor.intValue(2);
                }
            }
            cursor.dispose();
            int lower_part = (int) uid;
            int a;
            if (lower_part == 0) {
                for (a = 0; a < counts.length; a++) {
                    if (counts[a] == -1) {
                        cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(a)}), new Object[0]);
                        if (cursor.next()) {
                            counts[a] = cursor.intValue(0);
                        } else {
                            counts[a] = 0;
                        }
                        cursor.dispose();
                        putMediaCountDatabase(uid, a, counts[a]);
                    }
                }
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$88(this, uid, counts));
                return;
            }
            boolean missing = false;
            a = 0;
            while (a < counts.length) {
                if (counts[a] == -1 || iArr[a] == 1) {
                    type = a;
                    TLObject req = new TL_messages_search();
                    req.limit = 1;
                    req.offset_id = 0;
                    if (a == 0) {
                        req.filter = new TL_inputMessagesFilterPhotoVideo();
                    } else if (a == 1) {
                        req.filter = new TL_inputMessagesFilterDocument();
                    } else if (a == 2) {
                        req.filter = new TL_inputMessagesFilterRoundVoice();
                    } else if (a == 3) {
                        req.filter = new TL_inputMessagesFilterUrl();
                    } else if (a == 4) {
                        req.filter = new TL_inputMessagesFilterMusic();
                    }
                    req.var_q = TtmlNode.ANONYMOUS_REGION_ID;
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
                    if (req.peer == null) {
                        counts[a] = 0;
                    } else {
                        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$89(this, counts, type, uid)), classGuid);
                        if (counts[a] == -1) {
                            missing = true;
                        } else if (iArr[a] == 1) {
                            counts[a] = -1;
                        }
                    }
                }
                a++;
            }
            if (!missing) {
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$90(this, uid, countsFinal));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$52$DataQuery(long uid, int[] counts) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), counts);
    }

    final /* synthetic */ void lambda$null$54$DataQuery(int[] counts, int type, long uid, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages res = (messages_Messages) response;
            if (res instanceof TL_messages_messages) {
                counts[type] = res.messages.size();
            } else {
                counts[type] = res.count;
            }
            putMediaCountDatabase(uid, type, counts[type]);
        } else {
            counts[type] = 0;
        }
        boolean finished = true;
        for (int i : counts) {
            if (i == -1) {
                finished = false;
                break;
            }
        }
        if (finished) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$91(this, uid, counts));
        }
    }

    final /* synthetic */ void lambda$null$53$DataQuery(long uid, int[] counts) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), counts);
    }

    final /* synthetic */ void lambda$null$55$DataQuery(long uid, int[] countsFinal) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(uid), countsFinal);
    }

    public void getMediaCount(long uid, int type, int classGuid, boolean fromCache) {
        int lower_part = (int) uid;
        if (fromCache || lower_part == 0) {
            getMediaCountDatabase(uid, type, classGuid);
            return;
        }
        TL_messages_search req = new TL_messages_search();
        req.limit = 1;
        req.offset_id = 0;
        if (type == 0) {
            req.filter = new TL_inputMessagesFilterPhotoVideo();
        } else if (type == 1) {
            req.filter = new TL_inputMessagesFilterDocument();
        } else if (type == 2) {
            req.filter = new TL_inputMessagesFilterRoundVoice();
        } else if (type == 3) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == 4) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.var_q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
        if (req.peer != null) {
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$35(this, uid, type, classGuid)), classGuid);
        }
    }

    final /* synthetic */ void lambda$getMediaCount$58$DataQuery(long uid, int type, int classGuid, TLObject response, TL_error error) {
        if (error == null) {
            int count;
            messages_Messages res = (messages_Messages) response;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            if (res instanceof TL_messages_messages) {
                count = res.messages.size();
            } else {
                count = res.count;
            }
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$87(this, res));
            processLoadedMediaCount(count, uid, type, classGuid, false, 0);
        }
    }

    final /* synthetic */ void lambda$null$57$DataQuery(messages_Messages res) {
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TL_messageMediaPhoto) {
            return 0;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            if (MessageObject.isVoiceMessage(message) || MessageObject.isRoundVideoMessage(message)) {
                return 2;
            }
            if (MessageObject.isVideoMessage(message)) {
                return 0;
            }
            if (MessageObject.isStickerMessage(message)) {
                return -1;
            }
            if (MessageObject.isMusicMessage(message)) {
                return 4;
            }
            return 1;
        } else if (message.entities.isEmpty()) {
            return -1;
        } else {
            for (int a = 0; a < message.entities.size(); a++) {
                MessageEntity entity = (MessageEntity) message.entities.get(a);
                if ((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl) || (entity instanceof TL_messageEntityEmail)) {
                    return 3;
                }
            }
            return -1;
        }
    }

    public static boolean canAddMessageToMedia(Message message) {
        if ((message instanceof TL_message_secret) && (((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && message.media.ttl_seconds != 0 && message.media.ttl_seconds <= 60)) {
            return false;
        }
        if (!(message instanceof TL_message_secret) && (message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return false;
        }
        if ((message.media instanceof TL_messageMediaPhoto) || ((message.media instanceof TL_messageMediaDocument) && !MessageObject.isGifDocument(message.media.document))) {
            return true;
        }
        if (message.entities.isEmpty()) {
            return false;
        }
        for (int a = 0; a < message.entities.size(); a++) {
            MessageEntity entity = (MessageEntity) message.entities.get(a);
            if ((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl) || (entity instanceof TL_messageEntityEmail)) {
                return true;
            }
        }
        return false;
    }

    private void processLoadedMedia(messages_Messages res, long uid, int count, int max_id, int type, int fromCache, int classGuid, boolean isChannel, boolean topReached) {
        int lower_part = (int) uid;
        if (fromCache == 0 || !res.messages.isEmpty() || lower_part == 0) {
            int a;
            if (fromCache == 0) {
                ImageLoader.saveMessagesThumbs(res.messages);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                putMediaDatabase(uid, type, res.messages, max_id, topReached);
            }
            SparseArray<User> usersDict = new SparseArray();
            for (a = 0; a < res.users.size(); a++) {
                User u = (User) res.users.get(a);
                usersDict.put(u.var_id, u);
            }
            ArrayList<MessageObject> objects = new ArrayList();
            for (a = 0; a < res.messages.size(); a++) {
                objects.add(new MessageObject(this.currentAccount, (Message) res.messages.get(a), (SparseArray) usersDict, true));
            }
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$36(this, res, fromCache, uid, objects, classGuid, type, topReached));
        } else if (fromCache != 2) {
            loadMedia(uid, count, max_id, type, 0, classGuid);
        }
    }

    final /* synthetic */ void lambda$processLoadedMedia$59$DataQuery(messages_Messages res, int fromCache, long uid, ArrayList objects, int classGuid, int type, boolean topReached) {
        boolean z;
        int totalCount = res.count;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        ArrayList arrayList = res.users;
        if (fromCache != 0) {
            z = true;
        } else {
            z = false;
        }
        instance.putUsers(arrayList, z);
        instance = MessagesController.getInstance(this.currentAccount);
        arrayList = res.chats;
        if (fromCache != 0) {
            z = true;
        } else {
            z = false;
        }
        instance.putChats(arrayList, z);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoad, Long.valueOf(uid), Integer.valueOf(totalCount), objects, Integer.valueOf(classGuid), Integer.valueOf(type), Boolean.valueOf(topReached));
    }

    private void processLoadedMediaCount(int count, long uid, int type, int classGuid, boolean fromCache, int old) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$37(this, uid, fromCache, count, type, old, classGuid));
    }

    final /* synthetic */ void lambda$processLoadedMediaCount$60$DataQuery(long uid, boolean fromCache, int count, int type, int old, int classGuid) {
        int lower_part = (int) uid;
        boolean reload = fromCache && ((count == -1 || (count == 0 && type == 2)) && lower_part != 0);
        if (reload || (old == 1 && lower_part != 0)) {
            getMediaCount(uid, type, classGuid, false);
        }
        if (!reload) {
            if (!fromCache) {
                putMediaCountDatabase(uid, type, count);
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
            int i = NotificationCenter.mediaCountDidLoad;
            Object[] objArr = new Object[4];
            objArr[0] = Long.valueOf(uid);
            if (fromCache && count == -1) {
                count = 0;
            }
            objArr[1] = Integer.valueOf(count);
            objArr[2] = Boolean.valueOf(fromCache);
            objArr[3] = Integer.valueOf(type);
            instance.postNotificationName(i, objArr);
        }
    }

    private void putMediaCountDatabase(long uid, int type, int count) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$38(this, uid, type, count));
    }

    final /* synthetic */ void lambda$putMediaCountDatabase$61$DataQuery(long uid, int type, int count) {
        try {
            SQLitePreparedStatement state2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            state2.requery();
            state2.bindLong(1, uid);
            state2.bindInteger(2, type);
            state2.bindInteger(3, count);
            state2.bindInteger(4, 0);
            state2.step();
            state2.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void getMediaCountDatabase(long uid, int type, int classGuid) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$39(this, uid, type, classGuid));
    }

    final /* synthetic */ void lambda$getMediaCountDatabase$62$DataQuery(long uid, int type, int classGuid) {
        int count = -1;
        int old = 0;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
            if (cursor.next()) {
                count = cursor.intValue(0);
                old = cursor.intValue(1);
            }
            cursor.dispose();
            int lower_part = (int) uid;
            if (count == -1 && lower_part == 0) {
                cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                if (cursor.next()) {
                    count = cursor.intValue(0);
                }
                cursor.dispose();
                if (count != -1) {
                    putMediaCountDatabase(uid, type, count);
                }
            }
            processLoadedMediaCount(count, uid, type, classGuid, true, old);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void loadMediaDatabase(long uid, int count, int max_id, int type, int classGuid, boolean isChannel, int fromCache) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$40(this, count, uid, max_id, isChannel, type, fromCache, classGuid));
    }

    final /* synthetic */ void lambda$loadMediaDatabase$63$DataQuery(int count, long uid, int max_id, boolean isChannel, int type, int fromCache, int classGuid) {
        TL_messages_messages res = new TL_messages_messages();
        try {
            SQLiteCursor cursor;
            boolean topReached;
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            int countToLoad = count + 1;
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            boolean isEnd = false;
            if (((int) uid) != 0) {
                int channelId = 0;
                long messageMaxId = (long) max_id;
                if (isChannel) {
                    channelId = -((int) uid);
                }
                if (!(messageMaxId == 0 || channelId == 0)) {
                    messageMaxId |= ((long) channelId) << 32;
                }
                cursor = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                if (cursor.next()) {
                    isEnd = cursor.intValue(0) == 1;
                    cursor.dispose();
                } else {
                    cursor.dispose();
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                    if (cursor.next()) {
                        int mid = cursor.intValue(0);
                        if (mid != 0) {
                            SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                            state.requery();
                            state.bindLong(1, uid);
                            state.bindInteger(2, type);
                            state.bindInteger(3, 0);
                            state.bindInteger(4, mid);
                            state.step();
                            state.dispose();
                        }
                    }
                    cursor.dispose();
                }
                long holeMessageId;
                if (messageMaxId != 0) {
                    holeMessageId = 0;
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(max_id)}), new Object[0]);
                    if (cursor.next()) {
                        holeMessageId = (long) cursor.intValue(0);
                        if (channelId != 0) {
                            holeMessageId |= ((long) channelId) << 32;
                        }
                    }
                    cursor.dispose();
                    if (holeMessageId > 1) {
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(messageMaxId), Long.valueOf(holeMessageId), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                    } else {
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(messageMaxId), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                    }
                } else {
                    holeMessageId = 0;
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type)}), new Object[0]);
                    if (cursor.next()) {
                        holeMessageId = (long) cursor.intValue(0);
                        if (channelId != 0) {
                            holeMessageId |= ((long) channelId) << 32;
                        }
                    }
                    cursor.dispose();
                    if (holeMessageId > 1) {
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Long.valueOf(holeMessageId), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                    } else {
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                    }
                }
            } else {
                isEnd = true;
                if (max_id != 0) {
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(max_id), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                } else {
                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(uid), Integer.valueOf(type), Integer.valueOf(countToLoad)}), new Object[0]);
                }
            }
            while (cursor.next()) {
                AbstractSerializedData data = cursor.byteBufferValue(0);
                if (data != null) {
                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    message.var_id = cursor.intValue(1);
                    message.dialog_id = uid;
                    if (((int) uid) == 0) {
                        message.random_id = cursor.longValue(2);
                    }
                    res.messages.add(message);
                    MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), res.users);
            }
            if (!chatsToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), res.chats);
            }
            if (res.messages.size() > count) {
                topReached = false;
                res.messages.remove(res.messages.size() - 1);
            } else {
                topReached = isEnd;
            }
            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, topReached);
        } catch (Throwable e) {
            res.messages.clear();
            res.chats.clear();
            res.users.clear();
            FileLog.m13e(e);
            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, false);
        } catch (Throwable th) {
            Throwable th2 = th;
            processLoadedMedia(res, uid, count, max_id, type, fromCache, classGuid, isChannel, false);
        }
    }

    private void putMediaDatabase(long uid, int type, ArrayList<Message> messages, int max_id, boolean topReached) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$41(this, messages, topReached, uid, max_id, type));
    }

    final /* synthetic */ void lambda$putMediaDatabase$64$DataQuery(ArrayList messages, boolean topReached, long uid, int max_id, int type) {
        try {
            if (messages.isEmpty() || topReached) {
                MessagesStorage.getInstance(this.currentAccount).doneHolesInMedia(uid, max_id, type);
                if (messages.isEmpty()) {
                    return;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                Message message = (Message) it.next();
                if (canAddMessageToMedia(message)) {
                    long messageId = (long) message.var_id;
                    if (message.to_id.channel_id != 0) {
                        messageId |= ((long) message.to_id.channel_id) << 32;
                    }
                    state2.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state2.bindLong(1, messageId);
                    state2.bindLong(2, uid);
                    state2.bindInteger(3, message.date);
                    state2.bindInteger(4, type);
                    state2.bindByteBuffer(5, data);
                    state2.step();
                    data.reuse();
                }
            }
            state2.dispose();
            if (!(topReached && max_id == 0)) {
                int minId;
                if (topReached) {
                    minId = 1;
                } else {
                    minId = ((Message) messages.get(messages.size() - 1)).var_id;
                }
                if (max_id != 0) {
                    MessagesStorage.getInstance(this.currentAccount).closeHolesInMedia(uid, minId, max_id, type);
                } else {
                    MessagesStorage.getInstance(this.currentAccount).closeHolesInMedia(uid, minId, ConnectionsManager.DEFAULT_DATACENTER_ID, type);
                }
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void loadMusic(long uid, long max_id) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$42(this, uid, max_id));
    }

    final /* synthetic */ void lambda$loadMusic$66$DataQuery(long uid, long max_id) {
        SQLiteCursor cursor;
        ArrayList<MessageObject> arrayList = new ArrayList();
        if (((int) uid) != 0) {
            try {
                cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(uid), Long.valueOf(max_id), Integer.valueOf(4)}), new Object[0]);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } else {
            cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(uid), Long.valueOf(max_id), Integer.valueOf(4)}), new Object[0]);
        }
        while (cursor.next()) {
            NativeByteBuffer data = cursor.byteBufferValue(0);
            if (data != null) {
                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                data.reuse();
                if (MessageObject.isMusicMessage(message)) {
                    message.var_id = cursor.intValue(1);
                    message.dialog_id = uid;
                    arrayList.add(0, new MessageObject(this.currentAccount, message, false));
                }
            }
        }
        cursor.dispose();
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$86(this, uid, arrayList));
    }

    final /* synthetic */ void lambda$null$65$DataQuery(long uid, ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(uid), arrayList);
    }

    public void buildShortcuts() {
        if (VERSION.SDK_INT >= 25) {
            ArrayList<TL_topPeer> hintsFinal = new ArrayList();
            for (int a = 0; a < this.hints.size(); a++) {
                hintsFinal.add(this.hints.get(a));
                if (hintsFinal.size() == 3) {
                    break;
                }
            }
            Utilities.globalQueue.postRunnable(new DataQuery$$Lambda$43(this, hintsFinal));
        }
    }

    final /* synthetic */ void lambda$buildShortcuts$67$DataQuery(ArrayList hintsFinal) {
        try {
            int a;
            TL_topPeer hint;
            long did;
            String id;
            ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
            List<ShortcutInfo> currentShortcuts = shortcutManager.getDynamicShortcuts();
            ArrayList<String> shortcutsToUpdate = new ArrayList();
            ArrayList<String> newShortcutsIds = new ArrayList();
            ArrayList<String> shortcutsToDelete = new ArrayList();
            if (!(currentShortcuts == null || currentShortcuts.isEmpty())) {
                newShortcutsIds.add("compose");
                for (a = 0; a < hintsFinal.size(); a++) {
                    hint = (TL_topPeer) hintsFinal.get(a);
                    if (hint.peer.user_id != 0) {
                        did = (long) hint.peer.user_id;
                    } else {
                        did = (long) (-hint.peer.chat_id);
                        if (did == 0) {
                            did = (long) (-hint.peer.channel_id);
                        }
                    }
                    newShortcutsIds.add("did" + did);
                }
                for (a = 0; a < currentShortcuts.size(); a++) {
                    id = ((ShortcutInfo) currentShortcuts.get(a)).getId();
                    if (!newShortcutsIds.remove(id)) {
                        shortcutsToDelete.add(id);
                    }
                    shortcutsToUpdate.add(id);
                }
                if (newShortcutsIds.isEmpty() && shortcutsToDelete.isEmpty()) {
                    return;
                }
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("new_dialog");
            ArrayList<ShortcutInfo> arrayList = new ArrayList();
            arrayList.add(new Builder(ApplicationLoader.applicationContext, "compose").setShortLabel(LocaleController.getString("NewConversationShortcut", R.string.NewConversationShortcut)).setLongLabel(LocaleController.getString("NewConversationShortcut", R.string.NewConversationShortcut)).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_compose)).setIntent(intent).build());
            if (shortcutsToUpdate.contains("compose")) {
                shortcutManager.updateShortcuts(arrayList);
            } else {
                shortcutManager.addDynamicShortcuts(arrayList);
            }
            arrayList.clear();
            if (!shortcutsToDelete.isEmpty()) {
                shortcutManager.removeDynamicShortcuts(shortcutsToDelete);
            }
            for (a = 0; a < hintsFinal.size(); a++) {
                intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                hint = (TL_topPeer) hintsFinal.get(a);
                User user = null;
                Chat chat = null;
                if (hint.peer.user_id != 0) {
                    intent.putExtra("userId", hint.peer.user_id);
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(hint.peer.user_id));
                    did = (long) hint.peer.user_id;
                } else {
                    int chat_id = hint.peer.chat_id;
                    if (chat_id == 0) {
                        chat_id = hint.peer.channel_id;
                    }
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(chat_id));
                    intent.putExtra("chatId", chat_id);
                    did = (long) (-chat_id);
                }
                if (user != null || chat != null) {
                    String name;
                    TLObject photo = null;
                    if (user != null) {
                        name = ContactsController.formatName(user.first_name, user.last_name);
                        if (user.photo != null) {
                            photo = user.photo.photo_small;
                        }
                    } else {
                        name = chat.title;
                        if (chat.photo != null) {
                            photo = chat.photo.photo_small;
                        }
                    }
                    intent.putExtra("currentAccount", this.currentAccount);
                    intent.setAction("com.tmessages.openchat" + did);
                    intent.addFlags(ConnectionsManager.FileTypeFile);
                    Bitmap bitmap = null;
                    if (photo != null) {
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                        if (bitmap != null) {
                            int size = AndroidUtilities.m9dp(48.0f);
                            Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                            Canvas canvas = new Canvas(result);
                            if (roundPaint == null) {
                                roundPaint = new Paint(3);
                                bitmapRect = new RectF();
                                erasePaint = new Paint(1);
                                erasePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                                roundPath = new Path();
                                roundPath.addCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.m9dp(2.0f)), Direction.CW);
                                roundPath.toggleInverseFillType();
                            }
                            bitmapRect.set((float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(46.0f), (float) AndroidUtilities.m9dp(46.0f));
                            canvas.drawBitmap(bitmap, null, bitmapRect, roundPaint);
                            canvas.drawPath(roundPath, erasePaint);
                            try {
                                canvas.setBitmap(null);
                            } catch (Exception e) {
                            }
                            bitmap = result;
                        }
                    }
                    id = "did" + did;
                    if (TextUtils.isEmpty(name)) {
                        name = " ";
                    }
                    Builder builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(name).setLongLabel(name).setIntent(intent);
                    if (bitmap != null) {
                        builder.setIcon(Icon.createWithBitmap(bitmap));
                    } else {
                        builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                    }
                    arrayList.add(builder.build());
                    if (shortcutsToUpdate.contains(id)) {
                        shortcutManager.updateShortcuts(arrayList);
                    } else {
                        shortcutManager.addDynamicShortcuts(arrayList);
                    }
                    arrayList.clear();
                }
            }
        } catch (Throwable th) {
        }
    }

    public void loadHints(boolean cache) {
        if (!this.loading && UserConfig.getInstance(this.currentAccount).suggestContacts) {
            if (!cache) {
                this.loading = true;
                TL_contacts_getTopPeers req = new TL_contacts_getTopPeers();
                req.hash = 0;
                req.bots_pm = false;
                req.correspondents = true;
                req.groups = false;
                req.channels = false;
                req.bots_inline = true;
                req.offset = 0;
                req.limit = 20;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$45(this));
            } else if (!this.loaded) {
                this.loading = true;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$44(this));
                this.loaded = true;
            }
        }
    }

    final /* synthetic */ void lambda$loadHints$69$DataQuery() {
        ArrayList<TL_topPeer> hintsNew = new ArrayList();
        ArrayList<TL_topPeer> inlineBotsNew = new ArrayList();
        ArrayList<User> users = new ArrayList();
        ArrayList<Chat> chats = new ArrayList();
        int selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        try {
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (cursor.next()) {
                int did = cursor.intValue(0);
                if (did != selfUserId) {
                    int type = cursor.intValue(1);
                    TL_topPeer peer = new TL_topPeer();
                    peer.rating = cursor.doubleValue(2);
                    if (did > 0) {
                        peer.peer = new TL_peerUser();
                        peer.peer.user_id = did;
                        usersToLoad.add(Integer.valueOf(did));
                    } else {
                        peer.peer = new TL_peerChat();
                        peer.peer.chat_id = -did;
                        chatsToLoad.add(Integer.valueOf(-did));
                    }
                    if (type == 0) {
                        hintsNew.add(peer);
                    } else if (type == 1) {
                        inlineBotsNew.add(peer);
                    }
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!chatsToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$85(this, users, chats, hintsNew, inlineBotsNew));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$68$DataQuery(ArrayList users, ArrayList chats, ArrayList hintsNew, ArrayList inlineBotsNew) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        this.loading = false;
        this.loaded = true;
        this.hints = hintsNew;
        this.inlineBots = inlineBotsNew;
        buildShortcuts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(UserConfig.getInstance(this.currentAccount).lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    final /* synthetic */ void lambda$loadHints$74$DataQuery(TLObject response, TL_error error) {
        if (response instanceof TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$81(this, response));
        } else if (response instanceof TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$82(this));
        }
    }

    final /* synthetic */ void lambda$null$72$DataQuery(TLObject response) {
        TL_contacts_topPeers topPeers = (TL_contacts_topPeers) response;
        MessagesController.getInstance(this.currentAccount).putUsers(topPeers.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(topPeers.chats, false);
        for (int a = 0; a < topPeers.categories.size(); a++) {
            TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) topPeers.categories.get(a);
            if (category.category instanceof TL_topPeerCategoryBotsInline) {
                this.inlineBots = category.peers;
                UserConfig.getInstance(this.currentAccount).botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = category.peers;
                int selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                for (int b = 0; b < this.hints.size(); b++) {
                    if (((TL_topPeer) this.hints.get(b)).peer.user_id == selfUserId) {
                        this.hints.remove(b);
                        break;
                    }
                }
                UserConfig.getInstance(this.currentAccount).ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        buildShortcuts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$83(this, topPeers));
    }

    final /* synthetic */ void lambda$null$71$DataQuery(TL_contacts_topPeers topPeers) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(topPeers.users, topPeers.chats, false, false);
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int a = 0; a < topPeers.categories.size(); a++) {
                int type;
                TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) topPeers.categories.get(a);
                if (category.category instanceof TL_topPeerCategoryBotsInline) {
                    type = 1;
                } else {
                    type = 0;
                }
                for (int b = 0; b < category.peers.size(); b++) {
                    int did;
                    TL_topPeer peer = (TL_topPeer) category.peers.get(b);
                    if (peer.peer instanceof TL_peerUser) {
                        did = peer.peer.user_id;
                    } else if (peer.peer instanceof TL_peerChat) {
                        did = -peer.peer.chat_id;
                    } else {
                        did = -peer.peer.channel_id;
                    }
                    state.requery();
                    state.bindInteger(1, did);
                    state.bindInteger(2, type);
                    state.bindDouble(3, peer.rating);
                    state.bindInteger(4, 0);
                    state.step();
                }
            }
            state.dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$84(this));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$70$DataQuery() {
        UserConfig.getInstance(this.currentAccount).suggestContacts = true;
        UserConfig.getInstance(this.currentAccount).lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    final /* synthetic */ void lambda$null$73$DataQuery() {
        UserConfig.getInstance(this.currentAccount).suggestContacts = false;
        UserConfig.getInstance(this.currentAccount).lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        clearTopPeers();
    }

    public void clearTopPeers() {
        this.hints.clear();
        this.inlineBots.clear();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$46(this));
        buildShortcuts();
    }

    final /* synthetic */ void lambda$clearTopPeers$75$DataQuery() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception e) {
        }
    }

    public void increaseInlineRaiting(int uid) {
        if (UserConfig.getInstance(this.currentAccount).suggestContacts) {
            int dt;
            if (UserConfig.getInstance(this.currentAccount).botRatingLoadTime != 0) {
                dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - UserConfig.getInstance(this.currentAccount).botRatingLoadTime);
            } else {
                dt = 60;
            }
            TL_topPeer peer = null;
            for (int a = 0; a < this.inlineBots.size(); a++) {
                TL_topPeer p = (TL_topPeer) this.inlineBots.get(a);
                if (p.peer.user_id == uid) {
                    peer = p;
                    break;
                }
            }
            if (peer == null) {
                peer = new TL_topPeer();
                peer.peer = new TL_peerUser();
                peer.peer.user_id = uid;
                this.inlineBots.add(peer);
            }
            peer.rating += Math.exp((double) (dt / MessagesController.getInstance(this.currentAccount).ratingDecay));
            Collections.sort(this.inlineBots, DataQuery$$Lambda$47.$instance);
            if (this.inlineBots.size() > 20) {
                this.inlineBots.remove(this.inlineBots.size() - 1);
            }
            savePeer(uid, 1, peer.rating);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    static final /* synthetic */ int lambda$increaseInlineRaiting$76$DataQuery(TL_topPeer lhs, TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    public void removeInline(int uid) {
        for (int a = 0; a < this.inlineBots.size(); a++) {
            if (((TL_topPeer) this.inlineBots.get(a)).peer.user_id == uid) {
                this.inlineBots.remove(a);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryBotsInline();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(uid);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, DataQuery$$Lambda$48.$instance);
                deletePeer(uid, 1);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    static final /* synthetic */ void lambda$removeInline$77$DataQuery(TLObject response, TL_error error) {
    }

    public void removePeer(int uid) {
        for (int a = 0; a < this.hints.size(); a++) {
            if (((TL_topPeer) this.hints.get(a)).peer.user_id == uid) {
                this.hints.remove(a);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryCorrespondents();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(uid);
                deletePeer(uid, 0);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, DataQuery$$Lambda$49.$instance);
                return;
            }
        }
    }

    static final /* synthetic */ void lambda$removePeer$78$DataQuery(TLObject response, TL_error error) {
    }

    public void increasePeerRaiting(long did) {
        if (UserConfig.getInstance(this.currentAccount).suggestContacts) {
            int lower_id = (int) did;
            if (lower_id > 0) {
                User user = lower_id > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id)) : null;
                if (user != null && !user.bot && !user.self) {
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$50(this, did, lower_id));
                }
            }
        }
    }

    final /* synthetic */ void lambda$increasePeerRaiting$81$DataQuery(long did, int lower_id) {
        double dt = 0.0d;
        int lastTime = 0;
        int lastMid = 0;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(did)}), new Object[0]);
            if (cursor.next()) {
                lastMid = cursor.intValue(0);
                lastTime = cursor.intValue(1);
            }
            cursor.dispose();
            if (lastMid > 0 && UserConfig.getInstance(this.currentAccount).ratingLoadTime != 0) {
                dt = (double) (lastTime - UserConfig.getInstance(this.currentAccount).ratingLoadTime);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$79(this, lower_id, dt, did));
    }

    final /* synthetic */ void lambda$null$80$DataQuery(int lower_id, double dtFinal, long did) {
        TL_topPeer peer = null;
        for (int a = 0; a < this.hints.size(); a++) {
            TL_topPeer p = (TL_topPeer) this.hints.get(a);
            if ((lower_id < 0 && (p.peer.chat_id == (-lower_id) || p.peer.channel_id == (-lower_id))) || (lower_id > 0 && p.peer.user_id == lower_id)) {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            peer = new TL_topPeer();
            if (lower_id > 0) {
                peer.peer = new TL_peerUser();
                peer.peer.user_id = lower_id;
            } else {
                peer.peer = new TL_peerChat();
                peer.peer.chat_id = -lower_id;
            }
            this.hints.add(peer);
        }
        peer.rating += Math.exp(dtFinal / ((double) MessagesController.getInstance(this.currentAccount).ratingDecay));
        Collections.sort(this.hints, DataQuery$$Lambda$80.$instance);
        savePeer((int) did, 0, peer.rating);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    static final /* synthetic */ int lambda$null$79$DataQuery(TL_topPeer lhs, TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    private void savePeer(int did, int type, double rating) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$51(this, did, type, rating));
    }

    final /* synthetic */ void lambda$savePeer$82$DataQuery(int did, int type, double rating) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            state.requery();
            state.bindInteger(1, did);
            state.bindInteger(2, type);
            state.bindDouble(3, rating);
            state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            state.step();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void deletePeer(int did, int type) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$52(this, did, type));
    }

    final /* synthetic */ void lambda$deletePeer$83$DataQuery(int did, int type) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private Intent createIntrnalShortcutIntent(long did) {
        Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int lower_id = (int) did;
        int high_id = (int) (did >> 32);
        if (lower_id == 0) {
            shortcutIntent.putExtra("encId", high_id);
            if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id)) == null) {
                return null;
            }
        } else if (lower_id > 0) {
            shortcutIntent.putExtra("userId", lower_id);
        } else if (lower_id >= 0) {
            return null;
        } else {
            shortcutIntent.putExtra("chatId", -lower_id);
        }
        shortcutIntent.putExtra("currentAccount", this.currentAccount);
        shortcutIntent.setAction("com.tmessages.openchat" + did);
        shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
        return shortcutIntent;
    }

    public void installShortcut(long did) {
        try {
            Intent shortcutIntent = createIntrnalShortcutIntent(did);
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            User user = null;
            Chat chat = null;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            } else if (lower_id < 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            } else {
                return;
            }
            if (user != null || chat != null) {
                String name;
                TLObject photo = null;
                boolean selfUser = false;
                if (user == null) {
                    name = chat.title;
                    if (chat.photo != null) {
                        photo = chat.photo.photo_small;
                    }
                } else if (UserObject.isUserSelf(user)) {
                    name = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                    selfUser = true;
                } else {
                    name = ContactsController.formatName(user.first_name, user.last_name);
                    if (user.photo != null) {
                        photo = user.photo.photo_small;
                    }
                }
                Bitmap bitmap = null;
                if (selfUser || photo != null) {
                    if (!selfUser) {
                        try {
                            bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                    if (selfUser || bitmap != null) {
                        int size = AndroidUtilities.m9dp(58.0f);
                        Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                        result.eraseColor(0);
                        Canvas canvas = new Canvas(result);
                        if (selfUser) {
                            AvatarDrawable avatarDrawable = new AvatarDrawable(user);
                            avatarDrawable.setSavedMessages(1);
                            avatarDrawable.setBounds(0, 0, size, size);
                            avatarDrawable.draw(canvas);
                        } else {
                            Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                            if (roundPaint == null) {
                                roundPaint = new Paint(1);
                                bitmapRect = new RectF();
                            }
                            float scale = ((float) size) / ((float) bitmap.getWidth());
                            canvas.save();
                            canvas.scale(scale, scale);
                            roundPaint.setShader(bitmapShader);
                            bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                            canvas.drawRoundRect(bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), roundPaint);
                            canvas.restore();
                        }
                        Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.book_logo);
                        int w = AndroidUtilities.m9dp(15.0f);
                        int left = (size - w) - AndroidUtilities.m9dp(2.0f);
                        int top = (size - w) - AndroidUtilities.m9dp(2.0f);
                        drawable.setBounds(left, top, left + w, top + w);
                        drawable.draw(canvas);
                        try {
                            canvas.setBitmap(null);
                        } catch (Exception e2) {
                        }
                        bitmap = result;
                    }
                }
                if (VERSION.SDK_INT >= 26) {
                    Builder pinShortcutInfo = new Builder(ApplicationLoader.applicationContext, "sdid_" + did).setShortLabel(name).setIntent(shortcutIntent);
                    if (bitmap != null) {
                        pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap));
                    } else if (user != null) {
                        if (user.bot) {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                        } else {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                        }
                    } else if (chat != null) {
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                        } else {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_channel));
                        }
                    }
                    ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                    return;
                }
                Intent addIntent = new Intent();
                if (bitmap != null) {
                    addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap);
                } else if (user != null) {
                    if (user.bot) {
                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                    } else {
                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                    }
                } else if (chat != null) {
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                    } else {
                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_channel));
                    }
                }
                addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                addIntent.putExtra("duplicate", false);
                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
            }
        } catch (Throwable e3) {
            FileLog.m13e(e3);
        }
    }

    public void uninstallShortcut(long did) {
        try {
            if (VERSION.SDK_INT >= 26) {
                ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                ArrayList<String> arrayList = new ArrayList();
                arrayList.add("sdid_" + did);
                shortcutManager.removeDynamicShortcuts(arrayList);
                return;
            }
            int lower_id = (int) did;
            int high_id = (int) (did >> 32);
            User user = null;
            Chat chat = null;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            } else if (lower_id < 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            } else {
                return;
            }
            if (user != null || chat != null) {
                String name;
                if (user != null) {
                    name = ContactsController.formatName(user.first_name, user.last_name);
                } else {
                    name = chat.title;
                }
                Intent addIntent = new Intent();
                addIntent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(did));
                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                addIntent.putExtra("duplicate", false);
                addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ int lambda$static$84$DataQuery(MessageEntity entity1, MessageEntity entity2) {
        if (entity1.offset > entity2.offset) {
            return 1;
        }
        if (entity1.offset < entity2.offset) {
            return -1;
        }
        return 0;
    }

    public MessageObject loadPinnedMessage(long dialogId, int channelId, int mid, boolean useQueue) {
        if (!useQueue) {
            return loadPinnedMessageInternal(dialogId, channelId, mid, true);
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$53(this, dialogId, channelId, mid));
        return null;
    }

    final /* synthetic */ void lambda$loadPinnedMessage$85$DataQuery(long dialogId, int channelId, int mid) {
        loadPinnedMessageInternal(dialogId, channelId, mid, false);
    }

    private MessageObject loadPinnedMessageInternal(long dialogId, int channelId, int mid, boolean returnValue) {
        long messageId;
        if (channelId != 0) {
            messageId = ((long) mid) | (((long) channelId) << 32);
        } else {
            messageId = (long) mid;
        }
        Message result = null;
        try {
            NativeByteBuffer data;
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageId)}), new Object[0]);
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    result = Message.TLdeserialize(data, data.readInt32(false), false);
                    result.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    if (result.action instanceof TL_messageActionHistoryClear) {
                        result = null;
                    } else {
                        result.var_id = cursor.intValue(1);
                        result.date = cursor.intValue(2);
                        result.dialog_id = dialogId;
                        MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                    }
                }
            }
            cursor.dispose();
            if (result == null) {
                cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
                if (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        result = Message.TLdeserialize(data, data.readInt32(false), false);
                        result.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        data.reuse();
                        if (result.var_id != mid || (result.action instanceof TL_messageActionHistoryClear)) {
                            result = null;
                        } else {
                            result.dialog_id = dialogId;
                            MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                        }
                    }
                }
                cursor.dispose();
            }
            if (result == null) {
                if (channelId != 0) {
                    TL_channels_getMessages req = new TL_channels_getMessages();
                    req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                    req.var_id.add(Integer.valueOf(mid));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$54(this, channelId));
                } else {
                    TL_messages_getMessages req2 = new TL_messages_getMessages();
                    req2.var_id.add(Integer.valueOf(mid));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new DataQuery$$Lambda$55(this, channelId));
                }
            } else if (returnValue) {
                return broadcastPinnedMessage(result, users, chats, true, returnValue);
            } else {
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                broadcastPinnedMessage(result, users, chats, true, false);
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return null;
    }

    final /* synthetic */ void lambda$loadPinnedMessageInternal$86$DataQuery(int channelId, TLObject response, TL_error error) {
        boolean ok = false;
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage((Message) messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessage((Message) messagesRes.messages.get(0));
                ok = true;
            }
        }
        if (!ok) {
            MessagesStorage.getInstance(this.currentAccount).updateChatPinnedMessage(channelId, 0);
        }
    }

    final /* synthetic */ void lambda$loadPinnedMessageInternal$87$DataQuery(int channelId, TLObject response, TL_error error) {
        boolean ok = false;
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage((Message) messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessage((Message) messagesRes.messages.get(0));
                ok = true;
            }
        }
        if (!ok) {
            MessagesStorage.getInstance(this.currentAccount).updateChatPinnedMessage(channelId, 0);
        }
    }

    private void savePinnedMessage(Message result) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$56(this, result));
    }

    final /* synthetic */ void lambda$savePinnedMessage$88$DataQuery(Message result) {
        try {
            long dialogId;
            if (result.to_id.channel_id != 0) {
                dialogId = (long) (-result.to_id.channel_id);
            } else if (result.to_id.chat_id != 0) {
                dialogId = (long) (-result.to_id.chat_id);
            } else if (result.to_id.user_id != 0) {
                dialogId = (long) result.to_id.user_id;
            } else {
                return;
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
            NativeByteBuffer data = new NativeByteBuffer(result.getObjectSize());
            result.serializeToStream(data);
            state.requery();
            state.bindLong(1, dialogId);
            state.bindInteger(2, result.var_id);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private MessageObject broadcastPinnedMessage(Message result, ArrayList<User> users, ArrayList<Chat> chats, boolean isCache, boolean returnValue) {
        int a;
        SparseArray usersDict = new SparseArray();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(user.var_id, user);
        }
        SparseArray chatsDict = new SparseArray();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(chat.var_id, chat);
        }
        if (returnValue) {
            return new MessageObject(this.currentAccount, result, usersDict, chatsDict, false);
        }
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$57(this, users, isCache, chats, result, usersDict, chatsDict));
        return null;
    }

    final /* synthetic */ void lambda$broadcastPinnedMessage$89$DataQuery(ArrayList users, boolean isCache, ArrayList chats, Message result, SparseArray usersDict, SparseArray chatsDict) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, isCache);
        MessagesController.getInstance(this.currentAccount).putChats(chats, isCache);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.pinnedMessageDidLoad, new MessageObject(this.currentAccount, result, usersDict, chatsDict, false));
    }

    private static void removeEmptyMessages(ArrayList<Message> messages) {
        int a = 0;
        while (a < messages.size()) {
            Message message = (Message) messages.get(a);
            if (message == null || (message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
                messages.remove(a);
                a--;
            }
            a++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> messages, long dialogId) {
        int a;
        MessageObject messageObject;
        ArrayList<MessageObject> messageObjects;
        if (((int) dialogId) == 0) {
            ArrayList<Long> replyMessages = new ArrayList();
            LongSparseArray<ArrayList<MessageObject>> replyMessageRandomOwners = new LongSparseArray();
            for (a = 0; a < messages.size(); a++) {
                messageObject = (MessageObject) messages.get(a);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long id = messageObject.messageOwner.reply_to_random_id;
                    messageObjects = (ArrayList) replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(Long.valueOf(id))) {
                        replyMessages.add(Long.valueOf(id));
                    }
                }
            }
            if (!replyMessages.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$58(this, replyMessages, dialogId, replyMessageRandomOwners));
                return;
            }
            return;
        }
        ArrayList<Integer> replyMessages2 = new ArrayList();
        SparseArray<ArrayList<MessageObject>> replyMessageOwners = new SparseArray();
        StringBuilder stringBuilder = new StringBuilder();
        int channelId = 0;
        for (a = 0; a < messages.size(); a++) {
            messageObject = (MessageObject) messages.get(a);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                int id2 = messageObject.messageOwner.reply_to_msg_id;
                long messageId = (long) id2;
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                    channelId = messageObject.messageOwner.to_id.channel_id;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(messageId);
                messageObjects = (ArrayList) replyMessageOwners.get(id2);
                if (messageObjects == null) {
                    messageObjects = new ArrayList();
                    replyMessageOwners.put(id2, messageObjects);
                }
                messageObjects.add(messageObject);
                if (!replyMessages2.contains(Integer.valueOf(id2))) {
                    replyMessages2.add(Integer.valueOf(id2));
                }
            }
        }
        if (!replyMessages2.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$59(this, stringBuilder, dialogId, replyMessages2, replyMessageOwners, channelId));
        }
    }

    final /* synthetic */ void lambda$loadReplyMessagesForMessages$91$DataQuery(ArrayList replyMessages, long dialogId, LongSparseArray replyMessageRandomOwners) {
        try {
            ArrayList<MessageObject> arrayList;
            int b;
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    message.var_id = cursor.intValue(1);
                    message.date = cursor.intValue(2);
                    message.dialog_id = dialogId;
                    long value = cursor.longValue(3);
                    arrayList = (ArrayList) replyMessageRandomOwners.get(value);
                    replyMessageRandomOwners.remove(value);
                    if (arrayList != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
                        for (b = 0; b < arrayList.size(); b++) {
                            MessageObject object = (MessageObject) arrayList.get(b);
                            object.replyMessageObject = messageObject;
                            object.messageOwner.reply_to_msg_id = messageObject.getId();
                            if (object.isMegagroup()) {
                                Message message2 = object.replyMessageObject.messageOwner;
                                message2.flags |= Integer.MIN_VALUE;
                            }
                        }
                    }
                }
            }
            cursor.dispose();
            if (replyMessageRandomOwners.size() != 0) {
                for (b = 0; b < replyMessageRandomOwners.size(); b++) {
                    arrayList = (ArrayList) replyMessageRandomOwners.valueAt(b);
                    for (int a = 0; a < arrayList.size(); a++) {
                        ((MessageObject) arrayList.get(a)).messageOwner.reply_to_random_id = 0;
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$78(this, dialogId));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$90$DataQuery(long dialogId) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialogId));
    }

    final /* synthetic */ void lambda$loadReplyMessagesForMessages$94$DataQuery(StringBuilder stringBuilder, long dialogId, ArrayList replyMessages, SparseArray replyMessageOwners, int channelIdFinal) {
        try {
            ArrayList<Message> result = new ArrayList();
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                    message.var_id = cursor.intValue(1);
                    message.date = cursor.intValue(2);
                    message.dialog_id = dialogId;
                    MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                    result.add(message);
                    replyMessages.remove(Integer.valueOf(message.var_id));
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!chatsToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            broadcastReplyMessages(result, replyMessageOwners, users, chats, dialogId, true);
            if (!replyMessages.isEmpty()) {
                TLObject req;
                if (channelIdFinal != 0) {
                    req = new TL_channels_getMessages();
                    req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelIdFinal);
                    req.var_id = replyMessages;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$76(this, replyMessageOwners, dialogId));
                    return;
                }
                req = new TL_messages_getMessages();
                req.var_id = replyMessages;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$77(this, replyMessageOwners, dialogId));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$92$DataQuery(SparseArray replyMessageOwners, long dialogId, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages);
        }
    }

    final /* synthetic */ void lambda$null$93$DataQuery(SparseArray replyMessageOwners, long dialogId, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages);
        }
    }

    private void saveReplyMessages(SparseArray<ArrayList<MessageObject>> replyMessageOwners, ArrayList<Message> result) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$60(this, result, replyMessageOwners));
    }

    final /* synthetic */ void lambda$saveReplyMessages$95$DataQuery(ArrayList result, SparseArray replyMessageOwners) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            for (int a = 0; a < result.size(); a++) {
                Message message = (Message) result.get(a);
                ArrayList<MessageObject> messageObjects = (ArrayList) replyMessageOwners.get(message.var_id);
                if (messageObjects != null) {
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    for (int b = 0; b < messageObjects.size(); b++) {
                        MessageObject messageObject = (MessageObject) messageObjects.get(b);
                        state.requery();
                        long messageId = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        state.bindByteBuffer(1, data);
                        state.bindLong(2, messageId);
                        state.step();
                    }
                    data.reuse();
                }
            }
            state.dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void broadcastReplyMessages(ArrayList<Message> result, SparseArray<ArrayList<MessageObject>> replyMessageOwners, ArrayList<User> users, ArrayList<Chat> chats, long dialog_id, boolean isCache) {
        int a;
        SparseArray<User> usersDict = new SparseArray();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(user.var_id, user);
        }
        SparseArray<Chat> chatsDict = new SparseArray();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(chat.var_id, chat);
        }
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$61(this, users, isCache, chats, result, replyMessageOwners, usersDict, chatsDict, dialog_id));
    }

    final /* synthetic */ void lambda$broadcastReplyMessages$96$DataQuery(ArrayList users, boolean isCache, ArrayList chats, ArrayList result, SparseArray replyMessageOwners, SparseArray usersDict, SparseArray chatsDict, long dialog_id) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, isCache);
        MessagesController.getInstance(this.currentAccount).putChats(chats, isCache);
        boolean changed = false;
        for (int a = 0; a < result.size(); a++) {
            Message message = (Message) result.get(a);
            ArrayList<MessageObject> arrayList = (ArrayList) replyMessageOwners.get(message.var_id);
            if (arrayList != null) {
                MessageObject messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
                for (int b = 0; b < arrayList.size(); b++) {
                    MessageObject m = (MessageObject) arrayList.get(b);
                    m.replyMessageObject = messageObject;
                    if (m.messageOwner.action instanceof TL_messageActionPinMessage) {
                        m.generatePinMessageText(null, null);
                    } else if (m.messageOwner.action instanceof TL_messageActionGameScore) {
                        m.generateGameMessageText(null);
                    } else if (m.messageOwner.action instanceof TL_messageActionPaymentSent) {
                        m.generatePaymentSentMessageText(null);
                    }
                    if (m.isMegagroup()) {
                        Message message2 = m.replyMessageObject.messageOwner;
                        message2.flags |= Integer.MIN_VALUE;
                    }
                }
                changed = true;
            }
        }
        if (changed) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialog_id));
        }
    }

    public static void sortEntities(ArrayList<MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    private static boolean checkInclusion(int index, ArrayList<MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            MessageEntity entity = (MessageEntity) entities.get(a);
            if (entity.offset <= index && entity.offset + entity.length > index) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int start, int end, ArrayList<MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            MessageEntity entity = (MessageEntity) entities.get(a);
            if (entity.offset > start && entity.offset + entity.length <= end) {
                return true;
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int start, int countToRemove, ArrayList<MessageEntity> entities) {
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            MessageEntity entity = (MessageEntity) entities.get(a);
            if (entity.offset > start) {
                entity.offset -= countToRemove;
            }
        }
    }

    public CharSequence substring(CharSequence source, int start, int end) {
        if (source instanceof SpannableStringBuilder) {
            return ((SpannableStringBuilder) source).subSequence(start, end);
        }
        if (source instanceof SpannedString) {
            return ((SpannedString) source).subSequence(start, end);
        }
        return TextUtils.substring(source, start, end);
    }

    public ArrayList<MessageEntity> getEntities(CharSequence[] message) {
        if (message == null || message[0] == null) {
            return null;
        }
        int index;
        int a;
        CharSequence[] charSequenceArr;
        TL_messageEntityCode entity;
        MessageEntity entity2;
        ArrayList<MessageEntity> entities = null;
        int start = -1;
        int lastIndex = 0;
        boolean isPre = false;
        String mono = "`";
        String pre = "```";
        String bold = "**";
        String italic = "__";
        while (true) {
            index = TextUtils.indexOf(message[0], !isPre ? "`" : "```", lastIndex);
            if (index == -1) {
                break;
            } else if (start == -1) {
                isPre = message[0].length() - index > 2 && message[0].charAt(index + 1) == '`' && message[0].charAt(index + 2) == '`';
                start = index;
                lastIndex = index + (isPre ? 3 : 1);
            } else {
                if (entities == null) {
                    entities = new ArrayList();
                }
                a = index + (isPre ? 3 : 1);
                while (a < message[0].length() && message[0].charAt(a) == '`') {
                    index++;
                    a++;
                }
                lastIndex = index + (isPre ? 3 : 1);
                if (isPre) {
                    int firstChar = start > 0 ? message[0].charAt(start - 1) : 0;
                    boolean replacedFirst = firstChar == 32 || firstChar == 10;
                    CharSequence startMessage = substring(message[0], 0, start - (replacedFirst ? 1 : 0));
                    CharSequence content = substring(message[0], start + 3, index);
                    firstChar = index + 3 < message[0].length() ? message[0].charAt(index + 3) : 0;
                    CharSequence charSequence = message[0];
                    int i = index + 3;
                    int i2 = (firstChar == 32 || firstChar == 10) ? 1 : 0;
                    CharSequence endMessage = substring(charSequence, i2 + i, message[0].length());
                    if (startMessage.length() != 0) {
                        startMessage = TextUtils.concat(new CharSequence[]{startMessage, "\n"});
                    } else {
                        replacedFirst = true;
                    }
                    if (endMessage.length() != 0) {
                        endMessage = TextUtils.concat(new CharSequence[]{"\n", endMessage});
                    }
                    if (!TextUtils.isEmpty(content)) {
                        message[0] = TextUtils.concat(new CharSequence[]{startMessage, content, endMessage});
                        TL_messageEntityPre entity3 = new TL_messageEntityPre();
                        entity3.offset = (replacedFirst ? 0 : 1) + start;
                        entity3.length = (replacedFirst ? 0 : 1) + ((index - start) - 3);
                        entity3.language = TtmlNode.ANONYMOUS_REGION_ID;
                        entities.add(entity3);
                        lastIndex -= 6;
                    }
                } else if (start + 1 != index) {
                    charSequenceArr = new CharSequence[3];
                    charSequenceArr[0] = substring(message[0], 0, start);
                    charSequenceArr[1] = substring(message[0], start + 1, index);
                    charSequenceArr[2] = substring(message[0], index + 1, message[0].length());
                    message[0] = TextUtils.concat(charSequenceArr);
                    entity = new TL_messageEntityCode();
                    entity.offset = start;
                    entity.length = (index - start) - 1;
                    entities.add(entity);
                    lastIndex -= 2;
                }
                start = -1;
                isPre = false;
            }
        }
        if (start != -1 && isPre) {
            charSequenceArr = new CharSequence[2];
            charSequenceArr[0] = substring(message[0], 0, start);
            charSequenceArr[1] = substring(message[0], start + 2, message[0].length());
            message[0] = TextUtils.concat(charSequenceArr);
            if (entities == null) {
                entities = new ArrayList();
            }
            entity = new TL_messageEntityCode();
            entity.offset = start;
            entity.length = 1;
            entities.add(entity);
        }
        if (message[0] instanceof Spanned) {
            int b;
            Spanned spannable = message[0];
            TypefaceSpan[] spans = (TypefaceSpan[]) spannable.getSpans(0, message[0].length(), TypefaceSpan.class);
            if (spans != null && spans.length > 0) {
                for (TypefaceSpan span : spans) {
                    int spanStart = spannable.getSpanStart(span);
                    int spanEnd = spannable.getSpanEnd(span);
                    if (!(checkInclusion(spanStart, entities) || checkInclusion(spanEnd, entities) || checkIntersection(spanStart, spanEnd, entities))) {
                        if (entities == null) {
                            entities = new ArrayList();
                        }
                        if (span.isMono()) {
                            entity2 = new TL_messageEntityCode();
                        } else if (span.isBold()) {
                            entity2 = new TL_messageEntityBold();
                        } else {
                            entity2 = new TL_messageEntityItalic();
                        }
                        entity2.offset = spanStart;
                        entity2.length = spanEnd - spanStart;
                        entities.add(entity2);
                    }
                }
            }
            URLSpanUserMention[] spansMentions = (URLSpanUserMention[]) spannable.getSpans(0, message[0].length(), URLSpanUserMention.class);
            if (spansMentions != null && spansMentions.length > 0) {
                if (entities == null) {
                    entities = new ArrayList();
                }
                for (b = 0; b < spansMentions.length; b++) {
                    TL_inputMessageEntityMentionName entity4 = new TL_inputMessageEntityMentionName();
                    entity4.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(Utilities.parseInt(spansMentions[b].getURL()).intValue());
                    if (entity4.user_id != null) {
                        entity4.offset = spannable.getSpanStart(spansMentions[b]);
                        entity4.length = Math.min(spannable.getSpanEnd(spansMentions[b]), message[0].length()) - entity4.offset;
                        if (message[0].charAt((entity4.offset + entity4.length) - 1) == ' ') {
                            entity4.length--;
                        }
                        entities.add(entity4);
                    }
                }
            }
            URLSpanReplacement[] spansUrlReplacement = (URLSpanReplacement[]) spannable.getSpans(0, message[0].length(), URLSpanReplacement.class);
            if (spansUrlReplacement != null && spansUrlReplacement.length > 0) {
                if (entities == null) {
                    entities = new ArrayList();
                }
                for (b = 0; b < spansUrlReplacement.length; b++) {
                    TL_messageEntityTextUrl entity5 = new TL_messageEntityTextUrl();
                    entity5.offset = spannable.getSpanStart(spansUrlReplacement[b]);
                    entity5.length = Math.min(spannable.getSpanEnd(spansUrlReplacement[b]), message[0].length()) - entity5.offset;
                    entity5.url = spansUrlReplacement[b].getURL();
                    entities.add(entity5);
                }
            }
        }
        int c = 0;
        while (c < 2) {
            lastIndex = 0;
            start = -1;
            String checkString = c == 0 ? "**" : "__";
            char checkChar = c == 0 ? '*' : '_';
            while (true) {
                index = TextUtils.indexOf(message[0], checkString, lastIndex);
                if (index == -1) {
                    break;
                } else if (start == -1) {
                    char prevChar = index == 0 ? ' ' : message[0].charAt(index - 1);
                    if (!checkInclusion(index, entities) && (prevChar == ' ' || prevChar == 10)) {
                        start = index;
                    }
                    lastIndex = index + 2;
                } else {
                    a = index + 2;
                    while (a < message[0].length() && message[0].charAt(a) == checkChar) {
                        index++;
                        a++;
                    }
                    lastIndex = index + 2;
                    if (checkInclusion(index, entities) || checkIntersection(start, index, entities)) {
                        start = -1;
                    } else {
                        if (start + 2 != index) {
                            if (entities == null) {
                                entities = new ArrayList();
                            }
                            try {
                                charSequenceArr = new CharSequence[3];
                                charSequenceArr[0] = substring(message[0], 0, start);
                                charSequenceArr[1] = substring(message[0], start + 2, index);
                                charSequenceArr[2] = substring(message[0], index + 2, message[0].length());
                                message[0] = TextUtils.concat(charSequenceArr);
                            } catch (Exception e) {
                                message[0] = substring(message[0], 0, start).toString() + substring(message[0], start + 2, index).toString() + substring(message[0], index + 2, message[0].length()).toString();
                            }
                            if (c == 0) {
                                entity2 = new TL_messageEntityBold();
                            } else {
                                entity2 = new TL_messageEntityItalic();
                            }
                            entity2.offset = start;
                            entity2.length = (index - start) - 2;
                            removeOffsetAfter(entity2.offset + entity2.length, 4, entities);
                            entities.add(entity2);
                            lastIndex -= 4;
                        }
                        start = -1;
                    }
                }
            }
            c++;
        }
        return entities;
    }

    public void loadDrafts() {
        if (!UserConfig.getInstance(this.currentAccount).draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getAllDrafts(), new DataQuery$$Lambda$62(this));
        }
    }

    final /* synthetic */ void lambda$loadDrafts$98$DataQuery(TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$75(this));
        }
    }

    final /* synthetic */ void lambda$null$97$DataQuery() {
        UserConfig.getInstance(this.currentAccount).draftsLoaded = true;
        this.loadingDrafts = false;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public DraftMessage getDraft(long did) {
        return (DraftMessage) this.drafts.get(did);
    }

    public Message getDraftMessage(long did) {
        return (Message) this.draftMessages.get(did);
    }

    public void saveDraft(long did, CharSequence message, ArrayList<MessageEntity> entities, Message replyToMessage, boolean noWebpage) {
        saveDraft(did, message, entities, replyToMessage, noWebpage, false);
    }

    public void saveDraft(long did, CharSequence message, ArrayList<MessageEntity> entities, Message replyToMessage, boolean noWebpage, boolean clean) {
        DraftMessage draftMessage;
        if (TextUtils.isEmpty(message) && replyToMessage == null) {
            draftMessage = new TL_draftMessageEmpty();
        } else {
            draftMessage = new TL_draftMessage();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        draftMessage.message = message == null ? TtmlNode.ANONYMOUS_REGION_ID : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (replyToMessage != null) {
            draftMessage.reply_to_msg_id = replyToMessage.var_id;
            draftMessage.flags |= 1;
        }
        if (!(entities == null || entities.isEmpty())) {
            draftMessage.entities = entities;
            draftMessage.flags |= 8;
        }
        DraftMessage currentDraft = (DraftMessage) this.drafts.get(did);
        if (!clean) {
            if (currentDraft == null || !currentDraft.message.equals(draftMessage.message) || currentDraft.reply_to_msg_id != draftMessage.reply_to_msg_id || currentDraft.no_webpage != draftMessage.no_webpage) {
                if (currentDraft == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                    return;
                }
            }
            return;
        }
        saveDraft(did, draftMessage, replyToMessage, false);
        int lower_id = (int) did;
        if (lower_id != 0) {
            TL_messages_saveDraft req = new TL_messages_saveDraft();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
            if (req.peer != null) {
                req.message = draftMessage.message;
                req.no_webpage = draftMessage.no_webpage;
                req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                req.entities = draftMessage.entities;
                req.flags = draftMessage.flags;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, DataQuery$$Lambda$63.$instance);
            } else {
                return;
            }
        }
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    static final /* synthetic */ void lambda$saveDraft$99$DataQuery(TLObject response, TL_error error) {
    }

    public void saveDraft(long did, DraftMessage draft, Message replyToMessage, boolean fromServer) {
        SerializedData serializedData;
        Editor editor = this.preferences.edit();
        if (draft == null || (draft instanceof TL_draftMessageEmpty)) {
            this.drafts.remove(did);
            this.draftMessages.remove(did);
            this.preferences.edit().remove(TtmlNode.ANONYMOUS_REGION_ID + did).remove("r_" + did).commit();
        } else {
            this.drafts.put(did, draft);
            try {
                serializedData = new SerializedData(draft.getObjectSize());
                draft.serializeToStream(serializedData);
                editor.putString(TtmlNode.ANONYMOUS_REGION_ID + did, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
        if (replyToMessage == null) {
            this.draftMessages.remove(did);
            editor.remove("r_" + did);
        } else {
            this.draftMessages.put(did, replyToMessage);
            serializedData = new SerializedData(replyToMessage.getObjectSize());
            replyToMessage.serializeToStream(serializedData);
            editor.putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray()));
            serializedData.cleanup();
        }
        editor.commit();
        if (fromServer) {
            if (draft.reply_to_msg_id != 0 && replyToMessage == null) {
                int lower_id = (int) did;
                User user = null;
                Chat chat = null;
                if (lower_id > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                } else {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                }
                if (!(user == null && chat == null)) {
                    int channelIdFinal;
                    long messageId = (long) draft.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        messageId |= ((long) chat.var_id) << 32;
                        channelIdFinal = chat.var_id;
                    } else {
                        channelIdFinal = 0;
                    }
                    long messageIdFinal = messageId;
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$64(this, messageIdFinal, channelIdFinal, did));
                }
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
        }
    }

    final /* synthetic */ void lambda$saveDraft$102$DataQuery(long messageIdFinal, int channelIdFinal, long did) {
        Message message = null;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageIdFinal)}), new Object[0]);
            if (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    message = Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                    data.reuse();
                }
            }
            cursor.dispose();
            if (message != null) {
                saveDraftReplyMessage(did, message);
            } else if (channelIdFinal != 0) {
                TL_channels_getMessages req = new TL_channels_getMessages();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelIdFinal);
                req.var_id.add(Integer.valueOf((int) messageIdFinal));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DataQuery$$Lambda$73(this, did));
            } else {
                TL_messages_getMessages req2 = new TL_messages_getMessages();
                req2.var_id.add(Integer.valueOf((int) messageIdFinal));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new DataQuery$$Lambda$74(this, did));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$100$DataQuery(long did, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(did, (Message) messagesRes.messages.get(0));
            }
        }
    }

    final /* synthetic */ void lambda$null$101$DataQuery(long did, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages messagesRes = (messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(did, (Message) messagesRes.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long did, Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$65(this, did, message));
        }
    }

    final /* synthetic */ void lambda$saveDraftReplyMessage$103$DataQuery(long did, Message message) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(did);
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.var_id) {
            this.draftMessages.put(did, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            this.preferences.edit().putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray())).commit();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts() {
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void cleanDraft(long did, boolean replyOnly) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(did);
        if (draftMessage != null) {
            if (!replyOnly) {
                this.drafts.remove(did);
                this.draftMessages.remove(did);
                this.preferences.edit().remove(TtmlNode.ANONYMOUS_REGION_ID + did).remove("r_" + did).commit();
                MessagesController.getInstance(this.currentAccount).sortDialogs(null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(did, draftMessage.message, draftMessage.entities, null, draftMessage.no_webpage, true);
            }
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(long did, ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$66(this, messages, did));
    }

    final /* synthetic */ void lambda$clearBotKeyboard$104$DataQuery(ArrayList messages, long did) {
        if (messages != null) {
            for (int a = 0; a < messages.size(); a++) {
                long did1 = this.botKeyboardsByMids.get(((Integer) messages.get(a)).intValue());
                if (did1 != 0) {
                    this.botKeyboards.remove(did1);
                    this.botKeyboardsByMids.delete(((Integer) messages.get(a)).intValue());
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did1));
                }
            }
            return;
        }
        this.botKeyboards.remove(did);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did));
    }

    public void loadBotKeyboard(long did) {
        if (((Message) this.botKeyboards.get(did)) != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, keyboard, Long.valueOf(did));
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$67(this, did));
    }

    final /* synthetic */ void lambda$loadBotKeyboard$106$DataQuery(long did) {
        Message botKeyboard = null;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
            if (cursor.next() && !cursor.isNull(0)) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    botKeyboard = Message.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                }
            }
            cursor.dispose();
            if (botKeyboard != null) {
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$72(this, botKeyboard, did));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$105$DataQuery(Message botKeyboardFinal, long did) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, botKeyboardFinal, Long.valueOf(did));
    }

    public void loadBotInfo(int uid, boolean cache, int classGuid) {
        if (!cache || ((BotInfo) this.botInfos.get(uid)) == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$68(this, uid, classGuid));
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
    }

    final /* synthetic */ void lambda$loadBotInfo$108$DataQuery(int uid, int classGuid) {
        BotInfo botInfo = null;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(uid)}), new Object[0]);
            if (cursor.next() && !cursor.isNull(0)) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    botInfo = BotInfo.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                }
            }
            cursor.dispose();
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$71(this, botInfo, classGuid));
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    final /* synthetic */ void lambda$null$107$DataQuery(BotInfo botInfoFinal, int classGuid) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, botInfoFinal, Integer.valueOf(classGuid));
    }

    public void putBotKeyboard(long did, Message message) {
        if (message != null) {
            int mid = 0;
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
                if (cursor.next()) {
                    mid = cursor.intValue(0);
                }
                cursor.dispose();
                if (mid < message.var_id) {
                    SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, did);
                    state.bindInteger(2, message.var_id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    AndroidUtilities.runOnUIThread(new DataQuery$$Lambda$69(this, did, message));
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$putBotKeyboard$109$DataQuery(long did, Message message) {
        Message old = (Message) this.botKeyboards.get(did);
        this.botKeyboards.put(did, message);
        if (old != null) {
            this.botKeyboardsByMids.delete(old.var_id);
        }
        this.botKeyboardsByMids.put(message.var_id, did);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(did));
    }

    public void putBotInfo(BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DataQuery$$Lambda$70(this, botInfo));
        }
    }

    final /* synthetic */ void lambda$putBotInfo$110$DataQuery(BotInfo botInfo) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(botInfo.getObjectSize());
            botInfo.serializeToStream(data);
            state.bindInteger(1, botInfo.user_id);
            state.bindByteBuffer(2, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }
}
