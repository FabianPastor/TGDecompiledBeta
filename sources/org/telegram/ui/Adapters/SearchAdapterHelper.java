package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
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
    private int channelReqId = 0;
    private int currentAccount = UserConfig.selectedAccount;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList();
    private SparseArray<TLObject> globalSearchMap = new SparseArray();
    private ArrayList<TLObject> groupSearch = new ArrayList();
    private SparseArray<TLObject> groupSearchMap = new SparseArray();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundChannel;
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

        public final /* synthetic */ class -CC {
            public static SparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
                return null;
            }
        }

        SparseArray<User> getExcludeUsers();

        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    public SearchAdapterHelper(boolean z) {
        this.allResultsAreGlobal = z;
    }

    public boolean isSearchInProgress() {
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, int i, int i2) {
        String str2 = str;
        int i3 = i;
        int i4 = i2;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (str2 == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
            return;
        }
        boolean z5;
        if (str.length() <= 0) {
            z5 = z4;
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
        } else if (i3 != 0) {
            TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
            if (i4 == 1) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
            } else if (i4 == 3) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsBanned();
            } else if (i4 == 0) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsKicked();
            } else {
                tL_channels_getParticipants.filter = new TL_channelParticipantsSearch();
            }
            tL_channels_getParticipants.filter.q = str2;
            tL_channels_getParticipants.limit = 50;
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i3);
            i3 = this.channelLastReqId + 1;
            this.channelLastReqId = i3;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new -$$Lambda$SearchAdapterHelper$lQm_KegCMNFAimI1TKtkhyCcOJg(this, i3, str, z4), 2);
        } else {
            z5 = z4;
            this.lastFoundChannel = str.toLowerCase();
        }
        if (z) {
            if (str.length() > 0) {
                TL_contacts_search tL_contacts_search = new TL_contacts_search();
                tL_contacts_search.q = str2;
                tL_contacts_search.limit = 50;
                int i5 = this.lastReqId + 1;
                this.lastReqId = i5;
                this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_search, new -$$Lambda$SearchAdapterHelper$dscYso9YL4gEzQpoIdpN_Bu9BdA(this, i5, z2, z3, z4, str), 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged();
            }
        }
    }

    public /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int i, String str, boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$v5cwP_i-1geBZNEja0OzJuPoFoM(this, i, tL_error, tLObject, str, z));
    }

    public /* synthetic */ void lambda$null$0$SearchAdapterHelper(int i, TL_error tL_error, TLObject tLObject, String str, boolean z) {
        if (i == this.channelLastReqId && tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            this.lastFoundChannel = str.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(tL_channels_channelParticipants.participants);
            i = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int size = tL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i2);
                if (z || channelParticipant.user_id != i) {
                    this.groupSearchMap.put(channelParticipant.user_id, channelParticipant);
                } else {
                    this.groupSearch.remove(channelParticipant);
                }
            }
            ArrayList arrayList = this.localSearchResults;
            if (arrayList != null) {
                mergeResults(arrayList);
            }
            this.delegate.onDataSetChanged();
        }
        this.channelReqId = 0;
    }

    public /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int i, boolean z, boolean z2, boolean z3, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$BW0zXscUb5FIQ5wJIg2kAIcfk-Q(this, i, tL_error, tLObject, z, z2, z3, str));
    }

    public /* synthetic */ void lambda$null$2$SearchAdapterHelper(int i, TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, String str) {
        if (i == this.lastReqId && tL_error == null) {
            int i2;
            TL_contacts_found tL_contacts_found = (TL_contacts_found) tLObject;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_found.chats, false);
            MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_found.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_found.users, tL_contacts_found.chats, true, true);
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (i2 = 0; i2 < tL_contacts_found.chats.size(); i2++) {
                Chat chat = (Chat) tL_contacts_found.chats.get(i2);
                sparseArray.put(chat.id, chat);
            }
            for (i2 = 0; i2 < tL_contacts_found.users.size(); i2++) {
                User user = (User) tL_contacts_found.users.get(i2);
                sparseArray2.put(user.id, user);
            }
            for (i2 = 0; i2 < 2; i2++) {
                ArrayList arrayList;
                if (i2 != 0) {
                    arrayList = tL_contacts_found.results;
                } else if (this.allResultsAreGlobal) {
                    arrayList = tL_contacts_found.my_results;
                } else {
                }
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    User user2;
                    Object obj;
                    Peer peer = (Peer) arrayList.get(i3);
                    int i4 = peer.user_id;
                    if (i4 != 0) {
                        user2 = (User) sparseArray2.get(i4);
                        obj = null;
                    } else {
                        i4 = peer.chat_id;
                        if (i4 != 0) {
                            obj = (Chat) sparseArray.get(i4);
                        } else {
                            int i5 = peer.channel_id;
                            if (i5 != 0) {
                                Chat chat2 = (Chat) sparseArray.get(i5);
                            } else {
                                obj = null;
                                user2 = obj;
                            }
                        }
                        user2 = null;
                    }
                    if (obj != null) {
                        if (z) {
                            this.globalSearch.add(obj);
                            this.globalSearchMap.put(-obj.id, obj);
                        }
                    } else if (user2 != null && ((z2 || !user2.bot) && (z3 || !user2.self))) {
                        this.globalSearch.add(user2);
                        this.globalSearchMap.put(user2.id, user2);
                    }
                }
            }
            if (!this.allResultsAreGlobal) {
                for (int i6 = 0; i6 < tL_contacts_found.my_results.size(); i6++) {
                    Object obj2;
                    Object obj3;
                    Peer peer2 = (Peer) tL_contacts_found.my_results.get(i6);
                    int i7 = peer2.user_id;
                    if (i7 != 0) {
                        obj2 = (User) sparseArray2.get(i7);
                        obj3 = null;
                    } else {
                        i7 = peer2.chat_id;
                        if (i7 != 0) {
                            obj3 = (Chat) sparseArray.get(i7);
                        } else {
                            int i8 = peer2.channel_id;
                            if (i8 != 0) {
                                Chat chat3 = (Chat) sparseArray.get(i8);
                            } else {
                                obj3 = null;
                                obj2 = obj3;
                            }
                        }
                        obj2 = null;
                    }
                    if (obj3 != null) {
                        this.localServerSearch.add(obj3);
                        this.globalSearchMap.put(-obj3.id, obj3);
                    } else if (obj2 != null) {
                        this.localServerSearch.add(obj2);
                        this.globalSearchMap.put(obj2.id, obj2);
                    }
                }
            }
            this.lastFoundUsername = str.toLowerCase();
            ArrayList arrayList2 = this.localSearchResults;
            if (arrayList2 != null) {
                mergeResults(arrayList2);
            }
            mergeExcludeResults();
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$SearchAdapterHelper$DPsjy4ay_Xo08TMx7YPJqUY2IzQ(this));
        return false;
    }

    public /* synthetic */ void lambda$loadRecentHashtags$6$SearchAdapterHelper() {
        try {
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            ArrayList arrayList = new ArrayList();
            HashMap hashMap = new HashMap();
            while (queryFinalized.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = queryFinalized.stringValue(0);
                hashtagObject.date = queryFinalized.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            queryFinalized.dispose();
            Collections.sort(arrayList, -$$Lambda$SearchAdapterHelper$lNn-ZPLbXEKTf0Mjca-3cj25zrw.INSTANCE);
            AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$cRKQG3dF6eBcmtMnbev_zK5TKlg(this, arrayList, hashMap));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ int lambda$null$4(HashtagObject hashtagObject, HashtagObject hashtagObject2) {
        int i = hashtagObject.date;
        int i2 = hashtagObject2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void mergeResults(ArrayList<TLObject> arrayList) {
        this.localSearchResults = arrayList;
        if (this.globalSearchMap.size() != 0 && arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLObject tLObject = (TLObject) arrayList.get(i);
                if (tLObject instanceof User) {
                    User user = (User) tLObject;
                    User user2 = (User) this.globalSearchMap.get(user.id);
                    if (user2 != null) {
                        this.globalSearch.remove(user2);
                        this.localServerSearch.remove(user2);
                        this.globalSearchMap.remove(user2.id);
                    }
                    TLObject tLObject2 = (TLObject) this.groupSearchMap.get(user.id);
                    if (tLObject2 != null) {
                        this.groupSearch.remove(tLObject2);
                        this.groupSearchMap.remove(user.id);
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

    public void mergeExcludeResults() {
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate != null) {
            SparseArray excludeUsers = searchAdapterHelperDelegate.getExcludeUsers();
            if (excludeUsers != null) {
                int size = excludeUsers.size();
                for (int i = 0; i < size; i++) {
                    User user = (User) this.globalSearchMap.get(excludeUsers.keyAt(i));
                    if (user != null) {
                        this.globalSearch.remove(user);
                        this.localServerSearch.remove(user);
                        this.globalSearchMap.remove(user.id);
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
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@.]+").matcher(charSequence);
            Object obj = null;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(charSequence.charAt(start) == '@' || charSequence.charAt(start) == '#')) {
                    start++;
                }
                String charSequence2 = charSequence.subSequence(start, end).toString();
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
                obj = 1;
            }
            if (obj != null) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$SearchAdapterHelper$GIT1_XH69tqWW5ny1WrNuu7bX8Q(this, arrayList));
    }

    public /* synthetic */ void lambda$putRecentHashtags$7$SearchAdapterHelper(ArrayList arrayList) {
        try {
            int i;
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
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
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            if (arrayList.size() >= 100) {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
                while (i < arrayList.size()) {
                    SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("DELETE FROM hashtag_recent_v2 WHERE id = '");
                    stringBuilder.append(((HashtagObject) arrayList.get(i)).hashtag);
                    stringBuilder.append("'");
                    database.executeFast(stringBuilder.toString()).stepThis().dispose();
                    i++;
                }
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<TLObject> getLocalServerSearch() {
        return this.localServerSearch;
    }

    public ArrayList<TLObject> getGroupSearch() {
        return this.groupSearch;
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

    public void clearRecentHashtags() {
        this.hashtags = new ArrayList();
        this.hashtagsByText = new HashMap();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$SearchAdapterHelper$rJWBOKrijR0k23tFEyFN_1lhPTE(this));
    }

    public /* synthetic */ void lambda$clearRecentHashtags$8$SearchAdapterHelper() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: setHashtags */
    public void lambda$null$5$SearchAdapterHelper(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
