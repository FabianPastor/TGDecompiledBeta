package org.telegram.ui.Components.Premium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;

public class DoubledLimitsBottomSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private View divider;
    PremiumGradient.GradientTools gradientTools;
    int headerRow;
    int lastViewRow;
    final ArrayList<Limit> limits;
    int limitsStartRow;
    PremiumButtonView premiumButtonView;
    int rowCount;
    private PremiumPreviewFragment.SubscriptionTier selectedTier;
    ImageView titleImage;
    FrameLayout titleLayout;
    float titleProgress;
    TextView titleView;
    /* access modifiers changed from: private */
    public int totalGradientHeight;

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        return null;
    }

    public void setParentFragment(PremiumPreviewFragment premiumPreviewFragment) {
    }

    public DoubledLimitsBottomSheet(BaseFragment baseFragment, int i) {
        this(baseFragment, i, (PremiumPreviewFragment.SubscriptionTier) null);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DoubledLimitsBottomSheet(org.telegram.ui.ActionBar.BaseFragment r27, int r28, org.telegram.ui.PremiumPreviewFragment.SubscriptionTier r29) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = 0
            r0.<init>(r1, r3, r3)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.limits = r4
            r5 = r29
            r0.selectedTier = r5
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r5 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            java.lang.String r6 = "premiumGradient1"
            java.lang.String r7 = "premiumGradient2"
            java.lang.String r8 = "premiumGradient3"
            java.lang.String r9 = "premiumGradient4"
            r5.<init>(r6, r7, r8, r9)
            r0.gradientTools = r5
            r6 = 0
            r5.x1 = r6
            r5.y1 = r6
            r5.x2 = r6
            r6 = 1065353216(0x3var_, float:1.0)
            r5.y2 = r6
            r5 = 1
            r0.clipToActionBar = r5
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r28)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r13 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r7 = org.telegram.messenger.R.string.GroupsAndChannelsLimitTitle
            java.lang.String r8 = "GroupsAndChannelsLimitTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r7 = org.telegram.messenger.R.string.GroupsAndChannelsLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.channelsLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "GroupsAndChannelsLimitSubtitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r10, r7, r9)
            int r10 = r6.channelsLimitDefault
            int r11 = r6.channelsLimitPremium
            r12 = 0
            r7 = r13
            r7.<init>(r8, r9, r10, r11)
            r4.add(r13)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.PinChatsLimitTitle
            java.lang.String r9 = "PinChatsLimitTitle"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.PinChatsLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.dialogFiltersPinnedLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "PinChatsLimitSubtitle"
            java.lang.String r16 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r8 = r6.dialogFiltersPinnedLimitDefault
            int r9 = r6.dialogFiltersPinnedLimitPremium
            r19 = 0
            r14 = r7
            r17 = r8
            r18 = r9
            r14.<init>(r15, r16, r17, r18)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.PublicLinksLimitTitle
            java.lang.String r9 = "PublicLinksLimitTitle"
            java.lang.String r21 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.PublicLinksLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.publicLinksLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "PublicLinksLimitSubtitle"
            java.lang.String r22 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r8 = r6.publicLinksLimitDefault
            int r9 = r6.publicLinksLimitPremium
            r25 = 0
            r20 = r7
            r23 = r8
            r24 = r9
            r20.<init>(r21, r22, r23, r24)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.SavedGifsLimitTitle
            java.lang.String r9 = "SavedGifsLimitTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.SavedGifsLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.savedGifsLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "SavedGifsLimitSubtitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r13 = r6.savedGifsLimitDefault
            int r14 = r6.savedGifsLimitPremium
            r15 = 0
            r10 = r7
            r10.<init>(r11, r12, r13, r14)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.FavoriteStickersLimitTitle
            java.lang.String r9 = "FavoriteStickersLimitTitle"
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.FavoriteStickersLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.stickersFavedLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "FavoriteStickersLimitSubtitle"
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r8 = r6.stickersFavedLimitDefault
            int r9 = r6.stickersFavedLimitPremium
            r21 = 0
            r16 = r7
            r19 = r8
            r20 = r9
            r16.<init>(r17, r18, r19, r20)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.BioLimitTitle
            java.lang.String r9 = "BioLimitTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.BioLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.stickersFavedLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "BioLimitSubtitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r13 = r6.aboutLengthLimitDefault
            int r14 = r6.aboutLengthLimitPremium
            r10 = r7
            r10.<init>(r11, r12, r13, r14)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.CaptionsLimitTitle
            java.lang.String r9 = "CaptionsLimitTitle"
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.CaptionsLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.stickersFavedLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "CaptionsLimitSubtitle"
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r8 = r6.captionLengthLimitDefault
            int r9 = r6.captionLengthLimitPremium
            r16 = r7
            r19 = r8
            r20 = r9
            r16.<init>(r17, r18, r19, r20)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.FoldersLimitTitle
            java.lang.String r9 = "FoldersLimitTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.FoldersLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.dialogFiltersLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "FoldersLimitSubtitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r13 = r6.dialogFiltersLimitDefault
            int r14 = r6.dialogFiltersLimitPremium
            r10 = r7
            r10.<init>(r11, r12, r13, r14)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r7 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r8 = org.telegram.messenger.R.string.ChatPerFolderLimitTitle
            java.lang.String r9 = "ChatPerFolderLimitTitle"
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r9, r8)
            int r8 = org.telegram.messenger.R.string.ChatPerFolderLimitSubtitle
            java.lang.Object[] r9 = new java.lang.Object[r5]
            int r10 = r6.dialogFiltersChatsLimitPremium
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9[r3] = r10
            java.lang.String r10 = "ChatPerFolderLimitSubtitle"
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatString(r10, r8, r9)
            int r8 = r6.dialogFiltersChatsLimitDefault
            int r6 = r6.dialogFiltersChatsLimitPremium
            r16 = r7
            r19 = r8
            r20 = r6
            r16.<init>(r17, r18, r19, r20)
            r4.add(r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            int r7 = org.telegram.messenger.R.string.ConnectedAccountsLimitTitle
            java.lang.String r8 = "ConnectedAccountsLimitTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r7 = org.telegram.messenger.R.string.ConnectedAccountsLimitSubtitle
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r9 = 4
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r3] = r9
            java.lang.String r9 = "ConnectedAccountsLimitSubtitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r9, r7, r8)
            r12 = 3
            r13 = 4
            r14 = 0
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r4.add(r6)
            r0.rowCount = r3
            r6 = 0
            int r6 = r6 + r5
            r0.rowCount = r6
            r0.headerRow = r3
            r0.limitsStartRow = r6
            int r4 = r4.size()
            int r6 = r6 + r4
            r0.rowCount = r6
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            android.content.Context r6 = r26.getContext()
            r4.<init>(r6)
            r0.titleLayout = r4
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r6 = r26.getContext()
            r4.<init>(r6)
            r0.titleView = r4
            int r6 = org.telegram.messenger.R.string.DoubledLimits
            java.lang.String r7 = "DoubledLimits"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            android.widget.TextView r4 = r0.titleView
            r6 = 17
            r4.setGravity(r6)
            android.widget.TextView r4 = r0.titleView
            r6 = 1101004800(0x41a00000, float:20.0)
            r4.setTextSize(r5, r6)
            android.widget.TextView r4 = r0.titleView
            java.lang.String r6 = "windowBackgroundWhiteBlackText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setTextColor(r6)
            android.widget.TextView r4 = r0.titleView
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r4.setTypeface(r6)
            android.widget.FrameLayout r4 = r0.titleLayout
            android.widget.TextView r6 = r0.titleView
            r7 = -2
            r8 = 16
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r8)
            r4.addView(r6, r7)
            android.widget.ImageView r4 = new android.widget.ImageView
            android.content.Context r6 = r26.getContext()
            r4.<init>(r6)
            r0.titleImage = r4
            org.telegram.ui.Components.Premium.PremiumGradient r6 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.content.Context r7 = r26.getContext()
            int r9 = org.telegram.messenger.R.drawable.other_2x_large
            android.graphics.drawable.Drawable r7 = androidx.core.content.ContextCompat.getDrawable(r7, r9)
            org.telegram.ui.Components.Premium.PremiumGradient$InternalDrawable r6 = r6.createGradientDrawable(r7)
            r4.setImageDrawable(r6)
            android.widget.FrameLayout r4 = r0.titleLayout
            android.widget.ImageView r6 = r0.titleImage
            r7 = 40
            r9 = 28
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r9, (int) r8)
            r4.addView(r6, r7)
            android.view.ViewGroup r4 = r0.containerView
            android.widget.FrameLayout r6 = r0.titleLayout
            r7 = -1
            r8 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
            r4.addView(r6, r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1 r4 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1
            android.content.Context r6 = r26.getContext()
            r4.<init>(r0, r6)
            r0.divider = r4
            java.lang.String r6 = "dialogBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setBackgroundColor(r6)
            android.view.ViewGroup r4 = r0.containerView
            android.view.View r6 = r0.divider
            r7 = -1
            r8 = 1116733440(0x42900000, float:72.0)
            r9 = 80
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r4.addView(r6, r7)
            org.telegram.ui.Components.Premium.PremiumButtonView r4 = new org.telegram.ui.Components.Premium.PremiumButtonView
            android.content.Context r6 = r26.getContext()
            r4.<init>(r6, r5)
            r0.premiumButtonView = r4
            org.telegram.ui.Components.AnimatedTextView r4 = r4.buttonTextView
            org.telegram.ui.PremiumPreviewFragment$SubscriptionTier r5 = r0.selectedTier
            java.lang.String r5 = org.telegram.ui.PremiumPreviewFragment.getPremiumButtonText(r2, r5)
            r4.setText(r5)
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.Components.Premium.PremiumButtonView r5 = r0.premiumButtonView
            r6 = -1
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 80
            r9 = 1098907648(0x41800000, float:16.0)
            r11 = 1098907648(0x41800000, float:16.0)
            r12 = 1094713344(0x41400000, float:12.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r4.addView(r5, r6)
            org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.premiumButtonView
            android.widget.FrameLayout r4 = r4.buttonLayout
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1
            r5.<init>(r0, r2, r1)
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView
            android.widget.TextView r1 = r1.overlayTextView
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.recyclerListView
            r2 = 1116733440(0x42900000, float:72.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r3, r3, r3, r2)
            int r1 = r26.getCurrentAccount()
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isPremium()
            r0.bindPremium(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.<init>(org.telegram.ui.ActionBar.BaseFragment, int, org.telegram.ui.PremiumPreviewFragment$SubscriptionTier):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, BaseFragment baseFragment, View view) {
        if (!UserConfig.getInstance(i).isPremium()) {
            PremiumPreviewFragment.buyPremium(baseFragment, this.selectedTier, "double_limits");
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        dismiss();
    }

    private void bindPremium(boolean z) {
        if (z) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    /* access modifiers changed from: protected */
    public void onPreDraw(Canvas canvas, int i, float f) {
        float measuredHeight = ((float) AndroidUtilities.statusBarHeight) + (((float) ((this.actionBar.getMeasuredHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(40.0f))) / 2.0f);
        float measuredWidth = ((float) (((this.titleLayout.getMeasuredWidth() - this.titleView.getMeasuredWidth()) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f))) / 2.0f;
        float dp = (float) ((AndroidUtilities.dp(72.0f) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f));
        float measuredWidth2 = ((float) this.titleImage.getMeasuredWidth()) + measuredWidth + ((float) AndroidUtilities.dp(6.0f));
        float dp2 = (float) AndroidUtilities.dp(72.0f);
        float max = Math.max((float) (i + AndroidUtilities.dp(24.0f)), measuredHeight);
        if (f > 0.0f) {
            float f2 = this.titleProgress;
            if (f2 != 1.0f) {
                float f3 = f2 + 0.10666667f;
                this.titleProgress = f3;
                if (f3 > 1.0f) {
                    this.titleProgress = 1.0f;
                }
                this.containerView.invalidate();
                FrameLayout frameLayout = this.titleLayout;
                float f4 = this.titleProgress;
                frameLayout.setTranslationY((max * (1.0f - f4)) + (measuredHeight * f4));
                TextView textView = this.titleView;
                float f5 = this.titleProgress;
                textView.setTranslationX((measuredWidth2 * (1.0f - f5)) + (dp2 * f5));
                ImageView imageView = this.titleImage;
                float f6 = this.titleProgress;
                imageView.setTranslationX((measuredWidth * (1.0f - f6)) + (dp * f6));
                this.titleImage.setAlpha(1.0f - this.titleProgress);
                float f7 = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
                this.titleImage.setScaleX(f7);
                this.titleImage.setScaleY(f7);
            }
        }
        if (f == 0.0f) {
            float f8 = this.titleProgress;
            if (f8 != 0.0f) {
                float f9 = f8 - 0.10666667f;
                this.titleProgress = f9;
                if (f9 < 0.0f) {
                    this.titleProgress = 0.0f;
                }
                this.containerView.invalidate();
            }
        }
        FrameLayout frameLayout2 = this.titleLayout;
        float var_ = this.titleProgress;
        frameLayout2.setTranslationY((max * (1.0f - var_)) + (measuredHeight * var_));
        TextView textView2 = this.titleView;
        float var_ = this.titleProgress;
        textView2.setTranslationX((measuredWidth2 * (1.0f - var_)) + (dp2 * var_));
        ImageView imageView2 = this.titleImage;
        float var_ = this.titleProgress;
        imageView2.setTranslationX((measuredWidth * (1.0f - var_)) + (dp * var_));
        this.titleImage.setAlpha(1.0f - this.titleProgress);
        float var_ = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
        this.titleImage.setScaleX(var_);
        this.titleImage.setScaleY(var_);
    }

    /* access modifiers changed from: protected */
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return false;
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
                /*
                    r2 = this;
                    android.content.Context r3 = r3.getContext()
                    r0 = 1
                    if (r4 == r0) goto L_0x002e
                    r0 = 2
                    if (r4 == r0) goto L_0x0026
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell r4 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r0 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    r4.<init>(r0, r3)
                    org.telegram.ui.Components.Premium.LimitPreviewView r3 = r4.previewView
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r0 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    android.view.ViewGroup r0 = r0.containerView
                    r3.setParentViewForGradien(r0)
                    org.telegram.ui.Components.Premium.LimitPreviewView r3 = r4.previewView
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r0 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r0.gradientTools
                    r3.setStaticGradinet(r0)
                    goto L_0x0035
                L_0x0026:
                    org.telegram.ui.Cells.FixedHeightEmptyCell r4 = new org.telegram.ui.Cells.FixedHeightEmptyCell
                    r0 = 16
                    r4.<init>(r3, r0)
                    goto L_0x0035
                L_0x002e:
                    org.telegram.ui.Cells.FixedHeightEmptyCell r4 = new org.telegram.ui.Cells.FixedHeightEmptyCell
                    r0 = 64
                    r4.<init>(r3, r0)
                L_0x0035:
                    androidx.recyclerview.widget.RecyclerView$LayoutParams r3 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                    r0 = -1
                    r1 = -2
                    r3.<init>((int) r0, (int) r1)
                    r4.setLayoutParams(r3)
                    org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r3.<init>(r4)
                    return r3
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.AnonymousClass2.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    LimitCell limitCell = (LimitCell) viewHolder.itemView;
                    DoubledLimitsBottomSheet doubledLimitsBottomSheet = DoubledLimitsBottomSheet.this;
                    limitCell.setData(doubledLimitsBottomSheet.limits.get(i - doubledLimitsBottomSheet.limitsStartRow));
                    LimitPreviewView limitPreviewView = limitCell.previewView;
                    DoubledLimitsBottomSheet doubledLimitsBottomSheet2 = DoubledLimitsBottomSheet.this;
                    limitPreviewView.gradientYOffset = doubledLimitsBottomSheet2.limits.get(i - doubledLimitsBottomSheet2.limitsStartRow).yOffset;
                    limitCell.previewView.gradientTotalHeight = DoubledLimitsBottomSheet.this.totalGradientHeight;
                }
            }

            public int getItemCount() {
                return DoubledLimitsBottomSheet.this.rowCount;
            }

            public int getItemViewType(int i) {
                DoubledLimitsBottomSheet doubledLimitsBottomSheet = DoubledLimitsBottomSheet.this;
                if (i == doubledLimitsBottomSheet.headerRow) {
                    return 1;
                }
                return i == doubledLimitsBottomSheet.lastViewRow ? 2 : 0;
            }
        };
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.billingProductDetailsUpdated || i == NotificationCenter.premiumPromoUpdated) {
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount, this.selectedTier));
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            bindPremium(UserConfig.getInstance(this.currentAccount).isPremium());
        }
    }

    private class LimitCell extends LinearLayout {
        LimitPreviewView previewView;
        TextView subtitle;
        TextView title;

        public LimitCell(DoubledLimitsBottomSheet doubledLimitsBottomSheet, Context context) {
            super(context);
            setOrientation(1);
            setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setTextSize(1, 15.0f);
            this.title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.title.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            addView(this.title, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 0, 16, 0));
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.subtitle.setTextSize(1, 14.0f);
            addView(this.subtitle, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 1, 16, 0));
            LimitPreviewView limitPreviewView = new LimitPreviewView(context, 0, 10, 20);
            this.previewView = limitPreviewView;
            addView(limitPreviewView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 8, 0, 21));
        }

        @SuppressLint({"SetTextI18n"})
        public void setData(Limit limit) {
            this.title.setText(limit.title);
            this.subtitle.setText(limit.subtitle);
            this.previewView.premiumCount.setText(Integer.toString(limit.premiumLimit));
            this.previewView.defaultCount.setText(Integer.toString(limit.defaultLimit));
        }
    }

    private void measureGradient(int i, int i2) {
        LimitCell limitCell = new LimitCell(this, getContext());
        int i3 = 0;
        for (int i4 = 0; i4 < this.limits.size(); i4++) {
            limitCell.setData(this.limits.get(i4));
            limitCell.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.limits.get(i4).yOffset = i3;
            i3 += limitCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    private static class Limit {
        final int defaultLimit;
        final int premiumLimit;
        final String subtitle;
        final String title;
        public int yOffset;

        private Limit(String str, String str2, int i, int i2) {
            this.title = str;
            this.subtitle = str2;
            this.defaultLimit = i;
            this.premiumLimit = i2;
        }
    }
}
