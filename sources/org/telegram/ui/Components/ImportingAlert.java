package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public class ImportingAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private BottomSheetCell cell;
    private RLottieDrawable completedDrawable;
    private boolean compteled;
    private RLottieImageView imageView;
    private TextView[] importCountTextView = new TextView[2];
    private TextView[] infoTextView = new TextView[2];
    private LineProgressView lineProgressView;
    private final Runnable onFinishCallback;
    private ChatActivity parentFragment;
    private TextView percentTextView;

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        /* access modifiers changed from: private */
        public RLottieImageView imageView;
        /* access modifiers changed from: private */
        public LinearLayout linearLayout;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.linearLayout = linearLayout2;
            linearLayout2.setOrientation(0);
            addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 17));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(20.0f), Theme.getColor("featuredStickers_buttonText")));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
            this.imageView.setAnimation(NUM, 26, 26);
            this.imageView.setScaleX(0.8f);
            this.imageView.setScaleY(0.8f);
            this.linearLayout.addView(this.imageView, LayoutHelper.createLinear(20, 20, 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16, 10, 0, 0, 0));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ImportingAlert() {
        if (this.compteled) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ImportingAlert(android.content.Context r19, org.telegram.ui.ChatActivity r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = 0
            r0.<init>(r1, r2)
            r3 = 2
            android.widget.TextView[] r4 = new android.widget.TextView[r3]
            r0.importCountTextView = r4
            android.widget.TextView[] r4 = new android.widget.TextView[r3]
            r0.infoTextView = r4
            org.telegram.ui.Components.-$$Lambda$ImportingAlert$QTmtvPIkJztkl5-X7EW5TvT5WB0 r4 = new org.telegram.ui.Components.-$$Lambda$ImportingAlert$QTmtvPIkJztkl5-X7EW5TvT5WB0
            r4.<init>()
            r0.onFinishCallback = r4
            r0.setApplyBottomPadding(r2)
            r0.setApplyTopPadding(r2)
            r5 = r20
            r0.parentFragment = r5
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            r0.setCustomView(r5)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r8)
            r8 = 1
            r9 = 1101004800(0x41a00000, float:20.0)
            r6.setTextSize(r8, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r10)
            java.lang.String r10 = "ImportImportingTitle"
            r11 = 2131625701(0x7f0e06e5, float:1.8878617E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r6.setText(r10)
            r6.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r10)
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 51
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 1101004800(0x41a00000, float:20.0)
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r6, r10)
            org.telegram.ui.Components.RLottieDrawable r6 = new org.telegram.ui.Components.RLottieDrawable
            r10 = 1123024896(0x42var_, float:120.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r12 = 2131558442(0x7f0d002a, float:1.87422E38)
            java.lang.String r13 = "NUM"
            r16 = 0
            r17 = 0
            r11 = r6
            r11.<init>((int) r12, (java.lang.String) r13, (int) r14, (int) r15, (boolean) r16, (int[]) r17)
            r0.completedDrawable = r6
            r6.setAllowDecodeSingleFrame(r8)
            org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
            r6.<init>(r1)
            r0.imageView = r6
            r6.setAutoRepeat(r8)
            org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
            r10 = 2131558443(0x7f0d002b, float:1.8742202E38)
            r11 = 120(0x78, float:1.68E-43)
            r6.setAnimation(r10, r11, r11)
            org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
            r6.playAnimation()
            org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
            r10 = 160(0xa0, float:2.24E-43)
            r11 = 1126170624(0x43200000, float:160.0)
            r12 = 49
            r13 = 1099431936(0x41880000, float:17.0)
            r14 = 1117650944(0x429e0000, float:79.0)
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r10)
            org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
            org.telegram.ui.Components.RLottieDrawable r6 = r6.getAnimatedDrawable()
            r10 = 178(0xb2, float:2.5E-43)
            r6.setOnFinishCallback(r4, r10)
            org.telegram.ui.ChatActivity r4 = r0.parentFragment
            org.telegram.messenger.SendMessagesHelper r4 = r4.getSendMessagesHelper()
            org.telegram.ui.ChatActivity r6 = r0.parentFragment
            long r10 = r6.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r4 = r4.getImportingHistory(r10)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.percentTextView = r6
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r10)
            android.widget.TextView r6 = r0.percentTextView
            r10 = 1103101952(0x41CLASSNAME, float:24.0)
            r6.setTextSize(r8, r10)
            android.widget.TextView r6 = r0.percentTextView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r10)
            android.widget.TextView r6 = r0.percentTextView
            java.lang.Object[] r10 = new java.lang.Object[r8]
            int r11 = r4.uploadProgress
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r10[r2] = r11
            java.lang.String r11 = "%d%%"
            java.lang.String r10 = java.lang.String.format(r11, r10)
            r6.setText(r10)
            android.widget.TextView r6 = r0.percentTextView
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 1132658688(0x43830000, float:262.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r10)
            org.telegram.ui.Components.LineProgressView r6 = new org.telegram.ui.Components.LineProgressView
            android.content.Context r10 = r18.getContext()
            r6.<init>(r10)
            r0.lineProgressView = r6
            int r10 = r4.uploadProgress
            float r10 = (float) r10
            r11 = 1120403456(0x42CLASSNAME, float:100.0)
            float r10 = r10 / r11
            r6.setProgress(r10, r2)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            java.lang.String r10 = "featuredStickers_addButton"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setProgressColor(r10)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            java.lang.String r10 = "dialogLineProgressBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setBackColor(r10)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            r10 = -1
            r11 = 1082130432(0x40800000, float:4.0)
            r12 = 51
            r13 = 1112014848(0x42480000, float:50.0)
            r14 = 1134133248(0x43998000, float:307.0)
            r15 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = new org.telegram.ui.Components.ImportingAlert$BottomSheetCell
            r6.<init>(r1)
            r0.cell = r6
            r10 = 0
            r6.setBackground(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            java.lang.String r10 = "ImportDone"
            r11 = 2131625688(0x7f0e06d8, float:1.8878591E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r6.setText(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            r10 = 4
            r6.setVisibility(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            org.telegram.ui.Components.-$$Lambda$ImportingAlert$8aLDb5GP-1CMSPXUaGRQhSF8nlo r10 = new org.telegram.ui.Components.-$$Lambda$ImportingAlert$8aLDb5GP-1CMSPXUaGRQhSF8nlo
            r10.<init>()
            r6.setOnClickListener(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r6.setPivotY(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            r10 = 1025758986(0x3d23d70a, float:0.04)
            r6.setScaleY(r10)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            r10 = -1
            r11 = 1112014848(0x42480000, float:50.0)
            r13 = 1107820544(0x42080000, float:34.0)
            r14 = 1131872256(0x43770000, float:247.0)
            r15 = 1107820544(0x42080000, float:34.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r10)
            r6 = 0
        L_0x01b4:
            if (r6 >= r3) goto L_0x02b6
            android.widget.TextView[] r10 = r0.importCountTextView
            android.widget.TextView r11 = new android.widget.TextView
            r11.<init>(r1)
            r10[r6] = r11
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            r11 = 1098907648(0x41800000, float:16.0)
            r10.setTextSize(r8, r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r10.setTypeface(r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r10.setTextColor(r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 49
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 1135214592(0x43aa0000, float:340.0)
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r10, r11)
            android.widget.TextView[] r10 = r0.infoTextView
            android.widget.TextView r11 = new android.widget.TextView
            r11.<init>(r1)
            r10[r6] = r11
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r11 = 1096810496(0x41600000, float:14.0)
            r10.setTextSize(r8, r11)
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            java.lang.String r11 = "dialogTextGray3"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.setTextColor(r11)
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r10.setGravity(r8)
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r11 = -2
            r14 = 1106247680(0x41var_, float:30.0)
            r15 = 1136132096(0x43b80000, float:368.0)
            r16 = 1106247680(0x41var_, float:30.0)
            r17 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r10, r11)
            if (r6 != 0) goto L_0x0269
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r11 = 2131625700(0x7f0e06e4, float:1.8878615E38)
            java.lang.String r12 = "ImportImportingInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setText(r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            r11 = 2131625687(0x7f0e06d7, float:1.887859E38)
            java.lang.Object[] r12 = new java.lang.Object[r3]
            long r13 = r4.getUploadedCount()
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r2] = r13
            long r13 = r4.getTotalCount()
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r8] = r13
            java.lang.String r13 = "ImportCount"
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r13, r11, r12)
            r10.setText(r11)
            goto L_0x02b2
        L_0x0269:
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r11 = 2131625689(0x7f0e06d9, float:1.8878593E38)
            java.lang.String r12 = "ImportDoneInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setText(r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            r11 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r12 = "ImportDoneTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setText(r11)
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r11 = 0
            r10.setAlpha(r11)
            android.widget.TextView[] r10 = r0.infoTextView
            r10 = r10[r6]
            r12 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r13 = (float) r13
            r10.setTranslationY(r13)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            r10.setAlpha(r11)
            android.widget.TextView[] r10 = r0.importCountTextView
            r10 = r10[r6]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r11 = (float) r11
            r10.setTranslationY(r11)
        L_0x02b2:
            int r6 = r6 + 1
            goto L_0x01b4
        L_0x02b6:
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            r1.addObserver(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ImportingAlert.<init>(android.content.Context, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ImportingAlert(View view) {
        dismiss();
    }

    public void setCompteled() {
        this.compteled = true;
        this.imageView.setAutoRepeat(false);
        this.cell.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.percentTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.percentTextView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.infoTextView[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.infoTextView[0], View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.importCountTextView[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.importCountTextView[0], View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofFloat(this.infoTextView[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.infoTextView[1], View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.importCountTextView[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.importCountTextView[1], View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.lineProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cell.linearLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(8.0f), 0.0f})});
        this.cell.background.animate().scaleY(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250).start();
        this.cell.imageView.animate().scaleY(1.0f).scaleX(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250).start();
        this.cell.imageView.playAnimation();
        animatorSet.start();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.historyImportProgressChanged) {
            return;
        }
        if (objArr.length > 1) {
            dismiss();
            return;
        }
        SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
        if (importingHistory == null) {
            setCompteled();
            return;
        }
        if (!this.compteled) {
            double currentFrame = (double) (180 - this.imageView.getAnimatedDrawable().getCurrentFrame());
            Double.isNaN(currentFrame);
            if ((currentFrame * 16.6d) + 3000.0d >= ((double) importingHistory.timeUntilFinish)) {
                this.imageView.setAutoRepeat(false);
                this.compteled = true;
            }
        }
        this.percentTextView.setText(String.format("%d%%", new Object[]{Integer.valueOf(importingHistory.uploadProgress)}));
        this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", NUM, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
        this.lineProgressView.setProgress(((float) importingHistory.uploadProgress) / 100.0f, true);
    }

    public void dismissInternal() {
        super.dismissInternal();
        this.parentFragment.getNotificationCenter().removeObserver(this, NotificationCenter.historyImportProgressChanged);
    }
}
