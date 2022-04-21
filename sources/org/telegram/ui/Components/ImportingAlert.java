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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ImportingAlert  reason: not valid java name */
    public /* synthetic */ void m4061lambda$new$0$orgtelegramuiComponentsImportingAlert() {
        if (this.completed) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ImportingAlert(android.content.Context r22, java.lang.String r23, org.telegram.ui.ChatActivity r24, org.telegram.ui.ActionBar.Theme.ResourcesProvider r25) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r25
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
            r7 = r24
            r0.parentFragment = r7
            r0.stickersShortName = r2
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r0.setCustomView(r8)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r9.setTypeface(r11)
            r11 = 1
            r12 = 1101004800(0x41a00000, float:20.0)
            r9.setTextSize(r11, r12)
            java.lang.String r12 = "dialogTextBlack"
            int r13 = r0.getThemedColor(r12)
            r9.setTextColor(r13)
            r9.setSingleLine(r11)
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r13)
            r14 = -2
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 51
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 1101004800(0x41a00000, float:20.0)
            r19 = 1099431936(0x41880000, float:17.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r8.addView(r9, r13)
            org.telegram.ui.Components.RLottieDrawable r13 = new org.telegram.ui.Components.RLottieDrawable
            r14 = 1123024896(0x42var_, float:120.0)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r15 = 2131558470(0x7f0d0046, float:1.8742257E38)
            java.lang.String r16 = "NUM"
            r19 = 0
            r20 = 0
            r14 = r13
            r14.<init>(r15, r16, r17, r18, r19, r20)
            r0.completedDrawable = r13
            r13.setAllowDecodeSingleFrame(r11)
            org.telegram.ui.Components.RLottieImageView r13 = new org.telegram.ui.Components.RLottieImageView
            r13.<init>(r1)
            r0.imageView = r13
            r13.setAutoRepeat(r11)
            org.telegram.ui.Components.RLottieImageView r13 = r0.imageView
            r14 = 2131558471(0x7f0d0047, float:1.8742259E38)
            r15 = 120(0x78, float:1.68E-43)
            r13.setAnimation(r14, r15, r15)
            org.telegram.ui.Components.RLottieImageView r13 = r0.imageView
            r13.playAnimation()
            org.telegram.ui.Components.RLottieImageView r13 = r0.imageView
            r14 = 160(0xa0, float:2.24E-43)
            r15 = 1126170624(0x43200000, float:160.0)
            r16 = 49
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 1117650944(0x429e0000, float:79.0)
            r19 = 1099431936(0x41880000, float:17.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r8.addView(r13, r14)
            org.telegram.ui.Components.RLottieImageView r13 = r0.imageView
            org.telegram.ui.Components.RLottieDrawable r13 = r13.getAnimatedDrawable()
            r14 = 178(0xb2, float:2.5E-43)
            r13.setOnFinishCallback(r6, r14)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r0.percentTextView = r6
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r6.setTypeface(r13)
            android.widget.TextView r6 = r0.percentTextView
            r13 = 1103101952(0x41CLASSNAME, float:24.0)
            r6.setTextSize(r11, r13)
            android.widget.TextView r6 = r0.percentTextView
            int r13 = r0.getThemedColor(r12)
            r6.setTextColor(r13)
            android.widget.TextView r6 = r0.percentTextView
            r13 = -2
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 49
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 1132658688(0x43830000, float:262.0)
            r18 = 1099431936(0x41880000, float:17.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r8.addView(r6, r13)
            org.telegram.ui.Components.LineProgressView r6 = new org.telegram.ui.Components.LineProgressView
            android.content.Context r13 = r21.getContext()
            r6.<init>(r13)
            r0.lineProgressView = r6
            java.lang.String r13 = "featuredStickers_addButton"
            int r13 = r0.getThemedColor(r13)
            r6.setProgressColor(r13)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            java.lang.String r13 = "dialogLineProgressBackground"
            int r13 = r0.getThemedColor(r13)
            r6.setBackColor(r13)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            r13 = -1
            r14 = 1082130432(0x40800000, float:4.0)
            r15 = 51
            r16 = 1112014848(0x42480000, float:50.0)
            r17 = 1134133248(0x43998000, float:307.0)
            r18 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r8.addView(r6, r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = new org.telegram.ui.Components.ImportingAlert$BottomSheetCell
            r6.<init>(r1, r3)
            r0.cell = r6
            r13 = 0
            r6.setBackground(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            java.lang.String r13 = "ImportDone"
            r14 = 2131626071(0x7f0e0857, float:1.8879368E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r14)
            r6.setText(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            r13 = 4
            r6.setVisibility(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda0
            r13.<init>(r0)
            r6.setOnClickListener(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            r13 = 1111490560(0x42400000, float:48.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r6.setPivotY(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            android.view.View r6 = r6.background
            r13 = 1025758986(0x3d23d70a, float:0.04)
            r6.setScaleY(r13)
            org.telegram.ui.Components.ImportingAlert$BottomSheetCell r6 = r0.cell
            r13 = -1
            r14 = 1112014848(0x42480000, float:50.0)
            r16 = 1107820544(0x42080000, float:34.0)
            r17 = 1131872256(0x43770000, float:247.0)
            r18 = 1107820544(0x42080000, float:34.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r8.addView(r6, r13)
            r6 = 0
        L_0x0186:
            if (r6 >= r5) goto L_0x0243
            android.widget.TextView[] r13 = r0.importCountTextView
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r13[r6] = r14
            android.widget.TextView[] r13 = r0.importCountTextView
            r13 = r13[r6]
            r14 = 1098907648(0x41800000, float:16.0)
            r13.setTextSize(r11, r14)
            android.widget.TextView[] r13 = r0.importCountTextView
            r13 = r13[r6]
            android.graphics.Typeface r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r13.setTypeface(r14)
            android.widget.TextView[] r13 = r0.importCountTextView
            r13 = r13[r6]
            int r14 = r0.getThemedColor(r12)
            r13.setTextColor(r14)
            android.widget.TextView[] r13 = r0.importCountTextView
            r13 = r13[r6]
            r14 = -2
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 49
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 1135214592(0x43aa0000, float:340.0)
            r19 = 1099431936(0x41880000, float:17.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r8.addView(r13, r14)
            android.widget.TextView[] r13 = r0.infoTextView
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r13[r6] = r14
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r14 = 1096810496(0x41600000, float:14.0)
            r13.setTextSize(r11, r14)
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            java.lang.String r14 = "dialogTextGray3"
            int r14 = r0.getThemedColor(r14)
            r13.setTextColor(r14)
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r13.setGravity(r11)
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r14 = -2
            r17 = 1106247680(0x41var_, float:30.0)
            r18 = 1136132096(0x43b80000, float:368.0)
            r19 = 1106247680(0x41var_, float:30.0)
            r20 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r8.addView(r13, r14)
            if (r6 != 0) goto L_0x0215
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r14 = 2131626086(0x7f0e0866, float:1.8879398E38)
            java.lang.String r15 = "ImportImportingInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r13.setText(r14)
            goto L_0x023e
        L_0x0215:
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r14 = 0
            r13.setAlpha(r14)
            android.widget.TextView[] r13 = r0.infoTextView
            r13 = r13[r6]
            r15 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r5 = (float) r5
            r13.setTranslationY(r5)
            android.widget.TextView[] r5 = r0.importCountTextView
            r5 = r5[r6]
            r5.setAlpha(r14)
            android.widget.TextView[] r5 = r0.importCountTextView
            r5 = r5[r6]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r13 = (float) r13
            r5.setTranslationY(r13)
        L_0x023e:
            int r6 = r6 + 1
            r5 = 2
            goto L_0x0186
        L_0x0243:
            org.telegram.ui.ChatActivity r5 = r0.parentFragment
            java.lang.String r10 = "ImportCount"
            r12 = 1120403456(0x42CLASSNAME, float:100.0)
            java.lang.String r13 = "%d%%"
            if (r5 == 0) goto L_0x02d7
            r5 = 2131626087(0x7f0e0867, float:1.88794E38)
            java.lang.String r14 = "ImportImportingTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r5)
            r9.setText(r5)
            org.telegram.ui.ChatActivity r5 = r0.parentFragment
            org.telegram.messenger.SendMessagesHelper r5 = r5.getSendMessagesHelper()
            org.telegram.ui.ChatActivity r14 = r0.parentFragment
            long r14 = r14.getDialogId()
            org.telegram.messenger.SendMessagesHelper$ImportingHistory r5 = r5.getImportingHistory(r14)
            android.widget.TextView r14 = r0.percentTextView
            java.lang.Object[] r15 = new java.lang.Object[r11]
            int r6 = r5.uploadProgress
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r15[r4] = r6
            java.lang.String r6 = java.lang.String.format(r13, r15)
            r14.setText(r6)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            int r13 = r5.uploadProgress
            float r13 = (float) r13
            float r13 = r13 / r12
            r6.setProgress(r13, r4)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r4]
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            long r13 = r5.getUploadedCount()
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r4] = r13
            long r13 = r5.getTotalCount()
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r11] = r4
            r4 = 2131626070(0x7f0e0856, float:1.8879366E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r4, r12)
            r6.setText(r4)
            android.widget.TextView[] r4 = r0.infoTextView
            r4 = r4[r11]
            r6 = 2131626072(0x7f0e0858, float:1.887937E38)
            java.lang.String r10 = "ImportDoneInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r4.setText(r6)
            android.widget.TextView[] r4 = r0.importCountTextView
            r4 = r4[r11]
            r6 = 2131626073(0x7f0e0859, float:1.8879372E38)
            java.lang.String r10 = "ImportDoneTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r4.setText(r6)
            org.telegram.ui.ChatActivity r4 = r0.parentFragment
            org.telegram.messenger.NotificationCenter r4 = r4.getNotificationCenter()
            int r6 = org.telegram.messenger.NotificationCenter.historyImportProgressChanged
            r4.addObserver(r0, r6)
            goto L_0x0359
        L_0x02d7:
            r5 = 2131626097(0x7f0e0871, float:1.887942E38)
            java.lang.String r6 = "ImportStickersImportingTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r9.setText(r5)
            int r5 = r0.currentAccount
            org.telegram.messenger.SendMessagesHelper r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5)
            org.telegram.messenger.SendMessagesHelper$ImportingStickers r5 = r5.getImportingStickers(r2)
            android.widget.TextView r6 = r0.percentTextView
            java.lang.Object[] r14 = new java.lang.Object[r11]
            int r15 = r5.uploadProgress
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14[r4] = r15
            java.lang.String r13 = java.lang.String.format(r13, r14)
            r6.setText(r13)
            org.telegram.ui.Components.LineProgressView r6 = r0.lineProgressView
            int r13 = r5.uploadProgress
            float r13 = (float) r13
            float r13 = r13 / r12
            r6.setProgress(r13, r4)
            android.widget.TextView[] r6 = r0.importCountTextView
            r6 = r6[r4]
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            long r13 = r5.getUploadedCount()
            java.lang.String r13 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r4] = r13
            long r13 = r5.getTotalCount()
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r12[r11] = r4
            r4 = 2131626070(0x7f0e0856, float:1.8879366E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r10, r4, r12)
            r6.setText(r4)
            android.widget.TextView[] r4 = r0.infoTextView
            r4 = r4[r11]
            r6 = 2131626092(0x7f0e086c, float:1.887941E38)
            java.lang.String r10 = "ImportStickersDoneInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r4.setText(r6)
            android.widget.TextView[] r4 = r0.importCountTextView
            r4 = r4[r11]
            r6 = 2131626093(0x7f0e086d, float:1.8879412E38)
            java.lang.String r10 = "ImportStickersDoneTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r4.setText(r6)
            int r4 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)
            int r6 = org.telegram.messenger.NotificationCenter.stickersImportProgressChanged
            r4.addObserver(r0, r6)
        L_0x0359:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ImportingAlert.<init>(android.content.Context, java.lang.String, org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ImportingAlert  reason: not valid java name */
    public /* synthetic */ void m4062lambda$new$1$orgtelegramuiComponentsImportingAlert(View v) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = id;
        Object[] objArr = args;
        if (i != NotificationCenter.historyImportProgressChanged) {
            String str = "ImportCount";
            if (i != NotificationCenter.stickersImportProgressChanged) {
                return;
            }
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
                double currentFrame = (double) (180 - this.imageView.getAnimatedDrawable().getCurrentFrame());
                Double.isNaN(currentFrame);
                if ((currentFrame * 16.6d) + 3000.0d >= ((double) importingStickers.timeUntilFinish)) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", new Object[]{Integer.valueOf(importingStickers.uploadProgress)}));
            this.importCountTextView[0].setText(LocaleController.formatString(str, NUM, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
            this.lineProgressView.setProgress(((float) importingStickers.uploadProgress) / 100.0f, true);
        } else if (objArr.length > 1) {
            dismiss();
        } else {
            String str2 = "ImportCount";
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
            if (importingHistory == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame2 = (double) (180 - this.imageView.getAnimatedDrawable().getCurrentFrame());
                Double.isNaN(currentFrame2);
                if ((currentFrame2 * 16.6d) + 3000.0d >= ((double) importingHistory.timeUntilFinish)) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", new Object[]{Integer.valueOf(importingHistory.uploadProgress)}));
            this.importCountTextView[0].setText(LocaleController.formatString(str2, NUM, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            this.lineProgressView.setProgress(((float) importingHistory.uploadProgress) / 100.0f, true);
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
