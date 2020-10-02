package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Log;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
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
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
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
    private ArrayList<FiltersView.DateData> localTipDates;
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
    public ArrayList<TLObject> searchResult = new ArrayList<>();
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
        new ArrayList();
        this.localTipDates = new ArrayList<>();
        this.folderId = i3;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<TLRPC$User> getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                DialogsSearchAdapter.this.waitingResponseCount--;
                Log.d("kek", "data set change " + DialogsSearchAdapter.this.waitingResponseCount);
                int unused = DialogsSearchAdapter.this.lastGlobalSearchId = i;
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
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false, false);
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

    public /* synthetic */ void lambda$searchMessagesInternal$1$DialogsSearchAdapter(String str, int i, int i2, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList = new ArrayList();
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i3), false, true);
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
                DialogsSearchAdapter.this.lambda$null$0$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$DialogsSearchAdapter(int i, int i2, TLRPC$TL_error tLRPC$TL_error, String str, TLObject tLObject, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, ArrayList arrayList) {
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
                    Integer num = MessagesController.getInstance(this.currentAccount).deletedHistory.get(MessageObject.getDialogId(tLRPC$Message));
                    if (num == null || tLRPC$Message.id > num.intValue()) {
                        this.searchResultMessages.add(arrayList.get(i3));
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
                notifyDataSetChanged();
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null && tLRPC$TL_messages_searchGlobal.offset_id == 0) {
                    dialogsSearchAdapterDelegate.searchStateChanged(this.waitingResponseCount > 0, true);
                    this.delegate.runResultsEnterAnimation();
                }
            }
        }
        this.reqId = 0;
    }

    public boolean hasRecentRearch() {
        int i = this.dialogsType;
        return (i == 2 || i == 4 || i == 5 || i == 6 || (this.recentSearchObjects.isEmpty() && MediaDataController.getInstance(this.currentAccount).hints.isEmpty())) ? false : true;
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
            if (r0 == r1) goto L_0x002e
            boolean r0 = r2.searchWas
            if (r0 != 0) goto L_0x002e
            java.util.ArrayList<org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject> r0 = r2.recentSearchObjects
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x001f
            int r0 = r2.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r0 = r0.hints
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x002e
        L_0x001f:
            int r0 = r2.dialogsType
            if (r0 == r1) goto L_0x002e
            r1 = 4
            if (r0 == r1) goto L_0x002e
            r1 = 5
            if (r0 == r1) goto L_0x002e
            r1 = 6
            if (r0 == r1) goto L_0x002e
            r0 = 1
            goto L_0x002f
        L_0x002e:
            r0 = 0
        L_0x002f:
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

    /* JADX WARNING: Removed duplicated region for block: B:25:0x008f A[Catch:{ Exception -> 0x016d }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0031 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadRecentSearch$4$DialogsSearchAdapter() {
        /*
            r13 = this;
            int r0 = r13.currentAccount     // Catch:{ Exception -> 0x016d }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x016d }
            org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x016d }
            java.lang.String r1 = "SELECT did, date FROM search_recent WHERE 1"
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x016d }
            org.telegram.SQLite.SQLiteCursor r0 = r0.queryFinalized(r1, r3)     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r1.<init>()     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r3.<init>()     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r4.<init>()     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r5.<init>()     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r5.<init>()     // Catch:{ Exception -> 0x016d }
            android.util.LongSparseArray r6 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x016d }
            r6.<init>()     // Catch:{ Exception -> 0x016d }
        L_0x0031:
            boolean r7 = r0.next()     // Catch:{ Exception -> 0x016d }
            r8 = 32
            if (r7 == 0) goto L_0x00a5
            long r9 = r0.longValue(r2)     // Catch:{ Exception -> 0x016d }
            int r7 = (int) r9     // Catch:{ Exception -> 0x016d }
            long r11 = r9 >> r8
            int r8 = (int) r11     // Catch:{ Exception -> 0x016d }
            r11 = 1
            if (r7 == 0) goto L_0x0071
            if (r7 <= 0) goto L_0x005d
            int r8 = r13.dialogsType     // Catch:{ Exception -> 0x016d }
            r12 = 2
            if (r8 == r12) goto L_0x008c
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016d }
            boolean r8 = r1.contains(r8)     // Catch:{ Exception -> 0x016d }
            if (r8 != 0) goto L_0x008c
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016d }
            r1.add(r7)     // Catch:{ Exception -> 0x016d }
            goto L_0x006f
        L_0x005d:
            int r7 = -r7
            java.lang.Integer r8 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016d }
            boolean r8 = r3.contains(r8)     // Catch:{ Exception -> 0x016d }
            if (r8 != 0) goto L_0x008c
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x016d }
            r3.add(r7)     // Catch:{ Exception -> 0x016d }
        L_0x006f:
            r7 = 1
            goto L_0x008d
        L_0x0071:
            int r7 = r13.dialogsType     // Catch:{ Exception -> 0x016d }
            if (r7 == 0) goto L_0x007a
            int r7 = r13.dialogsType     // Catch:{ Exception -> 0x016d }
            r12 = 3
            if (r7 != r12) goto L_0x008c
        L_0x007a:
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x016d }
            boolean r7 = r4.contains(r7)     // Catch:{ Exception -> 0x016d }
            if (r7 != 0) goto L_0x008c
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x016d }
            r4.add(r7)     // Catch:{ Exception -> 0x016d }
            goto L_0x006f
        L_0x008c:
            r7 = 0
        L_0x008d:
            if (r7 == 0) goto L_0x0031
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject     // Catch:{ Exception -> 0x016d }
            r7.<init>()     // Catch:{ Exception -> 0x016d }
            r7.did = r9     // Catch:{ Exception -> 0x016d }
            int r8 = r0.intValue(r11)     // Catch:{ Exception -> 0x016d }
            r7.date = r8     // Catch:{ Exception -> 0x016d }
            r5.add(r7)     // Catch:{ Exception -> 0x016d }
            long r8 = r7.did     // Catch:{ Exception -> 0x016d }
            r6.put(r8, r7)     // Catch:{ Exception -> 0x016d }
            goto L_0x0031
        L_0x00a5:
            r0.dispose()     // Catch:{ Exception -> 0x016d }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r0.<init>()     // Catch:{ Exception -> 0x016d }
            boolean r7 = r4.isEmpty()     // Catch:{ Exception -> 0x016d }
            java.lang.String r9 = ","
            if (r7 != 0) goto L_0x00e9
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r7.<init>()     // Catch:{ Exception -> 0x016d }
            int r10 = r13.currentAccount     // Catch:{ Exception -> 0x016d }
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r10)     // Catch:{ Exception -> 0x016d }
            java.lang.String r4 = android.text.TextUtils.join(r9, r4)     // Catch:{ Exception -> 0x016d }
            r10.getEncryptedChatsInternal(r4, r7, r1)     // Catch:{ Exception -> 0x016d }
            r4 = 0
        L_0x00c8:
            int r10 = r7.size()     // Catch:{ Exception -> 0x016d }
            if (r4 >= r10) goto L_0x00e9
            java.lang.Object r10 = r7.get(r4)     // Catch:{ Exception -> 0x016d }
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = (org.telegram.tgnet.TLRPC$EncryptedChat) r10     // Catch:{ Exception -> 0x016d }
            int r10 = r10.id     // Catch:{ Exception -> 0x016d }
            long r10 = (long) r10     // Catch:{ Exception -> 0x016d }
            long r10 = r10 << r8
            java.lang.Object r10 = r6.get(r10)     // Catch:{ Exception -> 0x016d }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r10     // Catch:{ Exception -> 0x016d }
            java.lang.Object r11 = r7.get(r4)     // Catch:{ Exception -> 0x016d }
            org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11     // Catch:{ Exception -> 0x016d }
            r10.object = r11     // Catch:{ Exception -> 0x016d }
            int r4 = r4 + 1
            goto L_0x00c8
        L_0x00e9:
            boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x016d }
            if (r4 != 0) goto L_0x0130
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x016d }
            r4.<init>()     // Catch:{ Exception -> 0x016d }
            int r7 = r13.currentAccount     // Catch:{ Exception -> 0x016d }
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r7)     // Catch:{ Exception -> 0x016d }
            java.lang.String r3 = android.text.TextUtils.join(r9, r3)     // Catch:{ Exception -> 0x016d }
            r7.getChatsInternal(r3, r4)     // Catch:{ Exception -> 0x016d }
            r3 = 0
        L_0x0102:
            int r7 = r4.size()     // Catch:{ Exception -> 0x016d }
            if (r3 >= r7) goto L_0x0130
            java.lang.Object r7 = r4.get(r3)     // Catch:{ Exception -> 0x016d }
            org.telegram.tgnet.TLRPC$Chat r7 = (org.telegram.tgnet.TLRPC$Chat) r7     // Catch:{ Exception -> 0x016d }
            int r8 = r7.id     // Catch:{ Exception -> 0x016d }
            int r8 = -r8
            long r10 = (long) r8     // Catch:{ Exception -> 0x016d }
            org.telegram.tgnet.TLRPC$InputChannel r8 = r7.migrated_to     // Catch:{ Exception -> 0x016d }
            if (r8 == 0) goto L_0x0125
            java.lang.Object r7 = r6.get(r10)     // Catch:{ Exception -> 0x016d }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r7 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r7     // Catch:{ Exception -> 0x016d }
            r6.remove(r10)     // Catch:{ Exception -> 0x016d }
            if (r7 == 0) goto L_0x012d
            r5.remove(r7)     // Catch:{ Exception -> 0x016d }
            goto L_0x012d
        L_0x0125:
            java.lang.Object r8 = r6.get(r10)     // Catch:{ Exception -> 0x016d }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r8 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r8     // Catch:{ Exception -> 0x016d }
            r8.object = r7     // Catch:{ Exception -> 0x016d }
        L_0x012d:
            int r3 = r3 + 1
            goto L_0x0102
        L_0x0130:
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x016d }
            if (r3 != 0) goto L_0x015f
            int r3 = r13.currentAccount     // Catch:{ Exception -> 0x016d }
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x016d }
            java.lang.String r1 = android.text.TextUtils.join(r9, r1)     // Catch:{ Exception -> 0x016d }
            r3.getUsersInternal(r1, r0)     // Catch:{ Exception -> 0x016d }
        L_0x0143:
            int r1 = r0.size()     // Catch:{ Exception -> 0x016d }
            if (r2 >= r1) goto L_0x015f
            java.lang.Object r1 = r0.get(r2)     // Catch:{ Exception -> 0x016d }
            org.telegram.tgnet.TLRPC$User r1 = (org.telegram.tgnet.TLRPC$User) r1     // Catch:{ Exception -> 0x016d }
            int r3 = r1.id     // Catch:{ Exception -> 0x016d }
            long r3 = (long) r3     // Catch:{ Exception -> 0x016d }
            java.lang.Object r3 = r6.get(r3)     // Catch:{ Exception -> 0x016d }
            org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r3     // Catch:{ Exception -> 0x016d }
            if (r3 == 0) goto L_0x015c
            r3.object = r1     // Catch:{ Exception -> 0x016d }
        L_0x015c:
            int r2 = r2 + 1
            goto L_0x0143
        L_0x015f:
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$TgrSEhniISqCg6ct5i9NTHhT7C8 r0 = org.telegram.ui.Adapters.$$Lambda$DialogsSearchAdapter$TgrSEhniISqCg6ct5i9NTHhT7C8.INSTANCE     // Catch:{ Exception -> 0x016d }
            java.util.Collections.sort(r5, r0)     // Catch:{ Exception -> 0x016d }
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YAAaRoGgRkDmshNt90P0fNwfz-U r0 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YAAaRoGgRkDmshNt90P0fNwfz-U     // Catch:{ Exception -> 0x016d }
            r0.<init>(r5, r6)     // Catch:{ Exception -> 0x016d }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x016d }
            goto L_0x0171
        L_0x016d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0171:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.lambda$loadRecentSearch$4$DialogsSearchAdapter():void");
    }

    static /* synthetic */ int lambda$null$2(RecentSearchObject recentSearchObject, RecentSearchObject recentSearchObject2) {
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
    public void lambda$null$3$DialogsSearchAdapter(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray) {
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(lowerCase, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DialogsSearchAdapter.this.lambda$searchDialogsInternal$9$DialogsSearchAdapter(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$searchDialogsInternal$9$DialogsSearchAdapter(String str, int i) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        MessagesStorage.getInstance(this.currentAccount).localSearch(this.dialogsType, str, arrayList, arrayList2, arrayList3, -1);
        updateSearchResults(arrayList, arrayList2, arrayList3, i);
        FiltersView.fillTipDates(str, this.localTipDates);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsSearchAdapter.this.lambda$null$8$DialogsSearchAdapter();
            }
        });
    }

    public /* synthetic */ void lambda$null$8$DialogsSearchAdapter() {
        FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
        if (delegate2 != null) {
            delegate2.updateFiltersView(false, (ArrayList<TLObject>) null, this.localTipDates);
        }
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<TLRPC$User> arrayList3, int i) {
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
                DialogsSearchAdapter.this.lambda$updateSearchResults$10$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$10$DialogsSearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        this.waitingResponseCount--;
        Log.d("kek", "update local search " + this.waitingResponseCount);
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
                TLObject tLObject = (TLObject) arrayList.get(i2);
                if (tLObject instanceof TLRPC$User) {
                    MessagesController.getInstance(this.currentAccount).putUser((TLRPC$User) tLObject, true);
                } else if (tLObject instanceof TLRPC$Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((TLRPC$Chat) tLObject, true);
                } else if (tLObject instanceof TLRPC$EncryptedChat) {
                    MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC$EncryptedChat) tLObject, true);
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

    public boolean isHashtagSearch() {
        return !this.searchResultHashtags.isEmpty();
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(String str) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate;
        String str2 = str;
        if (str2 == null || !str2.equals(this.lastSearchText)) {
            this.lastSearchText = str2;
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
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                if (this.needMessagesSearch != 2) {
                    this.searchAdapterHelper.queryServerSearch((String) null, true, true, true, true, this.dialogsType == 2, 0, this.dialogsType == 0, 0, 0);
                }
                this.searchWas = false;
                this.lastSearchId = 0;
                this.waitingResponseCount = 0;
                searchMessagesInternal((String) null, 0);
                notifyDataSetChanged();
                this.localTipDates.clear();
                FilteredSearchView.Delegate delegate2 = this.filtersDelegate;
                if (delegate2 != null) {
                    delegate2.updateFiltersView(false, (ArrayList<TLObject>) null, this.localTipDates);
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
                    for (int i = 0; i < hashtags.size(); i++) {
                        this.searchResultHashtags.add(hashtags.get(i).hashtag);
                    }
                    this.waitingResponseCount = 0;
                    notifyDataSetChanged();
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate2 = this.delegate;
                    if (dialogsSearchAdapterDelegate2 != null) {
                        dialogsSearchAdapterDelegate2.searchStateChanged(false, false);
                    }
                }
            }
            int i2 = this.lastSearchId + 1;
            this.lastSearchId = i2;
            this.waitingResponseCount = 3;
            notifyDataSetChanged();
            if (!(this.needMessagesSearch == 2 || (dialogsSearchAdapterDelegate = this.delegate) == null)) {
                dialogsSearchAdapterDelegate.searchStateChanged(true, false);
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$DialogsSearchAdapter$zjKeY8XytNvyYaDr1bnMPC5nRGc r5 = new Runnable(trim, i2, str2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;
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
            this.searchRunnable = r5;
            dispatchQueue.postRunnable(r5, 300);
        }
    }

    public /* synthetic */ void lambda$searchDialogs$12$DialogsSearchAdapter(String str, int i, String str2) {
        this.searchRunnable = null;
        searchDialogsInternal(str, i);
        $$Lambda$DialogsSearchAdapter$IbsY0jvr3UriSBn7nmzOA4zDytw r0 = new Runnable(i, str, str2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$null$11$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        };
        this.searchRunnable2 = r0;
        AndroidUtilities.runOnUIThread(r0);
    }

    public /* synthetic */ void lambda$null$11$DialogsSearchAdapter(int i, String str, String str2) {
        int i2 = i;
        this.searchRunnable2 = null;
        if (i2 == this.lastSearchId) {
            if (this.needMessagesSearch != 2) {
                this.searchAdapterHelper.queryServerSearch(str, true, this.dialogsType != 4, true, this.dialogsType != 4, this.dialogsType == 2, 0, this.dialogsType == 0, 0, i);
            } else {
                this.waitingResponseCount--;
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
            return size4 != 0 ? size5 + size4 + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0) : size5;
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

    public /* synthetic */ void lambda$onCreateViewHolder$13$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog((long) ((Integer) view.getTag()).intValue());
        }
    }

    public /* synthetic */ boolean lambda$onCreateViewHolder$14$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate == null) {
            return true;
        }
        dialogsSearchAdapterDelegate.needRemoveHint(((Integer) view.getTag()).intValue());
        return true;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        switch (i) {
            case 0:
                view = new ProfileSearchCell(this.mContext);
                break;
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            case 2:
                view = new DialogCell(this.mContext, false, true);
                break;
            case 3:
                view = new LoadingCell(this.mContext);
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                AnonymousClass2 r1 = new RecyclerListView(this, this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                r1.setTag(9);
                r1.setItemAnimator((RecyclerView.ItemAnimator) null);
                r1.setLayoutAnimation((LayoutAnimationController) null);
                AnonymousClass3 r2 = new LinearLayoutManager(this, this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                r2.setOrientation(0);
                r1.setLayoutManager(r2);
                r1.setAdapter(new CategoryAdapterRecycler());
                r1.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                    public final void onItemClick(View view, int i) {
                        DialogsSearchAdapter.this.lambda$onCreateViewHolder$13$DialogsSearchAdapter(view, i);
                    }
                });
                r1.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                    public final boolean onItemClick(View view, int i) {
                        return DialogsSearchAdapter.this.lambda$onCreateViewHolder$14$DialogsSearchAdapter(view, i);
                    }
                });
                this.innerListView = r1;
                view = r1;
                break;
            case 6:
                view = new TextCell(this.mContext, 16, false);
                break;
        }
        if (i == 5) {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }
        return new RecyclerListView.Holder(view);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v11, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: android.text.SpannableStringBuilder} */
    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v1 */
    /* JADX WARNING: type inference failed for: r6v4 */
    /* JADX WARNING: type inference failed for: r6v6 */
    /* JADX WARNING: type inference failed for: r6v7 */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0288, code lost:
        if (r0.startsWith("@" + r3.username) != false) goto L_0x028a;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0373  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            int r3 = r17.getItemViewType()
            r4 = 2
            r5 = 0
            r6 = 0
            r7 = 1
            if (r3 == 0) goto L_0x017f
            if (r3 == r7) goto L_0x00ad
            if (r3 == r4) goto L_0x0089
            r8 = 4
            if (r3 == r8) goto L_0x006a
            r8 = 5
            if (r3 == r8) goto L_0x005a
            r4 = 6
            if (r3 == r4) goto L_0x001f
            goto L_0x0379
        L_0x001f:
            java.lang.Object r2 = r1.getItem(r2)
            java.lang.String r2 = (java.lang.String) r2
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
            java.lang.String r3 = "windowBackgroundWhiteBlueText2"
            r0.setColors(r5, r3)
            r3 = 2131624158(0x7f0e00de, float:1.8875488E38)
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
            goto L_0x0379
        L_0x005a:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0
            int r2 = r2 / r4
            r0.setIndex(r2)
            goto L_0x0379
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
            r6 = 1
        L_0x0084:
            r0.setNeedDivider(r6)
            goto L_0x0379
        L_0x0089:
            android.view.View r0 = r0.itemView
            r8 = r0
            org.telegram.ui.Cells.DialogCell r8 = (org.telegram.ui.Cells.DialogCell) r8
            int r0 = r16.getItemCount()
            int r0 = r0 - r7
            if (r2 == r0) goto L_0x0096
            r6 = 1
        L_0x0096:
            r8.useSeparator = r6
            java.lang.Object r0 = r1.getItem(r2)
            r11 = r0
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            long r9 = r11.getDialogId()
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            int r12 = r0.date
            r13 = 0
            r8.setDialog(r9, r11, r12, r13)
            goto L_0x0379
        L_0x00ad:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            boolean r3 = r16.isRecentSearchDisplayed()
            r4 = 2131624806(0x7f0e0366, float:1.8876802E38)
            java.lang.String r5 = "ClearButton"
            if (r3 == 0) goto L_0x00f0
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
            boolean r3 = r3.isEmpty()
            r3 = r3 ^ r7
            if (r2 >= r3) goto L_0x00d9
            r2 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r3 = "ChatHints"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0379
        L_0x00d9:
            r2 = 2131626700(0x7f0e0acc, float:1.8880644E38)
            java.lang.String r3 = "Recent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$honrBco-zV9w0fwaI91SKdwfMI0 r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$honrBco-zV9w0fwaI91SKdwfMI0
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x0379
        L_0x00f0:
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x010f
            r2 = 2131625563(0x7f0e065b, float:1.8878337E38)
            java.lang.String r3 = "Hashtags"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YI57NotkimI_aZA_lycnYL-wN7k r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YI57NotkimI_aZA_lycnYL-wN7k
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x0379
        L_0x010f:
            org.telegram.ui.Adapters.SearchAdapterHelper r3 = r1.searchAdapterHelper
            java.util.ArrayList r3 = r3.getGlobalSearch()
            java.util.ArrayList<org.telegram.tgnet.TLObject> r4 = r1.searchResult
            int r4 = r4.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r1.searchAdapterHelper
            java.util.ArrayList r5 = r5.getLocalServerSearch()
            int r5 = r5.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
            java.util.ArrayList r8 = r8.getPhoneSearch()
            int r8 = r8.size()
            boolean r9 = r3.isEmpty()
            if (r9 == 0) goto L_0x0136
            goto L_0x013c
        L_0x0136:
            int r3 = r3.size()
            int r6 = r3 + 1
        L_0x013c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0145
            goto L_0x014a
        L_0x0145:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            r3.size()
        L_0x014a:
            int r4 = r4 + r5
            int r2 = r2 - r4
            if (r2 < 0) goto L_0x015e
            if (r2 >= r8) goto L_0x015e
            r2 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.String r3 = "PhoneNumberSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0379
        L_0x015e:
            int r2 = r2 - r8
            if (r2 < 0) goto L_0x0171
            if (r2 >= r6) goto L_0x0171
            r2 = 2131625518(0x7f0e062e, float:1.8878246E38)
            java.lang.String r3 = "GlobalSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0379
        L_0x0171:
            r2 = 2131626870(0x7f0e0b76, float:1.8880988E38)
            java.lang.String r3 = "SearchMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x0379
        L_0x017f:
            android.view.View r0 = r0.itemView
            r8 = r0
            org.telegram.ui.Cells.ProfileSearchCell r8 = (org.telegram.ui.Cells.ProfileSearchCell) r8
            java.lang.Object r0 = r1.getItem(r2)
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r3 == 0) goto L_0x0195
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r3 = r0.username
            r10 = r3
            r9 = r5
            r11 = r9
            r3 = r0
            goto L_0x01e4
        L_0x0195:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r3 == 0) goto L_0x01b6
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            int r9 = r0.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r9)
            if (r3 != 0) goto L_0x01ae
            goto L_0x01af
        L_0x01ae:
            r0 = r3
        L_0x01af:
            java.lang.String r3 = r0.username
            r9 = r0
            r10 = r3
            r3 = r5
            r11 = r3
            goto L_0x01e4
        L_0x01b6:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r3 == 0) goto L_0x01e0
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC$EncryptedChat) r0
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r3.getEncryptedChat(r0)
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r9 = r0.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r9)
            r11 = r0
            r9 = r5
            r10 = r9
            goto L_0x01e4
        L_0x01e0:
            r3 = r5
            r9 = r3
            r10 = r9
            r11 = r10
        L_0x01e4:
            boolean r0 = r16.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x01fa
            int r0 = r16.getItemCount()
            int r0 = r0 - r7
            if (r2 == r0) goto L_0x01f3
            r0 = 1
            goto L_0x01f4
        L_0x01f3:
            r0 = 0
        L_0x01f4:
            r8.useSeparator = r0
            r10 = r5
            r13 = 1
            goto L_0x0312
        L_0x01fa:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.util.ArrayList r0 = r0.getGlobalSearch()
            org.telegram.ui.Adapters.SearchAdapterHelper r12 = r1.searchAdapterHelper
            java.util.ArrayList r12 = r12.getPhoneSearch()
            java.util.ArrayList<org.telegram.tgnet.TLObject> r13 = r1.searchResult
            int r13 = r13.size()
            org.telegram.ui.Adapters.SearchAdapterHelper r14 = r1.searchAdapterHelper
            java.util.ArrayList r14 = r14.getLocalServerSearch()
            int r14 = r14.size()
            int r15 = r12.size()
            if (r15 <= 0) goto L_0x0229
            int r5 = r15 + -1
            java.lang.Object r5 = r12.get(r5)
            boolean r5 = r5 instanceof java.lang.String
            if (r5 == 0) goto L_0x0229
            int r5 = r15 + -2
            goto L_0x022a
        L_0x0229:
            r5 = r15
        L_0x022a:
            boolean r12 = r0.isEmpty()
            if (r12 == 0) goto L_0x0232
            r0 = 0
            goto L_0x0237
        L_0x0232:
            int r0 = r0.size()
            int r0 = r0 + r7
        L_0x0237:
            int r12 = r16.getItemCount()
            int r12 = r12 - r7
            if (r2 == r12) goto L_0x024b
            int r5 = r5 + r13
            int r5 = r5 + r14
            int r5 = r5 - r7
            if (r2 == r5) goto L_0x024b
            int r13 = r13 + r0
            int r13 = r13 + r15
            int r13 = r13 + r14
            int r13 = r13 - r7
            if (r2 == r13) goto L_0x024b
            r0 = 1
            goto L_0x024c
        L_0x024b:
            r0 = 0
        L_0x024c:
            r8.useSeparator = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r5 = "@"
            if (r2 >= r0) goto L_0x0290
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r2)
            r10 = r0
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            if (r10 == 0) goto L_0x028d
            if (r3 == 0) goto L_0x028d
            java.lang.String r0 = r3.username
            if (r0 == 0) goto L_0x028d
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x028d
            java.lang.String r0 = r10.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            java.lang.String r5 = r3.username
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x028d
        L_0x028a:
            r5 = 0
            goto L_0x0311
        L_0x028d:
            r5 = r10
            goto L_0x0310
        L_0x0290:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x030f
            if (r3 == 0) goto L_0x02a7
            java.lang.String r2 = r3.first_name
            java.lang.String r12 = r3.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r12)
            goto L_0x02ad
        L_0x02a7:
            if (r9 == 0) goto L_0x02ac
            java.lang.String r2 = r9.title
            goto L_0x02ad
        L_0x02ac:
            r2 = 0
        L_0x02ad:
            r12 = 33
            java.lang.String r13 = "windowBackgroundWhiteBlueText4"
            r14 = -1
            if (r2 == 0) goto L_0x02d2
            int r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r0)
            if (r15 == r14) goto L_0x02d2
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r2)
            android.text.style.ForegroundColorSpan r2 = new android.text.style.ForegroundColorSpan
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r2.<init>(r5)
            int r0 = r0.length()
            int r0 = r0 + r15
            r10.setSpan(r2, r15, r0, r12)
            goto L_0x028d
        L_0x02d2:
            if (r10 == 0) goto L_0x030f
            boolean r2 = r0.startsWith(r5)
            if (r2 == 0) goto L_0x02de
            java.lang.String r0 = r0.substring(r7)
        L_0x02de:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0309 }
            r2.<init>()     // Catch:{ Exception -> 0x0309 }
            r2.append(r5)     // Catch:{ Exception -> 0x0309 }
            r2.append(r10)     // Catch:{ Exception -> 0x0309 }
            int r5 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r0)     // Catch:{ Exception -> 0x0309 }
            if (r5 == r14) goto L_0x0307
            int r0 = r0.length()     // Catch:{ Exception -> 0x0309 }
            if (r5 != 0) goto L_0x02f8
            int r0 = r0 + 1
            goto L_0x02fa
        L_0x02f8:
            int r5 = r5 + 1
        L_0x02fa:
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0309 }
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x0309 }
            r14.<init>(r13)     // Catch:{ Exception -> 0x0309 }
            int r0 = r0 + r5
            r2.setSpan(r14, r5, r0, r12)     // Catch:{ Exception -> 0x0309 }
        L_0x0307:
            r10 = r2
            goto L_0x028a
        L_0x0309:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x028a
        L_0x030f:
            r5 = 0
        L_0x0310:
            r10 = 0
        L_0x0311:
            r13 = 0
        L_0x0312:
            if (r3 == 0) goto L_0x0326
            int r0 = r3.id
            int r2 = r1.selfUserId
            if (r0 != r2) goto L_0x0326
            r0 = 2131626834(0x7f0e0b52, float:1.8880915E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r5 = 0
            r14 = 1
            goto L_0x0329
        L_0x0326:
            r0 = r5
            r5 = r10
            r14 = 0
        L_0x0329:
            if (r9 == 0) goto L_0x0370
            int r2 = r9.participants_count
            if (r2 == 0) goto L_0x0370
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r2 == 0) goto L_0x0342
            boolean r2 = r9.megagroup
            if (r2 != 0) goto L_0x0342
            int r2 = r9.participants_count
            java.lang.String r10 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)
            goto L_0x034a
        L_0x0342:
            int r2 = r9.participants_count
            java.lang.String r10 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)
        L_0x034a:
            boolean r10 = r5 instanceof android.text.SpannableStringBuilder
            java.lang.String r12 = ", "
            if (r10 == 0) goto L_0x035b
            r4 = r5
            android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
            android.text.SpannableStringBuilder r4 = r4.append(r12)
            r4.append(r2)
            goto L_0x0370
        L_0x035b:
            boolean r10 = android.text.TextUtils.isEmpty(r5)
            if (r10 != 0) goto L_0x036e
            r10 = 3
            java.lang.CharSequence[] r10 = new java.lang.CharSequence[r10]
            r10[r6] = r5
            r10[r7] = r12
            r10[r4] = r2
            java.lang.CharSequence r2 = android.text.TextUtils.concat(r10)
        L_0x036e:
            r12 = r2
            goto L_0x0371
        L_0x0370:
            r12 = r5
        L_0x0371:
            if (r3 == 0) goto L_0x0374
            r9 = r3
        L_0x0374:
            r10 = r11
            r11 = r0
            r8.setData(r9, r10, r11, r12, r13, r14)
        L_0x0379:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    public /* synthetic */ void lambda$onBindViewHolder$15$DialogsSearchAdapter(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$16$DialogsSearchAdapter(View view) {
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
            delegate2.updateFiltersView(false, (ArrayList<TLObject>) null, this.localTipDates);
        }
    }
}
