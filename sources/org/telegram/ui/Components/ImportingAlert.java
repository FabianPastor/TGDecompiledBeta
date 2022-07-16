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
        private Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.linearLayout = linearLayout2;
            linearLayout2.setOrientation(0);
            addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 17));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(20.0f), getThemedColor("featuredStickers_buttonText")));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
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
            this.textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
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

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.completed) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ImportingAlert(android.content.Context r21, java.lang.String r22, org.telegram.ui.ChatActivity r23, org.telegram.ui.ActionBar.Theme.ResourcesProvider r24) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r24
            r4 = 0
            r0.<init>(r1, r4, r3)
            r5 = 2
            android.widget.TextView[] r6 = new android.widget.TextView[r5]
            r0.importCountTextView = r6
            android.widget.TextView[] r6 = new android.widget.TextView[r5]
            r0.infoTextView = r6
            org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda1
            r6.<init>(r0)
            r0.onFinishCallback = r6
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            r7 = r23
            r0.parentFragment = r7
            r0.stickersShortName = r2
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r1)
            r0.setCustomView(r7)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r8.setTypeface(r10)
            r10 = 1
            r11 = 1101004800(0x41a00000, float:20.0)
            r8.setTextSize(r10, r11)
            java.lang.String r11 = "dialogTextBlack"
            int r12 = r0.getThemedColor(r11)
            r8.setTextColor(r12)
            r8.setSingleLine(r10)
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            r8.setEllipsize(r12)
            r13 = -2
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 51
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 1101004800(0x41a00000, float:20.0)
            r18 = 1099431936(0x41880000, float:17.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r7.addView(r8, r12)
            org.telegram.ui.Components.RLottieDrawable r12 = new org.telegram.ui.Components.RLottieDrawable
            r13 = 1123024896(0x42var_, float:120.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r14 = 2131558474(0x7f0d004a, float:1.8742265E38)
            java.lang.String r15 = "NUM"
            r18 = 0
            r19 = 0
            r13 = r12
            r13.<init>(r14, r15, r16, r17, r18, r19)
            r0.completedDrawable = r12
            r12.setAllowDecodeSingleFrame(r10)
            org.telegram.ui.Components.RLottieImageView r12 = new org.telegram.ui.Components.RLottieImageView
            r12.<init>(r1)
            r0.imageView = r12
            r12.setAutoRepeat(r10)
            org.telegram.ui.Components.RLottieImageView r12 = r0.imageView
            r13 = 2131558475(0x7f0d004b, float:1.8742267E38)
            r14 = 120(0x78, float:1.68E-43)
            r12.setAnimation((int) r13, (int) r14, (int) r14)
            org.telegram.ui.Components.RLottieImageView r12 = r0.imageView
            r12.playAnimation()
            org.telegram.ui.Components.RLottieImageView r12 = r0.imageView
            r13 = 160(0xa0, float:2.24E-43)
            r14 = 1126170624(0x43200000, float:160.0)
            r15 = 49
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 1117650944(0x429e0000, float:79.0)
            r18 = 1099431936(0x41880000, float:17.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r7.addView(r12, r13)
            org.telegram.ui.Components.RLottieImageView r12 = r0.imageView
            org.telegram.ui.Components.RLottieDrawable r12 = r12.getAnimatedDrawable()
            r13 = 178(0xb2, float:2.5E-43)
            r12.setOnFinishCallback(r6, r13)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.percentTextView = r6
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r6.setTypeface(r12)
            android.widget.TextView r6 = r0.percentTextView
            r12 = 1103101952(0x41CLASSNAME, float:24.0)
            r6.setTextSize(r10, r12)
            android.widget.TextView r6 = r0.percentTextView
            int r12 = r0.getThemedColor(r11)
            r6.setTextColor(r12)
            android.widget.TextView r6 = r0.percentTextView
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 49
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1132658688(0x43830000, float:262.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r7.addView(r6, r12)
            org.telegram.ui.Components.LineProgressView r6 = new org.telegram.ui.Components.LineProgressView
            android.content.Context r12 = r20.getContext()
            r6.<init>(r12)
            r0.lineProgressView = r6
            java.lang.String r12 = "featuredStickers_addButton"
            int r12 = r0.getThemedColor(r12)
            r6.setProgressColor(r12)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            java.lang.String r12 = "dialogLineProgressBackground"
            int r12 = r0.getThemedColor(r12)
            r6.setBackColor(r12)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            r12 = -1
            r13 = 1082130432(0x40800000, float:4.0)
            r14 = 51
            r15 = 1112014848(0x42480000, float:50.0)
            r16 = 1134133248(0x43998000, float:307.0)
            r17 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r7.addView(r6, r12)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = new org.telegram.ui.Components.ImportingAlert$BottomSheetCell
            r6.<init>(r1, r3)
            r0.cell = r6
            r3 = 0
            r6.setBackground(r3)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            java.lang.String r6 = "ImportDone"
            r12 = 2131626215(0x7f0e08e7, float:1.887966E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r12)
            r3.setText(r6)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            r6 = 4
            r3.setVisibility(r6)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            android.view.View r3 = r3.background
            org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda0 r6 = new org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda0
            r6.<init>(r0)
            r3.setOnClickListener(r6)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            android.view.View r3 = r3.background
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r3.setPivotY(r6)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            android.view.View r3 = r3.background
            r6 = 1025758986(0x3d23d70a, float:0.04)
            r3.setScaleY(r6)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r3 = r0.cell
            r12 = -1
            r13 = 1112014848(0x42480000, float:50.0)
            r15 = 1107820544(0x42080000, float:34.0)
            r16 = 1131872256(0x43770000, float:247.0)
            r17 = 1107820544(0x42080000, float:34.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r7.addView(r3, r6)
            r3 = 0
        L_0x0186:
            if (r3 >= r5) goto L_0x0242
            android.widget.TextView[] r6 = r0.importCountTextView
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r6[r3] = r12
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            r12 = 1098907648(0x41800000, float:16.0)
            r6.setTextSize(r10, r12)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r6.setTypeface(r12)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            int r12 = r0.getThemedColor(r11)
            r6.setTextColor(r12)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 49
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1135214592(0x43aa0000, float:340.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r7.addView(r6, r12)
            android.widget.TextView[] r6 = r0.infoTextView
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r6[r3] = r12
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r12 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r10, r12)
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            java.lang.String r12 = "dialogTextGray3"
            int r12 = r0.getThemedColor(r12)
            r6.setTextColor(r12)
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r6.setGravity(r10)
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r12 = -2
            r15 = 1106247680(0x41var_, float:30.0)
            r16 = 1136132096(0x43b80000, float:368.0)
            r17 = 1106247680(0x41var_, float:30.0)
            r18 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r7.addView(r6, r12)
            if (r3 != 0) goto L_0x0215
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r12 = 2131626230(0x7f0e08f6, float:1.887969E38)
            java.lang.String r13 = "ImportImportingInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r6.setText(r12)
            goto L_0x023e
        L_0x0215:
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r12 = 0
            r6.setAlpha(r12)
            android.widget.TextView[] r6 = r0.infoTextView
            r6 = r6[r3]
            r13 = 1092616192(0x41200000, float:10.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r14 = (float) r14
            r6.setTranslationY(r14)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            r6.setAlpha(r12)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r3]
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r12 = (float) r12
            r6.setTranslationY(r12)
        L_0x023e:
            int r3 = r3 + 1
            goto L_0x0186
        L_0x0242:
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            r3 = 2131626214(0x7f0e08e6, float:1.8879658E38)
            java.lang.String r6 = "ImportCount"
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            java.lang.String r9 = "%d%%"
            if (r1 == 0) goto L_0x02d5
            r1 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r2 = "ImportImportingTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r8.setText(r1)
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            org.telegram.messenger.SendMessagesHelper r1 = r1.getSendMessagesHelper()
            org.telegram.ui.ChatActivity r2 = r0.parentFragment
            long r11 = r2.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r1 = r1.getImportingHistory(r11)
            android.widget.TextView r2 = r0.percentTextView
            java.lang.Object[] r8 = new java.lang.Object[r10]
            int r11 = r1.uploadProgress
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r8[r4] = r11
            java.lang.String r8 = java.lang.String.format(r9, r8)
            r2.setText(r8)
            org.telegram.ui.Components.LineProgressView r2 = r0.lineProgressView
            int r8 = r1.uploadProgress
            float r8 = (float) r8
            float r8 = r8 / r7
            r2.setProgress(r8, r4)
            android.widget.TextView[] r2 = r0.importCountTextView
            r2 = r2[r4]
            java.lang.Object[] r5 = new java.lang.Object[r5]
            long r7 = r1.getUploadedCount()
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r5[r4] = r7
            long r7 = r1.getTotalCount()
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r5[r10] = r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r3, r5)
            r2.setText(r1)
            android.widget.TextView[] r1 = r0.infoTextView
            r1 = r1[r10]
            r2 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.String r3 = "ImportDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView[] r1 = r0.importCountTextView
            r1 = r1[r10]
            r2 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            java.lang.String r3 = "ImportDoneTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            org.telegram.ui.ChatActivity r1 = r0.parentFragment
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            r1.addObserver(r0, r2)
            goto L_0x0353
        L_0x02d5:
            r1 = 2131626241(0x7f0e0901, float:1.8879713E38)
            java.lang.String r11 = "ImportStickersImportingTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            r8.setText(r1)
            int r1 = r0.currentAccount
            org.telegram.messenger.SendMessagesHelper r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1)
            org.telegram.messenger.SendMessagesHelper$ImportingStickers r1 = r1.getImportingStickers(r2)
            android.widget.TextView r2 = r0.percentTextView
            java.lang.Object[] r8 = new java.lang.Object[r10]
            int r11 = r1.uploadProgress
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r8[r4] = r11
            java.lang.String r8 = java.lang.String.format(r9, r8)
            r2.setText(r8)
            org.telegram.ui.Components.LineProgressView r2 = r0.lineProgressView
            int r8 = r1.uploadProgress
            float r8 = (float) r8
            float r8 = r8 / r7
            r2.setProgress(r8, r4)
            android.widget.TextView[] r2 = r0.importCountTextView
            r2 = r2[r4]
            java.lang.Object[] r5 = new java.lang.Object[r5]
            long r7 = r1.getUploadedCount()
            java.lang.String r7 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r5[r4] = r7
            long r7 = r1.getTotalCount()
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r7)
            r5[r10] = r1
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r6, r3, r5)
            r2.setText(r1)
            android.widget.TextView[] r1 = r0.infoTextView
            r1 = r1[r10]
            r2 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            java.lang.String r3 = "ImportStickersDoneInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView[] r1 = r0.importCountTextView
            r1 = r1[r10]
            r2 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.String r3 = "ImportStickersDoneTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.stickersImportProgressChanged
            r1.addObserver(r0, r2)
        L_0x0353:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ImportingAlert.<init>(android.content.Context, java.lang.String, org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
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
