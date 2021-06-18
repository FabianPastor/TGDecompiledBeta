package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.tgnet.RequestDelegate;
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
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
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
    /* access modifiers changed from: private */
    public Context mContext;
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
    private int selfUserId;
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

        void needRemoveHint(int i);

        void runResultsEnterAnimation();

        void searchStateChanged(boolean z, boolean z2);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isSearching() {
        return this.waitingResponseCount > 0;
    }

    protected static class RecentSearchObject {
        int date;
        long did;
        TLObject object;

        protected RecentSearchObject() {
        }
    }

    private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private CategoryAdapterRecycler() {
        }

        public void setIndex(int i) {
            notifyDataSetChanged();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            HintDialogCell hintDialogCell = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            hintDialogCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new RecyclerListView.Holder(hintDialogCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$Chat tLRPC$Chat;
            String str;
            HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
            TLRPC$TL_topPeer tLRPC$TL_topPeer = MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(i);
            new TLRPC$TL_dialog();
            TLRPC$Peer tLRPC$Peer = tLRPC$TL_topPeer.peer;
            int i2 = tLRPC$Peer.user_id;
            TLRPC$User tLRPC$User = null;
            if (i2 != 0) {
                tLRPC$User = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(tLRPC$TL_topPeer.peer.user_id));
                tLRPC$Chat = null;
            } else {
                int i3 = tLRPC$Peer.channel_id;
                if (i3 != 0) {
                    i2 = -i3;
                    tLRPC$Chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tLRPC$TL_topPeer.peer.channel_id));
                } else {
                    int i4 = tLRPC$Peer.chat_id;
                    if (i4 != 0) {
                        i2 = -i4;
                        tLRPC$Chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tLRPC$TL_topPeer.peer.chat_id));
                    } else {
                        i2 = 0;
                        tLRPC$Chat = null;
                    }
                }
            }
            hintDialogCell.setTag(Integer.valueOf(i2));
            if (tLRPC$User != null) {
                str = UserObject.getFirstName(tLRPC$User);
            } else {
                str = tLRPC$Chat != null ? tLRPC$Chat.title : "";
            }
            hintDialogCell.setDialog(i2, true, str);
        }

        public int getItemCount() {
            return MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int i, int i2, int i3) {
        this.folderId = i3;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ SparseArray getExcludeUsers() {
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
        int i2;
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
                TLRPC$Peer tLRPC$Peer = messageObject.messageOwner.peer_id;
                int i3 = tLRPC$Peer.channel_id;
                if (i3 == 0 && (i3 = tLRPC$Peer.chat_id) == 0) {
                    i2 = tLRPC$Peer.user_id;
                } else {
                    i2 = -i3;
                }
                tLRPC$TL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i2);
            }
            this.lastMessagesSearchString = str;
            int i4 = this.lastReqId + 1;
            this.lastReqId = i4;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_searchGlobal, new RequestDelegate(str, i4, i, tLRPC$TL_messages_searchGlobal) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    DialogsSearchAdapter.this.lambda$searchMessagesInternal$1$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchMessagesInternal$1 */
    public /* synthetic */ void lambda$searchMessagesInternal$1$DialogsSearchAdapter(String str, int i, int i2, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList = new ArrayList();
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            SparseArray sparseArray = new SparseArray();
            SparseArray sparseArray2 = new SparseArray();
            for (int i3 = 0; i3 < tLRPC$messages_Messages.chats.size(); i3++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$messages_Messages.chats.get(i3);
                sparseArray.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            for (int i4 = 0; i4 < tLRPC$messages_Messages.users.size(); i4++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i4);
                sparseArray2.put(tLRPC$User.id, tLRPC$User);
            }
            for (int i5 = 0; i5 < tLRPC$messages_Messages.messages.size(); i5++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i5), (SparseArray<TLRPC$User>) sparseArray2, (SparseArray<TLRPC$Chat>) sparseArray, false, true);
                arrayList.add(messageObject);
                String str2 = str;
                messageObject.setQuery(str);
            }
        }
        String str3 = str;
        AndroidUtilities.runOnUIThread(new Runnable(i, i2, tLRPC$TL_error, str, tLObject, tLRPC$TL_messages_searchGlobal, arrayList) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;
            public final /* synthetic */ String f$4;
            public final /* synthetic */ TLObject f$5;
            public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$6;
            public final /* synthetic */ ArrayList f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$searchMessagesInternal$0$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchMessagesInternal$0 */
    public /* synthetic */ void lambda$searchMessagesInternal$0$DialogsSearchAdapter(int i, int i2, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, ArrayList arrayList) {
        if (i == this.lastReqId && (i2 <= 0 || i2 == this.lastSearchId)) {
            this.waitingResponseCount--;
            if (tLRPC$TL_error == null) {
                this.currentMessagesQuery = str;
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
                    Integer num = MessagesController.getInstance(this.currentAccount).deletedHistory.get(MessageObject.getDialogId(tLRPC$Message));
                    if (num == null || tLRPC$Message.id > num.intValue()) {
                        this.searchResultMessages.add((MessageObject) arrayList.get(i3));
                        long dialogId = MessageObject.getDialogId(tLRPC$Message);
                        ConcurrentHashMap<Long, Integer> concurrentHashMap = tLRPC$Message.out ? MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(this.currentAccount).dialogs_read_inbox_max;
                        Integer num2 = (Integer) concurrentHashMap.get(Long.valueOf(dialogId));
                        if (num2 == null) {
                            num2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(tLRPC$Message.out, dialogId));
                            concurrentHashMap.put(Long.valueOf(dialogId), num2);
                        }
                        tLRPC$Message.unread = num2.intValue() < tLRPC$Message.id;
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$loadRecentSearch$4$DialogsSearchAdapter();
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008d A[Catch:{ Exception -> 0x016b }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0031 A[SYNTHETIC] */
    /* renamed from: lambda$loadRecentSearch$4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadRecentSearch$4$DialogsSearchAdapter() {
        /*
            r13 = this;
            int r0 = r13.currentAccount     // Catch:{ Exception -> 0x016b }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x016b }
            org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x016b }
            java.lang.String r1 = "SELECT did, date FROM search_recent WHERE 1"
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x016b }
            org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r1, r3)     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r1.<init>()     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r3.<init>()     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r4.<init>()     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r5.<init>()     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r5.<init>()     // Catch:{ Exception -> 0x016b }
            android.util.LongSparseArray r6 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x016b }
            r6.<init>()     // Catch:{ Exception -> 0x016b }
        L_0x0031:
            boolean r7 = r0.next()     // Catch:{ Exception -> 0x016b }
            r8 = 32
            if (r7 == 0) goto L_0x00a3
            long r9 = r0.longValue(r2)     // Catch:{ Exception -> 0x016b }
            int r7 = (int) r9     // Catch:{ Exception -> 0x016b }
            long r11 = r9 >> r8
            int r8 = (int) r11     // Catch:{ Exception -> 0x016b }
            r11 = 1
            if (r7 == 0) goto L_0x0071
            if (r7 <= 0) goto L_0x005d
            int r8 = r13.dialogsType     // Catch:{ Exception -> 0x016b }
            r12 = 2
            if (r8 == r12) goto L_0x008a
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016b }
            boolean r8 = r1.contains(r8)     // Catch:{ Exception -> 0x016b }
            if (r8 != 0) goto L_0x008a
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016b }
            r1.add(r7)     // Catch:{ Exception -> 0x016b }
            goto L_0x006f
        L_0x005d:
            int r7 = -r7
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016b }
            boolean r8 = r3.contains(r8)     // Catch:{ Exception -> 0x016b }
            if (r8 != 0) goto L_0x008a
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016b }
            r3.add(r7)     // Catch:{ Exception -> 0x016b }
        L_0x006f:
            r7 = 1
            goto L_0x008b
        L_0x0071:
            int r7 = r13.dialogsType     // Catch:{ Exception -> 0x016b }
            if (r7 == 0) goto L_0x0078
            r12 = 3
            if (r7 != r12) goto L_0x008a
        L_0x0078:
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x016b }
            boolean r7 = r4.contains(r7)     // Catch:{ Exception -> 0x016b }
            if (r7 != 0) goto L_0x008a
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x016b }
            r4.add(r7)     // Catch:{ Exception -> 0x016b }
            goto L_0x006f
        L_0x008a:
            r7 = 0
        L_0x008b:
            if (r7 == 0) goto L_0x0031
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject     // Catch:{ Exception -> 0x016b }
            r7.<init>()     // Catch:{ Exception -> 0x016b }
            r7.did = r9     // Catch:{ Exception -> 0x016b }
            int r8 = r0.intValue(r11)     // Catch:{ Exception -> 0x016b }
            r7.date = r8     // Catch:{ Exception -> 0x016b }
            r5.add(r7)     // Catch:{ Exception -> 0x016b }
            long r8 = r7.did     // Catch:{ Exception -> 0x016b }
            r6.put(r8, r7)     // Catch:{ Exception -> 0x016b }
            goto L_0x0031
        L_0x00a3:
            r0.dispose()     // Catch:{ Exception -> 0x016b }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r0.<init>()     // Catch:{ Exception -> 0x016b }
            boolean r7 = r4.isEmpty()     // Catch:{ Exception -> 0x016b }
            java.lang.String r9 = ","
            if (r7 != 0) goto L_0x00e7
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r7.<init>()     // Catch:{ Exception -> 0x016b }
            int r10 = r13.currentAccount     // Catch:{ Exception -> 0x016b }
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x016b }
            java.lang.String r4 = android.text.TextUtils.join(r9, r4)     // Catch:{ Exception -> 0x016b }
            r10.getEncryptedChatsInternal(r4, r7, r1)     // Catch:{ Exception -> 0x016b }
            r4 = 0
        L_0x00c6:
            int r10 = r7.size()     // Catch:{ Exception -> 0x016b }
            if (r4 >= r10) goto L_0x00e7
            java.lang.Object r10 = r7.get(r4)     // Catch:{ Exception -> 0x016b }
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = (org.telegram.tgnet.TLRPC$EncryptedChat) r10     // Catch:{ Exception -> 0x016b }
            int r10 = r10.id     // Catch:{ Exception -> 0x016b }
            long r10 = (long) r10     // Catch:{ Exception -> 0x016b }
            long r10 = r10 << r8
            java.lang.Object r10 = r6.get(r10)     // Catch:{ Exception -> 0x016b }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r10     // Catch:{ Exception -> 0x016b }
            java.lang.Object r11 = r7.get(r4)     // Catch:{ Exception -> 0x016b }
            org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11     // Catch:{ Exception -> 0x016b }
            r10.object = r11     // Catch:{ Exception -> 0x016b }
            int r4 = r4 + 1
            goto L_0x00c6
        L_0x00e7:
            boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x016b }
            if (r4 != 0) goto L_0x012e
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x016b }
            r4.<init>()     // Catch:{ Exception -> 0x016b }
            int r7 = r13.currentAccount     // Catch:{ Exception -> 0x016b }
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)     // Catch:{ Exception -> 0x016b }
            java.lang.String r3 = android.text.TextUtils.join(r9, r3)     // Catch:{ Exception -> 0x016b }
            r7.getChatsInternal(r3, r4)     // Catch:{ Exception -> 0x016b }
            r3 = 0
        L_0x0100:
            int r7 = r4.size()     // Catch:{ Exception -> 0x016b }
            if (r3 >= r7) goto L_0x012e
            java.lang.Object r7 = r4.get(r3)     // Catch:{ Exception -> 0x016b }
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7     // Catch:{ Exception -> 0x016b }
            int r8 = r7.id     // Catch:{ Exception -> 0x016b }
            int r8 = -r8
            long r10 = (long) r8     // Catch:{ Exception -> 0x016b }
            org.telegram.tgnet.TLRPC$InputChannel r8 = r7.migrated_to     // Catch:{ Exception -> 0x016b }
            if (r8 == 0) goto L_0x0123
            java.lang.Object r7 = r6.get(r10)     // Catch:{ Exception -> 0x016b }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r7 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r7     // Catch:{ Exception -> 0x016b }
            r6.remove(r10)     // Catch:{ Exception -> 0x016b }
            if (r7 == 0) goto L_0x012b
            r5.remove(r7)     // Catch:{ Exception -> 0x016b }
            goto L_0x012b
        L_0x0123:
            java.lang.Object r8 = r6.get(r10)     // Catch:{ Exception -> 0x016b }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r8 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r8     // Catch:{ Exception -> 0x016b }
            r8.object = r7     // Catch:{ Exception -> 0x016b }
        L_0x012b:
            int r3 = r3 + 1
            goto L_0x0100
        L_0x012e:
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x016b }
            if (r3 != 0) goto L_0x015d
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x016b }
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x016b }
            java.lang.String r1 = android.text.TextUtils.join(r9, r1)     // Catch:{ Exception -> 0x016b }
            r3.getUsersInternal(r1, r0)     // Catch:{ Exception -> 0x016b }
        L_0x0141:
            int r1 = r0.size()     // Catch:{ Exception -> 0x016b }
            if (r2 >= r1) goto L_0x015d
            java.lang.Object r1 = r0.get(r2)     // Catch:{ Exception -> 0x016b }
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1     // Catch:{ Exception -> 0x016b }
            int r3 = r1.id     // Catch:{ Exception -> 0x016b }
            long r3 = (long) r3     // Catch:{ Exception -> 0x016b }
            java.lang.Object r3 = r6.get(r3)     // Catch:{ Exception -> 0x016b }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r3     // Catch:{ Exception -> 0x016b }
            if (r3 == 0) goto L_0x015a
            r3.object = r1     // Catch:{ Exception -> 0x016b }
        L_0x015a:
            int r2 = r2 + 1
            goto L_0x0141
        L_0x015d:
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$KZCwPlFSVFfb6qnYhw41i-SJ33Y r0 = org.telegram.ui.Adapters.$$Lambda$DialogsSearchAdapter$KZCwPlFSVFfb6qnYhw41iSJ33Y.INSTANCE     // Catch:{ Exception -> 0x016b }
            java.util.Collections.sort(r5, r0)     // Catch:{ Exception -> 0x016b }
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$1h1ktwdqY4KtlZjilzfKb-hvBwo r0 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$1h1ktwdqY4KtlZjilzfKb-hvBwo     // Catch:{ Exception -> 0x016b }
            r0.<init>(r5, r6)     // Catch:{ Exception -> 0x016b }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x016b }
            goto L_0x016f
        L_0x016b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x016f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.lambda$loadRecentSearch$4$DialogsSearchAdapter():void");
    }

    static /* synthetic */ int lambda$loadRecentSearch$2(RecentSearchObject recentSearchObject, RecentSearchObject recentSearchObject2) {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(j) {
            public final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$putRecentSearch$5$DialogsSearchAdapter(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$putRecentSearch$5 */
    public /* synthetic */ void lambda$putRecentSearch$5$DialogsSearchAdapter(long j) {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$clearRecentSearch$6$DialogsSearchAdapter();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clearRecentSearch$6 */
    public /* synthetic */ void lambda$clearRecentSearch$6$DialogsSearchAdapter() {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(j) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$removeRecentSearch$7$DialogsSearchAdapter(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$removeRecentSearch$7 */
    public /* synthetic */ void lambda$removeRecentSearch$7$DialogsSearchAdapter(long j) {
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
    public void lambda$loadRecentSearch$3(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray) {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(lowerCase, i, str) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$searchDialogsInternal$9$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchDialogsInternal$9 */
    public /* synthetic */ void lambda$searchDialogsInternal$9$DialogsSearchAdapter(String str, int i, String str2) {
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
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$searchDialogsInternal$8$DialogsSearchAdapter();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchDialogsInternal$8 */
    public /* synthetic */ void lambda$searchDialogsInternal$8$DialogsSearchAdapter() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<Object>) null, this.localTipDates, this.localTipArchive);
        }
    }

    private void updateSearchResults(ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<TLRPC$User> arrayList3, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i, arrayList, arrayList3, arrayList2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ ArrayList f$3;
            public final /* synthetic */ ArrayList f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$updateSearchResults$11$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSearchResults$11 */
    public /* synthetic */ void lambda$updateSearchResults$11$DialogsSearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        int i2;
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
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Object obj = arrayList.get(i3);
                if (obj instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) obj;
                    MessagesController.getInstance(this.currentAccount).putUser(tLRPC$User, true);
                    i2 = tLRPC$User.id;
                } else if (obj instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
                    MessagesController.getInstance(this.currentAccount).putChat(tLRPC$Chat, true);
                    i2 = -tLRPC$Chat.id;
                } else {
                    if (obj instanceof TLRPC$EncryptedChat) {
                        MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC$EncryptedChat) obj, true);
                    }
                    i2 = 0;
                }
                if (i2 != 0) {
                    long j = (long) i2;
                    if (MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j) == null) {
                        MessagesStorage.getInstance(this.currentAccount).getDialogFolderId(j, new MessagesStorage.IntCallback(i2, obj) {
                            public final /* synthetic */ int f$1;
                            public final /* synthetic */ Object f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(int i) {
                                DialogsSearchAdapter.this.lambda$updateSearchResults$10$DialogsSearchAdapter(this.f$1, this.f$2, i);
                            }
                        });
                    }
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
    /* renamed from: lambda$updateSearchResults$10 */
    public /* synthetic */ void lambda$updateSearchResults$10$DialogsSearchAdapter(int i, Object obj, int i2) {
        if (i2 != -1) {
            TLRPC$TL_dialog tLRPC$TL_dialog = new TLRPC$TL_dialog();
            long j = (long) i;
            tLRPC$TL_dialog.id = j;
            if (i2 != 0) {
                tLRPC$TL_dialog.folder_id = i2;
            }
            if (obj instanceof TLRPC$Chat) {
                tLRPC$TL_dialog.flags = ChatObject.isChannel((TLRPC$Chat) obj) ? 1 : 0;
            }
            MessagesController.getInstance(this.currentAccount).dialogs_dict.put(j, tLRPC$TL_dialog);
            MessagesController.getInstance(this.currentAccount).getAllDialogs().add(tLRPC$TL_dialog);
            MessagesController.getInstance(this.currentAccount).sortDialogs((SparseArray<TLRPC$Chat>) null);
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
            notifyDataSetChanged();
            DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate3 = this.delegate;
            if (dialogsSearchAdapterDelegate3 != null) {
                dialogsSearchAdapterDelegate3.searchStateChanged(true, false);
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$DialogsSearchAdapter$ViRmrM9sNWpxU0kuNS_6dz4q0 r5 = new Runnable(trim, i5, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$searchDialogs$13$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            };
            this.searchRunnable = r5;
            dispatchQueue.postRunnable(r5, 300);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchDialogs$13 */
    public /* synthetic */ void lambda$searchDialogs$13$DialogsSearchAdapter(String str, int i, String str2) {
        this.searchRunnable = null;
        searchDialogsInternal(str, i);
        $$Lambda$DialogsSearchAdapter$5yi246j9nGiFRvjBscwwMVaLlP0 r0 = new Runnable(i, str, str2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$searchDialogs$12$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        };
        this.searchRunnable2 = r0;
        AndroidUtilities.runOnUIThread(r0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchDialogs$12 */
    public /* synthetic */ void lambda$searchDialogs$12$DialogsSearchAdapter(int i, String str, String str2) {
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
            int size = this.searchAdapterHelper.getLocalServerSearch().size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            int size3 = this.searchAdapterHelper.getPhoneSearch().size();
            int size4 = this.searchResultMessages.size();
            int size5 = this.searchResult.size() + 0 + size;
            if (size2 != 0) {
                size5 += size2 + 1;
            }
            if (size3 != 0) {
                size5 += size3;
            }
            if (size4 != 0) {
                size5 += size4 + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0);
            }
            this.currentItemCount = size5;
            return size5;
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
                obj = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC$User) tLObject).id));
                if (obj == null) {
                    return tLObject;
                }
            } else if (!(tLObject instanceof TLRPC$Chat) || (obj = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((TLRPC$Chat) tLObject).id))) == null) {
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
            int i4 = 0;
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
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
        if (isRecentSearchDisplayed() || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int size = this.searchResult.size();
        int size2 = localServerSearch.size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        int size5 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
        if (i >= 0 && i < size) {
            return false;
        }
        int i2 = i - size;
        if (i2 >= 0 && i2 < size2) {
            return false;
        }
        int i3 = i2 - size2;
        if (i3 > 0 && i3 < size3) {
            return false;
        }
        int i4 = i3 - size3;
        if (i4 > 0 && i4 < size4) {
            return true;
        }
        int i5 = i4 - size4;
        if (i5 <= 0 || i5 < size5) {
        }
        return false;
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 3) ? false : true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$14 */
    public /* synthetic */ void lambda$onCreateViewHolder$14$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog((long) ((Integer) view.getTag()).intValue());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$15 */
    public /* synthetic */ boolean lambda$onCreateViewHolder$15$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Integer) view.getTag()).intValue());
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.TextCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Cells.TextCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: org.telegram.ui.Cells.ProfileSearchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.ui.Cells.GraySectionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: org.telegram.ui.Cells.HashtagSearchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.ui.Cells.TextCell} */
    /* JADX WARNING: type inference failed for: r5v9, types: [org.telegram.ui.Adapters.DialogsSearchAdapter$2, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
        /*
            r4 = this;
            r5 = 1
            r0 = 0
            r1 = 0
            switch(r6) {
                case 0: goto L_0x0078;
                case 1: goto L_0x0070;
                case 2: goto L_0x0067;
                case 3: goto L_0x0059;
                case 4: goto L_0x0051;
                case 5: goto L_0x0012;
                case 6: goto L_0x0008;
                default: goto L_0x0006;
            }
        L_0x0006:
            goto L_0x007f
        L_0x0008:
            org.telegram.ui.Cells.TextCell r1 = new org.telegram.ui.Cells.TextCell
            android.content.Context r5 = r4.mContext
            r2 = 16
            r1.<init>(r5, r2, r0)
            goto L_0x007f
        L_0x0012:
            org.telegram.ui.Adapters.DialogsSearchAdapter$2 r5 = new org.telegram.ui.Adapters.DialogsSearchAdapter$2
            android.content.Context r2 = r4.mContext
            r5.<init>(r2)
            r2 = 9
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r5.setTag(r2)
            r5.setItemAnimator(r1)
            r5.setLayoutAnimation(r1)
            org.telegram.ui.Adapters.DialogsSearchAdapter$3 r2 = new org.telegram.ui.Adapters.DialogsSearchAdapter$3
            android.content.Context r3 = r4.mContext
            r2.<init>(r3)
            r2.setOrientation(r0)
            r5.setLayoutManager(r2)
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = new org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler
            r0.<init>()
            r5.setAdapter(r0)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$ZKrzR5gKYxEcS4YODs986TMHNuA r0 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$ZKrzR5gKYxEcS4YODs986TMHNuA
            r0.<init>()
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r0)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$BOQQlqvoOWt-UalEzfVMCoC2kTQ r0 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$BOQQlqvoOWt-UalEzfVMCoC2kTQ
            r0.<init>()
            r5.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r0)
            r4.innerListView = r5
            r1 = r5
            goto L_0x007f
        L_0x0051:
            org.telegram.ui.Cells.HashtagSearchCell r1 = new org.telegram.ui.Cells.HashtagSearchCell
            android.content.Context r5 = r4.mContext
            r1.<init>(r5)
            goto L_0x007f
        L_0x0059:
            org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r0 = r4.mContext
            r1.<init>(r0)
            r1.setViewType(r5)
            r1.setIsSingleCell(r5)
            goto L_0x007f
        L_0x0067:
            org.telegram.ui.Cells.DialogCell r2 = new org.telegram.ui.Cells.DialogCell
            android.content.Context r3 = r4.mContext
            r2.<init>(r1, r3, r0, r5)
            r1 = r2
            goto L_0x007f
        L_0x0070:
            org.telegram.ui.Cells.GraySectionCell r1 = new org.telegram.ui.Cells.GraySectionCell
            android.content.Context r5 = r4.mContext
            r1.<init>(r5)
            goto L_0x007f
        L_0x0078:
            org.telegram.ui.Cells.ProfileSearchCell r1 = new org.telegram.ui.Cells.ProfileSearchCell
            android.content.Context r5 = r4.mContext
            r1.<init>(r5)
        L_0x007f:
            r5 = 5
            r0 = -1
            if (r6 != r5) goto L_0x0092
            androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r6 = 1118568448(0x42aCLASSNAME, float:86.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.<init>((int) r0, (int) r6)
            r1.setLayoutParams(r5)
            goto L_0x009b
        L_0x0092:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r6 = -2
            r5.<init>((int) r0, (int) r6)
            r1.setLayoutParams(r5)
        L_0x009b:
            org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
            r5.<init>(r1)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: android.text.SpannableStringBuilder} */
    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v2 */
    /* JADX WARNING: type inference failed for: r6v3 */
    /* JADX WARNING: type inference failed for: r6v6 */
    /* JADX WARNING: type inference failed for: r6v8 */
    /* JADX WARNING: type inference failed for: r6v9 */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x028e, code lost:
        if (r0.startsWith("@" + r8.username) != false) goto L_0x0290;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0372  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x038b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
        /*
            r18 = this;
            r1 = r18
            r0 = r19
            r2 = r20
            int r3 = r19.getItemViewType()
            r4 = 2
            r5 = 0
            r6 = 0
            r7 = 1
            if (r3 == 0) goto L_0x017e
            if (r3 == r7) goto L_0x00ac
            if (r3 == r4) goto L_0x0088
            r8 = 4
            if (r3 == r8) goto L_0x0069
            r8 = 5
            if (r3 == r8) goto L_0x0059
            r4 = 6
            if (r3 == r4) goto L_0x001f
            goto L_0x038f
        L_0x001f:
            java.lang.Object r2 = r1.getItem(r2)
            java.lang.String r2 = (java.lang.String) r2
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
            java.lang.String r3 = "windowBackgroundWhiteBlueText2"
            r0.setColors(r5, r3)
            r3 = 2131624194(0x7f0e0102, float:1.887556E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "+"
            r7.append(r8)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            java.lang.String r2 = r5.format(r2)
            r4[r6] = r2
            java.lang.String r2 = "AddContactByPhone"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4)
            r0.setText(r2, r6)
            goto L_0x038f
        L_0x0059:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0
            int r2 = r2 / r4
            r0.setIndex(r2)
            goto L_0x038f
        L_0x0069:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.HashtagSearchCell r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r4 = r2 + -1
            java.lang.Object r3 = r3.get(r4)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            r0.setText(r3)
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            int r3 = r3.size()
            if (r2 == r3) goto L_0x0083
            r6 = 1
        L_0x0083:
            r0.setNeedDivider(r6)
            goto L_0x038f
        L_0x0088:
            android.view.View r0 = r0.itemView
            r8 = r0
            org.telegram.ui.Cells.DialogCell r8 = (org.telegram.ui.Cells.DialogCell) r8
            int r0 = r18.getItemCount()
            int r0 = r0 - r7
            if (r2 == r0) goto L_0x0095
            r6 = 1
        L_0x0095:
            r8.useSeparator = r6
            java.lang.Object r0 = r1.getItem(r2)
            r11 = r0
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            long r9 = r11.getDialogId()
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            int r12 = r0.date
            r13 = 0
            r8.setDialog(r9, r11, r12, r13)
            goto L_0x038f
        L_0x00ac:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            boolean r3 = r18.isRecentSearchDisplayed()
            r4 = 2131624906(0x7f0e03ca, float:1.8877005E38)
            java.lang.String r5 = "ClearButton"
            if (r3 == 0) goto L_0x00ef
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
            boolean r3 = r3.isEmpty()
            r3 = r3 ^ r7
            if (r2 >= r3) goto L_0x00d8
            r2 = 2131624821(0x7f0e0375, float:1.8876833E38)
            java.lang.String r3 = "ChatHints"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x038f
        L_0x00d8:
            r2 = 2131627146(0x7f0e0c8a, float:1.8881548E38)
            java.lang.String r3 = "Recent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$yure0aBWpre3NLHaJXqEvIAjw9Y r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$yure0aBWpre3NLHaJXqEvIAjw9Y
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x038f
        L_0x00ef:
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x010e
            r2 = 2131625757(0x7f0e071d, float:1.887873E38)
            java.lang.String r3 = "Hashtags"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$o79XLwWlVObmQNA8yLxkQkulze4 r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$o79XLwWlVObmQNA8yLxkQkulze4
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x038f
        L_0x010e:
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getGlobalSearch()
            java.util.ArrayList<java.lang.Object> r4 = r1.searchResult
            int r4 = r4.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r1.searchAdapterHelper
            java.util.ArrayList r5 = r5.getLocalServerSearch()
            int r5 = r5.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
            java.util.ArrayList r8 = r8.getPhoneSearch()
            int r8 = r8.size()
            boolean r9 = r3.isEmpty()
            if (r9 == 0) goto L_0x0135
            goto L_0x013b
        L_0x0135:
            int r3 = r3.size()
            int r6 = r3 + 1
        L_0x013b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0144
            goto L_0x0149
        L_0x0144:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            r3.size()
        L_0x0149:
            int r4 = r4 + r5
            int r2 = r2 - r4
            if (r2 < 0) goto L_0x015d
            if (r2 >= r8) goto L_0x015d
            r2 = 2131626943(0x7f0e0bbf, float:1.8881136E38)
            java.lang.String r3 = "PhoneNumberSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x038f
        L_0x015d:
            int r2 = r2 - r8
            if (r2 < 0) goto L_0x0170
            if (r2 >= r6) goto L_0x0170
            r2 = 2131625711(0x7f0e06ef, float:1.8878638E38)
            java.lang.String r3 = "GlobalSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x038f
        L_0x0170:
            r2 = 2131627335(0x7f0e0d47, float:1.8881932E38)
            java.lang.String r3 = "SearchMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x038f
        L_0x017e:
            android.view.View r0 = r0.itemView
            r3 = r0
            org.telegram.ui.Cells.ProfileSearchCell r3 = (org.telegram.ui.Cells.ProfileSearchCell) r3
            long r15 = r3.getDialogId()
            java.lang.Object r0 = r1.getItem(r2)
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r8 == 0) goto L_0x0198
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r8 = r0.username
            r9 = r5
            r11 = r9
            r10 = r8
            r8 = r0
            goto L_0x01e7
        L_0x0198:
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r8 == 0) goto L_0x01b9
            int r8 = r1.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            int r9 = r0.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r9)
            if (r8 != 0) goto L_0x01b1
            goto L_0x01b2
        L_0x01b1:
            r0 = r8
        L_0x01b2:
            java.lang.String r8 = r0.username
            r9 = r0
            r11 = r5
            r10 = r8
            r8 = r11
            goto L_0x01e7
        L_0x01b9:
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r8 == 0) goto L_0x01e3
            int r8 = r1.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC$EncryptedChat) r0
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r8.getEncryptedChat(r0)
            int r8 = r1.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            int r9 = r0.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r9)
            r11 = r0
            r9 = r5
            r10 = r9
            goto L_0x01e7
        L_0x01e3:
            r8 = r5
            r9 = r8
            r10 = r9
            r11 = r10
        L_0x01e7:
            boolean r0 = r18.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x01fe
            int r0 = r18.getItemCount()
            int r0 = r0 - r7
            if (r2 == r0) goto L_0x01f6
            r0 = 1
            goto L_0x01f7
        L_0x01f6:
            r0 = 0
        L_0x01f7:
            r3.useSeparator = r0
            r0 = r5
            r10 = r0
            r13 = 1
            goto L_0x0311
        L_0x01fe:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r0 = r0.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
            java.util.ArrayList r12 = r12.getPhoneSearch()
            java.util.ArrayList<java.lang.Object> r13 = r1.searchResult
            int r13 = r13.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
            java.util.ArrayList r14 = r14.getLocalServerSearch()
            int r14 = r14.size()
            int r17 = r12.size()
            if (r17 <= 0) goto L_0x022d
            int r5 = r17 + -1
            java.lang.Object r5 = r12.get(r5)
            boolean r5 = r5 instanceof java.lang.String
            if (r5 == 0) goto L_0x022d
            int r5 = r17 + -2
            goto L_0x022f
        L_0x022d:
            r5 = r17
        L_0x022f:
            boolean r12 = r0.isEmpty()
            if (r12 == 0) goto L_0x0237
            r0 = 0
            goto L_0x023c
        L_0x0237:
            int r0 = r0.size()
            int r0 = r0 + r7
        L_0x023c:
            int r12 = r18.getItemCount()
            int r12 = r12 - r7
            if (r2 == r12) goto L_0x0251
            int r5 = r5 + r13
            int r5 = r5 + r14
            int r5 = r5 - r7
            if (r2 == r5) goto L_0x0251
            int r13 = r13 + r0
            int r13 = r13 + r17
            int r13 = r13 + r14
            int r13 = r13 - r7
            if (r2 == r13) goto L_0x0251
            r0 = 1
            goto L_0x0252
        L_0x0251:
            r0 = 0
        L_0x0252:
            r3.useSeparator = r0
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r5 = "@"
            if (r2 >= r0) goto L_0x0296
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r2)
            r10 = r0
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            if (r10 == 0) goto L_0x0293
            if (r8 == 0) goto L_0x0293
            java.lang.String r0 = r8.username
            if (r0 == 0) goto L_0x0293
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0293
            java.lang.String r0 = r10.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            java.lang.String r5 = r8.username
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x0293
        L_0x0290:
            r0 = 0
            goto L_0x030d
        L_0x0293:
            r0 = r10
            goto L_0x030c
        L_0x0296:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x030b
            if (r8 == 0) goto L_0x02ad
            java.lang.String r2 = r8.first_name
            java.lang.String r12 = r8.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r12)
            goto L_0x02b3
        L_0x02ad:
            if (r9 == 0) goto L_0x02b2
            java.lang.String r2 = r9.title
            goto L_0x02b3
        L_0x02b2:
            r2 = 0
        L_0x02b3:
            r12 = 33
            java.lang.String r13 = "windowBackgroundWhiteBlueText4"
            r14 = -1
            if (r2 == 0) goto L_0x02d3
            int r4 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r0)
            if (r4 == r14) goto L_0x02d3
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r2)
            org.telegram.ui.Components.ForegroundColorSpanThemable r2 = new org.telegram.ui.Components.ForegroundColorSpanThemable
            r2.<init>(r13)
            int r0 = r0.length()
            int r0 = r0 + r4
            r10.setSpan(r2, r4, r0, r12)
            goto L_0x0293
        L_0x02d3:
            if (r10 == 0) goto L_0x030b
            boolean r2 = r0.startsWith(r5)
            if (r2 == 0) goto L_0x02df
            java.lang.String r0 = r0.substring(r7)
        L_0x02df:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0306 }
            r2.<init>()     // Catch:{ Exception -> 0x0306 }
            r2.append(r5)     // Catch:{ Exception -> 0x0306 }
            r2.append(r10)     // Catch:{ Exception -> 0x0306 }
            int r4 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r0)     // Catch:{ Exception -> 0x0306 }
            if (r4 == r14) goto L_0x0304
            int r0 = r0.length()     // Catch:{ Exception -> 0x0306 }
            if (r4 != 0) goto L_0x02f9
            int r0 = r0 + 1
            goto L_0x02fb
        L_0x02f9:
            int r4 = r4 + 1
        L_0x02fb:
            org.telegram.ui.Components.ForegroundColorSpanThemable r5 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0306 }
            r5.<init>(r13)     // Catch:{ Exception -> 0x0306 }
            int r0 = r0 + r4
            r2.setSpan(r5, r4, r0, r12)     // Catch:{ Exception -> 0x0306 }
        L_0x0304:
            r10 = r2
            goto L_0x0290
        L_0x0306:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0290
        L_0x030b:
            r0 = 0
        L_0x030c:
            r10 = 0
        L_0x030d:
            r3.setChecked(r6, r6)
            r13 = 0
        L_0x0311:
            if (r8 == 0) goto L_0x0325
            int r2 = r8.id
            int r4 = r1.selfUserId
            if (r2 != r4) goto L_0x0325
            r0 = 2131627298(0x7f0e0d22, float:1.8881856E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r5 = 0
            r14 = 1
            goto L_0x0327
        L_0x0325:
            r5 = r10
            r14 = 0
        L_0x0327:
            if (r9 == 0) goto L_0x036f
            int r2 = r9.participants_count
            if (r2 == 0) goto L_0x036f
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r2 == 0) goto L_0x0340
            boolean r2 = r9.megagroup
            if (r2 != 0) goto L_0x0340
            int r2 = r9.participants_count
            java.lang.String r4 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
            goto L_0x0348
        L_0x0340:
            int r2 = r9.participants_count
            java.lang.String r4 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r4, r2)
        L_0x0348:
            boolean r4 = r5 instanceof android.text.SpannableStringBuilder
            java.lang.String r10 = ", "
            if (r4 == 0) goto L_0x0359
            r4 = r5
            android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
            android.text.SpannableStringBuilder r4 = r4.append(r10)
            r4.append(r2)
            goto L_0x036f
        L_0x0359:
            boolean r4 = android.text.TextUtils.isEmpty(r5)
            if (r4 != 0) goto L_0x036d
            r4 = 3
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r4]
            r4[r6] = r5
            r4[r7] = r10
            r5 = 2
            r4[r5] = r2
            java.lang.CharSequence r2 = android.text.TextUtils.concat(r4)
        L_0x036d:
            r12 = r2
            goto L_0x0370
        L_0x036f:
            r12 = r5
        L_0x0370:
            if (r8 == 0) goto L_0x0373
            r9 = r8
        L_0x0373:
            r8 = r3
            r10 = r11
            r11 = r0
            r8.setData(r9, r10, r11, r12, r13, r14)
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogsSearchAdapterDelegate r0 = r1.delegate
            long r4 = r3.getDialogId()
            boolean r0 = r0.isSelected(r4)
            long r4 = r3.getDialogId()
            int r2 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x038c
            r6 = 1
        L_0x038c:
            r3.setChecked(r0, r6)
        L_0x038f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$16 */
    public /* synthetic */ void lambda$onBindViewHolder$16$DialogsSearchAdapter(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$17 */
    public /* synthetic */ void lambda$onBindViewHolder$17$DialogsSearchAdapter(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public int getItemViewType(int i) {
        if (isRecentSearchDisplayed()) {
            int i2 = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty();
            if (i < i2) {
                return 5;
            }
            return i == i2 ? 1 : 0;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size3 = this.searchAdapterHelper.getPhoneSearch().size();
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int size5 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < size) {
                return 0;
            }
            int i3 = i - size;
            if (i3 >= 0 && i3 < size2) {
                return 0;
            }
            int i4 = i3 - size2;
            if (i4 < 0 || i4 >= size3) {
                int i5 = i4 - size3;
                if (i5 >= 0 && i5 < size4) {
                    return i5 == 0 ? 1 : 0;
                }
                int i6 = i5 - size4;
                if (i6 < 0 || i6 >= size5) {
                    return 3;
                }
                if (i6 == 0) {
                    return 1;
                }
                return 2;
            }
            Object item = getItem(i4);
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
