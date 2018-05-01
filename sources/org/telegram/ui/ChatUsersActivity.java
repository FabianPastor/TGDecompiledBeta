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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
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

        public void onItemClick(int i) {
            if (i == -1) {
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
                editText = editText.getText().toString();
                if (editText.length() != 0) {
                    ChatUsersActivity.this.searchWas = true;
                    if (ChatUsersActivity.this.listView != null) {
                        ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                        ChatUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChatUsersActivity.this.listView.setFastScrollVisible(false);
                        ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChatUsersActivity.this.searchListViewAdapter.searchDialogs(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$3 */
    class C20233 implements OnItemClickListener {
        C20233() {
        }

        public void onItemClick(View view, int i) {
            if (ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter) {
                view = ChatUsersActivity.this.listViewAdapter.getItem(i);
                if (view != null) {
                    view = view.user_id;
                    if (view == null) {
                        i = new Bundle();
                        i.putInt("user_id", view);
                        ChatUsersActivity.this.presentFragment(new ProfileActivity(i));
                    }
                }
            }
            view = ChatUsersActivity.this.searchListViewAdapter.getItem(i);
            view = (view instanceof ChatParticipant) != 0 ? (ChatParticipant) view : null;
            if (view != null) {
                view = view.user_id;
                if (view == null) {
                    i = new Bundle();
                    i.putInt("user_id", view);
                    ChatUsersActivity.this.presentFragment(new ProfileActivity(i));
                }
            }
            view = null;
            if (view == null) {
                i = new Bundle();
                i.putInt("user_id", view);
                ChatUsersActivity.this.presentFragment(new ProfileActivity(i));
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$4 */
    class C20244 implements OnItemLongClickListener {
        C20244() {
        }

        public boolean onItemClick(View view, int i) {
            return (ChatUsersActivity.this.getParentActivity() == null || ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.listViewAdapter || ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(i), false) == null) ? false : true;
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$5 */
    class C20255 extends OnScrollListener {
        C20255() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && ChatUsersActivity.this.searching != null && ChatUsersActivity.this.searchWas != null) {
                AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            super.onScrolled(recyclerView, i, i2);
        }
    }

    /* renamed from: org.telegram.ui.ChatUsersActivity$7 */
    class C20267 implements ThemeDescriptionDelegate {
        C20267() {
        }

        public void didSetColor() {
            if (ChatUsersActivity.this.listView != null) {
                int childCount = ChatUsersActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ChatUsersActivity.this.listView.getChildAt(i);
                    if (childAt instanceof ManageChatUserCell) {
                        ((ManageChatUserCell) childAt).update(0);
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

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getItemViewType();
            if (!(viewHolder == null || viewHolder == 2)) {
                if (viewHolder != 6) {
                    return null;
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoPrivacyCell(this.mContext);
                viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                viewGroup = new ManageChatUserCell(this.mContext, 1, true);
                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) viewGroup).setDelegate(new C20271());
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    i = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(getItem(i).user_id));
                    if (i != 0) {
                        manageChatUserCell.setData(i, null, null);
                        return;
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ChatUsersActivity.this.participantsInfoRow) {
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) viewHolder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if ((i < ChatUsersActivity.this.participantsStartRow || i >= ChatUsersActivity.this.participantsEndRow) && i == ChatUsersActivity.this.participantsInfoRow) {
                return 1;
            }
            return 0;
        }

        public ChatParticipant getItem(int i) {
            return (ChatUsersActivity.this.participantsStartRow == -1 || i < ChatUsersActivity.this.participantsStartRow || i >= ChatUsersActivity.this.participantsEndRow) ? 0 : (ChatParticipant) ChatUsersActivity.this.participants.get(i - ChatUsersActivity.this.participantsStartRow);
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

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                if (!(SearchAdapter.this.getItem(((Integer) manageChatUserCell.getTag()).intValue()) instanceof ChatParticipant)) {
                    return null;
                }
                return ChatUsersActivity.this.createMenuForParticipant((ChatParticipant) SearchAdapter.this.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
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
                    SearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList arrayList = new ArrayList();
                    arrayList.addAll(ChatUsersActivity.this.participants);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String toLowerCase = str.trim().toLowerCase();
                            if (toLowerCase.length() == 0) {
                                SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                translitString = null;
                            }
                            int i = 0;
                            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                            strArr[0] = toLowerCase;
                            if (translitString != null) {
                                strArr[1] = translitString;
                            }
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int i2 = 0;
                            while (i2 < arrayList.size()) {
                                ChatParticipant chatParticipant = (ChatParticipant) arrayList.get(i2);
                                User user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id));
                                String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                                if (toLowerCase2.equals(translitString2)) {
                                    translitString2 = null;
                                }
                                int length = strArr.length;
                                int i3 = i;
                                int i4 = i3;
                                while (i3 < length) {
                                    StringBuilder stringBuilder;
                                    String str = strArr[i3];
                                    if (!toLowerCase2.startsWith(str)) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(str);
                                        if (!toLowerCase2.contains(stringBuilder.toString())) {
                                            if (translitString2 != null) {
                                                if (!translitString2.startsWith(str)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(str);
                                                    if (translitString2.contains(stringBuilder.toString())) {
                                                    }
                                                }
                                            }
                                            if (user.username != null && user.username.startsWith(str)) {
                                                i4 = 2;
                                            }
                                            if (i4 == 0) {
                                                if (i4 != 1) {
                                                    arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                                } else {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("@");
                                                    stringBuilder.append(user.username);
                                                    String stringBuilder2 = stringBuilder.toString();
                                                    StringBuilder stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("@");
                                                    stringBuilder3.append(str);
                                                    arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                }
                                                arrayList.add(chatParticipant);
                                                i2++;
                                                i = 0;
                                            } else {
                                                i3++;
                                            }
                                        }
                                    }
                                    i4 = 1;
                                    if (i4 == 0) {
                                        i3++;
                                    } else {
                                        if (i4 != 1) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(user.username);
                                            String stringBuilder22 = stringBuilder.toString();
                                            StringBuilder stringBuilder32 = new StringBuilder();
                                            stringBuilder32.append("@");
                                            stringBuilder32.append(str);
                                            arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder22, null, stringBuilder32.toString()));
                                        } else {
                                            arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                        }
                                        arrayList.add(chatParticipant);
                                        i2++;
                                        i = 0;
                                    }
                                }
                                i2++;
                                i = 0;
                            }
                            SearchAdapter.this.updateSearchResults(arrayList, arrayList2);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<ChatParticipant> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = arrayList;
                    SearchAdapter.this.searchResultNames = arrayList2;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public TLObject getItem(int i) {
            return (TLObject) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new ManageChatUserCell(this.mContext, 2, true);
            viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ((ManageChatUserCell) viewGroup).setDelegate(new C20284());
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            User user;
            TLObject item = getItem(i);
            if (item instanceof User) {
                user = (User) item;
            } else {
                user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) item).user_id));
            }
            String str = user.username;
            CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
            CharSequence charSequence2 = null;
            if (!(charSequence == null || str == null || str.length() <= 0)) {
                String charSequence3 = charSequence.toString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(str);
                if (charSequence3.startsWith(stringBuilder.toString())) {
                    charSequence2 = charSequence;
                    charSequence = null;
                }
            }
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
            manageChatUserCell.setTag(Integer.valueOf(i));
            manageChatUserCell.setData(user, charSequence, charSequence2);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) viewHolder.itemView).recycle();
            }
        }
    }

    public ChatUsersActivity(Bundle bundle) {
        super(bundle);
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupMembers", C0446R.string.GroupMembers));
        this.actionBar.setActionBarMenuOnItemClick(new C20211());
        this.searchListViewAdapter = new SearchAdapter(context);
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C20222());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        context = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        context.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C20233());
        this.listView.setOnItemLongClickListener(new C20244());
        this.listView.setOnScrollListener(new C20255());
        if (this.loadingUsers != null) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    private boolean createMenuForParticipant(final ChatParticipant chatParticipant, boolean z) {
        if (chatParticipant == null) {
            return false;
        }
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (chatParticipant.user_id == clientUserId) {
            return false;
        }
        boolean z2;
        if (!this.currentChat.creator) {
            if (chatParticipant instanceof TL_chatParticipant) {
                if (!(this.currentChat.admin && this.currentChat.admins_enabled)) {
                    if (chatParticipant.inviter_id == clientUserId) {
                    }
                }
            }
            z2 = false;
            if (!z2) {
                return false;
            }
            if (z) {
                return true;
            }
            z = new ArrayList();
            final ArrayList arrayList = new ArrayList();
            z.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
            arrayList.add(Integer.valueOf(0));
            Builder builder = new Builder(getParentActivity());
            builder.setItems((CharSequence[]) z.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (((Integer) arrayList.get(i)).intValue() == null) {
                        MessagesController.getInstance(ChatUsersActivity.this.currentAccount).deleteUserFromChat(ChatUsersActivity.this.chatId, MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id)), ChatUsersActivity.this.info);
                    }
                }
            });
            showDialog(builder.create());
            return true;
        }
        z2 = true;
        if (!z2) {
            return false;
        }
        if (z) {
            return true;
        }
        z = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        z.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
        arrayList2.add(Integer.valueOf(0));
        Builder builder2 = new Builder(getParentActivity());
        builder2.setItems((CharSequence[]) z.toArray(new CharSequence[arrayList2.size()]), /* anonymous class already generated */);
        showDialog(builder2.create());
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            i2 = ((Boolean) objArr[2]).booleanValue();
            if (chatFull.id == this.chatId && i2 == 0) {
                this.info = chatFull;
                fetchUsers();
                updateRows();
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        C20267 c20267 = new C20267();
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
        ThemeDescriptionDelegate themeDescriptionDelegate = c20267;
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C20267 c202672 = c20267;
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, c202672, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
