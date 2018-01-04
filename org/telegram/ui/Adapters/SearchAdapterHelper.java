package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private HashMap<Integer, TLObject> globalSearchMap = new HashMap();
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

    public void queryServerSearch(final String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, int channelId, boolean kicked) {
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
            req.filter.q = query;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
            final int currentReqId = this.channelLastReqId + 1;
            this.channelLastReqId = currentReqId;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (currentReqId == SearchAdapterHelper.this.channelLastReqId && error == null) {
                                TL_channels_channelParticipants res = response;
                                SearchAdapterHelper.this.lastFoundChannel = query.toLowerCase();
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
                req.filter.q = query;
                req.limit = 50;
                req.offset = 0;
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
                final int currentReqId2 = this.channelLastReqId2 + 1;
                this.channelLastReqId2 = currentReqId2;
                this.channelReqId2 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (currentReqId2 == SearchAdapterHelper.this.channelLastReqId2 && error == null) {
                                    TL_channels_channelParticipants res = response;
                                    SearchAdapterHelper.this.lastFoundChannel2 = query.toLowerCase();
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
        if (!allowUsername) {
            return;
        }
        if (query.length() > 0) {
            req = new TL_contacts_search();
            req.q = query;
            req.limit = 50;
            currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            final boolean z = allowChats;
            final boolean z2 = allowBots;
            final boolean z3 = allowSelf;
            final String str = query;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (currentReqId == SearchAdapterHelper.this.lastReqId && error == null) {
                                int a;
                                Chat chat;
                                User user;
                                Peer peer;
                                TL_contacts_found res = response;
                                SearchAdapterHelper.this.globalSearch.clear();
                                SearchAdapterHelper.this.globalSearchMap.clear();
                                SearchAdapterHelper.this.localServerSearch.clear();
                                MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putChats(res.chats, false);
                                MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(res.users, false);
                                MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                HashMap<Integer, Chat> chatsMap = new HashMap();
                                HashMap<Integer, User> usersMap = new HashMap();
                                for (a = 0; a < res.chats.size(); a++) {
                                    chat = (Chat) res.chats.get(a);
                                    chatsMap.put(Integer.valueOf(chat.id), chat);
                                }
                                for (a = 0; a < res.users.size(); a++) {
                                    user = (User) res.users.get(a);
                                    usersMap.put(Integer.valueOf(user.id), user);
                                }
                                for (int b = 0; b < 2; b++) {
                                    ArrayList<Peer> arrayList;
                                    if (b != 0) {
                                        arrayList = res.results;
                                        for (a = 0; a < arrayList.size(); a++) {
                                            peer = (Peer) arrayList.get(a);
                                            user = null;
                                            chat = null;
                                            if (peer.user_id != 0) {
                                                user = (User) usersMap.get(Integer.valueOf(peer.user_id));
                                            } else if (peer.chat_id != 0) {
                                                chat = (Chat) chatsMap.get(Integer.valueOf(peer.chat_id));
                                            } else if (peer.channel_id != 0) {
                                                chat = (Chat) chatsMap.get(Integer.valueOf(peer.channel_id));
                                            }
                                            if (chat == null) {
                                                SearchAdapterHelper.this.globalSearch.add(user);
                                                SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(user.id), user);
                                            } else if (!z) {
                                                SearchAdapterHelper.this.globalSearch.add(chat);
                                                SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(-chat.id), chat);
                                            }
                                        }
                                    } else if (SearchAdapterHelper.this.allResultsAreGlobal) {
                                        arrayList = res.my_results;
                                        for (a = 0; a < arrayList.size(); a++) {
                                            peer = (Peer) arrayList.get(a);
                                            user = null;
                                            chat = null;
                                            if (peer.user_id != 0) {
                                                user = (User) usersMap.get(Integer.valueOf(peer.user_id));
                                            } else if (peer.chat_id != 0) {
                                                chat = (Chat) chatsMap.get(Integer.valueOf(peer.chat_id));
                                            } else if (peer.channel_id != 0) {
                                                chat = (Chat) chatsMap.get(Integer.valueOf(peer.channel_id));
                                            }
                                            if (chat == null) {
                                                if (!z) {
                                                    SearchAdapterHelper.this.globalSearch.add(chat);
                                                    SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(-chat.id), chat);
                                                }
                                            } else if (user != null && ((z2 || !user.bot) && (z3 || !user.self))) {
                                                SearchAdapterHelper.this.globalSearch.add(user);
                                                SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(user.id), user);
                                            }
                                        }
                                    }
                                }
                                if (!SearchAdapterHelper.this.allResultsAreGlobal) {
                                    for (a = 0; a < res.my_results.size(); a++) {
                                        peer = (Peer) res.my_results.get(a);
                                        user = null;
                                        chat = null;
                                        if (peer.user_id != 0) {
                                            user = (User) usersMap.get(Integer.valueOf(peer.user_id));
                                        } else if (peer.chat_id != 0) {
                                            chat = (Chat) chatsMap.get(Integer.valueOf(peer.chat_id));
                                        } else if (peer.channel_id != 0) {
                                            chat = (Chat) chatsMap.get(Integer.valueOf(peer.channel_id));
                                        }
                                        if (chat != null) {
                                            SearchAdapterHelper.this.localServerSearch.add(chat);
                                            SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(-chat.id), chat);
                                        } else if (user != null) {
                                            SearchAdapterHelper.this.localServerSearch.add(user);
                                            SearchAdapterHelper.this.globalSearchMap.put(Integer.valueOf(user.id), user);
                                        }
                                    }
                                }
                                SearchAdapterHelper.this.lastFoundUsername = str.toLowerCase();
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
            return;
        }
        this.globalSearch.clear();
        this.globalSearchMap.clear();
        this.localServerSearch.clear();
        this.lastReqId = 0;
        this.delegate.onDataSetChanged();
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
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
                    Collections.sort(arrayList, new Comparator<HashtagObject>() {
                        public int compare(HashtagObject lhs, HashtagObject rhs) {
                            if (lhs.date < rhs.date) {
                                return 1;
                            }
                            if (lhs.date > rhs.date) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            SearchAdapterHelper.this.setHashtags(arrayList, hashMap);
                        }
                    });
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
        return false;
    }

    public void mergeResults(ArrayList<TLObject> localResults) {
        this.localSearchResults = localResults;
        if (!this.globalSearchMap.isEmpty() && localResults != null) {
            int count = localResults.size();
            for (int a = 0; a < count; a++) {
                TLObject obj = (TLObject) localResults.get(a);
                if (obj instanceof User) {
                    User u = (User) this.globalSearchMap.get(Integer.valueOf(((User) obj).id));
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(Integer.valueOf(u.id));
                    }
                } else if (obj instanceof Chat) {
                    Chat c = (Chat) this.globalSearchMap.get(Integer.valueOf(-((Chat) obj).id));
                    if (c != null) {
                        this.globalSearch.remove(c);
                        this.localServerSearch.remove(c);
                        this.globalSearchMap.remove(Integer.valueOf(-c.id));
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
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
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
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().commitTransaction();
                    if (arrayList.size() >= 100) {
                        MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                        for (a = 100; a < arrayList.size(); a++) {
                            MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(a)).hashtag + "'").stepThis().dispose();
                        }
                        MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().commitTransaction();
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
    }

    public void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
