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
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_found;
import org.telegram.tgnet.TLRPC.TL_contacts_search;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

public class SearchAdapterHelper {
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundUsername = null;
    private int lastReqId;
    private int reqId = 0;

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public interface SearchAdapterHelperDelegate {
        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    public void queryServerSearch(String query, boolean allowChats, boolean allowBots, boolean allowSelf) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (query == null || query.length() < 5) {
            this.globalSearch.clear();
            this.lastReqId = 0;
            this.delegate.onDataSetChanged();
            return;
        }
        TL_contacts_search req = new TL_contacts_search();
        req.q = query;
        req.limit = 50;
        final int currentReqId = this.lastReqId + 1;
        this.lastReqId = currentReqId;
        final boolean z = allowChats;
        final boolean z2 = allowBots;
        final boolean z3 = allowSelf;
        final String str = query;
        this.reqId = ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (currentReqId == SearchAdapterHelper.this.lastReqId && error == null) {
                            int a;
                            TL_contacts_found res = response;
                            SearchAdapterHelper.this.globalSearch.clear();
                            if (z) {
                                for (a = 0; a < res.chats.size(); a++) {
                                    SearchAdapterHelper.this.globalSearch.add(res.chats.get(a));
                                }
                            }
                            for (a = 0; a < res.users.size(); a++) {
                                User user = (User) res.users.get(a);
                                if ((z2 || !user.bot) && (z3 || !user.self)) {
                                    SearchAdapterHelper.this.globalSearch.add(res.users.get(a));
                                }
                            }
                            SearchAdapterHelper.this.lastFoundUsername = str;
                            SearchAdapterHelper.this.delegate.onDataSetChanged();
                        }
                        SearchAdapterHelper.this.reqId = 0;
                    }
                });
            }
        }, 2);
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
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
                    FileLog.e("tmessages", e);
                }
            }
        });
        return false;
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
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance().getDatabase().beginTransaction();
                    SQLitePreparedStatement state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
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
                    MessagesStorage.getInstance().getDatabase().commitTransaction();
                    if (arrayList.size() >= 100) {
                        MessagesStorage.getInstance().getDatabase().beginTransaction();
                        for (a = 100; a < arrayList.size(); a++) {
                            MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(a)).hashtag + "'").stepThis().dispose();
                        }
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
            }
        });
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<HashtagObject> getHashtags() {
        return this.hashtags;
    }

    public String getLastFoundUsername() {
        return this.lastFoundUsername;
    }

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList();
        this.hashtagsByText = new HashMap();
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
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
