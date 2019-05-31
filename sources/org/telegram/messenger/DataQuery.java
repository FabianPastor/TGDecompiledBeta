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
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
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
    private static Comparator<MessageEntity> entityComparator = -$$Lambda$DataQuery$_GtBS_Mb74mqs3D5Wip5N2Gb424.INSTANCE;
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

    static /* synthetic */ void lambda$saveDraft$99(TLObject tLObject, TL_error tL_error) {
    }

    public static DataQuery getInstance(int i) {
        DataQuery dataQuery = Instance[i];
        if (dataQuery == null) {
            synchronized (DataQuery.class) {
                dataQuery = Instance[i];
                if (dataQuery == null) {
                    DataQuery[] dataQueryArr = Instance;
                    DataQuery dataQuery2 = new DataQuery(i);
                    dataQueryArr[i] = dataQuery2;
                    dataQuery = dataQuery2;
                }
            }
        }
        return dataQuery;
    }

    public DataQuery(int i) {
        this.currentAccount = i;
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
                    TLdeserialize.readAttachPath(serializedData, UserConfig.getInstance(this.currentAccount).clientUserId);
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
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

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00fc  */
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
        if (r7 != r10) goto L_0x00ac;
    L_0x0049:
        if (r1 == 0) goto L_0x005e;
    L_0x004b:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = NUM; // 0x7f0d0870 float:1.8746496E38 double:1.0531308447E-314;
        r4 = "RemovedFromFavorites";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2 = android.widget.Toast.makeText(r2, r3, r8);
        r2.show();
        goto L_0x0070;
    L_0x005e:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = NUM; // 0x7f0d00c5 float:1.8742514E38 double:1.053129875E-314;
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
        r3 = r6.currentAccount;
        r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);
        r4 = new org.telegram.messenger.-$$Lambda$DataQuery$s_0gO8L3a_nFeJVvPBKP6JduK9o;
        r5 = r15;
        r4.<init>(r13, r15, r2);
        r3.sendRequest(r2, r4);
        r2 = r6.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2 = r2.maxFaveStickersCount;
        goto L_0x00b4;
    L_0x00ac:
        r2 = r6.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2 = r2.maxRecentStickersCount;
    L_0x00b4:
        r3 = r6.recentStickers;
        r3 = r3[r7];
        r3 = r3.size();
        if (r3 > r2) goto L_0x00c0;
    L_0x00be:
        if (r1 == 0) goto L_0x00e7;
    L_0x00c0:
        if (r1 == 0) goto L_0x00c4;
    L_0x00c2:
        r2 = r0;
        goto L_0x00d5;
    L_0x00c4:
        r2 = r6.recentStickers;
        r3 = r2[r7];
        r2 = r2[r7];
        r2 = r2.size();
        r2 = r2 - r9;
        r2 = r3.remove(r2);
        r2 = (org.telegram.tgnet.TLRPC.Document) r2;
    L_0x00d5:
        r3 = r6.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getStorageQueue();
        r4 = new org.telegram.messenger.-$$Lambda$DataQuery$HfncZGNQeEbuO4TOGgzIo-2VPzk;
        r4.<init>(r13, r14, r2);
        r3.postRunnable(r4);
    L_0x00e7:
        if (r1 != 0) goto L_0x00fa;
    L_0x00e9:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r2.add(r0);
        r3 = 0;
        r5 = 0;
        r0 = r13;
        r1 = r14;
        r4 = r17;
        r0.processLoadedRecentDocuments(r1, r2, r3, r4, r5);
    L_0x00fa:
        if (r7 != r10) goto L_0x0115;
    L_0x00fc:
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.recentDocumentsDidLoad;
        r2 = new java.lang.Object[r10];
        r3 = java.lang.Boolean.valueOf(r8);
        r2[r8] = r3;
        r3 = java.lang.Integer.valueOf(r14);
        r2[r9] = r3;
        r0.postNotificationName(r1, r2);
    L_0x0115:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.addRecentSticker(int, java.lang.Object, org.telegram.tgnet.TLRPC$Document, int, boolean):void");
    }

    public /* synthetic */ void lambda$addRecentSticker$0$DataQuery(Object obj, TL_messages_faveSticker tL_messages_faveSticker, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text) && obj != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(obj, tL_messages_faveSticker);
        }
    }

    public /* synthetic */ void lambda$addRecentSticker$1$DataQuery(int i, Document document) {
        i = i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveGif, new -$$Lambda$DataQuery$2rJc9xeBmgwGexV2v1U_RebLTns(this, tL_messages_saveGif));
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$-3R_0wEE3expP9wo0aColXdZCOk(this, document));
    }

    public /* synthetic */ void lambda$removeRecentGif$2$DataQuery(TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text)) {
            FileRefController.getInstance(this.currentAccount).requestReference("gif", tL_messages_saveGif);
        }
    }

    public /* synthetic */ void lambda$removeRecentGif$3$DataQuery(Document document) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
        if (this.recentGifs.size() > MessagesController.getInstance(this.currentAccount).maxRecentGifsCount) {
            ArrayList arrayList = this.recentGifs;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$xvF6o-1_RcVDv47fzE1DMviRP_s(this, (Document) arrayList.remove(arrayList.size() - 1)));
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(document);
        processLoadedRecentDocuments(0, arrayList2, true, i, false);
    }

    public /* synthetic */ void lambda$addRecentGif$4$DataQuery(Document document) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet):void");
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$-TcC0mAoIoAzht6PKxrSMA0GNwQ(this, stickerSet));
            return;
        }
        TL_messages_getStickerSet tL_messages_getStickerSet = new TL_messages_getStickerSet();
        tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
        InputStickerSet inputStickerSet = tL_messages_getStickerSet.stickerset;
        inputStickerSet.id = stickerSet.id;
        inputStickerSet.access_hash = stickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickerSet, new -$$Lambda$DataQuery$sSt--gYzisWLp4pAgk5FJ0UZgFM(this));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$6$DataQuery(StickerSet stickerSet) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$mnsVNSCVdaMktJngurpfoZuqcbc(this, tL_messages_stickerSet));
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$5$DataQuery(TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    public /* synthetic */ void lambda$loadGroupStickerSet$8$DataQuery(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$Wjx9ZjzCtddl-eyuO29wwGUiJOs(this, (TL_messages_stickerSet) tLObject));
        }
    }

    public /* synthetic */ void lambda$null$7$DataQuery(TL_messages_stickerSet tL_messages_stickerSet) {
        this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tL_messages_stickerSet.set.id));
    }

    private void putSetToCache(TL_messages_stickerSet tL_messages_stickerSet) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$Gs_0kL3OC_eV-RiFuvATRDxcFsE(this, tL_messages_stickerSet));
    }

    public /* synthetic */ void lambda$putSetToCache$9$DataQuery(TL_messages_stickerSet tL_messages_stickerSet) {
        String str = "";
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            if (!getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id) && ((!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id)))) {
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
        if (r8 == 0) goto L_0x0036;
    L_0x0022:
        r8 = r5.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r8 = r8.getStorageQueue();
        r9 = new org.telegram.messenger.-$$Lambda$DataQuery$vDbI4SDDFGBUSoYXXeeYvbjHYJY;
        r9.<init>(r5, r7, r6);
        r8.postRunnable(r9);
        goto L_0x00ce;
    L_0x0036:
        r8 = r5.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getEmojiSettings(r8);
        if (r9 != 0) goto L_0x007b;
    L_0x003e:
        r2 = 0;
        if (r7 == 0) goto L_0x0049;
    L_0x0042:
        r9 = "lastGifLoadTime";
        r8 = r8.getLong(r9, r2);
        goto L_0x0061;
    L_0x0049:
        if (r6 != 0) goto L_0x0052;
    L_0x004b:
        r9 = "lastStickersLoadTime";
        r8 = r8.getLong(r9, r2);
        goto L_0x0061;
    L_0x0052:
        if (r6 != r1) goto L_0x005b;
    L_0x0054:
        r9 = "lastStickersLoadTimeMask";
        r8 = r8.getLong(r9, r2);
        goto L_0x0061;
    L_0x005b:
        r9 = "lastStickersLoadTimeFavs";
        r8 = r8.getLong(r9, r2);
    L_0x0061:
        r2 = java.lang.System.currentTimeMillis();
        r2 = r2 - r8;
        r8 = java.lang.Math.abs(r2);
        r2 = 3600000; // 0x36ee80 float:5.044674E-39 double:1.7786363E-317;
        r4 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x007b;
    L_0x0071:
        if (r7 == 0) goto L_0x0076;
    L_0x0073:
        r5.loadingRecentGifs = r0;
        goto L_0x007a;
    L_0x0076:
        r7 = r5.loadingRecentStickers;
        r7[r6] = r0;
    L_0x007a:
        return;
    L_0x007b:
        if (r7 == 0) goto L_0x0099;
    L_0x007d:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs;
        r8.<init>();
        r9 = r5.recentGifs;
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        r9 = r5.currentAccount;
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9);
        r0 = new org.telegram.messenger.-$$Lambda$DataQuery$5cEA9KV-G5ibi7bkh4w7n6aFdJ8;
        r0.<init>(r5, r6, r7);
        r9.sendRequest(r8, r0);
        goto L_0x00ce;
    L_0x0099:
        r8 = 2;
        if (r6 != r8) goto L_0x00ac;
    L_0x009c:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers;
        r8.<init>();
        r9 = r5.recentStickers;
        r9 = r9[r6];
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        goto L_0x00c0;
    L_0x00ac:
        r8 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers;
        r8.<init>();
        r9 = r5.recentStickers;
        r9 = r9[r6];
        r9 = calcDocumentsHash(r9);
        r8.hash = r9;
        if (r6 != r1) goto L_0x00be;
    L_0x00bd:
        r0 = 1;
    L_0x00be:
        r8.attached = r0;
    L_0x00c0:
        r9 = r5.currentAccount;
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9);
        r0 = new org.telegram.messenger.-$$Lambda$DataQuery$5qGaDi2FRyj1QCnrOleMSVLz1k4;
        r0.<init>(r5, r6, r7);
        r9.sendRequest(r8, r0);
    L_0x00ce:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.loadRecents(int, boolean, boolean, boolean):void");
    }

    public /* synthetic */ void lambda$loadRecents$11$DataQuery(boolean z, int i) {
        int i2 = z ? 2 : i == 0 ? 3 : i == 1 ? 4 : 5;
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$4Ct3KKcQG3d2vX_KGUT-tJNgszQ(this, z, arrayList, i));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$null$10$DataQuery(boolean z, ArrayList arrayList, int i) {
        if (z) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[i] = arrayList;
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        loadRecents(i, z, false, false);
    }

    public /* synthetic */ void lambda$loadRecents$12$DataQuery(int i, boolean z, TLObject tLObject, TL_error tL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TL_messages_savedGifs ? ((TL_messages_savedGifs) tLObject).gifs : null, z, 0, true);
    }

    public /* synthetic */ void lambda$loadRecents$13$DataQuery(int i, boolean z, TLObject tLObject, TL_error tL_error) {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$uuHSgHUH35SvbaUdMusqTnQ-iPk(this, z, i, arrayList, z2, i2));
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$7M05Rnf_3mDk_ioxbIIY86tv1DE(this, z, i, arrayList));
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$14$DataQuery(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3 = i;
        ArrayList arrayList2 = arrayList;
        String str = "";
        try {
            int i4;
            StringBuilder stringBuilder;
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            int i5 = 2;
            if (z) {
                i4 = MessagesController.getInstance(this.currentAccount).maxRecentGifsCount;
            } else if (i3 == 2) {
                i4 = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
            } else {
                i4 = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
            }
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            i3 = z ? 2 : i3 == 0 ? 3 : i3 == 1 ? 4 : 5;
            if (z2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM web_recent_v3 WHERE type = ");
                stringBuilder.append(i3);
                database.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
            int i6 = 0;
            while (i6 < size) {
                if (i6 == i4) {
                    break;
                }
                Document document = (Document) arrayList2.get(i6);
                executeFast.requery();
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(document.id);
                executeFast.bindString(1, stringBuilder.toString());
                executeFast.bindInteger(i5, i3);
                executeFast.bindString(3, str);
                executeFast.bindString(4, str);
                executeFast.bindString(5, str);
                executeFast.bindInteger(6, 0);
                executeFast.bindInteger(7, 0);
                executeFast.bindInteger(8, 0);
                executeFast.bindInteger(9, i2 != 0 ? i2 : size - i6);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(10, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                i6++;
                i5 = 2;
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() >= i4) {
                database.beginTransaction();
                while (i4 < arrayList.size()) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                    stringBuilder2.append(((Document) arrayList2.get(i4)).id);
                    stringBuilder2.append("' AND type = ");
                    stringBuilder2.append(i3);
                    database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    i4++;
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$processLoadedRecentDocuments$15$DataQuery(boolean z, int i, ArrayList arrayList) {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        }
    }

    public void reorderStickers(int i, ArrayList<Long> arrayList) {
        Collections.sort(this.stickerSets[i], new -$$Lambda$DataQuery$dtOJW5lUpjPVOn6aZw8KSWcnFQs(arrayList));
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(z));
            loadStickers(z, false, true);
        }
    }

    public void loadFeaturedStickers(boolean z, boolean z2) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$_js4uGbpT5ecS0xV_WWy27wFeXg(this));
            } else {
                int i;
                TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers = new TL_messages_getFeaturedStickers();
                if (z2) {
                    i = 0;
                } else {
                    i = this.loadFeaturedHash;
                }
                tL_messages_getFeaturedStickers.hash = i;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getFeaturedStickers, new -$$Lambda$DataQuery$KjoE15J_7Y5rDsfACZqjBs6rNXk(this, tL_messages_getFeaturedStickers));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084 A:{Splitter:B:3:0x0019, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084 A:{Splitter:B:3:0x0019, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00a5  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:29:0x0073, code skipped:
            r5 = move-exception;
     */
    /* JADX WARNING: Missing block: B:30:0x0074, code skipped:
            r9 = r5;
            r5 = r0;
            r0 = r3;
            r3 = r4;
            r4 = r9;
     */
    /* JADX WARNING: Missing block: B:35:0x0084, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:36:0x0086, code skipped:
            r4 = th;
     */
    /* JADX WARNING: Missing block: B:37:0x0087, code skipped:
            r5 = r0;
     */
    /* JADX WARNING: Missing block: B:47:0x0097, code skipped:
            r0.dispose();
     */
    /* JADX WARNING: Missing block: B:52:0x00a5, code skipped:
            r3.dispose();
     */
    public /* synthetic */ void lambda$loadFeaturedStickers$17$DataQuery() {
        /*
        r10 = this;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = 0;
        r1 = 0;
        r3 = r10.currentAccount;	 Catch:{ Throwable -> 0x008e }
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);	 Catch:{ Throwable -> 0x008e }
        r3 = r3.getDatabase();	 Catch:{ Throwable -> 0x008e }
        r4 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1";
        r5 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x008e }
        r3 = r3.queryFinalized(r4, r5);	 Catch:{ Throwable -> 0x008e }
        r4 = r3.next();	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        if (r4 == 0) goto L_0x007a;
    L_0x001f:
        r4 = r3.byteBufferValue(r1);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        if (r4 == 0) goto L_0x0047;
    L_0x0025:
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r5.<init>();	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r0 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0044, all -> 0x0084 }
        r6 = 0;
    L_0x002f:
        if (r6 >= r0) goto L_0x003f;
    L_0x0031:
        r7 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0044, all -> 0x0084 }
        r7 = org.telegram.tgnet.TLRPC.StickerSetCovered.TLdeserialize(r4, r7, r1);	 Catch:{ Throwable -> 0x0044, all -> 0x0084 }
        r5.add(r7);	 Catch:{ Throwable -> 0x0044, all -> 0x0084 }
        r6 = r6 + 1;
        goto L_0x002f;
    L_0x003f:
        r4.reuse();	 Catch:{ Throwable -> 0x0044, all -> 0x0084 }
        r0 = r5;
        goto L_0x0047;
    L_0x0044:
        r0 = move-exception;
        r4 = r0;
        goto L_0x0088;
    L_0x0047:
        r4 = 1;
        r4 = r3.byteBufferValue(r4);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        if (r4 == 0) goto L_0x0066;
    L_0x004e:
        r5 = r4.readInt32(r1);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r6 = 0;
    L_0x0053:
        if (r6 >= r5) goto L_0x0063;
    L_0x0055:
        r7 = r4.readInt64(r1);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r7 = java.lang.Long.valueOf(r7);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r2.add(r7);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r6 = r6 + 1;
        goto L_0x0053;
    L_0x0063:
        r4.reuse();	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
    L_0x0066:
        r4 = 2;
        r4 = r3.intValue(r4);	 Catch:{ Throwable -> 0x0086, all -> 0x0084 }
        r1 = r10.calcFeaturedStickersHash(r0);	 Catch:{ Throwable -> 0x0073, all -> 0x0084 }
        r9 = r4;
        r4 = r1;
        r1 = r9;
        goto L_0x007b;
    L_0x0073:
        r5 = move-exception;
        r9 = r5;
        r5 = r0;
        r0 = r3;
        r3 = r4;
        r4 = r9;
        goto L_0x0092;
    L_0x007a:
        r4 = 0;
    L_0x007b:
        if (r3 == 0) goto L_0x0080;
    L_0x007d:
        r3.dispose();
    L_0x0080:
        r5 = r4;
        r4 = r1;
        r1 = r0;
        goto L_0x009d;
    L_0x0084:
        r0 = move-exception;
        goto L_0x00a3;
    L_0x0086:
        r4 = move-exception;
        r5 = r0;
    L_0x0088:
        r0 = r3;
        goto L_0x0091;
    L_0x008a:
        r1 = move-exception;
        r3 = r0;
        r0 = r1;
        goto L_0x00a3;
    L_0x008e:
        r3 = move-exception;
        r5 = r0;
        r4 = r3;
    L_0x0091:
        r3 = 0;
    L_0x0092:
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x008a }
        if (r0 == 0) goto L_0x009a;
    L_0x0097:
        r0.dispose();
    L_0x009a:
        r4 = r3;
        r1 = r5;
        r5 = 0;
    L_0x009d:
        r3 = 1;
        r0 = r10;
        r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5);
        return;
    L_0x00a3:
        if (r3 == 0) goto L_0x00a8;
    L_0x00a5:
        r3.dispose();
    L_0x00a8:
        goto L_0x00aa;
    L_0x00a9:
        throw r0;
    L_0x00aa:
        goto L_0x00a9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$loadFeaturedStickers$17$DataQuery():void");
    }

    public /* synthetic */ void lambda$loadFeaturedStickers$19$DataQuery(TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$LGTT4xzHAhL56fxY9zS2yMdk6qA(this, tLObject, tL_messages_getFeaturedStickers));
    }

    public /* synthetic */ void lambda$null$18$DataQuery(TLObject tLObject, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TL_messages_featuredStickers) {
            TL_messages_featuredStickers tL_messages_featuredStickers = (TL_messages_featuredStickers) tLObject2;
            processLoadedFeaturedStickers(tL_messages_featuredStickers.sets, tL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), tL_messages_getFeaturedStickers.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, int i2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$Br07Yue1FWlAHhubZJ8mcHmpI1c(this));
        Utilities.stageQueue.postRunnable(new -$$Lambda$DataQuery$cOH-T13u95HL6anewGDnniRkdeE(this, z, arrayList, i, i2, arrayList2));
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$20$DataQuery() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    public /* synthetic */ void lambda$processLoadedFeaturedStickers$24$DataQuery(boolean z, ArrayList arrayList, int i, int i2, ArrayList arrayList2) {
        long j = 1000;
        if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
            -$$Lambda$DataQuery$kRg5HaPmcNQUyKbUY_yQPqEg7dE -__lambda_dataquery_krg5hapmcnquykbuy_yqpqeg7de = new -$$Lambda$DataQuery$kRg5HaPmcNQUyKbUY_yQPqEg7dE(this, arrayList, i2);
            if (arrayList != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(-__lambda_dataquery_krg5hapmcnquykbuy_yqpqeg7de, j);
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$dAuSsiUmfJ4Spuw_RVTqCszQl9Y(this, arrayList2, longSparseArray, arrayList3, i2, i));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$W2Cvj9Df-M9tvBcZ-35ZTktPTCg(this, i));
            putFeaturedStickersToCache(null, null, i, 0);
        }
    }

    public /* synthetic */ void lambda$null$21$DataQuery(ArrayList arrayList, int i) {
        if (!(arrayList == null || i == 0)) {
            this.loadFeaturedHash = i;
        }
        loadFeaturedStickers(false, false);
    }

    public /* synthetic */ void lambda$null$22$DataQuery(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, int i, int i2) {
        this.unreadStickerSets = arrayList;
        this.featuredStickerSetsById = longSparseArray;
        this.featuredStickerSets = arrayList2;
        this.loadFeaturedHash = i;
        this.loadFeaturedDate = i2;
        loadStickers(3, true, false);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    public /* synthetic */ void lambda$null$23$DataQuery(int i) {
        this.loadFeaturedDate = i;
    }

    private void putFeaturedStickersToCache(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$EvRKZ0icyHpXu5syph8WWuRUigE(this, arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, i2));
    }

    public /* synthetic */ void lambda$putFeaturedStickersToCache$25$DataQuery(ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
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
        SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (z) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_readFeaturedStickers(), -$$Lambda$DataQuery$TygWBWHmoEKh7kTWN3Sp59N9f8M.INSTANCE);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readFeaturedStickers, -$$Lambda$DataQuery$CoOuaI5Ui4g_M8Hv23Yz8tRYQcY.INSTANCE);
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$g0o9Z-244wbMtOYxpACC2qzITTc(this, j), 1000);
        }
    }

    public /* synthetic */ void lambda$markFaturedStickersByIdAsRead$28$DataQuery(long j) {
        this.unreadStickerSets.remove(Long.valueOf(j));
        this.readingStickerSets.remove(Long.valueOf(j));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
            return;
        }
        TL_messages_getArchivedStickers tL_messages_getArchivedStickers = new TL_messages_getArchivedStickers();
        tL_messages_getArchivedStickers.limit = 0;
        if (i != 1) {
            z2 = false;
        }
        tL_messages_getArchivedStickers.masks = z2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getArchivedStickers, new -$$Lambda$DataQuery$vXxSlr6eK9Z6xNmLs0Dj7UWvraQ(this, i));
    }

    public /* synthetic */ void lambda$loadArchivedStickersCount$30$DataQuery(int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$ii2s4yGT-QcUcBrCrSZAhn6DFB4(this, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$29$DataQuery(TL_error tL_error, TLObject tLObject, int i) {
        if (tL_error == null) {
            TL_messages_archivedStickers tL_messages_archivedStickers = (TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tL_messages_archivedStickers.count;
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("archivedStickersCount");
            stringBuilder.append(i);
            edit.putInt(stringBuilder.toString(), tL_messages_archivedStickers.count).commit();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(int i, TL_messages_allStickers tL_messages_allStickers) {
        messages_AllStickers messages_allstickers = tL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (messages_allstickers.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), messages_allstickers.hash);
            return;
        }
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
            -$$Lambda$DataQuery$w1XixRjvar_wrwBdg1yg9ZISWPKs -__lambda_dataquery_w1xixrjvar_wrwbdg1yg9ziswpks = r0;
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            -$$Lambda$DataQuery$w1XixRjvar_wrwBdg1yg9ZISWPKs -__lambda_dataquery_w1xixrjvar_wrwbdg1yg9ziswpks2 = new -$$Lambda$DataQuery$w1XixRjvar_wrwBdg1yg9ZISWPKs(this, arrayList, i2, longSparseArray, stickerSet, tL_messages_allStickers, i);
            instance.sendRequest(tL_messages_getStickerSet, -__lambda_dataquery_w1xixrjvar_wrwbdg1yg9ziswpks);
            i2++;
            j = 1000;
        }
    }

    public /* synthetic */ void lambda$processLoadStickersResponse$32$DataQuery(ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$m9Jrl7_U-_SUx_Gpw56kknTexVQ(this, tLObject, arrayList, i, longSparseArray, stickerSet, tL_messages_allStickers, i2));
    }

    public /* synthetic */ void lambda$null$31$DataQuery(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers, int i2) {
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
            } else if (this.featuredStickerSets.isEmpty() || !MessagesController.getInstance(this.currentAccount).preloadFeaturedStickers) {
                return;
            }
            this.loadingStickers[i] = true;
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$xa4Sj3vXQ9Lm0vsRxYjFZWyzhng(this, i));
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getAllStickers, new -$$Lambda$DataQuery$rOQjG3YS0y6BqT7zHxJCSwr131o(this, i, i2));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073 A:{Splitter:B:3:0x0025, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073 A:{Splitter:B:3:0x0025, ExcHandler: all (th java.lang.Throwable)} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0094  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:23:0x0063, code skipped:
            r4 = move-exception;
     */
    /* JADX WARNING: Missing block: B:24:0x0064, code skipped:
            r11 = r4;
            r4 = r0;
            r0 = r2;
            r2 = r11;
     */
    /* JADX WARNING: Missing block: B:29:0x0073, code skipped:
            r13 = th;
     */
    /* JADX WARNING: Missing block: B:30:0x0074, code skipped:
            r0 = r2;
     */
    /* JADX WARNING: Missing block: B:31:0x0076, code skipped:
            r3 = move-exception;
     */
    /* JADX WARNING: Missing block: B:32:0x0077, code skipped:
            r4 = r0;
            r0 = r2;
            r2 = r3;
     */
    /* JADX WARNING: Missing block: B:45:0x0094, code skipped:
            r0.dispose();
     */
    public /* synthetic */ void lambda$loadStickers$33$DataQuery(int r13) {
        /*
        r12 = this;
        r0 = 0;
        r1 = 0;
        r2 = r12.currentAccount;	 Catch:{ Throwable -> 0x007d }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Throwable -> 0x007d }
        r2 = r2.getDatabase();	 Catch:{ Throwable -> 0x007d }
        r3 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x007d }
        r3.<init>();	 Catch:{ Throwable -> 0x007d }
        r4 = "SELECT data, date, hash FROM stickers_v2 WHERE id = ";
        r3.append(r4);	 Catch:{ Throwable -> 0x007d }
        r4 = r13 + 1;
        r3.append(r4);	 Catch:{ Throwable -> 0x007d }
        r3 = r3.toString();	 Catch:{ Throwable -> 0x007d }
        r4 = new java.lang.Object[r1];	 Catch:{ Throwable -> 0x007d }
        r2 = r2.queryFinalized(r3, r4);	 Catch:{ Throwable -> 0x007d }
        r3 = r2.next();	 Catch:{ Throwable -> 0x0076, all -> 0x0073 }
        if (r3 == 0) goto L_0x0069;
    L_0x002b:
        r3 = r2.byteBufferValue(r1);	 Catch:{ Throwable -> 0x0076, all -> 0x0073 }
        if (r3 == 0) goto L_0x0056;
    L_0x0031:
        r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0076, all -> 0x0073 }
        r4.<init>();	 Catch:{ Throwable -> 0x0076, all -> 0x0073 }
        r0 = r3.readInt32(r1);	 Catch:{ Throwable -> 0x0050, all -> 0x0073 }
        r5 = 0;
    L_0x003b:
        if (r5 >= r0) goto L_0x004b;
    L_0x003d:
        r6 = r3.readInt32(r1);	 Catch:{ Throwable -> 0x0050, all -> 0x0073 }
        r6 = org.telegram.tgnet.TLRPC.TL_messages_stickerSet.TLdeserialize(r3, r6, r1);	 Catch:{ Throwable -> 0x0050, all -> 0x0073 }
        r4.add(r6);	 Catch:{ Throwable -> 0x0050, all -> 0x0073 }
        r5 = r5 + 1;
        goto L_0x003b;
    L_0x004b:
        r3.reuse();	 Catch:{ Throwable -> 0x0050, all -> 0x0073 }
        r0 = r4;
        goto L_0x0056;
    L_0x0050:
        r0 = move-exception;
        r3 = 0;
        r11 = r2;
        r2 = r0;
        r0 = r11;
        goto L_0x0080;
    L_0x0056:
        r3 = 1;
        r3 = r2.intValue(r3);	 Catch:{ Throwable -> 0x0076, all -> 0x0073 }
        r1 = calcStickersHash(r0);	 Catch:{ Throwable -> 0x0063, all -> 0x0073 }
        r11 = r3;
        r3 = r1;
        r1 = r11;
        goto L_0x006a;
    L_0x0063:
        r4 = move-exception;
        r11 = r4;
        r4 = r0;
        r0 = r2;
        r2 = r11;
        goto L_0x0080;
    L_0x0069:
        r3 = 0;
    L_0x006a:
        if (r2 == 0) goto L_0x006f;
    L_0x006c:
        r2.dispose();
    L_0x006f:
        r7 = r0;
        r9 = r1;
        r10 = r3;
        goto L_0x008b;
    L_0x0073:
        r13 = move-exception;
        r0 = r2;
        goto L_0x0092;
    L_0x0076:
        r3 = move-exception;
        r4 = r0;
        r0 = r2;
        r2 = r3;
        goto L_0x007f;
    L_0x007b:
        r13 = move-exception;
        goto L_0x0092;
    L_0x007d:
        r2 = move-exception;
        r4 = r0;
    L_0x007f:
        r3 = 0;
    L_0x0080:
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x007b }
        if (r0 == 0) goto L_0x0088;
    L_0x0085:
        r0.dispose();
    L_0x0088:
        r9 = r3;
        r7 = r4;
        r10 = 0;
    L_0x008b:
        r8 = 1;
        r5 = r12;
        r6 = r13;
        r5.processLoadedStickers(r6, r7, r8, r9, r10);
        return;
    L_0x0092:
        if (r0 == 0) goto L_0x0097;
    L_0x0094:
        r0.dispose();
    L_0x0097:
        goto L_0x0099;
    L_0x0098:
        throw r13;
    L_0x0099:
        goto L_0x0098;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$loadStickers$33$DataQuery(int):void");
    }

    public /* synthetic */ void lambda$loadStickers$35$DataQuery(int i, int i2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$iGCHItKH69AqIe-44thbbHy-0Sg(this, tLObject, i, i2));
    }

    public /* synthetic */ void lambda$null$34$DataQuery(TLObject tLObject, int i, int i2) {
        if (tLObject instanceof TL_messages_allStickers) {
            processLoadStickersResponse(i, (TL_messages_allStickers) tLObject);
            return;
        }
        processLoadedStickers(i, null, false, (int) (System.currentTimeMillis() / 1000), i2);
    }

    private void putStickersToCache(int i, ArrayList<TL_messages_stickerSet> arrayList, int i2, int i3) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$HuXXMuf2NeuYCbcUMY_TfU-FwGA(this, arrayList != null ? new ArrayList(arrayList) : null, i, i2, i3));
    }

    public /* synthetic */ void lambda$putStickersToCache$36$DataQuery(ArrayList arrayList, int i, int i2, int i3) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
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
        SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$9vMJ1w-Dj5oCt4zZwXAybfYG_nU(this, i));
        Utilities.stageQueue.postRunnable(new -$$Lambda$DataQuery$inBvXSiWQIx1Y9BuZIwlL6K4fAA(this, z, arrayList, i2, i3, i));
    }

    public /* synthetic */ void lambda$processLoadedStickers$37$DataQuery(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
    }

    public /* synthetic */ void lambda$processLoadedStickers$41$DataQuery(boolean z, ArrayList arrayList, int i, int i2, int i3) {
        ArrayList arrayList2 = arrayList;
        int i4 = i;
        int i5 = i2;
        int i6 = i3;
        long j = 1000;
        if ((z && (arrayList2 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i4)) >= 3600)) || (!z && arrayList2 == null && i5 == 0)) {
            -$$Lambda$DataQuery$p-_TrjJXc0wEGRq96cujq9LPyXc -__lambda_dataquery_p-_trjjxc0wegrq96cujq9lpyxc = new -$$Lambda$DataQuery$p-_TrjJXc0wEGRq96cujq9LPyXc(this, arrayList2, i5, i6);
            if (arrayList2 != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(-__lambda_dataquery_p-_trjjxc0wegrq96cujq9lpyxc, j);
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$tJcdmi2t6adWqXKlQnAh02D50rs(this, i3, longSparseArray, hashMap, arrayList3, i2, i, hashMap2, longSparseArray3));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$8k_rAECt-51WNomiJ13t8JNjAGU(this, i6, i4));
            putStickersToCache(i6, null, i4, 0);
        }
    }

    public /* synthetic */ void lambda$null$38$DataQuery(ArrayList arrayList, int i, int i2) {
        if (!(arrayList == null || i == 0)) {
            this.loadHash[i2] = i;
        }
        loadStickers(i2, false, false);
    }

    public /* synthetic */ void lambda$null$39$DataQuery(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, int i2, int i3, HashMap hashMap2, LongSparseArray longSparseArray2) {
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    public /* synthetic */ void lambda$null$40$DataQuery(int i, int i2) {
        this.loadDate[i] = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x00a1  */
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
        if (r13 == 0) goto L_0x00b8;
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
        r12 = r10.currentAccount;
        r12 = org.telegram.messenger.NotificationCenter.getInstance(r12);
        r3 = org.telegram.messenger.NotificationCenter.stickersDidLoad;
        r4 = new java.lang.Object[r1];
        r5 = java.lang.Integer.valueOf(r2);
        r4[r11] = r5;
        r12.postNotificationName(r3, r4);
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
        r12.<init>();
        r12.stickerset = r0;
        if (r13 != r1) goto L_0x00a2;
    L_0x00a1:
        r11 = 1;
    L_0x00a2:
        r12.archived = r11;
        r11 = r10.currentAccount;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11);
        r6 = new org.telegram.messenger.-$$Lambda$DataQuery$qjcWF-Rlu499xbvvlZfzwoJxsQI;
        r0 = r6;
        r1 = r10;
        r3 = r13;
        r4 = r14;
        r5 = r15;
        r0.<init>(r1, r2, r3, r4, r5);
        r11.sendRequest(r12, r6);
        goto L_0x00cd;
    L_0x00b8:
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_uninstallStickerSet;
        r13.<init>();
        r13.stickerset = r0;
        r14 = r10.currentAccount;
        r14 = org.telegram.tgnet.ConnectionsManager.getInstance(r14);
        r15 = new org.telegram.messenger.-$$Lambda$DataQuery$DpM7Y4tX6aFYwIY3CiDaFq5CjNE;
        r15.<init>(r10, r12, r11, r2);
        r14.sendRequest(r13, r15);
    L_0x00cd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.removeStickersSet(android.content.Context, org.telegram.tgnet.TLRPC$StickerSet, int, org.telegram.ui.ActionBar.BaseFragment, boolean):void");
    }

    public /* synthetic */ void lambda$removeStickersSet$44$DataQuery(int i, int i2, BaseFragment baseFragment, boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$O3U-V0n7MR7QFur96I7yoxXpLvQ(this, tLObject, i, i2, baseFragment, z));
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$XkVtXDXzkHZcNcyTDn3dq8s1agk(this, i), 1000);
    }

    public /* synthetic */ void lambda$null$42$DataQuery(TLObject tLObject, int i, int i2, BaseFragment baseFragment, boolean z) {
        if (tLObject instanceof TL_messages_stickerSetInstallResultArchive) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(i));
            if (i2 != 1 && baseFragment != null && baseFragment.getParentActivity() != null) {
                baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), z ? baseFragment : null, ((TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
            }
        }
    }

    public /* synthetic */ void lambda$null$43$DataQuery(int i) {
        loadStickers(i, false, false);
    }

    public /* synthetic */ void lambda$removeStickersSet$46$DataQuery(StickerSet stickerSet, Context context, int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$x29XwwId3_CQasQvc9QUrJecg8c(this, tL_error, stickerSet, context, i));
    }

    public /* synthetic */ void lambda$null$45$DataQuery(TL_error tL_error, StickerSet stickerSet, Context context, int i) {
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
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        int[] iArr;
        if (str != null) {
            if (i5 != 0) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
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
            NotificationCenter instance;
            Object[] objArr;
            if (i4 == 1) {
                this.lastReturnedNum++;
                if (this.lastReturnedNum < this.searchResultMessages.size()) {
                    messageObject = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    instance = NotificationCenter.getInstance(this.currentAccount);
                    i4 = NotificationCenter.chatSearchResultsAvailable;
                    objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i);
                    objArr[1] = Integer.valueOf(messageObject.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    iArr = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr[0] + iArr[1]);
                    instance.postNotificationName(i4, objArr);
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
                    instance = NotificationCenter.getInstance(this.currentAccount);
                    i4 = NotificationCenter.chatSearchResultsAvailable;
                    objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i);
                    objArr[1] = Integer.valueOf(messageObject.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    iArr = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr[0] + iArr[1]);
                    instance.postNotificationName(i4, objArr);
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
                InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) j4);
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
                        tL_messages_search.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user2);
                        tL_messages_search.flags = 1 | tL_messages_search.flags;
                    }
                    tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
                    ConnectionsManager instance2 = ConnectionsManager.getInstance(this.currentAccount);
                    -$$Lambda$DataQuery$X_xjH4zrLbvar_x7_YlP3_d_hOC0 -__lambda_dataquery_x_xjh4zrlbvar_x7_ylp3_d_hoc0 = r0;
                    -$$Lambda$DataQuery$X_xjH4zrLbvar_x7_YlP3_d_hOC0 -__lambda_dataquery_x_xjh4zrlbvar_x7_ylp3_d_hoCLASSNAME = new -$$Lambda$DataQuery$X_xjH4zrLbvar_x7_YlP3_d_hOC0(this, j2, tL_messages_search, j, i, i2, user);
                    this.mergeReqId = instance2.sendRequest(tL_messages_search, -__lambda_dataquery_x_xjh4zrlbvar_x7_ylp3_d_hoc0, 2);
                    return;
                }
                return;
            }
            this.lastMergeDialogId = 0;
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLObject tL_messages_search2 = new TL_messages_search();
        tL_messages_search2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) j3);
        if (tL_messages_search2.peer != null) {
            tL_messages_search2.limit = 21;
            if (str2 != null) {
                str4 = str2;
            }
            tL_messages_search2.q = str4;
            tL_messages_search2.offset_id = i3;
            if (user2 != null) {
                tL_messages_search2.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user2);
                tL_messages_search2.flags |= 1;
            }
            tL_messages_search2.filter = new TL_inputMessagesFilterEmpty();
            int i7 = this.lastReqId + 1;
            this.lastReqId = i7;
            this.lastSearchQuery = str2;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search2, new -$$Lambda$DataQuery$YQtVaGINuYPbSOpEA3NgINY1Mog(this, i7, tL_messages_search2, j3, j, i, j2, user), 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInChat$48$DataQuery(long j, TL_messages_search tL_messages_search, long j2, int i, int i2, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$eJFDqCNJQlVs4Vxl5xT4VYdEsgw(this, j, tLObject, tL_messages_search, j2, i, i2, user));
    }

    public /* synthetic */ void lambda$null$47$DataQuery(long j, TLObject tLObject, TL_messages_search tL_messages_search, long j2, int i, int i2, User user) {
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

    public /* synthetic */ void lambda$searchMessagesInChat$50$DataQuery(int i, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$2id2umTzhmpQDk5s-dtlRZWpox0(this, i, tLObject, tL_messages_search, j, j2, i2, j3, user));
    }

    public /* synthetic */ void lambda$null$49$DataQuery(int i, TLObject tLObject, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user) {
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
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
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
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i2), Integer.valueOf(0), Integer.valueOf(getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                } else if (obj != null) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = (MessageObject) this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                    int i5 = NotificationCenter.chatSearchResultsAvailable;
                    Object[] objArr = new Object[6];
                    objArr[0] = Integer.valueOf(i2);
                    objArr[1] = Integer.valueOf(messageObject2.getId());
                    objArr[2] = Integer.valueOf(getMask());
                    objArr[3] = Long.valueOf(messageObject2.getDialogId());
                    objArr[4] = Integer.valueOf(this.lastReturnedNum);
                    int[] iArr2 = this.messagesSearchCount;
                    objArr[5] = Integer.valueOf(iArr2[0] + iArr2[1]);
                    instance.postNotificationName(i5, objArr);
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
        int i6 = i3;
        int i7 = (int) j;
        boolean z = i7 < 0 && ChatObject.isChannel(-i7, this.currentAccount);
        int i8;
        if (i4 != 0 || i7 == 0) {
            int i9 = i;
            int i10 = i2;
            i8 = i5;
            loadMediaDatabase(j, i, i2, i3, i5, z, i4);
        } else {
            TL_messages_search tL_messages_search = new TL_messages_search();
            tL_messages_search.limit = i;
            tL_messages_search.offset_id = i2;
            if (i6 == 0) {
                tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
            } else if (i6 == 1) {
                tL_messages_search.filter = new TL_inputMessagesFilterDocument();
            } else if (i6 == 2) {
                tL_messages_search.filter = new TL_inputMessagesFilterRoundVoice();
            } else if (i6 == 3) {
                tL_messages_search.filter = new TL_inputMessagesFilterUrl();
            } else if (i6 == 4) {
                tL_messages_search.filter = new TL_inputMessagesFilterMusic();
            }
            tL_messages_search.q = "";
            tL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i7);
            if (tL_messages_search.peer != null) {
                i8 = i5;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new -$$Lambda$DataQuery$sZkwI2OJzSWG6qrblxHbvHglZsU(this, j, i, i2, i3, i8, z)), i8);
            }
        }
    }

    public /* synthetic */ void lambda$loadMedia$51$DataQuery(long j, int i, int i2, int i3, int i4, boolean z, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            long j2 = j;
            MessagesController.getInstance(this.currentAccount).removeDeletedMessagesFromArray(j, messages_messages.messages);
            processLoadedMedia(messages_messages, j, i, i2, i3, 0, i4, z, messages_messages.messages.size() == 0);
            return;
        }
    }

    public void getMediaCounts(long j, int i) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$NQ5MkErWobCUOgqYnlpeC3jx-Y8(this, j, i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x0132 A:{Catch:{ Exception -> 0x0186 }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x012f A:{Catch:{ Exception -> 0x0186 }} */
    public /* synthetic */ void lambda$getMediaCounts$56$DataQuery(long r22, int r24) {
        /*
        r21 = this;
        r7 = r21;
        r8 = r22;
        r0 = 5;
        r10 = new int[r0];	 Catch:{ Exception -> 0x0186 }
        r11 = -1;
        r12 = 0;
        r10[r12] = r11;	 Catch:{ Exception -> 0x0186 }
        r13 = 1;
        r10[r13] = r11;	 Catch:{ Exception -> 0x0186 }
        r14 = 2;
        r10[r14] = r11;	 Catch:{ Exception -> 0x0186 }
        r15 = 3;
        r10[r15] = r11;	 Catch:{ Exception -> 0x0186 }
        r5 = 4;
        r10[r5] = r11;	 Catch:{ Exception -> 0x0186 }
        r6 = new int[r0];	 Catch:{ Exception -> 0x0186 }
        r6[r12] = r11;	 Catch:{ Exception -> 0x0186 }
        r6[r13] = r11;	 Catch:{ Exception -> 0x0186 }
        r6[r14] = r11;	 Catch:{ Exception -> 0x0186 }
        r6[r15] = r11;	 Catch:{ Exception -> 0x0186 }
        r6[r5] = r11;	 Catch:{ Exception -> 0x0186 }
        r4 = new int[r0];	 Catch:{ Exception -> 0x0186 }
        r4[r12] = r12;	 Catch:{ Exception -> 0x0186 }
        r4[r13] = r12;	 Catch:{ Exception -> 0x0186 }
        r4[r14] = r12;	 Catch:{ Exception -> 0x0186 }
        r4[r15] = r12;	 Catch:{ Exception -> 0x0186 }
        r4[r5] = r12;	 Catch:{ Exception -> 0x0186 }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x0186 }
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);	 Catch:{ Exception -> 0x0186 }
        r1 = r1.getDatabase();	 Catch:{ Exception -> 0x0186 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0186 }
        r3 = "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d";
        r5 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0186 }
        r16 = java.lang.Long.valueOf(r22);	 Catch:{ Exception -> 0x0186 }
        r5[r12] = r16;	 Catch:{ Exception -> 0x0186 }
        r2 = java.lang.String.format(r2, r3, r5);	 Catch:{ Exception -> 0x0186 }
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0186 }
        r1 = r1.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x0186 }
    L_0x004f:
        r2 = r1.next();	 Catch:{ Exception -> 0x0186 }
        if (r2 == 0) goto L_0x006c;
    L_0x0055:
        r2 = r1.intValue(r12);	 Catch:{ Exception -> 0x0186 }
        if (r2 < 0) goto L_0x004f;
    L_0x005b:
        if (r2 >= r0) goto L_0x004f;
    L_0x005d:
        r3 = r1.intValue(r13);	 Catch:{ Exception -> 0x0186 }
        r10[r2] = r3;	 Catch:{ Exception -> 0x0186 }
        r6[r2] = r3;	 Catch:{ Exception -> 0x0186 }
        r3 = r1.intValue(r14);	 Catch:{ Exception -> 0x0186 }
        r4[r2] = r3;	 Catch:{ Exception -> 0x0186 }
        goto L_0x004f;
    L_0x006c:
        r1.dispose();	 Catch:{ Exception -> 0x0186 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0186 }
        if (r0 != 0) goto L_0x00c4;
    L_0x0072:
        r0 = 0;
    L_0x0073:
        r1 = r10.length;	 Catch:{ Exception -> 0x0186 }
        if (r0 >= r1) goto L_0x00ba;
    L_0x0076:
        r1 = r10[r0];	 Catch:{ Exception -> 0x0186 }
        if (r1 != r11) goto L_0x00b7;
    L_0x007a:
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x0186 }
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);	 Catch:{ Exception -> 0x0186 }
        r1 = r1.getDatabase();	 Catch:{ Exception -> 0x0186 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x0186 }
        r3 = "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1";
        r4 = new java.lang.Object[r14];	 Catch:{ Exception -> 0x0186 }
        r5 = java.lang.Long.valueOf(r22);	 Catch:{ Exception -> 0x0186 }
        r4[r12] = r5;	 Catch:{ Exception -> 0x0186 }
        r5 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0186 }
        r4[r13] = r5;	 Catch:{ Exception -> 0x0186 }
        r2 = java.lang.String.format(r2, r3, r4);	 Catch:{ Exception -> 0x0186 }
        r3 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x0186 }
        r1 = r1.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x0186 }
        r2 = r1.next();	 Catch:{ Exception -> 0x0186 }
        if (r2 == 0) goto L_0x00ad;
    L_0x00a6:
        r2 = r1.intValue(r12);	 Catch:{ Exception -> 0x0186 }
        r10[r0] = r2;	 Catch:{ Exception -> 0x0186 }
        goto L_0x00af;
    L_0x00ad:
        r10[r0] = r12;	 Catch:{ Exception -> 0x0186 }
    L_0x00af:
        r1.dispose();	 Catch:{ Exception -> 0x0186 }
        r1 = r10[r0];	 Catch:{ Exception -> 0x0186 }
        r7.putMediaCountDatabase(r8, r0, r1);	 Catch:{ Exception -> 0x0186 }
    L_0x00b7:
        r0 = r0 + 1;
        goto L_0x0073;
    L_0x00ba:
        r0 = new org.telegram.messenger.-$$Lambda$DataQuery$uGKbWFS5irrcqKAN9F4Ebx5IyKQ;	 Catch:{ Exception -> 0x0186 }
        r0.<init>(r7, r8, r10);	 Catch:{ Exception -> 0x0186 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0186 }
        goto L_0x018a;
    L_0x00c4:
        r5 = 0;
        r16 = 0;
    L_0x00c7:
        r1 = r10.length;	 Catch:{ Exception -> 0x0186 }
        if (r5 >= r1) goto L_0x0177;
    L_0x00ca:
        r1 = r10[r5];	 Catch:{ Exception -> 0x0186 }
        if (r1 == r11) goto L_0x00df;
    L_0x00ce:
        r1 = r4[r5];	 Catch:{ Exception -> 0x0186 }
        if (r1 != r13) goto L_0x00d3;
    L_0x00d2:
        goto L_0x00df;
    L_0x00d3:
        r3 = r24;
        r18 = r4;
        r17 = r5;
        r19 = r6;
        r20 = 4;
        goto L_0x016c;
    L_0x00df:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_search;	 Catch:{ Exception -> 0x0186 }
        r3.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.limit = r13;	 Catch:{ Exception -> 0x0186 }
        r3.offset_id = r12;	 Catch:{ Exception -> 0x0186 }
        if (r5 != 0) goto L_0x00f3;
    L_0x00ea:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;	 Catch:{ Exception -> 0x0186 }
        r1.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.filter = r1;	 Catch:{ Exception -> 0x0186 }
    L_0x00f1:
        r2 = 4;
        goto L_0x011b;
    L_0x00f3:
        if (r5 != r13) goto L_0x00fd;
    L_0x00f5:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;	 Catch:{ Exception -> 0x0186 }
        r1.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.filter = r1;	 Catch:{ Exception -> 0x0186 }
        goto L_0x00f1;
    L_0x00fd:
        if (r5 != r14) goto L_0x0107;
    L_0x00ff:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;	 Catch:{ Exception -> 0x0186 }
        r1.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.filter = r1;	 Catch:{ Exception -> 0x0186 }
        goto L_0x00f1;
    L_0x0107:
        if (r5 != r15) goto L_0x0111;
    L_0x0109:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;	 Catch:{ Exception -> 0x0186 }
        r1.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.filter = r1;	 Catch:{ Exception -> 0x0186 }
        goto L_0x00f1;
    L_0x0111:
        r2 = 4;
        if (r5 != r2) goto L_0x011b;
    L_0x0114:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;	 Catch:{ Exception -> 0x0186 }
        r1.<init>();	 Catch:{ Exception -> 0x0186 }
        r3.filter = r1;	 Catch:{ Exception -> 0x0186 }
    L_0x011b:
        r1 = "";
        r3.q = r1;	 Catch:{ Exception -> 0x0186 }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x0186 }
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);	 Catch:{ Exception -> 0x0186 }
        r1 = r1.getInputPeer(r0);	 Catch:{ Exception -> 0x0186 }
        r3.peer = r1;	 Catch:{ Exception -> 0x0186 }
        r1 = r3.peer;	 Catch:{ Exception -> 0x0186 }
        if (r1 != 0) goto L_0x0132;
    L_0x012f:
        r10[r5] = r12;	 Catch:{ Exception -> 0x0186 }
        goto L_0x00d3;
    L_0x0132:
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x0186 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x0186 }
        r12 = new org.telegram.messenger.-$$Lambda$DataQuery$i4rT5QEZ9uG6p5oQhmr-S03Vnuo;	 Catch:{ Exception -> 0x0186 }
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
        r1.<init>(r2, r3, r4, r5);	 Catch:{ Exception -> 0x0186 }
        r1 = r14.sendRequest(r15, r12);	 Catch:{ Exception -> 0x0186 }
        r2 = r7.currentAccount;	 Catch:{ Exception -> 0x0186 }
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);	 Catch:{ Exception -> 0x0186 }
        r3 = r24;
        r2.bindRequestToGuid(r1, r3);	 Catch:{ Exception -> 0x0186 }
        r1 = r10[r17];	 Catch:{ Exception -> 0x0186 }
        if (r1 != r11) goto L_0x0166;
    L_0x0163:
        r16 = 1;
        goto L_0x016c;
    L_0x0166:
        r1 = r18[r17];	 Catch:{ Exception -> 0x0186 }
        if (r1 != r13) goto L_0x016c;
    L_0x016a:
        r10[r17] = r11;	 Catch:{ Exception -> 0x0186 }
    L_0x016c:
        r5 = r17 + 1;
        r4 = r18;
        r6 = r19;
        r12 = 0;
        r14 = 2;
        r15 = 3;
        goto L_0x00c7;
    L_0x0177:
        r19 = r6;
        if (r16 != 0) goto L_0x018a;
    L_0x017b:
        r0 = new org.telegram.messenger.-$$Lambda$DataQuery$wSmeN-vKHIRrAW1dEanMWvFyMCo;	 Catch:{ Exception -> 0x0186 }
        r1 = r19;
        r0.<init>(r7, r8, r1);	 Catch:{ Exception -> 0x0186 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0186 }
        goto L_0x018a;
    L_0x0186:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x018a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$getMediaCounts$56$DataQuery(long, int):void");
    }

    public /* synthetic */ void lambda$null$52$DataQuery(long j, int[] iArr) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$54$DataQuery(int[] iArr, int i, long j, TLObject tLObject, TL_error tL_error) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$D0edyBe7uzUZgJEes_37nxNPUP8(this, j, iArr));
        }
    }

    public /* synthetic */ void lambda$null$53$DataQuery(long j, int[] iArr) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public /* synthetic */ void lambda$null$55$DataQuery(long j, int[] iArr) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
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
            tL_messages_search.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i3);
            if (tL_messages_search.peer != null) {
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new -$$Lambda$DataQuery$b79Pt8rqwc9fu4IUQhVikz5ynL4(this, j, i, i2)), i2);
            }
        }
    }

    public /* synthetic */ void lambda$getMediaCount$58$DataQuery(long j, int i, int i2, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            int size;
            messages_Messages messages_messages = (messages_Messages) tLObject;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            if (messages_messages instanceof TL_messages_messages) {
                size = messages_messages.messages.size();
            } else {
                size = messages_messages.count;
            }
            int i3 = size;
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$GKdiXJjXrtMcEJNPcPmeNeb7UEQ(this, messages_messages));
            processLoadedMediaCount(i3, j, i, i2, false, 0);
        }
    }

    public /* synthetic */ void lambda$null$57$DataQuery(messages_Messages messages_messages) {
        MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
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
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages2.users, messages_messages2.chats, true, true);
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$0dlxm8i-c0v_chVqHF7I-je7M4g(this, messages_messages, i4, j, arrayList, i5, i3, z2));
        } else if (i6 != 2) {
            loadMedia(j, i, i2, i3, 0, i5);
        }
    }

    public /* synthetic */ void lambda$processLoadedMedia$59$DataQuery(messages_Messages messages_messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z) {
        int i4 = messages_messages.count;
        MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, i != 0);
        MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, i != 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Integer.valueOf(i2), Integer.valueOf(i3), Boolean.valueOf(z));
    }

    private void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z, int i4) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$GYaSd5P0GrMZfcQPvBwtuZFrwqw(this, j, z, i, i2, i4, i3));
    }

    public /* synthetic */ void lambda$processLoadedMediaCount$60$DataQuery(long j, boolean z, int i, int i2, int i3, int i4) {
        long j2 = j;
        int i5 = i;
        int i6 = i2;
        int i7 = (int) j2;
        Object obj = (!z || (!(i5 == -1 || (i5 == 0 && i6 == 2)) || i7 == 0)) ? null : 1;
        if (obj != null || (i3 == 1 && i7 != 0)) {
            getMediaCount(j, i2, i4, false);
        }
        if (obj == null) {
            if (!z) {
                putMediaCountDatabase(j2, i6, i5);
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
            int i8 = NotificationCenter.mediaCountDidLoad;
            Object[] objArr = new Object[4];
            objArr[0] = Long.valueOf(j);
            if (z && i5 == -1) {
                i5 = 0;
            }
            objArr[1] = Integer.valueOf(i5);
            objArr[2] = Boolean.valueOf(z);
            objArr[3] = Integer.valueOf(i2);
            instance.postNotificationName(i8, objArr);
        }
    }

    private void putMediaCountDatabase(long j, int i, int i2) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$VPyo4uiijc6Mw5vRqsga_xtHpO4(this, j, i, i2));
    }

    public /* synthetic */ void lambda$putMediaCountDatabase$61$DataQuery(long j, int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$r7IDcaPS4_-Lq9u-Bcjhps7n1wc(this, j, i, i2));
    }

    public /* synthetic */ void lambda$getMediaCountDatabase$62$DataQuery(long j, int i, int i2) {
        long j2 = j;
        try {
            int intValue;
            int intValue2;
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
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
                queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized.next()) {
                    intValue = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (intValue != -1) {
                    putMediaCountDatabase(j, i, intValue);
                    processLoadedMediaCount(intValue, j, i, i2, true, intValue2);
                }
            }
            i3 = i;
            processLoadedMediaCount(intValue, j, i, i2, true, intValue2);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, boolean z, int i5) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$OqcREWH3JA8hAPi-XM3PyDyEsWM(this, i, j, i2, z, i3, i5, i4));
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:62:0x0225, code skipped:
            r0 = th;
     */
    /* JADX WARNING: Missing block: B:63:0x0226, code skipped:
            r5 = r2;
     */
    /* JADX WARNING: Missing block: B:64:0x0229, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:65:0x022a, code skipped:
            r5 = r2;
     */
    public /* synthetic */ void lambda$loadMediaDatabase$63$DataQuery(int r27, long r28, int r30, boolean r31, int r32, int r33, int r34) {
        /*
        r26 = this;
        r12 = r26;
        r5 = r27;
        r3 = r28;
        r6 = r30;
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r2.<init>();
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r0.<init>();	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r1.<init>();	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r7 = r5 + 1;
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r8 = r8.getDatabase();	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r9 = (int) r3;
        r13 = 2;
        if (r9 == 0) goto L_0x022d;
    L_0x0027:
        r10 = (long) r6;
        if (r31 == 0) goto L_0x002c;
    L_0x002a:
        r14 = -r9;
        goto L_0x002d;
    L_0x002c:
        r14 = 0;
    L_0x002d:
        r17 = 32;
        r18 = 0;
        r20 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r20 == 0) goto L_0x0040;
    L_0x0035:
        if (r14 == 0) goto L_0x0040;
    L_0x0037:
        r20 = r0;
        r21 = r1;
        r0 = (long) r14;
        r0 = r0 << r17;
        r10 = r10 | r0;
        goto L_0x0044;
    L_0x0040:
        r20 = r0;
        r21 = r1;
    L_0x0044:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)";
        r15 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r23 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = 0;
        r15[r13] = r23;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r22 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r16 = 1;
        r15[r16] = r22;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = java.lang.String.format(r0, r1, r15);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = r8.queryFinalized(r0, r1);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = r0.next();	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        if (r1 == 0) goto L_0x0077;
    L_0x0069:
        r1 = r0.intValue(r13);	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        r13 = 1;
        if (r1 != r13) goto L_0x0072;
    L_0x0070:
        r1 = 1;
        goto L_0x0073;
    L_0x0072:
        r1 = 0;
    L_0x0073:
        r0.dispose();	 Catch:{ Exception -> 0x0335, all -> 0x0331 }
        goto L_0x00d7;
    L_0x0077:
        r0.dispose();	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0";
        r13 = 2;
        r15 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r5 = 0;
        r15[r5] = r13;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r16 = 1;
        r15[r16] = r13;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = java.lang.String.format(r0, r1, r15);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = r8.queryFinalized(r0, r1);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = r0.next();	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        if (r1 == 0) goto L_0x00d3;
    L_0x00a0:
        r1 = r0.intValue(r5);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        if (r1 == 0) goto L_0x00d3;
    L_0x00a6:
        r5 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)";
        r5 = r8.executeFast(r5);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r5.requery();	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r13 = 1;
        r5.bindLong(r13, r3);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r13 = r32;
        r15 = 2;
        r5.bindInteger(r15, r13);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r13 = 0;
        r15 = 3;
        r5.bindInteger(r15, r13);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r13 = 4;
        r5.bindInteger(r13, r1);	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r5.step();	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        r5.dispose();	 Catch:{ Exception -> 0x00ce, all -> 0x00c9 }
        goto L_0x00d3;
    L_0x00c9:
        r0 = move-exception;
        r7 = r27;
        goto L_0x0333;
    L_0x00ce:
        r0 = move-exception;
        r7 = r27;
        goto L_0x0337;
    L_0x00d3:
        r0.dispose();	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r1 = 0;
    L_0x00d7:
        r24 = 1;
        r0 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r0 == 0) goto L_0x018f;
    L_0x00dd:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r5 = "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1";
        r13 = 3;
        r15 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r22 = 0;
        r15[r22] = r13;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r16 = 1;
        r15[r16] = r13;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = java.lang.Integer.valueOf(r30);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r23 = 2;
        r15[r23] = r13;	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = java.lang.String.format(r0, r5, r15);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r5 = 0;
        r13 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r0 = r8.queryFinalized(r0, r13);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r13 = r0.next();	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        if (r13 == 0) goto L_0x011f;
    L_0x010d:
        r13 = r0.intValue(r5);	 Catch:{ Exception -> 0x0229, all -> 0x0225 }
        r15 = r1;
        r5 = r2;
        r1 = (long) r13;
        if (r14 == 0) goto L_0x011c;
    L_0x0116:
        r13 = (long) r14;
        r13 = r13 << r17;
        r18 = r1 | r13;
        goto L_0x0121;
    L_0x011c:
        r18 = r1;
        goto L_0x0121;
    L_0x011f:
        r15 = r1;
        r5 = r2;
    L_0x0121:
        r0.dispose();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = (r18 > r24 ? 1 : (r18 == r24 ? 0 : -1));
        if (r0 <= 0) goto L_0x015f;
    L_0x0128:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d";
        r2 = 5;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r13 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r14 = 0;
        r2[r14] = r13;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r18);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 2;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 3;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 4;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        goto L_0x0222;
    L_0x015f:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d";
        r2 = 4;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r13 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r14 = 0;
        r2[r14] = r13;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r10);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 2;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 3;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        goto L_0x0222;
    L_0x018f:
        r15 = r1;
        r5 = r2;
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d";
        r2 = 2;
        r10 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 0;
        r10[r11] = r2;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r13 = 1;
        r10[r13] = r2;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r10);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r1);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = r0.next();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r1 == 0) goto L_0x01c5;
    L_0x01b6:
        r1 = r0.intValue(r11);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = (long) r1;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r14 == 0) goto L_0x01c3;
    L_0x01bd:
        r10 = (long) r14;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = r10 << r17;
        r18 = r1 | r10;
        goto L_0x01c5;
    L_0x01c3:
        r18 = r1;
    L_0x01c5:
        r0.dispose();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = (r18 > r24 ? 1 : (r18 == r24 ? 0 : -1));
        if (r0 <= 0) goto L_0x01fb;
    L_0x01cc:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d";
        r2 = 4;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 0;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r18);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 2;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 3;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        goto L_0x0222;
    L_0x01fb:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d";
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 0;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 2;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
    L_0x0222:
        r14 = r15;
        r1 = 0;
        goto L_0x028c;
    L_0x0225:
        r0 = move-exception;
        r5 = r2;
        goto L_0x032a;
    L_0x0229:
        r0 = move-exception;
        r5 = r2;
        goto L_0x032e;
    L_0x022d:
        r20 = r0;
        r21 = r1;
        r5 = r2;
        if (r6 == 0) goto L_0x0264;
    L_0x0234:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d";
        r2 = 4;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 0;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r30);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 2;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 3;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        goto L_0x028b;
    L_0x0264:
        r0 = java.util.Locale.US;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d";
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 0;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = java.lang.Integer.valueOf(r32);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r11 = 1;
        r2[r11] = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r10 = 2;
        r2[r10] = r7;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r1 = 0;
        r2 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.queryFinalized(r0, r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
    L_0x028b:
        r14 = 1;
    L_0x028c:
        r2 = r0.next();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r2 == 0) goto L_0x02d9;
    L_0x0292:
        r2 = r0.byteBufferValue(r1);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r2 == 0) goto L_0x02cf;
    L_0x0298:
        r7 = r2.readInt32(r1);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r2, r7, r1);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8 = r12.currentAccount;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8 = r8.clientUserId;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7.readAttachPath(r2, r8);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2.reuse();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2 = 1;
        r8 = r0.intValue(r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7.id = r8;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7.dialog_id = r3;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r9 != 0) goto L_0x02c1;
    L_0x02b9:
        r2 = 2;
        r10 = r0.longValue(r2);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7.random_id = r10;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        goto L_0x02c2;
    L_0x02c1:
        r2 = 2;
    L_0x02c2:
        r8 = r5.messages;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8.add(r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8 = r20;
        r10 = r21;
        org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r7, r8, r10);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        goto L_0x02d4;
    L_0x02cf:
        r8 = r20;
        r10 = r21;
        r2 = 2;
    L_0x02d4:
        r20 = r8;
        r21 = r10;
        goto L_0x028c;
    L_0x02d9:
        r8 = r20;
        r10 = r21;
        r0.dispose();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r8.isEmpty();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2 = ",";
        if (r0 != 0) goto L_0x02f7;
    L_0x02e8:
        r0 = r12.currentAccount;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = android.text.TextUtils.join(r2, r8);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r8 = r5.users;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0.getUsersInternal(r7, r8);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
    L_0x02f7:
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        if (r0 != 0) goto L_0x030c;
    L_0x02fd:
        r0 = r12.currentAccount;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r2 = android.text.TextUtils.join(r2, r10);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = r5.chats;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0.getChatsInternal(r2, r7);	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
    L_0x030c:
        r0 = r5.messages;	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r0 = r0.size();	 Catch:{ Exception -> 0x032d, all -> 0x0329 }
        r7 = r27;
        if (r0 <= r7) goto L_0x0327;
    L_0x0316:
        r0 = r5.messages;	 Catch:{ Exception -> 0x0325 }
        r2 = r5.messages;	 Catch:{ Exception -> 0x0325 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0325 }
        r8 = 1;
        r2 = r2 - r8;
        r0.remove(r2);	 Catch:{ Exception -> 0x0325 }
        r11 = 0;
        goto L_0x034b;
    L_0x0325:
        r0 = move-exception;
        goto L_0x0338;
    L_0x0327:
        r11 = r14;
        goto L_0x034b;
    L_0x0329:
        r0 = move-exception;
    L_0x032a:
        r7 = r27;
        goto L_0x0361;
    L_0x032d:
        r0 = move-exception;
    L_0x032e:
        r7 = r27;
        goto L_0x0338;
    L_0x0331:
        r0 = move-exception;
        r7 = r5;
    L_0x0333:
        r5 = r2;
        goto L_0x0361;
    L_0x0335:
        r0 = move-exception;
        r7 = r5;
    L_0x0337:
        r5 = r2;
    L_0x0338:
        r11 = 0;
        r1 = r5.messages;	 Catch:{ all -> 0x0360 }
        r1.clear();	 Catch:{ all -> 0x0360 }
        r1 = r5.chats;	 Catch:{ all -> 0x0360 }
        r1.clear();	 Catch:{ all -> 0x0360 }
        r1 = r5.users;	 Catch:{ all -> 0x0360 }
        r1.clear();	 Catch:{ all -> 0x0360 }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0360 }
    L_0x034b:
        r1 = r26;
        r2 = r5;
        r3 = r28;
        r5 = r27;
        r6 = r30;
        r7 = r32;
        r8 = r33;
        r9 = r34;
        r10 = r31;
        r1.processLoadedMedia(r2, r3, r5, r6, r7, r8, r9, r10, r11);
        return;
    L_0x0360:
        r0 = move-exception;
    L_0x0361:
        r11 = 0;
        r1 = r26;
        r2 = r5;
        r3 = r28;
        r5 = r27;
        r6 = r30;
        r7 = r32;
        r8 = r33;
        r9 = r34;
        r10 = r31;
        r1.processLoadedMedia(r2, r3, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x0378;
    L_0x0377:
        throw r0;
    L_0x0378:
        goto L_0x0377;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$loadMediaDatabase$63$DataQuery(int, long, int, boolean, int, int, int):void");
    }

    private void putMediaDatabase(long j, int i, ArrayList<Message> arrayList, int i2, boolean z) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$VgKCIvejIuaPm3YY59TrIO7FIyE(this, arrayList, z, j, i2, i));
    }

    public /* synthetic */ void lambda$putMediaDatabase$64$DataQuery(ArrayList arrayList, boolean z, long j, int i, int i2) {
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        try {
            if (arrayList.isEmpty() || z) {
                MessagesStorage.getInstance(this.currentAccount).doneHolesInMedia(j2, i3, i4);
                if (arrayList.isEmpty()) {
                    return;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
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
                    MessagesStorage.getInstance(this.currentAccount).closeHolesInMedia(j, i5, i, i2);
                } else {
                    MessagesStorage.getInstance(this.currentAccount).closeHolesInMedia(j, i5, Integer.MAX_VALUE, i2);
                }
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadMusic(long j, long j2) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$cA-X50sc6geZKVWNtiHKllnXTb8(this, j, j2));
    }

    public /* synthetic */ void lambda$loadMusic$66$DataQuery(long j, long j2) {
        SQLiteCursor queryFinalized;
        ArrayList arrayList = new ArrayList();
        if (((int) j) != 0) {
            try {
                queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
        }
        while (queryFinalized.next()) {
            NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
            if (byteBufferValue != null) {
                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                byteBufferValue.reuse();
                if (MessageObject.isMusicMessage(TLdeserialize)) {
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.dialog_id = j;
                    arrayList.add(0, new MessageObject(this.currentAccount, TLdeserialize, false));
                }
            }
        }
        queryFinalized.dispose();
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$etglwa1VnT5BvHlWsrv51HhlBY4(this, j, arrayList));
    }

    public /* synthetic */ void lambda$null$65$DataQuery(long j, ArrayList arrayList) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(j), arrayList);
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
            Utilities.globalQueue.postRunnable(new -$$Lambda$DataQuery$usUs3tLksjqGquVtjg9TZkSDUaI(this, arrayList));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:86:0x025c A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0273 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0292 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x028a A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02af A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02ab A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x025c A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0273 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x028a A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0292 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02ab A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02af A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0273 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0292 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x028a A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02af A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02ab A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0273 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x028a A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0292 A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x02ab A:{Catch:{ Throwable -> 0x02bd }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x02af A:{Catch:{ Throwable -> 0x02bd }} */
    public /* synthetic */ void lambda$buildShortcuts$67$DataQuery(java.util.ArrayList r21) {
        /*
        r20 = this;
        r1 = r20;
        r2 = r21;
        r0 = "NewConversationShortcut";
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r4 = android.content.pm.ShortcutManager.class;
        r3 = r3.getSystemService(r4);	 Catch:{ Throwable -> 0x02bd }
        r3 = (android.content.pm.ShortcutManager) r3;	 Catch:{ Throwable -> 0x02bd }
        r4 = r3.getDynamicShortcuts();	 Catch:{ Throwable -> 0x02bd }
        r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02bd }
        r5.<init>();	 Catch:{ Throwable -> 0x02bd }
        r6 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02bd }
        r6.<init>();	 Catch:{ Throwable -> 0x02bd }
        r7 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02bd }
        r7.<init>();	 Catch:{ Throwable -> 0x02bd }
        r8 = "did";
        r9 = 0;
        r10 = "compose";
        if (r4 == 0) goto L_0x00a1;
    L_0x002a:
        r11 = r4.isEmpty();	 Catch:{ Throwable -> 0x02bd }
        if (r11 != 0) goto L_0x00a1;
    L_0x0030:
        r6.add(r10);	 Catch:{ Throwable -> 0x02bd }
        r11 = 0;
    L_0x0034:
        r12 = r21.size();	 Catch:{ Throwable -> 0x02bd }
        if (r11 >= r12) goto L_0x0074;
    L_0x003a:
        r12 = r2.get(r11);	 Catch:{ Throwable -> 0x02bd }
        r12 = (org.telegram.tgnet.TLRPC.TL_topPeer) r12;	 Catch:{ Throwable -> 0x02bd }
        r13 = r12.peer;	 Catch:{ Throwable -> 0x02bd }
        r13 = r13.user_id;	 Catch:{ Throwable -> 0x02bd }
        if (r13 == 0) goto L_0x004c;
    L_0x0046:
        r12 = r12.peer;	 Catch:{ Throwable -> 0x02bd }
        r12 = r12.user_id;	 Catch:{ Throwable -> 0x02bd }
    L_0x004a:
        r12 = (long) r12;	 Catch:{ Throwable -> 0x02bd }
        goto L_0x005f;
    L_0x004c:
        r13 = r12.peer;	 Catch:{ Throwable -> 0x02bd }
        r13 = r13.chat_id;	 Catch:{ Throwable -> 0x02bd }
        r13 = -r13;
        r13 = (long) r13;	 Catch:{ Throwable -> 0x02bd }
        r15 = 0;
        r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r17 != 0) goto L_0x005e;
    L_0x0058:
        r12 = r12.peer;	 Catch:{ Throwable -> 0x02bd }
        r12 = r12.channel_id;	 Catch:{ Throwable -> 0x02bd }
        r12 = -r12;
        goto L_0x004a;
    L_0x005e:
        r12 = r13;
    L_0x005f:
        r14 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02bd }
        r14.<init>();	 Catch:{ Throwable -> 0x02bd }
        r14.append(r8);	 Catch:{ Throwable -> 0x02bd }
        r14.append(r12);	 Catch:{ Throwable -> 0x02bd }
        r12 = r14.toString();	 Catch:{ Throwable -> 0x02bd }
        r6.add(r12);	 Catch:{ Throwable -> 0x02bd }
        r11 = r11 + 1;
        goto L_0x0034;
    L_0x0074:
        r11 = 0;
    L_0x0075:
        r12 = r4.size();	 Catch:{ Throwable -> 0x02bd }
        if (r11 >= r12) goto L_0x0094;
    L_0x007b:
        r12 = r4.get(r11);	 Catch:{ Throwable -> 0x02bd }
        r12 = (android.content.pm.ShortcutInfo) r12;	 Catch:{ Throwable -> 0x02bd }
        r12 = r12.getId();	 Catch:{ Throwable -> 0x02bd }
        r13 = r6.remove(r12);	 Catch:{ Throwable -> 0x02bd }
        if (r13 != 0) goto L_0x008e;
    L_0x008b:
        r7.add(r12);	 Catch:{ Throwable -> 0x02bd }
    L_0x008e:
        r5.add(r12);	 Catch:{ Throwable -> 0x02bd }
        r11 = r11 + 1;
        goto L_0x0075;
    L_0x0094:
        r4 = r6.isEmpty();	 Catch:{ Throwable -> 0x02bd }
        if (r4 == 0) goto L_0x00a1;
    L_0x009a:
        r4 = r7.isEmpty();	 Catch:{ Throwable -> 0x02bd }
        if (r4 == 0) goto L_0x00a1;
    L_0x00a0:
        return;
    L_0x00a1:
        r4 = new android.content.Intent;	 Catch:{ Throwable -> 0x02bd }
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r11 = org.telegram.ui.LaunchActivity.class;
        r4.<init>(r6, r11);	 Catch:{ Throwable -> 0x02bd }
        r6 = "new_dialog";
        r4.setAction(r6);	 Catch:{ Throwable -> 0x02bd }
        r6 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02bd }
        r6.<init>();	 Catch:{ Throwable -> 0x02bd }
        r11 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02bd }
        r12 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r11.<init>(r12, r10);	 Catch:{ Throwable -> 0x02bd }
        r12 = NUM; // 0x7f0d05e1 float:1.8745167E38 double:1.053130521E-314;
        r13 = org.telegram.messenger.LocaleController.getString(r0, r12);	 Catch:{ Throwable -> 0x02bd }
        r11 = r11.setShortLabel(r13);	 Catch:{ Throwable -> 0x02bd }
        r0 = org.telegram.messenger.LocaleController.getString(r0, r12);	 Catch:{ Throwable -> 0x02bd }
        r0 = r11.setLongLabel(r0);	 Catch:{ Throwable -> 0x02bd }
        r11 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r12 = NUM; // 0x7var_ float:1.7945878E38 double:1.0529358197E-314;
        r11 = android.graphics.drawable.Icon.createWithResource(r11, r12);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.setIcon(r11);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.setIntent(r4);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.build();	 Catch:{ Throwable -> 0x02bd }
        r6.add(r0);	 Catch:{ Throwable -> 0x02bd }
        r0 = r5.contains(r10);	 Catch:{ Throwable -> 0x02bd }
        if (r0 == 0) goto L_0x00f0;
    L_0x00ec:
        r3.updateShortcuts(r6);	 Catch:{ Throwable -> 0x02bd }
        goto L_0x00f3;
    L_0x00f0:
        r3.addDynamicShortcuts(r6);	 Catch:{ Throwable -> 0x02bd }
    L_0x00f3:
        r6.clear();	 Catch:{ Throwable -> 0x02bd }
        r0 = r7.isEmpty();	 Catch:{ Throwable -> 0x02bd }
        if (r0 != 0) goto L_0x00ff;
    L_0x00fc:
        r3.removeDynamicShortcuts(r7);	 Catch:{ Throwable -> 0x02bd }
    L_0x00ff:
        r0 = r21.size();	 Catch:{ Throwable -> 0x02bd }
        if (r9 >= r0) goto L_0x02bd;
    L_0x0105:
        r4 = new android.content.Intent;	 Catch:{ Throwable -> 0x02bd }
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r7 = org.telegram.messenger.OpenChatReceiver.class;
        r4.<init>(r0, r7);	 Catch:{ Throwable -> 0x02bd }
        r0 = r2.get(r9);	 Catch:{ Throwable -> 0x02bd }
        r0 = (org.telegram.tgnet.TLRPC.TL_topPeer) r0;	 Catch:{ Throwable -> 0x02bd }
        r7 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r7 = r7.user_id;	 Catch:{ Throwable -> 0x02bd }
        if (r7 == 0) goto L_0x013c;
    L_0x011a:
        r7 = "userId";
        r11 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r11 = r11.user_id;	 Catch:{ Throwable -> 0x02bd }
        r4.putExtra(r7, r11);	 Catch:{ Throwable -> 0x02bd }
        r7 = r1.currentAccount;	 Catch:{ Throwable -> 0x02bd }
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);	 Catch:{ Throwable -> 0x02bd }
        r11 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r11 = r11.user_id;	 Catch:{ Throwable -> 0x02bd }
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Throwable -> 0x02bd }
        r7 = r7.getUser(r11);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.user_id;	 Catch:{ Throwable -> 0x02bd }
        r11 = (long) r0;	 Catch:{ Throwable -> 0x02bd }
        r0 = 0;
        goto L_0x015c;
    L_0x013c:
        r7 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r7 = r7.chat_id;	 Catch:{ Throwable -> 0x02bd }
        if (r7 != 0) goto L_0x0146;
    L_0x0142:
        r0 = r0.peer;	 Catch:{ Throwable -> 0x02bd }
        r7 = r0.channel_id;	 Catch:{ Throwable -> 0x02bd }
    L_0x0146:
        r0 = r1.currentAccount;	 Catch:{ Throwable -> 0x02bd }
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Throwable -> 0x02bd }
        r11 = java.lang.Integer.valueOf(r7);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.getChat(r11);	 Catch:{ Throwable -> 0x02bd }
        r11 = "chatId";
        r4.putExtra(r11, r7);	 Catch:{ Throwable -> 0x02bd }
        r7 = -r7;
        r11 = (long) r7;	 Catch:{ Throwable -> 0x02bd }
        r7 = 0;
    L_0x015c:
        if (r7 == 0) goto L_0x0164;
    L_0x015e:
        r13 = org.telegram.messenger.UserObject.isDeleted(r7);	 Catch:{ Throwable -> 0x02bd }
        if (r13 == 0) goto L_0x0168;
    L_0x0164:
        if (r0 != 0) goto L_0x0168;
    L_0x0166:
        goto L_0x02b5;
    L_0x0168:
        if (r7 == 0) goto L_0x0182;
    L_0x016a:
        r0 = r7.first_name;	 Catch:{ Throwable -> 0x02bd }
        r13 = r7.last_name;	 Catch:{ Throwable -> 0x02bd }
        r0 = org.telegram.messenger.ContactsController.formatName(r0, r13);	 Catch:{ Throwable -> 0x02bd }
        r13 = r7.photo;	 Catch:{ Throwable -> 0x02bd }
        if (r13 == 0) goto L_0x0180;
    L_0x0176:
        r7 = r7.photo;	 Catch:{ Throwable -> 0x02bd }
        r7 = r7.photo_small;	 Catch:{ Throwable -> 0x02bd }
        r19 = r7;
        r7 = r0;
        r0 = r19;
        goto L_0x018e;
    L_0x0180:
        r7 = r0;
        goto L_0x018d;
    L_0x0182:
        r7 = r0.title;	 Catch:{ Throwable -> 0x02bd }
        r13 = r0.photo;	 Catch:{ Throwable -> 0x02bd }
        if (r13 == 0) goto L_0x018d;
    L_0x0188:
        r0 = r0.photo;	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.photo_small;	 Catch:{ Throwable -> 0x02bd }
        goto L_0x018e;
    L_0x018d:
        r0 = 0;
    L_0x018e:
        r13 = "currentAccount";
        r14 = r1.currentAccount;	 Catch:{ Throwable -> 0x02bd }
        r4.putExtra(r13, r14);	 Catch:{ Throwable -> 0x02bd }
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02bd }
        r13.<init>();	 Catch:{ Throwable -> 0x02bd }
        r14 = "com.tmessages.openchat";
        r13.append(r14);	 Catch:{ Throwable -> 0x02bd }
        r13.append(r11);	 Catch:{ Throwable -> 0x02bd }
        r13 = r13.toString();	 Catch:{ Throwable -> 0x02bd }
        r4.setAction(r13);	 Catch:{ Throwable -> 0x02bd }
        r13 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r4.addFlags(r13);	 Catch:{ Throwable -> 0x02bd }
        if (r0 == 0) goto L_0x025c;
    L_0x01b0:
        r13 = 1;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r13);	 Catch:{ Throwable -> 0x0255 }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x0255 }
        r14 = android.graphics.BitmapFactory.decodeFile(r0);	 Catch:{ Throwable -> 0x0255 }
        if (r14 == 0) goto L_0x0253;
    L_0x01bf:
        r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Throwable -> 0x0250 }
        r15 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0250 }
        r15 = android.graphics.Bitmap.createBitmap(r0, r0, r15);	 Catch:{ Throwable -> 0x0250 }
        r10 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0250 }
        r10.<init>(r15);	 Catch:{ Throwable -> 0x0250 }
        r17 = roundPaint;	 Catch:{ Throwable -> 0x0250 }
        r18 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r17 != 0) goto L_0x021e;
    L_0x01d6:
        r13 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x0250 }
        r1 = 3;
        r13.<init>(r1);	 Catch:{ Throwable -> 0x0250 }
        roundPaint = r13;	 Catch:{ Throwable -> 0x0250 }
        r1 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x0250 }
        r1.<init>();	 Catch:{ Throwable -> 0x0250 }
        bitmapRect = r1;	 Catch:{ Throwable -> 0x0250 }
        r1 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x0250 }
        r13 = 1;
        r1.<init>(r13);	 Catch:{ Throwable -> 0x0250 }
        erasePaint = r1;	 Catch:{ Throwable -> 0x0250 }
        r1 = erasePaint;	 Catch:{ Throwable -> 0x0250 }
        r13 = new android.graphics.PorterDuffXfermode;	 Catch:{ Throwable -> 0x0250 }
        r2 = android.graphics.PorterDuff.Mode.CLEAR;	 Catch:{ Throwable -> 0x0250 }
        r13.<init>(r2);	 Catch:{ Throwable -> 0x0250 }
        r1.setXfermode(r13);	 Catch:{ Throwable -> 0x0250 }
        r1 = new android.graphics.Path;	 Catch:{ Throwable -> 0x0250 }
        r1.<init>();	 Catch:{ Throwable -> 0x0250 }
        roundPath = r1;	 Catch:{ Throwable -> 0x0250 }
        r1 = roundPath;	 Catch:{ Throwable -> 0x0250 }
        r2 = r0 / 2;
        r2 = (float) r2;	 Catch:{ Throwable -> 0x0250 }
        r13 = r0 / 2;
        r13 = (float) r13;	 Catch:{ Throwable -> 0x0250 }
        r0 = r0 / 2;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x0250 }
        r0 = r0 - r17;
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0250 }
        r17 = r15;
        r15 = android.graphics.Path.Direction.CW;	 Catch:{ Throwable -> 0x0250 }
        r1.addCircle(r2, r13, r0, r15);	 Catch:{ Throwable -> 0x0250 }
        r0 = roundPath;	 Catch:{ Throwable -> 0x0250 }
        r0.toggleInverseFillType();	 Catch:{ Throwable -> 0x0250 }
        goto L_0x0220;
    L_0x021e:
        r17 = r15;
    L_0x0220:
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x0250 }
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x0250 }
        r1 = (float) r1;	 Catch:{ Throwable -> 0x0250 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Throwable -> 0x0250 }
        r2 = (float) r2;	 Catch:{ Throwable -> 0x0250 }
        r13 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x0250 }
        r15 = (float) r15;	 Catch:{ Throwable -> 0x0250 }
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x0250 }
        r13 = (float) r13;	 Catch:{ Throwable -> 0x0250 }
        r0.set(r1, r2, r15, r13);	 Catch:{ Throwable -> 0x0250 }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x0250 }
        r1 = roundPaint;	 Catch:{ Throwable -> 0x0250 }
        r2 = 0;
        r10.drawBitmap(r14, r2, r0, r1);	 Catch:{ Throwable -> 0x0250 }
        r0 = roundPath;	 Catch:{ Throwable -> 0x0250 }
        r1 = erasePaint;	 Catch:{ Throwable -> 0x0250 }
        r10.drawPath(r0, r1);	 Catch:{ Throwable -> 0x0250 }
        r10.setBitmap(r2);	 Catch:{ Exception -> 0x024d }
    L_0x024d:
        r10 = r17;
        goto L_0x025e;
    L_0x0250:
        r0 = move-exception;
        r10 = r14;
        goto L_0x0258;
    L_0x0253:
        r10 = r14;
        goto L_0x025e;
    L_0x0255:
        r0 = move-exception;
        r2 = 0;
        r10 = r2;
    L_0x0258:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x02bd }
        goto L_0x025e;
    L_0x025c:
        r2 = 0;
        r10 = r2;
    L_0x025e:
        r0 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02bd }
        r0.<init>();	 Catch:{ Throwable -> 0x02bd }
        r0.append(r8);	 Catch:{ Throwable -> 0x02bd }
        r0.append(r11);	 Catch:{ Throwable -> 0x02bd }
        r0 = r0.toString();	 Catch:{ Throwable -> 0x02bd }
        r1 = android.text.TextUtils.isEmpty(r7);	 Catch:{ Throwable -> 0x02bd }
        if (r1 == 0) goto L_0x0275;
    L_0x0273:
        r7 = " ";
    L_0x0275:
        r1 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02bd }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r1.<init>(r2, r0);	 Catch:{ Throwable -> 0x02bd }
        r1 = r1.setShortLabel(r7);	 Catch:{ Throwable -> 0x02bd }
        r1 = r1.setLongLabel(r7);	 Catch:{ Throwable -> 0x02bd }
        r1 = r1.setIntent(r4);	 Catch:{ Throwable -> 0x02bd }
        if (r10 == 0) goto L_0x0292;
    L_0x028a:
        r2 = android.graphics.drawable.Icon.createWithBitmap(r10);	 Catch:{ Throwable -> 0x02bd }
        r1.setIcon(r2);	 Catch:{ Throwable -> 0x02bd }
        goto L_0x029e;
    L_0x0292:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02bd }
        r4 = NUM; // 0x7var_ float:1.794588E38 double:1.05293582E-314;
        r2 = android.graphics.drawable.Icon.createWithResource(r2, r4);	 Catch:{ Throwable -> 0x02bd }
        r1.setIcon(r2);	 Catch:{ Throwable -> 0x02bd }
    L_0x029e:
        r1 = r1.build();	 Catch:{ Throwable -> 0x02bd }
        r6.add(r1);	 Catch:{ Throwable -> 0x02bd }
        r0 = r5.contains(r0);	 Catch:{ Throwable -> 0x02bd }
        if (r0 == 0) goto L_0x02af;
    L_0x02ab:
        r3.updateShortcuts(r6);	 Catch:{ Throwable -> 0x02bd }
        goto L_0x02b2;
    L_0x02af:
        r3.addDynamicShortcuts(r6);	 Catch:{ Throwable -> 0x02bd }
    L_0x02b2:
        r6.clear();	 Catch:{ Throwable -> 0x02bd }
    L_0x02b5:
        r9 = r9 + 1;
        r1 = r20;
        r2 = r21;
        goto L_0x00ff;
    L_0x02bd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$buildShortcuts$67$DataQuery(java.util.ArrayList):void");
    }

    public void loadHints(boolean z) {
        if (!this.loading && UserConfig.getInstance(this.currentAccount).suggestContacts) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_getTopPeers, new -$$Lambda$DataQuery$pT1Y9otWt0WNp61eT_y0o9CzuLY(this));
            } else if (!this.loaded) {
                this.loading = true;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$lAwKQ3k2p-MRBgXcDMGAD4Or380(this));
                this.loaded = true;
            }
        }
    }

    public /* synthetic */ void lambda$loadHints$69$DataQuery() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        try {
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
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
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(str, arrayList5), arrayList3);
            }
            if (!arrayList6.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(str, arrayList6), arrayList4);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$rnIvVF8aLiMm_ZCPm9Vn6vsrLWI(this, arrayList3, arrayList4, arrayList, arrayList2));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$68$DataQuery(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        this.loading = false;
        this.loaded = true;
        this.hints = arrayList3;
        this.inlineBots = arrayList4;
        buildShortcuts();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(UserConfig.getInstance(this.currentAccount).lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    public /* synthetic */ void lambda$loadHints$74$DataQuery(TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$oRCKjdqN7faicjM2qImTY_wiotg(this, tLObject));
        } else if (tLObject instanceof TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$g9_k4zEX2f9EpLAUA_w8HsokieE(this));
        }
    }

    public /* synthetic */ void lambda$null$72$DataQuery(TLObject tLObject) {
        TL_contacts_topPeers tL_contacts_topPeers = (TL_contacts_topPeers) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_topPeers.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_topPeers.chats, false);
        for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
            TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
            if (tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline) {
                this.inlineBots = tL_topPeerCategoryPeers.peers;
                UserConfig.getInstance(this.currentAccount).botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = tL_topPeerCategoryPeers.peers;
                int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                for (int i2 = 0; i2 < this.hints.size(); i2++) {
                    if (((TL_topPeer) this.hints.get(i2)).peer.user_id == clientUserId) {
                        this.hints.remove(i2);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$dzi9o0LNVO5dhAL5CQyt7dNBMiU(this, tL_contacts_topPeers));
    }

    public /* synthetic */ void lambda$null$71$DataQuery(TL_contacts_topPeers tL_contacts_topPeers) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_topPeers.users, tL_contacts_topPeers.chats, false, false);
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
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
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$SlSBEOrYr_Gu3TZ2prgLfFC7QFU(this));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$70$DataQuery() {
        UserConfig.getInstance(this.currentAccount).suggestContacts = true;
        UserConfig.getInstance(this.currentAccount).lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public /* synthetic */ void lambda$null$73$DataQuery() {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$HUiUbvM6V-1I-4yUAM7UkocMBMA(this));
        buildShortcuts();
    }

    public /* synthetic */ void lambda$clearTopPeers$75$DataQuery() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void increaseInlineRaiting(int i) {
        if (UserConfig.getInstance(this.currentAccount).suggestContacts) {
            int max = UserConfig.getInstance(this.currentAccount).botRatingLoadTime != 0 ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - UserConfig.getInstance(this.currentAccount).botRatingLoadTime) : 60;
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
            tL_topPeer.rating += Math.exp((double) (max / MessagesController.getInstance(this.currentAccount).ratingDecay));
            Collections.sort(this.inlineBots, -$$Lambda$DataQuery$PxKOMpM0CQNQDkCfpxm9xlSwLx0.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(i, 1, tL_topPeer.rating);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
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
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resetTopPeerRating, -$$Lambda$DataQuery$nEW3BBG5NIOAbnpoGoJ7S4IYot0.INSTANCE);
                deletePeer(i, 1);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public void removePeer(int i) {
        for (int i2 = 0; i2 < this.hints.size(); i2++) {
            if (((TL_topPeer) this.hints.get(i2)).peer.user_id == i) {
                this.hints.remove(i2);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TL_contacts_resetTopPeerRating tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryCorrespondents();
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                deletePeer(i, 0);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resetTopPeerRating, -$$Lambda$DataQuery$eHV0FXEJJkBEZzT5FGLWr-l3mT8.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(long j) {
        if (UserConfig.getInstance(this.currentAccount).suggestContacts) {
            int i = (int) j;
            if (i > 0) {
                User user = i > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)) : null;
                if (!(user == null || user.bot || user.self)) {
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$5no8NqhTBSPD_1vCOByuz2PkDVw(this, j, i));
                }
            }
        }
    }

    public /* synthetic */ void lambda$increasePeerRaiting$81$DataQuery(long j, int i) {
        double d = 0.0d;
        try {
            int intValue;
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
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
            if (i2 > 0 && UserConfig.getInstance(this.currentAccount).ratingLoadTime != 0) {
                d = (double) (intValue - UserConfig.getInstance(this.currentAccount).ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$5DGSvGUh65NYBwUAJYUrimvkN1o(this, i, d, j));
    }

    public /* synthetic */ void lambda$null$80$DataQuery(int i, double d, long j) {
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
        double d3 = (double) MessagesController.getInstance(this.currentAccount).ratingDecay;
        Double.isNaN(d3);
        tL_topPeer.rating = d2 + Math.exp(d / d3);
        Collections.sort(this.hints, -$$Lambda$DataQuery$12fipD1xacUTdjkG1gIFzgfExOI.INSTANCE);
        savePeer((int) j, 0, tL_topPeer.rating);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$v4m2TDWRtPX_m91A0Er26YD_Gv8(this, i, i2, d));
    }

    public /* synthetic */ void lambda$savePeer$82$DataQuery(int i, int i2, double d) {
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$zfORkxHX1nBnvnpPSEIl0qTrWEM(this, i, i2));
    }

    public /* synthetic */ void lambda$deletePeer$83$DataQuery(int i, int i2) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)})).stepThis().dispose();
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
            if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i2)) == null) {
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

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009e A:{SYNTHETIC, Splitter:B:33:0x009e} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d9 A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e5 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0096 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009e A:{SYNTHETIC, Splitter:B:33:0x009e} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d9 A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e5 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0096 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009e A:{SYNTHETIC, Splitter:B:33:0x009e} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d9 A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e5 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0086 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0062 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0096 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x009e A:{SYNTHETIC, Splitter:B:33:0x009e} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d9 A:{Catch:{ Throwable -> 0x0158 }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e5 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e5 A:{Catch:{ Exception -> 0x0249 }} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A:{Catch:{ Exception -> 0x0249 }} */
    public void installShortcut(long r17) {
        /*
        r16 = this;
        r1 = r16;
        r2 = r17;
        r4 = r16.createIntrnalShortcutIntent(r17);	 Catch:{ Exception -> 0x0249 }
        r0 = (int) r2;	 Catch:{ Exception -> 0x0249 }
        r5 = 32;
        r5 = r2 >> r5;
        r6 = (int) r5;	 Catch:{ Exception -> 0x0249 }
        r5 = 0;
        if (r0 != 0) goto L_0x0033;
    L_0x0011:
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x0249 }
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);	 Catch:{ Exception -> 0x0249 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0249 }
        r0 = r0.getEncryptedChat(r6);	 Catch:{ Exception -> 0x0249 }
        if (r0 != 0) goto L_0x0022;
    L_0x0021:
        return;
    L_0x0022:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0249 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0249 }
        r0 = r0.user_id;	 Catch:{ Exception -> 0x0249 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0249 }
        r0 = r6.getUser(r0);	 Catch:{ Exception -> 0x0249 }
        goto L_0x0043;
    L_0x0033:
        if (r0 <= 0) goto L_0x0046;
    L_0x0035:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0249 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0249 }
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0249 }
        r0 = r6.getUser(r0);	 Catch:{ Exception -> 0x0249 }
    L_0x0043:
        r6 = r0;
        r7 = r5;
        goto L_0x0059;
    L_0x0046:
        if (r0 >= 0) goto L_0x0248;
    L_0x0048:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0249 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0249 }
        r0 = -r0;
        r0 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x0249 }
        r0 = r6.getChat(r0);	 Catch:{ Exception -> 0x0249 }
        r7 = r0;
        r6 = r5;
    L_0x0059:
        if (r6 != 0) goto L_0x005e;
    L_0x005b:
        if (r7 != 0) goto L_0x005e;
    L_0x005d:
        return;
    L_0x005e:
        r0 = 1;
        r8 = 0;
        if (r6 == 0) goto L_0x0086;
    L_0x0062:
        r9 = org.telegram.messenger.UserObject.isUserSelf(r6);	 Catch:{ Exception -> 0x0249 }
        if (r9 == 0) goto L_0x0075;
    L_0x0068:
        r9 = "SavedMessages";
        r10 = NUM; // 0x7f0d08be float:1.8746654E38 double:1.0531308833E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Exception -> 0x0249 }
        r10 = r5;
        r11 = r9;
        r9 = 1;
        goto L_0x0094;
    L_0x0075:
        r9 = r6.first_name;	 Catch:{ Exception -> 0x0249 }
        r10 = r6.last_name;	 Catch:{ Exception -> 0x0249 }
        r9 = org.telegram.messenger.ContactsController.formatName(r9, r10);	 Catch:{ Exception -> 0x0249 }
        r10 = r6.photo;	 Catch:{ Exception -> 0x0249 }
        if (r10 == 0) goto L_0x0091;
    L_0x0081:
        r10 = r6.photo;	 Catch:{ Exception -> 0x0249 }
        r10 = r10.photo_small;	 Catch:{ Exception -> 0x0249 }
        goto L_0x0092;
    L_0x0086:
        r9 = r7.title;	 Catch:{ Exception -> 0x0249 }
        r10 = r7.photo;	 Catch:{ Exception -> 0x0249 }
        if (r10 == 0) goto L_0x0091;
    L_0x008c:
        r10 = r7.photo;	 Catch:{ Exception -> 0x0249 }
        r10 = r10.photo_small;	 Catch:{ Exception -> 0x0249 }
        goto L_0x0092;
    L_0x0091:
        r10 = r5;
    L_0x0092:
        r11 = r9;
        r9 = 0;
    L_0x0094:
        if (r9 != 0) goto L_0x009c;
    L_0x0096:
        if (r10 == 0) goto L_0x0099;
    L_0x0098:
        goto L_0x009c;
    L_0x0099:
        r13 = r5;
        goto L_0x015d;
    L_0x009c:
        if (r9 != 0) goto L_0x00af;
    L_0x009e:
        r10 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r0);	 Catch:{ Throwable -> 0x00ab }
        r10 = r10.toString();	 Catch:{ Throwable -> 0x00ab }
        r10 = android.graphics.BitmapFactory.decodeFile(r10);	 Catch:{ Throwable -> 0x00ab }
        goto L_0x00b0;
    L_0x00ab:
        r0 = move-exception;
        r10 = r5;
        goto L_0x0159;
    L_0x00af:
        r10 = r5;
    L_0x00b0:
        if (r9 != 0) goto L_0x00b4;
    L_0x00b2:
        if (r10 == 0) goto L_0x015c;
    L_0x00b4:
        r12 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);	 Catch:{ Throwable -> 0x0158 }
        r13 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0158 }
        r13 = android.graphics.Bitmap.createBitmap(r12, r12, r13);	 Catch:{ Throwable -> 0x0158 }
        r13.eraseColor(r8);	 Catch:{ Throwable -> 0x0158 }
        r14 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x0158 }
        r14.<init>(r13);	 Catch:{ Throwable -> 0x0158 }
        if (r9 == 0) goto L_0x00d9;
    L_0x00ca:
        r9 = new org.telegram.ui.Components.AvatarDrawable;	 Catch:{ Throwable -> 0x0158 }
        r9.<init>(r6);	 Catch:{ Throwable -> 0x0158 }
        r9.setAvatarType(r0);	 Catch:{ Throwable -> 0x0158 }
        r9.setBounds(r8, r8, r12, r12);	 Catch:{ Throwable -> 0x0158 }
        r9.draw(r14);	 Catch:{ Throwable -> 0x0158 }
        goto L_0x012a;
    L_0x00d9:
        r9 = new android.graphics.BitmapShader;	 Catch:{ Throwable -> 0x0158 }
        r15 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0158 }
        r8 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x0158 }
        r9.<init>(r10, r15, r8);	 Catch:{ Throwable -> 0x0158 }
        r8 = roundPaint;	 Catch:{ Throwable -> 0x0158 }
        if (r8 != 0) goto L_0x00f4;
    L_0x00e6:
        r8 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x0158 }
        r8.<init>(r0);	 Catch:{ Throwable -> 0x0158 }
        roundPaint = r8;	 Catch:{ Throwable -> 0x0158 }
        r0 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x0158 }
        r0.<init>();	 Catch:{ Throwable -> 0x0158 }
        bitmapRect = r0;	 Catch:{ Throwable -> 0x0158 }
    L_0x00f4:
        r0 = (float) r12;	 Catch:{ Throwable -> 0x0158 }
        r8 = r10.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x0158 }
        r0 = r0 / r8;
        r14.save();	 Catch:{ Throwable -> 0x0158 }
        r14.scale(r0, r0);	 Catch:{ Throwable -> 0x0158 }
        r0 = roundPaint;	 Catch:{ Throwable -> 0x0158 }
        r0.setShader(r9);	 Catch:{ Throwable -> 0x0158 }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x0158 }
        r8 = r10.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x0158 }
        r9 = r10.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x0158 }
        r15 = 0;
        r0.set(r15, r15, r8, r9);	 Catch:{ Throwable -> 0x0158 }
        r0 = bitmapRect;	 Catch:{ Throwable -> 0x0158 }
        r8 = r10.getWidth();	 Catch:{ Throwable -> 0x0158 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x0158 }
        r9 = r10.getHeight();	 Catch:{ Throwable -> 0x0158 }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x0158 }
        r15 = roundPaint;	 Catch:{ Throwable -> 0x0158 }
        r14.drawRoundRect(r0, r8, r9, r15);	 Catch:{ Throwable -> 0x0158 }
        r14.restore();	 Catch:{ Throwable -> 0x0158 }
    L_0x012a:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0158 }
        r0 = r0.getResources();	 Catch:{ Throwable -> 0x0158 }
        r8 = NUM; // 0x7var_ float:1.794484E38 double:1.052935567E-314;
        r0 = r0.getDrawable(r8);	 Catch:{ Throwable -> 0x0158 }
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x0158 }
        r12 = r12 - r8;
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x0158 }
        r15 = r12 - r15;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x0158 }
        r12 = r12 - r9;
        r9 = r15 + r8;
        r8 = r8 + r12;
        r0.setBounds(r15, r12, r9, r8);	 Catch:{ Throwable -> 0x0158 }
        r0.draw(r14);	 Catch:{ Throwable -> 0x0158 }
        r14.setBitmap(r5);	 Catch:{ Exception -> 0x015d }
        goto L_0x015d;
    L_0x0158:
        r0 = move-exception;
    L_0x0159:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0249 }
    L_0x015c:
        r13 = r10;
    L_0x015d:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0249 }
        r8 = 26;
        r9 = NUM; // 0x7var_f float:1.7944836E38 double:1.052935566E-314;
        r10 = NUM; // 0x7var_ float:1.7944838E38 double:1.0529355663E-314;
        r12 = NUM; // 0x7var_e float:1.7944834E38 double:1.0529355653E-314;
        r14 = NUM; // 0x7var_ float:1.7944842E38 double:1.0529355673E-314;
        if (r0 < r8) goto L_0x01e5;
    L_0x016f:
        r0 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Exception -> 0x0249 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0249 }
        r15.<init>();	 Catch:{ Exception -> 0x0249 }
        r5 = "sdid_";
        r15.append(r5);	 Catch:{ Exception -> 0x0249 }
        r15.append(r2);	 Catch:{ Exception -> 0x0249 }
        r2 = r15.toString();	 Catch:{ Exception -> 0x0249 }
        r0.<init>(r8, r2);	 Catch:{ Exception -> 0x0249 }
        r0 = r0.setShortLabel(r11);	 Catch:{ Exception -> 0x0249 }
        r0 = r0.setIntent(r4);	 Catch:{ Exception -> 0x0249 }
        if (r13 == 0) goto L_0x0199;
    L_0x0191:
        r2 = android.graphics.drawable.Icon.createWithBitmap(r13);	 Catch:{ Exception -> 0x0249 }
        r0.setIcon(r2);	 Catch:{ Exception -> 0x0249 }
        goto L_0x01d2;
    L_0x0199:
        if (r6 == 0) goto L_0x01b3;
    L_0x019b:
        r2 = r6.bot;	 Catch:{ Exception -> 0x0249 }
        if (r2 == 0) goto L_0x01a9;
    L_0x019f:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r2 = android.graphics.drawable.Icon.createWithResource(r2, r12);	 Catch:{ Exception -> 0x0249 }
        r0.setIcon(r2);	 Catch:{ Exception -> 0x0249 }
        goto L_0x01d2;
    L_0x01a9:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r2 = android.graphics.drawable.Icon.createWithResource(r2, r14);	 Catch:{ Exception -> 0x0249 }
        r0.setIcon(r2);	 Catch:{ Exception -> 0x0249 }
        goto L_0x01d2;
    L_0x01b3:
        if (r7 == 0) goto L_0x01d2;
    L_0x01b5:
        r2 = org.telegram.messenger.ChatObject.isChannel(r7);	 Catch:{ Exception -> 0x0249 }
        if (r2 == 0) goto L_0x01c9;
    L_0x01bb:
        r2 = r7.megagroup;	 Catch:{ Exception -> 0x0249 }
        if (r2 != 0) goto L_0x01c9;
    L_0x01bf:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r2 = android.graphics.drawable.Icon.createWithResource(r2, r9);	 Catch:{ Exception -> 0x0249 }
        r0.setIcon(r2);	 Catch:{ Exception -> 0x0249 }
        goto L_0x01d2;
    L_0x01c9:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r2 = android.graphics.drawable.Icon.createWithResource(r2, r10);	 Catch:{ Exception -> 0x0249 }
        r0.setIcon(r2);	 Catch:{ Exception -> 0x0249 }
    L_0x01d2:
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r3 = android.content.pm.ShortcutManager.class;
        r2 = r2.getSystemService(r3);	 Catch:{ Exception -> 0x0249 }
        r2 = (android.content.pm.ShortcutManager) r2;	 Catch:{ Exception -> 0x0249 }
        r0 = r0.build();	 Catch:{ Exception -> 0x0249 }
        r3 = 0;
        r2.requestPinShortcut(r0, r3);	 Catch:{ Exception -> 0x0249 }
        goto L_0x024d;
    L_0x01e5:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x0249 }
        r0.<init>();	 Catch:{ Exception -> 0x0249 }
        if (r13 == 0) goto L_0x01f2;
    L_0x01ec:
        r2 = "android.intent.extra.shortcut.ICON";
        r0.putExtra(r2, r13);	 Catch:{ Exception -> 0x0249 }
        goto L_0x022d;
    L_0x01f2:
        r2 = "android.intent.extra.shortcut.ICON_RESOURCE";
        if (r6 == 0) goto L_0x020e;
    L_0x01f6:
        r3 = r6.bot;	 Catch:{ Exception -> 0x0249 }
        if (r3 == 0) goto L_0x0204;
    L_0x01fa:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r3 = android.content.Intent.ShortcutIconResource.fromContext(r3, r12);	 Catch:{ Exception -> 0x0249 }
        r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x0249 }
        goto L_0x022d;
    L_0x0204:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r3 = android.content.Intent.ShortcutIconResource.fromContext(r3, r14);	 Catch:{ Exception -> 0x0249 }
        r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x0249 }
        goto L_0x022d;
    L_0x020e:
        if (r7 == 0) goto L_0x022d;
    L_0x0210:
        r3 = org.telegram.messenger.ChatObject.isChannel(r7);	 Catch:{ Exception -> 0x0249 }
        if (r3 == 0) goto L_0x0224;
    L_0x0216:
        r3 = r7.megagroup;	 Catch:{ Exception -> 0x0249 }
        if (r3 != 0) goto L_0x0224;
    L_0x021a:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r3 = android.content.Intent.ShortcutIconResource.fromContext(r3, r9);	 Catch:{ Exception -> 0x0249 }
        r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x0249 }
        goto L_0x022d;
    L_0x0224:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r3 = android.content.Intent.ShortcutIconResource.fromContext(r3, r10);	 Catch:{ Exception -> 0x0249 }
        r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x0249 }
    L_0x022d:
        r2 = "android.intent.extra.shortcut.INTENT";
        r0.putExtra(r2, r4);	 Catch:{ Exception -> 0x0249 }
        r2 = "android.intent.extra.shortcut.NAME";
        r0.putExtra(r2, r11);	 Catch:{ Exception -> 0x0249 }
        r2 = "duplicate";
        r3 = 0;
        r0.putExtra(r2, r3);	 Catch:{ Exception -> 0x0249 }
        r2 = "com.android.launcher.action.INSTALL_SHORTCUT";
        r0.setAction(r2);	 Catch:{ Exception -> 0x0249 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0249 }
        r2.sendBroadcast(r0);	 Catch:{ Exception -> 0x0249 }
        goto L_0x024d;
    L_0x0248:
        return;
    L_0x0249:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x024d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.installShortcut(long):void");
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
                    EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i2));
                    if (encryptedChat != null) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    } else {
                        return;
                    }
                } else if (i > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                } else if (i < 0) {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
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

    public /* synthetic */ void lambda$loadPinnedMessage$85$DataQuery(long j, int i, int i2) {
        loadPinnedMessageInternal(j, i, i2, false);
    }

    public MessageObject loadPinnedMessage(long j, int i, int i2, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(j, i, i2, true);
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$hzk0ktoumSLB8vFJVERgSnj2uGI(this, j, i, i2));
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x008b A:{Catch:{ Exception -> 0x016f }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x012c A:{Catch:{ Exception -> 0x016f }} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e4 A:{Catch:{ Exception -> 0x016f }} */
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
        r6 = new java.util.ArrayList;	 Catch:{ Exception -> 0x016f }
        r6.<init>();	 Catch:{ Exception -> 0x016f }
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x016f }
        r9.<init>();	 Catch:{ Exception -> 0x016f }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x016f }
        r10.<init>();	 Catch:{ Exception -> 0x016f }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x016f }
        r11.<init>();	 Catch:{ Exception -> 0x016f }
        r12 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r12);	 Catch:{ Exception -> 0x016f }
        r12 = r12.getDatabase();	 Catch:{ Exception -> 0x016f }
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x016f }
        r14 = "SELECT data, mid, date FROM messages WHERE mid = %d";
        r15 = 1;
        r8 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x016f }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x016f }
        r5 = 0;
        r8[r5] = r4;	 Catch:{ Exception -> 0x016f }
        r4 = java.lang.String.format(r13, r14, r8);	 Catch:{ Exception -> 0x016f }
        r8 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x016f }
        r4 = r12.queryFinalized(r4, r8);	 Catch:{ Exception -> 0x016f }
        r8 = r4.next();	 Catch:{ Exception -> 0x016f }
        if (r8 == 0) goto L_0x0085;
    L_0x004e:
        r8 = r4.byteBufferValue(r5);	 Catch:{ Exception -> 0x016f }
        if (r8 == 0) goto L_0x0085;
    L_0x0054:
        r12 = r8.readInt32(r5);	 Catch:{ Exception -> 0x016f }
        r12 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r8, r12, r5);	 Catch:{ Exception -> 0x016f }
        r13 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);	 Catch:{ Exception -> 0x016f }
        r13 = r13.clientUserId;	 Catch:{ Exception -> 0x016f }
        r12.readAttachPath(r8, r13);	 Catch:{ Exception -> 0x016f }
        r8.reuse();	 Catch:{ Exception -> 0x016f }
        r8 = r12.action;	 Catch:{ Exception -> 0x016f }
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x016f }
        if (r8 == 0) goto L_0x0071;
    L_0x0070:
        goto L_0x0085;
    L_0x0071:
        r8 = r4.intValue(r15);	 Catch:{ Exception -> 0x016f }
        r12.id = r8;	 Catch:{ Exception -> 0x016f }
        r8 = 2;
        r8 = r4.intValue(r8);	 Catch:{ Exception -> 0x016f }
        r12.date = r8;	 Catch:{ Exception -> 0x016f }
        r12.dialog_id = r0;	 Catch:{ Exception -> 0x016f }
        org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r12, r10, r11);	 Catch:{ Exception -> 0x016f }
        r8 = r12;
        goto L_0x0086;
    L_0x0085:
        r8 = 0;
    L_0x0086:
        r4.dispose();	 Catch:{ Exception -> 0x016f }
        if (r8 != 0) goto L_0x00e2;
    L_0x008b:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x016f }
        r4 = r4.getDatabase();	 Catch:{ Exception -> 0x016f }
        r12 = java.util.Locale.US;	 Catch:{ Exception -> 0x016f }
        r13 = "SELECT data FROM chat_pinned WHERE uid = %d";
        r14 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x016f }
        r15 = java.lang.Long.valueOf(r17);	 Catch:{ Exception -> 0x016f }
        r14[r5] = r15;	 Catch:{ Exception -> 0x016f }
        r12 = java.lang.String.format(r12, r13, r14);	 Catch:{ Exception -> 0x016f }
        r13 = new java.lang.Object[r5];	 Catch:{ Exception -> 0x016f }
        r4 = r4.queryFinalized(r12, r13);	 Catch:{ Exception -> 0x016f }
        r12 = r4.next();	 Catch:{ Exception -> 0x016f }
        if (r12 == 0) goto L_0x00df;
    L_0x00b1:
        r12 = r4.byteBufferValue(r5);	 Catch:{ Exception -> 0x016f }
        if (r12 == 0) goto L_0x00df;
    L_0x00b7:
        r8 = r12.readInt32(r5);	 Catch:{ Exception -> 0x016f }
        r8 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r12, r8, r5);	 Catch:{ Exception -> 0x016f }
        r5 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);	 Catch:{ Exception -> 0x016f }
        r5 = r5.clientUserId;	 Catch:{ Exception -> 0x016f }
        r8.readAttachPath(r12, r5);	 Catch:{ Exception -> 0x016f }
        r12.reuse();	 Catch:{ Exception -> 0x016f }
        r5 = r8.id;	 Catch:{ Exception -> 0x016f }
        if (r5 != r3) goto L_0x00de;
    L_0x00d1:
        r5 = r8.action;	 Catch:{ Exception -> 0x016f }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;	 Catch:{ Exception -> 0x016f }
        if (r5 == 0) goto L_0x00d8;
    L_0x00d7:
        goto L_0x00de;
    L_0x00d8:
        r8.dialog_id = r0;	 Catch:{ Exception -> 0x016f }
        org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r10, r11);	 Catch:{ Exception -> 0x016f }
        goto L_0x00df;
    L_0x00de:
        r8 = 0;
    L_0x00df:
        r4.dispose();	 Catch:{ Exception -> 0x016f }
    L_0x00e2:
        if (r8 != 0) goto L_0x012c;
    L_0x00e4:
        if (r2 == 0) goto L_0x010f;
    L_0x00e6:
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages;	 Catch:{ Exception -> 0x016f }
        r0.<init>();	 Catch:{ Exception -> 0x016f }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);	 Catch:{ Exception -> 0x016f }
        r1 = r1.getInputChannel(r2);	 Catch:{ Exception -> 0x016f }
        r0.channel = r1;	 Catch:{ Exception -> 0x016f }
        r1 = r0.id;	 Catch:{ Exception -> 0x016f }
        r3 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x016f }
        r1.add(r3);	 Catch:{ Exception -> 0x016f }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x016f }
        r3 = new org.telegram.messenger.-$$Lambda$DataQuery$sut4gZqSHSa63dx_2bNsxpCLASSNAMESk;	 Catch:{ Exception -> 0x016f }
        r3.<init>(r7, r2);	 Catch:{ Exception -> 0x016f }
        r1.sendRequest(r0, r3);	 Catch:{ Exception -> 0x016f }
        goto L_0x0173;
    L_0x010f:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages;	 Catch:{ Exception -> 0x016f }
        r0.<init>();	 Catch:{ Exception -> 0x016f }
        r1 = r0.id;	 Catch:{ Exception -> 0x016f }
        r3 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x016f }
        r1.add(r3);	 Catch:{ Exception -> 0x016f }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x016f }
        r3 = new org.telegram.messenger.-$$Lambda$DataQuery$bn-59i2M3GKJW8EWB0ORPpDQN_w;	 Catch:{ Exception -> 0x016f }
        r3.<init>(r7, r2);	 Catch:{ Exception -> 0x016f }
        r1.sendRequest(r0, r3);	 Catch:{ Exception -> 0x016f }
        goto L_0x0173;
    L_0x012c:
        if (r21 == 0) goto L_0x013b;
    L_0x012e:
        r5 = 1;
        r1 = r16;
        r2 = r8;
        r3 = r6;
        r4 = r9;
        r6 = r21;
        r0 = r1.broadcastPinnedMessage(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x016f }
        return r0;
    L_0x013b:
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x016f }
        r1 = ",";
        if (r0 != 0) goto L_0x0150;
    L_0x0143:
        r0 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x016f }
        r2 = android.text.TextUtils.join(r1, r10);	 Catch:{ Exception -> 0x016f }
        r0.getUsersInternal(r2, r6);	 Catch:{ Exception -> 0x016f }
    L_0x0150:
        r0 = r11.isEmpty();	 Catch:{ Exception -> 0x016f }
        if (r0 != 0) goto L_0x0163;
    L_0x0156:
        r0 = r7.currentAccount;	 Catch:{ Exception -> 0x016f }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x016f }
        r1 = android.text.TextUtils.join(r1, r11);	 Catch:{ Exception -> 0x016f }
        r0.getChatsInternal(r1, r9);	 Catch:{ Exception -> 0x016f }
    L_0x0163:
        r5 = 1;
        r0 = 0;
        r1 = r16;
        r2 = r8;
        r3 = r6;
        r4 = r9;
        r6 = r0;
        r1.broadcastPinnedMessage(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x016f }
        goto L_0x0173;
    L_0x016f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0173:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.loadPinnedMessageInternal(long, int, int, boolean):org.telegram.messenger.MessageObject");
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0047  */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$86$DataQuery(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        if (r11 != 0) goto L_0x0044;
    L_0x0004:
        r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10;
        r11 = r10.messages;
        removeEmptyMessages(r11);
        r11 = r10.messages;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x0044;
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
        r11 = r8.currentAccount;
        r11 = org.telegram.messenger.MessagesStorage.getInstance(r11);
        r2 = r10.users;
        r3 = r10.chats;
        r11.putUsersAndChats(r2, r3, r0, r0);
        r10 = r10.messages;
        r10 = r10.get(r1);
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;
        r8.savePinnedMessage(r10);
        goto L_0x0045;
    L_0x0044:
        r0 = 0;
    L_0x0045:
        if (r0 != 0) goto L_0x0050;
    L_0x0047:
        r10 = r8.currentAccount;
        r10 = org.telegram.messenger.MessagesStorage.getInstance(r10);
        r10.updateChatPinnedMessage(r9, r1);
    L_0x0050:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$loadPinnedMessageInternal$86$DataQuery(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0047  */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$87$DataQuery(int r9, org.telegram.tgnet.TLObject r10, org.telegram.tgnet.TLRPC.TL_error r11) {
        /*
        r8 = this;
        r0 = 1;
        r1 = 0;
        if (r11 != 0) goto L_0x0044;
    L_0x0004:
        r10 = (org.telegram.tgnet.TLRPC.messages_Messages) r10;
        r11 = r10.messages;
        removeEmptyMessages(r11);
        r11 = r10.messages;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x0044;
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
        r11 = r8.currentAccount;
        r11 = org.telegram.messenger.MessagesStorage.getInstance(r11);
        r2 = r10.users;
        r3 = r10.chats;
        r11.putUsersAndChats(r2, r3, r0, r0);
        r10 = r10.messages;
        r10 = r10.get(r1);
        r10 = (org.telegram.tgnet.TLRPC.Message) r10;
        r8.savePinnedMessage(r10);
        goto L_0x0045;
    L_0x0044:
        r0 = 0;
    L_0x0045:
        if (r0 != 0) goto L_0x0050;
    L_0x0047:
        r10 = r8.currentAccount;
        r10 = org.telegram.messenger.MessagesStorage.getInstance(r10);
        r10.updateChatPinnedMessage(r9, r1);
    L_0x0050:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$loadPinnedMessageInternal$87$DataQuery(int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessage(Message message) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$9sCQAO18nnba6ToshnaeVPhZWRk(this, message));
    }

    public /* synthetic */ void lambda$savePinnedMessage$88$DataQuery(Message message) {
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
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
                executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                message.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, message.id);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            } else {
                return;
            }
            i = -i;
            j = (long) i;
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
            nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
            message.serializeToStream(nativeByteBuffer);
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, message.id);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$Yx2wTX87h9OMWDNW8TJbWVjbdlk(this, arrayList, z, arrayList2, message, sparseArray, sparseArray2));
        return null;
    }

    public /* synthetic */ void lambda$broadcastPinnedMessage$89$DataQuery(ArrayList arrayList, boolean z, ArrayList arrayList2, Message message, SparseArray sparseArray, SparseArray sparseArray2) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, z);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, z);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.pinnedMessageDidLoad, new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false));
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
        if (((int) j) == 0) {
            arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            while (i < arrayList.size()) {
                messageObject = (MessageObject) arrayList2.get(i);
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
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$WzRDIT94H3f0FgoSArkqNqup9dc(this, arrayList3, j, longSparseArray));
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
                ArrayList arrayList5 = (ArrayList) sparseArray.get(i3);
                if (arrayList5 == null) {
                    arrayList5 = new ArrayList();
                    sparseArray.put(i3, arrayList5);
                }
                arrayList5.add(messageObject);
                if (!arrayList3.contains(Integer.valueOf(i3))) {
                    arrayList3.add(Integer.valueOf(i3));
                }
            }
            i++;
        }
        if (!arrayList3.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$BoxhS9ivFCv8Vy62eC0FaKjPf8o(this, stringBuilder, j, arrayList3, sparseArray, i2));
        }
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$91$DataQuery(ArrayList arrayList, long j, LongSparseArray longSparseArray) {
        try {
            ArrayList arrayList2;
            int i;
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$Xhb2v_0o_Dm3WG47KU6zYke67-0(this, j));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$90$DataQuery(long j) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
    }

    public /* synthetic */ void lambda$loadReplyMessagesForMessages$94$DataQuery(StringBuilder stringBuilder, long j, ArrayList arrayList, SparseArray sparseArray, int i) {
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
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
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
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(str, arrayList6), arrayList4);
            }
            if (!arrayList7.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(str, arrayList7), arrayList5);
            }
            broadcastReplyMessages(arrayList3, sparseArray, arrayList4, arrayList5, j, true);
            if (!arrayList.isEmpty()) {
                if (i2 != 0) {
                    TL_channels_getMessages tL_channels_getMessages = new TL_channels_getMessages();
                    tL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i2);
                    tL_channels_getMessages.id = arrayList2;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new -$$Lambda$DataQuery$JW_1UL1JA9UbvXsdwarITz2ppQM(this, sparseArray2, j2));
                    return;
                }
                TL_messages_getMessages tL_messages_getMessages = new TL_messages_getMessages();
                tL_messages_getMessages.id = arrayList2;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new -$$Lambda$DataQuery$DP0ftrGTBszIUd99KwPuGmnwqBM(this, sparseArray2, j2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$92$DataQuery(SparseArray sparseArray, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeEmptyMessages(messages_messages.messages);
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages);
        }
    }

    public /* synthetic */ void lambda$null$93$DataQuery(SparseArray sparseArray, long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            removeEmptyMessages(messages_messages.messages);
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            saveReplyMessages(sparseArray, messages_messages.messages);
        }
    }

    private void saveReplyMessages(SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<Message> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$H2PEh31YFWmyOEdst-RwAHB_-gE(this, arrayList, sparseArray));
    }

    public /* synthetic */ void lambda$saveReplyMessages$95$DataQuery(ArrayList arrayList, SparseArray sparseArray) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
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
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$j3QDbBWi358gz3ac8yzLPqCuKcU(this, arrayList2, z, arrayList3, arrayList, sparseArray, sparseArray2, sparseArray3, j));
    }

    public /* synthetic */ void lambda$broadcastReplyMessages$96$DataQuery(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3, long j) {
        boolean z2 = z;
        ArrayList arrayList4 = arrayList;
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, z2);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, z2);
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j));
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
        if (r20 == 0) goto L_0x03cf;
    L_0x0005:
        r2 = 0;
        r3 = r20[r2];
        if (r3 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x03cf;
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
        if (r1 == 0) goto L_0x02cb;
    L_0x01a0:
        r1 = r20[r2];
        r1 = (android.text.Spanned) r1;
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.TypefaceSpan.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.TypefaceSpan[]) r3;
        if (r3 == 0) goto L_0x0207;
    L_0x01b4:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x0207;
    L_0x01b7:
        r4 = 0;
    L_0x01b8:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x0207;
    L_0x01bb:
        r5 = r3[r4];
        r9 = r1.getSpanStart(r5);
        r12 = r1.getSpanEnd(r5);
        r13 = checkInclusion(r9, r6);
        if (r13 != 0) goto L_0x0204;
    L_0x01cb:
        r13 = checkInclusion(r12, r6);
        if (r13 != 0) goto L_0x0204;
    L_0x01d1:
        r13 = checkIntersection(r9, r12, r6);
        if (r13 == 0) goto L_0x01d8;
    L_0x01d7:
        goto L_0x0204;
    L_0x01d8:
        if (r6 != 0) goto L_0x01df;
    L_0x01da:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x01df:
        r13 = r5.isMono();
        if (r13 == 0) goto L_0x01eb;
    L_0x01e5:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode;
        r5.<init>();
        goto L_0x01fc;
    L_0x01eb:
        r5 = r5.isBold();
        if (r5 == 0) goto L_0x01f7;
    L_0x01f1:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold;
        r5.<init>();
        goto L_0x01fc;
    L_0x01f7:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
        r5.<init>();
    L_0x01fc:
        r5.offset = r9;
        r12 = r12 - r9;
        r5.length = r12;
        r6.add(r5);
    L_0x0204:
        r4 = r4 + 1;
        goto L_0x01b8;
    L_0x0207:
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.URLSpanUserMention.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.URLSpanUserMention[]) r3;
        if (r3 == 0) goto L_0x027c;
    L_0x0217:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x027c;
    L_0x021a:
        if (r6 != 0) goto L_0x0221;
    L_0x021c:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x0221:
        r4 = 0;
    L_0x0222:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x027c;
    L_0x0225:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
        r5.<init>();
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r12 = r3[r4];
        r12 = r12.getURL();
        r12 = org.telegram.messenger.Utilities.parseInt(r12);
        r12 = r12.intValue();
        r9 = r9.getInputUser(r12);
        r5.user_id = r9;
        r9 = r5.user_id;
        if (r9 == 0) goto L_0x0279;
    L_0x0248:
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
        if (r9 != r8) goto L_0x0276;
    L_0x0271:
        r9 = r5.length;
        r9 = r9 - r11;
        r5.length = r9;
    L_0x0276:
        r6.add(r5);
    L_0x0279:
        r4 = r4 + 1;
        goto L_0x0222;
    L_0x027c:
        r3 = r20[r2];
        r3 = r3.length();
        r4 = org.telegram.ui.Components.URLSpanReplacement.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (org.telegram.ui.Components.URLSpanReplacement[]) r3;
        if (r3 == 0) goto L_0x02cb;
    L_0x028c:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x02cb;
    L_0x028f:
        if (r6 != 0) goto L_0x0297;
    L_0x0291:
        r4 = new java.util.ArrayList;
        r4.<init>();
        r6 = r4;
    L_0x0297:
        r4 = 0;
    L_0x0298:
        r5 = r3.length;
        if (r4 >= r5) goto L_0x02cb;
    L_0x029b:
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
        goto L_0x0298;
    L_0x02cb:
        r1 = 0;
    L_0x02cc:
        if (r1 >= r10) goto L_0x03ce;
    L_0x02ce:
        if (r1 != 0) goto L_0x02d3;
    L_0x02d0:
        r3 = "**";
        goto L_0x02d5;
    L_0x02d3:
        r3 = "__";
    L_0x02d5:
        if (r1 != 0) goto L_0x02da;
    L_0x02d7:
        r4 = 42;
        goto L_0x02dc;
    L_0x02da:
        r4 = 95;
    L_0x02dc:
        r5 = 0;
        r9 = -1;
    L_0x02de:
        r12 = r20[r2];
        r5 = android.text.TextUtils.indexOf(r12, r3, r5);
        r12 = -1;
        if (r5 == r12) goto L_0x03c4;
    L_0x02e7:
        if (r9 != r12) goto L_0x0304;
    L_0x02e9:
        if (r5 != 0) goto L_0x02ee;
    L_0x02eb:
        r13 = 32;
        goto L_0x02f6;
    L_0x02ee:
        r13 = r20[r2];
        r14 = r5 + -1;
        r13 = r13.charAt(r14);
    L_0x02f6:
        r14 = checkInclusion(r5, r6);
        if (r14 != 0) goto L_0x0301;
    L_0x02fc:
        if (r13 == r8) goto L_0x0300;
    L_0x02fe:
        if (r13 != r7) goto L_0x0301;
    L_0x0300:
        r9 = r5;
    L_0x0301:
        r5 = r5 + 2;
        goto L_0x02de;
    L_0x0304:
        r13 = r5 + 2;
    L_0x0306:
        r14 = r20[r2];
        r14 = r14.length();
        if (r13 >= r14) goto L_0x031b;
    L_0x030e:
        r14 = r20[r2];
        r14 = r14.charAt(r13);
        if (r14 != r4) goto L_0x031b;
    L_0x0316:
        r5 = r5 + 1;
        r13 = r13 + 1;
        goto L_0x0306;
    L_0x031b:
        r13 = r5 + 2;
        r14 = checkInclusion(r5, r6);
        if (r14 != 0) goto L_0x03ba;
    L_0x0323:
        r14 = checkIntersection(r9, r5, r6);
        if (r14 == 0) goto L_0x032b;
    L_0x0329:
        goto L_0x03ba;
    L_0x032b:
        r14 = r9 + 2;
        if (r14 == r5) goto L_0x03b8;
    L_0x032f:
        if (r6 != 0) goto L_0x0336;
    L_0x0331:
        r6 = new java.util.ArrayList;
        r6.<init>();
    L_0x0336:
        r15 = 3;
        r7 = new java.lang.CharSequence[r15];	 Catch:{ Exception -> 0x035e }
        r8 = r20[r2];	 Catch:{ Exception -> 0x035e }
        r8 = r0.substring(r8, r2, r9);	 Catch:{ Exception -> 0x035e }
        r7[r2] = r8;	 Catch:{ Exception -> 0x035e }
        r8 = r20[r2];	 Catch:{ Exception -> 0x035e }
        r8 = r0.substring(r8, r14, r5);	 Catch:{ Exception -> 0x035e }
        r7[r11] = r8;	 Catch:{ Exception -> 0x035e }
        r8 = r20[r2];	 Catch:{ Exception -> 0x035e }
        r17 = r20[r2];	 Catch:{ Exception -> 0x035e }
        r11 = r17.length();	 Catch:{ Exception -> 0x035e }
        r8 = r0.substring(r8, r13, r11);	 Catch:{ Exception -> 0x035e }
        r7[r10] = r8;	 Catch:{ Exception -> 0x035e }
        r7 = org.telegram.messenger.AndroidUtilities.concat(r7);	 Catch:{ Exception -> 0x035e }
        r20[r2] = r7;	 Catch:{ Exception -> 0x035e }
        goto L_0x0396;
    L_0x035e:
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
    L_0x0396:
        if (r1 != 0) goto L_0x039e;
    L_0x0398:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityBold;
        r7.<init>();
        goto L_0x03a3;
    L_0x039e:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
        r7.<init>();
    L_0x03a3:
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
        goto L_0x03bb;
    L_0x03b8:
        r15 = 3;
        goto L_0x03bb;
    L_0x03ba:
        r15 = 3;
    L_0x03bb:
        r5 = r13;
        r7 = 10;
        r8 = 32;
        r9 = -1;
        r11 = 1;
        goto L_0x02de;
    L_0x03c4:
        r15 = 3;
        r1 = r1 + 1;
        r7 = 10;
        r8 = 32;
        r11 = 1;
        goto L_0x02cc;
    L_0x03ce:
        return r6;
    L_0x03cf:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.getEntities(java.lang.CharSequence[]):java.util.ArrayList");
    }

    public void loadDrafts() {
        if (!UserConfig.getInstance(this.currentAccount).draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getAllDrafts(), new -$$Lambda$DataQuery$A03PPqgH8dlVye1igOOaKjX18Mo(this));
        }
    }

    public /* synthetic */ void lambda$loadDrafts$98$DataQuery(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$VlsaxyuL0jbFdSJtmR9unZb00xo(this));
        }
    }

    public /* synthetic */ void lambda$null$97$DataQuery() {
        UserConfig.getInstance(this.currentAccount).draftsLoaded = true;
        this.loadingDrafts = false;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
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
                tL_messages_saveDraft.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                if (tL_messages_saveDraft.peer != null) {
                    tL_messages_saveDraft.message = tL_draftMessageEmpty.message;
                    tL_messages_saveDraft.no_webpage = tL_draftMessageEmpty.no_webpage;
                    tL_messages_saveDraft.reply_to_msg_id = tL_draftMessageEmpty.reply_to_msg_id;
                    tL_messages_saveDraft.entities = tL_draftMessageEmpty.entities;
                    tL_messages_saveDraft.flags = tL_draftMessageEmpty.flags;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveDraft, -$$Lambda$DataQuery$jWB8qVUldoWOaeSwZryuPoQq0I0.INSTANCE);
                } else {
                    return;
                }
            }
            MessagesController.getInstance(this.currentAccount).sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                } else {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
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
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$JgCeTQWWzbO2EPbV9fjIFzenxxY(this, j2, i2, j));
                }
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    public /* synthetic */ void lambda$saveDraft$102$DataQuery(long j, int i, long j2) {
        Message message = null;
        try {
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    message.readAttachPath(byteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (message != null) {
                saveDraftReplyMessage(j2, message);
            } else if (i != 0) {
                TL_channels_getMessages tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i);
                tL_channels_getMessages.id.add(Integer.valueOf((int) j));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new -$$Lambda$DataQuery$Fmy_RKu5TkGeEmPjdx9-EaHjKV0(this, j2));
            } else {
                TL_messages_getMessages tL_messages_getMessages = new TL_messages_getMessages();
                tL_messages_getMessages.id.add(Integer.valueOf((int) j));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new -$$Lambda$DataQuery$a_Ly20evk0XxP_zooJOMw6rmEJc(this, j2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$100$DataQuery(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, (Message) messages_messages.messages.get(0));
            }
        }
    }

    public /* synthetic */ void lambda$null$101$DataQuery(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            if (!messages_messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, (Message) messages_messages.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long j, Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$d7oypRr3g0xB92hPNQy49uD_p8A(this, j, message));
        }
    }

    public /* synthetic */ void lambda$saveDraftReplyMessage$103$DataQuery(long j, Message message) {
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
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
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
                MessagesController.getInstance(this.currentAccount).sortDialogs(null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$4JOcfs_Jho1Vm_zEM2xMi0yW30I(this, arrayList, j));
    }

    public /* synthetic */ void lambda$clearBotKeyboard$104$DataQuery(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long j2 = this.botKeyboardsByMids.get(((Integer) arrayList.get(i)).intValue());
                if (j2 != 0) {
                    this.botKeyboards.remove(j2);
                    this.botKeyboardsByMids.delete(((Integer) arrayList.get(i)).intValue());
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j2));
                }
            }
            return;
        }
        this.botKeyboards.remove(j);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j));
    }

    public void loadBotKeyboard(long j) {
        if (((Message) this.botKeyboards.get(j)) != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, r0, Long.valueOf(j));
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$CLASSNAMEbX86RVrRMk7D3n7tAtGTuntU(this, j));
    }

    public /* synthetic */ void lambda$loadBotKeyboard$106$DataQuery(long j) {
        Message message = null;
        try {
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (message != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$pG7ILjNgxGBHl9Rr7Ps_HNlhdG8(this, message, j));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$105$DataQuery(Message message, long j) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void loadBotInfo(int i, boolean z, int i2) {
        if (!z || ((BotInfo) this.botInfos.get(i)) == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$RU0vB5vrHjb7JApo8zy3krcj73Y(this, i, i2));
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, r5, Integer.valueOf(i2));
    }

    public /* synthetic */ void lambda$loadBotInfo$108$DataQuery(int i, int i2) {
        BotInfo botInfo = null;
        try {
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    botInfo = BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            if (botInfo != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$8CCh4-uK8so3qsC-8J2GOPVArpo(this, botInfo, i2));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$107$DataQuery(BotInfo botInfo, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, botInfo, Integer.valueOf(i));
    }

    public void putBotKeyboard(long j, Message message) {
        if (message != null) {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
                Object[] objArr = new Object[1];
                int i = 0;
                objArr[0] = Long.valueOf(j);
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", objArr), new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (i < message.id) {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, message.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$g5fV5AzEjpYa9k_FQikVmx9CWxY(this, j, message));
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$putBotKeyboard$109$DataQuery(long j, Message message) {
        Message message2 = (Message) this.botKeyboards.get(j);
        this.botKeyboards.put(j, message);
        if (message2 != null) {
            this.botKeyboardsByMids.delete(message2.id);
        }
        this.botKeyboardsByMids.put(message.id, j);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoad, message, Long.valueOf(j));
    }

    public void putBotInfo(BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$Z9uUIzBqjWHBx6zjrK5T9uXZVkc(this, botInfo));
        }
    }

    public /* synthetic */ void lambda$putBotInfo$110$DataQuery(BotInfo botInfo) {
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
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
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$PLdKh5WsK6t3m__-OQnCRDAEWbk(this, charSequence));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$116$DataQuery(java.lang.String r10) {
        /*
        r9 = this;
        r0 = -1;
        r1 = 0;
        r2 = 0;
        r4 = r9.currentAccount;	 Catch:{ Exception -> 0x0035 }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x0035 }
        r4 = r4.getDatabase();	 Catch:{ Exception -> 0x0035 }
        r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?";
        r6 = 1;
        r7 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x0035 }
        r8 = 0;
        r7[r8] = r10;	 Catch:{ Exception -> 0x0035 }
        r4 = r4.queryFinalized(r5, r7);	 Catch:{ Exception -> 0x0035 }
        r5 = r4.next();	 Catch:{ Exception -> 0x0035 }
        if (r5 == 0) goto L_0x002e;
    L_0x0020:
        r1 = r4.stringValue(r8);	 Catch:{ Exception -> 0x0035 }
        r5 = r4.intValue(r6);	 Catch:{ Exception -> 0x0035 }
        r6 = 2;
        r2 = r4.longValue(r6);	 Catch:{ Exception -> 0x0033 }
        goto L_0x002f;
    L_0x002e:
        r5 = -1;
    L_0x002f:
        r4.dispose();	 Catch:{ Exception -> 0x0033 }
        goto L_0x003a;
    L_0x0033:
        r4 = move-exception;
        goto L_0x0037;
    L_0x0035:
        r4 = move-exception;
        r5 = -1;
    L_0x0037:
        org.telegram.messenger.FileLog.e(r4);
    L_0x003a:
        r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r4 != 0) goto L_0x0057;
    L_0x003e:
        r6 = java.lang.System.currentTimeMillis();
        r6 = r6 - r2;
        r2 = java.lang.Math.abs(r6);
        r6 = 3600000; // 0x36ee80 float:5.044674E-39 double:1.7786363E-317;
        r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x0057;
    L_0x004e:
        r0 = new org.telegram.messenger.-$$Lambda$DataQuery$j5bKk2bgx79DQU8ExYLHBW0OgBI;
        r0.<init>(r9, r10);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
    L_0x0057:
        if (r5 != r0) goto L_0x0061;
    L_0x0059:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords;
        r0.<init>();
        r0.lang_code = r10;
        goto L_0x006a;
    L_0x0061:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference;
        r0.<init>();
        r0.lang_code = r10;
        r0.from_version = r5;
    L_0x006a:
        r2 = r9.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.messenger.-$$Lambda$DataQuery$3jRza4bNh4kyolsI4k84JKOEvVQ;
        r3.<init>(r9, r5, r1, r10);
        r2.sendRequest(r0, r3);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.lambda$fetchNewEmojiKeywords$116$DataQuery(java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$111$DataQuery(String str) {
        Boolean bool = (Boolean) this.currentFetchingEmoji.remove(str);
    }

    public /* synthetic */ void lambda$null$115$DataQuery(int i, String str, String str2, TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            TL_emojiKeywordsDifference tL_emojiKeywordsDifference = (TL_emojiKeywordsDifference) tLObject;
            if (i == -1 || tL_emojiKeywordsDifference.lang_code.equals(str)) {
                putEmojiKeywords(str2, tL_emojiKeywordsDifference);
                return;
            } else {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$tmdFPTFKuPJBcR8hsRDSJe8XFo4(this, str2));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$9eXVS7UU9pt2ANgZ-_XiV0PmN8Y(this, str2));
    }

    public /* synthetic */ void lambda$null$113$DataQuery(String str) {
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$DNPRuAZm79Q9W4IPII5JfioAwcA(this, str));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$112$DataQuery(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    public /* synthetic */ void lambda$null$114$DataQuery(String str) {
        Boolean bool = (Boolean) this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(String str, TL_emojiKeywordsDifference tL_emojiKeywordsDifference) {
        if (tL_emojiKeywordsDifference != null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$_y89sXvt9qXhyQF6eZF_c0a7Hdc(this, tL_emojiKeywordsDifference, str));
        }
    }

    public /* synthetic */ void lambda$putEmojiKeywords$118$DataQuery(TL_emojiKeywordsDifference tL_emojiKeywordsDifference, String str) {
        try {
            SQLitePreparedStatement executeFast;
            if (!tL_emojiKeywordsDifference.keywords.isEmpty()) {
                executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
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
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
                executeFast.dispose();
                executeFast2.dispose();
            }
            executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            executeFast.bindString(1, str);
            executeFast.bindString(2, tL_emojiKeywordsDifference.lang_code);
            executeFast.bindInteger(3, tL_emojiKeywordsDifference.version);
            executeFast.bindLong(4, System.currentTimeMillis());
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$TyujBNGo6dMrF4_NTghE5U4jKgQ(this, str));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$117$DataQuery(String str) {
        this.currentFetchingEmoji.remove(str);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DataQuery$gSc9XYndBuKxL05LBD_zU8iVGWA(this, strArr, keywordResultCallback, str, z, new ArrayList(Emoji.recentEmoji), countDownLatch));
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable unused) {
                }
            }
        }
    }

    public /* synthetic */ void lambda$getEmojiSuggestions$122$DataQuery(String[] strArr, KeywordResultCallback keywordResultCallback, String str, boolean z, ArrayList arrayList, CountDownLatch countDownLatch) {
        String[] strArr2 = strArr;
        KeywordResultCallback keywordResultCallback2 = keywordResultCallback;
        ArrayList arrayList2 = new ArrayList();
        HashMap hashMap = new HashMap();
        String str2 = null;
        int i = 0;
        Object obj = null;
        while (i < strArr2.length) {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?", strArr2[i]);
                if (queryFinalized.next()) {
                    str2 = queryFinalized.stringValue(0);
                }
                queryFinalized.dispose();
                if (str2 != null) {
                    obj = 1;
                }
                i++;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (obj == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$F5S3CTjE-FLA0OXEFvyeWWAj6H0(this, strArr2, keywordResultCallback2, arrayList2));
            return;
        }
        String toLowerCase = str.toLowerCase();
        for (int i2 = 0; i2 < 2; i2++) {
            String translitString;
            SQLiteCursor queryFinalized2;
            if (i2 == 1) {
                translitString = LocaleController.getInstance().getTranslitString(toLowerCase, false, false);
                if (translitString.equals(toLowerCase)) {
                }
            } else {
                translitString = toLowerCase;
            }
            StringBuilder stringBuilder = new StringBuilder(translitString);
            int length = stringBuilder.length();
            while (length > 0) {
                length--;
                char charAt = (char) (stringBuilder.charAt(length) + 1);
                stringBuilder.setCharAt(length, charAt);
                if (charAt != 0) {
                    toLowerCase = stringBuilder.toString();
                    break;
                }
            }
            toLowerCase = null;
            if (z) {
                queryFinalized2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?", translitString);
            } else if (toLowerCase != null) {
                queryFinalized2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?", translitString, toLowerCase);
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(translitString);
                stringBuilder.append("%");
                translitString = stringBuilder.toString();
                queryFinalized2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?", translitString);
            }
            while (queryFinalized2.next()) {
                String replace = queryFinalized2.stringValue(0).replace("️", "");
                if (hashMap.get(replace) == null) {
                    hashMap.put(replace, Boolean.valueOf(true));
                    KeywordResult keywordResult = new KeywordResult();
                    keywordResult.emoji = replace;
                    keywordResult.keyword = queryFinalized2.stringValue(1);
                    arrayList2.add(keywordResult);
                }
            }
            queryFinalized2.dispose();
            toLowerCase = translitString;
        }
        Collections.sort(arrayList2, new -$$Lambda$DataQuery$EciNoKVivTg7lU2H2PBLqQwZEOk(arrayList));
        if (countDownLatch != null) {
            keywordResultCallback2.run(arrayList2, str2);
            countDownLatch.countDown();
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DataQuery$WcOlD4X1lYU5wXzTVWls1TBaRSM(keywordResultCallback2, arrayList2, str2));
        }
    }

    public /* synthetic */ void lambda$null$119$DataQuery(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
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

    static /* synthetic */ int lambda$null$120(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
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
