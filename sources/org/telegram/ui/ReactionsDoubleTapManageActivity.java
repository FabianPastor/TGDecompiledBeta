package org.telegram.ui;

import android.annotation.SuppressLint;
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
import org.telegram.tgnet.TLRPC$TL_availableReaction;
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
            public void onItemClick(int i) {
                if (i == -1) {
                    ReactionsDoubleTapManageActivity.this.finishFragment();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setSupportsChangeAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r2 = new RecyclerView.Adapter() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.ui.Cells.AvailableReactionCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
                /*
                    r2 = this;
                    r3 = 2
                    if (r4 == 0) goto L_0x0022
                    if (r4 == r3) goto L_0x000e
                    org.telegram.ui.Cells.AvailableReactionCell r3 = new org.telegram.ui.Cells.AvailableReactionCell
                    android.content.Context r4 = r4
                    r0 = 1
                    r3.<init>(r4, r0)
                    goto L_0x003e
                L_0x000e:
                    org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                    android.content.Context r4 = r4
                    r3.<init>(r4)
                    r4 = 2131625432(0x7f0e05d8, float:1.8878072E38)
                    java.lang.String r0 = "DoubleTapPreviewRational"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r0, r4)
                    r3.setText(r4)
                    goto L_0x003e
                L_0x0022:
                    org.telegram.ui.Cells.ThemePreviewMessagesCell r4 = new org.telegram.ui.Cells.ThemePreviewMessagesCell
                    android.content.Context r0 = r4
                    org.telegram.ui.ReactionsDoubleTapManageActivity r1 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    org.telegram.ui.ActionBar.ActionBarLayout r1 = r1.parentLayout
                    r4.<init>(r0, r1, r3)
                    int r3 = android.os.Build.VERSION.SDK_INT
                    r0 = 19
                    if (r3 < r0) goto L_0x0039
                    r3 = 4
                    r4.setImportantForAccessibility(r3)
                L_0x0039:
                    org.telegram.ui.ReactionsDoubleTapManageActivity r3 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    r4.fragment = r3
                    r3 = r4
                L_0x003e:
                    org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r4.<init>(r3)
                    return r4
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReactionsDoubleTapManageActivity.AnonymousClass2.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (getItemViewType(i) == 1) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) ReactionsDoubleTapManageActivity.this.getAvailableReactions().get(i - ReactionsDoubleTapManageActivity.this.reactionsStartRow);
                    ((AvailableReactionCell) viewHolder.itemView).bind(tLRPC$TL_availableReaction, tLRPC$TL_availableReaction.reaction.contains(MediaDataController.getInstance(ReactionsDoubleTapManageActivity.this.currentAccount).getDoubleTapReaction()));
                }
            }

            public int getItemCount() {
                return ReactionsDoubleTapManageActivity.this.getAvailableReactions().size();
            }

            public int getItemViewType(int i) {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                if (i == reactionsDoubleTapManageActivity.previewRow) {
                    return 0;
                }
                return i == reactionsDoubleTapManageActivity.infoRow ? 2 : 1;
            }
        };
        this.listAdapter = r2;
        recyclerListView2.setAdapter(r2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactionsDoubleTapManageActivity$$ExternalSyntheticLambda1(this));
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        updateRows();
        return this.contentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
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
    public List<TLRPC$TL_availableReaction> getAvailableReactions() {
        return getMediaDataController().getReactionsList();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ReactionsDoubleTapManageActivity$$ExternalSyntheticLambda0(this), "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listAdapter.notifyDataSetChanged();
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 == this.currentAccount && i == NotificationCenter.reactionsDidLoad) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
