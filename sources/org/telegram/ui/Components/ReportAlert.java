package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class ReportAlert extends BottomSheet {
    private BottomSheetCell clearButton;
    private EditTextBoldCursor editText;

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private LinearLayout linearLayout;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
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
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ReportAlert(android.content.Context r26, int r27) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = 1
            r0.<init>(r1, r3)
            r4 = 0
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            android.widget.ScrollView r5 = new android.widget.ScrollView
            r5.<init>(r1)
            r5.setFillViewport(r3)
            r0.setCustomView(r5)
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r7 = -1
            r8 = -2
            r9 = 51
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createScroll(r7, r8, r9)
            r5.addView(r6, r7)
            org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
            r7.<init>(r1)
            r8 = 2131558509(0x7f0d006d, float:1.8742336E38)
            r9 = 120(0x78, float:1.68E-43)
            r7.setAnimation(r8, r9, r9)
            r7.playAnimation()
            r10 = 160(0xa0, float:2.24E-43)
            r11 = 1126170624(0x43200000, float:160.0)
            r12 = 49
            r13 = 1099431936(0x41880000, float:17.0)
            r14 = 1096810496(0x41600000, float:14.0)
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r6.addView(r7, r8)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r8.setTypeface(r9)
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            r8.setTextSize(r3, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
            r9 = 5
            r10 = 6
            if (r2 != 0) goto L_0x007e
            r11 = 2131627764(0x7f0e0ef4, float:1.8882802E38)
            java.lang.String r12 = "ReportTitleSpam"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            goto L_0x00cb
        L_0x007e:
            if (r2 != r10) goto L_0x008d
            r11 = 2131627762(0x7f0e0ef2, float:1.8882798E38)
            java.lang.String r12 = "ReportTitleFake"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            goto L_0x00cb
        L_0x008d:
            if (r2 != r3) goto L_0x009c
            r11 = 2131627765(0x7f0e0ef5, float:1.8882804E38)
            java.lang.String r12 = "ReportTitleViolence"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            goto L_0x00cb
        L_0x009c:
            r11 = 2
            if (r2 != r11) goto L_0x00ac
            r11 = 2131627761(0x7f0e0ef1, float:1.8882796E38)
            java.lang.String r12 = "ReportTitleChild"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            goto L_0x00cb
        L_0x00ac:
            if (r2 != r9) goto L_0x00bb
            r11 = 2131627763(0x7f0e0ef3, float:1.88828E38)
            java.lang.String r12 = "ReportTitlePornography"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            goto L_0x00cb
        L_0x00bb:
            r11 = 100
            if (r2 != r11) goto L_0x00cb
            r11 = 2131627735(0x7f0e0ed7, float:1.8882743E38)
            java.lang.String r12 = "ReportChat"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
        L_0x00cb:
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 49
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 1128595456(0x43450000, float:197.0)
            r17 = 1099431936(0x41880000, float:17.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r8, r11)
            android.widget.TextView r11 = new android.widget.TextView
            r11.<init>(r1)
            r12 = 1096810496(0x41600000, float:14.0)
            r11.setTextSize(r3, r12)
            java.lang.String r12 = "dialogTextGray3"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.setTextColor(r12)
            r11.setGravity(r3)
            r12 = 2131627747(0x7f0e0ee3, float:1.8882767E38)
            java.lang.String r13 = "ReportInfo"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setText(r12)
            r13 = -2
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 49
            r16 = 1106247680(0x41var_, float:30.0)
            r17 = 1131085824(0x436b0000, float:235.0)
            r18 = 1106247680(0x41var_, float:30.0)
            r19 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r6.addView(r11, r12)
            org.telegram.ui.Components.EditTextBoldCursor r12 = new org.telegram.ui.Components.EditTextBoldCursor
            r12.<init>(r1)
            r0.editText = r12
            r13 = 1099956224(0x41900000, float:18.0)
            r12.setTextSize(r3, r13)
            org.telegram.ui.Components.EditTextBoldCursor r12 = r0.editText
            java.lang.String r13 = "windowBackgroundWhiteHintText"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setHintTextColor(r13)
            org.telegram.ui.Components.EditTextBoldCursor r12 = r0.editText
            java.lang.String r13 = "windowBackgroundWhiteBlackText"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setTextColor(r14)
            org.telegram.ui.Components.EditTextBoldCursor r12 = r0.editText
            r14 = 0
            r12.setBackgroundDrawable(r14)
            org.telegram.ui.Components.EditTextBoldCursor r12 = r0.editText
            java.lang.String r15 = "windowBackgroundWhiteInputField"
            int r15 = r0.getThemedColor(r15)
            java.lang.String r9 = "windowBackgroundWhiteInputFieldActivated"
            int r9 = r0.getThemedColor(r9)
            java.lang.String r14 = "windowBackgroundWhiteRedText3"
            int r14 = r0.getThemedColor(r14)
            r12.setLineColors(r15, r9, r14)
            org.telegram.ui.Components.EditTextBoldCursor r9 = r0.editText
            r9.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r9 = r0.editText
            r9.setLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r9 = r0.editText
            r9.setPadding(r4, r4, r4, r4)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r4.setSingleLine(r3)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0170
            r9 = 5
            goto L_0x0171
        L_0x0170:
            r9 = 3
        L_0x0171:
            r4.setGravity(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r9 = 180224(0x2CLASSNAME, float:2.52548E-40)
            r4.setInputType(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r4.setImeOptions(r10)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r9 = 2131627746(0x7f0e0ee2, float:1.8882765E38)
            java.lang.String r10 = "ReportHint"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.setHint(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4.setCursorColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setCursorSize(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r9 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda1
            r9.<init>(r0)
            r4.setOnEditorActionListener(r9)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r18 = -1
            r19 = 1108344832(0x42100000, float:36.0)
            r20 = 51
            r21 = 1099431936(0x41880000, float:17.0)
            r22 = 1134067712(0x43988000, float:305.0)
            r23 = 1099431936(0x41880000, float:17.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r6.addView(r4, r9)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = new org.telegram.ui.Components.ReportAlert$BottomSheetCell
            r4.<init>(r1)
            r0.clearButton = r4
            r9 = 0
            r4.setBackground(r9)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = r0.clearButton
            r9 = 2131627751(0x7f0e0ee7, float:1.8882775E38)
            java.lang.String r10 = "ReportSend"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.setText(r9)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = r0.clearButton
            android.view.View r4 = r4.background
            org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda0 r9 = new org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda0
            r9.<init>(r0, r2)
            r4.setOnClickListener(r9)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = r0.clearButton
            r12 = -1
            r13 = 1112014848(0x42480000, float:50.0)
            r14 = 51
            r15 = 0
            r16 = 1135771648(0x43b28000, float:357.0)
            r17 = 0
            r18 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r6.addView(r4, r9)
            r0.smoothKeyboardAnimationEnabled = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReportAlert.<init>(android.content.Context, int):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ReportAlert  reason: not valid java name */
    public /* synthetic */ boolean m4294lambda$new$0$orgtelegramuiComponentsReportAlert(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.clearButton.background.callOnClick();
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ReportAlert  reason: not valid java name */
    public /* synthetic */ void m4295lambda$new$1$orgtelegramuiComponentsReportAlert(int type, View v) {
        AndroidUtilities.hideKeyboard(this.editText);
        onSend(type, this.editText.getText().toString());
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onSend(int type, String message) {
    }
}
