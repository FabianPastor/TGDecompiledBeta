package org.telegram.ui.Adapters;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ShareAlert;

public class SearchAdapterHelper {
    private boolean allResultsAreGlobal;
    private boolean allowGlobalResults = true;
    private int channelLastReqId;
    private int channelReqId = 0;
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
    private int lastReqId;
    private ArrayList<Object> localSearchResults;
    private ArrayList<TLObject> localServerSearch = new ArrayList<>();
    private LongSparseArray<TLObject> phoneSearchMap = new LongSparseArray<>();
    private ArrayList<Object> phonesSearch = new ArrayList<>();
    private int reqId = 0;

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
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, boolean canAddGroupsOnly, long channelId, boolean phoneNumbers, int type, int searchId) {
        String str = query;
        long j = channelId;
        int i = type;
        int i2 = searchId;
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (str == null) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.globalSearch.clear();
            this.globalSearchMap.clear();
            this.localServerSearch.clear();
            this.phonesSearch.clear();
            this.phoneSearchMap.clear();
            this.lastReqId = 0;
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged(i2);
            return;
        }
        if (query.length() <= 0) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged(i2);
        } else if (j != 0) {
            TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
            if (i == 1) {
                req.filter = new TLRPC.TL_channelParticipantsAdmins();
            } else if (i == 3) {
                req.filter = new TLRPC.TL_channelParticipantsBanned();
            } else if (i == 0) {
                req.filter = new TLRPC.TL_channelParticipantsKicked();
            } else {
                req.filter = new TLRPC.TL_channelParticipantsSearch();
            }
            req.filter.q = str;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(j);
            int currentReqId = this.channelLastReqId + 1;
            this.channelLastReqId = currentReqId;
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda8 = r0;
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda82 = new SearchAdapterHelper$$ExternalSyntheticLambda8(this, currentReqId, query, allowSelf, searchId);
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, searchAdapterHelper$$ExternalSyntheticLambda8, 2);
        } else {
            this.lastFoundChannel = query.toLowerCase();
        }
        if (allowUsername) {
            if (query.length() > 0) {
                TLRPC.TL_contacts_search req2 = new TLRPC.TL_contacts_search();
                req2.q = str;
                req2.limit = 50;
                int currentReqId2 = this.lastReqId + 1;
                this.lastReqId = currentReqId2;
                SearchAdapterHelper$$ExternalSyntheticLambda7 searchAdapterHelper$$ExternalSyntheticLambda7 = r0;
                ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
                SearchAdapterHelper$$ExternalSyntheticLambda7 searchAdapterHelper$$ExternalSyntheticLambda72 = new SearchAdapterHelper$$ExternalSyntheticLambda7(this, currentReqId2, searchId, allowChats, canAddGroupsOnly, allowBots, allowSelf, query);
                this.reqId = instance.sendRequest(req2, searchAdapterHelper$$ExternalSyntheticLambda7, 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged(i2);
            }
        }
        if (!canAddGroupsOnly && phoneNumbers && str.startsWith("+") && query.length() > 3) {
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
            this.delegate.onDataSetChanged(i2);
        }
    }

    /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1395xc4CLASSNAMEvar_(int currentReqId, String query, boolean allowSelf, int searchId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda3(this, currentReqId, error, response, query, allowSelf, searchId));
    }

    /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1394xb4a01a4(int currentReqId, TLRPC.TL_error error, TLObject response, String query, boolean allowSelf, int searchId) {
        if (currentReqId == this.channelLastReqId) {
            this.channelReqId = 0;
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
                removeGroupSearchFromGlobal();
                ArrayList<Object> arrayList = this.localSearchResults;
                if (arrayList != null) {
                    mergeResults(arrayList);
                }
                this.delegate.onDataSetChanged(searchId);
            }
        }
    }

    /* renamed from: lambda$queryServerSearch$3$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1397x37b0aa81(int currentReqId, int searchId, boolean allowChats, boolean canAddGroupsOnly, boolean allowBots, boolean allowSelf, String query, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda2(this, currentReqId, searchId, error, response, allowChats, canAddGroupsOnly, allowBots, allowSelf, query));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$queryServerSearch$2$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1396x7e391ce2(int r19, int r20, org.telegram.tgnet.TLRPC.TL_error r21, org.telegram.tgnet.TLObject r22, boolean r23, boolean r24, boolean r25, boolean r26, java.lang.String r27) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            int r3 = r0.lastReqId
            r4 = 0
            if (r1 != r3) goto L_0x000d
            r0.reqId = r4
        L_0x000d:
            if (r1 != r3) goto L_0x01e1
            org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r3 = r0.delegate
            boolean r3 = r3.canApplySearchResults(r2)
            if (r3 == 0) goto L_0x01e1
            if (r21 != 0) goto L_0x01e1
            r3 = r22
            org.telegram.tgnet.TLRPC$TL_contacts_found r3 = (org.telegram.tgnet.TLRPC.TL_contacts_found) r3
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r0.globalSearch
            r5.clear()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r5 = r0.globalSearchMap
            r5.clear()
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r0.localServerSearch
            r5.clear()
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r3.chats
            r5.putChats(r6, r4)
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r6 = r3.users
            r5.putUsers(r6, r4)
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r3.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r3.chats
            r7 = 1
            r4.putUsersAndChats(r5, r6, r7, r7)
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            androidx.collection.LongSparseArray r5 = new androidx.collection.LongSparseArray
            r5.<init>()
            r6 = 0
        L_0x005b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r3.chats
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x0073
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r3.chats
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC.Chat) r8
            long r9 = r8.id
            r4.put(r9, r8)
            int r6 = r6 + 1
            goto L_0x005b
        L_0x0073:
            r6 = 0
        L_0x0074:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r3.users
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x008c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r3.users
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            long r9 = r8.id
            r5.put(r9, r8)
            int r6 = r6 + 1
            goto L_0x0074
        L_0x008c:
            r6 = 0
        L_0x008d:
            r8 = 2
            r9 = 0
            if (r6 >= r8) goto L_0x0146
            if (r6 != 0) goto L_0x009d
            boolean r8 = r0.allResultsAreGlobal
            if (r8 != 0) goto L_0x009a
            goto L_0x0142
        L_0x009a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r8 = r3.my_results
            goto L_0x009f
        L_0x009d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r8 = r3.results
        L_0x009f:
            r11 = 0
        L_0x00a0:
            int r12 = r8.size()
            if (r11 >= r12) goto L_0x0140
            java.lang.Object r12 = r8.get(r11)
            org.telegram.tgnet.TLRPC$Peer r12 = (org.telegram.tgnet.TLRPC.Peer) r12
            r13 = 0
            r14 = 0
            r16 = r8
            long r7 = r12.user_id
            int r17 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r17 == 0) goto L_0x00c0
            long r7 = r12.user_id
            java.lang.Object r7 = r5.get(r7)
            r13 = r7
            org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC.User) r13
            goto L_0x00df
        L_0x00c0:
            long r7 = r12.chat_id
            int r17 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r17 == 0) goto L_0x00d0
            long r7 = r12.chat_id
            java.lang.Object r7 = r4.get(r7)
            r14 = r7
            org.telegram.tgnet.TLRPC$Chat r14 = (org.telegram.tgnet.TLRPC.Chat) r14
            goto L_0x00df
        L_0x00d0:
            long r7 = r12.channel_id
            int r17 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r17 == 0) goto L_0x00df
            long r7 = r12.channel_id
            java.lang.Object r7 = r4.get(r7)
            r14 = r7
            org.telegram.tgnet.TLRPC$Chat r14 = (org.telegram.tgnet.TLRPC.Chat) r14
        L_0x00df:
            if (r14 == 0) goto L_0x0108
            if (r23 == 0) goto L_0x0106
            if (r24 == 0) goto L_0x00eb
            boolean r7 = org.telegram.messenger.ChatObject.canAddBotsToChat(r14)
            if (r7 == 0) goto L_0x0106
        L_0x00eb:
            boolean r7 = r0.allowGlobalResults
            if (r7 != 0) goto L_0x00f7
            boolean r7 = org.telegram.messenger.ChatObject.isNotInChat(r14)
            if (r7 == 0) goto L_0x00f7
            r7 = 1
            goto L_0x0138
        L_0x00f7:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r7 = r0.globalSearch
            r7.add(r14)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r7 = r0.globalSearchMap
            long r9 = r14.id
            long r8 = -r9
            r7.put(r8, r14)
            r7 = 1
            goto L_0x0138
        L_0x0106:
            r7 = 1
            goto L_0x0138
        L_0x0108:
            if (r13 == 0) goto L_0x0137
            if (r24 != 0) goto L_0x0135
            if (r25 != 0) goto L_0x0115
            boolean r7 = r13.bot
            if (r7 != 0) goto L_0x0113
            goto L_0x0115
        L_0x0113:
            r7 = 1
            goto L_0x0138
        L_0x0115:
            if (r26 != 0) goto L_0x011b
            boolean r7 = r13.self
            if (r7 != 0) goto L_0x0113
        L_0x011b:
            boolean r7 = r0.allowGlobalResults
            if (r7 != 0) goto L_0x0127
            r7 = 1
            if (r6 != r7) goto L_0x0128
            boolean r8 = r13.contact
            if (r8 != 0) goto L_0x0128
            goto L_0x0138
        L_0x0127:
            r7 = 1
        L_0x0128:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r8 = r0.globalSearch
            r8.add(r13)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r8 = r0.globalSearchMap
            long r9 = r13.id
            r8.put(r9, r13)
            goto L_0x0138
        L_0x0135:
            r7 = 1
            goto L_0x0138
        L_0x0137:
            r7 = 1
        L_0x0138:
            int r11 = r11 + 1
            r8 = r16
            r9 = 0
            goto L_0x00a0
        L_0x0140:
            r16 = r8
        L_0x0142:
            int r6 = r6 + 1
            goto L_0x008d
        L_0x0146:
            boolean r6 = r0.allResultsAreGlobal
            if (r6 != 0) goto L_0x01c9
            r6 = 0
        L_0x014b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r7 = r3.my_results
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x01c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r7 = r3.my_results
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$Peer r7 = (org.telegram.tgnet.TLRPC.Peer) r7
            r8 = 0
            r9 = 0
            long r10 = r7.user_id
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x016f
            long r10 = r7.user_id
            java.lang.Object r10 = r5.get(r10)
            r8 = r10
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC.User) r8
            goto L_0x018e
        L_0x016f:
            long r10 = r7.chat_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x017f
            long r10 = r7.chat_id
            java.lang.Object r10 = r4.get(r10)
            r9 = r10
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC.Chat) r9
            goto L_0x018e
        L_0x017f:
            long r10 = r7.channel_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x018e
            long r10 = r7.channel_id
            java.lang.Object r10 = r4.get(r10)
            r9 = r10
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC.Chat) r9
        L_0x018e:
            if (r9 == 0) goto L_0x01a9
            if (r23 == 0) goto L_0x01c6
            if (r24 == 0) goto L_0x019b
            boolean r10 = org.telegram.messenger.ChatObject.canAddBotsToChat(r9)
            if (r10 != 0) goto L_0x019b
            goto L_0x01c6
        L_0x019b:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r10 = r0.localServerSearch
            r10.add(r9)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r10 = r0.globalSearchMap
            long r14 = r9.id
            long r14 = -r14
            r10.put(r14, r9)
            goto L_0x01c6
        L_0x01a9:
            if (r8 == 0) goto L_0x01c6
            if (r24 != 0) goto L_0x01c6
            if (r25 != 0) goto L_0x01b3
            boolean r10 = r8.bot
            if (r10 != 0) goto L_0x01c6
        L_0x01b3:
            if (r26 != 0) goto L_0x01ba
            boolean r10 = r8.self
            if (r10 == 0) goto L_0x01ba
            goto L_0x01c6
        L_0x01ba:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r10 = r0.localServerSearch
            r10.add(r8)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r10 = r0.globalSearchMap
            long r14 = r8.id
            r10.put(r14, r8)
        L_0x01c6:
            int r6 = r6 + 1
            goto L_0x014b
        L_0x01c9:
            r18.removeGroupSearchFromGlobal()
            java.lang.String r6 = r27.toLowerCase()
            r0.lastFoundUsername = r6
            java.util.ArrayList<java.lang.Object> r6 = r0.localSearchResults
            if (r6 == 0) goto L_0x01d9
            r0.mergeResults(r6)
        L_0x01d9:
            r18.mergeExcludeResults()
            org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r6 = r0.delegate
            r6.onDataSetChanged(r2)
        L_0x01e1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapterHelper.m1396x7e391ce2(int, int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, boolean, boolean, boolean, boolean, java.lang.String):void");
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
    public /* synthetic */ void m1392x65aedb6f() {
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
            Collections.sort(arrayList, SearchAdapterHelper$$ExternalSyntheticLambda6.INSTANCE);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda5(this, arrayList, hashMap));
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda4(this, arrayList));
    }

    /* renamed from: lambda$putRecentHashtags$7$org-telegram-ui-Adapters-SearchAdapterHelper  reason: not valid java name */
    public /* synthetic */ void m1393x31dfee5f(ArrayList arrayList) {
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
    public /* synthetic */ void m1390x6a670640() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: setHashtags */
    public void m1391xaCLASSNAMEdd0(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        this.hashtags = arrayList;
        this.hashtagsByText = hashMap;
        this.hashtagsLoadedFromDb = true;
        this.delegate.onSetHashtags(arrayList, hashMap);
    }
}
