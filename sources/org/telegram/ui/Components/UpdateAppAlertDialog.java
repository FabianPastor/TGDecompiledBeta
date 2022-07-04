package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;

public class UpdateAppAlertDialog extends BottomSheet {
    private int accountNum;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    private TLRPC.TL_help_appUpdate appUpdate;
    private boolean ignoreLayout;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    private int[] location = new int[2];
    private TextView messageTextView;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private NestedScrollView scrollView;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private TextView textView;

    public class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private boolean hasBackground;
        /* access modifiers changed from: private */
        public TextView[] textView = new TextView[2];
        final /* synthetic */ UpdateAppAlertDialog this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public BottomSheetCell(org.telegram.ui.Components.UpdateAppAlertDialog r17, android.content.Context r18, boolean r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                r2 = r17
                r0.this$0 = r2
                r0.<init>(r1)
                r3 = 2
                android.widget.TextView[] r4 = new android.widget.TextView[r3]
                r0.textView = r4
                r4 = r19 ^ 1
                r0.hasBackground = r4
                r4 = 0
                r0.setBackground(r4)
                android.view.View r4 = new android.view.View
                r4.<init>(r1)
                r0.background = r4
                boolean r5 = r0.hasBackground
                java.lang.String r6 = "featuredStickers_addButton"
                r7 = 0
                r8 = 1
                if (r5 == 0) goto L_0x0034
                float[] r5 = new float[r8]
                r9 = 1082130432(0x40800000, float:4.0)
                r5[r7] = r9
                android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((java.lang.String) r6, (float[]) r5)
                r4.setBackground(r5)
            L_0x0034:
                android.view.View r4 = r0.background
                r9 = -1
                r10 = -1082130432(0xffffffffbvar_, float:-1.0)
                r11 = 0
                r12 = 1098907648(0x41800000, float:16.0)
                r5 = 0
                if (r19 == 0) goto L_0x0041
                r13 = 0
                goto L_0x0043
            L_0x0041:
                r13 = 1098907648(0x41800000, float:16.0)
            L_0x0043:
                r14 = 1098907648(0x41800000, float:16.0)
                r15 = 1098907648(0x41800000, float:16.0)
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r9)
                r4 = 0
            L_0x004f:
                if (r4 >= r3) goto L_0x00e0
                android.widget.TextView[] r9 = r0.textView
                android.widget.TextView r10 = new android.widget.TextView
                r10.<init>(r1)
                r9[r4] = r10
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setLines(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setSingleLine(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setGravity(r8)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
                r9.setEllipsize(r10)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r10 = 17
                r9.setGravity(r10)
                boolean r9 = r0.hasBackground
                if (r9 == 0) goto L_0x00a0
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                java.lang.String r11 = "featuredStickers_buttonText"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r9.setTextColor(r11)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r11)
                r9.setTypeface(r11)
                goto L_0x00ab
            L_0x00a0:
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r9.setTextColor(r11)
            L_0x00ab:
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r11 = 1096810496(0x41600000, float:14.0)
                r9.setTextSize(r8, r11)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                boolean r11 = r0.hasBackground
                if (r11 == 0) goto L_0x00be
                r11 = 0
                goto L_0x00c4
            L_0x00be:
                r11 = 1095761920(0x41500000, float:13.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            L_0x00c4:
                r9.setPadding(r7, r7, r7, r11)
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r11 = -2
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r10)
                r0.addView(r9, r10)
                if (r4 != r8) goto L_0x00dc
                android.widget.TextView[] r9 = r0.textView
                r9 = r9[r4]
                r9.setAlpha(r5)
            L_0x00dc:
                int r4 = r4 + 1
                goto L_0x004f
            L_0x00e0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UpdateAppAlertDialog.BottomSheetCell.<init>(org.telegram.ui.Components.UpdateAppAlertDialog, android.content.Context, boolean):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), NUM));
        }

        public void setText(CharSequence text, boolean animated) {
            if (!animated) {
                this.textView[0].setText(text);
                return;
            }
            this.textView[1].setText(text);
            boolean unused = this.this$0.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, new float[]{0.0f, (float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(10.0f), 0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    boolean unused = BottomSheetCell.this.this$0.animationInProgress = false;
                    TextView temp = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = temp;
                }
            });
            animatorSet.start();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public UpdateAppAlertDialog(android.content.Context r29, org.telegram.tgnet.TLRPC.TL_help_appUpdate r30, int r31) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = 0
            r0.<init>(r1, r3)
            r4 = 2
            int[] r5 = new int[r4]
            r0.location = r5
            r0.appUpdate = r2
            r5 = r31
            r0.accountNum = r5
            r0.setCanceledOnTouchOutside(r3)
            r0.setApplyTopPadding(r3)
            r0.setApplyBottomPadding(r3)
            android.content.res.Resources r6 = r29.getResources()
            r7 = 2131166138(0x7var_ba, float:1.7946513E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "dialogBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r6.setColorFilter(r7)
            org.telegram.ui.Components.UpdateAppAlertDialog$1 r6 = new org.telegram.ui.Components.UpdateAppAlertDialog$1
            r6.<init>(r1)
            r6.setWillNotDraw(r3)
            r0.containerView = r6
            org.telegram.ui.Components.UpdateAppAlertDialog$2 r7 = new org.telegram.ui.Components.UpdateAppAlertDialog$2
            r7.<init>(r1)
            r0.scrollView = r7
            r8 = 1
            r7.setFillViewport(r8)
            androidx.core.widget.NestedScrollView r7 = r0.scrollView
            r7.setWillNotDraw(r3)
            androidx.core.widget.NestedScrollView r7 = r0.scrollView
            r7.setClipToPadding(r3)
            androidx.core.widget.NestedScrollView r7 = r0.scrollView
            r7.setVerticalScrollBarEnabled(r3)
            androidx.core.widget.NestedScrollView r7 = r0.scrollView
            r9 = -1
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = 51
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r6.addView(r7, r9)
            android.widget.LinearLayout r7 = new android.widget.LinearLayout
            r7.<init>(r1)
            r0.linearLayout = r7
            r7.setOrientation(r8)
            androidx.core.widget.NestedScrollView r7 = r0.scrollView
            android.widget.LinearLayout r9 = r0.linearLayout
            r10 = -1
            r11 = -2
            r12 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createScroll(r10, r11, r12)
            r7.addView((android.view.View) r9, (android.view.ViewGroup.LayoutParams) r11)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r7 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r7 = r7.sticker
            if (r7 == 0) goto L_0x0100
            org.telegram.ui.Components.BackupImageView r7 = new org.telegram.ui.Components.BackupImageView
            r7.<init>(r1)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r9 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r9 = r9.sticker
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.thumbs
            r11 = 1065353216(0x3var_, float:1.0)
            java.lang.String r13 = "windowBackgroundGray"
            org.telegram.messenger.SvgHelper$SvgDrawable r9 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize>) r9, (java.lang.String) r13, (float) r11)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r11 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r11 = r11.sticker
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.thumbs
            r13 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r11 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r13)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r13 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r13 = r13.sticker
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r11, (org.telegram.tgnet.TLRPC.Document) r13)
            if (r9 == 0) goto L_0x00d3
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r13 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r13 = r13.sticker
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForDocument(r13)
            r17 = 0
            java.lang.String r15 = "250_250"
            java.lang.String r18 = "update"
            r13 = r7
            r16 = r9
            r13.setImage((org.telegram.messenger.ImageLocation) r14, (java.lang.String) r15, (android.graphics.drawable.Drawable) r16, (int) r17, (java.lang.Object) r18)
            goto L_0x00e9
        L_0x00d3:
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r13 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r13 = r13.sticker
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForDocument(r13)
            r17 = 0
            r18 = 0
            java.lang.String r15 = "250_250"
            java.lang.String r19 = "update"
            r13 = r7
            r16 = r20
            r13.setImage((org.telegram.messenger.ImageLocation) r14, (java.lang.String) r15, (org.telegram.messenger.ImageLocation) r16, (java.lang.String) r17, (int) r18, (java.lang.Object) r19)
        L_0x00e9:
            android.widget.LinearLayout r13 = r0.linearLayout
            r21 = 160(0xa0, float:2.24E-43)
            r22 = 160(0xa0, float:2.24E-43)
            r23 = 49
            r24 = 17
            r25 = 8
            r26 = 17
            r27 = 0
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
            r13.addView(r7, r14)
        L_0x0100:
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r7.setTypeface(r9)
            r9 = 1101004800(0x41a00000, float:20.0)
            r7.setTextSize(r8, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setTextColor(r11)
            r7.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r11)
            r11 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.String r13 = "AppUpdate"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r7.setText(r11)
            android.widget.LinearLayout r11 = r0.linearLayout
            r13 = -2
            r14 = -2
            r15 = 49
            r16 = 23
            r17 = 16
            r18 = 23
            r19 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r11.addView(r7, r13)
            android.widget.TextView r11 = new android.widget.TextView
            android.content.Context r13 = r28.getContext()
            r11.<init>(r13)
            java.lang.String r13 = "dialogTextGray3"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r11.setTextColor(r13)
            r13 = 1096810496(0x41600000, float:14.0)
            r11.setTextSize(r8, r13)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r14 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r14.<init>()
            r11.setMovementMethod(r14)
            java.lang.String r14 = "dialogTextLink"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r11.setLinkTextColor(r15)
            r15 = 2131624383(0x7f0e01bf, float:1.8875944E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = r0.appUpdate
            java.lang.String r10 = r10.version
            r4[r3] = r10
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            long r12 = r10.size
            java.lang.String r10 = org.telegram.messenger.AndroidUtilities.formatFileSize(r12)
            r4[r8] = r10
            java.lang.String r10 = "AppUpdateVersionAndSize"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r15, r4)
            r11.setText(r4)
            r4 = 49
            r11.setGravity(r4)
            android.widget.LinearLayout r4 = r0.linearLayout
            r19 = -2
            r20 = -2
            r21 = 49
            r22 = 23
            r23 = 0
            r24 = 23
            r25 = 5
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r4.addView(r11, r10)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r10 = r28.getContext()
            r4.<init>(r10)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setTextColor(r9)
            r9 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r8, r9)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r9 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r9.<init>()
            r4.setMovementMethod(r9)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r4.setLinkTextColor(r9)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r9 = r0.appUpdate
            java.lang.String r9 = r9.text
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 == 0) goto L_0x01e8
            r9 = 2131624378(0x7f0e01ba, float:1.8875934E38)
            java.lang.String r10 = "AppUpdateChangelogEmpty"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            android.text.SpannableStringBuilder r9 = org.telegram.messenger.AndroidUtilities.replaceTags(r9)
            r4.setText(r9)
            goto L_0x0205
        L_0x01e8:
            android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = r0.appUpdate
            java.lang.String r10 = r10.text
            r9.<init>(r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r2.entities
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = 0
            r18 = r9
            r19 = r10
            org.telegram.messenger.MessageObject.addEntitiesToText(r18, r19, r20, r21, r22, r23)
            r4.setText(r9)
        L_0x0205:
            r9 = 51
            r4.setGravity(r9)
            android.widget.LinearLayout r9 = r0.linearLayout
            r17 = -2
            r18 = -2
            r19 = 51
            r20 = 23
            r21 = 15
            r22 = 23
            r23 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
            r9.addView(r4, r10)
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r10 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r12 = 83
            r13 = -1
            r9.<init>(r13, r10, r12)
            r10 = 1124204544(0x43020000, float:130.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.bottomMargin = r10
            android.view.View r10 = new android.view.View
            r10.<init>(r1)
            r0.shadow = r10
            java.lang.String r12 = "dialogShadowLine"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r10.setBackgroundColor(r12)
            android.view.View r10 = r0.shadow
            r12 = 0
            r10.setAlpha(r12)
            android.view.View r10 = r0.shadow
            java.lang.Integer r12 = java.lang.Integer.valueOf(r8)
            r10.setTag(r12)
            android.view.View r10 = r0.shadow
            r6.addView(r10, r9)
            org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell r10 = new org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell
            r10.<init>(r0, r1, r3)
            r12 = 2131624379(0x7f0e01bb, float:1.8875936E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r14 = "AppUpdateDownloadNow"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatString(r14, r12, r13)
            r10.setText(r12, r3)
            android.view.View r12 = r10.background
            org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda0
            r13.<init>(r0)
            r12.setOnClickListener(r13)
            r14 = -1
            r15 = 1112014848(0x42480000, float:50.0)
            r16 = 83
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r6.addView(r10, r12)
            org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell r12 = new org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell
            r12.<init>(r0, r1, r8)
            r8 = r12
            r12 = 2131624382(0x7f0e01be, float:1.8875942E38)
            java.lang.String r13 = "AppUpdateRemindMeLater"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r8.setText(r12, r3)
            android.view.View r3 = r8.background
            org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda1 r12 = new org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda1
            r12.<init>(r0)
            r3.setOnClickListener(r12)
            r13 = -1
            r14 = 1112014848(0x42480000, float:50.0)
            r15 = 83
            r16 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r6.addView(r8, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UpdateAppAlertDialog.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_help_appUpdate, int):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-UpdateAppAlertDialog  reason: not valid java name */
    public /* synthetic */ void m1542lambda$new$0$orgtelegramuiComponentsUpdateAppAlertDialog(View v) {
        FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-UpdateAppAlertDialog  reason: not valid java name */
    public /* synthetic */ void m1543lambda$new$1$orgtelegramuiComponentsUpdateAppAlertDialog(View v) {
        dismiss();
    }

    private void runShadowAnimation(int num, final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            UpdateAppAlertDialog.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = UpdateAppAlertDialog.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animation)) {
                        AnimatorSet unused = UpdateAppAlertDialog.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        this.linearLayout.getChildAt(0).getLocationInWindow(this.location);
        int newOffset = Math.max(this.location[1] - AndroidUtilities.dp(24.0f), 0);
        if (((float) (this.location[1] + this.linearLayout.getMeasuredHeight())) <= ((float) (this.container.getMeasuredHeight() - AndroidUtilities.dp(113.0f))) + this.containerView.getTranslationY()) {
            runShadowAnimation(0, false);
        } else {
            runShadowAnimation(0, true);
        }
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.scrollView.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }
}
