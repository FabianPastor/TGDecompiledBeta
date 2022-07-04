package org.telegram.ui.Adapters;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LayoutAnimationController;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import org.telegram.ui.ActionBar.Theme;
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
    private final int VIEW_TYPE_ADD_BY_PHONE = 6;
    private final int VIEW_TYPE_CATEGORY_LIST = 5;
    private final int VIEW_TYPE_DIALOG_CELL = 2;
    private final int VIEW_TYPE_GRAY_SECTION = 1;
    private final int VIEW_TYPE_HASHTAG_CELL = 4;
    private final int VIEW_TYPE_LOADING = 3;
    private final int VIEW_TYPE_PROFILE_CELL = 0;
    private Runnable cancelShowMoreAnimation;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentItemCount;
    private String currentMessagesQuery;
    /* access modifiers changed from: private */
    public DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private String filteredRecentQuery = null;
    private ArrayList<RecentSearchObject> filteredRecentSearchObjects = new ArrayList<>();
    private FilteredSearchView.Delegate filtersDelegate;
    private int folderId;
    boolean globalSearchCollapsed = true;
    private RecyclerListView innerListView;
    private DefaultItemAnimator itemAnimator;
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
    private long lastShowMoreUpdate;
    private boolean localTipArchive;
    private ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    boolean phoneCollapsed = true;
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
    public boolean showMoreAnimation = false;
    public View showMoreHeader;
    public int showMoreLastItem;
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
        private boolean forceDarkTheme;
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
            HintDialogCell cell = new HintDialogCell(this.mContext, this.drawChecked);
            cell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(cell);
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

    public DialogsSearchAdapter(Context context, int messagesSearch, int type, DefaultItemAnimator itemAnimator2) {
        this.itemAnimator = itemAnimator2;
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
                this.filteredRecentQuery = null;
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                return;
            }
            filterRecent(query);
            this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
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
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new DialogsSearchAdapter$$ExternalSyntheticLambda13(this, query, currentReqId, searchId, req), 2);
        }
    }

    /* renamed from: lambda$searchMessagesInternal$1$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2623xbfab1706(String query, int currentReqId, int searchId, TLRPC.TL_messages_searchGlobal req, TLObject response, TLRPC.TL_error error) {
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
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda23(this, currentReqId, searchId, error, query, response, req, messageObjects));
    }

    /* renamed from: lambda$searchMessagesInternal$0$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2622x4a30f0c5(int currentReqId, int searchId, TLRPC.TL_error error, String query, TLObject response, TLRPC.TL_messages_searchGlobal req, ArrayList messageObjects) {
        boolean z;
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
                this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    z = true;
                    dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                    this.delegate.runResultsEnterAnimation();
                } else {
                    z = true;
                }
                this.globalSearchCollapsed = z;
                this.phoneCollapsed = z;
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
        return (i == 2 || i == 4 || i == 5 || i == 6 || i == 11 || getRecentItemsCount() <= 0) ? false : true;
    }

    public boolean isSearchWas() {
        return this.searchWas;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && hasRecentSearch();
    }

    public void loadRecentSearch() {
        loadRecentSearch(this.currentAccount, this.dialogsType, new DialogsSearchAdapter$$ExternalSyntheticLambda14(this));
    }

    public static void loadRecentSearch(int currentAccount2, int dialogsType2, OnRecentSearchLoaded callback) {
        MessagesStorage.getInstance(currentAccount2).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda19(currentAccount2, dialogsType2, callback));
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
            Collections.sort(arrayList, DialogsSearchAdapter$$ExternalSyntheticLambda10.INSTANCE);
            try {
                AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda20(callback, arrayList, hashMap));
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda2(this, did));
    }

    /* renamed from: lambda$putRecentSearch$6$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2616x2f7d5292(long did) {
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
        StringBuilder queryFilter = null;
        if (this.searchWas) {
            while (this.filteredRecentSearchObjects.size() > 0) {
                RecentSearchObject obj = this.filteredRecentSearchObjects.remove(0);
                this.recentSearchObjects.remove(obj);
                this.recentSearchObjectsById.remove(obj.did);
                if (queryFilter == null) {
                    queryFilter = new StringBuilder("did IN (");
                    queryFilter.append(obj.did);
                } else {
                    queryFilter.append(", ");
                    queryFilter.append(obj.did);
                }
            }
            if (queryFilter == null) {
                queryFilter = new StringBuilder("1");
            } else {
                queryFilter.append(")");
            }
        } else {
            this.filteredRecentSearchObjects.clear();
            this.recentSearchObjects.clear();
            this.recentSearchObjectsById.clear();
            queryFilter = new StringBuilder("1");
        }
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda7(this, queryFilter));
    }

    /* renamed from: lambda$clearRecentSearch$7$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2605x988c9e51(StringBuilder finalQueryFilter) {
        try {
            finalQueryFilter.insert(0, "DELETE FROM search_recent WHERE ");
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(finalQueryFilter.toString()).stepThis().dispose();
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda3(this, did));
        }
    }

    /* renamed from: lambda$removeRecentSearch$8$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2617xfb99f9e3(long did) {
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
    public void m2606xa34cacff(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> hashMap) {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda6(this, q, searchId, query));
        }
    }

    /* renamed from: lambda$searchDialogsInternal$10$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2620x16ee3c0b(String q, int searchId, String query) {
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
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda21(this));
    }

    /* renamed from: lambda$searchDialogsInternal$9$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2621xd91b03c3() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(ArrayList<Object> result, ArrayList<CharSequence> names, ArrayList<TLRPC.User> encUsers, int searchId) {
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda1(this, searchId, result, names, encUsers));
    }

    /* renamed from: lambda$updateSearchResults$12$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2625xbfa838c8(int searchId, ArrayList result, ArrayList names, ArrayList encUsers) {
        int i = searchId;
        ArrayList arrayList = result;
        ArrayList arrayList2 = names;
        this.waitingResponseCount--;
        if (i == this.lastSearchId) {
            this.lastLocalSearchId = i;
            if (this.lastGlobalSearchId != i) {
                this.searchAdapterHelper.clear();
            }
            if (this.lastMessagesSearchId != i) {
                this.searchResultMessages.clear();
            }
            this.searchWas = true;
            int recentCount = this.filteredRecentSearchObjects.size();
            int a = 0;
            while (a < result.size()) {
                Object obj = arrayList.get(a);
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
                    MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(dialogId, new DialogsSearchAdapter$$ExternalSyntheticLambda12(this, dialogId, obj));
                }
                boolean foundInRecent = false;
                int j = 0;
                while (true) {
                    if (j < recentCount) {
                        RecentSearchObject o = this.filteredRecentSearchObjects.get(j);
                        if (o != null && o.did == dialogId) {
                            foundInRecent = true;
                            break;
                        }
                        j++;
                    } else {
                        break;
                    }
                }
                if (foundInRecent) {
                    arrayList.remove(a);
                    arrayList2.remove(a);
                    a--;
                }
                a++;
            }
            MessagesController.getInstance(this.currentAccount).putUsers(encUsers, true);
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList, this.filteredRecentSearchObjects);
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
            if (dialogsSearchAdapterDelegate != null) {
                dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                this.delegate.runResultsEnterAnimation();
            }
        }
    }

    /* renamed from: lambda$updateSearchResults$11$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2624x4a2e1287(long finalDialogId, Object obj, int param) {
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
                this.filteredRecentQuery = null;
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null, (ArrayList<RecentSearchObject>) null);
                SearchAdapterHelper searchAdapterHelper2 = this.searchAdapterHelper;
                int i2 = this.dialogsType;
                searchAdapterHelper2.queryServerSearch((String) null, true, true, i2 != 11, i2 != 11, i2 == 2 || i2 == 11, 0, i2 == 0, 0, 0);
                this.searchWas = false;
                this.lastSearchId = 0;
                this.waitingResponseCount = 0;
                this.globalSearchCollapsed = true;
                this.phoneCollapsed = true;
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
            filterRecent(query);
            this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
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
                    this.globalSearchCollapsed = true;
                    this.phoneCollapsed = true;
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
            this.globalSearchCollapsed = true;
            this.phoneCollapsed = true;
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate3 = this.delegate;
            if (dialogsSearchAdapterDelegate3 != null) {
                dialogsSearchAdapterDelegate3.searchStateChanged(true, false);
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            DialogsSearchAdapter$$ExternalSyntheticLambda5 dialogsSearchAdapter$$ExternalSyntheticLambda5 = new DialogsSearchAdapter$$ExternalSyntheticLambda5(this, query, searchId, str);
            this.searchRunnable = dialogsSearchAdapter$$ExternalSyntheticLambda5;
            dispatchQueue.postRunnable(dialogsSearchAdapter$$ExternalSyntheticLambda5, 300);
        }
    }

    /* renamed from: lambda$searchDialogs$14$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2619x4783704c(String query, int searchId, String text) {
        this.searchRunnable = null;
        searchDialogsInternal(query, searchId);
        DialogsSearchAdapter$$ExternalSyntheticLambda24 dialogsSearchAdapter$$ExternalSyntheticLambda24 = new DialogsSearchAdapter$$ExternalSyntheticLambda24(this, searchId, query, text);
        this.searchRunnable2 = dialogsSearchAdapter$$ExternalSyntheticLambda24;
        AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda24);
    }

    /* renamed from: lambda$searchDialogs$13$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2618xd2094a0b(int searchId, String query, String text) {
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

    public int getRecentItemsCount() {
        ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        int i = 1;
        int size = !recent.isEmpty() ? recent.size() + 1 : 0;
        if (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
            i = 0;
        }
        return size + i;
    }

    public int getRecentResultsCount() {
        ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        if (recent != null) {
            return recent.size();
        }
        return 0;
    }

    public int getItemCount() {
        if (this.waitingResponseCount == 3) {
            return 0;
        }
        int count = 0;
        if (!this.searchResultHashtags.isEmpty()) {
            return 0 + this.searchResultHashtags.size() + 1;
        }
        if (isRecentSearchDisplayed()) {
            count = 0 + getRecentItemsCount();
            if (!this.searchWas) {
                return count;
            }
        }
        int resultsCount = this.searchResult.size();
        int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
        int count2 = count + resultsCount + localServerCount;
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount > 3 && this.globalSearchCollapsed) {
            globalCount = 3;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount > 3 && this.phoneCollapsed) {
            phoneCount = 3;
        }
        int messagesCount = this.searchResultMessages.size();
        if (resultsCount + localServerCount > 0 && getRecentItemsCount() > 0) {
            count2++;
        }
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

    public Object getItem(int i) {
        TLObject chat;
        if (this.searchResultHashtags.isEmpty()) {
            int messagesCount = 0;
            if (isRecentSearchDisplayed()) {
                int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
                ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
                if (i <= offset || (i - 1) - offset >= recent.size()) {
                    i -= getRecentItemsCount();
                } else {
                    TLObject object = recent.get((i - 1) - offset).object;
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
                }
            }
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
            int localCount = this.searchResult.size();
            int localServerCount = localServerSearch.size();
            if (localCount + localServerCount > 0 && getRecentItemsCount() > 0) {
                if (i == 0) {
                    return null;
                }
                i--;
            }
            int phoneCount = phoneSearch.size();
            if (phoneCount > 3 && this.phoneCollapsed) {
                phoneCount = 3;
            }
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (globalCount > 4 && this.globalSearchCollapsed) {
                globalCount = 4;
            }
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
        if (!this.searchWas || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        if (isRecentSearchDisplayed()) {
            int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
            ArrayList<RecentSearchObject> recent = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
            if (i > offset && (i - 1) - offset < recent.size()) {
                return false;
            }
            i -= getRecentItemsCount();
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int localCount = this.searchResult.size();
        int localServerCount = localServerSearch.size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount > 3 && this.phoneCollapsed) {
            phoneCount = 3;
        }
        int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (globalCount > 4 && this.globalSearchCollapsed) {
            globalCount = 4;
        }
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
                r0.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21"));
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
                r0.setOnItemClickListener((RecyclerListView.OnItemClickListener) new DialogsSearchAdapter$$ExternalSyntheticLambda15(this));
                r0.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new DialogsSearchAdapter$$ExternalSyntheticLambda16(this));
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
    public /* synthetic */ void m2614xCLASSNAMEb56a(View view1, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog(((Long) view1.getTag()).longValue());
        }
    }

    /* renamed from: lambda$onCreateViewHolder$16$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ boolean m2615x38f2dbab(View view12, int position) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Long) view12.getTag()).longValue());
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r28v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: boolean} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x045f  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0477  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0482  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x04d8  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x04df  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x04e1  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0506  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0508  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r33, int r34) {
        /*
            r32 = this;
            r1 = r32
            r2 = r33
            r0 = r34
            int r3 = r33.getItemViewType()
            r4 = 4
            java.lang.String r5 = "windowBackgroundWhite"
            r6 = 3
            r7 = 0
            r8 = 1
            switch(r3) {
                case 0: goto L_0x021e;
                case 1: goto L_0x00b2;
                case 2: goto L_0x0087;
                case 3: goto L_0x0013;
                case 4: goto L_0x0061;
                case 5: goto L_0x0050;
                case 6: goto L_0x0015;
                default: goto L_0x0013;
            }
        L_0x0013:
            goto L_0x050e
        L_0x0015:
            java.lang.Object r3 = r1.getItem(r0)
            java.lang.String r3 = (java.lang.String) r3
            android.view.View r4 = r2.itemView
            org.telegram.ui.Cells.TextCell r4 = (org.telegram.ui.Cells.TextCell) r4
            r5 = 0
            java.lang.String r6 = "windowBackgroundWhiteBlueText2"
            r4.setColors(r5, r6)
            r5 = 2131624260(0x7f0e0144, float:1.8875695E38)
            java.lang.Object[] r6 = new java.lang.Object[r8]
            org.telegram.PhoneFormat.PhoneFormat r8 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "+"
            r9.append(r10)
            r9.append(r3)
            java.lang.String r9 = r9.toString()
            java.lang.String r8 = r8.format(r9)
            r6[r7] = r8
            java.lang.String r8 = "AddContactByPhone"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r4.setText(r5, r7)
            goto L_0x050e
        L_0x0050:
            android.view.View r3 = r2.itemView
            org.telegram.ui.Components.RecyclerListView r3 = (org.telegram.ui.Components.RecyclerListView) r3
            androidx.recyclerview.widget.RecyclerView$Adapter r4 = r3.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r4 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r4
            int r5 = r0 / 2
            r4.setIndex(r5)
            goto L_0x050e
        L_0x0061:
            android.view.View r3 = r2.itemView
            org.telegram.ui.Cells.HashtagSearchCell r3 = (org.telegram.ui.Cells.HashtagSearchCell) r3
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setBackgroundColor(r4)
            java.util.ArrayList<java.lang.String> r4 = r1.searchResultHashtags
            int r5 = r0 + -1
            java.lang.Object r4 = r4.get(r5)
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            r3.setText(r4)
            java.util.ArrayList<java.lang.String> r4 = r1.searchResultHashtags
            int r4 = r4.size()
            if (r0 == r4) goto L_0x0082
            r7 = 1
        L_0x0082:
            r3.setNeedDivider(r7)
            goto L_0x050e
        L_0x0087:
            android.view.View r3 = r2.itemView
            org.telegram.ui.Cells.DialogCell r3 = (org.telegram.ui.Cells.DialogCell) r3
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setBackgroundColor(r4)
            int r4 = r32.getItemCount()
            int r4 = r4 - r8
            if (r0 == r4) goto L_0x009a
            r7 = 1
        L_0x009a:
            r3.useSeparator = r7
            java.lang.Object r4 = r1.getItem(r0)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            long r10 = r4.getDialogId()
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            int r13 = r5.date
            r14 = 0
            r9 = r3
            r12 = r4
            r9.setDialog(r10, r12, r13, r14)
            goto L_0x050e
        L_0x00b2:
            android.view.View r3 = r2.itemView
            org.telegram.ui.Cells.GraySectionCell r3 = (org.telegram.ui.Cells.GraySectionCell) r3
            java.util.ArrayList<java.lang.String> r5 = r1.searchResultHashtags
            boolean r5 = r5.isEmpty()
            r9 = 2131625132(0x7f0e04ac, float:1.8877463E38)
            java.lang.String r10 = "ClearButton"
            if (r5 != 0) goto L_0x00da
            r4 = 2131626129(0x7f0e0891, float:1.8879485E38)
            java.lang.String r5 = "Hashtags"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r9)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda11 r6 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda11
            r6.<init>(r1)
            r3.setText(r4, r5, r6)
            goto L_0x050e
        L_0x00da:
            r5 = r34
            boolean r11 = r32.isRecentSearchDisplayed()
            if (r11 == 0) goto L_0x0150
            boolean r11 = r1.searchWas
            if (r11 != 0) goto L_0x00f6
            int r11 = r1.currentAccount
            org.telegram.messenger.MediaDataController r11 = org.telegram.messenger.MediaDataController.getInstance(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r11 = r11.hints
            boolean r11 = r11.isEmpty()
            if (r11 != 0) goto L_0x00f6
            r11 = 1
            goto L_0x00f7
        L_0x00f6:
            r11 = 0
        L_0x00f7:
            if (r0 >= r11) goto L_0x0106
            r4 = 2131625008(0x7f0e0430, float:1.8877212E38)
            java.lang.String r6 = "ChatHints"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            return
        L_0x0106:
            if (r0 != r11) goto L_0x0138
            boolean r4 = r1.searchWas
            r6 = 2131627858(0x7f0e0var_, float:1.8882992E38)
            java.lang.String r7 = "Recent"
            if (r4 != 0) goto L_0x0122
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r9)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda17 r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda17
            r7.<init>(r1)
            r3.setText(r4, r6, r7)
            goto L_0x0137
        L_0x0122:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r6 = 2131625131(0x7f0e04ab, float:1.8877461E38)
            java.lang.String r7 = "Clear"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda18 r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda18
            r7.<init>(r1)
            r3.setText(r4, r6, r7)
        L_0x0137:
            return
        L_0x0138:
            int r9 = r32.getRecentItemsCount()
            if (r0 != r9) goto L_0x014b
            r4 = 2131628093(0x7f0e103d, float:1.8883469E38)
            java.lang.String r6 = "SearchAllChatsShort"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            return
        L_0x014b:
            int r9 = r32.getRecentItemsCount()
            int r0 = r0 - r9
        L_0x0150:
            org.telegram.ui.Adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r9 = r9.getGlobalSearch()
            java.util.ArrayList<java.lang.Object> r10 = r1.searchResult
            int r10 = r10.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r11 = r1.searchAdapterHelper
            java.util.ArrayList r11 = r11.getLocalServerSearch()
            int r11 = r11.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
            java.util.ArrayList r12 = r12.getPhoneSearch()
            int r12 = r12.size()
            if (r12 <= r6) goto L_0x0177
            boolean r13 = r1.phoneCollapsed
            if (r13 == 0) goto L_0x0177
            r12 = 3
        L_0x0177:
            boolean r13 = r9.isEmpty()
            if (r13 == 0) goto L_0x017f
            r13 = 0
            goto L_0x0184
        L_0x017f:
            int r13 = r9.size()
            int r13 = r13 + r8
        L_0x0184:
            if (r13 <= r4) goto L_0x018b
            boolean r4 = r1.globalSearchCollapsed
            if (r4 == 0) goto L_0x018b
            r13 = 4
        L_0x018b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.searchResultMessages
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x0194
            goto L_0x019c
        L_0x0194:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.searchResultMessages
            int r4 = r4.size()
            int r7 = r4 + 1
        L_0x019c:
            r4 = r7
            int r7 = r10 + r11
            int r0 = r0 - r7
            r7 = 0
            r8 = 0
            if (r0 < 0) goto L_0x01c4
            if (r0 >= r12) goto L_0x01c4
            r14 = 2131627500(0x7f0e0dec, float:1.8882266E38)
            java.lang.String r15 = "PhoneNumberSearch"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.Adapters.SearchAdapterHelper r15 = r1.searchAdapterHelper
            java.util.ArrayList r15 = r15.getPhoneSearch()
            int r15 = r15.size()
            if (r15 <= r6) goto L_0x01f0
            boolean r7 = r1.phoneCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda9 r6 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda9
            r6.<init>(r1, r3)
            r8 = r6
            goto L_0x01f0
        L_0x01c4:
            int r0 = r0 - r12
            if (r0 < 0) goto L_0x01e7
            if (r0 >= r13) goto L_0x01e7
            r14 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.String r15 = "GlobalSearch"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            org.telegram.ui.Adapters.SearchAdapterHelper r15 = r1.searchAdapterHelper
            java.util.ArrayList r15 = r15.getGlobalSearch()
            int r15 = r15.size()
            if (r15 <= r6) goto L_0x01f0
            boolean r7 = r1.globalSearchCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda8 r6 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda8
            r6.<init>(r1, r9, r5, r3)
            r8 = r6
            goto L_0x01f0
        L_0x01e7:
            r6 = 2131628119(0x7f0e1057, float:1.8883522E38)
            java.lang.String r14 = "SearchMessages"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r6)
        L_0x01f0:
            if (r8 != 0) goto L_0x01f8
            r3.setText(r14)
            r34 = r0
            goto L_0x021a
        L_0x01f8:
            r6 = r8
            if (r7 == 0) goto L_0x0207
            r15 = 2131628340(0x7f0e1134, float:1.888397E38)
            r34 = r0
            java.lang.String r0 = "ShowMore"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r15)
            goto L_0x0212
        L_0x0207:
            r34 = r0
            r0 = 2131628339(0x7f0e1133, float:1.8883968E38)
            java.lang.String r15 = "ShowLess"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
        L_0x0212:
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0 r15 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0
            r15.<init>(r6)
            r3.setText(r14, r0, r15)
        L_0x021a:
            r0 = r34
            goto L_0x050e
        L_0x021e:
            android.view.View r3 = r2.itemView
            org.telegram.ui.Cells.ProfileSearchCell r3 = (org.telegram.ui.Cells.ProfileSearchCell) r3
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setBackgroundColor(r5)
            long r16 = r3.getDialogId()
            r5 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            java.lang.Object r15 = r1.getItem(r0)
            boolean r7 = r15 instanceof org.telegram.tgnet.TLRPC.User
            if (r7 == 0) goto L_0x0245
            r5 = r15
            org.telegram.tgnet.TLRPC$User r5 = (org.telegram.tgnet.TLRPC.User) r5
            java.lang.String r14 = r5.username
            r4 = r9
            r7 = r10
            r8 = r14
            goto L_0x029e
        L_0x0245:
            boolean r7 = r15 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r7 == 0) goto L_0x026c
            int r7 = r1.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            r4 = r15
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            r21 = r9
            long r8 = r4.id
            java.lang.Long r4 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r4 = r7.getChat(r4)
            if (r4 != 0) goto L_0x0265
            r4 = r15
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            r9 = r4
            goto L_0x0266
        L_0x0265:
            r9 = r4
        L_0x0266:
            java.lang.String r14 = r9.username
            r4 = r9
            r7 = r10
            r8 = r14
            goto L_0x029e
        L_0x026c:
            r21 = r9
            boolean r4 = r15 instanceof org.telegram.tgnet.TLRPC.EncryptedChat
            if (r4 == 0) goto L_0x029a
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r7 = r15
            org.telegram.tgnet.TLRPC$EncryptedChat r7 = (org.telegram.tgnet.TLRPC.EncryptedChat) r7
            int r7 = r7.id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r4.getEncryptedChat(r7)
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r10.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r4.getUser(r7)
            r7 = r10
            r8 = r14
            r4 = r21
            goto L_0x029e
        L_0x029a:
            r7 = r10
            r8 = r14
            r4 = r21
        L_0x029e:
            boolean r9 = r32.isRecentSearchDisplayed()
            if (r9 == 0) goto L_0x02c0
            int r9 = r32.getRecentItemsCount()
            if (r0 >= r9) goto L_0x02b8
            int r9 = r32.getRecentItemsCount()
            r10 = 1
            int r9 = r9 - r10
            if (r0 == r9) goto L_0x02b4
            r9 = 1
            goto L_0x02b5
        L_0x02b4:
            r9 = 0
        L_0x02b5:
            r3.useSeparator = r9
            r13 = 1
        L_0x02b8:
            int r9 = r32.getRecentItemsCount()
            int r0 = r0 - r9
            r21 = r13
            goto L_0x02c2
        L_0x02c0:
            r21 = r13
        L_0x02c2:
            org.telegram.ui.Adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r22 = r9.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r14 = r9.getPhoneSearch()
            java.util.ArrayList<java.lang.Object> r9 = r1.searchResult
            int r23 = r9.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r9 = r1.searchAdapterHelper
            java.util.ArrayList r9 = r9.getLocalServerSearch()
            int r24 = r9.size()
            int r9 = r23 + r24
            if (r9 <= 0) goto L_0x02ec
            int r9 = r32.getRecentItemsCount()
            if (r9 <= 0) goto L_0x02ec
            int r0 = r0 + -1
            r13 = r0
            goto L_0x02ed
        L_0x02ec:
            r13 = r0
        L_0x02ed:
            int r0 = r14.size()
            if (r0 <= r6) goto L_0x02fb
            boolean r9 = r1.phoneCollapsed
            if (r9 == 0) goto L_0x02fb
            r0 = 3
            r25 = r0
            goto L_0x02fd
        L_0x02fb:
            r25 = r0
        L_0x02fd:
            r0 = r25
            if (r25 <= 0) goto L_0x0310
            int r9 = r25 + -1
            java.lang.Object r9 = r14.get(r9)
            boolean r9 = r9 instanceof java.lang.String
            if (r9 == 0) goto L_0x0310
            int r0 = r0 + -2
            r26 = r0
            goto L_0x0312
        L_0x0310:
            r26 = r0
        L_0x0312:
            boolean r0 = r22.isEmpty()
            if (r0 == 0) goto L_0x031a
            r0 = 0
            goto L_0x0320
        L_0x031a:
            int r0 = r22.size()
            r9 = 1
            int r0 = r0 + r9
        L_0x0320:
            r9 = 4
            if (r0 <= r9) goto L_0x032b
            boolean r9 = r1.globalSearchCollapsed
            if (r9 == 0) goto L_0x032b
            r0 = 4
            r19 = r0
            goto L_0x032d
        L_0x032b:
            r19 = r0
        L_0x032d:
            if (r21 != 0) goto L_0x0351
            int r0 = r32.getItemCount()
            int r9 = r32.getRecentItemsCount()
            int r0 = r0 - r9
            r9 = 1
            int r0 = r0 - r9
            if (r13 == r0) goto L_0x034e
            int r0 = r23 + r26
            int r0 = r0 + r24
            int r0 = r0 - r9
            if (r13 == r0) goto L_0x034e
            int r0 = r23 + r19
            int r0 = r0 + r25
            int r0 = r0 + r24
            int r0 = r0 - r9
            if (r13 == r0) goto L_0x034e
            r0 = 1
            goto L_0x034f
        L_0x034e:
            r0 = 0
        L_0x034f:
            r3.useSeparator = r0
        L_0x0351:
            java.lang.String r0 = "@"
            if (r13 < 0) goto L_0x0395
            java.util.ArrayList<java.lang.Object> r9 = r1.searchResult
            int r9 = r9.size()
            if (r13 >= r9) goto L_0x0395
            if (r5 != 0) goto L_0x0395
            java.util.ArrayList<java.lang.CharSequence> r9 = r1.searchResultNames
            java.lang.Object r9 = r9.get(r13)
            r12 = r9
            java.lang.CharSequence r12 = (java.lang.CharSequence) r12
            if (r12 == 0) goto L_0x0395
            if (r5 == 0) goto L_0x0395
            java.lang.String r9 = r5.username
            if (r9 == 0) goto L_0x0395
            java.lang.String r9 = r5.username
            int r9 = r9.length()
            if (r9 <= 0) goto L_0x0395
            java.lang.String r9 = r12.toString()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r0)
            java.lang.String r6 = r5.username
            r10.append(r6)
            java.lang.String r6 = r10.toString()
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x0395
            r11 = r12
            r12 = 0
        L_0x0395:
            if (r11 != 0) goto L_0x044e
            if (r21 == 0) goto L_0x039c
            java.lang.String r6 = r1.filteredRecentQuery
            goto L_0x03a2
        L_0x039c:
            org.telegram.ui.Adapters.SearchAdapterHelper r6 = r1.searchAdapterHelper
            java.lang.String r6 = r6.getLastFoundUsername()
        L_0x03a2:
            boolean r9 = android.text.TextUtils.isEmpty(r6)
            if (r9 != 0) goto L_0x0447
            r9 = 0
            if (r5 == 0) goto L_0x03b4
            java.lang.String r10 = r5.first_name
            java.lang.String r2 = r5.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r10, r2)
            goto L_0x03b8
        L_0x03b4:
            if (r4 == 0) goto L_0x03b8
            java.lang.String r9 = r4.title
        L_0x03b8:
            java.lang.String r10 = "windowBackgroundWhiteBlueText4"
            r2 = -1
            if (r9 == 0) goto L_0x03ef
            r28 = r11
            int r11 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r6)
            r29 = r11
            if (r11 == r2) goto L_0x03e6
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r11.<init>(r9)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            r2.<init>(r10)
            int r30 = r6.length()
            r31 = r9
            r9 = r29
            r29 = r12
            int r12 = r9 + r30
            r30 = r13
            r13 = 33
            r11.setSpan(r2, r9, r12, r13)
            r12 = r11
            goto L_0x03f9
        L_0x03e6:
            r31 = r9
            r30 = r13
            r9 = r29
            r29 = r12
            goto L_0x03f7
        L_0x03ef:
            r31 = r9
            r28 = r11
            r29 = r12
            r30 = r13
        L_0x03f7:
            r12 = r29
        L_0x03f9:
            if (r8 == 0) goto L_0x0444
            if (r5 != 0) goto L_0x0444
            boolean r2 = r6.startsWith(r0)
            if (r2 == 0) goto L_0x0408
            r2 = 1
            java.lang.String r6 = r6.substring(r2)
        L_0x0408:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x043e }
            r2.<init>()     // Catch:{ Exception -> 0x043e }
            r2.append(r0)     // Catch:{ Exception -> 0x043e }
            r2.append(r8)     // Catch:{ Exception -> 0x043e }
            int r0 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r6)     // Catch:{ Exception -> 0x043e }
            r9 = r0
            r11 = -1
            if (r0 == r11) goto L_0x041d
            r0 = 1
            goto L_0x041e
        L_0x041d:
            r0 = 0
        L_0x041e:
            if (r0 == 0) goto L_0x043a
            int r11 = r6.length()     // Catch:{ Exception -> 0x043e }
            if (r9 != 0) goto L_0x0429
            int r11 = r11 + 1
            goto L_0x042b
        L_0x0429:
            int r9 = r9 + 1
        L_0x042b:
            org.telegram.ui.Components.ForegroundColorSpanThemable r13 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x043e }
            r13.<init>(r10)     // Catch:{ Exception -> 0x043e }
            int r10 = r9 + r11
            r29 = r0
            r0 = 33
            r2.setSpan(r13, r9, r10, r0)     // Catch:{ Exception -> 0x043e }
            goto L_0x043c
        L_0x043a:
            r29 = r0
        L_0x043c:
            r11 = r2
            goto L_0x0458
        L_0x043e:
            r0 = move-exception
            r11 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0458
        L_0x0444:
            r11 = r28
            goto L_0x0458
        L_0x0447:
            r28 = r11
            r29 = r12
            r30 = r13
            goto L_0x0454
        L_0x044e:
            r28 = r11
            r29 = r12
            r30 = r13
        L_0x0454:
            r11 = r28
            r12 = r29
        L_0x0458:
            r2 = 0
            r3.setChecked(r2, r2)
            r0 = 0
            if (r5 == 0) goto L_0x0477
            long r9 = r5.id
            r34 = r11
            r2 = r12
            long r11 = r1.selfUserId
            int r6 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x047a
            r6 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11 = 0
            r0 = 1
            r2 = r12
            goto L_0x047c
        L_0x0477:
            r34 = r11
            r2 = r12
        L_0x047a:
            r11 = r34
        L_0x047c:
            if (r4 == 0) goto L_0x04d8
            int r6 = r4.participants_count
            if (r6 == 0) goto L_0x04d8
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r6 == 0) goto L_0x0498
            boolean r6 = r4.megagroup
            if (r6 != 0) goto L_0x0498
            int r6 = r4.participants_count
            r9 = 0
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.String r12 = "Subscribers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r12, r6, r10)
            goto L_0x04a3
        L_0x0498:
            r9 = 0
            int r6 = r4.participants_count
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.String r9 = "Members"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r9, r6, r10)
        L_0x04a3:
            boolean r9 = r11 instanceof android.text.SpannableStringBuilder
            java.lang.String r10 = ", "
            if (r9 == 0) goto L_0x04b8
            r9 = r11
            android.text.SpannableStringBuilder r9 = (android.text.SpannableStringBuilder) r9
            android.text.SpannableStringBuilder r9 = r9.append(r10)
            r9.append(r6)
            r18 = 0
            r20 = 1
            goto L_0x04dc
        L_0x04b8:
            boolean r9 = android.text.TextUtils.isEmpty(r11)
            if (r9 != 0) goto L_0x04d2
            r9 = 3
            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r9]
            r18 = 0
            r9[r18] = r11
            r20 = 1
            r9[r20] = r10
            r10 = 2
            r9[r10] = r6
            java.lang.CharSequence r11 = android.text.TextUtils.concat(r9)
            r6 = r11
            goto L_0x04dd
        L_0x04d2:
            r18 = 0
            r20 = 1
            r11 = r6
            goto L_0x04dd
        L_0x04d8:
            r18 = 0
            r20 = 1
        L_0x04dc:
            r6 = r11
        L_0x04dd:
            if (r5 == 0) goto L_0x04e1
            r10 = r5
            goto L_0x04e2
        L_0x04e1:
            r10 = r4
        L_0x04e2:
            r27 = 1
            r9 = r3
            r11 = r7
            r12 = r2
            r28 = r30
            r13 = r6
            r29 = r14
            r14 = r27
            r27 = r15
            r15 = r0
            r9.setData(r10, r11, r12, r13, r14, r15)
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogsSearchAdapterDelegate r9 = r1.delegate
            long r10 = r3.getDialogId()
            boolean r9 = r9.isSelected(r10)
            long r10 = r3.getDialogId()
            int r12 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0508
            r10 = 1
            goto L_0x0509
        L_0x0508:
            r10 = 0
        L_0x0509:
            r3.setChecked(r9, r10)
            r0 = r28
        L_0x050e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    /* renamed from: lambda$onBindViewHolder$17$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2607x6020530b(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$18$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2608xd59a794c(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$19$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2609x4b149f8d(View v) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* renamed from: lambda$onBindViewHolder$20$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2610x6393e923(GraySectionCell cell) {
        String str;
        int i;
        boolean z = !this.phoneCollapsed;
        this.phoneCollapsed = z;
        if (z) {
            i = NUM;
            str = "ShowMore";
        } else {
            i = NUM;
            str = "ShowLess";
        }
        cell.setRightText(LocaleController.getString(str, i));
        notifyDataSetChanged();
    }

    /* renamed from: lambda$onBindViewHolder$23$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2613xCLASSNAMEbe6(ArrayList globalSearch, int rawPosition, GraySectionCell cell) {
        String str;
        int i;
        long now = SystemClock.elapsedRealtime();
        if (now - this.lastShowMoreUpdate >= 300) {
            this.lastShowMoreUpdate = now;
            int totalGlobalCount = globalSearch.isEmpty() ? 0 : globalSearch.size();
            boolean disableRemoveAnimation = getItemCount() > (Math.min(totalGlobalCount, this.globalSearchCollapsed ? 4 : Integer.MAX_VALUE) + rawPosition) + 1;
            DefaultItemAnimator defaultItemAnimator = this.itemAnimator;
            if (defaultItemAnimator != null) {
                long j = 200;
                defaultItemAnimator.setAddDuration(disableRemoveAnimation ? 45 : 200);
                DefaultItemAnimator defaultItemAnimator2 = this.itemAnimator;
                if (disableRemoveAnimation) {
                    j = 80;
                }
                defaultItemAnimator2.setRemoveDuration(j);
                this.itemAnimator.setRemoveDelay(disableRemoveAnimation ? 270 : 0);
            }
            boolean z = !this.globalSearchCollapsed;
            this.globalSearchCollapsed = z;
            if (z) {
                i = NUM;
                str = "ShowMore";
            } else {
                i = NUM;
                str = "ShowLess";
            }
            cell.setRightText(LocaleController.getString(str, i), this.globalSearchCollapsed);
            this.showMoreHeader = null;
            View parent = (View) cell.getParent();
            if (parent instanceof RecyclerView) {
                RecyclerView listView = (RecyclerView) parent;
                int nextGraySectionPosition = !this.globalSearchCollapsed ? rawPosition + 4 : rawPosition + totalGlobalCount + 1;
                int i2 = 0;
                while (true) {
                    if (i2 >= listView.getChildCount()) {
                        break;
                    }
                    View child = listView.getChildAt(i2);
                    if (listView.getChildAdapterPosition(child) == nextGraySectionPosition) {
                        this.showMoreHeader = child;
                        break;
                    }
                    i2++;
                }
            }
            if (!this.globalSearchCollapsed) {
                notifyItemChanged(rawPosition + 3);
                notifyItemRangeInserted(rawPosition + 4, totalGlobalCount - 3);
            } else {
                notifyItemRangeRemoved(rawPosition + 4, totalGlobalCount - 3);
                if (disableRemoveAnimation) {
                    AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda22(this, rawPosition), 350);
                } else {
                    notifyItemChanged(rawPosition + 3);
                }
            }
            Runnable runnable = this.cancelShowMoreAnimation;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (disableRemoveAnimation) {
                this.showMoreAnimation = true;
                DialogsSearchAdapter$$ExternalSyntheticLambda4 dialogsSearchAdapter$$ExternalSyntheticLambda4 = new DialogsSearchAdapter$$ExternalSyntheticLambda4(this, parent);
                this.cancelShowMoreAnimation = dialogsSearchAdapter$$ExternalSyntheticLambda4;
                AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda4, 400);
                return;
            }
            this.showMoreAnimation = false;
        }
    }

    /* renamed from: lambda$onBindViewHolder$21$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2611xd90e0var_(int rawPosition) {
        notifyItemChanged(rawPosition + 3);
    }

    /* renamed from: lambda$onBindViewHolder$22$org-telegram-ui-Adapters-DialogsSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2612x4e8835a5(View parent) {
        this.showMoreAnimation = false;
        this.showMoreHeader = null;
        if (parent != null) {
            parent.invalidate();
        }
    }

    public int getItemViewType(int i) {
        if (this.searchResultHashtags.isEmpty()) {
            if (isRecentSearchDisplayed()) {
                int offset = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
                if (i < offset) {
                    return 5;
                }
                if (i == offset) {
                    return 1;
                }
                if (i < getRecentItemsCount()) {
                    return 0;
                }
                i -= getRecentItemsCount();
            }
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int localCount = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            if (localCount + localServerCount > 0 && getRecentItemsCount() > 0) {
                if (i == 0) {
                    return 1;
                }
                i--;
            }
            int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
            if (phoneCount > 3 && this.phoneCollapsed) {
                phoneCount = 3;
            }
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (globalCount > 4 && this.globalSearchCollapsed) {
                globalCount = 4;
            }
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
                if (i4 < 0 || i4 >= globalCount) {
                    int i5 = i4 - globalCount;
                    if (i5 < 0 || i5 >= messagesCount) {
                        return 3;
                    }
                    if (i5 == 0) {
                        return 1;
                    }
                    return 2;
                } else if (i4 == 0) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                Object object = getItem(i3);
                if (!(object instanceof String)) {
                    return 0;
                }
                if ("section".equals((String) object)) {
                    return 1;
                }
                return 6;
            }
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

    public void filterRecent(String query) {
        this.filteredRecentQuery = query;
        this.filteredRecentSearchObjects.clear();
        if (!TextUtils.isEmpty(query)) {
            String lowerCasedQuery = query.toLowerCase();
            int count = this.recentSearchObjects.size();
            for (int i = 0; i < count; i++) {
                RecentSearchObject obj = this.recentSearchObjects.get(i);
                if (!(obj == null || obj.object == null)) {
                    String title = null;
                    String username = null;
                    if (obj.object instanceof TLRPC.Chat) {
                        title = ((TLRPC.Chat) obj.object).title;
                        username = ((TLRPC.Chat) obj.object).username;
                    } else if (obj.object instanceof TLRPC.User) {
                        title = UserObject.getUserName((TLRPC.User) obj.object);
                        username = ((TLRPC.User) obj.object).username;
                    } else if (obj.object instanceof TLRPC.ChatInvite) {
                        title = ((TLRPC.ChatInvite) obj.object).title;
                    }
                    if ((title != null && wordStartsWith(title.toLowerCase(), lowerCasedQuery)) || (username != null && wordStartsWith(username.toLowerCase(), lowerCasedQuery))) {
                        this.filteredRecentSearchObjects.add(obj);
                    }
                    if (this.filteredRecentSearchObjects.size() >= 5) {
                        return;
                    }
                }
            }
        }
    }

    private boolean wordStartsWith(String loweredTitle, String loweredQuery) {
        if (loweredQuery == null || loweredTitle == null) {
            return false;
        }
        String[] words = loweredTitle.toLowerCase().split(" ");
        for (int j = 0; j < words.length; j++) {
            if (words[j] != null && (words[j].startsWith(loweredQuery) || loweredQuery.startsWith(words[j]))) {
                return true;
            }
        }
        return false;
    }
}
