package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    private int rowCount = null;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private ArrayList<Integer> sortedUsers;
    private boolean usersEndReached;

    /* renamed from: org.telegram.ui.ChannelEditActivity$2 */
    class C19762 extends ActionBarMenuOnItemClick {
        C19762() {
        }

        public void onItemClick(int i) {
            if (ChannelEditActivity.this.getParentActivity() != null && i == -1) {
                ChannelEditActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$3 */
    class C19773 extends ActionBarMenuItemSearchListener {
        C19773() {
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
                editText = editText.getText().toString();
                if (editText.length() != 0) {
                    ChannelEditActivity.this.searchWas = true;
                    if (ChannelEditActivity.this.listView != null) {
                        ChannelEditActivity.this.listView.setAdapter(ChannelEditActivity.this.searchListViewAdapter);
                        ChannelEditActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChannelEditActivity.this.listView.setFastScrollVisible(false);
                        ChannelEditActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChannelEditActivity.this.searchListViewAdapter.searchDialogs(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$5 */
    class C19785 implements OnItemClickListener {
        C19785() {
        }

        public void onItemClick(View view, int i) {
            if (ChannelEditActivity.this.getParentActivity() != null) {
                if (ChannelEditActivity.this.listView.getAdapter() == ChannelEditActivity.this.searchListViewAdapter) {
                    view = new Bundle();
                    view.putInt("user_id", ChannelEditActivity.this.searchListViewAdapter.getItem(i).user_id);
                    ChannelEditActivity.this.presentFragment(new ProfileActivity(view));
                } else if (i < ChannelEditActivity.this.membersStartRow || i >= ChannelEditActivity.this.membersEndRow) {
                    if (i != ChannelEditActivity.this.blockedUsersRow) {
                        if (i != ChannelEditActivity.this.managementRow) {
                            if (i == ChannelEditActivity.this.permissionsRow) {
                                view = new ChannelPermissionsActivity(ChannelEditActivity.this.chat_id);
                                view.setInfo(ChannelEditActivity.this.info);
                                ChannelEditActivity.this.presentFragment(view);
                            } else if (i == ChannelEditActivity.this.eventLogRow) {
                                ChannelEditActivity.this.presentFragment(new ChannelAdminLogActivity(ChannelEditActivity.this.currentChat));
                            } else if (i == ChannelEditActivity.this.infoRow) {
                                view = new Bundle();
                                view.putInt("chat_id", ChannelEditActivity.this.chat_id);
                                i = new ChannelEditInfoActivity(view);
                                i.setInfo(ChannelEditActivity.this.info);
                                ChannelEditActivity.this.presentFragment(i);
                            }
                        }
                    }
                    view = new Bundle();
                    view.putInt("chat_id", ChannelEditActivity.this.chat_id);
                    if (i == ChannelEditActivity.this.blockedUsersRow) {
                        view.putInt("type", 0);
                    } else if (i == ChannelEditActivity.this.managementRow) {
                        view.putInt("type", 1);
                    }
                    ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(view));
                } else {
                    if (ChannelEditActivity.this.sortedUsers.isEmpty() == null) {
                        view = ((ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue())).user_id;
                    } else {
                        view = ((ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow)).user_id;
                    }
                    i = new Bundle();
                    i.putInt("user_id", view);
                    ChannelEditActivity.this.presentFragment(new ProfileActivity(i));
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$6 */
    class C19796 implements OnItemLongClickListener {
        C19796() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onItemClick(View view, int i) {
            if (i < ChannelEditActivity.this.membersStartRow || i >= ChannelEditActivity.this.membersEndRow || ChannelEditActivity.this.getParentActivity() == null) {
                return false;
            }
            if (ChannelEditActivity.this.sortedUsers.isEmpty() == null) {
                view = (TL_chatChannelParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue());
            } else {
                view = (TL_chatChannelParticipant) ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow);
            }
            return ChannelEditActivity.this.createMenuForParticipant(view, null, false);
        }
    }

    /* renamed from: org.telegram.ui.ChannelEditActivity$7 */
    class C19807 extends OnScrollListener {
        C19807() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (ChannelEditActivity.this.participantsMap != null && ChannelEditActivity.this.loadMoreMembersRow != -1 && ChannelEditActivity.this.layoutManager.findLastVisibleItemPosition() > ChannelEditActivity.this.loadMoreMembersRow - 8) {
                ChannelEditActivity.this.getChannelParticipants(0);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ChannelEditActivity$ListAdapter$1 */
        class C19831 implements ManageChatUserCellDelegate {
            C19831() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                manageChatUserCell = ((Integer) manageChatUserCell.getTag()).intValue();
                if (ChannelEditActivity.this.sortedUsers.isEmpty()) {
                    manageChatUserCell = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(manageChatUserCell - ChannelEditActivity.this.membersStartRow);
                } else {
                    manageChatUserCell = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(manageChatUserCell - ChannelEditActivity.this.membersStartRow)).intValue());
                }
                return ChannelEditActivity.this.createMenuForParticipant((TL_chatChannelParticipant) manageChatUserCell, null, z ^ 1);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new ManageChatTextCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new ManageChatUserCell(this.mContext, 8, true);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ManageChatUserCell) viewGroup).setDelegate(new C19831());
                    break;
                case 2:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 3:
                    viewGroup = new LoadingCell(this.mContext);
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) viewHolder.itemView).recycle();
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            String str = null;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                    manageChatTextCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    manageChatTextCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    if (i == ChannelEditActivity.this.managementRow) {
                        i = LocaleController.getString("ChannelAdministrators", C0446R.string.ChannelAdministrators);
                        if (ChannelEditActivity.this.info != null) {
                            str = String.format("%d", new Object[]{Integer.valueOf(ChannelEditActivity.this.info.admins_count)});
                        }
                        if (ChannelEditActivity.this.blockedUsersRow != -1) {
                            z = true;
                        }
                        manageChatTextCell.setText(i, str, C0446R.drawable.group_admin, z);
                        return;
                    } else if (i == ChannelEditActivity.this.blockedUsersRow) {
                        i = LocaleController.getString("ChannelBlacklist", C0446R.string.ChannelBlacklist);
                        if (ChannelEditActivity.this.info != null) {
                            str = String.format("%d", new Object[]{Integer.valueOf(ChannelEditActivity.this.info.kicked_count + ChannelEditActivity.this.info.banned_count)});
                        }
                        manageChatTextCell.setText(i, str, C0446R.drawable.group_banned, false);
                        return;
                    } else if (i == ChannelEditActivity.this.eventLogRow) {
                        manageChatTextCell.setText(LocaleController.getString("EventLog", C0446R.string.EventLog), null, C0446R.drawable.group_log, true);
                        return;
                    } else if (i == ChannelEditActivity.this.infoRow) {
                        int i2;
                        if (ChannelEditActivity.this.currentChat.megagroup != 0) {
                            i = "EventLogFilterGroupInfo";
                            i2 = C0446R.string.EventLogFilterGroupInfo;
                        } else {
                            i = "EventLogFilterChannelInfo";
                            i2 = C0446R.string.EventLogFilterChannelInfo;
                        }
                        manageChatTextCell.setText(LocaleController.getString(i, i2), null, C0446R.drawable.group_edit, true);
                        return;
                    } else {
                        viewHolder = ChannelEditActivity.this.permissionsRow;
                        return;
                    }
                case 1:
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    if (ChannelEditActivity.this.sortedUsers.isEmpty()) {
                        i = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow);
                    } else {
                        i = (ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(((Integer) ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue());
                    }
                    if (i != 0) {
                        if (i instanceof TL_chatChannelParticipant) {
                            ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) i).channelParticipant;
                            if ((channelParticipant instanceof TL_channelParticipantCreator) || (channelParticipant instanceof TL_channelParticipantAdmin)) {
                                z = true;
                            }
                            manageChatUserCell.setIsAdmin(z);
                        } else {
                            manageChatUserCell.setIsAdmin(i instanceof TL_chatParticipantAdmin);
                        }
                        manageChatUserCell.setData(MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(i.user_id)), null, null);
                        return;
                    }
                    return;
                case 2:
                    if (i != ChannelEditActivity.this.membersSectionRow || ChannelEditActivity.this.membersStartRow == -1) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getItemViewType();
            if (viewHolder != null) {
                return viewHolder == 1;
            } else {
                return true;
            }
        }

        public int getItemCount() {
            return ChannelEditActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (!(i == ChannelEditActivity.this.managementRow || i == ChannelEditActivity.this.blockedUsersRow || i == ChannelEditActivity.this.infoRow || i == ChannelEditActivity.this.eventLogRow)) {
                if (i != ChannelEditActivity.this.permissionsRow) {
                    if (i >= ChannelEditActivity.this.membersStartRow && i < ChannelEditActivity.this.membersEndRow) {
                        return 1;
                    }
                    if (i != ChannelEditActivity.this.membersSectionRow) {
                        if (i != ChannelEditActivity.this.membersSection2Row) {
                            if (i == ChannelEditActivity.this.loadMoreMembersRow) {
                                return 3;
                            }
                            return 0;
                        }
                    }
                    return 2;
                }
            }
            return 0;
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.ChannelEditActivity$SearchAdapter$4 */
        class C19854 implements ManageChatUserCellDelegate {
            C19854() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                return ChannelEditActivity.this.createMenuForParticipant(null, SearchAdapter.this.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChannelEditActivity.this) {
                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
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
                        FileLog.m3e(e);
                    }
                    SearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(str, false, false, true, true, ChannelEditActivity.this.chat_id, false);
                }
            });
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchAdapterHelper.getGroupSearch().size();
        }

        public ChannelParticipant getItem(int i) {
            return (ChannelParticipant) this.searchAdapterHelper.getGroupSearch().get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new ManageChatUserCell(this.mContext, 8, true);
            viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ((ManageChatUserCell) viewGroup).setDelegate(new C19854());
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                User user;
                CharSequence spannableStringBuilder;
                ChannelParticipant item = getItem(i);
                boolean z = true;
                if (item instanceof User) {
                    user = (User) item;
                    ChatParticipant chatParticipant = (ChatParticipant) ChannelEditActivity.this.participantsMap.get(user.id);
                    if (chatParticipant instanceof TL_chatChannelParticipant) {
                        ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) chatParticipant).channelParticipant;
                        if (!(channelParticipant instanceof TL_channelParticipantCreator)) {
                            if (!(channelParticipant instanceof TL_channelParticipantAdmin)) {
                                z = false;
                            }
                        }
                    } else {
                        z = chatParticipant instanceof TL_chatParticipantAdmin;
                    }
                } else {
                    if (!(item instanceof TL_channelParticipantAdmin)) {
                        if (!(item instanceof TL_channelParticipantCreator)) {
                            z = false;
                        }
                    }
                    user = MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(item.user_id));
                }
                String lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel();
                if (lastFoundChannel != null) {
                    Object userName = UserObject.getUserName(user);
                    spannableStringBuilder = new SpannableStringBuilder(userName);
                    int indexOf = userName.toLowerCase().indexOf(lastFoundChannel);
                    if (indexOf != -1) {
                        ((SpannableStringBuilder) spannableStringBuilder).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, lastFoundChannel.length() + indexOf, 33);
                    }
                } else {
                    spannableStringBuilder = null;
                }
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                manageChatUserCell.setIsAdmin(z);
                manageChatUserCell.setData(user, spannableStringBuilder, null);
            }
        }
    }

    public ChannelEditActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id", 0);
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        if (this.currentChat == null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    ChannelEditActivity.this.currentChat = MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).getChat(ChannelEditActivity.this.chat_id);
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m3e(e);
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

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        Theme.createProfileResources(context);
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("ManageGroup", C0446R.string.ManageGroup));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ManageChannel", C0446R.string.ManageChannel));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19762());
        this.searchListViewAdapter = new SearchAdapter(context);
        this.actionBar.createMenu().addItem(1, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19773()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.listViewAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        View emptyTextProgressView = new EmptyTextProgressView(context);
        emptyTextProgressView.setShowAtCenter(true);
        emptyTextProgressView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        emptyTextProgressView.showTextView();
        frameLayout.addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(emptyTextProgressView);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setOnItemClickListener(new C19785());
        this.listView.setOnItemLongClickListener(new C19796());
        this.listView.setOnScrollListener(new C19807());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            i = 0;
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chat_id) {
                objArr = ((Boolean) objArr[2]).booleanValue();
                if ((this.info instanceof TL_channelFull) && chatFull.participants == null && this.info != null) {
                    chatFull.participants = this.info.participants;
                }
                if (this.info == null && (chatFull instanceof TL_channelFull)) {
                    i = 1;
                }
                this.info = chatFull;
                fetchUsersFromChannelInfo();
                updateRowsIds();
                if (this.listViewAdapter != 0) {
                    this.listViewAdapter.notifyDataSetChanged();
                }
                i2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (i2 != 0) {
                    this.currentChat = i2;
                }
                if (i != 0 || objArr == null) {
                    getChannelParticipants(true);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private void getChannelParticipants(boolean z) {
        if (!(this.loadingUsers || this.participantsMap == null)) {
            if (this.info != null) {
                this.loadingUsers = true;
                int i = 0;
                final int i2 = (this.participantsMap.size() == 0 || !z) ? 0 : 300;
                final TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
                tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                if (!z) {
                    i = this.participantsMap.size();
                }
                tL_channels_getParticipants.offset = i;
                tL_channels_getParticipants.limit = true;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tL_error == null) {
                                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                                    if (tL_channels_channelParticipants.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                        ChannelEditActivity.this.usersEndReached = true;
                                    }
                                    if (tL_channels_getParticipants.offset == 0) {
                                        ChannelEditActivity.this.participantsMap.clear();
                                        ChannelEditActivity.this.info.participants = new TL_chatParticipants();
                                        MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                                        MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).updateChannelUsers(ChannelEditActivity.this.chat_id, tL_channels_channelParticipants.participants);
                                    }
                                    for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                                        TL_chatChannelParticipant tL_chatChannelParticipant = new TL_chatChannelParticipant();
                                        tL_chatChannelParticipant.channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                                        tL_chatChannelParticipant.inviter_id = tL_chatChannelParticipant.channelParticipant.inviter_id;
                                        tL_chatChannelParticipant.user_id = tL_chatChannelParticipant.channelParticipant.user_id;
                                        tL_chatChannelParticipant.date = tL_chatChannelParticipant.channelParticipant.date;
                                        if (ChannelEditActivity.this.participantsMap.indexOfKey(tL_chatChannelParticipant.user_id) < 0) {
                                            ChannelEditActivity.this.info.participants.participants.add(tL_chatChannelParticipant);
                                            ChannelEditActivity.this.participantsMap.put(tL_chatChannelParticipant.user_id, tL_chatChannelParticipant);
                                        }
                                    }
                                }
                                ChannelEditActivity.this.loadingUsers = false;
                                NotificationCenter.getInstance(ChannelEditActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, ChannelEditActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                            }
                        }, (long) i2);
                    }
                }), this.classGuid);
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
        fetchUsersFromChannelInfo();
    }

    private void fetchUsersFromChannelInfo() {
        if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int i = 0; i < this.info.participants.participants.size(); i++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i);
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
        if (!this.currentChat.megagroup) {
            if (this.info != null) {
                if (this.info.banned_count == 0) {
                    if (this.info.kicked_count != 0) {
                    }
                }
            }
            this.blockedUsersRow = -1;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.membersSectionRow = i;
            if (this.info != null || this.info.participants == null || this.info.participants.participants.isEmpty()) {
                this.membersStartRow = -1;
                this.membersEndRow = -1;
                this.loadMoreMembersRow = -1;
                this.membersSection2Row = -1;
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
            return;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.blockedUsersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.membersSectionRow = i;
        if (this.info != null) {
        }
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.loadMoreMembersRow = -1;
        this.membersSection2Row = -1;
    }

    private boolean createMenuForParticipant(TL_chatChannelParticipant tL_chatChannelParticipant, ChannelParticipant channelParticipant, boolean z) {
        if (tL_chatChannelParticipant == null && channelParticipant == null) {
            return false;
        }
        int i;
        ChannelParticipant channelParticipant2;
        TL_chatChannelParticipant tL_chatChannelParticipant2;
        ArrayList arrayList;
        ArrayList arrayList2;
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (channelParticipant != null) {
            if (clientUserId == channelParticipant.user_id) {
                return false;
            }
            tL_chatChannelParticipant = channelParticipant.user_id;
            TL_chatChannelParticipant tL_chatChannelParticipant3 = (TL_chatChannelParticipant) this.participantsMap.get(channelParticipant.user_id);
            if (tL_chatChannelParticipant3 != null) {
                channelParticipant = tL_chatChannelParticipant3.channelParticipant;
            }
            i = tL_chatChannelParticipant;
            channelParticipant2 = channelParticipant;
            tL_chatChannelParticipant2 = tL_chatChannelParticipant3;
        } else if (tL_chatChannelParticipant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        } else {
            tL_chatChannelParticipant2 = tL_chatChannelParticipant;
            i = tL_chatChannelParticipant.user_id;
            channelParticipant2 = tL_chatChannelParticipant.channelParticipant;
        }
        MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if ((channelParticipant2 instanceof TL_channelParticipant) == null) {
            if ((channelParticipant2 instanceof TL_channelParticipantBanned) == null) {
                tL_chatChannelParticipant = null;
                boolean z2 = ((channelParticipant2 instanceof TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TL_channelParticipantCreator)) || channelParticipant2.can_edit;
                arrayList = null;
                if (z) {
                    arrayList = new ArrayList();
                    arrayList2 = new ArrayList();
                } else {
                    arrayList2 = null;
                }
                if (!(tL_chatChannelParticipant == null || ChatObject.canAddAdmins(this.currentChat) == null)) {
                    if (z) {
                        return true;
                    }
                    arrayList.add(LocaleController.getString("SetAsAdmin", C0446R.string.SetAsAdmin));
                    arrayList2.add(Integer.valueOf(0));
                }
                if (ChatObject.canBlockUsers(this.currentChat) != null && z2) {
                    if (z) {
                        return true;
                    }
                    if (this.currentChat.megagroup == null) {
                        arrayList.add(LocaleController.getString("KickFromSupergroup", C0446R.string.KickFromSupergroup));
                        arrayList2.add(Integer.valueOf(1));
                        arrayList.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
                        arrayList2.add(Integer.valueOf(2));
                    } else {
                        arrayList.add(LocaleController.getString("ChannelRemoveUser", C0446R.string.ChannelRemoveUser));
                        arrayList2.add(Integer.valueOf(2));
                    }
                }
                if (arrayList != null) {
                    if (arrayList.isEmpty() != null) {
                        tL_chatChannelParticipant = new Builder(getParentActivity());
                        tL_chatChannelParticipant.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                if (((Integer) arrayList2.get(i)).intValue() == 2) {
                                    MessagesController.getInstance(ChannelEditActivity.this.currentAccount).deleteUserFromChat(ChannelEditActivity.this.chat_id, MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(i)), ChannelEditActivity.this.info);
                                    return;
                                }
                                DialogInterface channelRightsEditActivity = new ChannelRightsEditActivity(channelParticipant2.user_id, ChannelEditActivity.this.chat_id, channelParticipant2.admin_rights, channelParticipant2.banned_rights, ((Integer) arrayList2.get(i)).intValue(), true);
                                channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                        channelParticipant2.admin_rights = tL_channelAdminRights;
                                        channelParticipant2.banned_rights = tL_channelBannedRights;
                                        if (((Integer) arrayList2.get(i)).intValue() == null) {
                                            if (tL_chatChannelParticipant2 != null) {
                                                if (i == 1) {
                                                    tL_chatChannelParticipant2.channelParticipant = new TL_channelParticipantAdmin();
                                                } else {
                                                    tL_chatChannelParticipant2.channelParticipant = new TL_channelParticipant();
                                                }
                                                tL_chatChannelParticipant2.channelParticipant.inviter_id = UserConfig.getInstance(ChannelEditActivity.this.currentAccount).getClientUserId();
                                                tL_chatChannelParticipant2.channelParticipant.user_id = tL_chatChannelParticipant2.user_id;
                                                tL_chatChannelParticipant2.channelParticipant.date = tL_chatChannelParticipant2.date;
                                            }
                                        } else if (((Integer) arrayList2.get(i)).intValue() == 1 && i == 0 && ChannelEditActivity.this.currentChat.megagroup != 0 && ChannelEditActivity.this.info != 0 && ChannelEditActivity.this.info.participants != 0) {
                                            int i2;
                                            for (tL_channelAdminRights = null; tL_channelAdminRights < ChannelEditActivity.this.info.participants.participants.size(); tL_channelAdminRights++) {
                                                if (((TL_chatChannelParticipant) ChannelEditActivity.this.info.participants.participants.get(tL_channelAdminRights)).channelParticipant.user_id == i) {
                                                    if (ChannelEditActivity.this.info != null) {
                                                        ChatFull access$1100 = ChannelEditActivity.this.info;
                                                        access$1100.participants_count--;
                                                    }
                                                    ChannelEditActivity.this.info.participants.participants.remove(tL_channelAdminRights);
                                                    tL_channelAdminRights = 1;
                                                    if (ChannelEditActivity.this.info != null && ChannelEditActivity.this.info.participants != null) {
                                                        for (i2 = 0; i2 < ChannelEditActivity.this.info.participants.participants.size(); i2++) {
                                                            if (((ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i2)).user_id == i) {
                                                                ChannelEditActivity.this.info.participants.participants.remove(i2);
                                                                tL_channelAdminRights = 1;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (tL_channelAdminRights != null) {
                                                        NotificationCenter.getInstance(ChannelEditActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, ChannelEditActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                                                    }
                                                }
                                            }
                                            tL_channelAdminRights = null;
                                            for (i2 = 0; i2 < ChannelEditActivity.this.info.participants.participants.size(); i2++) {
                                                if (((ChatParticipant) ChannelEditActivity.this.info.participants.participants.get(i2)).user_id == i) {
                                                    ChannelEditActivity.this.info.participants.participants.remove(i2);
                                                    tL_channelAdminRights = 1;
                                                    break;
                                                }
                                            }
                                            if (tL_channelAdminRights != null) {
                                                NotificationCenter.getInstance(ChannelEditActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, ChannelEditActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                                            }
                                        }
                                    }
                                });
                                ChannelEditActivity.this.presentFragment(channelRightsEditActivity);
                            }
                        });
                        showDialog(tL_chatChannelParticipant.create());
                        return true;
                    }
                }
                return false;
            }
        }
        tL_chatChannelParticipant = 1;
        if (channelParticipant2 instanceof TL_channelParticipantAdmin) {
        }
        arrayList = null;
        if (z) {
            arrayList = new ArrayList();
            arrayList2 = new ArrayList();
        } else {
            arrayList2 = null;
        }
        if (z) {
            return true;
        }
        arrayList.add(LocaleController.getString("SetAsAdmin", C0446R.string.SetAsAdmin));
        arrayList2.add(Integer.valueOf(0));
        if (z) {
            return true;
        }
        if (this.currentChat.megagroup == null) {
            arrayList.add(LocaleController.getString("ChannelRemoveUser", C0446R.string.ChannelRemoveUser));
            arrayList2.add(Integer.valueOf(2));
        } else {
            arrayList.add(LocaleController.getString("KickFromSupergroup", C0446R.string.KickFromSupergroup));
            arrayList2.add(Integer.valueOf(1));
            arrayList.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
            arrayList2.add(Integer.valueOf(2));
        }
        if (arrayList != null) {
            if (arrayList.isEmpty() != null) {
                tL_chatChannelParticipant = new Builder(getParentActivity());
                tL_chatChannelParticipant.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), /* anonymous class already generated */);
                showDialog(tL_chatChannelParticipant.create());
                return true;
            }
        }
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass10 anonymousClass10 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ChannelEditActivity.this.listView != null) {
                    int childCount = ChannelEditActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChannelEditActivity.this.listView.getChildAt(i);
                        if (childAt instanceof ManageChatUserCell) {
                            ((ManageChatUserCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        r11 = new ThemeDescription[30];
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        r11[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r11[7] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        r11[8] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        view = this.listView;
        view2 = view;
        r11[9] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        view = this.listView;
        view2 = view;
        r11[10] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        view = this.listView;
        view2 = view;
        r11[11] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r11[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r11[13] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = anonymousClass10;
        r11[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r11[15] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r11[16] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        AnonymousClass10 anonymousClass102 = anonymousClass10;
        r11[17] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundRed);
        r11[18] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundOrange);
        r11[19] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundViolet);
        r11[20] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundGreen);
        r11[21] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundCyan);
        r11[22] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundBlue);
        r11[23] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundPink);
        r11[24] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r11[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r11[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r11[27] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r11[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r11[29] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r11;
    }
}
