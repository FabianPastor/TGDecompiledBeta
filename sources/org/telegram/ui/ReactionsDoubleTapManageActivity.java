package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class ReactionsDoubleTapManageActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private LinearLayout contentView;
    int infoRow;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    int premiumReactionRow;
    int previewRow;
    int reactionsStartRow = -1;
    int rowCount;
    /* access modifiers changed from: private */
    public SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        return super.onFragmentCreate();
    }

    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", R.string.Reactions));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
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
        AnonymousClass2 r2 = new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 2;
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: org.telegram.ui.ReactionsDoubleTapManageActivity$2$1} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: org.telegram.ui.Cells.AvailableReactionCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
                /*
                    r3 = this;
                    r4 = 4
                    r0 = 2
                    if (r5 == 0) goto L_0x0055
                    java.lang.String r1 = "windowBackgroundGrayShadow"
                    if (r5 == r0) goto L_0x0037
                    r0 = 3
                    if (r5 == r0) goto L_0x0029
                    if (r5 == r4) goto L_0x0016
                    org.telegram.ui.Cells.AvailableReactionCell r4 = new org.telegram.ui.Cells.AvailableReactionCell
                    android.content.Context r5 = r4
                    r0 = 1
                    r4.<init>(r5, r0, r0)
                    goto L_0x0070
                L_0x0016:
                    org.telegram.ui.ReactionsDoubleTapManageActivity$2$1 r4 = new org.telegram.ui.ReactionsDoubleTapManageActivity$2$1
                    android.content.Context r5 = r4
                    r4.<init>(r3, r5)
                    android.content.Context r5 = r4
                    int r0 = org.telegram.messenger.R.drawable.greydivider_bottom
                    android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r0, (java.lang.String) r1)
                    r4.setBackground(r5)
                    goto L_0x0070
                L_0x0029:
                    org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell r4 = new org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell
                    org.telegram.ui.ReactionsDoubleTapManageActivity r5 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    android.content.Context r0 = r4
                    r4.<init>(r0)
                    r5 = 0
                    r4.update(r5)
                    goto L_0x0070
                L_0x0037:
                    org.telegram.ui.Cells.TextInfoPrivacyCell r4 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                    android.content.Context r5 = r4
                    r4.<init>(r5)
                    int r5 = org.telegram.messenger.R.string.DoubleTapPreviewRational
                    java.lang.String r0 = "DoubleTapPreviewRational"
                    java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r0, r5)
                    r4.setText(r5)
                    android.content.Context r5 = r4
                    int r0 = org.telegram.messenger.R.drawable.greydivider
                    android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r0, (java.lang.String) r1)
                    r4.setBackground(r5)
                    goto L_0x0070
                L_0x0055:
                    org.telegram.ui.Cells.ThemePreviewMessagesCell r5 = new org.telegram.ui.Cells.ThemePreviewMessagesCell
                    android.content.Context r1 = r4
                    org.telegram.ui.ReactionsDoubleTapManageActivity r2 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    org.telegram.ui.ActionBar.ActionBarLayout r2 = r2.parentLayout
                    r5.<init>(r1, r2, r0)
                    int r0 = android.os.Build.VERSION.SDK_INT
                    r1 = 19
                    if (r0 < r1) goto L_0x006b
                    r5.setImportantForAccessibility(r4)
                L_0x006b:
                    org.telegram.ui.ReactionsDoubleTapManageActivity r4 = org.telegram.ui.ReactionsDoubleTapManageActivity.this
                    r5.fragment = r4
                    r4 = r5
                L_0x0070:
                    org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r5.<init>(r4)
                    return r5
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReactionsDoubleTapManageActivity.AnonymousClass2.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (getItemViewType(i) == 1) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) ReactionsDoubleTapManageActivity.this.getAvailableReactions().get(i - ReactionsDoubleTapManageActivity.this.reactionsStartRow);
                    ((AvailableReactionCell) viewHolder.itemView).bind(tLRPC$TL_availableReaction, tLRPC$TL_availableReaction.reaction.contains(MediaDataController.getInstance(ReactionsDoubleTapManageActivity.this.currentAccount).getDoubleTapReaction()), ReactionsDoubleTapManageActivity.this.currentAccount);
                }
            }

            public int getItemCount() {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                return reactionsDoubleTapManageActivity.rowCount + (reactionsDoubleTapManageActivity.premiumReactionRow < 0 ? reactionsDoubleTapManageActivity.getAvailableReactions().size() : 0) + 1;
            }

            public int getItemViewType(int i) {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                if (i == reactionsDoubleTapManageActivity.previewRow) {
                    return 0;
                }
                if (i == reactionsDoubleTapManageActivity.infoRow) {
                    return 2;
                }
                if (i == reactionsDoubleTapManageActivity.premiumReactionRow) {
                    return 3;
                }
                return i == getItemCount() - 1 ? 4 : 1;
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
            AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
            if (!availableReactionCell.locked || getUserConfig().isPremium()) {
                MediaDataController.getInstance(this.currentAccount).setDoubleTapReaction(availableReactionCell.react.reaction);
                this.listView.getAdapter().notifyItemRangeChanged(0, this.listView.getAdapter().getItemCount());
                return;
            }
            showDialog(new PremiumFeatureBottomSheet(this, 4, true));
        } else if (view instanceof SetDefaultReactionCell) {
            showSelectStatusDialog((SetDefaultReactionCell) view);
        }
    }

    private class SetDefaultReactionCell extends FrameLayout {
        /* access modifiers changed from: private */
        public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(24.0f));
        private TextView textView;

        public SetDefaultReactionCell(Context context) {
            super(context);
            setBackgroundColor(ReactionsDoubleTapManageActivity.this.getThemedColor("windowBackgroundWhite"));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 16.0f);
            this.textView.setTextColor(ReactionsDoubleTapManageActivity.this.getThemedColor("windowBackgroundWhiteBlackText"));
            this.textView.setText(LocaleController.getString("DoubleTapSetting", R.string.DoubleTapSetting));
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 23, 20.0f, 0.0f, 48.0f, 0.0f));
        }

        public void update(boolean z) {
            String doubleTapReaction = MediaDataController.getInstance(ReactionsDoubleTapManageActivity.this.currentAccount).getDoubleTapReaction();
            if (doubleTapReaction != null && doubleTapReaction.startsWith("animated_")) {
                try {
                    this.imageDrawable.set(Long.parseLong(doubleTapReaction.substring(9)), z);
                    return;
                } catch (Exception unused) {
                }
            }
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactionsDoubleTapManageActivity.this.currentAccount).getReactionsMap().get(doubleTapReaction);
            if (tLRPC$TL_availableReaction != null) {
                this.imageDrawable.set(tLRPC$TL_availableReaction.static_icon, z);
            }
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.imageDrawable.setBounds((getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, getWidth() - AndroidUtilities.dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
            this.imageDrawable.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageDrawable.detach();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageDrawable.attach();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x00b8 A[LOOP:0: B:20:0x00b2->B:22:0x00b8, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showSelectStatusDialog(org.telegram.ui.ReactionsDoubleTapManageActivity.SetDefaultReactionCell r17) {
        /*
            r16 = this;
            r10 = r16
            r11 = r17
            org.telegram.ui.SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow r0 = r10.selectAnimatedEmojiDialog
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            r0 = 1
            org.telegram.ui.SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow[] r12 = new org.telegram.ui.SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[r0]
            r0 = 0
            if (r11 == 0) goto L_0x0065
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r0 = r17.imageDrawable
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r1 = r17.imageDrawable
            if (r1 == 0) goto L_0x0062
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r1 = r17.imageDrawable
            r1.play()
            android.graphics.Rect r1 = org.telegram.messenger.AndroidUtilities.rectTmp2
            org.telegram.ui.Components.AnimatedEmojiDrawable$SwapAnimatedEmojiDrawable r2 = r17.imageDrawable
            android.graphics.Rect r2 = r2.getBounds()
            r1.set(r2)
            int r2 = r17.getHeight()
            int r3 = r1.centerY()
            int r2 = r2 - r3
            int r2 = -r2
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            r3 = 1134690304(0x43a20000, float:324.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            float r4 = (float) r4
            r5 = 1064514355(0x3var_, float:0.95)
            float r4 = r4 * r5
            float r3 = java.lang.Math.min(r3, r4)
            int r3 = (int) r3
            int r1 = r1.centerX()
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            int r4 = r4 - r3
            int r1 = r1 - r4
            r14 = r0
            r9 = r2
            r15 = r11
            goto L_0x0069
        L_0x0062:
            r14 = r0
            r15 = r11
            goto L_0x0067
        L_0x0065:
            r14 = r0
            r15 = r14
        L_0x0067:
            r1 = 0
            r9 = 0
        L_0x0069:
            org.telegram.ui.ReactionsDoubleTapManageActivity$3 r8 = new org.telegram.ui.ReactionsDoubleTapManageActivity$3
            android.content.Context r3 = r16.getContext()
            r4 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r1)
            r6 = 0
            r7 = 0
            r0 = r8
            r1 = r16
            r2 = r16
            r13 = r8
            r8 = r17
            r11 = r9
            r9 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.messenger.MediaDataController r0 = r16.getMediaDataController()
            java.lang.String r0 = r0.getDoubleTapReaction()
            if (r0 == 0) goto L_0x00a6
            java.lang.String r1 = "animated_"
            boolean r1 = r0.startsWith(r1)
            if (r1 == 0) goto L_0x00a6
            r1 = 9
            java.lang.String r0 = r0.substring(r1)     // Catch:{ Exception -> 0x00a6 }
            long r0 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x00a6 }
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x00a6 }
            r13.setSelected(r0)     // Catch:{ Exception -> 0x00a6 }
        L_0x00a6:
            java.util.List r0 = r16.getAvailableReactions()
            java.util.ArrayList r1 = new java.util.ArrayList
            r2 = 20
            r1.<init>(r2)
            r2 = 0
        L_0x00b2:
            int r3 = r0.size()
            if (r2 >= r3) goto L_0x00cd
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r3 = new org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction
            r3.<init>()
            java.lang.Object r4 = r0.get(r2)
            org.telegram.tgnet.TLRPC$TL_availableReaction r4 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r4
            java.lang.String r4 = r4.reaction
            r3.emojicon = r4
            r1.add(r3)
            int r2 = r2 + 1
            goto L_0x00b2
        L_0x00cd:
            r13.setRecentReactions(r1)
            r0 = 3
            r13.setSaveState(r0)
            r13.setScrimDrawable(r14, r15)
            org.telegram.ui.ReactionsDoubleTapManageActivity$4 r0 = new org.telegram.ui.ReactionsDoubleTapManageActivity$4
            r1 = -2
            r0.<init>(r13, r1, r1)
            r10.selectAnimatedEmojiDialog = r0
            r1 = 0
            r12[r1] = r0
            r0 = r12[r1]
            r2 = 53
            r3 = r17
            r13 = r11
            r0.showAsDropDown(r3, r1, r13, r2)
            r0 = r12[r1]
            r0.dimBehind()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReactionsDoubleTapManageActivity.showSelectStatusDialog(org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell):void");
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        this.rowCount = i + 1;
        this.infoRow = i;
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.reactionsStartRow = -1;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.premiumReactionRow = i2;
            return;
        }
        this.premiumReactionRow = -1;
        this.reactionsStartRow = this.rowCount;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
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
        if (i2 == this.currentAccount) {
            if (i == NotificationCenter.reactionsDidLoad) {
                this.listAdapter.notifyDataSetChanged();
            } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
                updateRows();
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }
}
