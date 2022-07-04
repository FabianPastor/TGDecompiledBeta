package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class JoinCallByUrlAlert extends BottomSheet {
    private boolean joinAfterDismiss;

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public View background;
        private LinearLayout linearLayout;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
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
    public JoinCallByUrlAlert(android.content.Context r20, org.telegram.tgnet.TLRPC.Chat r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = 1
            r0.<init>(r1, r3)
            r4 = 0
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r5.setOrientation(r3)
            r0.setCustomView(r5)
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            r6.<init>(r1)
            r7 = 1110704128(0x42340000, float:45.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setRoundRadius(r7)
            r8 = 90
            r9 = 90
            r10 = 49
            r11 = 0
            r12 = 29
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r5.addView(r6, r7)
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            r7.<init>((org.telegram.tgnet.TLRPC.Chat) r2)
            r6.setForUserOrChat(r2, r7)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r8.setTypeface(r9)
            r9 = 1099956224(0x41900000, float:18.0)
            r8.setTextSize(r3, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r8.setTextColor(r9)
            r8.setGravity(r3)
            r10 = -2
            r11 = -2
            r12 = 49
            r13 = 17
            r14 = 24
            r15 = 17
            r16 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r5.addView(r8, r9)
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r10 = 1096810496(0x41600000, float:14.0)
            r9.setTextSize(r3, r10)
            java.lang.String r10 = "dialogTextGray3"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r9.setTextColor(r10)
            r9.setGravity(r3)
            r12 = -2
            r13 = 49
            r14 = 30
            r15 = 8
            r16 = 30
            r17 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r5.addView(r9, r3)
            int r3 = r0.currentAccount
            org.telegram.messenger.AccountInstance r3 = org.telegram.messenger.AccountInstance.getInstance(r3)
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            long r10 = r2.id
            org.telegram.messenger.ChatObject$Call r3 = r3.getGroupCall(r10, r4)
            r10 = 2131626841(0x7f0e0b59, float:1.888093E38)
            java.lang.String r11 = "NoOneJoinedYet"
            if (r3 == 0) goto L_0x00ea
            org.telegram.tgnet.TLRPC$GroupCall r12 = r3.call
            java.lang.String r12 = r12.title
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 == 0) goto L_0x00c5
            java.lang.String r12 = r2.title
            r8.setText(r12)
            goto L_0x00cc
        L_0x00c5:
            org.telegram.tgnet.TLRPC$GroupCall r12 = r3.call
            java.lang.String r12 = r12.title
            r8.setText(r12)
        L_0x00cc:
            org.telegram.tgnet.TLRPC$GroupCall r12 = r3.call
            int r12 = r12.participants_count
            if (r12 != 0) goto L_0x00da
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r4)
            goto L_0x00f6
        L_0x00da:
            org.telegram.tgnet.TLRPC$GroupCall r10 = r3.call
            int r10 = r10.participants_count
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r11 = "Participants"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r4)
            r9.setText(r4)
            goto L_0x00f6
        L_0x00ea:
            java.lang.String r4 = r2.title
            r8.setText(r4)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r9.setText(r4)
        L_0x00f6:
            org.telegram.ui.Components.JoinCallByUrlAlert$BottomSheetCell r4 = new org.telegram.ui.Components.JoinCallByUrlAlert$BottomSheetCell
            r4.<init>(r1)
            r10 = 0
            r4.setBackground(r10)
            boolean r10 = org.telegram.messenger.ChatObject.isChannelOrGiga(r21)
            if (r10 == 0) goto L_0x0112
            r10 = 2131628976(0x7f0e13b0, float:1.888526E38)
            java.lang.String r11 = "VoipChannelJoinVoiceChatUrl"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.setText(r10)
            goto L_0x011e
        L_0x0112:
            r10 = 2131629068(0x7f0e140c, float:1.8885446E38)
            java.lang.String r11 = "VoipGroupJoinVoiceChatUrl"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.setText(r10)
        L_0x011e:
            android.view.View r10 = r4.background
            org.telegram.ui.Components.JoinCallByUrlAlert$$ExternalSyntheticLambda0 r11 = new org.telegram.ui.Components.JoinCallByUrlAlert$$ExternalSyntheticLambda0
            r11.<init>(r0)
            r10.setOnClickListener(r11)
            r12 = -1
            r13 = 50
            r14 = 51
            r15 = 0
            r16 = 30
            r17 = 0
            r18 = 0
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r5.addView(r4, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinCallByUrlAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$Chat):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinCallByUrlAlert  reason: not valid java name */
    public /* synthetic */ void m1075lambda$new$0$orgtelegramuiComponentsJoinCallByUrlAlert(View v) {
        this.joinAfterDismiss = true;
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onJoin() {
    }

    public void dismissInternal() {
        super.dismissInternal();
        if (this.joinAfterDismiss) {
            onJoin();
        }
    }
}
