package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LayoutAnimationController;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilteredSearchView;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentItemCount;
    private String currentMessagesQuery;
    /* access modifiers changed from: private */
    public DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private FilteredSearchView.Delegate filtersDelegate;
    private int folderId;
    private RecyclerListView innerListView;
    /* access modifiers changed from: private */
    public int lastGlobalSearchId;
    /* access modifiers changed from: private */
    public int lastLocalSearchId;
    /* access modifiers changed from: private */
    public int lastMessagesSearchId;
    private String lastMessagesSearchString;
    private int lastReqId;
    /* access modifiers changed from: private */
    public int lastSearchId;
    private String lastSearchText;
    private boolean localTipArchive;
    private ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList<>();
    private LongSparseArray<RecentSearchObject> recentSearchObjectsById = new LongSparseArray<>();
    private int reqId = 0;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public ArrayList<Object> searchResult = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> searchResultHashtags = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    private Runnable searchRunnable;
    private Runnable searchRunnable2;
    /* access modifiers changed from: private */
    public boolean searchWas;
    private long selfUserId;
    int waitingResponseCount;

    public static class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;
    }

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(long j);

        boolean isSelected(long j);

        void needClearList();

        void needRemoveHint(long j);

        void runResultsEnterAnimation();

        void searchStateChanged(boolean z, boolean z2);
    }

    public interface OnRecentSearchLoaded {
        void setRecentSearch(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray);
    }

    public static class RecentSearchObject {
        public int date;
        public long did;
        public TLObject object;
    }

    public boolean isSearching() {
        return this.waitingResponseCount > 0;
    }

    public static class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
        private final int currentAccount;
        private boolean drawChecked;
        private final Context mContext;

        public CategoryAdapterRecycler(Context context, int account, boolean drawChecked2) {
            this.drawChecked = drawChecked2;
            this.mContext = context;
            this.currentAccount = account;
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new HintDialogCell(this.mContext, this.drawChecked);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(view);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            HintDialogCell cell = (HintDialogCell) holder.itemView;
            TLRPC.TL_topPeer peer = MediaDataController.getInstance(this.currentAccount).hints.get(position);
            new TLRPC.TL_dialog();
            TLRPC.Chat chat = null;
            TLRPC.User user = null;
            long did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
                user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peer.peer.user_id));
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
                chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peer.peer.channel_id));
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
                chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peer.peer.chat_id));
            }
            cell.setTag(Long.valueOf(did));
            String name = "";
            if (user != null) {
                name = UserObject.getFirstName(user);
            } else if (chat != null) {
                name = chat.title;
            }
            cell.setDialog(did, true, name);
        }

        public int getItemCount() {
            return MediaDataController.getInstance(this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type) {
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ LongSparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int searchId) {
                DialogsSearchAdapter.this.waitingResponseCount--;
                int unused = DialogsSearchAdapter.this.lastGlobalSearchId = searchId;
                if (DialogsSearchAdapter.this.lastLocalSearchId != searchId) {
                    DialogsSearchAdapter.this.searchResult.clear();
                }
                if (DialogsSearchAdapter.this.lastMessagesSearchId != searchId) {
                    DialogsSearchAdapter.this.searchResultMessages.clear();
                }
                boolean unused2 = DialogsSearchAdapter.this.searchWas = true;
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(DialogsSearchAdapter.this.waitingResponseCount > 0, true);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.runResultsEnterAnimation();
                }
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                for (int a = 0; a < arrayList.size(); a++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(arrayList.get(a).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(DialogsSearchAdapter.this.waitingResponseCount > 0, false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            public boolean canApplySearchResults(int searchId) {
                return searchId == DialogsSearchAdapter.this.lastSearchId;
            }
        });
        this.mContext = context;
        this.needMessagesSearch = messagesSearch;
        this.dialogsType = type;
        this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        loadRecentSearch();
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
    }

    public RecyclerListView getInnerListView() {
        return this.innerListView;
    }

    public void setDelegate(DialogsSearchAdapterDelegate delegate2) {
        this.delegate = delegate2;
    }

    public boolean isMessagesSearchEndReached() {
        return this.messagesSearchEndReached;
    }

    public void loadMoreSearchMessages() {
        if (this.reqId == 0) {
            searchMessagesInternal(this.lastMessagesSearchString, this.lastMessagesSearchId);
        }
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    private void searchMessagesInternal(String query, int searchId) {
        if (this.needMessagesSearch == 0) {
            return;
        }
        if (!TextUtils.isEmpty(this.lastMessagesSearchString) || !TextUtils.isEmpty(query)) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                return;
            }
            TLRPC.TL_messages_searchGlobal req = new TLRPC.TL_messages_searchGlobal();
            req.limit = 20;
            req.q = query;
            req.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            req.flags |= 1;
            req.folder_id = this.folderId;
            if (!query.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                req.offset_rate = 0;
                req.offset_id = 0;
                req.offset_peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                MessageObject lastMessage = arrayList.get(arrayList.size() - 1);
                req.offset_id = lastMessage.getId();
                req.offset_rate = this.nextSearchRate;
                req.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(lastMessage.messageOwner.peer_id));
            }
            this.lastMessagesSearchString = query;
            int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DialogsSearchAdapter$$ExternalSyntheticLambda6(this, query, currentReqId, searchId, req), 2);
        }
    }

    /* renamed from: lambda$searchMessagesInternal$1$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1368xbfab1706(String query, int currentReqId, int searchId, TLRPC.TL_messages_searchGlobal req, TLObject response, TLRPC.TL_error error) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            LongSparseArray<TLRPC.Chat> chatsMap = new LongSparseArray<>();
            LongSparseArray<TLRPC.User> usersMap = new LongSparseArray<>();
            for (int a = 0; a < res.chats.size(); a++) {
                TLRPC.Chat chat = res.chats.get(a);
                chatsMap.put(chat.id, chat);
            }
            for (int a2 = 0; a2 < res.users.size(); a2++) {
                TLRPC.User user = res.users.get(a2);
                usersMap.put(user.id, user);
            }
            for (int a3 = 0; a3 < res.messages.size(); a3++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, res.messages.get(a3), usersMap, chatsMap, false, true);
                messageObjects.add(messageObject);
                messageObject.setQuery(query);
            }
            String str = query;
        } else {
            String str2 = query;
        }
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda15(this, currentReqId, searchId, error, query, response, req, messageObjects));
    }

    /* renamed from: lambda$searchMessagesInternal$0$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1367x4a30f0c5(int currentReqId, int searchId, TLRPC.TL_error error, String query, TLObject response, TLRPC.TL_messages_searchGlobal req, ArrayList messageObjects) {
        int i = searchId;
        if (currentReqId != this.lastReqId) {
            TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal = req;
            ArrayList arrayList = messageObjects;
        } else if (i <= 0 || i == this.lastSearchId) {
            this.waitingResponseCount--;
            if (error == null) {
                this.currentMessagesQuery = query;
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
                if (req.offset_id == 0) {
                    this.searchResultMessages.clear();
                }
                this.nextSearchRate = res.next_rate;
                int a = 0;
                while (a < res.messages.size()) {
                    TLRPC.Message message = res.messages.get(a);
                    int maxId = MessagesController.getInstance(this.currentAccount).deletedHistory.get(MessageObject.getDialogId(message));
                    if (maxId == 0 || message.id > maxId) {
                        this.searchResultMessages.add((MessageObject) messageObjects.get(a));
                        long dialog_id = MessageObject.getDialogId(message);
                        ConcurrentHashMap<Long, Integer> concurrentHashMap = message.out ? MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(this.currentAccount).dialogs_read_inbox_max;
                        Integer value = concurrentHashMap.get(Long.valueOf(dialog_id));
                        if (value == null) {
                            value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, dialog_id));
                            concurrentHashMap.put(Long.valueOf(dialog_id), value);
                        }
                        message.unread = value.intValue() < message.id;
                    } else {
                        ArrayList arrayList2 = messageObjects;
                    }
                    a++;
                    int i2 = currentReqId;
                    String str = query;
                }
                ArrayList arrayList3 = messageObjects;
                this.searchWas = true;
                this.messagesSearchEndReached = res.messages.size() != 20;
                if (i > 0) {
                    this.lastMessagesSearchId = i;
                    if (this.lastLocalSearchId != i) {
                        this.searchResult.clear();
                    }
                    if (this.lastGlobalSearchId != i) {
                        this.searchAdapterHelper.clear();
                    }
                }
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                    this.delegate.runResultsEnterAnimation();
                }
                notifyDataSetChanged();
            } else {
                TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal2 = req;
                ArrayList arrayList4 = messageObjects;
            }
        } else {
            TLRPC.TL_messages_searchGlobal tL_messages_searchGlobal3 = req;
            ArrayList arrayList5 = messageObjects;
        }
        this.reqId = 0;
    }

    public boolean hasRecentSearch() {
        int i = this.dialogsType;
        return (i == 2 || i == 4 || i == 5 || i == 6 || i == 11 || (this.recentSearchObjects.isEmpty() && MediaDataController.getInstance(this.currentAccount).hints.isEmpty())) ? false : true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001f, code lost:
        r0 = r2.dialogsType;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRecentSearchDisplayed() {
        /*
            r2 = this;
            int r0 = r2.needMessagesSearch
            r1 = 2
            if (r0 == r1) goto L_0x0032
            boolean r0 = r2.searchWas
            if (r0 != 0) goto L_0x0032
            java.util.ArrayList<org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject> r0 = r2.recentSearchObjects
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x001f
            int r0 = r2.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r0 = r0.hints
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0032
        L_0x001f:
            int r0 = r2.dialogsType
            if (r0 == r1) goto L_0x0032
            r1 = 4
            if (r0 == r1) goto L_0x0032
            r1 = 5
            if (r0 == r1) goto L_0x0032
            r1 = 6
            if (r0 == r1) goto L_0x0032
            r1 = 11
            if (r0 == r1) goto L_0x0032
            r0 = 1
            goto L_0x0033
        L_0x0032:
            r0 = 0
        L_0x0033:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.isRecentSearchDisplayed():boolean");
    }

    public void loadRecentSearch() {
        loadRecentSearch(this.currentAccount, this.dialogsType, new DialogsSearchAdapter$$ExternalSyntheticLambda7(this));
    }

    public static void loadRecentSearch(int currentAccount2, int dialogsType2, OnRecentSearchLoaded callback) {
        MessagesStorage.getInstance(currentAccount2).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda11(currentAccount2, dialogsType2, callback));
    }

    static /* synthetic */ void lambda$loadRecentSearch$5(int currentAccount2, int dialogsType2, OnRecentSearchLoaded callback) {
        int i = dialogsType2;
        try {
            SQLiteCursor cursor = MessagesStorage.getInstance(currentAccount2).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            ArrayList<Integer> encryptedToLoad = new ArrayList<>();
            new ArrayList();
            ArrayList<RecentSearchObject> arrayList = new ArrayList<>();
            LongSparseArray<RecentSearchObject> hashMap = new LongSparseArray<>();
            while (cursor.next()) {
                long did = cursor.longValue(0);
                boolean add = false;
                if (DialogObject.isEncryptedDialog(did)) {
                    if (i == 0 || i == 3) {
                        int encryptedChatId = DialogObject.getEncryptedChatId(did);
                        if (!encryptedToLoad.contains(Integer.valueOf(encryptedChatId))) {
                            encryptedToLoad.add(Integer.valueOf(encryptedChatId));
                            add = true;
                        }
                    }
                } else if (DialogObject.isUserDialog(did)) {
                    if (i != 2 && !usersToLoad.contains(Long.valueOf(did))) {
                        usersToLoad.add(Long.valueOf(did));
                        add = true;
                    }
                } else if (!chatsToLoad.contains(Long.valueOf(-did))) {
                    chatsToLoad.add(Long.valueOf(-did));
                    add = true;
                }
                if (add) {
                    RecentSearchObject recentSearchObject = new RecentSearchObject();
                    recentSearchObject.did = did;
                    recentSearchObject.date = cursor.intValue(1);
                    arrayList.add(recentSearchObject);
                    hashMap.put(recentSearchObject.did, recentSearchObject);
                }
            }
            cursor.dispose();
            ArrayList<TLRPC.User> users = new ArrayList<>();
            if (!encryptedToLoad.isEmpty()) {
                ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
                MessagesStorage.getInstance(currentAccount2).getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
                for (int a = 0; a < encryptedChats.size(); a++) {
                    RecentSearchObject recentSearchObject2 = hashMap.get(DialogObject.makeEncryptedDialogId((long) encryptedChats.get(a).id));
                    if (recentSearchObject2 != null) {
                        recentSearchObject2.object = encryptedChats.get(a);
                    }
                }
            }
            if (!chatsToLoad.isEmpty()) {
                ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                MessagesStorage.getInstance(currentAccount2).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                for (int a2 = 0; a2 < chats.size(); a2++) {
                    TLRPC.Chat chat = chats.get(a2);
                    long did2 = -chat.id;
                    if (chat.migrated_to != null) {
                        RecentSearchObject recentSearchObject3 = hashMap.get(did2);
                        hashMap.remove(did2);
                        if (recentSearchObject3 != null) {
                            arrayList.remove(recentSearchObject3);
                        }
                    } else {
                        RecentSearchObject recentSearchObject4 = hashMap.get(did2);
                        if (recentSearchObject4 != null) {
                            recentSearchObject4.object = chat;
                        }
                    }
                }
            }
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(currentAccount2).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                for (int a3 = 0; a3 < users.size(); a3++) {
                    TLRPC.User user = users.get(a3);
                    RecentSearchObject recentSearchObject5 = hashMap.get(user.id);
                    if (recentSearchObject5 != null) {
                        recentSearchObject5.object = user;
                    }
                }
            }
            Collections.sort(arrayList, DialogsSearchAdapter$$ExternalSyntheticLambda4.INSTANCE);
            try {
                AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda12(callback, arrayList, hashMap));
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
            OnRecentSearchLoaded onRecentSearchLoaded = callback;
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$loadRecentSearch$3(RecentSearchObject lhs, RecentSearchObject rhs) {
        if (lhs.date < rhs.date) {
            return 1;
        }
        if (lhs.date > rhs.date) {
            return -1;
        }
        return 0;
    }

    public void putRecentSearch(long did, TLObject object) {
        RecentSearchObject recentSearchObject = this.recentSearchObjectsById.get(did);
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(did, recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = did;
        recentSearchObject.object = object;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda18(this, did));
    }

    /* renamed from: lambda$putRecentSearch$6$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1361x2f7d5292(long did) {
        try {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            state.requery();
            state.bindLong(1, did);
            state.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new LongSparseArray<>();
        this.recentSearchObjects = new ArrayList<>();
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda13(this));
    }

    /* renamed from: lambda$clearRecentSearch$7$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1355x988c9e51() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void removeRecentSearch(long did) {
        RecentSearchObject object = this.recentSearchObjectsById.get(did);
        if (object != null) {
            this.recentSearchObjectsById.remove(did);
            this.recentSearchObjects.remove(object);
            notifyDataSetChanged();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda1(this, did));
        }
    }

    /* renamed from: lambda$removeRecentSearch$8$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1362xfb99f9e3(long did) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            database.executeFast("DELETE FROM search_recent WHERE did = " + did).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    /* access modifiers changed from: private */
    /* renamed from: setRecentSearch */
    public void m1356xa34cacff(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int a = 0; a < this.recentSearchObjects.size(); a++) {
            RecentSearchObject recentSearchObject = this.recentSearchObjects.get(a);
            if (recentSearchObject.object instanceof TLRPC.User) {
                MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof TLRPC.EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(String query, int searchId) {
        if (this.needMessagesSearch != 2) {
            String q = query.trim().toLowerCase();
            if (q.length() == 0) {
                this.lastSearchId = 0;
                updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), this.lastSearchId);
                return;
            }
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda3(this, q, searchId, query));
        }
    }

    /* renamed from: lambda$searchDialogsInternal$10$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1365x16ee3c0b(String q, int searchId, String query) {
        ArrayList<Object> resultArray = new ArrayList<>();
        ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
        ArrayList<TLRPC.User> encUsers = new ArrayList<>();
        MessagesStorage.getInstance(this.currentAccount).localSearch(this.dialogsType, q, resultArray, resultArrayNames, encUsers, -1);
        updateSearchResults(resultArray, resultArrayNames, encUsers, searchId);
        FiltersView.fillTipDates(q, this.localTipDates);
        this.localTipArchive = false;
        if (q.length() >= 3 && (LocaleController.getString("ArchiveSearchFilter", NUM).toLowerCase().startsWith(q) || "archive".startsWith(query))) {
            this.localTipArchive = true;
        }
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda14(this));
    }

    /* renamed from: lambda$searchDialogsInternal$9$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1366xd91b03c3() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(ArrayList<Object> result, ArrayList<CharSequence> names, ArrayList<TLRPC.User> encUsers, int searchId) {
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda17(this, searchId, result, encUsers, names));
    }

    /* renamed from: lambda$updateSearchResults$12$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1370xbfa838c8(int searchId, ArrayList result, ArrayList encUsers, ArrayList names) {
        this.waitingResponseCount--;
        if (searchId == this.lastSearchId) {
            this.lastLocalSearchId = searchId;
            if (this.lastGlobalSearchId != searchId) {
                this.searchAdapterHelper.clear();
            }
            if (this.lastMessagesSearchId != searchId) {
                this.searchResultMessages.clear();
            }
            this.searchWas = true;
            for (int a = 0; a < result.size(); a++) {
                Object obj = result.get(a);
                long dialogId = 0;
                if (obj instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) obj;
                    MessagesController.getInstance(this.currentAccount).putUser(user, true);
                    dialogId = user.id;
                } else if (obj instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) obj;
                    MessagesController.getInstance(this.currentAccount).putChat(chat, true);
                    dialogId = -chat.id;
                } else if (obj instanceof TLRPC.EncryptedChat) {
                    MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat) obj, true);
                }
                if (dialogId != 0 && MessagesController.getInstance(this.currentAccount).dialogs_dict.get(dialogId) == null) {
                    MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(dialogId, new DialogsSearchAdapter$$ExternalSyntheticLambda5(this, dialogId, obj));
                }
            }
            MessagesController.getInstance(this.currentAccount).putUsers(encUsers, true);
            this.searchResult = result;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(result);
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
            if (dialogsSearchAdapterDelegate != null) {
                dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                this.delegate.runResultsEnterAnimation();
            }
        }
    }

    /* renamed from: lambda$updateSearchResults$11$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1369x4a2e1287(long finalDialogId, Object obj, int param) {
        if (param != -1) {
            TLRPC.Dialog newDialog = new TLRPC.TL_dialog();
            newDialog.id = finalDialogId;
            if (param != 0) {
                newDialog.folder_id = param;
            }
            if (obj instanceof TLRPC.Chat) {
                newDialog.flags = ChatObject.isChannel((TLRPC.Chat) obj) ? 1 : 0;
            }
            MessagesController.getInstance(this.currentAccount).dialogs_dict.put(finalDialogId, newDialog);
            MessagesController.getInstance(this.currentAccount).getAllDialogs().add(newDialog);
            MessagesController.getInstance(this.currentAccount).sortDialogs((LongSparseArray<TLRPC.Chat>) null);
        }
    }

    public boolean isHashtagSearch() {
        return !this.searchResultHashtags.isEmpty();
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(String text, int folderId2) {
        String query;
        String str = text;
        int i = folderId2;
        if (str == null || !str.equals(this.lastSearchText) || (i != this.folderId && !TextUtils.isEmpty(text))) {
            this.lastSearchText = str;
            this.folderId = i;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            Runnable runnable = this.searchRunnable2;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable2 = null;
            }
            if (str != null) {
                query = text.trim();
            } else {
                query = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                SearchAdapterHelper searchAdapterHelper2 = this.searchAdapterHelper;
                int i2 = this.dialogsType;
                searchAdapterHelper2.queryServerSearch((String) null, true, true, i2 != 11, i2 != 11, i2 == 2 || i2 == 11, 0, i2 == 0, 0, 0);
                this.searchWas = false;
                this.lastSearchId = 0;
                this.waitingResponseCount = 0;
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    dialogsSearchAdapterDelegate.searchStateChanged(false, true);
                }
                searchMessagesInternal((String) null, 0);
                notifyDataSetChanged();
                this.localTipDates.clear();
                this.localTipArchive = false;
                FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
                if (delegate2 != null) {
                    delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, false);
                    return;
                }
                return;
            }
            if (this.needMessagesSearch == 2 || !query.startsWith("#") || query.length() != 1) {
                this.searchResultHashtags.clear();
            } else {
                this.messagesSearchEndReached = true;
                if (this.searchAdapterHelper.loadRecentHashtags()) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    ArrayList<SearchAdapterHelper.HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                    for (int a = 0; a < hashtags.size(); a++) {
                        this.searchResultHashtags.add(hashtags.get(a).hashtag);
                    }
                    this.waitingResponseCount = 0;
                    notifyDataSetChanged();
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate2 = this.delegate;
                    if (dialogsSearchAdapterDelegate2 != null) {
                        dialogsSearchAdapterDelegate2.searchStateChanged(false, false);
                    }
                }
            }
            int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            this.waitingResponseCount = 3;
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate3 = this.delegate;
            if (dialogsSearchAdapterDelegate3 != null) {
                dialogsSearchAdapterDelegate3.searchStateChanged(true, false);
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            DialogsSearchAdapter$$ExternalSyntheticLambda2 dialogsSearchAdapter$$ExternalSyntheticLambda2 = new DialogsSearchAdapter$$ExternalSyntheticLambda2(this, query, searchId, str);
            this.searchRunnable = dialogsSearchAdapter$$ExternalSyntheticLambda2;
            dispatchQueue.postRunnable(dialogsSearchAdapter$$ExternalSyntheticLambda2, 300);
        }
    }

    /* renamed from: lambda$searchDialogs$14$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1364x4783704c(String query, int searchId, String text) {
        this.searchRunnable = null;
        searchDialogsInternal(query, searchId);
        DialogsSearchAdapter$$ExternalSyntheticLambda16 dialogsSearchAdapter$$ExternalSyntheticLambda16 = new DialogsSearchAdapter$$ExternalSyntheticLambda16(this, searchId, query, text);
        this.searchRunnable2 = dialogsSearchAdapter$$ExternalSyntheticLambda16;
        AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda16);
    }

    /* renamed from: lambda$searchDialogs$13$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1363xd2094a0b(int searchId, String query, String text) {
        int i = searchId;
        this.searchRunnable2 = null;
        if (i == this.lastSearchId) {
            if (this.needMessagesSearch != 2) {
                SearchAdapterHelper searchAdapterHelper2 = this.searchAdapterHelper;
                int i2 = this.dialogsType;
                searchAdapterHelper2.queryServerSearch(query, true, i2 != 4, true, (i2 == 4 || i2 == 11) ? false : true, i2 == 2 || i2 == 1, 0, i2 == 0, 0, searchId);
            } else {
                this.waitingResponseCount -= 2;
            }
            if (this.needMessagesSearch == 0) {
                this.waitingResponseCount--;
                String str = text;
                return;
            }
            searchMessagesInternal(text, i);
        }
    }

    public int getItemCount() {
        int i = 0;
        if (this.waitingResponseCount == 3) {
            return 0;
        }
        if (isRecentSearchDisplayed()) {
            if (!this.recentSearchObjects.isEmpty()) {
                i = this.recentSearchObjects.size() + 1;
            }
            return i + (MediaDataController.getInstance(this.currentAccount).hints.isEmpty() ^ true ? 1 : 0);
        } else if (!this.searchResultHashtags.isEmpty()) {
            return 0 + this.searchResultHashtags.size() + 1;
        } else {
            int count = 0 + this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            int messagesCount = this.searchResultMessages.size();
            int count2 = count + localServerCount;
            if (globalCount != 0) {
                count2 += globalCount + 1;
            }
            if (phoneCount != 0) {
                count2 += phoneCount;
            }
            if (messagesCount != 0) {
                count2 += messagesCount + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0);
            }
            this.currentItemCount = count2;
            return count2;
        }
    }

    public Object getItem(int i) {
        TLObject chat;
        if (isRecentSearchDisplayed()) {
            int offset = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty();
            if (i <= offset || (i - 1) - offset >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject object = this.recentSearchObjects.get((i - 1) - offset).object;
            if (object instanceof TLRPC.User) {
                TLObject user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC.User) object).id));
                if (user != null) {
                    return user;
                }
                return object;
            } else if (!(object instanceof TLRPC.Chat) || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(((TLRPC.Chat) object).id))) == null) {
                return object;
            } else {
                return chat;
            }
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
            int localCount = this.searchResult.size();
            int localServerCount = localServerSearch.size();
            int phoneCount = phoneSearch.size();
            int messagesCount = 0;
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                messagesCount = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < localCount) {
                return this.searchResult.get(i);
            }
            int i2 = i - localCount;
            if (i2 >= 0 && i2 < localServerCount) {
                return localServerSearch.get(i2);
            }
            int i3 = i2 - localServerCount;
            if (i3 >= 0 && i3 < phoneCount) {
                return phoneSearch.get(i3);
            }
            int i4 = i3 - phoneCount;
            if (i4 > 0 && i4 < globalCount) {
                return globalSearch.get(i4 - 1);
            }
            int i5 = i4 - globalCount;
            if (i5 <= 0 || i5 >= messagesCount) {
                return null;
            }
            return this.searchResultMessages.get(i5 - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public boolean isGlobalSearch(int i) {
        if (isRecentSearchDisplayed() || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int localCount = this.searchResult.size();
        int localServerCount = localServerSearch.size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (!this.searchResultMessages.isEmpty()) {
            int size = this.searchResultMessages.size() + 1;
        }
        if (i >= 0 && i < localCount) {
            return false;
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < localServerCount) {
            return false;
        }
        int i3 = i2 - localServerCount;
        if (i3 > 0 && i3 < phoneCount) {
            return false;
        }
        int i4 = i3 - phoneCount;
        if (i4 > 0 && i4 < globalCount) {
            return true;
        }
        int i5 = i4 - globalCount;
        return false;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return (type == 1 || type == 3) ? false : true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new ProfileSearchCell(this.mContext);
                break;
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            case 2:
                view = new DialogCell((DialogsActivity) null, this.mContext, false, true);
                break;
            case 3:
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setViewType(1);
                flickerLoadingView.setIsSingleCell(true);
                view = flickerLoadingView;
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                AnonymousClass2 r0 = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent e) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            ViewParent parent = getParent().getParent();
                            boolean z = true;
                            if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                                z = false;
                            }
                            parent.requestDisallowInterceptTouchEvent(z);
                        }
                        return super.onInterceptTouchEvent(e);
                    }
                };
                r0.setTag(9);
                r0.setItemAnimator((RecyclerView.ItemAnimator) null);
                r0.setLayoutAnimation((LayoutAnimationController) null);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                layoutManager.setOrientation(0);
                r0.setLayoutManager(layoutManager);
                r0.setAdapter(new CategoryAdapterRecycler(this.mContext, this.currentAccount, false));
                r0.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DialogsSearchAdapter$$ExternalSyntheticLambda8(this));
                r0.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new DialogsSearchAdapter$$ExternalSyntheticLambda9(this));
                view = r0;
                this.innerListView = r0;
                break;
            default:
                view = new TextCell(this.mContext, 16, false);
                break;
        }
        if (viewType == 5) {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$15$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1359xCLASSNAMEb56a(View view1, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog(((Long) view1.getTag()).longValue());
        }
    }

    /* renamed from: lambda$onCreateViewHolder$16$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ boolean m1360x38f2dbab(View view12, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Long) view12.getTag()).longValue());
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v0, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x038f  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0410  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r31, int r32) {
        /*
            r30 = this;
            r1 = r30
            r2 = r31
            r3 = r32
            int r0 = r31.getItemViewType()
            r4 = 0
            r5 = 1
            switch(r0) {
                case 0: goto L_0x0172;
                case 1: goto L_0x00a0;
                case 2: goto L_0x007c;
                case 3: goto L_0x000f;
                case 4: goto L_0x005d;
                case 5: goto L_0x004c;
                case 6: goto L_0x0011;
                default: goto L_0x000f;
            }
        L_0x000f:
            goto L_0x0415
        L_0x0011:
            java.lang.Object r0 = r1.getItem(r3)
            java.lang.String r0 = (java.lang.String) r0
            android.view.View r6 = r2.itemView
            org.telegram.ui.Cells.TextCell r6 = (org.telegram.ui.Cells.TextCell) r6
            r7 = 0
            java.lang.String r8 = "windowBackgroundWhiteBlueText2"
            r6.setColors(r7, r8)
            r7 = 2131624208(0x7f0e0110, float:1.887559E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.PhoneFormat.PhoneFormat r8 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "+"
            r9.append(r10)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            java.lang.String r8 = r8.format(r9)
            r5[r4] = r8
            java.lang.String r8 = "AddContactByPhone"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r7, r5)
            r6.setText(r5, r4)
            goto L_0x0415
        L_0x004c:
            android.view.View r0 = r2.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r4 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r4
            int r5 = r3 / 2
            r4.setIndex(r5)
            goto L_0x0415
        L_0x005d:
            android.view.View r0 = r2.itemView
            org.telegram.ui.Cells.HashtagSearchCell r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0
            java.util.ArrayList<java.lang.String> r6 = r1.searchResultHashtags
            int r7 = r3 + -1
            java.lang.Object r6 = r6.get(r7)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            r0.setText(r6)
            java.util.ArrayList<java.lang.String> r6 = r1.searchResultHashtags
            int r6 = r6.size()
            if (r3 == r6) goto L_0x0077
            r4 = 1
        L_0x0077:
            r0.setNeedDivider(r4)
            goto L_0x0415
        L_0x007c:
            android.view.View r0 = r2.itemView
            org.telegram.ui.Cells.DialogCell r0 = (org.telegram.ui.Cells.DialogCell) r0
            int r6 = r30.getItemCount()
            int r6 = r6 - r5
            if (r3 == r6) goto L_0x0088
            r4 = 1
        L_0x0088:
            r0.useSeparator = r4
            java.lang.Object r4 = r1.getItem(r3)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            long r7 = r4.getDialogId()
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            int r10 = r5.date
            r11 = 0
            r6 = r0
            r9 = r4
            r6.setDialog(r7, r9, r10, r11)
            goto L_0x0415
        L_0x00a0:
            android.view.View r0 = r2.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            boolean r6 = r30.isRecentSearchDisplayed()
            r7 = 2131624982(0x7f0e0416, float:1.887716E38)
            java.lang.String r8 = "ClearButton"
            if (r6 == 0) goto L_0x00e2
            int r4 = r1.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r4 = r4.hints
            boolean r4 = r4.isEmpty()
            r4 = r4 ^ r5
            if (r3 >= r4) goto L_0x00cb
            r5 = 2131624868(0x7f0e03a4, float:1.8876928E38)
            java.lang.String r6 = "ChatHints"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setText(r5)
            goto L_0x00e0
        L_0x00cb:
            r5 = 2131627424(0x7f0e0da0, float:1.8882112E38)
            java.lang.String r6 = "Recent"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0 r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0
            r7.<init>(r1)
            r0.setText(r5, r6, r7)
        L_0x00e0:
            goto L_0x0415
        L_0x00e2:
            java.util.ArrayList<java.lang.String> r6 = r1.searchResultHashtags
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0101
            r4 = 2131625884(0x7f0e079c, float:1.8878989E38)
            java.lang.String r5 = "Hashtags"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r7)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda10 r6 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda10
            r6.<init>(r1)
            r0.setText(r4, r5, r6)
            goto L_0x0415
        L_0x0101:
            org.telegram.ui.Adapters.SearchAdapterHelper r6 = r1.searchAdapterHelper
            java.util.ArrayList r6 = r6.getGlobalSearch()
            java.util.ArrayList<java.lang.Object> r7 = r1.searchResult
            int r7 = r7.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
            java.util.ArrayList r8 = r8.getLocalServerSearch()
            int r8 = r8.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r9 = r9.getPhoneSearch()
            int r9 = r9.size()
            boolean r10 = r6.isEmpty()
            if (r10 == 0) goto L_0x0129
            r10 = 0
            goto L_0x012e
        L_0x0129:
            int r10 = r6.size()
            int r10 = r10 + r5
        L_0x012e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r11 = r1.searchResultMessages
            boolean r11 = r11.isEmpty()
            if (r11 == 0) goto L_0x0137
            goto L_0x013e
        L_0x0137:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.searchResultMessages
            int r4 = r4.size()
            int r4 = r4 + r5
        L_0x013e:
            int r5 = r7 + r8
            int r3 = r3 - r5
            if (r3 < 0) goto L_0x0152
            if (r3 >= r9) goto L_0x0152
            r5 = 2131627152(0x7f0e0CLASSNAME, float:1.888156E38)
            java.lang.String r11 = "PhoneNumberSearch"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r0.setText(r5)
            goto L_0x0170
        L_0x0152:
            int r3 = r3 - r9
            if (r3 < 0) goto L_0x0164
            if (r3 >= r10) goto L_0x0164
            r5 = 2131625836(0x7f0e076c, float:1.8878891E38)
            java.lang.String r11 = "GlobalSearch"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r0.setText(r5)
            goto L_0x0170
        L_0x0164:
            r5 = 2131627642(0x7f0e0e7a, float:1.8882554E38)
            java.lang.String r11 = "SearchMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r0.setText(r5)
        L_0x0170:
            goto L_0x0417
        L_0x0172:
            android.view.View r0 = r2.itemView
            r13 = r0
            org.telegram.ui.Cells.ProfileSearchCell r13 = (org.telegram.ui.Cells.ProfileSearchCell) r13
            long r14 = r13.getDialogId()
            r0 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            java.lang.Object r12 = r1.getItem(r3)
            boolean r4 = r12 instanceof org.telegram.tgnet.TLRPC.User
            if (r4 == 0) goto L_0x0194
            r0 = r12
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            java.lang.String r11 = r0.username
            r4 = r0
            r5 = r6
            r18 = r7
            goto L_0x01f0
        L_0x0194:
            boolean r4 = r12 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r4 == 0) goto L_0x01bc
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r5 = r12
            org.telegram.tgnet.TLRPC$Chat r5 = (org.telegram.tgnet.TLRPC.Chat) r5
            r18 = r6
            long r5 = r5.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            if (r4 != 0) goto L_0x01b4
            r4 = r12
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            r6 = r4
            goto L_0x01b5
        L_0x01b4:
            r6 = r4
        L_0x01b5:
            java.lang.String r11 = r6.username
            r4 = r0
            r5 = r6
            r18 = r7
            goto L_0x01f0
        L_0x01bc:
            r18 = r6
            boolean r4 = r12 instanceof org.telegram.tgnet.TLRPC.EncryptedChat
            if (r4 == 0) goto L_0x01eb
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r5 = r12
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = (org.telegram.tgnet.TLRPC.EncryptedChat) r5
            int r5 = r5.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r7 = r4.getEncryptedChat(r5)
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r5 = r7.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r4.getUser(r5)
            r4 = r0
            r5 = r18
            r18 = r7
            goto L_0x01f0
        L_0x01eb:
            r4 = r0
            r5 = r18
            r18 = r7
        L_0x01f0:
            boolean r0 = r30.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x0207
            r10 = 1
            int r0 = r30.getItemCount()
            r6 = 1
            int r0 = r0 - r6
            if (r3 == r0) goto L_0x0201
            r0 = 1
            goto L_0x0202
        L_0x0201:
            r0 = 0
        L_0x0202:
            r13.useSeparator = r0
            r0 = r10
            goto L_0x036d
        L_0x0207:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r6 = r0.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r7 = r0.getPhoneSearch()
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r19 = r0.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r0 = r0.getLocalServerSearch()
            int r20 = r0.size()
            int r21 = r7.size()
            r0 = r21
            if (r21 <= 0) goto L_0x0239
            int r2 = r21 + -1
            java.lang.Object r2 = r7.get(r2)
            boolean r2 = r2 instanceof java.lang.String
            if (r2 == 0) goto L_0x0239
            int r0 = r0 + -2
            r2 = r0
            goto L_0x023a
        L_0x0239:
            r2 = r0
        L_0x023a:
            boolean r0 = r6.isEmpty()
            if (r0 == 0) goto L_0x0244
            r0 = 0
            r17 = 1
            goto L_0x024c
        L_0x0244:
            int r0 = r6.size()
            r17 = 1
            int r0 = r0 + 1
        L_0x024c:
            r22 = r0
            int r0 = r30.getItemCount()
            int r0 = r0 + -1
            if (r3 == r0) goto L_0x026a
            int r0 = r19 + r2
            int r0 = r0 + r20
            int r0 = r0 + -1
            if (r3 == r0) goto L_0x026a
            int r0 = r19 + r22
            int r0 = r0 + r21
            int r0 = r0 + r20
            int r0 = r0 + -1
            if (r3 == r0) goto L_0x026a
            r0 = 1
            goto L_0x026b
        L_0x026a:
            r0 = 0
        L_0x026b:
            r13.useSeparator = r0
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r0 = r0.size()
            r23 = r2
            java.lang.String r2 = "@"
            if (r3 >= r0) goto L_0x02bd
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r3)
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            if (r0 == 0) goto L_0x02b6
            if (r4 == 0) goto L_0x02b6
            java.lang.String r9 = r4.username
            if (r9 == 0) goto L_0x02b6
            java.lang.String r9 = r4.username
            int r9 = r9.length()
            if (r9 <= 0) goto L_0x02b6
            java.lang.String r9 = r0.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r2 = r4.username
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            boolean r2 = r9.startsWith(r2)
            if (r2 == 0) goto L_0x02b6
            r2 = r0
            r0 = 0
            r9 = r0
            r8 = r2
            r25 = r6
            r26 = r7
            goto L_0x0368
        L_0x02b6:
            r9 = r0
            r25 = r6
            r26 = r7
            goto L_0x0368
        L_0x02bd:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 != 0) goto L_0x0360
            r3 = 0
            if (r4 == 0) goto L_0x02d9
            r24 = r3
            java.lang.String r3 = r4.first_name
            r25 = r6
            java.lang.String r6 = r4.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r3, r6)
            goto L_0x02e4
        L_0x02d9:
            r24 = r3
            r25 = r6
            if (r5 == 0) goto L_0x02e2
            java.lang.String r3 = r5.title
            goto L_0x02e4
        L_0x02e2:
            r3 = r24
        L_0x02e4:
            java.lang.String r6 = "windowBackgroundWhiteBlueText4"
            r26 = r7
            r7 = -1
            if (r3 == 0) goto L_0x0317
            r27 = r8
            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r3, r0)
            r28 = r8
            if (r8 == r7) goto L_0x0312
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>(r3)
            org.telegram.ui.Components.ForegroundColorSpanThemable r7 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            r7.<init>(r6)
            int r6 = r0.length()
            r8 = r28
            int r6 = r6 + r8
            r28 = r3
            r3 = 33
            r2.setSpan(r7, r8, r6, r3)
            r9 = r2
            r8 = r27
            goto L_0x0368
        L_0x0312:
            r8 = r28
            r28 = r3
            goto L_0x031b
        L_0x0317:
            r28 = r3
            r27 = r8
        L_0x031b:
            if (r11 == 0) goto L_0x0366
            boolean r3 = r0.startsWith(r2)
            if (r3 == 0) goto L_0x032a
            r3 = 1
            java.lang.String r0 = r0.substring(r3)
            r3 = r0
            goto L_0x032b
        L_0x032a:
            r3 = r0
        L_0x032b:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0359 }
            r0.<init>()     // Catch:{ Exception -> 0x0359 }
            r0.append(r2)     // Catch:{ Exception -> 0x0359 }
            r0.append(r11)     // Catch:{ Exception -> 0x0359 }
            int r2 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r11, r3)     // Catch:{ Exception -> 0x0359 }
            r8 = r2
            if (r2 == r7) goto L_0x0356
            int r2 = r3.length()     // Catch:{ Exception -> 0x0359 }
            if (r8 != 0) goto L_0x0346
            int r2 = r2 + 1
            goto L_0x0348
        L_0x0346:
            int r8 = r8 + 1
        L_0x0348:
            org.telegram.ui.Components.ForegroundColorSpanThemable r7 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0359 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x0359 }
            int r6 = r8 + r2
            r29 = r2
            r2 = 33
            r0.setSpan(r7, r8, r6, r2)     // Catch:{ Exception -> 0x0359 }
        L_0x0356:
            r8 = r0
            goto L_0x0368
        L_0x0359:
            r0 = move-exception
            r2 = r11
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = r2
            goto L_0x0368
        L_0x0360:
            r25 = r6
            r26 = r7
            r27 = r8
        L_0x0366:
            r8 = r27
        L_0x0368:
            r2 = 0
            r13.setChecked(r2, r2)
            r0 = r10
        L_0x036d:
            r2 = 0
            if (r4 == 0) goto L_0x0386
            long r6 = r4.id
            r10 = r2
            long r2 = r1.selfUserId
            int r19 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r19 != 0) goto L_0x0387
            r2 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r3 = "SavedMessages"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r8 = 0
            r2 = 1
            r3 = r9
            goto L_0x0389
        L_0x0386:
            r10 = r2
        L_0x0387:
            r3 = r9
            r2 = r10
        L_0x0389:
            if (r5 == 0) goto L_0x03e2
            int r6 = r5.participants_count
            if (r6 == 0) goto L_0x03e2
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r6 == 0) goto L_0x03a2
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x03a2
            int r6 = r5.participants_count
            java.lang.String r7 = "Subscribers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            goto L_0x03aa
        L_0x03a2:
            int r6 = r5.participants_count
            java.lang.String r7 = "Members"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
        L_0x03aa:
            boolean r7 = r8 instanceof android.text.SpannableStringBuilder
            java.lang.String r9 = ", "
            if (r7 == 0) goto L_0x03bf
            r7 = r8
            android.text.SpannableStringBuilder r7 = (android.text.SpannableStringBuilder) r7
            android.text.SpannableStringBuilder r7 = r7.append(r9)
            r7.append(r6)
            r16 = 0
            r17 = 1
            goto L_0x03e6
        L_0x03bf:
            boolean r7 = android.text.TextUtils.isEmpty(r8)
            if (r7 != 0) goto L_0x03da
            r7 = 3
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r7]
            r16 = 0
            r7[r16] = r8
            r17 = 1
            r7[r17] = r9
            r9 = 2
            r7[r9] = r6
            java.lang.CharSequence r8 = android.text.TextUtils.concat(r7)
            r19 = r8
            goto L_0x03e8
        L_0x03da:
            r16 = 0
            r17 = 1
            r8 = r6
            r19 = r8
            goto L_0x03e8
        L_0x03e2:
            r16 = 0
            r17 = 1
        L_0x03e6:
            r19 = r8
        L_0x03e8:
            if (r4 == 0) goto L_0x03ec
            r7 = r4
            goto L_0x03ed
        L_0x03ec:
            r7 = r5
        L_0x03ed:
            r6 = r13
            r8 = r18
            r9 = r3
            r10 = r19
            r20 = r11
            r11 = r0
            r21 = r12
            r12 = r2
            r6.setData(r7, r8, r9, r10, r11, r12)
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogsSearchAdapterDelegate r6 = r1.delegate
            long r7 = r13.getDialogId()
            boolean r6 = r6.isSelected(r7)
            long r7 = r13.getDialogId()
            int r9 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0410
            r7 = 1
            goto L_0x0411
        L_0x0410:
            r7 = 0
        L_0x0411:
            r13.setChecked(r6, r7)
        L_0x0415:
            r3 = r32
        L_0x0417:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    /* renamed from: lambda$onBindViewHolder$17$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1357x6020530b(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$18$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1358xd59a794c(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public int getItemViewType(int i) {
        if (isRecentSearchDisplayed()) {
            int offset = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty();
            if (i < offset) {
                return 5;
            }
            return i == offset ? 1 : 0;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int localCount = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < localCount) {
                return 0;
            }
            int i2 = i - localCount;
            if (i2 >= 0 && i2 < localServerCount) {
                return 0;
            }
            int i3 = i2 - localServerCount;
            if (i3 < 0 || i3 >= phoneCount) {
                int i4 = i3 - phoneCount;
                if (i4 >= 0 && i4 < globalCount) {
                    return i4 == 0 ? 1 : 0;
                }
                int i5 = i4 - globalCount;
                if (i5 < 0 || i5 >= messagesCount) {
                    return 3;
                }
                if (i5 == 0) {
                    return 1;
                }
                return 2;
            }
            Object object = getItem(i3);
            if (!(object instanceof String)) {
                return 0;
            }
            if ("section".equals((String) object)) {
                return 1;
            }
            return 6;
        } else if (i == 0) {
            return 1;
        } else {
            return 4;
        }
    }

    public void setFiltersDelegate(FilteredSearchView.Delegate filtersDelegate2, boolean update) {
        this.filtersDelegate = filtersDelegate2;
        if (filtersDelegate2 != null && update) {
            filtersDelegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    public int getCurrentItemCount() {
        return this.currentItemCount;
    }
}
