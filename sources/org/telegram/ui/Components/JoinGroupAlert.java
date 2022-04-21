package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;

public class JoinGroupAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public TLRPC.ChatInvite chatInvite;
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r37, org.telegram.tgnet.TLRPC.ChatInvite r38, java.lang.String r39, org.telegram.ui.ActionBar.BaseFragment r40) {
        /*
            r36 = this;
            r0 = r36
            r1 = r37
            r2 = r38
            r3 = 0
            r0.<init>(r1, r3)
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r4 = r40
            r0.fragment = r4
            r0.chatInvite = r2
            r5 = r39
            r0.hash = r5
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r7 = 1
            r6.setOrientation(r7)
            r6.setClickable(r7)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r8.addView(r6)
            androidx.core.widget.NestedScrollView r9 = new androidx.core.widget.NestedScrollView
            r9.<init>(r1)
            r9.addView(r8)
            r0.setCustomView(r9)
            android.widget.ImageView r10 = new android.widget.ImageView
            r10.<init>(r1)
            java.lang.String r11 = "listSelectorSDK21"
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r11)
            r10.setBackground(r11)
            java.lang.String r11 = "key_sheet_other"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.setColorFilter(r11)
            r11 = 2131165540(0x7var_, float:1.79453E38)
            r10.setImageResource(r11)
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1 r11 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1
            r11.<init>(r0)
            r10.setOnClickListener(r11)
            r11 = 1090519040(0x41000000, float:8.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.setPadding(r12, r12, r12, r12)
            r13 = 36
            r14 = 1108344832(0x42100000, float:36.0)
            r15 = 8388661(0x800035, float:1.1755018E-38)
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1086324736(0x40CLASSNAME, float:6.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r8.addView(r10, r13)
            org.telegram.ui.Components.BackupImageView r13 = new org.telegram.ui.Components.BackupImageView
            r13.<init>(r1)
            r14 = 1108082688(0x420CLASSNAME, float:35.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r13.setRoundRadius(r14)
            r15 = 70
            r16 = 70
            r17 = 49
            r18 = 0
            r19 = 29
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r6.addView(r13, r14)
            org.telegram.tgnet.TLRPC$Chat r14 = r2.chat
            if (r14 == 0) goto L_0x00bf
            org.telegram.ui.Components.AvatarDrawable r14 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r15 = r2.chat
            r14.<init>((org.telegram.tgnet.TLRPC.Chat) r15)
            org.telegram.tgnet.TLRPC$Chat r15 = r2.chat
            java.lang.String r15 = r15.title
            org.telegram.tgnet.TLRPC$Chat r3 = r2.chat
            int r3 = r3.participants_count
            org.telegram.tgnet.TLRPC$Chat r11 = r2.chat
            r13.setForUserOrChat(r11, r14, r2)
            r7 = r3
            r11 = r8
            goto L_0x00eb
        L_0x00bf:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            r14 = r3
            r11 = r8
            r7 = 0
            java.lang.String r15 = r2.title
            r3 = 0
            r14.setInfo(r7, r15, r3)
            java.lang.String r15 = r2.title
            int r3 = r2.participants_count
            org.telegram.tgnet.TLRPC$Photo r7 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.sizes
            r8 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            org.telegram.tgnet.TLRPC$Photo r8 = r2.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r7, (org.telegram.tgnet.TLRPC.Photo) r8)
            r19 = r3
            java.lang.String r3 = "50_50"
            r13.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r3, (android.graphics.drawable.Drawable) r14, (java.lang.Object) r2)
            r7 = r19
        L_0x00eb:
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r8 = r3
            java.lang.String r19 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r8.setTypeface(r3)
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 1
            r8.setTextSize(r4, r3)
            java.lang.String r18 = "dialogTextBlack"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r8.setTextColor(r3)
            r8.setText(r15)
            r8.setSingleLine(r4)
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r8.setEllipsize(r4)
            r20 = -2
            r21 = -2
            r22 = 49
            r23 = 10
            r24 = 9
            r25 = 10
            if (r7 <= 0) goto L_0x0125
            r26 = 0
            goto L_0x0127
        L_0x0125:
            r26 = 20
        L_0x0127:
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r6.addView(r8, r3)
            boolean r3 = r2.channel
            if (r3 == 0) goto L_0x0136
            boolean r3 = r2.megagroup
            if (r3 == 0) goto L_0x013e
        L_0x0136:
            org.telegram.tgnet.TLRPC$Chat r3 = r2.chat
            boolean r3 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r3)
            if (r3 == 0) goto L_0x0140
        L_0x013e:
            r3 = 1
            goto L_0x0141
        L_0x0140:
            r3 = 0
        L_0x0141:
            java.lang.String r4 = r2.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            r21 = 1
            r4 = r4 ^ 1
            java.lang.String r21 = "dialogTextGray3"
            if (r7 <= 0) goto L_0x019c
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r8 = r5
            r23 = r9
            r5 = 1096810496(0x41600000, float:14.0)
            r9 = 1
            r8.setTextSize(r9, r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r21)
            r8.setTextColor(r5)
            r8.setSingleLine(r9)
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r8.setEllipsize(r9)
            if (r3 == 0) goto L_0x0178
            java.lang.String r9 = "Subscribers"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r9, r7)
            r8.setText(r9)
            goto L_0x0181
        L_0x0178:
            java.lang.String r9 = "Members"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r9, r7)
            r8.setText(r9)
        L_0x0181:
            r24 = -2
            r25 = -2
            r26 = 49
            r27 = 10
            r28 = 3
            r29 = 10
            if (r4 == 0) goto L_0x0192
            r30 = 0
            goto L_0x0194
        L_0x0192:
            r30 = 20
        L_0x0194:
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r6.addView(r8, r9)
            goto L_0x019e
        L_0x019c:
            r23 = r9
        L_0x019e:
            r5 = 17
            if (r4 == 0) goto L_0x01d4
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r9.setGravity(r5)
            java.lang.String r5 = r2.about
            r9.setText(r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r9.setTextColor(r5)
            r18 = r4
            r4 = 1
            r5 = 1097859072(0x41700000, float:15.0)
            r9.setTextSize(r4, r5)
            r25 = -1
            r26 = -2
            r27 = 48
            r28 = 24
            r29 = 10
            r30 = 24
            r31 = 20
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30, (int) r31)
            r6.addView(r9, r5)
            goto L_0x01d6
        L_0x01d4:
            r18 = r4
        L_0x01d6:
            boolean r5 = r2.request_needed
            r4 = -1
            if (r5 == 0) goto L_0x02f6
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            android.content.Context r9 = r36.getContext()
            r5.<init>(r9)
            r9 = -2
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r9)
            r6.addView(r5, r4)
            org.telegram.ui.Components.RadialProgressView r4 = new org.telegram.ui.Components.RadialProgressView
            android.content.Context r9 = r36.getContext()
            r26 = r7
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r0.resourcesProvider
            r4.<init>(r9, r7)
            r0.requestProgressView = r4
            java.lang.String r7 = "featuredStickers_addButton"
            int r9 = r0.getThemedColor(r7)
            r4.setProgressColor(r9)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            r9 = 1107296256(0x42000000, float:32.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setSize(r9)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            r9 = 4
            r4.setVisibility(r9)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            r28 = r8
            r8 = 17
            r9 = 48
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r8)
            r5.addView(r4, r9)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r8 = r36.getContext()
            r4.<init>(r8)
            r0.requestTextView = r4
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r0.getThemedColor(r7)
            java.lang.String r9 = "featuredStickers_addButtonPressed"
            int r9 = r0.getThemedColor(r9)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r8, r7, r9)
            r4.setBackground(r7)
            android.widget.TextView r4 = r0.requestTextView
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r7)
            android.widget.TextView r4 = r0.requestTextView
            r7 = 17
            r4.setGravity(r7)
            android.widget.TextView r4 = r0.requestTextView
            r7 = 1
            r4.setSingleLine(r7)
            android.widget.TextView r7 = r0.requestTextView
            if (r3 == 0) goto L_0x0264
            r8 = 2131627773(0x7f0e0efd, float:1.888282E38)
            java.lang.String r9 = "RequestToJoinChannel"
            goto L_0x0269
        L_0x0264:
            r8 = 2131627777(0x7f0e0var_, float:1.8882828E38)
            java.lang.String r9 = "RequestToJoinGroup"
        L_0x0269:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7.setText(r8)
            android.widget.TextView r7 = r0.requestTextView
            java.lang.String r8 = "featuredStickers_buttonText"
            int r8 = r0.getThemedColor(r8)
            r7.setTextColor(r8)
            android.widget.TextView r7 = r0.requestTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r8 = 1
            r7.setTextSize(r8, r4)
            android.widget.TextView r7 = r0.requestTextView
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r7.setTypeface(r8)
            android.widget.TextView r7 = r0.requestTextView
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
            r8.<init>(r0, r1, r3)
            r7.setOnClickListener(r8)
            android.widget.TextView r7 = r0.requestTextView
            r29 = -1
            r30 = 48
            r31 = 8388611(0x800003, float:1.1754948E-38)
            r32 = 16
            r33 = 0
            r34 = 16
            r35 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34, (int) r35)
            r5.addView(r7, r8)
            android.widget.TextView r7 = new android.widget.TextView
            android.content.Context r8 = r36.getContext()
            r7.<init>(r8)
            r8 = 17
            r7.setGravity(r8)
            r4 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r7.setTextSize(r8, r4)
            if (r3 == 0) goto L_0x02ca
            r4 = 2131627775(0x7f0e0eff, float:1.8882824E38)
            java.lang.String r8 = "RequestToJoinChannelDescription"
            goto L_0x02cf
        L_0x02ca:
            r4 = 2131627779(0x7f0e0var_, float:1.8882832E38)
            java.lang.String r8 = "RequestToJoinGroupDescription"
        L_0x02cf:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r7.setText(r4)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r21)
            r7.setTextColor(r4)
            r29 = -1
            r30 = -2
            r31 = 48
            r32 = 24
            r33 = 17
            r34 = 24
            r35 = 15
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34, (int) r35)
            r6.addView(r7, r4)
            r17 = r3
            goto L_0x0419
        L_0x02f6:
            r26 = r7
            r28 = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r2.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x034f
            org.telegram.ui.Components.RecyclerListView r5 = new org.telegram.ui.Components.RecyclerListView
            r5.<init>(r1)
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 0
            r5.setPadding(r8, r8, r8, r7)
            r5.setNestedScrollingEnabled(r8)
            r5.setClipToPadding(r8)
            androidx.recyclerview.widget.LinearLayoutManager r7 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r36.getContext()
            r7.<init>(r9, r8, r8)
            r5.setLayoutManager(r7)
            r5.setHorizontalScrollBarEnabled(r8)
            r5.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r7 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r7.<init>(r1)
            r5.setAdapter(r7)
            java.lang.String r7 = "dialogScrollGlow"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setGlowColor(r7)
            r29 = -2
            r30 = 90
            r31 = 49
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 7
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r29, (int) r30, (int) r31, (int) r32, (int) r33, (int) r34, (int) r35)
            r6.addView(r5, r7)
        L_0x034f:
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            java.lang.String r7 = "dialogShadowLine"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            android.widget.LinearLayout$LayoutParams r7 = new android.widget.LinearLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r7.<init>(r4, r8)
            r6.addView(r5, r7)
            org.telegram.ui.Components.PickerBottomLayout r7 = new org.telegram.ui.Components.PickerBottomLayout
            r8 = 0
            r7.<init>(r1, r8)
            r9 = 83
            r8 = 48
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r8, (int) r9)
            r6.addView(r7, r4)
            android.widget.TextView r4 = r7.cancelButton
            r8 = 1099956224(0x41900000, float:18.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r8 = 0
            r4.setPadding(r9, r8, r1, r8)
            android.widget.TextView r1 = r7.cancelButton
            java.lang.String r4 = "dialogTextBlue2"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r8)
            android.widget.TextView r1 = r7.cancelButton
            r8 = 2131624753(0x7f0e0331, float:1.8876695E38)
            java.lang.String r9 = "Cancel"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            java.lang.String r8 = r8.toUpperCase()
            r1.setText(r8)
            android.widget.TextView r1 = r7.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            r8.<init>(r0)
            r1.setOnClickListener(r8)
            android.widget.LinearLayout r1 = r7.doneButton
            r8 = 1099956224(0x41900000, float:18.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r17 = r3
            r3 = 0
            r1.setPadding(r9, r3, r8, r3)
            android.widget.LinearLayout r1 = r7.doneButton
            r1.setVisibility(r3)
            android.widget.TextView r1 = r7.doneButtonBadgeTextView
            r3 = 8
            r1.setVisibility(r3)
            android.widget.TextView r1 = r7.doneButtonTextView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r3)
            boolean r1 = r2.channel
            if (r1 == 0) goto L_0x03e0
            boolean r1 = r2.megagroup
            if (r1 == 0) goto L_0x03ee
        L_0x03e0:
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0401
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0401
        L_0x03ee:
            android.widget.TextView r1 = r7.doneButtonTextView
            r3 = 2131627548(0x7f0e0e1c, float:1.8882364E38)
            java.lang.String r4 = "ProfileJoinChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r1.setText(r3)
            goto L_0x040f
        L_0x0401:
            android.widget.TextView r1 = r7.doneButtonTextView
            r3 = 2131626200(0x7f0e08d8, float:1.887963E38)
            java.lang.String r4 = "JoinGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
        L_0x040f:
            android.widget.LinearLayout r1 = r7.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3 r3 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
            r3.<init>(r0)
            r1.setOnClickListener(r3)
        L_0x0419:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$ChatInvite, java.lang.String, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4108lambda$new$0$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4113lambda$new$5$orgtelegramuiComponentsJoinGroupAlert(Context context, boolean isChannel, View view) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda5(this), 400);
        TLRPC.TL_messages_importChatInvite request = new TLRPC.TL_messages_importChatInvite();
        request.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new JoinGroupAlert$$ExternalSyntheticLambda8(this, context, isChannel, request), 2);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4109lambda$new$1$orgtelegramuiComponentsJoinGroupAlert() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4112lambda$new$4$orgtelegramuiComponentsJoinGroupAlert(Context context, boolean isChannel, TLRPC.TL_messages_importChatInvite request, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda6(this, error, context, isChannel, request));
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4111lambda$new$3$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, Context context, boolean isChannel, TLRPC.TL_messages_importChatInvite request) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (error != null) {
                if ("INVITE_REQUEST_SENT".equals(error.text)) {
                    setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda0(this, context, isChannel));
                } else {
                    AlertsCreator.processError(this.currentAccount, error, this.fragment, request, new Object[0]);
                }
            }
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4110lambda$new$2$orgtelegramuiComponentsJoinGroupAlert(Context context, boolean isChannel, DialogInterface dialog) {
        String subTitle;
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(context, this.fragment.getResourceProvider());
        layout.imageView.setAnimation(NUM, 28, 28);
        layout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", NUM));
        if (isChannel) {
            subTitle = LocaleController.getString("RequestToJoinChannelSentDescription", NUM);
        } else {
            subTitle = LocaleController.getString("RequestToJoinGroupSentDescription", NUM);
        }
        layout.subtitleTextView.setText(subTitle);
        Bulletin.make(this.fragment, (Bulletin.Layout) layout, 2750).show();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4114lambda$new$6$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4117lambda$new$9$orgtelegramuiComponentsJoinGroupAlert(View v) {
        dismiss();
        TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
        req.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new JoinGroupAlert$$ExternalSyntheticLambda9(this, req), 2);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4116lambda$new$8$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_messages_importChatInvite req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda7(this, error, response, req));
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m4115lambda$new$7$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_importChatInvite req) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (error == null) {
                TLRPC.Updates updates = (TLRPC.Updates) response;
                if (!updates.chats.isEmpty()) {
                    TLRPC.Chat chat = updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putLong("chat_id", chat.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this.fragment)) {
                        ChatActivity chatActivity = new ChatActivity(args);
                        BaseFragment baseFragment2 = this.fragment;
                        baseFragment2.presentFragment(chatActivity, baseFragment2 instanceof ChatActivity);
                        return;
                    }
                    return;
                }
                return;
            }
            AlertsCreator.processError(this.currentAccount, error, this.fragment, req, new Object[0]);
        }
    }

    private class UsersAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public UsersAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int participants_count;
            int count = JoinGroupAlert.this.chatInvite.participants.size();
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                participants_count = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                participants_count = JoinGroupAlert.this.chatInvite.participants_count;
            }
            if (count != participants_count) {
                return count + 1;
            }
            return count;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new JoinSheetUserCell(this.context);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int participants_count;
            JoinSheetUserCell cell = (JoinSheetUserCell) holder.itemView;
            if (position < JoinGroupAlert.this.chatInvite.participants.size()) {
                cell.setUser(JoinGroupAlert.this.chatInvite.participants.get(position));
                return;
            }
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                participants_count = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                participants_count = JoinGroupAlert.this.chatInvite.participants_count;
            }
            cell.setCount(participants_count - JoinGroupAlert.this.chatInvite.participants.size());
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }
}
