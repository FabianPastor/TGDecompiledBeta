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
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsSearch;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_contacts_search;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
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
        return (this.reqId == 0 && this.channelReqId == 0) ? false : true;
    }

    public void queryServerSearch(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, long j, boolean z6, int i, int i2) {
        String str2;
        String str3 = str;
        long j2 = j;
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
            this.delegate.onDataSetChanged(i4);
            return;
        }
        if (str.length() <= 0) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged(i4);
        } else if (j2 != 0) {
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
            int i5 = this.channelLastReqId + 1;
            this.channelLastReqId = i5;
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda8 = r0;
            SearchAdapterHelper$$ExternalSyntheticLambda8 searchAdapterHelper$$ExternalSyntheticLambda82 = new SearchAdapterHelper$$ExternalSyntheticLambda8(this, i5, str, z4, i2);
            this.channelReqId = instance.sendRequest(tLRPC$TL_channels_getParticipants, searchAdapterHelper$$ExternalSyntheticLambda8, 2);
        } else {
            this.lastFoundChannel = str.toLowerCase();
        }
        if (z) {
            if (str.length() > 0) {
                TLRPC$TL_contacts_search tLRPC$TL_contacts_search = new TLRPC$TL_contacts_search();
                tLRPC$TL_contacts_search.q = str3;
                tLRPC$TL_contacts_search.limit = 50;
                int i6 = this.lastReqId + 1;
                this.lastReqId = i6;
                SearchAdapterHelper$$ExternalSyntheticLambda7 searchAdapterHelper$$ExternalSyntheticLambda7 = r0;
                ConnectionsManager instance2 = ConnectionsManager.getInstance(this.currentAccount);
                SearchAdapterHelper$$ExternalSyntheticLambda7 searchAdapterHelper$$ExternalSyntheticLambda72 = new SearchAdapterHelper$$ExternalSyntheticLambda7(this, i6, i2, z2, z5, z3, z4, str);
                this.reqId = instance2.sendRequest(tLRPC$TL_contacts_search, searchAdapterHelper$$ExternalSyntheticLambda7, 2);
            } else {
                this.globalSearch.clear();
                this.globalSearchMap.clear();
                this.localServerSearch.clear();
                this.lastReqId = 0;
                this.delegate.onDataSetChanged(i4);
                if (!z5 && z6 && str3.startsWith("+") && str.length() > 3) {
                    this.phonesSearch.clear();
                    this.phoneSearchMap.clear();
                    String stripExceptNumbers = PhoneFormat.stripExceptNumbers(str);
                    ArrayList<TLRPC$TL_contact> arrayList = ContactsController.getInstance(this.currentAccount).contacts;
                    int size = arrayList.size();
                    boolean z7 = false;
                    for (int i7 = 0; i7 < size; i7++) {
                        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList.get(i7).user_id));
                        if (!(user == null || (str2 = user.phone) == null || !str2.startsWith(stripExceptNumbers))) {
                            if (!z7) {
                                z7 = user.phone.length() == stripExceptNumbers.length();
                            }
                            this.phonesSearch.add(user);
                            this.phoneSearchMap.put(user.id, user);
                        }
                    }
                    if (!z7) {
                        this.phonesSearch.add("section");
                        this.phonesSearch.add(stripExceptNumbers);
                    }
                    this.delegate.onDataSetChanged(i4);
                    return;
                }
                return;
            }
        }
        if (!z5) {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$1(int i, String str, boolean z, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda3(this, i, tLRPC$TL_error, tLObject, str, z, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$0(int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, boolean z, int i2) {
        if (i == this.channelLastReqId) {
            this.channelReqId = 0;
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
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i3);
                    long peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
                    if (z || peerId != clientUserId) {
                        this.groupSearchMap.put(peerId, tLRPC$ChannelParticipant);
                    } else {
                        this.groupSearch.remove(tLRPC$ChannelParticipant);
                    }
                }
                removeGroupSearchFromGlobal();
                ArrayList<Object> arrayList = this.localSearchResults;
                if (arrayList != null) {
                    mergeResults(arrayList);
                }
                this.delegate.onDataSetChanged(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$queryServerSearch$3(int i, int i2, boolean z, boolean z2, boolean z3, boolean z4, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda2(this, i, i2, tLRPC$TL_error, tLObject, z, z2, z3, z4, str));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$queryServerSearch$2(int r18, int r19, org.telegram.tgnet.TLRPC$TL_error r20, org.telegram.tgnet.TLObject r21, boolean r22, boolean r23, boolean r24, boolean r25, java.lang.String r26) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            int r3 = r0.lastReqId
            r4 = 0
            if (r1 != r3) goto L_0x000d
            r0.reqId = r4
        L_0x000d:
            if (r1 != r3) goto L_0x01e1
            org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r1 = r0.delegate
            boolean r1 = r1.canApplySearchResults(r2)
            if (r1 == 0) goto L_0x01e1
            if (r20 != 0) goto L_0x01e1
            r1 = r21
            org.telegram.tgnet.TLRPC$TL_contacts_found r1 = (org.telegram.tgnet.TLRPC$TL_contacts_found) r1
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r0.globalSearch
            r3.clear()
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r3 = r0.globalSearchMap
            r3.clear()
            java.util.ArrayList<org.telegram.tgnet.TLObject> r3 = r0.localServerSearch
            r3.clear()
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r1.chats
            r3.putChats(r5, r4)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r1.users
            r3.putUsers(r5, r4)
            int r3 = r0.currentAccount
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r1.chats
            r7 = 1
            r3.putUsersAndChats(r5, r6, r7, r7)
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray
            r3.<init>()
            androidx.collection.LongSparseArray r5 = new androidx.collection.LongSparseArray
            r5.<init>()
            r6 = 0
        L_0x005b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r1.chats
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x0073
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r8 = r1.chats
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$Chat r8 = (org.telegram.tgnet.TLRPC$Chat) r8
            long r9 = r8.id
            r3.put(r9, r8)
            int r6 = r6 + 1
            goto L_0x005b
        L_0x0073:
            r6 = 0
        L_0x0074:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r1.users
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x008c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r1.users
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$User r8 = (org.telegram.tgnet.TLRPC$User) r8
            long r9 = r8.id
            r5.put(r9, r8)
            int r6 = r6 + 1
            goto L_0x0074
        L_0x008c:
            r6 = 0
        L_0x008d:
            r8 = 2
            r9 = 0
            r10 = 0
            if (r6 >= r8) goto L_0x0145
            if (r6 != 0) goto L_0x00a0
            boolean r8 = r0.allResultsAreGlobal
            if (r8 != 0) goto L_0x009d
        L_0x0099:
            r18 = r5
            goto L_0x013d
        L_0x009d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r8 = r1.my_results
            goto L_0x00a2
        L_0x00a0:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r8 = r1.results
        L_0x00a2:
            r12 = 0
        L_0x00a3:
            int r13 = r8.size()
            if (r12 >= r13) goto L_0x0099
            java.lang.Object r13 = r8.get(r12)
            org.telegram.tgnet.TLRPC$Peer r13 = (org.telegram.tgnet.TLRPC$Peer) r13
            long r14 = r13.user_id
            int r16 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r16 == 0) goto L_0x00be
            java.lang.Object r13 = r5.get(r14)
            org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC$User) r13
            r14 = r13
            r13 = r9
            goto L_0x00db
        L_0x00be:
            long r14 = r13.chat_id
            int r16 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r16 == 0) goto L_0x00cc
            java.lang.Object r13 = r3.get(r14)
            org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC$Chat) r13
        L_0x00ca:
            r14 = r9
            goto L_0x00db
        L_0x00cc:
            long r13 = r13.channel_id
            int r15 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r15 == 0) goto L_0x00d9
            java.lang.Object r13 = r3.get(r13)
            org.telegram.tgnet.TLRPC$Chat r13 = (org.telegram.tgnet.TLRPC$Chat) r13
            goto L_0x00ca
        L_0x00d9:
            r13 = r9
            r14 = r13
        L_0x00db:
            if (r13 == 0) goto L_0x0105
            if (r22 == 0) goto L_0x0102
            if (r23 == 0) goto L_0x00e7
            boolean r14 = org.telegram.messenger.ChatObject.canAddBotsToChat(r13)
            if (r14 == 0) goto L_0x0102
        L_0x00e7:
            boolean r14 = r0.allowGlobalResults
            if (r14 != 0) goto L_0x00f2
            boolean r14 = org.telegram.messenger.ChatObject.isNotInChat(r13)
            if (r14 == 0) goto L_0x00f2
            goto L_0x0102
        L_0x00f2:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r14 = r0.globalSearch
            r14.add(r13)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r14 = r0.globalSearchMap
            r18 = r5
            long r4 = r13.id
            long r4 = -r4
            r14.put(r4, r13)
            goto L_0x0131
        L_0x0102:
            r18 = r5
            goto L_0x0131
        L_0x0105:
            r18 = r5
            if (r14 == 0) goto L_0x0131
            if (r23 != 0) goto L_0x0131
            if (r24 != 0) goto L_0x0111
            boolean r4 = r14.bot
            if (r4 != 0) goto L_0x0131
        L_0x0111:
            if (r25 != 0) goto L_0x0117
            boolean r4 = r14.self
            if (r4 != 0) goto L_0x0131
        L_0x0117:
            boolean r4 = r0.allowGlobalResults
            if (r4 != 0) goto L_0x0122
            if (r6 != r7) goto L_0x0122
            boolean r4 = r14.contact
            if (r4 != 0) goto L_0x0122
            goto L_0x0131
        L_0x0122:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r4 = r0.globalSearch
            r4.add(r14)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r4 = r0.globalSearchMap
            r21 = r8
            long r7 = r14.id
            r4.put(r7, r14)
            goto L_0x0133
        L_0x0131:
            r21 = r8
        L_0x0133:
            int r12 = r12 + 1
            r5 = r18
            r8 = r21
            r4 = 0
            r7 = 1
            goto L_0x00a3
        L_0x013d:
            int r6 = r6 + 1
            r5 = r18
            r4 = 0
            r7 = 1
            goto L_0x008d
        L_0x0145:
            r18 = r5
            boolean r4 = r0.allResultsAreGlobal
            if (r4 != 0) goto L_0x01c9
            r4 = 0
        L_0x014c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r1.my_results
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x01c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r5 = r1.my_results
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$Peer r5 = (org.telegram.tgnet.TLRPC$Peer) r5
            long r6 = r5.user_id
            int r8 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x016d
            r8 = r18
            java.lang.Object r5 = r8.get(r6)
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC$User) r5
            r6 = r5
            r5 = r9
            goto L_0x018c
        L_0x016d:
            r8 = r18
            long r6 = r5.chat_id
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x017d
            java.lang.Object r5 = r3.get(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
        L_0x017b:
            r6 = r9
            goto L_0x018c
        L_0x017d:
            long r5 = r5.channel_id
            int r7 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x018a
            java.lang.Object r5 = r3.get(r5)
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC$Chat) r5
            goto L_0x017b
        L_0x018a:
            r5 = r9
            r6 = r5
        L_0x018c:
            if (r5 == 0) goto L_0x01a7
            if (r22 == 0) goto L_0x01c4
            if (r23 == 0) goto L_0x0199
            boolean r6 = org.telegram.messenger.ChatObject.canAddBotsToChat(r5)
            if (r6 != 0) goto L_0x0199
            goto L_0x01c4
        L_0x0199:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r6 = r0.localServerSearch
            r6.add(r5)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r6 = r0.globalSearchMap
            long r12 = r5.id
            long r12 = -r12
            r6.put(r12, r5)
            goto L_0x01c4
        L_0x01a7:
            if (r6 == 0) goto L_0x01c4
            if (r23 != 0) goto L_0x01c4
            if (r24 != 0) goto L_0x01b1
            boolean r5 = r6.bot
            if (r5 != 0) goto L_0x01c4
        L_0x01b1:
            if (r25 != 0) goto L_0x01b8
            boolean r5 = r6.self
            if (r5 == 0) goto L_0x01b8
            goto L_0x01c4
        L_0x01b8:
            java.util.ArrayList<org.telegram.tgnet.TLObject> r5 = r0.localServerSearch
            r5.add(r6)
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLObject> r5 = r0.globalSearchMap
            long r12 = r6.id
            r5.put(r12, r6)
        L_0x01c4:
            int r4 = r4 + 1
            r18 = r8
            goto L_0x014c
        L_0x01c9:
            r17.removeGroupSearchFromGlobal()
            java.lang.String r1 = r26.toLowerCase()
            r0.lastFoundUsername = r1
            java.util.ArrayList<java.lang.Object> r1 = r0.localSearchResults
            if (r1 == 0) goto L_0x01d9
            r0.mergeResults(r1)
        L_0x01d9:
            r17.mergeExcludeResults()
            org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate r1 = r0.delegate
            r1.onDataSetChanged(r2)
        L_0x01e1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapterHelper.lambda$queryServerSearch$2(int, int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, boolean, boolean, boolean, boolean, java.lang.String):void");
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
            Collections.sort(arrayList, SearchAdapterHelper$$ExternalSyntheticLambda6.INSTANCE);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$ExternalSyntheticLambda5(this, arrayList, hashMap));
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$ExternalSyntheticLambda4(this, arrayList));
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
