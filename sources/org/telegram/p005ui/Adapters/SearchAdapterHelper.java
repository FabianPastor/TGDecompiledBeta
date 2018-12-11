package org.telegram.p005ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsSearch;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_contacts_found;
import org.telegram.tgnet.TLRPC.TL_contacts_search;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper */
public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private int channelLastReqId;
    private int channelLastReqId2;
    private int channelReqId = 0;
    private int channelReqId2 = 0;
    private int currentAccount = UserConfig.selectedAccount;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList();
    private SparseArray<TLObject> globalSearchMap = new SparseArray();
    private ArrayList<ChannelParticipant> groupSearch = new ArrayList();
    private ArrayList<ChannelParticipant> groupSearch2 = new ArrayList();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundChannel;
    private String lastFoundChannel2;
    private String lastFoundUsername = null;
    private int lastReqId;
    private ArrayList<TLObject> localSearchResults;
    private ArrayList<TLObject> localServerSearch = new ArrayList();
    private int reqId = 0;

    /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate */
    public interface SearchAdapterHelperDelegate {
        SparseArray<User> getExcludeUsers();

        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$DialogSearchResult */
    protected static final class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        protected DialogSearchResult() {
        }
    }

    /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject */
    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public SearchAdapterHelper(boolean global) {
        this.allResultsAreGlobal = global;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, int channelId, boolean kicked) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (this.channelReqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId2, true);
            this.channelReqId2 = 0;
        }
        if (query == null) {
            this.groupSearch.clear();
            this.groupSearch2.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.channelLastReqId2 = 0;
            this.delegate.onDataSetChanged();
            return;
        }
        TL_channels_getParticipants req;
        int currentReqId;
        if (query.length() <= 0 || channelId == 0) {
            this.groupSearch.clear();
            this.groupSearch2.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
        } else {
            req = new TL_channels_getParticipants();
            if (kicked) {
                req.filter = new TL_channelParticipantsBanned();
            } else {
                req.filter = new TL_channelParticipantsSearch();
            }
            req.filter.var_q = query;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
            currentReqId = this.channelLastReqId + 1;
            this.channelLastReqId = currentReqId;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SearchAdapterHelper$$Lambda$0(this, currentReqId, query), 2);
            if (kicked) {
                req = new TL_channels_getParticipants();
                req.filter = new TL_channelParticipantsKicked();
                req.filter.var_q = query;
                req.limit = 50;
                req.offset = 0;
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                int currentReqId2 = this.channelLastReqId2 + 1;
                this.channelLastReqId2 = currentReqId2;
                this.channelReqId2 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SearchAdapterHelper$$Lambda$1(this, currentReqId2, query), 2);
            }
        }
        if (!allowUsername) {
            return;
        }
        if (query.length() > 0) {
            req = new TL_contacts_search();
            req.var_q = query;
            req.limit = 50;
            currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SearchAdapterHelper$$Lambda$2(this, currentReqId, allowChats, allowBots, allowSelf, query), 2);
            return;
        }
        this.globalSearch.clear();
        this.globalSearchMap.clear();
        this.localServerSearch.clear();
        this.lastReqId = 0;
        this.delegate.onDataSetChanged();
    }

    final /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int currentReqId, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$10(this, currentReqId, error, response, query));
    }

    final /* synthetic */ void lambda$null$0$SearchAdapterHelper(int currentReqId, TL_error error, TLObject response, String query) {
        if (currentReqId == this.channelLastReqId && error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            this.lastFoundChannel = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.groupSearch = res.participants;
            this.delegate.onDataSetChanged();
        }
        this.channelReqId = 0;
    }

    final /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int currentReqId2, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$9(this, currentReqId2, error, response, query));
    }

    final /* synthetic */ void lambda$null$2$SearchAdapterHelper(int currentReqId2, TL_error error, TLObject response, String query) {
        if (currentReqId2 == this.channelLastReqId2 && error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            this.lastFoundChannel2 = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.groupSearch2 = res.participants;
            this.delegate.onDataSetChanged();
        }
        this.channelReqId2 = 0;
    }

    final /* synthetic */ void lambda$queryServerSearch$5$SearchAdapterHelper(int currentReqId, boolean allowChats, boolean allowBots, boolean allowSelf, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$8(this, currentReqId, error, response, allowChats, allowBots, allowSelf, query));
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0092  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$null$4$SearchAdapterHelper(int currentReqId, TL_error error, TLObject response, boolean allowChats, boolean allowBots, boolean allowSelf, String query) {
        if (currentReqId == this.lastReqId && error == null) {
            int a;
            Chat chat;
            User user;
            Peer peer;
            TL_contacts_found res = (TL_contacts_found) response;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            SparseArray<Chat> chatsMap = new SparseArray();
            SparseArray<User> usersMap = new SparseArray();
            for (a = 0; a < res.chats.size(); a++) {
                chat = (Chat) res.chats.get(a);
                chatsMap.put(chat.var_id, chat);
            }
            for (a = 0; a < res.users.size(); a++) {
                user = (User) res.users.get(a);
                usersMap.put(user.var_id, user);
            }
            for (int b = 0; b < 2; b++) {
                ArrayList<Peer> arrayList;
                if (b != 0) {
                    arrayList = res.results;
                    while (a < arrayList.size()) {
                    }
                } else if (this.allResultsAreGlobal) {
                    arrayList = res.my_results;
                    for (a = 0; a < arrayList.size(); a++) {
                        peer = (Peer) arrayList.get(a);
                        user = null;
                        chat = null;
                        if (peer.user_id != 0) {
                            user = (User) usersMap.get(peer.user_id);
                        } else if (peer.chat_id != 0) {
                            chat = (Chat) chatsMap.get(peer.chat_id);
                        } else if (peer.channel_id != 0) {
                            chat = (Chat) chatsMap.get(peer.channel_id);
                        }
                        if (chat != null) {
                            if (allowChats) {
                                this.globalSearch.add(chat);
                                this.globalSearchMap.put(-chat.var_id, chat);
                            }
                        } else if (user != null && ((allowBots || !user.bot) && (allowSelf || !user.self))) {
                            this.globalSearch.add(user);
                            this.globalSearchMap.put(user.var_id, user);
                        }
                    }
                }
            }
            if (!this.allResultsAreGlobal) {
                for (a = 0; a < res.my_results.size(); a++) {
                    peer = (Peer) res.my_results.get(a);
                    user = null;
                    chat = null;
                    if (peer.user_id != 0) {
                        user = (User) usersMap.get(peer.user_id);
                    } else if (peer.chat_id != 0) {
                        chat = (Chat) chatsMap.get(peer.chat_id);
                    } else if (peer.channel_id != 0) {
                        chat = (Chat) chatsMap.get(peer.channel_id);
                    }
                    if (chat != null) {
                        this.localServerSearch.add(chat);
                        this.globalSearchMap.put(-chat.var_id, chat);
                    } else if (user != null) {
                        this.localServerSearch.add(user);
                        this.globalSearchMap.put(user.var_id, user);
                    }
                }
            }
            this.lastFoundUsername = query.toLowerCase();
            if (this.localSearchResults != null) {
                mergeResults(this.localSearchResults);
            }
            mergeExcluteResults();
            this.delegate.onDataSetChanged();
        }
        this.reqId = 0;
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$3(this));
        return false;
    }

    final /* synthetic */ void lambda$loadRecentHashtags$8$SearchAdapterHelper() {
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            ArrayList<HashtagObject> arrayList = new ArrayList();
            HashMap<String, HashtagObject> hashMap = new HashMap();
            while (cursor.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = cursor.stringValue(0);
                hashtagObject.date = cursor.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            cursor.dispose();
            Collections.sort(arrayList, SearchAdapterHelper$$Lambda$6.$instance);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$7(this, arrayList, hashMap));
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ int lambda$null$6$SearchAdapterHelper(HashtagObject lhs, HashtagObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void mergeResults(ArrayList<TLObject> localResults) {
        this.localSearchResults = localResults;
        if (this.globalSearchMap.size() != 0 && localResults != null) {
            int count = localResults.size();
            for (int a = 0; a < count; a++) {
                TLObject obj = (TLObject) localResults.get(a);
                if (obj instanceof User) {
                    User u = (User) this.globalSearchMap.get(((User) obj).var_id);
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.var_id);
                    }
                } else if (obj instanceof Chat) {
                    Chat c = (Chat) this.globalSearchMap.get(-((Chat) obj).var_id);
                    if (c != null) {
                        this.globalSearch.remove(c);
                        this.localServerSearch.remove(c);
                        this.globalSearchMap.remove(-c.var_id);
                    }
                }
            }
        }
    }

    public void mergeExcluteResults() {
        if (this.delegate != null) {
            SparseArray<User> ignoreUsers = this.delegate.getExcludeUsers();
            if (ignoreUsers != null) {
                int size = ignoreUsers.size();
                for (int a = 0; a < size; a++) {
                    User u = (User) this.globalSearchMap.get(ignoreUsers.keyAt(a));
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.var_id);
                    }
                }
            }
        }
    }

    public void setDelegate(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
        this.delegate = searchAdapterHelperDelegate;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        if (message != null) {
            boolean changed = false;
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@.]+").matcher(message);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(message.charAt(start) == '@' || message.charAt(start) == '#')) {
                    start++;
                }
                String hashtag = message.subSequence(start, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap();
                    this.hashtags = new ArrayList();
                }
                HashtagObject hashtagObject = (HashtagObject) this.hashtagsByText.get(hashtag);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = hashtag;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                changed = true;
            }
            if (changed) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$4(this, arrayList));
    }

    final /* synthetic */ void lambda$putRecentHashtags$9$SearchAdapterHelper(ArrayList arrayList) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
            int a = 0;
            while (a < arrayList.size() && a != 100) {
                HashtagObject hashtagObject = (HashtagObject) arrayList.get(a);
                state.requery();
                state.bindString(1, hashtagObject.hashtag);
                state.bindInteger(2, hashtagObject.date);
                state.step();
                a++;
            }
            state.dispose();
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            if (arrayList.size() >= 100) {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
                for (a = 100; a < arrayList.size(); a++) {
                    MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(a)).hashtag + "'").stepThis().dispose();
                }
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<TLObject> getLocalServerSearch() {
        return this.localServerSearch;
    }

    public ArrayList<ChannelParticipant> getGroupSearch() {
        return this.groupSearch;
    }

    public ArrayList<ChannelParticipant> getGroupSearch2() {
        return this.groupSearch2;
    }

    public ArrayList<HashtagObject> getHashtags() {
        return this.hashtags;
    }

    public String getLastFoundUsername() {
        return this.lastFoundUsername;
    }

    public String getLastFoundChannel() {
        return this.lastFoundChannel;
    }

    public String getLastFoundChannel2() {
        return this.lastFoundChannel2;
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList();
        this.hashtagsByText = new HashMap();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$5(this));
    }

    final /* synthetic */ void lambda$clearRecentHashtags$10$SearchAdapterHelper() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    /* renamed from: setHashtags */
    public void lambda$null$7$SearchAdapterHelper(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
