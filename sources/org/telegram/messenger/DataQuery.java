package org.telegram.messenger;

import android.annotation.SuppressLint;
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
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import java.io.File;
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
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputPeer;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.LaunchActivity;

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
        public int compare(MessageEntity entity1, MessageEntity entity2) {
            if (entity1.offset > entity2.offset) {
                return 1;
            }
            if (entity1.offset < entity2.offset) {
                return -1;
            }
            return 0;
        }
    };
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

    /* renamed from: org.telegram.messenger.DataQuery$1 */
    class C17921 implements RequestDelegate {
        C17921() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    /* renamed from: org.telegram.messenger.DataQuery$3 */
    class C17933 implements RequestDelegate {
        C17933() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    /* renamed from: org.telegram.messenger.DataQuery$7 */
    class C17987 implements RequestDelegate {
        C17987() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                final TL_messages_stickerSet set = (TL_messages_stickerSet) response;
                MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        try {
                            SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            state.requery();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("s_");
                            stringBuilder.append(set.set.id);
                            state.bindString(1, stringBuilder.toString());
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
                            FileLog.m3e(e);
                        }
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DataQuery.this.groupStickerSets.put(set.set.id, set);
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(set.set.id));
                    }
                });
            }
        }
    }

    public static DataQuery getInstance(int num) {
        DataQuery localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DataQuery.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    DataQuery[] dataQueryArr = Instance;
                    DataQuery dataQuery = new DataQuery(num);
                    localInstance = dataQuery;
                    dataQueryArr[num] = dataQuery;
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
            Context context = ApplicationLoader.applicationContext;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("drafts");
            stringBuilder.append(this.currentAccount);
            this.preferences = context.getSharedPreferences(stringBuilder.toString(), 0);
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
            if (d.id == document.id && d.dc_id == document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void addRecentSticker(final int type, Document document, int date, boolean remove) {
        int maxCount;
        boolean found = false;
        for (int a = 0; a < this.recentStickers[type].size(); a++) {
            Document image = (Document) this.recentStickers[type].get(a);
            if (image.id == document.id) {
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
            req.id = new TL_inputDocument();
            req.id.id = document.id;
            req.id.access_hash = document.access_hash;
            req.unfave = remove;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C17921());
            maxCount = MessagesController.getInstance(this.currentAccount).maxFaveStickersCount;
        } else {
            maxCount = MessagesController.getInstance(this.currentAccount).maxRecentStickersCount;
        }
        if (this.recentStickers[type].size() > maxCount || remove) {
            final Document old = remove ? document : (Document) this.recentStickers[type].remove(this.recentStickers[type].size() - 1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    int cacheType;
                    if (type == 0) {
                        cacheType = 3;
                    } else if (type == 1) {
                        cacheType = 4;
                    } else {
                        cacheType = 5;
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                        stringBuilder.append(old.id);
                        stringBuilder.append("' AND type = ");
                        stringBuilder.append(cacheType);
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                    }
                    try {
                        SQLiteDatabase database2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                        stringBuilder2.append(old.id);
                        stringBuilder2.append("' AND type = ");
                        stringBuilder2.append(cacheType);
                        database2.executeFast(stringBuilder2.toString()).stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        if (!remove) {
            ArrayList<Document> arrayList = new ArrayList();
            arrayList.add(document);
            processLoadedRecentDocuments(type, arrayList, false, date);
        }
        if (type == 2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(false), Integer.valueOf(type));
        }
    }

    public ArrayList<Document> getRecentGifs() {
        return new ArrayList(this.recentGifs);
    }

    public void removeRecentGif(final Document document) {
        this.recentGifs.remove(document);
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C17933());
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

    public void addRecentGif(Document document, int date) {
        boolean found = false;
        for (int a = 0; a < this.recentGifs.size(); a++) {
            Document image = (Document) this.recentGifs.get(a);
            if (image.id == document.id) {
                this.recentGifs.remove(a);
                this.recentGifs.add(0, image);
                found = true;
            }
        }
        if (!found) {
            this.recentGifs.add(0, document);
        }
        if (this.recentGifs.size() > MessagesController.getInstance(this.currentAccount).maxRecentGifsCount) {
            final Document old = (Document) this.recentGifs.remove(this.recentGifs.size() - 1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("DELETE FROM web_recent_v3 WHERE id = '");
                        stringBuilder.append(old.id);
                        stringBuilder.append("' AND type = 2");
                        database.executeFast(stringBuilder.toString()).stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        ArrayList<Document> arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(0, arrayList, true, date);
    }

    public boolean isLoadingStickers(int type) {
        return this.loadingStickers[type];
    }

    public TL_messages_stickerSet getStickerSetByName(String name) {
        return (TL_messages_stickerSet) this.stickerSetsByName.get(name);
    }

    public TL_messages_stickerSet getStickerSetById(long id) {
        return (TL_messages_stickerSet) this.stickerSetsById.get(id);
    }

    public TL_messages_stickerSet getGroupStickerSetById(StickerSet stickerSet) {
        TL_messages_stickerSet set = (TL_messages_stickerSet) this.stickerSetsById.get(stickerSet.id);
        if (set == null) {
            set = (TL_messages_stickerSet) this.groupStickerSets.get(stickerSet.id);
            if (set != null) {
                if (set.set != null) {
                    if (set.set.hash != stickerSet.hash) {
                        loadGroupStickerSet(stickerSet, false);
                    }
                }
            }
            loadGroupStickerSet(stickerSet, true);
        }
        return set;
    }

    public void putGroupStickerSet(TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(stickerSet.set.id, stickerSet);
    }

    private void loadGroupStickerSet(final StickerSet stickerSet, boolean cache) {
        if (cache) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT document FROM web_recent_v3 WHERE id = 's_");
                        stringBuilder.append(stickerSet.id);
                        stringBuilder.append("'");
                        cursor = cursor.queryFinalized(stringBuilder.toString(), new Object[0]);
                        TL_messages_stickerSet set = null;
                        if (cursor.next() && !cursor.isNull(0)) {
                            NativeByteBuffer data = cursor.byteBufferValue(0);
                            if (data != null) {
                                set = TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false);
                                data.reuse();
                            }
                        }
                        final TL_messages_stickerSet set2 = set;
                        cursor.dispose();
                        if (set2 == null || set2.set == null || set2.set.hash != stickerSet.hash) {
                            DataQuery.this.loadGroupStickerSet(stickerSet, false);
                        }
                        if (!(set2 == null || set2.set == null)) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    DataQuery.this.groupStickerSets.put(set2.set.id, set2);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(set2.set.id));
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
        TL_messages_getStickerSet req = new TL_messages_getStickerSet();
        req.stickerset = new TL_inputStickerSetID();
        req.stickerset.id = stickerSet.id;
        req.stickerset.access_hash = stickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C17987());
    }

    public HashMap<String, ArrayList<Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public boolean canAddStickerToFavorites() {
        if (this.stickersLoaded[0] && this.stickerSets[0].size() < 5) {
            if (this.recentStickers[2].isEmpty()) {
                return false;
            }
        }
        return true;
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
        ArrayList<Document> arrayList2 = arrayList;
        int a = 0;
        if (arrayList2 == null) {
            return 0;
        }
        long acc = 0;
        while (a < Math.min(Callback.DEFAULT_DRAG_ANIMATION_DURATION, arrayList.size())) {
            Document document = (Document) arrayList2.get(a);
            if (document != null) {
                acc = (((20261 * ((((acc * 20261) + 2147483648L) + ((long) ((int) (document.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) document.id))) % 2147483648L;
            }
            a++;
        }
        return (int) acc;
    }

    public void loadRecents(final int type, final boolean gif, boolean cache, boolean force) {
        boolean z = true;
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        int cacheType;
                        SQLiteCursor cursor;
                        StringBuilder stringBuilder;
                        final ArrayList<Document> arrayList;
                        NativeByteBuffer data;
                        Document document;
                        if (gif) {
                            cacheType = 2;
                        } else if (type == 0) {
                            cacheType = 3;
                        } else if (type == 1) {
                            cacheType = 4;
                        } else {
                            cacheType = 5;
                            cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT document FROM web_recent_v3 WHERE type = ");
                            stringBuilder.append(cacheType);
                            stringBuilder.append(" ORDER BY date DESC");
                            cursor = cursor.queryFinalized(stringBuilder.toString(), new Object[0]);
                            arrayList = new ArrayList();
                            while (cursor.next()) {
                                if (!cursor.isNull(0)) {
                                    data = cursor.byteBufferValue(0);
                                    if (data != null) {
                                        document = Document.TLdeserialize(data, data.readInt32(false), false);
                                        if (document != null) {
                                            arrayList.add(document);
                                        }
                                        data.reuse();
                                    }
                                }
                            }
                            cursor.dispose();
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (gif) {
                                        DataQuery.this.recentGifs = arrayList;
                                        DataQuery.this.loadingRecentGifs = false;
                                        DataQuery.this.recentGifsLoaded = true;
                                    } else {
                                        DataQuery.this.recentStickers[type] = arrayList;
                                        DataQuery.this.loadingRecentStickers[type] = false;
                                        DataQuery.this.recentStickersLoaded[type] = true;
                                    }
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(gif), Integer.valueOf(type));
                                    DataQuery.this.loadRecents(type, gif, false, false);
                                }
                            });
                        }
                        cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("SELECT document FROM web_recent_v3 WHERE type = ");
                        stringBuilder.append(cacheType);
                        stringBuilder.append(" ORDER BY date DESC");
                        cursor = cursor.queryFinalized(stringBuilder.toString(), new Object[0]);
                        arrayList = new ArrayList();
                        while (cursor.next()) {
                            if (!cursor.isNull(0)) {
                                data = cursor.byteBufferValue(0);
                                if (data != null) {
                                    document = Document.TLdeserialize(data, data.readInt32(false), false);
                                    if (document != null) {
                                        arrayList.add(document);
                                    }
                                    data.reuse();
                                }
                            }
                        }
                        cursor.dispose();
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        } else {
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
                    if (Math.abs(System.currentTimeMillis() - lastLoadTime) < 3600000) {
                        if (gif) {
                            this.loadingRecentStickers[type] = false;
                        } else {
                            this.loadingRecentGifs = false;
                        }
                        return;
                    }
                }
                if (Math.abs(System.currentTimeMillis() - lastLoadTime) < 3600000) {
                    if (gif) {
                        this.loadingRecentStickers[type] = false;
                    } else {
                        this.loadingRecentGifs = false;
                    }
                    return;
                }
            }
            if (gif) {
                TL_messages_getSavedGifs req = new TL_messages_getSavedGifs();
                req.hash = calcDocumentsHash(this.recentGifs);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        ArrayList<Document> arrayList = null;
                        if (response instanceof TL_messages_savedGifs) {
                            arrayList = ((TL_messages_savedGifs) response).gifs;
                        }
                        DataQuery.this.processLoadedRecentDocuments(type, arrayList, gif, 0);
                    }
                });
            } else {
                TLObject request;
                if (type == 2) {
                    request = new TL_messages_getFavedStickers();
                    request.hash = calcDocumentsHash(this.recentStickers[type]);
                } else {
                    TLObject req2 = new TL_messages_getRecentStickers();
                    req2.hash = calcDocumentsHash(this.recentStickers[type]);
                    if (type != 1) {
                        z = false;
                    }
                    req2.attached = z;
                    request = req2;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        ArrayList<Document> arrayList = null;
                        if (type == 2) {
                            if (response instanceof TL_messages_favedStickers) {
                                arrayList = ((TL_messages_favedStickers) response).stickers;
                            }
                        } else if (response instanceof TL_messages_recentStickers) {
                            arrayList = ((TL_messages_recentStickers) response).stickers;
                        }
                        DataQuery.this.processLoadedRecentDocuments(type, arrayList, gif, 0);
                    }
                });
            }
        }
    }

    private void processLoadedRecentDocuments(final int type, final ArrayList<Document> documents, final boolean gif, int date) {
        if (documents != null) {
            final boolean z = gif;
            final int i = type;
            final ArrayList<Document> arrayList = documents;
            final int i2 = date;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        int maxCount;
                        SQLitePreparedStatement state;
                        int count;
                        int cacheType;
                        int a;
                        Document document;
                        StringBuilder stringBuilder;
                        NativeByteBuffer data;
                        int a2;
                        StringBuilder stringBuilder2;
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        if (z) {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentGifsCount;
                        } else if (i == 2) {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxFaveStickersCount;
                        } else {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentStickersCount;
                            database.beginTransaction();
                            state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            count = arrayList.size();
                            if (z) {
                                cacheType = 2;
                            } else if (i == 0) {
                                cacheType = 3;
                            } else if (i != 1) {
                                cacheType = 4;
                            } else {
                                cacheType = 5;
                                a = 0;
                                while (a < count) {
                                    if (a != maxCount) {
                                        break;
                                    }
                                    document = (Document) arrayList.get(a);
                                    state.requery();
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(document.id);
                                    state.bindString(1, stringBuilder.toString());
                                    state.bindInteger(2, cacheType);
                                    state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                                    state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                                    state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                                    state.bindInteger(6, 0);
                                    state.bindInteger(7, 0);
                                    state.bindInteger(8, 0);
                                    state.bindInteger(9, i2 == 0 ? i2 : count - a);
                                    data = new NativeByteBuffer(document.getObjectSize());
                                    document.serializeToStream(data);
                                    state.bindByteBuffer(10, data);
                                    state.step();
                                    if (data == null) {
                                        data.reuse();
                                    }
                                    a++;
                                }
                                state.dispose();
                                database.commitTransaction();
                                if (arrayList.size() >= maxCount) {
                                    database.beginTransaction();
                                    for (a2 = maxCount; a2 < arrayList.size(); a2++) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                                        stringBuilder2.append(((Document) arrayList.get(a2)).id);
                                        stringBuilder2.append("' AND type = ");
                                        stringBuilder2.append(cacheType);
                                        database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                                    }
                                    database.commitTransaction();
                                }
                            }
                            a = 0;
                            while (a < count) {
                                if (a != maxCount) {
                                    break;
                                }
                                document = (Document) arrayList.get(a);
                                state.requery();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(document.id);
                                state.bindString(1, stringBuilder.toString());
                                state.bindInteger(2, cacheType);
                                state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindInteger(6, 0);
                                state.bindInteger(7, 0);
                                state.bindInteger(8, 0);
                                if (i2 == 0) {
                                }
                                state.bindInteger(9, i2 == 0 ? i2 : count - a);
                                data = new NativeByteBuffer(document.getObjectSize());
                                document.serializeToStream(data);
                                state.bindByteBuffer(10, data);
                                state.step();
                                if (data == null) {
                                    data.reuse();
                                }
                                a++;
                            }
                            state.dispose();
                            database.commitTransaction();
                            if (arrayList.size() >= maxCount) {
                                database.beginTransaction();
                                for (a2 = maxCount; a2 < arrayList.size(); a2++) {
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                                    stringBuilder2.append(((Document) arrayList.get(a2)).id);
                                    stringBuilder2.append("' AND type = ");
                                    stringBuilder2.append(cacheType);
                                    database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                                }
                                database.commitTransaction();
                            }
                        }
                        database.beginTransaction();
                        state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        count = arrayList.size();
                        if (z) {
                            cacheType = 2;
                        } else if (i == 0) {
                            cacheType = 3;
                        } else if (i != 1) {
                            cacheType = 5;
                            a = 0;
                            while (a < count) {
                                if (a != maxCount) {
                                    break;
                                }
                                document = (Document) arrayList.get(a);
                                state.requery();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(document.id);
                                state.bindString(1, stringBuilder.toString());
                                state.bindInteger(2, cacheType);
                                state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                                state.bindInteger(6, 0);
                                state.bindInteger(7, 0);
                                state.bindInteger(8, 0);
                                if (i2 == 0) {
                                }
                                state.bindInteger(9, i2 == 0 ? i2 : count - a);
                                data = new NativeByteBuffer(document.getObjectSize());
                                document.serializeToStream(data);
                                state.bindByteBuffer(10, data);
                                state.step();
                                if (data == null) {
                                    data.reuse();
                                }
                                a++;
                            }
                            state.dispose();
                            database.commitTransaction();
                            if (arrayList.size() >= maxCount) {
                                database.beginTransaction();
                                for (a2 = maxCount; a2 < arrayList.size(); a2++) {
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                                    stringBuilder2.append(((Document) arrayList.get(a2)).id);
                                    stringBuilder2.append("' AND type = ");
                                    stringBuilder2.append(cacheType);
                                    database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                                }
                                database.commitTransaction();
                            }
                        } else {
                            cacheType = 4;
                        }
                        a = 0;
                        while (a < count) {
                            if (a != maxCount) {
                                break;
                            }
                            document = (Document) arrayList.get(a);
                            state.requery();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(document.id);
                            state.bindString(1, stringBuilder.toString());
                            state.bindInteger(2, cacheType);
                            state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindInteger(6, 0);
                            state.bindInteger(7, 0);
                            state.bindInteger(8, 0);
                            if (i2 == 0) {
                            }
                            state.bindInteger(9, i2 == 0 ? i2 : count - a);
                            data = new NativeByteBuffer(document.getObjectSize());
                            document.serializeToStream(data);
                            state.bindByteBuffer(10, data);
                            state.step();
                            if (data == null) {
                                data.reuse();
                            }
                            a++;
                        }
                        state.dispose();
                        database.commitTransaction();
                        if (arrayList.size() >= maxCount) {
                            database.beginTransaction();
                            for (a2 = maxCount; a2 < arrayList.size(); a2++) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("DELETE FROM web_recent_v3 WHERE id = '");
                                stringBuilder2.append(((Document) arrayList.get(a2)).id);
                                stringBuilder2.append("' AND type = ");
                                stringBuilder2.append(cacheType);
                                database.executeFast(stringBuilder2.toString()).stepThis().dispose();
                            }
                            database.commitTransaction();
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Editor editor = MessagesController.getEmojiSettings(DataQuery.this.currentAccount).edit();
                    if (gif) {
                        DataQuery.this.loadingRecentGifs = false;
                        DataQuery.this.recentGifsLoaded = true;
                        editor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
                    } else {
                        DataQuery.this.loadingRecentStickers[type] = false;
                        DataQuery.this.recentStickersLoaded[type] = true;
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
                            DataQuery.this.recentGifs = documents;
                        } else {
                            DataQuery.this.recentStickers[type] = documents;
                        }
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(gif), Integer.valueOf(type));
                    }
                }
            });
        }
    }

    public void reorderStickers(int type, final ArrayList<Long> order) {
        Collections.sort(this.stickerSets[type], new Comparator<TL_messages_stickerSet>() {
            public int compare(TL_messages_stickerSet lhs, TL_messages_stickerSet rhs) {
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
        });
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    public void calcNewHash(int type) {
        this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
    }

    public void addNewStickerSet(TL_messages_stickerSet set) {
        if (this.stickerSetsById.indexOfKey(set.set.id) < 0) {
            if (!this.stickerSetsByName.containsKey(set.set.short_name)) {
                int a;
                int type = set.set.masks;
                this.stickerSets[type].add(0, set);
                this.stickerSetsById.put(set.set.id, set);
                this.installedStickerSetsById.put(set.set.id, set);
                this.stickerSetsByName.put(set.set.short_name, set);
                LongSparseArray<Document> stickersById = new LongSparseArray();
                for (a = 0; a < set.documents.size(); a++) {
                    Document document = (Document) set.documents.get(a);
                    stickersById.put(document.id, document);
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
                loadStickers(type, false, true);
            }
        }
    }

    public void loadFeaturedStickers(boolean cache, boolean force) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ArrayList<StickerSetCovered> newStickerArray = null;
                        ArrayList<Long> unread = new ArrayList();
                        int date = 0;
                        int hash = 0;
                        SQLiteCursor cursor = null;
                        try {
                            cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT data, unread, date, hash FROM stickers_featured WHERE 1", new Object[0]);
                            if (cursor.next()) {
                                int count;
                                int a;
                                NativeByteBuffer data = cursor.byteBufferValue(0);
                                if (data != null) {
                                    newStickerArray = new ArrayList();
                                    count = data.readInt32(false);
                                    for (a = 0; a < count; a++) {
                                        newStickerArray.add(StickerSetCovered.TLdeserialize(data, data.readInt32(false), false));
                                    }
                                    data.reuse();
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
                                hash = DataQuery.this.calcFeaturedStickersHash(newStickerArray);
                            }
                            if (cursor != null) {
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            DataQuery.this.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                        } finally {
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        }
                        DataQuery.this.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                    }
                });
            } else {
                final TL_messages_getFeaturedStickers req = new TL_messages_getFeaturedStickers();
                req.hash = force ? 0 : this.loadFeaturedHash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (response instanceof TL_messages_featuredStickers) {
                                    TL_messages_featuredStickers res = response;
                                    DataQuery.this.processLoadedFeaturedStickers(res.sets, res.unread, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                    return;
                                }
                                DataQuery.this.processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), req.hash);
                            }
                        });
                    }
                });
            }
        }
    }

    private void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> res, ArrayList<Long> unreadStickers, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                DataQuery.this.loadingFeaturedStickers = false;
                DataQuery.this.featuredStickersLoaded = true;
            }
        });
        final boolean z = cache;
        final ArrayList<StickerSetCovered> arrayList = res;
        final int i = date;
        final int i2 = hash;
        final ArrayList<Long> arrayList2 = unreadStickers;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.DataQuery$17$1 */
            class C01091 implements Runnable {
                C01091() {
                }

                public void run() {
                    if (!(arrayList == null || i2 == 0)) {
                        DataQuery.this.loadFeaturedHash = i2;
                    }
                    DataQuery.this.loadFeaturedStickers(false, false);
                }
            }

            /* renamed from: org.telegram.messenger.DataQuery$17$3 */
            class C01113 implements Runnable {
                C01113() {
                }

                public void run() {
                    DataQuery.this.loadFeaturedDate = i;
                }
            }

            public void run() {
                long j = 1000;
                if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
                    Runnable c01091 = new C01091();
                    if (arrayList != null || z) {
                        j = 0;
                    }
                    AndroidUtilities.runOnUIThread(c01091, j);
                    if (arrayList == null) {
                        return;
                    }
                }
                int a = 0;
                if (arrayList != null) {
                    try {
                        final ArrayList<StickerSetCovered> stickerSetsNew = new ArrayList();
                        final LongSparseArray<StickerSetCovered> stickerSetsByIdNew = new LongSparseArray();
                        while (a < arrayList.size()) {
                            StickerSetCovered stickerSet = (StickerSetCovered) arrayList.get(a);
                            stickerSetsNew.add(stickerSet);
                            stickerSetsByIdNew.put(stickerSet.set.id, stickerSet);
                            a++;
                        }
                        if (!z) {
                            DataQuery.this.putFeaturedStickersToCache(stickerSetsNew, arrayList2, i, i2);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                DataQuery.this.unreadStickerSets = arrayList2;
                                DataQuery.this.featuredStickerSetsById = stickerSetsByIdNew;
                                DataQuery.this.featuredStickerSets = stickerSetsNew;
                                DataQuery.this.loadFeaturedHash = i2;
                                DataQuery.this.loadFeaturedDate = i;
                                DataQuery.this.loadStickers(3, true, false);
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new C01113());
                    DataQuery.this.putFeaturedStickersToCache(null, null, i, 0);
                }
            }
        });
    }

    private void putFeaturedStickersToCache(ArrayList<StickerSetCovered> stickers, ArrayList<Long> unreadStickers, int date, int hash) {
        final ArrayList<StickerSetCovered> stickersFinal = stickers != null ? new ArrayList(stickers) : null;
        final ArrayList<Long> arrayList = unreadStickers;
        final int i = date;
        final int i2 = hash;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state;
                    if (stickersFinal != null) {
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                        state.requery();
                        int a = 0;
                        int size = 4;
                        for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                            size += ((StickerSetCovered) stickersFinal.get(a2)).getObjectSize();
                        }
                        NativeByteBuffer data = new NativeByteBuffer(size);
                        NativeByteBuffer data2 = new NativeByteBuffer((arrayList.size() * 8) + 4);
                        data.writeInt32(stickersFinal.size());
                        for (int a3 = 0; a3 < stickersFinal.size(); a3++) {
                            ((StickerSetCovered) stickersFinal.get(a3)).serializeToStream(data);
                        }
                        data2.writeInt32(arrayList.size());
                        while (a < arrayList.size()) {
                            data2.writeInt64(((Long) arrayList.get(a)).longValue());
                            a++;
                        }
                        state.bindInteger(1, 1);
                        state.bindByteBuffer(2, data);
                        state.bindByteBuffer(3, data2);
                        state.bindInteger(4, i);
                        state.bindInteger(5, i2);
                        state.step();
                        data.reuse();
                        data2.reuse();
                        state.dispose();
                    } else {
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
                        state.requery();
                        state.bindInteger(1, i);
                        state.step();
                        state.dispose();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private int calcFeaturedStickersHash(ArrayList<StickerSetCovered> sets) {
        DataQuery dataQuery;
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((StickerSetCovered) sets.get(a)).set;
            if (set.archived) {
                dataQuery = this;
            } else {
                long acc2 = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
                acc = this.unreadStickerSets.contains(Long.valueOf(set.id)) ? (((20261 * acc2) + 2147483648L) + 1) % 2147483648L : acc2;
            }
        }
        dataQuery = this;
        ArrayList<StickerSetCovered> arrayList = sets;
        return (int) acc;
    }

    public void markFaturedStickersAsRead(boolean query) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (query) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_readFeaturedStickers(), new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        }
    }

    public int getFeaturesStickersHashWithoutUnread() {
        DataQuery dataQuery = this;
        long acc = 0;
        for (int a = 0; a < dataQuery.featuredStickerSets.size(); a++) {
            StickerSet set = ((StickerSetCovered) dataQuery.featuredStickerSets.get(a)).set;
            if (!set.archived) {
                acc = (((20261 * ((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersByIdAsRead(final long id) {
        if (this.unreadStickerSets.contains(Long.valueOf(id))) {
            if (!this.readingStickerSets.contains(Long.valueOf(id))) {
                this.readingStickerSets.add(Long.valueOf(id));
                TL_messages_readFeaturedStickers req = new TL_messages_readFeaturedStickers();
                req.id.add(Long.valueOf(id));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DataQuery.this.unreadStickerSets.remove(Long.valueOf(id));
                        DataQuery.this.readingStickerSets.remove(Long.valueOf(id));
                        DataQuery.this.loadFeaturedHash = DataQuery.this.calcFeaturedStickersHash(DataQuery.this.featuredStickerSets);
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                        DataQuery.this.putFeaturedStickersToCache(DataQuery.this.featuredStickerSets, DataQuery.this.unreadStickerSets, DataQuery.this.loadFeaturedDate, DataQuery.this.loadFeaturedHash);
                    }
                }, 1000);
            }
        }
    }

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void loadArchivedStickersCount(final int type, boolean cache) {
        boolean z = true;
        if (cache) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("archivedStickersCount");
            stringBuilder.append(type);
            int count = preferences.getInt(stringBuilder.toString(), -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
            } else {
                this.archivedStickersCount[type] = count;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
            }
            return;
        }
        TL_messages_getArchivedStickers req = new TL_messages_getArchivedStickers();
        req.limit = 0;
        if (type != 1) {
            z = false;
        }
        req.masks = z;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (error == null) {
                            TL_messages_archivedStickers res = response;
                            DataQuery.this.archivedStickersCount[type] = res.count;
                            Editor edit = MessagesController.getNotificationsSettings(DataQuery.this.currentAccount).edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("archivedStickersCount");
                            stringBuilder.append(type);
                            edit.putInt(stringBuilder.toString(), res.count).commit();
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
                        }
                    }
                });
            }
        });
    }

    private void processLoadStickersResponse(int type, TL_messages_allStickers res) {
        DataQuery dataQuery = this;
        TL_messages_allStickers tL_messages_allStickers = res;
        ArrayList<TL_messages_stickerSet> newStickerArray = new ArrayList();
        long j = 1000;
        if (tL_messages_allStickers.sets.isEmpty()) {
            processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers.hash);
            return;
        }
        LongSparseArray<TL_messages_stickerSet> newStickerSets = new LongSparseArray();
        int a = 0;
        while (true) {
            int a2 = a;
            if (a2 < tL_messages_allStickers.sets.size()) {
                StickerSet stickerSet = (StickerSet) tL_messages_allStickers.sets.get(a2);
                TL_messages_stickerSet oldSet = (TL_messages_stickerSet) dataQuery.stickerSetsById.get(stickerSet.id);
                if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                    newStickerArray.add(null);
                    final int index = a2;
                    TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                    req.stickerset = new TL_inputStickerSetID();
                    req.stickerset.id = stickerSet.id;
                    req.stickerset.access_hash = stickerSet.access_hash;
                    DataQuery dataQuery2 = dataQuery;
                    final ArrayList<TL_messages_stickerSet> arrayList = newStickerArray;
                    AnonymousClass23 anonymousClass23 = r0;
                    final LongSparseArray<TL_messages_stickerSet> longSparseArray = newStickerSets;
                    ConnectionsManager instance = ConnectionsManager.getInstance(dataQuery.currentAccount);
                    final StickerSet stickerSet2 = stickerSet;
                    TL_messages_getStickerSet req2 = req;
                    final TL_messages_allStickers tL_messages_allStickers2 = tL_messages_allStickers;
                    oldSet = type;
                    AnonymousClass23 anonymousClass232 = new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_messages_stickerSet res1 = response;
                                    arrayList.set(index, res1);
                                    longSparseArray.put(stickerSet2.id, res1);
                                    if (longSparseArray.size() == tL_messages_allStickers2.sets.size()) {
                                        for (int a = 0; a < arrayList.size(); a++) {
                                            if (arrayList.get(a) == null) {
                                                arrayList.remove(a);
                                            }
                                        }
                                        DataQuery.this.processLoadedStickers(oldSet, arrayList, false, (int) (System.currentTimeMillis() / 1000), tL_messages_allStickers2.hash);
                                    }
                                }
                            });
                        }
                    };
                    instance.sendRequest(req2, anonymousClass23);
                } else {
                    oldSet.set.archived = stickerSet.archived;
                    oldSet.set.installed = stickerSet.installed;
                    oldSet.set.official = stickerSet.official;
                    newStickerSets.put(oldSet.set.id, oldSet);
                    newStickerArray.add(oldSet);
                    if (newStickerSets.size() == tL_messages_allStickers.sets.size()) {
                        dataQuery.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / j), tL_messages_allStickers.hash);
                    }
                }
                a = a2 + 1;
                dataQuery = this;
                j = 1000;
            } else {
                return;
            }
        }
    }

    public void loadStickers(final int type, boolean cache, boolean force) {
        if (!this.loadingStickers[type]) {
            if (type != 3) {
                loadArchivedStickersCount(type, cache);
            } else if (this.featuredStickerSets.isEmpty() || !MessagesController.getInstance(this.currentAccount).preloadFeaturedStickers) {
                return;
            }
            this.loadingStickers[type] = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        ArrayList<TL_messages_stickerSet> newStickerArray = null;
                        int date = 0;
                        int hash = 0;
                        SQLiteCursor cursor = null;
                        try {
                            SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("SELECT data, date, hash FROM stickers_v2 WHERE id = ");
                            stringBuilder.append(type + 1);
                            cursor = database.queryFinalized(stringBuilder.toString(), new Object[0]);
                            if (cursor.next()) {
                                NativeByteBuffer data = cursor.byteBufferValue(0);
                                if (data != null) {
                                    newStickerArray = new ArrayList();
                                    int count = data.readInt32(false);
                                    for (int a = 0; a < count; a++) {
                                        newStickerArray.add(TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false));
                                    }
                                    data.reuse();
                                }
                                date = cursor.intValue(1);
                                hash = DataQuery.calcStickersHash(newStickerArray);
                            }
                        } catch (Throwable th) {
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        }
                    }
                });
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
                int hash = 0;
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (response instanceof TL_messages_allStickers) {
                                    DataQuery.this.processLoadStickersResponse(type, (TL_messages_allStickers) response);
                                } else {
                                    DataQuery.this.processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), hash);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void putStickersToCache(int type, ArrayList<TL_messages_stickerSet> stickers, int date, int hash) {
        final ArrayList<TL_messages_stickerSet> stickersFinal = stickers != null ? new ArrayList(stickers) : null;
        final int i = type;
        final int i2 = date;
        final int i3 = hash;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state;
                    if (stickersFinal != null) {
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                        state.requery();
                        int a = 0;
                        int size = 4;
                        for (int a2 = 0; a2 < stickersFinal.size(); a2++) {
                            size += ((TL_messages_stickerSet) stickersFinal.get(a2)).getObjectSize();
                        }
                        NativeByteBuffer data = new NativeByteBuffer(size);
                        data.writeInt32(stickersFinal.size());
                        while (a < stickersFinal.size()) {
                            ((TL_messages_stickerSet) stickersFinal.get(a)).serializeToStream(data);
                            a++;
                        }
                        state.bindInteger(1, i + 1);
                        state.bindByteBuffer(2, data);
                        state.bindInteger(3, i2);
                        state.bindInteger(4, i3);
                        state.step();
                        data.reuse();
                        state.dispose();
                    } else {
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                        state.requery();
                        state.bindInteger(1, i2);
                        state.step();
                        state.dispose();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
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
                    return attribute.stickerset.id;
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

    private void processLoadedStickers(final int type, ArrayList<TL_messages_stickerSet> res, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                DataQuery.this.loadingStickers[type] = false;
                DataQuery.this.stickersLoaded[type] = true;
            }
        });
        final boolean z = cache;
        final ArrayList<TL_messages_stickerSet> arrayList = res;
        final int i = date;
        final int i2 = hash;
        final int i3 = type;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.DataQuery$28$1 */
            class C01151 implements Runnable {
                C01151() {
                }

                public void run() {
                    if (!(arrayList == null || i2 == 0)) {
                        DataQuery.this.loadHash[i3] = i2;
                    }
                    DataQuery.this.loadStickers(i3, false, false);
                }
            }

            /* renamed from: org.telegram.messenger.DataQuery$28$3 */
            class C01173 implements Runnable {
                C01173() {
                }

                public void run() {
                    DataQuery.this.loadDate[i3] = i;
                }
            }

            public void run() {
                long j = 1000;
                if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
                    Runnable c01151 = new C01151();
                    if (arrayList != null || z) {
                        j = 0;
                    }
                    AndroidUtilities.runOnUIThread(c01151, j);
                    if (arrayList == null) {
                        return;
                    }
                }
                if (arrayList != null) {
                    try {
                        HashMap<String, ArrayList<Document>> allStickersNew;
                        ArrayList<TL_messages_stickerSet> stickerSetsNew = new ArrayList();
                        LongSparseArray<TL_messages_stickerSet> stickerSetsByIdNew = new LongSparseArray();
                        HashMap<String, TL_messages_stickerSet> stickerSetsByNameNew = new HashMap();
                        LongSparseArray<String> stickersByEmojiNew = new LongSparseArray();
                        LongSparseArray<Document> stickersByIdNew = new LongSparseArray();
                        HashMap<String, ArrayList<Document>> allStickersNew2 = new HashMap();
                        int a = 0;
                        while (a < arrayList.size()) {
                            TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) arrayList.get(a);
                            if (stickerSet == null) {
                                allStickersNew = allStickersNew2;
                            } else {
                                int b;
                                stickerSetsNew.add(stickerSet);
                                stickerSetsByIdNew.put(stickerSet.set.id, stickerSet);
                                stickerSetsByNameNew.put(stickerSet.set.short_name, stickerSet);
                                for (b = 0; b < stickerSet.documents.size(); b++) {
                                    Document document = (Document) stickerSet.documents.get(b);
                                    if (document != null) {
                                        if (!(document instanceof TL_documentEmpty)) {
                                            stickersByIdNew.put(document.id, document);
                                        }
                                    }
                                }
                                if (!stickerSet.set.archived) {
                                    b = 0;
                                    while (b < stickerSet.packs.size()) {
                                        TL_messages_stickerSet stickerSet2;
                                        TL_stickerPack stickerPack = (TL_stickerPack) stickerSet.packs.get(b);
                                        if (stickerPack != null) {
                                            if (stickerPack.emoticon == null) {
                                                stickerSet2 = stickerSet;
                                                allStickersNew = allStickersNew2;
                                                b++;
                                                stickerSet = stickerSet2;
                                                allStickersNew2 = allStickersNew;
                                            } else {
                                                stickerPack.emoticon = stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                                ArrayList<Document> arrayList = (ArrayList) allStickersNew2.get(stickerPack.emoticon);
                                                if (arrayList == null) {
                                                    arrayList = new ArrayList();
                                                    allStickersNew2.put(stickerPack.emoticon, arrayList);
                                                }
                                                int c = 0;
                                                while (c < stickerPack.documents.size()) {
                                                    Long id = (Long) stickerPack.documents.get(c);
                                                    stickerSet2 = stickerSet;
                                                    if (stickersByEmojiNew.indexOfKey(id.longValue()) < 0) {
                                                        allStickersNew = allStickersNew2;
                                                        stickersByEmojiNew.put(id.longValue(), stickerPack.emoticon);
                                                    } else {
                                                        allStickersNew = allStickersNew2;
                                                    }
                                                    Document sticker = (Document) stickersByIdNew.get(id.longValue());
                                                    if (sticker != null) {
                                                        arrayList.add(sticker);
                                                    }
                                                    c++;
                                                    stickerSet = stickerSet2;
                                                    allStickersNew2 = allStickersNew;
                                                }
                                            }
                                        }
                                        stickerSet2 = stickerSet;
                                        allStickersNew = allStickersNew2;
                                        b++;
                                        stickerSet = stickerSet2;
                                        allStickersNew2 = allStickersNew;
                                    }
                                }
                                allStickersNew = allStickersNew2;
                            }
                            a++;
                            allStickersNew2 = allStickersNew;
                        }
                        allStickersNew = allStickersNew2;
                        if (!z) {
                            DataQuery.this.putStickersToCache(i3, stickerSetsNew, i, i2);
                        }
                        final LongSparseArray<TL_messages_stickerSet> longSparseArray = stickerSetsByIdNew;
                        final HashMap<String, TL_messages_stickerSet> hashMap = stickerSetsByNameNew;
                        final ArrayList<TL_messages_stickerSet> arrayList2 = stickerSetsNew;
                        final HashMap<String, ArrayList<Document>> hashMap2 = allStickersNew;
                        final LongSparseArray<String> longSparseArray2 = stickersByEmojiNew;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                int a;
                                for (a = 0; a < DataQuery.this.stickerSets[i3].size(); a++) {
                                    StickerSet set = ((TL_messages_stickerSet) DataQuery.this.stickerSets[i3].get(a)).set;
                                    DataQuery.this.stickerSetsById.remove(set.id);
                                    DataQuery.this.installedStickerSetsById.remove(set.id);
                                    DataQuery.this.stickerSetsByName.remove(set.short_name);
                                }
                                for (a = 0; a < longSparseArray.size(); a++) {
                                    DataQuery.this.stickerSetsById.put(longSparseArray.keyAt(a), longSparseArray.valueAt(a));
                                    if (i3 != 3) {
                                        DataQuery.this.installedStickerSetsById.put(longSparseArray.keyAt(a), longSparseArray.valueAt(a));
                                    }
                                }
                                DataQuery.this.stickerSetsByName.putAll(hashMap);
                                DataQuery.this.stickerSets[i3] = arrayList2;
                                DataQuery.this.loadHash[i3] = i2;
                                DataQuery.this.loadDate[i3] = i;
                                if (i3 == 0) {
                                    DataQuery.this.allStickers = hashMap2;
                                    DataQuery.this.stickersByEmoji = longSparseArray2;
                                } else if (i3 == 3) {
                                    DataQuery.this.allStickersFeatured = hashMap2;
                                }
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(i3));
                            }
                        });
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new C01173());
                    DataQuery.this.putStickersToCache(i3, null, i, 0);
                }
            }
        });
    }

    public void removeStickersSet(Context context, StickerSet stickerSet, int hide, BaseFragment baseFragment, boolean showSettings) {
        DataQuery dataQuery = this;
        final StickerSet stickerSet2 = stickerSet;
        int i = hide;
        final boolean type = stickerSet2.masks;
        TL_inputStickerSetID stickerSetID = new TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet2.access_hash;
        stickerSetID.id = stickerSet2.id;
        Context context2;
        if (i != 0) {
            TL_messages_installStickerSet req;
            final boolean z;
            final int i2;
            final BaseFragment baseFragment2;
            final boolean z2;
            boolean z3 = false;
            stickerSet2.archived = i == 1;
            for (int a = 0; a < dataQuery.stickerSets[type].size(); a++) {
                TL_messages_stickerSet set = (TL_messages_stickerSet) dataQuery.stickerSets[type].get(a);
                if (set.set.id == stickerSet2.id) {
                    dataQuery.stickerSets[type].remove(a);
                    if (i == 2) {
                        dataQuery.stickerSets[type].add(0, set);
                    } else {
                        dataQuery.stickerSetsById.remove(set.set.id);
                        dataQuery.installedStickerSetsById.remove(set.set.id);
                        dataQuery.stickerSetsByName.remove(set.set.short_name);
                    }
                    dataQuery.loadHash[type] = calcStickersHash(dataQuery.stickerSets[type]);
                    putStickersToCache(type, dataQuery.stickerSets[type], dataQuery.loadDate[type], dataQuery.loadHash[type]);
                    NotificationCenter.getInstance(dataQuery.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
                    req = new TL_messages_installStickerSet();
                    req.stickerset = stickerSetID;
                    if (i == 1) {
                        z3 = true;
                    }
                    req.archived = z3;
                    z = type;
                    i2 = i;
                    baseFragment2 = baseFragment;
                    z2 = showSettings;
                    ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(req, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.DataQuery$29$2 */
                        class C01192 implements Runnable {
                            C01192() {
                            }

                            public void run() {
                                DataQuery.this.loadStickers(z, false, false);
                            }
                        }

                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (response instanceof TL_messages_stickerSetInstallResultArchive) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(z));
                                        if (i2 != 1 && baseFragment2 != null && baseFragment2.getParentActivity() != null) {
                                            baseFragment2.showDialog(new StickersArchiveAlert(baseFragment2.getParentActivity(), z2 ? baseFragment2 : null, ((TL_messages_stickerSetInstallResultArchive) response).sets).create());
                                        }
                                    }
                                }
                            });
                            AndroidUtilities.runOnUIThread(new C01192(), 1000);
                        }
                    });
                    context2 = context;
                    return;
                }
            }
            dataQuery.loadHash[type] = calcStickersHash(dataQuery.stickerSets[type]);
            putStickersToCache(type, dataQuery.stickerSets[type], dataQuery.loadDate[type], dataQuery.loadHash[type]);
            NotificationCenter.getInstance(dataQuery.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
            req = new TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            if (i == 1) {
                z3 = true;
            }
            req.archived = z3;
            z = type;
            i2 = i;
            baseFragment2 = baseFragment;
            z2 = showSettings;
            ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(req, /* anonymous class already generated */);
            context2 = context;
            return;
        }
        TL_messages_uninstallStickerSet req2 = new TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        context2 = context;
        ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            if (error != null) {
                                Toast.makeText(context2, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
                            } else if (stickerSet2.masks) {
                                Toast.makeText(context2, LocaleController.getString("MasksRemoved", R.string.MasksRemoved), 0).show();
                            } else {
                                Toast.makeText(context2, LocaleController.getString("StickersRemoved", R.string.StickersRemoved), 0).show();
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        DataQuery.this.loadStickers(type, false, true);
                    }
                });
            }
        });
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
        return this.searchResultMessagesMap[mergeDialog].indexOfKey(messageId) >= 0;
    }

    public void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, User user) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false, user);
    }

    private void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal, User user) {
        int max_id;
        long queryWithDialog;
        int i;
        int i2;
        int i3;
        String query2;
        int max_id2;
        long queryWithDialog2;
        int max_id3;
        String query3;
        TLObject tLObject;
        int i4;
        TL_messages_search req;
        long j = mergeDialogId;
        int i5 = direction;
        final TLObject tLObject2 = user;
        long queryWithDialog3 = dialog_id;
        boolean firstQuery = internal ^ 1;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(r12.currentAccount).cancelRequest(r12.reqId, true);
            r12.reqId = 0;
        }
        if (r12.mergeReqId != 0) {
            ConnectionsManager.getInstance(r12.currentAccount).cancelRequest(r12.mergeReqId, true);
            r12.mergeReqId = 0;
        }
        boolean firstQuery2;
        if (query != null) {
            max_id = 0;
            queryWithDialog = queryWithDialog3;
            i = 2;
            if (firstQuery) {
                NotificationCenter instance = NotificationCenter.getInstance(r12.currentAccount);
                int i6 = NotificationCenter.chatSearchResultsLoading;
                i2 = 1;
                Object[] objArr = new Object[1];
                i3 = 0;
                objArr[0] = Integer.valueOf(guid);
                instance.postNotificationName(i6, objArr);
                boolean[] zArr = r12.messagesSearchEndReached;
                r12.messagesSearchEndReached[1] = false;
                zArr[0] = false;
                int[] iArr = r12.messagesSearchCount;
                r12.messagesSearchCount[1] = 0;
                iArr[0] = 0;
                r12.searchResultMessages.clear();
                r12.searchResultMessagesMap[0].clear();
                r12.searchResultMessagesMap[1].clear();
            } else {
                i2 = 1;
                i3 = 0;
            }
            query2 = query;
            firstQuery2 = firstQuery;
            max_id2 = max_id;
        } else if (!r12.searchResultMessages.isEmpty()) {
            if (i5 == 1) {
                r12.lastReturnedNum++;
                if (r12.lastReturnedNum < r12.searchResultMessages.size()) {
                    MessageObject messageObject = (MessageObject) r12.searchResultMessages.get(r12.lastReturnedNum);
                    NotificationCenter instance2 = NotificationCenter.getInstance(r12.currentAccount);
                    i = NotificationCenter.chatSearchResultsAvailable;
                    r7 = new Object[6];
                    max_id = 0;
                    r7[3] = Long.valueOf(messageObject.getDialogId());
                    r7[4] = Integer.valueOf(r12.lastReturnedNum);
                    r7[5] = Integer.valueOf(r12.messagesSearchCount[0] + r12.messagesSearchCount[1]);
                    instance2.postNotificationName(i, r7);
                    return;
                }
                max_id = 0;
                queryWithDialog = queryWithDialog3;
                if (r12.messagesSearchEndReached[0] == 0 || j != 0 || r12.messagesSearchEndReached[1] == 0) {
                    int max_id4 = r12.lastSearchQuery;
                    MessageObject messageObject2 = (MessageObject) r12.searchResultMessages.get(r12.searchResultMessages.size() - 1);
                    if (messageObject2.getDialogId() != dialog_id || r12.messagesSearchEndReached[0]) {
                        if (messageObject2.getDialogId() == j) {
                            max_id = messageObject2.getId();
                        }
                        long queryWithDialog4 = j;
                        i = 1;
                        r12.messagesSearchEndReached[1] = false;
                        queryWithDialog3 = queryWithDialog4;
                    } else {
                        max_id = messageObject2.getId();
                        queryWithDialog3 = dialog_id;
                        i = 1;
                    }
                    query2 = max_id4;
                    queryWithDialog = queryWithDialog3;
                    firstQuery2 = false;
                    i2 = i;
                    max_id2 = max_id;
                    i = 2;
                    i3 = 0;
                } else {
                    r12.lastReturnedNum--;
                    return;
                }
            }
            max_id = 0;
            queryWithDialog = queryWithDialog3;
            if (i5 == 2) {
                r12.lastReturnedNum--;
                if (r12.lastReturnedNum < 0) {
                    r12.lastReturnedNum = 0;
                    return;
                }
                if (r12.lastReturnedNum >= r12.searchResultMessages.size()) {
                    r12.lastReturnedNum = r12.searchResultMessages.size() - 1;
                }
                MessageObject max_id5 = (MessageObject) r12.searchResultMessages.get(r12.lastReturnedNum);
                NotificationCenter.getInstance(r12.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(max_id5.getId()), Integer.valueOf(getMask()), Long.valueOf(max_id5.getDialogId()), Integer.valueOf(r12.lastReturnedNum), Integer.valueOf(r12.messagesSearchCount[0] + r12.messagesSearchCount[1]));
                return;
            }
            return;
        } else {
            return;
        }
        long queryWithDialog5 = (!r12.messagesSearchEndReached[i3] || r12.messagesSearchEndReached[i2] || j == 0) ? queryWithDialog : j;
        if (queryWithDialog5 != dialog_id || !firstQuery) {
            queryWithDialog2 = queryWithDialog5;
            max_id3 = max_id2;
            query3 = query2;
            tLObject = tLObject2;
            i4 = i;
        } else if (j != 0) {
            InputPeer inputPeer = MessagesController.getInstance(r12.currentAccount).getInputPeer((int) j);
            if (inputPeer != null) {
                final TL_messages_search tL_messages_search = new TL_messages_search();
                tL_messages_search.peer = inputPeer;
                r12.lastMergeDialogId = j;
                tL_messages_search.limit = 1;
                tL_messages_search.f49q = query2 != null ? query2 : TtmlNode.ANONYMOUS_REGION_ID;
                if (tLObject2 != null) {
                    tL_messages_search.from_id = MessagesController.getInstance(r12.currentAccount).getInputUser((User) tLObject2);
                    tL_messages_search.flags |= 1;
                }
                tL_messages_search.filter = new TL_inputMessagesFilterEmpty();
                AnonymousClass31 anonymousClass31 = r0;
                ConnectionsManager instance3 = ConnectionsManager.getInstance(r12.currentAccount);
                queryWithDialog5 = j;
                int i7 = i;
                req = tL_messages_search;
                final long j2 = dialog_id;
                i3 = guid;
                final int i8 = i5;
                User user2 = tLObject2;
                AnonymousClass31 anonymousClass312 = new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (DataQuery.this.lastMergeDialogId == queryWithDialog5) {
                                    DataQuery.this.mergeReqId = 0;
                                    if (response != null) {
                                        messages_Messages res = response;
                                        DataQuery.this.messagesSearchEndReached[1] = res.messages.isEmpty();
                                        DataQuery.this.messagesSearchCount[1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                                        DataQuery.this.searchMessagesInChat(tL_messages_search.f49q, j2, queryWithDialog5, i3, i8, true, tLObject2);
                                    }
                                }
                            }
                        });
                    }
                };
                r12.mergeReqId = instance3.sendRequest(req, anonymousClass31, 2);
                return;
            }
            return;
        } else {
            queryWithDialog2 = queryWithDialog5;
            max_id3 = max_id2;
            query3 = query2;
            tLObject = tLObject2;
            i4 = i;
            r12.lastMergeDialogId = 0;
            r12.messagesSearchEndReached[1] = true;
            r12.messagesSearchCount[1] = 0;
        }
        req = new TL_messages_search();
        long queryWithDialog6 = queryWithDialog2;
        req.peer = MessagesController.getInstance(r12.currentAccount).getInputPeer((int) queryWithDialog6);
        if (req.peer != null) {
            req.limit = 21;
            req.f49q = query3 != null ? query3 : TtmlNode.ANONYMOUS_REGION_ID;
            req.offset_id = max_id3;
            if (tLObject != null) {
                req.from_id = MessagesController.getInstance(r12.currentAccount).getInputUser((User) tLObject);
                req.flags |= 1;
            }
            req.filter = new TL_inputMessagesFilterEmpty();
            i2 = r12.lastReqId + 1;
            r12.lastReqId = i2;
            r12.lastSearchQuery = query3;
            queryWithDialog4 = queryWithDialog6;
            String query4 = query3;
            AnonymousClass32 anonymousClass32 = r0;
            final TL_messages_search tL_messages_search2 = req;
            queryWithDialog = queryWithDialog6;
            queryWithDialog6 = dialog_id;
            ConnectionsManager instance4 = ConnectionsManager.getInstance(r12.currentAccount);
            i8 = guid;
            int i9 = i4;
            max_id = max_id3;
            final long j3 = mergeDialogId;
            TL_messages_search req2 = req;
            int i10 = i9;
            final TLObject req3 = tLObject;
            AnonymousClass32 anonymousClass322 = new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (i2 == DataQuery.this.lastReqId) {
                                DataQuery.this.reqId = 0;
                                if (response != null) {
                                    int size;
                                    MessageObject messageObject;
                                    messages_Messages res = response;
                                    int a = 0;
                                    while (a < res.messages.size()) {
                                        Message message = (Message) res.messages.get(a);
                                        if ((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear)) {
                                            res.messages.remove(a);
                                            a--;
                                        }
                                        a++;
                                    }
                                    MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(res.users, false);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(res.chats, false);
                                    if (tL_messages_search2.offset_id == 0 && queryWithDialog4 == queryWithDialog6) {
                                        DataQuery.this.lastReturnedNum = 0;
                                        DataQuery.this.searchResultMessages.clear();
                                        DataQuery.this.searchResultMessagesMap[0].clear();
                                        DataQuery.this.searchResultMessagesMap[1].clear();
                                        DataQuery.this.messagesSearchCount[0] = 0;
                                    }
                                    boolean added = false;
                                    for (a = 0; a < Math.min(res.messages.size(), 20); a++) {
                                        added = true;
                                        MessageObject messageObject2 = new MessageObject(DataQuery.this.currentAccount, (Message) res.messages.get(a), false);
                                        DataQuery.this.searchResultMessages.add(messageObject2);
                                        DataQuery.this.searchResultMessagesMap[queryWithDialog4 == queryWithDialog6 ? 0 : 1].put(messageObject2.getId(), messageObject2);
                                    }
                                    DataQuery.this.messagesSearchEndReached[queryWithDialog4 == queryWithDialog6 ? 0 : 1] = res.messages.size() != 21;
                                    int[] access$4000 = DataQuery.this.messagesSearchCount;
                                    int i = queryWithDialog4 == queryWithDialog6 ? 0 : 1;
                                    if (!(res instanceof TL_messages_messagesSlice)) {
                                        if (!(res instanceof TL_messages_channelMessages)) {
                                            size = res.messages.size();
                                            access$4000[i] = size;
                                            if (DataQuery.this.searchResultMessages.isEmpty()) {
                                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i8), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                            } else if (added) {
                                                if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                                                    DataQuery.this.lastReturnedNum = DataQuery.this.searchResultMessages.size() - 1;
                                                }
                                                messageObject = (MessageObject) DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i8), Integer.valueOf(messageObject.getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]));
                                            }
                                            if (queryWithDialog4 == queryWithDialog6 && DataQuery.this.messagesSearchEndReached[0] && j3 != 0 && !DataQuery.this.messagesSearchEndReached[1]) {
                                                DataQuery.this.searchMessagesInChat(DataQuery.this.lastSearchQuery, queryWithDialog6, j3, i8, 0, true, req3);
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                    size = res.count;
                                    access$4000[i] = size;
                                    if (DataQuery.this.searchResultMessages.isEmpty()) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i8), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    } else if (added) {
                                        if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                                            DataQuery.this.lastReturnedNum = DataQuery.this.searchResultMessages.size() - 1;
                                        }
                                        messageObject = (MessageObject) DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i8), Integer.valueOf(messageObject.getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]));
                                    }
                                    if (queryWithDialog4 == queryWithDialog6) {
                                    }
                                }
                            }
                        }
                    });
                }
            };
            TL_messages_search req4 = req2;
            this.reqId = instance4.sendRequest(req4, anonymousClass32, i10);
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    public void loadMedia(long uid, int count, int max_id, int type, boolean fromCache, int classGuid) {
        long j = uid;
        int i = type;
        final boolean isChannel = ((int) j) < 0 && ChatObject.isChannel(-((int) j), this.currentAccount);
        int lower_part = (int) j;
        if (fromCache) {
            lower_part = classGuid;
        } else if (lower_part == 0) {
            int i2 = lower_part;
            lower_part = classGuid;
        } else {
            TL_messages_search req = new TL_messages_search();
            req.limit = count + 1;
            int i3 = max_id;
            req.offset_id = i3;
            if (i == 0) {
                req.filter = new TL_inputMessagesFilterPhotoVideo();
            } else if (i == 1) {
                req.filter = new TL_inputMessagesFilterDocument();
            } else if (i == 2) {
                req.filter = new TL_inputMessagesFilterVoice();
            } else if (i == 3) {
                req.filter = new TL_inputMessagesFilterUrl();
            } else if (i == 4) {
                req.filter = new TL_inputMessagesFilterMusic();
            }
            req.f49q = TtmlNode.ANONYMOUS_REGION_ID;
            req.peer = MessagesController.getInstance(r9.currentAccount).getInputPeer(lower_part);
            if (req.peer != null) {
                final int i4 = count;
                final long j2 = j;
                AnonymousClass33 anonymousClass33 = r0;
                final int i5 = i3;
                ConnectionsManager instance = ConnectionsManager.getInstance(r9.currentAccount);
                final int i6 = i;
                i3 = classGuid;
                AnonymousClass33 anonymousClass332 = new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            boolean topReached;
                            messages_Messages res = (messages_Messages) response;
                            if (res.messages.size() > i4) {
                                res.messages.remove(res.messages.size() - 1);
                                topReached = false;
                            } else {
                                topReached = true;
                            }
                            DataQuery.this.processLoadedMedia(res, j2, i4, i5, i6, false, i3, isChannel, topReached);
                        }
                    }
                };
                ConnectionsManager.getInstance(r9.currentAccount).bindRequestToGuid(instance.sendRequest(req, anonymousClass33), classGuid);
            }
            return;
        }
        loadMediaDatabase(j, count, max_id, i, lower_part, isChannel);
    }

    public void getMediaCount(long uid, int type, int classGuid, boolean fromCache) {
        int lower_part = (int) uid;
        if (!fromCache) {
            if (lower_part != 0) {
                TL_messages_search req = new TL_messages_search();
                req.limit = 1;
                req.offset_id = 0;
                if (type == 0) {
                    req.filter = new TL_inputMessagesFilterPhotoVideo();
                } else if (type == 1) {
                    req.filter = new TL_inputMessagesFilterDocument();
                } else if (type == 2) {
                    req.filter = new TL_inputMessagesFilterVoice();
                } else if (type == 3) {
                    req.filter = new TL_inputMessagesFilterUrl();
                } else if (type == 4) {
                    req.filter = new TL_inputMessagesFilterMusic();
                }
                req.f49q = TtmlNode.ANONYMOUS_REGION_ID;
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
                if (req.peer != null) {
                    final long j = uid;
                    final int i = type;
                    final int i2 = classGuid;
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                int size;
                                final messages_Messages res = (messages_Messages) response;
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                if (res instanceof TL_messages_messages) {
                                    size = res.messages.size();
                                } else {
                                    size = res.count;
                                }
                                int count = size;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(res.users, false);
                                        MessagesController.getInstance(DataQuery.this.currentAccount).putChats(res.chats, false);
                                    }
                                });
                                DataQuery.this.processLoadedMediaCount(count, j, i, i2, false);
                            }
                        }
                    }), classGuid);
                }
                return;
            }
        }
        getMediaCountDatabase(uid, type, classGuid);
    }

    public static int getMediaType(Message message) {
        if (message == null) {
            return -1;
        }
        int a = 0;
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
                    if (MessageObject.isMusicMessage(message)) {
                        return 4;
                    }
                    return 1;
                }
            }
            return 2;
        }
        if (!message.entities.isEmpty()) {
            while (true) {
                int a2 = a;
                if (a2 >= message.entities.size()) {
                    break;
                }
                MessageEntity entity = (MessageEntity) message.entities.get(a2);
                if ((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl)) {
                    break;
                } else if (entity instanceof TL_messageEntityEmail) {
                    break;
                } else {
                    a = a2 + 1;
                }
            }
            return 3;
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(Message message) {
        if ((message instanceof TL_message_secret) && (((message.media instanceof TL_messageMediaPhoto) || MessageObject.isVideoMessage(message) || MessageObject.isGifMessage(message)) && message.media.ttl_seconds != 0 && message.media.ttl_seconds <= 60)) {
            return false;
        }
        if (!(message instanceof TL_message_secret) && (message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return false;
        }
        if (!(message.media instanceof TL_messageMediaPhoto)) {
            if (!(message.media instanceof TL_messageMediaDocument) || MessageObject.isGifDocument(message.media.document)) {
                if (!message.entities.isEmpty()) {
                    int a = 0;
                    while (a < message.entities.size()) {
                        MessageEntity entity = (MessageEntity) message.entities.get(a);
                        if (!((entity instanceof TL_messageEntityUrl) || (entity instanceof TL_messageEntityTextUrl))) {
                            if (!(entity instanceof TL_messageEntityEmail)) {
                                a++;
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

    private void processLoadedMedia(messages_Messages res, long uid, int count, int max_id, int type, boolean fromCache, int classGuid, boolean isChannel, boolean topReached) {
        DataQuery dataQuery = this;
        messages_Messages messages_messages = res;
        long j = uid;
        int lower_part = (int) j;
        if (fromCache && messages_messages.messages.isEmpty() && lower_part != 0) {
            loadMedia(j, count, max_id, type, false, classGuid);
            return;
        }
        if (!fromCache) {
            ImageLoader.saveMessagesThumbs(messages_messages.messages);
            MessagesStorage.getInstance(dataQuery.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            putMediaDatabase(j, type, messages_messages.messages, max_id, topReached);
        }
        SparseArray<User> usersDict = new SparseArray();
        int a = 0;
        for (int a2 = 0; a2 < messages_messages.users.size(); a2++) {
            User u = (User) messages_messages.users.get(a2);
            usersDict.put(u.id, u);
        }
        ArrayList<MessageObject> objects = new ArrayList();
        while (a < messages_messages.messages.size()) {
            objects.add(new MessageObject(dataQuery.currentAccount, (Message) messages_messages.messages.get(a), (SparseArray) usersDict, true));
            a++;
        }
        final messages_Messages messages_messages2 = messages_messages;
        final boolean z = fromCache;
        final long j2 = j;
        final ArrayList<MessageObject> arrayList = objects;
        AnonymousClass35 anonymousClass35 = r0;
        final int i = classGuid;
        final int i2 = type;
        usersDict = topReached;
        AnonymousClass35 anonymousClass352 = new Runnable() {
            public void run() {
                int totalCount = messages_messages2.count;
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(messages_messages2.users, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(messages_messages2.chats, z);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(j2), Integer.valueOf(totalCount), arrayList, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(usersDict));
            }
        };
        AndroidUtilities.runOnUIThread(anonymousClass35);
    }

    private void processLoadedMediaCount(int count, long uid, int type, int classGuid, boolean fromCache) {
        final long j = uid;
        final boolean z = fromCache;
        final int i = count;
        final int i2 = type;
        final int i3 = classGuid;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int lower_part = (int) j;
                if (z && i == -1 && lower_part != 0) {
                    DataQuery.this.getMediaCount(j, i2, i3, false);
                    return;
                }
                if (!z) {
                    DataQuery.this.putMediaCountDatabase(j, i2, i);
                }
                NotificationCenter instance = NotificationCenter.getInstance(DataQuery.this.currentAccount);
                int i = NotificationCenter.mediaCountDidLoaded;
                Object[] objArr = new Object[4];
                int i2 = 0;
                objArr[0] = Long.valueOf(j);
                if (!z || i != -1) {
                    i2 = i;
                }
                objArr[1] = Integer.valueOf(i2);
                objArr[2] = Boolean.valueOf(z);
                objArr[3] = Integer.valueOf(i2);
                instance.postNotificationName(i, objArr);
            }
        });
    }

    private void putMediaCountDatabase(long uid, int type, int count) {
        final long j = uid;
        final int i = type;
        final int i2 = count;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
                    state2.requery();
                    state2.bindLong(1, j);
                    state2.bindInteger(2, i);
                    state2.bindInteger(3, i2);
                    state2.step();
                    state2.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void getMediaCountDatabase(long uid, int type, int classGuid) {
        final long j = uid;
        final int i = type;
        final int i2 = classGuid;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                int count = -1;
                try {
                    SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                    if (cursor.next()) {
                        count = cursor.intValue(0);
                    }
                    cursor.dispose();
                    int lower_part = (int) j;
                    if (count == -1 && lower_part == 0) {
                        cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                        if (cursor.next()) {
                            count = cursor.intValue(0);
                        }
                        cursor.dispose();
                        if (count != -1) {
                            DataQuery.this.putMediaCountDatabase(j, i, count);
                        }
                    }
                    DataQuery.this.processLoadedMediaCount(count, j, i, i2, true);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void loadMediaDatabase(long uid, int count, int max_id, int type, int classGuid, boolean isChannel) {
        final int i = count;
        final long j = uid;
        final int i2 = max_id;
        final boolean z = isChannel;
        final int i3 = type;
        final int i4 = classGuid;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                Throwable e;
                boolean topReached;
                messages_Messages res;
                Throwable e2;
                messages_Messages res2;
                AnonymousClass39 anonymousClass39 = this;
                messages_Messages res3 = new TL_messages_messages();
                try {
                    ArrayList<Integer> arrayList;
                    ArrayList<Integer> arrayList2;
                    SQLiteCursor cursor;
                    ArrayList<Integer> usersToLoad;
                    ArrayList<Integer> chatsToLoad;
                    boolean topReached2;
                    ArrayList<Integer> usersToLoad2 = new ArrayList();
                    ArrayList<Integer> chatsToLoad2 = new ArrayList();
                    int countToLoad = i + 1;
                    SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                    boolean z = false;
                    if (((int) j) != 0) {
                        int channelId = 0;
                        try {
                            SQLiteDatabase database2;
                            long messageMaxId = (long) i2;
                            if (z) {
                                database2 = database;
                                try {
                                    channelId = -((int) j);
                                } catch (Exception e3) {
                                    e = e3;
                                    topReached = false;
                                    res = res3;
                                    e2 = e;
                                    try {
                                        res.messages.clear();
                                        res.chats.clear();
                                        res.users.clear();
                                        FileLog.m3e(e2);
                                        DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                    } catch (Throwable th) {
                                        e = th;
                                        e2 = e;
                                        DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                        throw e2;
                                    }
                                } catch (Throwable th2) {
                                    e = th2;
                                    topReached = false;
                                    res = res3;
                                    e2 = e;
                                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                    throw e2;
                                }
                            }
                            database2 = database;
                            if (!(messageMaxId == 0 || channelId == 0)) {
                                messageMaxId |= ((long) channelId) << 32;
                            }
                            database = database2;
                            SQLiteCursor cursor2 = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                            if (cursor2.next()) {
                                z = cursor2.intValue(0) == 1;
                                cursor2.dispose();
                                topReached = false;
                                arrayList = usersToLoad2;
                                arrayList2 = chatsToLoad2;
                            } else {
                                cursor2.dispose();
                                Locale locale = Locale.US;
                                String str = "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0";
                                topReached = false;
                                try {
                                    Object[] objArr = new Object[2];
                                    arrayList = usersToLoad2;
                                    arrayList2 = chatsToLoad2;
                                    objArr[0] = Long.valueOf(j);
                                    objArr[1] = Integer.valueOf(i3);
                                    cursor2 = database.queryFinalized(String.format(locale, str, objArr), new Object[0]);
                                    if (cursor2.next()) {
                                        try {
                                            int mid = cursor2.intValue(0);
                                            if (mid != 0) {
                                                SQLitePreparedStatement state = database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                                                state.requery();
                                                state.bindLong(1, j);
                                                state.bindInteger(2, i3);
                                                state.bindInteger(3, 0);
                                                state.bindInteger(4, mid);
                                                state.step();
                                                state.dispose();
                                            }
                                        } catch (Throwable e4) {
                                            e2 = e4;
                                            res = res3;
                                            res.messages.clear();
                                            res.chats.clear();
                                            res.users.clear();
                                            FileLog.m3e(e2);
                                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                        } catch (Throwable e42) {
                                            e2 = e42;
                                            res = res3;
                                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                            throw e2;
                                        }
                                    }
                                    cursor2.dispose();
                                } catch (Throwable e422) {
                                    e2 = e422;
                                    res = res3;
                                    res.messages.clear();
                                    res.chats.clear();
                                    res.users.clear();
                                    FileLog.m3e(e2);
                                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                } catch (Throwable e4222) {
                                    e2 = e4222;
                                    res = res3;
                                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                                    throw e2;
                                }
                            }
                            if (messageMaxId != 0) {
                                long holeMessageId;
                                r5 = new Object[3];
                                long holeMessageId2 = 0;
                                r5[0] = Long.valueOf(j);
                                r5[1] = Integer.valueOf(i3);
                                r5[2] = Integer.valueOf(i2);
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", r5), new Object[0]);
                                if (cursor.next()) {
                                    holeMessageId = (long) cursor.intValue(0);
                                    if (channelId != 0) {
                                        holeMessageId |= ((long) channelId) << 32;
                                    }
                                } else {
                                    holeMessageId = holeMessageId2;
                                }
                                cursor.dispose();
                                if (holeMessageId > 1) {
                                    Locale locale2 = Locale.US;
                                    String str2 = "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d";
                                    Object[] objArr2 = new Object[5];
                                    res2 = res3;
                                    try {
                                        objArr2[0] = Long.valueOf(j);
                                        objArr2[1] = Long.valueOf(messageMaxId);
                                        objArr2[2] = Long.valueOf(holeMessageId);
                                        objArr2[3] = Integer.valueOf(i3);
                                        objArr2[4] = Integer.valueOf(countToLoad);
                                        cursor = database.queryFinalized(String.format(locale2, str2, objArr2), new Object[0]);
                                    } catch (Throwable e42222) {
                                        e2 = e42222;
                                        res = res2;
                                    } catch (Throwable e422222) {
                                        e2 = e422222;
                                        res = res2;
                                    }
                                } else {
                                    SQLiteCursor sQLiteCursor = cursor;
                                    res2 = res3;
                                    r4 = new Object[4];
                                    r4[0] = Long.valueOf(j);
                                    r4[1] = Long.valueOf(messageMaxId);
                                    r4[2] = Integer.valueOf(i3);
                                    r4[3] = Integer.valueOf(countToLoad);
                                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", r4), new Object[0]);
                                }
                            } else {
                                TL_messages_messages res4;
                                res2 = res3;
                                r14 = new Object[2];
                                long holeMessageId3 = 0;
                                r14[0] = Long.valueOf(j);
                                r14[1] = Integer.valueOf(i3);
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", r14), new Object[0]);
                                if (cursor.next()) {
                                    res4 = (long) cursor.intValue(0);
                                    if (channelId != 0) {
                                        res4 |= ((long) channelId) << 32;
                                    }
                                } else {
                                    res4 = holeMessageId3;
                                }
                                cursor.dispose();
                                if (res4 > 1) {
                                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(res4), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                } else {
                                    cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                                }
                            }
                        } catch (Throwable e4222222) {
                            topReached = false;
                            e2 = e4222222;
                            res = res3;
                            res.messages.clear();
                            res.chats.clear();
                            res.users.clear();
                            FileLog.m3e(e2);
                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                        } catch (Throwable e42222222) {
                            topReached = false;
                            e2 = e42222222;
                            res = res3;
                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                            throw e2;
                        }
                    }
                    topReached = false;
                    res2 = res3;
                    arrayList = usersToLoad2;
                    arrayList2 = chatsToLoad2;
                    z = true;
                    try {
                        if (i2 != 0) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                        }
                    } catch (Throwable e422222222) {
                        res = res2;
                        e2 = e422222222;
                        res.messages.clear();
                        res.chats.clear();
                        res.users.clear();
                        FileLog.m3e(e2);
                        DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                    } catch (Throwable eNUM) {
                        res = res2;
                        e2 = eNUM;
                        DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                        throw e2;
                    }
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                            data.reuse();
                            message.id = cursor.intValue(1);
                            message.dialog_id = j;
                            if (((int) j) == 0) {
                                message.random_id = cursor.longValue(2);
                            }
                            res = res2;
                            try {
                                res.messages.add(message);
                                if (message.from_id > 0) {
                                    usersToLoad = arrayList;
                                    if (!usersToLoad.contains(Integer.valueOf(message.from_id))) {
                                        usersToLoad.add(Integer.valueOf(message.from_id));
                                    }
                                    chatsToLoad = arrayList2;
                                } else {
                                    usersToLoad = arrayList;
                                    chatsToLoad = arrayList2;
                                    if (!chatsToLoad.contains(Integer.valueOf(-message.from_id))) {
                                        chatsToLoad.add(Integer.valueOf(-message.from_id));
                                    }
                                }
                            } catch (Exception e5) {
                                eNUM = e5;
                            }
                        } else {
                            usersToLoad = arrayList;
                            chatsToLoad = arrayList2;
                            res = res2;
                        }
                        arrayList = usersToLoad;
                        arrayList2 = chatsToLoad;
                        res2 = res;
                    }
                    usersToLoad = arrayList;
                    chatsToLoad = arrayList2;
                    res = res2;
                    cursor.dispose();
                    if (!usersToLoad.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), res.users);
                    }
                    if (!chatsToLoad.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), res.chats);
                    }
                    if (res.messages.size() > i) {
                        try {
                            res.messages.remove(res.messages.size() - 1);
                            topReached2 = false;
                        } catch (Throwable e4NUM) {
                            e2 = e4NUM;
                            topReached = false;
                            res.messages.clear();
                            res.chats.clear();
                            res.users.clear();
                            FileLog.m3e(e2);
                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                        } catch (Throwable e42NUM) {
                            e2 = e42NUM;
                            topReached = false;
                            DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                            throw e2;
                        }
                    }
                    topReached2 = z;
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached2);
                } catch (Throwable e422NUM) {
                    topReached = false;
                    res = res3;
                    e2 = e422NUM;
                    res.messages.clear();
                    res.chats.clear();
                    res.users.clear();
                    FileLog.m3e(e2);
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                } catch (Throwable e4222NUM) {
                    topReached = false;
                    res = res3;
                    e2 = e4222NUM;
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                    throw e2;
                }
            }
        });
    }

    private void putMediaDatabase(long uid, int type, ArrayList<Message> messages, int max_id, boolean topReached) {
        final ArrayList<Message> arrayList = messages;
        final boolean z = topReached;
        final long j = uid;
        final int i = max_id;
        final int i2 = type;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    int i;
                    if (arrayList.isEmpty() || z) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).doneHolesInMedia(j, i, i2);
                        if (arrayList.isEmpty()) {
                            return;
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    Iterator it = arrayList.iterator();
                    while (true) {
                        i = 1;
                        if (!it.hasNext()) {
                            break;
                        }
                        Message message = (Message) it.next();
                        if (DataQuery.canAddMessageToMedia(message)) {
                            long messageId = (long) message.id;
                            if (message.to_id.channel_id != 0) {
                                messageId |= ((long) message.to_id.channel_id) << 32;
                            }
                            state2.requery();
                            NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                            message.serializeToStream(data);
                            state2.bindLong(1, messageId);
                            state2.bindLong(2, j);
                            state2.bindInteger(3, message.date);
                            state2.bindInteger(4, i2);
                            state2.bindByteBuffer(5, data);
                            state2.step();
                            data.reuse();
                        }
                    }
                    state2.dispose();
                    if (!(z && i == 0)) {
                        if (!z) {
                            i = ((Message) arrayList.get(arrayList.size() - 1)).id;
                        }
                        int minId = i;
                        if (i != 0) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j, minId, i, i2);
                        } else {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j, minId, ConnectionsManager.DEFAULT_DATACENTER_ID, i2);
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadMusic(long uid, long max_id) {
        final long j = uid;
        final long j2 = max_id;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                final ArrayList<MessageObject> arrayList = new ArrayList();
                try {
                    SQLiteCursor cursor;
                    if (((int) j) != 0) {
                        cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
                    } else {
                        cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(4)}), new Object[0]);
                    }
                    while (cursor.next()) {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                            message.readAttachPath(data, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                            data.reuse();
                            if (MessageObject.isMusicMessage(message)) {
                                message.id = cursor.intValue(1);
                                message.dialog_id = j;
                                arrayList.add(0, new MessageObject(DataQuery.this.currentAccount, message, false));
                            }
                        }
                    }
                    cursor.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.musicDidLoaded, Long.valueOf(j), arrayList);
                    }
                });
            }
        });
    }

    public void buildShortcuts() {
        if (VERSION.SDK_INT >= 25) {
            final ArrayList<TL_topPeer> hintsFinal = new ArrayList();
            for (int a = 0; a < this.hints.size(); a++) {
                hintsFinal.add(this.hints.get(a));
                if (hintsFinal.size() == 3) {
                    break;
                }
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    List<ShortcutInfo> currentShortcuts;
                    ArrayList<String> newShortcutsIds;
                    ArrayList<String> shortcutsToDelete;
                    int a;
                    Intent intent;
                    String formatName;
                    Bitmap bitmap;
                    Throwable th;
                    FileLocation fileLocation;
                    TL_topPeer tL_topPeer;
                    User user;
                    Chat chat;
                    Throwable e;
                    String id;
                    Builder builder;
                    AnonymousClass42 anonymousClass42 = this;
                    try {
                        ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                        List<ShortcutInfo> currentShortcuts2 = shortcutManager.getDynamicShortcuts();
                        ArrayList<String> shortcutsToUpdate = new ArrayList();
                        ArrayList<String> newShortcutsIds2 = new ArrayList();
                        ArrayList<String> shortcutsToDelete2 = new ArrayList();
                        int a2 = 0;
                        if (!(currentShortcuts2 == null || currentShortcuts2.isEmpty())) {
                            int a3;
                            newShortcutsIds2.add("compose");
                            for (a3 = 0; a3 < hintsFinal.size(); a3++) {
                                long did;
                                TL_topPeer hint = (TL_topPeer) hintsFinal.get(a3);
                                if (hint.peer.user_id != 0) {
                                    did = (long) hint.peer.user_id;
                                } else {
                                    did = (long) (-hint.peer.chat_id);
                                    if (did == 0) {
                                        did = (long) (-hint.peer.channel_id);
                                    }
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("did");
                                stringBuilder.append(did);
                                newShortcutsIds2.add(stringBuilder.toString());
                            }
                            for (a3 = 0; a3 < currentShortcuts2.size(); a3++) {
                                String id2 = ((ShortcutInfo) currentShortcuts2.get(a3)).getId();
                                if (!newShortcutsIds2.remove(id2)) {
                                    shortcutsToDelete2.add(id2);
                                }
                                shortcutsToUpdate.add(id2);
                            }
                            if (newShortcutsIds2.isEmpty() && shortcutsToDelete2.isEmpty()) {
                                return;
                            }
                        }
                        Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent2.setAction("new_dialog");
                        ArrayList<ShortcutInfo> arrayList = new ArrayList();
                        arrayList.add(new Builder(ApplicationLoader.applicationContext, "compose").setShortLabel(LocaleController.getString("NewConversationShortcut", R.string.NewConversationShortcut)).setLongLabel(LocaleController.getString("NewConversationShortcut", R.string.NewConversationShortcut)).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_compose)).setIntent(intent2).build());
                        if (shortcutsToUpdate.contains("compose")) {
                            shortcutManager.updateShortcuts(arrayList);
                        } else {
                            shortcutManager.addDynamicShortcuts(arrayList);
                        }
                        arrayList.clear();
                        if (!shortcutsToDelete2.isEmpty()) {
                            shortcutManager.removeDynamicShortcuts(shortcutsToDelete2);
                        }
                        while (a2 < hintsFinal.size()) {
                            long did2;
                            Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                            TL_topPeer hint2 = (TL_topPeer) hintsFinal.get(a2);
                            User user2 = null;
                            Chat chat2 = null;
                            if (hint2.peer.user_id != 0) {
                                shortcutIntent.putExtra("userId", hint2.peer.user_id);
                                user2 = MessagesController.getInstance(DataQuery.this.currentAccount).getUser(Integer.valueOf(hint2.peer.user_id));
                                did2 = (long) hint2.peer.user_id;
                                currentShortcuts = currentShortcuts2;
                            } else {
                                int chat_id = hint2.peer.chat_id;
                                if (chat_id == 0) {
                                    chat_id = hint2.peer.channel_id;
                                }
                                currentShortcuts = currentShortcuts2;
                                chat2 = MessagesController.getInstance(DataQuery.this.currentAccount).getChat(Integer.valueOf(chat_id));
                                shortcutIntent.putExtra("chatId", chat_id);
                                did2 = (long) (-chat_id);
                            }
                            if (user2 == null && chat2 == null) {
                                newShortcutsIds = newShortcutsIds2;
                                shortcutsToDelete = shortcutsToDelete2;
                                a = a2;
                                intent = intent2;
                            } else {
                                FileLocation fileLocation2;
                                StringBuilder stringBuilder2;
                                File path;
                                Bitmap bitmap2;
                                int size;
                                Canvas canvas;
                                int i;
                                FileLocation photo;
                                if (user2 != null) {
                                    photo = null;
                                    newShortcutsIds = newShortcutsIds2;
                                    formatName = ContactsController.formatName(user2.first_name, user2.last_name);
                                    if (user2.photo != null) {
                                        fileLocation2 = user2.photo.photo_small;
                                    }
                                    fileLocation2 = photo;
                                    shortcutsToDelete = shortcutsToDelete2;
                                    intent = intent2;
                                    shortcutIntent.putExtra("currentAccount", DataQuery.this.currentAccount);
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("com.tmessages.openchat");
                                    stringBuilder2.append(did2);
                                    shortcutIntent.setAction(stringBuilder2.toString());
                                    shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
                                    bitmap = null;
                                    if (fileLocation2 != null) {
                                        try {
                                            path = FileLoader.getPathToAttach(fileLocation2, true);
                                            bitmap2 = BitmapFactory.decodeFile(path.toString());
                                            if (bitmap2 != null) {
                                                try {
                                                    size = AndroidUtilities.dp(NUM);
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                    fileLocation = fileLocation2;
                                                    a = a2;
                                                    tL_topPeer = hint2;
                                                    user = user2;
                                                    chat = chat2;
                                                    bitmap = bitmap2;
                                                    e = th;
                                                    FileLog.m3e(e);
                                                    id = new StringBuilder();
                                                    id.append("did");
                                                    id.append(did2);
                                                    id = id.toString();
                                                    if (TextUtils.isEmpty(formatName)) {
                                                        formatName = " ";
                                                    }
                                                    builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                                    if (bitmap == null) {
                                                        builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                                                    } else {
                                                        builder.setIcon(Icon.createWithBitmap(bitmap));
                                                    }
                                                    arrayList.add(builder.build());
                                                    if (shortcutsToUpdate.contains(id)) {
                                                        shortcutManager.updateShortcuts(arrayList);
                                                    } else {
                                                        shortcutManager.addDynamicShortcuts(arrayList);
                                                    }
                                                    arrayList.clear();
                                                    a2 = a + 1;
                                                    currentShortcuts2 = currentShortcuts;
                                                    newShortcutsIds2 = newShortcutsIds;
                                                    shortcutsToDelete2 = shortcutsToDelete;
                                                    intent2 = intent;
                                                    anonymousClass42 = this;
                                                }
                                                try {
                                                    fileLocation2 = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                                                    canvas = new Canvas(fileLocation2);
                                                    if (DataQuery.roundPaint == null) {
                                                        try {
                                                            try {
                                                                DataQuery.roundPaint = new Paint(3);
                                                                DataQuery.bitmapRect = new RectF();
                                                                DataQuery.erasePaint = new Paint(1);
                                                            } catch (Throwable th3) {
                                                                a = a2;
                                                                chat = chat2;
                                                                bitmap = bitmap2;
                                                                e = th3;
                                                                FileLog.m3e(e);
                                                                id = new StringBuilder();
                                                                id.append("did");
                                                                id.append(did2);
                                                                id = id.toString();
                                                                if (TextUtils.isEmpty(formatName)) {
                                                                    formatName = " ";
                                                                }
                                                                builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                                                if (bitmap == null) {
                                                                    builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                                                                } else {
                                                                    builder.setIcon(Icon.createWithBitmap(bitmap));
                                                                }
                                                                arrayList.add(builder.build());
                                                                if (shortcutsToUpdate.contains(id)) {
                                                                    shortcutManager.addDynamicShortcuts(arrayList);
                                                                } else {
                                                                    shortcutManager.updateShortcuts(arrayList);
                                                                }
                                                                arrayList.clear();
                                                                a2 = a + 1;
                                                                currentShortcuts2 = currentShortcuts;
                                                                newShortcutsIds2 = newShortcutsIds;
                                                                shortcutsToDelete2 = shortcutsToDelete;
                                                                intent2 = intent;
                                                                anonymousClass42 = this;
                                                            }
                                                            try {
                                                                DataQuery.erasePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                                                                DataQuery.roundPath = new Path();
                                                                a = a2;
                                                                DataQuery.roundPath.addCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.dp(NUM)), Direction.CW);
                                                                DataQuery.roundPath.toggleInverseFillType();
                                                            } catch (Throwable th32) {
                                                                bitmap = bitmap2;
                                                                e = th32;
                                                                FileLog.m3e(e);
                                                                id = new StringBuilder();
                                                                id.append("did");
                                                                id.append(did2);
                                                                id = id.toString();
                                                                if (TextUtils.isEmpty(formatName)) {
                                                                    formatName = " ";
                                                                }
                                                                builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                                                if (bitmap == null) {
                                                                    builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                                                                } else {
                                                                    builder.setIcon(Icon.createWithBitmap(bitmap));
                                                                }
                                                                arrayList.add(builder.build());
                                                                if (shortcutsToUpdate.contains(id)) {
                                                                    shortcutManager.addDynamicShortcuts(arrayList);
                                                                } else {
                                                                    shortcutManager.updateShortcuts(arrayList);
                                                                }
                                                                arrayList.clear();
                                                                a2 = a + 1;
                                                                currentShortcuts2 = currentShortcuts;
                                                                newShortcutsIds2 = newShortcutsIds;
                                                                shortcutsToDelete2 = shortcutsToDelete;
                                                                intent2 = intent;
                                                                anonymousClass42 = this;
                                                            }
                                                        } catch (Throwable th322) {
                                                            a = a2;
                                                            user = user2;
                                                            chat = chat2;
                                                            bitmap = bitmap2;
                                                            e = th322;
                                                            FileLog.m3e(e);
                                                            id = new StringBuilder();
                                                            id.append("did");
                                                            id.append(did2);
                                                            id = id.toString();
                                                            if (TextUtils.isEmpty(formatName)) {
                                                                formatName = " ";
                                                            }
                                                            builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                                            if (bitmap == null) {
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
                                                            a2 = a + 1;
                                                            currentShortcuts2 = currentShortcuts;
                                                            newShortcutsIds2 = newShortcutsIds;
                                                            shortcutsToDelete2 = shortcutsToDelete;
                                                            intent2 = intent;
                                                            anonymousClass42 = this;
                                                        }
                                                    } else {
                                                        i = size;
                                                        a = a2;
                                                        user = user2;
                                                        chat = chat2;
                                                    }
                                                    DataQuery.bitmapRect.set((float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
                                                    canvas.drawBitmap(bitmap2, null, DataQuery.bitmapRect, DataQuery.roundPaint);
                                                    canvas.drawPath(DataQuery.roundPath, DataQuery.erasePaint);
                                                    try {
                                                        canvas.setBitmap(null);
                                                    } catch (Exception e2) {
                                                    }
                                                    bitmap = fileLocation2;
                                                } catch (Throwable th3222) {
                                                    a = a2;
                                                    tL_topPeer = hint2;
                                                    user = user2;
                                                    chat = chat2;
                                                    bitmap = bitmap2;
                                                    e = th3222;
                                                    FileLog.m3e(e);
                                                    id = new StringBuilder();
                                                    id.append("did");
                                                    id.append(did2);
                                                    id = id.toString();
                                                    if (TextUtils.isEmpty(formatName)) {
                                                        formatName = " ";
                                                    }
                                                    builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                                    if (bitmap == null) {
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
                                                    a2 = a + 1;
                                                    currentShortcuts2 = currentShortcuts;
                                                    newShortcutsIds2 = newShortcutsIds;
                                                    shortcutsToDelete2 = shortcutsToDelete;
                                                    intent2 = intent;
                                                    anonymousClass42 = this;
                                                }
                                            } else {
                                                fileLocation = fileLocation2;
                                                a = a2;
                                                tL_topPeer = hint2;
                                                user = user2;
                                                chat = chat2;
                                                bitmap = bitmap2;
                                            }
                                        } catch (Throwable th4) {
                                            th3222 = th4;
                                            fileLocation = fileLocation2;
                                            a = a2;
                                            tL_topPeer = hint2;
                                            user = user2;
                                            chat = chat2;
                                            e = th3222;
                                            FileLog.m3e(e);
                                            id = new StringBuilder();
                                            id.append("did");
                                            id.append(did2);
                                            id = id.toString();
                                            if (TextUtils.isEmpty(formatName)) {
                                                formatName = " ";
                                            }
                                            builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                            if (bitmap == null) {
                                                builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                                            } else {
                                                builder.setIcon(Icon.createWithBitmap(bitmap));
                                            }
                                            arrayList.add(builder.build());
                                            if (shortcutsToUpdate.contains(id)) {
                                                shortcutManager.addDynamicShortcuts(arrayList);
                                            } else {
                                                shortcutManager.updateShortcuts(arrayList);
                                            }
                                            arrayList.clear();
                                            a2 = a + 1;
                                            currentShortcuts2 = currentShortcuts;
                                            newShortcutsIds2 = newShortcutsIds;
                                            shortcutsToDelete2 = shortcutsToDelete;
                                            intent2 = intent;
                                            anonymousClass42 = this;
                                        }
                                    } else {
                                        a = a2;
                                        tL_topPeer = hint2;
                                        user = user2;
                                        chat = chat2;
                                    }
                                    id = new StringBuilder();
                                    id.append("did");
                                    id.append(did2);
                                    id = id.toString();
                                    if (TextUtils.isEmpty(formatName)) {
                                        formatName = " ";
                                    }
                                    builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                    if (bitmap == null) {
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
                                } else {
                                    photo = null;
                                    newShortcutsIds = newShortcutsIds2;
                                    formatName = chat2.title;
                                    if (chat2.photo != null) {
                                        fileLocation2 = chat2.photo.photo_small;
                                    }
                                    fileLocation2 = photo;
                                    shortcutsToDelete = shortcutsToDelete2;
                                    intent = intent2;
                                    shortcutIntent.putExtra("currentAccount", DataQuery.this.currentAccount);
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("com.tmessages.openchat");
                                    stringBuilder2.append(did2);
                                    shortcutIntent.setAction(stringBuilder2.toString());
                                    shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
                                    bitmap = null;
                                    if (fileLocation2 != null) {
                                        a = a2;
                                        tL_topPeer = hint2;
                                        user = user2;
                                        chat = chat2;
                                    } else {
                                        path = FileLoader.getPathToAttach(fileLocation2, true);
                                        bitmap2 = BitmapFactory.decodeFile(path.toString());
                                        if (bitmap2 != null) {
                                            fileLocation = fileLocation2;
                                            a = a2;
                                            tL_topPeer = hint2;
                                            user = user2;
                                            chat = chat2;
                                            bitmap = bitmap2;
                                        } else {
                                            size = AndroidUtilities.dp(NUM);
                                            fileLocation2 = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                                            canvas = new Canvas(fileLocation2);
                                            if (DataQuery.roundPaint == null) {
                                                i = size;
                                                a = a2;
                                                user = user2;
                                                chat = chat2;
                                            } else {
                                                DataQuery.roundPaint = new Paint(3);
                                                DataQuery.bitmapRect = new RectF();
                                                DataQuery.erasePaint = new Paint(1);
                                                DataQuery.erasePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                                                DataQuery.roundPath = new Path();
                                                a = a2;
                                                DataQuery.roundPath.addCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.dp(NUM)), Direction.CW);
                                                DataQuery.roundPath.toggleInverseFillType();
                                            }
                                            DataQuery.bitmapRect.set((float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
                                            canvas.drawBitmap(bitmap2, null, DataQuery.bitmapRect, DataQuery.roundPaint);
                                            canvas.drawPath(DataQuery.roundPath, DataQuery.erasePaint);
                                            canvas.setBitmap(null);
                                            bitmap = fileLocation2;
                                        }
                                    }
                                    id = new StringBuilder();
                                    id.append("did");
                                    id.append(did2);
                                    id = id.toString();
                                    if (TextUtils.isEmpty(formatName)) {
                                        formatName = " ";
                                    }
                                    builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                    if (bitmap == null) {
                                        builder.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.shortcut_user));
                                    } else {
                                        builder.setIcon(Icon.createWithBitmap(bitmap));
                                    }
                                    arrayList.add(builder.build());
                                    if (shortcutsToUpdate.contains(id)) {
                                        shortcutManager.addDynamicShortcuts(arrayList);
                                    } else {
                                        shortcutManager.updateShortcuts(arrayList);
                                    }
                                    arrayList.clear();
                                }
                                shortcutsToDelete = shortcutsToDelete2;
                                intent = intent2;
                                shortcutIntent.putExtra("currentAccount", DataQuery.this.currentAccount);
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("com.tmessages.openchat");
                                stringBuilder2.append(did2);
                                shortcutIntent.setAction(stringBuilder2.toString());
                                shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
                                bitmap = null;
                                if (fileLocation2 != null) {
                                    path = FileLoader.getPathToAttach(fileLocation2, true);
                                    bitmap2 = BitmapFactory.decodeFile(path.toString());
                                    if (bitmap2 != null) {
                                        size = AndroidUtilities.dp(NUM);
                                        fileLocation2 = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                                        canvas = new Canvas(fileLocation2);
                                        if (DataQuery.roundPaint == null) {
                                            DataQuery.roundPaint = new Paint(3);
                                            DataQuery.bitmapRect = new RectF();
                                            DataQuery.erasePaint = new Paint(1);
                                            DataQuery.erasePaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
                                            DataQuery.roundPath = new Path();
                                            a = a2;
                                            DataQuery.roundPath.addCircle((float) (size / 2), (float) (size / 2), (float) ((size / 2) - AndroidUtilities.dp(NUM)), Direction.CW);
                                            DataQuery.roundPath.toggleInverseFillType();
                                        } else {
                                            i = size;
                                            a = a2;
                                            user = user2;
                                            chat = chat2;
                                        }
                                        DataQuery.bitmapRect.set((float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
                                        canvas.drawBitmap(bitmap2, null, DataQuery.bitmapRect, DataQuery.roundPaint);
                                        canvas.drawPath(DataQuery.roundPath, DataQuery.erasePaint);
                                        canvas.setBitmap(null);
                                        bitmap = fileLocation2;
                                    } else {
                                        fileLocation = fileLocation2;
                                        a = a2;
                                        tL_topPeer = hint2;
                                        user = user2;
                                        chat = chat2;
                                        bitmap = bitmap2;
                                    }
                                } else {
                                    a = a2;
                                    tL_topPeer = hint2;
                                    user = user2;
                                    chat = chat2;
                                }
                                id = new StringBuilder();
                                id.append("did");
                                id.append(did2);
                                id = id.toString();
                                if (TextUtils.isEmpty(formatName)) {
                                    formatName = " ";
                                }
                                builder = new Builder(ApplicationLoader.applicationContext, id).setShortLabel(formatName).setLongLabel(formatName).setIntent(shortcutIntent);
                                if (bitmap == null) {
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
                            a2 = a + 1;
                            currentShortcuts2 = currentShortcuts;
                            newShortcutsIds2 = newShortcutsIds;
                            shortcutsToDelete2 = shortcutsToDelete;
                            intent2 = intent;
                            anonymousClass42 = this;
                        }
                    } catch (Throwable th5) {
                    }
                }
            });
        }
    }

    public void loadHints(boolean cache) {
        if (!this.loading) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        if (response instanceof TL_contacts_topPeers) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    final TL_contacts_topPeers topPeers = response;
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(topPeers.users, false);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(topPeers.chats, false);
                                    for (int a = 0; a < topPeers.categories.size(); a++) {
                                        TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) topPeers.categories.get(a);
                                        if (category.category instanceof TL_topPeerCategoryBotsInline) {
                                            DataQuery.this.inlineBots = category.peers;
                                            UserConfig.getInstance(DataQuery.this.currentAccount).botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
                                        } else {
                                            DataQuery.this.hints = category.peers;
                                            int selfUserId = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                                            for (int b = 0; b < DataQuery.this.hints.size(); b++) {
                                                if (((TL_topPeer) DataQuery.this.hints.get(b)).peer.user_id == selfUserId) {
                                                    DataQuery.this.hints.remove(b);
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
                                                MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(topPeers.users, topPeers.chats, false, false);
                                                SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
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
                                                            state.requery();
                                                            state.bindInteger(1, did);
                                                            state.bindInteger(2, type);
                                                            state.bindDouble(3, peer.rating);
                                                            state.bindInteger(4, 0);
                                                            state.step();
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
                        ArrayList<TL_topPeer> hintsNew = new ArrayList();
                        ArrayList<TL_topPeer> inlineBotsNew = new ArrayList();
                        ArrayList<User> users = new ArrayList();
                        ArrayList<Chat> chats = new ArrayList();
                        int selfUserId = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                        try {
                            SQLiteCursor cursor;
                            ArrayList<Integer> usersToLoad = new ArrayList();
                            ArrayList<Integer> chatsToLoad = new ArrayList();
                            int i = 0;
                            SQLiteCursor cursor2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
                            while (true) {
                                cursor = cursor2;
                                if (!cursor.next()) {
                                    break;
                                }
                                int did = cursor.intValue(i);
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
                                cursor2 = cursor;
                                i = 0;
                            }
                            cursor.dispose();
                            if (!usersToLoad.isEmpty()) {
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                            }
                            if (!chatsToLoad.isEmpty()) {
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                            }
                            final ArrayList<User> arrayList = users;
                            final ArrayList<Chat> arrayList2 = chats;
                            final ArrayList<TL_topPeer> arrayList3 = hintsNew;
                            C01261 c01261 = r1;
                            final ArrayList<TL_topPeer> arrayList4 = inlineBotsNew;
                            C01261 c012612 = new Runnable() {
                                public void run() {
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList, true);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList2, true);
                                    DataQuery.this.loading = false;
                                    DataQuery.this.loaded = true;
                                    DataQuery.this.hints = arrayList3;
                                    DataQuery.this.inlineBots = arrayList4;
                                    DataQuery.this.buildShortcuts();
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    if (Math.abs(UserConfig.getInstance(DataQuery.this.currentAccount).lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                                        DataQuery.this.loadHints(false);
                                    }
                                }
                            };
                            AndroidUtilities.runOnUIThread(c01261);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                this.loaded = true;
            }
        }
    }

    public void increaseInlineRaiting(int uid) {
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
        Collections.sort(this.inlineBots, new Comparator<TL_topPeer>() {
            public int compare(TL_topPeer lhs, TL_topPeer rhs) {
                if (lhs.rating > rhs.rating) {
                    return -1;
                }
                if (lhs.rating < rhs.rating) {
                    return 1;
                }
                return 0;
            }
        });
        if (this.inlineBots.size() > 20) {
            this.inlineBots.remove(this.inlineBots.size() - 1);
        }
        savePeer(uid, 1, peer.rating);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public void removeInline(int uid) {
        for (int a = 0; a < this.inlineBots.size(); a++) {
            if (((TL_topPeer) this.inlineBots.get(a)).peer.user_id == uid) {
                this.inlineBots.remove(a);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryBotsInline();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(uid);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                deletePeer(uid, 1);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                return;
            }
        }
    }

    public void increasePeerRaiting(final long did) {
        final int lower_id = (int) did;
        if (lower_id > 0) {
            User user = lower_id > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id)) : null;
            if (user != null) {
                if (!user.bot) {
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                        public void run() {
                            double dt = 0.0d;
                            int lastTime = 0;
                            int lastMid = 0;
                            try {
                                SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(did)}), new Object[0]);
                                if (cursor.next()) {
                                    lastMid = cursor.intValue(0);
                                    lastTime = cursor.intValue(1);
                                }
                                cursor.dispose();
                                if (lastMid > 0 && UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime != 0) {
                                    dt = (double) (lastTime - UserConfig.getInstance(DataQuery.this.currentAccount).ratingLoadTime);
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            final double dtFinal = dt;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.messenger.DataQuery$48$1$1 */
                                class C01301 implements Comparator<TL_topPeer> {
                                    C01301() {
                                    }

                                    public int compare(TL_topPeer lhs, TL_topPeer rhs) {
                                        if (lhs.rating > rhs.rating) {
                                            return -1;
                                        }
                                        if (lhs.rating < rhs.rating) {
                                            return 1;
                                        }
                                        return 0;
                                    }
                                }

                                public void run() {
                                    TL_topPeer peer = null;
                                    for (int a = 0; a < DataQuery.this.hints.size(); a++) {
                                        TL_topPeer p = (TL_topPeer) DataQuery.this.hints.get(a);
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
                                        DataQuery.this.hints.add(peer);
                                    }
                                    peer.rating += Math.exp(dtFinal / ((double) MessagesController.getInstance(DataQuery.this.currentAccount).ratingDecay));
                                    Collections.sort(DataQuery.this.hints, new C01301());
                                    DataQuery.this.savePeer((int) did, 0, peer.rating);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void savePeer(int did, int type, double rating) {
        final int i = did;
        final int i2 = type;
        final double d = rating;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                    state.requery();
                    state.bindInteger(1, i);
                    state.bindInteger(2, i2);
                    state.bindDouble(3, d);
                    state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void deletePeer(final int did, final int type) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("com.tmessages.openchat");
        stringBuilder.append(did);
        shortcutIntent.setAction(stringBuilder.toString());
        shortcutIntent.addFlags(ConnectionsManager.FileTypeFile);
        return shortcutIntent;
    }

    public void installShortcut(long did) {
        Throwable th;
        Bitmap bitmap;
        DataQuery dataQuery = this;
        long j = did;
        try {
            int i;
            int i2;
            Intent shortcutIntent = createIntrnalShortcutIntent(did);
            int lower_id = (int) j;
            int high_id = (int) (j >> 32);
            User user = null;
            Chat chat = null;
            if (lower_id == 0) {
                EncryptedChat encryptedChat = MessagesController.getInstance(dataQuery.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(dataQuery.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                } else {
                    return;
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(dataQuery.currentAccount).getUser(Integer.valueOf(lower_id));
            } else if (lower_id < 0) {
                chat = MessagesController.getInstance(dataQuery.currentAccount).getChat(Integer.valueOf(-lower_id));
            } else {
                i = lower_id;
                i2 = high_id;
                return;
            }
            if (user != null || chat != null) {
                String name;
                StringBuilder stringBuilder;
                Builder pinShortcutInfo;
                Intent addIntent;
                FileLocation fileLocation;
                boolean z;
                FileLocation photo = null;
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
                Bitmap bitmap2 = null;
                if (!selfUser) {
                    if (photo == null) {
                        i = lower_id;
                        i2 = high_id;
                        if (VERSION.SDK_INT < 26) {
                            lower_id = ApplicationLoader.applicationContext;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("sdid_");
                            stringBuilder.append(j);
                            pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                            if (bitmap2 == null) {
                                pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0 && chat.megagroup == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_channel));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            }
                            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                        } else {
                            addIntent = new Intent();
                            if (bitmap2 == null) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0 && chat.megagroup == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_channel));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                            addIntent.putExtra("duplicate", false);
                            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                        }
                    }
                }
                if (!selfUser) {
                    try {
                        bitmap2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                    } catch (Throwable th2) {
                        th = th2;
                        FileLog.m3e(th);
                        if (VERSION.SDK_INT < 26) {
                            lower_id = ApplicationLoader.applicationContext;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("sdid_");
                            stringBuilder.append(j);
                            pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                            if (bitmap2 == null) {
                                pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0) {
                                }
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                            }
                            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                        } else {
                            addIntent = new Intent();
                            if (bitmap2 == null) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0) {
                                }
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                            addIntent.putExtra("duplicate", false);
                            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                        }
                    }
                }
                if (!selfUser) {
                    if (bitmap2 == null) {
                        i = lower_id;
                        i2 = high_id;
                        fileLocation = photo;
                        z = selfUser;
                        if (VERSION.SDK_INT < 26) {
                            addIntent = new Intent();
                            if (bitmap2 == null) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                            } else if (user == null) {
                                if (chat != null) {
                                    if (ChatObject.isChannel(chat) == 0) {
                                    }
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            } else if (user.bot == 0) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                            } else {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                            addIntent.putExtra("duplicate", false);
                            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                        } else {
                            lower_id = ApplicationLoader.applicationContext;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("sdid_");
                            stringBuilder.append(j);
                            pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                            if (bitmap2 == null) {
                                pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                            } else if (user == null) {
                                if (chat != null) {
                                    if (ChatObject.isChannel(chat) == 0) {
                                    }
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            } else if (user.bot == 0) {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                            } else {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            }
                            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                        }
                    }
                }
                try {
                    int size = AndroidUtilities.dp(NUM);
                    Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                    result.eraseColor(0);
                    Canvas canvas = new Canvas(result);
                    if (selfUser) {
                        try {
                            AvatarDrawable avatarDrawable = new AvatarDrawable(user);
                            i = lower_id;
                            try {
                                avatarDrawable.setSavedMessages(1);
                                avatarDrawable.setBounds(0, 0, size, size);
                                avatarDrawable.draw(canvas);
                                i2 = high_id;
                                fileLocation = photo;
                            } catch (Throwable th22) {
                                th = th22;
                                FileLog.m3e(th);
                                if (VERSION.SDK_INT < 26) {
                                    addIntent = new Intent();
                                    if (bitmap2 == null) {
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                                    } else if (user == null) {
                                        if (chat != null) {
                                            if (ChatObject.isChannel(chat) == 0) {
                                            }
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                        }
                                    } else if (user.bot == 0) {
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                    } else {
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                    }
                                    addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                                    addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                                    addIntent.putExtra("duplicate", false);
                                    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                    ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                                } else {
                                    lower_id = ApplicationLoader.applicationContext;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("sdid_");
                                    stringBuilder.append(j);
                                    pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                                    if (bitmap2 == null) {
                                        pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                                    } else if (user == null) {
                                        if (chat != null) {
                                            if (ChatObject.isChannel(chat) == 0) {
                                            }
                                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                        }
                                    } else if (user.bot == 0) {
                                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                    } else {
                                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                    }
                                    ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                                }
                            }
                        } catch (Throwable th222) {
                            i = lower_id;
                            th = th222;
                            i2 = high_id;
                            fileLocation = photo;
                            z = selfUser;
                            FileLog.m3e(th);
                            if (VERSION.SDK_INT < 26) {
                                addIntent = new Intent();
                                if (bitmap2 == null) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                                } else if (user == null) {
                                    if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                } else if (user.bot == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                }
                                addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                                addIntent.putExtra("duplicate", false);
                                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                            } else {
                                lower_id = ApplicationLoader.applicationContext;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("sdid_");
                                stringBuilder.append(j);
                                pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                                if (bitmap2 == null) {
                                    pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                                } else if (user == null) {
                                    if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                } else if (user.bot == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                }
                                ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                            }
                        }
                    }
                    try {
                        i2 = high_id;
                        try {
                            BitmapShader shader = new BitmapShader(bitmap2, TileMode.CLAMP, TileMode.CLAMP);
                            if (roundPaint == 0) {
                                try {
                                    roundPaint = new Paint(1);
                                    bitmapRect = new RectF();
                                } catch (Throwable th2222) {
                                    th = th2222;
                                    FileLog.m3e(th);
                                    if (VERSION.SDK_INT < 26) {
                                        addIntent = new Intent();
                                        if (bitmap2 == null) {
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                                        } else if (user == null) {
                                            if (chat != null) {
                                                if (ChatObject.isChannel(chat) == 0) {
                                                }
                                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                            }
                                        } else if (user.bot == 0) {
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                        } else {
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                        }
                                        addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                                        addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                                        addIntent.putExtra("duplicate", false);
                                        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                                    } else {
                                        lower_id = ApplicationLoader.applicationContext;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("sdid_");
                                        stringBuilder.append(j);
                                        pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                                        if (bitmap2 == null) {
                                            pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                                        } else if (user == null) {
                                            if (chat != null) {
                                                if (ChatObject.isChannel(chat) == 0) {
                                                }
                                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                            }
                                        } else if (user.bot == 0) {
                                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                        } else {
                                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                        }
                                        ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                                    }
                                }
                            }
                            lower_id = ((float) size) / ((float) bitmap2.getWidth());
                            canvas.save();
                            canvas.scale(lower_id, lower_id);
                            roundPaint.setShader(shader);
                            int scale = lower_id;
                            fileLocation = photo;
                            try {
                                bitmapRect.set(0.0f, 0.0f, (float) bitmap2.getWidth(), (float) bitmap2.getHeight());
                                canvas.drawRoundRect(bitmapRect, (float) bitmap2.getWidth(), (float) bitmap2.getHeight(), roundPaint);
                                canvas.restore();
                            } catch (Throwable th22222) {
                                z = selfUser;
                                bitmap = bitmap2;
                                th = th22222;
                                FileLog.m3e(th);
                                if (VERSION.SDK_INT < 26) {
                                    lower_id = ApplicationLoader.applicationContext;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("sdid_");
                                    stringBuilder.append(j);
                                    pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                                    if (bitmap2 == null) {
                                        pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                                    } else if (user == null) {
                                        if (user.bot == 0) {
                                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                        } else {
                                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                        }
                                    } else if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                    ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                                } else {
                                    addIntent = new Intent();
                                    if (bitmap2 == null) {
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                                    } else if (user == null) {
                                        if (user.bot == 0) {
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                        } else {
                                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                        }
                                    } else if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                    addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                                    addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                                    addIntent.putExtra("duplicate", false);
                                    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                    ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                                }
                            }
                        } catch (Throwable th222222) {
                            fileLocation = photo;
                            z = selfUser;
                            bitmap = bitmap2;
                            th = th222222;
                            FileLog.m3e(th);
                            if (VERSION.SDK_INT < 26) {
                                addIntent = new Intent();
                                if (bitmap2 == null) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                                } else if (user == null) {
                                    if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                } else if (user.bot == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                }
                                addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                                addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                                addIntent.putExtra("duplicate", false);
                                addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                            } else {
                                lower_id = ApplicationLoader.applicationContext;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("sdid_");
                                stringBuilder.append(j);
                                pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                                if (bitmap2 == null) {
                                    pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                                } else if (user == null) {
                                    if (chat != null) {
                                        if (ChatObject.isChannel(chat) == 0) {
                                        }
                                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                    }
                                } else if (user.bot == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                }
                                ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                            }
                        }
                    } catch (Throwable th2222222) {
                        i2 = high_id;
                        fileLocation = photo;
                        z = selfUser;
                        bitmap = bitmap2;
                        th = th2222222;
                        FileLog.m3e(th);
                        if (VERSION.SDK_INT < 26) {
                            lower_id = ApplicationLoader.applicationContext;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("sdid_");
                            stringBuilder.append(j);
                            pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                            if (bitmap2 == null) {
                                pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0) {
                                }
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                            }
                            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                        } else {
                            addIntent = new Intent();
                            if (bitmap2 == null) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                            } else if (user == null) {
                                if (user.bot == 0) {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                                } else {
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                                }
                            } else if (chat != null) {
                                if (ChatObject.isChannel(chat) == 0) {
                                }
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                            addIntent.putExtra("duplicate", false);
                            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                        }
                    }
                    Drawable drawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.book_logo);
                    lower_id = AndroidUtilities.dp(NUM);
                    high_id = (size - lower_id) - AndroidUtilities.dp(2.0f);
                    photo = (size - lower_id) - AndroidUtilities.dp(2.0f);
                    z = selfUser;
                    bitmap = bitmap2;
                    try {
                        drawable.setBounds(high_id, photo, high_id + lower_id, photo + lower_id);
                        drawable.draw(canvas);
                        try {
                            canvas.setBitmap(null);
                        } catch (Exception e) {
                        }
                        bitmap2 = result;
                    } catch (Throwable th22222222) {
                        th = th22222222;
                        bitmap2 = bitmap;
                        FileLog.m3e(th);
                        if (VERSION.SDK_INT < 26) {
                            addIntent = new Intent();
                            if (bitmap2 == null) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                            } else if (user == null) {
                                if (chat != null) {
                                    if (ChatObject.isChannel(chat) == 0) {
                                    }
                                    addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            } else if (user.bot == 0) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                            } else {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                            addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                            addIntent.putExtra("duplicate", false);
                            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                            ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                        } else {
                            lower_id = ApplicationLoader.applicationContext;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("sdid_");
                            stringBuilder.append(j);
                            pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                            if (bitmap2 == null) {
                                pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                            } else if (user == null) {
                                if (chat != null) {
                                    if (ChatObject.isChannel(chat) == 0) {
                                    }
                                    pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                                }
                            } else if (user.bot == 0) {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                            } else {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            }
                            ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                        }
                    }
                } catch (Throwable th222222222) {
                    i = lower_id;
                    i2 = high_id;
                    fileLocation = photo;
                    z = selfUser;
                    bitmap = bitmap2;
                    th = th222222222;
                    FileLog.m3e(th);
                    if (VERSION.SDK_INT < 26) {
                        lower_id = ApplicationLoader.applicationContext;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("sdid_");
                        stringBuilder.append(j);
                        pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                        if (bitmap2 == null) {
                            pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                        } else if (user == null) {
                            if (user.bot == 0) {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            } else {
                                pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                            }
                        } else if (chat != null) {
                            if (ChatObject.isChannel(chat) == 0) {
                            }
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                        }
                        ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                    } else {
                        addIntent = new Intent();
                        if (bitmap2 == null) {
                            addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                        } else if (user == null) {
                            if (user.bot == 0) {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                            } else {
                                addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                            }
                        } else if (chat != null) {
                            if (ChatObject.isChannel(chat) == 0) {
                            }
                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                        }
                        addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                        addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                        addIntent.putExtra("duplicate", false);
                        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                        ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                    }
                }
                if (VERSION.SDK_INT < 26) {
                    lower_id = ApplicationLoader.applicationContext;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("sdid_");
                    stringBuilder.append(j);
                    pinShortcutInfo = new Builder(lower_id, stringBuilder.toString()).setShortLabel(name).setIntent(shortcutIntent);
                    if (bitmap2 == null) {
                        pinShortcutInfo.setIcon(Icon.createWithBitmap(bitmap2));
                    } else if (user == null) {
                        if (user.bot == 0) {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_bot));
                        } else {
                            pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_user));
                        }
                    } else if (chat != null) {
                        if (ChatObject.isChannel(chat) == 0) {
                        }
                        pinShortcutInfo.setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.book_group));
                    }
                    ((ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class)).requestPinShortcut(pinShortcutInfo.build(), null);
                } else {
                    addIntent = new Intent();
                    if (bitmap2 == null) {
                        addIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap2);
                    } else if (user == null) {
                        if (user.bot == 0) {
                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_bot));
                        } else {
                            addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_user));
                        }
                    } else if (chat != null) {
                        if (ChatObject.isChannel(chat) == 0) {
                        }
                        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, R.drawable.book_group));
                    }
                    addIntent.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
                    addIntent.putExtra("android.intent.extra.shortcut.NAME", name);
                    addIntent.putExtra("duplicate", false);
                    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                    ApplicationLoader.applicationContext.sendBroadcast(addIntent);
                }
            }
        } catch (Throwable thNUM) {
            FileLog.m3e(thNUM);
        }
    }

    public void uninstallShortcut(long did) {
        try {
            if (VERSION.SDK_INT >= 26) {
                ShortcutManager shortcutManager = (ShortcutManager) ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
                ArrayList<String> arrayList = new ArrayList();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("sdid_");
                stringBuilder.append(did);
                arrayList.add(stringBuilder.toString());
                shortcutManager.removeDynamicShortcuts(arrayList);
            } else {
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
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public MessageObject loadPinnedMessage(final int channelId, final int mid, boolean useQueue) {
        if (!useQueue) {
            return loadPinnedMessageInternal(channelId, mid, true);
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                DataQuery.this.loadPinnedMessageInternal(channelId, mid, false);
            }
        });
        return null;
    }

    private MessageObject loadPinnedMessageInternal(int channelId, int mid, boolean returnValue) {
        DataQuery dataQuery = this;
        final int i = channelId;
        int i2 = mid;
        long messageId = ((long) i2) | (((long) i) << 32);
        Message result = null;
        try {
            NativeByteBuffer data;
            Message result2;
            ArrayList<User> users = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance(dataQuery.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageId)}), new Object[0]);
            if (cursor.next()) {
                data = cursor.byteBufferValue(0);
                if (data != null) {
                    result = Message.TLdeserialize(data, data.readInt32(false), false);
                    result.readAttachPath(data, UserConfig.getInstance(dataQuery.currentAccount).clientUserId);
                    data.reuse();
                    if (result.action instanceof TL_messageActionHistoryClear) {
                        result = null;
                    } else {
                        result.id = cursor.intValue(1);
                        result.date = cursor.intValue(2);
                        result.dialog_id = (long) (-i);
                        MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                    }
                }
            }
            cursor.dispose();
            if (result == null) {
                SQLiteCursor sQLiteCursor;
                SQLiteDatabase database = MessagesStorage.getInstance(dataQuery.currentAccount).getDatabase();
                Object[] objArr = new Object[1];
                result2 = result;
                objArr[0] = Integer.valueOf(channelId);
                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", objArr), new Object[0]);
                if (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        result = Message.TLdeserialize(data, data.readInt32(false), false);
                        result.readAttachPath(data, UserConfig.getInstance(dataQuery.currentAccount).clientUserId);
                        data.reuse();
                        if (result.id == i2) {
                            if (!(result.action instanceof TL_messageActionHistoryClear)) {
                                result.dialog_id = (long) (-i);
                                MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                                cursor.dispose();
                                result2 = result;
                                sQLiteCursor = cursor;
                            }
                        }
                        result = null;
                        cursor.dispose();
                        result2 = result;
                        sQLiteCursor = cursor;
                    }
                }
                result = result2;
                cursor.dispose();
                result2 = result;
                sQLiteCursor = cursor;
            } else {
                result2 = result;
            }
            if (result2 == null) {
                TL_channels_getMessages req = new TL_channels_getMessages();
                req.channel = MessagesController.getInstance(dataQuery.currentAccount).getInputChannel(i);
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance(dataQuery.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        boolean ok = false;
                        if (error == null) {
                            messages_Messages messagesRes = (messages_Messages) response;
                            DataQuery.removeEmptyMessages(messagesRes.messages);
                            if (!messagesRes.messages.isEmpty()) {
                                ImageLoader.saveMessagesThumbs(messagesRes.messages);
                                DataQuery.this.broadcastPinnedMessage((Message) messagesRes.messages.get(0), messagesRes.users, messagesRes.chats, false, false);
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                                DataQuery.this.savePinnedMessage((Message) messagesRes.messages.get(0));
                                ok = true;
                            }
                        }
                        if (!ok) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).updateChannelPinnedMessage(i, 0);
                        }
                    }
                });
            } else if (returnValue) {
                return broadcastPinnedMessage(result2, users, chats, true, returnValue);
            } else {
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance(dataQuery.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance(dataQuery.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                broadcastPinnedMessage(result2, users, chats, true, false);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return null;
    }

    private void savePinnedMessage(final Message result) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
                    NativeByteBuffer data = new NativeByteBuffer(result.getObjectSize());
                    result.serializeToStream(data);
                    state.requery();
                    state.bindInteger(1, result.to_id.channel_id);
                    state.bindInteger(2, result.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private MessageObject broadcastPinnedMessage(Message result, ArrayList<User> users, ArrayList<Chat> chats, boolean isCache, boolean returnValue) {
        SparseArray<User> usersDict = new SparseArray();
        int a = 0;
        for (int a2 = 0; a2 < users.size(); a2++) {
            User user = (User) users.get(a2);
            usersDict.put(user.id, user);
        }
        ArrayList<User> arrayList = users;
        SparseArray<Chat> chatsDict = new SparseArray();
        while (a < chats.size()) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(chat.id, chat);
            a++;
        }
        ArrayList<Chat> arrayList2 = chats;
        if (returnValue) {
            return new MessageObject(this.currentAccount, result, (SparseArray) usersDict, (SparseArray) chatsDict, false);
        }
        final ArrayList<User> arrayList3 = arrayList;
        final boolean z = isCache;
        final ArrayList<Chat> arrayList4 = arrayList2;
        final Message message = result;
        final SparseArray<User> sparseArray = usersDict;
        final SparseArray<Chat> sparseArray2 = chatsDict;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList3, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList4, z);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedPinnedMessage, new MessageObject(DataQuery.this.currentAccount, message, sparseArray, sparseArray2, false));
            }
        });
        return null;
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
        DataQuery dataQuery = this;
        ArrayList<MessageObject> arrayList = messages;
        long j = dialogId;
        int a = 0;
        int a2;
        MessageObject messageObject;
        if (((int) j) == 0) {
            ArrayList<Long> replyMessages = new ArrayList();
            LongSparseArray<ArrayList<MessageObject>> replyMessageRandomOwners = new LongSparseArray();
            while (true) {
                a2 = a;
                if (a2 >= messages.size()) {
                    break;
                }
                messageObject = (MessageObject) arrayList.get(a2);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    long id = messageObject.messageOwner.reply_to_random_id;
                    ArrayList<MessageObject> messageObjects = (ArrayList) replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(Long.valueOf(id))) {
                        replyMessages.add(Long.valueOf(id));
                    }
                }
                a = a2 + 1;
            }
            if (!replyMessages.isEmpty()) {
                final ArrayList<Long> arrayList2 = replyMessages;
                final long j2 = j;
                final LongSparseArray<ArrayList<MessageObject>> longSparseArray = replyMessageRandomOwners;
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
                            SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                            while (cursor.next()) {
                                NativeByteBuffer data = cursor.byteBufferValue(0);
                                if (data != null) {
                                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                    message.readAttachPath(data, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                    data.reuse();
                                    message.id = cursor.intValue(1);
                                    message.date = cursor.intValue(2);
                                    message.dialog_id = j2;
                                    long value = cursor.longValue(3);
                                    ArrayList<MessageObject> arrayList = (ArrayList) longSparseArray.get(value);
                                    longSparseArray.remove(value);
                                    if (arrayList != null) {
                                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, message, false);
                                        for (int b = 0; b < arrayList.size(); b++) {
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
                            if (longSparseArray.size() != 0) {
                                for (int b2 = 0; b2 < longSparseArray.size(); b2++) {
                                    ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(b2);
                                    for (int a = 0; a < arrayList2.size(); a++) {
                                        ((MessageObject) arrayList2.get(a)).messageOwner.reply_to_random_id = 0;
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
        ArrayList<Integer> replyMessages2 = new ArrayList();
        SparseArray<ArrayList<MessageObject>> replyMessageOwners = new SparseArray();
        StringBuilder stringBuilder = new StringBuilder();
        int channelId = 0;
        while (true) {
            a2 = a;
            if (a2 >= messages.size()) {
                break;
            }
            messageObject = (MessageObject) arrayList.get(a2);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                int id2 = messageObject.messageOwner.reply_to_msg_id;
                j2 = (long) id2;
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    long messageId = j2 | (((long) messageObject.messageOwner.to_id.channel_id) << 32);
                    channelId = messageObject.messageOwner.to_id.channel_id;
                    j2 = messageId;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(j2);
                ArrayList<MessageObject> messageObjects2 = (ArrayList) replyMessageOwners.get(id2);
                if (messageObjects2 == null) {
                    messageObjects2 = new ArrayList();
                    replyMessageOwners.put(id2, messageObjects2);
                }
                messageObjects2.add(messageObject);
                if (!replyMessages2.contains(Integer.valueOf(id2))) {
                    replyMessages2.add(Integer.valueOf(id2));
                }
            }
            a = a2 + 1;
        }
        if (!replyMessages2.isEmpty()) {
            final int channelIdFinal = channelId;
            final StringBuilder stringBuilder2 = stringBuilder;
            j2 = j;
            AnonymousClass57 anonymousClass57 = r0;
            final ArrayList<Integer> arrayList3 = replyMessages2;
            DispatchQueue storageQueue = MessagesStorage.getInstance(dataQuery.currentAccount).getStorageQueue();
            final SparseArray<ArrayList<MessageObject>> sparseArray = replyMessageOwners;
            AnonymousClass57 anonymousClass572 = new Runnable() {

                /* renamed from: org.telegram.messenger.DataQuery$57$1 */
                class C17941 implements RequestDelegate {
                    C17941() {
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            messages_Messages messagesRes = (messages_Messages) response;
                            DataQuery.removeEmptyMessages(messagesRes.messages);
                            ImageLoader.saveMessagesThumbs(messagesRes.messages);
                            DataQuery.this.broadcastReplyMessages(messagesRes.messages, sparseArray, messagesRes.users, messagesRes.chats, j2, false);
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                            DataQuery.this.saveReplyMessages(sparseArray, messagesRes.messages);
                        }
                    }
                }

                /* renamed from: org.telegram.messenger.DataQuery$57$2 */
                class C17952 implements RequestDelegate {
                    C17952() {
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            messages_Messages messagesRes = (messages_Messages) response;
                            DataQuery.removeEmptyMessages(messagesRes.messages);
                            ImageLoader.saveMessagesThumbs(messagesRes.messages);
                            DataQuery.this.broadcastReplyMessages(messagesRes.messages, sparseArray, messagesRes.users, messagesRes.chats, j2, false);
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                            DataQuery.this.saveReplyMessages(sparseArray, messagesRes.messages);
                        }
                    }
                }

                public void run() {
                    try {
                        SQLiteCursor cursor;
                        ArrayList<Message> result = new ArrayList();
                        ArrayList<User> users = new ArrayList();
                        ArrayList<Chat> chats = new ArrayList();
                        ArrayList<Integer> usersToLoad = new ArrayList();
                        ArrayList<Integer> chatsToLoad = new ArrayList();
                        SQLiteCursor cursor2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder2.toString()}), new Object[0]);
                        while (true) {
                            cursor = cursor2;
                            if (!cursor.next()) {
                                break;
                            }
                            NativeByteBuffer data = cursor.byteBufferValue(0);
                            if (data != null) {
                                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                message.readAttachPath(data, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                data.reuse();
                                message.id = cursor.intValue(1);
                                message.date = cursor.intValue(2);
                                message.dialog_id = j2;
                                MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                                result.add(message);
                                arrayList3.remove(Integer.valueOf(message.id));
                            }
                            cursor2 = cursor;
                        }
                        cursor.dispose();
                        if (!usersToLoad.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                        }
                        if (!chatsToLoad.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                        }
                        DataQuery.this.broadcastReplyMessages(result, sparseArray, users, chats, j2, true);
                        if (!arrayList3.isEmpty()) {
                            if (channelIdFinal != 0) {
                                TL_channels_getMessages req = new TL_channels_getMessages();
                                req.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(channelIdFinal);
                                req.id = arrayList3;
                                ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req, new C17941());
                            } else {
                                TL_messages_getMessages req2 = new TL_messages_getMessages();
                                req2.id = arrayList3;
                                ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req2, new C17952());
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            };
            storageQueue.postRunnable(anonymousClass57);
        }
    }

    private void saveReplyMessages(final SparseArray<ArrayList<MessageObject>> replyMessageOwners, final ArrayList<Message> result) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
                    for (int a = 0; a < result.size(); a++) {
                        Message message = (Message) result.get(a);
                        ArrayList<MessageObject> messageObjects = (ArrayList) replyMessageOwners.get(message.id);
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
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void broadcastReplyMessages(ArrayList<Message> result, SparseArray<ArrayList<MessageObject>> replyMessageOwners, ArrayList<User> users, ArrayList<Chat> chats, long dialog_id, boolean isCache) {
        SparseArray<User> usersDict = new SparseArray();
        int a = 0;
        for (int a2 = 0; a2 < users.size(); a2++) {
            User user = (User) users.get(a2);
            usersDict.put(user.id, user);
        }
        ArrayList<User> arrayList = users;
        SparseArray<Chat> chatsDict = new SparseArray();
        while (a < chats.size()) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(chat.id, chat);
            a++;
        }
        final ArrayList<User> arrayList2 = arrayList;
        final boolean z = isCache;
        final ArrayList<Chat> arrayList3 = chats;
        final ArrayList<Message> arrayList4 = result;
        final SparseArray<ArrayList<MessageObject>> sparseArray = replyMessageOwners;
        final SparseArray<User> sparseArray2 = usersDict;
        final SparseArray<Chat> sparseArray3 = chatsDict;
        final long j = dialog_id;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList2, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList3, z);
                boolean changed = false;
                for (int a = 0; a < arrayList4.size(); a++) {
                    Message message = (Message) arrayList4.get(a);
                    ArrayList<MessageObject> arrayList = (ArrayList) sparseArray.get(message.id);
                    if (arrayList != null) {
                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, message, sparseArray2, sparseArray3, false);
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
                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(j));
                }
            }
        });
    }

    public static void sortEntities(ArrayList<MessageEntity> entities) {
        Collections.sort(entities, entityComparator);
    }

    private static boolean checkInclusion(int index, ArrayList<MessageEntity> entities) {
        if (entities != null) {
            if (!entities.isEmpty()) {
                int count = entities.size();
                for (int a = 0; a < count; a++) {
                    MessageEntity entity = (MessageEntity) entities.get(a);
                    if (entity.offset <= index && entity.offset + entity.length > index) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private static boolean checkIntersection(int start, int end, ArrayList<MessageEntity> entities) {
        if (entities != null) {
            if (!entities.isEmpty()) {
                int count = entities.size();
                for (int a = 0; a < count; a++) {
                    MessageEntity entity = (MessageEntity) entities.get(a);
                    if (entity.offset > start && entity.offset + entity.length <= end) {
                        return true;
                    }
                }
                return false;
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

    public ArrayList<MessageEntity> getEntities(CharSequence[] message) {
        DataQuery dataQuery;
        if (message != null) {
            int i = 0;
            if (message[0] != null) {
                int indexOf;
                int index;
                int a;
                TL_messageEntityCode entity;
                ArrayList<MessageEntity> entities = null;
                int start = -1;
                int lastIndex = 0;
                boolean isPre = false;
                String mono = "`";
                String pre = "```";
                String bold = "**";
                String italic = "__";
                while (true) {
                    indexOf = TextUtils.indexOf(message[i], !isPre ? "`" : "```", lastIndex);
                    index = indexOf;
                    if (indexOf == -1) {
                        break;
                    } else if (start == -1) {
                        boolean z = (message[i].length() - index > 2 && message[i].charAt(index + 1) == '`' && message[i].charAt(index + 2) == '`') ? true : i;
                        isPre = z;
                        start = index;
                        lastIndex = index + (isPre ? 3 : 1);
                    } else {
                        String str;
                        if (entities == null) {
                            entities = new ArrayList();
                        }
                        a = (isPre ? 3 : 1) + index;
                        while (a < message[i].length() && message[i].charAt(a) == '`') {
                            index++;
                            a++;
                        }
                        indexOf = (isPre ? 3 : 1) + index;
                        if (isPre) {
                            boolean replacedFirst;
                            CharSequence startMessage;
                            CharSequence content;
                            CharSequence charSequence;
                            int i2;
                            CharSequence endMessage;
                            CharSequence[] charSequenceArr;
                            int i3;
                            TL_messageEntityPre entity2;
                            lastIndex = start > 0 ? message[i].charAt(start - 1) : i;
                            if (lastIndex != 32) {
                                if (lastIndex != 10) {
                                    replacedFirst = i;
                                    startMessage = TextUtils.substring(message[i], i, start - (replacedFirst ? 1 : i));
                                    content = TextUtils.substring(message[i], start + 3, index);
                                    lastIndex = index + 3 >= message[i].length() ? message[i].charAt(index + 3) : i;
                                    charSequence = message[i];
                                    i2 = index + 3;
                                    if (lastIndex != 32) {
                                        if (lastIndex == 10) {
                                            i = 0;
                                            endMessage = TextUtils.substring(charSequence, i2 + i, message[0].length());
                                            if (startMessage.length() == 0) {
                                                startMessage = TextUtils.concat(new CharSequence[]{startMessage, "\n"});
                                            } else {
                                                replacedFirst = true;
                                            }
                                            if (endMessage.length() == 0) {
                                                charSequenceArr = new CharSequence[2];
                                                i3 = 0;
                                                charSequenceArr[0] = "\n";
                                                lastIndex = 1;
                                                charSequenceArr[1] = endMessage;
                                                endMessage = TextUtils.concat(charSequenceArr);
                                            } else {
                                                lastIndex = 1;
                                                i3 = 0;
                                            }
                                            if (TextUtils.isEmpty(content)) {
                                                str = mono;
                                                message[i3] = TextUtils.concat(new CharSequence[]{startMessage, content, endMessage});
                                                entity2 = new TL_messageEntityPre();
                                                entity2.offset = (replacedFirst ? 0 : 1) + start;
                                                entity2.length = ((index - start) - 3) + (replacedFirst ? 0 : 1);
                                                entity2.language = TtmlNode.ANONYMOUS_REGION_ID;
                                                entities.add(entity2);
                                                indexOf -= 6;
                                            } else {
                                                str = mono;
                                            }
                                        }
                                    }
                                    i = 1;
                                    endMessage = TextUtils.substring(charSequence, i2 + i, message[0].length());
                                    if (startMessage.length() == 0) {
                                        replacedFirst = true;
                                    } else {
                                        startMessage = TextUtils.concat(new CharSequence[]{startMessage, "\n"});
                                    }
                                    if (endMessage.length() == 0) {
                                        lastIndex = 1;
                                        i3 = 0;
                                    } else {
                                        charSequenceArr = new CharSequence[2];
                                        i3 = 0;
                                        charSequenceArr[0] = "\n";
                                        lastIndex = 1;
                                        charSequenceArr[1] = endMessage;
                                        endMessage = TextUtils.concat(charSequenceArr);
                                    }
                                    if (TextUtils.isEmpty(content)) {
                                        str = mono;
                                    } else {
                                        str = mono;
                                        message[i3] = TextUtils.concat(new CharSequence[]{startMessage, content, endMessage});
                                        entity2 = new TL_messageEntityPre();
                                        if (replacedFirst) {
                                        }
                                        entity2.offset = (replacedFirst ? 0 : 1) + start;
                                        if (replacedFirst) {
                                        }
                                        entity2.length = ((index - start) - 3) + (replacedFirst ? 0 : 1);
                                        entity2.language = TtmlNode.ANONYMOUS_REGION_ID;
                                        entities.add(entity2);
                                        indexOf -= 6;
                                    }
                                }
                            }
                            replacedFirst = true;
                            if (replacedFirst) {
                            }
                            startMessage = TextUtils.substring(message[i], i, start - (replacedFirst ? 1 : i));
                            content = TextUtils.substring(message[i], start + 3, index);
                            if (index + 3 >= message[i].length()) {
                            }
                            charSequence = message[i];
                            i2 = index + 3;
                            if (lastIndex != 32) {
                                if (lastIndex == 10) {
                                    i = 0;
                                    endMessage = TextUtils.substring(charSequence, i2 + i, message[0].length());
                                    if (startMessage.length() == 0) {
                                        startMessage = TextUtils.concat(new CharSequence[]{startMessage, "\n"});
                                    } else {
                                        replacedFirst = true;
                                    }
                                    if (endMessage.length() == 0) {
                                        charSequenceArr = new CharSequence[2];
                                        i3 = 0;
                                        charSequenceArr[0] = "\n";
                                        lastIndex = 1;
                                        charSequenceArr[1] = endMessage;
                                        endMessage = TextUtils.concat(charSequenceArr);
                                    } else {
                                        lastIndex = 1;
                                        i3 = 0;
                                    }
                                    if (TextUtils.isEmpty(content)) {
                                        str = mono;
                                        message[i3] = TextUtils.concat(new CharSequence[]{startMessage, content, endMessage});
                                        entity2 = new TL_messageEntityPre();
                                        if (replacedFirst) {
                                        }
                                        entity2.offset = (replacedFirst ? 0 : 1) + start;
                                        if (replacedFirst) {
                                        }
                                        entity2.length = ((index - start) - 3) + (replacedFirst ? 0 : 1);
                                        entity2.language = TtmlNode.ANONYMOUS_REGION_ID;
                                        entities.add(entity2);
                                        indexOf -= 6;
                                    } else {
                                        str = mono;
                                    }
                                }
                            }
                            i = 1;
                            endMessage = TextUtils.substring(charSequence, i2 + i, message[0].length());
                            if (startMessage.length() == 0) {
                                replacedFirst = true;
                            } else {
                                startMessage = TextUtils.concat(new CharSequence[]{startMessage, "\n"});
                            }
                            if (endMessage.length() == 0) {
                                lastIndex = 1;
                                i3 = 0;
                            } else {
                                charSequenceArr = new CharSequence[2];
                                i3 = 0;
                                charSequenceArr[0] = "\n";
                                lastIndex = 1;
                                charSequenceArr[1] = endMessage;
                                endMessage = TextUtils.concat(charSequenceArr);
                            }
                            if (TextUtils.isEmpty(content)) {
                                str = mono;
                            } else {
                                str = mono;
                                message[i3] = TextUtils.concat(new CharSequence[]{startMessage, content, endMessage});
                                entity2 = new TL_messageEntityPre();
                                if (replacedFirst) {
                                }
                                entity2.offset = (replacedFirst ? 0 : 1) + start;
                                if (replacedFirst) {
                                }
                                entity2.length = ((index - start) - 3) + (replacedFirst ? 0 : 1);
                                entity2.language = TtmlNode.ANONYMOUS_REGION_ID;
                                entities.add(entity2);
                                indexOf -= 6;
                            }
                        } else {
                            str = mono;
                            if (start + 1 != index) {
                                message[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(message[0], 0, start), TextUtils.substring(message[0], start + 1, index), TextUtils.substring(message[0], index + 1, message[0].length())});
                                entity = new TL_messageEntityCode();
                                entity.offset = start;
                                entity.length = (index - start) - 1;
                                entities.add(entity);
                                indexOf -= 2;
                            }
                        }
                        lastIndex = indexOf;
                        start = -1;
                        isPre = false;
                        mono = str;
                        i = 0;
                    }
                }
                if (start != -1 && isPre) {
                    message[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(message[0], 0, start), TextUtils.substring(message[0], start + 2, message[0].length())});
                    if (entities == null) {
                        entities = new ArrayList();
                    }
                    entity = new TL_messageEntityCode();
                    entity.offset = start;
                    entity.length = 1;
                    entities.add(entity);
                }
                if (message[0] instanceof Spannable) {
                    Spannable spannable = message[0];
                    TypefaceSpan[] spans = (TypefaceSpan[]) spannable.getSpans(0, message[0].length(), TypefaceSpan.class);
                    if (spans == null || spans.length <= 0) {
                    } else {
                        ArrayList<MessageEntity> entities2 = entities;
                        int a2 = 0;
                        while (a2 < spans.length) {
                            TypefaceSpan[] spans2;
                            TypefaceSpan span = spans[a2];
                            int spanStart = spannable.getSpanStart(span);
                            int spanEnd = spannable.getSpanEnd(span);
                            if (checkInclusion(spanStart, entities2) || checkInclusion(spanEnd, entities2)) {
                                spans2 = spans;
                            } else if (checkIntersection(spanStart, spanEnd, entities2)) {
                                spans2 = spans;
                            } else {
                                MessageEntity entity3;
                                if (entities2 == null) {
                                    entities2 = new ArrayList();
                                }
                                if (span.isBold()) {
                                    entity3 = new TL_messageEntityBold();
                                } else {
                                    entity3 = new TL_messageEntityItalic();
                                }
                                entity3.offset = spanStart;
                                spans2 = spans;
                                entity3.length = spanEnd - spanStart;
                                entities2.add(entity3);
                            }
                            a2++;
                            spans = spans2;
                        }
                        entities = entities2;
                    }
                    URLSpanUserMention[] spansMentions = (URLSpanUserMention[]) spannable.getSpans(0, message[0].length(), URLSpanUserMention.class);
                    if (spansMentions != null && spansMentions.length > 0) {
                        if (entities == null) {
                            entities = new ArrayList();
                        }
                        indexOf = 0;
                        while (indexOf < spansMentions.length) {
                            URLSpanUserMention[] spansMentions2;
                            TL_inputMessageEntityMentionName entity4 = new TL_inputMessageEntityMentionName();
                            entity4.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(Utilities.parseInt(spansMentions[indexOf].getURL()).intValue());
                            if (entity4.user_id != null) {
                                entity4.offset = spannable.getSpanStart(spansMentions[indexOf]);
                                spansMentions2 = spansMentions;
                                entity4.length = Math.min(spannable.getSpanEnd(spansMentions[indexOf]), message[0].length()) - entity4.offset;
                                if (message[0].charAt((entity4.offset + entity4.length) - 1) == ' ') {
                                    entity4.length--;
                                }
                                entities.add(entity4);
                            } else {
                                spansMentions2 = spansMentions;
                            }
                            indexOf++;
                            spansMentions = spansMentions2;
                        }
                    }
                }
                dataQuery = this;
                i = 0;
                while (i < 2) {
                    boolean isPre2;
                    lastIndex = 0;
                    start = -1;
                    mono = i == 0 ? "**" : "__";
                    char checkChar = i == 0 ? '*' : '_';
                    while (true) {
                        a = TextUtils.indexOf(message[0], mono, lastIndex);
                        index = a;
                        if (a == -1) {
                            break;
                        } else if (start == -1) {
                            char prevChar = index == 0 ? ' ' : message[0].charAt(index - 1);
                            if (!checkInclusion(index, entities)) {
                                if (prevChar != ' ') {
                                    if (prevChar == '\n') {
                                    }
                                }
                                start = index;
                            }
                            lastIndex = index + 2;
                        } else {
                            String checkString;
                            int a3 = index + 2;
                            while (a3 < message[0].length() && message[0].charAt(a3) == checkChar) {
                                index++;
                                a3++;
                            }
                            lastIndex = index + 2;
                            if (checkInclusion(index, entities)) {
                                isPre2 = isPre;
                                checkString = mono;
                            } else if (checkIntersection(start, index, entities)) {
                                isPre2 = isPre;
                                checkString = mono;
                            } else {
                                if (start + 2 != index) {
                                    if (entities == null) {
                                        entities = new ArrayList();
                                    }
                                    CharSequence[] charSequenceArr2 = new CharSequence[3];
                                    charSequenceArr2[0] = TextUtils.substring(message[0], 0, start);
                                    charSequenceArr2[1] = TextUtils.substring(message[0], start + 2, index);
                                    isPre2 = isPre;
                                    checkString = mono;
                                    charSequenceArr2[2] = TextUtils.substring(message[0], index + 2, message[0].length());
                                    message[0] = TextUtils.concat(charSequenceArr2);
                                    if (i == 0) {
                                        isPre = new TL_messageEntityBold();
                                    } else {
                                        isPre = new TL_messageEntityItalic();
                                    }
                                    isPre.offset = start;
                                    isPre.length = (index - start) - 2;
                                    removeOffsetAfter(isPre.offset + isPre.length, 4, entities);
                                    entities.add(isPre);
                                    lastIndex -= 4;
                                } else {
                                    isPre2 = isPre;
                                    checkString = mono;
                                }
                                start = -1;
                                isPre = isPre2;
                                mono = checkString;
                            }
                            start = -1;
                            isPre = isPre2;
                            mono = checkString;
                        }
                    }
                    isPre2 = isPre;
                    i++;
                }
                return entities;
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

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.getInstance(DataQuery.this.currentAccount).processUpdates((Updates) response, false);
                            AndroidUtilities.runOnUIThread(new C01361());
                        }
                    }
                });
            }
        }
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
        DraftMessage currentDraft;
        int lower_id;
        TL_messages_saveDraft req;
        if (TextUtils.isEmpty(message)) {
            if (replyToMessage == null) {
                draftMessage = new TL_draftMessageEmpty();
                draftMessage.date = (int) (System.currentTimeMillis() / 1000);
                draftMessage.message = message != null ? TtmlNode.ANONYMOUS_REGION_ID : message.toString();
                draftMessage.no_webpage = noWebpage;
                if (replyToMessage != null) {
                    draftMessage.reply_to_msg_id = replyToMessage.id;
                    draftMessage.flags |= 1;
                }
                if (!(entities == null || entities.isEmpty())) {
                    draftMessage.entities = entities;
                    draftMessage.flags |= 8;
                }
                currentDraft = (DraftMessage) this.drafts.get(did);
                if (clean || ((currentDraft == null || !currentDraft.message.equals(draftMessage.message) || currentDraft.reply_to_msg_id != draftMessage.reply_to_msg_id || currentDraft.no_webpage != draftMessage.no_webpage) && (currentDraft != null || !TextUtils.isEmpty(draftMessage.message) || draftMessage.reply_to_msg_id != 0))) {
                    saveDraft(did, draftMessage, replyToMessage, false);
                    lower_id = (int) did;
                    if (lower_id != 0) {
                        req = new TL_messages_saveDraft();
                        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                        if (req.peer == null) {
                            req.message = draftMessage.message;
                            req.no_webpage = draftMessage.no_webpage;
                            req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                            req.entities = draftMessage.entities;
                            req.flags = draftMessage.flags;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                }
                            });
                        } else {
                            return;
                        }
                    }
                    MessagesController.getInstance(this.currentAccount).sortDialogs(null);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
                return;
            }
        }
        draftMessage = new TL_draftMessage();
        draftMessage.date = (int) (System.currentTimeMillis() / 1000);
        if (message != null) {
        }
        draftMessage.message = message != null ? TtmlNode.ANONYMOUS_REGION_ID : message.toString();
        draftMessage.no_webpage = noWebpage;
        if (replyToMessage != null) {
            draftMessage.reply_to_msg_id = replyToMessage.id;
            draftMessage.flags |= 1;
        }
        draftMessage.entities = entities;
        draftMessage.flags |= 8;
        currentDraft = (DraftMessage) this.drafts.get(did);
        if (!clean) {
        }
        saveDraft(did, draftMessage, replyToMessage, false);
        lower_id = (int) did;
        if (lower_id != 0) {
            req = new TL_messages_saveDraft();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
            if (req.peer == null) {
                req.message = draftMessage.message;
                req.no_webpage = draftMessage.no_webpage;
                req.reply_to_msg_id = draftMessage.reply_to_msg_id;
                req.entities = draftMessage.entities;
                req.flags = draftMessage.flags;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            } else {
                return;
            }
        }
        MessagesController.getInstance(this.currentAccount).sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void saveDraft(long did, DraftMessage draft, Message replyToMessage, boolean fromServer) {
        SerializedData serializedData;
        StringBuilder stringBuilder;
        int lower_id;
        User user;
        Chat chat;
        Chat chat2;
        long messageId;
        long messageId2;
        int channelIdFinal;
        long j = did;
        DraftMessage draftMessage = draft;
        Message message = replyToMessage;
        Editor editor = this.preferences.edit();
        if (draftMessage != null) {
            if (!(draftMessage instanceof TL_draftMessageEmpty)) {
                r8.drafts.put(j, draftMessage);
                try {
                    serializedData = new SerializedData(draft.getObjectSize());
                    draftMessage.serializeToStream(serializedData);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(j);
                    editor.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (message != null) {
                    r8.draftMessages.remove(j);
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("r_");
                    stringBuilder2.append(j);
                    editor.remove(stringBuilder2.toString());
                } else {
                    r8.draftMessages.put(j, message);
                    serializedData = new SerializedData(replyToMessage.getObjectSize());
                    message.serializeToStream(serializedData);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("r_");
                    stringBuilder.append(j);
                    editor.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
                }
                editor.commit();
                if (fromServer) {
                    if (draftMessage.reply_to_msg_id != 0 && message == null) {
                        lower_id = (int) j;
                        user = null;
                        chat = null;
                        if (lower_id <= 0) {
                            user = MessagesController.getInstance(r8.currentAccount).getUser(Integer.valueOf(lower_id));
                        } else {
                            chat = MessagesController.getInstance(r8.currentAccount).getChat(Integer.valueOf(-lower_id));
                        }
                        chat2 = chat;
                        if (!(user == null && chat2 == null)) {
                            messageId = (long) draftMessage.reply_to_msg_id;
                            if (ChatObject.isChannel(chat2)) {
                                messageId2 = messageId;
                                channelIdFinal = 0;
                            } else {
                                messageId2 = messageId | (((long) chat2.id) << 32);
                                channelIdFinal = chat2.id;
                            }
                            final long messageIdFinal = messageId2;
                            Runnable runnable = r1;
                            DispatchQueue storageQueue = MessagesStorage.getInstance(r8.currentAccount).getStorageQueue();
                            lower_id = j;
                            Runnable anonymousClass62 = new Runnable() {

                                /* renamed from: org.telegram.messenger.DataQuery$62$1 */
                                class C17961 implements RequestDelegate {
                                    C17961() {
                                    }

                                    public void run(TLObject response, TL_error error) {
                                        if (error == null) {
                                            messages_Messages messagesRes = (messages_Messages) response;
                                            if (!messagesRes.messages.isEmpty()) {
                                                DataQuery.this.saveDraftReplyMessage(lower_id, (Message) messagesRes.messages.get(0));
                                            }
                                        }
                                    }
                                }

                                /* renamed from: org.telegram.messenger.DataQuery$62$2 */
                                class C17972 implements RequestDelegate {
                                    C17972() {
                                    }

                                    public void run(TLObject response, TL_error error) {
                                        if (error == null) {
                                            messages_Messages messagesRes = (messages_Messages) response;
                                            if (!messagesRes.messages.isEmpty()) {
                                                DataQuery.this.saveDraftReplyMessage(lower_id, (Message) messagesRes.messages.get(0));
                                            }
                                        }
                                    }
                                }

                                public void run() {
                                    Message message = null;
                                    try {
                                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageIdFinal)}), new Object[0]);
                                        if (cursor.next()) {
                                            NativeByteBuffer data = cursor.byteBufferValue(0);
                                            if (data != null) {
                                                message = Message.TLdeserialize(data, data.readInt32(false), false);
                                                message.readAttachPath(data, UserConfig.getInstance(DataQuery.this.currentAccount).clientUserId);
                                                data.reuse();
                                            }
                                        }
                                        cursor.dispose();
                                        if (message != null) {
                                            DataQuery.this.saveDraftReplyMessage(lower_id, message);
                                        } else if (channelIdFinal != 0) {
                                            TL_channels_getMessages req = new TL_channels_getMessages();
                                            req.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(channelIdFinal);
                                            req.id.add(Integer.valueOf((int) messageIdFinal));
                                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req, new C17961());
                                        } else {
                                            TL_messages_getMessages req2 = new TL_messages_getMessages();
                                            req2.id.add(Integer.valueOf((int) messageIdFinal));
                                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req2, new C17972());
                                        }
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                            };
                            storageQueue.postRunnable(anonymousClass62);
                        }
                    }
                    NotificationCenter.getInstance(r8.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
                }
            }
        }
        r8.drafts.remove(j);
        r8.draftMessages.remove(j);
        Editor edit = r8.preferences.edit();
        stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(j);
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("r_");
        stringBuilder.append(j);
        edit.remove(stringBuilder.toString()).commit();
        if (message != null) {
            r8.draftMessages.put(j, message);
            serializedData = new SerializedData(replyToMessage.getObjectSize());
            message.serializeToStream(serializedData);
            stringBuilder = new StringBuilder();
            stringBuilder.append("r_");
            stringBuilder.append(j);
            editor.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
        } else {
            r8.draftMessages.remove(j);
            StringBuilder stringBuilder22 = new StringBuilder();
            stringBuilder22.append("r_");
            stringBuilder22.append(j);
            editor.remove(stringBuilder22.toString());
        }
        editor.commit();
        if (fromServer) {
            lower_id = (int) j;
            user = null;
            chat = null;
            if (lower_id <= 0) {
                chat = MessagesController.getInstance(r8.currentAccount).getChat(Integer.valueOf(-lower_id));
            } else {
                user = MessagesController.getInstance(r8.currentAccount).getUser(Integer.valueOf(lower_id));
            }
            chat2 = chat;
            messageId = (long) draftMessage.reply_to_msg_id;
            if (ChatObject.isChannel(chat2)) {
                messageId2 = messageId;
                channelIdFinal = 0;
            } else {
                messageId2 = messageId | (((long) chat2.id) << 32);
                channelIdFinal = chat2.id;
            }
            final long messageIdFinal2 = messageId2;
            Runnable runnable2 = anonymousClass62;
            DispatchQueue storageQueue2 = MessagesStorage.getInstance(r8.currentAccount).getStorageQueue();
            lower_id = j;
            Runnable anonymousClass622 = /* anonymous class already generated */;
            storageQueue2.postRunnable(anonymousClass622);
            NotificationCenter.getInstance(r8.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
        }
    }

    private void saveDraftReplyMessage(final long did, final Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    DraftMessage draftMessage = (DraftMessage) DataQuery.this.drafts.get(did);
                    if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
                        DataQuery.this.draftMessages.put(did, message);
                        SerializedData serializedData = new SerializedData(message.getObjectSize());
                        message.serializeToStream(serializedData);
                        Editor edit = DataQuery.this.preferences.edit();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("r_");
                        stringBuilder.append(did);
                        edit.putString(stringBuilder.toString(), Utilities.bytesToHex(serializedData.toByteArray())).commit();
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
                    }
                }
            });
        }
    }

    public void cleanDraft(long did, boolean replyOnly) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(did);
        if (draftMessage != null) {
            if (!replyOnly) {
                this.drafts.remove(did);
                this.draftMessages.remove(did);
                Editor edit = this.preferences.edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                stringBuilder.append(did);
                edit = edit.remove(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("r_");
                stringBuilder.append(did);
                edit.remove(stringBuilder.toString()).commit();
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

    public void clearBotKeyboard(final long did, final ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (messages != null) {
                    for (int a = 0; a < messages.size(); a++) {
                        long did = DataQuery.this.botKeyboardsByMids.get(((Integer) messages.get(a)).intValue());
                        if (did != 0) {
                            DataQuery.this.botKeyboards.remove(did);
                            DataQuery.this.botKeyboardsByMids.delete(((Integer) messages.get(a)).intValue());
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(did));
                        }
                    }
                    return;
                }
                DataQuery.this.botKeyboards.remove(did);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(did));
            }
        });
    }

    public void loadBotKeyboard(final long did) {
        if (((Message) this.botKeyboards.get(did)) != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, keyboard, Long.valueOf(did));
            return;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                Message botKeyboard = null;
                try {
                    SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
                    if (cursor.next() && !cursor.isNull(0)) {
                        NativeByteBuffer data = cursor.byteBufferValue(0);
                        if (data != null) {
                            botKeyboard = Message.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                        }
                    }
                    cursor.dispose();
                    if (botKeyboard != null) {
                        final Message botKeyboardFinal = botKeyboard;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, botKeyboardFinal, Long.valueOf(did));
                            }
                        });
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void loadBotInfo(final int uid, boolean cache, final int classGuid) {
        if (!cache || ((BotInfo) this.botInfos.get(uid)) == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    BotInfo botInfo = null;
                    try {
                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[]{Integer.valueOf(uid)}), new Object[0]);
                        if (cursor.next() && !cursor.isNull(0)) {
                            NativeByteBuffer data = cursor.byteBufferValue(0);
                            if (data != null) {
                                botInfo = BotInfo.TLdeserialize(data, data.readInt32(false), false);
                                data.reuse();
                            }
                        }
                        cursor.dispose();
                        if (botInfo != null) {
                            final BotInfo botInfoFinal = botInfo;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, botInfoFinal, Integer.valueOf(classGuid));
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
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, botInfo, Integer.valueOf(classGuid));
    }

    public void putBotKeyboard(final long did, final Message message) {
        if (message != null) {
            int mid = 0;
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(did)}), new Object[0]);
                if (cursor.next()) {
                    mid = cursor.intValue(0);
                }
                cursor.dispose();
                if (mid < message.id) {
                    SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    state.requery();
                    NativeByteBuffer data = new NativeByteBuffer(message.getObjectSize());
                    message.serializeToStream(data);
                    state.bindLong(1, did);
                    state.bindInteger(2, message.id);
                    state.bindByteBuffer(3, data);
                    state.step();
                    data.reuse();
                    state.dispose();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            Message old = (Message) DataQuery.this.botKeyboards.get(did);
                            DataQuery.this.botKeyboards.put(did, message);
                            if (old != null) {
                                DataQuery.this.botKeyboardsByMids.delete(old.id);
                            }
                            DataQuery.this.botKeyboardsByMids.put(message.id, did);
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, message, Long.valueOf(did));
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
                        SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
                        state.requery();
                        NativeByteBuffer data = new NativeByteBuffer(botInfo.getObjectSize());
                        botInfo.serializeToStream(data);
                        state.bindInteger(1, botInfo.user_id);
                        state.bindByteBuffer(2, data);
                        state.step();
                        data.reuse();
                        state.dispose();
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }
}
