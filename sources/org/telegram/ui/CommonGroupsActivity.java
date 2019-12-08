package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != CommonGroupsActivity.this.chats.size();
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
            View profileSearchCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                profileSearchCell = new ProfileSearchCell(this.mContext);
                profileSearchCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 1) {
                View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                profileSearchCell = textInfoPrivacyCell;
            } else {
                profileSearchCell = new LoadingCell(this.mContext);
                profileSearchCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(profileSearchCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData((Chat) CommonGroupsActivity.this.chats.get(i), null, null, null, false, false);
                boolean z = true;
                if (i == CommonGroupsActivity.this.chats.size() - 1 && CommonGroupsActivity.this.endReached) {
                    z = false;
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
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    CommonGroupsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$CommonGroupsActivity$J3Yy-YOVYpXyEU8UkIqEdngAT6I(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int findFirstVisibleItemPosition = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition == -1) {
                    i = 0;
                } else {
                    i = Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                if (i > 0) {
                    i2 = CommonGroupsActivity.this.listViewAdapter.getItemCount();
                    if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && findFirstVisibleItemPosition + i >= i2 - 5) {
                        CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                        commonGroupsActivity.getChats(((Chat) commonGroupsActivity.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
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

    public /* synthetic */ void lambda$createView$0$CommonGroupsActivity(View view, int i) {
        if (i >= 0 && i < this.chats.size()) {
            Chat chat = (Chat) this.chats.get(i);
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", chat.id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        }
    }

    private void getChats(int i, int i2) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (!(emptyTextProgressView == null || this.firstLoaded)) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TL_messages_getCommonChats tL_messages_getCommonChats = new TL_messages_getCommonChats();
            tL_messages_getCommonChats.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
            if (!(tL_messages_getCommonChats.user_id instanceof TL_inputUserEmpty)) {
                tL_messages_getCommonChats.limit = i2;
                tL_messages_getCommonChats.max_id = i;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getCommonChats, new -$$Lambda$CommonGroupsActivity$uEpDyQSDsMXTcD9ZAD3h6fxSQJA(this, i2)), this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$getChats$2$CommonGroupsActivity(int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$CommonGroupsActivity$khNmAu1RhPThZuRoaDt1rnPjqQQ(this, tL_error, tLObject, i));
    }

    public /* synthetic */ void lambda$null$1$CommonGroupsActivity(TL_error tL_error, TLObject tLObject, int i) {
        if (tL_error == null) {
            messages_Chats messages_chats = (messages_Chats) tLObject;
            MessagesController.getInstance(this.currentAccount).putChats(messages_chats.chats, false);
            boolean z = messages_chats.chats.isEmpty() || messages_chats.chats.size() != i;
            this.endReached = z;
            this.chats.addAll(messages_chats.chats);
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$CommonGroupsActivity$CM0EpwYGPtlvUyQJ8b7pz0B-Dbw -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw = new -$$Lambda$CommonGroupsActivity$CM0EpwYGPtlvUyQJ8b7pz0B-Dbw(this);
        r10 = new ThemeDescription[24];
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
        View view = this.listView;
        View view2 = view;
        r10[11] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, null, null, "chats_name");
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, null, null, "chats_secretName");
        r10[16] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$CommonGroupsActivity$CM0EpwYGPtlvUyQJ8b7pz0B-Dbw -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2 = -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw;
        r10[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundRed");
        r10[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundOrange");
        r10[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundViolet");
        r10[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundGreen");
        r10[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundCyan");
        r10[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundBlue");
        r10[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_commongroupsactivity_cm0epwygptlvuyqj8b7pz0b-dbw2, "avatar_backgroundPink");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$CommonGroupsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                }
            }
        }
    }
}
