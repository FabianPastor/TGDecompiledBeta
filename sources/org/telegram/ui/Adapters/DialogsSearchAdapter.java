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
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC$TL_topPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.RecyclerListView;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter {
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
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
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList<>();
    private LongSparseArray<RecentSearchObject> recentSearchObjectsById = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int reqId = 0;
    /* access modifiers changed from: private */
    public SearchAdapterHelper searchAdapterHelper;
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

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(long j);

        void needClearList();

        void needRemoveHint(int i);

        void searchStateChanged(boolean z);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    private static class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
        }
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

    public DialogsSearchAdapter(Context context, int i, int i2) {
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(false);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<TLRPC$User> getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                int unused = DialogsSearchAdapter.this.lastGlobalSearchId = i;
                if (DialogsSearchAdapter.this.lastLocalSearchId != i) {
                    DialogsSearchAdapter.this.searchResult.clear();
                }
                if (DialogsSearchAdapter.this.lastMessagesSearchId != i) {
                    DialogsSearchAdapter.this.searchResultMessages.clear();
                }
                boolean unused2 = DialogsSearchAdapter.this.searchWas = true;
                if (!DialogsSearchAdapter.this.searchAdapterHelper.isSearchInProgress() && DialogsSearchAdapter.this.delegate != null && DialogsSearchAdapter.this.reqId == 0) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                for (int i = 0; i < arrayList.size(); i++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(arrayList.get(i).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
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
                DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                if (dialogsSearchAdapterDelegate != null) {
                    dialogsSearchAdapterDelegate.searchStateChanged(false);
                    return;
                }
                return;
            }
            TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal = new TLRPC$TL_messages_searchGlobal();
            tLRPC$TL_messages_searchGlobal.limit = 20;
            tLRPC$TL_messages_searchGlobal.q = str;
            if (!str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                tLRPC$TL_messages_searchGlobal.offset_rate = 0;
                tLRPC$TL_messages_searchGlobal.offset_id = 0;
                tLRPC$TL_messages_searchGlobal.offset_peer = new TLRPC$TL_inputPeerEmpty();
            } else {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                MessageObject messageObject = arrayList.get(arrayList.size() - 1);
                tLRPC$TL_messages_searchGlobal.offset_id = messageObject.getId();
                tLRPC$TL_messages_searchGlobal.offset_rate = this.nextSearchRate;
                TLRPC$Peer tLRPC$Peer = messageObject.messageOwner.to_id;
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
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_searchGlobal, new RequestDelegate(i4, i, tLRPC$TL_messages_searchGlobal) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    DialogsSearchAdapter.this.lambda$searchMessagesInternal$1$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            }, 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInternal$1$DialogsSearchAdapter(int i, int i2, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, i2, tLRPC$TL_error, tLObject, tLRPC$TL_messages_searchGlobal) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;
            public final /* synthetic */ TLObject f$4;
            public final /* synthetic */ TLRPC$TL_messages_searchGlobal f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                DialogsSearchAdapter.this.lambda$null$0$DialogsSearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$DialogsSearchAdapter(int i, int i2, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_searchGlobal tLRPC$TL_messages_searchGlobal) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate;
        if (i == this.lastReqId && ((i2 <= 0 || i2 == this.lastSearchId) && tLRPC$TL_error == null)) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            boolean z = true;
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
                    this.searchResultMessages.add(new MessageObject(this.currentAccount, tLRPC$Message, false, true));
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
            if (tLRPC$messages_Messages.messages.size() == 20) {
                z = false;
            }
            this.messagesSearchEndReached = z;
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
        }
        this.reqId = 0;
        if (!this.searchAdapterHelper.isSearchInProgress() && (dialogsSearchAdapterDelegate = this.delegate) != null) {
            dialogsSearchAdapterDelegate.searchStateChanged(false);
        }
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
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(str, i) {
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

    /* JADX WARNING: type inference failed for: r14v2 */
    /* JADX WARNING: type inference failed for: r14v9 */
    /* JADX WARNING: type inference failed for: r14v13 */
    /* JADX WARNING: type inference failed for: r14v15 */
    /* JADX WARNING: type inference failed for: r14v45 */
    /* JADX WARNING: type inference failed for: r14v47 */
    /* JADX WARNING: type inference failed for: r14v51 */
    /* JADX WARNING: type inference failed for: r14v53 */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0516, code lost:
        r18 = r15;
        r11 = r19;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x050c A[Catch:{ Exception -> 0x06a5 }, LOOP:6: B:150:0x0397->B:188:0x050c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0683 A[Catch:{ Exception -> 0x06a5 }, LOOP:10: B:222:0x05d0->B:251:0x0683, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x01e3 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x03e1 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0625 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x024e A[Catch:{ Exception -> 0x06a5 }, LOOP:2: B:69:0x0190->B:99:0x024e, LOOP_END] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$searchDialogsInternal$9$DialogsSearchAdapter(java.lang.String r24, int r25) {
        /*
            r23 = this;
            r1 = r23
            java.lang.String r0 = "SavedMessages"
            r2 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r2 = r24.trim()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r2 = r2.toLowerCase()     // Catch:{ Exception -> 0x06a5 }
            int r3 = r2.length()     // Catch:{ Exception -> 0x06a5 }
            r4 = 0
            if (r3 != 0) goto L_0x0035
            r1.lastSearchId = r4     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r0.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r2.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r3.<init>()     // Catch:{ Exception -> 0x06a5 }
            int r4 = r1.lastSearchId     // Catch:{ Exception -> 0x06a5 }
            r1.updateSearchResults(r0, r2, r3, r4)     // Catch:{ Exception -> 0x06a5 }
            return
        L_0x0035:
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = r3.getTranslitString(r2)     // Catch:{ Exception -> 0x06a5 }
            boolean r5 = r2.equals(r3)     // Catch:{ Exception -> 0x06a5 }
            if (r5 != 0) goto L_0x0049
            int r5 = r3.length()     // Catch:{ Exception -> 0x06a5 }
            if (r5 != 0) goto L_0x004a
        L_0x0049:
            r3 = 0
        L_0x004a:
            r5 = 1
            if (r3 == 0) goto L_0x004f
            r7 = 1
            goto L_0x0050
        L_0x004f:
            r7 = 0
        L_0x0050:
            int r7 = r7 + r5
            java.lang.String[] r8 = new java.lang.String[r7]     // Catch:{ Exception -> 0x06a5 }
            r8[r4] = r2     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x0059
            r8[r5] = r3     // Catch:{ Exception -> 0x06a5 }
        L_0x0059:
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r3.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r9.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r10.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r11.<init>()     // Catch:{ Exception -> 0x06a5 }
            android.util.LongSparseArray r12 = new android.util.LongSparseArray     // Catch:{ Exception -> 0x06a5 }
            r12.<init>()     // Catch:{ Exception -> 0x06a5 }
            int r13 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.MessagesStorage r13 = org.telegram.messenger.MessagesStorage.getInstance(r13)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteDatabase r13 = r13.getDatabase()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600"
            java.lang.Object[] r15 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteCursor r13 = r13.queryFinalized(r14, r15)     // Catch:{ Exception -> 0x06a5 }
        L_0x0084:
            boolean r14 = r13.next()     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x00fe
            long r5 = r13.longValue(r4)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r14 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x06a5 }
            r4 = 0
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r4 = 1
            int r15 = r13.intValue(r4)     // Catch:{ Exception -> 0x06a5 }
            r14.date = r15     // Catch:{ Exception -> 0x06a5 }
            r12.put(r5, r14)     // Catch:{ Exception -> 0x06a5 }
            int r4 = (int) r5     // Catch:{ Exception -> 0x06a5 }
            r14 = 32
            long r5 = r5 >> r14
            int r6 = (int) r5     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x00e3
            if (r4 <= 0) goto L_0x00c8
            int r5 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r6 = 4
            if (r5 != r6) goto L_0x00b1
            int r5 = r1.selfUserId     // Catch:{ Exception -> 0x06a5 }
            if (r4 != r5) goto L_0x00b1
            goto L_0x00cd
        L_0x00b1:
            int r5 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r6 = 2
            if (r5 == r6) goto L_0x00cd
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x06a5 }
            boolean r5 = r3.contains(r5)     // Catch:{ Exception -> 0x06a5 }
            if (r5 != 0) goto L_0x00cd
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x06a5 }
            r3.add(r4)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x00cd
        L_0x00c8:
            int r5 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r6 = 4
            if (r5 != r6) goto L_0x00d0
        L_0x00cd:
            r4 = 0
            r5 = 1
            goto L_0x0084
        L_0x00d0:
            int r4 = -r4
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x06a5 }
            boolean r5 = r9.contains(r5)     // Catch:{ Exception -> 0x06a5 }
            if (r5 != 0) goto L_0x00cd
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x06a5 }
            r9.add(r4)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x00cd
        L_0x00e3:
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x00ec
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r5 = 3
            if (r4 != r5) goto L_0x00cd
        L_0x00ec:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x06a5 }
            boolean r4 = r10.contains(r4)     // Catch:{ Exception -> 0x06a5 }
            if (r4 != 0) goto L_0x00cd
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x06a5 }
            r10.add(r4)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x00cd
        L_0x00fe:
            r13.dispose()     // Catch:{ Exception -> 0x06a5 }
            int r4 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r5 = 4
            if (r4 == r5) goto L_0x012d
            boolean r2 = r0.startsWith(r2)     // Catch:{ Exception -> 0x06a5 }
            if (r2 == 0) goto L_0x012d
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult     // Catch:{ Exception -> 0x06a5 }
            r5 = 0
            r4.<init>()     // Catch:{ Exception -> 0x06a5 }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.date = r5     // Catch:{ Exception -> 0x06a5 }
            r4.name = r0     // Catch:{ Exception -> 0x06a5 }
            r4.object = r2     // Catch:{ Exception -> 0x06a5 }
            int r0 = r2.id     // Catch:{ Exception -> 0x06a5 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x06a5 }
            r12.put(r5, r4)     // Catch:{ Exception -> 0x06a5 }
            r0 = 1
            goto L_0x012e
        L_0x012d:
            r0 = 0
        L_0x012e:
            boolean r2 = r3.isEmpty()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r5 = ";;;"
            java.lang.String r6 = ","
            java.lang.String r13 = "@"
            java.lang.String r15 = " "
            if (r2 != 0) goto L_0x0275
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x06a5 }
            java.util.Locale r14 = java.util.Locale.US     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r4 = "SELECT data, status, name FROM users WHERE uid IN(%s)"
            r18 = r0
            r19 = r11
            r0 = 1
            java.lang.Object[] r11 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r0 = android.text.TextUtils.join(r6, r3)     // Catch:{ Exception -> 0x06a5 }
            r3 = 0
            r11[r3] = r0     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r0 = java.lang.String.format(r14, r4, r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.Object[] r4 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteCursor r0 = r2.queryFinalized(r0, r4)     // Catch:{ Exception -> 0x06a5 }
        L_0x0162:
            boolean r2 = r0.next()     // Catch:{ Exception -> 0x06a5 }
            if (r2 == 0) goto L_0x026b
            r2 = 2
            java.lang.String r3 = r0.stringValue(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r2.getTranslitString(r3)     // Catch:{ Exception -> 0x06a5 }
            boolean r2 = r3.equals(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r2 == 0) goto L_0x017d
            r2 = 0
            goto L_0x017e
        L_0x017d:
            r2 = r14
        L_0x017e:
            int r4 = r3.lastIndexOf(r5)     // Catch:{ Exception -> 0x06a5 }
            r11 = -1
            if (r4 == r11) goto L_0x018d
            int r4 = r4 + 3
            java.lang.String r14 = r3.substring(r4)     // Catch:{ Exception -> 0x06a5 }
            r4 = r14
            goto L_0x018e
        L_0x018d:
            r4 = 0
        L_0x018e:
            r11 = 0
            r14 = 0
        L_0x0190:
            if (r14 >= r7) goto L_0x0261
            r20 = r11
            r11 = r8[r14]     // Catch:{ Exception -> 0x06a5 }
            boolean r21 = r3.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r21 != 0) goto L_0x01de
            r21 = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r3.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x01e0
            if (r2 == 0) goto L_0x01d1
            boolean r14 = r2.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x01e0
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r2.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x01d1
            goto L_0x01e0
        L_0x01d1:
            if (r4 == 0) goto L_0x01db
            boolean r14 = r4.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x01db
            r14 = 2
            goto L_0x01e1
        L_0x01db:
            r14 = r20
            goto L_0x01e1
        L_0x01de:
            r21 = r14
        L_0x01e0:
            r14 = 1
        L_0x01e1:
            if (r14 == 0) goto L_0x024e
            r2 = 0
            org.telegram.tgnet.NativeByteBuffer r3 = r0.byteBufferValue(r2)     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x0261
            int r4 = r3.readInt32(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$User r4 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r3, r4, r2)     // Catch:{ Exception -> 0x06a5 }
            r3.reuse()     // Catch:{ Exception -> 0x06a5 }
            int r2 = r4.id     // Catch:{ Exception -> 0x06a5 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x06a5 }
            java.lang.Object r2 = r12.get(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$UserStatus r3 = r4.status     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x0210
            org.telegram.tgnet.TLRPC$UserStatus r3 = r4.status     // Catch:{ Exception -> 0x06a5 }
            r21 = r5
            r22 = r10
            r5 = 1
            int r10 = r0.intValue(r5)     // Catch:{ Exception -> 0x06a5 }
            r3.expires = r10     // Catch:{ Exception -> 0x06a5 }
            goto L_0x0214
        L_0x0210:
            r21 = r5
            r22 = r10
        L_0x0214:
            r3 = 1
            if (r14 != r3) goto L_0x0222
            java.lang.String r3 = r4.first_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r5 = r4.last_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r5, r11)     // Catch:{ Exception -> 0x06a5 }
            r2.name = r3     // Catch:{ Exception -> 0x06a5 }
            goto L_0x0249
        L_0x0222:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r3.<init>()     // Catch:{ Exception -> 0x06a5 }
            r3.append(r13)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r5 = r4.username     // Catch:{ Exception -> 0x06a5 }
            r3.append(r5)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x06a5 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r5.<init>()     // Catch:{ Exception -> 0x06a5 }
            r5.append(r13)     // Catch:{ Exception -> 0x06a5 }
            r5.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x06a5 }
            r10 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r10, r5)     // Catch:{ Exception -> 0x06a5 }
            r2.name = r3     // Catch:{ Exception -> 0x06a5 }
        L_0x0249:
            r2.object = r4     // Catch:{ Exception -> 0x06a5 }
            int r18 = r18 + 1
            goto L_0x0265
        L_0x024e:
            r20 = r2
            r22 = r10
            r2 = r21
            r21 = r5
            r5 = r14
            int r2 = r2 + 1
            r14 = r2
            r11 = r5
            r2 = r20
            r5 = r21
            goto L_0x0190
        L_0x0261:
            r21 = r5
            r22 = r10
        L_0x0265:
            r5 = r21
            r10 = r22
            goto L_0x0162
        L_0x026b:
            r21 = r5
            r22 = r10
            r0.dispose()     // Catch:{ Exception -> 0x06a5 }
            r0 = r18
            goto L_0x027d
        L_0x0275:
            r18 = r0
            r21 = r5
            r22 = r10
            r19 = r11
        L_0x027d:
            boolean r2 = r9.isEmpty()     // Catch:{ Exception -> 0x06a5 }
            if (r2 != 0) goto L_0x0340
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x06a5 }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r4 = "SELECT data, name FROM chats WHERE uid IN(%s)"
            r5 = 1
            java.lang.Object[] r10 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r5 = android.text.TextUtils.join(r6, r9)     // Catch:{ Exception -> 0x06a5 }
            r9 = 0
            r10[r9] = r5     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = java.lang.String.format(r3, r4, r10)     // Catch:{ Exception -> 0x06a5 }
            java.lang.Object[] r4 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x06a5 }
        L_0x02a5:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x033d
            r3 = 1
            java.lang.String r4 = r2.stringValue(r3)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = r3.getTranslitString(r4)     // Catch:{ Exception -> 0x06a5 }
            boolean r5 = r4.equals(r3)     // Catch:{ Exception -> 0x06a5 }
            if (r5 == 0) goto L_0x02bf
            r3 = 0
        L_0x02bf:
            r5 = 0
        L_0x02c0:
            if (r5 >= r7) goto L_0x02a5
            r9 = r8[r5]     // Catch:{ Exception -> 0x06a5 }
            boolean r10 = r4.startsWith(r9)     // Catch:{ Exception -> 0x06a5 }
            if (r10 != 0) goto L_0x0300
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r10.<init>()     // Catch:{ Exception -> 0x06a5 }
            r10.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r10.append(r9)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r10 = r4.contains(r10)     // Catch:{ Exception -> 0x06a5 }
            if (r10 != 0) goto L_0x0300
            if (r3 == 0) goto L_0x02fd
            boolean r10 = r3.startsWith(r9)     // Catch:{ Exception -> 0x06a5 }
            if (r10 != 0) goto L_0x0300
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r10.<init>()     // Catch:{ Exception -> 0x06a5 }
            r10.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r10.append(r9)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r10 = r3.contains(r10)     // Catch:{ Exception -> 0x06a5 }
            if (r10 == 0) goto L_0x02fd
            goto L_0x0300
        L_0x02fd:
            int r5 = r5 + 1
            goto L_0x02c0
        L_0x0300:
            r3 = 0
            org.telegram.tgnet.NativeByteBuffer r4 = r2.byteBufferValue(r3)     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x02a5
            int r5 = r4.readInt32(r3)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$Chat r5 = org.telegram.tgnet.TLRPC$Chat.TLdeserialize(r4, r5, r3)     // Catch:{ Exception -> 0x06a5 }
            r4.reuse()     // Catch:{ Exception -> 0x06a5 }
            if (r5 == 0) goto L_0x02a5
            boolean r3 = r5.deactivated     // Catch:{ Exception -> 0x06a5 }
            if (r3 != 0) goto L_0x02a5
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r5)     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x0324
            boolean r3 = org.telegram.messenger.ChatObject.isNotInChat(r5)     // Catch:{ Exception -> 0x06a5 }
            if (r3 != 0) goto L_0x02a5
        L_0x0324:
            int r3 = r5.id     // Catch:{ Exception -> 0x06a5 }
            int r3 = -r3
            long r3 = (long) r3     // Catch:{ Exception -> 0x06a5 }
            java.lang.Object r3 = r12.get(r3)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r3     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r4 = r5.title     // Catch:{ Exception -> 0x06a5 }
            r10 = 0
            java.lang.CharSequence r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r10, r9)     // Catch:{ Exception -> 0x06a5 }
            r3.name = r4     // Catch:{ Exception -> 0x06a5 }
            r3.object = r5     // Catch:{ Exception -> 0x06a5 }
            int r0 = r0 + 1
            goto L_0x02a5
        L_0x033d:
            r2.dispose()     // Catch:{ Exception -> 0x06a5 }
        L_0x0340:
            boolean r2 = r22.isEmpty()     // Catch:{ Exception -> 0x06a5 }
            if (r2 != 0) goto L_0x052e
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x06a5 }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r4 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)"
            r5 = 1
            java.lang.Object[] r9 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x06a5 }
            r5 = r22
            java.lang.String r5 = android.text.TextUtils.join(r6, r5)     // Catch:{ Exception -> 0x06a5 }
            r6 = 0
            r9[r6] = r5     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = java.lang.String.format(r3, r4, r9)     // Catch:{ Exception -> 0x06a5 }
            java.lang.Object[] r4 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x06a5 }
        L_0x036a:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x0524
            r3 = 1
            java.lang.String r4 = r2.stringValue(r3)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r3 = r3.getTranslitString(r4)     // Catch:{ Exception -> 0x06a5 }
            boolean r5 = r4.equals(r3)     // Catch:{ Exception -> 0x06a5 }
            if (r5 == 0) goto L_0x0384
            r3 = 0
        L_0x0384:
            r5 = r21
            int r6 = r4.lastIndexOf(r5)     // Catch:{ Exception -> 0x06a5 }
            r9 = -1
            if (r6 == r9) goto L_0x0394
            int r6 = r6 + 2
            java.lang.String r6 = r4.substring(r6)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x0395
        L_0x0394:
            r6 = 0
        L_0x0395:
            r9 = 0
            r10 = 0
        L_0x0397:
            if (r10 >= r7) goto L_0x0516
            r11 = r8[r10]     // Catch:{ Exception -> 0x06a5 }
            boolean r17 = r4.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r17 != 0) goto L_0x03de
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r4.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x03de
            if (r3 == 0) goto L_0x03d4
            boolean r14 = r3.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x03de
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r15)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r3.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x03d4
            goto L_0x03de
        L_0x03d4:
            if (r6 == 0) goto L_0x03df
            boolean r14 = r6.startsWith(r11)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x03df
            r9 = 2
            goto L_0x03df
        L_0x03de:
            r9 = 1
        L_0x03df:
            if (r9 == 0) goto L_0x050c
            r14 = 0
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x03f4
            int r4 = r3.readInt32(r14)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = org.telegram.tgnet.TLRPC$EncryptedChat.TLdeserialize(r3, r4, r14)     // Catch:{ Exception -> 0x06a5 }
            r3.reuse()     // Catch:{ Exception -> 0x06a5 }
            goto L_0x03f5
        L_0x03f4:
            r4 = 0
        L_0x03f5:
            r3 = 6
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r3)     // Catch:{ Exception -> 0x06a5 }
            if (r3 == 0) goto L_0x040a
            r6 = 0
            int r10 = r3.readInt32(r6)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$User r14 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r3, r10, r6)     // Catch:{ Exception -> 0x06a5 }
            r3.reuse()     // Catch:{ Exception -> 0x06a5 }
            r3 = r14
            goto L_0x040b
        L_0x040a:
            r3 = 0
        L_0x040b:
            if (r4 == 0) goto L_0x0516
            if (r3 == 0) goto L_0x0516
            int r6 = r4.id     // Catch:{ Exception -> 0x06a5 }
            r18 = r15
            long r14 = (long) r6     // Catch:{ Exception -> 0x06a5 }
            r20 = 32
            long r14 = r14 << r20
            java.lang.Object r6 = r12.get(r14)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r6 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r6     // Catch:{ Exception -> 0x06a5 }
            r10 = 2
            int r14 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.user_id = r14     // Catch:{ Exception -> 0x06a5 }
            r10 = 3
            byte[] r14 = r2.byteArrayValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.a_or_b = r14     // Catch:{ Exception -> 0x06a5 }
            r15 = 4
            byte[] r10 = r2.byteArrayValue(r15)     // Catch:{ Exception -> 0x06a5 }
            r4.auth_key = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 5
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.ttl = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 8
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.layer = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 9
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.seq_in = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 10
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.seq_out = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 11
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            int r14 = r10 >> 16
            short r14 = (short) r14     // Catch:{ Exception -> 0x06a5 }
            r4.key_use_count_in = r14     // Catch:{ Exception -> 0x06a5 }
            short r10 = (short) r10     // Catch:{ Exception -> 0x06a5 }
            r4.key_use_count_out = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 12
            long r14 = r2.longValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.exchange_id = r14     // Catch:{ Exception -> 0x06a5 }
            r10 = 13
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.key_create_date = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 14
            long r14 = r2.longValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.future_key_fingerprint = r14     // Catch:{ Exception -> 0x06a5 }
            r10 = 15
            byte[] r10 = r2.byteArrayValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.future_auth_key = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 16
            byte[] r10 = r2.byteArrayValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.key_hash = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 17
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.in_seq_no = r10     // Catch:{ Exception -> 0x06a5 }
            r10 = 18
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            if (r10 == 0) goto L_0x049a
            r4.admin_id = r10     // Catch:{ Exception -> 0x06a5 }
        L_0x049a:
            r10 = 19
            int r10 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.mtproto_seq = r10     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$UserStatus r10 = r3.status     // Catch:{ Exception -> 0x06a5 }
            if (r10 == 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$UserStatus r10 = r3.status     // Catch:{ Exception -> 0x06a5 }
            r14 = 7
            int r14 = r2.intValue(r14)     // Catch:{ Exception -> 0x06a5 }
            r10.expires = r14     // Catch:{ Exception -> 0x06a5 }
        L_0x04af:
            r10 = 1
            if (r9 != r10) goto L_0x04db
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r3.first_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r11 = r3.last_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)     // Catch:{ Exception -> 0x06a5 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x06a5 }
            r6.name = r9     // Catch:{ Exception -> 0x06a5 }
            android.text.SpannableStringBuilder r9 = (android.text.SpannableStringBuilder) r9     // Catch:{ Exception -> 0x06a5 }
            android.text.style.ForegroundColorSpan r10 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r11 = "chats_secretName"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)     // Catch:{ Exception -> 0x06a5 }
            r10.<init>(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.CharSequence r11 = r6.name     // Catch:{ Exception -> 0x06a5 }
            int r11 = r11.length()     // Catch:{ Exception -> 0x06a5 }
            r14 = 33
            r15 = 0
            r9.setSpan(r10, r15, r11, r14)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x0502
        L_0x04db:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r9.<init>()     // Catch:{ Exception -> 0x06a5 }
            r9.append(r13)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r3.username     // Catch:{ Exception -> 0x06a5 }
            r9.append(r10)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x06a5 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r10.<init>()     // Catch:{ Exception -> 0x06a5 }
            r10.append(r13)     // Catch:{ Exception -> 0x06a5 }
            r10.append(r11)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x06a5 }
            r11 = 0
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.generateSearchName(r9, r11, r10)     // Catch:{ Exception -> 0x06a5 }
            r6.name = r9     // Catch:{ Exception -> 0x06a5 }
        L_0x0502:
            r6.object = r4     // Catch:{ Exception -> 0x06a5 }
            r11 = r19
            r11.add(r3)     // Catch:{ Exception -> 0x06a5 }
            int r0 = r0 + 1
            goto L_0x051c
        L_0x050c:
            r18 = r15
            r11 = r19
            r20 = 32
            int r10 = r10 + 1
            goto L_0x0397
        L_0x0516:
            r18 = r15
            r11 = r19
            r20 = 32
        L_0x051c:
            r21 = r5
            r19 = r11
            r15 = r18
            goto L_0x036a
        L_0x0524:
            r18 = r15
            r11 = r19
            r5 = r21
            r2.dispose()     // Catch:{ Exception -> 0x06a5 }
            goto L_0x0534
        L_0x052e:
            r18 = r15
            r11 = r19
            r5 = r21
        L_0x0534:
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x06a5 }
            r0 = 0
        L_0x053a:
            int r3 = r12.size()     // Catch:{ Exception -> 0x06a5 }
            if (r0 >= r3) goto L_0x0554
            java.lang.Object r3 = r12.valueAt(r0)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r3     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLObject r4 = r3.object     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x0551
            java.lang.CharSequence r4 = r3.name     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x0551
            r2.add(r3)     // Catch:{ Exception -> 0x06a5 }
        L_0x0551:
            int r0 = r0 + 1
            goto L_0x053a
        L_0x0554:
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg r0 = org.telegram.ui.Adapters.$$Lambda$DialogsSearchAdapter$_pBPL6sPCHJiVM9cPl1od_rxvJg.INSTANCE     // Catch:{ Exception -> 0x06a5 }
            java.util.Collections.sort(r2, r0)     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r0.<init>()     // Catch:{ Exception -> 0x06a5 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ Exception -> 0x06a5 }
            r3.<init>()     // Catch:{ Exception -> 0x06a5 }
            r4 = 0
        L_0x0564:
            int r6 = r2.size()     // Catch:{ Exception -> 0x06a5 }
            if (r4 >= r6) goto L_0x057d
            java.lang.Object r6 = r2.get(r4)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult r6 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r6     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLObject r9 = r6.object     // Catch:{ Exception -> 0x06a5 }
            r0.add(r9)     // Catch:{ Exception -> 0x06a5 }
            java.lang.CharSequence r6 = r6.name     // Catch:{ Exception -> 0x06a5 }
            r3.add(r6)     // Catch:{ Exception -> 0x06a5 }
            int r4 = r4 + 1
            goto L_0x0564
        L_0x057d:
            int r2 = r1.dialogsType     // Catch:{ Exception -> 0x06a5 }
            r4 = 2
            if (r2 == r4) goto L_0x069f
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r4 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid"
            r6 = 0
            java.lang.Object[] r9 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x06a5 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r4, r9)     // Catch:{ Exception -> 0x06a5 }
        L_0x0595:
            boolean r4 = r2.next()     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x069c
            r4 = 3
            int r6 = r2.intValue(r4)     // Catch:{ Exception -> 0x06a5 }
            long r9 = (long) r6     // Catch:{ Exception -> 0x06a5 }
            int r6 = r12.indexOfKey(r9)     // Catch:{ Exception -> 0x06a5 }
            if (r6 < 0) goto L_0x05a8
            goto L_0x0595
        L_0x05a8:
            r6 = 2
            java.lang.String r9 = r2.stringValue(r6)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r10.getTranslitString(r9)     // Catch:{ Exception -> 0x06a5 }
            boolean r15 = r9.equals(r10)     // Catch:{ Exception -> 0x06a5 }
            if (r15 == 0) goto L_0x05bc
            r10 = 0
        L_0x05bc:
            int r15 = r9.lastIndexOf(r5)     // Catch:{ Exception -> 0x06a5 }
            r4 = -1
            if (r15 == r4) goto L_0x05cc
            int r15 = r15 + 3
            java.lang.String r17 = r9.substring(r15)     // Catch:{ Exception -> 0x06a5 }
            r15 = r17
            goto L_0x05cd
        L_0x05cc:
            r15 = 0
        L_0x05cd:
            r4 = 0
            r16 = 0
        L_0x05d0:
            if (r4 >= r7) goto L_0x0692
            r6 = r8[r4]     // Catch:{ Exception -> 0x06a5 }
            boolean r17 = r9.startsWith(r6)     // Catch:{ Exception -> 0x06a5 }
            if (r17 != 0) goto L_0x061e
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r21 = r5
            r5 = r18
            r14.append(r5)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r6)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r9.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x0622
            if (r10 == 0) goto L_0x0611
            boolean r14 = r10.startsWith(r6)     // Catch:{ Exception -> 0x06a5 }
            if (r14 != 0) goto L_0x0622
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r5)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r6)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            boolean r14 = r10.contains(r14)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x0611
            goto L_0x0622
        L_0x0611:
            if (r15 == 0) goto L_0x061b
            boolean r14 = r15.startsWith(r6)     // Catch:{ Exception -> 0x06a5 }
            if (r14 == 0) goto L_0x061b
            r14 = 2
            goto L_0x0623
        L_0x061b:
            r14 = r16
            goto L_0x0623
        L_0x061e:
            r21 = r5
            r5 = r18
        L_0x0622:
            r14 = 1
        L_0x0623:
            if (r14 == 0) goto L_0x0683
            r18 = r5
            r5 = 0
            org.telegram.tgnet.NativeByteBuffer r4 = r2.byteBufferValue(r5)     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x0695
            int r9 = r4.readInt32(r5)     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$User r9 = org.telegram.tgnet.TLRPC$User.TLdeserialize(r4, r9, r5)     // Catch:{ Exception -> 0x06a5 }
            r4.reuse()     // Catch:{ Exception -> 0x06a5 }
            org.telegram.tgnet.TLRPC$UserStatus r4 = r9.status     // Catch:{ Exception -> 0x06a5 }
            if (r4 == 0) goto L_0x0646
            org.telegram.tgnet.TLRPC$UserStatus r4 = r9.status     // Catch:{ Exception -> 0x06a5 }
            r10 = 1
            int r15 = r2.intValue(r10)     // Catch:{ Exception -> 0x06a5 }
            r4.expires = r15     // Catch:{ Exception -> 0x06a5 }
        L_0x0646:
            r4 = 1
            if (r14 != r4) goto L_0x0656
            java.lang.String r10 = r9.first_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r9.last_name     // Catch:{ Exception -> 0x06a5 }
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r14, r6)     // Catch:{ Exception -> 0x06a5 }
            r3.add(r6)     // Catch:{ Exception -> 0x06a5 }
            r14 = 0
            goto L_0x067e
        L_0x0656:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r10.<init>()     // Catch:{ Exception -> 0x06a5 }
            r10.append(r13)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r14 = r9.username     // Catch:{ Exception -> 0x06a5 }
            r10.append(r14)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x06a5 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06a5 }
            r14.<init>()     // Catch:{ Exception -> 0x06a5 }
            r14.append(r13)     // Catch:{ Exception -> 0x06a5 }
            r14.append(r6)     // Catch:{ Exception -> 0x06a5 }
            java.lang.String r6 = r14.toString()     // Catch:{ Exception -> 0x06a5 }
            r14 = 0
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r10, r14, r6)     // Catch:{ Exception -> 0x06a5 }
            r3.add(r6)     // Catch:{ Exception -> 0x06a5 }
        L_0x067e:
            r0.add(r9)     // Catch:{ Exception -> 0x06a5 }
            r6 = r14
            goto L_0x0696
        L_0x0683:
            r18 = r5
            r5 = 0
            r6 = 0
            r16 = 1
            int r4 = r4 + 1
            r16 = r14
            r5 = r21
            r6 = 2
            goto L_0x05d0
        L_0x0692:
            r21 = r5
            r5 = 0
        L_0x0695:
            r6 = 0
        L_0x0696:
            r16 = 1
            r5 = r21
            goto L_0x0595
        L_0x069c:
            r2.dispose()     // Catch:{ Exception -> 0x06a5 }
        L_0x069f:
            r2 = r25
            r1.updateSearchResults(r0, r3, r11, r2)     // Catch:{ Exception -> 0x06a5 }
            goto L_0x06a9
        L_0x06a5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06a9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.lambda$searchDialogsInternal$9$DialogsSearchAdapter(java.lang.String, int):void");
    }

    static /* synthetic */ int lambda$null$8(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
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
        if (i == this.lastSearchId) {
            this.lastLocalSearchId = i;
            if (this.lastGlobalSearchId != i) {
                this.searchAdapterHelper.clear();
            }
            if (this.lastMessagesSearchId != i) {
                this.searchResultMessages.clear();
            }
            this.searchWas = true;
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
            if (this.delegate == null) {
                return;
            }
            if (getItemCount() != 0 || (this.searchRunnable2 == null && !this.searchAdapterHelper.isSearchInProgress())) {
                this.delegate.searchStateChanged(false);
            } else {
                this.delegate.searchStateChanged(true);
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
                searchMessagesInternal((String) null, 0);
                notifyDataSetChanged();
                return;
            }
            if (this.needMessagesSearch == 2 || !trim.startsWith("#") || trim.length() != 1) {
                this.searchResultHashtags.clear();
                notifyDataSetChanged();
            } else {
                this.messagesSearchEndReached = true;
                if (this.searchAdapterHelper.loadRecentHashtags()) {
                    this.searchResultMessages.clear();
                    this.searchResultHashtags.clear();
                    ArrayList<SearchAdapterHelper.HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                    for (int i = 0; i < hashtags.size(); i++) {
                        this.searchResultHashtags.add(hashtags.get(i).hashtag);
                    }
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate2 = this.delegate;
                    if (dialogsSearchAdapterDelegate2 != null) {
                        dialogsSearchAdapterDelegate2.searchStateChanged(false);
                    }
                }
                notifyDataSetChanged();
            }
            int i2 = this.lastSearchId + 1;
            this.lastSearchId = i2;
            if (!(this.needMessagesSearch == 2 || (dialogsSearchAdapterDelegate = this.delegate) == null)) {
                dialogsSearchAdapterDelegate.searchStateChanged(true);
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
            }
            searchMessagesInternal(str2, i2);
        }
    }

    public int getItemCount() {
        if (isRecentSearchDisplayed()) {
            int i = 0;
            int size = !this.recentSearchObjects.isEmpty() ? this.recentSearchObjects.size() + 1 : 0;
            if (!MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                i = 2;
            }
            return size + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            int size2 = this.searchResult.size();
            int size3 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size4 = this.searchAdapterHelper.getGlobalSearch().size();
            int size5 = this.searchAdapterHelper.getPhoneSearch().size();
            int size6 = this.searchResultMessages.size();
            int i2 = size2 + size3;
            if (size4 != 0) {
                i2 += size4 + 1;
            }
            if (size5 != 0) {
                i2 += size5;
            }
            return size6 != 0 ? i2 + size6 + 1 + (this.messagesSearchEndReached ^ true ? 1 : 0) : i2;
        }
    }

    public Object getItem(int i) {
        int i2;
        Object obj;
        int i3 = 0;
        if (isRecentSearchDisplayed()) {
            if (!MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                i3 = 2;
            }
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
            int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                i3 = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < size) {
                return this.searchResult.get(i);
            }
            int i4 = i - size;
            if (i4 >= 0 && i4 < size2) {
                return localServerSearch.get(i4);
            }
            int i5 = i4 - size2;
            if (i5 >= 0 && i5 < size3) {
                return phoneSearch.get(i5);
            }
            int i6 = i5 - size3;
            if (i6 > 0 && i6 < size4) {
                return globalSearch.get(i6 - 1);
            }
            int i7 = i6 - size4;
            if (i7 <= 0 || i7 >= i3) {
                return null;
            }
            return this.searchResultMessages.get(i7 - 1);
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
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x028a, code lost:
        if (r0.startsWith("@" + r3.username) != false) goto L_0x028c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0374  */
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
            if (r3 == 0) goto L_0x0181
            if (r3 == r7) goto L_0x00ac
            if (r3 == r4) goto L_0x0088
            r8 = 4
            if (r3 == r8) goto L_0x0069
            r8 = 5
            if (r3 == r8) goto L_0x0059
            r4 = 6
            if (r3 == r4) goto L_0x001f
            goto L_0x037a
        L_0x001f:
            java.lang.Object r2 = r1.getItem(r2)
            java.lang.String r2 = (java.lang.String) r2
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
            java.lang.String r3 = "windowBackgroundWhiteBlueText2"
            r0.setColors(r5, r3)
            r3 = 2131624151(0x7f0e00d7, float:1.8875474E38)
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
            goto L_0x037a
        L_0x0059:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Adapters.DialogsSearchAdapter$CategoryAdapterRecycler r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0
            int r2 = r2 / r4
            r0.setIndex(r2)
            goto L_0x037a
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
            goto L_0x037a
        L_0x0088:
            android.view.View r0 = r0.itemView
            r8 = r0
            org.telegram.ui.Cells.DialogCell r8 = (org.telegram.ui.Cells.DialogCell) r8
            int r0 = r16.getItemCount()
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
            goto L_0x037a
        L_0x00ac:
            android.view.View r0 = r0.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            boolean r3 = r16.isRecentSearchDisplayed()
            r5 = 2131624787(0x7f0e0353, float:1.8876764E38)
            java.lang.String r8 = "ClearButton"
            if (r3 == 0) goto L_0x00f2
            int r3 = r1.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x00ca
            goto L_0x00cb
        L_0x00ca:
            r4 = 0
        L_0x00cb:
            if (r2 >= r4) goto L_0x00db
            r2 = 2131624704(0x7f0e0300, float:1.8876595E38)
            java.lang.String r3 = "ChatHints"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x037a
        L_0x00db:
            r2 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            java.lang.String r3 = "Recent"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r5)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$honrBco-zV9w0fwaI91SKdwfMI0 r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$honrBco-zV9w0fwaI91SKdwfMI0
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x037a
        L_0x00f2:
            java.util.ArrayList<java.lang.String> r3 = r1.searchResultHashtags
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0111
            r2 = 2131625526(0x7f0e0636, float:1.8878262E38)
            java.lang.String r3 = "Hashtags"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r5)
            org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YI57NotkimI_aZA_lycnYL-wN7k r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YI57NotkimI_aZA_lycnYL-wN7k
            r4.<init>()
            r0.setText(r2, r3, r4)
            goto L_0x037a
        L_0x0111:
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
            if (r9 == 0) goto L_0x0138
            goto L_0x013e
        L_0x0138:
            int r3 = r3.size()
            int r6 = r3 + 1
        L_0x013e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0147
            goto L_0x014c
        L_0x0147:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.searchResultMessages
            r3.size()
        L_0x014c:
            int r4 = r4 + r5
            int r2 = r2 - r4
            if (r2 < 0) goto L_0x0160
            if (r2 >= r8) goto L_0x0160
            r2 = 2131626486(0x7f0e09f6, float:1.888021E38)
            java.lang.String r3 = "PhoneNumberSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x037a
        L_0x0160:
            int r2 = r2 - r8
            if (r2 < 0) goto L_0x0173
            if (r2 >= r6) goto L_0x0173
            r2 = 2131625481(0x7f0e0609, float:1.8878171E38)
            java.lang.String r3 = "GlobalSearch"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x037a
        L_0x0173:
            r2 = 2131626792(0x7f0e0b28, float:1.888083E38)
            java.lang.String r3 = "SearchMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            goto L_0x037a
        L_0x0181:
            android.view.View r0 = r0.itemView
            r8 = r0
            org.telegram.ui.Cells.ProfileSearchCell r8 = (org.telegram.ui.Cells.ProfileSearchCell) r8
            java.lang.Object r0 = r1.getItem(r2)
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$User
            if (r3 == 0) goto L_0x0197
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            java.lang.String r3 = r0.username
            r10 = r3
            r9 = r5
            r11 = r9
            r3 = r0
            goto L_0x01e6
        L_0x0197:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r3 == 0) goto L_0x01b8
            int r3 = r1.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC$Chat) r0
            int r9 = r0.id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r9)
            if (r3 != 0) goto L_0x01b0
            goto L_0x01b1
        L_0x01b0:
            r0 = r3
        L_0x01b1:
            java.lang.String r3 = r0.username
            r9 = r0
            r10 = r3
            r3 = r5
            r11 = r3
            goto L_0x01e6
        L_0x01b8:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$EncryptedChat
            if (r3 == 0) goto L_0x01e2
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
            goto L_0x01e6
        L_0x01e2:
            r3 = r5
            r9 = r3
            r10 = r9
            r11 = r10
        L_0x01e6:
            boolean r0 = r16.isRecentSearchDisplayed()
            if (r0 == 0) goto L_0x01fc
            int r0 = r16.getItemCount()
            int r0 = r0 - r7
            if (r2 == r0) goto L_0x01f5
            r0 = 1
            goto L_0x01f6
        L_0x01f5:
            r0 = 0
        L_0x01f6:
            r8.useSeparator = r0
            r10 = r5
            r13 = 1
            goto L_0x0313
        L_0x01fc:
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
            if (r15 <= 0) goto L_0x022b
            int r5 = r15 + -1
            java.lang.Object r5 = r12.get(r5)
            boolean r5 = r5 instanceof java.lang.String
            if (r5 == 0) goto L_0x022b
            int r5 = r15 + -2
            goto L_0x022c
        L_0x022b:
            r5 = r15
        L_0x022c:
            boolean r12 = r0.isEmpty()
            if (r12 == 0) goto L_0x0234
            r0 = 0
            goto L_0x0239
        L_0x0234:
            int r0 = r0.size()
            int r0 = r0 + r7
        L_0x0239:
            int r12 = r16.getItemCount()
            int r12 = r12 - r7
            if (r2 == r12) goto L_0x024d
            int r5 = r5 + r13
            int r5 = r5 + r14
            int r5 = r5 - r7
            if (r2 == r5) goto L_0x024d
            int r13 = r13 + r0
            int r13 = r13 + r15
            int r13 = r13 + r14
            int r13 = r13 - r7
            if (r2 == r13) goto L_0x024d
            r0 = 1
            goto L_0x024e
        L_0x024d:
            r0 = 0
        L_0x024e:
            r8.useSeparator = r0
            java.util.ArrayList<org.telegram.tgnet.TLObject> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r5 = "@"
            if (r2 >= r0) goto L_0x0292
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r2)
            r10 = r0
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            if (r10 == 0) goto L_0x028f
            if (r3 == 0) goto L_0x028f
            java.lang.String r0 = r3.username
            if (r0 == 0) goto L_0x028f
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x028f
            java.lang.String r0 = r10.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            java.lang.String r5 = r3.username
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x028f
        L_0x028c:
            r5 = 0
            goto L_0x0312
        L_0x028f:
            r5 = r10
            goto L_0x0311
        L_0x0292:
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0310
            if (r3 == 0) goto L_0x02a9
            java.lang.String r2 = r3.first_name
            java.lang.String r12 = r3.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r12)
            goto L_0x02af
        L_0x02a9:
            if (r9 == 0) goto L_0x02ae
            java.lang.String r2 = r9.title
            goto L_0x02af
        L_0x02ae:
            r2 = 0
        L_0x02af:
            r12 = 33
            java.lang.String r13 = "windowBackgroundWhiteBlueText4"
            r14 = -1
            if (r2 == 0) goto L_0x02d3
            int r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r0)
            if (r15 == r14) goto L_0x02d3
            android.text.SpannableStringBuilder r10 = new android.text.SpannableStringBuilder
            r10.<init>(r2)
            android.text.style.ForegroundColorSpan r2 = new android.text.style.ForegroundColorSpan
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r2.<init>(r5)
            int r0 = r0.length()
            int r0 = r0 + r15
            r10.setSpan(r2, r15, r0, r12)
            goto L_0x028f
        L_0x02d3:
            if (r10 == 0) goto L_0x0310
            boolean r2 = r0.startsWith(r5)
            if (r2 == 0) goto L_0x02df
            java.lang.String r0 = r0.substring(r7)
        L_0x02df:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x030a }
            r2.<init>()     // Catch:{ Exception -> 0x030a }
            r2.append(r5)     // Catch:{ Exception -> 0x030a }
            r2.append(r10)     // Catch:{ Exception -> 0x030a }
            int r5 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r0)     // Catch:{ Exception -> 0x030a }
            if (r5 == r14) goto L_0x0308
            int r0 = r0.length()     // Catch:{ Exception -> 0x030a }
            if (r5 != 0) goto L_0x02f9
            int r0 = r0 + 1
            goto L_0x02fb
        L_0x02f9:
            int r5 = r5 + 1
        L_0x02fb:
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x030a }
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x030a }
            r14.<init>(r13)     // Catch:{ Exception -> 0x030a }
            int r0 = r0 + r5
            r2.setSpan(r14, r5, r0, r12)     // Catch:{ Exception -> 0x030a }
        L_0x0308:
            r10 = r2
            goto L_0x028c
        L_0x030a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x028c
        L_0x0310:
            r5 = 0
        L_0x0311:
            r10 = 0
        L_0x0312:
            r13 = 0
        L_0x0313:
            if (r3 == 0) goto L_0x0327
            int r0 = r3.id
            int r2 = r1.selfUserId
            if (r0 != r2) goto L_0x0327
            r0 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r5 = 0
            r14 = 1
            goto L_0x032a
        L_0x0327:
            r0 = r5
            r5 = r10
            r14 = 0
        L_0x032a:
            if (r9 == 0) goto L_0x0371
            int r2 = r9.participants_count
            if (r2 == 0) goto L_0x0371
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r2 == 0) goto L_0x0343
            boolean r2 = r9.megagroup
            if (r2 != 0) goto L_0x0343
            int r2 = r9.participants_count
            java.lang.String r10 = "Subscribers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)
            goto L_0x034b
        L_0x0343:
            int r2 = r9.participants_count
            java.lang.String r10 = "Members"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2)
        L_0x034b:
            boolean r10 = r5 instanceof android.text.SpannableStringBuilder
            java.lang.String r12 = ", "
            if (r10 == 0) goto L_0x035c
            r4 = r5
            android.text.SpannableStringBuilder r4 = (android.text.SpannableStringBuilder) r4
            android.text.SpannableStringBuilder r4 = r4.append(r12)
            r4.append(r2)
            goto L_0x0371
        L_0x035c:
            boolean r10 = android.text.TextUtils.isEmpty(r5)
            if (r10 != 0) goto L_0x036f
            r10 = 3
            java.lang.CharSequence[] r10 = new java.lang.CharSequence[r10]
            r10[r6] = r5
            r10[r7] = r12
            r10[r4] = r2
            java.lang.CharSequence r2 = android.text.TextUtils.concat(r10)
        L_0x036f:
            r12 = r2
            goto L_0x0372
        L_0x0371:
            r12 = r5
        L_0x0372:
            if (r3 == 0) goto L_0x0375
            r9 = r3
        L_0x0375:
            r10 = r11
            r11 = r0
            r8.setData(r9, r10, r11, r12, r13, r14)
        L_0x037a:
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
            int i2 = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty() ? 2 : 0;
            if (i > i2) {
                return 0;
            }
            if (i == i2 || i % 2 == 0) {
                return 1;
            }
            return 5;
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
                return i6 == 0 ? 1 : 2;
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
}
