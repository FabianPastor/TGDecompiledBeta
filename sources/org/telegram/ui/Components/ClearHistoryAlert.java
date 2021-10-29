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
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("featuredStickers_addButton"), getThemedColor("featuredStickers_addButtonPressed")));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
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
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ec A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0474  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0483  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ClearHistoryAlert(android.content.Context r28, org.telegram.tgnet.TLRPC$User r29, org.telegram.tgnet.TLRPC$Chat r30, boolean r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = r32
            r5 = 0
            r0.<init>(r1, r5, r4)
            r6 = 2
            int[] r7 = new int[r6]
            r0.location = r7
            r7 = r31 ^ 1
            r0.autoDeleteOnly = r7
            r0.setApplyBottomPadding(r5)
            if (r2 == 0) goto L_0x002f
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r2.id
            org.telegram.tgnet.TLRPC$UserFull r7 = r7.getUserFull(r8)
            if (r7 == 0) goto L_0x002d
            int r7 = r7.ttl_period
            goto L_0x003f
        L_0x002d:
            r7 = 0
            goto L_0x003f
        L_0x002f:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r3.id
            org.telegram.tgnet.TLRPC$ChatFull r7 = r7.getChatFull(r8)
            if (r7 == 0) goto L_0x002d
            int r7 = r7.ttl_period
        L_0x003f:
            r8 = 3
            r9 = 1
            if (r7 != 0) goto L_0x0048
            r0.currentTimer = r5
            r0.newTimer = r5
            goto L_0x0060
        L_0x0048:
            r10 = 86400(0x15180, float:1.21072E-40)
            if (r7 != r10) goto L_0x0052
            r0.currentTimer = r9
            r0.newTimer = r9
            goto L_0x0060
        L_0x0052:
            r10 = 604800(0x93a80, float:8.47505E-40)
            if (r7 != r10) goto L_0x005c
            r0.currentTimer = r6
            r0.newTimer = r6
            goto L_0x0060
        L_0x005c:
            r0.currentTimer = r8
            r0.newTimer = r8
        L_0x0060:
            android.content.res.Resources r7 = r28.getResources()
            r10 = 2131166054(0x7var_, float:1.7946343E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r10)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            r0.shadowDrawable = r7
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "dialogBackground"
            int r12 = r0.getThemedColor(r11)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r10.<init>(r12, r13)
            r7.setColorFilter(r10)
            org.telegram.ui.Components.ClearHistoryAlert$1 r7 = new org.telegram.ui.Components.ClearHistoryAlert$1
            r7.<init>(r1)
            r7.setFillViewport(r9)
            r7.setWillNotDraw(r5)
            r7.setClipToPadding(r5)
            int r10 = r0.backgroundPaddingLeft
            r7.setPadding(r10, r5, r10, r5)
            r0.containerView = r7
            org.telegram.ui.Components.ClearHistoryAlert$2 r10 = new org.telegram.ui.Components.ClearHistoryAlert$2
            r10.<init>(r1)
            r0.linearLayout = r10
            r10.setOrientation(r9)
            android.widget.LinearLayout r10 = r0.linearLayout
            r12 = 80
            r13 = -1
            r14 = -2
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createScroll(r13, r14, r12)
            r7.addView((android.view.View) r10, (android.view.ViewGroup.LayoutParams) r12)
            android.widget.LinearLayout r10 = r0.linearLayout
            r0.setCustomView(r10)
            int r10 = r0.currentAccount
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
            long r15 = r10.getClientUserId()
            if (r2 == 0) goto L_0x00d5
            boolean r10 = r2.bot
            if (r10 != 0) goto L_0x00d5
            r12 = r7
            long r6 = r2.id
            int r17 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
            if (r17 == 0) goto L_0x00d6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            boolean r6 = r6.canRevokePmInbox
            if (r6 == 0) goto L_0x00d6
            r6 = 1
            goto L_0x00d7
        L_0x00d5:
            r12 = r7
        L_0x00d6:
            r6 = 0
        L_0x00d7:
            if (r2 == 0) goto L_0x00e2
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.revokeTimePmLimit
            goto L_0x00ea
        L_0x00e2:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.revokeTimeLimit
        L_0x00ea:
            if (r2 == 0) goto L_0x00f5
            if (r6 == 0) goto L_0x00f5
            r6 = 2147483647(0x7fffffff, float:NaN)
            if (r7 != r6) goto L_0x00f5
            r6 = 1
            goto L_0x00f6
        L_0x00f5:
            r6 = 0
        L_0x00f6:
            boolean[] r7 = new boolean[r9]
            r7[r5] = r5
            boolean r15 = r0.autoDeleteOnly
            java.lang.String r10 = "windowBackgroundGray"
            java.lang.String r8 = "windowBackgroundGrayShadow"
            java.lang.String r18 = "fonts/rmedium.ttf"
            java.lang.String r13 = "dialogTextBlack"
            r14 = 1101004800(0x41a00000, float:20.0)
            if (r15 != 0) goto L_0x02fd
            android.widget.TextView r15 = new android.widget.TextView
            r15.<init>(r1)
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r15.setTypeface(r5)
            r15.setTextSize(r9, r14)
            int r5 = r0.getThemedColor(r13)
            r15.setTextColor(r5)
            r5 = 2131624962(0x7f0e0402, float:1.8877119E38)
            java.lang.String r14 = "ClearHistory"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r5)
            r15.setText(r5)
            r15.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r15.setEllipsize(r5)
            android.widget.LinearLayout r5 = r0.linearLayout
            r20 = -2
            r21 = -2
            r22 = 51
            r23 = 23
            r24 = 20
            r25 = 23
            r26 = 0
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r5.addView(r15, r14)
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r14 = r27.getContext()
            r5.<init>(r14)
            int r13 = r0.getThemedColor(r13)
            r5.setTextColor(r13)
            r13 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r9, r13)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r14 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r14.<init>()
            r5.setMovementMethod(r14)
            java.lang.String r14 = "dialogTextLink"
            int r14 = r0.getThemedColor(r14)
            r5.setLinkTextColor(r14)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x0175
            r14 = 5
            goto L_0x0176
        L_0x0175:
            r14 = 3
        L_0x0176:
            r14 = r14 | 48
            r5.setGravity(r14)
            android.widget.LinearLayout r14 = r0.linearLayout
            r20 = -2
            r21 = -2
            r22 = 51
            r23 = 23
            r24 = 16
            r25 = 23
            r26 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r14.addView(r5, r15)
            if (r2 == 0) goto L_0x01af
            r3 = 2131624348(0x7f0e019c, float:1.8875873E38)
            java.lang.Object[] r14 = new java.lang.Object[r9]
            java.lang.String r15 = org.telegram.messenger.UserObject.getUserName(r29)
            r18 = 0
            r14[r18] = r15
            java.lang.String r15 = "AreYouSureClearHistoryWithUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r15, r3, r14)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r5.setText(r3)
            goto L_0x01f8
        L_0x01af:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r30)
            if (r14 == 0) goto L_0x01e0
            boolean r14 = r3.megagroup
            if (r14 == 0) goto L_0x01c2
            java.lang.String r14 = r3.username
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 == 0) goto L_0x01c2
            goto L_0x01e0
        L_0x01c2:
            boolean r3 = r3.megagroup
            if (r3 == 0) goto L_0x01d3
            r3 = 2131624344(0x7f0e0198, float:1.8875865E38)
            java.lang.String r14 = "AreYouSureClearHistoryGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r5.setText(r3)
            goto L_0x01f8
        L_0x01d3:
            r3 = 2131624342(0x7f0e0196, float:1.887586E38)
            java.lang.String r14 = "AreYouSureClearHistoryChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r5.setText(r3)
            goto L_0x01f8
        L_0x01e0:
            r14 = 2131624346(0x7f0e019a, float:1.887587E38)
            java.lang.Object[] r15 = new java.lang.Object[r9]
            java.lang.String r3 = r3.title
            r18 = 0
            r15[r18] = r3
            java.lang.String r3 = "AreYouSureClearHistoryWithChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r14, r15)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r5.setText(r3)
        L_0x01f8:
            if (r6 == 0) goto L_0x026c
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r29)
            if (r3 != 0) goto L_0x026c
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r3.<init>(r1, r9, r4)
            r0.cell = r3
            r5 = 0
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.Cells.CheckBoxCell r3 = r0.cell
            r6 = 2131624964(0x7f0e0404, float:1.8877123E38)
            java.lang.Object[] r14 = new java.lang.Object[r9]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r29)
            r14[r5] = r2
            java.lang.String r2 = "ClearHistoryOptionAlso"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r14)
            java.lang.String r6 = ""
            r3.setText(r2, r6, r5, r5)
            org.telegram.ui.Cells.CheckBoxCell r2 = r0.cell
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            r5 = 1084227584(0x40a00000, float:5.0)
            if (r3 == 0) goto L_0x0234
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x0238
        L_0x0234:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x0238:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0241
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x0245
        L_0x0241:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x0245:
            r6 = 0
            r2.setPadding(r3, r6, r5, r6)
            android.widget.LinearLayout r2 = r0.linearLayout
            org.telegram.ui.Cells.CheckBoxCell r3 = r0.cell
            r20 = -1
            r21 = 48
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r2.addView(r3, r5)
            org.telegram.ui.Cells.CheckBoxCell r2 = r0.cell
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda2
            r3.<init>(r7)
            r2.setOnClickListener(r3)
        L_0x026c:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r2 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r2.<init>(r1, r4)
            r3 = 0
            r2.setBackground(r3)
            r3 = 2131624246(0x7f0e0136, float:1.8875666E38)
            java.lang.String r5 = "AlertClearHistory"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setText(r3)
            android.view.View r3 = r2.background
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda1
            r5.<init>(r0)
            r3.setOnClickListener(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r20 = -1
            r21 = 50
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r3.addView(r2, r5)
            org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
            r2.<init>(r1)
            r3 = 2131165450(0x7var_a, float:1.7945117E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r8)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r7 = r0.getThemedColor(r10)
            r6.<init>(r7)
            r5.<init>(r6, r3)
            r5.setFullsize(r9)
            r2.setBackgroundDrawable(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r2, r7)
            org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
            r2.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r3 = 2131624451(0x7f0e0203, float:1.8876082E38)
            java.lang.String r5 = "AutoDeleteHeader"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r2.setText(r3)
            android.widget.LinearLayout r3 = r0.linearLayout
            r21 = -2
            r22 = 1065353216(0x3var_, float:1.0)
            boolean r5 = r0.autoDeleteOnly
            if (r5 == 0) goto L_0x02ed
            r23 = 1101004800(0x41a00000, float:20.0)
            goto L_0x02f0
        L_0x02ed:
            r14 = 0
            r23 = 0
        L_0x02f0:
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r20, r21, r22, r23, r24, r25)
            r3.addView(r2, r5)
            goto L_0x03cc
        L_0x02fd:
            org.telegram.ui.Components.RLottieImageView r5 = new org.telegram.ui.Components.RLottieImageView
            r5.<init>(r1)
            r6 = 0
            r5.setAutoRepeat(r6)
            r7 = 2131558514(0x7f0d0072, float:1.8742346E38)
            r14 = 120(0x78, float:1.68E-43)
            r5.setAnimation(r7, r14, r14)
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.setPadding(r6, r7, r6, r6)
            r5.playAnimation()
            android.widget.LinearLayout r6 = r0.linearLayout
            r19 = 160(0xa0, float:2.24E-43)
            r20 = 160(0xa0, float:2.24E-43)
            r21 = 49
            r22 = 17
            r23 = 0
            r24 = 17
            r25 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r6.addView(r5, r7)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r5.setTypeface(r6)
            r6 = 1103101952(0x41CLASSNAME, float:24.0)
            r5.setTextSize(r9, r6)
            int r6 = r0.getThemedColor(r13)
            r5.setTextColor(r6)
            r6 = 2131624448(0x7f0e0200, float:1.8876076E38)
            java.lang.String r7 = "AutoDeleteAlertTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5.setText(r6)
            android.widget.LinearLayout r6 = r0.linearLayout
            r19 = -2
            r20 = -2
            r23 = 18
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r6.addView(r5, r7)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r6 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r9, r6)
            java.lang.String r6 = "dialogTextGray3"
            int r6 = r0.getThemedColor(r6)
            r5.setTextColor(r6)
            r5.setGravity(r9)
            if (r2 == 0) goto L_0x0392
            r3 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r29)
            r7 = 0
            r6[r7] = r2
            java.lang.String r2 = "AutoDeleteAlertUserInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r6)
            r5.setText(r2)
            goto L_0x03b5
        L_0x0392:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r30)
            if (r2 == 0) goto L_0x03a9
            boolean r2 = r3.megagroup
            if (r2 != 0) goto L_0x03a9
            r2 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r3 = "AutoDeleteAlertChannelInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5.setText(r2)
            goto L_0x03b5
        L_0x03a9:
            r2 = 2131624447(0x7f0e01ff, float:1.8876074E38)
            java.lang.String r3 = "AutoDeleteAlertGroupInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r5.setText(r2)
        L_0x03b5:
            android.widget.LinearLayout r2 = r0.linearLayout
            r19 = -2
            r20 = -2
            r21 = 49
            r22 = 30
            r23 = 22
            r24 = 30
            r25 = 20
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r19, (int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25)
            r2.addView(r5, r3)
        L_0x03cc:
            org.telegram.ui.Components.SlideChooseView r2 = new org.telegram.ui.Components.SlideChooseView
            r2.<init>(r1, r4)
            org.telegram.ui.Components.ClearHistoryAlert$3 r3 = new org.telegram.ui.Components.ClearHistoryAlert$3
            r5 = r12
            r3.<init>(r5)
            r2.setCallback(r3)
            r3 = 4
            java.lang.String[] r3 = new java.lang.String[r3]
            r5 = 2131624456(0x7f0e0208, float:1.8876092E38)
            java.lang.String r6 = "AutoDeleteNever"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 0
            r3[r6] = r5
            r5 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r6 = "AutoDelete24Hours"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3[r9] = r5
            r5 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r6 = "AutoDelete7Days"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2
            r3[r6] = r5
            r5 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r6 = "AutoDelete1Month"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 3
            r3[r6] = r5
            int r5 = r0.currentTimer
            r2.setOptions(r5, r3)
            android.widget.LinearLayout r3 = r0.linearLayout
            r12 = -1
            r13 = -2
            r14 = 0
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 0
            r17 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r13, r14, r15, r16, r17)
            r3.addView(r2, r5)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r3 = 2131165451(0x7var_b, float:1.794512E38)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r8)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r7 = r0.getThemedColor(r10)
            r6.<init>(r7)
            r5.<init>(r6, r3)
            r5.setFullsize(r9)
            r2.setBackgroundDrawable(r5)
            android.widget.LinearLayout r3 = r0.linearLayout
            r5 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r5)
            r3.addView(r2, r5)
            org.telegram.ui.Cells.TextInfoPrivacyCell r3 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r3.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r5 = 2131624455(0x7f0e0207, float:1.887609E38)
            java.lang.String r6 = "AutoDeleteInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setText(r5)
            r2.addView(r3)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r3 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r3.<init>(r1, r4)
            r0.setTimerButton = r3
            int r1 = r0.getThemedColor(r11)
            r3.setBackgroundColor(r1)
            boolean r1 = r0.autoDeleteOnly
            if (r1 == 0) goto L_0x0483
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131624457(0x7f0e0209, float:1.8876094E38)
            java.lang.String r4 = "AutoDeleteSet"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            goto L_0x04a6
        L_0x0483:
            if (r31 == 0) goto L_0x0498
            int r1 = r0.currentTimer
            if (r1 != 0) goto L_0x0498
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131625367(0x7f0e0597, float:1.887794E38)
            java.lang.String r4 = "EnableAutoDelete"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            goto L_0x04a6
        L_0x0498:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r3 = 2131624450(0x7f0e0202, float:1.887608E38)
            java.lang.String r4 = "AutoDeleteConfirm"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
        L_0x04a6:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            android.view.View r1 = r1.background
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r1 = r0.setTimerButton
            r2.addView(r1)
            r1 = 0
            r0.updateTimerButton(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ClearHistoryAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$0(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (!this.dismissedDelayed) {
            ClearHistoryAlertDelegate clearHistoryAlertDelegate = this.delegate;
            CheckBoxCell checkBoxCell = this.cell;
            clearHistoryAlertDelegate.onClearHistory(checkBoxCell != null && checkBoxCell.isChecked());
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
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
                AndroidUtilities.runOnUIThread(new ClearHistoryAlert$$ExternalSyntheticLambda3(this), 200);
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
