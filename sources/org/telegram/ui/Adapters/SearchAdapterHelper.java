package org.telegram.ui.Adapters;

import android.util.Pair;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsSearch;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_contacts_found;
import org.telegram.tgnet.TLRPC$TL_contacts_search;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Components.ShareAlert;

public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private boolean allowGlobalResults = true;
    private int currentAccount = UserConfig.selectedAccount;
    private SearchAdapterHelperDelegate delegate;
    private ArrayList<TLObject> globalSearch = new ArrayList<>();
    private LongSparseArray<TLObject> globalSearchMap = new LongSparseArray<>();
    private ArrayList<TLObject> groupSearch = new ArrayList<>();
    private LongSparseArray<TLObject> groupSearchMap = new LongSparseArray<>();
    private ArrayList<HashtagObject> hashtags;
    private HashMap<String, HashtagObject> hashtagsByText;
    private boolean hashtagsLoadedFromDb = false;
    private String lastFoundChannel;
    private String lastFoundUsername = null;
    private ArrayList<Object> localSearchResults;
    private ArrayList<TLObject> localServerSearch = new ArrayList<>();
    private ArrayList<Integer> pendingRequestIds = new ArrayList<>();
    private LongSparseArray<TLObject> phoneSearchMap = new LongSparseArray<>();
    private ArrayList<Object> phonesSearch = new ArrayList<>();

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public interface SearchAdapterHelperDelegate {

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$canApplySearchResults(SearchAdapterHelperDelegate searchAdapterHelperDelegate, int i) {
                return true;
            }

            public static LongSparseArray $default$getExcludeCallParticipants(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
                return null;
            }

            public static LongSparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate searchAdapterHelperDelegate) {
                return null;
            }

            public static void $default$onSetHashtags(SearchAdapterHelperDelegate searchAdapterHelperDelegate, ArrayList arrayList, HashMap hashMap) {
            }
        }

        boolean canApplySearchResults(int i);

        LongSparseArray<TLRPC$TL_groupCallParticipant> getExcludeCallParticipants();

        LongSparseArray<TLRPC$User> getExcludeUsers();

        void onDataSetChanged(int i);

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);
    }

    public SearchAdapterHelper(boolean z) {
        this.allResultsAreGlobal = z;
    }

    public void setAllowGlobalResults(boolean z) {
        this.allowGlobalResults = z;
    }

    public boolean isSearchInProgress() {
        return this.pendingRequestIds.size() > 0;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, long j, boolean z6, int i, int i2) {
        queryServerSearch(str, z, z2, z3, z4, z5, j, z6, i, i2, (Runnable) null);
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, long j, boolean z6, int i, int i2, Runnable runnable) {
        boolean z7;
        String str2;
        String str3 = str;
        long j2 = j;
        int i3 = i;
        int i4 = i2;
        Iterator<Integer> it = this.pendingRequestIds.iterator();
        while (it.hasNext()) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(it.next().intValue(), true);
        }
        this.pendingRequestIds.clear();
        if (str3 == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.delegate.onDataSetChanged(i4);
            return;
        }
        ArrayList arrayList = new ArrayList();
        if (str.length() > 0) {
            if (j2 != 0) {
                TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
                if (i3 == 1) {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
                } else if (i3 == 3) {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsBanned();
                } else if (i3 == 0) {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsKicked();
                } else {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsSearch();
                }
                tLRPC$TL_channels_getParticipants.filter.q = str3;
                tLRPC$TL_channels_getParticipants.limit = 50;
                tLRPC$TL_channels_getParticipants.offset = 0;
                tLRPC$TL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(j2);
                arrayList.add(new Pair(tLRPC$TL_channels_getParticipants, new SearchAdapterHelper$$ExternalSyntheticLambda7(this, str3, z4)));
            } else {
                boolean z8 = z4;
                this.lastFoundChannel = str.toLowerCase();
            }
            z7 = false;
        } else {
            boolean z9 = z4;
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            z7 = true;
        }
        if (z) {
            if (str.length() > 0) {
                TLRPC$TL_contacts_search tLRPC$TL_contacts_search = new TLRPC$TL_contacts_search();
                tLRPC$TL_contacts_search.q = str3;
                tLRPC$TL_contacts_search.limit = 20;
                SearchAdapterHelper$$ExternalSyntheticLambda6 searchAdapterHelper$$ExternalSyntheticLambda6 = r0;
                SearchAdapterHelper$$ExternalSyntheticLambda6 searchAdapterHelper$$ExternalSyntheticLambda62 = new SearchAdapterHelper$$ExternalSyntheticLambda6(this, i2, z2, z5, z3, z4, str);
                arrayList.add(new Pair(tLRPC$TL_contacts_search, searchAdapterHelper$$ExternalSyntheticLambda6));
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                z7 = false;
            }
        }
        if (!z5 && z6 && str3.startsWith("+") && str.length() > 3) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str);
            ArrayList<TLRPC$TL_contact> arrayList2 = ContactsController.getInstance(this.currentAccount).contacts;
            int size = arrayList2.size();
            boolean z10 = false;
            for (int i5 = 0; i5 < size; i5++) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList2.get(i5).user_id));
                if (!(user == null || (str2 = user.phone) == null || !str2.startsWith(stripExceptNumbers))) {
                    if (!z10) {
                        z10 = user.phone.length() == stripExceptNumbers.length();
                    }
                    this.phonesSearch.add(user);
                    this.phoneSearchMap.put(user.id, user);
                }
            }
            if (!z10) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(stripExceptNumbers);
            }
            z7 = false;
        }
        if (z7) {
            this.delegate.onDataSetChanged(i4);
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ArrayList arrayList3 = new ArrayList();
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            arrayList3.add((Object) null);
            AtomicInteger atomicInteger2 = new AtomicInteger();
            atomicInteger2.set(ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject) ((Pair) arrayList.get(i6)).first, new SearchAdapterHelper$$ExternalSyntheticLambda8(this, arrayList3, i6, atomicInteger2, atomicInteger, arrayList, i2, runnable)));
            this.pendingRequestIds.add(Integer.valueOf(atomicInteger2.get()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$0(String str, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            this.lastFoundChannel = str.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_channels_channelParticipants.chats, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(tLRPC$TL_channels_channelParticipants.participants);
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int size = tLRPC$TL_channels_channelParticipants.participants.size();
            for (int i = 0; i < size; i++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i);
                long peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
                if (z || peerId != clientUserId) {
                    this.groupSearchMap.put(peerId, tLRPC$ChannelParticipant);
                } else {
                    this.groupSearch.remove(tLRPC$ChannelParticipant);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$1(int i, boolean z, boolean z2, boolean z3, boolean z4, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLRPC$User tLRPC$User;
        TLRPC$Chat tLRPC$Chat;
        ArrayList<TLRPC$Peer> arrayList;
        TLRPC$User tLRPC$User2;
        TLRPC$Chat tLRPC$Chat2;
        if (this.delegate.canApplySearchResults(i) && tLRPC$TL_error == null) {
            TLRPC$TL_contacts_found tLRPC$TL_contacts_found = (TLRPC$TL_contacts_found) tLObject;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_contacts_found.chats, false);
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_contacts_found.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_found.users, tLRPC$TL_contacts_found.chats, true, true);
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i2 = 0; i2 < tLRPC$TL_contacts_found.chats.size(); i2++) {
                TLRPC$Chat tLRPC$Chat3 = tLRPC$TL_contacts_found.chats.get(i2);
                longSparseArray.put(tLRPC$Chat3.id, tLRPC$Chat3);
            }
            for (int i3 = 0; i3 < tLRPC$TL_contacts_found.users.size(); i3++) {
                TLRPC$User tLRPC$User3 = tLRPC$TL_contacts_found.users.get(i3);
                longSparseArray2.put(tLRPC$User3.id, tLRPC$User3);
            }
            for (int i4 = 0; i4 < 2; i4++) {
                if (i4 != 0) {
                    arrayList = tLRPC$TL_contacts_found.results;
                } else if (!this.allResultsAreGlobal) {
                } else {
                    arrayList = tLRPC$TL_contacts_found.my_results;
                }
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    TLRPC$Peer tLRPC$Peer = arrayList.get(i5);
                    long j = tLRPC$Peer.user_id;
                    if (j != 0) {
                        tLRPC$User2 = (TLRPC$User) longSparseArray2.get(j);
                        tLRPC$Chat2 = null;
                    } else {
                        long j2 = tLRPC$Peer.chat_id;
                        if (j2 != 0) {
                            tLRPC$Chat2 = (TLRPC$Chat) longSparseArray.get(j2);
                        } else {
                            long j3 = tLRPC$Peer.channel_id;
                            if (j3 != 0) {
                                tLRPC$Chat2 = (TLRPC$Chat) longSparseArray.get(j3);
                            } else {
                                tLRPC$Chat2 = null;
                                tLRPC$User2 = null;
                            }
                        }
                        tLRPC$User2 = null;
                    }
                    if (tLRPC$Chat2 != null) {
                        if (z && ((!z2 || ChatObject.canAddBotsToChat(tLRPC$Chat2)) && (this.allowGlobalResults || !ChatObject.isNotInChat(tLRPC$Chat2)))) {
                            this.globalSearch.add(tLRPC$Chat2);
                            this.globalSearchMap.put(-tLRPC$Chat2.id, tLRPC$Chat2);
                        }
                    } else if (tLRPC$User2 != null && !z2 && ((z3 || !tLRPC$User2.bot) && ((z4 || !tLRPC$User2.self) && (this.allowGlobalResults || i4 != 1 || tLRPC$User2.contact)))) {
                        this.globalSearch.add(tLRPC$User2);
                        this.globalSearchMap.put(tLRPC$User2.id, tLRPC$User2);
                    }
                }
            }
            if (!this.allResultsAreGlobal) {
                for (int i6 = 0; i6 < tLRPC$TL_contacts_found.my_results.size(); i6++) {
                    TLRPC$Peer tLRPC$Peer2 = tLRPC$TL_contacts_found.my_results.get(i6);
                    long j4 = tLRPC$Peer2.user_id;
                    if (j4 != 0) {
                        tLRPC$User = (TLRPC$User) longSparseArray2.get(j4);
                        tLRPC$Chat = null;
                    } else {
                        long j5 = tLRPC$Peer2.chat_id;
                        if (j5 != 0) {
                            tLRPC$Chat = (TLRPC$Chat) longSparseArray.get(j5);
                        } else {
                            long j6 = tLRPC$Peer2.channel_id;
                            if (j6 != 0) {
                                tLRPC$Chat = (TLRPC$Chat) longSparseArray.get(j6);
                            } else {
                                tLRPC$Chat = null;
                                tLRPC$User = null;
                            }
                        }
                        tLRPC$User = null;
                    }
                    if (tLRPC$Chat != null) {
                        if (z && (!z2 || ChatObject.canAddBotsToChat(tLRPC$Chat))) {
                            this.localServerSearch.add(tLRPC$Chat);
                            this.globalSearchMap.put(-tLRPC$Chat.id, tLRPC$Chat);
                        }
                    } else if (tLRPC$User != null && !z2 && ((z3 || !tLRPC$User.bot) && (z4 || !tLRPC$User.self))) {
                        this.localServerSearch.add(tLRPC$User);
                        this.globalSearchMap.put(tLRPC$User.id, tLRPC$User);
                    }
                }
            }
            this.lastFoundUsername = str.toLowerCase();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$3(ArrayList arrayList, int i, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, ArrayList arrayList2, int i2, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda3(this, arrayList, i, tLObject, tLRPC$TL_error, atomicInteger, atomicInteger2, arrayList2, i2, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$2(ArrayList arrayList, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, ArrayList arrayList2, int i2, Runnable runnable) {
        arrayList.set(i, new Pair(tLObject, tLRPC$TL_error));
        Integer valueOf = Integer.valueOf(atomicInteger.get());
        if (this.pendingRequestIds.contains(valueOf)) {
            this.pendingRequestIds.remove(valueOf);
            if (atomicInteger2.incrementAndGet() == arrayList2.size()) {
                for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                    RequestDelegate requestDelegate = (RequestDelegate) ((Pair) arrayList2.get(i3)).second;
                    Pair pair = (Pair) arrayList.get(i3);
                    if (pair != null) {
                        requestDelegate.run((TLObject) pair.first, (TLRPC$TL_error) pair.second);
                    }
                }
                removeGroupSearchFromGlobal();
                ArrayList<Object> arrayList3 = this.localSearchResults;
                if (arrayList3 != null) {
                    mergeResults(arrayList3);
                }
                mergeExcludeResults();
                this.delegate.onDataSetChanged(i2);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    private void removeGroupSearchFromGlobal() {
        if (this.globalSearchMap.size() != 0) {
            int size = this.groupSearchMap.size();
            for (int i = 0; i < size; i++) {
                TLRPC$User tLRPC$User = (TLRPC$User) this.globalSearchMap.get(this.groupSearchMap.keyAt(i));
                if (tLRPC$User != null) {
                    this.globalSearch.remove(tLRPC$User);
                    this.localServerSearch.remove(tLRPC$User);
                    this.globalSearchMap.remove(tLRPC$User.id);
                }
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda0(this));
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecentHashtags$6() {
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
            Collections.sort(arrayList, SearchAdapterHelper$$ExternalSyntheticLambda5.INSTANCE);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda4(this, arrayList, hashMap));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadRecentHashtags$4(HashtagObject hashtagObject, HashtagObject hashtagObject2) {
        int i = hashtagObject.date;
        int i2 = hashtagObject2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void addGroupMembers(ArrayList<TLObject> arrayList) {
        this.groupSearch.clear();
        this.groupSearch.addAll(arrayList);
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLObject tLObject = arrayList.get(i);
            if (tLObject instanceof TLRPC$ChatParticipant) {
                this.groupSearchMap.put(((TLRPC$ChatParticipant) tLObject).user_id, tLObject);
            } else if (tLObject instanceof TLRPC$ChannelParticipant) {
                this.groupSearchMap.put(MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer), tLObject);
            }
        }
        removeGroupSearchFromGlobal();
    }

    public void mergeResults(ArrayList<Object> arrayList) {
        TLRPC$Chat tLRPC$Chat;
        this.localSearchResults = arrayList;
        if (this.globalSearchMap.size() != 0 && arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Object obj = arrayList.get(i);
                if (obj instanceof ShareAlert.DialogSearchResult) {
                    obj = ((ShareAlert.DialogSearchResult) obj).object;
                }
                if (obj instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) obj;
                    TLRPC$User tLRPC$User2 = (TLRPC$User) this.globalSearchMap.get(tLRPC$User.id);
                    if (tLRPC$User2 != null) {
                        this.globalSearch.remove(tLRPC$User2);
                        this.localServerSearch.remove(tLRPC$User2);
                        this.globalSearchMap.remove(tLRPC$User2.id);
                    }
                    TLObject tLObject = this.groupSearchMap.get(tLRPC$User.id);
                    if (tLObject != null) {
                        this.groupSearch.remove(tLObject);
                        this.groupSearchMap.remove(tLRPC$User.id);
                    }
                    TLObject tLObject2 = this.phoneSearchMap.get(tLRPC$User.id);
                    if (tLObject2 != null) {
                        this.phonesSearch.remove(tLObject2);
                        this.phoneSearchMap.remove(tLRPC$User.id);
                    }
                } else if ((obj instanceof TLRPC$Chat) && (tLRPC$Chat = (TLRPC$Chat) this.globalSearchMap.get(-((TLRPC$Chat) obj).id)) != null) {
                    this.globalSearch.remove(tLRPC$Chat);
                    this.localServerSearch.remove(tLRPC$Chat);
                    this.globalSearchMap.remove(-tLRPC$Chat.id);
                }
            }
        }
    }

    public void mergeExcludeResults() {
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate != null) {
            LongSparseArray<TLRPC$User> excludeUsers = searchAdapterHelperDelegate.getExcludeUsers();
            if (excludeUsers != null) {
                int size = excludeUsers.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$User tLRPC$User = (TLRPC$User) this.globalSearchMap.get(excludeUsers.keyAt(i));
                    if (tLRPC$User != null) {
                        this.globalSearch.remove(tLRPC$User);
                        this.localServerSearch.remove(tLRPC$User);
                        this.globalSearchMap.remove(tLRPC$User.id);
                    }
                }
            }
            LongSparseArray<TLRPC$TL_groupCallParticipant> excludeCallParticipants = this.delegate.getExcludeCallParticipants();
            if (excludeCallParticipants != null) {
                int size2 = excludeCallParticipants.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC$User tLRPC$User2 = (TLRPC$User) this.globalSearchMap.get(excludeCallParticipants.keyAt(i2));
                    if (tLRPC$User2 != null) {
                        this.globalSearch.remove(tLRPC$User2);
                        this.localServerSearch.remove(tLRPC$User2);
                        this.globalSearchMap.remove(tLRPC$User2.id);
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
                    this.hashtagsByText.put(charSequence2, hashtagObject);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda2(this, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putRecentHashtags$7(ArrayList arrayList) {
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
            if (arrayList.size() > 100) {
                SQLitePreparedStatement executeFast2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = ?");
                for (i = 100; i < arrayList.size(); i++) {
                    executeFast2.requery();
                    executeFast2.bindString(1, ((HashtagObject) arrayList.get(i)).hashtag);
                    executeFast2.step();
                }
                executeFast2.dispose();
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void removeUserId(long j) {
        TLObject tLObject = this.globalSearchMap.get(j);
        if (tLObject != null) {
            this.globalSearch.remove(tLObject);
        }
        TLObject tLObject2 = this.groupSearchMap.get(j);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentHashtags$8() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: setHashtags */
    public void lambda$loadRecentHashtags$5(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
