package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
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
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsSearch;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
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

    public interface SearchAdapterHelperDelegate {
        SparseArray<User> getExcludeUsers();

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

    public static class HashtagObject {
        int date;
        String hashtag;
    }

    public SearchAdapterHelper(boolean global) {
        this.allResultsAreGlobal = global;
    }

    public void queryServerSearch(String query, boolean allowUsername, boolean allowChats, boolean allowBots, boolean allowSelf, int channelId, int type) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.channelReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.channelReqId, true);
            this.channelReqId = 0;
        }
        if (query == null) {
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
        TL_channels_getParticipants req;
        int currentReqId;
        if (query.length() <= 0) {
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.channelLastReqId = 0;
            this.delegate.onDataSetChanged();
        } else if (channelId != 0) {
            req = new TL_channels_getParticipants();
            if (type == 1) {
                req.filter = new TL_channelParticipantsAdmins();
            } else if (type == 3) {
                req.filter = new TL_channelParticipantsBanned();
            } else if (type == 0) {
                req.filter = new TL_channelParticipantsKicked();
            } else {
                req.filter = new TL_channelParticipantsSearch();
            }
            req.filter.q = query;
            req.limit = 50;
            req.offset = 0;
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(channelId);
            currentReqId = this.channelLastReqId + 1;
            this.channelLastReqId = currentReqId;
            this.channelReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SearchAdapterHelper$$Lambda$0(this, currentReqId, query), 2);
        } else {
            this.lastFoundChannel = query.toLowerCase();
        }
        if (!allowUsername) {
            return;
        }
        if (query.length() > 0) {
            req = new TL_contacts_search();
            req.q = query;
            req.limit = 50;
            currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SearchAdapterHelper$$Lambda$1(this, currentReqId, allowChats, allowBots, allowSelf, query), 2);
            return;
        }
        this.globalSearch.clear();
        this.globalSearchMap.clear();
        this.localServerSearch.clear();
        this.lastReqId = 0;
        this.delegate.onDataSetChanged();
    }

    final /* synthetic */ void lambda$queryServerSearch$1$SearchAdapterHelper(int currentReqId, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$8(this, currentReqId, error, response, query));
    }

    final /* synthetic */ void lambda$null$0$SearchAdapterHelper(int currentReqId, TL_error error, TLObject response, String query) {
        if (currentReqId == this.channelLastReqId && error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            this.lastFoundChannel = query.toLowerCase();
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.groupSearch.clear();
            this.groupSearchMap.clear();
            this.groupSearch.addAll(res.participants);
            int N = res.participants.size();
            for (int a = 0; a < N; a++) {
                ChannelParticipant participant = (ChannelParticipant) res.participants.get(a);
                this.groupSearchMap.put(participant.user_id, participant);
            }
            if (this.localSearchResults != null) {
                mergeResults(this.localSearchResults);
            }
            this.delegate.onDataSetChanged();
        }
        this.channelReqId = 0;
    }

    final /* synthetic */ void lambda$queryServerSearch$3$SearchAdapterHelper(int currentReqId, boolean allowChats, boolean allowBots, boolean allowSelf, String query, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$7(this, currentReqId, error, response, allowChats, allowBots, allowSelf, query));
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0092  */
    final /* synthetic */ void lambda$null$2$SearchAdapterHelper(int r15, org.telegram.tgnet.TLRPC.TL_error r16, org.telegram.tgnet.TLObject r17, boolean r18, boolean r19, boolean r20, java.lang.String r21) {
        /*
        r14 = this;
        r9 = r14.lastReqId;
        if (r15 != r9) goto L_0x0168;
    L_0x0004:
        if (r16 != 0) goto L_0x0168;
    L_0x0006:
        r6 = r17;
        r6 = (org.telegram.tgnet.TLRPC.TL_contacts_found) r6;
        r9 = r14.globalSearch;
        r9.clear();
        r9 = r14.globalSearchMap;
        r9.clear();
        r9 = r14.localServerSearch;
        r9.clear();
        r9 = r14.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r10 = r6.chats;
        r11 = 0;
        r9.putChats(r10, r11);
        r9 = r14.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r10 = r6.users;
        r11 = 0;
        r9.putUsers(r10, r11);
        r9 = r14.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r9);
        r10 = r6.users;
        r11 = r6.chats;
        r12 = 1;
        r13 = 1;
        r9.putUsersAndChats(r10, r11, r12, r13);
        r4 = new android.util.SparseArray;
        r4.<init>();
        r8 = new android.util.SparseArray;
        r8.<init>();
        r0 = 0;
    L_0x004b:
        r9 = r6.chats;
        r9 = r9.size();
        if (r0 >= r9) goto L_0x0063;
    L_0x0053:
        r9 = r6.chats;
        r3 = r9.get(r0);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        r9 = r3.id;
        r4.put(r9, r3);
        r0 = r0 + 1;
        goto L_0x004b;
    L_0x0063:
        r0 = 0;
    L_0x0064:
        r9 = r6.users;
        r9 = r9.size();
        if (r0 >= r9) goto L_0x007c;
    L_0x006c:
        r9 = r6.users;
        r7 = r9.get(r0);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
        r9 = r7.id;
        r8.put(r9, r7);
        r0 = r0 + 1;
        goto L_0x0064;
    L_0x007c:
        r2 = 0;
    L_0x007d:
        r9 = 2;
        if (r2 >= r9) goto L_0x00f3;
    L_0x0080:
        if (r2 != 0) goto L_0x00ad;
    L_0x0082:
        r9 = r14.allResultsAreGlobal;
        if (r9 != 0) goto L_0x0089;
    L_0x0086:
        r2 = r2 + 1;
        goto L_0x007d;
    L_0x0089:
        r1 = r6.my_results;
    L_0x008b:
        r0 = 0;
    L_0x008c:
        r9 = r1.size();
        if (r0 >= r9) goto L_0x0086;
    L_0x0092:
        r5 = r1.get(r0);
        r5 = (org.telegram.tgnet.TLRPC.Peer) r5;
        r7 = 0;
        r3 = 0;
        r9 = r5.user_id;
        if (r9 == 0) goto L_0x00b0;
    L_0x009e:
        r9 = r5.user_id;
        r7 = r8.get(r9);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
    L_0x00a6:
        if (r3 == 0) goto L_0x00d8;
    L_0x00a8:
        if (r18 != 0) goto L_0x00ca;
    L_0x00aa:
        r0 = r0 + 1;
        goto L_0x008c;
    L_0x00ad:
        r1 = r6.results;
        goto L_0x008b;
    L_0x00b0:
        r9 = r5.chat_id;
        if (r9 == 0) goto L_0x00bd;
    L_0x00b4:
        r9 = r5.chat_id;
        r3 = r4.get(r9);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        goto L_0x00a6;
    L_0x00bd:
        r9 = r5.channel_id;
        if (r9 == 0) goto L_0x00a6;
    L_0x00c1:
        r9 = r5.channel_id;
        r3 = r4.get(r9);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        goto L_0x00a6;
    L_0x00ca:
        r9 = r14.globalSearch;
        r9.add(r3);
        r9 = r14.globalSearchMap;
        r10 = r3.id;
        r10 = -r10;
        r9.put(r10, r3);
        goto L_0x00aa;
    L_0x00d8:
        if (r7 == 0) goto L_0x00aa;
    L_0x00da:
        if (r19 != 0) goto L_0x00e0;
    L_0x00dc:
        r9 = r7.bot;
        if (r9 != 0) goto L_0x00aa;
    L_0x00e0:
        if (r20 != 0) goto L_0x00e6;
    L_0x00e2:
        r9 = r7.self;
        if (r9 != 0) goto L_0x00aa;
    L_0x00e6:
        r9 = r14.globalSearch;
        r9.add(r7);
        r9 = r14.globalSearchMap;
        r10 = r7.id;
        r9.put(r10, r7);
        goto L_0x00aa;
    L_0x00f3:
        r9 = r14.allResultsAreGlobal;
        if (r9 != 0) goto L_0x0151;
    L_0x00f7:
        r0 = 0;
    L_0x00f8:
        r9 = r6.my_results;
        r9 = r9.size();
        if (r0 >= r9) goto L_0x0151;
    L_0x0100:
        r9 = r6.my_results;
        r5 = r9.get(r0);
        r5 = (org.telegram.tgnet.TLRPC.Peer) r5;
        r7 = 0;
        r3 = 0;
        r9 = r5.user_id;
        if (r9 == 0) goto L_0x0128;
    L_0x010e:
        r9 = r5.user_id;
        r7 = r8.get(r9);
        r7 = (org.telegram.tgnet.TLRPC.User) r7;
    L_0x0116:
        if (r3 == 0) goto L_0x0142;
    L_0x0118:
        r9 = r14.localServerSearch;
        r9.add(r3);
        r9 = r14.globalSearchMap;
        r10 = r3.id;
        r10 = -r10;
        r9.put(r10, r3);
    L_0x0125:
        r0 = r0 + 1;
        goto L_0x00f8;
    L_0x0128:
        r9 = r5.chat_id;
        if (r9 == 0) goto L_0x0135;
    L_0x012c:
        r9 = r5.chat_id;
        r3 = r4.get(r9);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        goto L_0x0116;
    L_0x0135:
        r9 = r5.channel_id;
        if (r9 == 0) goto L_0x0116;
    L_0x0139:
        r9 = r5.channel_id;
        r3 = r4.get(r9);
        r3 = (org.telegram.tgnet.TLRPC.Chat) r3;
        goto L_0x0116;
    L_0x0142:
        if (r7 == 0) goto L_0x0125;
    L_0x0144:
        r9 = r14.localServerSearch;
        r9.add(r7);
        r9 = r14.globalSearchMap;
        r10 = r7.id;
        r9.put(r10, r7);
        goto L_0x0125;
    L_0x0151:
        r9 = r21.toLowerCase();
        r14.lastFoundUsername = r9;
        r9 = r14.localSearchResults;
        if (r9 == 0) goto L_0x0160;
    L_0x015b:
        r9 = r14.localSearchResults;
        r14.mergeResults(r9);
    L_0x0160:
        r14.mergeExcludeResults();
        r9 = r14.delegate;
        r9.onDataSetChanged();
    L_0x0168:
        r9 = 0;
        r14.reqId = r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapterHelper.lambda$null$2$SearchAdapterHelper(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, boolean, boolean, boolean, java.lang.String):void");
    }

    public void unloadRecentHashtags() {
        this.hashtagsLoadedFromDb = false;
    }

    public boolean loadRecentHashtags() {
        if (this.hashtagsLoadedFromDb) {
            return true;
        }
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$2(this));
        return false;
    }

    final /* synthetic */ void lambda$loadRecentHashtags$6$SearchAdapterHelper() {
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
            ArrayList<HashtagObject> arrayList = new ArrayList();
            HashMap<String, HashtagObject> hashMap = new HashMap();
            while (cursor.next()) {
                HashtagObject hashtagObject = new HashtagObject();
                hashtagObject.hashtag = cursor.stringValue(0);
                hashtagObject.date = cursor.intValue(1);
                arrayList.add(hashtagObject);
                hashMap.put(hashtagObject.hashtag, hashtagObject);
            }
            cursor.dispose();
            Collections.sort(arrayList, SearchAdapterHelper$$Lambda$5.$instance);
            AndroidUtilities.runOnUIThread(new SearchAdapterHelper$$Lambda$6(this, arrayList, hashMap));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ int lambda$null$4$SearchAdapterHelper(HashtagObject lhs, HashtagObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void mergeResults(ArrayList<TLObject> localResults) {
        this.localSearchResults = localResults;
        if (this.globalSearchMap.size() != 0 && localResults != null) {
            int count = localResults.size();
            for (int a = 0; a < count; a++) {
                TLObject obj = (TLObject) localResults.get(a);
                if (obj instanceof User) {
                    User user = (User) obj;
                    User u = (User) this.globalSearchMap.get(user.id);
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
                    }
                    TLObject participant = (TLObject) this.groupSearchMap.get(user.id);
                    if (participant != null) {
                        this.groupSearch.remove(participant);
                        this.groupSearchMap.remove(user.id);
                    }
                } else if (obj instanceof Chat) {
                    Chat c = (Chat) this.globalSearchMap.get(-((Chat) obj).id);
                    if (c != null) {
                        this.globalSearch.remove(c);
                        this.localServerSearch.remove(c);
                        this.globalSearchMap.remove(-c.id);
                    }
                }
            }
        }
    }

    public void mergeExcludeResults() {
        if (this.delegate != null) {
            SparseArray<User> ignoreUsers = this.delegate.getExcludeUsers();
            if (ignoreUsers != null) {
                int size = ignoreUsers.size();
                for (int a = 0; a < size; a++) {
                    User u = (User) this.globalSearchMap.get(ignoreUsers.keyAt(a));
                    if (u != null) {
                        this.globalSearch.remove(u);
                        this.localServerSearch.remove(u);
                        this.globalSearchMap.remove(u.id);
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
            Matcher matcher = Pattern.compile("(^|\\s)#[\\w@.]+").matcher(message);
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

    private void putRecentHashtags(ArrayList<HashtagObject> arrayList) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$3(this, arrayList));
    }

    final /* synthetic */ void lambda$putRecentHashtags$7$SearchAdapterHelper(ArrayList arrayList) {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
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
            MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            if (arrayList.size() >= 100) {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().beginTransaction();
                for (a = 100; a < arrayList.size(); a++) {
                    MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((HashtagObject) arrayList.get(a)).hashtag + "'").stepThis().dispose();
                }
                MessagesStorage.getInstance(this.currentAccount).getDatabase().commitTransaction();
            }
        } catch (Throwable e) {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SearchAdapterHelper$$Lambda$4(this));
    }

    final /* synthetic */ void lambda$clearRecentHashtags$8$SearchAdapterHelper() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
        } catch (Throwable e) {
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
