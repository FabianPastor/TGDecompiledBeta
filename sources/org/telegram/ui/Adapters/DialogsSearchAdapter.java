package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_dialog;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC$TL_topPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentItemCount;
    /* access modifiers changed from: private */
    public DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private FilteredSearchView.Delegate filtersDelegate;
    private int folderId;
    boolean globalSearchCollapsed = true;
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

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isSearching() {
        return this.waitingResponseCount > 0;
    }

    public static class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
        private final int currentAccount;
        private boolean drawChecked;
        private final Context mContext;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public CategoryAdapterRecycler(Context context, int i, boolean z) {
            this.drawChecked = z;
            this.mContext = context;
            this.currentAccount = i;
        }

        public void setIndex(int i) {
            notifyDataSetChanged();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            HintDialogCell hintDialogCell = new HintDialogCell(this.mContext, this.drawChecked);
            hintDialogCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(hintDialogCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$Chat tLRPC$Chat;
            String str;
            HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
            TLRPC$TL_topPeer tLRPC$TL_topPeer = MediaDataController.getInstance(this.currentAccount).hints.get(i);
            new TLRPC$TL_dialog();
            TLRPC$Peer tLRPC$Peer = tLRPC$TL_topPeer.peer;
            long j = tLRPC$Peer.user_id;
            TLRPC$User tLRPC$User = null;
            if (j != 0) {
                tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tLRPC$TL_topPeer.peer.user_id));
                tLRPC$Chat = null;
            } else {
                long j2 = tLRPC$Peer.channel_id;
                if (j2 != 0) {
                    j = -j2;
                    tLRPC$Chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$TL_topPeer.peer.channel_id));
                } else {
                    long j3 = tLRPC$Peer.chat_id;
                    if (j3 != 0) {
                        j = -j3;
                        tLRPC$Chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$TL_topPeer.peer.chat_id));
                    } else {
                        tLRPC$Chat = null;
                        j = 0;
                    }
                }
            }
            hintDialogCell.setTag(Long.valueOf(j));
            if (tLRPC$User != null) {
                str = UserObject.getFirstName(tLRPC$User);
            } else {
                str = tLRPC$Chat != null ? tLRPC$Chat.title : "";
            }
            hintDialogCell.setDialog(j, true, str);
        }

        public int getItemCount() {
            return MediaDataController.getInstance(this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int i, int i2) {
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ LongSparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                DialogsSearchAdapter dialogsSearchAdapter = DialogsSearchAdapter.this;
                dialogsSearchAdapter.waitingResponseCount--;
                int unused = dialogsSearchAdapter.lastGlobalSearchId = i;
                if (DialogsSearchAdapter.this.lastLocalSearchId != i) {
                    DialogsSearchAdapter.this.searchResult.clear();
                }
                if (DialogsSearchAdapter.this.lastMessagesSearchId != i) {
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
                for (int i = 0; i < arrayList.size(); i++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(arrayList.get(i).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(DialogsSearchAdapter.this.waitingResponseCount > 0, false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            public boolean canApplySearchResults(int i) {
                return i == DialogsSearchAdapter.this.lastSearchId;
            }
        });
        this.mContext = context;
        this.needMessagesSearch = i;
        this.dialogsType = i2;
        this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        loadRecentSearch();
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
    }

    public RecyclerListView getInnerListView() {
        return this.innerListView;
    }

    public void setDelegate(DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate) {
        this.delegate = dialogsSearchAdapterDelegate;
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

    private void searchMessagesInternal(String str, int i) {
        if (this.needMessagesSearch == 0) {
            return;
        }
        if (!TextUtils.isEmpty(this.lastMessagesSearchString) || !TextUtils.isEmpty(str)) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                return;
            }
            TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal = new TLRPC$TL_messages_searchGlobal();
            tLRPC$TL_messages_searchGlobal.limit = 20;
            tLRPC$TL_messages_searchGlobal.q = str;
            tLRPC$TL_messages_searchGlobal.filter = new TLRPC$TL_inputMessagesFilterEmpty();
            tLRPC$TL_messages_searchGlobal.flags |= 1;
            tLRPC$TL_messages_searchGlobal.folder_id = this.folderId;
            if (!str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                tLRPC$TL_messages_searchGlobal.offset_rate = 0;
                tLRPC$TL_messages_searchGlobal.offset_id = 0;
                tLRPC$TL_messages_searchGlobal.offset_peer = new TLRPC$TL_inputPeerEmpty();
            } else {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                MessageObject messageObject = arrayList.get(arrayList.size() - 1);
                tLRPC$TL_messages_searchGlobal.offset_id = messageObject.getId();
                tLRPC$TL_messages_searchGlobal.offset_rate = this.nextSearchRate;
                tLRPC$TL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(messageObject.messageOwner.peer_id));
            }
            this.lastMessagesSearchString = str;
            int i2 = this.lastReqId + 1;
            this.lastReqId = i2;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_searchGlobal, new DialogsSearchAdapter$$ExternalSyntheticLambda18(this, str, i2, i, tLRPC$TL_messages_searchGlobal), 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInternal$1(String str, int i, int i2, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList = new ArrayList();
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i3 = 0; i3 < tLRPC$messages_Messages.chats.size(); i3++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$messages_Messages.chats.get(i3);
                longSparseArray.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            for (int i4 = 0; i4 < tLRPC$messages_Messages.users.size(); i4++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i4);
                longSparseArray2.put(tLRPC$User.id, tLRPC$User);
            }
            for (int i5 = 0; i5 < tLRPC$messages_Messages.messages.size(); i5++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i5), (LongSparseArray<TLRPC$User>) longSparseArray2, (LongSparseArray<TLRPC$Chat>) longSparseArray, false, true);
                arrayList.add(messageObject);
                String str2 = str;
                messageObject.setQuery(str);
            }
        }
        String str3 = str;
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda9(this, i, i2, tLRPC$TL_error, str, tLObject, tLRPC$TL_messages_searchGlobal, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInternal$0(int i, int i2, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, ArrayList arrayList) {
        if (i == this.lastReqId && (i2 <= 0 || i2 == this.lastSearchId)) {
            this.waitingResponseCount--;
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
                MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$messages_Messages.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tLRPC$messages_Messages.chats, false);
                if (tLRPC$TL_messages_searchGlobal.offset_id == 0) {
                    this.searchResultMessages.clear();
                }
                this.nextSearchRate = tLRPC$messages_Messages.next_rate;
                for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                    int i4 = MessagesController.getInstance(this.currentAccount).deletedHistory.get(MessageObject.getDialogId(tLRPC$Message));
                    if (i4 == 0 || tLRPC$Message.id > i4) {
                        this.searchResultMessages.add((MessageObject) arrayList.get(i3));
                        long dialogId = MessageObject.getDialogId(tLRPC$Message);
                        ConcurrentHashMap<Long, Integer> concurrentHashMap = tLRPC$Message.out ? MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(this.currentAccount).dialogs_read_inbox_max;
                        Integer num = concurrentHashMap.get(Long.valueOf(dialogId));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(tLRPC$Message.out, dialogId));
                            concurrentHashMap.put(Long.valueOf(dialogId), num);
                        }
                        tLRPC$Message.unread = num.intValue() < tLRPC$Message.id;
                    }
                }
                this.searchWas = true;
                this.messagesSearchEndReached = tLRPC$messages_Messages.messages.size() != 20;
                if (i2 > 0) {
                    this.lastMessagesSearchId = i2;
                    if (this.lastLocalSearchId != i2) {
                        this.searchResult.clear();
                    }
                    if (this.lastGlobalSearchId != i2) {
                        this.searchAdapterHelper.clear();
                    }
                }
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                    this.delegate.runResultsEnterAnimation();
                }
                this.globalSearchCollapsed = true;
                this.phoneCollapsed = true;
                notifyDataSetChanged();
            }
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
        loadRecentSearch(this.currentAccount, this.dialogsType, new DialogsSearchAdapter$$ExternalSyntheticLambda19(this));
    }

    public static void loadRecentSearch(int i, int i2, OnRecentSearchLoaded onRecentSearchLoaded) {
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda3(i, i2, onRecentSearchLoaded));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008d A[Catch:{ Exception -> 0x016a }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x002f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$loadRecentSearch$5(int r13, int r14, org.telegram.ui.Adapters.DialogsSearchAdapter.OnRecentSearchLoaded r15) {
        /*
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r13)     // Catch:{ Exception -> 0x016a }
            org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x016a }
            java.lang.String r1 = "SELECT did, date FROM search_recent WHERE 1"
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x016a }
            org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r1, r3)     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r1.<init>()     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r3.<init>()     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r4.<init>()     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r5.<init>()     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r5.<init>()     // Catch:{ Exception -> 0x016a }
            androidx.collection.LongSparseArray r6 = new androidx.collection.LongSparseArray     // Catch:{ Exception -> 0x016a }
            r6.<init>()     // Catch:{ Exception -> 0x016a }
        L_0x002f:
            boolean r7 = r0.next()     // Catch:{ Exception -> 0x016a }
            if (r7 == 0) goto L_0x00a3
            long r7 = r0.longValue(r2)     // Catch:{ Exception -> 0x016a }
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)     // Catch:{ Exception -> 0x016a }
            r10 = 1
            if (r9 == 0) goto L_0x005b
            if (r14 == 0) goto L_0x0045
            r9 = 3
            if (r14 != r9) goto L_0x008a
        L_0x0045:
            int r9 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)     // Catch:{ Exception -> 0x016a }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x016a }
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016a }
            if (r11 != 0) goto L_0x008a
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x016a }
            r4.add(r9)     // Catch:{ Exception -> 0x016a }
            goto L_0x0088
        L_0x005b:
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r7)     // Catch:{ Exception -> 0x016a }
            if (r9 == 0) goto L_0x0076
            r9 = 2
            if (r14 == r9) goto L_0x008a
            java.lang.Long r9 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x016a }
            boolean r9 = r1.contains(r9)     // Catch:{ Exception -> 0x016a }
            if (r9 != 0) goto L_0x008a
            java.lang.Long r9 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x016a }
            r1.add(r9)     // Catch:{ Exception -> 0x016a }
            goto L_0x0088
        L_0x0076:
            long r11 = -r7
            java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x016a }
            boolean r9 = r3.contains(r9)     // Catch:{ Exception -> 0x016a }
            if (r9 != 0) goto L_0x008a
            java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x016a }
            r3.add(r9)     // Catch:{ Exception -> 0x016a }
        L_0x0088:
            r9 = 1
            goto L_0x008b
        L_0x008a:
            r9 = 0
        L_0x008b:
            if (r9 == 0) goto L_0x002f
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r9 = new org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject     // Catch:{ Exception -> 0x016a }
            r9.<init>()     // Catch:{ Exception -> 0x016a }
            r9.did = r7     // Catch:{ Exception -> 0x016a }
            int r7 = r0.intValue(r10)     // Catch:{ Exception -> 0x016a }
            r9.date = r7     // Catch:{ Exception -> 0x016a }
            r5.add(r9)     // Catch:{ Exception -> 0x016a }
            long r7 = r9.did     // Catch:{ Exception -> 0x016a }
            r6.put(r7, r9)     // Catch:{ Exception -> 0x016a }
            goto L_0x002f
        L_0x00a3:
            r0.dispose()     // Catch:{ Exception -> 0x016a }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r14.<init>()     // Catch:{ Exception -> 0x016a }
            boolean r0 = r4.isEmpty()     // Catch:{ Exception -> 0x016a }
            java.lang.String r7 = ","
            if (r0 != 0) goto L_0x00ea
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r0.<init>()     // Catch:{ Exception -> 0x016a }
            org.telegram.messenger.MessagesStorage r8 = org.telegram.messenger.MessagesStorage.getInstance(r13)     // Catch:{ Exception -> 0x016a }
            java.lang.String r4 = android.text.TextUtils.join(r7, r4)     // Catch:{ Exception -> 0x016a }
            r8.getEncryptedChatsInternal(r4, r0, r1)     // Catch:{ Exception -> 0x016a }
            r4 = 0
        L_0x00c4:
            int r8 = r0.size()     // Catch:{ Exception -> 0x016a }
            if (r4 >= r8) goto L_0x00ea
            java.lang.Object r8 = r0.get(r4)     // Catch:{ Exception -> 0x016a }
            org.telegram.tgnet.TLRPC$EncryptedChat r8 = (org.telegram.tgnet.TLRPC$EncryptedChat) r8     // Catch:{ Exception -> 0x016a }
            int r8 = r8.id     // Catch:{ Exception -> 0x016a }
            long r8 = (long) r8     // Catch:{ Exception -> 0x016a }
            long r8 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r8)     // Catch:{ Exception -> 0x016a }
            java.lang.Object r8 = r6.get(r8)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r8 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r8     // Catch:{ Exception -> 0x016a }
            if (r8 == 0) goto L_0x00e7
            java.lang.Object r9 = r0.get(r4)     // Catch:{ Exception -> 0x016a }
            org.telegram.tgnet.TLObject r9 = (org.telegram.tgnet.TLObject) r9     // Catch:{ Exception -> 0x016a }
            r8.object = r9     // Catch:{ Exception -> 0x016a }
        L_0x00e7:
            int r4 = r4 + 1
            goto L_0x00c4
        L_0x00ea:
            boolean r0 = r3.isEmpty()     // Catch:{ Exception -> 0x016a }
            if (r0 != 0) goto L_0x0130
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x016a }
            r0.<init>()     // Catch:{ Exception -> 0x016a }
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r13)     // Catch:{ Exception -> 0x016a }
            java.lang.String r3 = android.text.TextUtils.join(r7, r3)     // Catch:{ Exception -> 0x016a }
            r4.getChatsInternal(r3, r0)     // Catch:{ Exception -> 0x016a }
            r3 = 0
        L_0x0101:
            int r4 = r0.size()     // Catch:{ Exception -> 0x016a }
            if (r3 >= r4) goto L_0x0130
            java.lang.Object r4 = r0.get(r3)     // Catch:{ Exception -> 0x016a }
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC$Chat) r4     // Catch:{ Exception -> 0x016a }
            long r8 = r4.id     // Catch:{ Exception -> 0x016a }
            long r8 = -r8
            org.telegram.tgnet.TLRPC$InputChannel r10 = r4.migrated_to     // Catch:{ Exception -> 0x016a }
            if (r10 == 0) goto L_0x0123
            java.lang.Object r4 = r6.get(r8)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r4 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r4     // Catch:{ Exception -> 0x016a }
            r6.remove(r8)     // Catch:{ Exception -> 0x016a }
            if (r4 == 0) goto L_0x012d
            r5.remove(r4)     // Catch:{ Exception -> 0x016a }
            goto L_0x012d
        L_0x0123:
            java.lang.Object r8 = r6.get(r8)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r8 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r8     // Catch:{ Exception -> 0x016a }
            if (r8 == 0) goto L_0x012d
            r8.object = r4     // Catch:{ Exception -> 0x016a }
        L_0x012d:
            int r3 = r3 + 1
            goto L_0x0101
        L_0x0130:
            boolean r0 = r1.isEmpty()     // Catch:{ Exception -> 0x016a }
            if (r0 != 0) goto L_0x015c
            org.telegram.messenger.MessagesStorage r13 = org.telegram.messenger.MessagesStorage.getInstance(r13)     // Catch:{ Exception -> 0x016a }
            java.lang.String r0 = android.text.TextUtils.join(r7, r1)     // Catch:{ Exception -> 0x016a }
            r13.getUsersInternal(r0, r14)     // Catch:{ Exception -> 0x016a }
        L_0x0141:
            int r13 = r14.size()     // Catch:{ Exception -> 0x016a }
            if (r2 >= r13) goto L_0x015c
            java.lang.Object r13 = r14.get(r2)     // Catch:{ Exception -> 0x016a }
            org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC$User) r13     // Catch:{ Exception -> 0x016a }
            long r0 = r13.id     // Catch:{ Exception -> 0x016a }
            java.lang.Object r0 = r6.get(r0)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r0     // Catch:{ Exception -> 0x016a }
            if (r0 == 0) goto L_0x0159
            r0.object = r13     // Catch:{ Exception -> 0x016a }
        L_0x0159:
            int r2 = r2 + 1
            goto L_0x0141
        L_0x015c:
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda16 r13 = org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda16.INSTANCE     // Catch:{ Exception -> 0x016a }
            java.util.Collections.sort(r5, r13)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda4 r13 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda4     // Catch:{ Exception -> 0x016a }
            r13.<init>(r15, r5, r6)     // Catch:{ Exception -> 0x016a }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)     // Catch:{ Exception -> 0x016a }
            goto L_0x016e
        L_0x016a:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x016e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.lambda$loadRecentSearch$5(int, int, org.telegram.ui.Adapters.DialogsSearchAdapter$OnRecentSearchLoaded):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadRecentSearch$3(RecentSearchObject recentSearchObject, RecentSearchObject recentSearchObject2) {
        int i = recentSearchObject.date;
        int i2 = recentSearchObject2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void putRecentSearch(long j, TLObject tLObject) {
        RecentSearchObject recentSearchObject = this.recentSearchObjectsById.get(j);
        if (recentSearchObject == null) {
            recentSearchObject = new RecentSearchObject();
            this.recentSearchObjectsById.put(j, recentSearchObject);
        } else {
            this.recentSearchObjects.remove(recentSearchObject);
        }
        this.recentSearchObjects.add(0, recentSearchObject);
        recentSearchObject.did = j;
        recentSearchObject.object = tLObject;
        recentSearchObject.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda13(this, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putRecentSearch$6(long j) {
        try {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new LongSparseArray<>();
        this.recentSearchObjects = new ArrayList<>();
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentSearch$7() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void removeRecentSearch(long j) {
        RecentSearchObject recentSearchObject = this.recentSearchObjectsById.get(j);
        if (recentSearchObject != null) {
            this.recentSearchObjectsById.remove(j);
            this.recentSearchObjects.remove(recentSearchObject);
            notifyDataSetChanged();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda12(this, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRecentSearch$8(long j) {
        try {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            database.executeFast("DELETE FROM search_recent WHERE did = " + j).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    /* access modifiers changed from: private */
    /* renamed from: setRecentSearch */
    public void lambda$loadRecentSearch$2(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = longSparseArray;
        for (int i = 0; i < this.recentSearchObjects.size(); i++) {
            RecentSearchObject recentSearchObject = this.recentSearchObjects.get(i);
            TLObject tLObject = recentSearchObject.object;
            if (tLObject instanceof TLRPC$User) {
                MessagesController.getInstance(this.currentAccount).putUser((TLRPC$User) recentSearchObject.object, true);
            } else if (tLObject instanceof TLRPC$Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((TLRPC$Chat) recentSearchObject.object, true);
            } else if (tLObject instanceof TLRPC$EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC$EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(String str, int i) {
        if (this.needMessagesSearch != 2) {
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                this.lastSearchId = 0;
                updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), this.lastSearchId);
                return;
            }
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda15(this, lowerCase, i, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchDialogsInternal$10(String str, int i, String str2) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        MessagesStorage.getInstance(this.currentAccount).localSearch(this.dialogsType, str, arrayList, arrayList2, arrayList3, -1);
        updateSearchResults(arrayList, arrayList2, arrayList3, i);
        FiltersView.fillTipDates(str, this.localTipDates);
        this.localTipArchive = false;
        if (str.length() >= 3 && (LocaleController.getString("ArchiveSearchFilter", NUM).toLowerCase().startsWith(str) || "archive".startsWith(str2))) {
            this.localTipArchive = true;
        }
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchDialogsInternal$9() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<TLRPC$User> arrayList3, int i) {
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda11(this, i, arrayList, arrayList3, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$12(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        long j;
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
            boolean z = false;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object obj = arrayList.get(i2);
                if (obj instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) obj;
                    MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, true);
                    j = tLRPC$User.id;
                } else if (obj instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
                    MessagesController.getInstance(this.currentAccount).putChat(tLRPC$Chat, true);
                    j = -tLRPC$Chat.id;
                } else {
                    if (obj instanceof TLRPC$EncryptedChat) {
                        MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC$EncryptedChat) obj, true);
                    }
                    j = 0;
                }
                if (j != 0 && MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j) == null) {
                    MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(j, new DialogsSearchAdapter$$ExternalSyntheticLambda17(this, j, obj));
                }
            }
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
            this.searchResult = arrayList;
            this.searchResultNames = arrayList3;
            this.searchAdapterHelper.mergeResults(arrayList);
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
            if (dialogsSearchAdapterDelegate != null) {
                if (this.waitingResponseCount > 0) {
                    z = true;
                }
                dialogsSearchAdapterDelegate.searchStateChanged(z, true);
                this.delegate.runResultsEnterAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$11(long j, Object obj, int i) {
        if (i != -1) {
            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
            tLRPC$TL_dialog.id = j;
            if (i != 0) {
                tLRPC$TL_dialog.folder_id = i;
            }
            if (obj instanceof TLRPC$Chat) {
                tLRPC$TL_dialog.flags = ChatObject.isChannel((TLRPC$Chat) obj) ? 1 : 0;
            }
            MessagesController.getInstance(this.currentAccount).dialogs_dict.put(j, tLRPC$TL_dialog);
            MessagesController.getInstance(this.currentAccount).getAllDialogs().add(tLRPC$TL_dialog);
            MessagesController.getInstance(this.currentAccount).sortDialogs((LongSparseArray<TLRPC$Chat>) null);
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

    public void searchDialogs(String str, int i) {
        String str2 = str;
        int i2 = i;
        if (str2 == null || !str2.equals(this.lastSearchText) || (i2 != this.folderId && !TextUtils.isEmpty(str))) {
            this.lastSearchText = str2;
            this.folderId = i2;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            Runnable runnable = this.searchRunnable2;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable2 = null;
            }
            String trim = str2 != null ? str.trim() : null;
            if (TextUtils.isEmpty(trim)) {
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
                SearchAdapterHelper searchAdapterHelper2 = this.searchAdapterHelper;
                int i3 = this.dialogsType;
                searchAdapterHelper2.queryServerSearch((String) null, true, true, i3 != 11, i3 != 11, i3 == 2 || i3 == 11, 0, i3 == 0, 0, 0);
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
            if (this.needMessagesSearch == 2 || !trim.startsWith("#") || trim.length() != 1) {
                this.searchResultHashtags.clear();
            } else {
                this.messagesSearchEndReached = true;
                if (this.searchAdapterHelper.loadRecentHashtags()) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    ArrayList<SearchAdapterHelper.HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                    for (int i4 = 0; i4 < hashtags.size(); i4++) {
                        this.searchResultHashtags.add(hashtags.get(i4).hashtag);
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
            int i5 = this.lastSearchId + 1;
            this.lastSearchId = i5;
            this.waitingResponseCount = 3;
            this.globalSearchCollapsed = true;
            this.phoneCollapsed = true;
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate3 = this.delegate;
            if (dialogsSearchAdapterDelegate3 != null) {
                dialogsSearchAdapterDelegate3.searchStateChanged(true, false);
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            DialogsSearchAdapter$$ExternalSyntheticLambda14 dialogsSearchAdapter$$ExternalSyntheticLambda14 = new DialogsSearchAdapter$$ExternalSyntheticLambda14(this, trim, i5, str2);
            this.searchRunnable = dialogsSearchAdapter$$ExternalSyntheticLambda14;
            dispatchQueue.postRunnable(dialogsSearchAdapter$$ExternalSyntheticLambda14, 300);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchDialogs$14(String str, int i, String str2) {
        this.searchRunnable = null;
        searchDialogsInternal(str, i);
        DialogsSearchAdapter$$ExternalSyntheticLambda10 dialogsSearchAdapter$$ExternalSyntheticLambda10 = new DialogsSearchAdapter$$ExternalSyntheticLambda10(this, i, str, str2);
        this.searchRunnable2 = dialogsSearchAdapter$$ExternalSyntheticLambda10;
        AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchDialogs$13(int i, String str, String str2) {
        int i2 = i;
        this.searchRunnable2 = null;
        if (i2 == this.lastSearchId) {
            if (this.needMessagesSearch != 2) {
                SearchAdapterHelper searchAdapterHelper2 = this.searchAdapterHelper;
                int i3 = this.dialogsType;
                searchAdapterHelper2.queryServerSearch(str, true, i3 != 4, true, (i3 == 4 || i3 == 11) ? false : true, i3 == 2 || i3 == 1, 0, i3 == 0, 0, i);
            } else {
                this.waitingResponseCount -= 2;
            }
            if (this.needMessagesSearch == 0) {
                this.waitingResponseCount--;
            } else {
                searchMessagesInternal(str2, i2);
            }
        }
    }

    public int getItemCount() {
        int i = 0;
        int i2 = 3;
        if (this.waitingResponseCount == 3) {
            return 0;
        }
        if (isRecentSearchDisplayed()) {
            if (!this.recentSearchObjects.isEmpty()) {
                i = this.recentSearchObjects.size() + 1;
            }
            return i + (MediaDataController.getInstance(this.currentAccount).hints.isEmpty() ^ true ? 1 : 0);
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1 + 0;
        } else {
            int size = this.searchResult.size() + 0;
            int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 > 3 && this.globalSearchCollapsed) {
                size3 = 3;
            }
            int size4 = this.searchAdapterHelper.getPhoneSearch().size();
            if (size4 <= 3 || !this.phoneCollapsed) {
                i2 = size4;
            }
            int size5 = this.searchResultMessages.size();
            int i3 = size + size2;
            if (size3 != 0) {
                i3 += size3 + 1;
            }
            if (i2 != 0) {
                i3 += i2;
            }
            if (size5 != 0) {
                i3 += size5 + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0);
            }
            this.currentItemCount = i3;
            return i3;
        }
    }

    public Object getItem(int i) {
        int i2;
        Object obj;
        if (isRecentSearchDisplayed()) {
            int i3 = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty();
            if (i <= i3 || (i2 = (i - 1) - i3) >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject tLObject = this.recentSearchObjects.get(i2).object;
            if (tLObject instanceof TLRPC$User) {
                obj = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC$User) tLObject).id));
                if (obj == null) {
                    return tLObject;
                }
            } else if (!(tLObject instanceof TLRPC$Chat) || (obj = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(((TLRPC$Chat) tLObject).id))) == null) {
                return tLObject;
            }
            return obj;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
            int size = this.searchResult.size();
            int size2 = localServerSearch.size();
            int size3 = phoneSearch.size();
            if (size3 > 3 && this.phoneCollapsed) {
                size3 = 3;
            }
            int i4 = 0;
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (size4 > 4 && this.globalSearchCollapsed) {
                size4 = 4;
            }
            if (!this.searchResultMessages.isEmpty()) {
                i4 = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < size) {
                return this.searchResult.get(i);
            }
            int i5 = i - size;
            if (i5 >= 0 && i5 < size2) {
                return localServerSearch.get(i5);
            }
            int i6 = i5 - size2;
            if (i6 >= 0 && i6 < size3) {
                return phoneSearch.get(i6);
            }
            int i7 = i6 - size3;
            if (i7 > 0 && i7 < size4) {
                return globalSearch.get(i7 - 1);
            }
            int i8 = i7 - size4;
            if (i8 <= 0 || i8 >= i4) {
                return null;
            }
            return this.searchResultMessages.get(i8 - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public boolean isGlobalSearch(int i) {
        int i2;
        if (isRecentSearchDisplayed() || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int size = this.searchResult.size();
        int size2 = localServerSearch.size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (size3 > 3 && this.phoneCollapsed) {
            size3 = 3;
        }
        int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (size4 > 4 && this.globalSearchCollapsed) {
            size4 = 4;
        }
        if (!this.searchResultMessages.isEmpty()) {
            this.searchResultMessages.size();
        }
        if (i >= 0 && i < size) {
            return false;
        }
        int i3 = i - size;
        if (i3 >= 0 && i3 < size2) {
            return false;
        }
        int i4 = i3 - size2;
        if ((i4 <= 0 || i4 >= size3) && (i2 = i4 - size3) > 0 && i2 < size4) {
            return true;
        }
        return false;
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 3) ? false : true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateViewHolder$15(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog(((Long) view.getTag()).longValue());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreateViewHolder$16(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Long) view.getTag()).longValue());
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.ui.Cells.GraySectionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.ui.Cells.HashtagSearchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Adapters.DialogsSearchAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.TextCell} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
        /*
            r5 = this;
            r6 = 5
            if (r7 == 0) goto L_0x0099
            r0 = 1
            if (r7 == r0) goto L_0x0091
            r1 = 2
            r2 = 0
            r3 = 0
            if (r7 == r1) goto L_0x0088
            r1 = 3
            if (r7 == r1) goto L_0x007a
            r0 = 4
            if (r7 == r0) goto L_0x0072
            if (r7 == r6) goto L_0x001e
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            android.content.Context r1 = r5.mContext
            r2 = 16
            r0.<init>(r1, r2, r3)
            goto L_0x00a0
        L_0x001e:
            org.telegram.ui.Adapters.DialogsSearchAdapter$2 r0 = new org.telegram.ui.Adapters.DialogsSearchAdapter$2
            android.content.Context r1 = r5.mContext
            r0.<init>(r5, r1)
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setSelectorRadius(r1)
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSelectorDrawableColor(r1)
            r1 = 9
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.setTag(r1)
            r0.setItemAnimator(r2)
            r0.setLayoutAnimation(r2)
            org.telegram.ui.Adapters.DialogsSearchAdapter$3 r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$3
            android.content.Context r2 = r5.mContext
            r1.<init>(r5, r2)
            r1.setOrientation(r3)
            r0.setLayoutManager(r1)
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler
            android.content.Context r2 = r5.mContext
            int r4 = r5.currentAccount
            r1.<init>(r2, r4, r3)
            r0.setAdapter(r1)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda20 r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda20
            r1.<init>(r5)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda21 r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda21
            r1.<init>(r5)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            r5.innerListView = r0
            goto L_0x00a0
        L_0x0072:
            org.telegram.ui.Cells.HashtagSearchCell r0 = new org.telegram.ui.Cells.HashtagSearchCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
            goto L_0x00a0
        L_0x007a:
            org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r2 = r5.mContext
            r1.<init>(r2)
            r1.setViewType(r0)
            r1.setIsSingleCell(r0)
            goto L_0x008f
        L_0x0088:
            org.telegram.ui.Cells.DialogCell r1 = new org.telegram.ui.Cells.DialogCell
            android.content.Context r4 = r5.mContext
            r1.<init>(r2, r4, r3, r0)
        L_0x008f:
            r0 = r1
            goto L_0x00a0
        L_0x0091:
            org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
            goto L_0x00a0
        L_0x0099:
            org.telegram.ui.Cells.ProfileSearchCell r0 = new org.telegram.ui.Cells.ProfileSearchCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
        L_0x00a0:
            r1 = -1
            if (r7 != r6) goto L_0x00b2
            androidx.recyclerview.widget.RecyclerView$LayoutParams r6 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.<init>((int) r1, (int) r7)
            r0.setLayoutParams(r6)
            goto L_0x00bb
        L_0x00b2:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r6 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r7 = -2
            r6.<init>((int) r1, (int) r7)
            r0.setLayoutParams(r6)
        L_0x00bb:
            org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
            r6.<init>(r0)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: android.text.SpannableStringBuilder} */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x02e6, code lost:
        if (r0.startsWith("@" + r10.username) != false) goto L_0x02e8;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x03e7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r20, int r21) {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            r2 = r21
            int r3 = r20.getItemViewType()
            r4 = 2
            r5 = 4
            r6 = 3
            r7 = 0
            r8 = 0
            r9 = 1
            if (r3 == 0) goto L_0x01c9
            if (r3 == r9) goto L_0x00ad
            if (r3 == r4) goto L_0x0089
            if (r3 == r5) goto L_0x006a
            r5 = 5
            if (r3 == r5) goto L_0x005a
            r4 = 6
            if (r3 == r4) goto L_0x0020
            goto L_0x03eb
        L_0x0020:
            java.lang.Object r2 = r1.getItem(r2)
            java.lang.String r2 = (java.lang.String) r2
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
            java.lang.String r3 = "windowBackgroundWhiteBlueText2"
            r0.setColors(r7, r3)
            r3 = 2131624210(0x7f0e0112, float:1.8875593E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "+"
            r6.append(r7)
            r6.append(r2)
            java.lang.String r2 = r6.toString()
            java.lang.String r2 = r5.format(r2)
            r4[r8] = r2
            java.lang.String r2 = "AddContactByPhone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            r0.setText(r2, r8)
            goto L_0x03eb
        L_0x005a:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0
            int r2 = r2 / r4
            r0.setIndex(r2)
            goto L_0x03eb
        L_0x006a:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.HashtagSearchCell r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r4 = r2 + -1
            java.lang.Object r3 = r3.get(r4)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            r0.setText(r3)
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r3 = r3.size()
            if (r2 == r3) goto L_0x0084
            r8 = 1
        L_0x0084:
            r0.setNeedDivider(r8)
            goto L_0x03eb
        L_0x0089:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.DialogCell r0 = (org.telegram.ui.Cells.DialogCell) r0
            int r3 = r19.getItemCount()
            int r3 = r3 - r9
            if (r2 == r3) goto L_0x0095
            r8 = 1
        L_0x0095:
            r0.useSeparator = r8
            java.lang.Object r2 = r1.getItem(r2)
            r5 = r2
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r3 = r5.getDialogId()
            org.telegram.tgnet.TLRPC$Message r2 = r5.messageOwner
            int r6 = r2.date
            r7 = 0
            r2 = r0
            r2.setDialog(r3, r5, r6, r7)
            goto L_0x03eb
        L_0x00ad:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            boolean r3 = r19.isRecentSearchDisplayed()
            r4 = 2131624999(0x7f0e0427, float:1.8877194E38)
            java.lang.String r10 = "ClearButton"
            if (r3 == 0) goto L_0x00f0
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
            boolean r3 = r3.isEmpty()
            r3 = r3 ^ r9
            if (r2 >= r3) goto L_0x00d9
            r2 = 2131624883(0x7f0e03b3, float:1.8876958E38)
            java.lang.String r3 = "ChatHints"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x03eb
        L_0x00d9:
            r2 = 2131627573(0x7f0e0e35, float:1.8882414E38)
            java.lang.String r3 = "Recent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda2
            r4.<init>(r1)
            r0.setText(r2, r3, r4)
            goto L_0x03eb
        L_0x00f0:
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x010f
            r2 = 2131625948(0x7f0e07dc, float:1.8879118E38)
            java.lang.String r3 = "Hashtags"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r4)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda1
            r4.<init>(r1)
            r0.setText(r2, r3, r4)
            goto L_0x03eb
        L_0x010f:
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getGlobalSearch()
            java.util.ArrayList<java.lang.Object> r4 = r1.searchResult
            int r4 = r4.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r10 = r1.searchAdapterHelper
            java.util.ArrayList r10 = r10.getLocalServerSearch()
            int r10 = r10.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r11 = r1.searchAdapterHelper
            java.util.ArrayList r11 = r11.getPhoneSearch()
            int r11 = r11.size()
            if (r11 <= r6) goto L_0x0136
            boolean r12 = r1.phoneCollapsed
            if (r12 == 0) goto L_0x0136
            r11 = 3
        L_0x0136:
            boolean r12 = r3.isEmpty()
            if (r12 == 0) goto L_0x013e
            r3 = 0
            goto L_0x0143
        L_0x013e:
            int r3 = r3.size()
            int r3 = r3 + r9
        L_0x0143:
            if (r3 <= r5) goto L_0x014a
            boolean r9 = r1.globalSearchCollapsed
            if (r9 == 0) goto L_0x014a
            goto L_0x014b
        L_0x014a:
            r5 = r3
        L_0x014b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0154
            goto L_0x0159
        L_0x0154:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            r3.size()
        L_0x0159:
            int r4 = r4 + r10
            int r2 = r2 - r4
            if (r2 < 0) goto L_0x017c
            if (r2 >= r11) goto L_0x017c
            r2 = 2131627248(0x7f0e0cf0, float:1.8881755E38)
            java.lang.String r3 = "PhoneNumberSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getPhoneSearch()
            int r3 = r3.size()
            if (r3 <= r6) goto L_0x01a7
            boolean r8 = r1.phoneCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda8 r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda8
            r7.<init>(r1)
            goto L_0x01a7
        L_0x017c:
            int r2 = r2 - r11
            if (r2 < 0) goto L_0x019e
            if (r2 >= r5) goto L_0x019e
            r2 = 2131625900(0x7f0e07ac, float:1.887902E38)
            java.lang.String r3 = "GlobalSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getGlobalSearch()
            int r3 = r3.size()
            if (r3 <= r6) goto L_0x01a7
            boolean r8 = r1.globalSearchCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda6 r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda6
            r7.<init>(r1)
            goto L_0x01a7
        L_0x019e:
            r2 = 2131627816(0x7f0e0var_, float:1.8882907E38)
            java.lang.String r3 = "SearchMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x01a7:
            if (r7 != 0) goto L_0x01ae
            r0.setText(r2)
            goto L_0x03eb
        L_0x01ae:
            if (r8 == 0) goto L_0x01b6
            r3 = 2131628035(0x7f0e1003, float:1.8883351E38)
            java.lang.String r4 = "ShowMore"
            goto L_0x01bb
        L_0x01b6:
            r3 = 2131628034(0x7f0e1002, float:1.888335E38)
            java.lang.String r4 = "ShowLess"
        L_0x01bb:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0
            r4.<init>(r7)
            r0.setText(r2, r3, r4)
            goto L_0x03eb
        L_0x01c9:
            android.view.View r0 = r0.itemView
            r3 = r0
            org.telegram.ui.Cells.ProfileSearchCell r3 = (org.telegram.ui.Cells.ProfileSearchCell) r3
            long r17 = r3.getDialogId()
            java.lang.Object r0 = r1.getItem(r2)
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r10 == 0) goto L_0x01e3
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r10 = r0.username
            r11 = r7
            r13 = r11
            r12 = r10
            r10 = r0
            goto L_0x0232
        L_0x01e3:
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r10 == 0) goto L_0x0204
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            long r11 = r0.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
            if (r10 != 0) goto L_0x01fc
            goto L_0x01fd
        L_0x01fc:
            r0 = r10
        L_0x01fd:
            java.lang.String r10 = r0.username
            r11 = r0
            r13 = r7
            r12 = r10
            r10 = r13
            goto L_0x0232
        L_0x0204:
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r10 == 0) goto L_0x022e
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC$EncryptedChat) r0
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r10.getEncryptedChat(r0)
            int r10 = r1.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            long r11 = r0.user_id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            r13 = r0
            r11 = r7
            r12 = r11
            goto L_0x0232
        L_0x022e:
            r10 = r7
            r11 = r10
            r12 = r11
            r13 = r12
        L_0x0232:
            boolean r0 = r19.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x0249
            int r0 = r19.getItemCount()
            int r0 = r0 - r9
            if (r2 == r0) goto L_0x0241
            r0 = 1
            goto L_0x0242
        L_0x0241:
            r0 = 0
        L_0x0242:
            r3.useSeparator = r0
            r0 = r7
            r12 = r0
            r15 = 1
            goto L_0x0369
        L_0x0249:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r0 = r0.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
            java.util.ArrayList r14 = r14.getPhoneSearch()
            java.util.ArrayList<java.lang.Object> r15 = r1.searchResult
            int r15 = r15.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r7 = r1.searchAdapterHelper
            java.util.ArrayList r7 = r7.getLocalServerSearch()
            int r7 = r7.size()
            int r4 = r14.size()
            if (r4 <= r6) goto L_0x0270
            boolean r6 = r1.phoneCollapsed
            if (r6 == 0) goto L_0x0270
            r4 = 3
        L_0x0270:
            if (r4 <= 0) goto L_0x027f
            int r6 = r4 + -1
            java.lang.Object r6 = r14.get(r6)
            boolean r6 = r6 instanceof java.lang.String
            if (r6 == 0) goto L_0x027f
            int r6 = r4 + -2
            goto L_0x0280
        L_0x027f:
            r6 = r4
        L_0x0280:
            boolean r14 = r0.isEmpty()
            if (r14 == 0) goto L_0x0288
            r0 = 0
            goto L_0x028d
        L_0x0288:
            int r0 = r0.size()
            int r0 = r0 + r9
        L_0x028d:
            if (r0 <= r5) goto L_0x0294
            boolean r14 = r1.globalSearchCollapsed
            if (r14 == 0) goto L_0x0294
            goto L_0x0295
        L_0x0294:
            r5 = r0
        L_0x0295:
            int r0 = r19.getItemCount()
            int r0 = r0 - r9
            if (r2 == r0) goto L_0x02a9
            int r6 = r6 + r15
            int r6 = r6 + r7
            int r6 = r6 - r9
            if (r2 == r6) goto L_0x02a9
            int r15 = r15 + r5
            int r15 = r15 + r4
            int r15 = r15 + r7
            int r15 = r15 - r9
            if (r2 == r15) goto L_0x02a9
            r0 = 1
            goto L_0x02aa
        L_0x02a9:
            r0 = 0
        L_0x02aa:
            r3.useSeparator = r0
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r4 = "@"
            if (r2 >= r0) goto L_0x02ee
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r2)
            r12 = r0
            java.lang.CharSequence r12 = (java.lang.CharSequence) r12
            if (r12 == 0) goto L_0x02eb
            if (r10 == 0) goto L_0x02eb
            java.lang.String r0 = r10.username
            if (r0 == 0) goto L_0x02eb
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x02eb
            java.lang.String r0 = r12.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            java.lang.String r4 = r10.username
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x02eb
        L_0x02e8:
            r0 = 0
            goto L_0x0365
        L_0x02eb:
            r0 = r12
            goto L_0x0364
        L_0x02ee:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0363
            if (r10 == 0) goto L_0x0305
            java.lang.String r2 = r10.first_name
            java.lang.String r5 = r10.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r5)
            goto L_0x030b
        L_0x0305:
            if (r11 == 0) goto L_0x030a
            java.lang.String r2 = r11.title
            goto L_0x030b
        L_0x030a:
            r2 = 0
        L_0x030b:
            r5 = 33
            java.lang.String r6 = "windowBackgroundWhiteBlueText4"
            r7 = -1
            if (r2 == 0) goto L_0x032b
            int r14 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r0)
            if (r14 == r7) goto L_0x032b
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r2)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            r2.<init>(r6)
            int r0 = r0.length()
            int r0 = r0 + r14
            r12.setSpan(r2, r14, r0, r5)
            goto L_0x02eb
        L_0x032b:
            if (r12 == 0) goto L_0x0363
            boolean r2 = r0.startsWith(r4)
            if (r2 == 0) goto L_0x0337
            java.lang.String r0 = r0.substring(r9)
        L_0x0337:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x035e }
            r2.<init>()     // Catch:{ Exception -> 0x035e }
            r2.append(r4)     // Catch:{ Exception -> 0x035e }
            r2.append(r12)     // Catch:{ Exception -> 0x035e }
            int r4 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r12, r0)     // Catch:{ Exception -> 0x035e }
            if (r4 == r7) goto L_0x035c
            int r0 = r0.length()     // Catch:{ Exception -> 0x035e }
            if (r4 != 0) goto L_0x0351
            int r0 = r0 + 1
            goto L_0x0353
        L_0x0351:
            int r4 = r4 + 1
        L_0x0353:
            org.telegram.ui.Components.ForegroundColorSpanThemable r7 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x035e }
            r7.<init>(r6)     // Catch:{ Exception -> 0x035e }
            int r0 = r0 + r4
            r2.setSpan(r7, r4, r0, r5)     // Catch:{ Exception -> 0x035e }
        L_0x035c:
            r12 = r2
            goto L_0x02e8
        L_0x035e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02e8
        L_0x0363:
            r0 = 0
        L_0x0364:
            r12 = 0
        L_0x0365:
            r3.setChecked(r8, r8)
            r15 = 0
        L_0x0369:
            if (r10 == 0) goto L_0x0380
            long r4 = r10.id
            long r6 = r1.selfUserId
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0380
            r0 = 2131627774(0x7f0e0efe, float:1.8882822E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r7 = 0
            r16 = 1
            goto L_0x0383
        L_0x0380:
            r7 = r12
            r16 = 0
        L_0x0383:
            if (r11 == 0) goto L_0x03cb
            int r2 = r11.participants_count
            if (r2 == 0) goto L_0x03cb
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r2 == 0) goto L_0x039c
            boolean r2 = r11.megagroup
            if (r2 != 0) goto L_0x039c
            int r2 = r11.participants_count
            java.lang.String r4 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
            goto L_0x03a4
        L_0x039c:
            int r2 = r11.participants_count
            java.lang.String r4 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
        L_0x03a4:
            boolean r4 = r7 instanceof android.text.SpannableStringBuilder
            java.lang.String r5 = ", "
            if (r4 == 0) goto L_0x03b5
            r4 = r7
            android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
            android.text.SpannableStringBuilder r4 = r4.append(r5)
            r4.append(r2)
            goto L_0x03cb
        L_0x03b5:
            boolean r4 = android.text.TextUtils.isEmpty(r7)
            if (r4 != 0) goto L_0x03c9
            r4 = 3
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r4]
            r4[r8] = r7
            r4[r9] = r5
            r5 = 2
            r4[r5] = r2
            java.lang.CharSequence r2 = android.text.TextUtils.concat(r4)
        L_0x03c9:
            r14 = r2
            goto L_0x03cc
        L_0x03cb:
            r14 = r7
        L_0x03cc:
            if (r10 == 0) goto L_0x03cf
            r11 = r10
        L_0x03cf:
            r10 = r3
            r12 = r13
            r13 = r0
            r10.setData(r11, r12, r13, r14, r15, r16)
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogsSearchAdapterDelegate r0 = r1.delegate
            long r4 = r3.getDialogId()
            boolean r0 = r0.isSelected(r4)
            long r4 = r3.getDialogId()
            int r2 = (r17 > r4 ? 1 : (r17 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x03e8
            r8 = 1
        L_0x03e8:
            r3.setChecked(r0, r8)
        L_0x03eb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$17(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$18(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$19() {
        this.phoneCollapsed = !this.phoneCollapsed;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$20() {
        this.globalSearchCollapsed = !this.globalSearchCollapsed;
        notifyDataSetChanged();
    }

    public int getItemViewType(int i) {
        if (isRecentSearchDisplayed()) {
            int i2 = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty();
            if (i < i2) {
                return 5;
            }
            return i == i2 ? 1 : 0;
        }
        int i3 = 4;
        if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size3 = this.searchAdapterHelper.getPhoneSearch().size();
            if (size3 > 3 && this.phoneCollapsed) {
                size3 = 3;
            }
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (size4 <= 4 || !this.globalSearchCollapsed) {
                i3 = size4;
            }
            int size5 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < size) {
                return 0;
            }
            int i4 = i - size;
            if (i4 >= 0 && i4 < size2) {
                return 0;
            }
            int i5 = i4 - size2;
            if (i5 < 0 || i5 >= size3) {
                int i6 = i5 - size3;
                if (i6 >= 0 && i6 < i3) {
                    return i6 == 0 ? 1 : 0;
                }
                int i7 = i6 - i3;
                if (i7 < 0 || i7 >= size5) {
                    return 3;
                }
                if (i7 == 0) {
                    return 1;
                }
                return 2;
            }
            Object item = getItem(i5);
            if (!(item instanceof String)) {
                return 0;
            }
            if ("section".equals((String) item)) {
                return 1;
            }
            return 6;
        } else if (i == 0) {
            return 1;
        } else {
            return 4;
        }
    }

    public void setFiltersDelegate(FilteredSearchView.Delegate delegate2, boolean z) {
        this.filtersDelegate = delegate2;
        if (delegate2 != null && z) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    public int getCurrentItemCount() {
        return this.currentItemCount;
    }
}
