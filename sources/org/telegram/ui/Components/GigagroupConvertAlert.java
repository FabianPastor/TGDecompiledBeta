package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class GigagroupConvertAlert extends BottomSheet {

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
    public GigagroupConvertAlert(android.content.Context r21, org.telegram.ui.ActionBar.BaseFragment r22) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = 1
            r0.<init>(r1, r2)
            r3 = 0
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r4.setOrientation(r2)
            r0.setCustomView(r4)
            org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
            r5.<init>(r1)
            r5.setAutoRepeat(r2)
            r6 = 2131558551(0x7f0d0097, float:1.874242E38)
            r7 = 120(0x78, float:1.68E-43)
            r5.setAnimation(r6, r7, r7)
            r5.playAnimation()
            r8 = 160(0xa0, float:2.24E-43)
            r9 = 160(0xa0, float:2.24E-43)
            r10 = 49
            r11 = 17
            r12 = 30
            r13 = 17
            r14 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r4.addView(r5, r6)
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r7)
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            r6.setTextSize(r2, r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
            java.lang.String r7 = "GigagroupConvertTitle"
            r8 = 2131625970(0x7f0e07f2, float:1.8879163E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)
            r6.setText(r7)
            r8 = -2
            r9 = -2
            r12 = 18
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r4.addView(r6, r7)
            android.widget.LinearLayout r7 = new android.widget.LinearLayout
            r7.<init>(r1)
            r7.setOrientation(r2)
            r10 = 1
            r11 = 0
            r12 = 12
            r13 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r4.addView(r7, r8)
            r8 = 0
        L_0x0089:
            r9 = 3
            if (r8 >= r9) goto L_0x0151
            android.widget.LinearLayout r10 = new android.widget.LinearLayout
            r10.<init>(r1)
            r10.setOrientation(r3)
            r11 = -2
            r12 = -2
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            r18 = 5
            if (r13 == 0) goto L_0x009e
            r13 = 5
            goto L_0x009f
        L_0x009e:
            r13 = 3
        L_0x009f:
            r14 = 0
            r15 = 8
            r16 = 0
            r17 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r7.addView(r10, r11)
            android.widget.ImageView r11 = new android.widget.ImageView
            r11.<init>(r1)
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            java.lang.String r13 = "dialogTextGray3"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r14, r15)
            r11.setColorFilter(r12)
            r12 = 2131165616(0x7var_b0, float:1.7945454E38)
            r11.setImageResource(r12)
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r14 = 1097859072(0x41700000, float:15.0)
            r12.setTextSize(r2, r14)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setTextColor(r13)
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x00de
            r9 = 5
        L_0x00de:
            r9 = r9 | 16
            r12.setGravity(r9)
            r9 = 1132593152(0x43820000, float:260.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r12.setMaxWidth(r9)
            switch(r8) {
                case 0: goto L_0x010a;
                case 1: goto L_0x00fd;
                case 2: goto L_0x00f0;
                default: goto L_0x00ef;
            }
        L_0x00ef:
            goto L_0x0117
        L_0x00f0:
            r9 = 2131625968(0x7f0e07f0, float:1.8879159E38)
            java.lang.String r13 = "GigagroupConvertInfo3"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r12.setText(r9)
            goto L_0x0117
        L_0x00fd:
            r9 = 2131625967(0x7f0e07ef, float:1.8879157E38)
            java.lang.String r13 = "GigagroupConvertInfo2"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r12.setText(r9)
            goto L_0x0117
        L_0x010a:
            r9 = 2131625966(0x7f0e07ee, float:1.8879155E38)
            java.lang.String r13 = "GigagroupConvertInfo1"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r12.setText(r9)
        L_0x0117:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            r13 = -2
            if (r9 == 0) goto L_0x0135
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r13)
            r10.addView(r12, r9)
            r13 = -2
            r14 = -2
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 1088421888(0x40e00000, float:7.0)
            r17 = 0
            r18 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r14, r15, r16, r17, r18)
            r10.addView(r11, r9)
            goto L_0x014d
        L_0x0135:
            r14 = -2
            r15 = -2
            r16 = 0
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1090519040(0x41000000, float:8.0)
            r19 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19)
            r10.addView(r11, r9)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r13)
            r10.addView(r12, r9)
        L_0x014d:
            int r8 = r8 + 1
            goto L_0x0089
        L_0x0151:
            org.telegram.ui.Components.GigagroupConvertAlert$BottomSheetCell r3 = new org.telegram.ui.Components.GigagroupConvertAlert$BottomSheetCell
            r3.<init>(r1)
            r8 = 0
            r3.setBackground(r8)
            r8 = 2131625969(0x7f0e07f1, float:1.887916E38)
            java.lang.String r9 = "GigagroupConvertProcessButton"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r3.setText(r8)
            android.view.View r8 = r3.background
            org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda2
            r10 = r22
            r9.<init>(r0, r1, r10)
            r8.setOnClickListener(r9)
            r11 = -1
            r12 = 50
            r13 = 51
            r14 = 0
            r15 = 29
            r16 = 0
            r17 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r4.addView(r3, r8)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r8.setTextSize(r2, r9)
            java.lang.String r2 = "dialogTextBlue2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r8.setTextColor(r2)
            r2 = 2131625964(0x7f0e07ec, float:1.887915E38)
            java.lang.String r9 = "GigagroupConvertCancelButton"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r8.setText(r2)
            r2 = 17
            r8.setGravity(r2)
            r11 = -2
            r12 = 48
            r13 = 49
            r14 = 17
            r15 = 0
            r16 = 17
            r17 = 16
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r4.addView(r8, r2)
            org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.GigagroupConvertAlert$$ExternalSyntheticLambda1
            r2.<init>(r0)
            r8.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GigagroupConvertAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GigagroupConvertAlert  reason: not valid java name */
    public /* synthetic */ void m4035lambda$new$1$orgtelegramuiComponentsGigagroupConvertAlert(Context context, BaseFragment parentFragment, View v) {
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("GigagroupConvertAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupConvertAlertText", NUM)));
        builder.setPositiveButton(LocaleController.getString("GigagroupConvertAlertConver", NUM), new GigagroupConvertAlert$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        parentFragment.showDialog(builder.create());
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GigagroupConvertAlert  reason: not valid java name */
    public /* synthetic */ void m4034lambda$new$0$orgtelegramuiComponentsGigagroupConvertAlert(DialogInterface dialogInterface, int i) {
        onCovert();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-GigagroupConvertAlert  reason: not valid java name */
    public /* synthetic */ void m4036lambda$new$2$orgtelegramuiComponentsGigagroupConvertAlert(View v) {
        onCancel();
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCovert() {
    }

    /* access modifiers changed from: protected */
    public void onCancel() {
    }
}
