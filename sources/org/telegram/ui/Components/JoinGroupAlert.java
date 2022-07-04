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
import org.telegram.messenger.UserConfig;
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
    private TLRPC.Chat currentChat;
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r35, org.telegram.tgnet.TLObject r36, java.lang.String r37, org.telegram.ui.ActionBar.BaseFragment r38, org.telegram.ui.ActionBar.Theme.ResourcesProvider r39) {
        /*
            r34 = this;
            r0 = r34
            r1 = r35
            r2 = r36
            r3 = r39
            r4 = 0
            r0.<init>(r1, r4, r3)
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = r0.getThemedColor(r5)
            r0.fixNavigationBar(r5)
            r5 = r38
            r0.fragment = r5
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC.ChatInvite
            if (r6 == 0) goto L_0x0029
            r6 = r2
            org.telegram.tgnet.TLRPC$ChatInvite r6 = (org.telegram.tgnet.TLRPC.ChatInvite) r6
            r0.chatInvite = r6
            goto L_0x0032
        L_0x0029:
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r6 == 0) goto L_0x0032
            r6 = r2
            org.telegram.tgnet.TLRPC$Chat r6 = (org.telegram.tgnet.TLRPC.Chat) r6
            r0.currentChat = r6
        L_0x0032:
            r6 = r37
            r0.hash = r6
            android.widget.LinearLayout r7 = new android.widget.LinearLayout
            r7.<init>(r1)
            r8 = 1
            r7.setOrientation(r8)
            r7.setClickable(r8)
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            r9.addView(r7)
            androidx.core.widget.NestedScrollView r10 = new androidx.core.widget.NestedScrollView
            r10.<init>(r1)
            r10.addView(r9)
            r0.setCustomView(r10)
            android.widget.ImageView r11 = new android.widget.ImageView
            r11.<init>(r1)
            java.lang.String r12 = "listSelectorSDK21"
            int r12 = r0.getThemedColor(r12)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12)
            r11.setBackground(r12)
            java.lang.String r12 = "key_sheet_other"
            int r12 = r0.getThemedColor(r12)
            r11.setColorFilter(r12)
            r12 = 2131165480(0x7var_, float:1.7945178E38)
            r11.setImageResource(r12)
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5 r12 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5
            r12.<init>(r0)
            r11.setOnClickListener(r12)
            r12 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.setPadding(r13, r13, r13, r13)
            r14 = 36
            r15 = 1108344832(0x42100000, float:36.0)
            r16 = 8388661(0x800035, float:1.1755018E-38)
            r17 = 1086324736(0x40CLASSNAME, float:6.0)
            r18 = 1090519040(0x41000000, float:8.0)
            r19 = 1086324736(0x40CLASSNAME, float:6.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r9.addView(r11, r14)
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            org.telegram.ui.Components.BackupImageView r12 = new org.telegram.ui.Components.BackupImageView
            r12.<init>(r1)
            r19 = 1108082688(0x420CLASSNAME, float:35.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r12.setRoundRadius(r4)
            r21 = 70
            r22 = 70
            r23 = 49
            r24 = 0
            r25 = 29
            r26 = 0
            r27 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
            r7.addView(r12, r4)
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r0.chatInvite
            r8 = 0
            if (r4 == 0) goto L_0x0133
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            if (r4 == 0) goto L_0x00f3
            org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r8 = r8.chat
            r4.<init>((org.telegram.tgnet.TLRPC.Chat) r8)
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r8 = r8.chat
            java.lang.String r8 = r8.title
            org.telegram.tgnet.TLRPC$ChatInvite r14 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r14 = r14.chat
            int r14 = r14.participants_count
            org.telegram.tgnet.TLRPC$ChatInvite r2 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r2 = r2.chat
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r0.chatInvite
            r12.setForUserOrChat(r2, r4, r5)
            r16 = r4
            r17 = r14
            r14 = r8
            goto L_0x012c
        L_0x00f3:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r4 = 0
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            java.lang.String r6 = r6.title
            r2.setInfo(r4, r6, r8)
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r0.chatInvite
            java.lang.String r4 = r4.title
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r0.chatInvite
            int r5 = r5.participants_count
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.sizes
            r8 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r8)
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r6, (org.telegram.tgnet.TLRPC.Photo) r8)
            org.telegram.tgnet.TLRPC$ChatInvite r14 = r0.chatInvite
            r16 = r4
            java.lang.String r4 = "50_50"
            r12.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r4, (android.graphics.drawable.Drawable) r2, (java.lang.Object) r14)
            r17 = r5
            r14 = r16
            r16 = r2
        L_0x012c:
            org.telegram.tgnet.TLRPC$ChatInvite r2 = r0.chatInvite
            java.lang.String r15 = r2.about
            r2 = r17
            goto L_0x016f
        L_0x0133:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            if (r2 == 0) goto L_0x016d
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            r2.<init>((org.telegram.tgnet.TLRPC.Chat) r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            java.lang.String r14 = r4.title
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            long r5 = r5.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r4.getChatFull(r5)
            if (r4 == 0) goto L_0x0154
            java.lang.String r8 = r4.about
        L_0x0154:
            r15 = r8
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            int r5 = r5.participants_count
            if (r4 == 0) goto L_0x015e
            int r6 = r4.participants_count
            goto L_0x015f
        L_0x015e:
            r6 = 0
        L_0x015f:
            int r17 = java.lang.Math.max(r5, r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            r12.setForUserOrChat(r5, r2, r5)
            r16 = r2
            r2 = r17
            goto L_0x016f
        L_0x016d:
            r2 = r17
        L_0x016f:
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r4.setTypeface(r6)
            r6 = 1099431936(0x41880000, float:17.0)
            r8 = 1
            r4.setTextSize(r8, r6)
            java.lang.String r6 = "dialogTextBlack"
            int r8 = r0.getThemedColor(r6)
            r4.setTextColor(r8)
            r4.setText(r14)
            r8 = 1
            r4.setSingleLine(r8)
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r8)
            r21 = -2
            r22 = -2
            r23 = 49
            r24 = 10
            r25 = 9
            r26 = 10
            if (r2 <= 0) goto L_0x01a9
            r27 = 0
            goto L_0x01ab
        L_0x01a9:
            r27 = 20
        L_0x01ab:
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26, (int) r27)
            r7.addView(r4, r8)
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            if (r8 == 0) goto L_0x01ca
            boolean r8 = r8.channel
            if (r8 == 0) goto L_0x01c0
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            boolean r8 = r8.megagroup
            if (r8 == 0) goto L_0x01d8
        L_0x01c0:
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r8 = r8.chat
            boolean r8 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r8)
            if (r8 != 0) goto L_0x01d8
        L_0x01ca:
            org.telegram.tgnet.TLRPC$Chat r8 = r0.currentChat
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x01da
            org.telegram.tgnet.TLRPC$Chat r8 = r0.currentChat
            boolean r8 = r8.megagroup
            if (r8 != 0) goto L_0x01da
        L_0x01d8:
            r8 = 1
            goto L_0x01db
        L_0x01da:
            r8 = 0
        L_0x01db:
            boolean r21 = android.text.TextUtils.isEmpty(r15)
            r19 = 1
            r21 = r21 ^ 1
            r22 = r4
            java.lang.String r4 = "dialogTextGray3"
            r23 = r9
            if (r2 <= 0) goto L_0x0241
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r25 = r10
            r26 = r11
            r10 = 1096810496(0x41600000, float:14.0)
            r11 = 1
            r9.setTextSize(r11, r10)
            int r10 = r0.getThemedColor(r4)
            r9.setTextColor(r10)
            r9.setSingleLine(r11)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r10)
            if (r8 == 0) goto L_0x0218
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r10 = "Subscribers"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2, r11)
            r9.setText(r10)
            goto L_0x0224
        L_0x0218:
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r10 = "Members"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r2, r11)
            r9.setText(r10)
        L_0x0224:
            r27 = -2
            r28 = -2
            r29 = 49
            r30 = 10
            r31 = 3
            r32 = 10
            if (r21 == 0) goto L_0x0235
            r33 = 0
            goto L_0x0237
        L_0x0235:
            r33 = 20
        L_0x0237:
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r7.addView(r9, r10)
            r22 = r9
            goto L_0x0245
        L_0x0241:
            r25 = r10
            r26 = r11
        L_0x0245:
            r9 = 1097859072(0x41700000, float:15.0)
            r10 = 17
            if (r21 == 0) goto L_0x0276
            android.widget.TextView r11 = new android.widget.TextView
            r11.<init>(r1)
            r11.setGravity(r10)
            r11.setText(r15)
            int r6 = r0.getThemedColor(r6)
            r11.setTextColor(r6)
            r6 = 1
            r11.setTextSize(r6, r9)
            r27 = -1
            r28 = -2
            r29 = 48
            r30 = 24
            r31 = 10
            r32 = 24
            r33 = 20
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r7.addView(r11, r6)
        L_0x0276:
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            r11 = 48
            r9 = -1
            if (r6 == 0) goto L_0x03ab
            boolean r6 = r6.request_needed
            if (r6 == 0) goto L_0x0283
            goto L_0x03ab
        L_0x0283:
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r0.chatInvite
            if (r4 == 0) goto L_0x03a9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r4.participants
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x02dc
            org.telegram.ui.Components.RecyclerListView r4 = new org.telegram.ui.Components.RecyclerListView
            r4.<init>(r1)
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            r4.setPadding(r6, r6, r6, r5)
            r4.setNestedScrollingEnabled(r6)
            r4.setClipToPadding(r6)
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r10 = r34.getContext()
            r5.<init>(r10, r6, r6)
            r4.setLayoutManager(r5)
            r4.setHorizontalScrollBarEnabled(r6)
            r4.setVerticalScrollBarEnabled(r6)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r5 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r5.<init>(r1)
            r4.setAdapter(r5)
            java.lang.String r5 = "dialogScrollGlow"
            int r5 = r0.getThemedColor(r5)
            r4.setGlowColor(r5)
            r27 = -2
            r28 = 90
            r29 = 49
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 7
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r7.addView(r4, r5)
        L_0x02dc:
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            java.lang.String r5 = "dialogShadowLine"
            int r5 = r0.getThemedColor(r5)
            r4.setBackgroundColor(r5)
            android.widget.LinearLayout$LayoutParams r5 = new android.widget.LinearLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r5.<init>(r9, r6)
            r7.addView(r4, r5)
            org.telegram.ui.Components.PickerBottomLayout r5 = new org.telegram.ui.Components.PickerBottomLayout
            r6 = 0
            r5.<init>(r1, r6, r3)
            r10 = 83
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r11, (int) r10)
            r7.addView(r5, r9)
            android.widget.TextView r9 = r5.cancelButton
            r10 = 1099956224(0x41900000, float:18.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r9.setPadding(r11, r6, r1, r6)
            android.widget.TextView r1 = r5.cancelButton
            java.lang.String r6 = "dialogTextBlue2"
            int r9 = r0.getThemedColor(r6)
            r1.setTextColor(r9)
            android.widget.TextView r1 = r5.cancelButton
            r9 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r11 = "Cancel"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            java.lang.String r9 = r9.toUpperCase()
            r1.setText(r9)
            android.widget.TextView r1 = r5.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda7 r9 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda7
            r9.<init>(r0)
            r1.setOnClickListener(r9)
            android.widget.LinearLayout r1 = r5.doneButton
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r11 = 0
            r1.setPadding(r9, r11, r10, r11)
            android.widget.LinearLayout r1 = r5.doneButton
            r1.setVisibility(r11)
            android.widget.TextView r1 = r5.doneButtonBadgeTextView
            r9 = 8
            r1.setVisibility(r9)
            android.widget.TextView r1 = r5.doneButtonTextView
            int r6 = r0.getThemedColor(r6)
            r1.setTextColor(r6)
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            boolean r1 = r1.channel
            if (r1 == 0) goto L_0x036a
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            boolean r1 = r1.megagroup
            if (r1 == 0) goto L_0x037c
        L_0x036a:
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x038f
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x038f
        L_0x037c:
            android.widget.TextView r1 = r5.doneButtonTextView
            r6 = 2131627739(0x7f0e0edb, float:1.888275E38)
            java.lang.String r9 = "ProfileJoinChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            java.lang.String r6 = r6.toUpperCase()
            r1.setText(r6)
            goto L_0x039d
        L_0x038f:
            android.widget.TextView r1 = r5.doneButtonTextView
            r6 = 2131626317(0x7f0e094d, float:1.8879867E38)
            java.lang.String r9 = "JoinGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r1.setText(r6)
        L_0x039d:
            android.widget.LinearLayout r1 = r5.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda6
            r6.<init>(r0)
            r1.setOnClickListener(r6)
            goto L_0x04bc
        L_0x03a9:
            goto L_0x04bc
        L_0x03ab:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            android.content.Context r6 = r34.getContext()
            r1.<init>(r6)
            r6 = -2
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r6)
            r7.addView(r1, r6)
            org.telegram.ui.Components.RadialProgressView r6 = new org.telegram.ui.Components.RadialProgressView
            android.content.Context r9 = r34.getContext()
            r6.<init>(r9, r3)
            r0.requestProgressView = r6
            java.lang.String r9 = "featuredStickers_addButton"
            int r10 = r0.getThemedColor(r9)
            r6.setProgressColor(r10)
            org.telegram.ui.Components.RadialProgressView r6 = r0.requestProgressView
            r10 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.setSize(r10)
            org.telegram.ui.Components.RadialProgressView r6 = r0.requestProgressView
            r10 = 4
            r6.setVisibility(r10)
            org.telegram.ui.Components.RadialProgressView r6 = r0.requestProgressView
            r10 = 17
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r10)
            r1.addView(r6, r11)
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r10 = r34.getContext()
            r6.<init>(r10)
            r0.requestTextView = r6
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r0.getThemedColor(r9)
            java.lang.String r11 = "featuredStickers_addButtonPressed"
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r10, r9, r11)
            r6.setBackground(r9)
            android.widget.TextView r6 = r0.requestTextView
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r9)
            android.widget.TextView r6 = r0.requestTextView
            r9 = 17
            r6.setGravity(r9)
            android.widget.TextView r6 = r0.requestTextView
            r9 = 1
            r6.setSingleLine(r9)
            android.widget.TextView r6 = r0.requestTextView
            if (r8 == 0) goto L_0x042c
            r9 = 2131627968(0x7f0e0fc0, float:1.8883215E38)
            java.lang.String r10 = "RequestToJoinChannel"
            goto L_0x0431
        L_0x042c:
            r9 = 2131627972(0x7f0e0fc4, float:1.8883224E38)
            java.lang.String r10 = "RequestToJoinGroup"
        L_0x0431:
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r6.setText(r9)
            android.widget.TextView r6 = r0.requestTextView
            java.lang.String r9 = "featuredStickers_buttonText"
            int r9 = r0.getThemedColor(r9)
            r6.setTextColor(r9)
            android.widget.TextView r6 = r0.requestTextView
            r9 = 1097859072(0x41700000, float:15.0)
            r10 = 1
            r6.setTextSize(r10, r9)
            android.widget.TextView r6 = r0.requestTextView
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r6.setTypeface(r5)
            android.widget.TextView r5 = r0.requestTextView
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda8 r6 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda8
            r6.<init>(r0, r8)
            r5.setOnClickListener(r6)
            android.widget.TextView r5 = r0.requestTextView
            r27 = -1
            r28 = 48
            r29 = 8388611(0x800003, float:1.1754948E-38)
            r30 = 16
            r31 = 0
            r32 = 16
            r33 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r1.addView(r5, r6)
            android.widget.TextView r5 = new android.widget.TextView
            android.content.Context r6 = r34.getContext()
            r5.<init>(r6)
            r6 = 17
            r5.setGravity(r6)
            r6 = 1096810496(0x41600000, float:14.0)
            r9 = 1
            r5.setTextSize(r9, r6)
            if (r8 == 0) goto L_0x0492
            r6 = 2131627970(0x7f0e0fc2, float:1.888322E38)
            java.lang.String r9 = "RequestToJoinChannelDescription"
            goto L_0x0497
        L_0x0492:
            r6 = 2131627974(0x7f0e0fc6, float:1.8883228E38)
            java.lang.String r9 = "RequestToJoinGroupDescription"
        L_0x0497:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r5.setText(r6)
            int r4 = r0.getThemedColor(r4)
            r5.setTextColor(r4)
            r27 = -1
            r28 = -2
            r29 = 48
            r30 = 24
            r31 = 17
            r32 = 24
            r33 = 15
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r27, (int) r28, (int) r29, (int) r30, (int) r31, (int) r32, (int) r33)
            r7.addView(r5, r4)
            goto L_0x03a9
        L_0x04bc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLObject, java.lang.String, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1076lambda$new$0$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1085lambda$new$7$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, View view) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda10(this), 400);
        if (this.chatInvite != null || this.currentChat == null) {
            TLRPC.TL_messages_importChatInvite request = new TLRPC.TL_messages_importChatInvite();
            request.hash = this.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new JoinGroupAlert$$ExternalSyntheticLambda3(this, isChannel, request), 2);
            return;
        }
        MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, (String) null, (BaseFragment) null, true, new JoinGroupAlert$$ExternalSyntheticLambda9(this), new JoinGroupAlert$$ExternalSyntheticLambda1(this, isChannel));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1077lambda$new$1$orgtelegramuiComponentsJoinGroupAlert() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ boolean m1081lambda$new$3$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, TLRPC.TL_error err) {
        if (err != null && "INVITE_REQUEST_SENT".equals(err.text)) {
            setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda0(this, isChannel));
        }
        dismiss();
        return false;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1080lambda$new$2$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, DialogInterface di) {
        showBulletin(getContext(), this.fragment, isChannel);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1084lambda$new$6$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, TLRPC.TL_messages_importChatInvite request, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda12(this, error, isChannel, request));
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1083lambda$new$5$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, boolean isChannel, TLRPC.TL_messages_importChatInvite request) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (error != null) {
                if ("INVITE_REQUEST_SENT".equals(error.text)) {
                    setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda4(this, isChannel));
                } else {
                    AlertsCreator.processError(this.currentAccount, error, this.fragment, request, new Object[0]);
                }
            }
            dismiss();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1082lambda$new$4$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, DialogInterface di) {
        showBulletin(getContext(), this.fragment, isChannel);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1086lambda$new$8$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1079lambda$new$11$orgtelegramuiComponentsJoinGroupAlert(View v) {
        dismiss();
        TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
        req.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new JoinGroupAlert$$ExternalSyntheticLambda2(this, req), 2);
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1078lambda$new$10$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_messages_importChatInvite req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda11(this, error, response, req));
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-JoinGroupAlert  reason: not valid java name */
    public /* synthetic */ void m1087lambda$new$9$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_importChatInvite req) {
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

    public static void showBulletin(Context context, BaseFragment fragment2, boolean isChannel) {
        String subTitle;
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(context, fragment2.getResourceProvider());
        layout.imageView.setAnimation(NUM, 28, 28);
        layout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", NUM));
        if (isChannel) {
            subTitle = LocaleController.getString("RequestToJoinChannelSentDescription", NUM);
        } else {
            subTitle = LocaleController.getString("RequestToJoinGroupSentDescription", NUM);
        }
        layout.subtitleTextView.setText(subTitle);
        Bulletin.make(fragment2, (Bulletin.Layout) layout, 2750).show();
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
