package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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

    /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$4 */
    class C07974 implements Runnable {

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$4$1 */
        class C07951 implements Comparator<HashtagObject> {
            C07951() {
            }

            public int compare(HashtagObject lhs, HashtagObject rhs) {
                if (lhs.date < rhs.date) {
                    return 1;
                }
                if (lhs.date > rhs.date) {
                    return -1;
                }
                return 0;
            }
        }

        C07974() {
        }

        public void run() {
            try {
                SQLiteCursor cursor = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
                final ArrayList<HashtagObject> arrayList = new ArrayList();
                final HashMap<String, HashtagObject> hashMap = new HashMap();
                while (cursor.next()) {
                    HashtagObject hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = cursor.stringValue(0);
                    hashtagObject.date = cursor.intValue(1);
                    arrayList.add(hashtagObject);
                    hashMap.put(hashtagObject.hashtag, hashtagObject);
                }
                cursor.dispose();
                Collections.sort(arrayList, new C07951());
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        SearchAdapterHelper.this.setHashtags(arrayList, hashMap);
                    }
                });
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$6 */
    class C07996 implements Runnable {
        C07996() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    protected static final class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        protected DialogSearchResult() {
        }
    }

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public interface SearchAdapterHelperDelegate {
        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    public SearchAdapterHelper(boolean global) {
        this.allResultsAreGlobal = global;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, int channelId, boolean kicked) {
        final String str = query;
        int i = channelId;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(r7.currentAccount).cancelRequest(r7.reqId, true);
            r7.reqId = 0;
        }
        if (r7.channelReqId != 0) {
            ConnectionsManager.getInstance(r7.currentAccount).cancelRequest(r7.channelReqId, true);
            r7.channelReqId = 0;
        }
        if (r7.channelReqId2 != 0) {
            ConnectionsManager.getInstance(r7.currentAccount).cancelRequest(r7.channelReqId2, true);
            r7.channelReqId2 = 0;
        }
        if (str == null) {
            r7.groupSearch.clear();
            r7.groupSearch2.clear();
            r7.globalSearch.clear();
            r7.globalSearchMap.clear();
            r7.localServerSearch.clear();
            r7.lastReqId = 0;
            r7.channelLastReqId = 0;
            r7.channelLastReqId2 = 0;
            r7.delegate.onDataSetChanged();
            return;
        }
        if (query.length() <= 0 || i == 0) {
            r7.groupSearch.clear();
            r7.groupSearch2.clear();
            r7.channelLastReqId = 0;
            r7.delegate.onDataSetChanged();
        } else {
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            if (kicked) {
                req.filter = new TL_channelParticipantsBanned();
            } else {
                req.filter = new TL_channelParticipantsSearch();
            }
            req.filter.f32q = str;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(r7.currentAccount).getInputChannel(i);
            final int currentReqId = r7.channelLastReqId + 1;
            r7.channelLastReqId = currentReqId;
            r7.channelReqId = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (currentReqId == SearchAdapterHelper.this.channelLastReqId && error == null) {
                                TL_channels_channelParticipants res = response;
                                SearchAdapterHelper.this.lastFoundChannel = str.toLowerCase();
                                MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(res.users, false);
                                SearchAdapterHelper.this.groupSearch = res.participants;
                                SearchAdapterHelper.this.delegate.onDataSetChanged();
                            }
                            SearchAdapterHelper.this.channelReqId = 0;
                        }
                    });
                }
            }, 2);
            if (kicked) {
                req = new TL_channels_getParticipants();
                req.filter = new TL_channelParticipantsKicked();
                req.filter.f32q = str;
                req.limit = 50;
                req.offset = 0;
                req.channel = MessagesController.getInstance(r7.currentAccount).getInputChannel(i);
                final int currentReqId2 = r7.channelLastReqId2 + 1;
                r7.channelLastReqId2 = currentReqId2;
                r7.channelReqId2 = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (currentReqId2 == SearchAdapterHelper.this.channelLastReqId2 && error == null) {
                                    TL_channels_channelParticipants res = response;
                                    SearchAdapterHelper.this.lastFoundChannel2 = str.toLowerCase();
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(res.users, false);
                                    SearchAdapterHelper.this.groupSearch2 = res.participants;
                                    SearchAdapterHelper.this.delegate.onDataSetChanged();
                                }
                                SearchAdapterHelper.this.channelReqId2 = 0;
                            }
                        });
                    }
                }, 2);
            }
        }
        if (allowUsername) {
            if (query.length() > 0) {
                TL_contacts_search req2 = new TL_contacts_search();
                req2.f45q = str;
                req2.limit = 50;
                final int currentReqId3 = r7.lastReqId + 1;
                r7.lastReqId = currentReqId3;
                final boolean z = allowChats;
                final boolean z2 = allowBots;
                final boolean z3 = allowSelf;
                final String str2 = str;
                r7.reqId = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (currentReqId3 == SearchAdapterHelper.this.lastReqId && error == null) {
                                    int a;
                                    TL_contacts_found res = response;
                                    SearchAdapterHelper.this.globalSearch.clear();
                                    SearchAdapterHelper.this.globalSearchMap.clear();
                                    SearchAdapterHelper.this.localServerSearch.clear();
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putChats(res.chats, false);
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(res.users, false);
                                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                    SparseArray<Chat> chatsMap = new SparseArray();
                                    SparseArray<User> usersMap = new SparseArray();
                                    for (a = 0; a < res.chats.size(); a++) {
                                        Chat chat = (Chat) res.chats.get(a);
                                        chatsMap.put(chat.id, chat);
                                    }
                                    for (a = 0; a < res.users.size(); a++) {
                                        User user = (User) res.users.get(a);
                                        usersMap.put(user.id, user);
                                    }
                                    for (a = 0; a < 2; a++) {
                                        ArrayList<Peer> arrayList;
                                        if (a != 0) {
                                            arrayList = res.results;
                                        } else if (SearchAdapterHelper.this.allResultsAreGlobal) {
                                            arrayList = res.my_results;
                                        } else {
                                        }
                                        for (int a2 = 0; a2 < arrayList.size(); a2++) {
                                            Peer peer = (Peer) arrayList.get(a2);
                                            User user2 = null;
                                            Chat chat2 = null;
                                            if (peer.user_id != 0) {
                                                user2 = (User) usersMap.get(peer.user_id);
                                            } else if (peer.chat_id != 0) {
                                                chat2 = (Chat) chatsMap.get(peer.chat_id);
                                            } else if (peer.channel_id != 0) {
                                                chat2 = (Chat) chatsMap.get(peer.channel_id);
                                            }
                                            if (chat2 != null) {
                                                if (z) {
                                                    SearchAdapterHelper.this.globalSearch.add(chat2);
                                                    SearchAdapterHelper.this.globalSearchMap.put(-chat2.id, chat2);
                                                }
                                            } else if (user2 != null && (z2 || !user2.bot)) {
                                                if (z3 || !user2.self) {
                                                    SearchAdapterHelper.this.globalSearch.add(user2);
                                                    SearchAdapterHelper.this.globalSearchMap.put(user2.id, user2);
                                                }
                                            }
                                        }
                                    }
                                    if (!SearchAdapterHelper.this.allResultsAreGlobal) {
                                        for (a = 0; a < res.my_results.size(); a++) {
                                            Peer peer2 = (Peer) res.my_results.get(a);
                                            User user3 = null;
                                            Chat chat3 = null;
                                            if (peer2.user_id != 0) {
                                                user3 = (User) usersMap.get(peer2.user_id);
                                            } else if (peer2.chat_id != 0) {
                                                chat3 = (Chat) chatsMap.get(peer2.chat_id);
                                            } else if (peer2.channel_id != 0) {
                                                chat3 = (Chat) chatsMap.get(peer2.channel_id);
                                            }
                                            if (chat3 != null) {
                                                SearchAdapterHelper.this.localServerSearch.add(chat3);
                                                SearchAdapterHelper.this.globalSearchMap.put(-chat3.id, chat3);
                                            } else if (user3 != null) {
                                                SearchAdapterHelper.this.localServerSearch.add(user3);
                                                SearchAdapterHelper.this.globalSearchMap.put(user3.id, user3);
                                            }
                                        }
                                    }
                                    SearchAdapterHelper.this.lastFoundUsername = str2.toLowerCase();
                                    if (SearchAdapterHelper.this.localSearchResults != null) {
                                        SearchAdapterHelper.this.mergeResults(SearchAdapterHelper.this.localSearchResults);
                                    }
                                    SearchAdapterHelper.this.delegate.onDataSetChanged();
                                }
                                SearchAdapterHelper.this.reqId = 0;
                            }
                        });
                    }
                }, 2);
            } else {
                r7.globalSearch.clear();
                r7.globalSearchMap.clear();
                r7.localServerSearch.clear();
                r7.lastReqId = 0;
                r7.delegate.onDataSetChanged();
            }
        }
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C07974());
        return false;
    }

    public void mergeResults(ArrayList<TLObject> localResults) {
        this.localSearchResults = localResults;
        if (this.globalSearchMap.size() != 0) {
            if (localResults != null) {
                int count = localResults.size();
                for (int a = 0; a < count; a++) {
                    TLObject obj = (TLObject) localResults.get(a);
                    if (obj instanceof User) {
                        User u = (User) this.globalSearchMap.get(((User) obj).id);
                        if (u != null) {
                            this.globalSearch.remove(u);
                            this.localServerSearch.remove(u);
                            this.globalSearchMap.remove(u.id);
                        }
                    } else if (obj instanceof Chat) {
                        Chat c = (Chat) this.globalSearchMap.get(-((Chat) obj).id);
                        if (c != null) {
                            this.globalSearch.remove(c);
                            this.localServerSearch.remove(c);
                            this.globalSearchMap.remove(-c.id);
                        }
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
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(message);
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

    private void putRecentHashtags(final ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    int a;
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
                    int a2 = 0;
                    while (true) {
                        a = 100;
                        if (a2 >= arrayList.size()) {
                            break;
                        } else if (a2 == 100) {
                            break;
                        } else {
                            HashtagObject hashtagObject = (HashtagObject) arrayList.get(a2);
                            state.requery();
                            state.bindString(1, hashtagObject.hashtag);
                            state.bindInteger(2, hashtagObject.date);
                            state.step();
                            a2++;
                        }
                    }
                    state.dispose();
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().commitTransaction();
                    if (arrayList.size() >= 100) {
                        MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                        while (true) {
                            a2 = a;
                            if (a2 >= arrayList.size()) {
                                break;
                            }
                            SQLiteDatabase database = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM hashtag_recent_v2 WHERE id = '");
                            stringBuilder.append(((HashtagObject) arrayList.get(a2)).hashtag);
                            stringBuilder.append("'");
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            a = a2 + 1;
                        }
                        MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().commitTransaction();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C07996());
    }

    public void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
