package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_getCommonChats;
import org.telegram.tgnet.TLRPC.messages_Chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class CommonGroupsActivity extends BaseFragment {
    private ArrayList<Chat> chats = new ArrayList();
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private int userId;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getAdapterPosition() != CommonGroupsActivity.this.chats.size();
        }

        public int getItemCount() {
            int count = CommonGroupsActivity.this.chats.size();
            if (CommonGroupsActivity.this.chats.isEmpty()) {
                return count;
            }
            count++;
            if (CommonGroupsActivity.this.endReached) {
                return count;
            }
            return count + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ProfileSearchCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new LoadingCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            if (holder.getItemViewType() == 0) {
                ProfileSearchCell cell = holder.itemView;
                cell.setData((Chat) CommonGroupsActivity.this.chats.get(position), null, null, null, false, false);
                if (!(position == CommonGroupsActivity.this.chats.size() - 1 && CommonGroupsActivity.this.endReached)) {
                    z = true;
                }
                cell.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            if (i < CommonGroupsActivity.this.chats.size()) {
                return 0;
            }
            if (CommonGroupsActivity.this.endReached || i != CommonGroupsActivity.this.chats.size()) {
                return 2;
            }
            return 1;
        }
    }

    public CommonGroupsActivity(int uid) {
        this.userId = uid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChats(0, 50);
        return true;
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CommonGroupsActivity.this.lambda$checkDiscard$18$ChatUsersActivity();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", R.string.NoGroupsInCommon));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new CommonGroupsActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = CommonGroupsActivity.this.listViewAdapter.getItemCount();
                    if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                        CommonGroupsActivity.this.getChats(((Chat) CommonGroupsActivity.this.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
                    }
                }
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$CommonGroupsActivity(View view, int position) {
        if (position >= 0 && position < this.chats.size()) {
            Chat chat = (Chat) this.chats.get(position);
            Bundle args = new Bundle();
            args.putInt("chat_id", chat.id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
            }
        }
    }

    private void getChats(int max_id, int count) {
        if (!this.loading) {
            this.loading = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TL_messages_getCommonChats req = new TL_messages_getCommonChats();
            req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
            if (!(req.user_id instanceof TL_inputUserEmpty)) {
                req.limit = count;
                req.max_id = max_id;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new CommonGroupsActivity$$Lambda$1(this, count)), this.classGuid);
            }
        }
    }

    final /* synthetic */ void lambda$getChats$2$CommonGroupsActivity(int count, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new CommonGroupsActivity$$Lambda$3(this, error, response, count));
    }

    final /* synthetic */ void lambda$null$1$CommonGroupsActivity(TL_error error, TLObject response, int count) {
        if (error == null) {
            boolean z;
            messages_Chats res = (messages_Chats) response;
            MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
            if (res.chats.isEmpty() || res.chats.size() != count) {
                z = true;
            } else {
                z = false;
            }
            this.endReached = z;
            this.chats.addAll(res.chats);
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        if (this.emptyView != null) {
            this.emptyView.showTextView();
        }
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new CommonGroupsActivity$$Lambda$2(this);
        r10 = new ThemeDescription[23];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LoadingCell.class, ProfileSearchCell.class}, null, null, null, "windowBackgroundWhite");
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r10[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r10[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, "chats_name");
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r10[16] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        r10[17] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        r10[18] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        r10[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        r10[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        r10[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        r10[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$3$CommonGroupsActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
