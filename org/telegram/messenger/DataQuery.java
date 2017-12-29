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
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.Toast;
import java.util.AbstractMap;
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
    private static Paint roundPaint;
    private HashMap<String, ArrayList<Document>> allStickers = new HashMap();
    private int[] archivedStickersCount = new int[2];
    private HashMap<Integer, BotInfo> botInfos = new HashMap();
    private HashMap<Long, Message> botKeyboards = new HashMap();
    private HashMap<Integer, Long> botKeyboardsByMids = new HashMap();
    private int currentAccount;
    private HashMap<Long, Message> draftMessages = new HashMap();
    private HashMap<Long, DraftMessage> drafts = new HashMap();
    private ArrayList<StickerSetCovered> featuredStickerSets = new ArrayList();
    private HashMap<Long, StickerSetCovered> featuredStickerSetsById = new HashMap();
    private boolean featuredStickersLoaded;
    private HashMap<Long, TL_messages_stickerSet> groupStickerSets = new HashMap();
    public ArrayList<TL_topPeer> hints = new ArrayList();
    private boolean inTransaction;
    public ArrayList<TL_topPeer> inlineBots = new ArrayList();
    private long lastMergeDialogId;
    private int lastReqId;
    private int lastReturnedNum;
    private String lastSearchQuery;
    private int[] loadDate = new int[2];
    private int loadFeaturedDate;
    private int loadFeaturedHash;
    private int[] loadHash = new int[2];
    boolean loaded;
    boolean loading;
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[3];
    private boolean[] loadingStickers = new boolean[2];
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
    private HashMap<Integer, MessageObject>[] searchResultMessagesMap = new HashMap[]{new HashMap(), new HashMap()};
    private ArrayList<TL_messages_stickerSet>[] stickerSets = new ArrayList[]{new ArrayList(), new ArrayList()};
    private HashMap<Long, TL_messages_stickerSet> stickerSetsById = new HashMap();
    private HashMap<String, TL_messages_stickerSet> stickerSetsByName = new HashMap();
    private HashMap<Long, String> stickersByEmoji = new HashMap();
    private boolean[] stickersLoaded = new boolean[2];
    private ArrayList<Long> unreadStickerSets = new ArrayList();

    public static DataQuery getInstance(int num) {
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
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public static DataQuery getAccountInstance() {
        return getInstance(UserConfig.selectedAccount);
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
                    if (message != null) {
                        this.draftMessages.put(Long.valueOf(did), message);
                    }
                } else {
                    DraftMessage draftMessage = DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                    if (draftMessage != null) {
                        this.drafts.put(Long.valueOf(did), draftMessage);
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
        for (a = 0; a < 2; a++) {
            this.loadHash[a] = 0;
            this.loadDate[a] = 0;
            this.stickerSets[a].clear();
            this.loadingStickers[a] = false;
            this.stickersLoaded[a] = false;
        }
        this.loadFeaturedDate = 0;
        this.loadFeaturedHash = 0;
        this.allStickers.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById.clear();
        this.featuredStickerSets.clear();
        this.unreadStickerSets.clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
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
                loadFeaturesStickers(true, false);
            }
        }
    }

    public ArrayList<Document> getRecentStickers(int type) {
        return new ArrayList(this.recentStickers[type]);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
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
                    }
                    try {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = " + cacheType).stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.e(e);
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "' AND type = 2").stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.e(e);
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
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "' AND type = 2").stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.e(e);
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

    public TL_messages_stickerSet getStickerSetById(Long id) {
        return (TL_messages_stickerSet) this.stickerSetsById.get(id);
    }

    public TL_messages_stickerSet getGroupStickerSetById(StickerSet stickerSet) {
        TL_messages_stickerSet set = (TL_messages_stickerSet) this.stickerSetsById.get(Long.valueOf(stickerSet.id));
        if (set == null) {
            set = (TL_messages_stickerSet) this.groupStickerSets.get(Long.valueOf(stickerSet.id));
            if (set == null || set.set == null) {
                loadGroupStickerSet(stickerSet, true);
            } else if (set.set.hash != stickerSet.hash) {
                loadGroupStickerSet(stickerSet, false);
            }
        }
        return set;
    }

    public void putGroupStickerSet(TL_messages_stickerSet stickerSet) {
        this.groupStickerSets.put(Long.valueOf(stickerSet.set.id), stickerSet);
    }

    private void loadGroupStickerSet(final StickerSet stickerSet, boolean cache) {
        if (cache) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        TL_messages_stickerSet set;
                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + stickerSet.id + "'", new Object[0]);
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
                            DataQuery.this.loadGroupStickerSet(stickerSet, false);
                        }
                        if (set != null && set.set != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    DataQuery.this.groupStickerSets.put(Long.valueOf(set.set.id), set);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(set.set.id));
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
            return;
        }
        TL_messages_getStickerSet req = new TL_messages_getStickerSet();
        req.stickerset = new TL_inputStickerSetID();
        req.stickerset.id = stickerSet.id;
        req.stickerset.access_hash = stickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    final TL_messages_stickerSet set = (TL_messages_stickerSet) response;
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                        public void run() {
                            try {
                                SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                                state.requery();
                                state.bindString(1, "s_" + set.set.id);
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
                                FileLog.e(e);
                            }
                        }
                    });
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            DataQuery.this.groupStickerSets.put(Long.valueOf(set.set.id), set);
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.groupStickersDidLoaded, Long.valueOf(set.set.id));
                        }
                    });
                }
            }
        });
    }

    public HashMap<String, ArrayList<Document>> getAllStickers() {
        return this.allStickers;
    }

    public boolean canAddStickerToFavorites() {
        return (this.stickersLoaded[0] && this.stickerSets[0].size() < 5 && this.recentStickers[2].isEmpty()) ? false : true;
    }

    public ArrayList<TL_messages_stickerSet> getStickerSets(int type) {
        return this.stickerSets[type];
    }

    public ArrayList<StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean isStickerPackInstalled(long id) {
        return this.stickerSetsById.containsKey(Long.valueOf(id));
    }

    public boolean isStickerPackUnread(long id) {
        return this.unreadStickerSets.contains(Long.valueOf(id));
    }

    public boolean isStickerPackInstalled(String name) {
        return this.stickerSetsByName.containsKey(name);
    }

    public String getEmojiForSticker(long id) {
        String value = (String) this.stickersByEmoji.get(Long.valueOf(id));
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
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (document.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) document.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void loadRecents(final int type, final boolean gif, boolean cache, boolean force) {
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
                        if (gif) {
                            cacheType = 2;
                        } else if (type == 0) {
                            cacheType = 3;
                        } else if (type == 1) {
                            cacheType = 4;
                        } else {
                            cacheType = 5;
                        }
                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + cacheType + " ORDER BY date DESC", new Object[0]);
                        final ArrayList<Document> arrayList = new ArrayList();
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
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ArrayList<Document> arrayList = null;
                    if (response instanceof TL_messages_savedGifs) {
                        arrayList = ((TL_messages_savedGifs) response).gifs;
                    }
                    DataQuery.this.processLoadedRecentDocuments(type, arrayList, gif, 0);
                }
            });
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
                        int cacheType;
                        SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                        if (z) {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentGifsCount;
                        } else if (i == 2) {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxFaveStickersCount;
                        } else {
                            maxCount = MessagesController.getInstance(DataQuery.this.currentAccount).maxRecentStickersCount;
                        }
                        database.beginTransaction();
                        SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        int count = arrayList.size();
                        if (z) {
                            cacheType = 2;
                        } else if (i == 0) {
                            cacheType = 3;
                        } else if (i == 1) {
                            cacheType = 4;
                        } else {
                            cacheType = 5;
                        }
                        int a = 0;
                        while (a < count && a != maxCount) {
                            Document document = (Document) arrayList.get(a);
                            state.requery();
                            state.bindString(1, TtmlNode.ANONYMOUS_REGION_ID + document.id);
                            state.bindInteger(2, cacheType);
                            state.bindString(3, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindString(4, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindString(5, TtmlNode.ANONYMOUS_REGION_ID);
                            state.bindInteger(6, 0);
                            state.bindInteger(7, 0);
                            state.bindInteger(8, 0);
                            state.bindInteger(9, i2 != 0 ? i2 : count - a);
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
                        if (arrayList.size() >= maxCount) {
                            database.beginTransaction();
                            for (a = maxCount; a < arrayList.size(); a++) {
                                database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((Document) arrayList.get(a)).id + "' AND type = " + cacheType).stepThis().dispose();
                            }
                            database.commitTransaction();
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
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
        if (!this.stickerSetsById.containsKey(Long.valueOf(set.set.id)) && !this.stickerSetsByName.containsKey(set.set.short_name)) {
            int a;
            int type = set.set.masks ? 1 : 0;
            this.stickerSets[type].add(0, set);
            this.stickerSetsById.put(Long.valueOf(set.set.id), set);
            this.stickerSetsByName.put(set.set.short_name, set);
            HashMap<Long, Document> stickersById = new HashMap();
            for (a = 0; a < set.documents.size(); a++) {
                Document document = (Document) set.documents.get(a);
                stickersById.put(Long.valueOf(document.id), document);
            }
            for (a = 0; a < set.packs.size(); a++) {
                TL_stickerPack stickerPack = (TL_stickerPack) set.packs.get(a);
                stickerPack.emoticon = stickerPack.emoticon.replace("️", TtmlNode.ANONYMOUS_REGION_ID);
                ArrayList<Document> arrayList = (ArrayList) this.allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = (Long) stickerPack.documents.get(c);
                    if (!this.stickersByEmoji.containsKey(id)) {
                        this.stickersByEmoji.put(id, stickerPack.emoticon);
                    }
                    Document sticker = (Document) stickersById.get(id);
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

    public void loadFeaturesStickers(boolean cache, boolean force) {
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        Throwable e;
                        Throwable th;
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
                                hash = DataQuery.this.calcFeaturedStickersHash(newStickerArray);
                            }
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        } catch (Throwable th3) {
                            e = th3;
                            FileLog.e(e);
                            if (cursor != null) {
                                cursor.dispose();
                            }
                            DataQuery.this.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                        }
                        DataQuery.this.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                    }
                });
                return;
            }
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
            public void run() {
                long j = 1000;
                if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
                    Runnable anonymousClass1 = new Runnable() {
                        public void run() {
                            if (!(arrayList == null || i2 == 0)) {
                                DataQuery.this.loadFeaturedHash = i2;
                            }
                            DataQuery.this.loadFeaturesStickers(false, false);
                        }
                    };
                    if (arrayList != null || z) {
                        j = 0;
                    }
                    AndroidUtilities.runOnUIThread(anonymousClass1, j);
                    if (arrayList == null) {
                        return;
                    }
                }
                if (arrayList != null) {
                    try {
                        final ArrayList<StickerSetCovered> stickerSetsNew = new ArrayList();
                        final HashMap<Long, StickerSetCovered> stickerSetsByIdNew = new HashMap();
                        for (int a = 0; a < arrayList.size(); a++) {
                            StickerSetCovered stickerSet = (StickerSetCovered) arrayList.get(a);
                            stickerSetsNew.add(stickerSet);
                            stickerSetsByIdNew.put(Long.valueOf(stickerSet.set.id), stickerSet);
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
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            DataQuery.this.loadFeaturedDate = i;
                        }
                    });
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
                        int a;
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                        state.requery();
                        int size = 4;
                        for (a = 0; a < stickersFinal.size(); a++) {
                            size += ((StickerSetCovered) stickersFinal.get(a)).getObjectSize();
                        }
                        NativeByteBuffer data = new NativeByteBuffer(size);
                        NativeByteBuffer data2 = new NativeByteBuffer((arrayList.size() * 8) + 4);
                        data.writeInt32(stickersFinal.size());
                        for (a = 0; a < stickersFinal.size(); a++) {
                            ((StickerSetCovered) stickersFinal.get(a)).serializeToStream(data);
                        }
                        data2.writeInt32(arrayList.size());
                        for (a = 0; a < arrayList.size(); a++) {
                            data2.writeInt64(((Long) arrayList.get(a)).longValue());
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
                        return;
                    }
                    state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
                    state.requery();
                    state.bindInteger(1, i);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }

    private int calcFeaturedStickersHash(ArrayList<StickerSetCovered> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((StickerSetCovered) sets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
                if (this.unreadStickerSets.contains(Long.valueOf(set.id))) {
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
        long acc = 0;
        for (int a = 0; a < this.featuredStickerSets.size(); a++) {
            StickerSet set = ((StickerSetCovered) this.featuredStickerSets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public void markFaturedStickersByIdAsRead(final long id) {
        if (this.unreadStickerSets.contains(Long.valueOf(id)) && !this.readingStickerSets.contains(Long.valueOf(id))) {
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

    public int getArchivedStickersCount(int type) {
        return this.archivedStickersCount[type];
    }

    public void loadArchivedStickersCount(final int type, boolean cache) {
        boolean z = true;
        if (cache) {
            int count = MessagesController.getNotificationsSettings(this.currentAccount).getInt("archivedStickersCount" + type, -1);
            if (count == -1) {
                loadArchivedStickersCount(type, false);
                return;
            }
            this.archivedStickersCount[type] = count;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
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
                            MessagesController.getNotificationsSettings(DataQuery.this.currentAccount).edit().putInt("archivedStickersCount" + type, res.count).commit();
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
                        }
                    }
                });
            }
        });
    }

    public void loadStickers(final int type, boolean cache, boolean force) {
        int hash = 0;
        if (!this.loadingStickers[type]) {
            loadArchivedStickersCount(type, cache);
            this.loadingStickers[type] = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        Throwable e;
                        Throwable th;
                        ArrayList<TL_messages_stickerSet> newStickerArray = null;
                        int date = 0;
                        int hash = 0;
                        SQLiteCursor cursor = null;
                        try {
                            cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT data, date, hash FROM stickers_v2 WHERE id = " + (type + 1), new Object[0]);
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
                                hash = DataQuery.calcStickersHash(newStickerArray);
                            }
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        } catch (Throwable th3) {
                            e = th3;
                            FileLog.e(e);
                            if (cursor != null) {
                                cursor.dispose();
                            }
                            DataQuery.this.processLoadedStickers(type, newStickerArray, true, date, hash);
                        }
                        DataQuery.this.processLoadedStickers(type, newStickerArray, true, date, hash);
                    }
                });
                return;
            }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (response instanceof TL_messages_allStickers) {
                                final TL_messages_allStickers res = response;
                                final ArrayList<TL_messages_stickerSet> newStickerArray = new ArrayList();
                                if (res.sets.isEmpty()) {
                                    DataQuery.this.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                    return;
                                }
                                HashMap<Long, TL_messages_stickerSet> newStickerSets = new HashMap();
                                for (int a = 0; a < res.sets.size(); a++) {
                                    final StickerSet stickerSet = (StickerSet) res.sets.get(a);
                                    TL_messages_stickerSet oldSet = (TL_messages_stickerSet) DataQuery.this.stickerSetsById.get(Long.valueOf(stickerSet.id));
                                    if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                                        newStickerArray.add(null);
                                        final int index = a;
                                        TLObject req = new TL_messages_getStickerSet();
                                        req.stickerset = new TL_inputStickerSetID();
                                        req.stickerset.id = stickerSet.id;
                                        req.stickerset.access_hash = stickerSet.access_hash;
                                        final HashMap<Long, TL_messages_stickerSet> hashMap = newStickerSets;
                                        ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                            public void run(final TLObject response, TL_error error) {
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        TL_messages_stickerSet res1 = response;
                                                        newStickerArray.set(index, res1);
                                                        hashMap.put(Long.valueOf(stickerSet.id), res1);
                                                        if (hashMap.size() == res.sets.size()) {
                                                            for (int a = 0; a < newStickerArray.size(); a++) {
                                                                if (newStickerArray.get(a) == null) {
                                                                    newStickerArray.remove(a);
                                                                }
                                                            }
                                                            DataQuery.this.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        oldSet.set.archived = stickerSet.archived;
                                        oldSet.set.installed = stickerSet.installed;
                                        oldSet.set.official = stickerSet.official;
                                        newStickerSets.put(Long.valueOf(oldSet.set.id), oldSet);
                                        newStickerArray.add(oldSet);
                                        if (newStickerSets.size() == res.sets.size()) {
                                            DataQuery.this.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                        }
                                    }
                                }
                                return;
                            }
                            DataQuery.this.processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), hash);
                        }
                    });
                }
            });
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
                        int a;
                        int i;
                        state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
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
                        if (i == 0) {
                            i = 1;
                        } else {
                            i = 2;
                        }
                        state.bindInteger(1, i);
                        state.bindByteBuffer(2, data);
                        state.bindInteger(3, i2);
                        state.bindInteger(4, i3);
                        state.step();
                        data.reuse();
                        state.dispose();
                        return;
                    }
                    state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                    state.requery();
                    state.bindInteger(1, i2);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }

    public String getStickerSetName(long setId) {
        TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) this.stickerSetsById.get(Long.valueOf(setId));
        if (stickerSet != null) {
            return stickerSet.set.short_name;
        }
        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.featuredStickerSetsById.get(Long.valueOf(setId));
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
            public void run() {
                if ((z && (arrayList == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600)) || (!z && arrayList == null && i2 == 0)) {
                    AnonymousClass1 anonymousClass1 = new Runnable() {
                        public void run() {
                            if (!(arrayList == null || i2 == 0)) {
                                DataQuery.this.loadHash[i3] = i2;
                            }
                            DataQuery.this.loadStickers(i3, false, false);
                        }
                    };
                    long j = (arrayList != null || z) ? 0 : 1000;
                    AndroidUtilities.runOnUIThread(anonymousClass1, j);
                    if (arrayList == null) {
                        return;
                    }
                }
                if (arrayList != null) {
                    try {
                        final ArrayList<TL_messages_stickerSet> stickerSetsNew = new ArrayList();
                        final HashMap<Long, TL_messages_stickerSet> stickerSetsByIdNew = new HashMap();
                        final HashMap<String, TL_messages_stickerSet> stickerSetsByNameNew = new HashMap();
                        final HashMap<Long, String> stickersByEmojiNew = new HashMap();
                        HashMap<Long, Document> stickersByIdNew = new HashMap();
                        final HashMap<String, ArrayList<Document>> allStickersNew = new HashMap();
                        for (int a = 0; a < arrayList.size(); a++) {
                            TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) arrayList.get(a);
                            if (stickerSet != null) {
                                int b;
                                stickerSetsNew.add(stickerSet);
                                stickerSetsByIdNew.put(Long.valueOf(stickerSet.set.id), stickerSet);
                                stickerSetsByNameNew.put(stickerSet.set.short_name, stickerSet);
                                for (b = 0; b < stickerSet.documents.size(); b++) {
                                    Document document = (Document) stickerSet.documents.get(b);
                                    if (!(document == null || (document instanceof TL_documentEmpty))) {
                                        stickersByIdNew.put(Long.valueOf(document.id), document);
                                    }
                                }
                                if (!stickerSet.set.archived) {
                                    for (b = 0; b < stickerSet.packs.size(); b++) {
                                        TL_stickerPack stickerPack = (TL_stickerPack) stickerSet.packs.get(b);
                                        if (!(stickerPack == null || stickerPack.emoticon == null)) {
                                            stickerPack.emoticon = stickerPack.emoticon.replace("️", TtmlNode.ANONYMOUS_REGION_ID);
                                            ArrayList<Document> arrayList = (ArrayList) allStickersNew.get(stickerPack.emoticon);
                                            if (arrayList == null) {
                                                arrayList = new ArrayList();
                                                allStickersNew.put(stickerPack.emoticon, arrayList);
                                            }
                                            for (int c = 0; c < stickerPack.documents.size(); c++) {
                                                Long id = (Long) stickerPack.documents.get(c);
                                                if (!stickersByEmojiNew.containsKey(id)) {
                                                    stickersByEmojiNew.put(id, stickerPack.emoticon);
                                                }
                                                Document sticker = (Document) stickersByIdNew.get(id);
                                                if (sticker != null) {
                                                    arrayList.add(sticker);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!z) {
                            DataQuery.this.putStickersToCache(i3, stickerSetsNew, i, i2);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                for (int a = 0; a < DataQuery.this.stickerSets[i3].size(); a++) {
                                    StickerSet set = ((TL_messages_stickerSet) DataQuery.this.stickerSets[i3].get(a)).set;
                                    DataQuery.this.stickerSetsById.remove(Long.valueOf(set.id));
                                    DataQuery.this.stickerSetsByName.remove(set.short_name);
                                }
                                DataQuery.this.stickerSetsById.putAll(stickerSetsByIdNew);
                                DataQuery.this.stickerSetsByName.putAll(stickerSetsByNameNew);
                                DataQuery.this.stickerSets[i3] = stickerSetsNew;
                                DataQuery.this.loadHash[i3] = i2;
                                DataQuery.this.loadDate[i3] = i;
                                if (i3 == 0) {
                                    DataQuery.this.allStickers = allStickersNew;
                                    DataQuery.this.stickersByEmoji = stickersByEmojiNew;
                                }
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(i3));
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            DataQuery.this.loadDate[i3] = i;
                        }
                    });
                    DataQuery.this.putStickersToCache(i3, null, i, 0);
                }
            }
        });
    }

    public void removeStickersSet(final Context context, final StickerSet stickerSet, int hide, BaseFragment baseFragment, boolean showSettings) {
        final int type = stickerSet.masks ? 1 : 0;
        TL_inputStickerSetID stickerSetID = new TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet.access_hash;
        stickerSetID.id = stickerSet.id;
        if (hide != 0) {
            TL_messages_installStickerSet req;
            final int i;
            final BaseFragment baseFragment2;
            final boolean z;
            stickerSet.archived = hide == 1;
            for (int a = 0; a < this.stickerSets[type].size(); a++) {
                TL_messages_stickerSet set = (TL_messages_stickerSet) this.stickerSets[type].get(a);
                if (set.set.id == stickerSet.id) {
                    this.stickerSets[type].remove(a);
                    if (hide == 2) {
                        this.stickerSets[type].add(0, set);
                    } else {
                        this.stickerSetsById.remove(Long.valueOf(set.set.id));
                        this.stickerSetsByName.remove(set.set.short_name);
                    }
                    this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
                    putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
                    req = new TL_messages_installStickerSet();
                    req.stickerset = stickerSetID;
                    req.archived = hide != 1;
                    i = hide;
                    baseFragment2 = baseFragment;
                    z = showSettings;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (response instanceof TL_messages_stickerSetInstallResultArchive) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(type));
                                        if (i != 1 && baseFragment2 != null && baseFragment2.getParentActivity() != null) {
                                            baseFragment2.showDialog(new StickersArchiveAlert(baseFragment2.getParentActivity(), z ? baseFragment2 : null, ((TL_messages_stickerSetInstallResultArchive) response).sets).create());
                                        }
                                    }
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    DataQuery.this.loadStickers(type, false, false);
                                }
                            }, 1000);
                        }
                    });
                    return;
                }
            }
            this.loadHash[type] = calcStickersHash(this.stickerSets[type]);
            putStickersToCache(type, this.stickerSets[type], this.loadDate[type], this.loadHash[type]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
            req = new TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            if (hide != 1) {
            }
            req.archived = hide != 1;
            i = hide;
            baseFragment2 = baseFragment;
            z = showSettings;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            return;
        }
        TL_messages_uninstallStickerSet req2 = new TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            if (error == null) {
                                if (stickerSet.masks) {
                                    Toast.makeText(context, LocaleController.getString("MasksRemoved", R.string.MasksRemoved), 0).show();
                                } else {
                                    Toast.makeText(context, LocaleController.getString("StickersRemoved", R.string.StickersRemoved), 0).show();
                                }
                                DataQuery.this.loadStickers(type, false, true);
                            }
                            Toast.makeText(context, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
                            DataQuery.this.loadStickers(type, false, true);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
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
        return this.searchResultMessagesMap[mergeDialog ? 1 : 0].containsKey(Integer.valueOf(messageId));
    }

    public void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, User user) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false, user);
    }

    private void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal, User user) {
        final TL_messages_search req;
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
                    req.q = query != null ? query : TtmlNode.ANONYMOUS_REGION_ID;
                    if (user != null) {
                        req.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                        req.flags |= 1;
                    }
                    req.filter = new TL_inputMessagesFilterEmpty();
                    final long j = mergeDialogId;
                    final long j2 = dialog_id;
                    final int i = guid;
                    final int i2 = direction;
                    final User user2 = user;
                    this.mergeReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (DataQuery.this.lastMergeDialogId == j) {
                                        DataQuery.this.mergeReqId = 0;
                                        if (response != null) {
                                            messages_Messages res = response;
                                            DataQuery.this.messagesSearchEndReached[1] = res.messages.isEmpty();
                                            DataQuery.this.messagesSearchCount[1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                                            DataQuery.this.searchMessagesInChat(req.q, j2, j, i, i2, true, user2);
                                        }
                                    }
                                }
                            });
                        }
                    }, 2);
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
            req.q = query != null ? query : TtmlNode.ANONYMOUS_REGION_ID;
            req.offset_id = max_id;
            if (user != null) {
                req.from_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
                req.flags |= 1;
            }
            req.filter = new TL_inputMessagesFilterEmpty();
            final int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.lastSearchQuery = query;
            j2 = queryWithDialog;
            final long j3 = dialog_id;
            final int i3 = guid;
            final long j4 = mergeDialogId;
            final User user3 = user;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (currentReqId == DataQuery.this.lastReqId) {
                                DataQuery.this.reqId = 0;
                                if (response != null) {
                                    MessageObject messageObject;
                                    int i;
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
                                    if (req.offset_id == 0 && j2 == j3) {
                                        DataQuery.this.lastReturnedNum = 0;
                                        DataQuery.this.searchResultMessages.clear();
                                        DataQuery.this.searchResultMessagesMap[0].clear();
                                        DataQuery.this.searchResultMessagesMap[1].clear();
                                        DataQuery.this.messagesSearchCount[0] = 0;
                                    }
                                    boolean added = false;
                                    for (a = 0; a < Math.min(res.messages.size(), 20); a++) {
                                        added = true;
                                        messageObject = new MessageObject(DataQuery.this.currentAccount, (Message) res.messages.get(a), null, false);
                                        DataQuery.this.searchResultMessages.add(messageObject);
                                        HashMap[] access$4300 = DataQuery.this.searchResultMessagesMap;
                                        if (j2 == j3) {
                                            i = 0;
                                        } else {
                                            i = 1;
                                        }
                                        access$4300[i].put(Integer.valueOf(messageObject.getId()), messageObject);
                                    }
                                    DataQuery.this.messagesSearchEndReached[j2 == j3 ? 0 : 1] = res.messages.size() != 21;
                                    int[] access$3700 = DataQuery.this.messagesSearchCount;
                                    i = j2 == j3 ? 0 : 1;
                                    int size = ((res instanceof TL_messages_messagesSlice) || (res instanceof TL_messages_channelMessages)) ? res.count : res.messages.size();
                                    access$3700[i] = size;
                                    if (DataQuery.this.searchResultMessages.isEmpty()) {
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i3), Integer.valueOf(0), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    } else if (added) {
                                        if (DataQuery.this.lastReturnedNum >= DataQuery.this.searchResultMessages.size()) {
                                            DataQuery.this.lastReturnedNum = DataQuery.this.searchResultMessages.size() - 1;
                                        }
                                        messageObject = (MessageObject) DataQuery.this.searchResultMessages.get(DataQuery.this.lastReturnedNum);
                                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i3), Integer.valueOf(messageObject.getId()), Integer.valueOf(DataQuery.this.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(DataQuery.this.lastReturnedNum), Integer.valueOf(DataQuery.this.messagesSearchCount[0] + DataQuery.this.messagesSearchCount[1]));
                                    }
                                    if (j2 == j3 && DataQuery.this.messagesSearchEndReached[0] && j4 != 0 && !DataQuery.this.messagesSearchEndReached[1]) {
                                        DataQuery.this.searchMessagesInChat(DataQuery.this.lastSearchQuery, j3, j4, i3, 0, true, user3);
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

    public void loadMedia(long uid, int count, int max_id, int type, boolean fromCache, int classGuid) {
        final boolean isChannel = ((int) uid) < 0 && ChatObject.isChannel(-((int) uid), this.currentAccount);
        int lower_part = (int) uid;
        if (fromCache || lower_part == 0) {
            loadMediaDatabase(uid, count, max_id, type, classGuid, isChannel);
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
            req.filter = new TL_inputMessagesFilterVoice();
        } else if (type == 3) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == 4) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
        if (req.peer != null) {
            final int i = count;
            final long j = uid;
            final int i2 = max_id;
            final int i3 = type;
            final int i4 = classGuid;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        boolean topReached;
                        messages_Messages res = (messages_Messages) response;
                        if (res.messages.size() > i) {
                            topReached = false;
                            res.messages.remove(res.messages.size() - 1);
                        } else {
                            topReached = true;
                        }
                        DataQuery.this.processLoadedMedia(res, j, i, i2, i3, false, i4, isChannel, topReached);
                    }
                }
            }), classGuid);
        }
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
            req.filter = new TL_inputMessagesFilterVoice();
        } else if (type == 3) {
            req.filter = new TL_inputMessagesFilterUrl();
        } else if (type == 4) {
            req.filter = new TL_inputMessagesFilterMusic();
        }
        req.q = TtmlNode.ANONYMOUS_REGION_ID;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_part);
        if (req.peer != null) {
            final long j = uid;
            final int i = type;
            final int i2 = classGuid;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        int count;
                        final messages_Messages res = (messages_Messages) response;
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                        if (res instanceof TL_messages_messages) {
                            count = res.messages.size();
                        } else {
                            count = res.count;
                        }
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
        if ((message instanceof TL_message) && (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0)) {
            return false;
        }
        if ((message instanceof TL_message_secret) && (message.media instanceof TL_messageMediaPhoto) && message.ttl != 0 && message.ttl <= 60) {
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

    private void processLoadedMedia(messages_Messages res, long uid, int count, int max_id, int type, boolean fromCache, int classGuid, boolean isChannel, boolean topReached) {
        int lower_part = (int) uid;
        if (fromCache && res.messages.isEmpty() && lower_part != 0) {
            loadMedia(uid, count, max_id, type, false, classGuid);
            return;
        }
        int a;
        if (!fromCache) {
            ImageLoader.saveMessagesThumbs(res.messages);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            putMediaDatabase(uid, type, res.messages, max_id, topReached);
        }
        AbstractMap usersDict = new HashMap();
        for (a = 0; a < res.users.size(); a++) {
            User u = (User) res.users.get(a);
            usersDict.put(Integer.valueOf(u.id), u);
        }
        final ArrayList<MessageObject> objects = new ArrayList();
        for (a = 0; a < res.messages.size(); a++) {
            objects.add(new MessageObject(this.currentAccount, (Message) res.messages.get(a), usersDict, true));
        }
        final messages_Messages org_telegram_tgnet_TLRPC_messages_Messages = res;
        final boolean z = fromCache;
        final long j = uid;
        final int i = classGuid;
        final int i2 = type;
        final boolean z2 = topReached;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int totalCount = org_telegram_tgnet_TLRPC_messages_Messages.count;
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(org_telegram_tgnet_TLRPC_messages_Messages.users, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(org_telegram_tgnet_TLRPC_messages_Messages.chats, z);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.mediaDidLoaded, Long.valueOf(j), Integer.valueOf(totalCount), objects, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z2));
            }
        });
    }

    private void processLoadedMediaCount(int count, long uid, int type, int classGuid, boolean fromCache) {
        final long j = uid;
        final boolean z = fromCache;
        final int i = count;
        final int i2 = type;
        final int i3 = classGuid;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int i = 0;
                int lower_part = (int) j;
                if (z && i == -1 && lower_part != 0) {
                    DataQuery.this.getMediaCount(j, i2, i3, false);
                    return;
                }
                if (!z) {
                    DataQuery.this.putMediaCountDatabase(j, i2, i);
                }
                NotificationCenter instance = NotificationCenter.getInstance(DataQuery.this.currentAccount);
                int i2 = NotificationCenter.mediaCountDidLoaded;
                Object[] objArr = new Object[4];
                objArr[0] = Long.valueOf(j);
                if (!(z && i == -1)) {
                    i = i;
                }
                objArr[1] = Integer.valueOf(i);
                objArr[2] = Boolean.valueOf(z);
                objArr[3] = Integer.valueOf(i2);
                instance.postNotificationName(i2, objArr);
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
                    FileLog.e(e);
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
                    FileLog.e(e);
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
                TL_messages_messages res = new TL_messages_messages();
                try {
                    SQLiteCursor cursor;
                    boolean topReached;
                    ArrayList<Integer> usersToLoad = new ArrayList();
                    ArrayList<Integer> chatsToLoad = new ArrayList();
                    int countToLoad = i + 1;
                    SQLiteDatabase database = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase();
                    boolean isEnd = false;
                    if (((int) j) != 0) {
                        int channelId = 0;
                        long messageMaxId = (long) i2;
                        if (z) {
                            channelId = -((int) j);
                        }
                        if (!(messageMaxId == 0 || channelId == 0)) {
                            messageMaxId |= ((long) channelId) << 32;
                        }
                        cursor = database.queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                        if (cursor.next()) {
                            isEnd = cursor.intValue(0) == 1;
                            cursor.dispose();
                        } else {
                            cursor.dispose();
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                            if (cursor.next()) {
                                int mid = cursor.intValue(0);
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
                            }
                            cursor.dispose();
                        }
                        long holeMessageId;
                        if (messageMaxId != 0) {
                            holeMessageId = 0;
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i2)}), new Object[0]);
                            if (cursor.next()) {
                                holeMessageId = (long) cursor.intValue(0);
                                if (channelId != 0) {
                                    holeMessageId |= ((long) channelId) << 32;
                                }
                            }
                            cursor.dispose();
                            if (holeMessageId > 1) {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(messageMaxId), Long.valueOf(holeMessageId), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            } else {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(messageMaxId), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            }
                        } else {
                            holeMessageId = 0;
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3)}), new Object[0]);
                            if (cursor.next()) {
                                holeMessageId = (long) cursor.intValue(0);
                                if (channelId != 0) {
                                    holeMessageId |= ((long) channelId) << 32;
                                }
                            }
                            cursor.dispose();
                            if (holeMessageId > 1) {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Long.valueOf(holeMessageId), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            } else {
                                cursor = database.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                            }
                        }
                    } else {
                        isEnd = true;
                        if (i2 != 0) {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                        } else {
                            cursor = database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[]{Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(countToLoad)}), new Object[0]);
                        }
                    }
                    while (cursor.next()) {
                        AbstractSerializedData data = cursor.byteBufferValue(0);
                        if (data != null) {
                            Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                            data.reuse();
                            message.id = cursor.intValue(1);
                            message.dialog_id = j;
                            if (((int) j) == 0) {
                                message.random_id = cursor.longValue(2);
                            }
                            res.messages.add(message);
                            if (message.from_id > 0) {
                                if (!usersToLoad.contains(Integer.valueOf(message.from_id))) {
                                    usersToLoad.add(Integer.valueOf(message.from_id));
                                }
                            } else if (!chatsToLoad.contains(Integer.valueOf(-message.from_id))) {
                                chatsToLoad.add(Integer.valueOf(-message.from_id));
                            }
                        }
                    }
                    cursor.dispose();
                    if (!usersToLoad.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), res.users);
                    }
                    if (!chatsToLoad.isEmpty()) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), res.chats);
                    }
                    if (res.messages.size() > i) {
                        topReached = false;
                        res.messages.remove(res.messages.size() - 1);
                    } else {
                        topReached = isEnd;
                    }
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, topReached);
                } catch (Throwable e) {
                    res.messages.clear();
                    res.chats.clear();
                    res.users.clear();
                    FileLog.e(e);
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, false);
                } catch (Throwable th) {
                    Throwable th2 = th;
                    DataQuery.this.processLoadedMedia(res, j, i, i2, i3, true, i4, z, false);
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
                int minId = 1;
                try {
                    if (arrayList.isEmpty() || z) {
                        MessagesStorage.getInstance(DataQuery.this.currentAccount).doneHolesInMedia(j, i, i2);
                        if (arrayList.isEmpty()) {
                            return;
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state2 = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
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
                            minId = ((Message) arrayList.get(arrayList.size() - 1)).id;
                        }
                        if (i != 0) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j, minId, i, i2);
                        } else {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).closeHolesInMedia(j, minId, ConnectionsManager.DEFAULT_DATACENTER_ID, i2);
                        }
                    }
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().commitTransaction();
                } catch (Throwable e) {
                    FileLog.e(e);
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
                            data.reuse();
                            if (MessageObject.isMusicMessage(message)) {
                                message.id = cursor.intValue(1);
                                message.dialog_id = j;
                                arrayList.add(0, new MessageObject(DataQuery.this.currentAccount, message, null, false));
                            }
                        }
                    }
                    cursor.dispose();
                } catch (Throwable e) {
                    FileLog.e(e);
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
                                user = MessagesController.getInstance(DataQuery.this.currentAccount).getUser(Integer.valueOf(hint.peer.user_id));
                                did = (long) hint.peer.user_id;
                            } else {
                                int chat_id = hint.peer.chat_id;
                                if (chat_id == 0) {
                                    chat_id = hint.peer.channel_id;
                                }
                                chat = MessagesController.getInstance(DataQuery.this.currentAccount).getChat(Integer.valueOf(chat_id));
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
                                intent.putExtra("currentAccount", DataQuery.this.currentAccount);
                                intent.setAction("com.tmessages.openchat" + did);
                                intent.addFlags(ConnectionsManager.FileTypeFile);
                                Bitmap bitmap = null;
                                if (photo != null) {
                                    bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString());
                                    if (bitmap != null) {
                                        int size = AndroidUtilities.dp(48.0f);
                                        Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                                        result.eraseColor(0);
                                        Canvas canvas = new Canvas(result);
                                        Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                                        if (DataQuery.roundPaint == null) {
                                            DataQuery.roundPaint = new Paint(1);
                                            DataQuery.bitmapRect = new RectF();
                                        }
                                        float scale = ((float) size) / ((float) bitmap.getWidth());
                                        canvas.scale(scale, scale);
                                        DataQuery.roundPaint.setShader(bitmapShader);
                                        DataQuery.bitmapRect.set((float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(46.0f), (float) AndroidUtilities.dp(46.0f));
                                        canvas.drawRoundRect(DataQuery.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), DataQuery.roundPaint);
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
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        UserConfig.getInstance(DataQuery.this.currentAccount).lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
                                                        UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                                                    }
                                                });
                                            } catch (Throwable e) {
                                                FileLog.e(e);
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
                        final ArrayList<TL_topPeer> hintsNew = new ArrayList();
                        final ArrayList<TL_topPeer> inlineBotsNew = new ArrayList();
                        final ArrayList<User> users = new ArrayList();
                        final ArrayList<Chat> chats = new ArrayList();
                        int selfUserId = UserConfig.getInstance(DataQuery.this.currentAccount).getClientUserId();
                        try {
                            ArrayList<Integer> usersToLoad = new ArrayList();
                            ArrayList<Integer> chatsToLoad = new ArrayList();
                            SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
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
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                            }
                            if (!chatsToLoad.isEmpty()) {
                                MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(users, true);
                                    MessagesController.getInstance(DataQuery.this.currentAccount).putChats(chats, true);
                                    DataQuery.this.loading = false;
                                    DataQuery.this.loaded = true;
                                    DataQuery.this.hints = hintsNew;
                                    DataQuery.this.inlineBots = inlineBotsNew;
                                    DataQuery.this.buildShortcuts();
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    if (Math.abs(UserConfig.getInstance(DataQuery.this.currentAccount).lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                                        DataQuery.this.loadHints(false);
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.e(e);
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
            if (user != null && !user.bot) {
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
                            FileLog.e(e);
                        }
                        final double dtFinal = dt;
                        AndroidUtilities.runOnUIThread(new Runnable() {
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
                                Collections.sort(DataQuery.this.hints, new Comparator<TL_topPeer>() {
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
                                DataQuery.this.savePeer((int) did, 0, peer.rating);
                                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                            }
                        });
                    }
                });
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
                    FileLog.e(e);
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
                    FileLog.e(e);
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
                            FileLog.e(e);
                        }
                    }
                    if (selfUser || bitmap != null) {
                        int size = AndroidUtilities.dp(58.0f);
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
                        int w = AndroidUtilities.dp(15.0f);
                        int left = (size - w) - AndroidUtilities.dp(2.0f);
                        int top = (size - w) - AndroidUtilities.dp(2.0f);
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
            FileLog.e(e3);
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
            FileLog.e(e);
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
        long messageId = ((long) mid) | (((long) channelId) << 32);
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
                    data.reuse();
                    if (result.action instanceof TL_messageActionHistoryClear) {
                        result = null;
                    } else {
                        result.id = cursor.intValue(1);
                        result.date = cursor.intValue(2);
                        result.dialog_id = (long) (-channelId);
                        MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                    }
                }
            }
            cursor.dispose();
            if (result == null) {
                cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[]{Integer.valueOf(channelId)}), new Object[0]);
                if (cursor.next()) {
                    data = cursor.byteBufferValue(0);
                    if (data != null) {
                        result = Message.TLdeserialize(data, data.readInt32(false), false);
                        data.reuse();
                        if (result.id != mid || (result.action instanceof TL_messageActionHistoryClear)) {
                            result = null;
                        } else {
                            result.dialog_id = (long) (-channelId);
                            MessagesStorage.addUsersAndChatsFromMessage(result, usersToLoad, chatsToLoad);
                        }
                    }
                }
                cursor.dispose();
            }
            if (result == null) {
                TL_channels_getMessages req = new TL_channels_getMessages();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                req.id.add(Integer.valueOf(mid));
                final int i = channelId;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
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
            FileLog.e(e);
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
                    FileLog.e(e);
                }
            }
        });
    }

    private MessageObject broadcastPinnedMessage(Message result, ArrayList<User> users, ArrayList<Chat> chats, boolean isCache, boolean returnValue) {
        int a;
        HashMap<Integer, User> usersDict = new HashMap();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(Integer.valueOf(user.id), user);
        }
        HashMap<Integer, Chat> chatsDict = new HashMap();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(Integer.valueOf(chat.id), chat);
        }
        if (returnValue) {
            return new MessageObject(this.currentAccount, result, usersDict, chatsDict, false);
        }
        final ArrayList<User> arrayList = users;
        final boolean z = isCache;
        final ArrayList<Chat> arrayList2 = chats;
        final Message message = result;
        final HashMap<Integer, User> hashMap = usersDict;
        final HashMap<Integer, Chat> hashMap2 = chatsDict;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList2, z);
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedPinnedMessage, new MessageObject(DataQuery.this.currentAccount, message, hashMap, hashMap2, false));
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
        int a;
        MessageObject messageObject;
        ArrayList<MessageObject> messageObjects;
        if (((int) dialogId) == 0) {
            final ArrayList<Long> replyMessages = new ArrayList();
            final HashMap<Long, ArrayList<MessageObject>> replyMessageRandomOwners = new HashMap();
            for (a = 0; a < messages.size(); a++) {
                messageObject = (MessageObject) messages.get(a);
                if (messageObject.isReply() && messageObject.replyMessageObject == null) {
                    Long id = Long.valueOf(messageObject.messageOwner.reply_to_random_id);
                    messageObjects = (ArrayList) replyMessageRandomOwners.get(id);
                    if (messageObjects == null) {
                        messageObjects = new ArrayList();
                        replyMessageRandomOwners.put(id, messageObjects);
                    }
                    messageObjects.add(messageObject);
                    if (!replyMessages.contains(id)) {
                        replyMessages.add(id);
                    }
                }
            }
            if (!replyMessages.isEmpty()) {
                final long j = dialogId;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        try {
                            ArrayList<MessageObject> arrayList;
                            SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", replyMessages)}), new Object[0]);
                            while (cursor.next()) {
                                NativeByteBuffer data = cursor.byteBufferValue(0);
                                if (data != null) {
                                    Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                    data.reuse();
                                    message.id = cursor.intValue(1);
                                    message.date = cursor.intValue(2);
                                    message.dialog_id = j;
                                    arrayList = (ArrayList) replyMessageRandomOwners.remove(Long.valueOf(cursor.longValue(3)));
                                    if (arrayList != null) {
                                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, message, null, null, false);
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
                            if (!replyMessageRandomOwners.isEmpty()) {
                                for (Entry<Long, ArrayList<MessageObject>> entry : replyMessageRandomOwners.entrySet()) {
                                    arrayList = (ArrayList) entry.getValue();
                                    for (int a = 0; a < arrayList.size(); a++) {
                                        ((MessageObject) arrayList.get(a)).messageOwner.reply_to_random_id = 0;
                                    }
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.didLoadedReplyMessages, Long.valueOf(j));
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                });
                return;
            }
            return;
        }
        final ArrayList<Integer> replyMessages2 = new ArrayList();
        final HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners = new HashMap();
        final StringBuilder stringBuilder = new StringBuilder();
        int channelId = 0;
        for (a = 0; a < messages.size(); a++) {
            messageObject = (MessageObject) messages.get(a);
            if (messageObject.getId() > 0 && messageObject.isReply() && messageObject.replyMessageObject == null) {
                Integer id2 = Integer.valueOf(messageObject.messageOwner.reply_to_msg_id);
                long messageId = (long) id2.intValue();
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
                if (!replyMessages2.contains(id2)) {
                    replyMessages2.add(id2);
                }
            }
        }
        if (!replyMessages2.isEmpty()) {
            final int channelIdFinal = channelId;
            final long j2 = dialogId;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        ArrayList<Message> result = new ArrayList();
                        ArrayList<User> users = new ArrayList();
                        ArrayList<Chat> chats = new ArrayList();
                        ArrayList<Integer> usersToLoad = new ArrayList();
                        ArrayList<Integer> chatsToLoad = new ArrayList();
                        SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
                        while (cursor.next()) {
                            NativeByteBuffer data = cursor.byteBufferValue(0);
                            if (data != null) {
                                Message message = Message.TLdeserialize(data, data.readInt32(false), false);
                                data.reuse();
                                message.id = cursor.intValue(1);
                                message.date = cursor.intValue(2);
                                message.dialog_id = j2;
                                MessagesStorage.addUsersAndChatsFromMessage(message, usersToLoad, chatsToLoad);
                                result.add(message);
                                replyMessages2.remove(Integer.valueOf(message.id));
                            }
                        }
                        cursor.dispose();
                        if (!usersToLoad.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                        }
                        if (!chatsToLoad.isEmpty()) {
                            MessagesStorage.getInstance(DataQuery.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                        }
                        DataQuery.this.broadcastReplyMessages(result, replyMessageOwners, users, chats, j2, true);
                        if (!replyMessages2.isEmpty()) {
                            if (channelIdFinal != 0) {
                                TL_channels_getMessages req = new TL_channels_getMessages();
                                req.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(channelIdFinal);
                                req.id = replyMessages2;
                                ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                    public void run(TLObject response, TL_error error) {
                                        if (error == null) {
                                            messages_Messages messagesRes = (messages_Messages) response;
                                            DataQuery.removeEmptyMessages(messagesRes.messages);
                                            ImageLoader.saveMessagesThumbs(messagesRes.messages);
                                            DataQuery.this.broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, j2, false);
                                            MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                                            DataQuery.this.saveReplyMessages(replyMessageOwners, messagesRes.messages);
                                        }
                                    }
                                });
                                return;
                            }
                            TL_messages_getMessages req2 = new TL_messages_getMessages();
                            req2.id = replyMessages2;
                            ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req2, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    if (error == null) {
                                        messages_Messages messagesRes = (messages_Messages) response;
                                        DataQuery.removeEmptyMessages(messagesRes.messages);
                                        ImageLoader.saveMessagesThumbs(messagesRes.messages);
                                        DataQuery.this.broadcastReplyMessages(messagesRes.messages, replyMessageOwners, messagesRes.users, messagesRes.chats, j2, false);
                                        MessagesStorage.getInstance(DataQuery.this.currentAccount).putUsersAndChats(messagesRes.users, messagesRes.chats, true, true);
                                        DataQuery.this.saveReplyMessages(replyMessageOwners, messagesRes.messages);
                                    }
                                }
                            });
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
        }
    }

    private void saveReplyMessages(final HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners, final ArrayList<Message> result) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
                    for (int a = 0; a < result.size(); a++) {
                        Message message = (Message) result.get(a);
                        ArrayList<MessageObject> messageObjects = (ArrayList) replyMessageOwners.get(Integer.valueOf(message.id));
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
                    FileLog.e(e);
                }
            }
        });
    }

    private void broadcastReplyMessages(ArrayList<Message> result, HashMap<Integer, ArrayList<MessageObject>> replyMessageOwners, ArrayList<User> users, ArrayList<Chat> chats, long dialog_id, boolean isCache) {
        int a;
        final HashMap<Integer, User> usersDict = new HashMap();
        for (a = 0; a < users.size(); a++) {
            User user = (User) users.get(a);
            usersDict.put(Integer.valueOf(user.id), user);
        }
        final HashMap<Integer, Chat> chatsDict = new HashMap();
        for (a = 0; a < chats.size(); a++) {
            Chat chat = (Chat) chats.get(a);
            chatsDict.put(Integer.valueOf(chat.id), chat);
        }
        final ArrayList<User> arrayList = users;
        final boolean z = isCache;
        final ArrayList<Chat> arrayList2 = chats;
        final ArrayList<Message> arrayList3 = result;
        final HashMap<Integer, ArrayList<MessageObject>> hashMap = replyMessageOwners;
        final long j = dialog_id;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(DataQuery.this.currentAccount).putUsers(arrayList, z);
                MessagesController.getInstance(DataQuery.this.currentAccount).putChats(arrayList2, z);
                boolean changed = false;
                for (int a = 0; a < arrayList3.size(); a++) {
                    Message message = (Message) arrayList3.get(a);
                    ArrayList<MessageObject> arrayList = (ArrayList) hashMap.get(Integer.valueOf(message.id));
                    if (arrayList != null) {
                        MessageObject messageObject = new MessageObject(DataQuery.this.currentAccount, message, usersDict, chatsDict, false);
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

    public ArrayList<MessageEntity> getEntities(CharSequence[] message) {
        if (message == null || message[0] == null) {
            return null;
        }
        int a;
        MessageEntity entity;
        ArrayList<MessageEntity> entities = null;
        int start = -1;
        int lastIndex = 0;
        boolean isPre = false;
        String mono = "`";
        String pre = "```";
        String bold = "**";
        String italic = "__";
        while (true) {
            int index = TextUtils.indexOf(message[0], !isPre ? "`" : "```", lastIndex);
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
                    CharSequence startMessage = TextUtils.substring(message[0], 0, start - (replacedFirst ? 1 : 0));
                    CharSequence content = TextUtils.substring(message[0], start + 3, index);
                    firstChar = index + 3 < message[0].length() ? message[0].charAt(index + 3) : 0;
                    CharSequence charSequence = message[0];
                    int i = index + 3;
                    int i2 = (firstChar == 32 || firstChar == 10) ? 1 : 0;
                    CharSequence endMessage = TextUtils.substring(charSequence, i2 + i, message[0].length());
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
                        TL_messageEntityPre entity2 = new TL_messageEntityPre();
                        entity2.offset = (replacedFirst ? 0 : 1) + start;
                        entity2.length = (replacedFirst ? 0 : 1) + ((index - start) - 3);
                        entity2.language = TtmlNode.ANONYMOUS_REGION_ID;
                        entities.add(entity2);
                        lastIndex -= 6;
                    }
                } else if (start + 1 != index) {
                    message[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(message[0], 0, start), TextUtils.substring(message[0], start + 1, index), TextUtils.substring(message[0], index + 1, message[0].length())});
                    TL_messageEntityCode entity3 = new TL_messageEntityCode();
                    entity3.offset = start;
                    entity3.length = (index - start) - 1;
                    entities.add(entity3);
                    lastIndex -= 2;
                }
                start = -1;
                isPre = false;
            }
        }
        if (start != -1 && isPre) {
            message[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(message[0], 0, start), TextUtils.substring(message[0], start + 2, message[0].length())});
            if (entities == null) {
                entities = new ArrayList();
            }
            entity3 = new TL_messageEntityCode();
            entity3.offset = start;
            entity3.length = 1;
            entities.add(entity3);
        }
        if (message[0] instanceof Spannable) {
            Spannable spannable = message[0];
            TypefaceSpan[] spans = (TypefaceSpan[]) spannable.getSpans(0, message[0].length(), TypefaceSpan.class);
            if (spans != null && spans.length > 0) {
                for (TypefaceSpan span : spans) {
                    int spanStart = spannable.getSpanStart(span);
                    int spanEnd = spannable.getSpanEnd(span);
                    if (!(checkInclusion(spanStart, entities) || checkInclusion(spanEnd, entities) || checkIntersection(spanStart, spanEnd, entities))) {
                        if (entities == null) {
                            entities = new ArrayList();
                        }
                        if (span.isBold()) {
                            entity = new TL_messageEntityBold();
                        } else {
                            entity = new TL_messageEntityItalic();
                        }
                        entity.offset = spanStart;
                        entity.length = spanEnd - spanStart;
                        entities.add(entity);
                    }
                }
            }
            URLSpanUserMention[] spansMentions = (URLSpanUserMention[]) spannable.getSpans(0, message[0].length(), URLSpanUserMention.class);
            if (spansMentions != null && spansMentions.length > 0) {
                if (entities == null) {
                    entities = new ArrayList();
                }
                for (int b = 0; b < spansMentions.length; b++) {
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
                    if (!checkInclusion(index, entities) && (prevChar == ' ' || prevChar == '\n')) {
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
                            message[0] = TextUtils.concat(new CharSequence[]{TextUtils.substring(message[0], 0, start), TextUtils.substring(message[0], start + 2, index), TextUtils.substring(message[0], index + 2, message[0].length())});
                            if (c == 0) {
                                entity = new TL_messageEntityBold();
                            } else {
                                entity = new TL_messageEntityItalic();
                            }
                            entity.offset = start;
                            entity.length = (index - start) - 2;
                            removeOffsetAfter(entity.offset + entity.length, 4, entities);
                            entities.add(entity);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getAllDrafts(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.getInstance(DataQuery.this.currentAccount).processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                UserConfig.getInstance(DataQuery.this.currentAccount).draftsLoaded = true;
                                DataQuery.this.loadingDrafts = false;
                                UserConfig.getInstance(DataQuery.this.currentAccount).saveConfig(false);
                            }
                        });
                    }
                }
            });
        }
    }

    public DraftMessage getDraft(long did) {
        return (DraftMessage) this.drafts.get(Long.valueOf(did));
    }

    public Message getDraftMessage(long did) {
        return (Message) this.draftMessages.get(Long.valueOf(did));
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
            draftMessage.reply_to_msg_id = replyToMessage.id;
            draftMessage.flags |= 1;
        }
        if (!(entities == null || entities.isEmpty())) {
            draftMessage.entities = entities;
            draftMessage.flags |= 8;
        }
        DraftMessage currentDraft = (DraftMessage) this.drafts.get(Long.valueOf(did));
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

    public void saveDraft(long did, DraftMessage draft, Message replyToMessage, boolean fromServer) {
        Editor editor = this.preferences.edit();
        if (draft == null || (draft instanceof TL_draftMessageEmpty)) {
            this.drafts.remove(Long.valueOf(did));
            this.draftMessages.remove(Long.valueOf(did));
            this.preferences.edit().remove(TtmlNode.ANONYMOUS_REGION_ID + did).remove("r_" + did).commit();
        } else {
            this.drafts.put(Long.valueOf(did), draft);
            try {
                SerializedData serializedData = new SerializedData(draft.getObjectSize());
                draft.serializeToStream(serializedData);
                editor.putString(TtmlNode.ANONYMOUS_REGION_ID + did, Utilities.bytesToHex(serializedData.toByteArray()));
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (replyToMessage == null) {
            this.draftMessages.remove(Long.valueOf(did));
            editor.remove("r_" + did);
        } else {
            this.draftMessages.put(Long.valueOf(did), replyToMessage);
            serializedData = new SerializedData(replyToMessage.getObjectSize());
            replyToMessage.serializeToStream(serializedData);
            editor.putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray()));
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
                        messageId |= ((long) chat.id) << 32;
                        channelIdFinal = chat.id;
                    } else {
                        channelIdFinal = 0;
                    }
                    final long messageIdFinal = messageId;
                    final long j = did;
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                        public void run() {
                            Message message = null;
                            try {
                                SQLiteCursor cursor = MessagesStorage.getInstance(DataQuery.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[]{Long.valueOf(messageIdFinal)}), new Object[0]);
                                if (cursor.next()) {
                                    NativeByteBuffer data = cursor.byteBufferValue(0);
                                    if (data != null) {
                                        message = Message.TLdeserialize(data, data.readInt32(false), false);
                                        data.reuse();
                                    }
                                }
                                cursor.dispose();
                                if (message != null) {
                                    DataQuery.this.saveDraftReplyMessage(j, message);
                                } else if (channelIdFinal != 0) {
                                    TL_channels_getMessages req = new TL_channels_getMessages();
                                    req.channel = MessagesController.getInstance(DataQuery.this.currentAccount).getInputChannel(channelIdFinal);
                                    req.id.add(Integer.valueOf((int) messageIdFinal));
                                    ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req, new RequestDelegate() {
                                        public void run(TLObject response, TL_error error) {
                                            if (error == null) {
                                                messages_Messages messagesRes = (messages_Messages) response;
                                                if (!messagesRes.messages.isEmpty()) {
                                                    DataQuery.this.saveDraftReplyMessage(j, (Message) messagesRes.messages.get(0));
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    TL_messages_getMessages req2 = new TL_messages_getMessages();
                                    req2.id.add(Integer.valueOf((int) messageIdFinal));
                                    ConnectionsManager.getInstance(DataQuery.this.currentAccount).sendRequest(req2, new RequestDelegate() {
                                        public void run(TLObject response, TL_error error) {
                                            if (error == null) {
                                                messages_Messages messagesRes = (messages_Messages) response;
                                                if (!messagesRes.messages.isEmpty()) {
                                                    DataQuery.this.saveDraftReplyMessage(j, (Message) messagesRes.messages.get(0));
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    });
                }
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
        }
    }

    private void saveDraftReplyMessage(final long did, final Message message) {
        if (message != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    DraftMessage draftMessage = (DraftMessage) DataQuery.this.drafts.get(Long.valueOf(did));
                    if (draftMessage != null && draftMessage.reply_to_msg_id == message.id) {
                        DataQuery.this.draftMessages.put(Long.valueOf(did), message);
                        SerializedData serializedData = new SerializedData(message.getObjectSize());
                        message.serializeToStream(serializedData);
                        DataQuery.this.preferences.edit().putString("r_" + did, Utilities.bytesToHex(serializedData.toByteArray())).commit();
                        NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(did));
                    }
                }
            });
        }
    }

    public void cleanDraft(long did, boolean replyOnly) {
        DraftMessage draftMessage = (DraftMessage) this.drafts.get(Long.valueOf(did));
        if (draftMessage != null) {
            if (!replyOnly) {
                this.drafts.remove(Long.valueOf(did));
                this.draftMessages.remove(Long.valueOf(did));
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

    public void clearBotKeyboard(final long did, final ArrayList<Integer> messages) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (messages != null) {
                    for (int a = 0; a < messages.size(); a++) {
                        Long did = (Long) DataQuery.this.botKeyboardsByMids.get(messages.get(a));
                        if (did != null) {
                            DataQuery.this.botKeyboards.remove(did);
                            DataQuery.this.botKeyboardsByMids.remove(messages.get(a));
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, did);
                        }
                    }
                    return;
                }
                DataQuery.this.botKeyboards.remove(Long.valueOf(did));
                NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, null, Long.valueOf(did));
            }
        });
    }

    public void loadBotKeyboard(final long did) {
        if (((Message) this.botKeyboards.get(Long.valueOf(did))) != null) {
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
                    FileLog.e(e);
                }
            }
        });
    }

    public void loadBotInfo(final int uid, boolean cache, final int classGuid) {
        if (!cache || ((BotInfo) this.botInfos.get(Integer.valueOf(uid))) == null) {
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
                        FileLog.e(e);
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
                            Message old = (Message) DataQuery.this.botKeyboards.put(Long.valueOf(did), message);
                            if (old != null) {
                                DataQuery.this.botKeyboardsByMids.remove(Integer.valueOf(old.id));
                            }
                            DataQuery.this.botKeyboardsByMids.put(Integer.valueOf(message.id), Long.valueOf(did));
                            NotificationCenter.getInstance(DataQuery.this.currentAccount).postNotificationName(NotificationCenter.botKeyboardDidLoaded, message, Long.valueOf(did));
                        }
                    });
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void putBotInfo(final BotInfo botInfo) {
        if (botInfo != null) {
            this.botInfos.put(Integer.valueOf(botInfo.user_id), botInfo);
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
                        FileLog.e(e);
                    }
                }
            });
        }
    }
}
