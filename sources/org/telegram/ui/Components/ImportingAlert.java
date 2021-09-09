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
    private boolean completed;
    private RLottieDrawable completedDrawable;
    private RLottieImageView imageView;
    private TextView[] importCountTextView = new TextView[2];
    private TextView[] infoTextView = new TextView[2];
    private LineProgressView lineProgressView;
    private final Runnable onFinishCallback;
    private ChatActivity parentFragment;
    private TextView percentTextView;
    private String stickersShortName;

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
        if (this.completed) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ImportingAlert(android.content.Context r20, java.lang.String r21, org.telegram.ui.ChatActivity r22) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = 0
            r0.<init>(r1, r3)
            r4 = 2
            android.widget.TextView[] r5 = new android.widget.TextView[r4]
            r0.importCountTextView = r5
            android.widget.TextView[] r5 = new android.widget.TextView[r4]
            r0.infoTextView = r5
            org.telegram.ui.Components.-$$Lambda$ImportingAlert$QTmtvPIkJztkl5-X7EW5TvT5WB0 r5 = new org.telegram.ui.Components.-$$Lambda$ImportingAlert$QTmtvPIkJztkl5-X7EW5TvT5WB0
            r5.<init>()
            r0.onFinishCallback = r5
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r6 = r22
            r0.parentFragment = r6
            r0.stickersShortName = r2
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r0.setCustomView(r6)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r7.setTypeface(r9)
            r9 = 1
            r10 = 1101004800(0x41a00000, float:20.0)
            r7.setTextSize(r9, r10)
            java.lang.String r10 = "dialogTextBlack"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r7.setTextColor(r11)
            r7.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r11 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r11)
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 51
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1101004800(0x41a00000, float:20.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r7, r11)
            org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
            r12 = 1123024896(0x42var_, float:120.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r13 = 2131558456(0x7f0d0038, float:1.8742228E38)
            java.lang.String r14 = "NUM"
            r17 = 0
            r18 = 0
            r12 = r11
            r12.<init>((int) r13, (java.lang.String) r14, (int) r15, (int) r16, (boolean) r17, (int[]) r18)
            r0.completedDrawable = r11
            r11.setAllowDecodeSingleFrame(r9)
            org.telegram.ui.Components.RLottieImageView r11 = new org.telegram.ui.Components.RLottieImageView
            r11.<init>(r1)
            r0.imageView = r11
            r11.setAutoRepeat(r9)
            org.telegram.ui.Components.RLottieImageView r11 = r0.imageView
            r12 = 2131558457(0x7f0d0039, float:1.874223E38)
            r13 = 120(0x78, float:1.68E-43)
            r11.setAnimation(r12, r13, r13)
            org.telegram.ui.Components.RLottieImageView r11 = r0.imageView
            r11.playAnimation()
            org.telegram.ui.Components.RLottieImageView r11 = r0.imageView
            r12 = 160(0xa0, float:2.24E-43)
            r13 = 1126170624(0x43200000, float:160.0)
            r14 = 49
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1117650944(0x429e0000, float:79.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r11, r12)
            org.telegram.ui.Components.RLottieImageView r11 = r0.imageView
            org.telegram.ui.Components.RLottieDrawable r11 = r11.getAnimatedDrawable()
            r12 = 178(0xb2, float:2.5E-43)
            r11.setOnFinishCallback(r5, r12)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.percentTextView = r5
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r5.setTypeface(r11)
            android.widget.TextView r5 = r0.percentTextView
            r11 = 1103101952(0x41CLASSNAME, float:24.0)
            r5.setTextSize(r9, r11)
            android.widget.TextView r5 = r0.percentTextView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r5.setTextColor(r11)
            android.widget.TextView r5 = r0.percentTextView
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 49
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 1132658688(0x43830000, float:262.0)
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r5, r11)
            org.telegram.ui.Components.LineProgressView r5 = new org.telegram.ui.Components.LineProgressView
            android.content.Context r11 = r19.getContext()
            r5.<init>(r11)
            r0.lineProgressView = r5
            java.lang.String r11 = "featuredStickers_addButton"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setProgressColor(r11)
            org.telegram.ui.Components.LineProgressView r5 = r0.lineProgressView
            java.lang.String r11 = "dialogLineProgressBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setBackColor(r11)
            org.telegram.ui.Components.LineProgressView r5 = r0.lineProgressView
            r11 = -1
            r12 = 1082130432(0x40800000, float:4.0)
            r13 = 51
            r14 = 1112014848(0x42480000, float:50.0)
            r15 = 1134133248(0x43998000, float:307.0)
            r16 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r5, r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = new org.telegram.ui.Components.ImportingAlert$BottomSheetCell
            r5.<init>(r1)
            r0.cell = r5
            r11 = 0
            r5.setBackground(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            java.lang.String r11 = "ImportDone"
            r12 = 2131625851(0x7f0e077b, float:1.8878922E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)
            r5.setText(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            r11 = 4
            r5.setVisibility(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            android.view.View r5 = r5.background
            org.telegram.ui.Components.-$$Lambda$ImportingAlert$8aLDb5GP-1CMSPXUaGRQhSF8nlo r11 = new org.telegram.ui.Components.-$$Lambda$ImportingAlert$8aLDb5GP-1CMSPXUaGRQhSF8nlo
            r11.<init>()
            r5.setOnClickListener(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            android.view.View r5 = r5.background
            r11 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r5.setPivotY(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            android.view.View r5 = r5.background
            r11 = 1025758986(0x3d23d70a, float:0.04)
            r5.setScaleY(r11)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r5 = r0.cell
            r11 = -1
            r12 = 1112014848(0x42480000, float:50.0)
            r14 = 1107820544(0x42080000, float:34.0)
            r15 = 1131872256(0x43770000, float:247.0)
            r16 = 1107820544(0x42080000, float:34.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r5, r11)
            r5 = 0
        L_0x0184:
            if (r5 >= r4) goto L_0x0240
            android.widget.TextView[] r11 = r0.importCountTextView
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r11[r5] = r12
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            r12 = 1098907648(0x41800000, float:16.0)
            r11.setTextSize(r9, r12)
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r11.setTypeface(r12)
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r11.setTextColor(r12)
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 49
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1135214592(0x43aa0000, float:340.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r11, r12)
            android.widget.TextView[] r11 = r0.infoTextView
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r11[r5] = r12
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r12 = 1096810496(0x41600000, float:14.0)
            r11.setTextSize(r9, r12)
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            java.lang.String r12 = "dialogTextGray3"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.setTextColor(r12)
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r11.setGravity(r9)
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r12 = -2
            r15 = 1106247680(0x41var_, float:30.0)
            r16 = 1136132096(0x43b80000, float:368.0)
            r17 = 1106247680(0x41var_, float:30.0)
            r18 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r11, r12)
            if (r5 != 0) goto L_0x0213
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r12 = 2131625866(0x7f0e078a, float:1.8878952E38)
            java.lang.String r13 = "ImportImportingInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setText(r12)
            goto L_0x023c
        L_0x0213:
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r12 = 0
            r11.setAlpha(r12)
            android.widget.TextView[] r11 = r0.infoTextView
            r11 = r11[r5]
            r13 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r14 = (float) r14
            r11.setTranslationY(r14)
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            r11.setAlpha(r12)
            android.widget.TextView[] r11 = r0.importCountTextView
            r11 = r11[r5]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r12 = (float) r12
            r11.setTranslationY(r12)
        L_0x023c:
            int r5 = r5 + 1
            goto L_0x0184
        L_0x0240:
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            r5 = 2131625850(0x7f0e077a, float:1.887892E38)
            java.lang.String r6 = "ImportCount"
            r8 = 1120403456(0x42CLASSNAME, float:100.0)
            java.lang.String r10 = "%d%%"
            if (r1 == 0) goto L_0x02d3
            r1 = 2131625867(0x7f0e078b, float:1.8878954E38)
            java.lang.String r2 = "ImportImportingTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r7.setText(r1)
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            org.telegram.messenger.SendMessagesHelper r1 = r1.getSendMessagesHelper()
            org.telegram.ui.ChatActivity r2 = r0.parentFragment
            long r11 = r2.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r1 = r1.getImportingHistory(r11)
            android.widget.TextView r2 = r0.percentTextView
            java.lang.Object[] r7 = new java.lang.Object[r9]
            int r11 = r1.uploadProgress
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r7[r3] = r11
            java.lang.String r7 = java.lang.String.format(r10, r7)
            r2.setText(r7)
            org.telegram.ui.Components.LineProgressView r2 = r0.lineProgressView
            int r7 = r1.uploadProgress
            float r7 = (float) r7
            float r7 = r7 / r8
            r2.setProgress(r7, r3)
            android.widget.TextView[] r2 = r0.importCountTextView
            r2 = r2[r3]
            java.lang.Object[] r4 = new java.lang.Object[r4]
            long r7 = r1.getUploadedCount()
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r4[r3] = r7
            long r7 = r1.getTotalCount()
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r4[r9] = r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r5, r4)
            r2.setText(r1)
            android.widget.TextView[] r1 = r0.infoTextView
            r1 = r1[r9]
            r2 = 2131625852(0x7f0e077c, float:1.8878924E38)
            java.lang.String r3 = "ImportDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView[] r1 = r0.importCountTextView
            r1 = r1[r9]
            r2 = 2131625853(0x7f0e077d, float:1.8878926E38)
            java.lang.String r3 = "ImportDoneTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            r1.addObserver(r0, r2)
            goto L_0x0351
        L_0x02d3:
            r1 = 2131625877(0x7f0e0795, float:1.8878974E38)
            java.lang.String r11 = "ImportStickersImportingTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            r7.setText(r1)
            int r1 = r0.currentAccount
            org.telegram.messenger.SendMessagesHelper r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1)
            org.telegram.messenger.SendMessagesHelper$ImportingStickers r1 = r1.getImportingStickers(r2)
            android.widget.TextView r2 = r0.percentTextView
            java.lang.Object[] r7 = new java.lang.Object[r9]
            int r11 = r1.uploadProgress
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r7[r3] = r11
            java.lang.String r7 = java.lang.String.format(r10, r7)
            r2.setText(r7)
            org.telegram.ui.Components.LineProgressView r2 = r0.lineProgressView
            int r7 = r1.uploadProgress
            float r7 = (float) r7
            float r7 = r7 / r8
            r2.setProgress(r7, r3)
            android.widget.TextView[] r2 = r0.importCountTextView
            r2 = r2[r3]
            java.lang.Object[] r4 = new java.lang.Object[r4]
            long r7 = r1.getUploadedCount()
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r4[r3] = r7
            long r7 = r1.getTotalCount()
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r4[r9] = r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r5, r4)
            r2.setText(r1)
            android.widget.TextView[] r1 = r0.infoTextView
            r1 = r1[r9]
            r2 = 2131625872(0x7f0e0790, float:1.8878964E38)
            java.lang.String r3 = "ImportStickersDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView[] r1 = r0.importCountTextView
            r1 = r1[r9]
            r2 = 2131625873(0x7f0e0791, float:1.8878966E38)
            java.lang.String r3 = "ImportStickersDoneTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.stickersImportProgressChanged
            r1.addObserver(r0, r2)
        L_0x0351:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ImportingAlert.<init>(android.content.Context, java.lang.String, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ImportingAlert(View view) {
        dismiss();
    }

    public void setCompleted() {
        this.completed = true;
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
        if (i == NotificationCenter.historyImportProgressChanged) {
            if (objArr.length > 1) {
                dismiss();
                return;
            }
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
            if (importingHistory == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame = (double) (180 - this.imageView.getAnimatedDrawable().getCurrentFrame());
                Double.isNaN(currentFrame);
                if ((currentFrame * 16.6d) + 3000.0d >= ((double) importingHistory.timeUntilFinish)) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", new Object[]{Integer.valueOf(importingHistory.uploadProgress)}));
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", NUM, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            this.lineProgressView.setProgress(((float) importingHistory.uploadProgress) / 100.0f, true);
        } else if (i != NotificationCenter.stickersImportProgressChanged) {
        } else {
            if (objArr.length > 1) {
                dismiss();
                return;
            }
            SendMessagesHelper.ImportingStickers importingStickers = SendMessagesHelper.getInstance(this.currentAccount).getImportingStickers(this.stickersShortName);
            if (importingStickers == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame2 = (double) (180 - this.imageView.getAnimatedDrawable().getCurrentFrame());
                Double.isNaN(currentFrame2);
                if ((currentFrame2 * 16.6d) + 3000.0d >= ((double) importingStickers.timeUntilFinish)) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", new Object[]{Integer.valueOf(importingStickers.uploadProgress)}));
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", NUM, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
            this.lineProgressView.setProgress(((float) importingStickers.uploadProgress) / 100.0f, true);
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            chatActivity.getNotificationCenter().removeObserver(this, NotificationCenter.historyImportProgressChanged);
        } else {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
    }
}
