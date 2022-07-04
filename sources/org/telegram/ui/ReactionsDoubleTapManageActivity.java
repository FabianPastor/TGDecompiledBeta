package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;

public class ReactionsDoubleTapManageActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private LinearLayout contentView;
    int infoRow;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    int previewRow;
    int reactionsStartRow;
    int rowCount;

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        return super.onFragmentCreate();
    }

    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ReactionsDoubleTapManageActivity.this.finishFragment();
                }
            }
        });
        LinearLayout linaerLayout = new LinearLayout(context);
        linaerLayout.setOrientation(1);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setSupportsChangeAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r2 = new RecyclerView.Adapter() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: org.telegram.ui.Cells.AvailableReactionCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
                /*
                    r4 = this;
                    switch(r6) {
                        case 0: goto L_0x0022;
                        case 1: goto L_0x0003;
                        case 2: goto L_0x000d;
                        default: goto L_0x0003;
                    }
                L_0x0003:
                    org.telegram.ui.Cells.AvailableReactionCell r0 = new org.telegram.ui.Cells.AvailableReactionCell
                    android.content.Context r1 = r4
                    r2 = 1
                    r0.<init>(r1, r2)
                    r1 = r0
                    goto L_0x0040
                L_0x000d:
                    org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                    android.content.Context r1 = r4
                    r0.<init>(r1)
                    r1 = 2131625528(0x7f0e0638, float:1.8878266E38)
                    java.lang.String r2 = "DoubleTapPreviewRational"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                    r0.setText(r1)
                    r1 = r0
                    goto L_0x0040
                L_0x0022:
                    org.telegram.ui.Cells.ThemePreviewMessagesCell r0 = new org.telegram.ui.Cells.ThemePreviewMessagesCell
                    android.content.Context r1 = r4
                    org.telegram.ui.ReactionsDoubleTapManageActivity r2 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    org.telegram.ui.ActionBar.ActionBarLayout r2 = r2.parentLayout
                    r3 = 2
                    r0.<init>(r1, r2, r3)
                    int r1 = android.os.Build.VERSION.SDK_INT
                    r2 = 19
                    if (r1 < r2) goto L_0x003a
                    r1 = 4
                    r0.setImportantForAccessibility(r1)
                L_0x003a:
                    org.telegram.ui.ReactionsDoubleTapManageActivity r1 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    r0.fragment = r1
                    r1 = r0
                L_0x0040:
                    org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r0.<init>(r1)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReactionsDoubleTapManageActivity.AnonymousClass2.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                switch (getItemViewType(position)) {
                    case 1:
                        TLRPC.TL_availableReaction react = (TLRPC.TL_availableReaction) ReactionsDoubleTapManageActivity.this.getAvailableReactions().get(position - ReactionsDoubleTapManageActivity.this.reactionsStartRow);
                        ((AvailableReactionCell) holder.itemView).bind(react, react.reaction.contains(MediaDataController.getInstance(ReactionsDoubleTapManageActivity.this.currentAccount).getDoubleTapReaction()));
                        return;
                    default:
                        return;
                }
            }

            public int getItemCount() {
                return ReactionsDoubleTapManageActivity.this.getAvailableReactions().size();
            }

            public int getItemViewType(int position) {
                if (position == ReactionsDoubleTapManageActivity.this.previewRow) {
                    return 0;
                }
                if (position == ReactionsDoubleTapManageActivity.this.infoRow) {
                    return 2;
                }
                return 1;
            }
        };
        this.listAdapter = r2;
        recyclerListView2.setAdapter(r2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactionsDoubleTapManageActivity$$ExternalSyntheticLambda1(this));
        linaerLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.contentView = linaerLayout;
        this.fragmentView = linaerLayout;
        updateColors();
        updateRows();
        return this.contentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ReactionsDoubleTapManageActivity  reason: not valid java name */
    public /* synthetic */ void m4570x634b36cc(View view, int position) {
        if (view instanceof AvailableReactionCell) {
            MediaDataController.getInstance(this.currentAccount).setDoubleTapReaction(((AvailableReactionCell) view).react.reaction);
            this.listView.getAdapter().notifyItemRangeChanged(0, this.listView.getAdapter().getItemCount());
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.infoRow = i;
        this.rowCount = i2 + 1;
        this.reactionsStartRow = i2;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    /* access modifiers changed from: private */
    public List<TLRPC.TL_availableReaction> getAvailableReactions() {
        return getMediaDataController().getReactionsList();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ReactionsDoubleTapManageActivity$$ExternalSyntheticLambda0(this), "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (account == this.currentAccount && id == NotificationCenter.reactionsDidLoad) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
