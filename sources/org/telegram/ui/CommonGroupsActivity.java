package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class CommonGroupsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    private int userId;

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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CommonGroupsActivity.this.lambda$createView$0$CommonGroupsActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int i3;
                int findFirstVisibleItemPosition = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition == -1) {
                    i3 = 0;
                } else {
                    i3 = Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                if (i3 > 0) {
                    int itemCount = CommonGroupsActivity.this.listViewAdapter.getItemCount();
                    if (!CommonGroupsActivity.this.endReached && !CommonGroupsActivity.this.loading && !CommonGroupsActivity.this.chats.isEmpty() && findFirstVisibleItemPosition + i3 >= itemCount - 5) {
                        CommonGroupsActivity commonGroupsActivity = CommonGroupsActivity.this;
                        commonGroupsActivity.getChats(((TLRPC.Chat) commonGroupsActivity.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
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
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chats.get(i).id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getChats(int i, int i2) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC.TL_messages_getCommonChats tL_messages_getCommonChats = new TLRPC.TL_messages_getCommonChats();
            tL_messages_getCommonChats.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.userId);
            if (!(tL_messages_getCommonChats.user_id instanceof TLRPC.TL_inputUserEmpty)) {
                tL_messages_getCommonChats.limit = i2;
                tL_messages_getCommonChats.max_id = i;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getCommonChats, new RequestDelegate(i2) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        CommonGroupsActivity.this.lambda$getChats$2$CommonGroupsActivity(this.f$1, tLObject, tL_error);
                    }
                }), this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$getChats$2$CommonGroupsActivity(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, i) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CommonGroupsActivity.this.lambda$null$1$CommonGroupsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$CommonGroupsActivity(TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        if (tL_error == null) {
            TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
            MessagesController.getInstance(this.currentAccount).putChats(messages_chats.chats, false);
            this.endReached = messages_chats.chats.isEmpty() || messages_chats.chats.size() != i;
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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != CommonGroupsActivity.this.chats.size();
        }

        public int getItemCount() {
            int size = CommonGroupsActivity.this.chats.size();
            if (CommonGroupsActivity.this.chats.isEmpty()) {
                return size;
            }
            int i = size + 1;
            return !CommonGroupsActivity.this.endReached ? i + 1 : i;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new ProfileSearchCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                view = textInfoPrivacyCell;
            } else {
                view = new LoadingCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData((TLRPC.Chat) CommonGroupsActivity.this.chats.get(i), (TLRPC.EncryptedChat) null, (CharSequence) null, (CharSequence) null, false, false);
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

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$CommonGroupsActivity$CM0EpwYGPtlvUyQJ8b7pz0BDbw r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CommonGroupsActivity.this.lambda$getThemeDescriptions$3$CommonGroupsActivity();
            }
        };
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        $$Lambda$CommonGroupsActivity$CM0EpwYGPtlvUyQJ8b7pz0BDbw r7 = r9;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LoadingCell.class, ProfileSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"), new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"), new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink")};
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
