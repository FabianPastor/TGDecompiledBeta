package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ShortcutManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build.VERSION;
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
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EmojiKeyword;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
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
import org.telegram.tgnet.TLRPC.TL_emojiKeyword;
import org.telegram.tgnet.TLRPC.TL_emojiKeywordDeleted;
import org.telegram.tgnet.TLRPC.TL_emojiKeywordsDifference;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
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
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
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
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_AllStickers;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TextStyleSpan.TextStyleRun;
import org.telegram.ui.Components.URLSpanReplacement;

public class MediaDataController extends BaseController {
    private static volatile MediaDataController[] Instance = new MediaDataController[3];
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
    private static Comparator<MessageEntity> entityComparator = -$$Lambda$MediaDataController$ylwBVrW68qjp7qTE1sAPpWyazsg.INSTANCE;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<Document>> allStickers = new HashMap();
    private HashMap<String, ArrayList<Document>> allStickersFeatured = new HashMap();
    private int[] archivedStickersCount = new int[2];
    private SparseArray<BotInfo> botInfos = new SparseArray();
    private LongSparseArray<Message> botKeyboards = new LongSparseArray();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap();
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

    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    static /* synthetic */ void lambda$markFaturedStickersAsRead$26(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$27(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$removeInline$77(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$removePeer$78(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$saveDraft$100(TLObject tLObject, TL_error tL_error) {
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
        String str = "drafts";
        if (this.currentAccount == 0) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences(str, 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.currentAccount);
            this.preferences = context.getSharedPreferences(stringBuilder.toString(), 0);
        }
        for (Entry entry : this.preferences.getAll().entrySet()) {
            try {
                String str2 = (String) entry.getKey();
                long longValue = Utilities.parseLong(str2).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) entry.getValue()));
                if (str2.startsWith("r_")) {
                    Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    TLdeserialize.readAttachPath(serializedData, getUserConfig().clientUserId);
                    if (TLdeserialize != null) {
                        this.draftMessages.put(longValue, TLdeserialize);
                    }
                } else {
                    DraftMessage TLdeserialize2 = DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
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
        int i;
        for (i = 0; i < 3; i++) {
            this.recentStickers[i].clear();
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = false;
        }
        for (i = 0; i < 4; i++) {
            this.loadHash[i] = 0;
            this.loadDate[i] = 0;
            this.stickerSets[i].clear();
            this.loadingStickers[i] = false;
            this.stickersLoaded[i] = false;
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

    public void checkStickers(int i) {
        if (!this.loadingStickers[i]) {
            if (!this.stickersLoaded[i] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[i])) >= 3600) {
                loadStickers(i, true, false);
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

    public ArrayList<Document> getRecentStickers(int i) {
        ArrayList arrayList = this.recentStickers[i];
        return new ArrayList(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<Document> getRecentStickersNoCopy(int i) {
        return this.recentStickers[i];
    }

    public boolean isStickerInFavorites(Document document) {
        for (int i = 0; i < this.recentStickers[2].size(); i++) {
            Document document2 = (Document) this.recentStickers[2].get(i);
            if (document2.id == document.id && document2.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f4  */
    public void addRecentSticker(int r14, java.lang.Object r15, org.telegram.tgnet.TLRPC.Document r16, int r17, boolean r18) {
        /*
        r13 = this;
        r6 = r13;
        r7 = r14;
        r0 = r16;
        r1 = r18;
        r8 = 0;
        r2 = 0;
    L_0x0008:
        r3 = r6.recentStickers;
        r3 = r3[r7];
        r3 = r3.size();
        r9 = 1;
        if (r2 >= r3) goto L_0x003a;
    L_0x0013:
        r3 = r6.recentStickers;
        r3 = r3[r7];
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.Document) r3;
        r4 = r3.id;
        r10 = r0.id;
        r12 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r12 != 0) goto L_0x0037;
    L_0x0025:
        r4 = r6.recentStickers;
        r4 = r4[r7];
        r4.remove(r2);
        if (r1 != 0) goto L_0x0035;
    L_0x002e:
        r2 = r6.recentStickers;
        r2 = r2[r7];
        r2.add(r8, r3);
    L_0x0035:
        r2 = 1;
        goto L_0x003b;
    L_0x0037:
        r2 = r2 + 1;
        goto L_0x0008;
    L_0x003a:
        r2 = 0;
    L_0x003b:
        if (r2 != 0) goto L_0x0046;
    L_0x003d:
        if (r1 != 0) goto L_0x0046;
    L_0x003f:
        r2 = r6.recentStickers;
        r2 = r2[r7];
        r2.add(r8, r0);
    L_0x0046:
        r10 = 2;
        if (r7 != r10) goto L_0x00a8;
    L_0x0049:
        if (r1 == 0) goto L_0x005e;
    L_0x004b:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = NUM; // 0x7f0d08b2 float:1.874663E38 double:1.0531308773E-314;
        r4 = "RemovedFromFavorites";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2 = android.widget.Toast.makeText(r2, r3, r8);
        r2.show();
        goto L_0x0070;
    L_0x005e:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = NUM; // 0x7f0d00c9 float:1.8742522E38 double:1.053129877E-314;
        r4 = "AddedToFavorites";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2 = android.widget.Toast.makeText(r2, r3, r8);
        r2.show();
    L_0x0070:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
        r2.<init>();
        r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument;
        r3.<init>();
        r2.id = r3;
        r3 = r2.id;
        r4 = r0.id;
        r3.id = r4;
        r4 = r0.access_hash;
        r3.access_hash = r4;
        r4 = r0.file_reference;
        r3.file_reference = r4;
        r4 = r3.file_reference;
        if (r4 != 0) goto L_0x0092;
    L_0x008e:
        r4 = new byte[r8];
        r3.file_reference = r4;
    L_0x0092:
        r2.unfave = r1;
        r3 = r13.getConnectionsManager();
        r4 = new org.telegram.messenger.-$$Lambda$MediaDataController$bR01DerqZY31ChiglV9cDnpCDvU;
        r5 = r15;
        r4.<init>(r13, r15, r2);
        r3.sendRequest(r2, r4);
        r2 = r13.getMessagesController();
        r2 = r2.maxFaveStickersCount;
        goto L_0x00ae;
    L_0x00a8:
        r2 = r13.getMessagesController();
        r2 = r2.maxRecentStickersCount;
    L_0x00ae:
        r3 = r6.recentStickers;
        r3 = r3[r7];
        r3 = r3.size();
        if (r3 > r2) goto L_0x00ba;
    L_0x00b8:
        if (r1 == 0) goto L_0x00df;
    L_0x00ba:
        if (r1 == 0) goto L_0x00be;
    L_0x00bc:
        r2 = r0;
        goto L_0x00cf;
    L_0x00be:
        r2 = r6.recentStickers;
        r3 = r2[r7];
        r2 = r2[r7];
        r2 = r2.size();
        r2 = r2 - r9;
        r2 = r3.remove(r2);
        r2 = (org.telegram.tgnet.TLRPC.Document) r2;
    L_0x00cf:
        r3 = r13.getMessagesStorage();
        r3 = r3.getStorageQueue();
        r4 = new org.telegram.messenger.-$$Lambda$MediaDataController$F8aiYfUT1_9zrSVmtlo1KSXQPYk;
        r4.<init>(r13, r14, r2);
        r3.postRunnable(r4);
    L_0x00df:
        if (r1 != 0) goto L_0x00f2;
    L_0x00e1:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r2.add(r0);
        r3 = 0;
        r5 = 0;
        r0 = r13;
        r1 = r14;
        r4 = r17;
        r0.processLoadedRecentDocuments(r1, r2, r3, r4, r5);
    L_0x00f2:
        if (r7 != r10) goto L_0x010b;
    L_0x00f4:
        r0 = r13.getNotificationCenter();
        r1 = org.telegram.messenger.NotificationCenter.recentDocumentsDidLoad;
        r2 = new java.lang.Object[r10];
        r3 = java.lang.Boolean.valueOf(r8);
        r2[r8] = r3;
        r3 = java.lang.Integer.valueOf(r14);
        r2[r9] = r3;
        r0.postNotificationName(r1, r2);
    L_0x010b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addRecentSticker(int, java.lang.Object, org.telegram.tgnet.TLRPC$Document, int, boolean):void");
    }

    public /* synthetic */ void lambda$addRecentSticker$0$MediaDataController(Object obj, TL_messages_faveSticker tL_messages_faveSticker, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tL_messages_faveSticker);
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$1$MediaDataController(int i, Document document) {
        i = i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
            stringBuilder.append(document.id);
            stringBuilder.append("' AND type = ");
            stringBuilder.append(i);
            database.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ArrayList<Document> getRecentGifs() {
        return new ArrayList(this.recentGifs);
    }

    public void removeRecentGif(Document document) {
        this.recentGifs.remove(document);
        TL_messages_saveGif tL_messages_saveGif = new TL_messages_saveGif();
        tL_messages_saveGif.id = new TL_inputDocument();
        InputDocument inputDocument = tL_messages_saveGif.id;
        inputDocument.id = document.id;
        inputDocument.access_hash = document.access_hash;
        inputDocument.file_reference = document.file_reference;
        if (inputDocument.file_reference == null) {
            inputDocument.file_reference = new byte[0];
        }
        tL_messages_saveGif.unsave = true;
        getConnectionsManager().sendRequest(tL_messages_saveGif, new -$$Lambda$MediaDataController$slEzQbDH_VVDpTJeXW32mMusC3c(this, tL_messages_saveGif));
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$w3yTY-QxW_L1Z8Vv3ZI2F9q0Qtc(this, document));
    }

    public /* synthetic */ void lambda$removeRecentGif$2$MediaDataController(TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text)) {
            getFileRefController().requestReference("gif", tL_messages_saveGif);
        }
    }

    public /* synthetic */ void lambda$removeRecentGif$3$MediaDataController(Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
            stringBuilder.append(document.id);
            stringBuilder.append("' AND type = 2");
            database.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean hasRecentGif(Document document) {
        for (int i = 0; i < this.recentGifs.size(); i++) {
            Document document2 = (Document) this.recentGifs.get(i);
            if (document2.id == document.id) {
                this.recentGifs.remove(i);
                this.recentGifs.add(0, document2);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(Document document, int i) {
        Object obj;
        for (int i2 = 0; i2 < this.recentGifs.size(); i2++) {
            Document document2 = (Document) this.recentGifs.get(i2);
            if (document2.id == document.id) {
                this.recentGifs.remove(i2);
                this.recentGifs.add(0, document2);
                obj = 1;
                break;
            }
        }
        obj = null;
        if (obj == null) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > getMessagesController().maxRecentGifsCount) {
            ArrayList arrayList = this.recentGifs;
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$tAERaQq-ySYnfpCuXt_3LwdZfwg(this, (Document) arrayList.remove(arrayList.size() - 1)));
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(document);
        processLoadedRecentDocuments(0, arrayList2, true, i, false);
    }

    public /* synthetic */ void lambda$addRecentGif$4$MediaDataController(Document document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
            stringBuilder.append(document.id);
            stringBuilder.append("' AND type = 2");
            database.executeFast(stringBuilder.toString()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean isLoadingStickers(int i) {
        return this.loadingStickers[i];
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0032  */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0031 A:{RETURN} */
    public void replaceStickerSet(org.telegram.tgnet.TLRPC.TL_messages_stickerSet r11) {
        /*
        r10 = this;
        r0 = r10.stickerSetsById;
        r1 = r11.set;
        r1 = r1.id;
        r0 = r0.get(r1);
        r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0;
        if (r0 != 0) goto L_0x001a;
    L_0x000e:
        r0 = r10.stickerSetsByName;
        r1 = r11.set;
        r1 = r1.short_name;
        r0 = r0.get(r1);
        r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0;
    L_0x001a:
        r1 = 1;
        r2 = 0;
        if (r0 != 0) goto L_0x002e;
    L_0x001e:
        r0 = r10.groupStickerSets;
        r3 = r11.set;
        r3 = r3.id;
        r0 = r0.get(r3);
        r0 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r0;
        if (r0 == 0) goto L_0x002e;
    L_0x002c:
        r3 = 1;
        goto L_0x002f;
    L_0x002e:
        r3 = 0;
    L_0x002f:
        if (r0 != 0) goto L_0x0032;
    L_0x0031:
        return;
    L_0x0032:
        r4 = new android.util.LongSparseArray;
        r4.<init>();
        r5 = r11.documents;
        r5 = r5.size();
        r6 = 0;
    L_0x003e:
        if (r6 >= r5) goto L_0x0050;
    L_0x0040:
        r7 = r11.documents;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.Document) r7;
        r8 = r7.id;
        r4.put(r8, r7);
        r6 = r6 + 1;
        goto L_0x003e;
    L_0x0050:
        r5 = r0.documents;
        r5 = r5.size();
        r6 = 0;
    L_0x0057:
        if (r2 >= r5) goto L_0x0074;
    L_0x0059:
        r7 = r11.documents;
        r7 = r7.get(r2);
        r7 = (org.telegram.tgnet.TLRPC.Document) r7;
        r7 = r7.id;
        r7 = r4.get(r7);
        r7 = (org.telegram.tgnet.TLRPC.Document) r7;
        if (r7 == 0) goto L_0x0071;
    L_0x006b:
        r6 = r0.documents;
        r6.set(r2, r7);
        r6 = 1;
    L_0x0071:
        r2 = r2 + 1;
        goto L_0x0057;
    L_0x0074:
        if (r6 == 0) goto L_0x008f;
    L_0x0076:
        if (r3 == 0) goto L_0x007c;
    L_0x0078:
        r10.putSetToCache(r0);
        goto L_0x008f;
    L_0x007c:
        r11 = r11.set;
        r11 = r11.masks;
        r0 = r10.stickerSets;
        r0 = r0[r11];
        r1 = r10.loadDate;
        r1 = r1[r11];
        r2 = r10.loadHash;
        r2 = r2[r11];
        r10.putStickersToCache(r11, r0, r1, r2);
    L_0x008f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet):void");
    }

    public TL_messages_stickerSet getStickerSetByName(String str) {
        return (TL_messages_stickerSet) this.stickerSetsByName.get(str);
    }

    public TL_messages_stickerSet getStickerSetById(long j) {
        return (TL_messages_stickerSet) this.stickerSetsById.get(j);
    }

    public TL_messages_stickerSet getGroupStickerSetById(StickerSet stickerSet) {
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(stickerSet.id);
        if (tL_messages_stickerSet == null) {
            tL_messages_stickerSet = (TL_messages_stickerSet) this.groupStickerSets.get(stickerSet.id);
            if (tL_messages_stickerSet != null) {
                StickerSet stickerSet2 = tL_messages_stickerSet.set;
                if (stickerSet2 != null) {
                    if (stickerSet2.hash != stickerSet.hash) {
                        loadGroupStickerSet(stickerSet, false);
                    }
                }
            }
            loadGroupStickerSet(stickerSet, true);
        }
        return tL_messages_stickerSet;
    }

    public void putGroupStickerSet(TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
    }

    private void loadGroupStickerSet(StickerSet stickerSet, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$zKb2P7Mu8tjpAr6s7jMtN1M64s4(this, stickerSet));
            return;
        }
        TL_messages_getStickerSet tL_messages_getStickerSet = new TL_messages_getStickerSet();
        tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
        InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
        inputStickerSet.id = stickerSet.id;
        inputStickerSet.access_hash = stickerSet.access_hash;
        getConnectionsManager().sendRequest(tL_messages_getStickerSet, new -$$Lambda$MediaDataController$her7M2I__DHKswS7pVBy_jBsB74(this));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$6$MediaDataController(StickerSet stickerSet) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT document FROM web_recent_v3 WHERE id = 's_");
            stringBuilder.append(stickerSet.id);
            stringBuilder.append("'");
            SQLiteCursor queryFinalized = database.queryFinalized(stringBuilder.toString(), new Object[0]);
            TL_messages_stickerSet tL_messages_stickerSet = null;
            if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    tL_messages_stickerSet = TL_messages_stickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (tL_messages_stickerSet == null || tL_messages_stickerSet.set == null || tL_messages_stickerSet.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
            if (tL_messages_stickerSet != null && tL_messages_stickerSet.set != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$4m_Tdz-jG6o4lAFVqqHk31a3xIo(this, tL_messages_stickerSet));
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$5$MediaDataController(TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$8$MediaDataController(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$J6m3oeS_sOQAsPb5kq1P5Tq1obU(this, (TL_messages_stickerSet) tLObject));
        }
    }

    public /* synthetic */ void lambda$null$7$MediaDataController(TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    private void putSetToCache(TL_messages_stickerSet tL_messages_stickerSet) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$PdTokjZmZOl4rxfgkndPuS8t8G0(this, tL_messages_stickerSet));
    }

    public /* synthetic */ void lambda$putSetToCache$9$MediaDataController(TL_messages_stickerSet tL_messages_stickerSet) {
        String str = "";
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("s_");
            stringBuilder.append(tL_messages_stickerSet.set.id);
            executeFast.bindString(1, stringBuilder.toString());
            executeFast.bindInteger(2, 6);
            executeFast.bindString(3, str);
            executeFast.bindString(4, str);
            executeFast.bindString(5, str);
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
            FileLog.e(e);
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

    public ArrayList<TL_messages_stickerSet> getStickerSets(int i) {
        if (i == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[i];
    }

    public ArrayList<StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int size = this.featuredStickerSets.size();
        for (int i = 0; i < size; i++) {
            StickerSetCovered stickerSetCovered = (StickerSetCovered) this.featuredStickerSets.get(i);
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
        String str = (String) this.stickersByEmoji.get(j);
        return str != null ? str : "";
    }

    private static int calcDocumentsHash(ArrayList<Document> arrayList) {
        int i = 0;
        if (arrayList == null) {
            return 0;
        }
        long j = 0;
        while (i < Math.min(200, arrayList.size())) {
            Document document = (Document) arrayList.get(i);
            if (document != null) {
                long j2 = document.id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
            i++;
        }
        return (int) j;
    }

    /* JADX WARNING: Missing block: B:6:0x000d, code skipped:
            if (r5.recentGifsLoaded != false) goto L_0x001f;
     */
    /* JADX WARNING: Missing block: B:11:0x001d, code skipped:
            if (r5.recentStickersLoaded[r6] != false) goto L_0x001f;
     */
    public void loadRecents(int r6, boolean r7, boolean r8, boolean r9) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 1;
        if (r7 == 0) goto L_0x0010;
    L_0x0004:
        r2 = r5.loadingRecentGifs;
        if (r2 == 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r5.loadingRecentGifs = r1;
        r2 = r5.recentGifsLoaded;
        if (r2 == 0) goto L_0x0020;
    L_0x000f:
        goto L_0x001f;
    L_0x0010:
        r2 = r5.loadingRecentStickers;
        r3 = r2[r6];
        if (r3 == 0) goto L_0x0017;
    L_0x0016:
        return;
    L_0x0017:
        r2[r6] = r1;
        r2 = r5.recentStickersLoaded;
        r2 = r2[r6];
        if (r2 == 0) goto L_0x0020;
    L_0x001f:
        r8 = 0;
    L_0x0020:
        if (r8 == 0) goto L_0x0034;
    L_0x0022:
        r8 = r5.getMessagesStorage();
        r8 = r8.getStorageQueue();
        r9 = new org.telegram.messenger.-$$Lambda$MediaDataController$QPS5duYCLASSNAMEredMPDPW2CFGVHg8w;
        r9.<init>(r5, r7, r6);
        r8.postRunnable(r9);
        goto L_0x00c8;
    L_0x0034:
        r8 = r5.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getEmojiSettings(r8);
        if (r9 != 0) goto L_0x0079;
    L_0x003c:
        r2 = 0;
        if (r7 == 0) goto L_0x0047;
    L_0x0040:
        r9 = "lastGifLoadTime";
        r8 = r8.getLong(r9, r2);
        goto L_0x005f;
    L_0x0047:
        if (r6 != 0) goto L_0x0050;
    L_0x0049:
        r9 = "lastStickersLoadTime";
        r8 = r8.getLong(r9, r2);
        goto L_0x005f;
    L_0x0050:
        if (r6 != r1) goto L_0x0059;
    L_0x0052:
        r9 = "lastStickersLoadTimeMask";
        r8 = r8.getLong(r9, r2);
        goto L_0x005f;
    L_0x0059:
        r9 = "lastStickersLoadTimeFavs";
        r8 = r8.getLong(r9, r2);
    L_0x005f:
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r8;
        r8 = java.lang.Math.abs(r2);
        r2 = 3600000; // 0x36ee80 float:5.044674E-39 double:1.7786363E-317;
        r4 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x0079;
    L_0x006f:
        if (r7 == 0) goto L_0x0074;
    L_0x0071:
        r5.loadingRecentGifs = r0;
        goto L_0x0078;
    L_0x0074:
        r7 = r5.loadingRecentStickers;
        r7[r6] = r0;
    L_0x0078:
        return;
    L_0x0079:
        if (r7 == 0) goto L_0x0095;
    L_0x007b:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs;
        r8.<init>();
        r9 = r5.recentGifs;
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        r9 = r5.getConnectionsManager();
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$Z1iUYQfO1ps0XmRdwRE5vT6syjA;
        r0.<init>(r5, r6, r7);
        r9.sendRequest(r8, r0);
        goto L_0x00c8;
    L_0x0095:
        r8 = 2;
        if (r6 != r8) goto L_0x00a8;
    L_0x0098:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers;
        r8.<init>();
        r9 = r5.recentStickers;
        r9 = r9[r6];
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        goto L_0x00bc;
    L_0x00a8:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers;
        r8.<init>();
        r9 = r5.recentStickers;
        r9 = r9[r6];
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        if (r6 != r1) goto L_0x00ba;
    L_0x00b9:
        r0 = 1;
    L_0x00ba:
        r8.attached = r0;
    L_0x00bc:
        r9 = r5.getConnectionsManager();
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$zzolW9tb9AHB6_Tn8oqQqzYp9EA;
        r0.<init>(r5, r6, r7);
        r9.sendRequest(r8, r0);
    L_0x00c8:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$loadRecents$11$MediaDataController(boolean z, int i) {
        int i2 = z ? 2 : i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT document FROM web_recent_v3 WHERE type = ");
            stringBuilder.append(i2);
            stringBuilder.append(" ORDER BY date DESC");
            SQLiteCursor queryFinalized = database.queryFinalized(stringBuilder.toString(), new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                if (!queryFinalized.isNull(0)) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        Document TLdeserialize = Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        if (TLdeserialize != null) {
                            arrayList.add(TLdeserialize);
                        }
                        byteBufferValue.reuse();
                    }
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$to-qjRbBndBI0N4cLa5DpZQ4GFY(this, z, arrayList, i));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$10$MediaDataController(boolean z, ArrayList arrayList, int i) {
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

    public /* synthetic */ void lambda$loadRecents$12$MediaDataController(int i, boolean z, TLObject tLObject, TL_error tL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TL_messages_savedGifs ? ((TL_messages_savedGifs) tLObject).gifs : null, z, 0, true);
    }

    public /* synthetic */ void lambda$loadRecents$13$MediaDataController(int i, boolean z, TLObject tLObject, TL_error tL_error) {
        ArrayList arrayList;
        if (i == 2) {
            if (tLObject instanceof TL_messages_favedStickers) {
                arrayList = ((TL_messages_favedStickers) tLObject).stickers;
                processLoadedRecentDocuments(i, arrayList, z, 0, true);
            }
        } else if (tLObject instanceof TL_messages_recentStickers) {
            arrayList = ((TL_messages_recentStickers) tLObject).stickers;
            processLoadedRecentDocuments(i, arrayList, z, 0, true);
        }
        arrayList = null;
        processLoadedRecentDocuments(i, arrayList, z, 0, true);
    }

    /* Access modifiers changed, original: protected */
    public void processLoadedRecentDocuments(int i, ArrayList<Document> arrayList, boolean z, int i2, boolean z2) {
        if (arrayList != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$eu97QS9EUBwmBA--mKfEX9BFJzY(this, z, i, arrayList, z2, i2));
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$TJxV4Az_w7ot1VAB7rJXCQS3Pv8(this, z, i, arrayList));
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$14$MediaDataController(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3 = i;
        ArrayList arrayList2 = arrayList;
        String str = "";
        try {
            int i4;
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            if (z) {
                i4 = getMessagesController().maxRecentGifsCount;
            } else if (i3 == 2) {
                i4 = getMessagesController().maxFaveStickersCount;
            } else {
                i4 = getMessagesController().maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            i3 = z ? 2 : i3 == 0 ? 3 : i3 == 1 ? 4 : 5;
            if (z2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM web_recent_v3 WHERE type = ");
                stringBuilder.append(i3);
                database.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
            int i5 = 0;
            while (i5 < size) {
                if (i5 == i4) {
                    break;
                }
                Document document = (Document) arrayList2.get(i5);
                executeFast.requery();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                i = i5;
                stringBuilder2.append(document.id);
                executeFast.bindString(1, stringBuilder2.toString());
                executeFast.bindInteger(2, i3);
                executeFast.bindString(3, str);
                executeFast.bindString(4, str);
                executeFast.bindString(5, str);
                executeFast.bindInteger(6, 0);
                executeFast.bindInteger(7, 0);
                executeFast.bindInteger(8, 0);
                executeFast.bindInteger(9, i2 != 0 ? i2 : size - i);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(10, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                i5 = i + 1;
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() >= i4) {
                database.beginTransaction();
                while (i4 < arrayList.size()) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("DELETE FROM web_recent_v3 WHERE id = '");
                    stringBuilder3.append(((Document) arrayList2.get(i4)).id);
                    stringBuilder3.append("' AND type = ");
                    stringBuilder3.append(i3);
                    database.executeFast(stringBuilder3.toString()).stepThis().dispose();
                    i4++;
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$15$MediaDataController(boolean z, int i, ArrayList arrayList) {
        Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
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
        Collections.sort(this.stickerSets[i], new -$$Lambda$MediaDataController$xwdS7YPStVh3THccJYH64BpCFqs(arrayList));
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    static /* synthetic */ int lambda$reorderStickers$16(ArrayList arrayList, TL_messages_stickerSet tL_messages_stickerSet, TL_messages_stickerSet tL_messages_stickerSet2) {
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

    public void addNewStickerSet(TL_messages_stickerSet tL_messages_stickerSet) {
        if (this.stickerSetsById.indexOfKey(tL_messages_stickerSet.set.id) < 0 && !this.stickerSetsByName.containsKey(tL_messages_stickerSet.set.short_name)) {
            int i;
            boolean z = tL_messages_stickerSet.set.masks;
            this.stickerSets[z].add(0, tL_messages_stickerSet);
            this.stickerSetsById.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
            this.installedStickerSetsById.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
            this.stickerSetsByName.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (i = 0; i < tL_messages_stickerSet.documents.size(); i++) {
                Document document = (Document) tL_messages_stickerSet.documents.get(i);
                longSparseArray.put(document.id, document);
            }
            for (i = 0; i < tL_messages_stickerSet.packs.size(); i++) {
                TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i);
                tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("️", "");
                ArrayList arrayList = (ArrayList) this.allStickers.get(tL_stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.allStickers.put(tL_stickerPack.emoticon, arrayList);
                }
                for (int i2 = 0; i2 < tL_stickerPack.documents.size(); i2++) {
                    Long l = (Long) tL_stickerPack.documents.get(i2);
                    if (this.stickersByEmoji.indexOfKey(l.longValue()) < 0) {
                        this.stickersByEmoji.put(l.longValue(), tL_stickerPack.emoticon);
                    }
                    Document document2 = (Document) longSparseArray.get(l.longValue());
                    if (document2 != null) {
                        arrayList.add(document2);
                    }
                }
            }
            this.loadHash[z] = calcStickersHash(this.stickerSets[z]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(z));
            loadStickers(z, false, true);
        }
    }

    public void loadFeaturedStickers(boolean z, boolean z2) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (z) {
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$78WJkM2XWqmHTdAOlmJVfDeAMvc(this));
            } else {
                int i;
                TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers = new TL_messages_getFeaturedStickers();
                if (z2) {
                    i = 0;
                } else {
                    i = this.loadFeaturedHash;
                }
                tL_messages_getFeaturedStickers.hash = i;
                getConnectionsManager().sendRequest(tL_messages_getFeaturedStickers, new -$$Lambda$MediaDataController$6lJTYZ3jn-LZR4bJu30Ml20Sl1g(this, tL_messages_getFeaturedStickers));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0082 A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:3:0x0017} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0082 A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:3:0x0017} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00a3  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:29:0x0071, code skipped:
            r5 = move-exception;
     */
    /* JADX WARNING: Missing block: B:30:0x0072, code skipped:
            r9 = r5;
            r5 = r0;
            r0 = r3;
            r3 = r4;
            r4 = r9;
     */
    /* JADX WARNING: Missing block: B:35:0x0082, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:36:0x0084, code skipped:
            r4 = th;
     */
    /* JADX WARNING: Missing block: B:37:0x0085, code skipped:
            r5 = r0;
     */
    /* JADX WARNING: Missing block: B:52:0x00a3, code skipped:
            r3.dispose();
     */
    public /* synthetic */ void lambda$loadFeaturedStickers$17$MediaDataController() {
        /*
        r10 = this;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = 0;
        r1 = 0;
        r3 = r10.getMessagesStorage();	 Catch:{ Throwable -> 0x008c }
        r3 = r3.getDatabase();	 Catch:{ Throwable -> 0x008c }
        r4 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1";
        r5 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x008c }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Throwable -> 0x008c }
        r4 = r3.next();	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        if (r4 == 0) goto L_0x0078;
    L_0x001d:
        r4 = r3.byteBufferValue(r1);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        if (r4 == 0) goto L_0x0045;
    L_0x0023:
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r5.<init>();	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r0 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0042, all -> 0x0082 }
        r6 = 0;
    L_0x002d:
        if (r6 >= r0) goto L_0x003d;
    L_0x002f:
        r7 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0042, all -> 0x0082 }
        r7 = org.telegram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r4, r7, r1);	 Catch:{ Throwable -> 0x0042, all -> 0x0082 }
        r5.add(r7);	 Catch:{ Throwable -> 0x0042, all -> 0x0082 }
        r6 = r6 + 1;
        goto L_0x002d;
    L_0x003d:
        r4.reuse();	 Catch:{ Throwable -> 0x0042, all -> 0x0082 }
        r0 = r5;
        goto L_0x0045;
    L_0x0042:
        r0 = move-exception;
        r4 = r0;
        goto L_0x0086;
    L_0x0045:
        r4 = 1;
        r4 = r3.byteBufferValue(r4);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        if (r4 == 0) goto L_0x0064;
    L_0x004c:
        r5 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r6 = 0;
    L_0x0051:
        if (r6 >= r5) goto L_0x0061;
    L_0x0053:
        r7 = r4.readInt64(r1);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r2.add(r7);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r6 = r6 + 1;
        goto L_0x0051;
    L_0x0061:
        r4.reuse();	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
    L_0x0064:
        r4 = 2;
        r4 = r3.intValue(r4);	 Catch:{ Throwable -> 0x0084, all -> 0x0082 }
        r1 = r10.calcFeaturedStickersHash(r0);	 Catch:{ Throwable -> 0x0071, all -> 0x0082 }
        r9 = r4;
        r4 = r1;
        r1 = r9;
        goto L_0x0079;
    L_0x0071:
        r5 = move-exception;
        r9 = r5;
        r5 = r0;
        r0 = r3;
        r3 = r4;
        r4 = r9;
        goto L_0x0090;
    L_0x0078:
        r4 = 0;
    L_0x0079:
        if (r3 == 0) goto L_0x007e;
    L_0x007b:
        r3.dispose();
    L_0x007e:
        r5 = r4;
        r4 = r1;
        r1 = r0;
        goto L_0x009b;
    L_0x0082:
        r0 = move-exception;
        goto L_0x00a1;
    L_0x0084:
        r4 = move-exception;
        r5 = r0;
    L_0x0086:
        r0 = r3;
        goto L_0x008f;
    L_0x0088:
        r1 = move-exception;
        r3 = r0;
        r0 = r1;
        goto L_0x00a1;
    L_0x008c:
        r3 = move-exception;
        r5 = r0;
        r4 = r3;
    L_0x008f:
        r3 = 0;
    L_0x0090:
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x0088 }
        if (r0 == 0) goto L_0x0098;
    L_0x0095:
        r0.dispose();
    L_0x0098:
        r4 = r3;
        r1 = r5;
        r5 = 0;
    L_0x009b:
        r3 = 1;
        r0 = r10;
        r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5);
        return;
    L_0x00a1:
        if (r3 == 0) goto L_0x00a6;
    L_0x00a3:
        r3.dispose();
    L_0x00a6:
        goto L_0x00a8;
    L_0x00a7:
        throw r0;
    L_0x00a8:
        goto L_0x00a7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadFeaturedStickers$17$MediaDataController():void");
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$19$MediaDataController(TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$tCdT3zp5oImA3HZ2MSf3kJu-rNY(this, tLObject, tL_messages_getFeaturedStickers));
    }

    public /* synthetic */ void lambda$null$18$MediaDataController(TLObject tLObject, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TL_messages_featuredStickers) {
            TL_messages_featuredStickers tL_messages_featuredStickers = (TL_messages_featuredStickers) tLObject2;
            processLoadedFeaturedStickers(tL_messages_featuredStickers.sets, tL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), tL_messages_getFeaturedStickers.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, int i2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$Lh_9zfupVSXhz_8xHMF4jyDavuo(this));
        Utilities.stageQueue.postRunnable(new -$$Lambda$MediaDataController$MrwxGQ5dcpjwT8hLZ-C0If3Ra5E(this, z, arrayList, i, i2, arrayList2));
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$20$MediaDataController() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$24$MediaDataController(boolean z, ArrayList arrayList, int i, int i2, ArrayList arrayList2) {
        long j = 1000;
        if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
            -$$Lambda$MediaDataController$VaJp1zuNPpFsl5SO6R6zxcZjAqU -__lambda_mediadatacontroller_vajp1zunppfsl5so6r6zxczjaqu = new -$$Lambda$MediaDataController$VaJp1zuNPpFsl5SO6R6zxcZjAqU(this, arrayList, i2);
            if (arrayList != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(-__lambda_mediadatacontroller_vajp1zunppfsl5so6r6zxczjaqu, j);
            if (arrayList == null) {
                return;
            }
        }
        int i3 = 0;
        if (arrayList != null) {
            try {
                ArrayList arrayList3 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                while (i3 < arrayList.size()) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) arrayList.get(i3);
                    arrayList3.add(stickerSetCovered);
                    longSparseArray.put(stickerSetCovered.set.id, stickerSetCovered);
                    i3++;
                }
                if (!z) {
                    putFeaturedStickersToCache(arrayList3, arrayList2, i, i2);
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$b7CtlOyzZpSZNeYGG9T-lbdLkFg(this, arrayList2, longSparseArray, arrayList3, i2, i));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$vISPXrMuKB5IXcEnfiF6iGjj1g8(this, i));
            putFeaturedStickersToCache(null, null, i, 0);
        }
    }

    public /* synthetic */ void lambda$null$21$MediaDataController(ArrayList arrayList, int i) {
        if (!(arrayList == null || i == 0)) {
            this.loadFeaturedHash = i;
        }
        loadFeaturedStickers(false, false);
    }

    public /* synthetic */ void lambda$null$22$MediaDataController(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, int i, int i2) {
        this.unreadStickerSets = arrayList;
        this.featuredStickerSetsById = longSparseArray;
        this.featuredStickerSets = arrayList2;
        this.loadFeaturedHash = i;
        this.loadFeaturedDate = i2;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$null$23$MediaDataController(int i) {
        this.loadFeaturedDate = i;
    }

    private void putFeaturedStickersToCache(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$LRfUPhzRo4MDxhhikU_xP9_zxaI(this, arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, i2));
    }

    public /* synthetic */ void lambda$putFeaturedStickersToCache$25$MediaDataController(ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((StickerSetCovered) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList2.size() * 8) + 4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((StickerSetCovered) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
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
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
        executeFast2.requery();
        executeFast2.bindInteger(1, i);
        executeFast2.step();
        executeFast2.dispose();
    }

    private int calcFeaturedStickersHash(ArrayList<StickerSetCovered> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            StickerSet stickerSet = ((StickerSetCovered) arrayList.get(i)).set;
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
                getConnectionsManager().sendRequest(new TL_messages_readFeaturedStickers(), -$$Lambda$MediaDataController$y1DFKDsar_wzxett1ChkJ6ppkSE.INSTANCE);
            }
        }
    }

    public int getFeaturesStickersHashWithoutUnread() {
        long j = 0;
        for (int i = 0; i < this.featuredStickerSets.size(); i++) {
            StickerSet stickerSet = ((StickerSetCovered) this.featuredStickerSets.get(i)).set;
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
            TL_messages_readFeaturedStickers tL_messages_readFeaturedStickers = new TL_messages_readFeaturedStickers();
            tL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
            getConnectionsManager().sendRequest(tL_messages_readFeaturedStickers, -$$Lambda$MediaDataController$UAvqJ58O587JwQnJkDEqwebSXv4.INSTANCE);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$51tUvNiKyBpn0ly17nZMP1lOQCc(this, j), 1000);
        }
    }

    public /* synthetic */ void lambda$markFaturedStickersByIdAsRead$28$MediaDataController(long j) {
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("archivedStickersCount");
            stringBuilder.append(i);
            int i2 = notificationsSettings.getInt(stringBuilder.toString(), -1);
            if (i2 == -1) {
                loadArchivedStickersCount(i, false);
                return;
            }
            this.archivedStickersCount[i] = i2;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
            return;
        }
        TL_messages_getArchivedStickers tL_messages_getArchivedStickers = new TL_messages_getArchivedStickers();
        tL_messages_getArchivedStickers.limit = 0;
        if (i != 1) {
            z2 = false;
        }
        tL_messages_getArchivedStickers.masks = z2;
        getConnectionsManager().sendRequest(tL_messages_getArchivedStickers, new -$$Lambda$MediaDataController$j4SxneqSvSh8Ri36MPP6vkyVOtQ(this, i));
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$30$MediaDataController(int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$QmWQvfhojhGVYrK_adnpMlcs7ZY(this, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$29$MediaDataController(TL_error tL_error, TLObject tLObject, int i) {
        if (tL_error == null) {
            TL_messages_archivedStickers tL_messages_archivedStickers = (TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tL_messages_archivedStickers.count;
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("archivedStickersCount");
            stringBuilder.append(i);
            edit.putInt(stringBuilder.toString(), tL_messages_archivedStickers.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(int i, TL_messages_allStickers tL_messages_allStickers) {
        messages_AllStickers messages_allstickers = tL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (messages_allstickers.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), messages_allstickers.hash);
        } else {
            LongSparseArray longSparseArray = new LongSparseArray();
            int i2 = 0;
            while (i2 < messages_allstickers.sets.size()) {
                StickerSet stickerSet = (StickerSet) messages_allstickers.sets.get(i2);
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(stickerSet.id);
                if (tL_messages_stickerSet != null) {
                    StickerSet stickerSet2 = tL_messages_stickerSet.set;
                    if (stickerSet2.hash == stickerSet.hash) {
                        stickerSet2.archived = stickerSet.archived;
                        stickerSet2.installed = stickerSet.installed;
                        stickerSet2.official = stickerSet.official;
                        longSparseArray.put(stickerSet2.id, tL_messages_stickerSet);
                        arrayList.add(tL_messages_stickerSet);
                        if (longSparseArray.size() == messages_allstickers.sets.size()) {
                            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / j), messages_allstickers.hash);
                        }
                        i2++;
                        j = 1000;
                    }
                }
                arrayList.add(null);
                TL_messages_getStickerSet tL_messages_getStickerSet = new TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
                InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
                inputStickerSet.id = stickerSet.id;
                inputStickerSet.access_hash = stickerSet.access_hash;
                -$$Lambda$MediaDataController$LB4w_LQoNrnnzWuFXfyhQ4rSRDc -__lambda_mediadatacontroller_lb4w_lqonrnnzwufxfyhq4rsrdc = r0;
                ConnectionsManager connectionsManager = getConnectionsManager();
                -$$Lambda$MediaDataController$LB4w_LQoNrnnzWuFXfyhQ4rSRDc -__lambda_mediadatacontroller_lb4w_lqonrnnzwufxfyhq4rsrdc2 = new -$$Lambda$MediaDataController$LB4w_LQoNrnnzWuFXfyhQ4rSRDc(this, arrayList, i2, longSparseArray, stickerSet, tL_messages_allStickers, i);
                connectionsManager.sendRequest(tL_messages_getStickerSet, -__lambda_mediadatacontroller_lb4w_lqonrnnzwufxfyhq4rsrdc);
                i2++;
                j = 1000;
            }
        }
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$32$MediaDataController(ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$6_PlsgcosU3ewOP-Dj1kNmqG1v4(this, tLObject, arrayList, i, longSparseArray, stickerSet, tL_messages_allStickers, i2));
    }

    public /* synthetic */ void lambda$null$31$MediaDataController(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) tLObject;
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

    public void loadStickers(int i, boolean z, boolean z2) {
        if (!this.loadingStickers[i]) {
            if (i != 3) {
                loadArchivedStickersCount(i, z);
            } else if (this.featuredStickerSets.isEmpty() || !getMessagesController().preloadFeaturedStickers) {
                return;
            }
            this.loadingStickers[i] = true;
            if (z) {
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$xPF_cJoaGuw25oPL6jNmRKHv__Q(this, i));
            } else {
                int i2 = 0;
                if (i == 3) {
                    TL_messages_allStickers tL_messages_allStickers = new TL_messages_allStickers();
                    tL_messages_allStickers.hash = this.loadFeaturedHash;
                    int size = this.featuredStickerSets.size();
                    while (i2 < size) {
                        tL_messages_allStickers.sets.add(((StickerSetCovered) this.featuredStickerSets.get(i2)).set);
                        i2++;
                    }
                    processLoadStickersResponse(i, tL_messages_allStickers);
                    return;
                }
                TLObject tL_messages_getAllStickers;
                if (i == 0) {
                    tL_messages_getAllStickers = new TL_messages_getAllStickers();
                    if (!z2) {
                        i2 = this.loadHash[i];
                    }
                    tL_messages_getAllStickers.hash = i2;
                } else {
                    tL_messages_getAllStickers = new TL_messages_getMaskStickers();
                    if (!z2) {
                        i2 = this.loadHash[i];
                    }
                    tL_messages_getAllStickers.hash = i2;
                }
                getConnectionsManager().sendRequest(tL_messages_getAllStickers, new -$$Lambda$MediaDataController$w7jS5yC3jKRaSQ9diflE1SGzwx4(this, i, i2));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071 A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:3:0x0023} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071 A:{ExcHandler: all (th java.lang.Throwable), Splitter:B:3:0x0023} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0083  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:23:0x0061, code skipped:
            r4 = move-exception;
     */
    /* JADX WARNING: Missing block: B:24:0x0062, code skipped:
            r11 = r4;
            r4 = r0;
            r0 = r2;
            r2 = r11;
     */
    /* JADX WARNING: Missing block: B:29:0x0071, code skipped:
            r13 = th;
     */
    /* JADX WARNING: Missing block: B:30:0x0072, code skipped:
            r0 = r2;
     */
    /* JADX WARNING: Missing block: B:31:0x0074, code skipped:
            r3 = move-exception;
     */
    /* JADX WARNING: Missing block: B:32:0x0075, code skipped:
            r4 = r0;
            r0 = r2;
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:40:0x0083, code skipped:
            r0.dispose();
     */
    /* JADX WARNING: Missing block: B:45:0x0092, code skipped:
            r0.dispose();
     */
    public /* synthetic */ void lambda$loadStickers$33$MediaDataController(int r13) {
        /*
        r12 = this;
        r0 = 0;
        r1 = 0;
        r2 = r12.getMessagesStorage();	 Catch:{ Throwable -> 0x007b }
        r2 = r2.getDatabase();	 Catch:{ Throwable -> 0x007b }
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x007b }
        r3.<init>();	 Catch:{ Throwable -> 0x007b }
        r4 = "SELECT data, date, hash FROM stickers_v2 WHERE id = ";
        r3.append(r4);	 Catch:{ Throwable -> 0x007b }
        r4 = r13 + 1;
        r3.append(r4);	 Catch:{ Throwable -> 0x007b }
        r3 = r3.toString();	 Catch:{ Throwable -> 0x007b }
        r4 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x007b }
        r2 = r2.queryFinalized(r3, r4);	 Catch:{ Throwable -> 0x007b }
        r3 = r2.next();	 Catch:{ Throwable -> 0x0074, all -> 0x0071 }
        if (r3 == 0) goto L_0x0067;
    L_0x0029:
        r3 = r2.byteBufferValue(r1);	 Catch:{ Throwable -> 0x0074, all -> 0x0071 }
        if (r3 == 0) goto L_0x0054;
    L_0x002f:
        r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0074, all -> 0x0071 }
        r4.<init>();	 Catch:{ Throwable -> 0x0074, all -> 0x0071 }
        r0 = r3.readInt32(r1);	 Catch:{ Throwable -> 0x004e, all -> 0x0071 }
        r5 = 0;
    L_0x0039:
        if (r5 >= r0) goto L_0x0049;
    L_0x003b:
        r6 = r3.readInt32(r1);	 Catch:{ Throwable -> 0x004e, all -> 0x0071 }
        r6 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r3, r6, r1);	 Catch:{ Throwable -> 0x004e, all -> 0x0071 }
        r4.add(r6);	 Catch:{ Throwable -> 0x004e, all -> 0x0071 }
        r5 = r5 + 1;
        goto L_0x0039;
    L_0x0049:
        r3.reuse();	 Catch:{ Throwable -> 0x004e, all -> 0x0071 }
        r0 = r4;
        goto L_0x0054;
    L_0x004e:
        r0 = move-exception;
        r3 = 0;
        r11 = r2;
        r2 = r0;
        r0 = r11;
        goto L_0x007e;
    L_0x0054:
        r3 = 1;
        r3 = r2.intValue(r3);	 Catch:{ Throwable -> 0x0074, all -> 0x0071 }
        r1 = calcStickersHash(r0);	 Catch:{ Throwable -> 0x0061, all -> 0x0071 }
        r11 = r3;
        r3 = r1;
        r1 = r11;
        goto L_0x0068;
    L_0x0061:
        r4 = move-exception;
        r11 = r4;
        r4 = r0;
        r0 = r2;
        r2 = r11;
        goto L_0x007e;
    L_0x0067:
        r3 = 0;
    L_0x0068:
        if (r2 == 0) goto L_0x006d;
    L_0x006a:
        r2.dispose();
    L_0x006d:
        r7 = r0;
        r9 = r1;
        r10 = r3;
        goto L_0x0089;
    L_0x0071:
        r13 = move-exception;
        r0 = r2;
        goto L_0x0090;
    L_0x0074:
        r3 = move-exception;
        r4 = r0;
        r0 = r2;
        r2 = r3;
        goto L_0x007d;
    L_0x0079:
        r13 = move-exception;
        goto L_0x0090;
    L_0x007b:
        r2 = move-exception;
        r4 = r0;
    L_0x007d:
        r3 = 0;
    L_0x007e:
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0079 }
        if (r0 == 0) goto L_0x0086;
    L_0x0083:
        r0.dispose();
    L_0x0086:
        r9 = r3;
        r7 = r4;
        r10 = 0;
    L_0x0089:
        r8 = 1;
        r5 = r12;
        r6 = r13;
        r5.processLoadedStickers(r6, r7, r8, r9, r10);
        return;
    L_0x0090:
        if (r0 == 0) goto L_0x0095;
    L_0x0092:
        r0.dispose();
    L_0x0095:
        goto L_0x0097;
    L_0x0096:
        throw r13;
    L_0x0097:
        goto L_0x0096;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickers$33$MediaDataController(int):void");
    }

    public /* synthetic */ void lambda$loadStickers$35$MediaDataController(int i, int i2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$XTwhattUif9jT9Xir4-5vzFzt-0(this, tLObject, i, i2));
    }

    public /* synthetic */ void lambda$null$34$MediaDataController(TLObject tLObject, int i, int i2) {
        if (tLObject instanceof TL_messages_allStickers) {
            processLoadStickersResponse(i, (TL_messages_allStickers) tLObject);
            return;
        }
        processLoadedStickers(i, null, false, (int) (System.currentTimeMillis() / 1000), i2);
    }

    private void putStickersToCache(int i, ArrayList<TL_messages_stickerSet> arrayList, int i2, int i3) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$ZnV_qmtLmwC1gIohVVsBEFY05mw(this, arrayList != null ? new ArrayList(arrayList) : null, i, i2, i3));
    }

    public /* synthetic */ void lambda$putStickersToCache$36$MediaDataController(ArrayList arrayList, int i, int i2, int i3) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                executeFast.requery();
                int i4 = 4;
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    i4 += ((TL_messages_stickerSet) arrayList.get(i5)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i6 = 0; i6 < arrayList.size(); i6++) {
                    ((TL_messages_stickerSet) arrayList.get(i6)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindInteger(1, i + 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i2);
                executeFast.bindInteger(4, i3);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
        executeFast2.requery();
        executeFast2.bindInteger(1, i2);
        executeFast2.step();
        executeFast2.dispose();
    }

    public String getStickerSetName(long j) {
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(j);
        if (tL_messages_stickerSet != null) {
            return tL_messages_stickerSet.set.short_name;
        }
        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.featuredStickerSetsById.get(j);
        return stickerSetCovered != null ? stickerSetCovered.set.short_name : null;
    }

    public static long getStickerSetId(Document document) {
        for (int i = 0; i < document.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TL_inputStickerSetID) {
                    return inputStickerSet.id;
                }
                return -1;
            }
        }
        return -1;
    }

    public static InputStickerSet getInputStickerSet(Document document) {
        for (int i = 0; i < document.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet;
            }
        }
        return null;
    }

    private static int calcStickersHash(ArrayList<TL_messages_stickerSet> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            StickerSet stickerSet = ((TL_messages_stickerSet) arrayList.get(i)).set;
            if (!stickerSet.archived) {
                j = (((j * 20261) + 2147483648L) + ((long) stickerSet.hash)) % 2147483648L;
            }
        }
        return (int) j;
    }

    private void processLoadedStickers(int i, ArrayList<TL_messages_stickerSet> arrayList, boolean z, int i2, int i3) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$VEWvp9l-GZuWBDjkb3vovUjfSGA(this, i));
        Utilities.stageQueue.postRunnable(new -$$Lambda$MediaDataController$U2QZv2jxK4C-aQzCLQZJxj7uca0(this, z, arrayList, i2, i3, i));
    }

    public /* synthetic */ void lambda$processLoadedStickers$37$MediaDataController(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
    }

    public /* synthetic */ void lambda$processLoadedStickers$41$MediaDataController(boolean z, ArrayList arrayList, int i, int i2, int i3) {
        ArrayList arrayList2 = arrayList;
        int i4 = i;
        int i5 = i2;
        int i6 = i3;
        long j = 1000;
        if ((z && (arrayList2 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i4)) >= 3600)) || (!z && arrayList2 == null && i5 == 0)) {
            -$$Lambda$MediaDataController$u1TkRwe9c3qci0SmAO4IwC4DOiA -__lambda_mediadatacontroller_u1tkrwe9c3qci0smao4iwc4doia = new -$$Lambda$MediaDataController$u1TkRwe9c3qci0SmAO4IwC4DOiA(this, arrayList2, i5, i6);
            if (arrayList2 != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(-__lambda_mediadatacontroller_u1tkrwe9c3qci0smao4iwc4doia, j);
            if (arrayList2 == null) {
                return;
            }
        }
        if (arrayList2 != null) {
            try {
                LongSparseArray longSparseArray;
                HashMap hashMap;
                HashMap hashMap2;
                ArrayList arrayList3 = new ArrayList();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                HashMap hashMap3 = new HashMap();
                LongSparseArray longSparseArray3 = new LongSparseArray();
                LongSparseArray longSparseArray4 = new LongSparseArray();
                HashMap hashMap4 = new HashMap();
                int i7 = 0;
                while (i7 < arrayList.size()) {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) arrayList2.get(i7);
                    if (tL_messages_stickerSet != null) {
                        arrayList3.add(tL_messages_stickerSet);
                        longSparseArray2.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
                        hashMap3.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
                        int i8 = 0;
                        while (i8 < tL_messages_stickerSet.documents.size()) {
                            Document document = (Document) tL_messages_stickerSet.documents.get(i8);
                            if (document != null) {
                                if (!(document instanceof TL_documentEmpty)) {
                                    longSparseArray4.put(document.id, document);
                                }
                            }
                            i8++;
                            arrayList2 = arrayList;
                        }
                        if (!tL_messages_stickerSet.set.archived) {
                            int i9 = 0;
                            while (i9 < tL_messages_stickerSet.packs.size()) {
                                TL_messages_stickerSet tL_messages_stickerSet2;
                                TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i9);
                                if (tL_stickerPack != null) {
                                    if (tL_stickerPack.emoticon != null) {
                                        tL_messages_stickerSet2 = tL_messages_stickerSet;
                                        tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("️", "");
                                        ArrayList arrayList4 = (ArrayList) hashMap4.get(tL_stickerPack.emoticon);
                                        if (arrayList4 == null) {
                                            arrayList4 = new ArrayList();
                                            hashMap4.put(tL_stickerPack.emoticon, arrayList4);
                                        }
                                        i8 = 0;
                                        while (i8 < tL_stickerPack.documents.size()) {
                                            Long l = (Long) tL_stickerPack.documents.get(i8);
                                            longSparseArray = longSparseArray2;
                                            hashMap = hashMap3;
                                            if (longSparseArray3.indexOfKey(l.longValue()) < 0) {
                                                hashMap2 = hashMap4;
                                                longSparseArray3.put(l.longValue(), tL_stickerPack.emoticon);
                                            } else {
                                                hashMap2 = hashMap4;
                                            }
                                            Document document2 = (Document) longSparseArray4.get(l.longValue());
                                            if (document2 != null) {
                                                arrayList4.add(document2);
                                            }
                                            i8++;
                                            longSparseArray2 = longSparseArray;
                                            hashMap3 = hashMap;
                                            hashMap4 = hashMap2;
                                        }
                                        longSparseArray = longSparseArray2;
                                        hashMap = hashMap3;
                                        hashMap2 = hashMap4;
                                        i9++;
                                        tL_messages_stickerSet = tL_messages_stickerSet2;
                                        longSparseArray2 = longSparseArray;
                                        hashMap3 = hashMap;
                                        hashMap4 = hashMap2;
                                    }
                                }
                                longSparseArray = longSparseArray2;
                                hashMap = hashMap3;
                                hashMap2 = hashMap4;
                                tL_messages_stickerSet2 = tL_messages_stickerSet;
                                i9++;
                                tL_messages_stickerSet = tL_messages_stickerSet2;
                                longSparseArray2 = longSparseArray;
                                hashMap3 = hashMap;
                                hashMap4 = hashMap2;
                            }
                        }
                    }
                    i7++;
                    arrayList2 = arrayList;
                    longSparseArray2 = longSparseArray2;
                    hashMap3 = hashMap3;
                    hashMap4 = hashMap4;
                }
                longSparseArray = longSparseArray2;
                hashMap = hashMap3;
                hashMap2 = hashMap4;
                if (!z) {
                    putStickersToCache(i6, arrayList3, i4, i5);
                }
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$IsE7MNxaqKwn15R0qVW5GXCtWUI(this, i3, longSparseArray, hashMap, arrayList3, i2, i, hashMap2, longSparseArray3));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$UlsOYUwmts7hUten3Rq5IDhsZME(this, i6, i4));
            putStickersToCache(i6, null, i4, 0);
        }
    }

    public /* synthetic */ void lambda$null$38$MediaDataController(ArrayList arrayList, int i, int i2) {
        if (!(arrayList == null || i == 0)) {
            this.loadHash[i2] = i;
        }
        loadStickers(i2, false, false);
    }

    public /* synthetic */ void lambda$null$39$MediaDataController(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, int i2, int i3, HashMap hashMap2, LongSparseArray longSparseArray2) {
        int i4;
        for (i4 = 0; i4 < this.stickerSets[i].size(); i4++) {
            StickerSet stickerSet = ((TL_messages_stickerSet) this.stickerSets[i].get(i4)).set;
            this.stickerSetsById.remove(stickerSet.id);
            this.installedStickerSetsById.remove(stickerSet.id);
            this.stickerSetsByName.remove(stickerSet.short_name);
        }
        for (i4 = 0; i4 < longSparseArray.size(); i4++) {
            this.stickerSetsById.put(longSparseArray.keyAt(i4), longSparseArray.valueAt(i4));
            if (i != 3) {
                this.installedStickerSetsById.put(longSparseArray.keyAt(i4), longSparseArray.valueAt(i4));
            }
        }
        this.stickerSetsByName.putAll(hashMap);
        this.stickerSets[i] = arrayList;
        this.loadHash[i] = i2;
        this.loadDate[i] = i3;
        if (i == 0) {
            this.allStickers = hashMap2;
            this.stickersByEmoji = longSparseArray2;
        } else if (i == 3) {
            this.allStickersFeatured = hashMap2;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$40$MediaDataController(int i, int i2) {
        this.loadDate[i] = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x009f  */
    public void removeStickersSet(android.content.Context r11, org.telegram.tgnet.TLRPC.StickerSet r12, int r13, org.telegram.ui.ActionBar.BaseFragment r14, boolean r15) {
        /*
        r10 = this;
        r2 = r12.masks;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
        r0.<init>();
        r3 = r12.access_hash;
        r0.access_hash = r3;
        r3 = r12.id;
        r0.id = r3;
        if (r13 == 0) goto L_0x00b4;
    L_0x0011:
        r11 = 0;
        r1 = 1;
        if (r13 != r1) goto L_0x0017;
    L_0x0015:
        r3 = 1;
        goto L_0x0018;
    L_0x0017:
        r3 = 0;
    L_0x0018:
        r12.archived = r3;
        r3 = 0;
    L_0x001b:
        r4 = r10.stickerSets;
        r4 = r4[r2];
        r4 = r4.size();
        if (r3 >= r4) goto L_0x006a;
    L_0x0025:
        r4 = r10.stickerSets;
        r4 = r4[r2];
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r4;
        r5 = r4.set;
        r5 = r5.id;
        r7 = r12.id;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 != 0) goto L_0x0067;
    L_0x0039:
        r12 = r10.stickerSets;
        r12 = r12[r2];
        r12.remove(r3);
        r12 = 2;
        if (r13 != r12) goto L_0x004b;
    L_0x0043:
        r12 = r10.stickerSets;
        r12 = r12[r2];
        r12.add(r11, r4);
        goto L_0x006a;
    L_0x004b:
        r12 = r10.stickerSetsById;
        r3 = r4.set;
        r5 = r3.id;
        r12.remove(r5);
        r12 = r10.installedStickerSetsById;
        r3 = r4.set;
        r5 = r3.id;
        r12.remove(r5);
        r12 = r10.stickerSetsByName;
        r3 = r4.set;
        r3 = r3.short_name;
        r12.remove(r3);
        goto L_0x006a;
    L_0x0067:
        r3 = r3 + 1;
        goto L_0x001b;
    L_0x006a:
        r12 = r10.loadHash;
        r3 = r10.stickerSets;
        r3 = r3[r2];
        r3 = calcStickersHash(r3);
        r12[r2] = r3;
        r12 = r10.stickerSets;
        r12 = r12[r2];
        r3 = r10.loadDate;
        r3 = r3[r2];
        r4 = r10.loadHash;
        r4 = r4[r2];
        r10.putStickersToCache(r2, r12, r3, r4);
        r12 = r10.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.stickersDidLoad;
        r4 = new java.lang.Object[r1];
        r5 = java.lang.Integer.valueOf(r2);
        r4[r11] = r5;
        r12.postNotificationName(r3, r4);
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
        r12.<init>();
        r12.stickerset = r0;
        if (r13 != r1) goto L_0x00a0;
    L_0x009f:
        r11 = 1;
    L_0x00a0:
        r12.archived = r11;
        r11 = r10.getConnectionsManager();
        r6 = new org.telegram.messenger.-$$Lambda$MediaDataController$eV9g99RvjceAWjduaNKfaJG6lcA;
        r0 = r6;
        r1 = r10;
        r3 = r13;
        r4 = r14;
        r5 = r15;
        r0.<init>(r1, r2, r3, r4, r5);
        r11.sendRequest(r12, r6);
        goto L_0x00c7;
    L_0x00b4:
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_uninstallStickerSet;
        r13.<init>();
        r13.stickerset = r0;
        r14 = r10.getConnectionsManager();
        r15 = new org.telegram.messenger.-$$Lambda$MediaDataController$iOG-1qJGc2XFef--4F0nPFq0WTw;
        r15.<init>(r10, r12, r11, r2);
        r14.sendRequest(r13, r15);
    L_0x00c7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.removeStickersSet(android.content.Context, org.telegram.tgnet.TLRPC$StickerSet, int, org.telegram.ui.ActionBar.BaseFragment, boolean):void");
    }

    public /* synthetic */ void lambda$removeStickersSet$44$MediaDataController(int i, int i2, BaseFragment baseFragment, boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$tNOcCvyhH5pLMnzM-2HYaacEMwo(this, tLObject, i, i2, baseFragment, z));
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$BXUHGCW-Z9nXNbo_n00UAzdDC_k(this, i), 1000);
    }

    public /* synthetic */ void lambda$null$42$MediaDataController(TLObject tLObject, int i, int i2, BaseFragment baseFragment, boolean z) {
        if (tLObject instanceof TL_messages_stickerSetInstallResultArchive) {
            getNotificationCenter().postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(i));
            if (i2 != 1 && baseFragment != null && baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), z ? baseFragment : null, ((TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
            }
        }
    }

    public /* synthetic */ void lambda$null$43$MediaDataController(int i) {
        loadStickers(i, false, false);
    }

    public /* synthetic */ void lambda$removeStickersSet$46$MediaDataController(StickerSet stickerSet, Context context, int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$ZszIZlejV9n197XcqRH5SEGmldM(this, tL_error, stickerSet, context, i));
    }

    public /* synthetic */ void lambda$null$45$MediaDataController(TL_error tL_error, StickerSet stickerSet, Context context, int i) {
        if (tL_error == null) {
            try {
                if (stickerSet.masks) {
                    Toast.makeText(context, LocaleController.getString("MasksRemoved", NUM), 0).show();
                } else {
                    Toast.makeText(context, LocaleController.getString("StickersRemoved", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
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

    public boolean isMessageFound(int i, boolean z) {
        return this.searchResultMessagesMap[z].indexOfKey(i) >= 0;
    }

    public void searchMessagesInChat(String str, long j, long j2, int i, int i2, User user) {
        searchMessagesInChat(str, j, j2, i, i2, false, user);
    }

    private void searchMessagesInChat(String str, long j, long j2, int i, int i2, boolean z, User user) {
        String str2;
        long j3;
        int i3;
        long j4 = j2;
        int i4 = i2;
        User user2 = user;
        int i5 = z ^ 1;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        int[] iArr;
        if (str != null) {
            if (i5 != 0) {
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
                boolean[] zArr = this.messagesSearchEndReached;
                zArr[1] = false;
                zArr[0] = false;
                iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
            }
            str2 = str;
            j3 = j;
            i3 = 0;
        } else if (!this.searchResultMessages.isEmpty()) {
            MessageObject messageObject;
            NotificationCenter notificationCenter;
            Object[] objArr;
            if (i4 == 1) {
                this.lastReturnedNum++;
                if (this.lastReturnedNum < this.searchResultMessages.size()) {
                    messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    notificationCenter = getNotificationCenter();
                    i4 = NotificationCenter.chatSearchResultsAvailable;
                    objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i);
                    objArr[1] = Integer.valueOf(messageObject.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    iArr = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr[0] + iArr[1]);
                    notificationCenter.postNotificationName(i4, objArr);
                    return;
                }
                boolean[] zArr2 = this.messagesSearchEndReached;
                if (zArr2[0] && j4 == 0 && zArr2[1]) {
                    this.lastReturnedNum--;
                    return;
                }
                int id;
                String str3 = this.lastSearchQuery;
                ArrayList arrayList = this.searchResultMessages;
                MessageObject messageObject2 = (MessageObject) arrayList.get(arrayList.size() - 1);
                if (messageObject2.getDialogId() != j || this.messagesSearchEndReached[0]) {
                    id = messageObject2.getDialogId() == j4 ? messageObject2.getId() : 0;
                    this.messagesSearchEndReached[1] = false;
                    j3 = j4;
                } else {
                    id = messageObject2.getId();
                    j3 = j;
                }
                i3 = id;
                str2 = str3;
                i5 = 0;
            } else {
                if (i4 == 2) {
                    this.lastReturnedNum--;
                    int i6 = this.lastReturnedNum;
                    if (i6 < 0) {
                        this.lastReturnedNum = 0;
                        return;
                    }
                    if (i6 >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    notificationCenter = getNotificationCenter();
                    i4 = NotificationCenter.chatSearchResultsAvailable;
                    objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i);
                    objArr[1] = Integer.valueOf(messageObject.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    iArr = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr[0] + iArr[1]);
                    notificationCenter.postNotificationName(i4, objArr);
                }
                return;
            }
        } else {
            return;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!(!zArr3[0] || zArr3[1] || j4 == 0)) {
            j3 = j4;
        }
        String str4 = "";
        if (j3 == j && i5 != 0) {
            if (j4 != 0) {
                InputPeer inputPeer = getMessagesController().getInputPeer((int) j4);
                if (inputPeer != null) {
                    TL_messages_search tL_messages_search = new TL_messages_search();
                    tL_messages_search.peer = inputPeer;
                    this.lastMergeDialogId = j4;
                    tL_messages_search.limit = 1;
                    if (str2 != null) {
                        str4 = str2;
                    }
                    tL_messages_search.q = str4;
                    if (user2 != null) {
                        tL_messages_search.from_id = getMessagesController().getInputUser(user2);
                        tL_messages_search.flags = 1 | tL_messages_search.flags;
                    }
                    tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
                    ConnectionsManager connectionsManager = getConnectionsManager();
                    -$$Lambda$MediaDataController$50YvvjfmB2aKVLBuihySKhFqfY0 -__lambda_mediadatacontroller_50yvvjfmb2akvlbuihyskhfqfy0 = r0;
                    -$$Lambda$MediaDataController$50YvvjfmB2aKVLBuihySKhFqfY0 -__lambda_mediadatacontroller_50yvvjfmb2akvlbuihyskhfqfy02 = new -$$Lambda$MediaDataController$50YvvjfmB2aKVLBuihySKhFqfY0(this, j2, tL_messages_search, j, i, i2, user);
                    this.mergeReqId = connectionsManager.sendRequest(tL_messages_search, -__lambda_mediadatacontroller_50yvvjfmb2akvlbuihyskhfqfy0, 2);
                    return;
                }
                return;
            }
            this.lastMergeDialogId = 0;
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLObject tL_messages_search2 = new TL_messages_search();
        tL_messages_search2.peer = getMessagesController().getInputPeer((int) j3);
        if (tL_messages_search2.peer != null) {
            tL_messages_search2.limit = 21;
            if (str2 != null) {
                str4 = str2;
            }
            tL_messages_search2.q = str4;
            tL_messages_search2.offset_id = i3;
            if (user2 != null) {
                tL_messages_search2.from_id = getMessagesController().getInputUser(user2);
                tL_messages_search2.flags |= 1;
            }
            tL_messages_search2.filter = new TL_inputMessagesFilterEmpty();
            int i7 = this.lastReqId + 1;
            this.lastReqId = i7;
            this.lastSearchQuery = str2;
            this.reqId = getConnectionsManager().sendRequest(tL_messages_search2, new -$$Lambda$MediaDataController$PjXbJNDiHGZM6cUBJrF7A9Vl_0E(this, i7, tL_messages_search2, j3, j, i, j2, user), 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$48$MediaDataController(long j, TL_messages_search tL_messages_search, long j2, int i, int i2, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$WljUbJDNh-HxA46uE2wuYU-EM_E(this, j, tLObject, tL_messages_search, j2, i, i2, user));
    }

    public /* synthetic */ void lambda$null$47$MediaDataController(long j, TLObject tLObject, TL_messages_search tL_messages_search, long j2, int i, int i2, User user) {
        if (this.lastMergeDialogId == j) {
            this.mergeReqId = 0;
            if (tLObject != null) {
                messages_Messages messages_messages = (messages_Messages) tLObject;
                this.messagesSearchEndReached[1] = messages_messages.messages.isEmpty();
                this.messagesSearchCount[1] = messages_messages instanceof TL_messages_messagesSlice ? messages_messages.count : messages_messages.messages.size();
                searchMessagesInChat(tL_messages_search.q, j2, j, i, i2, true, user);
            }
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$50$MediaDataController(int i, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$ysFuAHimxXbVIypfzslaoKloPf4(this, i, tLObject, tL_messages_search, j, j2, i2, j3, user));
    }

    public /* synthetic */ void lambda$null$49$MediaDataController(int i, TLObject tLObject, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user) {
        if (i == this.lastReqId) {
            this.reqId = 0;
            if (tLObject != null) {
                messages_Messages messages_messages = (messages_Messages) tLObject;
                int i3 = 0;
                while (i3 < messages_messages.messages.size()) {
                    Message message = (Message) messages_messages.messages.get(i3);
                    if ((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
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
                }
                i3 = 0;
                Object obj = null;
                while (i3 < Math.min(messages_messages.messages.size(), 20)) {
                    MessageObject messageObject = new MessageObject(this.currentAccount, (Message) messages_messages.messages.get(i3), false);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[j == j2 ? 0 : 1].put(messageObject.getId(), messageObject);
                    i3++;
                    obj = 1;
                }
                this.messagesSearchEndReached[j == j2 ? 0 : 1] = messages_messages.messages.size() != 21;
                int[] iArr = this.messagesSearchCount;
                int i4 = j == j2 ? 0 : 1;
                int size = ((messages_messages instanceof TL_messages_messagesSlice) || (messages_messages instanceof TL_messages_channelMessages)) ? messages_messages.count : messages_messages.messages.size();
                iArr[i4] = size;
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i2), Integer.valueOf(0), Integer.valueOf(getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                } else if (obj != null) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i5 = NotificationCenter.chatSearchResultsAvailable;
                    Object[] objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i2);
                    objArr[1] = Integer.valueOf(messageObject2.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject2.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    int[] iArr2 = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr2[0] + iArr2[1]);
                    notificationCenter.postNotificationName(i5, objArr);
                }
                if (j == j2) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && j3 != 0 && !zArr[1]) {
                        searchMessagesInChat(this.lastSearchQuery, j2, j3, i2, 0, true, user);
                    }
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    public void loadMedia(long j, int i, int i2, int i3, int i4, int i5) {
        boolean z;
        int i6;
        int i7;
        int i8;
        int i9 = i3;
        int i10 = (int) j;
        if (i10 >= 0) {
        } else if (ChatObject.isChannel(-i10, this.currentAccount)) {
            z = true;
            if (i4 == 0 || i10 == 0) {
                i6 = i;
                i7 = i2;
                i8 = i5;
                loadMediaDatabase(j, i, i2, i3, i5, z, i4);
            } else {
                TL_messages_search tL_messages_search = new TL_messages_search();
                tL_messages_search.limit = i;
                tL_messages_search.offset_id = i2;
                if (i9 == 0) {
                    tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
                } else if (i9 == 1) {
                    tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                } else if (i9 == 2) {
                    tL_messages_search.filter = new TL_inputMessagesFilterRoundVoice();
                } else if (i9 == 3) {
                    tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                } else if (i9 == 4) {
                    tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                }
                tL_messages_search.q = "";
                tL_messages_search.peer = getMessagesController().getInputPeer(i10);
                if (tL_messages_search.peer != null) {
                    i8 = i5;
                    getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new -$$Lambda$MediaDataController$EMCzILi4uRDLHSjzdsZxhKuxJ2M(this, j, i, i2, i3, i8, z)), i8);
                } else {
                    return;
                }
            }
        }
        z = false;
        if (i4 == 0) {
        }
        i6 = i;
        i7 = i2;
        i8 = i5;
        loadMediaDatabase(j, i, i2, i3, i5, z, i4);
    }

    public /* synthetic */ void lambda$loadMedia$51$MediaDataController(long j, int i, int i2, int i3, int i4, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            long j2 = j;
            getMessagesController().removeDeletedMessagesFromArray(j, messages_messages.messages);
            processLoadedMedia(messages_messages, j, i, i2, i3, 0, i4, z, messages_messages.messages.size() == 0);
        }
    }

    public void getMediaCounts(long j, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$eF5SqM0WvElUAOELfM1rWPtxF3k(this, j, i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x012c A:{Catch:{ Exception -> 0x017c }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0129 A:{Catch:{ Exception -> 0x017c }} */
    public /* synthetic */ void lambda$getMediaCounts$56$MediaDataController(long r22, int r24) {
        /*
        r21 = this;
        r7 = r21;
        r8 = r22;
        r0 = 5;
        r10 = new int[r0];	 Catch:{ Exception -> 0x017c }
        r11 = -1;
        r12 = 0;
        r10[r12] = r11;	 Catch:{ Exception -> 0x017c }
        r13 = 1;
        r10[r13] = r11;	 Catch:{ Exception -> 0x017c }
        r14 = 2;
        r10[r14] = r11;	 Catch:{ Exception -> 0x017c }
        r15 = 3;
        r10[r15] = r11;	 Catch:{ Exception -> 0x017c }
        r5 = 4;
        r10[r5] = r11;	 Catch:{ Exception -> 0x017c }
        r6 = new int[r0];	 Catch:{ Exception -> 0x017c }
        r6[r12] = r11;	 Catch:{ Exception -> 0x017c }
        r6[r13] = r11;	 Catch:{ Exception -> 0x017c }
        r6[r14] = r11;	 Catch:{ Exception -> 0x017c }
        r6[r15] = r11;	 Catch:{ Exception -> 0x017c }
        r6[r5] = r11;	 Catch:{ Exception -> 0x017c }
        r4 = new int[r0];	 Catch:{ Exception -> 0x017c }
        r4[r12] = r12;	 Catch:{ Exception -> 0x017c }
        r4[r13] = r12;	 Catch:{ Exception -> 0x017c }
        r4[r14] = r12;	 Catch:{ Exception -> 0x017c }
        r4[r15] = r12;	 Catch:{ Exception -> 0x017c }
        r4[r5] = r12;	 Catch:{ Exception -> 0x017c }
        r1 = r21.getMessagesStorage();	 Catch:{ Exception -> 0x017c }
        r1 = r1.getDatabase();	 Catch:{ Exception -> 0x017c }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x017c }
        r3 = "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d";
        r5 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x017c }
        r16 = java.lang.Long.valueOf(r22);	 Catch:{ Exception -> 0x017c }
        r5[r12] = r16;	 Catch:{ Exception -> 0x017c }
        r2 = java.lang.String.format(r2, r3, r5);	 Catch:{ Exception -> 0x017c }
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x017c }
        r1 = r1.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x017c }
    L_0x004d:
        r2 = r1.next();	 Catch:{ Exception -> 0x017c }
        if (r2 == 0) goto L_0x006a;
    L_0x0053:
        r2 = r1.intValue(r12);	 Catch:{ Exception -> 0x017c }
        if (r2 < 0) goto L_0x004d;
    L_0x0059:
        if (r2 >= r0) goto L_0x004d;
    L_0x005b:
        r3 = r1.intValue(r13);	 Catch:{ Exception -> 0x017c }
        r10[r2] = r3;	 Catch:{ Exception -> 0x017c }
        r6[r2] = r3;	 Catch:{ Exception -> 0x017c }
        r3 = r1.intValue(r14);	 Catch:{ Exception -> 0x017c }
        r4[r2] = r3;	 Catch:{ Exception -> 0x017c }
        goto L_0x004d;
    L_0x006a:
        r1.dispose();	 Catch:{ Exception -> 0x017c }
        r0 = (int) r8;	 Catch:{ Exception -> 0x017c }
        if (r0 != 0) goto L_0x00c0;
    L_0x0070:
        r0 = 0;
    L_0x0071:
        r1 = r10.length;	 Catch:{ Exception -> 0x017c }
        if (r0 >= r1) goto L_0x00b6;
    L_0x0074:
        r1 = r10[r0];	 Catch:{ Exception -> 0x017c }
        if (r1 != r11) goto L_0x00b3;
    L_0x0078:
        r1 = r21.getMessagesStorage();	 Catch:{ Exception -> 0x017c }
        r1 = r1.getDatabase();	 Catch:{ Exception -> 0x017c }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x017c }
        r3 = "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r4 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x017c }
        r5 = java.lang.Long.valueOf(r22);	 Catch:{ Exception -> 0x017c }
        r4[r12] = r5;	 Catch:{ Exception -> 0x017c }
        r5 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x017c }
        r4[r13] = r5;	 Catch:{ Exception -> 0x017c }
        r2 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x017c }
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x017c }
        r1 = r1.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x017c }
        r2 = r1.next();	 Catch:{ Exception -> 0x017c }
        if (r2 == 0) goto L_0x00a9;
    L_0x00a2:
        r2 = r1.intValue(r12);	 Catch:{ Exception -> 0x017c }
        r10[r0] = r2;	 Catch:{ Exception -> 0x017c }
        goto L_0x00ab;
    L_0x00a9:
        r10[r0] = r12;	 Catch:{ Exception -> 0x017c }
    L_0x00ab:
        r1.dispose();	 Catch:{ Exception -> 0x017c }
        r1 = r10[r0];	 Catch:{ Exception -> 0x017c }
        r7.putMediaCountDatabase(r8, r0, r1);	 Catch:{ Exception -> 0x017c }
    L_0x00b3:
        r0 = r0 + 1;
        goto L_0x0071;
    L_0x00b6:
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$zRQAPpfsqNn_E_Ghi9-XM9-9Nlw;	 Catch:{ Exception -> 0x017c }
        r0.<init>(r7, r8, r10);	 Catch:{ Exception -> 0x017c }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x017c }
        goto L_0x0180;
    L_0x00c0:
        r5 = 0;
        r16 = 0;
    L_0x00c3:
        r1 = r10.length;	 Catch:{ Exception -> 0x017c }
        if (r5 >= r1) goto L_0x016d;
    L_0x00c6:
        r1 = r10[r5];	 Catch:{ Exception -> 0x017c }
        if (r1 == r11) goto L_0x00db;
    L_0x00ca:
        r1 = r4[r5];	 Catch:{ Exception -> 0x017c }
        if (r1 != r13) goto L_0x00cf;
    L_0x00ce:
        goto L_0x00db;
    L_0x00cf:
        r3 = r24;
        r18 = r4;
        r17 = r5;
        r19 = r6;
        r20 = 4;
        goto L_0x0162;
    L_0x00db:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_search;	 Catch:{ Exception -> 0x017c }
        r3.<init>();	 Catch:{ Exception -> 0x017c }
        r3.limit = r13;	 Catch:{ Exception -> 0x017c }
        r3.offset_id = r12;	 Catch:{ Exception -> 0x017c }
        if (r5 != 0) goto L_0x00ef;
    L_0x00e6:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;	 Catch:{ Exception -> 0x017c }
        r1.<init>();	 Catch:{ Exception -> 0x017c }
        r3.filter = r1;	 Catch:{ Exception -> 0x017c }
    L_0x00ed:
        r2 = 4;
        goto L_0x0117;
    L_0x00ef:
        if (r5 != r13) goto L_0x00f9;
    L_0x00f1:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;	 Catch:{ Exception -> 0x017c }
        r1.<init>();	 Catch:{ Exception -> 0x017c }
        r3.filter = r1;	 Catch:{ Exception -> 0x017c }
        goto L_0x00ed;
    L_0x00f9:
        if (r5 != r14) goto L_0x0103;
    L_0x00fb:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;	 Catch:{ Exception -> 0x017c }
        r1.<init>();	 Catch:{ Exception -> 0x017c }
        r3.filter = r1;	 Catch:{ Exception -> 0x017c }
        goto L_0x00ed;
    L_0x0103:
        if (r5 != r15) goto L_0x010d;
    L_0x0105:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;	 Catch:{ Exception -> 0x017c }
        r1.<init>();	 Catch:{ Exception -> 0x017c }
        r3.filter = r1;	 Catch:{ Exception -> 0x017c }
        goto L_0x00ed;
    L_0x010d:
        r2 = 4;
        if (r5 != r2) goto L_0x0117;
    L_0x0110:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;	 Catch:{ Exception -> 0x017c }
        r1.<init>();	 Catch:{ Exception -> 0x017c }
        r3.filter = r1;	 Catch:{ Exception -> 0x017c }
    L_0x0117:
        r1 = "";
        r3.q = r1;	 Catch:{ Exception -> 0x017c }
        r1 = r21.getMessagesController();	 Catch:{ Exception -> 0x017c }
        r1 = r1.getInputPeer(r0);	 Catch:{ Exception -> 0x017c }
        r3.peer = r1;	 Catch:{ Exception -> 0x017c }
        r1 = r3.peer;	 Catch:{ Exception -> 0x017c }
        if (r1 != 0) goto L_0x012c;
    L_0x0129:
        r10[r5] = r12;	 Catch:{ Exception -> 0x017c }
        goto L_0x00cf;
    L_0x012c:
        r1 = r21.getConnectionsManager();	 Catch:{ Exception -> 0x017c }
        r12 = new org.telegram.messenger.-$$Lambda$MediaDataController$WBbpBDydgE0dsAgXb7eiXLR6DZ4;	 Catch:{ Exception -> 0x017c }
        r14 = r1;
        r1 = r12;
        r17 = 4;
        r2 = r21;
        r15 = r3;
        r3 = r10;
        r18 = r4;
        r4 = r5;
        r17 = r5;
        r19 = r6;
        r20 = 4;
        r5 = r22;
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x017c }
        r1 = r14.sendRequest(r15, r12);	 Catch:{ Exception -> 0x017c }
        r2 = r21.getConnectionsManager();	 Catch:{ Exception -> 0x017c }
        r3 = r24;
        r2.bindRequestToGuid(r1, r3);	 Catch:{ Exception -> 0x017c }
        r1 = r10[r17];	 Catch:{ Exception -> 0x017c }
        if (r1 != r11) goto L_0x015c;
    L_0x0159:
        r16 = 1;
        goto L_0x0162;
    L_0x015c:
        r1 = r18[r17];	 Catch:{ Exception -> 0x017c }
        if (r1 != r13) goto L_0x0162;
    L_0x0160:
        r10[r17] = r11;	 Catch:{ Exception -> 0x017c }
    L_0x0162:
        r5 = r17 + 1;
        r4 = r18;
        r6 = r19;
        r12 = 0;
        r14 = 2;
        r15 = 3;
        goto L_0x00c3;
    L_0x016d:
        r19 = r6;
        if (r16 != 0) goto L_0x0180;
    L_0x0171:
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$BD-15hl8jHIlzYEeWJ7Gb_lb95s;	 Catch:{ Exception -> 0x017c }
        r1 = r19;
        r0.<init>(r7, r8, r1);	 Catch:{ Exception -> 0x017c }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x017c }
        goto L_0x0180;
    L_0x017c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0180:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$getMediaCounts$56$MediaDataController(long, int):void");
    }

    public /* synthetic */ void lambda$null$52$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$54$MediaDataController(int[] iArr, int i, long j, TLObject tLObject, TL_error tL_error) {
        Object obj = null;
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (messages_messages instanceof TL_messages_messages) {
                iArr[i] = messages_messages.messages.size();
            } else {
                iArr[i] = messages_messages.count;
            }
            putMediaCountDatabase(j, i, iArr[i]);
        } else {
            iArr[i] = 0;
        }
        for (int i2 : iArr) {
            if (i2 == -1) {
                break;
            }
        }
        obj = 1;
        if (obj != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$foViV4tAsqlnBiPBSlUxeT69GAI(this, j, iArr));
        }
    }

    public /* synthetic */ void lambda$null$53$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$55$MediaDataController(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public void getMediaCount(long j, int i, int i2, boolean z) {
        int i3 = (int) j;
        if (z || i3 == 0) {
            getMediaCountDatabase(j, i, i2);
        } else {
            TL_messages_search tL_messages_search = new TL_messages_search();
            tL_messages_search.limit = 1;
            tL_messages_search.offset_id = 0;
            if (i == 0) {
                tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
            } else if (i == 1) {
                tL_messages_search.filter = new TL_inputMessagesFilterDocument();
            } else if (i == 2) {
                tL_messages_search.filter = new TL_inputMessagesFilterRoundVoice();
            } else if (i == 3) {
                tL_messages_search.filter = new TL_inputMessagesFilterUrl();
            } else if (i == 4) {
                tL_messages_search.filter = new TL_inputMessagesFilterMusic();
            }
            tL_messages_search.q = "";
            tL_messages_search.peer = getMessagesController().getInputPeer(i3);
            if (tL_messages_search.peer != null) {
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new -$$Lambda$MediaDataController$xhEg66KcWaHaHYMDQHDEwrYhFQY(this, j, i, i2)), i2);
            }
        }
    }

    public /* synthetic */ void lambda$getMediaCount$58$MediaDataController(long j, int i, int i2, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            int size;
            messages_Messages messages_messages = (messages_Messages) tLObject;
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            if (messages_messages instanceof TL_messages_messages) {
                size = messages_messages.messages.size();
            } else {
                size = messages_messages.count;
            }
            int i3 = size;
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$nxp-mqKi81SSU7O88ZrlDCHq5jo(this, messages_messages));
            processLoadedMediaCount(i3, j, i, i2, false, 0);
        }
    }

    public /* synthetic */ void lambda$null$57$MediaDataController(messages_Messages messages_messages) {
        getMessagesController().putUsers(messages_messages.users, false);
        getMessagesController().putChats(messages_messages.chats, false);
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        MessageMedia messageMedia = message.media;
        int i = 0;
        if (messageMedia instanceof TL_messageMediaPhoto) {
            return 0;
        }
        if (!(messageMedia instanceof TL_messageMediaDocument)) {
            if (!message.entities.isEmpty()) {
                while (i < message.entities.size()) {
                    MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                    if ((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityEmail)) {
                        return 3;
                    }
                    i++;
                }
            }
            return -1;
        } else if (MessageObject.isVoiceMessage(message) || MessageObject.isRoundVideoMessage(message)) {
            return 2;
        } else {
            if (MessageObject.isVideoMessage(message)) {
                return 0;
            }
            if (MessageObject.isStickerMessage(message)) {
                return -1;
            }
            return MessageObject.isMusicMessage(message) ? 4 : 1;
        }
    }

    public static boolean canAddMessageToMedia(Message message) {
        MessageMedia messageMedia;
        boolean z = message instanceof TL_message_secret;
        if (z && ((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message))) {
            int i = message.media.ttl_seconds;
            if (i != 0 && i <= 60) {
                return false;
            }
        }
        if (!z && (message instanceof TL_message)) {
            messageMedia = message.media;
            if (((messageMedia instanceof TL_messageMediaPhoto) || (messageMedia instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return false;
            }
        }
        messageMedia = message.media;
        if ((messageMedia instanceof TL_messageMediaPhoto) || ((messageMedia instanceof TL_messageMediaDocument) && !MessageObject.isGifDocument(messageMedia.document))) {
            return true;
        }
        if (!message.entities.isEmpty()) {
            for (int i2 = 0; i2 < message.entities.size(); i2++) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i2);
                if ((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl) || (messageEntity instanceof TL_messageEntityEmail)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processLoadedMedia(messages_Messages messages_messages, long j, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2) {
        messages_Messages messages_messages2 = messages_messages;
        int i6 = i4;
        int i7 = (int) j;
        if (i6 == 0 || !messages_messages2.messages.isEmpty() || i7 == 0) {
            if (i6 == 0) {
                ImageLoader.saveMessagesThumbs(messages_messages2.messages);
                getMessagesStorage().putUsersAndChats(messages_messages2.users, messages_messages2.chats, true, true);
                putMediaDatabase(j, i3, messages_messages2.messages, i2, z2);
            }
            SparseArray sparseArray = new SparseArray();
            for (int i8 = 0; i8 < messages_messages2.users.size(); i8++) {
                User user = (User) messages_messages2.users.get(i8);
                sparseArray.put(user.id, user);
            }
            ArrayList arrayList = new ArrayList();
            for (int i9 = 0; i9 < messages_messages2.messages.size(); i9++) {
                arrayList.add(new MessageObject(this.currentAccount, (Message) messages_messages2.messages.get(i9), sparseArray, true));
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$7ORMOXWyd_5u68ANeEx8i5blZAU(this, messages_messages, i4, j, arrayList, i5, i3, z2));
        } else if (i6 != 2) {
            loadMedia(j, i, i2, i3, 0, i5);
        }
    }

    public /* synthetic */ void lambda$processLoadedMedia$59$MediaDataController(messages_Messages messages_messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z) {
        int i4 = messages_messages.count;
        getMessagesController().putUsers(messages_messages.users, i != 0);
        getMessagesController().putChats(messages_messages.chats, i != 0);
        getNotificationCenter().postNotificationName(NotificationCenter.mediaDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Integer.valueOf(i2), Integer.valueOf(i3), Boolean.valueOf(z));
    }

    private void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z, int i4) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$s5Wdg4oqNR0OYWccIsIuQdgeAqQ(this, j, z, i, i2, i4, i3));
    }

    public /* synthetic */ void lambda$processLoadedMediaCount$60$MediaDataController(long j, boolean z, int i, int i2, int i3, int i4) {
        long j2 = j;
        int i5 = i;
        int i6 = i2;
        int i7 = (int) j2;
        Object obj = (!z || (!(i5 == -1 || (i5 == 0 && i6 == 2)) || i7 == 0)) ? null : 1;
        if (obj != null || (i3 == 1 && i7 != 0)) {
            getMediaCount(j, i2, i4, false);
        }
        if (obj == null) {
            if (z) {
            } else {
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
            return;
        }
    }

    private void putMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$aLhxc_RBdtl1gfSDe8gG0Rn_zJw(this, j, i, i2));
    }

    public /* synthetic */ void lambda$putMediaCountDatabase$61$MediaDataController(long j, int i, int i2) {
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
            FileLog.e(e);
        }
    }

    private void getMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$Ed_j1qbi1iX511FPCIAC5di6VHo(this, j, i, i2));
    }

    public /* synthetic */ void lambda$getMediaCountDatabase$62$MediaDataController(long j, int i, int i2) {
        Throwable e;
        long j2 = j;
        try {
            int intValue;
            int intValue2;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next()) {
                intValue = queryFinalized.intValue(0);
                intValue2 = queryFinalized.intValue(1);
            } else {
                intValue = -1;
                intValue2 = 0;
            }
            queryFinalized.dispose();
            int i3 = (int) j2;
            if (intValue == -1 && i3 == 0) {
                queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized.next()) {
                    intValue = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (intValue != -1) {
                    i3 = i;
                    try {
                        putMediaCountDatabase(j, i, intValue);
                        processLoadedMediaCount(intValue, j, i, i2, true, intValue2);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                    }
                }
            }
            i3 = i;
            processLoadedMediaCount(intValue, j, i, i2, true, intValue2);
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
        }
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, boolean z, int i5) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$k3TK4m4a_Pf-DHYYO4A-ZPC2XUY(this, i, j, i2, z, i3, i5, i4));
    }

    public /* synthetic */ void lambda$loadMediaDatabase$63$MediaDataController(int i, long j, int i2, boolean z, int i3, int i4, int i5) {
        Throwable e;
        boolean z2;
        int i6 = i;
        long j2 = j;
        int i7 = i2;
        TL_messages_messages tL_messages_messages = new TL_messages_messages();
        try {
            boolean z3;
            SQLiteCursor queryFinalized;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int i8 = i6 + 1;
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            int i9 = (int) j2;
            if (i9 != 0) {
                long j3 = (long) i7;
                int i10 = z ? -i9 : 0;
                long j4 = 0;
                if (!(j3 == 0 || i10 == 0)) {
                    j3 |= ((long) i10) << 32;
                }
                try {
                    int i11;
                    boolean z4;
                    int i12 = i10;
                    SQLiteCursor queryFinalized2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                    if (queryFinalized2.next()) {
                        boolean z5 = queryFinalized2.intValue(0) == 1;
                        queryFinalized2.dispose();
                        z3 = z5;
                        i11 = i12;
                    } else {
                        queryFinalized2.dispose();
                        SQLiteCursor queryFinalized3 = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                        if (queryFinalized3.next()) {
                            int intValue = queryFinalized3.intValue(0);
                            if (intValue != 0) {
                                SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                executeFast.requery();
                                executeFast.bindLong(1, j2);
                                executeFast.bindInteger(2, i3);
                                executeFast.bindInteger(3, 0);
                                executeFast.bindInteger(4, intValue);
                                i11 = i12;
                                executeFast.step();
                                executeFast.dispose();
                                queryFinalized3.dispose();
                                z3 = false;
                            }
                        }
                        i11 = i12;
                        queryFinalized3.dispose();
                        z3 = false;
                    }
                    if (j3 != 0) {
                        z4 = z3;
                        queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2)}), new Object[0]);
                        if (queryFinalized.next()) {
                            long intValue2 = (long) queryFinalized.intValue(0);
                            j4 = i11 != 0 ? intValue2 | (((long) i11) << 32) : intValue2;
                        }
                        queryFinalized.dispose();
                        if (j4 > 1) {
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j3), Long.valueOf(j4), Integer.valueOf(i3), Integer.valueOf(i8)}), new Object[0]);
                        } else {
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j3), Integer.valueOf(i3), Integer.valueOf(i8)}), new Object[0]);
                        }
                    } else {
                        z4 = z3;
                        queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                        if (queryFinalized.next()) {
                            j3 = (long) queryFinalized.intValue(0);
                            j4 = i11 != 0 ? j3 | (((long) i11) << 32) : j3;
                        }
                        queryFinalized.dispose();
                        if (j4 > 1) {
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(j4), Integer.valueOf(i3), Integer.valueOf(i8)}), new Object[0]);
                        } else {
                            queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i8)}), new Object[0]);
                        }
                    }
                    z3 = z4;
                    i8 = 0;
                } catch (Exception e2) {
                    e = e2;
                    i6 = i;
                    z2 = false;
                    try {
                        tL_messages_messages.messages.clear();
                        tL_messages_messages.chats.clear();
                        tL_messages_messages.users.clear();
                        FileLog.e(e);
                        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
                    } catch (Throwable th) {
                        e = th;
                        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, false);
                        throw e;
                    }
                } catch (Throwable th2) {
                    e = th2;
                    i6 = i;
                    processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, false);
                    throw e;
                }
            }
            if (i7 != 0) {
                queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i8)}), new Object[0]);
                i8 = 0;
            } else {
                Object[] objArr = new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i8)};
                i8 = 0;
                queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", objArr), new Object[0]);
            }
            z3 = true;
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(i8);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(i8), i8);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.dialog_id = j2;
                    if (i9 == 0) {
                        TLdeserialize.random_id = queryFinalized.longValue(2);
                    }
                    tL_messages_messages.messages.add(TLdeserialize);
                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList, arrayList2);
                }
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(str, arrayList), tL_messages_messages.users);
            }
            if (!arrayList2.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(str, arrayList2), tL_messages_messages.chats);
            }
            if (tL_messages_messages.messages.size() > i) {
                tL_messages_messages.messages.remove(tL_messages_messages.messages.size() - 1);
                z2 = false;
            } else {
                z2 = z3;
            }
        } catch (Exception e3) {
            e = e3;
            z2 = false;
            tL_messages_messages.messages.clear();
            tL_messages_messages.chats.clear();
            tL_messages_messages.users.clear();
            FileLog.e(e);
            processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
        }
        processLoadedMedia(tL_messages_messages, j, i, i2, i3, i4, i5, z, z2);
    }

    private void putMediaDatabase(long j, int i, ArrayList<Message> arrayList, int i2, boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$xibU_KYHvH1vFuaP3V7IC2SH8uA(this, arrayList, z, j, i2, i));
    }

    public /* synthetic */ void lambda$putMediaDatabase$64$MediaDataController(ArrayList arrayList, boolean z, long j, int i, int i2) {
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        try {
            if (arrayList.isEmpty() || z) {
                getMessagesStorage().doneHolesInMedia(j2, i3, i4);
                if (arrayList.isEmpty()) {
                    return;
                }
            }
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Message message = (Message) it.next();
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
                    executeFast.bindInteger(4, i4);
                    executeFast.bindByteBuffer(5, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                }
            }
            executeFast.dispose();
            if (!(z && i3 == 0)) {
                int i5;
                if (z) {
                    i5 = 1;
                } else {
                    ArrayList arrayList2 = arrayList;
                    i5 = ((Message) arrayList.get(arrayList.size() - 1)).id;
                }
                if (i3 != 0) {
                    getMessagesStorage().closeHolesInMedia(j, i5, i, i2);
                } else {
                    getMessagesStorage().closeHolesInMedia(j, i5, Integer.MAX_VALUE, i2);
                }
            }
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadMusic(long j, long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$h5rvQJ07EjOwjvauQFz76RVp3J8(this, j, j2));
    }

    public /* synthetic */ void lambda$loadMusic$66$MediaDataController(long j, long j2) {
        SQLiteCursor queryFinalized;
        ArrayList arrayList = new ArrayList();
        if (((int) j) != 0) {
            try {
                queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
        }
        while (queryFinalized.next()) {
            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
            if (byteBufferValue != null) {
                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                byteBufferValue.reuse();
                if (MessageObject.isMusicMessage(TLdeserialize)) {
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.dialog_id = j;
                    arrayList.add(0, new MessageObject(this.currentAccount, TLdeserialize, false));
                }
            }
        }
        queryFinalized.dispose();
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$HjUrk2jcRv-YHBoICgEo5Qnm9GQ(this, j, arrayList));
    }

    public /* synthetic */ void lambda$null$65$MediaDataController(long j, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(j), arrayList);
    }

    public void buildShortcuts() {
        if (VERSION.SDK_INT >= 25) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.hints.size(); i++) {
                arrayList.add(this.hints.get(i));
                if (arrayList.size() == 3) {
                    break;
                }
            }
            Utilities.globalQueue.postRunnable(new -$$Lambda$MediaDataController$NDEDnyIZzlzvYo0jZa7xysTxKKs(this, arrayList));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:86:0x0258 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x026f A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x028e A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0286 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02ab A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02a7 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0258 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x026f A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0286 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x028e A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02a7 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02ab A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x026f A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x028e A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0286 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02ab A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02a7 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x026f A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0286 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x028e A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02a7 A:{Catch:{ Throwable -> 0x02b7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02ab A:{Catch:{ Throwable -> 0x02b7 }} */
    public /* synthetic */ void lambda$buildShortcuts$67$MediaDataController(java.util.ArrayList r21) {
        /*
        r20 = this;
        r1 = r21;
        r0 = "NewConversationShortcut";
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r3 = android.content.pm.ShortcutManager.class;
        r2 = r2.getSystemService(r3);	 Catch:{ Throwable -> 0x02b7 }
        r2 = (android.content.pm.ShortcutManager) r2;	 Catch:{ Throwable -> 0x02b7 }
        r3 = r2.getDynamicShortcuts();	 Catch:{ Throwable -> 0x02b7 }
        r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02b7 }
        r4.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02b7 }
        r5.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r6 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02b7 }
        r6.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r7 = "did";
        r8 = 0;
        r9 = "compose";
        if (r3 == 0) goto L_0x009f;
    L_0x0028:
        r10 = r3.isEmpty();	 Catch:{ Throwable -> 0x02b7 }
        if (r10 != 0) goto L_0x009f;
    L_0x002e:
        r5.add(r9);	 Catch:{ Throwable -> 0x02b7 }
        r10 = 0;
    L_0x0032:
        r11 = r21.size();	 Catch:{ Throwable -> 0x02b7 }
        if (r10 >= r11) goto L_0x0072;
    L_0x0038:
        r11 = r1.get(r10);	 Catch:{ Throwable -> 0x02b7 }
        r11 = (org.telegram.tgnet.TLRPC.TL_topPeer) r11;	 Catch:{ Throwable -> 0x02b7 }
        r12 = r11.peer;	 Catch:{ Throwable -> 0x02b7 }
        r12 = r12.user_id;	 Catch:{ Throwable -> 0x02b7 }
        if (r12 == 0) goto L_0x004a;
    L_0x0044:
        r11 = r11.peer;	 Catch:{ Throwable -> 0x02b7 }
        r11 = r11.user_id;	 Catch:{ Throwable -> 0x02b7 }
    L_0x0048:
        r11 = (long) r11;	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x005d;
    L_0x004a:
        r12 = r11.peer;	 Catch:{ Throwable -> 0x02b7 }
        r12 = r12.chat_id;	 Catch:{ Throwable -> 0x02b7 }
        r12 = -r12;
        r12 = (long) r12;	 Catch:{ Throwable -> 0x02b7 }
        r14 = 0;
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r16 != 0) goto L_0x005c;
    L_0x0056:
        r11 = r11.peer;	 Catch:{ Throwable -> 0x02b7 }
        r11 = r11.channel_id;	 Catch:{ Throwable -> 0x02b7 }
        r11 = -r11;
        goto L_0x0048;
    L_0x005c:
        r11 = r12;
    L_0x005d:
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02b7 }
        r13.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r13.append(r7);	 Catch:{ Throwable -> 0x02b7 }
        r13.append(r11);	 Catch:{ Throwable -> 0x02b7 }
        r11 = r13.toString();	 Catch:{ Throwable -> 0x02b7 }
        r5.add(r11);	 Catch:{ Throwable -> 0x02b7 }
        r10 = r10 + 1;
        goto L_0x0032;
    L_0x0072:
        r10 = 0;
    L_0x0073:
        r11 = r3.size();	 Catch:{ Throwable -> 0x02b7 }
        if (r10 >= r11) goto L_0x0092;
    L_0x0079:
        r11 = r3.get(r10);	 Catch:{ Throwable -> 0x02b7 }
        r11 = (android.content.pm.ShortcutInfo) r11;	 Catch:{ Throwable -> 0x02b7 }
        r11 = r11.getId();	 Catch:{ Throwable -> 0x02b7 }
        r12 = r5.remove(r11);	 Catch:{ Throwable -> 0x02b7 }
        if (r12 != 0) goto L_0x008c;
    L_0x0089:
        r6.add(r11);	 Catch:{ Throwable -> 0x02b7 }
    L_0x008c:
        r4.add(r11);	 Catch:{ Throwable -> 0x02b7 }
        r10 = r10 + 1;
        goto L_0x0073;
    L_0x0092:
        r3 = r5.isEmpty();	 Catch:{ Throwable -> 0x02b7 }
        if (r3 == 0) goto L_0x009f;
    L_0x0098:
        r3 = r6.isEmpty();	 Catch:{ Throwable -> 0x02b7 }
        if (r3 == 0) goto L_0x009f;
    L_0x009e:
        return;
    L_0x009f:
        r3 = new android.content.Intent;	 Catch:{ Throwable -> 0x02b7 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r10 = org.telegram.ui.LaunchActivity.class;
        r3.<init>(r5, r10);	 Catch:{ Throwable -> 0x02b7 }
        r5 = "new_dialog";
        r3.setAction(r5);	 Catch:{ Throwable -> 0x02b7 }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02b7 }
        r5.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r10 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02b7 }
        r11 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r10.<init>(r11, r9);	 Catch:{ Throwable -> 0x02b7 }
        r11 = NUM; // 0x7f0d0612 float:1.8745267E38 double:1.0531305453E-314;
        r12 = org.telegram.messenger.LocaleController.getString(r0, r11);	 Catch:{ Throwable -> 0x02b7 }
        r10 = r10.setShortLabel(r12);	 Catch:{ Throwable -> 0x02b7 }
        r0 = org.telegram.messenger.LocaleController.getString(r0, r11);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r10.setLongLabel(r0);	 Catch:{ Throwable -> 0x02b7 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r11 = NUM; // 0x7var_ float:1.7945724E38 double:1.052935782E-314;
        r10 = android.graphics.drawable.Icon.createWithResource(r10, r11);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.setIcon(r10);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.setIntent(r3);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.build();	 Catch:{ Throwable -> 0x02b7 }
        r5.add(r0);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r4.contains(r9);	 Catch:{ Throwable -> 0x02b7 }
        if (r0 == 0) goto L_0x00ee;
    L_0x00ea:
        r2.updateShortcuts(r5);	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x00f1;
    L_0x00ee:
        r2.addDynamicShortcuts(r5);	 Catch:{ Throwable -> 0x02b7 }
    L_0x00f1:
        r5.clear();	 Catch:{ Throwable -> 0x02b7 }
        r0 = r6.isEmpty();	 Catch:{ Throwable -> 0x02b7 }
        if (r0 != 0) goto L_0x00fd;
    L_0x00fa:
        r2.removeDynamicShortcuts(r6);	 Catch:{ Throwable -> 0x02b7 }
    L_0x00fd:
        r0 = r21.size();	 Catch:{ Throwable -> 0x02b7 }
        if (r8 >= r0) goto L_0x02b7;
    L_0x0103:
        r3 = new android.content.Intent;	 Catch:{ Throwable -> 0x02b7 }
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r6 = org.telegram.messenger.OpenChatReceiver.class;
        r3.<init>(r0, r6);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r1.get(r8);	 Catch:{ Throwable -> 0x02b7 }
        r0 = (org.telegram.tgnet.TLRPC.TL_topPeer) r0;	 Catch:{ Throwable -> 0x02b7 }
        r6 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r6 = r6.user_id;	 Catch:{ Throwable -> 0x02b7 }
        if (r6 == 0) goto L_0x0138;
    L_0x0118:
        r6 = "userId";
        r10 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r10 = r10.user_id;	 Catch:{ Throwable -> 0x02b7 }
        r3.putExtra(r6, r10);	 Catch:{ Throwable -> 0x02b7 }
        r6 = r20.getMessagesController();	 Catch:{ Throwable -> 0x02b7 }
        r10 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r10 = r10.user_id;	 Catch:{ Throwable -> 0x02b7 }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x02b7 }
        r6 = r6.getUser(r10);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.user_id;	 Catch:{ Throwable -> 0x02b7 }
        r10 = (long) r0;	 Catch:{ Throwable -> 0x02b7 }
        r0 = 0;
        goto L_0x0156;
    L_0x0138:
        r6 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r6 = r6.chat_id;	 Catch:{ Throwable -> 0x02b7 }
        if (r6 != 0) goto L_0x0142;
    L_0x013e:
        r0 = r0.peer;	 Catch:{ Throwable -> 0x02b7 }
        r6 = r0.channel_id;	 Catch:{ Throwable -> 0x02b7 }
    L_0x0142:
        r0 = r20.getMessagesController();	 Catch:{ Throwable -> 0x02b7 }
        r10 = java.lang.Integer.valueOf(r6);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.getChat(r10);	 Catch:{ Throwable -> 0x02b7 }
        r10 = "chatId";
        r3.putExtra(r10, r6);	 Catch:{ Throwable -> 0x02b7 }
        r6 = -r6;
        r10 = (long) r6;	 Catch:{ Throwable -> 0x02b7 }
        r6 = 0;
    L_0x0156:
        if (r6 == 0) goto L_0x015e;
    L_0x0158:
        r12 = org.telegram.messenger.UserObject.isDeleted(r6);	 Catch:{ Throwable -> 0x02b7 }
        if (r12 == 0) goto L_0x0162;
    L_0x015e:
        if (r0 != 0) goto L_0x0162;
    L_0x0160:
        goto L_0x02b1;
    L_0x0162:
        if (r6 == 0) goto L_0x017c;
    L_0x0164:
        r0 = r6.first_name;	 Catch:{ Throwable -> 0x02b7 }
        r12 = r6.last_name;	 Catch:{ Throwable -> 0x02b7 }
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r12);	 Catch:{ Throwable -> 0x02b7 }
        r12 = r6.photo;	 Catch:{ Throwable -> 0x02b7 }
        if (r12 == 0) goto L_0x017a;
    L_0x0170:
        r6 = r6.photo;	 Catch:{ Throwable -> 0x02b7 }
        r6 = r6.photo_small;	 Catch:{ Throwable -> 0x02b7 }
        r19 = r6;
        r6 = r0;
        r0 = r19;
        goto L_0x0188;
    L_0x017a:
        r6 = r0;
        goto L_0x0187;
    L_0x017c:
        r6 = r0.title;	 Catch:{ Throwable -> 0x02b7 }
        r12 = r0.photo;	 Catch:{ Throwable -> 0x02b7 }
        if (r12 == 0) goto L_0x0187;
    L_0x0182:
        r0 = r0.photo;	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.photo_small;	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x0188;
    L_0x0187:
        r0 = 0;
    L_0x0188:
        r12 = "currentAccount";
        r13 = r20;
        r14 = r13.currentAccount;	 Catch:{ Throwable -> 0x02b7 }
        r3.putExtra(r12, r14);	 Catch:{ Throwable -> 0x02b7 }
        r12 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02b7 }
        r12.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r14 = "com.tmessages.openchat";
        r12.append(r14);	 Catch:{ Throwable -> 0x02b7 }
        r12.append(r10);	 Catch:{ Throwable -> 0x02b7 }
        r12 = r12.toString();	 Catch:{ Throwable -> 0x02b7 }
        r3.setAction(r12);	 Catch:{ Throwable -> 0x02b7 }
        r12 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r3.addFlags(r12);	 Catch:{ Throwable -> 0x02b7 }
        if (r0 == 0) goto L_0x0258;
    L_0x01ac:
        r12 = 1;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12);	 Catch:{ Throwable -> 0x0251 }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x0251 }
        r14 = android.graphics.BitmapFactory.decodeFile(r0);	 Catch:{ Throwable -> 0x0251 }
        if (r14 == 0) goto L_0x024f;
    L_0x01bb:
        r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Throwable -> 0x024c }
        r15 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x024c }
        r15 = android.graphics.Bitmap.createBitmap(r0, r0, r15);	 Catch:{ Throwable -> 0x024c }
        r9 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x024c }
        r9.<init>(r15);	 Catch:{ Throwable -> 0x024c }
        r17 = roundPaint;	 Catch:{ Throwable -> 0x024c }
        r18 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r17 != 0) goto L_0x021a;
    L_0x01d2:
        r12 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x024c }
        r1 = 3;
        r12.<init>(r1);	 Catch:{ Throwable -> 0x024c }
        roundPaint = r12;	 Catch:{ Throwable -> 0x024c }
        r1 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x024c }
        r1.<init>();	 Catch:{ Throwable -> 0x024c }
        bitmapRect = r1;	 Catch:{ Throwable -> 0x024c }
        r1 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x024c }
        r12 = 1;
        r1.<init>(r12);	 Catch:{ Throwable -> 0x024c }
        erasePaint = r1;	 Catch:{ Throwable -> 0x024c }
        r1 = erasePaint;	 Catch:{ Throwable -> 0x024c }
        r12 = new android.graphics.PorterDuffXfermode;	 Catch:{ Throwable -> 0x024c }
        r13 = android.graphics.PorterDuff.Mode.CLEAR;	 Catch:{ Throwable -> 0x024c }
        r12.<init>(r13);	 Catch:{ Throwable -> 0x024c }
        r1.setXfermode(r12);	 Catch:{ Throwable -> 0x024c }
        r1 = new android.graphics.Path;	 Catch:{ Throwable -> 0x024c }
        r1.<init>();	 Catch:{ Throwable -> 0x024c }
        roundPath = r1;	 Catch:{ Throwable -> 0x024c }
        r1 = roundPath;	 Catch:{ Throwable -> 0x024c }
        r12 = r0 / 2;
        r12 = (float) r12;	 Catch:{ Throwable -> 0x024c }
        r13 = r0 / 2;
        r13 = (float) r13;	 Catch:{ Throwable -> 0x024c }
        r0 = r0 / 2;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x024c }
        r0 = r0 - r17;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x024c }
        r17 = r15;
        r15 = android.graphics.Path.Direction.CW;	 Catch:{ Throwable -> 0x024c }
        r1.addCircle(r12, r13, r0, r15);	 Catch:{ Throwable -> 0x024c }
        r0 = roundPath;	 Catch:{ Throwable -> 0x024c }
        r0.toggleInverseFillType();	 Catch:{ Throwable -> 0x024c }
        goto L_0x021c;
    L_0x021a:
        r17 = r15;
    L_0x021c:
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x024c }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x024c }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x024c }
        r12 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x024c }
        r12 = (float) r12;	 Catch:{ Throwable -> 0x024c }
        r13 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x024c }
        r15 = (float) r15;	 Catch:{ Throwable -> 0x024c }
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x024c }
        r13 = (float) r13;	 Catch:{ Throwable -> 0x024c }
        r0.set(r1, r12, r15, r13);	 Catch:{ Throwable -> 0x024c }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x024c }
        r1 = roundPaint;	 Catch:{ Throwable -> 0x024c }
        r12 = 0;
        r9.drawBitmap(r14, r12, r0, r1);	 Catch:{ Throwable -> 0x024c }
        r0 = roundPath;	 Catch:{ Throwable -> 0x024c }
        r1 = erasePaint;	 Catch:{ Throwable -> 0x024c }
        r9.drawPath(r0, r1);	 Catch:{ Throwable -> 0x024c }
        r9.setBitmap(r12);	 Catch:{ Exception -> 0x0249 }
    L_0x0249:
        r9 = r17;
        goto L_0x025a;
    L_0x024c:
        r0 = move-exception;
        r9 = r14;
        goto L_0x0254;
    L_0x024f:
        r9 = r14;
        goto L_0x025a;
    L_0x0251:
        r0 = move-exception;
        r12 = 0;
        r9 = r12;
    L_0x0254:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x025a;
    L_0x0258:
        r12 = 0;
        r9 = r12;
    L_0x025a:
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02b7 }
        r0.<init>();	 Catch:{ Throwable -> 0x02b7 }
        r0.append(r7);	 Catch:{ Throwable -> 0x02b7 }
        r0.append(r10);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x02b7 }
        r1 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Throwable -> 0x02b7 }
        if (r1 == 0) goto L_0x0271;
    L_0x026f:
        r6 = " ";
    L_0x0271:
        r1 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02b7 }
        r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r1.<init>(r10, r0);	 Catch:{ Throwable -> 0x02b7 }
        r1 = r1.setShortLabel(r6);	 Catch:{ Throwable -> 0x02b7 }
        r1 = r1.setLongLabel(r6);	 Catch:{ Throwable -> 0x02b7 }
        r1 = r1.setIntent(r3);	 Catch:{ Throwable -> 0x02b7 }
        if (r9 == 0) goto L_0x028e;
    L_0x0286:
        r3 = android.graphics.drawable.Icon.createWithBitmap(r9);	 Catch:{ Throwable -> 0x02b7 }
        r1.setIcon(r3);	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x029a;
    L_0x028e:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02b7 }
        r6 = NUM; // 0x7var_ float:1.7945726E38 double:1.0529357827E-314;
        r3 = android.graphics.drawable.Icon.createWithResource(r3, r6);	 Catch:{ Throwable -> 0x02b7 }
        r1.setIcon(r3);	 Catch:{ Throwable -> 0x02b7 }
    L_0x029a:
        r1 = r1.build();	 Catch:{ Throwable -> 0x02b7 }
        r5.add(r1);	 Catch:{ Throwable -> 0x02b7 }
        r0 = r4.contains(r0);	 Catch:{ Throwable -> 0x02b7 }
        if (r0 == 0) goto L_0x02ab;
    L_0x02a7:
        r2.updateShortcuts(r5);	 Catch:{ Throwable -> 0x02b7 }
        goto L_0x02ae;
    L_0x02ab:
        r2.addDynamicShortcuts(r5);	 Catch:{ Throwable -> 0x02b7 }
    L_0x02ae:
        r5.clear();	 Catch:{ Throwable -> 0x02b7 }
    L_0x02b1:
        r8 = r8 + 1;
        r1 = r21;
        goto L_0x00fd;
    L_0x02b7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$buildShortcuts$67$MediaDataController(java.util.ArrayList):void");
    }

    public void loadHints(boolean z) {
        if (!this.loading && getUserConfig().suggestContacts) {
            if (!z) {
                this.loading = true;
                TL_contacts_getTopPeers tL_contacts_getTopPeers = new TL_contacts_getTopPeers();
                tL_contacts_getTopPeers.hash = 0;
                tL_contacts_getTopPeers.bots_pm = false;
                tL_contacts_getTopPeers.correspondents = true;
                tL_contacts_getTopPeers.groups = false;
                tL_contacts_getTopPeers.channels = false;
                tL_contacts_getTopPeers.bots_inline = true;
                tL_contacts_getTopPeers.offset = 0;
                tL_contacts_getTopPeers.limit = 20;
                getConnectionsManager().sendRequest(tL_contacts_getTopPeers, new -$$Lambda$MediaDataController$oWX2wLFwSogJfo4Yleselsta73w(this));
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$gt5XDb4twM5cmLMVbzureEo_OsQ(this));
                this.loaded = true;
            }
        }
    }

    public /* synthetic */ void lambda$loadHints$69$MediaDataController() {
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
                    TL_topPeer tL_topPeer = new TL_topPeer();
                    tL_topPeer.rating = queryFinalized.doubleValue(2);
                    if (intValue > 0) {
                        tL_topPeer.peer = new TL_peerUser();
                        tL_topPeer.peer.user_id = intValue;
                        arrayList5.add(Integer.valueOf(intValue));
                    } else {
                        tL_topPeer.peer = new TL_peerChat();
                        intValue = -intValue;
                        tL_topPeer.peer.chat_id = intValue;
                        arrayList6.add(Integer.valueOf(intValue));
                    }
                    if (intValue2 == 0) {
                        arrayList.add(tL_topPeer);
                    } else if (intValue2 == 1) {
                        arrayList2.add(tL_topPeer);
                    }
                }
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(str, arrayList5), arrayList3);
            }
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(str, arrayList6), arrayList4);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$Lh0_mvXCj3lCgZ0TDRqDYgU27ks(this, arrayList3, arrayList4, arrayList, arrayList2));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$68$MediaDataController(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
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

    public /* synthetic */ void lambda$loadHints$74$MediaDataController(TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$-CItU-_Bjd3-WnGQQbYImK25WqA(this, tLObject));
        } else if (tLObject instanceof TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$zLrXMhF5QPc_9pJF7qSI9b3mSkY(this));
        }
    }

    public /* synthetic */ void lambda$null$72$MediaDataController(TLObject tLObject) {
        TL_contacts_topPeers tL_contacts_topPeers = (TL_contacts_topPeers) tLObject;
        getMessagesController().putUsers(tL_contacts_topPeers.users, false);
        getMessagesController().putChats(tL_contacts_topPeers.chats, false);
        for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
            TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
            if (tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline) {
                this.inlineBots = tL_topPeerCategoryPeers.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = tL_topPeerCategoryPeers.peers;
                int clientUserId = getUserConfig().getClientUserId();
                for (int i2 = 0; i2 < this.hints.size(); i2++) {
                    if (((TL_topPeer) this.hints.get(i2)).peer.user_id == clientUserId) {
                        this.hints.remove(i2);
                        break;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$4O0tWROzWKd6bpJkkIr2vA6-tg8(this, tL_contacts_topPeers));
    }

    public /* synthetic */ void lambda$null$71$MediaDataController(TL_contacts_topPeers tL_contacts_topPeers) {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(tL_contacts_topPeers.users, tL_contacts_topPeers.chats, false, false);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
                TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
                int i2 = tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline ? 1 : 0;
                for (int i3 = 0; i3 < tL_topPeerCategoryPeers.peers.size(); i3++) {
                    int i4;
                    TL_topPeer tL_topPeer = (TL_topPeer) tL_topPeerCategoryPeers.peers.get(i3);
                    if (tL_topPeer.peer instanceof TL_peerUser) {
                        i4 = tL_topPeer.peer.user_id;
                    } else {
                        if (tL_topPeer.peer instanceof TL_peerChat) {
                            i4 = tL_topPeer.peer.chat_id;
                        } else {
                            i4 = tL_topPeer.peer.channel_id;
                        }
                        i4 = -i4;
                    }
                    executeFast.requery();
                    executeFast.bindInteger(1, i4);
                    executeFast.bindInteger(2, i2);
                    executeFast.bindDouble(3, tL_topPeer.rating);
                    executeFast.bindInteger(4, 0);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$DjHOlcZIuNe7KboBgzg5dpj4cuY(this));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$70$MediaDataController() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    public /* synthetic */ void lambda$null$73$MediaDataController() {
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
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$Y4SeKR73Z5v0oIPpo0XlKlQLyhs(this));
        buildShortcuts();
    }

    public /* synthetic */ void lambda$clearTopPeers$75$MediaDataController() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void increaseInlineRaiting(int i) {
        if (getUserConfig().suggestContacts) {
            int max = getUserConfig().botRatingLoadTime != 0 ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime) : 60;
            TL_topPeer tL_topPeer = null;
            for (int i2 = 0; i2 < this.inlineBots.size(); i2++) {
                TL_topPeer tL_topPeer2 = (TL_topPeer) this.inlineBots.get(i2);
                if (tL_topPeer2.peer.user_id == i) {
                    tL_topPeer = tL_topPeer2;
                    break;
                }
            }
            if (tL_topPeer == null) {
                tL_topPeer = new TL_topPeer();
                tL_topPeer.peer = new TL_peerUser();
                tL_topPeer.peer.user_id = i;
                this.inlineBots.add(tL_topPeer);
            }
            tL_topPeer.rating += Math.exp((double) (max / getMessagesController().ratingDecay));
            Collections.sort(this.inlineBots, -$$Lambda$MediaDataController$Ix3S2WSM5PVeYOJqIfv9R6zCLASSNAME.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(i, 1, tL_topPeer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    static /* synthetic */ int lambda$increaseInlineRaiting$76(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
        double d = tL_topPeer.rating;
        double d2 = tL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    public void removeInline(int i) {
        for (int i2 = 0; i2 < this.inlineBots.size(); i2++) {
            if (((TL_topPeer) this.inlineBots.get(i2)).peer.user_id == i) {
                this.inlineBots.remove(i2);
                TL_contacts_resetTopPeerRating tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryBotsInline();
                tL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(i);
                getConnectionsManager().sendRequest(tL_contacts_resetTopPeerRating, -$$Lambda$MediaDataController$OKX9THSE0o_e7a3sruFE-v3ZCLASSNAME.INSTANCE);
                deletePeer(i, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public void removePeer(int i) {
        for (int i2 = 0; i2 < this.hints.size(); i2++) {
            if (((TL_topPeer) this.hints.get(i2)).peer.user_id == i) {
                this.hints.remove(i2);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TL_contacts_resetTopPeerRating tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryCorrespondents();
                tL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(i);
                deletePeer(i, 0);
                getConnectionsManager().sendRequest(tL_contacts_resetTopPeerRating, -$$Lambda$MediaDataController$hoQxeftXfU0Ldj2neygp1aCSDyk.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(long j) {
        if (getUserConfig().suggestContacts) {
            int i = (int) j;
            if (i > 0) {
                User user = i > 0 ? getMessagesController().getUser(Integer.valueOf(i)) : null;
                if (!(user == null || user.bot || user.self)) {
                    getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$U40oWY_M12zJ1QUCV3WQFTSPUUY(this, j, i));
                }
            }
        }
    }

    public /* synthetic */ void lambda$increasePeerRaiting$81$MediaDataController(long j, int i) {
        double d = 0.0d;
        try {
            int intValue;
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            Object[] objArr = new Object[1];
            int i2 = 0;
            objArr[0] = Long.valueOf(j);
            SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", objArr), new Object[0]);
            if (queryFinalized.next()) {
                i2 = queryFinalized.intValue(0);
                intValue = queryFinalized.intValue(1);
            } else {
                intValue = 0;
            }
            queryFinalized.dispose();
            if (i2 > 0 && getUserConfig().ratingLoadTime != 0) {
                d = (double) (intValue - getUserConfig().ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$q95AYVcQHR4rYoul6CUTRUronfk(this, i, d, j));
    }

    public /* synthetic */ void lambda$null$80$MediaDataController(int i, double d, long j) {
        TL_topPeer tL_topPeer;
        for (int i2 = 0; i2 < this.hints.size(); i2++) {
            tL_topPeer = (TL_topPeer) this.hints.get(i2);
            if (i < 0) {
                Peer peer = tL_topPeer.peer;
                int i3 = -i;
                if (peer.chat_id != i3) {
                    if (peer.channel_id == i3) {
                        break;
                    }
                }
                break;
            }
            if (i > 0 && tL_topPeer.peer.user_id == i) {
                break;
            }
        }
        tL_topPeer = null;
        if (tL_topPeer == null) {
            tL_topPeer = new TL_topPeer();
            if (i > 0) {
                tL_topPeer.peer = new TL_peerUser();
                tL_topPeer.peer.user_id = i;
            } else {
                tL_topPeer.peer = new TL_peerChat();
                tL_topPeer.peer.chat_id = -i;
            }
            this.hints.add(tL_topPeer);
        }
        double d2 = tL_topPeer.rating;
        double d3 = (double) getMessagesController().ratingDecay;
        Double.isNaN(d3);
        tL_topPeer.rating = d2 + Math.exp(d / d3);
        Collections.sort(this.hints, -$$Lambda$MediaDataController$6QYcz7t0we-TGCvQPVF0csq9GDY.INSTANCE);
        savePeer((int) j, 0, tL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    static /* synthetic */ int lambda$null$79(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
        double d = tL_topPeer.rating;
        double d2 = tL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    private void savePeer(int i, int i2, double d) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$JeIEochBdz3oFR70kj105K78jHs(this, i, i2, d));
    }

    public /* synthetic */ void lambda$savePeer$82$MediaDataController(int i, int i2, double d) {
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
            FileLog.e(e);
        }
    }

    private void deletePeer(int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$yzlQpqlauOG5CBbg574c4yV3DUs(this, i, i2));
    }

    public /* synthetic */ void lambda$deletePeer$83$MediaDataController(int i, int i2) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("com.tmessages.openchat");
        stringBuilder.append(j);
        intent.setAction(stringBuilder.toString());
        intent.addFlags(67108864);
        return intent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC, Splitter:B:33:0x0094} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cf A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c0 A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC, Splitter:B:33:0x0094} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c0 A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cf A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC, Splitter:B:33:0x0094} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cf A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c0 A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x007c A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0058 A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC, Splitter:B:33:0x0094} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c0 A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00cf A:{Catch:{ Throwable -> 0x014f }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01da A:{Catch:{ Exception -> 0x023d }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0165 A:{Catch:{ Exception -> 0x023d }} */
    public void installShortcut(long r17) {
        /*
        r16 = this;
        r1 = r17;
        r3 = r16.createIntrnalShortcutIntent(r17);	 Catch:{ Exception -> 0x023d }
        r0 = (int) r1;	 Catch:{ Exception -> 0x023d }
        r4 = 32;
        r4 = r1 >> r4;
        r5 = (int) r4;	 Catch:{ Exception -> 0x023d }
        r4 = 0;
        if (r0 != 0) goto L_0x002d;
    L_0x000f:
        r0 = r16.getMessagesController();	 Catch:{ Exception -> 0x023d }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x023d }
        r0 = r0.getEncryptedChat(r5);	 Catch:{ Exception -> 0x023d }
        if (r0 != 0) goto L_0x001e;
    L_0x001d:
        return;
    L_0x001e:
        r5 = r16.getMessagesController();	 Catch:{ Exception -> 0x023d }
        r0 = r0.user_id;	 Catch:{ Exception -> 0x023d }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x023d }
        r0 = r5.getUser(r0);	 Catch:{ Exception -> 0x023d }
        goto L_0x003b;
    L_0x002d:
        if (r0 <= 0) goto L_0x003e;
    L_0x002f:
        r5 = r16.getMessagesController();	 Catch:{ Exception -> 0x023d }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x023d }
        r0 = r5.getUser(r0);	 Catch:{ Exception -> 0x023d }
    L_0x003b:
        r5 = r0;
        r6 = r4;
        goto L_0x004f;
    L_0x003e:
        if (r0 >= 0) goto L_0x023c;
    L_0x0040:
        r5 = r16.getMessagesController();	 Catch:{ Exception -> 0x023d }
        r0 = -r0;
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x023d }
        r0 = r5.getChat(r0);	 Catch:{ Exception -> 0x023d }
        r6 = r0;
        r5 = r4;
    L_0x004f:
        if (r5 != 0) goto L_0x0054;
    L_0x0051:
        if (r6 != 0) goto L_0x0054;
    L_0x0053:
        return;
    L_0x0054:
        r0 = 1;
        r7 = 0;
        if (r5 == 0) goto L_0x007c;
    L_0x0058:
        r8 = org.telegram.messenger.UserObject.isUserSelf(r5);	 Catch:{ Exception -> 0x023d }
        if (r8 == 0) goto L_0x006b;
    L_0x005e:
        r8 = "SavedMessages";
        r9 = NUM; // 0x7f0d0906 float:1.87468E38 double:1.053130919E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r9);	 Catch:{ Exception -> 0x023d }
        r9 = r4;
        r10 = r8;
        r8 = 1;
        goto L_0x008a;
    L_0x006b:
        r8 = r5.first_name;	 Catch:{ Exception -> 0x023d }
        r9 = r5.last_name;	 Catch:{ Exception -> 0x023d }
        r8 = org.telegram.messenger.ContactsController.formatName(r8, r9);	 Catch:{ Exception -> 0x023d }
        r9 = r5.photo;	 Catch:{ Exception -> 0x023d }
        if (r9 == 0) goto L_0x0087;
    L_0x0077:
        r9 = r5.photo;	 Catch:{ Exception -> 0x023d }
        r9 = r9.photo_small;	 Catch:{ Exception -> 0x023d }
        goto L_0x0088;
    L_0x007c:
        r8 = r6.title;	 Catch:{ Exception -> 0x023d }
        r9 = r6.photo;	 Catch:{ Exception -> 0x023d }
        if (r9 == 0) goto L_0x0087;
    L_0x0082:
        r9 = r6.photo;	 Catch:{ Exception -> 0x023d }
        r9 = r9.photo_small;	 Catch:{ Exception -> 0x023d }
        goto L_0x0088;
    L_0x0087:
        r9 = r4;
    L_0x0088:
        r10 = r8;
        r8 = 0;
    L_0x008a:
        if (r8 != 0) goto L_0x0092;
    L_0x008c:
        if (r9 == 0) goto L_0x008f;
    L_0x008e:
        goto L_0x0092;
    L_0x008f:
        r9 = r4;
        goto L_0x0153;
    L_0x0092:
        if (r8 != 0) goto L_0x00a5;
    L_0x0094:
        r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r0);	 Catch:{ Throwable -> 0x00a1 }
        r9 = r9.toString();	 Catch:{ Throwable -> 0x00a1 }
        r9 = android.graphics.BitmapFactory.decodeFile(r9);	 Catch:{ Throwable -> 0x00a1 }
        goto L_0x00a6;
    L_0x00a1:
        r0 = move-exception;
        r9 = r4;
        goto L_0x0150;
    L_0x00a5:
        r9 = r4;
    L_0x00a6:
        if (r8 != 0) goto L_0x00aa;
    L_0x00a8:
        if (r9 == 0) goto L_0x0153;
    L_0x00aa:
        r11 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Throwable -> 0x014f }
        r12 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x014f }
        r12 = android.graphics.Bitmap.createBitmap(r11, r11, r12);	 Catch:{ Throwable -> 0x014f }
        r12.eraseColor(r7);	 Catch:{ Throwable -> 0x014f }
        r13 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x014f }
        r13.<init>(r12);	 Catch:{ Throwable -> 0x014f }
        if (r8 == 0) goto L_0x00cf;
    L_0x00c0:
        r8 = new org.telegram.ui.Components.AvatarDrawable;	 Catch:{ Throwable -> 0x014f }
        r8.<init>(r5);	 Catch:{ Throwable -> 0x014f }
        r8.setAvatarType(r0);	 Catch:{ Throwable -> 0x014f }
        r8.setBounds(r7, r7, r11, r11);	 Catch:{ Throwable -> 0x014f }
        r8.draw(r13);	 Catch:{ Throwable -> 0x014f }
        goto L_0x0120;
    L_0x00cf:
        r8 = new android.graphics.BitmapShader;	 Catch:{ Throwable -> 0x014f }
        r14 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x014f }
        r15 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x014f }
        r8.<init>(r9, r14, r15);	 Catch:{ Throwable -> 0x014f }
        r14 = roundPaint;	 Catch:{ Throwable -> 0x014f }
        if (r14 != 0) goto L_0x00ea;
    L_0x00dc:
        r14 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x014f }
        r14.<init>(r0);	 Catch:{ Throwable -> 0x014f }
        roundPaint = r14;	 Catch:{ Throwable -> 0x014f }
        r0 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x014f }
        r0.<init>();	 Catch:{ Throwable -> 0x014f }
        bitmapRect = r0;	 Catch:{ Throwable -> 0x014f }
    L_0x00ea:
        r0 = (float) r11;	 Catch:{ Throwable -> 0x014f }
        r14 = r9.getWidth();	 Catch:{ Throwable -> 0x014f }
        r14 = (float) r14;	 Catch:{ Throwable -> 0x014f }
        r0 = r0 / r14;
        r13.save();	 Catch:{ Throwable -> 0x014f }
        r13.scale(r0, r0);	 Catch:{ Throwable -> 0x014f }
        r0 = roundPaint;	 Catch:{ Throwable -> 0x014f }
        r0.setShader(r8);	 Catch:{ Throwable -> 0x014f }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x014f }
        r8 = r9.getWidth();	 Catch:{ Throwable -> 0x014f }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x014f }
        r14 = r9.getHeight();	 Catch:{ Throwable -> 0x014f }
        r14 = (float) r14;	 Catch:{ Throwable -> 0x014f }
        r15 = 0;
        r0.set(r15, r15, r8, r14);	 Catch:{ Throwable -> 0x014f }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x014f }
        r8 = r9.getWidth();	 Catch:{ Throwable -> 0x014f }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x014f }
        r14 = r9.getHeight();	 Catch:{ Throwable -> 0x014f }
        r14 = (float) r14;	 Catch:{ Throwable -> 0x014f }
        r15 = roundPaint;	 Catch:{ Throwable -> 0x014f }
        r13.drawRoundRect(r0, r8, r14, r15);	 Catch:{ Throwable -> 0x014f }
        r13.restore();	 Catch:{ Throwable -> 0x014f }
    L_0x0120:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x014f }
        r0 = r0.getResources();	 Catch:{ Throwable -> 0x014f }
        r8 = NUM; // 0x7var_e float:1.7944671E38 double:1.052935526E-314;
        r0 = r0.getDrawable(r8);	 Catch:{ Throwable -> 0x014f }
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x014f }
        r11 = r11 - r8;
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r14);	 Catch:{ Throwable -> 0x014f }
        r15 = r11 - r15;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);	 Catch:{ Throwable -> 0x014f }
        r11 = r11 - r14;
        r14 = r15 + r8;
        r8 = r8 + r11;
        r0.setBounds(r15, r11, r14, r8);	 Catch:{ Throwable -> 0x014f }
        r0.draw(r13);	 Catch:{ Throwable -> 0x014f }
        r13.setBitmap(r4);	 Catch:{ Exception -> 0x014d }
    L_0x014d:
        r9 = r12;
        goto L_0x0153;
    L_0x014f:
        r0 = move-exception;
    L_0x0150:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x023d }
    L_0x0153:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x023d }
        r8 = 26;
        r11 = NUM; // 0x7var_c float:1.7944667E38 double:1.052935525E-314;
        r12 = NUM; // 0x7var_d float:1.794467E38 double:1.0529355253E-314;
        r13 = NUM; // 0x7var_b float:1.7944665E38 double:1.0529355243E-314;
        r14 = NUM; // 0x7var_f float:1.7944673E38 double:1.0529355262E-314;
        if (r0 < r8) goto L_0x01da;
    L_0x0165:
        r0 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Exception -> 0x023d }
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x023d }
        r8.<init>();	 Catch:{ Exception -> 0x023d }
        r15 = "sdid_";
        r8.append(r15);	 Catch:{ Exception -> 0x023d }
        r8.append(r1);	 Catch:{ Exception -> 0x023d }
        r1 = r8.toString();	 Catch:{ Exception -> 0x023d }
        r0.<init>(r7, r1);	 Catch:{ Exception -> 0x023d }
        r0 = r0.setShortLabel(r10);	 Catch:{ Exception -> 0x023d }
        r0 = r0.setIntent(r3);	 Catch:{ Exception -> 0x023d }
        if (r9 == 0) goto L_0x018f;
    L_0x0187:
        r1 = android.graphics.drawable.Icon.createWithBitmap(r9);	 Catch:{ Exception -> 0x023d }
        r0.setIcon(r1);	 Catch:{ Exception -> 0x023d }
        goto L_0x01c8;
    L_0x018f:
        if (r5 == 0) goto L_0x01a9;
    L_0x0191:
        r1 = r5.bot;	 Catch:{ Exception -> 0x023d }
        if (r1 == 0) goto L_0x019f;
    L_0x0195:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r1 = android.graphics.drawable.Icon.createWithResource(r1, r13);	 Catch:{ Exception -> 0x023d }
        r0.setIcon(r1);	 Catch:{ Exception -> 0x023d }
        goto L_0x01c8;
    L_0x019f:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r1 = android.graphics.drawable.Icon.createWithResource(r1, r14);	 Catch:{ Exception -> 0x023d }
        r0.setIcon(r1);	 Catch:{ Exception -> 0x023d }
        goto L_0x01c8;
    L_0x01a9:
        if (r6 == 0) goto L_0x01c8;
    L_0x01ab:
        r1 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x023d }
        if (r1 == 0) goto L_0x01bf;
    L_0x01b1:
        r1 = r6.megagroup;	 Catch:{ Exception -> 0x023d }
        if (r1 != 0) goto L_0x01bf;
    L_0x01b5:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r1 = android.graphics.drawable.Icon.createWithResource(r1, r11);	 Catch:{ Exception -> 0x023d }
        r0.setIcon(r1);	 Catch:{ Exception -> 0x023d }
        goto L_0x01c8;
    L_0x01bf:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r1 = android.graphics.drawable.Icon.createWithResource(r1, r12);	 Catch:{ Exception -> 0x023d }
        r0.setIcon(r1);	 Catch:{ Exception -> 0x023d }
    L_0x01c8:
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r2 = android.content.pm.ShortcutManager.class;
        r1 = r1.getSystemService(r2);	 Catch:{ Exception -> 0x023d }
        r1 = (android.content.pm.ShortcutManager) r1;	 Catch:{ Exception -> 0x023d }
        r0 = r0.build();	 Catch:{ Exception -> 0x023d }
        r1.requestPinShortcut(r0, r4);	 Catch:{ Exception -> 0x023d }
        goto L_0x0241;
    L_0x01da:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x023d }
        r0.<init>();	 Catch:{ Exception -> 0x023d }
        if (r9 == 0) goto L_0x01e7;
    L_0x01e1:
        r1 = "android.intent.extra.shortcut.ICON";
        r0.putExtra(r1, r9);	 Catch:{ Exception -> 0x023d }
        goto L_0x0222;
    L_0x01e7:
        r1 = "android.intent.extra.shortcut.ICON_RESOURCE";
        if (r5 == 0) goto L_0x0203;
    L_0x01eb:
        r2 = r5.bot;	 Catch:{ Exception -> 0x023d }
        if (r2 == 0) goto L_0x01f9;
    L_0x01ef:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r13);	 Catch:{ Exception -> 0x023d }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x023d }
        goto L_0x0222;
    L_0x01f9:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r14);	 Catch:{ Exception -> 0x023d }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x023d }
        goto L_0x0222;
    L_0x0203:
        if (r6 == 0) goto L_0x0222;
    L_0x0205:
        r2 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x023d }
        if (r2 == 0) goto L_0x0219;
    L_0x020b:
        r2 = r6.megagroup;	 Catch:{ Exception -> 0x023d }
        if (r2 != 0) goto L_0x0219;
    L_0x020f:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r11);	 Catch:{ Exception -> 0x023d }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x023d }
        goto L_0x0222;
    L_0x0219:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r12);	 Catch:{ Exception -> 0x023d }
        r0.putExtra(r1, r2);	 Catch:{ Exception -> 0x023d }
    L_0x0222:
        r1 = "android.intent.extra.shortcut.INTENT";
        r0.putExtra(r1, r3);	 Catch:{ Exception -> 0x023d }
        r1 = "android.intent.extra.shortcut.NAME";
        r0.putExtra(r1, r10);	 Catch:{ Exception -> 0x023d }
        r1 = "duplicate";
        r0.putExtra(r1, r7);	 Catch:{ Exception -> 0x023d }
        r1 = "com.android.launcher.action.INSTALL_SHORTCUT";
        r0.setAction(r1);	 Catch:{ Exception -> 0x023d }
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x023d }
        r1.sendBroadcast(r0);	 Catch:{ Exception -> 0x023d }
        goto L_0x0241;
    L_0x023c:
        return;
    L_0x023d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0241:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    public void uninstallShortcut(long j) {
        try {
            if (VERSION.SDK_INT >= 26) {
                ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                ArrayList arrayList = new ArrayList();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("sdid_");
                stringBuilder.append(j);
                arrayList.add(stringBuilder.toString());
                shortcutManager.removeDynamicShortcuts(arrayList);
            } else {
                User user;
                int i = (int) j;
                int i2 = (int) (j >> 32);
                Chat chat = null;
                if (i == 0) {
                    EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(i2));
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
                    String formatName;
                    if (user != null) {
                        formatName = ContactsController.formatName(user.first_name, user.last_name);
                    } else {
                        formatName = chat.title;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("android.intent.extra.shortcut.INTENT", createIntrnalShortcutIntent(j));
                    intent.putExtra("android.intent.extra.shortcut.NAME", formatName);
                    intent.putExtra("duplicate", false);
                    intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ int lambda$static$84(MessageEntity messageEntity, MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public /* synthetic */ void lambda$loadPinnedMessage$85$MediaDataController(long j, int i, int i2) {
        loadPinnedMessageInternal(j, i, i2, false);
    }

    public MessageObject loadPinnedMessage(long j, int i, int i2, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(j, i, i2, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$qRE9jwnC3Rt933jdgs8ETSLXQmQ(this, j, i, i2));
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0087 A:{Catch:{ Exception -> 0x015d }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x011e A:{Catch:{ Exception -> 0x015d }} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00dc A:{Catch:{ Exception -> 0x015d }} */
    private org.telegram.messenger.MessageObject loadPinnedMessageInternal(long r17, int r19, int r20, boolean r21) {
        /*
        r16 = this;
        r7 = r16;
        r0 = r17;
        r2 = r19;
        r3 = r20;
        if (r2 == 0) goto L_0x0011;
    L_0x000a:
        r4 = (long) r3;
        r8 = (long) r2;
        r6 = 32;
        r8 = r8 << r6;
        r4 = r4 | r8;
        goto L_0x0012;
    L_0x0011:
        r4 = (long) r3;
    L_0x0012:
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x015d }
        r6.<init>();	 Catch:{ Exception -> 0x015d }
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x015d }
        r9.<init>();	 Catch:{ Exception -> 0x015d }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x015d }
        r10.<init>();	 Catch:{ Exception -> 0x015d }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x015d }
        r11.<init>();	 Catch:{ Exception -> 0x015d }
        r12 = r16.getMessagesStorage();	 Catch:{ Exception -> 0x015d }
        r12 = r12.getDatabase();	 Catch:{ Exception -> 0x015d }
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x015d }
        r14 = "SELECT data, mid, date FROM messages WHERE mid = %d";
        r15 = 1;
        r8 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x015d }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x015d }
        r5 = 0;
        r8[r5] = r4;	 Catch:{ Exception -> 0x015d }
        r4 = java.lang.String.format(r13, r14, r8);	 Catch:{ Exception -> 0x015d }
        r8 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x015d }
        r4 = r12.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x015d }
        r8 = r4.next();	 Catch:{ Exception -> 0x015d }
        if (r8 == 0) goto L_0x0081;
    L_0x004c:
        r8 = r4.byteBufferValue(r5);	 Catch:{ Exception -> 0x015d }
        if (r8 == 0) goto L_0x0081;
    L_0x0052:
        r12 = r8.readInt32(r5);	 Catch:{ Exception -> 0x015d }
        r12 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r12, r5);	 Catch:{ Exception -> 0x015d }
        r13 = r16.getUserConfig();	 Catch:{ Exception -> 0x015d }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x015d }
        r12.readAttachPath(r8, r13);	 Catch:{ Exception -> 0x015d }
        r8.reuse();	 Catch:{ Exception -> 0x015d }
        r8 = r12.action;	 Catch:{ Exception -> 0x015d }
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x015d }
        if (r8 == 0) goto L_0x006d;
    L_0x006c:
        goto L_0x0081;
    L_0x006d:
        r8 = r4.intValue(r15);	 Catch:{ Exception -> 0x015d }
        r12.id = r8;	 Catch:{ Exception -> 0x015d }
        r8 = 2;
        r8 = r4.intValue(r8);	 Catch:{ Exception -> 0x015d }
        r12.date = r8;	 Catch:{ Exception -> 0x015d }
        r12.dialog_id = r0;	 Catch:{ Exception -> 0x015d }
        org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r12, r10, r11);	 Catch:{ Exception -> 0x015d }
        r8 = r12;
        goto L_0x0082;
    L_0x0081:
        r8 = 0;
    L_0x0082:
        r4.dispose();	 Catch:{ Exception -> 0x015d }
        if (r8 != 0) goto L_0x00da;
    L_0x0087:
        r4 = r16.getMessagesStorage();	 Catch:{ Exception -> 0x015d }
        r4 = r4.getDatabase();	 Catch:{ Exception -> 0x015d }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x015d }
        r13 = "SELECT data FROM chat_pinned WHERE uid = %d";
        r14 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x015d }
        r15 = java.lang.Long.valueOf(r17);	 Catch:{ Exception -> 0x015d }
        r14[r5] = r15;	 Catch:{ Exception -> 0x015d }
        r12 = java.lang.String.format(r12, r13, r14);	 Catch:{ Exception -> 0x015d }
        r13 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x015d }
        r4 = r4.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x015d }
        r12 = r4.next();	 Catch:{ Exception -> 0x015d }
        if (r12 == 0) goto L_0x00d7;
    L_0x00ab:
        r12 = r4.byteBufferValue(r5);	 Catch:{ Exception -> 0x015d }
        if (r12 == 0) goto L_0x00d7;
    L_0x00b1:
        r8 = r12.readInt32(r5);	 Catch:{ Exception -> 0x015d }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r12, r8, r5);	 Catch:{ Exception -> 0x015d }
        r5 = r16.getUserConfig();	 Catch:{ Exception -> 0x015d }
        r5 = r5.clientUserId;	 Catch:{ Exception -> 0x015d }
        r8.readAttachPath(r12, r5);	 Catch:{ Exception -> 0x015d }
        r12.reuse();	 Catch:{ Exception -> 0x015d }
        r5 = r8.id;	 Catch:{ Exception -> 0x015d }
        if (r5 != r3) goto L_0x00d6;
    L_0x00c9:
        r5 = r8.action;	 Catch:{ Exception -> 0x015d }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x015d }
        if (r5 == 0) goto L_0x00d0;
    L_0x00cf:
        goto L_0x00d6;
    L_0x00d0:
        r8.dialog_id = r0;	 Catch:{ Exception -> 0x015d }
        org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r10, r11);	 Catch:{ Exception -> 0x015d }
        goto L_0x00d7;
    L_0x00d6:
        r8 = 0;
    L_0x00d7:
        r4.dispose();	 Catch:{ Exception -> 0x015d }
    L_0x00da:
        if (r8 != 0) goto L_0x011e;
    L_0x00dc:
        if (r2 == 0) goto L_0x0103;
    L_0x00de:
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages;	 Catch:{ Exception -> 0x015d }
        r0.<init>();	 Catch:{ Exception -> 0x015d }
        r1 = r16.getMessagesController();	 Catch:{ Exception -> 0x015d }
        r1 = r1.getInputChannel(r2);	 Catch:{ Exception -> 0x015d }
        r0.channel = r1;	 Catch:{ Exception -> 0x015d }
        r1 = r0.id;	 Catch:{ Exception -> 0x015d }
        r3 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x015d }
        r1.add(r3);	 Catch:{ Exception -> 0x015d }
        r1 = r16.getConnectionsManager();	 Catch:{ Exception -> 0x015d }
        r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$8vcE3TLTS3fatjLL7LvYXCLASSNAMEW6I;	 Catch:{ Exception -> 0x015d }
        r3.<init>(r7, r2);	 Catch:{ Exception -> 0x015d }
        r1.sendRequest(r0, r3);	 Catch:{ Exception -> 0x015d }
        goto L_0x0161;
    L_0x0103:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages;	 Catch:{ Exception -> 0x015d }
        r0.<init>();	 Catch:{ Exception -> 0x015d }
        r1 = r0.id;	 Catch:{ Exception -> 0x015d }
        r3 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x015d }
        r1.add(r3);	 Catch:{ Exception -> 0x015d }
        r1 = r16.getConnectionsManager();	 Catch:{ Exception -> 0x015d }
        r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$xypn_j30rtu8EgMQpfPjb3Vslx0;	 Catch:{ Exception -> 0x015d }
        r3.<init>(r7, r2);	 Catch:{ Exception -> 0x015d }
        r1.sendRequest(r0, r3);	 Catch:{ Exception -> 0x015d }
        goto L_0x0161;
    L_0x011e:
        if (r21 == 0) goto L_0x012d;
    L_0x0120:
        r5 = 1;
        r1 = r16;
        r2 = r8;
        r3 = r6;
        r4 = r9;
        r6 = r21;
        r0 = r1.broadcastPinnedMessage(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x015d }
        return r0;
    L_0x012d:
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x015d }
        r1 = ",";
        if (r0 != 0) goto L_0x0140;
    L_0x0135:
        r0 = r16.getMessagesStorage();	 Catch:{ Exception -> 0x015d }
        r2 = android.text.TextUtils.join(r1, r10);	 Catch:{ Exception -> 0x015d }
        r0.getUsersInternal(r2, r6);	 Catch:{ Exception -> 0x015d }
    L_0x0140:
        r0 = r11.isEmpty();	 Catch:{ Exception -> 0x015d }
        if (r0 != 0) goto L_0x0151;
    L_0x0146:
        r0 = r16.getMessagesStorage();	 Catch:{ Exception -> 0x015d }
        r1 = android.text.TextUtils.join(r1, r11);	 Catch:{ Exception -> 0x015d }
        r0.getChatsInternal(r1, r9);	 Catch:{ Exception -> 0x015d }
    L_0x0151:
        r5 = 1;
        r0 = 0;
        r1 = r16;
        r2 = r8;
        r3 = r6;
        r4 = r9;
        r6 = r0;
        r1.broadcastPinnedMessage(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x015d }
        goto L_0x0161;
    L_0x015d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0161:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, int, int, boolean):org.telegram.messenger.MessageObject");
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0045  */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$86$MediaDataController(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        if (r11 != 0) goto L_0x0042;
    L_0x0004:
        r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10;
        r11 = r10.messages;
        removeEmptyMessages(r11);
        r11 = r10.messages;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x0042;
    L_0x0013:
        r11 = r10.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r11);
        r11 = r10.messages;
        r11 = r11.get(r1);
        r3 = r11;
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;
        r4 = r10.users;
        r5 = r10.chats;
        r6 = 0;
        r7 = 0;
        r2 = r8;
        r2.broadcastPinnedMessage(r3, r4, r5, r6, r7);
        r11 = r8.getMessagesStorage();
        r2 = r10.users;
        r3 = r10.chats;
        r11.putUsersAndChats(r2, r3, r0, r0);
        r10 = r10.messages;
        r10 = r10.get(r1);
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;
        r8.savePinnedMessage(r10);
        goto L_0x0043;
    L_0x0042:
        r0 = 0;
    L_0x0043:
        if (r0 != 0) goto L_0x004c;
    L_0x0045:
        r10 = r8.getMessagesStorage();
        r10.updateChatPinnedMessage(r9, r1);
    L_0x004c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$86$MediaDataController(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0045  */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$87$MediaDataController(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        if (r11 != 0) goto L_0x0042;
    L_0x0004:
        r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10;
        r11 = r10.messages;
        removeEmptyMessages(r11);
        r11 = r10.messages;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x0042;
    L_0x0013:
        r11 = r10.messages;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r11);
        r11 = r10.messages;
        r11 = r11.get(r1);
        r3 = r11;
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;
        r4 = r10.users;
        r5 = r10.chats;
        r6 = 0;
        r7 = 0;
        r2 = r8;
        r2.broadcastPinnedMessage(r3, r4, r5, r6, r7);
        r11 = r8.getMessagesStorage();
        r2 = r10.users;
        r3 = r10.chats;
        r11.putUsersAndChats(r2, r3, r0, r0);
        r10 = r10.messages;
        r10 = r10.get(r1);
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;
        r8.savePinnedMessage(r10);
        goto L_0x0043;
    L_0x0042:
        r0 = 0;
    L_0x0043:
        if (r0 != 0) goto L_0x004c;
    L_0x0045:
        r10 = r8.getMessagesStorage();
        r10.updateChatPinnedMessage(r9, r1);
    L_0x004c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$87$MediaDataController(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessage(Message message) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$-vPPlS_wLkifmDj_Dyo0bXEGOBQ(this, message));
    }

    public /* synthetic */ void lambda$savePinnedMessage$88$MediaDataController(Message message) {
        try {
            int i;
            long j;
            SQLitePreparedStatement executeFast;
            NativeByteBuffer nativeByteBuffer;
            if (message.to_id.channel_id != 0) {
                i = message.to_id.channel_id;
            } else if (message.to_id.chat_id != 0) {
                i = message.to_id.chat_id;
            } else if (message.to_id.user_id != 0) {
                i = message.to_id.user_id;
                j = (long) i;
                getMessagesStorage().getDatabase().beginTransaction();
                executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
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
            i = -i;
            j = (long) i;
            getMessagesStorage().getDatabase().beginTransaction();
            executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
            nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
            message.serializeToStream(nativeByteBuffer);
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, message.id);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private MessageObject broadcastPinnedMessage(Message message, ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        SparseArray sparseArray = new SparseArray();
        for (int i = 0; i < arrayList.size(); i++) {
            User user = (User) arrayList.get(i);
            sparseArray.put(user.id, user);
        }
        SparseArray sparseArray2 = new SparseArray();
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            Chat chat = (Chat) arrayList2.get(i2);
            sparseArray2.put(chat.id, chat);
        }
        if (z2) {
            return new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$pcBlpdEhtHgoAATkN4N7oRpDpr0(this, arrayList, z, arrayList2, message, sparseArray, sparseArray2));
        return null;
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$89$MediaDataController(ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, SparseArray sparseArray, SparseArray sparseArray2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
        getNotificationCenter().postNotificationName(NotificationCenter.pinnedMessageDidLoad, new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false));
    }

    private static void removeEmptyMessages(ArrayList<Message> arrayList) {
        int i = 0;
        while (i < arrayList.size()) {
            Message message = (Message) arrayList.get(i);
            if (message == null || (message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
    }

    public void loadReplyMessagesForMessages(ArrayList<MessageObject> arrayList, long j) {
        ArrayList<MessageObject> arrayList2 = arrayList;
        int i = 0;
        ArrayList arrayList3;
        MessageObject messageObject;
        ArrayList arrayList4;
        if (((int) j) == 0) {
            arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            while (i < arrayList.size()) {
                messageObject = (MessageObject) arrayList2.get(i);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long j2 = messageObject.messageOwner.reply_to_random_id;
                    arrayList4 = (ArrayList) longSparseArray.get(j2);
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
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$GhkMRygeqygXEtDpEPcIgj27Hs4(this, arrayList3, j, longSparseArray));
            } else {
                return;
            }
        }
        arrayList3 = new ArrayList();
        SparseArray sparseArray = new SparseArray();
        StringBuilder stringBuilder = new StringBuilder();
        int i2 = 0;
        while (i < arrayList.size()) {
            messageObject = (MessageObject) arrayList2.get(i);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                Message message = messageObject.messageOwner;
                int i3 = message.reply_to_msg_id;
                long j3 = (long) i3;
                int i4 = message.to_id.channel_id;
                if (i4 != 0) {
                    j3 |= ((long) i4) << 32;
                    i2 = i4;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(j3);
                arrayList4 = (ArrayList) sparseArray.get(i3);
                if (arrayList4 == null) {
                    arrayList4 = new ArrayList();
                    sparseArray.put(i3, arrayList4);
                }
                arrayList4.add(messageObject);
                if (!arrayList3.contains(Integer.valueOf(i3))) {
                    arrayList3.add(Integer.valueOf(i3));
                }
            }
            i++;
        }
        if (!arrayList3.isEmpty()) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$t1SIUeXEG8K5DX-UBfPa4nFCbQs(this, stringBuilder, j, arrayList3, sparseArray, i2));
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$91$MediaDataController(ArrayList arrayList, long j, LongSparseArray longSparseArray) {
        try {
            ArrayList arrayList2;
            int i;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j;
                    long longValue = queryFinalized.longValue(3);
                    arrayList2 = (ArrayList) longSparseArray.get(longValue);
                    longSparseArray.remove(longValue);
                    if (arrayList2 != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false);
                        for (i = 0; i < arrayList2.size(); i++) {
                            MessageObject messageObject2 = (MessageObject) arrayList2.get(i);
                            messageObject2.replyMessageObject = messageObject;
                            messageObject2.messageOwner.reply_to_msg_id = messageObject.getId();
                            if (messageObject2.isMegagroup()) {
                                Message message = messageObject2.replyMessageObject.messageOwner;
                                message.flags |= Integer.MIN_VALUE;
                            }
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (longSparseArray.size() != 0) {
                for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                    arrayList2 = (ArrayList) longSparseArray.valueAt(i2);
                    for (i = 0; i < arrayList2.size(); i++) {
                        ((MessageObject) arrayList2.get(i)).messageOwner.reply_to_random_id = 0;
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$fy0-gIpkNLZ5bQs3X-GK-JdX5yE(this, j));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$90$MediaDataController(long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$94$MediaDataController(StringBuilder stringBuilder, long j, ArrayList arrayList, SparseArray sparseArray, int i) {
        long j2 = j;
        ArrayList arrayList2 = arrayList;
        SparseArray sparseArray2 = sparseArray;
        int i2 = i;
        try {
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            ArrayList arrayList7 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j2;
                    MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList6, arrayList7);
                    arrayList3.add(TLdeserialize);
                    arrayList2.remove(Integer.valueOf(TLdeserialize.id));
                }
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(str, arrayList6), arrayList4);
            }
            if (!arrayList7.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(str, arrayList7), arrayList5);
            }
            broadcastReplyMessages(arrayList3, sparseArray, arrayList4, arrayList5, j, true);
            if (!arrayList.isEmpty()) {
                if (i2 != 0) {
                    TL_channels_getMessages tL_channels_getMessages = new TL_channels_getMessages();
                    tL_channels_getMessages.channel = getMessagesController().getInputChannel(i2);
                    tL_channels_getMessages.id = arrayList2;
                    getConnectionsManager().sendRequest(tL_channels_getMessages, new -$$Lambda$MediaDataController$whs4WdwKRbQz6dEqFt1pMkgf5Rg(this, sparseArray2, j2));
                    return;
                }
                TL_messages_getMessages tL_messages_getMessages = new TL_messages_getMessages();
                tL_messages_getMessages.id = arrayList2;
                getConnectionsManager().sendRequest(tL_messages_getMessages, new -$$Lambda$MediaDataController$u0z2XLLG2tZx9_OXN7ahaYrTG08(this, sparseArray2, j2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$92$MediaDataController(SparseArray sparseArray, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeEmptyMessages(messages_messages.messages);
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages);
        }
    }

    public /* synthetic */ void lambda$null$93$MediaDataController(SparseArray sparseArray, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeEmptyMessages(messages_messages.messages);
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages);
        }
    }

    private void saveReplyMessages(SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<Message> arrayList) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$rguewHESQirBaFG1ffD9rH0HBc8(this, arrayList, sparseArray));
    }

    public /* synthetic */ void lambda$saveReplyMessages$95$MediaDataController(ArrayList arrayList, SparseArray sparseArray) {
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            for (int i = 0; i < arrayList.size(); i++) {
                Message message = (Message) arrayList.get(i);
                ArrayList arrayList2 = (ArrayList) sparseArray.get(message.id);
                if (arrayList2 != null) {
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                        MessageObject messageObject = (MessageObject) arrayList2.get(i2);
                        executeFast.requery();
                        long id = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        executeFast.bindByteBuffer(1, nativeByteBuffer);
                        executeFast.bindLong(2, id);
                        executeFast.step();
                    }
                    nativeByteBuffer.reuse();
                }
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void broadcastReplyMessages(ArrayList<Message> arrayList, SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, long j, boolean z) {
        ArrayList<User> arrayList4;
        SparseArray sparseArray2 = new SparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            arrayList4 = arrayList2;
            User user = (User) arrayList2.get(i);
            sparseArray2.put(user.id, user);
        }
        arrayList4 = arrayList2;
        SparseArray sparseArray3 = new SparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            Chat chat = (Chat) arrayList3.get(i2);
            sparseArray3.put(chat.id, chat);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$RTm4GdjMFyXBgVF-KVcmzFr4Fw8(this, arrayList2, z, arrayList3, arrayList, sparseArray, sparseArray2, sparseArray3, j));
    }

    public /* synthetic */ void lambda$broadcastReplyMessages$96$MediaDataController(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3, long j) {
        boolean z2 = z;
        ArrayList arrayList4 = arrayList;
        getMessagesController().putUsers(arrayList, z2);
        getMessagesController().putChats(arrayList2, z2);
        Object obj = null;
        for (int i = 0; i < arrayList3.size(); i++) {
            Message message = (Message) arrayList3.get(i);
            ArrayList arrayList5 = (ArrayList) sparseArray.get(message.id);
            if (arrayList5 != null) {
                MessageObject messageObject = new MessageObject(this.currentAccount, message, sparseArray2, sparseArray3, false);
                for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                    MessageObject messageObject2 = (MessageObject) arrayList5.get(i2);
                    messageObject2.replyMessageObject = messageObject;
                    MessageAction messageAction = messageObject2.messageOwner.action;
                    if (messageAction instanceof TL_messageActionPinMessage) {
                        messageObject2.generatePinMessageText(null, null);
                    } else if (messageAction instanceof TL_messageActionGameScore) {
                        messageObject2.generateGameMessageText(null);
                    } else if (messageAction instanceof TL_messageActionPaymentSent) {
                        messageObject2.generatePaymentSentMessageText(null);
                    }
                    if (messageObject2.isMegagroup()) {
                        Message message2 = messageObject2.replyMessageObject.messageOwner;
                        message2.flags |= Integer.MIN_VALUE;
                    }
                }
                obj = 1;
            }
        }
        if (obj != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
        }
    }

    public static void sortEntities(ArrayList<MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }

    private static boolean checkInclusion(int i, ArrayList<MessageEntity> arrayList) {
        if (!(arrayList == null || arrayList.isEmpty())) {
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                MessageEntity messageEntity = (MessageEntity) arrayList.get(i2);
                int i3 = messageEntity.offset;
                if (i3 <= i && i3 + messageEntity.length > i) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkIntersection(int i, int i2, ArrayList<MessageEntity> arrayList) {
        if (!(arrayList == null || arrayList.isEmpty())) {
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageEntity messageEntity = (MessageEntity) arrayList.get(i3);
                int i4 = messageEntity.offset;
                if (i4 > i && i4 + messageEntity.length <= i2) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int i, int i2, ArrayList<MessageEntity> arrayList) {
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            MessageEntity messageEntity = (MessageEntity) arrayList.get(i3);
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

    private static CharacterStyle createNewSpan(CharacterStyle characterStyle, TextStyleRun textStyleRun, TextStyleRun textStyleRun2, boolean z) {
        TextStyleRun textStyleRun3 = new TextStyleRun(textStyleRun);
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
        return characterStyle instanceof URLSpanReplacement ? new URLSpanReplacement(((URLSpanReplacement) characterStyle).getURL(), textStyleRun3) : null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x008b A:{Catch:{ Exception -> 0x00b3 }} */
    public static void addStyleToText(org.telegram.ui.Components.TextStyleSpan r11, int r12, int r13, android.text.Spannable r14, boolean r15) {
        /*
        r15 = android.text.style.CharacterStyle.class;
        r15 = r14.getSpans(r12, r13, r15);	 Catch:{ Exception -> 0x00b3 }
        r15 = (android.text.style.CharacterStyle[]) r15;	 Catch:{ Exception -> 0x00b3 }
        r0 = 33;
        if (r15 == 0) goto L_0x00ab;
    L_0x000c:
        r1 = r15.length;	 Catch:{ Exception -> 0x00b3 }
        if (r1 <= 0) goto L_0x00ab;
    L_0x000f:
        r1 = 0;
    L_0x0010:
        r2 = r15.length;	 Catch:{ Exception -> 0x00b3 }
        if (r1 >= r2) goto L_0x00ab;
    L_0x0013:
        r2 = r15[r1];	 Catch:{ Exception -> 0x00b3 }
        if (r11 == 0) goto L_0x001c;
    L_0x0017:
        r3 = r11.getTextStyleRun();	 Catch:{ Exception -> 0x00b3 }
        goto L_0x0021;
    L_0x001c:
        r3 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;	 Catch:{ Exception -> 0x00b3 }
        r3.<init>();	 Catch:{ Exception -> 0x00b3 }
    L_0x0021:
        r4 = r2 instanceof org.telegram.ui.Components.TextStyleSpan;	 Catch:{ Exception -> 0x00b3 }
        if (r4 == 0) goto L_0x002d;
    L_0x0025:
        r4 = r2;
        r4 = (org.telegram.ui.Components.TextStyleSpan) r4;	 Catch:{ Exception -> 0x00b3 }
        r4 = r4.getTextStyleRun();	 Catch:{ Exception -> 0x00b3 }
        goto L_0x003f;
    L_0x002d:
        r4 = r2 instanceof org.telegram.ui.Components.URLSpanReplacement;	 Catch:{ Exception -> 0x00b3 }
        if (r4 == 0) goto L_0x00a7;
    L_0x0031:
        r4 = r2;
        r4 = (org.telegram.ui.Components.URLSpanReplacement) r4;	 Catch:{ Exception -> 0x00b3 }
        r4 = r4.getTextStyleRun();	 Catch:{ Exception -> 0x00b3 }
        if (r4 != 0) goto L_0x003f;
    L_0x003a:
        r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;	 Catch:{ Exception -> 0x00b3 }
        r4.<init>();	 Catch:{ Exception -> 0x00b3 }
    L_0x003f:
        if (r4 != 0) goto L_0x0043;
    L_0x0041:
        goto L_0x00a7;
    L_0x0043:
        r5 = r14.getSpanStart(r2);	 Catch:{ Exception -> 0x00b3 }
        r6 = r14.getSpanEnd(r2);	 Catch:{ Exception -> 0x00b3 }
        r14.removeSpan(r2);	 Catch:{ Exception -> 0x00b3 }
        r7 = 1;
        if (r5 <= r12) goto L_0x006b;
    L_0x0051:
        if (r13 <= r6) goto L_0x006b;
    L_0x0053:
        r2 = createNewSpan(r2, r4, r3, r7);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r2, r5, r6, r0);	 Catch:{ Exception -> 0x00b3 }
        if (r11 == 0) goto L_0x0069;
    L_0x005c:
        r2 = new org.telegram.ui.Components.TextStyleSpan;	 Catch:{ Exception -> 0x00b3 }
        r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;	 Catch:{ Exception -> 0x00b3 }
        r4.<init>(r3);	 Catch:{ Exception -> 0x00b3 }
        r2.<init>(r4);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r2, r6, r13, r0);	 Catch:{ Exception -> 0x00b3 }
    L_0x0069:
        r13 = r5;
        goto L_0x00a7;
    L_0x006b:
        r8 = 0;
        if (r5 > r12) goto L_0x0088;
    L_0x006e:
        if (r5 == r12) goto L_0x0077;
    L_0x0070:
        r9 = createNewSpan(r2, r4, r8, r7);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r9, r5, r12, r0);	 Catch:{ Exception -> 0x00b3 }
    L_0x0077:
        if (r6 <= r12) goto L_0x0088;
    L_0x0079:
        if (r11 == 0) goto L_0x0086;
    L_0x007b:
        r9 = createNewSpan(r2, r4, r3, r7);	 Catch:{ Exception -> 0x00b3 }
        r10 = java.lang.Math.min(r6, r13);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r9, r12, r10, r0);	 Catch:{ Exception -> 0x00b3 }
    L_0x0086:
        r9 = r6;
        goto L_0x0089;
    L_0x0088:
        r9 = r12;
    L_0x0089:
        if (r6 < r13) goto L_0x00a6;
    L_0x008b:
        if (r6 == r13) goto L_0x0094;
    L_0x008d:
        r8 = createNewSpan(r2, r4, r8, r7);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r8, r13, r6, r0);	 Catch:{ Exception -> 0x00b3 }
    L_0x0094:
        if (r13 <= r5) goto L_0x00a6;
    L_0x0096:
        if (r6 > r12) goto L_0x00a6;
    L_0x0098:
        if (r11 == 0) goto L_0x00a5;
    L_0x009a:
        r12 = createNewSpan(r2, r4, r3, r7);	 Catch:{ Exception -> 0x00b3 }
        r13 = java.lang.Math.min(r6, r13);	 Catch:{ Exception -> 0x00b3 }
        r14.setSpan(r12, r5, r13, r0);	 Catch:{ Exception -> 0x00b3 }
    L_0x00a5:
        r13 = r5;
    L_0x00a6:
        r12 = r9;
    L_0x00a7:
        r1 = r1 + 1;
        goto L_0x0010;
    L_0x00ab:
        if (r11 == 0) goto L_0x00b7;
    L_0x00ad:
        if (r12 >= r13) goto L_0x00b7;
    L_0x00af:
        r14.setSpan(r11, r12, r13, r0);	 Catch:{ Exception -> 0x00b3 }
        goto L_0x00b7;
    L_0x00b3:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
    L_0x00b7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addStyleToText(org.telegram.ui.Components.TextStyleSpan, int, int, android.text.Spannable, boolean):void");
    }

    public static ArrayList<TextStyleRun> getTextStyleRuns(ArrayList<MessageEntity> arrayList, CharSequence charSequence) {
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList(arrayList);
        Collections.sort(arrayList3, -$$Lambda$MediaDataController$HGpeb_hNfm0S_Rlla8Kw4aA7ynE.INSTANCE);
        int size = arrayList3.size();
        for (int i = 0; i < size; i++) {
            MessageEntity messageEntity = (MessageEntity) arrayList3.get(i);
            if (messageEntity.length > 0) {
                int i2 = messageEntity.offset;
                if (i2 >= 0 && i2 < charSequence.length()) {
                    if (messageEntity.offset + messageEntity.length > charSequence.length()) {
                        messageEntity.length = charSequence.length() - messageEntity.offset;
                    }
                    TextStyleRun textStyleRun = new TextStyleRun();
                    textStyleRun.start = messageEntity.offset;
                    textStyleRun.end = textStyleRun.start + messageEntity.length;
                    if (messageEntity instanceof TL_messageEntityStrike) {
                        textStyleRun.flags = 8;
                    } else if (messageEntity instanceof TL_messageEntityUnderline) {
                        textStyleRun.flags = 16;
                    } else if (messageEntity instanceof TL_messageEntityBlockquote) {
                        textStyleRun.flags = 32;
                    } else if (messageEntity instanceof TL_messageEntityBold) {
                        textStyleRun.flags = 1;
                    } else if (messageEntity instanceof TL_messageEntityItalic) {
                        textStyleRun.flags = 2;
                    } else if ((messageEntity instanceof TL_messageEntityCode) || (messageEntity instanceof TL_messageEntityPre)) {
                        textStyleRun.flags = 4;
                    } else if (messageEntity instanceof TL_messageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = messageEntity;
                    } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = messageEntity;
                    } else {
                        textStyleRun.flags = 128;
                        textStyleRun.urlEntity = messageEntity;
                    }
                    int size2 = arrayList2.size();
                    int i3 = 0;
                    while (i3 < size2) {
                        TextStyleRun textStyleRun2 = (TextStyleRun) arrayList2.get(i3);
                        int i4 = textStyleRun.start;
                        int i5 = textStyleRun2.start;
                        TextStyleRun textStyleRun3;
                        if (i4 > i5) {
                            i5 = textStyleRun2.end;
                            if (i4 < i5) {
                                i4 = textStyleRun.end;
                                if (i4 < i5) {
                                    textStyleRun3 = new TextStyleRun(textStyleRun);
                                    textStyleRun3.merge(textStyleRun2);
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun3);
                                    textStyleRun3 = new TextStyleRun(textStyleRun2);
                                    textStyleRun3.start = textStyleRun.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun3);
                                } else if (i4 >= i5) {
                                    textStyleRun3 = new TextStyleRun(textStyleRun);
                                    textStyleRun3.merge(textStyleRun2);
                                    textStyleRun3.end = textStyleRun2.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun3);
                                }
                                i4 = textStyleRun.start;
                                textStyleRun.start = textStyleRun2.end;
                                textStyleRun2.end = i4;
                            }
                        } else {
                            i4 = textStyleRun.end;
                            if (i5 < i4) {
                                int i6 = textStyleRun2.end;
                                if (i4 == i6) {
                                    textStyleRun2.merge(textStyleRun);
                                } else if (i4 < i6) {
                                    textStyleRun3 = new TextStyleRun(textStyleRun2);
                                    textStyleRun3.merge(textStyleRun);
                                    textStyleRun3.end = textStyleRun.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun3);
                                    textStyleRun2.start = textStyleRun.end;
                                } else {
                                    textStyleRun3 = new TextStyleRun(textStyleRun);
                                    textStyleRun3.start = textStyleRun2.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun3);
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
        }
        return arrayList2;
    }

    static /* synthetic */ int lambda$getTextStyleRuns$97(MessageEntity messageEntity, MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    /* JADX WARNING: Missing block: B:26:0x0057, code skipped:
            if (r6 != null) goto L_0x005e;
     */
    /* JADX WARNING: Missing block: B:27:0x0059, code skipped:
            r6 = new java.util.ArrayList();
     */
    /* JADX WARNING: Missing block: B:28:0x005e, code skipped:
            if (r1 == null) goto L_0x0062;
     */
    /* JADX WARNING: Missing block: B:29:0x0060, code skipped:
            r13 = 3;
     */
    /* JADX WARNING: Missing block: B:30:0x0062, code skipped:
            r13 = 1;
     */
    /* JADX WARNING: Missing block: B:31:0x0063, code skipped:
            r13 = r13 + r4;
     */
    /* JADX WARNING: Missing block: B:33:0x006a, code skipped:
            if (r13 >= r20[0].length()) goto L_0x0079;
     */
    /* JADX WARNING: Missing block: B:35:0x0072, code skipped:
            if (r20[0].charAt(r13) != '`') goto L_0x0079;
     */
    /* JADX WARNING: Missing block: B:36:0x0074, code skipped:
            r4 = r4 + 1;
            r13 = r13 + 1;
     */
    /* JADX WARNING: Missing block: B:37:0x0079, code skipped:
            if (r1 == null) goto L_0x007d;
     */
    /* JADX WARNING: Missing block: B:38:0x007b, code skipped:
            r12 = 3;
     */
    /* JADX WARNING: Missing block: B:39:0x007d, code skipped:
            r12 = 1;
     */
    /* JADX WARNING: Missing block: B:40:0x007e, code skipped:
            r12 = r12 + r4;
     */
    /* JADX WARNING: Missing block: B:41:0x007f, code skipped:
            if (r1 == null) goto L_0x0121;
     */
    /* JADX WARNING: Missing block: B:42:0x0081, code skipped:
            if (r5 <= 0) goto L_0x008c;
     */
    /* JADX WARNING: Missing block: B:43:0x0083, code skipped:
            r1 = r20[0].charAt(r5 - 1);
     */
    /* JADX WARNING: Missing block: B:44:0x008c, code skipped:
            r1 = 0;
     */
    /* JADX WARNING: Missing block: B:45:0x008d, code skipped:
            if (r1 == ' ') goto L_0x0094;
     */
    /* JADX WARNING: Missing block: B:46:0x008f, code skipped:
            if (r1 != 10) goto L_0x0092;
     */
    /* JADX WARNING: Missing block: B:47:0x0092, code skipped:
            r1 = 0;
     */
    /* JADX WARNING: Missing block: B:48:0x0094, code skipped:
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:49:0x0095, code skipped:
            r13 = substring(r20[0], 0, r5 - r1);
            r14 = substring(r20[0], r5 + 3, r4);
            r15 = r4 + 3;
     */
    /* JADX WARNING: Missing block: B:50:0x00ad, code skipped:
            if (r15 >= r20[0].length()) goto L_0x00b6;
     */
    /* JADX WARNING: Missing block: B:51:0x00af, code skipped:
            r3 = r20[0].charAt(r15);
     */
    /* JADX WARNING: Missing block: B:52:0x00b6, code skipped:
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:53:0x00b7, code skipped:
            r9 = r20[0];
     */
    /* JADX WARNING: Missing block: B:54:0x00b9, code skipped:
            if (r3 == ' ') goto L_0x00c0;
     */
    /* JADX WARNING: Missing block: B:55:0x00bb, code skipped:
            if (r3 != 10) goto L_0x00be;
     */
    /* JADX WARNING: Missing block: B:56:0x00be, code skipped:
            r3 = 0;
     */
    /* JADX WARNING: Missing block: B:57:0x00c0, code skipped:
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:58:0x00c1, code skipped:
            r3 = substring(r9, r15 + r3, r20[0].length());
            r8 = "\n";
     */
    /* JADX WARNING: Missing block: B:59:0x00d2, code skipped:
            if (r13.length() == 0) goto L_0x00df;
     */
    /* JADX WARNING: Missing block: B:60:0x00d4, code skipped:
            r13 = org.telegram.messenger.AndroidUtilities.concat(r13, r8);
     */
    /* JADX WARNING: Missing block: B:61:0x00df, code skipped:
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:63:0x00e4, code skipped:
            if (r3.length() == 0) goto L_0x00f0;
     */
    /* JADX WARNING: Missing block: B:64:0x00e6, code skipped:
            r3 = org.telegram.messenger.AndroidUtilities.concat(r8, r3);
     */
    /* JADX WARNING: Missing block: B:66:0x00f4, code skipped:
            if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x015e;
     */
    /* JADX WARNING: Missing block: B:67:0x00f6, code skipped:
            r20[0] = org.telegram.messenger.AndroidUtilities.concat(r13, r14, r3);
            r3 = new org.telegram.tgnet.TLRPC.TL_messageEntityPre();
            r3.offset = (r1 ^ 1) + r5;
            r3.length = ((r4 - r5) - 3) + (r1 ^ 1);
            r3.language = "";
            r6.add(r3);
            r12 = r12 - 6;
     */
    /* JADX WARNING: Missing block: B:69:0x0123, code skipped:
            if ((r5 + 1) == r4) goto L_0x015e;
     */
    /* JADX WARNING: Missing block: B:70:0x0125, code skipped:
            r20[0] = org.telegram.messenger.AndroidUtilities.concat(substring(r20[0], 0, r5), substring(r20[0], r5 + 1, r4), substring(r20[0], r4 + 1, r20[0].length()));
            r1 = new org.telegram.tgnet.TLRPC.TL_messageEntityCode();
            r1.offset = r5;
            r1.length = (r4 - r5) - 1;
            r6.add(r1);
            r12 = r12 - 2;
     */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> getEntities(java.lang.CharSequence[] r20) {
        /*
        r19 = this;
        r0 = r19;
        r1 = 0;
        if (r20 == 0) goto L_0x0419;
    L_0x0005:
        r2 = 0;
        r3 = r20[r2];
        if (r3 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0419;
    L_0x000c:
        r3 = -1;
        r6 = r1;
        r1 = 0;
        r4 = 0;
    L_0x0010:
        r5 = -1;
    L_0x0011:
        r7 = r20[r2];
        if (r1 != 0) goto L_0x0018;
    L_0x0015:
        r8 = "`";
        goto L_0x001a;
    L_0x0018:
        r8 = "```";
    L_0x001a:
        r4 = android.text.TextUtils.indexOf(r7, r8, r4);
        r7 = 10;
        r8 = 32;
        r10 = 2;
        r11 = 1;
        if (r4 == r3) goto L_0x0163;
    L_0x0026:
        r12 = 96;
        if (r5 != r3) goto L_0x0057;
    L_0x002a:
        r1 = r20[r2];
        r1 = r1.length();
        r1 = r1 - r4;
        if (r1 <= r10) goto L_0x0049;
    L_0x0033:
        r1 = r20[r2];
        r5 = r4 + 1;
        r1 = r1.charAt(r5);
        if (r1 != r12) goto L_0x0049;
    L_0x003d:
        r1 = r20[r2];
        r5 = r4 + 2;
        r1 = r1.charAt(r5);
        if (r1 != r12) goto L_0x0049;
    L_0x0047:
        r1 = 1;
        goto L_0x004a;
    L_0x0049:
        r1 = 0;
    L_0x004a:
        if (r1 == 0) goto L_0x004e;
    L_0x004c:
        r9 = 3;
        goto L_0x004f;
    L_0x004e:
        r9 = 1;
    L_0x004f:
        r5 = r4 + r9;
        r18 = r5;
        r5 = r4;
        r4 = r18;
        goto L_0x0011;
    L_0x0057:
        if (r6 != 0) goto L_0x005e;
    L_0x0059:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x005e:
        if (r1 == 0) goto L_0x0062;
    L_0x0060:
        r13 = 3;
        goto L_0x0063;
    L_0x0062:
        r13 = 1;
    L_0x0063:
        r13 = r13 + r4;
    L_0x0064:
        r14 = r20[r2];
        r14 = r14.length();
        if (r13 >= r14) goto L_0x0079;
    L_0x006c:
        r14 = r20[r2];
        r14 = r14.charAt(r13);
        if (r14 != r12) goto L_0x0079;
    L_0x0074:
        r4 = r4 + 1;
        r13 = r13 + 1;
        goto L_0x0064;
    L_0x0079:
        if (r1 == 0) goto L_0x007d;
    L_0x007b:
        r12 = 3;
        goto L_0x007e;
    L_0x007d:
        r12 = 1;
    L_0x007e:
        r12 = r12 + r4;
        if (r1 == 0) goto L_0x0121;
    L_0x0081:
        if (r5 <= 0) goto L_0x008c;
    L_0x0083:
        r1 = r20[r2];
        r13 = r5 + -1;
        r1 = r1.charAt(r13);
        goto L_0x008d;
    L_0x008c:
        r1 = 0;
    L_0x008d:
        if (r1 == r8) goto L_0x0094;
    L_0x008f:
        if (r1 != r7) goto L_0x0092;
    L_0x0091:
        goto L_0x0094;
    L_0x0092:
        r1 = 0;
        goto L_0x0095;
    L_0x0094:
        r1 = 1;
    L_0x0095:
        r13 = r20[r2];
        r14 = r5 - r1;
        r13 = r0.substring(r13, r2, r14);
        r14 = r20[r2];
        r15 = r5 + 3;
        r14 = r0.substring(r14, r15, r4);
        r15 = r4 + 3;
        r16 = r20[r2];
        r3 = r16.length();
        if (r15 >= r3) goto L_0x00b6;
    L_0x00af:
        r3 = r20[r2];
        r3 = r3.charAt(r15);
        goto L_0x00b7;
    L_0x00b6:
        r3 = 0;
    L_0x00b7:
        r9 = r20[r2];
        if (r3 == r8) goto L_0x00c0;
    L_0x00bb:
        if (r3 != r7) goto L_0x00be;
    L_0x00bd:
        goto L_0x00c0;
    L_0x00be:
        r3 = 0;
        goto L_0x00c1;
    L_0x00c0:
        r3 = 1;
    L_0x00c1:
        r15 = r15 + r3;
        r3 = r20[r2];
        r3 = r3.length();
        r3 = r0.substring(r9, r15, r3);
        r7 = r13.length();
        r8 = "\n";
        if (r7 == 0) goto L_0x00df;
    L_0x00d4:
        r7 = new java.lang.CharSequence[r10];
        r7[r2] = r13;
        r7[r11] = r8;
        r13 = org.telegram.messenger.AndroidUtilities.concat(r7);
        goto L_0x00e0;
    L_0x00df:
        r1 = 1;
    L_0x00e0:
        r7 = r3.length();
        if (r7 == 0) goto L_0x00f0;
    L_0x00e6:
        r7 = new java.lang.CharSequence[r10];
        r7[r2] = r8;
        r7[r11] = r3;
        r3 = org.telegram.messenger.AndroidUtilities.concat(r7);
    L_0x00f0:
        r7 = android.text.TextUtils.isEmpty(r14);
        if (r7 != 0) goto L_0x015e;
    L_0x00f6:
        r7 = 3;
        r8 = new java.lang.CharSequence[r7];
        r8[r2] = r13;
        r8[r11] = r14;
        r8[r10] = r3;
        r3 = org.telegram.messenger.AndroidUtilities.concat(r8);
        r20[r2] = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre;
        r3.<init>();
        r7 = r1 ^ 1;
        r7 = r7 + r5;
        r3.offset = r7;
        r4 = r4 - r5;
        r5 = 3;
        r4 = r4 - r5;
        r1 = r1 ^ 1;
        r4 = r4 + r1;
        r3.length = r4;
        r1 = "";
        r3.language = r1;
        r6.add(r3);
        r12 = r12 + -6;
        goto L_0x015e;
    L_0x0121:
        r1 = r5 + 1;
        if (r1 == r4) goto L_0x015e;
    L_0x0125:
        r3 = 3;
        r3 = new java.lang.CharSequence[r3];
        r7 = r20[r2];
        r7 = r0.substring(r7, r2, r5);
        r3[r2] = r7;
        r7 = r20[r2];
        r1 = r0.substring(r7, r1, r4);
        r3[r11] = r1;
        r1 = r20[r2];
        r7 = r4 + 1;
        r8 = r20[r2];
        r8 = r8.length();
        r1 = r0.substring(r1, r7, r8);
        r3[r10] = r1;
        r1 = org.telegram.messenger.AndroidUtilities.concat(r3);
        r20[r2] = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode;
        r1.<init>();
        r1.offset = r5;
        r4 = r4 - r5;
        r4 = r4 - r11;
        r1.length = r4;
        r6.add(r1);
        r12 = r12 + -2;
    L_0x015e:
        r4 = r12;
        r1 = 0;
        r3 = -1;
        goto L_0x0010;
    L_0x0163:
        if (r5 == r3) goto L_0x019a;
    L_0x0165:
        if (r1 == 0) goto L_0x019a;
    L_0x0167:
        r1 = new java.lang.CharSequence[r10];
        r3 = r20[r2];
        r3 = r0.substring(r3, r2, r5);
        r1[r2] = r3;
        r3 = r20[r2];
        r4 = r5 + 2;
        r9 = r20[r2];
        r9 = r9.length();
        r3 = r0.substring(r3, r4, r9);
        r1[r11] = r3;
        r1 = org.telegram.messenger.AndroidUtilities.concat(r1);
        r20[r2] = r1;
        if (r6 != 0) goto L_0x018e;
    L_0x0189:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x018e:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode;
        r1.<init>();
        r1.offset = r5;
        r1.length = r11;
        r6.add(r1);
    L_0x019a:
        r1 = r20[r2];
        r1 = r1 instanceof android.text.Spanned;
        if (r1 == 0) goto L_0x0315;
    L_0x01a0:
        r1 = r20[r2];
        r1 = (android.text.Spanned) r1;
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.TextStyleSpan.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.TextStyleSpan[]) r3;
        if (r3 == 0) goto L_0x0253;
    L_0x01b4:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x0253;
    L_0x01b7:
        r4 = 0;
    L_0x01b8:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x0253;
    L_0x01bb:
        r5 = r3[r4];
        r9 = r1.getSpanStart(r5);
        r12 = r1.getSpanEnd(r5);
        r13 = checkInclusion(r9, r6);
        if (r13 != 0) goto L_0x024f;
    L_0x01cb:
        r13 = checkInclusion(r12, r6);
        if (r13 != 0) goto L_0x024f;
    L_0x01d1:
        r13 = checkIntersection(r9, r12, r6);
        if (r13 == 0) goto L_0x01d9;
    L_0x01d7:
        goto L_0x024f;
    L_0x01d9:
        if (r6 != 0) goto L_0x01e0;
    L_0x01db:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x01e0:
        r5 = r5.getStyleFlags();
        r13 = r5 & 1;
        if (r13 == 0) goto L_0x01f6;
    L_0x01e8:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold;
        r13.<init>();
        r13.offset = r9;
        r14 = r12 - r9;
        r13.length = r14;
        r6.add(r13);
    L_0x01f6:
        r13 = r5 & 2;
        if (r13 == 0) goto L_0x0208;
    L_0x01fa:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
        r13.<init>();
        r13.offset = r9;
        r14 = r12 - r9;
        r13.length = r14;
        r6.add(r13);
    L_0x0208:
        r13 = r5 & 4;
        if (r13 == 0) goto L_0x021a;
    L_0x020c:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode;
        r13.<init>();
        r13.offset = r9;
        r14 = r12 - r9;
        r13.length = r14;
        r6.add(r13);
    L_0x021a:
        r13 = r5 & 8;
        if (r13 == 0) goto L_0x022c;
    L_0x021e:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
        r13.<init>();
        r13.offset = r9;
        r14 = r12 - r9;
        r13.length = r14;
        r6.add(r13);
    L_0x022c:
        r13 = r5 & 16;
        if (r13 == 0) goto L_0x023e;
    L_0x0230:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
        r13.<init>();
        r13.offset = r9;
        r14 = r12 - r9;
        r13.length = r14;
        r6.add(r13);
    L_0x023e:
        r5 = r5 & 32;
        if (r5 == 0) goto L_0x024f;
    L_0x0242:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote;
        r5.<init>();
        r5.offset = r9;
        r12 = r12 - r9;
        r5.length = r12;
        r6.add(r5);
    L_0x024f:
        r4 = r4 + 1;
        goto L_0x01b8;
    L_0x0253:
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.URLSpanUserMention.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.URLSpanUserMention[]) r3;
        if (r3 == 0) goto L_0x02c6;
    L_0x0263:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x02c6;
    L_0x0266:
        if (r6 != 0) goto L_0x026d;
    L_0x0268:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x026d:
        r4 = 0;
    L_0x026e:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x02c6;
    L_0x0271:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
        r5.<init>();
        r9 = r19.getMessagesController();
        r12 = r3[r4];
        r12 = r12.getURL();
        r12 = org.telegram.messenger.Utilities.parseInt(r12);
        r12 = r12.intValue();
        r9 = r9.getInputUser(r12);
        r5.user_id = r9;
        r9 = r5.user_id;
        if (r9 == 0) goto L_0x02c3;
    L_0x0292:
        r9 = r3[r4];
        r9 = r1.getSpanStart(r9);
        r5.offset = r9;
        r9 = r3[r4];
        r9 = r1.getSpanEnd(r9);
        r12 = r20[r2];
        r12 = r12.length();
        r9 = java.lang.Math.min(r9, r12);
        r12 = r5.offset;
        r9 = r9 - r12;
        r5.length = r9;
        r9 = r20[r2];
        r13 = r5.length;
        r12 = r12 + r13;
        r12 = r12 - r11;
        r9 = r9.charAt(r12);
        if (r9 != r8) goto L_0x02c0;
    L_0x02bb:
        r9 = r5.length;
        r9 = r9 - r11;
        r5.length = r9;
    L_0x02c0:
        r6.add(r5);
    L_0x02c3:
        r4 = r4 + 1;
        goto L_0x026e;
    L_0x02c6:
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.URLSpanReplacement.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.URLSpanReplacement[]) r3;
        if (r3 == 0) goto L_0x0315;
    L_0x02d6:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x0315;
    L_0x02d9:
        if (r6 != 0) goto L_0x02e1;
    L_0x02db:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r6 = r4;
    L_0x02e1:
        r4 = 0;
    L_0x02e2:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x0315;
    L_0x02e5:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
        r5.<init>();
        r9 = r3[r4];
        r9 = r1.getSpanStart(r9);
        r5.offset = r9;
        r9 = r3[r4];
        r9 = r1.getSpanEnd(r9);
        r12 = r20[r2];
        r12 = r12.length();
        r9 = java.lang.Math.min(r9, r12);
        r12 = r5.offset;
        r9 = r9 - r12;
        r5.length = r9;
        r9 = r3[r4];
        r9 = r9.getURL();
        r5.url = r9;
        r6.add(r5);
        r4 = r4 + 1;
        goto L_0x02e2;
    L_0x0315:
        r1 = 0;
    L_0x0316:
        if (r1 >= r10) goto L_0x0418;
    L_0x0318:
        if (r1 != 0) goto L_0x031d;
    L_0x031a:
        r3 = "**";
        goto L_0x031f;
    L_0x031d:
        r3 = "__";
    L_0x031f:
        if (r1 != 0) goto L_0x0324;
    L_0x0321:
        r4 = 42;
        goto L_0x0326;
    L_0x0324:
        r4 = 95;
    L_0x0326:
        r5 = 0;
        r9 = -1;
    L_0x0328:
        r12 = r20[r2];
        r5 = android.text.TextUtils.indexOf(r12, r3, r5);
        r12 = -1;
        if (r5 == r12) goto L_0x040e;
    L_0x0331:
        if (r9 != r12) goto L_0x034e;
    L_0x0333:
        if (r5 != 0) goto L_0x0338;
    L_0x0335:
        r13 = 32;
        goto L_0x0340;
    L_0x0338:
        r13 = r20[r2];
        r14 = r5 + -1;
        r13 = r13.charAt(r14);
    L_0x0340:
        r14 = checkInclusion(r5, r6);
        if (r14 != 0) goto L_0x034b;
    L_0x0346:
        if (r13 == r8) goto L_0x034a;
    L_0x0348:
        if (r13 != r7) goto L_0x034b;
    L_0x034a:
        r9 = r5;
    L_0x034b:
        r5 = r5 + 2;
        goto L_0x0328;
    L_0x034e:
        r13 = r5 + 2;
    L_0x0350:
        r14 = r20[r2];
        r14 = r14.length();
        if (r13 >= r14) goto L_0x0365;
    L_0x0358:
        r14 = r20[r2];
        r14 = r14.charAt(r13);
        if (r14 != r4) goto L_0x0365;
    L_0x0360:
        r5 = r5 + 1;
        r13 = r13 + 1;
        goto L_0x0350;
    L_0x0365:
        r13 = r5 + 2;
        r14 = checkInclusion(r5, r6);
        if (r14 != 0) goto L_0x0404;
    L_0x036d:
        r14 = checkIntersection(r9, r5, r6);
        if (r14 == 0) goto L_0x0375;
    L_0x0373:
        goto L_0x0404;
    L_0x0375:
        r14 = r9 + 2;
        if (r14 == r5) goto L_0x0402;
    L_0x0379:
        if (r6 != 0) goto L_0x0380;
    L_0x037b:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x0380:
        r15 = 3;
        r7 = new java.lang.CharSequence[r15];	 Catch:{ Exception -> 0x03a8 }
        r8 = r20[r2];	 Catch:{ Exception -> 0x03a8 }
        r8 = r0.substring(r8, r2, r9);	 Catch:{ Exception -> 0x03a8 }
        r7[r2] = r8;	 Catch:{ Exception -> 0x03a8 }
        r8 = r20[r2];	 Catch:{ Exception -> 0x03a8 }
        r8 = r0.substring(r8, r14, r5);	 Catch:{ Exception -> 0x03a8 }
        r7[r11] = r8;	 Catch:{ Exception -> 0x03a8 }
        r8 = r20[r2];	 Catch:{ Exception -> 0x03a8 }
        r17 = r20[r2];	 Catch:{ Exception -> 0x03a8 }
        r11 = r17.length();	 Catch:{ Exception -> 0x03a8 }
        r8 = r0.substring(r8, r13, r11);	 Catch:{ Exception -> 0x03a8 }
        r7[r10] = r8;	 Catch:{ Exception -> 0x03a8 }
        r7 = org.telegram.messenger.AndroidUtilities.concat(r7);	 Catch:{ Exception -> 0x03a8 }
        r20[r2] = r7;	 Catch:{ Exception -> 0x03a8 }
        goto L_0x03e0;
    L_0x03a8:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = r20[r2];
        r8 = r0.substring(r8, r2, r9);
        r8 = r8.toString();
        r7.append(r8);
        r8 = r20[r2];
        r8 = r0.substring(r8, r14, r5);
        r8 = r8.toString();
        r7.append(r8);
        r8 = r20[r2];
        r11 = r20[r2];
        r11 = r11.length();
        r8 = r0.substring(r8, r13, r11);
        r8 = r8.toString();
        r7.append(r8);
        r7 = r7.toString();
        r20[r2] = r7;
    L_0x03e0:
        if (r1 != 0) goto L_0x03e8;
    L_0x03e2:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold;
        r7.<init>();
        goto L_0x03ed;
    L_0x03e8:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
        r7.<init>();
    L_0x03ed:
        r7.offset = r9;
        r5 = r5 - r9;
        r5 = r5 - r10;
        r7.length = r5;
        r5 = r7.offset;
        r8 = r7.length;
        r5 = r5 + r8;
        r8 = 4;
        removeOffsetAfter(r5, r8, r6);
        r6.add(r7);
        r13 = r13 + -4;
        goto L_0x0405;
    L_0x0402:
        r15 = 3;
        goto L_0x0405;
    L_0x0404:
        r15 = 3;
    L_0x0405:
        r5 = r13;
        r7 = 10;
        r8 = 32;
        r9 = -1;
        r11 = 1;
        goto L_0x0328;
    L_0x040e:
        r15 = 3;
        r1 = r1 + 1;
        r7 = 10;
        r8 = 32;
        r11 = 1;
        goto L_0x0316;
    L_0x0418:
        return r6;
    L_0x0419:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[]):java.util.ArrayList");
    }

    public void loadDrafts() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TL_messages_getAllDrafts(), new -$$Lambda$MediaDataController$qf9nDq6xR0djPuYTT1dYnn6Tf9w(this));
        }
    }

    public /* synthetic */ void lambda$loadDrafts$99$MediaDataController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$nDHcHrGZBqGnrBMyUlvRpyO4AyM(this));
        }
    }

    public /* synthetic */ void lambda$null$98$MediaDataController() {
        getUserConfig().draftsLoaded = true;
        this.loadingDrafts = false;
        getUserConfig().saveConfig(false);
    }

    public DraftMessage getDraft(long j) {
        return (DraftMessage) this.drafts.get(j);
    }

    public Message getDraftMessage(long j) {
        return (Message) this.draftMessages.get(j);
    }

    public void saveDraft(long j, CharSequence charSequence, ArrayList<MessageEntity> arrayList, Message message, boolean z) {
        saveDraft(j, charSequence, arrayList, message, z, false);
    }

    public void saveDraft(long j, CharSequence charSequence, ArrayList<MessageEntity> arrayList, Message message, boolean z, boolean z2) {
        DraftMessage tL_draftMessageEmpty;
        String str;
        if (TextUtils.isEmpty(charSequence) && message == null) {
            tL_draftMessageEmpty = new TL_draftMessageEmpty();
        } else {
            tL_draftMessageEmpty = new TL_draftMessage();
        }
        tL_draftMessageEmpty.date = (int) (System.currentTimeMillis() / 1000);
        if (charSequence == null) {
            str = "";
        } else {
            str = charSequence.toString();
        }
        tL_draftMessageEmpty.message = str;
        tL_draftMessageEmpty.no_webpage = z;
        if (message != null) {
            tL_draftMessageEmpty.reply_to_msg_id = message.id;
            tL_draftMessageEmpty.flags |= 1;
        }
        if (!(arrayList == null || arrayList.isEmpty())) {
            tL_draftMessageEmpty.entities = arrayList;
            tL_draftMessageEmpty.flags |= 8;
        }
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(j);
        if (z2 || !((draftMessage != null && draftMessage.message.equals(tL_draftMessageEmpty.message) && draftMessage.reply_to_msg_id == tL_draftMessageEmpty.reply_to_msg_id && draftMessage.no_webpage == tL_draftMessageEmpty.no_webpage) || (draftMessage == null && TextUtils.isEmpty(tL_draftMessageEmpty.message) && tL_draftMessageEmpty.reply_to_msg_id == 0))) {
            saveDraft(j, tL_draftMessageEmpty, message, false);
            int i = (int) j;
            if (i != 0) {
                TL_messages_saveDraft tL_messages_saveDraft = new TL_messages_saveDraft();
                tL_messages_saveDraft.peer = getMessagesController().getInputPeer(i);
                if (tL_messages_saveDraft.peer != null) {
                    tL_messages_saveDraft.message = tL_draftMessageEmpty.message;
                    tL_messages_saveDraft.no_webpage = tL_draftMessageEmpty.no_webpage;
                    tL_messages_saveDraft.reply_to_msg_id = tL_draftMessageEmpty.reply_to_msg_id;
                    tL_messages_saveDraft.entities = tL_draftMessageEmpty.entities;
                    tL_messages_saveDraft.flags = tL_draftMessageEmpty.flags;
                    getConnectionsManager().sendRequest(tL_messages_saveDraft, -$$Lambda$MediaDataController$0cLOMbkf5MDVYRl4Poneil7YgDk.INSTANCE);
                } else {
                    return;
                }
            }
            getMessagesController().sortDialogs(null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void saveDraft(long j, DraftMessage draftMessage, Message message, boolean z) {
        StringBuilder stringBuilder;
        Editor edit = this.preferences.edit();
        String str = "";
        String str2 = "r_";
        StringBuilder stringBuilder2;
        if (draftMessage == null || (draftMessage instanceof TL_draftMessageEmpty)) {
            this.drafts.remove(j);
            this.draftMessages.remove(j);
            Editor edit2 = this.preferences.edit();
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(j);
            Editor remove = edit2.remove(stringBuilder2.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(j);
            remove.remove(stringBuilder.toString()).commit();
        } else {
            this.drafts.put(j, draftMessage);
            try {
                SerializedData serializedData = new SerializedData(draftMessage.getObjectSize());
                draftMessage.serializeToStream(serializedData);
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(j);
                edit.putString(stringBuilder2.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (message == null) {
            this.draftMessages.remove(j);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(str2);
            stringBuilder3.append(j);
            edit.remove(stringBuilder3.toString());
        } else {
            this.draftMessages.put(j, message);
            SerializedData serializedData2 = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData2);
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(j);
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        edit.commit();
        if (z) {
            if (draftMessage.reply_to_msg_id != 0 && message == null) {
                User user;
                int i = (int) j;
                Chat chat = null;
                if (i > 0) {
                    user = getMessagesController().getUser(Integer.valueOf(i));
                } else {
                    chat = getMessagesController().getChat(Integer.valueOf(-i));
                    user = null;
                }
                if (!(user == null && chat == null)) {
                    long j2;
                    int i2;
                    long j3 = (long) draftMessage.reply_to_msg_id;
                    if (ChatObject.isChannel(chat)) {
                        int i3 = chat.id;
                        j2 = j3 | (((long) i3) << 32);
                        i2 = i3;
                    } else {
                        j2 = j3;
                        i2 = 0;
                    }
                    getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$O5RDeFiGit8q1NqMPASmBZ6Xiw4(this, j2, i2, j));
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    public /* synthetic */ void lambda$saveDraft$103$MediaDataController(long j, int i, long j2) {
        Message message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    message.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (message != null) {
                saveDraftReplyMessage(j2, message);
            } else if (i != 0) {
                TL_channels_getMessages tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = getMessagesController().getInputChannel(i);
                tL_channels_getMessages.id.add(Integer.valueOf((int) j));
                getConnectionsManager().sendRequest(tL_channels_getMessages, new -$$Lambda$MediaDataController$DkGsvLeVCAPHzGiS5-pyMcn0zGc(this, j2));
            } else {
                TL_messages_getMessages tL_messages_getMessages = new TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf((int) j));
                getConnectionsManager().sendRequest(tL_messages_getMessages, new -$$Lambda$MediaDataController$F6PFGdJfs78rGTuadJcCWacwekU(this, j2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$101$MediaDataController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, (Message) messages_messages.messages.get(0));
            }
        }
    }

    public /* synthetic */ void lambda$null$102$MediaDataController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, (Message) messages_messages.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long j, Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$rxPBl0Wth4p_qQY5y_bgCFg3pbc(this, j, message));
        }
    }

    public /* synthetic */ void lambda$saveDraftReplyMessage$104$MediaDataController(long j, Message message) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(j);
        if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
            this.draftMessages.put(j, message);
            SerializedData serializedData = new SerializedData(message.getObjectSize());
            message.serializeToStream(serializedData);
            Editor edit = this.preferences.edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("r_");
            stringBuilder.append(j);
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts() {
        this.drafts.clear();
        this.draftMessages.clear();
        this.preferences.edit().clear().commit();
        getMessagesController().sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void cleanDraft(long j, boolean z) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(j);
        if (draftMessage != null) {
            if (!z) {
                this.drafts.remove(j);
                this.draftMessages.remove(j);
                Editor edit = this.preferences.edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(j);
                edit = edit.remove(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("r_");
                stringBuilder.append(j);
                edit.remove(stringBuilder.toString()).commit();
                getMessagesController().sortDialogs(null);
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id != 0) {
                draftMessage.reply_to_msg_id = 0;
                draftMessage.flags &= -2;
                saveDraft(j, draftMessage.message, draftMessage.entities, null, draftMessage.no_webpage, true);
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$fFK8_MDw4oBxbrTp5roN7T5GZ5o(this, arrayList, j));
    }

    public /* synthetic */ void lambda$clearBotKeyboard$105$MediaDataController(ArrayList arrayList, long j) {
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
        if (((Message) this.botKeyboards.get(j)) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, r0, Long.valueOf(j));
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$Gz5I8osJQBvFDrSEevGOtuo23nw(this, j));
    }

    public /* synthetic */ void lambda$loadBotKeyboard$107$MediaDataController(long j) {
        Message message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (message != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$GrmflOcHcXAhqoetvN5KDvbShLc(this, message, j));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$106$MediaDataController(Message message, long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void loadBotInfo(int i, boolean z, int i2) {
        if (!z || ((BotInfo) this.botInfos.get(i)) == null) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$AXhXCHOnA5dn0iSyFsKkx1qYL2Q(this, i, i2));
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, r5, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$loadBotInfo$109$MediaDataController(int i, int i2) {
        BotInfo botInfo = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    botInfo = BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$algbsa_c8p8xBmxAZehs4NPw9Us(this, botInfo, i2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$108$MediaDataController(BotInfo botInfo, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(i));
    }

    public void putBotKeyboard(long j, Message message) {
        if (message != null) {
            try {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                Object[] objArr = new Object[1];
                int i = 0;
                objArr[0] = Long.valueOf(j);
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", objArr), new Object[0]);
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
                    AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$Ss3BJMBs_OLHtUMPBuKsirDNoOo(this, j, message));
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$putBotKeyboard$110$MediaDataController(long j, Message message) {
        Message message2 = (Message) this.botKeyboards.get(j);
        this.botKeyboards.put(j, message);
        if (message2 != null) {
            this.botKeyboardsByMids.delete(message2.id);
        }
        this.botKeyboardsByMids.put(message.id, j);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void putBotInfo(BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$pIbD4vqEWPitW5RFURz-GVEPaOg(this, botInfo));
        }
    }

    public /* synthetic */ void lambda$putBotInfo$111$MediaDataController(BotInfo botInfo) {
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
            FileLog.e(e);
        }
    }

    public void fetchNewEmojiKeywords(String[] strArr) {
        if (strArr != null) {
            int i = 0;
            while (i < strArr.length) {
                CharSequence charSequence = strArr[i];
                if (!TextUtils.isEmpty(charSequence) && this.currentFetchingEmoji.get(charSequence) == null) {
                    this.currentFetchingEmoji.put(charSequence, Boolean.valueOf(true));
                    getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$WB4q8KlwiSIiz-XpZaJt2ToklOw(this, charSequence));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f  */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$117$MediaDataController(java.lang.String r10) {
        /*
        r9 = this;
        r0 = -1;
        r1 = 0;
        r2 = 0;
        r4 = r9.getMessagesStorage();	 Catch:{ Exception -> 0x0033 }
        r4 = r4.getDatabase();	 Catch:{ Exception -> 0x0033 }
        r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?";
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0033 }
        r8 = 0;
        r7[r8] = r10;	 Catch:{ Exception -> 0x0033 }
        r4 = r4.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x0033 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0033 }
        if (r5 == 0) goto L_0x002c;
    L_0x001e:
        r1 = r4.stringValue(r8);	 Catch:{ Exception -> 0x0033 }
        r5 = r4.intValue(r6);	 Catch:{ Exception -> 0x0033 }
        r6 = 2;
        r2 = r4.longValue(r6);	 Catch:{ Exception -> 0x0031 }
        goto L_0x002d;
    L_0x002c:
        r5 = -1;
    L_0x002d:
        r4.dispose();	 Catch:{ Exception -> 0x0031 }
        goto L_0x0038;
    L_0x0031:
        r4 = move-exception;
        goto L_0x0035;
    L_0x0033:
        r4 = move-exception;
        r5 = -1;
    L_0x0035:
        org.telegram.messenger.FileLog.e(r4);
    L_0x0038:
        r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r4 != 0) goto L_0x0055;
    L_0x003c:
        r6 = java.lang.System.currentTimeMillis();
        r6 = r6 - r2;
        r2 = java.lang.Math.abs(r6);
        r6 = 3600000; // 0x36ee80 float:5.044674E-39 double:1.7786363E-317;
        r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x0055;
    L_0x004c:
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$6XRaXYkBf_IOwWQcJBJiNJD6DNE;
        r0.<init>(r9, r10);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
    L_0x0055:
        if (r5 != r0) goto L_0x005f;
    L_0x0057:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords;
        r0.<init>();
        r0.lang_code = r10;
        goto L_0x0068;
    L_0x005f:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference;
        r0.<init>();
        r0.lang_code = r10;
        r0.from_version = r5;
    L_0x0068:
        r2 = r9.getConnectionsManager();
        r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$EVErkBGf1j_IFHYtQbxSB3xLCoY;
        r3.<init>(r9, r5, r1, r10);
        r2.sendRequest(r0, r3);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$117$MediaDataController(java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$112$MediaDataController(String str) {
        Boolean bool = (Boolean) this.currentFetchingEmoji.remove(str);
    }

    public /* synthetic */ void lambda$null$116$MediaDataController(int i, String str, String str2, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_emojiKeywordsDifference tL_emojiKeywordsDifference = (TL_emojiKeywordsDifference) tLObject;
            if (i == -1 || tL_emojiKeywordsDifference.lang_code.equals(str)) {
                putEmojiKeywords(str2, tL_emojiKeywordsDifference);
                return;
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$sU7x0ezZUU1ai2CLg5MYhLtyQOY(this, str2));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$gYoSkQToKx30ULfnqO2StylO5xI(this, str2));
    }

    public /* synthetic */ void lambda$null$114$MediaDataController(String str) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$B7dbwC7TKkYumBMxHIf-Pp1w8EU(this, str));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$113$MediaDataController(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    public /* synthetic */ void lambda$null$115$MediaDataController(String str) {
        Boolean bool = (Boolean) this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(String str, TL_emojiKeywordsDifference tL_emojiKeywordsDifference) {
        if (tL_emojiKeywordsDifference != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$EvTxiVNHRw13fRGs7CTexXVtmQQ(this, tL_emojiKeywordsDifference, str));
        }
    }

    public /* synthetic */ void lambda$putEmojiKeywords$119$MediaDataController(TL_emojiKeywordsDifference tL_emojiKeywordsDifference, String str) {
        try {
            SQLitePreparedStatement executeFast;
            if (!tL_emojiKeywordsDifference.keywords.isEmpty()) {
                executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int size = tL_emojiKeywordsDifference.keywords.size();
                for (int i = 0; i < size; i++) {
                    EmojiKeyword emojiKeyword = (EmojiKeyword) tL_emojiKeywordsDifference.keywords.get(i);
                    String toLowerCase;
                    int size2;
                    int i2;
                    if (emojiKeyword instanceof TL_emojiKeyword) {
                        TL_emojiKeyword tL_emojiKeyword = (TL_emojiKeyword) emojiKeyword;
                        toLowerCase = tL_emojiKeyword.keyword.toLowerCase();
                        size2 = tL_emojiKeyword.emoticons.size();
                        for (i2 = 0; i2 < size2; i2++) {
                            executeFast.requery();
                            executeFast.bindString(1, tL_emojiKeywordsDifference.lang_code);
                            executeFast.bindString(2, toLowerCase);
                            executeFast.bindString(3, (String) tL_emojiKeyword.emoticons.get(i2));
                            executeFast.step();
                        }
                    } else if (emojiKeyword instanceof TL_emojiKeywordDeleted) {
                        TL_emojiKeywordDeleted tL_emojiKeywordDeleted = (TL_emojiKeywordDeleted) emojiKeyword;
                        toLowerCase = tL_emojiKeywordDeleted.keyword.toLowerCase();
                        size2 = tL_emojiKeywordDeleted.emoticons.size();
                        for (i2 = 0; i2 < size2; i2++) {
                            executeFast2.requery();
                            executeFast2.bindString(1, tL_emojiKeywordsDifference.lang_code);
                            executeFast2.bindString(2, toLowerCase);
                            executeFast2.bindString(3, (String) tL_emojiKeywordDeleted.emoticons.get(i2));
                            executeFast2.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                executeFast.dispose();
                executeFast2.dispose();
            }
            executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            executeFast.bindString(1, str);
            executeFast.bindString(2, tL_emojiKeywordsDifference.lang_code);
            executeFast.bindInteger(3, tL_emojiKeywordsDifference.version);
            executeFast.bindLong(4, System.currentTimeMillis());
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$MediaDataController$laJZz9BwVi-z9ZUfKYkUXt_LAnA(this, str));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$118$MediaDataController(String str) {
        this.currentFetchingEmoji.remove(str);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback) {
        getEmojiSuggestions(strArr, str, z, keywordResultCallback, null);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, CountDownLatch countDownLatch) {
        if (keywordResultCallback != null) {
            if (TextUtils.isEmpty(str) || strArr == null) {
                keywordResultCallback.run(new ArrayList(), null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$MediaDataController$8sjmATlhe9Ln0JHkLqJNaDq-md8(this, strArr, keywordResultCallback, str, z, new ArrayList(Emoji.recentEmoji), countDownLatch));
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0126  */
    public /* synthetic */ void lambda$getEmojiSuggestions$123$MediaDataController(java.lang.String[] r15, org.telegram.messenger.MediaDataController.KeywordResultCallback r16, java.lang.String r17, boolean r18, java.util.ArrayList r19, java.util.concurrent.CountDownLatch r20) {
        /*
        r14 = this;
        r0 = r15;
        r1 = r16;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.HashMap;
        r3.<init>();
        r4 = 0;
        r5 = 0;
        r8 = r4;
        r6 = 0;
        r7 = 0;
    L_0x0012:
        r9 = r0.length;	 Catch:{ Exception -> 0x010e }
        r10 = 1;
        if (r6 >= r9) goto L_0x003d;
    L_0x0016:
        r9 = r14.getMessagesStorage();	 Catch:{ Exception -> 0x010e }
        r9 = r9.getDatabase();	 Catch:{ Exception -> 0x010e }
        r11 = "SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?";
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x010e }
        r13 = r0[r6];	 Catch:{ Exception -> 0x010e }
        r12[r5] = r13;	 Catch:{ Exception -> 0x010e }
        r9 = r9.queryFinalized(r11, r12);	 Catch:{ Exception -> 0x010e }
        r11 = r9.next();	 Catch:{ Exception -> 0x010e }
        if (r11 == 0) goto L_0x0034;
    L_0x0030:
        r8 = r9.stringValue(r5);	 Catch:{ Exception -> 0x010e }
    L_0x0034:
        r9.dispose();	 Catch:{ Exception -> 0x010e }
        if (r8 == 0) goto L_0x003a;
    L_0x0039:
        r7 = 1;
    L_0x003a:
        r6 = r6 + 1;
        goto L_0x0012;
    L_0x003d:
        if (r7 != 0) goto L_0x0049;
    L_0x003f:
        r3 = new org.telegram.messenger.-$$Lambda$MediaDataController$Wdr_oo0zkl67l_Mm3ST1kkaWeAY;	 Catch:{ Exception -> 0x010e }
        r6 = r14;
        r3.<init>(r14, r15, r1, r2);	 Catch:{ Exception -> 0x010c }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);	 Catch:{ Exception -> 0x010c }
        return;
    L_0x0049:
        r6 = r14;
        r0 = r17.toLowerCase();	 Catch:{ Exception -> 0x010c }
        r7 = r0;
        r0 = 0;
    L_0x0050:
        r9 = 2;
        if (r0 >= r9) goto L_0x0113;
    L_0x0053:
        if (r0 != r10) goto L_0x0065;
    L_0x0055:
        r11 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x010c }
        r11 = r11.getTranslitString(r7, r5, r5);	 Catch:{ Exception -> 0x010c }
        r12 = r11.equals(r7);	 Catch:{ Exception -> 0x010c }
        if (r12 == 0) goto L_0x0066;
    L_0x0063:
        goto L_0x0108;
    L_0x0065:
        r11 = r7;
    L_0x0066:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010c }
        r7.<init>(r11);	 Catch:{ Exception -> 0x010c }
        r12 = r7.length();	 Catch:{ Exception -> 0x010c }
    L_0x006f:
        if (r12 <= 0) goto L_0x0083;
    L_0x0071:
        r12 = r12 + -1;
        r13 = r7.charAt(r12);	 Catch:{ Exception -> 0x010c }
        r13 = r13 + r10;
        r13 = (char) r13;	 Catch:{ Exception -> 0x010c }
        r7.setCharAt(r12, r13);	 Catch:{ Exception -> 0x010c }
        if (r13 == 0) goto L_0x006f;
    L_0x007e:
        r7 = r7.toString();	 Catch:{ Exception -> 0x010c }
        goto L_0x0084;
    L_0x0083:
        r7 = r4;
    L_0x0084:
        if (r18 == 0) goto L_0x0099;
    L_0x0086:
        r7 = r14.getMessagesStorage();	 Catch:{ Exception -> 0x010c }
        r7 = r7.getDatabase();	 Catch:{ Exception -> 0x010c }
        r9 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?";
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x010c }
        r12[r5] = r11;	 Catch:{ Exception -> 0x010c }
        r7 = r7.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x010c }
        goto L_0x00d3;
    L_0x0099:
        if (r7 == 0) goto L_0x00b0;
    L_0x009b:
        r12 = r14.getMessagesStorage();	 Catch:{ Exception -> 0x010c }
        r12 = r12.getDatabase();	 Catch:{ Exception -> 0x010c }
        r13 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?";
        r9 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x010c }
        r9[r5] = r11;	 Catch:{ Exception -> 0x010c }
        r9[r10] = r7;	 Catch:{ Exception -> 0x010c }
        r7 = r12.queryFinalized(r13, r9);	 Catch:{ Exception -> 0x010c }
        goto L_0x00d3;
    L_0x00b0:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x010c }
        r7.<init>();	 Catch:{ Exception -> 0x010c }
        r7.append(r11);	 Catch:{ Exception -> 0x010c }
        r9 = "%";
        r7.append(r9);	 Catch:{ Exception -> 0x010c }
        r11 = r7.toString();	 Catch:{ Exception -> 0x010c }
        r7 = r14.getMessagesStorage();	 Catch:{ Exception -> 0x010c }
        r7 = r7.getDatabase();	 Catch:{ Exception -> 0x010c }
        r9 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?";
        r12 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x010c }
        r12[r5] = r11;	 Catch:{ Exception -> 0x010c }
        r7 = r7.queryFinalized(r9, r12);	 Catch:{ Exception -> 0x010c }
    L_0x00d3:
        r9 = r7.next();	 Catch:{ Exception -> 0x010c }
        if (r9 == 0) goto L_0x0104;
    L_0x00d9:
        r9 = r7.stringValue(r5);	 Catch:{ Exception -> 0x010c }
        r12 = "️";
        r13 = "";
        r9 = r9.replace(r12, r13);	 Catch:{ Exception -> 0x010c }
        r12 = r3.get(r9);	 Catch:{ Exception -> 0x010c }
        if (r12 == 0) goto L_0x00ec;
    L_0x00eb:
        goto L_0x00d3;
    L_0x00ec:
        r12 = java.lang.Boolean.valueOf(r10);	 Catch:{ Exception -> 0x010c }
        r3.put(r9, r12);	 Catch:{ Exception -> 0x010c }
        r12 = new org.telegram.messenger.MediaDataController$KeywordResult;	 Catch:{ Exception -> 0x010c }
        r12.<init>();	 Catch:{ Exception -> 0x010c }
        r12.emoji = r9;	 Catch:{ Exception -> 0x010c }
        r9 = r7.stringValue(r10);	 Catch:{ Exception -> 0x010c }
        r12.keyword = r9;	 Catch:{ Exception -> 0x010c }
        r2.add(r12);	 Catch:{ Exception -> 0x010c }
        goto L_0x00d3;
    L_0x0104:
        r7.dispose();	 Catch:{ Exception -> 0x010c }
        r7 = r11;
    L_0x0108:
        r0 = r0 + 1;
        goto L_0x0050;
    L_0x010c:
        r0 = move-exception;
        goto L_0x0110;
    L_0x010e:
        r0 = move-exception;
        r6 = r14;
    L_0x0110:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0113:
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$l58Gx_CJ42roaAOpSokhKzNLQYM;
        r3 = r19;
        r0.<init>(r3);
        java.util.Collections.sort(r2, r0);
        if (r20 == 0) goto L_0x0126;
    L_0x011f:
        r1.run(r2, r8);
        r20.countDown();
        goto L_0x012e;
    L_0x0126:
        r0 = new org.telegram.messenger.-$$Lambda$MediaDataController$Neb3j0YR-wvNNAvnRAFzOQ05wl8;
        r0.<init>(r1, r2, r8);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
    L_0x012e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$getEmojiSuggestions$123$MediaDataController(java.lang.String[], org.telegram.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    public /* synthetic */ void lambda$null$120$MediaDataController(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        int i = 0;
        while (i < strArr.length) {
            if (this.currentFetchingEmoji.get(strArr[i]) == null) {
                i++;
            } else {
                return;
            }
        }
        keywordResultCallback.run(arrayList, null);
    }

    static /* synthetic */ int lambda$null$121(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
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
        indexOf2 = keywordResult.keyword.length();
        int length = keywordResult2.keyword.length();
        if (indexOf2 < length) {
            return -1;
        }
        if (indexOf2 > length) {
            return 1;
        }
        return 0;
    }
}
