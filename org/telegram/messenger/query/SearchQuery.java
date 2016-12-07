package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.User;

public class SearchQuery {
    public static ArrayList<TL_topPeer> hints = new ArrayList();
    public static ArrayList<TL_topPeer> inlineBots = new ArrayList();
    private static HashMap<Integer, Integer> inlineDates = new HashMap();
    private static boolean loaded;
    private static boolean loading;

    public static void cleanup() {
        loading = false;
        loaded = false;
        hints.clear();
        inlineBots.clear();
        inlineDates.clear();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static void loadHints(boolean cache) {
        if (!loading) {
            if (!cache) {
                loading = true;
                TL_contacts_getTopPeers req = new TL_contacts_getTopPeers();
                req.hash = 0;
                req.bots_pm = false;
                req.correspondents = true;
                req.groups = false;
                req.channels = false;
                req.bots_inline = true;
                req.offset = 0;
                req.limit = 20;
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        if (response instanceof TL_contacts_topPeers) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    final TL_contacts_topPeers topPeers = response;
                                    MessagesController.getInstance().putUsers(topPeers.users, false);
                                    MessagesController.getInstance().putChats(topPeers.chats, false);
                                    for (int a = 0; a < topPeers.categories.size(); a++) {
                                        TL_topPeerCategoryPeers category = (TL_topPeerCategoryPeers) topPeers.categories.get(a);
                                        if (category.category instanceof TL_topPeerCategoryBotsInline) {
                                            SearchQuery.inlineBots = category.peers;
                                        } else {
                                            SearchQuery.hints = category.peers;
                                        }
                                    }
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    final HashMap<Integer, Integer> inlineDatesCopy = new HashMap(SearchQuery.inlineDates);
                                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                                        public void run() {
                                            try {
                                                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                                                MessagesStorage.getInstance().getDatabase().beginTransaction();
                                                MessagesStorage.getInstance().putUsersAndChats(topPeers.users, topPeers.chats, false, false);
                                                SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
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
                                                        int intValue;
                                                        TL_topPeer peer = (TL_topPeer) category.peers.get(b);
                                                        if (peer.peer instanceof TL_peerUser) {
                                                            did = peer.peer.user_id;
                                                        } else if (peer.peer instanceof TL_peerChat) {
                                                            did = -peer.peer.chat_id;
                                                        } else {
                                                            did = -peer.peer.channel_id;
                                                        }
                                                        Integer date = (Integer) inlineDatesCopy.get(Integer.valueOf(did));
                                                        state.requery();
                                                        state.bindInteger(1, did);
                                                        state.bindInteger(2, type);
                                                        state.bindDouble(3, peer.rating);
                                                        if (date != null) {
                                                            intValue = date.intValue();
                                                        } else {
                                                            intValue = 0;
                                                        }
                                                        state.bindInteger(4, intValue);
                                                        state.step();
                                                    }
                                                }
                                                state.dispose();
                                                MessagesStorage.getInstance().getDatabase().commitTransaction();
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        UserConfig.lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
                                                        UserConfig.saveConfig(false);
                                                    }
                                                });
                                            } catch (Throwable e) {
                                                FileLog.e("tmessages", e);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else if (!loaded) {
                loading = true;
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        final ArrayList<TL_topPeer> hintsNew = new ArrayList();
                        final ArrayList<TL_topPeer> inlineBotsNew = new ArrayList();
                        final HashMap<Integer, Integer> inlineDatesNew = new HashMap();
                        final ArrayList<User> users = new ArrayList();
                        final ArrayList<Chat> chats = new ArrayList();
                        try {
                            ArrayList<Integer> usersToLoad = new ArrayList();
                            ArrayList<Integer> chatsToLoad = new ArrayList();
                            SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating, date FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
                            while (cursor.next()) {
                                int did = cursor.intValue(0);
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
                                    inlineDatesNew.put(Integer.valueOf(did), Integer.valueOf(cursor.intValue(3)));
                                }
                            }
                            cursor.dispose();
                            if (!usersToLoad.isEmpty()) {
                                MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                            }
                            if (!chatsToLoad.isEmpty()) {
                                MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.getInstance().putUsers(users, true);
                                    MessagesController.getInstance().putChats(chats, true);
                                    SearchQuery.loading = false;
                                    SearchQuery.loaded = true;
                                    SearchQuery.hints = hintsNew;
                                    SearchQuery.inlineBots = inlineBotsNew;
                                    SearchQuery.inlineDates = inlineDatesNew;
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                                    if (Math.abs(UserConfig.lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
                                        SearchQuery.loadHints(false);
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                });
                loaded = true;
            }
        }
    }

    public static void increaseInlineRaiting(int uid) {
        int dt;
        Integer time = (Integer) inlineDates.get(Integer.valueOf(uid));
        if (time != null) {
            dt = Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - time.intValue());
        } else {
            dt = 60;
        }
        TL_topPeer peer = null;
        for (int a = 0; a < inlineBots.size(); a++) {
            TL_topPeer p = (TL_topPeer) inlineBots.get(a);
            if (p.peer.user_id == uid) {
                peer = p;
                break;
            }
        }
        if (peer == null) {
            peer = new TL_topPeer();
            peer.peer = new TL_peerUser();
            peer.peer.user_id = uid;
            inlineBots.add(peer);
        }
        peer.rating += Math.exp((double) (dt / MessagesController.getInstance().ratingDecay));
        Collections.sort(inlineBots, new Comparator<TL_topPeer>() {
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
        if (inlineBots.size() > 20) {
            inlineBots.remove(inlineBots.size() - 1);
        }
        savePeer(uid, 1, peer.rating);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public static void removeInline(int uid) {
        for (int a = 0; a < inlineBots.size(); a++) {
            if (((TL_topPeer) inlineBots.get(a)).peer.user_id == uid) {
                inlineBots.remove(a);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryBotsInline();
                req.peer = MessagesController.getInputPeer(uid);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                deletePeer(uid, 1);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public static void removePeer(int uid) {
        for (int a = 0; a < hints.size(); a++) {
            if (((TL_topPeer) hints.get(a)).peer.user_id == uid) {
                hints.remove(a);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TL_contacts_resetTopPeerRating req = new TL_contacts_resetTopPeerRating();
                req.category = new TL_topPeerCategoryCorrespondents();
                req.peer = MessagesController.getInputPeer(uid);
                deletePeer(uid, 0);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                return;
            }
        }
    }

    public static void increasePeerRaiting(final long did) {
        final int lower_id = (int) did;
        if (lower_id > 0) {
            User user = lower_id > 0 ? MessagesController.getInstance().getUser(Integer.valueOf(lower_id)) : null;
            if (user != null && !user.bot) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        double dt = 0.0d;
                        int lastTime = 0;
                        int lastMid = 0;
                        try {
                            SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(did)}), new Object[0]);
                            if (cursor.next()) {
                                lastMid = cursor.intValue(0);
                                lastTime = cursor.intValue(1);
                            }
                            cursor.dispose();
                            if (lastMid > 0) {
                                cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT date FROM messages WHERE uid = %d AND mid < %d AND out = 1 ORDER BY date DESC", new Object[]{Long.valueOf(did), Integer.valueOf(lastMid)}), new Object[0]);
                                if (cursor.next()) {
                                    dt = (double) (lastTime - cursor.intValue(0));
                                }
                                cursor.dispose();
                            }
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                        final double dtFinal = dt;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TL_topPeer peer = null;
                                for (int a = 0; a < SearchQuery.hints.size(); a++) {
                                    TL_topPeer p = (TL_topPeer) SearchQuery.hints.get(a);
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
                                    SearchQuery.hints.add(peer);
                                }
                                peer.rating += Math.exp(dtFinal / ((double) MessagesController.getInstance().ratingDecay));
                                Collections.sort(SearchQuery.hints, new Comparator<TL_topPeer>() {
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
                                SearchQuery.savePeer((int) did, 0, peer.rating);
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                            }
                        });
                    }
                });
            }
        }
    }

    private static void savePeer(final int did, final int type, final double rating) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                    state.requery();
                    state.bindInteger(1, did);
                    state.bindInteger(2, type);
                    state.bindDouble(3, rating);
                    state.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    private static void deletePeer(final int did, final int type) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Integer.valueOf(did), Integer.valueOf(type)})).stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }
}
