package org.telegram.ui.Components.Premium;

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
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;

public class DoubledLimitsBottomSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private BaseFragment baseFragment;
    private View divider;
    PremiumGradient.GradientTools gradientTools;
    int headerRow;
    int lastViewRow;
    final ArrayList<Limit> limits;
    int limitsStartEnd;
    int limitsStartRow;
    PremiumButtonView premiumButtonView;
    PremiumPreviewFragment premiumPreviewFragment;
    int rowCount = 0;
    ImageView titleImage;
    FrameLayout titleLayout;
    float titleProgress;
    TextView titleView;
    /* access modifiers changed from: private */
    public int totalGradientHeight;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DoubledLimitsBottomSheet(org.telegram.ui.ActionBar.BaseFragment r26, int r27) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = 0
            r0.<init>(r1, r2, r2)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.limits = r3
            r0.baseFragment = r1
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            java.lang.String r5 = "premiumGradient1"
            java.lang.String r6 = "premiumGradient2"
            java.lang.String r7 = "premiumGradient3"
            java.lang.String r8 = "premiumGradient4"
            r4.<init>(r5, r6, r7, r8)
            r0.gradientTools = r4
            r5 = 0
            r4.x1 = r5
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = r0.gradientTools
            r4.y1 = r5
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = r0.gradientTools
            r4.x2 = r5
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = r0.gradientTools
            r5 = 1065353216(0x3var_, float:1.0)
            r4.y2 = r5
            r4 = 1
            r0.clipToActionBar = r4
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r27)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r12 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r6 = "GroupsAndChannelsLimitTitle"
            r7 = 2131626116(0x7f0e0884, float:1.887946E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r6, r7)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            int r8 = r5.channelsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6[r2] = r8
            java.lang.String r8 = "GroupsAndChannelsLimitSubtitle"
            r9 = 2131626115(0x7f0e0883, float:1.8879457E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r9, r6)
            int r9 = r5.channelsLimitDefault
            int r10 = r5.channelsLimitPremium
            r11 = 0
            r6 = r12
            r6.<init>(r7, r8, r9, r10)
            r3.add(r12)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "PinChatsLimitTitle"
            r8 = 2131627527(0x7f0e0e07, float:1.888232E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersPinnedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "PinChatsLimitSubtitle"
            r9 = 2131627526(0x7f0e0e06, float:1.8882319E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.dialogFiltersPinnedLimitDefault
            int r8 = r5.dialogFiltersPinnedLimitPremium
            r18 = 0
            r13 = r6
            r16 = r7
            r17 = r8
            r13.<init>(r14, r15, r16, r17)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "PublicLinksLimitTitle"
            r8 = 2131627767(0x7f0e0ef7, float:1.8882808E38)
            java.lang.String r20 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.publicLinksLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "PublicLinksLimitSubtitle"
            r9 = 2131627766(0x7f0e0ef6, float:1.8882806E38)
            java.lang.String r21 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.publicLinksLimitDefault
            int r8 = r5.publicLinksLimitPremium
            r24 = 0
            r19 = r6
            r22 = r7
            r23 = r8
            r19.<init>(r20, r21, r22, r23)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "SavedGifsLimitTitle"
            r8 = 2131628076(0x7f0e102c, float:1.8883434E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.savedGifsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "SavedGifsLimitSubtitle"
            r9 = 2131628075(0x7f0e102b, float:1.8883432E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.savedGifsLimitDefault
            int r13 = r5.savedGifsLimitPremium
            r14 = 0
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "FavoriteStickersLimitTitle"
            r8 = 2131625798(0x7f0e0746, float:1.8878814E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "FavoriteStickersLimitSubtitle"
            r9 = 2131625797(0x7f0e0745, float:1.8878812E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.stickersFavedLimitDefault
            int r8 = r5.stickersFavedLimitPremium
            r20 = 0
            r15 = r6
            r18 = r7
            r19 = r8
            r15.<init>(r16, r17, r18, r19)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "BioLimitTitle"
            r8 = 2131624674(0x7f0e02e2, float:1.8876534E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "BioLimitSubtitle"
            r9 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.aboutLengthLimitDefault
            int r13 = r5.aboutLengthLimitPremium
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "CaptionsLimitTitle"
            r8 = 2131624845(0x7f0e038d, float:1.8876881E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "CaptionsLimitSubtitle"
            r9 = 2131624844(0x7f0e038c, float:1.887688E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.captionLengthLimitDefault
            int r8 = r5.captionLengthLimitPremium
            r15 = r6
            r18 = r7
            r19 = r8
            r15.<init>(r16, r17, r18, r19)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "FoldersLimitTitle"
            r8 = 2131625912(0x7f0e07b8, float:1.8879045E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "FoldersLimitSubtitle"
            r9 = 2131625911(0x7f0e07b7, float:1.8879043E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.dialogFiltersLimitDefault
            int r13 = r5.dialogFiltersLimitPremium
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "ChatPerFolderLimitTitle"
            r8 = 2131625025(0x7f0e0441, float:1.8877246E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersChatsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "ChatPerFolderLimitSubtitle"
            r9 = 2131625024(0x7f0e0440, float:1.8877244E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.dialogFiltersChatsLimitDefault
            int r8 = r5.dialogFiltersChatsLimitPremium
            r15 = r6
            r18 = r7
            r19 = r8
            r15.<init>(r16, r17, r18, r19)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "ConnectedAccountsLimitTitle"
            r8 = 2131625224(0x7f0e0508, float:1.887765E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            r8 = 4
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "ConnectedAccountsLimitSubtitle"
            r9 = 2131625223(0x7f0e0507, float:1.8877648E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            r12 = 3
            r13 = 4
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            r0.rowCount = r2
            r6 = 0
            int r6 = r6 + r4
            r0.rowCount = r6
            r0.headerRow = r2
            r0.limitsStartRow = r6
            int r3 = r3.size()
            int r6 = r6 + r3
            r0.rowCount = r6
            r0.limitsStartEnd = r6
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            android.content.Context r6 = r25.getContext()
            r3.<init>(r6)
            r0.titleLayout = r3
            android.widget.TextView r3 = new android.widget.TextView
            android.content.Context r6 = r25.getContext()
            r3.<init>(r6)
            r0.titleView = r3
            java.lang.String r6 = "DoubledLimits"
            r7 = 2131625531(0x7f0e063b, float:1.8878273E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
            r3.setText(r6)
            android.widget.TextView r3 = r0.titleView
            r6 = 17
            r3.setGravity(r6)
            android.widget.TextView r3 = r0.titleView
            r6 = 1101004800(0x41a00000, float:20.0)
            r3.setTextSize(r4, r6)
            android.widget.TextView r3 = r0.titleView
            java.lang.String r6 = "windowBackgroundWhiteBlackText"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setTextColor(r6)
            android.widget.TextView r3 = r0.titleView
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r3.setTypeface(r6)
            android.widget.FrameLayout r3 = r0.titleLayout
            android.widget.TextView r6 = r0.titleView
            r7 = -2
            r8 = 16
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r7, (int) r8)
            r3.addView(r6, r7)
            android.widget.ImageView r3 = new android.widget.ImageView
            android.content.Context r6 = r25.getContext()
            r3.<init>(r6)
            r0.titleImage = r3
            org.telegram.ui.Components.Premium.PremiumGradient r6 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.content.Context r7 = r25.getContext()
            r9 = 2131166023(0x7var_, float:1.794628E38)
            android.graphics.drawable.Drawable r7 = androidx.core.content.ContextCompat.getDrawable(r7, r9)
            org.telegram.ui.Components.Premium.PremiumGradient$InternalDrawable r6 = r6.createGradientDrawable(r7)
            r3.setImageDrawable(r6)
            android.widget.FrameLayout r3 = r0.titleLayout
            android.widget.ImageView r6 = r0.titleImage
            r7 = 40
            r9 = 28
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r9, (int) r8)
            r3.addView(r6, r7)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r6 = r0.titleLayout
            r7 = -1
            r8 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
            r3.addView(r6, r7)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1 r3 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1
            android.content.Context r6 = r25.getContext()
            r3.<init>(r6)
            r0.divider = r3
            java.lang.String r6 = "dialogBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setBackgroundColor(r6)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r6 = r0.divider
            r7 = -1
            r8 = 1116733440(0x42900000, float:72.0)
            r9 = 80
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r3.addView(r6, r7)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = new org.telegram.ui.Components.Premium.PremiumButtonView
            android.content.Context r6 = r25.getContext()
            r3.<init>(r6, r4)
            r0.premiumButtonView = r3
            android.widget.TextView r3 = r3.buttonTextView
            java.lang.String r4 = org.telegram.ui.PremiumPreviewFragment.getPremiumButtonText(r27)
            r3.setText(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.premiumButtonView
            r6 = -1
            r7 = 1111490560(0x42400000, float:48.0)
            r8 = 80
            r9 = 1098907648(0x41800000, float:16.0)
            r11 = 1098907648(0x41800000, float:16.0)
            r12 = 1094713344(0x41400000, float:12.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r3.addView(r4, r6)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = r0.premiumButtonView
            android.widget.FrameLayout r3 = r3.buttonLayout
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1
            r6 = r27
            r4.<init>(r0, r6, r1)
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = r0.premiumButtonView
            android.widget.TextView r3 = r3.overlayTextView
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0
            r4.<init>(r0)
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.recyclerListView
            r4 = 1116733440(0x42900000, float:72.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setPadding(r2, r2, r2, r4)
            int r2 = r25.getCurrentAccount()
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            boolean r2 = r2.isPremium()
            r0.bindPremium(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.<init>(org.telegram.ui.ActionBar.BaseFragment, int):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Premium-DoubledLimitsBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1230x68CLASSNAMEb8b(int currentAccount, BaseFragment fragment, View view) {
        if (!UserConfig.getInstance(currentAccount).isPremium()) {
            PremiumPreviewFragment.buyPremium(fragment, "double_limits");
        }
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-DoubledLimitsBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1231x233bfc0c(View v) {
        dismiss();
    }

    private void bindPremium(boolean hasPremium) {
        if (hasPremium) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", NUM), false, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
        measureGradient(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onPreDraw(Canvas canvas, int top, float progressToFullView) {
        float minTop = ((float) AndroidUtilities.statusBarHeight) + (((float) ((this.actionBar.getMeasuredHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(40.0f))) / 2.0f);
        float fromIconX = ((float) (((this.titleLayout.getMeasuredWidth() - this.titleView.getMeasuredWidth()) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f))) / 2.0f;
        float toIconX = (float) ((AndroidUtilities.dp(72.0f) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f));
        float fromX = ((float) this.titleImage.getMeasuredWidth()) + fromIconX + ((float) AndroidUtilities.dp(6.0f));
        float toX = (float) AndroidUtilities.dp(72.0f);
        float fromY = Math.max((float) (AndroidUtilities.dp(24.0f) + top), minTop);
        float toY = minTop;
        if (progressToFullView > 0.0f) {
            float f = this.titleProgress;
            if (f != 1.0f) {
                float f2 = f + 0.10666667f;
                this.titleProgress = f2;
                if (f2 > 1.0f) {
                    this.titleProgress = 1.0f;
                }
                this.containerView.invalidate();
                FrameLayout frameLayout = this.titleLayout;
                float f3 = this.titleProgress;
                frameLayout.setTranslationY(((1.0f - f3) * fromY) + (f3 * toY));
                TextView textView = this.titleView;
                float f4 = this.titleProgress;
                textView.setTranslationX(((1.0f - f4) * fromX) + (f4 * toX));
                ImageView imageView = this.titleImage;
                float f5 = this.titleProgress;
                imageView.setTranslationX(((1.0f - f5) * fromIconX) + (f5 * toIconX));
                this.titleImage.setAlpha(1.0f - this.titleProgress);
                float s = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
                this.titleImage.setScaleX(s);
                this.titleImage.setScaleY(s);
            }
        }
        if (progressToFullView == 0.0f) {
            float f6 = this.titleProgress;
            if (f6 != 0.0f) {
                float f7 = f6 - 0.10666667f;
                this.titleProgress = f7;
                if (f7 < 0.0f) {
                    this.titleProgress = 0.0f;
                }
                this.containerView.invalidate();
            }
        }
        FrameLayout frameLayout2 = this.titleLayout;
        float var_ = this.titleProgress;
        frameLayout2.setTranslationY(((1.0f - var_) * fromY) + (var_ * toY));
        TextView textView2 = this.titleView;
        float var_ = this.titleProgress;
        textView2.setTranslationX(((1.0f - var_) * fromX) + (var_ * toX));
        ImageView imageView2 = this.titleImage;
        float var_ = this.titleProgress;
        imageView2.setTranslationX(((1.0f - var_) * fromIconX) + (var_ * toIconX));
        this.titleImage.setAlpha(1.0f - this.titleProgress);
        float s2 = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
        this.titleImage.setScaleX(s2);
        this.titleImage.setScaleY(s2);
    }

    /* access modifiers changed from: protected */
    public CharSequence getTitle() {
        return null;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return false;
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.ui.Cells.FixedHeightEmptyCell} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
                /*
                    r5 = this;
                    android.content.Context r0 = r6.getContext()
                    switch(r7) {
                        case 1: goto L_0x002c;
                        case 2: goto L_0x0024;
                        default: goto L_0x0007;
                    }
                L_0x0007:
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell r1 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$LimitCell
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r2 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    r1.<init>(r0)
                    org.telegram.ui.Components.Premium.LimitPreviewView r2 = r1.previewView
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r3 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    android.view.ViewGroup r3 = r3.containerView
                    r2.setParentViewForGradien(r3)
                    org.telegram.ui.Components.Premium.LimitPreviewView r2 = r1.previewView
                    org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet r3 = org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.this
                    org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r3 = r3.gradientTools
                    r2.setStaticGradinet(r3)
                    r2 = r1
                    goto L_0x0034
                L_0x0024:
                    org.telegram.ui.Cells.FixedHeightEmptyCell r1 = new org.telegram.ui.Cells.FixedHeightEmptyCell
                    r2 = 16
                    r1.<init>(r0, r2)
                    goto L_0x0034
                L_0x002c:
                    org.telegram.ui.Cells.FixedHeightEmptyCell r1 = new org.telegram.ui.Cells.FixedHeightEmptyCell
                    r2 = 64
                    r1.<init>(r0, r2)
                L_0x0034:
                    androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                    r3 = -1
                    r4 = -2
                    r2.<init>((int) r3, (int) r4)
                    r1.setLayoutParams(r2)
                    org.telegram.ui.Components.RecyclerListView$Holder r2 = new org.telegram.ui.Components.RecyclerListView$Holder
                    r2.<init>(r1)
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.AnonymousClass2.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder.getItemViewType() == 0) {
                    LimitCell limitCell = (LimitCell) holder.itemView;
                    limitCell.setData(DoubledLimitsBottomSheet.this.limits.get(position - DoubledLimitsBottomSheet.this.limitsStartRow));
                    limitCell.previewView.gradientYOffset = DoubledLimitsBottomSheet.this.limits.get(position - DoubledLimitsBottomSheet.this.limitsStartRow).yOffset;
                    limitCell.previewView.gradientTotalHeight = DoubledLimitsBottomSheet.this.totalGradientHeight;
                }
            }

            public int getItemCount() {
                return DoubledLimitsBottomSheet.this.rowCount;
            }

            public int getItemViewType(int position) {
                if (position == DoubledLimitsBottomSheet.this.headerRow) {
                    return 1;
                }
                if (position == DoubledLimitsBottomSheet.this.lastViewRow) {
                    return 2;
                }
                return 0;
            }
        };
    }

    public void setParentFragment(PremiumPreviewFragment premiumPreviewFragment2) {
        this.premiumPreviewFragment = premiumPreviewFragment2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.billingProductDetailsUpdated || id == NotificationCenter.premiumPromoUpdated) {
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount));
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            bindPremium(UserConfig.getInstance(this.currentAccount).isPremium());
        }
    }

    private class LimitCell extends LinearLayout {
        LimitPreviewView previewView;
        TextView subtitle;
        TextView title;

        public LimitCell(Context context) {
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

        public void setData(Limit limit) {
            this.title.setText(limit.title);
            this.subtitle.setText(limit.subtitle);
            this.previewView.premiumCount.setText(Integer.toString(limit.premiumLimit));
            this.previewView.defaultCount.setText(Integer.toString(limit.defaultLimit));
        }
    }

    private void measureGradient(int w, int h) {
        int yOffset = 0;
        LimitCell dummyCell = new LimitCell(getContext());
        for (int i = 0; i < this.limits.size(); i++) {
            dummyCell.setData(this.limits.get(i));
            dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.limits.get(i).yOffset = yOffset;
            yOffset += dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
    }

    private static class Limit {
        final int current;
        final int defaultLimit;
        final int premiumLimit;
        final String subtitle;
        final String title;
        public int yOffset;

        private Limit(String title2, String subtitle2, int defaultLimit2, int premiumLimit2) {
            this.current = -1;
            this.title = title2;
            this.subtitle = subtitle2;
            this.defaultLimit = defaultLimit2;
            this.premiumLimit = premiumLimit2;
        }
    }
}
