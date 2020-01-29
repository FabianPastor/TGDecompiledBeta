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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private int channelLastReqId;
    private int channelReqId = 0;
    private int currentAccount = UserConfig.selectedAccount;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList<>();
    private SparseArray<TLObject> globalSearchMap = new SparseArray<>();
    private ArrayList<TLObject> groupSearch = new ArrayList<>();
    private SparseArray<TLObject> groupSearchMap = new SparseArray<>();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundChannel;
    private String lastFoundUsername = null;
    private int lastReqId;
    private ArrayList<TLObject> localSearchResults;
    private ArrayList<TLObject> localServerSearch = new ArrayList<>();
    private SparseArray<TLObject> phoneSearchMap = new SparseArray<>();
    private ArrayList<Object> phonesSearch = new ArrayList<>();
    private int reqId = 0;

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public interface SearchAdapterHelperDelegate {

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static SparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
                return null;
            }

            public static void $default$onSetHashtags(SearchAdapterHelperDelegate searchAdapterHelperDelegate, ArrayList arrayList, HashMap hashMap) {
            }
        }

        SparseArray<TLRPC.User> getExcludeUsers();

        void onDataSetChanged();

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    protected static final class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        protected DialogSearchResult() {
        }
    }

    public SearchAdapterHelper(boolean z) {
        this.allResultsAreGlobal = z;
    }

    public boolean isSearchInProgress() {
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, int i, boolean z5, int i2) {
        String str2;
        String str3 = str;
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
        if (str3 == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
            return;
        }
        if (str.length() <= 0) {
            boolean z6 = z4;
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
        } else if (i3 != 0) {
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
            if (i4 == 1) {
                tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
            } else if (i4 == 3) {
                tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsBanned();
            } else if (i4 == 0) {
                tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsKicked();
            } else {
                tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsSearch();
            }
            tL_channels_getParticipants.filter.q = str3;
            tL_channels_getParticipants.limit = 50;
            tL_channels_getParticipants.offset = 0;
            tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(i3);
            int i5 = this.channelLastReqId + 1;
            this.channelLastReqId = i5;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate(i5, str3, z4) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SearchAdapterHelper.this.lambda$queryServerSearch$1$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            }, 2);
        } else {
            boolean z7 = z4;
            this.lastFoundChannel = str.toLowerCase();
        }
        if (z) {
            if (str.length() > 0) {
                TLRPC.TL_contacts_search tL_contacts_search = new TLRPC.TL_contacts_search();
                tL_contacts_search.q = str3;
                tL_contacts_search.limit = 50;
                int i6 = this.lastReqId + 1;
                this.lastReqId = i6;
                this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_search, new RequestDelegate(i6, z2, z3, z4, str) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ boolean f$4;
                    private final /* synthetic */ String f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SearchAdapterHelper.this.lambda$queryServerSearch$3$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
                    }
                }, 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged();
            }
        }
        if (z5 && str3.startsWith("+") && str.length() > 3) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str);
            ArrayList<TLRPC.TL_contact> arrayList = ContactsController.getInstance(this.currentAccount).contacts;
            int size = arrayList.size();
            boolean z8 = false;
            for (int i7 = 0; i7 < size; i7++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arrayList.get(i7).user_id));
                if (!(user == null || (str2 = user.phone) == null || !str2.startsWith(stripExceptNumbers))) {
                    if (!z8) {
                        z8 = user.phone.length() == stripExceptNumbers.length();
                    }
                    this.phonesSearch.add(user);
                    this.phoneSearchMap.put(user.id, user);
                }
            }
            if (!z8) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(stripExceptNumbers);
            }
            this.delegate.onDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int i, String str, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, tL_error, tLObject, str, z) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ String f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SearchAdapterHelper.this.lambda$null$0$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$SearchAdapterHelper(int i, TLRPC.TL_error tL_error, TLObject tLObject, String str, boolean z) {
        if (i == this.channelLastReqId && tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            this.lastFoundChannel = str.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(tL_channels_channelParticipants.participants);
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int size = tL_channels_channelParticipants.participants.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC.ChannelParticipant channelParticipant = tL_channels_channelParticipants.participants.get(i2);
                if (z || channelParticipant.user_id != clientUserId) {
                    this.groupSearchMap.put(channelParticipant.user_id, channelParticipant);
                } else {
                    this.groupSearch.remove(channelParticipant);
                }
            }
            ArrayList<TLObject> arrayList = this.localSearchResults;
            if (arrayList != null) {
                mergeResults(arrayList);
            }
            this.delegate.onDataSetChanged();
        }
        this.channelReqId = 0;
    }

    public /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int i, boolean z, boolean z2, boolean z3, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, tL_error, tLObject, z, z2, z3, str) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ String f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                SearchAdapterHelper.this.lambda$null$2$SearchAdapterHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$SearchAdapterHelper(int i, TLRPC.TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, String str) {
        TLRPC.User user;
        TLRPC.Chat chat;
        ArrayList<TLRPC.Peer> arrayList;
        TLRPC.User user2;
        TLRPC.Chat chat2;
        if (i == this.lastReqId) {
            this.reqId = 0;
            if (tL_error == null) {
                TLRPC.TL_contacts_found tL_contacts_found = (TLRPC.TL_contacts_found) tLObject;
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_found.chats, false);
                MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_found.users, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_contacts_found.users, tL_contacts_found.chats, true, true);
                SparseArray sparseArray = new SparseArray();
                SparseArray sparseArray2 = new SparseArray();
                for (int i2 = 0; i2 < tL_contacts_found.chats.size(); i2++) {
                    TLRPC.Chat chat3 = tL_contacts_found.chats.get(i2);
                    sparseArray.put(chat3.id, chat3);
                }
                for (int i3 = 0; i3 < tL_contacts_found.users.size(); i3++) {
                    TLRPC.User user3 = tL_contacts_found.users.get(i3);
                    sparseArray2.put(user3.id, user3);
                }
                for (int i4 = 0; i4 < 2; i4++) {
                    if (i4 != 0) {
                        arrayList = tL_contacts_found.results;
                    } else if (!this.allResultsAreGlobal) {
                    } else {
                        arrayList = tL_contacts_found.my_results;
                    }
                    for (int i5 = 0; i5 < arrayList.size(); i5++) {
                        TLRPC.Peer peer = arrayList.get(i5);
                        int i6 = peer.user_id;
                        if (i6 != 0) {
                            user2 = (TLRPC.User) sparseArray2.get(i6);
                            chat2 = null;
                        } else {
                            int i7 = peer.chat_id;
                            if (i7 != 0) {
                                chat2 = (TLRPC.Chat) sparseArray.get(i7);
                            } else {
                                int i8 = peer.channel_id;
                                if (i8 != 0) {
                                    chat2 = (TLRPC.Chat) sparseArray.get(i8);
                                } else {
                                    chat2 = null;
                                    user2 = null;
                                }
                            }
                            user2 = null;
                        }
                        if (chat2 != null) {
                            if (z) {
                                this.globalSearch.add(chat2);
                                this.globalSearchMap.put(-chat2.id, chat2);
                            }
                        } else if (user2 != null && ((z2 || !user2.bot) && (z3 || !user2.self))) {
                            this.globalSearch.add(user2);
                            this.globalSearchMap.put(user2.id, user2);
                        }
                    }
                }
                if (!this.allResultsAreGlobal) {
                    for (int i9 = 0; i9 < tL_contacts_found.my_results.size(); i9++) {
                        TLRPC.Peer peer2 = tL_contacts_found.my_results.get(i9);
                        int i10 = peer2.user_id;
                        if (i10 != 0) {
                            user = (TLRPC.User) sparseArray2.get(i10);
                            chat = null;
                        } else {
                            int i11 = peer2.chat_id;
                            if (i11 != 0) {
                                chat = (TLRPC.Chat) sparseArray.get(i11);
                            } else {
                                int i12 = peer2.channel_id;
                                if (i12 != 0) {
                                    chat = (TLRPC.Chat) sparseArray.get(i12);
                                } else {
                                    chat = null;
                                    user = null;
                                }
                            }
                            user = null;
                        }
                        if (chat != null) {
                            if (z) {
                                this.localServerSearch.add(chat);
                                this.globalSearchMap.put(-chat.id, chat);
                            }
                        } else if (user != null && ((z2 || !user.bot) && (z3 || !user.self))) {
                            this.localServerSearch.add(user);
                            this.globalSearchMap.put(user.id, user);
                        }
                    }
                }
                this.lastFoundUsername = str.toLowerCase();
                ArrayList<TLObject> arrayList2 = this.localSearchResults;
                if (arrayList2 != null) {
                    mergeResults(arrayList2);
                }
                mergeExcludeResults();
                this.delegate.onDataSetChanged();
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                SearchAdapterHelper.this.lambda$loadRecentHashtags$6$SearchAdapterHelper();
            }
        });
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
            Collections.sort(arrayList, $$Lambda$SearchAdapterHelper$lNnZPLbXEKTf0Mjca3cj25zrw.INSTANCE);
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, hashMap) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ HashMap f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SearchAdapterHelper.this.lambda$null$5$SearchAdapterHelper(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
        TLRPC.Chat chat;
        this.localSearchResults = arrayList;
        if (this.globalSearchMap.size() != 0 && arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLObject tLObject = arrayList.get(i);
                if (tLObject instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) tLObject;
                    TLRPC.User user2 = (TLRPC.User) this.globalSearchMap.get(user.id);
                    if (user2 != null) {
                        this.globalSearch.remove(user2);
                        this.localServerSearch.remove(user2);
                        this.globalSearchMap.remove(user2.id);
                    }
                    TLObject tLObject2 = this.groupSearchMap.get(user.id);
                    if (tLObject2 != null) {
                        this.groupSearch.remove(tLObject2);
                        this.groupSearchMap.remove(user.id);
                    }
                    TLObject tLObject3 = this.phoneSearchMap.get(user.id);
                    if (tLObject3 != null) {
                        this.phonesSearch.remove(tLObject3);
                        this.phoneSearchMap.remove(user.id);
                    }
                } else if ((tLObject instanceof TLRPC.Chat) && (chat = (TLRPC.Chat) this.globalSearchMap.get(-((TLRPC.Chat) tLObject).id)) != null) {
                    this.globalSearch.remove(chat);
                    this.localServerSearch.remove(chat);
                    this.globalSearchMap.remove(-chat.id);
                }
            }
        }
    }

    public void mergeExcludeResults() {
        SparseArray<TLRPC.User> excludeUsers;
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate != null && (excludeUsers = searchAdapterHelperDelegate.getExcludeUsers()) != null) {
            int size = excludeUsers.size();
            for (int i = 0; i < size; i++) {
                TLRPC.User user = (TLRPC.User) this.globalSearchMap.get(excludeUsers.keyAt(i));
                if (user != null) {
                    this.globalSearch.remove(user);
                    this.localServerSearch.remove(user);
                    this.globalSearchMap.remove(user.id);
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
            boolean z = false;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(charSequence.charAt(start) == '@' || charSequence.charAt(start) == '#')) {
                    start++;
                }
                String charSequence2 = charSequence.subSequence(start, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap<>();
                    this.hashtags = new ArrayList<>();
                }
                HashtagObject hashtagObject = this.hashtagsByText.get(charSequence2);
                if (hashtagObject == null) {
                    hashtagObject = new HashtagObject();
                    hashtagObject.hashtag = charSequence2;
                    this.hashtagsByText.put(hashtagObject.hashtag, hashtagObject);
                } else {
                    this.hashtags.remove(hashtagObject);
                }
                hashtagObject.date = (int) (System.currentTimeMillis() / 1000);
                this.hashtags.add(0, hashtagObject);
                z = true;
            }
            if (z) {
                putRecentHashtags(this.hashtags);
            }
        }
    }

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SearchAdapterHelper.this.lambda$putRecentHashtags$7$SearchAdapterHelper(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$putRecentHashtags$7$SearchAdapterHelper(ArrayList arrayList) {
        int i;
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
            int i2 = 0;
            while (true) {
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
                for (i = 100; i < arrayList.size(); i++) {
                    SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
                    database.executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(i)).hashtag + "'").stepThis().dispose();
                }
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void removeUserId(int i) {
        TLObject tLObject = this.globalSearchMap.get(i);
        if (tLObject != null) {
            this.globalSearch.remove(tLObject);
        }
        TLObject tLObject2 = this.groupSearchMap.get(i);
        if (tLObject2 != null) {
            this.groupSearch.remove(tLObject2);
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
        this.hashtags = new ArrayList<>();
        this.hashtagsByText = new HashMap<>();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                SearchAdapterHelper.this.lambda$clearRecentHashtags$8$SearchAdapterHelper();
            }
        });
    }

    public /* synthetic */ void lambda$clearRecentHashtags$8$SearchAdapterHelper() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
