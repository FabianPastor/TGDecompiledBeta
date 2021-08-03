package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;

public class ClearHistoryAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public boolean autoDeleteOnly;
    private CheckBoxCell cell;
    private int currentTimer;
    private ClearHistoryAlertDelegate delegate;
    private boolean dismissedDelayed;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    private int[] location = new int[2];
    /* access modifiers changed from: private */
    public int newTimer;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    private BottomSheetCell setTimerButton;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;

    public interface ClearHistoryAlertDelegate {

        /* renamed from: org.telegram.ui.Components.ClearHistoryAlert$ClearHistoryAlertDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onClearHistory(ClearHistoryAlertDelegate clearHistoryAlertDelegate, boolean z) {
            }
        }

        void onAutoDeleteHistory(int i, int i2);

        void onClearHistory(boolean z);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
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
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0467  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0476  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ClearHistoryAlert(android.content.Context r27, org.telegram.tgnet.TLRPC$User r28, org.telegram.tgnet.TLRPC$Chat r29, boolean r30) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = 0
            r0.<init>(r1, r4)
            r5 = 2
            int[] r6 = new int[r5]
            r0.location = r6
            r6 = r30 ^ 1
            r0.autoDeleteOnly = r6
            r0.setApplyBottomPadding(r4)
            if (r2 == 0) goto L_0x002d
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r7 = r2.id
            org.telegram.tgnet.TLRPC$UserFull r6 = r6.getUserFull(r7)
            if (r6 == 0) goto L_0x002b
            int r6 = r6.ttl_period
            goto L_0x003d
        L_0x002b:
            r6 = 0
            goto L_0x003d
        L_0x002d:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r7 = r3.id
            org.telegram.tgnet.TLRPC$ChatFull r6 = r6.getChatFull(r7)
            if (r6 == 0) goto L_0x002b
            int r6 = r6.ttl_period
        L_0x003d:
            r7 = 3
            r8 = 1
            if (r6 != 0) goto L_0x0046
            r0.currentTimer = r4
            r0.newTimer = r4
            goto L_0x005e
        L_0x0046:
            r9 = 86400(0x15180, float:1.21072E-40)
            if (r6 != r9) goto L_0x0050
            r0.currentTimer = r8
            r0.newTimer = r8
            goto L_0x005e
        L_0x0050:
            r9 = 604800(0x93a80, float:8.47505E-40)
            if (r6 != r9) goto L_0x005a
            r0.currentTimer = r5
            r0.newTimer = r5
            goto L_0x005e
        L_0x005a:
            r0.currentTimer = r7
            r0.newTimer = r7
        L_0x005e:
            android.content.res.Resources r6 = r27.getResources()
            r9 = 2131166036(0x7var_, float:1.7946306E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r9)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r10 = "dialogBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r11, r12)
            r6.setColorFilter(r9)
            org.telegram.ui.Components.ClearHistoryAlert$1 r6 = new org.telegram.ui.Components.ClearHistoryAlert$1
            r6.<init>(r1)
            r6.setFillViewport(r8)
            r6.setWillNotDraw(r4)
            r6.setClipToPadding(r4)
            int r9 = r0.backgroundPaddingLeft
            r6.setPadding(r9, r4, r9, r4)
            r0.containerView = r6
            org.telegram.ui.Components.ClearHistoryAlert$2 r9 = new org.telegram.ui.Components.ClearHistoryAlert$2
            r9.<init>(r1)
            r0.linearLayout = r9
            r9.setOrientation(r8)
            android.widget.LinearLayout r9 = r0.linearLayout
            r11 = 80
            r12 = -1
            r13 = -2
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createScroll(r12, r13, r11)
            r6.addView((android.view.View) r9, (android.view.ViewGroup.LayoutParams) r11)
            android.widget.LinearLayout r9 = r0.linearLayout
            r0.setCustomView(r9)
            int r9 = r0.currentAccount
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            int r9 = r9.getClientUserId()
            if (r2 == 0) goto L_0x00d0
            boolean r11 = r2.bot
            if (r11 != 0) goto L_0x00d0
            int r11 = r2.id
            if (r11 == r9) goto L_0x00d0
            int r9 = r0.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            boolean r9 = r9.canRevokePmInbox
            if (r9 == 0) goto L_0x00d0
            r9 = 1
            goto L_0x00d1
        L_0x00d0:
            r9 = 0
        L_0x00d1:
            if (r2 == 0) goto L_0x00dc
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r11 = r11.revokeTimePmLimit
            goto L_0x00e4
        L_0x00dc:
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r11 = r11.revokeTimeLimit
        L_0x00e4:
            if (r2 == 0) goto L_0x00ef
            if (r9 == 0) goto L_0x00ef
            r9 = 2147483647(0x7fffffff, float:NaN)
            if (r11 != r9) goto L_0x00ef
            r9 = 1
            goto L_0x00f0
        L_0x00ef:
            r9 = 0
        L_0x00f0:
            boolean[] r11 = new boolean[r8]
            r11[r4] = r4
            boolean r14 = r0.autoDeleteOnly
            java.lang.String r15 = "windowBackgroundGray"
            java.lang.String r7 = "windowBackgroundGrayShadow"
            java.lang.String r16 = "fonts/rmedium.ttf"
            java.lang.String r17 = "dialogTextBlack"
            r5 = 1101004800(0x41a00000, float:20.0)
            if (r14 != 0) goto L_0x02f1
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r16)
            r14.setTypeface(r12)
            r14.setTextSize(r8, r5)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r14.setTextColor(r12)
            r12 = 2131624930(0x7f0e03e2, float:1.8877054E38)
            java.lang.String r5 = "ClearHistory"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r12)
            r14.setText(r5)
            r14.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r14.setEllipsize(r5)
            android.widget.LinearLayout r5 = r0.linearLayout
            r19 = -2
            r20 = -2
            r21 = 51
            r22 = 23
            r23 = 20
            r24 = 23
            r25 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r5.addView(r14, r12)
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r12 = r26.getContext()
            r5.<init>(r12)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r5.setTextColor(r12)
            r12 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r8, r12)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r14 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r14.<init>()
            r5.setMovementMethod(r14)
            java.lang.String r14 = "dialogTextLink"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r5.setLinkTextColor(r14)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x016f
            r14 = 5
            goto L_0x0170
        L_0x016f:
            r14 = 3
        L_0x0170:
            r14 = r14 | 48
            r5.setGravity(r14)
            android.widget.LinearLayout r14 = r0.linearLayout
            r19 = -2
            r20 = -2
            r21 = 51
            r22 = 23
            r23 = 16
            r24 = 23
            r25 = 5
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r14.addView(r5, r13)
            if (r2 == 0) goto L_0x01a7
            r3 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.Object[] r13 = new java.lang.Object[r8]
            java.lang.String r14 = org.telegram.messenger.UserObject.getUserName(r28)
            r13[r4] = r14
            java.lang.String r14 = "AreYouSureClearHistoryWithUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r14, r3, r13)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r5.setText(r3)
            goto L_0x01ee
        L_0x01a7:
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r29)
            if (r13 == 0) goto L_0x01d8
            boolean r13 = r3.megagroup
            if (r13 == 0) goto L_0x01ba
            java.lang.String r13 = r3.username
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x01ba
            goto L_0x01d8
        L_0x01ba:
            boolean r3 = r3.megagroup
            if (r3 == 0) goto L_0x01cb
            r3 = 2131624336(0x7f0e0190, float:1.8875849E38)
            java.lang.String r13 = "AreYouSureClearHistoryGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r5.setText(r3)
            goto L_0x01ee
        L_0x01cb:
            r3 = 2131624334(0x7f0e018e, float:1.8875845E38)
            java.lang.String r13 = "AreYouSureClearHistoryChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r5.setText(r3)
            goto L_0x01ee
        L_0x01d8:
            r13 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.Object[] r14 = new java.lang.Object[r8]
            java.lang.String r3 = r3.title
            r14[r4] = r3
            java.lang.String r3 = "AreYouSureClearHistoryWithChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r13, r14)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r5.setText(r3)
        L_0x01ee:
            if (r9 == 0) goto L_0x0260
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r28)
            if (r3 != 0) goto L_0x0260
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r3.<init>(r1, r8)
            r0.cell = r3
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r3.setBackgroundDrawable(r5)
            org.telegram.ui.Cells.CheckBoxCell r3 = r0.cell
            r5 = 2131624932(0x7f0e03e4, float:1.8877058E38)
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r28)
            r9[r4] = r2
            java.lang.String r2 = "ClearHistoryOptionAlso"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r5, r9)
            java.lang.String r5 = ""
            r3.setText(r2, r5, r4, r4)
            org.telegram.ui.Cells.CheckBoxCell r2 = r0.cell
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            r5 = 1084227584(0x40a00000, float:5.0)
            if (r3 == 0) goto L_0x0229
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x022d
        L_0x0229:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x022d:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0236
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x023a
        L_0x0236:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x023a:
            r2.setPadding(r3, r4, r5, r4)
            android.widget.LinearLayout r2 = r0.linearLayout
            org.telegram.ui.Cells.CheckBoxCell r3 = r0.cell
            r19 = -1
            r20 = 48
            r21 = 51
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r2.addView(r3, r5)
            org.telegram.ui.Cells.CheckBoxCell r2 = r0.cell
            org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$ozw5NBuDvAMeaemvo5DJ_10xH2A r3 = new org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$ozw5NBuDvAMeaemvo5DJ_10xH2A
            r3.<init>(r11)
            r2.setOnClickListener(r3)
        L_0x0260:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r2 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r2.<init>(r1)
            r3 = 0
            r2.setBackground(r3)
            r3 = 2131624240(0x7f0e0130, float:1.8875654E38)
            java.lang.String r5 = "AlertClearHistory"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setText(r3)
            android.view.View r3 = r2.background
            org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$PNvP59zQz0nepiY5Y7nSccm5u8s r5 = new org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$PNvP59zQz0nepiY5Y7nSccm5u8s
            r5.<init>()
            r3.setOnClickListener(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r19 = -1
            r20 = 50
            r21 = 51
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r3.addView(r2, r5)
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            r2.<init>(r1)
            r3 = 2131165443(0x7var_, float:1.7945103E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r7)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r9 = new android.graphics.drawable.ColorDrawable
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r9.<init>(r11)
            r5.<init>(r9, r3)
            r5.setFullsize(r8)
            r2.setBackgroundDrawable(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r5 = -2
            r9 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r5)
            r3.addView(r2, r11)
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>(r1)
            r3 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r5 = "AutoDeleteHeader"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setText(r3)
            android.widget.LinearLayout r3 = r0.linearLayout
            r20 = -2
            r21 = 1065353216(0x3var_, float:1.0)
            boolean r5 = r0.autoDeleteOnly
            if (r5 == 0) goto L_0x02e1
            r22 = 1101004800(0x41a00000, float:20.0)
            goto L_0x02e4
        L_0x02e1:
            r5 = 0
            r22 = 0
        L_0x02e4:
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r19, r20, r21, r22, r23, r24)
            r3.addView(r2, r5)
            goto L_0x03be
        L_0x02f1:
            org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
            r5.<init>(r1)
            r5.setAutoRepeat(r4)
            r9 = 2131558508(0x7f0d006c, float:1.8742334E38)
            r11 = 120(0x78, float:1.68E-43)
            r5.setAnimation(r9, r11, r11)
            r9 = 1101004800(0x41a00000, float:20.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r5.setPadding(r4, r9, r4, r4)
            r5.playAnimation()
            android.widget.LinearLayout r9 = r0.linearLayout
            r18 = 160(0xa0, float:2.24E-43)
            r19 = 160(0xa0, float:2.24E-43)
            r20 = 49
            r21 = 17
            r22 = 0
            r23 = 17
            r24 = 0
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r9.addView(r5, r11)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r16)
            r5.setTypeface(r9)
            r9 = 1103101952(0x41CLASSNAME, float:24.0)
            r5.setTextSize(r8, r9)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            r5.setTextColor(r9)
            r9 = 2131624439(0x7f0e01f7, float:1.8876058E38)
            java.lang.String r11 = "AutoDeleteAlertTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.setText(r9)
            android.widget.LinearLayout r9 = r0.linearLayout
            r18 = -2
            r19 = -2
            r22 = 18
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r9.addView(r5, r11)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r8, r9)
            java.lang.String r9 = "dialogTextGray3"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
            r5.setGravity(r8)
            if (r2 == 0) goto L_0x0384
            r3 = 2131624440(0x7f0e01f8, float:1.887606E38)
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r28)
            r9[r4] = r2
            java.lang.String r2 = "AutoDeleteAlertUserInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r9)
            r5.setText(r2)
            goto L_0x03a7
        L_0x0384:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r29)
            if (r2 == 0) goto L_0x039b
            boolean r2 = r3.megagroup
            if (r2 != 0) goto L_0x039b
            r2 = 2131624437(0x7f0e01f5, float:1.8876054E38)
            java.lang.String r3 = "AutoDeleteAlertChannelInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5.setText(r2)
            goto L_0x03a7
        L_0x039b:
            r2 = 2131624438(0x7f0e01f6, float:1.8876056E38)
            java.lang.String r3 = "AutoDeleteAlertGroupInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5.setText(r2)
        L_0x03a7:
            android.widget.LinearLayout r2 = r0.linearLayout
            r18 = -2
            r19 = -2
            r20 = 49
            r21 = 30
            r22 = 22
            r23 = 30
            r24 = 20
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24)
            r2.addView(r5, r3)
        L_0x03be:
            org.telegram.ui.Components.SlideChooseView r2 = new org.telegram.ui.Components.SlideChooseView
            r2.<init>(r1)
            org.telegram.ui.Components.ClearHistoryAlert$3 r3 = new org.telegram.ui.Components.ClearHistoryAlert$3
            r3.<init>(r6)
            r2.setCallback(r3)
            r3 = 4
            java.lang.String[] r3 = new java.lang.String[r3]
            r5 = 2131624447(0x7f0e01ff, float:1.8876074E38)
            java.lang.String r6 = "AutoDeleteNever"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3[r4] = r5
            r5 = 2131624435(0x7f0e01f3, float:1.887605E38)
            java.lang.String r6 = "AutoDelete24Hours"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3[r8] = r5
            r5 = 2131624436(0x7f0e01f4, float:1.8876052E38)
            java.lang.String r6 = "AutoDelete7Days"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2
            r3[r6] = r5
            r5 = 2131624434(0x7f0e01f2, float:1.8876048E38)
            java.lang.String r6 = "AutoDelete1Month"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 3
            r3[r6] = r5
            int r5 = r0.currentTimer
            r2.setOptions(r5, r3)
            android.widget.LinearLayout r3 = r0.linearLayout
            r18 = -1
            r19 = -2
            r20 = 0
            r21 = 1090519040(0x41000000, float:8.0)
            r22 = 0
            r23 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r18, r19, r20, r21, r22, r23)
            r3.addView(r2, r5)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r3 = 2131165444(0x7var_, float:1.7945105E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r7)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r6.<init>(r7)
            r5.<init>(r6, r3)
            r5.setFullsize(r8)
            r2.setBackgroundDrawable(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r2, r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>(r1)
            r5 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r6 = "AutoDeleteInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            r2.addView(r3)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r3 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r3.<init>(r1)
            r0.setTimerButton = r3
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r3.setBackgroundColor(r1)
            boolean r1 = r0.autoDeleteOnly
            if (r1 == 0) goto L_0x0476
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131624448(0x7f0e0200, float:1.8876076E38)
            java.lang.String r5 = "AutoDeleteSet"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            goto L_0x0499
        L_0x0476:
            if (r30 == 0) goto L_0x048b
            int r1 = r0.currentTimer
            if (r1 != 0) goto L_0x048b
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131625329(0x7f0e0571, float:1.8877863E38)
            java.lang.String r5 = "EnableAutoDelete"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
            goto L_0x0499
        L_0x048b:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131624441(0x7f0e01f9, float:1.8876062E38)
            java.lang.String r5 = "AutoDeleteConfirm"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1.setText(r3)
        L_0x0499:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$FJH2Cqqib5uRwfEXJ2e1hdCaX58 r3 = new org.telegram.ui.Components.-$$Lambda$ClearHistoryAlert$FJH2Cqqib5uRwfEXJ2e1hdCaX58
            r3.<init>()
            r1.setOnClickListener(r3)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r2.addView(r1)
            r0.updateTimerButton(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ClearHistoryAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean):void");
    }

    static /* synthetic */ void lambda$new$0(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$ClearHistoryAlert(View view) {
        if (!this.dismissedDelayed) {
            ClearHistoryAlertDelegate clearHistoryAlertDelegate = this.delegate;
            CheckBoxCell checkBoxCell = this.cell;
            clearHistoryAlertDelegate.onClearHistory(checkBoxCell != null && checkBoxCell.isChecked());
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$ClearHistoryAlert(View view) {
        int i;
        if (!this.dismissedDelayed) {
            int i2 = this.newTimer;
            if (i2 != this.currentTimer) {
                this.dismissedDelayed = true;
                int i3 = 70;
                if (i2 == 3) {
                    i = 2678400;
                } else if (i2 == 2) {
                    i = 604800;
                } else if (i2 == 1) {
                    i = 86400;
                } else {
                    i = 0;
                    i3 = 71;
                }
                this.delegate.onAutoDeleteHistory(i, i3);
            }
            if (this.dismissedDelayed) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        ClearHistoryAlert.this.dismiss();
                    }
                }, 200);
            } else {
                dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTimerButton(boolean z) {
        if (this.currentTimer != this.newTimer || this.autoDeleteOnly) {
            this.setTimerButton.setVisibility(0);
            if (z) {
                this.setTimerButton.animate().alpha(1.0f).setDuration(180).start();
            } else {
                this.setTimerButton.setAlpha(1.0f);
            }
        } else if (z) {
            this.setTimerButton.animate().alpha(0.0f).setDuration(180).start();
        } else {
            this.setTimerButton.setVisibility(4);
            this.setTimerButton.setAlpha(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        this.linearLayout.getChildAt(0).getLocationInWindow(this.location);
        int max = Math.max(this.location[1] - AndroidUtilities.dp(this.autoDeleteOnly ? 6.0f : 19.0f), 0);
        if (this.scrollOffsetY != max) {
            this.scrollOffsetY = max;
            this.containerView.invalidate();
        }
    }

    public void setDelegate(ClearHistoryAlertDelegate clearHistoryAlertDelegate) {
        this.delegate = clearHistoryAlertDelegate;
    }
}
