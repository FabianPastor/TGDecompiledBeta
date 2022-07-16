package org.telegram.ui.Adapters;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import org.telegram.tgnet.TLRPC$ChatInvite;
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
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    private Runnable cancelShowMoreAnimation;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentItemCount;
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

    public DialogsSearchAdapter(Context context, int i, int i2, DefaultItemAnimator defaultItemAnimator) {
        this.itemAnimator = defaultItemAnimator;
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
                this.filteredRecentQuery = null;
                this.searchResultMessages.clear();
                this.lastReqId = 0;
                this.lastMessagesSearchString = null;
                this.searchWas = false;
                notifyDataSetChanged();
                return;
            }
            filterRecent(str);
            this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
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
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_searchGlobal, new DialogsSearchAdapter$$ExternalSyntheticLambda21(this, str, i2, i, tLRPC$TL_messages_searchGlobal), 2);
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
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda8(this, i, i2, tLRPC$TL_error, str, tLObject, tLRPC$TL_messages_searchGlobal, arrayList));
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
                this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
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
        return (i == 2 || i == 4 || i == 5 || i == 6 || i == 11 || getRecentItemsCount() <= 0) ? false : true;
    }

    public boolean isSearchWas() {
        return this.searchWas;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && hasRecentSearch();
    }

    public void loadRecentSearch() {
        loadRecentSearch(this.currentAccount, this.dialogsType, new DialogsSearchAdapter$$ExternalSyntheticLambda22(this));
    }

    public static void loadRecentSearch(int i, int i2, OnRecentSearchLoaded onRecentSearchLoaded) {
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda4(i, i2, onRecentSearchLoaded));
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
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda19 r13 = org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda19.INSTANCE     // Catch:{ Exception -> 0x016a }
            java.util.Collections.sort(r5, r13)     // Catch:{ Exception -> 0x016a }
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda5 r13 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda5     // Catch:{ Exception -> 0x016a }
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda12(this, j));
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
        StringBuilder sb;
        if (this.searchWas) {
            sb = null;
            while (this.filteredRecentSearchObjects.size() > 0) {
                RecentSearchObject remove = this.filteredRecentSearchObjects.remove(0);
                this.recentSearchObjects.remove(remove);
                this.recentSearchObjectsById.remove(remove.did);
                if (sb == null) {
                    sb = new StringBuilder("did IN (");
                    sb.append(remove.did);
                } else {
                    sb.append(", ");
                    sb.append(remove.did);
                }
            }
            if (sb == null) {
                sb = new StringBuilder("1");
            } else {
                sb.append(")");
            }
        } else {
            this.filteredRecentSearchObjects.clear();
            this.recentSearchObjects.clear();
            this.recentSearchObjectsById.clear();
            sb = new StringBuilder("1");
        }
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda16(this, sb));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentSearch$7(StringBuilder sb) {
        try {
            sb.insert(0, "DELETE FROM search_recent WHERE ");
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast(sb.toString()).stepThis().dispose();
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new DialogsSearchAdapter$$ExternalSyntheticLambda11(this, j));
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
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchDialogsInternal$9() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<TLRPC$User> arrayList3, int i) {
        AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda10(this, i, arrayList, arrayList2, arrayList3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$12(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        long j;
        boolean z;
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
            int size = this.filteredRecentSearchObjects.size();
            boolean z2 = false;
            int i2 = 0;
            while (i2 < arrayList.size()) {
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
                    MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(j, new DialogsSearchAdapter$$ExternalSyntheticLambda20(this, j, obj));
                }
                int i3 = 0;
                while (true) {
                    if (i3 < size) {
                        RecentSearchObject recentSearchObject = this.filteredRecentSearchObjects.get(i3);
                        if (recentSearchObject != null && recentSearchObject.did == j) {
                            z = true;
                            break;
                        }
                        i3++;
                    } else {
                        z = false;
                        break;
                    }
                }
                if (z) {
                    arrayList.remove(i2);
                    arrayList2.remove(i2);
                    i2--;
                }
                i2++;
            }
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList3, true);
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList, this.filteredRecentSearchObjects);
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
            if (dialogsSearchAdapterDelegate != null) {
                if (this.waitingResponseCount > 0) {
                    z2 = true;
                }
                dialogsSearchAdapterDelegate.searchStateChanged(z2, true);
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
                this.filteredRecentQuery = null;
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<Object>) null, (ArrayList<RecentSearchObject>) null);
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
            filterRecent(trim);
            this.searchAdapterHelper.mergeResults(this.searchResult, this.filteredRecentSearchObjects);
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
        DialogsSearchAdapter$$ExternalSyntheticLambda9 dialogsSearchAdapter$$ExternalSyntheticLambda9 = new DialogsSearchAdapter$$ExternalSyntheticLambda9(this, i, str, str2);
        this.searchRunnable2 = dialogsSearchAdapter$$ExternalSyntheticLambda9;
        AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda9);
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

    public int getRecentItemsCount() {
        ArrayList<RecentSearchObject> arrayList = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        int i = 1;
        int size = !arrayList.isEmpty() ? arrayList.size() + 1 : 0;
        if (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
            i = 0;
        }
        return size + i;
    }

    public int getRecentResultsCount() {
        ArrayList<RecentSearchObject> arrayList = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public int getItemCount() {
        int i = 0;
        int i2 = 3;
        if (this.waitingResponseCount == 3) {
            return 0;
        }
        if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1 + 0;
        }
        if (isRecentSearchDisplayed()) {
            i = 0 + getRecentItemsCount();
            if (!this.searchWas) {
                return i;
            }
        }
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
        int i3 = i + size + size2;
        int size3 = this.searchAdapterHelper.getGlobalSearch().size();
        if (size3 > 3 && this.globalSearchCollapsed) {
            size3 = 3;
        }
        int size4 = this.searchAdapterHelper.getPhoneSearch().size();
        if (size4 <= 3 || !this.phoneCollapsed) {
            i2 = size4;
        }
        int size5 = this.searchResultMessages.size();
        if (size + size2 > 0 && getRecentItemsCount() > 0) {
            i3++;
        }
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

    public Object getItem(int i) {
        int i2;
        Object obj;
        if (this.searchResultHashtags.isEmpty()) {
            int i3 = 0;
            if (isRecentSearchDisplayed()) {
                int i4 = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
                ArrayList<RecentSearchObject> arrayList = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
                if (i <= i4 || (i2 = (i - 1) - i4) >= arrayList.size()) {
                    i -= getRecentItemsCount();
                } else {
                    TLObject tLObject = arrayList.get(i2).object;
                    if (tLObject instanceof TLRPC$User) {
                        obj = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC$User) tLObject).id));
                        if (obj == null) {
                            return tLObject;
                        }
                    } else if (!(tLObject instanceof TLRPC$Chat) || (obj = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(((TLRPC$Chat) tLObject).id))) == null) {
                        return tLObject;
                    }
                    return obj;
                }
            }
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            ArrayList<Object> phoneSearch = this.searchAdapterHelper.getPhoneSearch();
            int size = this.searchResult.size();
            int size2 = localServerSearch.size();
            if (size + size2 > 0 && getRecentItemsCount() > 0) {
                if (i == 0) {
                    return null;
                }
                i--;
            }
            int size3 = phoneSearch.size();
            if (size3 > 3 && this.phoneCollapsed) {
                size3 = 3;
            }
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (size4 > 4 && this.globalSearchCollapsed) {
                size4 = 4;
            }
            if (!this.searchResultMessages.isEmpty()) {
                i3 = this.searchResultMessages.size() + 1;
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
            if (i8 <= 0 || i8 >= i3) {
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
        if (!this.searchWas || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        if (isRecentSearchDisplayed()) {
            int i3 = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
            ArrayList<RecentSearchObject> arrayList = this.searchWas ? this.filteredRecentSearchObjects : this.recentSearchObjects;
            if (i > i3 && (i - 1) - i3 < arrayList.size()) {
                return false;
            }
            i -= getRecentItemsCount();
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
        int i4 = i - size;
        if (i4 >= 0 && i4 < size2) {
            return false;
        }
        int i5 = i4 - size2;
        if ((i5 <= 0 || i5 >= size3) && (i2 = i5 - size3) > 0 && i2 < size4) {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.ui.Cells.HashtagSearchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Adapters.DialogsSearchAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.TextCell} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
        /*
            r5 = this;
            r6 = 5
            if (r7 == 0) goto L_0x0090
            r0 = 1
            if (r7 == r0) goto L_0x0088
            r1 = 2
            r2 = 0
            r3 = 0
            if (r7 == r1) goto L_0x007f
            r1 = 3
            if (r7 == r1) goto L_0x0071
            r0 = 4
            if (r7 == r0) goto L_0x0069
            if (r7 == r6) goto L_0x001e
            org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
            android.content.Context r1 = r5.mContext
            r2 = 16
            r0.<init>(r1, r2, r3)
            goto L_0x0097
        L_0x001e:
            org.telegram.ui.Adapters.DialogsSearchAdapter$2 r0 = new org.telegram.ui.Adapters.DialogsSearchAdapter$2
            android.content.Context r1 = r5.mContext
            r0.<init>(r5, r1)
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
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda23 r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda23
            r1.<init>(r5)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda24 r1 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda24
            r1.<init>(r5)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            r5.innerListView = r0
            goto L_0x0097
        L_0x0069:
            org.telegram.ui.Cells.HashtagSearchCell r0 = new org.telegram.ui.Cells.HashtagSearchCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
            goto L_0x0097
        L_0x0071:
            org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r2 = r5.mContext
            r1.<init>(r2)
            r1.setViewType(r0)
            r1.setIsSingleCell(r0)
            goto L_0x0086
        L_0x007f:
            org.telegram.ui.Cells.DialogCell r1 = new org.telegram.ui.Cells.DialogCell
            android.content.Context r4 = r5.mContext
            r1.<init>(r2, r4, r3, r0)
        L_0x0086:
            r0 = r1
            goto L_0x0097
        L_0x0088:
            org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
            goto L_0x0097
        L_0x0090:
            org.telegram.ui.Cells.ProfileSearchCell r0 = new org.telegram.ui.Cells.ProfileSearchCell
            android.content.Context r1 = r5.mContext
            r0.<init>(r1)
        L_0x0097:
            r1 = -1
            if (r7 != r6) goto L_0x00a9
            androidx.recyclerview.widget.RecyclerView$LayoutParams r6 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r7 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.<init>((int) r1, (int) r7)
            r0.setLayoutParams(r6)
            goto L_0x00b2
        L_0x00a9:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r6 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r7 = -2
            r6.<init>((int) r1, (int) r7)
            r0.setLayoutParams(r6)
        L_0x00b2:
            org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
            r6.<init>(r0)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v21, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: java.lang.CharSequence} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r2v17 */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x035d, code lost:
        if (r6.startsWith("@" + r4.username) != false) goto L_0x0364;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03f7  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0404  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0417  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0423  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x045e  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0479  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x047b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r23, int r24) {
        /*
            r22 = this;
            r1 = r22
            r0 = r23
            r2 = r24
            int r3 = r23.getItemViewType()
            java.lang.String r4 = "windowBackgroundWhite"
            r5 = 2
            r6 = 4
            r7 = 3
            r8 = 0
            r9 = 0
            r10 = 1
            if (r3 == 0) goto L_0x0218
            if (r3 == r10) goto L_0x00be
            if (r3 == r5) goto L_0x0092
            if (r3 == r6) goto L_0x006c
            r4 = 5
            if (r3 == r4) goto L_0x005c
            r4 = 6
            if (r3 == r4) goto L_0x0022
            goto L_0x047f
        L_0x0022:
            java.lang.Object r2 = r1.getItem(r2)
            java.lang.String r2 = (java.lang.String) r2
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
            java.lang.String r3 = "windowBackgroundWhiteBlueText2"
            r0.setColors(r8, r3)
            r3 = 2131624265(0x7f0e0149, float:1.8875705E38)
            java.lang.Object[] r4 = new java.lang.Object[r10]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "+"
            r6.append(r7)
            r6.append(r2)
            java.lang.String r2 = r6.toString()
            java.lang.String r2 = r5.format(r2)
            r4[r9] = r2
            java.lang.String r2 = "AddContactByPhone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            r0.setText(r2, r9)
            goto L_0x047f
        L_0x005c:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0
            int r2 = r2 / r5
            r0.setIndex(r2)
            goto L_0x047f
        L_0x006c:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.HashtagSearchCell r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColor(r3)
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r4 = r2 + -1
            java.lang.Object r3 = r3.get(r4)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            r0.setText(r3)
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r3 = r3.size()
            if (r2 == r3) goto L_0x008d
            r9 = 1
        L_0x008d:
            r0.setNeedDivider(r9)
            goto L_0x047f
        L_0x0092:
            android.view.View r0 = r0.itemView
            r11 = r0
            org.telegram.ui.Cells.DialogCell r11 = (org.telegram.ui.Cells.DialogCell) r11
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r11.setBackgroundColor(r0)
            int r0 = r22.getItemCount()
            int r0 = r0 - r10
            if (r2 == r0) goto L_0x00a6
            r9 = 1
        L_0x00a6:
            r11.useSeparator = r9
            java.lang.Object r0 = r1.getItem(r2)
            r14 = r0
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            long r12 = r14.getDialogId()
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r15 = r0.date
            r16 = 0
            r11.setDialog(r12, r14, r15, r16)
            goto L_0x047f
        L_0x00be:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            boolean r3 = r3.isEmpty()
            r4 = 2131625141(0x7f0e04b5, float:1.8877482E38)
            java.lang.String r5 = "ClearButton"
            if (r3 != 0) goto L_0x00e6
            r2 = 2131626166(0x7f0e08b6, float:1.887956E38)
            java.lang.String r3 = "Hashtags"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda3
            r4.<init>(r1)
            r0.setText(r2, r3, r4)
            goto L_0x047f
        L_0x00e6:
            boolean r3 = r22.isRecentSearchDisplayed()
            if (r3 == 0) goto L_0x015c
            boolean r3 = r1.searchWas
            if (r3 != 0) goto L_0x0100
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0100
            r3 = 1
            goto L_0x0101
        L_0x0100:
            r3 = 0
        L_0x0101:
            if (r2 >= r3) goto L_0x0110
            r2 = 2131625017(0x7f0e0439, float:1.887723E38)
            java.lang.String r3 = "ChatHints"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            return
        L_0x0110:
            if (r2 != r3) goto L_0x0142
            boolean r2 = r1.searchWas
            r3 = 2131627908(0x7f0e0var_, float:1.8883094E38)
            java.lang.String r6 = "Recent"
            if (r2 != 0) goto L_0x012c
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r3)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda2 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda2
            r4.<init>(r1)
            r0.setText(r2, r3, r4)
            goto L_0x0141
        L_0x012c:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r3 = 2131625140(0x7f0e04b4, float:1.887748E38)
            java.lang.String r4 = "Clear"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda1
            r4.<init>(r1)
            r0.setText(r2, r3, r4)
        L_0x0141:
            return
        L_0x0142:
            int r3 = r22.getRecentItemsCount()
            if (r2 != r3) goto L_0x0155
            r2 = 2131628143(0x7f0e106f, float:1.888357E38)
            java.lang.String r3 = "SearchAllChatsShort"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            return
        L_0x0155:
            int r3 = r22.getRecentItemsCount()
            int r3 = r2 - r3
            goto L_0x015d
        L_0x015c:
            r3 = r2
        L_0x015d:
            org.telegram.ui.Adapters.SearchAdapterHelper r4 = r1.searchAdapterHelper
            java.util.ArrayList r4 = r4.getGlobalSearch()
            java.util.ArrayList<java.lang.Object> r5 = r1.searchResult
            int r5 = r5.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r11 = r1.searchAdapterHelper
            java.util.ArrayList r11 = r11.getLocalServerSearch()
            int r11 = r11.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
            java.util.ArrayList r12 = r12.getPhoneSearch()
            int r12 = r12.size()
            if (r12 <= r7) goto L_0x0184
            boolean r13 = r1.phoneCollapsed
            if (r13 == 0) goto L_0x0184
            r12 = 3
        L_0x0184:
            boolean r13 = r4.isEmpty()
            if (r13 == 0) goto L_0x018c
            r13 = 0
            goto L_0x0191
        L_0x018c:
            int r13 = r4.size()
            int r13 = r13 + r10
        L_0x0191:
            if (r13 <= r6) goto L_0x0198
            boolean r10 = r1.globalSearchCollapsed
            if (r10 == 0) goto L_0x0198
            goto L_0x0199
        L_0x0198:
            r6 = r13
        L_0x0199:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r1.searchResultMessages
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x01a2
            goto L_0x01a7
        L_0x01a2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r1.searchResultMessages
            r10.size()
        L_0x01a7:
            int r5 = r5 + r11
            int r3 = r3 - r5
            if (r3 < 0) goto L_0x01ca
            if (r3 >= r12) goto L_0x01ca
            r2 = 2131627542(0x7f0e0e16, float:1.8882351E38)
            java.lang.String r3 = "PhoneNumberSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getPhoneSearch()
            int r3 = r3.size()
            if (r3 <= r7) goto L_0x01f6
            boolean r9 = r1.phoneCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda18 r8 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda18
            r8.<init>(r1, r0)
            goto L_0x01f6
        L_0x01ca:
            int r3 = r3 - r12
            if (r3 < 0) goto L_0x01ed
            if (r3 >= r6) goto L_0x01ed
            r3 = 2131626116(0x7f0e0884, float:1.887946E38)
            java.lang.String r5 = "GlobalSearch"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r1.searchAdapterHelper
            java.util.ArrayList r5 = r5.getGlobalSearch()
            int r5 = r5.size()
            if (r5 <= r7) goto L_0x01eb
            boolean r9 = r1.globalSearchCollapsed
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda17 r8 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda17
            r8.<init>(r1, r4, r2, r0)
        L_0x01eb:
            r2 = r3
            goto L_0x01f6
        L_0x01ed:
            r2 = 2131628169(0x7f0e1089, float:1.8883623E38)
            java.lang.String r3 = "SearchMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x01f6:
            if (r8 != 0) goto L_0x01fd
            r0.setText(r2)
            goto L_0x047f
        L_0x01fd:
            if (r9 == 0) goto L_0x0205
            r3 = 2131628390(0x7f0e1166, float:1.8884071E38)
            java.lang.String r4 = "ShowMore"
            goto L_0x020a
        L_0x0205:
            r3 = 2131628389(0x7f0e1165, float:1.888407E38)
            java.lang.String r4 = "ShowLess"
        L_0x020a:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$$ExternalSyntheticLambda0
            r4.<init>(r8)
            r0.setText(r2, r3, r4)
            goto L_0x047f
        L_0x0218:
            android.view.View r0 = r0.itemView
            r3 = r0
            org.telegram.ui.Cells.ProfileSearchCell r3 = (org.telegram.ui.Cells.ProfileSearchCell) r3
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setBackgroundColor(r0)
            long r18 = r3.getDialogId()
            java.lang.Object r0 = r1.getItem(r2)
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r4 == 0) goto L_0x0239
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r4 = r0.username
            r12 = r4
            r11 = r8
            r13 = r11
            r4 = r0
            goto L_0x0288
        L_0x0239:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r4 == 0) goto L_0x025a
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            long r11 = r0.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r11)
            if (r4 != 0) goto L_0x0252
            goto L_0x0253
        L_0x0252:
            r0 = r4
        L_0x0253:
            java.lang.String r4 = r0.username
            r11 = r0
            r12 = r4
            r4 = r8
            r13 = r4
            goto L_0x0288
        L_0x025a:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r4 == 0) goto L_0x0284
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC$EncryptedChat) r0
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r4.getEncryptedChat(r0)
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r11 = r0.user_id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r11)
            r13 = r0
            r11 = r8
            r12 = r11
            goto L_0x0288
        L_0x0284:
            r4 = r8
            r11 = r4
            r12 = r11
            r13 = r12
        L_0x0288:
            boolean r0 = r22.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x02a9
            int r0 = r22.getRecentItemsCount()
            if (r2 >= r0) goto L_0x02a2
            int r0 = r22.getRecentItemsCount()
            int r0 = r0 - r10
            if (r2 == r0) goto L_0x029d
            r0 = 1
            goto L_0x029e
        L_0x029d:
            r0 = 0
        L_0x029e:
            r3.useSeparator = r0
            r0 = 1
            goto L_0x02a3
        L_0x02a2:
            r0 = 0
        L_0x02a3:
            int r14 = r22.getRecentItemsCount()
            int r2 = r2 - r14
            goto L_0x02aa
        L_0x02a9:
            r0 = 0
        L_0x02aa:
            org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
            java.util.ArrayList r14 = r14.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r15 = r1.searchAdapterHelper
            java.util.ArrayList r15 = r15.getPhoneSearch()
            java.util.ArrayList<java.lang.Object> r8 = r1.searchResult
            int r8 = r8.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r1.searchAdapterHelper
            java.util.ArrayList r5 = r5.getLocalServerSearch()
            int r5 = r5.size()
            int r20 = r8 + r5
            if (r20 <= 0) goto L_0x02d2
            int r20 = r22.getRecentItemsCount()
            if (r20 <= 0) goto L_0x02d2
            int r2 = r2 + -1
        L_0x02d2:
            int r9 = r15.size()
            if (r9 <= r7) goto L_0x02dd
            boolean r7 = r1.phoneCollapsed
            if (r7 == 0) goto L_0x02dd
            r9 = 3
        L_0x02dd:
            if (r9 <= 0) goto L_0x02ec
            int r7 = r9 + -1
            java.lang.Object r7 = r15.get(r7)
            boolean r7 = r7 instanceof java.lang.String
            if (r7 == 0) goto L_0x02ec
            int r7 = r9 + -2
            goto L_0x02ed
        L_0x02ec:
            r7 = r9
        L_0x02ed:
            boolean r15 = r14.isEmpty()
            if (r15 == 0) goto L_0x02f5
            r14 = 0
            goto L_0x02fa
        L_0x02f5:
            int r14 = r14.size()
            int r14 = r14 + r10
        L_0x02fa:
            if (r14 <= r6) goto L_0x0301
            boolean r15 = r1.globalSearchCollapsed
            if (r15 == 0) goto L_0x0301
            goto L_0x0302
        L_0x0301:
            r6 = r14
        L_0x0302:
            if (r0 != 0) goto L_0x0320
            int r14 = r22.getItemCount()
            int r15 = r22.getRecentItemsCount()
            int r14 = r14 - r15
            int r14 = r14 - r10
            if (r2 == r14) goto L_0x031d
            int r7 = r7 + r8
            int r7 = r7 + r5
            int r7 = r7 - r10
            if (r2 == r7) goto L_0x031d
            int r8 = r8 + r6
            int r8 = r8 + r9
            int r8 = r8 + r5
            int r8 = r8 - r10
            if (r2 == r8) goto L_0x031d
            r5 = 1
            goto L_0x031e
        L_0x031d:
            r5 = 0
        L_0x031e:
            r3.useSeparator = r5
        L_0x0320:
            java.lang.String r5 = "@"
            if (r2 < 0) goto L_0x0363
            java.util.ArrayList<java.lang.Object> r6 = r1.searchResult
            int r6 = r6.size()
            if (r2 >= r6) goto L_0x0363
            if (r4 != 0) goto L_0x0363
            java.util.ArrayList<java.lang.CharSequence> r6 = r1.searchResultNames
            java.lang.Object r2 = r6.get(r2)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            if (r2 == 0) goto L_0x0360
            if (r4 == 0) goto L_0x0360
            java.lang.String r6 = r4.username
            if (r6 == 0) goto L_0x0360
            int r6 = r6.length()
            if (r6 <= 0) goto L_0x0360
            java.lang.String r6 = r2.toString()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            java.lang.String r8 = r4.username
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            boolean r6 = r6.startsWith(r7)
            if (r6 == 0) goto L_0x0360
            goto L_0x0364
        L_0x0360:
            r6 = r2
            r2 = 0
            goto L_0x0365
        L_0x0363:
            r2 = 0
        L_0x0364:
            r6 = 0
        L_0x0365:
            if (r2 != 0) goto L_0x03e8
            if (r0 == 0) goto L_0x036c
            java.lang.String r0 = r1.filteredRecentQuery
            goto L_0x0372
        L_0x036c:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
        L_0x0372:
            boolean r7 = android.text.TextUtils.isEmpty(r0)
            if (r7 != 0) goto L_0x03e8
            if (r4 == 0) goto L_0x0383
            java.lang.String r7 = r4.first_name
            java.lang.String r8 = r4.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
            goto L_0x0389
        L_0x0383:
            if (r11 == 0) goto L_0x0388
            java.lang.String r7 = r11.title
            goto L_0x0389
        L_0x0388:
            r7 = 0
        L_0x0389:
            r8 = 33
            java.lang.String r9 = "windowBackgroundWhiteBlueText4"
            r14 = -1
            if (r7 == 0) goto L_0x03a9
            int r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r7, r0)
            if (r15 == r14) goto L_0x03a9
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            r6.<init>(r7)
            org.telegram.ui.Components.ForegroundColorSpanThemable r7 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            r7.<init>(r9)
            int r21 = r0.length()
            int r14 = r15 + r21
            r6.setSpan(r7, r15, r14, r8)
        L_0x03a9:
            if (r12 == 0) goto L_0x03e8
            if (r4 != 0) goto L_0x03e8
            boolean r2 = r0.startsWith(r5)
            if (r2 == 0) goto L_0x03b7
            java.lang.String r0 = r0.substring(r10)
        L_0x03b7:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x03e3 }
            r2.<init>()     // Catch:{ Exception -> 0x03e3 }
            r2.append(r5)     // Catch:{ Exception -> 0x03e3 }
            r2.append(r12)     // Catch:{ Exception -> 0x03e3 }
            int r5 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r12, r0)     // Catch:{ Exception -> 0x03e3 }
            r7 = -1
            if (r5 == r7) goto L_0x03cb
            r7 = 1
            goto L_0x03cc
        L_0x03cb:
            r7 = 0
        L_0x03cc:
            if (r7 == 0) goto L_0x03e8
            int r0 = r0.length()     // Catch:{ Exception -> 0x03e3 }
            if (r5 != 0) goto L_0x03d7
            int r0 = r0 + 1
            goto L_0x03d9
        L_0x03d7:
            int r5 = r5 + 1
        L_0x03d9:
            org.telegram.ui.Components.ForegroundColorSpanThemable r7 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x03e3 }
            r7.<init>(r9)     // Catch:{ Exception -> 0x03e3 }
            int r0 = r0 + r5
            r2.setSpan(r7, r5, r0, r8)     // Catch:{ Exception -> 0x03e3 }
            goto L_0x03e8
        L_0x03e3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03e9
        L_0x03e8:
            r12 = r2
        L_0x03e9:
            r2 = 0
            r3.setChecked(r2, r2)
            if (r4 == 0) goto L_0x0404
            long r7 = r4.id
            long r14 = r1.selfUserId
            int r0 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x0404
            r0 = 2131628127(0x7f0e105f, float:1.8883538E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r14 = r0
            r0 = 1
            r8 = 0
            goto L_0x0407
        L_0x0404:
            r14 = r6
            r8 = r12
            r0 = 0
        L_0x0407:
            if (r11 == 0) goto L_0x0458
            int r2 = r11.participants_count
            if (r2 == 0) goto L_0x0458
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r2 == 0) goto L_0x0423
            boolean r2 = r11.megagroup
            if (r2 != 0) goto L_0x0423
            int r2 = r11.participants_count
            r5 = 0
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r7 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2, r6)
            goto L_0x042e
        L_0x0423:
            r5 = 0
            int r2 = r11.participants_count
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.String r5 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2, r6)
        L_0x042e:
            boolean r5 = r8 instanceof android.text.SpannableStringBuilder
            java.lang.String r6 = ", "
            if (r5 == 0) goto L_0x043f
            r5 = r8
            android.text.SpannableStringBuilder r5 = (android.text.SpannableStringBuilder) r5
            android.text.SpannableStringBuilder r5 = r5.append(r6)
            r5.append(r2)
            goto L_0x0458
        L_0x043f:
            boolean r5 = android.text.TextUtils.isEmpty(r8)
            if (r5 != 0) goto L_0x0455
            r5 = 3
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r5]
            r7 = 0
            r5[r7] = r8
            r5[r10] = r6
            r6 = 2
            r5[r6] = r2
            java.lang.CharSequence r2 = android.text.TextUtils.concat(r5)
            goto L_0x0456
        L_0x0455:
            r7 = 0
        L_0x0456:
            r15 = r2
            goto L_0x045a
        L_0x0458:
            r7 = 0
            r15 = r8
        L_0x045a:
            if (r4 == 0) goto L_0x045e
            r12 = r4
            goto L_0x045f
        L_0x045e:
            r12 = r11
        L_0x045f:
            r16 = 1
            r11 = r3
            r17 = r0
            r11.setData(r12, r13, r14, r15, r16, r17)
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogsSearchAdapterDelegate r0 = r1.delegate
            long r4 = r3.getDialogId()
            boolean r0 = r0.isSelected(r4)
            long r4 = r3.getDialogId()
            int r2 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x047b
            r9 = 1
            goto L_0x047c
        L_0x047b:
            r9 = 0
        L_0x047c:
            r3.setChecked(r0, r9)
        L_0x047f:
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
    public /* synthetic */ void lambda$onBindViewHolder$19(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$20(GraySectionCell graySectionCell) {
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
        graySectionCell.setRightText(LocaleController.getString(str, i));
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$23(ArrayList arrayList, int i, GraySectionCell graySectionCell) {
        String str;
        int i2;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.lastShowMoreUpdate >= 300) {
            this.lastShowMoreUpdate = elapsedRealtime;
            int size = arrayList.isEmpty() ? 0 : arrayList.size();
            boolean z = getItemCount() > (Math.min(size, this.globalSearchCollapsed ? 4 : Integer.MAX_VALUE) + i) + 1;
            DefaultItemAnimator defaultItemAnimator = this.itemAnimator;
            if (defaultItemAnimator != null) {
                long j = 200;
                defaultItemAnimator.setAddDuration(z ? 45 : 200);
                DefaultItemAnimator defaultItemAnimator2 = this.itemAnimator;
                if (z) {
                    j = 80;
                }
                defaultItemAnimator2.setRemoveDuration(j);
                this.itemAnimator.setRemoveDelay(z ? 270 : 0);
            }
            boolean z2 = !this.globalSearchCollapsed;
            this.globalSearchCollapsed = z2;
            if (z2) {
                i2 = NUM;
                str = "ShowMore";
            } else {
                i2 = NUM;
                str = "ShowLess";
            }
            graySectionCell.setRightText(LocaleController.getString(str, i2), this.globalSearchCollapsed);
            this.showMoreHeader = null;
            View view = (View) graySectionCell.getParent();
            if (view instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) view;
                int i3 = !this.globalSearchCollapsed ? i + 4 : i + size + 1;
                int i4 = 0;
                while (true) {
                    if (i4 >= recyclerView.getChildCount()) {
                        break;
                    }
                    View childAt = recyclerView.getChildAt(i4);
                    if (recyclerView.getChildAdapterPosition(childAt) == i3) {
                        this.showMoreHeader = childAt;
                        break;
                    }
                    i4++;
                }
            }
            if (!this.globalSearchCollapsed) {
                notifyItemChanged(i + 3);
                notifyItemRangeInserted(i + 4, size - 3);
            } else {
                notifyItemRangeRemoved(i + 4, size - 3);
                if (z) {
                    AndroidUtilities.runOnUIThread(new DialogsSearchAdapter$$ExternalSyntheticLambda7(this, i), 350);
                } else {
                    notifyItemChanged(i + 3);
                }
            }
            Runnable runnable = this.cancelShowMoreAnimation;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (z) {
                this.showMoreAnimation = true;
                DialogsSearchAdapter$$ExternalSyntheticLambda13 dialogsSearchAdapter$$ExternalSyntheticLambda13 = new DialogsSearchAdapter$$ExternalSyntheticLambda13(this, view);
                this.cancelShowMoreAnimation = dialogsSearchAdapter$$ExternalSyntheticLambda13;
                AndroidUtilities.runOnUIThread(dialogsSearchAdapter$$ExternalSyntheticLambda13, 400);
                return;
            }
            this.showMoreAnimation = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$21(int i) {
        notifyItemChanged(i + 3);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$22(View view) {
        this.showMoreAnimation = false;
        this.showMoreHeader = null;
        if (view != null) {
            view.invalidate();
        }
    }

    public int getItemViewType(int i) {
        int i2 = 4;
        if (!this.searchResultHashtags.isEmpty()) {
            return i == 0 ? 1 : 4;
        }
        if (isRecentSearchDisplayed()) {
            int i3 = (this.searchWas || MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) ? 0 : 1;
            if (i < i3) {
                return 5;
            }
            if (i == i3) {
                return 1;
            }
            if (i < getRecentItemsCount()) {
                return 0;
            }
            i -= getRecentItemsCount();
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
        if (size + size2 > 0 && getRecentItemsCount() > 0) {
            if (i == 0) {
                return 1;
            }
            i--;
        }
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (size3 > 3 && this.phoneCollapsed) {
            size3 = 3;
        }
        int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        if (size4 <= 4 || !this.globalSearchCollapsed) {
            i2 = size4;
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
            if (i6 < 0 || i6 >= i2) {
                int i7 = i6 - i2;
                if (i7 < 0 || i7 >= size5) {
                    return 3;
                }
                if (i7 == 0) {
                    return 1;
                }
                return 2;
            } else if (i6 == 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            Object item = getItem(i5);
            if (!(item instanceof String)) {
                return 0;
            }
            if ("section".equals((String) item)) {
                return 1;
            }
            return 6;
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

    public void filterRecent(String str) {
        TLObject tLObject;
        String str2;
        this.filteredRecentQuery = str;
        this.filteredRecentSearchObjects.clear();
        if (!TextUtils.isEmpty(str)) {
            String lowerCase = str.toLowerCase();
            int size = this.recentSearchObjects.size();
            for (int i = 0; i < size; i++) {
                RecentSearchObject recentSearchObject = this.recentSearchObjects.get(i);
                if (!(recentSearchObject == null || (tLObject = recentSearchObject.object) == null)) {
                    String str3 = null;
                    if (tLObject instanceof TLRPC$Chat) {
                        str3 = ((TLRPC$Chat) tLObject).title;
                        str2 = ((TLRPC$Chat) tLObject).username;
                    } else if (tLObject instanceof TLRPC$User) {
                        str3 = UserObject.getUserName((TLRPC$User) tLObject);
                        str2 = ((TLRPC$User) recentSearchObject.object).username;
                    } else if (tLObject instanceof TLRPC$ChatInvite) {
                        str3 = ((TLRPC$ChatInvite) tLObject).title;
                        str2 = null;
                    } else {
                        str2 = null;
                    }
                    if ((str3 != null && wordStartsWith(str3.toLowerCase(), lowerCase)) || (str2 != null && wordStartsWith(str2.toLowerCase(), lowerCase))) {
                        this.filteredRecentSearchObjects.add(recentSearchObject);
                    }
                    if (this.filteredRecentSearchObjects.size() >= 5) {
                        return;
                    }
                }
            }
        }
    }

    private boolean wordStartsWith(String str, String str2) {
        if (str2 == null || str == null) {
            return false;
        }
        String[] split = str.toLowerCase().split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i] != null && (split[i].startsWith(str2) || str2.startsWith(split[i]))) {
                return true;
            }
        }
        return false;
    }
}
