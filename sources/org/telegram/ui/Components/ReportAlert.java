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
    public ReportAlert(android.content.Context r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = 1
            r0.<init>(r1, r3)
            r4 = 0
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            r0.setCustomView(r5)
            org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
            r6.<init>(r1)
            r6.setAutoRepeat(r3)
            r7 = 2131558466(0x7f0d0042, float:1.8742249E38)
            r8 = 120(0x78, float:1.68E-43)
            r6.setAnimation(r7, r8, r8)
            r6.playAnimation()
            r9 = 160(0xa0, float:2.24E-43)
            r10 = 1126170624(0x43200000, float:160.0)
            r11 = 49
            r12 = 1099431936(0x41880000, float:17.0)
            r13 = 1096810496(0x41600000, float:14.0)
            r14 = 1099431936(0x41880000, float:17.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r5.addView(r6, r7)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r7)
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            r6.setTextSize(r3, r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
            r7 = 5
            r8 = 3
            if (r2 != 0) goto L_0x006d
            r9 = 2131627135(0x7f0e0c7f, float:1.8881526E38)
            java.lang.String r10 = "ReportTitleSpam"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x00b9
        L_0x006d:
            if (r2 != r3) goto L_0x007c
            r9 = 2131627133(0x7f0e0c7d, float:1.8881522E38)
            java.lang.String r10 = "ReportTitleFake"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x00b9
        L_0x007c:
            r9 = 2
            if (r2 != r9) goto L_0x008c
            r9 = 2131627136(0x7f0e0CLASSNAME, float:1.8881528E38)
            java.lang.String r10 = "ReportTitleViolence"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x00b9
        L_0x008c:
            if (r2 != r8) goto L_0x009b
            r9 = 2131627132(0x7f0e0c7c, float:1.888152E38)
            java.lang.String r10 = "ReportTitleChild"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x00b9
        L_0x009b:
            r9 = 4
            if (r2 != r9) goto L_0x00ab
            r9 = 2131627134(0x7f0e0c7e, float:1.8881524E38)
            java.lang.String r10 = "ReportTitlePornography"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            goto L_0x00b9
        L_0x00ab:
            if (r2 != r7) goto L_0x00b9
            r9 = 2131627108(0x7f0e0CLASSNAME, float:1.8881471E38)
            java.lang.String r10 = "ReportChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
        L_0x00b9:
            r10 = -2
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 49
            r13 = 1099431936(0x41880000, float:17.0)
            r14 = 1128595456(0x43450000, float:197.0)
            r15 = 1099431936(0x41880000, float:17.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r9)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r3, r9)
            java.lang.String r9 = "dialogTextGray3"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r9)
            r6.setGravity(r3)
            r9 = 2131627118(0x7f0e0c6e, float:1.8881491E38)
            java.lang.String r10 = "ReportInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            r10 = -2
            r13 = 1106247680(0x41var_, float:30.0)
            r14 = 1131085824(0x436b0000, float:235.0)
            r15 = 1106247680(0x41var_, float:30.0)
            r16 = 1110441984(0x42300000, float:44.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r5.addView(r6, r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = new org.telegram.ui.Components.EditTextBoldCursor
            r6.<init>(r1)
            r0.editText = r6
            r9 = 1099956224(0x41900000, float:18.0)
            r6.setTextSize(r3, r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            java.lang.String r9 = "windowBackgroundWhiteHintText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setHintTextColor(r9)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            java.lang.String r9 = "windowBackgroundWhiteBlackText"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r10)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r4)
            r6.setBackgroundDrawable(r10)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            r6.setMaxLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            r6.setLines(r3)
            org.telegram.ui.Components.EditTextBoldCursor r6 = r0.editText
            r6.setPadding(r4, r4, r4, r4)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r4.setSingleLine(r3)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0145
            goto L_0x0146
        L_0x0145:
            r7 = 3
        L_0x0146:
            r3.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r4 = 180224(0x2CLASSNAME, float:2.52548E-40)
            r3.setInputType(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r4 = 6
            r3.setImeOptions(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r4 = 2131627117(0x7f0e0c6d, float:1.888149E38)
            java.lang.String r6 = "ReportHint"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setHint(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r3.setCursorColor(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setCursorSize(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r4 = 1069547520(0x3fCLASSNAME, float:1.5)
            r3.setCursorWidth(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            org.telegram.ui.Components.-$$Lambda$ReportAlert$AstKiJUdJqth6BECMQAvxwrJRIg r4 = new org.telegram.ui.Components.-$$Lambda$ReportAlert$AstKiJUdJqth6BECMQAvxwrJRIg
            r4.<init>()
            r3.setOnEditorActionListener(r4)
            org.telegram.ui.Components.EditTextBoldCursor r3 = r0.editText
            r6 = -1
            r7 = 1108344832(0x42100000, float:36.0)
            r8 = 51
            r9 = 1099431936(0x41880000, float:17.0)
            r10 = 1134067712(0x43988000, float:305.0)
            r11 = 1099431936(0x41880000, float:17.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r3, r4)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r3 = new org.telegram.ui.Components.ReportAlert$BottomSheetCell
            r3.<init>(r1)
            r0.clearButton = r3
            r1 = 0
            r3.setBackground(r1)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r3 = 2131627122(0x7f0e0CLASSNAME, float:1.88815E38)
            java.lang.String r4 = "ReportSend"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.-$$Lambda$ReportAlert$PciE8F7tTe4un-49oesSgLmsVNI r3 = new org.telegram.ui.Components.-$$Lambda$ReportAlert$PciE8F7tTe4un-49oesSgLmsVNI
            r3.<init>(r2)
            r1.setOnClickListener(r3)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r7 = 1112014848(0x42480000, float:50.0)
            r9 = 0
            r10 = 1135771648(0x43b28000, float:357.0)
            r11 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ReportAlert.<init>(android.content.Context, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ boolean lambda$new$0$ReportAlert(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.clearButton.background.callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ReportAlert(int i, View view) {
        AndroidUtilities.hideKeyboard(this.editText);
        onSend(i, this.editText.getText().toString());
        dismiss();
    }
}
