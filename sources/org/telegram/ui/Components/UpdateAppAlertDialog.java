package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.tgnet.TLRPC$TL_help_appUpdate;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class UpdateAppAlertDialog extends BottomSheet {
    private int accountNum;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    private TLRPC$TL_help_appUpdate appUpdate;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    private int[] location = new int[2];
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private NestedScrollView scrollView;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private boolean hasBackground;
        /* access modifiers changed from: private */
        public TextView[] textView = new TextView[2];

        public BottomSheetCell(Context context, boolean z) {
            super(context);
            this.hasBackground = !z;
            setBackground((Drawable) null);
            View view = new View(context);
            this.background = view;
            if (this.hasBackground) {
                view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            }
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, z ? 0.0f : 16.0f, 16.0f, 16.0f));
            for (int i = 0; i < 2; i++) {
                this.textView[i] = new TextView(context);
                this.textView[i].setLines(1);
                this.textView[i].setSingleLine(true);
                this.textView[i].setGravity(1);
                this.textView[i].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[i].setGravity(17);
                if (this.hasBackground) {
                    this.textView[i].setTextColor(Theme.getColor("featuredStickers_buttonText"));
                    this.textView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                } else {
                    this.textView[i].setTextColor(Theme.getColor("featuredStickers_addButton"));
                }
                this.textView[i].setTextSize(1, 14.0f);
                this.textView[i].setPadding(0, 0, 0, this.hasBackground ? 0 : AndroidUtilities.dp(13.0f));
                addView(this.textView[i], LayoutHelper.createFrame(-2, -2, 17));
                if (i == 1) {
                    this.textView[i].setAlpha(0.0f);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), NUM));
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (!z) {
                this.textView[0].setText(charSequence);
                return;
            }
            this.textView[1].setText(charSequence);
            boolean unused = UpdateAppAlertDialog.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, new float[]{0.0f, (float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(10.0f), 0.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = UpdateAppAlertDialog.this.animationInProgress = false;
                    TextView textView = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = textView;
                }
            });
            animatorSet.start();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public UpdateAppAlertDialog(android.content.Context r21, org.telegram.tgnet.TLRPC$TL_help_appUpdate r22, int r23) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = 0
            r0.<init>(r1, r3)
            r4 = 2
            int[] r5 = new int[r4]
            r0.location = r5
            r0.appUpdate = r2
            r5 = r23
            r0.accountNum = r5
            r0.setCanceledOnTouchOutside(r3)
            r0.setApplyTopPadding(r3)
            r0.setApplyBottomPadding(r3)
            android.content.res.Resources r5 = r21.getResources()
            r6 = 2131166096(0x7var_, float:1.7946428E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "dialogBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r7, r8)
            r5.setColorFilter(r6)
            org.telegram.ui.Components.UpdateAppAlertDialog$1 r5 = new org.telegram.ui.Components.UpdateAppAlertDialog$1
            r5.<init>(r1)
            r5.setWillNotDraw(r3)
            r0.containerView = r5
            org.telegram.ui.Components.UpdateAppAlertDialog$2 r6 = new org.telegram.ui.Components.UpdateAppAlertDialog$2
            r6.<init>(r1)
            r0.scrollView = r6
            r7 = 1
            r6.setFillViewport(r7)
            androidx.core.widget.NestedScrollView r6 = r0.scrollView
            r6.setWillNotDraw(r3)
            androidx.core.widget.NestedScrollView r6 = r0.scrollView
            r6.setClipToPadding(r3)
            androidx.core.widget.NestedScrollView r6 = r0.scrollView
            r6.setVerticalScrollBarEnabled(r3)
            androidx.core.widget.NestedScrollView r6 = r0.scrollView
            r8 = -1
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r10 = 51
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r5.addView(r6, r8)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r0.linearLayout = r6
            r6.setOrientation(r7)
            androidx.core.widget.NestedScrollView r6 = r0.scrollView
            android.widget.LinearLayout r8 = r0.linearLayout
            r9 = -1
            r10 = -2
            r11 = 51
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createScroll(r9, r10, r11)
            r6.addView((android.view.View) r8, (android.view.ViewGroup.LayoutParams) r10)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r6 = r6.sticker
            if (r6 == 0) goto L_0x00fd
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            r6.<init>(r1)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r8 = r8.sticker
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.thumbs
            r10 = 1065353216(0x3var_, float:1.0)
            java.lang.String r12 = "windowBackgroundGray"
            org.telegram.messenger.SvgHelper$SvgDrawable r15 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r8, (java.lang.String) r12, (float) r10)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r8 = r8.sticker
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.thumbs
            r10 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r10)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r10 = r10.sticker
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Document) r10)
            if (r15 == 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r8 = r8.sticker
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            r16 = 0
            java.lang.String r14 = "250_250"
            java.lang.String r17 = "update"
            r12 = r6
            r12.setImage((org.telegram.messenger.ImageLocation) r13, (java.lang.String) r14, (android.graphics.drawable.Drawable) r15, (int) r16, (java.lang.Object) r17)
            goto L_0x00e6
        L_0x00d1:
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r10 = r0.appUpdate
            org.telegram.tgnet.TLRPC$Document r10 = r10.sticker
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument(r10)
            r16 = 0
            r17 = 0
            java.lang.String r14 = "250_250"
            java.lang.String r18 = "update"
            r12 = r6
            r15 = r8
            r12.setImage((org.telegram.messenger.ImageLocation) r13, (java.lang.String) r14, (org.telegram.messenger.ImageLocation) r15, (java.lang.String) r16, (int) r17, (java.lang.Object) r18)
        L_0x00e6:
            android.widget.LinearLayout r8 = r0.linearLayout
            r12 = 160(0xa0, float:2.24E-43)
            r13 = 160(0xa0, float:2.24E-43)
            r14 = 49
            r15 = 17
            r16 = 8
            r17 = 17
            r18 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r8.addView(r6, r10)
        L_0x00fd:
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r6.setTypeface(r8)
            r8 = 1101004800(0x41a00000, float:20.0)
            r6.setTextSize(r7, r8)
            java.lang.String r8 = "dialogTextBlack"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTextColor(r10)
            r6.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r10)
            r10 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r12 = "AppUpdate"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r6.setText(r10)
            android.widget.LinearLayout r10 = r0.linearLayout
            r12 = -2
            r13 = -2
            r14 = 49
            r15 = 23
            r16 = 16
            r17 = 23
            r18 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r10.addView(r6, r12)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r10 = r20.getContext()
            r6.<init>(r10)
            java.lang.String r10 = "dialogTextGray3"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setTextColor(r10)
            r10 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r7, r10)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r12 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r12.<init>()
            r6.setMovementMethod(r12)
            java.lang.String r12 = "dialogTextLink"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setLinkTextColor(r13)
            r13 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r14 = r0.appUpdate
            java.lang.String r15 = r14.version
            r4[r3] = r15
            org.telegram.tgnet.TLRPC$Document r14 = r14.document
            int r14 = r14.size
            long r14 = (long) r14
            java.lang.String r14 = org.telegram.messenger.AndroidUtilities.formatFileSize(r14)
            r4[r7] = r14
            java.lang.String r14 = "AppUpdateVersionAndSize"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r14, r13, r4)
            r6.setText(r4)
            r4 = 49
            r6.setGravity(r4)
            android.widget.LinearLayout r4 = r0.linearLayout
            r13 = -2
            r14 = -2
            r15 = 49
            r16 = 23
            r17 = 0
            r18 = 23
            r19 = 5
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r4.addView(r6, r13)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r6 = r20.getContext()
            r4.<init>(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r4.setTextColor(r6)
            r4.setTextSize(r7, r10)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r6 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r6.<init>()
            r4.setMovementMethod(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r4.setLinkTextColor(r6)
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r6 = r0.appUpdate
            java.lang.String r6 = r6.text
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x01e0
            r2 = 2131624307(0x7f0e0173, float:1.887579E38)
            java.lang.String r6 = "AppUpdateChangelogEmpty"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r4.setText(r2)
            goto L_0x01f8
        L_0x01e0:
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r8 = r0.appUpdate
            java.lang.String r8 = r8.text
            r6.<init>(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r2.entities
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r12 = r6
            org.telegram.messenger.MessageObject.addEntitiesToText(r12, r13, r14, r15, r16, r17)
            r4.setText(r6)
        L_0x01f8:
            r4.setGravity(r11)
            android.widget.LinearLayout r2 = r0.linearLayout
            r10 = -2
            r11 = -2
            r12 = 51
            r13 = 23
            r14 = 15
            r15 = 23
            r16 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r2.addView(r4, r6)
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            int r4 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r6 = 83
            r2.<init>(r9, r4, r6)
            r4 = 1124204544(0x43020000, float:130.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.bottomMargin = r4
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.shadow = r4
            java.lang.String r6 = "dialogShadowLine"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setBackgroundColor(r6)
            android.view.View r4 = r0.shadow
            r6 = 0
            r4.setAlpha(r6)
            android.view.View r4 = r0.shadow
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            r4.setTag(r6)
            android.view.View r4 = r0.shadow
            r5.addView(r4, r2)
            org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell r2 = new org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell
            r2.<init>(r1, r3)
            r4 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.Object[] r6 = new java.lang.Object[r3]
            java.lang.String r8 = "AppUpdateDownloadNow"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r8, r4, r6)
            r2.setText(r4, r3)
            android.view.View r4 = r2.background
            org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda0 r6 = new org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda0
            r6.<init>(r0)
            r4.setOnClickListener(r6)
            r8 = -1
            r9 = 1112014848(0x42480000, float:50.0)
            r10 = 83
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r5.addView(r2, r4)
            org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell r2 = new org.telegram.ui.Components.UpdateAppAlertDialog$BottomSheetCell
            r2.<init>(r1, r7)
            r1 = 2131624311(0x7f0e0177, float:1.8875798E38)
            java.lang.String r4 = "AppUpdateRemindMeLater"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r2.setText(r1, r3)
            android.view.View r1 = r2.background
            org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda1
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            r6 = -1
            r7 = 1112014848(0x42480000, float:50.0)
            r8 = 83
            r9 = 0
            r10 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r2, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UpdateAppAlertDialog.<init>(android.content.Context, org.telegram.tgnet.TLRPC$TL_help_appUpdate, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        dismiss();
    }

    private void runShadowAnimation(int i, final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : 1);
            if (z) {
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
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            UpdateAppAlertDialog.this.shadow.setVisibility(4);
                        }
                        AnimatorSet unused = UpdateAppAlertDialog.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animator)) {
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
        int max = Math.max(this.location[1] - AndroidUtilities.dp(24.0f), 0);
        if (((float) (this.location[1] + this.linearLayout.getMeasuredHeight())) <= ((float) (this.container.getMeasuredHeight() - AndroidUtilities.dp(113.0f))) + this.containerView.getTranslationY()) {
            runShadowAnimation(0, false);
        } else {
            runShadowAnimation(0, true);
        }
        if (this.scrollOffsetY != max) {
            this.scrollOffsetY = max;
            this.scrollView.invalidate();
        }
    }
}
