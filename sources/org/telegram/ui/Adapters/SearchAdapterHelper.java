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
    class C08044 implements Runnable {

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$4$1 */
        class C08021 implements Comparator<HashtagObject> {
            C08021() {
            }

            public int compare(HashtagObject hashtagObject, HashtagObject hashtagObject2) {
                if (hashtagObject.date < hashtagObject2.date) {
                    return 1;
                }
                return hashtagObject.date > hashtagObject2.date ? -1 : null;
            }
        }

        C08044() {
        }

        public void run() {
            try {
                SQLiteCursor queryFinalized = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
                final Object arrayList = new ArrayList();
                final HashMap hashMap = new HashMap();
                while (queryFinalized.next()) {
                    HashtagObject hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = queryFinalized.stringValue(0);
                    hashtagObject.date = queryFinalized.intValue(1);
                    arrayList.add(hashtagObject);
                    hashMap.put(hashtagObject.hashtag, hashtagObject);
                }
                queryFinalized.dispose();
                Collections.sort(arrayList, new C08021());
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
    class C08066 implements Runnable {
        C08066() {
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

    public SearchAdapterHelper(boolean z) {
        this.allResultsAreGlobal = z;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, int i, boolean z5) {
        final String str2 = str;
        int i2 = i;
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
        if (str2 == null) {
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
        if (str2.length() <= 0 || i2 == 0) {
            r7.groupSearch.clear();
            r7.groupSearch2.clear();
            r7.channelLastReqId = 0;
            r7.delegate.onDataSetChanged();
        } else {
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            if (z5) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsBanned();
            } else {
                tL_channels_getParticipants.filter = new TL_channelParticipantsSearch();
            }
            tL_channels_getParticipants.filter.f32q = str2;
            tL_channels_getParticipants.limit = 50;
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.channel = MessagesController.getInstance(r7.currentAccount).getInputChannel(i2);
            final int i3 = r7.channelLastReqId + 1;
            r7.channelLastReqId = i3;
            r7.channelReqId = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (i3 == SearchAdapterHelper.this.channelLastReqId && tL_error == null) {
                                TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                SearchAdapterHelper.this.lastFoundChannel = str2.toLowerCase();
                                MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                                SearchAdapterHelper.this.groupSearch = tL_channels_channelParticipants.participants;
                                SearchAdapterHelper.this.delegate.onDataSetChanged();
                            }
                            SearchAdapterHelper.this.channelReqId = 0;
                        }
                    });
                }
            }, 2);
            if (z5) {
                TLObject tL_channels_getParticipants2 = new TL_channels_getParticipants();
                tL_channels_getParticipants2.filter = new TL_channelParticipantsKicked();
                tL_channels_getParticipants2.filter.f32q = str2;
                tL_channels_getParticipants2.limit = 50;
                tL_channels_getParticipants2.offset = 0;
                tL_channels_getParticipants2.channel = MessagesController.getInstance(r7.currentAccount).getInputChannel(i2);
                i2 = r7.channelLastReqId2 + 1;
                r7.channelLastReqId2 = i2;
                r7.channelReqId2 = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_channels_getParticipants2, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (i2 == SearchAdapterHelper.this.channelLastReqId2 && tL_error == null) {
                                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                    SearchAdapterHelper.this.lastFoundChannel2 = str2.toLowerCase();
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                                    SearchAdapterHelper.this.groupSearch2 = tL_channels_channelParticipants.participants;
                                    SearchAdapterHelper.this.delegate.onDataSetChanged();
                                }
                                SearchAdapterHelper.this.channelReqId2 = 0;
                            }
                        });
                    }
                }, 2);
            }
        }
        if (z) {
            if (str2.length() > 0) {
                TLObject tL_contacts_search = new TL_contacts_search();
                tL_contacts_search.f45q = str2;
                tL_contacts_search.limit = 50;
                final int i4 = r7.lastReqId + 1;
                r7.lastReqId = i4;
                final boolean z6 = z2;
                final boolean z7 = z3;
                final boolean z8 = z4;
                r7.reqId = ConnectionsManager.getInstance(r7.currentAccount).sendRequest(tL_contacts_search, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (i4 == SearchAdapterHelper.this.lastReqId && tL_error == null) {
                                    int i;
                                    Chat chat;
                                    TL_contacts_found tL_contacts_found = (TL_contacts_found) tLObject;
                                    SearchAdapterHelper.this.globalSearch.clear();
                                    SearchAdapterHelper.this.globalSearchMap.clear();
                                    SearchAdapterHelper.this.localServerSearch.clear();
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putChats(tL_contacts_found.chats, false);
                                    MessagesController.getInstance(SearchAdapterHelper.this.currentAccount).putUsers(tL_contacts_found.users, false);
                                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).putUsersAndChats(tL_contacts_found.users, tL_contacts_found.chats, true, true);
                                    SparseArray sparseArray = new SparseArray();
                                    SparseArray sparseArray2 = new SparseArray();
                                    for (i = 0; i < tL_contacts_found.chats.size(); i++) {
                                        chat = (Chat) tL_contacts_found.chats.get(i);
                                        sparseArray.put(chat.id, chat);
                                    }
                                    for (i = 0; i < tL_contacts_found.users.size(); i++) {
                                        User user = (User) tL_contacts_found.users.get(i);
                                        sparseArray2.put(user.id, user);
                                    }
                                    for (i = 0; i < 2; i++) {
                                        ArrayList arrayList;
                                        if (i != 0) {
                                            arrayList = tL_contacts_found.results;
                                        } else if (SearchAdapterHelper.this.allResultsAreGlobal) {
                                            arrayList = tL_contacts_found.my_results;
                                        } else {
                                        }
                                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                                            User user2;
                                            Chat chat2;
                                            Peer peer = (Peer) arrayList.get(i2);
                                            if (peer.user_id != 0) {
                                                user2 = (User) sparseArray2.get(peer.user_id);
                                                chat2 = null;
                                            } else {
                                                if (peer.chat_id != 0) {
                                                    chat2 = (Chat) sparseArray.get(peer.chat_id);
                                                } else if (peer.channel_id != 0) {
                                                    chat2 = (Chat) sparseArray.get(peer.channel_id);
                                                } else {
                                                    chat2 = null;
                                                    user2 = chat2;
                                                }
                                                user2 = null;
                                            }
                                            if (chat2 != null) {
                                                if (z6) {
                                                    SearchAdapterHelper.this.globalSearch.add(chat2);
                                                    SearchAdapterHelper.this.globalSearchMap.put(-chat2.id, chat2);
                                                }
                                            } else if (user2 != null && (z7 || !user2.bot)) {
                                                if (z8 || !user2.self) {
                                                    SearchAdapterHelper.this.globalSearch.add(user2);
                                                    SearchAdapterHelper.this.globalSearchMap.put(user2.id, user2);
                                                }
                                            }
                                        }
                                    }
                                    if (!SearchAdapterHelper.this.allResultsAreGlobal) {
                                        for (i = 0; i < tL_contacts_found.my_results.size(); i++) {
                                            User user3;
                                            Peer peer2 = (Peer) tL_contacts_found.my_results.get(i);
                                            if (peer2.user_id != 0) {
                                                user3 = (User) sparseArray2.get(peer2.user_id);
                                                chat = null;
                                            } else {
                                                if (peer2.chat_id != 0) {
                                                    chat = (Chat) sparseArray.get(peer2.chat_id);
                                                } else if (peer2.channel_id != 0) {
                                                    chat = (Chat) sparseArray.get(peer2.channel_id);
                                                } else {
                                                    chat = null;
                                                    user3 = chat;
                                                }
                                                user3 = null;
                                            }
                                            if (chat != null) {
                                                SearchAdapterHelper.this.localServerSearch.add(chat);
                                                SearchAdapterHelper.this.globalSearchMap.put(-chat.id, chat);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C08044());
        return false;
    }

    public void mergeResults(ArrayList<TLObject> arrayList) {
        this.localSearchResults = arrayList;
        if (this.globalSearchMap.size() != 0) {
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    TLObject tLObject = (TLObject) arrayList.get(i);
                    if (tLObject instanceof User) {
                        User user = (User) this.globalSearchMap.get(((User) tLObject).id);
                        if (user != null) {
                            this.globalSearch.remove(user);
                            this.localServerSearch.remove(user);
                            this.globalSearchMap.remove(user.id);
                        }
                    } else if (tLObject instanceof Chat) {
                        Chat chat = (Chat) this.globalSearchMap.get(-((Chat) tLObject).id);
                        if (chat != null) {
                            this.globalSearch.remove(chat);
                            this.localServerSearch.remove(chat);
                            this.globalSearchMap.remove(-chat.id);
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
        this.delegate = searchAdapterHelperDelegate;
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        if (charSequence != null) {
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(charSequence);
            int i = 0;
            while (matcher.find()) {
                i = matcher.start();
                int end = matcher.end();
                if (!(charSequence.charAt(i) == '@' || charSequence.charAt(i) == '#')) {
                    i++;
                }
                String charSequence2 = charSequence.subSequence(i, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap();
                    this.hashtags = new ArrayList();
                }
                HashtagObject hashtagObject = (HashtagObject) this.hashtagsByText.get(charSequence2);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = charSequence2;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                i = 1;
            }
            if (i != 0) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(final ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    int i;
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
                    int i2 = 0;
                    while (true) {
                        i = 100;
                        if (i2 >= arrayList.size()) {
                            break;
                        } else if (i2 == 100) {
                            break;
                        } else {
                            HashtagObject hashtagObject = (HashtagObject) arrayList.get(i2);
                            executeFast.requery();
                            executeFast.bindString(1, hashtagObject.hashtag);
                            executeFast.bindInteger(2, hashtagObject.date);
                            executeFast.step();
                            i2++;
                        }
                    }
                    executeFast.dispose();
                    MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().commitTransaction();
                    if (arrayList.size() >= 100) {
                        MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase().beginTransaction();
                        while (i < arrayList.size()) {
                            SQLiteDatabase database = MessagesStorage.getInstance(SearchAdapterHelper.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM hashtag_recent_v2 WHERE id = '");
                            stringBuilder.append(((HashtagObject) arrayList.get(i)).hashtag);
                            stringBuilder.append("'");
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                            i++;
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C08066());
    }

    public void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
