package org.telegram.messenger.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.StickersArchiveAlert;

public class StickersQuery {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static HashMap<String, ArrayList<Document>> allStickers = new HashMap();
    private static int[] archivedStickersCount = new int[2];
    private static ArrayList<StickerSetCovered> featuredStickerSets = new ArrayList();
    private static HashMap<Long, StickerSetCovered> featuredStickerSetsById = new HashMap();
    private static boolean featuredStickersLoaded;
    private static int[] loadDate = new int[2];
    private static int loadFeaturedDate;
    private static int loadFeaturedHash;
    private static int[] loadHash = new int[2];
    private static boolean loadingFeaturedStickers;
    private static boolean loadingRecentGifs;
    private static boolean[] loadingRecentStickers = new boolean[2];
    private static boolean[] loadingStickers = new boolean[2];
    private static ArrayList<Long> readingStickerSets = new ArrayList();
    private static ArrayList<Document> recentGifs = new ArrayList();
    private static boolean recentGifsLoaded;
    private static ArrayList<Document>[] recentStickers = new ArrayList[]{new ArrayList(), new ArrayList()};
    private static boolean[] recentStickersLoaded = new boolean[2];
    private static ArrayList<TL_messages_stickerSet>[] stickerSets = new ArrayList[]{new ArrayList(), new ArrayList()};
    private static HashMap<Long, TL_messages_stickerSet> stickerSetsById = new HashMap();
    private static HashMap<String, TL_messages_stickerSet> stickerSetsByName = new HashMap();
    private static HashMap<Long, String> stickersByEmoji = new HashMap();
    private static boolean[] stickersLoaded = new boolean[2];
    private static ArrayList<Long> unreadStickerSets = new ArrayList();

    public static void cleanup() {
        for (int a = 0; a < 2; a++) {
            loadHash[a] = 0;
            loadDate[a] = 0;
            stickerSets[a].clear();
            recentStickers[a].clear();
            loadingStickers[a] = false;
            stickersLoaded[a] = false;
            loadingRecentStickers[a] = false;
            recentStickersLoaded[a] = false;
        }
        loadFeaturedDate = 0;
        loadFeaturedHash = 0;
        allStickers.clear();
        stickersByEmoji.clear();
        featuredStickerSetsById.clear();
        featuredStickerSets.clear();
        unreadStickerSets.clear();
        recentGifs.clear();
        stickerSetsById.clear();
        stickerSetsByName.clear();
        loadingFeaturedStickers = false;
        featuredStickersLoaded = false;
        loadingRecentGifs = false;
        recentGifsLoaded = false;
    }

    public static void checkStickers(int type) {
        if (!loadingStickers[type]) {
            if (!stickersLoaded[type] || Math.abs((System.currentTimeMillis() / 1000) - ((long) loadDate[type])) >= 3600) {
                loadStickers(type, true, false);
            }
        }
    }

    public static void checkFeaturedStickers() {
        if (!loadingFeaturedStickers) {
            if (!featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) loadFeaturedDate)) >= 3600) {
                loadFeaturesStickers(true, false);
            }
        }
    }

    public static ArrayList<Document> getRecentStickers(int type) {
        return new ArrayList(recentStickers[type]);
    }

    public static ArrayList<Document> getRecentStickersNoCopy(int type) {
        return recentStickers[type];
    }

    public static void addRecentSticker(int type, Document document, int date) {
        boolean found = false;
        for (int a = 0; a < recentStickers[type].size(); a++) {
            Document image = (Document) recentStickers[type].get(a);
            if (image.id == document.id) {
                recentStickers[type].remove(a);
                recentStickers[type].add(0, image);
                found = true;
            }
        }
        if (!found) {
            recentStickers[type].add(0, document);
        }
        if (recentStickers[type].size() > MessagesController.getInstance().maxRecentStickersCount) {
            final Document old = (Document) recentStickers[type].remove(recentStickers[type].size() - 1);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "'").stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
        }
        ArrayList<Document> arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(type, arrayList, false, date);
    }

    public static ArrayList<Document> getRecentGifs() {
        return new ArrayList(recentGifs);
    }

    public static void removeRecentGif(final Document document) {
        recentGifs.remove(document);
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = true;
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + document.id + "'").stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public static void addRecentGif(Document document, int date) {
        boolean found = false;
        for (int a = 0; a < recentGifs.size(); a++) {
            Document image = (Document) recentGifs.get(a);
            if (image.id == document.id) {
                recentGifs.remove(a);
                recentGifs.add(0, image);
                found = true;
            }
        }
        if (!found) {
            recentGifs.add(0, document);
        }
        if (recentGifs.size() > MessagesController.getInstance().maxRecentGifsCount) {
            final Document old = (Document) recentGifs.remove(recentGifs.size() - 1);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + old.id + "'").stepThis().dispose();
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
        }
        ArrayList<Document> arrayList = new ArrayList();
        arrayList.add(document);
        processLoadedRecentDocuments(0, arrayList, true, date);
    }

    public static boolean isLoadingStickers(int type) {
        return loadingStickers[type];
    }

    public static TL_messages_stickerSet getStickerSetByName(String name) {
        return (TL_messages_stickerSet) stickerSetsByName.get(name);
    }

    public static TL_messages_stickerSet getStickerSetById(Long id) {
        return (TL_messages_stickerSet) stickerSetsById.get(id);
    }

    public static HashMap<String, ArrayList<Document>> getAllStickers() {
        return allStickers;
    }

    public static ArrayList<TL_messages_stickerSet> getStickerSets(int type) {
        return stickerSets[type];
    }

    public static ArrayList<StickerSetCovered> getFeaturedStickerSets() {
        return featuredStickerSets;
    }

    public static ArrayList<Long> getUnreadStickerSets() {
        return unreadStickerSets;
    }

    public static boolean isStickerPackInstalled(long id) {
        return stickerSetsById.containsKey(Long.valueOf(id));
    }

    public static boolean isStickerPackUnread(long id) {
        return unreadStickerSets.contains(Long.valueOf(id));
    }

    public static boolean isStickerPackInstalled(String name) {
        return stickerSetsByName.containsKey(name);
    }

    public static String getEmojiForSticker(long id) {
        String value = (String) stickersByEmoji.get(Long.valueOf(id));
        return value != null ? value : "";
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

    public static void loadRecents(final int type, final boolean gif, boolean cache) {
        boolean z = true;
        if (gif) {
            if (!loadingRecentGifs) {
                loadingRecentGifs = true;
                if (recentGifsLoaded) {
                    cache = false;
                }
            } else {
                return;
            }
        } else if (!loadingRecentStickers[type]) {
            loadingRecentStickers[type] = true;
            if (recentStickersLoaded[type]) {
                cache = false;
            }
        } else {
            return;
        }
        if (cache) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                        StringBuilder append = new StringBuilder().append("SELECT document FROM web_recent_v3 WHERE type = ");
                        int i = gif ? 2 : type == 0 ? 3 : 4;
                        SQLiteCursor cursor = database.queryFinalized(append.append(i).append(" ORDER BY date DESC").toString(), new Object[0]);
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
                                    StickersQuery.recentGifs = arrayList;
                                    StickersQuery.loadingRecentGifs = false;
                                    StickersQuery.recentGifsLoaded = true;
                                } else {
                                    StickersQuery.recentStickers[type] = arrayList;
                                    StickersQuery.loadingRecentStickers[type] = false;
                                    StickersQuery.recentStickersLoaded[type] = true;
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(gif), Integer.valueOf(type));
                                StickersQuery.loadRecents(type, gif, false);
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
            return;
        }
        long lastLoadTime;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        if (gif) {
            lastLoadTime = preferences.getLong("lastGifLoadTime", 0);
        } else {
            lastLoadTime = preferences.getLong("lastStickersLoadTime", 0);
        }
        if (Math.abs(System.currentTimeMillis() - lastLoadTime) < 3600000) {
            return;
        }
        if (gif) {
            TL_messages_getSavedGifs req = new TL_messages_getSavedGifs();
            req.hash = calcDocumentsHash(recentGifs);
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ArrayList<Document> arrayList = null;
                    if (response instanceof TL_messages_savedGifs) {
                        arrayList = ((TL_messages_savedGifs) response).gifs;
                    }
                    StickersQuery.processLoadedRecentDocuments(type, arrayList, gif, 0);
                }
            });
            return;
        }
        TL_messages_getRecentStickers req2 = new TL_messages_getRecentStickers();
        req2.hash = calcDocumentsHash(recentStickers[type]);
        if (type != 1) {
            z = false;
        }
        req2.attached = z;
        ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                ArrayList<Document> arrayList = null;
                if (response instanceof TL_messages_recentStickers) {
                    arrayList = ((TL_messages_recentStickers) response).stickers;
                }
                StickersQuery.processLoadedRecentDocuments(type, arrayList, gif, 0);
            }
        });
    }

    private static void processLoadedRecentDocuments(final int type, final ArrayList<Document> documents, final boolean gif, final int date) {
        if (documents != null) {
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    try {
                        SQLiteDatabase database = MessagesStorage.getInstance().getDatabase();
                        int maxCount = gif ? MessagesController.getInstance().maxRecentGifsCount : MessagesController.getInstance().maxRecentStickersCount;
                        database.beginTransaction();
                        SQLitePreparedStatement state = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        int count = documents.size();
                        int a = 0;
                        while (a < count && a != maxCount) {
                            Document document = (Document) documents.get(a);
                            state.requery();
                            state.bindString(1, "" + document.id);
                            int i = gif ? 2 : type == 0 ? 3 : 4;
                            state.bindInteger(2, i);
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
                                database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((Document) documents.get(a)).id + "'").stepThis().dispose();
                            }
                            database.commitTransaction();
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            });
        }
        if (date == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit();
                    if (gif) {
                        StickersQuery.loadingRecentGifs = false;
                        StickersQuery.recentGifsLoaded = true;
                        editor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
                    } else {
                        StickersQuery.loadingRecentStickers[type] = false;
                        StickersQuery.recentStickersLoaded[type] = true;
                        editor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
                    }
                    if (documents != null) {
                        if (gif) {
                            StickersQuery.recentGifs = documents;
                        } else {
                            StickersQuery.recentStickers[type] = documents;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, Boolean.valueOf(gif), Integer.valueOf(type));
                    }
                }
            });
        }
    }

    public static void reorderStickers(int type, final ArrayList<Long> order) {
        Collections.sort(stickerSets[type], new Comparator<TL_messages_stickerSet>() {
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
        loadHash[type] = calcStickersHash(stickerSets[type]);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
        loadStickers(type, false, true);
    }

    public static void calcNewHash(int type) {
        loadHash[type] = calcStickersHash(stickerSets[type]);
    }

    public static void addNewStickerSet(TL_messages_stickerSet set) {
        if (!stickerSetsById.containsKey(Long.valueOf(set.set.id)) && !stickerSetsByName.containsKey(set.set.short_name)) {
            int type;
            int a;
            if (set.set.masks) {
                type = 1;
            } else {
                type = 0;
            }
            stickerSets[type].add(0, set);
            stickerSetsById.put(Long.valueOf(set.set.id), set);
            stickerSetsByName.put(set.set.short_name, set);
            HashMap<Long, Document> stickersById = new HashMap();
            for (a = 0; a < set.documents.size(); a++) {
                Document document = (Document) set.documents.get(a);
                stickersById.put(Long.valueOf(document.id), document);
            }
            for (a = 0; a < set.packs.size(); a++) {
                TL_stickerPack stickerPack = (TL_stickerPack) set.packs.get(a);
                stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
                ArrayList<Document> arrayList = (ArrayList) allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = (Long) stickerPack.documents.get(c);
                    if (!stickersByEmoji.containsKey(id)) {
                        stickersByEmoji.put(id, stickerPack.emoticon);
                    }
                    Document sticker = (Document) stickersById.get(id);
                    if (sticker != null) {
                        arrayList.add(sticker);
                    }
                }
            }
            loadHash[type] = calcStickersHash(stickerSets[type]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
            loadStickers(type, false, true);
        }
    }

    public static void loadFeaturesStickers(boolean cache, boolean force) {
        if (!loadingFeaturedStickers) {
            loadingFeaturedStickers = true;
            if (cache) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        Throwable e;
                        Throwable th;
                        ArrayList<StickerSetCovered> newStickerArray = null;
                        ArrayList<Long> unread = new ArrayList();
                        int date = 0;
                        int hash = 0;
                        SQLiteCursor cursor = null;
                        try {
                            cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT data, unread, date, hash FROM stickers_featured WHERE 1", new Object[0]);
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
                                hash = StickersQuery.calcFeaturedStickersHash(newStickerArray);
                            }
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        } catch (Throwable th3) {
                            e = th3;
                            FileLog.e("tmessages", e);
                            if (cursor != null) {
                                cursor.dispose();
                            }
                            StickersQuery.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                        }
                        StickersQuery.processLoadedFeaturedStickers(newStickerArray, unread, true, date, hash);
                    }
                });
                return;
            }
            final TL_messages_getFeaturedStickers req = new TL_messages_getFeaturedStickers();
            req.hash = force ? 0 : loadFeaturedHash;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (response instanceof TL_messages_featuredStickers) {
                                TL_messages_featuredStickers res = response;
                                StickersQuery.processLoadedFeaturedStickers(res.sets, res.unread, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                return;
                            }
                            StickersQuery.processLoadedFeaturedStickers(null, null, false, (int) (System.currentTimeMillis() / 1000), req.hash);
                        }
                    });
                }
            });
        }
    }

    private static void processLoadedFeaturedStickers(ArrayList<StickerSetCovered> res, ArrayList<Long> unreadStickers, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                StickersQuery.loadingFeaturedStickers = false;
                StickersQuery.featuredStickersLoaded = true;
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
                                StickersQuery.loadFeaturedHash = i2;
                            }
                            StickersQuery.loadFeaturesStickers(false, false);
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
                            StickersQuery.putFeaturedStickersToCache(stickerSetsNew, arrayList2, i, i2);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                StickersQuery.unreadStickerSets = arrayList2;
                                StickersQuery.featuredStickerSetsById = stickerSetsByIdNew;
                                StickersQuery.featuredStickerSets = stickerSetsNew;
                                StickersQuery.loadFeaturedHash = i2;
                                StickersQuery.loadFeaturedDate = i;
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            StickersQuery.loadFeaturedDate = i;
                        }
                    });
                    StickersQuery.putFeaturedStickersToCache(null, null, i, 0);
                }
            }
        });
    }

    private static void putFeaturedStickersToCache(ArrayList<StickerSetCovered> stickers, final ArrayList<Long> unreadStickers, final int date, final int hash) {
        final ArrayList<StickerSetCovered> stickersFinal = stickers != null ? new ArrayList(stickers) : null;
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state;
                    if (stickersFinal != null) {
                        int a;
                        state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
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
                    }
                    state = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
                    state.requery();
                    state.bindInteger(1, date);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    private static int calcFeaturedStickersHash(ArrayList<StickerSetCovered> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((StickerSetCovered) sets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
                if (unreadStickerSets.contains(Long.valueOf(set.id))) {
                    acc = (((acc * 20261) + 2147483648L) + 1) % 2147483648L;
                }
            }
        }
        return (int) acc;
    }

    public static void markFaturedStickersAsRead(boolean query) {
        if (!unreadStickerSets.isEmpty()) {
            unreadStickerSets.clear();
            loadFeaturedHash = calcFeaturedStickersHash(featuredStickerSets);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
            putFeaturedStickersToCache(featuredStickerSets, unreadStickerSets, loadFeaturedDate, loadFeaturedHash);
            if (query) {
                ConnectionsManager.getInstance().sendRequest(new TL_messages_readFeaturedStickers(), new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        }
    }

    public static int getFeaturesStickersHashWithoutUnread() {
        long acc = 0;
        for (int a = 0; a < featuredStickerSets.size(); a++) {
            StickerSet set = ((StickerSetCovered) featuredStickerSets.get(a)).set;
            if (!set.archived) {
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (set.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) set.id))) % 2147483648L;
            }
        }
        return (int) acc;
    }

    public static void markFaturedStickersByIdAsRead(final long id) {
        if (unreadStickerSets.contains(Long.valueOf(id)) && !readingStickerSets.contains(Long.valueOf(id))) {
            readingStickerSets.add(Long.valueOf(id));
            TL_messages_readFeaturedStickers req = new TL_messages_readFeaturedStickers();
            req.id.add(Long.valueOf(id));
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    StickersQuery.unreadStickerSets.remove(Long.valueOf(id));
                    StickersQuery.readingStickerSets.remove(Long.valueOf(id));
                    StickersQuery.loadFeaturedHash = StickersQuery.calcFeaturedStickersHash(StickersQuery.featuredStickerSets);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
                    StickersQuery.putFeaturedStickersToCache(StickersQuery.featuredStickerSets, StickersQuery.unreadStickerSets, StickersQuery.loadFeaturedDate, StickersQuery.loadFeaturedHash);
                }
            }, 1000);
        }
    }

    public static int getArchivedStickersCount(int type) {
        return archivedStickersCount[type];
    }

    public static void loadArchivedStickersCount(final int type, boolean cache) {
        boolean z = true;
        if (!cache) {
            TL_messages_getArchivedStickers req = new TL_messages_getArchivedStickers();
            req.limit = 0;
            if (type != 1) {
                z = false;
            }
            req.masks = z;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_messages_archivedStickers res = response;
                                StickersQuery.archivedStickersCount[type] = res.count;
                                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("archivedStickersCount" + type, res.count).commit();
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
                            }
                        }
                    });
                }
            });
        } else if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("archivedStickersCount" + type, -1) == -1) {
            loadArchivedStickersCount(type, false);
        } else {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, Integer.valueOf(type));
        }
    }

    public static void loadStickers(final int type, boolean cache, boolean force) {
        int hash = 0;
        if (!loadingStickers[type]) {
            loadArchivedStickersCount(type, cache);
            loadingStickers[type] = true;
            if (cache) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        Throwable e;
                        Throwable th;
                        ArrayList<TL_messages_stickerSet> newStickerArray = null;
                        int date = 0;
                        int hash = 0;
                        SQLiteCursor cursor = null;
                        try {
                            cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT data, date, hash FROM stickers_v2 WHERE id = " + (type + 1), new Object[0]);
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
                                hash = StickersQuery.calcStickersHash(newStickerArray);
                            }
                            if (cursor != null) {
                                cursor.dispose();
                            }
                        } catch (Throwable th3) {
                            e = th3;
                            FileLog.e("tmessages", e);
                            if (cursor != null) {
                                cursor.dispose();
                            }
                            StickersQuery.processLoadedStickers(type, newStickerArray, true, date, hash);
                        }
                        StickersQuery.processLoadedStickers(type, newStickerArray, true, date, hash);
                    }
                });
                return;
            }
            TLObject req;
            if (type == 0) {
                req = new TL_messages_getAllStickers();
                TL_messages_getAllStickers tL_messages_getAllStickers = (TL_messages_getAllStickers) req;
                if (!force) {
                    hash = loadHash[type];
                }
                tL_messages_getAllStickers.hash = hash;
            } else {
                req = new TL_messages_getMaskStickers();
                TL_messages_getMaskStickers tL_messages_getMaskStickers = (TL_messages_getMaskStickers) req;
                if (!force) {
                    hash = loadHash[type];
                }
                tL_messages_getMaskStickers.hash = hash;
            }
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (response instanceof TL_messages_allStickers) {
                                final TL_messages_allStickers res = response;
                                final ArrayList<TL_messages_stickerSet> newStickerArray = new ArrayList();
                                if (res.sets.isEmpty()) {
                                    StickersQuery.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                    return;
                                }
                                final HashMap<Long, TL_messages_stickerSet> newStickerSets = new HashMap();
                                for (int a = 0; a < res.sets.size(); a++) {
                                    final StickerSet stickerSet = (StickerSet) res.sets.get(a);
                                    TL_messages_stickerSet oldSet = (TL_messages_stickerSet) StickersQuery.stickerSetsById.get(Long.valueOf(stickerSet.id));
                                    if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                                        newStickerArray.add(null);
                                        final int index = a;
                                        TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                                        req.stickerset = new TL_inputStickerSetID();
                                        req.stickerset.id = stickerSet.id;
                                        req.stickerset.access_hash = stickerSet.access_hash;
                                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                            public void run(final TLObject response, TL_error error) {
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        TL_messages_stickerSet res1 = response;
                                                        newStickerArray.set(index, res1);
                                                        newStickerSets.put(Long.valueOf(stickerSet.id), res1);
                                                        if (newStickerSets.size() == res.sets.size()) {
                                                            for (int a = 0; a < newStickerArray.size(); a++) {
                                                                if (newStickerArray.get(a) == null) {
                                                                    newStickerArray.remove(a);
                                                                }
                                                            }
                                                            StickersQuery.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
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
                                            StickersQuery.processLoadedStickers(type, newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                                        }
                                    }
                                }
                                return;
                            }
                            StickersQuery.processLoadedStickers(type, null, false, (int) (System.currentTimeMillis() / 1000), hash);
                        }
                    });
                }
            });
        }
    }

    private static void putStickersToCache(final int type, ArrayList<TL_messages_stickerSet> stickers, final int date, final int hash) {
        final ArrayList<TL_messages_stickerSet> stickersFinal = stickers != null ? new ArrayList(stickers) : null;
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state;
                    if (stickersFinal != null) {
                        int a;
                        int i;
                        state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
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
                        if (type == 0) {
                            i = 1;
                        } else {
                            i = 2;
                        }
                        state.bindInteger(1, i);
                        state.bindByteBuffer(2, data);
                        state.bindInteger(3, date);
                        state.bindInteger(4, hash);
                        state.step();
                        data.reuse();
                        state.dispose();
                        return;
                    }
                    state = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                    state.requery();
                    state.bindInteger(1, date);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public static String getStickerSetName(long setId) {
        TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) stickerSetsById.get(Long.valueOf(setId));
        return stickerSet != null ? stickerSet.set.short_name : null;
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

    private static void processLoadedStickers(final int type, ArrayList<TL_messages_stickerSet> res, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                StickersQuery.loadingStickers[type] = false;
                StickersQuery.stickersLoaded[type] = true;
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
                                StickersQuery.loadHash[i3] = i2;
                            }
                            StickersQuery.loadStickers(i3, false, false);
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
                                            stickerPack.emoticon = stickerPack.emoticon.replace("️", "");
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
                            StickersQuery.putStickersToCache(i3, stickerSetsNew, i, i2);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                for (int a = 0; a < StickersQuery.stickerSets[i3].size(); a++) {
                                    StickerSet set = ((TL_messages_stickerSet) StickersQuery.stickerSets[i3].get(a)).set;
                                    StickersQuery.stickerSetsById.remove(Long.valueOf(set.id));
                                    StickersQuery.stickerSetsByName.remove(set.short_name);
                                }
                                StickersQuery.stickerSetsById.putAll(stickerSetsByIdNew);
                                StickersQuery.stickerSetsByName.putAll(stickerSetsByNameNew);
                                StickersQuery.stickerSets[i3] = stickerSetsNew;
                                StickersQuery.loadHash[i3] = i2;
                                StickersQuery.loadDate[i3] = i;
                                if (i3 == 0) {
                                    StickersQuery.allStickers = allStickersNew;
                                    StickersQuery.stickersByEmoji = stickersByEmojiNew;
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(i3));
                            }
                        });
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                } else if (!z) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            StickersQuery.loadDate[i3] = i;
                        }
                    });
                    StickersQuery.putStickersToCache(i3, null, i, 0);
                }
            }
        });
    }

    public static void removeStickersSet(final Context context, final StickerSet stickerSet, final int hide, final BaseFragment baseFragment, final boolean showSettings) {
        final int type = stickerSet.masks ? 1 : 0;
        TL_inputStickerSetID stickerSetID = new TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet.access_hash;
        stickerSetID.id = stickerSet.id;
        if (hide != 0) {
            TL_messages_installStickerSet req;
            stickerSet.archived = hide == 1;
            for (int a = 0; a < stickerSets[type].size(); a++) {
                TL_messages_stickerSet set = (TL_messages_stickerSet) stickerSets[type].get(a);
                if (set.set.id == stickerSet.id) {
                    stickerSets[type].remove(a);
                    if (hide == 2) {
                        stickerSets[type].add(0, set);
                    } else {
                        stickerSetsById.remove(Long.valueOf(set.set.id));
                        stickerSetsByName.remove(set.set.short_name);
                    }
                    loadHash[type] = calcStickersHash(stickerSets[type]);
                    putStickersToCache(type, stickerSets[type], loadDate[type], loadHash[type]);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
                    req = new TL_messages_installStickerSet();
                    req.stickerset = stickerSetID;
                    req.archived = hide != 1;
                    ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (response instanceof TL_messages_stickerSetInstallResultArchive) {
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadArchivedStickers, Integer.valueOf(type));
                                        if (hide != 1 && baseFragment != null && baseFragment.getParentActivity() != null) {
                                            baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), showSettings ? baseFragment : null, ((TL_messages_stickerSetInstallResultArchive) response).sets).create());
                                        }
                                    }
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    StickersQuery.loadStickers(type, false, false);
                                }
                            }, 1000);
                        }
                    });
                    return;
                }
            }
            loadHash[type] = calcStickersHash(stickerSets[type]);
            putStickersToCache(type, stickerSets[type], loadDate[type], loadHash[type]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, Integer.valueOf(type));
            req = new TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            if (hide != 1) {
            }
            req.archived = hide != 1;
            ConnectionsManager.getInstance().sendRequest(req, /* anonymous class already generated */);
            return;
        }
        TL_messages_uninstallStickerSet req2 = new TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
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
                                StickersQuery.loadStickers(type, false, true);
                            }
                            Toast.makeText(context, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
                            StickersQuery.loadStickers(type, false, true);
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                });
            }
        });
    }
}
