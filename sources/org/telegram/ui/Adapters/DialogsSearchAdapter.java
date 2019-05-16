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
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
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
            TL_topPeer tL_topPeer = (TL_topPeer) DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(i);
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
            return DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
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
        DataQuery.getInstance(this.currentAccount).loadHints(true);
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
        if (this.needMessagesSearch != 0) {
            String str2 = this.lastMessagesSearchString;
            if (!((str2 == null || str2.length() == 0) && (str == null || str.length() == 0))) {
                if (this.reqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
                if (str == null || str.length() == 0) {
                    this.searchResultMessages.clear();
                    this.lastReqId = 0;
                    this.lastMessagesSearchString = null;
                    notifyDataSetChanged();
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate = this.delegate;
                    if (dialogsSearchAdapterDelegate != null) {
                        dialogsSearchAdapterDelegate.searchStateChanged(false);
                    }
                } else {
                    TL_messages_searchGlobal tL_messages_searchGlobal = new TL_messages_searchGlobal();
                    tL_messages_searchGlobal.limit = 20;
                    tL_messages_searchGlobal.q = str;
                    if (!str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                        tL_messages_searchGlobal.offset_date = 0;
                        tL_messages_searchGlobal.offset_id = 0;
                        tL_messages_searchGlobal.offset_peer = new TL_inputPeerEmpty();
                    } else {
                        int i;
                        ArrayList arrayList = this.searchResultMessages;
                        MessageObject messageObject = (MessageObject) arrayList.get(arrayList.size() - 1);
                        tL_messages_searchGlobal.offset_id = messageObject.getId();
                        Message message = messageObject.messageOwner;
                        tL_messages_searchGlobal.offset_date = message.date;
                        Peer peer = message.to_id;
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
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate2 = this.delegate;
                    if (dialogsSearchAdapterDelegate2 != null) {
                        dialogsSearchAdapterDelegate2.searchStateChanged(true);
                    }
                    this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_searchGlobal, new -$$Lambda$DialogsSearchAdapter$jvBeESgA8AL2rHOVVbFgj80mlPg(this, i3, tL_messages_searchGlobal), 2);
                }
            }
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
        return (this.recentSearchObjects.isEmpty() && DataQuery.getInstance(this.currentAccount).hints.isEmpty()) ? false : true;
    }

    public boolean isRecentSearchDisplayed() {
        if (this.needMessagesSearch != 2) {
            String str = this.lastSearchText;
            if ((str == null || str.length() == 0) && !(this.recentSearchObjects.isEmpty() && DataQuery.getInstance(this.currentAccount).hints.isEmpty())) {
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

    /* JADX WARNING: Removed duplicated region for block: B:95:0x0252 A:{LOOP_END, Catch:{ Exception -> 0x068b }, LOOP:2: B:65:0x0194->B:95:0x0252} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x01e7 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x050b A:{LOOP_END, Catch:{ Exception -> 0x068b }, LOOP:6: B:150:0x0397->B:189:0x050b} */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x03e2 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x0668 A:{LOOP_END, Catch:{ Exception -> 0x068b }, LOOP:10: B:223:0x05bc->B:252:0x0668} */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x060d A:{SYNTHETIC} */
    public /* synthetic */ void lambda$searchDialogsInternal$8$DialogsSearchAdapter(java.lang.String r25, int r26) {
        /*
        r24 = this;
        r1 = r24;
        r0 = "SavedMessages";
        r2 = NUM; // 0x7f0d087d float:1.8746522E38 double:1.053130851E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);	 Catch:{ Exception -> 0x068b }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x068b }
        r2 = r25.trim();	 Catch:{ Exception -> 0x068b }
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x068b }
        r3 = r2.length();	 Catch:{ Exception -> 0x068b }
        r4 = -1;
        if (r3 != 0) goto L_0x0035;
    L_0x001e:
        r1.lastSearchId = r4;	 Catch:{ Exception -> 0x068b }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r0.<init>();	 Catch:{ Exception -> 0x068b }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r2.<init>();	 Catch:{ Exception -> 0x068b }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r3.<init>();	 Catch:{ Exception -> 0x068b }
        r4 = r1.lastSearchId;	 Catch:{ Exception -> 0x068b }
        r1.updateSearchResults(r0, r2, r3, r4);	 Catch:{ Exception -> 0x068b }
        return;
    L_0x0035:
        r3 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x068b }
        r3 = r3.getTranslitString(r2);	 Catch:{ Exception -> 0x068b }
        r5 = r2.equals(r3);	 Catch:{ Exception -> 0x068b }
        r6 = 0;
        if (r5 != 0) goto L_0x004a;
    L_0x0044:
        r5 = r3.length();	 Catch:{ Exception -> 0x068b }
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
        r8 = new java.lang.String[r8];	 Catch:{ Exception -> 0x068b }
        r8[r7] = r2;	 Catch:{ Exception -> 0x068b }
        if (r3 == 0) goto L_0x005b;
    L_0x0059:
        r8[r5] = r3;	 Catch:{ Exception -> 0x068b }
    L_0x005b:
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r3.<init>();	 Catch:{ Exception -> 0x068b }
        r9 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r9.<init>();	 Catch:{ Exception -> 0x068b }
        r10 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r10.<init>();	 Catch:{ Exception -> 0x068b }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r11.<init>();	 Catch:{ Exception -> 0x068b }
        r12 = new android.util.LongSparseArray;	 Catch:{ Exception -> 0x068b }
        r12.<init>();	 Catch:{ Exception -> 0x068b }
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r13 = org.telegram.messenger.MessagesStorage.getInstance(r13);	 Catch:{ Exception -> 0x068b }
        r13 = r13.getDatabase();	 Catch:{ Exception -> 0x068b }
        r14 = "SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600";
        r15 = new java.lang.Object[r7];	 Catch:{ Exception -> 0x068b }
        r13 = r13.queryFinalized(r14, r15);	 Catch:{ Exception -> 0x068b }
    L_0x0086:
        r14 = r13.next();	 Catch:{ Exception -> 0x068b }
        r15 = 32;
        if (r14 == 0) goto L_0x010a;
    L_0x008e:
        r4 = r13.longValue(r7);	 Catch:{ Exception -> 0x068b }
        r14 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x068b }
        r14.<init>(r1, r6);	 Catch:{ Exception -> 0x068b }
        r7 = 1;
        r6 = r13.intValue(r7);	 Catch:{ Exception -> 0x068b }
        r14.date = r6;	 Catch:{ Exception -> 0x068b }
        r12.put(r4, r14);	 Catch:{ Exception -> 0x068b }
        r6 = (int) r4;	 Catch:{ Exception -> 0x068b }
        r4 = r4 >> r15;
        r5 = (int) r4;	 Catch:{ Exception -> 0x068b }
        if (r6 == 0) goto L_0x00ea;
    L_0x00a6:
        if (r5 != r7) goto L_0x00be;
    L_0x00a8:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x0104;
    L_0x00ac:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x068b }
        r4 = r9.contains(r4);	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x0104;
    L_0x00b6:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x068b }
        r9.add(r4);	 Catch:{ Exception -> 0x068b }
        goto L_0x0104;
    L_0x00be:
        if (r6 <= 0) goto L_0x00d7;
    L_0x00c0:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x068b }
        r5 = 2;
        if (r4 == r5) goto L_0x0104;
    L_0x00c5:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x068b }
        r4 = r3.contains(r4);	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x0104;
    L_0x00cf:
        r4 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x068b }
        r3.add(r4);	 Catch:{ Exception -> 0x068b }
        goto L_0x0104;
    L_0x00d7:
        r4 = -r6;
        r5 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x068b }
        r5 = r9.contains(r5);	 Catch:{ Exception -> 0x068b }
        if (r5 != 0) goto L_0x0104;
    L_0x00e2:
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x068b }
        r9.add(r4);	 Catch:{ Exception -> 0x068b }
        goto L_0x0104;
    L_0x00ea:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x068b }
        if (r4 == 0) goto L_0x00f3;
    L_0x00ee:
        r4 = r1.dialogsType;	 Catch:{ Exception -> 0x068b }
        r6 = 3;
        if (r4 != r6) goto L_0x0104;
    L_0x00f3:
        r4 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x068b }
        r4 = r10.contains(r4);	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x0104;
    L_0x00fd:
        r4 = java.lang.Integer.valueOf(r5);	 Catch:{ Exception -> 0x068b }
        r10.add(r4);	 Catch:{ Exception -> 0x068b }
    L_0x0104:
        r4 = -1;
        r5 = 1;
        r6 = 0;
        r7 = 0;
        goto L_0x0086;
    L_0x010a:
        r13.dispose();	 Catch:{ Exception -> 0x068b }
        r2 = r0.startsWith(r2);	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x0134;
    L_0x0113:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ Exception -> 0x068b }
        r2 = r2.getCurrentUser();	 Catch:{ Exception -> 0x068b }
        r4 = new org.telegram.ui.Adapters.DialogsSearchAdapter$DialogSearchResult;	 Catch:{ Exception -> 0x068b }
        r5 = 0;
        r4.<init>(r1, r5);	 Catch:{ Exception -> 0x068b }
        r5 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4.date = r5;	 Catch:{ Exception -> 0x068b }
        r4.name = r0;	 Catch:{ Exception -> 0x068b }
        r4.object = r2;	 Catch:{ Exception -> 0x068b }
        r0 = r2.id;	 Catch:{ Exception -> 0x068b }
        r5 = (long) r0;	 Catch:{ Exception -> 0x068b }
        r12.put(r5, r4);	 Catch:{ Exception -> 0x068b }
        r0 = 1;
        goto L_0x0135;
    L_0x0134:
        r0 = 0;
    L_0x0135:
        r2 = r3.isEmpty();	 Catch:{ Exception -> 0x068b }
        r4 = ";;;";
        r5 = ",";
        r6 = "@";
        r7 = " ";
        if (r2 != 0) goto L_0x0271;
    L_0x0143:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x068b }
        r2 = r2.getDatabase();	 Catch:{ Exception -> 0x068b }
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x068b }
        r14 = "SELECT data, status, name FROM users WHERE uid IN(%s)";
        r17 = r0;
        r15 = 1;
        r0 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x068b }
        r3 = android.text.TextUtils.join(r5, r3);	 Catch:{ Exception -> 0x068b }
        r15 = 0;
        r0[r15] = r3;	 Catch:{ Exception -> 0x068b }
        r0 = java.lang.String.format(r13, r14, r0);	 Catch:{ Exception -> 0x068b }
        r3 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x068b }
        r0 = r2.queryFinalized(r0, r3);	 Catch:{ Exception -> 0x068b }
    L_0x0167:
        r2 = r0.next();	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x026b;
    L_0x016d:
        r2 = 2;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x068b }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x068b }
        r13 = r3.equals(r2);	 Catch:{ Exception -> 0x068b }
        if (r13 == 0) goto L_0x0181;
    L_0x0180:
        r2 = 0;
    L_0x0181:
        r13 = r3.lastIndexOf(r4);	 Catch:{ Exception -> 0x068b }
        r14 = -1;
        if (r13 == r14) goto L_0x018f;
    L_0x0188:
        r13 = r13 + 3;
        r13 = r3.substring(r13);	 Catch:{ Exception -> 0x068b }
        goto L_0x0190;
    L_0x018f:
        r13 = 0;
    L_0x0190:
        r15 = r8.length;	 Catch:{ Exception -> 0x068b }
        r14 = 0;
        r20 = 0;
    L_0x0194:
        if (r14 >= r15) goto L_0x0265;
    L_0x0196:
        r21 = r15;
        r15 = r8[r14];	 Catch:{ Exception -> 0x068b }
        r22 = r3.startsWith(r15);	 Catch:{ Exception -> 0x068b }
        if (r22 != 0) goto L_0x01e2;
    L_0x01a0:
        r22 = r14;
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r14.<init>();	 Catch:{ Exception -> 0x068b }
        r14.append(r7);	 Catch:{ Exception -> 0x068b }
        r14.append(r15);	 Catch:{ Exception -> 0x068b }
        r14 = r14.toString();	 Catch:{ Exception -> 0x068b }
        r14 = r3.contains(r14);	 Catch:{ Exception -> 0x068b }
        if (r14 != 0) goto L_0x01e4;
    L_0x01b7:
        if (r2 == 0) goto L_0x01d5;
    L_0x01b9:
        r14 = r2.startsWith(r15);	 Catch:{ Exception -> 0x068b }
        if (r14 != 0) goto L_0x01e4;
    L_0x01bf:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r14.<init>();	 Catch:{ Exception -> 0x068b }
        r14.append(r7);	 Catch:{ Exception -> 0x068b }
        r14.append(r15);	 Catch:{ Exception -> 0x068b }
        r14 = r14.toString();	 Catch:{ Exception -> 0x068b }
        r14 = r2.contains(r14);	 Catch:{ Exception -> 0x068b }
        if (r14 == 0) goto L_0x01d5;
    L_0x01d4:
        goto L_0x01e4;
    L_0x01d5:
        if (r13 == 0) goto L_0x01df;
    L_0x01d7:
        r14 = r13.startsWith(r15);	 Catch:{ Exception -> 0x068b }
        if (r14 == 0) goto L_0x01df;
    L_0x01dd:
        r14 = 2;
        goto L_0x01e5;
    L_0x01df:
        r14 = r20;
        goto L_0x01e5;
    L_0x01e2:
        r22 = r14;
    L_0x01e4:
        r14 = 1;
    L_0x01e5:
        if (r14 == 0) goto L_0x0252;
    L_0x01e7:
        r2 = 0;
        r3 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x068b }
        if (r3 == 0) goto L_0x0265;
    L_0x01ee:
        r13 = r3.readInt32(r2);	 Catch:{ Exception -> 0x068b }
        r13 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r3, r13, r2);	 Catch:{ Exception -> 0x068b }
        r3.reuse();	 Catch:{ Exception -> 0x068b }
        r2 = r13.id;	 Catch:{ Exception -> 0x068b }
        r2 = (long) r2;	 Catch:{ Exception -> 0x068b }
        r2 = r12.get(r2);	 Catch:{ Exception -> 0x068b }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x068b }
        r3 = r13.status;	 Catch:{ Exception -> 0x068b }
        if (r3 == 0) goto L_0x0216;
    L_0x0206:
        r3 = r13.status;	 Catch:{ Exception -> 0x068b }
        r19 = r11;
        r22 = r14;
        r14 = 1;
        r11 = r0.intValue(r14);	 Catch:{ Exception -> 0x068b }
        r14 = r22;
        r3.expires = r11;	 Catch:{ Exception -> 0x068b }
        goto L_0x0218;
    L_0x0216:
        r19 = r11;
    L_0x0218:
        r3 = 1;
        if (r14 != r3) goto L_0x0226;
    L_0x021b:
        r3 = r13.first_name;	 Catch:{ Exception -> 0x068b }
        r14 = r13.last_name;	 Catch:{ Exception -> 0x068b }
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r14, r15);	 Catch:{ Exception -> 0x068b }
        r2.name = r3;	 Catch:{ Exception -> 0x068b }
        goto L_0x024d;
    L_0x0226:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r3.<init>();	 Catch:{ Exception -> 0x068b }
        r3.append(r6);	 Catch:{ Exception -> 0x068b }
        r14 = r13.username;	 Catch:{ Exception -> 0x068b }
        r3.append(r14);	 Catch:{ Exception -> 0x068b }
        r3 = r3.toString();	 Catch:{ Exception -> 0x068b }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r14.<init>();	 Catch:{ Exception -> 0x068b }
        r14.append(r6);	 Catch:{ Exception -> 0x068b }
        r14.append(r15);	 Catch:{ Exception -> 0x068b }
        r14 = r14.toString();	 Catch:{ Exception -> 0x068b }
        r15 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r15, r14);	 Catch:{ Exception -> 0x068b }
        r2.name = r3;	 Catch:{ Exception -> 0x068b }
    L_0x024d:
        r2.object = r13;	 Catch:{ Exception -> 0x068b }
        r17 = r17 + 1;
        goto L_0x0267;
    L_0x0252:
        r20 = r2;
        r19 = r11;
        r2 = r22;
        r2 = r2 + 1;
        r15 = r21;
        r23 = r14;
        r14 = r2;
        r2 = r20;
        r20 = r23;
        goto L_0x0194;
    L_0x0265:
        r19 = r11;
    L_0x0267:
        r11 = r19;
        goto L_0x0167;
    L_0x026b:
        r19 = r11;
        r0.dispose();	 Catch:{ Exception -> 0x068b }
        goto L_0x0275;
    L_0x0271:
        r17 = r0;
        r19 = r11;
    L_0x0275:
        r0 = r9.isEmpty();	 Catch:{ Exception -> 0x068b }
        if (r0 != 0) goto L_0x0344;
    L_0x027b:
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x068b }
        r0 = r0.getDatabase();	 Catch:{ Exception -> 0x068b }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x068b }
        r3 = "SELECT data, name FROM chats WHERE uid IN(%s)";
        r11 = 1;
        r13 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x068b }
        r9 = android.text.TextUtils.join(r5, r9);	 Catch:{ Exception -> 0x068b }
        r11 = 0;
        r13[r11] = r9;	 Catch:{ Exception -> 0x068b }
        r2 = java.lang.String.format(r2, r3, r13);	 Catch:{ Exception -> 0x068b }
        r3 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x068b }
        r0 = r0.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x068b }
    L_0x029d:
        r2 = r0.next();	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x0341;
    L_0x02a3:
        r2 = 1;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x068b }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x068b }
        r9 = r3.equals(r2);	 Catch:{ Exception -> 0x068b }
        if (r9 == 0) goto L_0x02b7;
    L_0x02b6:
        r2 = 0;
    L_0x02b7:
        r9 = r8.length;	 Catch:{ Exception -> 0x068b }
        r11 = 0;
    L_0x02b9:
        if (r11 >= r9) goto L_0x029d;
    L_0x02bb:
        r13 = r8[r11];	 Catch:{ Exception -> 0x068b }
        r15 = r3.startsWith(r13);	 Catch:{ Exception -> 0x068b }
        if (r15 != 0) goto L_0x02f9;
    L_0x02c3:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r15.<init>();	 Catch:{ Exception -> 0x068b }
        r15.append(r7);	 Catch:{ Exception -> 0x068b }
        r15.append(r13);	 Catch:{ Exception -> 0x068b }
        r15 = r15.toString();	 Catch:{ Exception -> 0x068b }
        r15 = r3.contains(r15);	 Catch:{ Exception -> 0x068b }
        if (r15 != 0) goto L_0x02f9;
    L_0x02d8:
        if (r2 == 0) goto L_0x02f6;
    L_0x02da:
        r15 = r2.startsWith(r13);	 Catch:{ Exception -> 0x068b }
        if (r15 != 0) goto L_0x02f9;
    L_0x02e0:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r15.<init>();	 Catch:{ Exception -> 0x068b }
        r15.append(r7);	 Catch:{ Exception -> 0x068b }
        r15.append(r13);	 Catch:{ Exception -> 0x068b }
        r15 = r15.toString();	 Catch:{ Exception -> 0x068b }
        r15 = r2.contains(r15);	 Catch:{ Exception -> 0x068b }
        if (r15 == 0) goto L_0x02f6;
    L_0x02f5:
        goto L_0x02f9;
    L_0x02f6:
        r11 = r11 + 1;
        goto L_0x02b9;
    L_0x02f9:
        r2 = 0;
        r3 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x068b }
        if (r3 == 0) goto L_0x029d;
    L_0x0300:
        r9 = r3.readInt32(r2);	 Catch:{ Exception -> 0x068b }
        r9 = org.telegram.tgnet.TLRPC.Chat.TLdeserialize(r3, r9, r2);	 Catch:{ Exception -> 0x068b }
        r3.reuse();	 Catch:{ Exception -> 0x068b }
        if (r9 == 0) goto L_0x029d;
    L_0x030d:
        r2 = r9.deactivated;	 Catch:{ Exception -> 0x068b }
        if (r2 != 0) goto L_0x029d;
    L_0x0311:
        r2 = org.telegram.messenger.ChatObject.isChannel(r9);	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x031d;
    L_0x0317:
        r2 = org.telegram.messenger.ChatObject.isNotInChat(r9);	 Catch:{ Exception -> 0x068b }
        if (r2 != 0) goto L_0x029d;
    L_0x031d:
        r2 = r9.id;	 Catch:{ Exception -> 0x068b }
        if (r2 <= 0) goto L_0x0326;
    L_0x0321:
        r2 = r9.id;	 Catch:{ Exception -> 0x068b }
        r2 = -r2;
        r2 = (long) r2;	 Catch:{ Exception -> 0x068b }
        goto L_0x032c;
    L_0x0326:
        r2 = r9.id;	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.AndroidUtilities.makeBroadcastId(r2);	 Catch:{ Exception -> 0x068b }
    L_0x032c:
        r2 = r12.get(r2);	 Catch:{ Exception -> 0x068b }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x068b }
        r3 = r9.title;	 Catch:{ Exception -> 0x068b }
        r11 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r11, r13);	 Catch:{ Exception -> 0x068b }
        r2.name = r3;	 Catch:{ Exception -> 0x068b }
        r2.object = r9;	 Catch:{ Exception -> 0x068b }
        r17 = r17 + 1;
        goto L_0x029d;
    L_0x0341:
        r0.dispose();	 Catch:{ Exception -> 0x068b }
    L_0x0344:
        r0 = r10.isEmpty();	 Catch:{ Exception -> 0x068b }
        if (r0 != 0) goto L_0x051d;
    L_0x034a:
        r0 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r0);	 Catch:{ Exception -> 0x068b }
        r0 = r0.getDatabase();	 Catch:{ Exception -> 0x068b }
        r2 = java.util.Locale.US;	 Catch:{ Exception -> 0x068b }
        r3 = "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)";
        r9 = 1;
        r11 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x068b }
        r5 = android.text.TextUtils.join(r5, r10);	 Catch:{ Exception -> 0x068b }
        r9 = 0;
        r11[r9] = r5;	 Catch:{ Exception -> 0x068b }
        r2 = java.lang.String.format(r2, r3, r11);	 Catch:{ Exception -> 0x068b }
        r3 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x068b }
        r0 = r0.queryFinalized(r2, r3);	 Catch:{ Exception -> 0x068b }
    L_0x036c:
        r2 = r0.next();	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x0517;
    L_0x0372:
        r2 = 1;
        r3 = r0.stringValue(r2);	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x068b }
        r2 = r2.getTranslitString(r3);	 Catch:{ Exception -> 0x068b }
        r5 = r3.equals(r2);	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x0386;
    L_0x0385:
        r2 = 0;
    L_0x0386:
        r5 = r3.lastIndexOf(r4);	 Catch:{ Exception -> 0x068b }
        r9 = -1;
        if (r5 == r9) goto L_0x0394;
    L_0x038d:
        r5 = r5 + 2;
        r5 = r3.substring(r5);	 Catch:{ Exception -> 0x068b }
        goto L_0x0395;
    L_0x0394:
        r5 = 0;
    L_0x0395:
        r9 = 0;
        r10 = 0;
    L_0x0397:
        r11 = r8.length;	 Catch:{ Exception -> 0x068b }
        if (r9 >= r11) goto L_0x0511;
    L_0x039a:
        r11 = r8[r9];	 Catch:{ Exception -> 0x068b }
        r13 = r3.startsWith(r11);	 Catch:{ Exception -> 0x068b }
        if (r13 != 0) goto L_0x03df;
    L_0x03a2:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r13.<init>();	 Catch:{ Exception -> 0x068b }
        r13.append(r7);	 Catch:{ Exception -> 0x068b }
        r13.append(r11);	 Catch:{ Exception -> 0x068b }
        r13 = r13.toString();	 Catch:{ Exception -> 0x068b }
        r13 = r3.contains(r13);	 Catch:{ Exception -> 0x068b }
        if (r13 != 0) goto L_0x03df;
    L_0x03b7:
        if (r2 == 0) goto L_0x03d5;
    L_0x03b9:
        r13 = r2.startsWith(r11);	 Catch:{ Exception -> 0x068b }
        if (r13 != 0) goto L_0x03df;
    L_0x03bf:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r13.<init>();	 Catch:{ Exception -> 0x068b }
        r13.append(r7);	 Catch:{ Exception -> 0x068b }
        r13.append(r11);	 Catch:{ Exception -> 0x068b }
        r13 = r13.toString();	 Catch:{ Exception -> 0x068b }
        r13 = r2.contains(r13);	 Catch:{ Exception -> 0x068b }
        if (r13 == 0) goto L_0x03d5;
    L_0x03d4:
        goto L_0x03df;
    L_0x03d5:
        if (r5 == 0) goto L_0x03e0;
    L_0x03d7:
        r13 = r5.startsWith(r11);	 Catch:{ Exception -> 0x068b }
        if (r13 == 0) goto L_0x03e0;
    L_0x03dd:
        r10 = 2;
        goto L_0x03e0;
    L_0x03df:
        r10 = 1;
    L_0x03e0:
        if (r10 == 0) goto L_0x050b;
    L_0x03e2:
        r13 = 0;
        r2 = r0.byteBufferValue(r13);	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x03f5;
    L_0x03e9:
        r3 = r2.readInt32(r13);	 Catch:{ Exception -> 0x068b }
        r3 = org.telegram.tgnet.TLRPC.EncryptedChat.TLdeserialize(r2, r3, r13);	 Catch:{ Exception -> 0x068b }
        r2.reuse();	 Catch:{ Exception -> 0x068b }
        goto L_0x03f6;
    L_0x03f5:
        r3 = 0;
    L_0x03f6:
        r2 = 6;
        r2 = r0.byteBufferValue(r2);	 Catch:{ Exception -> 0x068b }
        if (r2 == 0) goto L_0x040a;
    L_0x03fd:
        r5 = 0;
        r9 = r2.readInt32(r5);	 Catch:{ Exception -> 0x068b }
        r9 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r2, r9, r5);	 Catch:{ Exception -> 0x068b }
        r2.reuse();	 Catch:{ Exception -> 0x068b }
        goto L_0x040b;
    L_0x040a:
        r9 = 0;
    L_0x040b:
        if (r3 == 0) goto L_0x0511;
    L_0x040d:
        if (r9 == 0) goto L_0x0511;
    L_0x040f:
        r2 = r3.id;	 Catch:{ Exception -> 0x068b }
        r14 = (long) r2;	 Catch:{ Exception -> 0x068b }
        r13 = 32;
        r14 = r14 << r13;
        r2 = r12.get(r14);	 Catch:{ Exception -> 0x068b }
        r2 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r2;	 Catch:{ Exception -> 0x068b }
        r5 = 2;
        r14 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.user_id = r14;	 Catch:{ Exception -> 0x068b }
        r5 = 3;
        r14 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.a_or_b = r14;	 Catch:{ Exception -> 0x068b }
        r5 = 4;
        r5 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.auth_key = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 5;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.ttl = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 8;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.layer = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 9;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.seq_in = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 10;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.seq_out = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 11;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r14 = r5 >> 16;
        r14 = (short) r14;	 Catch:{ Exception -> 0x068b }
        r3.key_use_count_in = r14;	 Catch:{ Exception -> 0x068b }
        r5 = (short) r5;	 Catch:{ Exception -> 0x068b }
        r3.key_use_count_out = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 12;
        r14 = r0.longValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.exchange_id = r14;	 Catch:{ Exception -> 0x068b }
        r5 = 13;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.key_create_date = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 14;
        r14 = r0.longValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.future_key_fingerprint = r14;	 Catch:{ Exception -> 0x068b }
        r5 = 15;
        r5 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.future_auth_key = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 16;
        r5 = r0.byteArrayValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.key_hash = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 17;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.in_seq_no = r5;	 Catch:{ Exception -> 0x068b }
        r5 = 18;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x0497;
    L_0x0495:
        r3.admin_id = r5;	 Catch:{ Exception -> 0x068b }
    L_0x0497:
        r5 = 19;
        r5 = r0.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r3.mtproto_seq = r5;	 Catch:{ Exception -> 0x068b }
        r5 = r9.status;	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x04ac;
    L_0x04a3:
        r5 = r9.status;	 Catch:{ Exception -> 0x068b }
        r14 = 7;
        r14 = r0.intValue(r14);	 Catch:{ Exception -> 0x068b }
        r5.expires = r14;	 Catch:{ Exception -> 0x068b }
    L_0x04ac:
        r5 = 1;
        if (r10 != r5) goto L_0x04da;
    L_0x04af:
        r5 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x068b }
        r10 = r9.first_name;	 Catch:{ Exception -> 0x068b }
        r11 = r9.last_name;	 Catch:{ Exception -> 0x068b }
        r10 = org.telegram.messenger.ContactsController.formatName(r10, r11);	 Catch:{ Exception -> 0x068b }
        r5.<init>(r10);	 Catch:{ Exception -> 0x068b }
        r2.name = r5;	 Catch:{ Exception -> 0x068b }
        r5 = r2.name;	 Catch:{ Exception -> 0x068b }
        r5 = (android.text.SpannableStringBuilder) r5;	 Catch:{ Exception -> 0x068b }
        r10 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x068b }
        r11 = "chats_secretName";
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r11);	 Catch:{ Exception -> 0x068b }
        r10.<init>(r11);	 Catch:{ Exception -> 0x068b }
        r11 = r2.name;	 Catch:{ Exception -> 0x068b }
        r11 = r11.length();	 Catch:{ Exception -> 0x068b }
        r15 = 33;
        r13 = 0;
        r5.setSpan(r10, r13, r11, r15);	 Catch:{ Exception -> 0x068b }
        goto L_0x0501;
    L_0x04da:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r5.<init>();	 Catch:{ Exception -> 0x068b }
        r5.append(r6);	 Catch:{ Exception -> 0x068b }
        r10 = r9.username;	 Catch:{ Exception -> 0x068b }
        r5.append(r10);	 Catch:{ Exception -> 0x068b }
        r5 = r5.toString();	 Catch:{ Exception -> 0x068b }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r10.<init>();	 Catch:{ Exception -> 0x068b }
        r10.append(r6);	 Catch:{ Exception -> 0x068b }
        r10.append(r11);	 Catch:{ Exception -> 0x068b }
        r10 = r10.toString();	 Catch:{ Exception -> 0x068b }
        r11 = 0;
        r5 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r11, r10);	 Catch:{ Exception -> 0x068b }
        r2.name = r5;	 Catch:{ Exception -> 0x068b }
    L_0x0501:
        r2.object = r3;	 Catch:{ Exception -> 0x068b }
        r11 = r19;
        r11.add(r9);	 Catch:{ Exception -> 0x068b }
        r17 = r17 + 1;
        goto L_0x0513;
    L_0x050b:
        r11 = r19;
        r9 = r9 + 1;
        goto L_0x0397;
    L_0x0511:
        r11 = r19;
    L_0x0513:
        r19 = r11;
        goto L_0x036c;
    L_0x0517:
        r11 = r19;
        r0.dispose();	 Catch:{ Exception -> 0x068b }
        goto L_0x051f;
    L_0x051d:
        r11 = r19;
    L_0x051f:
        r0 = r17;
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r2.<init>(r0);	 Catch:{ Exception -> 0x068b }
        r0 = 0;
    L_0x0527:
        r3 = r12.size();	 Catch:{ Exception -> 0x068b }
        if (r0 >= r3) goto L_0x0541;
    L_0x052d:
        r3 = r12.valueAt(r0);	 Catch:{ Exception -> 0x068b }
        r3 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r3;	 Catch:{ Exception -> 0x068b }
        r5 = r3.object;	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x053e;
    L_0x0537:
        r5 = r3.name;	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x053e;
    L_0x053b:
        r2.add(r3);	 Catch:{ Exception -> 0x068b }
    L_0x053e:
        r0 = r0 + 1;
        goto L_0x0527;
    L_0x0541:
        r0 = org.telegram.ui.Adapters.-$$Lambda$DialogsSearchAdapter$FVguPxj8QpbjyrNjyvgW9r4iI6c.INSTANCE;	 Catch:{ Exception -> 0x068b }
        java.util.Collections.sort(r2, r0);	 Catch:{ Exception -> 0x068b }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r0.<init>();	 Catch:{ Exception -> 0x068b }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x068b }
        r3.<init>();	 Catch:{ Exception -> 0x068b }
        r5 = 0;
    L_0x0551:
        r9 = r2.size();	 Catch:{ Exception -> 0x068b }
        if (r5 >= r9) goto L_0x056a;
    L_0x0557:
        r9 = r2.get(r5);	 Catch:{ Exception -> 0x068b }
        r9 = (org.telegram.ui.Adapters.DialogsSearchAdapter.DialogSearchResult) r9;	 Catch:{ Exception -> 0x068b }
        r10 = r9.object;	 Catch:{ Exception -> 0x068b }
        r0.add(r10);	 Catch:{ Exception -> 0x068b }
        r9 = r9.name;	 Catch:{ Exception -> 0x068b }
        r3.add(r9);	 Catch:{ Exception -> 0x068b }
        r5 = r5 + 1;
        goto L_0x0551;
    L_0x056a:
        r2 = r1.dialogsType;	 Catch:{ Exception -> 0x068b }
        r5 = 2;
        if (r2 == r5) goto L_0x0685;
    L_0x056f:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x068b }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x068b }
        r2 = r2.getDatabase();	 Catch:{ Exception -> 0x068b }
        r5 = "SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid";
        r9 = 0;
        r10 = new java.lang.Object[r9];	 Catch:{ Exception -> 0x068b }
        r2 = r2.queryFinalized(r5, r10);	 Catch:{ Exception -> 0x068b }
    L_0x0582:
        r5 = r2.next();	 Catch:{ Exception -> 0x068b }
        if (r5 == 0) goto L_0x0682;
    L_0x0588:
        r5 = 3;
        r9 = r2.intValue(r5);	 Catch:{ Exception -> 0x068b }
        r9 = (long) r9;	 Catch:{ Exception -> 0x068b }
        r9 = r12.indexOfKey(r9);	 Catch:{ Exception -> 0x068b }
        if (r9 < 0) goto L_0x0595;
    L_0x0594:
        goto L_0x0582;
    L_0x0595:
        r9 = 2;
        r10 = r2.stringValue(r9);	 Catch:{ Exception -> 0x068b }
        r13 = org.telegram.messenger.LocaleController.getInstance();	 Catch:{ Exception -> 0x068b }
        r13 = r13.getTranslitString(r10);	 Catch:{ Exception -> 0x068b }
        r15 = r10.equals(r13);	 Catch:{ Exception -> 0x068b }
        if (r15 == 0) goto L_0x05a9;
    L_0x05a8:
        r13 = 0;
    L_0x05a9:
        r15 = r10.lastIndexOf(r4);	 Catch:{ Exception -> 0x068b }
        r5 = -1;
        if (r15 == r5) goto L_0x05b7;
    L_0x05b0:
        r15 = r15 + 3;
        r15 = r10.substring(r15);	 Catch:{ Exception -> 0x068b }
        goto L_0x05b8;
    L_0x05b7:
        r15 = 0;
    L_0x05b8:
        r5 = r8.length;	 Catch:{ Exception -> 0x068b }
        r9 = 0;
        r17 = 0;
    L_0x05bc:
        if (r9 >= r5) goto L_0x0678;
    L_0x05be:
        r14 = r8[r9];	 Catch:{ Exception -> 0x068b }
        r18 = r10.startsWith(r14);	 Catch:{ Exception -> 0x068b }
        if (r18 != 0) goto L_0x0608;
    L_0x05c6:
        r18 = r4;
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r4.<init>();	 Catch:{ Exception -> 0x068b }
        r4.append(r7);	 Catch:{ Exception -> 0x068b }
        r4.append(r14);	 Catch:{ Exception -> 0x068b }
        r4 = r4.toString();	 Catch:{ Exception -> 0x068b }
        r4 = r10.contains(r4);	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x060a;
    L_0x05dd:
        if (r13 == 0) goto L_0x05fb;
    L_0x05df:
        r4 = r13.startsWith(r14);	 Catch:{ Exception -> 0x068b }
        if (r4 != 0) goto L_0x060a;
    L_0x05e5:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r4.<init>();	 Catch:{ Exception -> 0x068b }
        r4.append(r7);	 Catch:{ Exception -> 0x068b }
        r4.append(r14);	 Catch:{ Exception -> 0x068b }
        r4 = r4.toString();	 Catch:{ Exception -> 0x068b }
        r4 = r13.contains(r4);	 Catch:{ Exception -> 0x068b }
        if (r4 == 0) goto L_0x05fb;
    L_0x05fa:
        goto L_0x060a;
    L_0x05fb:
        if (r15 == 0) goto L_0x0605;
    L_0x05fd:
        r4 = r15.startsWith(r14);	 Catch:{ Exception -> 0x068b }
        if (r4 == 0) goto L_0x0605;
    L_0x0603:
        r4 = 2;
        goto L_0x060b;
    L_0x0605:
        r4 = r17;
        goto L_0x060b;
    L_0x0608:
        r18 = r4;
    L_0x060a:
        r4 = 1;
    L_0x060b:
        if (r4 == 0) goto L_0x0668;
    L_0x060d:
        r5 = 0;
        r9 = r2.byteBufferValue(r5);	 Catch:{ Exception -> 0x068b }
        if (r9 == 0) goto L_0x067b;
    L_0x0614:
        r10 = r9.readInt32(r5);	 Catch:{ Exception -> 0x068b }
        r10 = org.telegram.tgnet.TLRPC.User.TLdeserialize(r9, r10, r5);	 Catch:{ Exception -> 0x068b }
        r9.reuse();	 Catch:{ Exception -> 0x068b }
        r9 = r10.status;	 Catch:{ Exception -> 0x068b }
        if (r9 == 0) goto L_0x062c;
    L_0x0623:
        r9 = r10.status;	 Catch:{ Exception -> 0x068b }
        r13 = 1;
        r15 = r2.intValue(r13);	 Catch:{ Exception -> 0x068b }
        r9.expires = r15;	 Catch:{ Exception -> 0x068b }
    L_0x062c:
        r9 = 1;
        if (r4 != r9) goto L_0x063c;
    L_0x062f:
        r4 = r10.first_name;	 Catch:{ Exception -> 0x068b }
        r13 = r10.last_name;	 Catch:{ Exception -> 0x068b }
        r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r13, r14);	 Catch:{ Exception -> 0x068b }
        r3.add(r4);	 Catch:{ Exception -> 0x068b }
        r14 = 0;
        goto L_0x0664;
    L_0x063c:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r4.<init>();	 Catch:{ Exception -> 0x068b }
        r4.append(r6);	 Catch:{ Exception -> 0x068b }
        r13 = r10.username;	 Catch:{ Exception -> 0x068b }
        r4.append(r13);	 Catch:{ Exception -> 0x068b }
        r4 = r4.toString();	 Catch:{ Exception -> 0x068b }
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x068b }
        r13.<init>();	 Catch:{ Exception -> 0x068b }
        r13.append(r6);	 Catch:{ Exception -> 0x068b }
        r13.append(r14);	 Catch:{ Exception -> 0x068b }
        r13 = r13.toString();	 Catch:{ Exception -> 0x068b }
        r14 = 0;
        r4 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r14, r13);	 Catch:{ Exception -> 0x068b }
        r3.add(r4);	 Catch:{ Exception -> 0x068b }
    L_0x0664:
        r0.add(r10);	 Catch:{ Exception -> 0x068b }
        goto L_0x067c;
    L_0x0668:
        r17 = r5;
        r5 = 0;
        r14 = 0;
        r16 = 1;
        r9 = r9 + 1;
        r5 = r17;
        r17 = r4;
        r4 = r18;
        goto L_0x05bc;
    L_0x0678:
        r18 = r4;
        r5 = 0;
    L_0x067b:
        r14 = 0;
    L_0x067c:
        r16 = 1;
        r4 = r18;
        goto L_0x0582;
    L_0x0682:
        r2.dispose();	 Catch:{ Exception -> 0x068b }
    L_0x0685:
        r2 = r26;
        r1.updateSearchResults(r0, r3, r11, r2);	 Catch:{ Exception -> 0x068b }
        goto L_0x068f;
    L_0x068b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x068f:
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
            for (i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = (TLObject) arrayList.get(i);
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
        }
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
            if (TextUtils.isEmpty(str)) {
                this.searchAdapterHelper.unloadRecentHashtags();
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchResultHashtags.clear();
                this.searchAdapterHelper.mergeResults(null);
                if (this.needMessagesSearch != 2) {
                    this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, 0, 0);
                }
                searchMessagesInternal(null);
                notifyDataSetChanged();
            } else {
                if (this.needMessagesSearch != 2 && str.startsWith("#") && str.length() == 1) {
                    this.messagesSearchEndReached = true;
                    DialogsSearchAdapterDelegate dialogsSearchAdapterDelegate;
                    if (this.searchAdapterHelper.loadRecentHashtags()) {
                        this.searchResultMessages.clear();
                        this.searchResultHashtags.clear();
                        ArrayList hashtags = this.searchAdapterHelper.getHashtags();
                        for (int i = 0; i < hashtags.size(); i++) {
                            this.searchResultHashtags.add(((HashtagObject) hashtags.get(i)).hashtag);
                        }
                        dialogsSearchAdapterDelegate = this.delegate;
                        if (dialogsSearchAdapterDelegate != null) {
                            dialogsSearchAdapterDelegate.searchStateChanged(false);
                        }
                    } else {
                        dialogsSearchAdapterDelegate = this.delegate;
                        if (dialogsSearchAdapterDelegate != null) {
                            dialogsSearchAdapterDelegate.searchStateChanged(true);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    this.searchResultHashtags.clear();
                    notifyDataSetChanged();
                }
                int i2 = this.lastSearchId + 1;
                this.lastSearchId = i2;
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                -$$Lambda$DialogsSearchAdapter$2OrdD5nR-nF-l2wgPqqTnpM3YF8 -__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8 = new -$$Lambda$DialogsSearchAdapter$2OrdD5nR-nF-l2wgPqqTnpM3YF8(this, str, i2);
                this.searchRunnable = -__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8;
                dispatchQueue.postRunnable(-__lambda_dialogssearchadapter_2ordd5nr-nf-l2wgpqqtnpm3yf8, 300);
            }
        }
    }

    public /* synthetic */ void lambda$searchDialogs$11$DialogsSearchAdapter(String str, int i) {
        this.searchRunnable = null;
        searchDialogsInternal(str, i);
        -$$Lambda$DialogsSearchAdapter$XIN3XyLDARAzDLUaC4eBXOVmKVM -__lambda_dialogssearchadapter_xin3xyldarazdluac4ebxovmkvm = new -$$Lambda$DialogsSearchAdapter$XIN3XyLDARAzDLUaC4eBXOVmKVM(this, str);
        this.searchRunnable2 = -__lambda_dialogssearchadapter_xin3xyldarazdluac4ebxovmkvm;
        AndroidUtilities.runOnUIThread(-__lambda_dialogssearchadapter_xin3xyldarazdluac4ebxovmkvm);
    }

    public /* synthetic */ void lambda$null$10$DialogsSearchAdapter(String str) {
        this.searchRunnable2 = null;
        if (this.needMessagesSearch != 2) {
            this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, 0, 0);
        }
        searchMessagesInternal(str);
    }

    public int getItemCount() {
        int i;
        int size;
        if (isRecentSearchDisplayed()) {
            i = 0;
            size = !this.recentSearchObjects.isEmpty() ? this.recentSearchObjects.size() + 1 : 0;
            if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                i = 2;
            }
            return size + i;
        } else if (!this.searchResultHashtags.isEmpty()) {
            return this.searchResultHashtags.size() + 1;
        } else {
            size = this.searchResult.size();
            i = this.searchAdapterHelper.getLocalServerSearch().size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            int size3 = this.searchResultMessages.size();
            size += i;
            if (size2 != 0) {
                size += size2 + 1;
            }
            if (size3 != 0) {
                size += (size3 + 1) + (this.messagesSearchEndReached ^ 1);
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
    public java.lang.Object getItem(int r9) {
        /*
        r8 = this;
        r0 = r8.isRecentSearchDisplayed();
        r1 = 0;
        r2 = 0;
        if (r0 == 0) goto L_0x0064;
    L_0x0008:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);
        r0 = r0.hints;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0017;
    L_0x0016:
        r2 = 2;
    L_0x0017:
        if (r9 <= r2) goto L_0x0063;
    L_0x0019:
        r9 = r9 + -1;
        r9 = r9 - r2;
        r0 = r8.recentSearchObjects;
        r0 = r0.size();
        if (r9 >= r0) goto L_0x0063;
    L_0x0024:
        r0 = r8.recentSearchObjects;
        r9 = r0.get(r9);
        r9 = (org.telegram.ui.Adapters.DialogsSearchAdapter.RecentSearchObject) r9;
        r9 = r9.object;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.User;
        if (r0 == 0) goto L_0x0048;
    L_0x0032:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r9;
        r1 = (org.telegram.tgnet.TLRPC.User) r1;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getUser(r1);
        if (r0 == 0) goto L_0x0062;
    L_0x0047:
        goto L_0x0061;
    L_0x0048:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r0 == 0) goto L_0x0062;
    L_0x004c:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r9;
        r1 = (org.telegram.tgnet.TLRPC.Chat) r1;
        r1 = r1.id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        if (r0 == 0) goto L_0x0062;
    L_0x0061:
        r9 = r0;
    L_0x0062:
        return r9;
    L_0x0063:
        return r1;
    L_0x0064:
        r0 = r8.searchResultHashtags;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0078;
    L_0x006c:
        if (r9 <= 0) goto L_0x0077;
    L_0x006e:
        r0 = r8.searchResultHashtags;
        r9 = r9 + -1;
        r9 = r0.get(r9);
        return r9;
    L_0x0077:
        return r1;
    L_0x0078:
        r0 = r8.searchAdapterHelper;
        r0 = r0.getGlobalSearch();
        r3 = r8.searchAdapterHelper;
        r3 = r3.getLocalServerSearch();
        r4 = r8.searchResult;
        r4 = r4.size();
        r5 = r3.size();
        r6 = r0.isEmpty();
        if (r6 == 0) goto L_0x0096;
    L_0x0094:
        r6 = 0;
        goto L_0x009c;
    L_0x0096:
        r6 = r0.size();
        r6 = r6 + 1;
    L_0x009c:
        r7 = r8.searchResultMessages;
        r7 = r7.isEmpty();
        if (r7 == 0) goto L_0x00a5;
    L_0x00a4:
        goto L_0x00ad;
    L_0x00a5:
        r2 = r8.searchResultMessages;
        r2 = r2.size();
        r2 = r2 + 1;
    L_0x00ad:
        if (r9 < 0) goto L_0x00b8;
    L_0x00af:
        if (r9 >= r4) goto L_0x00b8;
    L_0x00b1:
        r0 = r8.searchResult;
        r9 = r0.get(r9);
        return r9;
    L_0x00b8:
        if (r9 < r4) goto L_0x00c4;
    L_0x00ba:
        r7 = r5 + r4;
        if (r9 >= r7) goto L_0x00c4;
    L_0x00be:
        r9 = r9 - r4;
        r9 = r3.get(r9);
        return r9;
    L_0x00c4:
        r3 = r4 + r5;
        if (r9 <= r3) goto L_0x00d6;
    L_0x00c8:
        r3 = r6 + r4;
        r3 = r3 + r5;
        if (r9 >= r3) goto L_0x00d6;
    L_0x00cd:
        r9 = r9 - r4;
        r9 = r9 - r5;
        r9 = r9 + -1;
        r9 = r0.get(r9);
        return r9;
    L_0x00d6:
        r0 = r6 + r4;
        r3 = r0 + r5;
        if (r9 <= r3) goto L_0x00ec;
    L_0x00dc:
        r0 = r0 + r2;
        r0 = r0 + r5;
        if (r9 >= r0) goto L_0x00ec;
    L_0x00e0:
        r0 = r8.searchResultMessages;
        r9 = r9 - r4;
        r9 = r9 - r6;
        r9 = r9 - r5;
        r9 = r9 + -1;
        r9 = r0.get(r9);
        return r9;
    L_0x00ec:
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
        int size3 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
        int size4 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
        if (i >= 0 && i < size) {
            return false;
        }
        if (i >= size && i < size2 + size) {
            return false;
        }
        if (i > size + size2 && i < (size3 + size) + size2) {
            return true;
        }
        size3 += size;
        if (i <= size3 + size2 || i < (size3 + size4) + size2) {
        }
        return false;
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 3) ? false : true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        if (i == 0) {
            view = new ProfileSearchCell(this.mContext);
        } else if (i == 1) {
            view = new GraySectionCell(this.mContext);
        } else if (i == 2) {
            view = new DialogCell(this.mContext, false);
        } else if (i == 3) {
            view = new LoadingCell(this.mContext);
        } else if (i == 4) {
            view = new HashtagSearchCell(this.mContext);
        } else if (i == 5) {
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
        }
        if (i == 5) {
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(86.0f)));
        } else {
            view.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(view);
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

    /* JADX WARNING: Removed duplicated region for block: B:134:0x02dc  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02da  */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
        /*
        r17 = this;
        r1 = r17;
        r0 = r18;
        r2 = r19;
        r3 = r18.getItemViewType();
        r4 = 3;
        r5 = 2;
        r6 = 0;
        r7 = 1;
        if (r3 == 0) goto L_0x00f7;
    L_0x0010:
        if (r3 == r7) goto L_0x006e;
    L_0x0012:
        if (r3 == r5) goto L_0x004d;
    L_0x0014:
        if (r3 == r4) goto L_0x02e2;
    L_0x0016:
        r4 = 4;
        if (r3 == r4) goto L_0x002e;
    L_0x0019:
        r4 = 5;
        if (r3 == r4) goto L_0x001e;
    L_0x001c:
        goto L_0x02e2;
    L_0x001e:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Components.RecyclerListView) r0;
        r0 = r0.getAdapter();
        r0 = (org.telegram.ui.Adapters.DialogsSearchAdapter.CategoryAdapterRecycler) r0;
        r2 = r2 / r5;
        r0.setIndex(r2);
        goto L_0x02e2;
    L_0x002e:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.HashtagSearchCell) r0;
        r3 = r1.searchResultHashtags;
        r4 = r2 + -1;
        r3 = r3.get(r4);
        r3 = (java.lang.CharSequence) r3;
        r0.setText(r3);
        r3 = r1.searchResultHashtags;
        r3 = r3.size();
        if (r2 == r3) goto L_0x0048;
    L_0x0047:
        r6 = 1;
    L_0x0048:
        r0.setNeedDivider(r6);
        goto L_0x02e2;
    L_0x004d:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.DialogCell) r0;
        r3 = r17.getItemCount();
        r3 = r3 - r7;
        if (r2 == r3) goto L_0x0059;
    L_0x0058:
        r6 = 1;
    L_0x0059:
        r0.useSeparator = r6;
        r2 = r1.getItem(r2);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r2.getDialogId();
        r5 = r2.messageOwner;
        r5 = r5.date;
        r0.setDialog(r3, r2, r5);
        goto L_0x02e2;
    L_0x006e:
        r0 = r0.itemView;
        r0 = (org.telegram.ui.Cells.GraySectionCell) r0;
        r3 = r17.isRecentSearchDisplayed();
        if (r3 == 0) goto L_0x00a6;
    L_0x0078:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.DataQuery.getInstance(r3);
        r3 = r3.hints;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0087;
    L_0x0086:
        goto L_0x0088;
    L_0x0087:
        r5 = 0;
    L_0x0088:
        if (r2 >= r5) goto L_0x0098;
    L_0x008a:
        r2 = NUM; // 0x7f0d0266 float:1.874336E38 double:1.053130081E-314;
        r3 = "ChatHints";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x02e2;
    L_0x0098:
        r2 = NUM; // 0x7f0d0820 float:1.8746333E38 double:1.053130805E-314;
        r3 = "Recent";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x02e2;
    L_0x00a6:
        r3 = r1.searchResultHashtags;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x00bc;
    L_0x00ae:
        r2 = NUM; // 0x7f0d04a4 float:1.8744524E38 double:1.0531303645E-314;
        r3 = "Hashtags";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x02e2;
    L_0x00bc:
        r3 = r1.searchAdapterHelper;
        r3 = r3.getGlobalSearch();
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x00e9;
    L_0x00c8:
        r3 = r1.searchResult;
        r3 = r3.size();
        r4 = r1.searchAdapterHelper;
        r4 = r4.getLocalServerSearch();
        r4 = r4.size();
        r3 = r3 + r4;
        if (r2 != r3) goto L_0x00e9;
    L_0x00db:
        r2 = NUM; // 0x7f0d047c float:1.8744443E38 double:1.0531303447E-314;
        r3 = "GlobalSearch";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x02e2;
    L_0x00e9:
        r2 = NUM; // 0x7f0d0891 float:1.8746563E38 double:1.053130861E-314;
        r3 = "SearchMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setText(r2);
        goto L_0x02e2;
    L_0x00f7:
        r0 = r0.itemView;
        r8 = r0;
        r8 = (org.telegram.ui.Cells.ProfileSearchCell) r8;
        r0 = r1.getItem(r2);
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.User;
        if (r3 == 0) goto L_0x010d;
    L_0x0104:
        r0 = (org.telegram.tgnet.TLRPC.User) r0;
        r3 = r0.username;
        r11 = r3;
        r10 = 0;
        r12 = 0;
        r3 = r0;
        goto L_0x015b;
    L_0x010d:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.Chat;
        if (r3 == 0) goto L_0x012d;
    L_0x0111:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r0 = (org.telegram.tgnet.TLRPC.Chat) r0;
        r10 = r0.id;
        r10 = java.lang.Integer.valueOf(r10);
        r3 = r3.getChat(r10);
        if (r3 != 0) goto L_0x0126;
    L_0x0125:
        goto L_0x0127;
    L_0x0126:
        r0 = r3;
    L_0x0127:
        r3 = r0.username;
        r10 = r0;
        r11 = r3;
        r3 = 0;
        goto L_0x015a;
    L_0x012d:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.EncryptedChat;
        if (r3 == 0) goto L_0x0157;
    L_0x0131:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r0 = (org.telegram.tgnet.TLRPC.EncryptedChat) r0;
        r0 = r0.id;
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r3.getEncryptedChat(r0);
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r10 = r0.user_id;
        r10 = java.lang.Integer.valueOf(r10);
        r3 = r3.getUser(r10);
        r12 = r0;
        r10 = 0;
        r11 = 0;
        goto L_0x015b;
    L_0x0157:
        r3 = 0;
        r10 = 0;
        r11 = 0;
    L_0x015a:
        r12 = 0;
    L_0x015b:
        r0 = r17.isRecentSearchDisplayed();
        if (r0 == 0) goto L_0x0172;
    L_0x0161:
        r0 = r17.getItemCount();
        r0 = r0 - r7;
        if (r2 == r0) goto L_0x016a;
    L_0x0168:
        r0 = 1;
        goto L_0x016b;
    L_0x016a:
        r0 = 0;
    L_0x016b:
        r8.useSeparator = r0;
        r0 = 0;
        r9 = 0;
        r13 = 1;
        goto L_0x027b;
    L_0x0172:
        r0 = r1.searchAdapterHelper;
        r0 = r0.getGlobalSearch();
        r13 = r1.searchResult;
        r13 = r13.size();
        r14 = r1.searchAdapterHelper;
        r14 = r14.getLocalServerSearch();
        r14 = r14.size();
        r15 = r0.isEmpty();
        if (r15 == 0) goto L_0x0190;
    L_0x018e:
        r0 = 0;
        goto L_0x0195;
    L_0x0190:
        r0 = r0.size();
        r0 = r0 + r7;
    L_0x0195:
        r15 = r17.getItemCount();
        r15 = r15 - r7;
        if (r2 == r15) goto L_0x01a8;
    L_0x019c:
        r15 = r13 + r14;
        r15 = r15 - r7;
        if (r2 == r15) goto L_0x01a8;
    L_0x01a1:
        r13 = r13 + r0;
        r13 = r13 + r14;
        r13 = r13 - r7;
        if (r2 == r13) goto L_0x01a8;
    L_0x01a6:
        r0 = 1;
        goto L_0x01a9;
    L_0x01a8:
        r0 = 0;
    L_0x01a9:
        r8.useSeparator = r0;
        r0 = r1.searchResult;
        r0 = r0.size();
        r13 = "@";
        if (r2 >= r0) goto L_0x01ea;
    L_0x01b5:
        r0 = r1.searchResultNames;
        r0 = r0.get(r2);
        r0 = (java.lang.CharSequence) r0;
        if (r0 == 0) goto L_0x0279;
    L_0x01bf:
        if (r3 == 0) goto L_0x0279;
    L_0x01c1:
        r2 = r3.username;
        if (r2 == 0) goto L_0x0279;
    L_0x01c5:
        r2 = r2.length();
        if (r2 <= 0) goto L_0x0279;
    L_0x01cb:
        r2 = r0.toString();
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r13);
        r13 = r3.username;
        r11.append(r13);
        r11 = r11.toString();
        r2 = r2.startsWith(r11);
        if (r2 == 0) goto L_0x0279;
    L_0x01e6:
        r9 = r0;
    L_0x01e7:
        r0 = 0;
        goto L_0x027a;
    L_0x01ea:
        r0 = r1.searchAdapterHelper;
        r0 = r0.getLastFoundUsername();
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x0278;
    L_0x01f6:
        if (r3 == 0) goto L_0x0205;
    L_0x01f8:
        r2 = r3.first_name;
        r14 = r3.last_name;
        r2 = org.telegram.messenger.ContactsController.formatName(r2, r14);
        r14 = r2.toLowerCase();
        goto L_0x0210;
    L_0x0205:
        if (r10 == 0) goto L_0x020e;
    L_0x0207:
        r2 = r10.title;
        r14 = r2.toLowerCase();
        goto L_0x0210;
    L_0x020e:
        r2 = 0;
        r14 = 0;
    L_0x0210:
        r15 = 33;
        r16 = "windowBackgroundWhiteBlueText4";
        r9 = -1;
        if (r2 == 0) goto L_0x0235;
    L_0x0217:
        r14 = r14.indexOf(r0);
        if (r14 == r9) goto L_0x0235;
    L_0x021d:
        r9 = new android.text.SpannableStringBuilder;
        r9.<init>(r2);
        r2 = new android.text.style.ForegroundColorSpan;
        r11 = org.telegram.ui.ActionBar.Theme.getColor(r16);
        r2.<init>(r11);
        r0 = r0.length();
        r0 = r0 + r14;
        r9.setSpan(r2, r14, r0, r15);
        r0 = r9;
        goto L_0x0279;
    L_0x0235:
        if (r11 == 0) goto L_0x0278;
    L_0x0237:
        r2 = r0.startsWith(r13);
        if (r2 == 0) goto L_0x0241;
    L_0x023d:
        r0 = r0.substring(r7);
    L_0x0241:
        r2 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x0271 }
        r2.<init>();	 Catch:{ Exception -> 0x0271 }
        r2.append(r13);	 Catch:{ Exception -> 0x0271 }
        r2.append(r11);	 Catch:{ Exception -> 0x0271 }
        r13 = r11.toLowerCase();	 Catch:{ Exception -> 0x0271 }
        r13 = r13.indexOf(r0);	 Catch:{ Exception -> 0x0271 }
        if (r13 == r9) goto L_0x026e;
    L_0x0256:
        r0 = r0.length();	 Catch:{ Exception -> 0x0271 }
        if (r13 != 0) goto L_0x025f;
    L_0x025c:
        r0 = r0 + 1;
        goto L_0x0261;
    L_0x025f:
        r13 = r13 + 1;
    L_0x0261:
        r9 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x0271 }
        r14 = org.telegram.ui.ActionBar.Theme.getColor(r16);	 Catch:{ Exception -> 0x0271 }
        r9.<init>(r14);	 Catch:{ Exception -> 0x0271 }
        r0 = r0 + r13;
        r2.setSpan(r9, r13, r0, r15);	 Catch:{ Exception -> 0x0271 }
    L_0x026e:
        r9 = r2;
        goto L_0x01e7;
    L_0x0271:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r9 = r11;
        goto L_0x01e7;
    L_0x0278:
        r0 = 0;
    L_0x0279:
        r9 = 0;
    L_0x027a:
        r13 = 0;
    L_0x027b:
        if (r3 == 0) goto L_0x0290;
    L_0x027d:
        r2 = r3.id;
        r11 = r1.selfUserId;
        if (r2 != r11) goto L_0x0290;
    L_0x0283:
        r0 = NUM; // 0x7f0d087d float:1.8746522E38 double:1.053130851E-314;
        r2 = "SavedMessages";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r11 = r0;
        r9 = 0;
        r14 = 1;
        goto L_0x0292;
    L_0x0290:
        r11 = r0;
        r14 = 0;
    L_0x0292:
        if (r10 == 0) goto L_0x02d7;
    L_0x0294:
        r0 = r10.participants_count;
        if (r0 == 0) goto L_0x02d7;
    L_0x0298:
        r0 = org.telegram.messenger.ChatObject.isChannel(r10);
        if (r0 == 0) goto L_0x02ab;
    L_0x029e:
        r0 = r10.megagroup;
        if (r0 != 0) goto L_0x02ab;
    L_0x02a2:
        r0 = r10.participants_count;
        r2 = "Subscribers";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
        goto L_0x02b3;
    L_0x02ab:
        r0 = r10.participants_count;
        r2 = "Members";
        r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0);
    L_0x02b3:
        r2 = r9 instanceof android.text.SpannableStringBuilder;
        r15 = ", ";
        if (r2 == 0) goto L_0x02c4;
    L_0x02b9:
        r2 = r9;
        r2 = (android.text.SpannableStringBuilder) r2;
        r2 = r2.append(r15);
        r2.append(r0);
        goto L_0x02d7;
    L_0x02c4:
        r2 = android.text.TextUtils.isEmpty(r9);
        if (r2 != 0) goto L_0x02d8;
    L_0x02ca:
        r2 = new java.lang.CharSequence[r4];
        r2[r6] = r9;
        r2[r7] = r15;
        r2[r5] = r0;
        r0 = android.text.TextUtils.concat(r2);
        goto L_0x02d8;
    L_0x02d7:
        r0 = r9;
    L_0x02d8:
        if (r3 == 0) goto L_0x02dc;
    L_0x02da:
        r9 = r3;
        goto L_0x02dd;
    L_0x02dc:
        r9 = r10;
    L_0x02dd:
        r10 = r12;
        r12 = r0;
        r8.setData(r9, r10, r11, r12, r13, r14);
    L_0x02e2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    public int getItemViewType(int i) {
        int i2 = 1;
        int i3;
        if (isRecentSearchDisplayed()) {
            i3 = !DataQuery.getInstance(this.currentAccount).hints.isEmpty() ? 2 : 0;
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
            i3 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int size3 = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if ((i >= 0 && i < size + size2) || (i > size + size2 && i < (i3 + size) + size2)) {
                return 0;
            }
            i3 += size;
            if (i > i3 + size2 && i < (i3 + size3) + size2) {
                return 2;
            }
            if (size3 == 0 || i != (i3 + size3) + size2) {
                return 1;
            }
            return 3;
        } else {
            if (i != 0) {
                i2 = 4;
            }
            return i2;
        }
    }
}
