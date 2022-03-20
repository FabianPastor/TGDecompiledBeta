package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class ReportAlert extends BottomSheet {
    private BottomSheetCell clearButton;
    private EditTextBoldCursor editText;

    /* access modifiers changed from: protected */
    public void onSend(int i, String str) {
        throw null;
    }

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
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
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ReportAlert(android.content.Context r19, int r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
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
            org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
            r5.<init>(r1)
            r7 = 2131558505(0x7f0d0069, float:1.8742328E38)
            r8 = 120(0x78, float:1.68E-43)
            r5.setAnimation(r7, r8, r8)
            r5.playAnimation()
            r9 = 160(0xa0, float:2.24E-43)
            r10 = 1126170624(0x43200000, float:160.0)
            r11 = 49
            r12 = 1099431936(0x41880000, float:17.0)
            r13 = 1096810496(0x41600000, float:14.0)
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r6.addView(r5, r7)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r7)
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            r5.setTextSize(r3, r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setTextColor(r7)
            r7 = 5
            r8 = 6
            if (r2 != 0) goto L_0x007d
            r9 = 2131627677(0x7f0e0e9d, float:1.8882625E38)
            java.lang.String r10 = "ReportTitleSpam"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00ca
        L_0x007d:
            if (r2 != r8) goto L_0x008c
            r9 = 2131627675(0x7f0e0e9b, float:1.8882621E38)
            java.lang.String r10 = "ReportTitleFake"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00ca
        L_0x008c:
            if (r2 != r3) goto L_0x009b
            r9 = 2131627678(0x7f0e0e9e, float:1.8882627E38)
            java.lang.String r10 = "ReportTitleViolence"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00ca
        L_0x009b:
            r9 = 2
            if (r2 != r9) goto L_0x00ab
            r9 = 2131627674(0x7f0e0e9a, float:1.888262E38)
            java.lang.String r10 = "ReportTitleChild"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00ca
        L_0x00ab:
            if (r2 != r7) goto L_0x00ba
            r9 = 2131627676(0x7f0e0e9c, float:1.8882623E38)
            java.lang.String r10 = "ReportTitlePornography"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00ca
        L_0x00ba:
            r9 = 100
            if (r2 != r9) goto L_0x00ca
            r9 = 2131627648(0x7f0e0e80, float:1.8882566E38)
            java.lang.String r10 = "ReportChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
        L_0x00ca:
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 49
            r13 = 1099431936(0x41880000, float:17.0)
            r14 = 1128595456(0x43450000, float:197.0)
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r6.addView(r5, r9)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r3, r9)
            java.lang.String r9 = "dialogTextGray3"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
            r5.setGravity(r3)
            r9 = 2131627660(0x7f0e0e8c, float:1.888259E38)
            java.lang.String r10 = "ReportInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            r10 = -2
            r13 = 1106247680(0x41var_, float:30.0)
            r14 = 1131085824(0x436b0000, float:235.0)
            r15 = 1106247680(0x41var_, float:30.0)
            r16 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r6.addView(r5, r9)
            org.telegram.ui.Components.EditTextBoldCursor r5 = new org.telegram.ui.Components.EditTextBoldCursor
            r5.<init>(r1)
            r0.editText = r5
            r9 = 1099956224(0x41900000, float:18.0)
            r5.setTextSize(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            java.lang.String r9 = "windowBackgroundWhiteBlackText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r10)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            r10 = 0
            r5.setBackgroundDrawable(r10)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            java.lang.String r11 = "windowBackgroundWhiteInputField"
            int r11 = r0.getThemedColor(r11)
            java.lang.String r12 = "windowBackgroundWhiteInputFieldActivated"
            int r12 = r0.getThemedColor(r12)
            java.lang.String r13 = "windowBackgroundWhiteRedText3"
            int r13 = r0.getThemedColor(r13)
            r5.setLineColors(r11, r12, r13)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            r5.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            r5.setLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r5 = r0.editText
            r5.setPadding(r4, r4, r4, r4)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r4.setSingleLine(r3)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x016a
            goto L_0x016b
        L_0x016a:
            r7 = 3
        L_0x016b:
            r4.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 180224(0x2CLASSNAME, float:2.52548E-40)
            r4.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r4.setImeOptions(r8)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 2131627659(0x7f0e0e8b, float:1.8882589E38)
            java.lang.String r7 = "ReportHint"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.setHint(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setCursorColor(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 1101004800(0x41a00000, float:20.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setCursorSize(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            r4.setCursorWidth(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda1
            r5.<init>(r0)
            r4.setOnEditorActionListener(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r11 = -1
            r12 = 1108344832(0x42100000, float:36.0)
            r13 = 51
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 1134067712(0x43988000, float:305.0)
            r16 = 1099431936(0x41880000, float:17.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r6.addView(r4, r5)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = new org.telegram.ui.Components.ReportAlert$BottomSheetCell
            r4.<init>(r1)
            r0.clearButton = r4
            r4.setBackground(r10)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r4 = 2131627664(0x7f0e0e90, float:1.8882599E38)
            java.lang.String r5 = "ReportSend"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Components.ReportAlert$$ExternalSyntheticLambda0
            r4.<init>(r0, r2)
            r1.setOnClickListener(r4)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r7 = -1
            r8 = 1112014848(0x42480000, float:50.0)
            r9 = 51
            r10 = 0
            r11 = 1135771648(0x43b28000, float:357.0)
            r12 = 0
            r13 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r6.addView(r1, r2)
            r0.smoothKeyboardAnimationEnabled = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReportAlert.<init>(android.content.Context, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.clearButton.background.callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i, View view) {
        AndroidUtilities.hideKeyboard(this.editText);
        onSend(i, this.editText.getText().toString());
        dismiss();
    }
}
