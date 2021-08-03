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
            r7 = 2131558474(0x7f0d004a, float:1.8742265E38)
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
            r8 = 3
            if (r2 != 0) goto L_0x007d
            r9 = 2131627250(0x7f0e0cf2, float:1.888176E38)
            java.lang.String r10 = "ReportTitleSpam"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00c9
        L_0x007d:
            if (r2 != r3) goto L_0x008c
            r9 = 2131627248(0x7f0e0cf0, float:1.8881755E38)
            java.lang.String r10 = "ReportTitleFake"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00c9
        L_0x008c:
            r9 = 2
            if (r2 != r9) goto L_0x009c
            r9 = 2131627251(0x7f0e0cf3, float:1.8881761E38)
            java.lang.String r10 = "ReportTitleViolence"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00c9
        L_0x009c:
            if (r2 != r8) goto L_0x00ab
            r9 = 2131627247(0x7f0e0cef, float:1.8881753E38)
            java.lang.String r10 = "ReportTitleChild"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00c9
        L_0x00ab:
            r9 = 4
            if (r2 != r9) goto L_0x00bb
            r9 = 2131627249(0x7f0e0cf1, float:1.8881757E38)
            java.lang.String r10 = "ReportTitlePornography"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x00c9
        L_0x00bb:
            if (r2 != r7) goto L_0x00c9
            r9 = 2131627223(0x7f0e0cd7, float:1.8881704E38)
            java.lang.String r10 = "ReportChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
        L_0x00c9:
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
            r9 = 2131627233(0x7f0e0ce1, float:1.8881725E38)
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
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r1, r4)
            r5.setBackgroundDrawable(r10)
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
            if (r5 == 0) goto L_0x0155
            goto L_0x0156
        L_0x0155:
            r7 = 3
        L_0x0156:
            r4.setGravity(r7)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 180224(0x2CLASSNAME, float:2.52548E-40)
            r4.setInputType(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 6
            r4.setImeOptions(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r5 = 2131627232(0x7f0e0ce0, float:1.8881723E38)
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
            org.telegram.ui.Components.-$$Lambda$ReportAlert$AstKiJUdJqth6BECMQAvxwrJRIg r5 = new org.telegram.ui.Components.-$$Lambda$ReportAlert$AstKiJUdJqth6BECMQAvxwrJRIg
            r5.<init>()
            r4.setOnEditorActionListener(r5)
            org.telegram.ui.Components.EditTextBoldCursor r4 = r0.editText
            r7 = -1
            r8 = 1108344832(0x42100000, float:36.0)
            r9 = 51
            r10 = 1099431936(0x41880000, float:17.0)
            r11 = 1134067712(0x43988000, float:305.0)
            r12 = 1099431936(0x41880000, float:17.0)
            r13 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r6.addView(r4, r5)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r4 = new org.telegram.ui.Components.ReportAlert$BottomSheetCell
            r4.<init>(r1)
            r0.clearButton = r4
            r1 = 0
            r4.setBackground(r1)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r4 = 2131627237(0x7f0e0ce5, float:1.8881733E38)
            java.lang.String r5 = "ReportSend"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.-$$Lambda$ReportAlert$PciE8F7tTe4un-49oesSgLmsVNI r4 = new org.telegram.ui.Components.-$$Lambda$ReportAlert$PciE8F7tTe4un-49oesSgLmsVNI
            r4.<init>(r2)
            r1.setOnClickListener(r4)
            org.telegram.ui.Components.ReportAlert$BottomSheetCell r1 = r0.clearButton
            r8 = 1112014848(0x42480000, float:50.0)
            r10 = 0
            r11 = 1135771648(0x43b28000, float:357.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r6.addView(r1, r2)
            r0.smoothKeyboardAnimationEnabled = r3
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
