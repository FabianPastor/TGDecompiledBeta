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
        void onAutoDeleteHistory(int i, int i2);

        void onClearHistory(boolean z);

        /* renamed from: org.telegram.ui.Components.ClearHistoryAlert$ClearHistoryAlertDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onClearHistory(ClearHistoryAlertDelegate _this, boolean revoke) {
            }

            public static void $default$onAutoDeleteHistory(ClearHistoryAlertDelegate _this, int ttl, int action) {
            }
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private LinearLayout linearLayout;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x04ae  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x04bd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ClearHistoryAlert(android.content.Context r36, org.telegram.tgnet.TLRPC.User r37, org.telegram.tgnet.TLRPC.Chat r38, boolean r39, org.telegram.ui.ActionBar.Theme.ResourcesProvider r40) {
        /*
            r35 = this;
            r0 = r35
            r1 = r36
            r2 = r37
            r3 = r38
            r4 = r40
            r5 = 0
            r0.<init>(r1, r5, r4)
            r6 = 2
            int[] r7 = new int[r6]
            r0.location = r7
            r7 = r39 ^ 1
            r0.autoDeleteOnly = r7
            r0.setApplyBottomPadding(r5)
            if (r2 == 0) goto L_0x0030
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r2.id
            org.telegram.tgnet.TLRPC$UserFull r7 = r7.getUserFull(r8)
            if (r7 == 0) goto L_0x002d
            int r8 = r7.ttl_period
            goto L_0x002e
        L_0x002d:
            r8 = 0
        L_0x002e:
            r7 = r8
            goto L_0x0043
        L_0x0030:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r3.id
            org.telegram.tgnet.TLRPC$ChatFull r7 = r7.getChatFull(r8)
            if (r7 == 0) goto L_0x0041
            int r8 = r7.ttl_period
            goto L_0x0042
        L_0x0041:
            r8 = 0
        L_0x0042:
            r7 = r8
        L_0x0043:
            r8 = 3
            r9 = 1
            if (r7 != 0) goto L_0x004c
            r0.currentTimer = r5
            r0.newTimer = r5
            goto L_0x0064
        L_0x004c:
            r10 = 86400(0x15180, float:1.21072E-40)
            if (r7 != r10) goto L_0x0056
            r0.currentTimer = r9
            r0.newTimer = r9
            goto L_0x0064
        L_0x0056:
            r10 = 604800(0x93a80, float:8.47505E-40)
            if (r7 != r10) goto L_0x0060
            r0.currentTimer = r6
            r0.newTimer = r6
            goto L_0x0064
        L_0x0060:
            r0.currentTimer = r8
            r0.newTimer = r8
        L_0x0064:
            android.content.res.Resources r10 = r36.getResources()
            r11 = 2131166129(0x7var_b1, float:1.7946495E38)
            android.graphics.drawable.Drawable r10 = r10.getDrawable(r11)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            r0.shadowDrawable = r10
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            java.lang.String r12 = "dialogBackground"
            int r13 = r0.getThemedColor(r12)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r13, r14)
            r10.setColorFilter(r11)
            org.telegram.ui.Components.ClearHistoryAlert$1 r10 = new org.telegram.ui.Components.ClearHistoryAlert$1
            r10.<init>(r1)
            r10.setFillViewport(r9)
            r10.setWillNotDraw(r5)
            r10.setClipToPadding(r5)
            int r11 = r0.backgroundPaddingLeft
            int r13 = r0.backgroundPaddingLeft
            r10.setPadding(r11, r5, r13, r5)
            r0.containerView = r10
            org.telegram.ui.Components.ClearHistoryAlert$2 r11 = new org.telegram.ui.Components.ClearHistoryAlert$2
            r11.<init>(r1)
            r0.linearLayout = r11
            r11.setOrientation(r9)
            android.widget.LinearLayout r11 = r0.linearLayout
            r13 = 80
            r14 = -1
            r15 = -2
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createScroll(r14, r15, r13)
            r10.addView((android.view.View) r11, (android.view.ViewGroup.LayoutParams) r13)
            android.widget.LinearLayout r11 = r0.linearLayout
            r0.setCustomView(r11)
            int r11 = r0.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            long r16 = r11.getClientUserId()
            if (r2 == 0) goto L_0x00db
            boolean r11 = r2.bot
            if (r11 != 0) goto L_0x00db
            r13 = r7
            long r6 = r2.id
            int r18 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x00dc
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            boolean r6 = r6.canRevokePmInbox
            if (r6 == 0) goto L_0x00dc
            r6 = 1
            goto L_0x00dd
        L_0x00db:
            r13 = r7
        L_0x00dc:
            r6 = 0
        L_0x00dd:
            if (r2 == 0) goto L_0x00e8
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.revokeTimePmLimit
            goto L_0x00f0
        L_0x00e8:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.revokeTimeLimit
        L_0x00f0:
            if (r2 == 0) goto L_0x00fb
            if (r6 == 0) goto L_0x00fb
            r11 = 2147483647(0x7fffffff, float:NaN)
            if (r7 != r11) goto L_0x00fb
            r11 = 1
            goto L_0x00fc
        L_0x00fb:
            r11 = 0
        L_0x00fc:
            boolean[] r8 = new boolean[r9]
            r8[r5] = r5
            r19 = 0
            boolean r14 = r0.autoDeleteOnly
            java.lang.String r15 = "windowBackgroundGray"
            java.lang.String r5 = "windowBackgroundGrayShadow"
            java.lang.String r21 = "fonts/rmedium.ttf"
            java.lang.String r9 = "dialogTextBlack"
            r22 = r6
            if (r14 != 0) goto L_0x032a
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r21)
            r14.setTypeface(r6)
            r23 = r7
            r6 = 1101004800(0x41a00000, float:20.0)
            r7 = 1
            r14.setTextSize(r7, r6)
            int r6 = r0.getThemedColor(r9)
            r14.setTextColor(r6)
            r6 = 2131625057(0x7f0e0461, float:1.8877311E38)
            java.lang.String r7 = "ClearHistory"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r14.setText(r6)
            r6 = 1
            r14.setSingleLine(r6)
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
            r14.setEllipsize(r6)
            android.widget.LinearLayout r6 = r0.linearLayout
            r24 = -2
            r25 = -2
            r26 = 51
            r27 = 23
            r28 = 20
            r29 = 23
            r30 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r6.addView(r14, r7)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r7 = r35.getContext()
            r6.<init>(r7)
            int r7 = r0.getThemedColor(r9)
            r6.setTextColor(r7)
            r7 = 1098907648(0x41800000, float:16.0)
            r9 = 1
            r6.setTextSize(r9, r7)
            org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy r9 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy
            r9.<init>()
            r6.setMovementMethod(r9)
            java.lang.String r9 = "dialogTextLink"
            int r9 = r0.getThemedColor(r9)
            r6.setLinkTextColor(r9)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0184
            r9 = 5
            goto L_0x0185
        L_0x0184:
            r9 = 3
        L_0x0185:
            r9 = r9 | 48
            r6.setGravity(r9)
            android.widget.LinearLayout r9 = r0.linearLayout
            r24 = -2
            r25 = -2
            r26 = 51
            r27 = 23
            r28 = 16
            r29 = 23
            r30 = 5
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r9.addView(r6, r7)
            if (r2 == 0) goto L_0x01c1
            r9 = 1
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r37)
            r20 = 0
            r7[r20] = r9
            java.lang.String r9 = "AreYouSureClearHistoryWithUser"
            r25 = r13
            r13 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r9, r13, r7)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            r6.setText(r7)
            goto L_0x020d
        L_0x01c1:
            r25 = r13
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r38)
            if (r7 == 0) goto L_0x01f4
            boolean r7 = r3.megagroup
            if (r7 == 0) goto L_0x01d6
            java.lang.String r7 = r3.username
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x01d6
            goto L_0x01f4
        L_0x01d6:
            boolean r7 = r3.megagroup
            if (r7 == 0) goto L_0x01e7
            r7 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r9 = "AreYouSureClearHistoryGroup"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setText(r7)
            goto L_0x020d
        L_0x01e7:
            r7 = 2131624368(0x7f0e01b0, float:1.8875914E38)
            java.lang.String r9 = "AreYouSureClearHistoryChannel"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setText(r7)
            goto L_0x020d
        L_0x01f4:
            r7 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            r9 = 1
            java.lang.Object[] r13 = new java.lang.Object[r9]
            java.lang.String r9 = r3.title
            r20 = 0
            r13[r20] = r9
            java.lang.String r9 = "AreYouSureClearHistoryWithChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r9, r7, r13)
            android.text.SpannableStringBuilder r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
            r6.setText(r7)
        L_0x020d:
            if (r11 == 0) goto L_0x028b
            boolean r7 = org.telegram.messenger.UserObject.isDeleted(r37)
            if (r7 != 0) goto L_0x028b
            org.telegram.ui.Cells.CheckBoxCell r7 = new org.telegram.ui.Cells.CheckBoxCell
            r9 = 1
            r7.<init>(r1, r9, r4)
            r0.cell = r7
            r13 = 0
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r13)
            r7.setBackgroundDrawable(r9)
            org.telegram.ui.Cells.CheckBoxCell r7 = r0.cell
            r24 = r6
            r9 = 1
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r9 = org.telegram.messenger.UserObject.getFirstName(r37)
            r6[r13] = r9
            java.lang.String r9 = "ClearHistoryOptionAlso"
            r13 = 2131625061(0x7f0e0465, float:1.887732E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r13, r6)
            java.lang.String r9 = ""
            r13 = 0
            r7.setText(r6, r9, r13, r13)
            org.telegram.ui.Cells.CheckBoxCell r6 = r0.cell
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            r9 = 1084227584(0x40a00000, float:5.0)
            if (r7 == 0) goto L_0x0250
            r7 = 1098907648(0x41800000, float:16.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x0254
        L_0x0250:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x0254:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x025d
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
            goto L_0x0263
        L_0x025d:
            r7 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
        L_0x0263:
            r9 = 0
            r6.setPadding(r13, r9, r7, r9)
            android.widget.LinearLayout r6 = r0.linearLayout
            org.telegram.ui.Cells.CheckBoxCell r7 = r0.cell
            r26 = -1
            r27 = 48
            r28 = 51
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32)
            r6.addView(r7, r9)
            org.telegram.ui.Cells.CheckBoxCell r6 = r0.cell
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda2
            r7.<init>(r8)
            r6.setOnClickListener(r7)
            goto L_0x028d
        L_0x028b:
            r24 = r6
        L_0x028d:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r6 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r6.<init>(r1, r4)
            r7 = 0
            r6.setBackground(r7)
            r7 = 2131624266(0x7f0e014a, float:1.8875707E38)
            java.lang.String r9 = "AlertClearHistory"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r6.setText(r7)
            android.view.View r7 = r6.background
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda0 r9 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda0
            r9.<init>(r0)
            r7.setOnClickListener(r9)
            android.widget.LinearLayout r7 = r0.linearLayout
            r26 = -1
            r27 = 50
            r28 = 51
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32)
            r7.addView(r6, r9)
            org.telegram.ui.Cells.ShadowSectionCell r7 = new org.telegram.ui.Cells.ShadowSectionCell
            r7.<init>(r1)
            r9 = 2131165483(0x7var_b, float:1.7945184E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r9, (java.lang.String) r5)
            org.telegram.ui.Components.CombinedDrawable r13 = new org.telegram.ui.Components.CombinedDrawable
            r21 = r6
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            r26 = r8
            int r8 = r0.getThemedColor(r15)
            r6.<init>(r8)
            r13.<init>(r6, r9)
            r6 = r13
            r8 = 1
            r6.setFullsize(r8)
            r7.setBackgroundDrawable(r6)
            android.widget.LinearLayout r8 = r0.linearLayout
            r27 = r6
            r28 = r9
            r6 = -1
            r13 = -2
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r13)
            r8.addView(r7, r9)
            org.telegram.ui.Cells.HeaderCell r6 = new org.telegram.ui.Cells.HeaderCell
            r6.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r8 = 2131624492(0x7f0e022c, float:1.8876165E38)
            java.lang.String r9 = "AutoDeleteHeader"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.setText(r8)
            android.widget.LinearLayout r8 = r0.linearLayout
            r29 = -1
            r30 = -2
            r31 = 1065353216(0x3var_, float:1.0)
            boolean r9 = r0.autoDeleteOnly
            if (r9 == 0) goto L_0x031a
            r32 = 1101004800(0x41a00000, float:20.0)
            goto L_0x031d
        L_0x031a:
            r9 = 0
            r32 = 0
        L_0x031d:
            r33 = 1065353216(0x3var_, float:1.0)
            r34 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r29, r30, r31, r32, r33, r34)
            r8.addView(r6, r9)
            goto L_0x0402
        L_0x032a:
            r23 = r7
            r26 = r8
            r25 = r13
            org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
            r6.<init>(r1)
            r7 = 0
            r6.setAutoRepeat(r7)
            r8 = 2131558554(0x7f0d009a, float:1.8742427E38)
            r13 = 120(0x78, float:1.68E-43)
            r6.setAnimation(r8, r13, r13)
            r8 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r6.setPadding(r7, r8, r7, r7)
            r6.playAnimation()
            android.widget.LinearLayout r7 = r0.linearLayout
            r27 = 160(0xa0, float:2.24E-43)
            r28 = 160(0xa0, float:2.24E-43)
            r29 = 49
            r30 = 17
            r31 = 0
            r32 = 17
            r33 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r7.addView(r6, r8)
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r21)
            r7.setTypeface(r8)
            r8 = 1103101952(0x41CLASSNAME, float:24.0)
            r13 = 1
            r7.setTextSize(r13, r8)
            int r8 = r0.getThemedColor(r9)
            r7.setTextColor(r8)
            r8 = 2131624487(0x7f0e0227, float:1.8876155E38)
            java.lang.String r9 = "AutoDeleteAlertTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7.setText(r8)
            android.widget.LinearLayout r8 = r0.linearLayout
            r27 = -2
            r28 = -2
            r31 = 18
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r8.addView(r7, r9)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r9 = 1096810496(0x41600000, float:14.0)
            r13 = 1
            r8.setTextSize(r13, r9)
            java.lang.String r9 = "dialogTextGray3"
            int r9 = r0.getThemedColor(r9)
            r8.setTextColor(r9)
            r8.setGravity(r13)
            if (r2 == 0) goto L_0x03c8
            r9 = 2131624488(0x7f0e0228, float:1.8876157E38)
            java.lang.Object[] r14 = new java.lang.Object[r13]
            java.lang.String r13 = org.telegram.messenger.UserObject.getFirstName(r37)
            r20 = 0
            r14[r20] = r13
            java.lang.String r13 = "AutoDeleteAlertUserInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r13, r9, r14)
            r8.setText(r9)
            goto L_0x03eb
        L_0x03c8:
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r38)
            if (r9 == 0) goto L_0x03df
            boolean r9 = r3.megagroup
            if (r9 != 0) goto L_0x03df
            r9 = 2131624485(0x7f0e0225, float:1.8876151E38)
            java.lang.String r13 = "AutoDeleteAlertChannelInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r8.setText(r9)
            goto L_0x03eb
        L_0x03df:
            r9 = 2131624486(0x7f0e0226, float:1.8876153E38)
            java.lang.String r13 = "AutoDeleteAlertGroupInfo"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r8.setText(r9)
        L_0x03eb:
            android.widget.LinearLayout r9 = r0.linearLayout
            r27 = -2
            r28 = -2
            r29 = 49
            r30 = 30
            r31 = 22
            r32 = 30
            r33 = 20
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r9.addView(r8, r13)
        L_0x0402:
            org.telegram.ui.Components.SlideChooseView r6 = new org.telegram.ui.Components.SlideChooseView
            r6.<init>(r1, r4)
            org.telegram.ui.Components.ClearHistoryAlert$3 r7 = new org.telegram.ui.Components.ClearHistoryAlert$3
            r7.<init>(r10)
            r6.setCallback(r7)
            r7 = 4
            java.lang.String[] r7 = new java.lang.String[r7]
            r8 = 2131624497(0x7f0e0231, float:1.8876175E38)
            java.lang.String r9 = "AutoDeleteNever"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 0
            r7[r9] = r8
            r8 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r9 = "AutoDelete24Hours"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 1
            r7[r9] = r8
            r8 = 2131624482(0x7f0e0222, float:1.8876145E38)
            java.lang.String r9 = "AutoDelete7Days"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 2
            r7[r9] = r8
            r8 = 2131624480(0x7f0e0220, float:1.887614E38)
            java.lang.String r9 = "AutoDelete1Month"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 3
            r7[r9] = r8
            int r8 = r0.currentTimer
            r6.setOptions(r8, r7)
            android.widget.LinearLayout r8 = r0.linearLayout
            r27 = -1
            r28 = -2
            r29 = 0
            r30 = 1090519040(0x41000000, float:8.0)
            r31 = 0
            r32 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r27, r28, r29, r30, r31, r32)
            r8.addView(r6, r9)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r9 = 2131165484(0x7var_c, float:1.7945186E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r9, (java.lang.String) r5)
            org.telegram.ui.Components.CombinedDrawable r9 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r13 = new android.graphics.drawable.ColorDrawable
            int r14 = r0.getThemedColor(r15)
            r13.<init>(r14)
            r9.<init>(r13, r5)
            r13 = 1
            r9.setFullsize(r13)
            r8.setBackgroundDrawable(r9)
            android.widget.LinearLayout r13 = r0.linearLayout
            r14 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r14)
            r13.addView(r8, r14)
            org.telegram.ui.Cells.TextInfoPrivacyCell r13 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            r13.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            r14 = 2131624496(0x7f0e0230, float:1.8876173E38)
            java.lang.String r15 = "AutoDeleteInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r13.setText(r14)
            r8.addView(r13)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r14 = new org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell
            r14.<init>(r1, r4)
            r0.setTimerButton = r14
            int r12 = r0.getThemedColor(r12)
            r14.setBackgroundColor(r12)
            boolean r12 = r0.autoDeleteOnly
            if (r12 == 0) goto L_0x04bd
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r12 = r0.setTimerButton
            r14 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r15 = "AutoDeleteSet"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r12.setText(r14)
            goto L_0x04e0
        L_0x04bd:
            if (r39 == 0) goto L_0x04d2
            int r12 = r0.currentTimer
            if (r12 != 0) goto L_0x04d2
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r12 = r0.setTimerButton
            r14 = 2131625523(0x7f0e0633, float:1.8878256E38)
            java.lang.String r15 = "EnableAutoDelete"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r12.setText(r14)
            goto L_0x04e0
        L_0x04d2:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r12 = r0.setTimerButton
            r14 = 2131624489(0x7f0e0229, float:1.887616E38)
            java.lang.String r15 = "AutoDeleteConfirm"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r12.setText(r14)
        L_0x04e0:
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r12 = r0.setTimerButton
            android.view.View r12 = r12.background
            org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda1 r14 = new org.telegram.ui.Components.ClearHistoryAlert$$ExternalSyntheticLambda1
            r14.<init>(r0)
            r12.setOnClickListener(r14)
            org.telegram.ui.Components.ClearHistoryAlert$BottomSheetCell r12 = r0.setTimerButton
            r8.addView(r12)
            r12 = 0
            r0.updateTimerButton(r12)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ClearHistoryAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ void lambda$new$0(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ClearHistoryAlert  reason: not valid java name */
    public /* synthetic */ void m3921lambda$new$1$orgtelegramuiComponentsClearHistoryAlert(View v) {
        if (!this.dismissedDelayed) {
            ClearHistoryAlertDelegate clearHistoryAlertDelegate = this.delegate;
            CheckBoxCell checkBoxCell = this.cell;
            clearHistoryAlertDelegate.onClearHistory(checkBoxCell != null && checkBoxCell.isChecked());
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ClearHistoryAlert  reason: not valid java name */
    public /* synthetic */ void m3922lambda$new$2$orgtelegramuiComponentsClearHistoryAlert(View v) {
        int action;
        int time;
        if (!this.dismissedDelayed) {
            int time2 = this.newTimer;
            if (time2 != this.currentTimer) {
                this.dismissedDelayed = true;
                if (time2 == 3) {
                    time = 2678400;
                    action = 70;
                } else if (time2 == 2) {
                    time = 604800;
                    action = 70;
                } else if (time2 == 1) {
                    time = 86400;
                    action = 70;
                } else {
                    time = 0;
                    action = 71;
                }
                this.delegate.onAutoDeleteHistory(time, action);
            }
            if (this.dismissedDelayed != 0) {
                AndroidUtilities.runOnUIThread(new ClearHistoryAlert$$ExternalSyntheticLambda3(this), 200);
            } else {
                dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTimerButton(boolean animated) {
        if (this.currentTimer != this.newTimer || this.autoDeleteOnly) {
            this.setTimerButton.setVisibility(0);
            if (animated) {
                this.setTimerButton.animate().alpha(1.0f).setDuration(180).start();
            } else {
                this.setTimerButton.setAlpha(1.0f);
            }
        } else if (animated) {
            this.setTimerButton.animate().alpha(0.0f).setDuration(180).start();
        } else {
            this.setTimerButton.setVisibility(4);
            this.setTimerButton.setAlpha(0.0f);
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        this.linearLayout.getChildAt(0).getLocationInWindow(this.location);
        int newOffset = Math.max(this.location[1] - AndroidUtilities.dp(this.autoDeleteOnly ? 6.0f : 19.0f), 0);
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.containerView.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void setDelegate(ClearHistoryAlertDelegate clearHistoryAlertDelegate) {
        this.delegate = clearHistoryAlertDelegate;
    }
}
