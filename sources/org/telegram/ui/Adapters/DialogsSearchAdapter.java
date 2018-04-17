package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
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
    private Timer searchTimer;
    private int selfUserId;

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$3 */
    class C07763 implements Runnable {

        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$3$1 */
        class C07741 implements Comparator<RecentSearchObject> {
            C07741() {
            }

            public int compare(RecentSearchObject lhs, RecentSearchObject rhs) {
                if (lhs.date < rhs.date) {
                    return 1;
                }
                if (lhs.date > rhs.date) {
                    return -1;
                }
                return 0;
            }
        }

        C07763() {
        }

        public void run() {
            try {
                long did;
                ArrayList<Integer> chatsToLoad;
                int a;
                int i = 0;
                SQLiteCursor cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
                ArrayList<Integer> usersToLoad = new ArrayList();
                ArrayList<Integer> chatsToLoad2 = new ArrayList();
                ArrayList<Integer> encryptedToLoad = new ArrayList();
                ArrayList<User> encUsers = new ArrayList();
                final ArrayList<RecentSearchObject> arrayList = new ArrayList();
                final LongSparseArray<RecentSearchObject> hashMap = new LongSparseArray();
                while (cursor.next()) {
                    did = cursor.longValue(i);
                    boolean add = false;
                    int lower_id = (int) did;
                    ArrayList<Integer> chatsToLoad3 = chatsToLoad2;
                    i = (int) (did >> 32);
                    if (lower_id == 0) {
                        chatsToLoad = chatsToLoad3;
                        if (DialogsSearchAdapter.this.dialogsType == null && encryptedToLoad.contains(Integer.valueOf(i)) == null) {
                            encryptedToLoad.add(Integer.valueOf(i));
                            add = true;
                        }
                    } else if (i != 1) {
                        chatsToLoad = chatsToLoad3;
                        if (lower_id > 0) {
                            if (DialogsSearchAdapter.this.dialogsType != 2 && usersToLoad.contains(Integer.valueOf(lower_id)) == null) {
                                usersToLoad.add(Integer.valueOf(lower_id));
                                add = true;
                            }
                        } else if (chatsToLoad.contains(Integer.valueOf(-lower_id)) == null) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                            add = true;
                        }
                    } else if (DialogsSearchAdapter.this.dialogsType == 0) {
                        chatsToLoad = chatsToLoad3;
                        if (!chatsToLoad.contains(Integer.valueOf(lower_id))) {
                            chatsToLoad.add(Integer.valueOf(lower_id));
                            add = true;
                        }
                    } else {
                        chatsToLoad = chatsToLoad3;
                    }
                    if (add) {
                        chatsToLoad2 = new RecentSearchObject();
                        chatsToLoad2.did = did;
                        chatsToLoad2.date = cursor.intValue(1);
                        arrayList.add(chatsToLoad2);
                        hashMap.put(chatsToLoad2.did, chatsToLoad2);
                    }
                    chatsToLoad2 = chatsToLoad;
                    i = 0;
                }
                chatsToLoad = chatsToLoad2;
                cursor.dispose();
                ArrayList<User> users = new ArrayList();
                if (!encryptedToLoad.isEmpty()) {
                    ArrayList<EncryptedChat> encryptedChats = new ArrayList();
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getEncryptedChatsInternal(TextUtils.join(",", encryptedToLoad), encryptedChats, usersToLoad);
                    for (a = 0; a < encryptedChats.size(); a++) {
                        ((RecentSearchObject) hashMap.get(((long) ((EncryptedChat) encryptedChats.get(a)).id) << 32)).object = (TLObject) encryptedChats.get(a);
                    }
                }
                if (!chatsToLoad.isEmpty()) {
                    ArrayList<Chat> chats = new ArrayList();
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    for (a = 0; a < chats.size(); a++) {
                        Chat chat = (Chat) chats.get(a);
                        if (chat.id > 0) {
                            did = (long) (-chat.id);
                        } else {
                            did = AndroidUtilities.makeBroadcastId(chat.id);
                        }
                        if (chat.migrated_to != null) {
                            RecentSearchObject recentSearchObject = (RecentSearchObject) hashMap.get(did);
                            hashMap.remove(did);
                            if (recentSearchObject != null) {
                                arrayList.remove(recentSearchObject);
                            }
                        } else {
                            ((RecentSearchObject) hashMap.get(did)).object = chat;
                        }
                    }
                }
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    int a2 = 0;
                    while (true) {
                        int a3 = a2;
                        if (a3 >= users.size()) {
                            break;
                        }
                        User user = (User) users.get(a3);
                        RecentSearchObject recentSearchObject2 = (RecentSearchObject) hashMap.get((long) user.id);
                        if (recentSearchObject2 != null) {
                            recentSearchObject2.object = user;
                        }
                        a2 = a3 + 1;
                    }
                }
                Collections.sort(arrayList, new C07741());
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DialogsSearchAdapter.this.setRecentSearch(arrayList, hashMap);
                    }
                });
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$5 */
    class C07785 implements Runnable {
        C07785() {
        }

        public void run() {
            try {
                MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private class DialogSearchResult {
        public int date;
        public CharSequence name;
        public TLObject object;

        private DialogSearchResult() {
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

    /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$1 */
    class C18911 implements SearchAdapterHelperDelegate {
        C18911() {
        }

        public void onDataSetChanged() {
            DialogsSearchAdapter.this.notifyDataSetChanged();
        }

        public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
            for (int a = 0; a < arrayList.size(); a++) {
                DialogsSearchAdapter.this.searchResultHashtags.add(((HashtagObject) arrayList.get(a)).hashtag);
            }
            if (DialogsSearchAdapter.this.delegate != null) {
                DialogsSearchAdapter.this.delegate.searchStateChanged(false);
            }
            DialogsSearchAdapter.this.notifyDataSetChanged();
        }
    }

    private class CategoryAdapterRecycler extends SelectionAdapter {
        private CategoryAdapterRecycler() {
        }

        public void setIndex(int value) {
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            view.setLayoutParams(new LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            return new Holder(view);
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            HintDialogCell cell = holder.itemView;
            TL_topPeer peer = (TL_topPeer) DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(position);
            TL_dialog dialog = new TL_dialog();
            Chat chat = null;
            User user = null;
            int did = 0;
            if (peer.peer.user_id != 0) {
                did = peer.peer.user_id;
                user = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(peer.peer.user_id));
            } else if (peer.peer.channel_id != 0) {
                did = -peer.peer.channel_id;
                chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(peer.peer.channel_id));
            } else if (peer.peer.chat_id != 0) {
                did = -peer.peer.chat_id;
                chat = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(peer.peer.chat_id));
            }
            cell.setTag(Integer.valueOf(did));
            String name = TtmlNode.ANONYMOUS_REGION_ID;
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else if (chat != null) {
                name = chat.title;
            }
            cell.setDialog(did, true, name);
        }

        public int getItemCount() {
            return DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
        }
    }

    public DialogsSearchAdapter(Context context, int messagesSearch, int type) {
        this.searchAdapterHelper.setDelegate(new C18911());
        this.mContext = context;
        this.needMessagesSearch = messagesSearch;
        this.dialogsType = type;
        this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        loadRecentSearch();
        DataQuery.getInstance(this.currentAccount).loadHints(true);
    }

    public RecyclerListView getInnerListView() {
        return this.innerListView;
    }

    public void setDelegate(DialogsSearchAdapterDelegate delegate) {
        this.delegate = delegate;
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

    private void searchMessagesInternal(String query) {
        if (this.needMessagesSearch != 0) {
            if (this.lastMessagesSearchString == null || this.lastMessagesSearchString.length() == 0) {
                if (query != null) {
                    if (query.length() == 0) {
                    }
                }
            }
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (query != null) {
                if (query.length() != 0) {
                    final TL_messages_searchGlobal req = new TL_messages_searchGlobal();
                    req.limit = 20;
                    req.f51q = query;
                    if (this.lastMessagesSearchString == null || !query.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                        req.offset_date = 0;
                        req.offset_id = 0;
                        req.offset_peer = new TL_inputPeerEmpty();
                    } else {
                        int id;
                        MessageObject lastMessage = (MessageObject) this.searchResultMessages.get(this.searchResultMessages.size() - 1);
                        req.offset_id = lastMessage.getId();
                        req.offset_date = lastMessage.messageOwner.date;
                        if (lastMessage.messageOwner.to_id.channel_id != 0) {
                            id = -lastMessage.messageOwner.to_id.channel_id;
                        } else if (lastMessage.messageOwner.to_id.chat_id != 0) {
                            id = -lastMessage.messageOwner.to_id.chat_id;
                        } else {
                            id = lastMessage.messageOwner.to_id.user_id;
                            req.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(id);
                        }
                        req.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(id);
                    }
                    this.lastMessagesSearchString = query;
                    final int currentReqId = this.lastReqId + 1;
                    this.lastReqId = currentReqId;
                    if (this.delegate != null) {
                        this.delegate.searchStateChanged(true);
                    }
                    this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (currentReqId == DialogsSearchAdapter.this.lastReqId && error == null) {
                                        messages_Messages res = response;
                                        boolean z = true;
                                        MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                        MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(res.users, false);
                                        MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChats(res.chats, false);
                                        if (req.offset_id == 0) {
                                            DialogsSearchAdapter.this.searchResultMessages.clear();
                                        }
                                        for (int a = 0; a < res.messages.size(); a++) {
                                            Message message = (Message) res.messages.get(a);
                                            DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(DialogsSearchAdapter.this.currentAccount, message, false));
                                            long dialog_id = MessageObject.getDialogId(message);
                                            ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_inbox_max;
                                            Integer value = (Integer) read_max.get(Long.valueOf(dialog_id));
                                            if (value == null) {
                                                value = Integer.valueOf(MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDialogReadMax(message.out, dialog_id));
                                                read_max.put(Long.valueOf(dialog_id), value);
                                            }
                                            message.unread = value.intValue() < message.id;
                                        }
                                        DialogsSearchAdapter dialogsSearchAdapter = DialogsSearchAdapter.this;
                                        if (res.messages.size() == 20) {
                                            z = false;
                                        }
                                        dialogsSearchAdapter.messagesSearchEndReached = z;
                                        DialogsSearchAdapter.this.notifyDataSetChanged();
                                    }
                                    if (DialogsSearchAdapter.this.delegate != null) {
                                        DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                                    }
                                    DialogsSearchAdapter.this.reqId = 0;
                                }
                            });
                        }
                    }, 2);
                    return;
                }
            }
            this.searchResultMessages.clear();
            this.lastReqId = 0;
            this.lastMessagesSearchString = null;
            notifyDataSetChanged();
            if (this.delegate != null) {
                this.delegate.searchStateChanged(false);
            }
        }
    }

    public boolean hasRecentRearch() {
        if (this.recentSearchObjects.isEmpty()) {
            if (DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isRecentSearchDisplayed() {
        return this.needMessagesSearch != 2 && ((this.lastSearchText == null || this.lastSearchText.length() == 0) && !(this.recentSearchObjects.isEmpty() && DataQuery.getInstance(this.currentAccount).hints.isEmpty()));
    }

    public void loadRecentSearch() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C07763());
    }

    public void putRecentSearch(final long did, TLObject object) {
        RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjectsById.get(did);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement state = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
                    state.requery();
                    state.bindLong(1, did);
                    state.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
                    state.step();
                    state.dispose();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void clearRecentSearch() {
        this.recentSearchObjectsById = new LongSparseArray();
        this.recentSearchObjects = new ArrayList();
        notifyDataSetChanged();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C07785());
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    private void setRecentSearch(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> hashMap) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = hashMap;
        for (int a = 0; a < this.recentSearchObjects.size(); a++) {
            RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjects.get(a);
            if (recentSearchObject.object instanceof User) {
                MessagesController.getInstance(this.currentAccount).putUser((User) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof Chat) {
                MessagesController.getInstance(this.currentAccount).putChat((Chat) recentSearchObject.object, true);
            } else if (recentSearchObject.object instanceof EncryptedChat) {
                MessagesController.getInstance(this.currentAccount).putEncryptedChat((EncryptedChat) recentSearchObject.object, true);
            }
        }
        notifyDataSetChanged();
    }

    private void searchDialogsInternal(final String query, final int searchId) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$6$1 */
                class C07791 implements Comparator<DialogSearchResult> {
                    C07791() {
                    }

                    public int compare(DialogSearchResult lhs, DialogSearchResult rhs) {
                        if (lhs.date < rhs.date) {
                            return 1;
                        }
                        if (lhs.date > rhs.date) {
                            return -1;
                        }
                        return 0;
                    }
                }

                public void run() {
                    C07806 c07806 = this;
                    try {
                        String savedMessages = LocaleController.getString("SavedMessages", R.string.SavedMessages).toLowerCase();
                        String search1 = query.trim().toLowerCase();
                        if (search1.length() == 0) {
                            DialogsSearchAdapter.this.lastSearchId = -1;
                            DialogsSearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), DialogsSearchAdapter.this.lastSearchId);
                            return;
                        }
                        DialogSearchResult dialogSearchResult;
                        String[] search;
                        ArrayList<User> encUsers;
                        int resultCount;
                        int high_id;
                        String[] search2;
                        String tName;
                        String username;
                        int usernamePos;
                        StringBuilder stringBuilder;
                        StringBuilder stringBuilder2;
                        int length;
                        ArrayList<User> encUsers2;
                        int found;
                        String q;
                        String search22 = LocaleController.getInstance().getTranslitString(search1);
                        if (search1.equals(search22) || search22.length() == 0) {
                            search22 = null;
                        }
                        int i = 0;
                        String[] search3 = new String[((search22 != null ? 1 : 0) + 1)];
                        search3[0] = search1;
                        if (search22 != null) {
                            search3[1] = search22;
                        }
                        ArrayList<Integer> usersToLoad = new ArrayList();
                        ArrayList<Integer> chatsToLoad = new ArrayList();
                        ArrayList<Integer> encryptedToLoad = new ArrayList();
                        ArrayList<User> encUsers3 = new ArrayList();
                        int resultCount2 = 0;
                        LongSparseArray<DialogSearchResult> dialogsResult = new LongSparseArray();
                        SQLiteCursor cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600", new Object[0]);
                        while (cursor.next()) {
                            String search23;
                            long id = cursor.longValue(i);
                            dialogSearchResult = new DialogSearchResult();
                            dialogSearchResult.date = cursor.intValue(1);
                            search = search3;
                            long id2 = id;
                            dialogsResult.put(id2, dialogSearchResult);
                            int lower_id = (int) id2;
                            encUsers = encUsers3;
                            resultCount = resultCount2;
                            high_id = (int) (id2 >> 32);
                            if (lower_id == 0) {
                                search23 = search22;
                                if (DialogsSearchAdapter.this.dialogsType == 0 && !encryptedToLoad.contains(Integer.valueOf(high_id))) {
                                    encryptedToLoad.add(Integer.valueOf(high_id));
                                }
                            } else if (high_id == 1) {
                                if (DialogsSearchAdapter.this.dialogsType == 0 && !chatsToLoad.contains(Integer.valueOf(lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(lower_id));
                                }
                                search23 = search22;
                            } else if (lower_id > 0) {
                                search23 = search22;
                                if (!(DialogsSearchAdapter.this.dialogsType == 2 || usersToLoad.contains(Integer.valueOf(lower_id)))) {
                                    usersToLoad.add(Integer.valueOf(lower_id));
                                }
                            } else {
                                search23 = search22;
                                if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(-lower_id));
                                }
                            }
                            search3 = search;
                            encUsers3 = encUsers;
                            resultCount2 = resultCount;
                            search22 = search23;
                            i = 0;
                        }
                        search = search3;
                        encUsers = encUsers3;
                        resultCount = resultCount2;
                        cursor.dispose();
                        if (savedMessages.startsWith(search1)) {
                            User user = UserConfig.getInstance(DialogsSearchAdapter.this.currentAccount).getCurrentUser();
                            dialogSearchResult = new DialogSearchResult();
                            dialogSearchResult.date = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            dialogSearchResult.name = savedMessages;
                            dialogSearchResult.object = user;
                            dialogsResult.put((long) user.id, dialogSearchResult);
                            resultCount2 = resultCount + 1;
                        } else {
                            resultCount2 = resultCount;
                        }
                        String str;
                        ArrayList<Integer> arrayList;
                        if (usersToLoad.isEmpty()) {
                            str = search1;
                            arrayList = usersToLoad;
                            search2 = search;
                        } else {
                            cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", usersToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                search22 = cursor.stringValue(2);
                                tName = LocaleController.getInstance().getTranslitString(search22);
                                if (search22.equals(tName)) {
                                    tName = null;
                                }
                                username = null;
                                usernamePos = search22.lastIndexOf(";;;");
                                if (usernamePos != -1) {
                                    username = search22.substring(usernamePos + 3);
                                }
                                String savedMessages2 = savedMessages;
                                search2 = search;
                                savedMessages = search2.length;
                                int found2 = 0;
                                high_id = 0;
                                while (high_id < savedMessages) {
                                    int usernamePos2;
                                    NativeByteBuffer data;
                                    String str2;
                                    String str3;
                                    String str4;
                                    String str5 = savedMessages;
                                    savedMessages = search2[high_id];
                                    if (search22.startsWith(savedMessages)) {
                                        str = search1;
                                        usernamePos2 = usernamePos;
                                    } else {
                                        str = search1;
                                        search1 = new StringBuilder();
                                        usernamePos2 = usernamePos;
                                        search1.append(" ");
                                        search1.append(savedMessages);
                                        if (search22.contains(search1.toString()) == null) {
                                            if (tName != null) {
                                                if (tName.startsWith(savedMessages) == null) {
                                                    search1 = new StringBuilder();
                                                    search1.append(" ");
                                                    search1.append(savedMessages);
                                                    if (tName.contains(search1.toString()) != null) {
                                                    }
                                                }
                                            }
                                            if (username == null || username.startsWith(savedMessages) == null) {
                                                search1 = found2;
                                            } else {
                                                search1 = 2;
                                            }
                                            if (search1 == null) {
                                                data = cursor.byteBufferValue(0);
                                                if (data == null) {
                                                    search22 = User.TLdeserialize(data, data.readInt32(false), false);
                                                    data.reuse();
                                                    dialogSearchResult = (DialogSearchResult) dialogsResult.get((long) search22.id);
                                                    if (search22.status == null) {
                                                        arrayList = usersToLoad;
                                                        search22.status.expires = cursor.intValue(1);
                                                    } else {
                                                        arrayList = usersToLoad;
                                                    }
                                                    if (search1 != 1) {
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(search22.first_name, search22.last_name, savedMessages);
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(search22.username);
                                                        username = stringBuilder.toString();
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("@");
                                                        stringBuilder2.append(savedMessages);
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(username, null, stringBuilder2.toString());
                                                    }
                                                    dialogSearchResult.object = search22;
                                                    resultCount2++;
                                                } else {
                                                    arrayList = usersToLoad;
                                                }
                                                search = search2;
                                                savedMessages = savedMessages2;
                                                search1 = str;
                                                usersToLoad = arrayList;
                                            } else {
                                                str2 = search22;
                                                str3 = tName;
                                                str4 = username;
                                                arrayList = usersToLoad;
                                                high_id++;
                                                found2 = search1;
                                                savedMessages = str5;
                                                search1 = str;
                                                usernamePos = usernamePos2;
                                            }
                                        }
                                    }
                                    search1 = true;
                                    if (search1 == null) {
                                        str2 = search22;
                                        str3 = tName;
                                        str4 = username;
                                        arrayList = usersToLoad;
                                        high_id++;
                                        found2 = search1;
                                        savedMessages = str5;
                                        search1 = str;
                                        usernamePos = usernamePos2;
                                    } else {
                                        data = cursor.byteBufferValue(0);
                                        if (data == null) {
                                            arrayList = usersToLoad;
                                        } else {
                                            search22 = User.TLdeserialize(data, data.readInt32(false), false);
                                            data.reuse();
                                            dialogSearchResult = (DialogSearchResult) dialogsResult.get((long) search22.id);
                                            if (search22.status == null) {
                                                arrayList = usersToLoad;
                                            } else {
                                                arrayList = usersToLoad;
                                                search22.status.expires = cursor.intValue(1);
                                            }
                                            if (search1 != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(search22.username);
                                                username = stringBuilder.toString();
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("@");
                                                stringBuilder2.append(savedMessages);
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(username, null, stringBuilder2.toString());
                                            } else {
                                                dialogSearchResult.name = AndroidUtilities.generateSearchName(search22.first_name, search22.last_name, savedMessages);
                                            }
                                            dialogSearchResult.object = search22;
                                            resultCount2++;
                                        }
                                        search = search2;
                                        savedMessages = savedMessages2;
                                        search1 = str;
                                        usersToLoad = arrayList;
                                    }
                                }
                                str = search1;
                                arrayList = usersToLoad;
                                search = search2;
                                savedMessages = savedMessages2;
                                search1 = str;
                                usersToLoad = arrayList;
                            }
                            str = search1;
                            arrayList = usersToLoad;
                            search2 = search;
                            cursor.dispose();
                        }
                        if (!chatsToLoad.isEmpty()) {
                            cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", chatsToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                savedMessages = cursor.stringValue(1);
                                search1 = LocaleController.getInstance().getTranslitString(savedMessages);
                                if (savedMessages.equals(search1)) {
                                    search1 = null;
                                }
                                length = search2.length;
                                int i2 = 0;
                                while (i2 < length) {
                                    username = search2[i2];
                                    if (!savedMessages.startsWith(username)) {
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(" ");
                                        stringBuilder2.append(username);
                                        if (!savedMessages.contains(stringBuilder2.toString())) {
                                            if (search1 != null) {
                                                if (!search1.startsWith(username)) {
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(" ");
                                                    stringBuilder2.append(username);
                                                    if (search1.contains(stringBuilder2.toString())) {
                                                    }
                                                }
                                            }
                                            i2++;
                                        }
                                    }
                                    NativeByteBuffer data2 = cursor.byteBufferValue(0);
                                    if (data2 != null) {
                                        Chat chat = Chat.TLdeserialize(data2, data2.readInt32(false), false);
                                        data2.reuse();
                                        String str6;
                                        if (chat == null || chat.deactivated) {
                                            str6 = search1;
                                        } else {
                                            long dialog_id;
                                            if (ChatObject.isChannel(chat)) {
                                                if (ChatObject.isNotInChat(chat)) {
                                                    String str7 = savedMessages;
                                                    str6 = search1;
                                                }
                                            }
                                            if (chat.id > 0) {
                                                dialog_id = (long) (-chat.id);
                                            } else {
                                                dialog_id = AndroidUtilities.makeBroadcastId(chat.id);
                                            }
                                            DialogSearchResult dialogSearchResult2 = (DialogSearchResult) dialogsResult.get(dialog_id);
                                            dialogSearchResult2.name = AndroidUtilities.generateSearchName(chat.title, null, username);
                                            dialogSearchResult2.object = chat;
                                            resultCount2++;
                                        }
                                    }
                                }
                            }
                            cursor.dispose();
                        }
                        ArrayList<Integer> arrayList2;
                        if (encryptedToLoad.isEmpty()) {
                            arrayList2 = encryptedToLoad;
                            encUsers2 = encUsers;
                        } else {
                            cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)", new Object[]{TextUtils.join(",", encryptedToLoad)}), new Object[0]);
                            while (cursor.next()) {
                                ArrayList<Integer> chatsToLoad2;
                                savedMessages = cursor.stringValue(1);
                                search22 = LocaleController.getInstance().getTranslitString(savedMessages);
                                if (savedMessages.equals(search22)) {
                                    search22 = null;
                                }
                                tName = null;
                                i = savedMessages.lastIndexOf(";;;");
                                if (i != -1) {
                                    tName = savedMessages.substring(i + 2);
                                }
                                found = 0;
                                usernamePos = 0;
                                while (usernamePos < search2.length) {
                                    int i3;
                                    int found3;
                                    EncryptedChat chat2;
                                    User user2;
                                    NativeByteBuffer name;
                                    EncryptedChat chat3;
                                    String str8;
                                    int use_count;
                                    NativeByteBuffer data3;
                                    int admin_id;
                                    int i4;
                                    ArrayList<Integer> arrayList3;
                                    StringBuilder stringBuilder3;
                                    String name2;
                                    String str9;
                                    q = search2[usernamePos];
                                    if (savedMessages.startsWith(q)) {
                                        i3 = i;
                                    } else {
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        i3 = i;
                                        stringBuilder4.append(" ");
                                        stringBuilder4.append(q);
                                        if (!savedMessages.contains(stringBuilder4.toString())) {
                                            if (search22 != null) {
                                                if (!search22.startsWith(q)) {
                                                    stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append(" ");
                                                    stringBuilder4.append(q);
                                                    if (search22.contains(stringBuilder4.toString())) {
                                                    }
                                                }
                                            }
                                            if (tName != null && tName.startsWith(q)) {
                                                found3 = 2;
                                                found = found3;
                                            }
                                            if (found == 0) {
                                                chat2 = null;
                                                user2 = null;
                                                name = cursor.byteBufferValue(null);
                                                if (name == null) {
                                                    chat3 = null;
                                                    chat2 = EncryptedChat.TLdeserialize(name, name.readInt32(false), false);
                                                    name.reuse();
                                                } else {
                                                    chat3 = null;
                                                    str8 = search22;
                                                }
                                                name = cursor.byteBufferValue(6);
                                                if (name == null) {
                                                    user2 = User.TLdeserialize(name, name.readInt32(false), false);
                                                    name.reuse();
                                                }
                                                if (!(chat2 == null || user2 == null)) {
                                                    chatsToLoad2 = chatsToLoad;
                                                    arrayList2 = encryptedToLoad;
                                                    dialogSearchResult = (DialogSearchResult) dialogsResult.get(((long) chat2.id) << 32);
                                                    chat2.user_id = cursor.intValue(2);
                                                    chat2.a_or_b = cursor.byteArrayValue(3);
                                                    chat2.auth_key = cursor.byteArrayValue(4);
                                                    chat2.ttl = cursor.intValue(5);
                                                    chat2.layer = cursor.intValue(8);
                                                    chat2.seq_in = cursor.intValue(9);
                                                    chat2.seq_out = cursor.intValue(10);
                                                    use_count = cursor.intValue(11);
                                                    chat2.key_use_count_in = (short) (use_count >> 16);
                                                    chat2.key_use_count_out = (short) use_count;
                                                    chat2.exchange_id = cursor.longValue(12);
                                                    chat2.key_create_date = cursor.intValue(13);
                                                    chat2.future_key_fingerprint = cursor.longValue(14);
                                                    chat2.future_auth_key = cursor.byteArrayValue(15);
                                                    chat2.key_hash = cursor.byteArrayValue(16);
                                                    chat2.in_seq_no = cursor.intValue(17);
                                                    chatsToLoad = cursor.intValue(18);
                                                    if (chatsToLoad != null) {
                                                        chat2.admin_id = chatsToLoad;
                                                    }
                                                    chat2.mtproto_seq = cursor.intValue(19);
                                                    if (user2.status != null) {
                                                        user2.status.expires = cursor.intValue(7);
                                                    }
                                                    if (found != 1) {
                                                        data3 = name;
                                                        dialogSearchResult.name = new SpannableStringBuilder(ContactsController.formatName(user2.first_name, user2.last_name));
                                                        admin_id = chatsToLoad;
                                                        ((SpannableStringBuilder) dialogSearchResult.name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_secretName)), null, dialogSearchResult.name.length(), 33);
                                                    } else {
                                                        data3 = name;
                                                        i4 = found;
                                                        arrayList3 = chatsToLoad;
                                                        savedMessages = new StringBuilder();
                                                        savedMessages.append("@");
                                                        savedMessages.append(user2.username);
                                                        savedMessages = savedMessages.toString();
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(q);
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(savedMessages, null, stringBuilder3.toString());
                                                    }
                                                    dialogSearchResult.object = chat2;
                                                    encUsers2 = encUsers;
                                                    encUsers2.add(user2);
                                                    resultCount2++;
                                                    encUsers = encUsers2;
                                                    chatsToLoad = chatsToLoad2;
                                                    encryptedToLoad = arrayList2;
                                                }
                                                chatsToLoad2 = chatsToLoad;
                                                arrayList2 = encryptedToLoad;
                                                encUsers2 = encUsers;
                                                encUsers = encUsers2;
                                                chatsToLoad = chatsToLoad2;
                                                encryptedToLoad = arrayList2;
                                            } else {
                                                name2 = savedMessages;
                                                str8 = search22;
                                                str9 = tName;
                                                i4 = found;
                                                chatsToLoad2 = chatsToLoad;
                                                arrayList2 = encryptedToLoad;
                                                usernamePos++;
                                                i = i3;
                                                savedMessages = name2;
                                            }
                                        }
                                    }
                                    found3 = 1;
                                    found = found3;
                                    if (found == 0) {
                                        name2 = savedMessages;
                                        str8 = search22;
                                        str9 = tName;
                                        i4 = found;
                                        chatsToLoad2 = chatsToLoad;
                                        arrayList2 = encryptedToLoad;
                                        usernamePos++;
                                        i = i3;
                                        savedMessages = name2;
                                    } else {
                                        chat2 = null;
                                        user2 = null;
                                        name = cursor.byteBufferValue(null);
                                        if (name == null) {
                                            chat3 = null;
                                            str8 = search22;
                                        } else {
                                            chat3 = null;
                                            chat2 = EncryptedChat.TLdeserialize(name, name.readInt32(false), false);
                                            name.reuse();
                                        }
                                        name = cursor.byteBufferValue(6);
                                        if (name == null) {
                                        } else {
                                            user2 = User.TLdeserialize(name, name.readInt32(false), false);
                                            name.reuse();
                                        }
                                        chatsToLoad2 = chatsToLoad;
                                        arrayList2 = encryptedToLoad;
                                        dialogSearchResult = (DialogSearchResult) dialogsResult.get(((long) chat2.id) << 32);
                                        chat2.user_id = cursor.intValue(2);
                                        chat2.a_or_b = cursor.byteArrayValue(3);
                                        chat2.auth_key = cursor.byteArrayValue(4);
                                        chat2.ttl = cursor.intValue(5);
                                        chat2.layer = cursor.intValue(8);
                                        chat2.seq_in = cursor.intValue(9);
                                        chat2.seq_out = cursor.intValue(10);
                                        use_count = cursor.intValue(11);
                                        chat2.key_use_count_in = (short) (use_count >> 16);
                                        chat2.key_use_count_out = (short) use_count;
                                        chat2.exchange_id = cursor.longValue(12);
                                        chat2.key_create_date = cursor.intValue(13);
                                        chat2.future_key_fingerprint = cursor.longValue(14);
                                        chat2.future_auth_key = cursor.byteArrayValue(15);
                                        chat2.key_hash = cursor.byteArrayValue(16);
                                        chat2.in_seq_no = cursor.intValue(17);
                                        chatsToLoad = cursor.intValue(18);
                                        if (chatsToLoad != null) {
                                            chat2.admin_id = chatsToLoad;
                                        }
                                        chat2.mtproto_seq = cursor.intValue(19);
                                        if (user2.status != null) {
                                            user2.status.expires = cursor.intValue(7);
                                        }
                                        if (found != 1) {
                                            data3 = name;
                                            i4 = found;
                                            arrayList3 = chatsToLoad;
                                            savedMessages = new StringBuilder();
                                            savedMessages.append("@");
                                            savedMessages.append(user2.username);
                                            savedMessages = savedMessages.toString();
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("@");
                                            stringBuilder3.append(q);
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName(savedMessages, null, stringBuilder3.toString());
                                        } else {
                                            data3 = name;
                                            dialogSearchResult.name = new SpannableStringBuilder(ContactsController.formatName(user2.first_name, user2.last_name));
                                            admin_id = chatsToLoad;
                                            ((SpannableStringBuilder) dialogSearchResult.name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_secretName)), null, dialogSearchResult.name.length(), 33);
                                        }
                                        dialogSearchResult.object = chat2;
                                        encUsers2 = encUsers;
                                        encUsers2.add(user2);
                                        resultCount2++;
                                        encUsers = encUsers2;
                                        chatsToLoad = chatsToLoad2;
                                        encryptedToLoad = arrayList2;
                                    }
                                }
                                chatsToLoad2 = chatsToLoad;
                                arrayList2 = encryptedToLoad;
                                encUsers2 = encUsers;
                                encUsers = encUsers2;
                                chatsToLoad = chatsToLoad2;
                                encryptedToLoad = arrayList2;
                            }
                            arrayList2 = encryptedToLoad;
                            encUsers2 = encUsers;
                            cursor.dispose();
                        }
                        ArrayList<DialogSearchResult> searchResults = new ArrayList(resultCount2);
                        for (length = 0; length < dialogsResult.size(); length++) {
                            dialogSearchResult = (DialogSearchResult) dialogsResult.valueAt(length);
                            if (!(dialogSearchResult.object == null || dialogSearchResult.name == null)) {
                                searchResults.add(dialogSearchResult);
                            }
                        }
                        Collections.sort(searchResults, new C07791());
                        ArrayList<TLObject> resultArray = new ArrayList();
                        ArrayList<CharSequence> resultArrayNames = new ArrayList();
                        for (i = 0; i < searchResults.size(); i++) {
                            DialogSearchResult dialogSearchResult3 = (DialogSearchResult) searchResults.get(i);
                            resultArray.add(dialogSearchResult3.object);
                            resultArrayNames.add(dialogSearchResult3.name);
                        }
                        if (DialogsSearchAdapter.this.dialogsType != 2) {
                            cursor = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                            while (cursor.next()) {
                                usernamePos = cursor.intValue(3);
                                if (dialogsResult.indexOfKey((long) usernamePos) < 0) {
                                    String name3 = cursor.stringValue(2);
                                    String tName2 = LocaleController.getInstance().getTranslitString(name3);
                                    if (name3.equals(tName2)) {
                                        tName2 = null;
                                    }
                                    q = null;
                                    i = name3.lastIndexOf(";;;");
                                    if (i != -1) {
                                        q = name3.substring(i + 3);
                                    }
                                    ArrayList<DialogSearchResult> searchResults2 = searchResults;
                                    searchResults = search2.length;
                                    int found4 = 0;
                                    found = 0;
                                    while (found < searchResults) {
                                        int usernamePos3;
                                        int uid;
                                        NativeByteBuffer data4;
                                        String str10;
                                        StringBuilder stringBuilder5;
                                        String stringBuilder6;
                                        StringBuilder stringBuilder7;
                                        ArrayList<DialogSearchResult> arrayList4 = searchResults;
                                        search1 = search2[found];
                                        if (name3.startsWith(search1)) {
                                            usernamePos3 = i;
                                            uid = usernamePos;
                                        } else {
                                            usernamePos3 = i;
                                            stringBuilder = new StringBuilder();
                                            uid = usernamePos;
                                            stringBuilder.append(" ");
                                            stringBuilder.append(search1);
                                            if (!name3.contains(stringBuilder.toString())) {
                                                if (tName2 != null) {
                                                    if (!tName2.startsWith(search1)) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(search1);
                                                        if (tName2.contains(stringBuilder.toString())) {
                                                        }
                                                    }
                                                }
                                                if (q == null || !q.startsWith(search1)) {
                                                    i = found4;
                                                } else {
                                                    i = 2;
                                                }
                                                if (i == 0) {
                                                    data4 = cursor.byteBufferValue(0);
                                                    if (data4 != null) {
                                                        name3 = User.TLdeserialize(data4, data4.readInt32(false), false);
                                                        data4.reuse();
                                                        if (name3.status == null) {
                                                            name3.status.expires = cursor.intValue(1);
                                                        } else {
                                                            str10 = tName2;
                                                        }
                                                        if (i != 1) {
                                                            resultArrayNames.add(AndroidUtilities.generateSearchName(name3.first_name, name3.last_name, search1));
                                                        } else {
                                                            stringBuilder5 = new StringBuilder();
                                                            stringBuilder5.append("@");
                                                            stringBuilder5.append(name3.username);
                                                            stringBuilder6 = stringBuilder5.toString();
                                                            stringBuilder7 = new StringBuilder();
                                                            stringBuilder7.append("@");
                                                            stringBuilder7.append(search1);
                                                            resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder6, null, stringBuilder7.toString()));
                                                        }
                                                        resultArray.add(name3);
                                                        searchResults = searchResults2;
                                                    }
                                                    searchResults = searchResults2;
                                                } else {
                                                    found++;
                                                    found4 = i;
                                                    searchResults = arrayList4;
                                                    i = usernamePos3;
                                                    usernamePos = uid;
                                                    tName2 = tName2;
                                                }
                                            }
                                        }
                                        i = 1;
                                        if (i == 0) {
                                            found++;
                                            found4 = i;
                                            searchResults = arrayList4;
                                            i = usernamePos3;
                                            usernamePos = uid;
                                            tName2 = tName2;
                                        } else {
                                            data4 = cursor.byteBufferValue(0);
                                            if (data4 != null) {
                                                name3 = User.TLdeserialize(data4, data4.readInt32(false), false);
                                                data4.reuse();
                                                if (name3.status == null) {
                                                    str10 = tName2;
                                                } else {
                                                    name3.status.expires = cursor.intValue(1);
                                                }
                                                if (i != 1) {
                                                    stringBuilder5 = new StringBuilder();
                                                    stringBuilder5.append("@");
                                                    stringBuilder5.append(name3.username);
                                                    stringBuilder6 = stringBuilder5.toString();
                                                    stringBuilder7 = new StringBuilder();
                                                    stringBuilder7.append("@");
                                                    stringBuilder7.append(search1);
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder6, null, stringBuilder7.toString()));
                                                } else {
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(name3.first_name, name3.last_name, search1));
                                                }
                                                resultArray.add(name3);
                                                searchResults = searchResults2;
                                            }
                                            searchResults = searchResults2;
                                        }
                                    }
                                    searchResults = searchResults2;
                                }
                            }
                            cursor.dispose();
                        }
                        DialogsSearchAdapter.this.updateSearchResults(resultArray, resultArrayNames, encUsers2, searchId);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private void updateSearchResults(ArrayList<TLObject> result, ArrayList<CharSequence> names, ArrayList<User> encUsers, int searchId) {
        final int i = searchId;
        final ArrayList<TLObject> arrayList = result;
        final ArrayList<User> arrayList2 = encUsers;
        final ArrayList<CharSequence> arrayList3 = names;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (i == DialogsSearchAdapter.this.lastSearchId) {
                    for (int a = 0; a < arrayList.size(); a++) {
                        TLObject obj = (TLObject) arrayList.get(a);
                        if (obj instanceof User) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUser((User) obj, true);
                        } else if (obj instanceof Chat) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChat((Chat) obj, true);
                        } else if (obj instanceof EncryptedChat) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putEncryptedChat((EncryptedChat) obj, true);
                        }
                    }
                    MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(arrayList2, true);
                    DialogsSearchAdapter.this.searchResult = arrayList;
                    DialogsSearchAdapter.this.searchResultNames = arrayList3;
                    DialogsSearchAdapter.this.searchAdapterHelper.mergeResults(DialogsSearchAdapter.this.searchResult);
                    DialogsSearchAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
    }

    public void searchDialogs(final String query) {
        if (query == null || this.lastSearchText == null || !query.equals(this.lastSearchText)) {
            this.lastSearchText = query;
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                    this.searchTimer = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (query != null) {
                if (query.length() != 0) {
                    if (this.needMessagesSearch != 2 && query.startsWith("#") && query.length() == 1) {
                        this.messagesSearchEndReached = true;
                        if (this.searchAdapterHelper.loadRecentHashtags()) {
                            this.searchResultMessages.clear();
                            this.searchResultHashtags.clear();
                            ArrayList<HashtagObject> hashtags = this.searchAdapterHelper.getHashtags();
                            for (int a = 0; a < hashtags.size(); a++) {
                                this.searchResultHashtags.add(((HashtagObject) hashtags.get(a)).hashtag);
                            }
                            if (this.delegate != null) {
                                this.delegate.searchStateChanged(false);
                            }
                        } else if (this.delegate != null) {
                            this.delegate.searchStateChanged(true);
                        }
                        notifyDataSetChanged();
                    } else {
                        this.searchResultHashtags.clear();
                        notifyDataSetChanged();
                    }
                    final int searchId = this.lastSearchId + 1;
                    this.lastSearchId = searchId;
                    this.searchTimer = new Timer();
                    this.searchTimer.schedule(new TimerTask() {

                        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$8$1 */
                        class C07821 implements Runnable {
                            C07821() {
                            }

                            public void run() {
                                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                                    DialogsSearchAdapter.this.searchAdapterHelper.queryServerSearch(query, true, true, true, true, 0, false);
                                }
                                DialogsSearchAdapter.this.searchMessagesInternal(query);
                            }
                        }

                        public void run() {
                            try {
                                cancel();
                                DialogsSearchAdapter.this.searchTimer.cancel();
                                DialogsSearchAdapter.this.searchTimer = null;
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            DialogsSearchAdapter.this.searchDialogsInternal(query, searchId);
                            AndroidUtilities.runOnUIThread(new C07821());
                        }
                    }, 200, 300);
                }
            }
            this.searchAdapterHelper.unloadRecentHashtags();
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchResultHashtags.clear();
            this.searchAdapterHelper.mergeResults(null);
            if (this.needMessagesSearch != 2) {
                this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, 0, false);
            }
            searchMessagesInternal(null);
            notifyDataSetChanged();
        }
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
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int messagesCount = this.searchResultMessages.size();
            size += i;
            if (globalCount != 0) {
                size += globalCount + 1;
            }
            if (messagesCount != 0) {
                size += (messagesCount + 1) + (this.messagesSearchEndReached ^ 1);
            }
            return size;
        }
    }

    public Object getItem(int i) {
        int messagesCount = 0;
        if (isRecentSearchDisplayed()) {
            if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                messagesCount = 2;
            }
            int offset = messagesCount;
            if (i <= offset || (i - 1) - offset >= this.recentSearchObjects.size()) {
                return null;
            }
            TLObject object = ((RecentSearchObject) this.recentSearchObjects.get((i - 1) - offset)).object;
            TLObject user;
            if (object instanceof User) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((User) object).id));
                if (user != null) {
                    object = user;
                }
            } else if (object instanceof Chat) {
                user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((Chat) object).id));
                if (user != null) {
                    object = user;
                }
            }
            return object;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList<TLObject> localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            int localCount = this.searchResult.size();
            int localServerCount = localServerSearch.size();
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                messagesCount = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < localCount) {
                return this.searchResult.get(i);
            }
            if (i >= localCount && i < localServerCount + localCount) {
                return localServerSearch.get(i - localCount);
            }
            if (i > localCount + localServerCount && i < (globalCount + localCount) + localServerCount) {
                return globalSearch.get(((i - localCount) - localServerCount) - 1);
            }
            if (i <= (globalCount + localCount) + localServerCount || i >= ((globalCount + localCount) + messagesCount) + localServerCount) {
                return null;
            }
            return this.searchResultMessages.get((((i - localCount) - globalCount) - localServerCount) - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isEnabled(ViewHolder holder) {
        int type = holder.getItemViewType();
        return (type == 1 || type == 3) ? false : true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                view = new ProfileSearchCell(this.mContext);
                break;
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            case 2:
                view = new DialogCell(this.mContext, false);
                break;
            case 3:
                view = new LoadingCell(this.mContext);
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                View horizontalListView = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent e) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return super.onInterceptTouchEvent(e);
                    }
                };
                horizontalListView.setTag(Integer.valueOf(9));
                horizontalListView.setItemAnimator(null);
                horizontalListView.setLayoutAnimation(null);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                layoutManager.setOrientation(0);
                horizontalListView.setLayoutManager(layoutManager);
                horizontalListView.setAdapter(new CategoryAdapterRecycler());
                horizontalListView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog((long) ((Integer) view.getTag()).intValue());
                        }
                    }
                });
                horizontalListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int position) {
                        if (DialogsSearchAdapter.this.delegate != null) {
                            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer) view.getTag()).intValue());
                        }
                        return true;
                    }
                });
                view = horizontalListView;
                this.innerListView = horizontalListView;
                break;
            default:
                break;
        }
        if (viewType == 5) {
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
        } else {
            view.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        EncryptedChat encryptedChat;
        Throwable isRecent;
        Object username;
        boolean savedMessages;
        CharSequence name;
        CharSequence username2;
        CharSequence username3;
        DialogsSearchAdapter dialogsSearchAdapter = this;
        ViewHolder viewHolder = holder;
        int i = position;
        switch (holder.getItemViewType()) {
            case 0:
                TLObject chat;
                TLObject chat2;
                ArrayList<TLObject> globalSearch;
                int localCount;
                int localServerCount;
                boolean z;
                String charSequence;
                StringBuilder stringBuilder;
                String nameSearch;
                String nameSearchLower;
                String nameSearch2;
                String nameSearchLower2;
                int index;
                SpannableStringBuilder spannableStringBuilder;
                Object name2;
                String str;
                int i2;
                int i3;
                SpannableStringBuilder spannableStringBuilder2;
                int indexOf;
                ProfileSearchCell cell = viewHolder.itemView;
                TLObject user = null;
                TLObject chat3 = null;
                CharSequence username4 = null;
                CharSequence name3 = null;
                String un = null;
                TLObject obj = getItem(i);
                if (obj instanceof User) {
                    user = (User) obj;
                    un = user.username;
                } else if (obj instanceof Chat) {
                    chat = MessagesController.getInstance(dialogsSearchAdapter.currentAccount).getChat(Integer.valueOf(((Chat) obj).id));
                    if (chat == null) {
                        chat = (Chat) obj;
                    }
                    chat3 = chat;
                    un = chat3.username;
                } else if (obj instanceof EncryptedChat) {
                    EncryptedChat encryptedChat2 = MessagesController.getInstance(dialogsSearchAdapter.currentAccount).getEncryptedChat(Integer.valueOf(((EncryptedChat) obj).id));
                    encryptedChat = encryptedChat2;
                    chat = MessagesController.getInstance(dialogsSearchAdapter.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id));
                    chat2 = null;
                    if (isRecentSearchDisplayed()) {
                        globalSearch = dialogsSearchAdapter.searchAdapterHelper.getGlobalSearch();
                        localCount = dialogsSearchAdapter.searchResult.size();
                        localServerCount = dialogsSearchAdapter.searchAdapterHelper.getLocalServerSearch().size();
                        z = (i != getItemCount() + -1 || i == (localCount + localServerCount) - 1 || i == ((localCount + (globalSearch.isEmpty() ? 0 : globalSearch.size() + 1)) + localServerCount) - 1) ? false : true;
                        cell.useSeparator = z;
                        if (i < dialogsSearchAdapter.searchResult.size()) {
                            name3 = (CharSequence) dialogsSearchAdapter.searchResultNames.get(i);
                            if (!(name3 == null || chat == null || chat.username == null || chat.username.length() <= 0)) {
                                charSequence = name3.toString();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("@");
                                stringBuilder.append(chat.username);
                                if (charSequence.startsWith(stringBuilder.toString())) {
                                    username4 = name3;
                                    name3 = null;
                                }
                            }
                        } else {
                            charSequence = dialogsSearchAdapter.searchAdapterHelper.getLastFoundUsername();
                            if (!TextUtils.isEmpty(charSequence)) {
                                if (chat == null) {
                                    nameSearch = null;
                                    nameSearchLower = null;
                                    nameSearch2 = ContactsController.formatName(chat.first_name, chat.last_name);
                                    nameSearchLower2 = nameSearch2.toLowerCase();
                                } else {
                                    nameSearch = null;
                                    nameSearchLower = null;
                                    if (chat2 == null) {
                                        nameSearch2 = chat2.title;
                                        nameSearchLower2 = nameSearch2.toLowerCase();
                                    } else {
                                        nameSearch2 = nameSearch;
                                        nameSearchLower2 = nameSearchLower;
                                        if (nameSearch2 == null) {
                                            localCount = nameSearchLower2.indexOf(charSequence);
                                            index = localCount;
                                            if (localCount == -1) {
                                                spannableStringBuilder = new SpannableStringBuilder(nameSearch2);
                                                nameSearchLower2 = index;
                                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), nameSearchLower2, charSequence.length() + nameSearchLower2, 33);
                                                name2 = spannableStringBuilder;
                                            } else {
                                                str = nameSearchLower2;
                                                i2 = localServerCount;
                                            }
                                        } else {
                                            str = nameSearchLower2;
                                            i3 = localCount;
                                            i2 = localServerCount;
                                        }
                                        if (un != null) {
                                            if (charSequence.startsWith("@")) {
                                                charSequence = charSequence.substring(1);
                                            }
                                            try {
                                                spannableStringBuilder2 = new SpannableStringBuilder();
                                                spannableStringBuilder2.append("@");
                                                spannableStringBuilder2.append(un);
                                                indexOf = un.toLowerCase().indexOf(charSequence);
                                                localCount = indexOf;
                                                if (indexOf == -1) {
                                                    indexOf = charSequence.length();
                                                    if (localCount != 0) {
                                                        indexOf++;
                                                    } else {
                                                        localCount++;
                                                    }
                                                    try {
                                                        spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), localCount, localCount + indexOf, 33);
                                                    } catch (Throwable e) {
                                                        isRecent = e;
                                                        username = un;
                                                        FileLog.m3e(isRecent);
                                                        z = false;
                                                        savedMessages = false;
                                                        if (chat == null) {
                                                            break;
                                                        }
                                                        name = name3;
                                                        if (ChatObject.isChannel(chat2)) {
                                                            break;
                                                        }
                                                        username2 = LocaleController.formatPluralString("Members", chat2.participants_count);
                                                        if (username4 instanceof SpannableStringBuilder) {
                                                            ((SpannableStringBuilder) username4).append(", ").append(username2);
                                                            username3 = username4;
                                                            if (chat == null) {
                                                            }
                                                            cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                                                            return;
                                                        }
                                                        username3 = TextUtils.isEmpty(username4) ? username2 : TextUtils.concat(new CharSequence[]{username4, ", ", username2});
                                                        cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                                                        return;
                                                    }
                                                }
                                                username = spannableStringBuilder2;
                                            } catch (Throwable e2) {
                                                String str2 = charSequence;
                                                isRecent = e2;
                                                username = un;
                                                FileLog.m3e(isRecent);
                                                z = false;
                                                savedMessages = false;
                                                if (chat == null) {
                                                }
                                                name = name3;
                                                if (ChatObject.isChannel(chat2)) {
                                                }
                                                username2 = LocaleController.formatPluralString("Members", chat2.participants_count);
                                                if (username4 instanceof SpannableStringBuilder) {
                                                    ((SpannableStringBuilder) username4).append(", ").append(username2);
                                                    username3 = username4;
                                                    if (chat == null) {
                                                    }
                                                    cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                                                    return;
                                                }
                                                if (TextUtils.isEmpty(username4)) {
                                                }
                                                if (chat == null) {
                                                }
                                                cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (nameSearch2 == null) {
                                    str = nameSearchLower2;
                                    i3 = localCount;
                                    i2 = localServerCount;
                                } else {
                                    localCount = nameSearchLower2.indexOf(charSequence);
                                    index = localCount;
                                    if (localCount == -1) {
                                        str = nameSearchLower2;
                                        i2 = localServerCount;
                                    } else {
                                        spannableStringBuilder = new SpannableStringBuilder(nameSearch2);
                                        nameSearchLower2 = index;
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), nameSearchLower2, charSequence.length() + nameSearchLower2, 33);
                                        name2 = spannableStringBuilder;
                                    }
                                }
                                if (un != null) {
                                    if (charSequence.startsWith("@")) {
                                        charSequence = charSequence.substring(1);
                                    }
                                    spannableStringBuilder2 = new SpannableStringBuilder();
                                    spannableStringBuilder2.append("@");
                                    spannableStringBuilder2.append(un);
                                    indexOf = un.toLowerCase().indexOf(charSequence);
                                    localCount = indexOf;
                                    if (indexOf == -1) {
                                    } else {
                                        indexOf = charSequence.length();
                                        if (localCount != 0) {
                                            localCount++;
                                        } else {
                                            indexOf++;
                                        }
                                        spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), localCount, localCount + indexOf, 33);
                                    }
                                    username = spannableStringBuilder2;
                                }
                            }
                        }
                        z = false;
                    } else {
                        cell.useSeparator = i == getItemCount() - 1;
                        z = true;
                    }
                    savedMessages = false;
                    if (chat == null && chat.id == dialogsSearchAdapter.selfUserId) {
                        name = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                        username4 = null;
                        savedMessages = true;
                    } else {
                        name = name3;
                    }
                    if (!(chat2 == null || chat2.participants_count == 0)) {
                        if (ChatObject.isChannel(chat2) || chat2.megagroup) {
                            username2 = LocaleController.formatPluralString("Members", chat2.participants_count);
                        } else {
                            username2 = LocaleController.formatPluralString("Subscribers", chat2.participants_count);
                        }
                        if (username4 instanceof SpannableStringBuilder) {
                            ((SpannableStringBuilder) username4).append(", ").append(username2);
                        } else {
                            if (TextUtils.isEmpty(username4)) {
                            }
                            if (chat == null) {
                            }
                            cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                            return;
                        }
                    }
                    username3 = username4;
                    if (chat == null) {
                    }
                    cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                    return;
                }
                chat = user;
                chat2 = chat3;
                encryptedChat = null;
                if (isRecentSearchDisplayed()) {
                    globalSearch = dialogsSearchAdapter.searchAdapterHelper.getGlobalSearch();
                    localCount = dialogsSearchAdapter.searchResult.size();
                    localServerCount = dialogsSearchAdapter.searchAdapterHelper.getLocalServerSearch().size();
                    if (globalSearch.isEmpty()) {
                    }
                    if (i != getItemCount() + -1) {
                        break;
                    }
                    cell.useSeparator = z;
                    if (i < dialogsSearchAdapter.searchResult.size()) {
                        name3 = (CharSequence) dialogsSearchAdapter.searchResultNames.get(i);
                        charSequence = name3.toString();
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(chat.username);
                        if (charSequence.startsWith(stringBuilder.toString())) {
                            username4 = name3;
                            name3 = null;
                        }
                        break;
                    }
                    charSequence = dialogsSearchAdapter.searchAdapterHelper.getLastFoundUsername();
                    if (TextUtils.isEmpty(charSequence)) {
                        if (chat == null) {
                            nameSearch = null;
                            nameSearchLower = null;
                            if (chat2 == null) {
                                nameSearch2 = nameSearch;
                                nameSearchLower2 = nameSearchLower;
                                if (nameSearch2 == null) {
                                    localCount = nameSearchLower2.indexOf(charSequence);
                                    index = localCount;
                                    if (localCount == -1) {
                                        spannableStringBuilder = new SpannableStringBuilder(nameSearch2);
                                        nameSearchLower2 = index;
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), nameSearchLower2, charSequence.length() + nameSearchLower2, 33);
                                        name2 = spannableStringBuilder;
                                    } else {
                                        str = nameSearchLower2;
                                        i2 = localServerCount;
                                    }
                                } else {
                                    str = nameSearchLower2;
                                    i3 = localCount;
                                    i2 = localServerCount;
                                }
                                if (un != null) {
                                    if (charSequence.startsWith("@")) {
                                        charSequence = charSequence.substring(1);
                                    }
                                    spannableStringBuilder2 = new SpannableStringBuilder();
                                    spannableStringBuilder2.append("@");
                                    spannableStringBuilder2.append(un);
                                    indexOf = un.toLowerCase().indexOf(charSequence);
                                    localCount = indexOf;
                                    if (indexOf == -1) {
                                        indexOf = charSequence.length();
                                        if (localCount != 0) {
                                            indexOf++;
                                        } else {
                                            localCount++;
                                        }
                                        spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), localCount, localCount + indexOf, 33);
                                    }
                                    username = spannableStringBuilder2;
                                }
                            } else {
                                nameSearch2 = chat2.title;
                                nameSearchLower2 = nameSearch2.toLowerCase();
                            }
                        } else {
                            nameSearch = null;
                            nameSearchLower = null;
                            nameSearch2 = ContactsController.formatName(chat.first_name, chat.last_name);
                            nameSearchLower2 = nameSearch2.toLowerCase();
                        }
                        if (nameSearch2 == null) {
                            str = nameSearchLower2;
                            i3 = localCount;
                            i2 = localServerCount;
                        } else {
                            localCount = nameSearchLower2.indexOf(charSequence);
                            index = localCount;
                            if (localCount == -1) {
                                str = nameSearchLower2;
                                i2 = localServerCount;
                            } else {
                                spannableStringBuilder = new SpannableStringBuilder(nameSearch2);
                                nameSearchLower2 = index;
                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), nameSearchLower2, charSequence.length() + nameSearchLower2, 33);
                                name2 = spannableStringBuilder;
                            }
                        }
                        if (un != null) {
                            if (charSequence.startsWith("@")) {
                                charSequence = charSequence.substring(1);
                            }
                            spannableStringBuilder2 = new SpannableStringBuilder();
                            spannableStringBuilder2.append("@");
                            spannableStringBuilder2.append(un);
                            indexOf = un.toLowerCase().indexOf(charSequence);
                            localCount = indexOf;
                            if (indexOf == -1) {
                            } else {
                                indexOf = charSequence.length();
                                if (localCount != 0) {
                                    localCount++;
                                } else {
                                    indexOf++;
                                }
                                spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), localCount, localCount + indexOf, 33);
                            }
                            username = spannableStringBuilder2;
                        }
                    }
                    z = false;
                } else {
                    if (i == getItemCount() - 1) {
                    }
                    cell.useSeparator = i == getItemCount() - 1;
                    z = true;
                }
                savedMessages = false;
                if (chat == null) {
                }
                name = name3;
                if (ChatObject.isChannel(chat2)) {
                }
                username2 = LocaleController.formatPluralString("Members", chat2.participants_count);
                if (username4 instanceof SpannableStringBuilder) {
                    if (TextUtils.isEmpty(username4)) {
                    }
                    if (chat == null) {
                    }
                    cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                    return;
                }
                ((SpannableStringBuilder) username4).append(", ").append(username2);
                username3 = username4;
                if (chat == null) {
                }
                cell.setData(chat == null ? chat : chat2, encryptedChat, name, username3, z, savedMessages);
                return;
            case 1:
                GraySectionCell cell2 = viewHolder.itemView;
                if (isRecentSearchDisplayed()) {
                    if (i < (!DataQuery.getInstance(dialogsSearchAdapter.currentAccount).hints.isEmpty() ? 2 : 0)) {
                        cell2.setText(LocaleController.getString("ChatHints", R.string.ChatHints).toUpperCase());
                    } else {
                        cell2.setText(LocaleController.getString("Recent", R.string.Recent).toUpperCase());
                    }
                    return;
                } else if (!dialogsSearchAdapter.searchResultHashtags.isEmpty()) {
                    cell2.setText(LocaleController.getString("Hashtags", R.string.Hashtags).toUpperCase());
                    return;
                } else if (dialogsSearchAdapter.searchAdapterHelper.getGlobalSearch().isEmpty() || i != dialogsSearchAdapter.searchResult.size() + dialogsSearchAdapter.searchAdapterHelper.getLocalServerSearch().size()) {
                    cell2.setText(LocaleController.getString("SearchMessages", R.string.SearchMessages));
                    return;
                } else {
                    cell2.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
                    return;
                }
            case 2:
                DialogCell cell3 = viewHolder.itemView;
                cell3.useSeparator = i != getItemCount() - 1;
                MessageObject messageObject = (MessageObject) getItem(i);
                cell3.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date);
                return;
            case 3:
                return;
            case 4:
                HashtagSearchCell cell4 = viewHolder.itemView;
                cell4.setText((CharSequence) dialogsSearchAdapter.searchResultHashtags.get(i - 1));
                cell4.setNeedDivider(i != dialogsSearchAdapter.searchResultHashtags.size());
                return;
            case 5:
                ((CategoryAdapterRecycler) viewHolder.itemView.getAdapter()).setIndex(i / 2);
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int i) {
        int i2 = 2;
        int i3 = 1;
        if (isRecentSearchDisplayed()) {
            if (DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                i2 = 0;
            }
            int offset = i2;
            if (i > offset) {
                return 0;
            }
            if (i != offset) {
                if (i % 2 != 0) {
                    return 5;
                }
            }
            return 1;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            int localCount = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            int messagesCount = this.searchResultMessages.isEmpty() ? 0 : this.searchResultMessages.size() + 1;
            if ((i >= 0 && i < localCount + localServerCount) || (i > localCount + localServerCount && i < (globalCount + localCount) + localServerCount)) {
                return 0;
            }
            if (i > (globalCount + localCount) + localServerCount && i < ((globalCount + localCount) + messagesCount) + localServerCount) {
                return 2;
            }
            if (messagesCount == 0 || i != ((globalCount + localCount) + messagesCount) + localServerCount) {
                return 1;
            }
            return 3;
        } else {
            if (i != 0) {
                i3 = 4;
            }
            return i3;
        }
    }
}
