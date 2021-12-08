package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;

public class MediaDataController extends BaseController {
    private static volatile MediaDataController[] Instance = new MediaDataController[3];
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_GIF = 5;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOS_ONLY = 6;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 8;
    public static final int MEDIA_URL = 3;
    public static final int MEDIA_VIDEOS_ONLY = 7;
    public static String SHORTCUT_CATEGORY = "org.telegram.messenger.SHORTCUT_SHARE";
    public static final int TYPE_EMOJI = 4;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_GREETINGS = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC.MessageEntity> entityComparator = MediaDataController$$ExternalSyntheticLambda13.INSTANCE;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC.Document>> allStickersFeatured = new HashMap<>();
    private int[] archivedStickersCount = new int[2];
    private HashMap<String, TLRPC.BotInfo> botInfos = new HashMap<>();
    private LongSparseArray<TLRPC.Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    private LongSparseArray<String> diceEmojiStickerSetsById = new LongSparseArray<>();
    private HashMap<String, TLRPC.TL_messages_stickerSet> diceStickerSetsByEmoji = new HashMap<>();
    private LongSparseArray<SparseArray<TLRPC.Message>> draftMessages = new LongSparseArray<>();
    private SharedPreferences draftPreferences;
    private LongSparseArray<SparseArray<TLRPC.DraftMessage>> drafts = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private LongSparseArray<TLRPC.StickerSetCovered> featuredStickerSetsById = new LongSparseArray<>();
    private boolean featuredStickersLoaded;
    private TLRPC.Document greetingsSticker;
    private LongSparseArray<TLRPC.TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    public ArrayList<TLRPC.TL_topPeer> hints = new ArrayList<>();
    private boolean inTransaction;
    public ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<TLRPC.TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private long lastDialogId;
    private int lastGuid;
    private long lastMergeDialogId;
    private int lastReplyMessageId;
    private int lastReqId;
    private int lastReturnedNum;
    private TLRPC.Chat lastSearchChat;
    private String lastSearchQuery;
    private TLRPC.User lastSearchUser;
    private int[] loadDate = new int[5];
    private int loadFeaturedDate;
    private long loadFeaturedHash;
    private long[] loadHash = new long[5];
    boolean loaded;
    boolean loading;
    private HashSet<String> loadingDiceStickerSets = new HashSet<>();
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingMoreSearchMessages;
    private LongSparseArray<Boolean> loadingPinnedMessages = new LongSparseArray<>();
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[4];
    private boolean[] loadingStickers = new boolean[5];
    private int mergeReqId;
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private ArrayList<Long> readingStickerSets = new ArrayList<>();
    private ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    private boolean recentGifsLoaded;
    private ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] recentStickersLoaded = new boolean[4];
    private LongSparseArray<Runnable> removingStickerSetsUndos = new LongSparseArray<>();
    private int reqId;
    private Runnable[] scheduledLoadStickers = new Runnable[5];
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private ConcurrentHashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new ConcurrentHashMap<>(100, 1.0f, 1);
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] stickersLoaded = new boolean[5];
    private ArrayList<Long> unreadStickerSets = new ArrayList<>();
    private HashMap<String, ArrayList<TLRPC.Message>> verifyingMessages = new HashMap<>();

    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    public static MediaDataController getInstance(int num) {
        MediaDataController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MediaDataController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController = new MediaDataController(num);
                    localInstance = mediaDataController;
                    mediaDataControllerArr[num] = mediaDataController;
                }
            }
        }
        return localInstance;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public MediaDataController(int num) {
        super(num);
        if (this.currentAccount == 0) {
            this.draftPreferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.draftPreferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Map.Entry<String, ?> entry : this.draftPreferences.getAll().entrySet()) {
            try {
                String key = entry.getKey();
                long did = Utilities.parseLong(key).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                boolean isThread = false;
                if (!key.startsWith("r_")) {
                    boolean startsWith = key.startsWith("rt_");
                    isThread = startsWith;
                    if (!startsWith) {
                        TLRPC.DraftMessage draftMessage = TLRPC.DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                        if (draftMessage != null) {
                            SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(did);
                            if (threads == null) {
                                threads = new SparseArray<>();
                                this.drafts.put(did, threads);
                            }
                            threads.put(key.startsWith("t_") ? Utilities.parseInt(key.substring(key.lastIndexOf(95) + 1)).intValue() : 0, draftMessage);
                        }
                        serializedData.cleanup();
                    }
                }
                TLRPC.Message message = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (message != null) {
                    message.readAttachPath(serializedData, getUserConfig().clientUserId);
                    SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(did);
                    if (threads2 == null) {
                        threads2 = new SparseArray<>();
                        this.draftMessages.put(did, threads2);
                    }
                    threads2.put(isThread ? Utilities.parseInt(key.substring(key.lastIndexOf(95) + 1)).intValue() : 0, message);
                }
                serializedData.cleanup();
            } catch (Exception e) {
            }
        }
        loadStickersByEmojiOrName("tg_placeholders_android", false, true);
    }

    public void cleanup() {
        int a = 0;
        while (true) {
            ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
            if (a >= arrayListArr.length) {
                break;
            }
            arrayListArr[a].clear();
            this.loadingRecentStickers[a] = false;
            this.recentStickersLoaded[a] = false;
            a++;
        }
        for (int a2 = 0; a2 < 4; a2++) {
            this.loadHash[a2] = 0;
            this.loadDate[a2] = 0;
            this.stickerSets[a2].clear();
            this.loadingStickers[a2] = false;
            this.stickersLoaded[a2] = false;
        }
        this.loadingPinnedMessages.clear();
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
        this.diceStickerSetsByEmoji.clear();
        this.diceEmojiStickerSetsById.clear();
        this.loadingDiceStickerSets.clear();
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable(MediaDataController$$ExternalSyntheticLambda8.INSTANCE);
        }
        this.verifyingMessages.clear();
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftPreferences.edit().clear().commit();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    static /* synthetic */ void lambda$cleanup$0() {
        try {
            ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void checkStickers(int type) {
        if (this.loadingStickers[type]) {
            return;
        }
        if (!this.stickersLoaded[type] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[type])) >= 3600) {
            loadStickers(type, true, false);
        }
    }

    public void checkFeaturedStickers() {
        if (this.loadingFeaturedStickers) {
            return;
        }
        if (!this.featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate)) >= 3600) {
            loadFeaturedStickers(true, false);
        }
    }

    public ArrayList<TLRPC.Document> getRecentStickers(int type) {
        ArrayList<TLRPC.Document> arrayList = this.recentStickers[type];
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC.Document> getRecentStickersNoCopy(int type) {
        return this.recentStickers[type];
    }

    public boolean isStickerInFavorites(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < this.recentStickers[2].size(); a++) {
            TLRPC.Document d = this.recentStickers[2].get(a);
            if (d.id == document.id && d.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentSticker(int type, Object parentObject, TLRPC.Document document, int date, boolean remove) {
        boolean found;
        int maxCount;
        TLRPC.Document old;
        int i = type;
        Object obj = parentObject;
        TLRPC.Document document2 = document;
        boolean z = remove;
        if (i == 3) {
            return;
        }
        if (MessageObject.isStickerDocument(document) || MessageObject.isAnimatedStickerDocument(document2, true)) {
            int a = 0;
            while (true) {
                if (a >= this.recentStickers[i].size()) {
                    found = false;
                    break;
                }
                TLRPC.Document image = this.recentStickers[i].get(a);
                if (image.id == document2.id) {
                    this.recentStickers[i].remove(a);
                    if (!z) {
                        this.recentStickers[i].add(0, image);
                    }
                    found = true;
                } else {
                    a++;
                }
            }
            if (!found && !z) {
                this.recentStickers[i].add(0, document2);
            }
            if (i == 2) {
                if (z) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, document2, 4);
                } else {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, document2, 5);
                }
                TLRPC.TL_messages_faveSticker req = new TLRPC.TL_messages_faveSticker();
                req.id = new TLRPC.TL_inputDocument();
                req.id.id = document2.id;
                req.id.access_hash = document2.access_hash;
                req.id.file_reference = document2.file_reference;
                if (req.id.file_reference == null) {
                    req.id.file_reference = new byte[0];
                }
                req.unfave = z;
                getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda38(this, obj, req));
                maxCount = getMessagesController().maxFaveStickersCount;
            } else {
                if (i == 0 && z) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, document2, 3);
                    TLRPC.TL_messages_saveRecentSticker req2 = new TLRPC.TL_messages_saveRecentSticker();
                    req2.id = new TLRPC.TL_inputDocument();
                    req2.id.id = document2.id;
                    req2.id.access_hash = document2.access_hash;
                    req2.id.file_reference = document2.file_reference;
                    if (req2.id.file_reference == null) {
                        req2.id.file_reference = new byte[0];
                    }
                    req2.unsave = true;
                    getConnectionsManager().sendRequest(req2, new MediaDataController$$ExternalSyntheticLambda39(this, obj, req2));
                }
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[i].size() > maxCount || z) {
                if (z) {
                    old = document2;
                } else {
                    ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
                    old = arrayListArr[i].remove(arrayListArr[i].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda59(this, i, old));
            }
            if (!z) {
                ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
                arrayList.add(document2);
                ArrayList<TLRPC.Document> arrayList2 = arrayList;
                processLoadedRecentDocuments(type, arrayList, false, date, false);
            }
            if (i == 2 || (i == 0 && z)) {
                getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, false, Integer.valueOf(type));
            }
        }
    }

    /* renamed from: lambda$addRecentSticker$2$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m773xvar_b2d65(Object parentObject, TLRPC.TL_messages_faveSticker req, TLObject response, TLRPC.TL_error error) {
        if (error == null || !FileRefController.isFileRefError(error.text) || parentObject == null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda61(this));
            return;
        }
        getFileRefController().requestReference(parentObject, req);
    }

    /* renamed from: lambda$addRecentSticker$1$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m772x31aeCLASSNAME() {
        getMediaDataController().loadRecents(2, false, false, true);
    }

    /* renamed from: lambda$addRecentSticker$3$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m774xb78796c4(Object parentObject, TLRPC.TL_messages_saveRecentSticker req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            getFileRefController().requestReference(parentObject, req);
        }
    }

    /* renamed from: lambda$addRecentSticker$4$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m775x7a740023(int type, TLRPC.Document old) {
        int cacheType;
        if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = " + cacheType).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public ArrayList<TLRPC.Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGif(TLRPC.Document document) {
        int i = 0;
        int N = this.recentGifs.size();
        while (true) {
            if (i >= N) {
                break;
            } else if (this.recentGifs.get(i).id == document.id) {
                this.recentGifs.remove(i);
                break;
            } else {
                i++;
            }
        }
        TLRPC.TL_messages_saveGif req = new TLRPC.TL_messages_saveGif();
        req.id = new TLRPC.TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.id.file_reference = document.file_reference;
        if (req.id.file_reference == null) {
            req.id.file_reference = new byte[0];
        }
        req.unsave = true;
        getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda48(this, req));
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda124(this, document));
    }

    /* renamed from: lambda$removeRecentGif$5$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m881x1CLASSNAMEd8(TLRPC.TL_messages_saveGif req, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            getFileRefController().requestReference("gif", req);
        }
    }

    /* renamed from: lambda$removeRecentGif$6$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m882xdeeCLASSNAME(TLRPC.Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean hasRecentGif(TLRPC.Document document) {
        for (int a = 0; a < this.recentGifs.size(); a++) {
            TLRPC.Document image = this.recentGifs.get(a);
            if (image.id == document.id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(TLRPC.Document document, int date) {
        if (document != null) {
            boolean found = false;
            int a = 0;
            while (true) {
                if (a >= this.recentGifs.size()) {
                    break;
                }
                TLRPC.Document image = this.recentGifs.get(a);
                if (image.id == document.id) {
                    this.recentGifs.remove(a);
                    this.recentGifs.add(0, image);
                    found = true;
                    break;
                }
                a++;
            }
            if (!found) {
                this.recentGifs.add(0, document);
            }
            if (this.recentGifs.size() > getMessagesController().maxRecentGifsCount) {
                ArrayList<TLRPC.Document> arrayList = this.recentGifs;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda123(this, arrayList.remove(arrayList.size() - 1)));
            }
            ArrayList<TLRPC.Document> arrayList2 = new ArrayList<>();
            arrayList2.add(document);
            processLoadedRecentDocuments(0, arrayList2, true, date, false);
        }
    }

    /* renamed from: lambda$addRecentGif$7$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m771lambda$addRecentGif$7$orgtelegrammessengerMediaDataController(TLRPC.Document old) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean isLoadingStickers(int type) {
        return this.loadingStickers[type];
    }

    public void replaceStickerSet(TLRPC.TL_messages_stickerSet set) {
        TLRPC.TL_messages_stickerSet existingSet = this.stickerSetsById.get(set.set.id);
        String emoji = this.diceEmojiStickerSetsById.get(set.set.id);
        if (emoji != null) {
            this.diceStickerSetsByEmoji.put(emoji, set);
            putDiceStickersToCache(emoji, set, (int) (System.currentTimeMillis() / 1000));
        }
        boolean isGroupSet = false;
        if (existingSet == null) {
            existingSet = this.stickerSetsByName.get(set.set.short_name);
        }
        if (existingSet == null && (existingSet = this.groupStickerSets.get(set.set.id)) != null) {
            isGroupSet = true;
        }
        if (existingSet != null) {
            boolean changed = false;
            if ("AnimatedEmojies".equals(set.set.short_name)) {
                changed = true;
                existingSet.documents = set.documents;
                existingSet.packs = set.packs;
                existingSet.set = set.set;
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda140(this, set));
            } else {
                LongSparseArray<TLRPC.Document> documents = new LongSparseArray<>();
                int size = set.documents.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.Document document = (TLRPC.Document) set.documents.get(a);
                    documents.put(document.id, document);
                }
                int size2 = existingSet.documents.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.Document newDocument = documents.get(((TLRPC.Document) existingSet.documents.get(a2)).id);
                    if (newDocument != null) {
                        existingSet.documents.set(a2, newDocument);
                        changed = true;
                    }
                }
            }
            if (!changed) {
                return;
            }
            if (isGroupSet) {
                putSetToCache(existingSet);
                return;
            }
            int type = set.set.masks;
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
            if ("AnimatedEmojies".equals(set.set.short_name)) {
                putStickersToCache(4, this.stickerSets[4], this.loadDate[4], this.loadHash[4]);
            }
        }
    }

    /* renamed from: lambda$replaceStickerSet$8$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m883xa7165cc1(TLRPC.TL_messages_stickerSet set) {
        LongSparseArray<TLRPC.Document> stickersById = getStickerByIds(4);
        for (int b = 0; b < set.documents.size(); b++) {
            TLRPC.Document document = (TLRPC.Document) set.documents.get(b);
            stickersById.put(document.id, document);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByName(String name) {
        return this.stickerSetsByName.get(name);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByEmojiOrName(String emoji) {
        return this.diceStickerSetsByEmoji.get(emoji);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetById(long id) {
        return this.stickerSetsById.get(id);
    }

    public TLRPC.TL_messages_stickerSet getGroupStickerSetById(TLRPC.StickerSet stickerSet) {
        TLRPC.TL_messages_stickerSet set = this.stickerSetsById.get(stickerSet.id);
        if (set == null) {
            set = this.groupStickerSets.get(stickerSet.id);
            if (set == null || set.set == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return set;
    }

    public void putGroupStickerSet(TLRPC.TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(stickerSet.set.id, stickerSet);
    }

    private void loadGroupStickerSet(TLRPC.StickerSet stickerSet, boolean cache) {
        if (cache) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda128(this, stickerSet));
            return;
        }
        TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
        req.stickerset = new TLRPC.TL_inputStickerSetID();
        req.stickerset.id = stickerSet.id;
        req.stickerset.access_hash = stickerSet.access_hash;
        getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda17(this));
    }

    /* renamed from: lambda$loadGroupStickerSet$10$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m813x9e7b8d2f(TLRPC.StickerSet stickerSet) {
        TLRPC.TL_messages_stickerSet set;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.id + "'", new Object[0]);
            if (!cursor.next() || cursor.isNull(0)) {
                set = null;
            } else {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    set = TLRPC.TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false);
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
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda137(this, set));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadGroupStickerSet$9$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m816xd2543abb(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    /* renamed from: lambda$loadGroupStickerSet$12$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m815x24545fed(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda136(this, (TLRPC.TL_messages_stickerSet) response));
        }
    }

    /* renamed from: lambda$loadGroupStickerSet$11$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m814x6167var_e(TLRPC.TL_messages_stickerSet set) {
        this.groupStickerSets.put(set.set.id, set);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(set.set.id));
    }

    private void putSetToCache(TLRPC.TL_messages_stickerSet set) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda139(this, set));
    }

    /* renamed from: lambda$putSetToCache$13$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m879x629a882(TLRPC.TL_messages_stickerSet set) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            state.requery();
            state.bindString(1, "s_" + set.set.id);
            state.bindInteger(2, 6);
            state.bindString(3, "");
            state.bindString(4, "");
            state.bindString(5, "");
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
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<TLRPC.Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public TLRPC.Document getEmojiAnimatedSticker(CharSequence message) {
        String emoji = message.toString().replace("️", "");
        ArrayList<TLRPC.TL_messages_stickerSet> arrayList = getStickerSets(4);
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            TLRPC.TL_messages_stickerSet set = arrayList.get(a);
            int N2 = set.packs.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.TL_stickerPack pack = (TLRPC.TL_stickerPack) set.packs.get(b);
                if (!pack.documents.isEmpty() && TextUtils.equals(pack.emoticon, emoji)) {
                    return getStickerByIds(4).get(pack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int type) {
        if (type == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[type];
    }

    public LongSparseArray<TLRPC.Document> getStickerByIds(int type) {
        return this.stickersByIds[type];
    }

    public ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int N = this.featuredStickerSets.size();
        for (int a = 0; a < N; a++) {
            TLRPC.StickerSetCovered pack = this.featuredStickerSets.get(a);
            if (!isStickerPackInstalled(pack.set.id) && ((!pack.covers.isEmpty() || pack.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(pack.set.id)))) {
                return false;
            }
        }
        return true;
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
        String value = this.stickersByEmoji.get(id);
        return value != null ? value : "";
    }

    public static long calcDocumentsHash(ArrayList<TLRPC.Document> arrayList) {
        return calcDocumentsHash(arrayList, 200);
    }

    public static long calcDocumentsHash(ArrayList<TLRPC.Document> arrayList, int maxCount) {
        if (arrayList == null) {
            return 0;
        }
        long acc = 0;
        int N = Math.min(maxCount, arrayList.size());
        for (int a = 0; a < N; a++) {
            TLRPC.Document document = arrayList.get(a);
            if (document != null) {
                acc = calcHash(acc, document.id);
            }
        }
        return acc;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(int r12, boolean r13, boolean r14, boolean r15) {
        /*
            r11 = this;
            r0 = 1
            if (r13 == 0) goto L_0x0010
            boolean r1 = r11.loadingRecentGifs
            if (r1 == 0) goto L_0x0008
            return
        L_0x0008:
            r11.loadingRecentGifs = r0
            boolean r1 = r11.recentGifsLoaded
            if (r1 == 0) goto L_0x0020
            r14 = 0
            goto L_0x0020
        L_0x0010:
            boolean[] r1 = r11.loadingRecentStickers
            boolean r2 = r1[r12]
            if (r2 == 0) goto L_0x0017
            return
        L_0x0017:
            r1[r12] = r0
            boolean[] r1 = r11.recentStickersLoaded
            boolean r1 = r1[r12]
            if (r1 == 0) goto L_0x0020
            r14 = 0
        L_0x0020:
            if (r14 == 0) goto L_0x0034
            org.telegram.messenger.MessagesStorage r0 = r11.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r0 = r0.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145 r1 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145
            r1.<init>(r11, r13, r12)
            r0.postRunnable(r1)
            goto L_0x0104
        L_0x0034:
            int r1 = r11.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getEmojiSettings(r1)
            r2 = 3
            r3 = 0
            if (r15 != 0) goto L_0x0084
            r4 = 0
            if (r13 == 0) goto L_0x0049
            java.lang.String r6 = "lastGifLoadTime"
            long r4 = r1.getLong(r6, r4)
            goto L_0x006a
        L_0x0049:
            if (r12 != 0) goto L_0x0052
            java.lang.String r6 = "lastStickersLoadTime"
            long r4 = r1.getLong(r6, r4)
            goto L_0x006a
        L_0x0052:
            if (r12 != r0) goto L_0x005b
            java.lang.String r6 = "lastStickersLoadTimeMask"
            long r4 = r1.getLong(r6, r4)
            goto L_0x006a
        L_0x005b:
            if (r12 != r2) goto L_0x0064
            java.lang.String r6 = "lastStickersLoadTimeGreet"
            long r4 = r1.getLong(r6, r4)
            goto L_0x006a
        L_0x0064:
            java.lang.String r6 = "lastStickersLoadTimeFavs"
            long r4 = r1.getLong(r6, r4)
        L_0x006a:
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r6 - r4
            long r6 = java.lang.Math.abs(r6)
            r8 = 3600000(0x36ee80, double:1.7786363E-317)
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 >= 0) goto L_0x0084
            if (r13 == 0) goto L_0x007f
            r11.loadingRecentGifs = r3
            goto L_0x0083
        L_0x007f:
            boolean[] r0 = r11.loadingRecentStickers
            r0[r12] = r3
        L_0x0083:
            return
        L_0x0084:
            if (r13 == 0) goto L_0x00a0
            org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs r0 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r11.recentGifs
            long r2 = calcDocumentsHash(r2)
            r0.hash = r2
            org.telegram.tgnet.ConnectionsManager r2 = r11.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda20 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda20
            r3.<init>(r11, r12)
            r2.sendRequest(r0, r3)
            goto L_0x0104
        L_0x00a0:
            r4 = 2
            if (r12 != r4) goto L_0x00b4
            org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r2 = r11.recentStickers
            r2 = r2[r12]
            long r2 = calcDocumentsHash(r2)
            r0.hash = r2
            goto L_0x00f8
        L_0x00b4:
            if (r12 != r2) goto L_0x00e2
            org.telegram.tgnet.TLRPC$TL_messages_getStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
            r0.<init>()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "👋"
            r2.append(r3)
            java.lang.String r3 = "⭐"
            java.lang.String r3 = org.telegram.messenger.Emoji.fixEmoji(r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.emoticon = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r2 = r11.recentStickers
            r2 = r2[r12]
            long r2 = calcDocumentsHash(r2)
            r0.hash = r2
            goto L_0x00f8
        L_0x00e2:
            org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers r2 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers
            r2.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r4 = r11.recentStickers
            r4 = r4[r12]
            long r4 = calcDocumentsHash(r4)
            r2.hash = r4
            if (r12 != r0) goto L_0x00f4
            goto L_0x00f5
        L_0x00f4:
            r0 = 0
        L_0x00f5:
            r2.attached = r0
            r0 = r2
        L_0x00f8:
            org.telegram.tgnet.ConnectionsManager r2 = r11.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda21 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda21
            r3.<init>(r11, r12)
            r2.sendRequest(r0, r3)
        L_0x0104:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    /* renamed from: lambda$loadRecents$15$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m833lambda$loadRecents$15$orgtelegrammessengerMediaDataController(boolean gif, int type) {
        int cacheType;
        if (gif) {
            cacheType = 2;
        } else if (type == 0) {
            cacheType = 3;
        } else if (type == 1) {
            cacheType = 4;
        } else if (type == 3) {
            cacheType = 6;
        } else {
            cacheType = 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor cursor = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + cacheType + " ORDER BY date DESC", new Object[0]);
            ArrayList<TLRPC.Document> arrayList = new ArrayList<>();
            while (cursor.next()) {
                if (!cursor.isNull(0)) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        TLRPC.Document document = TLRPC.Document.TLdeserialize(data, data.readInt32(false), false);
                        if (document != null) {
                            arrayList.add(document);
                        }
                        data.reuse();
                    }
                }
            }
            cursor.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda148(this, gif, arrayList, type));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$loadRecents$14$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m832lambda$loadRecents$14$orgtelegrammessengerMediaDataController(boolean gif, ArrayList arrayList, int type) {
        if (gif) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[type] = arrayList;
            this.loadingRecentStickers[type] = false;
            this.recentStickersLoaded[type] = true;
        }
        if (type == 3) {
            preloadNextGreetingsSticker();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        loadRecents(type, gif, false, false);
    }

    /* renamed from: lambda$loadRecents$16$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m834lambda$loadRecents$16$orgtelegrammessengerMediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (response instanceof TLRPC.TL_messages_savedGifs) {
            arrayList = ((TLRPC.TL_messages_savedGifs) response).gifs;
        }
        processLoadedRecentDocuments(type, arrayList, true, 0, true);
    }

    /* renamed from: lambda$loadRecents$17$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m835lambda$loadRecents$17$orgtelegrammessengerMediaDataController(int type, TLObject response, TLRPC.TL_error error) {
        ArrayList<TLRPC.Document> arrayList = null;
        if (type == 3) {
            if (response instanceof TLRPC.TL_messages_stickers) {
                arrayList = ((TLRPC.TL_messages_stickers) response).stickers;
            }
        } else if (type == 2) {
            if (response instanceof TLRPC.TL_messages_favedStickers) {
                arrayList = ((TLRPC.TL_messages_favedStickers) response).stickers;
            }
        } else if (response instanceof TLRPC.TL_messages_recentStickers) {
            arrayList = ((TLRPC.TL_messages_recentStickers) response).stickers;
        }
        processLoadedRecentDocuments(type, arrayList, false, 0, true);
    }

    private void preloadNextGreetingsSticker() {
        if (!this.recentStickers[3].isEmpty()) {
            this.greetingsSticker = this.recentStickers[3].get(Utilities.random.nextInt(this.recentStickers[3].size()));
            getFileLoader().loadFile(ImageLocation.getForDocument(this.greetingsSticker), this.greetingsSticker, (String) null, 0, 1);
        }
    }

    public TLRPC.Document getGreetingsSticker() {
        TLRPC.Document result = this.greetingsSticker;
        preloadNextGreetingsSticker();
        return result;
    }

    /* access modifiers changed from: protected */
    public void processLoadedRecentDocuments(int type, ArrayList<TLRPC.Document> documents, boolean gif, int date, boolean replace) {
        if (documents != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda147(this, gif, type, documents, replace, date));
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda146(this, gif, type, documents));
        }
    }

    /* renamed from: lambda$processLoadedRecentDocuments$18$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m864x24204c2c(boolean gif, int type, ArrayList documents, boolean replace, int date) {
        int maxCount;
        int cacheType;
        int i = type;
        ArrayList arrayList = documents;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            if (gif) {
                maxCount = getMessagesController().maxRecentGifsCount;
            } else if (i == 3) {
                maxCount = 200;
            } else if (i == 2) {
                maxCount = getMessagesController().maxFaveStickersCount;
            } else {
                maxCount = getMessagesController().maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int count = documents.size();
            if (gif) {
                cacheType = 2;
            } else if (i == 0) {
                cacheType = 3;
            } else if (i == 1) {
                cacheType = 4;
            } else if (i == 3) {
                cacheType = 6;
            } else {
                cacheType = 5;
            }
            if (replace) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + cacheType).stepThis().dispose();
            }
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                } else if (a == maxCount) {
                    break;
                } else {
                    TLRPC.Document document = (TLRPC.Document) arrayList.get(a);
                    state.requery();
                    state.bindString(1, "" + document.id);
                    state.bindInteger(2, cacheType);
                    state.bindString(3, "");
                    state.bindString(4, "");
                    state.bindString(5, "");
                    state.bindInteger(6, 0);
                    state.bindInteger(7, 0);
                    state.bindInteger(8, 0);
                    state.bindInteger(9, date != 0 ? date : count - a);
                    NativeByteBuffer data = new NativeByteBuffer(document.getObjectSize());
                    document.serializeToStream(data);
                    state.bindByteBuffer(10, data);
                    state.step();
                    data.reuse();
                    a++;
                }
            }
            state.dispose();
            database.commitTransaction();
            if (documents.size() >= maxCount) {
                database.beginTransaction();
                for (int a2 = maxCount; a2 < documents.size(); a2++) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document) arrayList.get(a2)).id + "' AND type = " + cacheType).stepThis().dispose();
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$processLoadedRecentDocuments$19$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m865xe70cb58b(boolean gif, int type, ArrayList documents) {
        SharedPreferences.Editor editor = MessagesController.getEmojiSettings(this.currentAccount).edit();
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
            } else if (type == 3) {
                editor.putLong("lastStickersLoadTimeGreet", System.currentTimeMillis()).commit();
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
            if (type == 3) {
                preloadNextGreetingsSticker();
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(gif), Integer.valueOf(type));
        }
    }

    public void reorderStickers(int type, ArrayList<Long> order) {
        Collections.sort(this.stickerSets[type], new MediaDataController$$ExternalSyntheticLambda10(order));
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    static /* synthetic */ int lambda$reorderStickers$20(ArrayList order, TLRPC.TL_messages_stickerSet lhs, TLRPC.TL_messages_stickerSet rhs) {
        int index1 = order.indexOf(Long.valueOf(lhs.set.id));
        int index2 = order.indexOf(Long.valueOf(rhs.set.id));
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

    public void storeTempStickerSet(TLRPC.TL_messages_stickerSet set) {
        this.stickerSetsById.put(set.set.id, set);
        this.stickerSetsByName.put(set.set.short_name, set);
    }

    public void addNewStickerSet(TLRPC.TL_messages_stickerSet set) {
        if (this.stickerSetsById.indexOfKey(set.set.id) < 0 && !this.stickerSetsByName.containsKey(set.set.short_name)) {
            int type = set.set.masks;
            this.stickerSets[type].add(0, set);
            this.stickerSetsById.put(set.set.id, set);
            this.installedStickerSetsById.put(set.set.id, set);
            this.stickerSetsByName.put(set.set.short_name, set);
            LongSparseArray<TLRPC.Document> stickersById = new LongSparseArray<>();
            for (int a = 0; a < set.documents.size(); a++) {
                TLRPC.Document document = (TLRPC.Document) set.documents.get(a);
                stickersById.put(document.id, document);
            }
            for (int a2 = 0; a2 < set.packs.size(); a2++) {
                TLRPC.TL_stickerPack stickerPack = (TLRPC.TL_stickerPack) set.packs.get(a2);
                stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                ArrayList<TLRPC.Document> arrayList = this.allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = stickerPack.documents.get(c);
                    if (this.stickersByEmoji.indexOfKey(id.longValue()) < 0) {
                        this.stickersByEmoji.put(id.longValue(), stickerPack.emoticon);
                    }
                    TLRPC.Document sticker = stickersById.get(id.longValue());
                    if (sticker != null) {
                        arrayList.add(sticker);
                    }
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
            loadStickers(type, false, true);
        }
    }

    public void loadFeaturedStickers(boolean cache, boolean force) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda105(this));
                return;
            }
            TLRPC.TL_messages_getFeaturedStickers req = new TLRPC.TL_messages_getFeaturedStickers();
            req.hash = force ? 0 : this.loadFeaturedHash;
            getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda47(this, req));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0072, code lost:
        if (r5 != null) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0079, code lost:
        if (r5 == null) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007b, code lost:
        r5.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x007e, code lost:
        r12 = r5;
        processLoadedFeaturedStickers(r0, r1, true, r2, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x008a, code lost:
        return;
     */
    /* renamed from: lambda$loadFeaturedStickers$21$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m810x418ff9d5() {
        /*
            r13 = this;
            r0 = 0
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r2 = 0
            r3 = 0
            r5 = 0
            org.telegram.messenger.MessagesStorage r6 = r13.getMessagesStorage()     // Catch:{ all -> 0x0075 }
            org.telegram.SQLite.SQLiteDatabase r6 = r6.getDatabase()     // Catch:{ all -> 0x0075 }
            java.lang.String r7 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1"
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x0075 }
            org.telegram.SQLite.SQLiteCursor r6 = r6.queryFinalized(r7, r9)     // Catch:{ all -> 0x0075 }
            r5 = r6
            boolean r6 = r5.next()     // Catch:{ all -> 0x0075 }
            if (r6 == 0) goto L_0x0072
            org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r8)     // Catch:{ all -> 0x0075 }
            if (r6 == 0) goto L_0x0047
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x0075 }
            r7.<init>()     // Catch:{ all -> 0x0075 }
            r0 = r7
            int r7 = r6.readInt32(r8)     // Catch:{ all -> 0x0075 }
            r9 = 0
        L_0x0033:
            if (r9 >= r7) goto L_0x0044
            int r10 = r6.readInt32(r8)     // Catch:{ all -> 0x0075 }
            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = org.telegram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r6, r10, r8)     // Catch:{ all -> 0x0075 }
            r0.add(r10)     // Catch:{ all -> 0x0075 }
            int r9 = r9 + 1
            goto L_0x0033
        L_0x0044:
            r6.reuse()     // Catch:{ all -> 0x0075 }
        L_0x0047:
            r7 = 1
            org.telegram.tgnet.NativeByteBuffer r7 = r5.byteBufferValue(r7)     // Catch:{ all -> 0x0075 }
            r6 = r7
            if (r6 == 0) goto L_0x0067
            int r7 = r6.readInt32(r8)     // Catch:{ all -> 0x0075 }
            r9 = 0
        L_0x0054:
            if (r9 >= r7) goto L_0x0064
            long r10 = r6.readInt64(r8)     // Catch:{ all -> 0x0075 }
            java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0075 }
            r1.add(r10)     // Catch:{ all -> 0x0075 }
            int r9 = r9 + 1
            goto L_0x0054
        L_0x0064:
            r6.reuse()     // Catch:{ all -> 0x0075 }
        L_0x0067:
            r7 = 2
            int r7 = r5.intValue(r7)     // Catch:{ all -> 0x0075 }
            r2 = r7
            long r7 = r13.calcFeaturedStickersHash(r0)     // Catch:{ all -> 0x0075 }
            r3 = r7
        L_0x0072:
            if (r5 == 0) goto L_0x007e
            goto L_0x007b
        L_0x0075:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x008b }
            if (r5 == 0) goto L_0x007e
        L_0x007b:
            r5.dispose()
        L_0x007e:
            r9 = r2
            r10 = r3
            r12 = r5
            r5 = 1
            r2 = r13
            r3 = r0
            r4 = r1
            r6 = r9
            r7 = r10
            r2.processLoadedFeaturedStickers(r3, r4, r5, r6, r7)
            return
        L_0x008b:
            r6 = move-exception
            if (r5 == 0) goto L_0x0091
            r5.dispose()
        L_0x0091:
            goto L_0x0093
        L_0x0092:
            throw r6
        L_0x0093:
            goto L_0x0092
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m810x418ff9d5():void");
    }

    /* renamed from: lambda$loadFeaturedStickers$23$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m812xCLASSNAMEcCLASSNAME(TLRPC.TL_messages_getFeaturedStickers req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda120(this, response, req));
    }

    /* renamed from: lambda$loadFeaturedStickers$22$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m811x47CLASSNAME(TLObject response, TLRPC.TL_messages_getFeaturedStickers req) {
        TLObject tLObject = response;
        if (tLObject instanceof TLRPC.TL_messages_featuredStickers) {
            TLRPC.TL_messages_featuredStickers res = (TLRPC.TL_messages_featuredStickers) tLObject;
            processLoadedFeaturedStickers(res.sets, res.unread, false, (int) (System.currentTimeMillis() / 1000), res.hash);
            TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers = req;
            return;
        }
        processLoadedFeaturedStickers((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, false, (int) (System.currentTimeMillis() / 1000), req.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<TLRPC.StickerSetCovered> res, ArrayList<Long> unreadStickers, boolean cache, int date, long hash) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda149(this));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda2(this, cache, res, date, hash, unreadStickers));
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$24$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m856x13e85886() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$28$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m860x1var_fe02(boolean cache, ArrayList res, int date, long hash, ArrayList unreadStickers) {
        ArrayList arrayList = res;
        int i = date;
        long j = hash;
        long j2 = 0;
        if ((cache && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!cache && arrayList == null && j == 0)) {
            MediaDataController$$ExternalSyntheticLambda106 mediaDataController$$ExternalSyntheticLambda106 = new MediaDataController$$ExternalSyntheticLambda106(this, arrayList, j);
            if (arrayList == null && !cache) {
                j2 = 1000;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda106, j2);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                ArrayList<TLRPC.StickerSetCovered> stickerSetsNew = new ArrayList<>();
                LongSparseArray<TLRPC.StickerSetCovered> stickerSetsByIdNew = new LongSparseArray<>();
                for (int a = 0; a < res.size(); a++) {
                    TLRPC.StickerSetCovered stickerSet = (TLRPC.StickerSetCovered) arrayList.get(a);
                    stickerSetsNew.add(stickerSet);
                    stickerSetsByIdNew.put(stickerSet.set.id, stickerSet);
                }
                if (!cache) {
                    putFeaturedStickersToCache(stickerSetsNew, unreadStickers, date, hash);
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda110(this, unreadStickers, stickerSetsByIdNew, stickerSetsNew, hash, date));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda22(this, i));
            putFeaturedStickersToCache((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, date, 0);
        }
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$25$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m857xd6d4c1e5(ArrayList res, long hash) {
        if (!(res == null || hash == 0)) {
            this.loadFeaturedHash = hash;
        }
        loadFeaturedStickers(false, false);
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$26$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m858x99CLASSNAMEb44(ArrayList unreadStickers, LongSparseArray stickerSetsByIdNew, ArrayList stickerSetsNew, long hash, int date) {
        this.unreadStickerSets = unreadStickers;
        this.featuredStickerSetsById = stickerSetsByIdNew;
        this.featuredStickerSets = stickerSetsNew;
        this.loadFeaturedHash = hash;
        this.loadFeaturedDate = date;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    /* renamed from: lambda$processLoadedFeaturedStickers$27$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m859x5cad94a3(int date) {
        this.loadFeaturedDate = date;
    }

    private void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> stickers, ArrayList<Long> unreadStickers, int date, long hash) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda111(this, stickers != null ? new ArrayList<>(stickers) : null, unreadStickers, date, hash));
    }

    /* renamed from: lambda$putFeaturedStickersToCache$29$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m876xe4a4CLASSNAME(ArrayList stickersFinal, ArrayList unreadStickers, int date, long hash) {
        if (stickersFinal != null) {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.StickerSetCovered) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                NativeByteBuffer data2 = new NativeByteBuffer((unreadStickers.size() * 8) + 4);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.StickerSetCovered) stickersFinal.get(a2)).serializeToStream(data);
                }
                data2.writeInt32(unreadStickers.size());
                for (int a3 = 0; a3 < unreadStickers.size(); a3++) {
                    data2.writeInt64(((Long) unreadStickers.get(a3)).longValue());
                }
                state.bindInteger(1, 1);
                state.bindByteBuffer(2, data);
                state.bindByteBuffer(3, data2);
                state.bindInteger(4, date);
                state.bindLong(5, hash);
                state.step();
                data.reuse();
                data2.reuse();
                state.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            state2.requery();
            state2.bindInteger(1, date);
            state2.step();
            state2.dispose();
        }
    }

    private long calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> sets) {
        if (sets == null || sets.isEmpty()) {
            return 0;
        }
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, set.id);
                if (this.unreadStickerSets.contains(Long.valueOf(set.id))) {
                    acc = calcHash(acc, 1);
                }
            }
        }
        return acc;
    }

    public static long calcHash(long hash, long id) {
        return (((hash ^ (id >> 21)) ^ (id << 35)) ^ (id >> 4)) + id;
    }

    public void markFaturedStickersAsRead(boolean query) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (query) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_readFeaturedStickers(), MediaDataController$$ExternalSyntheticLambda50.INSTANCE);
            }
        }
    }

    static /* synthetic */ void lambda$markFaturedStickersAsRead$30(TLObject response, TLRPC.TL_error error) {
    }

    public long getFeaturesStickersHashWithoutUnread() {
        long acc = 0;
        for (int a = 0; a < this.featuredStickerSets.size(); a++) {
            TLRPC.StickerSet set = this.featuredStickerSets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, set.id);
            }
        }
        return acc;
    }

    public void markFaturedStickersByIdAsRead(long id) {
        if (this.unreadStickerSets.contains(Long.valueOf(id)) && !this.readingStickerSets.contains(Long.valueOf(id))) {
            this.readingStickerSets.add(Long.valueOf(id));
            TLRPC.TL_messages_readFeaturedStickers req = new TLRPC.TL_messages_readFeaturedStickers();
            req.id.add(Long.valueOf(id));
            getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda51.INSTANCE);
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda66(this, id), 1000);
        }
    }

    static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$31(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$markFaturedStickersByIdAsRead$32$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m849xefvar_a9(long id) {
        this.unreadStickerSets.remove(Long.valueOf(id));
        this.readingStickerSets.remove(Long.valueOf(id));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
    }

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void verifyAnimatedStickerMessage(TLRPC.Message message) {
        verifyAnimatedStickerMessage(message, false);
    }

    public void verifyAnimatedStickerMessage(TLRPC.Message message, boolean safe) {
        if (message != null) {
            TLRPC.Document document = MessageObject.getDocument(message);
            String name = MessageObject.getStickerSetName(document);
            if (!TextUtils.isEmpty(name)) {
                TLRPC.TL_messages_stickerSet stickerSet = this.stickerSetsByName.get(name);
                if (stickerSet != null) {
                    int N = stickerSet.documents.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.Document sticker = (TLRPC.Document) stickerSet.documents.get(a);
                        if (sticker.id == document.id && sticker.dc_id == document.dc_id) {
                            message.stickerVerified = 1;
                            return;
                        }
                    }
                } else if (safe) {
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda126(this, message, name));
                } else {
                    m904x60ca8350(message, name);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: verifyAnimatedStickerMessageInternal */
    public void m904x60ca8350(TLRPC.Message message, String name) {
        ArrayList<TLRPC.Message> messages = this.verifyingMessages.get(name);
        if (messages == null) {
            messages = new ArrayList<>();
            this.verifyingMessages.put(name, messages);
        }
        messages.add(message);
        TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
        req.stickerset = MessageObject.getInputStickerSet(message);
        getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda40(this, name));
    }

    /* renamed from: lambda$verifyAnimatedStickerMessageInternal$35$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m906xfvar_b(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda97(this, name, response));
    }

    /* renamed from: lambda$verifyAnimatedStickerMessageInternal$34$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m905x3CLASSNAMEcc(String name, TLObject response) {
        ArrayList<TLRPC.Message> arrayList = this.verifyingMessages.get(name);
        if (response != null) {
            TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) response;
            storeTempStickerSet(set);
            int N2 = arrayList.size();
            for (int b = 0; b < N2; b++) {
                TLRPC.Message m = arrayList.get(b);
                TLRPC.Document d = MessageObject.getDocument(m);
                int a = 0;
                int N = set.documents.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.Document sticker = (TLRPC.Document) set.documents.get(a);
                    if (sticker.id == d.id && sticker.dc_id == d.dc_id) {
                        m.stickerVerified = 1;
                        break;
                    }
                    a++;
                }
                if (m.stickerVerified == 0) {
                    m.stickerVerified = 2;
                }
            }
        } else {
            int N22 = arrayList.size();
            for (int b2 = 0; b2 < N22; b2++) {
                arrayList.get(b2).stickerVerified = 2;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didVerifyMessagesStickers, arrayList);
        getMessagesStorage().updateMessageVerifyFlags(arrayList);
    }

    public void loadArchivedStickersCount(int type, boolean cache) {
        boolean z = true;
        if (cache) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int count = preferences.getInt("archivedStickersCount" + type, -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
                return;
            }
            this.archivedStickersCount[type] = count;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
            return;
        }
        TLRPC.TL_messages_getArchivedStickers req = new TLRPC.TL_messages_getArchivedStickers();
        req.limit = 0;
        if (type != 1) {
            z = false;
        }
        req.masks = z;
        getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda19(this, type));
    }

    /* renamed from: lambda$loadArchivedStickersCount$37$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m802x3d222a1d(int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda134(this, error, response, type));
    }

    /* renamed from: lambda$loadArchivedStickersCount$36$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m801x7a35c0be(TLRPC.TL_error error, TLObject response, int type) {
        if (error == null) {
            TLRPC.TL_messages_archivedStickers res = (TLRPC.TL_messages_archivedStickers) response;
            this.archivedStickersCount[type] = res.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + type, res.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(type));
        }
    }

    private void processLoadStickersResponse(int type, TLRPC.TL_messages_allStickers res) {
        TLRPC.TL_messages_allStickers tL_messages_allStickers = res;
        ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
        long j = 1000;
        if (tL_messages_allStickers.sets.isEmpty()) {
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
            return;
        }
        LongSparseArray<TLRPC.TL_messages_stickerSet> newStickerSets = new LongSparseArray<>();
        int a = 0;
        while (a < tL_messages_allStickers.sets.size()) {
            TLRPC.StickerSet stickerSet = (TLRPC.StickerSet) tL_messages_allStickers.sets.get(a);
            TLRPC.TL_messages_stickerSet oldSet = this.stickerSetsById.get(stickerSet.id);
            if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                newStickerArray.add((Object) null);
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = new TLRPC.TL_inputStickerSetID();
                req.stickerset.id = stickerSet.id;
                req.stickerset.access_hash = stickerSet.access_hash;
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = oldSet;
                MediaDataController$$ExternalSyntheticLambda43 mediaDataController$$ExternalSyntheticLambda43 = r0;
                MediaDataController$$ExternalSyntheticLambda43 mediaDataController$$ExternalSyntheticLambda432 = new MediaDataController$$ExternalSyntheticLambda43(this, newStickerArray, a, newStickerSets, stickerSet, res, type);
                getConnectionsManager().sendRequest(req, mediaDataController$$ExternalSyntheticLambda43);
            } else {
                oldSet.set.archived = stickerSet.archived;
                oldSet.set.installed = stickerSet.installed;
                oldSet.set.official = stickerSet.official;
                newStickerSets.put(oldSet.set.id, oldSet);
                newStickerArray.add(oldSet);
                if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
                    processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers.hash);
                }
            }
            a++;
            tL_messages_allStickers = res;
            j = 1000;
        }
    }

    /* renamed from: lambda$processLoadStickersResponse$39$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m851x9var_e56(ArrayList newStickerArray, int index, LongSparseArray newStickerSets, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers res, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda119(this, response, newStickerArray, index, newStickerSets, stickerSet, res, type));
    }

    /* renamed from: lambda$processLoadStickersResponse$38$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m850xdc2a34f7(TLObject response, ArrayList newStickerArray, int index, LongSparseArray newStickerSets, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers res, int type) {
        ArrayList arrayList = newStickerArray;
        TLRPC.TL_messages_allStickers tL_messages_allStickers = res;
        TLRPC.TL_messages_stickerSet res1 = (TLRPC.TL_messages_stickerSet) response;
        newStickerArray.set(index, res1);
        newStickerSets.put(stickerSet.id, res1);
        if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
            int a1 = 0;
            while (a1 < newStickerArray.size()) {
                if (newStickerArray.get(a1) == null) {
                    newStickerArray.remove(a1);
                    a1--;
                }
                a1++;
            }
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
        }
    }

    public void loadStickersByEmojiOrName(String name, boolean isEmoji, boolean cache) {
        if (this.loadingDiceStickerSets.contains(name)) {
            return;
        }
        if (!isEmoji || this.diceStickerSetsByEmoji.get(name) == null) {
            this.loadingDiceStickerSets.add(name);
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda99(this, name, isEmoji));
                return;
            }
            TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
            if (isEmoji) {
                TLRPC.TL_inputStickerSetDice inputStickerSetDice = new TLRPC.TL_inputStickerSetDice();
                inputStickerSetDice.emoticon = name;
                req.stickerset = inputStickerSetDice;
            } else {
                TLRPC.TL_inputStickerSetShortName inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSetShortName.short_name = name;
                req.stickerset = inputStickerSetShortName;
            }
            getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda42(this, name, isEmoji));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        r2.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003f, code lost:
        if (r2 == null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0042, code lost:
        processLoadedDiceStickers(r11, r12, r0, true, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0035, code lost:
        if (r2 != null) goto L_0x0037;
     */
    /* renamed from: lambda$loadStickersByEmojiOrName$40$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m846x5a340ed5(java.lang.String r11, boolean r12) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r10.getMessagesStorage()     // Catch:{ all -> 0x003b }
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ all -> 0x003b }
            java.lang.String r4 = "SELECT data, date FROM stickers_dice WHERE emoji = ?"
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]     // Catch:{ all -> 0x003b }
            r7 = 0
            r6[r7] = r11     // Catch:{ all -> 0x003b }
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r6)     // Catch:{ all -> 0x003b }
            r2 = r3
            boolean r3 = r2.next()     // Catch:{ all -> 0x003b }
            if (r3 == 0) goto L_0x0035
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r7)     // Catch:{ all -> 0x003b }
            if (r3 == 0) goto L_0x0030
            int r4 = r3.readInt32(r7)     // Catch:{ all -> 0x003b }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r3, r4, r7)     // Catch:{ all -> 0x003b }
            r0 = r4
            r3.reuse()     // Catch:{ all -> 0x003b }
        L_0x0030:
            int r4 = r2.intValue(r5)     // Catch:{ all -> 0x003b }
            r1 = r4
        L_0x0035:
            if (r2 == 0) goto L_0x0042
        L_0x0037:
            r2.dispose()
            goto L_0x0042
        L_0x003b:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x004c }
            if (r2 == 0) goto L_0x0042
            goto L_0x0037
        L_0x0042:
            r8 = 1
            r4 = r10
            r5 = r11
            r6 = r12
            r7 = r0
            r9 = r1
            r4.processLoadedDiceStickers(r5, r6, r7, r8, r9)
            return
        L_0x004c:
            r3 = move-exception
            if (r2 == 0) goto L_0x0052
            r2.dispose()
        L_0x0052:
            goto L_0x0054
        L_0x0053:
            throw r3
        L_0x0054:
            goto L_0x0053
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m846x5a340ed5(java.lang.String, boolean):void");
    }

    /* renamed from: lambda$loadStickersByEmojiOrName$42$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m848xe00ce193(String name, boolean isEmoji, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda135(this, error, response, name, isEmoji));
    }

    /* renamed from: lambda$loadStickersByEmojiOrName$41$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m847x1d207834(TLRPC.TL_error error, TLObject response, String name, boolean isEmoji) {
        if (BuildConfig.DEBUG && error != null) {
            return;
        }
        if (response instanceof TLRPC.TL_messages_stickerSet) {
            String str = name;
            boolean z = isEmoji;
            processLoadedDiceStickers(str, z, (TLRPC.TL_messages_stickerSet) response, false, (int) (System.currentTimeMillis() / 1000));
            return;
        }
        processLoadedDiceStickers(name, isEmoji, (TLRPC.TL_messages_stickerSet) null, false, (int) (System.currentTimeMillis() / 1000));
    }

    private void processLoadedDiceStickers(String name, boolean isEmoji, TLRPC.TL_messages_stickerSet res, boolean cache, int date) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda95(this, name));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda4(this, cache, res, date, name, isEmoji));
    }

    /* renamed from: lambda$processLoadedDiceStickers$43$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m852x217dca62(String name) {
        this.loadingDiceStickerSets.remove(name);
    }

    /* renamed from: lambda$processLoadedDiceStickers$46$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m855x6a43067f(boolean cache, TLRPC.TL_messages_stickerSet res, int date, String name, boolean isEmoji) {
        long j = 1000;
        if ((cache && (res == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) date)) >= 86400)) || (!cache && res == null)) {
            MediaDataController$$ExternalSyntheticLambda100 mediaDataController$$ExternalSyntheticLambda100 = new MediaDataController$$ExternalSyntheticLambda100(this, name, isEmoji);
            if (res != null || cache) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda100, j);
            if (res == null) {
                return;
            }
        }
        if (res != null) {
            if (!cache) {
                putDiceStickersToCache(name, res, date);
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda98(this, name, res));
        } else if (!cache) {
            putDiceStickersToCache(name, (TLRPC.TL_messages_stickerSet) null, date);
        }
    }

    /* renamed from: lambda$processLoadedDiceStickers$44$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m853xe46a33c1(String name, boolean isEmoji) {
        loadStickersByEmojiOrName(name, isEmoji, false);
    }

    /* renamed from: lambda$processLoadedDiceStickers$45$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m854xa7569d20(String name, TLRPC.TL_messages_stickerSet res) {
        this.diceStickerSetsByEmoji.put(name, res);
        this.diceEmojiStickerSetsById.put(res.set.id, name);
        getNotificationCenter().postNotificationName(NotificationCenter.diceStickersDidLoad, name);
    }

    private void putDiceStickersToCache(String emoji, TLRPC.TL_messages_stickerSet stickers, int date) {
        if (!TextUtils.isEmpty(emoji)) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda141(this, stickers, emoji, date));
        }
    }

    /* renamed from: lambda$putDiceStickersToCache$47$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m873x92c7d7c2(TLRPC.TL_messages_stickerSet stickers, String emoji, int date) {
        if (stickers != null) {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_dice VALUES(?, ?, ?)");
                state.requery();
                NativeByteBuffer data = new NativeByteBuffer(stickers.getObjectSize());
                stickers.serializeToStream(data);
                state.bindString(1, emoji);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.step();
                data.reuse();
                state.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_dice SET date = ?");
            state2.requery();
            state2.bindInteger(1, date);
            state2.step();
            state2.dispose();
        }
    }

    public void loadStickers(int type, boolean cache, boolean useHash) {
        loadStickers(type, cache, useHash, false);
    }

    public void loadStickers(int type, boolean cache, boolean force, boolean scheduleIfLoading) {
        TLObject req;
        if (!this.loadingStickers[type]) {
            if (type == 3) {
                if (this.featuredStickerSets.isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                    return;
                }
            } else if (type != 4) {
                loadArchivedStickersCount(type, cache);
            }
            this.loadingStickers[type] = true;
            if (cache) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda11(this, type));
            } else if (type == 3) {
                TLRPC.TL_messages_allStickers response = new TLRPC.TL_messages_allStickers();
                response.hash = this.loadFeaturedHash;
                int size = this.featuredStickerSets.size();
                for (int a = 0; a < size; a++) {
                    response.sets.add(this.featuredStickerSets.get(a).set);
                }
                processLoadStickersResponse(type, response);
            } else if (type == 4) {
                TLRPC.TL_messages_getStickerSet req2 = new TLRPC.TL_messages_getStickerSet();
                req2.stickerset = new TLRPC.TL_inputStickerSetAnimatedEmoji();
                getConnectionsManager().sendRequest(req2, new MediaDataController$$ExternalSyntheticLambda23(this, type));
            } else {
                long hash = 0;
                if (type == 0) {
                    req = new TLRPC.TL_messages_getAllStickers();
                    TLRPC.TL_messages_getAllStickers tL_messages_getAllStickers = (TLRPC.TL_messages_getAllStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getAllStickers.hash = hash;
                } else {
                    req = new TLRPC.TL_messages_getMaskStickers();
                    TLRPC.TL_messages_getMaskStickers tL_messages_getMaskStickers = (TLRPC.TL_messages_getMaskStickers) req;
                    if (!force) {
                        hash = this.loadHash[type];
                    }
                    tL_messages_getMaskStickers.hash = hash;
                }
                getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda24(this, type, hash));
            }
        } else if (scheduleIfLoading) {
            this.scheduledLoadStickers[type] = new MediaDataController$$ExternalSyntheticLambda60(this, type, force);
        }
    }

    /* renamed from: lambda$loadStickers$48$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m841x381931a2(int type, boolean force) {
        loadStickers(type, false, force, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005e, code lost:
        if (r4 != null) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0060, code lost:
        r4.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0068, code lost:
        if (r4 == null) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006b, code lost:
        processLoadedStickers(r14, r0, true, r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0074, code lost:
        return;
     */
    /* renamed from: lambda$loadStickers$49$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m842xfb059b01(int r14) {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r4 = 0
            org.telegram.messenger.MessagesStorage r5 = r13.getMessagesStorage()     // Catch:{ all -> 0x0064 }
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ all -> 0x0064 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0064 }
            r6.<init>()     // Catch:{ all -> 0x0064 }
            java.lang.String r7 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r6.append(r7)     // Catch:{ all -> 0x0064 }
            int r7 = r14 + 1
            r6.append(r7)     // Catch:{ all -> 0x0064 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0064 }
            r7 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x0064 }
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r8)     // Catch:{ all -> 0x0064 }
            r4 = r5
            boolean r5 = r4.next()     // Catch:{ all -> 0x0064 }
            if (r5 == 0) goto L_0x005e
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r7)     // Catch:{ all -> 0x0064 }
            if (r5 == 0) goto L_0x0053
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0064 }
            r6.<init>()     // Catch:{ all -> 0x0064 }
            r0 = r6
            int r6 = r5.readInt32(r7)     // Catch:{ all -> 0x0064 }
            r8 = 0
        L_0x003f:
            if (r8 >= r6) goto L_0x0050
            int r9 = r5.readInt32(r7)     // Catch:{ all -> 0x0064 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r5, r9, r7)     // Catch:{ all -> 0x0064 }
            r0.add(r9)     // Catch:{ all -> 0x0064 }
            int r8 = r8 + 1
            goto L_0x003f
        L_0x0050:
            r5.reuse()     // Catch:{ all -> 0x0064 }
        L_0x0053:
            r6 = 1
            int r6 = r4.intValue(r6)     // Catch:{ all -> 0x0064 }
            r1 = r6
            long r6 = calcStickersHash(r0)     // Catch:{ all -> 0x0064 }
            r2 = r6
        L_0x005e:
            if (r4 == 0) goto L_0x006b
        L_0x0060:
            r4.dispose()
            goto L_0x006b
        L_0x0064:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x0075 }
            if (r4 == 0) goto L_0x006b
            goto L_0x0060
        L_0x006b:
            r9 = 1
            r6 = r13
            r7 = r14
            r8 = r0
            r10 = r1
            r11 = r2
            r6.processLoadedStickers(r7, r8, r9, r10, r11)
            return
        L_0x0075:
            r5 = move-exception
            if (r4 == 0) goto L_0x007b
            r4.dispose()
        L_0x007b:
            goto L_0x007d
        L_0x007c:
            throw r5
        L_0x007d:
            goto L_0x007c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m842xfb059b01(int):void");
    }

    /* renamed from: lambda$loadStickers$50$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m843xbb56a92b(int type, TLObject response, TLRPC.TL_error error) {
        TLObject tLObject = response;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            ArrayList<TLRPC.TL_messages_stickerSet> newStickerArray = new ArrayList<>();
            newStickerArray.add((TLRPC.TL_messages_stickerSet) tLObject);
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(newStickerArray));
            return;
        }
        processLoadedStickers(type, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), 0);
    }

    /* renamed from: lambda$loadStickers$52$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m845x412f7be9(int type, long hash, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda118(this, response, type, hash));
    }

    /* renamed from: lambda$loadStickers$51$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m844x7e43128a(TLObject response, int type, long hash) {
        if (response instanceof TLRPC.TL_messages_allStickers) {
            processLoadStickersResponse(type, (TLRPC.TL_messages_allStickers) response);
            return;
        }
        processLoadedStickers(type, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), hash);
    }

    private void putStickersToCache(int type, ArrayList<TLRPC.TL_messages_stickerSet> stickers, int date, long hash) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda103(this, stickers != null ? new ArrayList<>(stickers) : null, type, date, hash));
    }

    /* renamed from: lambda$putStickersToCache$53$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m880x1d77462e(ArrayList stickersFinal, int type, int date, long hash) {
        if (stickersFinal != null) {
            try {
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                state.requery();
                int size = 4;
                for (int a = 0; a < stickersFinal.size(); a++) {
                    size += ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a)).getObjectSize();
                }
                NativeByteBuffer data = new NativeByteBuffer(size);
                data.writeInt32(stickersFinal.size());
                for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                    ((TLRPC.TL_messages_stickerSet) stickersFinal.get(a2)).serializeToStream(data);
                }
                state.bindInteger(1, type + 1);
                state.bindByteBuffer(2, data);
                state.bindInteger(3, date);
                state.bindLong(4, hash);
                state.step();
                data.reuse();
                state.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            state2.requery();
            state2.bindLong(1, (long) date);
            state2.step();
            state2.dispose();
        }
    }

    public String getStickerSetName(long setId) {
        TLRPC.TL_messages_stickerSet stickerSet = this.stickerSetsById.get(setId);
        if (stickerSet != null) {
            return stickerSet.set.short_name;
        }
        TLRPC.StickerSetCovered stickerSetCovered = this.featuredStickerSetsById.get(setId);
        if (stickerSetCovered != null) {
            return stickerSetCovered.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetID) {
                return attribute.stickerset.id;
            } else {
                return -1;
            }
        }
        return -1;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return null;
            } else {
                return attribute.stickerset;
            }
        }
        return null;
    }

    private static long calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            TLRPC.StickerSet set = sets.get(a).set;
            if (!set.archived) {
                acc = calcHash(acc, (long) set.hash);
            }
        }
        return acc;
    }

    private void processLoadedStickers(int type, ArrayList<TLRPC.TL_messages_stickerSet> res, boolean cache, int date, long hash) {
        int i = type;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda33(this, type));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda1(this, cache, res, date, hash, type));
    }

    /* renamed from: lambda$processLoadedStickers$54$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m866xae61453b(int type) {
        this.loadingStickers[type] = false;
        this.stickersLoaded[type] = true;
        Runnable[] runnableArr = this.scheduledLoadStickers;
        if (runnableArr[type] != null) {
            runnableArr[type].run();
            this.scheduledLoadStickers[type] = null;
        }
    }

    /* renamed from: lambda$processLoadedStickers$58$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m870xba12eab7(boolean cache, ArrayList res, int date, long hash, int type) {
        TLRPC.TL_messages_stickerSet stickerSet;
        TLRPC.TL_messages_stickerSet stickerSet2;
        MediaDataController mediaDataController = this;
        ArrayList arrayList = res;
        int i = date;
        long j = 0;
        if ((cache && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!cache && arrayList == null && hash == 0)) {
            MediaDataController$$ExternalSyntheticLambda108 mediaDataController$$ExternalSyntheticLambda108 = new MediaDataController$$ExternalSyntheticLambda108(this, res, hash, type);
            if (arrayList == null && !cache) {
                j = 1000;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda108, j);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSetsNew = new ArrayList<>();
                LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsByIdNew = new LongSparseArray<>();
                HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByNameNew = new HashMap<>();
                LongSparseArray<TLRPC.TL_messages_stickerSet> stickersByEmojiNew = new LongSparseArray<>();
                LongSparseArray<String> stickersByIdNew = new LongSparseArray<>();
                HashMap<String, TLRPC.TL_messages_stickerSet> allStickersNew = new HashMap<>();
                int a = 0;
                while (a < res.size()) {
                    TLRPC.TL_messages_stickerSet stickerSet3 = (TLRPC.TL_messages_stickerSet) arrayList.get(a);
                    if (stickerSet3 == null) {
                    } else if (mediaDataController.removingStickerSetsUndos.indexOfKey(stickerSet3.set.id) < 0) {
                        stickerSetsNew.add(stickerSet3);
                        stickerSetsByIdNew.put(stickerSet3.set.id, stickerSet3);
                        stickerSetsByNameNew.put(stickerSet3.set.short_name, stickerSet3);
                        for (int b = 0; b < stickerSet3.documents.size(); b++) {
                            TLRPC.Document document = (TLRPC.Document) stickerSet3.documents.get(b);
                            if (document != null) {
                                if (!(document instanceof TLRPC.TL_documentEmpty)) {
                                    stickersByIdNew.put(document.id, document);
                                }
                            }
                        }
                        if (!stickerSet3.set.archived) {
                            int b2 = 0;
                            while (b2 < stickerSet3.packs.size()) {
                                TLRPC.TL_stickerPack stickerPack = (TLRPC.TL_stickerPack) stickerSet3.packs.get(b2);
                                if (stickerPack == null) {
                                    stickerSet = stickerSet3;
                                } else if (stickerPack.emoticon == null) {
                                    stickerSet = stickerSet3;
                                } else {
                                    stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                                    ArrayList<TLRPC.Document> arrayList2 = (ArrayList) allStickersNew.get(stickerPack.emoticon);
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList<>();
                                        allStickersNew.put(stickerPack.emoticon, arrayList2);
                                    }
                                    int c = 0;
                                    while (c < stickerPack.documents.size()) {
                                        Long id = stickerPack.documents.get(c);
                                        if (stickersByEmojiNew.indexOfKey(id.longValue()) < 0) {
                                            stickerSet2 = stickerSet3;
                                            stickersByEmojiNew.put(id.longValue(), stickerPack.emoticon);
                                        } else {
                                            stickerSet2 = stickerSet3;
                                        }
                                        TLRPC.Document sticker = (TLRPC.Document) stickersByIdNew.get(id.longValue());
                                        if (sticker != null) {
                                            arrayList2.add(sticker);
                                        }
                                        c++;
                                        ArrayList arrayList3 = res;
                                        stickerSet3 = stickerSet2;
                                    }
                                    stickerSet = stickerSet3;
                                }
                                b2++;
                                ArrayList arrayList4 = res;
                                stickerSet3 = stickerSet;
                            }
                        }
                    }
                    a++;
                    mediaDataController = this;
                    arrayList = res;
                }
                if (!cache) {
                    putStickersToCache(type, stickerSetsNew, date, hash);
                }
                HashMap<String, TLRPC.TL_messages_stickerSet> hashMap = stickerSetsByNameNew;
                LongSparseArray<TLRPC.TL_messages_stickerSet> longSparseArray = stickerSetsByIdNew;
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda56(this, type, stickerSetsByIdNew, stickerSetsByNameNew, stickerSetsNew, hash, date, stickersByIdNew, allStickersNew, stickersByEmojiNew));
            } catch (Throwable e) {
                FileLog.e(e);
            }
            int i2 = type;
        } else if (!cache) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda44(this, type, i));
            putStickersToCache(type, (ArrayList<TLRPC.TL_messages_stickerSet>) null, date, 0);
        } else {
            int i3 = type;
        }
    }

    /* renamed from: lambda$processLoadedStickers$55$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m867x714dae9a(ArrayList res, long hash, int type) {
        if (!(res == null || hash == 0)) {
            this.loadHash[type] = hash;
        }
        loadStickers(type, false, false);
    }

    /* renamed from: lambda$processLoadedStickers$56$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m868x343a17f9(int type, LongSparseArray stickerSetsByIdNew, HashMap stickerSetsByNameNew, ArrayList stickerSetsNew, long hash, int date, LongSparseArray stickersByIdNew, HashMap allStickersNew, LongSparseArray stickersByEmojiNew) {
        int i = type;
        LongSparseArray longSparseArray = stickerSetsByIdNew;
        HashMap hashMap = allStickersNew;
        for (int a = 0; a < this.stickerSets[i].size(); a++) {
            TLRPC.StickerSet set = this.stickerSets[i].get(a).set;
            this.stickerSetsById.remove(set.id);
            this.stickerSetsByName.remove(set.short_name);
            if (!(i == 3 || i == 4)) {
                this.installedStickerSetsById.remove(set.id);
            }
        }
        for (int a2 = 0; a2 < stickerSetsByIdNew.size(); a2++) {
            this.stickerSetsById.put(stickerSetsByIdNew.keyAt(a2), (TLRPC.TL_messages_stickerSet) stickerSetsByIdNew.valueAt(a2));
            if (!(i == 3 || i == 4)) {
                this.installedStickerSetsById.put(stickerSetsByIdNew.keyAt(a2), (TLRPC.TL_messages_stickerSet) stickerSetsByIdNew.valueAt(a2));
            }
        }
        HashMap hashMap2 = stickerSetsByNameNew;
        this.stickerSetsByName.putAll(stickerSetsByNameNew);
        this.stickerSets[i] = stickerSetsNew;
        this.loadHash[i] = hash;
        this.loadDate[i] = date;
        this.stickersByIds[i] = stickersByIdNew;
        if (i == 0) {
            this.allStickers = hashMap;
            this.stickersByEmoji = stickersByEmojiNew;
        } else {
            LongSparseArray longSparseArray2 = stickersByEmojiNew;
            if (i == 3) {
                this.allStickersFeatured = hashMap;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    /* renamed from: lambda$processLoadedStickers$57$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m869xvar_(int type, int date) {
        this.loadDate[type] = date;
    }

    public boolean cancelRemovingStickerSet(long id) {
        Runnable undoAction = this.removingStickerSetsUndos.get(id);
        if (undoAction == null) {
            return false;
        }
        undoAction.run();
        return true;
    }

    public void preloadStickerSetThumb(TLRPC.TL_messages_stickerSet stickerSet) {
        ArrayList<TLRPC.Document> documents;
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
        if (thumb != null && (documents = stickerSet.documents) != null && !documents.isEmpty()) {
            loadStickerSetThumbInternal(thumb, stickerSet, documents.get(0), stickerSet.set.thumb_version);
        }
    }

    public void preloadStickerSetThumb(TLRPC.StickerSetCovered stickerSet) {
        TLRPC.Document sticker;
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
        if (thumb != null) {
            if (stickerSet.cover != null) {
                sticker = stickerSet.cover;
            } else if (!stickerSet.covers.isEmpty()) {
                sticker = stickerSet.covers.get(0);
            } else {
                return;
            }
            loadStickerSetThumbInternal(thumb, stickerSet, sticker, stickerSet.set.thumb_version);
        }
    }

    private void loadStickerSetThumbInternal(TLRPC.PhotoSize thumb, Object parentObject, TLRPC.Document sticker, int thumbVersion) {
        ImageLocation imageLocation = ImageLocation.getForSticker(thumb, sticker, thumbVersion);
        if (imageLocation != null) {
            getFileLoader().loadFile(imageLocation, parentObject, imageLocation.imageType == 1 ? "tgs" : "webp", 2, 1);
        }
    }

    public void toggleStickerSet(Context context, TLObject stickerSetObject, int toggle, BaseFragment baseFragment, boolean showSettings, boolean showTooltip) {
        TLRPC.TL_messages_stickerSet messages_stickerSet;
        TLRPC.StickerSet stickerSet;
        int currentIndex;
        TLRPC.StickerSet stickerSet2;
        Context context2 = context;
        TLObject tLObject = stickerSetObject;
        int i = toggle;
        BaseFragment baseFragment2 = baseFragment;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) tLObject;
            messages_stickerSet = messages_stickerSet2;
            stickerSet = messages_stickerSet2.set;
        } else if (tLObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSet stickerSet3 = ((TLRPC.StickerSetCovered) tLObject).set;
            if (i != 2) {
                TLRPC.TL_messages_stickerSet messages_stickerSet3 = this.stickerSetsById.get(stickerSet3.id);
                if (messages_stickerSet3 != null) {
                    messages_stickerSet = messages_stickerSet3;
                    stickerSet = stickerSet3;
                } else {
                    return;
                }
            } else {
                messages_stickerSet = null;
                stickerSet = stickerSet3;
            }
        } else {
            BaseFragment baseFragment3 = baseFragment2;
            throw new IllegalArgumentException("Invalid type of the given stickerSetObject: " + stickerSetObject.getClass());
        }
        int type = stickerSet.masks;
        stickerSet.archived = i == 1;
        int a = 0;
        while (true) {
            if (a >= this.stickerSets[type].size()) {
                currentIndex = 0;
                break;
            }
            TLRPC.TL_messages_stickerSet set = this.stickerSets[type].get(a);
            if (set.set.id == stickerSet.id) {
                int currentIndex2 = a;
                this.stickerSets[type].remove(a);
                if (i == 2) {
                    this.stickerSets[type].add(0, set);
                } else {
                    this.stickerSetsById.remove(set.set.id);
                    this.installedStickerSetsById.remove(set.set.id);
                    this.stickerSetsByName.remove(set.set.short_name);
                }
                currentIndex = currentIndex2;
            } else {
                a++;
            }
        }
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        if (i != 2) {
            if (!showTooltip) {
                stickerSet2 = stickerSet;
                BaseFragment baseFragment4 = baseFragment2;
            } else if (baseFragment2 == null) {
                stickerSet2 = stickerSet;
                BaseFragment baseFragment5 = baseFragment2;
            } else {
                StickerSetBulletinLayout bulletinLayout = new StickerSetBulletinLayout(context2, tLObject, i);
                TLRPC.StickerSet stickerSet4 = stickerSet;
                Bulletin.UndoButton undoButton = new Bulletin.UndoButton(context2, false).setUndoAction(new MediaDataController$$ExternalSyntheticLambda130(this, stickerSet, type, currentIndex, messages_stickerSet)).setDelayedAction(new MediaDataController$$ExternalSyntheticLambda87(this, context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet4, type));
                bulletinLayout.setButton(undoButton);
                LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
                long j = stickerSet4.id;
                undoButton.getClass();
                longSparseArray.put(j, new MediaDataController$$ExternalSyntheticLambda7(undoButton));
                Bulletin.make(baseFragment2, (Bulletin.Layout) bulletinLayout, 2750).show();
                TLRPC.StickerSet stickerSet5 = stickerSet4;
                return;
            }
            toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet2, type, false);
        } else if (!cancelRemovingStickerSet(stickerSet.id)) {
            toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet, type, showTooltip);
            TLRPC.StickerSet stickerSet6 = stickerSet;
            BaseFragment baseFragment6 = baseFragment2;
        } else {
            TLRPC.StickerSet stickerSet7 = stickerSet;
            BaseFragment baseFragment7 = baseFragment2;
        }
    }

    /* renamed from: lambda$toggleStickerSet$59$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m895xe96d96bf(TLRPC.StickerSet stickerSet, int type, int finalCurrentIndex, TLRPC.TL_messages_stickerSet messages_stickerSet) {
        stickerSet.archived = false;
        this.stickerSets[type].add(finalCurrentIndex, messages_stickerSet);
        this.stickerSetsById.put(stickerSet.id, messages_stickerSet);
        this.installedStickerSetsById.put(stickerSet.id, messages_stickerSet);
        this.stickerSetsByName.put(stickerSet.short_name, messages_stickerSet);
        this.removingStickerSetsUndos.remove(stickerSet.id);
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
    }

    /* renamed from: lambda$toggleStickerSet$60$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m896xa9bea4e9(Context context, int toggle, BaseFragment baseFragment, boolean showSettings, TLObject stickerSetObject, TLRPC.StickerSet stickerSet, int type) {
        toggleStickerSetInternal(context, toggle, baseFragment, showSettings, stickerSetObject, stickerSet, type, false);
    }

    private void toggleStickerSetInternal(Context context, int toggle, BaseFragment baseFragment, boolean showSettings, TLObject stickerSetObject, TLRPC.StickerSet stickerSet, int type, boolean showTooltip) {
        int i = toggle;
        TLRPC.StickerSet stickerSet2 = stickerSet;
        TLRPC.TL_inputStickerSetID stickerSetID = new TLRPC.TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet2.access_hash;
        stickerSetID.id = stickerSet2.id;
        if (i != 0) {
            TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            req.archived = z;
            getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda46(this, stickerSet, baseFragment, showSettings, type, showTooltip, context, stickerSetObject));
            int i2 = type;
            return;
        }
        TLRPC.TL_messages_uninstallStickerSet req2 = new TLRPC.TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        getConnectionsManager().sendRequest(req2, new MediaDataController$$ExternalSyntheticLambda45(this, stickerSet2, type));
    }

    /* renamed from: lambda$toggleStickerSetInternal$62$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m898xeCLASSNAMEb1c4(TLRPC.StickerSet stickerSet, BaseFragment baseFragment, boolean showSettings, int type, boolean showTooltip, Context context, TLObject stickerSetObject, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda131(this, stickerSet, response, baseFragment, showSettings, type, error, showTooltip, context, stickerSetObject));
    }

    /* renamed from: lambda$toggleStickerSetInternal$61$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m897x29874865(TLRPC.StickerSet stickerSet, TLObject response, BaseFragment baseFragment, boolean showSettings, int type, TLRPC.TL_error error, boolean showTooltip, Context context, TLObject stickerSetObject) {
        this.removingStickerSetsUndos.remove(stickerSet.id);
        if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
            processStickerSetInstallResultArchive(baseFragment, showSettings, type, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
        }
        loadStickers(type, false, false, true);
        if (error == null && showTooltip && baseFragment != null) {
            Bulletin.make(baseFragment, (Bulletin.Layout) new StickerSetBulletinLayout(context, stickerSetObject, 2), 1500).show();
        }
    }

    /* renamed from: lambda$toggleStickerSetInternal$64$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m900x724CLASSNAME(TLRPC.StickerSet stickerSet, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda129(this, stickerSet, type));
    }

    /* renamed from: lambda$toggleStickerSetInternal$63$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m899xavar_b23(TLRPC.StickerSet stickerSet, int type) {
        this.removingStickerSetsUndos.remove(stickerSet.id);
        loadStickers(type, false, true);
    }

    public void toggleStickerSets(ArrayList<TLRPC.StickerSet> stickerSetList, int type, int toggle, BaseFragment baseFragment, boolean showSettings) {
        int i = toggle;
        int stickerSetListSize = stickerSetList.size();
        ArrayList<TLRPC.InputStickerSet> inputStickerSets = new ArrayList<>(stickerSetListSize);
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 >= stickerSetListSize) {
                break;
            }
            TLRPC.StickerSet stickerSet = stickerSetList.get(i2);
            TLRPC.InputStickerSet inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.access_hash = stickerSet.access_hash;
            inputStickerSet.id = stickerSet.id;
            inputStickerSets.add(inputStickerSet);
            if (i != 0) {
                if (i != 1) {
                    z = false;
                }
                stickerSet.archived = z;
            }
            int a = 0;
            int size = this.stickerSets[type].size();
            while (true) {
                if (a >= size) {
                    break;
                }
                TLRPC.TL_messages_stickerSet set = this.stickerSets[type].get(a);
                if (set.set.id == inputStickerSet.id) {
                    this.stickerSets[type].remove(a);
                    if (i == 2) {
                        this.stickerSets[type].add(0, set);
                    } else {
                        this.stickerSetsById.remove(set.set.id);
                        this.installedStickerSetsById.remove(set.set.id);
                        this.stickerSetsByName.remove(set.set.short_name);
                    }
                } else {
                    a++;
                }
            }
            i2++;
        }
        ArrayList<TLRPC.StickerSet> arrayList = stickerSetList;
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(type));
        TLRPC.TL_messages_toggleStickerSets req = new TLRPC.TL_messages_toggleStickerSets();
        req.stickersets = inputStickerSets;
        switch (i) {
            case 0:
                req.uninstall = true;
                break;
            case 1:
                req.archive = true;
                break;
            case 2:
                req.unarchive = true;
                break;
        }
        getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda27(this, toggle, baseFragment, showSettings, type));
    }

    /* renamed from: lambda$toggleStickerSets$66$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m902xf0fd5cea(int toggle, BaseFragment baseFragment, boolean showSettings, int type, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda58(this, toggle, response, baseFragment, showSettings, type));
    }

    /* renamed from: lambda$toggleStickerSets$65$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m901x2e10var_b(int toggle, TLObject response, BaseFragment baseFragment, boolean showSettings, int type) {
        if (toggle != 0) {
            if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                processStickerSetInstallResultArchive(baseFragment, showSettings, type, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
            }
            loadStickers(type, false, false, true);
            return;
        }
        loadStickers(type, false, true);
    }

    public void processStickerSetInstallResultArchive(BaseFragment baseFragment, boolean showSettings, int type, TLRPC.TL_messages_stickerSetInstallResultArchive response) {
        int size = response.sets.size();
        for (int i = 0; i < size; i++) {
            this.installedStickerSetsById.remove(((TLRPC.StickerSetCovered) response.sets.get(i)).set.id);
        }
        loadArchivedStickersCount(type, false);
        getNotificationCenter().postNotificationName(NotificationCenter.needAddArchivedStickers, response.sets);
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), showSettings ? baseFragment : null, response.sets).create());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        if (r1[1] != false) goto L_0x001a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getMask() {
        /*
            r4 = this;
            r0 = 0
            int r1 = r4.lastReturnedNum
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r4.searchResultMessages
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
            if (r1 < r2) goto L_0x0018
            boolean[] r1 = r4.messagesSearchEndReached
            r2 = 0
            boolean r2 = r1[r2]
            if (r2 == 0) goto L_0x0018
            boolean r1 = r1[r3]
            if (r1 != 0) goto L_0x001a
        L_0x0018:
            r0 = r0 | 1
        L_0x001a:
            int r1 = r4.lastReturnedNum
            if (r1 <= 0) goto L_0x0020
            r0 = r0 | 2
        L_0x0020:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getMask():int");
    }

    public ArrayList<MessageObject> getFoundMessageObjects() {
        return this.searchResultMessages;
    }

    public void clearFoundMessageObjects() {
        this.searchResultMessages.clear();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isMessageFound(int r2, boolean r3) {
        /*
            r1 = this;
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r0 = r1.searchResultMessagesMap
            r0 = r0[r3]
            int r0 = r0.indexOfKey(r2)
            if (r0 < 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.isMessageFound(int, boolean):boolean");
    }

    public void searchMessagesInChat(String query, long dialogId, long mergeDialogId, int guid, int direction, int replyMessageId, TLRPC.User user, TLRPC.Chat chat) {
        searchMessagesInChat(query, dialogId, mergeDialogId, guid, direction, replyMessageId, false, user, chat, true);
    }

    public void jumpToSearchedMessage(int guid, int index) {
        if (index >= 0 && index < this.searchResultMessages.size()) {
            this.lastReturnedNum = index;
            MessageObject messageObject = this.searchResultMessages.get(index);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i = NotificationCenter.chatSearchResultsAvailable;
            int[] iArr = this.messagesSearchCount;
            notificationCenter.postNotificationName(i, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), true);
        }
    }

    public void loadMoreSearchMessages() {
        if (!this.loadingMoreSearchMessages) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (!zArr[0] || this.lastMergeDialogId != 0 || !zArr[1]) {
                int temp = this.searchResultMessages.size();
                this.lastReturnedNum = this.searchResultMessages.size();
                searchMessagesInChat((String) null, this.lastDialogId, this.lastMergeDialogId, this.lastGuid, 1, this.lastReplyMessageId, false, this.lastSearchUser, this.lastSearchChat, false);
                this.lastReturnedNum = temp;
                this.loadingMoreSearchMessages = true;
            }
        }
    }

    private void searchMessagesInChat(String query, long dialogId, long mergeDialogId, int guid, int direction, int replyMessageId, boolean internal, TLRPC.User user, TLRPC.Chat chat, boolean jumpToMessage) {
        boolean firstQuery;
        int max_id;
        String query2;
        char c;
        long queryWithDialog;
        int max_id2;
        String query3;
        long queryWithDialog2;
        int i;
        long j = dialogId;
        long j2 = mergeDialogId;
        int i2 = direction;
        TLRPC.User user2 = user;
        TLRPC.Chat chat2 = chat;
        long queryWithDialog3 = dialogId;
        boolean firstQuery2 = !internal;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (query != null) {
            if (firstQuery2) {
                boolean[] zArr = this.messagesSearchEndReached;
                c = 0;
                zArr[1] = false;
                zArr[0] = false;
                int[] iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
            } else {
                c = 0;
            }
            query2 = query;
            firstQuery = firstQuery2;
            max_id = 0;
        } else if (!this.searchResultMessages.isEmpty()) {
            if (i2 == 1) {
                int i3 = this.lastReturnedNum + 1;
                this.lastReturnedNum = i3;
                if (i3 < this.searchResultMessages.size()) {
                    MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i4 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr2 = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i4, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]), Boolean.valueOf(jumpToMessage));
                    return;
                }
                boolean[] zArr2 = this.messagesSearchEndReached;
                if (!zArr2[0] || j2 != 0 || !zArr2[1]) {
                    String query4 = this.lastSearchQuery;
                    ArrayList<MessageObject> arrayList = this.searchResultMessages;
                    MessageObject messageObject2 = arrayList.get(arrayList.size() - 1);
                    if (messageObject2.getDialogId() != j || this.messagesSearchEndReached[0]) {
                        if (messageObject2.getDialogId() == j2) {
                            max_id = messageObject2.getId();
                        } else {
                            max_id = 0;
                        }
                        queryWithDialog3 = mergeDialogId;
                        this.messagesSearchEndReached[1] = false;
                    } else {
                        max_id = messageObject2.getId();
                        queryWithDialog3 = dialogId;
                    }
                    query2 = query4;
                    firstQuery = false;
                    c = 0;
                } else {
                    this.lastReturnedNum--;
                    return;
                }
            } else if (i2 == 2) {
                int i5 = this.lastReturnedNum - 1;
                this.lastReturnedNum = i5;
                if (i5 < 0) {
                    this.lastReturnedNum = 0;
                    return;
                }
                if (i5 >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject3 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i6 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr3 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i6, Integer.valueOf(guid), Integer.valueOf(messageObject3.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject3.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr3[0] + iArr3[1]), Boolean.valueOf(jumpToMessage));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!zArr3[c] || zArr3[1] || j2 == 0) {
            queryWithDialog = queryWithDialog3;
        } else {
            queryWithDialog = mergeDialogId;
        }
        if (queryWithDialog != j || !firstQuery) {
            queryWithDialog2 = queryWithDialog;
            query3 = query2;
            max_id2 = max_id;
            i = 2;
        } else if (j2 != 0) {
            TLRPC.InputPeer inputPeer = getMessagesController().getInputPeer(j2);
            if (inputPeer != null) {
                TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
                req.peer = inputPeer;
                this.lastMergeDialogId = j2;
                req.limit = 1;
                req.q = query2;
                if (user2 != null) {
                    req.from_id = MessagesController.getInputPeer(user);
                    req.flags = 1 | req.flags;
                } else if (chat2 != null) {
                    req.from_id = MessagesController.getInputPeer(chat);
                    req.flags = 1 | req.flags;
                }
                req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
                MediaDataController$$ExternalSyntheticLambda37 mediaDataController$$ExternalSyntheticLambda37 = r0;
                TLRPC.TL_messages_search req2 = req;
                TLRPC.InputPeer inputPeer2 = inputPeer;
                String str = query2;
                int i7 = max_id;
                long j3 = queryWithDialog;
                ConnectionsManager connectionsManager = getConnectionsManager();
                MediaDataController$$ExternalSyntheticLambda37 mediaDataController$$ExternalSyntheticLambda372 = new MediaDataController$$ExternalSyntheticLambda37(this, mergeDialogId, req2, dialogId, guid, direction, replyMessageId, user, chat, jumpToMessage);
                this.mergeReqId = connectionsManager.sendRequest(req2, mediaDataController$$ExternalSyntheticLambda37, 2);
                return;
            }
            return;
        } else {
            queryWithDialog2 = queryWithDialog;
            query3 = query2;
            max_id2 = max_id;
            i = 2;
            this.lastMergeDialogId = 0;
            zArr3[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLRPC.TL_messages_search req3 = new TLRPC.TL_messages_search();
        long queryWithDialog4 = queryWithDialog2;
        req3.peer = getMessagesController().getInputPeer(queryWithDialog4);
        if (req3.peer != null) {
            this.lastGuid = guid;
            long queryWithDialog5 = queryWithDialog4;
            this.lastDialogId = dialogId;
            TLRPC.User user3 = user;
            this.lastSearchUser = user3;
            TLRPC.Chat chat3 = chat;
            this.lastSearchChat = chat3;
            this.lastReplyMessageId = replyMessageId;
            req3.limit = 21;
            String query5 = query3;
            req3.q = query5 != null ? query5 : "";
            int max_id3 = max_id2;
            req3.offset_id = max_id3;
            if (user3 != null) {
                req3.from_id = MessagesController.getInputPeer(user);
                req3.flags |= 1;
            } else if (chat3 != null) {
                req3.from_id = MessagesController.getInputPeer(chat);
                req3.flags |= 1;
            }
            int i8 = this.lastReplyMessageId;
            if (i8 != 0) {
                req3.top_msg_id = i8;
                req3.flags |= i;
            }
            req3.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.lastSearchQuery = query5;
            int i9 = max_id3;
            String str2 = query5;
            MediaDataController$$ExternalSyntheticLambda41 mediaDataController$$ExternalSyntheticLambda41 = r0;
            MediaDataController$$ExternalSyntheticLambda41 mediaDataController$$ExternalSyntheticLambda412 = new MediaDataController$$ExternalSyntheticLambda41(this, query5, currentReqId, jumpToMessage, req3, queryWithDialog5, dialogId, guid, mergeDialogId, replyMessageId, user, chat);
            this.reqId = getConnectionsManager().sendRequest(req3, mediaDataController$$ExternalSyntheticLambda41, 2);
        }
    }

    /* renamed from: lambda$searchMessagesInChat$68$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m892x8a4ecd59(long mergeDialogId, TLRPC.TL_messages_search req, long dialogId, int guid, int direction, int replyMessageId, TLRPC.User user, TLRPC.Chat chat, boolean jumpToMessage, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda80(this, mergeDialogId, response, req, dialogId, guid, direction, replyMessageId, user, chat, jumpToMessage));
    }

    /* renamed from: lambda$searchMessagesInChat$67$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m891xCLASSNAMEfa(long mergeDialogId, TLObject response, TLRPC.TL_messages_search req, long dialogId, int guid, int direction, int replyMessageId, TLRPC.User user, TLRPC.Chat chat, boolean jumpToMessage) {
        TLRPC.TL_messages_search tL_messages_search = req;
        if (this.lastMergeDialogId == mergeDialogId) {
            this.mergeReqId = 0;
            if (response != null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                this.messagesSearchEndReached[1] = res.messages.isEmpty();
                this.messagesSearchCount[1] = res instanceof TLRPC.TL_messages_messagesSlice ? res.count : res.messages.size();
                searchMessagesInChat(tL_messages_search.q, dialogId, mergeDialogId, guid, direction, replyMessageId, true, user, chat, jumpToMessage);
                return;
            }
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
            searchMessagesInChat(tL_messages_search.q, dialogId, mergeDialogId, guid, direction, replyMessageId, true, user, chat, jumpToMessage);
        }
    }

    /* renamed from: lambda$searchMessagesInChat$70$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m894xd8CLASSNAMEe2(String finalQuery, int currentReqId, boolean jumpToMessage, TLRPC.TL_messages_search req, long queryWithDialogFinal, long dialogId, int guid, long mergeDialogId, int replyMessageId, TLRPC.User user, TLRPC.Chat chat, TLObject response, TLRPC.TL_error error) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            int N = Math.min(res.messages.size(), 20);
            for (int a = 0; a < N; a++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, res.messages.get(a), false, false);
                messageObject.setQuery(finalQuery);
                messageObjects.add(messageObject);
            }
            String str = finalQuery;
        } else {
            String str2 = finalQuery;
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda62(this, currentReqId, jumpToMessage, response, req, queryWithDialogFinal, dialogId, guid, messageObjects, mergeDialogId, replyMessageId, user, chat));
    }

    /* renamed from: lambda$searchMessagesInChat$69$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m893x4d3b36b8(int currentReqId, boolean jumpToMessage, TLObject response, TLRPC.TL_messages_search req, long queryWithDialogFinal, long dialogId, int guid, ArrayList messageObjects, long mergeDialogId, int replyMessageId, TLRPC.User user, TLRPC.Chat chat) {
        if (currentReqId == this.lastReqId) {
            this.reqId = 0;
            if (!jumpToMessage) {
                this.loadingMoreSearchMessages = false;
            }
            if (response != null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                int a = 0;
                while (a < res.messages.size()) {
                    TLRPC.Message message = res.messages.get(a);
                    if ((message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                        res.messages.remove(a);
                        a--;
                    }
                    a++;
                }
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                getMessagesController().putUsers(res.users, false);
                getMessagesController().putChats(res.chats, false);
                if (req.offset_id == 0 && queryWithDialogFinal == dialogId) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(guid));
                }
                int N = Math.min(res.messages.size(), 20);
                boolean added = false;
                for (int a2 = 0; a2 < N; a2++) {
                    TLRPC.Message message2 = res.messages.get(a2);
                    added = true;
                    MessageObject messageObject = (MessageObject) messageObjects.get(a2);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[queryWithDialogFinal == dialogId ? (char) 0 : 1].put(messageObject.getId(), messageObject);
                }
                ArrayList arrayList = messageObjects;
                this.messagesSearchEndReached[queryWithDialogFinal == dialogId ? (char) 0 : 1] = res.messages.size() < 21;
                this.messagesSearchCount[queryWithDialogFinal == dialogId ? (char) 0 : 1] = ((res instanceof TLRPC.TL_messages_messagesSlice) || (res instanceof TLRPC.TL_messages_channelMessages)) ? res.count : res.messages.size();
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), 0, Integer.valueOf(getMask()), 0L, 0, 0, Boolean.valueOf(jumpToMessage));
                } else if (added) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i, Integer.valueOf(guid), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(jumpToMessage));
                }
                if (queryWithDialogFinal == dialogId) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && mergeDialogId != 0 && !zArr[1]) {
                        int i2 = N;
                        searchMessagesInChat(this.lastSearchQuery, dialogId, mergeDialogId, guid, 0, replyMessageId, true, user, chat, jumpToMessage);
                        return;
                    }
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x002a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMedia(long r22, int r24, int r25, int r26, int r27, int r28, int r29, int r30) {
        /*
            r21 = this;
            r14 = r22
            r13 = r24
            r11 = r25
            r12 = r26
            r10 = r27
            r8 = r28
            r7 = r29
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r22)
            r1 = 1
            if (r0 == 0) goto L_0x0022
            long r2 = -r14
            r6 = r21
            int r0 = r6.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r2, r0)
            if (r0 == 0) goto L_0x0024
            r9 = 1
            goto L_0x0026
        L_0x0022:
            r6 = r21
        L_0x0024:
            r0 = 0
            r9 = 0
        L_0x0026:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0066
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "load media did "
            r0.append(r2)
            r0.append(r14)
            java.lang.String r2 = " count = "
            r0.append(r2)
            r0.append(r13)
            java.lang.String r2 = " max_id "
            r0.append(r2)
            r0.append(r11)
            java.lang.String r2 = " type = "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r2 = " cache = "
            r0.append(r2)
            r0.append(r8)
            java.lang.String r2 = " classGuid = "
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0066:
            if (r8 != 0) goto L_0x0115
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r22)
            if (r0 == 0) goto L_0x0071
            r15 = r7
            goto L_0x0116
        L_0x0071:
            org.telegram.tgnet.TLRPC$TL_messages_search r0 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r0.<init>()
            r5 = r0
            r5.limit = r13
            if (r12 == 0) goto L_0x0081
            r5.offset_id = r12
            int r0 = -r13
            r5.add_offset = r0
            goto L_0x0083
        L_0x0081:
            r5.offset_id = r11
        L_0x0083:
            if (r10 != 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x008d:
            r0 = 6
            if (r10 != r0) goto L_0x0098
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x0098:
            r0 = 7
            if (r10 != r0) goto L_0x00a3
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x00a3:
            if (r10 != r1) goto L_0x00ad
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x00ad:
            r0 = 2
            if (r10 != r0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x00b8:
            r0 = 3
            if (r10 != r0) goto L_0x00c3
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x00c3:
            r0 = 4
            if (r10 != r0) goto L_0x00ce
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic
            r0.<init>()
            r5.filter = r0
            goto L_0x00d8
        L_0x00ce:
            r0 = 5
            if (r10 != r0) goto L_0x00d8
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif
            r0.<init>()
            r5.filter = r0
        L_0x00d8:
            java.lang.String r0 = ""
            r5.q = r0
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer((long) r14)
            r5.peer = r0
            org.telegram.tgnet.TLRPC$InputPeer r0 = r5.peer
            if (r0 != 0) goto L_0x00eb
            return
        L_0x00eb:
            org.telegram.tgnet.ConnectionsManager r4 = r21.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda31 r2 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda31
            r0 = r2
            r1 = r21
            r11 = r2
            r2 = r22
            r12 = r4
            r4 = r26
            r13 = r5
            r5 = r24
            r6 = r25
            r15 = r7
            r7 = r27
            r8 = r29
            r10 = r30
            r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10)
            int r0 = r12.sendRequest(r13, r11)
            org.telegram.tgnet.ConnectionsManager r1 = r21.getConnectionsManager()
            r1.bindRequestToGuid(r0, r15)
            goto L_0x012d
        L_0x0115:
            r15 = r7
        L_0x0116:
            r10 = r21
            r11 = r22
            r13 = r24
            r14 = r25
            r15 = r26
            r16 = r27
            r17 = r29
            r18 = r9
            r19 = r28
            r20 = r30
            r10.loadMediaDatabase(r11, r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x012d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadMedia(long, int, int, int, int, int, int, int):void");
    }

    /* renamed from: lambda$loadMedia$71$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m824lambda$loadMedia$71$orgtelegrammessengerMediaDataController(long dialogId, int min_id, int count, int max_id, int type, int classGuid, boolean isChannel, int requestIndex, TLObject response, TLRPC.TL_error error) {
        boolean topReached;
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            getMessagesController().removeDeletedMessagesFromArray(dialogId, res.messages);
            boolean z = false;
            if (min_id != 0) {
                if (res.messages.size() <= 1) {
                    z = true;
                }
                topReached = z;
            } else {
                if (res.messages.size() == 0) {
                    z = true;
                }
                topReached = z;
            }
            processLoadedMedia(res, dialogId, count, max_id, min_id, type, 0, classGuid, isChannel, topReached, requestIndex);
            return;
        }
        long j = dialogId;
    }

    public void getMediaCounts(long dialogId, int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda69(this, dialogId, classGuid));
    }

    /* renamed from: lambda$getMediaCounts$76$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m798x4e78a3dd(long dialogId, int classGuid) {
        long j = dialogId;
        try {
            int i = 0;
            int[] counts = {-1, -1, -1, -1, -1, -1, -1, -1};
            int[] countsFinal = {-1, -1, -1, -1, -1, -1, -1, -1};
            int[] old = {0, 0, 0, 0, 0, 0, 0, 0};
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
            while (cursor.next()) {
                int type = cursor.intValue(0);
                if (type >= 0 && type < 8) {
                    int intValue = cursor.intValue(1);
                    counts[type] = intValue;
                    countsFinal[type] = intValue;
                    old[type] = cursor.intValue(2);
                }
            }
            cursor.dispose();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                for (int a = 0; a < counts.length; a++) {
                    if (counts[a] == -1) {
                        SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(dialogId), Integer.valueOf(a)}), new Object[0]);
                        if (cursor2.next()) {
                            counts[a] = cursor2.intValue(0);
                        } else {
                            counts[a] = 0;
                        }
                        cursor2.dispose();
                        putMediaCountDatabase(j, a, counts[a]);
                        SQLiteCursor sQLiteCursor = cursor2;
                    }
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda84(this, j, counts));
                int i2 = classGuid;
                return;
            }
            boolean missing = false;
            TLRPC.TL_messages_getSearchCounters req = new TLRPC.TL_messages_getSearchCounters();
            req.peer = getMessagesController().getInputPeer(j);
            int a2 = 0;
            while (a2 < counts.length) {
                if (req.peer == null) {
                    counts[a2] = i;
                } else if (counts[a2] == -1 || old[a2] == 1) {
                    if (a2 == 0) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterPhotoVideo());
                    } else if (a2 == 1) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterDocument());
                    } else if (a2 == 2) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterRoundVoice());
                    } else if (a2 == 3) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterUrl());
                    } else if (a2 == 4) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterMusic());
                    } else if (a2 == 6) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterPhotos());
                    } else if (a2 == 7) {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterVideo());
                    } else {
                        req.filters.add(new TLRPC.TL_inputMessagesFilterGif());
                    }
                    if (counts[a2] == -1) {
                        missing = true;
                    } else if (old[a2] == 1) {
                        counts[a2] = -1;
                    }
                }
                a2++;
                i = 0;
            }
            if (!req.filters.isEmpty()) {
                try {
                    getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda49(this, counts, j)), classGuid);
                } catch (Exception e) {
                    e = e;
                    FileLog.e((Throwable) e);
                }
            } else {
                int i3 = classGuid;
            }
            if (!missing) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda86(this, j, countsFinal));
            }
        } catch (Exception e2) {
            e = e2;
            int i4 = classGuid;
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$getMediaCounts$72$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m794x42c6fe61(long dialogId, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), counts);
    }

    /* renamed from: lambda$getMediaCounts$74$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m796xCLASSNAMEfd11f(int[] counts, long dialogId, TLObject response, TLRPC.TL_error error) {
        int type;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] < 0) {
                counts[i] = 0;
            }
        }
        if (response != null) {
            TLRPC.Vector res = (TLRPC.Vector) response;
            int N = res.objects.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_messages_searchCounter searchCounter = (TLRPC.TL_messages_searchCounter) res.objects.get(a);
                if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterPhotoVideo) {
                    type = 0;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterDocument) {
                    type = 1;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterRoundVoice) {
                    type = 2;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterUrl) {
                    type = 3;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterMusic) {
                    type = 4;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterGif) {
                    type = 5;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterPhotos) {
                    type = 6;
                } else if (searchCounter.filter instanceof TLRPC.TL_inputMessagesFilterVideo) {
                    type = 7;
                }
                counts[type] = searchCounter.count;
                putMediaCountDatabase(dialogId, type, counts[type]);
            }
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda85(this, dialogId, counts));
    }

    /* renamed from: lambda$getMediaCounts$73$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m795x5b367c0(long dialogId, int[] counts) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), counts);
    }

    /* renamed from: lambda$getMediaCounts$75$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m797x8b8c3a7e(long dialogId, int[] countsFinal) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(dialogId), countsFinal);
    }

    public void getMediaCount(long dialogId, int type, int classGuid, boolean fromCache) {
        if (fromCache || DialogObject.isEncryptedDialog(dialogId)) {
            getMediaCountDatabase(dialogId, type, classGuid);
            return;
        }
        TLRPC.TL_messages_getSearchCounters req = new TLRPC.TL_messages_getSearchCounters();
        if (type == 0) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterPhotoVideo());
        } else if (type == 1) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterDocument());
        } else if (type == 2) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterRoundVoice());
        } else if (type == 3) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterUrl());
        } else if (type == 4) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterMusic());
        } else if (type == 5) {
            req.filters.add(new TLRPC.TL_inputMessagesFilterGif());
        }
        req.peer = getMessagesController().getInputPeer(dialogId);
        if (req.peer != null) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda30(this, dialogId, type, classGuid)), classGuid);
        }
    }

    /* renamed from: lambda$getMediaCount$77$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m792x23406e51(long dialogId, int type, int classGuid, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector res = (TLRPC.Vector) response;
            if (!res.objects.isEmpty()) {
                processLoadedMediaCount(((TLRPC.TL_messages_searchCounter) res.objects.get(0)).count, dialogId, type, classGuid, false, 0);
            }
        }
    }

    public static int getMediaType(TLRPC.Message message) {
        if (message == null) {
            return -1;
        }
        if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.Document document = message.media.document;
            if (document == null) {
                return -1;
            }
            boolean isAnimated = false;
            boolean isVideo = false;
            boolean isVoice = false;
            boolean isMusic = false;
            boolean isSticker = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                    isVoice = attribute.round_message;
                    isVideo = true ^ attribute.round_message;
                } else if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                    isAnimated = true;
                } else if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    isVoice = attribute.voice;
                    isMusic = true ^ attribute.voice;
                } else if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                    isSticker = true;
                }
            }
            if (isVoice) {
                return 2;
            }
            if (isVideo && !isAnimated) {
                return 0;
            }
            if (isSticker) {
                return -1;
            }
            if (isAnimated) {
                return 5;
            }
            if (isMusic) {
                return 4;
            }
            return 1;
        }
        if (!message.entities.isEmpty()) {
            for (int a2 = 0; a2 < message.entities.size(); a2++) {
                TLRPC.MessageEntity entity = message.entities.get(a2);
                if ((entity instanceof TLRPC.TL_messageEntityUrl) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntityEmail)) {
                    return 3;
                }
            }
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(TLRPC.Message message) {
        if ((message instanceof TLRPC.TL_message_secret) && (((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && message.media.ttl_seconds != 0 && message.media.ttl_seconds <= 60)) {
            return false;
        }
        if (((message instanceof TLRPC.TL_message_secret) || !(message instanceof TLRPC.TL_message) || ((!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !(message.media instanceof TLRPC.TL_messageMediaDocument)) || message.media.ttl_seconds == 0)) && getMediaType(message) != -1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void processLoadedMedia(TLRPC.messages_Messages res, long dialogId, int count, int max_id, int min_id, int type, int fromCache, int classGuid, boolean isChannel, boolean topReached, int requestIndex) {
        TLRPC.messages_Messages messages_messages = res;
        int i = min_id;
        int i2 = fromCache;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process load media did " + dialogId + " count = " + count + " max_id=" + max_id + " min_id=" + i + " type = " + type + " cache = " + i2 + " classGuid = " + classGuid);
        } else {
            long j = dialogId;
            int i3 = count;
            int i4 = max_id;
            int i5 = type;
            int i6 = classGuid;
        }
        if (i2 == 0 || (((!messages_messages.messages.isEmpty() || i != 0) && (messages_messages.messages.size() > 1 || i == 0)) || DialogObject.isEncryptedDialog(dialogId))) {
            if (i2 == 0) {
                ImageLoader.saveMessagesThumbs(messages_messages.messages);
                getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                putMediaDatabase(dialogId, type, messages_messages.messages, max_id, min_id, topReached);
            }
            MediaDataController$$ExternalSyntheticLambda143 mediaDataController$$ExternalSyntheticLambda143 = r0;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            MediaDataController$$ExternalSyntheticLambda143 mediaDataController$$ExternalSyntheticLambda1432 = new MediaDataController$$ExternalSyntheticLambda143(this, res, fromCache, dialogId, classGuid, type, topReached, min_id, requestIndex);
            dispatchQueue.postRunnable(mediaDataController$$ExternalSyntheticLambda143);
        } else if (i2 != 2) {
            loadMedia(dialogId, count, max_id, min_id, type, 0, classGuid, requestIndex);
        }
    }

    /* renamed from: lambda$processLoadedMedia$79$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m862x567db4d8(TLRPC.messages_Messages res, int fromCache, long dialogId, int classGuid, int type, boolean topReached, int min_id, int requestIndex) {
        TLRPC.messages_Messages messages_messages = res;
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < messages_messages.users.size(); a++) {
            TLRPC.User u = messages_messages.users.get(a);
            usersDict.put(u.id, u);
        }
        ArrayList<MessageObject> objects = new ArrayList<>();
        for (int a2 = 0; a2 < messages_messages.messages.size(); a2++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, messages_messages.messages.get(a2), usersDict, true, true);
            messageObject.createStrippedThumb();
            objects.add(messageObject);
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda144(this, res, fromCache, dialogId, objects, classGuid, type, topReached, min_id, requestIndex));
    }

    /* renamed from: lambda$processLoadedMedia$78$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m861x93914b79(TLRPC.messages_Messages res, int fromCache, long dialogId, ArrayList objects, int classGuid, int type, boolean topReached, int min_id, int requestIndex) {
        TLRPC.messages_Messages messages_messages = res;
        int totalCount = messages_messages.count;
        boolean z = true;
        getMessagesController().putUsers(messages_messages.users, fromCache != 0);
        getMessagesController().putChats(messages_messages.chats, fromCache != 0);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.mediaDidLoad;
        Object[] objArr = new Object[8];
        objArr[0] = Long.valueOf(dialogId);
        objArr[1] = Integer.valueOf(totalCount);
        objArr[2] = objects;
        objArr[3] = Integer.valueOf(classGuid);
        objArr[4] = Integer.valueOf(type);
        objArr[5] = Boolean.valueOf(topReached);
        if (min_id == 0) {
            z = false;
        }
        objArr[6] = Boolean.valueOf(z);
        objArr[7] = Integer.valueOf(requestIndex);
        notificationCenter.postNotificationName(i, objArr);
    }

    private void processLoadedMediaCount(int count, long dialogId, int type, int classGuid, boolean fromCache, int old) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda82(this, dialogId, fromCache, count, type, old, classGuid));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0020, code lost:
        if (r8 == false) goto L_0x0025;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006d  */
    /* renamed from: lambda$processLoadedMediaCount$80$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m863xa0eae977(long r17, boolean r19, int r20, int r21, int r22, int r23) {
        /*
            r16 = this;
            r0 = r20
            r7 = r21
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)
            r9 = 2
            r10 = -1
            r11 = 1
            r12 = 0
            if (r19 == 0) goto L_0x0018
            if (r0 == r10) goto L_0x0014
            if (r0 != 0) goto L_0x0018
            if (r7 != r9) goto L_0x0018
        L_0x0014:
            if (r8 != 0) goto L_0x0018
            r1 = 1
            goto L_0x0019
        L_0x0018:
            r1 = 0
        L_0x0019:
            r13 = r1
            if (r13 != 0) goto L_0x0023
            r14 = r22
            if (r14 != r11) goto L_0x0031
            if (r8 != 0) goto L_0x0031
            goto L_0x0025
        L_0x0023:
            r14 = r22
        L_0x0025:
            r6 = 0
            r1 = r16
            r2 = r17
            r4 = r21
            r5 = r23
            r1.getMediaCount(r2, r4, r5, r6)
        L_0x0031:
            if (r13 != 0) goto L_0x006d
            if (r19 != 0) goto L_0x003d
            r1 = r16
            r2 = r17
            r1.putMediaCountDatabase(r2, r7, r0)
            goto L_0x0041
        L_0x003d:
            r1 = r16
            r2 = r17
        L_0x0041:
            org.telegram.messenger.NotificationCenter r4 = r16.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Long r15 = java.lang.Long.valueOf(r17)
            r6[r12] = r15
            if (r19 == 0) goto L_0x0055
            if (r0 != r10) goto L_0x0055
            goto L_0x0056
        L_0x0055:
            r12 = r0
        L_0x0056:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r12)
            r6[r11] = r10
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r19)
            r6[r9] = r10
            r9 = 3
            java.lang.Integer r10 = java.lang.Integer.valueOf(r21)
            r6[r9] = r10
            r4.postNotificationName(r5, r6)
            goto L_0x0071
        L_0x006d:
            r1 = r16
            r2 = r17
        L_0x0071:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m863xa0eae977(long, boolean, int, int, int, int):void");
    }

    private void putMediaCountDatabase(long uid, int type, int count) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda73(this, uid, type, count));
    }

    /* renamed from: lambda$putMediaCountDatabase$81$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m877x557dceac(long uid, int type, int count) {
        try {
            SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            state2.requery();
            state2.bindLong(1, uid);
            state2.bindInteger(2, type);
            state2.bindInteger(3, count);
            state2.bindInteger(4, 0);
            state2.step();
            state2.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void getMediaCountDatabase(long dialogId, int type, int classGuid) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda71(this, dialogId, type, classGuid));
    }

    /* renamed from: lambda$getMediaCountDatabase$82$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m793xbba45CLASSNAME(long dialogId, int type, int classGuid) {
        int count = -1;
        int old = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(dialogId), Integer.valueOf(type)}), new Object[0]);
            if (cursor.next()) {
                count = cursor.intValue(0);
                old = cursor.intValue(1);
            }
            cursor.dispose();
            if (count != -1 || !DialogObject.isEncryptedDialog(dialogId)) {
                long j = dialogId;
                int i = type;
            } else {
                SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(dialogId), Integer.valueOf(type)}), new Object[0]);
                if (cursor2.next()) {
                    count = cursor2.intValue(0);
                }
                cursor2.dispose();
                if (count != -1) {
                    try {
                        putMediaCountDatabase(dialogId, type, count);
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                } else {
                    long j2 = dialogId;
                    int i2 = type;
                }
            }
            processLoadedMediaCount(count, dialogId, type, classGuid, true, old);
        } catch (Exception e2) {
            e = e2;
            long j3 = dialogId;
            int i3 = type;
            FileLog.e((Throwable) e);
        }
    }

    private void loadMediaDatabase(long uid, int count, int max_id, int min_id, int type, int classGuid, boolean isChannel, int fromCache, int requestIndex) {
        final int i = count;
        final long j = uid;
        final int i2 = min_id;
        final int i3 = type;
        final int i4 = max_id;
        final int i5 = classGuid;
        final int i6 = fromCache;
        final boolean z = isChannel;
        final int i7 = requestIndex;
        Runnable runnable = new Runnable() {
            public void run() {
                TLRPC.TL_messages_messages res;
                Throwable th;
                boolean isEnd;
                SQLiteCursor cursor;
                SQLiteDatabase database;
                int holeMessageId;
                int holeMessageId2;
                SQLiteCursor cursor2;
                int holeMessageId3;
                int startHole;
                int mid;
                boolean topReached = false;
                TLRPC.TL_messages_messages res2 = new TLRPC.TL_messages_messages();
                try {
                    ArrayList<Long> usersToLoad = new ArrayList<>();
                    ArrayList<Long> chatsToLoad = new ArrayList<>();
                    int countToLoad = i + 1;
                    SQLiteDatabase database2 = MediaDataController.this.getMessagesStorage().getDatabase();
                    boolean isEnd2 = false;
                    boolean reverseMessages = false;
                    if (!DialogObject.isEncryptedDialog(j)) {
                        if (i2 == 0) {
                            database = database2;
                            SQLiteCursor cursor3 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                            if (cursor3.next()) {
                                isEnd2 = cursor3.intValue(0) == 1;
                            } else {
                                cursor3.dispose();
                                cursor3 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v4 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                                if (cursor3.next() && (mid = cursor3.intValue(0)) != 0) {
                                    SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                    state.requery();
                                    state.bindLong(1, j);
                                    state.bindInteger(2, i3);
                                    state.bindInteger(3, 0);
                                    state.bindInteger(4, mid);
                                    state.step();
                                    state.dispose();
                                }
                            }
                            cursor3.dispose();
                        } else {
                            database = database2;
                        }
                        if (i4 != 0) {
                            SQLiteCursor cursor4 = database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND start <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i4)}), new Object[0]);
                            if (cursor4.next()) {
                                startHole = cursor4.intValue(0);
                                holeMessageId3 = cursor4.intValue(1);
                            } else {
                                holeMessageId3 = 0;
                                startHole = 0;
                            }
                            cursor4.dispose();
                            if (holeMessageId3 > 1) {
                                SQLiteCursor sQLiteCursor = cursor4;
                                int i = startHole;
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i4), Integer.valueOf(holeMessageId3), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                isEnd2 = false;
                            } else {
                                SQLiteCursor sQLiteCursor2 = cursor4;
                                int i2 = startHole;
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            }
                        } else if (i2 != 0) {
                            int startHole2 = 0;
                            boolean isEnd3 = isEnd2;
                            SQLiteCursor cursor5 = database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end >= %d ORDER BY end ASC LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2)}), new Object[0]);
                            if (cursor5.next()) {
                                startHole2 = cursor5.intValue(0);
                                holeMessageId2 = cursor5.intValue(1);
                            } else {
                                holeMessageId2 = 0;
                            }
                            cursor5.dispose();
                            reverseMessages = true;
                            if (startHole2 > 1) {
                                SQLiteCursor sQLiteCursor3 = cursor5;
                                int i3 = holeMessageId2;
                                cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND mid <= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(startHole2), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                isEnd = isEnd3;
                            } else {
                                SQLiteCursor sQLiteCursor4 = cursor5;
                                int i4 = holeMessageId2;
                                int i5 = startHole2;
                                cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                isEnd = true;
                            }
                        } else {
                            boolean isEnd4 = isEnd2;
                            SQLiteCursor cursor6 = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                            if (cursor6.next()) {
                                holeMessageId = cursor6.intValue(0);
                            } else {
                                holeMessageId = 0;
                            }
                            cursor6.dispose();
                            if (holeMessageId > 1) {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(holeMessageId), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                reverseMessages = false;
                                isEnd = isEnd4;
                            } else {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                reverseMessages = false;
                                isEnd = isEnd4;
                            }
                        }
                    } else {
                        SQLiteDatabase database3 = database2;
                        isEnd = true;
                        if (i4 != 0) {
                            cursor = database3.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            reverseMessages = false;
                        } else if (i2 != 0) {
                            cursor = database3.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d AND type = %d ORDER BY m.mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            reverseMessages = false;
                        } else {
                            cursor = database3.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            reverseMessages = false;
                        }
                    }
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, MediaDataController.this.getUserConfig().clientUserId);
                            data.reuse();
                            message.id = cursor.intValue(1);
                            message.dialog_id = j;
                            if (DialogObject.isEncryptedDialog(j)) {
                                message.random_id = cursor.longValue(2);
                            }
                            if (reverseMessages) {
                                res2.messages.add(0, message);
                            } else {
                                res2.messages.add(message);
                            }
                            MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                        }
                    }
                    cursor.dispose();
                    if (!usersToLoad.isEmpty()) {
                        MediaDataController.this.getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), res2.users);
                    }
                    if (!chatsToLoad.isEmpty()) {
                        MediaDataController.this.getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), res2.chats);
                    }
                    if (res2.messages.size() > i && i2 == 0) {
                        res2.messages.remove(res2.messages.size() - 1);
                    } else if (i2 != 0) {
                        topReached = false;
                    } else {
                        topReached = isEnd;
                    }
                    AndroidUtilities.runOnUIThread(new MediaDataController$1$$ExternalSyntheticLambda0(this, this, i5));
                    MediaDataController.this.processLoadedMedia(res2, j, i, i4, i2, i3, i6, i5, z, topReached, i7);
                    TLRPC.TL_messages_messages tL_messages_messages = res2;
                } catch (Exception e) {
                    Exception e2 = e;
                    res2.messages.clear();
                    res2.chats.clear();
                    res2.users.clear();
                    FileLog.e((Throwable) e2);
                    AndroidUtilities.runOnUIThread(new MediaDataController$1$$ExternalSyntheticLambda0(this, this, i5));
                    TLRPC.TL_messages_messages tL_messages_messages2 = res2;
                    MediaDataController.this.processLoadedMedia(res2, j, i, i4, i2, i3, i6, i5, z, false, i7);
                } catch (Throwable th2) {
                    res = res2;
                    th = th2;
                    AndroidUtilities.runOnUIThread(new MediaDataController$1$$ExternalSyntheticLambda0(this, this, i5));
                    MediaDataController.this.processLoadedMedia(res, j, i, i4, i2, i3, i6, i5, z, false, i7);
                    throw th;
                }
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-MediaDataController$1  reason: not valid java name */
            public /* synthetic */ void m907lambda$run$0$orgtelegrammessengerMediaDataController$1(Runnable task, int classGuid) {
                MediaDataController.this.getMessagesStorage().completeTaskForGuid(task, classGuid);
            }
        };
        MessagesStorage messagesStorage = getMessagesStorage();
        messagesStorage.getStorageQueue().postRunnable(runnable);
        messagesStorage.bindTaskToGuid(runnable, classGuid);
    }

    private void putMediaDatabase(long uid, int type, ArrayList<TLRPC.Message> messages, int max_id, int min_id, boolean topReached) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda57(this, min_id, messages, topReached, uid, max_id, type));
    }

    /* renamed from: lambda$putMediaDatabase$83$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m878xae7CLASSNAMEf(int min_id, ArrayList messages, boolean topReached, long uid, int max_id, int type) {
        ArrayList arrayList = messages;
        long j = uid;
        int i = max_id;
        int i2 = type;
        if (min_id == 0) {
            try {
                if (messages.isEmpty() || topReached) {
                    getMessagesStorage().doneHolesInMedia(j, i, i2);
                    if (messages.isEmpty()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return;
            }
        }
        getMessagesStorage().getDatabase().beginTransaction();
        SQLitePreparedStatement state2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
        Iterator it = messages.iterator();
        while (it.hasNext()) {
            TLRPC.Message message = (TLRPC.Message) it.next();
            if (canAddMessageToMedia(message)) {
                state2.requery();
                NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(data);
                state2.bindInteger(1, message.id);
                state2.bindLong(2, j);
                state2.bindInteger(3, message.date);
                state2.bindInteger(4, i2);
                state2.bindByteBuffer(5, data);
                state2.step();
                data.reuse();
            }
        }
        state2.dispose();
        if (!(topReached && i == 0 && min_id == 0)) {
            int minId = (!topReached || min_id != 0) ? ((TLRPC.Message) arrayList.get(messages.size() - 1)).id : 1;
            if (min_id != 0) {
                getMessagesStorage().closeHolesInMedia(uid, minId, ((TLRPC.Message) arrayList.get(0)).id, type);
            } else if (i != 0) {
                getMessagesStorage().closeHolesInMedia(uid, minId, max_id, type);
            } else {
                getMessagesStorage().closeHolesInMedia(uid, minId, Integer.MAX_VALUE, type);
            }
        }
        getMessagesStorage().getDatabase().commitTransaction();
    }

    public void loadMusic(long dialogId, long maxId, long minId) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda76(this, dialogId, maxId, minId));
    }

    /* renamed from: lambda$loadMusic$85$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m826lambda$loadMusic$85$orgtelegrammessengerMediaDataController(long dialogId, long maxId, long minId) {
        SQLiteCursor cursor;
        ArrayList<MessageObject> arrayListBegin = new ArrayList<>();
        ArrayList<MessageObject> arrayListEnd = new ArrayList<>();
        int a = 0;
        while (a < 2) {
            ArrayList<MessageObject> arrayList = a == 0 ? arrayListBegin : arrayListEnd;
            if (a == 0) {
                try {
                    if (!DialogObject.isEncryptedDialog(dialogId)) {
                        cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(dialogId), Long.valueOf(maxId), 4}), new Object[0]);
                    } else {
                        cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(dialogId), Long.valueOf(maxId), 4}), new Object[0]);
                    }
                } catch (Exception e) {
                    e = e;
                    long j = dialogId;
                    FileLog.e((Throwable) e);
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, dialogId, arrayListBegin, arrayListEnd));
                }
            } else if (!DialogObject.isEncryptedDialog(dialogId)) {
                cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(dialogId), Long.valueOf(minId), 4}), new Object[0]);
            } else {
                cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(dialogId), Long.valueOf(minId), 4}), new Object[0]);
            }
            while (cursor.next()) {
                NativeByteBuffer data = cursor.byteBufferValue(0);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    message.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    if (MessageObject.isMusicMessage(message)) {
                        message.id = cursor.intValue(1);
                        try {
                            message.dialog_id = dialogId;
                            try {
                                arrayList.add(0, new MessageObject(this.currentAccount, message, false, true));
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.e((Throwable) e);
                                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, dialogId, arrayListBegin, arrayListEnd));
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e((Throwable) e);
                            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, dialogId, arrayListBegin, arrayListEnd));
                        }
                    } else {
                        long j2 = dialogId;
                    }
                } else {
                    long j3 = dialogId;
                }
            }
            long j4 = dialogId;
            cursor.dispose();
            a++;
        }
        long j5 = dialogId;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, dialogId, arrayListBegin, arrayListEnd));
    }

    /* renamed from: lambda$loadMusic$84$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m825lambda$loadMusic$84$orgtelegrammessengerMediaDataController(long dialogId, ArrayList arrayListBegin, ArrayList arrayListEnd) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(dialogId), arrayListBegin, arrayListEnd);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT >= 23) {
            int maxShortcuts = ShortcutManagerCompat.getMaxShortcutCountPerActivity(ApplicationLoader.applicationContext) - 2;
            if (maxShortcuts <= 0) {
                maxShortcuts = 5;
            }
            ArrayList<TLRPC.TL_topPeer> hintsFinal = new ArrayList<>();
            if (SharedConfig.passcodeHash.length() <= 0) {
                for (int a = 0; a < this.hints.size(); a++) {
                    hintsFinal.add(this.hints.get(a));
                    if (hintsFinal.size() == maxShortcuts - 2) {
                        break;
                    }
                }
            }
            Utilities.globalQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda102(this, hintsFinal));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x02f7 A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0310 A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0315 A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x031d A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0336 A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x033c A[Catch:{ all -> 0x035a }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x02f2 A[Catch:{ all -> 0x035a }] */
    /* renamed from: lambda$buildShortcuts$86$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m780xefbCLASSNAMEab(java.util.ArrayList r30) {
        /*
            r29 = this;
            r1 = r30
            java.lang.String r0 = "NewConversationShortcut"
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x035a }
            if (r2 != 0) goto L_0x002a
            java.util.UUID r2 = java.util.UUID.randomUUID()     // Catch:{ all -> 0x035a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x035a }
            org.telegram.messenger.SharedConfig.directShareHash = r2     // Catch:{ all -> 0x035a }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            java.lang.String r3 = "mainconfig"
            r4 = 0
            android.content.SharedPreferences r2 = r2.getSharedPreferences(r3, r4)     // Catch:{ all -> 0x035a }
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ all -> 0x035a }
            java.lang.String r3 = "directShareHash2"
            java.lang.String r4 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x035a }
            android.content.SharedPreferences$Editor r2 = r2.putString(r3, r4)     // Catch:{ all -> 0x035a }
            r2.commit()     // Catch:{ all -> 0x035a }
        L_0x002a:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            java.util.List r2 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r2)     // Catch:{ all -> 0x035a }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x035a }
            r3.<init>()     // Catch:{ all -> 0x035a }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x035a }
            r4.<init>()     // Catch:{ all -> 0x035a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x035a }
            r5.<init>()     // Catch:{ all -> 0x035a }
            java.lang.String r6 = "did3_"
            java.lang.String r7 = "compose"
            if (r2 == 0) goto L_0x00a5
            boolean r8 = r2.isEmpty()     // Catch:{ all -> 0x035a }
            if (r8 != 0) goto L_0x00a5
            r4.add(r7)     // Catch:{ all -> 0x035a }
            r8 = 0
        L_0x004f:
            int r9 = r30.size()     // Catch:{ all -> 0x035a }
            if (r8 >= r9) goto L_0x0077
            java.lang.Object r9 = r1.get(r8)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$TL_topPeer r9 = (org.telegram.tgnet.TLRPC.TL_topPeer) r9     // Catch:{ all -> 0x035a }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x035a }
            r10.<init>()     // Catch:{ all -> 0x035a }
            r10.append(r6)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$Peer r11 = r9.peer     // Catch:{ all -> 0x035a }
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r11)     // Catch:{ all -> 0x035a }
            r10.append(r11)     // Catch:{ all -> 0x035a }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x035a }
            r4.add(r10)     // Catch:{ all -> 0x035a }
            int r8 = r8 + 1
            goto L_0x004f
        L_0x0077:
            r8 = 0
        L_0x0078:
            int r9 = r2.size()     // Catch:{ all -> 0x035a }
            if (r8 >= r9) goto L_0x0098
            java.lang.Object r9 = r2.get(r8)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat r9 = (androidx.core.content.pm.ShortcutInfoCompat) r9     // Catch:{ all -> 0x035a }
            java.lang.String r9 = r9.getId()     // Catch:{ all -> 0x035a }
            boolean r10 = r4.remove(r9)     // Catch:{ all -> 0x035a }
            if (r10 != 0) goto L_0x0091
            r5.add(r9)     // Catch:{ all -> 0x035a }
        L_0x0091:
            r3.add(r9)     // Catch:{ all -> 0x035a }
            int r8 = r8 + 1
            goto L_0x0078
        L_0x0098:
            boolean r8 = r4.isEmpty()     // Catch:{ all -> 0x035a }
            if (r8 == 0) goto L_0x00a5
            boolean r8 = r5.isEmpty()     // Catch:{ all -> 0x035a }
            if (r8 == 0) goto L_0x00a5
            return
        L_0x00a5:
            android.content.Intent r8 = new android.content.Intent     // Catch:{ all -> 0x035a }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            java.lang.Class<org.telegram.ui.LaunchActivity> r10 = org.telegram.ui.LaunchActivity.class
            r8.<init>(r9, r10)     // Catch:{ all -> 0x035a }
            java.lang.String r9 = "new_dialog"
            r8.setAction(r9)     // Catch:{ all -> 0x035a }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ all -> 0x035a }
            r9.<init>()     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r10 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x035a }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            r10.<init>((android.content.Context) r11, (java.lang.String) r7)     // Catch:{ all -> 0x035a }
            r11 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r11)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r10 = r10.setShortLabel(r12)     // Catch:{ all -> 0x035a }
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r11)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r10.setLongLabel(r0)     // Catch:{ all -> 0x035a }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            r11 = 2131166074(0x7var_a, float:1.7946383E38)
            androidx.core.graphics.drawable.IconCompat r10 = androidx.core.graphics.drawable.IconCompat.createWithResource(r10, r11)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIcon(r10)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r8)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat r0 = r0.build()     // Catch:{ all -> 0x035a }
            r9.add(r0)     // Catch:{ all -> 0x035a }
            boolean r0 = r3.contains(r7)     // Catch:{ all -> 0x035a }
            if (r0 == 0) goto L_0x00f7
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r0, r9)     // Catch:{ all -> 0x035a }
            goto L_0x00fc
        L_0x00f7:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r0, r9)     // Catch:{ all -> 0x035a }
        L_0x00fc:
            r9.clear()     // Catch:{ all -> 0x035a }
            boolean r0 = r5.isEmpty()     // Catch:{ all -> 0x035a }
            if (r0 != 0) goto L_0x010a
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r0, r5)     // Catch:{ all -> 0x035a }
        L_0x010a:
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ all -> 0x035a }
            r7 = 1
            r0.<init>(r7)     // Catch:{ all -> 0x035a }
            r10 = r0
            java.lang.String r0 = SHORTCUT_CATEGORY     // Catch:{ all -> 0x035a }
            r10.add(r0)     // Catch:{ all -> 0x035a }
            r0 = 0
            r11 = r0
        L_0x0118:
            int r0 = r30.size()     // Catch:{ all -> 0x035a }
            if (r11 >= r0) goto L_0x0352
            android.content.Intent r0 = new android.content.Intent     // Catch:{ all -> 0x035a }
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r13 = org.telegram.messenger.OpenChatReceiver.class
            r0.<init>(r12, r13)     // Catch:{ all -> 0x035a }
            r12 = r0
            java.lang.Object r0 = r1.get(r11)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$TL_topPeer r0 = (org.telegram.tgnet.TLRPC.TL_topPeer) r0     // Catch:{ all -> 0x035a }
            r13 = r0
            r0 = 0
            r14 = 0
            org.telegram.tgnet.TLRPC$Peer r15 = r13.peer     // Catch:{ all -> 0x035a }
            long r15 = org.telegram.messenger.MessageObject.getPeerId(r15)     // Catch:{ all -> 0x035a }
            r17 = r15
            boolean r15 = org.telegram.messenger.DialogObject.isUserDialog(r17)     // Catch:{ all -> 0x035a }
            if (r15 == 0) goto L_0x015a
            java.lang.String r15 = "userId"
            r16 = r8
            r7 = r17
            r12.putExtra(r15, r7)     // Catch:{ all -> 0x035a }
            org.telegram.messenger.MessagesController r15 = r29.getMessagesController()     // Catch:{ all -> 0x035a }
            r17 = r0
            java.lang.Long r0 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$User r0 = r15.getUser(r0)     // Catch:{ all -> 0x035a }
            r1 = r0
            r15 = r2
            goto L_0x0177
        L_0x015a:
            r16 = r8
            r7 = r17
            r17 = r0
            org.telegram.messenger.MessagesController r0 = r29.getMessagesController()     // Catch:{ all -> 0x035a }
            r15 = r2
            long r1 = -r7
            java.lang.Long r1 = java.lang.Long.valueOf(r1)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)     // Catch:{ all -> 0x035a }
            r14 = r0
            java.lang.String r0 = "chatId"
            long r1 = -r7
            r12.putExtra(r0, r1)     // Catch:{ all -> 0x035a }
            r1 = r17
        L_0x0177:
            if (r1 == 0) goto L_0x017f
            boolean r0 = org.telegram.messenger.UserObject.isDeleted(r1)     // Catch:{ all -> 0x035a }
            if (r0 == 0) goto L_0x0187
        L_0x017f:
            if (r14 != 0) goto L_0x0187
            r19 = r4
            r24 = r5
            goto L_0x0344
        L_0x0187:
            r0 = 0
            if (r1 == 0) goto L_0x01a4
            java.lang.String r2 = r1.first_name     // Catch:{ all -> 0x035a }
            r17 = r0
            java.lang.String r0 = r1.last_name     // Catch:{ all -> 0x035a }
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo     // Catch:{ all -> 0x035a }
            if (r2 == 0) goto L_0x019f
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r1.photo     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ all -> 0x035a }
            r17 = r0
            goto L_0x01b7
        L_0x019f:
            r2 = r17
            r17 = r0
            goto L_0x01b7
        L_0x01a4:
            r17 = r0
            java.lang.String r0 = r14.title     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r14.photo     // Catch:{ all -> 0x035a }
            if (r2 == 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r14.photo     // Catch:{ all -> 0x035a }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ all -> 0x035a }
            r17 = r0
            goto L_0x01b7
        L_0x01b3:
            r2 = r17
            r17 = r0
        L_0x01b7:
            java.lang.String r0 = "currentAccount"
            r18 = r1
            r19 = r4
            r1 = r29
            int r4 = r1.currentAccount     // Catch:{ all -> 0x035a }
            r12.putExtra(r0, r4)     // Catch:{ all -> 0x035a }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035a }
            r0.<init>()     // Catch:{ all -> 0x035a }
            java.lang.String r4 = "com.tmessages.openchat"
            r0.append(r4)     // Catch:{ all -> 0x035a }
            r0.append(r7)     // Catch:{ all -> 0x035a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035a }
            r12.setAction(r0)     // Catch:{ all -> 0x035a }
            java.lang.String r0 = "dialogId"
            r12.putExtra(r0, r7)     // Catch:{ all -> 0x035a }
            java.lang.String r0 = "hash"
            java.lang.String r4 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x035a }
            r12.putExtra(r0, r4)     // Catch:{ all -> 0x035a }
            r0 = 67108864(0x4000000, float:1.5046328E-36)
            r12.addFlags(r0)     // Catch:{ all -> 0x035a }
            r4 = 0
            if (r2 == 0) goto L_0x02d5
            r1 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)     // Catch:{ all -> 0x02c8 }
            r1 = r0
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x02c8 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x02c8 }
            r4 = r0
            if (r4 == 0) goto L_0x02bd
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x02c8 }
            r20 = r0
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x02c8 }
            r21 = r1
            r1 = r20
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r1, r1, r0)     // Catch:{ all -> 0x02c8 }
            r20 = r0
            android.graphics.Canvas r0 = new android.graphics.Canvas     // Catch:{ all -> 0x02c8 }
            r22 = r2
            r2 = r20
            r0.<init>(r2)     // Catch:{ all -> 0x02b5 }
            r20 = r0
            android.graphics.Paint r0 = roundPaint     // Catch:{ all -> 0x02b5 }
            r23 = 1073741824(0x40000000, float:2.0)
            if (r0 != 0) goto L_0x0277
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x02b5 }
            r24 = r5
            r5 = 3
            r0.<init>(r5)     // Catch:{ all -> 0x0271 }
            roundPaint = r0     // Catch:{ all -> 0x0271 }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x0271 }
            r0.<init>()     // Catch:{ all -> 0x0271 }
            bitmapRect = r0     // Catch:{ all -> 0x0271 }
            android.graphics.Paint r0 = new android.graphics.Paint     // Catch:{ all -> 0x0271 }
            r5 = 1
            r0.<init>(r5)     // Catch:{ all -> 0x0271 }
            erasePaint = r0     // Catch:{ all -> 0x0271 }
            android.graphics.PorterDuffXfermode r5 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0271 }
            r25 = r13
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x026c }
            r5.<init>(r13)     // Catch:{ all -> 0x026c }
            r0.setXfermode(r5)     // Catch:{ all -> 0x026c }
            android.graphics.Path r0 = new android.graphics.Path     // Catch:{ all -> 0x026c }
            r0.<init>()     // Catch:{ all -> 0x026c }
            roundPath = r0     // Catch:{ all -> 0x026c }
            int r5 = r1 / 2
            float r5 = (float) r5     // Catch:{ all -> 0x026c }
            int r13 = r1 / 2
            float r13 = (float) r13     // Catch:{ all -> 0x026c }
            int r26 = r1 / 2
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r23)     // Catch:{ all -> 0x026c }
            r28 = r1
            int r1 = r26 - r27
            float r1 = (float) r1
            r26 = r14
            android.graphics.Path$Direction r14 = android.graphics.Path.Direction.CW     // Catch:{ all -> 0x02b3 }
            r0.addCircle(r5, r13, r1, r14)     // Catch:{ all -> 0x02b3 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x02b3 }
            r0.toggleInverseFillType()     // Catch:{ all -> 0x02b3 }
            goto L_0x027f
        L_0x026c:
            r0 = move-exception
            r26 = r14
            goto L_0x02d1
        L_0x0271:
            r0 = move-exception
            r25 = r13
            r26 = r14
            goto L_0x02d1
        L_0x0277:
            r28 = r1
            r24 = r5
            r25 = r13
            r26 = r14
        L_0x027f:
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x02b3 }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)     // Catch:{ all -> 0x02b3 }
            float r1 = (float) r1     // Catch:{ all -> 0x02b3 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)     // Catch:{ all -> 0x02b3 }
            float r5 = (float) r5     // Catch:{ all -> 0x02b3 }
            r13 = 1110966272(0x42380000, float:46.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x02b3 }
            float r14 = (float) r14     // Catch:{ all -> 0x02b3 }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x02b3 }
            float r13 = (float) r13     // Catch:{ all -> 0x02b3 }
            r0.set(r1, r5, r14, r13)     // Catch:{ all -> 0x02b3 }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x02b3 }
            android.graphics.Paint r1 = roundPaint     // Catch:{ all -> 0x02b3 }
            r5 = 0
            r13 = r20
            r13.drawBitmap(r4, r5, r0, r1)     // Catch:{ all -> 0x02b3 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x02b3 }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x02b3 }
            r13.drawPath(r0, r1)     // Catch:{ all -> 0x02b3 }
            r13.setBitmap(r5)     // Catch:{ Exception -> 0x02af }
            goto L_0x02b0
        L_0x02af:
            r0 = move-exception
        L_0x02b0:
            r0 = r2
            r4 = r0
            goto L_0x02c7
        L_0x02b3:
            r0 = move-exception
            goto L_0x02d1
        L_0x02b5:
            r0 = move-exception
            r24 = r5
            r25 = r13
            r26 = r14
            goto L_0x02d1
        L_0x02bd:
            r21 = r1
            r22 = r2
            r24 = r5
            r25 = r13
            r26 = r14
        L_0x02c7:
            goto L_0x02dd
        L_0x02c8:
            r0 = move-exception
            r22 = r2
            r24 = r5
            r25 = r13
            r26 = r14
        L_0x02d1:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x035a }
            goto L_0x02dd
        L_0x02d5:
            r22 = r2
            r24 = r5
            r25 = r13
            r26 = r14
        L_0x02dd:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x035a }
            r0.<init>()     // Catch:{ all -> 0x035a }
            r0.append(r6)     // Catch:{ all -> 0x035a }
            r0.append(r7)     // Catch:{ all -> 0x035a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x035a }
            boolean r1 = android.text.TextUtils.isEmpty(r17)     // Catch:{ all -> 0x035a }
            if (r1 == 0) goto L_0x02f7
            java.lang.String r1 = " "
            r17 = r1
            goto L_0x02f9
        L_0x02f7:
            r1 = r17
        L_0x02f9:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x035a }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            r2.<init>((android.content.Context) r5, (java.lang.String) r0)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = r2.setShortLabel(r1)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = r2.setLongLabel(r1)     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r2 = r2.setIntent(r12)     // Catch:{ all -> 0x035a }
            boolean r5 = org.telegram.messenger.SharedConfig.directShare     // Catch:{ all -> 0x035a }
            if (r5 == 0) goto L_0x0313
            r2.setCategories(r10)     // Catch:{ all -> 0x035a }
        L_0x0313:
            if (r4 == 0) goto L_0x031d
            androidx.core.graphics.drawable.IconCompat r5 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r4)     // Catch:{ all -> 0x035a }
            r2.setIcon(r5)     // Catch:{ all -> 0x035a }
            goto L_0x0329
        L_0x031d:
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            r13 = 2131166075(0x7var_b, float:1.7946385E38)
            androidx.core.graphics.drawable.IconCompat r5 = androidx.core.graphics.drawable.IconCompat.createWithResource(r5, r13)     // Catch:{ all -> 0x035a }
            r2.setIcon(r5)     // Catch:{ all -> 0x035a }
        L_0x0329:
            androidx.core.content.pm.ShortcutInfoCompat r5 = r2.build()     // Catch:{ all -> 0x035a }
            r9.add(r5)     // Catch:{ all -> 0x035a }
            boolean r5 = r3.contains(r0)     // Catch:{ all -> 0x035a }
            if (r5 == 0) goto L_0x033c
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r5, r9)     // Catch:{ all -> 0x035a }
            goto L_0x0341
        L_0x033c:
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x035a }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r5, r9)     // Catch:{ all -> 0x035a }
        L_0x0341:
            r9.clear()     // Catch:{ all -> 0x035a }
        L_0x0344:
            int r11 = r11 + 1
            r1 = r30
            r2 = r15
            r8 = r16
            r4 = r19
            r5 = r24
            r7 = 1
            goto L_0x0118
        L_0x0352:
            r15 = r2
            r19 = r4
            r24 = r5
            r16 = r8
            goto L_0x035b
        L_0x035a:
            r0 = move-exception
        L_0x035b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m780xefbCLASSNAMEab(java.util.ArrayList):void");
    }

    public void loadHints(boolean cache) {
        if (!this.loading && getUserConfig().suggestContacts) {
            if (!cache) {
                this.loading = true;
                TLRPC.TL_contacts_getTopPeers req = new TLRPC.TL_contacts_getTopPeers();
                req.hash = 0;
                req.bots_pm = false;
                req.correspondents = true;
                req.groups = false;
                req.channels = false;
                req.bots_inline = true;
                req.offset = 0;
                req.limit = 20;
                getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda18(this));
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda116(this));
                this.loaded = true;
            }
        }
    }

    /* renamed from: lambda$loadHints$88$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m818lambda$loadHints$88$orgtelegrammessengerMediaDataController() {
        long selfUserId;
        ArrayList<TLRPC.TL_topPeer> hintsNew = new ArrayList<>();
        ArrayList<TLRPC.TL_topPeer> inlineBotsNew = new ArrayList<>();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        long selfUserId2 = getUserConfig().getClientUserId();
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (cursor.next()) {
                long did = cursor.longValue(i);
                if (did != selfUserId2) {
                    int type = cursor.intValue(1);
                    TLRPC.TL_topPeer peer = new TLRPC.TL_topPeer();
                    peer.rating = cursor.doubleValue(2);
                    if (did > 0) {
                        try {
                            peer.peer = new TLRPC.TL_peerUser();
                            peer.peer.user_id = did;
                            usersToLoad.add(Long.valueOf(did));
                            selfUserId = selfUserId2;
                        } catch (Exception e) {
                            e = e;
                            long j = selfUserId2;
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        peer.peer = new TLRPC.TL_peerChat();
                        selfUserId = selfUserId2;
                        try {
                            peer.peer.chat_id = -did;
                            arrayList.add(Long.valueOf(-did));
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.e((Throwable) e);
                        }
                    }
                    if (type == 0) {
                        hintsNew.add(peer);
                    } else if (type == 1) {
                        inlineBotsNew.add(peer);
                    }
                    selfUserId2 = selfUserId;
                    i = 0;
                }
            }
            cursor.dispose();
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
            if (!arrayList.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList), chats);
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda112(this, users, chats, hintsNew, inlineBotsNew));
        } catch (Exception e3) {
            e = e3;
            long j2 = selfUserId2;
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadHints$87$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m817lambda$loadHints$87$orgtelegrammessengerMediaDataController(ArrayList users, ArrayList chats, ArrayList hintsNew, ArrayList inlineBotsNew) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        this.loading = false;
        this.loaded = true;
        this.hints = hintsNew;
        this.inlineBots = inlineBotsNew;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    /* renamed from: lambda$loadHints$93$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m823lambda$loadHints$93$orgtelegrammessengerMediaDataController(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda117(this, response));
        } else if (response instanceof TLRPC.TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda138(this));
        }
    }

    /* renamed from: lambda$loadHints$91$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m821lambda$loadHints$91$orgtelegrammessengerMediaDataController(TLObject response) {
        TLRPC.TL_contacts_topPeers topPeers = (TLRPC.TL_contacts_topPeers) response;
        getMessagesController().putUsers(topPeers.users, false);
        getMessagesController().putChats(topPeers.chats, false);
        for (int a = 0; a < topPeers.categories.size(); a++) {
            TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
            if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                this.inlineBots = category.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = category.peers;
                long selfUserId = getUserConfig().getClientUserId();
                int b = 0;
                while (true) {
                    if (b >= this.hints.size()) {
                        break;
                    } else if (this.hints.get(b).peer.user_id == selfUserId) {
                        this.hints.remove(b);
                        break;
                    } else {
                        b++;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda132(this, topPeers));
    }

    /* renamed from: lambda$loadHints$90$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m820lambda$loadHints$90$orgtelegrammessengerMediaDataController(TLRPC.TL_contacts_topPeers topPeers) {
        int type;
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(topPeers.users, topPeers.chats, false, false);
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int a = 0; a < topPeers.categories.size(); a++) {
                TLRPC.TL_topPeerCategoryPeers category = topPeers.categories.get(a);
                if (category.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                    type = 1;
                } else {
                    type = 0;
                }
                for (int b = 0; b < category.peers.size(); b++) {
                    TLRPC.TL_topPeer peer = category.peers.get(b);
                    state.requery();
                    state.bindLong(1, MessageObject.getPeerId(peer.peer));
                    state.bindInteger(2, type);
                    state.bindDouble(3, peer.rating);
                    state.bindInteger(4, 0);
                    state.step();
                }
            }
            state.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda127(this));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadHints$89$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m819lambda$loadHints$89$orgtelegrammessengerMediaDataController() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    /* renamed from: lambda$loadHints$92$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m822lambda$loadHints$92$orgtelegrammessengerMediaDataController() {
        getUserConfig().suggestContacts = false;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
        clearTopPeers();
    }

    public void clearTopPeers() {
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda72(this));
        buildShortcuts();
    }

    /* renamed from: lambda$clearTopPeers$94$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m782x791b7e1e() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception e) {
        }
    }

    public void increaseInlineRaiting(long uid) {
        int dt;
        if (getUserConfig().suggestContacts) {
            if (getUserConfig().botRatingLoadTime != 0) {
                dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime);
            } else {
                dt = 60;
            }
            TLRPC.TL_topPeer peer = null;
            int a = 0;
            while (true) {
                if (a >= this.inlineBots.size()) {
                    break;
                }
                TLRPC.TL_topPeer p = this.inlineBots.get(a);
                if (p.peer.user_id == uid) {
                    peer = p;
                    break;
                }
                a++;
            }
            if (peer == null) {
                peer = new TLRPC.TL_topPeer();
                peer.peer = new TLRPC.TL_peerUser();
                peer.peer.user_id = uid;
                this.inlineBots.add(peer);
            }
            peer.rating += Math.exp((double) (dt / getMessagesController().ratingDecay));
            Collections.sort(this.inlineBots, MediaDataController$$ExternalSyntheticLambda14.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList<TLRPC.TL_topPeer> arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(uid, 1, peer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    static /* synthetic */ int lambda$increaseInlineRaiting$95(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    public void removeInline(long dialogId) {
        for (int a = 0; a < this.inlineBots.size(); a++) {
            if (this.inlineBots.get(a).peer.user_id == dialogId) {
                this.inlineBots.remove(a);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryBotsInline();
                req.peer = getMessagesController().getInputPeer(dialogId);
                getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda52.INSTANCE);
                deletePeer(dialogId, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$removeInline$96(TLObject response, TLRPC.TL_error error) {
    }

    public void removePeer(long uid) {
        for (int a = 0; a < this.hints.size(); a++) {
            if (this.hints.get(a).peer.user_id == uid) {
                this.hints.remove(a);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC.TL_contacts_resetTopPeerRating req = new TLRPC.TL_contacts_resetTopPeerRating();
                req.category = new TLRPC.TL_topPeerCategoryCorrespondents();
                req.peer = getMessagesController().getInputPeer(uid);
                deletePeer(uid, 0);
                getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda53.INSTANCE);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$removePeer$97(TLObject response, TLRPC.TL_error error) {
    }

    public void increasePeerRaiting(long dialogId) {
        TLRPC.User user;
        if (getUserConfig().suggestContacts && DialogObject.isUserDialog(dialogId) && (user = getMessagesController().getUser(Long.valueOf(dialogId))) != null && !user.bot && !user.self) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda63(this, dialogId));
        }
    }

    /* renamed from: lambda$increasePeerRaiting$100$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m799x30ce45dd(long dialogId) {
        double dt = 0.0d;
        int lastTime = 0;
        int lastMid = 0;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages_v2 WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
            if (cursor.next()) {
                lastMid = cursor.intValue(0);
                lastTime = cursor.intValue(1);
            }
            cursor.dispose();
            if (lastMid > 0 && getUserConfig().ratingLoadTime != 0) {
                dt = (double) (lastTime - getUserConfig().ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda67(this, dialogId, dt));
    }

    /* renamed from: lambda$increasePeerRaiting$99$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m800x7var_b4(long dialogId, double dtFinal) {
        TLRPC.TL_topPeer peer = null;
        int a = 0;
        while (true) {
            if (a >= this.hints.size()) {
                break;
            }
            TLRPC.TL_topPeer p = this.hints.get(a);
            if (p.peer.user_id == dialogId) {
                peer = p;
                break;
            }
            a++;
        }
        if (peer == null) {
            peer = new TLRPC.TL_topPeer();
            peer.peer = new TLRPC.TL_peerUser();
            peer.peer.user_id = dialogId;
            this.hints.add(peer);
        }
        double d = peer.rating;
        double d2 = (double) getMessagesController().ratingDecay;
        Double.isNaN(d2);
        peer.rating = d + Math.exp(dtFinal / d2);
        Collections.sort(this.hints, MediaDataController$$ExternalSyntheticLambda15.INSTANCE);
        savePeer(dialogId, 0, peer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    static /* synthetic */ int lambda$increasePeerRaiting$98(TLRPC.TL_topPeer lhs, TLRPC.TL_topPeer rhs) {
        if (lhs.rating > rhs.rating) {
            return -1;
        }
        if (lhs.rating < rhs.rating) {
            return 1;
        }
        return 0;
    }

    private void savePeer(long did, int type, double rating) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda70(this, did, type, rating));
    }

    /* renamed from: lambda$savePeer$101$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m888lambda$savePeer$101$orgtelegrammessengerMediaDataController(long did, int type, double rating) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            state.requery();
            state.bindLong(1, did);
            state.bindInteger(2, type);
            state.bindDouble(3, rating);
            state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void deletePeer(long dialogId, int type) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda68(this, dialogId, type));
    }

    /* renamed from: lambda$deletePeer$102$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m783lambda$deletePeer$102$orgtelegrammessengerMediaDataController(long dialogId, int type) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Long.valueOf(dialogId), Integer.valueOf(type)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private Intent createIntrnalShortcutIntent(long dialogId) {
        Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        if (DialogObject.isEncryptedDialog(dialogId)) {
            int encryptedChatId = DialogObject.getEncryptedChatId(dialogId);
            shortcutIntent.putExtra("encId", encryptedChatId);
            if (getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChatId)) == null) {
                return null;
            }
        } else if (DialogObject.isUserDialog(dialogId)) {
            shortcutIntent.putExtra("userId", dialogId);
        } else if (!DialogObject.isChatDialog(dialogId)) {
            return null;
        } else {
            shortcutIntent.putExtra("chatId", -dialogId);
        }
        shortcutIntent.putExtra("currentAccount", this.currentAccount);
        shortcutIntent.setAction("com.tmessages.openchat" + dialogId);
        shortcutIntent.addFlags(67108864);
        return shortcutIntent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x023e A[Catch:{ Exception -> 0x02a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01d0 A[Catch:{ Exception -> 0x02a0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installShortcut(long r20) {
        /*
            r19 = this;
            r1 = r20
            android.content.Intent r0 = r19.createIntrnalShortcutIntent(r20)     // Catch:{ Exception -> 0x02a0 }
            r3 = r0
            r0 = 0
            r4 = 0
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r20)     // Catch:{ Exception -> 0x02a0 }
            if (r5 == 0) goto L_0x0034
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r20)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.messenger.MessagesController r6 = r19.getMessagesController()     // Catch:{ Exception -> 0x02a0 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r6.getEncryptedChat(r7)     // Catch:{ Exception -> 0x02a0 }
            if (r6 != 0) goto L_0x0022
            return
        L_0x0022:
            org.telegram.messenger.MessagesController r7 = r19.getMessagesController()     // Catch:{ Exception -> 0x02a0 }
            long r8 = r6.user_id     // Catch:{ Exception -> 0x02a0 }
            java.lang.Long r8 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)     // Catch:{ Exception -> 0x02a0 }
            r0 = r7
            r5 = r4
            r4 = r0
            goto L_0x005f
        L_0x0034:
            boolean r5 = org.telegram.messenger.DialogObject.isUserDialog(r20)     // Catch:{ Exception -> 0x02a0 }
            if (r5 == 0) goto L_0x004a
            org.telegram.messenger.MessagesController r5 = r19.getMessagesController()     // Catch:{ Exception -> 0x02a0 }
            java.lang.Long r6 = java.lang.Long.valueOf(r20)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)     // Catch:{ Exception -> 0x02a0 }
            r0 = r5
            r5 = r4
            r4 = r0
            goto L_0x005f
        L_0x004a:
            boolean r5 = org.telegram.messenger.DialogObject.isChatDialog(r20)     // Catch:{ Exception -> 0x02a0 }
            if (r5 == 0) goto L_0x029f
            org.telegram.messenger.MessagesController r5 = r19.getMessagesController()     // Catch:{ Exception -> 0x02a0 }
            long r6 = -r1
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)     // Catch:{ Exception -> 0x02a0 }
            r4 = r5
            r4 = r0
        L_0x005f:
            if (r4 != 0) goto L_0x0064
            if (r5 != 0) goto L_0x0064
            return
        L_0x0064:
            r0 = 0
            r6 = 0
            if (r4 == 0) goto L_0x00a9
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r4)     // Catch:{ Exception -> 0x02a0 }
            if (r7 == 0) goto L_0x007c
            java.lang.String r7 = "RepliesTitle"
            r8 = 2131627470(0x7f0e0dce, float:1.8882205E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ Exception -> 0x02a0 }
            r6 = 1
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x00bb
        L_0x007c:
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r4)     // Catch:{ Exception -> 0x02a0 }
            if (r7 == 0) goto L_0x0090
            java.lang.String r7 = "SavedMessages"
            r8 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ Exception -> 0x02a0 }
            r6 = 1
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x00bb
        L_0x0090:
            java.lang.String r7 = r4.first_name     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r8 = r4.last_name     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r4.photo     // Catch:{ Exception -> 0x02a0 }
            if (r8 == 0) goto L_0x00a5
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r4.photo     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small     // Catch:{ Exception -> 0x02a0 }
            r0 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x00bb
        L_0x00a5:
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x00bb
        L_0x00a9:
            java.lang.String r7 = r5.title     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$ChatPhoto r8 = r5.photo     // Catch:{ Exception -> 0x02a0 }
            if (r8 == 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$ChatPhoto r8 = r5.photo     // Catch:{ Exception -> 0x02a0 }
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small     // Catch:{ Exception -> 0x02a0 }
            r0 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x00bb
        L_0x00b8:
            r8 = r7
            r7 = r6
            r6 = r0
        L_0x00bb:
            r9 = 0
            r11 = 0
            if (r7 != 0) goto L_0x00c8
            if (r6 == 0) goto L_0x00c2
            goto L_0x00c8
        L_0x00c2:
            r18 = r6
            r17 = r7
            goto L_0x01be
        L_0x00c8:
            r0 = 1
            if (r7 != 0) goto L_0x00e0
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r0)     // Catch:{ all -> 0x00d9 }
            java.lang.String r13 = r12.toString()     // Catch:{ all -> 0x00d9 }
            android.graphics.Bitmap r13 = android.graphics.BitmapFactory.decodeFile(r13)     // Catch:{ all -> 0x00d9 }
            r9 = r13
            goto L_0x00e0
        L_0x00d9:
            r0 = move-exception
            r18 = r6
            r17 = r7
            goto L_0x01bb
        L_0x00e0:
            if (r7 != 0) goto L_0x00eb
            if (r9 == 0) goto L_0x00e5
            goto L_0x00eb
        L_0x00e5:
            r18 = r6
            r17 = r7
            goto L_0x01af
        L_0x00eb:
            r12 = 1114112000(0x42680000, float:58.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x01b6 }
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01b6 }
            android.graphics.Bitmap r13 = android.graphics.Bitmap.createBitmap(r12, r12, r13)     // Catch:{ all -> 0x01b6 }
            r13.eraseColor(r11)     // Catch:{ all -> 0x01b6 }
            android.graphics.Canvas r14 = new android.graphics.Canvas     // Catch:{ all -> 0x01b6 }
            r14.<init>(r13)     // Catch:{ all -> 0x01b6 }
            if (r7 == 0) goto L_0x011e
            org.telegram.ui.Components.AvatarDrawable r15 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x00d9 }
            r15.<init>((org.telegram.tgnet.TLRPC.User) r4)     // Catch:{ all -> 0x00d9 }
            boolean r16 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r4)     // Catch:{ all -> 0x00d9 }
            if (r16 == 0) goto L_0x0112
            r0 = 12
            r15.setAvatarType(r0)     // Catch:{ all -> 0x00d9 }
            goto L_0x0115
        L_0x0112:
            r15.setAvatarType(r0)     // Catch:{ all -> 0x00d9 }
        L_0x0115:
            r15.setBounds(r11, r11, r12, r12)     // Catch:{ all -> 0x00d9 }
            r15.draw(r14)     // Catch:{ all -> 0x00d9 }
            r18 = r6
            goto L_0x0174
        L_0x011e:
            android.graphics.BitmapShader r15 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01b6 }
            android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01b6 }
            android.graphics.Shader$TileMode r10 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01b6 }
            r15.<init>(r9, r11, r10)     // Catch:{ all -> 0x01b6 }
            r10 = r15
            android.graphics.Paint r11 = roundPaint     // Catch:{ all -> 0x01b6 }
            if (r11 != 0) goto L_0x013a
            android.graphics.Paint r11 = new android.graphics.Paint     // Catch:{ all -> 0x00d9 }
            r11.<init>(r0)     // Catch:{ all -> 0x00d9 }
            roundPaint = r11     // Catch:{ all -> 0x00d9 }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x00d9 }
            r0.<init>()     // Catch:{ all -> 0x00d9 }
            bitmapRect = r0     // Catch:{ all -> 0x00d9 }
        L_0x013a:
            float r0 = (float) r12
            int r11 = r9.getWidth()     // Catch:{ all -> 0x01b6 }
            float r11 = (float) r11     // Catch:{ all -> 0x01b6 }
            float r0 = r0 / r11
            r14.save()     // Catch:{ all -> 0x01b6 }
            r14.scale(r0, r0)     // Catch:{ all -> 0x01b6 }
            android.graphics.Paint r11 = roundPaint     // Catch:{ all -> 0x01b6 }
            r11.setShader(r10)     // Catch:{ all -> 0x01b6 }
            android.graphics.RectF r11 = bitmapRect     // Catch:{ all -> 0x01b6 }
            int r15 = r9.getWidth()     // Catch:{ all -> 0x01b6 }
            float r15 = (float) r15     // Catch:{ all -> 0x01b6 }
            r17 = r0
            int r0 = r9.getHeight()     // Catch:{ all -> 0x01b6 }
            float r0 = (float) r0
            r18 = r6
            r6 = 0
            r11.set(r6, r6, r15, r0)     // Catch:{ all -> 0x01b2 }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x01b2 }
            int r6 = r9.getWidth()     // Catch:{ all -> 0x01b2 }
            float r6 = (float) r6     // Catch:{ all -> 0x01b2 }
            int r11 = r9.getHeight()     // Catch:{ all -> 0x01b2 }
            float r11 = (float) r11     // Catch:{ all -> 0x01b2 }
            android.graphics.Paint r15 = roundPaint     // Catch:{ all -> 0x01b2 }
            r14.drawRoundRect(r0, r6, r11, r15)     // Catch:{ all -> 0x01b2 }
            r14.restore()     // Catch:{ all -> 0x01b2 }
        L_0x0174:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x01b2 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x01b2 }
            r6 = 2131165292(0x7var_c, float:1.7944797E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r6)     // Catch:{ all -> 0x01b2 }
            r6 = r0
            r0 = 1097859072(0x41700000, float:15.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x01b2 }
            r10 = r0
            int r0 = r12 - r10
            r11 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x01b2 }
            int r15 = r0 - r15
            int r0 = r12 - r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x01b2 }
            int r11 = r0 - r11
            int r0 = r15 + r10
            r17 = r7
            int r7 = r11 + r10
            r6.setBounds(r15, r11, r0, r7)     // Catch:{ all -> 0x01b0 }
            r6.draw(r14)     // Catch:{ all -> 0x01b0 }
            r7 = 0
            r14.setBitmap(r7)     // Catch:{ Exception -> 0x01ac }
            goto L_0x01ad
        L_0x01ac:
            r0 = move-exception
        L_0x01ad:
            r0 = r13
            r9 = r0
        L_0x01af:
            goto L_0x01be
        L_0x01b0:
            r0 = move-exception
            goto L_0x01bb
        L_0x01b2:
            r0 = move-exception
            r17 = r7
            goto L_0x01bb
        L_0x01b6:
            r0 = move-exception
            r18 = r6
            r17 = r7
        L_0x01bb:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x02a0 }
        L_0x01be:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02a0 }
            r6 = 26
            r7 = 2131165290(0x7var_a, float:1.7944793E38)
            r10 = 2131165289(0x7var_, float:1.794479E38)
            r11 = 2131165293(0x7var_d, float:1.79448E38)
            r12 = 2131165291(0x7var_b, float:1.7944795E38)
            if (r0 < r6) goto L_0x023e
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x02a0 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a0 }
            r13.<init>()     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r14 = "sdid_"
            r13.append(r14)     // Catch:{ Exception -> 0x02a0 }
            r13.append(r1)     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x02a0 }
            r0.<init>((android.content.Context) r6, (java.lang.String) r13)     // Catch:{ Exception -> 0x02a0 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setShortLabel(r8)     // Catch:{ Exception -> 0x02a0 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r3)     // Catch:{ Exception -> 0x02a0 }
            if (r9 == 0) goto L_0x01fb
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r9)     // Catch:{ Exception -> 0x02a0 }
            r0.setIcon(r6)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0232
        L_0x01fb:
            if (r4 == 0) goto L_0x0215
            boolean r6 = r4.bot     // Catch:{ Exception -> 0x02a0 }
            if (r6 == 0) goto L_0x020b
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r10)     // Catch:{ Exception -> 0x02a0 }
            r0.setIcon(r6)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0232
        L_0x020b:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r11)     // Catch:{ Exception -> 0x02a0 }
            r0.setIcon(r6)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0232
        L_0x0215:
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x02a0 }
            if (r6 == 0) goto L_0x0229
            boolean r6 = r5.megagroup     // Catch:{ Exception -> 0x02a0 }
            if (r6 != 0) goto L_0x0229
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r7)     // Catch:{ Exception -> 0x02a0 }
            r0.setIcon(r6)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0232
        L_0x0229:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r12)     // Catch:{ Exception -> 0x02a0 }
            r0.setIcon(r6)     // Catch:{ Exception -> 0x02a0 }
        L_0x0232:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            androidx.core.content.pm.ShortcutInfoCompat r7 = r0.build()     // Catch:{ Exception -> 0x02a0 }
            r10 = 0
            androidx.core.content.pm.ShortcutManagerCompat.requestPinShortcut(r6, r7, r10)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x029e
        L_0x023e:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x02a0 }
            r0.<init>()     // Catch:{ Exception -> 0x02a0 }
            if (r9 == 0) goto L_0x024b
            java.lang.String r6 = "android.intent.extra.shortcut.ICON"
            r0.putExtra(r6, r9)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0284
        L_0x024b:
            java.lang.String r6 = "android.intent.extra.shortcut.ICON_RESOURCE"
            if (r4 == 0) goto L_0x0267
            boolean r7 = r4.bot     // Catch:{ Exception -> 0x02a0 }
            if (r7 == 0) goto L_0x025d
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            android.content.Intent$ShortcutIconResource r7 = android.content.Intent.ShortcutIconResource.fromContext(r7, r10)     // Catch:{ Exception -> 0x02a0 }
            r0.putExtra(r6, r7)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0284
        L_0x025d:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            android.content.Intent$ShortcutIconResource r7 = android.content.Intent.ShortcutIconResource.fromContext(r7, r11)     // Catch:{ Exception -> 0x02a0 }
            r0.putExtra(r6, r7)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0284
        L_0x0267:
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x02a0 }
            if (r10 == 0) goto L_0x027b
            boolean r10 = r5.megagroup     // Catch:{ Exception -> 0x02a0 }
            if (r10 != 0) goto L_0x027b
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            android.content.Intent$ShortcutIconResource r7 = android.content.Intent.ShortcutIconResource.fromContext(r10, r7)     // Catch:{ Exception -> 0x02a0 }
            r0.putExtra(r6, r7)     // Catch:{ Exception -> 0x02a0 }
            goto L_0x0284
        L_0x027b:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            android.content.Intent$ShortcutIconResource r7 = android.content.Intent.ShortcutIconResource.fromContext(r7, r12)     // Catch:{ Exception -> 0x02a0 }
            r0.putExtra(r6, r7)     // Catch:{ Exception -> 0x02a0 }
        L_0x0284:
            java.lang.String r6 = "android.intent.extra.shortcut.INTENT"
            r0.putExtra(r6, r3)     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r6 = "android.intent.extra.shortcut.NAME"
            r0.putExtra(r6, r8)     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r6 = "duplicate"
            r7 = 0
            r0.putExtra(r6, r7)     // Catch:{ Exception -> 0x02a0 }
            java.lang.String r6 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r0.setAction(r6)     // Catch:{ Exception -> 0x02a0 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02a0 }
            r6.sendBroadcast(r0)     // Catch:{ Exception -> 0x02a0 }
        L_0x029e:
            goto L_0x02a4
        L_0x029f:
            return
        L_0x02a0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    public void uninstallShortcut(long dialogId) {
        String name;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("sdid_" + dialogId);
                arrayList.add("ndid_" + dialogId);
                ShortcutManagerCompat.removeDynamicShortcuts(ApplicationLoader.applicationContext, arrayList);
                if (Build.VERSION.SDK_INT >= 30) {
                    ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).removeLongLivedShortcuts(arrayList);
                }
                return;
            }
            TLRPC.User user = null;
            TLRPC.Chat chat = null;
            if (DialogObject.isEncryptedDialog(dialogId)) {
                TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
                if (encryptedChat != null) {
                    user = getMessagesController().getUser(Long.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (DialogObject.isUserDialog(dialogId)) {
                user = getMessagesController().getUser(Long.valueOf(dialogId));
            } else if (DialogObject.isChatDialog(dialogId)) {
                chat = getMessagesController().getChat(Long.valueOf(-dialogId));
            } else {
                return;
            }
            if (user != null || chat != null) {
                if (user != null) {
                    name = ContactsController.formatName(user.first_name, user.last_name);
                } else {
                    name = chat.title;
                }
                Intent addIntent = new Intent();
                addIntent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(dialogId));
                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                addIntent.putExtra("duplicate", false);
                addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$static$103(TLRPC.MessageEntity entity1, TLRPC.MessageEntity entity2) {
        if (entity1.offset > entity2.offset) {
            return 1;
        }
        if (entity1.offset < entity2.offset) {
            return -1;
        }
        return 0;
    }

    public void loadPinnedMessages(long dialogId, int maxId, int fallback) {
        if (this.loadingPinnedMessages.indexOfKey(dialogId) < 0) {
            this.loadingPinnedMessages.put(dialogId, true);
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.peer = getMessagesController().getInputPeer(dialogId);
            req.limit = 40;
            req.offset_id = maxId;
            req.q = "";
            req.filter = new TLRPC.TL_inputMessagesFilterPinned();
            getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda26(this, fallback, req, dialogId, maxId));
        }
    }

    /* renamed from: lambda$loadPinnedMessages$105$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m830xa9e14b38(int fallback, TLRPC.TL_messages_search req, long dialogId, int maxId, TLObject response, TLRPC.TL_error error) {
        boolean endReached;
        int totalCount;
        TLObject tLObject = response;
        ArrayList<Integer> ids = new ArrayList<>();
        HashMap<Integer, MessageObject> messages = new HashMap<>();
        int totalCount2 = 0;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) tLObject;
            LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
            for (int a = 0; a < res.users.size(); a++) {
                TLRPC.User user = res.users.get(a);
                usersDict.put(user.id, user);
            }
            LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                TLRPC.Chat chat = res.chats.get(a2);
                chatsDict.put(chat.id, chat);
            }
            getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            int N = res.messages.size();
            for (int a3 = 0; a3 < N; a3++) {
                TLRPC.Message message = res.messages.get(a3);
                if (!(message instanceof TLRPC.TL_messageService) && !(message instanceof TLRPC.TL_messageEmpty)) {
                    ids.add(Integer.valueOf(message.id));
                    MessageObject messageObject = r13;
                    MessageObject messageObject2 = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false, false);
                    messages.put(Integer.valueOf(message.id), messageObject);
                }
            }
            if (fallback != 0 && ids.isEmpty()) {
                ids.add(Integer.valueOf(fallback));
            }
            boolean endReached2 = res.messages.size() < req.limit;
            totalCount = Math.max(res.count, res.messages.size());
            endReached = endReached2;
        } else {
            TLRPC.TL_messages_search tL_messages_search = req;
            if (fallback != 0) {
                ids.add(Integer.valueOf(fallback));
                totalCount2 = 1;
            }
            totalCount = totalCount2;
            endReached = false;
        }
        long j = dialogId;
        getMessagesStorage().updatePinnedMessages(j, ids, true, totalCount, maxId, endReached, messages);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda65(this, j));
    }

    /* renamed from: lambda$loadPinnedMessages$104$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m829xe6f4e1d9(long dialogId) {
        this.loadingPinnedMessages.remove(dialogId);
    }

    public ArrayList<MessageObject> loadPinnedMessages(long dialogId, long channelId, ArrayList<Integer> mids, boolean useQueue) {
        if (!useQueue) {
            return loadPinnedMessageInternal(dialogId, channelId, mids, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda77(this, dialogId, channelId, mids));
        return null;
    }

    /* renamed from: lambda$loadPinnedMessages$106$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m831x6ccdb497(long dialogId, long channelId, ArrayList mids) {
        loadPinnedMessageInternal(dialogId, channelId, mids, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: java.lang.StringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<org.telegram.messenger.MessageObject> loadPinnedMessageInternal(long r28, long r30, java.util.ArrayList<java.lang.Integer> r32, boolean r33) {
        /*
            r27 = this;
            r8 = r28
            r10 = r30
            r12 = r32
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r0.<init>(r12)     // Catch:{ Exception -> 0x020b }
            r1 = 0
            java.lang.String r13 = ","
            int r3 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0038
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            r4 = 0
            int r5 = r32.size()     // Catch:{ Exception -> 0x020b }
        L_0x001d:
            if (r4 >= r5) goto L_0x0035
            java.lang.Object r6 = r12.get(r4)     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x020b }
            int r7 = r3.length()     // Catch:{ Exception -> 0x020b }
            if (r7 == 0) goto L_0x002e
            r3.append(r13)     // Catch:{ Exception -> 0x020b }
        L_0x002e:
            r3.append(r6)     // Catch:{ Exception -> 0x020b }
            int r4 = r4 + 1
            goto L_0x001d
        L_0x0035:
            r14 = r3
            goto L_0x003d
        L_0x0038:
            java.lang.String r3 = android.text.TextUtils.join(r13, r12)     // Catch:{ Exception -> 0x020b }
            r14 = r3
        L_0x003d:
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            r15 = r3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            r7 = r3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            r5 = r3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            r6 = r3
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r3.<init>()     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.UserConfig r4 = r27.getUserConfig()     // Catch:{ Exception -> 0x020b }
            long r1 = r4.clientUserId     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.MessagesStorage r4 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x020b }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x020b }
            r18 = r5
            java.util.Locale r5 = java.util.Locale.US     // Catch:{ Exception -> 0x020b }
            r19 = r7
            java.lang.String r7 = "SELECT data, mid, date FROM messages_v2 WHERE mid IN (%s) AND uid = %d"
            r12 = 2
            java.lang.Object[] r10 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x020b }
            r11 = 0
            r10[r11] = r14     // Catch:{ Exception -> 0x020b }
            java.lang.Long r20 = java.lang.Long.valueOf(r28)     // Catch:{ Exception -> 0x020b }
            r12 = 1
            r10[r12] = r20     // Catch:{ Exception -> 0x020b }
            java.lang.String r5 = java.lang.String.format(r5, r7, r10)     // Catch:{ Exception -> 0x020b }
            java.lang.Object[] r7 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x020b }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x020b }
        L_0x0087:
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x020b }
            if (r5 == 0) goto L_0x00c7
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r11)     // Catch:{ Exception -> 0x020b }
            if (r5 == 0) goto L_0x00c5
            int r7 = r5.readInt32(r11)     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.TLRPC$Message r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r7, r11)     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.TLRPC$MessageAction r10 = r7.action     // Catch:{ Exception -> 0x020b }
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear     // Catch:{ Exception -> 0x020b }
            if (r10 != 0) goto L_0x00c2
            r7.readAttachPath(r5, r1)     // Catch:{ Exception -> 0x020b }
            int r10 = r4.intValue(r12)     // Catch:{ Exception -> 0x020b }
            r7.id = r10     // Catch:{ Exception -> 0x020b }
            r10 = 2
            int r12 = r4.intValue(r10)     // Catch:{ Exception -> 0x020b }
            r7.date = r12     // Catch:{ Exception -> 0x020b }
            r7.dialog_id = r8     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r7, r6, r3)     // Catch:{ Exception -> 0x020b }
            r15.add(r7)     // Catch:{ Exception -> 0x020b }
            int r10 = r7.id     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x020b }
            r0.remove(r10)     // Catch:{ Exception -> 0x020b }
        L_0x00c2:
            r5.reuse()     // Catch:{ Exception -> 0x020b }
        L_0x00c5:
            r12 = 1
            goto L_0x0087
        L_0x00c7:
            r4.dispose()     // Catch:{ Exception -> 0x020b }
            boolean r5 = r0.isEmpty()     // Catch:{ Exception -> 0x020b }
            if (r5 != 0) goto L_0x012f
            org.telegram.messenger.MessagesStorage r5 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x020b }
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ Exception -> 0x020b }
            java.util.Locale r7 = java.util.Locale.US     // Catch:{ Exception -> 0x020b }
            java.lang.String r10 = "SELECT data FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)"
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x020b }
            java.lang.Long r21 = java.lang.Long.valueOf(r28)     // Catch:{ Exception -> 0x020b }
            r12[r11] = r21     // Catch:{ Exception -> 0x020b }
            java.lang.String r21 = android.text.TextUtils.join(r13, r0)     // Catch:{ Exception -> 0x020b }
            r20 = 1
            r12[r20] = r21     // Catch:{ Exception -> 0x020b }
            java.lang.String r7 = java.lang.String.format(r7, r10, r12)     // Catch:{ Exception -> 0x020b }
            java.lang.Object[] r10 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x020b }
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r7, r10)     // Catch:{ Exception -> 0x020b }
            r4 = r5
        L_0x00f8:
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x020b }
            if (r5 == 0) goto L_0x012a
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r11)     // Catch:{ Exception -> 0x020b }
            if (r5 == 0) goto L_0x0129
            int r7 = r5.readInt32(r11)     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.TLRPC$Message r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r7, r11)     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.TLRPC$MessageAction r10 = r7.action     // Catch:{ Exception -> 0x020b }
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear     // Catch:{ Exception -> 0x020b }
            if (r10 != 0) goto L_0x0126
            r7.readAttachPath(r5, r1)     // Catch:{ Exception -> 0x020b }
            r7.dialog_id = r8     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r7, r6, r3)     // Catch:{ Exception -> 0x020b }
            r15.add(r7)     // Catch:{ Exception -> 0x020b }
            int r10 = r7.id     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x020b }
            r0.remove(r10)     // Catch:{ Exception -> 0x020b }
        L_0x0126:
            r5.reuse()     // Catch:{ Exception -> 0x020b }
        L_0x0129:
            goto L_0x00f8
        L_0x012a:
            r4.dispose()     // Catch:{ Exception -> 0x020b }
            r10 = r4
            goto L_0x0130
        L_0x012f:
            r10 = r4
        L_0x0130:
            boolean r4 = r0.isEmpty()     // Catch:{ Exception -> 0x020b }
            if (r4 != 0) goto L_0x019e
            r11 = r30
            r4 = 0
            int r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x017b
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r4 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages     // Catch:{ Exception -> 0x020b }
            r4.<init>()     // Catch:{ Exception -> 0x020b }
            r7 = r4
            org.telegram.messenger.MessagesController r4 = r27.getMessagesController()     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.TLRPC$InputChannel r4 = r4.getInputChannel((long) r11)     // Catch:{ Exception -> 0x020b }
            r7.channel = r4     // Catch:{ Exception -> 0x020b }
            r7.id = r0     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.ConnectionsManager r5 = r27.getConnectionsManager()     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda34 r4 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda34     // Catch:{ Exception -> 0x020b }
            r21 = r1
            r1 = r4
            r2 = r27
            r23 = r3
            r24 = r10
            r10 = r4
            r3 = r30
            r12 = r6
            r25 = r14
            r11 = r18
            r14 = r5
            r5 = r28
            r16 = r7
            r26 = r11
            r11 = r19
            r1.<init>(r2, r3, r5, r7)     // Catch:{ Exception -> 0x020b }
            r1 = r16
            r14.sendRequest(r1, r10)     // Catch:{ Exception -> 0x020b }
            r4 = r27
            goto L_0x01ad
        L_0x017b:
            r21 = r1
            r23 = r3
            r12 = r6
            r24 = r10
            r25 = r14
            r26 = r18
            r11 = r19
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r1 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages     // Catch:{ Exception -> 0x020b }
            r1.<init>()     // Catch:{ Exception -> 0x020b }
            r1.id = r0     // Catch:{ Exception -> 0x020b }
            org.telegram.tgnet.ConnectionsManager r2 = r27.getConnectionsManager()     // Catch:{ Exception -> 0x020b }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda36 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda36     // Catch:{ Exception -> 0x020b }
            r4 = r27
            r3.<init>(r4, r8, r1)     // Catch:{ Exception -> 0x0209 }
            r2.sendRequest(r1, r3)     // Catch:{ Exception -> 0x0209 }
            goto L_0x01ad
        L_0x019e:
            r4 = r27
            r21 = r1
            r23 = r3
            r12 = r6
            r24 = r10
            r25 = r14
            r26 = r18
            r11 = r19
        L_0x01ad:
            boolean r1 = r15.isEmpty()     // Catch:{ Exception -> 0x0209 }
            if (r1 != 0) goto L_0x0203
            boolean r1 = r12.isEmpty()     // Catch:{ Exception -> 0x0209 }
            if (r1 != 0) goto L_0x01c4
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0209 }
            java.lang.String r2 = android.text.TextUtils.join(r13, r12)     // Catch:{ Exception -> 0x0209 }
            r1.getUsersInternal(r2, r11)     // Catch:{ Exception -> 0x0209 }
        L_0x01c4:
            boolean r1 = r23.isEmpty()     // Catch:{ Exception -> 0x0209 }
            if (r1 != 0) goto L_0x01da
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0209 }
            r2 = r23
            java.lang.String r3 = android.text.TextUtils.join(r13, r2)     // Catch:{ Exception -> 0x0209 }
            r5 = r26
            r1.getChatsInternal(r3, r5)     // Catch:{ Exception -> 0x0209 }
            goto L_0x01de
        L_0x01da:
            r2 = r23
            r5 = r26
        L_0x01de:
            if (r33 == 0) goto L_0x01f2
            r19 = 1
            r20 = 1
            r1 = r15
            r15 = r27
            r16 = r1
            r17 = r11
            r18 = r5
            java.util.ArrayList r3 = r15.broadcastPinnedMessage(r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0209 }
            return r3
        L_0x01f2:
            r1 = r15
            r19 = 1
            r20 = 0
            r15 = r27
            r16 = r1
            r17 = r11
            r18 = r5
            r15.broadcastPinnedMessage(r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0209 }
            goto L_0x0208
        L_0x0203:
            r1 = r15
            r2 = r23
            r5 = r26
        L_0x0208:
            goto L_0x0211
        L_0x0209:
            r0 = move-exception
            goto L_0x020e
        L_0x020b:
            r0 = move-exception
            r4 = r27
        L_0x020e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0211:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, long, java.util.ArrayList, boolean):java.util.ArrayList");
    }

    /* renamed from: lambda$loadPinnedMessageInternal$107$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m827x8CLASSNAMEe474(long channelId, long dialogId, TLRPC.TL_channels_getMessages req, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(channelId));
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages, messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessages(dialogId, messagesRes.messages);
                ok = true;
            } else {
                long j = dialogId;
            }
        } else {
            long j2 = dialogId;
        }
        if (!ok) {
            getMessagesStorage().updatePinnedMessages(dialogId, req.id, false, -1, 0, false, (HashMap<Integer, MessageObject>) null);
            return;
        }
        TLRPC.TL_channels_getMessages tL_channels_getMessages = req;
    }

    /* renamed from: lambda$loadPinnedMessageInternal$108$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m828x4var_dd3(long dialogId, TLRPC.TL_messages_getMessages req, TLObject response, TLRPC.TL_error error) {
        boolean ok = false;
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            removeEmptyMessages(messagesRes.messages);
            if (!messagesRes.messages.isEmpty()) {
                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                broadcastPinnedMessage(messagesRes.messages, messagesRes.users, messagesRes.chats, false, false);
                getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                savePinnedMessages(dialogId, messagesRes.messages);
                ok = true;
            }
        }
        if (!ok) {
            getMessagesStorage().updatePinnedMessages(dialogId, req.id, false, -1, 0, false, (HashMap<Integer, MessageObject>) null);
        }
    }

    private void savePinnedMessages(long dialogId, ArrayList<TLRPC.Message> arrayList) {
        if (!arrayList.isEmpty()) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda107(this, arrayList, dialogId));
        }
    }

    /* renamed from: lambda$savePinnedMessages$109$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m889x7b854b7d(ArrayList arrayList, long dialogId) {
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(a);
                NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(data);
                state.requery();
                state.bindLong(1, dialogId);
                state.bindInteger(2, message.id);
                state.bindByteBuffer(3, data);
                state.step();
                data.reuse();
            }
            state.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private ArrayList<MessageObject> broadcastPinnedMessage(ArrayList<TLRPC.Message> results, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, boolean isCache, boolean returnValue) {
        ArrayList<TLRPC.User> arrayList = users;
        ArrayList<TLRPC.Chat> arrayList2 = chats;
        if (results.isEmpty()) {
            return null;
        }
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = arrayList.get(a);
            usersDict.put(user.id, user);
        }
        LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = arrayList2.get(a2);
            chatsDict.put(chat.id, chat);
        }
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (returnValue) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda113(this, arrayList, isCache, arrayList2));
            int checkedCount = 0;
            int N = results.size();
            for (int a3 = 0; a3 < N; a3++) {
                TLRPC.Message message = results.get(a3);
                if ((message.media instanceof TLRPC.TL_messageMediaDocument) || (message.media instanceof TLRPC.TL_messageMediaPhoto)) {
                    checkedCount++;
                }
                MessageObject messageObject = r1;
                TLRPC.Message message2 = message;
                MessageObject messageObject2 = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false, checkedCount < 30);
                messageObjects.add(messageObject);
            }
            return messageObjects;
        }
        boolean z = isCache;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda115(this, users, isCache, chats, results, messageObjects, usersDict, chatsDict));
        return null;
    }

    /* renamed from: lambda$broadcastPinnedMessage$110$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m776x64ad3ada(ArrayList users, boolean isCache, ArrayList chats) {
        getMessagesController().putUsers(users, isCache);
        getMessagesController().putChats(chats, isCache);
    }

    /* renamed from: lambda$broadcastPinnedMessage$112$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m778xea860d98(ArrayList users, boolean isCache, ArrayList chats, ArrayList results, ArrayList messageObjects, LongSparseArray usersDict, LongSparseArray chatsDict) {
        boolean z = isCache;
        ArrayList arrayList = messageObjects;
        getMessagesController().putUsers(users, z);
        getMessagesController().putChats(chats, z);
        int checkedCount = 0;
        int a = 0;
        int N = results.size();
        while (a < N) {
            TLRPC.Message message = (TLRPC.Message) results.get(a);
            if ((message.media instanceof TLRPC.TL_messageMediaDocument) || (message.media instanceof TLRPC.TL_messageMediaPhoto)) {
                checkedCount++;
            }
            MessageObject messageObject = r10;
            MessageObject messageObject2 = new MessageObject(this.currentAccount, message, (LongSparseArray<TLRPC.User>) usersDict, (LongSparseArray<TLRPC.Chat>) chatsDict, false, checkedCount < 30);
            arrayList.add(messageObject);
            a++;
            boolean z2 = isCache;
        }
        ArrayList arrayList2 = results;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda101(this, arrayList));
    }

    /* renamed from: lambda$broadcastPinnedMessage$111$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m777x2799a439(ArrayList messageObjects) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(((MessageObject) messageObjects.get(0)).getDialogId()), null, true, messageObjects, null, 0, -1, false);
    }

    private static void removeEmptyMessages(ArrayList<TLRPC.Message> messages) {
        int a = 0;
        while (a < messages.size()) {
            TLRPC.Message message = messages.get(a);
            if (message == null || (message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                messages.remove(a);
                a--;
            }
            a++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> messages, long dialogId, boolean scheduled, Runnable callback) {
        ArrayList<MessageObject> arrayList = messages;
        long j = dialogId;
        if (DialogObject.isEncryptedDialog(dialogId)) {
            ArrayList<Long> replyMessages = new ArrayList<>();
            LongSparseArray<ArrayList<MessageObject>> replyMessageRandomOwners = new LongSparseArray<>();
            for (int a = 0; a < messages.size(); a++) {
                MessageObject messageObject = arrayList.get(a);
                if (messageObject != null && messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long id = messageObject.messageOwner.reply_to.reply_to_random_id;
                    ArrayList<MessageObject> messageObjects = replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList<>();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(Long.valueOf(id))) {
                        replyMessages.add(Long.valueOf(id));
                    }
                }
            }
            if (replyMessages.isEmpty() == 0) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda109(this, replyMessages, dialogId, replyMessageRandomOwners, callback));
            } else if (callback != null) {
                callback.run();
            }
        } else {
            LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners = new LongSparseArray<>();
            LongSparseArray<ArrayList<Integer>> dialogReplyMessagesIds = new LongSparseArray<>();
            for (int a2 = 0; a2 < messages.size(); a2++) {
                MessageObject messageObject2 = arrayList.get(a2);
                if (messageObject2 != null && messageObject2.getId() > 0 && messageObject2.isReply()) {
                    int messageId = messageObject2.messageOwner.reply_to.reply_to_msg_id;
                    long channelId = 0;
                    if (messageObject2.messageOwner.reply_to.reply_to_peer_id != null) {
                        if (messageObject2.messageOwner.reply_to.reply_to_peer_id.channel_id != 0) {
                            channelId = messageObject2.messageOwner.reply_to.reply_to_peer_id.channel_id;
                        }
                    } else if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                        channelId = messageObject2.messageOwner.peer_id.channel_id;
                    }
                    if (messageObject2.replyMessageObject == null || !(messageObject2.replyMessageObject.messageOwner == null || messageObject2.replyMessageObject.messageOwner.peer_id == null || (messageObject2.messageOwner instanceof TLRPC.TL_messageEmpty) || messageObject2.replyMessageObject.messageOwner.peer_id.channel_id == channelId)) {
                        SparseArray<ArrayList<MessageObject>> sparseArray = replyMessageOwners.get(j);
                        ArrayList<Integer> ids = dialogReplyMessagesIds.get(channelId);
                        if (sparseArray == null) {
                            sparseArray = new SparseArray<>();
                            replyMessageOwners.put(j, sparseArray);
                        }
                        if (ids == null) {
                            ids = new ArrayList<>();
                            dialogReplyMessagesIds.put(channelId, ids);
                        }
                        ArrayList<MessageObject> arrayList2 = sparseArray.get(messageId);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList<>();
                            sparseArray.put(messageId, arrayList2);
                            if (!ids.contains(Integer.valueOf(messageId))) {
                                ids.add(Integer.valueOf(messageId));
                            }
                        }
                        arrayList2.add(messageObject2);
                    }
                }
            }
            if (replyMessageOwners.isEmpty() == 0) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda88(this, replyMessageOwners, dialogReplyMessagesIds, dialogId, scheduled, callback));
            } else if (callback != null) {
                callback.run();
            }
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$114$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m837xd3d402ff(ArrayList replyMessages, long dialogId, LongSparseArray replyMessageRandomOwners, Runnable callback) {
        long j = dialogId;
        LongSparseArray longSparseArray = replyMessageRandomOwners;
        try {
            ArrayList<MessageObject> loadedMessages = new ArrayList<>();
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            Locale locale = Locale.US;
            int i = 1;
            Object[] objArr = new Object[1];
            try {
                boolean z = false;
                objArr[0] = TextUtils.join(",", replyMessages);
                SQLiteCursor cursor = database.queryFinalized(String.format(locale, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms_v2 as r INNER JOIN messages_v2 as m ON r.mid = m.mid AND r.uid = m.uid WHERE r.random_id IN(%s)", objArr), new Object[0]);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(z ? 1 : 0);
                    if (data != null) {
                        TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                        message.readAttachPath(data, getUserConfig().clientUserId);
                        data.reuse();
                        message.id = cursor.intValue(i);
                        message.date = cursor.intValue(2);
                        message.dialog_id = j;
                        long value = cursor.longValue(3);
                        ArrayList<MessageObject> arrayList = (ArrayList) longSparseArray.get(value);
                        longSparseArray.remove(value);
                        if (arrayList != null) {
                            MessageObject messageObject = new MessageObject(this.currentAccount, message, z, z);
                            loadedMessages.add(messageObject);
                            int b = 0;
                            while (b < arrayList.size()) {
                                MessageObject object = arrayList.get(b);
                                object.replyMessageObject = messageObject;
                                NativeByteBuffer data2 = data;
                                object.messageOwner.reply_to = new TLRPC.TL_messageReplyHeader();
                                object.messageOwner.reply_to.reply_to_msg_id = messageObject.getId();
                                b++;
                                data = data2;
                            }
                        }
                    }
                    i = 1;
                    z = false;
                }
                cursor.dispose();
                if (replyMessageRandomOwners.size() != 0) {
                    for (int b2 = 0; b2 < replyMessageRandomOwners.size(); b2++) {
                        ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(b2);
                        for (int a = 0; a < arrayList2.size(); a++) {
                            TLRPC.Message message2 = arrayList2.get(a).messageOwner;
                            if (message2.reply_to != null) {
                                message2.reply_to.reply_to_random_id = 0;
                            }
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda78(this, j, loadedMessages));
                if (callback != null) {
                    callback.run();
                }
            } catch (Exception e) {
                e = e;
                FileLog.e((Throwable) e);
            }
        } catch (Exception e2) {
            e = e2;
            ArrayList arrayList3 = replyMessages;
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$113$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m836x10e799a0(long dialogId, ArrayList loadedMessages) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialogId), loadedMessages, null);
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$117$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m840x1CLASSNAMEf1c(LongSparseArray replyMessageOwners, LongSparseArray dialogReplyMessagesIds, long dialogId, boolean scheduled, Runnable callback) {
        ArrayList<TLRPC.Chat> chats;
        int N;
        ArrayList<TLRPC.User> users;
        ArrayList<TLRPC.Message> result;
        int a;
        int N2;
        SparseArray<ArrayList<MessageObject>> owners;
        long j;
        LongSparseArray longSparseArray = replyMessageOwners;
        LongSparseArray longSparseArray2 = dialogReplyMessagesIds;
        try {
            ArrayList<TLRPC.Message> result2 = new ArrayList<>();
            ArrayList<TLRPC.User> users2 = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats2 = new ArrayList<>();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int b = 0;
            int N22 = replyMessageOwners.size();
            while (b < N22) {
                try {
                    long did = longSparseArray.keyAt(b);
                    SparseArray<ArrayList<MessageObject>> owners2 = (SparseArray) longSparseArray.valueAt(b);
                    ArrayList<Integer> ids = (ArrayList) longSparseArray2.get(did);
                    if (ids == null) {
                        long j2 = dialogId;
                        N2 = N22;
                    } else {
                        N2 = N22;
                        long j3 = did;
                        SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", new Object[]{TextUtils.join(",", ids), Long.valueOf(dialogId)}), new Object[0]);
                        while (cursor.next()) {
                            NativeByteBuffer data = cursor.byteBufferValue(0);
                            if (data != null) {
                                TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                                message.readAttachPath(data, getUserConfig().clientUserId);
                                data.reuse();
                                message.id = cursor.intValue(1);
                                message.date = cursor.intValue(2);
                                message.dialog_id = dialogId;
                                MessagesStorage.addUsersAndChatsFromMessage(message, arrayList, arrayList2);
                                result2.add(message);
                                if (message.peer_id != null) {
                                    NativeByteBuffer nativeByteBuffer = data;
                                    j = message.peer_id.channel_id;
                                } else {
                                    j = 0;
                                }
                                long channelId = j;
                                ArrayList<Integer> mids = (ArrayList) longSparseArray2.get(channelId);
                                if (mids != null) {
                                    owners = owners2;
                                    mids.remove(Integer.valueOf(message.id));
                                    if (mids.isEmpty()) {
                                        longSparseArray2.remove(channelId);
                                    }
                                } else {
                                    owners = owners2;
                                }
                            } else {
                                long j4 = dialogId;
                                NativeByteBuffer nativeByteBuffer2 = data;
                                owners = owners2;
                            }
                            LongSparseArray longSparseArray3 = replyMessageOwners;
                            owners2 = owners;
                        }
                        long j5 = dialogId;
                        SparseArray<ArrayList<MessageObject>> sparseArray = owners2;
                        cursor.dispose();
                    }
                    b++;
                    longSparseArray = replyMessageOwners;
                    N22 = N2;
                } catch (Exception e) {
                    e = e;
                    long j6 = dialogId;
                    FileLog.e((Throwable) e);
                }
            }
            long j7 = dialogId;
            int i = N22;
            if (arrayList.isEmpty() == 0) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList), users2);
            }
            if (!arrayList2.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList2), chats2);
            }
            broadcastReplyMessages(result2, replyMessageOwners, users2, chats2, dialogId, true);
            if (!dialogReplyMessagesIds.isEmpty()) {
                int N3 = dialogReplyMessagesIds.size();
                int a2 = 0;
                while (a2 < N3) {
                    long channelId2 = longSparseArray2.keyAt(a2);
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                        req.channel = getMessagesController().getInputChannel(channelId2);
                        req.id = (ArrayList) longSparseArray2.valueAt(a2);
                        result = result2;
                        MediaDataController$$ExternalSyntheticLambda32 mediaDataController$$ExternalSyntheticLambda32 = r1;
                        long j8 = channelId2;
                        users = users2;
                        a = a2;
                        N = N3;
                        chats = chats2;
                        ConnectionsManager connectionsManager = getConnectionsManager();
                        MediaDataController$$ExternalSyntheticLambda32 mediaDataController$$ExternalSyntheticLambda322 = new MediaDataController$$ExternalSyntheticLambda32(this, dialogId, channelId2, replyMessageOwners, scheduled, callback);
                        connectionsManager.sendRequest(req, mediaDataController$$ExternalSyntheticLambda32);
                    } else {
                        result = result2;
                        long j9 = channelId2;
                        N = N3;
                        users = users2;
                        chats = chats2;
                        a = a2;
                        TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                        req2.id = (ArrayList) longSparseArray2.valueAt(a);
                        getConnectionsManager().sendRequest(req2, new MediaDataController$$ExternalSyntheticLambda35(this, dialogId, replyMessageOwners, scheduled, callback));
                    }
                    a2 = a + 1;
                    long j10 = dialogId;
                    result2 = result;
                    users2 = users;
                    N3 = N;
                    chats2 = chats;
                }
                int i2 = N3;
                ArrayList<TLRPC.User> arrayList3 = users2;
                ArrayList<TLRPC.Chat> arrayList4 = chats2;
                int i3 = a2;
                return;
            }
            ArrayList<TLRPC.User> arrayList5 = users2;
            ArrayList<TLRPC.Chat> arrayList6 = chats2;
            if (callback != null) {
                AndroidUtilities.runOnUIThread(callback);
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$115$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m838x96CLASSNAMEc5e(long dialogId, long channelId, LongSparseArray replyMessageOwners, boolean scheduled, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            for (int i = 0; i < messagesRes.messages.size(); i++) {
                TLRPC.Message message = messagesRes.messages.get(i);
                if (message.dialog_id == 0) {
                    message.dialog_id = dialogId;
                } else {
                    long j = dialogId;
                }
            }
            long j2 = dialogId;
            MessageObject.fixMessagePeer(messagesRes.messages, channelId);
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
        } else {
            long j3 = dialogId;
            long j4 = channelId;
            LongSparseArray longSparseArray = replyMessageOwners;
            boolean z = scheduled;
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    /* renamed from: lambda$loadReplyMessagesForMessages$116$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m839x59acd5bd(long dialogId, LongSparseArray replyMessageOwners, boolean scheduled, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            for (int i = 0; i < messagesRes.messages.size(); i++) {
                TLRPC.Message message = messagesRes.messages.get(i);
                if (message.dialog_id == 0) {
                    message.dialog_id = dialogId;
                } else {
                    long j = dialogId;
                }
            }
            long j2 = dialogId;
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, dialogId, false);
            getMessagesStorage().putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
            saveReplyMessages(replyMessageOwners, messagesRes.messages, scheduled);
        } else {
            long j3 = dialogId;
            LongSparseArray longSparseArray = replyMessageOwners;
            boolean z = scheduled;
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    private void saveReplyMessages(LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners, ArrayList<TLRPC.Message> result, boolean scheduled) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda3(this, scheduled, result, replyMessageOwners));
    }

    /* renamed from: lambda$saveReplyMessages$118$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m890xa1b050c1(boolean scheduled, ArrayList result, LongSparseArray replyMessageOwners) {
        SQLitePreparedStatement state;
        TLRPC.Message message;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            if (scheduled) {
                state = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            } else {
                state = getMessagesStorage().getDatabase().executeFast("UPDATE messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            }
            int a = 0;
            while (a < result.size()) {
                try {
                    message = (TLRPC.Message) result.get(a);
                } catch (Exception e) {
                    e = e;
                    LongSparseArray longSparseArray = replyMessageOwners;
                    FileLog.e((Throwable) e);
                }
                try {
                    SparseArray<ArrayList<MessageObject>> sparseArray = (SparseArray) replyMessageOwners.get(MessageObject.getDialogId(message));
                    if (sparseArray != null) {
                        ArrayList<MessageObject> messageObjects = sparseArray.get(message.id);
                        if (messageObjects != null) {
                            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(data);
                            for (int b = 0; b < messageObjects.size(); b++) {
                                MessageObject messageObject = messageObjects.get(b);
                                state.requery();
                                state.bindByteBuffer(1, data);
                                state.bindInteger(2, message.id);
                                state.bindInteger(3, messageObject.getId());
                                state.bindLong(4, messageObject.getDialogId());
                                state.step();
                            }
                            data.reuse();
                        }
                    }
                    a++;
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e((Throwable) e);
                }
            }
            ArrayList arrayList = result;
            LongSparseArray longSparseArray2 = replyMessageOwners;
            state.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e3) {
            e = e3;
            ArrayList arrayList2 = result;
            LongSparseArray longSparseArray3 = replyMessageOwners;
            FileLog.e((Throwable) e);
        }
    }

    private void broadcastReplyMessages(ArrayList<TLRPC.Message> result, LongSparseArray<SparseArray<ArrayList<MessageObject>>> replyMessageOwners, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, long dialog_id, boolean isCache) {
        LongSparseArray<TLRPC.User> usersDict = new LongSparseArray<>();
        for (int a = 0; a < users.size(); a++) {
            TLRPC.User user = users.get(a);
            usersDict.put(user.id, user);
        }
        ArrayList<TLRPC.User> arrayList = users;
        LongSparseArray<TLRPC.Chat> chatsDict = new LongSparseArray<>();
        for (int a2 = 0; a2 < chats.size(); a2++) {
            TLRPC.Chat chat = chats.get(a2);
            chatsDict.put(chat.id, chat);
        }
        ArrayList<TLRPC.Chat> arrayList2 = chats;
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        int N = result.size();
        for (int a3 = 0; a3 < N; a3++) {
            messageObjects.add(new MessageObject(this.currentAccount, result.get(a3), usersDict, chatsDict, false, false));
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda114(this, users, isCache, chats, messageObjects, replyMessageOwners, dialog_id));
    }

    /* renamed from: lambda$broadcastReplyMessages$119$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m779x7887470a(ArrayList users, boolean isCache, ArrayList chats, ArrayList messageObjects, LongSparseArray replyMessageOwners, long dialog_id) {
        ArrayList<MessageObject> arrayList;
        boolean z = isCache;
        ArrayList arrayList2 = messageObjects;
        LongSparseArray longSparseArray = replyMessageOwners;
        getMessagesController().putUsers(users, z);
        getMessagesController().putChats(chats, z);
        boolean changed = false;
        int a = 0;
        int N = messageObjects.size();
        while (a < N) {
            MessageObject messageObject = (MessageObject) arrayList2.get(a);
            SparseArray<ArrayList<MessageObject>> sparseArray = (SparseArray) longSparseArray.get(messageObject.getDialogId());
            if (!(sparseArray == null || (arrayList = sparseArray.get(messageObject.getId())) == null)) {
                int b = 0;
                while (b < arrayList.size()) {
                    MessageObject m = arrayList.get(b);
                    m.replyMessageObject = messageObject;
                    if (m.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                        m.generatePinMessageText((TLRPC.User) null, (TLRPC.Chat) null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                        m.generateGameMessageText((TLRPC.User) null);
                    } else if (m.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent) {
                        m.generatePaymentSentMessageText((TLRPC.User) null);
                    }
                    b++;
                    boolean z2 = isCache;
                }
                changed = true;
            }
            a++;
            boolean z3 = isCache;
        }
        if (changed) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(dialog_id), arrayList2, longSparseArray);
        }
    }

    public static void sortEntities(ArrayList<TLRPC.MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    private static boolean checkInclusion(int index, ArrayList<TLRPC.MessageEntity> entities, boolean end) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            int i = entity.offset;
            if (end) {
                if (i >= index) {
                    continue;
                }
            } else if (i > index) {
                continue;
            }
            if (entity.offset + entity.length > index) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int start, int end, ArrayList<TLRPC.MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return false;
        }
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start && entity.offset + entity.length <= end) {
                return true;
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int start, int countToRemove, ArrayList<TLRPC.MessageEntity> entities) {
        int count = entities.size();
        for (int a = 0; a < count; a++) {
            TLRPC.MessageEntity entity = entities.get(a);
            if (entity.offset > start) {
                entity.offset -= countToRemove;
            }
        }
    }

    public CharSequence substring(CharSequence source, int start, int end) {
        if (source instanceof SpannableStringBuilder) {
            return source.subSequence(start, end);
        }
        if (source instanceof SpannedString) {
            return source.subSequence(start, end);
        }
        return TextUtils.substring(source, start, end);
    }

    private static CharacterStyle createNewSpan(CharacterStyle baseSpan, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun newStyleRun, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (newStyleRun != null) {
            if (allowIntersection) {
                run.merge(newStyleRun);
            } else {
                run.replace(newStyleRun);
            }
        }
        if (baseSpan instanceof TextStyleSpan) {
            return new TextStyleSpan(run);
        }
        if (baseSpan instanceof URLSpanReplacement) {
            return new URLSpanReplacement(((URLSpanReplacement) baseSpan).getURL(), run);
        }
        return null;
    }

    public static void addStyleToText(TextStyleSpan span, int start, int end, Spannable editable, boolean allowIntersection) {
        TextStyleSpan.TextStyleRun textStyleRun;
        TextStyleSpan textStyleSpan = span;
        Spannable spannable = editable;
        boolean z = allowIntersection;
        int start2 = start;
        int end2 = end;
        try {
            CharacterStyle[] spans = (CharacterStyle[]) spannable.getSpans(start2, end2, CharacterStyle.class);
            if (spans != null && spans.length > 0) {
                int a = 0;
                while (a < spans.length) {
                    try {
                        CharacterStyle oldSpan = spans[a];
                        TextStyleSpan.TextStyleRun newStyleRun = textStyleSpan != null ? span.getTextStyleRun() : new TextStyleSpan.TextStyleRun();
                        if (oldSpan instanceof TextStyleSpan) {
                            textStyleRun = ((TextStyleSpan) oldSpan).getTextStyleRun();
                        } else if (!(oldSpan instanceof URLSpanReplacement)) {
                            a++;
                        } else {
                            TextStyleSpan.TextStyleRun textStyleRun2 = ((URLSpanReplacement) oldSpan).getTextStyleRun();
                            if (textStyleRun2 == null) {
                                textStyleRun = new TextStyleSpan.TextStyleRun();
                            } else {
                                textStyleRun = textStyleRun2;
                            }
                        }
                        if (textStyleRun != null) {
                            int spanStart = spannable.getSpanStart(oldSpan);
                            int spanEnd = spannable.getSpanEnd(oldSpan);
                            spannable.removeSpan(oldSpan);
                            if (spanStart <= start2 || end2 <= spanEnd) {
                                int startTemp = start2;
                                if (spanStart <= start2) {
                                    if (spanStart != start2) {
                                        spannable.setSpan(createNewSpan(oldSpan, textStyleRun, (TextStyleSpan.TextStyleRun) null, z), spanStart, start2, 33);
                                    }
                                    if (spanEnd > start2) {
                                        if (textStyleSpan != null) {
                                            spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), start2, Math.min(spanEnd, end2), 33);
                                        }
                                        start2 = spanEnd;
                                    }
                                }
                                if (spanEnd >= end2) {
                                    if (spanEnd != end2) {
                                        spannable.setSpan(createNewSpan(oldSpan, textStyleRun, (TextStyleSpan.TextStyleRun) null, z), end2, spanEnd, 33);
                                    }
                                    if (end2 > spanStart && spanEnd <= startTemp) {
                                        if (textStyleSpan != null) {
                                            spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), spanStart, Math.min(spanEnd, end2), 33);
                                        }
                                        end2 = spanStart;
                                    }
                                }
                            } else {
                                spannable.setSpan(createNewSpan(oldSpan, textStyleRun, newStyleRun, z), spanStart, spanEnd, 33);
                                if (textStyleSpan != null) {
                                    spannable.setSpan(new TextStyleSpan(new TextStyleSpan.TextStyleRun(newStyleRun)), spanEnd, end2, 33);
                                }
                                end2 = spanStart;
                            }
                        }
                        a++;
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                }
            }
            if (textStyleSpan != null && start2 < end2) {
                spannable.setSpan(textStyleSpan, start2, end2, 33);
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC.MessageEntity> entities, CharSequence text) {
        int b;
        ArrayList<TextStyleSpan.TextStyleRun> runs = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesCopy = new ArrayList<>(entities);
        Collections.sort(entitiesCopy, MediaDataController$$ExternalSyntheticLambda12.INSTANCE);
        int N = entitiesCopy.size();
        for (int a = 0; a < N; a++) {
            TLRPC.MessageEntity entity = entitiesCopy.get(a);
            if (entity.length > 0 && entity.offset >= 0 && entity.offset < text.length()) {
                if (entity.offset + entity.length > text.length()) {
                    entity.length = text.length() - entity.offset;
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity.offset;
                newRun.end = newRun.start + entity.length;
                if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    newRun.flags = 64;
                    newRun.urlEntity = entity;
                } else {
                    newRun.flags = 128;
                    newRun.urlEntity = entity;
                }
                int b2 = 0;
                int N2 = runs.size();
                while (b < N2) {
                    TextStyleSpan.TextStyleRun run = runs.get(b);
                    if (newRun.start > run.start) {
                        if (newRun.start < run.end) {
                            if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                r.merge(run);
                                int b3 = b + 1;
                                runs.add(b3, r);
                                TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                r2.start = newRun.end;
                                b = b3 + 1;
                                N2 = N2 + 1 + 1;
                                runs.add(b, r2);
                            } else {
                                TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                r3.merge(run);
                                r3.end = run.end;
                                b++;
                                N2++;
                                runs.add(b, r3);
                            }
                            int temp = newRun.start;
                            newRun.start = run.end;
                            run.end = temp;
                        }
                    } else if (run.start < newRun.end) {
                        int temp2 = run.start;
                        if (newRun.end == run.end) {
                            run.merge(newRun);
                        } else if (newRun.end < run.end) {
                            TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                            r4.merge(newRun);
                            r4.end = newRun.end;
                            b++;
                            N2++;
                            runs.add(b, r4);
                            run.start = newRun.end;
                        } else {
                            TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                            r5.start = run.end;
                            b++;
                            N2++;
                            runs.add(b, r5);
                            run.merge(newRun);
                        }
                        newRun.end = temp2;
                    }
                    b2 = b + 1;
                }
                if (newRun.start < newRun.end) {
                    runs.add(newRun);
                }
            }
        }
        return runs;
    }

    static /* synthetic */ int lambda$getTextStyleRuns$120(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    public void addStyle(int flags, int spanStart, int spanEnd, ArrayList<TLRPC.MessageEntity> entities) {
        if ((flags & 1) != 0) {
            TLRPC.MessageEntity entity = new TLRPC.TL_messageEntityBold();
            entity.offset = spanStart;
            entity.length = spanEnd - spanStart;
            entities.add(entity);
        }
        if ((flags & 2) != 0) {
            TLRPC.MessageEntity entity2 = new TLRPC.TL_messageEntityItalic();
            entity2.offset = spanStart;
            entity2.length = spanEnd - spanStart;
            entities.add(entity2);
        }
        if ((flags & 4) != 0) {
            TLRPC.MessageEntity entity3 = new TLRPC.TL_messageEntityCode();
            entity3.offset = spanStart;
            entity3.length = spanEnd - spanStart;
            entities.add(entity3);
        }
        if ((flags & 8) != 0) {
            TLRPC.MessageEntity entity4 = new TLRPC.TL_messageEntityStrike();
            entity4.offset = spanStart;
            entity4.length = spanEnd - spanStart;
            entities.add(entity4);
        }
        if ((flags & 16) != 0) {
            TLRPC.MessageEntity entity5 = new TLRPC.TL_messageEntityUnderline();
            entity5.offset = spanStart;
            entity5.length = spanEnd - spanStart;
            entities.add(entity5);
        }
        if ((flags & 32) != 0) {
            TLRPC.MessageEntity entity6 = new TLRPC.TL_messageEntityBlockquote();
            entity6.offset = spanStart;
            entity6.length = spanEnd - spanStart;
            entities.add(entity6);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0394, code lost:
        if (r13 == 10) goto L_0x0399;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> getEntities(java.lang.CharSequence[] r27, boolean r28) {
        /*
            r26 = this;
            r1 = r26
            if (r27 == 0) goto L_0x049d
            r2 = 0
            r0 = r27[r2]
            if (r0 != 0) goto L_0x000b
            goto L_0x049d
        L_0x000b:
            r0 = 0
            r3 = -1
            r4 = 0
            r5 = 0
            java.lang.String r6 = "`"
            java.lang.String r7 = "```"
            java.lang.String r8 = "**"
            java.lang.String r9 = "__"
            java.lang.String r10 = "~~"
        L_0x001a:
            r11 = r27[r2]
            if (r5 != 0) goto L_0x0021
            java.lang.String r12 = "`"
            goto L_0x0023
        L_0x0021:
            java.lang.String r12 = "```"
        L_0x0023:
            int r11 = android.text.TextUtils.indexOf(r11, r12, r4)
            r12 = r11
            r15 = -1
            r13 = 2
            if (r11 == r15) goto L_0x01b8
            r11 = 96
            if (r3 != r15) goto L_0x005a
            r15 = r27[r2]
            int r15 = r15.length()
            int r15 = r15 - r12
            if (r15 <= r13) goto L_0x004f
            r13 = r27[r2]
            int r15 = r12 + 1
            char r13 = r13.charAt(r15)
            if (r13 != r11) goto L_0x004f
            r13 = r27[r2]
            int r15 = r12 + 2
            char r13 = r13.charAt(r15)
            if (r13 != r11) goto L_0x004f
            r11 = 1
            goto L_0x0050
        L_0x004f:
            r11 = 0
        L_0x0050:
            r5 = r11
            r3 = r12
            if (r5 == 0) goto L_0x0056
            r13 = 3
            goto L_0x0057
        L_0x0056:
            r13 = 1
        L_0x0057:
            int r4 = r12 + r13
            goto L_0x001a
        L_0x005a:
            if (r0 != 0) goto L_0x0062
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r0 = r15
        L_0x0062:
            if (r5 == 0) goto L_0x0066
            r15 = 3
            goto L_0x0067
        L_0x0066:
            r15 = 1
        L_0x0067:
            int r15 = r15 + r12
        L_0x0068:
            r18 = r27[r2]
            int r14 = r18.length()
            if (r15 >= r14) goto L_0x007d
            r14 = r27[r2]
            char r14 = r14.charAt(r15)
            if (r14 != r11) goto L_0x007d
            int r12 = r12 + 1
            int r15 = r15 + 1
            goto L_0x0068
        L_0x007d:
            if (r5 == 0) goto L_0x0081
            r11 = 3
            goto L_0x0082
        L_0x0081:
            r11 = 1
        L_0x0082:
            int r11 = r11 + r12
            if (r5 == 0) goto L_0x0160
            if (r3 <= 0) goto L_0x0090
            r4 = r27[r2]
            int r14 = r3 + -1
            char r4 = r4.charAt(r14)
            goto L_0x0091
        L_0x0090:
            r4 = 0
        L_0x0091:
            r14 = 32
            if (r4 == r14) goto L_0x009c
            r14 = 10
            if (r4 != r14) goto L_0x009a
            goto L_0x009c
        L_0x009a:
            r14 = 0
            goto L_0x009d
        L_0x009c:
            r14 = 1
        L_0x009d:
            r15 = r27[r2]
            if (r14 == 0) goto L_0x00a4
            r18 = 1
            goto L_0x00a6
        L_0x00a4:
            r18 = 0
        L_0x00a6:
            int r13 = r3 - r18
            java.lang.CharSequence r13 = r1.substring(r15, r2, r13)
            r15 = r27[r2]
            int r2 = r3 + 3
            java.lang.CharSequence r2 = r1.substring(r15, r2, r12)
            int r15 = r12 + 3
            r18 = 0
            r20 = r27[r18]
            r21 = r4
            int r4 = r20.length()
            if (r15 >= r4) goto L_0x00cb
            r4 = r27[r18]
            int r15 = r12 + 3
            char r4 = r4.charAt(r15)
            goto L_0x00cc
        L_0x00cb:
            r4 = 0
        L_0x00cc:
            r15 = r27[r18]
            int r20 = r12 + 3
            r21 = r6
            r6 = 32
            if (r4 == r6) goto L_0x00dd
            r6 = 10
            if (r4 != r6) goto L_0x00db
            goto L_0x00dd
        L_0x00db:
            r6 = 0
            goto L_0x00de
        L_0x00dd:
            r6 = 1
        L_0x00de:
            int r6 = r20 + r6
            r16 = 0
            r17 = r27[r16]
            r20 = r4
            int r4 = r17.length()
            java.lang.CharSequence r4 = r1.substring(r15, r6, r4)
            int r6 = r13.length()
            java.lang.String r15 = "\n"
            if (r6 == 0) goto L_0x0105
            r22 = r7
            r6 = 2
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r6]
            r7[r16] = r13
            r6 = 1
            r7[r6] = r15
            java.lang.CharSequence r13 = org.telegram.messenger.AndroidUtilities.concat(r7)
            goto L_0x0108
        L_0x0105:
            r22 = r7
            r14 = 1
        L_0x0108:
            int r6 = r4.length()
            if (r6 == 0) goto L_0x011c
            r6 = 2
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r6]
            r6 = 0
            r7[r6] = r15
            r15 = 1
            r7[r15] = r4
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.concat(r7)
            goto L_0x011e
        L_0x011c:
            r6 = 0
            r15 = 1
        L_0x011e:
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x015c
            r23 = r8
            r7 = 3
            java.lang.CharSequence[] r8 = new java.lang.CharSequence[r7]
            r8[r6] = r13
            r8[r15] = r2
            r7 = 2
            r8[r7] = r4
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.concat(r8)
            r27[r6] = r7
            org.telegram.tgnet.TLRPC$TL_messageEntityPre r6 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre
            r6.<init>()
            if (r14 == 0) goto L_0x013f
            r7 = 0
            goto L_0x0140
        L_0x013f:
            r7 = 1
        L_0x0140:
            int r7 = r7 + r3
            r6.offset = r7
            int r7 = r12 - r3
            r8 = 3
            int r7 = r7 - r8
            if (r14 == 0) goto L_0x014c
            r19 = 0
            goto L_0x014e
        L_0x014c:
            r19 = 1
        L_0x014e:
            int r7 = r7 + r19
            r6.length = r7
            java.lang.String r7 = ""
            r6.language = r7
            r0.add(r6)
            int r11 = r11 + -6
            goto L_0x015e
        L_0x015c:
            r23 = r8
        L_0x015e:
            r4 = r11
            goto L_0x01ad
        L_0x0160:
            r21 = r6
            r22 = r7
            r23 = r8
            int r2 = r3 + 1
            if (r2 == r12) goto L_0x01ac
            r2 = 3
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r2]
            r4 = 0
            r6 = r27[r4]
            java.lang.CharSequence r6 = r1.substring(r6, r4, r3)
            r2[r4] = r6
            r6 = r27[r4]
            int r7 = r3 + 1
            java.lang.CharSequence r6 = r1.substring(r6, r7, r12)
            r7 = 1
            r2[r7] = r6
            r6 = r27[r4]
            int r7 = r12 + 1
            r8 = r27[r4]
            int r8 = r8.length()
            java.lang.CharSequence r6 = r1.substring(r6, r7, r8)
            r7 = 2
            r2[r7] = r6
            java.lang.CharSequence r2 = org.telegram.messenger.AndroidUtilities.concat(r2)
            r27[r4] = r2
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r2.<init>()
            r2.offset = r3
            int r4 = r12 - r3
            r6 = 1
            int r4 = r4 - r6
            r2.length = r4
            r0.add(r2)
            int r11 = r11 + -2
            r4 = r11
            goto L_0x01ad
        L_0x01ac:
            r4 = r11
        L_0x01ad:
            r3 = -1
            r5 = 0
            r6 = r21
            r7 = r22
            r8 = r23
            r2 = 0
            goto L_0x001a
        L_0x01b8:
            r21 = r6
            r22 = r7
            r23 = r8
            if (r3 == r15) goto L_0x01fa
            if (r5 == 0) goto L_0x01fa
            r2 = 2
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r2]
            r2 = 0
            r7 = r27[r2]
            java.lang.CharSequence r7 = r1.substring(r7, r2, r3)
            r6[r2] = r7
            r7 = r27[r2]
            int r8 = r3 + 2
            r11 = r27[r2]
            int r11 = r11.length()
            java.lang.CharSequence r7 = r1.substring(r7, r8, r11)
            r8 = 1
            r6[r8] = r7
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.concat(r6)
            r27[r2] = r6
            if (r0 != 0) goto L_0x01ed
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r2
        L_0x01ed:
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r2.<init>()
            r2.offset = r3
            r6 = 1
            r2.length = r6
            r0.add(r2)
        L_0x01fa:
            r2 = 0
            r6 = r27[r2]
            boolean r6 = r6 instanceof android.text.Spanned
            if (r6 == 0) goto L_0x034f
            r6 = r27[r2]
            android.text.Spanned r6 = (android.text.Spanned) r6
            r7 = r27[r2]
            int r7 = r7.length()
            java.lang.Class<org.telegram.ui.Components.TextStyleSpan> r8 = org.telegram.ui.Components.TextStyleSpan.class
            java.lang.Object[] r7 = r6.getSpans(r2, r7, r8)
            r2 = r7
            org.telegram.ui.Components.TextStyleSpan[] r2 = (org.telegram.ui.Components.TextStyleSpan[]) r2
            if (r2 == 0) goto L_0x024e
            int r7 = r2.length
            if (r7 <= 0) goto L_0x024e
            r7 = 0
        L_0x021a:
            int r8 = r2.length
            if (r7 >= r8) goto L_0x024e
            r8 = r2[r7]
            int r11 = r6.getSpanStart(r8)
            int r13 = r6.getSpanEnd(r8)
            r14 = 0
            boolean r20 = checkInclusion(r11, r0, r14)
            if (r20 != 0) goto L_0x024b
            r14 = 1
            boolean r20 = checkInclusion(r13, r0, r14)
            if (r20 != 0) goto L_0x024b
            boolean r14 = checkIntersection(r11, r13, r0)
            if (r14 == 0) goto L_0x023c
            goto L_0x024b
        L_0x023c:
            if (r0 != 0) goto L_0x0244
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            r0 = r14
        L_0x0244:
            int r14 = r8.getStyleFlags()
            r1.addStyle(r14, r11, r13, r0)
        L_0x024b:
            int r7 = r7 + 1
            goto L_0x021a
        L_0x024e:
            r7 = 0
            r8 = r27[r7]
            int r8 = r8.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanUserMention> r11 = org.telegram.ui.Components.URLSpanUserMention.class
            java.lang.Object[] r8 = r6.getSpans(r7, r8, r11)
            r7 = r8
            org.telegram.ui.Components.URLSpanUserMention[] r7 = (org.telegram.ui.Components.URLSpanUserMention[]) r7
            if (r7 == 0) goto L_0x02d7
            int r8 = r7.length
            if (r8 <= 0) goto L_0x02d7
            if (r0 != 0) goto L_0x026b
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0 = r8
        L_0x026b:
            r8 = 0
        L_0x026c:
            int r11 = r7.length
            if (r8 >= r11) goto L_0x02d2
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r11 = new org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            r11.<init>()
            org.telegram.messenger.MessagesController r13 = r26.getMessagesController()
            r14 = r7[r8]
            java.lang.String r14 = r14.getURL()
            java.lang.Long r14 = org.telegram.messenger.Utilities.parseLong(r14)
            r24 = r2
            r20 = r3
            long r2 = r14.longValue()
            org.telegram.tgnet.TLRPC$InputUser r2 = r13.getInputUser((long) r2)
            r11.user_id = r2
            org.telegram.tgnet.TLRPC$InputUser r2 = r11.user_id
            if (r2 == 0) goto L_0x02cb
            r2 = r7[r8]
            int r2 = r6.getSpanStart(r2)
            r11.offset = r2
            r2 = r7[r8]
            int r2 = r6.getSpanEnd(r2)
            r3 = 0
            r13 = r27[r3]
            int r13 = r13.length()
            int r2 = java.lang.Math.min(r2, r13)
            int r13 = r11.offset
            int r2 = r2 - r13
            r11.length = r2
            r2 = r27[r3]
            int r3 = r11.offset
            int r13 = r11.length
            int r3 = r3 + r13
            r13 = 1
            int r3 = r3 - r13
            char r2 = r2.charAt(r3)
            r3 = 32
            if (r2 != r3) goto L_0x02c8
            int r2 = r11.length
            int r2 = r2 - r13
            r11.length = r2
        L_0x02c8:
            r0.add(r11)
        L_0x02cb:
            int r8 = r8 + 1
            r3 = r20
            r2 = r24
            goto L_0x026c
        L_0x02d2:
            r24 = r2
            r20 = r3
            goto L_0x02db
        L_0x02d7:
            r24 = r2
            r20 = r3
        L_0x02db:
            r2 = 0
            r3 = r27[r2]
            int r3 = r3.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanReplacement> r8 = org.telegram.ui.Components.URLSpanReplacement.class
            java.lang.Object[] r3 = r6.getSpans(r2, r3, r8)
            r2 = r3
            org.telegram.ui.Components.URLSpanReplacement[] r2 = (org.telegram.ui.Components.URLSpanReplacement[]) r2
            if (r2 == 0) goto L_0x034c
            int r3 = r2.length
            if (r3 <= 0) goto L_0x034c
            if (r0 != 0) goto L_0x02f8
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = r3
        L_0x02f8:
            r3 = 0
        L_0x02f9:
            int r8 = r2.length
            if (r3 >= r8) goto L_0x0349
            org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl r8 = new org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            r8.<init>()
            r11 = r2[r3]
            int r11 = r6.getSpanStart(r11)
            r8.offset = r11
            r11 = r2[r3]
            int r11 = r6.getSpanEnd(r11)
            r13 = 0
            r14 = r27[r13]
            int r13 = r14.length()
            int r11 = java.lang.Math.min(r11, r13)
            int r13 = r8.offset
            int r11 = r11 - r13
            r8.length = r11
            r11 = r2[r3]
            java.lang.String r11 = r11.getURL()
            r8.url = r11
            r0.add(r8)
            r11 = r2[r3]
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r11 = r11.getTextStyleRun()
            if (r11 == 0) goto L_0x0341
            int r13 = r11.flags
            int r14 = r8.offset
            int r15 = r8.offset
            r25 = r2
            int r2 = r8.length
            int r15 = r15 + r2
            r1.addStyle(r13, r14, r15, r0)
            goto L_0x0343
        L_0x0341:
            r25 = r2
        L_0x0343:
            int r3 = r3 + 1
            r2 = r25
            r15 = -1
            goto L_0x02f9
        L_0x0349:
            r25 = r2
            goto L_0x0351
        L_0x034c:
            r25 = r2
            goto L_0x0351
        L_0x034f:
            r20 = r3
        L_0x0351:
            if (r28 == 0) goto L_0x0355
            r2 = 3
            goto L_0x0356
        L_0x0355:
            r2 = 2
        L_0x0356:
            r3 = 0
        L_0x0357:
            if (r3 >= r2) goto L_0x049c
            r4 = 0
            r6 = -1
            switch(r3) {
                case 0: goto L_0x0369;
                case 1: goto L_0x0364;
                default: goto L_0x035e;
            }
        L_0x035e:
            java.lang.String r7 = "~~"
            r8 = 126(0x7e, float:1.77E-43)
            goto L_0x036e
        L_0x0364:
            java.lang.String r7 = "__"
            r8 = 95
            goto L_0x036e
        L_0x0369:
            java.lang.String r7 = "**"
            r8 = 42
        L_0x036e:
            r11 = 0
            r13 = r27[r11]
            int r13 = android.text.TextUtils.indexOf(r13, r7, r4)
            r12 = r13
            r14 = -1
            if (r13 == r14) goto L_0x0493
            if (r6 != r14) goto L_0x03a2
            if (r12 != 0) goto L_0x0380
            r13 = 32
            goto L_0x0388
        L_0x0380:
            r13 = r27[r11]
            int r15 = r12 + -1
            char r13 = r13.charAt(r15)
        L_0x0388:
            boolean r15 = checkInclusion(r12, r0, r11)
            if (r15 != 0) goto L_0x039b
            r11 = 32
            if (r13 == r11) goto L_0x0397
            r15 = 10
            if (r13 != r15) goto L_0x039f
            goto L_0x0399
        L_0x0397:
            r15 = 10
        L_0x0399:
            r6 = r12
            goto L_0x039f
        L_0x039b:
            r11 = 32
            r15 = 10
        L_0x039f:
            int r4 = r12 + 2
            goto L_0x036e
        L_0x03a2:
            r11 = 32
            r15 = 10
            int r13 = r12 + 2
        L_0x03a8:
            r16 = 0
            r17 = r27[r16]
            int r11 = r17.length()
            if (r13 >= r11) goto L_0x03c1
            r11 = r27[r16]
            char r11 = r11.charAt(r13)
            if (r11 != r8) goto L_0x03c1
            int r12 = r12 + 1
            int r13 = r13 + 1
            r11 = 32
            goto L_0x03a8
        L_0x03c1:
            int r4 = r12 + 2
            r11 = 0
            boolean r13 = checkInclusion(r12, r0, r11)
            if (r13 != 0) goto L_0x048d
            boolean r11 = checkIntersection(r6, r12, r0)
            if (r11 == 0) goto L_0x03d5
            r14 = 1
            r17 = 2
            goto L_0x0490
        L_0x03d5:
            int r11 = r6 + 2
            if (r11 == r12) goto L_0x0487
            if (r0 != 0) goto L_0x03e2
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0 = r11
            goto L_0x03e3
        L_0x03e2:
            r11 = r0
        L_0x03e3:
            r13 = 3
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r13]     // Catch:{ Exception -> 0x0414 }
            r13 = 0
            r14 = r27[r13]     // Catch:{ Exception -> 0x0414 }
            java.lang.CharSequence r14 = r1.substring(r14, r13, r6)     // Catch:{ Exception -> 0x0414 }
            r0[r13] = r14     // Catch:{ Exception -> 0x0414 }
            r14 = r27[r13]     // Catch:{ Exception -> 0x0414 }
            int r15 = r6 + 2
            java.lang.CharSequence r14 = r1.substring(r14, r15, r12)     // Catch:{ Exception -> 0x0414 }
            r15 = 1
            r0[r15] = r14     // Catch:{ Exception -> 0x0414 }
            r14 = r27[r13]     // Catch:{ Exception -> 0x0414 }
            int r15 = r12 + 2
            r17 = r27[r13]     // Catch:{ Exception -> 0x0414 }
            int r13 = r17.length()     // Catch:{ Exception -> 0x0414 }
            java.lang.CharSequence r13 = r1.substring(r14, r15, r13)     // Catch:{ Exception -> 0x0414 }
            r14 = 2
            r0[r14] = r13     // Catch:{ Exception -> 0x0414 }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.concat(r0)     // Catch:{ Exception -> 0x0414 }
            r13 = 0
            r27[r13] = r0     // Catch:{ Exception -> 0x0414 }
            r13 = 0
            goto L_0x0456
        L_0x0414:
            r0 = move-exception
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r14 = 0
            r15 = r27[r14]
            java.lang.CharSequence r15 = r1.substring(r15, r14, r6)
            java.lang.String r15 = r15.toString()
            r13.append(r15)
            r15 = r27[r14]
            int r14 = r6 + 2
            java.lang.CharSequence r14 = r1.substring(r15, r14, r12)
            java.lang.String r14 = r14.toString()
            r13.append(r14)
            r14 = 0
            r15 = r27[r14]
            r17 = r0
            int r0 = r12 + 2
            r18 = r27[r14]
            int r14 = r18.length()
            java.lang.CharSequence r0 = r1.substring(r15, r0, r14)
            java.lang.String r0 = r0.toString()
            r13.append(r0)
            java.lang.String r0 = r13.toString()
            r13 = 0
            r27[r13] = r0
        L_0x0456:
            if (r3 != 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$TL_messageEntityBold r0 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold
            r0.<init>()
            r14 = 1
            goto L_0x046d
        L_0x045f:
            r14 = 1
            if (r3 != r14) goto L_0x0468
            org.telegram.tgnet.TLRPC$TL_messageEntityItalic r0 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            r0.<init>()
            goto L_0x046d
        L_0x0468:
            org.telegram.tgnet.TLRPC$TL_messageEntityStrike r0 = new org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            r0.<init>()
        L_0x046d:
            r0.offset = r6
            int r15 = r12 - r6
            r17 = 2
            int r15 = r15 + -2
            r0.length = r15
            int r15 = r0.offset
            int r13 = r0.length
            int r15 = r15 + r13
            r13 = 4
            removeOffsetAfter(r15, r13, r11)
            r11.add(r0)
            int r4 = r4 + -4
            r0 = r11
            goto L_0x048a
        L_0x0487:
            r14 = 1
            r17 = 2
        L_0x048a:
            r6 = -1
            goto L_0x036e
        L_0x048d:
            r14 = 1
            r17 = 2
        L_0x0490:
            r6 = -1
            goto L_0x036e
        L_0x0493:
            r14 = 1
            r17 = 2
            int r3 = r3 + 1
            r20 = r6
            goto L_0x0357
        L_0x049c:
            return r0
        L_0x049d:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[], boolean):java.util.ArrayList");
    }

    public void loadDraftsIfNeed() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getAllDrafts(), new MediaDataController$$ExternalSyntheticLambda16(this));
        }
    }

    /* renamed from: lambda$loadDraftsIfNeed$123$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m809x99a652fb(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda83(this));
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) response, false);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda94(this));
    }

    /* renamed from: lambda$loadDraftsIfNeed$121$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m807x13cd803d() {
        this.loadingDrafts = false;
    }

    /* renamed from: lambda$loadDraftsIfNeed$122$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m808xd6b9e99c() {
        this.loadingDrafts = false;
        UserConfig userConfig = getUserConfig();
        userConfig.draftsLoaded = true;
        userConfig.saveConfig(false);
    }

    public int getDraftFolderId(long dialogId) {
        return this.draftsFolderIds.get(dialogId, 0).intValue();
    }

    public void setDraftFolderId(long dialogId, int folderId) {
        this.draftsFolderIds.put(dialogId, Integer.valueOf(folderId));
    }

    public void clearDraftsFolderIds() {
        this.draftsFolderIds.clear();
    }

    public LongSparseArray<SparseArray<TLRPC.DraftMessage>> getDrafts() {
        return this.drafts;
    }

    public TLRPC.DraftMessage getDraft(long dialogId, int threadId) {
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
        if (threads == null) {
            return null;
        }
        return threads.get(threadId);
    }

    public TLRPC.Message getDraftMessage(long dialogId, int threadId) {
        SparseArray<TLRPC.Message> threads = this.draftMessages.get(dialogId);
        if (threads == null) {
            return null;
        }
        return threads.get(threadId);
    }

    public void saveDraft(long dialogId, int threadId, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage) {
        saveDraft(dialogId, threadId, message, entities, replyToMessage, noWebpage, false);
    }

    public void saveDraft(long dialogId, int threadId, CharSequence message, ArrayList<TLRPC.MessageEntity> entities, TLRPC.Message replyToMessage, boolean noWebpage, boolean clean) {
        TLRPC.DraftMessage draftMessage;
        long j = dialogId;
        int i = threadId;
        ArrayList<TLRPC.MessageEntity> arrayList = entities;
        TLRPC.Message message2 = replyToMessage;
        if (!TextUtils.isEmpty(message) || message2 != null) {
            draftMessage = new TLRPC.TL_draftMessage();
        } else {
            draftMessage = new TLRPC.TL_draftMessageEmpty();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        draftMessage.message = message == null ? "" : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (message2 != null) {
            draftMessage.reply_to_msg_id = message2.id;
            draftMessage.flags |= 1;
        }
        if (arrayList != null && !entities.isEmpty()) {
            draftMessage.entities = arrayList;
            draftMessage.flags |= 8;
        }
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(j);
        TLRPC.DraftMessage currentDraft = threads == null ? null : threads.get(i);
        if (!clean) {
            if (currentDraft != null && currentDraft.message.equals(draftMessage.message) && currentDraft.reply_to_msg_id == draftMessage.reply_to_msg_id && currentDraft.no_webpage == draftMessage.no_webpage) {
                return;
            }
            if (currentDraft == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                return;
            }
        }
        TLRPC.DraftMessage draftMessage2 = currentDraft;
        saveDraft(dialogId, threadId, draftMessage, replyToMessage, false);
        if (i == 0) {
            if (!DialogObject.isEncryptedDialog(dialogId)) {
                TLRPC.TL_messages_saveDraft req = new TLRPC.TL_messages_saveDraft();
                req.peer = getMessagesController().getInputPeer(j);
                if (req.peer != null) {
                    req.message = draftMessage.message;
                    req.no_webpage = draftMessage.no_webpage;
                    req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                    req.entities = draftMessage.entities;
                    req.flags = draftMessage.flags;
                    getConnectionsManager().sendRequest(req, MediaDataController$$ExternalSyntheticLambda54.INSTANCE);
                } else {
                    return;
                }
            }
            getMessagesController().sortDialogs((LongSparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    static /* synthetic */ void lambda$saveDraft$124(TLObject response, TLRPC.TL_error error) {
    }

    public void saveDraft(long dialogId, int threadId, TLRPC.DraftMessage draft, TLRPC.Message replyToMessage, boolean fromServer) {
        TLRPC.User user;
        TLRPC.Chat chat;
        String str;
        String str2;
        long j = dialogId;
        int i = threadId;
        TLRPC.DraftMessage draftMessage = draft;
        TLRPC.Message message = replyToMessage;
        SharedPreferences.Editor editor = this.draftPreferences.edit();
        MessagesController messagesController = getMessagesController();
        if (draftMessage == null || (draftMessage instanceof TLRPC.TL_draftMessageEmpty)) {
            SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(j);
            if (threads != null) {
                threads.remove(i);
                if (threads.size() == 0) {
                    this.drafts.remove(j);
                }
            }
            SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(j);
            if (threads2 != null) {
                threads2.remove(i);
                if (threads2.size() == 0) {
                    this.draftMessages.remove(j);
                }
            }
            if (i == 0) {
                this.draftPreferences.edit().remove("" + j).remove("r_" + j).commit();
            } else {
                this.draftPreferences.edit().remove("t_" + j + "_" + i).remove("rt_" + j + "_" + i).commit();
            }
            messagesController.removeDraftDialogIfNeed(j);
        } else {
            SparseArray<TLRPC.DraftMessage> threads3 = this.drafts.get(j);
            if (threads3 == null) {
                threads3 = new SparseArray<>();
                this.drafts.put(j, threads3);
            }
            threads3.put(i, draftMessage);
            if (i == 0) {
                messagesController.putDraftDialogIfNeed(j, draftMessage);
            }
            try {
                SerializedData serializedData = new SerializedData(draft.getObjectSize());
                draftMessage.serializeToStream(serializedData);
                if (i == 0) {
                    str2 = "" + j;
                } else {
                    str2 = "t_" + j + "_" + i;
                }
                editor.putString(str2, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        SparseArray<TLRPC.Message> threads4 = this.draftMessages.get(j);
        if (message == null) {
            if (threads4 != null) {
                threads4.remove(i);
                if (threads4.size() == 0) {
                    this.draftMessages.remove(j);
                }
            }
            if (i == 0) {
                editor.remove("r_" + j);
            } else {
                editor.remove("rt_" + j + "_" + i);
            }
        } else {
            if (threads4 == null) {
                threads4 = new SparseArray<>();
                this.draftMessages.put(j, threads4);
            }
            threads4.put(i, message);
            SerializedData serializedData2 = new SerializedData(replyToMessage.getObjectSize());
            message.serializeToStream(serializedData2);
            if (i == 0) {
                str = "r_" + j;
            } else {
                str = "rt_" + j + "_" + i;
            }
            editor.putString(str, Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        editor.commit();
        if (!fromServer || i != 0) {
            MessagesController messagesController2 = messagesController;
            return;
        }
        if (draftMessage == null || draftMessage.reply_to_msg_id == 0 || message != null) {
            MessagesController messagesController3 = messagesController;
        } else {
            if (DialogObject.isUserDialog(dialogId)) {
                user = getMessagesController().getUser(Long.valueOf(dialogId));
                chat = null;
            } else {
                user = null;
                chat = getMessagesController().getChat(Long.valueOf(-j));
            }
            if (user == null && chat == null) {
                SparseArray<TLRPC.Message> sparseArray = threads4;
                MessagesController messagesController4 = messagesController;
            } else {
                long channelId = ChatObject.isChannel(chat) ? chat.id : 0;
                int messageId = draftMessage.reply_to_msg_id;
                SparseArray<TLRPC.Message> sparseArray2 = threads4;
                MediaDataController$$ExternalSyntheticLambda55 mediaDataController$$ExternalSyntheticLambda55 = r1;
                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                TLRPC.Chat chat2 = chat;
                int i2 = messageId;
                MessagesController messagesController5 = messagesController;
                MediaDataController$$ExternalSyntheticLambda55 mediaDataController$$ExternalSyntheticLambda552 = new MediaDataController$$ExternalSyntheticLambda55(this, messageId, dialogId, channelId, threadId);
                storageQueue.postRunnable(mediaDataController$$ExternalSyntheticLambda55);
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(dialogId));
    }

    /* renamed from: lambda$saveDraft$127$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m886lambda$saveDraft$127$orgtelegrammessengerMediaDataController(int messageId, long dialogId, long channelId, int threadId) {
        NativeByteBuffer data;
        TLRPC.Message message = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d and uid = %d", new Object[]{Integer.valueOf(messageId), Long.valueOf(dialogId)}), new Object[0]);
            if (cursor.next() && (data = cursor.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                message.readAttachPath(data, getUserConfig().clientUserId);
                data.reuse();
            }
            cursor.dispose();
            if (message != null) {
                saveDraftReplyMessage(dialogId, threadId, message);
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
                req.channel = getMessagesController().getInputChannel(channelId);
                req.id.add(Integer.valueOf(messageId));
                getConnectionsManager().sendRequest(req, new MediaDataController$$ExternalSyntheticLambda28(this, dialogId, threadId));
            } else {
                TLRPC.TL_messages_getMessages req2 = new TLRPC.TL_messages_getMessages();
                req2.id.add(Integer.valueOf(messageId));
                getConnectionsManager().sendRequest(req2, new MediaDataController$$ExternalSyntheticLambda29(this, dialogId, threadId));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$saveDraft$125$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m884lambda$saveDraft$125$orgtelegrammessengerMediaDataController(long dialogId, int threadId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(dialogId, threadId, messagesRes.messages.get(0));
            }
        }
    }

    /* renamed from: lambda$saveDraft$126$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m885lambda$saveDraft$126$orgtelegrammessengerMediaDataController(long dialogId, int threadId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_Messages messagesRes = (TLRPC.messages_Messages) response;
            if (!messagesRes.messages.isEmpty()) {
                saveDraftReplyMessage(dialogId, threadId, messagesRes.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long dialogId, int threadId, TLRPC.Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda74(this, dialogId, threadId, message));
        }
    }

    /* renamed from: lambda$saveDraftReplyMessage$128$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m887x7522719a(long dialogId, int threadId, TLRPC.Message message) {
        StringBuilder sb;
        SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(dialogId);
        TLRPC.DraftMessage draftMessage = threads != null ? threads.get(threadId) : null;
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
            SparseArray<TLRPC.Message> threads2 = this.draftMessages.get(dialogId);
            if (threads2 == null) {
                threads2 = new SparseArray<>();
                this.draftMessages.put(dialogId, threads2);
            }
            threads2.put(threadId, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            SharedPreferences.Editor edit = this.draftPreferences.edit();
            if (threadId == 0) {
                sb.append("r_");
                sb.append(dialogId);
            } else {
                sb = new StringBuilder();
                sb.append("rt_");
                sb.append(dialogId);
                sb.append("_");
                sb.append(threadId);
            }
            edit.putString(sb.toString(), Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(dialogId));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts(boolean notify) {
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftsFolderIds.clear();
        this.draftPreferences.edit().clear().commit();
        if (notify) {
            getMessagesController().sortDialogs((LongSparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void cleanDraft(long dialogId, int threadId, boolean replyOnly) {
        long j = dialogId;
        int i = threadId;
        SparseArray<TLRPC.DraftMessage> threads2 = this.drafts.get(j);
        TLRPC.DraftMessage draftMessage = threads2 != null ? threads2.get(i) : null;
        if (draftMessage != null) {
            if (!replyOnly) {
                SparseArray<TLRPC.DraftMessage> threads = this.drafts.get(j);
                if (threads != null) {
                    threads.remove(i);
                    if (threads.size() == 0) {
                        this.drafts.remove(j);
                    }
                }
                SparseArray<TLRPC.Message> threads3 = this.draftMessages.get(j);
                if (threads3 != null) {
                    threads3.remove(i);
                    if (threads3.size() == 0) {
                        this.draftMessages.remove(j);
                    }
                }
                if (i == 0) {
                    this.draftPreferences.edit().remove("" + j).remove("r_" + j).commit();
                    getMessagesController().sortDialogs((LongSparseArray<TLRPC.Chat>) null);
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    return;
                }
                this.draftPreferences.edit().remove("t_" + j + "_" + i).remove("rt_" + j + "_" + i).commit();
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(dialogId, threadId, draftMessage.message, draftMessage.entities, (TLRPC.Message) null, draftMessage.no_webpage, true);
            }
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(long dialogId, ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda104(this, messages, dialogId));
    }

    /* renamed from: lambda$clearBotKeyboard$129$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m781x95dcb1f(ArrayList messages, long dialogId) {
        if (messages != null) {
            for (int a = 0; a < messages.size(); a++) {
                long did1 = this.botKeyboardsByMids.get(((Integer) messages.get(a)).intValue());
                if (did1 != 0) {
                    this.botKeyboards.remove(did1);
                    this.botKeyboardsByMids.delete(((Integer) messages.get(a)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(did1));
                }
            }
            return;
        }
        this.botKeyboards.remove(dialogId);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(dialogId));
    }

    public void loadBotKeyboard(long dialogId) {
        TLRPC.Message keyboard = this.botKeyboards.get(dialogId);
        if (keyboard != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, keyboard, Long.valueOf(dialogId));
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda64(this, dialogId));
    }

    /* renamed from: lambda$loadBotKeyboard$131$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m806xa8eevar_b(long dialogId) {
        NativeByteBuffer data;
        TLRPC.Message botKeyboard = null;
        try {
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
            if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
                botKeyboard = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                data.reuse();
            }
            cursor.dispose();
            if (botKeyboard != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda125(this, botKeyboard, dialogId));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadBotKeyboard$130$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m805xe60287fc(TLRPC.Message botKeyboardFinal, long dialogId) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, botKeyboardFinal, Long.valueOf(dialogId));
    }

    private TLRPC.BotInfo loadBotInfoInternal(long uid, long dialogId) throws SQLiteException {
        NativeByteBuffer data;
        TLRPC.BotInfo botInfo = null;
        SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info_v2 WHERE uid = %d AND dialogId = %d", new Object[]{Long.valueOf(uid), Long.valueOf(dialogId)}), new Object[0]);
        if (cursor.next() && !cursor.isNull(0) && (data = cursor.byteBufferValue(0)) != null) {
            botInfo = TLRPC.BotInfo.TLdeserialize(data, data.readInt32(false), false);
            data.reuse();
        }
        cursor.dispose();
        return botInfo;
    }

    public void loadBotInfo(long uid, long dialogId, boolean cache, int classGuid) {
        if (cache) {
            HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
            TLRPC.BotInfo botInfo = hashMap.get(uid + "_" + dialogId);
            if (botInfo != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
                return;
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda75(this, uid, dialogId, classGuid));
    }

    /* renamed from: lambda$loadBotInfo$133$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m804xbe962672(long uid, long dialogId, int classGuid) {
        try {
            TLRPC.BotInfo botInfo = loadBotInfoInternal(uid, dialogId);
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda121(this, botInfo, classGuid));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$loadBotInfo$132$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m803xfba9bd13(TLRPC.BotInfo botInfo, int classGuid) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(classGuid));
    }

    public void putBotKeyboard(long dialogId, TLRPC.Message message) {
        if (message != null) {
            int mid = 0;
            try {
                SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(dialogId)}), new Object[0]);
                if (cursor.next()) {
                    mid = cursor.intValue(0);
                }
                cursor.dispose();
                if (mid < message.id) {
                    SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, dialogId);
                    state.bindInteger(2, message.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda81(this, dialogId, message));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$putBotKeyboard$134$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m872x946411c7(long dialogId, TLRPC.Message message) {
        TLRPC.Message old = this.botKeyboards.get(dialogId);
        this.botKeyboards.put(dialogId, message);
        if (MessageObject.getChannelId(message) == 0) {
            if (old != null) {
                this.botKeyboardsByMids.delete(old.id);
            }
            this.botKeyboardsByMids.put(message.id, dialogId);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(dialogId));
    }

    public void putBotInfo(long dialogId, TLRPC.BotInfo botInfo) {
        if (botInfo != null) {
            HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
            hashMap.put(botInfo.user_id + "_" + dialogId, botInfo);
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda122(this, botInfo, dialogId));
        }
    }

    /* renamed from: lambda$putBotInfo$135$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m871lambda$putBotInfo$135$orgtelegrammessengerMediaDataController(TLRPC.BotInfo botInfo, long dialogId) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(botInfo.getObjectSize());
            botInfo.serializeToStream(data);
            state.bindLong(1, botInfo.user_id);
            state.bindLong(2, dialogId);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void updateBotInfo(long dialogId, TLRPC.TL_updateBotCommands update) {
        HashMap<String, TLRPC.BotInfo> hashMap = this.botInfos;
        TLRPC.BotInfo botInfo = hashMap.get(update.bot_id + "_" + dialogId);
        if (botInfo != null) {
            botInfo.commands = update.commands;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, 0);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda142(this, update, dialogId));
    }

    /* renamed from: lambda$updateBotInfo$136$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m903x3c8CLASSNAMEf2(TLRPC.TL_updateBotCommands update, long dialogId) {
        try {
            TLRPC.BotInfo info = loadBotInfoInternal(update.bot_id, dialogId);
            if (info != null) {
                info.commands = update.commands;
            }
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(info.getObjectSize());
            info.serializeToStream(data);
            state.bindLong(1, info.user_id);
            state.bindLong(2, dialogId);
            state.bindByteBuffer(3, data);
            state.step();
            data.reuse();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void fetchNewEmojiKeywords(String[] langCodes) {
        if (langCodes != null) {
            int a = 0;
            while (a < langCodes.length) {
                String langCode = langCodes[a];
                if (!TextUtils.isEmpty(langCode) && this.currentFetchingEmoji.get(langCode) == null) {
                    this.currentFetchingEmoji.put(langCode, true);
                    getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda93(this, langCode));
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$fetchNewEmojiKeywords$142$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m789x91416219(java.lang.String r10) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r9.getMessagesStorage()     // Catch:{ Exception -> 0x0032 }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x0032 }
            java.lang.String r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0032 }
            r8 = 0
            r7[r8] = r10     // Catch:{ Exception -> 0x0032 }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x0032 }
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x0032 }
            if (r5 == 0) goto L_0x002e
            java.lang.String r5 = r4.stringValue(r8)     // Catch:{ Exception -> 0x0032 }
            r1 = r5
            int r5 = r4.intValue(r6)     // Catch:{ Exception -> 0x0032 }
            r0 = r5
            r5 = 2
            long r5 = r4.longValue(r5)     // Catch:{ Exception -> 0x0032 }
            r2 = r5
        L_0x002e:
            r4.dispose()     // Catch:{ Exception -> 0x0032 }
            goto L_0x0036
        L_0x0032:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0036:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0053
            long r4 = java.lang.System.currentTimeMillis()
            long r4 = r4 - r2
            long r4 = java.lang.Math.abs(r4)
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x0053
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda89 r4 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda89
            r4.<init>(r9, r10)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            return
        L_0x0053:
            r4 = -1
            if (r0 != r4) goto L_0x005f
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords r4 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords
            r4.<init>()
            r4.lang_code = r10
            goto L_0x0069
        L_0x005f:
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference r4 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference
            r4.<init>()
            r4.lang_code = r10
            r4.from_version = r0
            r5 = r4
        L_0x0069:
            r5 = r1
            r6 = r0
            org.telegram.tgnet.ConnectionsManager r7 = r9.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda25 r8 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda25
            r8.<init>(r9, r6, r5, r10)
            r7.sendRequest(r4, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m789x91416219(java.lang.String):void");
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$137$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m784xCLASSNAMEeae73(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$141$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m788xce54f8ba(int versionFinal, String aliasFinal, String langCode, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_emojiKeywordsDifference res = (TLRPC.TL_emojiKeywordsDifference) response;
            if (versionFinal == -1 || res.lang_code.equals(aliasFinal)) {
                putEmojiKeywords(langCode, res);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda91(this, langCode));
            }
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda92(this, langCode));
        }
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$139$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m786x4b178131(String langCode) {
        try {
            SQLitePreparedStatement deleteState = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            deleteState.bindString(1, langCode);
            deleteState.step();
            deleteState.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda90(this, langCode));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$138$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m785x882b17d2(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
        fetchNewEmojiKeywords(new String[]{langCode});
    }

    /* renamed from: lambda$fetchNewEmojiKeywords$140$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m787xb688f5b(String langCode) {
        this.currentFetchingEmoji.remove(langCode);
    }

    private void putEmojiKeywords(String lang, TLRPC.TL_emojiKeywordsDifference res) {
        if (res != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda133(this, res, lang));
        }
    }

    /* renamed from: lambda$putEmojiKeywords$144$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m875x2a3var_(TLRPC.TL_emojiKeywordsDifference res, String lang) {
        try {
            if (!res.keywords.isEmpty()) {
                SQLitePreparedStatement insertState = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement deleteState = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int N = res.keywords.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.EmojiKeyword keyword = res.keywords.get(a);
                    if (keyword instanceof TLRPC.TL_emojiKeyword) {
                        TLRPC.TL_emojiKeyword emojiKeyword = (TLRPC.TL_emojiKeyword) keyword;
                        String key = emojiKeyword.keyword.toLowerCase();
                        int N2 = emojiKeyword.emoticons.size();
                        for (int b = 0; b < N2; b++) {
                            insertState.requery();
                            insertState.bindString(1, res.lang_code);
                            insertState.bindString(2, key);
                            insertState.bindString(3, emojiKeyword.emoticons.get(b));
                            insertState.step();
                        }
                    } else if (keyword instanceof TLRPC.TL_emojiKeywordDeleted) {
                        TLRPC.TL_emojiKeywordDeleted keywordDeleted = (TLRPC.TL_emojiKeywordDeleted) keyword;
                        String key2 = keywordDeleted.keyword.toLowerCase();
                        int N22 = keywordDeleted.emoticons.size();
                        for (int b2 = 0; b2 < N22; b2++) {
                            deleteState.requery();
                            deleteState.bindString(1, res.lang_code);
                            deleteState.bindString(2, key2);
                            deleteState.bindString(3, keywordDeleted.emoticons.get(b2));
                            deleteState.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                insertState.dispose();
                deleteState.dispose();
            }
            SQLitePreparedStatement infoState = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            infoState.bindString(1, lang);
            infoState.bindString(2, res.lang_code);
            infoState.bindInteger(3, res.version);
            infoState.bindLong(4, System.currentTimeMillis());
            infoState.step();
            infoState.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda96(this, lang));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$putEmojiKeywords$143$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m874x6752bae7(String lang) {
        this.currentFetchingEmoji.remove(lang);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, lang);
    }

    public void getEmojiSuggestions(String[] langCodes, String keyword, boolean fullMatch, KeywordResultCallback callback) {
        getEmojiSuggestions(langCodes, keyword, fullMatch, callback, (CountDownLatch) null);
    }

    public void getEmojiSuggestions(String[] langCodes, String keyword, boolean fullMatch, KeywordResultCallback callback, CountDownLatch sync) {
        if (callback != null) {
            if (TextUtils.isEmpty(keyword) || langCodes == null) {
                callback.run(new ArrayList(), (String) null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda5(this, langCodes, callback, keyword, fullMatch, new ArrayList<>(Emoji.recentEmoji), sync));
            if (sync != null) {
                try {
                    sync.await();
                } catch (Throwable th) {
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0156  */
    /* renamed from: lambda$getEmojiSuggestions$148$org-telegram-messenger-MediaDataController  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m791x22b62dde(java.lang.String[] r19, org.telegram.messenger.MediaDataController.KeywordResultCallback r20, java.lang.String r21, boolean r22, java.util.ArrayList r23, java.util.concurrent.CountDownLatch r24) {
        /*
            r18 = this;
            r1 = r19
            r2 = r20
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4 = r0
            r0 = 0
            r5 = 0
            r6 = 0
            r17 = r5
            r5 = r0
            r0 = r17
        L_0x0018:
            int r7 = r1.length     // Catch:{ Exception -> 0x013c }
            r8 = 0
            r9 = 1
            if (r6 >= r7) goto L_0x0045
            org.telegram.messenger.MessagesStorage r7 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013c }
            org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x013c }
            java.lang.String r10 = "SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?"
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013c }
            r11 = r1[r6]     // Catch:{ Exception -> 0x013c }
            r9[r8] = r11     // Catch:{ Exception -> 0x013c }
            org.telegram.SQLite.SQLiteCursor r7 = r7.queryFinalized(r10, r9)     // Catch:{ Exception -> 0x013c }
            boolean r9 = r7.next()     // Catch:{ Exception -> 0x013c }
            if (r9 == 0) goto L_0x003c
            java.lang.String r8 = r7.stringValue(r8)     // Catch:{ Exception -> 0x013c }
            r5 = r8
        L_0x003c:
            r7.dispose()     // Catch:{ Exception -> 0x013c }
            if (r5 == 0) goto L_0x0042
            r0 = 1
        L_0x0042:
            int r6 = r6 + 1
            goto L_0x0018
        L_0x0045:
            if (r0 != 0) goto L_0x0052
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda6 r6 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda6     // Catch:{ Exception -> 0x013c }
            r7 = r18
            r6.<init>(r7, r1, r2, r3)     // Catch:{ Exception -> 0x013a }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)     // Catch:{ Exception -> 0x013a }
            return
        L_0x0052:
            r7 = r18
            java.lang.String r6 = r21.toLowerCase()     // Catch:{ Exception -> 0x013a }
            r10 = 0
        L_0x0059:
            r11 = 2
            if (r10 >= r11) goto L_0x0137
            if (r10 != r9) goto L_0x0072
            org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x013a }
            java.lang.String r12 = r12.getTranslitString(r6, r8, r8)     // Catch:{ Exception -> 0x013a }
            boolean r13 = r12.equals(r6)     // Catch:{ Exception -> 0x013a }
            if (r13 == 0) goto L_0x0071
            r16 = r0
            r15 = 1
            goto L_0x012f
        L_0x0071:
            r6 = r12
        L_0x0072:
            r12 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r13.<init>(r6)     // Catch:{ Exception -> 0x013a }
            int r14 = r13.length()     // Catch:{ Exception -> 0x013a }
        L_0x007c:
            if (r14 <= 0) goto L_0x0094
            int r14 = r14 + -1
            char r15 = r13.charAt(r14)     // Catch:{ Exception -> 0x013a }
            int r11 = r15 + 1
            char r11 = (char) r11     // Catch:{ Exception -> 0x013a }
            r13.setCharAt(r14, r11)     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0092
            java.lang.String r15 = r13.toString()     // Catch:{ Exception -> 0x013a }
            r12 = r15
            goto L_0x0094
        L_0x0092:
            r11 = 2
            goto L_0x007c
        L_0x0094:
            if (r22 == 0) goto L_0x00ab
            org.telegram.messenger.MessagesStorage r11 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteDatabase r11 = r11.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r15 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?"
            java.lang.Object[] r8 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r8[r16] = r6     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteCursor r8 = r11.queryFinalized(r15, r8)     // Catch:{ Exception -> 0x013a }
            goto L_0x00eb
        L_0x00ab:
            if (r12 == 0) goto L_0x00c5
            org.telegram.messenger.MessagesStorage r8 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteDatabase r8 = r8.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?"
            r15 = 2
            java.lang.Object[] r15 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r15[r16] = r6     // Catch:{ Exception -> 0x013a }
            r15[r9] = r12     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteCursor r8 = r8.queryFinalized(r11, r15)     // Catch:{ Exception -> 0x013a }
            goto L_0x00eb
        L_0x00c5:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r8.<init>()     // Catch:{ Exception -> 0x013a }
            r8.append(r6)     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "%"
            r8.append(r11)     // Catch:{ Exception -> 0x013a }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x013a }
            r6 = r8
            org.telegram.messenger.MessagesStorage r8 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteDatabase r8 = r8.getDatabase()     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?"
            java.lang.Object[] r15 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x013a }
            r16 = 0
            r15[r16] = r6     // Catch:{ Exception -> 0x013a }
            org.telegram.SQLite.SQLiteCursor r8 = r8.queryFinalized(r11, r15)     // Catch:{ Exception -> 0x013a }
        L_0x00eb:
            boolean r11 = r8.next()     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0129
            r11 = 0
            java.lang.String r15 = r8.stringValue(r11)     // Catch:{ Exception -> 0x013a }
            java.lang.String r11 = "️"
            java.lang.String r9 = ""
            java.lang.String r9 = r15.replace(r11, r9)     // Catch:{ Exception -> 0x013a }
            java.lang.Object r11 = r4.get(r9)     // Catch:{ Exception -> 0x013a }
            if (r11 == 0) goto L_0x0109
            r16 = r0
            r15 = 1
            goto L_0x0125
        L_0x0109:
            r11 = 1
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r11)     // Catch:{ Exception -> 0x013a }
            r4.put(r9, r15)     // Catch:{ Exception -> 0x013a }
            org.telegram.messenger.MediaDataController$KeywordResult r11 = new org.telegram.messenger.MediaDataController$KeywordResult     // Catch:{ Exception -> 0x013a }
            r11.<init>()     // Catch:{ Exception -> 0x013a }
            r11.emoji = r9     // Catch:{ Exception -> 0x013a }
            r16 = r0
            r15 = 1
            java.lang.String r0 = r8.stringValue(r15)     // Catch:{ Exception -> 0x013a }
            r11.keyword = r0     // Catch:{ Exception -> 0x013a }
            r3.add(r11)     // Catch:{ Exception -> 0x013a }
        L_0x0125:
            r0 = r16
            r9 = 1
            goto L_0x00eb
        L_0x0129:
            r16 = r0
            r15 = 1
            r8.dispose()     // Catch:{ Exception -> 0x013a }
        L_0x012f:
            int r10 = r10 + 1
            r0 = r16
            r8 = 0
            r9 = 1
            goto L_0x0059
        L_0x0137:
            r16 = r0
            goto L_0x0142
        L_0x013a:
            r0 = move-exception
            goto L_0x013f
        L_0x013c:
            r0 = move-exception
            r7 = r18
        L_0x013f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0142:
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda9 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda9
            r6 = r23
            r0.<init>(r6)
            java.util.Collections.sort(r3, r0)
            r0 = r5
            if (r24 == 0) goto L_0x0156
            r2.run(r3, r0)
            r24.countDown()
            goto L_0x015e
        L_0x0156:
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda0 r8 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda0
            r8.<init>(r2, r3, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
        L_0x015e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.m791x22b62dde(java.lang.String[], org.telegram.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    /* renamed from: lambda$getEmojiSuggestions$145$org-telegram-messenger-MediaDataController  reason: not valid java name */
    public /* synthetic */ void m790xd9f0f1c1(String[] langCodes, KeywordResultCallback callback, ArrayList result) {
        int a = 0;
        while (a < langCodes.length) {
            if (this.currentFetchingEmoji.get(langCodes[a]) == null) {
                a++;
            } else {
                return;
            }
        }
        callback.run(result, (String) null);
    }

    static /* synthetic */ int lambda$getEmojiSuggestions$146(ArrayList recentEmoji, KeywordResult o1, KeywordResult o2) {
        int idx1 = recentEmoji.indexOf(o1.emoji);
        if (idx1 < 0) {
            idx1 = Integer.MAX_VALUE;
        }
        int idx2 = recentEmoji.indexOf(o2.emoji);
        if (idx2 < 0) {
            idx2 = Integer.MAX_VALUE;
        }
        if (idx1 < idx2) {
            return -1;
        }
        if (idx1 > idx2) {
            return 1;
        }
        int len1 = o1.keyword.length();
        int len2 = o2.keyword.length();
        if (len1 < len2) {
            return -1;
        }
        if (len1 > len2) {
            return 1;
        }
        return 0;
    }
}
