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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
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

    /* renamed from: org.telegram.ui.CommonGroupsActivity$1 */
    class C20231 extends ActionBarMenuOnItemClick {
        C20231() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                CommonGroupsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$2 */
    class C20242 implements OnItemClickListener {
        C20242() {
        }

        public void onItemClick(View view, int position) {
            if (position >= 0) {
                if (position < CommonGroupsActivity.this.chats.size()) {
                    Chat chat = (Chat) CommonGroupsActivity.this.chats.get(position);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).checkCanOpenChat(args, CommonGroupsActivity.this)) {
                        NotificationCenter.getInstance(CommonGroupsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        CommonGroupsActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$3 */
    class C20253 extends OnScrollListener {
        C20253() {
        }

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
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$5 */
    class C20275 implements ThemeDescriptionDelegate {
        C20275() {
        }

        public void didSetColor() {
            if (CommonGroupsActivity.this.listView != null) {
                int count = CommonGroupsActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = CommonGroupsActivity.this.listView.getChildAt(a);
                    if (child instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) child).update(0);
                    }
                }
            }
        }
    }

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
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new LoadingCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ProfileSearchCell cell = holder.itemView;
                cell.setData((Chat) CommonGroupsActivity.this.chats.get(position), null, null, null, false, false);
                boolean z = true;
                if (position == CommonGroupsActivity.this.chats.size() - 1) {
                    if (CommonGroupsActivity.this.endReached) {
                        z = false;
                    }
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C20231());
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
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
        this.listView.setOnItemClickListener(new C20242());
        this.listView.setOnScrollListener(new C20253());
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    private void getChats(int max_id, final int count) {
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
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    boolean z;
                                    messages_Chats res = response;
                                    MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).putChats(res.chats, false);
                                    CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                                    if (!res.chats.isEmpty()) {
                                        if (res.chats.size() == count) {
                                            z = false;
                                            commonGroupsActivity.endReached = z;
                                            CommonGroupsActivity.this.chats.addAll(res.chats);
                                        }
                                    }
                                    z = true;
                                    commonGroupsActivity.endReached = z;
                                    CommonGroupsActivity.this.chats.addAll(res.chats);
                                } else {
                                    CommonGroupsActivity.this.endReached = true;
                                }
                                CommonGroupsActivity.this.loading = false;
                                CommonGroupsActivity.this.firstLoaded = true;
                                if (CommonGroupsActivity.this.emptyView != null) {
                                    CommonGroupsActivity.this.emptyView.showTextView();
                                }
                                if (CommonGroupsActivity.this.listViewAdapter != null) {
                                    CommonGroupsActivity.this.listViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }), this.classGuid);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C20275();
        r9 = new ThemeDescription[23];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LoadingCell.class, ProfileSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r9[16] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        r9[17] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        r9[18] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        r9[19] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        r9[20] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        r9[21] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        r9[22] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        return r9;
    }
}
