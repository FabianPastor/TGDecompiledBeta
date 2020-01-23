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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_contacts_found;
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

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0165  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01aa  */
    public void queryServerSearch(java.lang.String r17, boolean r18, boolean r19, boolean r20, boolean r21, boolean r22, int r23, boolean r24, int r25, int r26) {
        /*
        r16 = this;
        r9 = r16;
        r10 = r17;
        r0 = r23;
        r1 = r25;
        r11 = r26;
        r2 = r9.reqId;
        r12 = 1;
        r13 = 0;
        if (r2 == 0) goto L_0x001d;
    L_0x0010:
        r2 = r9.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = r9.reqId;
        r2.cancelRequest(r3, r12);
        r9.reqId = r13;
    L_0x001d:
        r2 = r9.channelReqId;
        if (r2 == 0) goto L_0x002e;
    L_0x0021:
        r2 = r9.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = r9.channelReqId;
        r2.cancelRequest(r3, r12);
        r9.channelReqId = r13;
    L_0x002e:
        if (r10 != 0) goto L_0x005d;
    L_0x0030:
        r0 = r9.groupSearch;
        r0.clear();
        r0 = r9.groupSearchMap;
        r0.clear();
        r0 = r9.globalSearch;
        r0.clear();
        r0 = r9.globalSearchMap;
        r0.clear();
        r0 = r9.localServerSearch;
        r0.clear();
        r0 = r9.phonesSearch;
        r0.clear();
        r0 = r9.phoneSearchMap;
        r0.clear();
        r9.lastReqId = r13;
        r9.channelLastReqId = r13;
        r0 = r9.delegate;
        r0.onDataSetChanged(r11);
        return;
    L_0x005d:
        r2 = r17.length();
        r14 = 2;
        r6 = 50;
        r15 = 3;
        if (r2 <= 0) goto L_0x00d0;
    L_0x0067:
        if (r0 == 0) goto L_0x00c9;
    L_0x0069:
        r7 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
        r7.<init>();
        if (r1 != r12) goto L_0x0078;
    L_0x0070:
        r1 = new org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
        r1.<init>();
        r7.filter = r1;
        goto L_0x0093;
    L_0x0078:
        if (r1 != r15) goto L_0x0082;
    L_0x007a:
        r1 = new org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
        r1.<init>();
        r7.filter = r1;
        goto L_0x0093;
    L_0x0082:
        if (r1 != 0) goto L_0x008c;
    L_0x0084:
        r1 = new org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
        r1.<init>();
        r7.filter = r1;
        goto L_0x0093;
    L_0x008c:
        r1 = new org.telegram.tgnet.TLRPC$TL_channelParticipantsSearch;
        r1.<init>();
        r7.filter = r1;
    L_0x0093:
        r1 = r7.filter;
        r1.q = r10;
        r7.limit = r6;
        r7.offset = r13;
        r1 = r9.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r0 = r1.getInputChannel(r0);
        r7.channel = r0;
        r0 = r9.channelLastReqId;
        r2 = r0 + 1;
        r9.channelLastReqId = r2;
        r0 = r9.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r5 = new org.telegram.ui.Adapters.-$$Lambda$SearchAdapterHelper$TvNOKs_82NxNKUbKEl6ilcDLjOM;
        r0 = r5;
        r1 = r16;
        r3 = r17;
        r4 = r21;
        r15 = r5;
        r5 = r26;
        r0.<init>(r1, r2, r3, r4, r5);
        r0 = r8.sendRequest(r7, r15, r14);
        r9.channelReqId = r0;
        goto L_0x00e1;
    L_0x00c9:
        r0 = r17.toLowerCase();
        r9.lastFoundChannel = r0;
        goto L_0x00e1;
    L_0x00d0:
        r0 = r9.groupSearch;
        r0.clear();
        r0 = r9.groupSearchMap;
        r0.clear();
        r9.channelLastReqId = r13;
        r0 = r9.delegate;
        r0.onDataSetChanged(r11);
    L_0x00e1:
        if (r18 == 0) goto L_0x0133;
    L_0x00e3:
        r0 = r17.length();
        if (r0 <= 0) goto L_0x011b;
    L_0x00e9:
        r15 = new org.telegram.tgnet.TLRPC$TL_contacts_search;
        r15.<init>();
        r15.q = r10;
        r15.limit = r6;
        r0 = r9.lastReqId;
        r2 = r0 + 1;
        r9.lastReqId = r2;
        r0 = r9.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r7 = new org.telegram.ui.Adapters.-$$Lambda$SearchAdapterHelper$Pz3Yk9hHMq0c2qA_8xmmwKDyhTA;
        r0 = r7;
        r1 = r16;
        r3 = r26;
        r4 = r19;
        r5 = r22;
        r6 = r20;
        r12 = r7;
        r7 = r21;
        r13 = r8;
        r8 = r17;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8);
        r0 = r13.sendRequest(r15, r12, r14);
        r9.reqId = r0;
        goto L_0x0133;
    L_0x011b:
        r0 = r9.globalSearch;
        r0.clear();
        r0 = r9.globalSearchMap;
        r0.clear();
        r0 = r9.localServerSearch;
        r0.clear();
        r0 = 0;
        r9.lastReqId = r0;
        r1 = r9.delegate;
        r1.onDataSetChanged(r11);
        goto L_0x0134;
    L_0x0133:
        r0 = 0;
    L_0x0134:
        if (r22 != 0) goto L_0x01bb;
    L_0x0136:
        if (r24 == 0) goto L_0x01bb;
    L_0x0138:
        r1 = "+";
        r1 = r10.startsWith(r1);
        if (r1 == 0) goto L_0x01bb;
    L_0x0140:
        r1 = r17.length();
        r2 = 3;
        if (r1 <= r2) goto L_0x01bb;
    L_0x0147:
        r1 = r9.phonesSearch;
        r1.clear();
        r1 = r9.phoneSearchMap;
        r1.clear();
        r1 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r17);
        r2 = r9.currentAccount;
        r2 = org.telegram.messenger.ContactsController.getInstance(r2);
        r2 = r2.contacts;
        r3 = r2.size();
        r4 = 0;
        r5 = 0;
    L_0x0163:
        if (r4 >= r3) goto L_0x01a8;
    L_0x0165:
        r6 = r2.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.TL_contact) r6;
        r7 = r9.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r6 = r6.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r7.getUser(r6);
        if (r6 != 0) goto L_0x017e;
    L_0x017d:
        goto L_0x01a5;
    L_0x017e:
        r7 = r6.phone;
        if (r7 == 0) goto L_0x01a5;
    L_0x0182:
        r7 = r7.startsWith(r1);
        if (r7 == 0) goto L_0x01a5;
    L_0x0188:
        if (r5 != 0) goto L_0x0199;
    L_0x018a:
        r5 = r6.phone;
        r5 = r5.length();
        r7 = r1.length();
        if (r5 != r7) goto L_0x0198;
    L_0x0196:
        r5 = 1;
        goto L_0x0199;
    L_0x0198:
        r5 = 0;
    L_0x0199:
        r7 = r9.phonesSearch;
        r7.add(r6);
        r7 = r9.phoneSearchMap;
        r8 = r6.id;
        r7.put(r8, r6);
    L_0x01a5:
        r4 = r4 + 1;
        goto L_0x0163;
    L_0x01a8:
        if (r5 != 0) goto L_0x01b6;
    L_0x01aa:
        r0 = r9.phonesSearch;
        r2 = "section";
        r0.add(r2);
        r0 = r9.phonesSearch;
        r0.add(r1);
    L_0x01b6:
        r0 = r9.delegate;
        r0.onDataSetChanged(r11);
    L_0x01bb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapterHelper.queryServerSearch(java.lang.String, boolean, boolean, boolean, boolean, boolean, int, boolean, int, int):void");
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

    public /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int i, int i2, boolean z, boolean z2, boolean z3, boolean z4, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapterHelper$kNL0ud9mPGvAWOHlIMupT9Jkw8A(this, i, i2, tL_error, tLObject, z, z2, z3, z4, str));
    }

    public /* synthetic */ void lambda$null$2$SearchAdapterHelper(int i, int i2, TL_error tL_error, TLObject tLObject, boolean z, boolean z2, boolean z3, boolean z4, String str) {
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
                        Chat chat2;
                        Peer peer = (Peer) arrayList.get(i6);
                        int i7 = peer.user_id;
                        if (i7 != 0) {
                            user2 = (User) sparseArray2.get(i7);
                            chat2 = null;
                        } else {
                            i7 = peer.chat_id;
                            if (i7 != 0) {
                                chat2 = (Chat) sparseArray.get(i7);
                            } else {
                                int i8 = peer.channel_id;
                                if (i8 != 0) {
                                    chat2 = (Chat) sparseArray.get(i8);
                                } else {
                                    chat2 = null;
                                    user2 = chat2;
                                }
                            }
                            user2 = null;
                        }
                        if (chat2 != null) {
                            if (z && (!z2 || ChatObject.canAddBotsToChat(chat2))) {
                                this.globalSearch.add(chat2);
                                this.globalSearchMap.put(-chat2.id, chat2);
                            }
                        } else if (!(user2 == null || z2 || ((!z3 && user2.bot) || (!z4 && user2.self)))) {
                            this.globalSearch.add(user2);
                            this.globalSearchMap.put(user2.id, user2);
                        }
                    }
                }
                if (!this.allResultsAreGlobal) {
                    while (i4 < tL_contacts_found.my_results.size()) {
                        Chat chat3;
                        Peer peer2 = (Peer) tL_contacts_found.my_results.get(i4);
                        int i9 = peer2.user_id;
                        if (i9 != 0) {
                            user = (User) sparseArray2.get(i9);
                            chat3 = null;
                        } else {
                            i9 = peer2.chat_id;
                            if (i9 != 0) {
                                chat3 = (Chat) sparseArray.get(i9);
                            } else {
                                i5 = peer2.channel_id;
                                if (i5 != 0) {
                                    chat3 = (Chat) sparseArray.get(i5);
                                } else {
                                    chat3 = null;
                                    user = chat3;
                                }
                            }
                            user = null;
                        }
                        if (chat3 != null) {
                            if (z && (!z2 || ChatObject.canAddBotsToChat(chat3))) {
                                this.localServerSearch.add(chat3);
                                this.globalSearchMap.put(-chat3.id, chat3);
                            }
                        } else if (!(user == null || z2 || ((!z3 && user.bot) || (!z4 && user.self)))) {
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
