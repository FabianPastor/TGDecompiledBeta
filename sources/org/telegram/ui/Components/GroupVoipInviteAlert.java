package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.RecyclerListView;

public class GroupVoipInviteAlert extends UsersAlertBase {
    /* access modifiers changed from: private */
    public int addNewRow;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> contacts = new ArrayList<>();
    private boolean contactsEndReached;
    /* access modifiers changed from: private */
    public int contactsEndRow;
    /* access modifiers changed from: private */
    public int contactsHeaderRow;
    private SparseArray<TLObject> contactsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    private int delayResults;
    private GroupVoipInviteAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public int flickerProgressRow;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public HashSet<Integer> invitedUsers;
    /* access modifiers changed from: private */
    public int lastRow;
    /* access modifiers changed from: private */
    public boolean loadingUsers;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> participants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int participantsEndRow;
    private SparseArray<TLObject> participantsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int participantsStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private final SearchAdapter searchAdapter;

    public interface GroupVoipInviteAlertDelegate {
        void copyInviteLink();

        void inviteUser(int i);

        void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor);
    }

    /* access modifiers changed from: protected */
    public void updateColorKeys() {
        this.keyScrollUp = "voipgroup_scrollUp";
        this.keyListSelector = "voipgroup_listSelector";
        this.keySearchBackground = "voipgroup_searchBackground";
        this.keyInviteMembersBackground = "voipgroup_inviteMembersBackground";
        this.keyListViewBackground = "voipgroup_listViewBackground";
        this.keyActionBarUnscrolled = "voipgroup_actionBarUnscrolled";
        this.keyNameText = "voipgroup_nameText";
        this.keyLastSeenText = "voipgroup_lastSeenText";
        this.keyLastSeenTextUnscrolled = "voipgroup_lastSeenTextUnscrolled";
        this.keySearchPlaceholder = "voipgroup_searchPlaceholder";
        this.keySearchText = "voipgroup_searchText";
        this.keySearchIcon = "voipgroup_mutedIcon";
        this.keySearchIconUnscrolled = "voipgroup_mutedIconUnscrolled";
    }

    public GroupVoipInviteAlert(Context context, int i, TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull, SparseArray<TLRPC$TL_groupCallParticipant> sparseArray, HashSet<Integer> hashSet) {
        super(context, false, i);
        setDimBehindAlpha(75);
        this.currentChat = tLRPC$Chat;
        this.info = tLRPC$ChatFull;
        this.ignoredUsers = sparseArray;
        this.invitedUsers = hashSet;
        this.currentChat = tLRPC$Chat;
        this.info = tLRPC$ChatFull;
        this.ignoredUsers = sparseArray;
        this.invitedUsers = hashSet;
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                GroupVoipInviteAlert.this.lambda$new$0$GroupVoipInviteAlert(view, i);
            }
        });
        SearchAdapter searchAdapter2 = new SearchAdapter(context);
        this.searchAdapter = searchAdapter2;
        this.searchListViewAdapter = searchAdapter2;
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        loadChatParticipants(0, 200);
        updateRows();
        setColorProgress(0.0f);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupVoipInviteAlert(View view, int i) {
        if (i == this.addNewRow) {
            this.delegate.copyInviteLink();
            dismiss();
        } else if (view instanceof ManageChatUserCell) {
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) view;
            if (!this.invitedUsers.contains(Integer.valueOf(manageChatUserCell.getUserId()))) {
                this.delegate.inviteUser(manageChatUserCell.getUserId());
            }
        }
    }

    public void setDelegate(GroupVoipInviteAlertDelegate groupVoipInviteAlertDelegate) {
        this.delegate = groupVoipInviteAlertDelegate;
    }

    private void updateRows() {
        this.addNewRow = -1;
        this.emptyRow = -1;
        this.participantsStartRow = -1;
        this.participantsEndRow = -1;
        this.contactsHeaderRow = -1;
        this.contactsStartRow = -1;
        this.contactsEndRow = -1;
        this.membersHeaderRow = -1;
        this.lastRow = -1;
        boolean z = false;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.emptyRow = 0;
        if (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addNewRow = i;
        }
        if (!this.loadingUsers || this.firstLoaded) {
            if (!this.contacts.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.contactsHeaderRow = i2;
                this.contactsStartRow = i3;
                int size = i3 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
                z = true;
            }
            if (!this.participants.isEmpty()) {
                if (z) {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersHeaderRow = i4;
                }
                int i5 = this.rowCount;
                this.participantsStartRow = i5;
                int size2 = i5 + this.participants.size();
                this.rowCount = size2;
                this.participantsEndRow = size2;
            }
        }
        if (this.loadingUsers) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.flickerProgressRow = i6;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.lastRow = i7;
    }

    private void loadChatParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            loadChatParticipants(i, i2, true);
        }
    }

    /* access modifiers changed from: protected */
    public void loadChatParticipants(int i, int i2, boolean z) {
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray;
        TLRPC$User user;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            if (this.info != null) {
                int i3 = UserConfig.getInstance(this.currentAccount).clientUserId;
                int size = this.info.participants.participants.size();
                for (int i4 = 0; i4 < size; i4++) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant = this.info.participants.participants.get(i4);
                    int i5 = tLRPC$ChatParticipant.user_id;
                    if (i5 != i3 && (((sparseArray = this.ignoredUsers) == null || sparseArray.indexOfKey(i5) < 0) && ((user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChatParticipant.user_id))) == null || !user.bot))) {
                        this.participants.add(tLRPC$ChatParticipant);
                        this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
                    }
                }
            }
            updateRows();
            RecyclerListView.SelectionAdapter selectionAdapter = this.listViewAdapter;
            if (selectionAdapter != null) {
                selectionAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        RecyclerListView.SelectionAdapter selectionAdapter2 = this.listViewAdapter;
        if (selectionAdapter2 != null) {
            selectionAdapter2.notifyDataSetChanged();
        }
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants_count <= 200) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        } else if (!this.contactsEndReached) {
            this.delayResults = 2;
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
            this.contactsEndReached = true;
            loadChatParticipants(0, 200, false);
        } else {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        }
        tLRPC$TL_channels_getParticipants.filter.q = "";
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = i2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate(tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupVoipInviteAlert.this.lambda$loadChatParticipants$3$GroupVoipInviteAlert(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadChatParticipants$3 */
    public /* synthetic */ void lambda$loadChatParticipants$3$GroupVoipInviteAlert(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupVoipInviteAlert.this.lambda$null$2$GroupVoipInviteAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$GroupVoipInviteAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        int i;
        SparseArray<TLObject> sparseArray;
        ArrayList<TLObject> arrayList;
        boolean z;
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray2;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int i2 = 0;
            while (true) {
                if (i2 >= tLRPC$TL_channels_channelParticipants.participants.size()) {
                    break;
                } else if (tLRPC$TL_channels_channelParticipants.participants.get(i2).user_id == clientUserId) {
                    tLRPC$TL_channels_channelParticipants.participants.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            this.delayResults--;
            if (tLRPC$TL_channels_getParticipants.filter instanceof TLRPC$TL_channelParticipantsContacts) {
                arrayList = this.contacts;
                sparseArray = this.contactsMap;
            } else {
                arrayList = this.participants;
                sparseArray = this.participantsMap;
            }
            arrayList.clear();
            arrayList.addAll(tLRPC$TL_channels_channelParticipants.participants);
            int size = tLRPC$TL_channels_channelParticipants.participants.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i3);
                sparseArray.put(tLRPC$ChannelParticipant.user_id, tLRPC$ChannelParticipant);
            }
            int size2 = this.participants.size();
            int i4 = 0;
            while (i4 < size2) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = (TLRPC$ChannelParticipant) this.participants.get(i4);
                if (this.contactsMap.get(tLRPC$ChannelParticipant2.user_id) == null && ((sparseArray2 = this.ignoredUsers) == null || sparseArray2.indexOfKey(tLRPC$ChannelParticipant2.user_id) < 0)) {
                    z = false;
                } else {
                    z = true;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChannelParticipant2.user_id));
                if (user != null && user.bot) {
                    z = true;
                }
                if (z) {
                    this.participants.remove(i4);
                    this.participantsMap.remove(tLRPC$ChannelParticipant2.user_id);
                    i4--;
                    size2--;
                }
                i4++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    Collections.sort(arrayList, new Object(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                        public final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final int compare(Object obj, Object obj2) {
                            return GroupVoipInviteAlert.this.lambda$null$1$GroupVoipInviteAlert(this.f$1, (TLObject) obj, (TLObject) obj2);
                        }

                        public /* synthetic */ Comparator reversed() {
                            return Comparator.CC.$default$reversed(this);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                            return Comparator.CC.$default$thenComparing(this, function, comparator);
                        }

                        public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                            return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                            return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                            return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                        }

                        public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                            return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
            if (this.flickerProgressRow == 1) {
                i = 1;
            } else {
                RecyclerListView.SelectionAdapter selectionAdapter = this.listViewAdapter;
                i = selectionAdapter != null ? selectionAdapter.getItemCount() - 1 : 0;
            }
            showItemsAnimated(i);
        }
        updateRows();
        RecyclerListView.SelectionAdapter selectionAdapter2 = this.listViewAdapter;
        if (selectionAdapter2 != null) {
            selectionAdapter2.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ int lambda$null$1$GroupVoipInviteAlert(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        int i3;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC$ChannelParticipant) tLObject).user_id));
        TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC$ChannelParticipant) tLObject2).user_id));
        if (user == null || (tLRPC$UserStatus2 = user.status) == null) {
            i2 = 0;
        } else {
            i2 = user.self ? i + 50000 : tLRPC$UserStatus2.expires;
        }
        if (user2 == null || (tLRPC$UserStatus = user2.status) == null) {
            i3 = 0;
        } else {
            i3 = user2.self ? i + 50000 : tLRPC$UserStatus.expires;
        }
        if (i2 <= 0 || i3 <= 0) {
            if (i2 >= 0 || i3 >= 0) {
                if ((i2 < 0 && i3 > 0) || (i2 == 0 && i3 != 0)) {
                    return -1;
                }
                if ((i3 >= 0 || i2 <= 0) && (i3 != 0 || i2 == 0)) {
                    return 0;
                }
                return 1;
            } else if (i2 > i3) {
                return 1;
            } else {
                if (i2 < i3) {
                    return -1;
                }
                return 0;
            }
        } else if (i2 > i3) {
            return 1;
        } else {
            if (i2 < i3) {
                return -1;
            }
            return 0;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int emptyRow;
        private int globalStartRow;
        private int groupStartRow;
        private int lastRow;
        /* access modifiers changed from: private */
        public int lastSearchId;
        private Context mContext;
        /* access modifiers changed from: private */
        public SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public boolean searchInProgress;
        private Runnable searchRunnable;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate(GroupVoipInviteAlert.this) {
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                public /* synthetic */ SparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                public void onDataSetChanged(int i) {
                    if (i >= 0 && i == SearchAdapter.this.lastSearchId && !SearchAdapter.this.searchInProgress) {
                        boolean z = true;
                        int itemCount = SearchAdapter.this.getItemCount() - 1;
                        if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                            z = false;
                        }
                        SearchAdapter.this.notifyDataSetChanged();
                        if (SearchAdapter.this.getItemCount() > itemCount) {
                            GroupVoipInviteAlert.this.showItemsAnimated(itemCount);
                        }
                        if (!SearchAdapter.this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                            GroupVoipInviteAlert.this.emptyView.showProgress(false, z);
                        }
                    }
                }

                public SparseArray<TLRPC$TL_groupCallParticipant> getExcludeCallParticipants() {
                    return GroupVoipInviteAlert.this.ignoredUsers;
                }
            });
        }

        public void searchUsers(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, -1);
            if (!TextUtils.isEmpty(str)) {
                GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(false, 0);
                notifyDataSetChanged();
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(true, 0);
                this.searchInProgress = true;
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                $$Lambda$GroupVoipInviteAlert$SearchAdapter$CjJVFO4QLy91LWrj5CbugY1KnwU r1 = new Runnable(str, i) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.lambda$searchUsers$0$GroupVoipInviteAlert$SearchAdapter(this.f$1, this.f$2);
                    }
                };
                this.searchRunnable = r1;
                AndroidUtilities.runOnUIThread(r1, 300);
                RecyclerView.Adapter adapter = GroupVoipInviteAlert.this.listView.getAdapter();
                GroupVoipInviteAlert groupVoipInviteAlert = GroupVoipInviteAlert.this;
                RecyclerListView.SelectionAdapter selectionAdapter = groupVoipInviteAlert.searchListViewAdapter;
                if (adapter != selectionAdapter) {
                    groupVoipInviteAlert.listView.setAdapter(selectionAdapter);
                    return;
                }
                return;
            }
            this.lastSearchId = -1;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$searchUsers$0 */
        public /* synthetic */ void lambda$searchUsers$0$GroupVoipInviteAlert$SearchAdapter(String str, int i) {
            if (this.searchRunnable != null) {
                this.searchRunnable = null;
                processSearch(str, i);
            }
        }

        private void processSearch(String str, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(str, i) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.lambda$processSearch$2$GroupVoipInviteAlert$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$processSearch$2 */
        public /* synthetic */ void lambda$processSearch$2$GroupVoipInviteAlert$SearchAdapter(String str, int i) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) && GroupVoipInviteAlert.this.info != null) {
                arrayList = new ArrayList(GroupVoipInviteAlert.this.info.participants.participants);
            }
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new Runnable(str, i, arrayList) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$2;
                    public final /* synthetic */ ArrayList f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        GroupVoipInviteAlert.SearchAdapter.this.lambda$null$1$GroupVoipInviteAlert$SearchAdapter(this.f$1, this.f$2, this.f$3);
                    }
                });
            } else {
                String str2 = str;
                int i2 = i;
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatObject.canAddUsers(GroupVoipInviteAlert.this.currentChat), false, true, false, false, ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) ? GroupVoipInviteAlert.this.currentChat.id : 0, false, 2, i);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d9, code lost:
            if (r14.contains(" " + r5) != false) goto L_0x00e9;
         */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00f1 A[LOOP:1: B:32:0x009d->B:50:0x00f1, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x00ed A[SYNTHETIC] */
        /* renamed from: lambda$null$1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$GroupVoipInviteAlert$SearchAdapter(java.lang.String r19, int r20, java.util.ArrayList r21) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                r2 = r21
                java.lang.String r3 = r19.trim()
                java.lang.String r3 = r3.toLowerCase()
                int r4 = r3.length()
                if (r4 != 0) goto L_0x001d
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r0.updateSearchResults(r2, r1)
                return
            L_0x001d:
                org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r4 = r4.getTranslitString(r3)
                boolean r5 = r3.equals(r4)
                if (r5 != 0) goto L_0x0031
                int r5 = r4.length()
                if (r5 != 0) goto L_0x0032
            L_0x0031:
                r4 = 0
            L_0x0032:
                r5 = 0
                r7 = 1
                if (r4 == 0) goto L_0x0038
                r8 = 1
                goto L_0x0039
            L_0x0038:
                r8 = 0
            L_0x0039:
                int r8 = r8 + r7
                java.lang.String[] r9 = new java.lang.String[r8]
                r9[r5] = r3
                if (r4 == 0) goto L_0x0042
                r9[r7] = r4
            L_0x0042:
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                if (r2 == 0) goto L_0x00fc
                int r4 = r21.size()
                r10 = 0
            L_0x004e:
                if (r10 >= r4) goto L_0x00fc
                java.lang.Object r11 = r2.get(r10)
                org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r12 == 0) goto L_0x0060
                r12 = r11
                org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC$ChatParticipant) r12
                int r12 = r12.user_id
                goto L_0x0069
            L_0x0060:
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r12 == 0) goto L_0x00f6
                r12 = r11
                org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r12
                int r12 = r12.user_id
            L_0x0069:
                org.telegram.ui.Components.GroupVoipInviteAlert r13 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r13 = r13.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r12 = r13.getUser(r12)
                boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r12)
                if (r13 == 0) goto L_0x0083
                goto L_0x00f6
            L_0x0083:
                java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r12)
                java.lang.String r13 = r13.toLowerCase()
                org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r14 = r14.getTranslitString(r13)
                boolean r15 = r13.equals(r14)
                if (r15 == 0) goto L_0x009a
                r14 = 0
            L_0x009a:
                r15 = 0
                r16 = 0
            L_0x009d:
                if (r15 >= r8) goto L_0x00f6
                r5 = r9[r15]
                boolean r17 = r13.startsWith(r5)
                if (r17 != 0) goto L_0x00e9
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r7 = " "
                r6.append(r7)
                r6.append(r5)
                java.lang.String r6 = r6.toString()
                boolean r6 = r13.contains(r6)
                if (r6 != 0) goto L_0x00e9
                if (r14 == 0) goto L_0x00dc
                boolean r6 = r14.startsWith(r5)
                if (r6 != 0) goto L_0x00e9
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r7)
                r6.append(r5)
                java.lang.String r6 = r6.toString()
                boolean r6 = r14.contains(r6)
                if (r6 == 0) goto L_0x00dc
                goto L_0x00e9
            L_0x00dc:
                java.lang.String r6 = r12.username
                if (r6 == 0) goto L_0x00eb
                boolean r5 = r6.startsWith(r5)
                if (r5 == 0) goto L_0x00eb
                r16 = 2
                goto L_0x00eb
            L_0x00e9:
                r16 = 1
            L_0x00eb:
                if (r16 == 0) goto L_0x00f1
                r3.add(r11)
                goto L_0x00f6
            L_0x00f1:
                int r15 = r15 + 1
                r5 = 0
                r7 = 1
                goto L_0x009d
            L_0x00f6:
                int r10 = r10 + 1
                r5 = 0
                r7 = 1
                goto L_0x004e
            L_0x00fc:
                r0.updateSearchResults(r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.lambda$null$1$GroupVoipInviteAlert$SearchAdapter(java.lang.String, int, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new Runnable(i, arrayList) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    GroupVoipInviteAlert.SearchAdapter.this.lambda$updateSearchResults$3$GroupVoipInviteAlert$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$3 */
        public /* synthetic */ void lambda$updateSearchResults$3$GroupVoipInviteAlert$SearchAdapter(int i, ArrayList arrayList) {
            if (i == this.lastSearchId) {
                this.searchInProgress = false;
                if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat)) {
                    this.searchAdapterHelper.addGroupMembers(arrayList);
                }
                boolean z = true;
                int itemCount = getItemCount() - 1;
                if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                    z = false;
                }
                notifyDataSetChanged();
                if (getItemCount() > itemCount) {
                    GroupVoipInviteAlert.this.showItemsAnimated(itemCount);
                }
                if (!this.searchInProgress && !this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                    GroupVoipInviteAlert.this.emptyView.showProgress(false, z);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if ((!(view instanceof ManageChatUserCell) || !GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(((ManageChatUserCell) view).getUserId()))) && viewHolder.getItemViewType() == 0) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return this.totalCount;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            this.totalCount = 0 + 1;
            this.emptyRow = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i = this.totalCount;
                this.groupStartRow = i;
                this.totalCount = i + size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            int i3 = this.totalCount;
            this.totalCount = i3 + 1;
            this.lastRow = i3;
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int i2 = this.groupStartRow;
            if (i2 >= 0 && i > i2 && i < i2 + 1 + this.searchAdapterHelper.getGroupSearch().size()) {
                return this.searchAdapterHelper.getGroupSearch().get((i - this.groupStartRow) - 1);
            }
            int i3 = this.globalStartRow;
            if (i3 < 0 || i <= i3 || i >= i3 + 1 + this.searchAdapterHelper.getGlobalSearch().size()) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get((i - this.globalStartRow) - 1);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                r3 = 2
                if (r4 == 0) goto L_0x003d
                r0 = 1
                if (r4 == r0) goto L_0x0027
                if (r4 == r3) goto L_0x0010
                android.view.View r3 = new android.view.View
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                goto L_0x0069
            L_0x0010:
                android.view.View r3 = new android.view.View
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r4 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = 1113587712(0x42600000, float:56.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r4.<init>((int) r0, (int) r1)
                r3.setLayoutParams(r4)
                goto L_0x0069
            L_0x0027:
                org.telegram.ui.Cells.GraySectionCell r3 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r2.mContext
                r3.<init>(r4)
                java.lang.String r4 = "voipgroup_actionBarUnscrolled"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
                r3.setBackgroundColor(r4)
                java.lang.String r4 = "voipgroup_searchPlaceholder"
                r3.setTextColor(r4)
                goto L_0x0069
            L_0x003d:
                org.telegram.ui.Cells.ManageChatUserCell r4 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r0 = r2.mContext
                r1 = 0
                r4.<init>(r0, r3, r3, r1)
                r3 = 2131165751(0x7var_, float:1.7945728E38)
                r4.setCustomRightImage(r3)
                java.lang.String r3 = "voipgroup_nameText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r4.setNameColor(r3)
                java.lang.String r3 = "voipgroup_lastSeenTextUnscrolled"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                java.lang.String r0 = "voipgroup_listeningText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r4.setStatusColors(r3, r0)
                java.lang.String r3 = "voipgroup_listViewBackground"
                r4.setDividerColor(r3)
                r3 = r4
            L_0x0069:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00ee  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                r13 = this;
                int r0 = r14.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x0033
                if (r0 == r1) goto L_0x000b
                goto L_0x012f
            L_0x000b:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.GraySectionCell r14 = (org.telegram.ui.Cells.GraySectionCell) r14
                int r0 = r13.groupStartRow
                if (r15 != r0) goto L_0x0021
                r15 = 2131624714(0x7f0e030a, float:1.8876616E38)
                java.lang.String r0 = "ChannelMembers"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x012f
            L_0x0021:
                int r0 = r13.globalStartRow
                if (r15 != r0) goto L_0x012f
                r15 = 2131625679(0x7f0e06cf, float:1.8878573E38)
                java.lang.String r0 = "GlobalSearch"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x012f
            L_0x0033:
                org.telegram.tgnet.TLObject r0 = r13.getItem(r15)
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$User
                if (r2 == 0) goto L_0x003e
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
                goto L_0x0073
            L_0x003e:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r2 == 0) goto L_0x0059
                org.telegram.ui.Components.GroupVoipInviteAlert r2 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.tgnet.TLRPC$ChannelParticipant r0 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r0
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
                goto L_0x0073
            L_0x0059:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r2 == 0) goto L_0x012f
                org.telegram.ui.Components.GroupVoipInviteAlert r2 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            L_0x0073:
                java.lang.String r2 = r0.username
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.util.ArrayList r3 = r3.getGroupSearch()
                int r3 = r3.size()
                r4 = 0
                r5 = 0
                if (r3 == 0) goto L_0x008f
                int r3 = r3 + r1
                if (r3 <= r15) goto L_0x008e
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.lang.String r3 = r3.getLastFoundChannel()
                r6 = 1
                goto L_0x0091
            L_0x008e:
                int r15 = r15 - r3
            L_0x008f:
                r3 = r5
                r6 = 0
            L_0x0091:
                r7 = 33
                java.lang.String r8 = "voipgroup_listeningText"
                r9 = -1
                if (r6 != 0) goto L_0x00eb
                if (r2 == 0) goto L_0x00eb
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.util.ArrayList r6 = r6.getGlobalSearch()
                int r6 = r6.size()
                if (r6 == 0) goto L_0x00eb
                int r6 = r6 + r1
                if (r6 <= r15) goto L_0x00eb
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.lang.String r6 = r6.getLastFoundUsername()
                java.lang.String r10 = "@"
                boolean r11 = r6.startsWith(r10)
                if (r11 == 0) goto L_0x00bb
                java.lang.String r6 = r6.substring(r1)
            L_0x00bb:
                android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x00e6 }
                r1.<init>()     // Catch:{ Exception -> 0x00e6 }
                r1.append(r10)     // Catch:{ Exception -> 0x00e6 }
                r1.append(r2)     // Catch:{ Exception -> 0x00e6 }
                int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r6)     // Catch:{ Exception -> 0x00e6 }
                if (r10 == r9) goto L_0x00e4
                int r6 = r6.length()     // Catch:{ Exception -> 0x00e6 }
                if (r10 != 0) goto L_0x00d5
                int r6 = r6 + 1
                goto L_0x00d7
            L_0x00d5:
                int r10 = r10 + 1
            L_0x00d7:
                android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x00e6 }
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x00e6 }
                r11.<init>(r12)     // Catch:{ Exception -> 0x00e6 }
                int r6 = r6 + r10
                r1.setSpan(r11, r10, r6, r7)     // Catch:{ Exception -> 0x00e6 }
            L_0x00e4:
                r2 = r1
                goto L_0x00ec
            L_0x00e6:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
                goto L_0x00ec
            L_0x00eb:
                r2 = r5
            L_0x00ec:
                if (r3 == 0) goto L_0x010e
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r0)
                android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
                r5.<init>(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r1, r3)
                if (r1 == r9) goto L_0x010e
                android.text.style.ForegroundColorSpan r6 = new android.text.style.ForegroundColorSpan
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r6.<init>(r8)
                int r3 = r3.length()
                int r3 = r3 + r1
                r5.setSpan(r6, r1, r3, r7)
            L_0x010e:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.ManageChatUserCell r14 = (org.telegram.ui.Cells.ManageChatUserCell) r14
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                r14.setTag(r15)
                org.telegram.ui.Components.GroupVoipInviteAlert r15 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                java.util.HashSet r15 = r15.invitedUsers
                int r1 = r0.id
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                boolean r15 = r15.contains(r1)
                r14.setCustomImageVisible(r15)
                r14.setData(r0, r5, r2, r4)
            L_0x012f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.emptyRow) {
                return 2;
            }
            if (i == this.lastRow) {
                return 3;
            }
            return (i == this.globalStartRow || i == this.groupStartRow) ? 1 : 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if ((view instanceof ManageChatUserCell) && GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(((ManageChatUserCell) view).getUserId()))) {
                return false;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0 || itemViewType == 1) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return GroupVoipInviteAlert.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v12, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                java.lang.String r6 = "voipgroup_actionBar"
                r0 = 6
                r1 = 2
                java.lang.String r2 = "voipgroup_listeningText"
                if (r7 == 0) goto L_0x006c
                r3 = 1
                if (r7 == r3) goto L_0x005e
                java.lang.String r6 = "voipgroup_actionBarUnscrolled"
                if (r7 == r1) goto L_0x004a
                r1 = 3
                if (r7 == r1) goto L_0x0033
                r1 = 5
                if (r7 == r1) goto L_0x001e
                android.view.View r6 = new android.view.View
                android.content.Context r7 = r5.mContext
                r6.<init>(r7)
                goto L_0x0094
            L_0x001e:
                org.telegram.ui.Components.FlickerLoadingView r7 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r1 = r5.mContext
                r7.<init>(r1)
                r7.setViewType(r0)
                r7.setIsSingleCell(r3)
                java.lang.String r0 = "voipgroup_inviteMembersBackground"
                java.lang.String r1 = "voipgroup_searchBackground"
                r7.setColors(r0, r1, r6)
                goto L_0x0093
            L_0x0033:
                android.view.View r6 = new android.view.View
                android.content.Context r7 = r5.mContext
                r6.<init>(r7)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r7 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = 1113587712(0x42600000, float:56.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r7.<init>((int) r0, (int) r1)
                r6.setLayoutParams(r7)
                goto L_0x0094
            L_0x004a:
                org.telegram.ui.Cells.GraySectionCell r7 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r0 = r5.mContext
                r7.<init>(r0)
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r7.setBackgroundColor(r6)
                java.lang.String r6 = "voipgroup_searchPlaceholder"
                r7.setTextColor(r6)
                goto L_0x0093
            L_0x005e:
                org.telegram.ui.Cells.ManageChatTextCell r7 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r0 = r5.mContext
                r7.<init>(r0)
                r7.setColors(r2, r2)
                r7.setDividerColor(r6)
                goto L_0x0093
            L_0x006c:
                org.telegram.ui.Cells.ManageChatUserCell r7 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r3 = r5.mContext
                r4 = 0
                r7.<init>(r3, r0, r1, r4)
                r0 = 2131165751(0x7var_, float:1.7945728E38)
                r7.setCustomRightImage(r0)
                java.lang.String r0 = "voipgroup_nameText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r7.setNameColor(r0)
                java.lang.String r0 = "voipgroup_lastSeenTextUnscrolled"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r7.setStatusColors(r0, r1)
                r7.setDividerColor(r6)
            L_0x0093:
                r6 = r7
            L_0x0094:
                org.telegram.ui.Components.RecyclerListView$Holder r7 = new org.telegram.ui.Components.RecyclerListView$Holder
                r7.<init>(r6)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int i3;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TLObject item = getItem(i);
                if (i < GroupVoipInviteAlert.this.participantsStartRow || i >= GroupVoipInviteAlert.this.participantsEndRow) {
                    i2 = GroupVoipInviteAlert.this.contactsEndRow;
                } else {
                    i2 = GroupVoipInviteAlert.this.participantsEndRow;
                }
                if (item instanceof TLRPC$ChannelParticipant) {
                    i3 = ((TLRPC$ChannelParticipant) item).user_id;
                } else {
                    i3 = ((TLRPC$ChatParticipant) item).user_id;
                }
                TLRPC$User user = MessagesController.getInstance(GroupVoipInviteAlert.this.currentAccount).getUser(Integer.valueOf(i3));
                if (user != null) {
                    manageChatUserCell.setCustomImageVisible(GroupVoipInviteAlert.this.invitedUsers.contains(Integer.valueOf(user.id)));
                    if (i != i2 - 1) {
                        z = true;
                    }
                    manageChatUserCell.setData(user, (CharSequence) null, (CharSequence) null, z);
                }
            } else if (itemViewType == 1) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                if (i == GroupVoipInviteAlert.this.addNewRow) {
                    manageChatTextCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", NUM), (String) null, NUM, 7, (!GroupVoipInviteAlert.this.loadingUsers || GroupVoipInviteAlert.this.firstLoaded) && GroupVoipInviteAlert.this.membersHeaderRow == -1 && !GroupVoipInviteAlert.this.participants.isEmpty());
                }
            } else if (itemViewType == 2) {
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                if (i == GroupVoipInviteAlert.this.membersHeaderRow) {
                    graySectionCell.setText(LocaleController.getString("ChannelOtherMembers", NUM));
                } else if (i == GroupVoipInviteAlert.this.contactsHeaderRow) {
                    graySectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if ((i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) || (i >= GroupVoipInviteAlert.this.contactsStartRow && i < GroupVoipInviteAlert.this.contactsEndRow)) {
                return 0;
            }
            if (i == GroupVoipInviteAlert.this.addNewRow) {
                return 1;
            }
            if (i == GroupVoipInviteAlert.this.membersHeaderRow || i == GroupVoipInviteAlert.this.contactsHeaderRow) {
                return 2;
            }
            if (i == GroupVoipInviteAlert.this.emptyRow) {
                return 3;
            }
            if (i == GroupVoipInviteAlert.this.lastRow) {
                return 4;
            }
            if (i == GroupVoipInviteAlert.this.flickerProgressRow) {
                return 5;
            }
            return 0;
        }

        public TLObject getItem(int i) {
            if (i >= GroupVoipInviteAlert.this.participantsStartRow && i < GroupVoipInviteAlert.this.participantsEndRow) {
                return (TLObject) GroupVoipInviteAlert.this.participants.get(i - GroupVoipInviteAlert.this.participantsStartRow);
            }
            if (i < GroupVoipInviteAlert.this.contactsStartRow || i >= GroupVoipInviteAlert.this.contactsEndRow) {
                return null;
            }
            return (TLObject) GroupVoipInviteAlert.this.contacts.get(i - GroupVoipInviteAlert.this.contactsStartRow);
        }
    }

    /* access modifiers changed from: protected */
    public void search(String str) {
        this.searchAdapter.searchUsers(str);
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
        this.delegate.needOpenSearch(motionEvent, editTextBoldCursor);
    }
}
