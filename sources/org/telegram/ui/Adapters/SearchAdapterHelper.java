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
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
        boolean canApplySearchResults(int i);

        LongSparseArray<TLRPC.TL_groupCallParticipant> getExcludeCallParticipants();

        LongSparseArray<TLRPC.User> getExcludeUsers();

        void onDataSetChanged(int i);

        void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap);

        /* renamed from: org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onSetHashtags(SearchAdapterHelperDelegate _this, ArrayList arrayList, HashMap hashMap) {
            }

            public static LongSparseArray $default$getExcludeUsers(SearchAdapterHelperDelegate _this) {
                return null;
            }

            public static LongSparseArray $default$getExcludeCallParticipants(SearchAdapterHelperDelegate _this) {
                return null;
            }

            public static boolean $default$canApplySearchResults(SearchAdapterHelperDelegate _this, int searchId) {
                return true;
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

    public SearchAdapterHelper(boolean allAsGlobal) {
        this.allResultsAreGlobal = allAsGlobal;
    }

    public void setAllowGlobalResults(boolean value) {
        this.allowGlobalResults = value;
    }

    public boolean isSearchInProgress() {
        return this.pendingRequestIds.size() > 0;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, boolean canAddGroupsOnly, long channelId, boolean phoneNumbers, int type, int searchId) {
        queryServerSearch(query, allowUsername, allowChats, allowBots, allowSelf, canAddGroupsOnly, channelId, phoneNumbers, type, searchId, (Runnable) null);
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, boolean canAddGroupsOnly, long channelId, boolean phoneNumbers, int type, int searchId, Runnable onEnd) {
        boolean hasChanged;
        int i;
        String str = query;
        long j = channelId;
        int i2 = type;
        int i3 = searchId;
        Iterator<Integer> it = this.pendingRequestIds.iterator();
        while (it.hasNext()) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(it.next().intValue(), true);
        }
        this.pendingRequestIds.clear();
        if (str == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.delegate.onDataSetChanged(i3);
            return;
        }
        ArrayList<Pair<TLObject, RequestDelegate>> requests = new ArrayList<>();
        if (query.length() > 0) {
            if (j != 0) {
                TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
                if (i2 == 1) {
                    req.filter = new TLRPC.TL_channelParticipantsAdmins();
                } else if (i2 == 3) {
                    req.filter = new TLRPC.TL_channelParticipantsBanned();
                } else if (i2 == 0) {
                    req.filter = new TLRPC.TL_channelParticipantsKicked();
                } else {
                    req.filter = new TLRPC.TL_channelParticipantsSearch();
                }
                req.filter.q = str;
                req.limit = 50;
                req.offset = 0;
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(j);
                requests.add(new Pair(req, new SearchAdapterHelper$$ExternalSyntheticLambda7(this, str, allowSelf)));
            } else {
                boolean z = allowSelf;
                this.lastFoundChannel = query.toLowerCase();
            }
            hasChanged = false;
        } else {
            boolean z2 = allowSelf;
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            hasChanged = true;
        }
        if (!allowUsername) {
            i = 3;
        } else if (query.length() > 0) {
            TLRPC.TL_contacts_search req2 = new TLRPC.TL_contacts_search();
            req2.q = str;
            req2.limit = 20;
            SearchAdapterHelper$$ExternalSyntheticLambda6 searchAdapterHelper$$ExternalSyntheticLambda6 = r0;
            i = 3;
            SearchAdapterHelper$$ExternalSyntheticLambda6 searchAdapterHelper$$ExternalSyntheticLambda62 = new SearchAdapterHelper$$ExternalSyntheticLambda6(this, searchId, allowChats, canAddGroupsOnly, allowBots, allowSelf, query);
            requests.add(new Pair(req2, searchAdapterHelper$$ExternalSyntheticLambda6));
        } else {
            i = 3;
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            hasChanged = false;
        }
        if (!canAddGroupsOnly && phoneNumbers && str.startsWith("+") && query.length() > i) {
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            String phone = PhoneFormat.stripExceptNumbers(query);
            ArrayList<TLRPC.TL_contact> arrayList = ContactsController.getInstance(this.currentAccount).contacts;
            boolean hasFullMatch = false;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList.get(a).user_id));
                if (!(user == null || user.phone == null || !user.phone.startsWith(phone))) {
                    if (!hasFullMatch) {
                        hasFullMatch = user.phone.length() == phone.length();
                    }
                    this.phonesSearch.add(user);
                    this.phoneSearchMap.put(user.id, user);
                }
            }
            if (!hasFullMatch) {
                this.phonesSearch.add("section");
                this.phonesSearch.add(phone);
            }
            hasChanged = false;
        }
        if (hasChanged) {
            this.delegate.onDataSetChanged(i3);
        }
        AtomicInteger gotResponses = new AtomicInteger(0);
        ArrayList<Pair<TLObject, TLRPC.TL_error>> responses = new ArrayList<>();
        int i4 = 0;
        while (i4 < requests.size()) {
            Pair<TLObject, RequestDelegate> r = requests.get(i4);
            responses.add((Object) null);
            AtomicInteger reqId = new AtomicInteger();
            ArrayList<Pair<TLObject, TLRPC.TL_error>> arrayList2 = responses;
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda8 = r0;
            ArrayList<Pair<TLObject, TLRPC.TL_error>> responses2 = responses;
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            Pair<TLObject, RequestDelegate> pair = r;
            AtomicInteger reqId2 = reqId;
            TLObject req3 = (TLObject) r.first;
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda82 = new SearchAdapterHelper$$ExternalSyntheticLambda8(this, arrayList2, i4, reqId, gotResponses, requests, searchId, onEnd);
            reqId2.set(instance.sendRequest(req3, searchAdapterHelper$$ExternalSyntheticLambda8));
            this.pendingRequestIds.add(Integer.valueOf(reqId2.get()));
            i4++;
            String str2 = query;
            int i5 = searchId;
            responses = responses2;
        }
    }

    /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1354xb4a01a4(String query, boolean allowSelf, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            this.lastFoundChannel = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(res.participants);
            long currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int N = res.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.ChannelParticipant participant = res.participants.get(a);
                long peerId = MessageObject.getPeerId(participant.peer);
                if (allowSelf || peerId != currentUserId) {
                    this.groupSearchMap.put(peerId, participant);
                } else {
                    this.groupSearch.remove(participant);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v21, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1355xc4CLASSNAMEvar_(int r18, boolean r19, boolean r20, boolean r21, boolean r22, java.lang.String r23, org.telegram.tgnet.TLObject r24, org.telegram.tgnet.TLRPC.TL_error r25) {
        /*
            r17 = this;
            r0 = r17
            org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r1 = r0.delegate
            r2 = r18
            boolean r1 = r1.canApplySearchResults(r2)
            if (r1 == 0) goto L_0x01c3
            if (r25 != 0) goto L_0x01c3
            r1 = r24
            org.telegram.tgnet.TLRPC$TL_contacts_found r1 = (org.telegram.tgnet.TLRPC.TL_contacts_found) r1
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r0.globalSearch
            r3.clear()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r3 = r0.globalSearchMap
            r3.clear()
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r0.localServerSearch
            r3.clear()
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r1.chats
            r5 = 0
            r3.putChats(r4, r5)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r1.users
            r3.putUsers(r4, r5)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r1.chats
            r6 = 1
            r3.putUsersAndChats(r4, r5, r6, r6)
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray
            r3.<init>()
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            r5 = 0
        L_0x0051:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r1.chats
            int r7 = r7.size()
            if (r5 >= r7) goto L_0x0069
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r7 = r1.chats
            java.lang.Object r7 = r7.get(r5)
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC.Chat) r7
            long r8 = r7.id
            r3.put(r8, r7)
            int r5 = r5 + 1
            goto L_0x0051
        L_0x0069:
            r5 = 0
        L_0x006a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r1.users
            int r7 = r7.size()
            if (r5 >= r7) goto L_0x0082
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r7 = r1.users
            java.lang.Object r7 = r7.get(r5)
            org.telegram.tgnet.TLRPC$User r7 = (org.telegram.tgnet.TLRPC.User) r7
            long r8 = r7.id
            r4.put(r8, r7)
            int r5 = r5 + 1
            goto L_0x006a
        L_0x0082:
            r5 = 0
        L_0x0083:
            r7 = 2
            r8 = 0
            if (r5 >= r7) goto L_0x013a
            if (r5 != 0) goto L_0x0093
            boolean r7 = r0.allResultsAreGlobal
            if (r7 != 0) goto L_0x0090
            goto L_0x0135
        L_0x0090:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r7 = r1.my_results
            goto L_0x0095
        L_0x0093:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r7 = r1.results
        L_0x0095:
            r10 = 0
        L_0x0096:
            int r11 = r7.size()
            if (r10 >= r11) goto L_0x0134
            java.lang.Object r11 = r7.get(r10)
            org.telegram.tgnet.TLRPC$Peer r11 = (org.telegram.tgnet.TLRPC.Peer) r11
            r12 = 0
            r13 = 0
            long r14 = r11.user_id
            int r16 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r16 == 0) goto L_0x00b4
            long r14 = r11.user_id
            java.lang.Object r14 = r4.get(r14)
            r12 = r14
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC.User) r12
            goto L_0x00d3
        L_0x00b4:
            long r14 = r11.chat_id
            int r16 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r16 == 0) goto L_0x00c4
            long r14 = r11.chat_id
            java.lang.Object r14 = r3.get(r14)
            r13 = r14
            org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC.Chat) r13
            goto L_0x00d3
        L_0x00c4:
            long r14 = r11.channel_id
            int r16 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r16 == 0) goto L_0x00d3
            long r14 = r11.channel_id
            java.lang.Object r14 = r3.get(r14)
            r13 = r14
            org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC.Chat) r13
        L_0x00d3:
            if (r13 == 0) goto L_0x00fc
            if (r19 == 0) goto L_0x00fa
            if (r20 == 0) goto L_0x00df
            boolean r14 = org.telegram.messenger.ChatObject.canAddBotsToChat(r13)
            if (r14 == 0) goto L_0x00fa
        L_0x00df:
            boolean r14 = r0.allowGlobalResults
            if (r14 != 0) goto L_0x00eb
            boolean r14 = org.telegram.messenger.ChatObject.isNotInChat(r13)
            if (r14 == 0) goto L_0x00eb
            r14 = r7
            goto L_0x012c
        L_0x00eb:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r14 = r0.globalSearch
            r14.add(r13)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r14 = r0.globalSearchMap
            long r8 = r13.id
            long r8 = -r8
            r14.put(r8, r13)
            r14 = r7
            goto L_0x012c
        L_0x00fa:
            r14 = r7
            goto L_0x012c
        L_0x00fc:
            if (r12 == 0) goto L_0x012b
            if (r20 != 0) goto L_0x0129
            if (r21 != 0) goto L_0x0109
            boolean r8 = r12.bot
            if (r8 != 0) goto L_0x0107
            goto L_0x0109
        L_0x0107:
            r14 = r7
            goto L_0x012c
        L_0x0109:
            if (r22 != 0) goto L_0x010f
            boolean r8 = r12.self
            if (r8 != 0) goto L_0x0107
        L_0x010f:
            boolean r8 = r0.allowGlobalResults
            if (r8 != 0) goto L_0x011b
            if (r5 != r6) goto L_0x011b
            boolean r8 = r12.contact
            if (r8 != 0) goto L_0x011b
            r14 = r7
            goto L_0x012c
        L_0x011b:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r0.globalSearch
            r8.add(r12)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r8 = r0.globalSearchMap
            r14 = r7
            long r6 = r12.id
            r8.put(r6, r12)
            goto L_0x012c
        L_0x0129:
            r14 = r7
            goto L_0x012c
        L_0x012b:
            r14 = r7
        L_0x012c:
            int r10 = r10 + 1
            r7 = r14
            r6 = 1
            r8 = 0
            goto L_0x0096
        L_0x0134:
            r14 = r7
        L_0x0135:
            int r5 = r5 + 1
            r6 = 1
            goto L_0x0083
        L_0x013a:
            boolean r5 = r0.allResultsAreGlobal
            if (r5 != 0) goto L_0x01bd
            r5 = 0
        L_0x013f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r1.my_results
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x01bd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r6 = r1.my_results
            java.lang.Object r6 = r6.get(r5)
            org.telegram.tgnet.TLRPC$Peer r6 = (org.telegram.tgnet.TLRPC.Peer) r6
            r7 = 0
            r8 = 0
            long r9 = r6.user_id
            r11 = 0
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0163
            long r9 = r6.user_id
            java.lang.Object r9 = r4.get(r9)
            r7 = r9
            org.telegram.tgnet.TLRPC$User r7 = (org.telegram.tgnet.TLRPC.User) r7
            goto L_0x0182
        L_0x0163:
            long r9 = r6.chat_id
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0173
            long r9 = r6.chat_id
            java.lang.Object r9 = r3.get(r9)
            r8 = r9
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            goto L_0x0182
        L_0x0173:
            long r9 = r6.channel_id
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0182
            long r9 = r6.channel_id
            java.lang.Object r9 = r3.get(r9)
            r8 = r9
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
        L_0x0182:
            if (r8 == 0) goto L_0x019d
            if (r19 == 0) goto L_0x01ba
            if (r20 == 0) goto L_0x018f
            boolean r9 = org.telegram.messenger.ChatObject.canAddBotsToChat(r8)
            if (r9 != 0) goto L_0x018f
            goto L_0x01ba
        L_0x018f:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.localServerSearch
            r9.add(r8)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r9 = r0.globalSearchMap
            long r13 = r8.id
            long r13 = -r13
            r9.put(r13, r8)
            goto L_0x01ba
        L_0x019d:
            if (r7 == 0) goto L_0x01ba
            if (r20 != 0) goto L_0x01ba
            if (r21 != 0) goto L_0x01a7
            boolean r9 = r7.bot
            if (r9 != 0) goto L_0x01ba
        L_0x01a7:
            if (r22 != 0) goto L_0x01ae
            boolean r9 = r7.self
            if (r9 == 0) goto L_0x01ae
            goto L_0x01ba
        L_0x01ae:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r9 = r0.localServerSearch
            r9.add(r7)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r9 = r0.globalSearchMap
            long r13 = r7.id
            r9.put(r13, r7)
        L_0x01ba:
            int r5 = r5 + 1
            goto L_0x013f
        L_0x01bd:
            java.lang.String r5 = r23.toLowerCase()
            r0.lastFoundUsername = r5
        L_0x01c3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapterHelper.m1355xc4CLASSNAMEvar_(int, boolean, boolean, boolean, boolean, java.lang.String, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* renamed from: lambda$queryServerSearch$3$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1357x37b0aa81(ArrayList responses, int index, AtomicInteger reqId, AtomicInteger gotResponses, ArrayList requests, int searchId, Runnable onEnd, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda3(this, responses, index, response, error, reqId, gotResponses, requests, searchId, onEnd));
    }

    /* renamed from: lambda$queryServerSearch$2$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1356x7e391ce2(ArrayList responses, int index, TLObject response, TLRPC.TL_error error, AtomicInteger reqId, AtomicInteger gotResponses, ArrayList requests, int searchId, Runnable onEnd) {
        responses.set(index, new Pair(response, error));
        Integer reqIdValue = Integer.valueOf(reqId.get());
        if (this.pendingRequestIds.contains(reqIdValue)) {
            this.pendingRequestIds.remove(reqIdValue);
            if (gotResponses.incrementAndGet() == requests.size()) {
                for (int j = 0; j < requests.size(); j++) {
                    RequestDelegate callback = (RequestDelegate) ((Pair) requests.get(j)).second;
                    Pair<TLObject, TLRPC.TL_error> res = (Pair) responses.get(j);
                    if (res != null) {
                        callback.run((TLObject) res.first, (TLRPC.TL_error) res.second);
                    }
                }
                removeGroupSearchFromGlobal();
                ArrayList<Object> arrayList = this.localSearchResults;
                if (arrayList != null) {
                    mergeResults(arrayList);
                }
                mergeExcludeResults();
                this.delegate.onDataSetChanged(searchId);
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        }
    }

    private void removeGroupSearchFromGlobal() {
        if (this.globalSearchMap.size() != 0) {
            int N = this.groupSearchMap.size();
            for (int a = 0; a < N; a++) {
                TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(this.groupSearchMap.keyAt(a));
                if (u != null) {
                    this.globalSearch.remove(u);
                    this.localServerSearch.remove(u);
                    this.globalSearchMap.remove(u.id);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda1(this));
        return false;
    }

    /* renamed from: lambda$loadRecentHashtags$6$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1352x65aedb6f() {
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            ArrayList<HashtagObject> arrayList = new ArrayList<>();
            HashMap<String, HashtagObject> hashMap = new HashMap<>();
            while (cursor.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = cursor.stringValue(0);
                hashtagObject.date = cursor.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            cursor.dispose();
            Collections.sort(arrayList, SearchAdapterHelper$$ExternalSyntheticLambda5.INSTANCE);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda4(this, arrayList, hashMap));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$loadRecentHashtags$4(HashtagObject lhs, HashtagObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void addGroupMembers(ArrayList<TLObject> participants) {
        this.groupSearch.clear();
        this.groupSearch.addAll(participants);
        int N = participants.size();
        for (int a = 0; a < N; a++) {
            TLObject object = participants.get(a);
            if (object instanceof TLRPC.ChatParticipant) {
                this.groupSearchMap.put(((TLRPC.ChatParticipant) object).user_id, object);
            } else if (object instanceof TLRPC.ChannelParticipant) {
                this.groupSearchMap.put(MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer), object);
            }
        }
        removeGroupSearchFromGlobal();
    }

    public void mergeResults(ArrayList<Object> localResults) {
        TLRPC.Chat c;
        this.localSearchResults = localResults;
        if (this.globalSearchMap.size() != 0 && localResults != null) {
            int count = localResults.size();
            for (int a = 0; a < count; a++) {
                Object obj = localResults.get(a);
                if (obj instanceof ShareAlert.DialogSearchResult) {
                    obj = ((ShareAlert.DialogSearchResult) obj).object;
                }
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(user.id);
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
                    }
                    TLObject participant = this.groupSearchMap.get(user.id);
                    if (participant != null) {
                        this.groupSearch.remove(participant);
                        this.groupSearchMap.remove(user.id);
                    }
                    Object object = this.phoneSearchMap.get(user.id);
                    if (object != null) {
                        this.phonesSearch.remove(object);
                        this.phoneSearchMap.remove(user.id);
                    }
                } else if ((obj instanceof TLRPC.Chat) && (c = (TLRPC.Chat) this.globalSearchMap.get(-((TLRPC.Chat) obj).id)) != null) {
                    this.globalSearch.remove(c);
                    this.localServerSearch.remove(c);
                    this.globalSearchMap.remove(-c.id);
                }
            }
        }
    }

    public void mergeExcludeResults() {
        SearchAdapterHelperDelegate searchAdapterHelperDelegate = this.delegate;
        if (searchAdapterHelperDelegate != null) {
            LongSparseArray<TLRPC.User> ignoreUsers = searchAdapterHelperDelegate.getExcludeUsers();
            if (ignoreUsers != null) {
                int size = ignoreUsers.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.User u = (TLRPC.User) this.globalSearchMap.get(ignoreUsers.keyAt(a));
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
                    }
                }
            }
            LongSparseArray<TLRPC.TL_groupCallParticipant> ignoreParticipants = this.delegate.getExcludeCallParticipants();
            if (ignoreParticipants != null) {
                int size2 = ignoreParticipants.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.User u2 = (TLRPC.User) this.globalSearchMap.get(ignoreParticipants.keyAt(a2));
                    if (u2 != null) {
                        this.globalSearch.remove(u2);
                        this.localServerSearch.remove(u2);
                        this.globalSearchMap.remove(u2.id);
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
            Matcher matcher = Pattern.compile("(^|\\s)#[^0-9][\\w@.]+").matcher(message);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(message.charAt(start) == '@' || message.charAt(start) == '#')) {
                    start++;
                }
                String hashtag = message.subSequence(start, end).toString();
                if (this.hashtagsByText == null) {
                    this.hashtagsByText = new HashMap<>();
                    this.hashtags = new ArrayList<>();
                }
                HashtagObject hashtagObject = this.hashtagsByText.get(hashtag);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda2(this, arrayList));
    }

    /* renamed from: lambda$putRecentHashtags$7$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1353x31dfee5f(ArrayList arrayList) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
            int a = 0;
            while (true) {
                if (a >= arrayList.size()) {
                    break;
                } else if (a == 100) {
                    break;
                } else {
                    HashtagObject hashtagObject = (HashtagObject) arrayList.get(a);
                    state.requery();
                    state.bindString(1, hashtagObject.hashtag);
                    state.bindInteger(2, hashtagObject.date);
                    state.step();
                    a++;
                }
            }
            state.dispose();
            if (arrayList.size() > 100) {
                SQLitePreparedStatement state2 = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = ?");
                for (int a2 = 100; a2 < arrayList.size(); a2++) {
                    state2.requery();
                    state2.bindString(1, ((HashtagObject) arrayList.get(a2)).hashtag);
                    state2.step();
                }
                state2.dispose();
            }
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void removeUserId(long userId) {
        Object object = this.globalSearchMap.get(userId);
        if (object != null) {
            this.globalSearch.remove(object);
        }
        Object object2 = this.groupSearchMap.get(userId);
        if (object2 != null) {
            this.groupSearch.remove(object2);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$clearRecentHashtags$8$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1350x6a670640() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: setHashtags */
    public void m1351xaCLASSNAMEdd0(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
