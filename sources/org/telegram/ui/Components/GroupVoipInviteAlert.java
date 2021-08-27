package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
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
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
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
    /* access modifiers changed from: private */
    public boolean showContacts;

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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
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

    private void fillContacts() {
        int i;
        if (this.showContacts) {
            this.contacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
            int i2 = UserConfig.getInstance(this.currentAccount).clientUserId;
            int i3 = 0;
            int size = this.contacts.size();
            while (i3 < size) {
                if ((this.contacts.get(i3) instanceof TLRPC$TL_contact) && ((i = ((TLRPC$TL_contact) this.contacts.get(i3)).user_id) == i2 || this.ignoredUsers.indexOfKey(i) >= 0 || this.invitedUsers.contains(Integer.valueOf(i)))) {
                    this.contacts.remove(i3);
                    i3--;
                    size--;
                }
                i3++;
            }
            Collections.sort(this.contacts, new GroupVoipInviteAlert$$ExternalSyntheticLambda1(MessagesController.getInstance(this.currentAccount), ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0041 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x004c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0057 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0060 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ int lambda$fillContacts$1(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLObject r4, org.telegram.tgnet.TLObject r5) {
        /*
            org.telegram.tgnet.TLRPC$TL_contact r5 = (org.telegram.tgnet.TLRPC$TL_contact) r5
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r2.getUser(r5)
            org.telegram.tgnet.TLRPC$TL_contact r4 = (org.telegram.tgnet.TLRPC$TL_contact) r4
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            r4 = 50000(0xCLASSNAME, float:7.0065E-41)
            r0 = 0
            if (r5 == 0) goto L_0x002c
            boolean r1 = r5.self
            if (r1 == 0) goto L_0x0025
            int r5 = r3 + r4
            goto L_0x002d
        L_0x0025:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
            if (r5 == 0) goto L_0x002c
            int r5 = r5.expires
            goto L_0x002d
        L_0x002c:
            r5 = 0
        L_0x002d:
            if (r2 == 0) goto L_0x003c
            boolean r1 = r2.self
            if (r1 == 0) goto L_0x0035
            int r3 = r3 + r4
            goto L_0x003d
        L_0x0035:
            org.telegram.tgnet.TLRPC$UserStatus r2 = r2.status
            if (r2 == 0) goto L_0x003c
            int r3 = r2.expires
            goto L_0x003d
        L_0x003c:
            r3 = 0
        L_0x003d:
            r2 = -1
            r4 = 1
            if (r5 <= 0) goto L_0x004a
            if (r3 <= 0) goto L_0x004a
            if (r5 <= r3) goto L_0x0046
            return r4
        L_0x0046:
            if (r5 >= r3) goto L_0x0049
            return r2
        L_0x0049:
            return r0
        L_0x004a:
            if (r5 >= 0) goto L_0x0055
            if (r3 >= 0) goto L_0x0055
            if (r5 <= r3) goto L_0x0051
            return r4
        L_0x0051:
            if (r5 >= r3) goto L_0x0054
            return r2
        L_0x0054:
            return r0
        L_0x0055:
            if (r5 >= 0) goto L_0x0059
            if (r3 > 0) goto L_0x005d
        L_0x0059:
            if (r5 != 0) goto L_0x005e
            if (r3 == 0) goto L_0x005e
        L_0x005d:
            return r2
        L_0x005e:
            if (r3 < 0) goto L_0x0064
            if (r5 == 0) goto L_0x0063
            goto L_0x0064
        L_0x0063:
            return r0
        L_0x0064:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.lambda$fillContacts$1(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLObject):int");
    }

    /* access modifiers changed from: protected */
    public void loadChatParticipants(int i, int i2, boolean z) {
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray;
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
                    if (i5 != i3 && ((sparseArray = this.ignoredUsers) == null || sparseArray.indexOfKey(i5) < 0)) {
                        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChatParticipant.user_id));
                        if (!UserObject.isDeleted(user) && !user.bot) {
                            this.participants.add(tLRPC$ChatParticipant);
                            this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
                        }
                    }
                }
                if (this.participants.isEmpty()) {
                    this.showContacts = true;
                    fillContacts();
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new GroupVoipInviteAlert$$ExternalSyntheticLambda3(this, tLRPC$TL_channels_getParticipants));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChatParticipants$4(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChatParticipants$3(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        int i;
        SparseArray<TLObject> sparseArray;
        ArrayList<TLObject> arrayList;
        boolean z;
        SparseArray<TLRPC$TL_groupCallParticipant> sparseArray2;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_channels_channelParticipants.chats, false);
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            int i2 = 0;
            while (true) {
                if (i2 >= tLRPC$TL_channels_channelParticipants.participants.size()) {
                    break;
                } else if (MessageObject.getPeerId(tLRPC$TL_channels_channelParticipants.participants.get(i2).peer) == clientUserId) {
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
                sparseArray.put(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer), tLRPC$ChannelParticipant);
            }
            int size2 = this.participants.size();
            int i4 = 0;
            while (i4 < size2) {
                int peerId = MessageObject.getPeerId(((TLRPC$ChannelParticipant) this.participants.get(i4)).peer);
                if (this.contactsMap.get(peerId) == null && ((sparseArray2 = this.ignoredUsers) == null || sparseArray2.indexOfKey(peerId) < 0)) {
                    z = false;
                } else {
                    z = true;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId));
                if ((user != null && user.bot) || UserObject.isDeleted(user)) {
                    z = true;
                }
                if (z) {
                    this.participants.remove(i4);
                    this.participantsMap.remove(peerId);
                    i4--;
                    size2--;
                }
                i4++;
            }
            try {
                if (this.info.participants_count <= 200) {
                    Collections.sort(arrayList, new GroupVoipInviteAlert$$ExternalSyntheticLambda2(this, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
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
            if (this.participants.isEmpty()) {
                this.showContacts = true;
                fillContacts();
            }
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
    public /* synthetic */ int lambda$loadChatParticipants$2(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        int i3;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer)));
        TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject2).peer)));
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
            this.searchAdapterHelper.mergeResults((ArrayList<Object>) null);
            this.searchAdapterHelper.queryServerSearch((String) null, true, false, true, false, false, GroupVoipInviteAlert.this.currentChat.id, false, 2, -1);
            if (!TextUtils.isEmpty(str)) {
                GroupVoipInviteAlert.this.emptyView.showProgress(true, true);
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(false, 0);
                notifyDataSetChanged();
                GroupVoipInviteAlert.this.listView.setAnimateEmptyView(true, 0);
                this.searchInProgress = true;
                int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1 groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1 = new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1(this, str, i);
                this.searchRunnable = groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(groupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda1, 300);
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
        public /* synthetic */ void lambda$searchUsers$0(String str, int i) {
            if (this.searchRunnable != null) {
                this.searchRunnable = null;
                processSearch(str, i);
            }
        }

        private void processSearch(String str, int i) {
            AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda2(this, str, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$2(String str, int i) {
            ArrayList arrayList = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) && GroupVoipInviteAlert.this.info != null) {
                arrayList = new ArrayList(GroupVoipInviteAlert.this.info.participants.participants);
            }
            if (arrayList != null) {
                Utilities.searchQueue.postRunnable(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda3(this, str, i, arrayList));
            } else {
                String str2 = str;
                int i2 = i;
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatObject.canAddUsers(GroupVoipInviteAlert.this.currentChat), false, true, false, false, ChatObject.isChannel(GroupVoipInviteAlert.this.currentChat) ? GroupVoipInviteAlert.this.currentChat.id : 0, false, 2, i);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00dd, code lost:
            if (r14.contains(" " + r5) != false) goto L_0x00ed;
         */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00f5 A[LOOP:1: B:32:0x00a1->B:50:0x00f5, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x00f1 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$processSearch$1(java.lang.String r19, int r20, java.util.ArrayList r21) {
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
                if (r2 == 0) goto L_0x0100
                int r4 = r21.size()
                r10 = 0
            L_0x004e:
                if (r10 >= r4) goto L_0x0100
                java.lang.Object r11 = r2.get(r10)
                org.telegram.tgnet.TLObject r11 = (org.telegram.tgnet.TLObject) r11
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r12 == 0) goto L_0x0060
                r12 = r11
                org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC$ChatParticipant) r12
                int r12 = r12.user_id
                goto L_0x006d
            L_0x0060:
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r12 == 0) goto L_0x00fa
                r12 = r11
                org.telegram.tgnet.TLRPC$ChannelParticipant r12 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r12
                org.telegram.tgnet.TLRPC$Peer r12 = r12.peer
                int r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
            L_0x006d:
                org.telegram.ui.Components.GroupVoipInviteAlert r13 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r13 = r13.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                org.telegram.tgnet.TLRPC$User r12 = r13.getUser(r12)
                boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r12)
                if (r13 == 0) goto L_0x0087
                goto L_0x00fa
            L_0x0087:
                java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r12)
                java.lang.String r13 = r13.toLowerCase()
                org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r14 = r14.getTranslitString(r13)
                boolean r15 = r13.equals(r14)
                if (r15 == 0) goto L_0x009e
                r14 = 0
            L_0x009e:
                r15 = 0
                r16 = 0
            L_0x00a1:
                if (r15 >= r8) goto L_0x00fa
                r5 = r9[r15]
                boolean r17 = r13.startsWith(r5)
                if (r17 != 0) goto L_0x00ed
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r7 = " "
                r6.append(r7)
                r6.append(r5)
                java.lang.String r6 = r6.toString()
                boolean r6 = r13.contains(r6)
                if (r6 != 0) goto L_0x00ed
                if (r14 == 0) goto L_0x00e0
                boolean r6 = r14.startsWith(r5)
                if (r6 != 0) goto L_0x00ed
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r7)
                r6.append(r5)
                java.lang.String r6 = r6.toString()
                boolean r6 = r14.contains(r6)
                if (r6 == 0) goto L_0x00e0
                goto L_0x00ed
            L_0x00e0:
                java.lang.String r6 = r12.username
                if (r6 == 0) goto L_0x00ef
                boolean r5 = r6.startsWith(r5)
                if (r5 == 0) goto L_0x00ef
                r16 = 2
                goto L_0x00ef
            L_0x00ed:
                r16 = 1
            L_0x00ef:
                if (r16 == 0) goto L_0x00f5
                r3.add(r11)
                goto L_0x00fa
            L_0x00f5:
                int r15 = r15 + 1
                r5 = 0
                r7 = 1
                goto L_0x00a1
            L_0x00fa:
                int r10 = r10 + 1
                r5 = 0
                r7 = 1
                goto L_0x004e
            L_0x0100:
                r0.updateSearchResults(r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupVoipInviteAlert.SearchAdapter.lambda$processSearch$1(java.lang.String, int, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> arrayList, int i) {
            AndroidUtilities.runOnUIThread(new GroupVoipInviteAlert$SearchAdapter$$ExternalSyntheticLambda0(this, i, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$3(int i, ArrayList arrayList) {
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
                r3 = 2131165761(0x7var_, float:1.7945748E38)
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
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00f2  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                r13 = this;
                int r0 = r14.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x0033
                if (r0 == r1) goto L_0x000b
                goto L_0x0133
            L_0x000b:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.GraySectionCell r14 = (org.telegram.ui.Cells.GraySectionCell) r14
                int r0 = r13.groupStartRow
                if (r15 != r0) goto L_0x0021
                r15 = 2131624749(0x7f0e032d, float:1.8876687E38)
                java.lang.String r0 = "ChannelMembers"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x0133
            L_0x0021:
                int r0 = r13.globalStartRow
                if (r15 != r0) goto L_0x0133
                r15 = 2131625752(0x7f0e0718, float:1.887872E38)
                java.lang.String r0 = "GlobalSearch"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x0133
            L_0x0033:
                org.telegram.tgnet.TLObject r0 = r13.getItem(r15)
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$User
                if (r2 == 0) goto L_0x003e
                org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
                goto L_0x0077
            L_0x003e:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChannelParticipant
                if (r2 == 0) goto L_0x005d
                org.telegram.ui.Components.GroupVoipInviteAlert r2 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.tgnet.TLRPC$ChannelParticipant r0 = (org.telegram.tgnet.TLRPC$ChannelParticipant) r0
                org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
                int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
                goto L_0x0077
            L_0x005d:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$ChatParticipant
                if (r2 == 0) goto L_0x0133
                org.telegram.ui.Components.GroupVoipInviteAlert r2 = org.telegram.ui.Components.GroupVoipInviteAlert.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            L_0x0077:
                java.lang.String r2 = r0.username
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.util.ArrayList r3 = r3.getGroupSearch()
                int r3 = r3.size()
                r4 = 0
                r5 = 0
                if (r3 == 0) goto L_0x0093
                int r3 = r3 + r1
                if (r3 <= r15) goto L_0x0092
                org.telegram.ui.Adapters.SearchAdapterHelper r3 = r13.searchAdapterHelper
                java.lang.String r3 = r3.getLastFoundChannel()
                r6 = 1
                goto L_0x0095
            L_0x0092:
                int r15 = r15 - r3
            L_0x0093:
                r3 = r5
                r6 = 0
            L_0x0095:
                r7 = 33
                java.lang.String r8 = "voipgroup_listeningText"
                r9 = -1
                if (r6 != 0) goto L_0x00ef
                if (r2 == 0) goto L_0x00ef
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.util.ArrayList r6 = r6.getGlobalSearch()
                int r6 = r6.size()
                if (r6 == 0) goto L_0x00ef
                int r6 = r6 + r1
                if (r6 <= r15) goto L_0x00ef
                org.telegram.ui.Adapters.SearchAdapterHelper r6 = r13.searchAdapterHelper
                java.lang.String r6 = r6.getLastFoundUsername()
                java.lang.String r10 = "@"
                boolean r11 = r6.startsWith(r10)
                if (r11 == 0) goto L_0x00bf
                java.lang.String r6 = r6.substring(r1)
            L_0x00bf:
                android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x00ea }
                r1.<init>()     // Catch:{ Exception -> 0x00ea }
                r1.append(r10)     // Catch:{ Exception -> 0x00ea }
                r1.append(r2)     // Catch:{ Exception -> 0x00ea }
                int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r6)     // Catch:{ Exception -> 0x00ea }
                if (r10 == r9) goto L_0x00e8
                int r6 = r6.length()     // Catch:{ Exception -> 0x00ea }
                if (r10 != 0) goto L_0x00d9
                int r6 = r6 + 1
                goto L_0x00db
            L_0x00d9:
                int r10 = r10 + 1
            L_0x00db:
                android.text.style.ForegroundColorSpan r11 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x00ea }
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r8)     // Catch:{ Exception -> 0x00ea }
                r11.<init>(r12)     // Catch:{ Exception -> 0x00ea }
                int r6 = r6 + r10
                r1.setSpan(r11, r10, r6, r7)     // Catch:{ Exception -> 0x00ea }
            L_0x00e8:
                r2 = r1
                goto L_0x00f0
            L_0x00ea:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
                goto L_0x00f0
            L_0x00ef:
                r2 = r5
            L_0x00f0:
                if (r3 == 0) goto L_0x0112
                java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r0)
                android.text.SpannableStringBuilder r5 = new android.text.SpannableStringBuilder
                r5.<init>(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r1, r3)
                if (r1 == r9) goto L_0x0112
                android.text.style.ForegroundColorSpan r6 = new android.text.style.ForegroundColorSpan
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r6.<init>(r8)
                int r3 = r3.length()
                int r3 = r3 + r1
                r5.setSpan(r6, r1, r3, r7)
            L_0x0112:
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
            L_0x0133:
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
                r0 = 2131165761(0x7var_, float:1.7945748E38)
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
                if (item instanceof TLRPC$TL_contact) {
                    i3 = ((TLRPC$TL_contact) item).user_id;
                } else if (item instanceof TLRPC$User) {
                    i3 = ((TLRPC$User) item).id;
                } else if (item instanceof TLRPC$ChannelParticipant) {
                    i3 = MessageObject.getPeerId(((TLRPC$ChannelParticipant) item).peer);
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
                } else if (i != GroupVoipInviteAlert.this.contactsHeaderRow) {
                } else {
                    if (GroupVoipInviteAlert.this.showContacts) {
                        graySectionCell.setText(LocaleController.getString("YourContactsToInvite", NUM));
                    } else {
                        graySectionCell.setText(LocaleController.getString("GroupContacts", NUM));
                    }
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
