package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0646ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Adapters.SearchAdapterHelper;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.ManageChatTextCell;
import org.telegram.p005ui.Cells.ManageChatUserCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChannelEditActivity */
public class ChannelEditActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 1;
    private int blockedUsersRow;
    private int chat_id;
    private Chat currentChat;
    private int eventLogRow;
    private ChatFull info;
    private int infoRow;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int loadMoreMembersRow;
    private boolean loadingUsers;
    private int managementRow;
    private int membersEndRow;
    private int membersSection2Row;
    private int membersSectionRow;
    private int membersStartRow;
    private SparseArray<ChatParticipant> participantsMap = new SparseArray();
    private int permissionsRow;
    private int rowCount = 0;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private ArrayList<Integer> sortedUsers;
    private boolean usersEndReached;

    /* renamed from: org.telegram.ui.ChannelEditActivity$1 */
    class C19401 extends ActionBarMenuOnItemClick {
        C19401() {
        }

        public void onItemClick(int id) {
            if (ChannelEditActivity.this.getParentActivity() != null && id == -1) {
                ChannelEditActivity.this.lambda$checkDiscard$69$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$2 */
    class C19412 extends ActionBarMenuItemSearchListener {
        C19412() {
        }

        public void onSearchExpand() {
            ChannelEditActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            ChannelEditActivity.this.searchListViewAdapter.searchDialogs(null);
            ChannelEditActivity.this.searching = false;
            ChannelEditActivity.this.searchWas = false;
            ChannelEditActivity.this.listView.setAdapter(ChannelEditActivity.this.listViewAdapter);
            ChannelEditActivity.this.listViewAdapter.notifyDataSetChanged();
            ChannelEditActivity.this.listView.setFastScrollVisible(true);
            ChannelEditActivity.this.listView.setVerticalScrollBarEnabled(false);
        }

        public void onTextChanged(EditText editText) {
            if (ChannelEditActivity.this.searchListViewAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ChannelEditActivity.this.searchWas = true;
                    if (ChannelEditActivity.this.listView != null) {
                        ChannelEditActivity.this.listView.setAdapter(ChannelEditActivity.this.searchListViewAdapter);
                        ChannelEditActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChannelEditActivity.this.listView.setFastScrollVisible(false);
                        ChannelEditActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChannelEditActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$4 */
    class C19424 extends OnScrollListener {
        C19424() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (ChannelEditActivity.this.participantsMap != null && ChannelEditActivity.this.loadMoreMembersRow != -1 && ChannelEditActivity.this.layoutManager.findLastVisibleItemPosition() > ChannelEditActivity.this.loadMoreMembersRow - 8) {
                ChannelEditActivity.this.getChannelParticipants(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ManageChatUserCell(this.mContext, 8, true);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ManageChatUserCell) view).setDelegate(new ChannelEditActivity$ListAdapter$$Lambda$0(this));
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 3:
                    view = new LoadingCell(this.mContext);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        final /* synthetic */ boolean lambda$onCreateViewHolder$0$ChannelEditActivity$ListAdapter(ManageChatUserCell cell, boolean click) {
            ChatParticipant part;
            int i = ((Integer) cell.getTag()).intValue();
            if (ChannelEditActivity.this.sortedUsers.isEmpty()) {
                part = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow);
            } else {
                part = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue());
            }
            return ChannelEditActivity.this.createMenuForParticipant((TL_chatChannelParticipant) part, null, !click);
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public void onBindViewHolder(ViewHolder holder, int i) {
            boolean z = false;
            String str = null;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatTextCell textCell = holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    if (i == ChannelEditActivity.this.managementRow) {
                        textCell.setText(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), ChannelEditActivity.this.info != null ? String.format("%d", new Object[]{Integer.valueOf(ChannelEditActivity.this.info.admins_count)}) : null, R.drawable.group_admin, ChannelEditActivity.this.blockedUsersRow != -1);
                        return;
                    } else if (i == ChannelEditActivity.this.blockedUsersRow) {
                        String string = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                        if (ChannelEditActivity.this.info != null) {
                            str = String.format("%d", new Object[]{Integer.valueOf(ChannelEditActivity.this.info.kicked_count + ChannelEditActivity.this.info.banned_count)});
                        }
                        textCell.setText(string, str, R.drawable.group_banned, false);
                        return;
                    } else if (i == ChannelEditActivity.this.eventLogRow) {
                        textCell.setText(LocaleController.getString("EventLog", R.string.EventLog), null, R.drawable.group_log, true);
                        return;
                    } else if (i == ChannelEditActivity.this.infoRow) {
                        textCell.setText(ChannelEditActivity.this.currentChat.megagroup ? LocaleController.getString("EventLogFilterGroupInfo", R.string.EventLogFilterGroupInfo) : LocaleController.getString("EventLogFilterChannelInfo", R.string.EventLogFilterChannelInfo), null, R.drawable.group_edit, true);
                        return;
                    } else {
                        if (i == ChannelEditActivity.this.permissionsRow) {
                        }
                        return;
                    }
                case 1:
                    ChatParticipant part;
                    ManageChatUserCell userCell = holder.itemView;
                    userCell.setTag(Integer.valueOf(i));
                    if (ChannelEditActivity.this.sortedUsers.isEmpty()) {
                        part = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow);
                    } else {
                        part = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue());
                    }
                    if (part != null) {
                        if (part instanceof TL_chatChannelParticipant) {
                            ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) part).channelParticipant;
                            if ((channelParticipant instanceof TL_channelParticipantCreator) || (channelParticipant instanceof TL_channelParticipantAdmin)) {
                                z = true;
                            }
                            userCell.setIsAdmin(z);
                        } else {
                            userCell.setIsAdmin(part instanceof TL_chatParticipantAdmin);
                        }
                        userCell.setData(MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(part.user_id)), null, null);
                        return;
                    }
                    return;
                case 2:
                    if (i != ChannelEditActivity.this.membersSectionRow || ChannelEditActivity.this.membersStartRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 0 || type == 1) {
                return true;
            }
            return false;
        }

        public int getItemCount() {
            return ChannelEditActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ChannelEditActivity.this.managementRow || i == ChannelEditActivity.this.blockedUsersRow || i == ChannelEditActivity.this.infoRow || i == ChannelEditActivity.this.eventLogRow || i == ChannelEditActivity.this.permissionsRow) {
                return 0;
            }
            if (i >= ChannelEditActivity.this.membersStartRow && i < ChannelEditActivity.this.membersEndRow) {
                return 1;
            }
            if (i == ChannelEditActivity.this.membersSectionRow || i == ChannelEditActivity.this.membersSection2Row) {
                return 2;
            }
            if (i == ChannelEditActivity.this.loadMoreMembersRow) {
                return 3;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$SearchAdapter */
    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
        private Timer searchTimer;

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChannelEditActivity.this) {
                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }
            });
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            if (query == null) {
                this.searchAdapterHelper.queryServerSearch(null, false, false, true, true, ChannelEditActivity.this.chat_id, false);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        SearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m14e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new ChannelEditActivity$SearchAdapter$$Lambda$0(this, query));
        }

        final /* synthetic */ void lambda$processSearch$0$ChannelEditActivity$SearchAdapter(String query) {
            this.searchAdapterHelper.queryServerSearch(query, false, false, true, true, ChannelEditActivity.this.chat_id, false);
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchAdapterHelper.getGroupSearch().size();
        }

        public ChannelParticipant getItem(int i) {
            return (ChannelParticipant) this.searchAdapterHelper.getGroupSearch().get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ManageChatUserCell(this.mContext, 8, true);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ((ManageChatUserCell) view).setDelegate(new ChannelEditActivity$SearchAdapter$$Lambda$1(this));
            return new Holder(view);
        }

        final /* synthetic */ boolean lambda$onCreateViewHolder$1$ChannelEditActivity$SearchAdapter(ManageChatUserCell cell, boolean click) {
            return ChannelEditActivity.this.createMenuForParticipant(null, getItem(((Integer) cell.getTag()).intValue()), !click);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    User user;
                    boolean isAdmin;
                    TLObject object = getItem(position);
                    if (object instanceof User) {
                        user = (User) object;
                        ChatParticipant part = (ChatParticipant) ChannelEditActivity.this.participantsMap.get(user.f177id);
                        if (part instanceof TL_chatChannelParticipant) {
                            ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) part).channelParticipant;
                            isAdmin = (channelParticipant instanceof TL_channelParticipantCreator) || (channelParticipant instanceof TL_channelParticipantAdmin);
                        } else {
                            isAdmin = part instanceof TL_chatParticipantAdmin;
                        }
                    } else {
                        isAdmin = (object instanceof TL_channelParticipantAdmin) || (object instanceof TL_channelParticipantCreator);
                        user = MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) object).user_id));
                    }
                    CharSequence name = null;
                    String nameSearch = this.searchAdapterHelper.getLastFoundChannel();
                    if (nameSearch != null) {
                        String u = UserObject.getUserName(user);
                        name = new SpannableStringBuilder(u);
                        int idx = u.toLowerCase().indexOf(nameSearch);
                        if (idx != -1) {
                            ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                        }
                    }
                    ManageChatUserCell userCell = holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    userCell.setIsAdmin(isAdmin);
                    userCell.setData(user, name, null);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ChannelEditActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id", 0);
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        if (this.currentChat == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ChannelEditActivity$$Lambda$0(this, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
        }
        getChannelParticipants(true);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.sortedUsers = new ArrayList();
        updateRowsIds();
        return true;
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ChannelEditActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chat_id);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        Theme.createProfileResources(context);
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("ManageGroup", R.string.ManageGroup));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ManageChannel", R.string.ManageChannel));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19401());
        this.searchListViewAdapter = new SearchAdapter(context);
        this.actionBar.createMenu().addItem(1, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19412()).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.listViewAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        EmptyTextProgressView emptyView = new EmptyTextProgressView(context);
        emptyView.setShowAtCenter(true);
        emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        emptyView.showTextView();
        frameLayout.addView(emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(emptyView);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setOnItemClickListener(new ChannelEditActivity$$Lambda$1(this));
        this.listView.setOnItemLongClickListener(new ChannelEditActivity$$Lambda$2(this));
        this.listView.setOnScrollListener(new C19424());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$ChannelEditActivity(View view, int position) {
        if (getParentActivity() != null) {
            Bundle args;
            if (this.listView.getAdapter() == this.searchListViewAdapter) {
                args = new Bundle();
                args.putInt("user_id", this.searchListViewAdapter.getItem(position).user_id);
                presentFragment(new ProfileActivity(args));
            } else if (position >= this.membersStartRow && position < this.membersEndRow) {
                int user_id;
                if (this.sortedUsers.isEmpty()) {
                    user_id = ((ChatParticipant) this.info.participants.participants.get(position - this.membersStartRow)).user_id;
                } else {
                    user_id = ((ChatParticipant) this.info.participants.participants.get(((Integer) this.sortedUsers.get(position - this.membersStartRow)).intValue())).user_id;
                }
                args = new Bundle();
                args.putInt("user_id", user_id);
                presentFragment(new ProfileActivity(args));
            } else if (position == this.blockedUsersRow || position == this.managementRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chat_id);
                if (position == this.blockedUsersRow) {
                    args.putInt("type", 0);
                } else if (position == this.managementRow) {
                    args.putInt("type", 1);
                }
                presentFragment(new ChannelUsersActivity(args));
            } else if (position == this.permissionsRow) {
                ChannelPermissionsActivity permissions = new ChannelPermissionsActivity(this.chat_id);
                permissions.setInfo(this.info);
                presentFragment(permissions);
            } else if (position == this.eventLogRow) {
                presentFragment(new ChannelAdminLogActivity(this.currentChat));
            } else if (position == this.infoRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chat_id);
                ChannelEditInfoActivity fragment = new ChannelEditInfoActivity(args);
                fragment.setInfo(this.info);
                presentFragment(fragment);
            }
        }
    }

    final /* synthetic */ boolean lambda$createView$2$ChannelEditActivity(View view, int position) {
        if (position < this.membersStartRow || position >= this.membersEndRow) {
            return false;
        }
        if (getParentActivity() == null) {
            return false;
        }
        TL_chatChannelParticipant user;
        if (this.sortedUsers.isEmpty()) {
            user = (TL_chatChannelParticipant) this.info.participants.participants.get(position - this.membersStartRow);
        } else {
            user = (TL_chatChannelParticipant) this.info.participants.participants.get(((Integer) this.sortedUsers.get(position - this.membersStartRow)).intValue());
        }
        return createMenuForParticipant(user, null, false);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        boolean loadChannelParticipants = false;
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.f79id == this.chat_id) {
                boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
                if ((this.info instanceof TL_channelFull) && chatFull.participants == null && this.info != null) {
                    chatFull.participants = this.info.participants;
                }
                if (this.info == null && (chatFull instanceof TL_channelFull)) {
                    loadChannelParticipants = true;
                }
                this.info = chatFull;
                fetchUsersFromChannelInfo();
                updateRowsIds();
                if (this.listViewAdapter != null) {
                    this.listViewAdapter.notifyDataSetChanged();
                }
                Chat newChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (newChat != null) {
                    this.currentChat = newChat;
                }
                if (loadChannelParticipants || !byChannelUsers) {
                    getChannelParticipants(true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            lambda$null$11$ProfileActivity();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private void getChannelParticipants(boolean reload) {
        int i = 0;
        if (!this.loadingUsers && this.participantsMap != null && this.info != null) {
            int delay;
            this.loadingUsers = true;
            if (this.participantsMap.size() == 0 || !reload) {
                delay = 0;
            } else {
                delay = 300;
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            req.filter = new TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelEditActivity$$Lambda$3(this, req, delay)), this.classGuid);
        }
    }

    final /* synthetic */ void lambda$getChannelParticipants$4$ChannelEditActivity(TL_channels_getParticipants req, int delay, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelEditActivity$$Lambda$7(this, error, response, req), (long) delay);
    }

    final /* synthetic */ void lambda$null$3$ChannelEditActivity(TL_error error, TLObject response, TL_channels_getParticipants req) {
        if (error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            if (res.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                this.usersEndReached = true;
            }
            if (req.offset == 0) {
                this.participantsMap.clear();
                this.info.participants = new TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, res.participants);
            }
            for (int a = 0; a < res.participants.size(); a++) {
                TL_chatChannelParticipant participant = new TL_chatChannelParticipant();
                participant.channelParticipant = (ChannelParticipant) res.participants.get(a);
                participant.inviter_id = participant.channelParticipant.inviter_id;
                participant.user_id = participant.channelParticipant.user_id;
                participant.date = participant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(participant.user_id) < 0) {
                    this.info.participants.participants.add(participant);
                    this.participantsMap.put(participant.user_id, participant);
                }
            }
        }
        this.loadingUsers = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
    }

    public void setInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        fetchUsersFromChannelInfo();
    }

    private void fetchUsersFromChannelInfo() {
        if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
                this.participantsMap.put(chatParticipant.user_id, chatParticipant);
            }
        }
    }

    private void updateRowsIds() {
        int i;
        this.rowCount = 0;
        if (ChatObject.canEditInfo(this.currentChat)) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.infoRow = i;
        } else {
            this.infoRow = -1;
        }
        this.permissionsRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.eventLogRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.managementRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.blockedUsersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.membersSectionRow = i;
        if (this.info == null || this.info.participants == null || this.info.participants.participants.isEmpty()) {
            this.membersStartRow = -1;
            this.membersEndRow = -1;
            this.loadMoreMembersRow = -1;
            this.membersSection2Row = -1;
            return;
        }
        this.membersStartRow = this.rowCount;
        this.rowCount += this.info.participants.participants.size();
        this.membersEndRow = this.rowCount;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.membersSection2Row = i;
        if (this.usersEndReached) {
            this.loadMoreMembersRow = -1;
            return;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.loadMoreMembersRow = i;
    }

    private boolean createMenuForParticipant(TL_chatChannelParticipant user, ChannelParticipant channelParticipant, boolean resultOnly) {
        if (user == null && channelParticipant == null) {
            return false;
        }
        int uid;
        ArrayList<String> items;
        ArrayList<Integer> actions;
        int currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (channelParticipant != null) {
            if (currentUserId == channelParticipant.user_id) {
                return false;
            }
            uid = channelParticipant.user_id;
            user = (TL_chatChannelParticipant) this.participantsMap.get(channelParticipant.user_id);
            if (user != null) {
                channelParticipant = user.channelParticipant;
            }
        } else if (user.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        } else {
            uid = user.user_id;
            channelParticipant = user.channelParticipant;
        }
        User u = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
        boolean allowSetAdmin = (channelParticipant instanceof TL_channelParticipant) || (channelParticipant instanceof TL_channelParticipantBanned);
        boolean canEditAdmin = !((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator)) || channelParticipant.can_edit;
        if (resultOnly) {
            items = null;
            actions = null;
        } else {
            items = new ArrayList();
            actions = new ArrayList();
        }
        if (allowSetAdmin && ChatObject.canAddAdmins(this.currentChat)) {
            if (resultOnly) {
                return true;
            }
            items.add(LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
            actions.add(Integer.valueOf(0));
        }
        if (ChatObject.canBlockUsers(this.currentChat) && canEditAdmin) {
            if (resultOnly) {
                return true;
            }
            if (this.currentChat.megagroup) {
                items.add(LocaleController.getString("KickFromSupergroup", R.string.KickFromSupergroup));
                actions.add(Integer.valueOf(1));
                items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                actions.add(Integer.valueOf(2));
            } else {
                items.add(LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser));
                actions.add(Integer.valueOf(2));
            }
        }
        if (items == null || items.isEmpty()) {
            return false;
        }
        ChannelParticipant channelParticipantFinal = channelParticipant;
        TL_chatChannelParticipant userFinal = user;
        Builder builder = new Builder(getParentActivity());
        builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new ChannelEditActivity$$Lambda$4(this, actions, uid, channelParticipantFinal, userFinal));
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$createMenuForParticipant$6$ChannelEditActivity(ArrayList actions, int uid, ChannelParticipant channelParticipantFinal, TL_chatChannelParticipant userFinal, DialogInterface dialogInterface, int i) {
        if (((Integer) actions.get(i)).intValue() == 2) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid)), this.info);
            return;
        }
        ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(channelParticipantFinal.user_id, this.chat_id, channelParticipantFinal.admin_rights, channelParticipantFinal.banned_rights, ((Integer) actions.get(i)).intValue(), true);
        fragment.setDelegate(new ChannelEditActivity$$Lambda$6(this, channelParticipantFinal, actions, i, userFinal, uid));
        presentFragment(fragment);
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:51:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$null$5$ChannelEditActivity(ChannelParticipant channelParticipantFinal, ArrayList actions, int i, TL_chatChannelParticipant userFinal, int uid, int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
        channelParticipantFinal.admin_rights = rightsAdmin;
        channelParticipantFinal.banned_rights = rightsBanned;
        if (((Integer) actions.get(i)).intValue() == 0) {
            if (userFinal != null) {
                if (rights == 1) {
                    userFinal.channelParticipant = new TL_channelParticipantAdmin();
                } else {
                    userFinal.channelParticipant = new TL_channelParticipant();
                }
                userFinal.channelParticipant.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                userFinal.channelParticipant.user_id = userFinal.user_id;
                userFinal.channelParticipant.date = userFinal.date;
            }
        } else if (((Integer) actions.get(i)).intValue() == 1 && rights == 0 && this.currentChat.megagroup && this.info != null && this.info.participants != null) {
            int a;
            boolean changed = false;
            for (a = 0; a < this.info.participants.participants.size(); a++) {
                if (((TL_chatChannelParticipant) this.info.participants.participants.get(a)).channelParticipant.user_id == uid) {
                    if (this.info != null) {
                        ChatFull chatFull = this.info;
                        chatFull.participants_count--;
                    }
                    this.info.participants.participants.remove(a);
                    changed = true;
                    if (this.info != null && this.info.participants != null) {
                        for (a = 0; a < this.info.participants.participants.size(); a++) {
                            if (((ChatParticipant) this.info.participants.participants.get(a)).user_id == uid) {
                                this.info.participants.participants.remove(a);
                                changed = true;
                                break;
                            }
                        }
                    }
                    if (!changed) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                        return;
                    }
                    return;
                }
            }
            while (a < this.info.participants.participants.size()) {
            }
            if (!changed) {
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChannelEditActivity$$Lambda$5(this);
        r10 = new ThemeDescription[33];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatTextCell.class, ManageChatUserCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        r10[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r10[5] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        r10[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ManageChatTextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[16] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menu);
        r10[19] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[26] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        r10[27] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[29] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[31] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[32] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$7$ChannelEditActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
