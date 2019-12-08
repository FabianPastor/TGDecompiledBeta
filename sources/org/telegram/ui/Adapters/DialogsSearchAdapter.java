package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsSearchAdapter extends SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private DialogsSearchAdapterDelegate delegate;
    private int dialogsType;
    private RecyclerListView innerListView;
    private String lastMessagesSearchString;
    private int lastReqId;
    private int lastSearchId = 0;
    private String lastSearchText;
    private Context mContext;
    private boolean messagesSearchEndReached;
    private int needMessagesSearch;
    private int nextSearchRate;
    private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList();
    private LongSparseArray<RecentSearchObject> recentSearchObjectsById = new LongSparseArray();
    private int reqId = 0;
    private SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
    private ArrayList<TLObject> searchResult = new ArrayList();
    private ArrayList<String> searchResultHashtags = new ArrayList();
    private ArrayList<MessageObject> searchResultMessages = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Runnable searchRunnable;
    private Runnable searchRunnable2;
    private boolean searchWas;
    private int selfUserId;

    private class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
        }

        /* synthetic */ DialogSearchResult(DialogsSearchAdapter dialogsSearchAdapter, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public interface DialogsSearchAdapterDelegate {
        void didPressedOnSubDialog(long j);

        void needClearList();

        void needRemoveHint(int i);

        void searchStateChanged(boolean z);
    }

    protected static class RecentSearchObject {
        int date;
        long did;
        TLObject object;

        protected RecentSearchObject() {
        }
    }

    private class CategoryAdapterRecycler extends SelectionAdapter {
        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        private CategoryAdapterRecycler() {
        }

        /* synthetic */ CategoryAdapterRecycler(DialogsSearchAdapter dialogsSearchAdapter, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void setIndex(int i) {
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            HintDialogCell hintDialogCell = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            hintDialogCell.setLayoutParams(new LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(86.0f)));
            return new Holder(hintDialogCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Chat chat;
            HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
            TL_topPeer tL_topPeer = (TL_topPeer) MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(i);
            TL_dialog tL_dialog = new TL_dialog();
            Peer peer = tL_topPeer.peer;
            int i2 = peer.user_id;
            User user = null;
            if (i2 != 0) {
                user = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(tL_topPeer.peer.user_id));
                chat = null;
            } else {
                i2 = peer.channel_id;
                if (i2 != 0) {
                    i2 = -i2;
                    chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tL_topPeer.peer.channel_id));
                } else {
                    int i3 = peer.chat_id;
                    if (i3 != 0) {
                        i2 = -i3;
                        chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tL_topPeer.peer.chat_id));
                    } else {
                        i2 = 0;
                        chat = null;
                    }
                }
            }
            hintDialogCell.setTag(Integer.valueOf(i2));
            CharSequence firstName = user != null ? UserObject.getFirstName(user) : chat != null ? chat.title : "";
            hintDialogCell.setDialog(i2, true, firstName);
        }

        public int getItemCount() {
            return MediaDataController.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public DialogsSearchAdapter(Context context, int i, int i2) {
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<User> getExcludeUsers() {
                return -CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged() {
                DialogsSearchAdapter.this.searchWas = true;
                if (!(DialogsSearchAdapter.this.searchAdapterHelper.isSearchInProgress() || DialogsSearchAdapter.this.delegate == null)) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                for (int i = 0; i < arrayList.size(); i++) {
                    DialogsSearchAdapter.this.searchResultHashtags.add(((HashtagObject) arrayList.get(i)).hashtag);
                }
                if (DialogsSearchAdapter.this.delegate != null) {
                    DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.this.notifyDataSetChanged();
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
        searchMessagesInternal(this.lastMessagesSearchString);
    }

    public String getLastSearchString() {
        return this.lastMessagesSearchString;
    }

    private void searchMessagesInternal(String str) {
        if (!(this.needMessagesSearch == 0 || (TextUtils.isEmpty(this.lastMessagesSearchString) && TextUtils.isEmpty(str)))) {
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
                }
                return;
            }
            TL_messages_searchGlobal tL_messages_searchGlobal = new TL_messages_searchGlobal();
            tL_messages_searchGlobal.limit = 20;
            tL_messages_searchGlobal.q = str;
            if (!str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                tL_messages_searchGlobal.offset_rate = 0;
                tL_messages_searchGlobal.offset_id = 0;
                tL_messages_searchGlobal.offset_peer = new TL_inputPeerEmpty();
            } else {
                int i;
                ArrayList arrayList = this.searchResultMessages;
                MessageObject messageObject = (MessageObject) arrayList.get(arrayList.size() - 1);
                tL_messages_searchGlobal.offset_id = messageObject.getId();
                tL_messages_searchGlobal.offset_rate = this.nextSearchRate;
                Peer peer = messageObject.messageOwner.to_id;
                int i2 = peer.channel_id;
                if (i2 == 0) {
                    i2 = peer.chat_id;
                    if (i2 == 0) {
                        i = peer.user_id;
                        tL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                    }
                }
                i = -i2;
                tL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
            }
            this.lastMessagesSearchString = str;
            int i3 = this.lastReqId + 1;
            this.lastReqId = i3;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_searchGlobal, new -$$Lambda$DialogsSearchAdapter$jvBeESgA8AL2rHOVVbFgj80mlPg(this, i3, tL_messages_searchGlobal), 2);
        }
    }

    public /* synthetic */ void lambda$searchMessagesInternal$1$DialogsSearchAdapter(int i, TL_messages_searchGlobal tL_messages_searchGlobal, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DialogsSearchAdapter$tzGJ1M1cHM4tSDBBFCCxa18ZhzA(this, i, tL_error, tLObject, tL_messages_searchGlobal));
    }

    public /* synthetic */ void lambda$null$0$DialogsSearchAdapter(int i, TL_error tL_error, TLObject tLObject, TL_messages_searchGlobal tL_messages_searchGlobal) {
        if (i == this.lastReqId && tL_error == null) {
            messages_Messages messages_messages = (messages_Messages) tLObject;
            boolean z = true;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
            if (tL_messages_searchGlobal.offset_id == 0) {
                this.searchResultMessages.clear();
            }
            this.nextSearchRate = messages_messages.next_rate;
            for (i = 0; i < messages_messages.messages.size(); i++) {
                Message message = (Message) messages_messages.messages.get(i);
                this.searchResultMessages.add(new MessageObject(this.currentAccount, message, false));
                long dialogId = MessageObject.getDialogId(message);
                ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(this.currentAccount).dialogs_read_inbox_max;
                Integer num = (Integer) concurrentHashMap.get(Long.valueOf(dialogId));
                if (num == null) {
                    num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, dialogId));
                    concurrentHashMap.put(Long.valueOf(dialogId), num);
                }
                message.unread = num.intValue() < message.id;
            }
            this.searchWas = true;
            if (messages_messages.messages.size() == 20) {
                z = false;
            }
            this.messagesSearchEndReached = z;
            notifyDataSetChanged();
        }
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.searchStateChanged(false);
        }
        this.reqId = 0;
    }

    public boolean hasRecentRearch() {
        int i = this.dialogsType;
        return (i == 4 || i == 5 || i == 6 || (this.recentSearchObjects.isEmpty() && MediaDataController.getInstance(this.currentAccount).hints.isEmpty())) ? false : true;
    }

    public boolean isRecentSearchDisplayed() {
        if (!(this.needMessagesSearch == 2 || this.searchWas || (this.recentSearchObjects.isEmpty() && MediaDataController.getInstance(this.currentAccount).hints.isEmpty()))) {
            int i = this.dialogsType;
            if (!(i == 4 || i == 5 || i == 6)) {
                return true;
            }
        }
        return false;
    }

    public void loadRecentSearch() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DialogsSearchAdapter$EdS8aWM1r9L4_WkQYwpXcCyFRfM(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x0031 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a7 A:{Catch:{ Exception -> 0x0191 }} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a7 A:{Catch:{ Exception -> 0x0191 }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0031 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0031 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a7 A:{Catch:{ Exception -> 0x0191 }} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a7 A:{Catch:{ Exception -> 0x0191 }} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0031 A:{SYNTHETIC} */
    public /* synthetic */ void lambda$loadRecentSearch$4$DialogsSearchAdapter() {
        /*
        r13 = this;
        r0 = r13.currentAccount;	 Catch:{ Exception -> 0x0191 }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x0191 }
        r0 = r0.getDatabase();	 Catch:{ Exception -> 0x0191 }
        r1 = "SELECT did, date FROM search_recent WHERE 1";
        r2 = 0;
        r3 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0191 }
        r0 = r0.queryFinalized(r1, r3);	 Catch:{ Exception -> 0x0191 }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r1.<init>();	 Catch:{ Exception -> 0x0191 }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r3.<init>();	 Catch:{ Exception -> 0x0191 }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r4.<init>();	 Catch:{ Exception -> 0x0191 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r5.<init>();	 Catch:{ Exception -> 0x0191 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r5.<init>();	 Catch:{ Exception -> 0x0191 }
        r6 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x0191 }
        r6.<init>();	 Catch:{ Exception -> 0x0191 }
    L_0x0031:
        r7 = r0.next();	 Catch:{ Exception -> 0x0191 }
        r8 = 32;
        if (r7 == 0) goto L_0x00be;
    L_0x0039:
        r9 = r0.longValue(r2);	 Catch:{ Exception -> 0x0191 }
        r7 = (int) r9;	 Catch:{ Exception -> 0x0191 }
        r11 = r9 >> r8;
        r8 = (int) r11;	 Catch:{ Exception -> 0x0191 }
        r11 = 1;
        if (r7 == 0) goto L_0x0088;
    L_0x0044:
        if (r8 != r11) goto L_0x005c;
    L_0x0046:
        r8 = r13.dialogsType;	 Catch:{ Exception -> 0x0191 }
        if (r8 != 0) goto L_0x00a4;
    L_0x004a:
        r8 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r8 = r3.contains(r8);	 Catch:{ Exception -> 0x0191 }
        if (r8 != 0) goto L_0x00a4;
    L_0x0054:
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r3.add(r7);	 Catch:{ Exception -> 0x0191 }
        goto L_0x00a2;
    L_0x005c:
        if (r7 <= 0) goto L_0x0075;
    L_0x005e:
        r8 = r13.dialogsType;	 Catch:{ Exception -> 0x0191 }
        r12 = 2;
        if (r8 == r12) goto L_0x00a4;
    L_0x0063:
        r8 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r8 = r1.contains(r8);	 Catch:{ Exception -> 0x0191 }
        if (r8 != 0) goto L_0x00a4;
    L_0x006d:
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r1.add(r7);	 Catch:{ Exception -> 0x0191 }
        goto L_0x00a2;
    L_0x0075:
        r7 = -r7;
        r8 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r8 = r3.contains(r8);	 Catch:{ Exception -> 0x0191 }
        if (r8 != 0) goto L_0x00a4;
    L_0x0080:
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0191 }
        r3.add(r7);	 Catch:{ Exception -> 0x0191 }
        goto L_0x00a2;
    L_0x0088:
        r7 = r13.dialogsType;	 Catch:{ Exception -> 0x0191 }
        if (r7 == 0) goto L_0x0091;
    L_0x008c:
        r7 = r13.dialogsType;	 Catch:{ Exception -> 0x0191 }
        r12 = 3;
        if (r7 != r12) goto L_0x00a4;
    L_0x0091:
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0191 }
        r7 = r4.contains(r7);	 Catch:{ Exception -> 0x0191 }
        if (r7 != 0) goto L_0x00a4;
    L_0x009b:
        r7 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0191 }
        r4.add(r7);	 Catch:{ Exception -> 0x0191 }
    L_0x00a2:
        r7 = 1;
        goto L_0x00a5;
    L_0x00a4:
        r7 = 0;
    L_0x00a5:
        if (r7 == 0) goto L_0x0031;
    L_0x00a7:
        r7 = new org.telegram.ui.Adapters.DialogsSearchAdapter$RecentSearchObject;	 Catch:{ Exception -> 0x0191 }
        r7.<init>();	 Catch:{ Exception -> 0x0191 }
        r7.did = r9;	 Catch:{ Exception -> 0x0191 }
        r8 = r0.intValue(r11);	 Catch:{ Exception -> 0x0191 }
        r7.date = r8;	 Catch:{ Exception -> 0x0191 }
        r5.add(r7);	 Catch:{ Exception -> 0x0191 }
        r8 = r7.did;	 Catch:{ Exception -> 0x0191 }
        r6.put(r8, r7);	 Catch:{ Exception -> 0x0191 }
        goto L_0x0031;
    L_0x00be:
        r0.dispose();	 Catch:{ Exception -> 0x0191 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r0.<init>();	 Catch:{ Exception -> 0x0191 }
        r7 = r4.isEmpty();	 Catch:{ Exception -> 0x0191 }
        r9 = ",";
        if (r7 != 0) goto L_0x0102;
    L_0x00ce:
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r7.<init>();	 Catch:{ Exception -> 0x0191 }
        r10 = r13.currentAccount;	 Catch:{ Exception -> 0x0191 }
        r10 = org.telegram.messenger.MessagesStorage.getInstance(r10);	 Catch:{ Exception -> 0x0191 }
        r4 = android.text.TextUtils.join(r9, r4);	 Catch:{ Exception -> 0x0191 }
        r10.getEncryptedChatsInternal(r4, r7, r1);	 Catch:{ Exception -> 0x0191 }
        r4 = 0;
    L_0x00e1:
        r10 = r7.size();	 Catch:{ Exception -> 0x0191 }
        if (r4 >= r10) goto L_0x0102;
    L_0x00e7:
        r10 = r7.get(r4);	 Catch:{ Exception -> 0x0191 }
        r10 = (org.telegram.tgnet.TLRPC.EncryptedChat) r10;	 Catch:{ Exception -> 0x0191 }
        r10 = r10.id;	 Catch:{ Exception -> 0x0191 }
        r10 = (long) r10;	 Catch:{ Exception -> 0x0191 }
        r10 = r10 << r8;
        r10 = r6.get(r10);	 Catch:{ Exception -> 0x0191 }
        r10 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r10;	 Catch:{ Exception -> 0x0191 }
        r11 = r7.get(r4);	 Catch:{ Exception -> 0x0191 }
        r11 = (org.telegram.tgnet.TLObject) r11;	 Catch:{ Exception -> 0x0191 }
        r10.object = r11;	 Catch:{ Exception -> 0x0191 }
        r4 = r4 + 1;
        goto L_0x00e1;
    L_0x0102:
        r4 = r3.isEmpty();	 Catch:{ Exception -> 0x0191 }
        if (r4 != 0) goto L_0x0154;
    L_0x0108:
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0191 }
        r4.<init>();	 Catch:{ Exception -> 0x0191 }
        r7 = r13.currentAccount;	 Catch:{ Exception -> 0x0191 }
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r7);	 Catch:{ Exception -> 0x0191 }
        r3 = android.text.TextUtils.join(r9, r3);	 Catch:{ Exception -> 0x0191 }
        r7.getChatsInternal(r3, r4);	 Catch:{ Exception -> 0x0191 }
        r3 = 0;
    L_0x011b:
        r7 = r4.size();	 Catch:{ Exception -> 0x0191 }
        if (r3 >= r7) goto L_0x0154;
    L_0x0121:
        r7 = r4.get(r3);	 Catch:{ Exception -> 0x0191 }
        r7 = (org.telegram.tgnet.TLRPC.Chat) r7;	 Catch:{ Exception -> 0x0191 }
        r8 = r7.id;	 Catch:{ Exception -> 0x0191 }
        if (r8 <= 0) goto L_0x0130;
    L_0x012b:
        r8 = r7.id;	 Catch:{ Exception -> 0x0191 }
        r8 = -r8;
        r10 = (long) r8;	 Catch:{ Exception -> 0x0191 }
        goto L_0x0136;
    L_0x0130:
        r8 = r7.id;	 Catch:{ Exception -> 0x0191 }
        r10 = org.telegram.messenger.AndroidUtilities.makeBroadcastId(r8);	 Catch:{ Exception -> 0x0191 }
    L_0x0136:
        r8 = r7.migrated_to;	 Catch:{ Exception -> 0x0191 }
        if (r8 == 0) goto L_0x0149;
    L_0x013a:
        r7 = r6.get(r10);	 Catch:{ Exception -> 0x0191 }
        r7 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r7;	 Catch:{ Exception -> 0x0191 }
        r6.remove(r10);	 Catch:{ Exception -> 0x0191 }
        if (r7 == 0) goto L_0x0151;
    L_0x0145:
        r5.remove(r7);	 Catch:{ Exception -> 0x0191 }
        goto L_0x0151;
    L_0x0149:
        r8 = r6.get(r10);	 Catch:{ Exception -> 0x0191 }
        r8 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r8;	 Catch:{ Exception -> 0x0191 }
        r8.object = r7;	 Catch:{ Exception -> 0x0191 }
    L_0x0151:
        r3 = r3 + 1;
        goto L_0x011b;
    L_0x0154:
        r3 = r1.isEmpty();	 Catch:{ Exception -> 0x0191 }
        if (r3 != 0) goto L_0x0183;
    L_0x015a:
        r3 = r13.currentAccount;	 Catch:{ Exception -> 0x0191 }
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);	 Catch:{ Exception -> 0x0191 }
        r1 = android.text.TextUtils.join(r9, r1);	 Catch:{ Exception -> 0x0191 }
        r3.getUsersInternal(r1, r0);	 Catch:{ Exception -> 0x0191 }
    L_0x0167:
        r1 = r0.size();	 Catch:{ Exception -> 0x0191 }
        if (r2 >= r1) goto L_0x0183;
    L_0x016d:
        r1 = r0.get(r2);	 Catch:{ Exception -> 0x0191 }
        r1 = (org.telegram.tgnet.TLRPC.User) r1;	 Catch:{ Exception -> 0x0191 }
        r3 = r1.id;	 Catch:{ Exception -> 0x0191 }
        r3 = (long) r3;	 Catch:{ Exception -> 0x0191 }
        r3 = r6.get(r3);	 Catch:{ Exception -> 0x0191 }
        r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r3;	 Catch:{ Exception -> 0x0191 }
        if (r3 == 0) goto L_0x0180;
    L_0x017e:
        r3.object = r1;	 Catch:{ Exception -> 0x0191 }
    L_0x0180:
        r2 = r2 + 1;
        goto L_0x0167;
    L_0x0183:
        r0 = org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$TgrSEhniISqCg6ct5i9NTHhT7C8.INSTANCE;	 Catch:{ Exception -> 0x0191 }
        java.util.Collections.sort(r5, r0);	 Catch:{ Exception -> 0x0191 }
        r0 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$YAAaRoGgRkDmshNt90P0fNwfz-U;	 Catch:{ Exception -> 0x0191 }
        r0.<init>(r13, r5, r6);	 Catch:{ Exception -> 0x0191 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0191 }
        goto L_0x0195;
    L_0x0191:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0195:
        return;
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

    public /* synthetic */ void lambda$null$3$DialogsSearchAdapter(ArrayList arrayList, LongSparseArray longSparseArray) {
        setRecentSearch(arrayList, longSparseArray);
    }

    public void putRecentSearch(long j, TLObject tLObject) {
        Object obj = (RecentSearchObject) this.recentSearchObjectsById.get(j);
        if (obj == null) {
            obj = new RecentSearchObject();
            this.recentSearchObjectsById.put(j, obj);
        } else {
            this.recentSearchObjects.remove(obj);
        }
        this.recentSearchObjects.add(0, obj);
        obj.did = j;
        obj.object = tLObject;
        obj.date = (int) (System.currentTimeMillis() / 1000);
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DialogsSearchAdapter$EHNVdrp_nz-CUR77EsM44jqsFBg(this, j));
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
            FileLog.e(e);
        }
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new LongSparseArray();
        this.recentSearchObjects = new ArrayList();
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DialogsSearchAdapter$It_IWmufaVpNeuW4BM4G8iqCxcU(this));
    }

    public /* synthetic */ void lambda$clearRecentSearch$6$DialogsSearchAdapter() {
        try {
            MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    private void setRecentSearch(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = longSparseArray;
        for (int i = 0; i < this.recentSearchObjects.size(); i++) {
            RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjects.get(i);
            TLObject tLObject = recentSearchObject.object;
            if (tLObject instanceof User) {
                MessagesController.getInstance(this.currentAccount).putUser((User) recentSearchObject.object, true);
            } else if (tLObject instanceof Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((Chat) recentSearchObject.object, true);
            } else if (tLObject instanceof EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(String str, int i) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$DialogsSearchAdapter$jlz_Jg93hVH6t3KHP9A-P2d1MzU(this, str, i));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x0267 A:{LOOP_END, LOOP:2: B:75:0x01a9->B:105:0x0267, Catch:{ Exception -> 0x06a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x01fc A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0520 A:{LOOP_END, LOOP:6: B:160:0x03ac->B:199:0x0520, Catch:{ Exception -> 0x06a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x03f7 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0680 A:{LOOP_END, LOOP:10: B:233:0x05d3->B:262:0x0680, Catch:{ Exception -> 0x06a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0624 A:{SYNTHETIC} */
    public /* synthetic */ void lambda$searchDialogsInternal$8$DialogsSearchAdapter(java.lang.String r25, int r26) {
        /*
        r24 = this;
        r1 = r24;
        r0 = "SavedMessages";
        r2 = NUM; // 0x7f0d0904 float:1.8746796E38 double:1.053130918E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x06a4 }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x06a4 }
        r2 = r25.trim();	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x06a4 }
        r3 = r2.length();	 Catch:{ Exception -> 0x06a4 }
        r4 = -1;
        if (r3 != 0) goto L_0x0035;
    L_0x001e:
        r1.lastSearchId = r4;	 Catch:{ Exception -> 0x06a4 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r0.<init>();	 Catch:{ Exception -> 0x06a4 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r2.<init>();	 Catch:{ Exception -> 0x06a4 }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r3.<init>();	 Catch:{ Exception -> 0x06a4 }
        r4 = r1.lastSearchId;	 Catch:{ Exception -> 0x06a4 }
        r1.updateSearchResults(r0, r2, r3, r4);	 Catch:{ Exception -> 0x06a4 }
        return;
    L_0x0035:
        r3 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x06a4 }
        r3 = r3.getTranslitString(r2);	 Catch:{ Exception -> 0x06a4 }
        r5 = r2.equals(r3);	 Catch:{ Exception -> 0x06a4 }
        r6 = 0;
        if (r5 != 0) goto L_0x004a;
    L_0x0044:
        r5 = r3.length();	 Catch:{ Exception -> 0x06a4 }
        if (r5 != 0) goto L_0x004b;
    L_0x004a:
        r3 = r6;
    L_0x004b:
        r5 = 1;
        r7 = 0;
        if (r3 == 0) goto L_0x0051;
    L_0x004f:
        r8 = 1;
        goto L_0x0052;
    L_0x0051:
        r8 = 0;
    L_0x0052:
        r8 = r8 + r5;
        r8 = new java.lang.String[r8];	 Catch:{ Exception -> 0x06a4 }
        r8[r7] = r2;	 Catch:{ Exception -> 0x06a4 }
        if (r3 == 0) goto L_0x005b;
    L_0x0059:
        r8[r5] = r3;	 Catch:{ Exception -> 0x06a4 }
    L_0x005b:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r3.<init>();	 Catch:{ Exception -> 0x06a4 }
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r9.<init>();	 Catch:{ Exception -> 0x06a4 }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r10.<init>();	 Catch:{ Exception -> 0x06a4 }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r11.<init>();	 Catch:{ Exception -> 0x06a4 }
        r12 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x06a4 }
        r12.<init>();	 Catch:{ Exception -> 0x06a4 }
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r13 = org.telegram.messenger.MessagesStorage.getInstance(r13);	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.getDatabase();	 Catch:{ Exception -> 0x06a4 }
        r14 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600";
        r15 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.queryFinalized(r14, r15);	 Catch:{ Exception -> 0x06a4 }
    L_0x0086:
        r14 = r13.next();	 Catch:{ Exception -> 0x06a4 }
        r15 = 32;
        if (r14 == 0) goto L_0x011f;
    L_0x008e:
        r4 = r13.longValue(r7);	 Catch:{ Exception -> 0x06a4 }
        r14 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x06a4 }
        r14.<init>(r1, r6);	 Catch:{ Exception -> 0x06a4 }
        r7 = 1;
        r6 = r13.intValue(r7);	 Catch:{ Exception -> 0x06a4 }
        r14.date = r6;	 Catch:{ Exception -> 0x06a4 }
        r12.put(r4, r14);	 Catch:{ Exception -> 0x06a4 }
        r6 = (int) r4;	 Catch:{ Exception -> 0x06a4 }
        r4 = r4 >> r15;
        r5 = (int) r4;	 Catch:{ Exception -> 0x06a4 }
        if (r6 == 0) goto L_0x0104;
    L_0x00a6:
        if (r5 != r7) goto L_0x00c8;
    L_0x00a8:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r5 = 4;
        if (r4 != r5) goto L_0x00b2;
    L_0x00ad:
        r4 = -1;
        r5 = 1;
        r6 = 0;
        r7 = 0;
        goto L_0x0086;
    L_0x00b2:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x00ad;
    L_0x00b6:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x06a4 }
        r4 = r9.contains(r4);	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x00ad;
    L_0x00c0:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x06a4 }
        r9.add(r4);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x00ad;
    L_0x00c8:
        if (r6 <= 0) goto L_0x00eb;
    L_0x00ca:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r5 = 4;
        if (r4 != r5) goto L_0x00d4;
    L_0x00cf:
        r4 = r1.selfUserId;	 Catch:{ Exception -> 0x06a4 }
        if (r6 != r4) goto L_0x00d4;
    L_0x00d3:
        goto L_0x00ad;
    L_0x00d4:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r5 = 2;
        if (r4 == r5) goto L_0x00ad;
    L_0x00d9:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x06a4 }
        r4 = r3.contains(r4);	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x00ad;
    L_0x00e3:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x06a4 }
        r3.add(r4);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x00ad;
    L_0x00eb:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r5 = 4;
        if (r4 != r5) goto L_0x00f1;
    L_0x00f0:
        goto L_0x00ad;
    L_0x00f1:
        r4 = -r6;
        r5 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x06a4 }
        r5 = r9.contains(r5);	 Catch:{ Exception -> 0x06a4 }
        if (r5 != 0) goto L_0x00ad;
    L_0x00fc:
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x06a4 }
        r9.add(r4);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x00ad;
    L_0x0104:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        if (r4 == 0) goto L_0x010d;
    L_0x0108:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r6 = 3;
        if (r4 != r6) goto L_0x00ad;
    L_0x010d:
        r4 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x06a4 }
        r4 = r10.contains(r4);	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x00ad;
    L_0x0117:
        r4 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x06a4 }
        r10.add(r4);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x00ad;
    L_0x011f:
        r13.dispose();	 Catch:{ Exception -> 0x06a4 }
        r2 = r0.startsWith(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x0149;
    L_0x0128:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getCurrentUser();	 Catch:{ Exception -> 0x06a4 }
        r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x06a4 }
        r5 = 0;
        r4.<init>(r1, r5);	 Catch:{ Exception -> 0x06a4 }
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.date = r5;	 Catch:{ Exception -> 0x06a4 }
        r4.name = r0;	 Catch:{ Exception -> 0x06a4 }
        r4.object = r2;	 Catch:{ Exception -> 0x06a4 }
        r0 = r2.id;	 Catch:{ Exception -> 0x06a4 }
        r5 = (long) r0;	 Catch:{ Exception -> 0x06a4 }
        r12.put(r5, r4);	 Catch:{ Exception -> 0x06a4 }
        r0 = 1;
        goto L_0x014a;
    L_0x0149:
        r0 = 0;
    L_0x014a:
        r2 = r3.isEmpty();	 Catch:{ Exception -> 0x06a4 }
        r4 = ";;;";
        r5 = ",";
        r6 = "@";
        r7 = " ";
        if (r2 != 0) goto L_0x0286;
    L_0x0158:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getDatabase();	 Catch:{ Exception -> 0x06a4 }
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x06a4 }
        r14 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
        r18 = r0;
        r15 = 1;
        r0 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x06a4 }
        r3 = android.text.TextUtils.join(r5, r3);	 Catch:{ Exception -> 0x06a4 }
        r15 = 0;
        r0[r15] = r3;	 Catch:{ Exception -> 0x06a4 }
        r0 = java.lang.String.format(r13, r14, r0);	 Catch:{ Exception -> 0x06a4 }
        r3 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x06a4 }
        r0 = r2.queryFinalized(r0, r3);	 Catch:{ Exception -> 0x06a4 }
    L_0x017c:
        r2 = r0.next();	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x0280;
    L_0x0182:
        r2 = 2;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x06a4 }
        r13 = r3.equals(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r13 == 0) goto L_0x0196;
    L_0x0195:
        r2 = 0;
    L_0x0196:
        r13 = r3.lastIndexOf(r4);	 Catch:{ Exception -> 0x06a4 }
        r14 = -1;
        if (r13 == r14) goto L_0x01a4;
    L_0x019d:
        r13 = r13 + 3;
        r13 = r3.substring(r13);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x01a5;
    L_0x01a4:
        r13 = 0;
    L_0x01a5:
        r15 = r8.length;	 Catch:{ Exception -> 0x06a4 }
        r14 = 0;
        r20 = 0;
    L_0x01a9:
        if (r14 >= r15) goto L_0x027a;
    L_0x01ab:
        r21 = r15;
        r15 = r8[r14];	 Catch:{ Exception -> 0x06a4 }
        r22 = r3.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r22 != 0) goto L_0x01f7;
    L_0x01b5:
        r22 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r14.<init>();	 Catch:{ Exception -> 0x06a4 }
        r14.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r14.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x06a4 }
        r14 = r3.contains(r14);	 Catch:{ Exception -> 0x06a4 }
        if (r14 != 0) goto L_0x01f9;
    L_0x01cc:
        if (r2 == 0) goto L_0x01ea;
    L_0x01ce:
        r14 = r2.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r14 != 0) goto L_0x01f9;
    L_0x01d4:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r14.<init>();	 Catch:{ Exception -> 0x06a4 }
        r14.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r14.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x06a4 }
        r14 = r2.contains(r14);	 Catch:{ Exception -> 0x06a4 }
        if (r14 == 0) goto L_0x01ea;
    L_0x01e9:
        goto L_0x01f9;
    L_0x01ea:
        if (r13 == 0) goto L_0x01f4;
    L_0x01ec:
        r14 = r13.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r14 == 0) goto L_0x01f4;
    L_0x01f2:
        r14 = 2;
        goto L_0x01fa;
    L_0x01f4:
        r14 = r20;
        goto L_0x01fa;
    L_0x01f7:
        r22 = r14;
    L_0x01f9:
        r14 = 1;
    L_0x01fa:
        if (r14 == 0) goto L_0x0267;
    L_0x01fc:
        r2 = 0;
        r3 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r3 == 0) goto L_0x027a;
    L_0x0203:
        r13 = r3.readInt32(r2);	 Catch:{ Exception -> 0x06a4 }
        r13 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r3, r13, r2);	 Catch:{ Exception -> 0x06a4 }
        r3.reuse();	 Catch:{ Exception -> 0x06a4 }
        r2 = r13.id;	 Catch:{ Exception -> 0x06a4 }
        r2 = (long) r2;	 Catch:{ Exception -> 0x06a4 }
        r2 = r12.get(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x06a4 }
        r3 = r13.status;	 Catch:{ Exception -> 0x06a4 }
        if (r3 == 0) goto L_0x022b;
    L_0x021b:
        r3 = r13.status;	 Catch:{ Exception -> 0x06a4 }
        r19 = r11;
        r22 = r14;
        r14 = 1;
        r11 = r0.intValue(r14);	 Catch:{ Exception -> 0x06a4 }
        r14 = r22;
        r3.expires = r11;	 Catch:{ Exception -> 0x06a4 }
        goto L_0x022d;
    L_0x022b:
        r19 = r11;
    L_0x022d:
        r3 = 1;
        if (r14 != r3) goto L_0x023b;
    L_0x0230:
        r3 = r13.first_name;	 Catch:{ Exception -> 0x06a4 }
        r14 = r13.last_name;	 Catch:{ Exception -> 0x06a4 }
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r14, r15);	 Catch:{ Exception -> 0x06a4 }
        r2.name = r3;	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0262;
    L_0x023b:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r3.<init>();	 Catch:{ Exception -> 0x06a4 }
        r3.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r14 = r13.username;	 Catch:{ Exception -> 0x06a4 }
        r3.append(r14);	 Catch:{ Exception -> 0x06a4 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x06a4 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r14.<init>();	 Catch:{ Exception -> 0x06a4 }
        r14.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r14.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x06a4 }
        r15 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r15, r14);	 Catch:{ Exception -> 0x06a4 }
        r2.name = r3;	 Catch:{ Exception -> 0x06a4 }
    L_0x0262:
        r2.object = r13;	 Catch:{ Exception -> 0x06a4 }
        r18 = r18 + 1;
        goto L_0x027c;
    L_0x0267:
        r20 = r2;
        r19 = r11;
        r2 = r22;
        r2 = r2 + 1;
        r15 = r21;
        r23 = r14;
        r14 = r2;
        r2 = r20;
        r20 = r23;
        goto L_0x01a9;
    L_0x027a:
        r19 = r11;
    L_0x027c:
        r11 = r19;
        goto L_0x017c;
    L_0x0280:
        r19 = r11;
        r0.dispose();	 Catch:{ Exception -> 0x06a4 }
        goto L_0x028a;
    L_0x0286:
        r18 = r0;
        r19 = r11;
    L_0x028a:
        r0 = r9.isEmpty();	 Catch:{ Exception -> 0x06a4 }
        if (r0 != 0) goto L_0x0359;
    L_0x0290:
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x06a4 }
        r0 = r0.getDatabase();	 Catch:{ Exception -> 0x06a4 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x06a4 }
        r3 = "SELECT data, name FROM chats WHERE uid IN(%s)";
        r11 = 1;
        r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x06a4 }
        r9 = android.text.TextUtils.join(r5, r9);	 Catch:{ Exception -> 0x06a4 }
        r11 = 0;
        r13[r11] = r9;	 Catch:{ Exception -> 0x06a4 }
        r2 = java.lang.String.format(r2, r3, r13);	 Catch:{ Exception -> 0x06a4 }
        r3 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x06a4 }
        r0 = r0.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x06a4 }
    L_0x02b2:
        r2 = r0.next();	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x0356;
    L_0x02b8:
        r2 = 1;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x06a4 }
        r9 = r3.equals(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r9 == 0) goto L_0x02cc;
    L_0x02cb:
        r2 = 0;
    L_0x02cc:
        r9 = r8.length;	 Catch:{ Exception -> 0x06a4 }
        r11 = 0;
    L_0x02ce:
        if (r11 >= r9) goto L_0x02b2;
    L_0x02d0:
        r13 = r8[r11];	 Catch:{ Exception -> 0x06a4 }
        r15 = r3.startsWith(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r15 != 0) goto L_0x030e;
    L_0x02d8:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r15.<init>();	 Catch:{ Exception -> 0x06a4 }
        r15.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r15.append(r13);	 Catch:{ Exception -> 0x06a4 }
        r15 = r15.toString();	 Catch:{ Exception -> 0x06a4 }
        r15 = r3.contains(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r15 != 0) goto L_0x030e;
    L_0x02ed:
        if (r2 == 0) goto L_0x030b;
    L_0x02ef:
        r15 = r2.startsWith(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r15 != 0) goto L_0x030e;
    L_0x02f5:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r15.<init>();	 Catch:{ Exception -> 0x06a4 }
        r15.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r15.append(r13);	 Catch:{ Exception -> 0x06a4 }
        r15 = r15.toString();	 Catch:{ Exception -> 0x06a4 }
        r15 = r2.contains(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r15 == 0) goto L_0x030b;
    L_0x030a:
        goto L_0x030e;
    L_0x030b:
        r11 = r11 + 1;
        goto L_0x02ce;
    L_0x030e:
        r2 = 0;
        r3 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r3 == 0) goto L_0x02b2;
    L_0x0315:
        r9 = r3.readInt32(r2);	 Catch:{ Exception -> 0x06a4 }
        r9 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r3, r9, r2);	 Catch:{ Exception -> 0x06a4 }
        r3.reuse();	 Catch:{ Exception -> 0x06a4 }
        if (r9 == 0) goto L_0x02b2;
    L_0x0322:
        r2 = r9.deactivated;	 Catch:{ Exception -> 0x06a4 }
        if (r2 != 0) goto L_0x02b2;
    L_0x0326:
        r2 = org.telegram.messenger.ChatObject.isChannel(r9);	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x0332;
    L_0x032c:
        r2 = org.telegram.messenger.ChatObject.isNotInChat(r9);	 Catch:{ Exception -> 0x06a4 }
        if (r2 != 0) goto L_0x02b2;
    L_0x0332:
        r2 = r9.id;	 Catch:{ Exception -> 0x06a4 }
        if (r2 <= 0) goto L_0x033b;
    L_0x0336:
        r2 = r9.id;	 Catch:{ Exception -> 0x06a4 }
        r2 = -r2;
        r2 = (long) r2;	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0341;
    L_0x033b:
        r2 = r9.id;	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.AndroidUtilities.makeBroadcastId(r2);	 Catch:{ Exception -> 0x06a4 }
    L_0x0341:
        r2 = r12.get(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x06a4 }
        r3 = r9.title;	 Catch:{ Exception -> 0x06a4 }
        r11 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r11, r13);	 Catch:{ Exception -> 0x06a4 }
        r2.name = r3;	 Catch:{ Exception -> 0x06a4 }
        r2.object = r9;	 Catch:{ Exception -> 0x06a4 }
        r18 = r18 + 1;
        goto L_0x02b2;
    L_0x0356:
        r0.dispose();	 Catch:{ Exception -> 0x06a4 }
    L_0x0359:
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x06a4 }
        if (r0 != 0) goto L_0x0534;
    L_0x035f:
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x06a4 }
        r0 = r0.getDatabase();	 Catch:{ Exception -> 0x06a4 }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x06a4 }
        r3 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)";
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x06a4 }
        r5 = android.text.TextUtils.join(r5, r10);	 Catch:{ Exception -> 0x06a4 }
        r9 = 0;
        r11[r9] = r5;	 Catch:{ Exception -> 0x06a4 }
        r2 = java.lang.String.format(r2, r3, r11);	 Catch:{ Exception -> 0x06a4 }
        r3 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x06a4 }
        r0 = r0.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x06a4 }
    L_0x0381:
        r2 = r0.next();	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x052e;
    L_0x0387:
        r2 = 1;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x06a4 }
        r5 = r3.equals(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x039b;
    L_0x039a:
        r2 = 0;
    L_0x039b:
        r5 = r3.lastIndexOf(r4);	 Catch:{ Exception -> 0x06a4 }
        r9 = -1;
        if (r5 == r9) goto L_0x03a9;
    L_0x03a2:
        r5 = r5 + 2;
        r5 = r3.substring(r5);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x03aa;
    L_0x03a9:
        r5 = 0;
    L_0x03aa:
        r9 = 0;
        r10 = 0;
    L_0x03ac:
        r11 = r8.length;	 Catch:{ Exception -> 0x06a4 }
        if (r9 >= r11) goto L_0x0527;
    L_0x03af:
        r11 = r8[r9];	 Catch:{ Exception -> 0x06a4 }
        r13 = r3.startsWith(r11);	 Catch:{ Exception -> 0x06a4 }
        if (r13 != 0) goto L_0x03f4;
    L_0x03b7:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r13.<init>();	 Catch:{ Exception -> 0x06a4 }
        r13.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r13.append(r11);	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x06a4 }
        r13 = r3.contains(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r13 != 0) goto L_0x03f4;
    L_0x03cc:
        if (r2 == 0) goto L_0x03ea;
    L_0x03ce:
        r13 = r2.startsWith(r11);	 Catch:{ Exception -> 0x06a4 }
        if (r13 != 0) goto L_0x03f4;
    L_0x03d4:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r13.<init>();	 Catch:{ Exception -> 0x06a4 }
        r13.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r13.append(r11);	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x06a4 }
        r13 = r2.contains(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r13 == 0) goto L_0x03ea;
    L_0x03e9:
        goto L_0x03f4;
    L_0x03ea:
        if (r5 == 0) goto L_0x03f5;
    L_0x03ec:
        r13 = r5.startsWith(r11);	 Catch:{ Exception -> 0x06a4 }
        if (r13 == 0) goto L_0x03f5;
    L_0x03f2:
        r10 = 2;
        goto L_0x03f5;
    L_0x03f4:
        r10 = 1;
    L_0x03f5:
        if (r10 == 0) goto L_0x0520;
    L_0x03f7:
        r13 = 0;
        r2 = r0.byteBufferValue(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x040a;
    L_0x03fe:
        r3 = r2.readInt32(r13);	 Catch:{ Exception -> 0x06a4 }
        r3 = org.telegram.tgnet.TLRPC.EncryptedChat.TLdeserialize(r2, r3, r13);	 Catch:{ Exception -> 0x06a4 }
        r2.reuse();	 Catch:{ Exception -> 0x06a4 }
        goto L_0x040b;
    L_0x040a:
        r3 = 0;
    L_0x040b:
        r2 = 6;
        r2 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x06a4 }
        if (r2 == 0) goto L_0x041f;
    L_0x0412:
        r5 = 0;
        r9 = r2.readInt32(r5);	 Catch:{ Exception -> 0x06a4 }
        r9 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r2, r9, r5);	 Catch:{ Exception -> 0x06a4 }
        r2.reuse();	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0420;
    L_0x041f:
        r9 = 0;
    L_0x0420:
        if (r3 == 0) goto L_0x0527;
    L_0x0422:
        if (r9 == 0) goto L_0x0527;
    L_0x0424:
        r2 = r3.id;	 Catch:{ Exception -> 0x06a4 }
        r14 = (long) r2;	 Catch:{ Exception -> 0x06a4 }
        r13 = 32;
        r14 = r14 << r13;
        r2 = r12.get(r14);	 Catch:{ Exception -> 0x06a4 }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x06a4 }
        r5 = 2;
        r14 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.user_id = r14;	 Catch:{ Exception -> 0x06a4 }
        r5 = 3;
        r14 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.a_or_b = r14;	 Catch:{ Exception -> 0x06a4 }
        r15 = 4;
        r5 = r0.byteArrayValue(r15);	 Catch:{ Exception -> 0x06a4 }
        r3.auth_key = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 5;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.ttl = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 8;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.layer = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 9;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.seq_in = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 10;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.seq_out = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 11;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r14 = r5 >> 16;
        r14 = (short) r14;	 Catch:{ Exception -> 0x06a4 }
        r3.key_use_count_in = r14;	 Catch:{ Exception -> 0x06a4 }
        r5 = (short) r5;	 Catch:{ Exception -> 0x06a4 }
        r3.key_use_count_out = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 12;
        r13 = r0.longValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.exchange_id = r13;	 Catch:{ Exception -> 0x06a4 }
        r5 = 13;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.key_create_date = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 14;
        r13 = r0.longValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.future_key_fingerprint = r13;	 Catch:{ Exception -> 0x06a4 }
        r5 = 15;
        r5 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.future_auth_key = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 16;
        r5 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.key_hash = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 17;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.in_seq_no = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = 18;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x04ac;
    L_0x04aa:
        r3.admin_id = r5;	 Catch:{ Exception -> 0x06a4 }
    L_0x04ac:
        r5 = 19;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r3.mtproto_seq = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = r9.status;	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x04c1;
    L_0x04b8:
        r5 = r9.status;	 Catch:{ Exception -> 0x06a4 }
        r13 = 7;
        r13 = r0.intValue(r13);	 Catch:{ Exception -> 0x06a4 }
        r5.expires = r13;	 Catch:{ Exception -> 0x06a4 }
    L_0x04c1:
        r5 = 1;
        if (r10 != r5) goto L_0x04ef;
    L_0x04c4:
        r5 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r10 = r9.first_name;	 Catch:{ Exception -> 0x06a4 }
        r11 = r9.last_name;	 Catch:{ Exception -> 0x06a4 }
        r10 = org.telegram.messenger.ContactsController.formatName(r10, r11);	 Catch:{ Exception -> 0x06a4 }
        r5.<init>(r10);	 Catch:{ Exception -> 0x06a4 }
        r2.name = r5;	 Catch:{ Exception -> 0x06a4 }
        r5 = r2.name;	 Catch:{ Exception -> 0x06a4 }
        r5 = (android.text.SpannableStringBuilder) r5;	 Catch:{ Exception -> 0x06a4 }
        r10 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x06a4 }
        r11 = "chats_secretName";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);	 Catch:{ Exception -> 0x06a4 }
        r10.<init>(r11);	 Catch:{ Exception -> 0x06a4 }
        r11 = r2.name;	 Catch:{ Exception -> 0x06a4 }
        r11 = r11.length();	 Catch:{ Exception -> 0x06a4 }
        r13 = 33;
        r14 = 0;
        r5.setSpan(r10, r14, r11, r13);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0516;
    L_0x04ef:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r5.<init>();	 Catch:{ Exception -> 0x06a4 }
        r5.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r10 = r9.username;	 Catch:{ Exception -> 0x06a4 }
        r5.append(r10);	 Catch:{ Exception -> 0x06a4 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x06a4 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r10.<init>();	 Catch:{ Exception -> 0x06a4 }
        r10.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r10.append(r11);	 Catch:{ Exception -> 0x06a4 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x06a4 }
        r11 = 0;
        r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r11, r10);	 Catch:{ Exception -> 0x06a4 }
        r2.name = r5;	 Catch:{ Exception -> 0x06a4 }
    L_0x0516:
        r2.object = r3;	 Catch:{ Exception -> 0x06a4 }
        r11 = r19;
        r11.add(r9);	 Catch:{ Exception -> 0x06a4 }
        r18 = r18 + 1;
        goto L_0x052a;
    L_0x0520:
        r11 = r19;
        r15 = 4;
        r9 = r9 + 1;
        goto L_0x03ac;
    L_0x0527:
        r11 = r19;
        r15 = 4;
    L_0x052a:
        r19 = r11;
        goto L_0x0381;
    L_0x052e:
        r11 = r19;
        r0.dispose();	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0536;
    L_0x0534:
        r11 = r19;
    L_0x0536:
        r0 = r18;
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r2.<init>(r0);	 Catch:{ Exception -> 0x06a4 }
        r0 = 0;
    L_0x053e:
        r3 = r12.size();	 Catch:{ Exception -> 0x06a4 }
        if (r0 >= r3) goto L_0x0558;
    L_0x0544:
        r3 = r12.valueAt(r0);	 Catch:{ Exception -> 0x06a4 }
        r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r3;	 Catch:{ Exception -> 0x06a4 }
        r5 = r3.object;	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x0555;
    L_0x054e:
        r5 = r3.name;	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x0555;
    L_0x0552:
        r2.add(r3);	 Catch:{ Exception -> 0x06a4 }
    L_0x0555:
        r0 = r0 + 1;
        goto L_0x053e;
    L_0x0558:
        r0 = org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$FVguPxj8QpbjyrNjyvgW9r4iI6c.INSTANCE;	 Catch:{ Exception -> 0x06a4 }
        java.util.Collections.sort(r2, r0);	 Catch:{ Exception -> 0x06a4 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r0.<init>();	 Catch:{ Exception -> 0x06a4 }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06a4 }
        r3.<init>();	 Catch:{ Exception -> 0x06a4 }
        r5 = 0;
    L_0x0568:
        r9 = r2.size();	 Catch:{ Exception -> 0x06a4 }
        if (r5 >= r9) goto L_0x0581;
    L_0x056e:
        r9 = r2.get(r5);	 Catch:{ Exception -> 0x06a4 }
        r9 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r9;	 Catch:{ Exception -> 0x06a4 }
        r10 = r9.object;	 Catch:{ Exception -> 0x06a4 }
        r0.add(r10);	 Catch:{ Exception -> 0x06a4 }
        r9 = r9.name;	 Catch:{ Exception -> 0x06a4 }
        r3.add(r9);	 Catch:{ Exception -> 0x06a4 }
        r5 = r5 + 1;
        goto L_0x0568;
    L_0x0581:
        r2 = r1.dialogsType;	 Catch:{ Exception -> 0x06a4 }
        r5 = 2;
        if (r2 == r5) goto L_0x069e;
    L_0x0586:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x06a4 }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.getDatabase();	 Catch:{ Exception -> 0x06a4 }
        r5 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
        r9 = 0;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x06a4 }
        r2 = r2.queryFinalized(r5, r10);	 Catch:{ Exception -> 0x06a4 }
    L_0x0599:
        r5 = r2.next();	 Catch:{ Exception -> 0x06a4 }
        if (r5 == 0) goto L_0x069b;
    L_0x059f:
        r5 = 3;
        r9 = r2.intValue(r5);	 Catch:{ Exception -> 0x06a4 }
        r9 = (long) r9;	 Catch:{ Exception -> 0x06a4 }
        r9 = r12.indexOfKey(r9);	 Catch:{ Exception -> 0x06a4 }
        if (r9 < 0) goto L_0x05ac;
    L_0x05ab:
        goto L_0x0599;
    L_0x05ac:
        r9 = 2;
        r10 = r2.stringValue(r9);	 Catch:{ Exception -> 0x06a4 }
        r13 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.getTranslitString(r10);	 Catch:{ Exception -> 0x06a4 }
        r14 = r10.equals(r13);	 Catch:{ Exception -> 0x06a4 }
        if (r14 == 0) goto L_0x05c0;
    L_0x05bf:
        r13 = 0;
    L_0x05c0:
        r14 = r10.lastIndexOf(r4);	 Catch:{ Exception -> 0x06a4 }
        r15 = -1;
        if (r14 == r15) goto L_0x05ce;
    L_0x05c7:
        r14 = r14 + 3;
        r14 = r10.substring(r14);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x05cf;
    L_0x05ce:
        r14 = 0;
    L_0x05cf:
        r5 = r8.length;	 Catch:{ Exception -> 0x06a4 }
        r9 = 0;
        r16 = 0;
    L_0x05d3:
        if (r9 >= r5) goto L_0x0691;
    L_0x05d5:
        r15 = r8[r9];	 Catch:{ Exception -> 0x06a4 }
        r18 = r10.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r18 != 0) goto L_0x061f;
    L_0x05dd:
        r18 = r4;
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r4.<init>();	 Catch:{ Exception -> 0x06a4 }
        r4.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r4.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x06a4 }
        r4 = r10.contains(r4);	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x0621;
    L_0x05f4:
        if (r13 == 0) goto L_0x0612;
    L_0x05f6:
        r4 = r13.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r4 != 0) goto L_0x0621;
    L_0x05fc:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r4.<init>();	 Catch:{ Exception -> 0x06a4 }
        r4.append(r7);	 Catch:{ Exception -> 0x06a4 }
        r4.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x06a4 }
        r4 = r13.contains(r4);	 Catch:{ Exception -> 0x06a4 }
        if (r4 == 0) goto L_0x0612;
    L_0x0611:
        goto L_0x0621;
    L_0x0612:
        if (r14 == 0) goto L_0x061c;
    L_0x0614:
        r4 = r14.startsWith(r15);	 Catch:{ Exception -> 0x06a4 }
        if (r4 == 0) goto L_0x061c;
    L_0x061a:
        r4 = 2;
        goto L_0x0622;
    L_0x061c:
        r4 = r16;
        goto L_0x0622;
    L_0x061f:
        r18 = r4;
    L_0x0621:
        r4 = 1;
    L_0x0622:
        if (r4 == 0) goto L_0x0680;
    L_0x0624:
        r5 = 0;
        r9 = r2.byteBufferValue(r5);	 Catch:{ Exception -> 0x06a4 }
        if (r9 == 0) goto L_0x0694;
    L_0x062b:
        r10 = r9.readInt32(r5);	 Catch:{ Exception -> 0x06a4 }
        r10 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r10, r5);	 Catch:{ Exception -> 0x06a4 }
        r9.reuse();	 Catch:{ Exception -> 0x06a4 }
        r9 = r10.status;	 Catch:{ Exception -> 0x06a4 }
        if (r9 == 0) goto L_0x0644;
    L_0x063a:
        r9 = r10.status;	 Catch:{ Exception -> 0x06a4 }
        r13 = 1;
        r14 = r2.intValue(r13);	 Catch:{ Exception -> 0x06a4 }
        r13 = r14;
        r9.expires = r13;	 Catch:{ Exception -> 0x06a4 }
    L_0x0644:
        r9 = 1;
        if (r4 != r9) goto L_0x0654;
    L_0x0647:
        r4 = r10.first_name;	 Catch:{ Exception -> 0x06a4 }
        r13 = r10.last_name;	 Catch:{ Exception -> 0x06a4 }
        r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r13, r15);	 Catch:{ Exception -> 0x06a4 }
        r3.add(r4);	 Catch:{ Exception -> 0x06a4 }
        r15 = 0;
        goto L_0x067c;
    L_0x0654:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r4.<init>();	 Catch:{ Exception -> 0x06a4 }
        r4.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r13 = r10.username;	 Catch:{ Exception -> 0x06a4 }
        r4.append(r13);	 Catch:{ Exception -> 0x06a4 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x06a4 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x06a4 }
        r13.<init>();	 Catch:{ Exception -> 0x06a4 }
        r13.append(r6);	 Catch:{ Exception -> 0x06a4 }
        r13.append(r15);	 Catch:{ Exception -> 0x06a4 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x06a4 }
        r15 = 0;
        r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r15, r13);	 Catch:{ Exception -> 0x06a4 }
        r3.add(r4);	 Catch:{ Exception -> 0x06a4 }
    L_0x067c:
        r0.add(r10);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x0695;
    L_0x0680:
        r16 = r5;
        r5 = 0;
        r15 = 0;
        r17 = 1;
        r9 = r9 + 1;
        r5 = r16;
        r15 = -1;
        r16 = r4;
        r4 = r18;
        goto L_0x05d3;
    L_0x0691:
        r18 = r4;
        r5 = 0;
    L_0x0694:
        r15 = 0;
    L_0x0695:
        r17 = 1;
        r4 = r18;
        goto L_0x0599;
    L_0x069b:
        r2.dispose();	 Catch:{ Exception -> 0x06a4 }
    L_0x069e:
        r2 = r26;
        r1.updateSearchResults(r0, r3, r11, r2);	 Catch:{ Exception -> 0x06a4 }
        goto L_0x06a8;
    L_0x06a4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x06a8:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.lambda$searchDialogsInternal$8$DialogsSearchAdapter(java.lang.String, int):void");
    }

    static /* synthetic */ int lambda$null$7(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<User> arrayList3, int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$DialogsSearchAdapter$-rG-J91Hu2opSK8sjEhFEpnZiuA(this, i, arrayList, arrayList3, arrayList2));
    }

    public /* synthetic */ void lambda$updateSearchResults$9$DialogsSearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        if (i == this.lastSearchId) {
            this.searchWas = true;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLObject tLObject = (TLObject) arrayList.get(i2);
                if (tLObject instanceof User) {
                    MessagesController.getInstance(this.currentAccount).putUser((User) tLObject, true);
                } else if (tLObject instanceof Chat) {
                    MessagesController.getInstance(this.currentAccount).putChat((Chat) tLObject, true);
                } else if (tLObject instanceof EncryptedChat) {
                    MessagesController.getInstance(this.currentAccount).putEncryptedChat((EncryptedChat) tLObject, true);
                }
            }
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
            this.searchResult = arrayList;
            this.searchResultNames = arrayList3;
            this.searchAdapterHelper.mergeResults(this.searchResult);
            notifyDataSetChanged();
            if (this.delegate != null) {
                if (getItemCount() != 0 || (this.searchRunnable2 == null && !this.searchAdapterHelper.isSearchInProgress())) {
                    this.delegate.searchStateChanged(false);
                } else {
                    this.delegate.searchStateChanged(true);
                }
            }
        }
    }

    public boolean isHashtagSearch() {
        return this.searchResultHashtags.isEmpty() ^ 1;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(String str) {
        if (str == null || !str.equals(this.lastSearchText)) {
            this.lastSearchText = str;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            Runnable runnable = this.searchRunnable2;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable2 = null;
            }
            CharSequence trim = str != null ? str.trim() : null;
            if (TextUtils.isEmpty(trim)) {
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults(null);
                if (this.needMessagesSearch != 2) {
                    this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, 0, this.dialogsType == 0, 0);
                }
                this.searchWas = false;
                this.lastSearchId = -1;
                searchMessagesInternal(null);
                notifyDataSetChanged();
            } else {
                if (this.needMessagesSearch != 2 && trim.startsWith("#") && trim.length() == 1) {
                    this.messagesSearchEndReached = true;
                    if (this.searchAdapterHelper.loadRecentHashtags()) {
                        this.searchResultMessages.clear();
                        this.searchResultHashtags.clear();
                        ArrayList hashtags = this.searchAdapterHelper.getHashtags();
                        for (int i = 0; i < hashtags.size(); i++) {
                            this.searchResultHashtags.add(((HashtagObject) hashtags.get(i)).hashtag);
                        }
                        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                        if (dialogsSearchAdapterDelegate != null) {
                            dialogsSearchAdapterDelegate.searchStateChanged(false);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    this.searchResultHashtags.clear();
                    notifyDataSetChanged();
                }
                int i2 = this.lastSearchId + 1;
                this.lastSearchId = i2;
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                -$$Lambda$DialogsSearchAdapter$2OrdD5nR-nF-l2wgPqqTnpM3YF8 -__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8 = new -$$Lambda$DialogsSearchAdapter$2OrdD5nR-nF-l2wgPqqTnpM3YF8(this, trim, i2);
                this.searchRunnable = -__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8;
                dispatchQueue.postRunnable(-__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8, 300);
            }
        }
    }

    public /* synthetic */ void lambda$searchDialogs$11$DialogsSearchAdapter(String str, int i) {
        this.searchRunnable = null;
        searchDialogsInternal(str, i);
        -$$Lambda$DialogsSearchAdapter$VsX50TGE7QZO8RUCru1or1M1bIg -__lambda_dialogssearchadapter_vsx50tge7qzo8rucru1or1m1big = new -$$Lambda$DialogsSearchAdapter$VsX50TGE7QZO8RUCru1or1M1bIg(this, i, str);
        this.searchRunnable2 = -__lambda_dialogssearchadapter_vsx50tge7qzo8rucru1or1m1big;
        AndroidUtilities.runOnUIThread(-__lambda_dialogssearchadapter_vsx50tge7qzo8rucru1or1m1big);
    }

    public /* synthetic */ void lambda$null$10$DialogsSearchAdapter(int i, String str) {
        this.searchRunnable2 = null;
        if (i == this.lastSearchId) {
            if (this.needMessagesSearch != 2) {
                this.searchAdapterHelper.queryServerSearch(str, true, this.dialogsType != 4, true, this.dialogsType != 4, 0, this.dialogsType == 0, 0);
            }
            searchMessagesInternal(str);
        }
    }

    public int getItemCount() {
        int i;
        int size;
        if (isRecentSearchDisplayed()) {
            i = 0;
            size = !this.recentSearchObjects.isEmpty() ? this.recentSearchObjects.size() + 1 : 0;
            if (!MediaDataController.getInstance(this.currentAccount).hints.isEmpty()) {
                i = 2;
            }
            return size + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            size = this.searchResult.size();
            i = this.searchAdapterHelper.getLocalServerSearch().size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            int size3 = this.searchAdapterHelper.getPhoneSearch().size();
            int size4 = this.searchResultMessages.size();
            size += i;
            if (size2 != 0) {
                size += size2 + 1;
            }
            if (size3 != 0) {
                size += size3;
            }
            if (size4 != 0) {
                size += (size4 + 1) + (this.messagesSearchEndReached ^ 1);
            }
            return size;
        }
    }

    /* JADX WARNING: Missing block: B:11:0x0045, code skipped:
            if (r0 != null) goto L_0x0061;
     */
    /* JADX WARNING: Missing block: B:15:0x005f, code skipped:
            if (r0 != null) goto L_0x0061;
     */
    public java.lang.Object getItem(int r11) {
        /*
        r10 = this;
        r0 = r10.isRecentSearchDisplayed();
        r1 = 0;
        r2 = 0;
        if (r0 == 0) goto L_0x0064;
    L_0x0008:
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.MediaDataController.getInstance(r0);
        r0 = r0.hints;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0017;
    L_0x0016:
        r2 = 2;
    L_0x0017:
        if (r11 <= r2) goto L_0x0063;
    L_0x0019:
        r11 = r11 + -1;
        r11 = r11 - r2;
        r0 = r10.recentSearchObjects;
        r0 = r0.size();
        if (r11 >= r0) goto L_0x0063;
    L_0x0024:
        r0 = r10.recentSearchObjects;
        r11 = r0.get(r11);
        r11 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r11;
        r11 = r11.object;
        r0 = r11 instanceof org.telegram.tgnet.TLRPC.User;
        if (r0 == 0) goto L_0x0048;
    L_0x0032:
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r11;
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getUser(r1);
        if (r0 == 0) goto L_0x0062;
    L_0x0047:
        goto L_0x0061;
    L_0x0048:
        r0 = r11 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r0 == 0) goto L_0x0062;
    L_0x004c:
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r11;
        r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        if (r0 == 0) goto L_0x0062;
    L_0x0061:
        r11 = r0;
    L_0x0062:
        return r11;
    L_0x0063:
        return r1;
    L_0x0064:
        r0 = r10.searchResultHashtags;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0078;
    L_0x006c:
        if (r11 <= 0) goto L_0x0077;
    L_0x006e:
        r0 = r10.searchResultHashtags;
        r11 = r11 + -1;
        r11 = r0.get(r11);
        return r11;
    L_0x0077:
        return r1;
    L_0x0078:
        r0 = r10.searchAdapterHelper;
        r0 = r0.getGlobalSearch();
        r3 = r10.searchAdapterHelper;
        r3 = r3.getLocalServerSearch();
        r4 = r10.searchAdapterHelper;
        r4 = r4.getPhoneSearch();
        r5 = r10.searchResult;
        r5 = r5.size();
        r6 = r3.size();
        r7 = r4.size();
        r8 = r0.isEmpty();
        if (r8 == 0) goto L_0x00a0;
    L_0x009e:
        r8 = 0;
        goto L_0x00a6;
    L_0x00a0:
        r8 = r0.size();
        r8 = r8 + 1;
    L_0x00a6:
        r9 = r10.searchResultMessages;
        r9 = r9.isEmpty();
        if (r9 == 0) goto L_0x00af;
    L_0x00ae:
        goto L_0x00b7;
    L_0x00af:
        r2 = r10.searchResultMessages;
        r2 = r2.size();
        r2 = r2 + 1;
    L_0x00b7:
        if (r11 < 0) goto L_0x00c2;
    L_0x00b9:
        if (r11 >= r5) goto L_0x00c2;
    L_0x00bb:
        r0 = r10.searchResult;
        r11 = r0.get(r11);
        return r11;
    L_0x00c2:
        r11 = r11 - r5;
        if (r11 < 0) goto L_0x00cc;
    L_0x00c5:
        if (r11 >= r6) goto L_0x00cc;
    L_0x00c7:
        r11 = r3.get(r11);
        return r11;
    L_0x00cc:
        r11 = r11 - r6;
        if (r11 < 0) goto L_0x00d6;
    L_0x00cf:
        if (r11 >= r7) goto L_0x00d6;
    L_0x00d1:
        r11 = r4.get(r11);
        return r11;
    L_0x00d6:
        r11 = r11 - r7;
        if (r11 <= 0) goto L_0x00e2;
    L_0x00d9:
        if (r11 >= r8) goto L_0x00e2;
    L_0x00db:
        r11 = r11 + -1;
        r11 = r0.get(r11);
        return r11;
    L_0x00e2:
        r11 = r11 - r8;
        if (r11 <= 0) goto L_0x00f0;
    L_0x00e5:
        if (r11 >= r2) goto L_0x00f0;
    L_0x00e7:
        r0 = r10.searchResultMessages;
        r11 = r11 + -1;
        r11 = r0.get(r11);
        return r11;
    L_0x00f0:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.getItem(int):java.lang.Object");
    }

    public boolean isGlobalSearch(int i) {
        if (isRecentSearchDisplayed() || !this.searchResultHashtags.isEmpty()) {
            return false;
        }
        ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
        ArrayList localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
        int size = this.searchResult.size();
        int size2 = localServerSearch.size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        int size4 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        int size5 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
        if (i >= 0 && i < size) {
            return false;
        }
        i -= size;
        if (i >= 0 && i < size2) {
            return false;
        }
        i -= size2;
        if (i > 0 && i < size3) {
            return false;
        }
        i -= size3;
        if (i > 0 && i < size4) {
            return true;
        }
        i -= size4;
        if (i <= 0 || i < size5) {
        }
        return false;
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 3) ? false : true;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$12$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.didPressedOnSubDialog((long) ((Integer) view.getTag()).intValue());
        }
    }

    public /* synthetic */ boolean lambda$onCreateViewHolder$13$DialogsSearchAdapter(View view, int i) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needRemoveHint(((Integer) view.getTag()).intValue());
        }
        return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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
                View anonymousClass2 = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                anonymousClass2.setTag(Integer.valueOf(9));
                anonymousClass2.setItemAnimator(null);
                anonymousClass2.setLayoutAnimation(null);
                AnonymousClass3 anonymousClass3 = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                anonymousClass3.setOrientation(0);
                anonymousClass2.setLayoutManager(anonymousClass3);
                anonymousClass2.setAdapter(new CategoryAdapterRecycler(this, null));
                anonymousClass2.setOnItemClickListener(new -$$Lambda$DialogsSearchAdapter$DZkEHCwRy7JqjbUQmUNPYIVHu-I(this));
                anonymousClass2.setOnItemLongClickListener(new -$$Lambda$DialogsSearchAdapter$VmJg1wMYhOLJS8dwKIzHrQMjS0A(this));
                this.innerListView = anonymousClass2;
                view = anonymousClass2;
                break;
            case 6:
                view = new TextCell(this.mContext, 16);
                break;
        }
        if (i == 5) {
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            view.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(view);
    }

    /* JADX WARNING: Removed duplicated region for block: B:150:0x036e  */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
        /*
        r16 = this;
        r1 = r16;
        r0 = r17;
        r2 = r18;
        r3 = r17.getItemViewType();
        r4 = 2;
        r5 = 0;
        r6 = 0;
        r7 = 1;
        if (r3 == 0) goto L_0x017e;
    L_0x0010:
        if (r3 == r7) goto L_0x00a9;
    L_0x0012:
        if (r3 == r4) goto L_0x0088;
    L_0x0014:
        r8 = 4;
        if (r3 == r8) goto L_0x0069;
    L_0x0017:
        r8 = 5;
        if (r3 == r8) goto L_0x0059;
    L_0x001a:
        r4 = 6;
        if (r3 == r4) goto L_0x001f;
    L_0x001d:
        goto L_0x0374;
    L_0x001f:
        r2 = r1.getItem(r2);
        r2 = (java.lang.String) r2;
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.TextCell) r0;
        r3 = "windowBackgroundWhiteBlueText2";
        r0.setColors(r5, r3);
        r3 = NUM; // 0x7f0d00a8 float:1.8742455E38 double:1.0531298605E-314;
        r4 = new java.lang.Object[r7];
        r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "+";
        r7.append(r8);
        r7.append(r2);
        r2 = r7.toString();
        r2 = r5.format(r2);
        r4[r6] = r2;
        r2 = "AddContactByPhone";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);
        r0.setText(r2, r6);
        goto L_0x0374;
    L_0x0059:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Components.RecyclerListView) r0;
        r0 = r0.getAdapter();
        r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0;
        r2 = r2 / r4;
        r0.setIndex(r2);
        goto L_0x0374;
    L_0x0069:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0;
        r3 = r1.searchResultHashtags;
        r4 = r2 + -1;
        r3 = r3.get(r4);
        r3 = (java.lang.CharSequence) r3;
        r0.setText(r3);
        r3 = r1.searchResultHashtags;
        r3 = r3.size();
        if (r2 == r3) goto L_0x0083;
    L_0x0082:
        r6 = 1;
    L_0x0083:
        r0.setNeedDivider(r6);
        goto L_0x0374;
    L_0x0088:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.DialogCell) r0;
        r3 = r16.getItemCount();
        r3 = r3 - r7;
        if (r2 == r3) goto L_0x0094;
    L_0x0093:
        r6 = 1;
    L_0x0094:
        r0.useSeparator = r6;
        r2 = r1.getItem(r2);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r2.getDialogId();
        r5 = r2.messageOwner;
        r5 = r5.date;
        r0.setDialog(r3, r2, r5);
        goto L_0x0374;
    L_0x00a9:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.GraySectionCell) r0;
        r3 = r16.isRecentSearchDisplayed();
        r5 = NUM; // 0x7f0d02d0 float:1.8743575E38 double:1.0531301333E-314;
        r8 = "ClearButton";
        if (r3 == 0) goto L_0x00ef;
    L_0x00b8:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MediaDataController.getInstance(r3);
        r3 = r3.hints;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x00c7;
    L_0x00c6:
        goto L_0x00c8;
    L_0x00c7:
        r4 = 0;
    L_0x00c8:
        if (r2 >= r4) goto L_0x00d8;
    L_0x00ca:
        r2 = NUM; // 0x7f0d0283 float:1.8743419E38 double:1.053130095E-314;
        r3 = "ChatHints";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x0374;
    L_0x00d8:
        r2 = NUM; // 0x7f0d089e float:1.874659E38 double:1.0531308675E-314;
        r3 = "Recent";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = org.telegram.messenger.LocaleController.getString(r8, r5);
        r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$941fnPDSgReuKOmz7WsSoxVuOTY;
        r4.<init>(r1);
        r0.setText(r2, r3, r4);
        goto L_0x0374;
    L_0x00ef:
        r3 = r1.searchResultHashtags;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x010e;
    L_0x00f7:
        r2 = NUM; // 0x7f0d04fb float:1.87447E38 double:1.0531304075E-314;
        r3 = "Hashtags";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r3 = org.telegram.messenger.LocaleController.getString(r8, r5);
        r4 = new org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$honrBco-zV9w0fwaI91SKdwfMI0;
        r4.<init>(r1);
        r0.setText(r2, r3, r4);
        goto L_0x0374;
    L_0x010e:
        r3 = r1.searchAdapterHelper;
        r3 = r3.getGlobalSearch();
        r4 = r1.searchResult;
        r4 = r4.size();
        r5 = r1.searchAdapterHelper;
        r5 = r5.getLocalServerSearch();
        r5 = r5.size();
        r8 = r1.searchAdapterHelper;
        r8 = r8.getPhoneSearch();
        r8 = r8.size();
        r9 = r3.isEmpty();
        if (r9 == 0) goto L_0x0135;
    L_0x0134:
        goto L_0x013b;
    L_0x0135:
        r3 = r3.size();
        r6 = r3 + 1;
    L_0x013b:
        r3 = r1.searchResultMessages;
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x0144;
    L_0x0143:
        goto L_0x0149;
    L_0x0144:
        r3 = r1.searchResultMessages;
        r3.size();
    L_0x0149:
        r4 = r4 + r5;
        r2 = r2 - r4;
        if (r2 < 0) goto L_0x015d;
    L_0x014d:
        if (r2 >= r8) goto L_0x015d;
    L_0x014f:
        r2 = NUM; // 0x7f0d0832 float:1.874637E38 double:1.053130814E-314;
        r3 = "PhoneNumberSearch";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x0374;
    L_0x015d:
        r2 = r2 - r8;
        if (r2 < 0) goto L_0x0170;
    L_0x0160:
        if (r2 >= r6) goto L_0x0170;
    L_0x0162:
        r2 = NUM; // 0x7f0d04d2 float:1.8744618E38 double:1.053130387E-314;
        r3 = "GlobalSearch";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x0374;
    L_0x0170:
        r2 = NUM; // 0x7f0d091a float:1.874684E38 double:1.0531309287E-314;
        r3 = "SearchMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x0374;
    L_0x017e:
        r0 = r0.itemView;
        r8 = r0;
        r8 = (org.telegram.ui.Cells.ProfileSearchCell) r8;
        r0 = r1.getItem(r2);
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        if (r3 == 0) goto L_0x0194;
    L_0x018b:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r3 = r0.username;
        r10 = r3;
        r9 = r5;
        r11 = r9;
        r3 = r0;
        goto L_0x01e3;
    L_0x0194:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r3 == 0) goto L_0x01b5;
    L_0x0198:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r9 = r0.id;
        r9 = java.lang.Integer.valueOf(r9);
        r3 = r3.getChat(r9);
        if (r3 != 0) goto L_0x01ad;
    L_0x01ac:
        goto L_0x01ae;
    L_0x01ad:
        r0 = r3;
    L_0x01ae:
        r3 = r0.username;
        r9 = r0;
        r10 = r3;
        r3 = r5;
        r11 = r3;
        goto L_0x01e3;
    L_0x01b5:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.EncryptedChat;
        if (r3 == 0) goto L_0x01df;
    L_0x01b9:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r0 = (org.telegram.tgnet.TLRPC.EncryptedChat) r0;
        r0 = r0.id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r3.getEncryptedChat(r0);
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r9 = r0.user_id;
        r9 = java.lang.Integer.valueOf(r9);
        r3 = r3.getUser(r9);
        r11 = r0;
        r9 = r5;
        r10 = r9;
        goto L_0x01e3;
    L_0x01df:
        r3 = r5;
        r9 = r3;
        r10 = r9;
        r11 = r10;
    L_0x01e3:
        r0 = r16.isRecentSearchDisplayed();
        if (r0 == 0) goto L_0x01f9;
    L_0x01e9:
        r0 = r16.getItemCount();
        r0 = r0 - r7;
        if (r2 == r0) goto L_0x01f2;
    L_0x01f0:
        r0 = 1;
        goto L_0x01f3;
    L_0x01f2:
        r0 = 0;
    L_0x01f3:
        r8.useSeparator = r0;
        r0 = r5;
        r13 = 1;
        goto L_0x030f;
    L_0x01f9:
        r0 = r1.searchAdapterHelper;
        r0 = r0.getGlobalSearch();
        r12 = r1.searchAdapterHelper;
        r12 = r12.getPhoneSearch();
        r13 = r1.searchResult;
        r13 = r13.size();
        r14 = r1.searchAdapterHelper;
        r14 = r14.getLocalServerSearch();
        r14 = r14.size();
        r15 = r12.size();
        if (r15 <= 0) goto L_0x0228;
    L_0x021b:
        r5 = r15 + -1;
        r5 = r12.get(r5);
        r5 = r5 instanceof java.lang.String;
        if (r5 == 0) goto L_0x0228;
    L_0x0225:
        r5 = r15 + -2;
        goto L_0x0229;
    L_0x0228:
        r5 = r15;
    L_0x0229:
        r12 = r0.isEmpty();
        if (r12 == 0) goto L_0x0231;
    L_0x022f:
        r0 = 0;
        goto L_0x0236;
    L_0x0231:
        r0 = r0.size();
        r0 = r0 + r7;
    L_0x0236:
        r12 = r16.getItemCount();
        r12 = r12 - r7;
        if (r2 == r12) goto L_0x024a;
    L_0x023d:
        r5 = r5 + r13;
        r5 = r5 + r14;
        r5 = r5 - r7;
        if (r2 == r5) goto L_0x024a;
    L_0x0242:
        r13 = r13 + r0;
        r13 = r13 + r15;
        r13 = r13 + r14;
        r13 = r13 - r7;
        if (r2 == r13) goto L_0x024a;
    L_0x0248:
        r0 = 1;
        goto L_0x024b;
    L_0x024a:
        r0 = 0;
    L_0x024b:
        r8.useSeparator = r0;
        r0 = r1.searchResult;
        r0 = r0.size();
        r5 = "@";
        if (r2 >= r0) goto L_0x028c;
    L_0x0257:
        r0 = r1.searchResultNames;
        r0 = r0.get(r2);
        r0 = (java.lang.CharSequence) r0;
        if (r0 == 0) goto L_0x030d;
    L_0x0261:
        if (r3 == 0) goto L_0x030d;
    L_0x0263:
        r2 = r3.username;
        if (r2 == 0) goto L_0x030d;
    L_0x0267:
        r2 = r2.length();
        if (r2 <= 0) goto L_0x030d;
    L_0x026d:
        r2 = r0.toString();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r5);
        r5 = r3.username;
        r10.append(r5);
        r5 = r10.toString();
        r2 = r2.startsWith(r5);
        if (r2 == 0) goto L_0x030d;
    L_0x0288:
        r5 = r0;
    L_0x0289:
        r0 = 0;
        goto L_0x030e;
    L_0x028c:
        r0 = r1.searchAdapterHelper;
        r0 = r0.getLastFoundUsername();
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x030c;
    L_0x0298:
        if (r3 == 0) goto L_0x02a3;
    L_0x029a:
        r2 = r3.first_name;
        r12 = r3.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r12);
        goto L_0x02a9;
    L_0x02a3:
        if (r9 == 0) goto L_0x02a8;
    L_0x02a5:
        r2 = r9.title;
        goto L_0x02a9;
    L_0x02a8:
        r2 = 0;
    L_0x02a9:
        r12 = 33;
        r13 = "windowBackgroundWhiteBlueText4";
        r14 = -1;
        if (r2 == 0) goto L_0x02ce;
    L_0x02b0:
        r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r0);
        if (r15 == r14) goto L_0x02ce;
    L_0x02b6:
        r5 = new android.text.SpannableStringBuilder;
        r5.<init>(r2);
        r2 = new android.text.style.ForegroundColorSpan;
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r13);
        r2.<init>(r10);
        r0 = r0.length();
        r0 = r0 + r15;
        r5.setSpan(r2, r15, r0, r12);
        r0 = r5;
        goto L_0x030d;
    L_0x02ce:
        if (r10 == 0) goto L_0x030c;
    L_0x02d0:
        r2 = r0.startsWith(r5);
        if (r2 == 0) goto L_0x02da;
    L_0x02d6:
        r0 = r0.substring(r7);
    L_0x02da:
        r2 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0305 }
        r2.<init>();	 Catch:{ Exception -> 0x0305 }
        r2.append(r5);	 Catch:{ Exception -> 0x0305 }
        r2.append(r10);	 Catch:{ Exception -> 0x0305 }
        r5 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r0);	 Catch:{ Exception -> 0x0305 }
        if (r5 == r14) goto L_0x0303;
    L_0x02eb:
        r0 = r0.length();	 Catch:{ Exception -> 0x0305 }
        if (r5 != 0) goto L_0x02f4;
    L_0x02f1:
        r0 = r0 + 1;
        goto L_0x02f6;
    L_0x02f4:
        r5 = r5 + 1;
    L_0x02f6:
        r14 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0305 }
        r13 = org.telegram.ui.ActionBar.Theme.getColor(r13);	 Catch:{ Exception -> 0x0305 }
        r14.<init>(r13);	 Catch:{ Exception -> 0x0305 }
        r0 = r0 + r5;
        r2.setSpan(r14, r5, r0, r12);	 Catch:{ Exception -> 0x0305 }
    L_0x0303:
        r5 = r2;
        goto L_0x0289;
    L_0x0305:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r5 = r10;
        goto L_0x0289;
    L_0x030c:
        r0 = 0;
    L_0x030d:
        r5 = 0;
    L_0x030e:
        r13 = 0;
    L_0x030f:
        if (r3 == 0) goto L_0x0323;
    L_0x0311:
        r2 = r3.id;
        r10 = r1.selfUserId;
        if (r2 != r10) goto L_0x0323;
    L_0x0317:
        r0 = NUM; // 0x7f0d0904 float:1.8746796E38 double:1.053130918E-314;
        r2 = "SavedMessages";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r5 = 0;
        r14 = 1;
        goto L_0x0324;
    L_0x0323:
        r14 = 0;
    L_0x0324:
        if (r9 == 0) goto L_0x036b;
    L_0x0326:
        r2 = r9.participants_count;
        if (r2 == 0) goto L_0x036b;
    L_0x032a:
        r2 = org.telegram.messenger.ChatObject.isChannel(r9);
        if (r2 == 0) goto L_0x033d;
    L_0x0330:
        r2 = r9.megagroup;
        if (r2 != 0) goto L_0x033d;
    L_0x0334:
        r2 = r9.participants_count;
        r10 = "Subscribers";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2);
        goto L_0x0345;
    L_0x033d:
        r2 = r9.participants_count;
        r10 = "Members";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2);
    L_0x0345:
        r10 = r5 instanceof android.text.SpannableStringBuilder;
        r12 = ", ";
        if (r10 == 0) goto L_0x0356;
    L_0x034b:
        r4 = r5;
        r4 = (android.text.SpannableStringBuilder) r4;
        r4 = r4.append(r12);
        r4.append(r2);
        goto L_0x036b;
    L_0x0356:
        r10 = android.text.TextUtils.isEmpty(r5);
        if (r10 != 0) goto L_0x0369;
    L_0x035c:
        r10 = 3;
        r10 = new java.lang.CharSequence[r10];
        r10[r6] = r5;
        r10[r7] = r12;
        r10[r4] = r2;
        r2 = android.text.TextUtils.concat(r10);
    L_0x0369:
        r12 = r2;
        goto L_0x036c;
    L_0x036b:
        r12 = r5;
    L_0x036c:
        if (r3 == 0) goto L_0x036f;
    L_0x036e:
        r9 = r3;
    L_0x036f:
        r10 = r11;
        r11 = r0;
        r8.setData(r9, r10, r11, r12, r13, r14);
    L_0x0374:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    public /* synthetic */ void lambda$onBindViewHolder$14$DialogsSearchAdapter(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public /* synthetic */ void lambda$onBindViewHolder$15$DialogsSearchAdapter(View view) {
        DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
        if (dialogsSearchAdapterDelegate != null) {
            dialogsSearchAdapterDelegate.needClearList();
        }
    }

    public int getItemViewType(int i) {
        int i2 = 1;
        int i3;
        if (isRecentSearchDisplayed()) {
            i3 = !MediaDataController.getInstance(this.currentAccount).hints.isEmpty() ? 2 : 0;
            if (i > i3) {
                return 0;
            }
            if (i == i3 || i % 2 == 0) {
                return 1;
            }
            return 5;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
            int size3 = this.searchAdapterHelper.getPhoneSearch().size();
            i3 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int size4 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if (i >= 0 && i < size) {
                return 0;
            }
            i -= size;
            if (i >= 0 && i < size2) {
                return 0;
            }
            i -= size2;
            if (i < 0 || i >= size3) {
                i -= size3;
                if (i >= 0 && i < i3) {
                    return i == 0 ? 1 : 0;
                } else {
                    i -= i3;
                    if (i < 0 || i >= size4) {
                        return 3;
                    }
                    return i == 0 ? 1 : 2;
                }
            } else {
                Object item = getItem(i);
                if (!(item instanceof String)) {
                    return 0;
                }
                if ("section".equals((String) item)) {
                    return 1;
                }
                return 6;
            }
        } else {
            if (i != 0) {
                i2 = 4;
            }
            return i2;
        }
    }
}
