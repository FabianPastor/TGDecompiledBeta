package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
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
import org.telegram.tgnet.TLRPC.TL_contact;
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
    private SparseArray<TLObject> phoneSearchMap = new SparseArray();
    private ArrayList<Object> phonesSearch = new ArrayList();
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
            public static boolean $default$canApplySearchResults(SearchAdapterHelperDelegate searchAdapterHelperDelegate, int i) {
                return true;
            }

            public static SparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
                return null;
            }

            public static void $default$onSetHashtags(SearchAdapterHelperDelegate searchAdapterHelperDelegate, ArrayList arrayList, HashMap hashMap) {
            }
        }

        boolean canApplySearchResults(int i);

        SparseArray<User> getExcludeUsers();

        void onDataSetChanged(int i);

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    public SearchAdapterHelper(boolean z) {
        this.allResultsAreGlobal = z;
    }

    public boolean isSearchInProgress() {
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, int i, boolean z5, int i2, int i3) {
        String str2 = str;
        int i4 = i;
        int i5 = i2;
        int i6 = i3;
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
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged(i6);
            return;
        }
        int i7;
        ConnectionsManager instance;
        if (str.length() <= 0) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged(i6);
        } else if (i4 != 0) {
            TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
            if (i5 == 1) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
            } else if (i5 == 3) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsBanned();
            } else if (i5 == 0) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsKicked();
            } else {
                tL_channels_getParticipants.filter = new TL_channelParticipantsSearch();
            }
            tL_channels_getParticipants.filter.q = str2;
            tL_channels_getParticipants.limit = 50;
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i4);
            i7 = this.channelLastReqId + 1;
            this.channelLastReqId = i7;
            instance = ConnectionsManager.getInstance(this.currentAccount);
            -$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM -__lambda_searchadapterhelper_tvnoks_82nxnkubkel6ilcdljom = r0;
            -$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM -__lambda_searchadapterhelper_tvnoks_82nxnkubkel6ilcdljom2 = new -$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM(this, i7, str, z4, i3);
            this.channelReqId = instance.sendRequest(tL_channels_getParticipants, -__lambda_searchadapterhelper_tvnoks_82nxnkubkel6ilcdljom, 2);
        } else {
            this.lastFoundChannel = str.toLowerCase();
        }
        if (z) {
            if (str.length() > 0) {
                TL_contacts_search tL_contacts_search = new TL_contacts_search();
                tL_contacts_search.q = str2;
                tL_contacts_search.limit = 50;
                i7 = this.lastReqId + 1;
                this.lastReqId = i7;
                instance = ConnectionsManager.getInstance(this.currentAccount);
                -$$Lambda$SearchAdapterHelper$vMHpk9OJlbeR1DBViyt5uXKzrRA -__lambda_searchadapterhelper_vmhpk9ojlber1dbviyt5uxkzrra = r0;
                -$$Lambda$SearchAdapterHelper$vMHpk9OJlbeR1DBViyt5uXKzrRA -__lambda_searchadapterhelper_vmhpk9ojlber1dbviyt5uxkzrra2 = new -$$Lambda$SearchAdapterHelper$vMHpk9OJlbeR1DBViyt5uXKzrRA(this, i7, i3, z2, z3, z4, str);
                this.reqId = instance.sendRequest(tL_contacts_search, -__lambda_searchadapterhelper_vmhpk9ojlber1dbviyt5uxkzrra, 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged(i6);
            }
        }
        if (z5 && str2.startsWith("+") && str.length() > 3) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str);
            ArrayList arrayList = ContactsController.getInstance(this.currentAccount).contacts;
            i7 = arrayList.size();
            Object obj = null;
            for (int i8 = 0; i8 < i7; i8++) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(i8)).user_id));
                if (user != null) {
                    String str3 = user.phone;
                    if (str3 != null && str3.startsWith(stripExceptNumbers)) {
                        if (obj == null) {
                            obj = user.phone.length() == stripExceptNumbers.length() ? 1 : null;
                        }
                        this.phonesSearch.add(user);
                        this.phoneSearchMap.put(user.id, user);
                    }
                }
            }
            if (obj == null) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(stripExceptNumbers);
            }
            this.delegate.onDataSetChanged(i6);
        }
    }

    public /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int i, String str, boolean z, int i2, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$O0DUaWpMcq3Mf9AKa_6os4VKuNw(this, i, tL_error, tLObject, str, z, i2));
    }

    public /* synthetic */ void lambda$null$0$SearchAdapterHelper(int i, TL_error tL_error, TLObject tLObject, String str, boolean z, int i2) {
        if (i == this.channelLastReqId && tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            this.lastFoundChannel = str.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(tL_channels_channelParticipants.participants);
            i = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int size = tL_channels_channelParticipants.participants.size();
            for (int i3 = 0; i3 < size; i3++) {
                ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i3);
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
            this.delegate.onDataSetChanged(i2);
        }
        this.channelReqId = 0;
    }

    public /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int i, int i2, boolean z, boolean z2, boolean z3, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$0Mt8OZeXgQY12jqbJArDWgm6lmM(this, i, i2, tL_error, tLObject, z, z2, z3, str));
    }

    public /* synthetic */ void lambda$null$2$SearchAdapterHelper(int i, int i2, TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, String str) {
        int i3 = i2;
        if (i == this.lastReqId && this.delegate.canApplySearchResults(i2)) {
            int i4 = 0;
            this.reqId = 0;
            if (tL_error == null) {
                int i5;
                User user;
                TL_contacts_found tL_contacts_found = (TL_contacts_found) tLObject;
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_found.chats, false);
                MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_found.users, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_found.users, tL_contacts_found.chats, true, true);
                SparseArray sparseArray = new SparseArray();
                SparseArray sparseArray2 = new SparseArray();
                for (i5 = 0; i5 < tL_contacts_found.chats.size(); i5++) {
                    Chat chat = (Chat) tL_contacts_found.chats.get(i5);
                    sparseArray.put(chat.id, chat);
                }
                for (i5 = 0; i5 < tL_contacts_found.users.size(); i5++) {
                    user = (User) tL_contacts_found.users.get(i5);
                    sparseArray2.put(user.id, user);
                }
                for (i5 = 0; i5 < 2; i5++) {
                    ArrayList arrayList;
                    if (i5 != 0) {
                        arrayList = tL_contacts_found.results;
                    } else if (this.allResultsAreGlobal) {
                        arrayList = tL_contacts_found.my_results;
                    } else {
                    }
                    for (int i6 = 0; i6 < arrayList.size(); i6++) {
                        User user2;
                        Object obj;
                        Peer peer = (Peer) arrayList.get(i6);
                        int i7 = peer.user_id;
                        if (i7 != 0) {
                            user2 = (User) sparseArray2.get(i7);
                            obj = null;
                        } else {
                            i7 = peer.chat_id;
                            if (i7 != 0) {
                                obj = (Chat) sparseArray.get(i7);
                            } else {
                                int i8 = peer.channel_id;
                                if (i8 != 0) {
                                    Chat chat2 = (Chat) sparseArray.get(i8);
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
                    while (i4 < tL_contacts_found.my_results.size()) {
                        Object obj2;
                        Peer peer2 = (Peer) tL_contacts_found.my_results.get(i4);
                        int i9 = peer2.user_id;
                        if (i9 != 0) {
                            user = (User) sparseArray2.get(i9);
                            obj2 = null;
                        } else {
                            i9 = peer2.chat_id;
                            if (i9 != 0) {
                                obj2 = (Chat) sparseArray.get(i9);
                            } else {
                                i5 = peer2.channel_id;
                                if (i5 != 0) {
                                    Chat chat3 = (Chat) sparseArray.get(i5);
                                } else {
                                    obj2 = null;
                                    user = obj2;
                                }
                            }
                            user = null;
                        }
                        if (obj2 != null) {
                            if (z) {
                                this.localServerSearch.add(obj2);
                                this.globalSearchMap.put(-obj2.id, obj2);
                            }
                        } else if (user != null && ((z2 || !user.bot) && (z3 || !user.self))) {
                            this.localServerSearch.add(user);
                            this.globalSearchMap.put(user.id, user);
                        }
                        i4++;
                    }
                }
                this.lastFoundUsername = str.toLowerCase();
                ArrayList arrayList2 = this.localSearchResults;
                if (arrayList2 != null) {
                    mergeResults(arrayList2);
                }
                mergeExcludeResults();
                this.delegate.onDataSetChanged(i2);
            }
        }
    }

    public void clear() {
        this.globalSearch.clear();
        this.globalSearchMap.clear();
        this.localServerSearch.clear();
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
                    Object obj = this.phoneSearchMap.get(user.id);
                    if (obj != null) {
                        this.phonesSearch.remove(obj);
                        this.phoneSearchMap.remove(user.id);
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
            Matcher matcher = Pattern.compile("(^|\\s)#[^0-9][\\w@.]+").matcher(charSequence);
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

    public void removeUserId(int i) {
        Object obj = this.globalSearchMap.get(i);
        if (obj != null) {
            this.globalSearch.remove(obj);
        }
        Object obj2 = this.groupSearchMap.get(i);
        if (obj2 != null) {
            this.groupSearch.remove(obj2);
        }
    }

    public ArrayList<TLObject> getGlobalSearch() {
        return this.globalSearch;
    }

    public ArrayList<Object> getPhoneSearch() {
        return this.phonesSearch;
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
