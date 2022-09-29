package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;

public class DoubledLimitsBottomSheet$Adapter extends RecyclerListView.SelectionAdapter {
    ViewGroup containerView;
    boolean drawHeader;
    PremiumGradient.GradientTools gradientTools;
    int headerRow;
    int lastViewRow;
    final ArrayList<DoubledLimitsBottomSheet$Limit> limits;
    int limitsStartRow;
    int rowCount = 0;
    private int totalGradientHeight;

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    public DoubledLimitsBottomSheet$Adapter(int i, boolean z) {
        ArrayList<DoubledLimitsBottomSheet$Limit> arrayList = new ArrayList<>();
        this.limits = arrayList;
        this.drawHeader = z;
        PremiumGradient.GradientTools gradientTools2 = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools2;
        gradientTools2.x1 = 0.0f;
        gradientTools2.y1 = 0.0f;
        gradientTools2.x2 = 0.0f;
        gradientTools2.y2 = 1.0f;
        MessagesController instance = MessagesController.getInstance(i);
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("GroupsAndChannelsLimitTitle", R.string.GroupsAndChannelsLimitTitle), LocaleController.formatString("GroupsAndChannelsLimitSubtitle", R.string.GroupsAndChannelsLimitSubtitle, Integer.valueOf(instance.channelsLimitPremium)), instance.channelsLimitDefault, instance.channelsLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("PinChatsLimitTitle", R.string.PinChatsLimitTitle), LocaleController.formatString("PinChatsLimitSubtitle", R.string.PinChatsLimitSubtitle, Integer.valueOf(instance.dialogFiltersPinnedLimitPremium)), instance.dialogFiltersPinnedLimitDefault, instance.dialogFiltersPinnedLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("PublicLinksLimitTitle", R.string.PublicLinksLimitTitle), LocaleController.formatString("PublicLinksLimitSubtitle", R.string.PublicLinksLimitSubtitle, Integer.valueOf(instance.publicLinksLimitPremium)), instance.publicLinksLimitDefault, instance.publicLinksLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("SavedGifsLimitTitle", R.string.SavedGifsLimitTitle), LocaleController.formatString("SavedGifsLimitSubtitle", R.string.SavedGifsLimitSubtitle, Integer.valueOf(instance.savedGifsLimitPremium)), instance.savedGifsLimitDefault, instance.savedGifsLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("FavoriteStickersLimitTitle", R.string.FavoriteStickersLimitTitle), LocaleController.formatString("FavoriteStickersLimitSubtitle", R.string.FavoriteStickersLimitSubtitle, Integer.valueOf(instance.stickersFavedLimitPremium)), instance.stickersFavedLimitDefault, instance.stickersFavedLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("BioLimitTitle", R.string.BioLimitTitle), LocaleController.formatString("BioLimitSubtitle", R.string.BioLimitSubtitle, Integer.valueOf(instance.stickersFavedLimitPremium)), instance.aboutLengthLimitDefault, instance.aboutLengthLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("CaptionsLimitTitle", R.string.CaptionsLimitTitle), LocaleController.formatString("CaptionsLimitSubtitle", R.string.CaptionsLimitSubtitle, Integer.valueOf(instance.stickersFavedLimitPremium)), instance.captionLengthLimitDefault, instance.captionLengthLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("FoldersLimitTitle", R.string.FoldersLimitTitle), LocaleController.formatString("FoldersLimitSubtitle", R.string.FoldersLimitSubtitle, Integer.valueOf(instance.dialogFiltersLimitPremium)), instance.dialogFiltersLimitDefault, instance.dialogFiltersLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("ChatPerFolderLimitTitle", R.string.ChatPerFolderLimitTitle), LocaleController.formatString("ChatPerFolderLimitSubtitle", R.string.ChatPerFolderLimitSubtitle, Integer.valueOf(instance.dialogFiltersChatsLimitPremium)), instance.dialogFiltersChatsLimitDefault, instance.dialogFiltersChatsLimitPremium, (DoubledLimitsBottomSheet$1) null));
        arrayList.add(new DoubledLimitsBottomSheet$Limit(LocaleController.getString("ConnectedAccountsLimitTitle", R.string.ConnectedAccountsLimitTitle), LocaleController.formatString("ConnectedAccountsLimitSubtitle", R.string.ConnectedAccountsLimitSubtitle, 4), 3, 4, (DoubledLimitsBottomSheet$1) null));
        int i2 = 1 + 0;
        this.rowCount = i2;
        this.headerRow = 0;
        this.limitsStartRow = i2;
        this.rowCount = i2 + arrayList.size();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
    /* JADX WARNING: type inference failed for: r15v4, types: [org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Adapter$1, android.widget.FrameLayout] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
        /*
            r13 = this;
            android.content.Context r14 = r14.getContext()
            r0 = 16
            r1 = 1
            r2 = -2
            if (r15 == r1) goto L_0x0029
            r1 = 2
            if (r15 == r1) goto L_0x0022
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell r15 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell
            r15.<init>(r14)
            org.telegram.ui.Components.Premium.LimitPreviewView r14 = r15.previewView
            android.view.ViewGroup r0 = r13.containerView
            r14.setParentViewForGradien(r0)
            org.telegram.ui.Components.Premium.LimitPreviewView r14 = r15.previewView
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r13.gradientTools
            r14.setStaticGradinet(r0)
            goto L_0x00a5
        L_0x0022:
            org.telegram.ui.Cells.FixedHeightEmptyCell r15 = new org.telegram.ui.Cells.FixedHeightEmptyCell
            r15.<init>(r14, r0)
            goto L_0x00a5
        L_0x0029:
            boolean r15 = r13.drawHeader
            if (r15 == 0) goto L_0x009e
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Adapter$1 r15 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Adapter$1
            r15.<init>(r13, r14)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r14)
            r4 = 0
            r3.setOrientation(r4)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r14)
            org.telegram.ui.Components.Premium.PremiumGradient r5 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            int r6 = org.telegram.messenger.R.drawable.other_2x_large
            android.graphics.drawable.Drawable r6 = androidx.core.content.ContextCompat.getDrawable(r14, r6)
            org.telegram.ui.Components.Premium.PremiumGradient$InternalDrawable r5 = r5.createGradientDrawable(r6)
            r4.setImageDrawable(r5)
            r6 = 40
            r7 = 1105199104(0x41e00000, float:28.0)
            r8 = 16
            r9 = 0
            r10 = 0
            r11 = 1090519040(0x41000000, float:8.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r3.addView(r4, r5)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r14)
            int r14 = org.telegram.messenger.R.string.DoubledLimits
            java.lang.String r5 = "DoubledLimits"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r5, r14)
            r4.setText(r14)
            r14 = 17
            r4.setGravity(r14)
            r5 = 1101004800(0x41a00000, float:20.0)
            r4.setTextSize(r1, r5)
            java.lang.String r1 = "windowBackgroundWhiteBlackText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r4.setTextColor(r1)
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r4.setTypeface(r1)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r0)
            r3.addView(r4, r0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r14)
            r15.addView(r3, r14)
            goto L_0x00a5
        L_0x009e:
            org.telegram.ui.Cells.FixedHeightEmptyCell r15 = new org.telegram.ui.Cells.FixedHeightEmptyCell
            r0 = 64
            r15.<init>(r14, r0)
        L_0x00a5:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r14 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r0 = -1
            r14.<init>((int) r0, (int) r2)
            r15.setLayoutParams(r14)
            org.telegram.ui.Components.RecyclerListView$Holder r14 = new org.telegram.ui.Components.RecyclerListView$Holder
            r14.<init>(r15)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DoubledLimitsBottomSheet$LimitCell doubledLimitsBottomSheet$LimitCell = (DoubledLimitsBottomSheet$LimitCell) viewHolder.itemView;
            doubledLimitsBottomSheet$LimitCell.setData(this.limits.get(i - this.limitsStartRow));
            doubledLimitsBottomSheet$LimitCell.previewView.gradientYOffset = this.limits.get(i - this.limitsStartRow).yOffset;
            doubledLimitsBottomSheet$LimitCell.previewView.gradientTotalHeight = this.totalGradientHeight;
        }
    }

    public int getItemCount() {
        return this.rowCount;
    }

    public int getItemViewType(int i) {
        if (i == this.headerRow) {
            return 1;
        }
        return i == this.lastViewRow ? 2 : 0;
    }

    public void measureGradient(Context context, int i, int i2) {
        DoubledLimitsBottomSheet$LimitCell doubledLimitsBottomSheet$LimitCell = new DoubledLimitsBottomSheet$LimitCell(context);
        int i3 = 0;
        for (int i4 = 0; i4 < this.limits.size(); i4++) {
            doubledLimitsBottomSheet$LimitCell.setData(this.limits.get(i4));
            doubledLimitsBottomSheet$LimitCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.limits.get(i4).yOffset = i3;
            i3 += doubledLimitsBottomSheet$LimitCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }
}
