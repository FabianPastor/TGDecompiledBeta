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
import android.text.Spannable;
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
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
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
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanUserMention;

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
    private static Comparator<MessageEntity> entityComparator = new Comparator<MessageEntity>() {
        public int compare(MessageEntity messageEntity, MessageEntity messageEntity2) {
            if (messageEntity.offset > messageEntity2.offset) {
                return 1;
            }
            return messageEntity.offset < messageEntity2.offset ? -1 : null;
        }
    };
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<Document>> allStickers = new HashMap();
    private HashMap<String, ArrayList<Document>> allStickersFeatured = new HashMap();
    private int[] archivedStickersCount = new int[2];
    private SparseArray<BotInfo> botInfos;
    private LongSparseArray<Message> botKeyboards;
    private SparseLongArray botKeyboardsByMids;
    private int currentAccount;
    private LongSparseArray<Message> draftMessages;
    private LongSparseArray<DraftMessage> drafts;
    private ArrayList<StickerSetCovered> featuredStickerSets;
    private LongSparseArray<StickerSetCovered> featuredStickerSetsById;
    private boolean featuredStickersLoaded;
    private LongSparseArray<TL_messages_stickerSet> groupStickerSets = new LongSparseArray();
    public ArrayList<TL_topPeer> hints;
    private boolean inTransaction;
    public ArrayList<TL_topPeer> inlineBots;
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
    private boolean[] loadingRecentStickers;
    private boolean[] loadingStickers = new boolean[4];
    private int mergeReqId;
    private int[] messagesSearchCount;
    private boolean[] messagesSearchEndReached;
    private SharedPreferences preferences;
    private ArrayList<Long> readingStickerSets;
    private ArrayList<Document> recentGifs;
    private boolean recentGifsLoaded;
    private ArrayList<Document>[] recentStickers;
    private boolean[] recentStickersLoaded;
    private int reqId;
    private ArrayList<MessageObject> searchResultMessages;
    private SparseArray<MessageObject>[] searchResultMessagesMap;
    private ArrayList<TL_messages_stickerSet>[] stickerSets;
    private LongSparseArray<TL_messages_stickerSet> stickerSetsById = new LongSparseArray();
    private HashMap<String, TL_messages_stickerSet> stickerSetsByName = new HashMap();
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray();
    private boolean[] stickersLoaded = new boolean[4];
    private ArrayList<Long> unreadStickerSets;

    /* renamed from: org.telegram.messenger.DataQuery$1 */
    class C17921 implements RequestDelegate {
        public void run(TLObject tLObject, TL_error tL_error) {
        }

        C17921() {
        }
    }

    /* renamed from: org.telegram.messenger.DataQuery$3 */
    class C17933 implements RequestDelegate {
        public void run(TLObject tLObject, TL_error tL_error) {
        }

        C17933() {
        }
    }

    /* renamed from: org.telegram.messenger.DataQuery$7 */
    class C17987 implements RequestDelegate {
        C17987() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                final TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) tLObject;
                MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        try {
                            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            executeFast.requery();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("s_");
                            stringBuilder.append(tL_messages_stickerSet.set.id);
                            executeFast.bindString(1, stringBuilder.toString());
                            executeFast.bindInteger(2, 6);
                            executeFast.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
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
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DataQuery.this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(tL_messages_stickerSet.set.id));
                    }
                });
            }
        }
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

    public DataQuery(int r8) {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r7 = this;
        r7.<init>();
        r0 = 4;
        r1 = new java.util.ArrayList[r0];
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = 0;
        r1[r3] = r2;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r4 = 1;
        r1[r4] = r2;
        r2 = new java.util.ArrayList;
        r2.<init>(r3);
        r5 = 2;
        r1[r5] = r2;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r6 = 3;
        r1[r6] = r2;
        r7.stickerSets = r1;
        r1 = new android.util.LongSparseArray;
        r1.<init>();
        r7.stickerSetsById = r1;
        r1 = new android.util.LongSparseArray;
        r1.<init>();
        r7.installedStickerSetsById = r1;
        r1 = new android.util.LongSparseArray;
        r1.<init>();
        r7.groupStickerSets = r1;
        r1 = new java.util.HashMap;
        r1.<init>();
        r7.stickerSetsByName = r1;
        r1 = new boolean[r0];
        r7.loadingStickers = r1;
        r1 = new boolean[r0];
        r7.stickersLoaded = r1;
        r1 = new int[r0];
        r7.loadHash = r1;
        r0 = new int[r0];
        r7.loadDate = r0;
        r0 = new int[r5];
        r7.archivedStickersCount = r0;
        r0 = new android.util.LongSparseArray;
        r0.<init>();
        r7.stickersByEmoji = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r7.allStickers = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r7.allStickersFeatured = r0;
        r0 = new java.util.ArrayList[r6];
        r1 = new java.util.ArrayList;
        r1.<init>();
        r0[r3] = r1;
        r1 = new java.util.ArrayList;
        r1.<init>();
        r0[r4] = r1;
        r1 = new java.util.ArrayList;
        r1.<init>();
        r0[r5] = r1;
        r7.recentStickers = r0;
        r0 = new boolean[r6];
        r7.loadingRecentStickers = r0;
        r0 = new boolean[r6];
        r7.recentStickersLoaded = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.recentGifs = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.featuredStickerSets = r0;
        r0 = new android.util.LongSparseArray;
        r0.<init>();
        r7.featuredStickerSetsById = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.unreadStickerSets = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.readingStickerSets = r0;
        r0 = new int[r5];
        r0 = {0, 0};
        r7.messagesSearchCount = r0;
        r0 = new boolean[r5];
        r0 = {0, 0};
        r7.messagesSearchEndReached = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.searchResultMessages = r0;
        r0 = new android.util.SparseArray[r5];
        r1 = new android.util.SparseArray;
        r1.<init>();
        r0[r3] = r1;
        r1 = new android.util.SparseArray;
        r1.<init>();
        r0[r4] = r1;
        r7.searchResultMessagesMap = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.hints = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.inlineBots = r0;
        r0 = new android.util.LongSparseArray;
        r0.<init>();
        r7.drafts = r0;
        r0 = new android.util.LongSparseArray;
        r0.<init>();
        r7.draftMessages = r0;
        r0 = new android.util.SparseArray;
        r0.<init>();
        r7.botInfos = r0;
        r0 = new android.util.LongSparseArray;
        r0.<init>();
        r7.botKeyboards = r0;
        r0 = new org.telegram.messenger.support.SparseLongArray;
        r0.<init>();
        r7.botKeyboardsByMids = r0;
        r7.currentAccount = r8;
        r8 = r7.currentAccount;
        if (r8 != 0) goto L_0x011a;
    L_0x010f:
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = "drafts";
        r8 = r8.getSharedPreferences(r0, r3);
        r7.preferences = r8;
        goto L_0x0135;
    L_0x011a:
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "drafts";
        r0.append(r1);
        r1 = r7.currentAccount;
        r0.append(r1);
        r0 = r0.toString();
        r8 = r8.getSharedPreferences(r0, r3);
        r7.preferences = r8;
    L_0x0135:
        r8 = r7.preferences;
        r8 = r8.getAll();
        r8 = r8.entrySet();
        r8 = r8.iterator();
    L_0x0143:
        r0 = r8.hasNext();
        if (r0 == 0) goto L_0x019f;
    L_0x0149:
        r0 = r8.next();
        r0 = (java.util.Map.Entry) r0;
        r1 = r0.getKey();	 Catch:{ Exception -> 0x0143 }
        r1 = (java.lang.String) r1;	 Catch:{ Exception -> 0x0143 }
        r2 = org.telegram.messenger.Utilities.parseLong(r1);	 Catch:{ Exception -> 0x0143 }
        r2 = r2.longValue();	 Catch:{ Exception -> 0x0143 }
        r0 = r0.getValue();	 Catch:{ Exception -> 0x0143 }
        r0 = (java.lang.String) r0;	 Catch:{ Exception -> 0x0143 }
        r0 = org.telegram.messenger.Utilities.hexToBytes(r0);	 Catch:{ Exception -> 0x0143 }
        r5 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0143 }
        r5.<init>(r0);	 Catch:{ Exception -> 0x0143 }
        r0 = "r_";	 Catch:{ Exception -> 0x0143 }
        r0 = r1.startsWith(r0);	 Catch:{ Exception -> 0x0143 }
        if (r0 == 0) goto L_0x018f;	 Catch:{ Exception -> 0x0143 }
    L_0x0174:
        r0 = r5.readInt32(r4);	 Catch:{ Exception -> 0x0143 }
        r0 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r5, r0, r4);	 Catch:{ Exception -> 0x0143 }
        r1 = r7.currentAccount;	 Catch:{ Exception -> 0x0143 }
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);	 Catch:{ Exception -> 0x0143 }
        r1 = r1.clientUserId;	 Catch:{ Exception -> 0x0143 }
        r0.readAttachPath(r5, r1);	 Catch:{ Exception -> 0x0143 }
        if (r0 == 0) goto L_0x0143;	 Catch:{ Exception -> 0x0143 }
    L_0x0189:
        r1 = r7.draftMessages;	 Catch:{ Exception -> 0x0143 }
        r1.put(r2, r0);	 Catch:{ Exception -> 0x0143 }
        goto L_0x0143;	 Catch:{ Exception -> 0x0143 }
    L_0x018f:
        r0 = r5.readInt32(r4);	 Catch:{ Exception -> 0x0143 }
        r0 = org.telegram.tgnet.TLRPC.DraftMessage.TLdeserialize(r5, r0, r4);	 Catch:{ Exception -> 0x0143 }
        if (r0 == 0) goto L_0x0143;	 Catch:{ Exception -> 0x0143 }
    L_0x0199:
        r1 = r7.drafts;	 Catch:{ Exception -> 0x0143 }
        r1.put(r2, r0);	 Catch:{ Exception -> 0x0143 }
        goto L_0x0143;
    L_0x019f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.<init>(int):void");
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
        i = this.recentStickers[i];
        return new ArrayList(i.subList(0, Math.min(i.size(), 20)));
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

    public void addRecentSticker(final int i, Document document, int i2, boolean z) {
        int i3 = 0;
        int i4 = i3;
        while (i3 < this.recentStickers[i].size()) {
            Document document2 = (Document) this.recentStickers[i].get(i3);
            if (document2.id == document.id) {
                this.recentStickers[i].remove(i3);
                if (!z) {
                    this.recentStickers[i].add(0, document2);
                }
                i4 = 1;
            }
            i3++;
        }
        if (i4 == 0 && !z) {
            this.recentStickers[i].add(0, document);
        }
        if (i == 2) {
            if (z) {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("RemovedFromFavorites", C0446R.string.RemovedFromFavorites), 0).show();
            } else {
                Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("AddedToFavorites", C0446R.string.AddedToFavorites), 0).show();
            }
            TLObject tL_messages_faveSticker = new TL_messages_faveSticker();
            tL_messages_faveSticker.id = new TL_inputDocument();
            tL_messages_faveSticker.id.id = document.id;
            tL_messages_faveSticker.id.access_hash = document.access_hash;
            tL_messages_faveSticker.unfave = z;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_faveSticker, new C17921());
            i4 = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
        } else {
            i4 = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
        }
        if (this.recentStickers[i].size() > i4 || z) {
            Document document3;
            if (z) {
                document3 = document;
            } else {
                document3 = (Document) this.recentStickers[i].remove(this.recentStickers[i].size() - 1);
            }
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    int i = i == 0 ? 3 : i == 1 ? 4 : 5;
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                        stringBuilder.append(document3.id);
                        stringBuilder.append("' AND type = ");
                        stringBuilder.append(i);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        if (!z) {
            z = new ArrayList();
            z.add(document);
            processLoadedRecentDocuments(i, z, false, i2);
        }
        if (i == 2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[]{Boolean.valueOf(false), Integer.valueOf(i)});
        }
    }

    public ArrayList<Document> getRecentGifs() {
        return new ArrayList(this.recentGifs);
    }

    public void removeRecentGif(final Document document) {
        this.recentGifs.remove(document);
        TLObject tL_messages_saveGif = new TL_messages_saveGif();
        tL_messages_saveGif.id = new TL_inputDocument();
        tL_messages_saveGif.id.id = document.id;
        tL_messages_saveGif.id.access_hash = document.access_hash;
        tL_messages_saveGif.unsave = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveGif, new C17933());
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                    stringBuilder.append(document.id);
                    stringBuilder.append("' AND type = 2");
                    database.executeFast(stringBuilder.toString()).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void addRecentGif(Document document, int i) {
        int i2 = 0;
        int i3 = i2;
        while (i2 < this.recentGifs.size()) {
            Document document2 = (Document) this.recentGifs.get(i2);
            if (document2.id == document.id) {
                this.recentGifs.remove(i2);
                this.recentGifs.add(0, document2);
                i3 = true;
            }
            i2++;
        }
        if (i3 == 0) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > MessagesController.getInstance(this.currentAccount).maxRecentGifsCount) {
            final Document document3 = (Document) this.recentGifs.remove(this.recentGifs.size() - 1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                        stringBuilder.append(document3.id);
                        stringBuilder.append("' AND type = 2");
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(0, arrayList, true, i);
    }

    public boolean isLoadingStickers(int i) {
        return this.loadingStickers[i];
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
                if (tL_messages_stickerSet.set != null) {
                    if (tL_messages_stickerSet.set.hash != stickerSet.hash) {
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

    private void loadGroupStickerSet(final StickerSet stickerSet, boolean z) {
        if (z) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT document FROM web_recent_v3 WHERE id = 's_");
                        stringBuilder.append(stickerSet.id);
                        stringBuilder.append("'");
                        SQLiteCursor queryFinalized = database.queryFinalized(stringBuilder.toString(), new Object[0]);
                        TL_messages_stickerSet tL_messages_stickerSet = null;
                        if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                tL_messages_stickerSet = TL_messages_stickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                            }
                        }
                        queryFinalized.dispose();
                        if (tL_messages_stickerSet == null || tL_messages_stickerSet.set == null || tL_messages_stickerSet.set.hash != stickerSet.hash) {
                            DataQuery.this.loadGroupStickerSet(stickerSet, false);
                        }
                        if (tL_messages_stickerSet != null && tL_messages_stickerSet.set != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    DataQuery.this.groupStickerSets.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(tL_messages_stickerSet.set.id));
                                }
                            });
                        }
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                }
            });
            return;
        }
        z = new TL_messages_getStickerSet();
        z.stickerset = new TL_inputStickerSetID();
        z.stickerset.id = stickerSet.id;
        z.stickerset.access_hash = stickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new C17987());
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

    public boolean isStickerPackInstalled(long j) {
        return this.installedStickerSetsById.indexOfKey(j) >= null ? 1 : 0;
    }

    public boolean isStickerPackUnread(long j) {
        return this.unreadStickerSets.contains(Long.valueOf(j));
    }

    public boolean isStickerPackInstalled(String str) {
        return this.stickerSetsByName.containsKey(str);
    }

    public String getEmojiForSticker(long j) {
        String str = (String) this.stickersByEmoji.get(j);
        return str != null ? str : TtmlNode.ANONYMOUS_REGION_ID;
    }

    private static int calcDocumentsHash(ArrayList<Document> arrayList) {
        int i = 0;
        if (arrayList == null) {
            return 0;
        }
        long j = 0;
        while (i < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size())) {
            Document document = (Document) arrayList.get(i);
            if (document != null) {
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (document.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) document.id))) % 2147483648L;
            }
            i++;
        }
        return (int) j;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(final int i, final boolean z, boolean z2, boolean z3) {
        boolean z4 = false;
        if (z) {
            if (!this.loadingRecentGifs) {
                this.loadingRecentGifs = true;
            } else {
                return;
            }
        } else if (!this.loadingRecentStickers[i]) {
            this.loadingRecentStickers[i] = true;
            if (this.recentStickersLoaded[i]) {
            }
            if (z2) {
                z2 = MessagesController.getEmojiSettings(this.currentAccount);
                if (!z3) {
                    if (z) {
                        z2 = z2.getLong("lastGifLoadTime", 0);
                    } else if (i == 0) {
                        z2 = z2.getLong("lastStickersLoadTime", 0);
                    } else if (i != 1) {
                        z2 = z2.getLong("lastStickersLoadTimeMask", 0);
                    } else {
                        z2 = z2.getLong("lastStickersLoadTimeFavs", 0);
                    }
                    if (Math.abs(System.currentTimeMillis() - z2) < 3600000) {
                        if (z) {
                            this.loadingRecentStickers[i] = null;
                        } else {
                            this.loadingRecentGifs = false;
                        }
                        return;
                    }
                }
                if (z) {
                    if (i != true) {
                        z2 = new TL_messages_getFavedStickers();
                        z2.hash = calcDocumentsHash(this.recentStickers[i]);
                    } else {
                        z2 = new TL_messages_getRecentStickers();
                        z2.hash = calcDocumentsHash(this.recentStickers[i]);
                        if (i == 1) {
                            z4 = true;
                        }
                        z2.attached = z4;
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(z2, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (i == 2) {
                                if ((tLObject instanceof TL_messages_favedStickers) != null) {
                                    tLObject = ((TL_messages_favedStickers) tLObject).stickers;
                                    DataQuery.this.processLoadedRecentDocuments(i, tLObject, z, 0);
                                }
                            } else if ((tLObject instanceof TL_messages_recentStickers) != null) {
                                tLObject = ((TL_messages_recentStickers) tLObject).stickers;
                                DataQuery.this.processLoadedRecentDocuments(i, tLObject, z, 0);
                            }
                            tLObject = null;
                            DataQuery.this.processLoadedRecentDocuments(i, tLObject, z, 0);
                        }
                    });
                } else {
                    z2 = new TL_messages_getSavedGifs();
                    z2.hash = calcDocumentsHash(this.recentGifs);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(z2, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            DataQuery.this.processLoadedRecentDocuments(i, (tLObject instanceof TL_messages_savedGifs) != null ? ((TL_messages_savedGifs) tLObject).gifs : null, z, 0);
                        }
                    });
                }
            } else {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        try {
                            int i = z ? 2 : i == 0 ? 3 : i == 1 ? 4 : 5;
                            SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT document FROM web_recent_v3 WHERE type = ");
                            stringBuilder.append(i);
                            stringBuilder.append(" ORDER BY date DESC");
                            SQLiteCursor queryFinalized = database.queryFinalized(stringBuilder.toString(), new Object[0]);
                            final ArrayList arrayList = new ArrayList();
                            while (queryFinalized.next()) {
                                if (!queryFinalized.isNull(0)) {
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
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
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (z) {
                                        DataQuery.this.recentGifs = arrayList;
                                        DataQuery.this.loadingRecentGifs = false;
                                        DataQuery.this.recentGifsLoaded = true;
                                    } else {
                                        DataQuery.this.recentStickers[i] = arrayList;
                                        DataQuery.this.loadingRecentStickers[i] = false;
                                        DataQuery.this.recentStickersLoaded[i] = true;
                                    }
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(z), Integer.valueOf(i));
                                    DataQuery.this.loadRecents(i, z, false, false);
                                }
                            });
                        } catch (Throwable th) {
                            FileLog.m3e(th);
                        }
                    }
                });
            }
        } else {
            return;
        }
        z2 = false;
        if (z2) {
            z2 = MessagesController.getEmojiSettings(this.currentAccount);
            if (z3) {
                if (z) {
                    z2 = z2.getLong("lastGifLoadTime", 0);
                } else if (i == 0) {
                    z2 = z2.getLong("lastStickersLoadTime", 0);
                } else if (i != 1) {
                    z2 = z2.getLong("lastStickersLoadTimeFavs", 0);
                } else {
                    z2 = z2.getLong("lastStickersLoadTimeMask", 0);
                }
                if (Math.abs(System.currentTimeMillis() - z2) < 3600000) {
                    if (z) {
                        this.loadingRecentStickers[i] = null;
                    } else {
                        this.loadingRecentGifs = false;
                    }
                    return;
                }
            }
            if (z) {
                if (i != true) {
                    z2 = new TL_messages_getRecentStickers();
                    z2.hash = calcDocumentsHash(this.recentStickers[i]);
                    if (i == 1) {
                        z4 = true;
                    }
                    z2.attached = z4;
                } else {
                    z2 = new TL_messages_getFavedStickers();
                    z2.hash = calcDocumentsHash(this.recentStickers[i]);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z2, /* anonymous class already generated */);
            } else {
                z2 = new TL_messages_getSavedGifs();
                z2.hash = calcDocumentsHash(this.recentGifs);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z2, /* anonymous class already generated */);
            }
        } else {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
        }
    }

    private void processLoadedRecentDocuments(final int i, final ArrayList<Document> arrayList, final boolean z, int i2) {
        if (arrayList != null) {
            final boolean z2 = z;
            final int i3 = i;
            final ArrayList<Document> arrayList2 = arrayList;
            final int i4 = i2;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        int i;
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        int i2 = 2;
                        if (z2) {
                            i = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentGifsCount;
                        } else if (i3 == 2) {
                            i = MessagesController.getInstance(DataQuery.this.currentAccount).maxFaveStickersCount;
                        } else {
                            i = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentStickersCount;
                        }
                        database.beginTransaction();
                        SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        int size = arrayList2.size();
                        int i3 = z2 ? 2 : i3 == 0 ? 3 : i3 == 1 ? 4 : 5;
                        int i4 = 0;
                        while (i4 < size) {
                            if (i4 == i) {
                                break;
                            }
                            Document document = (Document) arrayList2.get(i4);
                            executeFast.requery();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(document.id);
                            executeFast.bindString(1, stringBuilder.toString());
                            executeFast.bindInteger(i2, i3);
                            executeFast.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                            executeFast.bindInteger(6, 0);
                            executeFast.bindInteger(7, 0);
                            executeFast.bindInteger(8, 0);
                            executeFast.bindInteger(9, i4 != 0 ? i4 : size - i4);
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                            document.serializeToStream(nativeByteBuffer);
                            executeFast.bindByteBuffer(10, nativeByteBuffer);
                            executeFast.step();
                            if (nativeByteBuffer != null) {
                                nativeByteBuffer.reuse();
                            }
                            i4++;
                            i2 = 2;
                        }
                        executeFast.dispose();
                        database.commitTransaction();
                        if (arrayList2.size() >= i) {
                            database.beginTransaction();
                            while (i < arrayList2.size()) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                                stringBuilder2.append(((Document) arrayList2.get(i)).id);
                                stringBuilder2.append("' AND type = ");
                                stringBuilder2.append(i3);
                                database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                                i++;
                            }
                            database.commitTransaction();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Editor edit = MessagesController.getEmojiSettings(DataQuery.this.currentAccount).edit();
                    if (z) {
                        DataQuery.this.loadingRecentGifs = false;
                        DataQuery.this.recentGifsLoaded = true;
                        edit.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
                    } else {
                        DataQuery.this.loadingRecentStickers[i] = false;
                        DataQuery.this.recentStickersLoaded[i] = true;
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
                            DataQuery.this.recentGifs = arrayList;
                        } else {
                            DataQuery.this.recentStickers[i] = arrayList;
                        }
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(z), Integer.valueOf(i));
                    }
                }
            });
        }
    }

    public void reorderStickers(int i, final ArrayList<Long> arrayList) {
        Collections.sort(this.stickerSets[i], new Comparator<TL_messages_stickerSet>() {
            public int compare(TL_messages_stickerSet tL_messages_stickerSet, TL_messages_stickerSet tL_messages_stickerSet2) {
                tL_messages_stickerSet = arrayList.indexOf(Long.valueOf(tL_messages_stickerSet.set.id));
                tL_messages_stickerSet2 = arrayList.indexOf(Long.valueOf(tL_messages_stickerSet2.set.id));
                if (tL_messages_stickerSet > tL_messages_stickerSet2) {
                    return 1;
                }
                return tL_messages_stickerSet < tL_messages_stickerSet2 ? -1 : null;
            }
        });
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    public void calcNewHash(int i) {
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
    }

    public void addNewStickerSet(TL_messages_stickerSet tL_messages_stickerSet) {
        if (this.stickerSetsById.indexOfKey(tL_messages_stickerSet.set.id) < 0) {
            if (!this.stickerSetsByName.containsKey(tL_messages_stickerSet.set.short_name)) {
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
                    tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(z));
                loadStickers(z, false, true);
            }
        }
    }

    public void loadFeaturedStickers(boolean z, boolean z2) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        int readInt32;
                        int i;
                        ArrayList arrayList;
                        boolean z;
                        Throwable th;
                        ArrayList arrayList2;
                        Throwable th2;
                        ArrayList arrayList3 = new ArrayList();
                        SQLiteCursor sQLiteCursor = null;
                        boolean z2 = false;
                        ArrayList arrayList4;
                        try {
                            SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT data, unread, date, hash FROM stickers_featured WHERE 1", new Object[0]);
                            ArrayList arrayList5;
                            boolean intValue;
                            try {
                                if (queryFinalized.next()) {
                                    int i2;
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        arrayList4 = new ArrayList();
                                        int readInt322 = byteBufferValue.readInt32(false);
                                        for (i2 = 0; i2 < readInt322; i2++) {
                                            arrayList4.add(StickerSetCovered.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                        }
                                        byteBufferValue.reuse();
                                        arrayList5 = arrayList4;
                                    }
                                    NativeByteBuffer byteBufferValue2 = queryFinalized.byteBufferValue(1);
                                    if (byteBufferValue2 != null) {
                                        readInt32 = byteBufferValue2.readInt32(false);
                                        for (i2 = 0; i2 < readInt32; i2++) {
                                            arrayList3.add(Long.valueOf(byteBufferValue2.readInt64(false)));
                                        }
                                        byteBufferValue2.reuse();
                                    }
                                    intValue = queryFinalized.intValue(2);
                                    readInt32 = DataQuery.this.calcFeaturedStickersHash(arrayList5);
                                    z2 = intValue;
                                } else {
                                    readInt32 = 0;
                                }
                                if (queryFinalized != null) {
                                    queryFinalized.dispose();
                                }
                                i = z2;
                                arrayList = arrayList5;
                            } catch (Throwable th3) {
                                th2 = th3;
                                if (queryFinalized != null) {
                                    queryFinalized.dispose();
                                }
                                throw th2;
                            }
                        } catch (Throwable th4) {
                            arrayList4 = null;
                            th = th4;
                            z = false;
                            FileLog.m3e(th);
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            i = z;
                            arrayList2 = arrayList4;
                            readInt32 = 0;
                            arrayList = arrayList2;
                            DataQuery.this.processLoadedFeaturedStickers(arrayList, arrayList3, true, i, readInt32);
                        }
                        DataQuery.this.processLoadedFeaturedStickers(arrayList, arrayList3, true, i, readInt32);
                    }
                });
            } else {
                z = new TL_messages_getFeaturedStickers();
                if (z2) {
                    z2 = false;
                } else {
                    z2 = this.loadFeaturedHash;
                }
                z.hash = z2;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tLObject instanceof TL_messages_featuredStickers) {
                                    TL_messages_featuredStickers tL_messages_featuredStickers = (TL_messages_featuredStickers) tLObject;
                                    DataQuery.this.processLoadedFeaturedStickers(tL_messages_featuredStickers.sets, tL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tL_messages_featuredStickers.hash);
                                    return;
                                }
                                DataQuery.this.processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), z.hash);
                            }
                        });
                    }
                });
            }
        }
    }

    private void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                DataQuery.this.loadingFeaturedStickers = false;
                DataQuery.this.featuredStickersLoaded = true;
            }
        });
        final boolean z2 = z;
        final ArrayList<StickerSetCovered> arrayList3 = arrayList;
        final int i3 = i;
        final int i4 = i2;
        final ArrayList<Long> arrayList4 = arrayList2;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.DataQuery$17$1 */
            class C01091 implements Runnable {
                C01091() {
                }

                public void run() {
                    if (!(arrayList3 == null || i4 == 0)) {
                        DataQuery.this.loadFeaturedHash = i4;
                    }
                    DataQuery.this.loadFeaturedStickers(false, false);
                }
            }

            /* renamed from: org.telegram.messenger.DataQuery$17$3 */
            class C01113 implements Runnable {
                C01113() {
                }

                public void run() {
                    DataQuery.this.loadFeaturedDate = i3;
                }
            }

            public void run() {
                long j = 1000;
                if ((z2 && (arrayList3 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i3)) >= 3600)) || (!z2 && arrayList3 == null && i4 == 0)) {
                    Runnable c01091 = new C01091();
                    if (arrayList3 != null || z2) {
                        j = 0;
                    }
                    AndroidUtilities.runOnUIThread(c01091, j);
                    if (arrayList3 == null) {
                        return;
                    }
                }
                int i = 0;
                if (arrayList3 != null) {
                    try {
                        final ArrayList arrayList = new ArrayList();
                        final LongSparseArray longSparseArray = new LongSparseArray();
                        while (i < arrayList3.size()) {
                            StickerSetCovered stickerSetCovered = (StickerSetCovered) arrayList3.get(i);
                            arrayList.add(stickerSetCovered);
                            longSparseArray.put(stickerSetCovered.set.id, stickerSetCovered);
                            i++;
                        }
                        if (!z2) {
                            DataQuery.this.putFeaturedStickersToCache(arrayList, arrayList4, i3, i4);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                DataQuery.this.unreadStickerSets = arrayList4;
                                DataQuery.this.featuredStickerSetsById = longSparseArray;
                                DataQuery.this.featuredStickerSets = arrayList;
                                DataQuery.this.loadFeaturedHash = i4;
                                DataQuery.this.loadFeaturedDate = i3;
                                DataQuery.this.loadStickers(3, true, false);
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                            }
                        });
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                } else if (!z2) {
                    AndroidUtilities.runOnUIThread(new C01113());
                    DataQuery.this.putFeaturedStickersToCache(null, null, i3, 0);
                }
            }
        });
    }

    private void putFeaturedStickersToCache(ArrayList<StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        final ArrayList arrayList3 = arrayList != null ? new ArrayList(arrayList) : null;
        final ArrayList<Long> arrayList4 = arrayList2;
        final int i3 = i;
        final int i4 = i2;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast;
                    if (arrayList3 != null) {
                        executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                        executeFast.requery();
                        int i = 0;
                        int i2 = 4;
                        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                            i2 += ((StickerSetCovered) arrayList3.get(i3)).getObjectSize();
                        }
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i2);
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList4.size() * 8) + 4);
                        nativeByteBuffer.writeInt32(arrayList3.size());
                        for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                            ((StickerSetCovered) arrayList3.get(i4)).serializeToStream(nativeByteBuffer);
                        }
                        nativeByteBuffer2.writeInt32(arrayList4.size());
                        while (i < arrayList4.size()) {
                            nativeByteBuffer2.writeInt64(((Long) arrayList4.get(i)).longValue());
                            i++;
                        }
                        executeFast.bindInteger(1, 1);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.bindByteBuffer(3, nativeByteBuffer2);
                        executeFast.bindInteger(4, i3);
                        executeFast.bindInteger(5, i4);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        nativeByteBuffer2.reuse();
                        executeFast.dispose();
                        return;
                    }
                    executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
                    executeFast.requery();
                    executeFast.bindInteger(1, i3);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private int calcFeaturedStickersHash(ArrayList<StickerSetCovered> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            StickerSet stickerSet = ((StickerSetCovered) arrayList.get(i)).set;
            if (!stickerSet.archived) {
                long j2 = (((((((j * 20261) + 2147483648L) + ((long) ((int) (stickerSet.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) stickerSet.id))) % 2147483648L;
                j = this.unreadStickerSets.contains(Long.valueOf(stickerSet.id)) ? (((j2 * 20261) + 2147483648L) + 1) % 2147483648L : j2;
            }
        }
        return (int) j;
    }

    public void markFaturedStickersAsRead(boolean z) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (z) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_readFeaturedStickers(), new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
            }
        }
    }

    public int getFeaturesStickersHashWithoutUnread() {
        long j = 0;
        for (int i = 0; i < this.featuredStickerSets.size(); i++) {
            StickerSet stickerSet = ((StickerSetCovered) this.featuredStickerSets.get(i)).set;
            if (!stickerSet.archived) {
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (stickerSet.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) stickerSet.id))) % 2147483648L;
            }
        }
        return (int) j;
    }

    public void markFaturedStickersByIdAsRead(final long j) {
        if (this.unreadStickerSets.contains(Long.valueOf(j))) {
            if (!this.readingStickerSets.contains(Long.valueOf(j))) {
                this.readingStickerSets.add(Long.valueOf(j));
                TLObject tL_messages_readFeaturedStickers = new TL_messages_readFeaturedStickers();
                tL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_readFeaturedStickers, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DataQuery.this.unreadStickerSets.remove(Long.valueOf(j));
                        DataQuery.this.readingStickerSets.remove(Long.valueOf(j));
                        DataQuery.this.loadFeaturedHash = DataQuery.this.calcFeaturedStickersHash(DataQuery.this.featuredStickerSets);
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                        DataQuery.this.putFeaturedStickersToCache(DataQuery.this.featuredStickerSets, DataQuery.this.unreadStickerSets, DataQuery.this.loadFeaturedDate, DataQuery.this.loadFeaturedHash);
                    }
                }, 1000);
            }
        }
    }

    public int getArchivedStickersCount(int i) {
        return this.archivedStickersCount[i];
    }

    public void loadArchivedStickersCount(final int i, boolean z) {
        boolean z2 = true;
        if (z) {
            z = MessagesController.getNotificationsSettings(this.currentAccount);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("archivedStickersCount");
            stringBuilder.append(i);
            z = z.getInt(stringBuilder.toString(), -1);
            if (z) {
                loadArchivedStickersCount(i, false);
                return;
            }
            this.archivedStickersCount[i] = z;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(i));
            return;
        }
        z = new TL_messages_getArchivedStickers();
        z.limit = 0;
        if (i != 1) {
            z2 = false;
        }
        z.masks = z2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (tL_error == null) {
                            TL_messages_archivedStickers tL_messages_archivedStickers = (TL_messages_archivedStickers) tLObject;
                            DataQuery.this.archivedStickersCount[i] = tL_messages_archivedStickers.count;
                            Editor edit = MessagesController.getNotificationsSettings(DataQuery.this.currentAccount).edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("archivedStickersCount");
                            stringBuilder.append(i);
                            edit.putInt(stringBuilder.toString(), tL_messages_archivedStickers.count).commit();
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(i));
                        }
                    }
                });
            }
        });
    }

    private void processLoadStickersResponse(int i, TL_messages_allStickers tL_messages_allStickers) {
        DataQuery dataQuery = this;
        TL_messages_allStickers tL_messages_allStickers2 = tL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (tL_messages_allStickers2.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers2.hash);
            return;
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        int i2 = 0;
        while (i2 < tL_messages_allStickers2.sets.size()) {
            final StickerSet stickerSet = (StickerSet) tL_messages_allStickers2.sets.get(i2);
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) dataQuery.stickerSetsById.get(stickerSet.id);
            if (tL_messages_stickerSet == null || tL_messages_stickerSet.set.hash != stickerSet.hash) {
                arrayList.add(null);
                TLObject tL_messages_getStickerSet = new TL_messages_getStickerSet();
                tL_messages_getStickerSet.stickerset = new TL_inputStickerSetID();
                tL_messages_getStickerSet.stickerset.id = stickerSet.id;
                tL_messages_getStickerSet.stickerset.access_hash = stickerSet.access_hash;
                final ArrayList arrayList2 = arrayList;
                final int i3 = i2;
                final LongSparseArray longSparseArray2 = longSparseArray;
                AnonymousClass23 anonymousClass23 = r0;
                final TL_messages_allStickers tL_messages_allStickers3 = tL_messages_allStickers2;
                ConnectionsManager instance = ConnectionsManager.getInstance(dataQuery.currentAccount);
                final int i4 = i;
                AnonymousClass23 anonymousClass232 = new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) tLObject;
                                arrayList2.set(i3, tL_messages_stickerSet);
                                longSparseArray2.put(stickerSet.id, tL_messages_stickerSet);
                                if (longSparseArray2.size() == tL_messages_allStickers3.sets.size()) {
                                    for (int i = 0; i < arrayList2.size(); i++) {
                                        if (arrayList2.get(i) == null) {
                                            arrayList2.remove(i);
                                        }
                                    }
                                    DataQuery.this.processLoadedStickers(i4, arrayList2, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers3.hash);
                                }
                            }
                        });
                    }
                };
                instance.sendRequest(tL_messages_getStickerSet, anonymousClass23);
            } else {
                tL_messages_stickerSet.set.archived = stickerSet.archived;
                tL_messages_stickerSet.set.installed = stickerSet.installed;
                tL_messages_stickerSet.set.official = stickerSet.official;
                longSparseArray.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
                arrayList.add(tL_messages_stickerSet);
                if (longSparseArray.size() == tL_messages_allStickers2.sets.size()) {
                    processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers2.hash);
                }
            }
            i2++;
            j = 1000;
        }
    }

    public void loadStickers(final int i, boolean z, boolean z2) {
        if (!this.loadingStickers[i]) {
            if (i != 3) {
                loadArchivedStickersCount(i, z);
            } else if (this.featuredStickerSets.isEmpty() || !MessagesController.getInstance(this.currentAccount).preloadFeaturedStickers) {
                return;
            }
            this.loadingStickers[i] = true;
            if (z) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        ArrayList arrayList;
                        int i;
                        int i2;
                        Throwable th;
                        Throwable th2;
                        SQLiteCursor sQLiteCursor = null;
                        boolean z = false;
                        ArrayList arrayList2;
                        int intValue;
                        try {
                            SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT data, date, hash FROM stickers_v2 WHERE id = ");
                            stringBuilder.append(i + 1);
                            SQLiteCursor queryFinalized = database.queryFinalized(stringBuilder.toString(), new Object[0]);
                            ArrayList arrayList3;
                            try {
                                int access$2300;
                                if (queryFinalized.next()) {
                                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        arrayList2 = new ArrayList();
                                        int readInt32 = byteBufferValue.readInt32(false);
                                        for (int i3 = 0; i3 < readInt32; i3++) {
                                            arrayList2.add(TL_messages_stickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false));
                                        }
                                        byteBufferValue.reuse();
                                        arrayList3 = arrayList2;
                                    }
                                    intValue = queryFinalized.intValue(1);
                                    access$2300 = DataQuery.calcStickersHash(arrayList3);
                                    z = intValue;
                                } else {
                                    access$2300 = 0;
                                }
                                if (queryFinalized != null) {
                                    queryFinalized.dispose();
                                }
                                arrayList = arrayList3;
                                i = z;
                                i2 = access$2300;
                            } catch (Throwable th3) {
                                th2 = th3;
                                sQLiteCursor = queryFinalized;
                                if (sQLiteCursor != null) {
                                    sQLiteCursor.dispose();
                                }
                                throw th2;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            arrayList2 = null;
                            intValue = 0;
                            FileLog.m3e(th);
                            if (sQLiteCursor != null) {
                                sQLiteCursor.dispose();
                            }
                            i2 = 0;
                            i = intValue;
                            arrayList = arrayList2;
                            DataQuery.this.processLoadedStickers(i, arrayList, true, i, i2);
                        }
                        DataQuery.this.processLoadedStickers(i, arrayList, true, i, i2);
                    }
                });
            } else {
                z = false;
                if (i == 3) {
                    z2 = new TL_messages_allStickers();
                    z2.hash = this.loadFeaturedHash;
                    boolean size = this.featuredStickerSets.size();
                    while (z < size) {
                        z2.sets.add(((StickerSetCovered) this.featuredStickerSets.get(z)).set);
                        z++;
                    }
                    processLoadStickersResponse(i, z2);
                    return;
                }
                TLObject tL_messages_getAllStickers;
                if (i == 0) {
                    tL_messages_getAllStickers = new TL_messages_getAllStickers();
                    TL_messages_getAllStickers tL_messages_getAllStickers2 = (TL_messages_getAllStickers) tL_messages_getAllStickers;
                    if (!z2) {
                        z = this.loadHash[i];
                    }
                    tL_messages_getAllStickers2.hash = z;
                } else {
                    tL_messages_getAllStickers = new TL_messages_getMaskStickers();
                    TL_messages_getMaskStickers tL_messages_getMaskStickers = (TL_messages_getMaskStickers) tL_messages_getAllStickers;
                    if (!z2) {
                        z = this.loadHash[i];
                    }
                    tL_messages_getMaskStickers.hash = z;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getAllStickers, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tLObject instanceof TL_messages_allStickers) {
                                    DataQuery.this.processLoadStickersResponse(i, (TL_messages_allStickers) tLObject);
                                } else {
                                    DataQuery.this.processLoadedStickers(i, null, false, (int) (System.currentTimeMillis() / 1000), z);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void putStickersToCache(int i, ArrayList<TL_messages_stickerSet> arrayList, int i2, int i3) {
        final ArrayList arrayList2 = arrayList != null ? new ArrayList(arrayList) : null;
        final int i4 = i;
        final int i5 = i2;
        final int i6 = i3;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast;
                    if (arrayList2 != null) {
                        executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                        executeFast.requery();
                        int i = 0;
                        int i2 = 4;
                        for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                            i2 += ((TL_messages_stickerSet) arrayList2.get(i3)).getObjectSize();
                        }
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i2);
                        nativeByteBuffer.writeInt32(arrayList2.size());
                        while (i < arrayList2.size()) {
                            ((TL_messages_stickerSet) arrayList2.get(i)).serializeToStream(nativeByteBuffer);
                            i++;
                        }
                        executeFast.bindInteger(1, i4 + 1);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.bindInteger(3, i5);
                        executeFast.bindInteger(4, i6);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        executeFast.dispose();
                        return;
                    }
                    executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                    executeFast.requery();
                    executeFast.bindInteger(1, i5);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public String getStickerSetName(long j) {
        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(j);
        if (tL_messages_stickerSet != null) {
            return tL_messages_stickerSet.set.short_name;
        }
        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.featuredStickerSetsById.get(j);
        return stickerSetCovered != null ? stickerSetCovered.set.short_name : 0;
    }

    public static long getStickerSetId(Document document) {
        for (int i = 0; i < document.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                if ((documentAttribute.stickerset instanceof TL_inputStickerSetID) != null) {
                    return documentAttribute.stickerset.id;
                }
                return -1;
            }
        }
        return -1;
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

    private void processLoadedStickers(final int i, ArrayList<TL_messages_stickerSet> arrayList, boolean z, int i2, int i3) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                DataQuery.this.loadingStickers[i] = false;
                DataQuery.this.stickersLoaded[i] = true;
            }
        });
        final boolean z2 = z;
        final ArrayList<TL_messages_stickerSet> arrayList2 = arrayList;
        final int i4 = i2;
        final int i5 = i3;
        final int i6 = i;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.DataQuery$28$1 */
            class C01151 implements Runnable {
                C01151() {
                }

                public void run() {
                    if (!(arrayList2 == null || i5 == 0)) {
                        DataQuery.this.loadHash[i6] = i5;
                    }
                    DataQuery.this.loadStickers(i6, false, false);
                }
            }

            /* renamed from: org.telegram.messenger.DataQuery$28$3 */
            class C01173 implements Runnable {
                C01173() {
                }

                public void run() {
                    DataQuery.this.loadDate[i6] = i4;
                }
            }

            public void run() {
                long j = 1000;
                if ((z2 && (arrayList2 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i4)) >= 3600)) || (!z2 && arrayList2 == null && i5 == 0)) {
                    Runnable c01151 = new C01151();
                    if (arrayList2 != null || z2) {
                        j = 0;
                    }
                    AndroidUtilities.runOnUIThread(c01151, j);
                    if (arrayList2 == null) {
                        return;
                    }
                }
                if (arrayList2 != null) {
                    try {
                        LongSparseArray longSparseArray;
                        final ArrayList arrayList = new ArrayList();
                        LongSparseArray longSparseArray2 = new LongSparseArray();
                        final HashMap hashMap = new HashMap();
                        final LongSparseArray longSparseArray3 = new LongSparseArray();
                        LongSparseArray longSparseArray4 = new LongSparseArray();
                        final HashMap hashMap2 = new HashMap();
                        int i = 0;
                        while (i < arrayList2.size()) {
                            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) arrayList2.get(i);
                            if (tL_messages_stickerSet != null) {
                                int i2;
                                arrayList.add(tL_messages_stickerSet);
                                longSparseArray2.put(tL_messages_stickerSet.set.id, tL_messages_stickerSet);
                                hashMap.put(tL_messages_stickerSet.set.short_name, tL_messages_stickerSet);
                                for (i2 = 0; i2 < tL_messages_stickerSet.documents.size(); i2++) {
                                    Document document = (Document) tL_messages_stickerSet.documents.get(i2);
                                    if (document != null) {
                                        if (!(document instanceof TL_documentEmpty)) {
                                            longSparseArray4.put(document.id, document);
                                        }
                                    }
                                }
                                if (!tL_messages_stickerSet.set.archived) {
                                    i2 = 0;
                                    while (i2 < tL_messages_stickerSet.packs.size()) {
                                        TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i2);
                                        if (tL_stickerPack != null) {
                                            if (tL_stickerPack.emoticon != null) {
                                                tL_stickerPack.emoticon = tL_stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                                ArrayList arrayList2 = (ArrayList) hashMap2.get(tL_stickerPack.emoticon);
                                                if (arrayList2 == null) {
                                                    arrayList2 = new ArrayList();
                                                    hashMap2.put(tL_stickerPack.emoticon, arrayList2);
                                                }
                                                int i3 = 0;
                                                while (i3 < tL_stickerPack.documents.size()) {
                                                    TL_messages_stickerSet tL_messages_stickerSet2;
                                                    Long l = (Long) tL_stickerPack.documents.get(i3);
                                                    longSparseArray = longSparseArray2;
                                                    if (longSparseArray3.indexOfKey(l.longValue()) < 0) {
                                                        tL_messages_stickerSet2 = tL_messages_stickerSet;
                                                        longSparseArray3.put(l.longValue(), tL_stickerPack.emoticon);
                                                    } else {
                                                        tL_messages_stickerSet2 = tL_messages_stickerSet;
                                                    }
                                                    Document document2 = (Document) longSparseArray4.get(l.longValue());
                                                    if (document2 != null) {
                                                        arrayList2.add(document2);
                                                    }
                                                    i3++;
                                                    longSparseArray2 = longSparseArray;
                                                    tL_messages_stickerSet = tL_messages_stickerSet2;
                                                }
                                            }
                                        }
                                        i2++;
                                        longSparseArray2 = longSparseArray2;
                                        tL_messages_stickerSet = tL_messages_stickerSet;
                                    }
                                }
                            }
                            i++;
                            longSparseArray2 = longSparseArray2;
                        }
                        longSparseArray = longSparseArray2;
                        if (!z2) {
                            DataQuery.this.putStickersToCache(i6, arrayList, i4, i5);
                        }
                        longSparseArray2 = longSparseArray;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                int i;
                                for (i = 0; i < DataQuery.this.stickerSets[i6].size(); i++) {
                                    StickerSet stickerSet = ((TL_messages_stickerSet) DataQuery.this.stickerSets[i6].get(i)).set;
                                    DataQuery.this.stickerSetsById.remove(stickerSet.id);
                                    DataQuery.this.installedStickerSetsById.remove(stickerSet.id);
                                    DataQuery.this.stickerSetsByName.remove(stickerSet.short_name);
                                }
                                for (i = 0; i < longSparseArray2.size(); i++) {
                                    DataQuery.this.stickerSetsById.put(longSparseArray2.keyAt(i), longSparseArray2.valueAt(i));
                                    if (i6 != 3) {
                                        DataQuery.this.installedStickerSetsById.put(longSparseArray2.keyAt(i), longSparseArray2.valueAt(i));
                                    }
                                }
                                DataQuery.this.stickerSetsByName.putAll(hashMap);
                                DataQuery.this.stickerSets[i6] = arrayList;
                                DataQuery.this.loadHash[i6] = i5;
                                DataQuery.this.loadDate[i6] = i4;
                                if (i6 == 0) {
                                    DataQuery.this.allStickers = hashMap2;
                                    DataQuery.this.stickersByEmoji = longSparseArray3;
                                } else if (i6 == 3) {
                                    DataQuery.this.allStickersFeatured = hashMap2;
                                }
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(i6));
                            }
                        });
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                } else if (!z2) {
                    AndroidUtilities.runOnUIThread(new C01173());
                    DataQuery.this.putStickersToCache(i6, null, i4, 0);
                }
            }
        });
    }

    public void removeStickersSet(final Context context, final StickerSet stickerSet, int i, BaseFragment baseFragment, boolean z) {
        final boolean z2 = stickerSet.masks;
        InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
        tL_inputStickerSetID.access_hash = stickerSet.access_hash;
        tL_inputStickerSetID.id = stickerSet.id;
        if (i != 0) {
            int i2;
            final BaseFragment baseFragment2;
            final boolean z3;
            context = null;
            stickerSet.archived = i == 1;
            for (i2 = 0; i2 < this.stickerSets[z2].size(); i2++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSets[z2].get(i2);
                if (tL_messages_stickerSet.set.id == stickerSet.id) {
                    this.stickerSets[z2].remove(i2);
                    if (i == 2) {
                        this.stickerSets[z2].add(0, tL_messages_stickerSet);
                    } else {
                        this.stickerSetsById.remove(tL_messages_stickerSet.set.id);
                        this.installedStickerSetsById.remove(tL_messages_stickerSet.set.id);
                        this.stickerSetsByName.remove(tL_messages_stickerSet.set.short_name);
                    }
                    this.loadHash[z2] = calcStickersHash(this.stickerSets[z2]);
                    putStickersToCache(z2, this.stickerSets[z2], this.loadDate[z2], this.loadHash[z2]);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(z2));
                    stickerSet = new TL_messages_installStickerSet();
                    stickerSet.stickerset = tL_inputStickerSetID;
                    if (i == 1) {
                        context = 1;
                    }
                    stickerSet.archived = context;
                    i2 = i;
                    baseFragment2 = baseFragment;
                    z3 = z;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(stickerSet, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.DataQuery$29$2 */
                        class C01192 implements Runnable {
                            C01192() {
                            }

                            public void run() {
                                DataQuery.this.loadStickers(z2, false, false);
                            }
                        }

                        public void run(final TLObject tLObject, TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (tLObject instanceof TL_messages_stickerSetInstallResultArchive) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(z2));
                                        if (i2 != 1 && baseFragment2 != null && baseFragment2.getParentActivity() != null) {
                                            baseFragment2.showDialog(new StickersArchiveAlert(baseFragment2.getParentActivity(), z3 ? baseFragment2 : null, ((TL_messages_stickerSetInstallResultArchive) tLObject).sets).create());
                                        }
                                    }
                                }
                            });
                            AndroidUtilities.runOnUIThread(new C01192(), 1000);
                        }
                    });
                    return;
                }
            }
            this.loadHash[z2] = calcStickersHash(this.stickerSets[z2]);
            putStickersToCache(z2, this.stickerSets[z2], this.loadDate[z2], this.loadHash[z2]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(z2));
            stickerSet = new TL_messages_installStickerSet();
            stickerSet.stickerset = tL_inputStickerSetID;
            if (i == 1) {
                context = 1;
            }
            stickerSet.archived = context;
            i2 = i;
            baseFragment2 = baseFragment;
            z3 = z;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(stickerSet, /* anonymous class already generated */);
            return;
        }
        i = new TL_messages_uninstallStickerSet();
        i.stickerset = tL_inputStickerSetID;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(i, new RequestDelegate() {
            public void run(TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            if (tL_error != null) {
                                Toast.makeText(context, LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred), 0).show();
                            } else if (stickerSet.masks) {
                                Toast.makeText(context, LocaleController.getString("MasksRemoved", C0446R.string.MasksRemoved), 0).show();
                            } else {
                                Toast.makeText(context, LocaleController.getString("StickersRemoved", C0446R.string.StickersRemoved), 0).show();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        DataQuery.this.loadStickers(z2, false, true);
                    }
                });
            }
        });
    }

    private int getMask() {
        int i = 1;
        if (this.lastReturnedNum >= this.searchResultMessages.size() - 1 && this.messagesSearchEndReached[0]) {
            if (this.messagesSearchEndReached[1]) {
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
        int id;
        long j4;
        int i4;
        final long j5 = j2;
        final int i5 = i2;
        final User user2 = user;
        int i6 = z ^ 1;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(r12.currentAccount).cancelRequest(r12.reqId, true);
            r12.reqId = 0;
        }
        if (r12.mergeReqId != 0) {
            ConnectionsManager.getInstance(r12.currentAccount).cancelRequest(r12.mergeReqId, true);
            r12.mergeReqId = 0;
        }
        if (str != null) {
            if (i6 != 0) {
                NotificationCenter.getInstance(r12.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
                boolean[] zArr = r12.messagesSearchEndReached;
                r12.messagesSearchEndReached[1] = false;
                zArr[0] = false;
                int[] iArr = r12.messagesSearchCount;
                r12.messagesSearchCount[1] = 0;
                iArr[0] = 0;
                r12.searchResultMessages.clear();
                r12.searchResultMessagesMap[0].clear();
                r12.searchResultMessagesMap[1].clear();
            }
            str2 = str;
            j3 = j;
            i3 = false;
        } else if (!r12.searchResultMessages.isEmpty()) {
            MessageObject messageObject;
            if (i5 == 1) {
                r12.lastReturnedNum++;
                if (r12.lastReturnedNum < r12.searchResultMessages.size()) {
                    messageObject = (MessageObject) r12.searchResultMessages.get(r12.lastReturnedNum);
                    NotificationCenter.getInstance(r12.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(r12.lastReturnedNum), Integer.valueOf(r12.messagesSearchCount[0] + r12.messagesSearchCount[1]));
                    return;
                } else if (r12.messagesSearchEndReached[0] && j5 == 0 && r12.messagesSearchEndReached[1]) {
                    r12.lastReturnedNum--;
                    return;
                } else {
                    String str3 = r12.lastSearchQuery;
                    MessageObject messageObject2 = (MessageObject) r12.searchResultMessages.get(r12.searchResultMessages.size() - 1);
                    if (messageObject2.getDialogId() != j || r12.messagesSearchEndReached[0]) {
                        id = messageObject2.getDialogId() == j5 ? messageObject2.getId() : 0;
                        r12.messagesSearchEndReached[1] = false;
                        j3 = j5;
                    } else {
                        id = messageObject2.getId();
                        j3 = j;
                    }
                    i3 = id;
                    str2 = str3;
                    i6 = 0;
                }
            } else if (i5 == 2) {
                r12.lastReturnedNum--;
                if (r12.lastReturnedNum < 0) {
                    r12.lastReturnedNum = 0;
                    return;
                }
                if (r12.lastReturnedNum >= r12.searchResultMessages.size()) {
                    r12.lastReturnedNum = r12.searchResultMessages.size() - 1;
                }
                messageObject = (MessageObject) r12.searchResultMessages.get(r12.lastReturnedNum);
                NotificationCenter.getInstance(r12.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(r12.lastReturnedNum), Integer.valueOf(r12.messagesSearchCount[0] + r12.messagesSearchCount[1]));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        if (!r12.messagesSearchEndReached[0] || r12.messagesSearchEndReached[1]) {
            j4 = 0;
        } else {
            j4 = 0;
            if (j5 != 0) {
                j3 = j5;
            }
        }
        if (j3 != j || r1 == 0) {
            i4 = i3;
        } else if (j5 != j4) {
            InputPeer inputPeer = MessagesController.getInstance(r12.currentAccount).getInputPeer((int) j5);
            if (inputPeer != null) {
                TLObject tL_messages_search = new TL_messages_search();
                tL_messages_search.peer = inputPeer;
                r12.lastMergeDialogId = j5;
                tL_messages_search.limit = 1;
                if (str2 == null) {
                    str2 = TtmlNode.ANONYMOUS_REGION_ID;
                }
                tL_messages_search.f49q = str2;
                if (user2 != null) {
                    tL_messages_search.from_id = MessagesController.getInstance(r12.currentAccount).getInputUser(user2);
                    tL_messages_search.flags = 1 | tL_messages_search.flags;
                }
                tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
                ConnectionsManager instance = ConnectionsManager.getInstance(r12.currentAccount);
                final long j6 = j5;
                final TLObject tLObject = tL_messages_search;
                AnonymousClass31 anonymousClass31 = r0;
                final long j7 = j;
                final int i7 = i;
                final User user3 = user2;
                AnonymousClass31 anonymousClass312 = new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (DataQuery.this.lastMergeDialogId == j6) {
                                    DataQuery.this.mergeReqId = 0;
                                    if (tLObject != null) {
                                        messages_Messages messages_messages = (messages_Messages) tLObject;
                                        DataQuery.this.messagesSearchEndReached[1] = messages_messages.messages.isEmpty();
                                        DataQuery.this.messagesSearchCount[1] = messages_messages instanceof TL_messages_messagesSlice ? messages_messages.count : messages_messages.messages.size();
                                        DataQuery.this.searchMessagesInChat(tLObject.f49q, j7, j6, i7, i5, true, user3);
                                    }
                                }
                            }
                        });
                    }
                };
                r12.mergeReqId = instance.sendRequest(tL_messages_search, anonymousClass31, 2);
                return;
            }
            return;
        } else {
            i4 = i3;
            r12.lastMergeDialogId = 0;
            r12.messagesSearchEndReached[1] = true;
            r12.messagesSearchCount[1] = 0;
        }
        TLObject tL_messages_search2 = new TL_messages_search();
        tL_messages_search2.peer = MessagesController.getInstance(r12.currentAccount).getInputPeer((int) j3);
        if (tL_messages_search2.peer != null) {
            tL_messages_search2.limit = 21;
            tL_messages_search2.f49q = str2 != null ? str2 : TtmlNode.ANONYMOUS_REGION_ID;
            tL_messages_search2.offset_id = i4;
            if (user2 != null) {
                tL_messages_search2.from_id = MessagesController.getInstance(r12.currentAccount).getInputUser(user2);
                tL_messages_search2.flags |= 1;
            }
            tL_messages_search2.filter = new TL_inputMessagesFilterEmpty();
            int i8 = r12.lastReqId + 1;
            r12.lastReqId = i8;
            r12.lastSearchQuery = str2;
            id = i8;
            final TLObject tLObject2 = tL_messages_search2;
            final long j8 = j;
            i5 = i;
            r12.reqId = ConnectionsManager.getInstance(r12.currentAccount).sendRequest(tL_messages_search2, new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (id == DataQuery.this.lastReqId) {
                                DataQuery.this.reqId = 0;
                                if (tLObject != null) {
                                    int size;
                                    MessageObject messageObject;
                                    messages_Messages messages_messages = (messages_Messages) tLObject;
                                    int i = 0;
                                    while (i < messages_messages.messages.size()) {
                                        Message message = (Message) messages_messages.messages.get(i);
                                        if ((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
                                            messages_messages.messages.remove(i);
                                            i--;
                                        }
                                        i++;
                                    }
                                    MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(messages_messages.users, false);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(messages_messages.chats, false);
                                    if (tLObject2.offset_id == 0 && j3 == j8) {
                                        DataQuery.this.lastReturnedNum = 0;
                                        DataQuery.this.searchResultMessages.clear();
                                        DataQuery.this.searchResultMessagesMap[0].clear();
                                        DataQuery.this.searchResultMessagesMap[1].clear();
                                        DataQuery.this.messagesSearchCount[0] = 0;
                                    }
                                    i = 0;
                                    int i2 = i;
                                    while (i < Math.min(messages_messages.messages.size(), 20)) {
                                        MessageObject messageObject2 = new MessageObject(DataQuery.this.currentAccount, (Message) messages_messages.messages.get(i), false);
                                        DataQuery.this.searchResultMessages.add(messageObject2);
                                        DataQuery.this.searchResultMessagesMap[j3 == j8 ? 0 : 1].put(messageObject2.getId(), messageObject2);
                                        i++;
                                        boolean z = true;
                                    }
                                    DataQuery.this.messagesSearchEndReached[j3 == j8 ? 0 : 1] = messages_messages.messages.size() != 21;
                                    int[] access$4000 = DataQuery.this.messagesSearchCount;
                                    int i3 = j3 == j8 ? 0 : 1;
                                    if (!(messages_messages instanceof TL_messages_messagesSlice)) {
                                        if (!(messages_messages instanceof TL_messages_channelMessages)) {
                                            size = messages_messages.messages.size();
                                            access$4000[i3] = size;
                                            if (DataQuery.this.searchResultMessages.isEmpty()) {
                                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i5), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                            } else if (i2 != 0) {
                                                if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                                                    DataQuery.this.lastReturnedNum = DataQuery.this.searchResultMessages.size() - 1;
                                                }
                                                messageObject = (MessageObject) DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i5), Integer.valueOf(messageObject.getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]));
                                            }
                                            if (j3 == j8 && DataQuery.this.messagesSearchEndReached[0] && j5 != 0 && !DataQuery.this.messagesSearchEndReached[1]) {
                                                DataQuery.this.searchMessagesInChat(DataQuery.this.lastSearchQuery, j8, j5, i5, 0, true, user2);
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                    size = messages_messages.count;
                                    access$4000[i3] = size;
                                    if (DataQuery.this.searchResultMessages.isEmpty()) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i5), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    } else if (i2 != 0) {
                                        if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                                            DataQuery.this.lastReturnedNum = DataQuery.this.searchResultMessages.size() - 1;
                                        }
                                        messageObject = (MessageObject) DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i5), Integer.valueOf(messageObject.getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]));
                                    }
                                    if (j3 == j8) {
                                    }
                                }
                            }
                        }
                    });
                }
            }, 2);
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    public void loadMedia(long j, int i, int i2, int i3, boolean z, int i4) {
        final int i5 = i3;
        final long j2 = j;
        int i6 = (int) j2;
        final boolean z2 = i6 < 0 && ChatObject.isChannel(-i6, this.currentAccount);
        if (!z) {
            if (i6 != 0) {
                TLObject tL_messages_search = new TL_messages_search();
                tL_messages_search.limit = i + 1;
                int i7 = i2;
                tL_messages_search.offset_id = i7;
                if (i5 == 0) {
                    tL_messages_search.filter = new TL_inputMessagesFilterPhotoVideo();
                } else if (i5 == 1) {
                    tL_messages_search.filter = new TL_inputMessagesFilterDocument();
                } else if (i5 == 2) {
                    tL_messages_search.filter = new TL_inputMessagesFilterVoice();
                } else if (i5 == 3) {
                    tL_messages_search.filter = new TL_inputMessagesFilterUrl();
                } else if (i5 == 4) {
                    tL_messages_search.filter = new TL_inputMessagesFilterMusic();
                }
                tL_messages_search.f49q = TtmlNode.ANONYMOUS_REGION_ID;
                tL_messages_search.peer = MessagesController.getInstance(r9.currentAccount).getInputPeer(i6);
                if (tL_messages_search.peer != null) {
                    final int i8 = i;
                    final int i9 = i7;
                    i7 = i4;
                    ConnectionsManager.getInstance(r9.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(r9.currentAccount).sendRequest(tL_messages_search, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                boolean z;
                                messages_Messages messages_messages = (messages_Messages) tLObject;
                                if (messages_messages.messages.size() > i8) {
                                    messages_messages.messages.remove(messages_messages.messages.size() - 1);
                                    z = null;
                                } else {
                                    z = true;
                                }
                                DataQuery.this.processLoadedMedia(messages_messages, j2, i8, i9, i5, false, i7, z2, z);
                            }
                        }
                    }), i4);
                }
                return;
            }
        }
        loadMediaDatabase(j2, i, i2, i5, i4, z2);
    }

    public void getMediaCount(long j, int i, int i2, boolean z) {
        int i3 = (int) j;
        if (!z) {
            if (i3 != 0) {
                z = new TL_messages_search();
                z.limit = 1;
                z.offset_id = 0;
                if (i == 0) {
                    z.filter = new TL_inputMessagesFilterPhotoVideo();
                } else if (i == 1) {
                    z.filter = new TL_inputMessagesFilterDocument();
                } else if (i == 2) {
                    z.filter = new TL_inputMessagesFilterVoice();
                } else if (i == 3) {
                    z.filter = new TL_inputMessagesFilterUrl();
                } else if (i == 4) {
                    z.filter = new TL_inputMessagesFilterMusic();
                }
                z.f49q = TtmlNode.ANONYMOUS_REGION_ID;
                z.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i3);
                if (z.peer != null) {
                    final long j2 = j;
                    final int i4 = i;
                    final int i5 = i2;
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tL_error == null) {
                                final messages_Messages messages_messages = (messages_Messages) tLObject;
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                if ((messages_messages instanceof TL_messages_messages) != null) {
                                    tL_error = messages_messages.messages.size();
                                } else {
                                    tL_error = messages_messages.count;
                                }
                                int i = tL_error;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(messages_messages.users, false);
                                        MessagesController.getInstance(DataQuery.this.currentAccount).putChats(messages_messages.chats, false);
                                    }
                                });
                                DataQuery.this.processLoadedMediaCount(i, j2, i4, i5, false);
                            }
                        }
                    }), i2);
                }
                return;
            }
        }
        getMediaCountDatabase(j, i, i2);
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        int i = 0;
        if (message.media instanceof TL_messageMediaPhoto) {
            return 0;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            if (!MessageObject.isVoiceMessage(message)) {
                if (!MessageObject.isRoundVideoMessage(message)) {
                    if (MessageObject.isVideoMessage(message)) {
                        return 0;
                    }
                    if (MessageObject.isStickerMessage(message)) {
                        return -1;
                    }
                    return MessageObject.isMusicMessage(message) != null ? 4 : 1;
                }
            }
            return 2;
        }
        if (!message.entities.isEmpty()) {
            while (i < message.entities.size()) {
                MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                if (!((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl))) {
                    if (!(messageEntity instanceof TL_messageEntityEmail)) {
                        i++;
                    }
                }
                return 3;
            }
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(Message message) {
        boolean z = message instanceof TL_message_secret;
        if (z && (((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && message.media.ttl_seconds != 0 && message.media.ttl_seconds <= 60)) {
            return false;
        }
        if (!z && (message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return false;
        }
        if (!(message.media instanceof TL_messageMediaPhoto)) {
            if (!(message.media instanceof TL_messageMediaDocument) || MessageObject.isGifDocument(message.media.document)) {
                if (!message.entities.isEmpty()) {
                    int i = 0;
                    while (i < message.entities.size()) {
                        MessageEntity messageEntity = (MessageEntity) message.entities.get(i);
                        if (!((messageEntity instanceof TL_messageEntityUrl) || (messageEntity instanceof TL_messageEntityTextUrl))) {
                            if (!(messageEntity instanceof TL_messageEntityEmail)) {
                                i++;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private void processLoadedMedia(messages_Messages messages_messages, long j, int i, int i2, int i3, boolean z, int i4, boolean z2, boolean z3) {
        DataQuery dataQuery = this;
        messages_Messages messages_messages2 = messages_messages;
        long j2 = j;
        int i5 = (int) j2;
        if (z && messages_messages2.messages.isEmpty() && i5 != 0) {
            loadMedia(j2, i, i2, i3, false, i4);
            return;
        }
        if (!z) {
            ImageLoader.saveMessagesThumbs(messages_messages2.messages);
            MessagesStorage.getInstance(dataQuery.currentAccount).putUsersAndChats(messages_messages2.users, messages_messages2.chats, true, true);
            putMediaDatabase(j2, i3, messages_messages2.messages, i2, z3);
        }
        SparseArray sparseArray = new SparseArray();
        int i6 = 0;
        for (int i7 = 0; i7 < messages_messages2.users.size(); i7++) {
            User user = (User) messages_messages2.users.get(i7);
            sparseArray.put(user.id, user);
        }
        final ArrayList arrayList = new ArrayList();
        while (i6 < messages_messages2.messages.size()) {
            arrayList.add(new MessageObject(dataQuery.currentAccount, (Message) messages_messages2.messages.get(i6), sparseArray, true));
            i6++;
        }
        final messages_Messages messages_messages3 = messages_messages2;
        final boolean z4 = z;
        final long j3 = j2;
        final int i8 = i4;
        final int i9 = i3;
        final boolean z5 = z3;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = messages_messages3.count;
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(messages_messages3.users, z4);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(messages_messages3.chats, z4);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(j3), Integer.valueOf(i), arrayList, Integer.valueOf(i8), Integer.valueOf(i9), Boolean.valueOf(z5));
            }
        });
    }

    private void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z) {
        final long j2 = j;
        final boolean z2 = z;
        final int i4 = i;
        final int i5 = i2;
        final int i6 = i3;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = (int) j2;
                if (z2 && i4 == -1 && i != 0) {
                    DataQuery.this.getMediaCount(j2, i5, i6, false);
                    return;
                }
                if (!z2) {
                    DataQuery.this.putMediaCountDatabase(j2, i5, i4);
                }
                NotificationCenter instance = NotificationCenter.getInstance(DataQuery.this.currentAccount);
                int i2 = NotificationCenter.mediaCountDidLoaded;
                Object[] objArr = new Object[4];
                int i3 = 0;
                objArr[0] = Long.valueOf(j2);
                if (!z2 || i4 != -1) {
                    i3 = i4;
                }
                objArr[1] = Integer.valueOf(i3);
                objArr[2] = Boolean.valueOf(z2);
                objArr[3] = Integer.valueOf(i5);
                instance.postNotificationName(i2, objArr);
            }
        });
    }

    private void putMediaCountDatabase(long j, int i, int i2) {
        final long j2 = j;
        final int i3 = i;
        final int i4 = i2;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j2);
                    executeFast.bindInteger(2, i3);
                    executeFast.bindInteger(3, i4);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void getMediaCountDatabase(long j, int i, int i2) {
        final long j2 = j;
        final int i3 = i;
        final int i4 = i2;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j2), Integer.valueOf(i3)}), new Object[0]);
                    int intValue = queryFinalized.next() ? queryFinalized.intValue(0) : -1;
                    queryFinalized.dispose();
                    int i = (int) j2;
                    if (intValue == -1 && i == 0) {
                        queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j2), Integer.valueOf(i3)}), new Object[0]);
                        if (queryFinalized.next()) {
                            intValue = queryFinalized.intValue(0);
                        }
                        queryFinalized.dispose();
                        if (intValue != -1) {
                            DataQuery.this.putMediaCountDatabase(j2, i3, intValue);
                        }
                    }
                    DataQuery.this.processLoadedMediaCount(intValue, j2, i3, i4, true);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, boolean z) {
        final int i5 = i;
        final long j2 = j;
        final int i6 = i2;
        final boolean z2 = z;
        final int i7 = i3;
        final int i8 = i4;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                messages_Messages messages_messages;
                Throwable th;
                Throwable e;
                messages_Messages messages_messages2;
                Throwable th2;
                boolean z;
                AnonymousClass39 anonymousClass39 = this;
                messages_Messages tL_messages_messages = new TL_messages_messages();
                try {
                    boolean z2;
                    ArrayList arrayList;
                    SQLiteCursor queryFinalized;
                    boolean z3;
                    ArrayList arrayList2;
                    ArrayList arrayList3 = new ArrayList();
                    Iterable arrayList4 = new ArrayList();
                    int i = i5 + 1;
                    SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                    Object[] objArr;
                    SQLiteDatabase sQLiteDatabase;
                    if (((int) j2) != 0) {
                        try {
                            long j = (long) i6;
                            int i2 = z2 ? -((int) j2) : 0;
                            if (!(j == 0 || i2 == 0)) {
                                j |= ((long) i2) << 32;
                            }
                            r15 = new Object[2];
                            SQLiteDatabase sQLiteDatabase2 = database;
                            r15[0] = Long.valueOf(j2);
                            r15[1] = Integer.valueOf(i7);
                            SQLiteDatabase sQLiteDatabase3 = sQLiteDatabase2;
                            SQLiteCursor queryFinalized2 = sQLiteDatabase3.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", r15), new Object[0]);
                            if (queryFinalized2.next()) {
                                boolean z4 = queryFinalized2.intValue(0) == 1;
                                queryFinalized2.dispose();
                                z2 = z4;
                            } else {
                                queryFinalized2.dispose();
                                queryFinalized2 = sQLiteDatabase3.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(j2), Integer.valueOf(i7)}), new Object[0]);
                                if (queryFinalized2.next()) {
                                    int intValue = queryFinalized2.intValue(0);
                                    if (intValue != 0) {
                                        SQLitePreparedStatement executeFast = sQLiteDatabase3.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                        executeFast.requery();
                                        executeFast.bindLong(1, j2);
                                        executeFast.bindInteger(2, i7);
                                        executeFast.bindInteger(3, 0);
                                        executeFast.bindInteger(4, intValue);
                                        executeFast.step();
                                        executeFast.dispose();
                                    }
                                }
                                queryFinalized2.dispose();
                                z2 = false;
                            }
                            if (j != 0) {
                                Locale locale = Locale.US;
                                String str = "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1";
                                Object[] objArr2 = new Object[3];
                                arrayList = arrayList3;
                                messages_messages = tL_messages_messages;
                                try {
                                    long intValue2;
                                    objArr2[0] = Long.valueOf(j2);
                                    objArr2[1] = Integer.valueOf(i7);
                                    objArr2[2] = Integer.valueOf(i6);
                                    queryFinalized = sQLiteDatabase3.queryFinalized(String.format(locale, str, objArr2), new Object[0]);
                                    if (queryFinalized.next()) {
                                        intValue2 = (long) queryFinalized.intValue(0);
                                        if (i2 != 0) {
                                            intValue2 |= ((long) i2) << 32;
                                        }
                                    } else {
                                        intValue2 = 0;
                                    }
                                    queryFinalized.dispose();
                                    if (intValue2 > 1) {
                                        objArr = new Object[5];
                                        SQLiteDatabase sQLiteDatabase4 = sQLiteDatabase3;
                                        objArr[0] = Long.valueOf(j2);
                                        objArr[1] = Long.valueOf(j);
                                        objArr[2] = Long.valueOf(intValue2);
                                        objArr[3] = Integer.valueOf(i7);
                                        objArr[4] = Integer.valueOf(i);
                                        queryFinalized = sQLiteDatabase4.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", objArr), new Object[0]);
                                    } else {
                                        queryFinalized = sQLiteDatabase3.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j2), Long.valueOf(j), Integer.valueOf(i7), Integer.valueOf(i)}), new Object[0]);
                                    }
                                } catch (Throwable e2) {
                                    th = e2;
                                    messages_messages2 = messages_messages;
                                } catch (Throwable e22) {
                                    th2 = e22;
                                    messages_messages2 = messages_messages;
                                }
                            } else {
                                long intValue3;
                                arrayList = arrayList3;
                                messages_messages = tL_messages_messages;
                                sQLiteDatabase = sQLiteDatabase3;
                                queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j2), Integer.valueOf(i7)}), new Object[0]);
                                if (queryFinalized.next()) {
                                    intValue3 = (long) queryFinalized.intValue(0);
                                    if (i2 != 0) {
                                        intValue3 |= ((long) i2) << 32;
                                    }
                                } else {
                                    intValue3 = 0;
                                }
                                queryFinalized.dispose();
                                if (intValue3 > 1) {
                                    queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j2), Long.valueOf(intValue3), Integer.valueOf(i7), Integer.valueOf(i)}), new Object[0]);
                                } else {
                                    queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j2), Integer.valueOf(i7), Integer.valueOf(i)}), new Object[0]);
                                }
                            }
                            z3 = false;
                        } catch (Throwable e222) {
                            th = e222;
                            messages_messages2 = tL_messages_messages;
                            z = false;
                            try {
                                messages_messages2.messages.clear();
                                messages_messages2.chats.clear();
                                messages_messages2.users.clear();
                                FileLog.m3e(th);
                                DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, z);
                            } catch (Throwable th3) {
                                e222 = th3;
                                th2 = e222;
                                DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, false);
                                throw th2;
                            }
                        } catch (Throwable e2222) {
                            th2 = e2222;
                            messages_messages2 = tL_messages_messages;
                            DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, false);
                            throw th2;
                        }
                    }
                    arrayList = arrayList3;
                    messages_messages = tL_messages_messages;
                    sQLiteDatabase = database;
                    try {
                        if (i6 != 0) {
                            queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j2), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i)}), new Object[0]);
                            z3 = false;
                        } else {
                            objArr = new Object[]{Long.valueOf(j2), Integer.valueOf(i7), Integer.valueOf(i)};
                            z3 = false;
                            queryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", objArr), new Object[0]);
                        }
                        z2 = true;
                    } catch (Exception e3) {
                        e2222 = e3;
                        messages_messages2 = messages_messages;
                        th = e2222;
                        z = false;
                        messages_messages2.messages.clear();
                        messages_messages2.chats.clear();
                        messages_messages2.users.clear();
                        FileLog.m3e(th);
                        DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, z);
                    } catch (Throwable th4) {
                        e2222 = th4;
                        messages_messages2 = messages_messages;
                        th2 = e2222;
                        DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, false);
                        throw th2;
                    }
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(z3);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z3), z3);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            TLdeserialize.id = queryFinalized.intValue(1);
                            TLdeserialize.dialog_id = j2;
                            if (((int) j2) == 0) {
                                TLdeserialize.random_id = queryFinalized.longValue(2);
                            }
                            messages_messages2 = messages_messages;
                            try {
                                messages_messages2.messages.add(TLdeserialize);
                                if (TLdeserialize.from_id > 0) {
                                    arrayList2 = arrayList;
                                    if (!arrayList2.contains(Integer.valueOf(TLdeserialize.from_id))) {
                                        arrayList2.add(Integer.valueOf(TLdeserialize.from_id));
                                    }
                                } else {
                                    arrayList2 = arrayList;
                                    if (!arrayList4.contains(Integer.valueOf(-TLdeserialize.from_id))) {
                                        arrayList4.add(Integer.valueOf(-TLdeserialize.from_id));
                                    }
                                }
                            } catch (Exception e4) {
                                e2222 = e4;
                            }
                        } else {
                            messages_messages2 = messages_messages;
                            arrayList2 = arrayList;
                        }
                        messages_messages = messages_messages2;
                        arrayList = arrayList2;
                    }
                    messages_messages2 = messages_messages;
                    arrayList2 = arrayList;
                    queryFinalized.dispose();
                    if (!arrayList2.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", arrayList2), messages_messages2.users);
                    }
                    if (!arrayList4.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", arrayList4), messages_messages2.chats);
                    }
                    if (messages_messages2.messages.size() > i5) {
                        messages_messages2.messages.remove(messages_messages2.messages.size() - 1);
                        z = z3;
                    } else {
                        z = z2;
                    }
                } catch (Exception e5) {
                    e2222 = e5;
                    messages_messages2 = tL_messages_messages;
                    th = e2222;
                    z = false;
                    messages_messages2.messages.clear();
                    messages_messages2.chats.clear();
                    messages_messages2.users.clear();
                    FileLog.m3e(th);
                    DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, z);
                } catch (Throwable th5) {
                    e2222 = th5;
                    messages_messages2 = tL_messages_messages;
                    th2 = e2222;
                    DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, false);
                    throw th2;
                }
                DataQuery.this.processLoadedMedia(messages_messages2, j2, i5, i6, i7, true, i8, z2, z);
            }
        });
    }

    private void putMediaDatabase(long j, int i, ArrayList<Message> arrayList, int i2, boolean z) {
        final ArrayList<Message> arrayList2 = arrayList;
        final boolean z2 = z;
        final long j2 = j;
        final int i3 = i2;
        final int i4 = i;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    int i;
                    if (arrayList2.isEmpty() || z2) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).doneHolesInMedia(j2, i3, i4);
                        if (arrayList2.isEmpty()) {
                            return;
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    Iterator it = arrayList2.iterator();
                    while (true) {
                        i = 1;
                        if (!it.hasNext()) {
                            break;
                        }
                        Message message = (Message) it.next();
                        if (DataQuery.canAddMessageToMedia(message)) {
                            long j = (long) message.id;
                            if (message.to_id.channel_id != 0) {
                                j |= ((long) message.to_id.channel_id) << 32;
                            }
                            executeFast.requery();
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(nativeByteBuffer);
                            executeFast.bindLong(1, j);
                            executeFast.bindLong(2, j2);
                            executeFast.bindInteger(3, message.date);
                            executeFast.bindInteger(4, i4);
                            executeFast.bindByteBuffer(5, nativeByteBuffer);
                            executeFast.step();
                            nativeByteBuffer.reuse();
                        }
                    }
                    executeFast.dispose();
                    if (!(z2 && i3 == 0)) {
                        if (!z2) {
                            i = ((Message) arrayList2.get(arrayList2.size() - 1)).id;
                        }
                        int i2 = i;
                        if (i3 != 0) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j2, i2, i3, i4);
                        } else {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j2, i2, ConnectionsManager.DEFAULT_DATACENTER_ID, i4);
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadMusic(long j, long j2) {
        final long j3 = j;
        final long j4 = j2;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                final ArrayList arrayList = new ArrayList();
                try {
                    SQLiteCursor queryFinalized;
                    if (((int) j3) != 0) {
                        queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j3), Long.valueOf(j4), Integer.valueOf(4)}), new Object[0]);
                    } else {
                        queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j3), Long.valueOf(j4), Integer.valueOf(4)}), new Object[0]);
                    }
                    while (queryFinalized.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                            byteBufferValue.reuse();
                            if (MessageObject.isMusicMessage(TLdeserialize)) {
                                TLdeserialize.id = queryFinalized.intValue(1);
                                TLdeserialize.dialog_id = j3;
                                arrayList.add(0, new MessageObject(DataQuery.this.currentAccount, TLdeserialize, false));
                            }
                        }
                    }
                    queryFinalized.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.musicDidLoaded, Long.valueOf(j3), arrayList);
                    }
                });
            }
        });
    }

    public void buildShortcuts() {
        if (VERSION.SDK_INT >= 25) {
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.hints.size(); i++) {
                arrayList.add(this.hints.get(i));
                if (arrayList.size() == 3) {
                    break;
                }
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                @android.annotation.SuppressLint({"NewApi"})
                public void run() {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                    /*
                    r19 = this;
                    r1 = r19;
                    r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r3 = android.content.pm.ShortcutManager.class;	 Catch:{ Throwable -> 0x02ea }
                    r2 = r2.getSystemService(r3);	 Catch:{ Throwable -> 0x02ea }
                    r2 = (android.content.pm.ShortcutManager) r2;	 Catch:{ Throwable -> 0x02ea }
                    r3 = r2.getDynamicShortcuts();	 Catch:{ Throwable -> 0x02ea }
                    r4 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02ea }
                    r4.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02ea }
                    r5.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r6 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02ea }
                    r6.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r7 = 0;	 Catch:{ Throwable -> 0x02ea }
                    if (r3 == 0) goto L_0x00a2;	 Catch:{ Throwable -> 0x02ea }
                L_0x0022:
                    r8 = r3.isEmpty();	 Catch:{ Throwable -> 0x02ea }
                    if (r8 != 0) goto L_0x00a2;	 Catch:{ Throwable -> 0x02ea }
                L_0x0028:
                    r8 = "compose";	 Catch:{ Throwable -> 0x02ea }
                    r5.add(r8);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r7;	 Catch:{ Throwable -> 0x02ea }
                L_0x002e:
                    r9 = r0;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r9.size();	 Catch:{ Throwable -> 0x02ea }
                    if (r8 >= r9) goto L_0x0075;	 Catch:{ Throwable -> 0x02ea }
                L_0x0036:
                    r9 = r0;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r9.get(r8);	 Catch:{ Throwable -> 0x02ea }
                    r9 = (org.telegram.tgnet.TLRPC.TL_topPeer) r9;	 Catch:{ Throwable -> 0x02ea }
                    r10 = r9.peer;	 Catch:{ Throwable -> 0x02ea }
                    r10 = r10.user_id;	 Catch:{ Throwable -> 0x02ea }
                    if (r10 == 0) goto L_0x004a;	 Catch:{ Throwable -> 0x02ea }
                L_0x0044:
                    r9 = r9.peer;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r9.user_id;	 Catch:{ Throwable -> 0x02ea }
                    r9 = (long) r9;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x005e;	 Catch:{ Throwable -> 0x02ea }
                L_0x004a:
                    r10 = r9.peer;	 Catch:{ Throwable -> 0x02ea }
                    r10 = r10.chat_id;	 Catch:{ Throwable -> 0x02ea }
                    r10 = -r10;	 Catch:{ Throwable -> 0x02ea }
                    r10 = (long) r10;	 Catch:{ Throwable -> 0x02ea }
                    r12 = 0;	 Catch:{ Throwable -> 0x02ea }
                    r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));	 Catch:{ Throwable -> 0x02ea }
                    if (r14 != 0) goto L_0x005d;	 Catch:{ Throwable -> 0x02ea }
                L_0x0056:
                    r9 = r9.peer;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r9.channel_id;	 Catch:{ Throwable -> 0x02ea }
                    r9 = -r9;	 Catch:{ Throwable -> 0x02ea }
                    r9 = (long) r9;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x005e;	 Catch:{ Throwable -> 0x02ea }
                L_0x005d:
                    r9 = r10;	 Catch:{ Throwable -> 0x02ea }
                L_0x005e:
                    r11 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02ea }
                    r11.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r12 = "did";	 Catch:{ Throwable -> 0x02ea }
                    r11.append(r12);	 Catch:{ Throwable -> 0x02ea }
                    r11.append(r9);	 Catch:{ Throwable -> 0x02ea }
                    r9 = r11.toString();	 Catch:{ Throwable -> 0x02ea }
                    r5.add(r9);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8 + 1;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x002e;	 Catch:{ Throwable -> 0x02ea }
                L_0x0075:
                    r8 = r7;	 Catch:{ Throwable -> 0x02ea }
                L_0x0076:
                    r9 = r3.size();	 Catch:{ Throwable -> 0x02ea }
                    if (r8 >= r9) goto L_0x0095;	 Catch:{ Throwable -> 0x02ea }
                L_0x007c:
                    r9 = r3.get(r8);	 Catch:{ Throwable -> 0x02ea }
                    r9 = (android.content.pm.ShortcutInfo) r9;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r9.getId();	 Catch:{ Throwable -> 0x02ea }
                    r10 = r5.remove(r9);	 Catch:{ Throwable -> 0x02ea }
                    if (r10 != 0) goto L_0x008f;	 Catch:{ Throwable -> 0x02ea }
                L_0x008c:
                    r6.add(r9);	 Catch:{ Throwable -> 0x02ea }
                L_0x008f:
                    r4.add(r9);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8 + 1;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x0076;	 Catch:{ Throwable -> 0x02ea }
                L_0x0095:
                    r3 = r5.isEmpty();	 Catch:{ Throwable -> 0x02ea }
                    if (r3 == 0) goto L_0x00a2;	 Catch:{ Throwable -> 0x02ea }
                L_0x009b:
                    r3 = r6.isEmpty();	 Catch:{ Throwable -> 0x02ea }
                    if (r3 == 0) goto L_0x00a2;	 Catch:{ Throwable -> 0x02ea }
                L_0x00a1:
                    return;	 Catch:{ Throwable -> 0x02ea }
                L_0x00a2:
                    r3 = new android.content.Intent;	 Catch:{ Throwable -> 0x02ea }
                    r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r8 = org.telegram.ui.LaunchActivity.class;	 Catch:{ Throwable -> 0x02ea }
                    r3.<init>(r5, r8);	 Catch:{ Throwable -> 0x02ea }
                    r5 = "new_dialog";	 Catch:{ Throwable -> 0x02ea }
                    r3.setAction(r5);	 Catch:{ Throwable -> 0x02ea }
                    r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x02ea }
                    r5.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r8 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02ea }
                    r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r10 = "compose";	 Catch:{ Throwable -> 0x02ea }
                    r8.<init>(r9, r10);	 Catch:{ Throwable -> 0x02ea }
                    r9 = "NewConversationShortcut";	 Catch:{ Throwable -> 0x02ea }
                    r10 = NUM; // 0x7f0c03ec float:1.8611228E38 double:1.0530978945E-314;	 Catch:{ Throwable -> 0x02ea }
                    r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.setShortLabel(r9);	 Catch:{ Throwable -> 0x02ea }
                    r9 = "NewConversationShortcut";	 Catch:{ Throwable -> 0x02ea }
                    r9 = org.telegram.messenger.LocaleController.getString(r9, r10);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.setLongLabel(r9);	 Catch:{ Throwable -> 0x02ea }
                    r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r10 = NUM; // 0x7f0701c6 float:1.7945499E38 double:1.0529357273E-314;	 Catch:{ Throwable -> 0x02ea }
                    r9 = android.graphics.drawable.Icon.createWithResource(r9, r10);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.setIcon(r9);	 Catch:{ Throwable -> 0x02ea }
                    r3 = r8.setIntent(r3);	 Catch:{ Throwable -> 0x02ea }
                    r3 = r3.build();	 Catch:{ Throwable -> 0x02ea }
                    r5.add(r3);	 Catch:{ Throwable -> 0x02ea }
                    r3 = "compose";	 Catch:{ Throwable -> 0x02ea }
                    r3 = r4.contains(r3);	 Catch:{ Throwable -> 0x02ea }
                    if (r3 == 0) goto L_0x00f9;	 Catch:{ Throwable -> 0x02ea }
                L_0x00f5:
                    r2.updateShortcuts(r5);	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x00fc;	 Catch:{ Throwable -> 0x02ea }
                L_0x00f9:
                    r2.addDynamicShortcuts(r5);	 Catch:{ Throwable -> 0x02ea }
                L_0x00fc:
                    r5.clear();	 Catch:{ Throwable -> 0x02ea }
                    r3 = r6.isEmpty();	 Catch:{ Throwable -> 0x02ea }
                    if (r3 != 0) goto L_0x0108;	 Catch:{ Throwable -> 0x02ea }
                L_0x0105:
                    r2.removeDynamicShortcuts(r6);	 Catch:{ Throwable -> 0x02ea }
                L_0x0108:
                    r3 = r0;	 Catch:{ Throwable -> 0x02ea }
                    r3 = r3.size();	 Catch:{ Throwable -> 0x02ea }
                    if (r7 >= r3) goto L_0x02ea;	 Catch:{ Throwable -> 0x02ea }
                L_0x0110:
                    r3 = new android.content.Intent;	 Catch:{ Throwable -> 0x02ea }
                    r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r8 = org.telegram.messenger.OpenChatReceiver.class;	 Catch:{ Throwable -> 0x02ea }
                    r3.<init>(r6, r8);	 Catch:{ Throwable -> 0x02ea }
                    r6 = r0;	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.get(r7);	 Catch:{ Throwable -> 0x02ea }
                    r6 = (org.telegram.tgnet.TLRPC.TL_topPeer) r6;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.user_id;	 Catch:{ Throwable -> 0x02ea }
                    if (r8 == 0) goto L_0x014d;	 Catch:{ Throwable -> 0x02ea }
                L_0x0127:
                    r8 = "userId";	 Catch:{ Throwable -> 0x02ea }
                    r10 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r10 = r10.user_id;	 Catch:{ Throwable -> 0x02ea }
                    r3.putExtra(r8, r10);	 Catch:{ Throwable -> 0x02ea }
                    r8 = org.telegram.messenger.DataQuery.this;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.currentAccount;	 Catch:{ Throwable -> 0x02ea }
                    r8 = org.telegram.messenger.MessagesController.getInstance(r8);	 Catch:{ Throwable -> 0x02ea }
                    r10 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r10 = r10.user_id;	 Catch:{ Throwable -> 0x02ea }
                    r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.getUser(r10);	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.user_id;	 Catch:{ Throwable -> 0x02ea }
                    r10 = (long) r6;	 Catch:{ Throwable -> 0x02ea }
                    r6 = 0;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x0171;	 Catch:{ Throwable -> 0x02ea }
                L_0x014d:
                    r8 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.chat_id;	 Catch:{ Throwable -> 0x02ea }
                    if (r8 != 0) goto L_0x0157;	 Catch:{ Throwable -> 0x02ea }
                L_0x0153:
                    r6 = r6.peer;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r6.channel_id;	 Catch:{ Throwable -> 0x02ea }
                L_0x0157:
                    r6 = org.telegram.messenger.DataQuery.this;	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.currentAccount;	 Catch:{ Throwable -> 0x02ea }
                    r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Throwable -> 0x02ea }
                    r10 = java.lang.Integer.valueOf(r8);	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.getChat(r10);	 Catch:{ Throwable -> 0x02ea }
                    r10 = "chatId";	 Catch:{ Throwable -> 0x02ea }
                    r3.putExtra(r10, r8);	 Catch:{ Throwable -> 0x02ea }
                    r8 = -r8;	 Catch:{ Throwable -> 0x02ea }
                    r10 = (long) r8;	 Catch:{ Throwable -> 0x02ea }
                    r8 = 0;	 Catch:{ Throwable -> 0x02ea }
                L_0x0171:
                    if (r8 != 0) goto L_0x0177;	 Catch:{ Throwable -> 0x02ea }
                L_0x0173:
                    if (r6 != 0) goto L_0x0177;	 Catch:{ Throwable -> 0x02ea }
                L_0x0175:
                    goto L_0x02e4;	 Catch:{ Throwable -> 0x02ea }
                L_0x0177:
                    if (r8 == 0) goto L_0x0191;	 Catch:{ Throwable -> 0x02ea }
                L_0x0179:
                    r6 = r8.first_name;	 Catch:{ Throwable -> 0x02ea }
                    r12 = r8.last_name;	 Catch:{ Throwable -> 0x02ea }
                    r6 = org.telegram.messenger.ContactsController.formatName(r6, r12);	 Catch:{ Throwable -> 0x02ea }
                    r12 = r8.photo;	 Catch:{ Throwable -> 0x02ea }
                    if (r12 == 0) goto L_0x018f;	 Catch:{ Throwable -> 0x02ea }
                L_0x0185:
                    r8 = r8.photo;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r8.photo_small;	 Catch:{ Throwable -> 0x02ea }
                    r18 = r8;	 Catch:{ Throwable -> 0x02ea }
                    r8 = r6;	 Catch:{ Throwable -> 0x02ea }
                    r6 = r18;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x019d;	 Catch:{ Throwable -> 0x02ea }
                L_0x018f:
                    r8 = r6;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x019c;	 Catch:{ Throwable -> 0x02ea }
                L_0x0191:
                    r8 = r6.title;	 Catch:{ Throwable -> 0x02ea }
                    r12 = r6.photo;	 Catch:{ Throwable -> 0x02ea }
                    if (r12 == 0) goto L_0x019c;	 Catch:{ Throwable -> 0x02ea }
                L_0x0197:
                    r6 = r6.photo;	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.photo_small;	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x019d;	 Catch:{ Throwable -> 0x02ea }
                L_0x019c:
                    r6 = 0;	 Catch:{ Throwable -> 0x02ea }
                L_0x019d:
                    r12 = "currentAccount";	 Catch:{ Throwable -> 0x02ea }
                    r13 = org.telegram.messenger.DataQuery.this;	 Catch:{ Throwable -> 0x02ea }
                    r13 = r13.currentAccount;	 Catch:{ Throwable -> 0x02ea }
                    r3.putExtra(r12, r13);	 Catch:{ Throwable -> 0x02ea }
                    r12 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02ea }
                    r12.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r13 = "com.tmessages.openchat";	 Catch:{ Throwable -> 0x02ea }
                    r12.append(r13);	 Catch:{ Throwable -> 0x02ea }
                    r12.append(r10);	 Catch:{ Throwable -> 0x02ea }
                    r12 = r12.toString();	 Catch:{ Throwable -> 0x02ea }
                    r3.setAction(r12);	 Catch:{ Throwable -> 0x02ea }
                    r12 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;	 Catch:{ Throwable -> 0x02ea }
                    r3.addFlags(r12);	 Catch:{ Throwable -> 0x02ea }
                    if (r6 == 0) goto L_0x0289;
                L_0x01c3:
                    r12 = 1;
                    r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12);	 Catch:{ Throwable -> 0x0281 }
                    r6 = r6.toString();	 Catch:{ Throwable -> 0x0281 }
                    r6 = android.graphics.BitmapFactory.decodeFile(r6);	 Catch:{ Throwable -> 0x0281 }
                    if (r6 == 0) goto L_0x027f;
                L_0x01d2:
                    r13 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x027b }
                    r14 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x027b }
                    r14 = android.graphics.Bitmap.createBitmap(r13, r13, r14);	 Catch:{ Throwable -> 0x027b }
                    r15 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x027b }
                    r15.<init>(r14);	 Catch:{ Throwable -> 0x027b }
                    r16 = org.telegram.messenger.DataQuery.roundPaint;	 Catch:{ Throwable -> 0x027b }
                    if (r16 != 0) goto L_0x023d;	 Catch:{ Throwable -> 0x027b }
                L_0x01e9:
                    r9 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x027b }
                    r12 = 3;	 Catch:{ Throwable -> 0x027b }
                    r9.<init>(r12);	 Catch:{ Throwable -> 0x027b }
                    org.telegram.messenger.DataQuery.roundPaint = r9;	 Catch:{ Throwable -> 0x027b }
                    r9 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x027b }
                    r9.<init>();	 Catch:{ Throwable -> 0x027b }
                    org.telegram.messenger.DataQuery.bitmapRect = r9;	 Catch:{ Throwable -> 0x027b }
                    r9 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x027b }
                    r12 = 1;	 Catch:{ Throwable -> 0x027b }
                    r9.<init>(r12);	 Catch:{ Throwable -> 0x027b }
                    org.telegram.messenger.DataQuery.erasePaint = r9;	 Catch:{ Throwable -> 0x027b }
                    r9 = org.telegram.messenger.DataQuery.erasePaint;	 Catch:{ Throwable -> 0x027b }
                    r12 = new android.graphics.PorterDuffXfermode;	 Catch:{ Throwable -> 0x027b }
                    r1 = android.graphics.PorterDuff.Mode.CLEAR;	 Catch:{ Throwable -> 0x027b }
                    r12.<init>(r1);	 Catch:{ Throwable -> 0x027b }
                    r9.setXfermode(r12);	 Catch:{ Throwable -> 0x027b }
                    r1 = new android.graphics.Path;	 Catch:{ Throwable -> 0x027b }
                    r1.<init>();	 Catch:{ Throwable -> 0x027b }
                    org.telegram.messenger.DataQuery.roundPath = r1;	 Catch:{ Throwable -> 0x027b }
                    r1 = org.telegram.messenger.DataQuery.roundPath;	 Catch:{ Throwable -> 0x027b }
                    r9 = r13 / 2;	 Catch:{ Throwable -> 0x027b }
                    r9 = (float) r9;	 Catch:{ Throwable -> 0x027b }
                    r12 = r13 / 2;	 Catch:{ Throwable -> 0x027b }
                    r12 = (float) r12;	 Catch:{ Throwable -> 0x027b }
                    r13 = r13 / 2;	 Catch:{ Throwable -> 0x027b }
                    r17 = r14;	 Catch:{ Throwable -> 0x027b }
                    r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Throwable -> 0x027b }
                    r16 = org.telegram.messenger.AndroidUtilities.dp(r14);	 Catch:{ Throwable -> 0x027b }
                    r13 = r13 - r16;	 Catch:{ Throwable -> 0x027b }
                    r13 = (float) r13;	 Catch:{ Throwable -> 0x027b }
                    r14 = android.graphics.Path.Direction.CW;	 Catch:{ Throwable -> 0x027b }
                    r1.addCircle(r9, r12, r13, r14);	 Catch:{ Throwable -> 0x027b }
                    r1 = org.telegram.messenger.DataQuery.roundPath;	 Catch:{ Throwable -> 0x027b }
                    r1.toggleInverseFillType();	 Catch:{ Throwable -> 0x027b }
                    goto L_0x023f;	 Catch:{ Throwable -> 0x027b }
                L_0x023d:
                    r17 = r14;	 Catch:{ Throwable -> 0x027b }
                L_0x023f:
                    r1 = org.telegram.messenger.DataQuery.bitmapRect;	 Catch:{ Throwable -> 0x027b }
                    r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Throwable -> 0x027b }
                    r12 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x027b }
                    r12 = (float) r12;	 Catch:{ Throwable -> 0x027b }
                    r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x027b }
                    r9 = (float) r9;	 Catch:{ Throwable -> 0x027b }
                    r13 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;	 Catch:{ Throwable -> 0x027b }
                    r14 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x027b }
                    r14 = (float) r14;	 Catch:{ Throwable -> 0x027b }
                    r13 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x027b }
                    r13 = (float) r13;	 Catch:{ Throwable -> 0x027b }
                    r1.set(r12, r9, r14, r13);	 Catch:{ Throwable -> 0x027b }
                    r1 = org.telegram.messenger.DataQuery.bitmapRect;	 Catch:{ Throwable -> 0x027b }
                    r9 = org.telegram.messenger.DataQuery.roundPaint;	 Catch:{ Throwable -> 0x027b }
                    r12 = 0;	 Catch:{ Throwable -> 0x027b }
                    r15.drawBitmap(r6, r12, r1, r9);	 Catch:{ Throwable -> 0x027b }
                    r1 = org.telegram.messenger.DataQuery.roundPath;	 Catch:{ Throwable -> 0x027b }
                    r9 = org.telegram.messenger.DataQuery.erasePaint;	 Catch:{ Throwable -> 0x027b }
                    r15.drawPath(r1, r9);	 Catch:{ Throwable -> 0x027b }
                    r15.setBitmap(r12);	 Catch:{ Exception -> 0x0278 }
                L_0x0278:
                    r9 = r17;
                    goto L_0x028b;
                L_0x027b:
                    r0 = move-exception;
                    r1 = r0;
                    r9 = r6;
                    goto L_0x0285;
                L_0x027f:
                    r9 = r6;
                    goto L_0x028b;
                L_0x0281:
                    r0 = move-exception;
                    r12 = 0;
                    r1 = r0;
                    r9 = r12;
                L_0x0285:
                    org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x028b;	 Catch:{ Throwable -> 0x02ea }
                L_0x0289:
                    r12 = 0;	 Catch:{ Throwable -> 0x02ea }
                    r9 = r12;	 Catch:{ Throwable -> 0x02ea }
                L_0x028b:
                    r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02ea }
                    r1.<init>();	 Catch:{ Throwable -> 0x02ea }
                    r6 = "did";	 Catch:{ Throwable -> 0x02ea }
                    r1.append(r6);	 Catch:{ Throwable -> 0x02ea }
                    r1.append(r10);	 Catch:{ Throwable -> 0x02ea }
                    r1 = r1.toString();	 Catch:{ Throwable -> 0x02ea }
                    r6 = android.text.TextUtils.isEmpty(r8);	 Catch:{ Throwable -> 0x02ea }
                    if (r6 == 0) goto L_0x02a4;	 Catch:{ Throwable -> 0x02ea }
                L_0x02a2:
                    r8 = " ";	 Catch:{ Throwable -> 0x02ea }
                L_0x02a4:
                    r6 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Throwable -> 0x02ea }
                    r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r6.<init>(r10, r1);	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.setShortLabel(r8);	 Catch:{ Throwable -> 0x02ea }
                    r6 = r6.setLongLabel(r8);	 Catch:{ Throwable -> 0x02ea }
                    r3 = r6.setIntent(r3);	 Catch:{ Throwable -> 0x02ea }
                    if (r9 == 0) goto L_0x02c1;	 Catch:{ Throwable -> 0x02ea }
                L_0x02b9:
                    r6 = android.graphics.drawable.Icon.createWithBitmap(r9);	 Catch:{ Throwable -> 0x02ea }
                    r3.setIcon(r6);	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x02cd;	 Catch:{ Throwable -> 0x02ea }
                L_0x02c1:
                    r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x02ea }
                    r8 = NUM; // 0x7f0701c7 float:1.79455E38 double:1.052935728E-314;	 Catch:{ Throwable -> 0x02ea }
                    r6 = android.graphics.drawable.Icon.createWithResource(r6, r8);	 Catch:{ Throwable -> 0x02ea }
                    r3.setIcon(r6);	 Catch:{ Throwable -> 0x02ea }
                L_0x02cd:
                    r3 = r3.build();	 Catch:{ Throwable -> 0x02ea }
                    r5.add(r3);	 Catch:{ Throwable -> 0x02ea }
                    r1 = r4.contains(r1);	 Catch:{ Throwable -> 0x02ea }
                    if (r1 == 0) goto L_0x02de;	 Catch:{ Throwable -> 0x02ea }
                L_0x02da:
                    r2.updateShortcuts(r5);	 Catch:{ Throwable -> 0x02ea }
                    goto L_0x02e1;	 Catch:{ Throwable -> 0x02ea }
                L_0x02de:
                    r2.addDynamicShortcuts(r5);	 Catch:{ Throwable -> 0x02ea }
                L_0x02e1:
                    r5.clear();	 Catch:{ Throwable -> 0x02ea }
                L_0x02e4:
                    r7 = r7 + 1;
                    r1 = r19;
                    goto L_0x0108;
                L_0x02ea:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.42.run():void");
                }
            });
        }
    }

    public void loadHints(boolean z) {
        if (!this.loading) {
            if (!z) {
                this.loading = true;
                z = new TL_contacts_getTopPeers();
                z.hash = 0;
                z.bots_pm = false;
                z.correspondents = true;
                z.groups = false;
                z.channels = false;
                z.bots_inline = true;
                z.offset = 0;
                z.limit = 20;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(z, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        if ((tLObject instanceof TL_contacts_topPeers) != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    final TL_contacts_topPeers tL_contacts_topPeers = (TL_contacts_topPeers) tLObject;
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(tL_contacts_topPeers.users, false);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(tL_contacts_topPeers.chats, false);
                                    for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
                                        TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
                                        if (tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline) {
                                            DataQuery.this.inlineBots = tL_topPeerCategoryPeers.peers;
                                            UserConfig.getInstance(DataQuery.this.currentAccount).botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
                                        } else {
                                            DataQuery.this.hints = tL_topPeerCategoryPeers.peers;
                                            int clientUserId = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                                            for (int i2 = 0; i2 < DataQuery.this.hints.size(); i2++) {
                                                if (((TL_topPeer) DataQuery.this.hints.get(i2)).peer.user_id == clientUserId) {
                                                    DataQuery.this.hints.remove(i2);
                                                    break;
                                                }
                                            }
                                            UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
                                        }
                                    }
                                    UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                                    DataQuery.this.buildShortcuts();
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                        /* renamed from: org.telegram.messenger.DataQuery$44$1$1$1 */
                                        class C01271 implements Runnable {
                                            C01271() {
                                            }

                                            public void run() {
                                                UserConfig.getInstance(DataQuery.this.currentAccount).lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
                                                UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                                            }
                                        }

                                        public void run() {
                                            try {
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(tL_contacts_topPeers.users, tL_contacts_topPeers.chats, false, false);
                                                SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                                                for (int i = 0; i < tL_contacts_topPeers.categories.size(); i++) {
                                                    TL_topPeerCategoryPeers tL_topPeerCategoryPeers = (TL_topPeerCategoryPeers) tL_contacts_topPeers.categories.get(i);
                                                    int i2 = tL_topPeerCategoryPeers.category instanceof TL_topPeerCategoryBotsInline ? 1 : 0;
                                                    for (int i3 = 0; i3 < tL_topPeerCategoryPeers.peers.size(); i3++) {
                                                        int i4;
                                                        TL_topPeer tL_topPeer = (TL_topPeer) tL_topPeerCategoryPeers.peers.get(i3);
                                                        if (tL_topPeer.peer instanceof TL_peerUser) {
                                                            i4 = tL_topPeer.peer.user_id;
                                                        } else if (tL_topPeer.peer instanceof TL_peerChat) {
                                                            i4 = -tL_topPeer.peer.chat_id;
                                                        } else {
                                                            i4 = -tL_topPeer.peer.channel_id;
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
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                                                AndroidUtilities.runOnUIThread(new C01271());
                                            } catch (Throwable e) {
                                                FileLog.m3e(e);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else if (!this.loaded) {
                this.loading = true;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        final ArrayList arrayList = new ArrayList();
                        final ArrayList arrayList2 = new ArrayList();
                        final ArrayList arrayList3 = new ArrayList();
                        final ArrayList arrayList4 = new ArrayList();
                        int clientUserId = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                        try {
                            Iterable arrayList5 = new ArrayList();
                            Iterable arrayList6 = new ArrayList();
                            SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
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
                            if (!arrayList5.isEmpty()) {
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", arrayList5), arrayList3);
                            }
                            if (!arrayList6.isEmpty()) {
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", arrayList6), arrayList4);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList3, true);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList4, true);
                                    DataQuery.this.loading = false;
                                    DataQuery.this.loaded = true;
                                    DataQuery.this.hints = arrayList;
                                    DataQuery.this.inlineBots = arrayList2;
                                    DataQuery.this.buildShortcuts();
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    if (Math.abs(UserConfig.getInstance(DataQuery.this.currentAccount).lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                                        DataQuery.this.loadHints(false);
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                this.loaded = true;
            }
        }
    }

    public void increaseInlineRaiting(int i) {
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
        Collections.sort(this.inlineBots, new Comparator<TL_topPeer>() {
            public int compare(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
                if (tL_topPeer.rating > tL_topPeer2.rating) {
                    return -1;
                }
                return tL_topPeer.rating < tL_topPeer2.rating ? 1 : null;
            }
        });
        if (this.inlineBots.size() > 20) {
            this.inlineBots.remove(this.inlineBots.size() - 1);
        }
        savePeer(i, 1, tL_topPeer.rating);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public void removeInline(int i) {
        for (int i2 = 0; i2 < this.inlineBots.size(); i2++) {
            if (((TL_topPeer) this.inlineBots.get(i2)).peer.user_id == i) {
                this.inlineBots.remove(i2);
                TLObject tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryBotsInline();
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resetTopPeerRating, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
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
                TLObject tL_contacts_resetTopPeerRating = new TL_contacts_resetTopPeerRating();
                tL_contacts_resetTopPeerRating.category = new TL_topPeerCategoryCorrespondents();
                tL_contacts_resetTopPeerRating.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                deletePeer(i, 0);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_resetTopPeerRating, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }
                });
                return;
            }
        }
    }

    public void increasePeerRaiting(final long j) {
        final int i = (int) j;
        if (i > 0) {
            User user = i > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)) : null;
            if (user != null) {
                if (!user.bot) {
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                        public void run() {
                            double d = 0.0d;
                            try {
                                int intValue;
                                SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                                Object[] objArr = new Object[1];
                                int i = 0;
                                objArr[0] = Long.valueOf(j);
                                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", objArr), new Object[0]);
                                if (queryFinalized.next()) {
                                    i = queryFinalized.intValue(0);
                                    intValue = queryFinalized.intValue(1);
                                } else {
                                    intValue = 0;
                                }
                                queryFinalized.dispose();
                                if (i > 0 && UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime != 0) {
                                    d = (double) (intValue - UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime);
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.DataQuery$48$1$1 */
                                class C01301 implements Comparator<TL_topPeer> {
                                    C01301() {
                                    }

                                    public int compare(TL_topPeer tL_topPeer, TL_topPeer tL_topPeer2) {
                                        if (tL_topPeer.rating > tL_topPeer2.rating) {
                                            return -1;
                                        }
                                        return tL_topPeer.rating < tL_topPeer2.rating ? 1 : null;
                                    }
                                }

                                public void run() {
                                    TL_topPeer tL_topPeer;
                                    int i = 0;
                                    while (i < DataQuery.this.hints.size()) {
                                        tL_topPeer = (TL_topPeer) DataQuery.this.hints.get(i);
                                        if (i >= 0 || (tL_topPeer.peer.chat_id != (-i) && tL_topPeer.peer.channel_id != (-i))) {
                                            if (i > 0 && tL_topPeer.peer.user_id == i) {
                                                break;
                                            }
                                            i++;
                                        } else {
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
                                        DataQuery.this.hints.add(tL_topPeer);
                                    }
                                    tL_topPeer.rating += Math.exp(d / ((double) MessagesController.getInstance(DataQuery.this.currentAccount).ratingDecay));
                                    Collections.sort(DataQuery.this.hints, new C01301());
                                    DataQuery.this.savePeer((int) j, 0, tL_topPeer.rating);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void savePeer(int i, int i2, double d) {
        final int i3 = i;
        final int i4 = i2;
        final double d2 = d;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                    executeFast.requery();
                    executeFast.bindInteger(1, i3);
                    executeFast.bindInteger(2, i4);
                    executeFast.bindDouble(3, d2);
                    executeFast.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
                    executeFast.step();
                    executeFast.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void deletePeer(final int i, final int i2) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
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

    public void installShortcut(long r18) {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r17 = this;
        r1 = r17;
        r2 = r18;
        r4 = r17.createIntrnalShortcutIntent(r18);	 Catch:{ Exception -> 0x0256 }
        r5 = (int) r2;	 Catch:{ Exception -> 0x0256 }
        r6 = 32;	 Catch:{ Exception -> 0x0256 }
        r6 = r2 >> r6;	 Catch:{ Exception -> 0x0256 }
        r6 = (int) r6;	 Catch:{ Exception -> 0x0256 }
        if (r5 != 0) goto L_0x0032;	 Catch:{ Exception -> 0x0256 }
    L_0x0010:
        r5 = r1.currentAccount;	 Catch:{ Exception -> 0x0256 }
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);	 Catch:{ Exception -> 0x0256 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0256 }
        r5 = r5.getEncryptedChat(r6);	 Catch:{ Exception -> 0x0256 }
        if (r5 != 0) goto L_0x0021;	 Catch:{ Exception -> 0x0256 }
    L_0x0020:
        return;	 Catch:{ Exception -> 0x0256 }
    L_0x0021:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0256 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0256 }
        r5 = r5.user_id;	 Catch:{ Exception -> 0x0256 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0256 }
        r5 = r6.getUser(r5);	 Catch:{ Exception -> 0x0256 }
        goto L_0x0042;	 Catch:{ Exception -> 0x0256 }
    L_0x0032:
        if (r5 <= 0) goto L_0x0044;	 Catch:{ Exception -> 0x0256 }
    L_0x0034:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0256 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0256 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0256 }
        r5 = r6.getUser(r5);	 Catch:{ Exception -> 0x0256 }
    L_0x0042:
        r6 = 0;	 Catch:{ Exception -> 0x0256 }
        goto L_0x0057;	 Catch:{ Exception -> 0x0256 }
    L_0x0044:
        if (r5 >= 0) goto L_0x0255;	 Catch:{ Exception -> 0x0256 }
    L_0x0046:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0256 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0256 }
        r5 = -r5;	 Catch:{ Exception -> 0x0256 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x0256 }
        r5 = r6.getChat(r5);	 Catch:{ Exception -> 0x0256 }
        r6 = r5;	 Catch:{ Exception -> 0x0256 }
        r5 = 0;	 Catch:{ Exception -> 0x0256 }
    L_0x0057:
        if (r5 != 0) goto L_0x005c;	 Catch:{ Exception -> 0x0256 }
    L_0x0059:
        if (r6 != 0) goto L_0x005c;	 Catch:{ Exception -> 0x0256 }
    L_0x005b:
        return;	 Catch:{ Exception -> 0x0256 }
    L_0x005c:
        r8 = 1;	 Catch:{ Exception -> 0x0256 }
        r9 = 0;	 Catch:{ Exception -> 0x0256 }
        if (r5 == 0) goto L_0x0084;	 Catch:{ Exception -> 0x0256 }
    L_0x0060:
        r10 = org.telegram.messenger.UserObject.isUserSelf(r5);	 Catch:{ Exception -> 0x0256 }
        if (r10 == 0) goto L_0x0073;	 Catch:{ Exception -> 0x0256 }
    L_0x0066:
        r10 = "SavedMessages";	 Catch:{ Exception -> 0x0256 }
        r11 = NUM; // 0x7f0c0595 float:1.861209E38 double:1.0530981045E-314;	 Catch:{ Exception -> 0x0256 }
        r10 = org.telegram.messenger.LocaleController.getString(r10, r11);	 Catch:{ Exception -> 0x0256 }
        r12 = r10;	 Catch:{ Exception -> 0x0256 }
        r11 = 0;	 Catch:{ Exception -> 0x0256 }
        r10 = r8;	 Catch:{ Exception -> 0x0256 }
        goto L_0x0093;	 Catch:{ Exception -> 0x0256 }
    L_0x0073:
        r10 = r5.first_name;	 Catch:{ Exception -> 0x0256 }
        r11 = r5.last_name;	 Catch:{ Exception -> 0x0256 }
        r10 = org.telegram.messenger.ContactsController.formatName(r10, r11);	 Catch:{ Exception -> 0x0256 }
        r11 = r5.photo;	 Catch:{ Exception -> 0x0256 }
        if (r11 == 0) goto L_0x0090;	 Catch:{ Exception -> 0x0256 }
    L_0x007f:
        r11 = r5.photo;	 Catch:{ Exception -> 0x0256 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0256 }
        goto L_0x008e;	 Catch:{ Exception -> 0x0256 }
    L_0x0084:
        r10 = r6.title;	 Catch:{ Exception -> 0x0256 }
        r11 = r6.photo;	 Catch:{ Exception -> 0x0256 }
        if (r11 == 0) goto L_0x0090;	 Catch:{ Exception -> 0x0256 }
    L_0x008a:
        r11 = r6.photo;	 Catch:{ Exception -> 0x0256 }
        r11 = r11.photo_small;	 Catch:{ Exception -> 0x0256 }
    L_0x008e:
        r12 = r10;
        goto L_0x0092;
    L_0x0090:
        r12 = r10;
        r11 = 0;
    L_0x0092:
        r10 = r9;
    L_0x0093:
        if (r10 != 0) goto L_0x009b;
    L_0x0095:
        if (r11 == 0) goto L_0x0098;
    L_0x0097:
        goto L_0x009b;
    L_0x0098:
        r7 = 0;
        goto L_0x0160;
    L_0x009b:
        if (r10 != 0) goto L_0x00af;
    L_0x009d:
        r11 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r8);	 Catch:{ Throwable -> 0x00aa }
        r11 = r11.toString();	 Catch:{ Throwable -> 0x00aa }
        r11 = android.graphics.BitmapFactory.decodeFile(r11);	 Catch:{ Throwable -> 0x00aa }
        goto L_0x00b0;
    L_0x00aa:
        r0 = move-exception;
        r7 = r0;
        r11 = 0;
        goto L_0x015c;
    L_0x00af:
        r11 = 0;
    L_0x00b0:
        if (r10 != 0) goto L_0x00b4;
    L_0x00b2:
        if (r11 == 0) goto L_0x015f;
    L_0x00b4:
        r13 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Throwable -> 0x015a }
        r14 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x015a }
        r14 = android.graphics.Bitmap.createBitmap(r13, r13, r14);	 Catch:{ Throwable -> 0x015a }
        r14.eraseColor(r9);	 Catch:{ Throwable -> 0x015a }
        r15 = new android.graphics.Canvas;	 Catch:{ Throwable -> 0x015a }
        r15.<init>(r14);	 Catch:{ Throwable -> 0x015a }
        if (r10 == 0) goto L_0x00d9;	 Catch:{ Throwable -> 0x015a }
    L_0x00ca:
        r10 = new org.telegram.ui.Components.AvatarDrawable;	 Catch:{ Throwable -> 0x015a }
        r10.<init>(r5);	 Catch:{ Throwable -> 0x015a }
        r10.setSavedMessages(r8);	 Catch:{ Throwable -> 0x015a }
        r10.setBounds(r9, r9, r13, r13);	 Catch:{ Throwable -> 0x015a }
        r10.draw(r15);	 Catch:{ Throwable -> 0x015a }
        goto L_0x012a;	 Catch:{ Throwable -> 0x015a }
    L_0x00d9:
        r10 = new android.graphics.BitmapShader;	 Catch:{ Throwable -> 0x015a }
        r9 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x015a }
        r7 = android.graphics.Shader.TileMode.CLAMP;	 Catch:{ Throwable -> 0x015a }
        r10.<init>(r11, r9, r7);	 Catch:{ Throwable -> 0x015a }
        r7 = roundPaint;	 Catch:{ Throwable -> 0x015a }
        if (r7 != 0) goto L_0x00f4;	 Catch:{ Throwable -> 0x015a }
    L_0x00e6:
        r7 = new android.graphics.Paint;	 Catch:{ Throwable -> 0x015a }
        r7.<init>(r8);	 Catch:{ Throwable -> 0x015a }
        roundPaint = r7;	 Catch:{ Throwable -> 0x015a }
        r7 = new android.graphics.RectF;	 Catch:{ Throwable -> 0x015a }
        r7.<init>();	 Catch:{ Throwable -> 0x015a }
        bitmapRect = r7;	 Catch:{ Throwable -> 0x015a }
    L_0x00f4:
        r7 = (float) r13;	 Catch:{ Throwable -> 0x015a }
        r8 = r11.getWidth();	 Catch:{ Throwable -> 0x015a }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x015a }
        r7 = r7 / r8;	 Catch:{ Throwable -> 0x015a }
        r15.save();	 Catch:{ Throwable -> 0x015a }
        r15.scale(r7, r7);	 Catch:{ Throwable -> 0x015a }
        r7 = roundPaint;	 Catch:{ Throwable -> 0x015a }
        r7.setShader(r10);	 Catch:{ Throwable -> 0x015a }
        r7 = bitmapRect;	 Catch:{ Throwable -> 0x015a }
        r8 = r11.getWidth();	 Catch:{ Throwable -> 0x015a }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x015a }
        r9 = r11.getHeight();	 Catch:{ Throwable -> 0x015a }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x015a }
        r10 = 0;	 Catch:{ Throwable -> 0x015a }
        r7.set(r10, r10, r8, r9);	 Catch:{ Throwable -> 0x015a }
        r7 = bitmapRect;	 Catch:{ Throwable -> 0x015a }
        r8 = r11.getWidth();	 Catch:{ Throwable -> 0x015a }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x015a }
        r9 = r11.getHeight();	 Catch:{ Throwable -> 0x015a }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x015a }
        r10 = roundPaint;	 Catch:{ Throwable -> 0x015a }
        r15.drawRoundRect(r7, r8, r9, r10);	 Catch:{ Throwable -> 0x015a }
        r15.restore();	 Catch:{ Throwable -> 0x015a }
    L_0x012a:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x015a }
        r7 = r7.getResources();	 Catch:{ Throwable -> 0x015a }
        r8 = NUM; // 0x7f070038 float:1.7944692E38 double:1.0529355307E-314;	 Catch:{ Throwable -> 0x015a }
        r7 = r7.getDrawable(r8);	 Catch:{ Throwable -> 0x015a }
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;	 Catch:{ Throwable -> 0x015a }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Throwable -> 0x015a }
        r13 = r13 - r8;	 Catch:{ Throwable -> 0x015a }
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Throwable -> 0x015a }
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x015a }
        r10 = r13 - r10;	 Catch:{ Throwable -> 0x015a }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x015a }
        r13 = r13 - r9;	 Catch:{ Throwable -> 0x015a }
        r9 = r10 + r8;	 Catch:{ Throwable -> 0x015a }
        r8 = r8 + r13;	 Catch:{ Throwable -> 0x015a }
        r7.setBounds(r10, r13, r9, r8);	 Catch:{ Throwable -> 0x015a }
        r7.draw(r15);	 Catch:{ Throwable -> 0x015a }
        r7 = 0;
        r15.setBitmap(r7);	 Catch:{ Exception -> 0x0158 }
    L_0x0158:
        r7 = r14;
        goto L_0x0160;
    L_0x015a:
        r0 = move-exception;
        r7 = r0;
    L_0x015c:
        org.telegram.messenger.FileLog.m3e(r7);	 Catch:{ Exception -> 0x0256 }
    L_0x015f:
        r7 = r11;	 Catch:{ Exception -> 0x0256 }
    L_0x0160:
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0256 }
        r9 = 26;	 Catch:{ Exception -> 0x0256 }
        r11 = NUM; // 0x7f070036 float:1.7944687E38 double:1.0529355297E-314;	 Catch:{ Exception -> 0x0256 }
        r13 = NUM; // 0x7f070039 float:1.7944694E38 double:1.052935531E-314;	 Catch:{ Exception -> 0x0256 }
        r14 = NUM; // 0x7f070035 float:1.7944685E38 double:1.052935529E-314;	 Catch:{ Exception -> 0x0256 }
        if (r8 < r9) goto L_0x01e9;	 Catch:{ Exception -> 0x0256 }
    L_0x016f:
        r8 = new android.content.pm.ShortcutInfo$Builder;	 Catch:{ Exception -> 0x0256 }
        r9 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0256 }
        r15.<init>();	 Catch:{ Exception -> 0x0256 }
        r10 = "sdid_";	 Catch:{ Exception -> 0x0256 }
        r15.append(r10);	 Catch:{ Exception -> 0x0256 }
        r15.append(r2);	 Catch:{ Exception -> 0x0256 }
        r2 = r15.toString();	 Catch:{ Exception -> 0x0256 }
        r8.<init>(r9, r2);	 Catch:{ Exception -> 0x0256 }
        r2 = r8.setShortLabel(r12);	 Catch:{ Exception -> 0x0256 }
        r2 = r2.setIntent(r4);	 Catch:{ Exception -> 0x0256 }
        if (r7 == 0) goto L_0x0199;	 Catch:{ Exception -> 0x0256 }
    L_0x0191:
        r3 = android.graphics.drawable.Icon.createWithBitmap(r7);	 Catch:{ Exception -> 0x0256 }
        r2.setIcon(r3);	 Catch:{ Exception -> 0x0256 }
        goto L_0x01d5;	 Catch:{ Exception -> 0x0256 }
    L_0x0199:
        if (r5 == 0) goto L_0x01b3;	 Catch:{ Exception -> 0x0256 }
    L_0x019b:
        r3 = r5.bot;	 Catch:{ Exception -> 0x0256 }
        if (r3 == 0) goto L_0x01a9;	 Catch:{ Exception -> 0x0256 }
    L_0x019f:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r3 = android.graphics.drawable.Icon.createWithResource(r3, r14);	 Catch:{ Exception -> 0x0256 }
        r2.setIcon(r3);	 Catch:{ Exception -> 0x0256 }
        goto L_0x01d5;	 Catch:{ Exception -> 0x0256 }
    L_0x01a9:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r3 = android.graphics.drawable.Icon.createWithResource(r3, r13);	 Catch:{ Exception -> 0x0256 }
        r2.setIcon(r3);	 Catch:{ Exception -> 0x0256 }
        goto L_0x01d5;	 Catch:{ Exception -> 0x0256 }
    L_0x01b3:
        if (r6 == 0) goto L_0x01d5;	 Catch:{ Exception -> 0x0256 }
    L_0x01b5:
        r3 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x0256 }
        if (r3 == 0) goto L_0x01c9;	 Catch:{ Exception -> 0x0256 }
    L_0x01bb:
        r3 = r6.megagroup;	 Catch:{ Exception -> 0x0256 }
        if (r3 != 0) goto L_0x01c9;	 Catch:{ Exception -> 0x0256 }
    L_0x01bf:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r3 = android.graphics.drawable.Icon.createWithResource(r3, r11);	 Catch:{ Exception -> 0x0256 }
        r2.setIcon(r3);	 Catch:{ Exception -> 0x0256 }
        goto L_0x01d5;	 Catch:{ Exception -> 0x0256 }
    L_0x01c9:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r4 = NUM; // 0x7f070037 float:1.794469E38 double:1.05293553E-314;	 Catch:{ Exception -> 0x0256 }
        r3 = android.graphics.drawable.Icon.createWithResource(r3, r4);	 Catch:{ Exception -> 0x0256 }
        r2.setIcon(r3);	 Catch:{ Exception -> 0x0256 }
    L_0x01d5:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r4 = android.content.pm.ShortcutManager.class;	 Catch:{ Exception -> 0x0256 }
        r3 = r3.getSystemService(r4);	 Catch:{ Exception -> 0x0256 }
        r3 = (android.content.pm.ShortcutManager) r3;	 Catch:{ Exception -> 0x0256 }
        r2 = r2.build();	 Catch:{ Exception -> 0x0256 }
        r4 = 0;	 Catch:{ Exception -> 0x0256 }
        r3.requestPinShortcut(r2, r4);	 Catch:{ Exception -> 0x0256 }
        goto L_0x025b;	 Catch:{ Exception -> 0x0256 }
    L_0x01e9:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0256 }
        r2.<init>();	 Catch:{ Exception -> 0x0256 }
        if (r7 == 0) goto L_0x01f6;	 Catch:{ Exception -> 0x0256 }
    L_0x01f0:
        r3 = "android.intent.extra.shortcut.ICON";	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r7);	 Catch:{ Exception -> 0x0256 }
        goto L_0x023a;	 Catch:{ Exception -> 0x0256 }
    L_0x01f6:
        if (r5 == 0) goto L_0x0214;	 Catch:{ Exception -> 0x0256 }
    L_0x01f8:
        r3 = r5.bot;	 Catch:{ Exception -> 0x0256 }
        if (r3 == 0) goto L_0x0208;	 Catch:{ Exception -> 0x0256 }
    L_0x01fc:
        r3 = "android.intent.extra.shortcut.ICON_RESOURCE";	 Catch:{ Exception -> 0x0256 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r5 = android.content.Intent.ShortcutIconResource.fromContext(r5, r14);	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0256 }
        goto L_0x023a;	 Catch:{ Exception -> 0x0256 }
    L_0x0208:
        r3 = "android.intent.extra.shortcut.ICON_RESOURCE";	 Catch:{ Exception -> 0x0256 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r5 = android.content.Intent.ShortcutIconResource.fromContext(r5, r13);	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0256 }
        goto L_0x023a;	 Catch:{ Exception -> 0x0256 }
    L_0x0214:
        if (r6 == 0) goto L_0x023a;	 Catch:{ Exception -> 0x0256 }
    L_0x0216:
        r3 = org.telegram.messenger.ChatObject.isChannel(r6);	 Catch:{ Exception -> 0x0256 }
        if (r3 == 0) goto L_0x022c;	 Catch:{ Exception -> 0x0256 }
    L_0x021c:
        r3 = r6.megagroup;	 Catch:{ Exception -> 0x0256 }
        if (r3 != 0) goto L_0x022c;	 Catch:{ Exception -> 0x0256 }
    L_0x0220:
        r3 = "android.intent.extra.shortcut.ICON_RESOURCE";	 Catch:{ Exception -> 0x0256 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r5 = android.content.Intent.ShortcutIconResource.fromContext(r5, r11);	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0256 }
        goto L_0x023a;	 Catch:{ Exception -> 0x0256 }
    L_0x022c:
        r3 = "android.intent.extra.shortcut.ICON_RESOURCE";	 Catch:{ Exception -> 0x0256 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r6 = NUM; // 0x7f070037 float:1.794469E38 double:1.05293553E-314;	 Catch:{ Exception -> 0x0256 }
        r5 = android.content.Intent.ShortcutIconResource.fromContext(r5, r6);	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0256 }
    L_0x023a:
        r3 = "android.intent.extra.shortcut.INTENT";	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r4);	 Catch:{ Exception -> 0x0256 }
        r3 = "android.intent.extra.shortcut.NAME";	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r12);	 Catch:{ Exception -> 0x0256 }
        r3 = "duplicate";	 Catch:{ Exception -> 0x0256 }
        r4 = 0;	 Catch:{ Exception -> 0x0256 }
        r2.putExtra(r3, r4);	 Catch:{ Exception -> 0x0256 }
        r3 = "com.android.launcher.action.INSTALL_SHORTCUT";	 Catch:{ Exception -> 0x0256 }
        r2.setAction(r3);	 Catch:{ Exception -> 0x0256 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0256 }
        r3.sendBroadcast(r2);	 Catch:{ Exception -> 0x0256 }
        goto L_0x025b;
    L_0x0255:
        return;
    L_0x0256:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
    L_0x025b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DataQuery.installShortcut(long):void");
    }

    public void uninstallShortcut(long j) {
        try {
            if (VERSION.SDK_INT >= 26) {
                ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                List arrayList = new ArrayList();
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public MessageObject loadPinnedMessage(final int i, final int i2, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(i, i2, true);
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                DataQuery.this.loadPinnedMessageInternal(i, i2, false);
            }
        });
        return 0;
    }

    private MessageObject loadPinnedMessageInternal(int i, int i2, boolean z) {
        DataQuery dataQuery = this;
        final int i3 = i;
        int i4 = i2;
        long j = ((long) i4) | (((long) i3) << 32);
        try {
            AbstractSerializedData byteBufferValue;
            Message message;
            TLObject tL_channels_getMessages;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterable arrayList3 = new ArrayList();
            Iterable arrayList4 = new ArrayList();
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(dataQuery.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(dataQuery.currentAccount).clientUserId);
                    byteBufferValue.reuse();
                    if (!(TLdeserialize.action instanceof TL_messageActionHistoryClear)) {
                        TLdeserialize.id = queryFinalized.intValue(1);
                        TLdeserialize.date = queryFinalized.intValue(2);
                        TLdeserialize.dialog_id = (long) (-i3);
                        MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList3, arrayList4);
                        message = TLdeserialize;
                        queryFinalized.dispose();
                        if (message == null) {
                            queryFinalized = MessagesStorage.getInstance(dataQuery.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                            if (queryFinalized.next()) {
                                byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    message.readAttachPath(byteBufferValue, UserConfig.getInstance(dataQuery.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    if (message.id == i4) {
                                        if (message.action instanceof TL_messageActionHistoryClear) {
                                            message.dialog_id = (long) (-i3);
                                            MessagesStorage.addUsersAndChatsFromMessage(message, arrayList3, arrayList4);
                                        }
                                    }
                                    message = null;
                                }
                            }
                            queryFinalized.dispose();
                        }
                        if (message == null) {
                            tL_channels_getMessages = new TL_channels_getMessages();
                            tL_channels_getMessages.channel = MessagesController.getInstance(dataQuery.currentAccount).getInputChannel(i3);
                            tL_channels_getMessages.id.add(Integer.valueOf(i2));
                            ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                    boolean z = true;
                                    if (tL_error == null) {
                                        messages_Messages messages_messages = (messages_Messages) tLObject;
                                        DataQuery.removeEmptyMessages(messages_messages.messages);
                                        if (messages_messages.messages.isEmpty() == null) {
                                            ImageLoader.saveMessagesThumbs(messages_messages.messages);
                                            DataQuery.this.broadcastPinnedMessage((Message) messages_messages.messages.get(0), messages_messages.users, messages_messages.chats, false, false);
                                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                            DataQuery.this.savePinnedMessage((Message) messages_messages.messages.get(0));
                                            if (!z) {
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).updateChannelPinnedMessage(i3, 0);
                                            }
                                        }
                                    }
                                    z = false;
                                    if (!z) {
                                        MessagesStorage.getInstance(DataQuery.this.currentAccount).updateChannelPinnedMessage(i3, 0);
                                    }
                                }
                            });
                        } else if (z) {
                            return broadcastPinnedMessage(message, arrayList, arrayList2, true, z);
                        } else {
                            if (!arrayList3.isEmpty()) {
                                MessagesStorage.getInstance(dataQuery.currentAccount).getUsersInternal(TextUtils.join(",", arrayList3), arrayList);
                            }
                            if (!arrayList4.isEmpty()) {
                                MessagesStorage.getInstance(dataQuery.currentAccount).getChatsInternal(TextUtils.join(",", arrayList4), arrayList2);
                            }
                            broadcastPinnedMessage(message, arrayList, arrayList2, true, false);
                        }
                        return null;
                    }
                }
            }
            message = null;
            queryFinalized.dispose();
            if (message == null) {
                queryFinalized = MessagesStorage.getInstance(dataQuery.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized.next()) {
                    byteBufferValue = queryFinalized.byteBufferValue(0);
                    if (byteBufferValue != null) {
                        message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                        message.readAttachPath(byteBufferValue, UserConfig.getInstance(dataQuery.currentAccount).clientUserId);
                        byteBufferValue.reuse();
                        if (message.id == i4) {
                            if (message.action instanceof TL_messageActionHistoryClear) {
                                message.dialog_id = (long) (-i3);
                                MessagesStorage.addUsersAndChatsFromMessage(message, arrayList3, arrayList4);
                            }
                        }
                        message = null;
                    }
                }
                queryFinalized.dispose();
            }
            if (message == null) {
                tL_channels_getMessages = new TL_channels_getMessages();
                tL_channels_getMessages.channel = MessagesController.getInstance(dataQuery.currentAccount).getInputChannel(i3);
                tL_channels_getMessages.id.add(Integer.valueOf(i2));
                ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(tL_channels_getMessages, /* anonymous class already generated */);
            } else if (z) {
                return broadcastPinnedMessage(message, arrayList, arrayList2, true, z);
            } else {
                if (arrayList3.isEmpty()) {
                    MessagesStorage.getInstance(dataQuery.currentAccount).getUsersInternal(TextUtils.join(",", arrayList3), arrayList);
                }
                if (arrayList4.isEmpty()) {
                    MessagesStorage.getInstance(dataQuery.currentAccount).getChatsInternal(TextUtils.join(",", arrayList4), arrayList2);
                }
                broadcastPinnedMessage(message, arrayList, arrayList2, true, false);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return null;
    }

    private void savePinnedMessage(final Message message) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(nativeByteBuffer);
                    executeFast.requery();
                    executeFast.bindInteger(1, message.to_id.channel_id);
                    executeFast.bindInteger(2, message.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private MessageObject broadcastPinnedMessage(Message message, ArrayList<User> arrayList, ArrayList<Chat> arrayList2, boolean z, boolean z2) {
        final SparseArray sparseArray = new SparseArray();
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            User user = (User) arrayList.get(i2);
            sparseArray.put(user.id, user);
        }
        final SparseArray sparseArray2 = new SparseArray();
        while (i < arrayList2.size()) {
            Chat chat = (Chat) arrayList2.get(i);
            sparseArray2.put(chat.id, chat);
            i++;
        }
        if (z2) {
            return new MessageObject(this.currentAccount, message, sparseArray, sparseArray2, false);
        }
        final ArrayList<User> arrayList3 = arrayList;
        final boolean z3 = z;
        final ArrayList<Chat> arrayList4 = arrayList2;
        final Message message2 = message;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList3, z3);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList4, z3);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedPinnedMessage, new MessageObject(DataQuery.this.currentAccount, message2, sparseArray, sparseArray2, false));
            }
        });
        return null;
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
        DataQuery dataQuery = this;
        ArrayList<MessageObject> arrayList2 = arrayList;
        final long j2 = j;
        int i = 0;
        ArrayList arrayList3;
        MessageObject messageObject;
        if (((int) j2) == 0) {
            arrayList3 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            while (i < arrayList.size()) {
                messageObject = (MessageObject) arrayList2.get(i);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long j3 = messageObject.messageOwner.reply_to_random_id;
                    ArrayList arrayList4 = (ArrayList) longSparseArray.get(j3);
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList();
                        longSparseArray.put(j3, arrayList4);
                    }
                    arrayList4.add(messageObject);
                    if (!arrayList3.contains(Long.valueOf(j3))) {
                        arrayList3.add(Long.valueOf(j3));
                    }
                }
                i++;
            }
            if (!arrayList3.isEmpty()) {
                final ArrayList arrayList5 = arrayList3;
                final LongSparseArray longSparseArray2 = longSparseArray;
                MessagesStorage.getInstance(dataQuery.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.DataQuery$56$1 */
                    class C01331 implements Runnable {
                        C01331() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(j2));
                        }
                    }

                    public void run() {
                        try {
                            ArrayList arrayList;
                            int i;
                            SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList5)}), new Object[0]);
                            while (queryFinalized.next()) {
                                AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                if (byteBufferValue != null) {
                                    Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                    TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                    byteBufferValue.reuse();
                                    TLdeserialize.id = queryFinalized.intValue(1);
                                    TLdeserialize.date = queryFinalized.intValue(2);
                                    TLdeserialize.dialog_id = j2;
                                    long longValue = queryFinalized.longValue(3);
                                    arrayList = (ArrayList) longSparseArray2.get(longValue);
                                    longSparseArray2.remove(longValue);
                                    if (arrayList != null) {
                                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, TLdeserialize, false);
                                        for (i = 0; i < arrayList.size(); i++) {
                                            MessageObject messageObject2 = (MessageObject) arrayList.get(i);
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
                            if (longSparseArray2.size() != 0) {
                                for (int i2 = 0; i2 < longSparseArray2.size(); i2++) {
                                    arrayList = (ArrayList) longSparseArray2.valueAt(i2);
                                    for (i = 0; i < arrayList.size(); i++) {
                                        ((MessageObject) arrayList.get(i)).messageOwner.reply_to_random_id = 0;
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new C01331());
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            } else {
                return;
            }
        }
        arrayList3 = new ArrayList();
        final SparseArray sparseArray = new SparseArray();
        StringBuilder stringBuilder = new StringBuilder();
        int i2 = 0;
        while (i < arrayList.size()) {
            messageObject = (MessageObject) arrayList2.get(i);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                int i3 = messageObject.messageOwner.reply_to_msg_id;
                long j4 = (long) i3;
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    long j5 = j4 | (((long) messageObject.messageOwner.to_id.channel_id) << 32);
                    i2 = messageObject.messageOwner.to_id.channel_id;
                    j4 = j5;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(j4);
                ArrayList arrayList6 = (ArrayList) sparseArray.get(i3);
                if (arrayList6 == null) {
                    arrayList6 = new ArrayList();
                    sparseArray.put(i3, arrayList6);
                }
                arrayList6.add(messageObject);
                if (!arrayList3.contains(Integer.valueOf(i3))) {
                    arrayList3.add(Integer.valueOf(i3));
                }
            }
            i++;
        }
        if (!arrayList3.isEmpty()) {
            final StringBuilder stringBuilder2 = stringBuilder;
            final int i4 = i2;
            MessagesStorage.getInstance(dataQuery.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.DataQuery$57$1 */
                class C17941 implements RequestDelegate {
                    C17941() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            messages_Messages messages_messages = (messages_Messages) tLObject;
                            DataQuery.removeEmptyMessages(messages_messages.messages);
                            ImageLoader.saveMessagesThumbs(messages_messages.messages);
                            DataQuery.this.broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j2, false);
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                            DataQuery.this.saveReplyMessages(sparseArray, messages_messages.messages);
                        }
                    }
                }

                /* renamed from: org.telegram.messenger.DataQuery$57$2 */
                class C17952 implements RequestDelegate {
                    C17952() {
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            messages_Messages messages_messages = (messages_Messages) tLObject;
                            DataQuery.removeEmptyMessages(messages_messages.messages);
                            ImageLoader.saveMessagesThumbs(messages_messages.messages);
                            DataQuery.this.broadcastReplyMessages(messages_messages.messages, sparseArray, messages_messages.users, messages_messages.chats, j2, false);
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                            DataQuery.this.saveReplyMessages(sparseArray, messages_messages.messages);
                        }
                    }
                }

                public void run() {
                    try {
                        ArrayList arrayList = new ArrayList();
                        ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        Iterable arrayList4 = new ArrayList();
                        Iterable arrayList5 = new ArrayList();
                        SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder2.toString()}), new Object[0]);
                        while (queryFinalized.next()) {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                TLdeserialize.readAttachPath(byteBufferValue, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                byteBufferValue.reuse();
                                TLdeserialize.id = queryFinalized.intValue(1);
                                TLdeserialize.date = queryFinalized.intValue(2);
                                TLdeserialize.dialog_id = j2;
                                MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5);
                                arrayList.add(TLdeserialize);
                                arrayList3.remove(Integer.valueOf(TLdeserialize.id));
                            }
                        }
                        queryFinalized.dispose();
                        if (!arrayList4.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
                        }
                        if (!arrayList5.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                        }
                        DataQuery.this.broadcastReplyMessages(arrayList, sparseArray, arrayList2, arrayList3, j2, true);
                        if (!arrayList3.isEmpty()) {
                            TLObject tL_channels_getMessages;
                            if (i4 != 0) {
                                tL_channels_getMessages = new TL_channels_getMessages();
                                tL_channels_getMessages.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(i4);
                                tL_channels_getMessages.id = arrayList3;
                                ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(tL_channels_getMessages, new C17941());
                                return;
                            }
                            tL_channels_getMessages = new TL_messages_getMessages();
                            tL_channels_getMessages.id = arrayList3;
                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(tL_channels_getMessages, new C17952());
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private void saveReplyMessages(final SparseArray<ArrayList<MessageObject>> sparseArray, final ArrayList<Message> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
                    for (int i = 0; i < arrayList.size(); i++) {
                        Message message = (Message) arrayList.get(i);
                        ArrayList arrayList = (ArrayList) sparseArray.get(message.id);
                        if (arrayList != null) {
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(nativeByteBuffer);
                            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                                MessageObject messageObject = (MessageObject) arrayList.get(i2);
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
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void broadcastReplyMessages(ArrayList<Message> arrayList, SparseArray<ArrayList<MessageObject>> sparseArray, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, long j, boolean z) {
        final SparseArray sparseArray2 = new SparseArray();
        int i = 0;
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            User user = (User) arrayList2.get(i2);
            sparseArray2.put(user.id, user);
        }
        final ArrayList<User> arrayList4 = arrayList2;
        final SparseArray sparseArray3 = new SparseArray();
        while (i < arrayList3.size()) {
            Chat chat = (Chat) arrayList3.get(i);
            sparseArray3.put(chat.id, chat);
            i++;
        }
        final ArrayList<Chat> arrayList5 = arrayList3;
        final boolean z2 = z;
        final ArrayList<Message> arrayList6 = arrayList;
        final SparseArray<ArrayList<MessageObject>> sparseArray4 = sparseArray;
        final long j2 = j;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList4, z2);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList5, z2);
                int i = 0;
                int i2 = i;
                while (i < arrayList6.size()) {
                    Message message = (Message) arrayList6.get(i);
                    ArrayList arrayList = (ArrayList) sparseArray4.get(message.id);
                    if (arrayList != null) {
                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, message, sparseArray2, sparseArray3, false);
                        for (int i3 = 0; i3 < arrayList.size(); i3++) {
                            MessageObject messageObject2 = (MessageObject) arrayList.get(i3);
                            messageObject2.replyMessageObject = messageObject;
                            if (messageObject2.messageOwner.action instanceof TL_messageActionPinMessage) {
                                messageObject2.generatePinMessageText(null, null);
                            } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                messageObject2.generateGameMessageText(null);
                            } else if (messageObject2.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                messageObject2.generatePaymentSentMessageText(null);
                            }
                            if (messageObject2.isMegagroup()) {
                                Message message2 = messageObject2.replyMessageObject.messageOwner;
                                message2.flags |= Integer.MIN_VALUE;
                            }
                        }
                        i2 = 1;
                    }
                    i++;
                }
                if (i2 != 0) {
                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(j2));
                }
            }
        });
    }

    public static void sortEntities(ArrayList<MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }

    private static boolean checkInclusion(int i, ArrayList<MessageEntity> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    MessageEntity messageEntity = (MessageEntity) arrayList.get(i2);
                    if (messageEntity.offset <= i && messageEntity.offset + messageEntity.length > i) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int i, int i2, ArrayList<MessageEntity> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    MessageEntity messageEntity = (MessageEntity) arrayList.get(i3);
                    if (messageEntity.offset > i && messageEntity.offset + messageEntity.length <= i2) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static void removeOffsetAfter(int i, int i2, ArrayList<MessageEntity> arrayList) {
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            MessageEntity messageEntity = (MessageEntity) arrayList.get(i3);
            if (messageEntity.offset > i) {
                messageEntity.offset -= i2;
            }
        }
    }

    public ArrayList<MessageEntity> getEntities(CharSequence[] charSequenceArr) {
        DataQuery dataQuery;
        if (charSequenceArr != null) {
            if (charSequenceArr[0] != null) {
                char c;
                char c2;
                int i;
                int i2;
                CharSequence substring;
                TL_messageEntityCode tL_messageEntityCode;
                ArrayList<MessageEntity> arrayList;
                int i3 = -1;
                ArrayList arrayList2 = null;
                int i4 = 0;
                int i5 = i4;
                int i6 = -1;
                while (true) {
                    i5 = TextUtils.indexOf(charSequenceArr[0], i4 == 0 ? "`" : "```", i5);
                    c = '\n';
                    c2 = ' ';
                    if (i5 == i3) {
                        break;
                    } else if (i6 == i3) {
                        i4 = (charSequenceArr[0].length() - i5 > 2 && charSequenceArr[0].charAt(i5 + 1) == '`' && charSequenceArr[0].charAt(i5 + 2) == '`') ? 1 : 0;
                        i6 = i5;
                        i5 += i4 != 0 ? 3 : 1;
                    } else {
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                        }
                        int i7 = (i4 != 0 ? 3 : 1) + i5;
                        while (i7 < charSequenceArr[0].length() && charSequenceArr[0].charAt(i7) == '`') {
                            i5++;
                            i7++;
                        }
                        i = (i4 != 0 ? 3 : 1) + i5;
                        if (i4 != 0) {
                            CharSequence substring2;
                            CharSequence substring3;
                            char charAt;
                            CharSequence charSequence;
                            TL_messageEntityPre tL_messageEntityPre;
                            char charAt2 = i6 > 0 ? charSequenceArr[0].charAt(i6 - 1) : '\u0000';
                            if (charAt2 != ' ') {
                                if (charAt2 != '\n') {
                                    i4 = 0;
                                    substring2 = TextUtils.substring(charSequenceArr[0], 0, i6 - i4);
                                    substring3 = TextUtils.substring(charSequenceArr[0], i6 + 3, i5);
                                    i2 = i5 + 3;
                                    charAt = i2 >= charSequenceArr[0].length() ? charSequenceArr[0].charAt(i2) : '\u0000';
                                    charSequence = charSequenceArr[0];
                                    if (charAt != ' ') {
                                        if (charAt == '\n') {
                                            i3 = 0;
                                            substring = TextUtils.substring(charSequence, i2 + i3, charSequenceArr[0].length());
                                            if (substring2.length() == 0) {
                                                substring2 = TextUtils.concat(new CharSequence[]{substring2, "\n"});
                                            } else {
                                                i4 = 1;
                                            }
                                            if (substring.length() != 0) {
                                                substring = TextUtils.concat(new CharSequence[]{"\n", substring});
                                            }
                                            if (!TextUtils.isEmpty(substring3)) {
                                                charSequenceArr[0] = TextUtils.concat(new CharSequence[]{substring2, substring3, substring});
                                                tL_messageEntityPre = new TL_messageEntityPre();
                                                tL_messageEntityPre.offset = (i4 ^ 1) + i6;
                                                tL_messageEntityPre.length = ((i5 - i6) - 3) + (i4 ^ 1);
                                                tL_messageEntityPre.language = TtmlNode.ANONYMOUS_REGION_ID;
                                                arrayList2.add(tL_messageEntityPre);
                                                i -= 6;
                                            }
                                        }
                                    }
                                    i3 = 1;
                                    substring = TextUtils.substring(charSequence, i2 + i3, charSequenceArr[0].length());
                                    if (substring2.length() == 0) {
                                        i4 = 1;
                                    } else {
                                        substring2 = TextUtils.concat(new CharSequence[]{substring2, "\n"});
                                    }
                                    if (substring.length() != 0) {
                                        substring = TextUtils.concat(new CharSequence[]{"\n", substring});
                                    }
                                    if (TextUtils.isEmpty(substring3)) {
                                        charSequenceArr[0] = TextUtils.concat(new CharSequence[]{substring2, substring3, substring});
                                        tL_messageEntityPre = new TL_messageEntityPre();
                                        tL_messageEntityPre.offset = (i4 ^ 1) + i6;
                                        tL_messageEntityPre.length = ((i5 - i6) - 3) + (i4 ^ 1);
                                        tL_messageEntityPre.language = TtmlNode.ANONYMOUS_REGION_ID;
                                        arrayList2.add(tL_messageEntityPre);
                                        i -= 6;
                                    }
                                }
                            }
                            i4 = 1;
                            substring2 = TextUtils.substring(charSequenceArr[0], 0, i6 - i4);
                            substring3 = TextUtils.substring(charSequenceArr[0], i6 + 3, i5);
                            i2 = i5 + 3;
                            if (i2 >= charSequenceArr[0].length()) {
                            }
                            charSequence = charSequenceArr[0];
                            if (charAt != ' ') {
                                if (charAt == '\n') {
                                    i3 = 0;
                                    substring = TextUtils.substring(charSequence, i2 + i3, charSequenceArr[0].length());
                                    if (substring2.length() == 0) {
                                        substring2 = TextUtils.concat(new CharSequence[]{substring2, "\n"});
                                    } else {
                                        i4 = 1;
                                    }
                                    if (substring.length() != 0) {
                                        substring = TextUtils.concat(new CharSequence[]{"\n", substring});
                                    }
                                    if (TextUtils.isEmpty(substring3)) {
                                        charSequenceArr[0] = TextUtils.concat(new CharSequence[]{substring2, substring3, substring});
                                        tL_messageEntityPre = new TL_messageEntityPre();
                                        tL_messageEntityPre.offset = (i4 ^ 1) + i6;
                                        tL_messageEntityPre.length = ((i5 - i6) - 3) + (i4 ^ 1);
                                        tL_messageEntityPre.language = TtmlNode.ANONYMOUS_REGION_ID;
                                        arrayList2.add(tL_messageEntityPre);
                                        i -= 6;
                                    }
                                }
                            }
                            i3 = 1;
                            substring = TextUtils.substring(charSequence, i2 + i3, charSequenceArr[0].length());
                            if (substring2.length() == 0) {
                                i4 = 1;
                            } else {
                                substring2 = TextUtils.concat(new CharSequence[]{substring2, "\n"});
                            }
                            if (substring.length() != 0) {
                                substring = TextUtils.concat(new CharSequence[]{"\n", substring});
                            }
                            if (TextUtils.isEmpty(substring3)) {
                                charSequenceArr[0] = TextUtils.concat(new CharSequence[]{substring2, substring3, substring});
                                tL_messageEntityPre = new TL_messageEntityPre();
                                tL_messageEntityPre.offset = (i4 ^ 1) + i6;
                                tL_messageEntityPre.length = ((i5 - i6) - 3) + (i4 ^ 1);
                                tL_messageEntityPre.language = TtmlNode.ANONYMOUS_REGION_ID;
                                arrayList2.add(tL_messageEntityPre);
                                i -= 6;
                            }
                        } else {
                            if (i6 + 1 != i5) {
                                charSequenceArr[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(charSequenceArr[0], 0, i6), TextUtils.substring(charSequenceArr[0], i6 + 1, i5), TextUtils.substring(charSequenceArr[0], i5 + 1, charSequenceArr[0].length())});
                                tL_messageEntityCode = new TL_messageEntityCode();
                                tL_messageEntityCode.offset = i6;
                                tL_messageEntityCode.length = (i5 - i6) - 1;
                                arrayList2.add(tL_messageEntityCode);
                                i -= 2;
                            }
                        }
                        i5 = i;
                        i4 = 0;
                        i3 = -1;
                        i6 = -1;
                    }
                }
                if (!(i6 == i3 || i4 == 0)) {
                    charSequenceArr[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(charSequenceArr[0], 0, i6), TextUtils.substring(charSequenceArr[0], i6 + 2, charSequenceArr[0].length())});
                    if (arrayList2 == null) {
                        arrayList = new ArrayList();
                    }
                    tL_messageEntityCode = new TL_messageEntityCode();
                    tL_messageEntityCode.offset = i6;
                    tL_messageEntityCode.length = 1;
                    arrayList.add(tL_messageEntityCode);
                }
                if (charSequenceArr[0] instanceof Spannable) {
                    Spannable spannable = (Spannable) charSequenceArr[0];
                    TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) spannable.getSpans(0, charSequenceArr[0].length(), TypefaceSpan.class);
                    if (typefaceSpanArr != null && typefaceSpanArr.length > 0) {
                        for (TypefaceSpan typefaceSpan : typefaceSpanArr) {
                            int spanStart = spannable.getSpanStart(typefaceSpan);
                            i = spannable.getSpanEnd(typefaceSpan);
                            if (!(checkInclusion(spanStart, arrayList) || checkInclusion(i, arrayList))) {
                                if (!checkIntersection(spanStart, i, arrayList)) {
                                    MessageEntity tL_messageEntityBold;
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                    }
                                    if (typefaceSpan.isBold()) {
                                        tL_messageEntityBold = new TL_messageEntityBold();
                                    } else {
                                        tL_messageEntityBold = new TL_messageEntityItalic();
                                    }
                                    tL_messageEntityBold.offset = spanStart;
                                    tL_messageEntityBold.length = i - spanStart;
                                    arrayList.add(tL_messageEntityBold);
                                }
                            }
                        }
                    }
                    URLSpanUserMention[] uRLSpanUserMentionArr = (URLSpanUserMention[]) spannable.getSpans(0, charSequenceArr[0].length(), URLSpanUserMention.class);
                    if (uRLSpanUserMentionArr != null && uRLSpanUserMentionArr.length > 0) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        for (i5 = 0; i5 < uRLSpanUserMentionArr.length; i5++) {
                            TL_inputMessageEntityMentionName tL_inputMessageEntityMentionName = new TL_inputMessageEntityMentionName();
                            tL_inputMessageEntityMentionName.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(Utilities.parseInt(uRLSpanUserMentionArr[i5].getURL()).intValue());
                            if (tL_inputMessageEntityMentionName.user_id != null) {
                                tL_inputMessageEntityMentionName.offset = spannable.getSpanStart(uRLSpanUserMentionArr[i5]);
                                tL_inputMessageEntityMentionName.length = Math.min(spannable.getSpanEnd(uRLSpanUserMentionArr[i5]), charSequenceArr[0].length()) - tL_inputMessageEntityMentionName.offset;
                                if (charSequenceArr[0].charAt((tL_inputMessageEntityMentionName.offset + tL_inputMessageEntityMentionName.length) - 1) == ' ') {
                                    tL_inputMessageEntityMentionName.length--;
                                }
                                arrayList.add(tL_inputMessageEntityMentionName);
                            }
                        }
                    }
                }
                dataQuery = this;
                i4 = 0;
                while (i4 < 2) {
                    substring = i4 == 0 ? "**" : "__";
                    char c3 = i4 == 0 ? '*' : '_';
                    i6 = 0;
                    ArrayList arrayList3 = arrayList;
                    int i8 = -1;
                    while (true) {
                        i6 = TextUtils.indexOf(charSequenceArr[0], substring, i6);
                        if (i6 == -1) {
                            break;
                        } else if (i8 == -1) {
                            char c4;
                            if (i6 == 0) {
                                c4 = c2;
                            } else {
                                c4 = charSequenceArr[0].charAt(i6 - 1);
                            }
                            if (!checkInclusion(i6, arrayList3) && (r13 == c2 || r13 == r6)) {
                                i8 = i6;
                            }
                            i6 += 2;
                        } else {
                            int i9 = i6 + 2;
                            while (i9 < charSequenceArr[0].length() && charSequenceArr[0].charAt(i9) == c3) {
                                i6++;
                                i9++;
                            }
                            i9 = i6 + 2;
                            if (!checkInclusion(i6, arrayList3)) {
                                if (!checkIntersection(i8, i6, arrayList3)) {
                                    if (i8 + 2 != i6) {
                                        MessageEntity tL_messageEntityBold2;
                                        if (arrayList3 == null) {
                                            arrayList3 = new ArrayList();
                                        }
                                        charSequenceArr[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(charSequenceArr[0], 0, i8), TextUtils.substring(charSequenceArr[0], i2, i6), TextUtils.substring(charSequenceArr[0], i9, charSequenceArr[0].length())});
                                        if (i4 == 0) {
                                            tL_messageEntityBold2 = new TL_messageEntityBold();
                                        } else {
                                            tL_messageEntityBold2 = new TL_messageEntityItalic();
                                        }
                                        tL_messageEntityBold2.offset = i8;
                                        tL_messageEntityBold2.length = (i6 - i8) - 2;
                                        removeOffsetAfter(tL_messageEntityBold2.offset + tL_messageEntityBold2.length, 4, arrayList3);
                                        arrayList3.add(tL_messageEntityBold2);
                                        i9 -= 4;
                                    }
                                    i6 = i9;
                                    i8 = -1;
                                    c = '\n';
                                    c2 = ' ';
                                }
                            }
                            i8 = -1;
                            i6 = i9;
                            c = '\n';
                            c2 = ' ';
                        }
                    }
                    i4++;
                    arrayList = arrayList3;
                    c = '\n';
                    c2 = ' ';
                }
                return arrayList;
            }
        }
        dataQuery = this;
        return null;
    }

    public void loadDrafts() {
        if (!UserConfig.getInstance(this.currentAccount).draftsLoaded) {
            if (!this.loadingDrafts) {
                this.loadingDrafts = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getAllDrafts(), new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.DataQuery$60$1 */
                    class C01361 implements Runnable {
                        C01361() {
                        }

                        public void run() {
                            UserConfig.getInstance(DataQuery.this.currentAccount).draftsLoaded = true;
                            DataQuery.this.loadingDrafts = false;
                            UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error == null) {
                            MessagesController.getInstance(DataQuery.this.currentAccount).processUpdates((Updates) tLObject, false);
                            AndroidUtilities.runOnUIThread(new C01361());
                        }
                    }
                });
            }
        }
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
        DraftMessage draftMessage;
        TLObject tL_messages_saveDraft;
        if (TextUtils.isEmpty(charSequence)) {
            if (message == null) {
                tL_draftMessageEmpty = new TL_draftMessageEmpty();
                tL_draftMessageEmpty.date = (int) (System.currentTimeMillis() / 1000);
                if (charSequence != null) {
                    charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    charSequence = charSequence.toString();
                }
                tL_draftMessageEmpty.message = charSequence;
                tL_draftMessageEmpty.no_webpage = z;
                if (message != null) {
                    tL_draftMessageEmpty.reply_to_msg_id = message.id;
                    tL_draftMessageEmpty.flags |= 1;
                }
                if (arrayList != null && arrayList.isEmpty() == null) {
                    tL_draftMessageEmpty.entities = arrayList;
                    tL_draftMessageEmpty.flags |= 8;
                }
                draftMessage = (DraftMessage) this.drafts.get(j);
                if (!z2 || ((draftMessage == null || draftMessage.message.equals(tL_draftMessageEmpty.message) == null || draftMessage.reply_to_msg_id != tL_draftMessageEmpty.reply_to_msg_id || draftMessage.no_webpage != tL_draftMessageEmpty.no_webpage) && !(draftMessage == null && TextUtils.isEmpty(tL_draftMessageEmpty.message) != null && tL_draftMessageEmpty.reply_to_msg_id == null))) {
                    saveDraft(j, tL_draftMessageEmpty, message, false);
                    j = (int) j;
                    if (j != null) {
                        tL_messages_saveDraft = new TL_messages_saveDraft();
                        tL_messages_saveDraft.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
                        if (tL_messages_saveDraft.peer == null) {
                            tL_messages_saveDraft.message = tL_draftMessageEmpty.message;
                            tL_messages_saveDraft.no_webpage = tL_draftMessageEmpty.no_webpage;
                            tL_messages_saveDraft.reply_to_msg_id = tL_draftMessageEmpty.reply_to_msg_id;
                            tL_messages_saveDraft.entities = tL_draftMessageEmpty.entities;
                            tL_messages_saveDraft.flags = tL_draftMessageEmpty.flags;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveDraft, new RequestDelegate() {
                                public void run(TLObject tLObject, TL_error tL_error) {
                                }
                            });
                        } else {
                            return;
                        }
                    }
                    MessagesController.getInstance(this.currentAccount).sortDialogs(null);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[null]);
                }
                return;
            }
        }
        tL_draftMessageEmpty = new TL_draftMessage();
        tL_draftMessageEmpty.date = (int) (System.currentTimeMillis() / 1000);
        if (charSequence != null) {
            charSequence = charSequence.toString();
        } else {
            charSequence = TtmlNode.ANONYMOUS_REGION_ID;
        }
        tL_draftMessageEmpty.message = charSequence;
        tL_draftMessageEmpty.no_webpage = z;
        if (message != null) {
            tL_draftMessageEmpty.reply_to_msg_id = message.id;
            tL_draftMessageEmpty.flags |= 1;
        }
        tL_draftMessageEmpty.entities = arrayList;
        tL_draftMessageEmpty.flags |= 8;
        draftMessage = (DraftMessage) this.drafts.get(j);
        if (z2) {
        }
        saveDraft(j, tL_draftMessageEmpty, message, false);
        j = (int) j;
        if (j != null) {
            tL_messages_saveDraft = new TL_messages_saveDraft();
            tL_messages_saveDraft.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            if (tL_messages_saveDraft.peer == null) {
                tL_messages_saveDraft.message = tL_draftMessageEmpty.message;
                tL_messages_saveDraft.no_webpage = tL_draftMessageEmpty.no_webpage;
                tL_messages_saveDraft.reply_to_msg_id = tL_draftMessageEmpty.reply_to_msg_id;
                tL_messages_saveDraft.entities = tL_draftMessageEmpty.entities;
                tL_messages_saveDraft.flags = tL_draftMessageEmpty.flags;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_saveDraft, /* anonymous class already generated */);
            } else {
                return;
            }
        }
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[null]);
    }

    public void saveDraft(long j, DraftMessage draftMessage, Message message, boolean z) {
        AbstractSerializedData serializedData;
        StringBuilder stringBuilder;
        int i;
        Chat chat;
        User user;
        long j2;
        long j3;
        int i2;
        long j4;
        long j5 = j;
        DraftMessage draftMessage2 = draftMessage;
        Message message2 = message;
        Editor edit = this.preferences.edit();
        if (draftMessage2 != null) {
            if (!(draftMessage2 instanceof TL_draftMessageEmpty)) {
                r8.drafts.put(j5, draftMessage2);
                try {
                    serializedData = new SerializedData(draftMessage.getObjectSize());
                    draftMessage2.serializeToStream(serializedData);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(j5);
                    edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (message2 != null) {
                    r8.draftMessages.remove(j5);
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("r_");
                    stringBuilder2.append(j5);
                    edit.remove(stringBuilder2.toString());
                } else {
                    r8.draftMessages.put(j5, message2);
                    serializedData = new SerializedData(message.getObjectSize());
                    message2.serializeToStream(serializedData);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("r_");
                    stringBuilder.append(j5);
                    edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
                }
                edit.commit();
                if (z) {
                    if (draftMessage2.reply_to_msg_id != 0 && message2 == null) {
                        i = (int) j5;
                        chat = null;
                        if (i <= 0) {
                            user = MessagesController.getInstance(r8.currentAccount).getUser(Integer.valueOf(i));
                        } else {
                            chat = MessagesController.getInstance(r8.currentAccount).getChat(Integer.valueOf(-i));
                            user = null;
                        }
                        if (!(user == null && chat == null)) {
                            j2 = (long) draftMessage2.reply_to_msg_id;
                            if (ChatObject.isChannel(chat)) {
                                j3 = j2;
                                i2 = 0;
                            } else {
                                j4 = j2 | (((long) chat.id) << 32);
                                i2 = chat.id;
                                j3 = j4;
                            }
                            j4 = j5;
                            MessagesStorage.getInstance(r8.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                /* renamed from: org.telegram.messenger.DataQuery$62$1 */
                                class C17961 implements RequestDelegate {
                                    C17961() {
                                    }

                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        if (tL_error == null) {
                                            messages_Messages messages_messages = (messages_Messages) tLObject;
                                            if (messages_messages.messages.isEmpty() == null) {
                                                DataQuery.this.saveDraftReplyMessage(j4, (Message) messages_messages.messages.get(0));
                                            }
                                        }
                                    }
                                }

                                /* renamed from: org.telegram.messenger.DataQuery$62$2 */
                                class C17972 implements RequestDelegate {
                                    C17972() {
                                    }

                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        if (tL_error == null) {
                                            messages_Messages messages_messages = (messages_Messages) tLObject;
                                            if (messages_messages.messages.isEmpty() == null) {
                                                DataQuery.this.saveDraftReplyMessage(j4, (Message) messages_messages.messages.get(0));
                                            }
                                        }
                                    }
                                }

                                public void run() {
                                    Message message = null;
                                    try {
                                        SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(j3)}), new Object[0]);
                                        if (queryFinalized.next()) {
                                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                                            if (byteBufferValue != null) {
                                                message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                message.readAttachPath(byteBufferValue, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                                byteBufferValue.reuse();
                                            }
                                        }
                                        queryFinalized.dispose();
                                        if (message != null) {
                                            DataQuery.this.saveDraftReplyMessage(j4, message);
                                        } else if (i2 != 0) {
                                            r0 = new TL_channels_getMessages();
                                            r0.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(i2);
                                            r0.id.add(Integer.valueOf((int) j3));
                                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(r0, new C17961());
                                        } else {
                                            r0 = new TL_messages_getMessages();
                                            r0.id.add(Integer.valueOf((int) j3));
                                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(r0, new C17972());
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                            });
                        }
                    }
                    NotificationCenter.getInstance(r8.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
                }
            }
        }
        r8.drafts.remove(j5);
        r8.draftMessages.remove(j5);
        Editor edit2 = r8.preferences.edit();
        stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(j5);
        edit2 = edit2.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("r_");
        stringBuilder.append(j5);
        edit2.remove(stringBuilder.toString()).commit();
        if (message2 != null) {
            r8.draftMessages.put(j5, message2);
            serializedData = new SerializedData(message.getObjectSize());
            message2.serializeToStream(serializedData);
            stringBuilder = new StringBuilder();
            stringBuilder.append("r_");
            stringBuilder.append(j5);
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
        } else {
            r8.draftMessages.remove(j5);
            StringBuilder stringBuilder22 = new StringBuilder();
            stringBuilder22.append("r_");
            stringBuilder22.append(j5);
            edit.remove(stringBuilder22.toString());
        }
        edit.commit();
        if (z) {
            i = (int) j5;
            chat = null;
            if (i <= 0) {
                chat = MessagesController.getInstance(r8.currentAccount).getChat(Integer.valueOf(-i));
                user = null;
            } else {
                user = MessagesController.getInstance(r8.currentAccount).getUser(Integer.valueOf(i));
            }
            j2 = (long) draftMessage2.reply_to_msg_id;
            if (ChatObject.isChannel(chat)) {
                j3 = j2;
                i2 = 0;
            } else {
                j4 = j2 | (((long) chat.id) << 32);
                i2 = chat.id;
                j3 = j4;
            }
            j4 = j5;
            MessagesStorage.getInstance(r8.currentAccount).getStorageQueue().postRunnable(/* anonymous class already generated */);
            NotificationCenter.getInstance(r8.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    private void saveDraftReplyMessage(final long j, final Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    DraftMessage draftMessage = (DraftMessage) DataQuery.this.drafts.get(j);
                    if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
                        DataQuery.this.draftMessages.put(j, message);
                        AbstractSerializedData serializedData = new SerializedData(message.getObjectSize());
                        message.serializeToStream(serializedData);
                        Editor edit = DataQuery.this.preferences.edit();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("r_");
                        stringBuilder.append(j);
                        edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray())).commit();
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
                    }
                }
            });
        }
    }

    public void cleanDraft(long j, boolean z) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(j);
        if (draftMessage != null) {
            if (!z) {
                this.drafts.remove(j);
                this.draftMessages.remove(j);
                z = this.preferences.edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                stringBuilder.append(j);
                z = z.remove(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("r_");
                stringBuilder.append(j);
                z.remove(stringBuilder.toString()).commit();
                MessagesController.getInstance(this.currentAccount).sortDialogs(null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            } else if (draftMessage.reply_to_msg_id) {
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

    public void clearBotKeyboard(final long j, final ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (arrayList != null) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        long j = DataQuery.this.botKeyboardsByMids.get(((Integer) arrayList.get(i)).intValue());
                        if (j != 0) {
                            DataQuery.this.botKeyboards.remove(j);
                            DataQuery.this.botKeyboardsByMids.delete(((Integer) arrayList.get(i)).intValue());
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(j));
                        }
                    }
                    return;
                }
                DataQuery.this.botKeyboards.remove(j);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(j));
            }
        });
    }

    public void loadBotKeyboard(final long j) {
        if (((Message) this.botKeyboards.get(j)) != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, r0, Long.valueOf(j));
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                Message message = null;
                try {
                    SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
                    if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                        AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                        if (byteBufferValue != null) {
                            message = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                        }
                    }
                    queryFinalized.dispose();
                    if (message != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, message, Long.valueOf(j));
                            }
                        });
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadBotInfo(final int i, boolean z, final int i2) {
        if (!z || ((BotInfo) this.botInfos.get(i)) == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    BotInfo botInfo = null;
                    try {
                        SQLiteCursor queryFinalized = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(i)}), new Object[0]);
                        if (queryFinalized.next() && !queryFinalized.isNull(0)) {
                            AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(0);
                            if (byteBufferValue != null) {
                                botInfo = BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                byteBufferValue.reuse();
                            }
                        }
                        queryFinalized.dispose();
                        if (botInfo != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, botInfo, Integer.valueOf(i2));
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, r5, Integer.valueOf(i2));
    }

    public void putBotKeyboard(final long j, final Message message) {
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            Message message = (Message) DataQuery.this.botKeyboards.get(j);
                            DataQuery.this.botKeyboards.put(j, message);
                            if (message != null) {
                                DataQuery.this.botKeyboardsByMids.delete(message.id);
                            }
                            DataQuery.this.botKeyboardsByMids.put(message.id, j);
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, message, Long.valueOf(j));
                        }
                    });
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void putBotInfo(final BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(botInfo.user_id, botInfo);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
                        executeFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(botInfo.getObjectSize());
                        botInfo.serializeToStream(nativeByteBuffer);
                        executeFast.bindInteger(1, botInfo.user_id);
                        executeFast.bindByteBuffer(2, nativeByteBuffer);
                        executeFast.step();
                        nativeByteBuffer.reuse();
                        executeFast.dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }
}
