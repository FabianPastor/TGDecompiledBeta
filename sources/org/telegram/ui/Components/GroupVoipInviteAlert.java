package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
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
    private LongSparseArray<TLObject> contactsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int contactsStartRow;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    private int delayResults;
    private GroupVoipInviteAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public boolean firstLoaded;
    /* access modifiers changed from: private */
    public int flickerProgressRow;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.TL_groupCallParticipant> ignoredUsers;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public HashSet<Long> invitedUsers;
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
    private LongSparseArray<TLObject> participantsMap = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int participantsStartRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private final SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public boolean showContacts;

    public interface GroupVoipInviteAlertDelegate {
        void copyInviteLink();

        void inviteUser(long j);

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

    public GroupVoipInviteAlert(Context context, int account, TLRPC.Chat chat, TLRPC.ChatFull chatFull, LongSparseArray<TLRPC.TL_groupCallParticipant> participants2, HashSet<Long> invited) {
        super(context, false, account, (Theme.ResourcesProvider) null);
        setDimBehindAlpha(75);
        this.currentChat = chat;
        this.info = chatFull;
        this.ignoredUsers = participants2;
        this.invitedUsers = invited;
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupVoipInviteAlert$$ExternalSyntheticLambda4(this));
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

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupVoipInviteAlert  reason: not valid java name */
    public /* synthetic */ void m4050lambda$new$0$orgtelegramuiComponentsGroupVoipInviteAlert(View view, int position) {
        if (position == this.addNewRow) {
            this.delegate.copyInviteLink();
            dismiss();
        } else if (view instanceof ManageChatUserCell) {
            ManageChatUserCell cell = (ManageChatUserCell) view;
            if (!this.invitedUsers.contains(Long.valueOf(cell.getUserId()))) {
                this.delegate.inviteUser(cell.getUserId());
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
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.emptyRow = 0;
        if (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addNewRow = i;
        }
        if (!this.loadingUsers || this.firstLoaded) {
            boolean hasAnyOther = false;
            if (!this.contacts.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.contactsHeaderRow = i2;
                this.contactsStartRow = i3;
                int size = i3 + this.contacts.size();
                this.rowCount = size;
                this.contactsEndRow = size;
                hasAnyOther = true;
            }
            if (!this.participants.isEmpty()) {
                if (hasAnyOther) {
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

    private void loadChatParticipants(int offset, int count) {
        if (!this.loadingUsers) {
            this.contactsEndReached = false;
            loadChatParticipants(offset, count, true);
        }
    }

    private void fillContacts() {
        if (this.showContacts) {
            this.contacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
            long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
            int a = 0;
            int N = this.contacts.size();
            while (a < N) {
                TLObject object = this.contacts.get(a);
                if (object instanceof TLRPC.TL_contact) {
                    long userId = ((TLRPC.TL_contact) object).user_id;
                    if (userId == selfId || this.ignoredUsers.indexOfKey(userId) >= 0 || this.invitedUsers.contains(Long.valueOf(userId))) {
                        this.contacts.remove(a);
                        a--;
                        N--;
                    }
                }
                a++;
            }
            Collections.sort(this.contacts, new GroupVoipInviteAlert$$ExternalSyntheticLambda1(MessagesController.getInstance(this.currentAccount), ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
        }
    }

    static /* synthetic */ int lambda$fillContacts$1(MessagesController messagesController, int currentTime, TLObject o1, TLObject o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(((TLRPC.TL_contact) o2).user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(((TLRPC.TL_contact) o1).user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return (status2 < 0 || status1 != 0) ? 1 : 0;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    /* access modifiers changed from: protected */
    public void loadChatParticipants(int offset, int count, boolean reset) {
        LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            if (this.info != null) {
                long selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
                int size = this.info.participants.participants.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.ChatParticipant participant = this.info.participants.participants.get(a);
                    if (participant.user_id != selfUserId && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(participant.user_id) < 0)) {
                        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(participant.user_id));
                        if (!UserObject.isDeleted(user) && !user.bot) {
                            this.participants.add(participant);
                            this.participantsMap.put(participant.user_id, participant);
                        }
                    }
                }
                if (this.participants.isEmpty()) {
                    this.showContacts = true;
                    fillContacts();
                }
            }
            updateRows();
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.loadingUsers = true;
        if (this.emptyView != null) {
            this.emptyView.showProgress(true, false);
        }
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null && chatFull.participants_count <= 200) {
            req.filter = new TLRPC.TL_channelParticipantsRecent();
        } else if (!this.contactsEndReached) {
            this.delayResults = 2;
            req.filter = new TLRPC.TL_channelParticipantsContacts();
            this.contactsEndReached = true;
            loadChatParticipants(0, 200, false);
        } else {
            req.filter = new TLRPC.TL_channelParticipantsRecent();
        }
        req.filter.q = "";
        req.offset = offset;
        req.limit = count;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new GroupVoipInviteAlert$$ExternalSyntheticLambda3(this, req));
    }

    /* renamed from: lambda$loadChatParticipants$4$org-telegram-ui-Components-GroupVoipInviteAlert  reason: not valid java name */
    public /* synthetic */ void m4049x8a32b9c(TLRPC.TL_channels_getParticipants req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$$ExternalSyntheticLambda0(this, error, response, req));
    }

    /* renamed from: lambda$loadChatParticipants$3$org-telegram-ui-Components-GroupVoipInviteAlert  reason: not valid java name */
    public /* synthetic */ void m4048x919919b(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        int num;
        LongSparseArray<TLObject> map;
        ArrayList<TLObject> objects;
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            long selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int a = 0;
            while (true) {
                if (a >= res.participants.size()) {
                    break;
                } else if (MessageObject.getPeerId(res.participants.get(a).peer) == selfId) {
                    res.participants.remove(a);
                    break;
                } else {
                    a++;
                }
            }
            this.delayResults--;
            if (req.filter instanceof TLRPC.TL_channelParticipantsContacts) {
                ArrayList<TLObject> objects2 = this.contacts;
                map = this.contactsMap;
                objects = objects2;
            } else {
                ArrayList<TLObject> objects3 = this.participants;
                map = this.participantsMap;
                objects = objects3;
            }
            objects.clear();
            objects.addAll(res.participants);
            int size = res.participants.size();
            for (int a2 = 0; a2 < size; a2++) {
                TLRPC.ChannelParticipant participant = res.participants.get(a2);
                map.put(MessageObject.getPeerId(participant.peer), participant);
            }
            int a3 = 0;
            int N = this.participants.size();
            while (a3 < N) {
                long peerId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) this.participants.get(a3)).peer);
                boolean remove = false;
                if (this.contactsMap.get(peerId) != null) {
                    remove = true;
                } else {
                    LongSparseArray<TLRPC.TL_groupCallParticipant> longSparseArray = this.ignoredUsers;
                    if (longSparseArray != null && longSparseArray.indexOfKey(peerId) >= 0) {
                        remove = true;
                    }
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
                if ((user != null && user.bot) || UserObject.isDeleted(user)) {
                    remove = true;
                }
                if (remove) {
                    this.participants.remove(a3);
                    this.participantsMap.remove(peerId);
                    a3--;
                    N--;
                }
                a3++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    Collections.sort(objects, new GroupVoipInviteAlert$$ExternalSyntheticLambda2(this, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = req;
        }
        if (this.delayResults <= 0) {
            this.loadingUsers = false;
            this.firstLoaded = true;
            if (this.flickerProgressRow == 1) {
                num = 1;
            } else {
                num = this.listViewAdapter != null ? this.listViewAdapter.getItemCount() - 1 : 0;
            }
            showItemsAnimated(num);
            if (this.participants.isEmpty()) {
                this.showContacts = true;
                fillContacts();
            }
        }
        updateRows();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
    }

    /* renamed from: lambda$loadChatParticipants$2$org-telegram-ui-Components-GroupVoipInviteAlert  reason: not valid java name */
    public /* synthetic */ int m4047x98fvar_a(int currentTime, TLObject lhs, TLObject rhs) {
        TLRPC.User user1 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) lhs).peer)));
        TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) rhs).peer)));
        int status1 = 0;
        int status2 = 0;
        if (!(user1 == null || user1.status == null)) {
            status1 = user1.self ? currentTime + 50000 : user1.status.expires;
        }
        if (!(user2 == null || user2.status == null)) {
            status2 = user2.self ? currentTime + 50000 : user2.status.expires;
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
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

                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }

                public void onDataSetChanged(int searchId) {
                    if (searchId >= 0 && searchId == SearchAdapter.this.lastSearchId && !SearchAdapter.this.searchInProgress) {
                        boolean emptyViewWasVisible = true;
                        int oldItemCount = SearchAdapter.this.getItemCount() - 1;
                        if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                            emptyViewWasVisible = false;
                        }
                        SearchAdapter.this.notifyDataSetChanged();
                        if (SearchAdapter.this.getItemCount() > oldItemCount) {
                            GroupVoipInviteAlert.this.showItemsAnimated(oldItemCount);
                        }
                        if (!SearchAdapter.this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                            GroupVoipInviteAlert.this.emptyView.showProgress(false, emptyViewWasVisible);
                        }
                    }
                }

                public LongSparseArray<TLRPC.TL_groupCallParticipant> getExcludeCallParticipants() {
                    return GroupVoipInviteAlert.this.ignoredUsers;
                }
            });
        }

        public void searchUsers(String query) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, -1);
            if (!TextUtils.isEmpty(query)) {
                GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(false, 0);
                notifyDataSetChanged();
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(true, 0);
                this.searchInProgress = true;
                int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2 groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2 = new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2(this, query, searchId);
                this.searchRunnable = groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2;
                AndroidUtilities.runOnUIThread(groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2, 300);
                if (GroupVoipInviteAlert.this.listView.getAdapter() != GroupVoipInviteAlert.this.searchListViewAdapter) {
                    GroupVoipInviteAlert.this.listView.setAdapter(GroupVoipInviteAlert.this.searchListViewAdapter);
                    return;
                }
                return;
            }
            this.lastSearchId = -1;
        }

        /* renamed from: lambda$searchUsers$0$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4053xc1afvar_(String query, int searchId) {
            if (this.searchRunnable != null) {
                this.searchRunnable = null;
                processSearch(query, searchId);
            }
        }

        private void processSearch(String query, int searchId) {
            AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1(this, query, searchId));
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4052x7920c6dc(String query, int searchId) {
            ArrayList<TLObject> participantsCopy = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) && GroupVoipInviteAlert.this.info != null) {
                participantsCopy = new ArrayList<>(GroupVoipInviteAlert.this.info.participants.participants);
            }
            if (participantsCopy != null) {
                Utilities.searchQueue.postRunnable(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda3(this, query, searchId, participantsCopy));
            } else {
                String str = query;
                int i = searchId;
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(query, ChatObject.canAddUsers(GroupVoipInviteAlert.this.currentChat), false, true, false, false, ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) ? GroupVoipInviteAlert.this.currentChat.id : 0, false, 2, searchId);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ea, code lost:
            if (r15.contains(" " + r3) != false) goto L_0x0101;
         */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x010a A[LOOP:1: B:31:0x00a8->B:50:0x010a, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0106 A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m4051x8591429b(java.lang.String r22, int r23, java.util.ArrayList r24) {
            /*
                r21 = this;
                r0 = r21
                r1 = r23
                java.lang.String r2 = r22.trim()
                java.lang.String r2 = r2.toLowerCase()
                int r3 = r2.length()
                if (r3 != 0) goto L_0x001b
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r3, r1)
                return
            L_0x001b:
                org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r3 = r3.getTranslitString(r2)
                boolean r4 = r2.equals(r3)
                if (r4 != 0) goto L_0x002f
                int r4 = r3.length()
                if (r4 != 0) goto L_0x0030
            L_0x002f:
                r3 = 0
            L_0x0030:
                r4 = 0
                r5 = 1
                if (r3 == 0) goto L_0x0036
                r6 = 1
                goto L_0x0037
            L_0x0036:
                r6 = 0
            L_0x0037:
                int r6 = r6 + r5
                java.lang.String[] r6 = new java.lang.String[r6]
                r6[r4] = r2
                if (r3 == 0) goto L_0x0040
                r6[r5] = r3
            L_0x0040:
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                r7 = 0
                int r8 = r24.size()
            L_0x004a:
                if (r7 >= r8) goto L_0x0129
                r9 = r24
                java.lang.Object r10 = r9.get(r7)
                org.telegram.tgnet.TLObject r10 = (org.telegram.tgnet.TLObject) r10
                boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r11 == 0) goto L_0x005e
                r11 = r10
                org.telegram.tgnet.TLRPC$ChatParticipant r11 = (org.telegram.tgnet.TLRPC.ChatParticipant) r11
                long r11 = r11.user_id
                goto L_0x006b
            L_0x005e:
                boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r11 == 0) goto L_0x0118
                r11 = r10
                org.telegram.tgnet.TLRPC$ChannelParticipant r11 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r11
                org.telegram.tgnet.TLRPC$Peer r11 = r11.peer
                long r11 = org.telegram.messenger.MessageObject.getPeerId(r11)
            L_0x006b:
                org.telegram.ui.Components.GroupVoipInviteAlert r13 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r13 = r13.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                java.lang.Long r14 = java.lang.Long.valueOf(r11)
                org.telegram.tgnet.TLRPC$User r13 = r13.getUser(r14)
                boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r13)
                if (r14 == 0) goto L_0x008b
                r17 = r2
                r18 = r3
                r20 = r6
                goto L_0x011e
            L_0x008b:
                java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r13)
                java.lang.String r14 = r14.toLowerCase()
                org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r15 = r15.getTranslitString(r14)
                boolean r16 = r14.equals(r15)
                if (r16 == 0) goto L_0x00a2
                r15 = 0
            L_0x00a2:
                r16 = 0
                int r4 = r6.length
                r17 = r2
                r2 = 0
            L_0x00a8:
                if (r2 >= r4) goto L_0x0113
                r18 = r3
                r3 = r6[r2]
                boolean r19 = r14.startsWith(r3)
                if (r19 != 0) goto L_0x00fd
                r19 = r4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r20 = r6
                java.lang.String r6 = " "
                r4.append(r6)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r14.contains(r4)
                if (r4 != 0) goto L_0x0101
                if (r15 == 0) goto L_0x00ed
                boolean r4 = r15.startsWith(r3)
                if (r4 != 0) goto L_0x0101
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r6)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r15.contains(r4)
                if (r4 == 0) goto L_0x00ed
                goto L_0x0101
            L_0x00ed:
                java.lang.String r4 = r13.username
                if (r4 == 0) goto L_0x0104
                java.lang.String r4 = r13.username
                boolean r4 = r4.startsWith(r3)
                if (r4 == 0) goto L_0x0104
                r4 = 2
                r16 = r4
                goto L_0x0104
            L_0x00fd:
                r19 = r4
                r20 = r6
            L_0x0101:
                r4 = 1
                r16 = r4
            L_0x0104:
                if (r16 == 0) goto L_0x010a
                r5.add(r10)
                goto L_0x011e
            L_0x010a:
                int r2 = r2 + 1
                r3 = r18
                r4 = r19
                r6 = r20
                goto L_0x00a8
            L_0x0113:
                r18 = r3
                r20 = r6
                goto L_0x011e
            L_0x0118:
                r17 = r2
                r18 = r3
                r20 = r6
            L_0x011e:
                int r7 = r7 + 1
                r2 = r17
                r3 = r18
                r6 = r20
                r4 = 0
                goto L_0x004a
            L_0x0129:
                r0.updateSearchResults(r5, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.m4051x8591429b(java.lang.String, int, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> participants, int searchId) {
            AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda0(this, searchId, participants));
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-GroupVoipInviteAlert$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4054x4fvar_acb(int searchId, ArrayList participants) {
            if (searchId == this.lastSearchId) {
                this.searchInProgress = false;
                if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat)) {
                    this.searchAdapterHelper.addGroupMembers(participants);
                }
                boolean emptyViewWasVisible = true;
                int oldItemCount = getItemCount() - 1;
                if (GroupVoipInviteAlert.this.emptyView.getVisibility() != 0) {
                    emptyViewWasVisible = false;
                }
                notifyDataSetChanged();
                if (getItemCount() > oldItemCount) {
                    GroupVoipInviteAlert.this.showItemsAnimated(oldItemCount);
                }
                if (!this.searchInProgress && !this.searchAdapterHelper.isSearchInProgress() && GroupVoipInviteAlert.this.listView.emptyViewIsVisible()) {
                    GroupVoipInviteAlert.this.emptyView.showProgress(false, emptyViewWasVisible);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if ((!(holder.itemView instanceof ManageChatUserCell) || !GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(((ManageChatUserCell) holder.itemView).getUserId()))) && holder.getItemViewType() == 0) {
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
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                int i = this.totalCount;
                this.groupStartRow = i;
                this.totalCount = i + count + 1;
            } else {
                this.groupStartRow = -1;
            }
            int count2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count2 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + count2 + 1;
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                switch(r6) {
                    case 0: goto L_0x0039;
                    case 1: goto L_0x0022;
                    case 2: goto L_0x000b;
                    default: goto L_0x0003;
                }
            L_0x0003:
                android.view.View r0 = new android.view.View
                android.content.Context r1 = r4.mContext
                r0.<init>(r1)
                goto L_0x0067
            L_0x000b:
                android.view.View r0 = new android.view.View
                android.content.Context r1 = r4.mContext
                r0.<init>(r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = 1113587712(0x42600000, float:56.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                goto L_0x0067
            L_0x0022:
                org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r4.mContext
                r0.<init>(r1)
                java.lang.String r1 = "voipgroup_actionBarUnscrolled"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r0.setBackgroundColor(r1)
                java.lang.String r1 = "voipgroup_searchPlaceholder"
                r0.setTextColor(r1)
                r1 = r0
                goto L_0x0067
            L_0x0039:
                org.telegram.ui.Cells.ManageChatUserCell r0 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r4.mContext
                r2 = 0
                r3 = 2
                r0.<init>(r1, r3, r3, r2)
                r1 = 2131165811(0x7var_, float:1.794585E38)
                r0.setCustomRightImage(r1)
                java.lang.String r1 = "voipgroup_nameText"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r0.setNameColor(r1)
                java.lang.String r1 = "voipgroup_lastSeenTextUnscrolled"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                java.lang.String r2 = "voipgroup_listeningText"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setStatusColors(r1, r2)
                java.lang.String r1 = "voipgroup_listViewBackground"
                r0.setDividerColor(r1)
                r1 = r0
            L_0x0067:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: android.text.SpannableStringBuilder} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.String} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0149  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x016e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r1 = r20
                r2 = r21
                r0 = r22
                int r3 = r21.getItemViewType()
                switch(r3) {
                    case 0: goto L_0x0037;
                    case 1: goto L_0x000f;
                    default: goto L_0x000d;
                }
            L_0x000d:
                goto L_0x0195
            L_0x000f:
                android.view.View r3 = r2.itemView
                org.telegram.ui.Cells.GraySectionCell r3 = (org.telegram.ui.Cells.GraySectionCell) r3
                int r4 = r1.groupStartRow
                if (r0 != r4) goto L_0x0025
                r4 = 2131624845(0x7f0e038d, float:1.8876881E38)
                java.lang.String r5 = "ChannelMembers"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0195
            L_0x0025:
                int r4 = r1.globalStartRow
                if (r0 != r4) goto L_0x0195
                r4 = 2131625975(0x7f0e07f7, float:1.8879173E38)
                java.lang.String r5 = "GlobalSearch"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x0195
            L_0x0037:
                org.telegram.tgnet.TLObject r3 = r1.getItem(r0)
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.User
                if (r4 == 0) goto L_0x0043
                r4 = r3
                org.telegram.tgnet.TLRPC$User r4 = (org.telegram.tgnet.TLRPC.User) r4
                goto L_0x007e
            L_0x0043:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.ChannelParticipant
                if (r4 == 0) goto L_0x0063
                org.telegram.ui.Components.GroupVoipInviteAlert r4 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r4 = r4.currentAccount
                org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                r5 = r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r5 = (org.telegram.tgnet.TLRPC.ChannelParticipant) r5
                org.telegram.tgnet.TLRPC$Peer r5 = r5.peer
                long r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
                goto L_0x007e
            L_0x0063:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.ChatParticipant
                if (r4 == 0) goto L_0x0194
                org.telegram.ui.Components.GroupVoipInviteAlert r4 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r4 = r4.currentAccount
                org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                r5 = r3
                org.telegram.tgnet.TLRPC$ChatParticipant r5 = (org.telegram.tgnet.TLRPC.ChatParticipant) r5
                long r5 = r5.user_id
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            L_0x007e:
                java.lang.String r5 = r4.username
                r6 = 0
                r7 = 0
                org.telegram.ui.Adapters.SearchAdapterHelper r8 = r1.searchAdapterHelper
                java.util.ArrayList r8 = r8.getGroupSearch()
                int r8 = r8.size()
                r9 = 0
                r10 = 0
                if (r8 == 0) goto L_0x00a6
                int r11 = r8 + 1
                if (r11 <= r0) goto L_0x009f
                org.telegram.ui.Adapters.SearchAdapterHelper r11 = r1.searchAdapterHelper
                java.lang.String r10 = r11.getLastFoundChannel()
                r9 = 1
                r11 = r10
                r10 = r9
                r9 = r0
                goto L_0x00a9
            L_0x009f:
                int r11 = r8 + 1
                int r0 = r0 - r11
                r11 = r10
                r10 = r9
                r9 = r0
                goto L_0x00a9
            L_0x00a6:
                r11 = r10
                r10 = r9
                r9 = r0
            L_0x00a9:
                java.lang.String r13 = "voipgroup_listeningText"
                r14 = -1
                if (r10 != 0) goto L_0x013f
                if (r5 == 0) goto L_0x013f
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.util.ArrayList r0 = r0.getGlobalSearch()
                int r8 = r0.size()
                if (r8 == 0) goto L_0x0138
                int r0 = r8 + 1
                if (r0 <= r9) goto L_0x0131
                org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
                java.lang.String r0 = r0.getLastFoundUsername()
                java.lang.String r15 = "@"
                boolean r16 = r0.startsWith(r15)
                if (r16 == 0) goto L_0x00d5
                r12 = 1
                java.lang.String r0 = r0.substring(r12)
                r12 = r0
                goto L_0x00d6
            L_0x00d5:
                r12 = r0
            L_0x00d6:
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0125 }
                r0.<init>()     // Catch:{ Exception -> 0x0125 }
                r0.append(r15)     // Catch:{ Exception -> 0x0125 }
                r0.append(r5)     // Catch:{ Exception -> 0x0125 }
                int r15 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r5, r12)     // Catch:{ Exception -> 0x0125 }
                r16 = r15
                if (r15 == r14) goto L_0x011d
                int r15 = r12.length()     // Catch:{ Exception -> 0x0125 }
                if (r16 != 0) goto L_0x00f4
                int r15 = r15 + 1
                r14 = r16
                goto L_0x00f8
            L_0x00f4:
                int r16 = r16 + 1
                r14 = r16
            L_0x00f8:
                r17 = r3
                android.text.style.ForegroundColorSpan r3 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0117 }
                r18 = r6
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)     // Catch:{ Exception -> 0x0113 }
                r3.<init>(r6)     // Catch:{ Exception -> 0x0113 }
                int r6 = r14 + r15
                r19 = r7
                r7 = 33
                r0.setSpan(r3, r14, r6, r7)     // Catch:{ Exception -> 0x0111 }
                r16 = r14
                goto L_0x0123
            L_0x0111:
                r0 = move-exception
                goto L_0x012c
            L_0x0113:
                r0 = move-exception
                r19 = r7
                goto L_0x012c
            L_0x0117:
                r0 = move-exception
                r18 = r6
                r19 = r7
                goto L_0x012c
            L_0x011d:
                r17 = r3
                r18 = r6
                r19 = r7
            L_0x0123:
                r6 = r0
                goto L_0x0147
            L_0x0125:
                r0 = move-exception
                r17 = r3
                r18 = r6
                r19 = r7
            L_0x012c:
                r6 = r5
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0147
            L_0x0131:
                r17 = r3
                r18 = r6
                r19 = r7
                goto L_0x0145
            L_0x0138:
                r17 = r3
                r18 = r6
                r19 = r7
                goto L_0x0145
            L_0x013f:
                r17 = r3
                r18 = r6
                r19 = r7
            L_0x0145:
                r6 = r18
            L_0x0147:
                if (r11 == 0) goto L_0x016e
                java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r4)
                android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
                r3.<init>(r0)
                r7 = r3
                int r3 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r0, r11)
                r12 = -1
                if (r3 == r12) goto L_0x0170
                android.text.style.ForegroundColorSpan r12 = new android.text.style.ForegroundColorSpan
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
                r12.<init>(r13)
                int r13 = r11.length()
                int r13 = r13 + r3
                r14 = 33
                r7.setSpan(r12, r3, r13, r14)
                goto L_0x0170
            L_0x016e:
                r7 = r19
            L_0x0170:
                android.view.View r0 = r2.itemView
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                java.lang.Integer r3 = java.lang.Integer.valueOf(r9)
                r0.setTag(r3)
                org.telegram.ui.Components.GroupVoipInviteAlert r3 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                java.util.HashSet r3 = r3.invitedUsers
                long r12 = r4.id
                java.lang.Long r12 = java.lang.Long.valueOf(r12)
                boolean r3 = r3.contains(r12)
                r0.setCustomImageVisible(r3)
                r3 = 0
                r0.setData(r4, r7, r6, r3)
                r0 = r9
                goto L_0x0195
            L_0x0194:
                return
            L_0x0195:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.emptyRow) {
                return 2;
            }
            if (i == this.lastRow) {
                return 3;
            }
            if (i == this.globalStartRow || i == this.groupStartRow) {
                return 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if ((holder.itemView instanceof ManageChatUserCell) && GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(((ManageChatUserCell) holder.itemView).getUserId()))) {
                return false;
            }
            int viewType = holder.getItemViewType();
            if (viewType == 0 || viewType == 1) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return GroupVoipInviteAlert.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                java.lang.String r0 = "voipgroup_actionBarUnscrolled"
                java.lang.String r1 = "voipgroup_actionBar"
                r2 = 6
                java.lang.String r3 = "voipgroup_listeningText"
                switch(r9) {
                    case 0: goto L_0x0065;
                    case 1: goto L_0x0056;
                    case 2: goto L_0x0041;
                    case 3: goto L_0x002a;
                    case 4: goto L_0x000a;
                    case 5: goto L_0x0013;
                    default: goto L_0x000a;
                }
            L_0x000a:
                android.view.View r0 = new android.view.View
                android.content.Context r1 = r7.mContext
                r0.<init>(r1)
                goto L_0x008f
            L_0x0013:
                org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r3 = r7.mContext
                r1.<init>(r3)
                r1.setViewType(r2)
                r2 = 1
                r1.setIsSingleCell(r2)
                java.lang.String r2 = "voipgroup_inviteMembersBackground"
                java.lang.String r3 = "voipgroup_searchBackground"
                r1.setColors(r2, r3, r0)
                r0 = r1
                goto L_0x008f
            L_0x002a:
                android.view.View r0 = new android.view.View
                android.content.Context r1 = r7.mContext
                r0.<init>(r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = 1113587712(0x42600000, float:56.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                goto L_0x008f
            L_0x0041:
                org.telegram.ui.Cells.GraySectionCell r1 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                java.lang.String r0 = "voipgroup_searchPlaceholder"
                r1.setTextColor(r0)
                r0 = r1
                goto L_0x008f
            L_0x0056:
                org.telegram.ui.Cells.ManageChatTextCell r0 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r2 = r7.mContext
                r0.<init>(r2)
                r0.setColors(r3, r3)
                r0.setDividerColor(r1)
                r1 = r0
                goto L_0x008f
            L_0x0065:
                org.telegram.ui.Cells.ManageChatUserCell r0 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r4 = r7.mContext
                r5 = 2
                r6 = 0
                r0.<init>(r4, r2, r5, r6)
                r2 = 2131165811(0x7var_, float:1.794585E38)
                r0.setCustomRightImage(r2)
                java.lang.String r2 = "voipgroup_nameText"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setNameColor(r2)
                java.lang.String r2 = "voipgroup_lastSeenTextUnscrolled"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r0.setStatusColors(r2, r3)
                r0.setDividerColor(r1)
                r1 = r0
            L_0x008f:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int lastRow;
            long userId;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    TLObject item = getItem(position);
                    if (position < GroupVoipInviteAlert.this.participantsStartRow || position >= GroupVoipInviteAlert.this.participantsEndRow) {
                        lastRow = GroupVoipInviteAlert.this.contactsEndRow;
                    } else {
                        lastRow = GroupVoipInviteAlert.this.participantsEndRow;
                    }
                    if (item instanceof TLRPC.TL_contact) {
                        userId = ((TLRPC.TL_contact) item).user_id;
                    } else if (item instanceof TLRPC.User) {
                        userId = ((TLRPC.User) item).id;
                    } else if (item instanceof TLRPC.ChannelParticipant) {
                        userId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) item).peer);
                    } else {
                        userId = ((TLRPC.ChatParticipant) item).user_id;
                    }
                    TLRPC.User user = MessagesController.getInstance(GroupVoipInviteAlert.this.currentAccount).getUser(Long.valueOf(userId));
                    if (user != null) {
                        userCell.setCustomImageVisible(GroupVoipInviteAlert.this.invitedUsers.contains(Long.valueOf(user.id)));
                        if (position != lastRow - 1) {
                            z = true;
                        }
                        userCell.setData(user, (CharSequence) null, (CharSequence) null, z);
                        return;
                    }
                    return;
                case 1:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    if (position == GroupVoipInviteAlert.this.addNewRow) {
                        actionCell.setText(LocaleController.getString("VoipGroupCopyInviteLink", NUM), (String) null, NUM, 7, (!GroupVoipInviteAlert.this.loadingUsers || GroupVoipInviteAlert.this.firstLoaded) && GroupVoipInviteAlert.this.membersHeaderRow == -1 && !GroupVoipInviteAlert.this.participants.isEmpty());
                        return;
                    }
                    return;
                case 2:
                    GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                    if (position == GroupVoipInviteAlert.this.membersHeaderRow) {
                        sectionCell.setText(LocaleController.getString("ChannelOtherMembers", NUM));
                        return;
                    } else if (position != GroupVoipInviteAlert.this.contactsHeaderRow) {
                        return;
                    } else {
                        if (GroupVoipInviteAlert.this.showContacts) {
                            sectionCell.setText(LocaleController.getString("YourContactsToInvite", NUM));
                            return;
                        } else {
                            sectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                            return;
                        }
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if ((position >= GroupVoipInviteAlert.this.participantsStartRow && position < GroupVoipInviteAlert.this.participantsEndRow) || (position >= GroupVoipInviteAlert.this.contactsStartRow && position < GroupVoipInviteAlert.this.contactsEndRow)) {
                return 0;
            }
            if (position == GroupVoipInviteAlert.this.addNewRow) {
                return 1;
            }
            if (position == GroupVoipInviteAlert.this.membersHeaderRow || position == GroupVoipInviteAlert.this.contactsHeaderRow) {
                return 2;
            }
            if (position == GroupVoipInviteAlert.this.emptyRow) {
                return 3;
            }
            if (position == GroupVoipInviteAlert.this.lastRow) {
                return 4;
            }
            if (position == GroupVoipInviteAlert.this.flickerProgressRow) {
                return 5;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (position >= GroupVoipInviteAlert.this.participantsStartRow && position < GroupVoipInviteAlert.this.participantsEndRow) {
                return (TLObject) GroupVoipInviteAlert.this.participants.get(position - GroupVoipInviteAlert.this.participantsStartRow);
            }
            if (position < GroupVoipInviteAlert.this.contactsStartRow || position >= GroupVoipInviteAlert.this.contactsEndRow) {
                return null;
            }
            return (TLObject) GroupVoipInviteAlert.this.contacts.get(position - GroupVoipInviteAlert.this.contactsStartRow);
        }
    }

    /* access modifiers changed from: protected */
    public void search(String text) {
        this.searchAdapter.searchUsers(text);
    }

    /* access modifiers changed from: protected */
    public void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
        this.delegate.needOpenSearch(ev, searchEditText);
    }
}
