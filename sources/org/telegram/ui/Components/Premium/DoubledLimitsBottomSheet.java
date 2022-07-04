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
    int rowCount = 0;
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
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            java.lang.String r5 = "premiumGradient1"
            java.lang.String r6 = "premiumGradient2"
            java.lang.String r7 = "premiumGradient3"
            java.lang.String r8 = "premiumGradient4"
            r4.<init>(r5, r6, r7, r8)
            r0.gradientTools = r4
            r5 = 0
            r4.x1 = r5
            r4.y1 = r5
            r4.x2 = r5
            r5 = 1065353216(0x3var_, float:1.0)
            r4.y2 = r5
            r4 = 1
            r0.clipToActionBar = r4
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r27)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r12 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r6 = "GroupsAndChannelsLimitTitle"
            r7 = 2131626113(0x7f0e0881, float:1.8879453E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r6, r7)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            int r8 = r5.channelsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6[r2] = r8
            java.lang.String r8 = "GroupsAndChannelsLimitSubtitle"
            r9 = 2131626112(0x7f0e0880, float:1.887945E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r9, r6)
            int r9 = r5.channelsLimitDefault
            int r10 = r5.channelsLimitPremium
            r11 = 0
            r6 = r12
            r6.<init>(r7, r8, r9, r10)
            r3.add(r12)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "PinChatsLimitTitle"
            r8 = 2131627524(0x7f0e0e04, float:1.8882315E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersPinnedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "PinChatsLimitSubtitle"
            r9 = 2131627523(0x7f0e0e03, float:1.8882313E38)
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
            r8 = 2131627764(0x7f0e0ef4, float:1.8882802E38)
            java.lang.String r20 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.publicLinksLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "PublicLinksLimitSubtitle"
            r9 = 2131627763(0x7f0e0ef3, float:1.88828E38)
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
            r8 = 2131628073(0x7f0e1029, float:1.8883428E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.savedGifsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "SavedGifsLimitSubtitle"
            r9 = 2131628072(0x7f0e1028, float:1.8883426E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.savedGifsLimitDefault
            int r13 = r5.savedGifsLimitPremium
            r14 = 0
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "FavoriteStickersLimitTitle"
            r8 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "FavoriteStickersLimitSubtitle"
            r9 = 2131625795(0x7f0e0743, float:1.8878808E38)
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
            r8 = 2131624672(0x7f0e02e0, float:1.887653E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "BioLimitSubtitle"
            r9 = 2131624671(0x7f0e02df, float:1.8876528E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.aboutLengthLimitDefault
            int r13 = r5.aboutLengthLimitPremium
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "CaptionsLimitTitle"
            r8 = 2131624843(0x7f0e038b, float:1.8876877E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.stickersFavedLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "CaptionsLimitSubtitle"
            r9 = 2131624842(0x7f0e038a, float:1.8876875E38)
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
            r8 = 2131625909(0x7f0e07b5, float:1.887904E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "FoldersLimitSubtitle"
            r9 = 2131625908(0x7f0e07b4, float:1.8879037E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r12 = r5.dialogFiltersLimitDefault
            int r13 = r5.dialogFiltersLimitPremium
            r9 = r6
            r9.<init>(r10, r11, r12, r13)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r6 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r7 = "ChatPerFolderLimitTitle"
            r8 = 2131625023(0x7f0e043f, float:1.8877242E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r7, r8)
            java.lang.Object[] r7 = new java.lang.Object[r4]
            int r8 = r5.dialogFiltersChatsLimitPremium
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r7[r2] = r8
            java.lang.String r8 = "ChatPerFolderLimitSubtitle"
            r9 = 2131625022(0x7f0e043e, float:1.887724E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.formatString(r8, r9, r7)
            int r7 = r5.dialogFiltersChatsLimitDefault
            int r5 = r5.dialogFiltersChatsLimitPremium
            r15 = r6
            r18 = r7
            r19 = r5
            r15.<init>(r16, r17, r18, r19)
            r3.add(r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit r5 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$Limit
            java.lang.String r6 = "ConnectedAccountsLimitTitle"
            r7 = 2131625222(0x7f0e0506, float:1.8877646E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r7)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            r7 = 4
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6[r2] = r7
            java.lang.String r7 = "ConnectedAccountsLimitSubtitle"
            r8 = 2131625221(0x7f0e0505, float:1.8877644E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r7, r8, r6)
            r11 = 3
            r12 = 4
            r13 = 0
            r8 = r5
            r8.<init>(r9, r10, r11, r12)
            r3.add(r5)
            r0.rowCount = r2
            r5 = 0
            int r5 = r5 + r4
            r0.rowCount = r5
            r0.headerRow = r2
            r0.limitsStartRow = r5
            int r3 = r3.size()
            int r5 = r5 + r3
            r0.rowCount = r5
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            android.content.Context r5 = r25.getContext()
            r3.<init>(r5)
            r0.titleLayout = r3
            android.widget.TextView r3 = new android.widget.TextView
            android.content.Context r5 = r25.getContext()
            r3.<init>(r5)
            r0.titleView = r3
            java.lang.String r5 = "DoubledLimits"
            r6 = 2131625529(0x7f0e0639, float:1.8878269E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)
            r3.setText(r5)
            android.widget.TextView r3 = r0.titleView
            r5 = 17
            r3.setGravity(r5)
            android.widget.TextView r3 = r0.titleView
            r5 = 1101004800(0x41a00000, float:20.0)
            r3.setTextSize(r4, r5)
            android.widget.TextView r3 = r0.titleView
            java.lang.String r5 = "windowBackgroundWhiteBlackText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setTextColor(r5)
            android.widget.TextView r3 = r0.titleView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r3.setTypeface(r5)
            android.widget.FrameLayout r3 = r0.titleLayout
            android.widget.TextView r5 = r0.titleView
            r6 = -2
            r7 = 16
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r7)
            r3.addView(r5, r6)
            android.widget.ImageView r3 = new android.widget.ImageView
            android.content.Context r5 = r25.getContext()
            r3.<init>(r5)
            r0.titleImage = r3
            org.telegram.ui.Components.Premium.PremiumGradient r5 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
            android.content.Context r6 = r25.getContext()
            r8 = 2131166022(0x7var_, float:1.7946278E38)
            android.graphics.drawable.Drawable r6 = androidx.core.content.ContextCompat.getDrawable(r6, r8)
            org.telegram.ui.Components.Premium.PremiumGradient$InternalDrawable r5 = r5.createGradientDrawable(r6)
            r3.setImageDrawable(r5)
            android.widget.FrameLayout r3 = r0.titleLayout
            android.widget.ImageView r5 = r0.titleImage
            r6 = 40
            r8 = 28
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r8, (int) r7)
            r3.addView(r5, r6)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r5 = r0.titleLayout
            r6 = -1
            r7 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7)
            r3.addView(r5, r6)
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1 r3 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$1
            android.content.Context r5 = r25.getContext()
            r3.<init>(r0, r5)
            r0.divider = r3
            java.lang.String r5 = "dialogBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setBackgroundColor(r5)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r5 = r0.divider
            r6 = -1
            r7 = 1116733440(0x42900000, float:72.0)
            r8 = 80
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r3.addView(r5, r6)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = new org.telegram.ui.Components.Premium.PremiumButtonView
            android.content.Context r5 = r25.getContext()
            r3.<init>(r5, r4)
            r0.premiumButtonView = r3
            android.widget.TextView r3 = r3.buttonTextView
            java.lang.String r4 = org.telegram.ui.PremiumPreviewFragment.getPremiumButtonText(r27)
            r3.setText(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.Premium.PremiumButtonView r4 = r0.premiumButtonView
            r5 = -1
            r6 = 1111490560(0x42400000, float:48.0)
            r7 = 80
            r8 = 1098907648(0x41800000, float:16.0)
            r10 = 1098907648(0x41800000, float:16.0)
            r11 = 1094713344(0x41400000, float:12.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r3.addView(r4, r5)
            org.telegram.ui.Components.Premium.PremiumButtonView r3 = r0.premiumButtonView
            android.widget.FrameLayout r3 = r3.buttonLayout
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1 r4 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1
            r5 = r27
            r4.<init>(r0, r5, r1)
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView
            android.widget.TextView r1 = r1.overlayTextView
            org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.recyclerListView
            r3 = 1116733440(0x42900000, float:72.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r2, r2, r2, r3)
            int r1 = r25.getCurrentAccount()
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            boolean r1 = r1.isPremium()
            r0.bindPremium(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.<init>(org.telegram.ui.ActionBar.BaseFragment, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, BaseFragment baseFragment, View view) {
        if (!UserConfig.getInstance(i).isPremium()) {
            PremiumPreviewFragment.buyPremium(baseFragment, "double_limits");
        }
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        dismiss();
    }

    private void bindPremium(boolean z) {
        if (z) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", NUM), false, false);
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
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount));
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
