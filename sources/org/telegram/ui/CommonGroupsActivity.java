package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
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
    class C20291 extends ActionBarMenuOnItemClick {
        C20291() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CommonGroupsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$2 */
    class C20302 implements OnItemClickListener {
        C20302() {
        }

        public void onItemClick(View view, int i) {
            if (i >= 0) {
                if (i < CommonGroupsActivity.this.chats.size()) {
                    Chat chat = (Chat) CommonGroupsActivity.this.chats.get(i);
                    i = new Bundle();
                    i.putInt("chat_id", chat.id);
                    if (MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).checkCanOpenChat(i, CommonGroupsActivity.this) != null) {
                        NotificationCenter.getInstance(CommonGroupsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        CommonGroupsActivity.this.presentFragment(new ChatActivity(i), 1);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$3 */
    class C20313 extends OnScrollListener {
        C20313() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            recyclerView = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
            if (recyclerView == -1) {
                i = 0;
            } else {
                i = Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - recyclerView) + 1;
            }
            if (i > 0) {
                i2 = CommonGroupsActivity.this.listViewAdapter.getItemCount();
                if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && recyclerView + i >= i2 - 5) {
                    CommonGroupsActivity.this.getChats(((Chat) CommonGroupsActivity.this.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CommonGroupsActivity$5 */
    class C20335 implements ThemeDescriptionDelegate {
        C20335() {
        }

        public void didSetColor() {
            if (CommonGroupsActivity.this.listView != null) {
                int childCount = CommonGroupsActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = CommonGroupsActivity.this.listView.getChildAt(i);
                    if (childAt instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) childAt).update(0);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != CommonGroupsActivity.this.chats.size() ? true : null;
        }

        public int getItemCount() {
            int size = CommonGroupsActivity.this.chats.size();
            if (CommonGroupsActivity.this.chats.isEmpty()) {
                return size;
            }
            size++;
            return !CommonGroupsActivity.this.endReached ? size + 1 : size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new ProfileSearchCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new LoadingCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData((Chat) CommonGroupsActivity.this.chats.get(i), null, null, null, false, false);
                boolean z = true;
                if (i == CommonGroupsActivity.this.chats.size() - 1) {
                    if (CommonGroupsActivity.this.endReached != 0) {
                        z = false;
                    }
                }
                profileSearchCell.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            if (i < CommonGroupsActivity.this.chats.size()) {
                return 0;
            }
            return (CommonGroupsActivity.this.endReached || i != CommonGroupsActivity.this.chats.size()) ? 2 : 1;
        }
    }

    public CommonGroupsActivity(int i) {
        this.userId = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getChats(0, 50);
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", C0446R.string.GroupsInCommonTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C20291());
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", C0446R.string.NoGroupsInCommon));
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
        context = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        context.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C20302());
        this.listView.setOnScrollListener(new C20313());
        if (this.loading != null) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    private void getChats(int i, final int i2) {
        if (!this.loading) {
            this.loading = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TLObject tL_messages_getCommonChats = new TL_messages_getCommonChats();
            tL_messages_getCommonChats.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
            if (!(tL_messages_getCommonChats.user_id instanceof TL_inputUserEmpty)) {
                tL_messages_getCommonChats.limit = i2;
                tL_messages_getCommonChats.max_id = i;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getCommonChats, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tL_error == null) {
                                    boolean z;
                                    messages_Chats messages_chats = (messages_Chats) tLObject;
                                    MessagesController.getInstance(CommonGroupsActivity.this.currentAccount).putChats(messages_chats.chats, false);
                                    CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                                    if (!messages_chats.chats.isEmpty()) {
                                        if (messages_chats.chats.size() == i2) {
                                            z = false;
                                            commonGroupsActivity.endReached = z;
                                            CommonGroupsActivity.this.chats.addAll(messages_chats.chats);
                                        }
                                    }
                                    z = true;
                                    commonGroupsActivity.endReached = z;
                                    CommonGroupsActivity.this.chats.addAll(messages_chats.chats);
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
        C20335 c20335 = new C20335();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[23];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LoadingCell.class, ProfileSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[11] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C20335 c203352 = c20335;
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, c203352, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
