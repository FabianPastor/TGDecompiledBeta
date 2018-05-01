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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
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

            public int compare(RecentSearchObject recentSearchObject, RecentSearchObject recentSearchObject2) {
                if (recentSearchObject.date < recentSearchObject2.date) {
                    return 1;
                }
                return recentSearchObject.date > recentSearchObject2.date ? -1 : null;
            }
        }

        C07763() {
        }

        public void run() {
            try {
                RecentSearchObject recentSearchObject;
                int i = 0;
                SQLiteCursor queryFinalized = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
                Iterable arrayList = new ArrayList();
                Iterable arrayList2 = new ArrayList();
                Iterable arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                final Object arrayList5 = new ArrayList();
                final LongSparseArray longSparseArray = new LongSparseArray();
                while (queryFinalized.next()) {
                    long longValue = queryFinalized.longValue(0);
                    int i2 = (int) longValue;
                    int i3 = (int) (longValue >> 32);
                    if (i2 == 0) {
                        if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList3.contains(Integer.valueOf(i3))) {
                            arrayList3.add(Integer.valueOf(i3));
                        }
                        i2 = 0;
                        if (i2 != 0) {
                            recentSearchObject = new RecentSearchObject();
                            recentSearchObject.did = longValue;
                            recentSearchObject.date = queryFinalized.intValue(1);
                            arrayList5.add(recentSearchObject);
                            longSparseArray.put(recentSearchObject.did, recentSearchObject);
                        }
                    } else if (i3 == 1) {
                        if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList2.contains(Integer.valueOf(i2))) {
                            arrayList2.add(Integer.valueOf(i2));
                        }
                        i2 = 0;
                        if (i2 != 0) {
                            recentSearchObject = new RecentSearchObject();
                            recentSearchObject.did = longValue;
                            recentSearchObject.date = queryFinalized.intValue(1);
                            arrayList5.add(recentSearchObject);
                            longSparseArray.put(recentSearchObject.did, recentSearchObject);
                        }
                    } else if (i2 > 0) {
                        if (!(DialogsSearchAdapter.this.dialogsType == 2 || arrayList.contains(Integer.valueOf(i2)))) {
                            arrayList.add(Integer.valueOf(i2));
                        }
                        i2 = 0;
                        if (i2 != 0) {
                            recentSearchObject = new RecentSearchObject();
                            recentSearchObject.did = longValue;
                            recentSearchObject.date = queryFinalized.intValue(1);
                            arrayList5.add(recentSearchObject);
                            longSparseArray.put(recentSearchObject.did, recentSearchObject);
                        }
                    } else {
                        i2 = -i2;
                        if (!arrayList2.contains(Integer.valueOf(i2))) {
                            arrayList2.add(Integer.valueOf(i2));
                        }
                        i2 = 0;
                        if (i2 != 0) {
                            recentSearchObject = new RecentSearchObject();
                            recentSearchObject.did = longValue;
                            recentSearchObject.date = queryFinalized.intValue(1);
                            arrayList5.add(recentSearchObject);
                            longSparseArray.put(recentSearchObject.did, recentSearchObject);
                        }
                    }
                    i2 = 1;
                    if (i2 != 0) {
                        recentSearchObject = new RecentSearchObject();
                        recentSearchObject.did = longValue;
                        recentSearchObject.date = queryFinalized.intValue(1);
                        arrayList5.add(recentSearchObject);
                        longSparseArray.put(recentSearchObject.did, recentSearchObject);
                    }
                }
                queryFinalized.dispose();
                ArrayList arrayList6 = new ArrayList();
                if (!arrayList3.isEmpty()) {
                    ArrayList arrayList7 = new ArrayList();
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getEncryptedChatsInternal(TextUtils.join(",", arrayList3), arrayList7, arrayList);
                    for (int i4 = 0; i4 < arrayList7.size(); i4++) {
                        ((RecentSearchObject) longSparseArray.get(((long) ((EncryptedChat) arrayList7.get(i4)).id) << 32)).object = (TLObject) arrayList7.get(i4);
                    }
                }
                if (!arrayList2.isEmpty()) {
                    ArrayList arrayList8 = new ArrayList();
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getChatsInternal(TextUtils.join(",", arrayList2), arrayList8);
                    for (int i5 = 0; i5 < arrayList8.size(); i5++) {
                        long j;
                        Chat chat = (Chat) arrayList8.get(i5);
                        if (chat.id > 0) {
                            j = (long) (-chat.id);
                        } else {
                            j = AndroidUtilities.makeBroadcastId(chat.id);
                        }
                        if (chat.migrated_to != null) {
                            recentSearchObject = (RecentSearchObject) longSparseArray.get(j);
                            longSparseArray.remove(j);
                            if (recentSearchObject != null) {
                                arrayList5.remove(recentSearchObject);
                            }
                        } else {
                            ((RecentSearchObject) longSparseArray.get(j)).object = chat;
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getUsersInternal(TextUtils.join(",", arrayList), arrayList6);
                    while (i < arrayList6.size()) {
                        User user = (User) arrayList6.get(i);
                        RecentSearchObject recentSearchObject2 = (RecentSearchObject) longSparseArray.get((long) user.id);
                        if (recentSearchObject2 != null) {
                            recentSearchObject2.object = user;
                        }
                        i++;
                    }
                }
                Collections.sort(arrayList5, new C07741());
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        DialogsSearchAdapter.this.setRecentSearch(arrayList5, longSparseArray);
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
            for (int i = 0; i < arrayList.size(); i++) {
                DialogsSearchAdapter.this.searchResultHashtags.add(((HashtagObject) arrayList.get(i)).hashtag);
            }
            if (DialogsSearchAdapter.this.delegate != null) {
                DialogsSearchAdapter.this.delegate.searchStateChanged(false);
            }
            DialogsSearchAdapter.this.notifyDataSetChanged();
        }
    }

    private class CategoryAdapterRecycler extends SelectionAdapter {
        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        private CategoryAdapterRecycler() {
        }

        public void setIndex(int i) {
            notifyDataSetChanged();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new HintDialogCell(DialogsSearchAdapter.this.mContext);
            viewGroup.setLayoutParams(new LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int i2;
            HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
            TL_topPeer tL_topPeer = (TL_topPeer) DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(i);
            TL_dialog tL_dialog = new TL_dialog();
            User user = null;
            if (tL_topPeer.peer.user_id != 0) {
                i2 = tL_topPeer.peer.user_id;
                user = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(tL_topPeer.peer.user_id));
                i = null;
            } else if (tL_topPeer.peer.channel_id != 0) {
                i2 = -tL_topPeer.peer.channel_id;
                i = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tL_topPeer.peer.channel_id));
            } else if (tL_topPeer.peer.chat_id != 0) {
                i2 = -tL_topPeer.peer.chat_id;
                i = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(tL_topPeer.peer.chat_id));
            } else {
                i2 = 0;
                i = 0;
            }
            hintDialogCell.setTag(Integer.valueOf(i2));
            CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            if (user != null) {
                charSequence = ContactsController.formatName(user.first_name, user.last_name);
            } else if (i != 0) {
                charSequence = i.title;
            }
            hintDialogCell.setDialog(i2, 1, charSequence);
        }

        public int getItemCount() {
            return DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public DialogsSearchAdapter(Context context, int i, int i2) {
        this.searchAdapterHelper.setDelegate(new C18911());
        this.mContext = context;
        this.needMessagesSearch = i;
        this.dialogsType = i2;
        this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        loadRecentSearch();
        DataQuery.getInstance(this.currentAccount).loadHints(1);
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
            if (this.lastMessagesSearchString == null || this.lastMessagesSearchString.length() == 0) {
                if (str != null) {
                    if (str.length() == 0) {
                    }
                }
            }
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (str != null) {
                if (str.length() != 0) {
                    final TLObject tL_messages_searchGlobal = new TL_messages_searchGlobal();
                    tL_messages_searchGlobal.limit = 20;
                    tL_messages_searchGlobal.f51q = str;
                    if (this.lastMessagesSearchString == null || !str.equals(this.lastMessagesSearchString) || this.searchResultMessages.isEmpty()) {
                        tL_messages_searchGlobal.offset_date = 0;
                        tL_messages_searchGlobal.offset_id = 0;
                        tL_messages_searchGlobal.offset_peer = new TL_inputPeerEmpty();
                    } else {
                        int i;
                        MessageObject messageObject = (MessageObject) this.searchResultMessages.get(this.searchResultMessages.size() - 1);
                        tL_messages_searchGlobal.offset_id = messageObject.getId();
                        tL_messages_searchGlobal.offset_date = messageObject.messageOwner.date;
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            i = -messageObject.messageOwner.to_id.channel_id;
                        } else if (messageObject.messageOwner.to_id.chat_id != 0) {
                            i = -messageObject.messageOwner.to_id.chat_id;
                        } else {
                            i = messageObject.messageOwner.to_id.user_id;
                        }
                        tL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
                    }
                    this.lastMessagesSearchString = str;
                    str = this.lastReqId + 1;
                    this.lastReqId = str;
                    if (this.delegate != null) {
                        this.delegate.searchStateChanged(true);
                    }
                    this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_searchGlobal, new RequestDelegate() {
                        public void run(final TLObject tLObject, final TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (str == DialogsSearchAdapter.this.lastReqId && tL_error == null) {
                                        messages_Messages messages_messages = (messages_Messages) tLObject;
                                        boolean z = true;
                                        MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                        MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(messages_messages.users, false);
                                        MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChats(messages_messages.chats, false);
                                        if (tL_messages_searchGlobal.offset_id == 0) {
                                            DialogsSearchAdapter.this.searchResultMessages.clear();
                                        }
                                        for (int i = 0; i < messages_messages.messages.size(); i++) {
                                            Message message = (Message) messages_messages.messages.get(i);
                                            DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(DialogsSearchAdapter.this.currentAccount, message, false));
                                            long dialogId = MessageObject.getDialogId(message);
                                            ConcurrentHashMap concurrentHashMap = message.out ? MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_outbox_max : MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_inbox_max;
                                            Integer num = (Integer) concurrentHashMap.get(Long.valueOf(dialogId));
                                            if (num == null) {
                                                num = Integer.valueOf(MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDialogReadMax(message.out, dialogId));
                                                concurrentHashMap.put(Long.valueOf(dialogId), num);
                                            }
                                            message.unread = num.intValue() < message.id;
                                        }
                                        DialogsSearchAdapter dialogsSearchAdapter = DialogsSearchAdapter.this;
                                        if (messages_messages.messages.size() == 20) {
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

    public void putRecentSearch(final long j, TLObject tLObject) {
        RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjectsById.get(j);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    SQLitePreparedStatement executeFast = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
                    executeFast.requery();
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, (int) (System.currentTimeMillis() / 1000));
                    executeFast.step();
                    executeFast.dispose();
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

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    private void setRecentSearch(ArrayList<RecentSearchObject> arrayList, LongSparseArray<RecentSearchObject> longSparseArray) {
        this.recentSearchObjects = arrayList;
        this.recentSearchObjectsById = longSparseArray;
        for (arrayList = null; arrayList < this.recentSearchObjects.size(); arrayList++) {
            RecentSearchObject recentSearchObject = (RecentSearchObject) this.recentSearchObjects.get(arrayList);
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

    private void searchDialogsInternal(final String str, final int i) {
        if (this.needMessagesSearch != 2) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$6$1 */
                class C07791 implements Comparator<DialogSearchResult> {
                    C07791() {
                    }

                    public int compare(DialogSearchResult dialogSearchResult, DialogSearchResult dialogSearchResult2) {
                        if (dialogSearchResult.date < dialogSearchResult2.date) {
                            return 1;
                        }
                        return dialogSearchResult.date > dialogSearchResult2.date ? -1 : null;
                    }
                }

                public void run() {
                    C07806 c07806 = this;
                    try {
                        CharSequence toLowerCase = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages).toLowerCase();
                        String toLowerCase2 = str.trim().toLowerCase();
                        if (toLowerCase2.length() == 0) {
                            DialogsSearchAdapter.this.lastSearchId = -1;
                            DialogsSearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), DialogsSearchAdapter.this.lastSearchId);
                            return;
                        }
                        int i;
                        int i2;
                        DialogSearchResult dialogSearchResult;
                        int i3;
                        SQLiteCursor queryFinalized;
                        String stringValue;
                        String translitString;
                        int lastIndexOf;
                        StringBuilder stringBuilder;
                        DialogSearchResult dialogSearchResult2;
                        String stringBuilder2;
                        String str;
                        StringBuilder stringBuilder3;
                        AbstractSerializedData byteBufferValue;
                        int lastIndexOf2;
                        String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                        if (toLowerCase2.equals(translitString2) || translitString2.length() == 0) {
                            translitString2 = null;
                        }
                        int i4 = 0;
                        String[] strArr = new String[((translitString2 != null ? 1 : 0) + 1)];
                        strArr[0] = toLowerCase2;
                        if (translitString2 != null) {
                            strArr[1] = translitString2;
                        }
                        Iterable arrayList = new ArrayList();
                        Iterable arrayList2 = new ArrayList();
                        Iterable arrayList3 = new ArrayList();
                        ArrayList arrayList4 = new ArrayList();
                        LongSparseArray longSparseArray = new LongSparseArray();
                        SQLiteCursor queryFinalized2 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600", new Object[0]);
                        while (queryFinalized2.next()) {
                            long longValue = queryFinalized2.longValue(i4);
                            DialogSearchResult dialogSearchResult3 = new DialogSearchResult();
                            dialogSearchResult3.date = queryFinalized2.intValue(1);
                            longSparseArray.put(longValue, dialogSearchResult3);
                            i = (int) longValue;
                            i2 = (int) (longValue >> 32);
                            if (i != 0) {
                                if (i2 == 1) {
                                    if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList2.contains(Integer.valueOf(i))) {
                                        arrayList2.add(Integer.valueOf(i));
                                    }
                                } else if (i <= 0) {
                                    i2 = -i;
                                    if (!arrayList2.contains(Integer.valueOf(i2))) {
                                        arrayList2.add(Integer.valueOf(i2));
                                    }
                                } else if (!(DialogsSearchAdapter.this.dialogsType == 2 || arrayList.contains(Integer.valueOf(i)))) {
                                    arrayList.add(Integer.valueOf(i));
                                }
                            } else if (DialogsSearchAdapter.this.dialogsType == 0 && !arrayList3.contains(Integer.valueOf(i2))) {
                                arrayList3.add(Integer.valueOf(i2));
                            }
                            i4 = 0;
                        }
                        queryFinalized2.dispose();
                        if (toLowerCase.startsWith(toLowerCase2)) {
                            TLObject currentUser = UserConfig.getInstance(DialogsSearchAdapter.this.currentAccount).getCurrentUser();
                            dialogSearchResult = new DialogSearchResult();
                            dialogSearchResult.date = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            dialogSearchResult.name = toLowerCase;
                            dialogSearchResult.object = currentUser;
                            longSparseArray.put((long) currentUser.id, dialogSearchResult);
                            i3 = 1;
                        } else {
                            i3 = 0;
                        }
                        if (!arrayList.isEmpty()) {
                            queryFinalized = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
                            while (queryFinalized.next()) {
                                stringValue = queryFinalized.stringValue(2);
                                translitString = LocaleController.getInstance().getTranslitString(stringValue);
                                if (stringValue.equals(translitString)) {
                                    translitString = null;
                                }
                                lastIndexOf = stringValue.lastIndexOf(";;;");
                                translitString2 = lastIndexOf != -1 ? stringValue.substring(lastIndexOf + 3) : null;
                                i2 = strArr.length;
                                i4 = 0;
                                Object obj = null;
                                while (i4 < i2) {
                                    int i5;
                                    AbstractSerializedData byteBufferValue2;
                                    TLObject TLdeserialize;
                                    StringBuilder stringBuilder4;
                                    String str2 = strArr[i4];
                                    if (stringValue.startsWith(str2)) {
                                        i5 = i2;
                                    } else {
                                        i5 = i2;
                                        stringBuilder = new StringBuilder();
                                        Object obj2 = obj;
                                        stringBuilder.append(" ");
                                        stringBuilder.append(str2);
                                        if (!stringValue.contains(stringBuilder.toString())) {
                                            if (translitString != null) {
                                                if (!translitString.startsWith(str2)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(str2);
                                                    if (translitString.contains(stringBuilder.toString())) {
                                                    }
                                                }
                                            }
                                            obj = (translitString2 == null || !translitString2.startsWith(str2)) ? obj2 : 2;
                                            if (i == null) {
                                                byteBufferValue2 = queryFinalized.byteBufferValue(0);
                                                if (byteBufferValue2 != null) {
                                                    TLdeserialize = User.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                                    byteBufferValue2.reuse();
                                                    dialogSearchResult2 = (DialogSearchResult) longSparseArray.get((long) TLdeserialize.id);
                                                    if (TLdeserialize.status != null) {
                                                        TLdeserialize.status.expires = queryFinalized.intValue(1);
                                                    }
                                                    if (i != 1) {
                                                        dialogSearchResult2.name = AndroidUtilities.generateSearchName(TLdeserialize.first_name, TLdeserialize.last_name, str2);
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(TLdeserialize.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder4 = new StringBuilder();
                                                        stringBuilder4.append("@");
                                                        stringBuilder4.append(str2);
                                                        dialogSearchResult2.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder4.toString());
                                                    }
                                                    dialogSearchResult2.object = TLdeserialize;
                                                    i3++;
                                                }
                                            } else {
                                                i4++;
                                                i2 = i5;
                                            }
                                        }
                                    }
                                    obj = 1;
                                    if (i == null) {
                                        i4++;
                                        i2 = i5;
                                    } else {
                                        byteBufferValue2 = queryFinalized.byteBufferValue(0);
                                        if (byteBufferValue2 != null) {
                                            TLdeserialize = User.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                                            byteBufferValue2.reuse();
                                            dialogSearchResult2 = (DialogSearchResult) longSparseArray.get((long) TLdeserialize.id);
                                            if (TLdeserialize.status != null) {
                                                TLdeserialize.status.expires = queryFinalized.intValue(1);
                                            }
                                            if (i != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(TLdeserialize.username);
                                                stringBuilder2 = stringBuilder.toString();
                                                stringBuilder4 = new StringBuilder();
                                                stringBuilder4.append("@");
                                                stringBuilder4.append(str2);
                                                dialogSearchResult2.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder4.toString());
                                            } else {
                                                dialogSearchResult2.name = AndroidUtilities.generateSearchName(TLdeserialize.first_name, TLdeserialize.last_name, str2);
                                            }
                                            dialogSearchResult2.object = TLdeserialize;
                                            i3++;
                                        }
                                    }
                                }
                            }
                            queryFinalized.dispose();
                        }
                        if (!arrayList2.isEmpty()) {
                            queryFinalized = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[]{TextUtils.join(",", arrayList2)}), new Object[0]);
                            while (queryFinalized.next()) {
                                stringValue = queryFinalized.stringValue(1);
                                translitString = LocaleController.getInstance().getTranslitString(stringValue);
                                if (stringValue.equals(translitString)) {
                                    translitString = null;
                                }
                                lastIndexOf = strArr.length;
                                i2 = 0;
                                while (i2 < lastIndexOf) {
                                    str = strArr[i2];
                                    if (!stringValue.startsWith(str)) {
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(" ");
                                        stringBuilder3.append(str);
                                        if (!stringValue.contains(stringBuilder3.toString())) {
                                            if (translitString != null) {
                                                if (!translitString.startsWith(str)) {
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(" ");
                                                    stringBuilder3.append(str);
                                                    if (translitString.contains(stringBuilder3.toString())) {
                                                    }
                                                }
                                            }
                                            i2++;
                                        }
                                    }
                                    byteBufferValue = queryFinalized.byteBufferValue(0);
                                    if (byteBufferValue != null) {
                                        TLObject TLdeserialize2 = Chat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                        byteBufferValue.reuse();
                                        if (!(TLdeserialize2 == null || TLdeserialize2.deactivated)) {
                                            if (!ChatObject.isChannel(TLdeserialize2) || !ChatObject.isNotInChat(TLdeserialize2)) {
                                                long j;
                                                if (TLdeserialize2.id > 0) {
                                                    j = (long) (-TLdeserialize2.id);
                                                } else {
                                                    j = AndroidUtilities.makeBroadcastId(TLdeserialize2.id);
                                                }
                                                dialogSearchResult2 = (DialogSearchResult) longSparseArray.get(j);
                                                dialogSearchResult2.name = AndroidUtilities.generateSearchName(TLdeserialize2.title, null, str);
                                                dialogSearchResult2.object = TLdeserialize2;
                                                i3++;
                                            }
                                        }
                                    }
                                }
                            }
                            queryFinalized.dispose();
                        }
                        lastIndexOf = 3;
                        if (!arrayList3.isEmpty()) {
                            queryFinalized = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)", new Object[]{TextUtils.join(",", arrayList3)}), new Object[0]);
                            while (queryFinalized.next()) {
                                stringBuilder2 = queryFinalized.stringValue(1);
                                translitString = LocaleController.getInstance().getTranslitString(stringBuilder2);
                                if (stringBuilder2.equals(translitString)) {
                                    translitString = null;
                                }
                                int lastIndexOf3 = stringBuilder2.lastIndexOf(";;;");
                                stringValue = lastIndexOf3 != -1 ? stringBuilder2.substring(lastIndexOf3 + 2) : null;
                                i4 = 0;
                                Object obj3 = null;
                                while (i4 < strArr.length) {
                                    TLObject TLdeserialize3;
                                    User TLdeserialize4;
                                    String str3 = strArr[i4];
                                    if (!stringBuilder2.startsWith(str3)) {
                                        StringBuilder stringBuilder5 = new StringBuilder();
                                        stringBuilder5.append(" ");
                                        stringBuilder5.append(str3);
                                        if (!stringBuilder2.contains(stringBuilder5.toString())) {
                                            if (translitString != null) {
                                                if (!translitString.startsWith(str3)) {
                                                    stringBuilder5 = new StringBuilder();
                                                    stringBuilder5.append(" ");
                                                    stringBuilder5.append(str3);
                                                    if (translitString.contains(stringBuilder5.toString())) {
                                                    }
                                                }
                                            }
                                            if (stringValue != null && stringValue.startsWith(str3)) {
                                                obj3 = 2;
                                            }
                                            if (lastIndexOf2 == null) {
                                                byteBufferValue = queryFinalized.byteBufferValue(0);
                                                if (byteBufferValue == null) {
                                                    TLdeserialize3 = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                    byteBufferValue.reuse();
                                                } else {
                                                    TLdeserialize3 = null;
                                                }
                                                byteBufferValue = queryFinalized.byteBufferValue(6);
                                                if (byteBufferValue == null) {
                                                    TLdeserialize4 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                                    byteBufferValue.reuse();
                                                } else {
                                                    TLdeserialize4 = null;
                                                }
                                                if (!(TLdeserialize3 == null || TLdeserialize4 == null)) {
                                                    dialogSearchResult = (DialogSearchResult) longSparseArray.get(((long) TLdeserialize3.id) << 32);
                                                    TLdeserialize3.user_id = queryFinalized.intValue(2);
                                                    TLdeserialize3.a_or_b = queryFinalized.byteArrayValue(3);
                                                    TLdeserialize3.auth_key = queryFinalized.byteArrayValue(4);
                                                    TLdeserialize3.ttl = queryFinalized.intValue(5);
                                                    TLdeserialize3.layer = queryFinalized.intValue(8);
                                                    TLdeserialize3.seq_in = queryFinalized.intValue(9);
                                                    TLdeserialize3.seq_out = queryFinalized.intValue(10);
                                                    i2 = queryFinalized.intValue(11);
                                                    TLdeserialize3.key_use_count_in = (short) (i2 >> 16);
                                                    TLdeserialize3.key_use_count_out = (short) i2;
                                                    TLdeserialize3.exchange_id = queryFinalized.longValue(12);
                                                    TLdeserialize3.key_create_date = queryFinalized.intValue(13);
                                                    TLdeserialize3.future_key_fingerprint = queryFinalized.longValue(14);
                                                    TLdeserialize3.future_auth_key = queryFinalized.byteArrayValue(15);
                                                    TLdeserialize3.key_hash = queryFinalized.byteArrayValue(16);
                                                    TLdeserialize3.in_seq_no = queryFinalized.intValue(17);
                                                    i2 = queryFinalized.intValue(18);
                                                    if (i2 != 0) {
                                                        TLdeserialize3.admin_id = i2;
                                                    }
                                                    TLdeserialize3.mtproto_seq = queryFinalized.intValue(19);
                                                    if (TLdeserialize4.status != null) {
                                                        TLdeserialize4.status.expires = queryFinalized.intValue(7);
                                                    }
                                                    if (lastIndexOf2 != 1) {
                                                        dialogSearchResult.name = new SpannableStringBuilder(ContactsController.formatName(TLdeserialize4.first_name, TLdeserialize4.last_name));
                                                        ((SpannableStringBuilder) dialogSearchResult.name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_secretName)), 0, dialogSearchResult.name.length(), 33);
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(TLdeserialize4.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(str3);
                                                        dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString());
                                                    }
                                                    dialogSearchResult.object = TLdeserialize3;
                                                    arrayList4.add(TLdeserialize4);
                                                    i3++;
                                                }
                                            } else {
                                                i4++;
                                            }
                                        }
                                    }
                                    obj3 = 1;
                                    if (lastIndexOf2 == null) {
                                        i4++;
                                    } else {
                                        byteBufferValue = queryFinalized.byteBufferValue(0);
                                        if (byteBufferValue == null) {
                                            TLdeserialize3 = null;
                                        } else {
                                            TLdeserialize3 = EncryptedChat.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                            byteBufferValue.reuse();
                                        }
                                        byteBufferValue = queryFinalized.byteBufferValue(6);
                                        if (byteBufferValue == null) {
                                            TLdeserialize4 = null;
                                        } else {
                                            TLdeserialize4 = User.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                            byteBufferValue.reuse();
                                        }
                                        dialogSearchResult = (DialogSearchResult) longSparseArray.get(((long) TLdeserialize3.id) << 32);
                                        TLdeserialize3.user_id = queryFinalized.intValue(2);
                                        TLdeserialize3.a_or_b = queryFinalized.byteArrayValue(3);
                                        TLdeserialize3.auth_key = queryFinalized.byteArrayValue(4);
                                        TLdeserialize3.ttl = queryFinalized.intValue(5);
                                        TLdeserialize3.layer = queryFinalized.intValue(8);
                                        TLdeserialize3.seq_in = queryFinalized.intValue(9);
                                        TLdeserialize3.seq_out = queryFinalized.intValue(10);
                                        i2 = queryFinalized.intValue(11);
                                        TLdeserialize3.key_use_count_in = (short) (i2 >> 16);
                                        TLdeserialize3.key_use_count_out = (short) i2;
                                        TLdeserialize3.exchange_id = queryFinalized.longValue(12);
                                        TLdeserialize3.key_create_date = queryFinalized.intValue(13);
                                        TLdeserialize3.future_key_fingerprint = queryFinalized.longValue(14);
                                        TLdeserialize3.future_auth_key = queryFinalized.byteArrayValue(15);
                                        TLdeserialize3.key_hash = queryFinalized.byteArrayValue(16);
                                        TLdeserialize3.in_seq_no = queryFinalized.intValue(17);
                                        i2 = queryFinalized.intValue(18);
                                        if (i2 != 0) {
                                            TLdeserialize3.admin_id = i2;
                                        }
                                        TLdeserialize3.mtproto_seq = queryFinalized.intValue(19);
                                        if (TLdeserialize4.status != null) {
                                            TLdeserialize4.status.expires = queryFinalized.intValue(7);
                                        }
                                        if (lastIndexOf2 != 1) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(TLdeserialize4.username);
                                            stringBuilder2 = stringBuilder.toString();
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append("@");
                                            stringBuilder3.append(str3);
                                            dialogSearchResult.name = AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString());
                                        } else {
                                            dialogSearchResult.name = new SpannableStringBuilder(ContactsController.formatName(TLdeserialize4.first_name, TLdeserialize4.last_name));
                                            ((SpannableStringBuilder) dialogSearchResult.name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chats_secretName)), 0, dialogSearchResult.name.length(), 33);
                                        }
                                        dialogSearchResult.object = TLdeserialize3;
                                        arrayList4.add(TLdeserialize4);
                                        i3++;
                                    }
                                }
                            }
                            queryFinalized.dispose();
                        }
                        ArrayList arrayList5 = new ArrayList(i3);
                        for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                            dialogSearchResult = (DialogSearchResult) longSparseArray.valueAt(i3);
                            if (!(dialogSearchResult.object == null || dialogSearchResult.name == null)) {
                                arrayList5.add(dialogSearchResult);
                            }
                        }
                        Collections.sort(arrayList5, new C07791());
                        ArrayList arrayList6 = new ArrayList();
                        ArrayList arrayList7 = new ArrayList();
                        for (i2 = 0; i2 < arrayList5.size(); i2++) {
                            DialogSearchResult dialogSearchResult4 = (DialogSearchResult) arrayList5.get(i2);
                            arrayList6.add(dialogSearchResult4.object);
                            arrayList7.add(dialogSearchResult4.name);
                        }
                        if (DialogsSearchAdapter.this.dialogsType != 2) {
                            queryFinalized = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                            while (queryFinalized.next()) {
                                if (longSparseArray.indexOfKey((long) queryFinalized.intValue(lastIndexOf)) < 0) {
                                    stringBuilder2 = queryFinalized.stringValue(2);
                                    translitString = LocaleController.getInstance().getTranslitString(stringBuilder2);
                                    if (stringBuilder2.equals(translitString)) {
                                        translitString = null;
                                    }
                                    lastIndexOf2 = stringBuilder2.lastIndexOf(";;;");
                                    String substring = lastIndexOf2 != -1 ? stringBuilder2.substring(lastIndexOf2 + 3) : null;
                                    i = strArr.length;
                                    int i6 = 0;
                                    Object obj4 = null;
                                    while (i6 < i) {
                                        StringBuilder stringBuilder6;
                                        Object obj5;
                                        AbstractSerializedData byteBufferValue3;
                                        User TLdeserialize5;
                                        String str4;
                                        translitString2 = strArr[i6];
                                        if (!stringBuilder2.startsWith(translitString2)) {
                                            stringBuilder6 = new StringBuilder();
                                            stringBuilder6.append(" ");
                                            stringBuilder6.append(translitString2);
                                            if (!stringBuilder2.contains(stringBuilder6.toString())) {
                                                if (translitString != null) {
                                                    if (!translitString.startsWith(translitString2)) {
                                                        stringBuilder6 = new StringBuilder();
                                                        stringBuilder6.append(" ");
                                                        stringBuilder6.append(translitString2);
                                                        if (translitString.contains(stringBuilder6.toString())) {
                                                        }
                                                    }
                                                }
                                                obj5 = (substring == null || !substring.startsWith(translitString2)) ? obj4 : 2;
                                                if (obj5 == null) {
                                                    byteBufferValue3 = queryFinalized.byteBufferValue(0);
                                                    if (byteBufferValue3 == null) {
                                                        TLdeserialize5 = User.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(false), false);
                                                        byteBufferValue3.reuse();
                                                        if (TLdeserialize5.status != null) {
                                                            TLdeserialize5.status.expires = queryFinalized.intValue(1);
                                                        }
                                                        if (obj5 != 1) {
                                                            arrayList7.add(AndroidUtilities.generateSearchName(TLdeserialize5.first_name, TLdeserialize5.last_name, translitString2));
                                                            substring = null;
                                                        } else {
                                                            stringBuilder6 = new StringBuilder();
                                                            stringBuilder6.append("@");
                                                            stringBuilder6.append(TLdeserialize5.username);
                                                            str = stringBuilder6.toString();
                                                            stringBuilder3 = new StringBuilder();
                                                            stringBuilder3.append("@");
                                                            stringBuilder3.append(translitString2);
                                                            translitString2 = stringBuilder3.toString();
                                                            substring = null;
                                                            arrayList7.add(AndroidUtilities.generateSearchName(str, null, translitString2));
                                                        }
                                                        arrayList6.add(TLdeserialize5);
                                                        lastIndexOf = 1;
                                                        str4 = substring;
                                                        lastIndexOf = 3;
                                                    } else {
                                                        lastIndexOf = 3;
                                                    }
                                                } else {
                                                    i6++;
                                                    obj4 = obj5;
                                                }
                                            }
                                        }
                                        obj5 = 1;
                                        if (obj5 == null) {
                                            i6++;
                                            obj4 = obj5;
                                        } else {
                                            byteBufferValue3 = queryFinalized.byteBufferValue(0);
                                            if (byteBufferValue3 == null) {
                                                lastIndexOf = 3;
                                            } else {
                                                TLdeserialize5 = User.TLdeserialize(byteBufferValue3, byteBufferValue3.readInt32(false), false);
                                                byteBufferValue3.reuse();
                                                if (TLdeserialize5.status != null) {
                                                    TLdeserialize5.status.expires = queryFinalized.intValue(1);
                                                }
                                                if (obj5 != 1) {
                                                    stringBuilder6 = new StringBuilder();
                                                    stringBuilder6.append("@");
                                                    stringBuilder6.append(TLdeserialize5.username);
                                                    str = stringBuilder6.toString();
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("@");
                                                    stringBuilder3.append(translitString2);
                                                    translitString2 = stringBuilder3.toString();
                                                    substring = null;
                                                    arrayList7.add(AndroidUtilities.generateSearchName(str, null, translitString2));
                                                } else {
                                                    arrayList7.add(AndroidUtilities.generateSearchName(TLdeserialize5.first_name, TLdeserialize5.last_name, translitString2));
                                                    substring = null;
                                                }
                                                arrayList6.add(TLdeserialize5);
                                                lastIndexOf = 1;
                                                str4 = substring;
                                                lastIndexOf = 3;
                                            }
                                        }
                                    }
                                    lastIndexOf = 3;
                                }
                            }
                            queryFinalized.dispose();
                        }
                        DialogsSearchAdapter.this.updateSearchResults(arrayList6, arrayList7, arrayList4, i);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
        }
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<User> arrayList3, int i) {
        final int i2 = i;
        final ArrayList<TLObject> arrayList4 = arrayList;
        final ArrayList<User> arrayList5 = arrayList3;
        final ArrayList<CharSequence> arrayList6 = arrayList2;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (i2 == DialogsSearchAdapter.this.lastSearchId) {
                    for (int i = 0; i < arrayList4.size(); i++) {
                        TLObject tLObject = (TLObject) arrayList4.get(i);
                        if (tLObject instanceof User) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUser((User) tLObject, true);
                        } else if (tLObject instanceof Chat) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChat((Chat) tLObject, true);
                        } else if (tLObject instanceof EncryptedChat) {
                            MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putEncryptedChat((EncryptedChat) tLObject, true);
                        }
                    }
                    MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(arrayList5, true);
                    DialogsSearchAdapter.this.searchResult = arrayList4;
                    DialogsSearchAdapter.this.searchResultNames = arrayList6;
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

    public void searchDialogs(final String str) {
        if (str == null || this.lastSearchText == null || !str.equals(this.lastSearchText)) {
            this.lastSearchText = str;
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                    this.searchTimer = null;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str != null) {
                if (str.length() != 0) {
                    if (this.needMessagesSearch != 2 && str.startsWith("#") && str.length() == 1) {
                        this.messagesSearchEndReached = true;
                        if (this.searchAdapterHelper.loadRecentHashtags()) {
                            this.searchResultMessages.clear();
                            this.searchResultHashtags.clear();
                            ArrayList hashtags = this.searchAdapterHelper.getHashtags();
                            for (int i = 0; i < hashtags.size(); i++) {
                                this.searchResultHashtags.add(((HashtagObject) hashtags.get(i)).hashtag);
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
                    final int i2 = this.lastSearchId + 1;
                    this.lastSearchId = i2;
                    this.searchTimer = new Timer();
                    this.searchTimer.schedule(new TimerTask() {

                        /* renamed from: org.telegram.ui.Adapters.DialogsSearchAdapter$8$1 */
                        class C07821 implements Runnable {
                            C07821() {
                            }

                            public void run() {
                                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                                    DialogsSearchAdapter.this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, 0, false);
                                }
                                DialogsSearchAdapter.this.searchMessagesInternal(str);
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
                            DialogsSearchAdapter.this.searchDialogsInternal(str, i2);
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Object getItem(int i) {
        int i2 = 0;
        if (isRecentSearchDisplayed()) {
            if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                i2 = 2;
            }
            if (i > i2) {
                i = (i - 1) - i2;
                if (i < this.recentSearchObjects.size()) {
                    User user;
                    i = ((RecentSearchObject) this.recentSearchObjects.get(i)).object;
                    if (i instanceof User) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((User) i).id));
                        if (user != null) {
                        }
                        return i;
                    }
                    if (i instanceof Chat) {
                        user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((Chat) i).id));
                    }
                    return i;
                    i = user;
                    return i;
                }
            }
            return null;
        } else if (this.searchResultHashtags.isEmpty()) {
            ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
            ArrayList localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            int size = this.searchResult.size();
            int size2 = localServerSearch.size();
            int size3 = globalSearch.isEmpty() ? 0 : globalSearch.size() + 1;
            if (!this.searchResultMessages.isEmpty()) {
                i2 = this.searchResultMessages.size() + 1;
            }
            if (i >= 0 && i < size) {
                return this.searchResult.get(i);
            }
            if (i >= size && i < size2 + size) {
                return localServerSearch.get(i - size);
            }
            if (i > size + size2 && i < (size3 + size) + size2) {
                return globalSearch.get(((i - size) - size2) - 1);
            }
            int i3 = size3 + size;
            if (i <= i3 + size2 || i >= (i3 + r2) + size2) {
                return null;
            }
            return this.searchResultMessages.get((((i - size) - size3) - size2) - 1);
        } else if (i > 0) {
            return this.searchResultHashtags.get(i - 1);
        } else {
            return null;
        }
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        viewHolder = viewHolder.getItemViewType();
        return (viewHolder == 1 || viewHolder == 3) ? false : true;
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
                view = new DialogCell(this.mContext, false);
                break;
            case 3:
                view = new LoadingCell(this.mContext);
                break;
            case 4:
                view = new HashtagSearchCell(this.mContext);
                break;
            case 5:
                View c23569 = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                c23569.setTag(Integer.valueOf(9));
                c23569.setItemAnimator(null);
                c23569.setLayoutAnimation(null);
                LayoutManager anonymousClass10 = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                anonymousClass10.setOrientation(0);
                c23569.setLayoutManager(anonymousClass10);
                c23569.setAdapter(new CategoryAdapterRecycler());
                c23569.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        if (DialogsSearchAdapter.this.delegate != 0) {
                            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog((long) ((Integer) view.getTag()).intValue());
                        }
                    }
                });
                c23569.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int i) {
                        if (DialogsSearchAdapter.this.delegate != 0) {
                            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer) view.getTag()).intValue());
                        }
                        return true;
                    }
                });
                this.innerListView = c23569;
                view = c23569;
                break;
            default:
                break;
        }
        if (i == 5) {
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(NUM)));
        } else {
            view.setLayoutParams(new LayoutParams(-1, -2));
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int i2 = 2;
        boolean z = false;
        switch (viewHolder.getItemViewType()) {
            case 0:
                Object obj;
                TLObject tLObject;
                TLObject tLObject2;
                boolean z2;
                SpannableStringBuilder spannableStringBuilder;
                SpannableStringBuilder string;
                boolean z3;
                CharSequence formatPluralString;
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                viewHolder = getItem(i);
                if (viewHolder instanceof User) {
                    viewHolder = (User) viewHolder;
                    obj = viewHolder.username;
                    tLObject = null;
                    tLObject2 = tLObject;
                } else if (viewHolder instanceof Chat) {
                    viewHolder = (Chat) viewHolder;
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(viewHolder.id));
                    if (chat != null) {
                        viewHolder = chat;
                    }
                    obj = viewHolder.username;
                    tLObject2 = null;
                    tLObject = viewHolder;
                    viewHolder = tLObject2;
                } else if (viewHolder instanceof EncryptedChat) {
                    viewHolder = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(((EncryptedChat) viewHolder).id));
                    tLObject2 = viewHolder;
                    viewHolder = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(viewHolder.user_id));
                    tLObject = null;
                    obj = tLObject;
                } else {
                    viewHolder = null;
                    tLObject = viewHolder;
                    obj = tLObject;
                    tLObject2 = obj;
                }
                if (isRecentSearchDisplayed()) {
                    profileSearchCell.useSeparator = i != getItemCount() - 1 ? 1 : 0;
                    z2 = true;
                } else {
                    ArrayList globalSearch = this.searchAdapterHelper.getGlobalSearch();
                    int size = this.searchResult.size();
                    int size2 = this.searchAdapterHelper.getLocalServerSearch().size();
                    boolean z4 = (i == getItemCount() - 1 || i == (size + size2) - 1 || i == ((size + (globalSearch.isEmpty() ? 0 : globalSearch.size() + 1)) + size2) - 1) ? false : true;
                    profileSearchCell.useSeparator = z4;
                    if (i < this.searchResult.size()) {
                        i = (CharSequence) this.searchResultNames.get(i);
                        if (!(i == null || viewHolder == null || viewHolder.username == null || viewHolder.username.length() <= 0)) {
                            String charSequence = i.toString();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("@");
                            stringBuilder.append(viewHolder.username);
                            if (charSequence.startsWith(stringBuilder.toString())) {
                                z2 = false;
                            }
                        }
                        spannableStringBuilder = i;
                        z2 = false;
                        i = 0;
                        if (viewHolder == null && viewHolder.id == this.selfUserId) {
                            string = LocaleController.getString("SavedMessages", C0446R.string.SavedMessages);
                            z3 = true;
                            i = 0;
                        } else {
                            z3 = false;
                            string = spannableStringBuilder;
                        }
                        if (!(tLObject == null || tLObject.participants_count == 0)) {
                            if (ChatObject.isChannel(tLObject) || tLObject.megagroup) {
                                formatPluralString = LocaleController.formatPluralString("Members", tLObject.participants_count);
                            } else {
                                formatPluralString = LocaleController.formatPluralString("Subscribers", tLObject.participants_count);
                            }
                            if (i instanceof SpannableStringBuilder) {
                                ((SpannableStringBuilder) i).append(", ").append(formatPluralString);
                            } else {
                                i = TextUtils.isEmpty(i) ? TextUtils.concat(new CharSequence[]{i, ", ", formatPluralString}) : formatPluralString;
                            }
                        }
                        profileSearchCell.setData(viewHolder != null ? viewHolder : tLObject, tLObject2, string, i, z2, z3);
                        return;
                    }
                    i = this.searchAdapterHelper.getLastFoundUsername();
                    if (!TextUtils.isEmpty(i)) {
                        CharSequence formatName;
                        String toLowerCase;
                        if (viewHolder != null) {
                            formatName = ContactsController.formatName(viewHolder.first_name, viewHolder.last_name);
                            toLowerCase = formatName.toLowerCase();
                        } else if (tLObject != null) {
                            formatName = tLObject.title;
                            toLowerCase = formatName.toLowerCase();
                        } else {
                            formatName = null;
                            toLowerCase = formatName;
                        }
                        if (formatName != null) {
                            size = toLowerCase.indexOf(i);
                            if (size != -1) {
                                spannableStringBuilder = new SpannableStringBuilder(formatName);
                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), size, i.length() + size, 33);
                                z2 = false;
                                i = 0;
                                if (viewHolder == null) {
                                    break;
                                }
                                z3 = false;
                                string = spannableStringBuilder;
                                if (ChatObject.isChannel(tLObject)) {
                                    break;
                                }
                                formatPluralString = LocaleController.formatPluralString("Members", tLObject.participants_count);
                                if (i instanceof SpannableStringBuilder) {
                                    ((SpannableStringBuilder) i).append(", ").append(formatPluralString);
                                } else if (TextUtils.isEmpty(i)) {
                                }
                                if (viewHolder != null) {
                                }
                                profileSearchCell.setData(viewHolder != null ? viewHolder : tLObject, tLObject2, string, i, z2, z3);
                                return;
                            }
                        }
                        if (obj != null) {
                            if (i.startsWith("@")) {
                                i = i.substring(1);
                            }
                            try {
                                string = new SpannableStringBuilder();
                                string.append("@");
                                string.append(obj);
                                size = obj.toLowerCase().indexOf(i);
                                if (size != -1) {
                                    i = i.length();
                                    if (size == 0) {
                                        i++;
                                    } else {
                                        size++;
                                    }
                                    string.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), size, i + size, 33);
                                }
                                z2 = false;
                                spannableStringBuilder = null;
                                i = string;
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                z2 = false;
                                i = obj;
                            }
                            if (viewHolder == null) {
                            }
                            z3 = false;
                            string = spannableStringBuilder;
                            if (ChatObject.isChannel(tLObject)) {
                            }
                            formatPluralString = LocaleController.formatPluralString("Members", tLObject.participants_count);
                            if (i instanceof SpannableStringBuilder) {
                                ((SpannableStringBuilder) i).append(", ").append(formatPluralString);
                            } else if (TextUtils.isEmpty(i)) {
                            }
                            if (viewHolder != null) {
                            }
                            profileSearchCell.setData(viewHolder != null ? viewHolder : tLObject, tLObject2, string, i, z2, z3);
                            return;
                        }
                    }
                    z2 = false;
                    spannableStringBuilder = null;
                    if (viewHolder == null) {
                    }
                    z3 = false;
                    string = spannableStringBuilder;
                    if (ChatObject.isChannel(tLObject)) {
                    }
                    formatPluralString = LocaleController.formatPluralString("Members", tLObject.participants_count);
                    if (i instanceof SpannableStringBuilder) {
                        ((SpannableStringBuilder) i).append(", ").append(formatPluralString);
                    } else if (TextUtils.isEmpty(i)) {
                    }
                    if (viewHolder != null) {
                    }
                    profileSearchCell.setData(viewHolder != null ? viewHolder : tLObject, tLObject2, string, i, z2, z3);
                    return;
                }
                i = 0;
                spannableStringBuilder = i;
                if (viewHolder == null) {
                }
                z3 = false;
                string = spannableStringBuilder;
                if (ChatObject.isChannel(tLObject)) {
                }
                formatPluralString = LocaleController.formatPluralString("Members", tLObject.participants_count);
                if (i instanceof SpannableStringBuilder) {
                    ((SpannableStringBuilder) i).append(", ").append(formatPluralString);
                } else if (TextUtils.isEmpty(i)) {
                }
                if (viewHolder != null) {
                }
                profileSearchCell.setData(viewHolder != null ? viewHolder : tLObject, tLObject2, string, i, z2, z3);
                return;
            case 1:
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                if (isRecentSearchDisplayed()) {
                    if (DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
                        i2 = 0;
                    }
                    if (i < i2) {
                        graySectionCell.setText(LocaleController.getString("ChatHints", C0446R.string.ChatHints).toUpperCase());
                        return;
                    } else {
                        graySectionCell.setText(LocaleController.getString("Recent", C0446R.string.Recent).toUpperCase());
                        return;
                    }
                } else if (!this.searchResultHashtags.isEmpty()) {
                    graySectionCell.setText(LocaleController.getString("Hashtags", C0446R.string.Hashtags).toUpperCase());
                    return;
                } else if (this.searchAdapterHelper.getGlobalSearch().isEmpty() || i != this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()) {
                    graySectionCell.setText(LocaleController.getString("SearchMessages", C0446R.string.SearchMessages));
                    return;
                } else {
                    graySectionCell.setText(LocaleController.getString("GlobalSearch", C0446R.string.GlobalSearch));
                    return;
                }
            case 2:
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                if (i != getItemCount() - 1) {
                    z = true;
                }
                dialogCell.useSeparator = z;
                MessageObject messageObject = (MessageObject) getItem(i);
                dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date);
                return;
            case 3:
                return;
            case 4:
                HashtagSearchCell hashtagSearchCell = (HashtagSearchCell) viewHolder.itemView;
                hashtagSearchCell.setText((CharSequence) this.searchResultHashtags.get(i - 1));
                if (i != this.searchResultHashtags.size()) {
                    z = true;
                }
                hashtagSearchCell.setNeedDivider(z);
                return;
            case 5:
                ((CategoryAdapterRecycler) ((RecyclerListView) viewHolder.itemView).getAdapter()).setIndex(i / 2);
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int i) {
        int i2 = 1;
        int i3;
        if (isRecentSearchDisplayed()) {
            i3 = !DataQuery.getInstance(this.currentAccount).hints.isEmpty() ? 2 : 0;
            if (i > i3) {
                return 0;
            }
            if (i != i3) {
                if (i % 2 != 0) {
                    return 5;
                }
            }
            return 1;
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
