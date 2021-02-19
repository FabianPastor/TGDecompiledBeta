package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class GigagroupConvertAlert extends BottomSheet {
    /* access modifiers changed from: protected */
    public void onCancel() {
        throw null;
    }

    /* access modifiers changed from: protected */
    public void onCovert() {
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
    public GigagroupConvertAlert(android.content.Context r19, org.telegram.ui.ActionBar.BaseFragment r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
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
            r6 = 2131558490(0x7f0d005a, float:1.8742297E38)
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
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r5.setTypeface(r6)
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            r5.setTextSize(r2, r6)
            java.lang.String r6 = "dialogTextBlack"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r6)
            java.lang.String r6 = "GigagroupConvertTitle"
            r7 = 2131625671(0x7f0e06c7, float:1.8878557E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
            r5.setText(r6)
            r7 = -2
            r8 = -2
            r9 = 49
            r10 = 17
            r11 = 18
            r12 = 17
            r13 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13)
            r4.addView(r5, r6)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r5.setOrientation(r2)
            r6 = -2
            r8 = 1
            r9 = 0
            r10 = 12
            r11 = 0
            r12 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12)
            r4.addView(r5, r6)
            r6 = 0
        L_0x0092:
            r7 = 3
            if (r6 >= r7) goto L_0x0159
            android.widget.LinearLayout r8 = new android.widget.LinearLayout
            r8.<init>(r1)
            r8.setOrientation(r3)
            r9 = -2
            r10 = -2
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            r16 = 5
            if (r11 == 0) goto L_0x00a7
            r11 = 5
            goto L_0x00a8
        L_0x00a7:
            r11 = 3
        L_0x00a8:
            r12 = 0
            r13 = 8
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r8, r9)
            android.widget.ImageView r9 = new android.widget.ImageView
            r9.<init>(r1)
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "dialogTextGray3"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r10.<init>(r12, r13)
            r9.setColorFilter(r10)
            r10 = 2131165581(0x7var_d, float:1.7945383E38)
            r9.setImageResource(r10)
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            r12 = 1097859072(0x41700000, float:15.0)
            r10.setTextSize(r2, r12)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.setTextColor(r11)
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x00e5
            r7 = 5
        L_0x00e5:
            r7 = r7 | 16
            r10.setGravity(r7)
            r7 = 1132593152(0x43820000, float:260.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r10.setMaxWidth(r7)
            if (r6 == 0) goto L_0x0115
            if (r6 == r2) goto L_0x0108
            r7 = 2
            if (r6 == r7) goto L_0x00fb
            goto L_0x0121
        L_0x00fb:
            r7 = 2131625669(0x7f0e06c5, float:1.8878552E38)
            java.lang.String r11 = "GigagroupConvertInfo3"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r10.setText(r7)
            goto L_0x0121
        L_0x0108:
            r7 = 2131625668(0x7f0e06c4, float:1.887855E38)
            java.lang.String r11 = "GigagroupConvertInfo2"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r10.setText(r7)
            goto L_0x0121
        L_0x0115:
            r7 = 2131625667(0x7f0e06c3, float:1.8878548E38)
            java.lang.String r11 = "GigagroupConvertInfo1"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r10.setText(r7)
        L_0x0121:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            r11 = -2
            if (r7 == 0) goto L_0x013e
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r8.addView(r10, r7)
            r11 = -2
            r12 = -2
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 1088421888(0x40e00000, float:7.0)
            r15 = 0
            r16 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
            r8.addView(r9, r7)
            goto L_0x0155
        L_0x013e:
            r12 = -2
            r13 = -2
            r14 = 0
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 1090519040(0x41000000, float:8.0)
            r17 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r13, r14, r15, r16, r17)
            r8.addView(r9, r7)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r8.addView(r10, r7)
        L_0x0155:
            int r6 = r6 + 1
            goto L_0x0092
        L_0x0159:
            org.telegram.ui.Components.GigagroupConvertAlert$BottomSheetCell r3 = new org.telegram.ui.Components.GigagroupConvertAlert$BottomSheetCell
            r3.<init>(r1)
            r5 = 0
            r3.setBackground(r5)
            r5 = 2131625670(0x7f0e06c6, float:1.8878555E38)
            java.lang.String r6 = "GigagroupConvertProcessButton"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            android.view.View r5 = r3.background
            org.telegram.ui.Components.-$$Lambda$GigagroupConvertAlert$kt77ktBSsuOd-oO8XNnVNm3cDZo r6 = new org.telegram.ui.Components.-$$Lambda$GigagroupConvertAlert$kt77ktBSsuOd-oO8XNnVNm3cDZo
            r7 = r20
            r6.<init>(r1, r7)
            r5.setOnClickListener(r6)
            r7 = -1
            r8 = 50
            r9 = 51
            r10 = 0
            r11 = 29
            r12 = 0
            r13 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13)
            r4.addView(r3, r5)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r2, r1)
            java.lang.String r1 = "dialogTextBlue2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3.setTextColor(r1)
            r1 = 2131625665(0x7f0e06c1, float:1.8878544E38)
            java.lang.String r2 = "GigagroupConvertCancelButton"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r3.setText(r1)
            r1 = 17
            r3.setGravity(r1)
            r5 = -2
            r6 = 48
            r7 = 49
            r8 = 17
            r9 = 0
            r10 = 17
            r11 = 16
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11)
            r4.addView(r3, r1)
            org.telegram.ui.Components.-$$Lambda$GigagroupConvertAlert$kliefHjIkU1OPbmSJq1l8-8COjA r1 = new org.telegram.ui.Components.-$$Lambda$GigagroupConvertAlert$kliefHjIkU1OPbmSJq1l8-8COjA
            r1.<init>()
            r3.setOnClickListener(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GigagroupConvertAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GigagroupConvertAlert(Context context, BaseFragment baseFragment, View view) {
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("GigagroupConvertAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupConvertAlertText", NUM)));
        builder.setPositiveButton(LocaleController.getString("GigagroupConvertAlertConver", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                GigagroupConvertAlert.this.lambda$null$0$GigagroupConvertAlert(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        baseFragment.showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$GigagroupConvertAlert(DialogInterface dialogInterface, int i) {
        onCovert();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GigagroupConvertAlert(View view) {
        onCancel();
        dismiss();
    }
}
