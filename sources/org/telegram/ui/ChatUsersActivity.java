package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChatUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private int chatId = this.arguments.getInt("chat_id");
    private Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private ChatFull info;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private ArrayList<ChatParticipant> participants = new ArrayList();
    private int participantsEndRow;
    private int participantsInfoRow;
    private int participantsStartRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    /* renamed from: org.telegram.ui.ChatUsersActivity$1 */
    class C20211 extends ActionBarMenuOnItemClick {
        C20211() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChatUsersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$2 */
    class C20222 extends ActionBarMenuItemSearchListener {
        C20222() {
        }

        public void onSearchExpand() {
            ChatUsersActivity.this.searching = true;
            ChatUsersActivity.this.emptyView.setShowAtCenter(true);
        }

        public void onSearchCollapse() {
            ChatUsersActivity.this.searchListViewAdapter.searchDialogs(null);
            ChatUsersActivity.this.searching = false;
            ChatUsersActivity.this.searchWas = false;
            ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
            ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
            ChatUsersActivity.this.listView.setFastScrollVisible(true);
            ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
            ChatUsersActivity.this.emptyView.setShowAtCenter(false);
        }

        public void onTextChanged(EditText editText) {
            if (ChatUsersActivity.this.searchListViewAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ChatUsersActivity.this.searchWas = true;
                    if (ChatUsersActivity.this.listView != null) {
                        ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                        ChatUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChatUsersActivity.this.listView.setFastScrollVisible(false);
                        ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChatUsersActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$3 */
    class C20233 implements OnItemClickListener {
        C20233() {
        }

        public void onItemClick(View view, int position) {
            int user_id = 0;
            if (ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter) {
                ChatParticipant participant = ChatUsersActivity.this.listViewAdapter.getItem(position);
                if (participant != null) {
                    user_id = participant.user_id;
                }
            } else {
                ChatParticipant participant2;
                TLObject object = ChatUsersActivity.this.searchListViewAdapter.getItem(position);
                if (object instanceof ChatParticipant) {
                    participant2 = (ChatParticipant) object;
                } else {
                    participant2 = null;
                }
                if (participant2 != null) {
                    user_id = participant2.user_id;
                }
            }
            if (user_id != 0) {
                Bundle args = new Bundle();
                args.putInt("user_id", user_id);
                ChatUsersActivity.this.presentFragment(new ProfileActivity(args));
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$4 */
    class C20244 implements OnItemLongClickListener {
        C20244() {
        }

        public boolean onItemClick(View view, int position) {
            return ChatUsersActivity.this.getParentActivity() != null && ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter && ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(position), false);
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$5 */
    class C20255 extends OnScrollListener {
        C20255() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && ChatUsersActivity.this.searching && ChatUsersActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$7 */
    class C20267 implements ThemeDescriptionDelegate {
        C20267() {
        }

        public void didSetColor() {
            if (ChatUsersActivity.this.listView != null) {
                int count = ChatUsersActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = ChatUsersActivity.this.listView.getChildAt(a);
                    if (child instanceof ManageChatUserCell) {
                        ((ManageChatUserCell) child).update(0);
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ChatUsersActivity$ListAdapter$1 */
        class C20271 implements ManageChatUserCellDelegate {
            C20271() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell cell, boolean click) {
                return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue()), click ^ 1);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (!(type == 0 || type == 2)) {
                if (type != 6) {
                    return false;
                }
            }
            return true;
        }

        public int getItemCount() {
            if (ChatUsersActivity.this.loadingUsers) {
                return 0;
            }
            return ChatUsersActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                view = new ManageChatUserCell(this.mContext, 1, true);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) view).setDelegate(new C20271());
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    User user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(getItem(position).user_id));
                    if (user != null) {
                        userCell.setData(user, null, null);
                        return;
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == ChatUsersActivity.this.participantsInfoRow) {
                        privacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if ((position < ChatUsersActivity.this.participantsStartRow || position >= ChatUsersActivity.this.participantsEndRow) && position == ChatUsersActivity.this.participantsInfoRow) {
                return 1;
            }
            return 0;
        }

        public ChatParticipant getItem(int position) {
            if (ChatUsersActivity.this.participantsStartRow == -1 || position < ChatUsersActivity.this.participantsStartRow || position >= ChatUsersActivity.this.participantsEndRow) {
                return null;
            }
            return (ChatParticipant) ChatUsersActivity.this.participants.get(position - ChatUsersActivity.this.participantsStartRow);
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<ChatParticipant> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        /* renamed from: org.telegram.ui.ChatUsersActivity$SearchAdapter$4 */
        class C20284 implements ManageChatUserCellDelegate {
            C20284() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell cell, boolean click) {
                if (!(SearchAdapter.this.getItem(((Integer) cell.getTag()).intValue()) instanceof ChatParticipant)) {
                    return false;
                }
                return ChatUsersActivity.this.createMenuForParticipant((ChatParticipant) SearchAdapter.this.getItem(((Integer) cell.getTag()).intValue()), click ^ 1);
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
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
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList<ChatParticipant> contactsCopy = new ArrayList();
                    contactsCopy.addAll(ChatUsersActivity.this.participants);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            int i = 0;
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<ChatParticipant> resultArray = new ArrayList();
                            ArrayList<CharSequence> resultArrayNames = new ArrayList();
                            int a = 0;
                            while (a < contactsCopy.size()) {
                                String str;
                                ChatParticipant participant = (ChatParticipant) contactsCopy.get(a);
                                User user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id));
                                String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                String tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                int length = search.length;
                                int found = 0;
                                int found2 = i;
                                while (found2 < length) {
                                    StringBuilder stringBuilder;
                                    String q = search[found2];
                                    if (name.startsWith(q)) {
                                        str = search1;
                                    } else {
                                        stringBuilder = new StringBuilder();
                                        str = search1;
                                        stringBuilder.append(" ");
                                        stringBuilder.append(q);
                                        if (name.contains(stringBuilder.toString()) == null) {
                                            if (tName != null) {
                                                if (tName.startsWith(q) == null) {
                                                    search1 = new StringBuilder();
                                                    search1.append(" ");
                                                    search1.append(q);
                                                    if (tName.contains(search1.toString()) != null) {
                                                    }
                                                }
                                            }
                                            if (!(user.username == null || user.username.startsWith(q) == null)) {
                                                search1 = 2;
                                                found = search1;
                                            }
                                            if (found == 0) {
                                                if (found != 1) {
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                                } else {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("@");
                                                    stringBuilder.append(user.username);
                                                    String stringBuilder2 = stringBuilder.toString();
                                                    StringBuilder stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("@");
                                                    stringBuilder3.append(q);
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                }
                                                resultArray.add(participant);
                                                a++;
                                                search1 = str;
                                                i = 0;
                                            } else {
                                                found2++;
                                                search1 = str;
                                            }
                                        }
                                    }
                                    search1 = true;
                                    found = search1;
                                    if (found == 0) {
                                        found2++;
                                        search1 = str;
                                    } else {
                                        if (found != 1) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(user.username);
                                            String stringBuilder22 = stringBuilder.toString();
                                            StringBuilder stringBuilder32 = new StringBuilder();
                                            stringBuilder32.append("@");
                                            stringBuilder32.append(q);
                                            resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder22, null, stringBuilder32.toString()));
                                        } else {
                                            resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                        }
                                        resultArray.add(participant);
                                        a++;
                                        search1 = str;
                                        i = 0;
                                    }
                                }
                                str = search1;
                                a++;
                                search1 = str;
                                i = 0;
                            }
                            SearchAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<ChatParticipant> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = users;
                    SearchAdapter.this.searchResultNames = names;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public TLObject getItem(int i) {
            return (TLObject) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ManageChatUserCell(this.mContext, 2, true);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ((ManageChatUserCell) view).setDelegate(new C20284());
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            User user;
            TLObject object = getItem(position);
            if (object instanceof User) {
                user = (User) object;
            } else {
                user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) object).user_id));
            }
            String un = user.username;
            CharSequence name = (CharSequence) this.searchResultNames.get(position);
            CharSequence username = null;
            if (!(name == null || un == null || un.length() <= 0)) {
                String charSequence = name.toString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(un);
                if (charSequence.startsWith(stringBuilder.toString())) {
                    username = name;
                    name = null;
                }
            }
            ManageChatUserCell userCell = holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(user, name, username);
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ChatUsersActivity(Bundle args) {
        super(args);
    }

    private void updateRows() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat != null) {
            this.participantsStartRow = -1;
            this.participantsEndRow = -1;
            this.participantsInfoRow = -1;
            this.rowCount = 0;
            if (this.participants.isEmpty()) {
                this.participantsStartRow = -1;
                this.participantsEndRow = -1;
            } else {
                this.participantsStartRow = this.rowCount;
                this.rowCount += this.participants.size();
                this.participantsEndRow = this.rowCount;
            }
            if (this.rowCount != 0) {
                int i = this.rowCount;
                this.rowCount = i + 1;
                this.participantsInfoRow = i;
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        fetchUsers();
        return true;
    }

    private void fetchUsers() {
        if (this.info == null) {
            this.loadingUsers = true;
            return;
        }
        this.loadingUsers = false;
        this.participants = new ArrayList(this.info.participants.participants);
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupMembers", R.string.GroupMembers));
        this.actionBar.setActionBarMenuOnItemClick(new C20211());
        this.searchListViewAdapter = new SearchAdapter(context);
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C20222());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C20233());
        this.listView.setOnItemLongClickListener(new C20244());
        this.listView.setOnScrollListener(new C20255());
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    private boolean createMenuForParticipant(final ChatParticipant participant, boolean resultOnly) {
        if (participant == null) {
            return false;
        }
        int currentUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (participant.user_id == currentUserId) {
            return false;
        }
        boolean allowKick = false;
        if (this.currentChat.creator) {
            allowKick = true;
        } else if ((participant instanceof TL_chatParticipant) && ((this.currentChat.admin && this.currentChat.admins_enabled) || participant.inviter_id == currentUserId)) {
            allowKick = true;
        }
        if (!allowKick) {
            return false;
        }
        if (resultOnly) {
            return true;
        }
        ArrayList<String> items = new ArrayList();
        final ArrayList<Integer> actions = new ArrayList();
        items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
        actions.add(Integer.valueOf(0));
        Builder builder = new Builder(getParentActivity());
        builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (((Integer) actions.get(i)).intValue() == 0) {
                    MessagesController.getInstance(ChatUsersActivity.this.currentAccount).deleteUserFromChat(ChatUsersActivity.this.chatId, MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id)), ChatUsersActivity.this.info);
                }
            }
        });
        showDialog(builder.create());
        return true;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
            if (chatFull.id == this.chatId && !byChannelUsers) {
                this.info = chatFull;
                fetchUsers();
                updateRows();
            }
        }
    }

    public void setInfo(ChatFull chatInfo) {
        this.info = chatInfo;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C20267();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[22];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        int i = 1;
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        view = this.listView;
        Class[] clsArr = new Class[i];
        clsArr[0] = ManageChatUserCell.class;
        String[] strArr = new String[i];
        strArr[0] = "statusOnlineColor";
        themeDescriptionArr[13] = new ThemeDescription(view, 0, clsArr, strArr, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        view = this.listView;
        clsArr = new Class[i];
        clsArr[0] = ManageChatUserCell.class;
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
