package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;

public class MediaDataController extends BaseController {
    private static volatile MediaDataController[] Instance = new MediaDataController[3];
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 5;
    public static final int MEDIA_URL = 3;
    public static final int TYPE_EMOJI = 4;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC.MessageEntity> entityComparator = $$Lambda$MediaDataController$sDNm5LkE_RPhKeoY01hkTYhXI.INSTANCE;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<TLRPC.Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC.Document>> allStickersFeatured = new HashMap<>();
    private int[] archivedStickersCount = new int[2];
    private SparseArray<TLRPC.BotInfo> botInfos = new SparseArray<>();
    private LongSparseArray<TLRPC.Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    private LongSparseArray<TLRPC.Message> draftMessages = new LongSparseArray<>();
    private LongSparseArray<TLRPC.DraftMessage> drafts = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private LongSparseArray<TLRPC.StickerSetCovered> featuredStickerSetsById = new LongSparseArray<>();
    private boolean featuredStickersLoaded;
    private LongSparseArray<TLRPC.TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    public ArrayList<TLRPC.TL_topPeer> hints = new ArrayList<>();
    private boolean inTransaction;
    public ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<TLRPC.TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private long lastDialogId;
    private int lastGuid;
    private long lastMergeDialogId;
    private int lastReqId;
    private int lastReturnedNum;
    private String lastSearchQuery;
    private TLRPC.User lastSearchUser;
    private int[] loadDate = new int[5];
    private int loadFeaturedDate;
    private int loadFeaturedHash;
    private int[] loadHash = new int[5];
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingMoreSearchMessages;
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[3];
    private boolean[] loadingStickers = new boolean[5];
    private int mergeReqId;
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private SharedPreferences preferences;
    private ArrayList<Long> readingStickerSets = new ArrayList<>();
    private ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    private boolean recentGifsLoaded;
    private ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] recentStickersLoaded = new boolean[3];
    private int reqId;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC.TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap<>();
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private LongSparseArray<TLRPC.Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] stickersLoaded = new boolean[5];
    private ArrayList<Long> unreadStickerSets = new ArrayList<>();

    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    static /* synthetic */ void lambda$markFaturedStickersAsRead$28(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$29(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$removeInline$79(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$removePeer$80(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$saveDraft$103(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static MediaDataController getInstance(int i) {
        MediaDataController mediaDataController = Instance[i];
        if (mediaDataController == null) {
            synchronized (MediaDataController.class) {
                mediaDataController = Instance[i];
                if (mediaDataController == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController2 = new MediaDataController(i);
                    mediaDataControllerArr[i] = mediaDataController2;
                    mediaDataController = mediaDataController2;
                }
            }
        }
        return mediaDataController;
    }

    public MediaDataController(int i) {
        super(i);
        if (this.currentAccount == 0) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.preferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Map.Entry next : this.preferences.getAll().entrySet()) {
            try {
                String str = (String) next.getKey();
                long longValue = Utilities.parseLong(str).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) next.getValue()));
                if (str.startsWith("r_")) {
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    TLdeserialize.readAttachPath(serializedData, getUserConfig().clientUserId);
                    if (TLdeserialize != null) {
                        this.draftMessages.put(longValue, TLdeserialize);
                    }
                } else {
                    TLRPC.DraftMessage TLdeserialize2 = TLRPC.DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (TLdeserialize2 != null) {
                        this.drafts.put(longValue, TLdeserialize2);
                    }
                }
                serializedData.cleanup();
            } catch (Exception unused) {
            }
        }
    }

    public void cleanup() {
        for (int i = 0; i < 3; i++) {
            this.recentStickers[i].clear();
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = false;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this.loadHash[i2] = 0;
            this.loadDate[i2] = 0;
            this.stickerSets[i2].clear();
            this.loadingStickers[i2] = false;
            this.stickersLoaded[i2] = false;
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
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable($$Lambda$MediaDataController$nmhbhe5tC7jDydlso3J10HRGcDM.INSTANCE);
        }
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    static /* synthetic */ void lambda$cleanup$0() {
        try {
            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).removeAllDynamicShortcuts();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void checkStickers(int i) {
        if (this.loadingStickers[i]) {
            return;
        }
        if (!this.stickersLoaded[i] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[i])) >= 3600) {
            loadStickers(i, true, false);
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

    public ArrayList<TLRPC.Document> getRecentStickers(int i) {
        ArrayList<TLRPC.Document> arrayList = this.recentStickers[i];
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC.Document> getRecentStickersNoCopy(int i) {
        return this.recentStickers[i];
    }

    public boolean isStickerInFavorites(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int i = 0; i < this.recentStickers[2].size(); i++) {
            TLRPC.Document document2 = this.recentStickers[2].get(i);
            if (document2.id == document.id && document2.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentSticker(int i, Object obj, TLRPC.Document document, int i2, boolean z) {
        boolean z2;
        int i3;
        TLRPC.Document document2;
        int i4 = i;
        TLRPC.Document document3 = document;
        boolean z3 = z;
        if (MessageObject.isStickerDocument(document) || MessageObject.isAnimatedStickerDocument(document3, true)) {
            int i5 = 0;
            while (true) {
                if (i5 >= this.recentStickers[i4].size()) {
                    z2 = false;
                    break;
                }
                TLRPC.Document document4 = this.recentStickers[i4].get(i5);
                if (document4.id == document3.id) {
                    this.recentStickers[i4].remove(i5);
                    if (!z3) {
                        this.recentStickers[i4].add(0, document4);
                    }
                    z2 = true;
                } else {
                    i5++;
                }
            }
            if (!z2 && !z3) {
                this.recentStickers[i4].add(0, document3);
            }
            if (i4 == 2) {
                if (z3) {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("RemovedFromFavorites", NUM), 0).show();
                } else {
                    Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("AddedToFavorites", NUM), 0).show();
                }
                TLRPC.TL_messages_faveSticker tL_messages_faveSticker = new TLRPC.TL_messages_faveSticker();
                tL_messages_faveSticker.id = new TLRPC.TL_inputDocument();
                TLRPC.InputDocument inputDocument = tL_messages_faveSticker.id;
                inputDocument.id = document3.id;
                inputDocument.access_hash = document3.access_hash;
                inputDocument.file_reference = document3.file_reference;
                if (inputDocument.file_reference == null) {
                    inputDocument.file_reference = new byte[0];
                }
                tL_messages_faveSticker.unfave = z3;
                Object obj2 = obj;
                getConnectionsManager().sendRequest(tL_messages_faveSticker, new RequestDelegate(obj, tL_messages_faveSticker) {
                    private final /* synthetic */ Object f$1;
                    private final /* synthetic */ TLRPC.TL_messages_faveSticker f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$addRecentSticker$1$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
                i3 = getMessagesController().maxFaveStickersCount;
            } else {
                i3 = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[i4].size() > i3 || z3) {
                if (z3) {
                    document2 = document3;
                } else {
                    ArrayList<TLRPC.Document>[] arrayListArr = this.recentStickers;
                    document2 = arrayListArr[i4].remove(arrayListArr[i4].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, document2) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ TLRPC.Document f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$addRecentSticker$2$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
            if (!z3) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(document3);
                processLoadedRecentDocuments(i, arrayList, false, i2, false);
            }
            if (i4 == 2) {
                getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, false, Integer.valueOf(i));
            }
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$1$MediaDataController(Object obj, TLRPC.TL_messages_faveSticker tL_messages_faveSticker, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_faveSticker);
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$2$MediaDataController(int i, TLRPC.Document document) {
        int i2 = i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = " + i2).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public ArrayList<TLRPC.Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGif(TLRPC.Document document) {
        this.recentGifs.remove(document);
        TLRPC.TL_messages_saveGif tL_messages_saveGif = new TLRPC.TL_messages_saveGif();
        tL_messages_saveGif.id = new TLRPC.TL_inputDocument();
        TLRPC.InputDocument inputDocument = tL_messages_saveGif.id;
        inputDocument.id = document.id;
        inputDocument.access_hash = document.access_hash;
        inputDocument.file_reference = document.file_reference;
        if (inputDocument.file_reference == null) {
            inputDocument.file_reference = new byte[0];
        }
        tL_messages_saveGif.unsave = true;
        getConnectionsManager().sendRequest(tL_messages_saveGif, new RequestDelegate(tL_messages_saveGif) {
            private final /* synthetic */ TLRPC.TL_messages_saveGif f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$removeRecentGif$3$MediaDataController(this.f$1, tLObject, tL_error);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(document) {
            private final /* synthetic */ TLRPC.Document f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$removeRecentGif$4$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeRecentGif$3$MediaDataController(TLRPC.TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text)) {
            getFileRefController().requestReference("gif", tL_messages_saveGif);
        }
    }

    public /* synthetic */ void lambda$removeRecentGif$4$MediaDataController(TLRPC.Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean hasRecentGif(TLRPC.Document document) {
        for (int i = 0; i < this.recentGifs.size(); i++) {
            TLRPC.Document document2 = this.recentGifs.get(i);
            if (document2.id == document.id) {
                this.recentGifs.remove(i);
                this.recentGifs.add(0, document2);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(TLRPC.Document document, int i) {
        boolean z;
        int i2 = 0;
        while (true) {
            if (i2 >= this.recentGifs.size()) {
                z = false;
                break;
            }
            TLRPC.Document document2 = this.recentGifs.get(i2);
            if (document2.id == document.id) {
                this.recentGifs.remove(i2);
                this.recentGifs.add(0, document2);
                z = true;
                break;
            }
            i2++;
        }
        if (!z) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > getMessagesController().maxRecentGifsCount) {
            ArrayList<TLRPC.Document> arrayList = this.recentGifs;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList.remove(arrayList.size() - 1)) {
                private final /* synthetic */ TLRPC.Document f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$addRecentGif$5$MediaDataController(this.f$1);
                }
            });
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(document);
        processLoadedRecentDocuments(0, arrayList2, true, i, false);
    }

    public /* synthetic */ void lambda$addRecentGif$5$MediaDataController(TLRPC.Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean isLoadingStickers(int i) {
        return this.loadingStickers[i];
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001e, code lost:
        r0 = r11.groupStickerSets.get(r12.set.id);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void replaceStickerSet(org.telegram.tgnet.TLRPC.TL_messages_stickerSet r12) {
        /*
            r11 = this;
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r11.stickerSetsById
            org.telegram.tgnet.TLRPC$StickerSet r1 = r12.set
            long r1 = r1.id
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0
            if (r0 != 0) goto L_0x001a
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r11.stickerSetsByName
            org.telegram.tgnet.TLRPC$StickerSet r1 = r12.set
            java.lang.String r1 = r1.short_name
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0
        L_0x001a:
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x002e
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r11.groupStickerSets
            org.telegram.tgnet.TLRPC$StickerSet r3 = r12.set
            long r3 = r3.id
            java.lang.Object r0 = r0.get(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0
            if (r0 == 0) goto L_0x002e
            r3 = 1
            goto L_0x002f
        L_0x002e:
            r3 = 0
        L_0x002f:
            if (r0 != 0) goto L_0x0032
            return
        L_0x0032:
            org.telegram.tgnet.TLRPC$StickerSet r4 = r12.set
            java.lang.String r4 = r4.short_name
            java.lang.String r5 = "AnimatedEmojies"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0053
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r12.documents
            r0.documents = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_stickerPack> r1 = r12.packs
            r0.packs = r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r12.set
            r0.set = r1
            org.telegram.messenger.-$$Lambda$MediaDataController$k_Aic-iK2zJ3Y91NflwmRI9TpjI r1 = new org.telegram.messenger.-$$Lambda$MediaDataController$k_Aic-iK2zJ3Y91NflwmRI9TpjI
            r1.<init>(r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x0096
        L_0x0053:
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r12.documents
            int r6 = r6.size()
            r7 = 0
        L_0x005f:
            if (r7 >= r6) goto L_0x0071
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r12.documents
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC.Document) r8
            long r9 = r8.id
            r4.put(r9, r8)
            int r7 = r7 + 1
            goto L_0x005f
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r0.documents
            int r6 = r6.size()
            r7 = 0
        L_0x0078:
            if (r1 >= r6) goto L_0x0095
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r0.documents
            java.lang.Object r8 = r8.get(r1)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC.Document) r8
            long r8 = r8.id
            java.lang.Object r8 = r4.get(r8)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC.Document) r8
            if (r8 == 0) goto L_0x0092
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r7 = r0.documents
            r7.set(r1, r8)
            r7 = 1
        L_0x0092:
            int r1 = r1 + 1
            goto L_0x0078
        L_0x0095:
            r2 = r7
        L_0x0096:
            if (r2 == 0) goto L_0x00cb
            if (r3 == 0) goto L_0x009e
            r11.putSetToCache(r0)
            goto L_0x00cb
        L_0x009e:
            org.telegram.tgnet.TLRPC$StickerSet r0 = r12.set
            boolean r0 = r0.masks
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>[] r1 = r11.stickerSets
            r1 = r1[r0]
            int[] r2 = r11.loadDate
            r2 = r2[r0]
            int[] r3 = r11.loadHash
            r3 = r3[r0]
            r11.putStickersToCache(r0, r1, r2, r3)
            org.telegram.tgnet.TLRPC$StickerSet r12 = r12.set
            java.lang.String r12 = r12.short_name
            boolean r12 = r5.equals(r12)
            if (r12 == 0) goto L_0x00cb
            r12 = 4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>[] r0 = r11.stickerSets
            r0 = r0[r12]
            int[] r1 = r11.loadDate
            r1 = r1[r12]
            int[] r2 = r11.loadHash
            r2 = r2[r12]
            r11.putStickersToCache(r12, r0, r1, r2)
        L_0x00cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet):void");
    }

    public /* synthetic */ void lambda$replaceStickerSet$6$MediaDataController(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        LongSparseArray<TLRPC.Document> stickerByIds = getStickerByIds(4);
        for (int i = 0; i < tL_messages_stickerSet.documents.size(); i++) {
            TLRPC.Document document = tL_messages_stickerSet.documents.get(i);
            stickerByIds.put(document.id, document);
        }
    }

    public TLRPC.TL_messages_stickerSet getStickerSetByName(String str) {
        return this.stickerSetsByName.get(str);
    }

    public TLRPC.TL_messages_stickerSet getStickerSetById(long j) {
        return this.stickerSetsById.get(j);
    }

    public TLRPC.TL_messages_stickerSet getGroupStickerSetById(TLRPC.StickerSet stickerSet) {
        TLRPC.StickerSet stickerSet2;
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSetsById.get(stickerSet.id);
        if (tL_messages_stickerSet == null) {
            tL_messages_stickerSet = this.groupStickerSets.get(stickerSet.id);
            if (tL_messages_stickerSet == null || (stickerSet2 = tL_messages_stickerSet.set) == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (stickerSet2.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return tL_messages_stickerSet;
    }

    public void putGroupStickerSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
    }

    private void loadGroupStickerSet(TLRPC.StickerSet stickerSet, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(stickerSet) {
                private final /* synthetic */ TLRPC.StickerSet f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$loadGroupStickerSet$8$MediaDataController(this.f$1);
                }
            });
            return;
        }
        TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
        tL_messages_getStickerSet.stickerset = new TLRPC.TL_inputStickerSetID();
        TLRPC.InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
        inputStickerSet.id = stickerSet.id;
        inputStickerSet.access_hash = stickerSet.access_hash;
        getConnectionsManager().sendRequest(tL_messages_getStickerSet, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$loadGroupStickerSet$10$MediaDataController(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$8$MediaDataController(TLRPC.StickerSet stickerSet) {
        NativeByteBuffer byteBufferValue;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.id + "'", new Object[0]);
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tL_messages_stickerSet = TLRPC.TL_messages_stickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tL_messages_stickerSet == null || tL_messages_stickerSet.set == null || tL_messages_stickerSet.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
            if (tL_messages_stickerSet != null && tL_messages_stickerSet.set != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tL_messages_stickerSet) {
                    private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$7$MediaDataController(this.f$1);
                    }
                });
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$7$MediaDataController(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$10$MediaDataController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_messages_stickerSet) tLObject) {
                private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$9$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$9$MediaDataController(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    private void putSetToCache(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(tL_messages_stickerSet) {
            private final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$putSetToCache$11$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$putSetToCache$11$MediaDataController(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindString(1, "s_" + tL_messages_stickerSet.set.id);
            executeFast.bindInteger(2, 6);
            executeFast.bindString(3, "");
            executeFast.bindString(4, "");
            executeFast.bindString(5, "");
            executeFast.bindInteger(6, 0);
            executeFast.bindInteger(7, 0);
            executeFast.bindInteger(8, 0);
            executeFast.bindInteger(9, 0);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_messages_stickerSet.getObjectSize());
            tL_messages_stickerSet.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(10, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
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

    public TLRPC.Document getEmojiAnimatedSticker(CharSequence charSequence) {
        String replace = charSequence.toString().replace("️", "");
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets2 = getStickerSets(4);
        int size = stickerSets2.size();
        for (int i = 0; i < size; i++) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets2.get(i);
            int size2 = tL_messages_stickerSet.packs.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC.TL_stickerPack tL_stickerPack = tL_messages_stickerSet.packs.get(i2);
                if (!tL_stickerPack.documents.isEmpty() && TextUtils.equals(tL_stickerPack.emoticon, replace)) {
                    return getStickerByIds(4).get(tL_stickerPack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int i) {
        if (i == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[i];
    }

    public LongSparseArray<TLRPC.Document> getStickerByIds(int i) {
        return this.stickersByIds[i];
    }

    public ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int size = this.featuredStickerSets.size();
        for (int i = 0; i < size; i++) {
            TLRPC.StickerSetCovered stickerSetCovered = this.featuredStickerSets.get(i);
            if (!isStickerPackInstalled(stickerSetCovered.set.id) && ((!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id)))) {
                return false;
            }
        }
        return true;
    }

    public boolean isStickerPackInstalled(long j) {
        return this.installedStickerSetsById.indexOfKey(j) >= 0;
    }

    public boolean isStickerPackUnread(long j) {
        return this.unreadStickerSets.contains(Long.valueOf(j));
    }

    public boolean isStickerPackInstalled(String str) {
        return this.stickerSetsByName.containsKey(str);
    }

    public String getEmojiForSticker(long j) {
        String str = this.stickersByEmoji.get(j);
        return str != null ? str : "";
    }

    private static int calcDocumentsHash(ArrayList<TLRPC.Document> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        long j = 0;
        for (int i = 0; i < Math.min(200, arrayList.size()); i++) {
            TLRPC.Document document = arrayList.get(i);
            if (document != null) {
                long j2 = document.id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
        }
        return (int) j;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        if (r5.recentStickersLoaded[r6] != false) goto L_0x001f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r5.recentGifsLoaded != false) goto L_0x001f;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(int r6, boolean r7, boolean r8, boolean r9) {
        /*
            r5 = this;
            r0 = 0
            r1 = 1
            if (r7 == 0) goto L_0x0010
            boolean r2 = r5.loadingRecentGifs
            if (r2 == 0) goto L_0x0009
            return
        L_0x0009:
            r5.loadingRecentGifs = r1
            boolean r2 = r5.recentGifsLoaded
            if (r2 == 0) goto L_0x0020
            goto L_0x001f
        L_0x0010:
            boolean[] r2 = r5.loadingRecentStickers
            boolean r3 = r2[r6]
            if (r3 == 0) goto L_0x0017
            return
        L_0x0017:
            r2[r6] = r1
            boolean[] r2 = r5.recentStickersLoaded
            boolean r2 = r2[r6]
            if (r2 == 0) goto L_0x0020
        L_0x001f:
            r8 = 0
        L_0x0020:
            if (r8 == 0) goto L_0x0034
            org.telegram.messenger.MessagesStorage r8 = r5.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r8.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MediaDataController$zvwtekz9kf_LvFftQPSNCSueoKU r9 = new org.telegram.messenger.-$$Lambda$MediaDataController$zvwtekz9kf_LvFftQPSNCSueoKU
            r9.<init>(r7, r6)
            r8.postRunnable(r9)
            goto L_0x00c8
        L_0x0034:
            int r8 = r5.currentAccount
            android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getEmojiSettings(r8)
            if (r9 != 0) goto L_0x0079
            r2 = 0
            if (r7 == 0) goto L_0x0047
            java.lang.String r9 = "lastGifLoadTime"
            long r8 = r8.getLong(r9, r2)
            goto L_0x005f
        L_0x0047:
            if (r6 != 0) goto L_0x0050
            java.lang.String r9 = "lastStickersLoadTime"
            long r8 = r8.getLong(r9, r2)
            goto L_0x005f
        L_0x0050:
            if (r6 != r1) goto L_0x0059
            java.lang.String r9 = "lastStickersLoadTimeMask"
            long r8 = r8.getLong(r9, r2)
            goto L_0x005f
        L_0x0059:
            java.lang.String r9 = "lastStickersLoadTimeFavs"
            long r8 = r8.getLong(r9, r2)
        L_0x005f:
            long r2 = java.lang.System.currentTimeMillis()
            long r2 = r2 - r8
            long r8 = java.lang.Math.abs(r2)
            r2 = 3600000(0x36ee80, double:1.7786363E-317)
            int r4 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x0079
            if (r7 == 0) goto L_0x0074
            r5.loadingRecentGifs = r0
            goto L_0x0078
        L_0x0074:
            boolean[] r7 = r5.loadingRecentStickers
            r7[r6] = r0
        L_0x0078:
            return
        L_0x0079:
            if (r7 == 0) goto L_0x0095
            org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs r8 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r5.recentGifs
            int r9 = calcDocumentsHash(r9)
            r8.hash = r9
            org.telegram.tgnet.ConnectionsManager r9 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$GRn4qrZjcnnR99-kZpxnozqg6a4 r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$GRn4qrZjcnnR99-kZpxnozqg6a4
            r0.<init>(r6, r7)
            r9.sendRequest(r8, r0)
            goto L_0x00c8
        L_0x0095:
            r8 = 2
            if (r6 != r8) goto L_0x00a8
            org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r5.recentStickers
            r9 = r9[r6]
            int r9 = calcDocumentsHash(r9)
            r8.hash = r9
            goto L_0x00bc
        L_0x00a8:
            org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r5.recentStickers
            r9 = r9[r6]
            int r9 = calcDocumentsHash(r9)
            r8.hash = r9
            if (r6 != r1) goto L_0x00ba
            r0 = 1
        L_0x00ba:
            r8.attached = r0
        L_0x00bc:
            org.telegram.tgnet.ConnectionsManager r9 = r5.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$NotvxEUhpIVm4qpJiXI2Vq4Wdjc r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$NotvxEUhpIVm4qpJiXI2Vq4Wdjc
            r0.<init>(r6, r7)
            r9.sendRequest(r8, r0)
        L_0x00c8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$loadRecents$13$MediaDataController(boolean z, int i) {
        NativeByteBuffer byteBufferValue;
        int i2 = z ? 2 : i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + i2 + " ORDER BY date DESC", new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                if (!queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                    TLRPC.Document TLdeserialize = TLRPC.Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable(z, arrayList, i) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$12$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$12$MediaDataController(boolean z, ArrayList arrayList, int i) {
        if (z) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[i] = arrayList;
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        loadRecents(i, z, false, false);
    }

    public /* synthetic */ void lambda$loadRecents$14$MediaDataController(int i, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TLRPC.TL_messages_savedGifs ? ((TLRPC.TL_messages_savedGifs) tLObject).gifs : null, z, 0, true);
    }

    public /* synthetic */ void lambda$loadRecents$15$MediaDataController(int i, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        ArrayList<TLRPC.Document> arrayList;
        if (i == 2) {
            if (tLObject instanceof TLRPC.TL_messages_favedStickers) {
                arrayList = ((TLRPC.TL_messages_favedStickers) tLObject).stickers;
                processLoadedRecentDocuments(i, arrayList, z, 0, true);
            }
        } else if (tLObject instanceof TLRPC.TL_messages_recentStickers) {
            arrayList = ((TLRPC.TL_messages_recentStickers) tLObject).stickers;
            processLoadedRecentDocuments(i, arrayList, z, 0, true);
        }
        arrayList = null;
        processLoadedRecentDocuments(i, arrayList, z, 0, true);
    }

    /* access modifiers changed from: protected */
    public void processLoadedRecentDocuments(int i, ArrayList<TLRPC.Document> arrayList, boolean z, int i2, boolean z2) {
        if (arrayList != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(z, i, arrayList, z2, i2) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$16$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new Runnable(z, i, arrayList) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedRecentDocuments$17$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$16$MediaDataController(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3;
        int i4 = i;
        ArrayList arrayList2 = arrayList;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            if (z) {
                i3 = getMessagesController().maxRecentGifsCount;
            } else if (i4 == 2) {
                i3 = getMessagesController().maxFaveStickersCount;
            } else {
                i3 = getMessagesController().maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            int i5 = z ? 2 : i4 == 0 ? 3 : i4 == 1 ? 4 : 5;
            if (z2) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + i5).stepThis().dispose();
            }
            int i6 = 0;
            while (true) {
                if (i6 >= size) {
                    break;
                } else if (i6 == i3) {
                    break;
                } else {
                    TLRPC.Document document = (TLRPC.Document) arrayList2.get(i6);
                    executeFast.requery();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    int i7 = i6;
                    sb.append(document.id);
                    executeFast.bindString(1, sb.toString());
                    executeFast.bindInteger(2, i5);
                    executeFast.bindString(3, "");
                    executeFast.bindString(4, "");
                    executeFast.bindString(5, "");
                    executeFast.bindInteger(6, 0);
                    executeFast.bindInteger(7, 0);
                    executeFast.bindInteger(8, 0);
                    executeFast.bindInteger(9, i2 != 0 ? i2 : size - i7);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                    document.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(10, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    i6 = i7 + 1;
                }
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() >= i3) {
                database.beginTransaction();
                while (i3 < arrayList.size()) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document) arrayList2.get(i3)).id + "' AND type = " + i5).stepThis().dispose();
                    i3++;
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$17$MediaDataController(boolean z, int i, ArrayList arrayList) {
        SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (z) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            edit.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
            if (i == 0) {
                edit.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (i == 1) {
                edit.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else {
                edit.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (arrayList != null) {
            if (z) {
                this.recentGifs = arrayList;
            } else {
                this.recentStickers[i] = arrayList;
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        }
    }

    public void reorderStickers(int i, ArrayList<Long> arrayList) {
        Collections.sort(this.stickerSets[i], new Comparator(arrayList) {
            private final /* synthetic */ ArrayList f$0;

            {
                this.f$0 = r1;
            }

            public final int compare(Object obj, Object obj2) {
                return MediaDataController.lambda$reorderStickers$18(this.f$0, (TLRPC.TL_messages_stickerSet) obj, (TLRPC.TL_messages_stickerSet) obj2);
            }
        });
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    static /* synthetic */ int lambda$reorderStickers$18(ArrayList arrayList, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, TLRPC.TL_messages_stickerSet tL_messages_stickerSet2) {
        int indexOf = arrayList.indexOf(Long.valueOf(tL_messages_stickerSet.set.id));
        int indexOf2 = arrayList.indexOf(Long.valueOf(tL_messages_stickerSet2.set.id));
        if (indexOf > indexOf2) {
            return 1;
        }
        return indexOf < indexOf2 ? -1 : 0;
    }

    public void calcNewHash(int i) {
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
    }

    public void addNewStickerSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        if (this.stickerSetsById.indexOfKey(tL_messages_stickerSet.set.id) < 0 && !this.stickerSetsByName.containsKey(tL_messages_stickerSet.set.short_name)) {
            int i = tL_messages_stickerSet.set.masks;
            this.stickerSets[i].add(0, tL_messages_stickerSet);
            this.stickerSetsById.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
            this.installedStickerSetsById.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
            this.stickerSetsByName.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < tL_messages_stickerSet.documents.size(); i2++) {
                TLRPC.Document document = tL_messages_stickerSet.documents.get(i2);
                longSparseArray.put(document.id, document);
            }
            for (int i3 = 0; i3 < tL_messages_stickerSet.packs.size(); i3++) {
                TLRPC.TL_stickerPack tL_stickerPack = tL_messages_stickerSet.packs.get(i3);
                tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("️", "");
                ArrayList arrayList = this.allStickers.get(tL_stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.allStickers.put(tL_stickerPack.emoticon, arrayList);
                }
                for (int i4 = 0; i4 < tL_stickerPack.documents.size(); i4++) {
                    Long l = tL_stickerPack.documents.get(i4);
                    if (this.stickersByEmoji.indexOfKey(l.longValue()) < 0) {
                        this.stickersByEmoji.put(l.longValue(), tL_stickerPack.emoticon);
                    }
                    TLRPC.Document document2 = (TLRPC.Document) longSparseArray.get(l.longValue());
                    if (document2 != null) {
                        arrayList.add(document2);
                    }
                }
            }
            this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
            loadStickers(i, false, true);
        }
    }

    public void loadFeaturedStickers(boolean z, boolean z2) {
        int i;
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (z) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    public final void run() {
                        MediaDataController.this.lambda$loadFeaturedStickers$19$MediaDataController();
                    }
                });
                return;
            }
            TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers = new TLRPC.TL_messages_getFeaturedStickers();
            if (z2) {
                i = 0;
            } else {
                i = this.loadFeaturedHash;
            }
            tL_messages_getFeaturedStickers.hash = i;
            getConnectionsManager().sendRequest(tL_messages_getFeaturedStickers, new RequestDelegate(tL_messages_getFeaturedStickers) {
                private final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$loadFeaturedStickers$21$MediaDataController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x008d A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadFeaturedStickers$19$MediaDataController() {
        /*
            r10 = this;
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = 0
            r1 = 0
            org.telegram.messenger.MessagesStorage r3 = r10.getMessagesStorage()     // Catch:{ all -> 0x0084 }
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1"
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ all -> 0x0084 }
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch:{ all -> 0x0084 }
            boolean r4 = r3.next()     // Catch:{ all -> 0x0081 }
            if (r4 == 0) goto L_0x0077
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r1)     // Catch:{ all -> 0x0081 }
            if (r4 == 0) goto L_0x0046
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0081 }
            r5.<init>()     // Catch:{ all -> 0x0081 }
            int r0 = r4.readInt32(r1)     // Catch:{ all -> 0x0042 }
            r6 = 0
        L_0x002d:
            if (r6 >= r0) goto L_0x003d
            int r7 = r4.readInt32(r1)     // Catch:{ all -> 0x0042 }
            org.telegram.tgnet.TLRPC$StickerSetCovered r7 = org.telegram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r4, r7, r1)     // Catch:{ all -> 0x0042 }
            r5.add(r7)     // Catch:{ all -> 0x0042 }
            int r6 = r6 + 1
            goto L_0x002d
        L_0x003d:
            r4.reuse()     // Catch:{ all -> 0x0042 }
            r0 = r5
            goto L_0x0046
        L_0x0042:
            r0 = move-exception
            r4 = r0
            r0 = r5
            goto L_0x0082
        L_0x0046:
            r4 = 1
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r4)     // Catch:{ all -> 0x0081 }
            if (r4 == 0) goto L_0x0065
            int r5 = r4.readInt32(r1)     // Catch:{ all -> 0x0081 }
            r6 = 0
        L_0x0052:
            if (r6 >= r5) goto L_0x0062
            long r7 = r4.readInt64(r1)     // Catch:{ all -> 0x0081 }
            java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0081 }
            r2.add(r7)     // Catch:{ all -> 0x0081 }
            int r6 = r6 + 1
            goto L_0x0052
        L_0x0062:
            r4.reuse()     // Catch:{ all -> 0x0081 }
        L_0x0065:
            r4 = 2
            int r4 = r3.intValue(r4)     // Catch:{ all -> 0x0081 }
            int r1 = r10.calcFeaturedStickersHash(r0)     // Catch:{ all -> 0x0072 }
            r9 = r4
            r4 = r1
            r1 = r9
            goto L_0x0078
        L_0x0072:
            r5 = move-exception
            r9 = r5
            r5 = r4
            r4 = r9
            goto L_0x0088
        L_0x0077:
            r4 = 0
        L_0x0078:
            if (r3 == 0) goto L_0x007d
            r3.dispose()
        L_0x007d:
            r5 = r4
            r4 = r1
            r1 = r0
            goto L_0x0093
        L_0x0081:
            r4 = move-exception
        L_0x0082:
            r5 = 0
            goto L_0x0088
        L_0x0084:
            r3 = move-exception
            r4 = r3
            r5 = 0
            r3 = r0
        L_0x0088:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x0099 }
            if (r3 == 0) goto L_0x0090
            r3.dispose()
        L_0x0090:
            r1 = r0
            r4 = r5
            r5 = 0
        L_0x0093:
            r3 = 1
            r0 = r10
            r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5)
            return
        L_0x0099:
            r0 = move-exception
            if (r3 == 0) goto L_0x009f
            r3.dispose()
        L_0x009f:
            goto L_0x00a1
        L_0x00a0:
            throw r0
        L_0x00a1:
            goto L_0x00a0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadFeaturedStickers$19$MediaDataController():void");
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$21$MediaDataController(TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tL_messages_getFeaturedStickers) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$null$20$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$20$MediaDataController(TLObject tLObject, TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC.TL_messages_featuredStickers) {
            TLRPC.TL_messages_featuredStickers tL_messages_featuredStickers = (TLRPC.TL_messages_featuredStickers) tLObject2;
            processLoadedFeaturedStickers(tL_messages_featuredStickers.sets, tL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, false, (int) (System.currentTimeMillis() / 1000), tL_messages_getFeaturedStickers.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<TLRPC.StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$22$MediaDataController();
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable(z, arrayList, i, i2, arrayList2) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ ArrayList f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedFeaturedStickers$26$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$22$MediaDataController() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$26$MediaDataController(boolean z, ArrayList arrayList, int i, int i2, ArrayList arrayList2) {
        long j = 1000;
        if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
            $$Lambda$MediaDataController$EdbyXOjkKGNenVPZfLO4iUglCbc r2 = new Runnable(arrayList, i2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$23$MediaDataController(this.f$1, this.f$2);
                }
            };
            if (arrayList != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(r2, j);
            if (arrayList == null) {
                return;
            }
        }
        if (arrayList != null) {
            try {
                ArrayList arrayList3 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) arrayList.get(i3);
                    arrayList3.add(stickerSetCovered);
                    longSparseArray.put(stickerSetCovered.set.id, stickerSetCovered);
                }
                if (!z) {
                    putFeaturedStickersToCache(arrayList3, arrayList2, i, i2);
                }
                AndroidUtilities.runOnUIThread(new Runnable(arrayList2, longSparseArray, arrayList3, i2, i) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ LongSparseArray f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ int f$4;
                    private final /* synthetic */ int f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$24$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$25$MediaDataController(this.f$1);
                }
            });
            putFeaturedStickersToCache((ArrayList<TLRPC.StickerSetCovered>) null, (ArrayList<Long>) null, i, 0);
        }
    }

    public /* synthetic */ void lambda$null$23$MediaDataController(ArrayList arrayList, int i) {
        if (!(arrayList == null || i == 0)) {
            this.loadFeaturedHash = i;
        }
        loadFeaturedStickers(false, false);
    }

    public /* synthetic */ void lambda$null$24$MediaDataController(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, int i, int i2) {
        this.unreadStickerSets = arrayList;
        this.featuredStickerSetsById = longSparseArray;
        this.featuredStickerSets = arrayList2;
        this.loadFeaturedHash = i;
        this.loadFeaturedDate = i2;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$null$25$MediaDataController(int i) {
        this.loadFeaturedDate = i;
    }

    private void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, i2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putFeaturedStickersToCache$27$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$putFeaturedStickersToCache$27$MediaDataController(ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC.StickerSetCovered) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList2.size() * 8) + 4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC.StickerSetCovered) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                nativeByteBuffer2.writeInt32(arrayList2.size());
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    nativeByteBuffer2.writeInt64(((Long) arrayList2.get(i6)).longValue());
                }
                executeFast.bindInteger(1, 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindByteBuffer(3, nativeByteBuffer2);
                executeFast.bindInteger(4, i);
                executeFast.bindInteger(5, i2);
                executeFast.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    private int calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.StickerSet stickerSet = arrayList.get(i).set;
            if (!stickerSet.archived) {
                long j2 = stickerSet.id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
                if (this.unreadStickerSets.contains(Long.valueOf(j2))) {
                    j = (((j * 20261) + 2147483648L) + 1) % 2147483648L;
                }
            }
        }
        return (int) j;
    }

    public void markFaturedStickersAsRead(boolean z) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (z) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_readFeaturedStickers(), $$Lambda$MediaDataController$IXjG5rWXs9Rasb_rX9lsPENNKyw.INSTANCE);
            }
        }
    }

    public int getFeaturesStickersHashWithoutUnread() {
        long j = 0;
        for (int i = 0; i < this.featuredStickerSets.size(); i++) {
            TLRPC.StickerSet stickerSet = this.featuredStickerSets.get(i).set;
            if (!stickerSet.archived) {
                long j2 = stickerSet.id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
        }
        return (int) j;
    }

    public void markFaturedStickersByIdAsRead(long j) {
        if (this.unreadStickerSets.contains(Long.valueOf(j)) && !this.readingStickerSets.contains(Long.valueOf(j))) {
            this.readingStickerSets.add(Long.valueOf(j));
            TLRPC.TL_messages_readFeaturedStickers tL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
            tL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
            getConnectionsManager().sendRequest(tL_messages_readFeaturedStickers, $$Lambda$MediaDataController$qEygjg7VUDQOR7MrWcYE_q6DKjQ.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable(j) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$markFaturedStickersByIdAsRead$30$MediaDataController(this.f$1);
                }
            }, 1000);
        }
    }

    public /* synthetic */ void lambda$markFaturedStickersByIdAsRead$30$MediaDataController(long j) {
        this.unreadStickerSets.remove(Long.valueOf(j));
        this.readingStickerSets.remove(Long.valueOf(j));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
    }

    public int getArchivedStickersCount(int i) {
        return this.archivedStickersCount[i];
    }

    public void loadArchivedStickersCount(int i, boolean z) {
        boolean z2 = true;
        if (z) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            int i2 = notificationsSettings.getInt("archivedStickersCount" + i, -1);
            if (i2 == -1) {
                loadArchivedStickersCount(i, false);
                return;
            }
            this.archivedStickersCount[i] = i2;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
            return;
        }
        TLRPC.TL_messages_getArchivedStickers tL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
        tL_messages_getArchivedStickers.limit = 0;
        if (i != 1) {
            z2 = false;
        }
        tL_messages_getArchivedStickers.masks = z2;
        getConnectionsManager().sendRequest(tL_messages_getArchivedStickers, new RequestDelegate(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$loadArchivedStickersCount$32$MediaDataController(this.f$1, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$32$MediaDataController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, i) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$31$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$31$MediaDataController(TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        if (tL_error == null) {
            TLRPC.TL_messages_archivedStickers tL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tL_messages_archivedStickers.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + i, tL_messages_archivedStickers.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(int i, TLRPC.TL_messages_allStickers tL_messages_allStickers) {
        TLRPC.TL_messages_allStickers tL_messages_allStickers2 = tL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (tL_messages_allStickers2.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers2.hash);
        } else {
            LongSparseArray longSparseArray = new LongSparseArray();
            int i2 = 0;
            while (i2 < tL_messages_allStickers2.sets.size()) {
                TLRPC.StickerSet stickerSet = tL_messages_allStickers2.sets.get(i2);
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSetsById.get(stickerSet.id);
                if (tL_messages_stickerSet != null) {
                    TLRPC.StickerSet stickerSet2 = tL_messages_stickerSet.set;
                    if (stickerSet2.hash == stickerSet.hash) {
                        stickerSet2.archived = stickerSet.archived;
                        stickerSet2.installed = stickerSet.installed;
                        stickerSet2.official = stickerSet.official;
                        longSparseArray.put(stickerSet2.id, tL_messages_stickerSet);
                        arrayList.add(tL_messages_stickerSet);
                        if (longSparseArray.size() == tL_messages_allStickers2.sets.size()) {
                            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers2.hash);
                        }
                        i2++;
                        j = 1000;
                    }
                }
                arrayList.add((Object) null);
                TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = new TLRPC.TL_inputStickerSetID();
                TLRPC.InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
                inputStickerSet.id = stickerSet.id;
                inputStickerSet.access_hash = stickerSet.access_hash;
                $$Lambda$MediaDataController$nb2ivmGgBA2rs303prGbd4fGXaY r10 = r0;
                ConnectionsManager connectionsManager = getConnectionsManager();
                $$Lambda$MediaDataController$nb2ivmGgBA2rs303prGbd4fGXaY r0 = new RequestDelegate(arrayList, i2, longSparseArray, stickerSet, tL_messages_allStickers, i) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ LongSparseArray f$3;
                    private final /* synthetic */ TLRPC.StickerSet f$4;
                    private final /* synthetic */ TLRPC.TL_messages_allStickers f$5;
                    private final /* synthetic */ int f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$processLoadStickersResponse$34$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
                    }
                };
                connectionsManager.sendRequest(tL_messages_getStickerSet, r10);
                i2++;
                j = 1000;
            }
        }
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$34$MediaDataController(ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers tL_messages_allStickers, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, arrayList, i, longSparseArray, stickerSet, tL_messages_allStickers, i2) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ LongSparseArray f$4;
            private final /* synthetic */ TLRPC.StickerSet f$5;
            private final /* synthetic */ TLRPC.TL_messages_allStickers f$6;
            private final /* synthetic */ int f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MediaDataController.this.lambda$null$33$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$33$MediaDataController(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC.StickerSet stickerSet, TLRPC.TL_messages_allStickers tL_messages_allStickers, int i2) {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) tLObject;
        arrayList.set(i, tL_messages_stickerSet);
        longSparseArray.put(stickerSet.id, tL_messages_stickerSet);
        if (longSparseArray.size() == tL_messages_allStickers.sets.size()) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                if (arrayList.get(i3) == null) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
            processLoadedStickers(i2, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getAllStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadStickers(int r5, boolean r6, boolean r7) {
        /*
            r4 = this;
            boolean[] r0 = r4.loadingStickers
            boolean r0 = r0[r5]
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            r0 = 4
            r1 = 3
            if (r5 != r1) goto L_0x001c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r4.featuredStickerSets
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x001b
            org.telegram.messenger.MessagesController r2 = r4.getMessagesController()
            boolean r2 = r2.preloadFeaturedStickers
            if (r2 != 0) goto L_0x0021
        L_0x001b:
            return
        L_0x001c:
            if (r5 == r0) goto L_0x0021
            r4.loadArchivedStickersCount(r5, r6)
        L_0x0021:
            boolean[] r2 = r4.loadingStickers
            r3 = 1
            r2[r5] = r3
            if (r6 == 0) goto L_0x003a
            org.telegram.messenger.MessagesStorage r6 = r4.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r6 = r6.getStorageQueue()
            org.telegram.messenger.-$$Lambda$MediaDataController$N2Bp9an5INgmY1R2JdhUmdI57xQ r7 = new org.telegram.messenger.-$$Lambda$MediaDataController$N2Bp9an5INgmY1R2JdhUmdI57xQ
            r7.<init>(r5)
            r6.postRunnable(r7)
            goto L_0x00aa
        L_0x003a:
            r6 = 0
            if (r5 != r1) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_messages_allStickers r7 = new org.telegram.tgnet.TLRPC$TL_messages_allStickers
            r7.<init>()
            int r0 = r4.loadFeaturedHash
            r7.hash = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r0 = r4.featuredStickerSets
            int r0 = r0.size()
        L_0x004c:
            if (r6 >= r0) goto L_0x0060
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSet> r1 = r7.sets
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r4.featuredStickerSets
            java.lang.Object r2 = r2.get(r6)
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            r1.add(r2)
            int r6 = r6 + 1
            goto L_0x004c
        L_0x0060:
            r4.processLoadStickersResponse(r5, r7)
            goto L_0x00aa
        L_0x0064:
            if (r5 != r0) goto L_0x007f
            org.telegram.tgnet.TLRPC$TL_messages_getStickerSet r6 = new org.telegram.tgnet.TLRPC$TL_messages_getStickerSet
            r6.<init>()
            org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji r7 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji
            r7.<init>()
            r6.stickerset = r7
            org.telegram.tgnet.ConnectionsManager r7 = r4.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$e7VYFMt5Abaes--3EuhTlbUjVjk r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$e7VYFMt5Abaes--3EuhTlbUjVjk
            r0.<init>(r5)
            r7.sendRequest(r6, r0)
            goto L_0x00aa
        L_0x007f:
            if (r5 != 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$TL_messages_getAllStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getAllStickers
            r0.<init>()
            if (r7 == 0) goto L_0x0089
            goto L_0x008d
        L_0x0089:
            int[] r6 = r4.loadHash
            r6 = r6[r5]
        L_0x008d:
            r0.hash = r6
            goto L_0x009e
        L_0x0090:
            org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers
            r0.<init>()
            if (r7 == 0) goto L_0x0098
            goto L_0x009c
        L_0x0098:
            int[] r6 = r4.loadHash
            r6 = r6[r5]
        L_0x009c:
            r0.hash = r6
        L_0x009e:
            org.telegram.tgnet.ConnectionsManager r7 = r4.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$_e3071bDREkcEstsnYOI_zA7IoM r1 = new org.telegram.messenger.-$$Lambda$MediaDataController$_e3071bDREkcEstsnYOI_zA7IoM
            r1.<init>(r5, r6)
            r7.sendRequest(r0, r1)
        L_0x00aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadStickers(int, boolean, boolean):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x007e A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadStickers$35$MediaDataController(int r13) {
        /*
            r12 = this;
            r0 = 0
            r1 = 0
            org.telegram.messenger.MessagesStorage r2 = r12.getMessagesStorage()     // Catch:{ all -> 0x0076 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ all -> 0x0076 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0076 }
            r3.<init>()     // Catch:{ all -> 0x0076 }
            java.lang.String r4 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r3.append(r4)     // Catch:{ all -> 0x0076 }
            int r4 = r13 + 1
            r3.append(r4)     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0076 }
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch:{ all -> 0x0076 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ all -> 0x0076 }
            boolean r3 = r2.next()     // Catch:{ all -> 0x0071 }
            if (r3 == 0) goto L_0x0067
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r1)     // Catch:{ all -> 0x0071 }
            if (r3 == 0) goto L_0x0054
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0071 }
            r4.<init>()     // Catch:{ all -> 0x0071 }
            int r0 = r3.readInt32(r1)     // Catch:{ all -> 0x004e }
            r5 = 0
        L_0x0039:
            if (r5 >= r0) goto L_0x0049
            int r6 = r3.readInt32(r1)     // Catch:{ all -> 0x004e }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r3, r6, r1)     // Catch:{ all -> 0x004e }
            r4.add(r6)     // Catch:{ all -> 0x004e }
            int r5 = r5 + 1
            goto L_0x0039
        L_0x0049:
            r3.reuse()     // Catch:{ all -> 0x004e }
            r0 = r4
            goto L_0x0054
        L_0x004e:
            r0 = move-exception
            r3 = 0
            r11 = r2
            r2 = r0
            r0 = r11
            goto L_0x0079
        L_0x0054:
            r3 = 1
            int r3 = r2.intValue(r3)     // Catch:{ all -> 0x0071 }
            int r1 = calcStickersHash(r0)     // Catch:{ all -> 0x0061 }
            r11 = r3
            r3 = r1
            r1 = r11
            goto L_0x0068
        L_0x0061:
            r4 = move-exception
            r11 = r4
            r4 = r0
            r0 = r2
            r2 = r11
            goto L_0x0079
        L_0x0067:
            r3 = 0
        L_0x0068:
            if (r2 == 0) goto L_0x006d
            r2.dispose()
        L_0x006d:
            r7 = r0
            r9 = r1
            r10 = r3
            goto L_0x0084
        L_0x0071:
            r3 = move-exception
            r4 = r0
            r0 = r2
            r2 = r3
            goto L_0x0078
        L_0x0076:
            r2 = move-exception
            r4 = r0
        L_0x0078:
            r3 = 0
        L_0x0079:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x008b }
            if (r0 == 0) goto L_0x0081
            r0.dispose()
        L_0x0081:
            r9 = r3
            r7 = r4
            r10 = 0
        L_0x0084:
            r8 = 1
            r5 = r12
            r6 = r13
            r5.processLoadedStickers(r6, r7, r8, r9, r10)
            return
        L_0x008b:
            r13 = move-exception
            if (r0 == 0) goto L_0x0091
            r0.dispose()
        L_0x0091:
            goto L_0x0093
        L_0x0092:
            throw r13
        L_0x0093:
            goto L_0x0092
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickers$35$MediaDataController(int):void");
    }

    public /* synthetic */ void lambda$loadStickers$36$MediaDataController(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            ArrayList arrayList = new ArrayList();
            arrayList.add((TLRPC.TL_messages_stickerSet) tLObject);
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(arrayList));
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), 0);
    }

    public /* synthetic */ void lambda$loadStickers$38$MediaDataController(int i, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, i2) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$null$37$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$37$MediaDataController(TLObject tLObject, int i, int i2) {
        if (tLObject instanceof TLRPC.TL_messages_allStickers) {
            processLoadStickersResponse(i, (TLRPC.TL_messages_allStickers) tLObject);
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC.TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), i2);
    }

    private void putStickersToCache(int i, ArrayList<TLRPC.TL_messages_stickerSet> arrayList, int i2, int i3) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList != null ? new ArrayList(arrayList) : null, i, i2, i3) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putStickersToCache$39$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$putStickersToCache$39$MediaDataController(ArrayList arrayList, int i, int i2, int i3) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                executeFast.requery();
                int i4 = 4;
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    i4 += ((TLRPC.TL_messages_stickerSet) arrayList.get(i5)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i6 = 0; i6 < arrayList.size(); i6++) {
                    ((TLRPC.TL_messages_stickerSet) arrayList.get(i6)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindInteger(1, i + 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i2);
                executeFast.bindInteger(4, i3);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i2);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public String getStickerSetName(long j) {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSetsById.get(j);
        if (tL_messages_stickerSet != null) {
            return tL_messages_stickerSet.set.short_name;
        }
        TLRPC.StickerSetCovered stickerSetCovered = this.featuredStickerSetsById.get(j);
        if (stickerSetCovered != null) {
            return stickerSetCovered.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                    return inputStickerSet.id;
                }
                return -1;
            }
        }
        return -1;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet;
            }
        }
        return null;
    }

    private static int calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.StickerSet stickerSet = arrayList.get(i).set;
            if (!stickerSet.archived) {
                j = (((j * 20261) + 2147483648L) + ((long) stickerSet.hash)) % 2147483648L;
            }
        }
        return (int) j;
    }

    private void processLoadedStickers(int i, ArrayList<TLRPC.TL_messages_stickerSet> arrayList, boolean z, int i2, int i3) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$40$MediaDataController(this.f$1);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable(z, arrayList, i2, i3, i) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedStickers$44$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedStickers$40$MediaDataController(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
    }

    public /* synthetic */ void lambda$processLoadedStickers$44$MediaDataController(boolean z, ArrayList arrayList, int i, int i2, int i3) {
        HashMap hashMap;
        HashMap hashMap2;
        LongSparseArray longSparseArray;
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        HashMap hashMap3;
        ArrayList arrayList2 = arrayList;
        int i4 = i;
        int i5 = i2;
        int i6 = i3;
        long j = 1000;
        if ((z && (arrayList2 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i4)) >= 3600)) || (!z && arrayList2 == null && i5 == 0)) {
            $$Lambda$MediaDataController$pe69kJ3Q580UaNbNh_XIjlzflYI r4 = new Runnable(arrayList2, i5, i6) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$41$MediaDataController(this.f$1, this.f$2, this.f$3);
                }
            };
            if (arrayList2 != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(r4, j);
            if (arrayList2 == null) {
                return;
            }
        }
        if (arrayList2 != null) {
            try {
                ArrayList arrayList3 = new ArrayList();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                HashMap hashMap4 = new HashMap();
                LongSparseArray longSparseArray3 = new LongSparseArray();
                LongSparseArray longSparseArray4 = new LongSparseArray();
                HashMap hashMap5 = new HashMap();
                int i7 = 0;
                while (i7 < arrayList.size()) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) arrayList2.get(i7);
                    if (tL_messages_stickerSet2 != null) {
                        arrayList3.add(tL_messages_stickerSet2);
                        longSparseArray2.put(tL_messages_stickerSet2.set.id, tL_messages_stickerSet2);
                        hashMap4.put(tL_messages_stickerSet2.set.short_name, tL_messages_stickerSet2);
                        int i8 = 0;
                        while (i8 < tL_messages_stickerSet2.documents.size()) {
                            TLRPC.Document document = tL_messages_stickerSet2.documents.get(i8);
                            if (document != null) {
                                if (!(document instanceof TLRPC.TL_documentEmpty)) {
                                    longSparseArray4.put(document.id, document);
                                }
                            }
                            i8++;
                            ArrayList arrayList4 = arrayList;
                        }
                        if (!tL_messages_stickerSet2.set.archived) {
                            int i9 = 0;
                            while (i9 < tL_messages_stickerSet2.packs.size()) {
                                TLRPC.TL_stickerPack tL_stickerPack = tL_messages_stickerSet2.packs.get(i9);
                                if (tL_stickerPack != null) {
                                    if (tL_stickerPack.emoticon != null) {
                                        tL_messages_stickerSet = tL_messages_stickerSet2;
                                        tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("️", "");
                                        ArrayList arrayList5 = (ArrayList) hashMap5.get(tL_stickerPack.emoticon);
                                        if (arrayList5 == null) {
                                            arrayList5 = new ArrayList();
                                            hashMap5.put(tL_stickerPack.emoticon, arrayList5);
                                        }
                                        int i10 = 0;
                                        while (i10 < tL_stickerPack.documents.size()) {
                                            Long l = tL_stickerPack.documents.get(i10);
                                            LongSparseArray longSparseArray5 = longSparseArray2;
                                            HashMap hashMap6 = hashMap4;
                                            if (longSparseArray3.indexOfKey(l.longValue()) < 0) {
                                                hashMap3 = hashMap5;
                                                longSparseArray3.put(l.longValue(), tL_stickerPack.emoticon);
                                            } else {
                                                hashMap3 = hashMap5;
                                            }
                                            TLRPC.Document document2 = (TLRPC.Document) longSparseArray4.get(l.longValue());
                                            if (document2 != null) {
                                                arrayList5.add(document2);
                                            }
                                            i10++;
                                            longSparseArray2 = longSparseArray5;
                                            hashMap4 = hashMap6;
                                            hashMap5 = hashMap3;
                                        }
                                        longSparseArray = longSparseArray2;
                                        hashMap2 = hashMap4;
                                        hashMap = hashMap5;
                                        i9++;
                                        tL_messages_stickerSet2 = tL_messages_stickerSet;
                                        longSparseArray2 = longSparseArray;
                                        hashMap4 = hashMap2;
                                        hashMap5 = hashMap;
                                    }
                                }
                                longSparseArray = longSparseArray2;
                                hashMap2 = hashMap4;
                                hashMap = hashMap5;
                                tL_messages_stickerSet = tL_messages_stickerSet2;
                                i9++;
                                tL_messages_stickerSet2 = tL_messages_stickerSet;
                                longSparseArray2 = longSparseArray;
                                hashMap4 = hashMap2;
                                hashMap5 = hashMap;
                            }
                        }
                    }
                    i7++;
                    arrayList2 = arrayList;
                    longSparseArray2 = longSparseArray2;
                    hashMap4 = hashMap4;
                    hashMap5 = hashMap5;
                }
                LongSparseArray longSparseArray6 = longSparseArray2;
                HashMap hashMap7 = hashMap4;
                HashMap hashMap8 = hashMap5;
                if (!z) {
                    putStickersToCache(i6, arrayList3, i4, i5);
                }
                AndroidUtilities.runOnUIThread(new Runnable(i3, longSparseArray6, hashMap7, arrayList3, i2, i, longSparseArray4, hashMap8, longSparseArray3) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ LongSparseArray f$2;
                    private final /* synthetic */ HashMap f$3;
                    private final /* synthetic */ ArrayList f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ int f$6;
                    private final /* synthetic */ LongSparseArray f$7;
                    private final /* synthetic */ HashMap f$8;
                    private final /* synthetic */ LongSparseArray f$9;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                        this.f$8 = r9;
                        this.f$9 = r10;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$42$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new Runnable(i6, i4) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$43$MediaDataController(this.f$1, this.f$2);
                }
            });
            putStickersToCache(i6, (ArrayList<TLRPC.TL_messages_stickerSet>) null, i4, 0);
        }
    }

    public /* synthetic */ void lambda$null$41$MediaDataController(ArrayList arrayList, int i, int i2) {
        if (!(arrayList == null || i == 0)) {
            this.loadHash[i2] = i;
        }
        loadStickers(i2, false, false);
    }

    public /* synthetic */ void lambda$null$42$MediaDataController(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, int i2, int i3, LongSparseArray longSparseArray2, HashMap hashMap2, LongSparseArray longSparseArray3) {
        int i4 = i;
        LongSparseArray longSparseArray4 = longSparseArray;
        HashMap hashMap3 = hashMap2;
        for (int i5 = 0; i5 < this.stickerSets[i4].size(); i5++) {
            TLRPC.StickerSet stickerSet = this.stickerSets[i4].get(i5).set;
            this.stickerSetsById.remove(stickerSet.id);
            this.stickerSetsByName.remove(stickerSet.short_name);
            if (!(i4 == 3 || i4 == 4)) {
                this.installedStickerSetsById.remove(stickerSet.id);
            }
        }
        for (int i6 = 0; i6 < longSparseArray.size(); i6++) {
            this.stickerSetsById.put(longSparseArray.keyAt(i6), longSparseArray.valueAt(i6));
            if (!(i4 == 3 || i4 == 4)) {
                this.installedStickerSetsById.put(longSparseArray.keyAt(i6), longSparseArray.valueAt(i6));
            }
        }
        HashMap hashMap4 = hashMap;
        this.stickerSetsByName.putAll(hashMap);
        this.stickerSets[i4] = arrayList;
        this.loadHash[i4] = i2;
        this.loadDate[i4] = i3;
        this.stickersByIds[i4] = longSparseArray2;
        if (i4 == 0) {
            this.allStickers = hashMap3;
            this.stickersByEmoji = longSparseArray3;
        } else if (i4 == 3) {
            this.allStickersFeatured = hashMap3;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$43$MediaDataController(int i, int i2) {
        this.loadDate[i] = i2;
    }

    public void removeStickersSet(Context context, TLRPC.StickerSet stickerSet, int i, BaseFragment baseFragment, boolean z) {
        int i2 = stickerSet.masks;
        TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
        tL_inputStickerSetID.access_hash = stickerSet.access_hash;
        tL_inputStickerSetID.id = stickerSet.id;
        if (i != 0) {
            boolean z2 = false;
            stickerSet.archived = i == 1;
            int i3 = 0;
            while (true) {
                if (i3 >= this.stickerSets[i2].size()) {
                    break;
                }
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSets[i2].get(i3);
                if (tL_messages_stickerSet.set.id == stickerSet.id) {
                    this.stickerSets[i2].remove(i3);
                    if (i == 2) {
                        this.stickerSets[i2].add(0, tL_messages_stickerSet);
                    } else {
                        this.stickerSetsById.remove(tL_messages_stickerSet.set.id);
                        this.installedStickerSetsById.remove(tL_messages_stickerSet.set.id);
                        this.stickerSetsByName.remove(tL_messages_stickerSet.set.short_name);
                    }
                } else {
                    i3++;
                }
            }
            this.loadHash[i2] = calcStickersHash(this.stickerSets[i2]);
            putStickersToCache(i2, this.stickerSets[i2], this.loadDate[i2], this.loadHash[i2]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i2));
            TLRPC.TL_messages_installStickerSet tL_messages_installStickerSet = new TLRPC.TL_messages_installStickerSet();
            tL_messages_installStickerSet.stickerset = tL_inputStickerSetID;
            if (i == 1) {
                z2 = true;
            }
            tL_messages_installStickerSet.archived = z2;
            getConnectionsManager().sendRequest(tL_messages_installStickerSet, new RequestDelegate(i2, i, baseFragment, z) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;
                private final /* synthetic */ boolean f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$removeStickersSet$46$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_messages_uninstallStickerSet tL_messages_uninstallStickerSet = new TLRPC.TL_messages_uninstallStickerSet();
        tL_messages_uninstallStickerSet.stickerset = tL_inputStickerSetID;
        getConnectionsManager().sendRequest(tL_messages_uninstallStickerSet, new RequestDelegate(stickerSet, context, i2) {
            private final /* synthetic */ TLRPC.StickerSet f$1;
            private final /* synthetic */ Context f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MediaDataController.this.lambda$removeStickersSet$48$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$removeStickersSet$46$MediaDataController(int i, int i2, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, i2, baseFragment, z) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ BaseFragment f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaDataController.this.lambda$null$45$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
        loadStickers(i, false, false);
    }

    public /* synthetic */ void lambda$null$45$MediaDataController(TLObject tLObject, int i, int i2, BaseFragment baseFragment, boolean z) {
        if (tLObject instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
            getNotificationCenter().postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(i));
            if (i2 != 1 && baseFragment != null && baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), z ? baseFragment : null, ((TLRPC.TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
            }
        }
    }

    public /* synthetic */ void lambda$removeStickersSet$48$MediaDataController(TLRPC.StickerSet stickerSet, Context context, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, stickerSet, context, i) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLRPC.StickerSet f$2;
            private final /* synthetic */ Context f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$null$47$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$47$MediaDataController(TLRPC.TL_error tL_error, TLRPC.StickerSet stickerSet, Context context, int i) {
        if (tL_error == null) {
            try {
                if (stickerSet.masks) {
                    Toast.makeText(context, LocaleController.getString("MasksRemoved", NUM), 0).show();
                } else {
                    Toast.makeText(context, LocaleController.getString("StickersRemoved", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            Toast.makeText(context, LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
        loadStickers(i, false, true);
    }

    private int getMask() {
        int i = 1;
        if (this.lastReturnedNum >= this.searchResultMessages.size() - 1) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && zArr[1]) {
                i = 0;
            }
        }
        return this.lastReturnedNum > 0 ? i | 2 : i;
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
            r3 = r0[r3]
            int r2 = r3.indexOfKey(r2)
            if (r2 < 0) goto L_0x000c
            r2 = 1
            goto L_0x000d
        L_0x000c:
            r2 = 0
        L_0x000d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.isMessageFound(int, boolean):boolean");
    }

    public void searchMessagesInChat(String str, long j, long j2, int i, int i2, TLRPC.User user) {
        searchMessagesInChat(str, j, j2, i, i2, false, user, true);
    }

    public void jumpToSearchedMessage(int i, int i2) {
        if (i2 >= 0 && i2 < this.searchResultMessages.size()) {
            this.lastReturnedNum = i2;
            MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i3 = NotificationCenter.chatSearchResultsAvailable;
            int[] iArr = this.messagesSearchCount;
            notificationCenter.postNotificationName(i3, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), true);
        }
    }

    public void loadMoreSearchMessages() {
        if (!this.loadingMoreSearchMessages) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (!zArr[0] || this.lastMergeDialogId != 0 || !zArr[1]) {
                int size = this.searchResultMessages.size();
                this.lastReturnedNum = this.searchResultMessages.size();
                searchMessagesInChat((String) null, this.lastDialogId, this.lastMergeDialogId, this.lastGuid, 1, false, this.lastSearchUser, false);
                this.lastReturnedNum = size;
                this.loadingMoreSearchMessages = true;
            }
        }
    }

    private void searchMessagesInChat(String str, long j, long j2, int i, int i2, boolean z, TLRPC.User user, boolean z2) {
        int i3;
        long j3;
        String str2;
        long j4;
        String str3;
        int i4;
        long j5 = j;
        long j6 = j2;
        int i5 = i2;
        TLRPC.User user2 = user;
        boolean z3 = !z;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (str != null) {
            if (z3) {
                boolean[] zArr = this.messagesSearchEndReached;
                zArr[1] = false;
                zArr[0] = false;
                int[] iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
            }
            str2 = str;
            j3 = j5;
            i3 = 0;
        } else if (!this.searchResultMessages.isEmpty()) {
            if (i5 == 1) {
                this.lastReturnedNum++;
                if (this.lastReturnedNum < this.searchResultMessages.size()) {
                    MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i6 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr2 = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i6, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]), Boolean.valueOf(z2));
                    return;
                }
                boolean[] zArr2 = this.messagesSearchEndReached;
                if (!zArr2[0] || j6 != 0 || !zArr2[1]) {
                    String str4 = this.lastSearchQuery;
                    ArrayList<MessageObject> arrayList = this.searchResultMessages;
                    MessageObject messageObject2 = arrayList.get(arrayList.size() - 1);
                    if (messageObject2.getDialogId() != j5 || this.messagesSearchEndReached[0]) {
                        i4 = messageObject2.getDialogId() == j6 ? messageObject2.getId() : 0;
                        this.messagesSearchEndReached[1] = false;
                        j3 = j6;
                    } else {
                        i4 = messageObject2.getId();
                        j3 = j5;
                    }
                    i3 = i4;
                    str2 = str4;
                    z3 = false;
                } else {
                    this.lastReturnedNum--;
                    return;
                }
            } else if (i5 == 2) {
                this.lastReturnedNum--;
                int i7 = this.lastReturnedNum;
                if (i7 < 0) {
                    this.lastReturnedNum = 0;
                    return;
                }
                if (i7 >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject3 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i8 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr3 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i8, Integer.valueOf(i), Integer.valueOf(messageObject3.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject3.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr3[0] + iArr3[1]), Boolean.valueOf(z2));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!zArr3[0] || zArr3[1]) {
            j4 = 0;
        } else {
            j4 = 0;
            if (j6 != 0) {
                j3 = j6;
            }
        }
        if (j3 == j5 && z3) {
            if (j6 != j4) {
                TLRPC.InputPeer inputPeer = getMessagesController().getInputPeer((int) j6);
                if (inputPeer != null) {
                    TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
                    tL_messages_search.peer = inputPeer;
                    this.lastMergeDialogId = j6;
                    tL_messages_search.limit = 1;
                    if (str2 == null) {
                        str2 = "";
                    }
                    tL_messages_search.q = str2;
                    if (user2 != null) {
                        tL_messages_search.from_id = getMessagesController().getInputUser(user2);
                        tL_messages_search.flags = 1 | tL_messages_search.flags;
                    }
                    tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
                    $$Lambda$MediaDataController$w7TOSpkl2l6evzSTxp6XN931MU r11 = r0;
                    $$Lambda$MediaDataController$w7TOSpkl2l6evzSTxp6XN931MU r0 = new RequestDelegate(j2, tL_messages_search, j, i, i2, user, z2) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ TLRPC.TL_messages_search f$2;
                        private final /* synthetic */ long f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ int f$5;
                        private final /* synthetic */ TLRPC.User f$6;
                        private final /* synthetic */ boolean f$7;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                            this.f$3 = r5;
                            this.f$4 = r7;
                            this.f$5 = r8;
                            this.f$6 = r9;
                            this.f$7 = r10;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.lambda$searchMessagesInChat$50$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
                        }
                    };
                    this.mergeReqId = getConnectionsManager().sendRequest(tL_messages_search, r11, 2);
                    return;
                }
                return;
            }
            this.lastMergeDialogId = 0;
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLRPC.TL_messages_search tL_messages_search2 = new TLRPC.TL_messages_search();
        tL_messages_search2.peer = getMessagesController().getInputPeer((int) j3);
        if (tL_messages_search2.peer != null) {
            this.lastGuid = i;
            this.lastDialogId = j5;
            this.lastSearchUser = user2;
            tL_messages_search2.limit = 21;
            if (str2 != null) {
                str3 = str2;
            } else {
                str3 = "";
            }
            tL_messages_search2.q = str3;
            tL_messages_search2.offset_id = i3;
            if (user2 != null) {
                tL_messages_search2.from_id = getMessagesController().getInputUser(user2);
                tL_messages_search2.flags |= 1;
            }
            tL_messages_search2.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            int i9 = this.lastReqId + 1;
            this.lastReqId = i9;
            this.lastSearchQuery = str2;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$MediaDataController$ZoVPUyusoxq6AeVwCvLbTk3HaM r13 = r0;
            $$Lambda$MediaDataController$ZoVPUyusoxq6AeVwCvLbTk3HaM r02 = new RequestDelegate(i9, z2, tL_messages_search2, j3, j, i, j2, user) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC.TL_messages_search f$3;
                private final /* synthetic */ long f$4;
                private final /* synthetic */ long f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ long f$7;
                private final /* synthetic */ TLRPC.User f$8;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r7;
                    this.f$6 = r9;
                    this.f$7 = r10;
                    this.f$8 = r12;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$searchMessagesInChat$52$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
                }
            };
            this.reqId = connectionsManager.sendRequest(tL_messages_search2, r13, 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$50$MediaDataController(long j, TLRPC.TL_messages_search tL_messages_search, long j2, int i, int i2, TLRPC.User user, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(j, tLObject, tL_messages_search, j2, i, i2, user, z) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_messages_search f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ TLRPC.User f$7;
            private final /* synthetic */ boolean f$8;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r8;
                this.f$6 = r9;
                this.f$7 = r10;
                this.f$8 = r11;
            }

            public final void run() {
                MediaDataController.this.lambda$null$49$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$49$MediaDataController(long j, TLObject tLObject, TLRPC.TL_messages_search tL_messages_search, long j2, int i, int i2, TLRPC.User user, boolean z) {
        if (this.lastMergeDialogId == j) {
            this.mergeReqId = 0;
            if (tLObject != null) {
                TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                this.messagesSearchEndReached[1] = messages_messages.messages.isEmpty();
                this.messagesSearchCount[1] = messages_messages instanceof TLRPC.TL_messages_messagesSlice ? messages_messages.count : messages_messages.messages.size();
                searchMessagesInChat(tL_messages_search.q, j2, j, i, i2, true, user, z);
            }
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$52$MediaDataController(int i, boolean z, TLRPC.TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, z, tLObject, tL_messages_search, j, j2, i2, j3, user) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ TLRPC.TL_messages_search f$4;
            private final /* synthetic */ long f$5;
            private final /* synthetic */ long f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ long f$8;
            private final /* synthetic */ TLRPC.User f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r8;
                this.f$7 = r10;
                this.f$8 = r11;
                this.f$9 = r13;
            }

            public final void run() {
                MediaDataController.this.lambda$null$51$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
            }
        });
    }

    public /* synthetic */ void lambda$null$51$MediaDataController(int i, boolean z, TLObject tLObject, TLRPC.TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, TLRPC.User user) {
        if (i == this.lastReqId) {
            this.reqId = 0;
            if (!z) {
                this.loadingMoreSearchMessages = false;
            }
            if (tLObject != null) {
                TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                int i3 = 0;
                while (i3 < messages_messages.messages.size()) {
                    TLRPC.Message message = messages_messages.messages.get(i3);
                    if ((message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                        messages_messages.messages.remove(i3);
                        i3--;
                    }
                    i3++;
                }
                getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                getMessagesController().putUsers(messages_messages.users, false);
                getMessagesController().putChats(messages_messages.chats, false);
                if (tL_messages_search.offset_id == 0 && j == j2) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i2));
                }
                int min = Math.min(messages_messages.messages.size(), 20);
                int i4 = 0;
                boolean z2 = false;
                while (i4 < min) {
                    MessageObject messageObject = new MessageObject(this.currentAccount, messages_messages.messages.get(i4), false);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[j == j2 ? (char) 0 : 1].put(messageObject.getId(), messageObject);
                    i4++;
                    z2 = true;
                }
                this.messagesSearchEndReached[j == j2 ? (char) 0 : 1] = messages_messages.messages.size() != 21;
                this.messagesSearchCount[j == j2 ? (char) 0 : 1] = ((messages_messages instanceof TLRPC.TL_messages_messagesSlice) || (messages_messages instanceof TLRPC.TL_messages_channelMessages)) ? messages_messages.count : messages_messages.messages.size();
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i2), 0, Integer.valueOf(getMask()), 0L, 0, 0, Boolean.valueOf(z));
                } else if (z2) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i5 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i5, Integer.valueOf(i2), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(z));
                }
                if (j == j2) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && j3 != 0 && !zArr[1]) {
                        searchMessagesInChat(this.lastSearchQuery, j2, j3, i2, 0, true, user, z);
                    }
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0023  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMedia(long r16, int r18, int r19, int r20, int r21, int r22) {
        /*
            r15 = this;
            r2 = r16
            r4 = r18
            r5 = r19
            r6 = r20
            r8 = r21
            r9 = r22
            int r0 = (int) r2
            r1 = 1
            if (r0 >= 0) goto L_0x001c
            int r7 = -r0
            r10 = r15
            int r11 = r10.currentAccount
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r7, r11)
            if (r7 == 0) goto L_0x001d
            r11 = 1
            goto L_0x001f
        L_0x001c:
            r10 = r15
        L_0x001d:
            r7 = 0
            r11 = 0
        L_0x001f:
            boolean r7 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r7 == 0) goto L_0x005f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r12 = "load media did "
            r7.append(r12)
            r7.append(r2)
            java.lang.String r12 = " count = "
            r7.append(r12)
            r7.append(r4)
            java.lang.String r12 = " max_id "
            r7.append(r12)
            r7.append(r5)
            java.lang.String r12 = " type = "
            r7.append(r12)
            r7.append(r6)
            java.lang.String r12 = " cache = "
            r7.append(r12)
            r7.append(r8)
            java.lang.String r12 = " classGuid = "
            r7.append(r12)
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            org.telegram.messenger.FileLog.d(r7)
        L_0x005f:
            if (r8 != 0) goto L_0x00d7
            if (r0 != 0) goto L_0x0065
            goto L_0x00d7
        L_0x0065:
            org.telegram.tgnet.TLRPC$TL_messages_search r12 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r12.<init>()
            r12.limit = r4
            r12.offset_id = r5
            if (r6 != 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo
            r1.<init>()
            r12.filter = r1
            goto L_0x00a2
        L_0x0078:
            if (r6 != r1) goto L_0x0082
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument
            r1.<init>()
            r12.filter = r1
            goto L_0x00a2
        L_0x0082:
            r1 = 2
            if (r6 != r1) goto L_0x008d
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice
            r1.<init>()
            r12.filter = r1
            goto L_0x00a2
        L_0x008d:
            r1 = 3
            if (r6 != r1) goto L_0x0098
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl
            r1.<init>()
            r12.filter = r1
            goto L_0x00a2
        L_0x0098:
            r1 = 4
            if (r6 != r1) goto L_0x00a2
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic
            r1.<init>()
            r12.filter = r1
        L_0x00a2:
            java.lang.String r1 = ""
            r12.q = r1
            org.telegram.messenger.MessagesController r1 = r15.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r0 = r1.getInputPeer(r0)
            r12.peer = r0
            org.telegram.tgnet.TLRPC$InputPeer r0 = r12.peer
            if (r0 != 0) goto L_0x00b5
            return
        L_0x00b5:
            org.telegram.tgnet.ConnectionsManager r13 = r15.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$X3JVomcYEAx51var_ugfuB0sf1c r14 = new org.telegram.messenger.-$$Lambda$MediaDataController$X3JVomcYEAx51var_ugfuB0sf1c
            r0 = r14
            r1 = r15
            r2 = r16
            r4 = r18
            r5 = r19
            r6 = r20
            r7 = r22
            r8 = r11
            r0.<init>(r2, r4, r5, r6, r7, r8)
            int r0 = r13.sendRequest(r12, r14)
            org.telegram.tgnet.ConnectionsManager r1 = r15.getConnectionsManager()
            r1.bindRequestToGuid(r0, r9)
            goto L_0x00e8
        L_0x00d7:
            r0 = r15
            r1 = r16
            r3 = r18
            r4 = r19
            r5 = r20
            r6 = r22
            r7 = r11
            r8 = r21
            r0.loadMediaDatabase(r1, r3, r4, r5, r6, r7, r8)
        L_0x00e8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadMedia(long, int, int, int, int, int):void");
    }

    public /* synthetic */ void lambda$loadMedia$53$MediaDataController(long j, int i, int i2, int i3, int i4, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            long j2 = j;
            getMessagesController().removeDeletedMessagesFromArray(j, messages_messages.messages);
            processLoadedMedia(messages_messages, j, i, i2, i3, 0, i4, z, messages_messages.messages.size() == 0);
        }
    }

    public void getMediaCounts(long j, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, i) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$getMediaCounts$58$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0129 A[Catch:{ Exception -> 0x017c }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x012c A[Catch:{ Exception -> 0x017c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getMediaCounts$58$MediaDataController(long r22, int r24) {
        /*
            r21 = this;
            r7 = r21
            r8 = r22
            r0 = 5
            int[] r10 = new int[r0]     // Catch:{ Exception -> 0x017c }
            r11 = -1
            r12 = 0
            r10[r12] = r11     // Catch:{ Exception -> 0x017c }
            r13 = 1
            r10[r13] = r11     // Catch:{ Exception -> 0x017c }
            r14 = 2
            r10[r14] = r11     // Catch:{ Exception -> 0x017c }
            r15 = 3
            r10[r15] = r11     // Catch:{ Exception -> 0x017c }
            r5 = 4
            r10[r5] = r11     // Catch:{ Exception -> 0x017c }
            int[] r6 = new int[r0]     // Catch:{ Exception -> 0x017c }
            r6[r12] = r11     // Catch:{ Exception -> 0x017c }
            r6[r13] = r11     // Catch:{ Exception -> 0x017c }
            r6[r14] = r11     // Catch:{ Exception -> 0x017c }
            r6[r15] = r11     // Catch:{ Exception -> 0x017c }
            r6[r5] = r11     // Catch:{ Exception -> 0x017c }
            int[] r4 = new int[r0]     // Catch:{ Exception -> 0x017c }
            r4[r12] = r12     // Catch:{ Exception -> 0x017c }
            r4[r13] = r12     // Catch:{ Exception -> 0x017c }
            r4[r14] = r12     // Catch:{ Exception -> 0x017c }
            r4[r15] = r12     // Catch:{ Exception -> 0x017c }
            r4[r5] = r12     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.MessagesStorage r1 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x017c }
            org.telegram.SQLite.SQLiteDatabase r1 = r1.getDatabase()     // Catch:{ Exception -> 0x017c }
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ Exception -> 0x017c }
            java.lang.String r3 = "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d"
            java.lang.Object[] r5 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x017c }
            java.lang.Long r16 = java.lang.Long.valueOf(r22)     // Catch:{ Exception -> 0x017c }
            r5[r12] = r16     // Catch:{ Exception -> 0x017c }
            java.lang.String r2 = java.lang.String.format(r2, r3, r5)     // Catch:{ Exception -> 0x017c }
            java.lang.Object[] r3 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x017c }
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x017c }
        L_0x004d:
            boolean r2 = r1.next()     // Catch:{ Exception -> 0x017c }
            if (r2 == 0) goto L_0x006a
            int r2 = r1.intValue(r12)     // Catch:{ Exception -> 0x017c }
            if (r2 < 0) goto L_0x004d
            if (r2 >= r0) goto L_0x004d
            int r3 = r1.intValue(r13)     // Catch:{ Exception -> 0x017c }
            r10[r2] = r3     // Catch:{ Exception -> 0x017c }
            r6[r2] = r3     // Catch:{ Exception -> 0x017c }
            int r3 = r1.intValue(r14)     // Catch:{ Exception -> 0x017c }
            r4[r2] = r3     // Catch:{ Exception -> 0x017c }
            goto L_0x004d
        L_0x006a:
            r1.dispose()     // Catch:{ Exception -> 0x017c }
            int r0 = (int) r8     // Catch:{ Exception -> 0x017c }
            if (r0 != 0) goto L_0x00c0
            r0 = 0
        L_0x0071:
            int r1 = r10.length     // Catch:{ Exception -> 0x017c }
            if (r0 >= r1) goto L_0x00b6
            r1 = r10[r0]     // Catch:{ Exception -> 0x017c }
            if (r1 != r11) goto L_0x00b3
            org.telegram.messenger.MessagesStorage r1 = r21.getMessagesStorage()     // Catch:{ Exception -> 0x017c }
            org.telegram.SQLite.SQLiteDatabase r1 = r1.getDatabase()     // Catch:{ Exception -> 0x017c }
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ Exception -> 0x017c }
            java.lang.String r3 = "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1"
            java.lang.Object[] r4 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x017c }
            java.lang.Long r5 = java.lang.Long.valueOf(r22)     // Catch:{ Exception -> 0x017c }
            r4[r12] = r5     // Catch:{ Exception -> 0x017c }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x017c }
            r4[r13] = r5     // Catch:{ Exception -> 0x017c }
            java.lang.String r2 = java.lang.String.format(r2, r3, r4)     // Catch:{ Exception -> 0x017c }
            java.lang.Object[] r3 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x017c }
            org.telegram.SQLite.SQLiteCursor r1 = r1.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x017c }
            boolean r2 = r1.next()     // Catch:{ Exception -> 0x017c }
            if (r2 == 0) goto L_0x00a9
            int r2 = r1.intValue(r12)     // Catch:{ Exception -> 0x017c }
            r10[r0] = r2     // Catch:{ Exception -> 0x017c }
            goto L_0x00ab
        L_0x00a9:
            r10[r0] = r12     // Catch:{ Exception -> 0x017c }
        L_0x00ab:
            r1.dispose()     // Catch:{ Exception -> 0x017c }
            r1 = r10[r0]     // Catch:{ Exception -> 0x017c }
            r7.putMediaCountDatabase(r8, r0, r1)     // Catch:{ Exception -> 0x017c }
        L_0x00b3:
            int r0 = r0 + 1
            goto L_0x0071
        L_0x00b6:
            org.telegram.messenger.-$$Lambda$MediaDataController$kNwijBRpY_lhcRXjFDUVJ4ij52s r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$kNwijBRpY_lhcRXjFDUVJ4ij52s     // Catch:{ Exception -> 0x017c }
            r0.<init>(r8, r10)     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0180
        L_0x00c0:
            r5 = 0
            r16 = 0
        L_0x00c3:
            int r1 = r10.length     // Catch:{ Exception -> 0x017c }
            if (r5 >= r1) goto L_0x016d
            r1 = r10[r5]     // Catch:{ Exception -> 0x017c }
            if (r1 == r11) goto L_0x00db
            r1 = r4[r5]     // Catch:{ Exception -> 0x017c }
            if (r1 != r13) goto L_0x00cf
            goto L_0x00db
        L_0x00cf:
            r3 = r24
            r18 = r4
            r17 = r5
            r19 = r6
            r20 = 4
            goto L_0x0162
        L_0x00db:
            org.telegram.tgnet.TLRPC$TL_messages_search r3 = new org.telegram.tgnet.TLRPC$TL_messages_search     // Catch:{ Exception -> 0x017c }
            r3.<init>()     // Catch:{ Exception -> 0x017c }
            r3.limit = r13     // Catch:{ Exception -> 0x017c }
            r3.offset_id = r12     // Catch:{ Exception -> 0x017c }
            if (r5 != 0) goto L_0x00ef
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo     // Catch:{ Exception -> 0x017c }
            r1.<init>()     // Catch:{ Exception -> 0x017c }
            r3.filter = r1     // Catch:{ Exception -> 0x017c }
        L_0x00ed:
            r2 = 4
            goto L_0x0117
        L_0x00ef:
            if (r5 != r13) goto L_0x00f9
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument     // Catch:{ Exception -> 0x017c }
            r1.<init>()     // Catch:{ Exception -> 0x017c }
            r3.filter = r1     // Catch:{ Exception -> 0x017c }
            goto L_0x00ed
        L_0x00f9:
            if (r5 != r14) goto L_0x0103
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice     // Catch:{ Exception -> 0x017c }
            r1.<init>()     // Catch:{ Exception -> 0x017c }
            r3.filter = r1     // Catch:{ Exception -> 0x017c }
            goto L_0x00ed
        L_0x0103:
            if (r5 != r15) goto L_0x010d
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl     // Catch:{ Exception -> 0x017c }
            r1.<init>()     // Catch:{ Exception -> 0x017c }
            r3.filter = r1     // Catch:{ Exception -> 0x017c }
            goto L_0x00ed
        L_0x010d:
            r2 = 4
            if (r5 != r2) goto L_0x0117
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic     // Catch:{ Exception -> 0x017c }
            r1.<init>()     // Catch:{ Exception -> 0x017c }
            r3.filter = r1     // Catch:{ Exception -> 0x017c }
        L_0x0117:
            java.lang.String r1 = ""
            r3.q = r1     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()     // Catch:{ Exception -> 0x017c }
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r0)     // Catch:{ Exception -> 0x017c }
            r3.peer = r1     // Catch:{ Exception -> 0x017c }
            org.telegram.tgnet.TLRPC$InputPeer r1 = r3.peer     // Catch:{ Exception -> 0x017c }
            if (r1 != 0) goto L_0x012c
            r10[r5] = r12     // Catch:{ Exception -> 0x017c }
            goto L_0x00cf
        L_0x012c:
            org.telegram.tgnet.ConnectionsManager r1 = r21.getConnectionsManager()     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.-$$Lambda$MediaDataController$Wej6h_SDhgOO315FvodBoxAfJrI r12 = new org.telegram.messenger.-$$Lambda$MediaDataController$Wej6h_SDhgOO315FvodBoxAfJrI     // Catch:{ Exception -> 0x017c }
            r14 = r1
            r1 = r12
            r17 = 4
            r2 = r21
            r15 = r3
            r3 = r10
            r18 = r4
            r4 = r5
            r17 = r5
            r19 = r6
            r20 = 4
            r5 = r22
            r1.<init>(r3, r4, r5)     // Catch:{ Exception -> 0x017c }
            int r1 = r14.sendRequest(r15, r12)     // Catch:{ Exception -> 0x017c }
            org.telegram.tgnet.ConnectionsManager r2 = r21.getConnectionsManager()     // Catch:{ Exception -> 0x017c }
            r3 = r24
            r2.bindRequestToGuid(r1, r3)     // Catch:{ Exception -> 0x017c }
            r1 = r10[r17]     // Catch:{ Exception -> 0x017c }
            if (r1 != r11) goto L_0x015c
            r16 = 1
            goto L_0x0162
        L_0x015c:
            r1 = r18[r17]     // Catch:{ Exception -> 0x017c }
            if (r1 != r13) goto L_0x0162
            r10[r17] = r11     // Catch:{ Exception -> 0x017c }
        L_0x0162:
            int r5 = r17 + 1
            r4 = r18
            r6 = r19
            r12 = 0
            r14 = 2
            r15 = 3
            goto L_0x00c3
        L_0x016d:
            r19 = r6
            if (r16 != 0) goto L_0x0180
            org.telegram.messenger.-$$Lambda$MediaDataController$W2WxdSfDOFuhCKgeEvnNLE6gT-U r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$W2WxdSfDOFuhCKgeEvnNLE6gT-U     // Catch:{ Exception -> 0x017c }
            r1 = r19
            r0.<init>(r8, r1)     // Catch:{ Exception -> 0x017c }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0180
        L_0x017c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0180:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$getMediaCounts$58$MediaDataController(long, int):void");
    }

    public /* synthetic */ void lambda$null$54$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$56$MediaDataController(int[] iArr, int i, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        boolean z = false;
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (messages_messages instanceof TLRPC.TL_messages_messages) {
                iArr[i] = messages_messages.messages.size();
            } else {
                iArr[i] = messages_messages.count;
            }
            putMediaCountDatabase(j, i, iArr[i]);
        } else {
            iArr[i] = 0;
        }
        int i2 = 0;
        while (true) {
            if (i2 >= iArr.length) {
                z = true;
                break;
            } else if (iArr[i2] == -1) {
                break;
            } else {
                i2++;
            }
        }
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable(j, iArr) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$55$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$55$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$57$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public void getMediaCount(long j, int i, int i2, boolean z) {
        int i3 = (int) j;
        if (z || i3 == 0) {
            getMediaCountDatabase(j, i, i2);
            return;
        }
        TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
        tL_messages_search.limit = 1;
        tL_messages_search.offset_id = 0;
        if (i == 0) {
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
        } else if (i == 1) {
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
        } else if (i == 2) {
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterRoundVoice();
        } else if (i == 3) {
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
        } else if (i == 4) {
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
        }
        tL_messages_search.q = "";
        tL_messages_search.peer = getMessagesController().getInputPeer(i3);
        if (tL_messages_search.peer != null) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate(j, i, i2) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$getMediaCount$60$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }), i2);
        }
    }

    public /* synthetic */ void lambda$getMediaCount$60$MediaDataController(long j, int i, int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i3;
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            if (messages_messages instanceof TLRPC.TL_messages_messages) {
                i3 = messages_messages.messages.size();
            } else {
                i3 = messages_messages.count;
            }
            AndroidUtilities.runOnUIThread(new Runnable(messages_messages) {
                private final /* synthetic */ TLRPC.messages_Messages f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$59$MediaDataController(this.f$1);
                }
            });
            processLoadedMediaCount(i3, j, i, i2, false, 0);
        }
    }

    public /* synthetic */ void lambda$null$59$MediaDataController(TLRPC.messages_Messages messages_messages) {
        getMessagesController().putUsers(messages_messages.users, false);
        getMessagesController().putChats(messages_messages.chats, false);
    }

    public static int getMediaType(TLRPC.Message message) {
        if (message == null) {
            return -1;
        }
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
            if (!message.entities.isEmpty()) {
                for (int i = 0; i < message.entities.size(); i++) {
                    TLRPC.MessageEntity messageEntity = message.entities.get(i);
                    if ((messageEntity instanceof TLRPC.TL_messageEntityUrl) || (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) || (messageEntity instanceof TLRPC.TL_messageEntityEmail)) {
                        return 3;
                    }
                }
            }
            return -1;
        } else if (MessageObject.isVoiceMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            return 2;
        } else {
            if (MessageObject.isVideoMessage(message)) {
                return 0;
            }
            if (MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message) || MessageObject.isNewGifMessage(message)) {
                return -1;
            }
            return MessageObject.isMusicMessage(message) ? 4 : 1;
        }
    }

    public static boolean canAddMessageToMedia(TLRPC.Message message) {
        int i;
        boolean z = message instanceof TLRPC.TL_message_secret;
        if (z && (((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && (i = message.media.ttl_seconds) != 0 && i <= 60)) {
            return false;
        }
        if (!z && (message instanceof TLRPC.TL_message)) {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return false;
            }
        }
        TLRPC.MessageMedia messageMedia2 = message.media;
        if ((messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) || ((messageMedia2 instanceof TLRPC.TL_messageMediaDocument) && !MessageObject.isGifDocument(messageMedia2.document))) {
            return true;
        }
        if (!message.entities.isEmpty()) {
            for (int i2 = 0; i2 < message.entities.size(); i2++) {
                TLRPC.MessageEntity messageEntity = message.entities.get(i2);
                if ((messageEntity instanceof TLRPC.TL_messageEntityUrl) || (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) || (messageEntity instanceof TLRPC.TL_messageEntityEmail)) {
                    return true;
                }
            }
        }
        if (getMediaType(message) != -1) {
            return true;
        }
        return false;
    }

    private void processLoadedMedia(TLRPC.messages_Messages messages_messages, long j, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2) {
        TLRPC.messages_Messages messages_messages2 = messages_messages;
        long j2 = j;
        int i6 = i4;
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("process load media did " + j2 + " count = " + i + " max_id " + i2 + " type = " + i3 + " cache = " + i6 + " classGuid = " + i5);
        } else {
            int i7 = i;
            int i8 = i2;
            int i9 = i3;
            int i10 = i5;
        }
        int i11 = (int) j2;
        if (i6 == 0 || !messages_messages2.messages.isEmpty() || i11 == 0) {
            if (i6 == 0) {
                ImageLoader.saveMessagesThumbs(messages_messages2.messages);
                getMessagesStorage().putUsersAndChats(messages_messages2.users, messages_messages2.chats, true, true);
                putMediaDatabase(j, i3, messages_messages2.messages, i2, z2);
            }
            SparseArray sparseArray = new SparseArray();
            for (int i12 = 0; i12 < messages_messages2.users.size(); i12++) {
                TLRPC.User user = messages_messages2.users.get(i12);
                sparseArray.put(user.id, user);
            }
            ArrayList arrayList = new ArrayList();
            for (int i13 = 0; i13 < messages_messages2.messages.size(); i13++) {
                arrayList.add(new MessageObject(this.currentAccount, messages_messages2.messages.get(i13), (SparseArray<TLRPC.User>) sparseArray, true));
            }
            AndroidUtilities.runOnUIThread(new Runnable(messages_messages, i4, j, arrayList, i5, i3, z2) {
                private final /* synthetic */ TLRPC.messages_Messages f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ ArrayList f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ boolean f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                }

                public final void run() {
                    MediaDataController.this.lambda$processLoadedMedia$61$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else if (i6 != 2) {
            loadMedia(j, i, i2, i3, 0, i5);
        }
    }

    public /* synthetic */ void lambda$processLoadedMedia$61$MediaDataController(TLRPC.messages_Messages messages_messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z) {
        int i4 = messages_messages.count;
        getMessagesController().putUsers(messages_messages.users, i != 0);
        getMessagesController().putChats(messages_messages.chats, i != 0);
        getNotificationCenter().postNotificationName(NotificationCenter.mediaDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Integer.valueOf(i2), Integer.valueOf(i3), Boolean.valueOf(z));
    }

    private void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z, int i4) {
        AndroidUtilities.runOnUIThread(new Runnable(j, z, i, i2, i4, i3) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                MediaDataController.this.lambda$processLoadedMediaCount$62$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedMediaCount$62$MediaDataController(long j, boolean z, int i, int i2, int i3, int i4) {
        long j2 = j;
        int i5 = i;
        int i6 = i2;
        int i7 = (int) j2;
        boolean z2 = z && (i5 == -1 || (i5 == 0 && i6 == 2)) && i7 != 0;
        if (z2 || (i3 == 1 && i7 != 0)) {
            getMediaCount(j, i2, i4, false);
        }
        if (!z2) {
            if (!z) {
                putMediaCountDatabase(j2, i6, i5);
            }
            NotificationCenter notificationCenter = getNotificationCenter();
            int i8 = NotificationCenter.mediaCountDidLoad;
            Object[] objArr = new Object[4];
            objArr[0] = Long.valueOf(j);
            if (z && i5 == -1) {
                i5 = 0;
            }
            objArr[1] = Integer.valueOf(i5);
            objArr[2] = Boolean.valueOf(z);
            objArr[3] = Integer.valueOf(i2);
            notificationCenter.postNotificationName(i8, objArr);
        }
    }

    private void putMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, i, i2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$putMediaCountDatabase$63$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$putMediaCountDatabase$63$MediaDataController(long j, int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, i);
            executeFast.bindInteger(3, i2);
            executeFast.bindInteger(4, 0);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void getMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, i, i2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$getMediaCountDatabase$64$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$getMediaCountDatabase$64$MediaDataController(long j, int i, int i2) {
        int i3;
        int i4;
        long j2 = j;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next()) {
                i4 = queryFinalized.intValue(0);
                i3 = queryFinalized.intValue(1);
            } else {
                i4 = -1;
                i3 = 0;
            }
            queryFinalized.dispose();
            int i5 = (int) j2;
            if (i4 == -1 && i5 == 0) {
                SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized2.next()) {
                    i4 = queryFinalized2.intValue(0);
                }
                queryFinalized2.dispose();
                if (i4 != -1) {
                    int i6 = i;
                    try {
                        putMediaCountDatabase(j, i, i4);
                        processLoadedMediaCount(i4, j, i, i2, true, i3);
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                }
            }
            int i7 = i;
            processLoadedMediaCount(i4, j, i, i2, true, i3);
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, boolean z, int i5) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, j, i2, z, i3, i5, i4) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ int f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
            }

            public final void run() {
                MediaDataController.this.lambda$loadMediaDatabase$65$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$loadMediaDatabase$65$MediaDataController(int i, long j, int i2, boolean z, int i3, int i4, int i5) {
        boolean z2;
        boolean z3;
        boolean z4;
        SQLiteCursor sQLiteCursor;
        SQLiteCursor queryFinalized;
        int i6;
        boolean z5;
        boolean z6;
        int intValue;
        int i7 = i;
        long j2 = j;
        int i8 = i2;
        TLRPC.TL_messages_messages tL_messages_messages = new TLRPC.TL_messages_messages();
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i9 = i7 + 1;
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            int i10 = (int) j2;
            if (i10 != 0) {
                long j3 = (long) i8;
                int i11 = z ? -i10 : 0;
                long j4 = 0;
                if (!(j3 == 0 || i11 == 0)) {
                    j3 |= ((long) i11) << 32;
                }
                try {
                    int i12 = i11;
                    SQLiteCursor queryFinalized2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                    if (queryFinalized2.next()) {
                        boolean z7 = queryFinalized2.intValue(0) == 1;
                        queryFinalized2.dispose();
                        z5 = z7;
                        i6 = i12;
                    } else {
                        queryFinalized2.dispose();
                        SQLiteCursor queryFinalized3 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                        if (!queryFinalized3.next() || (intValue = queryFinalized3.intValue(0)) == 0) {
                            i6 = i12;
                        } else {
                            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                            executeFast.requery();
                            executeFast.bindLong(1, j2);
                            executeFast.bindInteger(2, i3);
                            executeFast.bindInteger(3, 0);
                            executeFast.bindInteger(4, intValue);
                            i6 = i12;
                            executeFast.step();
                            executeFast.dispose();
                        }
                        queryFinalized3.dispose();
                        z5 = false;
                    }
                    if (j3 != 0) {
                        z6 = z5;
                        SQLiteCursor queryFinalized4 = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2)}), new Object[0]);
                        if (queryFinalized4.next()) {
                            long intValue2 = (long) queryFinalized4.intValue(0);
                            j4 = i6 != 0 ? intValue2 | (((long) i6) << 32) : intValue2;
                        }
                        queryFinalized4.dispose();
                        if (j4 > 1) {
                            sQLiteCursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j3), Long.valueOf(j4), Integer.valueOf(i3), Integer.valueOf(i9)}), new Object[0]);
                        } else {
                            sQLiteCursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j3), Integer.valueOf(i3), Integer.valueOf(i9)}), new Object[0]);
                        }
                    } else {
                        z6 = z5;
                        SQLiteCursor queryFinalized5 = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                        if (queryFinalized5.next()) {
                            long intValue3 = (long) queryFinalized5.intValue(0);
                            j4 = i6 != 0 ? intValue3 | (((long) i6) << 32) : intValue3;
                        }
                        queryFinalized5.dispose();
                        if (j4 > 1) {
                            sQLiteCursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j4), Integer.valueOf(i3), Integer.valueOf(i9)}), new Object[0]);
                        } else {
                            sQLiteCursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i9)}), new Object[0]);
                        }
                    }
                    z3 = z6;
                    z4 = false;
                } catch (Exception e) {
                    e = e;
                    int i13 = i;
                    z2 = false;
                    try {
                        tL_messages_messages.messages.clear();
                        tL_messages_messages.chats.clear();
                        tL_messages_messages.users.clear();
                        FileLog.e((Throwable) e);
                        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
                    } catch (Throwable th) {
                        th = th;
                        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, false);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    int i14 = i;
                    processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, false);
                    throw th;
                }
            } else {
                if (i8 != 0) {
                    queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i9)}), new Object[0]);
                    z4 = false;
                } else {
                    Locale locale = Locale.US;
                    Object[] objArr = {Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i9)};
                    z4 = false;
                    queryFinalized = database.queryFinalized(String.format(locale, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", objArr), new Object[0]);
                }
                z3 = true;
            }
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(z4 ? 1 : 0);
                if (byteBufferValue != null) {
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z4), z4);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = sQLiteCursor.intValue(1);
                    TLdeserialize.dialog_id = j2;
                    if (i10 == 0) {
                        TLdeserialize.random_id = sQLiteCursor.longValue(2);
                    }
                    tL_messages_messages.messages.add(TLdeserialize);
                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                }
            }
            sQLiteCursor.dispose();
            if (!arrayList.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList), tL_messages_messages.users);
            }
            if (!arrayList2.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList2), tL_messages_messages.chats);
            }
            if (tL_messages_messages.messages.size() > i) {
                tL_messages_messages.messages.remove(tL_messages_messages.messages.size() - 1);
                z2 = false;
            } else {
                z2 = z3;
            }
        } catch (Exception e2) {
            e = e2;
            z2 = false;
            tL_messages_messages.messages.clear();
            tL_messages_messages.chats.clear();
            tL_messages_messages.users.clear();
            FileLog.e((Throwable) e);
            processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
        }
        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
    }

    private void putMediaDatabase(long j, int i, ArrayList<TLRPC.Message> arrayList, int i2, boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, z, j, i2, i) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                MediaDataController.this.lambda$putMediaDatabase$66$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$putMediaDatabase$66$MediaDataController(ArrayList arrayList, boolean z, long j, int i, int i2) {
        int i3;
        long j2 = j;
        int i4 = i;
        int i5 = i2;
        try {
            if (arrayList.isEmpty() || z) {
                getMessagesStorage().doneHolesInMedia(j2, i4, i5);
                if (arrayList.isEmpty()) {
                    return;
                }
            }
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC.Message message = (TLRPC.Message) it.next();
                if (canAddMessageToMedia(message)) {
                    long j3 = (long) message.id;
                    if (message.to_id.channel_id != 0) {
                        j3 |= ((long) message.to_id.channel_id) << 32;
                    }
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    executeFast.bindLong(1, j3);
                    executeFast.bindLong(2, j2);
                    executeFast.bindInteger(3, message.date);
                    executeFast.bindInteger(4, i5);
                    executeFast.bindByteBuffer(5, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
            }
            executeFast.dispose();
            if (!z || i4 != 0) {
                if (z) {
                    i3 = 1;
                } else {
                    ArrayList arrayList2 = arrayList;
                    i3 = ((TLRPC.Message) arrayList.get(arrayList.size() - 1)).id;
                }
                if (i4 != 0) {
                    getMessagesStorage().closeHolesInMedia(j, i3, i, i2);
                } else {
                    getMessagesStorage().closeHolesInMedia(j, i3, Integer.MAX_VALUE, i2);
                }
            }
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void loadMusic(long j, long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, j2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$loadMusic$68$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x0068 A[Catch:{ Exception -> 0x009f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadMusic$68$MediaDataController(long r11, long r13) {
        /*
            r10 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = (int) r11
            r2 = 4
            r3 = 2
            r4 = 3
            r5 = 1
            r6 = 0
            if (r1 == 0) goto L_0x0038
            org.telegram.messenger.MessagesStorage r1 = r10.getMessagesStorage()     // Catch:{ Exception -> 0x009f }
            org.telegram.SQLite.SQLiteDatabase r1 = r1.getDatabase()     // Catch:{ Exception -> 0x009f }
            java.util.Locale r7 = java.util.Locale.US     // Catch:{ Exception -> 0x009f }
            java.lang.String r8 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x009f }
            java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x009f }
            r4[r6] = r9     // Catch:{ Exception -> 0x009f }
            java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x009f }
            r4[r5] = r13     // Catch:{ Exception -> 0x009f }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x009f }
            r4[r3] = r13     // Catch:{ Exception -> 0x009f }
            java.lang.String r13 = java.lang.String.format(r7, r8, r4)     // Catch:{ Exception -> 0x009f }
            java.lang.Object[] r14 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x009f }
            org.telegram.SQLite.SQLiteCursor r13 = r1.queryFinalized(r13, r14)     // Catch:{ Exception -> 0x009f }
            goto L_0x0062
        L_0x0038:
            org.telegram.messenger.MessagesStorage r1 = r10.getMessagesStorage()     // Catch:{ Exception -> 0x009f }
            org.telegram.SQLite.SQLiteDatabase r1 = r1.getDatabase()     // Catch:{ Exception -> 0x009f }
            java.util.Locale r7 = java.util.Locale.US     // Catch:{ Exception -> 0x009f }
            java.lang.String r8 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x009f }
            java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x009f }
            r4[r6] = r9     // Catch:{ Exception -> 0x009f }
            java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x009f }
            r4[r5] = r13     // Catch:{ Exception -> 0x009f }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x009f }
            r4[r3] = r13     // Catch:{ Exception -> 0x009f }
            java.lang.String r13 = java.lang.String.format(r7, r8, r4)     // Catch:{ Exception -> 0x009f }
            java.lang.Object[] r14 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x009f }
            org.telegram.SQLite.SQLiteCursor r13 = r1.queryFinalized(r13, r14)     // Catch:{ Exception -> 0x009f }
        L_0x0062:
            boolean r14 = r13.next()     // Catch:{ Exception -> 0x009f }
            if (r14 == 0) goto L_0x009b
            org.telegram.tgnet.NativeByteBuffer r14 = r13.byteBufferValue(r6)     // Catch:{ Exception -> 0x009f }
            if (r14 == 0) goto L_0x0062
            int r1 = r14.readInt32(r6)     // Catch:{ Exception -> 0x009f }
            org.telegram.tgnet.TLRPC$Message r1 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r14, r1, r6)     // Catch:{ Exception -> 0x009f }
            org.telegram.messenger.UserConfig r2 = r10.getUserConfig()     // Catch:{ Exception -> 0x009f }
            int r2 = r2.clientUserId     // Catch:{ Exception -> 0x009f }
            r1.readAttachPath(r14, r2)     // Catch:{ Exception -> 0x009f }
            r14.reuse()     // Catch:{ Exception -> 0x009f }
            boolean r14 = org.telegram.messenger.MessageObject.isMusicMessage(r1)     // Catch:{ Exception -> 0x009f }
            if (r14 == 0) goto L_0x0062
            int r14 = r13.intValue(r5)     // Catch:{ Exception -> 0x009f }
            r1.id = r14     // Catch:{ Exception -> 0x009f }
            r1.dialog_id = r11     // Catch:{ Exception -> 0x009f }
            org.telegram.messenger.MessageObject r14 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x009f }
            int r2 = r10.currentAccount     // Catch:{ Exception -> 0x009f }
            r14.<init>(r2, r1, r6)     // Catch:{ Exception -> 0x009f }
            r0.add(r6, r14)     // Catch:{ Exception -> 0x009f }
            goto L_0x0062
        L_0x009b:
            r13.dispose()     // Catch:{ Exception -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x00a3:
            org.telegram.messenger.-$$Lambda$MediaDataController$obZ9y2OwpklYXkbfSyaphhg9mLM r13 = new org.telegram.messenger.-$$Lambda$MediaDataController$obZ9y2OwpklYXkbfSyaphhg9mLM
            r13.<init>(r11, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadMusic$68$MediaDataController(long, long):void");
    }

    public /* synthetic */ void lambda$null$67$MediaDataController(long j, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(j), arrayList);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT >= 25) {
            ArrayList arrayList = new ArrayList();
            if (SharedConfig.passcodeHash.length() <= 0) {
                for (int i = 0; i < this.hints.size(); i++) {
                    arrayList.add(this.hints.get(i));
                    if (arrayList.size() == 3) {
                        break;
                    }
                }
            }
            Utilities.globalQueue.postRunnable(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$buildShortcuts$69$MediaDataController(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:64:0x01ad  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0259 A[Catch:{ all -> 0x02b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0270 A[Catch:{ all -> 0x02b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0287 A[Catch:{ all -> 0x02b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x028f A[Catch:{ all -> 0x02b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02a8 A[Catch:{ all -> 0x02b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02ac A[Catch:{ all -> 0x02b8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$buildShortcuts$69$MediaDataController(java.util.ArrayList r21) {
        /*
            r20 = this;
            r1 = r21
            java.lang.String r0 = "NewConversationShortcut"
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            java.lang.Class<android.content.pm.ShortcutManager> r3 = android.content.pm.ShortcutManager.class
            java.lang.Object r2 = r2.getSystemService(r3)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutManager r2 = (android.content.pm.ShortcutManager) r2     // Catch:{ all -> 0x02b8 }
            java.util.List r3 = r2.getDynamicShortcuts()     // Catch:{ all -> 0x02b8 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x02b8 }
            r4.<init>()     // Catch:{ all -> 0x02b8 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02b8 }
            r5.<init>()     // Catch:{ all -> 0x02b8 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x02b8 }
            r6.<init>()     // Catch:{ all -> 0x02b8 }
            java.lang.String r7 = "did"
            r8 = 0
            java.lang.String r9 = "compose"
            if (r3 == 0) goto L_0x009f
            boolean r10 = r3.isEmpty()     // Catch:{ all -> 0x02b8 }
            if (r10 != 0) goto L_0x009f
            r5.add(r9)     // Catch:{ all -> 0x02b8 }
            r10 = 0
        L_0x0032:
            int r11 = r21.size()     // Catch:{ all -> 0x02b8 }
            if (r10 >= r11) goto L_0x0072
            java.lang.Object r11 = r1.get(r10)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$TL_topPeer r11 = (org.telegram.tgnet.TLRPC.TL_topPeer) r11     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$Peer r12 = r11.peer     // Catch:{ all -> 0x02b8 }
            int r12 = r12.user_id     // Catch:{ all -> 0x02b8 }
            if (r12 == 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer     // Catch:{ all -> 0x02b8 }
            int r11 = r11.user_id     // Catch:{ all -> 0x02b8 }
        L_0x0048:
            long r11 = (long) r11     // Catch:{ all -> 0x02b8 }
            goto L_0x005d
        L_0x004a:
            org.telegram.tgnet.TLRPC$Peer r12 = r11.peer     // Catch:{ all -> 0x02b8 }
            int r12 = r12.chat_id     // Catch:{ all -> 0x02b8 }
            int r12 = -r12
            long r12 = (long) r12     // Catch:{ all -> 0x02b8 }
            r14 = 0
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer     // Catch:{ all -> 0x02b8 }
            int r11 = r11.channel_id     // Catch:{ all -> 0x02b8 }
            int r11 = -r11
            goto L_0x0048
        L_0x005c:
            r11 = r12
        L_0x005d:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b8 }
            r13.<init>()     // Catch:{ all -> 0x02b8 }
            r13.append(r7)     // Catch:{ all -> 0x02b8 }
            r13.append(r11)     // Catch:{ all -> 0x02b8 }
            java.lang.String r11 = r13.toString()     // Catch:{ all -> 0x02b8 }
            r5.add(r11)     // Catch:{ all -> 0x02b8 }
            int r10 = r10 + 1
            goto L_0x0032
        L_0x0072:
            r10 = 0
        L_0x0073:
            int r11 = r3.size()     // Catch:{ all -> 0x02b8 }
            if (r10 >= r11) goto L_0x0092
            java.lang.Object r11 = r3.get(r10)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo r11 = (android.content.pm.ShortcutInfo) r11     // Catch:{ all -> 0x02b8 }
            java.lang.String r11 = r11.getId()     // Catch:{ all -> 0x02b8 }
            boolean r12 = r5.remove(r11)     // Catch:{ all -> 0x02b8 }
            if (r12 != 0) goto L_0x008c
            r6.add(r11)     // Catch:{ all -> 0x02b8 }
        L_0x008c:
            r4.add(r11)     // Catch:{ all -> 0x02b8 }
            int r10 = r10 + 1
            goto L_0x0073
        L_0x0092:
            boolean r3 = r5.isEmpty()     // Catch:{ all -> 0x02b8 }
            if (r3 == 0) goto L_0x009f
            boolean r3 = r6.isEmpty()     // Catch:{ all -> 0x02b8 }
            if (r3 == 0) goto L_0x009f
            return
        L_0x009f:
            android.content.Intent r3 = new android.content.Intent     // Catch:{ all -> 0x02b8 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r10 = org.telegram.ui.LaunchActivity.class
            r3.<init>(r5, r10)     // Catch:{ all -> 0x02b8 }
            java.lang.String r5 = "new_dialog"
            r3.setAction(r5)     // Catch:{ all -> 0x02b8 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02b8 }
            r5.<init>()     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r10 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ all -> 0x02b8 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            r10.<init>(r11, r9)     // Catch:{ all -> 0x02b8 }
            r11 = 2131625653(0x7f0e06b5, float:1.887852E38)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r11)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r10 = r10.setShortLabel(r12)     // Catch:{ all -> 0x02b8 }
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r11)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r0 = r10.setLongLabel(r0)     // Catch:{ all -> 0x02b8 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            r11 = 2131165866(0x7var_aa, float:1.7945961E38)
            android.graphics.drawable.Icon r10 = android.graphics.drawable.Icon.createWithResource(r10, r11)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIcon(r10)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIntent(r3)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo r0 = r0.build()     // Catch:{ all -> 0x02b8 }
            r5.add(r0)     // Catch:{ all -> 0x02b8 }
            boolean r0 = r4.contains(r9)     // Catch:{ all -> 0x02b8 }
            if (r0 == 0) goto L_0x00ee
            r2.updateShortcuts(r5)     // Catch:{ all -> 0x02b8 }
            goto L_0x00f1
        L_0x00ee:
            r2.addDynamicShortcuts(r5)     // Catch:{ all -> 0x02b8 }
        L_0x00f1:
            r5.clear()     // Catch:{ all -> 0x02b8 }
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x02b8 }
            if (r0 != 0) goto L_0x00fd
            r2.removeDynamicShortcuts(r6)     // Catch:{ all -> 0x02b8 }
        L_0x00fd:
            int r0 = r21.size()     // Catch:{ all -> 0x02b8 }
            if (r8 >= r0) goto L_0x02b8
            android.content.Intent r3 = new android.content.Intent     // Catch:{ all -> 0x02b8 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r6 = org.telegram.messenger.OpenChatReceiver.class
            r3.<init>(r0, r6)     // Catch:{ all -> 0x02b8 }
            java.lang.Object r0 = r1.get(r8)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$TL_topPeer r0 = (org.telegram.tgnet.TLRPC.TL_topPeer) r0     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$Peer r6 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r6 = r6.user_id     // Catch:{ all -> 0x02b8 }
            if (r6 == 0) goto L_0x0139
            java.lang.String r6 = "userId"
            org.telegram.tgnet.TLRPC$Peer r10 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r10 = r10.user_id     // Catch:{ all -> 0x02b8 }
            r3.putExtra(r6, r10)     // Catch:{ all -> 0x02b8 }
            org.telegram.messenger.MessagesController r6 = r20.getMessagesController()     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$Peer r10 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r10 = r10.user_id     // Catch:{ all -> 0x02b8 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r10)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r0 = r0.user_id     // Catch:{ all -> 0x02b8 }
            long r10 = (long) r0     // Catch:{ all -> 0x02b8 }
            r0 = 0
            goto L_0x0157
        L_0x0139:
            org.telegram.tgnet.TLRPC$Peer r6 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r6 = r6.chat_id     // Catch:{ all -> 0x02b8 }
            if (r6 != 0) goto L_0x0143
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer     // Catch:{ all -> 0x02b8 }
            int r6 = r0.channel_id     // Catch:{ all -> 0x02b8 }
        L_0x0143:
            org.telegram.messenger.MessagesController r0 = r20.getMessagesController()     // Catch:{ all -> 0x02b8 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r10)     // Catch:{ all -> 0x02b8 }
            java.lang.String r10 = "chatId"
            r3.putExtra(r10, r6)     // Catch:{ all -> 0x02b8 }
            int r6 = -r6
            long r10 = (long) r6     // Catch:{ all -> 0x02b8 }
            r6 = 0
        L_0x0157:
            if (r6 == 0) goto L_0x015f
            boolean r12 = org.telegram.messenger.UserObject.isDeleted(r6)     // Catch:{ all -> 0x02b8 }
            if (r12 == 0) goto L_0x0163
        L_0x015f:
            if (r0 != 0) goto L_0x0163
            goto L_0x02b2
        L_0x0163:
            if (r6 == 0) goto L_0x017d
            java.lang.String r0 = r6.first_name     // Catch:{ all -> 0x02b8 }
            java.lang.String r12 = r6.last_name     // Catch:{ all -> 0x02b8 }
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r12)     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r6.photo     // Catch:{ all -> 0x02b8 }
            if (r12 == 0) goto L_0x017b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r6.photo     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ all -> 0x02b8 }
            r19 = r6
            r6 = r0
            r0 = r19
            goto L_0x0189
        L_0x017b:
            r6 = r0
            goto L_0x0188
        L_0x017d:
            java.lang.String r6 = r0.title     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r0.photo     // Catch:{ all -> 0x02b8 }
            if (r12 == 0) goto L_0x0188
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo     // Catch:{ all -> 0x02b8 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x02b8 }
            goto L_0x0189
        L_0x0188:
            r0 = 0
        L_0x0189:
            java.lang.String r12 = "currentAccount"
            r13 = r20
            int r14 = r13.currentAccount     // Catch:{ all -> 0x02b8 }
            r3.putExtra(r12, r14)     // Catch:{ all -> 0x02b8 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b8 }
            r12.<init>()     // Catch:{ all -> 0x02b8 }
            java.lang.String r14 = "com.tmessages.openchat"
            r12.append(r14)     // Catch:{ all -> 0x02b8 }
            r12.append(r10)     // Catch:{ all -> 0x02b8 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x02b8 }
            r3.setAction(r12)     // Catch:{ all -> 0x02b8 }
            r12 = 67108864(0x4000000, float:1.5046328E-36)
            r3.addFlags(r12)     // Catch:{ all -> 0x02b8 }
            if (r0 == 0) goto L_0x0259
            r12 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12)     // Catch:{ all -> 0x0252 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0252 }
            android.graphics.Bitmap r14 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x0252 }
            if (r14 == 0) goto L_0x0250
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x024d }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x024d }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r0, r0, r15)     // Catch:{ all -> 0x024d }
            android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x024d }
            r9.<init>(r15)     // Catch:{ all -> 0x024d }
            android.graphics.Paint r17 = roundPaint     // Catch:{ all -> 0x024d }
            r18 = 1073741824(0x40000000, float:2.0)
            if (r17 != 0) goto L_0x021b
            android.graphics.Paint r12 = new android.graphics.Paint     // Catch:{ all -> 0x024d }
            r1 = 3
            r12.<init>(r1)     // Catch:{ all -> 0x024d }
            roundPaint = r12     // Catch:{ all -> 0x024d }
            android.graphics.RectF r1 = new android.graphics.RectF     // Catch:{ all -> 0x024d }
            r1.<init>()     // Catch:{ all -> 0x024d }
            bitmapRect = r1     // Catch:{ all -> 0x024d }
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x024d }
            r12 = 1
            r1.<init>(r12)     // Catch:{ all -> 0x024d }
            erasePaint = r1     // Catch:{ all -> 0x024d }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x024d }
            android.graphics.PorterDuffXfermode r12 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x024d }
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x024d }
            r12.<init>(r13)     // Catch:{ all -> 0x024d }
            r1.setXfermode(r12)     // Catch:{ all -> 0x024d }
            android.graphics.Path r1 = new android.graphics.Path     // Catch:{ all -> 0x024d }
            r1.<init>()     // Catch:{ all -> 0x024d }
            roundPath = r1     // Catch:{ all -> 0x024d }
            android.graphics.Path r1 = roundPath     // Catch:{ all -> 0x024d }
            int r12 = r0 / 2
            float r12 = (float) r12     // Catch:{ all -> 0x024d }
            int r13 = r0 / 2
            float r13 = (float) r13     // Catch:{ all -> 0x024d }
            int r0 = r0 / 2
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ all -> 0x024d }
            int r0 = r0 - r17
            float r0 = (float) r0     // Catch:{ all -> 0x024d }
            r17 = r15
            android.graphics.Path$Direction r15 = android.graphics.Path.Direction.CW     // Catch:{ all -> 0x024d }
            r1.addCircle(r12, r13, r0, r15)     // Catch:{ all -> 0x024d }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x024d }
            r0.toggleInverseFillType()     // Catch:{ all -> 0x024d }
            goto L_0x021d
        L_0x021b:
            r17 = r15
        L_0x021d:
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x024d }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ all -> 0x024d }
            float r1 = (float) r1     // Catch:{ all -> 0x024d }
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ all -> 0x024d }
            float r12 = (float) r12     // Catch:{ all -> 0x024d }
            r13 = 1110966272(0x42380000, float:46.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x024d }
            float r15 = (float) r15     // Catch:{ all -> 0x024d }
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x024d }
            float r13 = (float) r13     // Catch:{ all -> 0x024d }
            r0.set(r1, r12, r15, r13)     // Catch:{ all -> 0x024d }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x024d }
            android.graphics.Paint r1 = roundPaint     // Catch:{ all -> 0x024d }
            r12 = 0
            r9.drawBitmap(r14, r12, r0, r1)     // Catch:{ all -> 0x024d }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x024d }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x024d }
            r9.drawPath(r0, r1)     // Catch:{ all -> 0x024d }
            r9.setBitmap(r12)     // Catch:{ Exception -> 0x024a }
        L_0x024a:
            r9 = r17
            goto L_0x025b
        L_0x024d:
            r0 = move-exception
            r9 = r14
            goto L_0x0255
        L_0x0250:
            r9 = r14
            goto L_0x025b
        L_0x0252:
            r0 = move-exception
            r12 = 0
            r9 = r12
        L_0x0255:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02b8 }
            goto L_0x025b
        L_0x0259:
            r12 = 0
            r9 = r12
        L_0x025b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b8 }
            r0.<init>()     // Catch:{ all -> 0x02b8 }
            r0.append(r7)     // Catch:{ all -> 0x02b8 }
            r0.append(r10)     // Catch:{ all -> 0x02b8 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02b8 }
            boolean r1 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x02b8 }
            if (r1 == 0) goto L_0x0272
            java.lang.String r6 = " "
        L_0x0272:
            android.content.pm.ShortcutInfo$Builder r1 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ all -> 0x02b8 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            r1.<init>(r10, r0)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r1 = r1.setShortLabel(r6)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r1 = r1.setLongLabel(r6)     // Catch:{ all -> 0x02b8 }
            android.content.pm.ShortcutInfo$Builder r1 = r1.setIntent(r3)     // Catch:{ all -> 0x02b8 }
            if (r9 == 0) goto L_0x028f
            android.graphics.drawable.Icon r3 = android.graphics.drawable.Icon.createWithBitmap(r9)     // Catch:{ all -> 0x02b8 }
            r1.setIcon(r3)     // Catch:{ all -> 0x02b8 }
            goto L_0x029b
        L_0x028f:
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02b8 }
            r6 = 2131165867(0x7var_ab, float:1.7945963E38)
            android.graphics.drawable.Icon r3 = android.graphics.drawable.Icon.createWithResource(r3, r6)     // Catch:{ all -> 0x02b8 }
            r1.setIcon(r3)     // Catch:{ all -> 0x02b8 }
        L_0x029b:
            android.content.pm.ShortcutInfo r1 = r1.build()     // Catch:{ all -> 0x02b8 }
            r5.add(r1)     // Catch:{ all -> 0x02b8 }
            boolean r0 = r4.contains(r0)     // Catch:{ all -> 0x02b8 }
            if (r0 == 0) goto L_0x02ac
            r2.updateShortcuts(r5)     // Catch:{ all -> 0x02b8 }
            goto L_0x02af
        L_0x02ac:
            r2.addDynamicShortcuts(r5)     // Catch:{ all -> 0x02b8 }
        L_0x02af:
            r5.clear()     // Catch:{ all -> 0x02b8 }
        L_0x02b2:
            int r8 = r8 + 1
            r1 = r21
            goto L_0x00fd
        L_0x02b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$buildShortcuts$69$MediaDataController(java.util.ArrayList):void");
    }

    public void loadHints(boolean z) {
        if (!this.loading && getUserConfig().suggestContacts) {
            if (!z) {
                this.loading = true;
                TLRPC.TL_contacts_getTopPeers tL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
                tL_contacts_getTopPeers.hash = 0;
                tL_contacts_getTopPeers.bots_pm = false;
                tL_contacts_getTopPeers.correspondents = true;
                tL_contacts_getTopPeers.groups = false;
                tL_contacts_getTopPeers.channels = false;
                tL_contacts_getTopPeers.bots_inline = true;
                tL_contacts_getTopPeers.offset = 0;
                tL_contacts_getTopPeers.limit = 20;
                getConnectionsManager().sendRequest(tL_contacts_getTopPeers, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$loadHints$76$MediaDataController(tLObject, tL_error);
                    }
                });
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
                    public final void run() {
                        MediaDataController.this.lambda$loadHints$71$MediaDataController();
                    }
                });
                this.loaded = true;
            }
        }
    }

    public /* synthetic */ void lambda$loadHints$71$MediaDataController() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        int clientUserId = getUserConfig().getClientUserId();
        try {
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                if (intValue != clientUserId) {
                    int intValue2 = queryFinalized.intValue(1);
                    TLRPC.TL_topPeer tL_topPeer = new TLRPC.TL_topPeer();
                    tL_topPeer.rating = queryFinalized.doubleValue(2);
                    if (intValue > 0) {
                        tL_topPeer.peer = new TLRPC.TL_peerUser();
                        tL_topPeer.peer.user_id = intValue;
                        arrayList5.add(Integer.valueOf(intValue));
                    } else {
                        tL_topPeer.peer = new TLRPC.TL_peerChat();
                        int i = -intValue;
                        tL_topPeer.peer.chat_id = i;
                        arrayList6.add(Integer.valueOf(i));
                    }
                    if (intValue2 == 0) {
                        arrayList.add(tL_topPeer);
                    } else if (intValue2 == 1) {
                        arrayList2.add(tL_topPeer);
                    }
                }
            }
            queryFinalized.dispose();
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList6), arrayList4);
            }
            AndroidUtilities.runOnUIThread(new Runnable(arrayList3, arrayList4, arrayList, arrayList2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;
                private final /* synthetic */ ArrayList f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$70$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$70$MediaDataController(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        this.loading = false;
        this.loaded = true;
        this.hints = arrayList3;
        this.inlineBots = arrayList4;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    public /* synthetic */ void lambda$loadHints$76$MediaDataController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$74$MediaDataController(this.f$1);
                }
            });
        } else if (tLObject instanceof TLRPC.TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$75$MediaDataController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$74$MediaDataController(TLObject tLObject) {
        TLRPC.TL_contacts_topPeers tL_contacts_topPeers = (TLRPC.TL_contacts_topPeers) tLObject;
        getMessagesController().putUsers(tL_contacts_topPeers.users, false);
        getMessagesController().putChats(tL_contacts_topPeers.chats, false);
        for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
            TLRPC.TL_topPeerCategoryPeers tL_topPeerCategoryPeers = tL_contacts_topPeers.categories.get(i);
            if (tL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline) {
                this.inlineBots = tL_topPeerCategoryPeers.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = tL_topPeerCategoryPeers.peers;
                int clientUserId = getUserConfig().getClientUserId();
                int i2 = 0;
                while (true) {
                    if (i2 >= this.hints.size()) {
                        break;
                    } else if (this.hints.get(i2).peer.user_id == clientUserId) {
                        this.hints.remove(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(tL_contacts_topPeers) {
            private final /* synthetic */ TLRPC.TL_contacts_topPeers f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$null$73$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$73$MediaDataController(TLRPC.TL_contacts_topPeers tL_contacts_topPeers) {
        int i;
        int i2;
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(tL_contacts_topPeers.users, tL_contacts_topPeers.chats, false, false);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int i3 = 0; i3 < tL_contacts_topPeers.categories.size(); i3++) {
                TLRPC.TL_topPeerCategoryPeers tL_topPeerCategoryPeers = tL_contacts_topPeers.categories.get(i3);
                int i4 = tL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline ? 1 : 0;
                for (int i5 = 0; i5 < tL_topPeerCategoryPeers.peers.size(); i5++) {
                    TLRPC.TL_topPeer tL_topPeer = tL_topPeerCategoryPeers.peers.get(i5);
                    if (tL_topPeer.peer instanceof TLRPC.TL_peerUser) {
                        i = tL_topPeer.peer.user_id;
                    } else {
                        if (tL_topPeer.peer instanceof TLRPC.TL_peerChat) {
                            i2 = tL_topPeer.peer.chat_id;
                        } else {
                            i2 = tL_topPeer.peer.channel_id;
                        }
                        i = -i2;
                    }
                    executeFast.requery();
                    executeFast.bindInteger(1, i);
                    executeFast.bindInteger(2, i4);
                    executeFast.bindDouble(3, tL_topPeer.rating);
                    executeFast.bindInteger(4, 0);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$72$MediaDataController();
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$72$MediaDataController() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    public /* synthetic */ void lambda$null$75$MediaDataController() {
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
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                MediaDataController.this.lambda$clearTopPeers$77$MediaDataController();
            }
        });
        buildShortcuts();
    }

    public /* synthetic */ void lambda$clearTopPeers$77$MediaDataController() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void increaseInlineRaiting(int i) {
        if (getUserConfig().suggestContacts) {
            int max = getUserConfig().botRatingLoadTime != 0 ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime) : 60;
            TLRPC.TL_topPeer tL_topPeer = null;
            int i2 = 0;
            while (true) {
                if (i2 >= this.inlineBots.size()) {
                    break;
                }
                TLRPC.TL_topPeer tL_topPeer2 = this.inlineBots.get(i2);
                if (tL_topPeer2.peer.user_id == i) {
                    tL_topPeer = tL_topPeer2;
                    break;
                }
                i2++;
            }
            if (tL_topPeer == null) {
                tL_topPeer = new TLRPC.TL_topPeer();
                tL_topPeer.peer = new TLRPC.TL_peerUser();
                tL_topPeer.peer.user_id = i;
                this.inlineBots.add(tL_topPeer);
            }
            tL_topPeer.rating += Math.exp((double) (max / getMessagesController().ratingDecay));
            Collections.sort(this.inlineBots, $$Lambda$MediaDataController$bWYlhelD_fiN8Ppmduxu_mGPVI.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList<TLRPC.TL_topPeer> arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(i, 1, tL_topPeer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    static /* synthetic */ int lambda$increaseInlineRaiting$78(TLRPC.TL_topPeer tL_topPeer, TLRPC.TL_topPeer tL_topPeer2) {
        double d = tL_topPeer.rating;
        double d2 = tL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    public void removeInline(int i) {
        for (int i2 = 0; i2 < this.inlineBots.size(); i2++) {
            if (this.inlineBots.get(i2).peer.user_id == i) {
                this.inlineBots.remove(i2);
                TLRPC.TL_contacts_resetTopPeerRating tL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryBotsInline();
                tL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(i);
                getConnectionsManager().sendRequest(tL_contacts_resetTopPeerRating, $$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc.INSTANCE);
                deletePeer(i, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public void removePeer(int i) {
        for (int i2 = 0; i2 < this.hints.size(); i2++) {
            if (this.hints.get(i2).peer.user_id == i) {
                this.hints.remove(i2);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC.TL_contacts_resetTopPeerRating tL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryCorrespondents();
                tL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(i);
                deletePeer(i, 0);
                getConnectionsManager().sendRequest(tL_contacts_resetTopPeerRating, $$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE7qciOI.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(long j) {
        int i;
        if (getUserConfig().suggestContacts && (i = (int) j) > 0) {
            TLRPC.User user = i > 0 ? getMessagesController().getUser(Integer.valueOf(i)) : null;
            if (user != null && !user.bot && !user.self) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, i) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$increasePeerRaiting$83$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$increasePeerRaiting$83$MediaDataController(long j, int i) {
        int i2;
        double d = 0.0d;
        try {
            int i3 = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                i3 = queryFinalized.intValue(0);
                i2 = queryFinalized.intValue(1);
            } else {
                i2 = 0;
            }
            queryFinalized.dispose();
            if (i3 > 0 && getUserConfig().ratingLoadTime != 0) {
                d = (double) (i2 - getUserConfig().ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new Runnable(i, d, j) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ double f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$null$82$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$82$MediaDataController(int i, double d, long j) {
        TLRPC.TL_topPeer tL_topPeer;
        int i2 = 0;
        while (true) {
            if (i2 >= this.hints.size()) {
                tL_topPeer = null;
                break;
            }
            tL_topPeer = this.hints.get(i2);
            if (i < 0) {
                TLRPC.Peer peer = tL_topPeer.peer;
                int i3 = -i;
                if (peer.chat_id != i3) {
                    if (peer.channel_id == i3) {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (i > 0 && tL_topPeer.peer.user_id == i) {
                break;
            }
            i2++;
        }
        if (tL_topPeer == null) {
            tL_topPeer = new TLRPC.TL_topPeer();
            if (i > 0) {
                tL_topPeer.peer = new TLRPC.TL_peerUser();
                tL_topPeer.peer.user_id = i;
            } else {
                tL_topPeer.peer = new TLRPC.TL_peerChat();
                tL_topPeer.peer.chat_id = -i;
            }
            this.hints.add(tL_topPeer);
        }
        double d2 = tL_topPeer.rating;
        double d3 = (double) getMessagesController().ratingDecay;
        Double.isNaN(d3);
        tL_topPeer.rating = d2 + Math.exp(d / d3);
        Collections.sort(this.hints, $$Lambda$MediaDataController$LbWkqVH_jfo54tkBgQFIE4DI4eg.INSTANCE);
        savePeer((int) j, 0, tL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    static /* synthetic */ int lambda$null$81(TLRPC.TL_topPeer tL_topPeer, TLRPC.TL_topPeer tL_topPeer2) {
        double d = tL_topPeer.rating;
        double d2 = tL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    private void savePeer(int i, int i2, double d) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, i2, d) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ double f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$savePeer$84$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$savePeer$84$MediaDataController(int i, int i2, double d) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindInteger(2, i2);
            executeFast.bindDouble(3, d);
            executeFast.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void deletePeer(int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, i2) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$deletePeer$85$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$deletePeer$85$MediaDataController(int i, int i2) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private Intent createIntrnalShortcutIntent(long j) {
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        int i = (int) j;
        int i2 = (int) (j >> 32);
        if (i == 0) {
            intent.putExtra("encId", i2);
            if (getMessagesController().getEncryptedChat(Integer.valueOf(i2)) == null) {
                return null;
            }
        } else if (i > 0) {
            intent.putExtra("userId", i);
        } else if (i >= 0) {
            return null;
        } else {
            intent.putExtra("chatId", -i);
        }
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setAction("com.tmessages.openchat" + j);
        intent.addFlags(67108864);
        return intent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0058 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x007c A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A[SYNTHETIC, Splitter:B:33:0x0094] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c0 A[Catch:{ all -> 0x014f }] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cf A[Catch:{ all -> 0x014f }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A[Catch:{ Exception -> 0x023d }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A[Catch:{ Exception -> 0x023d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installShortcut(long r17) {
        /*
            r16 = this;
            r1 = r17
            android.content.Intent r3 = r16.createIntrnalShortcutIntent(r17)     // Catch:{ Exception -> 0x023d }
            int r0 = (int) r1     // Catch:{ Exception -> 0x023d }
            r4 = 32
            long r4 = r1 >> r4
            int r5 = (int) r4     // Catch:{ Exception -> 0x023d }
            r4 = 0
            if (r0 != 0) goto L_0x002d
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()     // Catch:{ Exception -> 0x023d }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r5)     // Catch:{ Exception -> 0x023d }
            if (r0 != 0) goto L_0x001e
            return
        L_0x001e:
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x023d }
            int r0 = r0.user_id     // Catch:{ Exception -> 0x023d }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x023d }
            goto L_0x003b
        L_0x002d:
            if (r0 <= 0) goto L_0x003e
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x023d }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x023d }
        L_0x003b:
            r5 = r0
            r6 = r4
            goto L_0x004f
        L_0x003e:
            if (r0 >= 0) goto L_0x023c
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x023d }
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$Chat r0 = r5.getChat(r0)     // Catch:{ Exception -> 0x023d }
            r6 = r0
            r5 = r4
        L_0x004f:
            if (r5 != 0) goto L_0x0054
            if (r6 != 0) goto L_0x0054
            return
        L_0x0054:
            r0 = 1
            r7 = 0
            if (r5 == 0) goto L_0x007c
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r5)     // Catch:{ Exception -> 0x023d }
            if (r8 == 0) goto L_0x006b
            java.lang.String r8 = "SavedMessages"
            r9 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x023d }
            r9 = r4
            r10 = r8
            r8 = 1
            goto L_0x008a
        L_0x006b:
            java.lang.String r8 = r5.first_name     // Catch:{ Exception -> 0x023d }
            java.lang.String r9 = r5.last_name     // Catch:{ Exception -> 0x023d }
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r5.photo     // Catch:{ Exception -> 0x023d }
            if (r9 == 0) goto L_0x0087
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r5.photo     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x023d }
            goto L_0x0088
        L_0x007c:
            java.lang.String r8 = r6.title     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r6.photo     // Catch:{ Exception -> 0x023d }
            if (r9 == 0) goto L_0x0087
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r6.photo     // Catch:{ Exception -> 0x023d }
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x023d }
            goto L_0x0088
        L_0x0087:
            r9 = r4
        L_0x0088:
            r10 = r8
            r8 = 0
        L_0x008a:
            if (r8 != 0) goto L_0x0092
            if (r9 == 0) goto L_0x008f
            goto L_0x0092
        L_0x008f:
            r9 = r4
            goto L_0x0153
        L_0x0092:
            if (r8 != 0) goto L_0x00a5
            java.io.File r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r0)     // Catch:{ all -> 0x00a1 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00a1 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9)     // Catch:{ all -> 0x00a1 }
            goto L_0x00a6
        L_0x00a1:
            r0 = move-exception
            r9 = r4
            goto L_0x0150
        L_0x00a5:
            r9 = r4
        L_0x00a6:
            if (r8 != 0) goto L_0x00aa
            if (r9 == 0) goto L_0x0153
        L_0x00aa:
            r11 = 1114112000(0x42680000, float:58.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x014f }
            android.graphics.Bitmap$Config r12 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x014f }
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r11, r11, r12)     // Catch:{ all -> 0x014f }
            r12.eraseColor(r7)     // Catch:{ all -> 0x014f }
            android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x014f }
            r13.<init>(r12)     // Catch:{ all -> 0x014f }
            if (r8 == 0) goto L_0x00cf
            org.telegram.ui.Components.AvatarDrawable r8 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x014f }
            r8.<init>((org.telegram.tgnet.TLRPC.User) r5)     // Catch:{ all -> 0x014f }
            r8.setAvatarType(r0)     // Catch:{ all -> 0x014f }
            r8.setBounds(r7, r7, r11, r11)     // Catch:{ all -> 0x014f }
            r8.draw(r13)     // Catch:{ all -> 0x014f }
            goto L_0x0120
        L_0x00cf:
            android.graphics.BitmapShader r8 = new android.graphics.BitmapShader     // Catch:{ all -> 0x014f }
            android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x014f }
            android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x014f }
            r8.<init>(r9, r14, r15)     // Catch:{ all -> 0x014f }
            android.graphics.Paint r14 = roundPaint     // Catch:{ all -> 0x014f }
            if (r14 != 0) goto L_0x00ea
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x014f }
            r14.<init>(r0)     // Catch:{ all -> 0x014f }
            roundPaint = r14     // Catch:{ all -> 0x014f }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x014f }
            r0.<init>()     // Catch:{ all -> 0x014f }
            bitmapRect = r0     // Catch:{ all -> 0x014f }
        L_0x00ea:
            float r0 = (float) r11     // Catch:{ all -> 0x014f }
            int r14 = r9.getWidth()     // Catch:{ all -> 0x014f }
            float r14 = (float) r14     // Catch:{ all -> 0x014f }
            float r0 = r0 / r14
            r13.save()     // Catch:{ all -> 0x014f }
            r13.scale(r0, r0)     // Catch:{ all -> 0x014f }
            android.graphics.Paint r0 = roundPaint     // Catch:{ all -> 0x014f }
            r0.setShader(r8)     // Catch:{ all -> 0x014f }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x014f }
            int r8 = r9.getWidth()     // Catch:{ all -> 0x014f }
            float r8 = (float) r8     // Catch:{ all -> 0x014f }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x014f }
            float r14 = (float) r14     // Catch:{ all -> 0x014f }
            r15 = 0
            r0.set(r15, r15, r8, r14)     // Catch:{ all -> 0x014f }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x014f }
            int r8 = r9.getWidth()     // Catch:{ all -> 0x014f }
            float r8 = (float) r8     // Catch:{ all -> 0x014f }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x014f }
            float r14 = (float) r14     // Catch:{ all -> 0x014f }
            android.graphics.Paint r15 = roundPaint     // Catch:{ all -> 0x014f }
            r13.drawRoundRect(r0, r8, r14, r15)     // Catch:{ all -> 0x014f }
            r13.restore()     // Catch:{ all -> 0x014f }
        L_0x0120:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x014f }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x014f }
            r8 = 2131165293(0x7var_d, float:1.79448E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r8)     // Catch:{ all -> 0x014f }
            r8 = 1097859072(0x41700000, float:15.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x014f }
            int r11 = r11 - r8
            r14 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x014f }
            int r15 = r11 - r15
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x014f }
            int r11 = r11 - r14
            int r14 = r15 + r8
            int r8 = r8 + r11
            r0.setBounds(r15, r11, r14, r8)     // Catch:{ all -> 0x014f }
            r0.draw(r13)     // Catch:{ all -> 0x014f }
            r13.setBitmap(r4)     // Catch:{ Exception -> 0x014d }
        L_0x014d:
            r9 = r12
            goto L_0x0153
        L_0x014f:
            r0 = move-exception
        L_0x0150:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x023d }
        L_0x0153:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x023d }
            r8 = 26
            r11 = 2131165291(0x7var_b, float:1.7944795E38)
            r12 = 2131165292(0x7var_c, float:1.7944797E38)
            r13 = 2131165290(0x7var_a, float:1.7944793E38)
            r14 = 2131165294(0x7var_e, float:1.7944801E38)
            if (r0 < r8) goto L_0x01da
            android.content.pm.ShortcutInfo$Builder r0 = new android.content.pm.ShortcutInfo$Builder     // Catch:{ Exception -> 0x023d }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023d }
            r8.<init>()     // Catch:{ Exception -> 0x023d }
            java.lang.String r15 = "sdid_"
            r8.append(r15)     // Catch:{ Exception -> 0x023d }
            r8.append(r1)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = r8.toString()     // Catch:{ Exception -> 0x023d }
            r0.<init>(r7, r1)     // Catch:{ Exception -> 0x023d }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setShortLabel(r10)     // Catch:{ Exception -> 0x023d }
            android.content.pm.ShortcutInfo$Builder r0 = r0.setIntent(r3)     // Catch:{ Exception -> 0x023d }
            if (r9 == 0) goto L_0x018f
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithBitmap(r9)     // Catch:{ Exception -> 0x023d }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x01c8
        L_0x018f:
            if (r5 == 0) goto L_0x01a9
            boolean r1 = r5.bot     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x019f
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithResource(r1, r13)     // Catch:{ Exception -> 0x023d }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x01c8
        L_0x019f:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithResource(r1, r14)     // Catch:{ Exception -> 0x023d }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x01c8
        L_0x01a9:
            if (r6 == 0) goto L_0x01c8
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x023d }
            if (r1 == 0) goto L_0x01bf
            boolean r1 = r6.megagroup     // Catch:{ Exception -> 0x023d }
            if (r1 != 0) goto L_0x01bf
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithResource(r1, r11)     // Catch:{ Exception -> 0x023d }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x023d }
            goto L_0x01c8
        L_0x01bf:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.graphics.drawable.Icon r1 = android.graphics.drawable.Icon.createWithResource(r1, r12)     // Catch:{ Exception -> 0x023d }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x023d }
        L_0x01c8:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            java.lang.Class<android.content.pm.ShortcutManager> r2 = android.content.pm.ShortcutManager.class
            java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ Exception -> 0x023d }
            android.content.pm.ShortcutManager r1 = (android.content.pm.ShortcutManager) r1     // Catch:{ Exception -> 0x023d }
            android.content.pm.ShortcutInfo r0 = r0.build()     // Catch:{ Exception -> 0x023d }
            r1.requestPinShortcut(r0, r4)     // Catch:{ Exception -> 0x023d }
            goto L_0x0241
        L_0x01da:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x023d }
            r0.<init>()     // Catch:{ Exception -> 0x023d }
            if (r9 == 0) goto L_0x01e7
            java.lang.String r1 = "android.intent.extra.shortcut.ICON"
            r0.putExtra(r1, r9)     // Catch:{ Exception -> 0x023d }
            goto L_0x0222
        L_0x01e7:
            java.lang.String r1 = "android.intent.extra.shortcut.ICON_RESOURCE"
            if (r5 == 0) goto L_0x0203
            boolean r2 = r5.bot     // Catch:{ Exception -> 0x023d }
            if (r2 == 0) goto L_0x01f9
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r13)     // Catch:{ Exception -> 0x023d }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x023d }
            goto L_0x0222
        L_0x01f9:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r14)     // Catch:{ Exception -> 0x023d }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x023d }
            goto L_0x0222
        L_0x0203:
            if (r6 == 0) goto L_0x0222
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x023d }
            if (r2 == 0) goto L_0x0219
            boolean r2 = r6.megagroup     // Catch:{ Exception -> 0x023d }
            if (r2 != 0) goto L_0x0219
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r11)     // Catch:{ Exception -> 0x023d }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x023d }
            goto L_0x0222
        L_0x0219:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r12)     // Catch:{ Exception -> 0x023d }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x023d }
        L_0x0222:
            java.lang.String r1 = "android.intent.extra.shortcut.INTENT"
            r0.putExtra(r1, r3)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = "android.intent.extra.shortcut.NAME"
            r0.putExtra(r1, r10)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = "duplicate"
            r0.putExtra(r1, r7)     // Catch:{ Exception -> 0x023d }
            java.lang.String r1 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r0.setAction(r1)     // Catch:{ Exception -> 0x023d }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x023d }
            r1.sendBroadcast(r0)     // Catch:{ Exception -> 0x023d }
            goto L_0x0241
        L_0x023c:
            return
        L_0x023d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0241:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    public void uninstallShortcut(long j) {
        TLRPC.User user;
        String str;
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                ArrayList arrayList = new ArrayList();
                arrayList.add("sdid_" + j);
                ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).removeDynamicShortcuts(arrayList);
                return;
            }
            int i = (int) j;
            int i2 = (int) (j >> 32);
            TLRPC.Chat chat = null;
            if (i == 0) {
                TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(i2));
                if (encryptedChat != null) {
                    user = getMessagesController().getUser(Integer.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (i > 0) {
                user = getMessagesController().getUser(Integer.valueOf(i));
            } else if (i < 0) {
                chat = getMessagesController().getChat(Integer.valueOf(-i));
                user = null;
            } else {
                return;
            }
            if (user != null || chat != null) {
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name);
                } else {
                    str = chat.title;
                }
                Intent intent = new Intent();
                intent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(j));
                intent.putExtra("android.intent.extra.shortcut.NAME", str);
                intent.putExtra("duplicate", false);
                intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                ApplicationLoader.applicationContext.sendBroadcast(intent);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$static$86(TLRPC.MessageEntity messageEntity, TLRPC.MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public /* synthetic */ void lambda$loadPinnedMessage$87$MediaDataController(long j, int i, int i2) {
        loadPinnedMessageInternal(j, i, i2, false);
    }

    public MessageObject loadPinnedMessage(long j, int i, int i2, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(j, i, i2, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j, i, i2) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                MediaDataController.this.lambda$loadPinnedMessage$87$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0087 A[Catch:{ Exception -> 0x015d }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00dc A[Catch:{ Exception -> 0x015d }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x011e A[Catch:{ Exception -> 0x015d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.messenger.MessageObject loadPinnedMessageInternal(long r17, int r19, int r20, boolean r21) {
        /*
            r16 = this;
            r7 = r16
            r0 = r17
            r2 = r19
            r3 = r20
            if (r2 == 0) goto L_0x0011
            long r4 = (long) r3
            long r8 = (long) r2
            r6 = 32
            long r8 = r8 << r6
            long r4 = r4 | r8
            goto L_0x0012
        L_0x0011:
            long r4 = (long) r3
        L_0x0012:
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x015d }
            r6.<init>()     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ Exception -> 0x015d }
            r9.<init>()     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x015d }
            r10.<init>()     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x015d }
            r11.<init>()     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.MessagesStorage r12 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x015d }
            org.telegram.SQLite.SQLiteDatabase r12 = r12.getDatabase()     // Catch:{ Exception -> 0x015d }
            java.util.Locale r13 = java.util.Locale.US     // Catch:{ Exception -> 0x015d }
            java.lang.String r14 = "SELECT data, mid, date FROM messages WHERE mid = %d"
            r15 = 1
            java.lang.Object[] r8 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x015d }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x015d }
            r5 = 0
            r8[r5] = r4     // Catch:{ Exception -> 0x015d }
            java.lang.String r4 = java.lang.String.format(r13, r14, r8)     // Catch:{ Exception -> 0x015d }
            java.lang.Object[] r8 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x015d }
            org.telegram.SQLite.SQLiteCursor r4 = r12.queryFinalized(r4, r8)     // Catch:{ Exception -> 0x015d }
            boolean r8 = r4.next()     // Catch:{ Exception -> 0x015d }
            if (r8 == 0) goto L_0x0081
            org.telegram.tgnet.NativeByteBuffer r8 = r4.byteBufferValue(r5)     // Catch:{ Exception -> 0x015d }
            if (r8 == 0) goto L_0x0081
            int r12 = r8.readInt32(r5)     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.TLRPC$Message r12 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r12, r5)     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.UserConfig r13 = r16.getUserConfig()     // Catch:{ Exception -> 0x015d }
            int r13 = r13.clientUserId     // Catch:{ Exception -> 0x015d }
            r12.readAttachPath(r8, r13)     // Catch:{ Exception -> 0x015d }
            r8.reuse()     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.TLRPC$MessageAction r8 = r12.action     // Catch:{ Exception -> 0x015d }
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear     // Catch:{ Exception -> 0x015d }
            if (r8 == 0) goto L_0x006d
            goto L_0x0081
        L_0x006d:
            int r8 = r4.intValue(r15)     // Catch:{ Exception -> 0x015d }
            r12.id = r8     // Catch:{ Exception -> 0x015d }
            r8 = 2
            int r8 = r4.intValue(r8)     // Catch:{ Exception -> 0x015d }
            r12.date = r8     // Catch:{ Exception -> 0x015d }
            r12.dialog_id = r0     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r12, r10, r11)     // Catch:{ Exception -> 0x015d }
            r8 = r12
            goto L_0x0082
        L_0x0081:
            r8 = 0
        L_0x0082:
            r4.dispose()     // Catch:{ Exception -> 0x015d }
            if (r8 != 0) goto L_0x00da
            org.telegram.messenger.MessagesStorage r4 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x015d }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x015d }
            java.util.Locale r12 = java.util.Locale.US     // Catch:{ Exception -> 0x015d }
            java.lang.String r13 = "SELECT data FROM chat_pinned WHERE uid = %d"
            java.lang.Object[] r14 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x015d }
            java.lang.Long r15 = java.lang.Long.valueOf(r17)     // Catch:{ Exception -> 0x015d }
            r14[r5] = r15     // Catch:{ Exception -> 0x015d }
            java.lang.String r12 = java.lang.String.format(r12, r13, r14)     // Catch:{ Exception -> 0x015d }
            java.lang.Object[] r13 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x015d }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r12, r13)     // Catch:{ Exception -> 0x015d }
            boolean r12 = r4.next()     // Catch:{ Exception -> 0x015d }
            if (r12 == 0) goto L_0x00d7
            org.telegram.tgnet.NativeByteBuffer r12 = r4.byteBufferValue(r5)     // Catch:{ Exception -> 0x015d }
            if (r12 == 0) goto L_0x00d7
            int r8 = r12.readInt32(r5)     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.TLRPC$Message r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r12, r8, r5)     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.UserConfig r5 = r16.getUserConfig()     // Catch:{ Exception -> 0x015d }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x015d }
            r8.readAttachPath(r12, r5)     // Catch:{ Exception -> 0x015d }
            r12.reuse()     // Catch:{ Exception -> 0x015d }
            int r5 = r8.id     // Catch:{ Exception -> 0x015d }
            if (r5 != r3) goto L_0x00d6
            org.telegram.tgnet.TLRPC$MessageAction r5 = r8.action     // Catch:{ Exception -> 0x015d }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear     // Catch:{ Exception -> 0x015d }
            if (r5 == 0) goto L_0x00d0
            goto L_0x00d6
        L_0x00d0:
            r8.dialog_id = r0     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r10, r11)     // Catch:{ Exception -> 0x015d }
            goto L_0x00d7
        L_0x00d6:
            r8 = 0
        L_0x00d7:
            r4.dispose()     // Catch:{ Exception -> 0x015d }
        L_0x00da:
            if (r8 != 0) goto L_0x011e
            if (r2 == 0) goto L_0x0103
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r0 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages     // Catch:{ Exception -> 0x015d }
            r0.<init>()     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.getInputChannel((int) r2)     // Catch:{ Exception -> 0x015d }
            r0.channel = r1     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList<java.lang.Integer> r1 = r0.id     // Catch:{ Exception -> 0x015d }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r20)     // Catch:{ Exception -> 0x015d }
            r1.add(r3)     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.ConnectionsManager r1 = r16.getConnectionsManager()     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.-$$Lambda$MediaDataController$EoBCIKMUyQQkfYWQ7VLIfPEQ4wM r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$EoBCIKMUyQQkfYWQ7VLIfPEQ4wM     // Catch:{ Exception -> 0x015d }
            r3.<init>(r2)     // Catch:{ Exception -> 0x015d }
            r1.sendRequest(r0, r3)     // Catch:{ Exception -> 0x015d }
            goto L_0x0161
        L_0x0103:
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r0 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages     // Catch:{ Exception -> 0x015d }
            r0.<init>()     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList<java.lang.Integer> r1 = r0.id     // Catch:{ Exception -> 0x015d }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r20)     // Catch:{ Exception -> 0x015d }
            r1.add(r3)     // Catch:{ Exception -> 0x015d }
            org.telegram.tgnet.ConnectionsManager r1 = r16.getConnectionsManager()     // Catch:{ Exception -> 0x015d }
            org.telegram.messenger.-$$Lambda$MediaDataController$SudRK0B-OBB33PZ3f8c-n_O5sgc r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$SudRK0B-OBB33PZ3f8c-n_O5sgc     // Catch:{ Exception -> 0x015d }
            r3.<init>(r2)     // Catch:{ Exception -> 0x015d }
            r1.sendRequest(r0, r3)     // Catch:{ Exception -> 0x015d }
            goto L_0x0161
        L_0x011e:
            if (r21 == 0) goto L_0x012d
            r5 = 1
            r1 = r16
            r2 = r8
            r3 = r6
            r4 = r9
            r6 = r21
            org.telegram.messenger.MessageObject r0 = r1.broadcastPinnedMessage(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x015d }
            return r0
        L_0x012d:
            boolean r0 = r10.isEmpty()     // Catch:{ Exception -> 0x015d }
            java.lang.String r1 = ","
            if (r0 != 0) goto L_0x0140
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x015d }
            java.lang.String r2 = android.text.TextUtils.join(r1, r10)     // Catch:{ Exception -> 0x015d }
            r0.getUsersInternal(r2, r6)     // Catch:{ Exception -> 0x015d }
        L_0x0140:
            boolean r0 = r11.isEmpty()     // Catch:{ Exception -> 0x015d }
            if (r0 != 0) goto L_0x0151
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()     // Catch:{ Exception -> 0x015d }
            java.lang.String r1 = android.text.TextUtils.join(r1, r11)     // Catch:{ Exception -> 0x015d }
            r0.getChatsInternal(r1, r9)     // Catch:{ Exception -> 0x015d }
        L_0x0151:
            r5 = 1
            r0 = 0
            r1 = r16
            r2 = r8
            r3 = r6
            r4 = r9
            r6 = r0
            r1.broadcastPinnedMessage(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x015d }
            goto L_0x0161
        L_0x015d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0161:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, int, int, boolean):org.telegram.messenger.MessageObject");
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$88$MediaDataController(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
            r8 = this;
            r0 = 1
            r1 = 0
            if (r11 != 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$messages_Messages r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            removeEmptyMessages(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x0042
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            java.lang.Object r11 = r11.get(r1)
            r3 = r11
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC.Message) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r10.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r10.chats
            r6 = 0
            r7 = 0
            r2 = r8
            r2.broadcastPinnedMessage(r3, r4, r5, r6, r7)
            org.telegram.messenger.MessagesStorage r11 = r8.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r10.chats
            r11.putUsersAndChats(r2, r3, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r10 = r10.messages
            java.lang.Object r10 = r10.get(r1)
            org.telegram.tgnet.TLRPC$Message r10 = (org.telegram.tgnet.TLRPC.Message) r10
            r8.savePinnedMessage(r10)
            goto L_0x0043
        L_0x0042:
            r0 = 0
        L_0x0043:
            if (r0 != 0) goto L_0x004c
            org.telegram.messenger.MessagesStorage r10 = r8.getMessagesStorage()
            r10.updateChatPinnedMessage(r9, r1)
        L_0x004c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$88$MediaDataController(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:7:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$89$MediaDataController(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
            r8 = this;
            r0 = 1
            r1 = 0
            if (r11 != 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$messages_Messages r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            removeEmptyMessages(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x0042
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r10.messages
            java.lang.Object r11 = r11.get(r1)
            r3 = r11
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC.Message) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r10.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r10.chats
            r6 = 0
            r7 = 0
            r2 = r8
            r2.broadcastPinnedMessage(r3, r4, r5, r6, r7)
            org.telegram.messenger.MessagesStorage r11 = r8.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r10.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r3 = r10.chats
            r11.putUsersAndChats(r2, r3, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r10 = r10.messages
            java.lang.Object r10 = r10.get(r1)
            org.telegram.tgnet.TLRPC$Message r10 = (org.telegram.tgnet.TLRPC.Message) r10
            r8.savePinnedMessage(r10)
            goto L_0x0043
        L_0x0042:
            r0 = 0
        L_0x0043:
            if (r0 != 0) goto L_0x004c
            org.telegram.messenger.MessagesStorage r10 = r8.getMessagesStorage()
            r10.updateChatPinnedMessage(r9, r1)
        L_0x004c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$89$MediaDataController(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessage(TLRPC.Message message) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(message) {
            private final /* synthetic */ TLRPC.Message f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$savePinnedMessage$90$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$savePinnedMessage$90$MediaDataController(TLRPC.Message message) {
        int i;
        int i2;
        try {
            if (message.to_id.channel_id != 0) {
                i2 = message.to_id.channel_id;
            } else if (message.to_id.chat_id != 0) {
                i2 = message.to_id.chat_id;
            } else if (message.to_id.user_id != 0) {
                i = message.to_id.user_id;
                long j = (long) i;
                getMessagesStorage().getDatabase().beginTransaction();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, message.id);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                getMessagesStorage().getDatabase().commitTransaction();
            } else {
                return;
            }
            i = -i2;
            long j2 = (long) i;
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
            NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(message.getObjectSize());
            message.serializeToStream(nativeByteBuffer2);
            executeFast2.requery();
            executeFast2.bindLong(1, j2);
            executeFast2.bindInteger(2, message.id);
            executeFast2.bindByteBuffer(3, nativeByteBuffer2);
            executeFast2.step();
            nativeByteBuffer2.reuse();
            executeFast2.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private MessageObject broadcastPinnedMessage(TLRPC.Message message, ArrayList<TLRPC.User> arrayList, ArrayList<TLRPC.Chat> arrayList2, boolean z, boolean z2) {
        SparseArray sparseArray = new SparseArray();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.User user = arrayList.get(i);
            sparseArray.put(user.id, user);
        }
        SparseArray sparseArray2 = new SparseArray();
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            TLRPC.Chat chat = arrayList2.get(i2);
            sparseArray2.put(chat.id, chat);
        }
        if (z2) {
            return new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(arrayList, z, arrayList2, message, sparseArray, sparseArray2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ TLRPC.Message f$4;
            private final /* synthetic */ SparseArray f$5;
            private final /* synthetic */ SparseArray f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MediaDataController.this.lambda$broadcastPinnedMessage$91$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
        return null;
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$91$MediaDataController(ArrayList arrayList, boolean z, ArrayList arrayList2, TLRPC.Message message, SparseArray sparseArray, SparseArray sparseArray2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
        getNotificationCenter().postNotificationName(NotificationCenter.pinnedMessageDidLoad, new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray, (SparseArray<TLRPC.Chat>) sparseArray2, false));
    }

    private static void removeEmptyMessages(ArrayList<TLRPC.Message> arrayList) {
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC.Message message = arrayList.get(i);
            if (message == null || (message instanceof TLRPC.TL_messageEmpty) || (message.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> arrayList, long j, boolean z, Runnable runnable) {
        ArrayList<MessageObject> arrayList2 = arrayList;
        int i = 0;
        if (((int) j) == 0) {
            ArrayList arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            while (i < arrayList.size()) {
                MessageObject messageObject = arrayList2.get(i);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long j2 = messageObject.messageOwner.reply_to_random_id;
                    ArrayList arrayList4 = (ArrayList) longSparseArray.get(j2);
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList();
                        longSparseArray.put(j2, arrayList4);
                    }
                    arrayList4.add(messageObject);
                    if (!arrayList3.contains(Long.valueOf(j2))) {
                        arrayList3.add(Long.valueOf(j2));
                    }
                }
                i++;
            }
            if (!arrayList3.isEmpty()) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList3, j, longSparseArray, runnable) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ LongSparseArray f$3;
                    private final /* synthetic */ Runnable f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$loadReplyMessagesForMessages$93$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            } else if (runnable != null) {
                runnable.run();
            }
        } else {
            ArrayList arrayList5 = new ArrayList();
            SparseArray sparseArray = new SparseArray();
            StringBuilder sb = new StringBuilder();
            int i2 = 0;
            while (i < arrayList.size()) {
                MessageObject messageObject2 = arrayList2.get(i);
                if (messageObject2.getId() > 0 && messageObject2.isReply() && messageObject2.replyMessageObject == null) {
                    TLRPC.Message message = messageObject2.messageOwner;
                    int i3 = message.reply_to_msg_id;
                    long j3 = (long) i3;
                    int i4 = message.to_id.channel_id;
                    if (i4 != 0) {
                        j3 |= ((long) i4) << 32;
                        i2 = i4;
                    }
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(j3);
                    ArrayList arrayList6 = (ArrayList) sparseArray.get(i3);
                    if (arrayList6 == null) {
                        arrayList6 = new ArrayList();
                        sparseArray.put(i3, arrayList6);
                    }
                    arrayList6.add(messageObject2);
                    if (!arrayList5.contains(Integer.valueOf(i3))) {
                        arrayList5.add(Integer.valueOf(i3));
                    }
                }
                i++;
            }
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(sb, j, arrayList5, sparseArray, i2, z, runnable) {
                    private final /* synthetic */ StringBuilder f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ SparseArray f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ boolean f$6;
                    private final /* synthetic */ Runnable f$7;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                        this.f$7 = r9;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$loadReplyMessagesForMessages$96$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                    }
                });
            } else if (runnable != null) {
                runnable.run();
            }
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$93$MediaDataController(ArrayList arrayList, long j, LongSparseArray longSparseArray, Runnable runnable) {
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j;
                    long longValue = queryFinalized.longValue(3);
                    ArrayList arrayList2 = (ArrayList) longSparseArray.get(longValue);
                    longSparseArray.remove(longValue);
                    if (arrayList2 != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false);
                        for (int i = 0; i < arrayList2.size(); i++) {
                            MessageObject messageObject2 = (MessageObject) arrayList2.get(i);
                            messageObject2.replyMessageObject = messageObject;
                            messageObject2.messageOwner.reply_to_msg_id = messageObject.getId();
                            if (messageObject2.isMegagroup()) {
                                messageObject2.replyMessageObject.messageOwner.flags |= Integer.MIN_VALUE;
                            }
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (longSparseArray.size() != 0) {
                for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                    ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                    for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                        ((MessageObject) arrayList3.get(i3)).messageOwner.reply_to_random_id = 0;
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable(j) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$92$MediaDataController(this.f$1);
                }
            });
            if (runnable != null) {
                runnable.run();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$92$MediaDataController(long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$96$MediaDataController(StringBuilder sb, long j, ArrayList arrayList, SparseArray sparseArray, int i, boolean z, Runnable runnable) {
        ArrayList arrayList2 = arrayList;
        int i2 = i;
        try {
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{sb.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j;
                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                    arrayList3.add(TLdeserialize);
                    arrayList2.remove(Integer.valueOf(TLdeserialize.id));
                } else {
                    long j2 = j;
                }
            }
            long j3 = j;
            queryFinalized.dispose();
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList6), arrayList4);
            }
            if (!arrayList7.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList7), arrayList5);
            }
            broadcastReplyMessages(arrayList3, sparseArray, arrayList4, arrayList5, j, true);
            if (!arrayList.isEmpty()) {
                if (i2 != 0) {
                    TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                    tL_channels_getMessages.channel = getMessagesController().getInputChannel(i2);
                    tL_channels_getMessages.id = arrayList2;
                    getConnectionsManager().sendRequest(tL_channels_getMessages, new RequestDelegate(sparseArray, j, z, runnable) {
                        private final /* synthetic */ SparseArray f$1;
                        private final /* synthetic */ long f$2;
                        private final /* synthetic */ boolean f$3;
                        private final /* synthetic */ Runnable f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r5;
                            this.f$4 = r6;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MediaDataController.this.lambda$null$94$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                tL_messages_getMessages.id = arrayList2;
                getConnectionsManager().sendRequest(tL_messages_getMessages, new RequestDelegate(sparseArray, j, z, runnable) {
                    private final /* synthetic */ SparseArray f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ Runnable f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$95$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
                    }
                });
            } else if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$94$MediaDataController(SparseArray sparseArray, long j, boolean z, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$95$MediaDataController(SparseArray sparseArray, long j, boolean z, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    private void saveReplyMessages(SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<TLRPC.Message> arrayList, boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(z, arrayList, sparseArray) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ SparseArray f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaDataController.this.lambda$saveReplyMessages$97$MediaDataController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$saveReplyMessages$97$MediaDataController(boolean z, ArrayList arrayList, SparseArray sparseArray) {
        SQLitePreparedStatement sQLitePreparedStatement;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            if (z) {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages SET replydata = ? WHERE mid = ?");
            } else {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            }
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(i);
                ArrayList arrayList2 = (ArrayList) sparseArray.get(message.id);
                if (arrayList2 != null) {
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                        MessageObject messageObject = (MessageObject) arrayList2.get(i2);
                        sQLitePreparedStatement.requery();
                        long id = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                        sQLitePreparedStatement.bindLong(2, id);
                        sQLitePreparedStatement.step();
                    }
                    nativeByteBuffer.reuse();
                }
            }
            sQLitePreparedStatement.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void broadcastReplyMessages(ArrayList<TLRPC.Message> arrayList, SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<TLRPC.User> arrayList2, ArrayList<TLRPC.Chat> arrayList3, long j, boolean z) {
        SparseArray sparseArray2 = new SparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            ArrayList<TLRPC.User> arrayList4 = arrayList2;
            TLRPC.User user = arrayList2.get(i);
            sparseArray2.put(user.id, user);
        }
        ArrayList<TLRPC.User> arrayList5 = arrayList2;
        SparseArray sparseArray3 = new SparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            TLRPC.Chat chat = arrayList3.get(i2);
            sparseArray3.put(chat.id, chat);
        }
        AndroidUtilities.runOnUIThread(new Runnable(arrayList2, z, arrayList3, arrayList, sparseArray, sparseArray2, sparseArray3, j) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ SparseArray f$5;
            private final /* synthetic */ SparseArray f$6;
            private final /* synthetic */ SparseArray f$7;
            private final /* synthetic */ long f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                MediaDataController.this.lambda$broadcastReplyMessages$98$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$broadcastReplyMessages$98$MediaDataController(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3, long j) {
        boolean z2 = z;
        ArrayList arrayList4 = arrayList;
        getMessagesController().putUsers(arrayList, z2);
        getMessagesController().putChats(arrayList2, z2);
        boolean z3 = false;
        for (int i = 0; i < arrayList3.size(); i++) {
            TLRPC.Message message = (TLRPC.Message) arrayList3.get(i);
            ArrayList arrayList5 = (ArrayList) sparseArray.get(message.id);
            if (arrayList5 != null) {
                MessageObject messageObject = new MessageObject(this.currentAccount, message, (SparseArray<TLRPC.User>) sparseArray2, (SparseArray<TLRPC.Chat>) sparseArray3, false);
                for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                    MessageObject messageObject2 = (MessageObject) arrayList5.get(i2);
                    messageObject2.replyMessageObject = messageObject;
                    TLRPC.MessageAction messageAction = messageObject2.messageOwner.action;
                    if (messageAction instanceof TLRPC.TL_messageActionPinMessage) {
                        messageObject2.generatePinMessageText((TLRPC.User) null, (TLRPC.Chat) null);
                    } else if (messageAction instanceof TLRPC.TL_messageActionGameScore) {
                        messageObject2.generateGameMessageText((TLRPC.User) null);
                    } else if (messageAction instanceof TLRPC.TL_messageActionPaymentSent) {
                        messageObject2.generatePaymentSentMessageText((TLRPC.User) null);
                    }
                    if (messageObject2.isMegagroup()) {
                        messageObject2.replyMessageObject.messageOwner.flags |= Integer.MIN_VALUE;
                    }
                }
                z3 = true;
            }
        }
        if (z3) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
        }
    }

    public static void sortEntities(ArrayList<TLRPC.MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }

    private static boolean checkInclusion(int i, ArrayList<TLRPC.MessageEntity> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.MessageEntity messageEntity = arrayList.get(i2);
                int i3 = messageEntity.offset;
                if (i3 <= i && i3 + messageEntity.length > i) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkIntersection(int i, int i2, ArrayList<TLRPC.MessageEntity> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.MessageEntity messageEntity = arrayList.get(i3);
                int i4 = messageEntity.offset;
                if (i4 > i && i4 + messageEntity.length <= i2) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int i, int i2, ArrayList<TLRPC.MessageEntity> arrayList) {
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i3);
            int i4 = messageEntity.offset;
            if (i4 > i) {
                messageEntity.offset = i4 - i2;
            }
        }
    }

    public CharSequence substring(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof SpannableStringBuilder) {
            return charSequence.subSequence(i, i2);
        }
        if (charSequence instanceof SpannedString) {
            return charSequence.subSequence(i, i2);
        }
        return TextUtils.substring(charSequence, i, i2);
    }

    private static CharacterStyle createNewSpan(CharacterStyle characterStyle, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun textStyleRun2, boolean z) {
        TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (textStyleRun2 != null) {
            if (z) {
                textStyleRun3.merge(textStyleRun2);
            } else {
                textStyleRun3.replace(textStyleRun2);
            }
        }
        if (characterStyle instanceof TextStyleSpan) {
            return new TextStyleSpan(textStyleRun3);
        }
        if (characterStyle instanceof URLSpanReplacement) {
            return new URLSpanReplacement(((URLSpanReplacement) characterStyle).getURL(), textStyleRun3);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x008a A[Catch:{ Exception -> 0x00b2 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addStyleToText(org.telegram.ui.Components.TextStyleSpan r11, int r12, int r13, android.text.Spannable r14, boolean r15) {
        /*
            java.lang.Class<android.text.style.CharacterStyle> r0 = android.text.style.CharacterStyle.class
            java.lang.Object[] r0 = r14.getSpans(r12, r13, r0)     // Catch:{ Exception -> 0x00b2 }
            android.text.style.CharacterStyle[] r0 = (android.text.style.CharacterStyle[]) r0     // Catch:{ Exception -> 0x00b2 }
            r1 = 33
            if (r0 == 0) goto L_0x00aa
            int r2 = r0.length     // Catch:{ Exception -> 0x00b2 }
            if (r2 <= 0) goto L_0x00aa
            r2 = 0
        L_0x0010:
            int r3 = r0.length     // Catch:{ Exception -> 0x00b2 }
            if (r2 >= r3) goto L_0x00aa
            r3 = r0[r2]     // Catch:{ Exception -> 0x00b2 }
            if (r11 == 0) goto L_0x001c
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = r11.getTextStyleRun()     // Catch:{ Exception -> 0x00b2 }
            goto L_0x0021
        L_0x001c:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00b2 }
            r4.<init>()     // Catch:{ Exception -> 0x00b2 }
        L_0x0021:
            boolean r5 = r3 instanceof org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x00b2 }
            if (r5 == 0) goto L_0x002d
            r5 = r3
            org.telegram.ui.Components.TextStyleSpan r5 = (org.telegram.ui.Components.TextStyleSpan) r5     // Catch:{ Exception -> 0x00b2 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch:{ Exception -> 0x00b2 }
            goto L_0x003f
        L_0x002d:
            boolean r5 = r3 instanceof org.telegram.ui.Components.URLSpanReplacement     // Catch:{ Exception -> 0x00b2 }
            if (r5 == 0) goto L_0x00a6
            r5 = r3
            org.telegram.ui.Components.URLSpanReplacement r5 = (org.telegram.ui.Components.URLSpanReplacement) r5     // Catch:{ Exception -> 0x00b2 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch:{ Exception -> 0x00b2 }
            if (r5 != 0) goto L_0x003f
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00b2 }
            r5.<init>()     // Catch:{ Exception -> 0x00b2 }
        L_0x003f:
            if (r5 != 0) goto L_0x0043
            goto L_0x00a6
        L_0x0043:
            int r6 = r14.getSpanStart(r3)     // Catch:{ Exception -> 0x00b2 }
            int r7 = r14.getSpanEnd(r3)     // Catch:{ Exception -> 0x00b2 }
            r14.removeSpan(r3)     // Catch:{ Exception -> 0x00b2 }
            if (r6 <= r12) goto L_0x006a
            if (r13 <= r7) goto L_0x006a
            android.text.style.CharacterStyle r3 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r3, r6, r7, r1)     // Catch:{ Exception -> 0x00b2 }
            if (r11 == 0) goto L_0x0068
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x00b2 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00b2 }
            r5.<init>(r4)     // Catch:{ Exception -> 0x00b2 }
            r3.<init>(r5)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r3, r7, r13, r1)     // Catch:{ Exception -> 0x00b2 }
        L_0x0068:
            r13 = r6
            goto L_0x00a6
        L_0x006a:
            r8 = 0
            if (r6 > r12) goto L_0x0087
            if (r6 == r12) goto L_0x0076
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r8, r15)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r9, r6, r12, r1)     // Catch:{ Exception -> 0x00b2 }
        L_0x0076:
            if (r7 <= r12) goto L_0x0087
            if (r11 == 0) goto L_0x0085
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00b2 }
            int r10 = java.lang.Math.min(r7, r13)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r9, r12, r10, r1)     // Catch:{ Exception -> 0x00b2 }
        L_0x0085:
            r9 = r7
            goto L_0x0088
        L_0x0087:
            r9 = r12
        L_0x0088:
            if (r7 < r13) goto L_0x00a5
            if (r7 == r13) goto L_0x0093
            android.text.style.CharacterStyle r8 = createNewSpan(r3, r5, r8, r15)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r8, r13, r7, r1)     // Catch:{ Exception -> 0x00b2 }
        L_0x0093:
            if (r13 <= r6) goto L_0x00a5
            if (r7 > r12) goto L_0x00a5
            if (r11 == 0) goto L_0x00a4
            android.text.style.CharacterStyle r12 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00b2 }
            int r13 = java.lang.Math.min(r7, r13)     // Catch:{ Exception -> 0x00b2 }
            r14.setSpan(r12, r6, r13, r1)     // Catch:{ Exception -> 0x00b2 }
        L_0x00a4:
            r13 = r6
        L_0x00a5:
            r12 = r9
        L_0x00a6:
            int r2 = r2 + 1
            goto L_0x0010
        L_0x00aa:
            if (r11 == 0) goto L_0x00b6
            if (r12 >= r13) goto L_0x00b6
            r14.setSpan(r11, r12, r13, r1)     // Catch:{ Exception -> 0x00b2 }
            goto L_0x00b6
        L_0x00b2:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addStyleToText(org.telegram.ui.Components.TextStyleSpan, int, int, android.text.Spannable, boolean):void");
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC.MessageEntity> arrayList, CharSequence charSequence) {
        int i;
        ArrayList<TextStyleSpan.TextStyleRun> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList(arrayList);
        Collections.sort(arrayList3, $$Lambda$MediaDataController$i1rkvbOAO9BYYK9fg4hxbcAgyo.INSTANCE);
        int size = arrayList3.size();
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.MessageEntity messageEntity = (TLRPC.MessageEntity) arrayList3.get(i2);
            if (messageEntity.length > 0 && (i = messageEntity.offset) >= 0 && i < charSequence.length()) {
                if (messageEntity.offset + messageEntity.length > charSequence.length()) {
                    messageEntity.length = charSequence.length() - messageEntity.offset;
                }
                TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                textStyleRun.start = messageEntity.offset;
                textStyleRun.end = textStyleRun.start + messageEntity.length;
                if (messageEntity instanceof TLRPC.TL_messageEntityStrike) {
                    textStyleRun.flags = 8;
                } else if (messageEntity instanceof TLRPC.TL_messageEntityUnderline) {
                    textStyleRun.flags = 16;
                } else if (messageEntity instanceof TLRPC.TL_messageEntityBlockquote) {
                    textStyleRun.flags = 32;
                } else if (messageEntity instanceof TLRPC.TL_messageEntityBold) {
                    textStyleRun.flags = 1;
                } else if (messageEntity instanceof TLRPC.TL_messageEntityItalic) {
                    textStyleRun.flags = 2;
                } else if ((messageEntity instanceof TLRPC.TL_messageEntityCode) || (messageEntity instanceof TLRPC.TL_messageEntityPre)) {
                    textStyleRun.flags = 4;
                } else if (messageEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    textStyleRun.flags = 64;
                    textStyleRun.urlEntity = messageEntity;
                } else if (messageEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    textStyleRun.flags = 64;
                    textStyleRun.urlEntity = messageEntity;
                } else {
                    textStyleRun.flags = 128;
                    textStyleRun.urlEntity = messageEntity;
                }
                int size2 = arrayList2.size();
                int i3 = 0;
                while (i3 < size2) {
                    TextStyleSpan.TextStyleRun textStyleRun2 = arrayList2.get(i3);
                    int i4 = textStyleRun.start;
                    int i5 = textStyleRun2.start;
                    if (i4 > i5) {
                        int i6 = textStyleRun2.end;
                        if (i4 < i6) {
                            int i7 = textStyleRun.end;
                            if (i7 < i6) {
                                TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                textStyleRun3.merge(textStyleRun2);
                                int i8 = i3 + 1;
                                arrayList2.add(i8, textStyleRun3);
                                TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                textStyleRun4.start = textStyleRun.end;
                                i3 = i8 + 1;
                                size2 = size2 + 1 + 1;
                                arrayList2.add(i3, textStyleRun4);
                            } else if (i7 >= i6) {
                                TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                textStyleRun5.merge(textStyleRun2);
                                textStyleRun5.end = textStyleRun2.end;
                                i3++;
                                size2++;
                                arrayList2.add(i3, textStyleRun5);
                            }
                            int i9 = textStyleRun.start;
                            textStyleRun.start = textStyleRun2.end;
                            textStyleRun2.end = i9;
                        }
                    } else {
                        int i10 = textStyleRun.end;
                        if (i5 < i10) {
                            int i11 = textStyleRun2.end;
                            if (i10 == i11) {
                                textStyleRun2.merge(textStyleRun);
                            } else if (i10 < i11) {
                                TextStyleSpan.TextStyleRun textStyleRun6 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                textStyleRun6.merge(textStyleRun);
                                textStyleRun6.end = textStyleRun.end;
                                i3++;
                                size2++;
                                arrayList2.add(i3, textStyleRun6);
                                textStyleRun2.start = textStyleRun.end;
                            } else {
                                TextStyleSpan.TextStyleRun textStyleRun7 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                textStyleRun7.start = textStyleRun2.end;
                                i3++;
                                size2++;
                                arrayList2.add(i3, textStyleRun7);
                                textStyleRun2.merge(textStyleRun);
                            }
                            textStyleRun.end = i5;
                        }
                    }
                    i3++;
                }
                if (textStyleRun.start < textStyleRun.end) {
                    arrayList2.add(textStyleRun);
                }
            }
        }
        return arrayList2;
    }

    static /* synthetic */ int lambda$getTextStyleRuns$99(TLRPC.MessageEntity messageEntity, TLRPC.MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0057, code lost:
        if (r6 != null) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0059, code lost:
        r6 = new java.util.ArrayList<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005e, code lost:
        if (r1 == false) goto L_0x0062;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0060, code lost:
        r13 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0062, code lost:
        r13 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0063, code lost:
        r13 = r13 + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x006a, code lost:
        if (r13 >= r20[0].length()) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0072, code lost:
        if (r20[0].charAt(r13) != '`') goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0074, code lost:
        r4 = r4 + 1;
        r13 = r13 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0079, code lost:
        if (r1 == false) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007b, code lost:
        r12 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007d, code lost:
        r12 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x007e, code lost:
        r12 = r12 + r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x007f, code lost:
        if (r1 == false) goto L_0x0121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0081, code lost:
        if (r5 <= 0) goto L_0x008c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0083, code lost:
        r1 = r20[0].charAt(r5 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008c, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x008d, code lost:
        if (r1 == ' ') goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008f, code lost:
        if (r1 != 10) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0092, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0094, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0095, code lost:
        r13 = substring(r20[0], 0, r5 - r1);
        r14 = substring(r20[0], r5 + 3, r4);
        r15 = r4 + 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ad, code lost:
        if (r15 >= r20[0].length()) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00af, code lost:
        r3 = r20[0].charAt(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b6, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b7, code lost:
        r9 = r20[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b9, code lost:
        if (r3 == ' ') goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00bb, code lost:
        if (r3 != 10) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00be, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c0, code lost:
        r3 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c1, code lost:
        r3 = substring(r9, r15 + r3, r20[0].length());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00d2, code lost:
        if (r13.length() == 0) goto L_0x00df;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d4, code lost:
        r13 = org.telegram.messenger.AndroidUtilities.concat(r13, "\n");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00df, code lost:
        r1 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00e4, code lost:
        if (r3.length() == 0) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00e6, code lost:
        r3 = org.telegram.messenger.AndroidUtilities.concat("\n", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f4, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x015e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00f6, code lost:
        r20[0] = org.telegram.messenger.AndroidUtilities.concat(r13, r14, r3);
        r3 = new org.telegram.tgnet.TLRPC.TL_messageEntityPre();
        r3.offset = (r1 ^ 1) + r5;
        r3.length = ((r4 - r5) - 3) + (r1 ^ 1);
        r3.language = "";
        r6.add(r3);
        r12 = r12 - 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0121, code lost:
        r1 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0123, code lost:
        if (r1 == r4) goto L_0x015e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0125, code lost:
        r20[0] = org.telegram.messenger.AndroidUtilities.concat(substring(r20[0], 0, r5), substring(r20[0], r1, r4), substring(r20[0], r4 + 1, r20[0].length()));
        r1 = new org.telegram.tgnet.TLRPC.TL_messageEntityCode();
        r1.offset = r5;
        r1.length = (r4 - r5) - 1;
        r6.add(r1);
        r12 = r12 - 2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> getEntities(java.lang.CharSequence[] r20, boolean r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = 0
            if (r20 == 0) goto L_0x042e
            r2 = 0
            r3 = r20[r2]
            if (r3 != 0) goto L_0x000c
            goto L_0x042e
        L_0x000c:
            r3 = -1
            r6 = r1
            r1 = 0
            r4 = 0
        L_0x0010:
            r5 = -1
        L_0x0011:
            r7 = r20[r2]
            if (r1 != 0) goto L_0x0018
            java.lang.String r8 = "`"
            goto L_0x001a
        L_0x0018:
            java.lang.String r8 = "```"
        L_0x001a:
            int r4 = android.text.TextUtils.indexOf(r7, r8, r4)
            r7 = 10
            r8 = 32
            r10 = 2
            r11 = 1
            if (r4 == r3) goto L_0x0163
            r12 = 96
            if (r5 != r3) goto L_0x0057
            r1 = r20[r2]
            int r1 = r1.length()
            int r1 = r1 - r4
            if (r1 <= r10) goto L_0x0049
            r1 = r20[r2]
            int r5 = r4 + 1
            char r1 = r1.charAt(r5)
            if (r1 != r12) goto L_0x0049
            r1 = r20[r2]
            int r5 = r4 + 2
            char r1 = r1.charAt(r5)
            if (r1 != r12) goto L_0x0049
            r1 = 1
            goto L_0x004a
        L_0x0049:
            r1 = 0
        L_0x004a:
            if (r1 == 0) goto L_0x004e
            r9 = 3
            goto L_0x004f
        L_0x004e:
            r9 = 1
        L_0x004f:
            int r5 = r4 + r9
            r18 = r5
            r5 = r4
            r4 = r18
            goto L_0x0011
        L_0x0057:
            if (r6 != 0) goto L_0x005e
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x005e:
            if (r1 == 0) goto L_0x0062
            r13 = 3
            goto L_0x0063
        L_0x0062:
            r13 = 1
        L_0x0063:
            int r13 = r13 + r4
        L_0x0064:
            r14 = r20[r2]
            int r14 = r14.length()
            if (r13 >= r14) goto L_0x0079
            r14 = r20[r2]
            char r14 = r14.charAt(r13)
            if (r14 != r12) goto L_0x0079
            int r4 = r4 + 1
            int r13 = r13 + 1
            goto L_0x0064
        L_0x0079:
            if (r1 == 0) goto L_0x007d
            r12 = 3
            goto L_0x007e
        L_0x007d:
            r12 = 1
        L_0x007e:
            int r12 = r12 + r4
            if (r1 == 0) goto L_0x0121
            if (r5 <= 0) goto L_0x008c
            r1 = r20[r2]
            int r13 = r5 + -1
            char r1 = r1.charAt(r13)
            goto L_0x008d
        L_0x008c:
            r1 = 0
        L_0x008d:
            if (r1 == r8) goto L_0x0094
            if (r1 != r7) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r1 = 0
            goto L_0x0095
        L_0x0094:
            r1 = 1
        L_0x0095:
            r13 = r20[r2]
            int r14 = r5 - r1
            java.lang.CharSequence r13 = r0.substring(r13, r2, r14)
            r14 = r20[r2]
            int r15 = r5 + 3
            java.lang.CharSequence r14 = r0.substring(r14, r15, r4)
            int r15 = r4 + 3
            r16 = r20[r2]
            int r3 = r16.length()
            if (r15 >= r3) goto L_0x00b6
            r3 = r20[r2]
            char r3 = r3.charAt(r15)
            goto L_0x00b7
        L_0x00b6:
            r3 = 0
        L_0x00b7:
            r9 = r20[r2]
            if (r3 == r8) goto L_0x00c0
            if (r3 != r7) goto L_0x00be
            goto L_0x00c0
        L_0x00be:
            r3 = 0
            goto L_0x00c1
        L_0x00c0:
            r3 = 1
        L_0x00c1:
            int r15 = r15 + r3
            r3 = r20[r2]
            int r3 = r3.length()
            java.lang.CharSequence r3 = r0.substring(r9, r15, r3)
            int r7 = r13.length()
            java.lang.String r8 = "\n"
            if (r7 == 0) goto L_0x00df
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r10]
            r7[r2] = r13
            r7[r11] = r8
            java.lang.CharSequence r13 = org.telegram.messenger.AndroidUtilities.concat(r7)
            goto L_0x00e0
        L_0x00df:
            r1 = 1
        L_0x00e0:
            int r7 = r3.length()
            if (r7 == 0) goto L_0x00f0
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r10]
            r7[r2] = r8
            r7[r11] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r7)
        L_0x00f0:
            boolean r7 = android.text.TextUtils.isEmpty(r14)
            if (r7 != 0) goto L_0x015e
            r7 = 3
            java.lang.CharSequence[] r8 = new java.lang.CharSequence[r7]
            r8[r2] = r13
            r8[r11] = r14
            r8[r10] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r8)
            r20[r2] = r3
            org.telegram.tgnet.TLRPC$TL_messageEntityPre r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre
            r3.<init>()
            r7 = r1 ^ 1
            int r7 = r7 + r5
            r3.offset = r7
            int r4 = r4 - r5
            r5 = 3
            int r4 = r4 - r5
            r1 = r1 ^ 1
            int r4 = r4 + r1
            r3.length = r4
            java.lang.String r1 = ""
            r3.language = r1
            r6.add(r3)
            int r12 = r12 + -6
            goto L_0x015e
        L_0x0121:
            int r1 = r5 + 1
            if (r1 == r4) goto L_0x015e
            r3 = 3
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r3]
            r7 = r20[r2]
            java.lang.CharSequence r7 = r0.substring(r7, r2, r5)
            r3[r2] = r7
            r7 = r20[r2]
            java.lang.CharSequence r1 = r0.substring(r7, r1, r4)
            r3[r11] = r1
            r1 = r20[r2]
            int r7 = r4 + 1
            r8 = r20[r2]
            int r8 = r8.length()
            java.lang.CharSequence r1 = r0.substring(r1, r7, r8)
            r3[r10] = r1
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.concat(r3)
            r20[r2] = r1
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r1 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r1.<init>()
            r1.offset = r5
            int r4 = r4 - r5
            int r4 = r4 - r11
            r1.length = r4
            r6.add(r1)
            int r12 = r12 + -2
        L_0x015e:
            r4 = r12
            r1 = 0
            r3 = -1
            goto L_0x0010
        L_0x0163:
            if (r5 == r3) goto L_0x019a
            if (r1 == 0) goto L_0x019a
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r10]
            r3 = r20[r2]
            java.lang.CharSequence r3 = r0.substring(r3, r2, r5)
            r1[r2] = r3
            r3 = r20[r2]
            int r4 = r5 + 2
            r9 = r20[r2]
            int r9 = r9.length()
            java.lang.CharSequence r3 = r0.substring(r3, r4, r9)
            r1[r11] = r3
            java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.concat(r1)
            r20[r2] = r1
            if (r6 != 0) goto L_0x018e
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x018e:
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r1 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r1.<init>()
            r1.offset = r5
            r1.length = r11
            r6.add(r1)
        L_0x019a:
            r1 = r20[r2]
            boolean r1 = r1 instanceof android.text.Spanned
            if (r1 == 0) goto L_0x0315
            r1 = r20[r2]
            android.text.Spanned r1 = (android.text.Spanned) r1
            r3 = r20[r2]
            int r3 = r3.length()
            java.lang.Class<org.telegram.ui.Components.TextStyleSpan> r4 = org.telegram.ui.Components.TextStyleSpan.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            org.telegram.ui.Components.TextStyleSpan[] r3 = (org.telegram.ui.Components.TextStyleSpan[]) r3
            if (r3 == 0) goto L_0x0253
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0253
            r4 = 0
        L_0x01b8:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x0253
            r5 = r3[r4]
            int r9 = r1.getSpanStart(r5)
            int r12 = r1.getSpanEnd(r5)
            boolean r13 = checkInclusion(r9, r6)
            if (r13 != 0) goto L_0x024f
            boolean r13 = checkInclusion(r12, r6)
            if (r13 != 0) goto L_0x024f
            boolean r13 = checkIntersection(r9, r12, r6)
            if (r13 == 0) goto L_0x01d9
            goto L_0x024f
        L_0x01d9:
            if (r6 != 0) goto L_0x01e0
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x01e0:
            int r5 = r5.getStyleFlags()
            r13 = r5 & 1
            if (r13 == 0) goto L_0x01f6
            org.telegram.tgnet.TLRPC$TL_messageEntityBold r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold
            r13.<init>()
            r13.offset = r9
            int r14 = r12 - r9
            r13.length = r14
            r6.add(r13)
        L_0x01f6:
            r13 = r5 & 2
            if (r13 == 0) goto L_0x0208
            org.telegram.tgnet.TLRPC$TL_messageEntityItalic r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            r13.<init>()
            r13.offset = r9
            int r14 = r12 - r9
            r13.length = r14
            r6.add(r13)
        L_0x0208:
            r13 = r5 & 4
            if (r13 == 0) goto L_0x021a
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r13.<init>()
            r13.offset = r9
            int r14 = r12 - r9
            r13.length = r14
            r6.add(r13)
        L_0x021a:
            r13 = r5 & 8
            if (r13 == 0) goto L_0x022c
            org.telegram.tgnet.TLRPC$TL_messageEntityStrike r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            r13.<init>()
            r13.offset = r9
            int r14 = r12 - r9
            r13.length = r14
            r6.add(r13)
        L_0x022c:
            r13 = r5 & 16
            if (r13 == 0) goto L_0x023e
            org.telegram.tgnet.TLRPC$TL_messageEntityUnderline r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            r13.<init>()
            r13.offset = r9
            int r14 = r12 - r9
            r13.length = r14
            r6.add(r13)
        L_0x023e:
            r5 = r5 & 32
            if (r5 == 0) goto L_0x024f
            org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            r5.<init>()
            r5.offset = r9
            int r12 = r12 - r9
            r5.length = r12
            r6.add(r5)
        L_0x024f:
            int r4 = r4 + 1
            goto L_0x01b8
        L_0x0253:
            r3 = r20[r2]
            int r3 = r3.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanUserMention> r4 = org.telegram.ui.Components.URLSpanUserMention.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            org.telegram.ui.Components.URLSpanUserMention[] r3 = (org.telegram.ui.Components.URLSpanUserMention[]) r3
            if (r3 == 0) goto L_0x02c6
            int r4 = r3.length
            if (r4 <= 0) goto L_0x02c6
            if (r6 != 0) goto L_0x026d
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x026d:
            r4 = 0
        L_0x026e:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x02c6
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r5 = new org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            r5.<init>()
            org.telegram.messenger.MessagesController r9 = r19.getMessagesController()
            r12 = r3[r4]
            java.lang.String r12 = r12.getURL()
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)
            int r12 = r12.intValue()
            org.telegram.tgnet.TLRPC$InputUser r9 = r9.getInputUser((int) r12)
            r5.user_id = r9
            org.telegram.tgnet.TLRPC$InputUser r9 = r5.user_id
            if (r9 == 0) goto L_0x02c3
            r9 = r3[r4]
            int r9 = r1.getSpanStart(r9)
            r5.offset = r9
            r9 = r3[r4]
            int r9 = r1.getSpanEnd(r9)
            r12 = r20[r2]
            int r12 = r12.length()
            int r9 = java.lang.Math.min(r9, r12)
            int r12 = r5.offset
            int r9 = r9 - r12
            r5.length = r9
            r9 = r20[r2]
            int r13 = r5.length
            int r12 = r12 + r13
            int r12 = r12 - r11
            char r9 = r9.charAt(r12)
            if (r9 != r8) goto L_0x02c0
            int r9 = r5.length
            int r9 = r9 - r11
            r5.length = r9
        L_0x02c0:
            r6.add(r5)
        L_0x02c3:
            int r4 = r4 + 1
            goto L_0x026e
        L_0x02c6:
            r3 = r20[r2]
            int r3 = r3.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanReplacement> r4 = org.telegram.ui.Components.URLSpanReplacement.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            org.telegram.ui.Components.URLSpanReplacement[] r3 = (org.telegram.ui.Components.URLSpanReplacement[]) r3
            if (r3 == 0) goto L_0x0315
            int r4 = r3.length
            if (r4 <= 0) goto L_0x0315
            if (r6 != 0) goto L_0x02e1
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r6 = r4
        L_0x02e1:
            r4 = 0
        L_0x02e2:
            int r5 = r3.length
            if (r4 >= r5) goto L_0x0315
            org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            r5.<init>()
            r9 = r3[r4]
            int r9 = r1.getSpanStart(r9)
            r5.offset = r9
            r9 = r3[r4]
            int r9 = r1.getSpanEnd(r9)
            r12 = r20[r2]
            int r12 = r12.length()
            int r9 = java.lang.Math.min(r9, r12)
            int r12 = r5.offset
            int r9 = r9 - r12
            r5.length = r9
            r9 = r3[r4]
            java.lang.String r9 = r9.getURL()
            r5.url = r9
            r6.add(r5)
            int r4 = r4 + 1
            goto L_0x02e2
        L_0x0315:
            if (r21 == 0) goto L_0x0319
            r1 = 3
            goto L_0x031a
        L_0x0319:
            r1 = 2
        L_0x031a:
            r3 = 0
        L_0x031b:
            if (r3 >= r1) goto L_0x042d
            if (r3 == 0) goto L_0x0334
            if (r3 == r11) goto L_0x032b
            r4 = 126(0x7e, float:1.77E-43)
            java.lang.String r5 = "~~"
            r4 = 0
            r9 = -1
            r12 = 126(0x7e, float:1.77E-43)
            goto L_0x033c
        L_0x032b:
            r4 = 95
            java.lang.String r5 = "__"
            r4 = 0
            r9 = -1
            r12 = 95
            goto L_0x033c
        L_0x0334:
            r4 = 42
            java.lang.String r5 = "**"
            r4 = 0
            r9 = -1
            r12 = 42
        L_0x033c:
            r13 = r20[r2]
            int r4 = android.text.TextUtils.indexOf(r13, r5, r4)
            r13 = -1
            if (r4 == r13) goto L_0x0425
            if (r9 != r13) goto L_0x0362
            if (r4 != 0) goto L_0x034c
            r14 = 32
            goto L_0x0354
        L_0x034c:
            r14 = r20[r2]
            int r15 = r4 + -1
            char r14 = r14.charAt(r15)
        L_0x0354:
            boolean r15 = checkInclusion(r4, r6)
            if (r15 != 0) goto L_0x035f
            if (r14 == r8) goto L_0x035e
            if (r14 != r7) goto L_0x035f
        L_0x035e:
            r9 = r4
        L_0x035f:
            int r4 = r4 + 2
            goto L_0x033c
        L_0x0362:
            int r14 = r4 + 2
        L_0x0364:
            r15 = r20[r2]
            int r15 = r15.length()
            if (r14 >= r15) goto L_0x0379
            r15 = r20[r2]
            char r15 = r15.charAt(r14)
            if (r15 != r12) goto L_0x0379
            int r4 = r4 + 1
            int r14 = r14 + 1
            goto L_0x0364
        L_0x0379:
            int r14 = r4 + 2
            boolean r15 = checkInclusion(r4, r6)
            if (r15 != 0) goto L_0x041d
            boolean r15 = checkIntersection(r9, r4, r6)
            if (r15 == 0) goto L_0x0389
            goto L_0x041d
        L_0x0389:
            int r15 = r9 + 2
            if (r15 == r4) goto L_0x041d
            if (r6 != 0) goto L_0x0394
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x0394:
            r7 = 3
            java.lang.CharSequence[] r8 = new java.lang.CharSequence[r7]     // Catch:{ Exception -> 0x03bc }
            r7 = r20[r2]     // Catch:{ Exception -> 0x03bc }
            java.lang.CharSequence r7 = r0.substring(r7, r2, r9)     // Catch:{ Exception -> 0x03bc }
            r8[r2] = r7     // Catch:{ Exception -> 0x03bc }
            r7 = r20[r2]     // Catch:{ Exception -> 0x03bc }
            java.lang.CharSequence r7 = r0.substring(r7, r15, r4)     // Catch:{ Exception -> 0x03bc }
            r8[r11] = r7     // Catch:{ Exception -> 0x03bc }
            r7 = r20[r2]     // Catch:{ Exception -> 0x03bc }
            r17 = r20[r2]     // Catch:{ Exception -> 0x03bc }
            int r13 = r17.length()     // Catch:{ Exception -> 0x03bc }
            java.lang.CharSequence r7 = r0.substring(r7, r14, r13)     // Catch:{ Exception -> 0x03bc }
            r8[r10] = r7     // Catch:{ Exception -> 0x03bc }
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.concat(r8)     // Catch:{ Exception -> 0x03bc }
            r20[r2] = r7     // Catch:{ Exception -> 0x03bc }
            goto L_0x03f4
        L_0x03bc:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r8 = r20[r2]
            java.lang.CharSequence r8 = r0.substring(r8, r2, r9)
            java.lang.String r8 = r8.toString()
            r7.append(r8)
            r8 = r20[r2]
            java.lang.CharSequence r8 = r0.substring(r8, r15, r4)
            java.lang.String r8 = r8.toString()
            r7.append(r8)
            r8 = r20[r2]
            r13 = r20[r2]
            int r13 = r13.length()
            java.lang.CharSequence r8 = r0.substring(r8, r14, r13)
            java.lang.String r8 = r8.toString()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r20[r2] = r7
        L_0x03f4:
            if (r3 != 0) goto L_0x03fc
            org.telegram.tgnet.TLRPC$TL_messageEntityBold r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold
            r7.<init>()
            goto L_0x0409
        L_0x03fc:
            if (r3 != r11) goto L_0x0404
            org.telegram.tgnet.TLRPC$TL_messageEntityItalic r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            r7.<init>()
            goto L_0x0409
        L_0x0404:
            org.telegram.tgnet.TLRPC$TL_messageEntityStrike r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            r7.<init>()
        L_0x0409:
            r7.offset = r9
            int r4 = r4 - r9
            int r4 = r4 - r10
            r7.length = r4
            int r4 = r7.offset
            int r8 = r7.length
            int r4 = r4 + r8
            r8 = 4
            removeOffsetAfter(r4, r8, r6)
            r6.add(r7)
            int r14 = r14 + -4
        L_0x041d:
            r4 = r14
            r7 = 10
            r8 = 32
            r9 = -1
            goto L_0x033c
        L_0x0425:
            int r3 = r3 + 1
            r7 = 10
            r8 = 32
            goto L_0x031b
        L_0x042d:
            return r6
        L_0x042e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[], boolean):java.util.ArrayList");
    }

    public void loadDraftsIfNeed() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getAllDrafts(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MediaDataController.this.lambda$loadDraftsIfNeed$102$MediaDataController(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadDraftsIfNeed$102$MediaDataController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MediaDataController.this.lambda$null$100$MediaDataController();
                }
            });
            return;
        }
        getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MediaDataController.this.lambda$null$101$MediaDataController();
            }
        });
    }

    public /* synthetic */ void lambda$null$100$MediaDataController() {
        this.loadingDrafts = false;
    }

    public /* synthetic */ void lambda$null$101$MediaDataController() {
        this.loadingDrafts = false;
        UserConfig userConfig = getUserConfig();
        userConfig.draftsLoaded = true;
        userConfig.saveConfig(false);
    }

    public int getDraftFolderId(long j) {
        return this.draftsFolderIds.get(j, 0).intValue();
    }

    public void setDraftFolderId(long j, int i) {
        this.draftsFolderIds.put(j, Integer.valueOf(i));
    }

    public void clearDraftsFolderIds() {
        this.draftsFolderIds.clear();
    }

    public LongSparseArray<TLRPC.DraftMessage> getDrafts() {
        return this.drafts;
    }

    public TLRPC.DraftMessage getDraft(long j) {
        return this.drafts.get(j);
    }

    public TLRPC.Message getDraftMessage(long j) {
        return this.draftMessages.get(j);
    }

    public void saveDraft(long j, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.Message message, boolean z) {
        saveDraft(j, charSequence, arrayList, message, z, false);
    }

    public void saveDraft(long j, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.Message message, boolean z, boolean z2) {
        TLRPC.DraftMessage draftMessage;
        String str;
        if (!TextUtils.isEmpty(charSequence) || message != null) {
            draftMessage = new TLRPC.TL_draftMessage();
        } else {
            draftMessage = new TLRPC.TL_draftMessageEmpty();
        }
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        if (charSequence == null) {
            str = "";
        } else {
            str = charSequence.toString();
        }
        draftMessage.message = str;
        draftMessage.no_webpage = z;
        if (message != null) {
            draftMessage.reply_to_msg_id = message.id;
            draftMessage.flags |= 1;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            draftMessage.entities = arrayList;
            draftMessage.flags |= 8;
        }
        TLRPC.DraftMessage draftMessage2 = this.drafts.get(j);
        if (!z2) {
            if (draftMessage2 != null && draftMessage2.message.equals(draftMessage.message) && draftMessage2.reply_to_msg_id == draftMessage.reply_to_msg_id && draftMessage2.no_webpage == draftMessage.no_webpage) {
                return;
            }
            if (draftMessage2 == null && TextUtils.isEmpty(draftMessage.message) && draftMessage.reply_to_msg_id == 0) {
                return;
            }
        }
        saveDraft(j, draftMessage, message, false);
        int i = (int) j;
        if (i != 0) {
            TLRPC.TL_messages_saveDraft tL_messages_saveDraft = new TLRPC.TL_messages_saveDraft();
            tL_messages_saveDraft.peer = getMessagesController().getInputPeer(i);
            if (tL_messages_saveDraft.peer != null) {
                tL_messages_saveDraft.message = draftMessage.message;
                tL_messages_saveDraft.no_webpage = draftMessage.no_webpage;
                tL_messages_saveDraft.reply_to_msg_id = draftMessage.reply_to_msg_id;
                tL_messages_saveDraft.entities = draftMessage.entities;
                tL_messages_saveDraft.flags = draftMessage.flags;
                getConnectionsManager().sendRequest(tL_messages_saveDraft, $$Lambda$MediaDataController$v8ioWl4vBrxhh36KEB7whpBfCs.INSTANCE);
            } else {
                return;
            }
        }
        getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void saveDraft(long j, TLRPC.DraftMessage draftMessage, TLRPC.Message message, boolean z) {
        TLRPC.User user;
        int i;
        long j2;
        SharedPreferences.Editor edit = this.preferences.edit();
        MessagesController messagesController = getMessagesController();
        if (draftMessage == null || (draftMessage instanceof TLRPC.TL_draftMessageEmpty)) {
            this.drafts.remove(j);
            this.draftMessages.remove(j);
            this.preferences.edit().remove("" + j).remove("r_" + j).commit();
            messagesController.removeDraftDialogIfNeed(j);
        } else {
            this.drafts.put(j, draftMessage);
            messagesController.putDraftDialogIfNeed(j, draftMessage);
            try {
                SerializedData serializedData = new SerializedData(draftMessage.getObjectSize());
                draftMessage.serializeToStream(serializedData);
                edit.putString("" + j, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (message == null) {
            this.draftMessages.remove(j);
            edit.remove("r_" + j);
        } else {
            this.draftMessages.put(j, message);
            SerializedData serializedData2 = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData2);
            edit.putString("r_" + j, Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        edit.commit();
        if (z) {
            if (draftMessage.reply_to_msg_id != 0 && message == null) {
                int i2 = (int) j;
                TLRPC.Chat chat = null;
                if (i2 > 0) {
                    user = getMessagesController().getUser(Integer.valueOf(i2));
                } else {
                    chat = getMessagesController().getChat(Integer.valueOf(-i2));
                    user = null;
                }
                if (!(user == null && chat == null)) {
                    long j3 = (long) draftMessage.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        int i3 = chat.id;
                        j2 = j3 | (((long) i3) << 32);
                        i = i3;
                    } else {
                        j2 = j3;
                        i = 0;
                    }
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j2, i, j) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ long f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                            this.f$3 = r5;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$saveDraft$106$MediaDataController(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    public /* synthetic */ void lambda$saveDraft$106$MediaDataController(long j, int i, long j2) {
        NativeByteBuffer byteBufferValue;
        TLRPC.Message message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                message.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (message != null) {
                saveDraftReplyMessage(j2, message);
            } else if (i != 0) {
                TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                tL_channels_getMessages.channel = getMessagesController().getInputChannel(i);
                tL_channels_getMessages.id.add(Integer.valueOf((int) j));
                getConnectionsManager().sendRequest(tL_channels_getMessages, new RequestDelegate(j2) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$104$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
            } else {
                TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf((int) j));
                getConnectionsManager().sendRequest(tL_messages_getMessages, new RequestDelegate(j2) {
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MediaDataController.this.lambda$null$105$MediaDataController(this.f$1, tLObject, tL_error);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$104$MediaDataController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, messages_messages.messages.get(0));
            }
        }
    }

    public /* synthetic */ void lambda$null$105$MediaDataController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, messages_messages.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long j, TLRPC.Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable(j, message) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ TLRPC.Message f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    MediaDataController.this.lambda$saveDraftReplyMessage$107$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$saveDraftReplyMessage$107$MediaDataController(long j, TLRPC.Message message) {
        TLRPC.DraftMessage draftMessage = this.drafts.get(j);
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
            this.draftMessages.put(j, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            SharedPreferences.Editor edit = this.preferences.edit();
            edit.putString("r_" + j, Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts(boolean z) {
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftsFolderIds.clear();
        this.preferences.edit().clear().commit();
        if (z) {
            getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void cleanDraft(long j, boolean z) {
        TLRPC.DraftMessage draftMessage = this.drafts.get(j);
        if (draftMessage != null) {
            if (!z) {
                this.drafts.remove(j);
                this.draftMessages.remove(j);
                this.preferences.edit().remove("" + j).remove("r_" + j).commit();
                getMessagesController().sortDialogs((SparseArray<TLRPC.Chat>) null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(j, draftMessage.message, draftMessage.entities, (TLRPC.Message) null, draftMessage.no_webpage, true);
            }
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(long j, ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList, j) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MediaDataController.this.lambda$clearBotKeyboard$108$MediaDataController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$clearBotKeyboard$108$MediaDataController(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long j2 = this.botKeyboardsByMids.get(((Integer) arrayList.get(i)).intValue());
                if (j2 != 0) {
                    this.botKeyboards.remove(j2);
                    this.botKeyboardsByMids.delete(((Integer) arrayList.get(i)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j2));
                }
            }
            return;
        }
        this.botKeyboards.remove(j);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j));
    }

    public void loadBotKeyboard(long j) {
        TLRPC.Message message = this.botKeyboards.get(j);
        if (message != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MediaDataController.this.lambda$loadBotKeyboard$110$MediaDataController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$loadBotKeyboard$110$MediaDataController(long j) {
        NativeByteBuffer byteBufferValue;
        TLRPC.Message message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                message = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (message != null) {
                AndroidUtilities.runOnUIThread(new Runnable(message, j) {
                    private final /* synthetic */ TLRPC.Message f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$109$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$109$MediaDataController(TLRPC.Message message, long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void loadBotInfo(int i, boolean z, int i2) {
        TLRPC.BotInfo botInfo;
        if (!z || (botInfo = this.botInfos.get(i)) == null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, i2) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$loadBotInfo$112$MediaDataController(this.f$1, this.f$2);
                }
            });
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$loadBotInfo$112$MediaDataController(int i, int i2) {
        NativeByteBuffer byteBufferValue;
        TLRPC.BotInfo botInfo = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                botInfo = TLRPC.BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new Runnable(botInfo, i2) {
                    private final /* synthetic */ TLRPC.BotInfo f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$111$MediaDataController(this.f$1, this.f$2);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$111$MediaDataController(TLRPC.BotInfo botInfo, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(i));
    }

    public void putBotKeyboard(long j, TLRPC.Message message) {
        if (message != null) {
            try {
                int i = 0;
                SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (i < message.id) {
                    SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, message.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable(j, message) {
                        private final /* synthetic */ long f$1;
                        private final /* synthetic */ TLRPC.Message f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r4;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$putBotKeyboard$113$MediaDataController(this.f$1, this.f$2);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$putBotKeyboard$113$MediaDataController(long j, TLRPC.Message message) {
        TLRPC.Message message2 = this.botKeyboards.get(j);
        this.botKeyboards.put(j, message);
        if (message2 != null) {
            this.botKeyboardsByMids.delete(message2.id);
        }
        this.botKeyboardsByMids.put(message.id, j);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void putBotInfo(TLRPC.BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(botInfo) {
                private final /* synthetic */ TLRPC.BotInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$putBotInfo$114$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putBotInfo$114$MediaDataController(TLRPC.BotInfo botInfo) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(botInfo.getObjectSize());
            botInfo.serializeToStream(nativeByteBuffer);
            executeFast.bindInteger(1, botInfo.user_id);
            executeFast.bindByteBuffer(2, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void fetchNewEmojiKeywords(String[] strArr) {
        if (strArr != null) {
            int i = 0;
            while (i < strArr.length) {
                String str = strArr[i];
                if (!TextUtils.isEmpty(str) && this.currentFetchingEmoji.get(str) == null) {
                    this.currentFetchingEmoji.put(str, true);
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(str) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MediaDataController.this.lambda$fetchNewEmojiKeywords$120$MediaDataController(this.f$1);
                        }
                    });
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0055  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$120$MediaDataController(java.lang.String r10) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r9.getMessagesStorage()     // Catch:{ Exception -> 0x0033 }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x0033 }
            java.lang.String r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0033 }
            r8 = 0
            r7[r8] = r10     // Catch:{ Exception -> 0x0033 }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x0033 }
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x0033 }
            if (r5 == 0) goto L_0x002c
            java.lang.String r1 = r4.stringValue(r8)     // Catch:{ Exception -> 0x0033 }
            int r5 = r4.intValue(r6)     // Catch:{ Exception -> 0x0033 }
            r6 = 2
            long r2 = r4.longValue(r6)     // Catch:{ Exception -> 0x0031 }
            goto L_0x002d
        L_0x002c:
            r5 = -1
        L_0x002d:
            r4.dispose()     // Catch:{ Exception -> 0x0031 }
            goto L_0x0038
        L_0x0031:
            r4 = move-exception
            goto L_0x0035
        L_0x0033:
            r4 = move-exception
            r5 = -1
        L_0x0035:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0038:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0055
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r6 - r2
            long r2 = java.lang.Math.abs(r6)
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x0055
            org.telegram.messenger.-$$Lambda$MediaDataController$gYoSkQToKx30ULfnqO2StylO5xI r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$gYoSkQToKx30ULfnqO2StylO5xI
            r0.<init>(r10)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x0055:
            if (r5 != r0) goto L_0x005f
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords
            r0.<init>()
            r0.lang_code = r10
            goto L_0x0068
        L_0x005f:
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference
            r0.<init>()
            r0.lang_code = r10
            r0.from_version = r5
        L_0x0068:
            org.telegram.tgnet.ConnectionsManager r2 = r9.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$MediaDataController$rovYw7NQLGi16MDOQfyutPnyvpU r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$rovYw7NQLGi16MDOQfyutPnyvpU
            r3.<init>(r5, r1, r10)
            r2.sendRequest(r0, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$120$MediaDataController(java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$115$MediaDataController(String str) {
        Boolean remove = this.currentFetchingEmoji.remove(str);
    }

    public /* synthetic */ void lambda$null$119$MediaDataController(int i, String str, String str2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_emojiKeywordsDifference tL_emojiKeywordsDifference = (TLRPC.TL_emojiKeywordsDifference) tLObject;
            if (i == -1 || tL_emojiKeywordsDifference.lang_code.equals(str)) {
                putEmojiKeywords(str2, tL_emojiKeywordsDifference);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(str2) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        MediaDataController.this.lambda$null$117$MediaDataController(this.f$1);
                    }
                });
            }
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$118$MediaDataController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$117$MediaDataController(String str) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$116$MediaDataController(this.f$1);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$116$MediaDataController(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    public /* synthetic */ void lambda$null$118$MediaDataController(String str) {
        Boolean remove = this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(String str, TLRPC.TL_emojiKeywordsDifference tL_emojiKeywordsDifference) {
        if (tL_emojiKeywordsDifference != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(tL_emojiKeywordsDifference, str) {
                private final /* synthetic */ TLRPC.TL_emojiKeywordsDifference f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaDataController.this.lambda$putEmojiKeywords$122$MediaDataController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$putEmojiKeywords$122$MediaDataController(TLRPC.TL_emojiKeywordsDifference tL_emojiKeywordsDifference, String str) {
        try {
            if (!tL_emojiKeywordsDifference.keywords.isEmpty()) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int size = tL_emojiKeywordsDifference.keywords.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.EmojiKeyword emojiKeyword = tL_emojiKeywordsDifference.keywords.get(i);
                    if (emojiKeyword instanceof TLRPC.TL_emojiKeyword) {
                        TLRPC.TL_emojiKeyword tL_emojiKeyword = (TLRPC.TL_emojiKeyword) emojiKeyword;
                        String lowerCase = tL_emojiKeyword.keyword.toLowerCase();
                        int size2 = tL_emojiKeyword.emoticons.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            executeFast.requery();
                            executeFast.bindString(1, tL_emojiKeywordsDifference.lang_code);
                            executeFast.bindString(2, lowerCase);
                            executeFast.bindString(3, tL_emojiKeyword.emoticons.get(i2));
                            executeFast.step();
                        }
                    } else if (emojiKeyword instanceof TLRPC.TL_emojiKeywordDeleted) {
                        TLRPC.TL_emojiKeywordDeleted tL_emojiKeywordDeleted = (TLRPC.TL_emojiKeywordDeleted) emojiKeyword;
                        String lowerCase2 = tL_emojiKeywordDeleted.keyword.toLowerCase();
                        int size3 = tL_emojiKeywordDeleted.emoticons.size();
                        for (int i3 = 0; i3 < size3; i3++) {
                            executeFast2.requery();
                            executeFast2.bindString(1, tL_emojiKeywordsDifference.lang_code);
                            executeFast2.bindString(2, lowerCase2);
                            executeFast2.bindString(3, tL_emojiKeywordDeleted.emoticons.get(i3));
                            executeFast2.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                executeFast.dispose();
                executeFast2.dispose();
            }
            SQLitePreparedStatement executeFast3 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            executeFast3.bindString(1, str);
            executeFast3.bindString(2, tL_emojiKeywordsDifference.lang_code);
            executeFast3.bindInteger(3, tL_emojiKeywordsDifference.version);
            executeFast3.bindLong(4, System.currentTimeMillis());
            executeFast3.step();
            executeFast3.dispose();
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaDataController.this.lambda$null$121$MediaDataController(this.f$1);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$null$121$MediaDataController(String str) {
        this.currentFetchingEmoji.remove(str);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback) {
        getEmojiSuggestions(strArr, str, z, keywordResultCallback, (CountDownLatch) null);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, CountDownLatch countDownLatch) {
        if (keywordResultCallback != null) {
            if (TextUtils.isEmpty(str) || strArr == null) {
                keywordResultCallback.run(new ArrayList(), (String) null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable(strArr, keywordResultCallback, str, z, new ArrayList(Emoji.recentEmoji), countDownLatch) {
                private final /* synthetic */ String[] f$1;
                private final /* synthetic */ MediaDataController.KeywordResultCallback f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ ArrayList f$5;
                private final /* synthetic */ CountDownLatch f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    MediaDataController.this.lambda$getEmojiSuggestions$126$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0127  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getEmojiSuggestions$126$MediaDataController(java.lang.String[] r15, org.telegram.messenger.MediaDataController.KeywordResultCallback r16, java.lang.String r17, boolean r18, java.util.ArrayList r19, java.util.concurrent.CountDownLatch r20) {
        /*
            r14 = this;
            r0 = r15
            r1 = r16
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            r4 = 0
            r5 = 0
            r8 = r4
            r6 = 0
            r7 = 0
        L_0x0012:
            int r9 = r0.length     // Catch:{ Exception -> 0x010f }
            r10 = 1
            if (r6 >= r9) goto L_0x003d
            org.telegram.messenger.MessagesStorage r9 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x010f }
            org.telegram.SQLite.SQLiteDatabase r9 = r9.getDatabase()     // Catch:{ Exception -> 0x010f }
            java.lang.String r11 = "SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x010f }
            r13 = r0[r6]     // Catch:{ Exception -> 0x010f }
            r12[r5] = r13     // Catch:{ Exception -> 0x010f }
            org.telegram.SQLite.SQLiteCursor r9 = r9.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x010f }
            boolean r11 = r9.next()     // Catch:{ Exception -> 0x010f }
            if (r11 == 0) goto L_0x0034
            java.lang.String r8 = r9.stringValue(r5)     // Catch:{ Exception -> 0x010f }
        L_0x0034:
            r9.dispose()     // Catch:{ Exception -> 0x010f }
            if (r8 == 0) goto L_0x003a
            r7 = 1
        L_0x003a:
            int r6 = r6 + 1
            goto L_0x0012
        L_0x003d:
            if (r7 != 0) goto L_0x0049
            org.telegram.messenger.-$$Lambda$MediaDataController$pHSUznhxjsMmLgUBH-x5-EUanrs r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$pHSUznhxjsMmLgUBH-x5-EUanrs     // Catch:{ Exception -> 0x010f }
            r6 = r14
            r3.<init>(r15, r1, r2)     // Catch:{ Exception -> 0x010d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x010d }
            return
        L_0x0049:
            r6 = r14
            java.lang.String r0 = r17.toLowerCase()     // Catch:{ Exception -> 0x010d }
            r7 = r0
            r0 = 0
        L_0x0050:
            r9 = 2
            if (r0 >= r9) goto L_0x0114
            if (r0 != r10) goto L_0x0065
            org.telegram.messenger.LocaleController r11 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x010d }
            java.lang.String r11 = r11.getTranslitString(r7, r5, r5)     // Catch:{ Exception -> 0x010d }
            boolean r12 = r11.equals(r7)     // Catch:{ Exception -> 0x010d }
            if (r12 == 0) goto L_0x0066
            goto L_0x0109
        L_0x0065:
            r11 = r7
        L_0x0066:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x010d }
            r7.<init>(r11)     // Catch:{ Exception -> 0x010d }
            int r12 = r7.length()     // Catch:{ Exception -> 0x010d }
        L_0x006f:
            if (r12 <= 0) goto L_0x0083
            int r12 = r12 + -1
            char r13 = r7.charAt(r12)     // Catch:{ Exception -> 0x010d }
            int r13 = r13 + r10
            char r13 = (char) r13     // Catch:{ Exception -> 0x010d }
            r7.setCharAt(r12, r13)     // Catch:{ Exception -> 0x010d }
            if (r13 == 0) goto L_0x006f
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x010d }
            goto L_0x0084
        L_0x0083:
            r7 = r4
        L_0x0084:
            if (r18 == 0) goto L_0x0099
            org.telegram.messenger.MessagesStorage r7 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x010d }
            java.lang.String r9 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x010d }
            r12[r5] = r11     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteCursor r7 = r7.queryFinalized(r9, r12)     // Catch:{ Exception -> 0x010d }
            goto L_0x00d3
        L_0x0099:
            if (r7 == 0) goto L_0x00b0
            org.telegram.messenger.MessagesStorage r12 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteDatabase r12 = r12.getDatabase()     // Catch:{ Exception -> 0x010d }
            java.lang.String r13 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?"
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x010d }
            r9[r5] = r11     // Catch:{ Exception -> 0x010d }
            r9[r10] = r7     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteCursor r7 = r12.queryFinalized(r13, r9)     // Catch:{ Exception -> 0x010d }
            goto L_0x00d3
        L_0x00b0:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x010d }
            r7.<init>()     // Catch:{ Exception -> 0x010d }
            r7.append(r11)     // Catch:{ Exception -> 0x010d }
            java.lang.String r9 = "%"
            r7.append(r9)     // Catch:{ Exception -> 0x010d }
            java.lang.String r11 = r7.toString()     // Catch:{ Exception -> 0x010d }
            org.telegram.messenger.MessagesStorage r7 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x010d }
            java.lang.String r9 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x010d }
            r12[r5] = r11     // Catch:{ Exception -> 0x010d }
            org.telegram.SQLite.SQLiteCursor r7 = r7.queryFinalized(r9, r12)     // Catch:{ Exception -> 0x010d }
        L_0x00d3:
            boolean r9 = r7.next()     // Catch:{ Exception -> 0x010d }
            if (r9 == 0) goto L_0x0105
            java.lang.String r9 = r7.stringValue(r5)     // Catch:{ Exception -> 0x010d }
            java.lang.String r12 = "️"
            java.lang.String r13 = ""
            java.lang.String r9 = r9.replace(r12, r13)     // Catch:{ Exception -> 0x010d }
            java.lang.Object r12 = r3.get(r9)     // Catch:{ Exception -> 0x010d }
            if (r12 == 0) goto L_0x00ed
            goto L_0x00d3
        L_0x00ed:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r10)     // Catch:{ Exception -> 0x010d }
            r3.put(r9, r12)     // Catch:{ Exception -> 0x010d }
            org.telegram.messenger.MediaDataController$KeywordResult r12 = new org.telegram.messenger.MediaDataController$KeywordResult     // Catch:{ Exception -> 0x010d }
            r12.<init>()     // Catch:{ Exception -> 0x010d }
            r12.emoji = r9     // Catch:{ Exception -> 0x010d }
            java.lang.String r9 = r7.stringValue(r10)     // Catch:{ Exception -> 0x010d }
            r12.keyword = r9     // Catch:{ Exception -> 0x010d }
            r2.add(r12)     // Catch:{ Exception -> 0x010d }
            goto L_0x00d3
        L_0x0105:
            r7.dispose()     // Catch:{ Exception -> 0x010d }
            r7 = r11
        L_0x0109:
            int r0 = r0 + 1
            goto L_0x0050
        L_0x010d:
            r0 = move-exception
            goto L_0x0111
        L_0x010f:
            r0 = move-exception
            r6 = r14
        L_0x0111:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0114:
            org.telegram.messenger.-$$Lambda$MediaDataController$r6g2yO2X3EtfXgS4HbjbLHFe7_w r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$r6g2yO2X3EtfXgS4HbjbLHFe7_w
            r3 = r19
            r0.<init>(r3)
            java.util.Collections.sort(r2, r0)
            if (r20 == 0) goto L_0x0127
            r1.run(r2, r8)
            r20.countDown()
            goto L_0x012f
        L_0x0127:
            org.telegram.messenger.-$$Lambda$MediaDataController$F8cPeg4s5oDcrIuKDb2pj_Q2yuU r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$F8cPeg4s5oDcrIuKDb2pj_Q2yuU
            r0.<init>(r2, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x012f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$getEmojiSuggestions$126$MediaDataController(java.lang.String[], org.telegram.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$123$MediaDataController(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        int i = 0;
        while (i < strArr.length) {
            if (this.currentFetchingEmoji.get(strArr[i]) == null) {
                i++;
            } else {
                return;
            }
        }
        keywordResultCallback.run(arrayList, (String) null);
    }

    static /* synthetic */ int lambda$null$124(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
        int indexOf = arrayList.indexOf(keywordResult.emoji);
        if (indexOf < 0) {
            indexOf = Integer.MAX_VALUE;
        }
        int indexOf2 = arrayList.indexOf(keywordResult2.emoji);
        if (indexOf2 < 0) {
            indexOf2 = Integer.MAX_VALUE;
        }
        if (indexOf < indexOf2) {
            return -1;
        }
        if (indexOf > indexOf2) {
            return 1;
        }
        int length = keywordResult.keyword.length();
        int length2 = keywordResult2.keyword.length();
        if (length < length2) {
            return -1;
        }
        if (length > length2) {
            return 1;
        }
        return 0;
    }
}
